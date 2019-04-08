package com.majesco.itaf.main;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.hamcrest.number.IsCloseTo;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.sikuli.script.App;
import org.sikuli.script.Screen;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.majesco.itaf.recovery.StartRecoveryClaims;
import com.majesco.itaf.util.BillingProduct;
//import java.util.Timer;
import com.majesco.itaf.util.Constants;
import com.majesco.itaf.util.ExcelUtility;
import com.majesco.itaf.verification.WebVerification;
import com.majesco.itaf.vo.Reporter;

@SuppressWarnings("unused")
public class WebHelperClaims {

	public static String claimsloader= "//div[@class='overlay']/div[@class='logo-wrapper']/div";
	public static Reporter report = new Reporter();
	final static Logger log = Logger.getLogger(WebHelperClaims.class.getName());
	//bhaskar Time Travel Approach START
	//public static HSSFCell executeFlag_Values = null;
	public static Cell WebservicecycleDate = null;
	public static String wscycledate;
	public static Boolean blank = false;
	//bhaskar Recovery Scenario (WebService) START
	private static HashMap<String,Object> maincontrolsheet = new HashMap<String,Object>();
	private static Row currRow = null;
	public static boolean recovery_done = false;
	private static int current_SC_NO = 0;
	private static int currentscenario_num = 0;
	private static int failedscenario_num = 0;
	public static Sheet MainControllerSheet = null;
	public static Boolean colnotfound = false;
	//bhaskar Recovery Scenario (WebService) END

	//bhaskar FIND Action START
	public static HashMap<String, Object> vColumnheaderIndex = new HashMap<String, Object>();
	public static HashMap<String, Object> vColumnheaderValues = new HashMap<String, Object>();
	//bhaskar FIND Action End


	public static Date toDate=null;
	public static String testCase = null;
	public static Screen sikuliScreen = null;
	public static List<String> searchValue1 = null;
	public static Boolean pageLoaded = false;
	
