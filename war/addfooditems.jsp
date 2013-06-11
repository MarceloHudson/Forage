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
	String food = "food";
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Key foodKey = KeyFactory.createKey("Food", food);
    
    // Run an ancestor query to ensure we see the most up-to-date
    // view of the FoodTypes belonging to Food.
    Query query = new Query("FoodType", foodKey);
    List<Entity> types = datastore.prepare(query).asList(FetchOptions.Builder.withLimit(15));
    if (types.isEmpty()) {
        %>
	<p>There are no types loaded into the database.</p>
	<%
    } else {
        %>
	<p>Types:</p>
	<%
        for (Entity f : types) {
            pageContext.setAttribute("type_content", f.getProperty("type"));
             
            
            %>
	<blockquote>${fn:escapeXml(type_content)}</blockquote>
	<% 
        }
    }
%>


	<%--  --%>


	<form action="<%= blobstoreService.createUploadUrl("/addfooditems") %>" method="post" enctype="multipart/form-data">
		<%-- 
		<div>
		<select name="foodTypes">
			<option name="type" value="Fruit">Fruit</option>
			<option name="type" value="Vegetable">Vegetable</option>
			<option name="type" value="Herb">Herb</option>
		</select>
		</div>--%>

		<p>Food type:</p>
		<div>
			<textarea name="type" rows="1" cols="40"></textarea>

		</div>
		<p>Food item:</p>
		<div>
			<textarea name="item" rows="1" cols="40"></textarea>

		</div>
		<p>Description:</p>
		<div>
			<textarea name="description" rows="5" cols="40"></textarea>

		</div>
		<p>Add an image:</p>
		<div>
			 <input type="file" name="myFile">
		</div>
		<div>
			<input type="submit" value="Add Food Item" />
		</div>
	</form>
	
</body>
</html>