package com.pk.employeedata.dao;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.pk.employeedata.dto.Employee;

public class EmployeeDao {
	
	public static EmployeeDao employeeDao;
	
	private Map<Integer, Employee> contentProvider = new HashMap<Integer, Employee>();
	
	private EmployeeDao() {
		Employee emp1 = new Employee();
		emp1.setEmpId(2);
		emp1.setAge(33);
		emp1.setBloodGroup("B+ve");
		emp1.setDepartment("Web");
		emp1.setFirstName("Jolly");
		emp1.setJoinDate("2-Jan");
		emp1.setLastName("Doe");
		emp1.setSex("Female");
		emp1.setShift("Day");
		contentProvider.put(1, emp1);
		
		Employee emp2 = new Employee();
		emp2.setEmpId(4);
		emp2.setAge(23);
		emp2.setBloodGroup("B+ve");
		emp2.setDepartment("Dot net");
		emp2.setFirstName("Brad");
		emp2.setJoinDate("3-Jan");
		emp2.setLastName("Pitt");
		emp2.setSex("Male");
		emp2.setShift("Night");
		contentProvider.put(2, emp2);
		
		Employee emp3 = new Employee();
		emp3.setEmpId(5);
		emp3.setAge(50);
		emp3.setBloodGroup("A+ve");
		emp3.setDepartment("Unknown");
		emp3.setFirstName("Justin");
		emp3.setJoinDate("3-Jan");
		emp3.setLastName("Timberlake");
		emp3.setSex("Male");
		emp3.setShift("Night");
		contentProvider.put(3, emp3);
		
		Employee emp4 = new Employee();
		emp4.setEmpId(1);
		emp4.setAge(32);
		emp4.setBloodGroup("A+ve");
		emp4.setDepartment("Java");
		emp4.setFirstName("John");
		emp4.setJoinDate("1-Jan");
		emp4.setLastName("Doe");
		emp4.setSex("Male");
		emp4.setShift("Day");
		contentProvider.put(4, emp4);
		
		Employee emp5 = new Employee();
		emp5.setEmpId(3);
		emp5.setAge(43);
		emp5.setBloodGroup("B+ve");
		emp5.setDepartment("Web");
		emp5.setFirstName("Steeve");
		emp5.setJoinDate("3-Jan");
		emp5.setLastName("Waugh");
		emp5.setSex("Male");
		emp5.setShift("Day");
		contentProvider.put(5, emp5);
	}
	
	public static EmployeeDao getInstance(){
		if(null == employeeDao) {
			synchronized (EmployeeDao.class) {
		        if (employeeDao == null) {
		        	employeeDao = new EmployeeDao();
		        }
		      }
		}
		return employeeDao;
	}
	
	public Map<Integer, Employee> getModel(){
	    return contentProvider;
	  }
	
	public void clear(){
		contentProvider.clear();
	}
	
	public void addAll(List<Employee> empList) {
		Iterator<Employee> it = empList.iterator();
		Integer i = 0;
		while(it.hasNext()) {
			Employee emp = it.next();
			contentProvider.put((i++), emp);
		}
	}
	
	public Employee getEmployeeByID(String id) {
		Collection<Employee> empList = contentProvider.values();
		Iterator<Employee> it = empList.iterator();
		while(it.hasNext()){
			Employee emp = it.next();
			if(emp.getEmpId() == Integer.valueOf(id)) return emp;
		}
		
		return null;
	}
	
	public static void reset() {
		employeeDao = new EmployeeDao();
	}
}
