package com.amex.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExcelReader {
    

	private FileInputStream fis = null;
	public  FileOutputStream fileOut =null;
	private Workbook workbook = null;
	private Sheet sheet = null;
	private Row row = null;
	private Cell cell = null;
	public static final Logger logger = LoggerFactory.getLogger(ExcelReader.class);
	private static volatile ExcelReader instance = null;
	private static volatile Map<String,ExcelReader> excelReaderMap = new HashMap<String, ExcelReader>();
	private String path;

	public ExcelReader(String path) {   
		// LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory(); 
		try { 
		this.path = path;
		fis = new FileInputStream(this.path); 
		this.workbook = WorkbookFactory.create(fis); 
		fis.close(); 
		} catch (Exception e) { 
				logger.error(e.getMessage()); 
				} 
			 }
		//}

		// creating singleton Object for each excelFile
		public static ExcelReader getInstance(String excelFilePath) {
			if (instance == null || !excelReaderMap.containsKey(excelFilePath)) {
				synchronized (ExcelReader.class) {
					if (instance == null || !excelReaderMap.containsKey(excelFilePath)) {
						instance = new ExcelReader(excelFilePath);
						excelReaderMap.put(excelFilePath, instance);
					}
				}
			}       
			return excelReaderMap.get(excelFilePath);
		}


		// returns the row count in a sheet
		public int getRowCount(String sheetName) {
			//logger.info(" inside getRowCount() method");
			int index = workbook.getSheetIndex(sheetName);
			if (index == -1)
				return 0;
			else {
				sheet = workbook.getSheetAt(index);
				int number = sheet.getLastRowNum() + 1;
				return number;
			}

		}

		// returns the columnData in a sheet with colNum
		public List<String> getColumnData(String sheetName, int colNum) {
			//logger.info(" inside getColumnData() method");

			List<String> list = null;
			try {

				list = new ArrayList<String>();
				int index = workbook.getSheetIndex(sheetName);
				if (index == -1) return list;

				sheet = workbook.getSheetAt(index);
				list = this.getEntireColumnCellValues(sheet, colNum);
				return list;

			} catch (Exception e) {
				if (e.getMessage() != null && e.getMessage().isEmpty()) {
					logger.error(e.getMessage());
				} else {
					//logger.error(" column " + colNum + " does not exist in file");
				}
				return list;
			}

		}

		// returns the columnData in a sheet with colName
		public List<String> getColumnData(String sheetName, String colName) {
			//logger.info(" inside getColumnData() method");
			List<String> list = null;
			try {

				list = new ArrayList<String>();
				int index = workbook.getSheetIndex(sheetName);
				if (index == -1) return list;

				sheet = workbook.getSheetAt(index);
				row = sheet.getRow(0);
				if (row == null) return list;

				int colNum = 0;
				for (int i = 0; i < row.getLastCellNum(); i++) {
					if (row.getCell(i).getStringCellValue().trim()
							.equals(colName.trim())){
						colNum = i;
					}                   
				}           
				if (colNum == -1) return list;  

				list = this.getEntireColumnCellValues(sheet,colNum);
				return list;

			} catch (Exception e) {
				if(e.getMessage() != null && e.getMessage().isEmpty()){
					//logger.error(e.getMessage());
				} else{
					//logger.error(" column " + colName + " does not exist in file");
				}   
				return list;
			}
		}


		// returns the data from a cell
		public String getCellData(String sheetName, String colName, int rowNum) {
			//logger.info(" inside getCellData() method");
			try {
				if (rowNum <= 0) return "";

				int index = workbook.getSheetIndex(sheetName);
				int col_Num = -1;
				if (index == -1) return "";

				sheet = workbook.getSheetAt(index);
				row = sheet.getRow(0);
				for (int i = 0; i < row.getLastCellNum(); i++) {
					if (row.getCell(i).getStringCellValue().trim()
							.equals(colName.trim()))
						col_Num = i;
				}
				if (col_Num == -1) return "";

				sheet = workbook.getSheetAt(index);
				row = sheet.getRow(rowNum - 1);
				if (row == null) return "";

				cell = row.getCell(col_Num);
				return this.getCellValue(cell);

			} catch (Exception e) {
				if(e.getMessage() != null && e.getMessage().isEmpty()){
					//logger.error(e.getMessage());
				} else{
					//logger.error("row " + rowNum + " or column " + colName + " does not exist in file");
				}
				return "row " + rowNum + " or column " + colName + " does not exist in file";
			}
		}

		// returns the data from a cell
		public String getCellData(String sheetName, int colNum, int rowNum) {
			//logger.info(" inside getCellData() method");
			try {
				if (rowNum <= 0) return "";

				int index = workbook.getSheetIndex(sheetName);

				if (index == -1) return "";

				sheet = workbook.getSheetAt(index);
				row = sheet.getRow(rowNum - 1);
				if (row == null) return "";

				cell = row.getCell(colNum);

				return this.getCellValue(cell);

			} catch (Exception e) {
				if(e.getMessage() != null && e.getMessage().isEmpty()){
					//logger.error(e.getMessage());
				} else{
					//logger.error("row " + rowNum + " or column " + colNum + " does not exist  in file");
				}
				return "row " + rowNum + " or column " + colNum + " does not exist  in file";
			}
		}

		// find whether sheets exists
		public boolean isSheetExist(String sheetName) {

			//logger.info(" inside isSheetExist() method");
			int index = workbook.getSheetIndex(sheetName);
			if (index == -1) {
				index = workbook.getSheetIndex(sheetName.toUpperCase());
				if (index == -1) return false;
				else
					return true;
			} else
				return true;
		}

		// returns number of columns in a sheet
		public int getColumnCount(String sheetName) {

			//logger.info(" inside getColumnCount() method");
			if (!isSheetExist(sheetName)) return -1;

			sheet = workbook.getSheet(sheetName);
			row = sheet.getRow(0);

			if (row == null) return -1;

			return row.getLastCellNum();

		}

		//returns row number
		public int getCellRowNum(String sheetName, String colName, String cellValue) {
			//logger.info(" inside getCellRowNum() method");

			for (int i = 2; i <= getRowCount(sheetName); i++) {
				if (getCellData(sheetName, colName, i).equalsIgnoreCase(cellValue)) {
					return i;
				}
			}
			return -1;

		}

		// returns Row data in a sheet
		public HashMap<String, String> getRowData(String sheetName, int rowNum) {

			//logger.info(" inside getRowData() method");
			HashMap<String, String> hashMap = null;
			try {

				hashMap = new HashMap<String, String>();            
				if (rowNum <= 0) return hashMap;

				int index = workbook.getSheetIndex(sheetName);          
				if (index == -1) return hashMap;

				sheet = workbook.getSheetAt(index);
				row = sheet.getRow(rowNum - 1);
				Row headerRow = sheet.getRow(0);

				if (headerRow == null || row == null) return hashMap;

				for (int i = 0; i < headerRow.getLastCellNum(); i++) {
					hashMap.put(this.getCellValue(headerRow.getCell(i)), this.getCellValue(row.getCell(i)));                
				}
				return hashMap;

			} catch (Exception e) {
				if(e.getMessage() != null && e.getMessage().isEmpty()){
					//logger.error(e.getMessage());
				} else{
					//logger.error("row " + rowNum + " does not exist  in file");
				}
				return hashMap;
			}
		}

		// get the cell data as string.
		private String getCellValue(Cell cell){

			if (cell == null)
				return "";
			if (cell.getCellType() == Cell.CELL_TYPE_STRING)
				return cell.getStringCellValue();
			else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC
					|| cell.getCellType() == Cell.CELL_TYPE_FORMULA) {

				String cellText = String.valueOf(cell.getNumericCellValue());
				if (DateUtil.isCellDateFormatted(cell)) {
					// format in form of M/D/YY
					double d = cell.getNumericCellValue();

					Calendar cal = Calendar.getInstance();
					cal.setTime(DateUtil.getJavaDate(d));
					cellText = (String.valueOf(cal.get(Calendar.YEAR)))
							.substring(2);
					cellText = cal.get(Calendar.DAY_OF_MONTH) + "/"
							+ cal.get(Calendar.MONTH) + 1 + "/" + cellText;

				}
				return cellText;
			} else if (cell.getCellType() == Cell.CELL_TYPE_BLANK)
				return "";
			else
				return String.valueOf(cell.getBooleanCellValue());

		}

		// get entire column cell data as a list    
		private List<String> getEntireColumnCellValues(Sheet sheet, int colNum){

			List<String> list = new ArrayList<String>();
			for (Row row : sheet) {
				if (row.getRowNum() != 0) {
					cell = row.getCell(colNum);
					if (cell != null) {
						if (cell.getCellType() == Cell.CELL_TYPE_STRING)
							list.add(cell.getStringCellValue());
						else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC
								|| cell.getCellType() == Cell.CELL_TYPE_FORMULA) {

							String cellText = String.valueOf(cell
									.getNumericCellValue());
							if (DateUtil.isCellDateFormatted(cell)) {
								// format in form of M/D/YY
								double d = cell.getNumericCellValue();

								Calendar cal = Calendar.getInstance();
								cal.setTime(DateUtil.getJavaDate(d));
								cellText = (String.valueOf(cal
										.get(Calendar.YEAR))).substring(2);
								cellText = cal.get(Calendar.MONTH) + 1 + "/"
										+ cal.get(Calendar.DAY_OF_MONTH) + "/"
										+ cellText;
							}

							list.add(cellText);
						} else if (cell.getCellType() == Cell.CELL_TYPE_BLANK)
							list.add("");
						else
							list.add(String.valueOf(cell.getBooleanCellValue()));
					}
				}
			}       
			return list;        
		}
	
		
		public boolean setCellData(String sheetName,String colName,int rowNum, String data){
			try{
//			fis = new FileInputStream(path); 
//			workbook = new Workbook(fis);
			if(rowNum<=0)
				return false;
			
			int index = workbook.getSheetIndex(sheetName);
			int colNum=-1;
			if(index==-1)
				return false;
			
			
			sheet = workbook.getSheetAt(index);
			

			row=sheet.getRow(0);
			for(int i=0;i<row.getLastCellNum();i++){
				//System.out.println(row.getCell(i).getStringCellValue().trim());
				if(row.getCell(i).getStringCellValue().trim().equals(colName))
					colNum=i;
			}
			if(colNum==-1)
				return false;

			sheet.autoSizeColumn(colNum); 
			row = sheet.getRow(rowNum-1);
			if (row == null)
				row = sheet.createRow(rowNum-1);
			
			cell = row.getCell(colNum);	
			if (cell == null)
		        cell = row.createCell(colNum);

		    // cell style
		    //CellStyle cs = workbook.createCellStyle();
		    //cs.setWrapText(true);
		    //cell.setCellStyle(cs);
		    cell.setCellValue(data);

		    fileOut = new FileOutputStream(path);

			workbook.write(fileOut);

		    fileOut.close();	
		    
		    fis = new FileInputStream(path); 
			this.workbook = WorkbookFactory.create(fis); 

			}
			catch(Exception e){
				logger.info(e.getMessage(),e);
				return false; 
			}
			return true;
		}

}


