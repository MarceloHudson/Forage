package forage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;
import javax.servlet.http.*;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;

@SuppressWarnings("serial")
public class Servlet extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		// get list of food items of the kind received in req and return as list
		String kind = req.getParameter("kind");
		String description = req.getParameter("description"); // Specific
																// location
																// description
		String name = req.getParameter("name");
		String lat = req.getParameter("lat");
		String lng = req.getParameter("long");
		String health = req.getParameter("health");
		String verifiedS = req.getParameter("verified");
		boolean verified = false;

		if (verifiedS.equalsIgnoreCase("True")) {
			verified = true;
		}

		// query datastore for entity of kind FoodItem with name kind. Should
		// actually only return one entity.
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		@SuppressWarnings("deprecation")
		Query query = new Query("FoodItem").addFilter("name",
				Query.FilterOperator.EQUAL, kind);
		Entity item = datastore.prepare(query).asSingleEntity();
		Key parentKey = item.getKey();

		// create itemLocation entity with selected foodItem as parentKey
		Entity itemLocation = new Entity("Location", parentKey);
		itemLocation.setProperty("description", description);
		itemLocation.setProperty("name", name);
		itemLocation.setProperty("lat", lat);
		itemLocation.setProperty("long", lng);
		itemLocation.setProperty("health", health);
		itemLocation.setProperty("verified", verified);

		datastore.put(itemLocation);

		// then write back a response for the marker in the app!!
		try {
			String response = "<p1>" + kind + "</p1><br><br>" + description
					+ "<br><br>Health: " + health + "/10";
			resp.getWriter().println(response);
		} catch (IOException e) {
			resp.setContentType("text/html");
			resp.getWriter()
					.println(
							"<!DOCTYPE html><html><body>Error Reading Text</body></html>");
		}
	}
}
