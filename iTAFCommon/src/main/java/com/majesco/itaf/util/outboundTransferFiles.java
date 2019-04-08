package com.majesco.itaf.util;

import java.io.File;
import java.util.Vector;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.majesco.itaf.main.Config;
import com.majesco.itaf.main.ITAFWebDriver;
import com.majesco.itaf.main.MainController;
import com.majesco.itaf.main.ObjectFactory;
import com.majesco.itaf.main.WebHelper;
import com.majesco.itaf.main.WebHelperBilling;
import com.majesco.itaf.webservice.WebService;



public class outboundTransferFiles {
	private final static Logger log = Logger.getLogger(WebHelperBilling.class.getName());
	public static String FlatFileResponse = null;
	public static int failedSC = -1;
	public static String report_msg;
	public static String report_status;
	
	private static ITAFWebDriver webDriver = ITAFWebDriver.getInstance();
	private static MainController controller = ObjectFactory.getMainController();
	
	public void copyFiles(String copyFrom,String copyTo, String xmlFileName, String extn, String xflFile,String archive,String validateTag, String validationMsg,String file_cycledate)
	{
		 //Input Details For Linux
		
		/* System.out.println("Please enter the hostname of the server ");
	     String hostName = sc.nextLine();
	     
	     System.out.println("Please enter your username: ");
	     String userName = sc.nextLine();
	     
	     System.out.println("Please enter your password: ");
	     String password = sc.nextLine();
	     
	     System.out.println("Please enter the location where your files can be found: ");
	     String copyFrom = sc.nextLine();
	     
	     System.out.println("Please enter the location where you want to place your files: ");
	     String copyTo = sc.nextLine();
	     
	     System.out.println("Enter the extension of File you need to copy: ");
	     String extn = sc.nextLine();
	     
	     System.out.println("Enter the xfl to be executed with Extension: ");
	     String xflFile = sc.nextLine();*/
	     
	     
	     //Get data from Config
	     String hostName = Config.flatFileHostName;
	     String userName = Config.flatFileUserName;
	     String password = Config.flatFilePassword;
		 int port = Integer.parseInt(Config.flatFilePort);
	     
	     //
		 
		 Boolean flatfile_found = false;
	     
	     
		 JSch jsch = new JSch();
		
		 Session session = null;
	     System.out.println("Trying to connect.....");
	   
	    try
	    {
            session = jsch.getSession(userName, hostName, 22);
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(password);
            session.connect(); 
            
            Channel channel = session.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp) channel;
            
           
            System.out.println("Done !!");
            
            //FlatFile copying from remote Linux server to local machine
            
            sftpChannel.cd(copyFrom);
            Vector filelist = sftpChannel.ls(copyFrom);

            report_msg = "";
            report_status = "";
            String searchFile;
            
            if(WebHelperBilling.stransactionType.contains("Outbound"))
            {
            	searchFile = xmlFileName + "_";
            }
            else
            {
            	searchFile = xmlFileName + "_" + file_cycledate;
            }
            
            for(int i=0; i<filelist.size();i++)
            {
                LsEntry entry = (LsEntry) filelist.get(i);

                Boolean b = entry.getFilename().endsWith(extn);
                
                if(b == true)
                {	
                
                String fileName = entry.getFilename();
                String response_filename = fileName.replace(extn,".xml");
                String scno_responseFile = response_filename.substring(2, (response_filename.indexOf('_')));   //Meghna---for Flat file Sc failure
                response_filename = copyTo + "/" +response_filename;
                
                
                
                //Converting Flat file to xml
                
                if(WebHelperBilling.stransactionType.contains("Outbound"))
                {
                	if(fileName.startsWith(xmlFileName + "_"))
                	{	
                		flatfile_found = true;
                		try
                		{
                			File ff_dir = new File(copyTo);
                    		FileUtils.forceMkdir(ff_dir);
                    		log.info("Directory created " + ff_dir);
                		}
                		catch(Exception e)
            			{
                			log.error(e.getMessage(), e);
            				log.info("Creating direcotory failed");
            			}
                		

                		sftpChannel.get(entry.getFilename(), copyTo);
                    	flatToXmlConversion fx = new flatToXmlConversion();
                        fx.convertFlatToXml(copyTo,fileName,extn,xflFile);
                        
                        File oldFile = new File(response_filename);
                        File newFile = new File(copyTo + "/" +xmlFileName + "_" + file_cycledate + "_Response.xml");
                        
                        log.info("Oubound File craeted : " + copyTo + "/" +xmlFileName + "_" + file_cycledate + "_Response.xml");
                        
                        if(oldFile.renameTo(newFile))
                        {
                        	log.info("Renamed");
                        }
                        else
                        {
                        	log.info("Failed Renamed");
                        }
                        
                        break;
                	}
                }
                
                else if(fileName.startsWith(xmlFileName + "_" + file_cycledate))			//---For Inbound  Response              	
                //if(fileName.startsWith(searchFile))
                {	
                	
                	flatfile_found = true;
                	sftpChannel.get(entry.getFilename(), copyTo);
                	flatToXmlConversion fx = new flatToXmlConversion();
                    fx.convertFlatToXml(copyTo,fileName,extn,xflFile);
                    
                    
                  //Meghna--Verify Response XML//
                    try
                    {
                    	String Tag_Name = "*";
        				String[] Node_Value = new String[2];
        				Node_Value[1] = "ErrorCode";
        				Node_Value[0] = "Description";
        				
        				int index = 0;
        				
        					//successFlag = WebService.getXMLResponseTagValue(responseXml,Tag_Name,Node_Value1,index);
        				FlatFileResponse = WebService.getXMLResponseStatusFlatFile(response_filename,Tag_Name,Node_Value,index,validateTag,validationMsg);

        						//if(successFlag == null)
        					if(FlatFileResponse.equalsIgnoreCase("SUCCESS"))
        				{

        					//iTAFSeleniumWeb.WebDriver.report.setStrMessage("SUCCESS");//Mandar--
        					report_msg = "SUCCESS:" + response_filename;
        					report_status = "PASS";
        					/*iTAFSeleniumWeb.WebDriver.report.setStrMessage("SUCCESS : Matched -- "+ Node_Value[1]);//Mandar
        					iTAFSeleniumWeb.WebDriver.report.setStrStatus("PASS");*/
        					WebHelper.success = true;
        					WebHelper.description = "SUCCESS";									
        				}

        				else if(FlatFileResponse.equalsIgnoreCase("FAILED") && WebHelper.success != true)
        					{
        						//successFlag = WebService.getXMLResponseTagValue(responseXml,Tag_Name,Node_Value2,index);
        					Node_Value[1] = "ErrorCode";
        					WebHelper.FailedResponseTagValue = WebService.getXMLResponseTagValue(response_filename,Tag_Name,Node_Value[1],index);
        					if(webDriver.getReport().getMessage() == null || webDriver.getReport().getMessage() == "")
        					{
        						//to do//Mandar --20/09/2017
        					}
        					//iTAFSeleniumWeb.WebDriver.report.setStrMessage(FailedResponseTagValue);//Mandar--
        					/*iTAFSeleniumWeb.WebDriver.report.setStrMessage("REQUEST FAILED : Error Msg displayed -- " + WebHelper.FailedResponseTagValue);//Mandar --
        					iTAFSeleniumWeb.WebDriver.report.setStrStatus("FAIL");*/
        					
        					//Meghna--Flat File
        					report_msg = "FAILED:" + response_filename ;
        					report_status = "FAIL";
        					//Meghna--Flat File
        					
        					WebHelper.description = WebHelper.FailedResponseTagValue;
        					WebHelper.failed = true;
        					} 								
        				//String Node_Value1 = "ProcessStatusFlag";
        				//String ProcessStatusFlag = WebService.getXMLResponseTagValue(responseXml,Tag_Name,Node_Value1,index);
        				System.out.println("Tag value from Response file is:"+WebHelper.FailedResponseTagValue);
        					
        				if(FlatFileResponse == null || FlatFileResponse.equalsIgnoreCase("FAILED") || FlatFileResponse.equalsIgnoreCase("BLANK"))
        				{
        					//NoResponseFile = true;
        					//bhaskar Recovery Scenario (WebService) START
        					
        					//Meghna--For marking SC in MainCont in case of failures
        					
        					failedSC = Integer.parseInt(scno_responseFile);
        					
        					controller.recoveryhandler();
        					log.info("Flat File response failed for : " +failedSC );
        					
        					
        					//bhaskar Recovery Scenario (WebService) END
        				}
        				WebHelper.success = false;//Mandar--Uncommented as this was writing SUCCESS instead of error message for failed scenarios.---Meghna
                    }
    				
                    catch(Exception e)
                    {
                    	log.error(e.getMessage(), e);
                    	//---Meghna
                    }
    				//Meghna--Verify Response XML//
                    break;
                }
                
                
                }
            }
            
            if (!flatfile_found)
            {
            	report_msg = report_msg + ("FAILED: Flat File not found");
				//report_status = "FAIL";
            }
            
            //Meghna---Write Final message and status to report//
            if(webDriver.getReport().getMessage() == null || webDriver.getReport().getMessage() == "")
            {
            	webDriver.getReport().setMessage(report_msg);
            }
            
			if(report_status.contains("FAIL"))
			{
				webDriver.getReport().setStatus("FAIL");
			}
			else
			{
				webDriver.getReport().setStatus("PASS");
			}
            //Meghna---Write Final message and status to report//
			
			
			//---Archiving------//
             System.out.println("Do you want to archive Files? Y or N");
             //String archive = sc.nextLine();
              
			if (archive.equalsIgnoreCase("Y")) {
				// Archiving files
				archiveFiles af = new archiveFiles();
				af.archive(sftpChannel, copyFrom, extn);
			}
            
            sftpChannel.exit();
            session.disconnect();
          
		} catch (JSchException e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (SftpException e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
	  
	}


}
