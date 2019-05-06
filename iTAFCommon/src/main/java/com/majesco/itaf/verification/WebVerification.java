package com.majesco.itaf.verification;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;

import com.majesco.itaf.main.Automation;
import com.majesco.itaf.main.Config;
import com.majesco.itaf.main.ITAFWebDriver;
import com.majesco.itaf.main.ITAFWebDriverPAS;
import com.majesco.itaf.main.MainController;
import com.majesco.itaf.main.MainControllerClaims;
import com.majesco.itaf.main.ObjectFactory;
import com.majesco.itaf.main.WebHelper;
import com.majesco.itaf.main.WebHelperBilling;
import com.majesco.itaf.main.WebHelperClaims;
import com.majesco.itaf.main.WebHelperPAS;
import com.majesco.itaf.main.WebHelperUtil;
import com.majesco.itaf.util.Constants;
import com.majesco.itaf.util.DMProduct;
import com.majesco.itaf.util.ExcelUtility;
import com.majesco.itaf.util.JDBCConnection;
import com.majesco.itaf.vo.Reporter;
//XX
//

@SuppressWarnings("unused")
public class WebVerification {

	final static Logger log = Logger.getLogger(WebVerification.class.getName());
	private static HashMap<String, Integer> vTableListMap = new HashMap<String, Integer>();
	private static HashMap<String, Integer> templateMap = new HashMap<String, Integer>();
	private static List<String> columns = new ArrayList<String>();
	private static List<String> columnsData = new ArrayList<String>();
	private static Date vdate = null;
	private static HashMap<String, Integer> inputHashTable = new HashMap<String, Integer>();
	private static List<List<String>> rows = new ArrayList<List<String>>();
	public static int currentRowIndex = 0;
	private static boolean isTableFound = false;
	public static boolean isFromVerification = false;
	private static Date dt = null;
	private static String failedMsg = null;
	private static final String EXCEL_FILE_EXTENSTION = ".xlsx";
	private static final boolean isClaimsApplication = ITAFWebDriver.isClaimsApplication();

	private static MainController controller = ObjectFactory.getMainController();

