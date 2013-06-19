<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>
<%@ page
	import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@ page import="com.google.appengine.api.datastore.DatastoreService"%>
<%@ page import="com.google.appengine.api.datastore.Query"%>
<%@ page import="com.google.appengine.api.datastore.Entity"%>
<%@ page import="com.google.appengine.api.datastore.FetchOptions"%>
<%@ page import="com.google.appengine.api.datastore.Key"%>
<%@ page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<html>
<head>
<link type="text/css" rel="stylesheet" href="/stylesheets/main.css" />
</head>

<body>

	<%
		//create key to run ancestor query on food types
		String food = "food";
		DatastoreService datastore = DatastoreServiceFactory
				.getDatastoreService();
		Key foodKey = KeyFactory.createKey("Food", food);

		// Run an ancestor query to get food types
		Query typeQuery = new Query("FoodType", foodKey);
		List<Entity> types = datastore.prepare(typeQuery).asList(
				FetchOptions.Builder.withLimit(15));
		List<Key> pKeys = new ArrayList<Key>();

		//grab the keys of the food types in order to use them for ancestor query
		if (!types.isEmpty()) {
			for (Entity e : types) {
				pKeys.add(e.getKey());
			}
		}

		if (!pKeys.isEmpty()) {
			//run ancestor query to get food items that are children of food type (the addFilter() stops it returning the parent as part of the list)
			Query itemQuery = new Query("FoodItem")
					.setAncestor(pKeys.get(0))
					.addFilter(Entity.KEY_RESERVED_PROPERTY,
							Query.FilterOperator.GREATER_THAN, pKeys.get(0));
			
			List<Entity> items = datastore.prepare(itemQuery).asList(
					FetchOptions.Builder.withLimit(15));

			//run ancestor query to get food items that are children of food type (the addFilter() stops it returning the parent as part of the list)
			Query itemQuery1 = new Query("FoodItem")
					.setAncestor(pKeys.get(1))
					.addFilter(Entity.KEY_RESERVED_PROPERTY,
							Query.FilterOperator.GREATER_THAN, pKeys.get(1));
			
			List<Entity> items1 = datastore.prepare(itemQuery1).asList(
					FetchOptions.Builder.withLimit(15));

			//run ancestor query to get food items that are children of food type (the addFilter() stops it returning the parent as part of the list)
			Query itemQuery2 = new Query("FoodItem")
					.setAncestor(pKeys.get(2))
					.addFilter(Entity.KEY_RESERVED_PROPERTY,
							Query.FilterOperator.GREATER_THAN, pKeys.get(2));
			
			List<Entity> items2 = datastore.prepare(itemQuery2).asList(
					FetchOptions.Builder.withLimit(15));

			//print to screen
			if (items.isEmpty()) {
	%>
	<p>There are no food items (Fruit) loaded into the database.</p>
	<%
		} else {
	%>
	<p>Fruit Items:</p>
	<%
		for (Entity f : items) {
					pageContext.setAttribute("type_content",
							f.getProperty("name"));
	%>
	<div>
		<blockquote>${fn:escapeXml(type_content)}</blockquote>
	</div>
	<%
		}
			}

			//print to screen
			if (items1.isEmpty()) {
	%>
	<p>There are no food items (Vegetable) loaded into the database.</p>
	<%
		} else {
	%>
	<p>Vegetable Items:</p>
	<%
		for (Entity f : items1) {
					pageContext.setAttribute("type_content1",
							f.getProperty("name"));
	%>
	<div>
		<blockquote>${fn:escapeXml(type_content1)}</blockquote>
	</div>
	<%
		}
			}

			//print to screen
			if (items2.isEmpty()) {
	%>
	<p>There are no food items (Herb) loaded into the database.</p>
	<%
		} else {
	%>
	<p>Herb Items:</p>
	<%
		for (Entity f : items2) {
					pageContext.setAttribute("type_content2",
							f.getProperty("name"));
	%>
	<div>
		<blockquote>${fn:escapeXml(type_content2)}</blockquote>
	</div>
	<%
		}
			}
		}
	%>

	<form action="/addfoodlocations" method="post">

		<p>Food item:</p>
		<div>
			<textarea name="kind" rows="1" cols="40"></textarea>
		</div>

		<p>Location name:</p>
		<div>
			<textarea name="name" rows="1" cols="40"></textarea>
		</div>

		<p>Description:</p>
		<div>
			<textarea name="description" rows="5" cols="40"></textarea>

		</div>
		<p>Latitude:</p>
		<div>
			<textarea name="lat" rows="1" cols="40"></textarea>

		</div>

		<p>Longitude:</p>
		<div>
			<textarea name="long" rows="1" cols="40"></textarea>

		</div>

		<p>Health (out of 10):</p>
		<div>
			<textarea name="health" rows="1" cols="40"></textarea>

		</div>
		
		<p>Verified (True of False):</p>
		<div>
			<textarea name="verified" rows="1" cols="40"></textarea>

		</div>
		<div>
			<input type="submit" value="Add Food Location" />
		</div>
	</form>


</body>
</html>