package com.majesco.itaf.main;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Sheet;

import com.majesco.itaf.util.ExcelUtility;
import com.majesco.itaf.vo.Reporter;
//XX

public class TransactionMapping {

	private final static Logger log = Logger.getLogger(TransactionMapping.class.getName());
	private static String operationType ="";
	public static String directoryPathFileUpload = ""; //Meghna---For FileUpload
	
	private static ITAFWebDriver webDriver = ITAFWebDriver.getInstance();
	private static MainController controller = ObjectFactory.getMainController();
	//private static WebHelperPAS webHelper = (WebHelperPAS)ObjectFactory.getInstance().getWebHelper();//TODO remove cast and change the variable type to WebHelper
	//private static WebHelperBilling webHelperBilling = (WebHelperBilling)ObjectFactory.getInstance().getWebHelper();//TODO remove cast and change the variable type to WebHelper

	public static Reporter TransactionInputData(String cycleDate, String controllerTestCaseID,String controllerTransactionType,String filePath) throws IOException,Exception
	{
		Reporter report = new Reporter();
		Sheet workSheet =null;
		HashMap<String, Integer> inputHashTable = new HashMap<>();
		try
		{
			if(ITAFWebDriver.isClaimsApplication()){
				workSheet = ExcelUtility.getXLSSheet(Config.transactionInputFilePath, "Web_Transaction_Input_Files");
			} else {
				workSheet = ExcelUtility.GetSheet(Config.transactionInputFilePath, "Web_Transaction_Input_Files");
				log.info("TRANSACTION INPUT FILE PATH : " + Config.transactionInputFilePath);
			}
		}
		catch(IOException ioe)
		{
			log.error("Failed to access TRANSACTION_INPUT_FILEPATH <-|-> LocalizeMessage " + ioe.getLocalizedMessage() +" <-|-> Message "+ ioe.getMessage() +" <-|-> Cause "+ ioe.getCause(), ioe);
			throw new Exception("Failed to access TRANSACTION_INPUT_FILEPATH   <-|-> LocalizeMessage " + ioe.getLocalizedMessage() +" <-|-> Message"+ ioe.getMessage() +" <-|-> Cause "+ ioe.getCause());	
		}
		
		int rowCount = workSheet.getLastRowNum()+1;
		for(int rowIndex=1;rowIndex<rowCount&&!controller.pauseExecution;rowIndex++)
		{
			String transactionCode = WebHelperUtil.getCellData("TransactionCode", workSheet, rowIndex, inputHashTable);			
			String transactionType1 = WebHelperUtil.getCellData("TransactionType", workSheet, rowIndex, inputHashTable);//dev
			String directoryPath = WebHelperUtil.getCellData("DirPath", workSheet, rowIndex, inputHashTable);
			String inputExcel = WebHelperUtil.getCellData("InputSheet", workSheet, rowIndex, inputHashTable);
			

			//bhaskar to check multiple values in TransactionType START
			boolean rowFound = false;
			//bhaskar to check multiple values in TransactionType END
			// Devishree to check multiple values in TransactionType Start
			String[] transactionTypeArr = transactionType1.split(",");
			int transactionTypeArrSize = transactionTypeArr.length;
						
			for(int Introwindex=0;Introwindex<=transactionTypeArrSize-1;Introwindex++)
			{
			String transactionType = transactionTypeArr[Introwindex];
			if(transactionTypeArrSize>1)
				{
				log.info("TransactionType Value:"+transactionType1);
				}
			
			log.info("TransactionType in Transaction mapping :"+transactionType);
			log.info("TransactionType in Maincontroller :"+controllerTransactionType);
			
			// Devishree to check multiple values in TransactionType End (Extra for loop below)
					
			if(transactionType.equalsIgnoreCase(controllerTransactionType))
			{
				if(transactionCode != null&&directoryPath == null && controllerTransactionType.equalsIgnoreCase(transactionType))
				{
					report.setInputPath("");
					report.setOperationType("");
					report.setTransactioncode(transactionCode);
					
					webDriver.getReport().setTransactioncode(transactionCode);  //Added by aniruddha for trans_id issue 03/02/2017
					webDriver.getReport().setTestcaseId(controllerTestCaseID); //Added by aniruddha for trans_id issue 03/03/2017
					
					webDriver.DataInput("","", controllerTestCaseID, transactionType, transactionCode, "",cycleDate);//Meghna:R10.10-For Common Structure Sheet - Added argument

					rowFound = true;
					break;
				}
				
				if(!transactionType.startsWith("Verify"))
				{
					operationType = "Input";
				}

				//if(transactionType.startsWith("Verify") && (!directoryPath.isEmpty()) && (!inputExcel.isEmpty()))
				if(transactionType.startsWith("Verify") && (!StringUtils.isEmpty(directoryPath)) && (!StringUtils.isEmpty(inputExcel)))
				{
					operationType = "InputandVerfiy";
					log.info("InputandVerify");
				}
				else if(transactionType.startsWith("Verify") && (StringUtils.isEmpty(directoryPath)) && (StringUtils.isEmpty(inputExcel)))
				{
					operationType = "Verify";
				}
				
				log.info("operationType is:"+operationType);
				if(controllerTransactionType.equalsIgnoreCase(transactionType))
				{
					if((directoryPath == null||inputExcel == null)&& operationType !="Verify")
					{
						controller.pauseFun("Please Enter the directory or excelsheet name");
					}
					else
					{
						String inputFilePath=null;
						String structurePath = null;//Meghna:R10.10-For Common Structure Sheet
						if(operationType != "Verify")
						{
							inputFilePath = Config.inputDataFilePath + directoryPath + "\\" + inputExcel;
							directoryPathFileUpload = directoryPath; //Meghna---For FileUpload
							if (ITAFWebDriver.isBillingApplication()) {
								structurePath = Config.structureSheetFilePath + directoryPath + "\\" + inputExcel; //Meghna:R10.10-For Common Structure Sheet
							}
						}					
						log.info(inputFilePath);
						report.setInputPath(inputFilePath);
						report.setOperationType(operationType);
						report.setTransactioncode(transactionCode);	
						
						webDriver.getReport().setTransactioncode(transactionCode);	//Added by aniruddha foe trans_id issue 03/02/2017
						webDriver.getReport().setTestcaseId(controllerTestCaseID);    //Added by aniruddha foe trans_id issue 03/03/2017
						
						if(ITAFWebDriver.isClaimsApplication()) {
							webDriver.DataInput(inputFilePath,controllerTestCaseID,transactionType,transactionCode,operationType,cycleDate);
						} else if(ITAFWebDriver.isBillingApplication()) {
							webDriver.DataInput(structurePath, inputFilePath,controllerTestCaseID,transactionType,transactionCode,operationType,cycleDate);
						}
						rowFound = true;
						break;		
					}
				}
			}
			else if(!transactionType.equalsIgnoreCase(controllerTransactionType)&& rowIndex == rowCount-1)
			{
				controller.pauseFun("Transaction "+controller.controllerTransactionType+" Not Found");
				ExcelUtility.writeReport(webDriver.getReport());
			}
			}// Devishree
			//bhaskar to check multiple values in TransactionType START
			if(rowFound == true)
			break;
			//bhaskar to check multiple values in TransactionType END
		}
		return null;
	}
	
