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
			jsonString = "{movie:not found}";	
			try {
			URL url = new URL("http://www.omdbapi.com/?t="+id+"l&apikey=plzBanMe");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		      conn.setRequestMethod("GET");
		      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		      
		     JsonReader jsonReader;
			
			jsonReader = Json.createReader(new InputStreamReader(conn.getInputStream()));
			JsonObject jsonObject = jsonReader.readObject();
			} catch (IOException e) {
				e.printStackTrace();
			}			 
			
			jedis.set(id, jsonString);
		}
			
		
		return jsonString;
	}
	/*@GET
	@Produces({"text/html"})
	public String getProductsHTML() {
		String htmlString = "<html><body>";
		try {
			JAXBContext jaxbContext1 = JAXBContext.newInstance(ProductsXML.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();
			File XMLfile = new File("/Users/philippepossemiers/Desktop/Products.xml");
			ProductsXML productsXML = (ProductsXML)jaxbUnmarshaller.unmarshal(XMLfile);
			ArrayList<Product> listOfProducts = productsXML.getProducts();
			
			for(Product product : listOfProducts) {
				htmlString += "<b>Name : " + product.getName() + "</b><br>";
				htmlString += "Id : " + product.getId() + "<br>";
				htmlString += "Brand : " + product.getBrand() + "<br>";
				htmlString += "Description : " + product.getDescription() + "<br>";
				htmlString += "Price : " + product.getPrice() + "<br>";
				htmlString += "<br><br>";
			}
		} 
		catch (JAXBException e) {
		   e.printStackTrace();
		}
		return htmlString;
	}*/
	
	/*@GET
	@Produces({"text/html"})
	public String getProductsHTML() {
		
		String htmlString = "<html><body>";
		try {
			JsonReader reader = Json.createReader(new StringReader(getProductsJSON()));
			JsonObject rootObj = reader.readObject();
			JsonArray array = rootObj.getJsonArray("products");	
			array.forEach(System.out::println);
			for(int i = 0 ; i < array.size(); i++) {
				JsonObject obj = array.getJsonObject(i);
				System.out.println("niet kapot");
				htmlString += "<b>Name : " + obj.getString("name") + "</b><br>";
				htmlString += "ID : " + obj.getString("id") + "<br>";
				htmlString += "Brand : " + obj.getString("brand") + "<br>";
				htmlString += "Description : " + obj.getString("description") + "<br>";
				htmlString += "Price : " + obj.getJsonNumber("price") + "<br>";
				htmlString += "<br><br>";
			}
		}
		catch(Exception ex) {
			System.out.println(ex);
			htmlString = "<html><body><h1>Exception</h1>" + ex.getMessage();
		}

		return htmlString + "</body></html>";
	}*/
	
	/*@GET
	@Produces({"application/json"})
	public String getProductsJSON() {
		String jsonString = "";
		try {
			InputStream fis = new FileInputStream(FILE);
	        JsonReader reader = Json.createReader(fis);
	        JsonObject obj = reader.readObject();
	        reader.close();
	        fis.close();
	        
	        jsonString = obj.toString();
		} 
		catch (Exception ex) {
			jsonString = ex.getMessage();
		}
		
		return jsonString;
	}*/
	
	
	
	/*@GET
	@Path("/add")
	@Produces("text/html")
	public String getProductForm() {
		String form = "<html><body><h1>Add Product</h1><form action='/JAX-RS/products' method='post'><p>";
		form += "ID : <input type='text' name='id' /></p><p>Name : <input type='text' name='name' />";
		form += "</p><p>Brand : <input type='text' name='brand' /></p><p>Price : <input type='text' name='price' /></p>";
		form += "<p>Description : <input type='text' name='description' /></p><input type='submit' value='Add Product' />";
		form += "</form></body></html>";
		
		return form;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response addProduct(@FormParam("id") String id, @FormParam("name") String name, 
							 @FormParam("brand") String brand, @FormParam("price") float price, 
							 @FormParam("description") String description) {
	
		System.out.println(description);
		
		java.net.URI location = null;
		try {
			// read existing products
			InputStream fis = new FileInputStream(FILE);
			JsonReader jsonReader1 = Json.createReader(fis);
			JsonObject jsonObject = jsonReader1.readObject();
			jsonReader1.close();
			fis.close();
			
			JsonArray array = jsonObject.getJsonArray("products");
			JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
				        
			for(int i = 0; i < array.size(); i++){
				 // add existing products
				 JsonObject obj = array.getJsonObject(i);
				 arrBuilder.add(obj);
			}
			// add new product
			JsonObjectBuilder b = Json.createObjectBuilder().
					add("id", id).
					add("name", name).
					add("brand", brand).
					add("price", price).
					add("description", description);
			arrBuilder.add(b.build());
			
			// now wrap it in a JSON object
	        JsonArray newArray = arrBuilder.build();
	        JsonObjectBuilder builder = Json.createObjectBuilder();
	        builder.add("products", newArray);
	        JsonObject newJSON = builder.build();

	        // write to file
	        OutputStream os = new FileOutputStream(FILE);
	        JsonWriter writer = Json.createWriter(os);
	        writer.writeObject(newJSON);
	        writer.close();
			
			location = new java.net.URI("/JAX-RS/products");
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}

		return Response.seeOther(location).build();
	}
	
	@POST
	@Consumes({"application/json"})
	public Response addProduct(String productJSON) {
		java.net.URI location = null;
		try {
			// read existing products
			InputStream fis = new FileInputStream(FILE);
	        JsonReader jsonReader1 = Json.createReader(fis);
	        JsonObject jsonObject = jsonReader1.readObject();
	        jsonReader1.close();
	        fis.close();
	        
	        JsonReader jsonReader2 = Json.createReader(new StringReader(productJSON));
	        JsonObject newObject = jsonReader2.readObject();
	        jsonReader2.close();
	        
	        JsonArray array = jsonObject.getJsonArray("products");
	        JsonArrayBuilder arrBuilder = Json.createArrayBuilder();
	        
	        for(int i = 0; i < array.size(); i++){
	        	// add existing products
	        	JsonObject obj = array.getJsonObject(i);
	        	arrBuilder.add(obj);
	        }
	        // add new product
	        arrBuilder.add(newObject);
	        
	        // now wrap it in a JSON object
	        JsonArray newArray = arrBuilder.build();
	        JsonObjectBuilder builder = Json.createObjectBuilder();
	        builder.add("products", newArray);
	        JsonObject newJSON = builder.build();

	        // write to file
	        OutputStream os = new FileOutputStream(FILE);
	        JsonWriter writer = Json.createWriter(os);
	        writer.writeObject(newJSON);
	        writer.close();
	        
	        location = new java.net.URI("/JAX-RS/products");
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return Response.seeOther(location).build();
	}*/
}