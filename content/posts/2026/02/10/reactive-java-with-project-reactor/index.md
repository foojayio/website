---
title: "Reactive Java with Project Reactor"
slug: "reactive-java-with-project-reactor"
date: "2026-02-10T14:32:03+00:00"
lastmod: "2026-02-10T14:32:05+00:00"
description: "Over the past decade, the Java ecosystem has gradually abandoned the idea that increasing the number of threads is the scalable solution to growing load. Cloud-native implementations, containerized workloads, and high-I/O applications have highlighted the inefficiencies of the traditional synchronous thread-per-request model.Reactive programming is not a panacea, a miracle solution. Certainly, it does not make applications “faster” by default. What it offers, when applied correctly, is the ability to predict behavior under load, better resource utilization, and explicit control over data flow. For systems that handle high concurrency, streaming data, or variable traffic patterns, these characteristics are hugely relevant.Project Reactor has become the de facto standard for reactive libraries in the Java ecosystem. This is due to its strong integration with Spring WebFlux and Spring Data. In combination with MongoDB's Reactive Streams driver, it allows you to build non-blocking end-to-end pipelines, from the HTTP layer to the database."
authors:
  - "matteo-rossi"
image: "4201559.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
tags:
related_posts:
  - "abstracting-data-access-in-java-with-the-dao-pattern"
  - "agents-meet-databases-the-future-of-agentic-architectures"
  - "atlas-searching-with-the-java-driver"
  - "best-practices-for-deploying-mongodb-in-kubernetes"
frozen: false
---

**Introduction: Why Reactive Java Still Matters**

Over the past decade, the Java ecosystem has gradually abandoned the idea that increasing the number of threads is the scalable solution to growing load. Cloud-native implementations, containerized workloads, and high-I/O applications have highlighted the inefficiencies of the traditional synchronous thread-per-request model.

Reactive programming is not a panacea, a miracle solution. Certainly, it does not make applications "faster" by default. What it offers, when applied correctly, is the ability to predict behavior under load, better resource utilization, and explicit control over data flow. For systems that handle high concurrency, streaming data, or variable traffic patterns, these characteristics are hugely relevant.

Project Reactor has become the de facto standard for reactive libraries in the Java ecosystem. This is due to its strong integration with Spring WebFlux and Spring Data. In combination with MongoDB's Reactive Streams driver, it allows you to build non-blocking end-to-end pipelines, from the HTTP layer to the database.

This article focuses on the architectural concepts underlying Reactor, with particular attention to:

* Reactive Streams and their contract
* Managing backpressure appropriately
* Practical uses of the MongoDB reactive driver to build high-performance Java architectures

This article is not an introduction to the basics of reactive programming. It assumes that the reader already knows what Flux and Mono are and is more interested in understanding why and when these abstractions make sense at the system level.

### Reactive Streams: The Contract Behind Reactor

Reactive Streams is at the heart of the Reactor Project. Reactor is not just another asynchronous library. It is a concrete implementation of a standardized protocol for asynchronous stream processing with non-blocking backpressure.

The specification defines four main interfaces:

* Publisher\<T\> -- produces data
* Subscriber\<T\> -- consumes data
* Subscription -- mediates demand and cancellation
* Processor\<T, R\> -- acts as both subscriber and publisher

Although Reactive Streams defines the Processor\<T,R\> interface as a combination of Publisher and Subscriber, in the latest versions of Project Reactor (since version 3.5.0) all Processors are deprecated and scheduled for removal. The Reactor team recommends using the new Sinks API, which offers safer and clearer ways to produce signals in reactive streams.

What is important from an architectural point of view is not the API itself, but the contract it imposes:

* Data flows from producer to consumer asynchronously
* Consumers explicitly signal demand via request(n)
* Producers must never emit more data than requested
* Cancellation is explicit and propagates upstream

This demand-driven model is very different from traditional push systems. In this case, consumers control the flow, rather than being flooded by producers. This is the fundamental feature that makes backpressure possible and is the reason why interoperability between libraries is possible. The reactive driver of MongoDB, Reactor, RxJava, and other compliant implementations can all use the same pipeline without any adapters that interrupt flow control.

