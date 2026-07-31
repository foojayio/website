---
title: "How to Choose a Cache: Capabilities | Foojay.io Today"
slug: "choosing-a-cache-1"
date: "2021-10-26T13:20:11+00:00"
lastmod: "2023-02-03T13:39:33+00:00"
description: "Like in many design decisions, a cache is a trade-off. Caching is a trade-off where you accept stale data to have them available/fast."
canonical: "https://blog.frankel.ch/choose-cache/1/"
authors:
  - "nicolas-frankel"
image: "pexels-scott-webb-1544944.jpg"
categories:
  - "Performance"
  - "Research"
tags:
related_posts:
  - "a-list-of-cache-providers"
  - "the-right-feature-at-the-right-place"
  - "web-caching-server"
frozen: false
---

Today, I'd like to provide some help on how to choose a cache solution. I will organize it into two parts:

* In this post, we will list what features a cache must have and which ones it can optionally provide. Most criteria are general and can be used regardless of the tech stack, while a couple is specific to the JVM.
* In the second part, I'll list providers and verify their respective capabilities

Why cache? {#h2-0-why-cache}
----------------------------

First, let's bust a common myth. Using a cache is not the sign of a badly-designed system *per se*, though it might be the case. Like in many design decisions, a cache is a trade-off.

My favorite example is an e-commerce shop implemented via a microservice architecture. Each capability is a micro-service:

* Catalog
* Cart
* Checkout
* Pricing
* Payment

Now, imagine that the user has items in their cart and clicks to checkout. Server-side, the *checkout* service sends a request to the *pricing* service to get a quote for the cart. At this point, we have two requirements:

1. Pricing data must be **available**: if the pricing service is down, the checkout will fail as well as the sale.
2. Pricing data must be available **fast** : if the service is up, but the user waits too long, they may give up. While the term "long" is subjective, a 100ms latency has a [definitive impact on sales](https://www.gigaspaces.com/blog/amazon-found-every-100ms-of-latency-cost-them-1-in-sales).

From a scientific point of view, wrong data is terrible. From an e-commerce one, it's better to sell at a slightly outdated price than to lose sales.

Hence, caching is a trade-off where you accept stale data to have them available/fast.

Mandatory cache features {#h2-1-mandatory-cache-features}
---------------------------------------------------------

You are probably familiar with the quote, "Don't roll your own cryptography library". It hints that designing such a library may look simple at first glance, but chances are you'll make a significant security mistake if you're not a security expert - and even so. You shouldn't design your own cache either, but for slightly different reasons.

You might think that a cache is just an In-Memory Key-Value Store. It's what a hashtable data structure precisely is. Depending on the language, the structure has a different name: [map](https://golang.org/ref/spec#Map_types) in Go, [dictionaries](https://docs.python.org/3/tutorial/datastructures.html#dictionaries) in Python, `HashMap` in [Java](https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html) and [Rust](https://doc.rust-lang.org/std/collections/struct.HashMap.html), [Hash](https://ruby-doc.org/core-3.0.2/Hash.html) in Ruby, etc. Whatever the stack, we can model a cache with such structures.

As a junior developer, I believed it as well, but I've changed my mind since then. A professional cache provides additional features that a mere hashtable doesn't.

### Size limit {#h3-2-size-limit}

Let's start with a simple feature.

The longer an application stays up, the bigger its cache will potentially grow. Depending on the exact usage, e.g., if one caches many entries with different keys, it can grow even more. An unbounded cache will compete with your application regarding memory usage, up to the point where no memory will be available anymore. That's something you want to avoid!

### Eviction strategy(ies) {#h3-3-eviction-strategy-ies}

When a cache has hit its size limit, which entry do we remove when a new entry arrives? Choosing the entry to remove is known as the **eviction** strategy. A couple of such strategies are pretty widespread:

|  Name  |            Evict the entry...             |                                                                                                                                     Requirement                                                                                                                                     |
|--------|-------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Random | ... at random                             |                                                                                                                                                                                                                                                                                     |
|        | ... that was first added to the cache     | Keep hashtable entries in a linked list to support insertion-order traversal                                                                                                                                                                                                        |
|        | ... that was _last_ added to the cache    | Keep hashtable entries in a stack structure to support reverse insertion-order traversal                                                                                                                                                                                            |
|        | ... that was the least recently accessed  | * Store an additional timestamp field with each entry * **and** update it every time it's accessed * **and** * use a sampling algorithm to find the approximately lowest timestamp * **or** use an ordered data structure instead of hashtable to find the exactly lowest timestamp |
|        | ... that is the least frequently accessed | * Store an additional counter field with each entry * **and** increment it every time it's accessed * **and** * use a sampling algorithm to find the approximately lowest count * **or** use an ordered data structure instead of hashtable to find the exactly lowest count        |

You can find other possible strategies on [Wikipedia](https://en.wikipedia.org/wiki/Cache_replacement_policies).

### Time-To-Live {#h3-4-time-to-live}

You might know about the quote:
> There are two hard things in computer science:
>
> 1. Cache invalidation
> 2. Naming things
> 3. And off-by-one errors

It relates to how long the cache considers an entry valid before it removes it. When you add an entry to the cache, you should set the duration after it becomes stale.

A possible implementation is to add a field to each entry: the timestamp when the entry will expire (current time + ). A thread may occasionally visit entries and remove the expired ones eagerly. Alternatively, the cache may evict the expired entries lazily when it needs more space.

Other criteria {#h2-5-other-criteria}
-------------------------------------

Other criteria are optional but still worthy of consideration. Here they are, in no particular order.

### Configuration {#h3-6-configuration}

While configuration is not a feature, it impacts the *developer experience*. As such, it should be a part of any analysis regarding the choice of a cache.

Some cache may be able to run out-of-the-box with sensible defaults, but others may require explicit configuration. You probably need to configure a couple of parameters in all cases, such as the size limit.

Two options are possible: file-based configuration and programmatic configuration. Of course, a third option is to provide both.

### Integration with cache abstractions {#h3-7-integration-with-cache-abstractions}

The JVM ecosystem has an official Cache API, known as [JCache](https://github.com/jsr107/jsr107spec), or 107. It's a specification with an API that describes four annotations, *i.e.* , `@CacheResult`, `@CachePut`, `@CacheRemove`, and `@CacheRemoveAll`. Vendors are to implement the specification.

The Spring framework is pretty widespread in the JVM ecosystem. It also provides a caching API. Historically, it predates JCache. While different, the API is very similar to JCache's. Spring offers out-of-the-box integration code for a couple of caches, while a couple of others do provide Spring integration.

### Caching patterns {#h3-8-caching-patterns}

I've described several caching patterns in the [following talk](https://youtu.be/na2HqjBexbU).

Because it's pretty long, here's a summary:

* Cache-Aside
* Cache-Through
* Read-Through
* Refresh-Ahead
* Cache-Ahead

Generally, people start with Cache-Aside, *i.e.*, the application orchestrates the reads/writes between the cache and the source of truth. However, a cache's true power lies in the more advanced patterns.

### Distributed vs. local {#h3-9-distributed-vs-local}

Early caches shared the same runtime as the application. Then, architects designed caches that ran in their process. In parallel, you can choose from single-node and distributed caches, caches made out of nodes belonging to the same cluster.

The idea behind a distributed cache is to pool multiple nodes together to appear as a single storage unit. If one needs more storage, one adds more nodes. It's the principle behind *horizontal scaling*.

While conceptually simple, it opens a lot of new options. For example, you can *replicate* entries across several nodes so that the failure of a node doesn't mean data loss. Another possible capability is to put entries on specific nodes based on a property of the entry: this capability is known as *sharding*. This way, finding an entry becomes faster as the cache doesn't need to request each node for data but knows which node the data is located on. Of course, the cluster can provide both replication and sharding.

In addition, with a big enough storage capacity, one can use the cache as an in-memory database. A cache is a key-value store: the usual use case is to retrieve an entry by its key. Historically, databases have had a much larger scope, and provided querying capabilities, *i.e.* , `SELECT * FROM Foo`. Hence, a distributed cache can also offer such capabilities via a dedicated API or a SQL-like syntax.

Once the cache is able to pool memory across nodes, it can pool s as well. At this point, the cache has become a *data grid*. One can send tasks, which the cluster executes in parallel across its nodes. Most importantly, the cache can ensure the tasks run close to the data they access, eliminating network traffic.

With a distributed cache, the architecture is client-server: the application is the client; the cache is the server. To maximize your investments, you will probably want to share your data across multiple clients. Clients can belong to different languages, Java and JVM languages, but also others: C#, C, C++, Ruby, Python, Go, Rust, Erlang, etc. It's worth checking which bindings the cache offers regarding the languages you're using.

Of course, none of these features are "free". A distributed cache is a distributed system and comes with [all their pitfalls](https://www.cs.fsu.edu/~xyuan/cop5611/lecture2.html). One important criterion to look into is how nodes form a cluster over the network. For example:

* Is there an auto-discovery mechanism?
* If yes, can it be disabled?
* Can you configure more than one cluster on the same network?
* Does it work on Kubernetes?
* etc.

### Non-blocking API {#h3-10-non-blocking-api}

The goal of a cache is to improve performance, as it's faster to access local in-memory data than data on disk or over the network. If data access takes long, either reads or writes, blocking slows down the whole client code. To solve this issue, caches can provide a non-blocking .

You need to consider several aspects; the most important one is the API used by the cache. For example, `CompletableFuture` requires Java 8. Depending on the stack you're using, you might favor a cache that integrates with RxJava, Project Reactor, Kotlin coroutines, or any combination thereof.

Standard project's health indicators {#h2-11-standard-project-s-health-indicators}
----------------------------------------------------------------------------------

Besides all criteria mentioned above, I'd advise you to consider indicators that should be part of every product evaluation:

* **License**: mainly Open Source vs. commercial, but depending on your usage, not every Open Source license is compatible
* **Pricing**, if commercial
* **Project maturity**: check the project's inception date. The reasoning is that you can probably rely more on an "old" project than on one created yesterday.
* **Activity** :
  * Number of *core* committers - the [Bus Factor](https://en.wikipedia.org/wiki/Bus_factor)
  * Number of non-core committers
  * Committers' commit history
  * Number of open issues
  * Median time to fix them
  * If a core committer stopped working on the project
  * etc.
* **Documentation** : while this term is very generic, good documentation is made of *reference material* , *tutorials* , *how-to guides* and *explanations* . If you have never stumbled upon these terms before, read this [page](https://documentation.divio.com/) or watch this [video](https://www.youtube.com/watch?v=t4vKPhjcMZg). I only started to get documentation after watching the talk.
* **Community**: how large is it? How active? How helpful?
* **Support**: what are the support channels? Stackoverflow? Google Groups? Slack? How often do questions get an answer?
* etc.

Conclusion {#h2-12-conclusion}
------------------------------

In this post, I described several criteria on which to base your choice of cache provider. I'll attempt to list and compare the most common Open Source cache providers in the JVM ecosystem in the next post.

Many thanks to my colleague Marko Topolnik for his review.

**To go further:**

* [Wikipedia's page on cache](https://en.wikipedia.org/wiki/Cache_(computing))
* [List of cache replacement policies](https://en.wikipedia.org/wiki/Cache_replacement_policies)
* [A Guided Tour of Caching Patterns (video)](https://www.youtube.com/watch?v=na2HqjBexbU)
* [JCache](https://hazelcast.com/glossary/jcache-java-cache/)
* [Spring's Cache Abstraction](https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/integration.html#cache)

*Originally published at [A Java Geek](https://blog.frankel.ch/choose-cache/1/) on October 24^th^, 2021*

*[JSR]: Java Specification Request
*[CPU]: Central Processing Unit
*[LFU]: Least Frequently Used
*[API]: Application Programming Interface
*[FIFO]: First In First Out
*[TTL]: Time-To-Live
*[LRU]: Least Recently Used
