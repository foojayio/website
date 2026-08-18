---
title: "General Build Distribution: A Game-Changer or a Gimmick?"
slug: "general-build-distribution-a-game-changer-or-a-gimmick"
date: "2022-07-20T21:09:29+00:00"
lastmod: "2022-07-28T17:34:17+00:00"
description: "Understand the performance potential of remote and distributed builds and explore how to improve build feedback times."
authors:
  - "kyle-moore"
image: "1_0FUueLptBTH-g9orqUngWg.png"
categories:
  - "DevOps"
  - "Performance"
tags:
related_posts:
  - "remote-and-distributed-build-patterns"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "evolution-of-microservices"
  - "compilation-avoidance-with-gradle"
frozen: false
---

**Understand the performance potential of remote and distributed builds and explore how to improve build feedback times.**

The [Remote and Distributed Build Patterns](https://foojay.io/today/remote-and-distributed-build-patterns/) article explains the differences between remote and distributed builds and variations on each. Specifically, we distinguished between "test distribution" and "general build distribution".

This article discusses distributed builds in a broader perspective of improving build feedback times. We'll start by explaining the types of changes engineers tend to make, identify the typical bottlenecks and share how these relate to distributed builds. We will also study the performance potential of general build distribution. Finally, we will explore a holistic approach to improving build feedback times.

In greater detail below, we will elaborate on these three findings:

* Building in a distributed fashion is not a substitute for a well-tuned build process.
* Improving incremental build performance, not "full rebuilds", is the most important aspect of improving the local developer experience.
* General build distribution of a well-tuned build beyond test distribution is an evolutionary, not revolutionary, process that yields marginal performance benefits for most JVM projects.

The analysis and findings presented here apply especially to projects for the JVM ecosystem. Future follow-up articles will address the Android and native/iOS ecosystems.

Most Important Scenarios to Optimize
------------------------------------

Two keys to improving the local developer experience lie in understanding the typical bottlenecks faced by engineers, as well as the types of changes built by engineers as they add new features, fix bugs and write tests.

### Test Execution is the Bottleneck

Test execution is frequently the single most time-consuming portion of build time. Optimizing builds to avoid unnecessary test execution can yield large productivity gains. The Gradle Build Tool already skips tests when no meaningful changes are detected on the classpath, and can also restore test execution results from the build cache. The post [Stop rerunning your tests](https://www.stefan-oehme.com/stop-rerunning-tests) does a great job of explaining the efficiencies available by minimizing test re-execution.

In the context of distributed builds, this bottleneck is addressed by [modern test distribution](https://blog.gradle.org/remote-and-distributed-build-patterns#modern-test-distribution) such as the Test Distribution solution provided by Gradle Enterprise.

### Frequency of Incremental Builds vs. Full Rebuilds

The next key point is that in the vast majority of cases engineers are building small, incremental changes. We posit that these small, incremental changes are unlikely to benefit from distributing the build steps beyond test distribution. Also, developers using modern build systems seldom perform a "full rebuild" without the benefit of a shared build cache or retained history of a previous build on the same machine.

Consider a change to the body of a private method of a Java class: only that class must be recompiled and the library containing it is reassembled. But there is no reason to recompile a downstream consumer of that library as it cannot link to a private method. At the opposite end of the spectrum, consider a modification to the public API of a "common" library consumed by many other subprojects in a multiproject build. This will cause a "domino effect" by causing its downstream consumers to be recompiled. General build distribution may help in this scenario, but we contend this is the exception, not the norm (see [Parallelization factor](#parallelization-factor) below for more insights).

Additionally, Java compilation is relatively fast compared to "native" languages, further reducing the optimization potential of general build distribution in Java projects.

Thus, we encourage skepticism at claims of building large projects "from scratch" as a true measure of build system performance, or as a justification for implementing a remote or distributed build.

Parallelization Factor
----------------------

The key to understanding the maximum speed potential of any build (locally built, hosted remotely or distributed) is to visualize the interdependencies of its outputs. Imagine a relatively small software project having three subprojects: A, B and C. If compiling subproject C requires the outputs of subprojects A and B, then C *depends on* A and B. Most importantly, we cannot begin building C until both A and B are complete; therefore the best-case build-time scenario can be denoted as ***max(A, B) + C*** . Given a local or remote build host with unlimited CPU cores, or a pool of unlimited distributed build agents, the build *cannot* be parallelized further than this bottleneck.

[![Project structure](https://miro.medium.com/max/1400/1*423NV_D5lKEF1wFxBQ3zQg.png "Project structure")](// "Project structure")  
*Figure 1.: Project structure*

As we see that this bottleneck is dependency-based and not performance-based, we now have the ability to predict the potential benefit of remote or distributed builds.

To put this theory to the test, we've performed some analysis of the *parallelization factor* to establish a theoretically achievable minimal build time given the bottlenecks described above. We examined the build of Gradle itself and other sizable builds in collaboration with some of our partners. We've found these interesting results:

* Test execution consumes the vast majority of build time, accounting for 80-90% of the end-to-end CI cycle.
* After test execution, the most time-consuming tasks are CPU-intensive tasks like compilation or validation, followed by disk-bound packaging/assembly tasks.
* More than half of the non-test tasks are executed in a single process.

This last point is critical: tasks that operate as a single process - with no other processes executing simultaneously - are indicative of a bottleneck, like subproject C in the above example. Single-process tasks are proof that further optimization via distribution is not possible. It *is* possible that a more powerful remote CPU would finish the compilation task more quickly, but this benefit could easily be negated by the overhead of sending bits back and forth.

[![Time V Threads](https://miro.medium.com/max/1400/1*h6-tkIIyMb0YUmqg5uR4jg.png "Time V Threads")](http://https://miro.medium.com/max/1400/1*h6-tkIIyMb0YUmqg5uR4jg.png "Time V Threads")  
*Figure 2.: Cumulative work time, grouped by number of concurrent workers. Half of the work was executed with no other busy processes running in parallel.*

Setting aside test execution (addressed by Test Distribution, see [above](#test-execution-is-the-bottleneck)), and focusing on the remaining CPU-intensive 10-20% portion of build times, we find that the potential for optimization is low. The failure of half these tasks to execute in parallel with other processes means that, at best, a general distribution solution could expedite only 5-10% of overall build time, while incurring significant costs in terms of build complexity and management overhead.

The Path Forward
----------------

As we discussed above, most of the changes done by developers are small, incremental changes and the biggest bottleneck is typically test execution. Therefore, focusing the build optimizations on those aspects will typically yield the best results. The following section lists some of the key steps your build process can implement today. These fundamentals of build performance optimization will not just improve any build whether local, remote or distributed, but will also ensure the best possible performance when potentially moving to a remote or distributed environment in the future.

In this order, we recommend taking advantage of these Gradle Build Tool features to optimize local build feedback time. Most of these features are documented in further detail at [Improving the Performance of Gradle Builds](https://docs.gradle.org/current/userguide/performance.html):

1. [Incremental Build](https://docs.gradle.org/current/userguide/performance.html#incremental_build)
2. [Compile Avoidance](https://docs.gradle.org/current/userguide/performance.html#compile_avoidance) and [Incremental Compilation](https://docs.gradle.org/current/userguide/performance.html#incremental_compilation)
3. [Remote Build Cache](https://docs.gradle.org/current/userguide/build_cache.html)
4. [Parallel Execution](https://docs.gradle.org/current/userguide/performance.html#parallel_execution)
5. [Configuration Cache](https://docs.gradle.org/current/userguide/configuration_cache.html) (also increases local parallelism)

Additionally, the following features in Gradle Enterprise drastically shorten test feedback time which is usually by far the biggest bottleneck in build performance:

* [Test Distribution](https://gradle.com/gradle-enterprise-solutions/test-distribution/)
* [Predictive Test Selection](https://gradle.com/gradle-enterprise-solutions/predictive-test-selection/)

While general build distribution may demonstrate impressive build performance gains when measured in isolation, we've demonstrated that for most JVM projects it's unlikely to offer significant additional build performance improvements for typical scenarios in well-optimized builds. This is not to suggest that we find a general distribution solution uninteresting. Rather, we view this on our long-term roadmap as an evolutionary, not revolutionary, solution.

Summary
-------

Anecdotal evidence and industry experience have shown two things: first, engineers are most likely to iterate and rebuild small, incremental changes - not rebuilding the entire project from scratch. Second, regardless of the type of change being built, test execution is the primary cause of build slowness and reduced developer productivity.

Using active process counts as a proxy for the potential parallelization of local builds, we've shown that general build distribution solutions would have a relatively small - if any - impact on build performance for many builds in the JVM ecosystem.

Running all aspects of a JVM build in a purely distributed fashion is not a panacea. Existing Gradle Build Tool features like incremental task execution, compilation avoidance, incremental compilation, build cache and configuration cache are available today and greatly reduce build times, especially for the most frequent incremental changes. Additionally, commercial features in Gradle Enterprise like [Test Distribution](https://gradle.com/gradle-enterprise-solutions/test-distribution/) and [Predictive Test Selection](https://gradle.com/gradle-enterprise-solutions/predictive-test-selection/) drastically reduce test execution time which is the primary bottleneck for most builds.

Feedback
--------

Let us know if you have any questions on our [forums](https://discuss.gradle.org/) or [Gradle Community Slack](https://gradle.com/slack-invite).
