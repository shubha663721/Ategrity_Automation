package com.majesco.itaf.batch;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.majesco.itaf.main.Config;
import com.majesco.itaf.main.ITAFWebDriver;
import com.majesco.itaf.main.MainController;
import com.majesco.itaf.main.ObjectFactory;
import com.majesco.itaf.main.WebHelperUtil;
import com.majesco.itaf.util.ExcelUtility;
import com.majesco.itaf.util.JDBCConnection;
import com.majesco.itaf.vo.Reporter;
import com.majesco.itaf.webservice.WebService;

public class RunBatch {
	private final static Logger log = Logger.getLogger(RunBatch.class);
	public static String XMLDataFile = null;
	public static String Business_Date = null;
	public static String Batch_Date = null;
	public static String tempDate = null;
	public static String PDC_Count = null;
	public static String BatchExecutionDate = null;
	public static String Job_Status = null;

	public static Cell sWSDL_URL = null;
	public static Cell sContent_Type = null;

	public static String batchNo = null;
	public static int Batch_No = 0;
	public static HashMap<String, Object> BatchHashMap = new HashMap<String, Object>();
	public static String Batch_Status = null;
	public static String Batch_End = null;
	public static int batchTimer = 1;
	public static String stopBatch = null;
	public static int retVal = 0;
	public static String strBatchCompleteData = null;
	public static HashMap<String, Integer> HeaderValues = new HashMap<String, Integer>();

	private static boolean isMsSQLDB = Config.databaseType.equalsIgnoreCase("MsSQL");
	private static boolean isOracleDB = Config.databaseType.equalsIgnoreCase("Oracle");

	private static ITAFWebDriver webDriver = ITAFWebDriver.getInstance();
	private static MainController controller = ObjectFactory.getMainController();

	public static Reporter RunBatch_WS() {
		return runBatch(true);
	}

	public static Reporter RunBatch_WSFF() {
		return runBatch(false);
	}

	private static Reporter runBatch(boolean considerPDCCount) {
		Date startDate = new Date();
		int Job_Status_count = 0;
		Batch_End = null;
		webDriver.getReport().setFromDate(Config.dtFormat.format(startDate));
		webDriver.getReport().setIteration(Config.cycleNumber);
		webDriver.getReport().setTestcaseId(controller.controllerTestCaseID.toString());
		webDriver.getReport().setGroupName(controller.controllerGroupName.toString());
		webDriver.getReport().setTrasactionType(controller.controllerTransactionType.toString());
		webDriver.getReport().setCycleDate(controller.cycleDateCellValue);
		webDriver.getReport().setTestDescription(controller.testDesciption);
		Reporter report = new Reporter();

		// Meghna--For Day and Night Batch//
		String batch_trans_type = controller.controllerTransactionType.toString();
		String testCaseId = controller.controllerTestCaseID.toString();

		if (batch_trans_type.equalsIgnoreCase("RunBatch_JBEAM_NIGHT")) {
			XMLDataFile = Config.runbatchxmlpath_night;
		} else if (batch_trans_type.equalsIgnoreCase("RunBatch_JBEAM_DAY")) {
			XMLDataFile = Config.runbatchxmlpath_day;
		} else {
			XMLDataFile = Config.runbatchxmlpath;
		}

		Business_Date = controller.cycleDateValue;
		log.info("Business Date to start run batch :" + Business_Date);
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(new SimpleDateFormat("MM/dd/yyyy").parse(Business_Date));
		} catch (ParseException e1) {
			log.error(e1.getLocalizedMessage(), e1);
			webDriver.getReport().setStatus("FAIL");
			webDriver.getReport().setMessage(e1.getLocalizedMessage());
		}
		int weekday = calendar.get(Calendar.DAY_OF_WEEK);
		BatchHashMap.put("STOP_BATCH", "FALSE");

		log.info("day of the week is : " + weekday);
		log.info("Modified Business Date : " + Business_Date);
		log.info("batch_trans_type : " + batch_trans_type);

