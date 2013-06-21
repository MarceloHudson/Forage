package forage;

import java.io.IOException;
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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

public class AddRecipeServlet extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		//get parameters from from 
		String recipe = "recipe";
		String ingredients = req.getParameter("ing");
		String parentFood = req.getParameter("parentFood");
		String description = req.getParameter("desc");
		String instructions = req.getParameter("instruc");
		String name = req.getParameter("name");
		
		//gets blob from blobstore to add to entity
	    Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        BlobKey blobKey = blobs.get("myFile");
        
        //query datastore for entity of kind FoodItem with name of the food entered in form. Should actually only return one entity.
	    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    @SuppressWarnings("deprecation")
		Query query = new Query("FoodItem").addFilter("name", Query.FilterOperator.EQUAL, parentFood);
	    Entity item = datastore.prepare(query).asSingleEntity();
		Key parentKey = item.getKey();
		
		//create recipe entity with the values entered on jsp as properties
		Entity rec = new Entity("Recipe", parentKey);
		rec.setProperty("respDescription", description);
		rec.setProperty("ingredients", ingredients);
		rec.setProperty("instructions", instructions);
		rec.setProperty("image", blobKey);
		rec.setProperty("name", name);
		
		//add to datastore
		datastore.put(rec);

		//send back to jsp
		resp.sendRedirect("/addrecipes.jsp");
}
}