	public static void performVerification(String transactionType, String testcaseID, String operationtype, String cycleDate) throws IOException,
			Exception {
		Sheet vTableSheet = WebHelperUtil.getSheet(Config.verificationTableISTPath, "VerificationTables");
		int rowCount = vTableSheet.getLastRowNum() + 1;
		vTableListMap = WebHelperUtil.getValueFromHashMap(vTableSheet);
		Reporter report = new Reporter();
		Sheet actualSheet = null;
		WebElement tableElement = null;
		List<WebElement> rowElements = null;
		String ActualPath = null;
		String duplicateActualPath = null;
		String expectedSheetPath = null;
		String SheetName = "ActualValues";
		vdate = new Date();
		WebHelper.frmDate = new Date();
		for (int rowIndex = 1; rowIndex < rowCount && !controller.pauseExecution; rowIndex++) {
			try {
				Row vRow = vTableSheet.getRow(rowIndex);
				String executeFlag = WebHelperUtil.getCellData("Verify", vTableSheet, rowIndex, inputHashTable);
				String vTransaction = WebHelperUtil.getCellData("TransactionType", vTableSheet, rowIndex, inputHashTable);

				if (isTableFound == true && !vTransaction.equalsIgnoreCase(transactionType)) {
					break;
				}

				if (executeFlag.toString().equalsIgnoreCase("Y") && vTransaction.toString().equalsIgnoreCase(transactionType.toString())) {
					isTableFound = true;
					String functionalFlag = WebHelperUtil.getCellData("Functional", vTableSheet, rowIndex, inputHashTable);
					String templateDir = WebHelperUtil.getCellData("TemplateAdditionalPath", vTableSheet, rowIndex, inputHashTable);
					String templateSheet = WebHelperUtil.getCellData("TemplateSheet", vTableSheet, rowIndex, inputHashTable);
					String expectedDirPath = WebHelperUtil.getCellData("ExpectedDataAdditionalPath", vTableSheet, rowIndex, inputHashTable);
					String expectedSheet = WebHelperUtil.getCellData("Expected", vTableSheet, rowIndex, inputHashTable);
					String templatePath = Config.verificationTemplatePath + templateDir.toString() + "\\" + templateSheet.toString();
					System.out.println(templatePath);

					ActualPath = Config.actualValuesValuesPath + expectedDirPath + "\\" + transactionType + "_Actual" + EXCEL_FILE_EXTENSTION;
					duplicateActualPath = Config.actualValuesValuesPath + expectedDirPath + "\\" + transactionType + "_Actual_duplicate"
							+ EXCEL_FILE_EXTENSTION;

					expectedSheetPath = Config.expectedValuesValuesPath + expectedDirPath.toString() + "\\" + expectedSheet.toString();
					Sheet layoutSheet = WebHelperUtil.getSheet(templatePath, "Layout");
					int templateRowCount = layoutSheet.getLastRowNum() + 1;
					templateMap = WebHelperUtil.getValueFromHashMap(layoutSheet);

					for (int templateIndex = 1; templateIndex < templateRowCount && !controller.pauseExecution; templateIndex++) {
						Row layoutRow = layoutSheet.getRow(templateIndex);
						String tableID = WebHelperUtil.getCellData("TableID", layoutSheet, templateIndex, inputHashTable);
						String tableType = WebHelperUtil.getCellData("TableType", layoutSheet, templateIndex, inputHashTable);
						String tableIDType = WebHelperUtil.getCellData("TableIDType", layoutSheet, templateIndex, inputHashTable);
						String startRow = WebHelperUtil.getCellData("StartRow", layoutSheet, templateIndex, inputHashTable);
						String endRow = WebHelperUtil.getCellData("EndRow", layoutSheet, templateIndex, inputHashTable);
						String columnName = WebHelperUtil.getCellData("ColumnName", layoutSheet, templateIndex, inputHashTable);
						String rowNo = WebHelperUtil.getCellData("Row", layoutSheet, templateIndex, inputHashTable);
						String colNo = WebHelperUtil.getCellData("Column", layoutSheet, templateIndex, inputHashTable);
						String controlName = WebHelperUtil.getCellData("ControlName", layoutSheet, templateIndex, inputHashTable);
						String controlType = WebHelperUtil.getCellData("ControlType", layoutSheet, templateIndex, inputHashTable);
						String controlID = WebHelperUtil.getCellData("ControlID", layoutSheet, templateIndex, inputHashTable);
						report.setTestcaseId(testcaseID.toString());
						report.setTrasactionType(vTransaction.toString());
						report.setTestDescription(controller.testDesciption);
						report.setFromDate(Config.dtFormat.format(vdate));
						report.setIteration(Config.cycleNumber);
						report.setStatus("PASS");
						report.setMessage(" ");
						if (tableType.equalsIgnoreCase("NonUniform")) {
							columns.add("TestCaseID");
							columns.add("TransactionType");
							if (isClaimsApplication) {
								columns.add("Header");// Tanvi : 4/25/2017
								columns.add("CycleDate");// Tanvi : 4/25/2017
							}
							columns.add("CurrentDate");

							// tableElement =
							// Automation.driver.findElement(By.id(tableID.toString()));
							// tableElement =
							// getElementByType(tableIDType.toString(),
							// tableID.toString());
							columnsData = new ArrayList<String>();
							columnsData.add(testcaseID.toString());
							columnsData.add(vTransaction.toString());
							if (isClaimsApplication) {
								columnsData.add(MainControllerClaims.mainControllerHeaderNo);// Tanvi
																								// :
																								// 4/25/2017
								columnsData.add(cycleDate);// Tanvi : 4/25/2017
							}
							columnsData.add(Config.dtFormat.format(vdate));

							int colNoInt;
							String strXPath;

							for (templateIndex = 1; templateIndex < templateRowCount; templateIndex++) {
								layoutRow = layoutSheet.getRow(templateIndex);
								columnName = WebHelperUtil.getCellData("ColumnName", layoutSheet, templateIndex, inputHashTable);
								columns.add(columnName.toString());

								rowNo = WebHelperUtil.getCellData("Row", layoutSheet, templateIndex, inputHashTable);
								colNo = WebHelperUtil.getCellData("Column", layoutSheet, templateIndex, inputHashTable);
								colNoInt = Integer.parseInt(colNo);
								int rowNoInt = Integer.parseInt(rowNo); // Tanvi
																		// :
																		// 4/25/2017
								// String sVal =
								// Automation.selenium.getTable(tableID.toString()
								// + "." + rowNo + "." + colNo);//RC Code
								// strXPath = tableID + "/tbody/tr[" + (rowNo) +
								// "]/td[" + (colNoInt) + "]";//Alternative if
								// below line doesn't work
								strXPath = tableID + "/tbody/tr[" + (rowNoInt + 1) + "]/td[" + (colNoInt + 1) + "]"; // Tanvi
																														// :
																														// 4/25/2017
								String sVal = Automation.driver.findElement(By.xpath(strXPath)).getText();
								if (sVal.equalsIgnoreCase(""))// Tanvi :
																// 4/25/2017
																// :start
								{
									sVal = Automation.driver.findElement(By.xpath(strXPath)).getAttribute("value");
								}// Tanvi : 4/25/2017 :End
								columnsData.add(sVal);
							}
							rows.add(columnsData);

						} else if (tableType.equalsIgnoreCase("DB")) {
							if (tableIDType.equalsIgnoreCase("Expected")) {
								SheetName = "Expected";
								ActualPath = Config.expectedValuesValuesPath + expectedDirPath + "\\" + transactionType + "_Expected"
										+ EXCEL_FILE_EXTENSTION;
							}
							ResultSet rs = null;
							Connection conn = JDBCConnection.establishDBConn();
							Statement st = conn.createStatement();
							rs = st.executeQuery(tableID);
							isTableFound = true;
							columns.add("TestCaseID");
							columns.add("TransactionType");
							if (isClaimsApplication) {
								columns.add("Header");// Tanvi : 4/25/2017
								columns.add("CycleDate");// Tanvi : 4/25/2017
							}
							columns.add("CurrentDate");

							for (int rIndex = Integer.parseInt(startRow.toString()); rs.next(); rIndex++)// rowElements.size()
							{
								columnsData = new ArrayList<String>();
								columnsData.add(testcaseID.toString());
								columnsData.add(vTransaction.toString());
								if (isClaimsApplication) {
									columnsData.add(MainControllerClaims.mainControllerHeaderNo);// Tanvi
																									// :
																									// 4/25/2017
									columnsData.add(cycleDate);// Tanvi :
																// 4/25/2017
								}
								columnsData.add(Config.dtFormat.format(vdate));

								for (templateIndex = 1; templateIndex < templateRowCount; templateIndex++) {
									layoutRow = layoutSheet.getRow(templateIndex);
									columnName = WebHelperUtil.getCellData("ColumnName", layoutSheet, templateIndex, inputHashTable);
									colNo = WebHelperUtil.getCellData("Column", layoutSheet, templateIndex, inputHashTable);

									if (rIndex == Integer.parseInt(startRow.toString())) {
										columns.add(columnName.toString());
									}
									columnsData.add(rs.getString(Integer.parseInt(colNo)));
								}
								rows.add(columnsData);
							}
							rs.close();
							st.close();
							JDBCConnection.closeConnection(conn);
						} else if (tableType.equalsIgnoreCase("Uniform")) {
							int rCount = 0;
							// Tripti:Below 3 lines NOT required
							columns.add("TestCaseID");
							columns.add("TransactionType");
							if (isClaimsApplication) {
								columns.add("Header");// Tanvi : 4/25/2017
								columns.add("CycleDate");// Tanvi : 4/25/2017
							}
							columns.add("CurrentDate");
							int colNoInt;
							String strXPath;

							tableElement = getElementByType(tableIDType, tableID);
							rowElements = tableElement.findElements(By.xpath(tableID + "/tbody/tr"));// TM:10/02/2015-tbody
																										// not
																										// needed
																										// as
																										// suggested
																										// by
																										// Dhiraj

							if (endRow.equalsIgnoreCase("0") || endRow.equalsIgnoreCase("")) {
								rCount = rowElements.size();
							} else {
								rCount = Integer.parseInt(endRow);
								if (rCount > rowElements.size()) {
									rCount = rowElements.size();
								}
							}

							for (int rIndex = Integer.parseInt(startRow.toString()); rIndex < rCount; rIndex++)// row-wise
																												// loop
							{
								columnsData = new ArrayList<String>();
								columnsData.add(testcaseID.toString());
								columnsData.add(vTransaction.toString());
								if (isClaimsApplication) {
									columnsData.add(MainControllerClaims.mainControllerHeaderNo);// Tanvi
																									// :
																									// 4/25/2017
									columnsData.add(cycleDate);// Tanvi :
																// 4/25/2017
								}
								columnsData.add(Config.dtFormat.format(vdate));

								for (templateIndex = 1; templateIndex < templateRowCount; templateIndex++)// column-wise
																											// loop
								{
									layoutRow = layoutSheet.getRow(templateIndex);
									columnName = WebHelperUtil.getCellData("ColumnName", layoutSheet, templateIndex, inputHashTable);
									colNo = WebHelperUtil.getCellData("Column", layoutSheet, templateIndex, inputHashTable);
									controlType = WebHelperUtil.getCellData("ControlType", layoutSheet, templateIndex, inputHashTable);
									colNoInt = Integer.parseInt(colNo);

									if (rIndex == Integer.parseInt(startRow.toString())) {
										columns.add(columnName.toString());
									}

									if (controlType == "" || controlType.equalsIgnoreCase(null)) {
										// String sVal =
										// Automation.selenium.getTable(tableID.toString()
										// + "." + rIndex + "." + colNo);
										strXPath = tableID + "/tbody/tr[" + (rIndex + 1) + "]/td[" + (colNoInt + 1) + "]";
										String sVal = Automation.driver.findElement(By.xpath(strXPath)).getText();
										columnsData.add(sVal);
									} else {
										strXPath = tableID + "/tbody/tr[" + (rIndex + 1) + "]/td[" + (colNoInt + 1) + "]/" + controlName; // TM-10/02/2015:
																																			// suggestion
																																			// by
																																			// Dhiraj
										String sVal = Automation.driver.findElement(By.xpath(strXPath)).getAttribute("value");
										columnsData.add(sVal);
									}
								}
								rows.add(columnsData);
							}
							break;
						} else if (tableType.equalsIgnoreCase("UniformDynamic")) {
							columns.add("TestCaseID");
							columns.add("TransactionType");
							if (isClaimsApplication) {
								columnsData.add(MainControllerClaims.mainControllerHeaderNo);// Tanvi
																								// :
																								// 4/25/2017
								columns.add("Header");// Tanvi : 4/25/2017
								columns.add("CycleDate");// Tanvi : 4/25/2017
							}
							columns.add("CurrentDate");
							int colNoInt;
							String strXPath;

							if (!Config.seleniumExecution.equalsIgnoreCase("RC")) {
								tableElement = getElementByType(tableIDType, tableID);
								rowElements = tableElement.findElements(By.tagName("tr"));
							}

							for (int rIndex = Integer.parseInt(startRow.toString()); rIndex < rowElements.size(); rIndex++) {
								columnsData = new ArrayList<String>();
								columnsData.add(testcaseID.toString());
								columnsData.add(vTransaction.toString());
								if (isClaimsApplication) {
									columnsData.add(cycleDate);// Tanvi :
																// 4/25/2017
								}
								columnsData.add(Config.dtFormat.format(vdate));
								for (templateIndex = 1; templateIndex < templateRowCount; templateIndex++) {
									layoutRow = layoutSheet.getRow(templateIndex);
									columnName = WebHelperUtil.getCellData("ColumnName", layoutSheet, templateIndex, inputHashTable);
									colNo = WebHelperUtil.getCellData("Column", layoutSheet, templateIndex, inputHashTable);
									colNoInt = Integer.parseInt(colNo);

									if (rIndex == Integer.parseInt(startRow.toString())) {
										columns.add(columnName.toString());
									}

									// String sVal =
									// Automation.selenium.getTable(tableID.toString()
									// + "." + rIndex + "." + colNo);
									strXPath = tableID + "/tbody/tr[" + (rIndex + 1) + "]/td[" + (colNoInt + 1) + "]";
									String sVal = Automation.driver.findElement(By.xpath(strXPath)).getText();
									columnsData.add(sVal);

								}

								rows.add(columnsData);
							}
							break;
						} else if (tableType.toString().equalsIgnoreCase("ControlNames")) {
							// isTableFound = true;
							System.out.println("Inside Control Names block");
							isFromVerification = true;
							columns.add("TestCaseID");
							columns.add("TransactionType");
							if (isClaimsApplication) {
								columns.add("Header");// Tanvi : 4/25/2017
							}
							columns.add("CycleDate");
							columns.add("CurrentDate");
							columnsData = new ArrayList<String>();
							columnsData.add(testcaseID.toString());
							columnsData.add(vTransaction.toString());
							if (isClaimsApplication) {
								columnsData.add(MainControllerClaims.mainControllerHeaderNo);// Tanvi
																								// :
																								// 4/25/2017
							}
							columnsData.add(cycleDate.toString());
							columnsData.add(Config.dtFormat.format(vdate));

							for (templateIndex = 1; templateIndex < templateRowCount; templateIndex++) {
								layoutRow = layoutSheet.getRow(templateIndex);

								columnName = WebHelperUtil.getCellData("ColumnName", layoutSheet, templateIndex, inputHashTable);
								controlType = WebHelperUtil.getCellData("ControlType", layoutSheet, templateIndex, inputHashTable);
								controlName = WebHelperUtil.getCellData("ControlName", layoutSheet, templateIndex, inputHashTable);
								controlID = WebHelperUtil.getCellData("ControlID", layoutSheet, templateIndex, inputHashTable);
								tableElement = getElementByType(controlID.toString(), controlName.toString());
								columns.add(columnName.toString());
								String sVal = null;
								// bhaskar
								if (isClaimsApplication) {
									sVal = WebHelperClaims.doAction("", vRow, "", "", controlType.toString(), controlID.toString(),
											controlName.toString(), "Verification", "", "", "", "", "V", tableElement, false, null, null, 0, 0, "",
											"", operationtype, "", "");
								} else if (ITAFWebDriver.isBillingApplication()) {
									sVal = WebHelperBilling.doAction("", vRow, "", "", controlType.toString(), controlID.toString(),
											controlName.toString(), "Verification", "", "", "", "", "V", tableElement, false, null, null, 0, 0, "",
											"", operationtype, "", "");
								}
								// bhaskar
								columnsData.add(sVal);

							}
							rows.add(columnsData);

						}
					}

					actualSheet = WebVerification.createActualSheet(vTransaction.toString(), columns, rows, ActualPath, SheetName, cycleDate,
							testcaseID.toString(), duplicateActualPath);

					// TM: 16-01-2015
					/*
					 * if(!expectedSheet.toString().equalsIgnoreCase("$IGNORE$"))
					 * {
					 */
					File expectedFile = new File(expectedSheetPath);
					if (expectedFile.exists()) {
						Sheet expectedSht = WebHelperUtil.getSheet(expectedSheetPath, "Expected");
						System.out.println(actualSheet + "," + expectedSht + "," + columns + "," + columnsData + "," + testcaseID.toString() + ","
								+ vTransaction.toString() + "," + operationtype + "," + cycleDate);// Tanvi
																									// :
																									// 4/25/2017

						if (isClaimsApplication) {
							report = ExcelUtility.CompareExcel(actualSheet, expectedSht, columns, columnsData, testcaseID.toString(),
									vTransaction.toString(), operationtype, cycleDate);
						} else if (ITAFWebDriver.isBillingApplication()) {
							report = ExcelUtility.CompareExcel(actualSheet, expectedSht, columns, columnsData, testcaseID.toString(),
									vTransaction.toString());
						}
						report.setReport(report);
					} else {
						report.setStatus("PASS");
						report.setMessage("Expected Sheet not found| Actual Sheet created");
					}
					// }

				} else if (!vTransaction.equalsIgnoreCase(transactionType) && rowIndex == rowCount - 1) {
					controller.pauseFun("Transaction " + transactionType + " not Found");
				}

			} catch (Exception e) {
				log.error(e.getMessage(), e);
				report.setStatus("FAIL");
				if (isClaimsApplication) {
					report.setCycleDate(controller.cycleDateCellValue);// Tanvi
																		// :
																		// 4/25/2017
				} else if (ITAFWebDriver.isBillingApplication()) {
					dt = new Date();
					report.setToDate(Config.dtFormat.format(dt));
					ExcelUtility.writeReport(report);
					controller.pauseFun(e.getMessage());
				}
			} finally {
				columns.clear();
				columnsData.clear();
				rows.clear();
			}
		}
		dt = new Date();

		report.getReport();
		report.setTestDescription(controller.testDesciption);
		report.setToDate(Config.dtFormat.format(dt));
		// TM-19/01/2015: changes made to add the following message only if
		// Blank
		if (isClaimsApplication) {
			if (report.getStatus().equalsIgnoreCase("FAIL")) {
				report.setMessage(failedMsg);
			}
			report.setCycleDate(controller.cycleDateCellValue);// Tanvi :
																// 4/25/2017
		}
		if (StringUtils.isBlank(report.getMessage()))
			report.setMessage("See Detailed Results");
		//
		isTableFound = false;
		ExcelUtility.writeReport(report);
	}