Reactive Streams shifts the complexity of implicit thread management to explicit flow control. This problem is much more manageable at scale.

### Project Reactor Core Concepts

Project Reactor is based on Reactive Streams, but adds many operators and conventions. To properly understand how it works, it is useful to take another look at some of the fundamental concepts.

#### **Mono** and **Flux** Are Semantic Types

Mono and Flux are often explained as "0-1" and "0-N" relationships, but they are actually better understood as semantic contracts:

* Mono models a deferred calculation in the future that may or may not produce a single value.
* Flux models a potentially unlimited stream of values over time.

The choice between the two is based not only on cardinality, but also on intent. Returning a Flux from an API communicates that the consumer must be prepared to handle streaming behavior, even if the current implementation only emits a few elements.

#### Cold vs Hot Publishers

Most Reactor pipelines are cold by default: data production starts at the time of subscription. This feature is essential for creating reproducible and testable streams and is well suited to operations with specific, ever-changing requirements, such as database queries.

Hot producers, on the other hand, represent shared data sources (event streams, message brokers). Mixing hot and cold producers together without a clear architectural boundary very often causes insidious bugs and backpressure issues.

#### Lazy Execution and Subscription Time

In Reactor, nothing succeeds until someone subscribes. This laziness allows pipelines to be put together declaratively. However, this means that problems that occur, for example, during bean initialization, are an architectural problem.

A common mistake is to use subscribe() imperatively within the code exposing a service. Best practices state that subscription should be handled by the outermost layer (e.g., the HTTP runtime) and not by the domain logic.

#### Threading and Schedulers: No Magic Involved

Reactor does not introduce its own thread-per-task model. By default, execution takes place on the calling thread, unless it is explicitly moved via a Scheduler.

The most commonly used schedulers are:

* parallel() -- CPU-bound work
* boundedElastic() -- blocking or I/O-bound tasks
* single() -- serialized execution

From an architectural point of view, the golden rule is as follows: schedulers are an escape route, not a default setting. Excessive use of publishOn and subscribeOn often indicates that blocking code has become part of a non-blocking reactive pipeline.

### Backpressure: The Hard Part

Backpressure is normally described as the ability to slow down producers. Although technically correct, this definition does not fully explain its true architectural impact.

In Reactive Streams, backpressure is not optional. Each subscriber controls how many elements it is ready to process via request(n). If the demand is zero, the producer must stop emitting. This mechanism has profound implications:

* Memory usage becomes limited by demand.
* Slow consumers no longer force buffering by default.
* The load is naturally distributed evenly across the system boundaries.

#### Backpressure Strategies in Reactor

Reactor provides several operators to deal with situations where upstream cannot honor downstream demand:

* onBackpressureBuffer() -- trades memory for throughput
* onBackpressureDrop() -- drops excess elements
* onBackpressureLatest() -- keeps only the most recent element

Every strategy is an architectural choice. For example, buffering may be fine for telemetry data, but it is risky or counterproductive for APIs that interact with users. Losing data may be an acceptable option for systems that capture metrics, but it is not acceptable for systems that handle financial transactions.

Backpressure is not something to consider after the design is complete. It must be designed from start to end, otherwise it could turn into hidden queues and unpredictable latency spikes.

### Reactive Does Not Mean Faster

Performance is one of the most common misconceptions about reactive systems. Reactive applications are often expected to be faster, when in fact their main advantage is predictable behavior under load.

Reactive systems tend to:

* Use fewer threads
* Avoid context switching
* Maintain stable latency under high concurrency

However, these types of applications can exhibit higher latency under low load than imperative systems, due to the overhead introduced by scheduling activities.

Some of the most common mistakes are:

* Blocking calls within reactive pipelines
* Using too much flatMap without limiting concurrency
* Considering Reactor as "asynchronous imperative code"

These issues not only degrade performance, but also break the reactive contract and often negate the benefits of backpressure.

### MongoDB Reactive Driver: Architecture Overview

MongoDB offers three main Java drivers:

