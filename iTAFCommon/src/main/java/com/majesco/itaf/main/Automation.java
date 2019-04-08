package com.majesco.itaf.main;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import com.majesco.itaf.util.CalendarSnippet;
import com.majesco.itaf.util.WebDriverFactory;

public class Automation  {

	private final static Logger log = Logger.getLogger(Automation.class
			.getName());
	
	public static WebDriver driver;
	public static String MainWebDriverHandle = null;

	public static enum browserTypeEnum {
		InternetExplorer, FireFox, Chrome, Safari, GeckoFireFox
	};

	private static String browser = null;
	public static browserTypeEnum browserType = null;
	private static WebDriverFactory webDriverObj = new WebDriverFactory();
														// Batch

	private static ITAFWebDriver itafWebDriver = null;

	public static void setMainWebDriverHandle(String handle) {
		MainWebDriverHandle = handle;
	}

	public static String getMainWebDriverHandle() {
		return MainWebDriverHandle;
	}

	public static void setUp() throws NullPointerException, Exception {
		
		itafWebDriver = ITAFWebDriver.getInstance();

		Date initialDate = new Date();
		String strInitialDate = Config.dtFormat.format(initialDate);
		itafWebDriver.getReport().setFromDate(strInitialDate);

		//ObjectFactory.getInstance().initMainController();
		
		// bhaskar driver new session START
		try {
			if (CalendarSnippet.isProcessRunning("iexplore.exe")) {
				CalendarSnippet.killProcess("iexplore.exe");
			}
			if (CalendarSnippet.isProcessRunning("IEDriverServer.exe")) {
				CalendarSnippet.killProcess("IEDriverServer.exe");
			}

			// Meghna-Commenting this to stop killing chrome driver of PAS
			// execution//
			if (ITAFWebDriver.isPASApplication()) {
				if(CalendarSnippet.isProcessRunning("chromedriver.exe")) {
			//	 CalendarSnippet.killProcess("chromedriver.exe"); 
					 }
				 if(CalendarSnippet.isProcessRunning("chrome.exe") ) {
					// CalendarSnippet.killProcess("chrome.exe"); 
					 }
			}
			 
			if (CalendarSnippet.isProcessRunning("firefox.exe")) {
				CalendarSnippet.killProcess("firefox.exe");
			}			 
			// bhaskar driver new session END
		} catch (Exception e) {
			log.error("Failed to kill "
					+ Config.browserType
					+ " browser process",e);
			// throw new Exception("Failed to kill "+
			// Config.browserType +" browser process");
		}
		try {

			browser = Config.browserType;
			browserType = browserTypeEnum.valueOf(browser);
			Object baseURL = Config.baseURL;

			switch (browserType) {
			case InternetExplorer:
				driver = getIEDriverInstance();
				// bhaskar driver new session START
				itafWebDriver.getReport().setDriver(driver);
				// driver = webDriver.getReport().getDriver();
				// bhaskar driver new session END
				driver.manage().deleteAllCookies();
				driver.manage().window().maximize();

				// driver.manage().timeouts().pageLoadTimeout(180,TimeUnit.SECONDS);
				driver.manage()
						.timeouts()
						.pageLoadTimeout(
								Integer.parseInt(Config.timeOut),
								TimeUnit.SECONDS);

				driver.get(baseURL.toString());

				break;

			case FireFox:
				System.setProperty("webdriver.firefox.profile", "default");
				driver = getFFDriverInstance();
				// bhaskar driver new session START
				itafWebDriver.getReport().setDriver(driver);
				// driver = webDriver.getReport().getDriver();
				// bhaskar driver new session END
				driver.manage().deleteAllCookies();
				driver.manage().window().maximize();
				driver.navigate().to(baseURL.toString());
				break;

			case Chrome:
				
				driver = getChromeDriverInstance();
				log.info(" webDriver.getReport() " + itafWebDriver.getReport());
				log.info(" driver " + driver);
				itafWebDriver.getReport().setDriver(driver);
				driver.manage().deleteAllCookies();
				driver.get(baseURL.toString());
			
				break;

			// iTAFCommon
			// Using the Chrome case code from PAS iTAF as PAS uses Chrome
			// extensively for testing
			// and since billing uses IE, commenting the billing code for the
			// same

			/*
			 * case "Chrome": //driver = getChromeDriverInstance();
			 * 
			 * String drivPath =
			 * "D:\\Workspace\\iTAFSeleniumWeb\\libs\\chromedriver.exe";
			 * System.setProperty("webdriver.chrome.driver", drivPath);
			 * 
			 * ChromeOptions options = new ChromeOptions();
			 * options.addArguments("--kiosk");
			 * 
			 * driver = new ChromeDriver();
			 * 
			 * 
			 * //bhaskar driver new session START
			 * webDriver.getReport().setDriver(driver); driver =
			 * webDriver.getReport().getDriver(); //bhaskar driver new session
			 * END driver.manage().deleteAllCookies();
			 * driver.manage().window().maximize();
			 * driver.get(baseURL.toString());
			 * 
			 * 
			 * break;
			 */

			case Safari:
				driver = getSafariDriverInstance();
				// bhaskar driver new session START
				itafWebDriver.getReport().setDriver(driver);
				// driver = webDriver.getReport().getDriver();
				// bhaskar driver new session END
				driver.manage().window().maximize();
				driver.get(baseURL.toString());
				break;

			case GeckoFireFox:

				driver = getGeckoDriverInstance();
				itafWebDriver.getReport().setDriver(driver);
				DesiredCapabilities capabilities = DesiredCapabilities
						.firefox();
				capabilities.setCapability("marionette", true);
				driver = new FirefoxDriver(capabilities);
				driver.navigate().to(baseURL.toString());
				break;

			}
			/** Implicit Wait **/
			// driver.manage().timeouts().implicitlyWait(Long.parseLong(Automation.configHashMap.get("TIMEOUT").toString()),
			// TimeUnit.SECONDS);

			// TODO
			// This code may not be required as MainWebDriverHandle is not used
			// from anywhere
			if (driver.getWindowHandle() != null) {
				Automation.setMainWebDriverHandle(driver.getWindowHandle());
				// log.info("WebDriver handle is : " +
				// getMainWebDriverHandle()); // commented for log clearing
			} else {
				log.error("Unable to get WebDriver handle.");
				throw new NullPointerException(
						"Failed to get WebDriver handle which indicates Random pop-up Functionality wont work");
			}

		} catch (NullPointerException npe) {
			log.error("Failed create DriverInstance <-|-> LocalizeMessage "
					+ npe.getLocalizedMessage() + " <-|-> Message "
					+ npe.getMessage() + " <-|-> Cause " + npe.getCause(),npe);
			getController().pauseFun(
					"Failed create DriverInstance in Automation.setUp <-|-> LocalizeMessage "
							+ npe.getLocalizedMessage() + " <-|-> Message "
							+ npe.getMessage() + " <-|-> Cause "
							+ npe.getCause());
		} catch (Exception e) {
			log.error("Failed create Chrome DriverInstance <-|-> LocalizeMessage "
					+ e.getLocalizedMessage()
					+ " <-|-> Message "
					+ e.getMessage() + " <-|-> Cause " + e.getCause(),e);
			getController().pauseFun(
					"Error from Automation.Setup " + e.getMessage());
		}

		// bhaskar
		// }
		// bhaskar
	}