	public static void performVerification(String transactionType, String testcaseID) throws IOException, Exception {
		HSSFSheet vTableSheet = ExcelUtility.getXLSSheet(Config.verificationTableISTPath, "VerificationTables");
		int rowCount = vTableSheet.getLastRowNum() + 1;
		vTableListMap = WebHelperUtil.getValueFromHashMap(vTableSheet);
		Reporter report = new Reporter();
		HSSFSheet actualSheet = null;
		WebElement tableElement = null;
		List<WebElement> rowElements = null;
		List<WebElement> columnElements = null;
		String ActualPath = null;
		String expectedSheetPath = null;
		String SheetName = "ActualValues";
		vdate = new Date();
		WebHelper.frmDate = new Date();
		for (int rowIndex = 1; rowIndex < rowCount && !controller.pauseExecution; rowIndex++) {
			try {
				HSSFRow vRow = vTableSheet.getRow(rowIndex);
				String executeFlag = WebHelperUtil.getCellData("Verify", vTableSheet, rowIndex, inputHashTable);
				String vTransaction = WebHelperUtil.getCellData("TransactionType", vTableSheet, rowIndex, inputHashTable);
				if (isTableFound == true && !vTransaction.equalsIgnoreCase(transactionType)) {
					break;
				}

				if (executeFlag.toString().equalsIgnoreCase("Y") && vTransaction.toString().equalsIgnoreCase(transactionType.toString())) {
					isTableFound = true;
					String functionalFlag = WebHelperUtil.getCellData("Functional", vTableSheet, rowIndex, inputHashTable);
					String templateDir = WebHelperUtil.getCellData("TemplateAdditionalPath", vTableSheet, rowIndex, inputHashTable);
					String templateSheet = WebHelperUtil.getCellData("TemplateSheet", vTableSheet, rowIndex, inputHashTable);
					String expectedDirPath = WebHelperUtil.getCellData("ExpectedDataAdditionalPath", vTableSheet, rowIndex, inputHashTable);
					String expectedSheet = WebHelperUtil.getCellData("Expected", vTableSheet, rowIndex, inputHashTable);
					String templatePath = Config.verificationTemplatePath + templateDir.toString() + "\\" + templateSheet.toString();
					System.out.println(templatePath);
					ActualPath = Config.expectedValuesValuesPath + expectedDirPath + "\\" + transactionType + "_Actual.xls";
					// TM:16-01-2015
					expectedSheetPath = Config.expectedValuesValuesPath + expectedDirPath.toString() + "\\" + expectedSheet.toString();
					HSSFSheet layoutSheet = ExcelUtility.getXLSSheet(templatePath, "Layout");
					int templateRowCount = layoutSheet.getLastRowNum() + 1;
					templateMap = WebHelperUtil.getValueFromHashMap(layoutSheet);

					for (int templateIndex = 1; templateIndex < templateRowCount && !controller.pauseExecution; templateIndex++) {
						HSSFRow layoutRow = layoutSheet.getRow(templateIndex);
						String tableID = WebHelperUtil.getCellData("TableID", layoutSheet, templateIndex, inputHashTable);
						String tableType = WebHelperUtil.getCellData("TableType", layoutSheet, templateIndex, inputHashTable);
						String tableIDType = WebHelperUtil.getCellData("TableIDType", layoutSheet, templateIndex, inputHashTable);
						String startRow = WebHelperUtil.getCellData("StartRow", layoutSheet, templateIndex, inputHashTable);
						String endRow = WebHelperUtil.getCellData("EndRow", layoutSheet, templateIndex, inputHashTable);
						String columnName = WebHelperUtil.getCellData("ColumnName", layoutSheet, templateIndex, inputHashTable);
						String rowNo = WebHelperUtil.getCellData("Row", layoutSheet, templateIndex, inputHashTable);
						String colNo = WebHelperUtil.getCellData("Column", layoutSheet, templateIndex, inputHashTable);
						String controlName = WebHelperUtil.getCellData("ControlName", layoutSheet, templateIndex, inputHashTable);
						String controlType = WebHelperUtil.getCellData("ControlType", layoutSheet, templateIndex, inputHashTable);
						String controlID = WebHelperUtil.getCellData("ControlID", layoutSheet, templateIndex, inputHashTable);
						// String
						// NumberofPages=WebHelper.getCellData("NumberofPages",
						// layoutSheet, templateIndex,inputHashTable);
						report.setTestcaseId(testcaseID.toString());
						report.setTrasactionType(vTransaction.toString());
						report.setTestDescription(controller.testDesciption);
						report.setFromDate(Config.dtFormat.format(vdate));
						report.setIteration(Config.cycleNumber);
						report.setStatus("PASS");
						report.setMessage(" ");

						if (tableType.equalsIgnoreCase("NonUniform")) {
							columns.add("TestCaseID");
							columns.add("TransactionType");
							columns.add("CurrentDate");
							// tableElement =
							// Automation.driver.findElement(By.id(tableID.toString()));
							// tableElement =
							// getElementByType(tableIDType.toString(),
							// tableID.toString());
							columnsData = new ArrayList<String>();
							columnsData.add(testcaseID.toString());
							columnsData.add(vTransaction.toString());
							columnsData.add(Config.dtFormat.format(vdate));
							int rowNoInt;
							int colNoInt;
							String strXPath;

							for (templateIndex = 1; templateIndex < templateRowCount; templateIndex++) {
								layoutRow = layoutSheet.getRow(templateIndex);
								columnName = WebHelperUtil.getCellData("ColumnName", layoutSheet, templateIndex, inputHashTable);
								columns.add(columnName.toString());

								controlName = WebHelperUtil.getCellData("ControlName", layoutSheet, templateIndex, inputHashTable);
								tableID = WebHelperUtil.getCellData("TableID", layoutSheet, templateIndex, inputHashTable);
								tableType = WebHelperUtil.getCellData("TableType", layoutSheet, templateIndex, inputHashTable);

								if (tableType.equalsIgnoreCase("NonTbody")) {

									strXPath = tableID + controlName;

									try {
										if (Automation.driver.findElement(By.xpath(strXPath)).isDisplayed()) {

											String sVal = Automation.driver.findElement(By.xpath(strXPath)).getText();

											columnsData.add(sVal);

										}

									} catch (Exception e) {
										log.error(e.getMessage(), e);
										String sVal = null;

										columnsData.add(sVal);

									}
								} else {

									rowNo = WebHelperUtil.getCellData("Row", layoutSheet, templateIndex, inputHashTable);
									colNo = WebHelperUtil.getCellData("Column", layoutSheet, templateIndex, inputHashTable);
									rowNoInt = Integer.parseInt(rowNo);
									colNoInt = Integer.parseInt(colNo);
									// String sVal =
									// Automation.selenium.getTable(tableID.toString()
									// + "." + rowNo + "." + colNo);//RC Code
									// strXPath = tableID + "/tbody/tr[" +
									// (rowNo) + "]/td[" + (colNoInt) +
									// "]";//Alternative if below line doesn't
									// work
									strXPath = tableID + "/tbody/tr[" + (rowNoInt + 1) + "]/td[" + (colNoInt + 1) + "]" + controlName;
									try {
										if (Automation.driver.findElement(By.xpath(strXPath)).isDisplayed()) {

											String sVal = Automation.driver.findElement(By.xpath(strXPath)).getText();

											columnsData.add(sVal);

										}

									} catch (Exception e) {
										log.error(e.getMessage(), e);
										String sVal = null;

										columnsData.add(sVal);

									}
								}
							}
							rows.add(columnsData);

						}

						if (tableType.equalsIgnoreCase("RateFactor")) {
							int i;

							// NumberofPages=WebHelper.getCellData("NumberofPages",
							// layoutSheet, templateIndex,inputHashTable);
							columns.add("TestCaseID");
							columns.add("TransactionType");
							columns.add("CurrentDate");
							// tableElement =
							// Automation.driver.findElement(By.id(tableID.toString()));
							// tableElement =
							// getElementByType(tableIDType.toString(),
							// tableID.toString());
							columnsData = new ArrayList<String>();
							columnsData.add(testcaseID.toString());
							columnsData.add(vTransaction.toString());
							columnsData.add(Config.dtFormat.format(vdate));
							String strXPath;
							String sVal;
							// int rowNoInt;
							// int colNoInt;
							// String strXPath;

							// for (i=1;i<=Integer.parseInt(NumberofPages);i++){

							for (templateIndex = 1; templateIndex < templateRowCount; templateIndex++) {
								layoutRow = layoutSheet.getRow(templateIndex);
								columnName = WebHelperUtil.getCellData("ColumnName", layoutSheet, templateIndex, inputHashTable);
								columns.add(columnName.toString());

								// rowNo = WebHelper.getCellData("Row",
								// layoutSheet, templateIndex,inputHashTable);
								// colNo = WebHelper.getCellData("Column",
								// layoutSheet, templateIndex,inputHashTable);
								// rowNoInt = Integer.parseInt(rowNo);
								// colNoInt = Integer.parseInt(colNo);
								controlType = WebHelperUtil.getCellData("ControlType", layoutSheet, templateIndex, inputHashTable);
								// controlName =
								// WebHelper.getCellData("ControlName",
								// layoutSheet, templateIndex,inputHashTable);
								tableID = WebHelperUtil.getCellData("TableID", layoutSheet, templateIndex, inputHashTable);

								strXPath = tableID + controlName;
								try {
									if (Automation.driver.findElement(By.xpath(strXPath)).isDisplayed()) {

										if (controlType.equalsIgnoreCase("WebEdit")) {
											sVal = Automation.driver.findElement(By.xpath(strXPath)).getAttribute("value");
										} else {
											sVal = Automation.driver.findElement(By.xpath(strXPath)).getText();
										}
										columnsData.add(sVal);

									}

								} catch (Exception e) {
									log.error(e.getMessage(), e);
									sVal = null;

									columnsData.add(sVal);

								}

							}
							rows.add(columnsData);
							// }
						}

						if (tableType.equalsIgnoreCase("NonTable")) {
							columns.add("TestCaseID");
							columns.add("TransactionType");
							columns.add("CurrentDate");
							// tableElement =
							// Automation.driver.findElement(By.id(tableID.toString()));
							// tableElement =
							// getElementByType(tableIDType.toString(),
							// tableID.toString());
							columnsData = new ArrayList<String>();
							columnsData.add(testcaseID.toString());
							columnsData.add(vTransaction.toString());
							columnsData.add(Config.dtFormat.format(vdate));
							String strXPath;
							String sVal;
							// int rowNoInt;
							// int colNoInt;
							// String strXPath;

							for (templateIndex = 1; templateIndex < templateRowCount; templateIndex++) {
								layoutRow = layoutSheet.getRow(templateIndex);
								columnName = WebHelperUtil.getCellData("ColumnName", layoutSheet, templateIndex, inputHashTable);
								columns.add(columnName.toString());

								// rowNo = WebHelper.getCellData("Row",
								// layoutSheet, templateIndex,inputHashTable);
								// colNo = WebHelper.getCellData("Column",
								// layoutSheet, templateIndex,inputHashTable);
								// rowNoInt = Integer.parseInt(rowNo);
								// colNoInt = Integer.parseInt(colNo);
								controlType = WebHelperUtil.getCellData("ControlType", layoutSheet, templateIndex, inputHashTable);
								// controlName =
								// WebHelper.getCellData("ControlName",
								// layoutSheet, templateIndex,inputHashTable);
								tableID = WebHelperUtil.getCellData("TableID", layoutSheet, templateIndex, inputHashTable);

								strXPath = tableID + controlName;
								try {
									if (Automation.driver.findElement(By.xpath(strXPath)).isDisplayed()) {

										if (controlType.equalsIgnoreCase("WebEdit")) {
											sVal = Automation.driver.findElement(By.xpath(strXPath)).getAttribute("value");
										} else {
											sVal = Automation.driver.findElement(By.xpath(strXPath)).getText();
										}
										columnsData.add(sVal);

									} else {

										sVal = null;

										columnsData.add(sVal);

									}

								} catch (Exception e) {
									log.error(e.getMessage(), e);
									sVal = null;

									columnsData.add(sVal);

								}

							}
							rows.add(columnsData);

						} else if (tableType.equalsIgnoreCase("DB")) {
							if (tableIDType.equalsIgnoreCase("Expected")) {
								SheetName = "Expected";
								ActualPath = Config.expectedValuesValuesPath + expectedDirPath + "\\" + transactionType + "_Expected.xls";
							}
							Connection conn = null;
							ResultSet rs = null;// =
											// JDBCConnection.establishDBConn(tableID);
							Statement st = null;
							if (Config.projectName.equals("DistributionManagement")) {// Minaakshi
								tableID = DMProduct.getDynamicSQLQuery(tableID, controlName);
								conn = JDBCConnection.establishPASDBConn();
								st = conn.createStatement();
								rs = st.executeQuery(tableID);
							} else {
								conn = JDBCConnection.establishDBConn();
								st = conn.createStatement();
								rs = st.executeQuery(tableID);
							}

							isTableFound = true;
							columns.add("TestCaseID");
							columns.add("TransactionType");
							columns.add("CurrentDate");

							for (int rIndex = Integer.parseInt(startRow.toString()); rs.next(); rIndex++)// rowElements.size()
							{
								columnsData = new ArrayList<String>();
								columnsData.add(testcaseID.toString());
								columnsData.add(vTransaction.toString());
								columnsData.add(Config.dtFormat.format(vdate));

								for (templateIndex = 1; templateIndex < templateRowCount; templateIndex++) {
									layoutRow = layoutSheet.getRow(templateIndex);
									columnName = WebHelperUtil.getCellData("ColumnName", layoutSheet, templateIndex, inputHashTable);
									colNo = WebHelperUtil.getCellData("Column", layoutSheet, templateIndex, inputHashTable);

									if (rIndex == Integer.parseInt(startRow.toString())) {
										columns.add(columnName.toString());
									}
									columnsData.add(rs.getString(Integer.parseInt(colNo)));
								}
								rows.add(columnsData);
							}
							rs.close();
							st.close();
							JDBCConnection.closeConnection(conn);
						} else if (tableType.equalsIgnoreCase("UniformDynamic"))

						{
							int rCount = 0;
							// Tripti:Below 3 lines NOT required
							columns.add("TestCaseID");
							columns.add("TransactionType");
							columns.add("CurrentDate");
							columnsData = new ArrayList<String>();
							columnsData.add(testcaseID.toString());
							columnsData.add(vTransaction.toString());
							columnsData.add(Config.dtFormat.format(vdate));

							int colNoInt;
							String strXPath;

							for (templateIndex = 1; templateIndex < templateRowCount; templateIndex++)// column-wise
																										// loop
							{
								rowNo = WebHelperUtil.getCellData("Row", layoutSheet, templateIndex, inputHashTable);
								// startRow = WebHelper.getCellData("StartRow",
								// layoutSheet, templateIndex,inputHashTable);
								int rIndex = Integer.parseInt(rowNo.toString());
								layoutRow = layoutSheet.getRow(templateIndex);
								columnName = WebHelperUtil.getCellData("ColumnName", layoutSheet, templateIndex, inputHashTable);
								colNo = WebHelperUtil.getCellData("Column", layoutSheet, templateIndex, inputHashTable);
								controlType = WebHelperUtil.getCellData("ControlType", layoutSheet, templateIndex, inputHashTable);
								colNoInt = Integer.parseInt(colNo);
								controlName = WebHelperUtil.getCellData("ControlName", layoutSheet, templateIndex, inputHashTable);
								tableID = WebHelperUtil.getCellData("TableID", layoutSheet, templateIndex, inputHashTable);
								columns.add(columnName.toString());

								if (controlType == "" || controlType.equalsIgnoreCase(null)) {

									strXPath = tableID + "/tbody/tr[" + (rIndex + 1) + "]/td[" + (colNoInt + 1) + "]";

									String sVal = Automation.driver.findElement(By.xpath(strXPath)).getText();

									columnsData.add(sVal);
								} else {

									strXPath = tableID + "/tbody/tr[" + (rIndex + 1) + "]/td[" + (colNoInt + 1) + "]" + controlName;

									try {
										if (Automation.driver.findElement(By.xpath(strXPath)).isDisplayed()) {

											String sVal = Automation.driver.findElement(By.xpath(strXPath)).getText();

											columnsData.add(sVal);

										} else

										{
											String sVal = null;

											columnsData.add(sVal);
										}

									} catch (Exception e) {
										log.error(e.getMessage(), e);
										String sVal = null;

										columnsData.add(sVal);

									}

								}
								rows.add(columnsData);
							}
							break;
						}

						// }
						// else if(tableType.isEmpty()){

						// System.out.println("row already executed");
						// }
						else if (tableType.equalsIgnoreCase("Uniform")) {
							columns.add("TestCaseID");
							columns.add("TransactionType");
							columns.add("CurrentDate");
							int colNoInt;
							String strXPath;

							if (!Config.seleniumExecution.equalsIgnoreCase("RC")) {
								tableElement = getElementByType(tableIDType, tableID);
								rowElements = tableElement.findElements(By.tagName("tr"));
							}

							for (int rIndex = Integer.parseInt(startRow.toString()); rIndex < rowElements.size(); rIndex++) {
								columnsData = new ArrayList<String>();
								columnsData.add(testcaseID.toString());
								columnsData.add(vTransaction.toString());
								columnsData.add(Config.dtFormat.format(vdate));
								for (templateIndex = 1; templateIndex < templateRowCount; templateIndex++) {
									layoutRow = layoutSheet.getRow(templateIndex);
									columnName = WebHelperUtil.getCellData("ColumnName", layoutSheet, templateIndex, inputHashTable);
									colNo = WebHelperUtil.getCellData("Column", layoutSheet, templateIndex, inputHashTable);
									colNoInt = Integer.parseInt(colNo);

									if (rIndex == Integer.parseInt(startRow.toString())) {
										columns.add(columnName.toString());
									}

									// String sVal =
									// Automation.selenium.getTable(tableID.toString()
									// + "." + rIndex + "." + colNo);
									if (Config.projectName.equals("DistributionManagement")) {// Minaakshi
																								// 20-09-2018
										strXPath = tableID + "/tr[" + (rIndex + 1) + "]/td[" + (colNoInt + 1) + "]";
										String sVal = Automation.driver.findElement(By.xpath(strXPath)).getText();
										columnsData.add(sVal);
									} else {
										strXPath = tableID + "/tbody/tr[" + (rIndex + 1) + "]/td[" + (colNoInt + 1) + "]";
										String sVal = Automation.driver.findElement(By.xpath(strXPath)).getText();
										columnsData.add(sVal);
									}

								}

								rows.add(columnsData);
							}
							break;
						} else if (tableType.toString().equalsIgnoreCase("ControlNames")) {
							// isTableFound = true;
							isFromVerification = true;
							columns.add("TestCaseID");
							columns.add("TransactionType");
							columns.add("CurrentDate");
							columnsData = new ArrayList<String>();
							columnsData.add(testcaseID.toString());
							columnsData.add(vTransaction.toString());
							columnsData.add(Config.dtFormat.format(vdate));

							for (templateIndex = 1; templateIndex < templateRowCount; templateIndex++) {
								layoutRow = layoutSheet.getRow(templateIndex);

								columnName = WebHelperUtil.getCellData("ColumnName", layoutSheet, templateIndex, inputHashTable);
								controlType = WebHelperUtil.getCellData("ControlType", layoutSheet, templateIndex, inputHashTable);
								controlName = WebHelperUtil.getCellData("ControlName", layoutSheet, templateIndex, inputHashTable);
								controlID = WebHelperUtil.getCellData("ControlID", layoutSheet, templateIndex, inputHashTable);
								tableElement = getElementByType(controlID.toString(), controlName.toString());
								columns.add(columnName.toString());
								// bhaskar
								String sVal = WebHelperPAS.doAction("", controlType.toString(), controlID.toString(), controlName.toString(),
										"Verification", "", "V", tableElement, false, null, null, 0, 0, "", "");
								// bhaskar
								columnsData.add(sVal);

							}
							rows.add(columnsData);

						}

						// ***Code change done by DM Team***// 14-11-2018

						else if (tableType.toString().equalsIgnoreCase("ControlNames_DM"))// Minaakshi
																							// :
																							// 14-11-2018
						{
							// isTableFound = true;
							isFromVerification = true;
							columns.add("TestCaseID");
							columns.add("TransactionType");
							columns.add("CurrentDate");

							int colNoInt;
							String strXPath;

							if (!Config.seleniumExecution.equalsIgnoreCase("RC")) {
								tableElement = getElementByType(tableIDType, tableID);
								rowElements = tableElement.findElements(By.tagName("tr"));
							}

							/*
							 * columnsData =new ArrayList<String>();
							 * columnsData.add(testcaseID.toString());
							 * columnsData.add(vTransaction.toString());
							 * columnsData.add(Config.dtFormat.format(vdate));
							 */

							for (int rIndex = Integer.parseInt(startRow.toString()); rIndex < rowElements.size(); rIndex++) {

								columnsData = new ArrayList<String>();
								columnsData.add(testcaseID.toString());
								columnsData.add(vTransaction.toString());
								columnsData.add(Config.dtFormat.format(vdate));

								for (templateIndex = 1; templateIndex < templateRowCount; templateIndex++) {
									layoutRow = layoutSheet.getRow(templateIndex);

									columnName = WebHelperUtil.getCellData("ColumnName", layoutSheet, templateIndex, inputHashTable);
									controlType = WebHelperUtil.getCellData("ControlType", layoutSheet, templateIndex, inputHashTable);
									controlName = WebHelperUtil.getCellData("ControlName", layoutSheet, templateIndex, inputHashTable);
									controlID = WebHelperUtil.getCellData("ControlID", layoutSheet, templateIndex, inputHashTable);

									// //table[@id='dgBankAccount']/tbody/tr[$]/td[2]/div/div/input
									String[] arrTempcontrolName = controlName.split("\\$");
									controlName = arrTempcontrolName[0].toString() + (rIndex + 1) + arrTempcontrolName[1].toString();

									tableElement = getElementByType(controlID.toString(), controlName.toString());

									// columns.add(columnName.toString());

									if (rIndex == Integer.parseInt(startRow.toString())) {
										columns.add(columnName.toString());
									}

									// bhaskar
									String sVal = WebHelperPAS.doAction("", controlType.toString(), controlID.toString(), controlName.toString(),
											"Verification", "", "V", tableElement, false, null, null, 0, 0, "", "");
									// bhaskar
									columnsData.add(sVal);
								}
								rows.add(columnsData);

							}
						}// ***//

					}

					actualSheet = createActualSheet(vTransaction.toString(), columns, rows, ActualPath, SheetName);

					// TM: 16-01-2015
					/*
					 * if(!expectedSheet.toString().equalsIgnoreCase("$IGNORE$"))
					 * {
					 */
					File expectedFile = new File(expectedSheetPath);
					if (expectedFile.exists()) {
						HSSFSheet expectedSht = ExcelUtility.getXLSSheet(expectedSheetPath, "Expected");
						report = ExcelUtility.CompareExcel(actualSheet, expectedSht, columns, columnsData, testcaseID.toString(),
								vTransaction.toString());
						report.setReport(report);
					} else {
						report.setStatus("FAIL");
						report.setMessage("Expected Sheet not found| Actual Sheet created");
					}
					// }

				} else if (!vTransaction.equalsIgnoreCase(transactionType) && rowIndex == rowCount - 1) {
					controller.pauseFun("Transaction " + transactionType + " not Found");
				}

			} catch (Exception e) {
				report.setToDate(Config.dtFormat.format(dt));
				report.setStatus("FAIL");
				report.setMessage(e.getMessage());
				ExcelUtility.writeReportPAS(report);
				controller.pauseFun(e.getMessage());
			} finally {
				columns.clear();
				columnsData.clear();
				rows.clear();
			}
		}
		dt = new Date();

		report.getReport();
		report.setTestDescription(controller.testDesciption);
		report.setToDate(Config.dtFormat.format(dt));
		// TM-19/01/2015: changes made to add the following message only if
		// Blank
		if (StringUtils.isBlank(report.getMessage()))
			report.setMessage("See Detailed Results");
		//
		isTableFound = false;
		ExcelUtility.writeReportPAS(report);
	}

