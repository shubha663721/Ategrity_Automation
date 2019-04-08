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


public class outboundTransferFileWin {
	
	private final static Logger log = Logger.getLogger(reportToFlatConversion.class.getName());
	
	public void copyFilesWindows(Scanner sc) throws IOException
	{
	     // Get Input Details for Windows
		
	     System.out.println("Please enter the location where your files can be found: ");
	     String copyFrom = sc.nextLine();
	     
	     System.out.println("Please enter the location where you want to place your files: ");
	     String copyTo = sc.nextLine();
	     
	     System.out.println("Enter the extension of File you need to copy: ");
	     String extn = sc.nextLine();
	     
	     System.out.println("Enter the xfl to be executed with Extension: ");
	     String xflFile = sc.nextLine();
	     
	     //Copy Files from Windows remote server to local machines
	     
	     File remoteFile = new File(copyFrom);
	     File[] listOfFiles = remoteFile.listFiles();
	     
	     for (int i = 0; i < listOfFiles.length; i++) 
	     {
	         
	    	 Boolean b = listOfFiles[i].getName().endsWith(extn);
	    	 
	    	 
	    	 if(b == true)
	    	 {
	    		 String fileName = listOfFiles[i].getName();
	    	
	    		 System.out.println(listOfFiles[i].getName());
	    	 
	    		 try
	    		 {
	    		 	InputStream in = new FileInputStream(new File(copyFrom+"/"+listOfFiles[i].getName()));
	    		 	OutputStream out = new FileOutputStream(new File(copyTo+"/"+listOfFiles[i].getName()));
	     
	    		 	byte[] buffer = new byte[1024];
	     
	    		 	int len;
	     
	    		 	while ((len = in.read(buffer)) > 0)
	    		 	{
	    		 		out.write(buffer, 0, len);
	    		 	}
	     
	     
	    		 	in.close();
	    		 	out.close();
	    		 				
	    		 	//Flat to Xml conversion
	    		 				
	    		 	flatToXmlConversion fx = new flatToXmlConversion();
	    		 	fx.convertFlatToXml(copyTo,fileName,extn,xflFile);
	     
	     
	    		 }
	    		 catch( FileNotFoundException e)
	    		 {
	    			 log.error(e.getMessage(), e);
	    		 }
	    		 catch (IOException e)
	    		 {
	    			 log.error(e.getMessage(), e);
	    		 }
	    	 }
	     }
	     
	     //archiving files
	     System.out.println("Do you want to archive Files? Y or N");
         String archive = sc.nextLine();
         if (archive.equalsIgnoreCase("Y"))
         {
        	 archiveFiles af = new archiveFiles();
        	 af.archive(copyFrom, extn);
         }
	}
	

}
