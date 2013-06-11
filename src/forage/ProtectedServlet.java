package forage;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.oauth.OAuthService;
import com.google.appengine.api.oauth.OAuthServiceFactory;
import com.google.appengine.api.users.User;


//Using OAuth: requires 2 libaries = google-oauth-java-client and google-oauth-java-client. Get them here:
//http://fabiouechi.blogspot.co.nz/2011/11/using-google-oauth-java-client-to.html. 
public class ProtectedServlet extends HttpServlet {
	
	    @Override
	    public void doGet(HttpServletRequest req, HttpServletResponse resp)
		    throws IOException {
		User user = null;
		try {
		    OAuthService oauth = OAuthServiceFactory.getOAuthService();
		    //checks for user with current authorisation
		    user = oauth.getCurrentUser();
		    resp.getWriter().println("Authenticated: " + user.getEmail());
		} catch (OAuthRequestException e) {
		    resp.getWriter().println("Not authenticated: " + e.getMessage());
		}
	    }
	    
	}