	// public static HSSFRow createHeader(HSSFSheet actualSheet,List<String>
	// columns)
	public static XSSFRow createHeader(XSSFSheet actualSheet, List<String> columns) {
		// XX
		/*
		 * HSSFRow actualRow = actualSheet.getRow(0); HSSFCell testCaseID =
		 * actualRow.createCell(0); HSSFCell transactionType =
		 * actualRow.createCell(1); HSSFCell Date = actualRow.createCell(2);
		 */
		//
		// XX
		XSSFRow actualRow = actualSheet.getRow(0);
		XSSFCell testCaseID = actualRow.createCell(0);
		XSSFCell transactionType = actualRow.createCell(1);
		XSSFCell Date = actualRow.createCell(2);
		Iterator<String> iterator = columns.iterator();
		int count = 0;
		if (iterator.hasNext() == true) {
			count = count + 1;
			// @SuppressWarnings("unused")
			XSSFCell dynamicColumns = actualRow.createCell(count);
			// HSSFCell dynamicColumns = actualRow.createCell(count);
		}
		return actualRow;
	}

	public static HSSFRow createHeader(HSSFSheet actualSheet, List<String> columns) {
		HSSFRow actualRow = actualSheet.getRow(0);
		HSSFCell testCaseID = actualRow.createCell(0);
		HSSFCell transactionType = actualRow.createCell(1);
		HSSFCell Date = actualRow.createCell(2);
		Iterator<String> iterator = columns.iterator();
		int count = 0;
		if (iterator.hasNext() == true) {
			count = count + 1;
			// @SuppressWarnings("unused")
			HSSFCell dynamicColumns = actualRow.createCell(count);
		}
		return actualRow;
	}

