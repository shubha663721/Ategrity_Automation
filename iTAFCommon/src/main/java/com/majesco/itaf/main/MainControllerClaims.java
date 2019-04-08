package com.majesco.itaf.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.majesco.itaf.batch.RunBatch;
import com.majesco.itaf.util.ExcelUtility;
import com.majesco.itaf.util.JDBCConnection;
import com.majesco.itaf.vo.Reporter;
import com.majesco.itaf.webservice.WebService;

public class MainControllerClaims extends MainController {

	private final static Logger log = Logger.getLogger(MainController.class.getName());

	private static HashMap<String, Integer> sheetValues = new HashMap<String, Integer>();
	private static HashMap<String, Integer> controlsheet = new HashMap<String, Integer>();
	private static HashMap<String, Integer> startpointerValues = new HashMap<String, Integer>();
	public static int startCol = 0;
	public static int startRow = 0;
	private static HSSFRow controllerRow = null;
	private static HSSFRow currentRow = null;
	public static boolean recoverydone = false;
	private static int currentSC_NO = 0;
	private static int currentscenarionum = 0;
	private static int currentaccountnum = 0;
	private static String currentAccount_NO = null;
	private static int currentpolicynum = 0;
	private static String currentPolicy_NO = null;
	private static int currentagentnum = 0;
	private static String currentAgent_NO = null;
	private static int failedscenarionum = 0;
	private static int scenarionum = 0;
	public static HSSFSheet MainControlSheet = null;
	public static HSSFRow startpointerRow = null;
	public static int START_ROW = 0;
	public static int START_COLUMN = 0;
	public static HSSFRow CycleDateRow = null;
	public static HSSFCell sc_no = null;
	protected static ResultSet result = null;
	public static String scrshotbody = "";
	public static String strScrCompleteData = "";
	public static String scrEmail = "";
	public static String scrreportsheet = "";
	public static String scrdetailreportsheet = "";
	public static HSSFCell cycleDate = null;
	protected static Statement st = null;
	public static String executionApproach = "";
	public static String cbd_requestxml = null;
	public static String cbd_wsdl = null;
	public static HSSFSheet startpointerSheet = null;
	public static HSSFSheet MainControllerSheet = null;
	public static HSSFRow updatedstartpointerRow = null;
	private static HashMap<String, Integer> updatedstartpointerValues = new HashMap<String, Integer>();
	public static int UPDATED_START_ROW = 0;
	public static int UPDATED_START_COLUMN = 0;
	public static int ustartCol = 0;
	public static int ustartRow = 0;
	public static boolean columnChanged = false;
	public static String jobName = null, PolicyNo = null, brokerNumber = null, AccountNumber = null, brokerSystemCode = null,
			accountSystemCode = null, job_SEQ = null;
	public static boolean rowChanged = false;// Tanvi : 4/11/2017
	public static boolean linearpExecution = false;// Tanvi :4/11/2017
	public static String mainControllerHeaderNo = null;// Tanvi :4/11/2017

	private static ITAFWebDriver webDriver = ITAFWebDriver.getInstance();

	// bhaskar START Pointer Update END
	/**
	 * Finds the Start Pointer in the MainController Sheet and executes the
	 * Transaction
	 **/

