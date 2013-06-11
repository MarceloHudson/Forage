package forage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

/**
 * This servlet serves Get requests from client apps for newly added food items. Returns XML
 * @author M Hudson
 *
 */
public class GetFoodItems extends HttpServlet{

	private static final long serialVersionUID = -177880688284914817L;
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
	
		//create key to run ancestor query on food types
		String food = "food";
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		Key foodKey = KeyFactory.createKey("Food", food);

		// Run an ancestor query to get food types
		Query typeQuery = new Query("FoodType", foodKey);
		List<Entity> types = datastore.prepare(typeQuery).asList(FetchOptions.Builder.withLimit(15));
		
		List<Key> pKeys = new ArrayList<Key>();

		//grab the keys of the food types in order to use them for ancestor query
		if (!types.isEmpty()) {
			for (Entity e : types) {
				pKeys.add(e.getKey());
			}
		}
		
		/*NOTE: the app with send the food type as part of their request. This will determine which parent key is
		 * used in the query below.
		 */

		
		//run ancestor query to get food items that are children of food type (the addFilter() stops it returning the 
		//parent as part of the list)
		@SuppressWarnings("deprecation")
		Query itemQuery = new Query("FoodItem")
				.setAncestor(pKeys.get(0))
				.addFilter(Entity.KEY_RESERVED_PROPERTY,
						Query.FilterOperator.GREATER_THAN, pKeys.get(0));
		
		List<Entity> items = datastore.prepare(itemQuery).asList(
				FetchOptions.Builder.withLimit(15));
		

		try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		 
				// root elements
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("food");
				doc.appendChild(rootElement);
				
				//add entity properties as elements to xml
				for(Entity t: items){
					
					Element item = doc.createElement("item");
					rootElement.appendChild(item);
					
					Element name = doc.createElement("name");
					String itemName = (String) t.getProperty("name");
					name.appendChild(doc.createTextNode(itemName));
					item.appendChild(name);
					
					Element desc = doc.createElement("description");
					String itemDesc = (String) t.getProperty("description");
					desc.appendChild(doc.createTextNode(itemDesc));
					item.appendChild(desc);
			}
				
				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				
				// Output xml as http response
				resp.setContentType("text/xml;charset=UTF-8");
				StreamResult result = new StreamResult(resp.getOutputStream());
		 
				transformer.transform(source, result);
		 
			} catch (ParserConfigurationException pce) {
				pce.printStackTrace();
			} catch (TransformerException tfe) {
				tfe.printStackTrace();
			}	
	}
		
		
}


