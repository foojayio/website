---
title: "Modeling One-to-Many Relationships in Java with MongoDB"
date: "2026-03-26T15:50:30+00:00"
lastmod: "2026-03-26T15:50:32+00:00"
description: "This tutorial walks you through both approaches — embedded documents and references — using plain Java POJOs and the MongoDB Java Sync Driver. You'll build a small blogging application, see the resulting document structures, and learn when each pattern shines (and when it doesn't). Along the way, we'll also introduce a hybrid strategy known as the Subset Pattern that combines the best of both worlds."
authors:
  - "arthur_rib"
image: "Screenshot-2026-03-10-at-2.22.35-PM.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
related_posts:
  - "abstracting-data-access-in-java-with-the-dao-pattern"
  - "agents-meet-databases-the-future-of-agentic-architectures"
  - "atlas-online-archive-efficiently-manage-the-data-lifecycle"
  - "atlas-searching-with-the-java-driver"
frozen: false
---

In a relational database, modeling a one-to-many relationship is straightforward: you create two tables and connect them with a foreign key. When you need the data together, you write a JOIN. In MongoDB, you have a choice, and that choice has a direct impact on your application's performance, scalability, and maintainability.

Consider a common scenario: a BlogPost that has many Comment objects. In Java, this is a natural List\<Comment\> field on the post. But when it comes time to persist that relationship in MongoDB, you need to decide *how* to store it. Should the comments live inside the blog post document? Or should they sit in their own collection, connected by references?

This tutorial walks you through both approaches — **embedded documents** and **references** — using plain Java POJOs and the MongoDB Java Sync Driver. You'll build a small blogging application, see the resulting document structures, and learn when each pattern shines (and when it doesn't). Along the way, we'll also introduce a hybrid strategy known as the **Subset Pattern** that combines the best of both worlds.

## **What You'll Learn**

* What a one-to-many relationship is and how it maps from Java objects to MongoDB documents.
* When to embed documents vs. when to use references, and the trade-offs of each.
* How to model both patterns in Java using the MongoDB Java Sync Driver and POJOs.
* How to query and update each pattern effectively.
* Best practices for avoiding common schema design pitfalls.

## **Prerequisites**

To follow along, you'll need:

* **Java 11+** installed.
* **Maven** for dependency management.
* A **MongoDB Atlas** cluster (the [free tier](https://www.mongodb.com/cloud/atlas/register/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=many-mongodb-foojay&utm_term=hugh.murray) works perfectly) or a local MongoDB instance.
* Basic familiarity with Java and object-oriented programming.

The full source code for this tutorial is available on [GitHub](https://github.com/arthurmr96/mongodb-java-modeling-relationships). The appName for this repo is devrel-tutorial-java-driver-foojay

### **Project Setup**

Create a Maven project with the following dependencies in your pom.xml:

```
<dependencies>
    <!-- MongoDB Java Sync Driver -->
    <dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver-sync</artifactId>
        <version>5.3.1</version>
    </dependency>
    <!-- dotenv: loads MONGODB_URI from .env file -->
    <dependency>
        <groupId>io.github.cdimascio</groupId>
        <artifactId>dotenv-java</artifactId>
        <version>3.0.0</version>
    </dependency>
    <!-- Logging -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>2.0.13</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.5.6</version>
    </dependency>
</dependencies>
```

Create a .env file at the project root with your MongoDB connection string:

```
MONGODB_URI=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/?retryWrites=true&w=majority
```

### **Configuring the MongoClient with POJO Support**

Before we dive into the relationship patterns, we need a MongoClient configured with the PojoCodecProvider. This tells the driver how to automatically map Java objects to BSON documents and vice versa — no manual serialization required.

```
package com.example.mongodb.relationships.config;
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
    /**
     * DevRel tracking name — identifies traffic from this tutorial on foojay.io.
     * Format: devrel-{medium}-{primary}-{secondary}-{platform}
     */

    private static final String APP_NAME = "devrel-tutorial-java-driver-foojay";
    private MongoConfig() {
        // utility class
    }

    public static MongoClient createClient() {
        String mongoUri = loadMongoUri();
        CodecRegistry pojoCodecRegistry = fromRegistries(
                MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build())
        );
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .applicationName(APP_NAME)
                .codecRegistry(pojoCodecRegistry)
                .build();
        return MongoClients.create(settings);
    }

    private static String loadMongoUri() {
        // Try system environment variable first (e.g., CI/CD pipelines)
        String uri = System.getenv("MONGODB_URI");
        if (uri != null && !uri.isBlank()) {
            return uri;
        }

        // Fall back to .env file for local development
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        uri = dotenv.get("MONGODB_URI");
        if (uri == null || uri.isBlank()) {
            throw new IllegalStateException(
                    "MONGODB_URI is not set. Please define it as an environment variable " +
                    "or in a .env file at the project root. " +
                    "See .env.example for the expected format."
            );
        }
        return uri;
    }
}
```

The key line here is PojoCodecProvider.builder().automatic(true).build(). Setting automatic(true) tells the driver to handle any POJO it encounters, not just ones you register explicitly. This is what makes the entire POJO-to-BSON mapping work seamlessly throughout the examples that follow.

## **What Is a One-to-Many Relationship in Java?**

In object-oriented terms, a one-to-many relationship means that one object contains or is associated with a collection of other objects. A BlogPost has many Comment objects. In Java, this is typically expressed as a List:

```
public class BlogPost {
    private String title;
    private List<Comment> comments;
}
```

This is intuitive and familiar. But how does this translate to a document database? In MongoDB, a document is a rich, hierarchical data structure — similar to a JSON object. Unlike relational tables, a single MongoDB document can hold nested objects and arrays. That flexibility gives you options that don't exist in the relational world.

The core question becomes: should those Comment objects live *inside* the BlogPost document, or should they live in a separate collection with a pointer back to the post?

## **How Does MongoDB Store Documents Differently Than a Relational Database?**

In a relational database, data is normalized into tables. A blog_posts table and a comments table are connected by a post_id foreign key. To read a post with its comments, you write a JOIN query. The database enforces referential integrity, and the schema is fixed.

MongoDB takes a different approach. Data is stored as flexible BSON documents (binary JSON) that can contain nested objects, arrays, and mixed types. There are no JOINs in the traditional sense — although MongoDB's $lookup aggregation stage can perform similar operations when needed.

This flexibility means MongoDB lets you *choose* your relationship strategy per use case. The two primary strategies are:

* **Embedded Documents** — store the related data directly inside the parent document.
* **References** — store a pointer (usually an ObjectId) to a document in another collection.

Neither is universally "better. The right choice depends on your data access patterns, update frequency, and growth expectations. Let's explore both.

## **Pattern 1: Embedded Documents**

### **When Should You Embed?**

Embedding means storing the related data directly inside the parent document. When you fetch the parent, you get everything in a single read — no second query needed.

Use embedding when:

* The child data is **always read together** with the parent.
* The child array is **bounded in size** (e.g., a handful of comments per post, not millions of log entries).
* You don't need to query or update the child documents **independently** of their parent.

|-------------------------------------|--------------------------------------------|
| **Pros**                            | **Cons**                                   |
| Single read to fetch everything     | Document can grow very large               |
| Atomic updates on parent + children | Hard to query/update children in isolation |
| Simple Java mapping with POJOs      | 16 MB document size limit                  |

### **Modeling Embedded Documents in Java**

Let's model our blogging scenario with embedding. The Comment and User (the post author) are embedded directly inside the BlogPost document.

Here's the embedded Comment — notice it has no _id field, because it doesn't exist as an independent document:

```
package com.example.mongodb.relationships.embedded.model;
import org.bson.codecs.pojo.annotations.BsonProperty;
import java.time.Instant;

public class Comment {
    @BsonProperty("author")
    private String author;
    @BsonProperty("body")
    private String body;
    @BsonProperty("posted_at")
    private Instant postedAt;
    public Comment() {}
    public Comment(String author, String body) {
        this.author = author;
        this.body = body;
        this.postedAt = Instant.now();
    }
    // Getters and setters omitted for brevity
}
```

And the embedded User, representing the post author:

```
package com.example.mongodb.relationships.embedded.model;
import org.bson.codecs.pojo.annotations.BsonProperty;

public class User {
    @BsonProperty("username")
    private String username;
    @BsonProperty("display_name")
    private String displayName;
    @BsonProperty("email")
    private String email;
    @BsonProperty("bio")
    private String bio;
    public User() {}
    public User(String username, String displayName, String email, String bio) {
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.bio = bio;
    }
    // Getters and setters omitted for brevity
}
```

Now, the BlogPost itself. It holds the author as an embedded User and the comments as an embedded List\<Comment\>:

```
package com.example.mongodb.relationships.embedded.model;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BlogPost {
    @BsonId
    private ObjectId id;
    @BsonProperty("title")
    private String title;
    @BsonProperty("content")
    private String content;
    @BsonProperty("author")
    private User author;
    @BsonProperty("published_at")
    private Instant publishedAt;
    @BsonProperty("comments")
    private List<Comment> comments = new ArrayList<>();
    public BlogPost() {}
    public BlogPost(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.publishedAt = Instant.now();
    }
    // Getters and setters omitted for brevity
}
```

The @BsonProperty annotation maps each Java field to its corresponding BSON field name. The @BsonId annotation marks the id field as the document's _id. Every POJO needs a no-argument constructor for the PojoCodecProvider to deserialize documents back into Java objects.

### **Inserting and Querying Embedded Documents**

With our POJOs defined, let's see how to insert a blog post with embedded comments and then read it back:

```
package com.example.mongodb.relationships.embedded;
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
    private static final String DATABASE_NAME = "relationships_demo";
    private static final String COLLECTION_NAME = "blog_posts_embedded";
    private final MongoCollection<BlogPost> collection;
    public EmbeddedExample(MongoClient client) {
        MongoDatabase database = client.getDatabase(DATABASE_NAME);
        this.collection = database.getCollection(COLLECTION_NAME, BlogPost.class);
    }

    public void run() {
        // 1. Build the author as an embedded User object
        User alice = new User("alice", "Alice Johnson", "alice@example.com",
                "Java developer and MongoDB enthusiast.");

        // 2. Build the post with the embedded author and comments
        BlogPost post = new BlogPost(
                "Getting Started with MongoDB",
                "MongoDB is a document database that stores data in flexible, JSON-like documents.",
                alice
        );

        post.setComments(Arrays.asList(
                new Comment("Bob", "Great introduction, very clear!"),
                new Comment("Carol", "I never thought of it that way. Thanks!")
        ));

        // 3. Insert — one document containing author, content, and comments
        collection.insertOne(post);

        // 4. Fetch the post — author and comments come back in the same read
        BlogPost fetched = collection.find(Filters.eq("_id", post.getId())).first();

        if (fetched != null) {
            System.out.println("Title: " + fetched.getTitle());
            System.out.println("Author: " + fetched.getAuthor().getDisplayName());
            fetched.getComments().forEach(c ->
                    System.out.println("  Comment by " + c.getAuthor() + ": " + c.getBody())
            );
        }

        // 5. Add a new comment using $push — atomic update on the parent document
        collection.updateOne(
                Filters.eq("_id", post.getId()),
                Updates.push("comments", new Comment("Dave", "Looking forward to the next post!"))
        );

        // 6. Query: find all posts that have at least one comment from "Bob"
        long count = collection.countDocuments(Filters.eq("comments.author", "Bob"));
        System.out.println("Posts with a comment from Bob: " + count);

        // 7. Query: find posts by the embedded author's username
        long alicePosts = collection.countDocuments(Filters.eq("author.username", "alice"));
        System.out.println("Posts authored by 'alice': " + alicePosts);
    }
}
```

The resulting MongoDB document looks like this:

```
{
  "_id": ObjectId("..."),
  "title": "Getting Started with MongoDB",
  "content": "MongoDB is a document database...",
  "author": {
    "username": "alice",
    "display_name": "Alice Johnson",
    "email": "alice@example.com",
    "bio": "Java developer and MongoDB enthusiast."
  },
  "published_at": ISODate("2025-01-01T00:00:00Z"),
  "comments": [
    { "author": "Bob",   "body": "Great introduction, very clear!",           "posted_at": ISODate("...") },
    { "author": "Carol", "body": "I never thought of it that way. Thanks!",   "posted_at": ISODate("...") }
  ]
}
```

Everything — the post content, the author profile, and all comments — lives in a single document. One find() call returns it all. Adding a new comment is an atomic $push operation on the parent document, with no need to touch a second collection.

You can also query into the embedded data using dot notation. Filters.eq("comments.author", "Bob") finds all posts that have at least one comment authored by Bob, and Filters.eq("author.username", "alice") filters by the embedded author's username.

## **Pattern 2: References**

### **When Should You Use References?**

Referencing means storing a pointer — typically an ObjectId — to a document that lives in a separate collection. To assemble the full object, you need multiple queries.

Use references when:

* Children are **numerous or unbounded** (e.g., thousands of comments on a viral post).
* Children are **queried or updated independently** of their parent.
* **Multiple parents** could reference the same child (e.g., a user who authors many posts and comments).

|---------------------------------------|-----------------------------------------------|
| **Pros**                              | **Cons**                                      |
| Keeps documents small and predictable | Requires multiple reads (no JOINs by default) |
| Children can be queried independently | More complex Java code to assemble objects    |
| Scales to large, growing datasets     | No atomic cross-document updates by default   |

### **Modeling References in Java**

In the referenced approach, users, blog posts, and comments each live in their own collection. The BlogPost stores an ObjectId pointing to the author in the users collection, and a list of ObjectIds pointing to comments in the comments collection.

Here's the User — now an independent document with its own _id:

```
package com.example.mongodb.relationships.referenced.model;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import java.time.Instant;

public class User {
    @BsonId
    private ObjectId id;
    @BsonProperty("username")
    private String username;
    @BsonProperty("display_name")
    private String displayName;
    @BsonProperty("email")
    private String email;
    @BsonProperty("bio")
    private String bio;
    @BsonProperty("joined_at")
    private Instant joinedAt;
    public User() {}
    public User(String username, String displayName, String email, String bio) {
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.bio = bio;
        this.joinedAt = Instant.now();
    }
    // Getters and setters omitted for brevity
}
```

The Comment also becomes an independent document, referencing both the post and the author by ObjectId:

```
package com.example.mongodb.relationships.referenced.model;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import java.time.Instant;

public class Comment {
    @BsonId
    private ObjectId id;
    @BsonProperty("post_id")
    private ObjectId postId;
    @BsonProperty("author_id")
    private ObjectId authorId;
    @BsonProperty("body")
    private String body;
    @BsonProperty("posted_at")
    private Instant postedAt;
    public Comment() {}
    public Comment(ObjectId postId, ObjectId authorId, String body) {
        this.postId = postId;
        this.authorId = authorId;
        this.body = body;
        this.postedAt = Instant.now();
    }
    // Getters and setters omitted for brevity
}
```

And the BlogPost holds references instead of embedded objects:

```
package com.example.mongodb.relationships.referenced.model;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class BlogPost {
    @BsonId
    private ObjectId id;
    @BsonProperty("title")
    private String title;
    @BsonProperty("content")
    private String content;
    @BsonProperty("author_id")
    private ObjectId authorId;
    @BsonProperty("published_at")
    private Instant publishedAt;
    @BsonProperty("comment_ids")
    private List<ObjectId> commentIds = new ArrayList<>();
    public BlogPost() {}
    public BlogPost(String title, String content, ObjectId authorId) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
        this.publishedAt = Instant.now();
    }
    // Getters and setters omitted for brevity
}
```

Notice the difference: instead of private User author and private List\<Comment\> comments, we now have private ObjectId authorId and private List\<ObjectId\> commentIds. The data itself lives elsewhere.

### **Inserting and Querying Referenced Documents**

Working with references requires more steps. You insert documents into separate collections, maintain the reference list, and resolve references with additional queries:

```
package com.example.mongodb.relationships.referenced;
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
    private static final String DATABASE_NAME = "relationships_demo";
    private final MongoCollection<User> usersCollection;
    private final MongoCollection<BlogPost> postsCollection;
    private final MongoCollection<Comment> commentsCollection;
    public ReferencedExample(MongoClient client) {
        MongoDatabase database = client.getDatabase(DATABASE_NAME);
        this.usersCollection = database.getCollection("users", User.class);
        this.postsCollection = database.getCollection("blog_posts_referenced", BlogPost.class);
        this.commentsCollection = database.getCollection("comments", Comment.class);
    }

    public void run() {
        // 1. Insert users into the users collection
        User alice = new User("alice", "Alice Johnson", "alice@example.com",
                "Java developer and MongoDB enthusiast.");
        User bob = new User("bob", "Bob Smith", "bob@example.com",
                "Backend engineer who loves databases.");
        User carol = new User("carol", "Carol Williams", "carol@example.com",
                "Full-stack developer and tech blogger.");
        usersCollection.insertMany(Arrays.asList(alice, bob, carol));

        // 2. Insert the blog post, referencing Alice as the author by ObjectId
        BlogPost post = new BlogPost(
                "Understanding MongoDB Indexes",
                "Indexes support efficient execution of queries in MongoDB.",
                alice.getId()
        );

        postsCollection.insertOne(post);
        ObjectId postId = post.getId();

        // 3. Insert comments referencing the post and their respective authors
        List<Comment> comments = Arrays.asList(
                new Comment(postId, bob.getId(), "The index on _id is automatic, right?"),
                new Comment(postId, carol.getId(), "What about compound indexes? Any tips?")
        );

        commentsCollection.insertMany(comments);
        // Collect the ObjectIds assigned by MongoDB during insert
        List<ObjectId> commentIds = new ArrayList<>();
        comments.forEach(c -> commentIds.add(c.getId()));

        // 4. Update the post to store the reference list
        postsCollection.updateOne(
                Filters.eq("_id", postId),
                Updates.set("comment_ids", commentIds)
        );

        // 5. Multi-step fetch: load post, then resolve author and comments
        BlogPost fetchedPost = postsCollection.find(Filters.eq("_id", postId)).first();

        if (fetchedPost != null) {
            // Resolve the post author from the users collection
            User postAuthor = usersCollection
                    .find(Filters.eq("_id", fetchedPost.getAuthorId()))
                    .first();

            // Resolve comments by their ObjectIds
            List<Comment> resolvedComments = commentsCollection
                    .find(Filters.in("_id", fetchedPost.getCommentIds()))
                    .into(new ArrayList<>());

            // Batch-load all comment authors in a single query
            List<ObjectId> commentAuthorIds = resolvedComments.stream()
                    .map(Comment::getAuthorId)
                    .distinct()
                    .collect(Collectors.toList());

            Map<ObjectId, User> commentAuthors = usersCollection
                    .find(Filters.in("_id", commentAuthorIds))
                    .into(new ArrayList<>())
                    .stream()
                    .collect(Collectors.toMap(User::getId, Function.identity()));

            // Print the assembled object graph
            System.out.println("Title: " + fetchedPost.getTitle());
            if (postAuthor != null) {
                System.out.println("Author: " + postAuthor.getDisplayName());
            }

            resolvedComments.forEach(c -> {
                User commentAuthor = commentAuthors.get(c.getAuthorId());
                String authorName = commentAuthor != null
                        ? commentAuthor.getDisplayName() : "Unknown";
                System.out.println("  Comment by " + authorName + ": " + c.getBody());
            });
        }

        // 6. Query comments independently — key advantage of references
        commentsCollection
                .find(Filters.eq("author_id", bob.getId()))
                .forEach(c -> System.out.println("Bob's comment: " + c.getBody()));

        // 7. Query all posts by a specific author
        long alicePosts = postsCollection
                .countDocuments(Filters.eq("author_id", alice.getId()));
        System.out.println("Posts authored by Alice: " + alicePosts);
    }
}
```

The resulting MongoDB documents span three collections:

```
// users collection
[{
  "_id": ObjectId("uuu"),
  "username": "alice",
  "display_name": "Alice Johnson",
  "email": "alice@example.com",
  "bio": "Java developer and MongoDB enthusiast.",
  "joined_at": ISODate("...")
},
{
  "_id": ObjectId("uuu2"),
  "username": "bob",
  "display_name": "Bob Smith",
  "email": "bob@example.com",
  "bio": "Java developer and MongoDB enthusiast.",
  "joined_at": ISODate("...")
}]

// blog_posts_referenced collection
[{
  "_id": ObjectId("aaa"),
  "title": "Understanding MongoDB Indexes",
  "content": "Indexes support efficient execution of queries...",
  "author_id": ObjectId("uuu"),
  "published_at": ISODate("..."),
  "comment_ids": [ObjectId("bbb"), ObjectId("ccc")]
}]

// comments collection
[{
  "_id": ObjectId("bbb"),
  "post_id": ObjectId("aaa"),
  "author_id": ObjectId("uuu2"),
  "body": "The index on _id is automatic, right?",
  "posted_at": ISODate("...")
}]
```

The trade-off is visible in the code. Assembling the full object graph requires fetching the post, then the author, then the comments, and then the comment authors. That's multiple round-trips. However, the Filters.in() operator lets us batch-load related documents efficiently — notice how we collect all unique commentAuthorIds and resolve them in a single query rather than one query per comment.

A key advantage shows up in step 6: you can query the comments collection directly. Finding all comments by a specific user, or the most recent comments across all posts, is a simple query — no need to scan through embedded arrays in every blog post document.

**Note:** For scenarios where you'd rather resolve references on the server side, MongoDB's [$lookup](https://www.mongodb.com/docs/manual/reference/operator/aggregation/lookup/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=many-mongodb-foojay&utm_term=hugh.murray) aggregation stage can perform left-outer-join-like operations between collections. This is useful for analytics queries or dashboards, but for most application reads, the multi-step approach shown here gives you more control over what gets loaded and when.

## **Best Practices for Schema Design in MongoDB**

Now that you've seen both patterns in action, here are the principles that should guide your schema design decisions.

### **Design for Your Query Patterns, Not Your Data Structure**

This is the single most important rule in MongoDB schema design. Don't start by drawing an entity-relationship diagram and normalizing it. Instead, ask: *What questions will my application ask most often?* If your app always displays a blog post with its comments, embedding makes those reads fast. If your app has a separate "all comments by user" page, references give you direct access.

### **Avoid Unbounded Arrays**

Embedding works well when the array has a predictable upper bound. A blog post with 5--50 comments? Embedding is fine. A social media post that could accumulate hundreds of thousands of reactions? That array will grow without limit, eventually hitting MongoDB's 16 MB document size limit. Use references when a list can grow indefinitely.

### **Think About Atomicity**

MongoDB guarantees atomic updates at the single-document level. When you embed comments inside a blog post, updating the post and adding a comment is a single atomic operation. With references, updating documents across multiple collections is not atomic by default. If you need atomic updates across parent and children, embedding gives you that guarantee out of the box. For cross-collection atomicity, you'd need to use [multi-document transactions](https://www.mongodb.com/docs/manual/core/transactions/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=many-mongodb-foojay&utm_term=hugh.murray).

### **Consider the Subset Pattern**

What if you need the read performance of an embedding, but your dataset is too large to embed entirely? The **Subset Pattern** offers a middle ground: embed a *subset* of the related data for fast access, while keeping the full dataset in a separate collection.

For our blogging example, you might embed only the three most recent comments inside the post for quick rendering, while storing all comments in a separate comments collection for the "View all comments" page.

Here is a simplified view of how the Subset Pattern looks in Java. First, the snapshot classes — lightweight copies of data optimized for display:

```
public class AuthorSnapshot {
    @BsonId
    private ObjectId id;
    @BsonProperty("username")
    private String username;
    @BsonProperty("display_name")
    private String displayName;
    @BsonProperty("profile_picture_url")
    private String profilePictureUrl;
    public AuthorSnapshot() {}
    public static AuthorSnapshot fromUser(User user) {
        return new AuthorSnapshot(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getProfilePictureUrl()
        );
    }
    // Getters and setters omitted for brevity
}

public class CommentSnapshot {
    @BsonId
    private ObjectId id;
    @BsonProperty("author")
    private String author;
    @BsonProperty("body")
    private String body;
    @BsonProperty("posted_at")
    private Instant postedAt;
    public CommentSnapshot() {}
    public static CommentSnapshot fromComment(Comment comment, String authorDisplayName) {
        return new CommentSnapshot(
                comment.getId(),
                authorDisplayName,
                comment.getBody(),
                comment.getPostedAt()
        );
    }
    // Getters and setters omitted for brevity
}
```

And the BlogPost that combines both:

```
public class BlogPost {
    public static final int LATEST_COMMENTS_LIMIT = 3;
    @BsonId
    private ObjectId id;
    @BsonProperty("title")
    private String title;
    @BsonProperty("content")
    private String content;
    @BsonProperty("author")
    private AuthorSnapshot author;
    @BsonProperty("published_at")
    private Instant publishedAt;
    @BsonProperty("latest_comments")
    private List<CommentSnapshot> latestComments = new ArrayList<>();
    @BsonProperty("comment_count")
    private int commentCount;
    // Constructor, getters, and setters omitted for brevity
}
```

The key maintenance operation occurs when a new comment is added. You insert the full Comment into the comments collection, then atomically update the post using $push with $slice to keep only the most recent entries:

```
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.PushOptions;
import com.mongodb.client.model.Updates;

private void addComment(ObjectId postId, User author, String body) {
    // 1. Insert the canonical comment into the comments collection
    Comment comment = new Comment(postId, author.getId(), body);
    commentsCollection.insertOne(comment);
    // 2. Build the lightweight snapshot for embedding
    CommentSnapshot snapshot = CommentSnapshot.fromComment(comment, author.getDisplayName());
    // 3. Update the post in a single round-trip: $push with $slice caps the
    //    embedded array, and $inc keeps the counter in sync — both field
    //    mutations are atomic within this updateOne call. Note that the
    //    insertOne in step 1 and this updateOne are two separate operations
    //    and are not atomic as a whole.
    postsCollection.updateOne(
            Filters.eq("_id", postId),
            Updates.combine(
                    Updates.pushEach(
                            "latest_comments",
                            Arrays.asList(snapshot),
                            new PushOptions().slice(-BlogPost.LATEST_COMMENTS_LIMIT)
                    ),
                    Updates.inc("comment_count", 1)
            )
    );
}
```

The resulting document gives you the best of both worlds — a single read for the most common view, with the full dataset available in a separate collection when needed:

```
{
  "_id": ObjectId("ppp"),
  "title": "The Subset Pattern in Practice",
  "content": "The Subset Pattern is a schema design strategy...",
  "author": {
    "_id": ObjectId("uuu"),
    "username": "alice",
    "display_name": "Alice Johnson",
    "profile_picture_url": "https://cdn.example.com/avatars/alice.jpg"
  },
  "published_at": ISODate("..."),
  "latest_comments": [
    { "_id": ObjectId("c3"), "author": "Dave Brown",      "body": "This is exactly what I was looking for.",       "posted_at": ISODate("...") },
    { "_id": ObjectId("c4"), "author": "Eve Davis",       "body": "Could you write a follow-up on the Bucket Pattern?", "posted_at": ISODate("...") },
    { "_id": ObjectId("c5"), "author": "Bob Smith",       "body": "I refactored my schema using this — works great!",   "posted_at": ISODate("...") }
  ],
  "comment_count": 5
}
```

The AuthorSnapshot carries the user's _id alongside the display fields, so it serves as both a reference and a read-optimized cache. When the reader navigates to the full author profile, you resolve that _id against the users collection. The comment_count field lets the UI display "View all 5 comments" without a count query.

The trade-off is clear: if a user changes their display name, you need to update the embedded snapshots in every post where they appear. For a blogging platform where profile changes are infrequent compared to post reads, this is usually an excellent trade-off.

### **Keep Documents Under the 16 MB Limit**

This is MongoDB's hard constraint on document size. If your embedded arrays could push a document past this limit, use references. The Subset Pattern is particularly useful here: you get the read performance of embedding for the most common view while the full dataset lives safely in its own collection.

## **Choosing the Right Relationship Model for Your Java App**

The choice between embedded documents and references comes down to your application's access patterns:

**Choose embedding** when the related data is always read together with the parent, the array is bounded in size, and you value read performance and atomic updates.

**Choose references** when the related data is numerous or unbounded, queried or updated independently, or shared across multiple parents. References keep documents small and predictable at the cost of additional queries.

**Choose the Subset Pattern** when you need the read performance of embedding, but your dataset is too large or too volatile to embed entirely. Embed a curated subset for fast access; reference the full dataset for completeness.

The Java POJO model maps cleanly to all three patterns. The PojoCodecProvider handles serialization and deserialization automatically, whether your fields are embedded objects, ObjectId references, or a mix of both. Schema design in MongoDB should always be driven by your application's query patterns — and Java's type system makes it easy to express exactly the document structure you need.

The full working code for all three patterns is available on [GitHub](https://github.com/arthurmr96/mongodb-java-modeling-relationships). To experiment with your own data, sign up for a free [MongoDB Atlas](https://www.mongodb.com/cloud/atlas/register/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=many-mongodb-foojay&utm_term=hugh.murray) cluster, clone the repository, set your connection string in the .env file, and run:

```
mvn compile exec:java
```

## **FAQs**

### **Can I mix embedded and referenced documents in the same MongoDB schema?**

Yes — and often you should. The Subset Pattern is a perfect example: you embed the most recent comments for quick display while storing the full comment history as references in a separate collection. Schema design in MongoDB is flexible by nature, and mixing strategies per relationship is a common and recommended practice.

### **How do I handle one-to-many relationships in Spring Data MongoDB?**

Spring Data MongoDB provides @DBRef and embedded document support out of the box. The schema design patterns covered here — embedded documents, references, and the Subset Pattern — apply regardless of your framework. This tutorial uses the core Java Sync Driver to explain the underlying mechanics, but the concepts translate directly to Spring Data, Quarkus, and Micronaut.

### **Does this work with the MongoDB Java Reactive Streams driver?**

The schema design patterns covered in this tutorial apply universally regardless of which driver or framework you use. MongoDB's official [Java Reactive Streams driver](https://www.mongodb.com/docs/languages/java/reactive-streams-driver/current/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=many-mongodb-foojay&utm_term=hugh.murray) offers the same operations with an asynchronous, non-blocking API. Community integrations like [Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb), [Quarkus MongoDB](https://quarkus.io/guides/mongodb), and [Micronaut MongoDB](https://micronaut-projects.github.io/micronaut-mongodb/latest/guide/) also build on these same underlying concepts while adding framework-specific conveniences.

### **What happens if my embedded array grows too large?**

MongoDB documents have a 16 MB size limit. If your array can grow unboundedly — event logs, chat messages, IoT sensor readings — you should use references instead of embedding. The Subset Pattern offers a middle ground if you still want fast reads for a recent slice of the data.

### **Is there a performance difference between embedded and referenced documents?**

Yes. Embedded documents are fetched in a single read operation, making them faster for read-heavy use cases where the child data is always needed alongside the parent. References require at least two reads, adding latency — but they keep documents smaller and more efficient to update individually.

### **Do I need to manage referential integrity manually with MongoDB references?**

Yes. Unlike SQL foreign keys, MongoDB does not enforce referential integrity on ObjectId references. Your application code — typically your Java service layer — is responsible for keeping references consistent. This means handling cascading deletes, orphaned references, and ensuring that IDs point to existing documents is up to you.