	@SuppressWarnings("resource")
	public Reporter ControllerData(String FilePath) throws NullPointerException, Exception {
		// log.info("In MainController value of pauseExecution1:"+pauseExecution);
		Reporter report = new Reporter();
		HSSFSheet reqSheet = null;
		int execFlag = 0, scenarioNO = 0, rowCount = 0;
		try {
			reqSheet = ExcelUtility.getXLSSheet(Config.controllerFilePath, "MainControlSheet");
			sheetValues = WebHelperUtil.getValueFromHashMap(reqSheet);
			execFlag = Integer.parseInt(sheetValues.get("ExecuteFlag").toString());
			scenarioNO = Integer.parseInt(sheetValues.get("SC_NO").toString());
			rowCount = reqSheet.getLastRowNum() + 1;
		} catch (NullPointerException ne) {
			log.error("Failed Read MainControlSheet in MainCOntroller  <-|-> LocalizeMessage " + ne.getLocalizedMessage() + " <-|-> Message "
					+ ne.getMessage() + " <-|-> Cause " + ne.getCause());
			throw new Exception("Failed Read MainControlSheet in MainCOntroller <-|-> LocalizeMessage " + ne.getLocalizedMessage() + " <-|-> Message"
					+ ne.getMessage() + " <-|-> Cause " + ne.getCause());
		} catch (Exception e) {
			log.error("Failed get MainControlSheet in MainCOntroller <-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message "
					+ e.getMessage() + " <-|-> Cause " + e.getCause());
			throw new Exception("Failed get MainControlSheet in MainCOntroller  <-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message"
					+ e.getMessage() + " <-|-> Cause " + e.getCause());
		}
		// log.info("row count is : "+rowCount);
		int colCount = 0;
		// boolean isStartFound = false;
		executionApproach = Config.executionApproach;
		try {
			startpointerSheet = ExcelUtility.getXLSSheet(Config.controllerFilePath, "StartPointer");
			// HSSFSheet startpointerSheet =
			// ExcelUtility.GetSheet(Config.controllerFilePath,"StartPointer");
			startpointerValues = WebHelperUtil.getValueFromHashMap(startpointerSheet);
			startpointerRow = startpointerSheet.getRow(1);
			START_ROW = Integer.parseInt(startpointerValues.get("StartRow").toString());
			START_COLUMN = Integer.parseInt(startpointerValues.get("StartCol").toString());
			startRow = (int) startpointerRow.getCell(START_ROW).getNumericCellValue();
			startCol = (int) startpointerRow.getCell(START_COLUMN).getNumericCellValue();
		} catch (NullPointerException ne) {
			log.error("Failed get Start Pointer  <-|-> LocalizeMessage " + ne.getLocalizedMessage() + " <-|-> Message " + ne.getMessage()
					+ " <-|-> Cause " + ne.getCause());
			throw new Exception("Failed get Start Pointer <-|-> LocalizeMessage " + ne.getLocalizedMessage() + " <-|-> Message" + ne.getMessage()
					+ " <-|-> Cause " + ne.getCause());
		} catch (Exception e) {
			log.error("Failed create Chrome DriverInstance <-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message " + e.getMessage()
					+ " <-|-> Cause " + e.getCause());
			throw new Exception("Failed to read process list  <-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message" + e.getMessage()
					+ " <-|-> Cause " + e.getCause());
		}
		startRow = startRow - 1;
		startCol = startCol - 1;
		// log.info("startRow is : "+startRow);
		// log.info("startCol is : "+startCol);
		// isStartFound = true;
		// bhaskar Time Travel Approach END
		// bhaskar Time Travel Approach ***Commenting the Existing Code*** START
		/*
		 * for(int rowindex=0;rowindex<rowCount&&!isStartFound;rowindex++) {
		 * controllerRow = reqSheet.getRow(rowindex); //TM: commented the
		 * following to avoid continue
		 * if(controllerRow.getCell(execFlag).toString().equals(null)) {
		 * continue; } //TM: following 'if' is replacement of the above
		 * if(controllerRow.getCell(execFlag) != null) {
		 * if(controllerRow.getCell(execFlag).toString().equals("Y")) { colCount
		 * = controllerRow.getLastCellNum()+1; for(int
		 * colIndex=execFlag+1;colIndex<colCount;colIndex++) { HSSFCell cellVal
		 * = controllerRow.getCell(colIndex);
		 * 
		 * //TM: commented the following code to avoid continue if(cellVal ==
		 * null) { log.info("START not Found"); continue;
		 * 
		 * } else //TM: following new if added to check for null and else part
		 * for the same. if(cellVal != null){
		 * if(cellVal.toString().equalsIgnoreCase("START")) { startCol =
		 * colIndex; startRow = rowindex; isStartFound = true; break;
		 * 
		 * } } else{ log.info("START not Found"); }
		 * 
		 * } } else { log.info("Execute Flag is N"); } }
		 * 
		 * }
		 */
		// bhaskar Time Travel Approach ***Commenting the Existing Code*** END
		// log.info("In MainController value of pauseExecution2:"+pauseExecution);
		if (executionApproach.equalsIgnoreCase("Linear")) // Linear
		{
			// for(int rowIndex=startRow;rowIndex<rowCount;rowIndex++)

			ustartCol = startCol;// Tanvi : 4/11/2017
			ustartRow = startRow;// Tanvi : 4/11/2017
			for (int rowIndex = startRow; rowIndex < rowCount; rowIndex++) // Tanvi
																			// :
																			// 4/11/2017

			{
				// Tanvi : 4/13/2017 :Start
				if (pExecution == true) {
					return report;
				}
				// Tanvi : 4/13/2017 : End

				linearpExecution = false;// Tanvi : 4/11/2017
				ustartRow = rowIndex + 1;// Tanvi : 4/11/2017
				pauseExecution = false;

				reqSheet = ExcelUtility.getXLSSheet(Config.controllerFilePath, "MainControlSheet"); // Tanvi
																									// :
																									// 4/11/2017
				sheetValues = WebHelperUtil.getValueFromHashMap(reqSheet); // Tanvi
																			// :
																			// 4/11/2017
				controllerRow = reqSheet.getRow(rowIndex);

				// colCount = controllerRow.getLastCellNum()+1;
				colCount = controllerRow.getLastCellNum();

				if (rowIndex > startRow)// Tanvi : 4/11/2017 :START
				{
					rowChanged = true;
					startCol = 10;
				}
				// //Tanvi : 4/11/2017 :END

				testDesciption = WebHelperUtil.getCellData("Test_Description", reqSheet, rowIndex, sheetValues);
				HSSFCell executeFlag = controllerRow.getCell(execFlag);
				controllerTestCaseID = controllerRow.getCell(Integer.parseInt(sheetValues.get("TestCaseID").toString()));
				controllerGroupName = controllerRow.getCell(Integer.parseInt(sheetValues.get("GroupName").toString()));
				sc_no = controllerRow.getCell(Integer.parseInt(sheetValues.get("SC_NO").toString()));// Tanvi
																										// :
																										// 4/11/2017

				if (controllerTestCaseID.getStringCellValue().equalsIgnoreCase("") || controllerTestCaseID.equals(null)) {
					log.info("No KeyWord Found");
					continue;
				}
				if (executeFlag != null) {
					if (executeFlag.toString().equalsIgnoreCase("Y")) {
						// for(int
						// columnIndex=startCol+1;columnIndex<colCount&&!pauseExecution;columnIndex++)
						System.out.println("pExecution" + pExecution);// Tanvi :
																		// 4/25/2017
						for (int columnIndex = startCol; columnIndex < colCount && !linearpExecution; columnIndex++)// Tanvi
																													// :
																													// 4/11/2017
						{

							controllerTransactionType = controllerRow.getCell(columnIndex);

							mainControllerHeaderNo = reqSheet.getRow(0).getCell(columnIndex).toString();// Tanvi
																										// :
																										// 4/25/2017
							System.out.println(mainControllerHeaderNo);// Tanvi
																		// :
																		// 4/25/2017

							// Tanvi : 4/11/2017 :START
							CycleDateRow = reqSheet.getRow(1);
							cycleDate = CycleDateRow.getCell(columnIndex);
							cycleDateCellValue = cycleDate.getStringCellValue().toString();
							cycleDateValue = null;
							// Tanvi : 4/11/2017 :END

							// TM: commented the following code to avoid
							// continue
							/*
							 * if(controllerTransactionType == null ||
							 * controllerTransactionType
							 * .getStringCellValue().equals("")) { log.info(
							 * "No Transaction Found in the Maincontroller at Cell : "
							 * +columnIndex); continue; }
							 */

							// Tanvi : 4/20/2017 :Start

							if (cycleDateValue == null || cycleDateValue.toString().equalsIgnoreCase("")) {
								cycleDateValue = "NA";
							}
							if (cycleDateCellValue == null || cycleDateCellValue.toString().equalsIgnoreCase("")) {
								cycleDateCellValue = "NA";
							}

							if ((controllerTransactionType != null && StringUtils.isNotBlank(controllerTransactionType.getStringCellValue()))
									&& (!cycleDateCellValue.equalsIgnoreCase("NA"))) {

								// Tanvi : uncomment after testing is done:
								// start

								try {
									ResultSet rs = null;
									Connection conn = JDBCConnection.establishHTML5BillingDBConn();
									Statement st = conn.createStatement();
									rs = st.executeQuery("select * from business_day");
									while (rs.next()) {
										businessDate = rs.getString("business_date");
									}
									rs.close();
									st.close();
									JDBCConnection.closeConnection(conn);
								} catch (Exception e) {
									log.error("Failed to get Current Date from Database<-|-> LocalizeMessage " + e.getLocalizedMessage()
											+ " <-|-> Message " + e.getMessage() + " <-|-> Cause " + e.getCause());
									// throw new
									// Exception("Failed to get Current Date from Database<-|-> LocalizeMessage "
									// + e.getLocalizedMessage()
									// +" <-|-> Message"+ e.getMessage()
									// +" <-|-> Cause "+ e.getCause());
								}

								// log.info("businessDate in application database is :"+businessDate);
								if (businessDate != null) {
									try {
										SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
										Date newBDate = simpleDateFormat.parse(businessDate);
										DateFormat cycleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
										cycleDateValue = cycleDateFormat.format(newBDate);

										// Tanvi : uncomment after testing is
										// done: End

										// cycleDateValue =
										// "4/11/2017";//Tanvi:uncomment this
										// code line after testing
										log.info("Inside business date change method : Maincontroller");

										// bhaskar changebusinessdate
										// transaction through webservice START

										webDriver.report.setTrasactionType("ChangeBusinessdate");
										String fromDate = Config.dtFormat.format(new Date());
										webDriver.report.setFromDate(fromDate);
										webDriver.report.setStatus("");
										webDriver.report.setMessage("");
										log.info("Maincontroller cycle date is not equal to application business date");
										// Tanvi : uncomment after testing is
										// done: Start
										businessDateValue = cycleDateCellValue.toString();
										log.info("Application business date should be : " + businessDateValue);
										// TransactionMapping.TransactionInputData("ChangeBusinessDate");

										cbd_requestxml = Config.changebusinessdaterequestxmlpath;
										cbd_wsdl = Config.changebusinessdatewsdl;

										DocumentBuilderFactory cbd_request_dbFactory = DocumentBuilderFactory.newInstance();
										DocumentBuilder cbd_request_dBuilder = cbd_request_dbFactory.newDocumentBuilder();
										Document cbd_request_doc = cbd_request_dBuilder.parse(cbd_requestxml);
										cbd_request_doc.getDocumentElement().normalize();
										SimpleDateFormat cycledateformat = new SimpleDateFormat("MM/dd/yyyy");
										Date cycledate = cycledateformat.parse(businessDateValue);
										DateFormat xmlformat = new SimpleDateFormat("yyyy-MM-dd");
										xmlbusinessdate = xmlformat.format(cycledate);
										NodeList request_list = cbd_request_doc.getElementsByTagName("*");

										for (int i = 0; i < request_list.getLength(); i++) {
											Node node = request_list.item(i);
											if (node.getNodeName().equalsIgnoreCase("Businessdate")) {
												node.setTextContent(xmlbusinessdate);
												break;
											}
										}
										TransformerFactory cbd_transformerFactory = TransformerFactory.newInstance();
										Transformer cbd_transformer = cbd_transformerFactory.newTransformer();
										DOMSource cbd_source = new DOMSource(cbd_request_doc);
										StreamResult cbd_result = new StreamResult(new File(cbd_requestxml));
										cbd_transformer.transform(cbd_source, cbd_result);

										String cbd_responsexml = WebService
												.callWebService("", cbd_wsdl, cbd_requestxml, Config.user, Config.password);

										DocumentBuilderFactory cbd_reponse_dbFactory = DocumentBuilderFactory.newInstance();
										DocumentBuilder cbd_response_dBuilder = cbd_reponse_dbFactory.newDocumentBuilder();
										Document cbd_response_doc = cbd_response_dBuilder.parse(cbd_responsexml);
										cbd_response_doc.getDocumentElement().normalize();

										NodeList response_list = cbd_response_doc.getElementsByTagName("*");
										String response_nodevalue1 = null;
										String response_nodevalue2 = null;
										for (int j = 0; j < response_list.getLength(); j++) {
											Node response_node = response_list.item(j);
											if (response_node.getNodeName().equalsIgnoreCase("ProcessStatusFlag")) {
												response_nodevalue1 = response_node.getTextContent();
											}
											if (response_node.getNodeName().equalsIgnoreCase("SuccessFlag")) {
												response_nodevalue2 = response_node.getTextContent();
												break;
											}
										}

										if (response_nodevalue1.equalsIgnoreCase("COMPLETED") && response_nodevalue2.equalsIgnoreCase("SUCCESS")
												&& cbd_responsexml.contains(xmlbusinessdate)) {
											webDriver.report.setStatus("PASS");
										} else {
											webDriver.report.setStatus("FAIL");
										}

										webDriver.report.setMessage(response_nodevalue1 + " ; " + response_nodevalue2 + " ; " + xmlbusinessdate);

										// Tanvi : uncomment after testing is
										// done: End
									} catch (Exception e) {
										webDriver.report.setMessage(e.getLocalizedMessage());
										webDriver.report.setStatus("FAIL");
										pauseFun("Webservice issue " + e);

									}

									// by devishree- on 16/03/2017//
									finally {
										String toDate = Config.dtFormat.format(new Date());
										webDriver.report.setToDate(toDate);
										ExcelUtility.writeReport(webDriver.report);
										log.info("Application business date is changed and rechecking in database");
										webDriver.report.setTrasactionType("");
									}

									/*
									 * finally {
									 * ExcelUtility.writeReport(webDriver
									 * .report);
									 * 
									 * columnIndex2 = columnIndex2-1; log.info(
									 * "Application business date is changed and rechecking in database"
									 * );
									 * webDriver.report.setTrasactionType(""); }
									 */

								}
							}
							// Tanvi : 4/20/2017 :End

							// TM: Updated following sysout to give an
							// understanding of what is getting printed on the
							// console
							log.info("Value of controllerTestCaseID : " + controllerTestCaseID);
							log.info("Value of controllerTransactionType : " + controllerTransactionType);
							log.info("Value of cycleDateCellValue : " + cycleDateCellValue);// Tanvi
																							// :
																							// 4/25/2017
							log.info("Value of cycleDateValue : " + cycleDateValue);// Tanvi
																					// :
																					// 4/25/2017
							/*
							 * if(controllerTransactionType.toString().
							 * equalsIgnoreCase("Login1")) {
							 * System.out.println("SearchHotel2"); }
							 */

							// TM: wrapped the PAUSE if into another if as
							// replacement of above commented if
							if (controllerTransactionType != null && StringUtils.isNotBlank(controllerTransactionType.getStringCellValue())
									&& (cycleDateCellValue.equalsIgnoreCase(cycleDateValue)))// Tanvi
																								// :
																								// 4/25/2017
							{
								if (controllerTransactionType.toString().equalsIgnoreCase("PAUSE")) {
									pauseFun("Do You Wish To Continue");
								}

								else {

									// Tanvi : 4/11/2017 :START
									ustartCol = columnIndex + 1;
									FileInputStream in = new FileInputStream(Config.controllerFilePath);
									HSSFWorkbook LinearupdatedmainWB = new HSSFWorkbook(in);
									MainControllerSheet = LinearupdatedmainWB.getSheet("StartPointer");
									updatedstartpointerRow = MainControllerSheet.getRow(1);
									updatedstartpointerValues = WebHelperUtil.getValueFromHashMap(MainControllerSheet);
									UPDATED_START_ROW = Integer.parseInt(updatedstartpointerValues.get("StartRow").toString());
									UPDATED_START_COLUMN = Integer.parseInt(updatedstartpointerValues.get("StartCol").toString());
									System.out.println("ustartRow" + ustartRow);
									System.out.println("ustartCol" + ustartCol);
									updatedstartpointerRow.getCell(UPDATED_START_ROW).setCellValue(ustartRow);
									updatedstartpointerRow.getCell(UPDATED_START_COLUMN).setCellValue(ustartCol);
									FileOutputStream out = new FileOutputStream(Config.controllerFilePath);
									LinearupdatedmainWB.write(out);
									out.flush();
									out.close();
									in.close();

									webDriver.report.setTrasactionType(controllerTransactionType.toString());
									webDriver.report.setTestDescription(testDesciption.toString());
									webDriver.report.setTestcaseId(controllerTestCaseID.toString());
									webDriver.report.setCycleDate(cycleDateCellValue.toString());
									// Tanvi : 4/11/2017 :END

									// Tanvi : 4/13/2017 :START
									if (controllerTransactionType.toString().equalsIgnoreCase("RunBatch_JBEAM"))
									// bhaskar Pause Functionality END
									{
										if (!cycleDateCellValue.equalsIgnoreCase("NA")) {
											report = RunBatch.RunBatch_WS();
											Date endDate = new Date();
											webDriver.report.setToDate(Config.dtFormat.format(endDate));
											ExcelUtility.writeReport(webDriver.report);
										} else {
											Date endDate = new Date();
											webDriver.report.setToDate(Config.dtFormat.format(endDate));
											webDriver.report.setStatus("Cycledate not available.");
											ExcelUtility.writeReport(webDriver.report);
										}

									}
									// Developer : Sabia, Purpose : Reports
									// Compare & Verify Utility, Date :
									// 13/8/2018
									else if (controllerTransactionType.toString().equalsIgnoreCase("VerifyCSVReport")) {
										verifyCSVReport();
										continue;
									}

									else if (controllerTransactionType.toString().equalsIgnoreCase("SC_END") && !pExecution) {
										String recovery_scenario_Temp = Config.recovery_scenario;
										if (recovery_scenario_Temp.equalsIgnoreCase("FALSE")) {
											Config.recovery_scenario = "TRUE";
										}

										recoveryhandler();

										Config.recovery_scenario = recovery_scenario_Temp;
									}

									// Tanvi : 4/13/2017 :End
									else {
										// Tanvi : 4/13/2017 :End
										report = TransactionMapping.TransactionInputData(cycleDateCellValue, controllerTestCaseID.toString(),
												controllerTransactionType.toString(), Config.transactionInputFilePath);
									}
								}
							} else {

								log.info("No Transaction Found in the Maincontroller at Cell : " + columnIndex);
							}

						}

					}
				} else {
					log.info("Execute Flag is not Set");
				}
			}
		} else if (executionApproach.equalsIgnoreCase("Timetravel")) // Timetravel
		{

			/*
			 * for(int
			 * columnIndex=startCol;columnIndex<colCount&&!pauseExecution
			 * ;columnIndex++) { log.info("Inside Time Travel loop 1");
			 */
			int columnIndex2 = startCol;
			for (int rowIndex2 = 1; rowIndex2 < rowCount; columnIndex2++) {

				if (pExecution == true) {
					return report;
				}
				// bhaskar START Pointer Update START

				ustartCol = columnIndex2 + 1;
				// bhaskar START Pointer Update END
				// log.info("Inside Time Travel loop 1");
				// log.info("Row value is :"+rowIndex2);
				// log.info("Column value is : "+columnIndex2);
				// log.info("Total Row count value is : "+rowCount);
				controllerRow = reqSheet.getRow(rowIndex2);
				CycleDateRow = reqSheet.getRow(1);
				colCount = CycleDateRow.getLastCellNum();
				if (columnIndex2 >= colCount)
					break;

				cycleDate = controllerRow.getCell(columnIndex2);
				colCount = controllerRow.getLastCellNum();
				cycleDateCellValue = cycleDate.getStringCellValue();
				// log.info("CycleDate in MainController sheet is :"+cycleDateCellValue+" for column "+columnIndex2);
				// DriverManager.registerDriver(new
				// oracle.jdbc.driver.OracleDriver());
				// c =
				// DriverManager.getConnection("jdbc:oracle:thin:@172.17.156.7:1521:toolkitm",
				// "billing151_html5_clm", "billing151_html5_clm");

				// tanvi: uncommnet this code after testing :start

				try {
					ResultSet rs = null;
					Connection conn = JDBCConnection.establishHTML5BillingDBConn();
					Statement st = conn.createStatement();
					rs = st.executeQuery("select * from business_day");
					while (rs.next()) {
						businessDate = rs.getString("business_date");
					}
					rs.close();
					st.close();
					JDBCConnection.closeConnection(conn);
				} catch (Exception e) {
					log.error("Failed to get Current Date from Database<-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message "
							+ e.getMessage() + " <-|-> Cause " + e.getCause());
					throw new Exception("Failed to get Current Date from Database<-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message"
							+ e.getMessage() + " <-|-> Cause " + e.getCause());
				}

				// tanvi: uncommnet this after testing:End
				// log.info("businessDate in application database is :"+businessDate);

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
				Date newBDate = simpleDateFormat.parse(businessDate);
				DateFormat cycleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
				cycleDateValue = cycleDateFormat.format(newBDate);
				// log.info("newBDate is :"+newBDate);

				log.info("Cycle Date of MainController sheet is :" + cycleDateCellValue + " for column " + columnIndex2);
				log.info("Business Date of Application database is :" + cycleDateValue + " for column " + columnIndex2);

				if (columnIndex2 > startCol) {
					columnChanged = true;
					// bhaskar driver new session START Commenting to remove
					// repeated login
					/*
					 * log.info(
					 * "Maincontroller column is changed hence browser to be restarted"
					 * ); Automation.setUp();
					 * TransactionMapping.TransactionInputData("Login");
					 * Thread.sleep(3000); WebHelper.saveScreenShot();
					 */
					// bhaskar driver new session END
				}

				// cycleDateValue = cycleDateCellValue;
				if (cycleDateCellValue.equalsIgnoreCase(cycleDateValue)) // If
																			// Cycle
																			// Date
																			// in
																			// MainController
																			// Sheet
																			// is
																			// equal
																			// to
																			// Business
																			// Date
																			// of
																			// the
																			// Application
				{
					log.info("Maincontroller cycle date is equal to application business date");
					// Thread.sleep(3000);
					// WebHelper.saveScreenShot();
					// for(columnIndex=startCol;columnIndex<colCount&&!pauseExecution;columnIndex++)
					// {
					// RunBatch.RunBatch_WS();
					// log.info("Time Travel debugging ^^^^^^^^^^^^^^^^^rowIndex^^^^^^^^^^^^^^^^"+rowIndex2);
					// log.info("Time Travel debugging ^^^^^^^^^^^^^^^^^^^^rowCount^^^^^^^^^^^^^"+rowCount);
					if (columnChanged == true) {
						startRow = 1;
					}

					for (int rowIndex = startRow; rowIndex < rowCount; rowIndex++) {
						// bhaskar START Pointer Update START
						if (!pExecution) {
							ustartRow = rowIndex + 1;
							log.info("start pointer Updated to STARTROW :" + ustartRow + " STARTCOLUMN : " + ustartCol);
							log.info("***************************PLEASE DONT OPEN MAINCONTROLLER SHEET*********************");
							try {
								FileInputStream in = new FileInputStream(Config.controllerFilePath);
								HSSFWorkbook updatedmainWB = new HSSFWorkbook(in);
								MainControllerSheet = updatedmainWB.getSheet("StartPointer");
								updatedstartpointerRow = MainControllerSheet.getRow(1);
								updatedstartpointerValues = WebHelperUtil.getValueFromHashMap(MainControllerSheet);
								UPDATED_START_ROW = Integer.parseInt(updatedstartpointerValues.get("StartRow").toString());
								UPDATED_START_COLUMN = Integer.parseInt(updatedstartpointerValues.get("StartCol").toString());
								updatedstartpointerRow.getCell(UPDATED_START_ROW).setCellValue(ustartRow);
								updatedstartpointerRow.getCell(UPDATED_START_COLUMN).setCellValue(ustartCol);
								FileOutputStream out = new FileOutputStream(Config.controllerFilePath);
								updatedmainWB.write(out);
								out.flush();
								out.close();
								in.close();
							} catch (Exception e) {
								log.error("Failed to update StartPointer <-|-> LocalizeMessage " + e.getLocalizedMessage() + " <-|-> Message "
										+ e.getMessage() + " <-|-> Cause " + e.getCause());
								throw new Exception("Failed to update StartPointer <-|-> LocalizeMessage " + e.getLocalizedMessage()
										+ " <-|-> Message" + e.getMessage() + " <-|-> Cause " + e.getCause());
							}
						}
						// bhaskar START Pointer Update END
						pauseExecution = false;
						// bhaskar Recovery Scenario (Both Transaction and
						// WebService) START
						if (recoverydone == true) {
							controllerRow = MainControlSheet.getRow(rowIndex);
							// recoverydone = false;
						}
						/*
						 * else if(WebHelper.recovery_done == true) {
						 * controllerRow =
						 * WebHelper.MainControllerSheet.getRow(rowIndex); }
						 */
						else {
							controllerRow = reqSheet.getRow(rowIndex);
						}
						// bhaskar Recovery Scenario (Both Transaction and
						// WebService) END
						CycleDateRow = reqSheet.getRow(1);
						colCount = CycleDateRow.getLastCellNum();
						testDesciption = WebHelperUtil.getCellData("Test_Description", reqSheet, rowIndex, sheetValues);
						HSSFCell executeFlag = controllerRow.getCell(execFlag);
						sc_no = controllerRow.getCell(scenarioNO);
						controllerTestCaseID = controllerRow.getCell(Integer.parseInt(sheetValues.get("TestCaseID").toString()));
						controllerGroupName = controllerRow.getCell(Integer.parseInt(sheetValues.get("GroupName").toString()));
						// Tanvi : 4/25/2017 :Start
						try {
							mainControllerHeaderNo = reqSheet.getRow(0).getCell(columnIndex2).toString();
						} catch (Exception e) {
							mainControllerHeaderNo = "DefaultHeader";
						}
						// Tanvi : 4/25/2017 :End
						if (controllerTestCaseID.getStringCellValue().equalsIgnoreCase("") || controllerTestCaseID.equals(null)) {
							// log.info("No KeyWord Found");
							continue;
						}
						if (executeFlag != null) {
							if (executeFlag.toString().equalsIgnoreCase("Y")) {
								// log.info("Time Travel debugging ^^^^^^^^^^^^^^^^^columnIndex2^^^^^^^^^^^^^^^^"+columnIndex2);
								// log.info("Time Travel debugging ^^^^^^^^^^^^^^^^colCount^^^^^^^^^^^^^^^^^"+colCount);
								// bhaskar Pause Functionality START
								// if(columnIndex2<=colCount&&!pauseExecution)

								if (columnIndex2 <= colCount && !pauseExecution && !pExecution)
								// bhaskar Pause Functionality END
								{
									controllerTransactionType = controllerRow.getCell(columnIndex2);
									// log.info("Value of Main controller TestCaseID : "+controllerTestCaseID+" TransactionType : "+controllerTransactionType+" Execute Flag is : "+executeFlag.toString());
									if (controllerTransactionType != null && StringUtils.isNotBlank(controllerTransactionType.getStringCellValue())) {
										// bhaskar set transaction name START
										webDriver.report.setTrasactionType(controllerTransactionType.toString());
										// bhaskar set transaction name END

										// Added by aniruddha foe trans_id issue
										// 03/02/2017
										webDriver.report.setMessage("");
										webDriver.report.setScreenShot("");

										// Added by aniruddha foe trans_id issue
										// 03/03/2017
										webDriver.report.setTestcaseId(controllerTestCaseID.toString());

										if (controllerTransactionType.toString().equalsIgnoreCase("PAUSE")) {
											// bhaskar Pause Functionality START
											// pauseFun("Do You Wish To Continue");
											String msg = "Do You Wish To Continue";
											String uInteraction = Config.userInteraction;
											if (!uInteraction.equalsIgnoreCase("FALSE")) {
												webDriver.frame.setVisible(true);
												webDriver.frame.setAlwaysOnTop(true);
												webDriver.frame.setLocationRelativeTo(null);

												JOptionPane.setRootFrame(webDriver.frame);
												int stopexecution = JOptionPane.showConfirmDialog(webDriver.frame, msg, msg,
														JOptionPane.YES_NO_OPTION);
												// log.info(stopexecution);
												if (stopexecution == JOptionPane.NO_OPTION) {
													// pExecution = true;
												}
												webDriver.frame.dispose();
												// log.info(stopexecution);
											}
											// bhaskar Pause Functionality END
										}

										// bhaskar Pause Functionality START
										// else
										// if(controllerTransactionType.toString().equalsIgnoreCase("RunBatch_JBEAM"))

										else if (controllerTransactionType.toString().equalsIgnoreCase("RunBatch_JBEAM") && !pExecution)
										// bhaskar Pause Functionality END
										{
											report = RunBatch.RunBatch_WS();
											Date endDate = new Date();
											webDriver.report.setToDate(Config.dtFormat.format(endDate));
											ExcelUtility.writeReport(webDriver.report);

										}
										// bhaskar Pause Functionality START
										// else

										else if (controllerTransactionType.toString().equalsIgnoreCase("SC_END") && !pExecution) {
											recoveryhandler();
										}

										else if (!pExecution)
										// bhaskar Pause Functionality END
										{
											if ((columnChanged == true) && (!(controllerTransactionType.toString().startsWith("WebService")))) {
												log.info("First UI transaction of the changed column occurred, hence performing login");
												Automation.setUp();
												TransactionMapping.TransactionInputData("Login");
												Thread.sleep(3000);
												WebHelperUtil.saveScreenShot();
												columnChanged = false;
												webDriver.report.setTrasactionType(controllerTransactionType.toString());
											}
											webDriver.report.setCycleDate(cycleDateCellValue);
											report = TransactionMapping.TransactionInputData(cycleDateCellValue, controllerTestCaseID.toString(),
													controllerTransactionType.toString(), Config.transactionInputFilePath);

										}
									} else {
										log.info("No Transaction Found in the Maincontroller at Cell : " + columnIndex2);
									}

								}
							}
						} else {
							log.info("Execute Flag is not Set");
						}

					}
				}
				// }
				else // If Cycle Date in MainController Sheet is not equal to
						// Business Date of the Application
				{
					// report =
					// TransactionMapping.TransactionInputData(controllerTestCaseID,controllerTransactionType,Automation.getConfigValue("TRANSACTION_INPUT_FILEPATH").toString());
					// bhaskar Pause Functionality START
					if (!pExecution) {
						try {
							/*
							 * log.info(
							 * "Maincontroller cycle date is not equal to application business date"
							 * ); businessDateValue =
							 * cycleDateCellValue.toString();
							 * log.info("Application business date should be : "
							 * +businessDateValue);
							 * TransactionMapping.TransactionInputData
							 * ("ChangeBusinessDate"); Thread.sleep(3000);
							 * WebHelper.saveScreenShot(); columnIndex2 =
							 * columnIndex2-1; log.info(
							 * "Application business date is changed and rechecking in database"
							 * );
							 */

							// bhaskar changebusinessdate transaction through
							// webservice START
							webDriver.report.setTrasactionType("ChangeBusinessdate");
							webDriver.report.setCycleDate(cycleDateCellValue);
							String fromDate = Config.dtFormat.format(new Date());
							webDriver.report.setFromDate(fromDate);
							webDriver.report.setStatus("");
							webDriver.report.setMessage("");
							log.info("Maincontroller cycle date is not equal to application business date");

							// /Tanvi : uncomment after testing is done: start

							businessDateValue = cycleDateCellValue.toString();
							log.info("Maincontroller cycle date is : " + businessDateValue);
							log.info("Maincontroller cycle date is not equal to application business date");
							// TransactionMapping.TransactionInputData("ChangeBusinessDate");
							cbd_requestxml = Config.changebusinessdaterequestxmlpath;
							cbd_wsdl = Config.changebusinessdatewsdl;

							DocumentBuilderFactory cbd_request_dbFactory = DocumentBuilderFactory.newInstance();
							DocumentBuilder cbd_request_dBuilder = cbd_request_dbFactory.newDocumentBuilder();
							Document cbd_request_doc = cbd_request_dBuilder.parse(cbd_requestxml);
							cbd_request_doc.getDocumentElement().normalize();
							SimpleDateFormat cycledateformat = new SimpleDateFormat("MM/dd/yyyy");
							Date cycledate = cycledateformat.parse(businessDateValue);
							DateFormat xmlformat = new SimpleDateFormat("yyyy-MM-dd");
							xmlbusinessdate = xmlformat.format(cycledate);
							NodeList request_list = cbd_request_doc.getElementsByTagName("*");

							for (int i = 0; i < request_list.getLength(); i++) {
								Node node = request_list.item(i);
								if (node.getNodeName().equalsIgnoreCase("Businessdate")) {
									node.setTextContent(xmlbusinessdate);
									break;
								}
							}
							log.info("Date " + xmlbusinessdate + " updated in XML file : ");
							TransformerFactory cbd_transformerFactory = TransformerFactory.newInstance();
							Transformer cbd_transformer = cbd_transformerFactory.newTransformer();
							DOMSource cbd_source = new DOMSource(cbd_request_doc);
							StreamResult cbd_result = new StreamResult(new File(cbd_requestxml));
							cbd_transformer.transform(cbd_source, cbd_result);

							String cbd_responsexml = WebService.callWebService("", cbd_wsdl, cbd_requestxml, Config.user, Config.password);

							DocumentBuilderFactory cbd_reponse_dbFactory = DocumentBuilderFactory.newInstance();
							DocumentBuilder cbd_response_dBuilder = cbd_reponse_dbFactory.newDocumentBuilder();
							Document cbd_response_doc = cbd_response_dBuilder.parse(cbd_responsexml);
							cbd_response_doc.getDocumentElement().normalize();

							NodeList response_list = cbd_response_doc.getElementsByTagName("*");
							String response_nodevalue1 = null;
							String response_nodevalue2 = null;
							for (int j = 0; j < response_list.getLength(); j++) {
								Node response_node = response_list.item(j);
								if (response_node.getNodeName().equalsIgnoreCase("ProcessStatusFlag")) {
									response_nodevalue1 = response_node.getTextContent();
								}
								if (response_node.getNodeName().equalsIgnoreCase("SuccessFlag")) {
									response_nodevalue2 = response_node.getTextContent();
									break;
								}
							}

							if (response_nodevalue1.equalsIgnoreCase("COMPLETED") && response_nodevalue2.equalsIgnoreCase("SUCCESS")
									&& cbd_responsexml.contains(xmlbusinessdate)) {
								webDriver.report.setStatus("PASS");
							} else {
								webDriver.report.setStatus("FAIL");
							}

							webDriver.report.setMessage(response_nodevalue1 + " ; " + response_nodevalue2 + " ; " + xmlbusinessdate);
							// Tanvi : uncomment after testing is done: End
							log.info(response_nodevalue1 + " ; " + response_nodevalue2 + " ; " + xmlbusinessdate);

						} catch (Exception e) {
							webDriver.report.setMessage(e.getLocalizedMessage());
							webDriver.report.setStatus("FAIL");
						}

						// by devishree- on 16/03/2017//
						finally {
							String toDate = Config.dtFormat.format(new Date());
							webDriver.report.setToDate(toDate);
							ExcelUtility.writeReport(webDriver.report);
							columnIndex2 = columnIndex2 - 1;
							log.info("Application business date is changed and rechecking in database");
							webDriver.report.setTrasactionType("");
						}

						/*
						 * finally { ExcelUtility.writeReport(webDriver.report);
						 * 
						 * columnIndex2 = columnIndex2-1; log.info(
						 * "Application business date is changed and rechecking in database"
						 * ); webDriver.report.setTrasactionType(""); }
						 */
					}
					// bhaskar changebusinessdate transaction through webservice
					// END

				}
			}
			// }
		}
		// bhaskar Time Travel Approach END
		// log.info("In MainController value of pauseExecution3:"+pauseExecution);
		startCol = execFlag + 1;
		return report;
	}

