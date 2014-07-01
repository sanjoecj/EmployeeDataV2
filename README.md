
=======================================================================================================

package com.amex.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DB2Manager {
    
    private DbReader dbReader = null;
    
    public List<Map<String,String>> executeQueries(List<String> queries) {
	Iterator<String> queriesIt = queries.iterator();
	List<Map<String,String>> resultList = new ArrayList<Map<String,String>>();
	while(queriesIt.hasNext()) {
	    String query = queriesIt.next();
	    List<Map<String,String>> results = dbReader.executeQuery(query);
	    Iterator<Map<String, String>> resultIt = results.iterator();
	    while(resultIt.hasNext()) {
		resultList.add(resultIt.next());
	    }
	}
	return resultList;
    }
    
    //private List<String> 
}


=========================================================================================================


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
     * This method is used get the database connection
     * 
     * @param driver
     * @param url
     * @param userName
     * @param password
     * @param query
     * 
     */
    public DbReader(String driver, String url, String userName,
	    String password) {
	try {
	    logger.info(" Trying to connect database");
	    Class.forName(driver);
	    connection = DriverManager.getConnection(url, userName, password);
	    connection.setAutoCommit(false);
	} catch (Exception e) {
	    logger.error(e.getMessage());
	}
    }
    
    public List<Map<String,String>> executeQuery(String query) {
	try {
	    preparedStatement = connection.prepareStatement(query);
	
	    resultSet = preparedStatement.executeQuery();
	    resultSetMetaData = resultSet.getMetaData();
	    
	    
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return read();
    }
    

    private static volatile DbReader instance = null;

    /**
     * This method create the instance of DbReader
     * 
     * @param driver
     * @param url
     * @param userName
     * @param password
     * @param tableName
     * @return DbReader
     */
    public static DbReader getInstance(String driver, String url,
	    String userName, String password) {
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
     * This method read the data from database
     * 
     * @return List of map objects
     */
    private List<Map<String, String>> read() {
	logger.info(" inside read() method");
	List<Map<String, String>> maplist = null;
	try {

	    int colCount = resultSetMetaData.getColumnCount();
	    Map<String, String> map = new HashMap<String, String>();
	    maplist = new ArrayList<Map<String, String>>();
	    while (resultSet.next()) {
		for (int i = 1; i <= colCount; i++) {
		    map.put(resultSetMetaData.getColumnName(i).trim(),
			    resultSet.getString(i).trim());
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



============================================================================================================
