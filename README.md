EmployeeDataV2
==============
package com.amex.main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amex.base.TestBase;
import com.amex.utils.Constants;
import com.amex.utils.FileUtil;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class DriverScript extends TestBase { 
	private String requestURL;
	private String headerKey; 
	private String headerValue; 
	private String requestName;
	private String requestParam; 
	private String expectedResponseCode; 
	private String requestBody; 
	private String contentType; 
	private String requestMethod; 
	private String responseKeySet; 
	private String errorName;
	private int currentTestcase;
	private boolean testFailed=false;
	private String	tcid;
	private static Logger logger = LoggerFactory.getLogger(DriverScript.class);


	/**
	 * This method is the main method that starts the execution.
	 * 
	 * @throws IOException
	 */
	public void run() throws IOException{

		clearResults();
		logger.info("Test Execution started.");

		for (currentTestcase = 2; currentTestcase <= reader.getRowCount(Constants.SHEET_NAME); currentTestcase++) {
			HashMap<String, String> values = reader.getRowData(Constants.SHEET_NAME, currentTestcase);
			String runMode = values.get(Constants.COLUMN_RUN_MODE).trim();
			tcid = values.get(Constants.COLUMN_TCID).trim();

			if(runMode.equalsIgnoreCase("YES")){
				// Initialize all the values from test data sheet
				initialize(values);
				replaceURLParameters(requestParam);
				FileUtil.createFile(getFileOutPath(), tcid+"_Request.txt", requestBody);
				Response response = getResponse();

				if(response!=null){
					FileUtil.createFile(getFileOutPath(), tcid+"_Response.txt", response.asString());
					validateResponse(response);

					// Updating the pass result only if there is no failure
					if(!testFailed){
						testPassed();
					}
				}
			}else{
				logger.info("Test Skipped : "+tcid);
				testSkipped();				
			}			
		}
	}	


	/**
	 * The method clears all the previous test results from the test data sheet.
	 */
	private void clearResults() {
		logger.info("Clearing all the Test results from Excel sheet");
		for (currentTestcase = 2; currentTestcase <= reader.getRowCount(Constants.SHEET_NAME); currentTestcase++) {
			reader.setCellData(Constants.SHEET_NAME, Constants.COLUMN_FAILURE_CAUSE, currentTestcase, "");
		}
		testFailed=false;
	}


	private void initialize(HashMap<String, String> values) {

		requestURL           = Constants.URL+ values.get(Constants.COLUMN_API).trim();          
		headerKey            = values.get(Constants.COLUMN_HEADER_KEY).trim();
		headerValue          = values.get(Constants.COLUMN_HEADER_VALUE).trim();
		requestName          = values.get(Constants.COLUMN_REQUEST_NAME).trim();
		requestParam         = values.get(Constants.COLUMN_REQUEST_PARAM).trim();
		expectedResponseCode = values.get(Constants.COLUMN_RESPONSE_CODE).trim();
		requestMethod        = values.get(Constants.COLUMN_REQUEST_METHOD).trim();          
		contentType          = Constants.CONTENT_TYPE_JSON;
		requestBody          = generateValidRequestBody(requestName, requestParam);
		errorName            = values.get(Constants.COLUMN_ERROR_NAME).trim();
		responseKeySet       = values.get(Constants.COLUMN_RESPONSE_KEY).trim();
	}


	private Response getResponse() {
		Response response = null;
		RestAssured.useRelaxedHTTPSValidation();
		try{

			if(requestMethod.equalsIgnoreCase("POST")){
				// Call POST service 
				response = RestAssured.given().headers(headerKey, headerValue)
				.body(requestBody).contentType(contentType).post(requestURL)
				.andReturn();
			}else if (requestMethod.equalsIgnoreCase("GET")) {
				// Call GET service
				response = RestAssured.given().headers(headerKey, headerValue)
				.contentType(contentType).get(requestURL)
				.andReturn();
			}
		}catch(Exception e){
			testFailed(e.getLocalizedMessage());
			logger.info(e.getMessage(), e);


		}
		return response;
	}


	private void validateResponse(Response response) {

		int actualResponseCode=response.getStatusCode();      
		int expResponseCode=(int) Float.parseFloat(expectedResponseCode);     

		if(actualResponseCode==expResponseCode){
			if(actualResponseCode==200){
				validateValidResponse(response);
			}else if(actualResponseCode==400||actualResponseCode==499){
				validateErrorResponse(response);
			}else{
				logger.info("The response code does not fall in 200/400/499, "
						+ actualResponseCode);
			}
		}else{
			// TODO Fail the test and do the logging
			testFailed("Exp response: "
					+ expResponseCode + " Act response: "
					+ actualResponseCode);
		}
	}


	private void validateValidResponse(Response response) {

		// Fetching the JSON response
		JsonPath json = response.getBody().jsonPath();

		// Read the expected response values to be validated
		String[] responseKeys = responseKeySet.split(",");

		// TODO Validate against DB
		for (int i = 0; i < responseKeys.length; i++) {
			String key = responseKeys[i].trim();
			String actualValue = json.getString(key);
			System.out.println(key + "-----" + actualValue);
		}
		System.out.println(response.asString());
	}


	private void validateErrorResponse(Response response) {     
		// Fetching the JSON response
		JsonPath json = response.getBody().jsonPath();

		// Get the expected error details from Property files
		String[] expValues = errorCodesProp.getProperty(errorName).split(",");

		// Read the expected response values to be validated
		String[] responseKeys = responseKeySet.split(",");

		// Validating the error response details 
		for(int i=0;i<expValues.length;i++){            
			String ActualValue = json.getString((responseKeys[i].trim()));
			if(!expValues[i].equalsIgnoreCase(ActualValue)){
				// TO-DO Logging and updating excel sheet
				System.out.println("Test Failed : Expected : "+expValues[i]+" Actual : "+ActualValue);
			}
		}
		System.out.println(response.asString());
	} 


	private String getRequestSchema(String requestName) {
		return requestProp.getProperty(requestName);
	}


	private String generateValidRequestBody(String requestName, String requestParam) {
		if(requestMethod.equalsIgnoreCase("POST")){
			String request = getRequestSchema(requestName);
			HashMap<String, String> paramsMap = generateRequestParamsMap(requestParam);
			String finalRequest = replaceRequestParameters(request, paramsMap);
			return finalRequest;
		}
		return null;
	}


	private void replaceURLParameters(String requestParam){
		if (requestMethod.equalsIgnoreCase("GET")){
			HashMap<String, String> paramsMap = generateRequestParamsMap(requestParam);	
			requestURL =replaceRequestParameters(requestURL, paramsMap);
		}
	}


	private HashMap<String, String> generateRequestParamsMap(String params) {
		HashMap<String, String> paramsMap = new HashMap<String, String>();
		String[] paramSet = params.split(",");
		for (int i = 0; i < paramSet.length; i++) {
			String[] param = paramSet[i].split(":");
			if(param[1].equalsIgnoreCase("null")){
				param[1]="";
			}
			paramsMap.put(param[0].trim(), param[1].trim());
		}
		return paramsMap;
	}


	private String replaceRequestParameters(String request, HashMap<String, String> params) {
		System.out.println(request);
		for (Map.Entry<String, String> entry : params.entrySet()) {
			request = request.replace(entry.getKey(), entry.getValue());
		}
		return request;
	}


	private String getFileOutPath() {
		return System.getProperty("user.dir")+Constants.TEST_OUTPUT_PATH + "\\"+currentTimeStamp + "\\ ";
	}


	private void testSkipped(){
		reader.setCellData(Constants.SHEET_NAME, Constants.COLUMN_TEST_RESULT, currentTestcase, Constants.TEST_SKIP);
	}


	private void testPassed(){
		reader.setCellData(Constants.SHEET_NAME, Constants.COLUMN_TEST_RESULT, currentTestcase, Constants.TEST_PASSED);
	}


	private void testFailed(String failureCause){
		logger.info("Test Failed : "+failureCause);
		reader.setCellData(Constants.SHEET_NAME, Constants.COLUMN_TEST_RESULT, currentTestcase, Constants.TEST_FAILED);
		reader.setCellData(Constants.SHEET_NAME, Constants.COLUMN_FAILURE_CAUSE, currentTestcase, failureCause);
		testFailed=true;
	}
}



