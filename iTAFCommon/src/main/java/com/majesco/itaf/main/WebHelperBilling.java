package com.majesco.itaf.main;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.Point;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.sikuli.script.Screen;

import com.majesco.itaf.recovery.StartRecovery;
import com.majesco.itaf.util.BillingProduct;
import com.majesco.itaf.util.Constants;
import com.majesco.itaf.util.ExcelUtility;
import com.majesco.itaf.verification.WebVerification;
import com.majesco.itaf.vo.Reporter;

public class WebHelperBilling {

	final static Logger log = Logger.getLogger(WebHelperBilling.class.getName());
	public static Reporter report = new Reporter();
	public static File FlatFile;// Mandar
	public static Cell eftTransaction = null;
	public static Cell WebservicecycleDate = null;
	public static String eftFlag = null; // mandar
	public static String wscycledate, NodeName, ValueToBeCompared; // Mandar
	public static Boolean blank = false;
	public static boolean recovery_done = false;
	public static Sheet MainControllerSheet = null;
	public static Boolean colnotfound = false;
	// bhaskar FIND Action START
	public static HashMap<String, Object> vColumnheaderIndex = new HashMap<String, Object>();
	public static HashMap<String, Object> vColumnheaderValues = new HashMap<String, Object>();

	public static Date toDate = null;
	public static String testCase = null;
	public static Wait<WebDriver> waitForElementPresence;
	public static Screen sikuliScreen = null;
	public static List<String> searchValue1 = null;
	public static Boolean pageLoaded = false;
	// meghna - For FlatFile Validation--04/12/2017
	public static String stransactionType = "";

	static ITAFWebDriver webDriver = ITAFWebDriver.getInstance();
	static MainController controller = ObjectFactory.getMainController();

	static void implementWait() {

		// ***below given wait commented for basebilling, need to
		// verify***--Mandar
		// wait = new
		// WebDriverWait(currentdriver,Integer.parseInt(Automation.configHashMap.get("TIMEOUT").toString()));

		// For handling Object level issue 01 June
		WebHelper.wait = new FluentWait<WebDriver>(WebHelper.currentdriver).withTimeout(Integer.parseInt(Config.timeOut), TimeUnit.SECONDS)
				.pollingEvery(5, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);

		// Meghna//

		waitForElementPresence = new FluentWait<WebDriver>(WebHelper.currentdriver).withTimeout(30, TimeUnit.SECONDS)
				.pollingEvery(5, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);
		//
	}

