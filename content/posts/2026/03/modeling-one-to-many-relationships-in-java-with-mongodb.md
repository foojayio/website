---
title: "Modeling One-to-Many Relationships in Java with MongoDB"
slug: "modeling-one-to-many-relationships-in-java-with-mongodb"
date: "2026-03-26T15:50:30+00:00"
lastmod: "2026-03-26T15:50:32+00:00"
description: "This tutorial walks you through both approaches — embedded documents and references — using plain Java POJOs and the MongoDB Java Sync Driver. You'll build a small blogging application, see the resulting document structures, and learn when each pattern shines (and when it doesn't). Along the way, we'll also introduce a hybrid strategy known as the Subset Pattern that combines the best of both worlds."
authors:
  - "arthur_rib"
image: "https://foojay.io/wp-content/uploads/2026/03/Screenshot-2026-03-10-at-2.22.35-PM.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
tags:
related_posts:
  - "abstracting-data-access-in-java-with-the-dao-pattern"
  - "agents-meet-databases-the-future-of-agentic-architectures"
  - "atlas-online-archive-efficiently-manage-the-data-lifecycle"
  - "atlas-searching-with-the-java-driver"
enlighterjs: true
frozen: false
---

In a relational database, modeling a one-to-many relationship is straightforward: you create two tables and connect them with a foreign key. When you need the data together, you write a JOIN. In MongoDB, you have a choice, and that choice has a direct impact on your application's performance, scalability, and maintainability.

Consider a common scenario: a BlogPost that has many Comment objects. In Java, this is a natural List\<Comment\> field on the post. But when it comes time to persist that relationship in MongoDB, you need to decide *how* to store it. Should the comments live inside the blog post document? Or should they sit in their own collection, connected by references?

This tutorial walks you through both approaches --- **embedded documents** and **references** --- using plain Java POJOs and the MongoDB Java Sync Driver. You'll build a small blogging application, see the resulting document structures, and learn when each pattern shines (and when it doesn't). Along the way, we'll also introduce a hybrid strategy known as the **Subset Pattern** that combines the best of both worlds.

**What You'll Learn** {#h2-0-what-you-ll-learn}
-----------------------------------------------

* What a one-to-many relationship is and how it maps from Java objects to MongoDB documents.
* When to embed documents vs. when to use references, and the trade-offs of each.
* How to model both patterns in Java using the MongoDB Java Sync Driver and POJOs.
* How to query and update each pattern effectively.
* Best practices for avoiding common schema design pitfalls.

