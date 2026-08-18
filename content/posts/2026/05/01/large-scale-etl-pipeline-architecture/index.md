---
title: "Large-Scale ETL Pipeline Architecture"
slug: "large-scale-etl-pipeline-architecture"
date: "2026-05-01T19:23:04+00:00"
lastmod: "2026-05-01T19:23:06+00:00"
description: "In this article, we will explore how to design a high-throughput ETL pipeline architecture using Java, focusing on concurrency models, error recovery strategies, and practical implementation techniques. To do this, we will leverage tools such as Project Reactor to build scalable, non-blocking pipelines. In the loading stage, we will consider MongoDB as the sink for transformed data."
authors:
  - "matteo-rossi"
image: "Screenshot-2026-04-21-at-1.37.53-PM.png"
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

Modern data-driven systems. ETL pipelines are no longer simply scheduled background processes that run silently overnight, one after another. They are the backbone of real-time analytics, powering operational dashboards and recommendation systems. They enable machine learning workflows.

This evolution, while creating enormous benefits, has---with the increase in data volume and the decrease in latency tolerance---called into question the traditional sequential ETL approach. A bottleneck for the speed is now required.

Designing an ETL pipeline today that operates at scale means tackling issues such as concurrency management, resilience against failures---whether total or, worse yet, partial---and observability. The challenge is no longer simply moving data, but doing so quickly, securely, predictably, and in a way that can be observed.

