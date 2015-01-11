package amex.qa.common;

import java.io.File;

import org.junit.Assume;
import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.Proxy.ProxyType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
/*import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;*/
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.safari.SafariDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
class LocalDriverFactory {
	
	private final Logger logger = LoggerFactory.getLogger(LocalDriverFactory.class);
	
    public WebDriver createInstance(String browserName) {

    	WebDriver driver = null;
    	// [Suren:] Commenting the peice of code as it was creating dependency issues while setting up CI
    	/*if(browserName.toLowerCase().contains("headless")){
    		DesiredCapabilities sCaps = new DesiredCapabilities();
    		 sCaps.setJavascriptEnabled(true);
    		 sCaps.setCapability("takesScreenshot", false);
    		 // 01
    		 // Change "User-Agent" via page-object capabilities
    		 sCaps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_SETTINGS_PREFIX + "userAgent", "My User Agent - Chrome");
    		 // 02
    		 // Disable "web-security", enable all possible "ssl-protocols" and "ignore-ssl-errors" for PhantomJSDriver
    		 sCaps.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, new String[] {
    		 "--web-security=false",
    		 "--ssl-protocol=any",
    		 "--ignore-ssl-errors=true",
    		 "--webdriver-loglevel=ERROR"
    		 });
    		String file = this.getClass().getResource("/phantomjs.exe").getFile();
        	System.setProperty("phantomjs.binary.path",file);
    		 driver = new PhantomJSDriver(sCaps);
    		
    	}*/
    	
        if (browserName.toLowerCase().contains("firefox")) {
        	
        	FirefoxProfile firefoxProfile = new FirefoxProfile();
    		firefoxProfile.setPreference("security.mixed_content.block_active_content",false);
    		firefoxProfile.setPreference("security.mixed_content.block_display_content",true);
    		firefoxProfile.setPreference("security.warn_leaving_secure",false);
    		firefoxProfile.setPreference("security.warn_submit_insecure",false);
    		firefoxProfile.setPreference("security.warn_leaving_secure.show_once",false);
    		firefoxProfile.setPreference("security.warn_viewing_mixed",false);
    		firefoxProfile.setPreference("security.warn_viewing_mixed.show_once",false);
    		firefoxProfile.setPreference("browser.ssl_override_behavior", 2); //Pre-populate the current URL and pre-fetch the certificate
    		firefoxProfile.setPreference("browser.tabs.warnOnOpen",false);
    		firefoxProfile.setPreference("browser.tabs.warnOnClose",false);
    		firefoxProfile.setPreference("browser.warnOnQuit",false);
    		firefoxProfile.setPreference("browser.privatebrowsing.dont_prompt_on_enter", true);
    		firefoxProfile.setPreference("browser.privatebrowsing.autostart", true);
    		firefoxProfile.setPreference("browser.helperApps.neverAsk.saveToDisk" , "application/octet-stream;application/csv;text/csv;application/vnd.ms-excel;"); 
    		firefoxProfile.setPreference("browser.helperApps.alwaysAsk.force", false);
    		firefoxProfile.setPreference("browser.download.manager.showWhenStarting",false);
    		firefoxProfile.setPreference("browser.download.folderList", 2); 
    		firefoxProfile.setPreference("browser.download.dir","C:\\Users\\rdontham\\workspace2\\AMEX-Payments-QA\\downloads"); 
    		
        	logger.info("Local WebDriver Factory - Firefox");
        	//String Xport = System.getProperty("lmportal.xvfb.id", ":1");
        	
        	final File firefoxPath = new File("C:\\Program Files (x86)\\Mozilla Firefox\\firefox.exe");
        	FirefoxBinary firefoxBinary = new FirefoxBinary(firefoxPath);
        	/*firefoxBinary.setEnvironmentProperty("DISPLAY", Xport);*/
           // driver = new FirefoxDriver(firefoxProfile);
        	try{
        		logger.info("in to try");
        		driver = new FirefoxDriver(firefoxProfile);
        	}
        	catch(Exception e){
        		e.printStackTrace();
        	}
        	 
        	 logger.info("Completed the setup for the firefox");
            return driver;
        }
        if (browserName.toLowerCase().contains("internet")) {
       	
        	DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
        	capabilities.setCapability(InternetExplorerDriver.UNEXPECTED_ALERT_BEHAVIOR, org.openqa.selenium.UnexpectedAlertBehaviour.DISMISS);
        	capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);

        	Proxy proxy = new Proxy();
        	proxy.setProxyType(ProxyType.MANUAL);
	        proxy.setSslProxy("trustAllSSLCertificates");
        	DesiredCapabilities capabilities1 = DesiredCapabilities.internetExplorer();
        	capabilities1.setCapability(CapabilityType.PROXY, proxy);
	        capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true); 
	        capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
	        capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
	  //      capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING,false);
	    //    capabilities.setCapability(InternetExplorerDriver.ELEMENT_SCROLL_BEHAVIOR, true);
	   //     capabilities.setCapability(InternetExplorerDriver.ENABLE_PERSISTENT_HOVERING,false);
	    //    capabilities.setCapability(InternetExplorerDriver.REQUIRE_WINDOW_FOCUS, true);
	        capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
	        
