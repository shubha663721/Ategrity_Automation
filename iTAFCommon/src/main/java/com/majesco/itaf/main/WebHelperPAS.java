package com.majesco.itaf.main;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;	
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sikuli.script.App;
import org.sikuli.script.Pattern;
import org.sikuli.script.Screen;

import atu.testrecorder.ATUTestRecorder;

import com.google.common.io.Files;
import com.majesco.itaf.util.CalendarSnippet;
import com.majesco.itaf.util.CommonExpectedConditions;
import com.majesco.itaf.util.Constants;
import com.majesco.itaf.util.DMProduct;
import com.majesco.itaf.util.ExcelUtility;
import com.majesco.itaf.util.JDBCConnection;
import com.majesco.itaf.util.Jacob;
import com.majesco.itaf.util.PDFComparisonUtil;
import com.majesco.itaf.util.WaitTool;
import com.majesco.itaf.verification.WebVerification;
import com.majesco.itaf.vo.Reporter;
import com.majesco.itaf.webservice.WebService;

public class WebHelperPAS {

	final static Logger log = Logger.getLogger(WebHelperPAS.class.getName());
	public static ATUTestRecorder recorder = null;
	public static Cell transactionType = null;
	public static int TotalpassCount;
	public static int TotalfailCount;
	public static int DResult = 1;
	boolean stillChanging = true;

	public static Date toDate = null;
	public static String testCase = null;
	public static Screen sikuliScreen = null;
	public static List<String> searchValue1 = null;
	public static Boolean pageLoaded = false;

	// Minaakshi : Variable added for DM-Product
	public static String sqlQuery = "";
	public static String readFromColName = "";
	public static String toBeFetchedDBColName = "";
	public static String expectedDBStatus = "";
	public static String writeToColName = "";

	public static Wait<WebDriver> waitForElementPresence; // Minaakshi : 05-02-2019
	public static Wait<WebDriver> dmgebtWait; // Minaakshi : 05-02-2019	
	static ITAFWebDriver webDriver = ITAFWebDriver.getInstance();
	static MainController controller = ObjectFactory.getMainController();

	static {
		WebHelper.wait = new WebDriverWait(Automation.driver, Integer.parseInt(Config.timeOut));}
		static void implementWait() {
			// Minaakshi : 05-02-2019 							  
		
		dmgebtWait= new FluentWait<WebDriver>(Automation.driver).withTimeout(Integer.parseInt(Config.timeOut), TimeUnit.SECONDS)
				.pollingEvery(3, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		
		waitForElementPresence = new FluentWait<WebDriver>(Automation.driver).withTimeout(30, TimeUnit.SECONDS)
				.pollingEvery(3, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
	}
	
	// bhaskar
	public static void GetCellInfo(String FilePath, HSSFRow rowValues, int valuesRowIndex, int valuesRowCount) throws IOException
	// newly Added two Variables for Action Loop
	{
		try {
			
			WebHelper.frmDate = new Date();
			WebHelper.isDynamicNumFound = true;
			List<WebElement> controlList = null;
			// String testCase = null;
			String ctrlValue = null;
			// HSSFRow structureRow=null;
			InputStream myXls = new FileInputStream(FilePath);
			HSSFWorkbook workBook = new HSSFWorkbook(myXls);
			WebHelper.format = workBook.createDataFormat();
			HSSFSheet sheetStructure = workBook.getSheet("Structure");
			// HSSFCell controlValue=null;
			int rowCount = sheetStructure.getLastRowNum() + 1;
			HSSFSheet headerValues = ExcelUtility.getXLSSheet(FilePath, "Values");
			// HSSFRow headerRow = headerValues.getRow(0);
			// System.out.println(Automation.dtFormat.format(frmDate));
			String fromDate = Config.dtFormat.format(WebHelper.frmDate);
			webDriver.getReport().setFromDate(fromDate);
			WebHelper.structureHeader = WebHelperUtil.getValueFromHashMap(sheetStructure);
			WebHelper.columnName = null;
			int dynamicIndexNumber;// Added for Action Loop
			String imageType, indexVal, controlName, executeFlag, action, logicalName, controltype, controlID, dynamicIndex, newDynamicIndex, rowNo, colNo, columnName1, CompareText;// newly
			// Added for Action Loop

			// Setting of default reporting values before executing a
			// transaction
			webDriver.getReport().setMessage("");
			webDriver.getReport().setStatus("PASS");
			for (int rowIndex = 1; rowIndex < rowCount && !controller.pauseExecution; rowIndex++) {
				// structureRow = sheetStructure.getRow(rowIndex);
				controlName = WebHelperUtil.getCellData("ControlName", sheetStructure, rowIndex, WebHelper.structureHeader);// structureRow.getCell(3);
				executeFlag = WebHelperUtil.getCellData("ExecuteFlag", sheetStructure, rowIndex, WebHelper.structureHeader);// structureRow.getCell(0);

				if (executeFlag.toString().equals("Y")) {
					WebElement webElement = null;
					imageType = WebHelperUtil.getCellData("ImageType", sheetStructure, rowIndex, WebHelper.structureHeader);

					action = WebHelperUtil.getCellData("Action", sheetStructure, rowIndex, WebHelper.structureHeader);// structureRow.getCell(1);
					logicalName = WebHelperUtil.getCellData("LogicalName", sheetStructure, rowIndex, WebHelper.structureHeader);// structureRow.getCell(2);

					controltype = WebHelperUtil.getCellData("ControlType", sheetStructure, rowIndex, WebHelper.structureHeader);// structureRow.getCell(4);
					controlID = WebHelperUtil.getCellData("ControlID", sheetStructure, rowIndex, WebHelper.structureHeader);// structureRow.getCell(6);
					indexVal = WebHelperUtil.getCellData("Index", sheetStructure, rowIndex, WebHelper.structureHeader);// structureRow.getCell(7);
					WebHelper.columnName = WebHelperUtil.getCellData("ColumnName", sheetStructure, rowIndex, WebHelper.structureHeader);

					// columnName1 = getCellData("CompareText", sheetStructure,
					// rowIndex, structureHeader);
					rowNo = WebHelperUtil.getCellData("RowNo", sheetStructure, rowIndex, WebHelper.structureHeader);
					colNo = WebHelperUtil.getCellData("ColumnNo", sheetStructure, rowIndex, WebHelper.structureHeader);
					dynamicIndex = WebHelperUtil.getCellData("DynamicIndex", sheetStructure, rowIndex, WebHelper.structureHeader);
					// Added code for loop
					System.out.println("iTAF:" + logicalName + " " + rowIndex);
					log.info("iTAF:" + logicalName + " " + rowIndex);

					/*
					 * Below code has been written To Handle condition for
					 * multiple rows in excel sheet
					 */

					if (imageType.equalsIgnoreCase("START_SCRIPT")) {

						try {
							webElement = getElementByType(controlID, controlName, WebHelper.control, imageType, ctrlValue);
							if (webElement.isDisplayed()) {
								System.out.println("Proceed");
							}
						} catch (Exception e) {
							log.error(e.getMessage(), e);
							for (int searchrow = rowIndex; searchrow < rowCount; searchrow++) {
								imageType = WebHelperUtil.getCellData("ImageType", sheetStructure, rowIndex, WebHelper.structureHeader);
								rowIndex++;
								if (imageType.equalsIgnoreCase("END_SCRIPT")) {

									break;
								}
							}
						}
					}

					if (action.equalsIgnoreCase("LOOP")) {
						WebHelper.loopRow = rowIndex + 1;
					}

					// if rownum != 1 , then do below steps
					if ((valuesRowIndex != ExcelUtility.firstRow) && (dynamicIndex.length() > 0)) // valuesRowIndex
					{

						dynamicIndexNumber = Integer.parseInt(dynamicIndex.substring(dynamicIndex.length() - 1, dynamicIndex.length()));

						if (ExcelUtility.dynamicNum == 0) {
							ExcelUtility.dynamicNum = dynamicIndexNumber + 1;
							WebHelper.isDynamicNumFound = false;

						} else if (ExcelUtility.dynamicNum != 0 && WebHelper.isDynamicNumFound) {
							ExcelUtility.dynamicNum = ExcelUtility.dynamicNum + 1;
							WebHelper.isDynamicNumFound = false;
						}

						newDynamicIndex = dynamicIndex.replace(String.valueOf(dynamicIndexNumber), String.valueOf(ExcelUtility.dynamicNum));
						controlName = controlName.replace(dynamicIndex, newDynamicIndex);
					}

					/**
					 * Stop the execution of the current test case unexpected
					 * alert
					 **/

					if (!action.equalsIgnoreCase("LOOP") && !action.equalsIgnoreCase("END_LOOP") && !action.equalsIgnoreCase("TableInput_End")) {
						// boolean isControlValueFound =false;
						if (WebHelper.valuesHeader.isEmpty() == true) {
							WebHelper.valuesHeader = WebHelperUtil.getValueFromHashMap(headerValues);
						}
						Object actualValue = null;
						if (logicalName != null) {
							actualValue = WebHelper.valuesHeader.get(logicalName.toString());
						}// headerRow.getCell(colIndex);
						if (actualValue == null) {
							log.info("actualValue is Null");
						} else {
							// int colIndex =
							// Integer.parseInt(actualValue.toString());
							// controlValue = rowValues.getCell(colIndex);

							ctrlValue = WebHelperUtil.getCellData(logicalName, headerValues, valuesRowIndex, WebHelper.valuesHeader);
							// controlValue=getCellData(logicalName,headerValues,
							// valuesRowIndex, valuesHeader);

							WebHelper.testcaseID = rowValues.getCell(Integer.parseInt(WebHelper.valuesHeader.get("TestCaseID").toString()));

							if (WebHelper.testcaseID == null) {
								testCase = "";
							} else {
								testCase = WebHelper.testcaseID.toString();
							}
							transactionType = rowValues.getCell(WebHelper.valuesHeader.get("TransactionType"));
						}


						if ((action.equals("I") && !ctrlValue.isEmpty())
								|| (action.equals("V") && !ctrlValue.isEmpty()) 
								// Mrinmayee 06-12-2018
								|| (action.equals("V_availabilityStatus") && !ctrlValue.isEmpty())
								|| (action.equals("V_text") && !ctrlValue.isEmpty())
								|| (action.equals("V_edit") && !ctrlValue.isEmpty())
								|| (action.equals("V_disableStatus") && !ctrlValue.isEmpty())
								|| (action.equals("V_checkboxStatus") && !ctrlValue.isEmpty())
								|| (action.equals("VFPresence") && !ctrlValue.isEmpty())
								|| (action.equals("InputifExist") && !ctrlValue.isEmpty())
								|| (!action.equals("I") && !action.equals("V") && !action.equals("V_availabilityStatus") && !action.equals("V_edit") && !action.equals("V_text") && !action.equals("V_disableStatus") && !action.equals("VFPresence") &&!action.equals("V_checkboxStatus") && !action.equals("VerifyPopUpElement"))) {
								
						if (controltype.equalsIgnoreCase("PDFDocumentCompare") || controltype.equalsIgnoreCase("IgnoreString")) {
								if (controltype.equalsIgnoreCase("IgnoreString")) {
									PDFComparisonUtil.setStringToIgnore(ctrlValue);
								}
								if (controltype.equalsIgnoreCase("PDFDocumentCompare")) {
									if (!ctrlValue.isEmpty()) {
										String[] result = PDFComparisonUtil.PDFCompare(logicalName, controltype, ctrlValue);
										webDriver.report = WriteToDetailResults(result[1], result[2], logicalName);
									}
								}

							} else {
								if (!controltype.startsWith("Sikuli")) {
									if (!action.equalsIgnoreCase("LOOP")
											&& !controltype.equalsIgnoreCase("Wait")
											&& !controltype.equalsIgnoreCase("Wait_DM")	
											// Minaakshi : 05-02-2019	
											&& !action.equalsIgnoreCase("END_LOOP")
											&& !controltype.equalsIgnoreCase("Browser")
											&& !controltype.equalsIgnoreCase("CloseBrowser")
											&& !controltype.equalsIgnoreCase("AttributeisPresent")
											&& !controltype.equalsIgnoreCase("CloseAndLaunchNewBrowser")
											&& !controltype.equalsIgnoreCase("PageRefresh")
											&& !controltype.equalsIgnoreCase("WaitForPageToLoad")
											&& !controltype.equalsIgnoreCase("WaitUntilElementInvisible")
											&& !controltype.equalsIgnoreCase("filedownload")
											&& !controltype.equalsIgnoreCase("NewBrowser")
											&& !controltype.equalsIgnoreCase("Window")
											&& !controltype.equalsIgnoreCase("Alert")
											&& !controltype.equalsIgnoreCase("URL")
											&& !controltype.equalsIgnoreCase("WaitForJS")
											&& !controltype.contains("Robot")
											&& !controltype.equalsIgnoreCase("Calendar")
											&& !controltype.equalsIgnoreCase("CalendarNew")
											&& !controltype.equalsIgnoreCase("CalendarIPF")
											&& !controltype.equalsIgnoreCase("CalendarEBP")
											&& (!action.equalsIgnoreCase("Read") || ((action.equalsIgnoreCase("Read")
													&& !StringUtils.isEmpty(controlName) && !ctrlValue.equalsIgnoreCase("IGNORE"))))// Minaakshi
											&& !controltype.equalsIgnoreCase("JSScript")
											&& !controltype.equalsIgnoreCase("DB")
											&& !controltype.equalsIgnoreCase("Database")// Minaakshi
											&& !controltype.equalsIgnoreCase("CreateDynamicData")// Minaakshi
																									// :
																									// 03-10-2018
											&& !controlID.equalsIgnoreCase("XML")
											&& !controltype.startsWith("Process")
											&& !controltype.startsWith("Destroy")
											&& !controltype.startsWith("ReadSikuli")
											&& !controltype.equalsIgnoreCase("WebService")
											&& !action.equalsIgnoreCase("VA")
											&& (ctrlValue == null || !ctrlValue.equalsIgnoreCase("IGNORE"))// Minaakshi
											&& !controltype.equalsIgnoreCase("IFrame") && !action.equalsIgnoreCase("LOOP")
											&& !controltype.equalsIgnoreCase("RowNumbersToExecute")
											&& !controltype.equalsIgnoreCase("WaitForElementToVisible")
											&& !controltype.equalsIgnoreCase("Wait_IfValue") && !action.equalsIgnoreCase("Billing") 
											&& !controltype.equalsIgnoreCase("D_Wait")
											&& !action.equalsIgnoreCase("UpdateDBScript")) {
										if ((indexVal.equalsIgnoreCase("") || indexVal.equalsIgnoreCase("0"))
												&& !controlID.equalsIgnoreCase("TagValue") && !controlID.equalsIgnoreCase("TagText")) {
											webElement = getElementByType(controlID, controlName, WebHelper.control, imageType, ctrlValue);

											// webElement =
											// getElementByType(controlName);
										} else {
											controlList = WebHelperUtil.getElementsByType(controlID, controlName, WebHelper.control, imageType,
													ctrlValue);

											if (controlList != null && controlList.size() > 1) {
												webElement = WebHelperUtil.GetControlByIndex(indexVal, controlList, controlID, controlName,
														WebHelper.control, ctrlValue); // ,
												// ISelenium
												// selenium)
											} else {
												break;
											}
										}
									}
								} else {
									sikuliScreen = new Screen();
									// bhaskar
									// sikuliapp = Automation.SikuliScr;
									// bhaskar
								}
							}
						}

						/*** Perform action on the identified control ***/
						// added by sheetal for new control type:
						// XPathValueMultiWithInput
						if (controlID.equalsIgnoreCase("XPathValueMultiWithInput")) {
							String tempcontrolValue = ctrlValue;
							String[] tempValues2 = tempcontrolValue.split(";;");
							int l = tempValues2.length;
							ctrlValue = tempValues2[l - 1];
						}

						if (!(controltype.equalsIgnoreCase("PDFDocumentCompare") || controltype.equalsIgnoreCase("IgnoreString"))) {
							doAction(imageType, controltype, controlID, controlName, ctrlValue, logicalName, action, webElement, true,
									sheetStructure, headerValues, rowIndex, rowCount, rowNo, colNo);
						}

						// log.info("ctrlValue :"+ctrlValue);

						/*** Perform action on the identified control ***/
					}

					if (action == "END_LOOP" && (valuesRowCount != valuesRowIndex)) {
						WebHelper.loopRow = 1;
						break;
					}

				} else {
					System.out.println("ExecuteFlag is N");
					log.info("ExecuteFlag is N");
				}
			}

			// Setting of reporting values after execution in case of no
			// exception
			Date toDate = new Date();
			webDriver.getReport().setFromDate(Config.dtFormat.format(WebHelper.frmDate));
			webDriver.getReport().setIteration(Config.cycleNumber);
			webDriver.getReport().setTestcaseId(controller.controllerTestCaseID.toString());
			webDriver.getReport().setGroupName(controller.controllerGroupName.toString());
			webDriver.getReport().setTrasactionType(controller.controllerTransactionType.toString());
			webDriver.getReport().setTestDescription(controller.testDesciption);
			webDriver.getReport().setToDate(Config.dtFormat.format(toDate));

			// Setting status for field verification failures
			if (WebHelper.fieldVerFailCount > 0) {
				webDriver.getReport().setMessage("Check Detailed Results");
				webDriver.getReport().setStatus("FAIL");
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			controller.pauseFun(e.getMessage());
		} finally {
			WebHelper.structureHeader.clear();
			WebHelper.valuesHeader.clear();
			ExcelUtility.writeReportPAS(webDriver.getReport());
			WebHelper.fieldVerFailCount = 0;
		}
	}

	public static Reporter WriteToDetailResults(String expectedValue, String actualValue, String columnName) throws IOException,NullPointerException

	{
	
		//below condition changed to take the value from config sheet
		//if (WebHelper.file.exists() == true && DResult == 1) {
		if (WebHelper.file.exists() == true && StringUtils.equalsIgnoreCase(Config.appendVerificationResultPath,"false") && DResult == 1) {
			// print = new PrintStream(file);
			WebHelper.file.delete();
		}
		// File file= new
		// File(Automation.getConfigValue("VERIFICATIONRESULTSPATH").toString());
		Reporter report = new Reporter();
		report.setReport(report);
		report = report.getReport();
		String passCount = "";
		String failCount = "";
		report.setTestcaseId(controller.controllerTestCaseID.toString());
		report.setTrasactionType(transactionType.toString());
		report.setTestDescription(controller.testDesciption);
		// Quote ID for PAS report requirement
		if (controller.controllerQuoteId != null) {
			report.setStrQuoteId(controller.controllerQuoteId.toString());
		} else {
			report.setStrQuoteId("");
		}

		if ((expectedValue.trim()).equalsIgnoreCase(actualValue.trim())) {
			report.setActualValue(actualValue);
			report.setExpectedValue(expectedValue);
			report.setStatus("PASS");
			report.setToDate(Config.dtFormat.format(WebHelper.frmDate));
			passCount = "1";
			failCount = "0";
			TotalpassCount += 1;
			System.out.println(TotalpassCount);
		} else {
			report.setActualValue("FAIL|" + actualValue + "|" + expectedValue);
			System.out.println("ab");
			report.setExpectedValue(expectedValue);
			report.setStatus("FAIL");
			report.setToDate(Config.dtFormat.format(WebHelper.frmDate));
			failCount = "1";
			passCount = "0";
			// DS:30-05-2014
			WebHelper.fieldVerFailCount += 1;
			TotalfailCount += 1;
			System.out.println(TotalfailCount);
		}

		WebHelper.print = new PrintStream(new FileOutputStream(WebHelper.file, true));

		int usedRows = WebHelperUtil.count(WebHelper.file);
		if (usedRows == 0) {
			WebHelper.print.print("Iteration,TestCaseID,TransactionType,CurrentDate,RowType,Status,PassCount,FailCount, Compare Result,Quote Number");
			WebHelper.print.println();
		}
		usedRows = WebHelperUtil.count(WebHelper.file);

		WebHelper.print.print(ExcelUtility.myChar + Config.cycleNumber + ExcelUtility.myChar + "," + ExcelUtility.myChar + report.getTestcaseId()
				+ ExcelUtility.myChar + "," + ExcelUtility.myChar + report.getTrasactionType() + ExcelUtility.myChar + "," + ExcelUtility.myChar
				+ report.getToDate() + ExcelUtility.myChar + "," + ExcelUtility.myChar + "Field: " + columnName + ExcelUtility.myChar + ","
				+ ExcelUtility.myChar + report.getStatus() + ExcelUtility.myChar + "," + ExcelUtility.myChar + passCount + ExcelUtility.myChar + ","
				+ ExcelUtility.myChar + failCount + ExcelUtility.myChar + "," + ExcelUtility.myChar + report.getActualValue() + ExcelUtility.myChar
				+ "," + ExcelUtility.myChar + report.getStrQuoteId() + ExcelUtility.myChar);
		WebHelper.print.println();
		DResult++;
		return report;
	}

	/** Locating Web Element **/
	public static WebElement getElementByType(String controlId, String controlName, String controlType, String imageType, String controlValue)
			throws Exception {
		// String a=controlValue;

		WebDriverWait wait = new WebDriverWait(Automation.driver, Integer.parseInt(Config.timeOut));
		if (Config.projectName.equals("DistributionManagement")) // Minaakshi : 05-02-2019
		{
			WebHelperPAS.implementWait();
		}
		WebElement controlList = null;
		Constants.ControlIdEnum controlID = Constants.ControlIdEnum.valueOf(controlId);
		WebDriverWait wait2 = new WebDriverWait(Automation.driver, 2);

		// System.out.println("bhaski controlID:"+controlID);
		try {
			switch (controlID) {
			case doNothing:
				break;
			case PageToLoad:

			case Id:

				// WebHelper.getText(Automation.driver,Automation.driver.findElement(By.xpath("")));
			case HTMLID:
				// System.out.println(Automation.driver.getCurrentUrl());

				controlList = wait.until(ExpectedConditions.elementToBeClickable(By.id(controlName)));
				break;

			case XPath_ctrvalue:

			case XPath:

				// JavascriptExecutor js =
				// (JavascriptExecutor)Automation.driver;
			if (Config.projectName.equals("DistributionManagement")){// Minaakshi : 05-02-2019
					try{
						
						Thread.sleep(1000);
						controlList = dmgebtWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));
						
					}catch (Exception ex) {
						log.error(ex.getMessage(), ex);
						log.info("In Second Wait : XPATH : Element is not clickable : " + controlName);
						controlList = waitForElementPresence.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(controlName)));
					}}
					
				else{

				Thread.sleep(1000);

				controlList = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));
				}

