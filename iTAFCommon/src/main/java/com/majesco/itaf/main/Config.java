package com.majesco.itaf.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * This class is responsible for loading all the configurations from Config xls
 * and making them available to the application to access.
 * 
 * @author shantanuc
 * 
 */
public class Config {

	private final static Logger log = Logger.getLogger(Config.class);

	private static HashMap<String, Object> configHashMap = new HashMap<String, Object>();
	

	public static String projectName = "";
	public static String productTeam = null;
	public static String SikuliScreenValue = null;
	public static String SikuliScr = null;

	public static String runbatchinputsheetpath = null;
	public static String errorlog = "";
	public static String applicationdatabaseusername = null;
	public static String applicationdatabasepassword = null;
	public static String databaseHost = null;
	public static String databasePort = null;
	public static String databaseSID = null;

	public static String jbeamdatabaseusername = null;
	public static String jbeamdatabasepassword = null;
	public static String jbeamHost = null;
	public static String jbeamPort = null;
	public static String jbeamSID = null;
	public static String jbeamusername = null;
	public static String jbeampassword = null;
	public static String jbeamVersion = null;
	public static String changebusinessdaterequestxmlpath = null;
	public static String changebusinessdatewsdl = null;
	public static String databaseType = null;
	public static String databaseName = null;
	public static String user = null;
	public static String password = null;
	
	public static String endComparison = null;
	public static String expectedValuesPath = null;
	public static String appendData = null;
	public static String recovery_scenario = null;
	public static String runbatchxmlpath = null;
	public static String runbatchxmlpath_day = null;
	public static String runbatchxmlpath_night = null;
	public static String verificationResultPath = null,appendVerificationResultPath="", appendResultOutput="";
	public static String dashboardResultPath;
	
	public static String seleniumExecution,transactionInfo, policyUniqueNoPath, suiteDataPath, structureSheetFilePath;
	public static String defaultLogin, applicationName, verificationTableISTPath,verificationTemplatePath,actualValuesValuesPath; 
	public static String expectedValuesValuesPath, executionStatusReportUtility,webserviceComparisonUtilityPath, authTokenURL,restfulServiceComparisonUtilityPath;
	public static String emailId, resultOutput, suiteTesting, dashboardFilepath, webserviceComparison, backupFilepath,restfulserviceComparison;
	public static String authScope, authTokenUsername, authTokenPassword, authTokenClientId, authTokenClientSecret,authEnabled;
	public static String controllerFilePath, transactionInputFilePath,inputDataFilePath,applyStaticWait,copyServerRemotePath,
			resultFilePath, executionApproach, flatFileHostName,flatFileUserName,flatFilePassword,flatFilePort ;
	public static String timeOut, userInteraction, cycleNumber, restComparisonResultPath;
	public static String browserType, baseURL;
	public static String emailTransactionInfo="false", emailOnFailure="false";

	public static DateFormat dtFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	private static String[] replaceSequences = { " & $B$2 & ", " &$B$2& ",
			"&$B$2& ", "&$B$19&" };
	
	// PDF Comparison functionality added below
	public static String defaultLocation1 = null;
	public static String defaultLocation2 = null;
	public static String expectedLocation = null;
	public static String actualLocation = null;
	public static String pdfBackupLocation = null;
	public static String pdfParamPath = null;
	public static String pdfParamResultPath = null;
	
	//get path of files for comparison for PAS
	public static String expFilePath= null; 
	public static String actualPdfDownloadPath=null;
	public static String pdfCompResultPath= null;
	public static String csvCompareReportFormat, csvCompareReportExpectedPath,
			csvCompareReportActualPath, csvCompareReportConfigPath,
			csvCompareReportResultPath;

	public static void initializeConfigMap(String filePathFileName)
			throws IOException, Exception {
		Sheet configSheet = null;
		Row rowActual = null;
		String parameterName = null;
		String value = null;

		DataFormatter format = new DataFormatter();
		String projectPath = System.getProperty("user.dir");
		String configPath = projectPath + "\\" + filePathFileName;

		configSheet = getExcelSheet(configPath, "Config");
		int rowCount = configSheet.getLastRowNum() + 1;

		for (int rowIndex = 1; rowIndex < rowCount; rowIndex++) {
			rowActual = configSheet.getRow(rowIndex);
			parameterName = format.formatCellValue(rowActual.getCell(0));
			value = format.formatCellValue(rowActual.getCell(1));
			value = value.replace("\"", "");
			if (StringUtils.isNotBlank(parameterName) || StringUtils.isNotBlank(value)) {
				configHashMap.put(parameterName, value);
			}
		}
		
		projectName = getConfigValue("PROJECT_NAME");

		for (int rowIndex2 = 1; rowIndex2 < rowCount; rowIndex2++) {

			rowActual = configSheet.getRow(rowIndex2);
			parameterName = format.formatCellValue(rowActual.getCell(0));
			value = format.formatCellValue(rowActual.getCell(1));
			value = value.replace("\"", "");

			for (String replaceSeq : replaceSequences) {
				if (value.contains(replaceSeq)) {
					value = value.replace(replaceSeq, projectName);
					break;
				}
			}

			if (StringUtils.isNotBlank(parameterName)
					|| StringUtils.isNotBlank(value)) {
				configHashMap.put(parameterName, value);
			}
		}

		loadConfigData();
	}