In this article, we will explore how to design a high-throughput ETL pipeline architecture using Java, focusing on concurrency models, error recovery strategies, and practical implementation techniques. To do this, we will leverage tools such as Project Reactor to build scalable, non-blocking pipelines. In the loading stage, we will consider [MongoDB](https://www.mongodb.com/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=etl-foojay&utm_term=hugh.murray) as the sink for transformed data.

Rethinking ETL for modern systems
---------------------------------

Conceptually, the ETL (Extract, Transform, Load) model remains unchanged. It is not in opposition to the emerging ELT model, but rather complements it. Its implementation, however, has evolved significantly. Instead of monolithic batch processes, today's pipelines are, by their very nature:

* Distributed
* Concurrent
* Fault-tolerant
* Incremental or streaming

At scale, to achieve high throughput goals, each stage of the pipeline must be independently scalable and resilient. Extraction may involve retrieving data from APIs, databases, or message queues. Transformation may require enrichment, validation, and aggregation. Loading could target data warehouses, [MongoDB collections](https://www.mongodb.com/docs/manual/core/databases-and-collections/), search indexes, or downstream services.

What is the main challenge? Coordinating these stages efficiently and effectively, without introducing bottlenecks or single points of failure.

Architectural building blocks
-----------------------------

A robust architecture for an ETL pipeline must consist of several loosely coupled layers, interconnected through well-defined and clear interfaces and boundaries. Each layer is a modular unit with its own specific responsibility.

In general, the architecture looks like this:

1. Source ingestion layer
2. Transformation layer
3. Loading layer
4. Error handling and recovery layer
5. Observability layer

The layers must communicate with one another, avoiding, where possible, heavy synchronous synchronization. The advantage is the ability to leverage asynchronous communication that accounts for backpressure mechanisms.

Embracing concurrency with reactive pipelines
---------------------------------------------

One of the most effective ways to build high-throughput pipelines is to adopt reactive, non-blocking processing. We can use libraries like Project Reactor, which provide abstractions such as Flux and Mono that allow us to model data as streams and process them concurrently.

Let's look at an example, starting with the basics. A simplified extraction and loading pipeline:

```
Flux<DataRecord> pipeline =
    extract()
        .flatMap(this::transform)
        .flatMap(this::load)
        .doOnError(error -> log.error("Pipeline error", error));
```


To the casual observer, this flow might appear to be a sequential process. The secret lies in using \`flatMap\`, which enablesthe simultaneous processing of multiple records. Each stage can process the elements independently, and the pipeline naturally adapts to the available resources.

First point to note: concurrency. Concurrency must be controlled. Unrestricted parallelism can overload downstream systems, causing a cascade of problems.

```
.flatMap(this::transform, 10) // limit concurrency
.flatMap(this::load, 5)
```


We always balance system throughput and stability by appropriately adjusting the levels of concurrency that are both possible and necessary for each layer.

Backpressure: the hidden hero
-----------------------------

In this type of system, producers often overwhelm consumers with the speed at which they generate data and events. Without proper control, this leads to memory overload and system instability.

A new hero is in town: reactive streams introduce the concept of backpressure, allowing downstream components to signal how much data they can handle. With Project Reactor, this mechanism is built into the model. For example:

```
extract()
    .onBackpressureBuffer(1000)
    .flatMap(this::transform, 10)
    .flatMap(this::load, 5);
```


In this case, when the downstream system is slower, we try to accumulate up to 1,000 elements in the buffer. Alternatively, instead of accumulating, we can discard or limit the elements, depending on the use case.

Choosing the right backpressure management strategy is critical and depends heavily on the use case: for financial or transactional data, discarding records is unacceptable, whereas for telemetry or log data, it is acceptable.

Designing for failure: error handling strategies
------------------------------------------------

Let's start with a basic premise: failures in this type of pipeline are inevitable. Network issues, invalid data, timeouts, and downstream service interruptions are all events that occur regularly. The goal is not to eliminate failures, but to manage them properly.

A simple pipeline like the one below fails immediately:

```
.flatMap(this::transform)
.flatMap(this::load)
```


If a single record fails, the entire flow could be interrupted and leave the system in an undefined state: generally speaking, that's not the outcome we'd like.

Instead, let's try to isolate failures at the individual record level:

```
.flatMap(record ->
    transform(record)
        .flatMap(this::load)
        .onErrorResume(error -> {
            log.warn("Failed processing record {}", record.getId(), error);
            return Mono.empty();
        })
)
```


This ensures that one bad record does not stop the entire pipeline.

Retry and recovery patterns
---------------------------

Some types of failures are temporary, and the operation can be safely retried once the outage has ended. Other types of failures, however, require compensation or manual intervention.

Here is a simple example of a retry:

```
.flatMap(record ->
    transform(record)
        .flatMap(this::load)
        .retryWhen(Retry.backoff(3, Duration.ofMillis(200)))
)
```


The system makes a total of three attempts, applying an exponential backoff between each attempt. However, these attempts must be used with great caution and care to avoid cascading errors.

To ensure more reliable recovery, we can also use a dead letter queue (DLQ). Failed records are retained to allow for subsequent analysis:

```
.onErrorResume(error -> 
    sendToDeadLetterQueue(record, error)
)
```


This allows the pipeline to continue processing while preserving problematic data.

Idempotency: the cornerstone of safe retries
--------------------------------------------

Retry attempts have one mandatory prerequisite: they only work if they are idempotent. Without idempotence, repeated execution can lead to duplicate data and inconsistent states.

Let's look at some examples: loading data into a database should be designed to tolerate multiple identical operations on the same objects and prevent the existence of duplicates. In practice:

* Use upserts instead of inserts
* Include unique keys in the communication
* Track the processing status

When MongoDB is used as the [sink](https://www.mongodb.com/docs/kafka-connector/v1.13/tutorials/sink-connector/), a common approach is to map the business identifier to _id, or to enforce a unique index on the key used to identify the record. This allows the loading stage to safely retry the same operation without creating duplicates.

A simple example using an upsert with MongoDB:

```
public Mono<UpdateResult> load(DataRecord record) {
return Mono.from(
     mongoCollection.replaceOne(
         Filters.eq("_id", record.getId()),
         toDocument(record),
         new ReplaceOptions().upsert(true)
)
);
}
```


This ensures that reprocessing the same record does not corrupt data.

Batching vs streaming
---------------------

Another key decision in designing this type of pipeline is the processing model. Should data be processed in batches or as a continuous stream?

Batch processing improves efficiency by reducing the I/O workload:

```
.buffer(100)
.flatMap(this::bulkLoad)
```


Streaming, on the other hand, reduces latency and improves overall responsiveness.

In practice, hybrid approaches work best: this means, for example, processing small batches continuously:

```
.bufferTimeout(100, Duration.ofSeconds(1))
.flatMap(this::bulkLoad)
```


This balances throughput and latency effectively.

When MongoDB is the sink, batching can be implemented through bulk write operations:

```
public Mono<BulkWriteResult> bulkLoadToMongo(List<DataRecord> records) {
     if (records == null || records.isEmpty()) {
         return Mono.empty();
     }

     List<WriteModel<Document>> writes = records.stream()
         .map(record -> new ReplaceOneModel<Document>(
             Filters.eq("_id", record.getId()),
             toDocument(record),
             new ReplaceOptions().upsert(true)
         ))
         .toList();

     return Mono.from(
         mongoCollection.bulkWrite(
             writes,
             new BulkWriteOptions().ordered(false)
         )
     );
}
```


Using unordered bulk operations improves throughput because individual write failures do not necessarily block the rest of the batch.

Parallelizing transformations
-----------------------------

Processing operations often place a heavy load on the CPU; we can try to maximize performance by parallelizing them wherever possible:

```
extract()
    .parallel()
    .runOn(Schedulers.parallel())
    .flatMap(this::transform)
    .sequential()
    .flatMap(this::load);
```


This allows you to distribute the work across multiple CPU cores: the more processors I have available, the sooner my work will be completed. However, parallelization introduces a certain level of complexity that must be understood and managed, especially when order matters.

If order matters, the first thing to do is preserve it:

```
.flatMapSequential(this::transform, 10)
```


This maintains order while still allowing some concurrency.

Integrating with messaging systems
----------------------------------

High-throughput ETL architectures often rely on messaging systems such as Apache Kafka. Instead of retrieving data in batches, the pipelines process events as they occur. This produces a continuous stream of data, and in the case of Kafka, a consumer can be implemented as follows:

```
receiver.receive()
    .flatMap(record ->
        transform(record.value())
            .flatMap(this::load)
            .doOnSuccess(v -> record.receiverOffset().acknowledge())
    )
```


This approach enables real-time processing and horizontal scalability, allowing the system to handle the incoming data flow.

Kafka also offers durability and reproducibility through its durable logs mechanism, as well as parallelism through its partitioning mechanism: these are essential features for large-scale ETL systems.

Observability and monitoring
----------------------------

In distributed systems---and this is nothing new---observability is just as important as correctness. Without adequate observability processes and mechanisms, debugging becomes slow, complicated, or even impossible.

A good ETL pipeline should provide:

* Metrics on point-in-time and overall throughput
* Error rates
* Processing latency
* Number of retries

How can we achieve all this? Tools like Micrometer integrate seamlessly with reactive pipelines and enable the collection of system observability metrics:

```
.doOnNext(record -> metrics.incrementProcessed())
.doOnError(error -> metrics.incrementErrors())
```


When we talk about observability, it's essential to also discuss logging and tracing. Every record should include a correlation ID to track its path through the pipeline and make troubleshooting easier.

Putting it all together
-----------------------

Let's combine the concepts into a more complete pipeline:

```
public Flux<Void> buildPipeline() {

    return extract()
        .onBackpressureBuffer(1000)
        .flatMap(record ->
            transform(record)
                .flatMap(this::load)
                .retryWhen(Retry.backoff(3, Duration.ofMillis(200)))
                .onErrorResume(error ->
                    sendToDeadLetterQueue(record, error)
                ),
            10
        )
        .bufferTimeout(100, Duration.ofSeconds(1))
        .flatMap(this::bulkLoadToMongo)
        .doOnError(error -> log.error("Pipeline failure", error));
}
```


This pipeline:

* Controls concurrency
* Handles backpressure
* Retries transient failures
* Isolates bad records
* Uses batching for efficiency
* Preserves system stability

Trade-offs and practical considerations
---------------------------------------

As with any architectural discussion, there is no universally optimal choice. There are several options, each with its own advantages and disadvantages, as well as trade-offs that must be accepted and understood.

Reactive pipelines reduce thread usage but increase cognitive complexity. Debugging asynchronous flows can be more difficult than with traditional imperative code, which is primarily characterized by thread-by-thread operations.

Retry mechanisms improve system resilience but can amplify the load during service interruptions: they should be used with caution and discretion.

Batch processing improves overall throughput but increases latency.

The key is to align architectural decisions with the requirements of the solution being built: a real-time fraud detection system has very different constraints compared to a nightly reporting process.

Conclusion
----------

Designing large-scale ETL pipelines involves addressing and understanding the associated complexities: concurrency, integration patterns, error handling, and observability.

By leveraging reactive programming with tools such as Project Reactor and integrating them with streaming platforms like Apache Kafka, and using MongoDB as the sink for transformed data, it is possible to build high-performance pipelines with appropriate resilience requirements.

The real shift lies in the design and the model: we move from linear data processing to a stream-based way of thinking. Changing this paradigm means designing systems as data streams rather than as a set of sequential steps. This shift enables higher throughput, better fault isolation and recovery, and increased scalability that scales with incoming demand.

Building ETL pipelines is not just about moving data: it is about creating a reliable and scalable backbone for the entire data platform. All the code is available at this [repository](https://github.com/matteoroxis/etl-pipeline).
