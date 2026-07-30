---
title: "A Tentative Comparison of Fault Tolerance Libraries on the JVM"
slug: "comparison-fault-tolerance-libraries"
date: "2022-01-11T09:31:19+00:00"
lastmod: "2022-01-11T09:31:21+00:00"
description: "If you're implementing microservices or not, chances are that you're calling HTTP endpoints. With HTTP calls, a lot of things can go wrong."
canonical: "https://blog.frankel.ch/comparison-fault-tolerance-libraries/"
authors:
  - "nicolas-frankel"
image: "/images/posts/2022/01/comparison-fault-tolerance-libraries/bridge-g8256e354c.jpg"
categories:
  - "Developer Tools"
  - "Microservices"
  - "Research"
tags:
related_posts:
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
  - "blockhound-how-it-works"
  - "avoiding-nullpointerexception"
  - "the-vary-http-header"
enlighterjs: true
frozen: false
---

If you're implementing microservices or not, chances are that you're calling HTTP endpoints. With HTTP calls, a lot of things can go wrong.

Experienced developers plan for this and design beyond just the happy path. In general, fault tolerance encompasses the following features:

* Retry
* Timeout
* Circuit Breaker
* Fallback
* Rate Limiter to avoid server-side 429 responses
* Bulkhead: Rate Limiter limits the number of calls in a determined timeframe, while Bulkhead limits the number of concurrent calls

