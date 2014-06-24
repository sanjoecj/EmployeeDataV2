package com.pk.test.comparators;

import java.util.Comparator;

import org.json.simple.JSONObject;

public class JsonObjectComparator implements Comparator<JSONObject> {
	
	private String compareField;
	
	public JsonObjectComparator(String compString) {
		this.compareField = compString;
	}
	
	@Override
	public int compare(JSONObject obj1, JSONObject obj2) {
		// TODO Auto-generated method stub
		
		String field1 = obj1.get(this.compareField).toString();
		String field2 = obj2.get(this.compareField).toString();
		
		return field1.compareTo(field2);

	}

}
