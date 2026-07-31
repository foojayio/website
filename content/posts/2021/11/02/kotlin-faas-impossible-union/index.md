---
title: "Kotlin and FaaS: An Impossible Union? | Foojay.io Today"
slug: "kotlin-faas-impossible-union"
date: "2021-11-02T08:40:48+00:00"
lastmod: "2021-11-02T08:44:34+00:00"
description: "I'd like to first explain why the JVM platform is a bad idea for FaaS. Then, I'll proceed to propose alternatives to use Kotlin nonetheless."
canonical: "https://blog.frankel.ch/kotlin-faas-impossible-union/"
authors:
  - "nicolas-frankel"
image: "pexels-alexandre-saraiva-carniato-2049755.jpg"
categories:
  - "Kotlin"
  - "Performance"
tags:
related_posts:
  - "five-java-developer-must-haves"
  - "kicking-spring-natives-tires"
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
  - "exposed-kotlin-orm-complete-guide"
frozen: false
---

Some time ago, I read a post describing how to run a serverless Kotlin function on [OpenFaaS](https://www.openfaas.com/). While the content is technically correct, I believe the concept itself is very wrong. Such posts can lead people to make ill-advised decisions: "because we can" is hardly a winning strategy.

In this article, I'd like to first explain why the JVM platform is a bad idea for . Then, I'll proceed to propose alternatives to use Kotlin nonetheless.

(I deliberately chose not to link to the original post to avoid lending it any credibility. If you want to read it anyway, Google is your friend.)

Semantics {#h2-0-semantics}
---------------------------

Before anything, we should first settle the semantics of FaaS and serverless. While the terms are sometimes used interchangeably, I will use the following definitions in the scope of this post. In all cases, it always pays to have a look at what people on Wikipedia agreed on:
> Serverless computing is a cloud-computing execution model in which the cloud provider runs the server, and dynamically manages the allocation of machine resources. Pricing is based on the actual amount of resources consumed by an application, rather than on pre-purchased units of capacity.
>
> -- <https://en.wikipedia.org/wiki/Serverless_computing>
> Function as a service (FaaS) is a category of cloud computing services that provides a platform allowing customers to develop, run, and manage application functionalities without the complexity of building and maintaining the infrastructure typically associated with developing and launching an app.
>
> -- <https://en.wikipedia.org/wiki/Function_as_a_service>

From the definitions above:

* Serverless is about the managing of resources: You need to handle them dynamically, the property being **elasticity**.
* FaaS is about the size of code: The evolution of sizes goes from a full-fledged app to microservices to **a function**. The original post mentions it.

*Ergo*, FaaS implies serverless.

The JVM and FaaS {#h2-1-the-jvm-and-faas}
-----------------------------------------

The platform is a fine piece of technology. In particular, the abstraction layer allows the JVM to compile the *bytecode* to native code adapted to the workload. That's the reason why even though C/C++ compiled apps are closer to the bare metal, the JVM was able to compete with them performance-wise - and even win.

However, this optimization comes at a cost: the JVM needs time to warm up, *e.g.*, to load classes into memory. That's why performance tests on the JVM require a couple of warm-up rounds. For that reason, the JVM is well-adapted to long-running processes, where the startup time is negligible compared to the overall lifetime process. Application servers are the poster child of such use-cases: once started, one should run for a long time, say days, to be very conservative. In that regard, a startup time of a minute means nothing.

Now comes FaaS: in the JVM context, FaaS means the JVM starts, the function runs, and everything is discarded just afterward. In that case, the startup time has a significant impact on the overall execution time. Moreover, the JVM has no time to compile the *bytecode* to native code: it stays "cold".

Some recommend keeping the JVM up to reuse it afterward and thus avoid paying the cost of the startup time. It's pretty straightforward to achieve that: just send requests to the function faster than the pace at which the FaaS provider discards the underlying JVM. However, it just defeats the purpose of serverless, as the elasticity property is now gone.

It applies to Kotlin, Java, Scala, Groovy, Clojure, and every other language that runs on top of the JVM. Even though I love Kotlin, this is a no-go: anybody telling otherwise doesn't need the elasticity that the FaaS approach provides - or worse, has no clue. It would be akin to build a microservices architecture in most contexts: .

The Best of Both Worlds {#h2-2-the-best-of-both-worlds}
-------------------------------------------------------

And yet, all hope is not lost.

There are ways to use Kotlin and still be able to benefit from FaaS. Since the problem with FaaS is the JVM, why not remove it? There are two easy ways to achieve that:

* Use Graal's native image: GraalVM is a bundle of different technologies. Among them is [SubstrateVM](https://github.com/oracle/graal/blob/master/docs/reference-manual/native-image/README.md): it allows transforming a JAR (or a class) into a native executable. You can then wrap the resulting application into a Docker container, which must be compatible with one's FaaS framework of choice. Here's [one example](https://thenatureofsoftware.se/posts/openfaas_graalvm_native/) of such an approach.
* Transpile to JavaScript: Another alternative is to transpile Kotlin into JavaScript. JavaScript doesn't need the JVM platform to run. Then, the code can be wrapped into a container as above or packaged directly in a ZIP archive. The latter option can run on proprietary infrastructures, such as AWS Functions.

Both approaches need a solid build pipeline to start from Kotlin and end up with a Docker container (or a ZIP). As with the original design, they need both unit testing to test the code is correct and integration testing to test the final artifact works as expected.

Conclusion {#h2-3-conclusion}
-----------------------------

One needs to be aware of the context of most technologies. You (probably) don't need microservices, FaaS, or whatever the hype curve is trending right now. However, by understanding the pros and cons of those, you'll be able to leverage them when the time is right.

As for now, it would be ill-advised to use the JVM with FaaS. That doesn't mean you cannot use Kotlin at all.

* [OpenFaaS native Kotlin functions](https://thenatureofsoftware.se/posts/openfaas_graalvm_native/)
* [Binary As A Function](https://medium.com/@napperley/binary-as-a-function-a0d6490efb27)

*Originally published at [A Java Geek](https://blog.frankel.ch/kotlin-faas-impossible-union/) on October 16^th^, 2021*

*[JVM]: Java Virtual Machine
*[FaaS]: Function-as-a-Service
*[YAGNI]: You Ain't Gonna Need It
