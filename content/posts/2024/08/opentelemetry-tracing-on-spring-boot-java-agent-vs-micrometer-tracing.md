---
title: "OpenTelemetry Tracing on Spring Boot, Java Agent vs. Micrometer Tracing"
slug: "opentelemetry-tracing-on-spring-boot-java-agent-vs-micrometer-tracing"
date: "2024-08-22T08:34:57+00:00"
lastmod: "2024-08-22T08:34:58+00:00"
description: "Let's compare three different ways to do OpenTelemtry Tracing: Java agent v1, Java agent v2, and Micrometer Tracing."
canonical: "https://blog.frankel.ch/opentelemetry-tracing-spring-boot/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2024/08/micrometer-tracing-otel.png"
categories:
  - "DevOps"
  - "OpenTelemetry"
  - "Spring"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**My [demo](https://github.com/nfrankel/opentelemetry-tracing) of OpenTelemetry Tracing features two Spring Boot components. One uses the Java agent, and I noticed a different behavior when I recently upgraded it from v1.x to v2.x. In the other one, I'm using Micrometer Tracing because I compile to GraalVM native, and it can't process Java agents.**

I want to compare these three different ways in this post: Java agent v1, Java agent v2, and Micrometer Tracing.

The base application and its infrastructure {#h2-0-the-base-application-and-its-infrastructure}
-----------------------------------------------------------------------------------------------

I'll use the same base application: a simple Spring Boot application, coded in Kotlin. It offers a single endpoint.

* The function beyond the endpoint is named `entry()`
* It calls another function named `intermediate()`
* The latter uses a `WebClient` instance, the replacement of `RestTemplate`, to make a call to the above endpoint
* To avoid infinite looping, I pass a custom request header: if the `entry()` function finds it, it doesn't proceed further

![Sample app sequence diagram](/images/posts/2024/08/opentelemetry-tracing-on-spring-boot-java-agent-vs-micrometer-tracing/app-sequence.png)

It translates into the following code:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">@SpringBootApplication
class Agent1xApplication

@RestController
class MicrometerController {

    private val logger = LoggerFactory.getLogger(MicrometerController::class.java)

    @GetMapping("/{message}")
    fun entry(@PathVariable message: String, @RequestHeader("X-done") done: String?) {
        logger.info("entry: $message")
        if (done == null) intermediate()
    }

    fun intermediate() {
        logger.info("intermediate")
        RestClient.builder()
            .baseUrl("http://localhost:8080/done")
            .build()
            .get()
            .header("X-done", "true")
            .retrieve()
            .toBodilessEntity()
    }
}</pre>

For every setup, I'll check two stages: the primary stage, with OpenTelemetry enabled, and a customization stage to create additional internal spans.

Micrometer Tracing {#h2-1-micrometer-tracing}
---------------------------------------------

Micrometer Tracing stems from [Micrometer](https://micrometer.io/), a "vendor-neutral application observability facade".
> Micrometer Tracing provides a simple facade for the most popular tracer libraries, letting you instrument your JVM-based application code without vendor lock-in. It is designed to add little to no overhead to your tracing collection activity while maximizing the portability of your tracing effort.
>
> -- [Micrometer Tracing site](https://docs.micrometer.io/tracing/reference/index.html)

To start with Micrometer Tracing, one needs to add a few dependencies:

* Spring Boot Actuator, `org.springframework.boot:spring-boot-starter-actuator`
* Micrometer Tracing itself, `io.micrometer:micrometer-tracing`
* A "bridge" to the target tracing backend API. In my case, it's OpenTelemetry, hence `io.micrometer:micrometer-tracing-bridge-otel`
* A concrete exporter to the backend, `io.opentelemetry:opentelemetry-exporter-otlp`

We don't need a BOM because versions are already defined in the Spring Boot parent.

Yet, we need two runtime configuration parameters: where should the traces be sent, and what is the component's name. They are governed by the `MANAGEMENT_OTLP_TRACING_ENDPOINT` and `SPRING_APPLICATION_NAME` variables.

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">services:
  jaeger:
    image: jaegertracing/all-in-one:1.55
    environment:
      - COLLECTOR_OTLP_ENABLED=true                                     #1
    ports:
      - "16686:16686"
  micrometer-tracing:
    build:
      dockerfile: Dockerfile-micrometer
    environment:
      MANAGEMENT_OTLP_TRACING_ENDPOINT: http://jaeger:4318/v1/traces    #2
      SPRING_APPLICATION_NAME: micrometer-tracing                       #3</pre>

1. Enable the OpenTelemetry collector for Jaeger
2. Full URL to the Jaeger OpenTelemetry gRPC endpoint
3. Set the OpenTelemetry's service name

Here's the result:

![Micrometer traces on Jaeger with no customization](/images/posts/2024/08/opentelemetry-tracing-on-spring-boot-java-agent-vs-micrometer-tracing/trace-micrometer-basic.png)

Without any customization, Micrometer creates spans when receiving and sending HTTP requests.

The framework needs to inject magic into the `RestClient` for sending. We must let the former instantiate the latter for that:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">class MicrometerTracingApplication {

    @Bean
    fun restClient(builder: RestClient.Builder) =
        builder.baseUrl("http://localhost:8080/done").build()
}</pre>

We can create manual spans in several ways, one via the OpenTelemetry API itself. However, the setup requires a lot of boilerplate code. The most straightforward way is the Micrometer's [Observation API](https://docs.micrometer.io/micrometer/reference/observation.html). Its main benefit is to use a single API that manages both *metrics* and *traces*.

![Sample app sequence diagram](/images/posts/2024/08/opentelemetry-tracing-on-spring-boot-java-agent-vs-micrometer-tracing/observation-api-classes.png)

Here's the updated code:

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">class MicrometerController(
    private val restClient: RestClient,
    private val registry: ObservationRegistry
) {

    @GetMapping("/{message}")
    fun entry(@PathVariable message: String, @RequestHeader("X-done") done: String?) {
        logger.info("entry: $message")
        val observation = Observation.start("entry", registry)
        if (done == null) intermediate(observation)
        observation.stop()
    }

    fun intermediate(parent: Observation) {
        logger.info("intermediate")
        val observation = Observation.createNotStarted("intermediate", registry)
            .parentObservation(parent)
            .start()
        restClient.get()
            .header("X-done", "true")
            .retrieve()
            .toBodilessEntity()
        observation.stop()
    }
}</pre>

The added observation calls reflect upon the generated traces:

image:{assetsdir}/trace-micrometer-custom.webp\[Micrometer traces on Jaeger with the Observation API,840\]

OpenTelemetry Agent v1 {#h2-2-opentelemetry-agent-v1}
-----------------------------------------------------

An alternative to Micrometer Tracing is the generic [OpenTelemetry Java Agent](https://github.com/open-telemetry/opentelemetry-java-instrumentation). Its main benefit is that it impacts neither the code nor the developers; the agent is a pure runtime-scoped concern.

<pre class="EnlighterJSRAW" data-enlighter-language="shell">java -javaagent:opentelemetry-javaagent.jar agent-one-1.0-SNAPSHOT.jar</pre>

The agent abides by OpenTelemetry's configuration with environment variables:

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">services:
  agent-1x:
    build:
      dockerfile: Dockerfile-agent1
    environment:
      OTEL_EXPORTER_OTLP_ENDPOINT: http://jaeger:4317                   #1
      OTEL_RESOURCE_ATTRIBUTES: service.name=agent-1x                   #2
      OTEL_METRICS_EXPORTER: none                                       #3
      OTEL_LOGS_EXPORTER: none                                          #4
    ports:
      - "8081:8080"</pre>

1. Set the protocol, the domain, and the port. The library appends `/v1/traces`
2. Set the OpenTelemetry's service name
3. Export neither the metrics nor the logs

With no more configuration, we get the following traces:

![Agent v1 traces on Jaeger with no customization](/images/posts/2024/08/opentelemetry-tracing-on-spring-boot-java-agent-vs-micrometer-tracing/trace-agent-1x-basic.png)

The agent automatically tracks requests, both received and sent, **as well as functions marked with Spring-related annotations** . Traces are correctly nested inside each other, according to the call stack. To trace additional functions, we need to add a dependency to our codebase, `io.opentelemetry.instrumentation:opentelemetry-instrumentation-annotations`. We can now annotate previously untraced functions with the `@WithSpan` annotation.

![@WithSpan class diagram](/images/posts/2024/08/opentelemetry-tracing-on-spring-boot-java-agent-vs-micrometer-tracing/withspan-class-diagram.png)

The `value()` part governs the trace's label, while the `kind` translates as a `span.kind` attribute. If the value is set to an empty string, which is the default, it outputs the function's name. For my purposes, default values are good enough.

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">@WithSpan
fun intermediate() {
    logger.info("intermediate")
    RestClient.builder()
        .baseUrl("http://localhost:8080/done")
        .build()
        .get()
        .header("X-done", "true")
        .retrieve()
        .toBodilessEntity()
}</pre>

It yields the expected new `intermediate()` trace:

![](/images/posts/2024/08/opentelemetry-tracing-on-spring-boot-java-agent-vs-micrometer-tracing/trace-agent-1x-custom.png)

OpenTelemetry Agent v2 {#h2-3-opentelemetry-agent-v2}
-----------------------------------------------------

OpenTelemetry released a new major version of the agent in January of this year. I updated my demo with it; traces are now only created when the app receives and sends requests.

![Agent v2 traces on Jaeger with no customization](/images/posts/2024/08/opentelemetry-tracing-on-spring-boot-java-agent-vs-micrometer-tracing/trace-agent-2x-basic.png)

As for the previous version, we can add traces with the `@WithSpan` annotation. The only difference is that we must also annotate the `entry()` function. It's not traced by default.

![Agent v2 traces on Jaeger with annotations](/images/posts/2024/08/opentelemetry-tracing-on-spring-boot-java-agent-vs-micrometer-tracing/trace-agent-2x-custom.png)

Discussion {#h2-4-discussion}
-----------------------------

Spring became successful for two reasons: it simplified complex solutions, *i.e.*, EJBs 2, and provided an abstraction layer over competing libraries. Micrometer Tracing started as an abstraction layer over Zipkin and Jaeger, and it made total sense. This argument becomes moot with OpenTelemetry being supported by most libraries across programming languages and trace collectors. The Observation API is still a considerable benefit of Micrometer Tracing, as it uses a single API over Metrics and Traces.

On the Java Agent side, OpenTelemetry configuration is similar across all tech stacks and libraries - environment variables. I was a bit disappointed when I upgraded from v1 to v2, as the new agent is not Spring-aware: Spring-annotated functions are not traced by default. In the end, it's a wise decision. It's much better to be explicit about the spans you want than remove some you don't want to see.

The complete source code for this post can be found on [GitHub](https://github.com/ajavageek/boot-tracing).

**To go further:**

* [Demo of OpenTelemetry Tracing](https://github.com/nfrankel/opentelemetry-tracing)
* [Micrometer Tracing](https://docs.micrometer.io/tracing/reference/index.html)
* [OpenTelemetry Traces](https://opentelemetry.io/docs/concepts/signals/traces/)
* [OpenTelemetry Java integration](https://opentelemetry.io/docs/languages/java/getting-started/)
* [OpenTelemetry Java examples](https://github.com/open-telemetry/opentelemetry-java-examples#java-opentelemetry-examples)
* [Distributed Tracing with Spring Boot 3 --- Micrometer vs OpenTelemetry](https://itnext.io/distributed-tracing-with-spring-boot-3-micrometer-vs-opentelemetry-b3593546f61b)
* [Observability With Spring Boot 3](https://www.baeldung.com/spring-boot-3-observability)

*** ** * ** ***

*Originally published at [A Java Geek](https://blog.frankel.ch/opentelemetry-tracing-spring-boot/) on August 3^rd^, 2024*
