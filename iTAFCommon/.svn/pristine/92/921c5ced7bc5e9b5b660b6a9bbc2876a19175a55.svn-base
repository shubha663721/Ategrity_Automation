package com.majesco.itaf.main;
import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.majesco.itaf.util.CalendarSnippet;
import com.majesco.itaf.util.ExcelUtility;
import com.majesco.itaf.util.Jacob;
import com.majesco.itaf.util.ZipFolder;
import com.majesco.itaf.verification.WebVerification;

public class ITAFWebDriverClaims extends ITAFWebDriver{

	private final static Logger log = Logger.getLogger(ITAFWebDriverClaims.class.getName());
	private static MainController controller;
	
	ITAFWebDriverClaims(){		
	}

	public void init() throws IOException
	{
		String reportsheet = "";
		String detailreportsheet = "";
		String nodetailreportsheet = "";
		String FailedScreen = "";
		String body = "";
		String strCompleteData = "";
		File f = null;
		String webserviceUtility = "";
		String dashboardpath = "";
		String resultpath = "";
		File dir;
		String defaultlogin = "";

		try
		{	

			/*	ResultSet rs =JDBCConnection.establishHTML5BillingDBConn("select business_date from business_day");
			while(rs.next())
			{
				String businessDate = rs.getString("business_date");
			}
			rs.close();*/

			Runtime.getRuntime().exec("wscript closexl.vbs");
			//Jacob.main("UpdateStartPointer.xlsm", "Macro1");
			//Jacob.main("D:\\iTAFSeleniumWeb\\UpdateStartPointer.xlsm", "Macro1");
			Automation.setUp();

			controller = ObjectFactory.getMainController();

			email = Config.emailId;			
			reportsheet = Config.resultOutput;
			detailreportsheet = Config.verificationResultPath;
			defaultlogin = Config.defaultLogin;//Tanvi : 4/11/2017 

			log.info("MainController File Path is :"+Config.controllerFilePath);

			//Tanvi : 4/11/2017 :START
			if(defaultlogin.equals("Y"))
			{
				TransactionMapping.TransactionInputData("Login");  //Default Login START
			}
			//Tanvi : 4/11/2017 :END
			WebHelperUtil.saveScreenShot();
			controller.ControllerData(Config.controllerFilePath); //Start Transaction

		}
		catch(Exception e)
		{
			log.error(e.getMessage(), e);
			log.error(e.getLocalizedMessage());						
			report.setStatus("FAIL");
			report.setTestcaseId(controller.controllerTestCaseID.toString());
			report.setTrasactionType(controller.controllerTransactionType.toString());			
			try {
				controller.pauseFun("d: "+controller.controllerTestCaseID +", Tranasction: "+controller.controllerTransactionType+", Error: "+e.getMessage());
			}
			catch (Exception e1) 
			{
				log.error(e1.getMessage(), e1);
				controller.pauseFun("File Not Found");
			}
		}
		finally
		{	
			//TM: 16-01-2015		
			try {
				if(com.majesco.itaf.util.CalendarSnippet.isProcessRunning("IEDriverServer.exe"))
				{

					com.majesco.itaf.util.CalendarSnippet.killProcess("IEDriverServer.exe");
				}
			} 
			catch (Exception e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
				e.printStackTrace();
			}

			if(controller.controllerTransactionType.toString().equalsIgnoreCase("END") || controller.controllerTransactionType.toString().equalsIgnoreCase("END1"))
			{	
				if(controller.controllerTransactionType.toString().equalsIgnoreCase("END"))
				{
					ExcelUtility.writeReport(report);
					System.exit(1);
				}
				else
				{
					ExcelUtility.writeReport(report);
					Jacob.main("D:\\iTAFSeleniumWeb\\UpdateStartPointer.xlsm", "Macro1");
					System.exit(1);
				}
			}

			Date finalfrmDate = new Date();
			report.setIteration(Config.cycleNumber);
			report.setTestcaseId("Common");
			report.setCycleDate("Common");			
			report.setTrasactionType("XML Comparison Macro");
			report.setFromDate(Config.dtFormat.format(finalfrmDate));
			//iTAFSeleniumWeb.WebDriver.report.setStrGroupName(MainController.controllerGroupName.toString());


			webserviceUtility = Config.webserviceComparisonUtilityPath;
			System.out.println("webserviceUtility : "+webserviceUtility);

			dashboardpath = Config.dashboardFilepath;
			System.out.println("dashboardpath : "+dashboardpath);

			if(Config.webserviceComparison.equalsIgnoreCase("True"))
			{

				//Call to Independent_XML_COMPARISON_Utility.xlsm
				try 
				{
					Thread.sleep(5000);
				} 
				catch (InterruptedException e1) 
				{
					log.error(e1.getMessage(), e1);
					e1.printStackTrace(); 	// TODO Auto-generated catch block
				}
				Jacob.main(webserviceUtility, "Macro1");
				report.setStatus("Executed Successfully");
				ExcelUtility.writeReport(report);			
				try 
				{
					CalendarSnippet.killProcess("EXCEL.EXE");
				} 
				catch (Exception e) 
				{
					log.error(e.getMessage(), e);
					e.printStackTrace(); // TODO Auto-generated catch block
				}

				f= new File(Config.resultFilePath+"WebService_Comparison_Results.xls");


				if(f.exists())
				{		
					System.out.println("FileExists");
					//Call to Results_Dashboard_Utility.XLS
					try 
					{
						Thread.sleep(5000);
					} 
					catch (InterruptedException e1) 
					{
						log.error(e1.getMessage(), e1);
						e1.printStackTrace(); 	// TODO Auto-generated catch block
					}

					Date final1frmDate = new Date();
					report.setFromDate(Config.dtFormat.format(final1frmDate));
					report.setTrasactionType("Dashboard Macro");
					dashboardpath = Config.dashboardFilepath;
					System.out.println("dashboardpath : "+dashboardpath);
					Jacob.main(dashboardpath, "Macro1");
					report.setStatus("Executed Successfully");
					ExcelUtility.writeReport(report);	
					try 
					{
						CalendarSnippet.killProcess("EXCEL.EXE");
					} 
					catch (Exception e) 
					{
						log.error(e.getMessage(), e);
						e.printStackTrace(); // TODO Auto-generated catch block
					}	
				}
				else
				{

					System.out.println("WebService_Comparison_Results file does not exist ");
				}
			}
			else
			{
				report.setStatus("Did not execute");
				report.setGroupName("Independent_XML_COMPARISON_Utility file does not exist");
				ExcelUtility.writeReport(report);	
			}

			resultpath = Config.resultFilePath;
			dir = new File(resultpath+"Dashboard_Reports");
			ZipFolder.zipDirectory(dir,resultpath+"Dashboard_Reports.zip",resultpath);
			File tempfile = new File(detailreportsheet);
			boolean exists = tempfile.exists();
			if(!email.toString().equalsIgnoreCase("NA") && !email.equalsIgnoreCase("") && !email.equalsIgnoreCase(null))
			{
				if(exists == true)
				{
					body = "PFA the Reports";
					strCompleteData = email + "#" + body + "#" + reportsheet + "#" + detailreportsheet + "#" + FailedScreen + "#" +resultpath+"Dashboard_Reports.zip";				
					Runtime.getRuntime().exec("wscript SendMail.vbs "+(char)34+strCompleteData+(char)34);
				}
				else
				{
					body = "PFA the Report";
					strCompleteData = email + "#" + body + "#" + reportsheet + "#" + FailedScreen + "#" + nodetailreportsheet +"#" +resultpath+"Dashboard_Reports.zip";
					Runtime.getRuntime().exec("wscript SendMail.vbs "+(char)34+strCompleteData+(char)34);				
				}
			}

			/*frame.setVisible(true);
			frame.setAlwaysOnTop(true);
			WebDriver.frame.setLocationRelativeTo(null);
			JOptionPane.setRootFrame(WebDriver.frame);

			JOptionPane.showMessageDialog(frame, "Execution Completed");		
			frame.dispose();*/

			System.exit(1);
		}
	}


