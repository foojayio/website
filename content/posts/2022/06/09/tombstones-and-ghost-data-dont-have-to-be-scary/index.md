---
title: "Tombstones and Ghost Data Don’t Have to Be Scary!"
slug: "tombstones-and-ghost-data-dont-have-to-be-scary"
date: "2022-06-09T20:36:25+00:00"
lastmod: "2022-06-10T10:13:44+00:00"
description: "Working with tombstones can be one of the more frustrating aspects of Apache Cassandra®. Problems with tombstones can happen when a large number of - by Aaron Ploetz"
canonical: "https://medium.com/building-the-open-data-stack/tombstones-and-ghost-data-dont-have-to-be-scary-with-these-tips-and-tricks-from-datastax-48f3c275b05a"
authors:
  - "aaron-ploetz"
image: "1_g5MhVmTyqdCWG_-LkItmNg.jpeg"
categories:
  - "Apache Cassandra"
  - "Databases"
  - "DataStax"
  - "DevOps"
tags:
related_posts:
  - "a-case-for-databases-on-kubernetes-from-a-former-skeptic"
  - "avoiding-nullpointerexception"
  - "bring-streaming-to-apache-cassandra-with-apache-pulsar"
  - "kubernetes-data-simplicity-getting-started-with-k8ssandra"
frozen: false
---

![](1_g5MhVmTyqdCWG_-LkItmNg-1024x679.jpeg)