	@SuppressWarnings("resource")
	public void recoveryhandler() {
		try {
			linearpExecution = true;// Tanvi : 4/11/2017
			// bhaskar Recovery Scenario (Transaction) START
			if (Config.recovery_scenario.equalsIgnoreCase("TRUE")) {
				HSSFSheet reqSheet = ExcelUtility.getXLSSheet(Config.controllerFilePath, "MainControlSheet");
				controlsheet = WebHelperUtil.getValueFromHashMap(reqSheet);
				int maincontrollersheetrowCount = reqSheet.getLastRowNum() + 1;
				log.info("No of Rows in MainController sheet:" + maincontrollersheetrowCount);
				FileInputStream in;
				HSSFWorkbook mainWB;
				in = new FileInputStream(Config.controllerFilePath);
				mainWB = new HSSFWorkbook(in);
				MainControlSheet = mainWB.getSheet("MainControlSheet");

				for (int rowindx = 2; rowindx < maincontrollersheetrowCount; rowindx++) {
					log.info("###################### MainController Row ################## : " + rowindx);
					currentRow = reqSheet.getRow(rowindx);
					currentscenarionum = Integer.parseInt(controlsheet.get("SC_NO").toString());
					// log.info("currentscenarionum : "+currentscenarionum);
					currentSC_NO = (int) currentRow.getCell(currentscenarionum).getNumericCellValue();
					log.info("current maincontroller sceanrio number : " + currentSC_NO);
					failedscenarionum = (int) sc_no.getNumericCellValue();
					log.info("failed scenario number : " + failedscenarionum);
					// log.info("current MainController scenario number:"+currentSC_NO);
					// log.info("Failed MainController scenario number:"+failedscenarionum);
					if (currentSC_NO == failedscenarionum) {
						HashMap<String, Integer> MainControlHashMap = WebHelperUtil.getValueFromHashMap(MainControlSheet);
						int executeflaghashmap = Integer.parseInt(MainControlHashMap.get("ExecuteFlag").toString());
						// log.info("executeflaghashmap is : "+executeflaghashmap);
						HSSFRow ExecuteFlagRow = MainControlSheet.getRow(rowindx);
						HSSFCell ExecuteFlagCell = ExecuteFlagRow.getCell(executeflaghashmap);
						// ExecuteFlagCell.setCellValue("N");

						// To mark batch tc as Y again
						// String TCID =
						// (MainControlHashMap.get("TestCaseID").toString());
						// if(TCID.equalsIgnoreCase("RunBatch_JBEAM"))
						// {
						// System.out.println("Marked BatchTc Y");
						// ExecuteFlagCell.setCellValue("Y");
						// }

						// Start : To run DB Queries to mark object

						int ColAccountNumber = Integer.parseInt(controlsheet.get("AccountNumber").toString());
						int ColPolicyNumber = Integer.parseInt(controlsheet.get("PolicyNumber").toString());
						int ColAgentNumber = Integer.parseInt(controlsheet.get("AgentNumber").toString());

						String STAccountNumber = currentRow.getCell(ColAccountNumber).getStringCellValue();
						String STPolicyNumber = currentRow.getCell(ColPolicyNumber).getStringCellValue();
						String STAgentNumber = currentRow.getCell(ColAgentNumber).getStringCellValue();

						// String STAccountNumber = "2017013131"; // Sabia
						// hardcode the value

						log.info("-------- The data pickup for SkipRowIdentifier ---------");
						log.info("STAccountNumber = " + STAccountNumber.toString());
						log.info("STPolicyNumber = " + currentRow.getCell(ColPolicyNumber).getStringCellValue());
						log.info("STAgentNumber = " + currentRow.getCell(ColAgentNumber).getStringCellValue());

						// Developer : Sabia, Purpose : Adding the value to the
						// map, Date : 13/8/2018
						objFailedAccountList.add(STAccountNumber);
						objFailedBrokerList.add(STAgentNumber);
						objFailedPolicyList.add(STPolicyNumber);

						if (controllerTransactionType.toString().equalsIgnoreCase("SC_END")) {
							// ExecuteFlagCell.setCellValue("Y");
							WebService.RunDBQueries("XX_Scenario_Completed", STPolicyNumber, STAgentNumber, STAccountNumber);
						} else {
							ExecuteFlagCell.setCellValue("N");
							WebService.RunDBQueries("XX_Transaction_Failed", STPolicyNumber, STAgentNumber, STAccountNumber);
						}
						// End : To run DB Queries to mark object

						log.info("Row no" + rowindx + "out of MainController rows" + maincontrollersheetrowCount);
					}
				}
				FileOutputStream out;
				out = new FileOutputStream(Config.controllerFilePath);
				mainWB.write(out);
				out.flush();
				out.close();
				in.close();
				recoverydone = true;
				// log.info("End of Recovery");
			}
		} catch (IOException e) {
			log.error(e.getLocalizedMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		}
		// bhaskar Recovery Scenario (Transaction) END
	}

	/** Pauses the Execution **/

	// bhaskar Batch Objects Failure START
	public void batchRecoveryScenario(String batchNo) {

		FileInputStream in = null;
		HSSFWorkbook mainWB = null;
		ResultSet failedRecords = null;

		jobName = null;
		PolicyNo = null;
		brokerNumber = null;
		AccountNumber = null;
		job_SEQ = null;
		String temp_job_SEQ = "";

		Connection conn11 = null;
		Statement st11 = null;
		try {
			HSSFSheet reqSheet = ExcelUtility.getXLSSheet(Config.controllerFilePath, "MainControlSheet");
			controlsheet = WebHelperUtil.getValueFromHashMap(reqSheet);
			int maincontrollersheetrowCount = reqSheet.getLastRowNum() + 1;
			log.info("No of Rows in MainController sheet:" + maincontrollersheetrowCount);
			// FileInputStream in;
			// HSSFWorkbook mainWB;
			in = new FileInputStream(Config.controllerFilePath);
			mainWB = new HSSFWorkbook(in);
			MainControlSheet = mainWB.getSheet("MainControlSheet");

			conn11 = JDBCConnection.establishHTML5BillingCoreDBConn();
			st11 = conn11.createStatement();
			failedRecords = st11
					.executeQuery("SELECT BASE.JOB_NAME, CORE.POLICY_NO, BASE.BROKER_SYSTEM_CODE, "
							+ "BASE.ACCOUNT_SYSTEM_CODE, BASE.JOB_SEQ FROM "
							+ Config.jbeamdatabaseusername
							+ ".LOG CORE, "
							+ Config.applicationdatabaseusername
							+ ".JOB_SCHEDULE BASE WHERE CORE.BE_SEQ_NO=BASE.JOB_SEQ AND BASE.JOB_STATUS = "
							+ "'FAILED' AND CORE.TASK_NAME is not null AND CORE.BATCH_NO='"
							+ batchNo
							+ "' AND BASE.JOB_NAME NOT "
							+ "IN ('ENTITY_INTERFACE','PREMIUM_INTERFACE','RCAN_INTERFACE','CHECK_UPDATE_INTERFACE','AP_INTERFACE',"
							+ "'RETURNEDPAYMENT_INTERFACE','GL_INTERFACE','POLICY_STATUS_INTERFACE','RREIN_INTERFACE','GENERATE_FILE_NAME','OFS_MERGE_XML_JOB') "
							+ "AND (BASE.POLICY_NO is not null OR BASE.ACCOUNT_SYSTEM_CODE is not null OR BASE.BROKER_SYSTEM_CODE is not null)");
			while (failedRecords.next()) {
				jobName = failedRecords.getString(1).toString();
				try {
					PolicyNo = failedRecords.getString(2).toString();
				} catch (NullPointerException ne) {
					log.info("Policy# is null for Failed Job_Object " + jobName + "Batch # " + batchNo);
				}

				try {
					brokerSystemCode = failedRecords.getString(3).toString();
				} catch (NullPointerException ne) {
					log.info("brokerSystemCode is null for Failed Job_Object " + jobName + "Batch # " + batchNo);
				}
				if (brokerSystemCode != null) {
					ResultSet rs = null;
					Connection conn = JDBCConnection.establishHTML5BillingDBConn();
					Statement st = conn.createStatement();
					rs = st.executeQuery("select system_entity_code from entity_register where entity_type= 'BROKER' and source_system_entity_code = '"
							+ brokerSystemCode + "'");
					while (rs.next()) {
						brokerNumber = rs.getString(1);
					}
					rs.close();
					st.close();
					JDBCConnection.closeConnection(conn);
				}
				try {
					accountSystemCode = failedRecords.getString(4).toString();
				} catch (NullPointerException ne) {
					log.info("accountSystemCode is null for failed Job_Object " + jobName + "Batch # " + batchNo);
				}
				if (accountSystemCode != null) {
					ResultSet rs1 = null;
					Connection conn = JDBCConnection.establishHTML5BillingDBConn();
					Statement st = conn.createStatement();
					rs1 = st.executeQuery("select system_entity_code from entity_register where entity_type= 'ACCOUNT' and source_system_entity_code = '"
							+ accountSystemCode + "'");
					while (rs1.next()) {
						AccountNumber = rs1.getString(1);
					}
					rs1.close();
					st.close();
					JDBCConnection.closeConnection(conn);
				}
				job_SEQ = failedRecords.getString(5).toString();

				for (int rowindx = 2; rowindx < maincontrollersheetrowCount; rowindx++) {
					log.info("###################### MainController Row ################## : " + rowindx);
					currentRow = reqSheet.getRow(rowindx);
					currentaccountnum = Integer.parseInt(controlsheet.get("AccountNumber").toString());
					currentAccount_NO = currentRow.getCell(currentaccountnum).toString();
					currentpolicynum = Integer.parseInt(controlsheet.get("PolicyNumber").toString());
					currentPolicy_NO = currentRow.getCell(currentpolicynum).getStringCellValue();
					currentagentnum = Integer.parseInt(controlsheet.get("AgentNumber").toString());
					currentAgent_NO = currentRow.getCell(currentagentnum).getStringCellValue();
					currentscenarionum = Integer.parseInt(controlsheet.get("SC_NO").toString());
					// log.info("currentscenarionum : "+currentscenarionum);
					currentSC_NO = (int) currentRow.getCell(currentscenarionum).getNumericCellValue();
					scenarionum = (int) sc_no.getNumericCellValue();
					// log.info("current MainController scenario number:"+currentSC_NO);
					// log.info("Failed MainController scenario number:"+failedscenarionum);
					if ((PolicyNo != null && PolicyNo.equalsIgnoreCase(currentPolicy_NO))
							|| (AccountNumber != null && AccountNumber.equalsIgnoreCase(currentAccount_NO))
							|| (brokerNumber != null && brokerNumber.equalsIgnoreCase(currentAgent_NO))) {
						failedscenarionum = currentSC_NO;
						Connection conn = JDBCConnection.establishHTML5BillingDBConn();
						Statement st = conn.createStatement();
						st.execute("UPDATE job_schedule  SET job_status = 'Batch_Failed_Object'  WHERE job_status <> 'COMPLETED' AND job_seq = '"
								+ job_SEQ + "'");

						// This changes for multiple job_seq failure start
						if (!temp_job_SEQ.equalsIgnoreCase(job_SEQ)) {

							webDriver.report.setMessage(webDriver.report.getMessage() + "Job_Object " + jobName + " Batch # " + batchNo + " job_seq "
									+ job_SEQ + " SC NO " + failedscenarionum);
							temp_job_SEQ = job_SEQ;
						}
						// This changes for multiple job_seq failure End
						// ----Aniruddha C.
						for (int scblockindex = 2; scblockindex < maincontrollersheetrowCount; scblockindex++) {
							HashMap<String, Integer> MainControlHashMap = WebHelperUtil.getValueFromHashMap(MainControlSheet);
							int executeflaghashmap = Integer.parseInt(MainControlHashMap.get("ExecuteFlag").toString());
							currentRow = reqSheet.getRow(scblockindex);
							currentaccountnum = Integer.parseInt(controlsheet.get("AccountNumber").toString());
							currentAccount_NO = currentRow.getCell(currentaccountnum).toString();
							currentpolicynum = Integer.parseInt(controlsheet.get("PolicyNumber").toString());
							currentPolicy_NO = currentRow.getCell(currentpolicynum).getStringCellValue();
							currentagentnum = Integer.parseInt(controlsheet.get("AgentNumber").toString());
							currentAgent_NO = currentRow.getCell(currentagentnum).getStringCellValue();
							currentscenarionum = Integer.parseInt(controlsheet.get("SC_NO").toString());
							// log.info("executeflaghashmap is : "+executeflaghashmap);
							HSSFRow ExecuteFlagRow = MainControlSheet.getRow(scblockindex);
							HSSFCell ExecuteFlagCell = ExecuteFlagRow.getCell(executeflaghashmap);

							// Add if condition for checking scenario number and
							// block all policies, account and broker
							currentRow = reqSheet.getRow(scblockindex);
							currentscenarionum = Integer.parseInt(controlsheet.get("SC_NO").toString());
							// log.info("currentscenarionum : "+currentscenarionum);
							currentSC_NO = (int) currentRow.getCell(currentscenarionum).getNumericCellValue();
							if (failedscenarionum == currentSC_NO) {
								ExecuteFlagCell.setCellValue("N");
								log.info("Row no" + scblockindex + "out of MainController rows" + maincontrollersheetrowCount);
								// log.info("currentscenarionum blocked: "+currentscenarionum);
								log.info("currentscenarionum blocked: " + currentSC_NO);// Changes
																						// done
																						// by
																						// Aniruddha
								if (currentPolicy_NO != null) {
									st.execute("UPDATE job_schedule  SET job_status = 'XX_Obejct_Failed'  WHERE job_status <> 'COMPLETED' AND policy_term_id IN (select policy_term_id from policy_register where policy_no = '"
											+ PolicyNo + "')");
									log.info("Marked objects for Policy_NO : " + currentPolicy_NO);
								}
								if (currentAgent_NO != null) {
									st.execute("UPDATE job_schedule SET job_status = 'XX_Obejct_Failed' WHERE job_status <> 'COMPLETED' AND account_system_code = (select system_entity_code from entity_register where entity_type= 'BROKER' and source_system_entity_code = '"
											+ brokerNumber + "')");
									log.info("Marked objects for Agent_NO : " + currentAgent_NO);
								}
								if (currentAccount_NO != null) {
									st.execute("UPDATE job_schedule SET job_status = 'XX_Obejct_Failed'  WHERE job_status <> 'COMPLETED' AND account_system_code = (select system_entity_code from entity_register where entity_type= 'ACCOUNT' and source_system_entity_code = '"
											+ AccountNumber + "')");
									log.info("Marked objects for Account_NO : " + currentAccount_NO);
								}
								// rowindx = scblockindex;
							}
						}
						if(Config.databaseType.equalsIgnoreCase("ORACLE"))
						{
						    st.execute("commit");
						}
						st.close();
						JDBCConnection.closeConnection(conn);
					}
					/*
					 * FileOutputStream out; out = new
					 * FileOutputStream(Config.controllerFilePath);
					 * mainWB.write(out); out.flush(); out.close(); in.close();
					 * failedRecords.close();
					 */
				}
			}
		} catch (SQLException se) {
			log.error(se.getLocalizedMessage());
		} catch (ClassNotFoundException ce) {
			log.error(ce.getLocalizedMessage());
		} catch (IOException io) {
			log.error(io.getLocalizedMessage());
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		} finally {
			try { // changes for saving main controller Aniruddha C. 3/9/2017
				failedRecords.close();
				st11.close();
				JDBCConnection.closeConnection(conn11);
				FileOutputStream out;
				out = new FileOutputStream(Config.controllerFilePath);
				mainWB.write(out);
				out.flush();
				out.close();
				in.close();
			} catch (Exception e) {
				log.error("Exception thrown while saving main controller  after marking SC" + failedscenarionum + " in file  <-|-> LocalizeMessage "
						+ e.getLocalizedMessage() + " <-|-> Message " + e.getMessage() + " <-|-> Cause " + e.getCause());
			}
		}
	}

	// bhaskar Batch Objects Failure END

	public boolean pauseFun(String message) {

		/**
		 * DS:18-07-2014:Replacing timeout in msg String tempMsg =
		 * "Timed out after CONFIGTIMEOUT seconds waiting for presence of element located by"
		 * ; tempMsg = tempMsg.replace("CONFIGTIMEOUT",
		 * Automation.getConfigValue("TIMEOUT").toString()); if(message!= null)
		 * message = message.replace(tempMsg, "Element not found");
		 **/

		String userInteraction = "TRUE";
		/*
		 * try {
		 */
		log.info("In PauseFun method");
		webDriver.report.setGroupName(controllerGroupName.toString());
		webDriver.report.setTestcaseId(controllerTestCaseID.toString());
		webDriver.report.setTestDescription(testDesciption);
		webDriver.report.setTrasactionType(controllerTransactionType.toString());
		WebHelperClaims.toDate = new Date();
		webDriver.report.setMessage(message);
		webDriver.report.setToDate(Config.dtFormat.format(WebHelperClaims.toDate));

		if (!(controllerTransactionType.toString().startsWith("WebService"))) {
			WebHelperUtil.saveScreenShot();
		}
		if (message == null) {
			message = "TestCase: " + controllerTestCaseID + " Tranasction: " + controllerTransactionType + " Error: Unknown...";
			webDriver.report.setMessage(message);
		}
		if (Config.getConfgiMapSize() != 0) {
			try {
				if (Config.userInteraction == null) {
					throw new Exception("Null Value Found for UserInteractioin Parameter");
				} else {
					userInteraction = Config.userInteraction;
				}
			} catch (Exception e) {

				JOptionPane.showConfirmDialog(webDriver.frame, "Null Value Found for UserInteractioin Parameter");
			}
		}

		/** Don't mark status as FAIL if transaction name is PAUSE **/
		if (!controllerTransactionType.toString().equalsIgnoreCase("PAUSE")) {
			log.info("Start Recovery");
			webDriver.report.setStatus("FAIL");
			webDriver.report.setCycleDate(cycleDateCellValue);
			// bhaskar
			// iATFSeleniumWeb.WebDriver.FailedReport = "Y";

			recoveryhandler();
			log.info("End Recovery");
			scrshotbody = "PFA the Screenshot for failed Transaction";
			scrEmail = Config.emailId;
			if (!scrEmail.toString().equalsIgnoreCase("NA") && !scrEmail.equalsIgnoreCase("") && !scrEmail.equalsIgnoreCase(null)) {
				strScrCompleteData = scrEmail + "#" + scrshotbody + "#" + scrreportsheet + "#" + scrdetailreportsheet + "#" + FailScreen + "#" + "";
				try {
					Runtime.getRuntime().exec("wscript SendMail.vbs " + (char) 34 + strScrCompleteData + (char) 34);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// bhaskar reporting START
			// String reportimg =
			// ExcelUtility.test.addScreenCapture(FailScreen);
			// log.info("Failscreenis:"+FailScreen);
			// ExcelUtility.test.log(LogStatus.INFO, "Image",
			// "Screenshot for failed Transaction " +
			// ExcelUtility.test.addScreenCapture(FailScreen));
			// bhaskar reporting END
			// bhaskar
		}

		if (!userInteraction.equalsIgnoreCase("FALSE")) {
			webDriver.frame.setVisible(true);
			webDriver.frame.setAlwaysOnTop(true);
			webDriver.frame.setLocationRelativeTo(null);

			JOptionPane.setRootFrame(webDriver.frame);
			int response = JOptionPane.showConfirmDialog(webDriver.frame, message, "iTAF - Do you want to STOP...", JOptionPane.YES_NO_OPTION);
			// log.info(response);
			if (response == JOptionPane.YES_OPTION) {
				// pauseExecution = true;
				pExecution = true;
			} else if (response == 1) {
				/** Call error reporting and stop execution **/
				/*
				 * try { ExcelUtility.writeReport(WebDriver.report); } catch
				 * (IOException e) { // TODO Auto-generated catch block
				 * e.printStackTrace(); }
				 */

			} else {
				log.info("You have pressed cancel" + response);
				// pauseExecution =true;
			}
		} else {
			webDriver.report.setMessage(message);
			// pauseExecution = true;
		}
		/*
		 * } catch(Exception e) { log.info(e.getMessage()); } finally {
		 * //WebDriver.frame.dispose(); log.info("In Pause fun function"); }
		 * return pauseExecution;
		 */
		return pauseExecution;

	}

}
