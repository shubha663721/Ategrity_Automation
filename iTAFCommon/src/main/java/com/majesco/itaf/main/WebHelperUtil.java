package com.majesco.itaf.main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.openqa.selenium.interactions.internal.Coordinates;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;

import com.google.common.base.Function;
import com.majesco.itaf.recovery.StartRecovery;
import com.majesco.itaf.recovery.StartRecoveryClaims;
import com.majesco.itaf.util.Constants;
import com.majesco.itaf.util.ExcelUtility;
import com.majesco.itaf.vo.Reporter;
import com.majesco.itaf.webservice.WebService;

public class WebHelperUtil {

	final static Logger log = Logger.getLogger(WebHelperUtil.class.getName());

	static ITAFWebDriver webDriver = ITAFWebDriver.getInstance();
	static MainController controller = ObjectFactory.getMainController();

	private static boolean isBillingApplication = ITAFWebDriver.isBillingApplication();
	private static boolean isClaimsApplication = ITAFWebDriver.isClaimsApplication();
	private static boolean isPASApplication = ITAFWebDriver.isPASApplication();

	public static final int retryCount = 3;
	public static int retryCounter = 0;

	/**
	 * This method return Map containing Column Header and its index as key and
	 * value respectively.
	 * 
	 * @param reqSheet
	 * @return
	 */
	public static HashMap<String, Integer> getValueFromHashMap(Sheet reqSheet) {
		HashMap<String, Integer> inputHashTable = new HashMap<>();
		Row rowHeader = reqSheet.getRow(0);
		int columnCount = rowHeader.getPhysicalNumberOfCells();
		for (int colIndex = 0; colIndex < columnCount; colIndex++) {
			inputHashTable.put(rowHeader.getCell(colIndex).toString(), colIndex);
		}
		return inputHashTable;
	}

	public static String getCellData(String reqValue, Sheet reqSheet, int rowIndex, HashMap<String, Integer> inputHashTable) throws IOException {
		Cell reqCell = null;
		Integer actualvalue = null;
		String req = "";
		DataFormatter fmt = new DataFormatter();
		if (inputHashTable.isEmpty()) {
			inputHashTable = getValueFromHashMap(reqSheet);
		}

		Row rowActual = reqSheet.getRow(rowIndex);
		if (rowActual == null) {
			return req;
		}

		actualvalue = inputHashTable.get(reqValue);
		if (actualvalue == null) {
			webDriver.getReport().setMessage("Column " + reqValue + " not Found. Please Check input Sheet");
			controller.pauseFun("Column " + reqValue + " not Found. Please Check input Sheet");
			return req;
		}

		int colIndex = actualvalue;
		reqCell = rowActual.getCell(colIndex);
		if (reqCell == null) {
			System.out.println(reqValue + " is Null");
			return req;
		}

		//if (Config.projectName.equals("DistributionManagement")) {
			// Minaakshi : 04-01-2019
		if (ITAFWebDriver.isPASApplication()){// Mrinmayee
			HSSFFormulaEvaluator.evaluateAllFormulaCells(reqSheet.getWorkbook());
			// Minaakshi : 03-01-2018
			FormulaEvaluator evaluator = reqSheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

			CellValue cellValue = evaluator.evaluate(reqCell);
			CellType type = null;

			if (cellValue != null) {
				type = cellValue.getCellTypeEnum();
			} else {
				type = reqCell.getCellTypeEnum();
			}
			switch (type) {
			case BLANK:
				req = "";
				break;
			case NUMERIC:
				req = fmt.formatCellValue(reqCell, evaluator);
				break;
			case STRING:
				req = reqCell.getStringCellValue();
				break;
			case BOOLEAN:
				req = Boolean.toString(reqCell.getBooleanCellValue());
				break;
			case ERROR:
				req = "error";
				break;
			default:
				break;
			}

		} else {
			int type = reqCell.getCellType();
			switch (type) {
			case Cell.CELL_TYPE_BLANK:
				req = "";
				break;
			case Cell.CELL_TYPE_NUMERIC:
				req = fmt.formatCellValue(reqCell);
				break;
			case Cell.CELL_TYPE_STRING:
				req = reqCell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				req = Boolean.toString(reqCell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_ERROR:
				req = "error";
				break;
			case Cell.CELL_TYPE_FORMULA:
				req = reqCell.getCellFormula();
				break;
			default:
				break;
			}
		}

		return req;
	}

	public static int count(File filename) throws IOException {
		InputStream is = new BufferedInputStream(new FileInputStream(filename));
		try {
			byte[] c = new byte[1024];
			int count = 0;
			int readChars = 0;
			boolean empty = true;
			while ((readChars = is.read(c)) != -1) {
				empty = false;
				for (int i = 0; i < readChars; ++i) {
					if (c[i] == '\n')
						++count;
				}
			}
			return (count == 0 && !empty) ? 1 : count;
		} finally {
			is.close();
		}
	}

	public static List<WebElement> getElementsByType(String controlId, String controlName, String controlType, String imageType, String controlValue)
			throws Exception {

		List<WebElement> controlList = null;
		Constants.ControlIdEnum controlID = Constants.ControlIdEnum.valueOf(controlId);
		try {
			switch (controlID) {
			case Id:
			case HTMLID:
				controlList = WebHelper.wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.id(controlName)));
				break;

			case Name:
				controlList = WebHelper.wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.name(controlName)));
				break;

