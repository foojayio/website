---
title: "MongoDB ACID Transactions With Java"
slug: "mongodb-acid-transactions-with-java"
date: "2025-07-23T14:39:45+00:00"
lastmod: "2025-07-23T14:43:58+00:00"
description: "MongoDB’s support for multi-document ACID transactions gives you flexibility when single-document atomicity isn’t enough."
authors:
  - "tim-kelly"
image: "mongologo.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
tags:
related_posts:
  - "how-to-identify-the-underlying-causes-of-connection-timeout-errors-for-mongodb-with-java"
  - "java-on-azure-tooling-update-september-2022"
  - "java-virtual-threads-in-action-optimizing-mongodb-operation"
  - "understanding-bson-a-beginners-guide-to-mongodbs-data-format"
frozen: false
---

A database is constantly under pressure. Real-time applications, exponential growth of data, and multiple operations may hit the system at once. Without coordination, this can lead to race conditions, conflicts, and ultimately, the dreaded inconsistent data! To address this, MongoDB supports [transactions](https://learn.mongodb.com/courses/mongodb-transactions?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+acid&utm_term=tim.kelly).

Transactions let us group multiple operations together so they either all succeed or none take effect, a concept known as [atomicity](https://www.mongodb.com/docs/manual/reference/glossary/#std-term-atomic-operation?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+acid&utm_term=tim.kelly). MongoDB's document model already provides atomicity at the single-document level, which covers a lot of use cases. But when you need atomicity across multiple documents or collections, transactions are the answer.

In this tutorial, you'll learn how MongoDB supports ACID transactions, when to use them, and how to implement them in Java using the [Java sync driver](https://www.mongodb.com/docs/drivers/java/sync/current/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+acid&utm_term=tim.kelly).

## What are MongoDB ACID transactions and when should you use them?

A MongoDB transaction is a way to execute a sequence of operations as a single atomic unit. This means if any part fails, the entire set of operations is rolled back. This is what ACID guarantees:

* **Atomicity**: All or nothing.
* **Consistency**: Data integrity is preserved.
* **Isolation**: Concurrent operations don't interfere.
* **Durability**: Once committed, changes survive crashes.

MongoDB gives us these guarantees for multi-document transactions starting in version 4.0 on replica sets and 4.2 on sharded clusters, going as far back as 2018.

### But do you really need a transaction?

[Data modelling best practices](https://www.mongodb.com/resources/basics/databases/data-modeling?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+acid&utm_term=tim.kelly) in MongoDB recommend embedding related data in a single document. For example, rather than storing a user and their addresses in separate collections, you'd typically embed the addresses inside the user document. One of the many benefits of this approach is it makes most operations atomic without needing transactions.

However, there are valid reasons to go beyond single-document operations:

* Moving money between accounts
* Creating and linking resources across collections
* Coordinating writes across tenants or services

In those situations, transactions are the right tool.

## Transaction APIs: Callback vs core

Before we dive into the fund transfer example, it's important to understand how MongoDB exposes transactions in the Java driver. There are two main ways to run transactions:

* [Callback API](https://www.mongodb.com/docs/manual/core/transactions-in-applications/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+acid&utm_term=tim.kelly#callback-api): The higher-level option that handles retries and commit errors for us, and usually the way to go.
* [Core API](https://www.mongodb.com/docs/manual/core/transactions-in-applications/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+acid&utm_term=tim.kelly#core-api): The lower-level option that gives us full control over the transaction lifecycle, but more the more niche cases.

Both APIs use sessions under the hood and offer full ACID semantics. The difference lies in how much responsibility we, the developer, take on.

### When to use the callback API

The callback API is ideal when our transaction logic is simple and self-contained. We write our operations in a lambda, and the driver handles the rest---from including starting the transaction to committing it, and even retrying if needed. Below is a little code snippet to show what this might look like:

```
try (ClientSession session = client.startSession()) {
    session.withTransaction(() -> {
        // Your transactional logic here
        return null;     
    }); 
}
```

This is the recommended approach for most applications. It keeps our code clean and simple, and avoids the subtle edge cases of manual session and transaction management.

It's recommended when:

* Your logic fits in a single method or lambda.
* You're working with predictable transaction boundaries.
* You want built-in retry handling without a lot of boilerplate (it's Java, we have enough boilerplate as it is).

### When to use the core API

The core API gives us more control over how a transaction starts, commits, and aborts. We'll manage everything manually, which means more flexibility. But as always, with great power comes great responsibility.

```
ClientSession session = client.startSession();
try {
    session.startTransaction();

    // Your logic

    session.commitTransaction();
} catch (Exception e) {
    session.abortTransaction();
} finally {
    session.close();
}
```

Not a lot more complicated than the callback API, just a more hands-on approach. The core API can be the right choice for you when:

* You need fine-grained control over the transaction lifecycle.
* The logic spans multiple methods or external systems.
* You're building custom retry/backoff logic.
* You want to log or handle partial failures explicitly.

## Setting up: A simple banking example

Let's build a simple banking demo. We'll set up two accounts: Alice and Bob. Then, we'll transfer money from one to the other, and ensure both the debit and credit happen atomically.

Our Java program connects to a MongoDB database, sets up the data, and invokes a `transferFunds` function that wraps everything in a transaction.

### Prerequisites

We need a few things in order to get started:

* Java 17+ (I use 24): [Download Java](https://adoptopenjdk.net/) or use your preferred distribution.
* Maven 3.9+: [Download Maven](https://maven.apache.org/download.cgi).
* MongoDB Atlas cluster: A [free tier M0](https://www.mongodb.com/cloud/atlas/register/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+acid&utm_term=tim.kelly) will work perfectly.

All of the code below is available on [GitHub](https://github.com/timotheekelly/mongodbacid).

In our Maven project, we need to add our MongoDB driver dependency. Open the `pom.xml` and add the following dependency for the Java sync driver:

```
<dependencies>
    <dependency>
        <groupId>org.mongodb</groupId>
        <artifactId>mongodb-driver-sync</artifactId>
        <version>5.4.0</version>
    </dependency>
</dependencies>
```

## Connecting and seeding data

Before we can demonstrate transactions, we need some data to work with. In this section, we connect to MongoDB, set up a `bank` database, and create a simple `accounts` collection with two users: Alice and Bob.

In our application, we are going to add all our code to the one class. First, let's start with the setup:

```
public class TransactionExample {
    public static void main(String[] args) {
        String uri ="YOUR-CONNECTION-STRING";

        try (MongoClient client = MongoClients.create(uri)) {
            MongoDatabase db = client.getDatabase("bank");
            MongoCollection<Document> accounts = db.getCollection("accounts");

            // Drop and re-create the collection with two example users
            accounts.drop();
            accounts.insertMany(List.of(
                    new Document("_id", 1).append("user", "Alice").append("balance", 1000),
                    new Document("_id", 2).append("user", "Bob").append("balance", 500)
            ));

            // Call the transaction logic
            transferFunds(client, 1, 2, 200);
        }
    }
}
```

All we are doing here is reading the MongoDB connection URI from an environment variable. Always good practice to keep secrets out of our code!

We create a `bank` database and get the `accounts` collection. We call `drop()` to clear any previous data. This ensures each run starts fresh (for our demo purposes). Then, we insert two user accounts: Alice starts with 1000, Bob with 500. Finally, we call `transferFunds()` to run the transaction (we'll make this next).

Now, let's look at how we implement that transaction, using the callback API.

## Writing a transaction (with the callback API)

MongoDB requires that all transactions happen inside a session. When using the callback API, we pass a lambda (or Java function) to the driver's `withTransaction()` method, and the driver takes care of the rest, including starting, committing, and retrying the transaction if needed.

Here's how the fund transfer logic works:

```
public static void transferFunds(MongoClient client, int fromId, int toId, int amount) {
    try (ClientSession session = client.startSession()) {
        session.withTransaction(() -> {
            MongoCollection<Document> accounts = client.getDatabase("bank").getCollection("accounts");

            // Step 1: Read the sender’s account
            Document sender = accounts.find(session, eq("_id", fromId)).first();
            if (sender == null || sender.getInteger("balance") < amount) {
                throw new RuntimeException("Insufficient funds or invalid sender.");
            }

            // Step 2: Deduct the amount from sender
            accounts.updateOne(session, eq("_id", fromId),
                new Document("$inc", new Document("balance", -amount)));

            // Step 3: Credit the amount to receiver
            accounts.updateOne(session, eq("_id", toId),
                new Document("$inc", new Document("balance", amount)));

            System.out.println("Transaction committed successfully.");
            return null; // Required by withTransaction
        });
    } catch (Exception e) {
        System.err.println("Transaction failed: " + e.getMessage());
    }
}
```

We start with a `startSession()` to begin a client session. We must use the same session in all read and write operations inside the transaction.

We can `withTransaction()` to wrap our business logic. The driver will:

* Automatically call `startTransaction()`.
* Handle retries if there's a transient error (like a replica set failover).
* Attempt to commit, and if that fails with an uncertain result, retry the entire transaction.

Now, for our simple business use case example, inside the lambda, we:

1. Read the sender's document and check their balance.
2. Deduct from their balance.
3. Add to the receiver's balance.

If the sender has insufficient funds, we throw an exception. This causes the transaction to be aborted. Finally, we print a success message or handle errors if the transaction fails entirely. Super simple and straightforward thanks to our trusty callback API.

## Supported operations

Most of the common MongoDB operations can be included in a transaction:

* `find()` (read)
* `insertOne()` / `insertMany()`
* `updateOne()` / `updateMany()`
* `deleteOne()` / `deleteMany()`
* Simple aggregation queries (no `$out` or `$merge`)

That being said, we can't do everything. There are some limitations to operations that can be run in our transactions, such as:

* Creating collections or indexes inside a transaction.
* Using capped collections or change streams.
* Using `$out` or `$merge` aggregation stages.

We have a comprehensive list of supported operations in [our docs](https://www.mongodb.com/docs/manual/core/transactions/#transactions-and-operations?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+acid&utm_term=tim.kelly), so make sure to check out what transactions can do before implementing them in your application.

## Transactions in sharded clusters

Transactions are also [supported across shards](https://www.mongodb.com/docs/manual/core/transactions-sharded-clusters/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+acid&utm_term=tim.kelly). This means we can span documents across multiple shards and still keep full ACID guarantees.

Behind the scenes, the driver and [`mongos`](https://www.mongodb.com/docs/manual/reference/program/mongos/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=java+acid&utm_term=tim.kelly) coordinate the transaction. The process uses a two-phase commit:

1. All involved shards prepare to commit.
2. If all agree, they commit. Otherwise, everything is rolled back.

This comes at a performance cost, so avoid sharded transactions unless you truly need them. Good schema design often eliminates the need.

## Conclusion

MongoDB's support for multi-document ACID transactions gives you flexibility when single-document atomicity isn't enough. In most applications, considerate document design will reduce the need for them. But when you do need that extra level of consistency, MongoDB transactions are there and ready.

Whether you're building financial systems, multi-step workflows, or audit-sensitive logic, MongoDB is ACID compliant, and with Java, gives you the tools to handle complex interactions safely.

If you found this tutorial useful, make sure to check out my tutorial [Understanding BSON for Java Developers](https://dev.to/mongodb/understanding-bson-for-java-developers-a-beginners-guide-to-mongodbs-data-format-3ddp).