	        capabilities.setJavascriptEnabled(true); 
	        
	     //   capabilities.setVersion("8.0");
	       /* WindowsUtils.writeStringRegistryValue(" ", arg1);
	        capabilities.setCapability(CapabilityType.FEATURE_BROWSER_EMULATION, value);*/

        	logger.info("Local WebDriver Factory - Internet Explorer");
        	String file = this.getClass().getResource("/IEDriverServer.exe").getFile();
        	System.setProperty("webdriver.ie.driver",file);
        	
            driver = new InternetExplorerDriver(capabilities);
            


            return driver;
        }
        if (browserName.toLowerCase().contains("chrome")) {
        	logger.info("Local WebDriver Factory - Chrome");
        	String file = this.getClass().getResource("/chromedriver.exe").getFile();
        	System.setProperty("webdriver.chrome.driver",file);
        	driver= new ChromeDriver();
        	//TODO - add the Chrome Driver File
           
        }
        
        if(browserName.toLowerCase().contains("safari")){
        	logger.info("safari browser is initialized");
        	Assume.assumeTrue(isSupportedPlatform());
            driver = new SafariDriver();

        }
        return driver;
    }
    
    private  boolean isSupportedPlatform() {
        Platform current = Platform.getCurrent();
        return Platform.MAC.is(current) || Platform.WINDOWS.is(current);
      }

}


==============================================================================================




package amex.qa.common;

import org.openqa.selenium.WebDriver;

/**
 * @author mgajula
 * 
 * LocalDriverManager creates a threadsafe local copy of the WebDriver in use. 
 * Objects making use of WebDriver get it from LocalDriverManager
 * and return it (set) after use (unless terminating the driver instance entirely)
 * 
 * @param
 * @return
 */
public class LocalDriverManager {
    
	public static ThreadLocal<WebDriver> webDriver = new ThreadLocal<WebDriver>();
 
    public static WebDriver getDriver() {
        return webDriver.get();
    }
 
    public static void setWebDriver(WebDriver driver) {
        webDriver.set(driver);
    }
}





================================================================================================================





package amex.qa.common;

import java.util.Calendar;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mgajula
 *
 */
public class PageBase { 
	
	protected static final String legalDisclimerData="All users of our online services subject to Privacy Statement and agree to be bound by Terms of Service. Please review.";
	protected static final String copyrightData="© "+ Calendar.getInstance().get(Calendar.YEAR) +" American Express Company. All rights reserved.";
	protected WebDriver driver;
	protected Helper helper;
	protected final static  Logger logger = LoggerFactory.getLogger(PageBase.class);
	protected WebDriverWait wait;
	
	@FindBy(how=How.CSS,using="p.iNLegal")
	protected WebElement legalDisclimer;
	