A couple of libraries implement these features on the JVM. In this post, we will look at Microprofile Fault Tolerance, [Failsafe](https://failsafe.dev/) and Resilience4J.

Microprofile Fault Tolerance {#h2-0-microprofile-fault-tolerance}
-----------------------------------------------------------------

[Microprofile Fault Tolerance](https://download.eclipse.org/microprofile/microprofile-fault-tolerance-1.1.2/microprofile-fault-tolerance-spec.html) comes from the Microprofile umbrella project. It differs from the two others because it's a *specification* , which relies on a runtime to provide its capabilities. For example, Open Liberty is one such runtime. [SmallRye Fault Tolerance](https://smallrye.io/docs/smallrye-fault-tolerance/5.2.1/index.html) is another one. In turn, other components such as Quarkus and WildFly embed SmallRye.

Microprofile defines *annotations* for each feature: `@Timeout`, `@Retry Policy`, `@Fallback`, `@Circuit Breaker`, and `@Bulkhead`. It also defines `@Asynchronous`.

Because the runtime reads annotations, one should carefully read the documentation to understand how they interact if more than one is set.
> A `@Fallback` can be specified and it will be invoked if the `TimeoutException` is thrown. If `@Timeout` is used together with `@Retry`, the `TimoutException` will trigger the retry. When `@Timeout` is used with `@CircuitBreaker` and if a `TimeoutException` occurs, the failure will contribute towards the circuit open.
>
> -- [Timeout Usage](https://download.eclipse.org/microprofile/microprofile-fault-tolerance-1.1.2/microprofile-fault-tolerance-spec.html#_timeout_usage)

Resilience4J {#h2-1-resilience4j}
---------------------------------

I came upon [Resilience4J](https://resilience4j.readme.io/docs) when I was running my talk on the Circuit Breaker pattern. The talk included a demo, and it relied on [Hystrix](https://github.com/Netflix/Hystrix). One day, I wanted to update the demo to the latest Hystrix version and noticed that maintainers had deprecated it in favor of Resilience4J.

Resilience4J is based on several core concepts:

* One JAR per fault tolerance feature, with additional JARs for specific integrations, *e.g.*, Kotlin
* Static factories
* Function composition via the *Decorator pattern* applied to functions
* Integration with Java's functional interfaces, *e.g.* , `Runnable`, `Callable`, `Function`, etc.
* Exception propagation: one can use a functional interface that throws, and the library will propagate it across the call pipeline

Here's a simplified class diagram for `Retry`.

![Resilience4J Retry API](/images/posts/2022/01/comparison-fault-tolerance-libraries/retry-api-700x399.png)

Each fault tolerance feature is built around the same template seen above. One can create a pipeline of several features by leveraging function composition, each one calling another one.

Let's analyze a sample:

<pre class="EnlighterJSRAW" data-enlighter-language="java">var retrySupplier = Retry.decorateSupplier(                                  // 1
    Retry.ofDefaults("retry"),                                               // 2
    () -&gt; server.call()                                                      // 1
);
var config = new CircuitBreakerConfig.Builder()                              // 3
        .slowCallDurationThreshold(Duration.ofMillis(200))                   // 4
        .slidingWindowSize(2)                                                // 5
        .minimumNumberOfCalls(2)                                             // 6
        .build();
var breakerSupplier = CircuitBreaker.of("circuit-breaker", config)           // 7
                                    .decorateSupplier(retrySupplier);        // 7
supplier = SupplierUtils.recover(                                            // 8
    breakerSupplier,
    List.of(IllegalStateException.class, CallNotPermittedException.class),   // 9
    e -&gt; "fallback"                                                         // 10
);</pre>

1. Decorate the base `server.call()` function with `Retry`: this function is the one to be protected
2. Use the default configuration
3. Create a new *Circuit Breaker* config
4. Set the threshold above which a call is considered to be slow
5. Count over a sliding window of 2 calls
6. Minimum number of calls to decide whether to open the *Circuit Breaker*
7. Decorate the retry function with a *Circuit Breaker* with the above config
8. Create a fallback value to return when the *Circuit Breaker* is open
9. List of exceptions to handle: they won't be propagated. Resilience4J throws a `CallNotPermittedException` when the circuit is open.
10. In case any of the configured exceptions are thrown, call this function instead

The order in which functions are composed can be hard to decipher. Hence, the project offers the `Decorators` class to combine functions using a fluent API. You can find it in the `resilience4j-all` module. One can rewrite the above code as:

<pre class="EnlighterJSRAW" data-enlighter-language="java">var pipeline = Decorators.ofSupplier(() -&gt; server.call())
    .withRetry(Retry.ofDefaults("retry"))
    .withCircuitBreaker(CircuitBreaker.of("circuit-breaker", config))
    .withFallback(
        List.of(IllegalStateException.class, CallNotPermittedException.class),
        e -&gt; "fallback"
    );</pre>

It makes the intent much clearer.

Failsafe {#h2-2-failsafe}
-------------------------

I stumbled upon Failsafe not long ago. Its tenets are similar to Resilience4J: static factories, function composition, and exception propagation.

While Resilience4J fault tolerance feature don't share a class hierarchy, Failsafe provides the concept of `Policy`:

![Failsafe Retry API](/images/posts/2022/01/comparison-fault-tolerance-libraries/failsafe-retry-api-555x510.png)

I believe the main difference with Resilience4J lies in its pipelining approach. Resilience4J's API requires you first to provide the "base" function and then embed it inside any wrapper function. You cannot reuse the pipeline on top of different base functions. Failsafe allows it via the `FailsafeExecutor` class.

![Failsafe API](/images/posts/2022/01/comparison-fault-tolerance-libraries/failsafe-api-700x353.png)

Here's how to create a pipeline, *i.e.* , an instance of `FailsafeExecutor`.  

Notice there's no reference to the base call:

<pre class="EnlighterJSRAW" data-enlighter-language="java">var pipeline = Failsafe.with(                            // 1
    Fallback.of("fallback"),                             // 2
    Timeout.ofDuration(Duration.of(2000, MILLIS)),       // 3
    RetryPolicy.ofDefault()                              // 4
);</pre>

1. Define the list of policies applied from the last to the first in order
2. Fallback value
3. If the call exceeds 2000ms, throws a ` TimeoutExceededException`
4. Default retry policy

At this point, it's possible to wrap the call:

<pre class="EnlighterJSRAW" data-enlighter-language="java">pipeline.get(() -&gt; server.call());</pre>

Failsafe also provides a fluent API. One can rewrite the above code as:

<pre class="EnlighterJSRAW" data-enlighter-language="java">var pipeline = Failsafe.with(Fallback.of("fallback"))
    .compose(RetryPolicy.ofDefault())
    .compose(Timeout.ofDuration(Duration.of(2000, MILLIS)));</pre>

Conclusion {#h2-3-conclusion}
-----------------------------

All three libraries provide more or less the same features. If you don't use a CDI-compliant runtime such like regular application server or Quarkus, forget about Microprofile Fault Tolerance.

Failsafe and Resilience4J are both based on function composition and are pretty similar. If you need to define your function pipeline independently of the base call, prefer Failsafe. Otherwise, pick any of them.

As I'm more familiar with Resilience4J, I'll probably use Failsafe in my next project to get more experience with it.

**To go further:**

* [Microprofile Fault Tolerance specification](https://download.eclipse.org/microprofile/microprofile-fault-tolerance-1.1.2/microprofile-fault-tolerance-spec.html)
* [SmallRye Fault Tolerance Documentation](https://smallrye.io/docs/smallrye-fault-tolerance/5.0.0/index.html)
* [Introduction to Resilience4J](https://resilience4j.readme.io/docs)
* [Failsafe overview](https://failsafe.dev/)

*Originally published at [A Java Geek](https://blog.frankel.ch/comparison-fault-tolerance-libraries/) on January 7^th^, 2022*
