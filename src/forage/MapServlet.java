package forage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;

@SuppressWarnings("serial")
public class MapServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		String maps = "";
		BufferedReader reader = null;
		Key parent = null;

		// specify the food to search for
		String food = req.getParameter("food");

		// query for the food item to be found
		Query foodItem = new Query("FoodItem");
		List<Entity> foodList = datastore.prepare(foodItem).asList(
				FetchOptions.Builder.withLimit(50));

		// go through the food list, and try to find parent entity
		if (food != null) {
			for (Entity e : foodList) {
				if (e.getProperty("name").equals(food)) {
					parent = e.getKey();
					break;
				}
			}
		}
		// create a new query based on what food item is being searched
		// if nothing found(or no parameters), return whole list of fooditems
		Query locationQuery;
		if (parent != null) {
			locationQuery = new Query("Location").setAncestor(parent)
					.addFilter(Entity.KEY_RESERVED_PROPERTY,
							Query.FilterOperator.GREATER_THAN, parent);
		} else {
			locationQuery = new Query("Location");
		}
		List<Entity> items = datastore.prepare(locationQuery).asList(
				FetchOptions.Builder.withLimit(50));

		// create a string that contains all the marker locations
		String markers = "";
		for (Entity i : items) {
			Key par = i.getParent();
			Entity p = null;
			try {
				p = datastore.get(par);
			} catch (EntityNotFoundException e) {

			}
			String lat = (String) i.getProperty("lat");
			String lon = (String) i.getProperty("long");
			String title = (String) p.getProperty("name");
			String health = (String) i.getProperty("health");
			String description = (String) i.getProperty("description");
			markers += "placeMarker(" + lat + ", " + lon + ", \"<div><p1>" + title
					+ "</p1><br><br>" + description + "<br>Health: "
					+ health + "/10</div>\", info);";
		}
		try {
			resp.setContentType("text/html");
			ServletContext s = this.getServletContext();
			String path = s.getRealPath("mapcode.txt");
			reader = new BufferedReader(new InputStreamReader(
					new FileInputStream(path), "UTF8"));

			maps = reader.readLine();
			maps += markers;
			maps += reader.readLine();

			resp.getWriter().println(maps);
		} catch (IOException e) {
			resp.setContentType("text/html");
			resp.getWriter()
					.println(
							"<!DOCTYPE html><html><body>Error Reading Text</body></html>");
		} finally {
			reader.close();
		}
	}
}
