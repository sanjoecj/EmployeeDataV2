package amex.qa.common;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DatabaseUtil {
	
	public abstract Connection openConnection();
	public void closeConnection(Connection con) {
		if(con!=null){
			try {
				con.commit();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

}





=============================================================



package amex.qa.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public class DatabaseUtilFactory {
	private static List<String> databaseNameProperties=new ArrayList<String>();
	
	static{
		Properties databaseProperties=new Properties();
		String databases;
		try {
			
			databaseProperties.load(DatabaseUtilFactory.class.getResourceAsStream("/Defaultdatabase.properties"));
			databases= databaseProperties.getProperty("Databases");
			if(StringUtils.isNotBlank(databases)){
				String[] database = databases.split(",");
				databaseNameProperties=Arrays.asList(database);
			}
		}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		catch (Exception e) {
			// TODO: handle exception
		}
			try{
				databaseProperties.load(DatabaseUtilFactory.class.getResourceAsStream("/Database.properties"));
				 databases = databaseProperties.getProperty("Databases");
					if(StringUtils.isNotBlank(databases)){
						String[] database = databases.split(",");
						databaseNameProperties=Arrays.asList(database);
					}
			}
			catch(IOException innerIo){
				
			}
			catch (Exception e) {
				// TODO: handle exception
			}
			
		
	}

	public DatabaseUtilFactory() {
		this("DB2");
	}
	public DatabaseUtilFactory(String databaseName){
		
	}
	
	public DatabaseUtil createDatabaseUtil(String databaseName){
		if(StringUtils.isBlank(databaseName)){
			throw new RuntimeException("User not supplied databaseName to interact");
		}
		if(databaseNameProperties.isEmpty()){
			throw new RuntimeException("No Database configuration found");
		}
		if(databaseNameProperties.contains(databaseName)){
			if(databaseName.equalsIgnoreCase("db2")){
				return new DB2DatabaseUtil();
			}
		}
		else{
			throw new RuntimeException("No Database is registred");
		}
		throw new RuntimeException("Unknow error occured unalble to create databaseUtil");
	}
	public DatabaseUtil createfDefaultDatabaseUtil(){
		return createDatabaseUtil("DB2");
	}
	
	
}



================================================================================


package amex.qa.common;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DB2DatabaseUtil extends DatabaseUtil{
	private static Properties databaseProperties=new Properties();
	static{
		try {
			databaseProperties.load(DatabaseUtilFactory.class.getResourceAsStream("/db2.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	public DB2DatabaseUtil() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Connection openConnection() {
		if(databaseProperties.isEmpty()){
			throw new RuntimeException("Unable to read the db2 configuration");
		}
		Connection connection=null;
		 try {
			Class.forName(databaseProperties.getProperty("driverclass"));
			connection = DriverManager.getConnection(databaseProperties.getProperty("url"), databaseProperties.getProperty("username"), databaseProperties.getProperty("password"));
		    connection.setAutoCommit(false);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 if(connection!=null){
			 return connection;
		 }
		 else{
			 throw new RuntimeException("Unable to open connection");
		 }
		    
	}
}



==========================================================================================


package amex.qa.common;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Global {
	
	
	private static String userName = "userName";
	private static String key = "key";
	private static String os = "WINDOWS";
	private static String browser = "internetExplorer";
	private static String browserVersion = null;
	private static ThreadLocal<String>  baseUrl = new  ThreadLocal<String>();
	private static String jenkinsMasterURL="http://localhost:4444/wd/hub";
	
	private static Properties globalProp = new Properties();
    
    private final static Logger logger = LoggerFactory.getLogger(Global.class);
    

    
	/*
	 * I know this isn't ideal approach - temporary fix
	 */
	private static void LoadProperties(){
		
		try {

			globalProp.load(Global.class.getResourceAsStream("/Global.properties"));
			
		} catch (IOException e) {
			
			logger.info("Unable to load Global.Properties file. IO Exceotion.");
			e.printStackTrace();
			System.exit(0);
		}
		catch (NullPointerException e)
		{
			
			logger.info("Unable to load Global.Properties file. File null.");
			e.printStackTrace();
			System.exit(0);
		}			
	}
	//LoadProperties
	
	public static String getUserName(){
		
		LoadProperties();
		
		if(globalProp.getProperty("userName") != null)
		{
			userName = globalProp.getProperty("userName");
		
		}			
		
		if (System.getProperty("userName") != null)
		{	
			userName = System.getProperty("userName");
		}
		
		
		return userName;
	}
	
	public static String getKey() {
		
		LoadProperties();
		
		if(globalProp.getProperty("key") != null)
		{
			key= globalProp.getProperty("key");
		}	
		
		if (System.getProperty("key") != null)
		{
			key = System.getProperty("key");
		}
		return key;
	}
	
	public static String getOS(){
		
		LoadProperties();
		
		if(globalProp.getProperty("os") != null)
		{
			os= globalProp.getProperty("os");
		}	
		
		if (System.getProperty("os") != null)
		{
			os = System.getProperty("os");
			logger.info("OS PARAMETER FOUND: " + os );
		}
		return os;
	}
	
	public static String getBrowser(){
		
		LoadProperties();
		
		
		
		if (System.getProperty("browser") != null)
		{
			browser = System.getProperty("browser");
			logger.info("BROWSER PARAMETER FOUND: " + browser );
			return browser;
		}
		if(globalProp.getProperty("browser") != null)
		{
			browser= globalProp.getProperty("browser");
		}	
		return browser;
	}
	
	public static String getBrowserVersion(){
		
		LoadProperties();
		
		if(globalProp.getProperty("browserVersion") != null)
		{
			browserVersion= globalProp.getProperty("browserVersion");
		}
		
		if (System.getProperty("browserVersion") != null)
		{
			browserVersion = System.getProperty("browserVersion");
			logger.info("BROWSER VERSION PARAMETER FOUND: " + browserVersion );
		}
		return browserVersion;
	}
	
	/*public static String getBaseUrl()
	{
		if(baseUrl==null){
			LoadProperties();
			if (System.getProperty("baseUrl") != null)
			{
				baseUrl = System.getProperty("baseUrl");
				logger.info("BASEURL PARAMETER FOUND: " + baseUrl );
				baseUrl.
				return baseUrl;
			}
			
			if(globalProp.getProperty("baseUrl") != null)
			{
				baseUrl= globalProp.getProperty("baseUrl");
			}
		}
		
		
		
		return baseUrl;
	}*/
	/*public static void setBaseUrl(String baseUrl1){
		baseUrl=baseUrl1;
	}*/
	
	public static void setBaseUrl(String baseUrl1){
		baseUrl.set(baseUrl1);
	}
	public static String getBaseUrl(){
		String baseUrl1 = baseUrl.get();
		if(StringUtils.isEmpty(baseUrl1)){
			LoadProperties();
			if (System.getProperty("baseUrl") != null)
			{
				 baseUrl1 = System.getProperty("baseUrl");
				logger.info("BASEURL PARAMETER FOUND: " + baseUrl );
				baseUrl.set(baseUrl1);
				
			}
			
			else if(globalProp.getProperty("baseUrl") != null)
			{
				baseUrl1= globalProp.getProperty("baseUrl");
				baseUrl.set(baseUrl1);
			}
		}
		return baseUrl1;
	}
	
	public static String getCanBaseUrl(){
		String baseUrl1 = baseUrl.get();
		if(StringUtils.isEmpty(baseUrl1)){
			LoadProperties();
			if (System.getProperty("baseUrlCan") != null)
			{
				 baseUrl1 = System.getProperty("baseUrlCan");
				logger.info("BASEURL PARAMETER FOUND: " + baseUrl );
				baseUrl.set(baseUrl1);
				
			}
			
			else if(globalProp.getProperty("baseUrlCan") != null)
			{
				baseUrl1= globalProp.getProperty("baseUrlCan");
				baseUrl.set(baseUrl1);
			}
		}
		return baseUrl1;
	}
	
	public static String getJenkinsMasterURL(){
		LoadProperties();

		if(globalProp.getProperty("jenkins.master.url") != null)
		{
			jenkinsMasterURL= globalProp.getProperty("jenkins.master.url");
		}
		
		if (System.getProperty("jenkins.master.url") != null)
		{
			jenkinsMasterURL = System.getProperty("jenkins.master.url");
			logger.info("jenkins.master.url : " + jenkinsMasterURL );
		}
		return jenkinsMasterURL;
	}
	public static boolean isRunningOnJenkins(){
		LoadProperties();
		boolean isJunkinsRun=false;
		if(globalProp.getProperty("jenkins.run") != null)
		{
			logger.info("globalProp.getProperty(\"jenkins.run\") "+globalProp.getProperty("jenkins.run"));
			if(globalProp.getProperty("jenkins.run").equalsIgnoreCase("True"))
			isJunkinsRun= true;
			logger.info("globalProp.getProperty(\"jenkins.run\") "+isJunkinsRun);
		}
		
		if (System.getProperty("jenkins.run") != null)
		{
			if(globalProp.getProperty("jenkins.run").equalsIgnoreCase("True"))
				isJunkinsRun= true;
				logger.info("globalProp.getProperty(\"jenkins.run\") "+isJunkinsRun);
		}
		return isJunkinsRun;
	}

	
}//class



===================================================================================================

package amex.qa.common;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Helper {
	
	 final static Logger logger = LoggerFactory.getLogger(Helper.class);

	
		public static Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
		    Map<String, String> query_pairs = new LinkedHashMap<String, String>();
		    String query = url.getQuery();
		    String[] pairs = query.split("&");
		    for (String pair : pairs) {
		        int idx = pair.indexOf("=");
		        query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
		    }
		    return query_pairs;
		}
	
	
	public static void Wait(Integer intSeconds)
	{
		logger.debug("Waiting " + intSeconds + " Seconds");

		Thread thread = new Thread();
		try {
			
			synchronized(thread){
				thread.wait(intSeconds * 1000);
			}
		}
		catch (InterruptedException e)
		{
			
		}
		
		logger.debug(intSeconds + " Wait Complete");
	}
	
	
	
	public void popupAlert(WebDriver driver) {
		String parentWindowHandle = driver.getWindowHandle(); // save the current window handle.
		WebDriver popup = null;
		Iterator<String> windowIterator = (Iterator<String>) driver.getWindowHandles();
		while(windowIterator.hasNext()) { 
			String windowHandle = windowIterator.next(); 
			popup = driver.switchTo().window(windowHandle);
			popup.findElement(By.name("ok")).submit();

		}
    }
	
	public void ieWindow(WebDriver driver) {
		String parentWindowHandle = driver.getWindowHandle(); // save the current window handle.
		WebDriver popup = null;
		Iterator<String> windowIterator = (Iterator<String>) driver.getWindowHandles();
		while(windowIterator.hasNext()) { 
			String windowHandle = windowIterator.next(); 
			popup = driver.switchTo().window(windowHandle);
			popup.findElement(By.name("no")).submit();

		}
    }
	
	
	public void handleAlert(WebDriver driver) throws Exception {
		  try {
		   Alert alert = driver.switchTo().alert();
		   alert.accept();
		  } catch (NoAlertPresentException e) {
			  System.out.println("Alert not caputured");
		  }
		 }
	
	
	
	public static boolean isElementPresent(By by,WebDriver driver) {
		try {
			 driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
			return driver.findElement(by).isEnabled();
		} catch (NoSuchElementException e) {
			System.out.println("Element is missing "+e);
			return false;
		} 
	}
	
	public static boolean isElementPresent(By by,WebDriver driver,Integer waitTime) {
		try {
			 driver.manage().timeouts().implicitlyWait(waitTime!=null?waitTime:30, TimeUnit.SECONDS);
			 /*if(driver.findElement(by).isEnabled()==true)*/
			 if(driver.findElement(by).isDisplayed()==true)
			 {
				 return driver.findElement(by).isEnabled();
			 }
			 else
			 {
				 return false;
			 }
		} catch (NoSuchElementException e) {
			System.out.println("Element is missing "+e);
			return false;
		} 
	}
	
	/*################################################################################################
	 * 	Function - To check the Sort - Ascending on each column 									 #	
	 * 	@Author - mgajula																			 #	
	 * 	@comments or input data -																	 #	
	 *################################################################################################*/
	
	public static boolean verifyAscending(ArrayList<String> resultArray) {
		int arraySize = resultArray.size();

		boolean ascending = false;
		
			for (int j = 0; j < (arraySize - 1); j++) 
			{

				if (resultArray.get(j).compareTo(resultArray.get(j + 1))<=0) 
				{ ascending = true;} else 
				{ ascending = false;
				  break;
				}
			}

/*			for (int j = 0; j < (arraySize - 1); j++) {

			if (Integer.parseInt(resultArray.get(j)) <= Integer
					.parseInt(resultArray.get(j + 1))) {
				ascending = true;
			} else {
				ascending = false;
				break;
			}
		}*/

		System.out.println("Boolean is " + ascending);
		return ascending;
	}
	
	/*################################################################################################
	 * 	Function - To check the Sort - Ascending on each column for date							 #	
	 * 	@Author - mgajula																			 #	
	 * 	@comments or input data -																	 #	
	 *################################################################################################*/


	public static boolean verifyDateSorting(ArrayList<String> resultArray) throws ParseException {
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/YYYY");
		int arraySize = resultArray.size();

		boolean ascending = false;

		for (int j = 0; j < (arraySize - 1); j++) {

		//	int compare = resultArray.get(j).compareTo(resultArray.get(j + 1));
			
    		
        	Date date1 = sdf.parse(resultArray.get(j));
        	Date date2 = sdf.parse(resultArray.get(j + 1));
 
        	System.out.println(sdf.format(date1));
        	System.out.println(sdf.format(date2));
 
        	if(date1.compareTo(date2)>0){
        		ascending = false;
        		System.out.println("Date1 is after Date2");
        		System.out.println("Ascending failed comparing "+date1+"  And "+date2) ;
        		break;
        		
        	}else if(date1.compareTo(date2)<0){
        		ascending = true;
        		System.out.println("Date1 is before Date2");
        	}else if(date1.compareTo(date2)==0){
        		ascending = true;
        		System.out.println("Date1 is equal to Date2");
        	}else{
        		System.out.println("How to get here?");
        	}

		}

		System.out.println("Boolean is " + ascending);
		return ascending;
	}

}




=================================================================================================================




package amex.qa.common;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;

public class LocalDriverEventListener implements WebDriverEventListener {

	public LocalDriverEventListener() {
		// TODO Auto-generated constructor stub
	}

	public void afterChangeValueOf(WebElement arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		
	}

	
	public void afterClickOn(WebElement arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		
	}

	
	public void afterFindBy(By arg0, WebElement arg1, WebDriver arg2) {
		// TODO Auto-generated method stub
		
	}

	
	public void afterNavigateBack(WebDriver arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void afterNavigateForward(WebDriver arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void afterNavigateTo(String arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		
	}

	
	public void afterScript(String arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		
	}

	
	public void beforeChangeValueOf(WebElement arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		
	}

	
	public void beforeClickOn(WebElement arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		
	}

	
	public void beforeFindBy(By arg0, WebElement arg1, WebDriver arg2) {
		// TODO Auto-generated method stub
		
	}

	
	public void beforeNavigateBack(WebDriver arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void beforeNavigateForward(WebDriver arg0) {
		// TODO Auto-generated method stub
		
	}

	
	public void beforeNavigateTo(String arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		
	}

	
	public void beforeScript(String arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		
	}

	
	public void onException(Throwable arg0, WebDriver arg1) {
		// TODO Auto-generated method stub
		
	}

}