================================================================================================================



package com.amex.base;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amex.utils.Constants;
import com.amex.utils.ExcelReader;
import com.amex.utils.FileReader;

public class TestBase { 
	protected ExcelReader reader; 
	protected Properties requestProp;
	protected Properties errorCodesProp;
	protected String	currentTimeStamp;
	private static Logger logger = LoggerFactory.getLogger(TestBase.class);

	public void load() throws IOException{
		logger.info("Loading all the required files");

		// Loading Request property file		
		requestProp = loadPropertyFile(System.getProperty("user.dir")+Constants.REQUEST_PROP_PATH);    

		// Loading Error Codes property file		
		errorCodesProp = loadPropertyFile(System.getProperty("user.dir")+Constants.ERROR_CODES_PROP_PATH);

		// Loading the Test data excel sheet		
		reader = FileReader.getExcelReader(System.getProperty("user.dir")+Constants.TESTDATA_PATH);

		currentTimeStamp = getCurrentTimeStamp();
	}

	private String getCurrentTimeStamp() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy_HH_mm_ss");
		Date now = new Date();
		String strDate = sdfDate.format(now);
		return strDate;
	}

	private Properties loadPropertyFile(String path) {
		logger.info("Loading the property file : "+path);
		Properties prop = new Properties();  
		FileInputStream fis=null;

		try {
			fis = new FileInputStream(path);
			prop.load(fis);
		} catch (FileNotFoundException e)  {
			logger.info(e.getMessage(), e);
		}catch (IOException e) {
			logger.info(e.getMessage(), e);
		}finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return prop;
	}
}


==================================================================================================================



package com.amex.main;

import java.io.IOException;

public class MainTest {	