	/** Returns an IE Driver's Instance **/
	private static WebDriver getIEDriverInstance() throws Exception {
		// TM:Commented the following code as driver is defined global
		try {
			return webDriverObj.createDriver("msie");
		}

		/*
		 * catch(Exception e) { log.error(
		 * "Failed create Internet Explorer DriverInstance <-|-> LocalizeMessage "
		 * + e.getLocalizedMessage() +" <-|-> Message "+ e.getMessage()
		 * +" <-|-> Cause "+ e.getCause()); throw new
		 * Exception("Failed to read process list  <-|-> LocalizeMessage " +
		 * e.getLocalizedMessage() +" <-|-> Message"+ e.getMessage()
		 * +" <-|-> Cause "+ e.getCause());
		 * 
		 * }
		 */

		// Meghna -- to handle Login host connection issue
		catch (Exception e1) {
			log.error(e1.getMessage(), e1);
			try {
				if (CalendarSnippet.isProcessRunning("iexplore.exe")) {
					CalendarSnippet.killProcess("iexplore.exe");
				}
				return webDriverObj.createDriver("msie");
			}

			catch (Exception e) {
				log.error("Failed create Internet Explorer DriverInstance <-|-> LocalizeMessage "
						+ e.getLocalizedMessage()
						+ " <-|-> Message "
						+ e.getMessage() + " <-|-> Cause " + e.getCause(),e);
				throw new Exception(
						"Failed to read process list  <-|-> LocalizeMessage "
								+ e.getLocalizedMessage() + " <-|-> Message"
								+ e.getMessage() + " <-|-> Cause "
								+ e.getCause());
			}

		}
		//

	}

