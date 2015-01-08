
package com.amex.main;

import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.amex.dataprovider.RowDataProvider;
import com.amex.dataprovider.RowDataWrapper;
import com.amex.testfactory.APITestFactory;
import com.amex.utils.Constants;
import com.amex.utils.FileUtil;
import com.jayway.restassured.response.Response;

public class DriverScript extends APITestFactory {

	private static Logger logger = LoggerFactory.getLogger(DriverScript.class);

	/**
	 * This method is the main method that starts the execution.
	 * 
	 * @throws IOException
	 */
	@Test(dataProviderClass = RowDataProvider.class, dataProvider = "getRowData")
	public void test(RowDataWrapper rowDataWrapper) throws IOException {
		testFailed=false;
		logger.info("\n\nExecuting test case "+rowDataWrapper+"\n");
		currentTestcase=rowDataWrapper.getCurrentTestcase();
		HashMap<String, String> rowData = rowDataWrapper.getRowData();
		String runMode = rowData.get(Constants.COLUMN_RUN_MODE).trim();
		tcid = rowData.get(Constants.COLUMN_TCID).trim();
		
		// Execute when Run Mode is Yes
		if (runMode.equalsIgnoreCase("YES")) {
			
			// Initialize all the values from test data sheet
			initialize(rowData);
			
			//TODO define runBootstrapSQLQueries()
			
			// Replaces the URL with path parameters
			replaceURLParameters(urlParameters);				
			
			Response response = getResponse();

			if (response != null) {
				FileUtil.createFile(fileoutpath, tcid + "_Response.txt", response.asString());
				validateResponse(response);

				// Updating the pass result only if there is no failure
				if (!testFailed) {
					testPassed();
				}
			}
		} else if (runMode.equalsIgnoreCase("NO")) {	    
			testSkipped(tcid+" : Test Skipped as Run Mode was NO");

		}else{
			testFailed("Unable to read Run Mode");
		}
	}	
}


===========================================================




package com.amex.base;

import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;

import com.amex.utils.Constants;
import com.amex.utils.DateTimeUtil;
import com.amex.utils.ExcelReader;
import com.amex.utils.FileUtil;
import com.amex.utils.PropertiesReader;

public class TestBase {
    protected static ExcelReader reader;
    protected Properties requestProp;
    protected Properties errorCodesProp;
    protected Properties configProp;
    protected String currentTimeStamp;
    
    private String requestPropFileName;
    private String errorPropFileName;
    
    private static Logger logger = LoggerFactory.getLogger(TestBase.class);

    @BeforeSuite
    public void setUp() throws IOException {
	logger.info("Loading all the required files");

	//Loading config.properties
	configProp = PropertiesReader.loadPropertyFile(Constants.CONFIG_PROP_PATH);
	
	requestPropFileName = configProp.getProperty(Constants.KEY_REQ_PROP_FILE);
	errorPropFileName   = configProp.getProperty(Constants.KEY_ERR_PROP_FILE);
	
	// Loading Request property file
	requestProp = PropertiesReader.loadPropertyFile(Constants.REQUEST_PROP_PATH + requestPropFileName);

	// Loading Error Codes property file
	errorCodesProp = PropertiesReader.loadPropertyFile(Constants.ERROR_CODES_PROP_PATH + errorPropFileName);

	// Loading the Test data excel sheet
	reader = FileUtil.getExcelReader(Constants.TESTDATA_PATH + configProp.getProperty(Constants.KEY_TESTDATA));
	
	currentTimeStamp = DateTimeUtil.getCurrentTimeStamp();
    }

}


=========================================================================



package com.amex.dataprovider;

import java.util.HashMap;

import org.testng.annotations.DataProvider;

import com.amex.base.TestBase;
import com.amex.utils.Constants;

public class RowDataProvider extends TestBase{
    
    
    @DataProvider
    public static Object[][] getRowData() {
	Object[][] object =  new Object[reader.getRowCount(Constants.SHEET_NAME)-1][1];
	
	// Iterating through every row in the sheet
	for (int currentTestcase = 2; currentTestcase <= reader.getRowCount(Constants.SHEET_NAME); currentTestcase++) {	    
	    
		HashMap<String, String> rowData = reader.getRowData(Constants.SHEET_NAME, currentTestcase);
	    // allocating Row Data wrapper object to Object array
		object[currentTestcase-2][0]=new RowDataWrapper(rowData,currentTestcase);
	}
	return  object;
    }
}


==========================================================================


package com.amex.dataprovider;

import java.util.HashMap;

