package edu.ap.jaxrs;

import java.io.*;
import java.net.*;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import redis.clients.jedis.Jedis;

import javax.json.*;
import javax.servlet.ServletContext;

@Path("/movies")
public class movies {
	
	Jedis jedis;
	
	public movies(@Context ServletContext servletContext) {	
		jedis = new Jedis("localhost");
	}
	
	@GET
	@Path("{id}")
	@Produces({"application/json"})
	public String getMovieJSON(@PathParam("id") String id) {
		
		String jsonString = "";
		String js = jedis.get(id);
		if(js != ""){
			jsonString = js;
		}else{
			jsonString = "";	
			try {
				
			URL url = new URL("http://www.omdbapi.com/?t="+id+"l&apikey=plzBanMe");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		      conn.setRequestMethod("GET");
		      
		     JsonReader jsonReader;
			
			jsonReader = Json.createReader(new InputStreamReader(conn.getInputStream()));
			JsonObject jsonObject = jsonReader.readObject();
			
			String year = jsonObject.getString("Year");
			String director = jsonObject.getString("Director");
			
			jsonString = "{year:"+year+",director:"+director+"}";
			} catch (IOException e) {
				return "{movie:not found}";
			}			 
			
			jedis.set(id, jsonString);
		}
			
		
		return jsonString;
	}
}