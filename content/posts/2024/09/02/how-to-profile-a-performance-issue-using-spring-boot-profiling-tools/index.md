---
title: "How to profile a performance issue using Spring Boot profiling tools"
slug: "how-to-profile-a-performance-issue-using-spring-boot-profiling-tools"
date: "2024-09-02T09:56:13+00:00"
lastmod: "2024-09-02T10:03:50+00:00"
description: "Profiling performance issues and establishing robust monitoring and observability are critical for maintaining the health and efficiency of your Spring Boot application."
canonical: "https://digma.ai/how-to-use-spring-boot-profiling-tools/"
authors:
  - "nasim-salmany"
image: "1_YcY6m7k2T-2AD_ymmgYq1w.webp"
categories:
  - "Performance"
  - "Profiler"
  - "Spring"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "a-simple-service-with-spring-boot"
  - "annotation-free-spring"
frozen: false
---

**In today's fast-paced development environment, ensuring the performance and reliability of your services is critical. Imagine you are part of a team responsible for several services essential to both internal teams and external customers. These services are the backbone of various business operations, and any downtime or performance degradation can have significant repercussions.**{#c79c}

Now, picture this scenario: your team has not yet implemented a robust monitoring and observability system. One day, you start receiving emails and messages from frustrated clients reporting that your service is down. Panic sets in as your team tries to figure out what went wrong. Without proper monitoring tools, finding the root cause of issues in production is challenging. Persistent downtime increases pressure, affecting business operations and customer satisfaction.{#acf3}

This situation highlights a common but critical challenge: the need for effective performance profiling and monitoring of your Spring Boot applications. Without these tools, you're essentially flying blind, making it extremely difficult to troubleshoot issues under pressure.{#70d5}

In this article, we explore the tools available for profiling performance issues and understand the scenarios where profiling is crucial. By the end, we'll delve into features of Digma, empowering developers with insights even during the development phase.{#0b0e}

## Understanding Various Aspects of Profiling Performance Issues

Profiling a Spring Boot performance issue involves analyzing the application's runtime behavior to identify and diagnose inefficiencies, bottlenecks, and resource constraints. This includes **monitoring CPU** and **memory usage** , **thread activity** , **garbage collection** , and **database interactions**. Profiling tools collect data on method execution times, resource usage, and other performance metrics to help pinpoint the root causes of performance issues.{#5721}  
![How to profile a performance issue using Spring Boot profiling tools - 1*YcY6m7k2T 2AD ymmgYq1w](https://miro.medium.com/v2/resize:fit:720/format:webp/1*YcY6m7k2T-2AD_ymmgYq1w.png "How To Profile A Performance Issue Using Spring Boot Profiling Tools")

Visualizing Spring Boot Performance Profiling Process

## key scenarios where Spring Boot profiling tools are essential

### High Latency and Slow Response Times:

> 1. Analyze the time taken for each layer of the application (e.g., controller, service, repository).  
>
> 2. Detect inefficient algorithms or processing logic.{#11f4}

### High CPU Usage

> 1. Find CPU-intensive methods or loops that need optimization.  
>
> 2. Analyze thread dumps to identify threads consuming excessive CPU.  
>
> 3. Detect performance issues related to garbage collection{#3b28}

### High Memory Usage and Memory Leaks

> 1. Identify memory leaks by analyzing heap dumps.  
>
> 2. Monitor object allocation and garbage collection activity.  
>
> 3. Detect excessive creation of short-lived objects that can increase garbage collection overhead.{#6e42}

### Slow Database Queries

> 1. Use database profiling tools to identify slow queries.  
>
> 2. Optimize SQL queries and database indexes.  
>
> 3. Monitor connection pool usage to ensure efficient database connections.{#0db8}

### Network Latency and I/O Issues

> 1. Measure the time taken for network calls and external service interactions.  
>
> 2. Identify slow or failing network connections.  
>
> 3. Optimize HTTP client configurations and retry mechanisms.{#3d70}

### Increased Error Rates and Failures

> 1. Examine stack traces and error logs to identify the source of exceptions.  
>
> 2. Identify resource contention issues such as deadlocks or excessive locking.{#c864}

Profiling in these key cases helps identify and resolve performance bottlenecks, ensuring your Spring Boot application runs efficiently and reliably. By addressing these scenarios, you can enhance user experience, improve resource utilization, and maintain the overall health of your application.{#f369}

## Java and Spring Boot Profiling Tools

To effectively profile performance issues in Spring Boot applications, you can utilize a combination of tools designed for different aspects of performance analysis. [**JProfiler**](https://www.ej-technologies.com/products/jprofiler/overview.html) and [**YourKit**](https://www.yourkit.com/) provide deep insights into CPU and memory usage, helping to identify bottlenecks and memory leaks. [**VisualVM**](https://visualvm.github.io/), an open-source profiler, offers real-time monitoring and detailed performance analysis.{#89ee}

Spring Boot [**Actuator**](https://docs.spring.io/spring-boot/reference/actuator/index.html), combined with [**Micrometer**](https://micrometer.io/), allows for the collection of detailed application metrics and integration with monitoring systems like [Prometheus](https://prometheus.io/) and [Grafana](https://grafana.com/products/cloud/?src=ggl-s&mdm=cpc&camp=b-grafana-exac-emea&cnt=118483912276&trm=grafana&device=c&gad_source=1&gclid=Cj0KCQjwmMayBhDuARIsAM9HM8fJTI96dUbcgaKZj1lF6bSZJ1mhAmcNQ0sB-fAn4AIA2HoXfm8CWVIaAhkoEALw_wcB).{#51f5}

Additionally, distributed tracing tools like [**Zipkin**](https://zipkin.io/) or [**Elastic APM**](https://www.elastic.co/observability/application-performance-monitoring?utm_campaign=Google-B-EMEA-N-Exact&utm_content=Observability-APM&utm_source=google&utm_medium=cpc&device=c&utm_term=elastic+apm&gad_source=1&gclid=Cj0KCQjwmMayBhDuARIsAM9HM8fHRspTtJ9sxu-qie4gQJSqNtFFmv2KGxI6ccZ3vdXi7HYSVKTJrJQaAumtEALw_wcB) help trace and diagnose latency issues across microservices. Using these tools together enables comprehensive performance profiling and optimization of your Spring Boot applications.{#61f4}

We wrote a great article you can check out: https://digma.ai/9-best-java-profilers-to-use-in-2024/

### [JProfiler](https://www.ej-technologies.com/products/jprofiler/overview.html)

> A powerful Java profiler that provides comprehensive insights into CPU, memory, and thread usage.{#8525}
>
> **Use Case:** Suppose your Spring Boot application is experiencing slow response times. By attaching JProfiler, you can analyze CPU hotspots, identify methods consuming excessive CPU cycles, and optimize them for better performance. For instance, you might discover that a particular database query is inefficient, allowing you to refactor it for improved speed.{#1590}

### VisualVM

> Open-source profiling tool that comes bundled with the JDK, offering basic yet effective profiling features.{#00e7}
>
> **Use Case:**VisualVM can be used to monitor a Spring Boot application running in a production environment. By analyzing heap dumps and monitoring garbage collection, you can ensure that your application maintains optimal memory usage, preventing out-of-memory errors and improving overall stability.{#6bf8}

### YourKit

> Java profiling tool known for its user-friendly interface and detailed analysis capabilities.{#3812}
>
> **Use Case:** If your application suffers from memory leaks, YourKit can help. By profiling your Spring Boot application, you can track memory allocation, identify objects that are not being garbage collected, and pinpoint the root cause of memory leaks. This enables you to clean up unused objects and improve memory management.{#8f1f}

### Digma

> Innovative platform that introduces Continuous Feedback to the development cycle. It automates the discovery of performance issues and conducts Root Cause Analysis (RCA) effortlessly.{#43af}
>
> **Use Case:** With[Digma,](https://digma.ai/how-it-works/) developers can swiftly detect bottlenecks in their Spring Boot applications. By profiling the application, Digma identifies areas of inefficiency and provides detailed insights into why these issues occurred. This enables developers to optimize their code for improved performance and reliability.{#e8ca}
>
> We will explore more on this article.{#3011}

## Spring Boot 3 built-in tools for monitoring and observability

Monitoring and performance profiling are distinct yet complementary. Monitoring provides continuous visibility and alerts to potential issues, while profiling offers the detailed analysis needed to diagnose and fix those issues. By integrating both approaches, teams can ensure their Spring Boot applications run efficiently and reliably{#b676}

When it comes to monitoring and maintaining the performance of Spring Boot applications, Spring Boot provides several [**built-in tools**](https://spring.io/blog/2022/10/12/observability-with-spring-boot-3) that can help you track your applications' health, metrics, and other vital statistics. Here are some key built-in tools and features:{#93b0}

### Actuator

> Spring Boot Actuator provides production-ready features to help you monitor and manage your application. With Actuator, you can expose various endpoints that give insight into the application's state and performance.{#a868}

**Health Checks:** The /actuator/health endpoint provides detailed information about the health of the application and its components.{#1d98}

**Metrics:** The /actuator/metrics endpoint exposes various application metrics such as memory usage, CPU usage, and HTTP request statistics.{#da24}

**Info Endpoint:** The /actuator/info endpoint can display arbitrary application information, which can be useful for showing version numbers or build information.{#e051}

**Environment:** The /actuator/env endpoint exposes properties from the Spring Environment, including system properties and configuration properties.{#bd75}

### Micrometer

> [Micrometer](https://micrometer.io/) is an application metrics facade that supports numerous monitoring systems, including **Prometheus** , **Graphite**, and more. Spring Boot integrates Micrometer to collect metrics and send them to the desired monitoring system.{#dd28}

**Rich Metrics Collection:** Collects JVM metrics, HTTP request metrics, and custom application metrics.{#9334}

**Integration with Monitoring Systems:** Supports integration with Prometheus, Grafana, Datadog, New Relic, and more.{#e0ab}

### Comparison

* **Profiling** dives deep into the performance details of specific parts of an application to optimize and improve efficiency.
* **Monitoring**is about tracking the health and performance of an application in real time.
* **Observability** provides a holistic view of an application's state by combining metrics, logs, and traces to understand not just what is happening but why it is happening.

## Using Digma for Early Detection and Profiling of Spring Boot Performance Issues

[Digma](https://digma.ai/) is a Continuous Feedback platform designed to integrate observability directly into the development process. It aims to help developers gain real-time insights into their code's performance, scalability, and potential issues right from their Integrated Development Environment (IDE). By doing this, Digma enables developers to identify and address problems early in the development cycle, improving overall code quality and reducing production issues.{#12c4}

**Requirements**{#b598}

* Set up your spring boot application
* Add the [Digma plugin](https://docs.digma.ai/digma-developer-guide/installation/readme-1)to your development environment.
* Add [observability](https://docs.digma.ai/digma-developer-guide/instrumentation/spring-spring-boot-dropwizard-and-default/covering-more-of-your-code-with-observability) to your code
* Install and set up JMeter.

This setup includes a sample Spring Boot application with an inefficient query. The query retrieves all products from the database and sorts them by their sales count in descending order.{#3a35}

Let's assume we have an Product entity.

```
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int sales;
    // Getters and Setters
}
```

Create a repository interface with a method including a query.

```
package com.example.demo.service;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @WithSpan
    public List<Product> getBestSellingProducts() {
        return productRepository.findBestSellingProducts();
    }
}
```

Create a controller to handle HTTP requests.

```
package com.example.demo.controller;
import com.example.demo.entity.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
@RestController("/api/v1/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Observed
    @GetMapping("/best-selling")
    public List<Product> getBestSellingProducts() {
        return productService.getBestSellingProducts();
    }
}
```

Run your Spring Boot application and test the endpoints. You can use a tool like Postman or simply your web browser to send GET requests to:

* <http://localhost:8080/api/v1/products/best-selling>

> Simulate high traffic using [Jmeter](https://jmeter.apache.org/usermanual/get-started.html).{#bfcb}

Use load testing tools like Apache JMeter, Gatling, or Locust to generate a high volume of requests to your endpoints. This will help us to generate insight into Digma.{#50ed}
![testing tools like Apache JMeter, Gatling, or Locust to generate a high volume of requests to your endpoints.](https://miro.medium.com/v2/resize:fit:1100/format:webp/1*RJdOontfwrBFbzak7B2VAQ.png "How To Profile A Performance Issue Using Spring Boot Profiling Tools")

Let's look at Digma's features for understanding performance issues and analyzing our application.{#af95}

### Duration

The [duration](https://docs.digma.ai/digma-developer-guide/digma-features/analytics/duration) feature in Digma provides comprehensive insights into the performance of your application, particularly focusing on how long specific operations take to complete. This feature aggregates duration statistics for selected assets, allowing you to monitor and analyze performance trends over time. By utilizing this feature, you can identify recent changes in performance and understand how different parts of your application behave under various conditions.{#d0dc}

In observability, we can see all recent calls, and by clicking on calls we get to see the insights.{#1ec0}

<figure class="wp-block-image size-large is-resized">
 <img decoding="async" title="How To Profile A Performance Issue Using Spring Boot Profiling Tools" src="https://miro.medium.com/v2/resize:fit:720/format:webp/1*OViVway4sDw2tYTuEfriiQ.png" alt="Duration ">
</figure>

**Last Call Duration:**   

The duration of the most recent call to the endpoint. This is a snapshot of how long the last request took to process.{#b6b4}

**Median Duration:**   

The median value indicates that half of the requests to this endpoint are processed in less than 1.93 milliseconds, and half take longer. This is a good measure of typical performance.{#a7db}

**Slowest 5%:**   

The slowest 5% of the requests take 6.69 milliseconds or longer. This highlights the tail latency, showing the worst-case scenarios for this endpoint.{#8348}
> By leveraging the detailed insights provided by Digma's duration feature, you can maintain optimal performance for your endpoints, ensure a smooth user experience, and proactively address any issues that arise.{#f129}

### Scaling Issues:

Digma's [scaling issue insight](https://docs.digma.ai/digma-developer-guide/digma-features/insights/scaling-issue) delves into the performance of your code when subjected to concurrent operations, offering an in-depth analysis of its behavior under load. This feature not only identifies any performance bottlenecks but also automatically investigates the root cause of any detected issues, providing valuable insights for optimizing your application's scalability and efficiency.{#ab19}
![How to profile a performance issue using Spring Boot profiling tools - image](https://digma.ai/wp-content/uploads/2024/06/image.png "How To Profile A Performance Issue Using Spring Boot Profiling Tools")

This alert indicates that the performance of "/best-selling" endpoint degrades as the number of concurrent requests increases. The constant degradation suggests a potential problem with the scalability of this endpoint, which could affect user experience and system performance under load.{#078f}

* **Duration**: The response time for the API calls ranges from 16.57 milliseconds to 679.56 milliseconds.
* **Ticket**: This option allows you to create a ticket for further investigation and resolution of the issue.

> By simulating multiple simultaneous user interactions, Digma's scaling issue insight offers a comprehensive understanding of your system's performance characteristics, empowering you to fine-tune your code and infrastructure for enhanced reliability and responsiveness under varying workloads.{#abc8}

### Bottleneck

The [bottleneck](https://docs.digma.ai/digma-developer-guide/digma-features/insights/bottleneck) feature is crucial for identifying parts of your application that cause delays, enhancing overall performance. It highlights specific assets, such as methods, services, or database queries, that consume the most time during request processing.{#909c}

By distinguishing between synchronous and asynchronous executions, it ensures that only blocking assets are marked as bottlenecks, allowing developers to focus on critical paths that directly impact user experience.{#a069}

We can add another method that simulates a more time-consuming process:{#8f3c}
![How to profile a performance issue using Spring Boot profiling tools - 1* vITaeRHSieVNTBaRVNMnw](https://miro.medium.com/v2/resize:fit:720/format:webp/1*-vITaeRHSieVNTBaRVNMnw.png "How To Profile A Performance Issue Using Spring Boot Profiling Tools")

The bottleneck feature helps identify parts of your system that are causing delays by taking up a lot of processing time during a request. It points out which components are slowing things down and could potentially impact the overall performance of your application.

![How to profile a performance issue using Spring Boot profiling tools - 1*itY7Ter9yVO1vNPmLmSoFA](https://miro.medium.com/v2/resize:fit:700/1*itY7Ter9yVO1vNPmLmSoFA.png "How To Profile A Performance Issue Using Spring Boot Profiling Tools")

**Performance Metrics:**{#d000}

**% of Duration:**   

This indicates that the identified method (getProductsByRandomOrder) is consuming 96% of the total time for the request.{#6b3d}

**% of Requests:**   

This means that 6% of the requests are invoking this particular method.{#2341}

**Duration:**   

The method takes 2.05 seconds to execute, which is significantly high and likely the cause of the bottleneck.{#39f5}

It is possible to create tickets for issues detected by Digma{#89f3}  
![An image of a Jira ticket](https://digma.ai/wp-content/uploads/2024/06/image-1.png "How To Profile A Performance Issue Using Spring Boot Profiling Tools")

## Final thoughts: Spring Boot Profiling Tools

Profiling performance issues and establishing robust monitoring and observability are critical for maintaining the health and efficiency of your Spring Boot application. Profiling helps identify and resolve bottlenecks, inefficient code, and resource-intensive processes, ensuring the application performs optimally under various loads.{#a686}

Concurrently, having comprehensive monitoring and observability allows you to track key performance metrics, detect anomalies in real time, and gain insights into the application's behavior. This dual approach not only enhances the user experience by reducing latency and preventing downtime but also enables proactive maintenance, reducing the risk of unexpected failures and facilitating continuous improvement in the application's performance and reliability.