import com.amex.utils.Constants;

public class RowDataWrapper {
    private HashMap<String, String> rowData;
    private int currentTestcase;

    public RowDataWrapper(HashMap<String, String> rowData, int currentTestcase) {
	this.rowData = rowData;
	this.currentTestcase = currentTestcase;
    }

    public HashMap<String, String> getRowData() {
	return rowData;
    }
    
    public int getCurrentTestcase() {
	return currentTestcase;
    }

    public String toString() {
	return rowData.get(Constants.COLUMN_TCID).trim() + ": " + rowData.get("Api_Name").trim();
    }
}



=============================================================================



package com.amex.utils;

public class Constants {

//public static final String URL 				   = "http://10.192.37.167:8080/EmployeeData/disputes";
//public static final String URL 			   = "https://dwww421.app.aexp.com/merchant/services/dsiputes";
public static final String REQUEST_PROP_PATH       = "src\\com\\amex\\config\\request\\";
public static final String ERROR_CODES_PROP_PATH   = "src\\com\\amex\\config\\errorhandles\\";
public static final String SQL_QUERIES_PROP_PATH   = "src\\com\\amex\\config\\request\\";
public static final String TEMP_PROP_PATH          = "src\\com\\amex\\config\\temp.properties";
public static final String CONFIG_PROP_PATH 	   = "src\\com\\amex\\config\\config.properties";
public static final String SHEET_NAME              = "Sheet1";
public static final String TESTDATA_PATH           = "src\\com\\amex\\testdata\\";
//public static final String EXCEL_TEST_REPORT_PATH  = "\\API_Report.xlsx";
public static final String CONTENT_TYPE_JSON       = "application/json";
public static final String TEST_OUTPUT_PATH	   = "Test_Output_files";

public static final String COLUMN_TCID	           = "TCID";
public static final String COLUMN_API              = "API";
public static final String COLUMN_HEADER_KEY       = "Header_Key";
public static final String COLUMN_HEADER_VALUE     = "Header_Value";
public static final String COLUMN_URL_PARAMETERS   = "Url_Parameters";
public static final String COLUMN_REQUEST_NAME     = "Request_Name";
public static final String COLUMN_REQUEST_PARAM    = "Request_Parameters";
public static final String COLUMN_RESPONSE_CODE    = "Expected_Response_Code";
public static final String COLUMN_REQUEST_METHOD   = "Request_Method";
public static final String COLUMN_ERROR_NAME       = "Error_Name";
public static final String COLUMN_RESPONSE_KEY     = "Response_Keys";
public static final String COLUMN_RUN_MODE         = "Run_Mode";
public static final String COLUMN_TEST_RESULT      = "Test_Result";
public static final String COLUMN_FAILURE_CAUSE    = "Failure_Cause";
public static final String COLUMN_DB_QUERIES       = "DB_Queries";
public static final String COLUMN_DB_RESULT_KEYS   = "DB_Result_Keys";
public static final String COLUMN_REQUEST_VALUES   = "Request_Values";
public static final String COLUMN_RESPONSE_STORE_RETRIEVE = "Response_Store_Retrieve";
public static final String COLUMN_FILE_SRC_PATH    = "File_Src_Path";

public static final String TEST_SKIP               = "Skipped";
public static final String TEST_PASSED             = "Passed";
public static final String TEST_FAILED             = "Failed";


public static final String JDBC_DRVER_DB2          = "COM.ibm.db2os390.sqlj.jdbc.DB2SQLJDriver";
public static final String JDBC_CONNECTION_URL	   = "jdbc:db2://adc1db2d.ipc.us.aexp.com:7320/ADC1DB2D";


public static final String DB_USER                 = "IG8671A";
public static final String DB_PASS                 = "aug3@aug";

//Config properties keys
public static final String KEY_TESTDATA            = "testdata.filename";
public static final String KEY_TESTREPORT	   = "testreport.filename";
public static final String KEY_URL                 = "api.base.url";
public static final String KEY_REQ_PROP_FILE 	   = "request.prop.filename";
public static final String KEY_SQL_PROP_FILE	   = "sql.queries.prop.filename";
public static final String KEY_ERR_PROP_FILE	   = "error.prop.filename";
public static final String KEY_LOGIN_URL	   = "loginURL";
public static final String KEY_LOGIN_REQUIRED      = "isLoginRequired";






}



====================================================================



