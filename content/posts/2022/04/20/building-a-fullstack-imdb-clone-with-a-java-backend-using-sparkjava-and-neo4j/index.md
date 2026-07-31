---
title: "Fullstack IMDB Clone with a Java Backend using SparkJava and Neo4j"
slug: "building-a-fullstack-imdb-clone-with-a-java-backend-using-sparkjava-and-neo4j"
date: "2022-04-20T07:11:02+00:00"
lastmod: "2022-06-01T20:11:24+00:00"
description: "Learn about a brand new Java developer course for Neo4J with Java 17, with lessons learned, code snippets, insights, and more!"
authors:
  - "jennifer-reif"
  - "michael-hunger"
image: "neo4j-graphacademy-developer.png"
categories:
  - "Graph"
  - "Neo4J"
  - "Tutorials"
tags:
related_posts:
  - "visualization-of-the-message-flow-between-business-functions-with-vaadin-and-neo4j"
  - "journeys-in-java-level-1-building-an-empire-of-microservices"
  - "journeys-in-java-level-2-building-an-empire-of-microservices"
  - "gear-up-for-nodes-2024-what-to-know"
enlighterjs: true
frozen: false
---

Our [GraphAcademy](https://graphacademy.neo4j.com) is teaching folks starting out in the graph space the fundamentals of the data model, query language, and graph algorithms.

And for developers, there are [courses for using our drivers](https://graphacademy.neo4j.com/categories/developer/) and building the backend of a fullstack app in JavaScript, Go, Python, Java, and soon .Net.

![OzxHfAx](https://i.imgur.com/OzxHfAx.png)

The application is an IMDB clone based on the [MovieLens recommendation](https://github.com/neo4j-graph-examples/recommendations) dataset augmented with movie and cast data from [themoviedb.org](https://themoviedb.org).

The front-end is written in vue.js and looks pretty slick.

It calls a number of REST API endpoints to serve the different views and functionalities.

![as0CeWJ](https://i.imgur.com/as0CeWJ.png)

Basic functions are:

* registering and authenticating the user and storing their information
* listing genres, movies, people sorted and filtered, and related information
* favoriting and rating movies and returning those lists and recommendations

The app-development course walks through implementing the endpoints step by step, starting with fake fixture data and ends at the full-fledged app, ready to be deployed.

Jennifer Reif and I volunteered to implement the [course for the Java Backend](https://graphacademy.neo4j.com/courses/app-java/) and want to discuss some of the choices made and aspects we noticed while building it.

The [repository with the application code](https://github.com/neo4j-graphacademy/app-java) is standalone and can be use to build and run the app.

It has branches for each course module so you can see the progress/diff and jump ahead to the completed solution in case something went wrong.

The course infrastructure is using [Asciidoctor.js](https://docs.asciidoctor.org/asciidoctor.js/latest/) so that we can use `includes` (via region-tags) directly from our code repository.

So we also get syntax highlighting, admonition bullets, and many more useful things out of the box.

During the interactive course a lot of quizzes check understanding, let you run tests or check the database for successful updates.

The course also automatically integrates with the [Neo4j Sandbox](https://sandbox.neo4j.com) so you can run the test queries and also the app against your personal instance hosting the movies dataset.

![model](https://github.com/neo4j-graph-examples/recommendations/raw/main/documentation/img/model.png)

Setup {#_setup}
---------------

We went with a traditional Java dev setup, installing Java 17 and Apache Maven via sdkman.

As Java 17 brings string blocks and records, we wanted to make use of those niceties.

<pre class="EnlighterJSRAW" data-enlighter-language="shell">sdk install java 17-open
sdk use java 17-open
sdk install maven</pre>

Web Framework - SparkJava {#_web_framework_sparkjava}
-----------------------------------------------------

You might not have heard of [SparkJava](https://sparkjava.com/) which has been around for quite some time and is the Express/Sinatra equivalent minimalistic web framework for Java.

As there will be other courses coming for Spring (Data Neo4j), we wanted to stick with something simple for this course. Quarkus was also an option that we thought of, but then we went with SparkJava for its minimalism.

Also the JavaScript course app was using Express.

So, moving the code over from JavaScript to Java was pretty straightforward, with just a bunch of replacements we had something working within a few minutes.

The minimal hello world example looks like this:

<pre class="EnlighterJSRAW" data-enlighter-language="java">import static spark.Spark.*;

public class HelloWorld {
    public static void main(String[] args) {
        get("/hello", (req, res) -&gt; "Hello World");
    }
}</pre>

Our whole main app that registers the routes, adds error handling, verifies auth, serves public files formatting in JSON (using GSON), and starts the server is not more than 20 lines.

The [docs for SparkJava](https://sparkjava.com/documentation#examples-and-faq) are really short and comprehensive at the same time, everything you need can be found quickly.

<pre class="EnlighterJSRAW" data-enlighter-language="java">package neoflix;

import static spark.Spark.*;

import java.util.*;
import com.google.gson.Gson;
import neoflix.routes.*;
import org.neo4j.driver.*;

public class NeoflixApp {

    public static void main(String[] args) throws Exception {
        AppUtils.loadProperties();
        int port = AppUtils.getServerPort();
        port(port);

        Driver driver = AppUtils.initDriver();
        Gson gson = GsonUtils.gson();

        staticFiles.location("/public");
        String jwtSecret = AppUtils.getJwtSecret();
        before((req, res) -&gt; AppUtils.handleAuthAndSetUser(req, jwtSecret));
        path("/api", () -&gt; {
            path("/movies", new MovieRoutes(driver, gson));
            path("/genres", new GenreRoutes(driver, gson));
            path("/auth", new AuthRoutes(driver, gson, jwtSecret));
            path("/account", new AccountRoutes(driver, gson));
            path("/people", new PeopleRoutes(driver, gson));
        });
        exception(ValidationException.class, (exception, request, response) -&gt; {
            response.status(422);
            var body = Map.of("message",exception.getMessage(), "details", exception.getDetails());
            response.body(gson.toJson(body));
            response.type("application/json");
        });
        System.out.printf("Server listening on http://localhost:%d/%n", port);
    }
}</pre>

Routes - AccountRoutes {#_routes_accountroutes}
-----------------------------------------------

The routes can be grouped by root path and then handled in a simple DSL. Here is the route for getting the favorites list in `AccountRoutes`

<pre class="EnlighterJSRAW" data-enlighter-language="java">get("/favorites", (req, res) -&gt; {
    var params = Params.parse(req, Params.MOVIE_SORT);
    String userId = AppUtils.getUserId(req);
    return favoriteService.all(userId, params);
}, gson::toJson);</pre>

We first parse some params from the request URL then extract the userId from the request attributes and call the FavoriteService to query the database.

Fixtures {#_fixtures}
---------------------

While going through the course, the implementation for using the database is added incrementally.  

To be able to test, run, and interact with the app from the beginning, some static fixtures datasets are used to return responses from the services.

The original Javascript app used JS files to hold the fixtures as JS objects but we wanted something more portable.

So, we converted the fixtures into JSON files and then read them into `List<Map>` structures in the services that used the fixtures.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public static List&lt;Map&gt; loadFixtureList(final String name) {
    var fixture = new InputStreamReader(AppUtils.class.getResourceAsStream("/fixtures/" + name + ".json"));
    return GsonUtils.gson().fromJson(fixture,List.class);
}</pre>

Which can then be used in the service with `this.popular = AppUtils.loadFixtureList("popular");`.

<pre class="EnlighterJSRAW" data-enlighter-language="json">[{
	"actors": [
	  {"name": "Tim Robbins","tmdbId": "0000209"},
	  {"name": "William Sadler","tmdbId": "0006669"},
	  {"name": "Bob Gunton","tmdbId": "0348409"},
	  {"name": "Morgan Freeman","tmdbId": "0000151"}
	],
	"languages": ["English"],
	"plot": "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
	"year": 1994,
	"genres": [{"name": "Drama"},{"name": "Crime"}],
	"directors": [{"name": "Frank Darabont","tmdbId": "0001104"}],
	"imdbRating": 9.3,
	"tmdbId": "0111161",
	"favorite": false,
	"title": "Shawshank Redemption, The",
	"poster": "https://image.tmdb.org/t/p/w440_and_h660_face/5KCVkau1HEl7ZzfPsKAPM0sMiKc.jpg"
}]</pre>

To make it not completely static we used some Java Streams processing fun to at least handle some of the filtering, sorting and pagination even with the fixture data.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public static List&lt;Map&gt; process(
                List&lt;Map&gt; result, Params params) {
    return params == null ? result : result.stream()
        .sorted((m1, m2) -&gt;
            (params.order() == Params.Order.ASC ? 1 : -1) *
                ((Comparable)m1.getOrDefault(params.sort().name(),"")).compareTo(
                        m2.getOrDefault(params.sort().name(),"")
                ))
        .skip(params.skip()).limit(params.limit())
        .toList();
}</pre>

Which is then used in the services, like this:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public List&lt;Map&gt; all(Params params, String userId) {
    // TODO: Open an Session
    // TODO: Execute a query in a new Read Transaction
    // TODO: Get a list of Movies from the Result
    // TODO: Close the session

    return AppUtils.process(popular, params);
}</pre>

Neo4j Driver {#_neo4j_driver}
-----------------------------

The real service implementations use the official [Neo4j Java Driver](https://neo4j.com/developer/java) to query the database.  

We can send parameterized [Cypher queries](https://neo4j.com/developer/cypher) to the server, use parameters and process the results within a retryable transaction function (read- or write-transaction).

Add the driver dependency ([org.neo4j.driver:neo4j-java-driver](https://search.maven.org/artifact/org.neo4j.driver/neo4j-java-driver/4.4.5/jar)) to `pom.xml`.

Then you can create a single driver instance for your application lifetime, and use driver sessions as you go.

Sessions don't hold onto TCP connections but use them from a pool as needed.  

Within a session, you can use read- and write-transactions to do your (units of) work.

We got the connection credentials from `application.properties` which we had read in and set as System properties for convenience and initialized the Driver.

<pre class="EnlighterJSRAW" data-enlighter-language="java">static Driver initDriver() {
    AuthToken auth = AuthTokens.basic(getNeo4jUsername(), getNeo4jPassword());
    Driver driver = GraphDatabase.driver(getNeo4jUri(), auth);
    driver.verifyConnectivity();
    return driver;
}</pre>

Services - FavoriteService {#_services_favoriteservice}
-------------------------------------------------------

The driver is then passed to each service on construction and can be used from there to create sessions and interact with the database.

Here in an example of the `FavoriteService` for listing the favorites of a user, you can see how we use String blocks for the Cypher statement and lambdas for the callback of `readTransaction`

<pre class="EnlighterJSRAW" data-enlighter-language="java">public List&lt;Map&gt; all(String userId, Params params) {
    // Open a new session
    try (var session = this.driver.session()) {

        // Retrieve a list of movies favorited by the user
        var favorites = session.readTransaction(tx -&gt; {
            String query = """
                        MATCH (u:User {userId: $userId})-[r:HAS_FAVORITE]-&gt;(m:Movie)
                        RETURN m {
                            .*,
                            favorite: true
                        } AS movie
                        ORDER BY m.title ASC
                        SKIP $skip
                        LIMIT $limit
                    """;
            var res = tx.run(query, Values.parameters("userId", userId, "skip",
                                        params.skip(), "limit", params.limit()));
            return res.list(row -&gt; row.get("movie").asMap());
        });
        return favorites;
    }
}</pre>

When adding a favorite movie, we use `FavoriteService.add` with a write transaction to create the `FAVORITE` relationship between the user and the movie.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public Map add(String userId, String movieId) {
    // Open a new Session
    try (var session = this.driver.session()) {
        // Create HAS_FAVORITE relationship within a Write Transaction
        var favorite = session.writeTransaction(tx -&gt; {
            String statement = """
                        MATCH (u:User {userId: $userId})
                        MATCH (m:Movie {tmdbId: $movieId})

                        MERGE (u)-[r:HAS_FAVORITE]-&gt;(m)
                                ON CREATE SET r.createdAt = datetime()

                        RETURN m {
                            .*,
                            favorite: true
                        } AS movie
                    """;
            var res = tx.run(statement,
                            Values.parameters("userId", userId, "movieId", movieId));
            return res.single().get("movie").asMap();
        });
        return favorite;
    // Throw an error if the user or movie could not be found
    } catch (NoSuchRecordException e) {
        throw new ValidationException("Could not create favorite movie for user",
            Map.of("movie",movieId, "user",userId));
    }
}</pre>

The `result.single()` method would fail if there is *not exactly one* result with an `NoSuchRecordException`, so we don't need to check for that within the query.

Authentication {#_authentication}
---------------------------------

Our app also provides user management, to allow for personalization.  

That's why we have to:

* register the user
* authenticate the user
* verify auth-token and add user information to the request

For registration and authentication, we store the user directly as a Node in Neo4j and use the `bcrypt` library to hash and compare the hashed passwords with the inputs.

Here is an example from the `authenticate` method of `AuthService`.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public Map authenticate(String email, String plainPassword) {
    // Open a new Session
    try (var session = this.driver.session()) {
        // Find the User node within a Read Transaction
        var user = session.readTransaction(tx -&gt; {
            String statement = "MATCH (u:User {email: $email}) RETURN u";
            var res = tx.run(statement, Values.parameters("email", email));
            return res.single().get("u").asMap();

        });
        // Check password
        if (!AuthUtils.verifyPassword(plainPassword, (String)user.get("password"))) {
            throw new ValidationException("Incorrect password", Map.of("password","Incorrect password"));
        }
        String sub = (String)user.get("userId");
        // compute JWT token signature
        String token = AuthUtils.sign(sub, userToClaims(user), jwtSecret);
        return userWithToken(user, token);
    } catch(NoSuchRecordException e) {
        throw new ValidationException("Incorrect email", Map.of("email","Incorrect email"));
    }
}</pre>

For passing authentication information as [JWT tokens](https://jwt.io) to the browser and back, we use the [Auth0 Java Library](https://github.com/auth0/auth0-java) to generate the token and then also validate it.

This is done with a `before` handler in SparkJava, that on successful validation stores the `sub` which contains the `userId` in a request attribute that the routes then can access, e.g. for personalization or ratings.

<pre class="EnlighterJSRAW" data-enlighter-language="java">static void handleAuthAndSetUser(Request req, String jwtSecret) {
    String token = req.headers("Authorization");
    String bearer = "Bearer ";
    if (token != null &amp;amp;&amp;amp; !token.isBlank() &amp;amp;&amp;amp; token.startsWith(bearer)) {
        token = token.substring(bearer.length());
        String userId = AuthUtils.verify(token, jwtSecret);
        req.attribute("user", userId);
    }
}
// usage in NeoflixApp
before((req, res) -&gt; AppUtils.handleAuthAndSetUser(req, jwtSecret));</pre>

Java 17 Records {#_java_17_records}
-----------------------------------

Originally, we had planned to use Java 17 records throughout the course, but then we ran into two issues.

For one, the [Google Gson](https://github.com/google/gson) library doesn't support (de-)serialization with Records well yet, so we would have had to switch to Jackson instead (should probably have done that).

And, the results from the Neo4j Java Driver were not minimalistically convertible to a record instance as we liked.

As we didn't want to add a lot of noise to the educational code, esp. if you want to keep the single-line lambda closure for the callback, we kept the `toMap()` API that the driver results offer.

<pre class="EnlighterJSRAW" data-enlighter-language="java">var movies = tx.run(query, params)
    .list(row -&gt; row.get("movie")
                    .computeOrDefault(v -&gt;
                        new Movie(v.get("title").asString(),v.get("tmbdId").asString()),
                            v.get("published").asLocalDate())));</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="java">var movies = tx.run(query, params).list(row -&gt; row.get("movie").toMap());</pre>

Testing {#_testing}
-------------------

We used [JUnit 5](https://junit.org/junit5/) for testing, which was a no-brainer.

Because we wanted the same test to work across all branches of the repository no matter if a database connection was available or not, we used `Assume` statements to skip a few of the tests and a conditional on the existence of the driver instance for some cleanup code in the `@BeforeClass/Before` setup methods.

The course uses test execution with `mvn test -Dtest=neoflix.TestName#testMethod` to have the course taker check their progression and correct implementation.

Some of the tests also output results that the user is expected to fill into quizzes during the course.

Conclusion {#_conclusion}
-------------------------

For a real app, we'd could have gone with one of the larger app-frameworks, as there are more needs taken care of.

Also, the repeated use of plain Cypher queries for the operations, plus the mapping and error handling, should have been handled by infrastructure code that we usually would have refactored out.

But, for educational purposes, we left it in each service.

Feel free to check out the [Neo4j Java course](https://graphacademy.neo4j.com/courses/app-java/) and [the Neoflix app](https://github.com/neo4j-graphacademy/app-java)

If you're inclined and interested to rewrite it with any other web framework (Spring Boot, Quarkus, Micronaut, vert.x, Play, etc) please let us know and share the repository, so we can add it as a branch.
