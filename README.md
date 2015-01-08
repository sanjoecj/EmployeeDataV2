

package com.amex.testfactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;

import com.amex.base.TestBase;
import com.amex.utils.Constants;
import com.amex.utils.DB2Manager;
import com.amex.utils.FileUtil;
import com.amex.utils.ParamsUtil;
import com.amex.utils.PropertiesReader;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class APITestFactory extends TestBase{
    	private String baseUrl;
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
	private String dbResultKeySet;
	private String errorName;
	protected String tcid;
	protected String fileoutpath;
	protected String urlParameters;
	private String dbQueries;
	private String requestValues;
	private String fileUploadSrcPath;
	private String responseStoreRetr;
	private String loginUrl;

	private List<Map<String, String>> resultsList;

	private DB2Manager db2Manager; 
	protected int currentTestcase;
	protected Boolean testFailed = false;
	private Boolean isDbQueried;
	private Boolean isLoginRequired;
	private static Logger logger = LoggerFactory.getLogger(APITestFactory.class);



	@BeforeClass
	public void init() {
		FileUtil.createDirectory(Constants.TEST_OUTPUT_PATH);
		fileoutpath = getFileOutPath(currentTimeStamp);
		try {
		    db2Manager  = new DB2Manager();
		} catch (Exception e) {
		    //Error in connection with DB
		    logger.info(e.getMessage());
		    testFailed(e.getMessage());
		    
		}
	}


	/**
	 * The method clears all the previous test results from the test data sheet.
	 */
	private void clearResults() {
		logger.info("Clearing all the Test results from Excel sheet");
		for (currentTestcase = 2; currentTestcase <= reader.getRowCount(Constants.SHEET_NAME); currentTestcase++) {
			reader.setCellData(Constants.SHEET_NAME, Constants.COLUMN_FAILURE_CAUSE, currentTestcase, "");
			reader.setCellData(Constants.SHEET_NAME, Constants.COLUMN_TEST_RESULT, currentTestcase, "");
		}
		testFailed = false;
	}


	/**
	 * Initialize all the required parameters for a web service call.
	 * 
	 * @param rowData
	 */
	protected void initialize(HashMap<String, String> rowData) {
	    	baseUrl		     = configProp.getProperty(Constants.KEY_URL);
		requestURL           = baseUrl + rowData.get(Constants.COLUMN_API).trim();
		headerKey            = rowData.get(Constants.COLUMN_HEADER_KEY).trim();
		headerValue          = rowData.get(Constants.COLUMN_HEADER_VALUE).trim();
		requestName          = rowData.get(Constants.COLUMN_REQUEST_NAME).trim();
		requestParam         = rowData.get(Constants.COLUMN_REQUEST_PARAM).trim();
		expectedResponseCode = rowData.get(Constants.COLUMN_RESPONSE_CODE).trim();
		requestMethod        = rowData.get(Constants.COLUMN_REQUEST_METHOD).trim();
		contentType          = Constants.CONTENT_TYPE_JSON;
		requestBody          = generateValidRequestBody(requestName, requestParam);
		errorName            = rowData.get(Constants.COLUMN_ERROR_NAME).trim();
		responseKeySet       = rowData.get(Constants.COLUMN_RESPONSE_KEY).trim();
		dbResultKeySet       = rowData.get(Constants.COLUMN_DB_RESULT_KEYS).trim();
		urlParameters        = rowData.get(Constants.COLUMN_URL_PARAMETERS).trim();
		dbQueries            = rowData.get(Constants.COLUMN_DB_QUERIES).trim();
		requestValues        = rowData.get(Constants.COLUMN_REQUEST_VALUES).trim();
		fileUploadSrcPath    = rowData.get(Constants.COLUMN_FILE_SRC_PATH).trim();
		responseStoreRetr    = rowData.get(Constants.COLUMN_RESPONSE_STORE_RETRIEVE).trim();
		loginUrl	     = configProp.getProperty(Constants.KEY_LOGIN_URL)
				        + "?userid="+headerValue+"&locale=en_US&submit=Submit";
		isLoginRequired	     = Boolean.valueOf(configProp.getProperty(Constants.KEY_LOGIN_REQUIRED));
		isDbQueried          = false;
	}


	/**
	 * Call the web service and get the response.
	 * 
	 * @return
	 */
	protected Response getResponse() {
		Response response = null;
		RestAssured.useRelaxedHTTPSValidation();
		
		try {
		    	logger.info("Final request url: " + requestURL);
		    	Map<String, String> cookies = new HashMap<String, String>();
		    	
		    	//When the header data needs to be sent as cookie, hit the login url, get the 
		    	//cookies and pass it in the actual request on hitting the request endpoint.
		    	if(isLoginRequired) {
		    	    Response cookieResp = RestAssured.given().redirects()
		    	    .follow(false).get(loginUrl);
		    	    cookies = cookieResp.cookies();
		    	    RestAssured.given().redirects().follow(false)
		    	    .cookies(cookies).get(baseUrl);
		    	}
		    	
			if (requestMethod.equalsIgnoreCase("POST")) {
				// Call POST service
				FileUtil.createFile(fileoutpath, tcid + "_Request.txt", requestBody);
				response = RestAssured.given().cookies(cookies).header(headerKey, headerValue)
				.contentType(contentType).body(requestBody)
				.post(requestURL).andReturn();
			} else if (requestMethod.equalsIgnoreCase("FILEPOST")) {
			    	HashMap<String, String> formParams;
			        formParams = getFormParams(requestParam);
			        if(formParams != null) {
			            response = RestAssured.given().cookies(cookies).headers(headerKey, headerValue)
				    .multiPart(new File(fileUploadSrcPath))
				    .formParams(formParams).post(requestURL);
			        }
			} else if (requestMethod.equalsIgnoreCase("DELETE")) {
			    	response = RestAssured.given().cookies(cookies).headers(headerKey, headerValue)
				.delete(requestURL).andReturn();
			    
			} else if (requestMethod.equalsIgnoreCase("GET")) {
				// Call GET service
				response = RestAssured.given().cookies(cookies).headers(headerKey, headerValue)
				.contentType(contentType).get(requestURL).andReturn();
			}
		
		storeResponseInTemp(response);

		}catch(UnknownHostException e){
			logger.info(e.getMessage(), e);
			testFailed("Host not found: " + e.getMessage());

		} catch (Exception exception) {
			testFailed(exception.getLocalizedMessage());
			logger.info(exception.getMessage(), exception);

		}
		return response;
	}


        /**
         * Get the parameters from temp.property files to get the form parameters that 
         * needs to be sent as headers.
         * @param reqParams
         * @return
         */
        private HashMap<String, String> getFormParams(String reqParams) {
        	if (!reqParams.isEmpty()) {
        	    // get the values from properties file
        	    if (reqParams.startsWith("TempProps[")) {
        		Properties tempProps = PropertiesReader.loadPropertyFile(Constants.TEMP_PROP_PATH);
        		String keys = reqParams.replaceAll(".*\\[|\\].*", "");
        		String[] keysArr = keys.split(",");
        		StringBuilder keyvalue = new StringBuilder();
        		for (int i = 0; i < keysArr.length; ++i) {
        		    keyvalue.append(keysArr[i].replace("_", "") + ":" + tempProps.getProperty(keysArr[i]));
        		    if (i != keysArr.length - 1) {
        			keyvalue.append(",");
        		    }
        		}
        		reqParams = keyvalue.toString();
        		return ParamsUtil.generateRequestParamsMap(reqParams);
        	    }
        	}
    	return null;
        }


	/**
	 * Store the response keys mentioned in the Excel sheet into the
	 * temp.properties file to be used in another test case.
	 * @param response
	 */
	private void storeResponseInTemp(Response response) {
	    
	    if (response != null) {
		JsonPath json = response.getBody().jsonPath();
		HashMap<String, String> paramsMap;
		Properties tempProps = PropertiesReader.loadPropertyFile(Constants.TEMP_PROP_PATH);
		if(!responseStoreRetr.isEmpty()) {
		    if(responseStoreRetr.startsWith("Save=")) {
			paramsMap = ParamsUtil.generateRequestParamsMap(responseStoreRetr.split("Save=")[1]);
		    
        		    for (Map.Entry<String, String> entry : paramsMap.entrySet()) {
        			String key = entry.getKey();
        			String value = entry.getValue();
        			String propValue = json.getString(value);
        			tempProps.put(key,propValue);
        		    }
        		    try {
				tempProps.store(new FileOutputStream(Constants.TEMP_PROP_PATH), "");
			    } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			    }
		    }
		}
	    }
	    
	}


	/**
	 * Check the HTTP response status code and validate the reponse. If 200 OK,
	 * assert the response with the values from DB If 400, 499, assert the error
	 * details in the response body with values in the ErrorCodes.properties
	 * file.
	 * 
	 * @param response
	 */
	protected void validateResponse(Response response) {

		int actualResponseCode = response.getStatusCode();
		int expResponseCode = (int) Float.parseFloat(expectedResponseCode);

		if (actualResponseCode == expResponseCode) {
			if (actualResponseCode == 200) {

				boolean isEmpty=true;

				if(!responseKeySet.isEmpty()) {
					validateValidResponse(response);
					isEmpty=false;
				}
				if(!requestValues.isEmpty()) {
					validateRequestValues();
					isEmpty=false;
				}
				if(isEmpty){
					testSkipped("Both Request Keys and Request Values are not provided");
				}

			} else if (actualResponseCode == 400 || actualResponseCode == 499 || actualResponseCode == 401) {
				validateErrorResponse(response);
			} else {
				logger.info("The response code does not fall in 200/400/499/401, " + actualResponseCode);
				testFailed("The response code does not fall in 200/400/499/401, " + actualResponseCode);
			}
		} else {    
			testFailed("Exp response: " + expResponseCode + " Act response: " + actualResponseCode);

		}
	}


	/**
	 * When HTTP Response Code 200, use this method to validate the response
	 * obtained against DB values.
	 * 
	 * @param response
	 */
	private void validateValidResponse(Response response) {

		// Fetching the JSON response
		JsonPath json = response.getBody().jsonPath();

		// Read the expected response values to be validated
		String[] responseKeys = responseKeySet.split(",");
		String[] dbResultKeys = dbResultKeySet.split(",");
		try {
			List<String> queriesList = db2Manager.getQueries(dbQueries);
			resultsList = db2Manager.executeQueries(queriesList);
			isDbQueried = true;
			String resultString="";
			if(responseKeys.length == dbResultKeys.length) {
				for (int i = 0; i < responseKeys.length; i++) {
					String key = responseKeys[i].trim();
					String dbkey = dbResultKeys[i].trim();

					// Getting value from JSON response
					String actualValue;

					try{
						actualValue = json.getString(key);
					}catch(IllegalArgumentException iae){
						logger.info("Trying to access an element that is not available : ",iae);
						actualValue="";
						testFailed("Trying to access an element that is not available : " + iae.getMessage() );
					}catch(NullPointerException npe){
						logger.info("Json object for "+key+" returned null : ",npe);
						actualValue="";						
					}
					
					// Getting value from Database
					String expectedValue = db2Manager.getRowWiseValue(resultsList, dbkey);
					expectedValue = expectedValue != null ? expectedValue.trim() : expectedValue;
					

					logger.info("Expected Value [From DB] : " + dbkey + " = " + expectedValue);
					logger.info("Actual Value [From JSON] : " + key + " = " + actualValue);

					// Replacing empty if the value returned by JSON response is null.
					if(actualValue==null){
						actualValue="";
					}else{
						// Trim the value only if it is not null
						actualValue=actualValue.trim();
					}

					// Replacing empty if the value returned by DB response is null.
					if(expectedValue==null)
						expectedValue="";

					// Check the values are not matching and append the values to a result string
					if(!actualValue.equals(expectedValue)){

						resultString=resultString+"Expected Value [From DB] : " + dbkey + " = " + expectedValue+"\n";
						resultString=resultString+"Actual Value [From JSON] : " + key + " = " + actualValue+"\n\n";

						// TODO Debugging purpose - remove the code
						System.out.println("Expected Value : " + dbkey + " = " + expectedValue);
						System.out.println("Actual Value   : " + key + " = " + actualValue);

					}
				}

				// Report all the mismatching values to the Excel report
				if(!resultString.isEmpty()){
					testFailed(resultString);
				}

			}else{
				testSkipped("Response Keys and DB keys count does not match \n" +
						" Response Keys Length : "+responseKeys.length +"\n" +
						" DB  Keys  Length     : "+dbResultKeys.length +"\n");
			}
		}catch (Exception e) {
			logger.info(e.getMessage(),e);
			testFailed(e.getMessage());

		}
	}


	/**
	 * When HTTP Response Code 400/499, use this method to validate the error
	 * response obtained against the values in properties file.
	 * 
	 * @param response
	 */
	private void validateErrorResponse(Response response) {
		// Fetching the JSON response
		JsonPath json = response.getBody().jsonPath();

		if(!errorCodesProp.containsKey(errorName))
			testSkipped("Error Name["+ errorName +"] is not available in the Error property file");
		
		String actualErrName = json.getString("errors[0].errorNm").trim();
		if(!errorName.equalsIgnoreCase(actualErrName)){
		    testFailed("\nExpected Error Name : "+errorName + "\n" 
			    +"Actual Error Name : "+actualErrName);
		}
		
		// Get the expected error details from Property files
		String[] expValues = errorCodesProp.getProperty(errorName).split(";");

		// Read the expected response values to be validated
		String[] responseKeys = responseKeySet.split(",");

		if(expValues.length!=responseKeys.length)
			testSkipped("Response Keys and Error keys count does not match");

		// Validating the error response details
		for (int count = 0; count < expValues.length; count++) {

			String actualValue = json.getString((responseKeys[count].trim()));
			String expectedValue = expValues[count].trim();

			if (!expectedValue.equalsIgnoreCase(actualValue)) {
				testFailed("\nExpected Error Code: "
						+ expectedValue + "\nActual Error Code: " + actualValue);
			}
		}
	}


	/**
	 * Get the json request defined in the Request.properties file
	 * 
	 * @param requestName
	 * @return
	 */
	private String getRequestSchema(String requestName) {
		// Failing the test if the Request Schema key is not available
		if(!requestProp.containsKey(requestName)){
			testFailed("Request Key is not available in the JSON request properties file");
		}
		return requestProp.getProperty(requestName);
	}


	/**
	 * Generates a valid json request from the json obatained from the
	 * properties file. Uses the request parameters provided in the Excel sheet
	 * to form a valid Json Request body.
	 * 
	 * @param requestName
	 * @param requestParam
	 * @return
	 */
	private String generateValidRequestBody(String requestName,
			String requestParam) {
		if (requestMethod.equalsIgnoreCase("POST")) {
			String request = getRequestSchema(requestName);
			String finalRequest = ParamsUtil.replaceParams(request, requestParam);
			return finalRequest.trim();
		}
		return null;
	}


	/**
	 * Replace the place holder parameters in the URL with valid parameter values
	 * obtained from excel sheet.
	 * 
	 * @param urlParams
	 */
	protected void replaceURLParameters(String urlParams) {
		if(!urlParams.isEmpty()){
		    if(urlParams.startsWith("TempProps[")) {
			Properties tempProps = PropertiesReader.loadPropertyFile(Constants.TEMP_PROP_PATH);
			String keys = urlParams.replaceAll(".*\\[|\\].*", "");
			String[] keysArr = keys.split(",");
			StringBuilder keyvalue = new StringBuilder();
			for(int i = 0; i<keysArr.length; ++i){
			    keyvalue.append(keysArr[i] + ":" + tempProps.getProperty(keysArr[i]));
			    if(i != keysArr.length-1) {
				keyvalue.append(",");
			    }
			    
			}
			urlParams=keyvalue.toString();
		    }
			requestURL = ParamsUtil.replaceParams(requestURL, urlParams);
			writeURLToFile(requestURL);
		}

	}

	private void writeURLToFile(String reqURL){
		try {
			FileUtil.createFile(fileoutpath, tcid + "_Url.txt", reqURL);
		} catch (IOException exception) {
			logger.info(exception.getMessage(),exception);
		}
	}

	/**
	 * Get the output filepath with timstamp as the last folder
	 * in the folder structure. 
	 * 
	 * @return
	 */
	private String getFileOutPath(String timestamp) {
		return Constants.TEST_OUTPUT_PATH + "\\" + timestamp + "\\";
	}

	/**
	 * Set the Test_Result column in excel sheet as Skipped.
	 * 
	 */
	protected void testSkipped() {
		reader.setCellData(Constants.SHEET_NAME, Constants.COLUMN_TEST_RESULT, currentTestcase, Constants.TEST_SKIP);
	}

	protected void testSkipped(String SkipCause) {
		logger.info("Test Skipped : " + SkipCause);
		try{
			reader.setCellData(Constants.SHEET_NAME, Constants.COLUMN_TEST_RESULT, currentTestcase, Constants.TEST_SKIP);
			reader.setCellData(Constants.SHEET_NAME, Constants.COLUMN_FAILURE_CAUSE, currentTestcase, SkipCause);
		}catch(Exception e){
			logger.info(e.getMessage(),e);
		}
		throw new SkipException("Test Skipped : "+SkipCause);


	}



	/**
	 * Set the Test_Result column in excel sheet as Passed.
	 * 
	 */
	protected void testPassed() {
		reader.setCellData(Constants.SHEET_NAME, Constants.COLUMN_TEST_RESULT, currentTestcase, Constants.TEST_PASSED);
	}


	/**
	 * Set the Test_Result column in excel sheet as Failed. And also sets the
	 * failure cause in Failure_Cause column.
	 * 
	 */
	protected void testFailed(String failureCause) {
		logger.info("Test Failed : " + failureCause);
		reader.setCellData(Constants.SHEET_NAME, Constants.COLUMN_TEST_RESULT, currentTestcase, Constants.TEST_FAILED);
		reader.setCellData(Constants.SHEET_NAME, Constants.COLUMN_FAILURE_CAUSE, currentTestcase, failureCause);
		testFailed = true;
		Assert.fail("Test Failed : " + failureCause);
	}


	@AfterSuite
	public void tearDown() {
		try {
			FileUtil.copyFile(Constants.TESTDATA_PATH + configProp.getProperty(Constants.KEY_TESTDATA), fileoutpath + "\\" + configProp.getProperty(Constants.KEY_TESTREPORT));
			clearResults();
		} catch (IOException e) {
			logger.info(e.getMessage(), e);
		}
	}


	private void validateRequestValues() {
		HashMap <String,String> requestValuesMap = ParamsUtil.generateRequestParamsMap(requestValues);
		String resultString="";
                try {
                    

		if(!isDbQueried) {
			List<String> queriesList;
			try {
				queriesList = db2Manager.getQueries(dbQueries);
				resultsList = db2Manager.executeQueries(queriesList);
			} catch (IOException e) {
				e.printStackTrace();
				logger.info(e.getMessage(),e);
			}
		}

		for (Map.Entry<String, String> entry : requestValuesMap.entrySet()) {
			String key = entry.getKey();
			String expectedValue = entry.getValue();
			String actualValue = db2Manager.getRowWiseValue(resultsList, key);


			if(!expectedValue.equals(actualValue)){

				resultString=resultString+"Expected Value [From Excel] : " + key + " = " + expectedValue+"\n";
				resultString=resultString+"Actual Value [From DB] : " + key + " = " + actualValue+"\n\n";

				// TODO Debugging purpose - remove the code
				System.out.println("Expected Value : " + key + " = " + expectedValue);
				System.out.println("Actual Value   : " + key + " = " + actualValue);

			}
		}
		// Report all the mismatching values to the Excel report
		if(!resultString.isEmpty()){
			testFailed(resultString);
		}
                }catch (Exception e) {
			logger.info(e.getMessage(),e);
			testFailed(e.getMessage());

		}
	}


}


