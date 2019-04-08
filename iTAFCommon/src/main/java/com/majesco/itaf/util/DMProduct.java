package com.majesco.itaf.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.openqa.selenium.WebElement;

import com.majesco.itaf.main.Config;
import com.majesco.itaf.main.ITAFWebDriver;
import com.majesco.itaf.main.MainController;
import com.majesco.itaf.main.ObjectFactory;
import com.majesco.itaf.main.WebHelper;
import com.majesco.itaf.main.WebHelperUtil;

public class DMProduct {
	// Minaakshi : Code added to read data from UniqueNumber sheet via Template
	// ControlName
	final static Logger log = Logger.getLogger(WebHelperUtil.class.getName());
	static MainController controller = ObjectFactory.getMainController();
	static ITAFWebDriver webDriver = ITAFWebDriver.getInstance();

	public static String ReadFromExcelUsingColumnName(String controlValue, String ColumnName) throws IOException {
		Sheet uniqueNumberSheet = null;
		String uniqueTestcaseID = "";
		HashMap<String, Integer> uniqueValuesHashMap = null;
		String uniqueNumber = null;
		String SearchTestcaseID = null;
		try {
			uniqueNumberSheet = WebHelperUtil.getSheet(Config.transactionInfo, "DataSheet");
			uniqueValuesHashMap = WebHelperUtil.getValueFromHashMap(uniqueNumberSheet);
			int rowCount = uniqueNumberSheet.getPhysicalNumberOfRows();

			if (controlValue.equals("")) {
				SearchTestcaseID = controller.controllerTestCaseID.toString();
			} else {
				SearchTestcaseID = controlValue;
			}

			for (int rIndex = 1; rIndex < rowCount; rIndex++) {
				uniqueTestcaseID = WebHelperUtil.getCellData("TestCaseID", uniqueNumberSheet, rIndex, uniqueValuesHashMap);

				if (SearchTestcaseID.equals(uniqueTestcaseID)) {
					return uniqueNumber = WebHelperUtil.getCellData(ColumnName, uniqueNumberSheet, rIndex, uniqueValuesHashMap);
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			controller.pauseFun(e.getMessage() + " from ReadFromExcel Function");
		}
		return uniqueNumber;
	}

	// Minaakshi : Added this function to write SQL queries to Unique Number
	// sheet for reference
	public static void writeDataToUniqueNumberSheet(String ctrlValue, String columnName) throws Exception {
		Workbook uniqueWB = null;
		try {
			FileInputStream in = new FileInputStream(Config.transactionInfo.toString());
			uniqueWB = WebHelperUtil.createWorkbook(in);

			Sheet uniqueNumberSheet = uniqueWB.getSheet("DataSheet");
			HashMap<String, Integer> uniqueValuesHashMap = WebHelperUtil.getValueFromHashMap(uniqueNumberSheet);
			Row uniqueRow = null;
			int rowNum = uniqueNumberSheet.getPhysicalNumberOfRows();
			log.info("%%%%%%%%*********" + rowNum);

			for (int rIndex = 0; rIndex < rowNum; rIndex++) {
				uniqueRow = uniqueNumberSheet.getRow(rIndex);
				String uniqueTestcaseID = WebHelperUtil.getCellData("TestCaseID", uniqueNumberSheet, rIndex, uniqueValuesHashMap);

				if (controller.controllerTestCaseID.toString().equals(uniqueTestcaseID)) {
					uniqueRow = uniqueNumberSheet.getRow(rIndex);
					break;
				} else if (rIndex == rowNum - 1) {
					uniqueRow = uniqueNumberSheet.createRow(rowNum);
				}
			}

			Cell uniqueTestCaseID = uniqueRow.createCell(uniqueValuesHashMap.get("TestCaseID").intValue());
			Cell uniqueCell = uniqueRow.createCell(uniqueValuesHashMap.get(columnName).intValue());
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
	}

	public static Boolean writeRequestIDToExcel(String ctrlValue, WebElement webElement, String controlId, String controlType, String controlName,
			String rowNo, String colNo) throws Exception {
		Workbook uniqueWB = null;
		try {
			FileInputStream in = new FileInputStream(Config.transactionInfo.toString());
			uniqueWB = WebHelperUtil.createWorkbook(in);
			Sheet uniqueNumberSheet = uniqueWB.getSheet("DataSheet");
			HashMap<String, Integer> uniqueValuesHashMap = WebHelperUtil.getValueFromHashMap(uniqueNumberSheet);
			Row uniqueRow = null;
			int rowNum = uniqueNumberSheet.getPhysicalNumberOfRows();
			log.info("%%%%%%%%*********" + rowNum);

			for (int rIndex = 0; rIndex < rowNum; rIndex++) {
				uniqueRow = uniqueNumberSheet.getRow(rIndex);
				String uniqueTestcaseID = WebHelperUtil.getCellData("TestCaseID", uniqueNumberSheet, rIndex, uniqueValuesHashMap);

				if (controller.controllerTestCaseID.toString().equals(uniqueTestcaseID)) {
					uniqueRow = uniqueNumberSheet.getRow(rIndex);
					break;
				} else if (rIndex == rowNum - 1) {
					uniqueRow = uniqueNumberSheet.createRow(rowNum);
				}
			}

			ctrlValue = webElement.getText();

			// Split string and fetch request id
			// Transfer request 14912 has been successfully submitted for review
			// & approval.
			String arrString[] = ctrlValue.split("\\s+");

			Cell uniqueTestCaseID = uniqueRow.createCell(uniqueValuesHashMap.get("TestCaseID").intValue());
			Cell uniqueCell = uniqueRow.createCell(uniqueValuesHashMap.get(WebHelper.columnName).intValue());
			uniqueTestCaseID.setCellValue(controller.controllerTestCaseID.toString());

			for (int i = 0; i < arrString.length; i++) {// Minaakshi :
														// 03-10-2018 Added
														// request# condition
				if (arrString[i].toString().equalsIgnoreCase("request") || arrString[i].toString().equalsIgnoreCase("request#")
						|| arrString[i].toString().equalsIgnoreCase("Id")
						|| (arrString[i].toString().equalsIgnoreCase("termination") && !arrString[i + 1].toString().equalsIgnoreCase("request"))) {
					uniqueCell.setCellValue(arrString[i + 1].toString());
					break;
				}
			}

			in.close();
			FileOutputStream out = new FileOutputStream(Config.transactionInfo);
			uniqueWB.write(out);
		} catch (FileNotFoundException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		}
		return true;
	}

	public static String convertDate(String strValue) {
		String strTemp = null;
		// 09/03/2018 2018-08-31 00:00:00.0|2018-09-03 00:00:00.0
		DateFormat inputFormat = new SimpleDateFormat("MM/dd/yyyy");
		DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		try {
			Date date = inputFormat.parse(strValue);
			strTemp = outputFormat.format(date);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return strTemp;

	}

	public static String getDynamicSQLQuery(String tableID, String ctrlName) throws IOException {

		String uniqueNumber = "";
		String uniqueNumber1 = "";
		String uniqueNumber2 = "";

		if (ctrlName.contains("|")) {
			String[] arrTempCntrlVal = ctrlName.split("\\|");
			uniqueNumber = ReadFromExcelUsingColumnName("", arrTempCntrlVal[0].toString());
			uniqueNumber1 = ReadFromExcelUsingColumnName("", arrTempCntrlVal[1].toString());
			uniqueNumber2 = ReadFromExcelUsingColumnName("", arrTempCntrlVal[2].toString());
		} else {
			uniqueNumber = ReadFromExcelUsingColumnName("", ctrlName);
		}

		if (uniqueNumber.contains(",")) {
			String[] arrTempVal = uniqueNumber.split(",");
			tableID = tableID.replace("$value1", arrTempVal[0].toString());
			tableID = tableID.replace("$value2", arrTempVal[1].toString());
		} else if (tableID.contains(" OR ")) {
			tableID = tableID.replace("$value1", uniqueNumber);
			tableID = tableID.replace("$value2", uniqueNumber);
		} else if (tableID.contains(" AND ")) {
			tableID = tableID.replace("$value1", uniqueNumber);
			tableID = tableID.replace("$value2", uniqueNumber1);
			tableID = tableID.replace("$value3", uniqueNumber2);
		} else {
			tableID = tableID.replace("$value", uniqueNumber);
		}

		return tableID;

	}

	public static String getDataFetchingSQLQuery(String sqlQuery, String ctrlValue) throws IOException {

		if (sqlQuery.contains("IN (")) {
			String[] arrTempVal = ctrlValue.split(",");
			sqlQuery = sqlQuery.replace("$value1", arrTempVal[0].toString());
			sqlQuery = sqlQuery.replace("$value2", arrTempVal[1].toString());
		} else {
			sqlQuery = sqlQuery.replace("$value", ctrlValue);
		}

		return sqlQuery;

	}

	public static String getDynamicExpectedValue(String strValue, String ActValue) throws IOException {
		String strExptValue = "";
		String tempExpecetedValue = "";
		String tempstrValue = "";
		String tempstrValue1 = "";
		int i = 0;

		// SMK_TC_02|AGYName$- Trupanion Minaakshi : 14-11-2018
		// 41196- Trupanion
		if (strValue.contains("$")) {
			String[] arrOfStr1 = strValue.split("\\$");
			tempstrValue = arrOfStr1[0].toString();
			tempstrValue1 = arrOfStr1[1].toString();
		} else {
			tempstrValue = strValue;
		}

		// tempstrValue = strValue;
		// String[] arrOfStr = strValue.split("\\|");

		String[] arrOfStr = tempstrValue.split("\\|");

		if (arrOfStr.length > 2) {// Minaakshi : 03-10-2018

			for (i = 1; i < arrOfStr.length; i++) {
				tempExpecetedValue = tempExpecetedValue + " " + arrOfStr[i].toString();
				tempExpecetedValue = tempExpecetedValue.trim();
			}

			i = 0;
			for (i = 1; i < arrOfStr.length; i++) {
				tempstrValue = ReadFromExcelUsingColumnName(arrOfStr[0].toString(), arrOfStr[i].toString());
				tempExpecetedValue = tempExpecetedValue.replace(arrOfStr[i].toString(), tempstrValue);
			}
		}

		else {
			tempExpecetedValue = ReadFromExcelUsingColumnName(arrOfStr[0].toString(), arrOfStr[1].toString());
		}

		if ((arrOfStr[1].toString().contains("SSN#")) || (arrOfStr[1].toString().contains("FEIN#"))) {
			String replacedtempExpecetedValue = tempExpecetedValue.replace("-", "");
			tempExpecetedValue = replacedtempExpecetedValue;
		} else if ((arrOfStr[1].toString().contains("Date")) && (ActValue.contains("-"))) {
			String replacedtempExpecetedValue = convertDate(tempExpecetedValue);
			tempExpecetedValue = replacedtempExpecetedValue;
		}

		if (strValue.contains("$")) {
			strExptValue = tempExpecetedValue + tempstrValue1;
		} else {
			strExptValue = tempExpecetedValue;
		}

		// strExptValue = tempExpecetedValue;

		return strExptValue;

	}

	public static String getExpectedValueByFormula(String expectedValue, String actualValue, Sheet expectedSheet, Cell expectedCell) {

		String req = "";

		DataFormatter fmt = new DataFormatter();

		HSSFFormulaEvaluator.evaluateAllFormulaCells(expectedSheet.getWorkbook()); // Minaakshi
																					// :
																					// 04-01-2019
		FormulaEvaluator evaluator = expectedSheet.getWorkbook().getCreationHelper().createFormulaEvaluator();

		// CellValue cellValue = evaluator.evaluate(expectedCell);
		req = fmt.formatCellValue(expectedCell, evaluator);

		if (actualValue.contains("-")) {
			String replacedtempExpecetedValue = convertDate(req);
			req = replacedtempExpecetedValue;
		}

		return req;
	}
}
