---
title: "Understanding BSON: A Beginner’s Guide to MongoDB’s Data Format"
slug: "understanding-bson-a-beginners-guide-to-mongodbs-data-format"
date: "2025-05-22T08:05:06+00:00"
lastmod: "2025-05-23T06:58:58+00:00"
description: "BSON is at the core of how MongoDB stores and transmits data. It extends JSON with additional data."
authors:
  - "tim-kelly"
image: "mongologo.png"
categories:
  - "Databases"
  - "Developer Tools"
  - "Mongo"
tags:
related_posts:
  - "building-a-real-time-ai-fraud-detection-system-with-spring-kafka-and-mongodb"
  - "busting-myths-building-futures-a-conversation-with-cay-horstmann-on-java-and-machine-learning"
  - "creating-cloud-native-java-applications-with-the-12-factor-app-methodology"
  - "different-approaches-to-building-stateful-microservices-in-the-cloud-native-world"
enlighterjs: true
frozen: false
---

**When working with MongoDB, it's easy to think you're dealing with JSON. After all, the queries, documents, and API responses all look like JSON. But MongoDB is not storing JSON. It's storing BSON---a binary format designed for efficient storage and fast traversal.**

BSON (Binary JSON) is more than just a binary version of JSON. It introduces additional data types like ObjectId, Decimal128, and Timestamp, allowing MongoDB to handle more complex data structures and ensure data integrity. While we might rarely interact with raw BSON directly, understanding how MongoDB stores and processes BSON documents can help us write more efficient queries, handle data conversions properly, and debug unexpected behavior.

