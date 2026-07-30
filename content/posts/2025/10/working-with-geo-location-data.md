---
title: "Working with Geo Location Data in MongoDB"
slug: "working-with-geo-location-data"
date: "2025-10-07T14:01:53+00:00"
lastmod: "2025-10-07T14:01:54+00:00"
description: "MongoDB makes it really easy to work with location data (sometimes called Geo Data) by simplifying how to store this type of data and streamlining how you query for it so you can easily create “find nearby” queries, or plot your location data with ease!Let’s start with the basics: modeling your data, indexing it properly, running geo queries, and then displaying results on a map."
authors:
  - "justin-jenkins"
image: "https://foojay.io/wp-content/uploads/2025/10/Screenshot-2025-10-06-at-1.23.35-PM.png"
categories:
  - "Databases"
  - "Mongo"
tags:
related_posts:
enlighterjs: true
frozen: false
---

[MongoDB](https://www.mongodb.com/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=geo-mongodb-foojay&utm_term=tony.kim) makes it really easy to work with location data (sometimes called Geo Data) by simplifying how to store this type of data and streamlining how you query for it so you can easily create "find nearby" queries, or plot your location data with ease!

Let's start with the basics: modeling your data, indexing it properly, running geo queries, and then displaying results on a map.

**Model Your Data with GeoJSON**

The best way to store and interact with location data in MongoDB is to use what is called [GeoJSON](https://www.mongodb.com/docs/manual/reference/geojson/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=geo-mongodb-foojay&utm_term=tony.kim).

GeoJSON is a geospatial data interchange format based on JavaScript Object Notation (JSON). It defines several types of JSON objects and the manner in which they are combined to represent data about geographic features, their properties, and their spatial extents.

This format can store a wide variety of location like data, for our purposes we'll focus on the Point type. To store a Point you'll need a type and coordinates:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;&nbsp;"name": "York Minster",
&nbsp;&nbsp;"category": "history",
&nbsp;&nbsp;"location": {
&nbsp;&nbsp;&nbsp;&nbsp;"type": "Point",
&nbsp;&nbsp;&nbsp;&nbsp;"coordinates": [
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;-1.081,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;53.962
&nbsp;&nbsp;&nbsp;&nbsp;]
&nbsp;&nbsp;}
}</pre>

Note: the order matters, its \[longitude, latitude\] ... this may differ from how some map applications handle coordinate order.

In this example we named our point location and it has a type and an array of coordinates (the name and category are only used for our application, not the geo data).

Creating Geospatial Indexes

To enable queries on our locations we'll need to create a special type of index called a geospatial index:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.places.createIndex({ location: "2dsphere" });</pre>

This will create an index on our location field, however we likely will want to create compound index that also includes the category of our location so let's create that index too:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.places.createIndex(
&nbsp;&nbsp;{ category: 1, location: "2dsphere" }
);</pre>

Now we can target our queries to the category of location first, before checking the distance if we wish.

A 2dsphere type of index will "support queries that interpret geometry on a sphere" vs on a flat service (2d).

Using a 2d index for queries on spherical data can return incorrect results or an error. For example, 2d indexes don't support spherical queries that wrap around the poles.

**Performing Geo Queries**

There are a bunch of geo related queries you can do with MongoDB, we'll mostly focus on [$near](https://www.mongodb.com/docs/atlas/atlas-search/near/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=geo-mongodb-foojay&utm_term=tony.kim) and [$geoWithin](https://www.mongodb.com/docs/manual/reference/operator/query/geowithin/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=geo-mongodb-foojay&utm_term=tony.kim).

The simplest sort of geo related query operator is $near, which will return documents with a location nearest to the provided location (also a Point) which is this case The Tower of London:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.places.find({
&nbsp;&nbsp;location: {
&nbsp;&nbsp;&nbsp;&nbsp;$near: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$geometry: {&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;type: "Point",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;coordinates: [-0.0761, 51.508] // Tower of London
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;},&nbsp;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$maxDistance: 2000 // meters
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;}
});</pre>

This will bring us back two of locations around London within 2,000 meters of The Tower of London:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;&nbsp;_id: ObjectId('68b75623ad2321ea365d00e8'),
&nbsp;&nbsp;name: 'Tower of London',
&nbsp;&nbsp;category: 'history',
&nbsp;&nbsp;location: {
&nbsp;&nbsp;&nbsp;&nbsp;type: 'Point',
&nbsp;&nbsp;&nbsp;&nbsp;coordinates: [-0.0761, 51.5081]
&nbsp;&nbsp;}
},
{
&nbsp;&nbsp;_id: ObjectId('68b75623ad2321ea365d00ec'),
&nbsp;&nbsp;name: "St. Paul's Cathedral",
&nbsp;&nbsp;category: 'history',
&nbsp;&nbsp;location: {
&nbsp;&nbsp;&nbsp;&nbsp;type: 'Point',
&nbsp;&nbsp;&nbsp;&nbsp;coordinates: [-0.0983, 51.5138]
&nbsp;&nbsp;}
}</pre>