	@FindBy(how=How.CSS,using="p.iNCopy")
	protected WebElement copyright;
	
	public PageBase(WebDriver driver) {
		this.driver = driver;
		 wait = new WebDriverWait(driver, 15);
	}
	
	

	public void ConfirmUrl(String strUrl)
	{
		
		if(!driver.getCurrentUrl().contains(strUrl))
		{
			logger.info("Page Url string not found - navigating to URL");
			driver.navigate().to(strUrl);
			logger.info("After navigation URL is " + driver.getCurrentUrl());
			
		}else
		{
			logger.info("Current URL: " + driver.getCurrentUrl() );
			
		}
		//TODO - create routines to handle security pop-ups
	}
	
	/**
	 * Waits till the element is found and times out after the provided
	 * duration. If the element is loaded before the provided duration the code
	 * execution continues. Verifying the visibility of element happens every 5
	 * seconds
	 * 
	 * @param WebElement
	 *            - Element for which the web driver should wait
	 * @param duration
	 *            - Number of seconds[in numbers] to wait
	 * @returns WebElement - Same Element after the element is found
	 * @author Suren
	 */
	public WebElement waitForElement(WebElement locator,int...duration) {
		int durationNewValue=0;
		if(duration.length==0){
			durationNewValue=50;
		}else{
			durationNewValue=duration[0];
		}
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
				.withTimeout(durationNewValue, TimeUnit.SECONDS)
				.pollingEvery(5, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class);		
		
		WebElement webElement = wait.until(ExpectedConditions
				.elementToBeClickable(locator));
		return webElement;
	}
	
	
	public String getAlertText(int duration) {
		String alertmsg= "";
		try {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
					.withTimeout(duration, TimeUnit.SECONDS)
					.pollingEvery(2, TimeUnit.SECONDS)
					.ignoring(NoSuchElementException.class);
			wait.until(ExpectedConditions.alertIsPresent());
			Alert alert = driver.switchTo().alert();
			alertmsg= alert.getText();
			alert.accept();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return alertmsg;
	}
	
	
	public WebElement waitForElementVisibility(WebElement locator,int duration) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
				.withTimeout(duration, TimeUnit.SECONDS)
				.pollingEvery(2, TimeUnit.SECONDS)
				.ignoring(NoSuchElementException.class);

		WebElement webElement = wait.until(ExpectedConditions
				.visibilityOf(locator));
		return webElement;
	}
	
