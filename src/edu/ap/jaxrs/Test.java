package edu.ap.jaxrs;

import java.io.*;
import java.net.URISyntaxException;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.json.*;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Path("/test")
public class Test {
	
	private String FILE;
	
	public Test(@Context ServletContext servletContext) {
		//System.out.println("Hallokes");		
		FILE = servletContext.getInitParameter("FILE_PATH");
	}
	@GET
	@Produces("text/html")
	public String getroot() {
		return "<!DOCTYPE html><html><body><div>Okey</div><h1>Mooi</h1></body></html>";
	}
	@GET
	@Path("/add")
	@Produces("text/html")
	public String getURLParam() {
		return "<html><body><form action=\"/JAX-RS/test/\" method=\"post\">\t<input name=\"name\" type=\"text\">\t<input value=\"Send\" type=\"submit\"></form></body></html>";
	}
	@GET
	@Path("/jsp")
	@Produces("text/html")
	public void getJSP() {
		
	}	
	@GET
	@Path("{name}")
	@Produces("text/html")
	public String getURLParam(@PathParam("name") String name) {
		return "<html><body><h1>Mooi "+name+"</h1></body></html>";
	}	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response addProduct(@FormParam("name") String name) {	
		
		java.net.URI location=null;
		try {
			location = new java.net.URI("/JAX-RS/test/"+name);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		return Response.seeOther(location).build();
	}
}