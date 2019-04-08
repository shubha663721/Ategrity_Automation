package com.majesco.itaf.main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


 

 

public class SuiteDriver {
	
	final static Logger log = Logger.getLogger(SuiteDriver.class.getName());

	private static ITAFWebDriver webDriver = ITAFWebDriver.getInstance();

    public static void start()

    {
    	try
    	{	
    		 int suiteLastRow;
    		 String findText,replaceText;
    		 
    		 /*String fileUnqNum = "C:\\Users\\BaseBillingAutomatio\\Desktop\\Suite\\UniqueNumber.xls";
    		 String fileSuite = "C:\\Users\\BaseBillingAutomatio\\Desktop\\Suite\\Suite.xlsx";
    		 String inputPath = "C:\\Users\\BaseBillingAutomatio\\Desktop\\Suite\\InputMaster";*/
    		 
    		 String fileUnqNum = Config.policyUniqueNoPath;
    		 String fileSuite = Config.suiteDataPath;
    		 String inputPath = Config.inputDataFilePath;
    		 String mainCPath = Config.controllerFilePath;
    		 
    		 
    		 
    		 getDataFromPAS(fileUnqNum,fileSuite); //Meghna-Getting Data from PAS Unique Number in Billing Format
    		 
    		 
    		 //Find and Replace keywords from Suite.xlsx//
    		 File suiteFile = new File(fileSuite);
    		 FileInputStream suiteInp = new FileInputStream(suiteFile);
    		 Workbook wb = WorkbookFactory.create(suiteInp);
    		 Sheet suiteSht = wb.getSheet("DataSheet");
    		 
    		 
    		 /*Row sRow = suiteSht.getRow(0);
    		 suiteLastRow = suiteSht.getLastRowNum();
    		 
    		 File targetPath = new File(inputPath);
	    	
    		 for(int k = 1;k<=suiteLastRow;k++)
    		 {	
	    		
	    		Row sRow1 = suiteSht.getRow(k);
	    		
	    		findText=  sRow1.getCell(0).toString();
    	        replaceText=sRow1.getCell(1).toString();
    	        
    	        File[] listOfFiles = targetPath.listFiles();
    	        traverseFileFolders(listOfFiles,findText,replaceText);
    	        
    	        System.out.println("Replaced " + findText + "with " +replaceText );
    		 }*/
    		 
    		 
    		 startFolder(inputPath,suiteSht);  //Meghna--Replace in all Input Folders and files
    		 
    		 startFolder(mainCPath,suiteSht);  //Meghna--Replace in MainController
    		 
	    	
    	}
       
       catch(Exception e)
       {
    	   log.error(e.getMessage(), e);
    	   e.printStackTrace();
       }
    }
    
    public static void startFolder(String inputPath, Sheet suiteSht)
    {
    	int suiteLastRow;
		String findText,replaceText;
		 
    	Row sRow = suiteSht.getRow(0);
		suiteLastRow = suiteSht.getLastRowNum();
    	File targetPath = new File(inputPath);
    	
		 for(int k = 1;k<=suiteLastRow;k++)
		 {	
   		
	   		Row sRow1 = suiteSht.getRow(k);
	   		
	   		findText=  sRow1.getCell(1).toString();
	        replaceText=sRow1.getCell(2).toString();
	        
	        
	        if(targetPath.isDirectory())
	        {
	        	File[] listOfFiles = targetPath.listFiles();
		        traverseFileFolders(listOfFiles,findText,replaceText);
	        }
	        else if(targetPath.isFile())
	        {
	        	 if(targetPath.getName().endsWith(".xml") || targetPath.getName().endsWith(".csv"))
			     {
			    	 replaceInXML(targetPath,findText,replaceText);
			     }
			     else if(targetPath.getName().endsWith(".xlsx") || targetPath.getName().endsWith(".xls"))
			     {
			    	 replaceInExcel(targetPath,findText,replaceText);
			     }
	        }
	        
	        
	        System.out.println("Replaced " + findText + "with " +replaceText );
		 }
    }

       
   public static void traverseFileFolders(File[] listOfFiles,String findText,String replaceText)
   {
	   
	   for(File file : listOfFiles)
       {    
		 if(file.isDirectory())
		 {
	        File[] listOfFilesInSubDir = file.listFiles();
	        traverseFileFolders(listOfFilesInSubDir,findText,replaceText);
		 }
		
		 else if(file.isFile())
		 {
		     if(file.getName().endsWith(".xml") || file.getName().endsWith(".csv"))
		     {
		    	 replaceInXML(file,findText,replaceText);
		     }
		     else if(file.getName().endsWith(".xlsx") || file.getName().endsWith(".xls"))
		     {
		    	 replaceInExcel(file,findText,replaceText);
		     }
		 }

       }

   }
       