	public String waitForElementCheck(WebElement locator,int...duration) {
		int durationNewValue=0;
		if(duration.length==0){
			durationNewValue=50;
		}else{
			durationNewValue=duration[0];
		}
		try {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
					.withTimeout(durationNewValue, TimeUnit.SECONDS)
					.pollingEvery(1, TimeUnit.SECONDS)
					.ignoring(NoSuchElementException.class);
			
			wait.until(ExpectedConditions
					.elementToBeClickable(locator));
			return null;
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	

}//class




============================================================================================





package amex.qa.common;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

@Listeners({TestNGExecutionListener.class,TestNGSuiteListener.class,TestNGEventListener.class})
public class TestBase {
	
	protected final static  Logger logger = LoggerFactory.getLogger(TestBase.class);
	protected WebDriver driver;
	protected Helper helper;
	protected Properties prop;
	protected Properties languageProperties;
	protected Properties languagePropertiesAPI;
	
	public WebDriver getDriver() {
        return driver;
}
	
	public TestBase() {	}
	
	@BeforeClass
	public void beforeClass()
	{
		driver = LocalDriverManager.getDriver();//Gets a threadsafe instance of Webdriver
		//Load the properties file
		prop = new Properties();
		
		try {
			if(driver.getCurrentUrl().contains("qwww413") || driver.getCurrentUrl().contains("qwww209")){
				prop.load(TestBase.class.getResourceAsStream("/TestData_E2.properties"));
				logger.info("E2 user properties got loaded");
			}
			else if(driver.getCurrentUrl().contains("https://www413.americanexpress")){
			prop.load(TestBase.class.getResourceAsStream("/TestData_E3.properties"));
			}
			else{
				prop.load(TestBase.class.getResourceAsStream("/TestData.properties"));
				logger.info("E1 user properties got loaded");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("Unable to load TestData.Properties file");
			System.exit(0);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
			logger.info("Unable to load TestData.Properties file. File null.");
			System.exit(0);
		}		
		
	}
	
	
	@BeforeMethod
	@Parameters({ "lang" })
	public  void loadLanguage(@Optional String language){
		languageProperties=new Properties();
		languagePropertiesAPI=new Properties();
		if(StringUtils.isNotBlank(language) && language.equalsIgnoreCase("fr")){
			try {
				prop.setProperty("lang", "fr");
				languageProperties.load(TestBase.class.getResourceAsStream("/labletext_fr.properties"));
				if(driver.getCurrentUrl().contains("disputes"))
				languagePropertiesAPI.load(TestBase.class.getResourceAsStream("/DisputesCodeDescription_fr.properties"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.info("Unable to load labletext_fr.properties file");
				System.exit(0);
			}
			catch (NullPointerException e)
			{
				e.printStackTrace();
				logger.info("Unable to load TestData.Properties file. File null.");
				System.exit(0);
			}	
		}
		if(StringUtils.isBlank(language)|| language.equalsIgnoreCase("en")){
			try {
				languageProperties.load(TestBase.class.getResourceAsStream("/labletext_en.properties"));
				if(driver.getCurrentUrl().contains("disputes"))
				languagePropertiesAPI.load(TestBase.class.getResourceAsStream("/DisputesCodeDescription_en.properties"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.info("Unable to load labletext_en.properties file");
				System.exit(0);
			}
			catch (NullPointerException e)
			{
				e.printStackTrace();
				logger.info("Unable to load TestData.Properties file. File null.");
				System.exit(0);
			}	
		}
		
	}
	
	
	public Properties loadProperties()
	{
		driver = LocalDriverManager.getDriver();//Gets a threadsafe instance of Webdriver
		prop = new Properties();
		
		try {
			if(driver.getCurrentUrl().contains("qwww413.americanexpress")){
				prop.load(TestBase.class.getResourceAsStream("/TestData_E2.properties"));
			}
			else if(driver.getCurrentUrl().contains("https://www413.americanexpress")){
				prop.load(TestBase.class.getResourceAsStream("/TestData_E3.properties"));
				}
			else{
			prop.load(TestBase.class.getResourceAsStream("/TestData.properties"));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			logger.info("Unable to load TestData.Properties file");
			System.exit(0);
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
			logger.info("Unable to load TestData.Properties file. File null.");
			System.exit(0);
			
		}
		
		return prop;
		
	}
	
	public void waitForPageLoaded(WebDriver driver) {

	     ExpectedCondition<Boolean> expectation = new
	ExpectedCondition<Boolean>() {
	        public Boolean apply(WebDriver driver) {
	          return ((JavascriptExecutor)driver).executeScript("return document.readyState").equals("complete");
	        }
	      };

	     Wait<WebDriver> wait = new WebDriverWait(driver,60);
	      try {
	              wait.until(expectation);
	      } catch(Throwable error) {
	    	  Assert.assertFalse(true,"Timeout waiting for Page Load Request to complete.");
	      }
	 } 
	protected String waitForElementCheck(WebElement locator,int...duration) {
		int durationNewValue=0;
		if(duration.length==0){
			durationNewValue=50;
		}else{
			durationNewValue=duration[0];
		}
		try {
			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
					.withTimeout(durationNewValue, TimeUnit.SECONDS)
					.pollingEvery(1, TimeUnit.SECONDS)
					.ignoring(NoSuchElementException.class);
			
			wait.until(ExpectedConditions
					.elementToBeClickable(locator));
			return null;
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
}//Class


	

 ===============================================================================
 
 
 
 package amex.qa.common;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

public class TestNGEventListener implements ITestListener {
	
	private final Logger logger = LoggerFactory.getLogger(TestNGEventListener.class);

	
	public void onTestStart(ITestResult result) {

		
		logger.info("Test Started: "+ result.getName()+" Thread Name: "+Thread.currentThread().getName());
		Reporter.log("Test Started: "+ result.getName()+" Thread ID: "+Thread.currentThread().getId() +" TestCaseName : "+result.getTestContext().getCurrentXmlTest().getName());
		//screenShot(result);
	}

	
	public void onTestSuccess(ITestResult result) {

		logger.info("Test Passed: "+ result.getMethod().getMethodName().toString());
		Reporter.log("Test Passed: "+ result.getMethod().getMethodName().toString()+" Thread ID: "+Thread.currentThread().getId() +" TestCaseName : "+result.getTestContext().getCurrentXmlTest().getName());
	}

	
	public void onTestFailure(ITestResult result) {

		logger.info("Test Failed: "+ result.getMethod().getMethodName().toString());
		Reporter.log("Test Failed: "+ result.getMethod().getMethodName().toString()+" Thread ID: "+Thread.currentThread().getId() +" TestCaseName : "+result.getTestContext().getCurrentXmlTest().getName());  
		screenShot(result);
		  
	}

	
	public void onTestSkipped(ITestResult result) {
		Reporter.log("Test Skipped: " + result.getMethod().getMethodName().toString());  
		logger.info("Test Skipped: " + result.getMethod().getMethodName().toString()+" Thread ID: "+Thread.currentThread().getId() +" TestCaseName : "+result.getTestContext().getCurrentXmlTest().getName());
	}

	
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		logger.info("Test Failed: "+ result.getMethod().getMethodName().toString()+" Thread ID: "+Thread.currentThread().getId() +" TestCaseName : "+result.getTestContext().getCurrentXmlTest().getName());
		//Take snapshot
		
	}

	
	public void onStart(ITestContext context) {
		 WebDriver driver;
		 DesiredCapabilities capabilities=null;
		logger.info("Test Class Started: " + context.getClass().getName().toString());
		if(LocalDriverManager.getDriver() != null)
		{
			logger.info("Driver Already Initialized, exiting method.");
			 return; //exit method
		}
		
		Global.setBaseUrl(null);

		logger.info("Testing Started: " +context.getName() );
		String language = context.getCurrentXmlTest().getParameter("lang");
		
		logger.info("@@@@@@@ language" +language);
		String browserStack = context.getCurrentXmlTest().getParameter("amexRemoteBrowserStack");
		String takeJenkinsPropertiesFromMaven = System.getProperty("takeJenkinsPropertiesFromMaven");
		logger.info("takeJenkinsPropertiesFromMaven "+takeJenkinsPropertiesFromMaven);
		if((StringUtils.isEmpty(takeJenkinsPropertiesFromMaven)|| !takeJenkinsPropertiesFromMaven.equalsIgnoreCase("true")) && StringUtils.isNotEmpty(browserStack)){
			String[] split = browserStack.split(",");
			 capabilities = new DesiredCapabilities();
			String browserName=null;
			if(split[1].equalsIgnoreCase("IE")){
				capabilities.setCapability(InternetExplorerDriver.UNEXPECTED_ALERT_BEHAVIOR, org.openqa.selenium.UnexpectedAlertBehaviour.DISMISS);
	        	capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
	        	capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true); 
		        capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
		        capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
		        capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
		        capabilities.setJavascriptEnabled(true); 
				browserName="internet explorer";
			}
			else if(split[1].contains("firefox")){
				browserName="firefox";
			}
			if(null==browserName){
				capabilities.setCapability(InternetExplorerDriver.UNEXPECTED_ALERT_BEHAVIOR, org.openqa.selenium.UnexpectedAlertBehaviour.DISMISS);
	        	capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
	        	capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true); 
		        capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
		        capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
		        capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
		        capabilities.setJavascriptEnabled(true); 
				browserName="internet explorer";
			}
			if(split[1].equalsIgnoreCase("safari")){
				browserName="safari";
				capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true); 
				capabilities.setCapability(CapabilityType.UNEXPECTED_ALERT_BEHAVIOUR, org.openqa.selenium.UnexpectedAlertBehaviour.DISMISS); 
			}
			capabilities.setBrowserName(browserName);
			if(split.length>2){
				capabilities.setCapability("version", split[2]);	
			}
			
			capabilities.setCapability("platform", split[0]);
			 
		}
		else if(StringUtils.isNotEmpty(takeJenkinsPropertiesFromMaven) && takeJenkinsPropertiesFromMaven.equalsIgnoreCase("true")){
			capabilities = new DesiredCapabilities();
			String browserName = System.getProperty("browser");
			logger.info("browserName "+browserName);
			if(StringUtils.isNotEmpty(browserName)){
				if(browserName.equalsIgnoreCase("ie")){
					capabilities.setCapability(InternetExplorerDriver.UNEXPECTED_ALERT_BEHAVIOR, org.openqa.selenium.UnexpectedAlertBehaviour.DISMISS);
		        	capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		        	capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true); 
			        capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
			        capabilities.setCapability(InternetExplorerDriver.IE_ENSURE_CLEAN_SESSION, true);
			        capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
			        capabilities.setJavascriptEnabled(true); 
			        browserName="internet explorer";
				}
				if(browserName.equalsIgnoreCase("firefox")){
					
				}
				if(browserName.equalsIgnoreCase("safari")){
					capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true); 
				}
				capabilities.setBrowserName(browserName);
			}
			String operationgSystem = System.getProperty("os");
			logger.info("operationgSystem "+operationgSystem);
			if(StringUtils.isNotEmpty(operationgSystem)){
				capabilities.setCapability("platform", operationgSystem);
			}
			String browserVersion = System.getProperty("browserVersion");
			logger.info("browserVersion "+browserVersion);
			if(StringUtils.isNotEmpty(browserVersion)){
				capabilities.setCapability("version", browserVersion);	
			}
		}
	
			try {
				
				if((StringUtils.isEmpty(takeJenkinsPropertiesFromMaven)|| !takeJenkinsPropertiesFromMaven.equalsIgnoreCase("true"))&& Global.isRunningOnJenkins() && StringUtils.isNotEmpty(browserStack)){
					logger.info("Initializing remote webdriver.....");
					String jenkinsMasterURL = Global.getJenkinsMasterURL();
					logger.info("jenkinsMasterURL "+jenkinsMasterURL);
					driver = new RemoteWebDriver(new URL(jenkinsMasterURL), capabilities);
				}
				else if(StringUtils.isNotEmpty(takeJenkinsPropertiesFromMaven) && takeJenkinsPropertiesFromMaven.equalsIgnoreCase("true")){
					logger.info("Initializing remote webdriver from maven command line.....");
					String jenkinsMasterURL = Global.getJenkinsMasterURL();
					logger.info("jenkinsMasterURL "+jenkinsMasterURL);
					driver = new RemoteWebDriver(new URL(jenkinsMasterURL), capabilities);

				}
				else{
					logger.info("Initializing Local WebDriver");		
					driver = new LocalDriverFactory().createInstance(Global.getBrowser());
				}
				
				driver.manage().timeouts().implicitlyWait(50, TimeUnit.SECONDS);				
				driver.manage().timeouts().setScriptTimeout(50, TimeUnit.SECONDS);
				//driver.manage().timeouts().pageLoadTimeout(100, TimeUnit.SECONDS);
				
				
				logger.info("Current Test URL: "+ Global.getBaseUrl());

				String baseUrl = Global.getBaseUrl();
				if(StringUtils.isNotBlank(language) && language.endsWith("fr")){
					baseUrl=baseUrl+"?locale=fr_CA";
				}
				Global.setBaseUrl(baseUrl);
				driver.navigate().to(baseUrl);
				Helper.Wait(5);
				driver.manage().window().maximize();
				LocalDriverManager.setWebDriver(driver);//make it threadsafe

			}
		    catch (Exception e) {
			e.printStackTrace();
		    }
	}

	
	public void onFinish(ITestContext context) {
		

		logger.info("Test Class Finished: " + context.getClass().getName().toString());
		
		
		
		if(LocalDriverManager.getDriver()!=null){
			LocalDriverManager.getDriver().close();
			LocalDriverManager.getDriver().quit();
			
			
			LocalDriverManager.setWebDriver(null);
		}
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	        	String property = System.getProperty("os.name");
				//System.out.println("property "+property);
				try {
					String line;
					Process p;
					if(property.contains("Window")){
						p = Runtime.getRuntime().exec
						        (System.getenv("windir") +"\\system32\\"+"tasklist.exe");
					}
					else{
						p=Runtime.getRuntime().exec("ps -few");
					}
					//Process p=Runtime.getRuntime().exec("ps -few");
					/*Process p = Runtime.getRuntime().exec
					        (System.getenv("windir") +"\\system32\\"+"tasklist.exe");*/
					BufferedReader input =
			                new BufferedReader(new InputStreamReader(p.getInputStream()));
					boolean found=false;
					boolean chromefound=false;
			        while ((line = input.readLine()) != null) {
			            if(line.contains("IEDriverServer.exe")){
			            	found=true;
			            	System.out.println("found the ");
			            	break;
			            }
			          
			        }
			        input.close();
			        if(found){
			        	Thread.sleep(2000L);
			        	System.out.println("killing the process");
			        	Runtime.getRuntime().exec("taskkill /F /IM IEDriverServer.exe");
					}
			        
			        
			        
			    } catch (Exception err) {
			        err.printStackTrace();
			    }
				
	        }
	    }));
		
	}
	
