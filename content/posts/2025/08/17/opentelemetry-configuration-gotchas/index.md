---
title: "OpenTelemetry configuration gotchas"
slug: "opentelemetry-configuration-gotchas"
date: "2025-08-17T08:01:26+00:00"
lastmod: "2025-08-18T08:03:42+00:00"
description: "Operators of OpenTelemetry can't treat services as black boxes. They must consider the underlying stack and framework, and learn how to configure them accordingly."
canonical: "https://blog.frankel.ch/opentelemetry-gotchas/"
authors:
  - "nicolas-frankel"
image: "cover-1024x683.png"
categories:
  - "Observability"
tags:
related_posts:
  - "beyond-pass-fail-a-modern-approach-to-java-integration-testing"
  - "creating-cloud-native-java-applications-with-the-12-factor-app-methodology"
  - "couch-to-fully-observed-code-with-spring-boot-3-2-micrometer-tracing-and-digma"
  - "continuous-feedback-free-udemy-course-additional-coupons-available"
enlighterjs: true
frozen: false
---

Last week, I described [several approaches to OpenTelemetry on the JVM](https://blog.frankel.ch/opentelemetry-tracing-jvm/), their requirements, and their different results. This week, I want to highlight several gotchas found across stacks in the zero-code instrumentation.

## The promise of OpenTelemetry

Since its inception, OpenTelemetry has unified the 3 pillars of observability. In the distributed tracing space, it replaced proprietary protocols Zipkin and Jaeger. IMHO, it achieved such success for several reasons:

* First, a huge industry pressure to work across proprietary tools
* Zero-code instrumentation, allowing developers to be unconcerned by OpenTelemetry
* Easy and unified configuration mechanism via environment variables.

The latter is a boon for the Ops team, as they don't have to know the underlying framework (or stack!) details. They only need to check the [Environment Variable Specification](https://opentelemetry.io/docs/specs/otel/configuration/sdk-environment-variables/) and they are done.

Here's a simple snippet to illustrate my point:

```yaml
environment:
  OTEL_EXPORTER_OTLP_ENDPOINT: http://collector:4317             #1
  OTEL_SERVICE_NAME: foobar                                      #2
```

1. Configure the endpoint to send data to
2. Set the component's name

From the above, you can't guess what the underlying stack is.

On the OpenTelemetry side, different developers contribute to different language libraries. [Developers of the Java agent](https://github.com/open-telemetry/opentelemetry-java-instrumentation/graphs/contributors) are different from the [those of the Python library](https://github.com/open-telemetry/opentelemetry-python-contrib/graphs/contributors). Moreover, each stack has specific approaches and limitations.

It naturally creates differences in the different implementations.

## Gotchas

Here are a couple of gotchas I found out, but the list is not exhaustive.

### Path or no path?

Let's start easy with a gotcha that exists across stacks.

OpenTelemetry offers some configuration parameters for endpoints.

* A generic one, to use for an OpenTelemetry Collector that accepts all signals: `OTEL_EXPORTER_OTLP_ENDPOINT`
* Parameters specialized for a single signal, *e.g.* , `OTEL_EXPORTER_OTLP_TRACES_ENDPOINT`

In the latter case, one can set the root path to the endpoint, *e.g* , `http://collector:4317`. The SDK will automatically append the default path, depending on the signal type. For example, it appends `/v1/traces` for the `OTEL_EXPORTER_OTLP_TRACES_ENDPOINT` configuration parameter.

Alternatively, one can set the **full** path to the endpoint, *e.g* , `http://collector:4317/v1/traces`.

The gotcha will catch you when the OpenTelemetry evolves to v2 if you didn't use paths. Because the library will automatically append `/v1/`, you'll have to make sure the backend offers both `/v1` and `/v2` endpoints.

### Python logging

To open the list, here's the first gotcha: by default, the Python library doesn't send logging data. It must be enabled explicitly!

```yaml
environment:
  OTEL_PYTHON_LOGGING_AUTO_INSTRUMENTATION_ENABLED: true         #1
  OTEL_EXPORTER_OTLP_ENDPOINT: http://collector:4317
  OTEL_SERVICE_NAME: foobar
```

1. Enable logs

Developers also must be involved:
> Unlike Traces and Metrics, there is no equivalent Logs API. There is only an SDK. For Python, you use the Python `logger` library, and then the OTel SDK attaches an OTLP handler to the root logger, turning the Python logger into an OTLP logger. One way to accomplish this is documented in the logs example in the OpenTelemetry Python repository.
>
> -- [Logs Auto-Instrumentation Example](https://opentelemetry.io/docs/zero-code/python/logs-example/)

## Micrometer Tracing

Before OpenTelemetry, Jaeger and Zipkin reigned supreme in the distributed tracing area. In the great Spring tradition, the project created Spring Cloud Sleuth to offer a facade over Zipkin. Over time, it evolved to be compatible with OpenTracing, one of OpenTelemetry's parents, along with OpenCensus.

I cannot be sure whether OpenTelemetry was the cause, but Spring Cloud Sleuth evolved into the (badly-named) [Micrometer Tracing](https://docs.micrometer.io/tracing/reference/) library. Micrometer Tracing [targets Zipkin first](https://docs.micrometer.io/tracing/reference/tracers.html), but also supports OpenTelemetry. I find adding the requested libraries a bit complex, but it's manageable.

However, the configuration doesn't conform to the OpenTelemetry environment variables. It brings its variables:

```yaml
environment:
  MANAGEMENT_OTLP_TRACING_ENDPOINT: http://jaeger:4318/v1/traces #1-2
  OTEL_SERVICE_NAME: foobar                                      #3
```

1. Non-OpenTelemetry environment variable name
2. **MUST** set the full path
3. Conform to the OpenTelemetry spec since [Spring Boot 3.5](https://github.com/spring-projects/spring-boot/pull/44394). Before it, it used [spring.application.name](https://docs.spring.io/spring-boot/appendix/application-properties/index.html#application-properties.core.spring.application.name)

## Quarkus

Compare with [Quarkus](https://quarkus.io/guides/opentelemetry), which prefixes regular OpenTelemetry environment variables with `QUARKUS_`:

```yaml
environment:
  QUARKUS_OTEL_EXPORTER_OTLP_TRACES_ENDPOINT: http://jaeger:4317
  QUARKUS_OTEL_SERVICE_NAME: foobar
```

It's consistent. And yet, the Quarkus instrumentation has another gotcha:
> Only the **tracing** signal is enabled by default. To enable **metrics** and **logs**, add the following configuration to your application.properties file:
>
> -- [Quarkus instrumentation](https://opentelemetry.io/docs/zero-code/java/quarkus/)

Meanwhile, if you're an expert at operating OpenTelemetry, these defaults will surprise you. In OpenTelemetry, they are governed by other configuration parameters:

```yaml
environment:
  OTEL_METRICS_EXPORTER: none
  OTEL_LOGS_EXPORTER: none
```

## Summary

OpenTelemetry has become a *de facto* standard in a few years. However, the promise of ubiquitous configuration, if there was ever such a thing, doesn't hold. Operators of OpenTelemetry can't treat services as black boxes. They must consider the underlying stack and framework, and learn how to configure them accordingly.

On the JVM, I'd recommend to stick to the Java Agent as much as possible, but if you must across stacks, there's no magical solution.

**To go further:**

* [Maven - Introduction to the Dependency Mechanism](https://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html)
* [Effective Rust - Item 25: Manage your dependency graph](https://effective-rust.com/dep-graph.html)

*Originally published at [A Java Geek](https://blog.frankel.ch/opentelemetry-gotchas/) on August 10^th^, 2025*
