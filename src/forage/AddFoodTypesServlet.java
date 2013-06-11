package forage;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class AddFoodTypesServlet extends HttpServlet {
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		//set parent reference to food (is the root of the datastore)
		String food = "food";
		Key foodKey = KeyFactory.createKey("Food", food);
		String type = req.getParameter("type"); //was content
		
		//create foodtype entity with the value entered
		Entity foodType = new Entity("FoodType", foodKey);
		foodType.setProperty("type", type);
		

		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(foodType);
		
		//send back to jsp
		resp.sendRedirect("/addfoodtypes.jsp");
}
}
