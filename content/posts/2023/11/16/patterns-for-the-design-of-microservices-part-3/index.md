---
title: "Patterns For The Design Of Microservices – Part 3"
slug: "patterns-for-the-design-of-microservices-part-3"
date: "2023-11-16T04:35:02+00:00"
lastmod: "2023-11-19T21:31:57+00:00"
description: "Design patterns plays a pivotal role in designing and solving the commonly occurring problems in software application."
authors:
  - "mahendra1413"
image: "MSPatternsOne.png"
categories:
  - "Java"
  - "Kotlin"
  - "Microservices"
  - "Observability"
  - "Spring"
tags:
related_posts:
  - "patterns-for-the-design-of-microservices-part-1"
  - "patterns-for-the-design-of-microservices-part-2"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
frozen: false
---

In [part1](https://foojay.io/today/patterns-for-the-design-of-microservices-part-1/ "part1"), [part2](https://foojay.io/today/patterns-for-the-design-of-microservices-part-2/ "part2"), we discussed several design patterns that aid in the development of microservices.

This blog post will cover the subsequent design patterns.

* Observability Patterns
* Cross-cutting Concern Patterns

<img fetchpriority="high" decoding="async" class="size-medium wp-image-103034" src="MSPatternsOne-700x394.png" alt="Microservice Architecture" width="700" height="394">

Microservice Architecture Patterns

<br />

Undoubtedly, the logging mechanism benefits troubleshooting issues that arise when executing operations or transactions on multiple deployed microservices.

We can further categorize the observability patterns into six distinct patterns.

* Log Aggregation
* Application Metrics
* Audit Logging
* Distrubuted Tracing
* Exception Tracking
* Health Check API

In the subsequent segment, we will discuss each topic separately.

Log Aggregation
---------------

By implementing the microservice architecture pattern, we partition the monolithic application into several microservices and deploy them separately. Nevertheless, we need to establish a centralized logging mechanism across all the services to ensure seamless communication between them. This approach facilitates developers in tracing and troubleshoot issues that may arise during the application's operation.

The centralized logging file must encompass **error, warning, information, and debug messages**, and it should consolidate all logs from various service instances. And, upon detecting specific occurrences in the logs, we can activate alert mechanisms.

The following services commonly employ capturing and storing logs in applications built on microservice architecture:

* [Elastic Search](https://www.elastic.co/ "Elastic Search")
* [Logz.io](https://logz.io/)
* [AWS Cloud Watch](https://aws.amazon.com/cloudwatch/)
* [Azure Monitor](https://azure.microsoft.com/en-in/products/monitor)
* [Google Cloud Operations Suite](https://cloud.google.com/products/operations)

**Props**   

To capture all the logs, we need substantial infrastructure.

Application Metrics
-------------------

Gaining a comprehensive understanding of the application's performance through the observation of metrics captured throughout its runtime is crucial.

The application shall record and transmit all metrics to a centralized server for safekeeping. Later, we will obtain the metrics from the server to conduct a thorough analysis.

A plethora of instrumentation and metric libraries and tools for capturing application metrics.

We can use tools such as [Prometheus](https://prometheus.io/), [Grafana](https://grafana.com/products/cloud/logs/), AWS CloudWatch, and Azure Monitoring to assist in consolidating the metric.

We can also use instrumentation libraries such as [Prometheus Client Libraries](https://prometheus.io/docs/instrumenting/clientlibs/) and Coda Hale/Yammer [Java Metrics Library](http://metrics.dropwizard.io/3.1.0/)

Audit Logging
-------------

The user needs to capture and store different events while interacting with various instances of microservices in the database. The microservice architecture extensively employs this practice, commonly referred to as audit logging.

Distrubuted Tracing
-------------------

Microservices architectures employ distributed tracing as a crucial technique for monitoring and resolving intricate, distributed systems. It helps in comprehending and visually representing the progression of requests as they navigate through diverse microservices and components, thereby offering valuable insights into the system's performance and behavior. This technique plays a vital role in identifying problems, enhancing performance, and guaranteeing dependability within a microservices environment.

Utilizing the [Spring Boot](https://spring.io/projects/spring-boot) and [Spring Cloud](https://spring.io/projects/spring-cloud), [Micrometer](https://micrometer.io/) frameworks as a Microservice chassis achieves distributed tracing. They offer various capabilities, including Spring Cloud Sleuth and Micrometer. This feature facilitates distributed tracing by instrumenting Spring components to collect trace information and deliver it to a [Zipkin](https://zipkin.io/) Server, Prometheus, or Grafana. These services then gathers and presents the traces.

In the context of event-driven architecture, we employ Message Queue systems such as ActiveMQ, RabbitMQ, or Kafka to capture traces and subsequently transmit them to Monitoring Servers.

Exception Tracking
------------------

In the microservices architecture, separate servers host numerous services and instances. If a runtime issue occurs, the application raises an exception and provides a stacktrace to indicate how it manages the exception.

To optimize code and ensure cleanliness, developers commonly practice writing single exception handling (@RestControllerAdvice) class for the entire application instead of writing it for each individual module.

Health Check API
----------------

In the microservice architecture, a service may be incapable of processing a request despite the application being operational, owing to connectivity problems with databases, messaging systems, or other issues. In such cases, users cannot execute any requests and must activate an alert mechanism.

In the Spring Boot framework, we can validate the application's health by utilizing the **spring-boot-starter-actuator** dependency. The [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html#actuator.endpoints) module creates an /health endpoint, which captures various metrics that aid developers in regularly monitoring the application.

We can further categorize the cross-cutting concern patterns into four distinct patterns.

* External Configuration
* Service Discovery Pattern
* Circuit Breaker Pattern
* Blue Green Deployment Pattern

External Configuration
----------------------

In a monolithic application, the application tightly couples the majority of the business-related properties, database, or any 3rd party related services.

Nevertheless, in a microservice architecture, we can externalize these configurations by utilizing Github for the development, testing, and production environments.

As a result, this approach enables developers to effortlessly modify or enables profiles and business-related properties without the need for deployment.

[Spring Boot's externalized configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config) makes values within the Spring application context accessible. It retrieves them from various sources such as **operating system environment variables, property files, and command line arguments**.

Service Discovery Pattern
-------------------------

In monolithic architecture, a fixed host and port number are used for service communication, utilizing protocols such as REST API and Remote Procedure Calls (RPC).

However, in microservice architecture, applications typically operate within virtualized or containerized environments, resulting in dynamic changes to the number and locations of service instances. Applications employ HTTP/REST protocols to invoke these services.

The Service Discovery pattern has two categories:

### Client-side Service Discovery

This approach involves registering all service instances in the discovery server as soon as the service is up and running. Conversely, the instances are de-registered when the service is stopped or shutdown. This implementation resembles a HashMap and does not involve a load-balancer to handle requests.

### Server-side Service Discovery

Similar to client-side service discovery, this method registers all service instances in the discovery server upon their activation. Likewise, the instances are de-registered when the service is stopped or shutdown. However, in this scenario, a load-balancer is present to manage incoming requests.

In the realm of Microservices, numerous frameworks and components enable the execution of the service discovery pattern. These include **Netflix OSS Components, OpenFeign Kubernetes, Istio, Consul,** and several others.

The [Netflix OSS Components](http://netflix.github.io/) offer [Eureka](https://github.com/Netflix/eureka/wiki/Eureka-at-a-glance), a [Service Registry](https://microservices.io/patterns/service-registry.html), and [Ribbon](https://github.com/Netflix/ribbon) to facilitate communication between services. However, [OpenFeign](https://spring.io/projects/spring-cloud-openfeign) has now substituted Eureka, serving as an abstraction of both the Service Registry and Ribbon.

We will utilize the application name to invoke a service from another. The service registry maintains multiple instances of services, each with dynamically assigned IP addresses and ports. Therefore, by specifying the application name, we can successfully invoke the desired service.

Circuit Breaker Pattern
-----------------------

In certain cases, services may encounter unanticipated failures or undergo maintenance, resulting in the user experiencing unresponsiveness while utilizing them. To enhance the customer/user experience, we will incorporate a circuit-breaker pattern and establish a fallback method that ensures a seamless user journey without causing any inconvenience.

Utilizing [Netflix Hystrix](https://github.com/Netflix/Hystrix) in conjunction with Spring Boot enables us to address latency and fault tolerance, achieving the implementation of this pattern. However, it is important to note that the approach is currently in maintenance mode and no longer supports any further enhancements.

To enhance modularity, we have introduced a new framework called [Resilience4j](https://resilience4j.readme.io/docs). This framework segregates **Circuit Breaker, Rate Limiter, Retry, and Bulkhead** into separate modules, allowing for greater flexibility and customization.

Blue-Green Deployment Pattern
-----------------------------

In microservice architecture, we employ the technique of blue-green deployment to securely update and launch software applications without any interruptions or disturbances. To put it simply, you can envision having two identical sets of your application, with one labeled "blue" and the other labeled "green."

The process operates as follows:

### Blue Deployment

Customers are already utilizing the current and operational application, and this pattern will enhance any new features for it.

### Green Deployment

This is the updated version of your application that you desire to deploy and evaluate.

<https://microservices.io/>
