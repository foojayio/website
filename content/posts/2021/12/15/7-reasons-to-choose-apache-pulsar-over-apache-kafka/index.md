---
title: "7 Reasons to Choose Apache Pulsar over Apache Kafka"
date: "2021-12-15T10:03:13+00:00"
lastmod: "2022-07-05T20:57:34+00:00"
description: "When you set out to build the best messaging infrastructure service, the first step is to pick the right underlying messaging technology!"
canonical: "https://datastax.medium.com/7-reasons-to-choose-apache-pulsar-over-apache-kafka-cb111087eadb"
authors:
  - "chris-bartholomew"
image: "1_Fp4f1tFBgsfvxyfXBGEpDQ.jpeg"
categories:
  - "Apache Cassandra"
  - "Apache Pulsar"
  - "Databases"
  - "DataStax"
  - "Microservices"
tags:
related_posts:
  - "fast-jms-for-apache-pulsar-modernize-and-reduce-costs-with-blazing-performance"
  - "a-case-for-databases-on-kubernetes-from-a-former-skeptic"
  - "developing-an-enterprise-level-apache-cassandra-sink-connector-for-apache-pulsar"
  - "why-developers-should-use-apache-pulsar"
frozen: false
---

***I wrote an earlier version of this article in 2019, while I was CEO of Kesque, a real-time messaging service built on*** [***Apache Pulsar***](https://pulsar.apache.org/)***, the cloud-native distributed messaging and streaming platform. A lot of big changes have happened in the interim; perhaps the most significant of these is the fact that the company I founded in early 2019 was*** [***acquired***](https://www.datastax.com/press-release/datastax-delivers-scale-out-enterprise-event-streaming-modern-data-apps)***, in January, by DataStax. One thing that hasn't changed, however, is the rationale behind our choice of Apache Pulsar.***

At Kesque, it was our mission to empower developers to build cloud-native distributed applications by making cloud-agnostic, high-performance messaging technology easily available to everyone. Developers want to write distributed applications or microservice but don't want the hassle of managing complex message infrastructure or getting locked into a particular cloud vendor. They need a solution that just works. Everywhere.

When you set out to build the best messaging infrastructure service, the first step is to pick the right underlying messaging technology. There are lots of choices out there, from various open-source projects like RabbitMQ, ActiveMQ, and NATS to proprietary solutions such as IBM MQ or Red Hat AMQ. And, of course, there is Apache Kafka, which is almost synonymous with streaming. But we didn't go with Apache Kafka, we went with Apache Pulsar.

So why did we build our messaging service using [Apache Pulsar](https://www.datastax.com/blog/2020/06/what-apache-pulsar)? Here are the top seven reasons why we chose Apache Pulsar over Apache Kafka.

Apache Pulsar is like two products in one. Not only can it handle high-rate, real-time use cases like Kafka, but it also [supports standard message queuing](https://pulsar.apache.org/docs/en/concepts-messaging/#subscription-modes) patterns, such as competing consumers, fail-over subscriptions, and easy message fan out. Apache Pulsar automatically keeps track of the client read position in the topic and stores that information in its high-performance distributed ledger, Apache BookKeeper.

Unlike Kafka, Apache Pulsar can handle many of the use cases of a traditional queuing system, like RabbitMQ. So instead of running two systems --- one for real-time streaming and one for queuing --- you do both with Pulsar. It's a two-for-one deal, and those are always good.

If you use Kafka, you know about partitions. All topics are partitioned in Kafka. Partitioning is important because it increases throughput. By spreading the work across partitions and therefore multiple brokers, the rate that can be processed by a single topic goes up. But what if you have some topics that don't need high rates. In these simple cases, wouldn't it be nice to not have to worry about partitions and the API and management complexity that comes along with them?

Well, with Apache Pulsar it can be that simple. If you just need a topic, then use a [topic](https://pulsar.apache.org/docs/en/concepts-messaging/#topics). You don't have to specify the number of partitions or think about how many consumers the topic might have. Pulsar [subscriptions](https://pulsar.apache.org/docs/en/concepts-messaging/#subscription-modes) allow you to add as many consumers as you want on a topic with Pulsar keeping track of it all. If your consuming application can't keep up, you just use a shared subscription to distribute the load between multiple consumers.

And if you really do need the performance of a partitioned topic, you can do that, too. Pulsar has [partitioned topics](https://pulsar.apache.org/docs/en/concepts-messaging/#partitioned-topics) if you need them --- but only if you need them.

The Kafka team deserves credit for the [insight](https://engineering.linkedin.com/distributed-systems/log-what-every-software-engineer-should-know-about-real-time-datas-unifying) that a log is a great abstraction for a real-time data exchange system. Because logs are append-only, data can be written to them quickly, and because the data in a log is sequential, it can be extracted quickly in the order that it was written. Sequential reading and writing is fast, random is not. Persistent storage interactions are a bottleneck in any system that offers data guarantees, and the log abstraction makes this about as efficient as possible.

Simple logs are great. But they can get you into trouble when they get large. Fitting a log on a single server becomes a challenge. What happens if it gets full and you need to scale out? And what happens if the server storing the log fails and needs to be recreated from a replica?

Copying a large log from one server to another, while efficient, can still take a long time. If your system is trying to do this while keeping up with real-time data, this can be quite a challenge. Check out "Adding a New Broker Results in Terrible Performance" in the blog post [Stories from the Front: Lessons Learned from Supporting Apache Kafka](https://www.confluent.io/blog/stories-front-lessons-learned-supporting-apache-kafka/) for some color on this.

Apache Pulsar avoids the problem of copying large logs by breaking the log into segments. It distributes those segments across multiple servers while the data is being written by using Apache BookKeeper as its [storage layer](https://pulsar.apache.org/docs/en/concepts-architecture-overview/#apache-bookkeeper). This means that the log is never stored on a single server, so a single server is never a bottleneck. Failure scenarios are easier to deal with and scaling out is a snap. Just add another server. No rebalancing needed.

Stateless is music to the ears of anyone building cloud-native applications. Stateless components start up quickly, are interchangeable, and scale seamlessly. Wouldn't it be great if a message broker was stateless?

The Kafka broker is not stateless. Each broker contains the complete log for each of its partitions. If one broker fails, not just any broker can take over for it. If the load is getting too high, you can't simply add another broker. Brokers must synchronize state from other brokers that contain replicas of its partitions.

In the Apache Pulsar architecture, the [brokers are stateless](https://pulsar.apache.org/docs/en/concepts-architecture-overview/#brokers). Yes, you heard that right. A completely stateless system wouldn't be able to persist messages, so Apache Pulsar does maintain state, just not in the brokers. In Pulsar architecture, the brokering of data is separated from the storing of data. The brokers accept data from producers and send data to consumers, but the data is stored in Apache BookKeeper.

Because Pulsar brokers are stateless, if the load gets high, you just need to add another broker. The broker starts up quickly and gets to work right away.

Geo-replication is a first-class feature in Pulsar. It's not a bolt-on or a proprietary add-on. Pulsar was [designed with geo-replication](https://pulsar.apache.org/docs/en/administration-geo/) in mind. Configuring it is easy and it just works. So whether it's a globally distributed application or disaster recovery scenario, you can set it up with Pulsar. No Ph.D. needed.

[Benchmark tests](http://openmessaging.cloud/docs/benchmarks/pulsar/) have shown that Pulsar delivers higher throughput along with lower and more consistent latency. Faster and more consistent is better. What else is there to say?

Pulsar has many of the same features as Kafka --- such as geo-replication, in-stream message processing (Pulsar Functions), input and output connectors (Pulsar IO), SQL-based topic queries (Pulsar SQL), schema registry, as well as features Kafka doesn't have like tiered storage and multi-tenancy.

All these features are part of the [Apache open source project](https://pulsar.apache.org/en/).

Pulsar is not a collection of open-source and closed-source features or open-source features controlled by a commercial entity. All its many useful features are open-source under the Apache umbrella. And unless the unthinkable happens, all this goodness will stay open source.

As you can see, we had lots of reasons to pick Apache Pulsar for building our messaging infrastructure service. And we didn't even go into the reasons it's easier to build a service around Pulsar, such as multi-tenancy, namespaces, authentication and authorization, documentation, Kubernetes friendliness.

If you're looking to build out a messaging infrastructure service, give our managed Pulsar service, [Astra Streaming](https://astra.dev/3yn6PlD), a try. You won't regret it.