	static ITAFWebDriver webDriver = ITAFWebDriver.getInstance();
	static MainController controller = ObjectFactory.getMainController();
		
	
	static void implementWait(){
		WebDriverWait WaitForPageLoad=new WebDriverWait(Automation.driver,90);
		WaitForPageLoad.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(claimsloader)));
		//Devishree : For handling Object level issue 01 June			
		WebHelper.wait = new FluentWait<WebDriver>(WebHelper.currentdriver)
				.withTimeout(Integer.parseInt(Config.timeOut), TimeUnit.SECONDS)
				.pollingEvery(5, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class)
				.ignoring(ElementNotVisibleException.class)
				.ignoring(ElementNotInteractableException.class)
				.ignoring(ElementNotFoundException.class);

		//wait = new WebDriverWait(currentdriver,Integer.parseInt(Automation.configHashMap.get("TIMEOUT").toString()));
		//bhaskar driver new session END

	}

	public static void calldoAction(Sheet headerValues, String logicalName,
			Row rowValues, String TransactionType, int valuesRowIndex,
			String action, String controlName, Sheet sheetStructure,
			int rowIndex, String TestCaseID, String controltype,
			String controlID, String indexVal, String imageType,
			String FilePath, int rowCount, String rowNo, String colNo,
			String operationType) throws Exception	{
		String ctrlValue1 = null;
		String ctrlValue2 = null;
		String ctrlValue= null;
		String cycleDate = null;
		WebElement webElement = null;
		List<WebElement> controlList = null;
		colnotfound = false;
		//boolean isControlValueFound =false;
		if(WebHelper.valuesHeader.isEmpty()== true)
		{
			WebHelper.valuesHeader = WebHelperUtil.getValueFromHashMap(headerValues);
		}
		Object actualValue=null;

		if(logicalName!=null){
			actualValue = WebHelper.valuesHeader.get(logicalName.toString());}//headerRow.getCell(colIndex);

		if(actualValue == null)
		{
			colnotfound = true;  //log.info("Null");
		}

		WebHelper.testcaseID = rowValues.getCell(Integer.parseInt(WebHelper.valuesHeader.get("TestCaseID").toString()));
		WebHelper.cycleDate_Values2 = rowValues.getCell(Integer.parseInt(WebHelper.valuesHeader.get("CycleDate").toString()));
		if(WebHelper.testcaseID == null)
		{
			testCase = "";
		}
		else
		{
			testCase = WebHelper.testcaseID.toString();
		}
		WebHelper.transactionType = rowValues.getCell(Integer.parseInt(WebHelper.valuesHeader.get("TransactionType").toString()));
		String stransactionType = TransactionType.toString();

		if(stransactionType.toString().startsWith("WebService"))//devishree
		{
			WebHelper.ctrlValue1Cell = rowValues.getCell(Integer.parseInt(WebHelper.valuesHeader.get("PathToNode").toString()));
			WebHelper.ctrlValue2Cell = rowValues.getCell(Integer.parseInt(WebHelper.valuesHeader.get("ColumnName").toString()));
			WebservicecycleDate = rowValues.getCell(Integer.parseInt(WebHelper.valuesHeader.get("CycleDate").toString()));
			ctrlValue1 = WebHelper.ctrlValue1Cell.toString();
			ctrlValue2 = WebHelper.ctrlValue2Cell.toString();
			wscycledate = WebservicecycleDate.toString();
			SimpleDateFormat cdFormat = new SimpleDateFormat("dd-MMM-yyyy");
			DateFormat cycleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
			if(wscycledate.contains("-"))
			{
				Date CycleDate_Values = cdFormat.parse(wscycledate);
				wscycledate = cycleDateFormat.format(CycleDate_Values);
			}
		}

		if(stransactionType.toString().equalsIgnoreCase("ChangeBusinessDate"))
		{
			ctrlValue = controller.businessDateValue;
		}
		else
		{
			if(colnotfound == false)
			{
				ctrlValue = WebHelperUtil.getCellData(logicalName,headerValues, valuesRowIndex, WebHelper.valuesHeader);
			}
			else
			{
				ctrlValue = "";
			}
			cycleDate = WebHelperUtil.getCellData("CycleDate",headerValues,valuesRowIndex,WebHelper.valuesHeader);
		}

		//bhaskar remove leading and trailing whitespaces from values sheet data START
		Pattern trimregex = Pattern.compile("^\\s+|\\s+$");
		Matcher match = trimregex.matcher(ctrlValue);
		StringBuffer ctrlValue_output = new StringBuffer();
		while(match.find())
			match.appendReplacement(ctrlValue_output, "");
		match.appendTail(ctrlValue_output);
		//log.info(ctrlValue_output);
		//bhaskar remove leading and trailing whitespaces from values sheet data END

		//bhaskar Action CAPTURE keyword START
		if(action.equalsIgnoreCase("Capture"))
		{
			//Reporter report = new Reporter();
			log.info("Inside Capture Case");
			controlName = WebHelperUtil.getCellData("ControlName",sheetStructure,rowIndex,WebHelper.structureHeader);
			logicalName = WebHelperUtil.getCellData("LogicalName",sheetStructure,rowIndex,WebHelper.structureHeader);
			if(ctrlValue.equalsIgnoreCase("Y"))
			{
				log.info("CYCLEDATE is :"+cycleDate);
				TransactionMapping.TransactionCaptureData(cycleDate,TestCaseID,controlName,Config.transactionInputFilePath);
			}
		}
		//bhaskar Action CAPTURE keyword END

		//if ((action.equals("I")&&!ctrlValue.isEmpty())||(action.equals("V")&&!ctrlValue.isEmpty())|| !action.equals("I"))
		if (((action.equals("I") && !StringUtils.isEmpty(ctrlValue))||(action.equals("V") && !StringUtils.isEmpty(ctrlValue))|| !action.equals("I")) && !action.equalsIgnoreCase("Capture") && !action.equalsIgnoreCase("FIND") && !action.equalsIgnoreCase("TABLEINPUT"))
		{
			if(logicalName.equalsIgnoreCase("WAIT"))
			{
				log.info(action + " " + logicalName + " For " + controlName);
			}
			else
			{
				log.info(action + " On " + controltype + " " + logicalName);
			}

			if(logicalName.equalsIgnoreCase("CreateBatch"))
			{
				System.out.println("wait");}

			if(!controltype.startsWith("Sikuli"))
			{
				if(!action.equalsIgnoreCase("V")&&!action.equalsIgnoreCase("LOOP")&&!controltype.equalsIgnoreCase("Wait")&&!action.equalsIgnoreCase("END_LOOP")&&
						!controltype.equalsIgnoreCase("Browser")&&!controltype.equalsIgnoreCase("NewBrowser")&&!controltype.equalsIgnoreCase("CloseBrowser")&&!controltype.equalsIgnoreCase("Window")&&!controltype.equalsIgnoreCase("Alert")&&
						!controltype.equalsIgnoreCase("URL")&&!controltype.equalsIgnoreCase("WaitForJS")&&!controltype.contains("Robot") && 
						!controltype.equalsIgnoreCase("Calendar")&&!controltype.equalsIgnoreCase("CalendarNew")&&!controltype.equalsIgnoreCase("CalendarIPF")&&!controltype.equalsIgnoreCase("CalendarEBP")&&										
						//(!action.equalsIgnoreCase("Read")||((action.equalsIgnoreCase("Read")&& !controlName.isEmpty())))&&
						(!action.equalsIgnoreCase("Read")||((action.equalsIgnoreCase("Read")&& !StringUtils.isEmpty(controlName))))&&
						!controltype.equalsIgnoreCase("JSScript")&&!controltype.equalsIgnoreCase("DB")&& !controlID.equalsIgnoreCase("XML")&& !controltype.startsWith("Process")
						&& !controltype.startsWith("Destroy")&& !controltype.startsWith("ReadSikuli") &&!controltype.equalsIgnoreCase("WebService") && !action.equalsIgnoreCase("VA") && !action.equalsIgnoreCase("FileCompare") && !controltype.equalsIgnoreCase("Screenshot")//devishree
						&&!controltype.equalsIgnoreCase("WebService1")&&!controltype.equalsIgnoreCase("WebService2")&&!controltype.equalsIgnoreCase("WebService3")&&!controltype.equalsIgnoreCase("WebServiceV")&&!controltype.equalsIgnoreCase("WebServiceC")&&!controltype.equalsIgnoreCase("WebServiceRP")&&!controltype.equalsIgnoreCase("WebServiceV1")&&!controltype.equalsIgnoreCase("WebServiceV2")&&!controltype.equalsIgnoreCase("WebServiceVAG")&&!controltype.equalsIgnoreCase("WebServiceV3")&&!controltype.equalsIgnoreCase("Multiselect"))  //devishree
				{
					//bhaskar Supressing exception when element not found START
					//if((indexVal.equalsIgnoreCase("")||indexVal.equalsIgnoreCase("0"))&& !controlID.equalsIgnoreCase("TagValue")&&!controlID.equalsIgnoreCase("TagText"))
					if((indexVal.equalsIgnoreCase("")||indexVal.equalsIgnoreCase("0"))&& !controlID.equalsIgnoreCase("TagValue")&&!controlID.equalsIgnoreCase("TagText") && !action.equalsIgnoreCase("NoException") && !action.equalsIgnoreCase("FIND") && !action.equalsIgnoreCase("TABLEINPUT"))
						//bhaskar Supressing exception when element not found END
					{
						try
						{	

							if(controlName.contains("+"))
							{
								controlName = controlName.replace("+", ctrlValue);

								/*String NewcontrolName = "";
						String[] SplitXPath = controlName.split("+");
						String tempactualValue = null;
						for(int k =0; k < SplitXPath.length; k++)
						{
							if((k % 2) !=  0)
							{
								//tempactualValue = valuesHeader.get(SplitXPath[k].toString();								
							} 
							NewcontrolName = NewcontrolName + SplitXPath[k];								
						}*/
							}

							for (int i=0; i<25; i++)   //Devishree :6/9/2017: Start : For handling Object level issue 01 June
							{ 
								if(((JavascriptExecutor)WebHelper.currentdriver).executeScript("return document.readyState").toString().equals("complete"))
								{ 
									break; 
								} 
								else
								{
									Thread.sleep(1000);
									//Automation.driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
								}
							}  //Devishree :6/9/2017: End : For handling Object level issue 01 June


							webElement = getElementByType(controlID, controlName,WebHelper.control,imageType,ctrlValue);
						}catch(NoSuchElementException nse)
						{
							log.error("Failed to find Elements using FindBy for Control ID " + controlID + " <-|-> controlName :" + controlName + "<-|-> LocalizeMessage " + nse.getLocalizedMessage() +" <-|-> Message "+ nse.getMessage() +" <-|-> Cause "+ nse.getCause(), nse);
							StartRecoveryClaims.initiateRecovery();
							throw new NoSuchElementException("Failed to find Elements using FindBy for Control ID " + controlID + " <-|-> controlName :" + controlName + " <-|-> LocalizeMessage " + nse.getLocalizedMessage() +" <-|-> Message"+ nse.getMessage() +" <-|-> Cause "+ nse.getCause());	
						}catch(StaleElementReferenceException sere)
						{
							log.error("Element is no longer appearing on the DOM page for Control ID" + controlID + " <-|-> controlName :" + controlName + "<-|-> LocalizeMessage " + sere.getLocalizedMessage() +" <-|-> Message "+ sere.getMessage() +" <-|-> Cause "+ sere.getCause(), sere);
							StartRecoveryClaims.initiateRecovery();
							throw new StaleElementReferenceException("Element is no longer appearing on the DOM page for Control ID " + controlID + " <-|-> controlName :" + controlName + " <-|-> LocalizeMessage " + sere.getLocalizedMessage() +" <-|-> Message"+ sere.getMessage() +" <-|-> Cause "+ sere.getCause());	
						}catch(ElementNotVisibleException env)
						{
							log.error("Element is not visible Control ID" + controlID + " <-|-> controlName :" + controlName + "<-|-> LocalizeMessage " + env.getLocalizedMessage() +" <-|-> Message "+ env.getMessage() +" <-|-> Cause "+ env.getCause(), env);
							StartRecoveryClaims.initiateRecovery();
							throw new ElementNotVisibleException("Element is not visible Control ID " + controlID + " <-|-> controlName :" + controlName + " <-|-> LocalizeMessage " + env.getLocalizedMessage() +" <-|-> Message"+ env.getMessage() +" <-|-> Cause "+ env.getCause());	
						}


					}
					//bhaskar Supressing exception when element not found START
					else if(action.equalsIgnoreCase("NoException") || action.equalsIgnoreCase("FIND") || action.equalsIgnoreCase("TABLEINPUT") )
					{
						Boolean elementexists = false;
						Constants.ControlIdEnum scontrolID = Constants.ControlIdEnum.valueOf(controlID);
						Thread.sleep(1000);

						switch(scontrolID)
						{
						case Id:										
							elementexists = WebHelper.currentdriver.findElements(By.id(controlName)).size() > 0;
							break;

						case XPath:
							elementexists = WebHelper.currentdriver.findElements(By.xpath(controlName)).size() > 0;
							break;

						case Name:
							elementexists = WebHelper.currentdriver.findElements(By.name(controlName)).size() > 0;	
							break;

						case ClassName:		 	
							elementexists = WebHelper.currentdriver.findElements(By.className(controlName)).size() > 0;	
							break;

						case LinkText:
							elementexists = WebHelper.currentdriver.findElements(By.linkText(controlName)).size() > 0;
							break;											

						case CSSSelector:
							elementexists = WebHelper.currentdriver.findElements(By.cssSelector(controlName)).size() > 0;
							break;

						case Id_p:
						case HTMLID_p:
							elementexists = WebHelper.currentdriver.findElements(By.id(controlName)).size() > 0;
							break;

						case XPath_p:
							elementexists = WebHelper.currentdriver.findElements(By.xpath(controlName)).size() > 0;
							break;

						default:
							break;
						}

						if(elementexists == true)
						{
							try
							{
								webElement = getElementByType(controlID, controlName,WebHelper.control,imageType,ctrlValue);
							}catch(NoSuchElementException nse)
							{
								log.error("Failed to find Elements using FindBy for Control ID " + scontrolID + " <-|-> controlName :" + controlName + "<-|-> LocalizeMessage " + nse.getLocalizedMessage() +" <-|-> Message "+ nse.getMessage() +" <-|-> Cause "+ nse.getCause(), nse);
								StartRecoveryClaims.initiateRecovery();
								//	throw new NoSuchElementException("Failed to find Elements using FindBy for Control ID " + scontrolID + " <-|-> controlName :" + controlName + " <-|-> LocalizeMessage " + nse.getLocalizedMessage() +" <-|-> Message"+ nse.getMessage() +" <-|-> Cause "+ nse.getCause());	
							}catch(StaleElementReferenceException sere)
							{
								log.error("Element is no longer appearing on the DOM page for Control ID" + scontrolID + " <-|-> controlName :" + controlName + "<-|-> LocalizeMessage " + sere.getLocalizedMessage() +" <-|-> Message "+ sere.getMessage() +" <-|-> Cause "+ sere.getCause(), sere);
								StartRecoveryClaims.initiateRecovery();
								//	throw new StaleElementReferenceException("Element is no longer appearing on the DOM page for Control ID " + scontrolID + " <-|-> controlName :" + controlName + " <-|-> LocalizeMessage " + sere.getLocalizedMessage() +" <-|-> Message"+ sere.getMessage() +" <-|-> Cause "+ sere.getCause());	
							}catch(ElementNotVisibleException env)
							{
								log.error("Element is not visible Control ID" + scontrolID + " <-|-> controlName :" + controlName + "<-|-> LocalizeMessage " + env.getLocalizedMessage() +" <-|-> Message "+ env.getMessage() +" <-|-> Cause "+ env.getCause(), env);
								StartRecoveryClaims.initiateRecovery();
								//	throw new ElementNotVisibleException("Element is not visible Control ID " + scontrolID + " <-|-> controlName :" + controlName + " <-|-> LocalizeMessage " + env.getLocalizedMessage() +" <-|-> Message"+ env.getMessage() +" <-|-> Cause "+ env.getCause());	
							}
							catch(Exception env)
							{
								log.error("Exception is thrown for Control ID" + scontrolID + " <-|-> controlName :" + controlName + "<-|-> LocalizeMessage " + env.getLocalizedMessage() +" <-|-> Message "+ env.getMessage() +" <-|-> Cause "+ env.getCause(), env);
								StartRecoveryClaims.initiateRecovery();
								//	throw new ElementNotVisibleException("Element is not visible Control ID " + scontrolID + " <-|-> controlName :" + controlName + " <-|-> LocalizeMessage " + env.getLocalizedMessage() +" <-|-> Message"+ env.getMessage() +" <-|-> Cause "+ env.getCause());	
							}

						}
						else
						{
							return;
						}
					}
					//bhaskar Supressing exception when element not found END
					else
					{
						controlList = WebHelperUtil.getElementsByType(controlID, controlName,WebHelper.control,imageType,ctrlValue);

						if(controlList != null && controlList.size() > 1)
						{
							try
							{
								webElement = WebHelperUtil.GetControlByIndex(indexVal, controlList, controlID, controlName, WebHelper.control,ctrlValue); //, ISelenium selenium)
							}catch(NoSuchElementException nse)
							{
								log.error("Failed to find Elements using FindBy with index for Control ID " + controlID + " <-|-> controlName :" + controlName + "<-|-> LocalizeMessage " + nse.getLocalizedMessage() +" <-|-> Message "+ nse.getMessage() +" <-|-> Cause "+ nse.getCause(), nse);
								StartRecoveryClaims.initiateRecovery();
								throw new NoSuchElementException("Failed to find Elements using FindBy with index for Control ID " + controlID + " <-|-> controlName :" + controlName + " <-|-> LocalizeMessage " + nse.getLocalizedMessage() +" <-|-> Message"+ nse.getMessage() +" <-|-> Cause "+ nse.getCause());	
							}catch(StaleElementReferenceException sere)
							{
								log.error("Element is no longer appearing on the DOM page with index for Control ID" + controlID + " <-|-> controlName :" + controlName + "<-|-> LocalizeMessage " + sere.getLocalizedMessage() +" <-|-> Message "+ sere.getMessage() +" <-|-> Cause "+ sere.getCause(), sere);
								StartRecoveryClaims.initiateRecovery();
								throw new StaleElementReferenceException("Element is no longer appearing on the DOM page with index for Control ID " + controlID + " <-|-> controlName :" + controlName + " <-|-> LocalizeMessage " + sere.getLocalizedMessage() +" <-|-> Message"+ sere.getMessage() +" <-|-> Cause "+ sere.getCause());	
							}catch(ElementNotVisibleException env)
							{
								log.error("Element is not visible with index for Control ID" + controlID + " <-|-> controlName :" + controlName + "<-|-> LocalizeMessage " + env.getLocalizedMessage() +" <-|-> Message "+ env.getMessage() +" <-|-> Cause "+ env.getCause(), env);
								StartRecoveryClaims.initiateRecovery();
								throw new ElementNotVisibleException("Element is not visible with index for Control ID " + controlID + " <-|-> controlName :" + controlName + " <-|-> LocalizeMessage " + env.getLocalizedMessage() +" <-|-> Message"+ env.getMessage() +" <-|-> Cause "+ env.getCause());	
							}
						}
						else
						{
							return;
						}
					}
				}
			}
			else
			{
				sikuliScreen = new Screen();
				//bhaskar
				//sikuliapp = Automation.SikuliScr;
				//bhaskar
			}
		}

		/***	Perform action on the identified control	***/
		//log.info("go to method doAction");
		if(!action.equalsIgnoreCase("Capture"))
		{
			doAction(FilePath,rowValues,testCase,imageType,controltype,controlID,controlName,ctrlValue,ctrlValue1,ctrlValue2,wscycledate,logicalName,action,webElement,true,sheetStructure,headerValues,rowIndex,rowCount,rowNo,colNo,operationType,cycleDate,TransactionType);
		}
	}

	/** Locating Web Element **/
	public static WebElement getElementByType(String controlId, String controlName, String controlType,String imageType,String controlValue) throws Exception
	{

		WebElement controlList = null;
		try
		{

			if(controlId.equalsIgnoreCase("Id") || controlId.equalsIgnoreCase("HTMLID"))
			{
				controlList = WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.id(controlName)));				
			}
			else if(controlId.equalsIgnoreCase("XPath"))
			{
				//controlList = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName)));
				controlList = WebHelper.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));
			}
			else if(controlId.equalsIgnoreCase("Name"))
			{
				//controlList = wait.until(ExpectedConditions.elementToBeClickable(By.name(controlName)));
				//controlList = wait.until(ExpectedConditions.visibilityOfElementLocated(By.name(controlName)));
				controlList = WebHelper.wait.until(ExpectedConditions.presenceOfElementLocated(By.name(controlName)));
			}
			else if(controlId.equalsIgnoreCase("ClassName"))
			{
				controlList =  WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.className(controlName)));
			}
			else if(controlId.equalsIgnoreCase("LinkText") || controlId.equalsIgnoreCase("LinkValue"))
			{
				controlList = WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.linkText(controlName)));
			}
			else if(controlId.equalsIgnoreCase("TagText") || controlId.equalsIgnoreCase("TagValue") || controlId.equalsIgnoreCase("TagOuterText"))
			{
				controlList = WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.tagName(imageType)));
			}
			else if(controlId.equalsIgnoreCase("CSSSelector"))
			{
				controlList = WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(controlName)));
			}
			else if(controlId.equalsIgnoreCase("AjaxPath"))
			{
				controlList = WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName+"[contains(text(),'"+controlValue+"')]")));
			}
			else if(controlId.equalsIgnoreCase("Id_p") || controlId.equalsIgnoreCase("HTMLID_p"))
			{
				controlList = WebHelper.wait.until(ExpectedConditions.presenceOfElementLocated(By.id(controlName)));
			}
			else if(controlId.equalsIgnoreCase("XPath_p"))
			{
				controlList = WebHelper.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));
			}
			else if(controlId.equalsIgnoreCase("XPath_R"))//Asif : Added 31-May :Start
			{
				if(!controlValue.isEmpty()){

					try{				

						controlList = WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlValue)));

					}catch(Exception e){
						log.error(e.getMessage(), e);
						controlList=null;														
					}				
				}
			}//Asif : Added 31-May :End

			else if(controlId.equalsIgnoreCase("XPath_if"))//Asif : Added 31-May :Start
			{
				WebDriverWait wait1 = new WebDriverWait(Automation.driver,3); 
				try{

					if(wait1.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName))).isDisplayed()){
						controlList = wait1.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName)));
					}
				}catch(Exception e){
					log.error(e.getMessage(), e);
					controlList=null;
				}
			}//Asif : Added 31-May :End

			else if(controlId.equalsIgnoreCase("XPath_value"))//Asif : Added 31-May :Start
			{
				if(!controlValue.isEmpty()){

					try{				

						if (Automation.driver.findElement(By.xpath(controlName)).isDisplayed()){

							List<WebElement>webElement=Automation.driver.findElements(By.xpath(controlName));

							for(WebElement element:webElement){

								System.out.println(element.getAttribute("value"));

								if(element.getAttribute("value").equalsIgnoreCase(controlValue)){

									String Id=element.getAttribute("id");

									String elementTagName	= element.getTagName();

									String controlName1="//"+elementTagName+"[@id='"+Id+"'"+"]/"+imageType;

									controlName=controlName1;

									controlList = Automation.driver.findElement(By.xpath(controlName));

									break;
								}

								else{

									System.out.println("Element value doesn't match with control value");
								}


							}

							controlList = WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName)));

						}						

					}catch(Exception e){
						log.error(e.getMessage(), e);
						controlList=null;														
					}				
				} else{

					System.out.print("don't perform any action");
				}
			}//Asif : Added 31-May :End


			//bhaskar removing enum constants as suggested by dharmendra END
			return controlList;
		}
		catch (Exception ex)
		{
			log.error(ex.getMessage(), ex);
			webDriver.getReport().setMessage(ex.getLocalizedMessage());
			webDriver.getReport().setStatus("FAIL");
			StartRecoveryClaims.initiateRecovery();
			throw new Exception("Failed while access controlName: " + controlName + " <-|-> LocalizeMessage " + ex.getLocalizedMessage() +" <-|-> Message "+ ex.getMessage() +" <-|-> Cause "+ ex.getCause());
		}
	}

	//@SuppressWarnings({ "incomplete-switch", "resource" })
	public static String doAction(String FilePath, Row rowValues,
			String testCase, String imageType, String controlType,
			String controlId, String controlName, String ctrlValue,
			String ctrlValue1, String ctrlValue2, String wscycledate,
			String logicalName, String action, WebElement webElement,
			Boolean Results, Sheet strucSheet, Sheet valSheet,
			int rowIndex, int rowcount, String rowNo, String colNo,
			String operationType, String cycleDate, String TransactionType)
			throws WebDriverException, IOException, Exception	{	
		
		String clocation;		
		//log.info("In method doaction");
		List<WebElement> WebElementList = null;
		String currentValue =null;
		//HSSFSheet uniqueNumberSheet =null;
		String uniqueNumber = "";
		WebVerification.isFromVerification =false;
		//HashMap<String ,Object> uniqueValuesHashMap = null;
		//HSSFRow uniqueRow = null;	
		Constants.ControlTypeEnum controlTypeEnum = Constants.ControlTypeEnum.valueOf(controlType);
		Constants.ControlTypeEnum actionName  = Constants.ControlTypeEnum.valueOf(action.toString());
		//bhaskar
		WebHelper.sikscreen = Config.SikuliScr;
		//log.info(sikscreen);
		//bhaskar
		if(controlType.contains("Robot")&&!WebHelper.isIntialized)
		{
			log.info("In method doaction debug1");
			WebHelper.robot = new Robot();
			WebHelper.isIntialized = true;
		}

		if (!WebHelperUtil.stringIn(action, new String[] { "I", "V", "F", "VA", "VV"})
				|| !ctrlValue.equalsIgnoreCase("")) {
			
			try
			{
				switch(controlTypeEnum)
				{

				case WebEdit:
					switch(actionName)
					{
					case Read:						
						uniqueNumber = ReadFromExcel(ctrlValue);
						//log.info("!!!!!!!!!!!!!!!!");
						//log.info("uniqueNumber:"+uniqueNumber);
						webElement.clear();
						webElement.sendKeys(uniqueNumber);
						break;
					case Write:
						if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes") || !(ctrlValue.trim().equalsIgnoreCase("")))
						{
							WebHelperUtil.writeToExcel(ctrlValue, webElement, controlId, controlType, controlName, rowNo, colNo);
						}
						else{
							System.out.println("Not needed");
						}
						break;
					case I:
						/*if (logicalName.equalsIgnoreCase("Policy")) {
						((JavascriptExecutor)currentdriver).executeScript("arguments[0].setAttribute('value', '"+ctrlValue+"')", webElement);
					}*/
                        if(!ctrlValue.equalsIgnoreCase("null") || !(ctrlValue.trim().equalsIgnoreCase("")))
                        {      
                               if(logicalName.contains("Date") && (ctrlValue.equalsIgnoreCase("Today") || ctrlValue.equalsIgnoreCase("Future"))) //Mayur
                               {
                                      if(ctrlValue.equalsIgnoreCase("Today"))
                                      {
                                             DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                             Date date = new Date();
                                             String todayDate= dateFormat.format(date);
                                             System.out.println(todayDate);
                                             webElement.clear();
                                             Thread.sleep(500);
                                             if(logicalName.contains("VendorManagemtBusiness"))
                                             {
                                                    String vendorName= "Automation"+todayDate;
                                                    webElement.sendKeys(vendorName);
                                             }
                                             else
                                             {
                                                    webElement.sendKeys(todayDate);
                                             }
                                      }
                                      else{
                                             DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                                             Date date = new Date();
                                             Calendar c = Calendar.getInstance();
                                             c.setTime(date); // Now use today date.
                                             c.add(Calendar.DATE, 1);
                                             date = c.getTime();
                                             String todayDate= dateFormat.format(date);
                                             System.out.println(todayDate);
                                             webElement.clear();
                                             Thread.sleep(500);
                                             webElement.sendKeys(todayDate);
                                      }
                               }
                               else if((logicalName.contains("ClaimInput") || logicalName.contains("PolicyInput"))&& ctrlValue.equalsIgnoreCase("Y")) //Mayur
                               {
                                      uniqueNumber = ReadFromExcel(ctrlValue);
                                      webElement.clear();
                                      webElement.sendKeys(uniqueNumber);
                               }
                               else if(logicalName.contains("CheckNo") && ctrlValue.equalsIgnoreCase("Y")) //Mayur
                               {
                                      String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
                                      StringBuilder salt = new StringBuilder();
                                      Random rnd = new Random();
                                      while (salt.length() < 7) 
                                      { // length of the random string.
                                             int index = (int) (rnd.nextFloat() * SALTCHARS.length());
                                             salt.append(SALTCHARS.charAt(index));
                                      }
                                      String saltStr = salt.toString();
                                      System.out.println(saltStr);
                                      webElement.clear();
                                      webElement.sendKeys(saltStr);
                               }
                               else{
                                      log.info("ctrlValue is :"+ctrlValue);
                                      //Thread.sleep(1000);
                                      webElement.clear();
                                      //webElement.click();
                                      Thread.sleep(500);
                                      webElement.sendKeys(ctrlValue);
                                      Thread.sleep(300);
                               }             
                        }
                        else
                        {
                               webElement.clear();
                        }
                        break;
					case V:
						//currentValue = webElement.getAttribute(controlName.toString());


						if(!(ctrlValue.equalsIgnoreCase(""))) 
						{
							WebDriverWait WaitForPageLoad=new WebDriverWait(Automation.driver,60);
							WaitForPageLoad.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(claimsloader)));
							WebElement	Verifyelement = null;
							//switch (controlID)

							//	case Name:
							//			Verifyelement =wait.until(ExpectedConditions.elementToBeClickable(By.name(controlName)));
							//			break;

							//	case XPath:
							Verifyelement= WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName)));
							//			break;

							currentValue = Verifyelement.getText();
							Verifyelement = null;
						}
						else
						{
							System.out.println("Element not Present");
						}
						break;
					case VV: //Mayur_Claims
						Reporter report =new Reporter();
						report.setReport(report);

						String ActualValue1 = webElement.getText();
						String ExpectedValue1 = ctrlValue;

						String[] parts = ActualValue1.split(" ");
						String ActualValue= parts[0]+parts[1]+parts[2];

						String ExpectedValue= ExpectedValue1.replaceAll("\\s+","");

						log.info("ActualValue is : "+ActualValue);
						log.info("ExpectedValue is : "+ExpectedValue);

						if(ActualValue.contains(ExpectedValue))
						{
							report.setStatus("PASS");							
							report.setStatus(report.getStatus());
							report.setMessage("Values Matched");
							//WebHelper.saveScreenShot();
						}
						else
						{
							report.setStatus("FAIL");							
							report.setStatus(report.getStatus());
							report.setMessage("Values Not Matched");
							WebHelperUtil.saveScreenShot();
							controller.pauseFun("");
						}
						WebHelper.columns.add("");
						WebHelper.columnsData.add(WebHelper.columns);
						int temprowcount = 0;
						int tempcolcount = 0;
						ExcelUtility.WriteToCompareDetailResults(testCase,TransactionType,WebHelper.columns,WebHelper.columnsData,temprowcount,tempcolcount,report,ExpectedValue,ActualValue,logicalName,operationType,cycleDate);
						break;
					default:
						break;
					}
					break;

				case WebButton:				
					switch(actionName)
					{
					case I:                                                       
						if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes") || !(ctrlValue.trim().equalsIgnoreCase("")))
						{

							((JavascriptExecutor)WebHelper.currentdriver).executeScript("arguments[0].scrollIntoView();", webElement);

							Thread.sleep(1000);
							if(Automation.browserType.toString().toUpperCase().contains("INTERNETEXPLORER"))
							{
								System.out.println("IE");
								WebDriverWait WaitForPageLoad=new WebDriverWait(Automation.driver,90);
								WaitForPageLoad.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(claimsloader)));
								WaitForPageLoad.until(ExpectedConditions.visibilityOf(webElement));
								WaitForPageLoad.until(ExpectedConditions.elementToBeClickable(webElement));
								webElement.click();
								//((JavascriptExecutor)currentdriver).executeScript("arguments[0].click();", webElement);
							}
							else
							{
								//((JavascriptExecutor)currentdriver).executeScript("arguments[0].click();", webElement);
								WebDriverWait WaitForPageLoad=new WebDriverWait(Automation.driver,90);
								WaitForPageLoad.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(claimsloader)));
								WaitForPageLoad.until(ExpectedConditions.visibilityOf(webElement));
								WaitForPageLoad.until(ExpectedConditions.elementToBeClickable(webElement));
								webElement.click();
							}
						}
						break;
					case NC:
						if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes"))
						{

							((JavascriptExecutor)WebHelper.currentdriver).executeScript("arguments[0].click();", webElement);

						}
						break;
					case V:
						WebDriverWait WaitForPageLoad=new WebDriverWait(Automation.driver,5);
						WaitForPageLoad.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(claimsloader)));
						WebElement	Verifyelement = null;
						//switch (controlID)

						//	case Name:
						//			Verifyelement =wait.until(ExpectedConditions.elementToBeClickable(By.name(controlName)));
						//			break;

						//	case XPath:
						try{
							Verifyelement= WaitForPageLoad.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName)));
							if(Verifyelement.isDisplayed())
							{
								if(Verifyelement.isEnabled() == true)
									currentValue = "True";
								else
									currentValue = "False";
							}
						}
						catch(Exception e)
						{
							log.error(e.getMessage(), e);
							currentValue = "False";
						}
						break;
					case Read: //Mayur_Claims (WorkBench)
						/*if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes") || !(ctrlValue.trim().equalsIgnoreCase("")))
						{*/
						Thread.sleep(1000);
						Reporter report =new Reporter();
						report.setReport(report);

						WebHelper.ActualValue = webElement.getText();
						uniqueNumber = ReadFromExcel(ctrlValue);
						WebHelper.ExpectedValue = uniqueNumber;

						log.info("ActualValue is : "+WebHelper.ActualValue);
						log.info("ExpectedValue is : "+WebHelper.ExpectedValue);
						//if(ActualValue.equalsIgnoreCase(ExpectedValue))

						if(WebHelper.ActualValue.contains(WebHelper.ExpectedValue))
						{
							report.setStatus("PASS");							
							report.setStatus(report.getStatus());
							report.setMessage("Values Matched");
							//WebHelper.saveScreenShot();
						}
						else
						{
							report.setStatus("FAIL");							
							report.setStatus(report.getStatus());
							report.setMessage("Values Not Matched");
						}
						WebHelper.columns.add("");
						WebHelper.columnsData.add(WebHelper.columns);
						int temprowcount = 0;
						int tempcolcount = 0;
						ExcelUtility.WriteToCompareDetailResults(testCase,TransactionType,WebHelper.columns,WebHelper.columnsData,temprowcount,tempcolcount,report,WebHelper.ExpectedValue,WebHelper.ActualValue,logicalName,operationType,cycleDate);
						/*}
						else
						{
							System.out.println("Element is not clicked");
						}*/
						break;	
					case T: //Mayur
						if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes") || !(ctrlValue.trim().equalsIgnoreCase("")))
						{
							WaitForPageLoad=new WebDriverWait(Automation.driver,90);
							WaitForPageLoad.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(claimsloader)));
							//WaitForPageLoad.until(ExpectedConditions.invisibilityOfElementLocated(By.className("overlay hide")));
							WaitForPageLoad.until(ExpectedConditions.visibilityOf(webElement));
							WaitForPageLoad.until(ExpectedConditions.elementToBeClickable(webElement));
							webElement.click();
						}
						else
						{
							System.out.println("Element is not clicked");
						}
						break;
					default:
						break;
					}
					break;

				case WebElement:
					//bhaskar
					//log.info("Inside webelement scenario");
					//WebVerification.isFromVerification = true;
					//bhaskar
					switch(actionName)
					{	
					case NC: //Scroll and Click on JAvaScript
						if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes"))
						{
							uniqueNumber = ReadFromExcel(ctrlValue);
							webElement.clear();
							webElement.sendKeys(uniqueNumber);
							/*((JavascriptExecutor)currentdriver).executeScript("arguments[0].scrollIntoView();", webElement);
							Thread.sleep(1000);
							((JavascriptExecutor)currentdriver).executeScript("arguments[0].click();", webElement);*/
						}
						else{
							System.out.println("Element is not Clicked");
						}
						break;
					case I:	//Mayur_Claims
						Thread.sleep(500);
						if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes") || !(ctrlValue.trim().equalsIgnoreCase("")))
						{
							WebDriverWait WaitForPageLoad=new WebDriverWait(Automation.driver,90);
							WaitForPageLoad.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(claimsloader)));
							WaitForPageLoad.until(ExpectedConditions.visibilityOf(webElement));
							WaitForPageLoad.until(ExpectedConditions.elementToBeClickable(webElement));
							webElement.click();
						}
						else
						{
							System.out.println("Element is not clicked");
						}
						break;
					case Read: // Mayur_Claims
						uniqueNumber = ReadFromExcel(ctrlValue);
						/*try{
							webElement= wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//li[@class='last']//a")));
							((JavascriptExecutor)currentdriver).executeScript("arguments[0].scrollIntoView();", webElement);
							WebDriverWait WaitForPageLoad=new WebDriverWait(Automation.driver,120);
							WaitForPageLoad.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(claimsloader)));
							WaitForPageLoad.until(ExpectedConditions.visibilityOf(webElement));
							WaitForPageLoad.until(ExpectedConditions.elementToBeClickable(webElement));
							webElement.click();
							WebElement webElement2= wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName+"[contains(text(),'"+uniqueNumber+"')]")));
							System.out.println(webElement2);
							WaitForPageLoad.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(claimsloader)));
							webElement2.click();
							Thread.sleep(1000);
						}
						catch(Exception e){*/
						WebElement webElement2= WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName+"[contains(text(),'"+uniqueNumber+"')]")));
						System.out.println(webElement2);
						WebDriverWait WaitForPageLoad=new WebDriverWait(Automation.driver,120);
						WaitForPageLoad.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(claimsloader)));
						webElement2.click();
						Thread.sleep(1000);
						/*}*/
						/*webElement.clear();
						webElement.sendKeys(uniqueNumber);*/
						break;
					case Write:
						if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes") || !(ctrlValue.trim().equalsIgnoreCase("")))
						{
							WebHelperUtil.writeToExcel(ctrlValue, webElement, controlId, controlType, controlName, rowNo, colNo);
						}
						else{
							System.out.println("Not needed");
						}
						break;
					case V:// WIP
						if(!(ctrlValue.equalsIgnoreCase(""))) {
							uniqueNumber = ReadFromExcel(ctrlValue);
							uniqueNumber = webElement.getText();
						}

						/*if(WebVerification.isFromVerification == true)
						{
							currentValue = webElement.getText();
							if(currentValue.equalsIgnoreCase(null) || currentValue.equalsIgnoreCase(""))
								currentValue = webElement.getAttribute("value");
							break;
						}
						boolean textPresent = false;
						textPresent = webElement.getText().contains(ctrlValue);						
						if(textPresent == false)
						{
							currentValue = Boolean.toString(textPresent);

						}
						else
						{
							currentValue = ctrlValue;

						}*/
						//break;

						//bhaskar Action Verify Values VV START 
					case T: //Mayur_Claims (Payment I Icon Verification)
						Reporter report =new Reporter();
						report.setReport(report);
						WebDriverWait WaitForPageLoad11=new WebDriverWait(Automation.driver,5);
						Thread.sleep(1000);
						WebElement logout= WaitForPageLoad11.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='v-label v-widget v-has-width' and contains(text(),'Pending Approval')]")));
						logout.click();

						String ActualValue1 = webElement.getText();
						String ExpectedValue1 = ctrlValue;

						String[] parts = ActualValue1.split(" ");
						String ActualValue= parts[0]+parts[1]+parts[2];

						String ExpectedValue= ExpectedValue1.replaceAll("\\s+","");

						log.info("ActualValue is : "+ActualValue);
						log.info("ExpectedValue is : "+ExpectedValue);

						if(ActualValue.contains(ExpectedValue))
						{
							report.setStatus("PASS");							
							report.setStatus(report.getStatus());
							report.setMessage("Values Matched");
							//WebHelper.saveScreenShot();
						}
						else
						{
							report.setStatus("FAIL");							
							report.setStatus(report.getStatus());
							report.setMessage("Values Not Matched");
							WebHelperUtil.saveScreenShot();
							controller.pauseFun("");
						}
						WebHelper.columns.add("");
						WebHelper.columnsData.add(WebHelper.columns);
						int temprowcount = 0;
						int tempcolcount = 0;
						ExcelUtility.WriteToCompareDetailResults(testCase,TransactionType,WebHelper.columns,WebHelper.columnsData,temprowcount,tempcolcount,report,ExpectedValue,ActualValue,logicalName,operationType,cycleDate);
						break;
						/*Thread.sleep(1000);
						Reporter report =new Reporter();
						report.setReport(report);
						try{
							String ActualValue = webElement.getText();
							String ExpectedValue = ctrlValue;

							log.info("ActualValue is : "+ActualValue);
							log.info("ExpectedValue is : "+ExpectedValue);
							//if(ActualValue.equalsIgnoreCase(ExpectedValue))

							if(ActualValue.contains(ExpectedValue))
							{
								report.getStrStatus() = "PASS";							
								report.setStrStatus(report.strStatus);
								report.setStrMessage("Values Matched");
							}
							else
							{
								report.strStatus = "FAIL";							
								report.setStrStatus(report.strStatus);
								report.setStrMessage("Values Not Matched");
								WebHelper.saveScreenShot();
								MainController.pauseFun("");	
							}
							columns.add("");
							columnsData.add(columns);
							int temprowcount = 0;
							int tempcolcount = 0;
							ExcelUtility.WriteToCompareDetailResults(testCase,TransactionType,columns,columnsData,temprowcount,tempcolcount,report,ExpectedValue,ActualValue,logicalName,operationType,cycleDate);
							break;
						}
						catch(Exception e){
							columns.add("");
							columnsData.add(columns);
							int temprowcount = 0;
							int tempcolcount = 0;
							ExcelUtility.WriteToCompareDetailResults(testCase,TransactionType,columns,columnsData,temprowcount,tempcolcount,report,ExpectedValue,ActualValue,logicalName,operationType,cycleDate);
							break;
						}
						 */
					default:
						break;
					}
					break;

				case JSScript:
					((JavascriptExecutor)WebHelper.currentdriver).executeScript(controlName, ctrlValue);
					break;

				case WaitForPageToLoad://Asif: Added 31-May 2017:Start
					switch(actionName)
					{
					case I: //Mayur_Claims
						if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes"))
						{
							//Automation.driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);

							WebDriverWait WaitForPageLoad=new WebDriverWait(Automation.driver,120);
							WaitForPageLoad.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(claimsloader)));
							WaitForPageLoad.until(ExpectedConditions.visibilityOf(WebHelper.currentdriver.findElement(By.xpath(controlName))));
							WaitForPageLoad.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName)));

						}
						break;
					case NC:
						Automation.driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);

						WebDriverWait WaitForPageLoad=new WebDriverWait(Automation.driver,300);

						WaitForPageLoad.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName)));

						break;
					default:
						break;

					}
					break;


				case Wait: // Implicit wait
					switch(actionName)
                    {
                    case NC:
                           Thread.sleep(Integer.parseInt(controlName)*1000);
                           //log.info("In Wait");
                           break;
                    case I:
                           if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes"))
                           {
                                  Thread.sleep(Integer.parseInt(controlName)*1000);
                           }
                           break;
					default:
						break;
                    }
                    break;

				case CheckBox:
					switch(actionName)
					{
					case I:
						if(ctrlValue.equalsIgnoreCase("") || StringUtils.isEmpty(ctrlValue))
						{
							break;
						}
						else if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes"))
						{

							if (!webElement.isSelected()) // Added By Dharmendra to Check whether CheckBox select or not
							{
								Thread.sleep(1000);
								webElement.click(); // Select Checkbox, if it is not selectd
								Thread.sleep(1000);
							}
						}

						else if (ctrlValue.equalsIgnoreCase("N") || ctrlValue.equalsIgnoreCase("No"))
						{
							Thread.sleep(1000);
							if (webElement.isSelected())  // Added By Dharmendra to Check whether CheckBox select or not
							{
								Thread.sleep(1000);
								webElement.click(); //  Deselect Checkbox, if it is selectd
								Thread.sleep(1000);
							}
						}
						break;
					case T:
						if(ctrlValue.equalsIgnoreCase("") || StringUtils.isEmpty(ctrlValue))
						{
							break;
						}
						else if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes"))
						{
							if (!webElement.isSelected()) // Added By Dharmendra to Check whether CheckBox select or not
							{
								Thread.sleep(1000);
								((JavascriptExecutor)WebHelper.currentdriver).executeScript("arguments[0].scrollIntoView();", webElement);
								Thread.sleep(1000);
								webElement.click(); // Select Checkbox, if it is not selectd
							}
						}

						else if (ctrlValue.equalsIgnoreCase("N") || ctrlValue.equalsIgnoreCase("No"))
						{
							if (webElement.isSelected())  // Added By Dharmendra to Check whether CheckBox select or not
							{
								Thread.sleep(1000);
								((JavascriptExecutor)WebHelper.currentdriver).executeScript("arguments[0].scrollIntoView();", webElement);
								Thread.sleep(1000);
								webElement.click(); //  Deselect Checkbox, if it is selectd
							}
						}
						break;
					case V:
						if(webElement.isSelected())
						{
							//log.info("logical Name is not Selected:"+logicalName);
							System.out.println(logicalName + "Is Selected");
							//currentValue = webElement.getAttribute(controlName.toString());
						}
						else{
							System.out.println(logicalName + "Is not Selected");
						}
						break;
					case NC:
						if (!webElement.isSelected())
						{
							webElement.click();
						}
						break;
					case Read: // Mayur_Claims for delete/void bulk
						uniqueNumber = ReadFromExcel(ctrlValue);
						try{
							WebElement webElement2= WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName+"[contains(text(),'"+uniqueNumber+"')]//following::td[@data-colid='Col_7']/div/div/input")));
							System.out.println(webElement2);
							webElement2.click();
							Thread.sleep(1000);
						}
						catch(Exception e){
							log.error(e.getMessage(), e);
							WebElement webElement1= WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='mainRegion']/div[@class='page-region']//div[@data-name='tabExpPayDetl_tab']//li[@class='last']/a")));
							webElement1.click();
							WebElement webElement2= WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName+"[contains(text(),'"+uniqueNumber+"')]//following::td[@data-colid='Col_7']/div/div/input")));
							System.out.println(webElement2);
							webElement2.click();
							Thread.sleep(1000);
						}
						/*webElement.clear();
						webElement.sendKeys(uniqueNumber);*/
						break;
					default:
						break;
					}
					break;

				case Radio:
					switch(actionName)
					{
					case I: 
					case V:
					case F:
						return WebHelper.doAction(FilePath, rowValues, testCase,
								imageType, controlType, controlId, controlName,
								ctrlValue, ctrlValue1, ctrlValue2, wscycledate,
								logicalName, action, webElement, Results,
								strucSheet, valSheet, rowIndex, rowcount, rowNo,
								colNo, operationType, cycleDate, TransactionType);

					case NC: //Mayur- To verify radio or check box selected or not			
						if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes"))
						{
							if(!webElement.isSelected())
							{						
								System.out.println("IS NOT SELECTED");
								log.info(logicalName+" is not Selected ");
								//webElement.click();
							}
							else{
								System.out.println("IS SELECTED");
								log.info(logicalName+" is Selected ");
							}
						}
						else
						{
							System.out.println("No action CHECKBOX/ RADIO Button");
						}
						break;
					case T: //Mayur_Select depending on the requirement
						if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes"))
						{
							if(!webElement.isSelected()){						
								webElement.click();
							}
							else{
								System.out.println("No radio found");
								//currentValue = webElement.getAttribute(controlName.toString());
							}
						}
						else
						{
							System.out.println("NO for this Scenario");
						}
						break;
					default:
						break;		
					}
					break;	

				case WebLink:
					switch(actionName)
					{
					case I: 
						if(ctrlValue.equalsIgnoreCase("B"))
						{
							//Suit Automation(WIP-Unassigned Claims)
							Reporter report =new Reporter();
							report.setReport(report);
							WebDriverWait WaitForPageLoad11=new WebDriverWait(Automation.driver,10);
							Thread.sleep(1000);
							WebElement WIPUnssignedClaims= WaitForPageLoad11.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='v-label v-widget v-has-width' and contains(text(),'Unassigned Claims')]")));
							//logout.click();
							String ActualValue1 = WIPUnssignedClaims.getText();
							String[] parts = ActualValue1.split(" ");
							String ActualValue= parts[0];

							WIPUnssignedClaims.click();

							WebElement ExpectedValue1= WaitForPageLoad11.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='totalrec']//label[@data-section='record_count']")));
							String ExpText = ExpectedValue1.getText();
							String[] ExpTextparts = ExpText.split(" ");
							String ExpectedValue= ExpTextparts[4];

							log.info("ActualValue is : "+ActualValue);
							log.info("ExpectedValue is : "+ExpectedValue);

							if(ActualValue.contains(ExpectedValue))
							{
								report.setStatus("PASS");							
								report.setStatus(report.getStatus());
								report.setMessage("Values Matched");
								//WebHelper.saveScreenShot();
							}
							else
							{
								report.setStatus("FAIL");							
								report.setStatus(report.getStatus());
								report.setMessage("Values Not Matched");
								WebHelperUtil.saveScreenShot();
								//MainController.pauseFun("");
							}
							WebHelper.columns.add("");
							WebHelper.columnsData.add(WebHelper.columns);
							int temprowcount = 0;
							int tempcolcount = 0;
							ExcelUtility.WriteToCompareDetailResults(testCase,TransactionType,WebHelper.columns,WebHelper.columnsData,temprowcount,tempcolcount,report,ExpectedValue,ActualValue,logicalName,operationType,cycleDate);
							break;
						}
						else if(ctrlValue.equalsIgnoreCase("A"))
						{
							//Suit Automation(WIP-Pending Approval)
							Reporter report =new Reporter();
							report.setReport(report);
							WebDriverWait WaitForPageLoad11=new WebDriverWait(Automation.driver,10);
							Thread.sleep(1000);
							WebElement WIPPendingApproval= WaitForPageLoad11.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='v-label v-widget v-has-width' and contains(text(),'Pending Approval')]")));
							//logout.click();
							String ActualValue1 = WIPPendingApproval.getText();
							String[] parts = ActualValue1.split(" ");
							String ActualValue= parts[0];

							WIPPendingApproval.click();
							WebElement PendingForMyApprovalClick= WaitForPageLoad11.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[@data-labelkey='mm.icd.Approval.PendingForApprovals']")));
							PendingForMyApprovalClick.click();

							WebElement ReserveString= WaitForPageLoad11.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='dspPendingReserve']//span[@data-section='displayvalue']")));
							String ExpText1 = ReserveString.getText();
							String[] ExpText1parts = ExpText1.split(" ");
							String ExpPart1= ExpText1parts[1];
							int beginInd = ExpPart1.indexOf("(");
							int endInd = ExpPart1.indexOf(")");
							String SumText1 = ExpPart1.substring(beginInd+1,endInd);
							int SumNum1 = Integer.parseInt(SumText1);

							WebElement PaymentsString= WaitForPageLoad11.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@id='dspPendingPayments']//span[@data-section='displayvalue']")));
							String ExpText2 = PaymentsString.getText();
							String[] ExpText2parts = ExpText2.split(" ");
							String ExpPart2= ExpText2parts[1];
							int beginInd2 = ExpPart2.indexOf("(");
							int endInd2 = ExpPart2.indexOf(")");
							String SumText2 = ExpPart2.substring(beginInd2+1,endInd2);
							int SumNum2 = Integer.parseInt(SumText2);

							int Sum= SumNum1 + SumNum2;
							String ExpectedValue= String.valueOf(Sum);

							log.info("ActualValue is : "+ActualValue);
							log.info("ExpectedValue is : "+ExpectedValue);

							if(ActualValue.contains(ExpectedValue))
							{
								report.setStatus("PASS");							
								report.setStatus(report.getStatus());
								report.setMessage("Values Matched");
								//WebHelper.saveScreenShot();
							}
							else
							{
								report.setStatus("FAIL");							
								report.setStatus(report.getStatus());
								report.setMessage("Values Not Matched");
								WebHelperUtil.saveScreenShot();
								//MainController.pauseFun("");
							}
							WebHelper.columns.add("");
							WebHelper.columnsData.add(WebHelper.columns);
							int temprowcount = 0;
							int tempcolcount = 0;
							ExcelUtility.WriteToCompareDetailResults(testCase,TransactionType,WebHelper.columns,WebHelper.columnsData,temprowcount,tempcolcount,report,ExpectedValue,ActualValue,logicalName,operationType,cycleDate);
						}
						else if(ctrlValue.equalsIgnoreCase("C"))
						{
							{
								//Suit Automation(WIP-Pending FNOL)
								Reporter report1 =new Reporter();
								report1.setReport(report1);
								Thread.sleep(1000);
								WebDriverWait WaitForPageLoad12=new WebDriverWait(Automation.driver,10);
								WebElement WIPPendingFNOL= WaitForPageLoad12.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='v-label v-widget v-has-width' and contains(text(),'Pending FNOL')]")));
								//logout.click();
								String ActualValue2 = WIPPendingFNOL.getText();
								String[] PFParts = ActualValue2.split(" ");
								String PFActualValue= PFParts[0];

								WIPPendingFNOL.click();

								WebElement ExpectedValue2= WaitForPageLoad12.until(ExpectedConditions.elementToBeClickable(By.xpath("//div[@class='totalrec']//label[@data-section='record_count']")));
								String ExpText2 = ExpectedValue2.getText();
								String[] PFExpTextparts = ExpText2.split(" ");
								String PFExpectedValue= PFExpTextparts[4];

								log.info("ActualValue is : "+PFActualValue);
								log.info("ExpectedValue is : "+PFExpectedValue);

								if(PFActualValue.contains(PFExpectedValue))
								{
									report1.setStatus("PASS");							
									report1.setStatus(report1.getStatus());
									report1.setMessage("Values Matched");
									//WebHelper.saveScreenShot();
								}
								else
								{
									report1.setStatus("FAIL");							
									report1.setStatus(report1.getStatus());
									report1.setMessage("Values Not Matched");
									WebHelperUtil.saveScreenShot();
									//MainController.pauseFun("");
								}
								WebHelper.columns.add("");
								WebHelper.columnsData.add(WebHelper.columns);
								int temprowcount = 0;
								int tempcolcount = 0;
								ExcelUtility.WriteToCompareDetailResults(testCase,TransactionType,WebHelper.columns,WebHelper.columnsData,temprowcount,tempcolcount,report1,PFExpectedValue,PFActualValue,logicalName,operationType,cycleDate);									
								break;
							}
						}
					default:
						break;
					}
					break;
				case CloseWindow://added this Case to bypass page loading after clicking the event
				case WaitForJS:
				case ListBox:
				case WebList:					
					return WebHelper.doAction(FilePath, rowValues, testCase,
							imageType, controlType, controlId, controlName,
							ctrlValue, ctrlValue1, ctrlValue2, wscycledate,
							logicalName, action, webElement, Results,
							strucSheet, valSheet, rowIndex, rowcount, rowNo,
							colNo, operationType, cycleDate, TransactionType);
					
				case AjaxWebList:
					switch (actionName) {
					case I:
						WebDriverWait WaitForPageLoad = new WebDriverWait(
								Automation.driver, 30);
						WaitForPageLoad.until(ExpectedConditions
								.invisibilityOfElementLocated(By
										.xpath(claimsloader)));
						WaitForPageLoad.until(ExpectedConditions
								.visibilityOf(webElement));
						WaitForPageLoad.until(ExpectedConditions
								.elementToBeClickable(webElement));
						Thread.sleep(200);
						webElement.click();
						// Thread.sleep(300);
						break;
					case VA:
						return WebHelper.doAction(FilePath, rowValues, testCase,
								imageType, controlType, controlId, controlName,
								ctrlValue, ctrlValue1, ctrlValue2, wscycledate,
								logicalName, action, webElement, Results,
								strucSheet, valSheet, rowIndex, rowcount, rowNo,
								colNo, operationType, cycleDate, TransactionType);
					default:
						break;
					}
					break;

				case IFrame:
					log.info("In method doaction debug4");
					if (controlName.startsWith("//iframe")) {
						WebDriverWait wait1 = new WebDriverWait(Automation.driver,20);
						wait1.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));
						Automation.driver.switchTo().frame(Automation.driver.findElement(By.xpath(controlName)));
					}
					else {
						Automation.driver.switchTo().frame(controlName);
					}
					//currentdriver = currentdriver.switchTo().frame(controlName);
					log.info("In method doaction debug5");
					break;

				case Multiselect : //Mayur_ MultiSelect/Deselct for JS (Ajax) Claims
					switch(actionName)
					{
					case I:
						if(!ctrlValue.equalsIgnoreCase("null")){

							if(!ctrlValue.contains(",")){
								webElement= WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName+"[contains(text(),'"+ctrlValue+"')]")));															
								//((JavascriptExecutor)currentdriver).executeScript("arguments[0].scrollIntoView();", webElement);
								WebDriverWait WaitForPageLoad=new WebDriverWait(Automation.driver,30);
								WaitForPageLoad.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(claimsloader)));
								WaitForPageLoad.until(ExpectedConditions.visibilityOf(webElement));
								WaitForPageLoad.until(ExpectedConditions.elementToBeClickable(webElement));
								webElement.click();
							}

							else{
								String string = ctrlValue;
								String[] parts = string.split(",");
								String partWebelemt= parts[0];
								for (int i = 1; i < parts.length; i++) {
									WebElement	webElement1= WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName+"[contains(text(),'"+parts[i]+"')]")));
									WebDriverWait WaitForPageLoad=new WebDriverWait(Automation.driver,30);
									WaitForPageLoad.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(claimsloader)));
									webElement1.click();
									if (i<parts.length-1){
										webElement= WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(partWebelemt)));
										WaitForPageLoad.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(claimsloader)));
										WaitForPageLoad.until(ExpectedConditions.visibilityOf(webElement));
										WaitForPageLoad.until(ExpectedConditions.elementToBeClickable(webElement));
										webElement.click();
										/*Thread.sleep(1000);
										robot = new Robot();
										robot.keyPress(KeyEvent.VK_ENTER);
										robot.keyRelease(KeyEvent.VK_ENTER);
										Thread.sleep(1000);*/
									}
								}
							}
						}
						else
						{
							System.out.println("No Data Entered");
						}
						break;

					case T: //Mayur - Deselect multi choice
						if(!(ctrlValue.equalsIgnoreCase(""))){
							/*if(!ctrlValue.contains(",")){
								webElement= wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName+"[@title='"+ctrlValue+"']//span[@class='select2-selection__choice__remove']")));															
								webElement.click();
							}
							else{*/
							String string = ctrlValue;
							String[] parts = string.split(",");
							String partWebelemt= parts[0];
							for (int i = 1; i < parts.length; i++) {
								//webElement= wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName+"[@title='"+parts[i]+"']//span[@class='select2-selection__choice__remove']")));
								webElement= WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName+"[contains(@title,'"+parts[i]+"')]//span[@class='select2-selection__choice__remove']")));
								WebDriverWait WaitForPageLoad=new WebDriverWait(Automation.driver,30);
								WaitForPageLoad.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(claimsloader)));
								WaitForPageLoad.until(ExpectedConditions.visibilityOf(webElement));
								WaitForPageLoad.until(ExpectedConditions.elementToBeClickable(webElement));
								webElement.click();
								Thread.sleep(1000);
								if (i==parts.length-1) {
									webElement= WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath(partWebelemt)));
									WaitForPageLoad.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(claimsloader)));
									WaitForPageLoad.until(ExpectedConditions.visibilityOf(webElement));
									WaitForPageLoad.until(ExpectedConditions.elementToBeClickable(webElement));
									webElement.click();
								}
							}
							//}

							/*robot = new Robot();
							robot.keyPress(KeyEvent.VK_TAB);
							robot.keyRelease(KeyEvent.VK_TAB);*/
						}
						else
						{
							System.out.println("No Data Entered");
						}
					default:
						break;
					}
					break;

				case Browser:
					//Thread.sleep(3000); //DS:Check if required
					switch(actionName)
					{
					case I: 
						Set<String> handlers = null;
						handlers = WebHelper.currentdriver.getWindowHandles();
						for(String handler : handlers)
						{
							WebHelper.currentdriver =WebHelper.currentdriver.switchTo().window(handler);

							//TM-19/01/2015: Changed following comparison from equalsIgnoreCase to contains
							if (WebHelper.currentdriver.getTitle().contains(controlName))
							{
								log.info("Focus on window with title: "+ WebHelper.currentdriver.getTitle());					
								break;
							}
						}					
						break;
					default:
						break;
					}
					break;

				case NewBrowser://Mayur: Added 31-May :Start
					switch(actionName)
                    {
                    case I: //Mayur_Extra verification...
                           if(!ctrlValue.equalsIgnoreCase("null"))
                           {
                                  String parentWindow = Automation.driver.getWindowHandle();
                                  Set<String> handles =  Automation.driver.getWindowHandles();
                                  for(String windowHandle  : handles)
                                  {
                                         if(!windowHandle.equals(parentWindow))
                                         {
                                                Automation.driver.switchTo().window(windowHandle);
                                         }
                                  }
                           }
                           break;
                    case NC:
                           if(!ctrlValue.equalsIgnoreCase("null"))
                           {
                                  String parentWindow1 = Automation.driver.getWindowHandle();
                                  Set<String> handles1 =  Automation.driver.getWindowHandles();
                                  for(String windowHandle1  : handles1)
                                  {
                                         if(!windowHandle1.equals(parentWindow1))
                                         {
                                                Automation.driver.switchTo().window(windowHandle1);
                                                Thread.sleep(1000);
                                                Automation.driver.close();
                                         }
                                  }
                                  Automation.driver.switchTo().window(parentWindow1);
                           }
                           break;
                    }
                    break;//case NewBrowser://Mayur
					

				case CloseBrowser://Mayur
					switch(actionName)
					{
					case I:
						if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes") || !(ctrlValue.trim().equalsIgnoreCase("")))
						{
							Automation.driver.close();
						}
						break;//case
					default:
						break;
					}
					break;


				case URL:
					switch(actionName)
					{
					case I:					
						WebHelper.currentdriver.navigate().to(ctrlValue);
						break;
					case NC:	
						if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes") || !(ctrlValue.trim().equalsIgnoreCase("")))
						{
							WebHelper.currentdriver.navigate().to(controlName);
							WebHelper.currentdriver.navigate().refresh();
							break;
						}
					default:
						break;
					}
					break;

				case Menu:
				case Alert:
				case WebImage:
				case ActionClick:
				case ActionDoubleClick:
				case ActionClickandEsc:					
					return WebHelper.doAction(FilePath, rowValues, testCase,
							imageType, controlType, controlId, controlName,
							ctrlValue, ctrlValue1, ctrlValue2, wscycledate,
							logicalName, action, webElement, Results,
							strucSheet, valSheet, rowIndex, rowcount, rowNo,
							colNo, operationType, cycleDate, TransactionType);

				case ActionMouseOver:
					switch(actionName){
					case I:
						if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("Yes") || !(ctrlValue.trim().equalsIgnoreCase("")))
						{
							WebDriverWait WaitForPageLoad=new WebDriverWait(Automation.driver,30);
							WaitForPageLoad.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(claimsloader)));
							WaitForPageLoad.until(ExpectedConditions.visibilityOf(webElement));
							WaitForPageLoad.until(ExpectedConditions.elementToBeClickable(webElement));
							Actions builderMouserOver = new Actions(WebHelper.currentdriver);
							builderMouserOver.moveToElement(webElement).build().perform();
							//Thread.sleep(1000);
							//Action mouseOverAction = builderMouserOver.moveToElement(webElement).build();
							//mouseOverAction.perform();
						}
					default:
						break;
					}
					break;

				case Calendar:
				case CalendarNew:
				case CalendarIPF:
				case CalendarEBP:
					return WebHelper.doAction(FilePath, rowValues, testCase,
							imageType, controlType, controlId, controlName,
							ctrlValue, ctrlValue1, ctrlValue2, wscycledate,
							logicalName, action, webElement, Results,
							strucSheet, valSheet, rowIndex, rowcount, rowNo,
							colNo, operationType, cycleDate, TransactionType);

				case Window:
					switch(actionName)
					{							
					case O:					
						return WebHelper.doAction(FilePath, rowValues, testCase,
								imageType, controlType, controlId, controlName,
								ctrlValue, ctrlValue1, ctrlValue2, wscycledate,
								logicalName, action, webElement, Results,
								strucSheet, valSheet, rowIndex, rowcount, rowNo,
								colNo, operationType, cycleDate, TransactionType);
					case I: //Mayur_ Claims_handle AJAX pop-up
						if(ctrlValue.equalsIgnoreCase("Y") || ctrlValue.equalsIgnoreCase("D") || ctrlValue.equalsIgnoreCase("C")|| ctrlValue.equalsIgnoreCase("Ok") || ctrlValue.equalsIgnoreCase("Ok1") || !(ctrlValue.trim().equalsIgnoreCase("")))
						{
							if(ctrlValue.equalsIgnoreCase("Y")){
								try
								{ 
									webElement= WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@name='OK']")));															
									webElement.click();
									log.info("Pop-up found on the web page" +"ctrlValue");
									Thread.sleep(1000);
								}
								catch(Exception e){
									log.error(e.getMessage(), e);
									webElement= WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@name='btnOk']")));															
									webElement.click();
									log.info("Pop-up found on the web page" +"ctrlValue");
									Thread.sleep(1000);
								}	
							}
							else if((ctrlValue.equalsIgnoreCase("D"))){
								try
								{
									WebDriverWait WaitForPageLoad=new WebDriverWait(Automation.driver,7);
									WebElement webElement1= WaitForPageLoad.until(ExpectedConditions.elementToBeClickable(By.name("btnProceedWithFNOL")));
									//webElement= wait.until(ExpectedConditions.elementToBeClickable(By.name("btnProceedWithFNOL")));
									webElement1.click();
									log.info("Pop-up found on the web page" +"ctrlValue");
									Thread.sleep(1000);
								}
								catch(Exception e){
									log.error(e.getMessage(), e);
									System.out.println("NO duplicate pop up found");
								}		
							}
							else if((ctrlValue.equalsIgnoreCase("C"))){
								webElement= WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@name='Cancel']")));															
								webElement.click();
								log.info("Pop-up found on the web page" +"ctrlValue");
								Thread.sleep(1000);
							}
							else if((ctrlValue.equalsIgnoreCase("Ok"))){
								webElement= WebHelper.wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@name='btnOk']")));															
								webElement.click();
								log.info("Pop-up found on the web page" +"ctrlValue");
								Thread.sleep(1000);
							}
							else if((ctrlValue.equalsIgnoreCase("Ok1"))){
								try
								{
									WebDriverWait WaitForPageLoad=new WebDriverWait(Automation.driver,7);
                                    //WebElement webElement1= WaitForPageLoad.until(ExpectedConditions.elementToBeClickable(By.name("btnProceedWithFNOL")));
                                    WebElement webElement1= WaitForPageLoad.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@name='OK']")));
                                    webElement1.click();
                                    log.info("Pop-up found on the web page" +"ctrlValue");
								}
								catch(Exception e){
									log.error(e.getMessage(), e);
									System.out.println("NO Pop up found");
								}		
							}
							else{
								System.out.println("NO window Found");
							}
						}
						break;
					default:
						break;
					}
					break;

				case WebTable:
					switch(actionName)
					{
					case Read:
					case Write:
					case NC:
					case V:
						return WebHelper.doAction(FilePath, rowValues, testCase,
								imageType, controlType, controlId, controlName,
								ctrlValue, ctrlValue1, ctrlValue2, wscycledate,
								logicalName, action, webElement, Results,
								strucSheet, valSheet, rowIndex, rowcount, rowNo,
								colNo, operationType, cycleDate, TransactionType);

					case TableInput:
						WebHelper.findtablefound = WebHelper.currentdriver.findElements(By.xpath(controlName)).size() > 0;
						if(WebHelper.findtablefound == true)
						{
							WebElement tableFound = WebHelper.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));
							BillingProduct.TableInputAction(tableFound, controlName, logicalName, rowValues, WebHelper.valuesHeader,ExcelUtility.TIvaluesheetrows);							
							Thread.sleep(1000);
						}
						else
						{
							break;
						}
						break;

					case FIND:
						WebHelper.findtablefound = WebHelper.currentdriver.findElements(By.xpath(controlName)).size() > 0;
						if(WebHelper.findtablefound == true)
						{
							WebElement tableFound = WebHelper.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));
							BillingProduct.findAction(tableFound,controlName,logicalName,rowValues,WebHelper.valuesHeader);
							Thread.sleep(1000);
						}
						else
						{
							break;
						}
						break;

					case I:
						WebHelper.findtablefound = WebHelper.currentdriver.findElements(By.xpath(controlName)).size() > 0;
						if(WebHelper.findtablefound == true)
						{
							WebElement tableFound = WebHelper.wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(controlName)));
							List<WebElement> table_Rows =  tableFound.findElements(By.tagName("tr"));
							List<WebElement> table_Columns =  table_Rows.get(1).findElements(By.tagName("td"));

							int ApplicationtableRowsize = table_Rows.size();                   //ApplicationtableRowsize = no of rows in the WebTable
							int Applicationtablecolumnsize = table_Columns.size();             //Applicationtablecolumnsize = no of columns in the WebTable

							String ColumnName = ctrlValue.split(",")[0];
							String ColumnType = ctrlValue.split(",")[1];

							for(int i=1;i<=Applicationtablecolumnsize;i++)
							{
								Thread.sleep(1000);	
								String ApplicationColumnHeaderxapth = controlName+"/thead/tr/th["+i+"]";
								log.info("ApplicationColumnHeader is:"+ApplicationColumnHeaderxapth);
								WebElement element = WebHelper.currentdriver.findElement(By.xpath(ApplicationColumnHeaderxapth));								
								String ApplicationColumnHeader = element.getText();								
								if((ColumnName).equalsIgnoreCase(ApplicationColumnHeader))
								{
									for(int r=1; r<=ApplicationtableRowsize; r++)
									{
										if(ColumnType.equalsIgnoreCase("Webcheckbox"))
										{
											String XPath = controlName+"/tbody/tr["+r+"]/td["+i+"]/div/div/input";											
											WebHelper.objfound = WebHelper.currentdriver.findElements(By.xpath(XPath)).size() > 0;
											if(WebHelper.objfound == true)
											{
												WebElement newelement = WebHelper.currentdriver.findElement(By.xpath(XPath));
												newelement.click();	
												Thread.sleep(500);
												((JavascriptExecutor)WebHelper.currentdriver).executeScript("arguments[0].scrollIntoView();", newelement);
												Thread.sleep(500);
												WebHelper.objfound = false;
											}

										}
										else if(ColumnType.equalsIgnoreCase("WebLink"))
										{
											String XPath = controlName+"/tbody/tr["+r+"]/td["+i+"]/div/span";											
											WebHelper.objfound = WebHelper.currentdriver.findElements(By.xpath(XPath)).size() > 0;
											if(WebHelper.objfound == true)
											{
												WebElement newelement = WebHelper.currentdriver.findElement(By.xpath(XPath));
												log.info("link xpath " + XPath);
												newelement.click();
												Thread.sleep(500);
												((JavascriptExecutor)WebHelper.currentdriver).executeScript("arguments[0].scrollIntoView();", newelement);
												Thread.sleep(500);
												WebHelper.objfound = false;
											}
										}
										else if(ColumnType.equalsIgnoreCase("WebCheckBox"))
										{
											// not encountered
										}
									}
								}																								
							}			

						}
						break;
					default:
						break;
					}
					break;

					//bhaskar capture screenshot START
				case Screenshot:
					switch (actionName) {
					case NC:
						return WebHelper.doAction(FilePath, rowValues, testCase,
								imageType, controlType, controlId, controlName,
								ctrlValue, ctrlValue1, ctrlValue2, wscycledate,
								logicalName, action, webElement, Results,
								strucSheet, valSheet, rowIndex, rowcount, rowNo,
								colNo, operationType, cycleDate, TransactionType);
					default:
						break;
					}
					break;

				case Robot:
					if(controlName.equalsIgnoreCase("SetFilePath"))
					{
						StringSelection stringSelection = new StringSelection(ctrlValue);					
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);	
						WebHelper.robot.delay(2000);
						WebHelper.robot.keyPress(KeyEvent.VK_CONTROL);
						WebHelper.robot.keyPress(KeyEvent.VK_V);
						WebHelper.robot.keyRelease(KeyEvent.VK_V);
						WebHelper.robot.keyRelease(KeyEvent.VK_CONTROL);
						WebHelper.robot.keyPress(KeyEvent.VK_ENTER);
						WebHelper.robot.keyRelease(KeyEvent.VK_ENTER);
						Thread.sleep(2000);
					}
					else if(controlName.equalsIgnoreCase("TAB"))
					{
						WebHelper.robot.keyPress(KeyEvent.VK_TAB);
						WebHelper.robot.keyRelease(KeyEvent.VK_TAB);	
					}
					else if(controlName.equalsIgnoreCase("SPACE"))
					{
						WebHelper.robot.keyPress(KeyEvent.VK_SPACE);	  				
						WebHelper.robot.keyRelease(KeyEvent.VK_SPACE);
					}
					else if(controlName.equalsIgnoreCase("ENTER"))
					{
						WebHelper.robot.keyPress(KeyEvent.VK_ENTER);
						WebHelper.robot.keyRelease(KeyEvent.VK_ENTER);
						Thread.sleep(3000);
					}
					break;

				case DB:
				case WaitForEC:
				case SikuliScreen:
				case SikuliType:
				case SikuliButton:
				case Slider:
				case Date:						
					return WebHelper.doAction(FilePath, rowValues, testCase,
							imageType, controlType, controlId, controlName,
							ctrlValue, ctrlValue1, ctrlValue2, wscycledate,
							logicalName, action, webElement, Results,
							strucSheet, valSheet, rowIndex, rowcount, rowNo,
							colNo, operationType, cycleDate, TransactionType);

				case MaskedInputDate:
					if(!ctrlValue.equalsIgnoreCase("null"))
					{						
						webElement.clear();
						webElement.click();
						webElement.sendKeys(ctrlValue);

					}
					else
					{
						webElement.clear();
					}
					break;
					//bhaskar

				case FileUpload:
					webElement.sendKeys(ctrlValue);
					break;		

				case ScrollTo:						
					/*Locatable element = (Locatable) webElement;
				Point p= element.getCoordinates().onScreen();
				JavascriptExecutor js = (JavascriptExecutor) currentdriver;  
				js.executeScript("window.scrollTo(" + p.getX() + "," + (p.getY()+150) + ");");	*/

				case NC: //ScrollUp_Mayur
					if(!ctrlValue.equalsIgnoreCase("")){
						if(ctrlValue.equalsIgnoreCase("Up")){
							JavascriptExecutor jse = (JavascriptExecutor)WebHelper.currentdriver;
							jse.executeScript("window.scrollBy(0,-250)", "");
							Thread.sleep(1000);
							break;
						}
						else if(ctrlValue.equalsIgnoreCase("Down")){		
							JavascriptExecutor jse = (JavascriptExecutor)WebHelper.currentdriver;
							jse.executeScript("window.scrollBy(0,250)", "");
							Thread.sleep(1000);
							break;
						}
						else if(ctrlValue.equalsIgnoreCase("DoubleDown")){		
							JavascriptExecutor jse = (JavascriptExecutor)WebHelper.currentdriver;
							jse.executeScript("window.scrollBy(0,775)", "");
							Thread.sleep(1000);
							break;
						}
						else if(ctrlValue.equalsIgnoreCase("DoubleUp")){		
							JavascriptExecutor jse = (JavascriptExecutor)WebHelper.currentdriver;
							jse.executeScript("window.scrollBy(0,-775)", "");
							Thread.sleep(1000);
							break;
						}
						else{
							System.out.println("Invalid contol value");		
						}
					}
					else{
						System.out.println("no scroll");
					}
					break;

				case I:
					JavascriptExecutor jsc = (JavascriptExecutor)WebHelper.currentdriver;
					jsc.executeScript("window.scrollBy(0,250)", "");
					/*try{ //Asif:Added 31-May:Start
						if(webElement.isDisplayed()){
							Coordinates coordinate = ((Locatable)webElement).getCoordinates(); 
							coordinate.onPage(); 
							coordinate.inViewPort();
						}
					}catch(Exception e){
						System.out.println("Element does not exist");

					}//Asif:Added 31-May:End
					 */
					break;	

				case WebService:	//devishree
				case WebService1:   
				case WebService2:  
				case WebService3:  
				case WebServiceV:  
				case WebServiceC:  
				case WebServiceRP:  
				case WebServiceV1:  
				case WebServiceV2:  
				case WebServiceVAG:  
				case WebServiceV3:  
					return WebHelper.doAction(FilePath, rowValues, testCase,
							imageType, controlType, controlId, controlName,
							ctrlValue, ctrlValue1, ctrlValue2, wscycledate,
							logicalName, action, webElement, Results,
							strucSheet, valSheet, rowIndex, rowcount, rowNo,
							colNo, operationType, cycleDate, TransactionType);

				default:
					log.info("U r in Default");
					break;
				}
			}
			catch(WebDriverException we)
			{
				//throw new Exception("Error Occurred from Do Action "+controlName + we.getMessage());
				//iTAFSeleniumWeb.WebDriver.report.strMessage = we.getMessage();
				webDriver.getReport().setMessage(we.getLocalizedMessage());
				webDriver.getReport().setStatus("FAIL");
				controller.pauseFun("Element is not visible Control ID" + controlTypeEnum + " <-|-> controlName :" + controlName + "<-|-> LocalizeMessage " + we.getLocalizedMessage() +" <-|-> Message "+ we.getMessage() +" <-|-> Cause "+ we.getCause());
				log.error("Element is not visible Control ID" + controlTypeEnum + " <-|-> controlName :" + controlName + "<-|-> LocalizeMessage " + we.getLocalizedMessage() +" <-|-> Message "+ we.getMessage() +" <-|-> Cause "+ we.getCause(), we);
				StartRecoveryClaims.initiateRecovery();
				throw new WebDriverException("Element is not visible Control ID " + controlTypeEnum + " <-|-> controlName :" + controlName + " <-|-> LocalizeMessage " + we.getLocalizedMessage() +" <-|-> Message"+ we.getMessage() +" <-|-> Cause "+ we.getCause());	
			}

			/*			catch(InterruptedException inte)
		{
			iTAFSeleniumWeb.WebDriver.report.setStrMessage(inte.getLocalizedMessage());
			iTAFSeleniumWeb.WebDriver.report.setStrStatus("FAIL");
			MainController.pauseFun(inte.getMessage());
		}*/
			catch(IOException ioe)
			{
				webDriver.getReport().setMessage(ioe.getLocalizedMessage());
				webDriver.getReport().setStatus("FAIL");
				controller.pauseFun("IOException is thrown in doAction <-|-> LocalizeMessage " + ioe.getLocalizedMessage() +" <-|-> Message "+ ioe.getMessage() +" <-|-> Cause "+ ioe.getCause());
				log.error("IOException is thrown in doAction <-|-> LocalizeMessage " + ioe.getLocalizedMessage() +" <-|-> Message "+ ioe.getMessage() +" <-|-> Cause "+ ioe.getCause(), ioe);
				throw new WebDriverException("IOException is thrown in doAction <-|-> LocalizeMessage " + ioe.getLocalizedMessage() +" <-|-> Message"+ ioe.getMessage() +" <-|-> Cause "+ ioe.getCause());
			}
			catch(Exception e)
			{
				//throw new Exception(e.getMessage());
				webDriver.getReport().setMessage(e.getMessage());
				webDriver.getReport().setMessage(e.getLocalizedMessage());
				webDriver.getReport().setStatus("FAIL");
				controller.pauseFun(e.getMessage());
				log.error("Exception in doAction <-|-> LocalizeMessage " + e.getLocalizedMessage() +" <-|-> Message "+ e.getMessage() +" <-|-> Cause "+ e.getCause(), e);
				throw new WebDriverException("Exception in doAction <-|-> LocalizeMessage " + e.getLocalizedMessage() +" <-|-> Message"+ e.getMessage() +" <-|-> Cause "+ e.getCause());
			}
		}
		//TM-02/02/2015: Radio button found ("F") & AJAX control ("VA")

		if((action.toString().equalsIgnoreCase("V")||action.toString().equalsIgnoreCase("F")||action.toString().equalsIgnoreCase("VA")) && !ctrlValue.equalsIgnoreCase(""))
		{
			if(Results == true)
			{
				webDriver.setReport(WebHelperUtil.WriteToDetailResults(ctrlValue, currentValue, logicalName));
			}
		}

		return currentValue;

	}
	public static String getMonth() {
		return WebHelper.month;
	}

	static String ReadFromExcel(String controlValue) throws IOException {
		return WebHelperUtil.ReadFromExcel(controlValue, WebHelper.columnName);
	}

}