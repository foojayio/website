---
title: "Implementing Soft Deletes in Java"
slug: "implementing-soft-deletes-in-java"
date: "2026-05-21T10:09:00+00:00"
lastmod: "2026-05-22T07:06:23+00:00"
description: "Usually, when deleting documents from a database, the entry is permanently gone and can not be recovered or accessed again. Sometimes data needs to be made unavailable for regular access without actually being removed from a database. A common example is a user deleting their account on a platform, but retention policies require you to keep all data related to that user for a certain period of time. At the same time, no data regarding that user is accessible on the platform. A soft delete is a treatment for a piece of data that ensures it is ignored by your application while actually still being stored in the database."
authors:
  - "daniel-hartmann"
image: "https://foojay.io/wp-content/uploads/2026/05/Technical_STORAGE_Archive2_Thumbnail_BS_Lavender.png"
categories:
  - "Databases"
tags:
related_posts:
enlighterjs: true
frozen: false
---

What are soft deletes? {#soft-deletes}
--------------------------------------

Usually, when deleting documents from a database, the entry is permanently gone and can not be recovered or accessed again.

Sometimes data needs to be made unavailable for regular access without actually being removed from a database. A common example is a user deleting their account on a platform, but retention policies require you to keep all data related to that user for a certain period of time. At the same time, no data regarding that user is accessible on the platform. A soft delete is a treatment for a piece of data that ensures it is ignored by your application while actually still being stored in the database.

How can soft deletes be approached? {#How-can-soft-deletes-be-approached?}
--------------------------------------------------------------------------

### Field Flagging {#Field-Flagging}

One way to approach the realization of soft deletes would be to add an additional field to your collection that tracks whether a document should be visible to your application or not. The simplest way to do this would be to implement a Boolean field with a name like "isDeleted" that you can query for in order to exclude documents that are soft deleted. While this allows you to identify ***if*** a document has been deleted, it will not help with data retention periods. To cover this, we need to know when a document has been deleted. Thus, a better solution would be to implement a date field instead, calling it "deletedAt". The field would be set to **null**for each active document and hold the timestamp of the deletion otherwise.

### Archive collections {#archive-collections}

Another approach is to move the data into an archive collection. For example, if you are using a user collection for which you need to implement soft deletion, add a user_deleted collection and move deleted documents over there. It might still be useful to add a separate date field to your documents to track when a document has been deleted (or moved to the archive collection in this case). A benefit of this approach over flagging deleted documents is that it keeps the primary collection leaner and does not add any overhead to your queries.

Implementation of soft deletes {#Implementation-of-soft-deletes}
----------------------------------------------------------------

### Java driver {#Java-driver}

The code snippets provided in this guide are based on the MongoDB [Java Sync driver](https://www.mongodb.com/docs/drivers/java/sync/current/) version 5.6. This is the latest version of the Java driver provided officially by [MongoDB](https://www.mongodb.com/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=soft-imp-mongodb-foojay&utm_term=hugh.murray), built to develop synchronous applications.

### Code examples {#Code-examples}

As a basis for our examples in this guide, we are using the sample_mflix database provided as one of the samples within [MongoDB Atlas.](https://www.mongodb.com/products/platform/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=soft-imp-mongodb-foojay&utm_term=hugh.murray)

#### Flagging approach {#Flagging-approach}

What we want to do is to change the schema of the users collection from its current form:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
  "_id": {
    "$oid": "59b99dddcfa9a34dcd788604"
  },
  "name": "Thoros of Myr",
  "email": "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="631302160f3c08021a062304020e060c05170b110c0d4d0610">[email&nbsp;protected]</a>",
  "password": "$2b$12$bkA1MM3UEwZ4N0VpCQY68eMY8HKTHWtk2xI2QnG4MuW5UWHlBrF8G"
}</pre>

To something that includes a flag regarding the deletion:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
  "_id": {
    "$oid": "59b99dddcfa9a34dcd788604"
  },
  "name": "Thoros of Myr",
  "email": "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="f28293879ead99938b97b295939f979d94869a809d9cdc9781">[email&nbsp;protected]</a>",
  "password": "$2b$12$bkA1MM3UEwZ4N0VpCQY68eMY8HKTHWtk2xI2QnG4MuW5UWHlBrF8G",
  "deletedAt": null
}</pre>

It's worth mentioning that you could also omit the field for an active document instead. MongoDB will still return it in queries that are filtering for {"deletedAt": null}. However, an index on the field will not be able to efficiently return all active documents when the field is absent. Therefore, we will prime our collection by actively setting null values and later update documents with correct timestamps for soft deletion. But first, we will set up a reusable "mongoclient" object to make use of [MongoDB connection pooling](https://www.mongodb.com/docs/manual/administration/connection-pool-overview/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=soft-imp-mongodb-foojay&utm_term=hugh.murray) instead of connecting a new client with every application call. This will reduce latency significantly and reduce the number of times new connections have to be created:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public final class MongoClientProvider {

    private static volatile MongoClient mongoClient;
    private static final Object LOCK = new Object();

    // Private constructor to prevent instantiation
    private MongoClientProvider() {}

    public static MongoClient getClient() {
        if (mongoClient == null) {
            synchronized (LOCK) {
                if (mongoClient == null) {
                    mongoClient = createClient();
                    validateConnection(mongoClient);
                    registerShutdownHook();
                }
            }
        }
        return mongoClient;
    }

    private static MongoClient createClient() {

        ConnectionString connString = new ConnectionString("&lt;my connection string&gt;");

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .retryWrites(true)
                .build();

        return MongoClients.create(settings);
    }

    // using a ping to validate the Connection
    private static void validateConnection(MongoClient client) {
        try {
        client.getDatabase("admin")
              .runCommand(new Document("ping", 1));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to connect to MongoDB", e);
    }

   // make sure the MongoClient is closed properly when the JVM shuts down
   private static void registerShutdownHook() {
      Runtime.getRuntime().addShutdownHook(new Thread(() -&gt; {
         if (mongoClient != null) {
            try {
               mongoClient.close();
            } catch (Exception e) {
               System.err.println("Error closing MongoClient: " + e.getMessage());
            }
         }
      }));
   }

}
}</pre>

Now we can use the getClient() Method to efficiently call upon a connection to the database. Next, we can start priming the collection:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class UpdateByFieldExample {

public static void main(String[] args) {

    try (MongoClient mongoClient = getClient()) {

        MongoDatabase database = mongoClient.getDatabase("sample_mflix");
        MongoCollection&lt;Document&gt; collection = database.getCollection("users");

        UpdateResult result = collection.updateMany(
Filters.empty(), //empty filter to apply to all documents
Updates.set("deletedAt", null)
        );

        }
    }
}</pre>

This method will update the whole collection in one go. For larger collections, it can be a good idea to perform operations like that in batches to avoid loading everything into memory at once and also gain more control over throughput, for example, when working within a production environment that has to maintain a regular workload during the change. Performing the change in batches could look like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Updates.*;

public class BatchUpdateExample {

    public static void main(String[] args) {

        try (MongoClient mongoClient = getClient()) {

            MongoDatabase db = mongoClient.getDatabase("sample_mflix");
            MongoCollection&lt;Document&gt; collection = db.getCollection("users");

            int batchSize = 100;
            ObjectId lastId = null;

            while (true) {

                // empty filter to apply to all documents appended by the last id of the previous batch to skip already processed documents
                Bson filter = lastId == null ? 
                Filters.empty() : Fitlers.gt(“_id”, lastId);
                }

                // Fetch batch
                FindIterable&lt;Document&gt; docs = collection.find(filter)
                        .sort(new Document("_id", 1))
                        .limit(batchSize);

                // put each document Id into a List
                List&lt;ObjectId&gt; ids = new ArrayList&lt;&gt;();
                for (Document doc : docs) {
                    ObjectId id = doc.getObjectId("_id");
                    ids.add(id);
                    lastId = id;
                }

                // break the loop if no documents are left to process
                if (ids.isEmpty()) {
                    break;
                }

                // perform update on current batch
                UpdateResult result = collection.updateMany(
                        in("_id", ids),
                        Updates.set("deletedAt", null)
                        )
                );
            }
        }
    }
}</pre>