***Working with tombstones can be one of the more frustrating aspects of Apache Cassandra®. Problems with tombstones can happen when a large number of deletes occur within a short period of time, resulting in slower queries and disk bloat. This article will discuss tombstones, including the purpose they serve, and strategies/methods on how to avoid them.***{#2508}

[Tombstones](https://cassandra.apache.org/doc/4.0/cassandra/operating/compaction/index.html#why-tombstones) in [Apache Cassandra](https://cassandra.apache.org/_/index.html)*®* are written markers that are created whenever a value is deleted. Their purpose is to indicate that any prior values have been deleted. When a read occurs, it pulls data from the sorted string table (SSTable) files on a node that may contain the data. All prior values for the requested key are returned, ordered by write-time.{#d711}

So, if a particular value has been written and deleted several times, there may be many tombstones and obsolete values which will need to be read and reconciled. Tombstones increase the latency in your queries and can return a frightening amount of "ghost data" (deleted values) you don't want. As a result, tombstones are some of the more frustrating aspects of working with Cassandra.{#d711}

After a tombstone has been in the system for a certain period of time, which is determined by a table's `gc_grace_seconds` (10 days is the default), it is usually removed during the next compaction cycle on that table.{#b8a6}

Of course, the best way to deal with tombstones is to not create them in the first place. There are several ways to accomplish this, and most of them happen in the data model.{#d2b5}

## No DELETEs

The simplest way to avoid tombstones is to never do the one operation guaranteed to create them --- deleting data. Of course, this isn't always possible and will definitely be applicable by use case. But by and large, if you have a data model which doesn't require data to be deleted on a regular basis, you'll avoid most of the pain that leads to tombstones right there.{#9069}

## No writing NULL values

Writing a NULL value into Cassandra creates a tombstone. Having a NULL in a pre-built query variable is probably the most common way that tombstones are inadvertently created in the system. This used to be a bigger problem until the DataStax Java Driver "unset" all NULL values by default in a prepared statement with its driver version 3 and CQL protocol (sometimes referred to as the "native binary" protocol) version 4. With the most recent versions of the [DataStax Java drivers](https://docs.datastax.com/en/developer/java-driver/index.html%20rel=), this stands as a good reminder to always write with [prepared statements](https://docs.datastax.com/en/developer/java-driver/3.0/manual/statements/prepared/).{#9606}

In some use cases, delete operations and their resulting tombstones are unavoidable. The best path forward, in this case, is to adjust the data model and usage pattern so that the fewest possible tombstones are returned.{#4b1d}

## Collection operations

The implementation of [collections](https://www.datastax.com/blog/collections-cassandra) (sets, lists, and maps) is a good addition to the [Cassandra data modeling](https://www.datastax.com/blog/basic-rules-cassandra-data-modeling) paradigm. But it's important to remember that frequently adding, removing, or updating collection items can lead to tombstone generation. When using collections, you'll find success by changing their contents as little as possible.{#0da0}

## Using TTLs

Setting a time-to-live (TTL) on a table is a great way to keep the size of a data set from getting out of control. But it's important to remember that once a TTL activates and deletes the data in question, it creates a tombstone. TTLs are best used with time series models that care more about recent data.{#595c}

    CREATE TABLE messages (
        day text,
        message_time timestamp,
        message_text text,
        PRIMARY KEY (day, message_time)
    ) WITH CLUSTERING ORDER BY (message_time DESC)
        AND default_time_to_live = 2592000};

In the table definition shown above, messages are partitioned by day. Within each partition, messages are "clustered" by `message_time` in descending order. Cassandra uses clustering keys to enforce the on-disk sort order. This way, data can be quickly read sequentially from the disk. With a `default_time_to_live` of one month, old messages will be deleted after that period.{#7e29}

The advantage here is that if the application only really cares about querying the most recent data, it should never query the tombstones. This is important because tombstones are mostly problematic when returned as a part of a result set. They are much less likely to cause problems if they are never queried.{#db0a}

## Compaction strategy selection

The default compaction strategy for Apache Cassandra is `SizeTieredCompactionStrategy`. It makes for a good default because it works well with many use cases and access patterns. However, there are use cases where it makes sense to use either `TimeWindowCompactionStrategy` or `LeveledCompactionStrategy`.{#56e6}

The [TimeWindowCompactionStrategy](https://thelastpickle.com/blog/2016/12/08/TWCS-part1.html) (Dejanovski, 2016) is a good choice when working with log-based time series use cases. The idea is that data gets stored according to a certain time component or "window." This is a good compaction strategy for working with TTLs, because the SSTable files themselves are stored according to the specified time window. This allows the compaction process to remove entire files of old data, which is much more efficient than reconciling individual tombstones while merging the data with other files.{#cab9}

A popular alternative to `SizeTieredCompactionStrategy` is `LeveledCompactionStrategy`. The `LeveledCompactionStrategy` makes sense when read/write patterns are performing reads much more often. Its design is built around providing a very high probability that a read should happen from a single SSTable file. Without getting too deep into the nuances of `LeveledCompactionStrategy`, it tends to remove tombstones easier as the deleted data works its way up through the levels.{#c3a4}
> Note: The decision to change the compaction strategy of a table is not to be taken lightly. It's not something that will mitigate tombstones by itself. The [compaction strategies](https://cassandra.apache.org/doc/latest/cassandra/operating/compaction/index.html) mentioned above were designed for specific use cases and access patterns, and not adhering to those patterns may cause additional problems.

So let's say that you're at a point where a table has a large number of tombstones. Queries are noticeably slowing down and an occasional `TombstoneOverwhelmingException` is being returned. At this point, what can you do?{#d49d}

## `Compaction options`

The default `SizeTieredCompactionStrategy` does provide some options to assist with cleaning up tombstones. The most effective one is tombstone_threshold, which takes a percentage as a value, for example:{#2b5b}

    'tombstone_threshold':'0.3'

In this scenario, a single-SSTable compaction will be initiated when the percentage of expired data in the file exceeds 30%. However, the percentage of expired data does not take the `gc_grace_seconds` setting into account, and only the *eligible* expired data will be removed.{#6b08}

## nodetool garbagecollect

Tombstones can be removed with single-table compaction by invoking:{#a9b8}

    nodetool garbagecollect <keyspace> <table>

This command can be used to reclaim some space and get rid of expired data. It's important to remember that this command is also bound by the `gc_grace_seconds` setting.{#eb8a}
> Note: Be careful not to confuse the `garbagecollect` keyword with the garbage collection process which manages memory use inside the Java heap.

## Lowering gc_grace_seconds

One obvious way to cycle through tombstones faster is to lower the table's value for `gc_grace_seconds`. This has the effect of marking tombstones as eligible for collection in a shorter period of time. By default, `gc_grace_seconds` is set to 10 days (864,000 seconds).{#cb23}

Be **very careful** with this setting. The idea with a 10 day waiting period is that it coincides with the recommendation of running repair operations on a weekly basis. This gives the tombstones ample time to replicate to the nodes responsible for the specific, deleted replicas. If this period is set too low, it is quite possible that not all replicas will receive the tombstone. This is a [common cause of deleted data](https://thelastpickle.com/blog/2016/07/27/about-deletes-and-tombstones.html) (Rodriguez, 2016) "ghosting" it's way back into a result set.{#35dc}
> Note: Never set `gc_grace_seconds` to zero! There is no other way to prevent compaction from cleaning up tombstones before they have had time to properly replicate.

Effectively dealing with tombstones requires upfront planning and an understanding of (both) the business use case and how Cassandra handles deletes internally. After all, the best way to not be affected by tombstone issues is to not create them in the first place.{#7cb6}

Removing problematic tombstones manually can be a challenging task, replete with nuances and without guarantees of success. The organic running of the compaction process is the best way to remove expired data (including tombstones). If possible, letting Cassandra handle this on its own is always the best plan.{#d9b6}

* Be sure that the app code is not writing NULL values.
* Clustering a table by timestamp (in descending order) makes it easier to avoid querying expired or TTL'd data.
* Select a compaction strategy by how well it fits the application's data access patterns, not by how it handles tombstones.
* Setting a table's `tombstone_threshold` can be an effective way to keep eligible tombstone counts down.
* Try to avoid manually running compaction tasks.

*Follow the* [*DataStax Tech Blog*](https://datastax.medium.com/)*for more developer stories. Check out our* [*YouTube*](https://www.youtube.com/channel/UCqA6zOSMpQ55vvguq4Y0jAg)*channel for tutorials and here for DataStax Developers on* [*Twitter*](https://twitter.com/DataStaxDevs)*for the latest news about our developer community.*{#d6cb}

Curious to learn more about (or play with) Cassandra itself? We recommend trying it on the [Astra DB](https://astra.dev/38tAKzB) free plan for the fastest setup.

1. [Apache Cassandra Documentation: Compaction](http://cassandra.apache.org/doc/3.11.3/operating/compaction.html?highlight=tombstones)
2. [DataStax Java Driver for Apache Cassandra](https://docs.datastax.com/en/developer/java-driver/index.html)
3. [DataStax Java Driver v3: Prepared Statements](https://docs.datastax.com/en/developer/java-driver/3.0/manual/statements/prepared/)
4. [DataStax Java Drivers](https://docs.datastax.com/en/developer/java-driver/index.html) (all versions)
5. [Basic Rules of Cassandra Data Modeling](https://www.datastax.com/blog/basic-rules-cassandra-data-modeling)
6. [Collections in Cassandra](https://www.datastax.com/blog/collections-cassandra)
7. [TWCS Part 1 --- What is it and when should you use it?](https://thelastpickle.com/blog/2016/12/08/TWCS-part1.html) (Dejanovski, 2016)
8. [Compaction Strategies in Cassandra](https://cassandra.apache.org/doc/latest/cassandra/operating/compaction/index.html)
9. [About Deletes and Tombstones](https://thelastpickle.com/blog/2016/07/27/about-deletes-and-tombstones.html) (Rodriguez, 2016)