===============================================================



payments_errorcode.properties
=============================

#Error Codes and Description

#  Payment Error Codes
invalidStartDate=1007;Start Date cannot be greater than End Date
DPFailure=1001;DATA POWER CALL FAILED
invalidSeNumber=1004;Invalid SE Number
invalidStartDateFormat=1005;Invalid Start Date Format 
invalidEndDateFormat=1006;Invalid End Date Format
userNotAuthorized=1000;User Not Authorized
invalidSettlementDtFormat =1024;Invalid Settlement Date Format.
invalidSettlementNumber=1028;Invalid Settlement Number




====================================================================


payments_request.properties
===========================



# Payment Summary
PaymentSumHappyPath={ "startDt": "_startDt","endDt": "_endDt","seNbrList": ["_seNbrList1"],"clientId": "_clientId","messageId": "_messageId" }

# Payment Settlement
PaymentSettleHappyPath={ "lowerBound": "_lowerBound","upperBound": "_upperBound","sortColumn": "_sortColumn","startDt": "_startDt","endDt": "_endDt","seNbrList": ["_seNbrList1"] }
PendingSettlementPath={ "startDt": "_startDt","endDt": "_endDt","seNbrList": ["_seNbrList1"],"sortColumn": "_sortColumn","lowerBound": "_lowerBound","upperBound": "_upperBound" }

