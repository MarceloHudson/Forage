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

public class AddRecipeServlet extends HttpServlet {
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		
		//set parent reference to recipe 
		String recipe = "recipe";
		Key recipeKey = KeyFactory.createKey("Recipe", recipe);
		String ingredients = req.getParameter("ing");
		String description = req.getParameter("desc");
		String instructions = req.getParameter("instruc");
		String name = req.getParameter("name");
		
		//gets blob from blobstore to add to entity
	    Map<String, BlobKey> blobs = blobstoreService.getUploadedBlobs(req);
        BlobKey blobKey = blobs.get("myFile");
		
		//create recipe entity with the values entered on jsp as properties
		Entity rec = new Entity("Recipe", recipeKey);
		rec.setProperty("respDescription", description);
		rec.setProperty("ingredients", ingredients);
		rec.setProperty("instructions", instructions);
		rec.setProperty("image", blobKey);
		rec.setProperty("name", name);
		
		//add to datastore
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		datastore.put(rec);

		//send back to jsp
		resp.sendRedirect("/addrecipes.jsp");
}
}
