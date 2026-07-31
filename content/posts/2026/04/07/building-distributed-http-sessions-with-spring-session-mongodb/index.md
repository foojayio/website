---
title: "Manage HTTP Sessions with Spring Session MongoDB"
slug: "building-distributed-http-sessions-with-spring-session-mongodb"
date: "2026-04-07T15:19:10+00:00"
lastmod: "2026-04-07T15:19:12+00:00"
description: "Spring Session MongoDB is a library that enables Spring applications to store and manage HTTP session data in MongoDB rather than relying on container-specific session storage. In traditional deployments, session state is often tied to a single application instance, which makes scaling across multiple servers difficult. By integrating Spring Session with MongoDB, session data can be persisted beyond application restarts and shared across instances in a cluster, enabling scalable distributed applications with minimal configuration."
authors:
  - "tim-kelly"
image: "Screenshot-2026-03-12-at-10.15.08-AM.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
  - "Spring"
tags:
related_posts:
  - "abstracting-data-access-in-java-with-the-dao-pattern"
  - "agents-meet-databases-the-future-of-agentic-architectures"
  - "atlas-searching-with-the-java-driver"
  - "beyond-keywords-optimizing-vector-search-with-filters-and-caching-part-2"
enlighterjs: true
frozen: false
---

[Spring Session MongoDB](https://www.mongodb.com/docs/drivers/java/sync/current/integrations/spring-session/?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=spring+sessions+mongodb&utm_term=tim.kelly) is a library that enables Spring applications to store and manage HTTP session data in MongoDB rather than relying on container-specific session storage. In traditional deployments, session state is often tied to a single application instance, which makes scaling across multiple servers difficult. By integrating [Spring Session](https://spring.io/projects/spring-session) with MongoDB, session data can be persisted beyond application restarts and shared across instances in a cluster, enabling scalable distributed applications with minimal configuration.

In this tutorial, we will build a small API that manages a user's theme preference (light or dark). The example is intentionally simple because the goal is not to demonstrate business logic, but to clearly observe how HTTP sessions work in practice.

A session is created on the server, linked to a cookie in the client, and then reused across requests so the application can remember state. With Spring Session MongoDB, that session state is persisted in MongoDB instead of being stored in memory inside the application container.

MongoDB works well as a session store because document models map naturally to session objects, [TTL indexes](https://www.mongodb.com/docs/manual/core/index-ttl/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=spring%2Bsessions%2Bmongodb&utm_term=hugh.murray) automatically handle expiration, and the database scales horizontally as application traffic grows.

By the end of the tutorial, you will see:

* How sessions are created
* How cookies link requests to sessions
* How session state is stored in MongoDB
* How the same session can be reused across requests

If you want the full code for this tutorial, check out the [GitHub repository](https://github.com/mongodb-developer/spring-sessions-mongodb-app).

Prerequisites {#h2-0-prerequisites}
-----------------------------------

Before starting, ensure you have the following installed:

* [Java 17+](https://openjdk.org/install/)
* [Maven](https://maven.apache.org/download.cgi)
* [A MongoDB deployment](https://www.mongodb.com/cloud/atlas/register/?utm_campaign=devrel&utm_source=third-part-content&utm_medium=cta&utm_content=spring+sessions+mongodb&utm_term=tim.kelly&utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=spring%2Bsessions%2Bmongodb&utm_term=hugh.murray)

The application expects a MongoDB connection string through the environment variable:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">MONGODB_URI</pre>

For example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">export MONGODB_URI="mongodb+srv://&lt;username&gt;:&lt;password&gt;@cluster.mongodb.net/"</pre>

The application configuration will append the database name automatically.

Project Dependencies {#h2-1-project-dependencies}
-------------------------------------------------

We're going to start with a new Spring application. You can use [Spring Initializr](https://start.spring.io/) and make sure you are using Spring Boot 4.0+ to ensure compatibility with [Spring Session 4.0](https://docs.spring.io/spring-session/reference/whats-new.html) or higher. The Maven configuration for this project is shown below.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependencies&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;spring-boot-starter-web&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;spring-boot-starter-data-mongodb&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.mongodb&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;mongodb-spring-session&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;4.0.0-rc0&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;spring-boot-starter-test&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;scope&gt;test&lt;/scope&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&lt;/dependencies&gt;</pre>

### Spring Web {#h3-2-spring-web}

spring-boot-starter-web provides an embedded web server, and the Spring MVC framework is used for building REST APIs. It includes annotations such as:

* @RestController
* @RequestMapping
* @GetMapping
* @PostMapping

Without this dependency, there would be no HTTP application for sessions to attach to.

### Spring Data MongoDB {#h3-3-spring-data-mongodb}

spring-boot-starter-data-mongodb provides the MongoDB driver integration used by Spring Boot. It manages database connections, configuration, and mapping infrastructure.

Even though our controller code never directly interacts with MongoDB, Spring Session relies on this integration to persist session documents.

### MongoDB Spring Session {#h3-4-mongodb-spring-session}

The most important dependency is:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mongodb-spring-session</pre>

This library replaces the default HTTP session implementation with a MongoDB-backed version.

Instead of storing session state in memory inside the application container, sessions are persisted as documents in MongoDB. This allows multiple application instances to access the same session data.

In a distributed system, this removes the dependency between a user session and a single server instance.

Application Configuration {#h2-5-application-configuration}
-----------------------------------------------------------

Next, we configure the MongoDB connection.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">spring.application.name=devrel-tutorial-java-spring-session-mongodb

spring.mongodb.database=springSessions

spring.mongodb.uri=${MONGODB_URI}&amp;appName=${spring.application.name}</pre>

Three properties are defined here.

1. spring.application.name simply identifies the application and is appended to the MongoDB connection as an appName.
2. spring.mongodb.database specifies the database where session documents will be stored.
3. spring.mongodb.uri pulls the base connection string from the MONGODB_URI environment variable and appends the application name.

**Note:**   

The example above appends appName using \&. This assumes that your MONGODB_URI already includes query parameters, which is common for MongoDB Atlas connection strings, such as:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mongodb+srv://&lt;username&gt;:&lt;password&gt;@cluster.mongodb.net/?retryWrites=true&amp;w=majority</pre>

If your URI already contains options like ?retryWrites=true, you can keep the configuration exactly as written:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">spring.mongodb.uri=${MONGODB_URI}&amp;appName=${spring.application.name}</pre>

However, if your URI does **not** contain a query section, appending \&appName will produce an invalid connection string. In that case, you should append the parameter using ? instead:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">spring.mongodb.uri=${MONGODB_URI}?appName=${spring.application.name}</pre>

In short:

* Use \&appName= if the URI already has query parameters.
* Use ?appName= if the URI does not have one.

Bootstrapping the Application {#h2-6-bootstrapping-the-application}
-------------------------------------------------------------------

The entry point for the application is a standard Spring Boot class.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@SpringBootApplication
public class SpringSessionsMongodbAppApplication {
&nbsp;&nbsp;&nbsp;&nbsp;public static void main(String[] args) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;SpringApplication.run(SpringSessionsMongodbAppApplication.class, args);
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

Nothing special happens here. The important detail is that we are not manually configuring session storage. Spring Boot will automatically wire everything together once the session configuration is enabled.

Enabling MongoDB HTTP Sessions {#h2-7-enabling-mongodb-http-sessions}
---------------------------------------------------------------------

To activate MongoDB-backed sessions, we add a configuration class.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Configuration
@EnableMongoHttpSession
public class SessionConfig {
}</pre>

The @EnableMongoHttpSession annotation instructs Spring to replace the default session management mechanism with the MongoDB-backed implementation provided by Spring Session. This annotation changes the underlying session storage model for the entire application.

Controllers will continue to use the familiar HttpSession API, but session state will now be persisted in MongoDB.

Building the Theme API {#h2-8-building-the-theme-api}
-----------------------------------------------------

The API exposes two endpoints.

POST /theme

GET /theme

The controller implementation is shown below.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@RestController
@RequestMapping("/theme")
public class ThemeController {
&nbsp;&nbsp;&nbsp;&nbsp;@PostMapping
&nbsp;&nbsp;&nbsp;&nbsp;public Map&lt;String, Object&gt; setTheme(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@RequestParam String theme,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;HttpSession session) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;session.setAttribute("theme", theme);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return Map.of(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"message", "Theme set",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"theme", theme,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"sessionId", session.getId()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@GetMapping
&nbsp;&nbsp;&nbsp;&nbsp;public Map&lt;String, Object&gt; getTheme(HttpSession session) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String theme = (String) session.getAttribute("theme");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return Map.of(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"theme", theme,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"sessionId", session.getId()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;);
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

Two important things are happening here. First, the controller accepts an HttpSession object as a method parameter. Spring automatically provides this object for each request.

Second, the controller interacts with the session using the standard API.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">session.setAttribute("theme", theme);</pre>

From the controller's perspective, this behaves exactly like a normal session. However, because Spring Session MongoDB is enabled, the session data is not stored in memory. Instead, it is persisted as a document in MongoDB. The controller does not need to know anything about that implementation detail.

Running the Application {#h2-9-running-the-application}
-------------------------------------------------------

Start the application with:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mvn spring-boot:run</pre>

The API will be available at:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">http://localhost:8080</pre>

Now we can test the session behavior.

Testing Session Behavior with curl {#h2-10-testing-session-behavior-with-curl}
------------------------------------------------------------------------------

Using curl allows us to inspect HTTP headers and cookies directly. First, we create a session and store a theme preference.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">curl -i -c cookies.txt -X POST "http://localhost:8080/theme?theme=light"</pre>

The response should look similar to this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">HTTP/1.1 200
Set-Cookie: SESSION=YjI0MGU5NjctYjJlYS00ZGY1LWFlNjgtOTBhNmE1MWQzMTBj
Content-Type: application/json

{
&nbsp;"sessionId":"b240e967-b2ea-4df5-ae68-90a6a51d310c",
&nbsp;"theme":"light",
&nbsp;"message":"Theme set"
}</pre>

Several things happened here. Because the request did not include a session cookie, Spring created a new session. The theme value was stored as a session attribute, and Spring Session persisted the session in MongoDB. The server then returned a cookie called SESSION. The -c cookies.txt option instructs curl to save that cookie so it can be reused later.

Reusing the Session {#h2-11-reusing-the-session}
------------------------------------------------

Next we send another request using the stored cookie.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">curl -i -b cookies.txt http://localhost:8080/theme</pre>

Example response:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;"theme":"light",
&nbsp;"sessionId":"b240e967-b2ea-4df5-ae68-90a6a51d310c"
}</pre>

The session ID is the same as the previous request. This confirms that the session was successfully resolved using the cookie.

Spring performed the following steps internally:

1. Read the SESSION cookie
2. Extract the session identifier
3. Retrieve the session document from MongoDB
4. Populate the HttpSession object
5. Return the stored attribute to the controller

From the application's perspective, this still looks like normal session usage.

Inspecting the Session in MongoDB {#h2-12-inspecting-the-session-in-mongodb}
----------------------------------------------------------------------------

If you connect to MongoDB and inspect the springSessions database, you will see documents created by Spring Session representing each active HTTP session.

A session document might look similar to this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;&nbsp;"_id": "4321d619-8526-4ca2-8163-32d09b12ee98",
&nbsp;&nbsp;"created": { "$date": "2026-03-12T14:24:11.341Z" },
&nbsp;&nbsp;"accessed": { "$date": "2026-03-12T14:24:15.733Z" },
&nbsp;&nbsp;"interval": "PT30M",
&nbsp;&nbsp;"principal": null,
&nbsp;&nbsp;"expireAt": { "$date": "2026-03-12T14:54:15.733Z" },
&nbsp;&nbsp;"attr": { "$binary": "..."}
}</pre>

Each field captures a different aspect of the session lifecycle.

The _id field is the session identifier. This corresponds to the session ID used internally by Spring when resolving an HttpSession. When a request arrives with a SESSION cookie, Spring extracts the identifier from that cookie and uses it to retrieve the matching session document.

The created timestamp records when the session was first created. This can be useful for understanding how long sessions typically remain active in your application or for auditing session activity.

The accessed field tracks the last time the session was used. Each time a request successfully resolves the session, Spring updates this value. This allows the system to determine whether the session is still active or has become idle.

The interval field defines the session inactivity timeout. In this example, the value PT30M represents a 30-minute timeout. If the session is not accessed within that window, it becomes eligible for expiration.

The expireAt field stores the exact moment when the session should expire. MongoDB typically maintains a TTL index on this field so that expired sessions are automatically removed from the database without any additional cleanup logic. This means session lifecycle management happens automatically at the database level.

The attr field stores the session attributes themselves. In our example, the controller stored the theme preference in the session using:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">session.setAttribute("theme", theme);</pre>

Spring serializes the session attributes and stores them in this field. When the session is loaded again, Spring deserializes the data and restores the attributes into the HttpSession object that your controller interacts with.

Although the example only stores a single theme value, real applications typically store more meaningful session data. Common examples include:

* authenticated user information
* temporary user preferences
* multi-step workflow state
* shopping cart contents
* CSRF tokens
* feature flags or UI state

The key architectural benefit is that this session data is now externalized. Instead of living in the memory of a single application instance, it is stored in MongoDB, where any instance in a cluster can retrieve it.

This is particularly important in load-balanced environments. When a request arrives, it might be routed to any server in the application cluster. Because the session data is stored centrally, the server handling the request can resolve the session using the cookie identifier and reconstruct the same HttpSession state regardless of which instance handled the previous request.

In practice, this means your application can scale horizontally without losing the ability to maintain consistent session state across requests.

Why This Matters {#h2-13-why-this-matters}
------------------------------------------

In a single-node application, storing sessions in memory may appear sufficient. However, as soon as multiple application instances are introduced, this approach breaks down. Imagine a load-balanced system where a user sends the first request to server A. That server stores the session in memory. On the next request, the load balancer routes the user to server B. If sessions are stored locally, server B has no knowledge of that user's session. This leads to inconsistent application behavior. Many systems attempt to solve this with sticky sessions at the load balancer, but this approach reduces resilience and complicates scaling.

Spring Session MongoDB solves this by moving session state into a shared datastore. Now every application instance can resolve the same session using the session identifier stored in the cookie.

Conclusion {#h2-14-conclusion}
------------------------------

Spring Session MongoDB allows Spring applications to externalize HTTP session storage without changing the programming model used by controllers. Developers can continue working with the familiar HttpSession API while the underlying session state is persisted in MongoDB.

In this tutorial, we built a simple API that stores a theme preference in a session, enabled MongoDB-backed sessions with @EnableMongoHttpSession, and verified the behavior using curl.

Although the example is intentionally small, the same architecture supports much larger use cases such as authentication sessions, user preferences, shopping carts, and multi-step workflows.

By storing session state in MongoDB, applications gain the ability to scale horizontally while maintaining consistent session behavior across a cluster.
