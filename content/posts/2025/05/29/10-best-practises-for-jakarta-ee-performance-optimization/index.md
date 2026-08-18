---
title: "10 Best Practises For Jakarta EE Performance Optimization"
slug: "10-best-practises-for-jakarta-ee-performance-optimization"
date: "2025-05-29T13:44:01+00:00"
lastmod: "2025-05-29T13:44:04+00:00"
description: "We start a series where we compiled 10 best practices for performance optimizations and suggestions how to implement them using Jakarta EE & Eclipse GlassFish."
canonical: "https://omnifish.ee/10-best-practices-for-jakarta-ee-performance-optimization-2/"
authors:
  - "ondro-mihalyi"
image: "omnifish-logo-turquise-bg-400px-e1663869726664.png"
categories:
  - "Jakarta EE"
  - "Java"
  - "Performance"
  - "Tools"
tags:
related_posts:
  - "ejb-support-in-piranha-via-cdi"
  - "glassfish-embedded-a-simple-way-to-run-jakarta-ee-apps"
  - "glassfish-is-rolling-forward-whats-new"
  - "issues-with-old-glassfish-server-upgrade-to-eclipse-glassfish"
frozen: false
---

> **With this article, we start a series where we compiled 10 best practices for performance optimizations and suggestions how to implement them using [Jakarta EE](https://jakarta.ee/) \& [Eclipse GlassFish](https://omnifish.ee/glassfish/).**
>
> Enjoy reading this initial overview and watching the video about performance tuning Java applications, and stay tuned for upcoming articles from our [OmniFish team](https://omnifish.ee/) that will go into details about each best practice.

To make your [Jakarta EE](https://jakarta.ee/) applications faster and more efficient, here are **10 proven best practices** you can easily implement:

1. [**Optimize Database Operations**:](https://omnifish.ee/performance-best-practice-no-1-optimize-database-operations/) Use connection pooling, prepared statements, and statement caching to reduce query times and resource usage.
2. [**Implement Caching**:](https://omnifish.ee/performance-best-practice-no-2-implement-caching/) Combine client-side, in-memory, and distributed caching to minimize server load and improve responsiveness.
3. **Fine-Tune Runtime Settings**: Adjust heap sizes, JVM options, Garbage collection options, thread pool sizes, or network listener settings
4. **Leverage Asynchronous Processing**: Handle tasks in parallel to boost system throughput and user experience.
5. **Manage Components Effectively**: Reduce overhead by optimizing resource allocation and disabling unused features.
6. **Compress Network Data**: Enable compression to reduce data transfer times and improve response speeds.
7. **Use Monitoring Tools**: Track performance metrics like memory usage, CPU load, and database connections to identify bottlenecks.
8. **Test Performance Regularly** : Spot and fix issues early with profiling tools like [VisualVM](https://visualvm.github.io/) or [JProfiler](https://www.ej-technologies.com/jprofiler), or using performance test tools like [Apache JMeter](https://jmeter.apache.org/), [Gatling](https://gatling.io/), or [Bombardier](https://github.com/codesenberg/bombardier)
9. **Streamline Code** : Replace inefficient code patterns (e.g., use `StringBuilder` instead of string concatenation for better performance, use sets or maps instead of lists for big collections where appropriate, use asynchronous I/O and streaming data instead of blocking calls and request-response communication for bigger payloads)
10. **Adopt New \& Standard Java Features** : Use features like Java Streams API for streamed and parallel processing and [JCache](https://www.baeldung.com/jcache) for standardized caching.

These strategies directly impact user experience, scalability, and operational costs, helping you build applications that perform well under pressure.

## Quick Comparison

|     **Performance Area**      |                              **Key Practice**                              |                       **Impact**                        |
|-------------------------------|----------------------------------------------------------------------------|---------------------------------------------------------|
| Database Operations           | Connection pooling, prepared statements, tuned Jakarta Persistence queries | Faster queries, fewer queries, less resource usage      |
| Caching                       | In-memory, client-side, distributed caches                                 | Reduced database load, reduce load on system components |
| Runtime Settings              | Adjust heap sizes, JVM configurations, Server tuining                      | Improved stability and speed                            |
| Asynchronous Processing       | Parallel task handling                                                     | Higher throughput                                       |
| Component Management          | Disable unused features                                                    | Lower memory overhead                                   |
| Network Data Compression      | Enable compression                                                         | Faster response times                                   |
| Monitoring Tools              | Gather metrics from JVM, OS, Server, Applications                          | Early bottleneck detection                              |
| Performance Testing           | Regular testing and profiling with tools                                   | Easier optimization                                     |
| Streamline Code               | Use efficient code patterns                                                | Faster execution, lower memory use                      |
| New \& Standard Java Features | Streams API, JCache                                                        | Enhanced responsiveness                                 |

## Secrets of Performance Tuning Java on Kubernetes by Bruno Borges

Before we go into details about each best practice in the next articles, I'd like to leave this video for you get an interesting perspective on performance tuning of Java apps from **Bruno Borges** , an amazing Java developer advocate, [Java Champion](https://javachampions.org/), and a person I deeply respect and admire.

{{< youtube wApqCjHWF8Q >}}

## Next Steps

To put these ideas into action, you should start with **Continuously Monitoring** your applications. Set up monitoring of your applications and collect metrics about what's going on. Track CPU usage, memory, garbage collection, speed of processing requests, etc.. This data helps identify bottlenecks early and will prove invaluable to understand what's happening once you have performance issues and how to address them. They will also show the impact of the optimizations you implement in the future

In the next article, we'll go into more details about **improving database performance**, so stay tuned...