		try {
			if (batch_trans_type.equalsIgnoreCase("RunBatch_JBEAM_NIGHT")) {
				Batch_Date = getNextBatchDate();
				Business_Date = getNextBusinessDate();
			} else if (batch_trans_type.equalsIgnoreCase("RunBatch_JBEAM_DAY")) {
				Batch_Date = getBatchDate();
				Business_Date = getBusinessDate();

			} else if (batch_trans_type.equalsIgnoreCase("RunBatch_JBEAMFF")) {

				if (testCaseId.equalsIgnoreCase("RunBatch_JBEAM_NIGHT")) {
					Batch_Date = getNextBatchDate();
					Business_Date = getNextBusinessDate();
					log.info("RunBatch_JBEAM_NIGHT:RunBatch_JBEAMFF:Business_Date : " + Business_Date);

				} else if (testCaseId.equalsIgnoreCase("RunBatch_JBEAM_DAY")) {
					Batch_Date = getBatchDate();
					Business_Date = getBusinessDate();
					log.info("RunBatch_JBEAM_DAY:RunBatch_JBEAMFF:Business_Date : " + Business_Date);
				} else {
					Batch_Date = getNextBatchDate();
					Business_Date = getNextBusinessDate();
					log.info("RunBatch_JBEAM_Default:RunBatch_JBEAMFF:Business_Date : " + Business_Date);
				}
			} else if (batch_trans_type.equalsIgnoreCase("RunBatch_JBEAM")) {

				if (testCaseId.equalsIgnoreCase("RunBatch_JBEAM_NIGHT")) {
					Batch_Date = getNextBatchDate();
					Business_Date = getNextBusinessDate();
					log.info("RunBatch_JBEAM_NIGHT:RunBatch_JBEAM:Business_Date : " + Business_Date);

				} else if (testCaseId.equalsIgnoreCase("RunBatch_JBEAM_DAY")) {
					Batch_Date = getBatchDate();
					Business_Date = getBusinessDate();
					log.info("RunBatch_JBEAM_DAY:RunBatch_JBEAM:Business_Date : " + Business_Date);
				} else {
					Batch_Date = getNextBatchDate();
					Business_Date = getNextBusinessDate();
					log.info("RunBatch_JBEAM_Default:RunBatch_JBEAM:Business_Date : " + Business_Date);
				}
			}

			log.info("Modified Batch Date : " + Batch_Date);

			BatchExecutionDate = Business_Date;

			log.info("BatchExecutionDate : " + BatchExecutionDate);

			Job_Status_count = getBatchJobCount();

			if (considerPDCCount) {
				int PDC_Count_Num = getPDCCount();

				if (Job_Status_count == 0 && PDC_Count_Num == 0) {
					log.info("Batch object as well as PDC object Not Found for the BatchExecutionDate : " + BatchExecutionDate);
					webDriver.getReport().setStatus("Pass");
					webDriver.getReport().setMessage(
							"Batch object as well as PDC object Not Found for the BatchExecutionDate : " + BatchExecutionDate);
					return report;
				} else if (Job_Status_count > 0 || PDC_Count_Num > 0) {
					executeBatch();
				}
			} else {

				executeBatch();

			}
		} catch (SQLException sqle) {
			log.error(sqle.getLocalizedMessage(), sqle);
			webDriver.getReport().setStatus("FAIL");
			webDriver.getReport().setMessage(sqle.getLocalizedMessage());
		} catch (ClassNotFoundException cnfe) {
			log.error(cnfe.getLocalizedMessage(), cnfe);
			webDriver.getReport().setStatus("FAIL");
			webDriver.getReport().setMessage(cnfe.getLocalizedMessage());
		} catch (IOException ioe) {
			log.error(ioe.getLocalizedMessage(), ioe);
			webDriver.getReport().setStatus("FAIL");
			webDriver.getReport().setMessage(ioe.getLocalizedMessage());
		} catch (Exception e) {
			log.error(e.getLocalizedMessage(), e);
			webDriver.getReport().setStatus("FAIL");
			webDriver.getReport().setMessage(e.getLocalizedMessage());
		}
		return report;
	}

	private static void executeBatch() throws ClassNotFoundException, SQLException, Exception {
		String sequenceNo = null;
		int waitcount = 0;

		DateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.ENGLISH); // yyyy-MM-dd
																							// HH:mm:ss
		Date newBatchDate = simpleDateFormat.parse(Batch_Date);
		DateFormat cycleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Batch_Date = cycleDateFormat.format(newBatchDate);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(XMLDataFile);

		doc.getDocumentElement().normalize();

		Node configuration = doc.getElementsByTagName("instructionParameters").item(2);
		NodeList list = configuration.getChildNodes();

		String NodeValue = "value";
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);

			if (NodeValue.equals(node.getNodeName())) {
				node.setTextContent(Batch_Date + " 23:59:59");
				break;
			}
		}

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(XMLDataFile));
		transformer.transform(source, result);

		Sheet valuesSheet = ExcelUtility.GetSheet(Config.runbatchinputsheetpath, "Values");
		if (HeaderValues.isEmpty() == true) {
			HeaderValues = WebHelperUtil.getValueFromHashMap(valuesSheet);
		}

		Row currentRow = valuesSheet.getRow(1);

		sWSDL_URL = currentRow.getCell(HeaderValues.get("WSDL_URL"));
		sContent_Type = currentRow.getCell(HeaderValues.get("ContentType"));

		String sRequestXML = XMLDataFile;

		try {
			String sResponseXML = WebService.callWebService_JBeam(sWSDL_URL.toString(), sRequestXML.toString(), sContent_Type.toString());
			sequenceNo = WebService.getXMLResponseTagValue(sResponseXML, "batchDetails", "instructionSeqNo", 0);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("Sequence Number of Response XML is: " + sequenceNo);
		if ((sequenceNo != null) == true) {// Changes made for sequenceNo is
											// null then not need to execute
											// further ---Aniruddha 02/15/2016
			waitcount = 0;
			webDriver.getReport().setMessage("Instruction Sequence No is : " + sequenceNo); // Mandar

			do {
				batchNo = executeQueryOnCoreDB("Select BATCH_NO from BATCH where INSTRUCTION_SEQ_NO =" + sequenceNo);
				log.info("Batch No is : " + batchNo);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					log.error(e.getMessage(), e);
					Thread.currentThread().interrupt();
				}
				waitcount++;
			} while (batchNo == null && waitcount <= 60);

			if (batchNo == null) {
				controller.pExecution = true;
				log.info("Instruction sequence number is not generated, Execution is going to stop");
				BatchHashMap.put("STOP_BATCH", "TRUE");
				// MainController.pauseFun("Instruction Sequence number is not generated, Framework stops execution");
				String BatchError = "Instruction Sequence number is not generated, Framework stops execution";
				if (!(webDriver.getEmail().equalsIgnoreCase("NA")) && !(webDriver.getEmail().equalsIgnoreCase(""))
						&& !(webDriver.getEmail().equalsIgnoreCase(null))) {
					strBatchCompleteData = webDriver.getEmail() + "#" + BatchError + "#" + "Batch No not Fetched for sequence No:" + sequenceNo;
					Runtime.getRuntime().exec("wscript BatchFailMail.vbs " + (char) 34 + strBatchCompleteData + (char) 34);
				} else {
					webDriver.getFrame().setVisible(true);
					webDriver.getFrame().setAlwaysOnTop(true);
					webDriver.getFrame().setLocationRelativeTo(null);
					JOptionPane.setRootFrame(webDriver.getFrame());
					JOptionPane.showMessageDialog(webDriver.getFrame(), "Batch not working and email id not configured in config sheet");
					controller.pExecution = true;
					webDriver.getFrame().dispose();
				}
			}

			stopBatch = BatchHashMap.get("STOP_BATCH").toString();

			boolean bBatchRunning = true;
			batchTimer = 0;
			log.info("Waiting for Batch to complete.");
			while (bBatchRunning || batchTimer <= 60 || !stopBatch.equalsIgnoreCase("TRUE")) {
				Batch_End = executeQueryOnCoreDB("Select BATCH_END_REASON from BATCH where INSTRUCTION_SEQ_NO =" + sequenceNo);
				if (Batch_End != null && Batch_End.trim().length() > 0) {
					break;
				}
				Thread.sleep(1000 * 3);
				batchTimer = batchTimer + 1;
			}
			log.info("Batch End Reason : " + Batch_End);

			if (Batch_End.equalsIgnoreCase("USER_INTERRUPTED")) {
				log.info("Batch End Reason Fetched from Database : " + Batch_End);
				webDriver.getReport().setStatus("BATCH USER INTERRUPTED");
			} else if (Batch_End.equalsIgnoreCase("BATCH_COMPLETED")) {
				webDriver.getReport().setStatus("PASS");
				log.info("Batch End Reason Fetched from Database : " + Batch_End);
				webDriver.getReport().setMessage("Batch Completed -- Batch No is :" + batchNo);// Mandar
			} else {
				webDriver.getReport().setStatus("FAIL");
				log.info("Batch End Reason Fetched from Database : " + Batch_End);
				webDriver.getReport().setMessage("Batch Failed -- Batch No is :" + batchNo);// Mandar
			}

			String failedRecords = getFailedRecords();
			if (failedRecords != null) {
				controller.batchRecoveryScenario(batchNo);
			}
		} else {
			log.info("Instruction sequence number is not generated, Execution is going to stop");
			BatchHashMap.put("STOP_BATCH", "TRUE");
			webDriver.getReport().setStatus("FAIL");
			webDriver.getReport().setMessage(("Instruction sequence number is not generated, Execution is going to stop"));// Mandar
			controller.pExecution = true;
		}
	}

	private static String getNextBatchDate() throws ClassNotFoundException, SQLException, Exception {
		String query = null;

		if (isMsSQLDB) {
			query = "Select convert(varchar(10),CAST(NEXT_BUSINESS_DATE AS datetime),110) from business_day";// Meghna--Selecting
																												// next_business_date
		} else if (isOracleDB) {
			query = "Select TO_CHAR(next_business_date,'mm-dd-yyyy') from business_day"; // Meghna--Selecting
																							// next_business_date
		} else {
			throw new RuntimeException("No database selected");
		}

		return executeQueryOnBillingDB(query);
	}

	private static String getBatchDate() throws ClassNotFoundException, SQLException, Exception {
		String query = null;

		if (isMsSQLDB) {
			query = "Select convert(varchar(10),CAST(BUSINESS_DATE AS datetime),110) from business_day";// Meghna--Selecting
																										// next_business_date
		} else if (isOracleDB) {
			query = "Select TO_CHAR(business_date,'mm-dd-yyyy') from business_day"; // Meghna--Selecting
																					// next_business_date
		} else {
			throw new RuntimeException("No database selected");
		}

		return executeQueryOnBillingDB(query);
	}

	private static String getBusinessDate() throws ClassNotFoundException, SQLException, Exception {
		String query = null;
		if (isMsSQLDB) {
			query = "Select Replace(convert(varchar(10),CAST(BUSINESS_DATE AS datetime),6),' ','-') from business_day";// Mandar
																														// --
																														// Generic
																														// Query//Meghna--Selecting
																														// next_business_date
		} else if (isOracleDB) {
			query = "Select TO_CHAR(business_date,'dd-mon-yy') from business_day"; // Meghna--Selecting
																					// next_business_date
		}
		return executeQueryOnBillingDB(query);
	}

	private static String getNextBusinessDate() throws ClassNotFoundException, SQLException, Exception {
		String query = null;
		if (isMsSQLDB) {
			query = "Select Replace(convert(varchar(10),CAST(NEXT_BUSINESS_DATE AS datetime),6),' ','-') from business_day";// Mandar
																															// --
																															// Generic
																															// Query//Meghna--Selecting
																															// next_business_date
		} else if (isOracleDB) {
			query = "Select TO_CHAR(next_business_date,'dd-mon-yy') from business_day"; // Meghna--Selecting
																						// next_business_date
		}
		return executeQueryOnBillingDB(query);
	}

	private static int getBatchJobCount() {
		String query = null;
		int jobCount = 0;
		if (isMsSQLDB) {
			query = "Select count(*) from job_schedule where Replace(convert(varchar(10),CAST(EXECUTION_DATE AS date),6),' ','-') <= CAST(" + "'"
					+ BatchExecutionDate + "' AS DATE)" + " " + "and" + " job_status = 'SCHEDULED'";
		}// Tanvi : 17-May 2017 : to add MSSql query : End
		else if (isOracleDB) {
			query = "select count(*) from job_schedule where trunc(execution_date) <= " + "'" + BatchExecutionDate + "'" + " " + "and"
					+ " job_status = 'SCHEDULED'";
		}
		log.info("Query:" + query);

		try {
			Job_Status = executeQueryOnBillingDB(query);
			jobCount = Integer.parseInt(Job_Status);
			log.info(Job_Status + " number of object are indetified for batch | execution date " + BatchExecutionDate);
		} catch (Exception e) {
			log.error("Job_Status returns null", e);
			jobCount = 0;
		}
		return jobCount;
	}

	private static int getPDCCount() {
		String query = null;
		int PDC_Count_Num = 0;
		if (isMsSQLDB) {
			query = "Select count(*) from payment_batch where Replace(convert(varchar(10),CAST(deposit_date AS date),6),' ','-') <= CAST(" + "'"
					+ BatchExecutionDate + "' AS DATE)" + " and payment_batch_Status= 'OPEN'";
		}// Tanvi : 17-May 2017 : to add MSSql query : End
		else if (isOracleDB) {
			query = "select count(*) from payment_batch where pdc_flag = 'Y' and deposit_date <= '" + BatchExecutionDate
					+ "' and payment_batch_Status = 'OPEN'";
		}

		try {
			PDC_Count = executeQueryOnBillingDB(query);
			PDC_Count_Num = Integer.parseInt(PDC_Count);
			log.info(PDC_Count + " number of PDC objects are indetified for batch | execution date " + BatchExecutionDate);
		} catch (Exception e) {
			log.error("PDC_Count returns null", e);
			PDC_Count_Num = 0;
		}
		return PDC_Count_Num;
	}

	private static String getFailedRecords() throws ClassNotFoundException, SQLException, Exception {
		String query = null, failedRecords = null;
		if (isMsSQLDB) {
			if ("GroupBilling".equalsIgnoreCase(Config.productTeam)) {
				query = "SELECT CORE.BATCH_NO, BASE.JOB_NAME, CORE.POLICY_NO, CORE.BROKER, BASE.ACCOUNT_SYSTEM_CODE, BASE.BROKER_SYSTEM_CODE,BASE.GROUP_SYSTEM_CODE FROM "
						+ Config.jbeamdatabaseusername
						+ ".dbo.LOG CORE, "
						+ Config.databaseName
						+ ".dbo.JOB_SCHEDULE BASE WHERE CORE.BE_SEQ_NO=BASE.JOB_SEQ AND BASE.JOB_STATUS = 'FAILED' AND CORE.BATCH_NO='"
						+ batchNo
						+ "' AND BASE.JOB_NAME NOT IN ('ENTITY_INTERFACE','PREMIUM_INTERFACE','RCAN_INTERFACE','CHECK_UPDATE_INTERFACE','AP_INTERFACE','RETURNEDPAYMENT_INTERFACE','GL_INTERFACE','POLICY_STATUS_INTERFACE','RREIN_INTERFACE','GENERATE_FILE_NAME','OFS_MERGE_XML_JOB') AND (BASE.POLICY_NO is not null OR BASE.ACCOUNT_SYSTEM_CODE is not null OR BASE.BROKER_SYSTEM_CODE is not null OR BASE.GROUP_SYSTEM_CODE IS NOT NULL)";
				failedRecords = executeQueryOnCoreDB(query);
			} else {
				query = "SELECT CORE.BATCH_NO, BASE.JOB_NAME, CORE.POLICY_NO, CORE.BROKER, BASE.ACCOUNT_SYSTEM_CODE, BASE.BROKER_SYSTEM_CODE FROM "
						+ Config.jbeamdatabaseusername
						+ ".dbo.LOG CORE, "
						+ Config.databaseName
						+ ".dbo.JOB_SCHEDULE BASE WHERE CORE.BE_SEQ_NO=BASE.JOB_SEQ AND BASE.JOB_STATUS = 'FAILED' AND CORE.TASK_NAME is not null AND CORE.BATCH_NO='"
						+ batchNo
						+ "' AND BASE.JOB_NAME NOT IN ('ENTITY_INTERFACE','PREMIUM_INTERFACE','RCAN_INTERFACE','CHECK_UPDATE_INTERFACE','AP_INTERFACE','RETURNEDPAYMENT_INTERFACE','GL_INTERFACE','POLICY_STATUS_INTERFACE','RREIN_INTERFACE','GENERATE_FILE_NAME','OFS_MERGE_XML_JOB') AND (BASE.POLICY_NO is not null OR BASE.ACCOUNT_SYSTEM_CODE is not null OR BASE.BROKER_SYSTEM_CODE is not null)";
				failedRecords = executeQueryOnCoreDB(query);// Mandar --
															// applicationdatabaseusername
															// changed to
															// databaseName --
															// 17/11/2017
			}
		}// Tanvi : 17-May 2017 : to add MSSql query : End
		else if (isOracleDB) {
			query = "SELECT CORE.BATCH_NO, BASE.JOB_NAME, CORE.POLICY_NO, CORE.BROKER, BASE.ACCOUNT_SYSTEM_CODE, BASE.BROKER_SYSTEM_CODE FROM "
					+ Config.jbeamdatabaseusername
					+ ".LOG CORE, "
					+ Config.databaseName
					+ ".JOB_SCHEDULE BASE WHERE CORE.BE_SEQ_NO=BASE.JOB_SEQ AND BASE.JOB_STATUS = 'FAILED' AND CORE.TASK_NAME is not null AND CORE.BATCH_NO='"
					+ batchNo
					+ "' AND BASE.JOB_NAME NOT IN ('ENTITY_INTERFACE','PREMIUM_INTERFACE','RCAN_INTERFACE','CHECK_UPDATE_INTERFACE','AP_INTERFACE','RETURNEDPAYMENT_INTERFACE','GL_INTERFACE','POLICY_STATUS_INTERFACE','RREIN_INTERFACE','GENERATE_FILE_NAME','OFS_MERGE_XML_JOB') AND (BASE.POLICY_NO is not null OR BASE.ACCOUNT_SYSTEM_CODE is not null OR BASE.BROKER_SYSTEM_CODE is not null)";// Mandar
																																																																																																		// --
																																																																																																		// applicationdatabaseusername
																																																																																																		// changed
																																																																																																		// to
																																																																																																		// databaseName
																																																																																																		// --
																																																																																																		// 17/11/2017
			failedRecords = executeQueryOnCoreDB(query);
		}
		return failedRecords;
	}

	private static String executeQueryOnCoreDB(String query) throws ClassNotFoundException, SQLException, Exception {
		ResultSet rs = null;
		String returnVale = null;
		Connection conn = JDBCConnection.establishHTML5BillingCoreDBConn();
		Statement st = conn.createStatement();
		rs = st.executeQuery(query);
		if (rs.next()) {
			returnVale = rs.getString(1);
		}
		st.close();
		rs.close();
		JDBCConnection.closeConnection(conn);
		return returnVale;
	}

	private static String executeQueryOnBillingDB(String query) throws ClassNotFoundException, SQLException, Exception {
		String returnVale = null;
		ResultSet rs;
		Connection conn = JDBCConnection.establishHTML5BillingDBConn();
		Statement st = conn.createStatement();
		rs = st.executeQuery(query);
		if (rs.next()) {
			returnVale = rs.getString(1);
		}
		rs.close();
		st.close();
		JDBCConnection.closeConnection(conn);
		return returnVale;
	}

	private static String executeQuery(ResultSet rs) throws ClassNotFoundException, SQLException, Exception {
		String returnVale = null;
		try {
			if (rs.next()) {
				returnVale = rs.getString(1);
			}
		} finally {
			try {
				rs.close();
			} catch (Exception ex) {
				// Do nothing
			}
		}
		return returnVale;
	}

}

// Originally ending at line 880
