// Created by Bhaskar Polipilli for Billing Product Related Migration Code changes 

package com.majesco.itaf.util;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.Select;
//Meghna
//XX
//
//Mandar

import com.majesco.itaf.main.Automation;
import com.majesco.itaf.main.Config;
import com.majesco.itaf.main.ITAFWebDriver;
import com.majesco.itaf.main.MainController;
import com.majesco.itaf.main.ObjectFactory;
import com.majesco.itaf.main.WebHelper;
import com.majesco.itaf.main.WebHelperUtil;
import com.majesco.itaf.recovery.StartRecovery;
import com.majesco.itaf.webservice.WebService;

public class BillingProduct 
{
	
	private final static Logger log = Logger.getLogger(WebService.class.getName());
	public static LinkedHashMap<String, Object> ColumnheaderIndex = new LinkedHashMap<String, Object>();
	public static LinkedHashMap<String, Object> Operatectrlvalues = new LinkedHashMap<String, Object>();
	public static LinkedHashMap<String, Object> Searchctrlvalues = new LinkedHashMap<String, Object>();	
	public static LinkedHashMap<String, Object> OperateControlType = new LinkedHashMap<String, Object>();
	//public static HSSFDataFormat TIformat = null;
	public static XSSFDataFormat TIformat = null;
	//public static void findAction(WebElement tableFound,String controlName,String logicalName,HSSFRow rowValues,HashMap<String,Object> valuesHeader)  throws Exception
	
	private static ITAFWebDriver webDriver = ITAFWebDriver.getInstance();
	private static MainController controller = ObjectFactory.getMainController();
	