    public static void replaceInXML(File file,String findText,String replaceText)

    {
    	 BufferedReader reader;
    	 String line = "";
         String oldText = "";

	      try
	      {
	           reader = new BufferedReader(new FileReader(file));
	           
	
	           while ((line = reader.readLine()) != null)
	           {
	
	        	   oldText += line + "\r\n";
	           }
	
	           reader.close();
	
	           //System.out.println(file.getName());
	
	           String newText = oldText.replace(findText,replaceText);
	
	           FileWriter writer = new FileWriter(file);
	           writer.write(newText);
	           writer.close();
	
	      } 
	      catch (FileNotFoundException e) 
	      {
	    	  log.error(e.getMessage(), e);
	      } 
	      catch (IOException e) 
	      {
	    	  log.error(e.getMessage(), e);
	      }

    }

    public static void replaceInExcel(File file,String findText,String replaceText) 
    {	
    	int lastRow,lastCol;
        String curHeader;
        int[] arrRefCols = new int[500];
        
        
        try 
        {
          FileInputStream finp = new FileInputStream(file);
          Workbook wb = WorkbookFactory.create(finp);
          Sheet sht;
          
          if(file.getName().contains("MainController.xlsx"))
          {
        	  sht = wb.getSheet("MainControlSheet");
          }
          else
          {
        	  sht = wb.getSheet("Values");
          }
          
         
           Row row1 = sht.getRow(0);

           lastRow = sht.getLastRowNum();
           lastCol = row1.getPhysicalNumberOfCells();

           int j = 0;

           System.out.println(file.getName());

           /*for(int i=0;i<lastCol;i++)
           {
	          curHeader = row1.getCell(i).toString();
	
	          if(curHeader.equals(""))
	          {
	                break;
	          }
	
	          if(curHeader.contains("[Ref]"))
	          {
	        	  System.out.println(curHeader);
	              arrRefCols[j] = i;
	              j = j + 1;
	          }

           }*/

           replaceInRefColumns(file,wb,sht,lastCol,lastRow,findText,replaceText);

        }

        catch (IOException ioe)
        {
        	log.error(ioe.getMessage(), ioe);
        }

        catch (Exception e)
        {
        	log.error(e.getMessage(), e);
        }

    }


    /*public static void replaceInRefColumnsOLD(File file,Workbook wb,Sheet sht,int[] arrRefCols,int lastRowNum,String findText,String replaceText)
    {

       String oldText,newText;
       try 
       {

         FileOutputStream fout = new FileOutputStream(file);
         DataFormatter fmt = new DataFormatter();

          for(int j = 0; j<=arrRefCols.length;j++)
          {     
            if(j>10)
             {
                    break;
             }

             for(int i = 1;i<=lastRowNum;i++)
             {
	            Row rows = sht.getRow(i);
	            Cell curCell = rows.getCell(arrRefCols[j]);
	            
	            if(curCell != null)
	            {	
	            	
	            	int type = curCell.getCellType();
					switch(type)
					{
					case HSSFCell.CELL_TYPE_BLANK:
						oldText = "";
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						oldText = fmt.formatCellValue(curCell);								
						break;
					case HSSFCell.CELL_TYPE_STRING:
						oldText = curCell.getStringCellValue();
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						oldText = Boolean.toString(curCell.getBooleanCellValue());
						break;
					case HSSFCell.CELL_TYPE_ERROR:
						oldText = "error";
						break;
					case HSSFCell.CELL_TYPE_FORMULA:
						oldText = curCell.getCellFormula();
						break;
					}
					
                  oldText = curCell.getStringCellValue();
                  
                  
                  
                  newText = oldText.replace(findText,replaceText);
                  curCell.setCellValue(newText);
	            }   
             }
          }

          wb.write(fout);
          fout.flush();
          fout.close();

       } 
       catch (FileNotFoundException e)
       {
         // TODO Auto-generated catch block
         e.printStackTrace();
        } 
       catch (IOException e)
       {
	     // TODO Auto-generated catch block
	     e.printStackTrace();

        }
       
    }*/
    
