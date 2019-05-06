package com.majesco.itaf.main;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
//Meghna
//Meghna
//Backup

import com.majesco.itaf.util.CalendarSnippet;
import com.majesco.itaf.util.ExcelUtility;
import com.majesco.itaf.util.Jacob;
import com.majesco.itaf.util.ZipFolder;
import com.majesco.itaf.verification.WebVerification;


public class ITAFWebDriverBilling extends ITAFWebDriver{
	
	private final static Logger log = Logger.getLogger(ITAFWebDriverBilling.class.getName());
	
	private MainController controller;
	//private static WebHelperBilling webHelper = (WebHelperBilling)ObjectFactory.getInstance().getWebHelper();//TODO remove cast and change the variable type to WebHelper
	
	ITAFWebDriverBilling(){
	}

	/*public static ITAFWebDriverBilling getInstance(){
		return (ITAFWebDriverBilling)ITAFWebDriver.getInstance();
	}*/
	
	protected void init() throws IOException{
		String reportsheet = "";
		String detailreportsheet = "";
		String nodetailreportsheet = "";
		String FailedScreen = "";
		String body = "";
		String strCompleteData = "";
		File f = null;
		String webserviceUtility = "";
		String restfulserviceUtility = "";
		String dashboardpath = "";
		String resultpath = "";
		File dir;
		File execStatusFile = null;//Meghna

		try
		{	
			//Jacob.main("D:\\Workspace\\iTAFSeleniumWeb\\RegressionTesting_MASTER_old\\Resources\\Independent_XML_COMPARISON_Utility.xlsm", "Macro1");
			Automation.setUp();
			
			controller = ObjectFactory.getMainController();
			
			email = Config.emailId;			
			reportsheet = Config.resultOutput;
			detailreportsheet = Config.verificationResultPath;
			
			log.info("MainController File Path is :"+Config.controllerFilePath);
			
			TransactionMapping.TransactionInputData("Login");  //Default Login START
			WebHelperUtil.saveScreenShot();
			
			//Meghna- For Suite Testing//
			if(Config.suiteTesting.equalsIgnoreCase("TRUE"))
			{
				SuiteDriver.start();
			}
			
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
				log.error(e.getMessage(), e);
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
					log.error(e.getMessage(), e);
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			
			//Meghna - Added keyword END for SUITE Integration//
			if(controller.controllerTransactionType !=null)
			{
				if(controller.controllerTransactionType.toString().equalsIgnoreCase("END"))
				{
					ExcelUtility.writeReport(report);
					System.exit(1);
				}
			}
			//END

			//*** Meghna***	
			Date finalfrmDate = new Date();
			report.setIteration(Config.cycleNumber);
			report.setTestcaseId("Common");
			report.setCycleDate("Common");			
			report.setTrasactionType("XML Comparison Macro");
			report.setFromDate(Config.dtFormat.format(finalfrmDate));
			//report.setStrGroupName(MainController.controllerGroupName.toString());
			//***		
			
			//***Mandar***
			dashboardpath = Config.dashboardFilepath;
			log.info("dashboardpath : "+dashboardpath);
			//***
			
			if(Config.webserviceComparison.equalsIgnoreCase("True"))
			{
				webserviceUtility = Config.webserviceComparisonUtilityPath;
				log.info("webserviceUtility : "+webserviceUtility);
				//Call to Independent_XML_COMPARISON_Utility.xlsm
				try 
				{
					Thread.sleep(5000);
				} 
				catch (InterruptedException e1) 
				{
					log.error(e1.getMessage(), e1);
					//e1.printStackTrace(); 	// TODO Auto-generated catch block
				}
				//Jacob.main(webserviceUtility, "Macro1");//Mandar
				
				////Aniruddha***
				Jacob.main(webserviceUtility, "WebserviceVerification");		
				report.setStatus("Executed Successfully");
				ExcelUtility.writeReport(report);	
				//***
						
				try 
				{
					CalendarSnippet.killProcess("EXCEL.EXE");
				} 
				catch (Exception e) 
				{
					log.error(e.getMessage(), e);
					//e.printStackTrace(); // TODO Auto-generated catch block
				}
			
				f= new File(Config.resultFilePath+"WebService_Comparison_Results.xls");
			
			
				if(f.exists())
				{		
					log.info("FileExists");
					//Call to Results_Dashboard_Utility.XLS
					try 
					{
						Thread.sleep(5000);
					} 
					catch (InterruptedException e1) 
					{
						log.error(e1.getMessage(), e1);
						//e1.printStackTrace(); 	// TODO Auto-generated catch block
					}
					
					//Meghna***
					Date final1frmDate = new Date();
					report.setFromDate(Config.dtFormat.format(final1frmDate));
					report.setTrasactionType("Dashboard Macro");
					dashboardpath = Config.dashboardFilepath;
					log.info("dashboardpath : "+dashboardpath);
					Jacob.main(dashboardpath, "Macro1");
					report.setStatus("Executed Successfully");
					ExcelUtility.writeReport(report);	
					//***
					
					try 
					{
						CalendarSnippet.killProcess("EXCEL.EXE");
					} 
					catch (Exception e) 
					{
						log.error(e.getMessage(), e);
						//e.printStackTrace(); // TODO Auto-generated catch block
					}	
				}
				else
				{
					
					log.info("WebService_Comparison_Results file does not exist ");
				}
			}
			//*** Meghna
			//***For GB***09/06/2018***REST Verification
			else if("True".equalsIgnoreCase(Config.restfulserviceComparison))
			{
				restfulserviceUtility = Config.restfulServiceComparisonUtilityPath;
				log.info("restfulserviceUtility : "+restfulserviceUtility);
				
				try 
				{
					Thread.sleep(5000);
				} 
				catch (InterruptedException e1) 
				{
					log.error(e1.getMessage(), e1);
					//e1.printStackTrace(); 	// TODO Auto-generated catch block
				}
				
				Jacob.main(restfulserviceUtility, "restfulserviceVerification");		
				report.setStatus("Executed Successfully");
				ExcelUtility.writeReport(report);	
									
				try 
				{
					CalendarSnippet.killProcess("EXCEL.EXE");
				} 
				catch (Exception e) 
				{
					log.error(e.getMessage(), e);
					//e.printStackTrace(); // TODO Auto-generated catch block
				}
			
				f= new File(Config.resultFilePath+"RestfulService_Comparison_Results.xls");
			
			
				if(f.exists())
				{		
					log.info("FileExists");
					//Call to Results_Dashboard_Utility.XLS
					try 
					{
						Thread.sleep(5000);
					} 
					catch (InterruptedException e1) 
					{
						log.error(e1.getMessage(), e1);
						//e1.printStackTrace(); 	// TODO Auto-generated catch block
					}
					
					Date final1frmDate = new Date();
					report.setFromDate(Config.dtFormat.format(final1frmDate));
					report.setTrasactionType("Dashboard Macro");
					dashboardpath = Config.dashboardFilepath;
					log.info("dashboardpath : "+dashboardpath);
					Jacob.main(dashboardpath, "Macro1");
					report.setStatus("Executed Successfully");
					ExcelUtility.writeReport(report);	
					//***
					
					try 
					{
						CalendarSnippet.killProcess("EXCEL.EXE");
					} 
					catch (Exception e) 
					{
						log.error(e.getMessage(), e);
						//e.printStackTrace(); // TODO Auto-generated catch block
					}	
				}
				else
				{
					
					log.info("RestfulService_Comparison_Results file does not exist ");
				}
			}
			//***need to work on the below given code ---09/06/2018 -- Mandar
			else
			{
				report.setStatus("Did not execute");
				report.setGroupName("Independent_XML_COMPARISON_Utility file does not exist");
				ExcelUtility.writeReport(report);	
			}
			//***
			
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
			
			//BACKUP -- Meghna
			String target_path = Config.backupFilepath;
			
			if (!(target_path.equals("")))
			{
				String input_folder= Config.inputDataFilePath;
				String result_folder= Config.resultFilePath;
				String mainC_file= Config.controllerFilePath;
				
				Calendar cd = Calendar.getInstance();
				
				int currMonth = cd.get((Calendar.MONTH)) + 1 ;
				
				String s_prefix = cd.get(Calendar.DAY_OF_MONTH) + "_" + currMonth + "_" + cd.get(Calendar.YEAR) + "_" + cd.get(Calendar.HOUR) + "_" + cd.get(Calendar.MINUTE) + "_" + cd.get(Calendar.SECOND);
				
			
				target_path =target_path + "/" + "Bck_" + s_prefix;
				
				File targetPath = new File(target_path);
				
				if (!targetPath.exists()) 
				{
					targetPath.mkdir();
				}
				
				backup(new File(input_folder),new File(target_path + "/Input"));
				backup(new File(result_folder),new File(target_path + "/Results"));
				backup(new File(mainC_file),new File(target_path));
				
				report.setTrasactionType("Backup");
				report.setStatus("Backup done");
				report.setMessage(target_path);
				ExcelUtility.writeReport(report);	
				
				log.info("Backup done");
			}
			
			try
			{
				String statusUtility = Config.executionStatusReportUtility;
				log.info("Status Utility : "+statusUtility);
				
				if (statusUtility.equals(""))
				{
					log.info("Execution Status Tracker utility path not found in Config");
				}
				else
				{
					execStatusFile= new File(statusUtility);
					if(execStatusFile.exists())
					{
						Jacob.main(statusUtility, "report");
						log.info("Status Report sent");
					}
					else
					{
						System.out.println("Execution Status Tracker utility not found.");
					}
				}
			}
			catch (Exception e) 
			{
				log.error(e.getMessage(), e);
				e.printStackTrace(); // TODO Auto-generated catch block
			}
			
			//Send Status -- Meghna
			
			/*///This code is commented for un-interrupted mode execution of both Single Account and Agency bill 
			 * frame.setVisible(true);
			frame.setAlwaysOnTop(true);
			WebDriver.frame.setLocationRelativeTo(null);
			JOptionPane.setRootFrame(WebDriver.frame);
			JOptionPane.showMessageDialog(frame, "Execution Completed");		
			frame.dispose();
			*/
			
			System.exit(1);
		}
	}