			case XPath:
				controlList = WebHelper.wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath(controlName)));
				break;

			case ClassName:
				controlList = WebHelper.wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className(controlName)));
				break;

			case TagText:
			case TagValue:
			case TagOuterText:
				controlList = WebHelper.wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.tagName(imageType)));
				break;

			case LinkText:
				controlList = WebHelper.wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.linkText(controlName)));
				break;

			case LinkValue:
				controlList = WebHelper.wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.linkText(controlValue)));
				break;

			case CSSSelector:
				controlList = WebHelper.wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(controlName)));
				break;

			default:
				break;
			}
			return controlList;
		} catch (Exception ex) {
			if (isBillingApplication) {
				StartRecovery.initiateRecovery();
			} else if (isClaimsApplication) {
				StartRecoveryClaims.initiateRecovery();
			}
			log.error(ex.getMessage(), ex);
			throw new Exception("Failed while access controlName: " + controlName + " <-|-> LocalizeMessage " + ex.getLocalizedMessage()
					+ " <-|-> Message " + ex.getMessage() + " <-|-> Cause " + ex.getCause(), ex);
		}
	}

	public static WebElement GetControlByIndex(String indexVal, List<WebElement> lstControl, String controlID, String controlName,
			String controlType, String controlValue) throws Exception {
		try {
			int indxValue = 0;
			if (!indexVal.equalsIgnoreCase("") && indexVal.length() > 0) {
				indxValue = Integer.valueOf(indexVal);
			}
			if (lstControl.size() > 1 && indexVal.equalsIgnoreCase("")) {
				log.info("More than one control found please provide the index");
			} else if (lstControl.size() == 1) {
				indexVal = "0";
			}

			int index = 0;
			for (int buttonIndex = 0; buttonIndex < lstControl.size(); buttonIndex++) {
				if (controlID.equalsIgnoreCase("TagValue") && (lstControl.get(buttonIndex).getAttribute("value") != null)) {
					if (lstControl.get(buttonIndex).getAttribute("value").equalsIgnoreCase(controlValue))// changed by IK on 23-01-2014 for HSCIC
					
					{
						return lstControl.get(buttonIndex);
					}

				} else if (controlID.equalsIgnoreCase("TagText") && (lstControl.get(buttonIndex).getAttribute("text") != null)) {
					if (lstControl.get(buttonIndex).getAttribute("text").equalsIgnoreCase(controlValue))// changed by IK on 23-01-2014 for HSCIC
					{
						return lstControl.get(buttonIndex);
					}
				} else {
					if (index == indxValue) {
						return lstControl.get(buttonIndex);
					}
					index += 1;
				}

			}
			return null;
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
			throw new Exception("Failed while access controlName: " + controlName + " <-|-> LocalizeMessage " + ex.getLocalizedMessage()
					+ " <-|-> Message " + ex.getMessage() + " <-|-> Cause " + ex.getCause(), ex);
		}
	}

	public static void waitForAjaxLoad(WebDriver driver) throws InterruptedException {
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		if ((Boolean) executor.executeScript("return window.jQuery != undefined")) {
			while (!(Boolean) executor.executeScript("return jQuery.active == 0")) {
				Thread.sleep(1000);
			}
		}
		return;
	}

	/** This Functions Waits for the Ajax Controls To Load on the page **/
	public static Boolean waitFroAjax() throws InterruptedException, IOException {
		Boolean ajaxIsComplete = false;
		try {

			while (true) // Handle timeout somewhere
			{

				ajaxIsComplete = (Boolean) ((JavascriptExecutor) Automation.driver)
						.executeScript("return window.jQuery != undefined && jQuery.active == 0");
				if (ajaxIsComplete) {
					break;
				}
				Thread.sleep(100);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			controller.pauseFun("Ajax controls Loading " + e.getMessage());
		}
		return ajaxIsComplete;
	}

	// Added by Rajesh M to calling other framework jar from Policy automation
	// framework
	public static void executeJar(String controlName) throws IOException {
		String[] command = controlName.split(",");
		if (command.length >= 4) {
			System.out.println(" ");
			System.out.println("==========Arguments Passed ===========");
			for (String s : command) {
				System.out.println(s);
			}
			System.out.println("============================");
			System.out.println(" ");
			final StringBuffer sb = new StringBuffer();
			// int processComplete = -1;
			ProcessBuilder pb = new ProcessBuilder(command);
			pb.redirectErrorStream(true);
			try {
				final Process process = pb.start();
				final InputStream is = process.getInputStream();

				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(is));
					String line;
					while ((line = reader.readLine()) != null) {
						sb.append(line).append('\n');
					}
				} catch (IOException e) {
					log.error(e.getMessage(), e);
					System.out.println("Java ProcessBuilder: IOException occured.");
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				System.out.println("Java ProcessBuilder Complete:");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				controller.pauseFun(e.getMessage() + " from executeJar Function");
			}
			System.out.println("Java ProcessBuilder - return=: " + sb.toString());
		} else {
			System.out.println("In executeJar function: Arguments below 4");
		}
	}

	public static boolean WebObjectPresent(WebElement webelement) {
		try {
			WebDriverWait Popwait = new WebDriverWait(Automation.driver, 180);
			Popwait.until(ExpectedConditions.elementToBeClickable(webelement));

			return true;

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return false;
		}
	}

	public static void InputValue(WebElement webElement, String value, String controlId, String controlName, String imageType, String ctrlValue)
			throws InterruptedException {
		// Located element and stored It's reference In variable.
		JavascriptExecutor js = (JavascriptExecutor) Automation.driver;
		{
			WebElement Search_Box = webElement;
			try {

				webElement.clear();
				webElement.sendKeys(value);

				webElement.sendKeys(Keys.TAB);

				String WebEditValue = (Search_Box.getAttribute("value").toString()).replaceAll("[-$, ;%()]", "");
				System.out.println(WebEditValue.replace("-", ""));
				String CompareValue = ctrlValue.replaceAll("[$, ;%-()]", "");
				CompareValue = CompareValue.replace("-", "");
				WebEditValue = WebEditValue.replace("-", "");
				System.out.println(CompareValue);
				Thread.sleep(500);
				if (WebEditValue.equals(CompareValue)) {
					webElement.sendKeys(Keys.TAB);
					Thread.sleep(200);
				} else {
					Thread.sleep(1000);
				}

			} catch (Exception e) {
				log.error(e.getMessage(), e);
				try {
					// i--;
					Thread.sleep(1000);
					webElement = WebHelperPAS.getElementByType(controlId, controlName, WebHelper.control, imageType, ctrlValue);

					js.executeScript("arguments[0].focus(); arguments[0].blur(); return true", webElement);
					js.executeScript("arguments[0].value = '';", webElement);

					webElement.sendKeys(value);

					webElement.sendKeys(Keys.TAB);

					if (!Search_Box.getAttribute("value").isEmpty()) {
						webElement.sendKeys(Keys.TAB);
						// break;
					}
				} catch (Exception e1) {
					log.error(e1.getMessage(), e1);
					Thread.sleep(3000);
					System.out.println("Web Edit Element Not Found:");
				}

			}
		}
	}

	// public static WebDriverWait wait = new WebDriverWait(SeleniumInfo.Driver,
	// TimeSpan.FromSeconds(20));
	public static void WaitUntilAttributeValueEquals(WebElement webElement) {
		int count = 1;
		while (webElement.getAttribute("value").isEmpty()) {
			count++;
		}

	}

	public static void fnHighlightMe(WebDriver driver, WebElement controlName) throws InterruptedException {
		// Creating JavaScriptExecuter Interface
		JavascriptExecutor js = (JavascriptExecutor) driver;
		for (int iCnt = 0; iCnt < 3; iCnt++) {
			// Execute javascript
			js.executeScript("arguments[0].style.border='4px groove green'", controlName);
			Thread.sleep(1000);
			js.executeScript("arguments[0].style.border=''", controlName);
		}
	}

	public static void handleWebTableAction(WebElement webElement) {

		WebElement Dynamictable = webElement;

		List<WebElement> DynamictableRow = Dynamictable.findElements(By.tagName("tr"));

		int RowCount = DynamictableRow.size();
		int count = 0;
		for (int i = 0; i < RowCount; i++) {
			boolean matchfound = false;
			if (count > 0) {
				break;
			}
			// for(WebElement tableRows:tableRow){
			WebElement tableRows = DynamictableRow.get(i);

			List<WebElement> tableColumn = tableRows.findElements(By.tagName("td"));

			int ColumnCount = tableColumn.size();

			for (int j = 0; j < ColumnCount; j++) {

				WebElement Columns = tableColumn.get(j);
				if (count > 0) {
					break;
				}
				if (count == 2) {

					tableRows.findElement(By.xpath("//input[contains(@id,'description-textbox')]")).clear();
					tableRows.findElement(By.xpath("//input[contains(@id,'description-textbox')]")).sendKeys("NYSE");
					matchfound = true;
					count++;
					break;

				}

				else {

					System.out.println("Not Found");
				}

			}

		}

	}

	public static String ReadFromExcel(String controlValue, String reqValue) throws IOException {
		Sheet uniqueNumberSheet = null;
		String uniqueTestcaseID = "";
		HashMap<String, Integer> uniqueValuesHashMap = null;
		String uniqueNumber = null;

		if (!isClaimsApplication) {
			controlValue = "";
		}

		try {
			uniqueNumberSheet = getSheet(Config.transactionInfo, "DataSheet");
			uniqueValuesHashMap = getValueFromHashMap(uniqueNumberSheet);
			int rowCount = uniqueNumberSheet.getPhysicalNumberOfRows();

			for (int rIndex = 1; rIndex < rowCount; rIndex++) {
				if (controlValue.equals("") || (isClaimsApplication && controlValue.equals("Y"))) {
					uniqueTestcaseID = getCellData("TestCaseID", uniqueNumberSheet, rIndex, uniqueValuesHashMap);
				} else {
					uniqueTestcaseID = controlValue;
				}
				if (controller.controllerTestCaseID.toString().equals(uniqueTestcaseID)) {
					return uniqueNumber = getCellData(reqValue, uniqueNumberSheet, rIndex, uniqueValuesHashMap);

				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			controller.pauseFun(e.getMessage() + " from ReadFromExcel Function");
		}
		return uniqueNumber;
	}

	/**
	 * Return XLS sheet or XLSX sheet based on whether it is billing application
	 * or PAS application. This method can be changed to to use filePath
	 * extension to decide the same. Will take call on the same later.
	 * 
	 * @param FilePath
	 * @param SheetName
	 * @return
	 * @throws IOException
	 */
	public static Sheet getSheet(String FilePath, String SheetName) throws IOException {
		if (isBillingApplication) {
			return ExcelUtility.GetSheet(FilePath, SheetName);
		} else if (isClaimsApplication || isPASApplication) {
			return ExcelUtility.getXLSSheet(FilePath, SheetName);
		}
		return null;
	}

	//
	// Meghna-Check if there is data in values sheet to input in webtable//
	// public static String checkTable(String logicalName , HSSFRow
	// rowValues,ArrayList<Integer> valuesheetrowsnum)
	public static String checkTable(String logicalName, Row rowValues, ArrayList<Integer> valuesheetrowsnum) {
		LinkedHashMap<String, Object> Operatectrlvalues = new LinkedHashMap<String, Object>();
		LinkedHashMap<String, Object> Searchctrlvalues = new LinkedHashMap<String, Object>();
		LinkedHashMap<String, Object> OperateControlType = new LinkedHashMap<String, Object>();
		// HSSFDataFormat TIformat = null;
		XSSFDataFormat TIformat = null;
		String rowvalue = "";

		try {

			InputStream TImyXls = new FileInputStream(WebHelper.TIFilePath);
			// HSSFWorkbook TIworkBook = new HSSFWorkbook(TImyXls);
			XSSFWorkbook TIworkBook = new XSSFWorkbook(TImyXls);
			TIformat = TIworkBook.createDataFormat();
			// HSSFSheet TIsheetStructure = TIworkBook.getSheet("Values");
			XSSFSheet TIsheetStructure = TIworkBook.getSheet("Values");

			String[] ExcelLogicalName = logicalName.split("\\|");
			int logicalnameLen = ExcelLogicalName.length;
			String ctrlValue = null;

			for (int valuesheetrow : valuesheetrowsnum) {
				Operatectrlvalues.clear();
				OperateControlType.clear();

				for (int i = 0; i < logicalnameLen; i += 3) // for getting
															// target columns
															// and values
				{
					// System.out.println("XXX");
					ctrlValue = getCellData((ExcelLogicalName[i + 2].toString()), TIsheetStructure, valuesheetrow, WebHelper.valuesHeader);

					Operatectrlvalues.put(ExcelLogicalName[i], ctrlValue);
					OperateControlType.put(ExcelLogicalName[i], ExcelLogicalName[i + 1]);
					rowvalue = rowvalue + ctrlValue;

				}

				log.info("Target column ctrlvalues are : " + Operatectrlvalues);
				log.info("Target column ctrltypes are : " + OperateControlType);
				if (rowvalue.equals("")) {
					log.info("As per value sheet, there is nothing to input on application further and hence coming out of TableInput function");

				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.info(e.getLocalizedMessage());
		}

		return rowvalue;
	}

	// Meghna--Check If there is data in VAlues sheet to find//
	// public static String CheckFind(String logicalName , HSSFRow rowValues)
	public static String CheckFind(String logicalName, Row rowValues) {
		LinkedHashMap<String, Object> Searchctrlvalues = new LinkedHashMap<String, Object>();
		String[] ExcelLogicalName = logicalName.split("\\|");
		String[] excelHeader = null;
		String[] actualHeader = null;
		String Sctrl = "";
		excelHeader = ExcelLogicalName[0].split("\\:");
		actualHeader = ExcelLogicalName[1].split("\\:");
		int actualHeaderLen = excelHeader.length;

		String ctrlValue = null;
		for (int i = 0; i < actualHeaderLen; i++) {
			Cell ctrlValuecell = rowValues.getCell(Integer.parseInt(WebHelper.valuesHeader.get(excelHeader[i]).toString()));
			DataFormatter fmt = new DataFormatter();
			if (ctrlValuecell == null) {
				ctrlValue = "";
			} else {
				int type = ctrlValuecell.getCellType();
				switch (type) {
				case XSSFCell.CELL_TYPE_BLANK:
					ctrlValue = "";
					break;
				case XSSFCell.CELL_TYPE_NUMERIC:
					ctrlValue = fmt.formatCellValue(ctrlValuecell);
					break;
				case XSSFCell.CELL_TYPE_STRING:
					ctrlValue = ctrlValuecell.getStringCellValue();
					break;
				case XSSFCell.CELL_TYPE_BOOLEAN:
					ctrlValue = Boolean.toString(ctrlValuecell.getBooleanCellValue());
					break;
				case XSSFCell.CELL_TYPE_ERROR:
					ctrlValue = "error";
					break;
				case XSSFCell.CELL_TYPE_FORMULA:
					ctrlValue = ctrlValuecell.getCellFormula();
					break;

				}
			}
			Searchctrlvalues.put(actualHeader[i], ctrlValue);
			Sctrl = Sctrl + ctrlValue;
		}

		if (Sctrl.equals("")) {
			log.info("As per value sheet, there is nothing to be searched on application and hence coming out of FIND function");
		}
		return Sctrl;
	}

	public static Reporter WriteToDetailResults(String expectedValue, String actualValue, String columnName) throws IOException {
		Reporter report = new Reporter();
		report.setReport(report);
		report = report.getReport();
		String passCount = "";
		String failCount = "";
		report.setTestcaseId(controller.controllerTestCaseID.toString());
		report.setTrasactionType(WebHelper.transactionType.toString());
		report.setTestDescription(controller.testDesciption);
		if (expectedValue.equalsIgnoreCase(actualValue)) {
			report.setActualValue(actualValue);
			report.setExpectedValue(expectedValue);
			report.setStatus("PASS");
			report.setToDate(Config.dtFormat.format(WebHelper.frmDate));
			passCount = "1";
			failCount = "0";
		} else {
			report.setActualValue("FAIL|" + actualValue + "|" + expectedValue);
			report.setExpectedValue(expectedValue);
			report.setStatus("FAIL");
			report.setToDate(Config.dtFormat.format(WebHelper.frmDate));
			failCount = "1";
			passCount = "0";
			// DS:30-05-2014
			WebHelper.fieldVerFailCount += 1;
		}

		if (WebHelper.file.exists() == false) {
			WebHelper.print = new PrintStream(WebHelper.file);
		}

		WebHelper.print = new PrintStream(new FileOutputStream(WebHelper.file, true));
		int usedRows = count(WebHelper.file);
		if (usedRows == 0) {
			WebHelper.print.print("Iteration,TestCaseID,TransactionType,CurrentDate,RowType,Status,PassCount,FailCount");
			WebHelper.print.println();
		}
		usedRows = count(WebHelper.file);

		WebHelper.print.print(ExcelUtility.myChar + Config.cycleNumber + ExcelUtility.myChar + "," + ExcelUtility.myChar + report.getTestcaseId()
				+ ExcelUtility.myChar + "," + ExcelUtility.myChar + report.getTrasactionType() + ExcelUtility.myChar + "," + ExcelUtility.myChar
				+ report.getToDate() + ExcelUtility.myChar + "," + ExcelUtility.myChar + "Field: " + columnName + ExcelUtility.myChar + ","
				+ ExcelUtility.myChar + report.getStatus() + ExcelUtility.myChar + "," + ExcelUtility.myChar + passCount + ExcelUtility.myChar + ","
				+ ExcelUtility.myChar + failCount + ExcelUtility.myChar + "," + ExcelUtility.myChar + report.getActualValue() + ExcelUtility.myChar);
		WebHelper.print.println();
		return report;
	}

	/** This Functions Waits for the HTMLPage To Load **/
	public static Boolean waitForCondition() throws IOException {
		ExpectedCondition<Boolean> expCondition = null;

		try {
			// wait = new
			// WebDriverWait(currentdriver,Integer.parseInt(Automation.timeOut.toString()));//Integer.parseInt(Automation.timeOut.toString())
			expCondition = new ExpectedCondition<Boolean>() {

				@Override
				public Boolean apply(WebDriver driver) {
					return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
				}
			};
		} catch (WebDriverException e) {
			log.error(e.getMessage(), e);
			controller.pauseFun("From PageLoaded Function" + e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			controller.pauseFun("Timed Out after waiting");
		}

		return WebHelper.wait.until(expCondition);
	}

	public static List<Object> getPropertiesOfWebElement(WebElement webElement, String imageType) {
		List<WebElement> elements = webElement.findElements(By.tagName(imageType));
		WebElement element = elements.get(0);
		List<Object> elementProperties = new ArrayList<Object>();
		String elementType = element.getAttribute("type");
		String elementTagName = element.getTagName();
		// String elementClassName = element.getClass().toString();
		String controlType = "";
		String id = "";
		String name = "";
		if (elementType.equals("text") && elementTagName.equals("input")) {
			id = element.getAttribute("id");
			name = element.getAttribute("name");
			controlType = "WebEdit";
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
		} else if (elementType.contains("") && elementTagName.equals("a")) {
			id = element.getAttribute("id");
			name = element.getAttribute("name");
			controlType = "WebLink";
		}
		elementProperties.add(id);
		elementProperties.add(name);
		elementProperties.add(controlType);
		elementProperties.add((Object) element);
		return elementProperties;
	}

	public static void saveScreenShot() {
		if (!(WebHelper.currentdriver instanceof TakesScreenshot)) {

			log.info("Not able to take screenshot: Current WebDriver does not support TakesScreenshot interface.");
			return;
		}

		File scrFile;
		try {

			scrFile = ((TakesScreenshot) WebHelper.currentdriver).getScreenshotAs(OutputType.FILE);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.info("Taking screenshot failed for: " + webDriver.getReport().getTestcaseId());
			// e.printStackTrace();
			return;
		}
		String date = null;
		String Screenshotcycledate = null;

		if (StringUtils.isNotBlank(webDriver.getReport().getFromDate()))
			date = webDriver.getReport().getFromDate().replaceAll("[-/: ]", "");
		else {
			webDriver.getReport().setFromDate(Config.dtFormat.format(new Date()));
		}

		if (webDriver.getReport().getTestcaseId() == null || webDriver.getReport().getTestcaseId() == "") {
			webDriver.getReport().setTestcaseId("Common");
		}

		webDriver.getReport().setCycleDate(controller.cycleDateCellValue);

		if (webDriver.getReport().getCycleDate() == null || webDriver.getReport().getCycleDate() == "") {
			webDriver.getReport().setCycleDate("Common");
			Screenshotcycledate = "Common";
		} else {
			Screenshotcycledate = webDriver.getReport().getCycleDate().replace("/", "_");
		}

		String fileName = webDriver.getReport().getTestcaseId() + "_" + webDriver.getReport().getTrasactionType() + "_" + Screenshotcycledate;
		String location = Config.resultFilePath + "\\ScreenShots\\" + fileName + "_" + WebHelper.screenshotnum + ".jpeg";
		WebHelper.screenshotnum = WebHelper.screenshotnum + 1;
		// bhaskar
		controller.FailScreen = location.toString();
		// bhaskar
		webDriver.getReport().setScreenShot("file:\\\\" + location);

		try {

			FileUtils.copyFile(scrFile, new File(location));

		} catch (IOException e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
			return;
		}

	}

	public static Boolean writeToExcel(String ctrlValue, WebElement webElement, String controlId, String controlType, String controlName,
			String rowNo, String colNo) throws Exception {
		Workbook uniqueWB = null;
		try {
			log.info("^^^^^^^^^^^^^^^ctrlValue^^^^^^^^^^^^:" + ctrlValue);

			// *** Added by Meghana
			String writeToCol = null;
			if (isBillingApplication) {
				writeToCol = ctrlValue;

				// *** Added by Meghana
				if (ctrlValue == "") {

					log.info("Column in which value is to be written is not provided");
					log.info("Value not written");
					return true;
				}

				// ***
			} else if (isClaimsApplication || isPASApplication) {
				writeToCol = WebHelper.columnName;
			}
			FileInputStream in = new FileInputStream(Config.transactionInfo.toString());
			uniqueWB = createWorkbook(in);
			Sheet uniqueNumberSheet = uniqueWB.getSheet("DataSheet");
			HashMap<String, Integer> uniqueValuesHashMap = getValueFromHashMap(uniqueNumberSheet);
			Row uniqueRow = null;
			int rowNum = uniqueNumberSheet.getPhysicalNumberOfRows();
			log.info("%%%%%%%%*********" + rowNum);

			// ***

			for (int rIndex = 0; rIndex < rowNum; rIndex++) {

				uniqueRow = uniqueNumberSheet.getRow(rIndex);
				String uniqueTestcaseID = getCellData("TestCaseID", uniqueNumberSheet, rIndex, uniqueValuesHashMap);

				if (controller.controllerTestCaseID.toString().equals(uniqueTestcaseID))// &&
																						// MainController.controllerTransactionType.toString().equals(uniqueTransactionType)
				{
					uniqueRow = uniqueNumberSheet.getRow(rIndex);
					break;
				} else if (rIndex == rowNum - 1) {
					uniqueRow = uniqueNumberSheet.createRow(rowNum);
				}
			}

			if (controlType.equalsIgnoreCase("WebTable")) {
				if (StringUtils.isBlank(rowNo) || StringUtils.isBlank(colNo)) {
					controller.pauseFun("RowNumber or ColumnNumber is Missing");
					return false;
				} else {
					ctrlValue = WebHelper.currentdriver.findElement(By.xpath(controlName + "/tr[" + rowNo + "]/td[" + colNo + "]")).getText();
				}
			} else if (controlType.equalsIgnoreCase("ListBox") || controlType.equalsIgnoreCase("WebList")) {

				ctrlValue = new Select(webElement).getFirstSelectedOption().toString();
			} else if (controlType.equalsIgnoreCase("DB")) {

				log.info(ctrlValue);
			} else if (isBillingApplication && controlType.equalsIgnoreCase("WebService")) {// ***Meghana
				ctrlValue = WebService.getXMLTagValue(controlName);
				// ***
			} else if (Config.projectName.equals("DistributionManagement")) {
				// Minaakshi : 06-12-2018
				ctrlValue = webElement.getText();

				if (ctrlValue.equalsIgnoreCase(null) || ctrlValue.equalsIgnoreCase("")) {
					ctrlValue = webElement.getAttribute("value");
				}

			} else {
				ctrlValue = webElement.getText();
				log.info("uniquenumbersheet:" + ctrlValue);
			}

			Cell uniqueTestCaseID = uniqueRow.createCell(uniqueValuesHashMap.get("TestCaseID"));
			Cell uniqueCell = uniqueRow.createCell(uniqueValuesHashMap.get(writeToCol));

			uniqueTestCaseID.setCellValue(controller.controllerTestCaseID.toString());
			uniqueCell.setCellValue(ctrlValue);
			in.close();
			FileOutputStream out = new FileOutputStream(Config.transactionInfo);
			uniqueWB.write(out);

		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			throw e;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			try {
				uniqueWB.close();
			} catch (Exception ex) {
			}
		}
		return true;
	}

	public static Workbook createWorkbook(InputStream in) throws IOException {
		if (isBillingApplication) {
			return new XSSFWorkbook(in);
		} else if (isClaimsApplication || isPASApplication) {
			return new HSSFWorkbook(in);
		}
		return null;
	}

	@SuppressWarnings("resource")
	public static void GetCellInfo(String structurePath, String FilePath, Row rowValues, int valuesRowIndex, int valuesRowCount,
			String TransactionType, String TestCaseID, String operationType, ArrayList<Integer> valuesheetrows) {
		try {
			WebHelper.TIFilePath = FilePath;
			if (webDriver.getReport().getDriver() != null) {
				WebHelper.currentdriver = webDriver.getReport().getDriver();
			}

			if (isBillingApplication) {
				WebHelperBilling.implementWait();
			} else if (isClaimsApplication) {
				WebHelperClaims.implementWait();
			}
			WebHelper.frmDate = new Date();
			log.info("********************************" + TransactionType + " start DateTime is " + WebHelper.frmDate);
			WebHelper.isDynamicNumFound = true;

			InputStream myXls = new FileInputStream(FilePath);
			Workbook workBook = createWorkbook(myXls);
			WebHelper.format = workBook.createDataFormat();

			// Meghna:R10.10-For Common Structure Sheet - Get Structure sheet
			// from common path
			Sheet sheetStructure = null;
			if (structurePath != null && structurePath.trim().length() > 0) {
				log.info("STRUCTURE FILE PATH IS : " + structurePath);
				InputStream strucFile = new FileInputStream(structurePath);
				Workbook wbStruc = createWorkbook(strucFile);
				sheetStructure = wbStruc.getSheet("Structure");
			} else {
				sheetStructure = workBook.getSheet("Structure");
			}

			int rowCount = sheetStructure.getLastRowNum() + 1;
			Sheet headerValues = getSheet(FilePath, "Values");
			// log.info(Automation.dtFormat.format(frmDate));
			String fromDate = Config.dtFormat.format(WebHelper.frmDate);
			WebHelper.Config_endComparison = Config.endComparison;
			webDriver.getReport().setFromDate(fromDate);
			WebHelper.structureHeader = getValueFromHashMap(sheetStructure);
			WebHelper.columnName = null;
			int dynamicIndexNumber;// Added for Action Loop
			String imageType, indexVal, controlName, executeFlag, action, logicalName, controltype, controlID, dynamicIndex, newDynamicIndex, rowNo, colNo;// newly
			webDriver.getReport().setMessage("");
			webDriver.getReport().setStatus("PASS");
			// log.info("in method GetCellInfo 2");
			// log.info("MainController.pauseExecution:"+MainController.pauseExecution);
			for (int rowIndex = 1; rowIndex < rowCount && !controller.pauseExecution; rowIndex++) {
				// structureRow = sheetStructure.getRow(rowIndex);
				controlName = getCellData("ControlName", sheetStructure, rowIndex, WebHelper.structureHeader);// structureRow.getCell(3);
				executeFlag = getCellData("ExecuteFlag", sheetStructure, rowIndex, WebHelper.structureHeader);// structureRow.getCell(0);

				if (executeFlag.toString().equals("Y")) {
					imageType = getCellData("ImageType", sheetStructure, rowIndex, WebHelper.structureHeader);
					action = getCellData("Action", sheetStructure, rowIndex, WebHelper.structureHeader);// structureRow.getCell(1);
					logicalName = getCellData("LogicalName", sheetStructure, rowIndex, WebHelper.structureHeader);// structureRow.getCell(2);
					controltype = getCellData("ControlType", sheetStructure, rowIndex, WebHelper.structureHeader);// structureRow.getCell(4);
					controlID = getCellData("ControlID", sheetStructure, rowIndex, WebHelper.structureHeader);// structureRow.getCell(6);
					indexVal = getCellData("Index", sheetStructure, rowIndex, WebHelper.structureHeader);// structureRow.getCell(7);
					WebHelper.columnName = getCellData("ColumnName", sheetStructure, rowIndex, WebHelper.structureHeader);
					rowNo = getCellData("RowNo", sheetStructure, rowIndex, WebHelper.structureHeader);
					colNo = getCellData("ColumnNo", sheetStructure, rowIndex, WebHelper.structureHeader);
					dynamicIndex = getCellData("DynamicIndex", sheetStructure, rowIndex, WebHelper.structureHeader);// Added

					// *** Mandar -- For the below given code in GPbilling its
					// "FinalSubmitButton" -- need to verify ***
					if (logicalName.equalsIgnoreCase("PolicyNo")) {
						System.out.println("stop");
					}
					if (logicalName.equalsIgnoreCase("FinalSubmitButton")) {
						System.out.println("stop");
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
					// bhaskar Looping START
					if (action.equalsIgnoreCase("LOOP_VALUE")) {
						for (int valuesheetrow : valuesheetrows) {
							rowIndex = rowIndex + 1;
							for (int structurerowIndex = rowIndex; structurerowIndex < rowCount; structurerowIndex++) {
								action = getCellData("Action", sheetStructure, structurerowIndex, WebHelper.structureHeader);
								logicalName = getCellData("LogicalName", sheetStructure, structurerowIndex, WebHelper.structureHeader);
								controlName = getCellData("ControlName", sheetStructure, structurerowIndex, WebHelper.structureHeader);
								controltype = getCellData("ControlType", sheetStructure, structurerowIndex, WebHelper.structureHeader);
								controlID = getCellData("ControlID", sheetStructure, structurerowIndex, WebHelper.structureHeader);
								indexVal = getCellData("Index", sheetStructure, structurerowIndex, WebHelper.structureHeader);
								imageType = getCellData("ImageType", sheetStructure, structurerowIndex, WebHelper.structureHeader);
								rowNo = getCellData("RowNo", sheetStructure, structurerowIndex, WebHelper.structureHeader);
								colNo = getCellData("ColumnNo", sheetStructure, structurerowIndex, WebHelper.structureHeader);
								String ExFlag = getCellData("ExecuteFlag", sheetStructure, structurerowIndex, WebHelper.structureHeader);
								if (action.equalsIgnoreCase("LOOP_VALUE_END")) {
									WebHelper.loopvalueendIndex = structurerowIndex;
									rowIndex = rowIndex - 1;
									break;
								} else {
									if (ExFlag.equalsIgnoreCase("Y")) {
										WebHelper.loopexpectedRow = headerValues.getRow(valuesheetrow);
										try {
											calldoAction(headerValues, logicalName, WebHelper.loopexpectedRow, TransactionType, valuesheetrow,
													action, controlName, sheetStructure, structurerowIndex, TestCaseID, controltype, controlID,
													indexVal, imageType, FilePath, rowCount, rowNo, colNo, operationType);
										} catch (Exception e) {
											log.error(
													" Exception is handled for TransactionType" + TransactionType + " <-|-> TestCaseID : "
															+ TestCaseID + " <-|->BusinessDate " + controller.businessDate
															+ " <-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message "
															+ e.getMessage() + " <-|-> Cause " + e.getCause(), e);
											webDriver.getReport().setMessage(e.getLocalizedMessage());
											webDriver.getReport().setStatus("FAIL");
											log.error(" Exception is handled for TransactionType" + TransactionType + " <-|-> TestCaseID : "
													+ TestCaseID + " <-|->BusinessDate " + controller.businessDate + " <-|-> LocalizeMessage "
													+ e.getLocalizedMessage() + " <-|-> Message " + e.getMessage() + " <-|-> Cause " + e.getCause());
											controller.recoveryhandler();
											return;
											// Bhaskar Exception handle in
											// calldoAction method end
											// 02/21/2017
										}
									}
								}
							}
						}
						rowIndex = WebHelper.loopvalueendIndex;
						continue;
					}
					try {
						boolean ifRetryRequired = true;
						if (TransactionType.startsWith("WebService") || TransactionType.startsWith("RestService")) {
							calldoAction(headerValues, logicalName, rowValues, TransactionType, valuesRowIndex, action, controlName,
									sheetStructure, rowIndex, TestCaseID, controltype, controlID, indexVal, imageType, FilePath, rowCount,
									rowNo, colNo, operationType);
							ifRetryRequired = false;
						}
						if (ifRetryRequired) {
							for (retryCounter = 1; retryCounter <= retryCount; retryCounter++) {
								try {
									calldoAction(headerValues, logicalName, rowValues, TransactionType, valuesRowIndex, action, controlName,
											sheetStructure, rowIndex, TestCaseID, controltype, controlID, indexVal, imageType, FilePath, rowCount,
											rowNo, colNo, operationType);
									break;
								} catch (Exception exception) {
									log.warn("Retry counter = " + retryCounter);
									log.error(exception.getMessage(), exception);
									if (isLastRetry()) {
										throw exception;
									}
									Thread.sleep(5000);
								}
							}
						}
					}

					catch (Exception e) {
						log.error(e.getLocalizedMessage(), e);
						if (isClaimsApplication) {
							webDriver.getReport().setMessage(e.getLocalizedMessage());
							// }
							webDriver.getReport().setStatus("FAIL");
							log.error(" Exception is handled for TransactionType" + TransactionType + " <-|-> TestCaseID : " + TestCaseID
									+ " <-|->BusinessDate " + controller.businessDate + " <-|-> LocalizeMessage " + e.getLocalizedMessage()
									+ " <-|-> Message " + e.getMessage() + " <-|-> Cause " + e.getCause(), e);
							controller.pauseFun(e.getMessage());// Tanvi :
																// 4/13/2017
							return;
							// Bhaskar Exception handle in calldoAction method
							// End
							// 02/13/2017
						} else if (isBillingApplication) {

							// Added to login again in case login fails for the
							// first time - Meghna
							if (TransactionType.equals("Login") | TransactionType.equals("Login2")) {
								if (WebHelper.loginCnt < 3) {
									TransactionType = "Login"; // For login2
																// results
																// issue
									WebHelper.loginCnt = WebHelper.loginCnt + 1;
									log.info("Login again in case of failure");
									Automation.setUp();
									GetCellInfo(structurePath, FilePath, rowValues, valuesRowIndex, valuesRowCount, TransactionType, TestCaseID,
											operationType, valuesheetrows);
									log.info("Log In");
								}
								WebHelper.loginCnt = 0;
								return;

							}
							// Added to login again in case login fails for the
							// first time

							if (WebService.getErrorCodeAndMessage() == null || WebService.getErrorCodeAndMessage().equals("")) {
								log.info(e.getLocalizedMessage());// Mandar
								webDriver.getReport().setMessage(e.getLocalizedMessage());
							} else {
								log.info(WebService.getErrorCodeAndMessage());
								webDriver.getReport().setMessage(WebService.getErrorCodeAndMessage());
							}
							WebService.setErrorCodeAndMessage(null);// Mandar 25/09/2017 for rebill -- 17/11/2017
							webDriver.getReport().setStatus("FAIL");
							log.error(" Exception is handled for TransactionType " + TransactionType + " <-|-> TestCaseID : " + TestCaseID
									+ " <-|->BusinessDate " + controller.businessDate + " <-|-> LocalizeMessage " + e.getLocalizedMessage()
									+ " <-|-> Message " + e.getMessage() + " <-|-> Cause " + e.getCause(), e);
							controller.recoveryhandler();
							// For Reporting UI Validation Failure Message in
							// Summary Result----Meghna--01/12/2017
							if (action.equalsIgnoreCase("VV")) {
								webDriver.getReport().setMessage("Expected Message : " + WebHelper.inputValue.toUpperCase() + " not displayed");
							}
							// For Reporting UI Validation Failure Message in
							// Summary Result----Meghna--01/12/2017
							return;

							// Bhaskar Exception handle in calldoAction method
							// End
							// 02/13/2017
						}
					}
					// bhaskar Looping END

					if (action == "END_LOOP" && (valuesRowCount != valuesRowIndex)) {
						WebHelper.loopRow = 1;
						break;
					}

				} else {
					log.info("ExecuteFlag is N");
				}
			}

			if (!WebHelper.transactionType.toString().equalsIgnoreCase("Login")
					&& !WebHelper.transactionType.toString().equalsIgnoreCase("ChangeBusinessDate")) {
				Date toDate = new Date();
				webDriver.getReport().setFromDate(Config.dtFormat.format(WebHelper.frmDate));
				webDriver.getReport().setIteration(Config.cycleNumber);
				webDriver.getReport().setTestcaseId(controller.controllerTestCaseID.toString());
				webDriver.getReport().setGroupName(controller.controllerGroupName.toString());
				webDriver.getReport().setTrasactionType(controller.controllerTransactionType.toString());
				// bhaskar Added Cycle Date in Summaryresults START
				webDriver.getReport().setCycleDate(controller.cycleDateCellValue);
				// bhaskar Added Cycle Date in Summaryresults END
				if (WebHelper.webserviceFailed != true) {
					webDriver.getReport().setTestDescription(controller.testDesciption);
				} else {
					webDriver.getReport().setTestDescription(WebHelper.ErrDescription);
				}
				webDriver.getReport().setToDate(Config.dtFormat.format(toDate));

				// Setting status for field verification failures
				if (WebHelper.fieldVerFailCount > 0) {
					webDriver.getReport().setMessage("Check Detailed Results");
					webDriver.getReport().setStatus("FAIL");
				}
			} else if (WebHelper.transactionType.toString().equalsIgnoreCase("Login")) {
				webDriver.getReport().setTrasactionType("Login");
				if (isClaimsApplication) {
					String toDate = Config.dtFormat.format(new Date()); // Tanvi
					// :
					// 4/25/2017
					webDriver.getReport().setToDate(toDate);// Tanvi : 4/25/2017
				}
			} else if (WebHelper.transactionType.toString().equalsIgnoreCase("ChangeBusinessDate")) {
				webDriver.getReport().setTrasactionType("ChangeBusinessDate");
			}
		}

		catch (IOException e) {
			controller.pauseFun(e.getMessage());
			webDriver.getReport().setMessage(e.getLocalizedMessage());
			webDriver.getReport().setStatus("FAIL");
			log.error(" Exception is handled for TransactionType" + TransactionType + " <-|-> TestCaseID : " + TestCaseID + " <-|->BusinessDate "
					+ controller.businessDate + " <-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message " + e.getMessage()
					+ " <-|-> Cause " + e.getCause(), e);
		}

		catch (Exception e) {

			// - Devishree for testing error -- 11/09/2017
			controller.pauseFun(e.getMessage());
			webDriver.getReport().setMessage(e.getLocalizedMessage());
			webDriver.getReport().setStatus("FAIL");
			log.error(" Exception is handled for TransactionType" + TransactionType + " <-|-> TestCaseID : " + TestCaseID + " <-|->BusinessDate "
					+ controller.businessDate + " <-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message " + e.getMessage()
					+ " <-|-> Cause " + e.getCause(), e);
		} finally {
			log.info("Completed Transaction : " + WebHelper.transactionType.toString());
			WebHelper.structureHeader.clear();
			WebHelper.valuesHeader.clear();
			try {
				ExcelUtility.writeReport(webDriver.getReport());
			} catch (IOException ioe) {
				log.error("Failed to ExcelUtility.writeReport in getCellInfo <-|-> LocalizeMessage " + ioe.getLocalizedMessage() + " <-|-> Message "
						+ ioe.getMessage() + " <-|-> Cause " + ioe.getCause(), ioe);
			} catch (Exception e) {
				log.error("Faile while writting ExcelUtility.writeReport in getCellInfo <-|-> LocalizeMessage " + e.getLocalizedMessage()
						+ " <-|-> Message " + e.getMessage() + " <-|-> Cause " + e.getCause(), e);
			}
			WebHelper.fieldVerFailCount = 0;
		}
	}

	public static boolean isLastRetry() {
		if (retryCounter == retryCount) {
			return true;
		}
		return false;
	}

	private static void calldoAction(Sheet headerValues, String logicalName, Row rowValues, String TransactionType, int valuesRowIndex,
			String action, String controlName, Sheet sheetStructure, int rowIndex, String TestCaseID, String controltype, String controlID,
			String indexVal, String imageType, String FilePath, int rowCount, String rowNo, String colNo, String operationType) throws Exception {
		if (isBillingApplication) {
			WebHelperBilling.calldoAction(headerValues, logicalName, rowValues, TransactionType, valuesRowIndex, action, controlName, sheetStructure,
					rowIndex, TestCaseID, controltype, controlID, indexVal, imageType, FilePath, rowCount, rowNo, colNo, operationType);
		} else if (isClaimsApplication) {
			WebHelperClaims.calldoAction(headerValues, logicalName, rowValues, TransactionType, valuesRowIndex, action, controlName, sheetStructure,
					rowIndex, TestCaseID, controltype, controlID, indexVal, imageType, FilePath, rowCount, rowNo, colNo, operationType);
		}
	}

	static boolean stringIn(String sourceString, String[] stringList) {
		if (sourceString == null) {
			return false;
		}
		for (String string : stringList) {
			if (sourceString.equalsIgnoreCase(string)) {
				return true;
			}
		}
		return false;
	}

	// ***********************Unused Methods Below************************
	// *******************************************************************

	public static boolean ButtonClick(WebElement webelement) {
		boolean result = false;
		int attempts = 0;
		while (attempts < 2) {
			try {
				webelement.click();
				result = true;
				break;
			} catch (StaleElementReferenceException e) {
				attempts++;
			}

		}
		return result;
	}

	public static Boolean fluentWait(WebElement element) {

		Wait<WebElement> wait = new FluentWait<WebElement>(element)

		.withTimeout(60, TimeUnit.SECONDS)

		.pollingEvery(5, TimeUnit.SECONDS)

		.ignoring(NoSuchElementException.class);

		Boolean foo = wait.until(new Function<WebElement, Boolean>() {

			public Boolean apply(WebElement element) {

				return element.isEnabled();

			}

		});
		return foo;
	}

	public static String getText(WebDriver driver, WebElement element) {

		return (String) ((JavascriptExecutor) driver).executeScript("return jQuery(arguments[0]).text();", element);
	}

	public static String getHTMLResponse() {
		// String fileContents = Files.toString(new
		// File("D:\\eclipse\\JSFiles\\Sync.js"), Charsets.UTF_8);
		JavascriptExecutor js = (JavascriptExecutor) Automation.driver;
		Automation.driver.manage().timeouts().setScriptTimeout(120, TimeUnit.SECONDS);
		String url = Automation.driver.getCurrentUrl();
		String body = (String) js.executeAsyncScript(

		"var url = arguments[0];" + "var callback = arguments[1];" + "$.ajax({url: url,type:'Get',success: callback});", url);

		return body;

	}

	public static String getHTMLResponse1(String url) {
		// String fileContents = Files.toString(new
		// File("D:\\eclipse\\JSFiles\\Sync.js"), Charsets.UTF_8);
		// var progressElem = $('#progressCounter');
		return url;
	}

	public static void JSONResponse1(String url) {

		try {

			HttpPost request = new HttpPost(url);
			HttpClient internalClient = HttpClientBuilder.create().build();
			long startTime = System.currentTimeMillis();
			org.apache.http.HttpResponse baseResponse = (org.apache.http.HttpResponse) internalClient.execute(request);
			long duration = System.currentTimeMillis() - startTime;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	public static void HttpResponse(String url) throws IllegalStateException, IOException {

		try {

			// URL obj = new URL(url);

			HttpClient client = HttpClientBuilder.create().build();
			HttpGet request = new HttpGet(url);
			org.apache.http.HttpResponse response = client.execute(request);

			response.getParams();

			System.out.println("Response Code : " + ((org.apache.http.HttpResponse) response).getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(new InputStreamReader(((org.apache.http.HttpResponse) response).getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	public static void WebServer() {

		try {
			// constants
			URL url1 = new URL(Automation.driver.getCurrentUrl());
			// String message = new JSONObject().toString();
			String message = "add";
			HttpsURLConnection conn = (HttpsURLConnection) url1.openConnection();
			conn.setRequestMethod("POST");
			conn.connect();
			conn.disconnect();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			System.out.println("Unable to connect");
		}

	}

	public static void InputValue1(WebElement webElement, String value) throws InterruptedException {
		// Located element and stored It's reference In variable.

		try {
			WebElement Search_Box = webElement;
			int count = 1;
			int k = 1;
			for (int i = count; i < 5; i++) {

				count++;
				// ((JavascriptExecutor)Automation.driver).executeScript("window.focus();");
				Search_Box.clear();
				Thread.sleep(1000);
				Search_Box.clear();
				Search_Box.sendKeys(Keys.TAB);

				Search_Box.clear();

				Search_Box.sendKeys(Keys.TAB);

				// Search_Box.click();

				Search_Box.sendKeys(Keys.chord(Keys.CONTROL, "a"));

				Search_Box.sendKeys(value);

				Search_Box.sendKeys(Keys.TAB);

				Thread.sleep(1000);

				if (!Search_Box.getAttribute("value").isEmpty()) {

					break;
				}

				else {

					Thread.sleep(5000);

				}

			}

		} catch (StaleElementReferenceException e) {
			e.toString();
			System.out.println("Trying to recover from a stale element :" + e.getMessage());

		}
	}

	public static void ScrollTo(WebElement webelement) {

		try {
			if (webelement.isDisplayed()) {
				Coordinates coordinate = ((Locatable) webelement).getCoordinates();
				coordinate.onPage();
				coordinate.inViewPort();
			}
		} catch (Exception e) {
			System.out.println("Element does not exist");

		}

	}

	public static void doWork(Callback callback) {
		System.out.println("doing work");
		callback.call();
	}

	public interface Callback {
		void call();
	}

	public static String readUrl(String url) throws Exception {
		BufferedReader reader = null;
		try {
			URL obj = new URL(url);
			reader = new BufferedReader(new InputStreamReader(obj.openStream()));
			StringBuffer buffer = new StringBuffer();
			int read;
			char[] chars = new char[1024];
			while ((read = reader.read(chars)) != -1)
				buffer.append(chars, 0, read);

			return buffer.toString();
		} finally {
			if (reader != null)
				reader.close();
		}
	}

	public static void waitForPageLoadingToComplete() throws Exception {
		ExpectedCondition<Boolean> expectation = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {

				return ((JavascriptExecutor) Automation.driver).executeScript("return document.readyState").equals("complete");

			}

		};
		Wait<WebDriver> wait = new WebDriverWait(Automation.driver, 30);
		wait.until(expectation);
	}

	public static void waitForPageLoaded() {

		log.info("wait test");
	}

	public static void waitTillPageLoads() {
		Boolean readyStateComplete = false;
		JavascriptExecutor executor = (JavascriptExecutor) WebHelper.currentdriver;

		while (!readyStateComplete) {

			readyStateComplete = (executor.executeScript("return document.readyState").toString()).equals("loaded");
			log.info(executor.executeScript("return document.readyState").toString());
		}

		// return readyStateComplete;

	}

	public static Document readAndConvertAsDoc(String filepath) {
		try {
			File requestxmlfile = new File(filepath);

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(requestxmlfile);
			doc.getDocumentElement().normalize();
			return doc;
		} catch (Exception r) {
			webDriver.getReport().setMessage(r.getMessage());
		}
		return null;
	}

}
