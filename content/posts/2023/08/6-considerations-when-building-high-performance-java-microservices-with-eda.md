---
title: "Building High-Performance Java Microservices with EDA"
slug: "6-considerations-when-building-high-performance-java-microservices-with-eda"
date: "2023-08-16T21:34:18+00:00"
lastmod: "2023-08-17T10:01:03+00:00"
description: "Renowned for its resilience and low latency, EDA is a reliable choice for developing robust, high-performing microservices."
authors:
  - "rob-austin"
image: "/images/posts/2023/08/6-considerations-when-building-high-performance-java-microservices-with-eda/Screenshot-2023-08-10-at-5.13.36-PM-1024x607-1.png"
categories:
  - "Chronicle Software"
  - "Developer Tools"
  - "Java"
  - "Java Core"
  - "JavaFX"
tags:
related_posts:
  - "automatically-creating-microservices-architecture-diagrams"
  - "billions-of-messages-tcp-ip"
  - "building-custom-solutions-vs-buy-and-build-software"
  - "intro-to-the-boxlang-formatter"
frozen: false
---

**Event-Driven Architecture (EDA) is a design principle focused on the creation, detection, and reaction to events.**

Renowned for its resilience and low latency, EDA is a reliable choice for developing robust, high-performing microservices.

Moreover, this method can be helpful in improving productivity and making the process of cloud migration smoother.

![](/images/posts/2023/08/6-considerations-when-building-high-performance-java-microservices-with-eda/Screenshot-2023-08-10-at-5.13.36-PM-1024x607.png)

In this article we will outline 6 key considerations and tactics for developing such services.

### 1) Crafting Event-Based Microservices {#h3-0-1-crafting-event-based-microservices}

Within EDA, microservices interact with each other through events. An event is simply an immutable indication that something has happened. Microservices register their interest in a subset of events and perform their processing by reacting to these events when they occur. On completion of handling of an event, microservices will usually post one or more events reflecting the result of this processing, which will trigger further downstream microservices.

For simplicity, we treat all inputs as recorded, replayable events. These inputs include the wall clock, reference information, configuration details, commands, and queries. For instance, timestamps are derived from the most recent wall clock event, so they are replayable, and a command or query is modelled as an event signifying that such a command or query has been requested.

The EDA environment manages events using an immutable, ever-growing journal or log. This methodology means that microservices become less reliant on each others' internal operation (loosely coupled), making systems more flexible in many ways, facilitating different deployment options, and improving scalability.

Microservices developed within an event-driven framework are inherently simpler to design, test, and reason about. Each microservice is a function of its code and all the events it has ever processed. This aspect simplifies the creation of behaviour-driven tests, essentially boiling down to a data-in and data-out scenario. This simplifies the maintenance of the software.

### 2) Implementing Application Logic within an Event-Driven Context {#h3-1-2-implementing-application-logic-within-an-event-driven-context}

In an EDA application, events are defined to model those in your business domain. Application components react to these events in ways that model the activities of your business processes. Data associated with an event encapsulated within the event's payload can be implemented in the application as a Data Transfer Object (DTO).

Representing events in a single, immutable event stream has the additional advantage of providing an audit trail of all the state changes that have occurred during the execution of the application, making it easier to analyse unexpected behaviour, generate test environments that mirror production environments and satisfy regulatory requirements. The event stream becomes the single source of truth throughout the application.

Adopting a lightweight, comprehensive recording strategy eliminates the need for extensive logging, minimising overhead and latency. To replicate the application's state, retrieve the event journal and replay the microservices to the desired point. This approach allows you to debug and verify issue resolutions in the application proactively rather than waiting for the issues to recur.

### 3) Optimising Microservice Performance {#h3-2-3-optimising-microservice-performance}

Using high performance, low latency messaging, microservices can communicate as fast as threads in a monolith while still maintaining key benefits of microservices. These include distinct contracts between components, independent testing and development, a comprehensive record of all interactions, and independence in deployment strategies.

Despite a system being distributed across numerous data centres globally, the efficiency of these microservices means that a single machine can effectively handle the critical, most latency-sensitive processing tasks.

We generally conduct latency benchmarks for single-threaded services at one hundred thousand events per second. A service requiring higher throughput can handle loads exceeding a million events per second.

Moreover, each component will operate fastest when event processing is performed in a single thread since this eliminates the significant overhead of lock contention, as there will be no concurrent access to mutable state within the component.

### 4 -- 6) Event Replication, Deterministic Services, and Live Upgrades {#h3-3-4-6-event-replication-deterministic-services-and-live-upgrades}

We use Chronicle Queue as an event store, with total ordering and replication of this journal, from leader to followers, followers will see exactly the same data in the same order, with the same identifier for each message.

Chronicle Services is a Java-based Microservices framework that provides features which can be used to ensure that your services are deterministic. You can be sure that the follower services will be in the same state as the leader and be ready to take over from it.

We see increasing demand for support for live upgrades, and our consulting team can help you build services that can seamlessly transition between instances running different software versions and revert back if necessary.

### Support and Assistance {#h3-4-support-and-assistance}

At Chronicle Software, our Chronicle Services Framework and our Chronicle Queue messaging layer will empower you to create your high-performance, resilient microservices architecture.

You can leverage our software to build your solution, and we can offer support and workshops to guide you through this process.

We also provide comprehensive documentation and product resources for learning.

Additionally, you can augment your team by working with someone from Chronicle Software.

We suggest investing in ongoing production support to ensure smooth operation and maintain system reliability once your system is in production.

### Links {#h3-5-links}

Website: <https://chronicle.software/services/>

Email: [\[email protected\]](/cdn-cgi/l/email-protection)

Demo request: <https://chronicle.software/demo/?product=services>

Documentation: Services Docs
