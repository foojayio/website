---
title: "Fast JMS for Apache Pulsar: Blazing Performance"
slug: "fast-jms-for-apache-pulsar-modernize-and-reduce-costs-with-blazing-performance"
date: "2021-12-10T08:54:44+00:00"
lastmod: "2021-12-10T08:54:46+00:00"
description: "Learn today how you can modernize and reduce costs with blazing performance with fast JMS for Apache Pulsar."
canonical: "https://www.datastax.com/blog/fast-jms-apache-pulsar"
authors:
  - "chris-bartholomew"
image: "intro-fast-jms-for-apache-pulsar-thumbnail.png"
categories:
  - "Apache Pulsar"
  - "DataStax"
  - "Microservices"
tags:
related_posts:
  - "a-case-for-databases-on-kubernetes-from-a-former-skeptic"
  - "developing-an-enterprise-level-apache-cassandra-sink-connector-for-apache-pulsar"
  - "k8ssandra-production-ready-platform-for-running-apache-cassandra-on-kubernetes"
  - "why-developers-should-use-apache-pulsar"
frozen: false
---

DataStax recently announced the availability of [Fast JMS for Apache Pulsar](https://github.com/datastax/pulsar-jms), a JMS 2.0 API. By combining the industry-standard Java Messaging Service (JMS) API with the cloud-native and horizontally scalable Apache Pulsar™ streaming platform, DataStax is providing a powerful way to modernize your JMS infrastructure, improve performance, and reduce costs. Fast JMS is open source and is included in DataStax's Luna Streaming Enterprise support of Apache Pulsar.

What is JMS? {#h2-0-what-is-jms}
--------------------------------

[Java Message Service](https://en.wikipedia.org/wiki/Jakarta_Messaging) (JMS), or [Jakarta Messaging](https://jakarta.ee/specifications/messaging/) as it is now known, is a standard messaging API that is part of the Java Enterprise Edition (now [Jakarta EE](https://jakarta.ee/)) ecosystem. It defines how to create, send, receive, and read messages between loosely coupled producers and consumers.

JMS was first released in 1998 and has been widely adopted as the backbone for enterprise applications. There are two main versions of the standard, 1.1 and 2.0. The 2.0 version, which updates the API to make it easier to use, is backward compatible for applications written to the 1.1 standard.

Because JMS is an API standard that is not opinionated about the implementation, it has seen wide adoption by traditional message brokers such as RabbitMQ, Apache ActiveMQ, and IBM MQ. However, these traditional message brokers are difficult to operate at the scale demanded by modern enterprise applications.

Traditional brokers {#h2-1-traditional-brokers}
-----------------------------------------------

Traditional message brokers, or message queues, are monolithic applications that cannot easily scale horizontally as performance demands increase, and as you push traditional message brokers to their performance limits, recovery from operational issues becomes increasingly complex. Network partitions can be triggered by high CPU or even routine maintenance. Partitions can cause "split brain" scenarios, where queues have different versions of the same message. This requires either

[manual recovery by the operator or roll-the-dice automatic recovery by the broker](https://news.ycombinator.com/item?id=10827843).

Without horizontal scaling, running JMS using traditional brokers can lead to deployments of hundreds, if not thousands of individual message brokers that need to be individually monitored, maintained, and upgraded. This creates significant complexity, both directly in the management of the brokers themselves, but also indirectly, as the broker configuration leaks into the application tier, meaning that you may need to build sharding capabilities in the application layer to deal with distributing the data among a large number of clusters.

Why Kafka can't support JMS {#h2-2-why-kafka-can-t-support-jms}
---------------------------------------------------------------

Apache Kafka has solved the performance and scaling issues of traditional message brokers by moving to a distributed system design and using log-based architectures. It has also enabled powerful new features such as long-term message retention and playback, enabling you to travel back in time and replay previously consumed messages to recover from misconfiguration issues, ingest historical data to a new service, or to create a sample set of testing data that statistically matches real world scenarios.

The problem with Kafka is that, while it is optimized for performance and scalability, it was not designed to handle traditional message exchange patterns like load-balancing messages across many consumers. Kafka can't even retain messages until they are safely acknowledged by a consumer and instead deletes messages after a specific time period (for example, one week) whether they have been acknowledged or not. These limitations are due to foundational architectural decisions.

If you are trying to use JMS built on top of Kafka, expect to see a long list of exclusions from the JMS API specification. This breaks one of the fundamental promises of an API specification: the ability to change the implementation (in this case the JMS provider) without having to rewrite your application. So while you can take advantage of greater performance, scalability, and operational simplicity, you have to invest time and effort to rewrite existing applications to meet new restrictions and constrain the development of new JMS applications.

Apache Pulsar, the best of both worlds {#h2-3-apache-pulsar-the-best-of-both-worlds}
------------------------------------------------------------------------------------

Apache Pulsar, which was originally developed at Yahoo! and donated to the Apache Software Foundation in 2016, was designed to take advantage of modern systems design while at the same time supporting traditional message exchange patterns. Pulsar is a distributed system that uses a log-structured storage architecture and is [designed](https://yahooeng.tumblr.com/post/150078336821/open-sourcing-pulsar-pub-sub-messaging-at-scale) to provide high scalability and low latency.

What's really revolutionary about Pulsar is that it was explicitly designed to handle the requirements of both modern streaming and the traditional broker on a single platform. This means that Pulsar has several features that will sound familiar to users of traditional message brokers, such as tracking of unacknowledged messages, redelivery of messages, and individual message acknowledgements. Pulsar supports these features at the same time providing the high performance and scalability expected of a modern streaming platform.

On top of all that, because Pulsar emerged from an internet-scale enterprise, it also supports enterprise-grade features such as multi-tenancy and geo-replication. If you want to learn more about just how Pulsar's architecture enables it to deliver this unique best-of-both-worlds feature set, check out "[Four Reasons why Apache Pulsar is Essential to the Modern Data Stack](https://www.datastax.com/blog/2021/01/four-reasons-why-apache-pulsar-essential-modern-data-stack)" by Jonathan Ellis.

Fast JMS for Apache Pulsar {#h2-4-fast-jms-for-apache-pulsar}
-------------------------------------------------------------

Because of its unified streaming and queuing design and support for the modern enterprise, Pulsar is an ideal platform on which to build a modern, fast JMS implementation. Here's what you get with Fast JMS for Apache Pulsar:

* **Blazing performance** Achieve millions of JMS messages per second with 99.9 percentile publish-to-acknowledge latency of less than 10 ms.
* **Drop-in replacement JMS provider** Supports JMS/Jakarta 2.0 and is backwards compatible with JMS 1.1.
* **Horizontal scalability** You can scale up or down without operational hassles. Pulsar separates compute from storage, which means you can scale those dimensions independently, as required. Pulsar also supports offloading old messages to object storage for practically infinite storage capacity.
* **Consolidation** Because Apache Pulsar is natively multi-tenant and high performance, you can consolidate JMS applications spread across multiple legacy JMS brokers onto a single Pulsar installation. And because Pulsar is easily horizontally scaled, you don't need to overprovision.
* **Message replay** Pulsar natively supports message retention and replay. This enables applications to travel back in time and replay previously consumed messages to recover from misconfiguration issues, recover from bugs in application code, and test new applications against real data.
* **Geo-replication** Geo-replication is a first-class feature in Pulsar. You can easily replicate your messages to other locations for disaster recovery or global distribution.
* **Future readiness** By switching to Pulsar, not only can you support traditional messaging workloads, but you can also support streaming use cases such as log collection, microservices integration, event streaming, and event sourcing. These new workloads can run alongside legacy JMS applications with a single operational model.
* **Open source and cloud native** Both Apache Pulsar and our JMS API are 100% open source under the Apache license. No lock-in, and you can run it anywhere you want: on-premises, in the cloud, on Kubernetes, or on bare metal.

Comparing with ActiveMQ and Kafka {#h2-5-comparing-with-activemq-and-kafka}
---------------------------------------------------------------------------

Let's compare Fast JMS for Apache Pulsar with JMS using a traditional message broker, ActiveMQ, and Kafka. We wanted to stick with open source options in our comparison since Fast JMS is open source, but there is no similar open source functionality available for Kafka. Confluent offers a JMS implementation as part of their proprietary Confluent Platform, so we will use that for comparison purposes.

When considering a JMS solution, there are two dimensions to consider: how well it supports the JMS specification and the capabilities of the underlying platform. First we'll take a look at support JMS features.

|-------------------------|------------|----------------------|-------------------|
| JMS Feature             | ActiveMQ   | Confluent JMS        | DataStax Fast JMS |
| 1.1 Compatible          | Yes        | Yes                  | Yes               |
| 2.0 Compatible          | No         | No                   | Yes               |
| Queue Browsers          | Yes        | Yes                  | Yes               |
| Message Expiry          | Yes        | No                   | Yes               |
| Durable Subscribers     | Yes        | No                   | Yes               |
| Transactions            | Yes        | No                   | Yes               |
| Message Selectors       | Yes        | No                   | Yes               |
| Temporary Topics/Queues | Yes        | No                   | Yes               |
| License                 | Apache 2.0 | Confluent Enterprise | Apache 2.0        |

As you can see, Fast JMS for Apache Pulsar is able to support a complete set of JMS functionality, while Kafka is not. Fast JMS even has support for the JMS 2.0 standard, which is not supported in ActiveMQ.

Now, let's look at platform capabilities.

|---------------------------|------------|------------|------------|
| Feature                   | ActiveMQ   | Kafka      | Pulsar     |
| High Performance          | No         | Yes        | Yes        |
| Simple Horizontal Scaling | No         | Yes        | Yes        |
| High Availability         | Yes        | Yes        | Yes        |
| Message Replay            | No         | Yes        | Yes        |
| Native Geo-Replication    | No         | No         | Yes        |
| Tiered Storage            | No         | No         | Yes        |
| License                   | Apache 2.0 | Apache 2.0 | Apache 2.0 |

Both Kafka and Pulsar offer performance, scalability, and features like message replay that ActiveMQ cannot touch. But Pulsar comes out ahead of Kafka with native geo-replication and tiered storage.

Fast JMS for Pulsar has both more complete JMS capabilities and superior platform capabilities than the alternatives, making it the best solution for a modern JMS deployment. It's also completely open source under the Apache 2.0 license.

Getting started {#h2-6-getting-started}
---------------------------------------

We're very excited about the combined power of JMS and Pulsar. If you want to get started, take a look at the [docs](https://docs.datastax.com/en/fast-pulsar-jms/1.0/index.html) and the [GitHub repository](https://github.com/datastax/pulsar-jms). You will find examples to quickly try out the JMS API and you can even get instructions on how to run the TCK tests if you're interested in contributing improvements!

To use Fast JMS for Apache Pulsar, you will need a working Pulsar system. You can get started with Pulsar on the [Apache site](https://pulsar.apache.org/docs/en/standalone/) or with our [Luna Streaming Pulsar distribution](https://docs.datastax.com/en/luna/streaming/1.0/index.html), which is optimized for running in Kubernetes.

You can also [sign up](https://dtsx.io/3dDA5dX) for [Astra Streaming](https://www.datastax.com/products/astra-streaming), which is our enterprise-grade Pulsar cloud service currently in private beta.

If you want some help while getting started with Fast JMS and Pulsar, take a look at our [Luna Streaming Enterprise](https://www.datastax.com/products/luna-streaming) support. You can get expert help on all things Pulsar from developers of Fast JMS and Pulsar.

Wrapping up {#h2-7-wrapping-up}
-------------------------------

We think JMS and Apache Pulsar are a great pairing. Our Fast JMS API for Apache Pulsar brings the industry standard of JMS to a powerful and modern streaming platform. You can improve your legacy JMS infrastructure and get ready for new event-driven streaming applications, while at the same time reducing costs and simplifying operations.

And if you need help with Fast JMS or Pulsar, DataStax's world-class support and engineering teams are here to partner with you.