	public static void findAction(WebElement tableFound,String controlName,String logicalName,Row rowValues,HashMap<String,Integer> valuesHeader)  throws Exception
	{
		try
		{
			ColumnheaderIndex.clear();
			Operatectrlvalues.clear();
			Searchctrlvalues.clear();
			OperateControlType.clear();
			String Attempt = "";// Deduction -//***For GB - 24/07/2018***//START
			System.out.println("logicalName is : " + logicalName);
			// ***END***//
		log.info("logicalName is : " + logicalName);
		
		String currentHeader = null;
		
		String finalXpathTemp;//Meghna
		int tempPosition;//Meghna
		List<WebElement> table_Th =  tableFound.findElements(By.tagName("th"));
		//List<WebElement> table_Th1 =  tableFound.findElements(By.xpath("//tr/td"));
		
		
		// to scroll table to the left
        WebElement firstelement = table_Th.get(0);
        ((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView(true);", firstelement);
        // to scroll table to the left

		
		for(int i=0;i<table_Th.size();i++)
		{			
			currentHeader = (table_Th.get(i).getText()).toUpperCase(); //Meghna - Updated to handle table header change upgrade
			if(ColumnheaderIndex.containsKey(currentHeader)==false)
				{
					ColumnheaderIndex.put(currentHeader, i+1);
					
					/*if(currentHeader.equalsIgnoreCase("net") && MainController.controllerTransactionType.toString().equalsIgnoreCase("CreateAccountCurrent"))
					{
						ColumnheaderIndex.put(currentHeader, 11);
					}
					//AgencyPaymentAllocation
					if(currentHeader.equalsIgnoreCase("net") && MainController.controllerTransactionType.toString().equalsIgnoreCase("AgencyPaymentAllocation"))
					{
						ColumnheaderIndex.put(currentHeader, 12);
					}*/
				}
		}		
		log.info("Application column headers and their indexes are : " + ColumnheaderIndex);
				
		String[] ExcelLogicalName = logicalName.split("\\|");
		String[] excelHeader = null;
		String[] actualHeader = null;
		String Sctrl = "";
		//int a = ColumnheaderIndex.get(currentHeader)		
		
			excelHeader = ExcelLogicalName[0].split("\\:");
			actualHeader = ExcelLogicalName[1].split("\\:");

			// ***For GB - 24/07/2018***Deduction -- START***//
			int arrlen = ExcelLogicalName.length;
			if (((arrlen - 2) % 3) == 0) {
			} else {
				String AttemptColName = ExcelLogicalName[arrlen - 1];
				Cell ctrlValuecell111 = rowValues.getCell(Integer
						.parseInt(valuesHeader.get(AttemptColName).toString()));
				DataFormatter fmt = new DataFormatter();
				if (ctrlValuecell111 == null) {
					Attempt = "";
				} else {
					int type = ctrlValuecell111.getCellType();
					switch (type) {
					case HSSFCell.CELL_TYPE_BLANK:
						Attempt = "1";
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						Attempt = fmt.formatCellValue(ctrlValuecell111);
						break;
					case HSSFCell.CELL_TYPE_STRING:
						Attempt = ctrlValuecell111.getStringCellValue();
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						Attempt = Boolean.toString(ctrlValuecell111
								.getBooleanCellValue());
						break;
					case HSSFCell.CELL_TYPE_ERROR:
						Attempt = "error";
						break;
					case HSSFCell.CELL_TYPE_FORMULA:
						Attempt = ctrlValuecell111.getCellFormula();
						break;
					}
				}

				System.out.println(" Attempt no " + Attempt);
			}
			// ****************Deduction -- End ******************//

			int actualHeaderLen = excelHeader.length;
			
			String ctrlValue = null;
			for(int i =0;i<actualHeaderLen;i++)
			{
				//HSSFCell ctrlValuecell = rowValues.getCell(Integer.parseInt(valuesHeader.get(excelHeader[i]).toString()));
				Cell ctrlValuecell = rowValues.getCell(valuesHeader.get(excelHeader[i]));
				DataFormatter fmt = new DataFormatter();
				if(ctrlValuecell == null)
				{
					ctrlValue = "";
				}
				else
				{
					int type = ctrlValuecell.getCellType();
					switch(type)
					{
					case HSSFCell.CELL_TYPE_BLANK:
						ctrlValue = "";
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						ctrlValue = fmt.formatCellValue(ctrlValuecell);								
						break;
					case HSSFCell.CELL_TYPE_STRING:
						ctrlValue = ctrlValuecell.getStringCellValue();
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						ctrlValue = Boolean.toString(ctrlValuecell.getBooleanCellValue());
						break;
					case HSSFCell.CELL_TYPE_ERROR:
						ctrlValue = "error";
						break;
					case HSSFCell.CELL_TYPE_FORMULA:
						ctrlValue = ctrlValuecell.getCellFormula();
						break;
					}
				}
				Searchctrlvalues.put(actualHeader[i],ctrlValue);
				Sctrl = Sctrl + ctrlValue;
			}
			
			if(Sctrl.equals(""))
			{
				log.info("As per value sheet, there is nothing to be searched on application and hence coming out of FIND function");
				return;
			}
			log.info("Concatenated string in Values : " + Sctrl);			
			log.info("Values to be searched on application table are : " + Searchctrlvalues);
			
			int logicalnameLen = ExcelLogicalName.length;
			
			for(int i=2;i<logicalnameLen;i+=3)   // for getting target columns and values
			{
				
				//HSSFCell ctrlValuecell = rowValues.getCell(Integer.parseInt(valuesHeader.get(ExcelLogicalName[i+2]).toString()));
				//Cell ctrlValuecell = rowValues.getCell(Integer.parseInt(valuesHeader.get(ExcelLogicalName[i+2]).toString()));
				Cell ctrlValuecell = rowValues.getCell(valuesHeader.get(ExcelLogicalName[i+2]));
				DataFormatter fmt = new DataFormatter();
				if(ctrlValuecell == null)
				{
					ctrlValue = "";
				}
				else
				{
					int type = ctrlValuecell.getCellType();
					switch(type)
					{
					case HSSFCell.CELL_TYPE_BLANK:
						ctrlValue = "";
						break;
					case HSSFCell.CELL_TYPE_NUMERIC:
						ctrlValue = fmt.formatCellValue(ctrlValuecell);								
						break;
					case HSSFCell.CELL_TYPE_STRING:
						ctrlValue = ctrlValuecell.getStringCellValue();
						break;
					case HSSFCell.CELL_TYPE_BOOLEAN:
						ctrlValue = Boolean.toString(ctrlValuecell.getBooleanCellValue());
						break;
					case HSSFCell.CELL_TYPE_ERROR:
						ctrlValue = "error";
						break;
					case HSSFCell.CELL_TYPE_FORMULA:
						ctrlValue = ctrlValuecell.getCellFormula();
						break;
					}
				}
				
				Operatectrlvalues.put(ExcelLogicalName[i],ctrlValue);
				OperateControlType.put(ExcelLogicalName[i], ExcelLogicalName[i+1]);
								
			}			
			log.info("Target column ctrlvalues are : " + Operatectrlvalues);
			log.info("Target column ctrltypes are : " + OperateControlType);
			
			// for creating XPath			
			String finalXpath = controlName+"//tr";
			Set<String> keys = Searchctrlvalues.keySet();
			int counter = 1;
			for(String key : keys )  //For loop 
			{		
				//int position = (int) ColumnheaderIndex.get(key);
				int positionint;
				String position;
				
				try{
					positionint = (Integer.parseInt(key));
					position = ""+positionint;
				}
				catch(Exception pe)
				{
					//position = (String) ColumnheaderIndex.get(key).toString().trim();
					position = (String) ColumnheaderIndex.get((key).toUpperCase()).toString().trim(); //Meghna-Updated to handle table header change upgrade
					//log.error(pe.getMessage(), pe);
				}		
				
				//String ctrlvalue = (String) Searchctrlvalues.get(key).toString().trim();
				String ctrlvalue = (String) Searchctrlvalues.get(key).toString();
				//String ctrlvalue = (String) Searchctrlvalues.get((key).toUpperCase()).toString(); //Meghna-Updated to handle table header change upgrade
				log.info("Search key , position , value is : " + key + ": " + position + ": " + ctrlvalue);
				
				//Added for ModifyAccCurrent Transaction ----@title property--- Meghna
				String datePattern = "\\d{4}-\\d{2}-\\d{2}";
				if(!ctrlvalue.equalsIgnoreCase(""))
				
				{
					//Added for ModifyAccCurrent Transaction ----@title property--- Meghna
					if(ctrlvalue.matches(datePattern))
                    {
                           if(counter ==1)
                           {
                                  finalXpath = finalXpath + "/td[position()="+position+" and (.//@title='"+(ctrlvalue.trim())+"' or .//text()='"+(ctrlvalue.trim())+"')]";
                                  //finalXpath = finalXpath + "/td[position()="+position+" and (.//@value='"+(ctrlvalue)+"' or .//text()='"+(ctrlvalue)+"')]";
                           }
                           
							else
                           {
                                  finalXpath = finalXpath + "/../td[position()="+position+" and (.//@title='"+(ctrlvalue.trim())+"' or .//text()='"+(ctrlvalue.trim())+"')]";
                                  //finalXpath = finalXpath + "/../td[position()="+position+" and (.//@value='"+(ctrlvalue)+"' or .//text()='"+(ctrlvalue)+"')]";
                           }             
                    }
					//Added for ModifyAccCurrent Transaction ----@title property--- Meghna
							
					else if(!ctrlvalue.equalsIgnoreCase("BLANK") )
						{
							if(counter ==1)
								{
									finalXpath = finalXpath + "/td[position()="+position+" and (.//@value='"+(ctrlvalue.trim())+"' or .//text()='"+(ctrlvalue.trim())+"')]";
									//finalXpath = finalXpath + "/td[position()="+position+" and (.//@value='"+(ctrlvalue)+"' or .//text()='"+(ctrlvalue)+"')]";
								}
								
							else
								{
									finalXpath = finalXpath + "/../td[position()="+position+" and (.//@value='"+(ctrlvalue.trim())+"' or .//text()='"+(ctrlvalue.trim())+"')]";
									//finalXpath = finalXpath + "/../td[position()="+position+" and (.//@value='"+(ctrlvalue)+"' or .//text()='"+(ctrlvalue)+"')]";
								}				
						}
					else
						{
							if(counter ==1)
								{
									//finalXpath = finalXpath + "/td[position()="+position+" and (.//@value='N' or not(@title))]";
									finalXpath = finalXpath + "/td[position()="+position+" and (.//@value='N' or  @title='')]";
								}
							else
								{
									finalXpath = finalXpath + "/../td[position()="+position+" and (.//@value='N' or  @title='')]";
								}
						}
					counter++;
				}							
			}
			
			log.info("finalXpath is : " +finalXpath);
			finalXpathTemp = finalXpath;						//Meghna
			
			//finalXpath = "//tr/td[position()=2 and (.//@value='PO10121128' or .//text()='PO10121128')]/../td[position()=3 and (.//@value='01/05/2012-01/05/2013' or .//text()='01/05/2012-01/05/2013')]/../td[position()=5 and (.//@value='58271' or .//text()='58271')]";
			//finalXpath = finalXpath+"/../td[position()="+columnFound+"]";
			
			// for operating on Traget column
			Set<String> Operatekeys = Operatectrlvalues.keySet();
			String targetXpath = "";
			
			for(String Oprkey : Operatekeys )
			{
				String controlType = (String)OperateControlType.get(Oprkey).toString();
				String controlValue = (String)Operatectrlvalues.get(Oprkey).toString();
				
				int positionint;
				String position;
				
				try{
					positionint = (Integer.parseInt(Oprkey));
					position = ""+positionint;
				}
				catch(Exception pe)
				{
					//position = (String) ColumnheaderIndex.get(Oprkey).toString().trim();
					position = (String) ColumnheaderIndex.get((Oprkey).toUpperCase()).toString().trim(); //Meghna-Updated to handle table header change upgrade
					//log.error(pe.getMessage(), pe);
				}
				
				/*if(MainController.controllerTransactionType.toString().equalsIgnoreCase("WriteOffUndoWriteOff")) // To handle Gross column in WriteOff transaction
				{
					if(Oprkey.toString().trim().startsWith("Gross") && Oprkey.toString().trim().endsWith("Gross"))
					{position = "13";}
				}							
				
				if(MainController.controllerTransactionType.toString().equalsIgnoreCase("CreateAccountCurrent"))
				{
					if(Oprkey.toString().trim().startsWith("Gross") && Oprkey.toString().trim().endsWith("Gross"))
					{position = "13";}
					if(Oprkey.toString().trim().startsWith("Comm. %") && Oprkey.toString().trim().endsWith("Comm. %"))
					{position = "14";}					
				}
				
				if(MainController.controllerTransactionType.toString().equalsIgnoreCase("AgencyPaymentAllocation"))
				{
					if(Oprkey.toString().trim().equalsIgnoreCase(""))
					{position = "13";}
					if(Oprkey.toString().trim().startsWith("Gross") && Oprkey.toString().trim().endsWith("Gross"))
					{position = "14";}
					if(Oprkey.toString().trim().startsWith("Comm. %") && Oprkey.toString().trim().endsWith("Comm. %"))
					{position = "15";}					
				}*/
				
				tempPosition = (Integer.parseInt(position))+1;//Meghna		
				log.info("Target key , position , value , type : " + Oprkey + ": " + position + ": " + controlValue + ": " + controlType);
								
				targetXpath = finalXpath+"/../td[position()="+position+"]";
				//System.out.println("targetXpath is : " + targetXpath);
			
				if(controlType.equalsIgnoreCase("WebEdit"))  //operate on targeted column
				{						
					String XPath = targetXpath+"//div/div/input";
					log.info("XPath of target WebEdit is : " + XPath);
					WebElement newelement = Automation.driver.findElement(By.xpath(XPath));
					
					//Meghna--To clear a field in webtable--04/12/2017
					if(controlValue.equalsIgnoreCase("BLANK"))
					{
						newelement.click();
						Thread.sleep(1000);		
						newelement.clear();
					}
					//Meghna--To clear a field in webtable--04/12/2017
					
					else if(controlType!= "" && controlValue!= "")
					{
						//Thread.sleep(1000);
						((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", newelement);
						Thread.sleep(1000);//Mandar for EST_PR issue
						newelement.click();
						Thread.sleep(1000);		
						newelement.clear();
						Thread.sleep(1000);
						//***Meghna
						((JavascriptExecutor)Automation.driver).executeScript("arguments[0].setAttribute('value', '"+controlValue+"')", newelement); //For handling Object level issue 01 June
						newelement.clear();	
						Thread.sleep(1000);
						//***
						newelement.sendKeys(controlValue);
						
						Thread.sleep(1000);
						newelement.click();
						
						newelement.sendKeys(Keys.TAB);
						Thread.sleep(1000);
					}
					
				}
				else if(controlType.equalsIgnoreCase("WebButton"))
				{
					if(controlValue!= "")
					{
					String XPath = targetXpath+"//div/div/button";
					log.info("XPath of target WebButton is : " + XPath);
					WebElement newelement = Automation.driver.findElement(By.xpath(XPath));
					//Thread.sleep(1000);
					((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", newelement);
					Thread.sleep(1000);
					newelement.click();
					}
				}
				else if(controlType.equalsIgnoreCase("WebLink"))
				{				
					if(controlValue!= "")
					{
					
						if (controller.controllerTransactionType.toString().equalsIgnoreCase("ModifyAccountCurrent") && (tempPosition == 12))
						{
							WebElement headers = Automation.driver.findElement(By.xpath(".//*[@id='dgAddTransactionTemp']//*[@title='Transaction']/div"));
							//((JavascriptExecutor)Automation.driver).executeScript("arguments[0].click();", headers);
							Thread.sleep(1000);
							headers.click();
							
							//*****************************//
							
							Boolean hclicked = false;
							
							while(!hclicked)
								{
										try
										{
										
											WebElement headers_asc = Automation.driver.findElement(By.xpath(".//*[contains(@id,'dgAddTransactionTemp')]/tbody/tr[1]/td[3]/div/div/span/span/span/span"));
											String first_value = headers_asc.getAttribute("title");
											
											log.info(first_value);
											
											if(first_value.equals("New Business"))
											{
												hclicked = true;
											}
											else
											{
												headers.click();
												first_value = "";
											}
											
										}
										
										catch(Exception ex)
										{
											log.error(ex.getMessage(), ex);
											log.info("Header element not found");
										}
									}
							Thread.sleep(15000);
						}
							
					//*****************************//
					



					String linkXPath = targetXpath + "//div/div/span";
					
					log.info("XPath of target WebLink is : " + linkXPath);
					WebElement newelement = Automation.driver.findElement(By.xpath(linkXPath));
					/*Thread.sleep(1000);
					Thread.sleep(1000);*/
					if(controller.controllerTransactionType.toString().equalsIgnoreCase("ModifyAccountCurrent"))
					{
						log.info("Do not scroll");
					}
					else

					{//Meghna
						((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", newelement);

					}//Meghna
					Thread.sleep(1000);

					
					newelement.click();
					
					log.info("Checks");
					
					//******For Modify Acc Current ---Meghna***
					int loopcntr = 0; //meghna--Loop Control
					if(controller.controllerTransactionType.toString().equalsIgnoreCase("ModifyAccountCurrent"))
					{	
						String tempValue;
						
						if (tempPosition == 12)
						{
							finalXpathTemp = finalXpathTemp+"/../td[position()="+(tempPosition)+"]";
							WebElement tempEdit = Automation.driver.findElement(By.xpath(finalXpathTemp)) ;
							
							tempValue = tempEdit.getAttribute("title");
							
							//while (tempValue.isEmpty())
							while (tempValue.isEmpty() && loopcntr < 50)     //meghna--Loop Control
							{
								if(newelement.isDisplayed())
								{
									//tempEdit.sendKeys(Keys.UP);
									//newelement.click();//meghna-commented to test with javascript
									((JavascriptExecutor)Automation.driver).executeScript("arguments[0].click();", newelement);
									
								}
								tempValue = tempEdit.getAttribute("title");
								loopcntr = loopcntr+1;							//meghna--Loop Control
							}
						}
						
					}
					
				}
					//*****************
					
					
					//******************* Below given code is to handle remarks popup in the Find Action*************//Mandar20/07/2017
						if(controller.controllerTransactionType.toString().equalsIgnoreCase("APR_UI")&& Oprkey.equalsIgnoreCase("Remarks"))   // To handle objects which are not in table//Harcoded for create payment UI
						{
							WebElement RemWebedit = Automation.driver.findElement(By.xpath(".//*[@id='addRemarks']"));
							((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", RemWebedit);
							RemWebedit.sendKeys(controlValue);
							Thread.sleep(1000);
							WebElement RemButton = Automation.driver.findElement(By.xpath(".//*[@id='okRemarks']"));
							RemButton.click();
							Thread.sleep(1000);
							//*************************//
						}
					
					}
				
			
				else if(controlType.equalsIgnoreCase("Radio"))
				{
					String XPath = targetXpath+"//div/div/input";
					log.info("XPath of target Radio is : " + XPath);
					WebElement newelement = Automation.driver.findElement(By.xpath(XPath));
					Thread.sleep(1000);
					((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", newelement);
					Thread.sleep(1000);
					newelement.click();
				}
				else if(controlType.equalsIgnoreCase("CheckBox"))
				{	
					if(controlValue!= "")//Meghna
					{	
						Thread.sleep(3000);
						String XPath = targetXpath+"//div/div/input";
						log.info("XPath of target CheckBox is : " + XPath);
						WebElement newelement = Automation.driver.findElement(By.xpath(XPath));
						if(controlValue.equalsIgnoreCase("Y") || controlValue.equalsIgnoreCase("Yes"))
						{
							if (!newelement.isSelected())
							{
								//newelement.click();
								Thread.sleep(1000);
								((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", newelement);
								newelement.click();
								Thread.sleep(1000);
								if(!newelement.isSelected())
								{
								((JavascriptExecutor)Automation.driver).executeScript("arguments[0].click();", newelement);
								}
								//((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", newelement);
								Thread.sleep(1000);
							}
						}
						else if(controlValue.equalsIgnoreCase("N") || controlValue.equalsIgnoreCase("No"))
						{
							if (newelement.isSelected())
							{
							
								Thread.sleep(1000);
								((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", newelement);
								newelement.click();  //Mangesh--For unchecking the checkbox
								Thread.sleep(1000);
								newelement.click();
							}
						}
						else if(controlValue.equalsIgnoreCase("") || StringUtils.isEmpty(controlValue))
						{
							//return;
						}
					}
						
					
				}
				else if(controlType.equalsIgnoreCase("WebList"))
				{
					if(controlValue!= "")
						{
						Thread.sleep(3000);
						String XPath = targetXpath+"//div/div/select";
						log.info("XPath of target WebList is : " + XPath);
						WebElement newelement = Automation.driver.findElement(By.xpath(XPath));
						ExpectedCondition<Boolean> isTextPresent =	CommonExpectedConditions.textToBePresentInElement(newelement, controlValue);
						if(isTextPresent != null)
							{
								Thread.sleep(1000);
								((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", newelement);
								newelement.click();
								Select dropdown1 = new Select(newelement);
								Thread.sleep(1000);
								dropdown1.selectByVisibleText(controlValue);
								Thread.sleep(1000);
								if (!"GroupBilling".equalsIgnoreCase(Config
										.productTeam)){
									try
									{
										if (newelement.isDisplayed())//Meghna
										{
											newelement.click();
										}
									}
									
									catch(Exception ex)
									{
										log.error(ex.getMessage(), ex);
										log.info("Dropdown Selected");
									}
									//new Select(newelement).selectByVisibleText(controlValue);
								}							
							}
						
						/*//---Meghna---Adding loop to select list option when the code works but option is not selected---//
						
						if(isTextPresent != null)
						{
						Thread.sleep(1000);
						((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", newelement);
						newelement.click();
						Select dropdown1 = new Select(newelement);
						WebElement opt = dropdown1.getFirstSelectedOption();
						String selOpt = opt.getAttribute("value");
				
						while(!(selOpt.equalsIgnoreCase(controlValue)) && (selOpt.equalsIgnoreCase("Select")))
							
						{
							Thread.sleep(1000);
							dropdown1.selectByVisibleText(controlValue);
							Thread.sleep(1000);
							try
							{
								if (newelement.isDisplayed())//Meghna
								{
									newelement.click();
								}
							}
							
							catch(Exception ex)
							{
								log.info("Dropdown Selected");
							}
							//new Select(newelement).selectByVisibleText(controlValue);
							
							opt = dropdown1.getFirstSelectedOption();
							selOpt = opt.getAttribute("value");
						}

						}
						
						//Meghna--
*/						
						}
				}
			}
	}
	catch(Exception ex)
	{
		log.error(ex.getMessage(), ex);
		//System.err.println("Cause = " + e.getStackTrace()[0].getClassName());
		//System.err.println("Cause = " + e.getStackTrace()[0].getMethodName());
		//System.err.println("Cause = " + e.getStackTrace()[0].getLineNumber());
		//e.getCause();
		
		//iTAFSeleniumWeb.WebDriver.report.setStrMessage(ex.getLocalizedMessage());
		webDriver.getReport().setMessage("Error in getting the row in FIND : " + ex.getLocalizedMessage());
		webDriver.getReport().setStatus("FAIL");
		StartRecovery.initiateRecovery();
		throw new Exception("Failed in FIND: " + controlName + " <-|-> LocalizeMessage " + ex.getLocalizedMessage() +" <-|-> Message "+ ex.getMessage() +" <-|-> Cause "+ ex.getCause());
	
				
	}	
	}
	
	
	//public static void TableInputAction(WebElement tableFound,String controlName,String logicalName,HSSFRow rowValues,HashMap<String,Object> valuesHeader, ArrayList<Integer> valuesheetrowsnum)  throws Exception
	public static void TableInputAction(WebElement tableFound,String controlName,String logicalName,Row rowValues12,HashMap<String,Integer> valuesHeader, ArrayList<Integer> valuesheetrowsnum)  throws Exception
	{
		try
			{
				log.info("logicalName is : " + logicalName);
				ColumnheaderIndex.clear();
				Operatectrlvalues.clear();
				OperateControlType.clear();
				
				String rowvalue = "";
				String currentHeader = null;

				
				//Meghna-For ChecksEntry Edit Flow--01/12/2017
				//int TIRow = 1;
				int TIRow;
				if(controller.controllerTransactionType.toString().equalsIgnoreCase("ChecksEntry") && logicalName.contains("Edit_"))
				{
					TIRow = 2; //Only for Edit specific scenario--Meghna
				}
				else
				{
					TIRow = 1; //for all TableInput scenarios--Meghna
				}
				//Meghna-For ChecksEntry Edit Flow--01/12/2017
				
				InputStream TImyXls = new FileInputStream(WebHelper.TIFilePath);
				//HSSFWorkbook TIworkBook = new HSSFWorkbook(TImyXls);
				XSSFWorkbook TIworkBook = new XSSFWorkbook(TImyXls);
				TIformat  = TIworkBook.createDataFormat();
				//HSSFSheet TIsheetStructure = TIworkBook.getSheet("Values");		
				XSSFSheet TIsheetStructure = TIworkBook.getSheet("Values");		
				
				List<WebElement> table_Th =  tableFound.findElements(By.tagName("th"));
				//List<WebElement> table_Th1 =  tableFound.findElements(By.xpath("//tr/td"));
				for(int i=0;i<table_Th.size();i++)
				{			
					//currentHeader = table_Th.get(i).getText(); //Meghna
					currentHeader = (table_Th.get(i).getText()).toUpperCase(); //Meghna - Updated to handle table header change upgrade
					if(ColumnheaderIndex.containsKey(currentHeader)==false)
						{
							ColumnheaderIndex.put(currentHeader, i+1);
						}
				}		
				log.info("Application column headers and their indexes are : " + ColumnheaderIndex);
				
				String[] ExcelLogicalName = logicalName.split("\\|");
				int logicalnameLen = ExcelLogicalName.length;
				String ctrlValue = null;
				
				for( int valuesheetrow : valuesheetrowsnum)
				{
				Operatectrlvalues.clear();
				OperateControlType.clear();
				for(int i=0;i<logicalnameLen;i+=3)   // for getting target columns and values
				{
					//System.out.println("XXX");
					ctrlValue = WebHelperUtil.getCellData((ExcelLogicalName[i+2].toString()),TIsheetStructure,valuesheetrow,valuesHeader);
			
					/*HSSFCell ctrlValuecell = rowValues.getCell(Integer.parseInt(valuesHeader.get(ExcelLogicalName[i+2]).toString()));
					DataFormatter fmt = new DataFormatter();
					
					if(ctrlValuecell == null)
					{
						ctrlValue = "";
					}
					else
					{
						int type = ctrlValuecell.getCellType();
						switch(type)
						{
						case HSSFCell.CELL_TYPE_BLANK:
							ctrlValue = "";
							break;
						case HSSFCell.CELL_TYPE_NUMERIC:
							ctrlValue = fmt.formatCellValue(ctrlValuecell);								
							break;
						case HSSFCell.CELL_TYPE_STRING:
							ctrlValue = ctrlValuecell.getStringCellValue();
							break;
						case HSSFCell.CELL_TYPE_BOOLEAN:
							ctrlValue = Boolean.toString(ctrlValuecell.getBooleanCellValue());
							break;
						case HSSFCell.CELL_TYPE_ERROR:
							ctrlValue = "error";
							break;
						case HSSFCell.CELL_TYPE_FORMULA:
							ctrlValue = ctrlValuecell.getCellFormula();
							break;
						}
					}
					*/
					Operatectrlvalues.put(ExcelLogicalName[i],ctrlValue);
					OperateControlType.put(ExcelLogicalName[i], ExcelLogicalName[i+1]);
					rowvalue = rowvalue + ctrlValue;	
					
				}	
				
				log.info("Target column ctrlvalues are : " + Operatectrlvalues);
				log.info("Target column ctrltypes are : " + OperateControlType);
				if(rowvalue.equals(""))
				{
					log.info("As per value sheet, there is nothing to input on application further and hence coming out of TableInput function");
					return;
				}
				rowvalue = "";
				Set<String> Operatekeys = Operatectrlvalues.keySet();
				String targetXpath = "";
				
				for(String Oprkey : Operatekeys )
				{
					String controlType = (String)OperateControlType.get(Oprkey).toString();
					String controlValue = (String)Operatectrlvalues.get(Oprkey).toString();
					int positionint;
					String position;
					
					try{
						positionint = (Integer.parseInt(Oprkey));
						position = ""+positionint;
					}
					catch(Exception pe)
					{
						//position = (String) ColumnheaderIndex.get(Oprkey).toString().trim();//Meghna
						position = (String) ColumnheaderIndex.get((Oprkey).toUpperCase()).toString().trim();
						//log.error(pe.getMessage(), pe);
					}
										
					/*if(MainController.controllerTransactionType.toString().equalsIgnoreCase("WriteOffUndoWriteOff")) 
					{
						if(Oprkey.toString().trim().startsWith("Gross") && Oprkey.toString().trim().endsWith("Gross"))
						{
							position = "13";}
					}

					if(MainController.controllerTransactionType.toString().equalsIgnoreCase("CreateAccountCurrent") || MainController.controllerTransactionType.toString().equalsIgnoreCase("AgencyPaymentAllocation"))
					{
						if(Oprkey.toString().trim().startsWith("Gross") && Oprkey.toString().trim().endsWith("Gross"))
						{
							position = "12";}
						if(Oprkey.toString().trim().startsWith("Comm. %") && Oprkey.toString().trim().endsWith("Comm. %")) 
						{
							position = "13";}
						if(Oprkey.toString().trim().startsWith("Commission") && Oprkey.toString().trim().endsWith("Commission")) 
						{
							position = "14";}
						if(Oprkey.toString().trim().startsWith("Net") && Oprkey.toString().trim().endsWith("Net")) 
						{
							position = "15";}
						if(Oprkey.toString().trim().startsWith("Remarks") && Oprkey.toString().trim().endsWith("Remarks")) 
						{
							position = "16";}
						if(Oprkey.toString().trim().equalsIgnoreCase(""))
						{
							position = "17";}	
					}*/
					
					log.info("Target key , position , value , type : " + Oprkey + ": " + position + ": " + controlValue + ": " + controlType);
					String actualcontrolname = controlName;
					targetXpath = actualcontrolname+"/tbody/tr["+TIRow+"]/td["+position+"]";				
														
					if(controlType.equalsIgnoreCase("WebEdit"))  //operate on targeted column
					{						
						String XPath = targetXpath+"//div/div/input";
						log.info("XPath of target WebEdit is : " + XPath);
						WebElement newelement = Automation.driver.findElement(By.xpath(XPath));
						log.info("controlType is : " + controlType + " && " + "controlValue is : " + controlValue);//Mandar
						
						//Meghna-01/12/2017--Ui Validation-- To clear fields in webtable
						if(controlValue.equalsIgnoreCase("BLANK"))
						{
							newelement.click();
							Thread.sleep(1000);		
							newelement.clear();
						}
						//Meghna-01/12/2017--Ui Validation-- To clear fields in webtable
						
						else if(controlType!= "" && controlValue!= "")
						{
							Thread.sleep(1000);
							((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", newelement);
							Thread.sleep(1000);
							newelement.click();
							Thread.sleep(1000);		
							newelement.clear();
							Thread.sleep(1000);
							newelement.sendKeys(controlValue);
							Thread.sleep(2000);
							//newelement.click();//Mandar to avoid remarks click on payment screen (UI)	
							//newelement.sendKeys(Keys.TAB);//Mandar
							//Thread.sleep(2000);//Mandar
						   ((JavascriptExecutor)Automation.driver).executeScript("arguments[0].setAttribute('value', '"+controlValue+"')", newelement); //For handling Object level issue 01 June//Meghna
						   	newelement.clear();	
							Thread.sleep(1000);	
							newelement.sendKeys(controlValue);
							Thread.sleep(1000);
							newelement.click();
							newelement.sendKeys(Keys.TAB);
							Thread.sleep(1000);
							//newelement.sendKeys(Keys.ESCAPE);//Mandar for back to back date calendars--Commented as this was causing failure in ChecksEntry transaction--Meghna
							
							//Mandar 12/05/2017 to handle Group Billing Condition 
							if(controller.controllerTransactionType.toString().equalsIgnoreCase("CreateDeposit") && Oprkey.equalsIgnoreCase("*Apply To #"))// To handle objects which are not in table
							{
							//WebElement ApplyToWebedit = Automation.driver.findElement(By.xpath(".//*[@id='addRemarks']"));
							try
							{
							WebElement ApplyToWebedit = Automation.driver.findElement(By.xpath(".//*[contains(@class,'modal-title')]"));
							((JavascriptExecutor)Automation.driver).executeScript("arguments[0].click();", ApplyToWebedit);
							ApplyToWebedit.sendKeys(controlValue);
							Thread.sleep(1000);
							WebElement ApplyToButton = Automation.driver.findElement(By.xpath(".//*[@name='Ok']"));
							ApplyToButton.click();
							Thread.sleep(1000);
							}catch(Exception e)
							{
								log.error(e.getMessage(), e);
								log.info("Warning message on Apply To # handle and tLocalizedMessage" + e.getLocalizedMessage());
							}
							}
						}
						
					}
					else if(controlType.equalsIgnoreCase("WebButton"))
					{
						String XPath = targetXpath+"//div/div";
						log.info("XPath of target WebButton is : " + XPath);
						WebElement newelement = Automation.driver.findElement(By.xpath(XPath));
						Thread.sleep(1000);
						((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", newelement);
						Thread.sleep(1000);
						newelement.click();
					}
					else if(controlType.equalsIgnoreCase("WebLink"))
					{	
						
						if(controlValue!= "")
						{
							String linkXPath = targetXpath + "//div/div/span";
							log.info("XPath of target WebLink is : " + linkXPath);
							WebElement newelement = Automation.driver.findElement(By.xpath(linkXPath));
							Thread.sleep(1000);
							Thread.sleep(1000);
							((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", newelement);
							Thread.sleep(1000);
							newelement.click();
							//if(MainController.controllerTransactionType.toString().equalsIgnoreCase("ChecksEntry") && Oprkey.equalsIgnoreCase("Remarks"))   // To handle objects which are not in table
							if((controller.controllerTransactionType.toString().equalsIgnoreCase("ChecksEntry") && Oprkey.equalsIgnoreCase("Remarks")) ||(controller.controllerTransactionType.toString().equalsIgnoreCase("CreateDeposit") && Oprkey.equalsIgnoreCase("Remarks")))   // To handle objects which are not in table/Mandar --CreateDeposit
							
							{
								WebElement RemWebedit = Automation.driver.findElement(By.xpath(".//*[@id='addRemarks']"));
								((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", RemWebedit);
								RemWebedit.clear();//Mandar
								Thread.sleep(1000);
								RemWebedit.sendKeys(controlValue);//Mandar
								((JavascriptExecutor)Automation.driver).executeScript("arguments[0].setAttribute('value', '"+controlValue+"')", RemWebedit); //For handling Object level issue 01 June
								RemWebedit.clear();	//Mandar -- newelement chnaged to RemWebedit
								Thread.sleep(1000);
								RemWebedit.sendKeys(controlValue);//Mandar -- newelement chnaged to RemWebedit			
								Thread.sleep(1000);
								WebElement RemButton = Automation.driver.findElement(By.xpath(".//*[@id='okRemarks']"));
								RemButton.click();
								Thread.sleep(1000);
						    }
						
						}
					}
					else if(controlType.equalsIgnoreCase("Radio"))
					{
						String XPath = targetXpath+"//div/div/input";
						log.info("XPath of target Radio is : " + XPath);
						WebElement newelement = Automation.driver.findElement(By.xpath(XPath));
						Thread.sleep(1000);
						((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", newelement);
						Thread.sleep(1000);
						newelement.click();
					}
					else if(controlType.equalsIgnoreCase("CheckBox"))
					{
						String XPath = targetXpath+"//div/div/input";
						log.info("XPath of target CheckBox is : " + XPath);
						WebElement newelement = Automation.driver.findElement(By.xpath(XPath));
						if(controlValue.equalsIgnoreCase("Y") || controlValue.equalsIgnoreCase("Yes"))
						{
							if (!newelement.isSelected())
							{
								//newelement.click();
								Thread.sleep(1000);
								((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", newelement);
								newelement.click();
								Thread.sleep(1000);
								if(!newelement.isSelected())
								{
								((JavascriptExecutor)Automation.driver).executeScript("arguments[0].click();", newelement);
								}
								//((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", newelement);
								Thread.sleep(1000);
							}
						}
						else if(controlValue.equalsIgnoreCase("N") || controlValue.equalsIgnoreCase("No"))
						{
							if (newelement.isSelected())
							{
							
								Thread.sleep(1000);
								((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", newelement);
								Thread.sleep(1000);
								newelement.click();
							}
						}
						else if(controlValue.equalsIgnoreCase("") || StringUtils.isEmpty(controlValue))
						{
							//return;
						}
					}
					else if(controlType.equalsIgnoreCase("WebList"))
					{
						if(controlValue!= "")
							{
							String XPath = targetXpath+"//div/div/select";
							log.info("XPath of target WebList is : " + XPath);
							WebElement newelement = Automation.driver.findElement(By.xpath(XPath));
							ExpectedCondition<Boolean> isTextPresent =	CommonExpectedConditions.textToBePresentInElement(newelement, controlValue);
							if(isTextPresent != null)
								{
								Thread.sleep(1000);
								((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", newelement);
								
								newelement.click();
								Select dropdown1 = new Select(newelement);
								Thread.sleep(1000);
								dropdown1.selectByVisibleText(controlValue);
								Thread.sleep(1000);
								
								newelement.click();
								Thread.sleep(1000);
								//new Select(newelement).selectByVisibleText(controlValue);
								
								}
							}
					}
				}
				//if((MainController.controllerTransactionType.toString().equalsIgnoreCase("ChecksEntry")))
				if((controller.controllerTransactionType.toString().equalsIgnoreCase("ChecksEntry")) || (controller.controllerTransactionType.toString().equalsIgnoreCase("ManualEntry")) || (controller.controllerTransactionType.toString().equalsIgnoreCase("CreateDeposit")))//Mandar -- Createdeposit Added
				{TIRow = TIRow + 1;	}				
				
				}
				
			}
			
		catch(Exception ex)
			{
			log.error(ex.getMessage(), ex);
			webDriver.getReport().setMessage("Error in TableInputAction : " + ex.getLocalizedMessage());
			webDriver.getReport().setStatus("FAIL");
				StartRecovery.initiateRecovery();
				throw new Exception("Failed in TableInput: " + controlName + " <-|-> LocalizeMessage " + ex.getLocalizedMessage() +" <-|-> Message "+ ex.getMessage() +" <-|-> Cause "+ ex.getCause());			
			}	
	
	}
	
}
