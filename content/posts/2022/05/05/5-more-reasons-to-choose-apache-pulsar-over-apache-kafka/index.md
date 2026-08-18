---
title: "5 More Reasons to Choose Apache Pulsar Over Apache Kafka"
slug: "5-more-reasons-to-choose-apache-pulsar-over-apache-kafka"
date: "2022-05-05T18:11:20+00:00"
lastmod: "2022-05-05T18:11:21+00:00"
description: "Author’s note: I originally published this blog post in 2019, while I was CEO of Kesque, a real-time messaging service built on Apache Pulsar, the - by Chris Bartholomew"
canonical: "https://datastax.medium.com/5-more-reasons-to-choose-apache-pulsar-over-apache-kafka-c09b259e3691"
authors:
  - "chris-bartholomew"
image: "1_Fp4f1tFBgsfvxyfXBGEpDQ.jpeg"
categories:
  - "Apache Pulsar"
  - "DataStax"
  - "Microservices"
tags:
related_posts:
  - "why-developers-should-use-apache-pulsar"
  - "bring-streaming-to-apache-cassandra-with-apache-pulsar"
  - "full-stream-ahead-astra-streaming-powered-by-apache-pulsar"
  - "why-pulsar-beats-kafka-for-a-scalable-distributed-data-architecture"
frozen: false
---

*Author's note: I originally published this blog post in 2019, while I was CEO of Kesque, a real-time messaging service built on* [*Apache Pulsar*](https://pulsar.apache.org/)*, the cloud-native distributed messaging and streaming platform. It's a follow-up to an earlier post, "* [*7 Reasons to Choose Apache Pulsar over Apache Kafka*](https://datastax.medium.com/7-reasons-to-choose-apache-pulsar-over-apache-kafka-cb111087eadb)*." A lot of big changes have happened since these two posts went live, including Kesque's* [*acquisition*](https://www.datastax.com/press-release/datastax-delivers-scale-out-enterprise-event-streaming-modern-data-apps)*, in January 2021, by DataStax. The reasons to choose Pulsar, however, haven't changed.*