	public static HSSFSheet createActualSheet(String transactionType, List<String> columns, List<List<String>> columnData, String actualPath,
			String SheetName) throws IOException, Exception {
		FileOutputStream out = null;
		FileOutputStream out1 = null;
		POIFSFileSystem lPOIfs = null;
		InputStream in = null;
		HSSFWorkbook workBook = null;
		HSSFSheet workSheet = null;
		// String ActualPath =
		// Automation.expectedValuesValuesPath.toString()+transactionType +
		// "_Actual.xls";
		File lFile = new File(actualPath);
		if (lFile.exists() && SheetName.equalsIgnoreCase("Expected")) {
			lFile.delete();
			System.out.println("Deleted Existing Expected Sheet");
		}
		int columnSize = columns.size();
		int columnIndex = 0;
		HSSFCell actualCell = null;
		HSSFCell valuesCell = null;
		if (!lFile.exists()) {
			try {
				workBook = new HSSFWorkbook();
				workSheet = workBook.createSheet(SheetName);
				HSSFRow actualRowHeader = workSheet.createRow(0);
				int rowNum = 0;
				HSSFRow valuesRow = workSheet.createRow(rowNum + 1);
				currentRowIndex = rowNum;
				// Iterator<String> iterator = columns.iterator();
				int rowCount = rows.size();
				for (int rIndex = 0; rIndex < rowCount; rIndex++) // changed by
																	// asif
				// for(int rIndex=0;rIndex<1;rIndex++)
				{
					if (rIndex > 0) {
						valuesRow = workSheet.createRow(rowNum + 1 + rIndex);
					}
					columnSize = rows.get(rIndex).size();
					for (columnIndex = columnSize; columnIndex >= 1; columnIndex--) {
						actualCell = actualRowHeader.createCell(columnSize - columnIndex);
						valuesCell = valuesRow.createCell(columnSize - columnIndex);
						actualCell.setCellValue(columns.get(columnSize - columnIndex));
						valuesCell.setCellValue(rows.get(rIndex).get(columnSize - columnIndex));
						System.out.print(valuesCell.toString());
					}
				}
				out = new FileOutputStream(actualPath);
				workBook.write(out);
			} catch (IOException ioe) {
				ioe.getLocalizedMessage();
			} finally {
				out.flush();
				out.close();
				workBook.close();
			}
		} else {
			try {
				in = new FileInputStream(actualPath);
				lPOIfs = new POIFSFileSystem(in);
				workBook = new HSSFWorkbook(lPOIfs);
				workSheet = workBook.getSheet(SheetName);
				int lastRow = workSheet.getLastRowNum();
				currentRowIndex = lastRow;
				HSSFRow row = workSheet.getRow(lastRow + 1);
				if (row == null) {
					row = workSheet.createRow(lastRow + 1);
				}
				int rowCount = rows.size();
				for (int rIndex = 0; rIndex < rowCount; rIndex++) {
					if (rIndex > 0) {
						row = workSheet.createRow(lastRow + 1 + rIndex);
					}
					int cSize = rows.get(rIndex).size();

					for (columnIndex = cSize; columnIndex >= 1; columnIndex--) {
						valuesCell = row.getCell(cSize - columnIndex);
						if (valuesCell == null) {
							valuesCell = row.createCell(cSize - columnIndex);
							valuesCell.setCellType(Cell.CELL_TYPE_STRING);
							valuesCell.setCellValue(rows.get(rIndex).get(cSize - columnIndex));
						}
					}
				}
				out1 = new FileOutputStream(actualPath);
				workBook.write(out1);
			} catch (Exception ioe) {
				controller.pauseFun(ioe.getLocalizedMessage() + " from CreateActualSheet Function ");
			} finally {
				out1.flush();
				out1.close();
				// System.out.println("HELLO WORLD");
			}
		}
		return workSheet;
	}