#Payments Settle Currency
SettleCurrencyReq={"guid": "_guid","seNbrList": ["_seNbr1"],"busCtrCd": "_busCtrCd"}
##More than 20 SE_No's
SettleCurrencyReq1={"guid":"","seNbrList":["_se1","_se2","_se3","_se4","_se5","_se6","_se7","_se8","_se9","_se_a","_se_b","_se_c","_se_d","_se_e","_se_f","_se_g","_se_h","_se_i","_se_j","_se_k","_se_l"],"busCtrCd":""}
##Empty seNbrlist
SettleCurrencyReq2={"guid":"","seNbrList":[],"busCtrCd":"_busCtrCd"}
##20 SE_Nbr's
SettleCurrencyReq3={"guid":"","seNbrList":["_se1","_se2","_se3","_se4","_se5","_se6","_se7","_se8","_se9","_se_a","_se_b","_se_c","_se_d","_se_e","_se_f","_se_g","_se_h","_se_i","_se_j","_se_k"],"busCtrCd":""}
##Only GUID
SettleCurrencyReq4={"guid": "_guid","seNbrList": [],"busCtrCd": "_busCtrCd"}




======================================================================



payments_sqlqueries.propertioes
===============================


GUID=select GLBL_USER_ID from OD1.TC391_USER where FIRST_NM = '_first_nm' and LST_NM = '_lst_nm'

