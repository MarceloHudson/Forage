google.maps.visualRefresh = true;
var map, GeoMarker, mc, info, markerList, markerList2;
var markerState = false;
function initialize() {
	var mapOptions = {
		zoom : 14,
		mapTypeId : google.maps.MapTypeId.SATELLITE,
		panControl : false,
		zoomControl : false,
		mapTypeControlOptions : {
			style : google.maps.MapTypeControlStyle.HORIZONTAL_BAR,
			padding : "20px",
			position : google.maps.ControlPosition.TOP_RIGHT
		}
	};
	map = new google.maps.Map(document.getElementById("map-canvas"), mapOptions);
	GeoMarker = new GeolocationMarker();
	GeoMarker.setCircleOptions({
		fillColor : '#808080'
	});
	if (navigator.geolocation) {
		navigator.geolocation.getCurrentPosition(function(position) {
			var pos = new google.maps.LatLng(position.coords.latitude,
					position.coords.longitude);
			map.setCenter(pos);
		}, function() {
			handleNoGeolocation(true);
		});
	} else {
		handleNoGeolocation(false);
	}
	GeoMarker.setMap(map);
	info = new InfoBubble({
		maxWidth : 150,
		shadowStyle : 0,
		backgroundColor : 'rgba(255,255,255,0.85)'
	});
	markerList = [];
	markerList2 = [];
	var mcOptions = {
		gridSize : 25,
		maxZoom : 15,
		styles : [ {
			height : 31,
			url : "cluster.png",
			width : 33
		} ]
	};
	mc = new MarkerClusterer(map, markerList2, mcOptions);


	placeMarker(-41.316753, 174.792801,
			"<div1><p>Crab Apples<div1><br/> Health : 7/10</p>", true,
			"red-shadow.png");
	placeMarker(-41.326591, 174.822647, "<p>Figs <br/>Health : 8/10</p>", true,
			"red-shadow.png");
	placeMarker(-41.301033, 174.782959, "<p>Olives <br/>Health : 7/10</p>",
			true, "red-shadow.png");
	placeMarker(-41.297287, 174.789353,
			"<p>Walnut Tree<br/> Health : 4/10</p>", true, "red-shadow.png");
	placeMarker(-41.308392, 174.777389, "<p>Apple <br/>Health : 2/10</p>",
			false, "red-shadow.png");
	placeMarker(-41.297337, 174.789353,
			"<p>Cabbage Tree <br/>Health : 7/10</p>", false, "red-shadow.png");
	placeMarker(-41.29928, 174.77826, "<p>Elder Tree <br/>Health : 10/10</p>",
			false, "red-shadow.png");
	placeMarker(-41.29794, 174.77736, "<p>Pear Tree<br/>Health : 7/10</p>",
			false, "red-shadow.png");
	placeMarker(-41.307598, 174.791653,
			"<p>Kawakawa Tree<br/>Health : 5/10</p>", false, "red-shadow.png");
	placeMarker(-41.317960, 174.787339,
			"<p>Blackberries<br/>Health : 6/10</p>", false, "red-shadow.png");
	placeMarker(-41.307653, 174.784835,
			"<p>Blackberries<br/>Health : 8/10</p>", false, "red-shadow.png");

	// split
	mc.clearMarkers();
	mc.addMarkers(markerList);
	createLayout();


	google.maps.event.addListener(map, 'click', function(event) {
		httpPost("http://seekandsavour.appspot.com/forage", event.latLng)
	});

}
function handleNoGeolocation(errorFlag) {
	if (errorFlag) {
		var content = 'Error: The Geolocation service failed.';
	} else {
		var content = 'Error: Your browser doesn\'t support geolocation.';
	}
	var options = {
		map : map,
		position : new google.maps.LatLng(-41.2889, 174.7772),
		content : content
	};
	map.setCenter(options.position);
}
function placeMarker(lat, lng, title, visible, foodType) {
	var location = new google.maps.LatLng(lat, lng);
	var markers = new google.maps.Marker({
		position : location,
		map : map,
		title : title,
		icon : foodType
	});
	if (visible == true) {
		markerList.push(markers);
	}
	markerList2.push(markers);
	google.maps.event.addListener(markers, 'click', function() {
		info.setContent(title);
		info.open(map, markers);
		map.panTo(markers.getPosition());
	});
}