	// @SuppressWarnings("resource")
	public static Sheet createActualSheet(String transactionType, List<String> columns, List<List<String>> columnData, String actualPath,
			String SheetName, String cycleDate, String testcaseID, String duplicateActualPath) throws IOException, Exception {
		FileOutputStream out = null;
		FileOutputStream out1 = null;
		POIFSFileSystem lPOIfs = null;
		InputStream in = null;
		// XX
		Workbook workBook = null;
		Sheet workSheet = null;
		//
		// String ActualPath =
		// Automation.expectedValuesValuesPath.toString()+transactionType +
		// "_Actual.xls";
		File lFile = new File(actualPath);
		if (!Config.appendData.toString().equalsIgnoreCase("Y")) {
			if (lFile.exists() && SheetName.equalsIgnoreCase("ActualValues")) {
				// lFile.delete();
				// System.out.println("Deleted Existing Actual Sheet");
				String newActualPath = duplicateActualPath;
				InputStream in1 = new FileInputStream(actualPath);
				POIFSFileSystem lPOIfs1 = new POIFSFileSystem(in1);
				// XX
				Workbook workBook1 = createWorkbook(lPOIfs1);
				Sheet workSheet1 = workBook1.getSheet(SheetName);
				//

				String expectedString = testcaseID + "," + transactionType + "," + cycleDate;
				// lFile.setReadOnly();
				// XX
				Row controllerRow = null;
				Cell TestCaseIDCell = null;
				Cell TransactionTypeCell = null;
				Cell CycleDateCell = null;
				//

				int tempindex = 1;
				// XX
				Sheet tempActualsheet = WebHelperUtil.getSheet(actualPath, "ActualValues");
				Workbook workBook2 = createWorkbook();
				Sheet workSheet2 = workBook2.createSheet(SheetName);
				FileOutputStream out3 = new FileOutputStream(duplicateActualPath);
				workBook2.write(out3);
				out3.close();
				// XX
				Sheet tempdupActualsheet = WebHelperUtil.getSheet(duplicateActualPath, "ActualValues");
				int rowCount = tempActualsheet.getLastRowNum() + 1;
				for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {
					controllerRow = tempActualsheet.getRow(rowIndex);
					TestCaseIDCell = controllerRow.getCell(0);
					TransactionTypeCell = controllerRow.getCell(1);
					CycleDateCell = controllerRow.getCell(2);
					String TestCaseID = TestCaseIDCell.getStringCellValue();
					String TransactionType = TransactionTypeCell.getStringCellValue();
					String CycleDate = CycleDateCell.getStringCellValue();
					String actualString = TestCaseID + "," + TransactionType + "," + CycleDate;
					if (actualString.equalsIgnoreCase("expectedString")) {
						tempindex = 1;
						break;
					} else {
						// XX
						Row sourceRow = tempActualsheet.getRow(rowIndex);
						Row destinationRow = tempdupActualsheet.getRow(tempindex);
						//

						if (destinationRow != null) {
							tempdupActualsheet.shiftRows(tempindex, tempdupActualsheet.getLastRowNum(), 1);
						} else {
							destinationRow = tempdupActualsheet.createRow(tempindex);
						}

						for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
							Cell oldCell = sourceRow.getCell(i);
							Cell newCell = destinationRow.createCell(i);

							if (oldCell == null) {
								newCell = null;
								continue;
							}

							// Set the cell data type
							newCell.setCellType(oldCell.getCellType());

							// Set the cell data value
							switch (oldCell.getCellType()) {
							case Cell.CELL_TYPE_BLANK:
								newCell.setCellValue(oldCell.getStringCellValue());
								break;
							case Cell.CELL_TYPE_BOOLEAN:
								newCell.setCellValue(oldCell.getBooleanCellValue());
								break;
							case Cell.CELL_TYPE_ERROR:
								newCell.setCellErrorValue(oldCell.getErrorCellValue());
								break;
							case Cell.CELL_TYPE_FORMULA:
								newCell.setCellFormula(oldCell.getCellFormula());
								break;
							case Cell.CELL_TYPE_NUMERIC:
								newCell.setCellValue(oldCell.getNumericCellValue());
								break;
							case Cell.CELL_TYPE_STRING:
								newCell.setCellValue(oldCell.getRichStringCellValue());
								break;
							}
						}
						tempindex = tempindex + 1;

					}
					FileOutputStream out2 = new FileOutputStream(duplicateActualPath);
					workBook1.write(out2);
					out2.close();
				}
				in1.close();

			}
			System.out.println("need to delete");
			lFile.delete();
		}
		int columnSize = columns.size();
		int columnIndex = 0;
		Cell actualCell = null;
		Cell valuesCell = null;
		if (!lFile.exists()) {
			try {
				workBook = createWorkbook();
				workSheet = workBook.createSheet(SheetName);
				Row actualRowHeader = workSheet.createRow(0);
				int rowNum = 0;
				Row valuesRow = workSheet.createRow(rowNum + 1);
				currentRowIndex = rowNum;
				// Iterator<String> iterator = columns.iterator();
				int rowCount = rows.size();
				for (int rIndex = 0; rIndex < rowCount; rIndex++) {
					if (rIndex > 0) {
						valuesRow = workSheet.createRow(rowNum + 1 + rIndex);
					}
					columnSize = rows.get(rIndex).size();
					for (columnIndex = columnSize; columnIndex >= 1; columnIndex--) {
						actualCell = actualRowHeader.createCell(columnSize - columnIndex);
						valuesCell = valuesRow.createCell(columnSize - columnIndex);
						actualCell.setCellValue(columns.get(columnSize - columnIndex));
						valuesCell.setCellValue(rows.get(rIndex).get(columnSize - columnIndex));
						System.out.print(valuesCell.toString());
					}
				}
				out = new FileOutputStream(actualPath);
				workBook.write(out);
			} catch (IOException ioe) {
				ioe.getLocalizedMessage();
			} finally {
				out.flush();
				out.close();
			}
		} else {
			try {
				in = new FileInputStream(actualPath);
				lPOIfs = new POIFSFileSystem(in);
				workBook = createWorkbook(lPOIfs);
				workSheet = workBook.getSheet(SheetName);
				int lastRow = workSheet.getLastRowNum();
				currentRowIndex = lastRow;
				Row row = workSheet.getRow(lastRow + 1);
				if (row == null) {
					row = workSheet.createRow(lastRow + 1);
				}
				int rowCount = rows.size();
				for (int rIndex = 0; rIndex < rowCount; rIndex++) {
					if (rIndex > 0) {
						row = workSheet.createRow(lastRow + 1 + rIndex);
					}
					int cSize = rows.get(rIndex).size();

					for (columnIndex = cSize; columnIndex >= 1; columnIndex--) {
						valuesCell = row.getCell(cSize - columnIndex);
						if (valuesCell == null) {
							valuesCell = row.createCell(cSize - columnIndex);
							valuesCell.setCellType(Cell.CELL_TYPE_STRING);
							valuesCell.setCellValue(rows.get(rIndex).get(cSize - columnIndex));
						}
					}
				}
				out1 = new FileOutputStream(actualPath);
				workBook.write(out1);
			} catch (Exception ioe) {
				controller.pauseFun(ioe.getLocalizedMessage() + " from CreateActualSheet Function ");
			} finally {
				out1.flush();
				out1.close();
				// System.out.println("HELLO WORLD");
			}
		}
		return workSheet;
	}

	private static Workbook createWorkbook() throws IOException {
		return createWorkbook(null);
	}

	private static Workbook createWorkbook(POIFSFileSystem lPOIfs1) throws IOException {
		if (ITAFWebDriver.isBillingApplication()) {
			return new XSSFWorkbook();
		} else if (ITAFWebDriver.isClaimsApplication()) {
			if (lPOIfs1 == null) {
				return new HSSFWorkbook();
			} else {
				return new HSSFWorkbook(lPOIfs1);
			}
		}
		return null;
	}

	public static WebElement getElementByType(String controlID, String controlName) throws IOException {
		WebElement element = null;
		try {
			Constants.ControlIdEnum controlId = Constants.ControlIdEnum.valueOf(controlID);
			switch (controlId) {
			case ClassName:
				element = Automation.driver.findElement(By.className(controlName));
				break;
			case Id:
			case HTMLID:
				element = Automation.driver.findElement(By.id(controlName));
				break;
			case Name:
				element = Automation.driver.findElement(By.name(controlName));
				break;
			case TagName:
				element = Automation.driver.findElement(By.tagName(controlName));
				break;
			case XPath:
				element = Automation.driver.findElement(By.xpath(controlName));
				break;
			default:
				break;
			}
		} catch (Exception e) {
			failedMsg = e.getMessage();
			controller.pauseFun(e.getMessage() + " from getElementByType Function");
		}
		return element;
	}

	public static String getText(ITAFWebDriverPAS driver, WebElement element) {
		return (String) ((JavascriptExecutor) driver).executeScript("return jQuery(arguments[0]).text();", element);
	}

}