	/** Returns a FireFox Driver's Instance **/
	private static WebDriver getFFDriverInstance() throws Exception {
		// TM: commented the following code as driver is defined global
		try {
			return new FirefoxDriver();
		} catch (Exception e) {
			log.error("Failed create Fire Fox DriverInstance <-|-> LocalizeMessage "
					+ e.getLocalizedMessage()
					+ " <-|-> Message "
					+ e.getMessage() + " <-|-> Cause " + e.getCause(),e);
			throw new Exception(
					"Failed to read process list  <-|-> LocalizeMessage "
							+ e.getLocalizedMessage() + " <-|-> Message"
							+ e.getMessage() + " <-|-> Cause " + e.getCause());
		}
	}

	/** Returns a Chrome Driver's Instance **/
	private static WebDriver getChromeDriverInstance() throws Exception {
		// TM: commented the following code as driver is defined global
		try {
			return webDriverObj.createDriver("chrome");
		} catch (Exception e) {
			log.error("Failed create Chrome DriverInstance <-|-> LocalizeMessage "
					+ e.getLocalizedMessage()
					+ " <-|-> Message "
					+ e.getMessage() + " <-|-> Cause " + e.getCause(), e);
			throw new Exception(
					"Failed to read process list  <-|-> LocalizeMessage "
							+ e.getLocalizedMessage() + " <-|-> Message"
							+ e.getMessage() + " <-|-> Cause " + e.getCause());

		}
	}

	/** Returns a Safari Driver Instance **/
	private static WebDriver getSafariDriverInstance() throws Exception {
		try {
			// TM: commented the following code as driver is defined global
			return webDriverObj.createDriver("safari");
		} catch (Exception e) {
			log.error("Failed create Safari DriverInstance <-|-> LocalizeMessage "
					+ e.getLocalizedMessage()
					+ " <-|-> Message "
					+ e.getMessage() + " <-|-> Cause " + e.getCause(),e);
			throw new Exception(
					"Failed to read process list  <-|-> LocalizeMessage "
							+ e.getLocalizedMessage() + " <-|-> Message"
							+ e.getMessage() + " <-|-> Cause " + e.getCause());

		}
	}

	private static WebDriver getGeckoDriverInstance() throws Exception {
		// TM: commented the following code as driver is defined global
		try {
			// TM: commented the following code as driver is defined global
			return webDriverObj.createDriver("geckodriver");
		} catch (Exception e) {
			log.error("Failed create Safari DriverInstance <-|-> LocalizeMessage "
					+ e.getLocalizedMessage()
					+ " <-|-> Message "
					+ e.getMessage() + " <-|-> Cause " + e.getCause(),e);
			throw new Exception(
					"Failed to read process list  <-|-> LocalizeMessage "
							+ e.getLocalizedMessage() + " <-|-> Message"
							+ e.getMessage() + " <-|-> Cause " + e.getCause());

		}
	}

	private static MainController getController() {
		MainController controller = ObjectFactory.getMainController();
		return controller;
	}
	

	//Meghna : R10.9 - For Refreshing login page if the page is not loaded//
	public static void refreshPage(String controlName)
	{	
		Wait<WebDriver> waitLogin;
		WebElement loginPage; 
		Boolean loginLoaded = false;
		int loginCnt = 0;
			
		waitLogin = new FluentWait<WebDriver>(driver)
				.withTimeout(2, TimeUnit.SECONDS)
				.pollingEvery(1,TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class);
				
		
		while((!loginLoaded) && (loginCnt < 10))
		{	
			try
			{
				loginPage = waitLogin.until(ExpectedConditions.elementToBeClickable(By.xpath(controlName)));
				loginLoaded=true;
			}
			catch(Exception e)
			{
				driver.navigate().refresh();
				loginCnt = loginCnt + 1;
			}
		}
		
		if(loginLoaded)
		{
			log.info("Login Page loaded");
		}
		
	}

}