function createLayout(map){
	var img = document.createElement("IMG");
	img.src = "buttonone.png";
	var control = document.createElement('div');
	control.style.paddingLeft = '5px';
	control.style.paddingBottom = '40px';
	control.style.cursor = 'pointer';
	control.appendChild(img);
	google.maps.event.addDomListener(control, 'click', function() {
		img.src = "buttongrey.png";
		setTimeout(function() {
			img.src = "buttonone.png";
		}, 200);
		var point = GeoMarker.getPosition();
		map.panTo(point);
	});
	map.controls[google.maps.ControlPosition.LEFT_BOTTOM].push(control);
	var img2 = document.createElement("IMG");
	img2.src = "buttonone.png";
	var control2 = document.createElement('div');
	control2.style.paddingLeft = '5px';
	control2.style.paddingBottom = '40px';
	control2.style.cursor = 'pointer';
	control2.appendChild(img2);

	google.maps.event.addDomListener(control2, 'click', function() {
		img2.src = "buttongrey.png";
		setTimeout(function() {
			img2.src = "buttonone.png";
		}, 200);
		mc.clearMarkers();
		if (markerState == false) {
			mc.addMarkers(markerList2);
			markerState = true;
		} else {
			mc.addMarkers(markerList);
			markerState = false;
		}
	});
	map.controls[google.maps.ControlPosition.RIGHT_BOTTOM].push(control2);
	
	var foodTypeInput = document.createElement("INPUT");
	foodTypeInput.name = "foodType";
	foodTypeInput.type = "text";
	foodTypeInput.style.width = "79%";
	foodTypeInput.style.padding = "3%";
	foodTypeInput.style.outline = "none";
	foodTypeInput.style.border = "none";
	foodTypeInput.style.margin = "7.5%";
	foodTypeInput.placeholder = "Food Type";

	var descriptionInput = document.createElement("TEXTAREA");
	descriptionInput.name = "descType";
	descriptionInput.rows = "5";
	descriptionInput.maxlength = "400";
	descriptionInput.wrap = "soft";
	descriptionInput.style.width = "79%";
	descriptionInput.style.resize = "none";
	descriptionInput.style.outline = "none";
	descriptionInput.style.border = "none";
	descriptionInput.style.margin = "7.5%";
	descriptionInput.style.padding = "3%";
	descriptionInput.placeholder = "Description";
	descriptionInput.style.overflow = "hidden";

	var healthInput = document.createElement("INPUT");
	healthInput.name = "healthType";
	healthInput.style.width = "79%";
	healthInput.style.border = "none";
	healthInput.style.outline = "none";
	healthInput.style.margin = "7.5%";
	healthInput.style.padding = "3%";
	healthInput.type = "text";
	healthInput.placeholder = "Health";

	var control3 = document.createElement('div');
	control3.style.paddingTop = '200px';
	control3.style.width = "20%";
	var innerDiv = document.createElement('div');
	innerDiv.innerHTML = "<p3>Add &nbsp;Location</p3>";
	// this is weird. textalign will only work like this if i use set attribute,
	// but background color doesn't work. hmmmmm...
	innerDiv.setAttribute("style", "text-align:center;");
	innerDiv.style.backgroundColor = 'rgba(255,255,255,0.75)';
	innerDiv.appendChild(foodTypeInput);
	innerDiv.appendChild(descriptionInput);
	innerDiv.appendChild(healthInput);
	control3.appendChild(innerDiv);
	map.controls[google.maps.ControlPosition.TOP_CENTER].push(control3);
}

function httpPost(url, position) {
	var xmlHttp = null;
	xmlHttp = new XMLHttpRequest();
	xmlHttp.open("GET", url, false);
	xmlHttp.send(null);
	placeMarker(position.lat(), position.lng(), xmlHttp.responseText, true,
			"red-shadow.png");
}