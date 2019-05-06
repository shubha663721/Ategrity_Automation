package com.majesco.itaf.util;

import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.majesco.itaf.main.Config;

public class inboundFileTransfer {
	
	private final static Logger log = Logger.getLogger(inboundFileTransfer.class.getName());
	
//	public void copyFilesToRemote(Scanner sc)
	public void copyFilesToRemote(String copyFromLocal,String copyToRemote, String xmlFileName, String extn, String xflFile)
	{
		//Input Details For Linux
			
		 /*System.out.println("Please enter the hostname of the server ");
	     String hostName = sc.nextLine();*/
		String hostName = Config.flatFileHostName;
	     
	    /* System.out.println("Please enter your username: ");
	     String userName = sc.nextLine();*/
		
		String userName = Config.flatFileUserName;
	     
	     /*System.out.println("Please enter your password: ");
	     String password = sc.nextLine();*/
		
		String password = Config.flatFilePassword;
		int port = Integer.parseInt(Config.flatFilePort);
	     
	     /*System.out.println("Please enter the location where your files can be found: ");
	     String copyFromLocal = sc.nextLine();*/
		
	     
	    /* System.out.println("Please enter the location where you want to place your files: ");
	     String copyToRemote = sc.nextLine();*/
		
	     
	     /*System.out.println("Enter the extension of File you need to copy: ");
	     String extn = sc.nextLine();*/
		
	     
	    /* System.out.println("Enter the xfl to be executed with Extension: ");
	     String xflFile = sc.nextLine();*/
		

		 JSch jsch = new JSch();
		
		 Session session = null;
	     System.out.println("Trying to connect.....");
	     
	     try
		    {
	            //session = jsch.getSession(userName, hostName, 22);
	            session = jsch.getSession(userName, hostName, port);
	            session.setConfig("StrictHostKeyChecking", "no");
	            session.setPassword(password);
	            session.connect(); 
	            
	            Channel channel = session.openChannel("sftp");
	            channel.connect();
	            ChannelSftp sftpChannel = (ChannelSftp) channel;
	            
	           
	            System.out.println("Done !!");
	            
	            //Convert xml to flat
	            
	            /*System.out.println("Enter the xml that need to be converted into flat file: ");
	    		String xmlFileName = sc.nextLine();*/
	    		
	    		sftpChannel.lcd(copyFromLocal);
	            
				//call to xml converter
	    		xmlFileName = xmlFileName + ".xml";
	    		xmlToFlatConversion xf = new xmlToFlatConversion();
				xf.convertXmlToFlat(copyFromLocal,copyToRemote,xmlFileName,extn,xflFile);
				//xf.convertXmlToFlat(copyFromLocal,copyToRemote,xmlFileName,extn,xflFile);
				
				//Copy flat from Local to remote linux server
				sftpChannel.cd(copyToRemote);
				sftpChannel.put(copyFromLocal+"/"+xmlFileName.replaceAll(".xml",extn), copyToRemote);
	            
	    		
	           // xmlToFlatConversion xc = new xmlToFlatConversion();
	           //xc.xmlToFlatFileTransfer(xmlFileName,sftpChannel,copyToRemote,copyFromLocal,extn,xflFile);
	            
	            sftpChannel.exit();
	            session.disconnect();
	          
	        	} 
		    catch (JSchException e) 
		    	{
		    	log.error(e.getMessage(), e);
	            e.printStackTrace();  
		    	} 
		    catch (SftpException e) 
		    	{
		    	log.error(e.getMessage(), e);
	            e.printStackTrace();
		    	}
	}

}
