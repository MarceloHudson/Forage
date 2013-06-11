package forage;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.oauth.OAuthRequestException;
import com.google.appengine.api.oauth.OAuthService;
import com.google.appengine.api.oauth.OAuthServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class LoginServlet extends HttpServlet {
	
		public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
			User user = null;
			try {
			    OAuthService oauth = OAuthServiceFactory.getOAuthService();
			    user = oauth.getCurrentUser();
			    resp.getWriter().println("Authenticated: " + user.getEmail());
			} catch (OAuthRequestException e) {
			    resp.getWriter().println("Not authenticated: " + e.getMessage());
			}
		    }
		}

