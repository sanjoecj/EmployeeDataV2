package com.pk.employeedata.res;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.pk.employeedata.dao.EmployeeDao;
import com.pk.employeedata.dto.Employee;

@Path("/")
public class EmpResource {
	@GET
	@Path("/employees")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Employee> getAllEmployees() {
		List<Employee> emplList = new ArrayList<Employee>();
		emplList.addAll(EmployeeDao.getInstance().getModel().values());
		return emplList;
	}
	
	@GET
	@Path("/employees/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Employee getEmployee(@PathParam("id") String id) {
		return EmployeeDao.getInstance().getEmployeeByID(id);
	}
	
	@GET
	@Path("/employees/{id}")
	@Produces(MediaType.APPLICATION_XML)
	public Employee getEmployeeXML(@PathParam("id") String id) {
		return EmployeeDao.getInstance().getEmployeeByID(id);
	}
	
	@POST
	@Path("/employees")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addEmployees(List<Employee> employees) {
		EmployeeDao.getInstance().clear();
		EmployeeDao.getInstance().addAll(employees);
		return Response.status(Status.OK).build();
	}
	
	@POST
	@Path("/employees/add")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addEmployees(@HeaderParam("UID") String uid,String employee) {
		
		if (uid == null) {
			return  Response.status(Status.BAD_REQUEST).build();
		}
		
		return Response.status(Status.OK).build();
	}
	
	@GET
	@Path("/reset")
	public Response reset() {
		EmployeeDao.reset();
		return Response.status(Status.OK).build();
	}
	
}