	public static void calldoAction(Sheet headerValues, String logicalName, Row rowValues, String TransactionType, int valuesRowIndex, String action,
			String controlName, Sheet sheetStructure, int rowIndex, String TestCaseID, String controltype, String controlID, String indexVal,
			String imageType, String FilePath, int rowCount, String rowNo, String colNo, String operationType) throws Exception {
		String ctrlValue1 = null;
		String ctrlValue2 = null;
		String ctrlValue = null;
		String cycleDate = null;
		WebElement webElement = null;

		List<WebElement> controlList = null;
		colnotfound = false;
		// boolean isControlValueFound =false;
		if (WebHelper.valuesHeader.isEmpty() == true) {
			WebHelper.valuesHeader = WebHelperUtil.getValueFromHashMap(headerValues);
		}
		Object actualValue = null;

		if (logicalName != null) {
			actualValue = WebHelper.valuesHeader.get(logicalName.toString());
		}// headerRow.getCell(colIndex);

		if (actualValue == null) {
			colnotfound = true; // log.info("Null");
		}

		WebHelper.testcaseID = rowValues.getCell(Integer.parseInt(WebHelper.valuesHeader.get("TestCaseID").toString()));
		WebHelper.cycleDate_Values2 = rowValues.getCell(Integer.parseInt(WebHelper.valuesHeader.get("CycleDate").toString()));
		if (WebHelper.testcaseID == null) {
			testCase = "";
		} else {
			testCase = WebHelper.testcaseID.toString();
		}
		WebHelper.transactionType = rowValues.getCell(Integer.parseInt(WebHelper.valuesHeader.get("TransactionType").toString()));
		stransactionType = TransactionType.toString();

		// Meghna - For FlatFile Validation--04/12/2014
		if (stransactionType.toString().startsWith("FlatFile") || stransactionType.toString().contains("Outbound")) {
			log.info("transaction is FlatFile");
			WebHelper.ctrlValue1Cell = rowValues.getCell(Integer.parseInt(WebHelper.valuesHeader.get("ValidateTag").toString()));
			WebHelper.ctrlValue2Cell = rowValues.getCell(Integer.parseInt(WebHelper.valuesHeader.get("ValidationMsg").toString()));
		}
		// Meghna - For FlatFile Validation--04/12/2014

		if (stransactionType.toString().startsWith("WebService"))// devishree
		{
			log.info("transaction is webservice");// Mandar
			// Mandar----
			WebHelper.ctrlValue1Cell = rowValues.getCell(Integer.parseInt(WebHelper.valuesHeader.get("PathToNode").toString()));
			WebHelper.ctrlValue2Cell = rowValues.getCell(Integer.parseInt(WebHelper.valuesHeader.get("ColumnName").toString()));
			eftTransaction = rowValues.getCell(Integer.parseInt(WebHelper.valuesHeader.get("WebServiceName").toString()));
			eftFlag = eftTransaction.toString();
			// -------------------------------------//
			// --Mandar
			WebservicecycleDate = rowValues.getCell(Integer.parseInt(WebHelper.valuesHeader.get("CycleDate").toString()));
			if (WebHelper.ctrlValue1Cell != null && WebHelper.ctrlValue2Cell != null) {
				ctrlValue1 = WebHelper.ctrlValue1Cell.toString();
				ctrlValue2 = WebHelper.ctrlValue2Cell.toString();
			} else {
				ctrlValue1 = "";
				ctrlValue2 = "";
			}
			log.info("transaction is webservice");
			wscycledate = WebservicecycleDate.toString();
			SimpleDateFormat cdFormat = new SimpleDateFormat("dd-MMM-yyyy");
			DateFormat cycleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
			if (wscycledate.contains("-")) {
				Date CycleDate_Values = cdFormat.parse(wscycledate);
				wscycledate = cycleDateFormat.format(CycleDate_Values);
			}
		}

		// *** Below code added by Amol Gavali
		if (stransactionType.startsWith("Rest")) {
			log.info("This is a REST service.");
		}
		// ***

		if (stransactionType.toString().equalsIgnoreCase("ChangeBusinessDate")) {
			ctrlValue = controller.businessDateValue;
		} else {
			if (colnotfound == false) {
				ctrlValue = WebHelperUtil.getCellData(logicalName, headerValues, valuesRowIndex, WebHelper.valuesHeader);
			} else {
				ctrlValue = "";
			}
			cycleDate = WebHelperUtil.getCellData("CycleDate", headerValues, valuesRowIndex, WebHelper.valuesHeader);
		}

		// bhaskar remove leading and trailing whitespaces from values sheet
		// data START
		// log.info("regex start");//Mandar
		Pattern trimregex = Pattern.compile("^\\s+|\\s+$");
		Matcher match = trimregex.matcher(ctrlValue);
		StringBuffer ctrlValue_output = new StringBuffer();
		while (match.find())
			match.appendReplacement(ctrlValue_output, "");
		match.appendTail(ctrlValue_output);
		// log.info(ctrlValue_output);
		// log.info("regex ends");//Mandar
		// bhaskar remove leading and trailing whitespaces from values sheet
		// data END

		// bhaskar Action CAPTURE keyword START
		if (action.equalsIgnoreCase("Capture")) {
			// Reporter report = new Reporter();
			log.info("Inside Capture Case");
			controlName = WebHelperUtil.getCellData("ControlName", sheetStructure, rowIndex, WebHelper.structureHeader);
			logicalName = WebHelperUtil.getCellData("LogicalName", sheetStructure, rowIndex, WebHelper.structureHeader);
			if (ctrlValue.equalsIgnoreCase("Y")) {
				log.info("CYCLEDATE is :" + cycleDate);
				TransactionMapping.TransactionCaptureData(cycleDate, TestCaseID, controlName, Config.transactionInputFilePath);
			}
		}
		// bhaskar Action CAPTURE keyword END

		WebHelper.inputValue = ctrlValue; // Meghna-For UI
											// Validation--01/12/2017
		// Pagination//
		if (((action.equals("I") && !StringUtils.isEmpty(ctrlValue)) || (action.equals("V") && !StringUtils.isEmpty(ctrlValue)) || !action
				.equals("I"))
				&& !action.equalsIgnoreCase("Capture")
				&& !action.equalsIgnoreCase("FIND")
				&& !action.equalsIgnoreCase("TABLEINPUT")
				|| action.equals("PGN") && !StringUtils.isEmpty(ctrlValue)) {
			if (logicalName.equalsIgnoreCase("WAIT")) {
				log.info(action + " " + logicalName + " For " + controlName);
			} else {
				log.info(action + " On " + controltype + " " + logicalName);
			}

			if (logicalName.equalsIgnoreCase("CreateBatch")) {
				System.out.println("wait");
			}

			if (!controltype.startsWith("Sikuli")) {
				if (!action.equalsIgnoreCase("LOOP")
						&& !controltype.equalsIgnoreCase("Wait")
						&& !action.equalsIgnoreCase("END_LOOP")
						&& !controltype.equalsIgnoreCase("Browser")
						&& !controltype.equalsIgnoreCase("Window")
						&& !controltype.equalsIgnoreCase("Alert")
						&& !controltype.equalsIgnoreCase("URL")
						&& !controltype.equalsIgnoreCase("WaitForJS")
						&& !controltype.contains("Robot")
						&& !controltype.equalsIgnoreCase("Calendar")
						&& !controltype.equalsIgnoreCase("CalendarNew")
						&& !controltype.equalsIgnoreCase("CalendarIPF")
						&& !controltype.equalsIgnoreCase("CalendarEBP")
						&&
						// (!action.equalsIgnoreCase("Read")||((action.equalsIgnoreCase("Read")&&
						// !controlName.isEmpty())))&&
						(!action.equalsIgnoreCase("Read") || ((action.equalsIgnoreCase("Read") && !StringUtils.isEmpty(controlName))))
						&& !controltype.equalsIgnoreCase("JSScript")
						&& !controltype.equalsIgnoreCase("DB")
						&& !controlID.equalsIgnoreCase("XML")
						&& !controltype.startsWith("Process")
						&& !controltype.startsWith("Destroy")
						&& !controltype.startsWith("ReadSikuli")
						&& !controltype.equalsIgnoreCase("WebService")
						&& !action.equalsIgnoreCase("VA")
						&& !action.equalsIgnoreCase("FileCompare")
						&& !controltype.equalsIgnoreCase("Screenshot")// devishree
						&& !controltype.equalsIgnoreCase("WebService1") && !controltype.equalsIgnoreCase("WebService2")
						&& !controltype.equalsIgnoreCase("WebService3") && !controltype.equalsIgnoreCase("WebServiceV")
						&& !controltype.equalsIgnoreCase("WebServiceC") && !controltype.equalsIgnoreCase("WebServiceRP")
						&& !controltype.equalsIgnoreCase("WebServiceV1") && !controltype.equalsIgnoreCase("WebServiceV2")
						&& !controltype.equalsIgnoreCase("WebServiceVAG") && !controltype.equalsIgnoreCase("WebServiceV3")
						&& !controltype.equalsIgnoreCase("OutPutForm") && !controltype.equalsIgnoreCase("CopyFlatFile")
						&& !controltype.equalsIgnoreCase("WebServiceCSI") && !controltype.equalsIgnoreCase("WebService_CheckUpdate")
						&& !controltype.equalsIgnoreCase("WebService_VoidRef") && !controltype.equalsIgnoreCase("CovertToXml")
						&& !controltype.equalsIgnoreCase("Pagination")) // devishree//Mandar
																		// --
																		// CopyfaltFile----//Meghna-WebserviceCSI
																		// &
																		// ConvertToXml--04/12/2017
				{
					// bhaskar Supressing exception when element not found START
					if ((indexVal.equalsIgnoreCase("") || indexVal.equalsIgnoreCase("0")) && !controlID.equalsIgnoreCase("TagValue")
							&& !controlID.equalsIgnoreCase("TagText") && !action.equalsIgnoreCase("NoException") && !action.equalsIgnoreCase("FIND")
							&& !action.equalsIgnoreCase("TABLEINPUT") && !action.equalsIgnoreCase("IVV") && !action.equalsIgnoreCase("WMSG"))// Mandar
																																				// --
																																				// for
																																				// IVV/WMSG

					// bhaskar Supressing exception when element not found END
					{
						try {

							if (controlName.contains("+")) {
								controlName = controlName.replace("+", ctrlValue);
							}
							// Mandar --- Below given code not used for GP
							// Billing
							for (int i = 0; i < 25; i++) // For handling Object
															// level issue 01
															// June
							{
								if (((JavascriptExecutor) WebHelper.currentdriver).executeScript("return document.readyState").toString()
										.equals("complete")) {
									break;
								} else {
									Thread.sleep(1000);
									// Automation.driver.manage().timeouts().pageLoadTimeout(120,
									// TimeUnit.SECONDS);
								}
							} // For handling Object level issue 01 June

							if ((action.equalsIgnoreCase("VV") || action.equalsIgnoreCase("I") || action.equalsIgnoreCase("PGN"))
									&& ctrlValue.equals("")) {
								/*
								 * if(ctrlValue.equals("")) {
								 */
								WebHelper.webElementForROBOT = null;
								// }

							}

							else {
								webElement = getElementByType(controlID, controlName, controltype, imageType, ctrlValue);
								WebHelper.webElementForROBOT = webElement;
							}
							// Mandar---
							// Meghna -- 17/11/2017
						} catch (NoSuchElementException nse) {
							log.error("Failed to find Elements using FindBy for Control ID " + controlID + " <-|-> controlName :" + controlName
									+ "<-|-> LocalizeMessage " + nse.getLocalizedMessage() + " <-|-> Message " + nse.getMessage() + " <-|-> Cause "
									+ nse.getCause(), nse);
							StartRecovery.initiateRecovery();
							throw new NoSuchElementException("Failed to find Elements using FindBy for Control ID " + controlID
									+ " <-|-> controlName :" + controlName + " <-|-> LocalizeMessage " + nse.getLocalizedMessage() + " <-|-> Message"
									+ nse.getMessage() + " <-|-> Cause " + nse.getCause());
						} catch (StaleElementReferenceException sere) {
							log.error("Element is no longer appearing on the DOM page for Control ID" + controlID + " <-|-> controlName :"
									+ controlName + "<-|-> LocalizeMessage " + sere.getLocalizedMessage() + " <-|-> Message " + sere.getMessage()
									+ " <-|-> Cause " + sere.getCause(), sere);
							StartRecovery.initiateRecovery();
							throw new StaleElementReferenceException("Element is no longer appearing on the DOM page for Control ID " + controlID
									+ " <-|-> controlName :" + controlName + " <-|-> LocalizeMessage " + sere.getLocalizedMessage()
									+ " <-|-> Message" + sere.getMessage() + " <-|-> Cause " + sere.getCause());
						} catch (ElementNotVisibleException env) {
							log.error("Element is not visible Control ID" + controlID + " <-|-> controlName :" + controlName
									+ "<-|-> LocalizeMessage " + env.getLocalizedMessage() + " <-|-> Message " + env.getMessage() + " <-|-> Cause "
									+ env.getCause(), env);
							StartRecovery.initiateRecovery();
							throw new ElementNotVisibleException("Element is not visible Control ID " + controlID + " <-|-> controlName :"
									+ controlName + " <-|-> LocalizeMessage " + env.getLocalizedMessage() + " <-|-> Message" + env.getMessage()
									+ " <-|-> Cause " + env.getCause());
						}

					}
					// bhaskar Supressing exception when element not found START
					// -- for IVV/WMSG

					// Pagination//
					else if (action.equalsIgnoreCase("NoException") || action.equalsIgnoreCase("FIND") || action.equalsIgnoreCase("TABLEINPUT")
							|| action.equalsIgnoreCase("I") || action.equalsIgnoreCase("PT")
							// Varsha-- for Dropdown select with partial value
							|| action.equalsIgnoreCase("IVV") || action.equalsIgnoreCase("WMSG") || action.equalsIgnoreCase("PGN"))
					// Mandar -- for IVV/WMSG
					{
						Boolean elementexists = false;
						Constants.ControlIdEnum scontrolID = Constants.ControlIdEnum.valueOf(controlID);
						Thread.sleep(1000);

						switch (scontrolID) {
						case Id:
							elementexists = WebHelper.currentdriver.findElements(By.id(controlName)).size() > 0;
							break;

						case XPath:
							elementexists = WebHelper.currentdriver.findElements(By.xpath(controlName)).size() > 0;
							break;

						case Name:
							elementexists = WebHelper.currentdriver.findElements(By.name(controlName)).size() > 0;
							break;

						case ClassName:
							elementexists = WebHelper.currentdriver.findElements(By.className(controlName)).size() > 0;
							break;

						case LinkText:
							elementexists = WebHelper.currentdriver.findElements(By.linkText(controlName)).size() > 0;
							break;

						case CSSSelector:
							elementexists = WebHelper.currentdriver.findElements(By.cssSelector(controlName)).size() > 0;
							break;

						case Id_p:
						case HTMLID_p:
							elementexists = WebHelper.currentdriver.findElements(By.id(controlName)).size() > 0;
							break;

						case XPath_p:
							elementexists = WebHelper.currentdriver.findElements(By.xpath(controlName)).size() > 0;
							break;

						default:
							break;
						}

						if (elementexists == true) {
							try {
								webElement = getElementByType(controlID, controlName, controltype, imageType, ctrlValue);
							} catch (NoSuchElementException nse) {
								log.error("Failed to find Elements using FindBy for Control ID " + scontrolID + " <-|-> controlName :" + controlName
										+ "<-|-> LocalizeMessage " + nse.getLocalizedMessage() + " <-|-> Message " + nse.getMessage()
										+ " <-|-> Cause " + nse.getCause(), nse);
								StartRecovery.initiateRecovery();

							} catch (StaleElementReferenceException sere) {
								log.error("Element is no longer appearing on the DOM page for Control ID" + scontrolID + " <-|-> controlName :"
										+ controlName + "<-|-> LocalizeMessage " + sere.getLocalizedMessage() + " <-|-> Message " + sere.getMessage()
										+ " <-|-> Cause " + sere.getCause(), sere);
								StartRecovery.initiateRecovery();

							} catch (ElementNotVisibleException env) {
								log.error("Element is not visible Control ID" + scontrolID + " <-|-> controlName :" + controlName
										+ "<-|-> LocalizeMessage " + env.getLocalizedMessage() + " <-|-> Message " + env.getMessage()
										+ " <-|-> Cause " + env.getCause(), env);
								StartRecovery.initiateRecovery();

							} catch (Exception env) {
								log.error("Exception is thrown for Control ID" + scontrolID + " <-|-> controlName :" + controlName
										+ "<-|-> LocalizeMessage " + env.getLocalizedMessage() + " <-|-> Message " + env.getMessage()
										+ " <-|-> Cause " + env.getCause(), env);
								StartRecovery.initiateRecovery();

							}

						} else {
							return;
						}
					}
					// bhaskar Supressing exception when element not found END
					else {
						controlList = WebHelperUtil.getElementsByType(controlID, controlName, WebHelper.control, imageType, ctrlValue);

						if (controlList != null && controlList.size() > 1) {
							try {
								webElement = WebHelperUtil.GetControlByIndex(indexVal, controlList, controlID, controlName, WebHelper.control,
										ctrlValue); // , ISelenium selenium)
							} catch (NoSuchElementException nse) {
								log.error("Failed to find Elements using FindBy with index for Control ID " + controlID + " <-|-> controlName :"
										+ controlName + "<-|-> LocalizeMessage " + nse.getLocalizedMessage() + " <-|-> Message " + nse.getMessage()
										+ " <-|-> Cause " + nse.getCause(), nse);
								StartRecovery.initiateRecovery();
								throw new NoSuchElementException("Failed to find Elements using FindBy with index for Control ID " + controlID
										+ " <-|-> controlName :" + controlName + " <-|-> LocalizeMessage " + nse.getLocalizedMessage()
										+ " <-|-> Message" + nse.getMessage() + " <-|-> Cause " + nse.getCause());
							} catch (StaleElementReferenceException sere) {
								log.error("Element is no longer appearing on the DOM page with index for Control ID" + controlID
										+ " <-|-> controlName :" + controlName + "<-|-> LocalizeMessage " + sere.getLocalizedMessage()
										+ " <-|-> Message " + sere.getMessage() + " <-|-> Cause " + sere.getCause(), sere);
								StartRecovery.initiateRecovery();
								throw new StaleElementReferenceException("Element is no longer appearing on the DOM page with index for Control ID "
										+ controlID + " <-|-> controlName :" + controlName + " <-|-> LocalizeMessage " + sere.getLocalizedMessage()
										+ " <-|-> Message" + sere.getMessage() + " <-|-> Cause " + sere.getCause());
							} catch (ElementNotVisibleException env) {
								log.error("Element is not visible with index for Control ID" + controlID + " <-|-> controlName :" + controlName
										+ "<-|-> LocalizeMessage " + env.getLocalizedMessage() + " <-|-> Message " + env.getMessage()
										+ " <-|-> Cause " + env.getCause(), env);
								StartRecovery.initiateRecovery();
								throw new ElementNotVisibleException("Element is not visible with index for Control ID " + controlID
										+ " <-|-> controlName :" + controlName + " <-|-> LocalizeMessage " + env.getLocalizedMessage()
										+ " <-|-> Message" + env.getMessage() + " <-|-> Cause " + env.getCause());
							}
						} else {
							return;
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

		/*** Perform action on the identified control ***/
		// log.info("go to method doAction");
		if (!action.equalsIgnoreCase("Capture")) {
			doAction(FilePath, rowValues, testCase, imageType, controltype, controlID, controlName, ctrlValue, ctrlValue1, ctrlValue2, wscycledate,
					logicalName, action, webElement, true, sheetStructure, headerValues, rowIndex, rowCount, rowNo, colNo, operationType, cycleDate,
					TransactionType);
		}
	}

	/** Locating Web Element **/
	// public static WebElement getElementByType(String controlId, String
	// controlName, String controlType,String imageType,String controlValue)
	// throws Exception //Meghna--perfomance issue
	public static WebElement getElementByType(String controlId, String controlName, String controlType, String imageType, String controlValue)
			throws Exception {

		WebElement controlList = null;
		// ControlIdEnum controlID = ControlIdEnum.valueOf(controlId);
		// log.info("bhaski controlID:"+controlID);
		try {

			if (controlId.equalsIgnoreCase("Id") || controlId.equalsIgnoreCase("HTMLID")) {
				try {

					controlList = WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.id(controlName)));// Meghna--For
																													// Performance
																													// issue
				}

				catch (Exception ex) {
					log.error(ex.getMessage(), ex);
					log.info("ID/HTML ID : Element is not clickable : " + controlName);
					controlList = waitForElementPresence.until(ExpectedConditions.presenceOfElementLocated(By.id(controlName)));
				}

			} else if (controlId.equalsIgnoreCase("XPath")) {

				try {
					// controlList =
					// WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName)));//Meghna--For
					// Performance issue
					controlList = WebHelper.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));

				}

				// catch(NoSuchElementException ne)
				catch (Exception ex) {
					log.error(ex.getMessage(), ex);
					log.info("XPATH : Element is not clickable : " + controlName);
					controlList = waitForElementPresence.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));
				}

			} else if (controlId.equalsIgnoreCase("Name")) {

				try {
					// controlList =
					// WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.name(controlName)));//Meghna--For
					// Performance issue
					controlList = WebHelper.wait.until(ExpectedConditions.presenceOfElementLocated(By.name(controlName)));
					// Meghna--For Performance issue
				}

				// catch(NoSuchElementException ne)
				catch (Exception ex) {
					log.error(ex.getMessage(), ex);
					log.info("NAME : Element is not clickable : " + controlName);
					controlList = waitForElementPresence.until(ExpectedConditions.presenceOfElementLocated(By.name(controlName)));
				}

			}

			// Meghna-Added for Commission Ext.,and TransactionError
			// transaction//
			else if (controlId.equalsIgnoreCase("XPath_Menu")) {
				controlList = waitForElementPresence.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));
			}
			//

			else if (controlId.equalsIgnoreCase("ClassName")) {
				controlList = WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.className(controlName)));
			} else if (controlId.equalsIgnoreCase("LinkText") || controlId.equalsIgnoreCase("LinkValue")) {
				controlList = WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.linkText(controlName)));
			} else if (controlId.equalsIgnoreCase("TagText") || controlId.equalsIgnoreCase("TagValue") || controlId.equalsIgnoreCase("TagOuterText")) {
				controlList = WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.tagName(imageType)));
			} else if (controlId.equalsIgnoreCase("CSSSelector")) {
				controlList = WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(controlName)));
			} else if (controlId.equalsIgnoreCase("AjaxPath")) {
				controlList = WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName + "[contains(text(),'" + controlValue
						+ "')]")));
			} else if (controlId.equalsIgnoreCase("Id_p") || controlId.equalsIgnoreCase("HTMLID_p")) {
				controlList = WebHelper.wait.until(ExpectedConditions.presenceOfElementLocated(By.id(controlName)));
			} else if (controlId.equalsIgnoreCase("XPath_p")) {
				controlList = WebHelper.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));
			}
			// bhaskar removing enum constants as suggested by dharmendra END
			return controlList;
		}

		catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			// log.info("bhaskar in catch block");
			webDriver.getReport().setMessage(ex.getLocalizedMessage());
			webDriver.getReport().setStatus("FAIL");
			StartRecovery.initiateRecovery();
			throw new Exception("Failed while access controlName: " + controlName + " <-|-> LocalizeMessage " + ex.getLocalizedMessage()
					+ " <-|-> Message " + ex.getMessage() + " <-|-> Cause " + ex.getCause());
		}
	}

	@SuppressWarnings({ "incomplete-switch", "resource" })
	public static String doAction(String FilePath, Row rowValues, String testCase, String imageType, String controlType, String controlId,
			String controlName, String ctrlValue, String ctrlValue1, String ctrlValue2, String wscycledate, String logicalName, String action,
			WebElement webElement, Boolean Results, Sheet strucSheet, Sheet valSheet, int rowIndex, int rowcount, String rowNo, String colNo,
			String operationType, String cycleDate, String TransactionType) throws WebDriverException, IOException, Exception {
		// log.info("In method doaction");

		String cdate, clocation; // Meghna- Case Screenshot - Declared the
									// variables here to use them for NC and
									// Screenshot

		List<WebElement> WebElementList = null;
		String currentValue = null;
		// HSSFSheet uniqueNumberSheet =null;
		String uniqueNumber = "";
		WebVerification.isFromVerification = false;
		// HashMap<String ,Object> uniqueValuesHashMap = null;
		// HSSFRow uniqueRow = null;
		Constants.ControlTypeEnum controlTypeEnum = Constants.ControlTypeEnum.valueOf(controlType);
		Constants.ControlTypeEnum actionName = Constants.ControlTypeEnum.valueOf(action);
		String DestinationFlatFile = null;// Mandar for Flatfile
		String SourceFlatFile = null;// Mandar for Flatfile
		// bhaskar
		WebHelper.sikscreen = Config.SikuliScr;
		// log.info(sikscreen);
		// bhaskar
		if (controlType.contains("Robot") && !WebHelper.isIntialized) {
			log.info("In method doaction debug1");
			WebHelper.robot = new Robot();
			WebHelper.isIntialized = true;
		}

		// Mandar -- for
		// IVV/WMSG--Meghna--FlatFile-04/12/2017
		// Pagination//

		if (!WebHelperUtil.stringIn(action, new String[] { "I", "PGN", "V", "F", "VA", "VV", "CV", "IVV", "PT", "WMSG" })
				|| !ctrlValue.equalsIgnoreCase("")) {
			// log.info("In method doaction debug2");
			try {
				// log.info("In method doaction debug3");
				switch (controlTypeEnum)

				{

				case WebEdit:
					switch (actionName) {
					case Read:
						uniqueNumber = ReadFromExcel(ctrlValue);
						// log.info("!!!!!!!!!!!!!!!!");
						// log.info("uniqueNumber:"+uniqueNumber);
						webElement.clear();
						((JavascriptExecutor) WebHelper.currentdriver).executeScript("arguments[0].setAttribute('value', '" + uniqueNumber + "')",
								webElement); // For handling Object level issue
												// 01 June -- Meghana
						webElement.clear();
						Thread.sleep(1000);
						webElement.sendKeys(uniqueNumber);// Meghana
						break;
					case Write:
						WebHelperUtil.writeToExcel(ctrlValue, webElement, controlId, controlType, controlName, rowNo, colNo);
						break;
					case I:

						/*
						 * if(logicalName.equalsIgnoreCase("AdjustmentAmount"))
						 * { Thread.sleep(20000); }
						 */

						// Meghna-For UI Validation--To clear text from the
						// field
						if (ctrlValue.equalsIgnoreCase("BLANK")) {
							webElement.click();
							Thread.sleep(1000);
							webElement.clear();
						} else if (!ctrlValue.equalsIgnoreCase("null")) {
							log.info("ctrlValue is :" + ctrlValue);
							// Thread.sleep(1000);
							try {
								webElement.click();
							} catch (Exception ex) {
								log.error("Error before sleeping for 30 seconds");
								Thread.sleep(30000);
								webElement.click();
							}
							webElement.clear();
							Thread.sleep(1000);
							((JavascriptExecutor) WebHelper.currentdriver).executeScript("arguments[0].setAttribute('value', '" + ctrlValue + "')",
									webElement); // For handling Object level
													// issue 01 June
							webElement.clear();
							Thread.sleep(1000);
							webElement.sendKeys(ctrlValue);
							Thread.sleep(2000); // Meghna
							// webElement.sendKeys(Keys.TAB); //For handling
							// Object level issue 01 June
							// Thread.sleep(2000);
						} else {
							webElement.clear();
						}
						break;
					// Pagination//
					case PGN:
						if (!ctrlValue.equalsIgnoreCase("null")) {
							log.info("ctrlValue is :" + ctrlValue);
							webElement.click();
							webElement.clear();
							Thread.sleep(1000);
							((JavascriptExecutor) WebHelper.currentdriver).executeScript("arguments[0].setAttribute('value', '" + ctrlValue + "')",
									webElement); // For handling Object level
													// issue 01 June
							webElement.clear();
							Thread.sleep(1000);
							webElement.sendKeys(ctrlValue);
							Robot robot = new Robot();
							Thread.sleep(1000);
							webElement.click();
							Thread.sleep(1000);
							robot.keyPress(KeyEvent.VK_ENTER);
							robot.keyRelease(KeyEvent.VK_ENTER);
						} else {
							webElement.clear();
						}
						break;
					case V:
						currentValue = webElement.getText();
						break;
					}
					break;

				case WebButton:
					switch (actionName) {
					case I:

						if (logicalName.equalsIgnoreCase("CloseBatch")) {
							log.info("stop");
						}
						if (ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes") || !(ctrlValue.trim().equalsIgnoreCase(""))) {

							// new
							// Actions(currentdriver).moveToElement(webElement).perform();
							// // Action for focusing on button

							((JavascriptExecutor) WebHelper.currentdriver).executeScript("arguments[0].scrollIntoView();", webElement);
							// Thread.sleep(5000);
							// webElement.click();
							if (Automation.browserType.toString().toUpperCase().contains("INTERNETEXPLORER")) {
								((JavascriptExecutor) WebHelper.currentdriver).executeScript("arguments[0].click();", webElement);
							} else {
								webElement.click();
							}
						}

						if (logicalName.equalsIgnoreCase("OkButton")) {
							log.info("stop");
						}

						// waitTillPageLoads(currentdriver);
						break;

					case NC:
						if (Automation.browserType.toString().toUpperCase().contains("INTERNETEXPLORER")) {
							((JavascriptExecutor) WebHelper.currentdriver).executeScript("arguments[0].click();", webElement);
						} else {
							webElement.click();
						}
						break;
					case V:
						if (webElement.isDisplayed()) {
							if (webElement.isEnabled() == true)
								currentValue = "True";
							else
								currentValue = "False";
						}
					case FileUpload:

						String autoitFileDir = Config.inputDataFilePath + TransactionMapping.directoryPathFileUpload.toString();
						// webElement.click();

						// Meghna--Take screenshot after buton click//
						clocation = Config.resultFilePath + "\\ScreenShots\\" + "Before_FileUpload" + "_" + WebHelper.screenshotnum + ".png";
						WebHelper.image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
						ImageIO.write(WebHelper.image, "png", new File(clocation));
						// ---

						Thread.sleep(2000);

						// added Tejraj 30-11-2018
						WebDriver driver = Automation.driver;
						WebElement element = driver.findElement(By.xpath(".//*[@class='btn btn-default btn-file']"));
						// browser button
						WebElement text = driver.findElement(By.xpath(".//*[@name='fileUploader']"));
						// text box of browse button
						String uploaded = "";
						int count = 0;
						while (uploaded.equals("")) {
							count++;
							try {
								uploaded = uploadFile(element, text, autoitFileDir, ctrlValue);
								log.info("uploaded file name: " + uploaded);
							} catch (Exception e) {
								log.info("Trying to handle windows dialog box...");
								// below line is added to upload file and to
								// close windows dialog box
								Runtime.getRuntime().exec(autoitFileDir + "\\FileUpload.exe " + autoitFileDir + "\\" + ctrlValue);
								Thread.sleep(3000);
								try {
									text.getAttribute("value");
									// to check whether dialog box is present or not
								} catch (Exception exp) {
									Runtime.getRuntime().exec(autoitFileDir + "\\FileUpload.exe " + autoitFileDir + "\\" + ctrlValue);
									Thread.sleep(3000);
									break;
								}
								// log.info("Exception caught while file uploading: "
								// + e.getMessage());
							}
							// System.out.println("uploaded file name: "+uploaded);

							if (count == 3) {
								break;
							}
						}
						log.info("File Upload Retry Counter is: " + count);
						try {
							String fileName = text.getAttribute("value");
							if (!fileName.equals(""))
								log.info("File uploaded successfully");
						} catch (Exception e) {
							log.info("File Upload Failed!!");
						}
						// end

						Thread.sleep(2000);
						// Meghna--Take screenshot after upload //
						clocation = Config.resultFilePath + "\\ScreenShots\\" + "After_FileUpload" + "_" + WebHelper.screenshotnum + ".png";
						WebHelper.image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
						ImageIO.write(WebHelper.image, "png", new File(clocation));
						// ---
						break;
					}
					break;

				case WebElement:
					// bhaskar
					// log.info("Inside webelement scenario");
					WebVerification.isFromVerification = true;
					// bhaskar
					switch (actionName) {
					case NC:
						webElement.click();
						Thread.sleep(500);
						break;

					case Read:
						uniqueNumber = ReadFromExcel(ctrlValue);
						webElement.clear();

						((JavascriptExecutor) WebHelper.currentdriver).executeScript("arguments[0].setAttribute('value', '" + uniqueNumber + "')",
								webElement); // For handling Object level issue
												// 01 June --Meghana
						webElement.clear();
						Thread.sleep(1000);
						webElement.sendKeys(uniqueNumber);
						break;
					case Write:
						WebHelperUtil.writeToExcel(ctrlValue, webElement, controlId, controlType, controlName, rowNo, colNo);
						break;
					case V:
						if (WebVerification.isFromVerification == true) {
							currentValue = webElement.getText();
							if (currentValue.equalsIgnoreCase(null) || currentValue.equalsIgnoreCase(""))
								currentValue = webElement.getAttribute("value");
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
					// ----Mandar -- to handle UI error message Validations
					// ----//
					case IVV:
						if (!ctrlValue.equalsIgnoreCase("")) {
							Reporter report = new Reporter();
							report.setReport(report);
							Date vvDate = new Date();
							// String svvDate =
							// Automation.dtFormat.format(vvDate);
							WebHelper.ActualValue = webElement.getText();
							WebHelper.ExpectedValue = ctrlValue;
							log.info("ActualValue is : " + WebHelper.ActualValue);
							log.info("ExpectedValue is : " + WebHelper.ExpectedValue);
							// if(ActualValue.equalsIgnoreCase(ExpectedValue))
							WebHelper.ActualValue = WebHelper.ActualValue.toUpperCase();
							WebHelper.ExpectedValue = WebHelper.ExpectedValue.toUpperCase();
							if (WebHelper.ActualValue.contains(WebHelper.ExpectedValue)) {
								report.setStatus("PASS");
								report.setStatus(report.getStatus());
								report.setMessage("Values Matched");
								report.setToDate(Config.dtFormat.format(vvDate));
								WebHelperUtil.saveScreenShot();
								if (webElement.isDisplayed()) {
									try {
										WebElement OKwebButton = Automation.driver.findElement(By.xpath(".//*[@name='Ok']"));
										((JavascriptExecutor) Automation.driver).executeScript("arguments[0].click();", OKwebButton);
										log.info("OK Button clicked, Actual value is  : " + WebHelper.ActualValue);
										webDriver.getReport().setMessage("Message Displayed : " + WebHelper.ActualValue);

										// OKwebButton.sendKeys(controlType);
										Thread.sleep(1000);
									} catch (Exception e)// Mandar
									{
										log.error(e.getMessage(), e);
										log.info("Ok Button on Error Msg Not Found" + e.getLocalizedMessage());
										WebElement CloseDeposit = Automation.driver.findElement(By
												.xpath(".//*[@data-module='CloseDeposit']/div/div/*[@data-name='moduleTitle']"));
										// ((JavascriptExecutor)Automation.driver).executeScript("arguments[0].exist();",
										// CloseDeposit);
										log.info("Close Deposit Label present on Screen, Actual value is  : " + WebHelper.ActualValue);
										webDriver.getReport().setMessage(
												"Close Deposit Sucessfully done - Label on Screen : " + WebHelper.ActualValue);
									}

								}
							} else {
								report.setStatus("FAIL");
								report.setStatus(report.getStatus());
								report.setMessage("Expected error msg not matching Actual: Expected Msg is : " + WebHelper.ExpectedValue
										+ "Actual Msg is : " + WebHelper.ActualValue);// Mandar
								String message = webDriver.getReport().getMessage();
								report.setToDate(Config.dtFormat.format(vvDate));
								// MainController.pauseFun(""); Mandar
								log.error("Expected error msg not matching Actual: Expected Msg is : " + WebHelper.ExpectedValue + "Actual Msg is : "
										+ WebHelper.ActualValue);// Mandar;
								StartRecovery.initiateRecovery();
								controller.pauseFun(message);
								// throw new
								// NoSuchElementException("Failed to find Elements using FindBy for Control ID "
								// + ctrlValue
								// + " <-|-> controlName :" + controlName +
								// " <-|-> LocalizeMessage ");

							}

							WebHelper.columns.add("");
							WebHelper.columnsData.add(WebHelper.columns);
							int temprowcount = 0;
							int tempcolcount = 0;
							ExcelUtility.WriteToCompareDetailResults(testCase, TransactionType, WebHelper.columns, WebHelper.columnsData,
									temprowcount, tempcolcount, report, WebHelper.ExpectedValue, WebHelper.ActualValue, logicalName, operationType,
									cycleDate);
						}
						break;

					case WMSG:
						if (!ctrlValue.equalsIgnoreCase("")) {
							Reporter report = new Reporter();
							report.setReport(report);
							Date vvDate = new Date();
							// String svvDate =
							// Automation.dtFormat.format(vvDate);
							WebHelper.ActualValue = webElement.getText();
							WebHelper.ExpectedValue = ctrlValue;
							log.info("ActualValue is : " + WebHelper.ActualValue);
							log.info("ExpectedValue is : " + WebHelper.ExpectedValue);
							// if(ActualValue.equalsIgnoreCase(ExpectedValue))
							WebHelper.ActualValue = WebHelper.ActualValue.toUpperCase();
							WebHelper.ExpectedValue = WebHelper.ExpectedValue.toUpperCase();
							if (WebHelper.ActualValue.contains(WebHelper.ExpectedValue)) {
								report.setStatus("PASS");
								report.setStatus(report.getStatus());
								report.setMessage("Values Matched");
								report.setToDate(Config.dtFormat.format(vvDate));
								WebHelperUtil.saveScreenShot();
								if (webElement.isDisplayed()) {
									try {
										WebElement YeswebButton = Automation.driver.findElement(By.xpath(".//*[@name='Yes' or @name='Ok']"));
										((JavascriptExecutor) Automation.driver).executeScript("arguments[0].click();", YeswebButton);
										log.info("Yes Button on warning msg clicked, Actual value is  : " + WebHelper.ActualValue);
										webDriver.getReport().setMessage("Message Displayed : " + WebHelper.ActualValue);
										// OKwebButton.sendKeys(controlType);
										Thread.sleep(1000);
									} catch (Exception e) {
										log.error(e.getMessage(), e);
										log.info("Yes/Ok Button on Warning Msg Not Found" + e.getLocalizedMessage());
									}

								}
							}

							else {
								report.setStatus("FAIL");
								report.setStatus(report.getStatus());
								webDriver.getReport().setMessage("Expected message not displayed : " + WebHelper.ActualValue);// Mandar
								String message = webDriver.getReport().getMessage();
								report.setToDate(Config.dtFormat.format(vvDate));
								controller.pauseFun(message);
							}
							WebHelper.columns.add("");
							WebHelper.columnsData.add(WebHelper.columns);
							int temprowcount = 0;
							int tempcolcount = 0;
							ExcelUtility.WriteToCompareDetailResults(testCase, TransactionType, WebHelper.columns, WebHelper.columnsData,
									temprowcount, tempcolcount, report, WebHelper.ExpectedValue, WebHelper.ActualValue, logicalName, operationType,
									cycleDate);
						}
						break;
					// bhaskar Action Verify Values VV START
					case VV:
						Reporter report = new Reporter();
						report.setReport(report);
						Date vvDate = new Date();
						// String svvDate = Automation.dtFormat.format(vvDate);
						WebHelper.ActualValue = webElement.getText();
						WebHelper.ExpectedValue = ctrlValue;
						log.info("ActualValue is : " + WebHelper.ActualValue);
						log.info("ExpectedValue is : " + WebHelper.ExpectedValue);
						// if(ActualValue.equalsIgnoreCase(ExpectedValue))
						WebHelper.ActualValue = WebHelper.ActualValue.toUpperCase();
						WebHelper.ExpectedValue = WebHelper.ExpectedValue.toUpperCase();
						if (WebHelper.ActualValue.contains(WebHelper.ExpectedValue)) {
							report.setStatus("PASS");
							report.setStatus(report.getStatus());
							report.setMessage("Values Matched");
							report.setToDate(Config.dtFormat.format(vvDate));
							WebHelperUtil.saveScreenShot();
						} else {
							report.setStatus("FAIL");
							report.setMessage("Expected error msg not matching Actual: Expected Msg is : " + WebHelper.ExpectedValue
									+ "Actual Msg is : " + WebHelper.ActualValue);
							String message = report.getMessage();
							report.setToDate(Config.dtFormat.format(vvDate));
							StartRecovery.initiateRecovery();
							// Meghna---to initiate recovery for UI validation
							controller.pauseFun(message);
						}
						WebHelper.columns.add("");
						WebHelper.columnsData.add(WebHelper.columns);
						int temprowcount = 0;
						int tempcolcount = 0;
						ExcelUtility.WriteToCompareDetailResults(testCase, TransactionType, WebHelper.columns, WebHelper.columnsData, temprowcount,
								tempcolcount, report, WebHelper.ExpectedValue, WebHelper.ActualValue, logicalName, operationType, cycleDate);
						break;
					// bhaskar Action Verify Values VV START

					// CV for exact comparison -Meghna
					case CV:
						Thread.sleep(5000);
						Reporter reportt = new Reporter();
						reportt.setReport(reportt);
						Date cvDate = new Date();
						// String svvDate = Automation.dtFormat.format(vvDate);
						WebHelper.ActualValue = webElement.getText();
						WebHelper.ExpectedValue = ctrlValue;
						log.info("ActualValue is : " + WebHelper.ActualValue);
						log.info("ExpectedValue is : " + WebHelper.ExpectedValue);
						// if(ActualValue.equalsIgnoreCase(ExpectedValue))
						WebHelper.ActualValue = WebHelper.ActualValue.toUpperCase();
						WebHelper.ExpectedValue = WebHelper.ExpectedValue.toUpperCase();
						if (WebHelper.ActualValue.equals(WebHelper.ExpectedValue)) {
							reportt.setStatus("PASS");
							reportt.setStatus(reportt.getStatus());
							reportt.setMessage("Values Matched");
							reportt.setToDate(Config.dtFormat.format(cvDate));
							WebHelperUtil.saveScreenShot();
						} else {
							reportt.setStatus("FAIL");
							reportt.setStatus(reportt.getStatus());
							reportt.setMessage("Values Not Matched");
							reportt.setToDate(Config.dtFormat.format(cvDate));
							controller.pauseFun("Values Not Matched");
						}
						WebHelper.columns.add("");
						WebHelper.columnsData.add(WebHelper.columns);
						int temprowcountt = 0;
						int tempcolcountt = 0;
						ExcelUtility.WriteToCompareDetailResults(testCase, TransactionType, WebHelper.columns, WebHelper.columnsData, temprowcountt,
								tempcolcountt, reportt, WebHelper.ExpectedValue, WebHelper.ActualValue, logicalName, operationType, cycleDate);
						break;
					// CV for exact comparison -Meghna
					}
					break;

				case JSScript:
					((JavascriptExecutor) WebHelper.currentdriver).executeScript(controlName, ctrlValue);
					break;

				case Wait:

					// Meghna--For skipping static wait give in structure sheet
					// as it was increasing overall execution time//
					if ((Config.applyStaticWait).equalsIgnoreCase("True")) {
						Thread.sleep(Integer.parseInt(controlName) * 1000);
					} else {
						// waitForPageLoaded();
						log.info("Wait not applied");
					}

					break;

				case WaitFor:
					return WebHelper.doAction(FilePath, rowValues, testCase, imageType, controlType, controlId, controlName, ctrlValue, ctrlValue1,
							ctrlValue2, wscycledate, logicalName, action, webElement, Results, strucSheet, valSheet, rowIndex, rowcount, rowNo,
							colNo, operationType, cycleDate, TransactionType);

				case CheckBox:
					switch (actionName) {
					case I:
						Thread.sleep(3000);
						log.info("In checkbox");
						if (ctrlValue.equalsIgnoreCase("") || StringUtils.isEmpty(ctrlValue)) {
							break;
						} else if (ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes")) {
							if (!webElement.isSelected())
							// Added By Dharmendra to check whether CheckBox
							// select or not
							{
								// webElement.click();
								// Select Checkbox, if it is not selectd
								((JavascriptExecutor) WebHelper.currentdriver).executeScript("arguments[0].scrollIntoView();", webElement);
								((JavascriptExecutor) WebHelper.currentdriver).executeScript("arguments[0].click();", webElement);
								// Meghna for HoldRelease issue
							}

						}

						else if (ctrlValue.equalsIgnoreCase("N") || ctrlValue.equalsIgnoreCase("No")) {
							if (webElement.isEnabled())
							// Meghna--for Performance issue-handling returned
							// payment checkbox
							{
								if (webElement.isSelected())
								// Added By Dharmendra to check whether CheckBox
								// select or not
								{
									webElement.click();
									// Deselect Checkbox, if it is selected
								}
							}

						}

						break;

					case NC:
						if (!webElement.isSelected()) {
							webElement.click();
						}
						break;
					}
					break;

				case Radio:
				case WebLink:
				case CloseWindow:
					// added this Case to bypass page loading after clicking the
					// event
				case WaitForJS:
				case ListBox:
				case WebList:
				case AjaxWebList:
				case Refresh:
				case Browser:
				case URL:
				case Menu:
				case Alert:
				case WebImage:
				case ActionClick:
				case ActionDoubleClick:
				case ActionClickandEsc:
					return WebHelper.doAction(FilePath, rowValues, testCase, imageType, controlType, controlId, controlName, ctrlValue, ctrlValue1,
							ctrlValue2, wscycledate, logicalName, action, webElement, Results, strucSheet, valSheet, rowIndex, rowcount, rowNo,
							colNo, operationType, cycleDate, TransactionType);

				case IFrame:
					log.info("In method doaction debug4");
					WebHelper.currentdriver = WebHelper.currentdriver.switchTo().frame(controlName);
					log.info("In method doaction debug5");
					break;
				case ActionMouseOver:
					Actions builderMouserOver = new Actions(WebHelper.currentdriver);
					builderMouserOver.moveToElement(webElement).perform();
					// Action mouseOverAction =
					// builderMouserOver.moveToElement(webElement).build();
					// mouseOverAction.perform();
					break;

				case Calendar:
				case CalendarNew:
				case CalendarIPF:
				case CalendarEBP:
					return WebHelper.doAction(FilePath, rowValues, testCase, imageType, controlType, controlId, controlName, ctrlValue, ctrlValue1,
							ctrlValue2, wscycledate, logicalName, action, webElement, Results, strucSheet, valSheet, rowIndex, rowcount, rowNo,
							colNo, operationType, cycleDate, TransactionType);

					/** Code for window popups **/
				case Window:
					switch (actionName) {
					case O:
						return WebHelper.doAction(FilePath, rowValues, testCase, imageType, controlType, controlId, controlName, ctrlValue,
								ctrlValue1, ctrlValue2, wscycledate, logicalName, action, webElement, Results, strucSheet, valSheet, rowIndex,
								rowcount, rowNo, colNo, operationType, cycleDate, TransactionType);
					}
					break;

				case WebTable:
					switch (actionName) {
					case Read:
					case Write:
					case NC:
					case V:
						return WebHelper.doAction(FilePath, rowValues, testCase, imageType, controlType, controlId, controlName, ctrlValue,
								ctrlValue1, ctrlValue2, wscycledate, logicalName, action, webElement, Results, strucSheet, valSheet, rowIndex,
								rowcount, rowNo, colNo, operationType, cycleDate, TransactionType);
					case TableInput:

						String tableV = WebHelperUtil.checkTable(logicalName, rowValues, ExcelUtility.TIvaluesheetrows);

						if (!(tableV.equals(""))) {
							WebElement tableFound1 = WebHelper.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));
							// Meghna--to wait
							WebHelper.findtablefound = WebHelper.currentdriver.findElements(By.xpath(controlName)).size() > 0;

							if (WebHelper.findtablefound == true) {
								// WebElement tableFound =
								// wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));
								// //Meghna--for performance issue//
								WebElement tableFound = WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName)));
								// Meghna--for performance issue//
								BillingProduct.TableInputAction(tableFound, controlName, logicalName, rowValues, WebHelper.valuesHeader,
										ExcelUtility.TIvaluesheetrows);
								Thread.sleep(1000);
							} else {
								log.info("Table not found. TABLE INPUT Functionality failed");
								// Meghna--To log error in case table not found
								break;
							}
						}

						break;
					// Modified the code to handle performance issues//
					case FIND:

						String findV = WebHelperUtil.CheckFind(logicalName, rowValues);

						if (!(findV.equals(""))) {
							Thread.sleep(5000);
							WebHelper.findtablefound = WebHelper.currentdriver.findElements(By.xpath(controlName)).size() > 0;

							if (WebHelper.findtablefound == true) {
								// WebElement tableFound =
								// wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));
								WebElement tableFound = WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName))); // Meghna
								BillingProduct.findAction(tableFound, controlName, logicalName, rowValues, WebHelper.valuesHeader);
								Thread.sleep(1000);
							} else {
								log.info("Table not found. FIND Functionality failed");
								// Meghna--To log error in case table not found
								break;
							}
						}
						break;

					case I:
						Thread.sleep(10000);
						WebHelper.findtablefound = WebHelper.currentdriver.findElements(By.xpath(controlName)).size() > 0;
						if (WebHelper.findtablefound == true) {
							WebElement tableFound = WebHelper.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));
							List<WebElement> table_Rows = tableFound.findElements(By.tagName("tr"));
							List<WebElement> table_Columns = table_Rows.get(1).findElements(By.tagName("td"));

							int ApplicationtableRowsize = table_Rows.size();
							// ApplicationtableRowsize = no of rows in the
							// WebTable
							int Applicationtablecolumnsize = table_Columns.size();
							// Applicationtablecolumnsize = no of columns in the
							// WebTable
							String ColumnName = ctrlValue.split(",")[0];
							String ColumnType = ctrlValue.split(",")[1];

							for (int i = 1; i <= Applicationtablecolumnsize; i++) {
								Thread.sleep(1000);
								String ApplicationColumnHeaderxapth = controlName + "/thead/tr/th[" + i + "]";
								log.info("ApplicationColumnHeader is:" + ApplicationColumnHeaderxapth);
								WebElement element = WebHelper.currentdriver.findElement(By.xpath(ApplicationColumnHeaderxapth));
								String ApplicationColumnHeader = element.getText();
								if ((ColumnName).equalsIgnoreCase(ApplicationColumnHeader)) {
									for (int r = 1; r <= ApplicationtableRowsize; r++) {
										if (ColumnType.equalsIgnoreCase("Webcheckbox")) {
											String XPath = controlName + "/tbody/tr[" + r + "]/td[" + i + "]/div/div/input";
											WebHelper.objfound = WebHelper.currentdriver.findElements(By.xpath(XPath)).size() > 0;
											if (WebHelper.objfound == true) {
												WebElement newelement = WebHelper.currentdriver.findElement(By.xpath(XPath));
												((JavascriptExecutor) WebHelper.currentdriver).executeScript("arguments[0].scrollIntoView();",
														newelement);// Meghana
																	// --
												newelement.click();
												Thread.sleep(500);
												((JavascriptExecutor) WebHelper.currentdriver).executeScript("arguments[0].scrollIntoView();",
														newelement);
												Thread.sleep(500);
												WebHelper.objfound = false;
											}

										} else if (ColumnType.equalsIgnoreCase("WebLink")) {
											String XPath = controlName + "/tbody/tr[" + r + "]/td[" + i + "]/div/span";
											WebHelper.objfound = WebHelper.currentdriver.findElements(By.xpath(XPath)).size() > 0;
											if (WebHelper.objfound == true) {
												WebElement newelement = WebHelper.currentdriver.findElement(By.xpath(XPath));
												log.info("link xpath " + XPath);

												((JavascriptExecutor) WebHelper.currentdriver).executeScript("arguments[0].scrollIntoView();",
														newelement);// Meghana
																	// --
												((JavascriptExecutor) WebHelper.currentdriver).executeScript("arguments[0].click();", newelement);// Meghana
																																					// --
												// newelement.click();//Meghana
												// --
												Thread.sleep(500);
												// ((JavascriptExecutor)currentdriver).executeScript("arguments[0].scrollIntoView();",
												// newelement);
												Thread.sleep(500);
												WebHelper.objfound = false;
											}
										} else if (ColumnType.equalsIgnoreCase("WebCheckBox")) {
											// not encountered
										}
									}
								}
							}

						}
						break;
					}
					break;

				// bhaskar capture screenshot START
				case Screenshot:
					switch (actionName) {
					case NC:
					case Screenshot:
						return WebHelper.doAction(FilePath, rowValues, testCase, imageType, controlType, controlId, controlName, ctrlValue,
								ctrlValue1, ctrlValue2, wscycledate, logicalName, action, webElement, Results, strucSheet, valSheet, rowIndex,
								rowcount, rowNo, colNo, operationType, cycleDate, TransactionType);
					}
					break;

				case Robot:
					if (controlName.equalsIgnoreCase("SetFilePath")) {
						StringSelection stringSelection = new StringSelection(ctrlValue);
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
						WebHelper.robot.delay(1000);
						WebHelper.robot.keyPress(KeyEvent.VK_CONTROL);
						WebHelper.robot.keyPress(KeyEvent.VK_V);
						WebHelper.robot.keyRelease(KeyEvent.VK_V);
						WebHelper.robot.keyRelease(KeyEvent.VK_CONTROL);

					} else if (controlName.equalsIgnoreCase("TAB")) {
						Thread.sleep(1000);

						// ****Belowgiven code commented by Basebilling ---
						// Meghana******
						// robot.keyPress(KeyEvent.VK_TAB);
						// robot.keyRelease(KeyEvent.VK_TAB);

						// Belowgiven code changes done by Basebilling ---
						// Meghana
						try {
							WebHelper.webElementForROBOT.sendKeys(Keys.TAB);
						} catch (Exception ex) {
							log.error(ex.getMessage(), ex);
							System.out.println("Object was not available");
						}
						// Thread.sleep(1000);
						// ***
					} else if (controlName.equalsIgnoreCase("SPACE")) {
						WebHelper.robot.keyPress(KeyEvent.VK_SPACE);
						WebHelper.robot.keyRelease(KeyEvent.VK_SPACE);
					} else if (controlName.equalsIgnoreCase("ENTER")) {
						WebHelper.robot.keyPress(KeyEvent.VK_ENTER);
						WebHelper.robot.keyRelease(KeyEvent.VK_ENTER);
						Thread.sleep(3000);
					}
					break;

				case DB:
				case WaitForEC:
				case SikuliScreen:
				case SikuliType:
				case SikuliButton:
				case Slider:
				case Date:
					return WebHelper.doAction(FilePath, rowValues, testCase, imageType, controlType, controlId, controlName, ctrlValue, ctrlValue1,
							ctrlValue2, wscycledate, logicalName, action, webElement, Results, strucSheet, valSheet, rowIndex, rowcount, rowNo,
							colNo, operationType, cycleDate, TransactionType);

				case MaskedInputDate:
					if (!ctrlValue.equalsIgnoreCase("null")) {
						webElement.clear();
						webElement.click();
						((JavascriptExecutor) WebHelper.currentdriver).executeScript("arguments[0].setAttribute('value', '" + ctrlValue + "')",
								webElement); // For handling Object level issue
						// 01 June -- Meghana
						webElement.clear(); // -- Meghana
						Thread.sleep(1000); // -- Meghana
						webElement.sendKeys(ctrlValue);
						webElement.sendKeys(Keys.TAB);// Mandar
					} else {
						webElement.clear();
					}
					break;
				// bhaskar

				case FileUpload:

					((JavascriptExecutor) WebHelper.currentdriver).executeScript("arguments[0].setAttribute('value', '" + ctrlValue + "')",
							webElement); // For handling Object level issue 01
											// June
					webElement.clear();
					Thread.sleep(1000);
					webElement.sendKeys(ctrlValue);
					break;

				case ScrollTo:
					Locatable element = (Locatable) webElement;
					Point p = element.getCoordinates().onScreen();
					JavascriptExecutor js = (JavascriptExecutor) WebHelper.currentdriver;
					js.executeScript("window.scrollTo(" + p.getX() + "," + (p.getY() + 150) + ");");
					break;
				case Freeze:
				case AP_Outbound:
				case FlatFileResponse:
				case FlatFile:
				case CopyFlatFile:
				case Task_Load:
				case Apex_Archive:
					return WebHelper.doAction(FilePath, rowValues, testCase, imageType, controlType, controlId, controlName, ctrlValue, ctrlValue1,
							ctrlValue2, wscycledate, logicalName, action, webElement, Results, strucSheet, valSheet, rowIndex, rowcount, rowNo,
							colNo, operationType, cycleDate, TransactionType);

				case WebServiceCSI: // Meghna--04/12/2017
				case WebService_CheckUpdate:// ***For GB - 27/07/2018***//
				case WebService_VoidRef:// ***For GB - 27/07/2018***//
				case WebService: // devishree
				case WebService1:
				case WebService2:
				case WebService3:
				case WebServiceV:
				case WebServiceC:
				case WebServiceRP:
				case WebServiceVI:
				case WebServiceV1:
				case WebServiceV2:
				case WebServiceVAG:
				case WebServiceV3:
				case Restful:
					return WebHelper.doAction(FilePath, rowValues, testCase, imageType, controlType, controlId, controlName, ctrlValue, ctrlValue1,
							ctrlValue2, wscycledate, logicalName, action, webElement, Results, strucSheet, valSheet, rowIndex, rowcount, rowNo,
							colNo, operationType, cycleDate, TransactionType);

				case OutPutForm:
					return WebHelper.doAction(FilePath, rowValues, testCase, imageType, controlType, controlId, controlName, ctrlValue, ctrlValue1,
							ctrlValue2, wscycledate, logicalName, action, webElement, Results, strucSheet, valSheet, rowIndex, rowcount, rowNo,
							colNo, operationType, cycleDate, TransactionType);
				default:
					log.info("U r in Default");
					break;
				}
			} catch (Exception e) {
				if (!WebHelperUtil.isLastRetry()) {
					throw e;
				}
				log.error(e.getMessage(), e);
				// throw new Exception(e.getMessage());
				webDriver.getReport().setMessage(e.getMessage());
				webDriver.getReport().setMessage(e.getLocalizedMessage());
				// iTAFSeleniumWeb.WebDriver.report.setstrMessage =
				// errorCodeAndMessage; //Mandar
				webDriver.getReport().setStatus("FAIL");
				controller.pauseFun(e.getMessage());
				log.error("Exception in doAction <-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message " + e.getMessage()
						+ " <-|-> Cause " + e.getCause());
				throw new WebDriverException("Exception in doAction <-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message"
						+ e.getMessage() + " <-|-> Cause " + e.getCause(), e);
			}
		}
		// TM-02/02/2015: Radio button found ("F") & AJAX control ("VA")
		if ((action.equalsIgnoreCase("V") || action.equalsIgnoreCase("F") || action.equalsIgnoreCase("VA")) && !ctrlValue.equalsIgnoreCase("")) {
			if (Results == true) {
				webDriver.setReport(WebHelperUtil.WriteToDetailResults(ctrlValue, currentValue, logicalName));
			}
		}

		return currentValue;

	}

	static String ReadFromExcel(String controlValue) throws IOException {
		return WebHelperUtil.ReadFromExcel(controlValue, controlValue);
	}

	private static String uploadFile(WebElement element, WebElement text, String autoitFileDir, String ctrlValue) throws InterruptedException,
			IOException {

		element.click();
		Thread.sleep(2000);
		Runtime.getRuntime().exec(autoitFileDir + "\\FileUpload.exe " + autoitFileDir + "\\" + ctrlValue);
		// Below Thread.sleep is required once windows browse modal populated
		// with file path should be opened that takes sometime.
		Thread.sleep(5000);
		log.info("Inside uploadFile() called");
		return text.getAttribute("value");
	}

}