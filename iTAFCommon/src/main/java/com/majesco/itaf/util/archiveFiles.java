package com.majesco.itaf.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

public class archiveFiles
{
	private final static Logger log = Logger.getLogger(archiveFiles.class.getName());
	//Windows
	public void archive(String copyFrom, String extn) throws IOException
	{
		//Creating an Archive Folder if does not exist
		
		 File archiveFile = new File(copyFrom+"/Archive");
	       
		 if (!archiveFile.exists())
		 {
			 if (archiveFile.mkdir())
			 {
				 System.out.println("Directory is created!");
			 }
			 
			 else
			 {
				 System.out.println("Failed to create directory!");
			 }
	      }
	        
	      File file = new File(copyFrom);
	      File[] listOfFiles = file.listFiles();
	      
	      
	      for (int i = 0; i < listOfFiles.length; i++)
	      
	      {
	    	  //checking for the extn provided by user
	    	  Boolean b = listOfFiles[i].getName().endsWith(extn);
	    	  
	    	  if(b == true)
	    	  {
		    	//String fileName = listOfFiles[i].getName();
		    		 	
		    	//using path to delete files
		    	String path = listOfFiles[i].getAbsolutePath();
		    		 	
		    	Path filePath = Paths.get(path);
		    	 
		    	try
		    	{
		    		InputStream in = new FileInputStream(new File(copyFrom+"/"+listOfFiles[i].getName()));
		    		OutputStream out = new FileOutputStream(new File(archiveFile+"/"+listOfFiles[i].getName()));
		     
		    		byte[] buffer = new byte[1024];
		     
		    		int len;
		    		 			
		    		 //copying files int archive folder
		    		 			
		    		 while ((len = in.read(buffer)) > 0)
		    		 {
		    		 	out.write(buffer, 0, len);
		    		 }
		    		 				
		    		 in.close();
		    		 out.close();
		    		 				
		    		 //Deleting Files after Archiving
		    		 Files.delete(filePath);
		    		 				
	      
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
	      
	      System.out.println("Archive Complete");
	      
	  

	}
	
	//Linux
	public void archive (ChannelSftp sftpChannel,String copyFrom, String extn) throws SftpException
	{
		//Creating an Archive Folder if does not exist
		
		sftpChannel.cd(copyFrom);
		String currentDirectory=sftpChannel.pwd();
		String dir="Archive";
		System.out.println(currentDirectory);
		SftpATTRS attrs=null;
		try 
		{
			attrs = sftpChannel.stat(currentDirectory+"/"+dir);
		} 
		catch (Exception e)
		{
				log.error(e.getMessage(), e);
		    	System.out.println(currentDirectory+"/"+dir+" not found");
		}
		if (attrs != null)
		{
		    System.out.println("Directory exists IsDir="+attrs.isDir());
		} else 
		{
		    System.out.println("Creating dir "+dir);
		    System.out.println(currentDirectory+"/"+dir);
		    sftpChannel.mkdir(dir);
		}
        
		String path = currentDirectory+"/"+dir;
		System.out.println(path);
		
		//copy file into archive
		
        try
        {
        	sftpChannel.cd(copyFrom);
        	Vector filelist = sftpChannel.ls(copyFrom);
        
        	for(int i=0; i<filelist.size();i++)
        	{
        		LsEntry entry = (LsEntry) filelist.get(i);
        		Boolean b = entry.getFilename().endsWith(extn);
            
        		if(b == true)
        		{	
            
        			String fileName = entry.getFilename();
        			
        			//System.out.println(path);
        			System.out.println(fileName);
        			//System.out.println(copyFrom+"/"+entry.getFilename());
        			
        			sftpChannel.rename(copyFrom+"/"+fileName, path+"/"+fileName);
        			//sftpChannel.get(fileName, path);   
        			
        			
            
        		}
        	}
        	
        	System.out.println("Archive Complete");
        }
        
         
        catch (SftpException e) 
    		{
        		log.error(e.getMessage(), e);
        		e.printStackTrace();
    		}
	}
	

	
}
