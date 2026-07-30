---
title: "Building Reactive Data Streams with Project Reactor"
slug: "building-reactive-data-streams-with-project-reactor"
date: "2026-03-19T15:13:31+00:00"
lastmod: "2026-04-15T21:09:34+00:00"
description: "Creating Non-Blocking Streaming Endpoints for High-Throughput Applications"
authors:
  - "matteo-rossi"
image: "https://foojay.io/wp-content/uploads/2026/03/Screenshot-2026-03-12-at-10.15.08-AM.png"
categories:
  - "Databases"
  - "Java"
  - "Mongo"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Creating Non-Blocking Streaming Endpoints for High-Throughput Applications {#h2-0-creating-non-blocking-streaming-endpoints-for-high-throughput-applications}
-------------------------------------------------------------------------------------------------------------------------------------------------------------

There are problems that only occur in production. Or rather, we only notice them in production.

We have created an application that exposes clean APIs, according to all standards. You have modeled the domain elegantly and efficiently: all load tests show reassuring data. CPU usage is reasonable, to say the least. However, there is a problem: when traffic exceeds a threshold, the system slows down. Response time latency becomes inconsistent; threads stack up on top of each other. Response times increase unpredictably, and application customers begin to complain about this situation.

Nothing is broken, but something is revealing the limitations and inefficiency of our system. In a JVM-based environment, the main cause of these problems is not computational efficiency; it is waiting.

For many years, we thought of waiting as a block. A thread calls the database and waits. A thread calls a remote service and waits. A thread reads a file and waits. The model and strategy are very simple, completely intuitive, and easy to debug. This strategy scales linearly with concurrency, and as we know, linear scalability, while predictable, is very expensive.

This weakness is particularly evident in high-throughput systems. Real-time dashboards, telemetry pipelines, IoT sensors, streaming APIs: all of these systems are not traditional request/response systems, but rather continuous streams of data. And to face a new enemy, we need a new superhero. This is where Project Reactor changes the way we think about backend architecture.

In reactive programming in JVM environments, data is modeled as a stream of signals, explicitly coordinating demand and separating resource usage from concurrency. This is not asynchronous execution, but rather replacing the thread-per-request model with event-driven orchestration. Thinking about it and implementing it well means allowing a small number of threads to serve thousands of simultaneous connections without crashing under the stress of increasing load.

In this article, we will explore what this means in practice, especially when our application integrates with a real datastore such as [MongoDB](https://www.mongodb.com/lp/cloud/atlas/try4-reg/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=reactive-foojay&utm_term=hugh.murray) and exposes streaming endpoints via Spring WebFlux.

From Snapshots to Streams {#h2-1-from-snapshots-to-streams}
-----------------------------------------------------------

Traditional REST endpoints return responses that are very similar to snapshots. A call arrives at the controller, the controller calls the service, the service calls the repository that queries the database. At this point, the results are collected in memory, serialized, and returned to the caller. The request is only completed and sent when the complete data set is ready.

Reactive systems revolutionize this mental model. Instead of returning a complete set of information, the endpoint returns a publisher. Instead of assembling and serializing everything once the operation is complete, a pipeline is created and described, within which data flows one after the other. Consumers are then able to request items at their own rate and according to their needs.

Let's look at a concrete example with Spring Boot.

### Project Setup {#h3-2-project-setup}

We start with a minimal reactive stack: WebFlux and [Reactive MongoDB](https://www.mongodb.com/docs/languages/java/reactive-streams-driver/current/getting-started/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=reactive-foojay&utm_term=hugh.murray).

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependencies&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;spring-boot-starter-webflux&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;dependency&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId&gt;spring-boot-starter-data-mongodb-reactive&lt;/artifactId&gt;
&nbsp;&nbsp;&nbsp;&nbsp;&lt;/dependency&gt;
&lt;/dependencies&gt;</pre>

In this configuration, there is no servlet container or blocking JDBC driver. The entire stack is designed and built to perform non-blocking I/O operations.

It is possible to configure the application to use a MongoDB database via application.properties:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">spring.data.mongodb.uri=mongodb+srv://&lt;db_user&gt;:&lt;db_pass&gt;@&lt;db-uri&gt;/?appName=devrel-tutorial-java-reactive-streaming-foojay</pre>

### Domain Model: Telemetry as a Streamable Document {#h3-3-domain-model-telemetry-as-a-streamable-document}

Let's assume we collect telemetry events coming from different devices.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Document(collection = "telemetry")

public class TelemetryEvent {
&nbsp;&nbsp;&nbsp;&nbsp;@Id
&nbsp;&nbsp;&nbsp;&nbsp;private String id;
&nbsp;&nbsp;&nbsp;&nbsp;private String deviceId;
&nbsp;&nbsp;&nbsp;&nbsp;private double temperature;
&nbsp;&nbsp;&nbsp;&nbsp;private Instant timestamp;
&nbsp;&nbsp;&nbsp;&nbsp;public TelemetryEvent(String deviceId, double temperature, Instant timestamp) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.deviceId = deviceId;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.temperature = temperature;
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.timestamp = timestamp;
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;// getters omitted for brevity
}</pre>

There is nothing special about the definition of this document. The big difference lies in how we access this collection.

Reactive Repository {#h2-4-reactive-repository}
-----------------------------------------------

When using Spring Data Reactive MongoDB, the methods in the repository layer directly return Flux and Mono.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public interface TelemetryRepository
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;extends ReactiveMongoRepository&lt;TelemetryEvent, String&gt; {
&nbsp;&nbsp;&nbsp;&nbsp;Flux&lt;TelemetryEvent&gt; findByDeviceId(String deviceId);
}</pre>

Within this interface, there is a big difference. We are not wrapping and retrieving a list of TelemetryEvents. We are returning a Flux\<TelemetryEvent\> that progressively transmits the data as it is read by the non-blocking MongoDB driver. A data stream.

This mode has important implications, as memory usage becomes proportional to demand. Doing so reduces garbage collection pressure because large amounts of data are not accumulated. Fewer objects in memory to delete means fewer heavy garbage cycles to perform. And we also have another important advantage. Latency improves because the first elements can be forwarded to the client before the entire data set is available.

### Service Layer: Explicit Backpressure Strategy {#h3-5-service-layer-explicit-backpressure-strategy}

Within the service layer, we can explicitly manage backpressure.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Service

public class TelemetryService {
&nbsp;&nbsp;&nbsp;&nbsp;private final TelemetryRepository repository;
&nbsp;&nbsp;&nbsp;&nbsp;public TelemetryService(TelemetryRepository repository) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.repository = repository;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;public Flux&lt;TelemetryEvent&gt; streamByDevice(String deviceId) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return repository.findByDeviceId(deviceId)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.onBackpressureLatest();
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

The operator highlighted within the service class, onBackpressureLatest(), clearly expresses how we want to handle backpressure. If the consumer is slower than the producer, we eliminate the intermediate events and keep only the most recent ones. If we are creating a real-time dashboard, this behavior perfectly represents what we want to achieve. If we are managing financial transactions, well, maybe we are doing something wrong.

Reactive programming requires making these choices consciously.

### Streaming Endpoint with Server-Sent Events {#h3-6-streaming-endpoint-with-server-sent-events}

Let's expose the data stream to clients through Spring WebFlux.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@RestController
@RequestMapping("/telemetry")

public class TelemetryController {
&nbsp;&nbsp;&nbsp;&nbsp;private final TelemetryService service;
&nbsp;&nbsp;&nbsp;&nbsp;public TelemetryController(TelemetryService service) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;this.service = service;
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;@GetMapping(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;value = "/stream/{deviceId}",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;produces = MediaType.TEXT_EVENT_STREAM_VALUE
&nbsp;&nbsp;&nbsp;&nbsp;)

&nbsp;&nbsp;&nbsp;&nbsp;public Flux&lt;TelemetryEvent&gt; stream(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;@PathVariable String deviceId) {
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;return service.streamByDevice(deviceId);
&nbsp;&nbsp;&nbsp;&nbsp;}
}</pre>

The key to our solution is MediaType.TEXT_EVENT_STREAM_VALUE. The HTTP connection remains open, and data is sent as it becomes available. There is no aggregation phase, no thread blocked waiting for some operation to be completed. The stream remains alive and active as long as the connection is open.

Congratulations. We have built a completely non-blocking endpoint capable of returning streaming data, backed by MongoDB.

The Real Bottleneck: Waiting, Not Working {#h2-7-the-real-bottleneck-waiting-not-working}
-----------------------------------------------------------------------------------------

In a classic Java application based on a blocking stack, each client connection uses a thread. When that thread is waiting for a response from an external object, such as a database or service, that thread remains idle but still reserved. If we multiply this by thousands of simultaneous connections, the number and management of threads becomes the bottleneck to the scalability of our application.

With Reactor, threads are not tied to waiting in any way: when a database query is initiated or an HTTP call is made to an external service, the event cycle is released, and the thread can be used by other calls. When the data becomes available, the pipeline resumes its flow from where it left off. The cost of waiting is drastically reduced, and thread usage is optimized.

This does not mean that the system does less work, but it does mean that it wastes fewer resources doing nothing.

### The Most Dangerous Line of Code {#h3-8-the-most-dangerous-line-of-code}

There is one method that can silently destroy reactive scalability: block().

Consider this anti-pattern:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public Flux&lt;TelemetryEvent&gt; broken(String deviceId) {
&nbsp;&nbsp;&nbsp;&nbsp;return repository.findByDeviceId(deviceId)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.map(event -&gt; enrich(event).block()); // DON'T
}</pre>

That .block() call pauses the event loop thread. Under load, this collapses throughput and negates the reactive model.

The correct approach is composition:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public Flux&lt;TelemetryEvent&gt; correct(String deviceId) {
&nbsp;&nbsp;&nbsp;&nbsp;return repository.findByDeviceId(deviceId)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.flatMap(this::enrich);
}

private Mono&lt;TelemetryEvent&gt; enrich(TelemetryEvent event) {
&nbsp;&nbsp;&nbsp;&nbsp;return Mono.just(event); // pretend async call
}</pre>

Reactive systems reward and perform best within an end-to-end non-blocking design. A single blocking point, and the entire pipeline loses all its advantages.

Resource Efficiency Under Pressure {#h2-9-resource-efficiency-under-pressure}
-----------------------------------------------------------------------------

Let's imagine a scenario in which we have ten thousand simultaneous subscribers to a service that exposes telemetry data.

In a traditional servlet-based architecture, for each client-side connection, the application provides a single thread, unless complicated and not very maintainable pooling strategies are used. Memory usage increases with stack allocation, and continuous context switching tends to put pressure on the JVM.

In a WebFlux architecture with Reactor, we have a small event pool at the center that coordinates all incoming and outgoing connections. Threads are not reserved for a single call or temporarily inactive sockets. Work is triggered by availability events, which allow data to flow from upstream to downstream without waiting. This way, the CPU always remains active doing useful work, instead of spending time managing threads that spend most of their time waiting.

### Error Handling in Streaming Pipelines {#h3-10-error-handling-in-streaming-pipelines}

In APIs that expose streaming endpoints, errors behave differently than in traditional REST endpoints. In fact, in the reactive case, an exception interrupts the streaming and the connection with the client is interrupted.

Reactor offers precise control for this situation, which makes use of retries:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public Flux&lt;TelemetryEvent&gt; resilientStream(String deviceId) {
&nbsp;&nbsp;&nbsp;&nbsp;return repository.findByDeviceId(deviceId)
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.retryWhen(Retry.backoff(3, Duration.ofSeconds(1)))
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.onErrorResume(e -&gt; Flux.empty());
}</pre>

It is important to be careful when using retries: if thousands of clients simultaneously try to reestablish a connection after a temporary error, the recovery can overload the system and prevent it from resuming normal operation. Again, reactive programming offers control, but not immunity from a series of issues that must be taken into account during the system planning and design phase.

### Testing the Stream {#h3-11-testing-the-stream}

Testing reactive systems requires a different approach than testing a traditional system. In fact, it involves testing individual signals rather than collections:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">StepVerifier.create(
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;service.streamByDevice("device-1").take(3)
)
.expectNextCount(3)
.verifyComplete();</pre>

Within the tests, we can explicitly state the emission of events and the termination of streaming activities. Furthermore, we can simulate the passage of time virtually, thus building and testing long-duration flows instantly and deterministically. This is a major strength of Reactor: streaming systems become and remain testable in a deterministic manner.

### Closing Thoughts {#h3-12-closing-thoughts}

In this article, we learned how to build reactive data flows, and we understood that building reactive data flows for streaming data does not mean replacing the return type of endpoints from List to Flux. It means rethinking the way we model waiting, demand, response, coordination, and resource utilization. This architecture, when combined with tools that support reactive patterns such as [MongoDB](https://www.mongodb.com/lp/cloud/atlas/try4-reg/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=reactive-foojay&utm_term=hugh.murray) and exposed via Spring WebFlux, allows us to expose streaming endpoints that adapt predictably to external pressure from clients. It is a paradigm shift and a change in perspective: the complexity of thread management shifts to flow management, and very often, this is an advantage.

All this comes at a cost: reactive systems are not magic. They require discipline, a clear and consistent architectural design, but above all, they must not block. However, this complexity can be a valuable ally in building high-throughput systems that produce data continuously. And resources are grateful too.

In fact, in modern Java applications, making efficient use of waiting time is often the most important optimization of all. All the code is available at this [repository](https://github.com/matteoroxis/reactive-streaming){#https://github.com/matteoroxis/reactive-streaming}.
