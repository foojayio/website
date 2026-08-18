---
title: "When Should You Use a Cache With MongoDB?"
date: "2026-05-05T15:23:30+00:00"
lastmod: "2026-05-05T15:23:32+00:00"
description: "From time to time, I'll run a design review for an application being migrated from a relational database onto MongoDB, where the customer shares an architectural diagram showing a caching layer (typically Redis) sitting between the app server and MongoDB."
authors:
  - "amorgan"
image: "Screenshot-2026-04-21-at-1.44.45-PM.png"
categories:
  - "Databases"
  - "Mongo"
tags:
related_posts:
  - "abstracting-data-access-in-java-with-the-dao-pattern"
  - "agents-meet-databases-the-future-of-agentic-architectures"
  - "atlas-online-archive-efficiently-manage-the-data-lifecycle"
  - "atlas-searching-with-the-java-driver"
frozen: false
---

From time to time, I'll run a [design review](https://www.mongodb.com/events/mongodb-schema-design-reviews/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cache-foojay&utm_term=tony.kim) for an application being migrated from a relational database onto MongoDB, where the customer shares an architectural diagram showing a caching layer (typically Redis) sitting between the app server and MongoDB.

I like to keep the architecture as simple as possible---after all, each layer brings its own complexity and management costs---so I'll ask why the caching layer is there. Of course, the answer is always that it's there to speed up data access. This reveals a misunderstanding of both the reason why caching layers were created and what MongoDB provides.

I've yet to finish a design review without recommending that the cache tier be removed.

So to answer the question in the title of this article---when should you use a cache with MongoDB?---the answer is probably never. This article attempts to explain why, but if you get to the end and still think your application needs it, then I'd love to discuss your app with you.

## Why were caches like Memcached \& Redis invented, and why do they thrive?

Caching tiers were introduced because it was too slow for applications to read the required data directly from a relational database.

Does this mean there aren't smart developers working on Oracle, DB2, Postgres, MySQL, etc.? Why couldn't those developers make relational databases fast? The answer is that all those databases were written by great developers who included indexes, internal database caches, and other features to make reading a record as fast as possible.