	//bhaskar for Login and ChangeBusinessdate Transactions
	public static void TransactionInputData(String controllerTransactionType) throws IOException, NullPointerException, Exception
	{
		try
		{
		HashMap<String, Integer> inputHashTable1 = new HashMap<>();
		Sheet workSheet;
		
		if(ITAFWebDriver.isClaimsApplication()){
			workSheet = ExcelUtility.getXLSSheet(Config.transactionInputFilePath, "Web_Transaction_Input_Files");
		} else {
			workSheet = ExcelUtility.GetSheet(Config.transactionInputFilePath, "Web_Transaction_Input_Files");
		}
		
		int rowCount1 = workSheet.getLastRowNum()+1;
		log.info("Transaction Name in MainController : "+controllerTransactionType);
		
		webDriver.getReport().setTestcaseId("Common");
		
		if(controller.cycleDateCellValue == ("") || controller.cycleDateCellValue == null )
		{		
			webDriver.getReport().setCycleDate("Common");
		} else {
			webDriver.getReport().setCycleDate(controller.cycleDateCellValue);
		}
		
		webDriver.getReport().setTrasactionType(controllerTransactionType);
		Date toDate = new Date();
		webDriver.getReport().setToDate(Config.dtFormat.format(toDate));
		
		
		for(int rowIndex=1;rowIndex<rowCount1;rowIndex++)
		{
			//Added by aniruddha foe trans_id issue 03/02/2017
			webDriver.getReport().setMessage("");
			webDriver.getReport().setScreenShot("");

			String transactionCode1 = WebHelperUtil.getCellData("TransactionCode", workSheet, rowIndex, inputHashTable1);			
			String transactionType1 = WebHelperUtil.getCellData("TransactionType", workSheet, rowIndex, inputHashTable1);
			String directoryPath1 = WebHelperUtil.getCellData("DirPath", workSheet, rowIndex, inputHashTable1);
			String inputExcel1 = WebHelperUtil.getCellData("InputSheet", workSheet, rowIndex, inputHashTable1);
			//log.info("Transaction Name in transaction mapping : "+transactionType1);
			
			if(transactionType1.equalsIgnoreCase(controllerTransactionType))
			{
				if(inputExcel1 == null || StringUtils.isEmpty(inputExcel1))
				{
					controller.pauseFun("Please Enter the Input excel sheet name");
				}
				if((directoryPath1 != null && inputExcel1 != null) || (!StringUtils.isEmpty(directoryPath1) && !StringUtils.isEmpty(inputExcel1)))
				{
					operationType = "Input";
					String inputFilePath1 = null;
					inputFilePath1 = Config.inputDataFilePath + directoryPath1 + "\\" + inputExcel1;
					
					if(ITAFWebDriver.isClaimsApplication()){
						webDriver.DataInput(inputFilePath1,"",transactionType1,transactionCode1,operationType,"");
					} else if(ITAFWebDriver.isBillingApplication()){
						//Meghna:R10.10-For Common Structure Sheet
						String inputFilePath_structure = null;
						inputFilePath_structure = Config.structureSheetFilePath + directoryPath1 + "\\" + inputExcel1;
						webDriver.DataInput(inputFilePath_structure,inputFilePath1,"",transactionType1,transactionCode1,operationType,"");
					}
					break;
				}
				if((directoryPath1 == null && inputExcel1 != null) || (StringUtils.isEmpty(directoryPath1) && !StringUtils.isEmpty(inputExcel1)))
				{
					operationType = "Input";
					String inputFilePath2 = null;
					inputFilePath2 = Config.inputDataFilePath + inputExcel1;
					
					if(ITAFWebDriver.isClaimsApplication()){
						webDriver.DataInput(inputFilePath2,"",transactionType1,transactionCode1,operationType,"");
					} else if(ITAFWebDriver.isBillingApplication()){
						//Meghna:R10.10-For Common Structure Sheet					
						String inputFilePath_structure = null;
						inputFilePath_structure = Config.structureSheetFilePath + inputExcel1;
						webDriver.DataInput(inputFilePath_structure,inputFilePath2,"",transactionType1,transactionCode1,operationType,"");
					}
					break;
				}
				
			}
			else if(!transactionType1.equalsIgnoreCase(controllerTransactionType)&& rowIndex == rowCount1-1)
			{
				controller.pauseFun("Transaction "+transactionType1+" Not Found");
			}
		}
		}
		catch(IOException ne)
		{
			log.error("Failed to access TRANSACTION_INPUT_FILEPATH <-|-> LocalizeMessage " + ne.getLocalizedMessage() +" <-|-> Message "+ ne.getMessage() +" <-|-> Cause "+ ne.getCause(), ne);
			throw new Exception("Failed to access TRANSACTION_INPUT_FILEPATH   <-|-> LocalizeMessage " + ne.getLocalizedMessage() +" <-|-> Message"+ ne.getMessage() +" <-|-> Cause "+ ne.getCause());
		}
		catch(NullPointerException ne)
		{
			log.error("Failed to access TRANSACTION_INPUT_FILEPATH <-|-> LocalizeMessage " + ne.getLocalizedMessage() +" <-|-> Message "+ ne.getMessage() +" <-|-> Cause "+ ne.getCause(), ne);
			throw new Exception("Failed to access TRANSACTION_INPUT_FILEPATH   <-|-> LocalizeMessage " + ne.getLocalizedMessage() +" <-|-> Message"+ ne.getMessage() +" <-|-> Cause "+ ne.getCause());
		}
		catch(Exception e)
		{
			log.error("Failed while access TRANSACTION_INPUT_FILEPATH  <-|-> LocalizeMessage " + e.getLocalizedMessage() +" <-|-> Message "+ e.getMessage() +" <-|-> Cause "+ e.getCause(), e);
			throw new Exception("Failed while access TRANSACTION_INPUT_FILEPATH  <-|-> LocalizeMessage " + e.getLocalizedMessage() +" <-|-> Message"+ e.getMessage() +" <-|-> Cause "+ e.getCause());	
		}
	}
	
