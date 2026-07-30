---
title: "Debugging Gson, Moshi and Jackson JSON Frameworks in Production"
slug: "debugging-gson-moshi-and-jackson-json-frameworks-in-production"
date: "2022-07-08T07:14:01+00:00"
lastmod: "2022-07-08T07:14:03+00:00"
description: "Parsing is a major source of production failures. Some are easy to track but some are insidious. Here's how you can debug them on the fly!"
canonical: "https://lightrun.com/tutorials/debugging-gson-moshi-and-jackson-json-frameworks-in-production/"
authors:
  - "shai-almog"
image: "https://foojay.io/wp-content/uploads/2022/06/Debugging-JSON-API-Requests-1.png"
categories:
  - "Developer Tools"
  - "IntelliJ IDEA"
  - "Tutorials"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Parsing bugs are the gift that keeps on giving in the age of APIs.

We use a service, it works perfectly in debugging, QA, etc.

Then some user input that made its way to the web request returns a result we just can't parse.

Unfortunately, there isn't much we can do at this stage. We need to understand why the failure occurred and how we can workaround it and fix it.

In production, that isn't trivial. We could log all the calls we made and all the objects passed. But this will destroy our performance and send our log storage bill through the roof. It might also put us in violation of privacy laws, since the JSON data might include private user information.

This is a common problem in production since a webservice might change and trigger a serialization failure at runtime. This is especially true in polyglot environments where a Java class mapped using an API like Jackson might fail on the deserialization of a NodeJS object.

For JSON processing, Java has three common libraries:

* Jackson -- oldest and most stable parser API of the three
* Gson -- a more lightweight API from Google
* Moshi -- not as well known in the server (more popular on Android). Written by the author of Gson and should be considered its successor since Gson is no longer actively maintained

