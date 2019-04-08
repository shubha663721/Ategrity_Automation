package com.majesco.itaf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Scanner;

import org.apache.log4j.Logger;

public class inboundFileTransferWin {
	private final static Logger log = Logger.getLogger(inboundFileTransferWin.class.getName());
	
	public void copyFilesToRemoteWindows(Scanner sc)
	{
		// Get Input Details for Windows
		
	     System.out.println("Please enter the location of your local machine where file can be found ");
	     String copyFromLocal = sc.nextLine();
	     
	     System.out.println("Please enter the remote location where you want to place your files: ");
	     String copyToRemote = sc.nextLine();
	     
	     System.out.println("Enter the extension of File to which it should be converted: ");
	     String extn = sc.nextLine();
	     
	     System.out.println("Enter the xfl to be executed with Extension: ");
	     String xflFile = sc.nextLine();
	     
	     //Convert xml to flat 
		 System.out.println("Enter the xml that need to be converted into flat file: ");
		 String xmlFileName = sc.nextLine();
			
	     //xmlToFlatConversion xc = new xmlToFlatConversion();
         //xc.xmlToFlatFileTransferWin(xmlFileName,copyFromLocal,copyToRemote,extn,xflFile);
		 
		  //call to xml converter   
		 xmlToFlatConversion xf = new xmlToFlatConversion();
		 xf.convertXmlToFlat(copyFromLocal,copyToRemote,xmlFileName,extn,xflFile);
        
        try
		 {
		 	InputStream in = new FileInputStream(new File(copyFromLocal+"/"+xmlFileName.replaceAll(".xml",extn)));
		 	OutputStream out = new FileOutputStream(new File(copyToRemote+"/"+xmlFileName.replaceAll(".xml",extn)));

		 	byte[] buffer = new byte[1024];

		 	int len;

		 	while ((len = in.read(buffer)) > 0)
		 	{
		 		out.write(buffer, 0, len);
		 	}


		 	in.close();
		 	out.close();

		 }
        catch( FileNotFoundException e)
		 {
        	log.error(e.getMessage(), e);
		 	e.printStackTrace();
		 }
		 catch (IOException e)
		 {
			 log.error(e.getMessage(), e);
		 	e.printStackTrace();
		 }
       

	}

}