Obviously within that close of a range it might not always match a lot of major landmarks, but if you were looking for a restaurant within a 1 mile walk that might be perfect!
![](/images/posts/2025/10/working-with-geo-location-data/Screenshot-2025-10-06-at-1.21.29-PM-1024x458.png)

**Searching within an area**

We can get even more specific however, and find locations within a 5km radius. This will require a little more math but it still quite a simple query using $geoWithin:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.places.find({
&nbsp;&nbsp;location: {
&nbsp;&nbsp;&nbsp;&nbsp;$geoWithin: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$centerSphere: [[-0.0761, 51.508], 5 / 6378.1]
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;}
});</pre>

Let's break this down a little:

* $centerSphere: the shape is a circle on a sphere (a "spherical cap")
* The first value \[-0.0761, 51.508\] is the center in \[lng, lat\] (The Tower of London)
* The second value is the radius in radians. 5 / 6378.1 converts 5 km to radians by dividing by Earth's mean radius in kilometers (6378.1 km).

![](/images/posts/2025/10/working-with-geo-location-data/Screenshot-2025-10-06-at-1.22.21-PM-1024x641.png)

**Search Within a Custom Area**

Lastly we can do something more ad hoc than a circle. Imagine we are taking a walk across the Millennium Bridge from North London into South London, and want to search a very specific area like so:
![](/images/posts/2025/10/working-with-geo-location-data/Screenshot-2025-10-06-at-1.23.35-PM-1024x738.png)

We can do that by setting our type as a polygon and providing each point (roughly below):

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.places.find({
&nbsp;&nbsp;location: {
&nbsp;&nbsp;&nbsp;&nbsp;$geoWithin: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$geometry: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;type: "Polygon",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;coordinates: [[
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[-0.1145, 51.5073],&nbsp; // South Bank by Waterloo Bridge
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[-0.1048, 51.5074],&nbsp; // Bankside/Tate Modern (west of Millennium Bridge)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[-0.0931, 51.5070],&nbsp; // Shakespeare's Globe / Bankside
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[-0.0849, 51.5062],&nbsp; // London Bridge area
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[-0.0738, 51.5050],&nbsp; // Tower Bridge (south side / More London)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[-0.0698, 51.4935],&nbsp; // Bermondsey / Tower Bridge Road south
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[-0.0869, 51.4900],&nbsp; // Elephant &amp; Castle / Walworth
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[-0.1135, 51.4948],&nbsp; // Lambeth
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[-0.1160, 51.5015],&nbsp; // Lambeth North / Westminster Bridge Rd
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;[-0.1145, 51.5073] &nbsp; // "Close" the shape, back to start (South Bank)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;]]
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;}
});</pre>

Now we might get back locations such as Shakespeare's Globe, the Tate Modern, the Imperial War Museum or even the MongoDB London HQ!

**Using Geo Queries in Pipelines**

You can also take advantage of geo queries in Aggregation pipelines using $geoNear:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">db.places.aggregate([
&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;$geoNear: {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;near: { type: "Point", coordinates: [-0.1278, 51.5074] },
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;distanceField: "distanceMeters",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;maxDistance: 2000,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;spherical: true,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;query: { category: "history" }
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;},
&nbsp;&nbsp;{ $limit: 20 },
&nbsp;&nbsp;{ $project: { name: 1, category: 1, location: 1, distanceMeters: 1 } }
]);</pre>

While rather simple, this will get the location and then limit the output, and return just the fields specified but you could have any number of steps in between.

Note: $geoNear must be the first stage in aggregations.

**Mapping Geo Data**

There are a number of ways you can map this data, including some ways build directly into MongoDB products, such as Compass or Charts:

**MongoDB Charts**

If you haven't used MongoDB Charts it is a pretty cool charting platform built directly into MongoDB Atlas (MongoDB's Cloud Service) that you can use to make all sorts of charts and even maps!

Below is an interactive map generated with all the location points from our collection (go ahead, zoom in)!
![](/images/posts/2025/10/working-with-geo-location-data/Screenshot-2025-10-06-at-1.25.45-PM-1024x468.png)

This example is showing all the locations, but you can use a query when you build your map to get just a subset as well as add the map along with other charts to create a Dashboard. As a bit of a preview this is what Charts look like in edit mode.
![](/images/posts/2025/10/working-with-geo-location-data/Screenshot-2025-10-06-at-1.26.06-PM-1024x578.png)

**MongoDB Compass**

You can also view a map like this within MongoDB Compass if you click on the Schema tab and Analyze your documents:
![](/images/posts/2025/10/working-with-geo-location-data/Screenshot-2025-10-06-at-1.26.46-PM-1024x650.png)

Of course you can also use the Google Maps API or maybe Mapbox. What sort of ideas can you think of to use your new geo location knowledge? Have fun!
