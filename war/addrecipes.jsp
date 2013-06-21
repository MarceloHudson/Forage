<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@ page import="com.google.appengine.api.datastore.Query"%>
<%@ page import="com.google.appengine.api.datastore.Entity"%>
<%@ page import="com.google.appengine.api.datastore.FetchOptions"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<html>
<head>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
</head>

<body>
	
	<%
	BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	String recipe = "recipe";
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key recipeKey = KeyFactory.createKey("Recipe", recipe);
    
    // Run an ancestor query to ensure we see the most up-to-date
    // view of the FoodTypes belonging to Food.
    Query query = new Query("Recipe", recipeKey);
    List<Entity> recipes = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(15));
    if (recipes.isEmpty()) {
        %>
	<p>There are no recipes loaded into the database.</p>
	<%
    } else {
        %>
	<p>Recipes:</p>
	<%
        for (Entity f : recipes) {
            pageContext.setAttribute("type_content", f.getProperty("name"));
             
            
            %>
	<blockquote>${fn:escapeXml(type_content)}</blockquote>
<% 
        }
    }
%>


	<%--  --%>


	<form action="<%= blobstoreService.createUploadUrl("/addrecipe") %>" method="post" enctype="multipart/form-data">
		<p>Parent food name:</p>
		<div>
			<textarea name="parentFood" rows="1" cols="40"></textarea>
		</div>
		
		<p>Recipe name:</p>
		<div>
			<textarea name="name" rows="1" cols="40"></textarea>
		</div>
		
		<p>Recipe Description:</p>
		<div>
			<textarea name="desc" rows="5" cols="40"></textarea>
		</div>
		
		<p>Ingredients:</p>
		<div>
			<textarea name="ing" rows="5" cols="40"></textarea>
		</div>
		
		<p>Recipe Instructions:</p>
		<div>
			<textarea name="instruc" rows="5" cols="40"></textarea>
		</div>
		<p>Add an image:</p>
		<div>
			 <input type="file" name="myFile">
		</div>
		<div>
			<input type="submit" value="Add Recipe" />
		</div>
		</form>
</body>
</html>