Now that the collection is prepared, we can start applying the soft delete logic. Delete a single document by changing "deletedAt" from null to a current timestamp:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.Date;

public static void softDeleteUser(String id) {

      try (MongoClient mongoClient = getClient()) {
            MongoDatabase db = mongoClient.getDatabase("sample_mflix");
            MongoCollection&lt;Document&gt; collection = db.getCollection("users");

    		collection.updateOne(
            		Filters.eq("_id", new org.bson.types.ObjectId(id)),
                    	Updates.set("deletedAt", new Date())
    		);
	}
}</pre>

Now that the deletes are in place, we need to adapt our queries (add deletedAt:null to filters) in order to ignore fetching soft-deleted documents.

Let's say we usually query for users by name and e-mail address, like so:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public static Document getUser(String name, String mail)) {
Try (MongoClient mongoClient = getClient()) {

   MongoDatabase db = client.getDatabase("sample_mflix");
   MongoCollection&lt;Document&gt; collection = database.getCollection("users");

   Bson filter = Filters.and(Filters.eq("name", name), Filters.eq("email", mail));

   // Retrieves documents that match the filter
   Document result = collection.find(filter).first();
   Return result;
}

Return Document emptyDoc = createEmptyDocument();
}</pre>

We will now expand our filter to ensure that soft-deleted documents are not retrieved by our query.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public static Document getUserIfNoSoftDelete(String name, String mail)) {
Try (MongoClient mongoClient = getClient()) {

   MongoDatabase db = client.getDatabase("sample_mflix");
   MongoCollection&lt;Document&gt; collection = database.getCollection("users");

   Bson filter = Filters.and(Filters.eq("name", name), Filters.eq("email", mail), Filters.eq(deletedAt, null));

   // Retrieves documents that match the filter
   Document result = collection.find(filter).first();
   Return result;
}

Return Document emptyDoc = createEmptyDocument();

}</pre>