	@SuppressWarnings("unused")
	public void DataInput(String filePath,String testcaseID,String transactionType,String transactionCode,String operationType,String cycleDate) throws Exception
	{
		log.info("filePath is:"+filePath);
		if(transactionCode == null)
		{
			transactionCode = transactionType; 
		}
		log.info(transactionCode);

		//if(operationType.equalsIgnoreCase("InputandVerfiy")&&!operationType.isEmpty())
		if(operationType.equalsIgnoreCase("InputandVerfiy") && !StringUtils.isEmpty(operationType.toString()))
		{			
			ExcelUtility.GetDataFromValues(null, filePath, testcaseID.toString(), transactionType.toString(), cycleDate, operationType);
			WebVerification.performVerification(transactionType, testcaseID, "",cycleDate);
		}
		else if(operationType.equalsIgnoreCase("Capture") && !StringUtils.isEmpty(operationType))
		{
			log.info("Capture");
			String OperationType = "Capture";			
			WebVerification.performVerification(transactionType, testcaseID, cycleDate, operationType);
		}
		//else if(!operationType.equalsIgnoreCase("Verify")&&!operationType.isEmpty())
		else if(!operationType.equalsIgnoreCase("Verify") && !StringUtils.isEmpty(operationType.toString()))
		{
			ExcelUtility.GetDataFromValues(null, filePath, testcaseID.toString(), transactionType.toString(), cycleDate, operationType);
		}
		//else if(!operationType.equalsIgnoreCase("Input")&&!operationType.isEmpty())
		else if(!operationType.equalsIgnoreCase("Input") && !StringUtils.isEmpty(operationType.toString()))
		{
			log.info("INPUT");
			WebVerification.performVerification(transactionType, testcaseID, "", cycleDate);
		}
	}
}