	//Backup -- Meghna
	public static void backup(File sourceLocation, File targetLocation) throws IOException 
	{
		if (sourceLocation.isDirectory()) {
			FileUtils.copyDirectory(sourceLocation, targetLocation);
		} else {
			FileUtils.copyFileToDirectory(sourceLocation, targetLocation);
		}
	}
	// Backup
	
	@SuppressWarnings("unused")
	//Meghna:R10.10-For Common Structure Sheet - Added argument structurePath
	public void DataInput(String structurePath,String filePath,String testcaseID,String transactionType,String transactionCode,String operationType,String cycleDate) throws Exception
	{
		log.info("filePath is:"+filePath);
		if(transactionCode == null)
		{
			transactionCode = transactionType; 
		}
		log.info(transactionCode);
		
		//***Meghna
		if(transactionType.equalsIgnoreCase("CommissionExtraction") && cycleDate.equalsIgnoreCase("02/06/2012"))
		{			
			System.out.println("HI");
		}
		//***
		//if(operationType.equalsIgnoreCase("InputandVerfiy")&&!operationType.isEmpty())
		if(operationType.equalsIgnoreCase("InputandVerfiy") && !StringUtils.isEmpty(operationType.toString()))
		{			
			ExcelUtility.GetDataFromValues(structurePath, filePath, testcaseID.toString(), transactionType.toString(), cycleDate, operationType);
			//WebVerification.performVerification(transactionType, testcaseID);
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
			ExcelUtility.GetDataFromValues(structurePath, filePath, testcaseID.toString(), transactionType.toString(), cycleDate, operationType);
		}
		//else if(!operationType.equalsIgnoreCase("Input")&&!operationType.isEmpty())
		else if(!operationType.equalsIgnoreCase("Input") && !StringUtils.isEmpty(operationType.toString()))
		{
			log.info("INPUT");
			WebVerification.performVerification(transactionType, testcaseID, "", cycleDate);
		}
	}

}