	//bhaskar CAPTURE Keyword START
	public static Reporter TransactionCaptureData(String cycleDate, String controllerTestCaseID,String controllerTransactionType,String filePath) throws Exception
	{
		Reporter report = new Reporter();
		HashMap<String, Integer> inputHashTable = new HashMap<>();
		Sheet workSheet;
		
		if(ITAFWebDriver.isClaimsApplication()){
			workSheet = ExcelUtility.getXLSSheet(Config.transactionInputFilePath, "Web_Transaction_Input_Files");
		} else {
			workSheet = ExcelUtility.GetSheet(Config.transactionInputFilePath, "Web_Transaction_Input_Files");
		}
		
		int rowCount = workSheet.getLastRowNum()+1;
		for(int rowIndex=1;rowIndex<rowCount&&!controller.pauseExecution;rowIndex++)
		{
			String transactionCode = WebHelperUtil.getCellData("TransactionCode", workSheet, rowIndex, inputHashTable);			
			String transactionType = WebHelperUtil.getCellData("TransactionType", workSheet, rowIndex, inputHashTable);
			log.info("bhaskar transactionType:"+transactionType);
			String directoryPath = WebHelperUtil.getCellData("DirPath", workSheet, rowIndex, inputHashTable);
			log.info("bhaskar directoryPath:"+directoryPath);
			String inputExcel = WebHelperUtil.getCellData("InputSheet", workSheet, rowIndex, inputHashTable);
			log.info("bhaskar inputExcel:"+inputExcel);
			if(transactionType.equalsIgnoreCase(controllerTransactionType))
			{
				if(transactionCode != null&&directoryPath == null && controllerTransactionType.equalsIgnoreCase(transactionType))
				{
					report.setInputPath("");
					report.setOperationType("");
					report.setTransactioncode(transactionCode);
					if(ITAFWebDriver.isClaimsApplication()) {
						webDriver.DataInput("", controllerTestCaseID, transactionType, transactionCode, "",cycleDate);
					} else if (ITAFWebDriver.isBillingApplication()) {
						webDriver.DataInput("","", controllerTestCaseID, transactionType, transactionCode, "",cycleDate); //Meghna:R10.10-For Common Structure Sheet - Added argument
					}
					
					break;
				}
				
				if(!transactionType.startsWith("Verify"))
				{
					operationType = "Input";
				}

				if(transactionType.startsWith("Verify") && (!StringUtils.isEmpty(directoryPath)) && (!StringUtils.isEmpty(inputExcel)))
				{
					operationType = "InputandVerfiy";
					log.info("InputandVerify");
				}
				else if(transactionType.startsWith("Verify") && (StringUtils.isEmpty(directoryPath)) && (StringUtils.isEmpty(inputExcel)))
				{
					operationType = "Capture";
				}
				
				log.info("operationType is:"+operationType);
				if(controllerTransactionType.equalsIgnoreCase(transactionType))
				{
					if((directoryPath == null||inputExcel == null)&& operationType !="Capture")
					{
						controller.pauseFun("Please Enter the directory or excelsheet name");
					}
					else
					{
						String inputFilePath=null;
						
						//if(operationType != "Capture")
						//{
							inputFilePath = Config.inputDataFilePath + directoryPath + "\\" + inputExcel;
						//}					
						log.info(inputFilePath);
						report.setInputPath(inputFilePath);
						report.setOperationType(operationType);
						report.setTransactioncode(transactionCode);
						
						if(ITAFWebDriver.isClaimsApplication()) {
							webDriver.DataInput(inputFilePath,controllerTestCaseID,transactionType,transactionCode,operationType,cycleDate);
						} else if (ITAFWebDriver.isBillingApplication()) {
							//Meghna:R10.10-For Common Structure Sheet
							String structurePath=null;
							structurePath = Config.structureSheetFilePath + directoryPath + "\\" + inputExcel;
							webDriver.DataInput(structurePath,inputFilePath,controllerTestCaseID,transactionType,transactionCode,operationType,cycleDate);
						}

						break;
					}
				}
			}
			else if(!transactionType.equalsIgnoreCase(controllerTransactionType)&& rowIndex == rowCount-1)
			{
				controller.pauseFun("Transaction "+controller.controllerTransactionType+" Not Found");
				ExcelUtility.writeReport(webDriver.getReport());
			}
		}
		return null;
	}
	//bhaskar CAPTURE keyword END