# Queries for Payment Summary
SQLQueryPSHP=Select sum(SOC_NET_AM) as "q1.settlementAmt" ,sum(SUBM_GR_AM) as "a1.submissionAmt",(count(A.GMDL_Settle_ID)) as "q1.submissionCt",(Sum(SOC_GR_AM)-SUM(SOC_NET_AM)) as "q1.discountAmt" from OD1.TE130_MER_SETTLE_DTL A, od1.TE133_MER_SETTLE_SOC B WHERE A.SETTLE_SE_NO = '_SENO' and Settle_dt between '_startDt' and '_endDt' AND A.GMDL_Settle_ID = B.GMDL_SETTLE_ID and B.TRANS_TYPE_CD ='0'
ParamPSHP1=_SENO:1041161720,_startDt:2013-11-01,_endDt:2013-11-30

SQLQueryPSXSDeqED=Select sum(SOC_NET_AM) as "q1.settlementAmt" ,sum(SUBM_GR_AM) as "a1.submissionAmt",(count(A.GMDL_Settle_ID)) as "q1.submissionCt",(Sum(SOC_GR_AM)-SUM(SOC_NET_AM)) as "q1.discountAmt" from OD1.TE130_MER_SETTLE_DTL A, od1.TE133_MER_SETTLE_SOC B WHERE A.SETTLE_SE_NO = '_SENO' and Settle_dt between '_startDt' and '_endDt' AND A.GMDL_Settle_ID = B.GMDL_SETTLE_ID and B.TRANS_TYPE_CD ='0'
ParamPSXSDeqED1=_SENO:1041161720,_startDt:2013-12-01,_endDt:2013-11-30





===================================================================

config.properties
=================

#Test data ExcelSheet filename in the folder com/amex/testdata/

#testdata.filename   = API_Details_Disputes.xlsx
#testreport.filename = API_Report_Disputes.xlsx

testdata.filename   = API_Details_Payments_1.xlsx
testreport.filename = API_Report_Payments_1.xlsx

#Base url for the Api

#api.base.url    = https://dwww421.app.aexp.com/merchant/services/disputes
#isLoginRequired = true
#loginURL        = https://dwww421.app.aexp.com/merchant/services/disputes/sso

api.base.url   = https://dwww421.app.aexp.com/merchant/services/payments
#isLoginRequired  = false
#loginURL       = https://dwww421.app.aexp.com/merchant/services/payments/sso


#Setup properties file

#error.prop.filename       = disputes_ErrorCodes.properties
#request.prop.filename     = disputes_Request.properties
#sql.queries.prop.filename = disputes_sql_queries.properties

error.prop.filename       = payments_ErrorCodes.properties
request.prop.filename     = payments_Request.properties
sql.queries.prop.filename = payments_sql_queries.properties