Basics of JSON Parsing in Java {#h2-0-basics-of-json-parsing-in-java}
---------------------------------------------------------------------

These libraries are pretty similar in concept. They support serialization of generic types and mapping them to objects. We can convert to/from an arbitrary Java object or a list of objects. For simplicity, I'll focus on Strings since this is a tutorial about debugging.

In the code snippet below, we use standard Java POJO (Plain Old Java Object) for the User and DBObject values. These are trivial objects containing relatively simple fields such as Strings, primitives, byte arrays, etc. In the demo code, I created them with Lombok for convenience, but they would work just fine with getter/setters. This can also work for complex objects with deep nesting, collection types, etc.

With Jackson you can generate a JSON String using code such as this:

<pre class="EnlighterJSRAW" data-enlighter-language="java">String json = mapper.writeValueAsString(user);
</pre>

You can perform object serialization using Jackson:

<pre class="EnlighterJSRAW" data-enlighter-language="java">DBObject object = mapper.readValue(json, DBObject.class);
</pre>

For Gson, we can accomplish the same using:

<pre class="EnlighterJSRAW" data-enlighter-language="java">String json = gson.toJson(user);</pre>

And this is how Gson converts to an object:

<pre class="EnlighterJSRAW" data-enlighter-language="java">DBObject object = gson.fromJson(json, DBObject.class);</pre>

Moshi is a bit more verbose but supports more customization features and type safety:

<pre class="EnlighterJSRAW" data-enlighter-language="java">String json = moshi.adapter(User.class).toJson(user);</pre>

And this is the code to get an object in Moshi:

<pre class="EnlighterJSRAW" data-enlighter-language="java">DBObject object = moshi.adapter(DBObject.class).fromJson(json);</pre>

Notice that Moshi uses type adapters out of the box. The "type adapters" concept isn't unique to Moshi and exists in all of these tools. It lets us perform custom deserialization, specifically "low level" object/JSON mapping. In this case, Moshi puts this API in the forefront to

Demo {#h2-1-demo}
-----------------

To demonstrate debugging, I created a simple JSON database application that lets us save elements as JSON files. You can find the full source code [here](https://github.com/lightrun-platform/lightrun/tree/main/examples/JVM). This demo covers all three JSON parsers and most of their basic features, like type adapters.

We can pick the API to use in the request by submitting an HTTP header and a factory method in the DatabaseWS class picks the right implementation instance. This way, we can process any request as Gson, Jackson or Moshi. The default is Moshi.

This is a standard Spring Boot project you can open in IntelliJ/IDEA. The rest of this post assumes that you have Lightrun installed and running. If not, please install it for free [here](https://lightrun.com/free). You can read about it [here](https://docs.lightrun.com/).

### Creating a Database User {#h3-2-creating-a-database-user}

Before we start, we need to create a new database user. We can do that with the following command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">curl -X PUT -H "Content-Type: application/json" -H "Authorization: 45971c45-4049-48f8-970f-04d47be2defc" -d '{"login":"user", "password":"123456", "givenName":"Shai", "surname":"Almog"}' "http://localhost:8080/addUser"</pre>

Once executed, you should see a file called "\~/myDB/user.user". Notice that \~ represents the home directory of the current user. The file should contain something similar to the following JSON:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">{
  "login": "user",
  "givenName": "Shai",
  "surname": "Almog",
  "email": null,
  "hashedPassword": "$2a$10$DeBGvevs8RiHCIdRqa9fo.ED.6K2UYXXgXYF1.6uLxU1yxmq9c8ZK",
  "token": "7e5a50db-f44a-4177-af48-6fa39c127810",
  "password": null
}</pre>

The file was generated by Moshi. If we wish to use another option for the parser/generator we can add the argument:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">-H "type: jackson" or -H "type: gson" respectively.
</pre>

### Authentication {#h3-3-authentication}

Once we created a user, we can use the authentication API to login to the database using a curl command similar to this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">curl -H "Content-Type: application/json" -H "Authorization: 45971c45-4049-48f8-970f-04d47be2defc" -H "type: jackson" -d '{"login":"shai","password":"123456"}' "http://localhost:8080/auth"</pre>

In this case, I chose to login using the Jackson parser instead of the moshi default. This command returns a token in the response which we can then use to perform operations.

### Adding an Entry {#h3-4-adding-an-entry}

We can add a record to the "database" using a command, such as:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">curl -H "Content-Type: application/json" -H "Authorization: 45971c45-4049-48f8-970f-04d47be2defc" -H "type: jackson" -d '{"coreData":"FBYWFiEs"}' "http://localhost:8080/create"</pre>

Notice that the response from the authentication call we sent above is used in the authorization header.

There are several other features in the database but I'll skip them for now...

### Reading an Entry {#h3-5-reading-an-entry}

Reading elements from the database using deserialization is even easier. It's a simple REST GET method:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">curl -H "Content-Type: application/json" -H "Authorization: 45971c45-4049-48f8-970f-04d47be2defc" -H "type: jackson" "http://localhost:8080/read?id=0ca3edb8-37db-4d97-bbc4-e5222e94db17"</pre>

In this case, I used Jackson again, but it should work for Moshi/Gson just the same. Notice the argument to the read invocation is the ID of the object. We receive the id as a response from the create invocation.

Inspecting and Debugging {#h2-6-inspecting-and-debugging}
---------------------------------------------------------

Debugging APIs such as Gson, Jackson or Moshi is pretty challenging. Unlike typical Java APIs that will throw a checked exception when they fail, these tools typically log parsing errors. Furthermore, many serialization errors or problems can appear as a missing option rather than a visible bug.

A class might have different field names or might be missing an extra field and as a result object fields might include partial information following the deserialization process. Unfortunately, by the time we have the type object, it's a bit too late. The JSON is gone and we can't compare it to the result. We want to inspect the object during serialization.

Before we proceed, you need to make sure you installed Lightrun and have a [basic sense of its usage](https://docs.lightrun.com/). There's a free version you can install from [here](https://lightrun.com/free).

Once we have that, we can start debugging...

### Debugging Serialization in Action {#h3-7-debugging-serialization-in-action}

The code that generates a JSON file is usually much simpler to debug and has fewer issues. It's a simple place to start. We can place a snapshot right in the location where we write the object. E.g. in this case, I used the JSONDatabaseService class and placed a snapshot on the writeString method call.

![](/images/posts/2022/07/debugging-gson-moshi-and-jackson-json-frameworks-in-production/image2-1-700x371.png)

In the stack I can see the JSON data and the original object. That way, we can inspect any discrepancies between the two. Oftentimes, this can relate to default behaviors of an implementation like Jackson or Gson in relation to null objects, date, time, etc. Those are nuances that differ significantly between the various implementations.

### Deserialization {#h3-8-deserialization}

Reading a JSON string or file into a class is the place where most of us fail. Names that are missing are just skipped and a small typo can become an immense problem down the road.

Here is the true value of the ability to inspect the JSON before we convert a string to an object. We can easily do that in this sample by placing a snapshot in the `JacksonDatabaseService` class. Notice that this is Jackson specific. We can do the same for Gson and moshi by placing the snapshot in their respective abstraction classes.

![](/images/posts/2022/07/debugging-gson-moshi-and-jackson-json-frameworks-in-production/image1-700x488.jpg)

Once this is placed, we can try reading an object and we'll see the JSON that's received. In this particular case, grabbing the JSON is trivial from the method parameter. However, this isn't the exception and grabbing this data is possible in most cases.

This is the place to review the field names and various type adapters to understand the resulting object fields.

Final Word {#h2-9-final-word}
-----------------------------

JSON processing in Java is always a challenge whether you use Jackson, Gson or moshi. They all fail in roughly the same ways and oftentimes in production data. Since logging data might expose us to liability and increasing costs, our only option to debug these APIs is via developer observability tools, such as Lightrun.

For most debugging cases, we can just place a snapshot on API access and inspect the resulting node, list, etc. This is usually generic code that helps us convert a type representation such as an object of type foo. To a JSON file that we can send/store, etc.
