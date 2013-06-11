package forage;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

public class AddFoodItemsServlet extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	
		//get list of food types and return as list - uses foodKey to run ancestor query
		String food = "food";
	    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    Key foodKey = KeyFactory.createKey("Food", food);
	    String foodType = req.getParameter("type"); //foodtype selected by admin
	    String name = req.getParameter("item"); //item name e.g Apple
	    String description = req.getParameter("description"); //General item description
	    
	    //gets blob from blobstore to add to entity
	    Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        BlobKey blobKey = blobs.get("myFile");
	    
	    Query query = new Query("FoodType", foodKey);
	    List<Entity> types = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(15));
		Key parentKey = null;
	       
		//look through returned food types for selected type and grab key
		for(Entity e: types){
			if(foodType.equals(e.getProperty("type"))){
				parentKey = e.getKey();
			}
		}

		// create foodItem entity with selected foodType as parentKey 
		Entity foodItem = new Entity("FoodItem", parentKey);
		foodItem.setProperty("name", name);
		foodItem.setProperty("description", description);
		foodItem.setProperty("image", blobKey);
		datastore.put(foodItem);

		// send back to jsp
		resp.sendRedirect("/addfooditems.jsp");
	}

}