The next thing will be indexing the field; otherwise, our queries will slow down

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public static void createSoftDeleteIndex() {
Try (MongoClient mongoClient = getClient()) {

   MongoDatabase database = client.getDatabase("sample_mflix");
   MongoCollection&lt;Document&gt; collection = database.getCollection("users");

   // Create compound index
   String indexName = collection.createIndex(
      compoundIndex(
         ascending("email"),
         ascending("name"),
         ascending("deletedAt")
      ),
   new IndexOptions().name("idx_email_name_deletedAt")
   );
}
}</pre>

Lastly, we want to be able to recover a soft-deleted document. The reason may be an accidental deletion or a data rollback of some sort. To achieve this, we can simply set the deletedAt field that stores the timestamp of the deletion to null again:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import java.util.Date;
public static void recoverDocument(String id) {
      try (MongoClient mongoClient = getClient()) {
            MongoDatabase db = mongoClient.getDatabase("sample_mflix");
            MongoCollection&lt;Document&gt; collection = db.getCollection("users");
    		collection.updateOne(
            		Filters.eq("_id", new org.bson.types.ObjectId(id)),
                    		Updates.set("deletedAt", null)
    		);
	}
}</pre>

Now we have implemented a deletion method that keeps deleted documents in the database for later recovery, auditing, or other purposes

#### Archive approach {#Archive-approach}

An alternative to flagging is to use soft deletes by moving documents into an archive collection. This would have the benefit that the main collection can be kept lean and small for faster access and smaller indexes.

To implement this approach, we first need to create the secondary collection:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public static void createCollectionExample() {

        try (MongoClient mongoClient = getClient()) {

            // Access the database (it will be created if it doesn't exist)
            MongoDatabase db = mongoClient.getDatabase("sample_mflix");

            // Create a new collection
            database.createCollection("users_archive");
        }
    }
}</pre>

Now we can move documents to that collection and, by that realize a soft delete.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public static void softDeleteWithArchive() {

        try (MongoClient client = getClient()) {
            MongoDatabase db = client.getDatabase("sample_mflix");

            MongoCollection&lt;Document&gt; users = db.getCollection("users");
            MongoCollection&lt;Document&gt; archive = db.getCollection("users_archive");

            ObjectId userId = new ObjectId("64f123456789abcdef123456"); // replace with real ID

            // fetch the document from the user collection
            Document userDoc = users.find(eq("_id", userId)).first();

            if (userDoc != null) {
                // insert the document into the archive collection
                archive.insertOne(userDoc);

                // delete the document from the user collection
                users.deleteOne(eq("_id", userId));
            } 
        }
    }
}</pre>

In order to recover a document using this method, it can just be moved back the same way. Additionally, adding a field with a timestamp can be an option. This way, it is documented when a document has been soft-deleted. That information can be relevant, for example, when certain retention periods have to be met before a document can be removed completely. More on that when we come to the topic of cleanups.

### Cascading to related collections {#Cascading-to-related-collections-}

When using this method we need to make sure to also cascade deletions into related collections. For our example, let's take a look at the comments collection.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
  "_id": {
    "$oid": "5a9427648b0beebeb69579e7"
  },
  "name": "Mercedes Tyler",
  "email": "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="f8959d8a9b9d9c9d8ba78c81949d8ab89e99939d9f95999194d69b9795">[email&nbsp;protected]</a>",
  "movie_id": {
    "$oid": "573a1390f29313caabcd4323"
  },
  "text": "Eius veritatis vero facilis quaerat fuga temporibus. Praesentium expedita sequi repellat id. Corporis minima enim ex. Provident fugit nisi dignissimos nulla nam ipsum aliquam.",
  "date": {
    "$date": "2002-08-18T04:56:07.000Z"
  }
}</pre>