* Synchronous
* Asynchronous
* Reactive Streams

The Reactive Streams driver is based on the asynchronous driver, but offers a fully compatible Reactive Streams API. This is an important difference, allowing MongoDB to participate in backpressure-sensitive pipelines.

Internally, the driver relies on non-blocking I/O operations and event loops, with connection pooling handled asynchronously. Queries are executed lazily, and results are streamed via cursors rather than preloaded into memory.

For architects, this means that MongoDB can become a non-blocking participant in reactive communication, rather than a bottleneck.

### Integrating MongoDB Reactive Driver with Project Reactor

Project Reactor integrates seamlessly with MongoDB's reactive driver because they both use the same protocol. A MongoDB query returns a Publisher \<Document\> that can be directly adapted to a Flux. From there, the entire pipeline remains non-blocking.

A clear advantage of the architecture is query streaming. Instead of loading all results into memory, documents flow downstream on demand. This is particularly useful for:

* Large collections
* Export jobs
* Streaming APIs

Cursor behavior is an implementation detail, while backpressure spreads naturally from the consumer to the database.

### Performance Considerations with Reactive MongoDB

Using the reactive driver does not automatically result in better performance. There are a number of factors that need to be taken into account;

#### Backpressure End-to-End

The backpressure mechanism only works if all levels through which the data flow passes comply with it. If buffering is introduced at the HTTP level or you insert a Flux into a list too early in the flow, the chain is broken and pressure is created on the memory again.

#### Batch Size and Fetch Strategy

MongoDB cursors retrieve data in batches. The batch size you choose clearly affects latency, memory usage, and network efficiency. When designing reactive systems, it is essential to consider this trade-off, as it becomes more apparent and important.

#### Query Shape and Indexing

Reactive pipelines tend to amplify inefficiencies in query design. When scanning large amounts of data with poorly indexed queries, performance will continue to be slow, regardless of whether the driver is non-blocking. Potentially, in the case of high-throughput applications, slow, poorly indexed queries make reactive applications slower than traditional synchronous applications.

#### Observability

Reactive systems require different observability strategies. The number of threads, a metric often used to evaluate the performance of traditional Java systems, becomes less significant. It is therefore essential to observe metrics such as:

* Demand rate
* Queue size
* Event loop saturation

These metrics thus become critical signals of the health of the system.

### Reactive Architecture Patterns with MongoDB

MongoDB Reactive works particularly well with certain architectural models and patterns:

* Highly concurrent API requests/responses
* Streaming endpoints (server-sent events, WebSockets)
* Event-driven pipelines

In many real-world systems, reactive and imperative components coexist. For example, it is very common to find backend-for-frontend layers built entirely with reactive models.

Defining clear boundaries and architectural blueprints is certainly preferable to imposing reactive paradigms everywhere.

### When NOT to Use Reactive MongoDB

Reactive programming, along with its benefits, comes with real costs:

* Greater cognitive load
* More complex debugging
* Steeper learning curve for teams

For low-concurrency CRUD applications, the synchronous driver may be simpler and just as effective. For applications with high levels of computation, similarly, the synchronous driver allows for better performance and stability. The MongoDB reactive driver makes sense when scalability and resource efficiency are architectural requirements and not just theoretical aspirations.

### Conclusion: Reactive as a System Property

Reactive programming is not about APIs or frameworks; it is not enough to use a reactive driver to gain benefits from this paradigm. Reactive programming requires understanding how the system behaves under stress.

Project Reactor and MongoDB's reactive driver offer powerful building blocks, but they do not eliminate the need for architectural discipline.

When used correctly, reactive systems:

* Fail more gracefully
* Scale more predictably
* Make load and flow clearer

When used poorly, they only add complexity and bottlenecks.

The real value of Reactor and reactive MongoDB lies in making trade-offs visible and controllable. For experienced architects and developers, this visibility is often worth even more than simple performance improvements.

A practical example of how to apply the end-to-end reactive model with Project Reactor and the reactive MongoDB driver in a simple CRUD application is available at the [GitHub link](https://github.com/matteoroxis/reactongodb).
