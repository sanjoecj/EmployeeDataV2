
package amex.qa.common;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IExecutionListener;

public class TestNGExecutionListener implements IExecutionListener {
	
	
	public WebDriver driver;
	Process gridShellProcess;
	Process gridNodeProcess;
	
	final Logger logger = LoggerFactory.getLogger(TestNGExecutionListener.class);

	
	public void onExecutionStart() {
		
	/*	logger.info("Test Execution Listener Started");
		
		if(LocalDriverManager.getDriver() != null)
		{
			logger.info("Driver Already Initialized, exiting method.");
			 return; //exit method
		}
		

		logger.info("Testing Started");
		
		// Choose the browser, version, and platform to test
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setBrowserName(Global.getBrowser());
		capabilities.setCapability("version", Global.getBrowserVersion());
		capabilities.setCapability("platform", Platform.valueOf(Global.getOS()));
		capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

			try {
				
				logger.info("Initializing Local WebDriver");			
				driver = new LocalDriverFactory().createInstance(Global.getBrowser());	
				driver.manage().timeouts().setScriptTimeout(30, TimeUnit.SECONDS);
				logger.info("Current Test URL: "+ Global.getBaseUrl());
				logger.info("Browser: " + capabilities.getBrowserName() + " " + capabilities.getVersion());

				driver.navigate().to(Global.getBaseUrl());
				driver.manage().window().maximize();

				Helper.Wait(5);

				LocalDriverManager.setWebDriver(driver);//make it threadsafe

			}
		    catch (Exception e) {
			e.printStackTrace();
		    }
		*/
	}

	
	public void onExecutionFinish() {
		
		/*if( LocalDriverManager.getDriver() != null)
		{
			driver = LocalDriverManager.getDriver();//Gets a threadsafe instance
			driver.close();
			driver.quit();	
			
			LocalDriverManager.setWebDriver(null);	
		}
		
		logger.info("All Tests Completed");*/
	}

	
	
	
	
}//Class




=======================================================



package amex.qa.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ISuite;
import org.testng.ISuiteListener;

//import org.eclipse.core.resources.*;

public class TestNGSuiteListener implements ISuiteListener {
	
	
	final Logger logger = LoggerFactory.getLogger(TestNGSuiteListener.class);

	public void onStart(ISuite suite) {
		
		logger.info("TestNG Suite " + suite.getName() +" Started" );
		 
	}

	public void onFinish(ISuite suite) {
	
		logger.info("TestNG Suite completed");
	}

}//Class



===========================================================



package amex.qa.common;

import java.util.LinkedHashMap;

public class ThreadLocaleDataTrasnformer {
	public static ThreadLocal<LinkedHashMap<Integer, String>> disputeData = new ThreadLocal<LinkedHashMap<Integer, String>>();
	
	public static void setDisputeData(LinkedHashMap<Integer, String> disputeDataInput){
		disputeData.set(disputeDataInput);
	}
	public static LinkedHashMap<Integer, String> getDisputeData(){
		return disputeData.get();
	}
	 
    

}



===========================================================



package amex.qa.common;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


/**
 * Wait tool class.  Provides Wait methods for an elements, and AJAX elements to load.  
 * It uses WebDriverWait (explicit wait) for waiting an element or javaScript.  
 * 
 * To use implicitlyWait() and WebDriverWait() in the same test, 
 * we would have to nullify implicitlyWait() before calling WebDriverWait(), 
 * and reset after it.  This class takes care of it. 
 * 
 * 
 * Generally relying on implicitlyWait slows things down 
 * so use WaitToolï¿½s explicit wait methods as much as possible.
 * Also, consider (DEFAULT_WAIT_4_PAGE = 0) for not using implicitlyWait 
 * for a certain test.
 * 
 * @author Chon Chung, Mark Collin, Andre, Tarun Kumar 
 * 
 * @todo check FluentWait -- http://seleniumsimplified.com/2012/08/22/fluentwait-with-webelement/
 *
 * Copyright [2012] [Chon Chung]
 * 
 * Licensed under the Apache Open Source License, Version 2.0  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 */
