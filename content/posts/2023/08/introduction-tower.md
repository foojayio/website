---
title: "Introduction to the Tower Library"
slug: "introduction-tower"
date: "2023-08-22T08:28:33+00:00"
lastmod: "2023-08-22T08:28:35+00:00"
description: "In Rust, Tower is designed around Functional Programming and two main abstractions, Service and Layer. Learn more here!"
canonical: "https://blog.frankel.ch/introduction-tower/"
authors:
  - "nicolas-frankel"
image: "/images/posts/2023/08/introduction-tower/pexels-marius-ispas-3994380.jpg"
categories:
  - "Tutorials"
tags:
related_posts:
  - "rust-jvm"
  - "apache-apisix-loves-rust"
  - "java-panama-polyglot-rust-part-4"
  - "feedback-from-calling-rust-from-python"
frozen: false
---

One of the components of my [OpenTelemetry demo](https://github.com/nfrankel/opentelemetry-tracing) is a Rust application built with the Axum web framework. In its description, `axum` mentions:
> `axum` doesn't have its own middleware system but instead uses `tower::Service`. This means `axum` gets timeouts, tracing, compression, authorization, and more, for free. It also enables you to share middleware with applications written using `hyper` or `tonic`.
>
> -- [axum README](https://github.com/tokio-rs/axum)

**So far, I was happy to let this cryptic explanation lurk in the corner of my mind, but today is the day I want to understand what it means. Like many others, this post aims to explain to me and others how to do this.**

The `tower` crate offers the following information:
> Tower is a library of modular and reusable components for building robust networking clients and servers. Tower provides a simple core abstraction, the `Service` trait, which represents an asynchronous function taking a request and returning either a response or an error. This abstraction can be used to model both clients and servers. Generic components, like timeouts, rate limiting, and load balancing, can be modeled as `Service`s that wrap some inner service and apply additional behavior before or after the inner service is called. This allows implementing these components in a protocol-agnostic, composable way. Typically, such services are referred to as *middleware*.
>
> -- [tower crate](https://docs.rs/tower/latest/tower/)

Tower is designed around Functional Programming and two main abstractions, `Service` and `Layer`.

In its simplest expression, a `Service` is a function that reads an input and produces an output. It consists of two methods:

* One should call `poll_ready()` to ensure that the service can process requests
* `call()` processes the request and returns the response asynchronously

Because calls can fail, the return value is wrapped in a `Result`. Moreover, since Tower deals with asynchronous calls, the `Result` is wrapped in a `Future`. Hence, a `Service` transforms a `Self::Request` into a `Future<Result>`, with `Request` and `Response` needing to be defined by the developer.

The `Layer` trait allows composing `Service`s together.

Here's a slightly more detailed diagram:

![](/images/posts/2023/08/introduction-tower/tower-api-diagram-1024x891.png)

A typical `Service` implementation will wrap an underlying component; the component may be a service itself. Hence, you can chain multiple features by composing various functions.

The `call()` function implementation usually executes these steps in order, all of them being optional:

1. Pre-call
2. Call the wrapped component
3. Post-call

For example, a logging service could log the parameters before the call, call the logged component, and log the return value after the call. Another example would be a throttling service, which limits the rate of calls of the wrapped service: it would read the current status before the call and, if above a configured limit, would return immediately without calling the wrapped component. It will call the component and increment the status if the status is valid.

The role of a layer would be to take one service and wrap it into the other.

With this in mind, it's relatively easy to check the [axum-tracing-opentelemetry crate](https://docs.rs/axum-tracing-opentelemetry/latest/axum_tracing_opentelemetry/) and understand what it does. It offers two services with their respective layers: one is to extract the trace and span IDs from an HTTP request, and another is to send the data to the OTEL collector.

![](/images/posts/2023/08/introduction-tower/axum-tracing-otel-diagram.png)

Note that Tower comes with several out-of-the-box services, each available via a *feature crate*:

* `balance`: load-balance requests
* `buffer`: buffer
* `discover`: service discovery
* `filter`: conditional dispatch
* `hedge`: retry slow requests
* `limit`: limit requests
* `load`: load measurement
* `retry`: retry failed requests
* `timeout`: timeout requests

Finally, note that Tower comes in three crates: `tower` is the public crate, while `tower-service` and `tower-layer` are considered less stable.

![](/images/posts/2023/08/introduction-tower/tower-components.png)

In this post, we have explained the what is the Tower library:

* It's a Functional Programming library that provides function composition.
* If you come from the Object-Oriented Programming paradigm, it's similar to the Decorator pattern.
* It builds upon two abstractions, `Service` is the function, and `Layer` composes functions.

It's widespread in the Rust ecosystem, and learning it is a good investment.

**To go further:**

* [axum](https://docs.rs/axum/0.6.20/axum/index.html)
* [tower documentation](https://docs.rs/tower/0.4.13/tower/index.html)
* [tower crate](https://docs.rs/crate/tower/0.4.13)
* [axum_tracing_opentelemetry documentation](https://docs.rs/axum-tracing-opentelemetry/0.13.1/axum_tracing_opentelemetry/index.html)

*Originally published at [A Java Geek](https://blog.frankel.ch/introduction-tower/) on August 20^th^, 2023*

*[MPSC]: Multi Producer Single Consumer