package com.amex.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtil {

    /**
     * Get the current date and time.
     * 
     * @return returns the current date and time in the format: "dd-MM-yyyy_HH_mm_ss"
     */
    public static String getCurrentTimeStamp() {
	SimpleDateFormat sdfDate = new SimpleDateFormat("dd-MM-yyyy_HH_mm_ss");
	Date now = new Date();
	return sdfDate.format(now);
	
    }
}



==========================================================


package com.amex.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author spalu1
 *
 */
/**
 * @author spalu1
 *
 */
public class DB2Manager {

	private static DbReader dbReader = null;
	protected Properties requestProp;
	private Properties configProp;
	private String sqlPropFileName;
	private static Logger logger = LoggerFactory.getLogger(DB2Manager.class);

	public DB2Manager() throws Exception {
		dbReader = DbReader.getInstance(Constants.JDBC_DRVER_DB2,
				Constants.JDBC_CONNECTION_URL, Constants.DB_USER,
				Constants.DB_PASS);
		
		configProp      = PropertiesReader.loadPropertyFile(Constants.CONFIG_PROP_PATH);
		sqlPropFileName = configProp.getProperty(Constants.KEY_SQL_PROP_FILE);
	}

	/**
	 * Executes all the SQL queries passed to it 
	 * @param queries
	 * @return List of HashMap containing all the query results where Keys are column names
	 * @throws SQLException 
	 */
	public List<Map<String, String>> executeQueries(List<String> queries) throws SQLException {
		Iterator<String> queriesIt = queries.iterator();
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();

		// Iterating through all the passed queries
		while (queriesIt.hasNext()) {
			String query = queriesIt.next();

			// Executing query
			List<Map<String, String>> results = dbReader.executeQuery(query);
			    if (results != null) {
				resultList.addAll(results);
			}
		}
		logger.info(resultList.toString());
		return resultList;
	}


	/**
	 * Returns value from a List of HashMap if Key is provided
	 * @param resultList
	 * @param key
	 * @return
	 */
	public String getItemValue(List<Map<String, String>> resultList, String key) {
		try {
			logger.debug("In getItemValue, key: " + key );
			Iterator<Map<String, String>> resultsIt = resultList.iterator();
			while (resultsIt.hasNext()) {
				Map<String, String> resultMap = resultsIt.next();
				for (Map.Entry<String, String> entry : resultMap.entrySet()) {
					if (entry.getKey().equals(key)) {
						logger.debug("In getItemValue, value: " + entry.getValue());
						return entry.getValue();
					}
				}
			}
		} catch(Exception e){
			logger.info(e.getMessage(),e);
		}
		return null;
	}

	
	/**
	 *  Get the Complete query from the property files 
	 * @param dbQueries
	 * @return
	 * @throws IOException
	 */
	public List<String> getQueries(String dbQueries) throws Exception,IOException {
		
		requestProp = new Properties();
		FileInputStream fis = new FileInputStream(
				Constants.SQL_QUERIES_PROP_PATH + sqlPropFileName);
		requestProp.load(fis);
		List<String> allQueries = new ArrayList<String>();
		String[] queryKeyValues = dbQueries.split(",");
		for (String queryKeyValue : queryKeyValues) {

			queryKeyValue = queryKeyValue.trim();
			if (!queryKeyValue.isEmpty()) {
				String[] keyValue = queryKeyValue.split(":");
				if(keyValue.length != 2) {
				    throw new Exception("Query or Parameter key missing in the excel sheet.");
				}
				String query1 = requestProp.getProperty(keyValue[0]);
				String param1 = requestProp.getProperty(keyValue[1]);
				if (param1 != null) {
					// Combines query and parameter
					query1 = ParamsUtil.replaceParams(query1, param1);
				}
				allQueries.add(query1);
				logger.info(query1);
			}

		}
		return allQueries;
	}