A while back, I wrote a post about the [**7 Reasons We Choose Apache Pulsar over Apache Kafka**](https://kafkaesque.io/7-reasons-we-choose-apache-pulsar-over-apache-kafka/). Since then, I have been working on a detailed report comparing Kafka and [**Pulsar**](https://kesque.com/what-is-apache-pulsar/), talking to users of the open-source Pulsar project, and talking to users of our managed Pulsar service, Kesque. What I've realized is that I missed some reasons in that first post. So, I thought I'd do a follow-up post that adds to the list.

Before diving into the new reasons, let's quickly recap the seven mentioned in the previous post:

* **Streaming and queuing together**- Kafka and RabbitMQ in a single platform. It's a two-for-one deal.
* **Partitions are optional** - With Pulsar you don't need to mess around with partitions if you don't want to. (And I don't want to.)
* **Distributed log** - The Pulsar log is horizontally scalable because it is distributed. Do I hear music in my ears?
* **Stateless brokers** - A cloud-native dream scenario. Where did I put my auto-scaler?
* **Native geo-replication** - Anybody and I mean anybody, can get geo-replication working.
* **It's faster** - Tests prove this.
* **All Apace Software Foundation open source** - Nobody is going to pull the licensing rug out from under you.

Those are the first seven reasons. These seem like plenty, but I have found some more. So let's get into them.

1. Getting along with multi-tenancy
-----------------------------------

I really should have talked about multi-tenancy in the first post because it's a big deal. Even if you aren't planning on building a managed Pulsar service (and why would you, since we've already built **one**for you?), unless you are a hermit, there are going to be multiple teams working on multiple projects using your messaging infrastructure. Having to spin up a cluster for each team or project is a pain. And it's also expensive.

With Pulsar, you can have [**multiple tenants**](https://pulsar.apache.org/docs/en/concepts-multi-tenancy/) and those tenants can have multiple namespaces to keep things all organized. Add to that access controls, quotas, and rate-limiting for each namespace and you can imagine a future where we can all get along using just this one cluster. Not only can we imagine this future, but Kafka can imagine it, too. You can read about it in Kafka Improvement Proposal (KIP) [**KIP-37**](https://cwiki.apache.org/confluence/display/KAFKA/KIP-37+-+Add+Namespaces+to+Kafka). It's been under discussion for a while now.

2. Have we got a quorum yet? Replication
----------------------------------------

We're getting into the weeds here, but bear with me. You want to make sure your messages never get lost, so you configure your messaging system to make two or three replicas of each message in case something goes wrong.

Kafka does this using a follow-the-leader model. The leader stores the message and the followers make a copy of it. Once enough followers acknowledge they've got it, Kafka is happy. Pulsar uses a [**quorum model**](https://jack-vanlightly.com/blog/2018/10/2/understanding-how-apache-pulsar-works). It sends the message out to a bunch of nodes, and once enough of them acknowledge they've got it, Pulsar is happy.

Quorum replication is more democratic with none of this leader-follower hierarchy. The majority always wins, and all votes are equal. But that doesn't matter with technology. What does matter is that quorum replication tends to give more consistent behavior over time. This probably explains why Pulsar gives more consistent latency performance.

If you want to get into the gory details of Kafka and Pulsar latency, check out this [**blog post**](https://kafkaesque.io/performance-comparison-between-apache-pulsar-and-kafka-latency/) I wrote. (It's long. Don't say I didn't warn you.) Oh, and Kafka has been thinking about quorum replication for improving latency consistency, too. Check out [**KIP-250**](https://cwiki.apache.org/confluence/display/KAFKA/KIP-250+Add+Support+for+Quorum-based+Producer+Acknowledgment) for the discussion.

3. Tiered storage, event sourcing dreaming
------------------------------------------

One of the great things about a streaming system like Kafka is its ability to replay messages that have already been consumed. If you like those messages the first time around, replaying them to correct something or build a new application around them is fun to do.

What if you like those messages so much, you want to keep them around forever? Like, say if you are doing [**event-sourcing**](https://martinfowler.com/eaaDev/EventSourcing.html). It sounds like a great idea, but forever is a [**mighty long time**](https://www.youtube.com/watch?v=aXJhDltzYVQ) and storing messages forever can get expensive --- especially if you are storing them on those high-performance SSDs that keep your messaging system humming.

Wouldn't it make sense if you could move those old messages --- the ones you need to keep around because you might need them someday --- to a cheaper storage solution? And if you could use dirt cheap cloud storage like Amazon S3 buckets, wouldn't that be great?

You can probably guess where I am going here. With Pulsar [**tiered storage**](https://pulsar.apache.org/docs/en/concepts-tiered-storage/), you can automatically push those dusty old messages into practically infinite, cheap cloud storage and retrieve them just like you do those newer, fresh-as-a-daisy messages.

I bet Kafka would like to have that feature. You guessed it, they would. It's described in [**KIP-405**](https://cwiki.apache.org/confluence/display/KAFKA/KIP-405%3A+Kafka+Tiered+Storage).

4. End-to-end encryption and gobbledygook
-----------------------------------------

Obviously, security is important and you want to keep your messages safe from prying eyes. Of course, you will use TLS between your client and the messaging system (encrypted in transit).

When you do that, the messaging system has to decrypt the connection so it can figure out what the client is trying to say. It is then going to save the unencrypted message on disk. Of course, you will insist that the disk is encrypted so that if someone stole the disk your messages would be safe (encrypted at rest). But in both these cases, the messaging system has the keys to your data. If it didn't, it would be dealing with unintelligible streams of gobbledygook.

In many cases, this level of encryption is good enough. But if you want to make absolutely sure nobody can peek at your messages, you need end-to-end encryption. The producer encrypts the message before it sends it using keys that are shared with the consumer that will receive the message. When the message gets saved on the disk of the messaging system, it's encrypted and the messaging system doesn't have the key. The messaging system can do its job. But your message is super-secure gobbledygook to the messaging system.

Pulsar can do [**end-to-end encryption**](https://pulsar.apache.org/docs/en/security-encryption/) in its Java client. Kafka has been talking about doing it in [**KIP-317**](https://cwiki.apache.org/confluence/display/KAFKA/KIP-317%3A+Add+end-to-end+data+encryption+functionality+to+Apache+Kafka).

5. Broker balancing act
-----------------------

In my last post, I talked about Pulsar brokers being stateless, which is great. But there is actually more to the story.

Stateless components are desirable because when one gets overloaded, you can just add another one to handle the load. When new clients connect, they can be directed to the new instance. But that doesn't help the instance that was getting overloaded in the first place. You need to shift some of the work from the overloaded instance to the new, fresh one.

In other words, you need to rebalance the load.

Pulsar does [**broker load balancing**](https://pulsar.apache.org/docs/en/administration-load-balance/) automatically for you. It monitors the CPU, memory, and network (not disk; did I mention brokers are stateless?) usage of brokers and will move the load around to maintain balance. This means that you don't have to add that new broker until you use up the capacity of all the brokers --- not because one of them is running hot.

You can do broker load balancing with Kafka. But, you are going to have to install another package such as LinkedIn's [**Cruise Control**](https://github.com/linkedin/cruise-control). Or, if you like (eventually) paying for stuff, you can use Confluent's [**rebalancer**](https://docs.confluent.io/current/kafka/rebalancer/rebalancerhttps://www.confluent.io/product/auto-data-balancing/html) tool as well.

Community and ecosystem
-----------------------

One of the criticisms of my last post was that I didn't mention the size and richness of Kafka's community and ecosystem. That's a fair point.

In the community and ecosystem category, Kafka has Pulsar beat. Kafka has a five-year head start as an open-source project, so it only stands to reason that it will have a larger community, more related projects, and more answers on Stack Overflow.

All I can say is that the Pulsar community is growing, people are contributing new components and integrations regularly, and the folks on the community Slack channel are friendly and supportive.

Actually, there is one more thing I can say: It's clear that a lot of Pulsar was inspired and informed by Kafka and that Pulsar is standing on the shoulders of a giant. The Kafka project and community deserve a lot of credit and respect. I know that it may sometimes sound like I am disrespecting Kafka, but I'm really just excited about Pulsar.

Legit Kafka alternative
-----------------------

Between this post and the last one, I am up to a dozen reasons to choose Pulsar over Kafka. And the cool thing is that the deeper I dive into Pulsar, the more reasons I find. So, there might need to be a third blog post on this topic in the future. Stay tuned.

I think it's pretty clear that Pulsar is a legit alternative to Kafka. Pulsar supports most of the same functionality as Kafka but has several (a dozen by my count) advantages and is gaining momentum as more people learn about it.

If you are evaluating streaming and/or queuing systems, you owe it to yourself to check out [**Pulsar**](https://pulsar.apache.org/). It's that simple.

***Want to try out Pulsar?*** [*Sign up now*](https://dtsx.io/2ZZLdhX)*for* [*Astra Streaming*](https://www.datastax.com/products/astra-streaming)*, our fully managed Apache Pulsar service. We'll give you access to its full capabilities entirely free through beta. See for yourself how easy it is to build modern data applications and let us know what you'd like to see to make your experience even better.*
