---
title: "Event-Driven Architecture and Change Data Capture Made Easy"
slug: "event-driven-architecture-and-change-data-capture-made-easy"
date: "2025-02-18T16:44:14+00:00"
lastmod: "2025-02-18T16:44:16+00:00"
description: "Learn about Event-Driven Architecture (EDA) and Change Data Capture (CDC), their use cases, and how they can work together. learn more"
authors:
  - "abo-saad-muaath"
image: "https://foojay.io/wp-content/uploads/2025/01/change-data-capture.gif"
categories:
  - "DataEngineering"
  - "Kafka"
  - "Microservices"
tags:
related_posts:
  - "when-not-to-use-event-driven-architecture-eda"
  - "unified-event-driven-architecture-for-the-cloud-native-enterprise"
  - "why-developers-should-use-apache-pulsar"
  - "full-stream-ahead-astra-streaming-powered-by-apache-pulsar"
frozen: false
---

Hello again! In this article (Part 1), we will discuss two common ways to build modern [software systems](https://mezocode.com/microservice-journey-part-2-design-principles-for-well-crafted-architecture/): **Event-Driven Architecture (EDA) and Change Data Capture (CDC)**. They serve different purposes but can work well together in some situations.

we'll explain **EDA** and **CDC**, discuss their use cases, and explain when to use them and when not to. Using plain language and relatable examples, we'll also explore how they work together.

##### What is Event-Driven Architecture (EDA)?

Event-driven architecture is a way of designing software systems where components communicate by **producing and consuming events** . An event is simply a message that says, "Something happened." For example, if a user places an order on an e-commerce website, the system can create an event like **OrderPlaced**, which other components can react to.

In EDA, components are loosely coupled, meaning they don't need to know much about each other. Instead, they rely on events to trigger actions. This makes systems more flexible and scalable, especially as they grow in size.

##### How Does EDA Work?

EDA typically involves three main parts:

* **Event Producers:** These are the components that generate events. For example, when a user places an order, the **OrderService** might produce an **OrderPlaced** event.
* **Event Consumers:** These are the components that listen to events and react to them. For example, a **NotificationService** might listen to the **OrderPlaced** event and send the user a confirmation email.
* **Event Brokers:** These are tools that handle the delivery of events between producers and consumers. Examples include **Kafka** , **RabbitMQ** , or **AWS EventBridge**.

##### Use Case: EDA in an E-Commerce System

Imagine you're running an online store. When a user places an order, several things need to happen:

1. The payment needs to be processed.
2. The inventory should be updated.
3. A confirmation email should be sent to the user.

Using EDA, the **OrderService** can emit an **OrderPlaced** event. Other services (like **PaymentService** , **InventoryService** , and **NotificationService**) can subscribe to this event and take action independently. This decouples the services and allows them to scale independently.

##### What is Change Data Capture (CDC)?

CDC is a technique for tracking and capturing changes made to a database. Whenever data is added, updated, or deleted in a database, CDC detects these changes and makes them available for other systems or processes.

Unlike EDA, the CDC is not about producing and consuming events explicitly. Instead, it listens to the database and generates events based on changes in the data. Think of it as a way to bridge the gap between traditional database operations and modern event-driven systems.

##### How Does CDC Work?

CDC tools monitor a database's transaction log (a record of all changes made to the database) and convert those changes into events. Popular CDC tools include **Debezium** , **Oracle GoldenGate** , and **AWS DMS**.

##### Use Case: CDC in a Reporting System

Let's say your e-commerce site stores orders in a database. You want to build a real-time reporting system that shows sales metrics (e.g., total sales, top products) without impacting the performance of your main database.

Using CDC, you can capture changes to the **Orders** table (e.g., a new order being added) and stream those changes to a data warehouse like Snowflake or a real-time analytics service like Apache Kafka. This way, your reporting system stays up-to-date without directly querying the production database.

##### EDA vs. CDC: When to Use Each

While EDA and CDC may seem similar because they both deal with events, they solve different problems. Let's compare them:

|      Aspect       |                                        EDA                                        |                                             CDC                                              |
|-------------------|-----------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------|
| **Focus**         | Application-level events (e.g., "an order was placed").                           | Database-level changes (e.g., "a new row was added to the Orders table").                    |
| **Initiator**     | Events are explicitly produced by the application.                                | Events are generated automatically based on database changes.                                |
| **Coupling**      | Loosely coupled components; producers and consumers only share event definitions. | Tightly coupled to the database schema and structure.                                        |
| **Use Case**      | Triggering workflows, decoupling services, enabling real-time communication.      | Synchronizing data between systems, building analytics pipelines, or event-sourcing systems. |
| **Tool Examples** | Kafka, RabbitMQ, AWS EventBridge.                                                 | Debezium, Oracle GoldenGate, AWS DMS.                                                        |

##### When EDA and CDC Can Work Together

In some cases, EDA and CDC can complement each other. For example:

* **Scenario:** Your e-commerce system emits an **OrderPlaced** event when an order is created. However, for reporting purposes, you also need to track changes to the **Orders** database table.
* **Solution:** Use EDA to orchestrate workflows (like payment processing and inventory updates) and CDC to feed data into your reporting or analytics system.

![](https://mezocode.com/wp-content/uploads/2024/12/Screenshot-1446-06-01-at-4.15.47 PM.png)

**Explanation**:

* The CDC Tool listens to the transaction log of the Orders Database to detect changes (e.g., inserts, updates, deletes).
* These changes are turned into events and streamed to downstream systems:
  * A Data Warehouse for reporting and analytics.
  * A Real-Time Dashboard for monitoring metrics.
  * A Search Index for faster querying.

##### Conclusion

EDA and CDC are powerful patterns for modern software systems, but they serve different purposes. EDA is best for decoupling services and orchestrating real-time workflows, while CDC is great for synchronizing data and enabling analytics. By understanding their strengths and limitations, you can choose the right approach---or even combine them---for your specific needs.

In short:

* Use **EDA** when you need to react to business events in real time.
* Use **CDC** when you need to track database changes and sync data across systems.
* Use both when you want the best of both worlds, like decoupled workflows and real-time analytics.

By carefully applying these patterns, you can build modern, scalable systems that are both efficient and maintainable.  

Stay tuned for the next part. We will show you practical examples.

##### Useful References:

* [Designing Data-Intensive Applications by Martin Kleppmann](https://dataintensive.net)
* [Why Microservices Should Be Event-Driven](https://blog.christianposta.com/microservices/why-microservices-should-be-event-driven-autonomy-vs-authority/)
* [The Many Meanings of Event-Driven Architecture by Martin Fowler - GOTO 2017](https://youtu.be/STKCRSUsyP0?si=Xylkzl0ySbaGZQ_e)
* [Why Change Data Capture?](https://www.confluent.io/learn/change-data-capture)