				break;
			case XPathValue: // Minaakshi : 10-09-2018

				String tempCtrlName = controlName;
				String tempReplaceString = tempCtrlName.replace("$value", controlValue);
				controlList = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(tempReplaceString)));

//				try{// Minaakshi : 05-02-2019
//					controlList = dmgebtWait.until(ExpectedConditions.elementToBeClickable(By.xpath(tempReplaceString)));
//
//					
//				}catch (Exception ex) {
//					log.error(ex.getMessage(), ex);
//					log.info("In Second Wait : XPATH : Element is not visible : " + controlName);
//					controlList = waitForElementPresence.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(tempReplaceString)));
//					//controlList = waitForElementPresence.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(controlName)));
//				}
				break;
			case XPathValue_cpp: // Subhasis : 27-11-2018
				// case added for PAS requirement from Sandesh Kumbhar
				try {
					String tempCtrlNamecbp = controlName;
					String tempReplaceStringcbp = tempCtrlNamecbp.replace("$value", controlValue);
					controlList = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(tempReplaceStringcbp)));
				} catch (Exception e) {
					log.info(e.getMessage());
					return controlList;
				}
				break;
				
				
			case XPath_VFCount: //Sheetal:2/13/2019 - for form count verification in Policy2015
				try
				{
					String tempCtrlName2 = controlName;
					 int tempControlValue=Integer.parseInt(controlValue);
					 tempControlValue=tempControlValue+4;
					 String extraFormLocator=Integer.toString(tempControlValue);
					 
		             String tempReplaceString2 = tempCtrlName2.replace("$value", extraFormLocator);
		             controlList = Automation.driver.findElement(By.xpath(tempReplaceString2));
				}
				catch(Exception e){
					controlList=null;
				}
				break;	
				
				
				
			case XPathValue_p: // Mrinmayee : 15-11-2018
				try {
					// WebDriverWait wait2 = new WebDriverWait(Automation.driver
					// ,5);
					String tempCtrlName1 = controlName;
					String tempReplaceString1 = tempCtrlName1.replace("$value", controlValue);
					controlList = wait2.until(ExpectedConditions.presenceOfElementLocated(By.xpath(tempReplaceString1)));
				} catch (Exception e) {

					controlList = null;
				}

				break;
				//Sheetal:2/19/2019
			case XPathValueMulti:
                String tempcontrolValue=controlValue;
                String[] tempValues= tempcontrolValue.split(";;");
                String tempReplaceString3=controlName;
                int i=1;
                for(String temp: tempValues)
                {
                	tempReplaceString3=tempReplaceString3.replace("$" + i + "value", temp.trim());
                	i++;
                }
				              
                controlList = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(tempReplaceString3)));
                break;
				
			case XPathValueMultiWithInput: // Mrinmayee 12-12-2018
				String tempcontrolValue2 = controlValue;
				String[] tempValues2 = tempcontrolValue2.split(";;");
				String tempReplaceString4 = controlName;
				int k = 1;
				for (int j = 0; j < tempValues2.length - 1; j++) {
					tempReplaceString4 = tempReplaceString4.replace("$" + k + "value", tempValues2[j].trim());
					k++;
				}
				controlList = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(tempReplaceString4)));
				break;
			
			
			case XPathValueMulti_p:
                String tempcontrolVal=controlValue;
                String[] tempVal= tempcontrolVal.split(";;");
                String tempReplaceStr=controlName;
                int l=1;
                for(String temp: tempVal)
                {
                	tempReplaceStr=tempReplaceStr.replace("$" + l + "value", temp.trim());
                	l++;
                }
                try
				{
					controlList = wait2.until(ExpectedConditions.presenceOfElementLocated(By.xpath(tempReplaceStr)));
				}
				catch(Exception e){

						controlList=null;
				}
				break;
				
			case XPath_dy:// Minaakshi : Added this case while handling dyanamic
							// xpath which requires ctrlvalue from unique number
				String UNSheetValue = "";
				if (controlValue.equalsIgnoreCase("Yes")) {// Minaakshi :
															// 6-12-2018
					UNSheetValue = DMProduct.ReadFromExcelUsingColumnName("", WebHelper.columnName);
				} else {
					UNSheetValue = DMProduct.ReadFromExcelUsingColumnName(controlValue, WebHelper.columnName);
				}

				String tempCtrlName1 = controlName;
				String tempReplaceString1 = tempCtrlName1.replace("$value", UNSheetValue);
				//controlList = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(tempReplaceString1)));
				try{// Minaakshi : 05-02-2019
					controlList = dmgebtWait.until(ExpectedConditions.elementToBeClickable(By.xpath(tempReplaceString1)));
					
				}catch (Exception ex) {
					log.error(ex.getMessage(), ex);
					log.info("In Second Wait : XPATH : Element is not visible : " + controlName);
					controlList = waitForElementPresence.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(tempReplaceString1)));
					//controlList = waitForElementPresence.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName)));
				}
					
				break;
			 //Added by sheetal
			case XPath_Wait:
				WebDriverWait wait3 = new WebDriverWait(Automation.driver ,5);	
				int p, max=600;
				for(p=1;p<max;p+=10)
				{
					try
					{
						controlList=wait3.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));	
						System.out.println("Found element after waiting for approx: " + p + " seconds.");
						break;
					}
					catch(Exception e){
						Thread.sleep(10000);
					}
				}
				
				if(p>=max)
				{
					System.out.println("Time-out occured after wating for approx." + max + " seconds. Element: " + controlName + " is still not loaded.");
					controlList=null;
				}
				break;

				
			case Name:
				
				if (Config.projectName.equals("DistributionManagement")){
					try{// Minaakshi : 05-02-2019
						controlList = dmgebtWait.until(ExpectedConditions.elementToBeClickable(By.name(controlName)));
						
					}catch (Exception ex) {
						log.error(ex.getMessage(), ex);
						log.info("In Second Wait : NAME : Element is not visible : " + controlName);
						controlList = waitForElementPresence.until(ExpectedConditions.visibilityOfElementLocated(By.name(controlName)));
					}}
				else{
					controlList = wait.until(ExpectedConditions.elementToBeClickable(By.name(controlName)));
				}
 			//controlList = wait.until(ExpectedConditions.elementToBeClickable(By.name(controlName)));
				break;

			case ClassName:
				controlList = wait.until(ExpectedConditions.elementToBeClickable(By.className(controlName)));
				break;

			case LinkText:
				// js.executeScript("return document.readyState").toString().equals("complete");
				controlList = wait.until(ExpectedConditions.elementToBeClickable(By.linkText(controlName)));
				break;

			case LinkValue:
				controlList = wait.until(ExpectedConditions.elementToBeClickable(By.linkText(controlValue)));
				break;

			case TagText:
			case TagValue:
			case TagOuterText:
				controlList = wait.until(ExpectedConditions.elementToBeClickable(By.tagName(imageType)));
				break;

			case CSSSelector:
				controlList = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(controlName)));
				break;

			// GAIC AJAX controls - TM:02/02/2015
			case AjaxPath:
				// controlList =
				if (Config.projectName.equals("DistributionManagement")){
					try{// Minaakshi : 05-02-2019
						controlList = dmgebtWait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName + "[contains(text(),'" + controlValue + "')]")));
						
					}catch (Exception ex) {
						log.error(ex.getMessage(), ex);
						log.info("In Second Wait : XPATH : Element is not visible : " + controlName);
						controlList = waitForElementPresence.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(controlName)));
						//controlList = waitForElementPresence.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName)));
					}}
				else{
					// controlList =
					// Automation.driver.findElement(By.xpath(controlName+"[contains(text(),'"+controlValue+"')]"));
					controlList = wait
							.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName + "[contains(text(),'" + controlValue + "')]")));
					break;					
				}
				// Automation.driver.findElement(By.xpath(controlName+"[contains(text(),'"+controlValue+"')]"));
				//controlList = wait
						//.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName + "[contains(text(),'" + controlValue + "')]")));
				break;

			case AjaxPath_dy:// Minaakshi : Added this case while handling
								// dyanamic xpath which requires ctrlvalue from
								// unique number sheet
				String UNSheetValue2 = "";
				if (controlValue.equalsIgnoreCase("Yes")) {
					UNSheetValue2 = DMProduct.ReadFromExcelUsingColumnName("", WebHelper.columnName);
				} else {
					UNSheetValue2 = DMProduct.ReadFromExcelUsingColumnName(controlValue, WebHelper.columnName);
				}

				String temp2 = controlName + "[contains(text(),'" + UNSheetValue2 + "')]";
				// controlList =
				// Automation.driver.findElement(By.xpath(controlName+"[contains(text(),'"+controlValue+"')]"));
				//controlList = wait
						//.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName + "[contains(text(),'" + UNSheetValue2 + "')]")));
				try{// Minaakshi : 05-02-2019
					controlList = dmgebtWait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName + "[contains(text(),'" + UNSheetValue2 + "')]")));

					
				}catch (Exception ex) {
					log.error(ex.getMessage(), ex);
					log.info("In Second Wait : XPATH : Element is not visible : " + controlName);
					controlList = waitForElementPresence.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(controlName + "[contains(text(),'" + UNSheetValue2 + "')]")));

					//controlList = waitForElementPresence.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName)));
				}
				break;

			case Id_p:
			case HTMLID_p:
				controlList = wait.until(ExpectedConditions.presenceOfElementLocated(By.id(controlName)));
				break;

			case XPath_H:

				boolean controlList1 = wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(controlName)));
				/*
				 * controlList = wait.until(ExpectedConditions
				 * .presenceOfElementLocated(By.xpath(controlName)));
				 */
				break;

			case XPath_p:
				if (Config.projectName.equals("DistributionManagement")){
					try{ // Minaakshi : 05-02-2019
						controlList = dmgebtWait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));
					}catch (Exception ex) {
						log.error(ex.getMessage(), ex);
						log.info("In second wait : XPATH : Element is not visible : " + controlName);
						controlList = waitForElementPresence.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(controlName)));
					}}
				else{
				controlList = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));
				}
				break;
				
			case Xpath_ctrlvalue:
				try {

					if (!controlValue.isEmpty()) {

						controlName = controlName.replace("$value", controlValue);

						controlList = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName)));

					} else {
						controlList = null;
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					controlList = null;
				}

				break;

			case XPath_R:

				// controlList =
				// wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlValue)));

				try {

					if (!controlValue.isEmpty()) {

						controlList = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlValue)));

					} else {
						controlList = null;
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					controlList = null;
				}

				break;

			case XPath_value:

				if (!controlValue.isEmpty()) {

					try {

						if (Automation.driver.findElement(By.xpath(controlName)).isDisplayed()) {

							List<WebElement> webElement = Automation.driver.findElements(By.xpath(controlName));

							for (WebElement element : webElement) {

								System.out.println(element.getAttribute("value"));

								if (element.getAttribute("value").equalsIgnoreCase(controlValue)) {

									String Id = element.getAttribute("id");

									String elementTagName = element.getTagName();

									String controlName1 = "//" + elementTagName + "[@id='" + Id + "'" + "]/" + imageType;

									controlName = controlName1;

									controlList = Automation.driver.findElement(By.xpath(controlName));

									break;
								}

								else {

									System.out.println("Element value doesn't match with control value");
								}

							}

							controlList = wait2.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName)));
							// controlList=fluentWait(By.xpath(controlName));
						}

					} catch (Exception e) {
						log.error(e.getMessage(), e);

						controlList = null;
					}
				} else {

					System.out.print("don't perform any action");
				}
				break;

			// Mrinmayee - Wait till visibility of element
			case XPath_vis:

				Thread.sleep(1000);
				controlList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(controlName)));

				break;
			// Mrinmayee - Wait till visibility of element
			case XPath_if:

				try {

					// WebDriverWait wait2 = new
					// WebDriverWait(Automation.driver,5);
					//controlList = wait2.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName)));
					if (Automation.driver.findElement(By.xpath(controlName)).isDisplayed()){				
						controlList = Automation.driver.findElement(By.xpath(controlName));				
					}		
				} catch (Exception e) {
					//log.error(e.getMessage(), e);
					controlList = null;
				}
				break;

			}
			return controlList;
		} catch (Exception e) {
			// System.out.println("bhaskar in catch block");
			log.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}
	}

	@SuppressWarnings("incomplete-switch")
	public static String doAction(String imageType, String controlType, String controlId, String controlName, String ctrlValue, String logicalName,
			String action, WebElement webElement, Boolean Results, HSSFSheet strucSheet, HSSFSheet valSheet, int rowIndex, int rowcount,
			String rowNo, String colNo) throws Exception {

		// new
		// WebDriverWait(Automation.driver,timeOutInSeconds).until(ExpectedConditions.invisibilityOfElementLocated(By.id("divProgress")));

		List<WebElement> WebElementList = null;
		String currentValue = null;
		// HSSFSheet uniqueNumberSheet =null;
		String uniqueNumber = "";
		WebVerification.isFromVerification = false;
		// HashMap<String ,Object> uniqueValuesHashMap = null;
		// HSSFRow uniqueRow = null;
		Constants.ControlTypeEnum controlTypeEnum = Constants.ControlTypeEnum.valueOf(controlType);
		Constants.ControlTypeEnum actionName = Constants.ControlTypeEnum.valueOf(action);
		// bhaskar
		WebHelper.sikscreen = Config.SikuliScr;
		// System.out.println(sikscreen);
		// bhaskar
		if (controlType.contains("Robot") && !WebHelper.isIntialized) {
			System.out.println("In method doaction debug1");
			WebHelper.robot = new Robot();
			WebHelper.isIntialized = true;
		}

		log.info("ctrlValue doAction:" + ctrlValue);
		// Mrinmayee - skip the step for which control value is blank : start

		JavascriptExecutor js = (JavascriptExecutor) Automation.driver;
		try {
			// getHTMLResponse();
			// System.out.println("In method doaction debug3");
			switch (controlTypeEnum) {

			case WebEdit:
				switch (actionName) {
				case NC:

					webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);

					try {
						if (webElement.isEnabled()) {
							Thread.sleep(4000);
							// webElement.sendKeys(Keys.INSERT(ctrlValue));
							webElement.click();

							// webElement.sendKeys(Keys.chord(Keys.CONTROL,"a"));

							if (webElement.getAttribute("value") != null) {
								webElement.clear();
								// webElement.sendKeys(Keys.TAB);
								Thread.sleep(1000);
							}

							webElement.sendKeys(ctrlValue);
							// webElement.sendKeys(Keys.TAB); Mrinmayee
							Thread.sleep(2000);
							if (webElement.getAttribute("value").isEmpty()) {
								webElement.sendKeys(ctrlValue);

							}
							Automation.driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
							Automation.driver.manage().timeouts().pageLoadTimeout(50, TimeUnit.SECONDS);
							// Thread.sleep(2000);

							Thread.sleep(2000);
						} else {
							System.out.println("Element Not Found");
						}
						;

						// Search_Box.sendKeys(Keys.ENTER);

					} catch (StaleElementReferenceException e) {
						log.error(e.getMessage(), e);
						System.out.println("Trying to recover from a stale element :" + e.getMessage());

					}

					break;

				case Read:
					if (ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					if (Config.projectName.equals("DistributionManagement")) {
						uniqueNumber = DMProduct.ReadFromExcelUsingColumnName(ctrlValue, WebHelper.columnName);
					} else {
						uniqueNumber = WebHelperUtil.ReadFromExcel(ctrlValue, WebHelper.columnName);
					}
					// System.out.println("!!!!!!!!!!!!!!!!");
					// System.out.println("uniqueNumber:"+uniqueNumber);
					webElement.clear();
					webElement.sendKeys(uniqueNumber);
					break;
				case Write:
					if (ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					WebHelperUtil.writeToExcel(ctrlValue, webElement, controlId, controlType, controlName, rowNo, colNo);
					break;
				case Input: // Replace fields InputifExist and I by Asif
					if (ctrlValue == null || ctrlValue.trim().equals("")) {
						break;
					}

					try {
						if (webElement.isEnabled()) {

							if (!ctrlValue.equalsIgnoreCase("null")) {
								{
									// System.out.println("!@#$%^&*!@#$%^&*ctrlValue:"+ctrlValue);
									for (int i = 0; i < 5; i++) {
										webElement.sendKeys(Keys.ENTER);

										webElement.clear();

										Thread.sleep(500);
										webElement.sendKeys(ctrlValue);
										// webElement.click();
										System.out.println(webElement.getAttribute("value"));
										if (!webElement.getAttribute("value").isEmpty()) {
											webElement.sendKeys(Keys.TAB);
											break;
										} else {
											Thread.sleep(3000);
										}
									}
								}
							} else {
								break;
							}
						}
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						System.out.println("Element not exist");
					}
					break;
				case WaitTodisplayElementValue:

					WebHelperUtil.WaitUntilAttributeValueEquals(webElement);

					break;
				case V:
					if (ctrlValue == null || ctrlValue.trim().equals("") || ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}

					// currentValue = webElement.getText();
					try {
						currentValue = webElement.getAttribute("value");
						// currentValue = webElement.getText();
						System.out.println(currentValue);
						if (currentValue.equalsIgnoreCase(ctrlValue)) {

							System.out.println("PASSED");
						} else {
							System.out.println("FAILED");
						}
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						currentValue = "null";
						if (currentValue.equalsIgnoreCase(ctrlValue)) {

							System.out.println("PASSED");
						} else {
							System.out.println("FAILED");
						}
					}
					break;

				case Clear:
					if (ctrlValue == null || ctrlValue.trim().equals("")) {
						break;
					}
					// webElement.clear();
					try {
						if (webElement.isDisplayed()) {

							Coordinates coordinate = ((Locatable) webElement).getCoordinates();
							coordinate.onPage();
							coordinate.inViewPort();
							Thread.sleep(1000);
							webElement.clear();
						}
					} catch (Exception e) {
						System.out.println("Element does not exist");

					}
					break;
				// js1.executeScript("arguments[0].value = '';",
				// webElement);

				case I:
					if (ctrlValue == null || ctrlValue.trim().equals("")) {
						break;
					}
					// Thread.sleep(500);

					if (Config.projectName.equals("DistributionManagement")) {// Minaakshi
						if (!ctrlValue.equalsIgnoreCase("null")) {
							System.out.println("!@#$%^&*!@#$%^&*ctrlValue:" + ctrlValue);
							webElement.clear();
							webElement.sendKeys(ctrlValue);

						} else {
							webElement.clear();
						}
					} else {
						if (ctrlValue != null) {
							// waitForPageLoadingToComplete();
							// waitForAjaxLoad(Automation.driver);
							try {
								//sheetal 2-7-2019
								((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();",webElement);
								Thread.sleep(500);
								
								if (webElement.isEnabled()) {
									WebHelperUtil.InputValue(webElement, ctrlValue, controlId, controlName, imageType, ctrlValue);
								}

							} catch (Exception e) {
								log.error(e.getMessage(), e);
								System.out.println("Element not displayed");
							}
						}
					}
					break;// Minaakshi : 17-12-2018
					
				}	
				break;
				
			case ReplaceDefault:
				switch(actionName)										
				{
					case I:
						if(!ctrlValue.equalsIgnoreCase("null"))
						{		
							
											
							try {
								int counter=1;
								String temp="";
								while(!temp.equals(ctrlValue))
								{
									
									JavascriptExecutor js1 = (JavascriptExecutor) Automation.driver;  
									js1.executeScript("arguments[0].value = '';", webElement);
									webElement.sendKeys(ctrlValue);
									Thread.sleep(2000);
									webElement.sendKeys(Keys.TAB);
									webElement.sendKeys(Keys.TAB);
									Thread.sleep(5000);
									temp = webElement.getAttribute("value");
									temp= temp.replace("$", "");
									temp =temp.replace(" ", "");
									temp=temp.replace(",", "");
									counter++;
									if(counter==5)
									{
										System.out.println("Could not set correct value");
										break;
									}
								}	
	
							} catch(StaleElementReferenceException e) {
								e.toString();
								System.out.println("Trying to recover from a stale element :" + e.getMessage());
							}
						}						
						break;

				}
				break;	
			
			case WaitForElementToBeFound:
				if(webElement == null)
				{
					System.out.println("Element not found.");
				}
				else
				{
					System.out.println("Element found.");
				}
			
			break;
			
			
			case WebButton:
				switch (actionName) {
				case FileUpload:
					// Added code to handle Fileupload window using AutoIt
					String autoitFileDir = Config.inputDataFilePath + TransactionMapping.directoryPathFileUpload;
					webElement.click();
					Thread.sleep(5000);
					// Runtime.getRuntime().exec(autoitFileDir +
					// "\\FileUpload.exe "+ctrlValue);
					Runtime.getRuntime().exec(autoitFileDir + "\\FileUpload.exe " + autoitFileDir + "\\" + ctrlValue);
					Thread.sleep(5000);
					break;

				case I:
					if (ctrlValue == null || ctrlValue.trim().equals("")) {
						break;
					}

					if (!StringUtils.equalsIgnoreCase(ctrlValue,null) || !ctrlValue.equalsIgnoreCase("No")) {

						// waitForPageLoadingToComplete();
						// waitForAjaxLoad(Automation.driver);
						int i = 7;
						while (i >= 1) {
							try {

								webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);
								/*
								 * if(webElement==null){
								 * 
								 * break; }
								 */
								if (webElement.isEnabled() == true) {

									Thread.sleep(500);
									// webElement.click();
									// Mrinmayee - Webbutton click with
									// conditions
									Actions MousebuilderClick = new Actions(Automation.driver);
									Action MouseclickAction = MousebuilderClick.moveToElement(webElement).clickAndHold().release().build();
									MouseclickAction.perform();

									Thread.sleep(1000);
									break;
								}
							} catch (Exception e) {
								log.error(e.getMessage(), e);
								try {
									webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);

									Thread.sleep(500);
									if (webElement.isEnabled()) {
										if (i <= 5) {
											// js.executeScript("arguments[0].focus(); arguments[0].blur(); return true",
											// webElement);
											Actions MousebuilderClick = new Actions(Automation.driver);
											Action MouseclickAction = MousebuilderClick.moveToElement(webElement).clickAndHold().release().build();
											MouseclickAction.perform();
											break;
										} else {
											webElement.click();
											break;
										}

										// webElement.sendKeys(Keys.ENTER);
									}
									Thread.sleep(1000);

								} catch (Exception e1) {
									log.error(e.getMessage(), e);
									Thread.sleep(3000);
									i--;
									System.out.println("Element does not exist");
									// e1.printStackTrace();
								}
							}
						}
					} else {
						System.out.println("Don't Perform any action");
					}
					break;

				case NC:

					// waitForAjaxLoad(Automation.driver);
					// waitForPageLoadingToComplete();
					int iCtr = 7;
					while (iCtr >= 1) {

						webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);

						try {

							if (webElement.isEnabled()) {

								Thread.sleep(500);

								webElement.click();
								Thread.sleep(500);

							}
							break;
						}

						catch (Exception e) {
							log.error(e.getMessage());
							try {
								Thread.sleep(500);
								webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);

								if (webElement.isEnabled()) {

									if (iCtr <= 6) {
										// js.executeScript("arguments[0].focus(); arguments[0].blur(); return true",
										// webElement);
										Actions MousebuilderClick = new Actions(Automation.driver);
										Action MouseclickAction = MousebuilderClick.moveToElement(webElement).clickAndHold().release().build();
										MouseclickAction.perform();
										System.out.println("catchclicked");
										log.info("catchclicked");
										Thread.sleep(500);
										break;
									} else {
										// js.executeScript("arguments[0].focus(); arguments[0].blur(); return true",
										// webElement);
										webElement.click();
										break;
									}
								}

							} catch (Exception e1) {
								// webElement = getElementByType(controlId,
								// controlName,control,imageType,ctrlValue);
								if (iCtr <= 2) {
									log.error(e1.getMessage(), e1);
								} else {
									log.error(e1.getMessage());
								}
								Thread.sleep(1000 * iCtr);

								iCtr--;
								System.out.println("Element does not exist");
								// e1.printStackTrace();

							}
						}
					}
					Thread.sleep(1000);
					break;

				case V:
					if (ctrlValue == null || ctrlValue.trim().equals("") || ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					if (webElement.isDisplayed()) {
						if (webElement.isEnabled() == true)
							currentValue = "True";
						else
							currentValue = "False";
					}

				case ActionClick:

				// Minaakshi : 05-02-2019
					if (ctrlValue == null || ctrlValue.trim().equals("") || ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					Actions builderClick = new Actions(Automation.driver);
					Action clickAction = builderClick.moveToElement(webElement).clickAndHold().release().build();
					clickAction.perform();

					
					break;
				// Mrinmayee - Action click with conditions end
				case QSP:
					if (ctrlValue == null || ctrlValue.trim().equals("")) {
						break;
					}
					try {
						Automation.driver.switchTo().frame(Automation.driver.findElement(By.xpath(controlName)));
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						System.out.print("Quote search page not present");
					}
					break;

				case NCIF:
					//Sheetal: 2/19/2019, commenting below line as its already executed in flow
					//webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);

					try {
	
						if (webElement.isDisplayed()) {

							// if(webElement.isEnabled() == true)

							 Actions Click = new Actions(Automation.driver);
							 highlightElement(webElement);
							 Action Mouseclick = Click.moveToElement(webElement).clickAndHold().release().build();
							 Mouseclick.perform();
							 Thread.sleep(500);

						   //Sheetal: 2/19/2019, commenting below line as it should not be part of Webbutton->NCIF
						}
						
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						System.out.println("Element Not Found on Page.");
						log.info("Element Not Found on Page in NCIF case");
					}

					break;
				}
				break;
			// break;

			case WebElement:

				WebVerification.isFromVerification = true;
				// bhaskar
				switch (actionName) {

				case I: // Minaakshi
					if (ctrlValue == null || ctrlValue.trim().equals("")) {
						break;
					}																																																																																										
					if (ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes") || !ctrlValue.equalsIgnoreCase("")) {
						webElement.click();
					}
					break;

				case NC:

					// waitForAjaxLoad(Automation.driver);
					// waitForPageLoadingToComplete();
					int i = 5;
					while (i > 1) {

						try {

							if (webElement.isDisplayed()) {

								webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);

								webElement.click();

								System.out.println("clicked");
								log.info("WebElement:NC->clicked");
								// Thread.sleep(500);
								// Automation.driver.manage().timeouts().pageLoadTimeout(60,TimeUnit.SECONDS);

								break;

							}
						}

						catch (Exception e) {
							log.error(e.getMessage(), e);
							try {

								webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);

								Thread.sleep(2000);

								webElement.click();

								System.out.println("catchclicked");
								log.info("WebElement:NC->catchclicked");
								break;

							} catch (Exception e1) {
								log.error(e1.getMessage(), e1);
								// webElement = getElementByType(controlId,
								// controlName,control,imageType,ctrlValue);
								Thread.sleep(3000);

								i--;
								System.out.println("Element does not exist");
								log.info("WebElement:NC->Element does not exist");
								// e1.printStackTrace();

							}
						}
					}

					break;

				case Read:
					if (ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					uniqueNumber = WebHelperUtil.ReadFromExcel(ctrlValue, WebHelper.columnName);
					webElement.clear();
					webElement.sendKeys(uniqueNumber);
					break;
				case Write:
					if (ctrlValue == null) {

						WebHelperUtil.writeToExcel(ctrlValue, webElement, controlId, controlType, controlName, rowNo, colNo);
						break;
					}
					if (ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					if (Config.projectName.equals("DistributionManagement")) {// Minaakshi
						if (logicalName.equalsIgnoreCase("RequestID")) {
							DMProduct.writeRequestIDToExcel(ctrlValue, webElement, controlId, controlType, controlName, rowNo, colNo);
						} else {
							WebHelperUtil.writeToExcel(ctrlValue, webElement, controlId, controlType, controlName, rowNo, colNo);
						}
						break;
					} else {
						WebHelperUtil.writeToExcel(ctrlValue, webElement, controlId, controlType, controlName, rowNo, colNo);

					}
					/*
					 * WebHelperUtil.writeToExcel(ctrlValue, webElement,
					 * controlId, controlType, controlName, rowNo, colNo);
					 */
					break;

				case VD: // Minaakshi : Added for handling dynamic field
							// level verification
					if (ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}

					if (ctrlValue.equalsIgnoreCase(null)) {
						ctrlValue = DMProduct.ReadFromExcelUsingColumnName(ctrlValue, WebHelper.columnName);
					} else {
						String strTemp = ctrlValue;
						if (ctrlValue.contains("$value")) {// Minaakshi :
							ctrlValue = DMProduct.ReadFromExcelUsingColumnName("", WebHelper.columnName);
						} else if (ctrlValue.contains("|")) {
							// Minaakshi : 04-01-2019
							String[] temp = ctrlValue.split("\\|");
							String tempCtrlValue = DMProduct.ReadFromExcelUsingColumnName(temp[0].toString(), WebHelper.columnName);
							ctrlValue = temp[1].toString().replace("*value", tempCtrlValue);
						} else {
							ctrlValue = DMProduct.ReadFromExcelUsingColumnName(ctrlValue, WebHelper.columnName);
						}

						/*
						 * if (ctrlValue.contains("/")&&
						 * ctrlValue.contains("$value")){//Minaakshi :
						 * 03-10-2018 ctrlValue = ctrlValue.replace("/", "-"); }
						 */

						// ***Change done by DM Team***//
						if (ctrlValue.contains("/") && strTemp.contains("$value")) {// Minaakshi:14-11-2018
							ctrlValue = ctrlValue.replace("/", "-");
						}// ***//

						if (strTemp.contains("$value")) {// Minaakshi :
							String strReplacedString = strTemp.replace("$value", ctrlValue);
							ctrlValue = strReplacedString;
						}

					}

					if (WebVerification.isFromVerification == true) {
						currentValue = webElement.getText();
						if (currentValue.equalsIgnoreCase(null) || currentValue.equalsIgnoreCase("")) {
							currentValue = webElement.getAttribute("value");
						}

						break;
					}
					boolean textPresent1 = false;
					textPresent1 = webElement.getText().contains(ctrlValue);
					if (textPresent1 == false) {
						currentValue = Boolean.toString(textPresent1);

					} else {
						currentValue = ctrlValue;

					}

					break;

					
				case PopUpCount:
					if (ctrlValue == null || ctrlValue.trim().equals("")) {
						break;
					}

					// List<WebElement> a=webElement.getSize();
					List<WebElement> a = webElement.findElements(By.xpath(controlName));
					// List<WebElement> a=getElementByType(controlId,
					// controlName,control, imageType, ctrlValue);
					int b = a.size();
					currentValue = String.valueOf(b);
					if (WebVerification.isFromVerification == true) {
						if (ctrlValue.equals(currentValue)) {

							System.out.println("PASS");
						} else {
							System.out.println("FAIL");

						}
					}
					break;
				// System.out.println(b);
				// System.out.println(a);

				case V:
					if (StringUtils.equalsIgnoreCase(ctrlValue,null) || ctrlValue.trim().equals("") || ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					if (WebVerification.isFromVerification == true) {
						currentValue = webElement.getText();
						System.out.println(currentValue);

						// ***Change done by DM Team***/
						if (currentValue.equalsIgnoreCase(null) || currentValue.equalsIgnoreCase(""))// Minaakshi
																										// :
																										// 14-11-2018
						{
							currentValue = webElement.getAttribute("value");
						}
						// ***End***//

						break;
					}
					boolean textPresent = false;
					textPresent = webElement.getText().contains(ctrlValue);
					if (textPresent == false) {
						currentValue = Boolean.toString(textPresent);

					} else {
						currentValue = ctrlValue;

					}
					break;
					
			case V_edit:
					if (StringUtils.equalsIgnoreCase(ctrlValue,null) || ctrlValue.trim().equals("") || ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					if (webElement != null) {
						currentValue = webElement.getAttribute("value");
						if(StringUtils.equalsIgnoreCase(currentValue, null))
						{
							currentValue = webElement.getText();
						}
						ctrlValue="Edit/Error displayed: '" + currentValue +"'.";
						currentValue=ctrlValue;
					} else {
						currentValue = "Edit/Error not displayed. Expected at : " + ctrlValue;
						ctrlValue= currentValue;
					}
					break;
					
				case V_text: //Sheetal - 2-5-2019
					if (StringUtils.equalsIgnoreCase(ctrlValue,null) || ctrlValue.trim().equals("") || ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					
						highlightElement(webElement);
						currentValue = webElement.getAttribute("value");
						
						
						if(StringUtils.equalsIgnoreCase(currentValue, null))
						{
							currentValue = webElement.getText();
						}
						
						if(ctrlValue.equalsIgnoreCase("pickFromUniqueNumbers"))
						{
							//ctrlValue = ReadFromExcel(ctrlValue);
							ctrlValue = WebHelperUtil.ReadFromExcel(ctrlValue, WebHelper.columnName);
						}
						else if(ctrlValue.equalsIgnoreCase("blank"))
						{
							if(currentValue.isEmpty())
							{
								currentValue=ctrlValue;
							}
						}
						
						break;	
					
				case V_checkboxStatus: //Sheetal - 2-5-2019
					if (StringUtils.equalsIgnoreCase(ctrlValue,null) || ctrlValue.trim().equals("") || ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					highlightElement(webElement);
					String checkedValue=webElement.getAttribute("checked");	
					
					if(StringUtils.equalsIgnoreCase(checkedValue, "true"))
					{
						currentValue="Checked";
					}
					else
					{
						currentValue="Unchecked";
					}
					if(currentValue.equalsIgnoreCase(ctrlValue))
					{
						currentValue=ctrlValue;
					}
					break;	
					
				case V_disableStatus: //Sheetal - 2-5-2019
					if (StringUtils.equalsIgnoreCase(ctrlValue,null) || ctrlValue.trim().equals("") || ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					highlightElement(webElement);
					String disableStatus=webElement.getAttribute("disabled");	
					
					if(StringUtils.equalsIgnoreCase(disableStatus, "true"))
					{
						currentValue="Disabled";
					}
					else
					{
						currentValue="Enabled";
					}
					if(currentValue.equalsIgnoreCase(ctrlValue))
					{
						currentValue=ctrlValue;
					}
					break;
				
				
				case VerifyPopUpElement:

					if (WebVerification.isFromVerification == true) {

						try {
							webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);
							if (webElement.isDisplayed()) {

								currentValue = webElement.getText();
								ctrlValue = currentValue;
								System.out.println(currentValue);

							}
						} catch (Exception e) {
							log.error(e.getMessage(), e);
							currentValue = "WebElement " + logicalName + " is not Present";
							ctrlValue = logicalName;
						}
					}

					break;

				case V_availabilityStatus: // Mrinmayee 15-11-2018
					if (StringUtils.equalsIgnoreCase(ctrlValue,null) || ctrlValue.trim().equals("") || ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					
					if (webElement != null) {
						currentValue = "Available";
					} else {
						currentValue = "Not Available";
					}

					if (currentValue.equalsIgnoreCase(ctrlValue)) {
						currentValue = ctrlValue;
					} else if (!(ctrlValue.equalsIgnoreCase("available") || ctrlValue.equalsIgnoreCase("not available"))) {
						ctrlValue = currentValue;
					}
					break;

				case VFCount: // Mrinmayee 15-11-2018
					if (webElement != null) {
						ctrlValue = "Expected Form Count = " + ctrlValue;
						currentValue = "FORM COUNT EXCEEDS";
					} else {
						ctrlValue = "Form verification is successful, if all above individual forms' status is pass | Expected Form Count = "
								+ ctrlValue;
						currentValue = ctrlValue;
					}

					break;

				case VFPresence: // Mrinmayee 15-11-2018
					if (webElement != null) {
						currentValue = ctrlValue;
					} else {
						currentValue = "Form is not ";
					}

					break;

				case NCIF:

					// webElement = getElementByType(controlId,
					// controlName,control,imageType,ctrlValue);

					try {

						// if(!ctrlValue.equalsIgnoreCase("null"))

						if (webElement.isDisplayed()) {

							// if(webElement.isEnabled() == true)

							webElement.click();
							Thread.sleep(500);
						} else {
							System.out.println("Element is not displayed");
						}

					} catch (Exception e) {
						log.error(e.getMessage(), e);
						System.out.println("Element Not Found on Page.");
					}

					break;

				case Highlight:

					WebHelperUtil.fnHighlightMe(Automation.driver, Automation.driver.findElement(By.xpath(controlName)));

					// WebHelper.fnHighlightMe(Automation.driver,Automation.driver.findElement(controlName));

				}
				break;

			case MouseClick:

				try {

					if (webElement.isDisplayed()) {

						Actions builderClick = new Actions(Automation.driver);
						Action clickAction = builderClick.moveToElement(webElement).clickAndHold().release().build();
						clickAction.perform();

					}

				} catch (Exception e) {
					log.error(e.getMessage(), e);
					System.out.println("Element does not exist");
				}

				break;

			case C:

				try {

					if (!ctrlValue.equalsIgnoreCase("null"))

						currentValue = webElement.getText();

					if (currentValue.contentEquals(ctrlValue)) {

						System.out.print("Expected" + currentValue + "Matches with " + "Actual" + ctrlValue);
					}

					else {
						System.out.print("Expected" + currentValue + "Not Matches with " + "Actual" + ctrlValue);
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);

					System.out.println("Element Not Found");
				}

				break;

			case JSScript:
				((JavascriptExecutor) Automation.driver).executeScript(controlName, ctrlValue);

				break;

			// bhaskar
			// case IJSScript:
			// IJavascriptExecutor ijs = Automation.driver;
			// bhaskar
			case WaitForPageToLoad:

				Automation.driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);

				WebDriverWait WaitForPageLoad = new WebDriverWait(Automation.driver, 500);

				WaitForPageLoad.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));

				break;
			
			case WaitForElementToVisible: // Mrinmayee - Wait until the
											// expected value is present on
											// screen

				String temp1 = Automation.driver.findElement(By.xpath(controlName)).getText();
				System.out.println(temp1);
				int x = 200;

				while (x > 0) {

					if (temp1.equals(ctrlValue)) {
						// System.out.println("Element is present");
						System.out.println("temp1 value is:-" + temp1);
						System.out.println("ctrlValue value is:-" + ctrlValue);
						break;
					} else {
						String temp2 = Automation.driver.findElement(By.xpath(controlName)).getText();
						temp1 = temp2;
						System.out.println("temp1 value is:-" + temp1);
						System.out.println("ctrlValue value is:-" + ctrlValue);
						Thread.sleep(2000);
					}
					x--;
				}

				break;

			case AttributeisPresent:

				WebHelper.wait.until(ExpectedConditions.attributeContains(By.xpath(controlName), "value", ctrlValue));
				break;
			case WaitUntilElementInvisible:

				try {

					WebHelper.wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(controlName)));

					System.out.println("Element is Invisible");
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					System.out.println("Element is visible");
				}

				break;

			case filedownload:

				FirefoxProfile profile = new FirefoxProfile();

				// Accept SSL certificate errors
				profile.setAcceptUntrustedCertificates(true);
				profile.setAssumeUntrustedCertificateIssuer(true);

				// Download a file in FF browser
				profile.setPreference("browser.download.folderList", 2);
				profile.setPreference("browser.helperApps.alwaysAsk.force", false);

				profile.setPreference("browser.download.dir", "D:\\Downloads");
				profile.setPreference("browser.download.defaultFolder", "D:\\Downloads");
				profile.setPreference("browser.download.manager.showWhenStarting", false);
				// Set MIME types
				profile.setPreference(
						"browser.helperApps.neverAsk.saveToDisk",
						"multipart/x-zip,application/zip,application/x-zip-compressed,application/x-compressed,application/msword,application/csv,text/csv,image/png ,image/jpeg, application/pdf, text/html,text/plain,  application/excel, application/vnd.ms-excel, application/x-excel, application/x-msexcel, application/octet-stream");

				// FirefoxDriver driver = new FirefoxDriver(profile);

				Thread.sleep(2000);

				// driver.findElement(By.id("download")).click();

				break;

			case PageRefresh:

				switch (actionName) { // Mrinmaye 05-12-2018
				case I:
					if (ctrlValue == null || ctrlValue.trim().equals("")) {
						break;
					}

					// Automation.driver.navigate().refresh();
					Automation.refreshPage(controlName);
					break;

				case NC:
					Automation.refreshPage(controlName);
					break;
				}
				break;

			case WaitForObjectPresent:

				WebHelperUtil.WebObjectPresent(webElement);

				break;

			case Wait:
				Thread.sleep(Integer.parseInt(controlName) * 1000);
				// Thread.sleep(500);
				break;
			case Wait_DM: //// Minaakshi : 05-02-2019
				//Thread.sleep(5000);
				Wait<WebDriver> dmWait = new FluentWait<WebDriver>(Automation.driver).withTimeout(30, TimeUnit.SECONDS)
				.pollingEvery(1, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
				
				if (controlName.equalsIgnoreCase("$value")){
					if (ctrlValue.equalsIgnoreCase("HIGH")){
						Thread.sleep(20000);
					}
					else if (ctrlValue.equalsIgnoreCase("MEDIUM")){
						Thread.sleep(10000);
					}
					else if (ctrlValue.equalsIgnoreCase("LOW")){
						Thread.sleep(5000);
					}
					else if (ctrlValue.equalsIgnoreCase("VERYLOW")){
						Thread.sleep(2000);
					}
					else{
						break;
					}
				}
				else
				{
					try{
						WebElement dmWaitWebElement = dmWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(controlName)));
						if (dmWaitWebElement.isEnabled()){
							Thread.sleep(5000);
							log.info("In Wait_DM : Element is enabled and applied hard wait of 5 seconds");}
						else{
							break;
						}
					
					}catch(Exception e){
						log.info("In Wait_DM: Catch : Element is not enabled and so not applied hard wait of 5 seconds");
						break;
					}
				}
				break;

			case Wait_IfValue: // Mrinmayee : 15-11-2018
				switch (actionName) {
				case I:
					if (!ctrlValue.isEmpty()) {
						Thread.sleep(Integer.parseInt(controlName) * 1000);
					}
					break;
				}
				break;
            case CompleteFormVariable:
				System.out.println("webElement is: " + controlName);
				List<WebElement> list = Automation.driver.findElements(By.xpath(controlName));
				
				System.out.println("incomplete form count is: " + list.size());
				
				for(WebElement form: list){
					
					//form.click();
					Thread.sleep(5000);
					Automation.driver.findElement(By.xpath("(//label[contains(text(),'Incomplete')]//following::*[text()='Variable'])[1]")).click();
					Thread.sleep(5000);
					Automation.driver.findElement(By.xpath("//*[contains(@id,'MFV_VAR_VALUE_1')]")).sendKeys("X");
					Thread.sleep(2000);
					Automation.driver.findElement(By.xpath("//input[@value='Ok']")).click();
					Thread.sleep(2000);
				}
				
				System.out.println("Number of incomplete forms are: " + list.size());
				break;
			case Video_Recorder:	
				switch(actionName)
				{
				case Start:
				//	if(!ctrlValue.isEmpty())
					//{
					String fileName = "TestExecution " + new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
						recorder = new ATUTestRecorder("TestRecording/", fileName, false);
						recorder.start();
						   System.out.println("Recording Started");
			//		}
					break;
					
				case Stop:
					recorder.stop();
					recorder = null;
					   System.out.println("Recording Stopped");
					break;
				}
				break;
					
			case D_Wait:	
				switch(actionName)
				{
				case I:
					if(!ctrlValue.isEmpty())
					{
						waitForVaadin();
						Thread.sleep(1500);
					}
					break;
					
				case NC:
					waitForVaadin();
					Thread.sleep(1500);
					break;
				}
				break;
				
				
			case RowNumbersToExecute:
				System.out.println(ctrlValue);
				break;

			case CheckBox:
				switch (actionName) {
				case I:
					if (ctrlValue == null || ctrlValue.trim().equals("")) {
						break;
					}
					if (Config.projectName.equals("DistributionManagement")) { // Minaakshi
																				// :
																				// 18-12-2018
						webElement.click();
					} else {
						int j = 4;
						while (j > 0) {

							if (ctrlValue.equalsIgnoreCase("Notdisplayed")) {
								break;
							}

							/*
							 * if(webElement==null){
							 * 
							 * break; }
							 */
							try {

								if (webElement.isDisplayed()) {

									webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);
									if (ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes")) {

										if (!webElement.isSelected()) {
											Coordinates coordinate = ((Locatable) webElement).getCoordinates();
											coordinate.onPage();
											coordinate.inViewPort();
											JavascriptExecutor executor = (JavascriptExecutor) Automation.driver;
											executor.executeScript("arguments[0].click();", webElement);
											// Thread.sleep(5000);
											// webElement.click();
											if (webElement.isSelected()) {
												// Automation.driver.manage().timeouts().pageLoadTimeout(60,TimeUnit.SECONDS);
												break;
											}
										} else {

											break;
										}
									} else {
										Thread.sleep(500);
										if (!webElement.isSelected()) {

											break;
										} else {
											// webElement =
											// getElementByType(controlId,
											// controlName,control,imageType,ctrlValue);

											JavascriptExecutor executor = (JavascriptExecutor) Automation.driver;
											executor.executeScript("arguments[0].click();", webElement);

											break;
										}
									}
								}
							}

							catch (Exception e) {
								log.error(e.getMessage(), e);
								try {
									Thread.sleep(500);
									webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);
									// webElement.click();
									break;
								} catch (Exception e1) {
									log.error(e1.getMessage(), e1);

									Thread.sleep(2000);
									System.out.println("Checkbox not found");
									j--;
								}

							}

						}
					}

					break;

				case NC:
					webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);
					webElement.click();
					break;

				case SetCheckbox:
					if (ctrlValue == null || ctrlValue.trim().equals("")) {
						break;
					}

					// ctrlValue = getCellData(logicalName,headerValues,
					// valuesRowIndex, valuesHeader);
					int i = 4;

					while (i > 0) {

						if (ctrlValue.equalsIgnoreCase("Notdisplayed")) {
							break;
						}

						/*
						 * if(webElement==null){
						 * 
						 * break; }
						 */
						try {

							if (webElement.isDisplayed()) {

								webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);
								if (ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes")) {

									if (!webElement.isSelected()) {
										Coordinates coordinate = ((Locatable) webElement).getCoordinates();
										coordinate.onPage();
										coordinate.inViewPort();
										JavascriptExecutor executor = (JavascriptExecutor) Automation.driver;
										executor.executeScript("arguments[0].click();", webElement);
										// Thread.sleep(5000);
										// webElement.click();
										if (webElement.isSelected()) {
											// Automation.driver.manage().timeouts().pageLoadTimeout(60,TimeUnit.SECONDS);
											break;
										}
									} else {

										break;
									}
								} else {
									Thread.sleep(500);
									if (!webElement.isSelected()) {

										break;
									} else {
										// webElement =
										// getElementByType(controlId,
										// controlName,control,imageType,ctrlValue);

										JavascriptExecutor executor = (JavascriptExecutor) Automation.driver;
										executor.executeScript("arguments[0].click();", webElement);

										break;
									}
								}
							}
						}

						catch (Exception e) {
							log.error(e.getMessage(), e);
							try {
								Thread.sleep(500);
								webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);
								// webElement.click();
								break;
							} catch (Exception e1) {

								Thread.sleep(2000);
								System.out.println("Checkbox not found");
								i--;
							}

						}

					}

				}
				break;
			case Radio:
				switch (actionName) {
				case I:
					if (ctrlValue == null || ctrlValue.trim().equals("")) {
						break;
					}

					if (!ctrlValue.isEmpty()) {
						webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);
						int i = 4;
						while (i > 1) {
							try {
								if (webElement.isDisplayed()) {

									if (!webElement.isSelected()) {

										webElement.click();
										break;
									}
									// Thread.sleep(1000);
									if (webElement.isSelected()) {

										break;
									}
								}

							} catch (Exception e) {
								log.error(e.getMessage(), e);
								try {
									Thread.sleep(2000);
									webElement.click();
									break;
								} catch (Exception e1) {
									Thread.sleep(2000);
									System.out.print("WebElement Not Found");
									i--;
								}
							}
						}
					}

					break;

				case SelectRadioButton:
					if (!ctrlValue.isEmpty()) {
						webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);
						int i = 4;
						while (i > 1) {
							try {
								if (webElement.isDisplayed()) {

									webElement.click();
									break;

								}

							} catch (Exception e) {
								log.error(e.getMessage(), e);
								try {
									Thread.sleep(2000);
									webElement.click();
									break;
								} catch (Exception e1) {
									Thread.sleep(2000);
									System.out.print("WebElement Not Found");
									i--;
								}
							}
						}
					}

					break;

				case NC:

					if (!ctrlValue.isEmpty()) {
						webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);
						int i = 4;
						while (i > 1) {
							try {
								if (webElement.isDisplayed()) {
									webElement.click();
									break;
								}

							} catch (Exception e) {
								log.error(e.getMessage(), e);
								try {
									Thread.sleep(2000);
									webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);
									webElement.click();
									break;
								} catch (Exception e1) {
									Thread.sleep(2000);
									System.out.print("WebElement Not Found");
									i--;
								}
							}
						}

					}
					break;
				case V:
					if (ctrlValue == null || ctrlValue.trim().equals("") || ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					if (webElement.isSelected()) {
						currentValue = webElement.getAttribute(controlName.toString());
					}
					break;
				case F:
					if (ctrlValue == null || ctrlValue.trim().equals("")) {
						break;
					}
					if (webElement != null) {
						currentValue = "Y";
					}
					break;
				}
				break;

			case WebLink:
				if (ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes") || !ctrlValue.equalsIgnoreCase(""))// Minaakshi
																															// :
																															// 03-10-2018
				{
					webElement.click();
				}
				break;
			case CloseWindow:// added this Case to bypass page loading after
								// clicking the event
				switch (actionName) {
				case Read:
					if (ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					uniqueNumber = WebHelperUtil.ReadFromExcel(ctrlValue, WebHelper.columnName);
					WebElementList = WebHelperUtil.getElementsByType(controlId, controlName, controlType, imageType, uniqueNumber);
					webElement = WebHelperUtil.GetControlByIndex("", WebElementList, controlId, controlName, controlType, uniqueNumber);
					webElement.click();
					break;
				case Write:
					if (ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					WebHelperUtil.writeToExcel(ctrlValue, webElement, controlId, controlType, controlName, rowNo, colNo);
					break;
				case I:
					if (ctrlValue == null || ctrlValue.trim().equals("")) {
						break;
					}
					if (controlId.equalsIgnoreCase("LinkValue")) {
						webElement.click();
					} else {
						if (ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes")) {
							webElement.click();
						}
					}
					break;
				case NC:
					webElement = getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);
					// webElement.click();
					Actions MousebuilderClick = new Actions(Automation.driver);
					Action MouseclickAction = MousebuilderClick.moveToElement(webElement).clickAndHold().release().build();
					MouseclickAction.perform();
					System.out.println("clicked");
					// MousebuilderClick
					// .keyDown(Keys.CONTROL).click(webElement).keyUp(Keys.CONTROL).build().perform();
					break;
				}
				break;

			case WaitForJS:
				waitForCondition();
				break;

			case ListBox:
			case ActionClick:
				switch (actionName) {
				case I:
					if (ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					if (ctrlValue == null || ctrlValue.trim().equals("")) {

						break;
					}
					try {
						if (!ctrlValue.equalsIgnoreCase("")) {
							//sheetal 2-7-2019
							((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();",webElement);
							Thread.sleep(500);
							
							Actions builderClick = new Actions(Automation.driver);
							highlightElement(webElement);
							Action clickAction = builderClick.moveToElement(webElement).clickAndHold().release().build();
							clickAction.perform();
														
						}
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						System.out.println("Element not exist");
					}
					break;

				case NC:
					Actions builderClick = new Actions(Automation.driver);
					highlightElement(webElement);
					Action clickAction = builderClick.moveToElement(webElement).clickAndHold().release().build();
					clickAction.perform();
					break;
				}
				break;
			case WebList:
				switch (actionName) {
				case Read:
					if (ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					uniqueNumber = WebHelperUtil.ReadFromExcel(ctrlValue, WebHelper.columnName);
					new Select(webElement).selectByVisibleText(uniqueNumber);
					break;
				case Write:
					if (ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					WebHelperUtil.writeToExcel(ctrlValue, webElement, controlId, controlType, controlName, rowNo, colNo);
					break;
				case I:
					if (ctrlValue == null || ctrlValue.trim().equals("")) {
						break;
					}
					System.out.println("inside bhaskar2");
					ExpectedCondition<Boolean> isTextPresent = CommonExpectedConditions.textToBePresentInElement(webElement, ctrlValue);
					if (isTextPresent != null) {
						if (webElement != null) {
							new Select(webElement).selectByVisibleText(ctrlValue);
						}
					}
					break;
				case V:
					if (ctrlValue == null || ctrlValue.trim().equals("") || ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					if (!ctrlValue.contains(",")) {
						currentValue = new Select(webElement).getFirstSelectedOption().getText();
						if (currentValue.isEmpty()) {
							currentValue = new Select(webElement).getFirstSelectedOption().getAttribute("value");
						}

						break;
					} else {
						currentValue = new String();
						List<WebElement> currentValues = new ArrayList<WebElement>();
						currentValues = new Select(webElement).getOptions();

						for (int j = 0; j < currentValues.size(); j++) {
							if (j + 1 == currentValues.size())
								currentValue = currentValue.concat(currentValues.get(j).getText());
							else {
								currentValue = currentValue.concat(currentValues.get(j).getText() + ",");
							}
						}
						break;
					}
				}
				break;

			// New code for AJAX Dropdown with dojo
			case AjaxWebList:
				switch (actionName) {
				case I:
					if (ctrlValue == null || ctrlValue.trim().equals("")) {
						break;
					}
					webElement.click();
					break;
				case VA:

					Thread.sleep(20000);
					currentValue = new String();
					List<WebElement> currentValues = new ArrayList<WebElement>();
					currentValues = Automation.driver.findElements(By.xpath(controlName));

					for (int j = 0; j < currentValues.size(); j++) {
						if (j + 1 == currentValues.size())
							currentValue = currentValue.concat(currentValues.get(j).getText());
						else {
							currentValue = currentValue.concat(currentValues.get(j).getText() + ",");
						}
					}
					break;

				}
				break;

			case IFrame:
				switch (actionName) { // Mrinmayee 05-12-2018
				case I:
					if (ctrlValue == null || ctrlValue.trim().equals("")) {
						break;
					}
					System.out.println("In method doaction debug4");
					Thread.sleep(5000);

					if (controlName.startsWith("//iframe")) {

						WebDriverWait wait1 = new WebDriverWait(Automation.driver, 700);

						wait1.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));

						Automation.driver.switchTo().frame(Automation.driver.findElement(By.xpath(controlName)));

					}

					else {

						Automation.driver.switchTo().frame(controlName);

					}

					System.out.println("In method doaction debug5");
					break;

				case NC:
					System.out.println("In method doaction debug4");
					Thread.sleep(5000);

					if (controlName.startsWith("//iframe")) {

						WebDriverWait wait1 = new WebDriverWait(Automation.driver, 700);

						wait1.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));

						Automation.driver.switchTo().frame(Automation.driver.findElement(By.xpath(controlName)));

					}

					else {

						Automation.driver.switchTo().frame(controlName);

					}

					System.out.println("In method doaction debug5");
					break;
				}
				break;

			case Browser:
			case URL:
				return WebHelper.doAction(null, null, null, imageType, controlType, controlId, controlName, ctrlValue, null, null, null, logicalName,
						action, webElement, Results, strucSheet, valSheet, rowIndex, rowcount, rowNo, colNo, null, null, null);

			case NewBrowser:
				// Thread.sleep(3000); //DS:Check if required
				String parentWindow = Automation.driver.getWindowHandle();
				Set<String> handles = Automation.driver.getWindowHandles();
				for (String windowHandle : handles) {
					if (!windowHandle.equals(parentWindow)) {
						Automation.driver.switchTo().window(windowHandle);
						// <!--Perform your operation here for new window-->
						// Automation.driver.close(); //closing child window
						// Automation.driver.switchTo().window(parentWindow);
						// //cntrl to parent window
					}
				}
				break;

			case CloseBrowser:

				Automation.driver.close();

				break;

			case CloseAndLaunchNewBrowser:

				Automation.driver.close();

				Automation.setUp();

				break;

			// case RowNumbers:
			// System.out.print("");

			case Menu:
				webElement.click();
				break;

			case Alert:
				switch (actionName) {
				case V:
					if (ctrlValue == null || ctrlValue.trim().equals("") || ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					Alert alert = Automation.driver.switchTo().alert();
					if (alert != null) {
						currentValue = alert.getText();
						System.out.println("Alert found on the web page");
						System.out.println(currentValue);
						alert.accept();

						Thread.sleep(5000);
					}
					break;
				case NC:
					// wait.until(ExpectedConditions.presenceOfElementLocated(alert()));
					Alert alert1 = Automation.driver.switchTo().alert();
					if (alert1 != null) {
						alert1.accept();
						Thread.sleep(2000);
					} else {

						System.out.print("Alert Not found");
					}
					break;

				case I: // Mrinmayee

					if (ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes")) {
						Alert alert2 = Automation.driver.switchTo().alert();
						if (alert2 != null) {
							alert2.accept();
							Thread.sleep(2000);
						} else {

							System.out.print("Alert Not found");
						}
					} else {
						break;
					}
					break;
				}
				break;

			case WebImage:
				webElement.sendKeys(Keys.TAB);
				webElement.click();
				Thread.sleep(5000);
				for (int Seconds = 0; Seconds <= Integer.parseInt(Config.timeOut); Seconds++) {
					if (!((Automation.driver.getWindowHandles().size()) > 1)) {
						webElement.click();
						Thread.sleep(5000);
					} else {
						// break;
					}
				}
				break;

			/*
			 * case ActionClick: Actions builderClick = new
			 * Actions(Automation.driver); Action clickAction = builderClick.
			 * moveToElement(webElement).clickAndHold().release().build();
			 * clickAction.perform(); break;
			 */

			case ActionDoubleClick:
				Actions builderdoubleClick = new Actions(Automation.driver);
				builderdoubleClick.doubleClick(webElement).build().perform();// TM-27/01/2015
																				// :-
																				// commented
																				// following
																				// code
																				// and
																				// used
																				// this
																				// code
																				// for
																				// simultaneous
																				// clicks
				// Action doubleClickAction =
				// builderdoubleClick.moveToElement(webElement).click().build();
				// doubleClickAction.perform();
				// doubleClickAction.perform();
				break;

			case ActionClickandEsc:
				Actions clickandEsc = new Actions(Automation.driver);
				Action clickEscAction = clickandEsc.moveToElement(webElement).click().sendKeys(Keys.ENTER, Keys.ESCAPE).build();
				clickEscAction.perform();
				break;

			case ActionMouseOver:
				Actions builderMouserOver = new Actions(Automation.driver);
				Action mouseOverAction = builderMouserOver.moveToElement(webElement).build();
				mouseOverAction.perform();
				break;

			case SwitchtoNewBrowser:

			case Calendar:
				// Thread.sleep(5000);
				Boolean isCalendarDisplayed = Automation.driver.switchTo().activeElement().isDisplayed();
				System.out.println(isCalendarDisplayed);
				if (isCalendarDisplayed == true) {
					String[] dtMthYr = ctrlValue.split("/");
					WebElement Year = WaitTool.waitForElement(Automation.driver, By.name("year"), Integer.parseInt(Config.timeOut));// Automation.driver.findElement(By.name("year"));
					while (!Year.getAttribute("value").equalsIgnoreCase(dtMthYr[2])) {
						if (Integer.parseInt(Year.getAttribute("value")) > Integer.parseInt(dtMthYr[2])) {
							WebElement yearButton = WaitTool.waitForElement(Automation.driver, By.id("button1"), Integer.parseInt(Config.timeOut));// Automation.driver.findElement(By.id("button1"));
							yearButton.click();
						} else if (Integer.parseInt(Year.getAttribute("value")) < Integer.parseInt(dtMthYr[2])) {
							WebElement yearButton = WaitTool.waitForElement(Automation.driver, By.id("Button5"), Integer.parseInt(Config.timeOut));// Automation.driver.findElement(By.id("Button5"));
							yearButton.click();
						}
					}
					Select date = new Select(WaitTool.waitForElement(Automation.driver, By.name("month"), Integer.parseInt(Config.timeOut)));
					WebHelper.month = CalendarSnippet.getMonthForInt(Integer.parseInt(dtMthYr[1]));
					date.selectByVisibleText(WebHelper.month);
					WebElement Day = WaitTool.waitForElement(Automation.driver, By.id("Button6"), Integer.parseInt(Config.timeOut));// Automation.driver.findElement(By.id("Button6"));
					int day = 6;
					while (Day.getAttribute("value") != null) {
						Day = WaitTool.waitForElement(Automation.driver, By.id("Button" + day), Integer.parseInt(Config.timeOut));// Automation.driver.findElement(By.id("Button"+day));
						if (Day.getAttribute("value").toString().equalsIgnoreCase(dtMthYr[0])) {
							Day.click();
							break;
						}
						day++;
					}
				} else {
					System.out.println("Calendar not Diplayed");
				}
				// Automation.selenium.click(controlName);
				break;

			case CalendarNew:
				isCalendarDisplayed = Automation.driver.switchTo().activeElement().isDisplayed();
				System.out.println(isCalendarDisplayed);
				if (isCalendarDisplayed == true) {

					String[] dtMthYr = ctrlValue.split("/");
					Thread.sleep(2000);
					// String[] CurrentDate =
					// dtFormat.format(frmDate).split("/");
					WebElement Monthyear = Automation.driver.findElement(By.xpath("//table/thead/tr/td[2]"));
					String Monthyear1 = Monthyear.getText();
					String[] Monthyear2 = Monthyear1.split(",");
					Monthyear2[1] = Monthyear2[1].trim();

					WebHelper.month = CalendarSnippet.getMonthForString(Monthyear2[0]);

					while (!Monthyear2[1].equalsIgnoreCase(dtMthYr[2])) {
						if (Integer.parseInt(Monthyear2[1]) > Integer.parseInt(dtMthYr[2])) {
							WebElement yearButton = Automation.driver.findElement(By.cssSelector("td:contains('«')"));
							yearButton.click();
							Monthyear2[1] = Integer.toString(Integer.parseInt(Monthyear2[1]) - 1);
						} else if (Integer.parseInt(Monthyear2[1]) < Integer.parseInt(dtMthYr[2])) {
							WebElement yearButton = Automation.driver.findElement(By.cssSelector("td:contains('»')"));
							yearButton.click();
							Monthyear2[1] = Integer.toString(Integer.parseInt(Monthyear2[1]) + 1);
						}
					}

					while (!WebHelper.month.equalsIgnoreCase(dtMthYr[1])) {
						if (Integer.parseInt(WebHelper.month) > Integer.parseInt(dtMthYr[1])) {
							WebElement monthButton = Automation.driver.findElement(By.cssSelector("td:contains('‹')"));
							monthButton.click();
							if (Integer.parseInt(WebHelper.month) < 11) {
								WebHelper.month = "0" + Integer.toString(Integer.parseInt(WebHelper.month) - 1);
							} else {
								WebHelper.month = Integer.toString(Integer.parseInt(WebHelper.month) - 1);
							}

						} else if (Integer.parseInt(WebHelper.month) < Integer.parseInt(dtMthYr[1])) {
							WebElement monthButton = Automation.driver.findElement(By.cssSelector("td:contains('›')"));
							monthButton.click();
							if (Integer.parseInt(WebHelper.month) < 9) {
								WebHelper.month = "0" + Integer.toString(Integer.parseInt(WebHelper.month) + 1);
							} else {
								WebHelper.month = Integer.toString(Integer.parseInt(WebHelper.month) + 1);
							}
						}
					}

					WebElement dateButton = Automation.driver.findElement(By.cssSelector("td.day:contains('" + dtMthYr[0] + "')"));
					System.out.println(dateButton);
					dateButton.click();

				} else {
					System.out.println("Calendar not Diplayed");
				}
				break;

			case CalendarIPF:
				String[] dtMthYr = ctrlValue.split("/");
				Thread.sleep(2000);
				String year = dtMthYr[2];
				String monthNum = dtMthYr[1];
				String day = dtMthYr[0];

				// Xpath for Year, mMnth & Days
				String xpathYear = "//div[@class='datepicker datepicker-dropdown dropdown-menu datepicker-orient-left datepicker-orient-bottom']/div[@class='datepicker-years']";
				String xpathMonth = "//div[@class='datepicker datepicker-dropdown dropdown-menu datepicker-orient-left datepicker-orient-bottom']/div[@class='datepicker-months']";
				String xpathDay = "//div[@class='datepicker datepicker-dropdown dropdown-menu datepicker-orient-left datepicker-orient-bottom']/div[@class='datepicker-days']";

				// Selecting year in 3 steps
				Automation.driver.findElement(By.xpath(xpathDay + "/table/thead/tr[1]/th[2]")).click();
				Automation.driver.findElement(By.xpath(xpathMonth + "/table/thead/tr/th[2]")).click();
				Automation.driver.findElement(By.xpath(xpathYear + "/table/tbody/tr/td/span[@class='year'][contains(text()," + year + ")]")).click();

				// Selecting month in 1 step
				Automation.driver.findElement(By.xpath(xpathMonth + "/table/tbody/tr/td/span[" + monthNum + "]")).click();

				// Selecting day in 1 step
				Automation.driver.findElement(By.xpath(xpathDay + "/table/tbody/tr/td[@class='day'][contains(text()," + day + ")]")).click();
				break;

			case CalendarEBP:
				String[] dtMthYrEBP = ctrlValue.split("/");
				Thread.sleep(2000);
				String yearEBP = dtMthYrEBP[2];
				String monthNumEBP = CalendarSnippet.getMonthForInt(Integer.parseInt(dtMthYrEBP[1])).substring(0, 3);
				String dayEBP = dtMthYrEBP[0];

				// common path used for most of the elements
				String pathToVisibleCalendar = "//div[@class='ajax__calendar'][contains(@style, 'visibility: visible;')]/div";

				// following is to click the title once to reach the year
				// page
				WebHelper.wait.until(
						ExpectedConditions.elementToBeClickable(By.xpath(pathToVisibleCalendar + "/div[@class='ajax__calendar_header']/div[3]/div")))
						.click();
				// check if 'Dec' is visibly clickable after refreshing
				WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(pathToVisibleCalendar
						+ "/div/div/table/tbody/tr/td/div[contains(text(), 'Dec')]")));
				// following is to click the title once again to reach the
				// year page
				Automation.driver.findElement(By.xpath(pathToVisibleCalendar + "/div[@class='ajax__calendar_header']/div[3]/div")).click();

				// common path used for most of the elements while selection
				// of year, month and date
				pathToVisibleCalendar = "//div[@class='ajax__calendar'][contains(@style, 'visibility: visible;')]/div/div/div/table/tbody/tr/td";

				// each of the following line selects the year, month and
				// date
				WebHelper.wait.until(
						ExpectedConditions.elementToBeClickable(By.xpath(pathToVisibleCalendar + "/div[contains(text()," + yearEBP + ")]"))).click();
				WebHelper.wait.until(
						ExpectedConditions.elementToBeClickable(By.xpath(pathToVisibleCalendar
								+ "/div[@class='ajax__calendar_month'][contains(text(),'" + monthNumEBP + "')]"))).click();
				WebHelper.wait.until(
						ExpectedConditions.elementToBeClickable(By.xpath(pathToVisibleCalendar + "/div[@class='ajax__calendar_day'][contains(text(),"
								+ dayEBP + ")]"))).click();

				break;

			/** Code for window popups **/
			case Window:
				switch (actionName) {
				case O:
					String parentHandle = Automation.driver.getWindowHandle();
					for (String winHandle : Automation.driver.getWindowHandles()) {
						Automation.driver.switchTo().window(winHandle);
						/*
						 * if (Automation.driver.getTitle().equalsIgnoreCase(
						 * controlName)) { Automation.driver.close(); }
						 */
					}
					// Automation.driver.switchTo().window(parentHandle);
					break;
				}
				break;

			case WebTable:
				switch (actionName) {
				case MultiTable:
					WebHelperUtil.handleWebTableAction(webElement);

				case Read:
					if (ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					WebHelperUtil.ReadFromExcel(ctrlValue, WebHelper.columnName);
					break;
				case Write:
					if (ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					WebHelperUtil.writeToExcel(ctrlValue, webElement, controlId, controlType, controlName, rowNo, colNo);
					break;
				case NC:
					WebElement table = webElement;
					List<WebElement> tableRows = table.findElements(By.tagName("tr"));
					int tableRowIndex = 0;
					// int tableColumnIndex = 0;
					boolean matchFound = false;
					for (WebElement tableRow : tableRows) {
						tableRowIndex += 1;
						List<WebElement> tableColumns = tableRow.findElements(By.tagName("td"));
						if (tableColumns.size() > 0) {
							for (WebElement tableColumn : tableColumns)
								if (tableColumn.getText().equals(ctrlValue)) {
									matchFound = true;
									System.out.println(tableRowIndex);
									List<Object> elementProperties = getPropertiesOfWebElement(tableColumns.get(Integer.parseInt(colNo)), imageType);
									controlName = elementProperties.get(0).toString();
									if (controlName.equals("")) {
										controlName = elementProperties.get(1).toString();
									}
									controlType = elementProperties.get(2).toString();
									webElement = (WebElement) elementProperties.get(3);
									doAction(imageType, controlType, controlId, controlName, ctrlValue, logicalName, action, webElement, Results,
											strucSheet, valSheet, tableRowIndex, rowcount, rowNo, colNo);
									break;
								}
							if (matchFound) {
								break;
							}
						}

					}
					break;

				case NCTable:

					int i;
					int j;
					WebElement Table = webElement;
					List<WebElement> TableRows = Table.findElements(By.tagName("tr"));
					for (i = 1; i <= TableRows.size(); i++) {
						List<WebElement> TableColumns = TableRows.get(i).findElements(By.tagName("td"));

						for (j = 0; j < TableColumns.size(); j++) {

							if (TableColumns.get(j).getText().matches(ctrlValue)) {

								TableColumns.get(j).click();

							}

						}
					}
					break;
				case V:
					if (ctrlValue == null || ctrlValue.trim().equals("") || ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					WriteToDetailResults(ctrlValue, "", logicalName);
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						log.error(e.getMessage(), e);
						e.printStackTrace();
					}
					break;
				case TableInput:
				case FIND:
					return WebHelper.doAction(null, null, null, imageType, controlType, controlId, controlName, ctrlValue, null, null, null,
							logicalName, action, webElement, Results, strucSheet, valSheet, rowIndex, rowcount, rowNo, colNo, null, null, null);
				}
				break;

			case Robot:

				if (ctrlValue != null && ctrlValue.trim().equals("IGNORE")) {// Minaakshi
																				// :
																				// 03-10-2018
					break;
				}

				if (controlName.equalsIgnoreCase("SetFilePath")) {
					// Automation.driver.switchTo().alert();
					// Robot robot=new Robot();
					StringSelection stringSelection = new StringSelection(ctrlValue);
					Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
					WebHelper.robot.delay(1000);
					WebHelper.robot.keyPress(KeyEvent.VK_CONTROL);
					WebHelper.robot.keyPress(KeyEvent.VK_V);
					D: // download
					WebHelper.robot.keyRelease(KeyEvent.VK_V);
					WebHelper.robot.keyRelease(KeyEvent.VK_CONTROL);

				} else if (controlName.equalsIgnoreCase("TAB")) {
					// webElement.sendKeys(Keys.TAB);
					// webElement.sendKeys(Keys.ENTER);
					// webElement.sendKeys(Keys.TAB);
					WebHelper.robot.keyPress(KeyEvent.VK_TAB);
					WebHelper.robot.keyRelease(KeyEvent.VK_TAB);
					// JSONResponse();

					// JavascriptExecutor js1 =
					// (JavascriptExecutor)Automation.driver;
					// js.executeScript("return document.readyState").toString().equals("complete");
				}

				else if (controlName.equalsIgnoreCase("SPACE")) {
					WebHelper.robot.keyPress(KeyEvent.VK_SPACE);
					WebHelper.robot.keyRelease(KeyEvent.VK_SPACE);
				} else if (controlName.equalsIgnoreCase("ENTER")) {

					Thread.sleep(3000);

					WebHelper.robot.keyPress(KeyEvent.VK_ENTER);
					WebHelper.robot.keyRelease(KeyEvent.VK_ENTER);

					Thread.sleep(3000);

				}
				break;

			case DB:
				switch (actionName) {
				case Write:
					if (ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					String policyNo = Automation.driver.findElement(By.xpath(controlName)).getText();
					ctrlValue = ctrlValue + "'" + policyNo + "'";
					ResultSet rs = null;
					Connection conn = JDBCConnection.establishDBConn();
					Statement st = conn.createStatement();
					rs = st.executeQuery(ctrlValue);
					
					rs.next();
					ctrlValue = String.valueOf(rs.getLong("COL_1"));
					rs.close();
					st.close();
					JDBCConnection.closeConnection(conn);
					WebHelperUtil.writeToExcel(ctrlValue, webElement, controlId, controlType, controlName, rowNo, colNo);
					break;
				case UpdateDBScript:
					System.out.println("logicalName----->" + logicalName);
					System.out.println("controlName----->" + controlName);

					Connection conn1 = JDBCConnection.establishPASDBConn();
					Statement st1 = conn1.createStatement();
					st1.execute(controlName);
					if(Config.databaseType.equalsIgnoreCase("ORACLE"))
					{
					    st1.execute("commit");
					}
					st1.close();
					JDBCConnection.closeConnection(conn1);
					break;
				}
				break;

			case Database:// Minaakshi : Case added to fetch Unique values
							// from Database
				switch (actionName) {
				case CaptureDataFromDB:
					if (logicalName.equalsIgnoreCase("SQLQuery"))
						sqlQuery = ctrlValue;
					else if (logicalName.equalsIgnoreCase("ReadFromColName"))
						readFromColName = ctrlValue;
					else if (logicalName.equalsIgnoreCase("ColumnToBeFetchedFromDB"))
						toBeFetchedDBColName = ctrlValue;
					else if (logicalName.equalsIgnoreCase("WriteToColName")) {
						writeToColName = ctrlValue;
						if (!readFromColName.equalsIgnoreCase("")) {// Minaakshi
																	// :
																	// 03-10-2018
							String tempValue = DMProduct.ReadFromExcelUsingColumnName("", readFromColName);
							sqlQuery = DMProduct.getDataFetchingSQLQuery(sqlQuery, tempValue);
						}// Minaakshi : 20-09-2018

						ResultSet rs2;
						Connection conn2 = JDBCConnection.establishPASDBConn();
						Statement st2 = conn2.createStatement();
						rs2 = st2.executeQuery(sqlQuery);
						rs2.next();

						ctrlValue = String.valueOf(rs2.getString(toBeFetchedDBColName));

						if (rs2.next()) {
							String tempctrlValue = String.valueOf(rs2.getString(toBeFetchedDBColName));
							ctrlValue = ctrlValue + "," + tempctrlValue;
						}

						rs2.close();
						st2.close();
						JDBCConnection.closeConnection(conn2);

						DMProduct.writeDataToUniqueNumberSheet(ctrlValue, writeToColName);
					}

					break;
				}
				break;

			case CreateDynamicData:// Minaakshi : 03-10-2018
				Jacob.main(ctrlValue, "!GenerateData_Click");
				// DynamicDataGeneration.xlsm!GenerateData_Click
				break;

			case WaitForEC:
				WebHelper.wait.until(CommonExpectedConditions.elementToBeClickable(webElement));
				break;
			// bhaskar
			// case SikuliRun:
			// App.open(controlName);
			case SikuliScreen:
				App.open(WebHelper.sikscreen);
				break;
			// bhaskar
			case SikuliType:
				System.out.println("in sikulitype");
				System.out.println("controlName is:" + controlName);
				Pattern image1 = new Pattern(controlName);
				sikuliScreen.type(image1, ctrlValue);
				break;

			case SikuliButton:
				System.out.println("in sikuliButton");
				System.out.println("controlName is:" + controlName);
				Pattern image2 = new Pattern(controlName);
				sikuliScreen.click(image2);
				System.out.println("Done");
				break;
			// bhaskar
			case Slider:
				WebElement slider = Automation.driver.findElement(By.xpath(controlName));
				Thread.sleep(3000);
				Actions moveSlider = new Actions(Automation.driver);
				Action actionslider = moveSlider.dragAndDropBy(slider, 30, 0).build();
				actionslider.perform();
				break;
			// bhaskar

			// bhaskar
			case ifExist:

				try {

					if (Automation.driver.findElement(By.xpath(controlName)).isDisplayed()) {

						webElement.clear();
						webElement.click();
						webElement.sendKeys(ctrlValue);

					}

				} catch (Exception e) {
					log.error(e.getMessage(), e);
					System.out.println("Field does not Exist");
				}

				break;

			case MaskedInputDate:
				if (!ctrlValue.equalsIgnoreCase("null")) // changed by asif
				{

					webElement.sendKeys(Keys.ENTER);

					js.executeScript("arguments[0].value = '';", webElement);

					webElement.sendKeys(ctrlValue);

				} else {
					System.out.println("Don't Perform any action");
				}
				break;

			case Date:
				Calendar cal = new GregorianCalendar();
				int i = cal.get(Calendar.DAY_OF_MONTH);
				if (i >= 31) {
					i = i - 10;
				}
				break;

			case FileUpload:
				webElement.sendKeys(ctrlValue);
				break;

			case ScrollTo:
				if (Config.projectName.equals("DistributionManagement")) {// Minaakshi
					switch (actionName) {
					case I:
						if (ctrlValue == null || ctrlValue.trim().equals("")) {
							break;
						}
						if (ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes")) {
							((JavascriptExecutor) Automation.driver).executeScript("arguments[0].scrollIntoView();", webElement);
						}
					}
					break;
				} else {
					try {
						if (webElement.isDisplayed()) {
							Coordinates coordinate = ((Locatable) webElement).getCoordinates();
							coordinate.onPage();
							coordinate.inViewPort();
						}
					} catch (Exception e) {
						log.error(e.getMessage(), e);
						log.info("Element does not exist");
					}
				}

				break;
			
			case ScrollToElement: //Sheetal-2-5-2019. Had to create new due to project specific code in 'ScrollTo'
				switch(actionName)
				{
				case I:
					if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes") || !ctrlValue.equalsIgnoreCase(""))
					{
						((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();",webElement);
					}
					break;
				case NC:
					((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();",webElement);
					break;
				
				case NCIF:
					try{	
						if(webElement.isDisplayed()==true){						
							((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();",webElement);
							break;
						} 
					}catch(Exception e){

						System.out.println("Element Not Found on Page.");
					}
					break;
				}
				break;
							
			case WebService:
				switch (actionName) {
				case I:
					if (ctrlValue == null || ctrlValue.trim().equals("")) {
						break;
					}
					if (logicalName.equalsIgnoreCase("WSDL_URL"))
						WebHelper.wsdl_url = ctrlValue;
					else if (logicalName.equalsIgnoreCase("REQUEST_URL"))
						WebHelper.request_url = ctrlValue;
					else if (logicalName.equalsIgnoreCase("REQUEST_XML"))
						WebHelper.request_xml = ctrlValue;
					break;

				case T:
					if (ctrlValue == null || ctrlValue.trim().equals("")) {
						break;
					}
					WebService.callWebService();
					break;

				case V:
					if (ctrlValue == null || ctrlValue.trim().equals("") || ctrlValue.equalsIgnoreCase("IGNORE")) {
						break;
					}
					currentValue = WebService.getXMLTagValue(controlName);
					break;
				}
				break;
			// Added by Rajesh M to calling other framework jar from Policy
			// automation framework
			case ExecuteJar:
				switch (actionName) {
				case Billing:
					log.info("logicalName----->" + logicalName);
					log.info("controlName----->" + controlName);
					if (controlName != null) {
						WebHelperUtil.executeJar(controlName);
					} else {
						log.info("logicalName should not be blank or null");
					}
					break;
				}
				break;

			case SaveDocument:
				String[] temp=ctrlValue.split(";");
				
				String downloadedFileName2 = Config.actualPdfDownloadPath + "\\" + temp[0];
				File file1 = new File(downloadedFileName2);

				if (file1.exists()) {
					file1.delete();
					System.out.println("Old file DocumentPackaging.pdf deleted successfully");
				}
				
				//Below click downloads the document
				Actions MousebuilderClick1 = new Actions(Automation.driver);
				highlightElement(webElement);
				Action MouseclickAction1 = MousebuilderClick1.moveToElement(webElement).clickAndHold().release().build();
				MouseclickAction1.perform();

				break;

			case RenameDocument:

				String mainWindow = Automation.driver.getWindowHandle();
				
				String temp2[]=ctrlValue.split(";");
			
				String downloadedFileName = Config.actualPdfDownloadPath + "\\" + temp2[0];
				String renameFileNameTo = Config.actualPdfDownloadPath + "\\" + temp2[1];

				File oldFile = new File(downloadedFileName);
				File newFile = new File(renameFileNameTo);

				if (newFile.exists()) {
					newFile.delete();
					System.out.println("Old file " + temp2[1] + " deleted successfully");
				}

				oldFile.renameTo(newFile);

				String timeStampForFileBackup = new SimpleDateFormat("ddMMMMyyyy_HH_mm_ss").format(new Date());
				String backUpFileName = renameFileNameTo.replace(".pdf", "") + "_" + timeStampForFileBackup + ".pdf";

				File backUpFile = new File(backUpFileName);

				Files.copy(newFile, backUpFile);

				Set<String> handlers1 = null;
				handlers1 = Automation.driver.getWindowHandles();
				for (String handler2 : handlers1) {
					if (!mainWindow.equalsIgnoreCase(handler2)) {
						Automation.driver.switchTo().window(handler2);
						Automation.driver.close();
					}
				}
				Automation.driver.switchTo().window(mainWindow);
				break;

				
			default:
				log.info("U r in Default");
				break;
			}
		} catch (WebDriverException we) {
			log.error(we.getMessage(), we);
			throw new Exception("Error Occurred from Do Action " + controlName + we.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new Exception(e.getMessage());
		}

		// Mrinmayee - skip the step for which control value is blank end

		// TM-02/02/2015: Radio button found ("F") & AJAX control ("VA")
		if ((action.equalsIgnoreCase("V") || action.equalsIgnoreCase("F") || action.equalsIgnoreCase("PopUpCount")
				|| action.equalsIgnoreCase("VerifyPopUpElement") || action.equalsIgnoreCase("VA") || action.toString().equalsIgnoreCase("VD") || action.toString().equalsIgnoreCase("V_text") || action.toString().equalsIgnoreCase("V_availabilityStatus") || action.toString().equalsIgnoreCase("VFCount") || action.toString().equalsIgnoreCase("VFPresence") || action.toString().equalsIgnoreCase("V_checkboxStatus") || action.toString().equalsIgnoreCase("V_edit") || action.toString().equalsIgnoreCase("V_disableStatus"))
				&& !ctrlValue.equalsIgnoreCase("")
				&& !ctrlValue.equalsIgnoreCase("IGNORE"))																										// :
			{																														// 03-10-2018
			if (Results == true) {
				webDriver.setReport(WriteToDetailResults(ctrlValue, currentValue, logicalName));
			}
		}

		return currentValue;
	}


	/** This Functions Waits for the HTMLPage To Load **/
	public static Boolean waitForCondition() throws IOException {
		ExpectedCondition<Boolean> expCondition = null;

		try {
			// wait = new
			// WebDriverWait(Automation.driver,Integer.parseInt(Automation.timeOut.toString()));//Integer.parseInt(Automation.timeOut.toString())
			expCondition = new ExpectedCondition<Boolean>() {

				@Override
				public Boolean apply(WebDriver driver) {
					return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
				}
			};
		} catch (WebDriverException e) {
			log.error(e.getMessage(), e);
			controller.pauseFun("From PageLoaded Function" + e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			controller.pauseFun("Timed Out after waiting");
		}
		@SuppressWarnings("unused")
		WebDriverWait wait1 = new WebDriverWait(Automation.driver, 30);
		return WebHelper.wait.until(expCondition);
	}

	public static List<Object> getPropertiesOfWebElement(WebElement webElement, String imageType) {
		List<WebElement> elements = webElement.findElements(By.tagName(imageType));
		WebElement element = elements.get(0);
		List<Object> elementProperties = new ArrayList<Object>();
		String elementType = element.getAttribute("type");
		String elementTagName = element.getTagName();
		// String elementClassName = element.getClass().toString();
		String action = "";
		String controlType = "";
		String id = "";
		String name = "";
		String controlName = "";
		String controlID = "";
		if (elementType.equals("text") && elementTagName.equals("input")) {
			id = element.getAttribute("id");
			name = element.getAttribute("name");
			controlType = "WebEdit";
			controlID = "Id";
			// controlName="//"+elementTagName+"[@id="+id+"]";
			controlName = id;

			// action="I";

		} else if (elementType.contains("checkbox") && elementTagName.equals("input")) {
			id = element.getAttribute("id");
			name = element.getAttribute("name");
			controlType = "CheckBox";
		} else if (elementType.contains("listbox") && elementTagName.equals("select")) {
			id = element.getAttribute("id");
			name = element.getAttribute("name");
			controlType = "WebList";
		} else if (elementType.contains("radio") && elementTagName.equals("input")) {
			id = element.getAttribute("id");
			name = element.getAttribute("name");
			controlType = "Radio";
		}

		else if (elementType.contains("") && elementTagName.equals("a")) {
			id = element.getAttribute("id");
			name = element.getAttribute("name");
			controlType = "WebLink";
		} else if (elementType.contains("") && elementTagName.equals("")) {
			id = element.getAttribute("id");
			name = element.getAttribute("name");
			controlType = "WebEdit";
			controlID = "Id";
			controlName = id;

		}
		elementProperties.add(action);
		elementProperties.add(id);
		elementProperties.add(name);
		elementProperties.add(controlName);
		elementProperties.add(controlID);
		elementProperties.add(controlType);
		elementProperties.add((Object) element);

		for (int i = 0; i < 5; i++) {
			System.out.println(elementProperties.get(i).toString());
		}
		return elementProperties;
	}

	public static void saveScreenShot() {
		if (!(Automation.driver instanceof TakesScreenshot)) {

			System.out.println("Not able to take screenshot: Current WebDriver does not support TakesScreenshot interface.");
			return;
		}

		File scrFile;
		try {

			scrFile = ((TakesScreenshot) Automation.driver).getScreenshotAs(OutputType.FILE);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			System.out.println("Taking screenshot failed for: " + webDriver.getReport().getTestcaseId());
			// e.printStackTrace();
			return;
		}
		String date = null;

		if (StringUtils.isNotBlank(webDriver.getReport().getFromDate()))
			date = webDriver.getReport().getFromDate().replaceAll("[-/: ]", "");
		else
			webDriver.getReport().setFromDate(Config.dtFormat.format(new Date()));

		String fileName = webDriver.getReport().getTestcaseId() + "_" + webDriver.getReport().getTrasactionType() + "_" + date;
		// TM:19/01/2015 - Changes made to save screenshots in jpeg format
		// rather that png since they are heavier
		// String location = System.getProperty("user.dir")
		// +"\\Resources\\Results\\ScreenShots\\"+ fileName+".jpeg";
		String location = Config.resultFilePath + "\\ScreenShots\\" + fileName + ".jpeg";
		// bhaskar
		controller.testDesciption = location;
		// bhaskar
		webDriver.getReport().setScreenShot("file:\\\\" + location);

		try {

			FileUtils.copyFile(scrFile, new File(location));
			controller.FailScreen=new File(location).getAbsolutePath(); //Sheetal: 2/20/2019
			
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
			return;
		}

	}

	public static void JSONResponse() {

		try {

			for (int i = 0; i < 2; i++) {

				Thread.sleep(50);

				// long start = System.currentTimeMillis();
				// URL obj = new
				// URL(Automation.driver.getCurrentUrl().toString());
				// URL obj=new URL("https://127.0.0.1") ;
				URL obj = new URL("https://mic99.cover-all.com/mic/pctv2/pctentrypoint/");
				HttpsURLConnection conn = (HttpsURLConnection) obj.openConnection();

				// getFullURL(ServletRequest request);
				conn.setRequestMethod("POST");

				conn.connect();

				System.out.println(conn.getConnectTimeout());

				int header = conn.getResponseCode();

				switch (conn.getResponseCode()) {
				case HttpsURLConnection.HTTP_OK:
					System.out.println("200OK");

					// Thread.sleep(200);

					break; // fine, go on
				case HttpsURLConnection.HTTP_GATEWAY_TIMEOUT:
					System.out.print("TimOut");
					break;// retry
				case HttpsURLConnection.HTTP_UNAVAILABLE:
					System.out.print("Unavailable");
					break;// retry, server is unstable
				default:
					System.out.print("UnknownResposnecode");
					break; // abort
				}

			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}

	}

	/**
	 * Auth : DW Date : 02-Jan-2018 This function is used to highlight element
	 * on the html screen
	 **/
	public static void highlightElement(WebElement element) {
		try {
			((JavascriptExecutor) Automation.driver).executeScript("arguments[0].setAttribute('style', arguments[1]);", element,
					"border: 3px groove lime;");
			Thread.sleep(300);
			((JavascriptExecutor) Automation.driver).executeScript("arguments[0].setAttribute('style', arguments[1]);", element, "");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static void waitForVaadin() 
	{
		StringBuilder commandBuilder = new StringBuilder(500);
	    commandBuilder.append("if (window.vaadin == null) { return 4; }");
	    commandBuilder.append("var clients = window.vaadin.clients;");
	    commandBuilder.append("if (!clients) { return 3; }");
	    commandBuilder.append("for (var key in clients) {");
	    commandBuilder.append(" var client = clients[key];");
	    commandBuilder.append(" if (client.getElementsByPath == undefined) { return 2; }");
	    commandBuilder.append(" else if (client.isActive()) { return 1; } }");
	    commandBuilder.append("return 0;");
	    String command = commandBuilder.toString();
	    long startTime = System.currentTimeMillis();
	    long maxTime = startTime + 120000;
	    JavascriptExecutor js = (JavascriptExecutor) Automation.driver;
	    long errorCode = -1;
	    while (System.currentTimeMillis() < maxTime && errorCode != 0)
	    {
	        errorCode = (Long) js.executeScript(command);
	    }
	    if (errorCode != 0)
	    {
	    	System.out.println("Timeout after waiting for Vaadin call to finish.");
	    }
	}	

}