    public static void replaceInRefColumns(File file,Workbook wb,Sheet sht,int lastCol,int lastRowNum,String findText,String replaceText)
    {

       String oldText,newText;
       oldText = "";
       
       try 
       {

         FileOutputStream fout = new FileOutputStream(file);
         DataFormatter fmt = new DataFormatter();

          for(int j = 0; j<=lastCol;j++)
          {     
            
             for(int i = 1;i<=lastRowNum;i++)
             {
	            Row rows = sht.getRow(i);
	            Cell curCell = rows.getCell(j);
	            if(curCell != null)
	            {
	            	
	            	
	            	int type = curCell.getCellType();
					switch(type)
					{
					case HSSFCell.CELL_TYPE_BLANK:
						oldText = "";
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						oldText = fmt.formatCellValue(curCell);								
						break;
					case HSSFCell.CELL_TYPE_STRING:
						oldText = curCell.getStringCellValue();
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						oldText = Boolean.toString(curCell.getBooleanCellValue());
						break;
					case HSSFCell.CELL_TYPE_ERROR:
						oldText = "error";
						break;
					case HSSFCell.CELL_TYPE_FORMULA:
						oldText = curCell.getCellFormula();
						break;
					
					}
					
                  //oldText = curCell.getStringCellValue();
                  newText = oldText.replace(findText,replaceText);
                  curCell.setCellValue(newText);
	            }   
             }
          }

          wb.write(fout);
          fout.flush();
          fout.close();

       } 
       catch (FileNotFoundException e)
       {
    	   log.error(e.getMessage(), e);
        } 
       catch (IOException e)
       {
    	   log.error(e.getMessage(), e);
        }
    }
    
    public static void getDataFromPAS(String fileUnqNum,String fileSuite)
    {
    	File unqFile = new File(fileUnqNum);
    	File suiteFile = new File(fileSuite);
    	String curTcId,curBilling,curPAS;
    	try 
    	{	
    		//***************Unique Number File****************************************//
			FileInputStream fUnq = new FileInputStream(unqFile);
			Workbook wbUnq = WorkbookFactory.create(fUnq);
			Sheet shtUnq = wbUnq.getSheet("DataSheet");
			Row headerRowUnq = shtUnq.getRow(0);
			
			int lastRowUnq = shtUnq.getLastRowNum();
			int lastColunq = headerRowUnq.getPhysicalNumberOfCells();
			//*******************************************************//
			
			//*****************Suite File**************************************//
			FileInputStream fSuite = new FileInputStream(suiteFile);
			Workbook wbSuite = WorkbookFactory.create(fSuite);
			Sheet shtsuite = wbSuite.getSheet("DataSheet");
			//Row headerRowUnq = shtsuite.getRow(0);
			
			int lastRowSuite = shtsuite.getLastRowNum()+1;		
			
			
			if(lastRowSuite>1)
					
			{
				FileOutputStream foutSuite = new FileOutputStream(suiteFile);
				Row sRow;
				for(int k=1;k<lastRowSuite;k++)
				{
					sRow = shtsuite.getRow(k);
					
					if(sRow!=null)
					{
						shtsuite.removeRow(sRow);
					}
					
				}
				
				wbSuite.write(foutSuite);
				foutSuite.close();
				foutSuite.flush();
				
			}
			
			FileOutputStream foutSuite = new FileOutputStream(suiteFile);
			lastRowSuite = shtsuite.getLastRowNum()+1;
			
			
			//int lastColunq = headerRowUnq.getPhysicalNumberOfCells();
			//*******************************************************//
			
			
			for(int i=1;i<=lastRowUnq;i++)
			{
				Row curRowUnq = shtUnq.getRow(i);
				curTcId = curRowUnq.getCell(0).toString();
				
				for(int col=1;col<lastColunq;col++)
				{
					Row curRowSuite = shtsuite.createRow(lastRowSuite);
					
					curBilling = curTcId + (headerRowUnq.getCell(col)).toString();
					
					if(curRowUnq.getCell(col)!=null)
					{
						curPAS= curRowUnq.getCell(col).toString();
					}
					else
					{
						curPAS = "";
					}
					
					System.out.println(curTcId +"----" + curBilling +"----" + curPAS);
					
					curRowSuite.createCell(0).setCellValue(curTcId);
					curRowSuite.createCell(1).setCellValue(curBilling);
					curRowSuite.createCell(2).setCellValue(curPAS);
					lastRowSuite =lastRowSuite+1;	
				}
			}
			
			wbSuite.write(foutSuite);
			foutSuite.close();
			foutSuite.flush();
			
		} 
    	    	
    	/*catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EncryptedDocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			iTAFSeleniumWeb.WebDriver.report.setStrMessage(e.getLocalizedMessage());
			iTAFSeleniumWeb.WebDriver.report.setStrStatus("FAIL");
		}*/
    	
    	catch (Exception e) {
    		log.error(e.getMessage(), e);
			webDriver.getReport().setMessage(e.getLocalizedMessage());
			webDriver.getReport().setStatus("FAIL");
		}
    	
    }

}

 