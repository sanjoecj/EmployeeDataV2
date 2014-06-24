package com.pk.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.client.Client;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.pk.test.comparators.JsonObjectComparator;

public class ServiceClient {
	public static void main(String args[]) {
		URL oracle;
		try {
			oracle = new URL("https://localhost:8443/EmployeeData/employees/");
		
        URLConnection yc = oracle.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(
                                    yc.getInputStream()));
        String inputLine;
        StringBuffer sb = new StringBuffer();
        while ((inputLine = in.readLine()) != null) 
            sb.append(inputLine);
        in.close();

        JSONParser jsonParser = new JSONParser();        
        Object obj = jsonParser.parse(sb.toString());
        
        JSONArray jsArray = (JSONArray) obj;
        List <JSONObject>list = new ArrayList<JSONObject>();
        Iterator<JSONObject> it = jsArray.iterator();
        
        while(it.hasNext()) {
        	
        	JSONObject jsonObj  = it.next();
        	list.add(jsonObj);
        }
        
        System.out.println(list.toString());
        Collections.sort(list, Collections.reverseOrder(new JsonObjectComparator("empId")));
        
       
        
        System.out.println("==============================");
        System.out.println(list.toString());
        
        
        
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
