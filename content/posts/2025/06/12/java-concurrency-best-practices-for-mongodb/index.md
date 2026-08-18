---
title: "Java Concurrency Best Practices for MongoDB"
date: "2025-06-12T15:30:29+00:00"
lastmod: "2025-06-12T15:40:26+00:00"
description: "In a multi-threaded, distributed environment like MongoDB, when clients execute queries concurrently, operations interleave with one another if they are - by Vivekanandan Sakthivelu"
authors:
  - "vivekanandan-sakthivelu"
image: "mongologo.png"
categories:
  - "Java"
  - "Mongo"
tags:
related_posts:
frozen: false
---

In a multi-threaded, distributed environment like MongoDB, when clients execute queries concurrently, operations interleave with one another if they are not isolated, whether those operations involve single-document or multi-document operations.

For instance, Client C1's read operation might observe the effects of a write performed by Client C2, even if that write has not yet been made durable. When at least one of the concurrent operations is a write and isolation is not enforced, this can lead to undesirable outcomes, such as:

1. Lost updates.
2. Dirty reads.
3. Non-repeatable reads.
4. Phantom reads.

In this article, we'll look at some of the causes of these issues and how we can both resolve and avoid them entirely.

## Lost updates

Writes to a single document in MongoDB are [atomic](https://www.mongodb.com/docs/manual/core/write-operations-atomicity/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Java+Concurrency+Best+Practices&utm_term=tim.kelly#:~:text=In%20MongoDB%2C%20a%20write%20operation,the%20query%20condition%20still%20matches.). However, if an application reads a document, modifies it, and then writes it back, this entire read-modify-write cycle is not atomic. This scenario can result in a lost update situation, where two clients concurrently read the same document and then update it with different values, causing one client's changes to overwrite the other's changes.

The [example](https://gist.github.com/couragecowardlydog/34e8026bd74b69031b198f5e25b4adfe) below demonstrates this issue. Two threads read the same inventory document and update the quantity field independently. The reads and writes are not coordinated, meaning one thread's update may overwrite the other's, resulting in an inconsistent state.

```java
package io.gitrebase;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InventoryUpdate {

    private static final String DATABASE_NAME = "test";
    private static final String COLLECTION_NAME = "inventory";

    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017,localhost:27018,localhost:27019/?replicaSet=rs0");
        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
        MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

        Document product = new Document("productCode", "PROD_001")
                .append("name", "Laptop")
                .append("quantity", 50);
        collection.insertOne(product);
        System.out.println("Product created: " + product.toJson());

        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(() -> updateProductQuantity(collection, "PROD_001", -5));  // decrease by 5
        executorService.submit(() -> updateProductQuantity(collection, "PROD_001", 10));  // increase by 10

        executorService.shutdown();
    }

    public static void updateProductQuantity(MongoCollection<Document> collection, String productCode, int quantityChange) {
        try {
            // Find product by productCode
            Document productDoc = collection.find(Filters.eq("productCode", productCode)).first();

            if (productDoc != null) {
                int currentQuantity = productDoc.getInteger("quantity");
                int updatedQuantity = currentQuantity + quantityChange;

                Document updatedDoc = new Document("quantity", updatedQuantity);

                UpdateResult result = collection.updateOne(
                        Filters.eq("productCode", productCode),
                        new Document("$set", updatedDoc)
                );

                System.out.println("Updated product " + productCode + ": quantity changed by " + quantityChange +
                        " | New Quantity: " + updatedQuantity);
            } else {
                System.out.println("Product not found for code: " + productCode);
            }
        } catch (Exception e) {
            System.out.println("Error during update: " + e.getMessage());
        }
    }
}
```

To avoid lost updates, it's best to shift the responsibility for concurrency control to the database itself, where possible. For example, using atomic update operators like [`$inc`](https://www.mongodb.com/docs/manual/reference/operator/update/inc/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=Java+Concurrency+Best+Practices&utm_term=tim.kelly#mongodb-update-up.-inc) allows MongoDB to apply changes directly without requiring a read-modify-write cycle in the application. This reduces the chance of conflicting updates and helps maintain data integrity even under concurrent access.

## Dirty reads

A dirty read occurs when an application reads data that might later be rolled back or overwritten. In MongoDB, this can happen outside of transactions when clients read data that hasn't yet been confirmed as durable across the replica set. For example, if a client writes to the primary and another client reads that data immediately, the read might return a value that hasn't been replicated to a majority of nodes. If the primary crashes or steps down before replication completes, the write may be rolled back during the election process, meaning the read saw data that was effectively "undone."

While MongoDB prevents dirty reads within transactions by only making data visible after the transaction is committed, dirty reads can still occur outside of transactions if the application uses the default `readConcern: "local"`. To avoid this, applications should use `readConcern: "majority"` to ensure that reads only return data that has been acknowledged by a majority of replica set members and is unlikely to be rolled back.

## Non-repeatable reads

A non-repeatable read occurs when a client reads the same document multiple times within a session and receives different values because another client has modified the document in between reads.

For the [example](https://gist.github.com/couragecowardlydog/6457af17307f2f3f9acb8e4b1dcda4ab#file-nonrepeatableread-java) below, Client A reads a document before processing another query. Meanwhile, Client B modifies this document. Later, when Client A reads the same document again, it sees the modified version of the document, resulting in a non-repeatable read.

```java
package io.gitrebase;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

public class NonRepeatableRead {

    private static final String DATABASE_NAME = "gitrebase";
    private static final String COLLECTION_PRODUCTS = "products";
    private static final String COLLECTION_ORDERS = "orders";

    public static void main(String[] args) throws InterruptedException {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017,localhost:27018,localhost:27019/?replicaSet=rs0");
        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
        MongoCollection<Document> products = database.getCollection(COLLECTION_PRODUCTS);
        MongoCollection<Document> orders = database.getCollection(COLLECTION_ORDERS);

        products.deleteMany(Filters.eq("category", "PIZZA"));
        Document pizza = new Document("_id", "PIZZA_001")
                .append("name", "Cheese Burst Pizza")
                .append("category", "PIZZA")
                .append("price", 350);
        products.insertOne(pizza);
        System.out.println("Inserted product: " + pizza.toJson());

        // Client A 
        Thread clientAThread = new Thread(() -> {
            try {
                // t1: Fetch product price
                System.out.println("Client A: Fetching product ...");
                Document firstRead = products.find(Filters.eq("_id", "PIZZA_001")).first();
                System.out.println("Client A : " + firstRead.toJson());

                // Simulate delay before placing order
                Thread.sleep(1000);

                // t3: Place an order with the price fetched at t1
                System.out.println("Client A: Placing order ...");
                orders.insertOne(new Document("orderId", "ORD_001")
                        .append("productId", "PIZZA_001")
                        .append("orderedPrice", firstRead.getInteger("price")));
                System.out.println("Client A: Order placed at t3.");

                // Simulate delay before fetching price again
                Thread.sleep(2000);

                // Fetch product price again
                System.out.println("Client A: Fetching product ...");
                Document secondRead = products.find(Filters.eq("_id", "PIZZA_001")).first();
                System.out.println("Client A : " + secondRead.toJson());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread clientBThread = new Thread(() -> {
            try {
                // Increment pizza price by 10%
                Thread.sleep(1000);  // ensure it happens after t1
                System.out.println("Client B: Incrementing pizza price by 10% ...");
                products.updateMany(Filters.eq("category", "PIZZA"), Updates.mul("price", 1.10));
                System.out.println("Client B: Price updated.");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        clientAThread.start();
        clientBThread.start();

        clientAThread.join();
        clientBThread.join();
    }
}
```

* At time t1, Client A issues `findOne({ \_id: 'PIZZA\_001' })` on the products collection to retrieve product details.
* At time t2, Client B updates all documents in the products collection where `category = 'PIZZA'` by incrementing their price by 10% using `{ $mul: { price: 1.10 } }`.
* At time t3, Client A places an order by inserting a document into the orders collection with the price fetched at t1.
* At time t4, Client A issues `findOne({ \_id: 'PIZZA\_001' })` again on the products collection and notices the price has increased.

At t4, **Client A** observes a different version of the document compared to the initial read at t1. This happens because the sequence of operations was not properly isolated. To avoid this, the entire sequence should be encapsulated within a multi-document transaction using a stronger read isolation level, ensuring the client always sees a consistent view of data throughout the session.

## Phantom reads

A non-repeatable read occurs when the value of a document changes between two reads within the same session due to a concurrent write operation. A phantom read occurs when the result set of a query changes between executions in the same session because another client has inserted, deleted, or modified documents that affect the query's outcome. As a result, the subsequent execution of the query returns a different result set than the first, even though no changes were made by the reading client.

In non-transactional operations, if a client uses a "cursor" to iterate over a result set, the same document can be returned in a result set more than once, or missed entirely, if another client modifies the underlying data while the cursor is still active. This behavior leads to an unstable result set and is a manifestation of either phantom or non-repeatable reads.

## How to avoid these issues

To avoid these kinds of anomalies, MongoDB provides concurrency control mechanisms and configurable isolation properties, allowing clients to control the degree to which they observe the effects of concurrent operations.

Concurrency control allows a database to ensure operations execute in an orderly manner, preventing multiple clients from concurrently modifying the same resource and causing an inconsistent database state. At the document level, MongoDB guarantees atomic operations, even if an operation modifies multiple fields of the document, either completes entirely or does not occur at all. Clients either see the complete updated document or no changes at all.

MongoDB uses multi-version concurrency control (MVCC) to ensure concurrent write operations do not lead to lost updates or data inconsistencies. However, concurrency control alone may not be sufficient. Even with proper locking mechanisms, interactions between operations can still lead to anomalies such as dirty reads, non-repeatable reads, and phantom reads, emphasizing the importance of isolation.

### Isolation

Isolation defines the degree to which the operations of one client remain hidden from other concurrently executing operations. It is a critical component of the ACID properties and directly influences the anomalies, such as dirty reads, non-repeatable reads, and phantom reads, that may occur during concurrent access.

### Read concern

In MongoDB, read concern allows applications to control the isolation level of operations by specifying how visible the effects of concurrent writes should be. The read concern level determines whether a read returns uncommitted, in-memory data, or only durable data that has been acknowledged by a majority of replica set members. Read concern can be specified at the operation, session, or transaction level. If the read concern is not specified at the operation level, it defaults to the session, transaction, or replica set default.

![](data:image/png;base64,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)  
*Timeline of a write operation to a three-member replica set*

We'll use the following example to understand the read isolation: In a Primary--Secondary--Secondary (P--S--S) replica set, when a client issues a write at time t1, the primary writes to its oplog and applies the change. Secondaries begin replicating from the oplog at t2 and t3. Once a majority of nodes (in this case, one secondary plus the primary) apply the operation, the majority commit point is reached (t4), and the primary acknowledges the write to the client at t6. Secondaries independently update their read snapshots after processing entries up to the majority commit point (t7, t8).

#### Local

The "local" read concern returns data from the node's in-memory view, without guaranteeing that the data is durable or replicated to other nodes.

In a replica set, if a read follows a write (without a stronger write concern) and the read uses `readConcern: "local"` and read preference as primary, data returned by the read operation has only been written to the primary and not yet acknowledged by a majority of the nodes. If the primary steps down before the write replicates to a majority of the nodes, the write will be rolled back during the election process. So the newly elected primary might not have the previously written data, causing the read operation to observe a value that no longer exists in the cluster. It offers local latency but no durability guarantees across the replica set. Use read concern as local, where absolute durability and consistency aren't critical.

Applications using read concern as "local" will observe the following data in the example discussed:

|   Time   | Primary | Secondary | Secondary |
|----------|---------|-----------|-----------|
| t0       | wpre    | wpre      | wpre      |
| t1       | w0      | wpre      | wpre      |
| t2       | w0      | w0        | wpre      |
| After t3 | w0      | w0        | w0        |

#### Majority

The "majority" read concern returns data acknowledged by a majority of nodes in the replica set, providing a stronger consistency and durability guarantee. When a read operation is issued with "majority," the read node returns data from its in-memory view of the data at the majority-commit point, offering a higher level of consistency compared to other read concerns like "local" or "available." The majority read concern ensures stronger durability guarantees and prevents reading rollback-prone data.

The following table shows the data returned from the primary and secondary nodes when using the majority read concern.

|   Time   | Primary | Secondary | Secondary |
|----------|---------|-----------|-----------|
| Until t3 | wpre    | wpre      | wpre      |
| t4       | w0      | wpre      | wpre      |
| t5       | w0      | wpre      | wpre      |
| t6       | w0      | wpre      | wpre      |
| t7       | w0      | w0        | wpre      |
| t8       | w0      | w0        | w0        |

The `rs.status().optimes.lastCommittedOpTime` command returns the timestamp of the most recent operation that has been replicated to a majority of replica set members. Data returned by a read operation with the "majority" read concern is guaranteed to have been written before or at this timestamp and acknowledged by the majority of nodes in the replica set.

`ReadConcern.MAJORITY` can be used both inside and outside of transactions. Within a transaction, reads observe majority-committed data only if the transaction is eventually committed using `writeConcern: "majority"`. Otherwise, the snapshot may not reflect data acknowledged by a majority.

Below, I've provided an [example](https://gist.github.com/couragecowardlydog/e0d417c37e7780b591fefa39d9281f26%20file=ReadConcernMajority.java) of `ReadConcern.MAJORITY` being used with MongoDB, which prevents rollback-prone reads.

```java

```bash
package io.gitrebase;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

public class ReadConcernMajority {

    private static final String DATABASE_NAME = "gitrebase";
    private static final String COLLECTION_PRODUCTS = "inventory";
    private static final String MONGO_URI = "mongodb://localhost:27017,localhost:27018,localhost:27019/?replicaSet=rs0";

    public static void main(String[] args) {
        MongoClient client = MongoClients.create(MONGO_URI);
        MongoDatabase database = client.getDatabase(DATABASE_NAME);

        // Reading collection with ReadConcern.MAJORITY
        MongoCollection<Document> products = database.getCollection(COLLECTION_PRODUCTS)
                .withReadConcern(ReadConcern.MAJORITY);

        products.deleteMany(Filters.eq("category", "PIZZA"));

        Document pizza = new Document("_id", "PIZZA_001")
                .append("name", "Cheese Burst Pizza")
                .append("category", "PIZZA")
                .append("price", 350);
        products.insertOne(pizza);

        Document result = products.find(Filters.eq("_id", "PIZZA_001"))
                .first();

        if (result != null) {
            System.out.println("Found pizza: " + result.toJson());
        }
    }
}
```

```

#### Snapshot

The "snapshot" read concern in MongoDB provides a consistent, point-in-time view of data throughout the duration of an operation. It uses WiredTiger's checkpointing and timestamp-based data visibility to enable multi-document transactions with strong, repeatable-read isolation.

If a multi-document transaction uses \\`readConcern: "majority"\`, different reads in the same transaction may still return different results due to interleaved writes. To avoid this, MongoDB provides \\`readConcern: "snapshot"\`, which ensures a stable, point-in-time view of data throughout the transaction. This prevents both non-repeatable reads and phantom reads, enabling full repeatable-read isolation.

In the previous example of a non-repeatable read, the entire sequence of operation can be isolated from the write operation using the MongoDB transaction API with an appropriate read concern.

```java
package io.gitrebase;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

public class InventoryUpdateWithTransaction {

    private static final String DATABASE_NAME = "gitrebase";
    private static final String COLLECTION_PRODUCTS = "products";
    private static final String COLLECTION_ORDERS = "orders";
    private static final String MONGO_URI = "mongodb://localhost:27017,localhost:27018,localhost:27019/?replicaSet=rs0";

    public static void main(String[] args) throws InterruptedException {
        MongoClient client = MongoClients.create(MONGO_URI);
        MongoDatabase database = client.getDatabase(DATABASE_NAME);
        MongoCollection<Document> products = database.getCollection(COLLECTION_PRODUCTS);
        MongoCollection<Document> orders = database.getCollection(COLLECTION_ORDERS);

        products.deleteMany(Filters.eq("category", "PIZZA"));
        Document pizza = new Document("_id", "PIZZA_001")
                .append("name", "Cheese Burst Pizza")
                .append("category", "PIZZA")
                .append("price", 350);
        products.insertOne(pizza);
        System.out.println("Inserted product: " + pizza.toJson());

        // Write to the same collection from another thread
        Thread clientAThread = new Thread(() -> {

            final ClientSession clientSession = client.startSession();
            TransactionOptions txnOptions = TransactionOptions.builder()
                    .readPreference(ReadPreference.primary())
                    .readConcern(ReadConcern.SNAPSHOT)
                    .writeConcern(WriteConcern.MAJORITY)
                    .build();
            TransactionBody txnBody = (TransactionBody<String>) () -> {
                try {
                    System.out.println("Client A: Fetching product ...");
                    Document firstRead = products.find(clientSession, Filters.eq("_id", "PIZZA_001")).first();
                    System.out.println("Client A : " + firstRead.toJson());
                    Thread.sleep(3000);
                    System.out.println("Client A: Placing order ...");
                    orders.insertOne(new Document("orderId", "ORD_001")
                            .append("productId", "PIZZA_001"));
                    System.out.println("Client A: Order placed ");
                    Thread.sleep(1000);
                    System.out.println("Client A: Fetching product ...");
                    Document secondRead = products.find(clientSession, Filters.eq("_id", "PIZZA_001")).first();
                    System.out.println("Client A : " + secondRead.toJson());
                    return "Inserted into collections in different databases";
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            };
            try {
                clientSession.withTransaction(txnBody, txnOptions);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                clientSession.close();
            }
        });

        // Write to the same collection from another thread
        Thread clientBThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
                // Increment pizza price by 10%
                System.out.println("Client B: Incrementing pizza price by 10% ...");
                products.updateMany(Filters.eq("category", "PIZZA"), Updates.mul("price", 1.10));
                System.out.println("Client B: Price updated.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        clientBThread.start();
        clientAThread.start();

        clientAThread.join();
        clientBThread.join();
    }
}
```

Using SNAPSHOT isolation ensures the client observes a consistent view throughout the transaction, even if a concurrent write operation happens outside. The second read shows the same price as the first, despite Client B's concurrent update.

### Write concern

In MongoDB, write concern specifies the level of acknowledgment requested from MongoDB for write operations. It determines how much assurance an application requires before a write operation is considered successful. Write control lets us balance the trade-off between durability, consistency, and latency (the stronger the write concern, the stronger the durability and consistency guarantee). Write concern can be specified at the individual operation for single-document operations and at the transaction level for multi-document transactions

#### Unacknowledged

With an unacknowledged write concern, MongoDB does not need any acknowledgement from the data-bearing nodes to acknowledge a client write operation. This improves write performance but provides no guarantees about the success of the write or durability. Data will be rolled back if the primary steps down before the write operations have been written to the on-disk journal or replicated to any of the secondaries.

#### Acknowledged

Acknowledged write concerns ensure that write operation has propagated to the in-memory or journal of a standalone mongod or the primary in a replica set, before sending an acknowledgment back to the client for successful write. While specifying the write concern `( { w: 1, j: <boolean> } )`, the value of `j` decides whether an acknowledgement to the client is sent before or after journaling. Both write concerns w:0 and w:1 don't guarantee that the write will be made durable in case of a network partition.

#### Majority

The majority write concern requires acknowledgment from the majority of data-bearing nodes, offering the highest level of durability assurance against data loss due to node failures or rollbacks. While the majority write concern offers greater consistency and durability, it adds overhead to overall latency.

| Write Concern ↓ / Journal Ack → |                              j: false/unspecified                              |                                                                           j: true                                                                           |
|---------------------------------|--------------------------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------|
| w: 0                            | No guarantees on write, oplog, or journal                                      | Same as j: false, ***j: true*** has no effect                                                                                                               |
| w: 1                            | Acknowledges writes on primary with no guarantee of replication to secondaries | Written to the primary's journal, but no guarantee of replication                                                                                           |
| w: majority                     | Durable against primary node failure, but not guaranteed to be journaled       | Write is journaled on the primary and replicated to a majority of data-bearing nodes, ensuring durability and fault tolerance across multiple node failures |

Concurrency control and Isolation are foundational for ensuring data integrity in distributed systems like MongoDB. While MongoDB guarantees atomic operations at the document level and leverages MVCC for concurrent write operations, proper selection of read and write concerns is essential to balance availability, consistency, and durability in distributed deployments. The following table illustrates how different combinations of read and write concerns influence availability and consistency requirements.

| Write Concern ↓ / Read Concern → |                     local                     |                  majority                  |                  snapshot                  |
|----------------------------------|-----------------------------------------------|--------------------------------------------|--------------------------------------------|
| w:0                              | Availability: Highest Consistency: Lowest     | Availability: Medium-High Consistency: Low | Availability: Low Consistency: Medium      |
| w:1                              | Availability: High Consistency: Low           | Availability: Medium Consistency: Medium   | Availability: Low Consistency: Medium-High |
| w: majority                      | Availability: Medium-High Consistency: Medium | Availability: Medium Consistency: High     | Availability: Low Consistency: High        |

Unlike traditional databases that enforce a consistency model, MongoDB allows developers to fine-tune the balance between consistency and availability using read and write concerns.

This enables you to explicitly define the level of consistency guarantee that your application needs. Instead of a one-size-fits all approach, MongoDB gives developers the flexibility to prioritize the consistency and availability that applications need, making it an ideal choice for modern applications.
