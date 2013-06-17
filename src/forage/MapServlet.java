package forage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class MapServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		//hopefully this works
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		//String maps = "";
		//BufferedReader reader;
		Entity loc;
		Key location = KeyFactory.createKey("Location", "Location");
		try{
		loc = datastore.get(location);
		}catch(Exception e){
			resp.setContentType("text/html");
			resp.getWriter().println(
					"<!DOCTYPE html><html><body>Entity Not Found</body></html>");
		}
		/*
		try{
			resp.setContentType("text/html");
			ServletContext  s= this.getServletContext();
			String path = s.getRealPath("mapcode.txt");
		 reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(path), "UTF8"));
		//req.getAttribute("type");

		maps = reader.readLine();
		//get locations here format placeMarker(lon, lat, title, info) (dont change info) Also title is formatted as html
		maps += reader.readLine();
		
		resp.getWriter().println(maps);
		}catch(IOException e){
			resp.setContentType("text/html");
			resp.getWriter().println(
					"<!DOCTYPE html><html><body>Error Reading Text</body></html>");
		}
		finally{
			reader.close();
		}*/
	}
}
