---
title: "Building Reactive Java Applications with Spring Framework | Foojay Today"
slug: "building-reactive-java-applications-with-spring-framework"
date: "2022-02-15T14:09:28+00:00"
lastmod: "2022-05-30T15:36:37+00:00"
description: "Pretty much all Java developers are familiar with Spring Pet Clinic. Let's get to know the reactive implementation of it."
canonical: "https://medium.com/building-the-open-data-stack/building-a-reactive-implementation-of-spring-petclinic-in-apache-cassandra-7cd42c383291"
authors:
  - "cedrick-lunven"
image: "https://foojay.io/wp-content/uploads/2022/01/1_bAqotS_F1PipZLOHx_Tcww.jpeg"
categories:
  - "Apache Cassandra"
  - "Databases"
  - "DataStax"
  - "reactive"
  - "Spring"
tags:
related_posts:
  - "the-search-for-a-cloud-native-atabase"
  - "build-a-status-dashboard-using-spring-boot-and-astra-db"
  - "aggregation-optimization-in-mongodb-unnecessary-unwinds-part-2"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
frozen: false
---

![](/images/posts/2022/02/building-reactive-java-applications-with-spring-framework/1_bAqotS_F1PipZLOHx_Tcww-1024x403.jpeg)

*In one of our many free tutorials on* [*DataStax Developers YouTube channel*](https://www.youtube.com/c/DataStaxDevs/videos)*, we walked you through* [*how to build a reactive implementation of Spring PetClinic*](https://www.youtube.com/watch?v=1aRbndIcXV4)in *Apache Cassandra® using Spring WebFlux. The full series is* [*available on YouTube*](https://github.com/datastaxdevs/workshop-spring-reactive)*.*{#08a7}

If you're a Java developer who uses the Spring ecosystem, you've probably seen the [Spring Pet Clinic](https://github.com/datastaxdevs/workshop-spring-reactive). In this workshop, we will walk you through a new reactive implementation of the Pet Clinic backend that uses Spring WebFlux and Apache [Cassandra](https://www.datastax.com/what-is/cassandra)® (via [DataStax Astra DB](https://astra.dev/3lSpuQp)).{#47b5}

The cloud-native database-as-a-service built on Cassandra fits the highly concurrent, non-blocking nature of our reactive application. We'll do all of our work in the cloud with Gitpod's open-source, zero-install and collaborative development environment.{#4ad6}
![](/images/posts/2022/02/building-reactive-java-applications-with-spring-framework/0_N21fX8nSMgfaQC_X.png) Figure 1: A REST API using the Spring framework.

The steps we will take you through are:{#f2b3}

* Setting up Astra database
* Theory: synchronous vs. asynchronous vs. reactive programming
* Using the Gitpod platform
* Introducing the Spring Boot and WebFlux frameworks
* Reviewing backend code
* Starting frontend application

**Setting up your free Astra database**{#09da}

[DataStax Astra DB](https://astra.dev/3lSpuQp), built on the best distribution of Cassandra, provides the ability to develop and deploy data-driven applications with a cloud-native service, without the hassles of database and infrastructure administration. By automating tuning and configuration, DataStax Astra radically simplifies database operations.{#2072}
![](/images/posts/2022/02/building-reactive-java-applications-with-spring-framework/0_699aI-YPOwrI1vQ-.png) Figure 2: Datastax Astra DB Benefits.

Follow the [step-by-step instructions](https://github.com/datastaxdevs/workshop-spring-reactive) to [create your Astra database](https://auth.cloud.datastax.com/auth/realms/CloudUsers/protocol/openid-connect/registrations?client_id=auth-proxy&response_type=code&scope=openid+profile+email&redirect_uri=https://astra.datastax.com/welcome), and once your database is ready, you can copy your credentials over to [GitHub](https://github.com/datastaxdevs/workshop-spring-reactive).{#d7fa}

Most Java developers use **synchronous programming**. When you initiate a session, you execute quickly and you will get a response. Then you send the parameter to the API, and the driver will create a query. You bind the parameter that simply maps the parameter to the query. You execute the query and get back an object called a ResultSet.{#ee8d}
![](/images/posts/2022/02/building-reactive-java-applications-with-spring-framework/0_SI3IRVrpySGMTkGT.png) Figure 3: Synchronous queries weaknesses.

The issue with synchronous programming is that you need to wait. It can take a lot of time if you are querying for a lot of data, or a big cluster. Although synchronous programming is very simple, it can block communication. This means that nothing else in the application proceeds until the result from the query is returned. The application blocks for the entire round trip, from when the query is first sent to the database until the results are retrieved and returned to the application.{#3418}

The advantage of synchronous queries, however, is that it is simple to tell when a query completes, so the execution logic of the application is easy to follow. However, synchronous queries cause poor application throughput.{#9560}
![](/images/posts/2022/02/building-reactive-java-applications-with-spring-framework/0_ZNpNwFKcH-Dfxzcb.png) *Figure 4: Asynchronous queries weaknesses.*

An **asynchronous query** executes call does not block for results. With asynchronous queries, you will put the parameter in to prepare your queries, bind the parameters to your queries, and execute the query.{#5301}

But instead of waiting for the result, the customer and the driver will immediately give you an object called a completion stage. A future is returned from the asynchronous execute call. A future is a placeholder object that stands in for the result until the result is returned from the database. Depending on the driver and feature set of the language, this future can facilitate asynchronous processing of results. This typically allows high throughput. However, because you can process the result only when you get a callback, this can create sync resistance.{#fa1c}
![](/images/posts/2022/02/building-reactive-java-applications-with-spring-framework/0_4oVMOudaH5MSgZl6.png) *Figure 5: The Reactive Manifesto.*

Now, coming to the [reactive manifesto](https://www.reactivemanifesto.org/), the programming offers what is missing with the synchronous-only stack by achieving a responsive service. This means that the system always responds in a timely manner and stays responsive even in the face of any failure. It also offers a scale-out dynamic.{#28aa}

When it comes to huge volumes of data or multi-users, we often need asynchronous processing to make our systems fast and responsive. In Java, a representative of old object-oriented programming, asynchronicity can become really troublesome and make the code hard to understand and maintain. So, reactive programming is especially beneficial for this 'purely' object-oriented environment as it simplifies dealing with asynchronous flows.{#e0d2}
![](/images/posts/2022/02/building-reactive-java-applications-with-spring-framework/0_fqSkaGYcLnlgRlVR.png) *Figure 6: Reactive Queries.*

Gitpod is an open-source Kubernetes application providing prebuilt, collaborative development environments in your browser. Gitpod provides step-by-step screenshots to launch and build the Spring PetClinic Reactive backend application, created by our special workshop guest, Moritz Eysholdt from [TypeFox](https://www.typefox.io/).{#638a}
![](/images/posts/2022/02/building-reactive-java-applications-with-spring-framework/0_XGlsf8kG9qUyPWd8.png) *Figure 7: When Gitpod finishes building the app, a new tab will open in your browser showing the following.*

With its simple abstraction, Spring Reactor is a popular framework for Java from Spring developers. Spring Framework is a Java platform that provides comprehensive infrastructure support for developing Java applications. Spring enables you to build applications from "plain old Java objects" (POJOs) and to apply enterprise services non-invasively to POJOs.{#80bf}
![](/images/posts/2022/02/building-reactive-java-applications-with-spring-framework/0_GjnyqjsGfFBzoZMb.png) *Figure 8: What Spring can do.*

We will also introduce [Spring Boot](https://medium.com/building-the-open-data-stack/building-microservices-with-spring-data-cassandra-and-stargate-io-613f0aff8188), a tool that makes developing web applications and microservices with Spring Framework faster and easier. Spring Boot can create stand-alone Spring applications (without deploying WAR files), simplify your build configuration, and automatically configure Spring and 3rd party libraries. Spring Boot also has production-ready features such as metrics, health checks, and externalized configuration.{#7678}

At the heart of the Spring Framework are two fundamental features: inversion of control (IoC) and dependency injection (DI).{#74cc}

* Inversion of control essentially transfers the control of objects to the Spring Framework. Unlike traditional programming, where custom code makes calls to a library, IoC allows the framework to control the flow of a program. This keeps Java classes independent of each other for increased modularity and extensibility.
* Dependency injection is a pattern in Spring that's used to implement IoC, where the Spring container is in charge of "injecting" objects into the right dependencies. This allows for the loose coupling of components and shifts the responsibility of managing components onto the container.

![](/images/posts/2022/02/building-reactive-java-applications-with-spring-framework/0_k-fjVybBZyrmbQto.png) *Figure 9: How Spring Boot works.*

Let's have a look inside the main component `spring-petclinic-reactive` to see which libraries and frameworks have been used.{#d246}
![](/images/posts/2022/02/building-reactive-java-applications-with-spring-framework/0_JzQKikxBUVJGXWG9.png) *Figure 10: Understanding the architecture.*

* **Spring-boot**: Spring Boot makes it easy to create stand-alone, production-grade Spring-based Applications that you can "just run". We take an opinionated view of the Spring platform and third-party libraries so you can get started with minimum fuss. Most Spring Boot applications need minimal Spring configuration.
* **Spring-Security**: Spring Security is a powerful and highly customizable authentication and access-control framework. It is the de-facto standard for securing Spring-based applications. Spring Security is a framework that focuses on providing both authentication and authorization to Java applications. Like all Spring projects, the real power of Spring Security is found in how easily it can be extended to meet custom requirements.
* **Spring-WebFlux**: Spring sub-framework to create Reactive Rest Endpoint.
* **Spring-Actuator**: Expose Endpoints to expose metrics to third party systems. Examples are health, infos, jmx,and prometheus.
* **Spring-Test**: Enabled unit testing and mocking with Spring configuration and beans.
* **Spring-Cloud**: Spring Cloud provides tools for developers to quickly build some of the common patterns in distributed systems (e.g. configuration management, service discovery, circuit breakers, intelligent routing, micro-proxy, control bus, one-time tokens, global locks, leadership election, distributed sessions, cluster state). Coordination of distributed systems leads to boilerplate patterns, and using Spring Cloud developers can quickly stand up services and applications that implement those patterns. They will work well in any distributed environment, including the developer's own laptop, bare metal data centers, and managed platforms such as Cloud Foundry.
* **SpringFox *(Swagger)*** : Annotation-based rest documentation generation and test client generation (`swagger-ui`).

To understand the underlying data model implemented in Apache Cassandra, check out our Gitpod guide.{#1941}
![](/images/posts/2022/02/building-reactive-java-applications-with-spring-framework/0_Q7degYIlhNIUVZZR.png) *Figure 11: Frontend of Spring PetClinic.*

Once you configure and run the application, you can also test CRUD with Swagger as a hands-on exercise as follows:{#3d81}
![](/images/posts/2022/02/building-reactive-java-applications-with-spring-framework/0_wx9X4vg2vLqj615f.png) *Figure 12: Test CRUD with Swagger.*

And with that, we conclude our workshop on a reactive implementation of Spring PetClinic with Cassandra. To keep learning about open-source technologies and how to use them, simply register for any of our free [DataStax Workshops](https://www.datastax.com/workshops) to get started on your next big app.{#995f}

*Explore more tutorials on our* [*DataStax Developers YouTube channel*](https://www.youtube.com/c/DataStaxDevs/videos)*and* [*subscribe to our event alert*](https://docs.google.com/forms/d/e/1FAIpQLSfEtzzVauuFpFJWUiepYndqchBpNsaOwm6raPJDsMt9nTvMbw/viewform)*to get notified about new developer workshops. For exclusive posts on all things data: Cassandra, streaming, Kubernetes, and more; follow* [*DataStax on Medium*](https://datastax.medium.com/)*.*{#9b13}

1. [Build a Reactive app in Apache Cassandra™ with Spring Framework](https://www.youtube.com/watch?v=1aRbndIcXV4)
2. [Github Workshop Spring Reactive](https://github.com/datastaxdevs/workshop-spring-reactive)
3. [Join our Discord: Fellowship of the (Cassandra) Rings](https://discord.com/invite/pPjPcZN)
4. [Astra DB --- Managed Apache Cassandra as a Service](https://astra.dev/3lSpuQp)
5. [Building Microservices with Spring Data, Cassandra, and Stargate.io](https://medium.com/building-the-open-data-stack/building-microservices-with-spring-data-cassandra-and-stargate-io-613f0aff8188)
6. [DataStax Academy](https://auth.cloud.datastax.com/auth/realms/CloudUsers/login-actions/authenticate?client_id=absorb&tab_id=7jzmpQBmc-w)
7. [DataStax Certifications](https://www.datastax.com/dev/certifications)
8. [DataStax Workshops](https://www.datastax.com/workshops)