public class WaitTool {

	/** Default wait time for an element. 7  seconds. */ 
	public static final int DEFAULT_WAIT_4_ELEMENT = 7; 
	/** Default wait time for a page to be displayed.  12 seconds.  
	 * The average webpage load time is 6 seconds in 2012. 
	 * Based on your tests, please set this value. 
	 * "0" will nullify implicitlyWait and speed up a test. */ 
	public static final int DEFAULT_WAIT_4_PAGE = 50; 


	

	/**
	  * Wait for the element to be present in the DOM, and displayed on the page. 
	  * And returns the first WebElement using the given method.
	  * 
	  * @param WebDriver	The driver object to be used 
	  * @param By	selector to find the element
	  * @param int	The time in seconds to wait until returning a failure
	  *
	  * @return WebElement	the first WebElement using the given method, or null (if the timeout is reached)
	  */
	public static WebElement waitForElement(WebDriver driver, final By by, int timeOutInSeconds) {
		WebElement element; 
		try{	
			//To use WebDriverWait(), we would have to nullify implicitlyWait(). 
			//Because implicitlyWait time also set "driver.findElement()" wait time.  
			//info from: https://groups.google.com/forum/?fromgroups=#!topic/selenium-users/6VO_7IXylgY
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS); //nullify implicitlyWait() 
			  
			WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds); 
			element = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
			
			driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_4_PAGE, TimeUnit.SECONDS); //reset implicitlyWait
			return element; //return the element	
		} catch (Exception e) {
			//e.printStackTrace();
		} 
		return null; 
	}
	
	
	
	
	
	

	/**
	  * Wait for the element to be present in the DOM, regardless of being displayed or not.
	  * And returns the first WebElement using the given method.
	  *
	  * @param WebDriver	The driver object to be used 
	  * @param By	selector to find the element
	  * @param int	The time in seconds to wait until returning a failure
	  * 
	  * @return WebElement	the first WebElement using the given method, or null (if the timeout is reached)
	  */
	public static WebElement waitForElementPresent(WebDriver driver, final By by, int timeOutInSeconds) {
		WebElement element; 
		try{
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS); //nullify implicitlyWait() 
			
			WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds); 
			element = wait.until(ExpectedConditions.presenceOfElementLocated(by));
			
			driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_4_PAGE, TimeUnit.SECONDS); //reset implicitlyWait
			return element; //return the element
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null; 
	}
	

	/**
	  * Wait for the List<WebElement> to be present in the DOM, regardless of being displayed or not.
	  * Returns all elements within the current page DOM. 
	  * 
	  * @param WebDriver	The driver object to be used 
	  * @param By	selector to find the element
	  * @param int	The time in seconds to wait until returning a failure
	  *
	  * @return List<WebElement> all elements within the current page DOM, or null (if the timeout is reached)
	  */
	public static List<WebElement> waitForListElementsPresent(WebDriver driver, final By by, int timeOutInSeconds) {
		List<WebElement> elements; 
		try{	
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS); //nullify implicitlyWait() 
			  
			WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds); 
			wait.until((new ExpectedCondition<Boolean>() {
	            public Boolean apply(WebDriver driverObject) {
	                return areElementsPresent(driverObject, by);
	            }
	        }));
			
			elements = driver.findElements(by); 
			driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_4_PAGE, TimeUnit.SECONDS); //reset implicitlyWait
			return elements; //return the element	
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null; 
	}

	/**
	  * Wait for an element to appear on the refreshed web-page.
	  * And returns the first WebElement using the given method.
	  *
	  * This method is to deal with dynamic pages.
	  * 
	  * Some sites I (Mark) have tested have required a page refresh to add additional elements to the DOM.  
	  * Generally you (Chon) wouldn't need to do this in a typical AJAX scenario.
	  * 
	  * @param WebDriver	The driver object to use to perform this element search
	  * @param locator	selector to find the element
	  * @param int	The time in seconds to wait until returning a failure
	  * 
	  * @return WebElement	the first WebElement using the given method, or null(if the timeout is reached)
	  * 
	  * @author Mark Collin 
	  */
	 public static WebElement waitForElementRefresh(WebDriver driver, final By by, 
			                           int timeOutInSeconds) {
		WebElement element; 
		try{	
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS); //nullify implicitlyWait() 
		        new WebDriverWait(driver, timeOutInSeconds) {
		        }.until(new ExpectedCondition<Boolean>() {

		            
		            public Boolean apply(WebDriver driverObject) {
		                driverObject.navigate().refresh(); //refresh the page ****************
		                return isElementPresentAndDisplay(driverObject, by);
		            }
		        });
		    element = driver.findElement(by);
			driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_4_PAGE, TimeUnit.SECONDS); //reset implicitlyWait
			return element; //return the element
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return null; 
	 }
	 
	/**
	  * Wait for the Text to be present in the given element, regardless of being displayed or not.
	  *
	  * @param WebDriver	The driver object to be used to wait and find the element
	  * @param locator	selector of the given element, which should contain the text
	  * @param String	The text we are looking
	  * @param int	The time in seconds to wait until returning a failure
	  * 
	  * @return boolean 
	  */
	public static boolean waitForTextPresent(WebDriver driver, final By by, final String text, int timeOutInSeconds) {
		boolean isPresent = false; 
		try{	
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS); //nullify implicitlyWait() 
	        new WebDriverWait(driver, timeOutInSeconds) {
	        }.until(new ExpectedCondition<Boolean>() {
	
	            
	            public Boolean apply(WebDriver driverObject) {
	            	return isTextPresent(driverObject, by, text); //is the Text in the DOM
	            }
	        });
	        isPresent = isTextPresent(driver, by, text);
			driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_4_PAGE, TimeUnit.SECONDS); //reset implicitlyWait
			return isPresent; 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false; 
	}
	
	/**
	  * Wait for the Text to be present in the given element, regardless of being displayed or not.
	  *
	  * @param WebDriver	The driver object to be used to wait and find the element
	  * @param locator	selector of the given element, which should contain the text
	  * @param String	The text we are looking
	  * @param int	The time in seconds to wait until returning a failure
	  * 
	  * @return boolean 
	  */
	public static boolean waitForTextNotPresent(WebDriver driver, final By by, final String text, int timeOutInSeconds) {
		boolean isPresent = false; 
		try{	
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS); //nullify implicitlyWait() 
	        new WebDriverWait(driver, timeOutInSeconds) {
	        }.until(new ExpectedCondition<Boolean>() {
	
	            
	            public Boolean apply(WebDriver driverObject) {
	            	return isTextNotPresent(driverObject, by, text); //is the Text in the DOM
	            }
	        });
	        isPresent = isTextNotPresent(driver, by, text);
			driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_4_PAGE, TimeUnit.SECONDS); //reset implicitlyWait
			return isPresent; 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false; 
	}
	



	/** 
	 * Waits for the Condition of JavaScript.  
	 *
	 *
	 * @param WebDriver		The driver object to be used to wait and find the element
	 * @param String	The javaScript condition we are waiting. e.g. "return (xmlhttp.readyState >= 2 && xmlhttp.status == 200)" 
	 * @param int	The time in seconds to wait until returning a failure
	 * 
	 * @return boolean true or false(condition fail, or if the timeout is reached)
	 **/
	public static boolean waitForJavaScriptCondition(WebDriver driver, final String javaScript, 
            								   int timeOutInSeconds) {
		boolean jscondition = false; 
		try{	
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS); //nullify implicitlyWait() 
	        new WebDriverWait(driver, timeOutInSeconds) {
	        }.until(new ExpectedCondition<Boolean>() {
	
	            
	            public Boolean apply(WebDriver driverObject) {
	            	return (Boolean) ((JavascriptExecutor) driverObject).executeScript(javaScript);
	            }
	        });
	        jscondition =  (Boolean) ((JavascriptExecutor) driver).executeScript(javaScript); 
			driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_4_PAGE, TimeUnit.SECONDS); //reset implicitlyWait
			return jscondition; 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false; 
	}

	
	/** Waits for the completion of Ajax jQuery processing by checking "return jQuery.active == 0" condition.  
	 *
	 * @param WebDriver - The driver object to be used to wait and find the element
	 * @param int - The time in seconds to wait until returning a failure
	 * 
	 * @return boolean true or false(condition fail, or if the timeout is reached)
	 * */
	public static boolean waitForJQueryProcessing(WebDriver driver, int timeOutInSeconds){
		boolean jQcondition = false; 
		try{	
			driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS); //nullify implicitlyWait() 
	        new WebDriverWait(driver, timeOutInSeconds) {
	        }.until(new ExpectedCondition<Boolean>() {
	
	            
	            public Boolean apply(WebDriver driverObject) {
	            	return (Boolean) ((JavascriptExecutor) driverObject).executeScript("return jQuery.active == 0");
	            }
	        });
	        jQcondition = (Boolean) ((JavascriptExecutor) driver).executeScript("return jQuery.active == 0");
			driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_4_PAGE, TimeUnit.SECONDS); //reset implicitlyWait
			return jQcondition; 
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return jQcondition; 
    }
	

	/**
	 * Coming to implicit wait, If you have set it once then you would have to explicitly set it to zero to nullify it -
	 */
	public static void nullifyImplicitWait(WebDriver driver) {
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS); //nullify implicitlyWait() 
	} 
	

	/**
	 * Set driver implicitlyWait() time. 
	 */
	public static void setImplicitWait(WebDriver driver, int waitTime_InSeconds) {
		driver.manage().timeouts().implicitlyWait(waitTime_InSeconds, TimeUnit.SECONDS);  
	} 
	
	/**
	 * Reset ImplicitWait.  
	 * To reset ImplicitWait time you would have to explicitly 
	 * set it to zero to nullify it before setting it with a new time value. 
	 */
	public static void resetImplicitWait(WebDriver driver) {
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS); //nullify implicitlyWait() 
		driver.manage().timeouts().implicitlyWait(DEFAULT_WAIT_4_PAGE, TimeUnit.SECONDS); //reset implicitlyWait
	} 
	

	/**
	 * Reset ImplicitWait.  
	 * @param int - a new wait time in seconds
	 */
	public static void resetImplicitWait(WebDriver driver, int newWaittime_InSeconds) {
		driver.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS); //nullify implicitlyWait() 
		driver.manage().timeouts().implicitlyWait(newWaittime_InSeconds, TimeUnit.SECONDS); //reset implicitlyWait
	} 
		    

     /**
	   * Checks if the text is present in the element. 
       * 
	   * @param driver - The driver object to use to perform this element search
	   * @param by - selector to find the element that should contain text
	   * @param text - The Text element you are looking for
	   * @return true or false
	   */
	private static boolean isTextPresent(WebDriver driver, By by, String text)
	{
		try {
				return driver.findElement(by).getText().contains(text);
		} catch (NullPointerException e) {
				return false;
		}
	}
	
	 /**
	   * Checks if the text is present in the element. 
     * 
	   * @param driver - The driver object to use to perform this element search
	   * @param by - selector to find the element that should contain text
	   * @param text - The Text element you are looking for
	   * @return true or false
	   */
	private static boolean isTextNotPresent(WebDriver driver, By by, String text)
	{
		try {
				return !driver.findElement(by).getText().contains(text);
		} catch (NullPointerException e) {
				return false;
		}
	}
		

	/**
	 * Checks if the elment is in the DOM, regardless of being displayed or not.
	 * 
	 * @param driver - The driver object to use to perform this element search
	 * @param by - selector to find the element
	 * @return boolean
	 */
	private static boolean isElementPresent(WebDriver driver, By by) {
		try {
			driver.findElement(by);//if it does not find the element throw NoSuchElementException, which calls "catch(Exception)" and returns false; 
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
	

	/**
	 * Checks if the List<WebElement> are in the DOM, regardless of being displayed or not.
	 * 
	 * @param driver - The driver object to use to perform this element search
	 * @param by - selector to find the element
	 * @return boolean
	 */
	private static boolean areElementsPresent(WebDriver driver, By by) {
		try {
			driver.findElements(by); 
			return true; 
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	/**
	 * Checks if the elment is in the DOM and displayed. 
	 * 
	 * @param driver - The driver object to use to perform this element search
	 * @param by - selector to find the element
	 * @return boolean
	 */
	private static boolean isElementPresentAndDisplay(WebDriver driver, By by) {
		try {			
			return driver.findElement(by).isDisplayed();
		} catch (NoSuchElementException e) {
			return false;
		}
	}
	

	
 }

 
 
 ====================================================
 
 
 
 
 package amex.qa.logger.util;

import java.util.Date;

import org.testng.Reporter;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

public class GEMTLoggingFilter extends Filter<ILoggingEvent> {

	@Override
	public FilterReply decide(ILoggingEvent loggingEvent) {
		if(Level.INFO.equals(loggingEvent.getLevel())){
			Reporter.log("INFO: "+new Date(loggingEvent.getTimeStamp())+":"+loggingEvent.getFormattedMessage()+"<br />",0);
		}
		if(Level.ERROR.equals(loggingEvent.getLevel())){
			Reporter.log("<font color='red'>ERROR:"+loggingEvent.getFormattedMessage()+"</font><br />",0);
		}
		return FilterReply.ACCEPT;
	}

}



==========================================

documents\BrowserList.txt

android
chrome
firefox
htmlUnitWithJs
internetExplorer
ipad
iphone
opera
safari



=============================================

test\resources\db2.prop

url=jdbc:db2://<url>
username=ubame
password=pas
autocommit=false
driverclass=com.ibm.db2.jcc.DB2Driver

==================

test\resources\Defaultdatabase.properties

Databases=DB2

==================
test\resources\Global.properties

##SAUCE USERNAME
#userName=username
##SAUCE KEY
#key=key
##USE SAUCE TO RUN TEST
#blnUseSauce=false


##BROWSER PARAMETERS
#os=WINDOWS
##BROWSER - must use value from Selenium Grid acceptable list
browser=Firefox
##TEST URLS
baseUrl=https://dwww420.app.aexp.com/merchant/services/disputes/

##Selenium GRid Browser Acceptable list - spelling 
##android
##chrome
##firefox
##htmlUnitWithJs
##internetExplorer
##ipad
##iphone
##opera
##safari


==================
test\resources\labletext_en.properties
<empty>

==================
test\resources\labletext_fr.properties

<empty>
==================


installthirdparty.bat

@echo off

%~d0
cd %~p0

START mvn install:install-file -Dfile=lib\phantomjsdriver-1.1.0.jar -DgroupId=com.github.detro.ghostdriver -DartifactId=phantomjsdriver -Dversion=1.1.0 -Dpackaging=jar



=====================================


<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<groupId>com.aexp.itlabs.test.automation</groupId>
	<artifactId>ui-framework</artifactId>
	<version>0.0.2-SNAPSHOT</version>
	<name>ITALABS_AMEX_POM_Framework</name>
	<description>This is a Page Object Model project frameworK for AMEX Payments and Disputes</description>
	<parent>
		<groupId>com.aexp</groupId>
		<artifactId>aexp</artifactId>
		<version>18.0</version>
	</parent>
	<build>
		<sourceDirectory>src</sourceDirectory>
		<plugins>
			<plugin>
				 <artifactId>maven-compiler-plugin</artifactId>
                			<configuration>
                    			<source>${java-version}</source>
                   				 <target>${java-version}</target>
               				 </configuration>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-jar-plugin</artifactId>
				<version>2.3.2</version>
				<executions>
					<execution>
						<phase>package</phase>
						<goals>
							<goal>test-jar</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
			<plugin>
				<groupId>org.apache.maven.plugins</groupId>
				<artifactId>maven-surefire-plugin</artifactId>
				<version>2.5</version>
			</plugin>
		</plugins>
		<resources>
			<resource>
				<directory>src/test/resources</directory>
				<filtering>true</filtering>
			</resource>
		</resources>
	</build>
	<dependencies>
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-api</artifactId>
			<version>1.7.6</version>
		</dependency>
		<dependency>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-simple</artifactId>
			<version>1.6.1</version>
		</dependency>
		<dependency>
			<groupId>ch.qos.logback</groupId>
			<artifactId>logback-core</artifactId>
			<version>0.9.28</version>
		</dependency>
		<dependency>
			<groupId>ch.qos.logback</groupId>
			<artifactId>logback-classic</artifactId>
			<version>0.9.28</version>
		</dependency>
		<dependency>
			<groupId>org.testng</groupId>
			<artifactId>testng</artifactId>
			<version>6.8.5</version>
		</dependency>
		<dependency>
			<groupId>org.seleniumhq.selenium</groupId>
			<artifactId>selenium-java</artifactId>
			<version>2.43.1</version>
		</dependency>
		<dependency>
			<groupId>com.google.inject</groupId>
			<artifactId>guice</artifactId>
			<version>3.0</version>
		</dependency>
		<!-- <dependency>
			<groupId>com.github.detro.ghostdriver</groupId>
			<artifactId>phantomjsdriver</artifactId>
			<version>1.1.0</version>
		</dependency> -->

		<dependency>
			<groupId>org.seleniumhq.selenium</groupId>
			<artifactId>selenium-safari-driver</artifactId>
			<version>2.43.1</version>
		</dependency>
		<dependency>
			<groupId>com.ibm.db2</groupId>
			<artifactId>db2jcc_license_cu</artifactId>
			<version>3.63.75</version>
		</dependency>
		<dependency>
			<groupId>com.ibm.db2</groupId>
			<artifactId>db2jcc</artifactId>
			<version>3.63.75</version>
		</dependency>
		<dependency>
			<groupId>com.ibm.db2</groupId>
			<artifactId>db2jcc4</artifactId>
			<version>9.7.4</version>
		</dependency>
		<!-- Reporting jars -->
		<dependency>
			<groupId>com.github.spullara.mustache.java</groupId>
			<artifactId>compiler</artifactId>
			<version>0.8.15</version>
		</dependency>

		<dependency>
			<groupId>org.apache.poi</groupId>
			<artifactId>poi</artifactId>
			<version>3.9</version>
		</dependency>
		<dependency>
			<groupId>org.apache.poi</groupId>
			<artifactId>poi-ooxml</artifactId>
			<version>3.9</version>
			<exclusions>
				<exclusion>
					<artifactId>xml-apis</artifactId>
					<groupId>xml-apis</groupId>
				</exclusion>
			</exclusions>
		</dependency>
		<dependency>
			<groupId>org.apache.poi</groupId>
			<artifactId>poi-ooxml-schemas</artifactId>
			<version>3.9</version>
		</dependency>
		<dependency>
			<groupId>org.apache.poi</groupId>
			<artifactId>xmlbeans</artifactId>
			<version>2.4.0</version>
		</dependency>
		
	</dependencies>
