package com.majesco.itaf.recovery;


import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;

import com.majesco.itaf.main.Automation;
import com.majesco.itaf.main.Config;
import com.majesco.itaf.main.ITAFWebDriver;
import com.majesco.itaf.main.MainController;
import com.majesco.itaf.main.ObjectFactory;
import com.majesco.itaf.main.TransactionMapping;
import com.majesco.itaf.main.WebHelper;
import com.sun.corba.se.impl.orbutil.threadpool.TimeoutException;

public class StartRecoveryClaims 
{
	private final static Logger log = Logger.getLogger(StartRecoveryClaims.class);
	private static ITAFWebDriver webDriver = ITAFWebDriver.getInstance();
	private static MainController controller = ObjectFactory.getMainController();

	public static void initiateRecovery()
	{	
		String AlertMsg = null;
		String webPagebodyText=null;
		Set<String> allWindowHandles =null;
		StartRecoveryClaims.takeScreenShot();
		WebDriver driver = Automation.driver;
		String mainWindowHandle= driver.getWindowHandle();
		String modalMsgText= isModalPopupPresent();
		if(isAlertPresent())
		{
			AlertMsg=Automation.driver.switchTo().alert().getText().toString();
		}else if(modalMsgText != null && (modalMsgText.toUpperCase().contains("SYSTEM ERROR")|modalMsgText.toUpperCase().contains("YOUR SEARCH RESULTED IN")|modalMsgText.toUpperCase().contains("NO DATA")|modalMsgText.toUpperCase().contains("ENTER VALID DATA")))
		{
			AlertMsg = modalMsgText;
		}

		if(AlertMsg!=null && (AlertMsg.toUpperCase().contains("SYSTEM ERROR")|AlertMsg.toUpperCase().contains("YOUR SEARCH RESULTED IN")|AlertMsg.toUpperCase().contains("NO DATA")|modalMsgText.toUpperCase().contains("ENTER VALID DATA")))
		{	
			if(modalMsgText!=null && AlertMsg.toUpperCase().contains("SYSTEM ERROR")) //Mayur Gujjul
			{
				//Automation.driver.findElement(By.name("Ok")).click();
				System.out.println("System error Handling_Claims");
				try {
					WebElement ErrMsg;
					ErrMsg = Automation.driver.findElement(By.xpath("//button[@class='btn btn-popup' and @name='OK']"));
					//ErrMsg = WebHelper.getElementByType("XPath", ".//*[@class='btn btn-popup' and @name='Ok']","","","");
					((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", ErrMsg);
					ErrMsg.click();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
				//MainController.recoveryhandler(); 
			}

			if(modalMsgText!=null && AlertMsg.toUpperCase().contains("NO DATA")) //Mayur Gujjul
			{
				//Automation.driver.findElement(By.name("Ok")).click();
				System.out.println("System error/Blank Data Handling_Claims");				
			}

			else if(modalMsgText!=null)   // 1 
			{
				//Automation.driver.findElement(By.name("Ok")).click();
				System.out.println("inside 1");
				try {
					WebElement ErrMsg;
					ErrMsg = Automation.driver.findElement(By.xpath("//button[@class='btn btn-popup' and @name='OK']"));
					//ErrMsg = WebHelper.getElementByType("XPath", ".//*[@class='btn btn-popup' and @name='Ok']","","","");
					((JavascriptExecutor)Automation.driver).executeScript("arguments[0].scrollIntoView();", ErrMsg);
					ErrMsg.click();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}


				controller.recoveryhandler(); // for above 3 conditions recovery scenario should get triggered.
			}
			else  // 2
			{
				System.out.println("inside 2");
				Automation.driver.switchTo().alert().accept();
				controller.recoveryhandler(); // for above 3 conditions recovery scenario should get triggered.
				try
				{
					Automation.setUp();
					TransactionMapping.TransactionInputData("Login");  //Default Login START
				}
				catch(Exception e)
				{
					log.error(e.getMessage(), e);
					System.out.println("Failed to login into application");
				}
			}
		}
		else if(isAlertPresent())
		{
			Automation.driver.switchTo().alert().accept();
		}
		else{

			try
			{
				//allWindowHandles = webDriver.getReport().getDriver().getWindowHandles();
				allWindowHandles = driver.getWindowHandles();

				for (String currentWindowHandle : allWindowHandles) 
				{
					if (!currentWindowHandle.equals(mainWindowHandle)) 
					{
						driver.switchTo().window(currentWindowHandle);
						webPagebodyText= driver.findElement(By.tagName("body")).getText() ;
						//InputStreamReader inputStrRdr = new InputStreamReader(Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor));
						//BufferedReader input = new BufferedReader(inputStrRdr);
						/*				Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
			        DataFlavor[] dataFlavors = transferable.getTransferDataFlavors();
					for(Object object: dataFlavors)
					{
						if (webPagebodyText instanceof String)
						{*/
						System.out.println(webPagebodyText.toString());
						if (webPagebodyText.toString().toUpperCase().contains("SYSTEM ERROR OCCURERED"))
						{
							controller.recoveryhandler();
							try
							{
								Automation.setUp();
								TransactionMapping.TransactionInputData("Login");  //Default Login START
							}
							catch(Exception e)
							{
								log.error(e.getMessage(), e);
								System.out.println("Failed to login into application");
							}
							// Code to initiate Scenario block
						}
						else if(webPagebodyText.toString().toUpperCase().contains("SESSION TIMED OUT."))
						{
							System.out.println("Session time out and login in again");
							try
							{
								Automation.setUp();
								TransactionMapping.TransactionInputData("Login");  //Default Login START
							}
							catch(Exception e)
							{
								log.error(e.getMessage(), e);
								System.out.println("Failed to login into application");
							}
							// Code to login into application again
						}
						/*	}
					}*/
						driver.close();
					}
				}
				driver.switchTo().window(mainWindowHandle);			  
			}
			catch(WebDriverException e)
			{
				log.error("Exception occured while closing non-main windows  <-|-> LocalizeMessage " + e.getLocalizedMessage() +" <-|-> Message "+ e.getMessage() +" <-|-> Cause "+ e.getCause(), e);
			}
		}
	}

	//isAlertPresent() function check whether this pop is application pop-up or not
	public static boolean isAlertPresent()
	{
		try
		{
			Automation.driver.switchTo().alert();
			return true;
		}catch( NoAlertPresentException e)
		{
			log.error(e.getMessage());
			return false;
		}
	} 
	//isModalPresent() function check whether this modal class present or not
	public static String isModalPopupPresent()
	{
		try
		{
			WebElement modalMsgElement= Automation.driver.findElement(By.className("modal-message"));
			return modalMsgElement.getText();
		}
		catch(Exception e)
		{
			log.error(e.getMessage());
			return "NULL";
		}
	}
	public static void takeScreenShot()
	{
		String cdate = null;	
		BufferedImage image =null;
		if(StringUtils.isNotBlank(webDriver.getReport().getFromDate()))
			cdate = webDriver.getReport().getFromDate().replaceAll("[-/: ]","");
		else
			webDriver.getReport().setFromDate(Config.dtFormat.format(new Date()));									
		String cfileName = "Captured_InRecovery_"+webDriver.getReport().getTestcaseId() + "_" + webDriver.getReport().getTrasactionType()+ "_"+cdate;
		String clocation = Config.resultFilePath +"\\ScreenShots\\"+ cfileName+"_" + WebHelper.screenshotnum + ".png";
		WebHelper.screenshotnum = WebHelper.screenshotnum +1;
		try
		{
			image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
			ImageIO.write(image, "png", new File(clocation));
		}catch(Exception e)
		{
			log.error("Exception thrown while taking screenshot in pop-recovery  <-|-> LocalizeMessage " + e.getLocalizedMessage() +" <-|-> Message "+ e.getMessage() +" <-|-> Cause "+ e.getCause(), e);
		}

	}

}