In this guide, we'll take a look at some of BSON's key concepts, how it maps to Java types, and how we can work with BSON in the [MongoDB Java driver](https://www.mongodb.com/docs/drivers/java/sync/current/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+intro+to+bson&utm_term=tim.kelly). We'll look at creating basic documents, working with nested structures, handling raw BSON, querying with BSON, and finally, mapping Java objects to BSON using POJOs.

By the end, you'll have a clear understanding of how MongoDB leverages BSON under the hood and how you can work with it effectively in Java. Let's get started. If you just want to check out the code, pop over to the [GitHub repository](https://github.com/mongodb-developer/mongodbbsonexample).

## What is BSON?

[BSON](https://www.mongodb.com/resources/languages/bson#:~:text=What%20Does%20BSON%20Stand%20For,like%20dates%20and%20binary%20data.?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+intro+to+bson&utm_term=tim.kelly) stands for Binary JSON. It's a binary-encoded serialization of JSON-like documents. It's everything we like about JSON, just more efficient and type-rich---optimized for speed and storage in MongoDB.

While we interact with MongoDB using JSON-like queries, the documents themselves are stored and transmitted as BSON behind the scenes.

### Why not just JSON?

JSON is pretty great. It's human-readable, flexible, and widely adopted. But it's not ideal for databases. Here's why MongoDB uses BSON instead:

* **Speed**: BSON can be parsed faster than JSON.
* **Rich types**: BSON supports additional data types like Date, Decimal128, and ObjectId.
* **Traversability**: BSON includes length prefixes, making it easier for MongoDB to jump between fields during queries.

Here's an example of what a document {"hello": "world"} would look like in BSON, with length prefixes.

```
{"hello": "world"} →
\x16\x00\x00\x00           // total document size
\x02                       // 0x02 = type String
hello\x00                  // field name
\x06\x00\x00\x00world\x00  // field value
\x00                       // 0x00 = type EOO ('end of object')
```

**Note:** A BSON document has a [size limit of 16MB](https://www.mongodb.com/docs/manual/reference/limits/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+intro+to+bson&utm_term=tim.kelly) on MongoDB.

## BSON vs. JSON

| **Feature** |             **JSON**             |                 **BSON**                 |
|-------------|----------------------------------|------------------------------------------|
| Format      | Text-based                       | Binary                                   |
| Readability | Human-readable                   | Machine-efficient                        |
| Data types  | Limited (no dates, binary, etc.) | Rich and explicit (e.g., ObjectId, Date) |
| Speed       | Slower to parse                  | Faster to parse                          |
| Size        | Often smaller                    | Slightly larger due to type metadata     |

## Common BSON data types (and their Java equivalents)

| **BSON type** |      **Description**      |   **Java equivalent**   |
|---------------|---------------------------|-------------------------|
| String        | UTF-8 string              | String                  |
| Int32 / Int64 | 32-bit / 64-bit integers  | int, long               |
| Double        | 64-bit float              | double                  |
| Boolean       | true / false              | boolean                 |
| Date          | Epoch millis              | java.util.Date          |
| ObjectId      | 12-byte unique identifier | org.bson.types.ObjectId |
| Binary        | Byte array                | byte\[\]                |
| Document      | Embedded object           | org.bson.Document       |
| Array         | List of values            | List\<?\>               |

## BSON and MongoDB internals

* BSON is how MongoDB **stores** documents on disk.
* BSON is how MongoDB **communicates** between client and server.
* Indexes, metadata, and replication all operate on BSON.

Our Java driver handles the BSON encoding and decoding transparently. But if we're building performance-sensitive applications or exploring custom serialization, or we're even just curious, it's worth understanding.

## Setup and project structure

In order to follow along with the code, make sure you have Java 24 and Maven installed. You will also need a MongoDB cluster set up. A [MongoDB M0](https://www.mongodb.com/docs/atlas/tutorial/deploy-free-tier-cluster/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+intro+to+bson&utm_term=tim.kelly) free-forever tier is perfect for this.

1. Project structure:

```
/src
  └── main
      └── java
          └── com
              └── mongodb
                  ├── Main.java
                  ├── User.java
                  └── Address.java
pom.xml
```

2. Maven dependency (pom.xml):

```
<dependencies>
	<dependency>  
	    <groupId>org.mongodb</groupId>  
	    <artifactId>mongodb-driver-sync</artifactId>  
	    <version>5.4.0</version>  
	</dependency>
</dependencies>
```

## BSON data types and document creation

In MongoDB's Java driver, we can interact directly with BSON types using classes like BsonString, BsonInt32, and BsonObjectId. However, we rarely do this. Instead, we work with standard Java types, and MongoDB automatically handles the conversion to BSON.

Here's a look at BSON-specific types:

```
BsonString bsonString = new BsonString("Hello BSON");
BsonInt32 bsonInt32 = new BsonInt32(42);
BsonInt64 bsonInt64 = new BsonInt64(9876543210L);
BsonDecimal128 bsonDecimal = new BsonDecimal128(new Decimal128(new BigDecimal("12345.678")));
BsonDateTime bsonDate = new BsonDateTime(new Date().getTime());
BsonBinary bsonBinary = new BsonBinary("binary data".getBytes());
BsonObjectId bsonObjectId = new BsonObjectId(new ObjectId());
BsonTimestamp bsonTimestamp = new BsonTimestamp();
```

In practice, we work with familiar Java types. The driver converts String, int, Date, and other common types to their BSON equivalents behind the scenes.

For example, when we call new Date() in Java, MongoDB stores it as a BSON Date type, represented as milliseconds since the epoch:

```
Document doc = new Document("created", new Date()); collection.insertOne(doc);
```

Internally, MongoDB stores it like this:

```
{ "created": { "$date": "2025-05-09T12:34:56.789Z" }
```

Understanding this conversion helps when we're debugging data types or working with MongoDB tools that expose BSON representations.

When interacting with MongoDB, we typically work with the Document class rather than BSON-specific types. The Document class represents a BSON document and allows us to structure data using standard Java types.

Here's how we create a basic document:

```
private static void createBasicDocument() {
    System.out.println("\n--- Basic Document Creation ---");

    Document doc = new Document("name", "John Doe")
            .append("age", 30)
            .append("isMember", true)
            .append("joined", new Date())
            .append("_id", new ObjectId());

    collection.insertOne(doc);
    System.out.println("Inserted Document: " + doc.toJson());
}
```

In this method, we create a Document object with standard Java types: a String, an int, a boolean, a Date, and an ObjectId. The MongoDB driver automatically converts these to BSON types when the document is inserted into the collection.

```
Inserted Document: {
   "_id": "64e99b0b4321a1b9a6e4d8c7", 
   "name": "John Doe", 
   "age": 30, 
   "isMember": true, 
   "joined": "2025-05-09T12:34:56.789Z"
}
```

The ObjectId is generated automatically, if not provided. The Date is converted to a BSON Date type, stored as milliseconds since the epoch.

### Nested fields and arrays

MongoDB allows for nested structures and arrays, making it easy to represent complex data within a single document. This structure gives us more flexibility in how we want to model our data than traditional relational databases, where data would typically be spread across multiple tables. In MongoDB, we can embed related data directly within the document, creating hierarchical structures that are easy to query and manipulate.

In Java, we use the Document class to create nested structures and arrays. Each nested level is represented by another Document, and arrays are represented by Java List objects. When we work with nested documents and arrays in MongoDB, each level of nesting is still BSON, not JSON. This matters because MongoDB uses BSON-specific types like Decimal128 and Date, even in nested structures:

```
private static void createNestedDocument() {
    System.out.println("\n--- Nested Fields and Arrays ---");

    Document nestedDoc = new Document("user", "Alice")
            .append("balance", new Decimal128(new BigDecimal("12345.67")))
            .append("contacts", Arrays.asList("123-456-7890", "987-654-3210"))
            .append("address", new Document("city", "Dublin").append("postalCode", "D02"))
            .append("tags", Arrays.asList("premium", "verified"))
            .append("activity", new Document("login", new Date()).append("status", "active"));

    collection.insertOne(nestedDoc);
    System.out.println("Inserted Nested Document: " + nestedDoc.toJson());
}
```

In this method, we are creating a document that represents a user with several fields:

* "user" is a simple string field.
* "balance" is a Decimal128 value, which is used for financial calculations to prevent precision loss.
* "contacts" is a list of strings, representing multiple contact numbers.
* "address" is a nested document containing "city" and "postalCode".
* "tags" is an array of strings, useful for categorizing or labeling documents.
* "activity" is another nested document containing a login timestamp and a status field.

When the above method is executed, the document is converted to BSON and inserted into the MongoDB collection. The output of the inserted document will look like this:

```
Inserted Nested Document: {
  "user": "Alice",
  "balance": 12345.67,
  "contacts": ["123-456-7890", "987-654-3210"],
  "address": {
    "city": "Dublin",
    "postalCode": "D02"
  },
  "tags": ["premium", "verified"],
  "activity": {
    "login": "2025-05-09T12:34:56.789Z",
    "status": "active"
  }
}
```

The nested structure is straightforward to read and query. Each level of nesting is a separate Document object, and arrays are automatically converted to BSON arrays.

### Why use nested structures?

Nested documents provide a way to keep related data together, minimizing the number of queries needed to access complete data sets. Instead of joining tables, we can query a single document to retrieve user details, address information, and recent activity. This approach is particularly useful when dealing with hierarchical data, embedded lists, or object relationships that are tightly coupled.

## Raw BSON manipulation

So far, we've relied on the Document class to handle BSON conversion. But what if we need direct control over BSON structure? That's where raw BSON manipulation comes in. MongoDB's Java driver provides a higher-level Document class that abstracts away BSON specifics, allowing us to work with Java types like String, int, and Date. However, sometimes, we may need to interact directly with BSON data. This can be useful when dealing with binary data, timestamps, or performing low-level optimizations.

* **Binary data:** BSON allows for binary storage (BsonBinary). This is useful for handling images, files, or encrypted data.
* **Timestamps:** BSON BsonTimestamp includes both a time and an increment value, making it useful for tracking operations in oplogs or distributed systems.

The BsonDocument class provides a more granular way to construct BSON documents using BSON-specific types such as BsonString, BsonInt32, and BsonBinary. Unlike the Document class, BsonDocument requires explicit type declarations for each field, making it more verbose but also more explicit.

The following method constructs a BSON document directly using BSON-specific classes:

```
private static void demonstrateRawBSON() {
    System.out.println("\n--- Raw BSON Manipulation ---");

    BsonDocument rawBson = new BsonDocument()
            .append("title", new BsonString("Raw BSON Example"))
            .append("value", new BsonInt32(100))
            .append("binaryData", new BsonBinary("raw data".getBytes()))
            .append("timestamp", new BsonTimestamp())
            .append("array", new BsonArray(Arrays.asList(
                    new BsonInt32(1), 
                    new BsonInt32(2), 
                    new BsonInt32(3)
            )));

    System.out.println("Raw BSON: " + rawBson.toJson());
}
```

In this example, we create a BSON document using explicit BSON classes:

* "title" is a BsonString, representing a UTF-8 string.
* "value" is a BsonInt32, a 32-bit integer.
* "binaryData" is a BsonBinary, representing raw byte data as a base64-encoded string.
* "timestamp" is a BsonTimestamp, containing both a Unix timestamp and an increment counter.
* "array" is a BsonArray, holding multiple BsonInt32 values.

Each append() call explicitly defines the BSON type, making this method more verbose than using the Document class but also more precise.

```
Raw BSON: {
  "title": "Raw BSON Example",
  "value": 100,
  "binaryData": {
    "$binary": "cmF3IGRhdGE=",
    "$type": "00"
  },
  "timestamp": {
    "$timestamp": {
      "t": 1650034567,
      "i": 1
    }
  },
  "array": [1, 2, 3]
}
```

The binaryData field is represented as a base64-encoded string with a type identifier (00 for generic binary data). The timestamp field includes both a time value (t) and an increment (i), useful for replication and internal operations.

Direct BSON manipulation is not typically necessary for most MongoDB operations. For most use cases, the Document class is sufficient, and a lot more intuitive. The BsonDocument class is there when we need more precise control over BSON data or when working with advanced MongoDB features like oplog processing or custom serialization.

## Querying with BSON

When querying MongoDB, we typically use the [Filters class](https://www.mongodb.com/docs/drivers/java/sync/current/builders/filters/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+intro+to+bson&utm_term=tim.kelly).The Filters class provides static factory methods for all the MongoDB query operators. Each method returns an instance of the BSON type, which we can pass to any method that expects a query filter. These filters work with BSON data but allow us to write queries using Java types and let the MongoDB driver handle the conversion to BSON.

Let's take a look at a simple equality filter. The following method demonstrates how to query for specific documents using Filters.eq() and Filters.exists():

```
private static void queryWithBSON() {
    System.out.println("\n--- Querying with BSON ---");

    // Find the first document where the "user" field is "Alice"
    Document result = collection.find(Filters.eq("user", "Alice")).first();
    if (result != null) {
        System.out.println("Found Document: " + result.toJson());
    }

    // Find all documents that contain the "activity" field
    List<Document> results = collection.find(Filters.exists("activity")).into(new ArrayList<>());
    System.out.println("Documents with 'activity' field:");
    results.forEach(doc -> System.out.println(doc.toJson()));
}
```

The Filters.eq() method creates a simple equality query, matching documents where the user field is "Alice". This query is not searching for a string, but for a BSON BsonString. Similarly, when querying for dates, BSON expects a Date type, not a string. This query is similar to the following MongoDB query in the shell:

```
db.collection.find({ "user": "Alice" })
```

The Filters.exists() method finds documents that contain a specific field, regardless of its value. In this case, we are searching for all documents that have the "activity" field. This query is equivalent to:

```
db.collection.find({ "activity": { $exists: true } })
```

If a document with "user": "Alice" exists, the output will look something like this:

```
--- Querying with BSON ---
Found Document: {
  "_id": "64e99b0b4321a1b9a6e4d8c7",
  "user": "Alice",
  "balance": 12345.67,
  "contacts": ["123-456-7890", "987-654-3210"],
  "address": {
    "city": "Dublin",
    "postalCode": "D02"
  },
  "tags": ["premium", "verified"],
  "activity": {
    "login": "2025-05-09T12:34:56.789Z",
    "status": "active"
  }
}
```

If there are multiple documents containing the "activity" field, each will be printed as a separate JSON object.

The Filters class provides a range of query operators, allowing us to construct complex queries using methods like eq(), exists(), gt(), lt(), and in(). These methods allow us to write type-safe queries without dealing directly with BSON objects.

This approach keeps the syntax concise and consistent, making the most of the Java types while MongoDB handles the BSON conversion automatically.

## Aggregation with BSON

[MongoDB's aggregation](https://www.mongodb.com/docs/manual/aggregation/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+intro+to+bson&utm_term=tim.kelly) framework allows for complex data processing, transforming documents in a collection through a series of stages like filtering, grouping, and projecting. While we usually interact with MongoDB through Java types like String or int, the aggregation framework operates directly on BSON data, making it important for us to understand how BSON types are handled in aggregation operations.

In the Java driver, we construct aggregation pipelines using the Aggregates class, where each stage is represented as a BSON operation. Each stage in the pipeline processes BSON documents, applying transformations and producing a new BSON structure for the next stage.

Each stage processes BSON data directly. MongoDB maintains BSON data types throughout the pipeline, preventing data loss and ensuring accuracy in operations like [$sum](https://www.mongodb.com/docs/manual/reference/operator/aggregation/sum/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+intro+to+bson&utm_term=tim.kelly) and [$avg](https://www.mongodb.com/docs/manual/reference/operator/aggregation/avg/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+intro+to+bson&utm_term=tim.kelly).

For instance, if we aggregate on a Decimal128 field, the aggregation framework maintains the precision:

```
List<Bson> pipeline = List.of(     Aggregates.group("$user", 
Accumulators.sum("totalBalance", "$balance")) );
```

If balance is stored as a Decimal128, the aggregation framework sums it as a Decimal128. This is crucial for financial calculations where precision matters.

In this example, we will build an aggregation pipeline that:

1. Filters documents to include only those with a balance field.
2. Groups documents by the user field, calculating the sum of all balance values for each user.
3. Projects the output to include the user and the calculated totalBalance.

```
private static void aggregateWithBSON() {
    System.out.println("\n--- Aggregation with BSON ---");

    List<Bson> pipeline = List.of(
        Aggregates.match(Filters.exists("balance")),
        Aggregates.group("$user", Accumulators.sum("totalBalance", "$balance")),
        Aggregates.project(new Document("user", "$_id").append("totalBalance", 1))
    );

    List<Document> results = collection.aggregate(pipeline).into(new ArrayList<>());
    System.out.println("Aggregation Results:");
    results.forEach(doc -> System.out.println(doc.toJson()));
}
```

**Match stage:** The first stage filters documents based on the existence of the balance field:

```
Aggregates.match(Filters.exists("balance"))
```

This generates a BSON structure similar to:

```
{ "$match": { "balance": { "$exists": true } } }
```

The Filters.exists() method constructs a BSON document using the $exists operator, targeting BSON fields directly. If balance is a Decimal128 type, it remains a Decimal128 throughout the pipeline, maintaining precision.

**Group stage:** The group stage aggregates documents by a specified field. In BSON, the _id field represents the grouping key. Here, we use the user field as the key and calculate the sum of the balance field:

```
Aggregates.group("$user", Accumulators.sum("totalBalance", "$balance"))
```

The resulting BSON structure:

```
{
  "$group": {
    "_id": "$user",
    "totalBalance": { "$sum": "$balance" }
  }
}
```

In this stage, the key (_id) is defined as the user field. The balance field is aggregated using the $sum accumulator, and the result is stored in a new BSON field called totalBalance.

**Project stage:** In the project stage, we transform the structure of the BSON document, selecting specific fields and renaming them as needed:

```
Aggregates.project(new Document("user", "$_id").append("totalBalance", 1))
```

The resulting BSON structure:

```
{
  "$project": {
    "user": "$_id",
    "totalBalance": 1
  }
}
```

This operation renames the _id field to user and includes the totalBalance field in the output. Notice that _id is no longer a BSON ObjectId but a value derived from the group key, in this case, a String.

If the collection contains the following documents:

```
{ "user": "Alice", "balance": 5000 }
{ "user": "Alice", "balance": 3000 }
{ "user": "Bob", "balance": 7000 }
{ "user": "Alice", "balance": 2500 }
```

The output of the aggregation pipeline will look like this:

```
Aggregation Results:
{"user": "Alice", "totalBalance": 10500}
{"user": "Bob", "totalBalance": 7000}
```

Each document in the output is a BSON object resulting from the aggregation pipeline. The user field is derived from the grouping key, and totalBalance is the calculated sum of all balance values per user.

## POJO mapping: Bridging Java and BSON

As we've seen, BSON is the native data format for storing documents in MongoDB. While BSON extends JSON with additional data types, Java developers typically don't need to work directly with raw BSON. Instead, we can work with familiar Java objects and let the MongoDB driver handle the BSON conversion behind the scenes. This is where POJO (Plain Old Java Object) mapping comes into play.

POJOs are often used for data encapsulation, which is the practice of separating business logic from data representation. If you want a deep understanding of POJO mapping with MongoDB, check out [our guide](https://www.mongodb.com/docs/drivers/java/sync/current/data-formats/document-data-format-pojo/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+intro+to+bson&utm_term=tim.kelly), but we will go over the basics and get up and running with a simple example.

The PojoCodecProvider allows MongoDB to automatically map Java objects to BSON documents and back. This not only simplifies data handling but also keeps our data model consistent with our Java classes.

### Why POJO mapping?

Without POJO mapping, we would need to manually convert Java objects into Document objects and vice versa. This is error-prone and can quickly become cumbersome as our data model grows more complex.

POJO mapping abstracts away the BSON conversion process. We define our Java classes, and the MongoDB driver handles the rest.

### Setting up POJO mapping in Java

Before we define our Java classes, we need to configure the PojoCodecProvider. This codec provider registers our Java classes for automatic BSON mapping.

```
import org.bson.codecs.configuration.CodecProvider;  
import org.bson.codecs.configuration.CodecRegistry;  
import org.bson.codecs.pojo.PojoCodecProvider;  

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;  
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;  
import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;  

public class CodecSetup {  

    public static CodecRegistry getPojoCodecRegistry() {  
        CodecProvider pojoCodecProvider = PojoCodecProvider.builder()  
                .automatic(true)
                .build();  

        return fromRegistries(getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));  
    }  
}
```

The codec registry combines the default [BSON codecs](https://www.mongodb.com/docs/drivers/java/sync/current/data-formats/codecs/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+intro+to+bson&utm_term=tim.kelly) with our custom POJO codecs.

### Defining a POJO class: User

Now, let's define a simple User class that MongoDB will automatically map to BSON.

```
package com.mongodb;  

import org.bson.types.ObjectId;  
import org.bson.codecs.pojo.annotations.BsonId;  
import org.bson.codecs.pojo.annotations.BsonProperty;  

public class User {  

    @BsonId  
    private ObjectId id;  

    @BsonProperty("username")  
    private String name;  

    private int age;  

    @BsonProperty("member")  
    private boolean isMember;  

    public User() {  
        // No-arg constructor required for POJO mapping  
    }  

    public User(String name, int age, boolean isMember) {  
        this.name = name;  
        this.age = age;  
        this.isMember = isMember;  
    }  

    public ObjectId getId() {  
        return id;  
    }  

    public void setId(ObjectId id) {  
        this.id = id;  
    }  

    public String getName() {  
        return name;  
    }  

    public void setName(String name) {  
        this.name = name;  
    }  

    public int getAge() {  
        return age;  
    }  

    public void setAge(int age) {  
        this.age = age;  
    }  

    public boolean isMember() {  
        return isMember;  
    }  

    public void setMember(boolean isMember) {  
        this.isMember = isMember;  
    }  

    @Override  
    public String toString() {  
        return "User [id=" + id + ", username=" + name + ", age=" + age + ", member=" + isMember + "]";  
    }  
}
```

* **@BsonId**: Marks the id field as the BSON _id field
* **@BsonProperty**: Maps the name field to the BSON key username, and isMember to member

### Inserting and querying POJOs

Now that we have our codec set up and our POJO classes defined, let's see how we can insert and query these objects.

```
// ...
private static MongoCollection<User> userCollection;  

public static void main(String[] args) {  

	// ...
    CodecRegistry codecRegistry = CodecSetup.getPojoCodecRegistry();  

    try (MongoClient client = MongoClients.create(CONNECTION_STRING)) {    

		// ...

        userCollection = database.getCollection("users", User.class).withCodecRegistry(codecRegistry);  
        demonstratePojoMapping();  
    }  
}

private static void demonstratePojoMapping() {  
    System.out.println("\n--- POJO Mapping ---");  

    User user = new User("John Doe", 30, true);  
    userCollection.insertOne(user);  
    System.out.println("Inserted User: " + user);  

    List<User> users = new ArrayList<>();  
    userCollection.find().into(users);  
    System.out.println("Retrieved Users: " + users);  
}
```

* First, we make sure we register our codec. We then connect to a User collection with this codec.
* When the User object is inserted, MongoDB will automatically convert it to a BSON document.
* When querying, MongoDB will deserialize the BSON back into a User object, maintaining type integrity.

### BSON representation of the user document

When the User object is inserted, MongoDB stores it as a BSON document. The BSON representation will look like this:

```
{
	"_id":{ "$oid":"68211b91221c1ce15490b565" },
	"age":{ "$numberInt":"30" },
	"member":true,
	"username":"John Doe"
}
```

Notice that:

* The id field is mapped to the BSON _id field.
* The name field is renamed to username.

Even though we're working with plain Java objects, MongoDB is still converting these to BSON. This automatic conversion ensures that data types are preserved when stored in MongoDB.

### Why use POJO mapping?

1. **Cleaner code:**   
   POJO mapping eliminates the need to manually convert Java objects to BSON Document objects, reducing boilerplate code.
2. **Type safety:**   
   MongoDB automatically handles type conversions, ensuring that data is deserialized to the correct Java type (e.g., ObjectId, Date).
3. **Nested structures:**   
   Complex nested objects are easily represented using embedded BSON documents, maintaining data structure and hierarchy.

### What about custom conversions?

The default POJO mapping behavior is plenty sufficient for most use cases, but MongoDB also provides options for advanced customization. We can define custom codecs, register additional conventions, or handle abstract types and enums using advanced configuration. For more advanced scenarios, check out our [PojoCodecProvider documentation](https://mongodb.github.io/mongo-java-driver/4.11/driver/getting-started/pojos/).

## Conclusion

BSON is at the core of how MongoDB stores and transmits data. It extends JSON with additional data types like ObjectId, Decimal128, and Timestamp, allowing MongoDB to handle richer and more complex data structures. While BSON is a binary format optimized for storage and traversal, the Java driver abstracts most of its complexity, allowing developers to work with familiar Java types.

Throughout this tutorial, we explored how BSON types map to Java types and how we can interact with BSON data using the Document class and the PojoCodecProvider. We saw how nested structures, arrays, and raw BSON objects are constructed and manipulated, and how aggregation pipelines process BSON data directly within the database.

While most operations in MongoDB can be performed using Java objects and the Document class, understanding BSON is essential for tasks involving binary data, precision calculations with Decimal128, and operations that require explicit data types like BsonTimestamp.

In every operation---document creation, querying, aggregation---BSON is working behind the scenes, ensuring that data types are consistent, optimized, and capable of handling complex structures. By understanding how Java types map to BSON, we can write more predictable queries, prevent data type mismatches, and take full advantage of MongoDB's type system. For a deeper dive, explore the [BSON Specification](https://bsonspec.org/).