	/**
	 * 
	 * @param resultList
	 *            List of results hashmap
	 * @param keyrow
	 *            String in the form
	 *            "ColumnName[RowNum], where rownum = 0,1,2,3...."
	 * @return
	 */
	public String getRowWiseValue(List<Map<String, String>> resultList,
			String keyrow) {

		try {
			logger.debug("In getRowWiseValue, keyrow: " + keyrow );
			//Get the column name
			String key = keyrow.split("\\[")[0];
			String value = null;
			Integer rowNum = 0;
			if (keyrow.indexOf(".") != -1) {
				resultList =  getItemValueByQuery(resultList,key);
			}
			//Checks whether if '[' is present
			if (keyrow.indexOf('[') != -1) {
				//gets the rownumber from inside []
				rowNum = Integer.valueOf(keyrow.replaceAll(".*\\[|\\].*", ""));
			} else {
				//if there is no row number mentioned
				return getItemValue(resultList, key);
			}
			if (0 <= rowNum && resultList.size() > rowNum) {
				Map<String, String> row = resultList.get(rowNum);
				value = row.get(key);
			}
			return value;
		} catch (Exception e) {
			logger.info(e.getMessage(), e);
		}
		return null;
	}
    private List<Map<String, String>> getItemValueByQuery(List<Map<String, String>> resultList, String keyrow) {

	//Get query identifier
	String queryId = keyrow.substring(0, keyrow.indexOf("."));
	Iterator<Map <String,String>> it = resultList.iterator();
	List<Map<String, String>> newResultList = new ArrayList<Map<String, String>>();
	while(it.hasNext()) {
	    Map <String,String> resultMap =  it.next();
	    HashMap<String, String> newResultMap = new HashMap<String, String>();
	    for (Map.Entry<String, String> entry : resultMap.entrySet()) {
		if (entry.getKey().startsWith(queryId)) {
		    newResultMap.put(entry.getKey(), entry.getValue());
		}
	    }
	    if(!newResultMap.isEmpty()){
		newResultList.add(newResultMap);
	    }
	}
	return newResultList;
    }
}


======================================================================



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
 */
public class DbReader {

	private Connection connection = null;
	private ResultSet resultSet = null;
	private PreparedStatement preparedStatement = null;
	private ResultSetMetaData resultSetMetaData = null;

	private static Logger logger = LoggerFactory.getLogger(DbReader.class);

	/**
	 * This method is used to establish the database connection
	 * 
	 * @param driver
	 * @param url
	 * @param userName
	 * @param password
	 * @throws Exception 
	 * 
	 */
	public DbReader(String driver, String url, String userName,
			String password) throws Exception {
		try {
			logger.info(" Trying to connect database");
			Class.forName(driver);
			connection = DriverManager.getConnection(url, userName, password);
			connection.setAutoCommit(false);
		} catch (Exception e) {
			logger.info(e.getMessage(),e);
			throw e;
		}
	}
	
	
	/**
	 * Execute single query 
	 * @param query
	 * @return
	 * @throws SQLException 
	 */
	public List<Map<String,String>> executeQuery(String query) throws SQLException{
		try {
			preparedStatement = connection.prepareStatement(query);
			
			// If it is a DML query, execute teh below code
			if(!query.split(" ")[0].equalsIgnoreCase("SELECT")) {
				int status = preparedStatement.executeUpdate();

				// Checking how many rows are updated.
				String _status=(status!=0)?""+status+" Row(s) affected":" No rows affected";
				logger.info("Execute Update, staus : "+ _status);
				connection.commit();
				return null;
			}

			resultSet = preparedStatement.executeQuery();
			resultSetMetaData = resultSet.getMetaData();

		} catch (SQLException e) {   
			logger.info(e.getMessage(), e);
			throw e;
		}
		return read();
	}


	private static volatile DbReader instance = null;

	/**
	 * This method creates and returns the DbReader instance
	 * 
	 * @param driver
	 * @param url
	 * @param userName
	 * @param password
	 * @return DbReader
	 * @throws Exception 
	 */
	public static DbReader getInstance(String driver, String url,
			String userName, String password) throws Exception {
		logger.info(" Trying to create instance for DB");
		if (instance == null) {
			synchronized (DbReader.class) {
				if (instance == null) {
					instance = new DbReader(driver, url, userName, password);
				}
			}
		}
		return instance;
	}

	/**
	 * This method reads the data from database
	 * 
	 * @return List of map objects
	 */
	private List<Map<String, String>> read() {
		logger.debug(" inside read() method");
		List<Map<String, String>> maplist = null;
		try {
			int colCount = resultSetMetaData.getColumnCount();

			maplist = new ArrayList<Map<String, String>>();
			while (resultSet.next()) {
				Map<String, String> map = new HashMap<String, String>();
				for (int i = 1; i <= colCount; i++) {
					map.put(resultSetMetaData.getColumnName(i).trim(),
							resultSet.getString(i).trim());
				}
				maplist.add(map);
			}
		} catch (Exception e) {
			logger.info(e.getMessage(),e);
		} finally {
			try {
				resultSet.close();

			} catch (SQLException e) {
				if (e.getMessage() != null && e.getMessage().isEmpty()) {
					logger.error(e.getMessage());
				} else {
					logger.error(" Problem occured while getting data from db");
				}
			}

		}
		return maplist;
	}
}