=============================================================================


package com.amex.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {
    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static ExcelReader getExcelReader(String excelFilePath) {

	if (excelFilePath == null) {
	    return null;
	} else {
	    logger.info("Loading the Excel file : " + excelFilePath);
	    return ExcelReader.getInstance(excelFilePath);
	}
    }
    
    public static void createFile(String filepath, String fileName, String content) throws IOException {
	try {

	    if (createDirectory(filepath)) {
		File file = new File(filepath + fileName);
		file.createNewFile();

		FileWriter fw = new FileWriter(file.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(content);
		bw.close();

		logger.info("Directory/File is created!" + filepath + " "
			+ fileName);

	    }

	} catch (Exception e) {
	    logger.info(e.getMessage(),e);
	}

    }

    public static boolean createDirectory(String path) {
	File file = new File(path);
	if (!file.exists()) {
	    if (file.mkdirs()) {
		return true;
	    }
	} else {
	    return true;
	}
	return false;
    }

    public static void copyFile(String sourcePath, String destPath) throws IOException {

	File source = new File(sourcePath);
	File dest = new File(destPath);
	InputStream input = null;
	OutputStream output = null;
	try {
	    input = new FileInputStream(source);
	    output = new FileOutputStream(dest);
	    byte[] buf = new byte[1024];
	    int bytesRead;
	    while ((bytesRead = input.read(buf)) > 0) {
		output.write(buf, 0, bytesRead);
	    }
	} catch (Exception e) {
	    logger.info(e.getMessage(), e);
	} finally {
	    input.close();
	    output.close();
	}
    }

}




=====================================================================




package com.amex.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ParamsUtil {
	
	
	/**
     * Replaces the params in a string with corresponding values provided in the 
     * keyvalueparams in the form "_key: value,_key1:value1"
     * 
     * @param requestBody
     * @param keyvalueparams
     * @return
     */
    public static String replaceParams(String requestBody, String keyvalueparams) {
	ParamsUtil putils = new ParamsUtil();
	HashMap<String, String> params = generateRequestParamsMap(keyvalueparams);
	return putils.replaceRequestParameters(requestBody, params);
    }
	

    /**
     * Generates a map of key value parameters from a string in the format:
     * "key:value,key1:value1..." -->
     * 
     * @param params
     * @return
     */
    public static HashMap<String, String> generateRequestParamsMap(String params) {
	HashMap<String, String> paramsMap = new HashMap<String, String>();
	String[] paramSet = params.split(",");
	for (int i = 0; i < paramSet.length; i++) {
	    String[] param = paramSet[i].split(":");
	    if (param[1].equalsIgnoreCase("NIL")) {
		param[1] = "";
	    }
	    if (param[1].startsWith("TempProps[")) {
		Properties tempProps = PropertiesReader.loadPropertyFile(Constants.TEMP_PROP_PATH);
		String keyInProp = param[1].replaceAll(".*\\[|\\].*", "");
		param[1] = tempProps.getProperty(keyInProp).trim();
	    }
	    paramsMap.put(param[0].trim(), param[1].trim());
	}
	return paramsMap;
    }

    /**
     * Replace the placeholder request parameters in the JSON request body with
     * the values in a HashMap.
     * 
     * @param requestBody
     * @param params
     * @return
     */
    private String replaceRequestParameters(String requestBody, HashMap<String, String> params) {
	for (Map.Entry<String, String> entry : params.entrySet()) {
	    requestBody = requestBody.replace(entry.getKey(), entry.getValue());
	}
	return requestBody;
    }

    
}



=========================================================================




package com.amex.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 */
public class PropertiesReader {
    
    private static Logger logger = LoggerFactory.getLogger(PropertiesReader.class);

    /**
     * Loads the property file from the path provided and returns a Property
     * object
     * 
     * @param path
     * @return
     */
    public static Properties loadPropertyFile(String path) {
	logger.info("Loading the property file : " + path);
	Properties prop = new Properties();
	FileInputStream fis = null;

	try {
	    fis = new FileInputStream(path);
	    prop.load(fis);
	} catch (FileNotFoundException e) {
	    logger.info(e.getMessage(), e);
	} catch (IOException e) {
	    logger.info(e.getMessage(), e);
	} finally {
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