	public static Reporter TransactionInputData(String controllerTestCaseID, String controllerTransactionType,String filePath) throws Exception
	{
		Reporter report = new Reporter();
		HashMap<String, Integer> inputHashTable = new HashMap<String, Integer>();
		HSSFSheet workSheet = ExcelUtility.getXLSSheet(Config.transactionInputFilePath, "Web_Transaction_Input_Files");
		int rowCount = workSheet.getLastRowNum()+1;
		for(int rowIndex=1;rowIndex<rowCount&&!controller.pauseExecution;rowIndex++)
		{
			String transactionCode = WebHelperUtil.getCellData("TransactionCode", workSheet, rowIndex, inputHashTable);			
			String transactionType = WebHelperUtil.getCellData("TransactionType", workSheet, rowIndex, inputHashTable);
			System.out.println("bhaskar transactionType:"+transactionType);
			String directoryPath = WebHelperUtil.getCellData("DirPath", workSheet, rowIndex, inputHashTable);
			System.out.println("bhaskar directoryPath:"+directoryPath);
			String inputExcel = WebHelperUtil.getCellData("InputSheet", workSheet, rowIndex, inputHashTable);
			System.out.println("bhaskar inputExcel:"+inputExcel);
			if(transactionType.equalsIgnoreCase(controllerTransactionType))
			{
				if(transactionCode != null&&directoryPath == null && controllerTransactionType.equalsIgnoreCase(transactionType))
				{
					report.setInputPath("");
					report.setOperationType("");
					report.setTransactioncode(transactionCode);
					webDriver.DataInput("", controllerTestCaseID, transactionType, transactionCode, "");
					break;
				}
				
				if(!transactionType.startsWith("Verify"))
				{
					operationType = "Input";
				}

				if(transactionType.startsWith("Verify") && (!directoryPath.isEmpty()) && (!inputExcel.isEmpty()))
				{
					operationType = "InputandVerfiy";
					System.out.println("--------------------------InputandVerify--------------------------");
					System.out.println("--------------------------InputandVerify--------------------------");
				}
				else if(transactionType.startsWith("Verify") && (directoryPath.isEmpty()) && (inputExcel.isEmpty()))
				{
					operationType = "Verify";
				}
				
				System.out.println("operationType is:"+operationType);
				if(controllerTransactionType.equalsIgnoreCase(transactionType))
				{
					if((directoryPath == null||inputExcel == null)&& operationType !="Verify")
					{
						controller.pauseFun("Please Enter the directory or excelsheet name");
					}
					else
					{
						String inputFilePath=null;
						if(operationType != "Verify")
						{
							inputFilePath = Config.inputDataFilePath + directoryPath + "\\" + inputExcel;
							directoryPathFileUpload = directoryPath; //Added For FileUpload
						}					
						System.out.println(inputFilePath);
						report.setInputPath(inputFilePath);
						report.setOperationType(operationType);
						report.setTransactioncode(transactionCode);						
						webDriver.DataInput(inputFilePath,controllerTestCaseID,transactionType,transactionCode,operationType);
						break;
					}
				}
			}
			else if(!transactionType.equalsIgnoreCase(controllerTransactionType)&& rowIndex == rowCount-1)
			{
				controller.pauseFun("Transaction "+controller.controllerTransactionType+" Not Found");
				ExcelUtility.writeReportPAS(webDriver.getReport());
			}
		}
		return null;
	}

}