	public static void main(String[] args)  {		
		DriverScript driver = new DriverScript();
		try {
			driver.load();	
			driver.run();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
}
==================================================================================================================


package com.amex.utils;

public class Constants {

public static final String URL 					   = "http://192.168.1.16:8080/EmployeeData";
//public static final String URL 					   = "https://dwww420.app.aexp.com";
public static final String REQUEST_PROP_PATH       = "\\src\\com\\amex\\config\\request\\Request.properties";
public static final String ERROR_CODES_PROP_PATH   = "\\src\\com\\amex\\config\\errorhandles\\ErrorCodes.properties";
public static final String SHEET_NAME              = "Sheet1";
public static final String TESTDATA_PATH           = "\\src\\com\\amex\\testdata\\API_TestData.xlsx";
public static final String CONTENT_TYPE_JSON       = "application/json";
public static final String TEST_OUTPUT_PATH	       = "\\Test_Output_files";

public static final String COLUMN_TCID	           = "TCID";
public static final String COLUMN_API              = "API";
public static final String COLUMN_HEADER_KEY       = "Header_Key";
public static final String COLUMN_HEADER_VALUE     = "Header_Value";
public static final String COLUMN_REQUEST_NAME     = "Request_Name";
public static final String COLUMN_REQUEST_PARAM    = "Request_Parameters";
public static final String COLUMN_RESPONSE_CODE    = "Expected_Response_Code";
public static final String COLUMN_REQUEST_METHOD   = "Request_Method";
public static final String COLUMN_ERROR_NAME       = "Error_Name";
public static final String COLUMN_RESPONSE_KEY     = "Response_Keys";
public static final String COLUMN_RUN_MODE         = "Run_Mode";
public static final String COLUMN_TEST_RESULT      = "Test_Result";
public static final String COLUMN_FAILURE_CAUSE    = "Failure_Cause";

public static final String TEST_SKIP               = "Skipped";
public static final String TEST_PASSED             = "Passed";
public static final String TEST_FAILED             = "Failed";




}



==================================================================================================================





package com.amex.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileReader {
	private static Logger logger = LoggerFactory.getLogger(FileReader.class);
	
	public static ExcelReader getExcelReader(String excelFilePath) {
		
		if (excelFilePath == null) {
			return null;
		} else {
			logger.info("Loading the Excel file : "+excelFilePath);
			return ExcelReader.getInstance(excelFilePath);
		}
	}
}




=================================================================================================

package com.amex.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author Prokarma
 *
 */
public class DbReader {

	private Connection connection = null;
	private ResultSet resultSet = null;
	private PreparedStatement preparedStatement = null;
	private ResultSetMetaData resultSetMetaData = null;
	
	private static Logger logger = LoggerFactory.getLogger(DbReader.class);

	/**
	 * This method is used get the database connection 
	 * @param driver
	 * @param url
	 * @param userName
	 * @param password
	 * @param tableName
	 * 
	 */
	public DbReader(String driver, String url, String userName,
			String password, String tableName) {
		try {
			logger.info(" Trying to connect database");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, userName, password);
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(tableName);
			resultSet = preparedStatement.executeQuery();
			resultSetMetaData = resultSet.getMetaData();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

	private static volatile DbReader instance = null;

	
	/**
	 * This method create the instance of DbReader
	 * @param driver
	 * @param url
	 * @param userName
	 * @param password
	 * @param tableName
	 * @return DbReader
	 */
	public static DbReader getInstance(String driver, String url,
			String userName, String password, String tableName) {
		logger.info(" Trying to create instance for DB");
		if (instance == null) {
			synchronized (DbReader.class) {
				if (instance == null) {
					instance = new DbReader(driver, url, userName, password,
							tableName);
				}
			}
		}
		return instance;
	}

	/**
	 * This method read the data from database 
	 * @return List of map objects
	 */
	public List<Map<String, String>> read() {
		logger.info(" inside read() method");
		List<Map<String, String>> maplist = null;
		try {

			int colCount = resultSetMetaData.getColumnCount();
			Map<String, String> map = new HashMap<String, String>();
			maplist = new ArrayList<Map<String, String>>();
			while (resultSet.next()) {
				for (int i = 1; i <= colCount; i++) {
					map.put(resultSetMetaData.getColumnName(i).trim(), resultSet.getString(i)
							.trim());
				}
				maplist.add(map);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				resultSet.close();
				connection.close();
			} catch (SQLException e) {
				if(e.getMessage() != null && e.getMessage().isEmpty()){
					logger.error(e.getMessage());
				}
				else{
					logger.error(" Problem occured while getting data from db");
				}				
			}

		}
		return maplist;
	}
}




=============================================================================================================


package com.amex.test;

import java.util.List;
import java.util.Map;

import com.amex.utils.DbReader;


public class DBTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
	DbReader reader = DbReader.getInstance("COM.ibm.db2os390.sqlj.jdbc.DB2SQLJDriver", "jdbc:db2://adc1db2d.ipc.us.aexp.com:7320/ADC1DB2D", 
		"AH5807A", "rush1234", "select GLBL_USER_ID from OD1.TC391_USER where FIRST_NM = 'italabs' and LST_NM = 'labsita'");
	
	List<Map<String,String>> list = reader.read();
	
	
	System.out.println(list.toString());
	
	
    }

}
