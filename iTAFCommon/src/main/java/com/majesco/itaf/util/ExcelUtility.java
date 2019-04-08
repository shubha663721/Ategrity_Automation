package com.majesco.itaf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.majesco.itaf.main.Config;
import com.majesco.itaf.main.ITAFWebDriver;
import com.majesco.itaf.main.MainController;
import com.majesco.itaf.main.MainControllerPAS;
import com.majesco.itaf.main.ObjectFactory;
import com.majesco.itaf.main.WebHelper;
import com.majesco.itaf.main.WebHelperPAS;
import com.majesco.itaf.main.WebHelperUtil;
import com.majesco.itaf.verification.WebVerification;
import com.majesco.itaf.vo.Reporter;
import com.majesco.itaf.webservice.WebService;
 
public class ExcelUtility {

	private final static Logger log = Logger.getLogger(ExcelUtility.class
			.getName());
	public static char myChar = 34;

	public static int firstRow = 1;// newly Added code for Loop Action
	
	public static int DResult =1;

	// TODO Add getter setters for below members
	public static int dynamicNum = 0;
	public static ArrayList<Integer> TIvaluesheetrows = new ArrayList<Integer>();

	private static ITAFWebDriver webDriver = ITAFWebDriver.getInstance();
	private static MainController controller = ObjectFactory
			.getMainController();

	private static boolean isClaimsApplication = ITAFWebDriver
			.isClaimsApplication();