**Prerequisites** {#h2-1-prerequisites}
---------------------------------------

To follow along, you'll need:

* **Java 11+** installed.
* **Maven** for dependency management.
* A **MongoDB Atlas** cluster (the [free tier](https://www.mongodb.com/cloud/atlas/register/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=many-mongodb-foojay&utm_term=hugh.murray) works perfectly) or a local MongoDB instance.
* Basic familiarity with Java and object-oriented programming.

The full source code for this tutorial is available on [GitHub](https://github.com/arthurmr96/mongodb-java-modeling-relationships). The appName for this repo is devrel-tutorial-java-driver-foojay

### **Project Setup** {#h3-2-project-setup}

Create a Maven project with the following dependencies in your pom.xml:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependencies&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;!-- MongoDB Java Sync Driver --&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.mongodb&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;mongodb-driver-sync&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;5.3.1&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;!-- dotenv: loads MONGODB_URI from .env file --&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;io.github.cdimascio&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;dotenv-java&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;3.0.0&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;!-- Logging --&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.slf4j&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;slf4j-api&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;2.0.13&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;ch.qos.logback&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;logback-classic&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;version&gt;1.5.6&lt;/version&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&lt;/dependencies&gt;</pre>

Create a .env file at the project root with your MongoDB connection string:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">MONGODB_URI=mongodb+srv://&lt;username&gt;:&lt;password&gt;@&lt;cluster&gt;.mongodb.net/?retryWrites=true&amp;w=majority</pre>

### **Configuring the MongoClient with POJO Support** {#h3-3-configuring-the-mongoclient-with-pojo-support}

Before we dive into the relationship patterns, we need a MongoClient configured with the PojoCodecProvider. This tells the driver how to automatically map Java objects to BSON documents and vice versa --- no manual serialization required.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.example.mongodb.relationships.config;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoConfig {
&nbsp;&nbsp;&nbsp;&nbsp;/**
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* DevRel tracking name — identifies traffic from this tutorial on foojay.io.
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;* Format: devrel-{medium}-{primary}-{secondary}-{platform}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;*/

&nbsp;&nbsp;&nbsp;&nbsp;private static final String APP_NAME = "devrel-tutorial-java-driver-foojay";
&nbsp;&nbsp;&nbsp;&nbsp;private MongoConfig() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// utility class
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;public static MongoClient createClient() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String mongoUri = loadMongoUri();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CodecRegistry pojoCodecRegistry = fromRegistries(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MongoClientSettings.getDefaultCodecRegistry(),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;fromProviders(PojoCodecProvider.builder().automatic(true).build())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MongoClientSettings settings = MongoClientSettings.builder()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.applyConnectionString(new ConnectionString(mongoUri))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.applicationName(APP_NAME)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.codecRegistry(pojoCodecRegistry)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.build();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return MongoClients.create(settings);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;private static String loadMongoUri() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Try system environment variable first (e.g., CI/CD pipelines)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String uri = System.getenv("MONGODB_URI");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (uri != null &amp;&amp; !uri.isBlank()) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return uri;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Fall back to .env file for local development
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;uri = dotenv.get("MONGODB_URI");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (uri == null || uri.isBlank()) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;throw new IllegalStateException(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"MONGODB_URI is not set. Please define it as an environment variable " +
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"or in a .env file at the project root. " +
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"See .env.example for the expected format."
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return uri;
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

The key line here is PojoCodecProvider.builder().automatic(true).build(). Setting automatic(true) tells the driver to handle any POJO it encounters, not just ones you register explicitly. This is what makes the entire POJO-to-BSON mapping work seamlessly throughout the examples that follow.

**What Is a One-to-Many Relationship in Java?** {#h2-4-what-is-a-one-to-many-relationship-in-java}
--------------------------------------------------------------------------------------------------

In object-oriented terms, a one-to-many relationship means that one object contains or is associated with a collection of other objects. A BlogPost has many Comment objects. In Java, this is typically expressed as a List:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class BlogPost {
&nbsp;&nbsp;&nbsp;&nbsp;private String title;
&nbsp;&nbsp;&nbsp;&nbsp;private List&lt;Comment&gt; comments;
}</pre>

This is intuitive and familiar. But how does this translate to a document database? In MongoDB, a document is a rich, hierarchical data structure --- similar to a JSON object. Unlike relational tables, a single MongoDB document can hold nested objects and arrays. That flexibility gives you options that don't exist in the relational world.

The core question becomes: should those Comment objects live *inside* the BlogPost document, or should they live in a separate collection with a pointer back to the post?

**How Does MongoDB Store Documents Differently Than a Relational Database?** {#h2-5-how-does-mongodb-store-documents-differently-than-a-relational-database}
------------------------------------------------------------------------------------------------------------------------------------------------------------

In a relational database, data is normalized into tables. A blog_posts table and a comments table are connected by a post_id foreign key. To read a post with its comments, you write a JOIN query. The database enforces referential integrity, and the schema is fixed.

MongoDB takes a different approach. Data is stored as flexible BSON documents (binary JSON) that can contain nested objects, arrays, and mixed types. There are no JOINs in the traditional sense --- although MongoDB's $lookup aggregation stage can perform similar operations when needed.

This flexibility means MongoDB lets you *choose* your relationship strategy per use case. The two primary strategies are:

* **Embedded Documents** --- store the related data directly inside the parent document.
* **References** --- store a pointer (usually an ObjectId) to a document in another collection.

Neither is universally "better. The right choice depends on your data access patterns, update frequency, and growth expectations. Let's explore both.

**Pattern 1: Embedded Documents** {#h2-6-pattern-1-embedded-documents}
----------------------------------------------------------------------

### **When Should You Embed?** {#h3-7-when-should-you-embed}

Embedding means storing the related data directly inside the parent document. When you fetch the parent, you get everything in a single read --- no second query needed.

Use embedding when:

* The child data is **always read together** with the parent.
* The child array is **bounded in size** (e.g., a handful of comments per post, not millions of log entries).
* You don't need to query or update the child documents **independently** of their parent.

|-------------------------------------|--------------------------------------------|
| **Pros**                            | **Cons**                                   |
| Single read to fetch everything     | Document can grow very large               |
| Atomic updates on parent + children | Hard to query/update children in isolation |
| Simple Java mapping with POJOs      | 16 MB document size limit                  |

### **Modeling Embedded Documents in Java** {#h3-8-modeling-embedded-documents-in-java}

Let's model our blogging scenario with embedding. The Comment and User (the post author) are embedded directly inside the BlogPost document.

Here's the embedded Comment --- notice it has no _id field, because it doesn't exist as an independent document:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.example.mongodb.relationships.embedded.model;
import org.bson.codecs.pojo.annotations.BsonProperty;
import java.time.Instant;

public class Comment {
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("author")
&nbsp;&nbsp;&nbsp;&nbsp;private String author;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("body")
&nbsp;&nbsp;&nbsp;&nbsp;private String body;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("posted_at")
&nbsp;&nbsp;&nbsp;&nbsp;private Instant postedAt;
&nbsp;&nbsp;&nbsp;&nbsp;public Comment() {}
&nbsp;&nbsp;&nbsp;&nbsp;public Comment(String author, String body) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.author = author;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.body = body;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.postedAt = Instant.now();
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;// Getters and setters omitted for brevity
}</pre>

And the embedded User, representing the post author:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.example.mongodb.relationships.embedded.model;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class User {
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("username")
&nbsp;&nbsp;&nbsp;&nbsp;private String username;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("display_name")
&nbsp;&nbsp;&nbsp;&nbsp;private String displayName;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("email")
&nbsp;&nbsp;&nbsp;&nbsp;private String email;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("bio")
&nbsp;&nbsp;&nbsp;&nbsp;private String bio;
&nbsp;&nbsp;&nbsp;&nbsp;public User() {}
&nbsp;&nbsp;&nbsp;&nbsp;public User(String username, String displayName, String email, String bio) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.username = username;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.displayName = displayName;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.email = email;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.bio = bio;
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;// Getters and setters omitted for brevity
}</pre>

Now, the BlogPost itself. It holds the author as an embedded User and the comments as an embedded List\<Comment\>:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.example.mongodb.relationships.embedded.model;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BlogPost {
&nbsp;&nbsp;&nbsp;&nbsp;@BsonId
&nbsp;&nbsp;&nbsp;&nbsp;private ObjectId id;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("title")
&nbsp;&nbsp;&nbsp;&nbsp;private String title;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("content")
&nbsp;&nbsp;&nbsp;&nbsp;private String content;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("author")
&nbsp;&nbsp;&nbsp;&nbsp;private User author;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("published_at")
&nbsp;&nbsp;&nbsp;&nbsp;private Instant publishedAt;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("comments")
&nbsp;&nbsp;&nbsp;&nbsp;private List&lt;Comment&gt; comments = new ArrayList&lt;&gt;();
&nbsp;&nbsp;&nbsp;&nbsp;public BlogPost() {}
&nbsp;&nbsp;&nbsp;&nbsp;public BlogPost(String title, String content, User author) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.title = title;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.content = content;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.author = author;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.publishedAt = Instant.now();
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;// Getters and setters omitted for brevity
}</pre>

The @BsonProperty annotation maps each Java field to its corresponding BSON field name. The @BsonId annotation marks the id field as the document's _id. Every POJO needs a no-argument constructor for the PojoCodecProvider to deserialize documents back into Java objects.

### **Inserting and Querying Embedded Documents** {#h3-9-inserting-and-querying-embedded-documents}

With our POJOs defined, let's see how to insert a blog post with embedded comments and then read it back:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.example.mongodb.relationships.embedded;
import com.example.mongodb.relationships.embedded.model.BlogPost;
import com.example.mongodb.relationships.embedded.model.Comment;
import com.example.mongodb.relationships.embedded.model.User;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.Arrays;

public class EmbeddedExample {
&nbsp;&nbsp;&nbsp;&nbsp;private static final String DATABASE_NAME = "relationships_demo";
&nbsp;&nbsp;&nbsp;&nbsp;private static final String COLLECTION_NAME = "blog_posts_embedded";
&nbsp;&nbsp;&nbsp;&nbsp;private final MongoCollection&lt;BlogPost&gt; collection;
&nbsp;&nbsp;&nbsp;&nbsp;public EmbeddedExample(MongoClient client) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MongoDatabase database = client.getDatabase(DATABASE_NAME);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.collection = database.getCollection(COLLECTION_NAME, BlogPost.class);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;public void run() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// 1. Build the author as an embedded User object
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;User alice = new User("alice", "Alice Johnson", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="dabbb6b3b9bf9abfa2bbb7aab6bff4b9b5b7">[email&nbsp;protected]</a>",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"Java developer and MongoDB enthusiast.");

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// 2. Build the post with the embedded author and comments
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;BlogPost post = new BlogPost(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"Getting Started with MongoDB",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"MongoDB is a document database that stores data in flexible, JSON-like documents.",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;alice
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;);

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;post.setComments(Arrays.asList(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Comment("Bob", "Great introduction, very clear!"),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Comment("Carol", "I never thought of it that way. Thanks!")
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;));

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// 3. Insert — one document containing author, content, and comments
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;collection.insertOne(post);

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// 4. Fetch the post — author and comments come back in the same read
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;BlogPost fetched = collection.find(Filters.eq("_id", post.getId())).first();

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (fetched != null) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("Title: " + fetched.getTitle());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("Author: " + fetched.getAuthor().getDisplayName());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;fetched.getComments().forEach(c -&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("&nbsp; Comment by " + c.getAuthor() + ": " + c.getBody())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// 5. Add a new comment using $push — atomic update on the parent document
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;collection.updateOne(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Filters.eq("_id", post.getId()),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Updates.push("comments", new Comment("Dave", "Looking forward to the next post!"))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;);

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// 6. Query: find all posts that have at least one comment from "Bob"
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;long count = collection.countDocuments(Filters.eq("comments.author", "Bob"));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("Posts with a comment from Bob: " + count);

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// 7. Query: find posts by the embedded author's username
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;long alicePosts = collection.countDocuments(Filters.eq("author.username", "alice"));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("Posts authored by 'alice': " + alicePosts);
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

The resulting MongoDB document looks like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;&nbsp;"_id": ObjectId("..."),
&nbsp;&nbsp;"title": "Getting Started with MongoDB",
&nbsp;&nbsp;"content": "MongoDB is a document database...",
&nbsp;&nbsp;"author": {
&nbsp;&nbsp;&nbsp;&nbsp;"username": "alice",
&nbsp;&nbsp;&nbsp;&nbsp;"display_name": "Alice Johnson",
&nbsp;&nbsp;&nbsp;&nbsp;"email": "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="94f5f8fdf7f1d4f1ecf5f9e4f8f1baf7fbf9">[email&nbsp;protected]</a>",
&nbsp;&nbsp;&nbsp;&nbsp;"bio": "Java developer and MongoDB enthusiast."
&nbsp;&nbsp;},
&nbsp;&nbsp;"published_at": ISODate("2025-01-01T00:00:00Z"),
&nbsp;&nbsp;"comments": [
&nbsp;&nbsp;&nbsp;&nbsp;{ "author": "Bob", &nbsp; "body": "Great introduction, very clear!", &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; "posted_at": ISODate("...") },
&nbsp;&nbsp;&nbsp;&nbsp;{ "author": "Carol", "body": "I never thought of it that way. Thanks!", &nbsp; "posted_at": ISODate("...") }
&nbsp;&nbsp;]
}</pre>

Everything --- the post content, the author profile, and all comments --- lives in a single document. One find() call returns it all. Adding a new comment is an atomic $push operation on the parent document, with no need to touch a second collection.

You can also query into the embedded data using dot notation. Filters.eq("comments.author", "Bob") finds all posts that have at least one comment authored by Bob, and Filters.eq("author.username", "alice") filters by the embedded author's username.

**Pattern 2: References** {#h2-10-pattern-2-references}
-------------------------------------------------------

### **When Should You Use References?** {#h3-11-when-should-you-use-references}

Referencing means storing a pointer --- typically an ObjectId --- to a document that lives in a separate collection. To assemble the full object, you need multiple queries.

Use references when:

* Children are **numerous or unbounded** (e.g., thousands of comments on a viral post).
* Children are **queried or updated independently** of their parent.
* **Multiple parents** could reference the same child (e.g., a user who authors many posts and comments).

|---------------------------------------|-----------------------------------------------|
| **Pros**                              | **Cons**                                      |
| Keeps documents small and predictable | Requires multiple reads (no JOINs by default) |
| Children can be queried independently | More complex Java code to assemble objects    |
| Scales to large, growing datasets     | No atomic cross-document updates by default   |

### **Modeling References in Java** {#h3-12-modeling-references-in-java}

In the referenced approach, users, blog posts, and comments each live in their own collection. The BlogPost stores an ObjectId pointing to the author in the users collection, and a list of ObjectIds pointing to comments in the comments collection.

Here's the User --- now an independent document with its own _id:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.example.mongodb.relationships.referenced.model;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import java.time.Instant;

public class User {
&nbsp;&nbsp;&nbsp;&nbsp;@BsonId
&nbsp;&nbsp;&nbsp;&nbsp;private ObjectId id;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("username")
&nbsp;&nbsp;&nbsp;&nbsp;private String username;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("display_name")
&nbsp;&nbsp;&nbsp;&nbsp;private String displayName;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("email")
&nbsp;&nbsp;&nbsp;&nbsp;private String email;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("bio")
&nbsp;&nbsp;&nbsp;&nbsp;private String bio;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("joined_at")
&nbsp;&nbsp;&nbsp;&nbsp;private Instant joinedAt;
&nbsp;&nbsp;&nbsp;&nbsp;public User() {}
&nbsp;&nbsp;&nbsp;&nbsp;public User(String username, String displayName, String email, String bio) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.username = username;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.displayName = displayName;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.email = email;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.bio = bio;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.joinedAt = Instant.now();
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;// Getters and setters omitted for brevity
}</pre>

The Comment also becomes an independent document, referencing both the post and the author by ObjectId:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.example.mongodb.relationships.referenced.model;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import java.time.Instant;

public class Comment {
&nbsp;&nbsp;&nbsp;&nbsp;@BsonId
&nbsp;&nbsp;&nbsp;&nbsp;private ObjectId id;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("post_id")
&nbsp;&nbsp;&nbsp;&nbsp;private ObjectId postId;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("author_id")
&nbsp;&nbsp;&nbsp;&nbsp;private ObjectId authorId;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("body")
&nbsp;&nbsp;&nbsp;&nbsp;private String body;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("posted_at")
&nbsp;&nbsp;&nbsp;&nbsp;private Instant postedAt;
&nbsp;&nbsp;&nbsp;&nbsp;public Comment() {}
&nbsp;&nbsp;&nbsp;&nbsp;public Comment(ObjectId postId, ObjectId authorId, String body) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.postId = postId;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.authorId = authorId;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.body = body;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.postedAt = Instant.now();
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;// Getters and setters omitted for brevity
}</pre>

And the BlogPost holds references instead of embedded objects:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.example.mongodb.relationships.referenced.model;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BlogPost {
&nbsp;&nbsp;&nbsp;&nbsp;@BsonId
&nbsp;&nbsp;&nbsp;&nbsp;private ObjectId id;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("title")
&nbsp;&nbsp;&nbsp;&nbsp;private String title;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("content")
&nbsp;&nbsp;&nbsp;&nbsp;private String content;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("author_id")
&nbsp;&nbsp;&nbsp;&nbsp;private ObjectId authorId;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("published_at")
&nbsp;&nbsp;&nbsp;&nbsp;private Instant publishedAt;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("comment_ids")
&nbsp;&nbsp;&nbsp;&nbsp;private List&lt;ObjectId&gt; commentIds = new ArrayList&lt;&gt;();
&nbsp;&nbsp;&nbsp;&nbsp;public BlogPost() {}
&nbsp;&nbsp;&nbsp;&nbsp;public BlogPost(String title, String content, ObjectId authorId) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.title = title;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.content = content;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.authorId = authorId;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.publishedAt = Instant.now();
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;// Getters and setters omitted for brevity
}</pre>

Notice the difference: instead of private User author and private List\<Comment\> comments, we now have private ObjectId authorId and private List\<ObjectId\> commentIds. The data itself lives elsewhere.

### **Inserting and Querying Referenced Documents** {#h3-13-inserting-and-querying-referenced-documents}

Working with references requires more steps. You insert documents into separate collections, maintain the reference list, and resolve references with additional queries:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package com.example.mongodb.relationships.referenced;
import com.example.mongodb.relationships.referenced.model.BlogPost;
import com.example.mongodb.relationships.referenced.model.Comment;
import com.example.mongodb.relationships.referenced.model.User;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReferencedExample {
&nbsp;&nbsp;&nbsp;&nbsp;private static final String DATABASE_NAME = "relationships_demo";
&nbsp;&nbsp;&nbsp;&nbsp;private final MongoCollection&lt;User&gt; usersCollection;
&nbsp;&nbsp;&nbsp;&nbsp;private final MongoCollection&lt;BlogPost&gt; postsCollection;
&nbsp;&nbsp;&nbsp;&nbsp;private final MongoCollection&lt;Comment&gt; commentsCollection;
&nbsp;&nbsp;&nbsp;&nbsp;public ReferencedExample(MongoClient client) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;MongoDatabase database = client.getDatabase(DATABASE_NAME);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.usersCollection = database.getCollection("users", User.class);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.postsCollection = database.getCollection("blog_posts_referenced", BlogPost.class);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.commentsCollection = database.getCollection("comments", Comment.class);
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;public void run() {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// 1. Insert users into the users collection
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;User alice = new User("alice", "Alice Johnson", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="690805000a0c290c11080419050c470a0604">[email&nbsp;protected]</a>",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"Java developer and MongoDB enthusiast.");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;User bob = new User("bob", "Bob Smith", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="83e1ece1c3e6fbe2eef3efe6ade0ecee">[email&nbsp;protected]</a>",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"Backend engineer who loves databases.");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;User carol = new User("carol", "Carol Williams", "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="593a382b3635193c21383429353c773a3634">[email&nbsp;protected]</a>",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"Full-stack developer and tech blogger.");
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;usersCollection.insertMany(Arrays.asList(alice, bob, carol));

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// 2. Insert the blog post, referencing Alice as the author by ObjectId
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;BlogPost post = new BlogPost(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"Understanding MongoDB Indexes",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"Indexes support efficient execution of queries in MongoDB.",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;alice.getId()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;);

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;postsCollection.insertOne(post);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;ObjectId postId = post.getId();

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// 3. Insert comments referencing the post and their respective authors
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Comment&gt; comments = Arrays.asList(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Comment(postId, bob.getId(), "The index on _id is automatic, right?"),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new Comment(postId, carol.getId(), "What about compound indexes? Any tips?")
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;);

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;commentsCollection.insertMany(comments);
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Collect the ObjectIds assigned by MongoDB during insert
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;ObjectId&gt; commentIds = new ArrayList&lt;&gt;();
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;comments.forEach(c -&gt; commentIds.add(c.getId()));

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// 4. Update the post to store the reference list
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;postsCollection.updateOne(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Filters.eq("_id", postId),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Updates.set("comment_ids", commentIds)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;);

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// 5. Multi-step fetch: load post, then resolve author and comments
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;BlogPost fetchedPost = postsCollection.find(Filters.eq("_id", postId)).first();

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (fetchedPost != null) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Resolve the post author from the users collection
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;User postAuthor = usersCollection
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.find(Filters.eq("_id", fetchedPost.getAuthorId()))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.first();

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Resolve comments by their ObjectIds
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;Comment&gt; resolvedComments = commentsCollection
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.find(Filters.in("_id", fetchedPost.getCommentIds()))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.into(new ArrayList&lt;&gt;());

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Batch-load all comment authors in a single query
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;List&lt;ObjectId&gt; commentAuthorIds = resolvedComments.stream()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.map(Comment::getAuthorId)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.distinct()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.collect(Collectors.toList());

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Map&lt;ObjectId, User&gt; commentAuthors = usersCollection
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.find(Filters.in("_id", commentAuthorIds))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.into(new ArrayList&lt;&gt;())
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.stream()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.collect(Collectors.toMap(User::getId, Function.identity()));

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// Print the assembled object graph
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("Title: " + fetchedPost.getTitle());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;if (postAuthor != null) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("Author: " + postAuthor.getDisplayName());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;resolvedComments.forEach(c -&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;User commentAuthor = commentAuthors.get(c.getAuthorId());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;String authorName = commentAuthor != null
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;? commentAuthor.getDisplayName() : "Unknown";
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("&nbsp; Comment by " + authorName + ": " + c.getBody());
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;});
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// 6. Query comments independently — key advantage of references
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;commentsCollection
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.find(Filters.eq("author_id", bob.getId()))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.forEach(c -&gt; System.out.println("Bob's comment: " + c.getBody()));

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// 7. Query all posts by a specific author
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;long alicePosts = postsCollection
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.countDocuments(Filters.eq("author_id", alice.getId()));
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("Posts authored by Alice: " + alicePosts);
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

The resulting MongoDB documents span three collections:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// users collection
[{
&nbsp;&nbsp;"_id": ObjectId("uuu"),
&nbsp;&nbsp;"username": "alice",
&nbsp;&nbsp;"display_name": "Alice Johnson",
&nbsp;&nbsp;"email": "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="a1c0cdc8c2c4e1c4d9c0ccd1cdc48fc2cecc">[email&nbsp;protected]</a>",
&nbsp;&nbsp;"bio": "Java developer and MongoDB enthusiast.",
&nbsp;&nbsp;"joined_at": ISODate("...")
},
{
&nbsp;&nbsp;"_id": ObjectId("uuu2"),
&nbsp;&nbsp;"username": "bob",
&nbsp;&nbsp;"display_name": "Bob Smith",
&nbsp;&nbsp;"email": "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e3818c81a3869b828e938f86cd808c8e">[email&nbsp;protected]</a>",
&nbsp;&nbsp;"bio": "Java developer and MongoDB enthusiast.",
&nbsp;&nbsp;"joined_at": ISODate("...")
}]

// blog_posts_referenced collection
[{
&nbsp;&nbsp;"_id": ObjectId("aaa"),
&nbsp;&nbsp;"title": "Understanding MongoDB Indexes",
&nbsp;&nbsp;"content": "Indexes support efficient execution of queries...",
&nbsp;&nbsp;"author_id": ObjectId("uuu"),
&nbsp;&nbsp;"published_at": ISODate("..."),
&nbsp;&nbsp;"comment_ids": [ObjectId("bbb"), ObjectId("ccc")]
}]

// comments collection
[{
&nbsp;&nbsp;"_id": ObjectId("bbb"),
&nbsp;&nbsp;"post_id": ObjectId("aaa"),
&nbsp;&nbsp;"author_id": ObjectId("uuu2"),
&nbsp;&nbsp;"body": "The index on _id is automatic, right?",
&nbsp;&nbsp;"posted_at": ISODate("...")
}]</pre>

The trade-off is visible in the code. Assembling the full object graph requires fetching the post, then the author, then the comments, and then the comment authors. That's multiple round-trips. However, the Filters.in() operator lets us batch-load related documents efficiently --- notice how we collect all unique commentAuthorIds and resolve them in a single query rather than one query per comment.

A key advantage shows up in step 6: you can query the comments collection directly. Finding all comments by a specific user, or the most recent comments across all posts, is a simple query --- no need to scan through embedded arrays in every blog post document.

**Note:** For scenarios where you'd rather resolve references on the server side, MongoDB's [$lookup](https://www.mongodb.com/docs/manual/reference/operator/aggregation/lookup/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=many-mongodb-foojay&utm_term=hugh.murray) aggregation stage can perform left-outer-join-like operations between collections. This is useful for analytics queries or dashboards, but for most application reads, the multi-step approach shown here gives you more control over what gets loaded and when.

**Best Practices for Schema Design in MongoDB** {#h2-14-best-practices-for-schema-design-in-mongodb}
----------------------------------------------------------------------------------------------------

Now that you've seen both patterns in action, here are the principles that should guide your schema design decisions.

### **Design for Your Query Patterns, Not Your Data Structure** {#h3-15-design-for-your-query-patterns-not-your-data-structure}

This is the single most important rule in MongoDB schema design. Don't start by drawing an entity-relationship diagram and normalizing it. Instead, ask: *What questions will my application ask most often?* If your app always displays a blog post with its comments, embedding makes those reads fast. If your app has a separate "all comments by user" page, references give you direct access.

### **Avoid Unbounded Arrays** {#h3-16-avoid-unbounded-arrays}

Embedding works well when the array has a predictable upper bound. A blog post with 5--50 comments? Embedding is fine. A social media post that could accumulate hundreds of thousands of reactions? That array will grow without limit, eventually hitting MongoDB's 16 MB document size limit. Use references when a list can grow indefinitely.

### **Think About Atomicity** {#h3-17-think-about-atomicity}

MongoDB guarantees atomic updates at the single-document level. When you embed comments inside a blog post, updating the post and adding a comment is a single atomic operation. With references, updating documents across multiple collections is not atomic by default. If you need atomic updates across parent and children, embedding gives you that guarantee out of the box. For cross-collection atomicity, you'd need to use [multi-document transactions](https://www.mongodb.com/docs/manual/core/transactions/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=many-mongodb-foojay&utm_term=hugh.murray).

### **Consider the Subset Pattern** {#h3-18-consider-the-subset-pattern}

What if you need the read performance of an embedding, but your dataset is too large to embed entirely? The **Subset Pattern** offers a middle ground: embed a *subset* of the related data for fast access, while keeping the full dataset in a separate collection.

For our blogging example, you might embed only the three most recent comments inside the post for quick rendering, while storing all comments in a separate comments collection for the "View all comments" page.

Here is a simplified view of how the Subset Pattern looks in Java. First, the snapshot classes --- lightweight copies of data optimized for display:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class AuthorSnapshot {
&nbsp;&nbsp;&nbsp;&nbsp;@BsonId
&nbsp;&nbsp;&nbsp;&nbsp;private ObjectId id;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("username")
&nbsp;&nbsp;&nbsp;&nbsp;private String username;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("display_name")
&nbsp;&nbsp;&nbsp;&nbsp;private String displayName;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("profile_picture_url")
&nbsp;&nbsp;&nbsp;&nbsp;private String profilePictureUrl;
&nbsp;&nbsp;&nbsp;&nbsp;public AuthorSnapshot() {}
&nbsp;&nbsp;&nbsp;&nbsp;public static AuthorSnapshot fromUser(User user) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new AuthorSnapshot(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;user.getId(),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;user.getUsername(),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;user.getDisplayName(),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;user.getProfilePictureUrl()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;);
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;// Getters and setters omitted for brevity
}

public class CommentSnapshot {
&nbsp;&nbsp;&nbsp;&nbsp;@BsonId
&nbsp;&nbsp;&nbsp;&nbsp;private ObjectId id;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("author")
&nbsp;&nbsp;&nbsp;&nbsp;private String author;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("body")
&nbsp;&nbsp;&nbsp;&nbsp;private String body;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("posted_at")
&nbsp;&nbsp;&nbsp;&nbsp;private Instant postedAt;
&nbsp;&nbsp;&nbsp;&nbsp;public CommentSnapshot() {}
&nbsp;&nbsp;&nbsp;&nbsp;public static CommentSnapshot fromComment(Comment comment, String authorDisplayName) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return new CommentSnapshot(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;comment.getId(),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;authorDisplayName,
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;comment.getBody(),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;comment.getPostedAt()
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;);
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;// Getters and setters omitted for brevity
}</pre>

And the BlogPost that combines both:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class BlogPost {
&nbsp;&nbsp;&nbsp;&nbsp;public static final int LATEST_COMMENTS_LIMIT = 3;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonId
&nbsp;&nbsp;&nbsp;&nbsp;private ObjectId id;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("title")
&nbsp;&nbsp;&nbsp;&nbsp;private String title;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("content")
&nbsp;&nbsp;&nbsp;&nbsp;private String content;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("author")
&nbsp;&nbsp;&nbsp;&nbsp;private AuthorSnapshot author;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("published_at")
&nbsp;&nbsp;&nbsp;&nbsp;private Instant publishedAt;
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("latest_comments")
&nbsp;&nbsp;&nbsp;&nbsp;private List&lt;CommentSnapshot&gt; latestComments = new ArrayList&lt;&gt;();
&nbsp;&nbsp;&nbsp;&nbsp;@BsonProperty("comment_count")
&nbsp;&nbsp;&nbsp;&nbsp;private int commentCount;
&nbsp;&nbsp;&nbsp;&nbsp;// Constructor, getters, and setters omitted for brevity
}</pre>

The key maintenance operation occurs when a new comment is added. You insert the full Comment into the comments collection, then atomically update the post using $push with $slice to keep only the most recent entries:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import com.mongodb.client.model.Filters;
import com.mongodb.client.model.PushOptions;
import com.mongodb.client.model.Updates;

private void addComment(ObjectId postId, User author, String body) {
&nbsp;&nbsp;&nbsp;&nbsp;// 1. Insert the canonical comment into the comments collection
&nbsp;&nbsp;&nbsp;&nbsp;Comment comment = new Comment(postId, author.getId(), body);
&nbsp;&nbsp;&nbsp;&nbsp;commentsCollection.insertOne(comment);
&nbsp;&nbsp;&nbsp;&nbsp;// 2. Build the lightweight snapshot for embedding
&nbsp;&nbsp;&nbsp;&nbsp;CommentSnapshot snapshot = CommentSnapshot.fromComment(comment, author.getDisplayName());
&nbsp;&nbsp;&nbsp;&nbsp;// 3. Update the post in a single round-trip: $push with $slice caps the
&nbsp;&nbsp;&nbsp;&nbsp;//&nbsp; &nbsp; embedded array, and $inc keeps the counter in sync — both field
&nbsp;&nbsp;&nbsp;&nbsp;//&nbsp; &nbsp; mutations are atomic within this updateOne call. Note that the
&nbsp;&nbsp;&nbsp;&nbsp;//&nbsp; &nbsp; insertOne in step 1 and this updateOne are two separate operations
&nbsp;&nbsp;&nbsp;&nbsp;//&nbsp; &nbsp; and are not atomic as a whole.
&nbsp;&nbsp;&nbsp;&nbsp;postsCollection.updateOne(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Filters.eq("_id", postId),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Updates.combine(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Updates.pushEach(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"latest_comments",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Arrays.asList(snapshot),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new PushOptions().slice(-BlogPost.LATEST_COMMENTS_LIMIT)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;),
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Updates.inc("comment_count", 1)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;)
&nbsp;&nbsp;&nbsp;&nbsp;);
}</pre>

The resulting document gives you the best of both worlds --- a single read for the most common view, with the full dataset available in a separate collection when needed:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
&nbsp;&nbsp;"_id": ObjectId("ppp"),
&nbsp;&nbsp;"title": "The Subset Pattern in Practice",
&nbsp;&nbsp;"content": "The Subset Pattern is a schema design strategy...",
&nbsp;&nbsp;"author": {
&nbsp;&nbsp;&nbsp;&nbsp;"_id": ObjectId("uuu"),
&nbsp;&nbsp;&nbsp;&nbsp;"username": "alice",
&nbsp;&nbsp;&nbsp;&nbsp;"display_name": "Alice Johnson",
&nbsp;&nbsp;&nbsp;&nbsp;"profile_picture_url": "https://cdn.example.com/avatars/alice.jpg"
&nbsp;&nbsp;},
&nbsp;&nbsp;"published_at": ISODate("..."),
&nbsp;&nbsp;"latest_comments": [
&nbsp;&nbsp;&nbsp;&nbsp;{ "_id": ObjectId("c3"), "author": "Dave Brown",&nbsp; &nbsp; &nbsp; "body": "This is exactly what I was looking for.", &nbsp; &nbsp; &nbsp; "posted_at": ISODate("...") },
&nbsp;&nbsp;&nbsp;&nbsp;{ "_id": ObjectId("c4"), "author": "Eve Davis", &nbsp; &nbsp; &nbsp; "body": "Could you write a follow-up on the Bucket Pattern?", "posted_at": ISODate("...") },
&nbsp;&nbsp;&nbsp;&nbsp;{ "_id": ObjectId("c5"), "author": "Bob Smith", &nbsp; &nbsp; &nbsp; "body": "I refactored my schema using this — works great!", &nbsp; "posted_at": ISODate("...") }
&nbsp;&nbsp;],
&nbsp;&nbsp;"comment_count": 5
}</pre>

The AuthorSnapshot carries the user's _id alongside the display fields, so it serves as both a reference and a read-optimized cache. When the reader navigates to the full author profile, you resolve that _id against the users collection. The comment_count field lets the UI display "View all 5 comments" without a count query.

The trade-off is clear: if a user changes their display name, you need to update the embedded snapshots in every post where they appear. For a blogging platform where profile changes are infrequent compared to post reads, this is usually an excellent trade-off.

### **Keep Documents Under the 16 MB Limit** {#h3-19-keep-documents-under-the-16-mb-limit}

This is MongoDB's hard constraint on document size. If your embedded arrays could push a document past this limit, use references. The Subset Pattern is particularly useful here: you get the read performance of embedding for the most common view while the full dataset lives safely in its own collection.

**Choosing the Right Relationship Model for Your Java App** {#h2-20-choosing-the-right-relationship-model-for-your-java-app}
----------------------------------------------------------------------------------------------------------------------------

The choice between embedded documents and references comes down to your application's access patterns:

**Choose embedding** when the related data is always read together with the parent, the array is bounded in size, and you value read performance and atomic updates.

**Choose references** when the related data is numerous or unbounded, queried or updated independently, or shared across multiple parents. References keep documents small and predictable at the cost of additional queries.

**Choose the Subset Pattern** when you need the read performance of embedding, but your dataset is too large or too volatile to embed entirely. Embed a curated subset for fast access; reference the full dataset for completeness.

The Java POJO model maps cleanly to all three patterns. The PojoCodecProvider handles serialization and deserialization automatically, whether your fields are embedded objects, ObjectId references, or a mix of both. Schema design in MongoDB should always be driven by your application's query patterns --- and Java's type system makes it easy to express exactly the document structure you need.

The full working code for all three patterns is available on [GitHub](https://github.com/arthurmr96/mongodb-java-modeling-relationships). To experiment with your own data, sign up for a free [MongoDB Atlas](https://www.mongodb.com/cloud/atlas/register/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=many-mongodb-foojay&utm_term=hugh.murray) cluster, clone the repository, set your connection string in the .env file, and run:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mvn compile exec:java</pre>

**FAQs** {#h2-21-faqs}
----------------------

### **Can I mix embedded and referenced documents in the same MongoDB schema?** {#h3-22-can-i-mix-embedded-and-referenced-documents-in-the-same-mongodb-schema}

Yes --- and often you should. The Subset Pattern is a perfect example: you embed the most recent comments for quick display while storing the full comment history as references in a separate collection. Schema design in MongoDB is flexible by nature, and mixing strategies per relationship is a common and recommended practice.

### **How do I handle one-to-many relationships in Spring Data MongoDB?** {#h3-23-how-do-i-handle-one-to-many-relationships-in-spring-data-mongodb}

Spring Data MongoDB provides @DBRef and embedded document support out of the box. The schema design patterns covered here --- embedded documents, references, and the Subset Pattern --- apply regardless of your framework. This tutorial uses the core Java Sync Driver to explain the underlying mechanics, but the concepts translate directly to Spring Data, Quarkus, and Micronaut.

### **Does this work with the MongoDB Java Reactive Streams driver?** {#h3-24-does-this-work-with-the-mongodb-java-reactive-streams-driver}

The schema design patterns covered in this tutorial apply universally regardless of which driver or framework you use. MongoDB's official [Java Reactive Streams driver](https://www.mongodb.com/docs/languages/java/reactive-streams-driver/current/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=many-mongodb-foojay&utm_term=hugh.murray) offers the same operations with an asynchronous, non-blocking API. Community integrations like [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb), [Quarkus MongoDB](https://quarkus.io/guides/mongodb), and [Micronaut MongoDB](https://micronaut-projects.github.io/micronaut-mongodb/latest/guide/) also build on these same underlying concepts while adding framework-specific conveniences.

### **What happens if my embedded array grows too large?** {#h3-25-what-happens-if-my-embedded-array-grows-too-large}

MongoDB documents have a 16 MB size limit. If your array can grow unboundedly --- event logs, chat messages, IoT sensor readings --- you should use references instead of embedding. The Subset Pattern offers a middle ground if you still want fast reads for a recent slice of the data.

### **Is there a performance difference between embedded and referenced documents?** {#h3-26-is-there-a-performance-difference-between-embedded-and-referenced-documents}

Yes. Embedded documents are fetched in a single read operation, making them faster for read-heavy use cases where the child data is always needed alongside the parent. References require at least two reads, adding latency --- but they keep documents smaller and more efficient to update individually.

### **Do I need to manage referential integrity manually with MongoDB references?** {#h3-27-do-i-need-to-manage-referential-integrity-manually-with-mongodb-references}

Yes. Unlike SQL foreign keys, MongoDB does not enforce referential integrity on ObjectId references. Your application code --- typically your Java service layer --- is responsible for keeping references consistent. This means handling cascading deletes, orphaned references, and ensuring that IDs point to existing documents is up to you.
