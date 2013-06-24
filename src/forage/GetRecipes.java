package forage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
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

import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.oauth.OAuthService;
import com.google.appengine.api.oauth.OAuthServiceFactory;
import com.google.appengine.api.users.User;




/**
 * Services requests for new recipes from client apps. Returns XML
 * @author M Hudson
 *
 */
@SuppressWarnings("serial")
public class GetRecipes extends HttpServlet {
	private List<String> vals = new ArrayList<String>();
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{ 
		BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
		String recipe = "Recipe";
	    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	    User user = null;
	    
	    //authorisation check
		try {
		    OAuthService oauth = OAuthServiceFactory.getOAuthService();
		    //checks for user with current authorisation
		    user = oauth.getCurrentUser();
		} catch (OAuthRequestException e) {
		    resp.getWriter().println("Not authenticated: " + e.getMessage());
		    return;
		}
	    
		Enumeration e = req.getParameterNames();
		while(e.hasMoreElements()){
			String val = (String)e.nextElement();
			vals.add(val);
			
		}
		
		String value = req.getParameter(vals.get(0));
		
	    // Run an kind query to get a list of all of the recipes
	    Query query = new Query(recipe);
	    List<Entity> recipes = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(100));
	    
	    if(recipes.size() != Integer.parseInt(value)){
	    
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				// root elements
				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement("recipe");
				doc.appendChild(rootElement);

				// add entity properties as elements to xml
				for (Entity r : recipes) {

					Element item = doc.createElement("item");
					rootElement.appendChild(item);

					// name node
					Element name = doc.createElement("name");
					String itemName = (String) r.getProperty("name");
					name.appendChild(doc.createTextNode(itemName));
					item.appendChild(name);

					// ingredient node
					Element ingred = doc.createElement("ingredients");
					Text tmp1 = (Text) r.getProperty("ingredients");
					String itemIngr = tmp1.getValue();
					ingred.appendChild(doc.createTextNode(itemIngr));
					item.appendChild(ingred);

					// instructions node
					Element instruct = doc.createElement("instructions");
					Text tmp2 = (Text) r.getProperty("instructions");
					String itemInstruct = tmp2.getValue();
					instruct.appendChild(doc.createTextNode(itemInstruct));
					item.appendChild(instruct);

					// image node
					// getting an image byte stream and encoding it into a
					// string able to be stored in XML
					BlobKey blobKey = (BlobKey) r.getProperty("image");
					BlobInfo blobInfo = new BlobInfoFactory()
							.loadBlobInfo(blobKey);
					byte[] image = blobstoreService.fetchData(blobKey, 0,
							blobInfo.getSize());
					String encodedImage = Base64.encodeBase64String(image); // store
																			// this
																			// byte
																			// stream
																			// within
																			// the
																			// xml
																			// node

					Element respImage = doc.createElement("image");
					String itemImage = encodedImage;
					respImage.appendChild(doc.createTextNode(itemImage));
					item.appendChild(respImage);

				}

				// write the content into xml file
				TransformerFactory transformerFactory = TransformerFactory
						.newInstance();
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
		} else {
			resp.setContentType("text/xml;charset=UTF-8");
			resp.getWriter().println("null");
		}
	    
	}

}