	// Reads the Values sheet from Input Excel and returns the row
	// public static HSSFRow GetDataFromValues(String FilePath,String
	// TestCaseID,String TransactionType,String cycleDate,String operationType)
	// throws IOException, InterruptedException,Exception
	// Meghna:R10.10-For Common Structure Sheet - Added argument structurePath
	public static Row GetDataFromValues(String structurePath, String FilePath,
			String TestCaseID, String TransactionType, String cycleDate,
			String operationType) throws IOException, InterruptedException,
			Exception // Meghna - fo XLS to XLSX
	{
		Row expectedRow = null;
		String tempRecovery_Scenario = null;
		Cell transactionType = null;
		Cell testCase_ID = null;
		HashMap<String, Integer> headerValues = new HashMap<>();
		Cell cycleDate_Values = null;
		Cell executeFlag_Values = null;
		Cell testCaseID_Values = null;

		Boolean firstRowFound = false;
		int singlerowindex = 0;
		int executeFlagcount = 0;

		try {
			Row currentRow = null;
			Sheet valuesSheet = WebHelperUtil.getSheet(FilePath, "Values");

			firstRowFound = false;
			ArrayList<Integer> valuesheetrows = new ArrayList<Integer>();

			int rowCount = valuesSheet.getLastRowNum() + 1;
			int rowCount1 = valuesSheet.getPhysicalNumberOfRows();
			log.info("Number of rows in value sheet are:" + rowCount);
			log.info("Testcase ID:" + TestCaseID);
			log.info("Transaction Type:" + TransactionType);
			log.info("Business Date:" + cycleDate);
			if (rowCount == 0) {
				// MainController.pauseExecution = true;
				controller.pauseFun("No data for transaction "
						+ TransactionType + " TestCase" + TestCaseID
						+ " Cycle date" + cycleDate + " in Values sheet");
			}

			// for(int
			// rowIndex=firstRow;rowIndex<firstRow+endRow&&!MainController.pauseExecution;rowIndex++)
			for (int rowIndex = 1; rowIndex < rowCount
					&& !controller.pauseExecution; rowIndex++) {

				// bhaskar Time Travel START
				currentRow = valuesSheet.getRow(rowIndex);

				if (headerValues.isEmpty() == true) {
					headerValues = WebHelperUtil
							.getValueFromHashMap(valuesSheet);
					log.info("headerValues : " + headerValues);
				}
				String TestCaseID_Values_Str = WebHelperUtil.getCellData(
						"TestCaseID", valuesSheet, rowIndex, headerValues);// structureRow.getCell(3);
				if (TestCaseID_Values_Str != "")// Added by devishree to avoid
												// null rows in value sheet.
				{

					testCaseID_Values = currentRow.getCell(headerValues
							.get("TestCaseID"));
					cycleDate_Values = currentRow.getCell(headerValues
							.get("CycleDate"));
					try {
						executeFlag_Values = currentRow.getCell(headerValues
								.get("ExecuteFlag"));
					} catch (Exception ex) {
						log.error("ExecuteFlag is null. Reading value from Execute_Status");
						executeFlag_Values = currentRow.getCell(headerValues
								.get("Execute_Status"));
					}
					transactionType = currentRow.getCell(headerValues
							.get("TransactionType"));
				
					testCase_ID = currentRow.getCell(headerValues
							.get("TestCaseID"));

					SimpleDateFormat cdFormat = new SimpleDateFormat(
							"dd-MMM-yyyy");
					DateFormat cycleDateFormat = new SimpleDateFormat(
							"MM/dd/yyyy");
					String CycleDate_Valuessheet = null;
					String CycleDate_MainController = null;
					if (!transactionType.toString().equalsIgnoreCase("Login")
							&& !transactionType.toString().equalsIgnoreCase(
									"ChangeBusinessDate")) {
						if (cycleDate_Values.toString().contains("-")) {
							Date CycleDate_Values = cdFormat
									.parse(cycleDate_Values.toString());
							CycleDate_Valuessheet = cycleDateFormat
									.format(CycleDate_Values);
						}

						// Tanvi :11/04/2017 :start
						else if (cycleDate_Values.toString().equalsIgnoreCase(
								"NA")
								|| cycleDate_Values.toString()
										.equalsIgnoreCase("")) {
							CycleDate_Valuessheet = "NA";
						} else
						// Tanvi :11/04/2017 :end
						{
							CycleDate_Valuessheet = cycleDate_Values.toString();
						}

						if (controller.cycleDateCellValue.toString().contains(
								"-")) {
							Date CycleDate_Main_Controller = cycleDateFormat
									.parse(controller.cycleDateCellValue
											.toString());
							CycleDate_MainController = cycleDateFormat
									.format(CycleDate_Main_Controller);
						}

						// Tanvi :11/04/2017 :start
						else if (controller.cycleDateCellValue.toString()
								.equalsIgnoreCase("NA"))

						{
							CycleDate_MainController = "NA";
						}

						else
						// Tanvi :11/04/2017 :end
						{
							CycleDate_MainController = controller.cycleDateCellValue
									.toString();
						}

						if ((testCaseID_Values.toString()
								.equalsIgnoreCase(controller.controllerTestCaseID
										.toString()))
								&& ((CycleDate_Valuessheet
										.equals(CycleDate_MainController)))
								&& (!executeFlag_Values.toString()
										.equalsIgnoreCase("Y"))
								&& (transactionType.toString()
										.equalsIgnoreCase(controller.controllerTransactionType
												.toString()))) {
							executeFlagcount = executeFlagcount + 1;
							log.info("executeFlagcount : " + executeFlagcount);
						}

						if ((!testCaseID_Values.toString().equalsIgnoreCase(
								controller.controllerTestCaseID.toString()))
								|| (!(CycleDate_Valuessheet
										.equals(CycleDate_MainController)))
								|| (!executeFlag_Values.toString()
										.equalsIgnoreCase("Y"))
								|| (!transactionType
										.toString()
										.equalsIgnoreCase(
												controller.controllerTransactionType
														.toString()))) {
							if (rowIndex == rowCount) {
								//
							}
							continue;
						}
					}
					// bhaskar Loop Purpose START
					if (firstRowFound == false) {
						expectedRow = valuesSheet.getRow(rowIndex);
						valuesheetrows.add(rowIndex);
						singlerowindex = rowIndex;
					} else {
						valuesheetrows.add(rowIndex);
					}
					log.info("row found :" + rowIndex);
					firstRowFound = true;
				
				}
			}
			// bhaskar Loop Purpose START
			if (executeFlagcount == 1) {
				webDriver.getReport().setMessage(
						"ExecuteFLag is N in value sheet");
				if (Config.recovery_scenario.equalsIgnoreCase("TRUE")) {
					tempRecovery_Scenario = Config.recovery_scenario;
					Config.recovery_scenario = "FALSE";
				}
				controller.pauseFun(webDriver.getReport().getMessage());
				webDriver.getReport().setMessage(
						webDriver.getReport().getMessage());
				ExcelUtility.writeReport(webDriver.getReport());
				executeFlagcount = 0;
				if (tempRecovery_Scenario == "TRUE") {
					Config.recovery_scenario = tempRecovery_Scenario;
				}
			} else

			if (valuesheetrows.isEmpty()) {
				webDriver.getReport()
						.setMessage("No data found in value sheet");
				controller.pauseFun(webDriver.getReport().getMessage());
				webDriver.getReport().setMessage(
						webDriver.getReport().getMessage());
				ExcelUtility.writeReport(webDriver.getReport());
			} else {
				TIvaluesheetrows = valuesheetrows;
				WebHelperUtil.GetCellInfo(structurePath, FilePath, expectedRow,
						singlerowindex, rowCount, TransactionType, TestCaseID,
						operationType, valuesheetrows);
			}
		}

		catch (IOException we) {
			log.error(we.getMessage(), we);
			webDriver.getReport().setMessage(we.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			webDriver.getReport().setMessage(e.getMessage());
		}
		// bhaskar Loop Purpose END
		return expectedRow;
	}

	// Reads the Values sheet from Input Excel and returns the row
	public static HSSFRow GetDataFromValues(String FilePath, String TestCaseID,
			String TransactionType) throws IOException, InterruptedException,
			Exception {
		HSSFRow expectedRow = null;
		HSSFSheet valuesSheet = getXLSSheet(FilePath, "Values");

		int rowCount = valuesSheet.getLastRowNum() + 1;

		int endRow = getRowCount(valuesSheet);

		if (endRow == 0) {
			controller.pauseExecution = true;
		}

		for (int rowIndex = firstRow; rowIndex < firstRow + endRow
				&& !controller.pauseExecution; rowIndex++) {
			log.info(rowIndex);
			expectedRow = valuesSheet.getRow(rowIndex);
			WebHelperPAS.GetCellInfo(FilePath, expectedRow, rowIndex, rowCount);
		}
		return expectedRow;
	}

	public static int getRowCount(Sheet valSheet, String cTransactionType,
			String operationtype) throws IOException {
		Cell testCaseID = null;
		Cell transactionType = null;
		int loopRowCount = 0;

		firstRow = 1;
		Boolean isFirstFound = false;
		int rowCount = valSheet.getLastRowNum() + 1;
		for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {
			Row row = valSheet.getRow(rowIndex);
			testCaseID = row.getCell(0);
			String tCase = null;

			if (testCaseID == null) {
				tCase = "";
			} else {
				tCase = testCaseID.toString();
			}
			transactionType = row.getCell(1);
			String tType = null;
			if (transactionType == null) {
				break;
			}
			tType = transactionType.toString();
			if (transactionType.toString().equalsIgnoreCase("Login")
					|| transactionType.toString().equalsIgnoreCase(
							"ChangeBusinessDate")) {
				if (firstRow == 1 && !isFirstFound) {
					firstRow = rowIndex;
					isFirstFound = true;
				}
				loopRowCount++;
			} else {
				log.info("Value TestCase ID is : " + tCase);
				log.info("MainController TestCase ID is :"
						+ controller.controllerTestCaseID.toString());
				log.info("Value Transaction is :" + tType);
				log.info("MainController Transaction is :"
						+ controller.controllerTransactionType.toString());
				if (testCaseID == null && transactionType == null) {
					break;
				}
				else if ((tCase
						.equalsIgnoreCase(controller.controllerTestCaseID
								.toString()) && tType
						.equals(controller.controllerTransactionType.toString()))
						|| (tType.equalsIgnoreCase(cTransactionType.toString()) && operationtype == "Capture")) {
					// bhaskar Capture Data END
					if (firstRow == 1 && !isFirstFound) {
						firstRow = rowIndex;
						isFirstFound = true;
					}
					loopRowCount++;
				} else if ((!tCase
						.equalsIgnoreCase(controller.controllerTestCaseID
								.toString()) || !tType
						.equalsIgnoreCase(controller.controllerTransactionType
								.toString()))
						&& (!isFirstFound && rowIndex == rowCount - 1)) {
					controller
							.pauseFun("TestCaseID Or Transaction Didn't Match "
									+ controller.controllerTestCaseID + " "
									+ controller.controllerTransactionType);
					ExcelUtility.writeReport(webDriver.getReport());
					break;

				}
			}
		}
		return loopRowCount;
	}

	public static int getRowCount(Sheet valSheet) throws IOException {
		Cell testCaseID = null;
		Cell transactionType = null;

		int loopRowCount = 0;
		int temp = 0;
		firstRow = 1;
		Boolean isFirstFound = false;
		// bhaskar
		Boolean isDefaultFound = false;
		// bhaskar
		int rowCount = valSheet.getLastRowNum() + 1;
		for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {
			Row row = valSheet.getRow(rowIndex);
			testCaseID = row.getCell(0);
			String testCase = null;
			if (testCaseID == null) {
				testCase = "";
			} else {
				testCase = testCaseID.toString();
			}
			transactionType = row.getCell(1);
			// bhaskar
			String tCase = testCase;
			String tType = transactionType.toString();
			if (tCase.toString().equalsIgnoreCase("DEFAULT")
					&& tType.toString().equalsIgnoreCase("Login")) {
				isDefaultFound = true;
				temp = rowIndex;
			}
			// bhaskar
			if (testCaseID == null && transactionType == null) {
				break;
			}

			else if (tCase.equalsIgnoreCase(controller.controllerTestCaseID
					.toString())
					&& tType.equals(controller.controllerTransactionType
							.toString())) {
				if (firstRow == 1 && !isFirstFound) {
					firstRow = rowIndex;
					isFirstFound = true;
				}
				loopRowCount++;
			} else if ((!tCase.equalsIgnoreCase(controller.controllerTestCaseID
					.toString()) || !tType
					.equals(controller.controllerTransactionType.toString()))
					&& (!isFirstFound && rowIndex == rowCount - 1)) {
				// bhaskar
				if (isDefaultFound == true) {
					firstRow = temp;
					isFirstFound = true;
					loopRowCount++;
				}
				// bhaskar
				else {
					controller
							.pauseFun("TestCaseID Or Transaction Didn't Match "
									+ controller.controllerTestCaseID + " "
									+ controller.controllerTransactionType);
					ExcelUtility.writeReportPAS(webDriver.getReport());
					break;
				}
			}
		}
		return loopRowCount;
	}

	// Reads Excel-Sheet values by taking Path and SheetName
	//@SuppressWarnings({ "resource", "unused", "finally" })
	// public static HSSFSheet GetSheet(String FilePath,String SheetName) throws
	// IOException, Exception
	public static XSSFSheet GetSheet(String FilePath, String SheetName)
			throws IOException // XX
	{
		XSSFWorkbook workBook = null;
		XSSFSheet workSheet = null;
		try {
			log.info("FilePath is : " + FilePath + " and SheetName is : "
					+ SheetName);
			InputStream myXls = new FileInputStream(FilePath);
			String extension = FilenameUtils.getExtension(FilePath);
			log.info("File extension is: " + extension);
			workBook = new XSSFWorkbook(myXls); // XX
			workSheet = workBook.getSheet(SheetName);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			controller.pauseFun("Failed to access  file " + FilePath
					+ " Sheet:  " + SheetName);
			log.error("Failed to access  file " + FilePath + "Sheet:  "
					+ SheetName + "<-|-> LocalizeMessage "
					+ e.getLocalizedMessage() + " <-|-> Message "
					+ e.getMessage() + " <-|-> Cause " + e.getCause());
			throw new IOException("Failed to access  file :" + FilePath
					+ "Sheet:  " + SheetName + "  <-|-> LocalizeMessage "
					+ e.getLocalizedMessage() + " <-|-> Message"
					+ e.getMessage() + " <-|-> Cause " + e.getCause());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.error("Failed to access  file " + FilePath + "Sheet:  "
					+ SheetName + "<-|-> LocalizeMessage "
					+ e.getLocalizedMessage() + " <-|-> Message "
					+ e.getMessage() + " <-|-> Cause " + e.getCause());
			controller.pauseFun("Failed to access  file " + FilePath
					+ "Sheet:  " + SheetName);
			return null;
		} finally {
			workBook.close();
		}
		return workSheet;
	}

	// Reads Excel-Sheet values by taking Path and SheetName
	public static HSSFSheet getXLSSheet(String FilePath, String SheetName)
			throws IOException {
		HSSFSheet workSheet = null;
		try {
			log.info("FilePath is:" + FilePath);
			log.info("SheetName is:" + SheetName);
			InputStream myXls = new FileInputStream(FilePath);
			HSSFWorkbook workBook = new HSSFWorkbook(myXls);
			workSheet = workBook.getSheet(SheetName);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			controller.pauseFun("Failed to access  file " + FilePath
					+ " Sheet:  " + SheetName);
			log.error("Failed to access  file " + FilePath + "Sheet:  "
					+ SheetName + "<-|-> LocalizeMessage "
					+ e.getLocalizedMessage() + " <-|-> Message "
					+ e.getMessage() + " <-|-> Cause " + e.getCause());
			throw new IOException("Failed to access  file :" + FilePath
					+ "Sheet:  " + SheetName + "  <-|-> LocalizeMessage "
					+ e.getLocalizedMessage() + " <-|-> Message"
					+ e.getMessage() + " <-|-> Cause " + e.getCause());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.error("Failed to access  file " + FilePath + "Sheet:  "
					+ SheetName + "<-|-> LocalizeMessage "
					+ e.getLocalizedMessage() + " <-|-> Message "
					+ e.getMessage() + " <-|-> Cause " + e.getCause());
			controller.pauseFun("Failed to access  file " + FilePath
					+ "Sheet:  " + SheetName);
			return null;
		}
		return workSheet;
	}

	// Bhaskar Read XLSM sheet START
	@SuppressWarnings("resource")
	public static XSSFSheet GetxlmSheet(String FilePath, String SheetName)
			throws IOException, Exception
	{
		XSSFSheet workSheet = null;
		// org.apache.poi.ss.usermodel.Sheet workSheet=null;
		try {
			log.info("FilePath is:" + FilePath);
			log.info("SheetName is:" + SheetName);
			InputStream myXls = new FileInputStream(FilePath);
			XSSFWorkbook wBook = new XSSFWorkbook(myXls);
			workSheet = wBook.getSheet(SheetName);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			controller.pauseFun("Failed to access  file " + FilePath
					+ " Sheet:  " + SheetName);
			log.error("Failed to access  file " + FilePath + "Sheet:  "
					+ SheetName + "<-|-> LocalizeMessage "
					+ e.getLocalizedMessage() + " <-|-> Message "
					+ e.getMessage() + " <-|-> Cause " + e.getCause());
			throw new IOException("Failed to access  file :" + FilePath
					+ "Sheet:  " + SheetName + "  <-|-> LocalizeMessage "
					+ e.getLocalizedMessage() + " <-|-> Message"
					+ e.getMessage() + " <-|-> Cause " + e.getCause());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			log.error("Failed to access  file " + FilePath + "Sheet:  "
					+ SheetName + "<-|-> LocalizeMessage "
					+ e.getLocalizedMessage() + " <-|-> Message "
					+ e.getMessage() + " <-|-> Cause " + e.getCause());
			controller.pauseFun("Failed to access  file " + FilePath
					+ "Sheet:  " + SheetName);
			return null;
		}
		return workSheet;
	}


	// Writes SummaryResults
	public static void writeReport(Reporter report) throws IOException {
		PrintStream print = null;
		try {
			// log.info(report.strMessage);
			report.setReport(report);
			String frmDate = report.getFromDate();
			File file = new File(Config.resultOutput);
			report = report.getReport();

			String lasttoDate = Config.dtFormat.format(new Date());
			report.setToDate(lasttoDate);


			// TM:19/01/2015-Changes made to remove ==null
			if (StringUtils.isBlank(report.getMessage()))
				report.setMessage("");

			// TM:19/01/2015-Changes made to remove ==null
			if (StringUtils.isBlank(report.getTestDescription()))
				report.setTestDescription("");

			if (report.getStatus() == "FAIL") {

				// For batch run getting marked as 'N'-- Added by Devishree
				// 16/02/2016
				if (report.getTrasactionType().equalsIgnoreCase(
						"RunBatch_JBEAM")) {
					System.out.println("Do not go in Recovery");
				}
				// Tanvi :11/04/2017 : Start
				else if (report.getTrasactionType().startsWith("Verify")) {
					System.out.println("Do not go in Recovery");
				}
				// Tanvi :11/04/2017 : End
				else {
					controller.recoveryhandler();
				}

			}

			if (report.getTrasactionType().equalsIgnoreCase(
					"ChangeBusinessdate")) {
				report.setTestcaseId("Common");
			}

			// TM:19/01/2015-Added for GroupName Blank
			if (StringUtils.isBlank(report.getGroupName()))
				report.setGroupName("");

			// if(iTAFSeleniumWeb.WebHelper.NoResponseFile==true)
			if (WebService.isNoResponseFileTrue()) {
				report.setMessage(WebHelper.description);
			}

			if (WebHelper.faultstring == true) {
				if (WebService.isNodeAvailable()) {
					report.setMessage("<" + WebService.getreportNodeValue()
							+ "> " + WebHelper.description);
				} else {
					report.setMessage(WebHelper.description);
				}
			}

			// if(MainController.controllerTransactionType.toString().startsWith("WebService"))
			if (report.getTrasactionType().startsWith("WebService")) {
				report.setScreenShot("");
			}

			if (WebHelper.success == true) {
				report.setMessage(WebHelper.description);
			}

			if (file.exists() == false) {
				print = new PrintStream(file);
			}
			int usedRows = WebHelperUtil.count(file);
			if (usedRows == 0) {
				// bhaskar Added Cycle Date in Summaryresults START
				print.print("Iteration,TestCaseID,CycleDate,TransactionType,TestCaseDesription,StartDate,EndDate,Status,ExecutionDescription,Screenshot");
				// bhaskar Added Cycle Date in Summaryresults END
				print.println();
			}
			usedRows = WebHelperUtil.count(file);
			print = new PrintStream(new FileOutputStream(file, true));
			// bhaskar Added Cycle Date in Summaryresults START

			print.print(myChar + Config.cycleNumber + myChar + "," + myChar
					+ report.getTestcaseId() + myChar + "," + myChar
					+ report.getCycleDate() + myChar + "," + myChar
					+ report.getTrasactionType() + myChar + "," + myChar
					+ report.getTestDescription() + myChar + "," + myChar
					+ frmDate + myChar + "," + myChar + report.getToDate()
					+ myChar + "," + myChar + report.getStatus() + myChar + ","
					+ myChar + report.getMessage() + myChar + "," + myChar
					+ report.getScreenShot() + myChar);
			// bhaskar Added Cycle Date in Summaryresults END
			print.println();

			//
			if (report.getTrasactionType().equalsIgnoreCase("Login")
					&& report.getStatus().equalsIgnoreCase("Fail")) {
				controller.pExecution = true;
			}

			if (report.getTrasactionType().equalsIgnoreCase(
					"ChangeBusinessdate")
					&& report.getStatus().equalsIgnoreCase("Fail")) {
				controller.pExecution = true;
			}

			if (report.getStatus().equalsIgnoreCase("Fail")) {
				System.out.print("HI");
			}
		}

		catch (IOException ie) {
			log.error(ie.getMessage(), ie);
			controller.pauseFun(ie.getMessage());
		} finally {
			webDriver.getReport().setScreenShot("");
		}
	}

	// Writes SummaryResults
	public static void writeReportPAS(Reporter report) throws IOException {
		PrintStream print = null;
		try {
			
			File file = new File(Config.resultOutput);
			/*if (file.exists() == true && DResult == 1) {
				// print = new PrintStream(file);
				file.delete();
			}*/
			
			if (file.exists() == true && StringUtils.equalsIgnoreCase(Config.appendResultOutput,"false") && DResult == 1){
				file.delete();
			}
			
			report.setReport(report);
			String frmDate = report.getFromDate();
			//File file = new File(Config.resultOutput);
			report = report.getReport();
			// Quote ID for PAS report requirement
			if (controller.controllerQuoteId != null) {
				report.setStrQuoteId(controller.controllerQuoteId.toString());
			} else {
				report.setStrQuoteId("");
			}
			// TM:19/01/2015-Changes made to remove ==null
			if (StringUtils.isBlank(report.getMessage()))
				report.setMessage("");

			// TM:19/01/2015-Changes made to remove ==null
			if (StringUtils.isBlank(report.getTestDescription()))
				report.setTestDescription("");

			// TM:19/01/2015-Added for GroupName Blank
			if (StringUtils.isBlank(report.getGroupName()))
				report.setGroupName("");

			print = new PrintStream(new FileOutputStream(file,true));
			
			int usedRows = WebHelperUtil.count(file);
			if (usedRows == 0) {
				print.print("GroupName,Iteration,TestCaseID,TransactionType,TestCaseDesription,StartDate,EndDate,Status,Description,Screenshot,Quote Number");
				print.println();
			}
			usedRows = WebHelperUtil.count(file);
			print = new PrintStream(new FileOutputStream(file, true));
			print.print(myChar + report.getGroupName() + myChar + "," + myChar
					+ Config.cycleNumber + myChar + "," + myChar
					+ report.getTestcaseId() + myChar + "," + myChar
					+ report.getTrasactionType() + myChar + "," + myChar
					+ report.getTestDescription() + myChar + "," + myChar
					+ frmDate + myChar + "," + myChar + report.getToDate()
					+ myChar + "," + myChar + report.getStatus() + myChar + ","
					+ myChar + report.getMessage() + myChar + "," + myChar
					+ report.getScreenShot() + myChar + "," + myChar
					+ report.getStrQuoteId() + myChar);
			print.println();
			DResult++;
		} catch (IOException ie) {
			log.error(ie.getMessage(), ie);
			controller.pauseFun(ie.getMessage());
		} finally {
			webDriver.getReport().setScreenShot("");
		}
	}

	// Compares the Actual and Expected Sheets cell-wise.
	public static Reporter CompareExcel(Sheet actualSheet, Sheet expectedSheet,
			List<String> columns, List<String> columnsData, String testCaseID,
			String transactionType, String operationtype, String cycleDate)
			throws IOException {

		List<String> status = new ArrayList<String>();
		List<String> rowStatus = new ArrayList<String>();
		List<String> actualValue = new ArrayList<String>();
		List<List<String>> actualRows = new ArrayList<List<String>>();
		List<Integer> passCounts = new ArrayList<Integer>();
		List<Integer> failCounts = new ArrayList<Integer>();
		String expectedHeaderValue = null;
		String actualHeaderValue = null;

		boolean isrowFound = false;
		// bhaskar CAPTURE START
		// int expSheetRowCount =getRowCount(expectedSheet);
		// //expectedSheet.getPhysicalNumberOfRows();
		int expSheetRowCount = ExcelUtility.getRowCount(expectedSheet,
				transactionType, operationtype); // expectedSheet.getPhysicalNumberOfRows();
		// bhaskar CAPTURE END
		Reporter report = new Reporter();
		report.setReport(report);
		int passCount = 0;
		int failCount = 0;
		int colCount = 0;

		for (int rowIndex = firstRow; rowIndex < firstRow + expSheetRowCount; rowIndex++) {
			passCount = 0;
			failCount = 0;
			int currentRow = ++WebVerification.currentRowIndex;

			Row actualRow = actualSheet.getRow(currentRow);
			Row expectedRow = expectedSheet.getRow(rowIndex);
			// bhaskar HTML dashboard START
			Row actualRowHeader = actualSheet.getRow(0);
			Row expectedRowHeader = expectedSheet.getRow(0);
			// bhaskar HTML dashboard END
			// Tanvi :11/04/2017 : start
			if (isClaimsApplication) {
				try {
					for (int i = 0; i <= 3; i++) {
						System.out.println(actualRow.getCell(i).toString());
						System.out.println(expectedRow.getCell(i).toString());
						if (actualRow.getCell(i).toString() == null) {
							break;
						}
						if (expectedRow.getCell(i).toString() == null) {
							break;
						}

					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
					break;
				}
			}
			// Tanvi :11/04/2017 : End

			if (actualRow.getCell(0).toString()
					.equals(expectedRow.getCell(0).toString())
					&& actualRow.getCell(1).toString()
							.equals(expectedRow.getCell(1).toString())
					&& actualRow.getCell(2).toString()
							.equals(expectedRow.getCell(2).toString())
					&& (!isClaimsApplication || actualRow.getCell(3).toString()
							.equals(expectedRow.getCell(3).toString()))) {

				isrowFound = true;
				actualValue = new ArrayList<String>();
				if (actualRow == null || expectedRow == null) {
					break;
				}
				colCount = expectedRow.getPhysicalNumberOfCells();
				int startColIndex = isClaimsApplication ? 5 : 3;

				for (int columnIndex = startColIndex; columnIndex < colCount; columnIndex++) {
					Cell actualCell = actualRow.getCell(columnIndex);
					DataFormatter fmt = new DataFormatter();
					Cell expectedCell = expectedRow.getCell(columnIndex);
					Cell actualCellHeader = actualRowHeader
							.getCell(columnIndex);
					DataFormatter fmt1 = new DataFormatter();
					Cell expectedCellHeader = expectedRowHeader
							.getCell(columnIndex);

					if (actualCellHeader != null || expectedCellHeader != null) {
						expectedHeaderValue = fmt1
								.formatCellValue(expectedCellHeader);
						actualHeaderValue = fmt1
								.formatCellValue(actualCellHeader);
						log.info("bhaskar dashboard value of actual sheet:"
								+ expectedHeaderValue);
						log.info("bhaskar dashboard value of expected sheet:"
								+ actualHeaderValue);
						if (expectedHeaderValue == actualHeaderValue) {
							log.info("Column Headers of actual and expected sheet are :"
									+ expectedHeaderValue);
						}
					}
					// TM: Following 'if' is replacement of the above
					if (actualCell != null || expectedCell != null) {
						String expectedValue = fmt
								.formatCellValue(expectedCell);
						// bhaskar HTML dashboard START
						String stractualValue = fmt.formatCellValue(actualCell);
						log.info("bhaskar dashboard value of actual sheet:"
								+ stractualValue);
						log.info("bhaskar dashboard value of expected sheet:"
								+ expectedValue);
						// bhaskar HTML dashboard END
						if (!actualCell.toString().equalsIgnoreCase(
								expectedValue)) {
							report.setStatus("FAIL");
							report.setStatus(report.getStatus());
							failCount += 1;
							report.setActualValue("FAIL |" + expectedValue
									+ "|" + actualCell.toString());
							// bhaskar HTML dashboard START
							report.setMessage("Values Not Matched");
							// bhaskar CAPTURE START
							ExcelUtility
									.WriteToCompareDetailResults(testCaseID,
											transactionType, columns,
											actualRows, expSheetRowCount,
											colCount, report, expectedValue,
											stractualValue, actualHeaderValue,
											operationtype, cycleDate);
							// bhaskar CAPTURE END
							// bhaskar HTML dashboard END
						} else {
							passCount += 1;
							report.setStatus("PASS");
							report.setStatus(report.getStatus());
							report.setActualValue(actualCell.toString());
							log.info(actualCell.toString());
							// bhaskar HTML dashboard START
							report.setMessage("Values Matched");
							ExcelUtility
									.WriteToCompareDetailResults(testCaseID,
											transactionType, columns,
											actualRows, expSheetRowCount,
											colCount, report, expectedValue,
											stractualValue, actualHeaderValue,
											operationtype, cycleDate);
							// bhaskar CAPTURE END
							// bhaskar HTML dashboard END
						}
						status.add(report.getStatus());
						actualValue.add(report.getActualValue());
					}

				}
				if (status.contains("FAIL")) {
					report.setStatus("FAIL");
				} else {
					report.setStatus("PASS");
				}
				status.clear();
				rowStatus.add(report.getStatus());
				passCounts.add(passCount);
				failCounts.add(failCount);
				actualRows.add(actualValue);
				report.setReport(report);
			} else if (isrowFound == false) {
				continue;
				/*
				 * MainController.pauseFun("No Rows Found For Comparision");
				 * break;
				 */
			}
		}
		if (rowStatus.contains("FAIL")) {
			report.setStatus("FAIL");
			if (isClaimsApplication) {
				report.setMessage("Values Not Matched");
			}
		} else {
			if (isClaimsApplication) {
				report.setStatus("PASS");
			}
		}
		if (!isClaimsApplication) {
			cycleDate = null;
		}
		ExcelUtility.WriteToDetailResults(testCaseID, transactionType, columns,
				actualRows, passCounts, failCounts, expSheetRowCount, colCount,
				report, rowStatus, operationtype, cycleDate);
		passCounts.clear();
		failCounts.clear();
		return report;
	}

	// Compares the Actual and Expected Sheets cell-wise.
	public static Reporter CompareExcel(Sheet actualSheet, Sheet expectedSheet,
			List<String> columns, List<String> columnsData, String testCaseID,
			String transactionType) throws IOException {
		boolean isrowFound = false;
		int expSheetRowCount = getRowCount(expectedSheet); // expectedSheet.getPhysicalNumberOfRows();
		Reporter report = new Reporter();
		report.setReport(report);
		int passCount = 0;
		int failCount = 0;
		int colCount = 0;

		List<String> status = new ArrayList<String>();
		List<String> rowStatus = new ArrayList<String>();
		List<List<String>> actualRows = new ArrayList<List<String>>();
		List<Integer> passCounts = new ArrayList<Integer>();
		List<Integer> failCounts = new ArrayList<Integer>();

		for (int rowIndex = firstRow; rowIndex < firstRow + expSheetRowCount; rowIndex++) {
			passCount = 0;
			failCount = 0;
			int currentRow = ++WebVerification.currentRowIndex;
			Row actualRow = actualSheet.getRow(currentRow);
			Row expectedRow = expectedSheet.getRow(rowIndex);
			List<String> actualValue = new ArrayList<String>();

			if (actualRow.getCell(0).toString()
					.equals(expectedRow.getCell(0).toString())
					&& actualRow.getCell(1).toString()
							.equals(expectedRow.getCell(1).toString())) {

				isrowFound = true;
				actualValue = new ArrayList<String>();
				if (actualRow == null || expectedRow == null) {
					break;
				}
				if (Config.projectName.equals("DistributionManagement")) {// Minaakshi
					colCount = expectedRow.getLastCellNum();
				} else {
					colCount = expectedRow.getPhysicalNumberOfCells();
				}
				for (int columnIndex = 3; columnIndex < colCount; columnIndex++) {
					Cell actualCell = actualRow.getCell(columnIndex);
					DataFormatter fmt = new DataFormatter();
					Cell expectedCell = expectedRow.getCell(columnIndex);
					// TM: commented the code to find replacement of continue
					/*
					 * if(actualCell == null || expectedCell == null) {
					 * continue; }
					 */
					// TM: Following 'if' is replacement of the above
					if (actualCell != null || expectedCell != null) {
						String expectedValue = fmt
								.formatCellValue(expectedCell);
						if (Config.projectName.equals("DistributionManagement")) {// Minaakshi
							if (expectedValue.contains("|")) {
								
								//***Change done by DM Team*** - 14-11-2018
								//expectedValue = DMProduct.getDynamicExpectedValue(expectedValue);
								expectedValue = DMProduct.getDynamicExpectedValue(expectedValue,actualCell.toString());
								//***//									
								
							}
							if (expectedValue.contains("TEXT")) {//Minaakshi : 04-01-2019
								expectedValue = DMProduct.getExpectedValueByFormula(expectedValue,actualCell.toString(),expectedSheet,expectedCell);
							}
						}

						//***Change done by DM Team*** - 14-11-2018
						if (expectedValue.equalsIgnoreCase("IGNORE")){//Minaakshi : 14-11-2018
							passCount += 1;
							report.setStatus("PASS");
							report.setStatus(report.getStatus());
							report.setActualValue(expectedValue + "|" + actualCell.toString());
												
						}
							else if (!actualCell.toString().equalsIgnoreCase(
								expectedValue)) {
							report.setStatus("FAIL");
							report.setStatus(report.getStatus());
							failCount += 1;
							report.setActualValue("FAIL |" + expectedValue
									+ "|" + actualCell.toString());
						} else {
							passCount += 1;
							report.setStatus("PASS");
							report.setStatus(report.getStatus());
							report.setActualValue(actualCell.toString());
							System.out.println(actualCell.toString());
						}
						status.add(report.getStatus());
						actualValue.add(report.getActualValue());
					}

				}
				if (status.contains("FAIL")) {
					report.setStatus("FAIL");
				} else {
					report.setStatus("PASS");
				}
				status.clear();
				rowStatus.add(report.getStatus());
				passCounts.add(passCount);
				failCounts.add(failCount);
				actualRows.add(actualValue);
				report.setReport(report);
			} else if (isrowFound == false) {
				continue;
				/*
				 * MainController.pauseFun("No Rows Found For Comparision");
				 * break;
				 */
			}
		}
		if (rowStatus.contains("FAIL")) {
			report.setStatus("FAIL");
		}
		WriteToDetailResults(testCaseID, transactionType, columns, actualRows,
				passCounts, failCounts, expSheetRowCount, colCount, report,
				rowStatus);
		passCounts.clear();
		failCounts.clear();
		return report;
	}

	public static void WriteToDetailResults(String testCaseID,
			String transactionType, List<String> columns,
			List<List<String>> actualRows, List<Integer> passCounts,
			List<Integer> failCounts, int rowCount, int colCount,
			Reporter report, List<String> status, String operationtype,
			String cycleDate) throws IOException {
		PrintStream print = null;
		try {
			report = report.getReport();
			report.setFromDate(Config.dtFormat.format(WebHelper.frmDate));
			report.setTestcaseId(controller.controllerTestCaseID.toString());
			// bhaskar CAPTURE START
			if (operationtype == "Capture") {
				report.setTrasactionType(transactionType.toString());
			} else {
				report.setTrasactionType(controller.controllerTransactionType
						.toString());
			}
			// bhaskar CAPTURE END
			report.setStatus(report.getStatus());

			if (WebHelper.file.exists() == false) {
				// log.info("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				print = new PrintStream(WebHelper.file);
			}

			/*
			 * else {
			 */
			// Tripti: Below 3 lines should be outside of else
			columns.remove("TestCaseID");
			columns.remove("TransactionType");
			columns.remove("CurrentDate");
			// }

			print = new PrintStream(new FileOutputStream(WebHelper.file, true));
			int usedRows = WebHelperUtil.count(WebHelper.file);
			if (usedRows == 0) {
				// TM: Added println instead of print
				if (isClaimsApplication) {
					print.println("Iteration,CyclDate,TestCaseID,TransactionType,CurrentDate,RowType,Status,PassCount,FailCount");
				} else {
					print.println("Iteration,TestCaseID,TransactionType,CurrentDate,RowType,Status,PassCount,FailCount");
				}
			}
			usedRows = WebHelperUtil.count(WebHelper.file);

			String strCycleDate = isClaimsApplication ? myChar + cycleDate
					+ myChar + "," : "";

			print.print(myChar + Config.cycleNumber + myChar + ","
					+ strCycleDate + myChar + report.getTestcaseId() + myChar
					+ "," + myChar + report.getTrasactionType() + myChar + ","
					+ myChar + report.getFromDate() + myChar + "," + myChar
					+ "Header" + myChar + "," + myChar + report.getStatus()
					+ myChar + "," + myChar + "" + myChar + "," + myChar + ""
					+ myChar);
			int counter = 0;
			while (columns.isEmpty() == false) {
				if (counter != columns.size()) {
					print.print("," + myChar + columns.get(counter) + myChar);
					counter++;
				} else {
					break;
				}
			}
			print.println();
			rowCount = actualRows.size();
			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				print.print(myChar + Config.cycleNumber + myChar + ","
						+ strCycleDate + myChar + report.getTestcaseId()
						+ myChar + "," + myChar + report.getTrasactionType()
						+ myChar + "," + myChar + report.getFromDate() + myChar
						+ "," + myChar + "Data" + myChar + "," + myChar
						+ status.get(rowIndex).toString() + myChar + ","
						+ myChar + passCounts.get(rowIndex) + myChar + ","
						+ myChar + failCounts.get(rowIndex) + myChar);
				counter = 0;
				while (actualRows.isEmpty() == false) {
					if (counter != actualRows.get(rowIndex).size()) {
						System.out.print(actualRows.get(rowIndex).get(counter));
						print.print("," + myChar
								+ actualRows.get(rowIndex).get(counter)
								+ myChar);
						counter++;
					} else {
						break;
					}
				}
				print.println();

			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			controller.pauseFun(e.getMessage());

		} finally {
			actualRows.clear();
			status.clear();
			columns.clear();

		}

	}

	// Writes WebVerification Results to the Excel Sheet

	public static void WriteToDetailResults(String testCaseID,
			String transactionType, List<String> columns,
			List<List<String>> actualRows, List<Integer> passCounts,
			List<Integer> failCounts, int rowCount, int colCount,
			Reporter report, List<String> status) throws IOException {
		PrintStream print = null;
		try {
			report = report.getReport();
			report.setFromDate(Config.dtFormat.format(WebHelper.frmDate));
			report.setTestcaseId(controller.controllerTestCaseID.toString());
			report.setTrasactionType(controller.controllerTransactionType
					.toString());
			report.setStatus(report.getStatus());

			if (WebHelper.file.exists() == false) {
				System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
				print = new PrintStream(WebHelper.file);
			}
			/*
			 * else {
			 */
			// Tripti: Below 3 lines should be outside of else
			columns.remove("TestCaseID");
			columns.remove("TransactionType");
			columns.remove("CurrentDate");
			// }

			print = new PrintStream(new FileOutputStream(WebHelper.file, true));
			int usedRows = WebHelperUtil.count(WebHelper.file);
			if (usedRows == 0) {
				// TM: Added println instead of print
				print.println("Iteration,TestCaseID,TransactionType,CurrentDate,RowType,Status,PassCount,FailCount");
			}
			usedRows = WebHelperUtil.count(WebHelper.file);

			print.print(myChar + Config.cycleNumber + myChar + "," + myChar
					+ report.getTestcaseId() + myChar + "," + myChar
					+ report.getTrasactionType() + myChar + "," + myChar
					+ report.getFromDate() + myChar + "," + myChar + "Header"
					+ myChar + "," + myChar + report.getStatus() + myChar + ","
					+ myChar + "" + myChar + "," + myChar + "" + myChar);
			int counter = 0;
			while (columns.isEmpty() == false) {
				if (counter != columns.size()) {
					print.print("," + myChar + columns.get(counter) + myChar);
					counter++;
				} else {
					break;
				}
			}
			print.println();
			rowCount = actualRows.size();
			for (int rowIndex = 0; rowIndex < rowCount; rowIndex++) {
				print.print(myChar + Config.cycleNumber + myChar + "," + myChar
						+ report.getTestcaseId() + myChar + "," + myChar
						+ report.getTrasactionType() + myChar + "," + myChar
						+ report.getFromDate() + myChar + "," + myChar + "Data"
						+ myChar + "," + myChar
						+ status.get(rowIndex).toString() + myChar + ","
						+ myChar + passCounts.get(rowIndex) + myChar + ","
						+ myChar + failCounts.get(rowIndex) + myChar);
				counter = 0;
				while (actualRows.isEmpty() == false) {
					if (counter != actualRows.get(rowIndex).size()) {
						System.out.print(actualRows.get(rowIndex).get(counter));
						print.print("," + myChar
								+ actualRows.get(rowIndex).get(counter)
								+ myChar);
						counter++;
					} else {
						break;
					}
				}
				print.println();

			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			controller.pauseFun(e.getMessage());

		} finally {
			actualRows.clear();
			status.clear();
			columns.clear();

		}

	}

	public static void WriteToCompareDetailResults(String testCaseID,
			String transactionType, List<String> columns,
			List<List<String>> columnsData, int rowCount, int colCount,
			Reporter report, String expectedValue, String stractualValue,
			String actualHeaderValue, String operationtype, String cycleDate)
			throws IOException
	// bhaskar CAPTURE END
	{
		PrintStream print = null;
		try {
			report = report.getReport();
			report.setFromDate(Config.dtFormat.format(WebHelper.frmDate));
			report.setTestcaseId(controller.controllerTestCaseID.toString());
			// bhaskar CAPTURE START
			if (operationtype == "Capture") {
				report.setTrasactionType(transactionType.toString());
			} else {
				report.setTrasactionType(controller.controllerTransactionType
						.toString());
			}
			// bhaskar CAPTURE END

			report.setStatus(report.getStatus());
			report.setMessage(report.getMessage());

			if (WebHelper.dashboardfile.exists() == false) {
				// log.info("#########################################");
				print = new PrintStream(WebHelper.dashboardfile);
			}

			print = new PrintStream(new FileOutputStream(
					WebHelper.dashboardfile, true));
			int usedRows = WebHelperUtil.count(WebHelper.dashboardfile);
			log.info("bhaskar dashboard execution" + usedRows);
			if (usedRows == 0) {
				// TM: Added println instead of print
				print.println("Iteration,CycleDate,TransactionName,TestCaseID,Date,RowType,Status,Message,FieldName,ExpectedValue,ActualValue");
			}
			usedRows = WebHelperUtil.count(WebHelper.dashboardfile);
			log.info("bhaskar dashboard execution" + usedRows);

			// print.println();

			print.println(myChar + Config.cycleNumber + myChar + "," + myChar
					+ cycleDate + myChar + "," + myChar
					+ report.getTrasactionType() + myChar + "," + myChar
					+ report.getTestcaseId() + myChar + "," + myChar
					+ report.getFromDate() + myChar + "," + myChar
					+ report.getTrasactionType() + "_1" + myChar + "," + myChar
					+ report.getStatus() + myChar + "," + myChar
					+ report.getMessage() + myChar + "," + myChar
					+ actualHeaderValue + myChar + "," + myChar + expectedValue
					+ myChar + "," + myChar + stractualValue + myChar);
			// print.println();

		}

		catch (Exception e) {
			log.error(e.getMessage(), e);
			controller.pauseFun(e.getMessage());

		} finally {
			columns.clear();
			columnsData.clear();

		}

	}
	/**
	 * This method copy the content of current workbook to destination workbook
	 * @param studentsSheet - Source workbook
	 * @param name - Current worksheet name
	 * @param rows - number of rows in current worksheet
	 * @param output_file - destination file outputstream to write the content
	 * @param destWorksheet - destination worksheet
	 * @throws IOException
	 */
	public static void copyFileToCreateNewOne(HSSFWorkbook studentsSheet,
			String name, int rows, FileOutputStream output_file,
			HSSFSheet destWorksheet) throws IOException {
		for (int i = 0; i <= rows; i++) {
			HSSFSheet worksheet = studentsSheet.getSheet(name);
			if (i == 0) {

				createOnlyHeader(worksheet, destWorksheet);

			} else {
				copyToFileOnlyData(worksheet, destWorksheet, i);

			}
			studentsSheet.write(output_file);
		}
	}

	
	/**
	 * This method copied the header row from source excel worksheet to destination worksheet
	 * @param currentWorksheet - source worksheet
	 * @param destWorksheet - destination worksheet
	 */
	public static void createOnlyHeader(HSSFSheet currentWorksheet,
			HSSFSheet destWorksheet) {
		// create the header
		HashMap<String, Integer> map = WebHelperUtil
				.getValueFromHashMap(currentWorksheet);

		Set<Entry<String, Integer>> set = map.entrySet();

		Iterator<Entry<String, Integer>> iter = set.iterator();
		HSSFRow destRow = destWorksheet.createRow(0);
		while (iter.hasNext()) {
			Entry<String, Integer> key = iter.next();
			HSSFCell cellA1 = destRow.createCell(key.getValue().intValue());
			cellA1.setCellValue(key.getKey());
		}
	}

	
	/**
	 * This method creates the header for report excel file
	 * @param currentWorksheet - String array of header details
	 * @param destWorksheet - Worksheet where the header will be populated
	 */
	public static void createOnlyHeaderFromInput(String[] currentWorksheet,
			HSSFSheet destWorksheet) {
		// create the header
		HSSFRow destRow = destWorksheet.createRow(0);
		for (int col = 0; col < currentWorksheet.length; col++) {
			HSSFCell cellA1 = destRow.createCell(col);
			cellA1.setCellValue(currentWorksheet[col]);
		}

	}

	

	/**
	 * This method copy the report data to Report excel sheet without header row.
	 * @param worksheet - current worksheet
	 * @param destWorksheet - destination worksheet
	 * @param row - row number where data to be written
	 */
	public static void copyToFileOnlyData(HSSFSheet worksheet,
			HSSFSheet destWorksheet, int row) {
		// create the records
		HSSFRow values = worksheet.getRow(row);
		int totalColumnCount = values.getLastCellNum();
		System.out.println("total columns in records" + totalColumnCount);
		HSSFRow dest1 = destWorksheet.createRow((short) row);
		for (int col = 0; col < totalColumnCount; col++) {
			HSSFCell cellA1 = dest1.createCell(col);

			HSSFCell root = values.getCell(col);
			DataFormatter fmt = new DataFormatter();

			String value = "";
			if (root != null) {
				int type = root.getCellType();
				value = selectDataType(root, fmt, value, type);
				System.out.println(value);
				cellA1.setCellValue(value);

			}
		}
	}
	
	/**
	 * This method copy the report data to Report excel sheet without header row.
	 * @param destWorksheet - Destination worksheet where data to be written
	 * @param report - value object to store business data to be written to report
	 * @param reportType - Type of report whether simple or detailed
	 */
	public static void copyToFileOnlyData(HSSFSheet destWorksheet,
			Reporter report, String reportType) {

		// create the records
		int row = destWorksheet.getLastRowNum();
		HSSFRow currentRow = destWorksheet.getRow(row);
		int totalColumnCount = destWorksheet.getRow(0).getLastCellNum();
		System.out.println("total columns in records" + totalColumnCount);
		HSSFRow dest1 = destWorksheet.createRow((short) (row + 1));

		for (int col = 0; col < totalColumnCount; col++) {
			HSSFCell cellA1 = dest1.createCell(col);

			HSSFCell root = currentRow.getCell(col);
			DataFormatter fmt = new DataFormatter();

			String value = "";
			if (root != null) {
				int type = root.getCellType();
				String reportValue = selectReportData(col, report, reportType);
				value = selectDataType(root, fmt, reportValue, type);
				System.out.println(value);
				cellA1.setCellValue(value);

			}
		}
	}
	/**
	 * This method returns the value to written to each cell in excel worksheet 
	 * @param col - column of the worksheet
	 * @param report - value object to store report data
	 * @param type - to be generated report type
	 * @return - return string representation of the data to written to worksheet
	 */
	private static String selectReportData(int col, Reporter report, String type) {
		if (type.equalsIgnoreCase("reports")) {
			if (col == 0) {
				return report.getGroupName();
			} else if (col == 1) {
				return Config.cycleNumber;
			} else if (col == 2) {
				return report.getTestcaseId();
			} else if (col == 3) {
				return report.getTrasactionType();
			} else if (col == 4) {
				return report.getTestDescription();
			} else if (col == 5) {
				return report.getFromDate();
			} else if (col == 6) {
				return report.getToDate();
			} else if (col == 7) {
				return report.getStatus();
			} else if (col == 8) {
				return report.getMessage();
			} else if (col == 9) {
				return report.getScreenShot();
			} else if (col == 10) {
				return report.getStrQuoteId();
			}
		} else if(type.equalsIgnoreCase("detailed")){
			if (col == 0) {
				return Config.cycleNumber;
			} else if (col == 1) {
				return report.getTestcaseId();
			} else if (col == 2) {
				return report.getTrasactionType();
			} else if (col == 3) {
				return report.getToDate();
			} else if (col == 4) {
				return report.getColumnName();
			} else if (col == 5) {
				return report.getStatus();
			} else if (col == 6) {
				return report.getPassCount();
			} else if (col == 7) {
				return report.getFailCount();
			} else if (col == 8) {
				return report.getActualValue();
			} else if (col == 9) {
				return report.getStrQuoteId();
			}
		}
		return null;
	}
	/**
	 * This method format the data type to display properly in excel worksheet
	 * @param root - Cell of workssheet where data to be found
	 * @param fmt - Date formatter used
	 * @param displayValue - raw data fethed from reported value object
	 * @param type - data type of the column in report worksheet
	 * @return formatted output string to be written to report
	 */
	private static String selectDataType(HSSFCell root, DataFormatter fmt,
			String displayValue, int type) {
		switch (type) {
		case Cell.CELL_TYPE_BLANK:
			displayValue = "";
			break;
		case Cell.CELL_TYPE_NUMERIC:
			displayValue = fmt.formatCellValue(root);
			break;
		case Cell.CELL_TYPE_STRING:
			displayValue = root.getStringCellValue();
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			displayValue = Boolean.toString(root.getBooleanCellValue());
			break;
		case Cell.CELL_TYPE_ERROR:
			displayValue = "error";
			break;
		case Cell.CELL_TYPE_FORMULA:
			displayValue = root.getCellFormula();
			break;
		}
		return displayValue;
	}

}
