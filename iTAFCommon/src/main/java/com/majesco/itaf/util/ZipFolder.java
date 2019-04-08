package com.majesco.itaf.util;

//import java.io.FileOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.Logger;

public class ZipFolder {
    
	final static Logger log = Logger.getLogger(ZipFolder.class.getName());
    static List<String> filesListInDir = new ArrayList<String>();

       public static void zipDirectory(File dir, String zipDirName,String findZipfolder) {
        try {
        	
        	FindZipfile(findZipfolder);
            populateFilesList(dir);
            //now zip files one by one
            //create ZipOutputStream to write to the zip file
            
            FileOutputStream fos = new FileOutputStream(zipDirName);
            ZipOutputStream zos = new ZipOutputStream(fos);
            for(String filePath : filesListInDir){
                System.out.println("Zipping "+filePath);
                //for ZipEntry we need to keep only relative file path, so we used substring on absolute path
                ZipEntry ze = new ZipEntry(filePath.substring(dir.getAbsolutePath().length()+1, filePath.length()));
                zos.putNextEntry(ze);
                //read the file and write to ZipOutputStream
                FileInputStream fis = new FileInputStream(filePath);
                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                zos.closeEntry();
                fis.close();
            }
            zos.close();
            fos.close();
        } catch (IOException e) {
        	log.error(e.getMessage(), e);
            e.printStackTrace();
        }
    }
    
    /**
     * This method populates all the files in a directory to a List
     * @param dir
     * @throws IOException
     */
    public static void populateFilesList(File dir) throws IOException {
        File[] files = dir.listFiles();
        for(File file : files){
            if(file.isFile()) filesListInDir.add(file.getAbsolutePath());
            else populateFilesList(file);
        }
    }
    
    public static void FindZipfile(String zipDirName)
    {
    	
    	 
    	  String files;
    	  File folder = new File(zipDirName);
    	  File[] listOfFiles = folder.listFiles(); 

    	  for (int i = 0; i < listOfFiles.length; i++) 
    	  {

    	   if (listOfFiles[i].isFile()) 
    	   {
    	   files = listOfFiles[i].getName();
    	   System.out.println("files : "+files);
    	       if ((files.endsWith(".rar") || files.endsWith(".zip")) && (files.equalsIgnoreCase("Dashboard_Reports.zip")))
    	       {
    	    	   listOfFiles[i].delete();
    	           System.out.println(files);
    	          
    	        }
    	     }
    	  }
    }
    


}