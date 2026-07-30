---
title: "Native GraphQL API with Neo4j AuraDB on Heroku"
slug: "native-graphql-api-with-neo4j-auradb-on-heroku"
date: "2022-08-08T07:29:38+00:00"
lastmod: "2022-08-08T07:39:08+00:00"
description: "Learn how to write a GraphQL API that uses Neo4j AuraDB as a backend with Quarkus and deploy it as a native image on Heroku."
authors:
  - "michael-simons"
image: "https://foojay.io/wp-content/uploads/2022/08/neo4j-graphql-quarkus-graphql-ui.jpg"
categories:
  - "Databases"
  - "Graph"
  - "Neo4J"
  - "nosql"
tags:
related_posts:
enlighterjs: true
frozen: false
---

In my first article on Foojay, I would like to present one of many possible approaches to create a GraphQL API. I work at [Neo4j](https://neo4j.com/), so it should not be a big suprise that I will use the Graph database with the same name as a backend for the application.

In this post I will cover a couple of things and I'll start with a simple use case. Conceptually we will look at the "Schema-First" vs "Object-First" discussions and why - at least in my opinion - GraphQL can be seen much more like an object mapping concept than a query language itself. You will learn that while Neo4j is a Graph database, it does not have a built-in GraphQL layer. It does however a great query language called ["Cypher"](https://neo4j.com/docs/cypher-manual/current/).

Technically we will pick [Quarkus](https://quarkus.io/), it's official [Neo4j-Extension](https://github.com/quarkiverse/quarkus-neo4j) and the [Cypher-DSL](https://github.com/neo4j-contrib/cypher-dsl). We will translate the incoming GraphQL requests back from the model to Cypher. For defining the model, Quarkus provides [SmallRye GraphQL](https://quarkus.io/guides/smallrye-graphql).

What I don't want to cover is a discussion about REST or GraphQL, exposing a database schema more or less directly or not. Every technology requires some diligence before being put to use. Some approaches fits your use case better than others, that's just normal.

In general, my litmus test usually goes into the direction of whether I can follow along with a framework or architecture to achieve my purpose or do I need to work against it. In the latter case, it's often better to pick something that works better, even if it is only in a personal perception of things.

The use case {#_the_use_case}
-----------------------------

A while back I started a README in this [repository](https://github.com/michael-simons/goodreads) containing a bunch of CS/IT books I liked. It grew to kinda append only database (`all.csv`) in which I kep track of books I buy and read so that I don't end up with duplicates. A book has a title, one or more authors, a state and a type.

I wanted to have a simple, searchable API and the result is online [here](https://neo4j-aura-quarkus-graphql.herokuapp.com/).

"Schema-First" vs "Object-First" {#_schema_first_vs_object_first}
-----------------------------------------------------------------

Any [GraphQL](https://graphql.org) API requires a schema. If you look at the official web page you'll see three steps:

1. Describe your data
2. Ask for what you want
3. Get predictable results

"Schema-First" vs "Object-First" is the question that arises in the first step, a book as described in the use case can be represented either as shown in the following GraphQL listing:

<pre class="EnlighterJSRAW" data-enlighter-language="graphql">type Book {
    id: ID
    state: State
    title: String
    authors: [Person]
}</pre>

or - when using an appropriate tool - as the following Java record:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public record Book(
	Long id,
	String title,
	State state,
	List&lt;Person&gt; authors
) {}</pre>

On the first look, there's hardly a difference. I personally prefer the Java version as I am familiar in that ecosystem and can find my way around - even without the amazing IDE support we have these days.

Things get a bit more interesting when defining queries:

<pre class="EnlighterJSRAW" data-enlighter-language="graphql">type Query {
  books(authorFilter: String, titleFilter: String, unreadOnly: Boolean = false): [Book]
}</pre>

This has not per-se a direction pendant in the model world.

In GraphQL, the same types are used for querying as for the model. Hence, I think GraphQL is more a modelling language than a query language.

A graph-database and it's issues with GraphQL {#_a_graph_database_and_its_issues_with_graphql}
----------------------------------------------------------------------------------------------

Neo4j is a graph database. It stores related objects as an actual graph, in which relationships between objects are first-class entities. They can have properties the same way as other entities but especially, they can be traversed very efficiently. "Graph database" and "GraphQL" have both a whole word in common, so why does a Graph database not come with GraphQL built in?

I personally don't know about historical reasons, I can only guess. And I would guess based on the same reasoning as above: GraphQL requires a somewhat static model respectively it represents a static model. Neo4j however does not have a static model or a data dictionary so to speak. You can look up a distinct set of labels or relationship types, but each node - even with the same label - can have different properties. That makes it hard to derive a proper model from the database content to describe a static GraphQL interface.

The querying model however fits GraphQL nicely. A book, including it's authors, can be described in Cypher like this:

<pre class="EnlighterJSRAW" data-enlighter-language="cypher">MATCH (b:Book {title: 'Sleeping Beauties'})&lt;-[w:WROTE]-(a)
RETURN b, w, a</pre>

or created like this:

<pre class="EnlighterJSRAW" data-enlighter-language="cypher">MERGE (k1:Person {name: 'Stephen King'})
MERGE (k2:Person {name: 'Owen King'})
MERGE (b:Book {title: 'Sleeping Beauties'})
MERGE (k1) -[:WROTE] -&gt;(b)
MERGE (k2) -[:WROTE] -&gt;(b)
RETURN *</pre>

In case you are interested, Neo4j offers an official solution rooted in the JavaScript ecosystem, called \` @neo4j/graphql\` and documented [here](https://neo4j.com/docs/graphql-manual/current/), OGM included. There's also [neo4j-graphql-java](https://github.com/neo4j-graphql/neo4j-graphql-java), which does the translation from GraphQL models to Cypher on the JVM. Both these tools are "schema first" approaches, hence, I don't want to use either. Those are great tools, but they wouldn't fit my personal interest, so - as said in the beginning - I would rather use something else than working against a solution.

Quarkus {#_quarkus}
-------------------

I chose Quarkus for a couple of reasons for this project:

* The presence of an Object-First based approach for GraphQL
* I am the maintainer of the Neo4j extension, and I want to test it from my perspective
* Development speed and turn-around times plus general positive developer experience
* Deployed as GraalVM native binary it's an excellent fit to run on [Heroku](https://www.heroku.com/home)

You would want to go to [code.quarkus.io](https://code.quarkus.io) or use the Maven archetypes or the IDE integration to get started. Select SmallRye GraphQL extension and the Neo4j client (under "Other"):

![neo4j graphql quarkus code.quarkus.io](/images/posts/2022/08/native-graphql-api-with-neo4j-auradb-on-heroku/neo4j-graphql-quarkus-code.quarkus.io_.jpg)

Eventually, your dependencies should look like this, test-dependencies omitted. You'll find in there the dependencies to Cypher-DSL and another project, [Neo4j-Migrations](https://github.com/michael-simons/neo4j-migrations): Think Flyway, but for Neo4j.

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;dependencies&gt;
    &lt;dependency&gt;
      &lt;groupId&gt;org.neo4j&lt;/groupId&gt;
      &lt;artifactId&gt;neo4j-cypher-dsl&lt;/artifactId&gt;
      &lt;version&gt;2022.6.1&lt;/version&gt;
    &lt;/dependency&gt;
    &lt;dependency&gt;
      &lt;groupId&gt;io.quarkus&lt;/groupId&gt;
      &lt;artifactId&gt;quarkus-jackson&lt;/artifactId&gt;
    &lt;/dependency&gt;
    &lt;dependency&gt;
      &lt;groupId&gt;io.quarkiverse.neo4j&lt;/groupId&gt;
      &lt;artifactId&gt;quarkus-neo4j&lt;/artifactId&gt;
      &lt;version&gt;1.4.0&lt;/version&gt;
    &lt;/dependency&gt;
    &lt;dependency&gt;
      &lt;groupId&gt;eu.michael-simons.neo4j&lt;/groupId&gt;
      &lt;artifactId&gt;neo4j-migrations-quarkus&lt;/artifactId&gt;
      &lt;version&gt;1.9.0&lt;/version&gt;
    &lt;/dependency&gt;
    &lt;dependency&gt;
      &lt;groupId&gt;io.quarkus&lt;/groupId&gt;
      &lt;artifactId&gt;quarkus-smallrye-graphql&lt;/artifactId&gt;
    &lt;/dependency&gt;
    &lt;dependency&gt;
      &lt;groupId&gt;io.quarkus&lt;/groupId&gt;
      &lt;artifactId&gt;quarkus-arc&lt;/artifactId&gt;
    &lt;/dependency&gt;
    &lt;dependency&gt;
      &lt;groupId&gt;io.quarkus&lt;/groupId&gt;
      &lt;artifactId&gt;quarkus-container-image-docker&lt;/artifactId&gt;
    &lt;/dependency&gt;
&lt;/dependencies&gt;</pre>

I have some static configuration in the project, looking like this:

<pre class="EnlighterJSRAW" data-enlighter-language="properties"># Always enables the UI for GraphQL
quarkus.smallrye-graphql.ui.always-include=true
# Allows filtering on query complexity later on
quarkus.smallrye-graphql.events.enabled=true

# Tunes Neo4j connection pool for startup performance
quarkus.neo4j.pool.max-connection-lifetime=8m
quarkus.neo4j.pool.max-connection-pool-size=10

# Either use port from the environment or 8080 if unset
quarkus.http.port=${PORT:8080}

# Populate database during dev and test
%dev.org.neo4j.migrations.locations-to-scan=classpath:neo4j/migrations,classpath:neo4j/example-data
%test.org.neo4j.migrations.locations-to-scan=classpath:neo4j/migrations,classpath:neo4j/example-data</pre>

Before we highlight some things in the project, let's have a look what is included with those dependencies and the bit of configuration. With **Git** , **JDK 17** and a working **Docker** environment on your machine, you can execute the following commands:

<pre class="EnlighterJSRAW" data-enlighter-language="console"># Should print something like java version "17.0.2" 2022-01-18 LTS
java -version
# Clone the project
git clone <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="8aede3fecaede3fee2ffe8a4e9e5e7">[email&nbsp;protected]</a>:michael-simons/neo4j-aura-quarkus-graphql.git
cd neo4j-aura-quarkus-graphql
# Start Quarkus in development mode
./mvnw compile quarkus:dev</pre>

In case you never developed with Quarkus before, Maven will download a chunk of the internet for you and after a while, starting up a Neo4j instance in a Docker container, setup the connection, populate the database for you and greet you like this:

![quarkus dev](/images/posts/2022/08/native-graphql-api-with-neo4j-auradb-on-heroku/quarkus-dev.jpg)

The compile phase in the above command is necessary to trigger the front-end Maven plugin that is configured. It will provide a Vue.js based UI for the application. In the shell above you can now hit \[w\] to open up a browser targeting at the root url: [localhost:8080](http://localhost:8080) looking like this:

![root ui](/images/posts/2022/08/native-graphql-api-with-neo4j-auradb-on-heroku/root-ui.jpg)

Hitting \[d\] however is much more interesting. It will open up the Quarkus developer UI at [localhost:8080/q/dev/](http://localhost:8080/q/dev/):

![dev ui](/images/posts/2022/08/native-graphql-api-with-neo4j-auradb-on-heroku/dev-ui.jpg)

From there you can for example have a look at which migrations have been applied:

![migrations ui](/images/posts/2022/08/native-graphql-api-with-neo4j-auradb-on-heroku/migrations-ui.jpg)

or call the GraphQL UI:

![neo4j graphql quarkus graphql ui](/images/posts/2022/08/native-graphql-api-with-neo4j-auradb-on-heroku/neo4j-graphql-quarkus-graphql-ui.jpg)

Which is exactly what we want. For the rest of this post I'm gonna walk through the most important pieces of implementation. I am not going touch the actual frontend, I leave that task up to someone with more Vue knowledge than I have. Regarding `frontend-maven-plugin`: Many things that [Jonas Hecht](https://twitter.com/jonashackt) describes in his post at [Codecentric](https://blog.codecentric.de/en/2018/04/spring-boot-vuejs/) applies to a Quarkus backend too.

Implementation {#_implementation}
---------------------------------

The whole API my application offers is in a class called `BooksAndMovies`. From a domain perspective, I always wanted to add more content of Neo4j's movie graph but haven't done yet. That class is declared as application scoped `GraphQLApi`:

<pre class="EnlighterJSRAW" data-enlighter-language="java">import graphql.schema.DataFetchingEnvironment;
import io.smallrye.graphql.api.Context;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.graphql.DefaultValue;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import org.neo4j.tips.quarkus.books.Book;
import org.neo4j.tips.quarkus.books.BookService;
import org.neo4j.tips.quarkus.people.Person;

@GraphQLApi
@ApplicationScoped
public class BooksAndMovies {

    private final Context context;

    private final BookService bookService;

    @Inject
    public BooksAndMovies(Context context, BookService bookService) {
        this.context = context;
        this.bookService = bookService;
    }

    @Query("books")
    public CompletableFuture&lt;List&lt;Book&gt;&gt; getBooks(
        @Name("titleFilter") String titleFilter,
        @Name("authorFilter") String authorFilter,
        @Name("unreadOnly") @DefaultValue("false") boolean unreadOnly
    ) {
        var env = context.unwrap(DataFetchingEnvironment.class);
        return bookService.findBooks(
            titleFilter,
            Person.withName(authorFilter),
            unreadOnly, env.getSelectionSet()
        );
    }
}</pre>

Every method on that class annotated with `@Query` will be a query in the GraphQL schema. There is a similar annotation for mutations. As you see the method returns a `CompletableFuture<>`, making it asynchronous. This is important under several aspects: On the API side of things it won't block a thread and it allows for an easy combination of methods, domain objects and fields.

But first, back to the `getBooks` method: It takes in a couple of filters and uses the GraphQL context to unwrap a so-called data fetching environment from which we retrieve what fields are of a book are actually request. This way, we don't over fetch.

The service method `BookService#findBooks` now uses the Neo4j database connectivity and the Cypher-DSL. The Cypher-DSL is used to build an optimized query that will be eventually executed against the connection:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public CompletableFuture&lt;List&lt;Book&gt;&gt; findBooks(
    String titleFilter,
    Person authorFilter,
    boolean unreadOnly,
    DataFetchingFieldSelectionSet selectionSet
) {

    var book = node("Book").named("b");
    var possibleAuthor = node("Person").named("p");
    var author = node("Person").named("a");

    var conditions = createDefaultBookCondition(book, unreadOnly);
    var additionalConditions = createAdditionalConditions(book,
        possibleAuthor, titleFilter, authorFilter);

    PatternElement patternToMatch = book;
    if (additionalConditions != Conditions.noCondition()) {
        patternToMatch = possibleAuthor.relationshipTo(book, "WROTE");
        additionalConditions = additionalConditions
            .and(author.isEqualTo(possibleAuthor));
    }

    var match = match(patternToMatch);

    var returnedExpressions = new ArrayList&lt;Expression&gt;();
    returnedExpressions.add(Functions.id(book).as("id"));
    if (selectionSet.contains("authors") || authorFilter != null) {
        match = match.match(book.relationshipFrom(author, "WROTE"));
        returnedExpressions.add(collect(author).as("authors"));
    }

    Predicate&lt;String&gt; isRequiredField = (String n) -&gt;  "authors".equals(n) || "id".equals(n);
    selectionSet.getImmediateFields().stream().map(SelectedField::getName)
        .distinct()
        .filter(isRequiredField.negate())
        .map(n -&gt; book.property(n).as(n))
        .forEach(returnedExpressions::add);

    var statement = makeExecutable(
        match.where(conditions).and(additionalConditions)
            .returning(returnedExpressions.toArray(Expression[]::new))
            .build()
    );

    return executeReadStatement(statement, Book::of);
}</pre>

There are two scenarios in which we must traverse the `WROTE` relationship: In case of filtering on the authors name and when the author is in the selection set. A builder like the Cypher-DSL makes this possible in a type safe fashion. Fun fact: If you don't insist on doing this manually like me here, the neo4j-graphql-java implementation uses the Cypher-DSL under the hood for the exact same purpose.

The generated query will look very similar to what I have shown earlier in matching a book.

Eventually, the asynchronous session of the Neo4j-Java driver is used to execute the query. You'll find all the helper methods in the project on [GitHub](https://github.com/michael-simons/neo4j-aura-quarkus-graphql).

Having a look at the definition of a `Person`, you'll find this:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public record Person(
    String name,
    Integer born,
    List&lt;Movie&gt; actedIn,
    List&lt;Book&gt; wrote
) {}</pre>

The GraphQL schema however has this:

<pre class="EnlighterJSRAW" data-enlighter-language="graphql">type Person {
  actedIn: [Movie]
  born: Int
  name: ID
  "A short biographie of the person, maybe empty if there is none to be found."
  shortBio: String
  wrote: [Book]
}</pre>

Where does that `shortBio` come from? It is another asynchronous method on the `BooksAndMovies` class:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Description("A short biographie of the person, maybe empty if there is none to be found.")
public CompletionStage&lt;String&gt; shortBio(@Source Person person) {
    return peopleService.getShortBio(person);
}</pre>

It takes in a source argument - the person - and asynchronously gets their biography and adds it to the result. It won't work with the example data yet, since I don't have a Wikipedia entry and that's where the `PeopleService` is looking at right now. I am not recommending doing such things without proper circuit breaker in production, but it is rather simple to build a federated GraphQL API based on the given stack.

Deployment {#_deployment}
-------------------------

We have seen how to run the project in developer mode in which the [dev services](https://quarkus.io/guides/dev-services) will use the amazing [Testcontainers](https://www.testcontainers.org) to spin up a database for you. In production however, I want to have something different and opted for [Neo4j AuraDB](https://neo4j.com/cloud/platform/aura-graph-database/). You can sign up there for an always free account.

The application itself is hosted on Heroku deployed by following the official guide: <https://quarkus.io/guides/deploying-to-heroku>. However, I don't deploy the default container but a container with a native image, build with [GraalVM](https://graalvm.org):

<pre class="EnlighterJSRAW" data-enlighter-language="console">./mvnw clean package\
  -Pnative\
  -Dquarkus.native.container-build=true\
  -Dquarkus.native.builder-image=quay.io/quarkus/ubi-quarkus-native-image:22.2-java17\
  -Dquarkus.docker.dockerfile-native-path=./src/main/docker/Dockerfile.native-distroless\
  -Dquarkus.container-image.build=true\
  -Dquarkus.container-image.group=registry.heroku.com/neo4j-aura-quarkus-graphql\
  -Dquarkus.container-image.name=web\
  -Dquarkus.container-image.tag=latest</pre>

By using a container build, I can delegate the compute intensive task to another machine and also don't need to have all the Graal tooling installed.

Feel free to fork my repository from <https://github.com/michael-simons/neo4j-aura-quarkus-graphql> and play around with the code. I'll happily answer your question on [Github](http://github.com/michael-simons/neo4j-migrations) or [Twitter](https://twitter.com/rotnroll666) and until then, happy hacking.