Each comment inside this collection is written by a specific user and is referenced by the field "name" which also exists in the user collection. When that user is deleted, it might also be desirable to delete all related comments. Now the question is whether to actually remove those comments from the database or to again implement a soft delete so they are restorable. This depends on your specific use cases and retention policies. Let's say, for example, you use soft deletes on your user collection because certain legal regulations require you to keep this information for a set period of time. In this case, the comments can probably be hard deleted since you will never want to touch them again. Another scenario might be that your application offers a user recovery feature, allowing a user who has deleted their account in the past to reactivate it and recover all their information. In that case, you might want to implement soft deletion for all related data to support that feature. For this example, it is assumed that related documents will be hard deleted. This would be one way to implement that behavior:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;

import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public static void cascadeDeletion (String userName){
        try (MongoClient client = getClient()) {
            MongoDatabase db = client.getDatabase("sample_mflix");
            MongoCollection&lt;Document&gt; collection = db.getCollection("comments");

            // Delete all comments that are posted by the deleted user
            DeleteResult result = collection.deleteMany(eq("name", userName));
        }
    }
}</pre>

This could either be included in the softDelete() function itself or called separately.

### Cleanup with TTL indexing {#Cleanup-with-TTL-indexing}

In many scenarios, a soft delete will only be a temporary solution. Documents are kept in a soft-deleted status for a while before being removed completely from the database. One common use case is to do that once the document has been inactive for a specified period of time. Let's say, for example, user data needs to be retained for twelve months after an account gets deleted. The soft delete allows engineers to realize that without impacting query performance for active users. Now, in order to clean up documents that have passed the retention period, we can make use of [MongoDB's TTL indexes](https://www.mongodb.com/docs/manual/core/index-ttl/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=soft-imp-mongodb-foojay&utm_term=hugh.murray). This is a special type of single-field index that can be utilized to automatically remove documents from the database. To do this, we will create an index on the "deletedAt" field, which we set to a timestamp when performing the soft delete.

Any document that does not contain that field at all will not be impacted by the TTL index.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import static com.mongodb.client.model.Indexes.ascending;
import com.mongodb.client.model.IndexOptions;

import java.util.concurrent.TimeUnit;

public static void createTTLIndex () {

        try (MongoClient client = getClient) {
            MongoDatabase db = client.getDatabase("sample_mflix");
            MongoCollection&lt;Document&gt; collection = db.getCollection("users");

            // TTL index: expire documents 1 year (365 days) after deletedAt
            IndexOptions options = new IndexOptions()
                    .expireAfter(365L, TimeUnit.DAYS);

            String indexName = collection.createIndex(ascending("deletedAt"), options);
        }
    }
}</pre>

Pros and cons of soft deletes {#Pros-and-cons-of-soft-deletes}
--------------------------------------------------------------

Now that the basic steps of implementing soft deletes in a MongoDB deployment are covered, let's summarize the pros and cons of this approach.

### Pros {#Pro}

The primary benefit of soft deletes is the simple and fast recovery of single documents without having to rely on backups that will take way more time, and also don't allow for the level of granularity that soft deletes are bringing. Also, it is a great way to ensure retention and compliance policies without a major impact on database performance. Finally, by adding more metadata with additional fields (e.g., who performed the delete), soft deletes can also be used.

### Cons {#Con}

Drawbacks of this approach would be the additional storage requirements for documents that, in the case of a hard delete, would not require any resources whatsoever. Also, the increased complexity of queries since each request needs to make sure it does not fetch deleted results. Another point is that indexes might become bloated by maintaining the additional entries. Lastly, it carries an increased risk of generating inconsistent database states when, for example, cascading of operations is not controlled thoroughly.

Wrap Up {#Wrap-Up}
------------------

1. Soft deletes are a great feature to support fast data recovery and retention policies  
2. There are two primary approaches to soft deletes: flagging and archiving  
3. Exemplary code snippets for implementing soft deletes in Java have been provided using the MongoDB Java Sync driver  
4. When utilizing soft deletes, cascading must be considered at all times, as well as eventual clean-ups, for example, with TTL indexes  
5. While being a powerful feature, there are also some drawbacks to soft deletes