	/** Loads the Config sheet into HashMap **/
	public static void loadConfigData()
			throws Exception {

		try {
			projectName = getConfigValue("PROJECT_NAME");
			endComparison = getConfigValue("END_COMPARISON");
			appendData = getConfigValue("APPEND_DATA_RUNTIME");
			recovery_scenario = getConfigValue("RECOVERY_SCENARIO");
			errorlog = getConfigValue("WEBSERVICE_ERRORLOG_FILEPATH");

			applicationdatabaseusername = getConfigValue("APPLICATIONDATABASEUSERNAME");
			applicationdatabasepassword = getConfigValue("APPLICATIONDATABASEPASSWORD");
			databaseHost = getConfigValue("DATABASE_HOST_IP_ADDRESS");
			databasePort = getConfigValue("DATABASE_PORT");
			databaseSID = getConfigValue("DATABASE_SID");
			databaseName = getConfigValue("DATABASE_NAME");
			databaseType = getConfigValue("DATABASE_TYPE");

			jbeamdatabaseusername = getConfigValue("JBEAMDATABASEUSERNAME");
			jbeamdatabasepassword = getConfigValue("JBEAMDATABASEPASSWORD");
			jbeamHost = getConfigValue("JBEAM_HOST_IP_ADDRESS");
			jbeamPort = getConfigValue("JBEAM_PORT");
			jbeamSID = getConfigValue("JBEAM_SID");
			jbeamusername = getConfigValue("JBEAM_USERID");
			jbeampassword = getConfigValue("JBEAM_PASSWORD");
			jbeamVersion = getConfigValue("JBEAM_VERSION");
			user = getConfigValue("APPLICATION_USER_ID");
			password = getConfigValue("APPLICATION_PASSWORD");

			expectedValuesPath = expectedValuesValuesPath;

			runbatchxmlpath = getConfigValue("JBEAMDAYBATCHXMLPATH");
			runbatchxmlpath_day = getConfigValue("JBEAM_DAY_BATCH_XMLPATH");
			runbatchxmlpath_night = getConfigValue("JBEAM_NIGHT_BATCH_XMLPATH");

			runbatchinputsheetpath = getConfigValue("JBEAMDAYBATCHINPUT");
			changebusinessdaterequestxmlpath = getConfigValue("CHANGEBUSINESSDATE_REQUEST_XML");
			changebusinessdatewsdl = getConfigValue("CHANGEBUSINESSDATE_WSDL");
			
			verificationResultPath = getConfigValue("VERIFICATIONRESULTSPATH");
			appendVerificationResultPath=getConfigValue("PAS_APPEND_VERIFICATIONRESULTSPATH");
			dashboardResultPath = getConfigValue("DASHBOARDRESULTSPATH");
			timeOut = getConfigValue("TIMEOUT");
			controllerFilePath = getConfigValue("CONTROLLER_FILEPATH");
			transactionInputFilePath = getConfigValue("TRANSACTION_INPUT_FILEPATH");
			productTeam = getConfigValue("PRODUCT_TEAM");
			resultFilePath = getConfigValue("RESULT_FILEPATH");
			executionApproach = getConfigValue("EXECUTION_APPROACH");
			userInteraction = getConfigValue("USERINTERACTION");
			cycleNumber = getConfigValue("CYCLENUMBER");
			flatFileHostName = getConfigValue("FLATFILE_HOSTNAME");
			flatFileUserName = getConfigValue("FLATFILE_USERNAME");
			flatFilePassword = getConfigValue("FLATFILE_PASSWORD");
			flatFilePort = getConfigValue("FLATFILE_PORT");
			inputDataFilePath = getConfigValue("INPUT_DATA_FILEPATH");
			applyStaticWait = getConfigValue("APPLY_STATIC_WAIT");
			copyServerRemotePath = getConfigValue("COPY_SERVER_REMOTE_PATH");
			webserviceComparisonUtilityPath = getConfigValue("WEBSERVICE_COMPARISION_UTILITYPATH");
			restComparisonResultPath = getConfigValue("REST_COMPARISON_RESULT_PATH");
			restfulServiceComparisonUtilityPath = getConfigValue("RESTFULSERVICE_COMPARISON_UTILITYPATH");
		    restfulserviceComparison = getConfigValue("RESTFULSERVICE_COMPARISON");

			authScope = Config.getConfigValue("AUTH_SCOPE");
			authTokenUsername = Config.getConfigValue("AUTH_TOKEN_USERNAME");
			authTokenPassword = Config.getConfigValue("AUTH_TOKEN_PASSWORD");
			authTokenClientId = Config.getConfigValue("AUTH_TOKEN_CLIENT_ID");
			authTokenClientSecret = Config.getConfigValue("AUTH_TOKEN_CLIENT_SECRET");
			authEnabled = Config.getConfigValue("AUTH_ENABLED");
			authTokenURL = Config.getConfigValue("AUTH_TOKEN_URL");
			emailId = getConfigValue("EMAIL_ID");
			emailOnFailure = getConfigValue("EMAIL_ON_FAILURE");
			resultOutput = getConfigValue("RESULTOUTPUT");
			appendResultOutput=getConfigValue("PAS_APPEND_RESULTOUTPUT");
			suiteTesting = getConfigValue("SUITE_TESTING");
			dashboardFilepath = getConfigValue("DASHBOARD_FILEPATH");
			webserviceComparison = getConfigValue("WEBSERVICE_COMPARISION");
			backupFilepath = getConfigValue("BACKUP_FILEPATH");
			executionStatusReportUtility = getConfigValue("EXECUTION_STATUS_REPORT_UTILITY");
			defaultLogin = getConfigValue("DEFAULTLOGIN");
			applicationName = getConfigValue("APPLICATION_NAME");
			verificationTableISTPath = getConfigValue("VERIFICATIONTABLELISTPATH");
			
			verificationTemplatePath = getConfigValue("VERIFCATIONTEMPLATEPATH");
			actualValuesValuesPath = getConfigValue("ACTUALVALUESPATH");
			expectedValuesValuesPath = getConfigValue("EXPECTEDVALUESPATH");
			seleniumExecution = getConfigValue("SELENIUMEXECUTION");
			transactionInfo = getConfigValue("TRANSACTION_INFO");
			emailTransactionInfo=getConfigValue("EMAIL_TRANSACTION_INFO");
			policyUniqueNoPath = getConfigValue("POLICY_UNIQUE_NUMBER_PATH");
			suiteDataPath = getConfigValue("SUITE_DATA_PATH");
			structureSheetFilePath = getConfigValue("STRUCTURE_SHEET_FILEPATH");
			browserType = getConfigValue("BROWSERTYPE");
			baseURL = getConfigValue("BASEURL");
			// pdf utility properties added below
			defaultLocation1 = getConfigValue("DEFAULT_LOCATION_AGENCY");
			defaultLocation2 = getConfigValue("DEFAULT_LOCATION_SINGLE");
			expectedLocation = getConfigValue("EXPECTED_LOCATION");
			actualLocation = getConfigValue("ACTUAL_LOCATION");
			pdfBackupLocation = getConfigValue("PDFBACKUP_LOCATION");
			pdfParamPath = getConfigValue("PDF_CONFIG_PATH");
			pdfParamResultPath = getConfigValue("PDF_RESULT_PATH");

			// PAS specific changes for pdf comparision
			expFilePath = getConfigValue("PDF_COMPARISON_ExpectedPDF");
			actualPdfDownloadPath = getConfigValue("PDF_COMPARISON_DownloadActualPDF");
			pdfCompResultPath = getConfigValue("PDF_COMPARISON_PDF_MismatchResults");
			
			csvCompareReportFormat = getConfigValue("REPORTFORMAT");
			csvCompareReportExpectedPath = getConfigValue("REPORTEXPECTEDPATH");
			csvCompareReportActualPath = getConfigValue("REPORTACTUALPATH");
			csvCompareReportConfigPath = getConfigValue("REPORTCONFIGPATH");
			csvCompareReportResultPath = getConfigValue("REPORTRESULTPATH");
					
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		}
	}

	public static int getConfgiMapSize(){
		return configHashMap.size();
	}

	public static String getConfigValue(String string) {
		Object value = configHashMap.get(string);
		if (value != null) {
			return value.toString();
		}
		return null;
	}
	
	public static Sheet getExcelSheet(String FilePath, String SheetName)
			throws Exception {
		Sheet workSheet = null;
		Workbook workBook = null;
		try {
			System.out.println("FilePath is:" + FilePath);
			System.out.println("SheetName is:" + SheetName);
			InputStream myXls = new FileInputStream(FilePath);

			String configsheetextension = FilenameUtils.getExtension(FilePath);
			if (configsheetextension.equalsIgnoreCase("xls")) {
				workBook = new HSSFWorkbook(myXls);
			} else if (configsheetextension.equalsIgnoreCase("xlsx") 
					|| configsheetextension.equalsIgnoreCase("xlsm")){
				workBook = new XSSFWorkbook(myXls);
			} else {
				throw new RuntimeException("Invalid file type. File type should be one of 'xls', 'xlsx' or 'xlsm'");
			}
			workSheet = workBook.getSheet(SheetName);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw e;
		} finally {
			try {
				workBook.close();
			} catch (IOException e) {}
		}
		return workSheet;
	}

}