The problem is that the application rarely needs to read just a single record from the normalised relational database. Instead, it typically needs to perform multiple joins across many tables to form a single business object. These joins are expensive (they're slow and consume many resources). For this reason, the application doesn't want to incur that cost every time they read the same business object. That's where the caching tier adds value---join the normalised, relational data once and then cache the results so that the application can efficiently fetch the same results many times.

There's also the issue of data distribution. Most relational databases were designed 50 years ago when an enterprise would run the database and any applications in a single data center. Fast forward to today, when enterprises and customers are spread worldwide, with everyone wanting to work with the same data. You don't want globally distributed app servers to suffer the latency and expense of continually fetching the same data from a database located on a different continent. You want a copy of the data located locally close to every app server that needs it.

Relational databases were not designed with this data distribution requirement in mind. RDBMS vendors have attempted to bolt on various solutions to work around this, but they're far from optimal. Instead, many enterprises delegate the data distribution to a distributed cache tier.

Note that Redis and Memcached are widely used for session handling for web applications where persistence isn't a requirement. In that case, the cache is the only data store (i.e., not a cache layer between the application and MongoDB). While you can (and people do) use MongoDB for session management, that's beyond the scope of this article.

## So, what's wrong with having a caching tier?

Introducing a caching layer is often a great solution when your database can't deliver the performance and latency your application needs.

However, this extra data tier comes with costs. The obvious ones are the software licenses and hardware required to provide the caching service.

Less obvious is the extra load on developers. It's a new query language (and possibly programming language) to master. What happens when the data in the RDBMS changes? How are those changes propagated to your cache tier?

So, a cache tier has to pay its way by delivering tangible benefits over having your application access the database directly.

## What's different with MongoDB?

The [MongoDB document model](https://mongodb.com/docs/manual/data-modeling/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cache-foojay&utm_term=tony.kim).

In MongoDB, we want you to store your data structured in a way that makes it efficient to quickly satisfy your application's most frequent queries (or those with the toughest SLAs). MongoDB mirrors the structure of objects by letting a single record (document) contain embedded (nested) objects. Support for arrays allows one-to-many and many-to-many relationships without joining multiple collections.

In many cases, the business object required by the application will map to a single MongoDB document. In other cases, it might require multiple documents that can be fetched with a single, indexed lookup.

MongoDB has its own internal LRU (least recently used) cache, so if your document has been accessed recently, chances are it's already in memory. So, as with Redis, MongoDB can satisfy the application's query by fetching a single document/object from memory.

Note that MongoDB supports [joins](https://mongodb.com/docs/manual/reference/operator/aggregation/lookup/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cache-foojay&utm_term=tony.kim), but we try to structure your data to minimize their use.

The other value-add from a caching layer is data locality in distributed architectures. MongoDB has this built in. A [MongoDB replica set](https://mongodb.com/docs/manual/replication/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cache-foojay&utm_term=tony.kim) has a single primary node that handles all writes, together with up to 49 secondary nodes---each with a copy of the data. For the lowest latency queries, you can place secondaries locally at each of your app server locations. MongoDB is responsible for keeping the data in the secondary nodes up to date with the primary, so you don't need to write and maintain any extra synchronization code.

## What does AI think?

The responses created by generative AI are driven by the information that's been published by real people, and so it should represent popular opinion on a topic. I thought it would be interesting to see what an AI has come to understand as conventional wisdom on why people place a cache in front of MongoDB.

I asked ChatGPT 4o this question:

"Explain why I would use a cache layer (such as Redis) rather than having my application read data from MongoDB directly."

I'll summarize and respond to the key benefits of the cache tier identified by ChatGPT:

"Improved Performance. Redis operates entirely in memory, making it significantly faster than MongoDB, which relies on disk I/O for data retrieval." As described above, MongoDB has its own in-memory cache, so the documents you frequently access will be in memory, and no disk access is required.

"Reduced Load on MongoDB. Frequently accessing MongoDB directly for the same data increases query load, which can slow down the database, especially under heavy read traffic." MongoDB is scalable. Extra secondary nodes can be added to the replica set to add extra query bandwidth. [MongoDB sharding](https://mongodb.com/docs/manual/sharding/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cache-foojay&utm_term=tony.kim) (partitioning) can scale data capacity or write throughput horizontally.

"Handling High-Read Traffic. Applications with high read-to-write ratios (e.g., web apps, APIs) benefit from Redis's ability to serve cached data quickly." MongoDB's database cache provides the same benefits without the extra developer effort to synchronize data changes.

"Faster Access to Frequently Used Data. Redis is ideal for caching frequently accessed or hot data (e.g., user sessions, configurations, or product details)." Frequently accessed, hot data will be held in MongoDB's in-memory database cache.

"Lower Latency for Geo-Distributed Applications. By replicating Redis caches closer to end-users, you can avoid high network latency when querying MongoDB from distant locations." Data locality can be solved by placing replicas near your app server sites.

"Support for Expiring Data (TTL). Redis has a built-in Time-to-Live (TTL) feature that automatically removes cached data after a specified duration." MongoDB uses an LRU cache, so any documents that are no longer being queried will be removed from memory if the space is needed for more recently queried data. MongoDB also has [TTL indexes](https://mongodb.com/docs/manual/core/index-ttl/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cache-foojay&utm_term=tony.kim) if you want to remove the documents from the database entirely or [Atlas Online Archive](https://mongodb.com/products/platform/atlas-online-archive/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cache-foojay&utm_term=tony.kim) if you want to move them to cheaper storage.

"Cost Efficiency. Reading from MongoDB repeatedly can be resource-intensive, especially with complex queries, leading to increased infrastructure costs." Your MongoDB schema should be designed so that your important queries don't require complex queries.

"Use Cases for Specialized Data Structures. Redis supports advanced data structures like lists, sets, sorted sets, hashes, and streams, which MongoDB doesn't provide natively." MongoDB supports lists and sets. Hashes can be represented in MongoDB as an array of documents containing key-value pairs (the [MongoDB attribute pattern](https://mongodb.com/blog/post/building-with-patterns-the-attribute-pattern/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cache-foojay&utm_term=tony.kim)). [MongoDB time series collections](https://mongodb.com/docs/manual/core/timeseries-collections/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cache-foojay&utm_term=tony.kim) meet the same needs as Redis streams.

"Resilience and Fault Tolerance. A cache layer can serve as a fallback if MongoDB is temporarily unavailable or under heavy load." MongoDB can scale vertically or horizontally to meet any load demands. [Scaling can be automated when using MongoDB Atlas](https://mongodb.com/docs/atlas/cluster-autoscaling/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cache-foojay&utm_term=tony.kim). MongoDB replica sets provide fault tolerance for both reads and writes.

"Simplified Complex Query Results. MongoDB can take time to compute complex queries (e.g., aggregations, joins) for frequently requested results." Your MongoDB schema should be designed to avoid the need to run complex queries frequently. Results can be stored (cached) in a [MongoDB materialized view](https://mongodb.com/docs/manual/core/materialized-views/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cache-foojay&utm_term=tony.kim), avoiding the need to repeatedly execute the same complex query/aggregation.

Note that the response you get from ChatGPT is heavily skewed by the question you ask. If I change my prompt to "Explain why I shouldn't use a cache layer (such as Redis) rather than having my application read data from MongoDB directly," ChatGPT is happy to dissuade me from adding the cache layer, citing issues such as increased system complexity, data consistency issues, performance for write-heavy workloads, cost, query flexibility, maintenance and reliability, small data sets (where the active data set fits in MongoDB's cache), and real-time reporting.

## Summary

A cache layer can add much value when your RDBMS cannot deliver the query performance your application demands. When using MongoDB, the database of record and cache functionality is combined in a single layer, saving you money and developer time.

A distributed cache can mitigate shortfalls in your RDBMS, but MongoDB has a built-in distribution.

Respond to this article if you still believe your application would benefit from a cache layer between your application and MongoDB. I'd love to take a look.

## Learn more about MongoDB design reviews

[Design reviews](https://www.mongodb.com/events/mongodb-schema-design-reviews/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=cache-foojay&utm_term=tony.kim) are a chance for a design expert from MongoDB to advise you on how best to use MongoDB for your application. The reviews are focused on making you successful using MongoDB. It's never too early to request a review. By engaging us early (perhaps before you've even decided to use MongoDB), we can advise you when you have the best opportunity to act on it.

This article explained how designing a MongoDB schema that matches how your application works with data can meet your performance requirements without needing a cache layer.