	public void screenShot(ITestResult result)
	{
		
		  //get a reference to the test class in use
		  Object currentClass = result.getInstance();
		  //Get driver instance singleton from base class underneath the test class
		  if (((TestBase) currentClass).getDriver() != null)
		  {
			  WebDriver driver = ((TestBase) currentClass).getDriver();
			  
			  if (driver.getClass().getName().equals("org.openqa.selenium.remote.RemoteWebDriver")) {
					 //if remoteDriver, need to augment the class with screenshot functionality
				      driver = new Augmenter().augment(driver);
				    } //if RemoteWebDriver
			  			  
			    File fileScreenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

				try {
					
					//Save file to reporting directories
					SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");
			        String date = sdf.format(new Date());
			        File fileSurefireCopy=null;
			        if(StringUtils.isNotEmpty(System.getProperty("profileId"))){
			        	  fileSurefireCopy = new File("target/"+System.getProperty("profileId")+"/html/ScreenShot-" + date + "-" + currentClass.getClass().getName() + "." + result.getMethod().getMethodName() + ".png");			        	  
			        	//Fix for report- ng
			        	  File reportNgfileSurefireCopy = new File("target/"+System.getProperty("profileId")+"/html/html/ScreenShot-" + date + "-" + currentClass.getClass().getName() + "." + result.getMethod().getMethodName() + ".png");
			        	  FileUtils.copyFile(fileScreenShot, reportNgfileSurefireCopy);
			        }
			        else{
			        	  fileSurefireCopy = new File("test-output/html/ScreenShot-" + date + "-" + currentClass.getClass().getName() + "." + result.getMethod().getMethodName() + ".png");
			        }
			       
					FileUtils.copyFile(fileScreenShot, fileSurefireCopy);
										
					String reportLink = "<a href=\"html/ScreenShot-"+ date + "-" + currentClass.getClass().getName() + "." + result.getMethod().getMethodName() + ".png\">Screen Shot - " + currentClass.getClass().getName() +"</a>" ;
										
					Reporter.log("<br/>" + reportLink, true);
										
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Reporter.log("error generating screenshot for " + currentClass.getClass().getName());
				}
				

		  }//if
		  
		
	}//screenShot

}


