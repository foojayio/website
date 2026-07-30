---
title: "A Modern Approach to Middleware with Chronicle"
slug: "a-modern-approach-to-middleware-with-chronicle"
date: "2024-05-17T09:46:40+00:00"
lastmod: "2024-05-17T09:46:41+00:00"
description: "Financial institutions today face significant challenges in updating their legacy middleware systems which are crucial for supporting millions of lines of code serving critical business functions."
authors:
  - "ryan-andrews"
image: "/images/posts/2024/05/a-modern-approach-to-middleware-with-chronicle/Screenshot-2024-05-07-at-09.59.51-700x487-1.png"
categories:
  - "Chronicle Software"
  - "Java"
  - "Microservices"
tags:
related_posts:
  - "achieving-high-throughput-without-sacrificing-latency"
  - "automatically-creating-microservices-architecture-diagrams"
  - "billions-of-messages-tcp-ip"
frozen: false
---

**Financial institutions today face significant challenges in updating their legacy middleware systems which are crucial for supporting millions of lines of code serving critical business functions. Prior to multicast support in modern switching hardware that became prevalent in the early 2000s, message middleware was largely done via proprietary protocols that converged onto TCP/IP. IBM's Websphere MQ was a leader in this space.**

Point to point middleware based on TCP/IP requires extra processing power and network bandwidth proportional to the number of consumers, and unreliable or slow consumers can negatively impact performance of the publisher. To combat these challenges, software vendors utilized IP multicast to create messaging platforms that supported topic based publish/subscribe networks.

Early multicast solutions such as Tibco Rendezvous used message brokers to facilitate communication and ensure reliability. In response, companies such as 29 West developed brokerless systems that reduced latency. These systems often bridged TCP/IP connections over WANs that did not allow multicast.

### Challenges and limitations {#h3-0-challenges-and-limitations}

As systems evolved and data volumes grew, some challenges and limitations of these multicast based systems presented themselves.

* **Topic to channel management.** Balancing message topics across channels requires advanced monitoring and manual intervention to adjust
* **Consumer side filtering.** Subscribers receive all data on a channel, wasting processing time to discard unwanted traffic from topics sharing the channel
* **Publisher flow control.** Publishers can unintentionally send data at a rate exceeding subscriber processing capabilities.
* **Data loss.** IP Multicast is inherently unreliable and messages are not guaranteed to be delivered or ordered
* **Late joining and recovery.** New subscribers can't easily catch up on ongoing multicast streams, and those falling behind often never catch up.

Addressing these limitations led to the introduction of additional features aimed at improving reliability such as replay stores, ack/nack messaging, and redundant multicast publishers to manage the inherent unreliability of IP multicast. However, these features often introduced accidental complexity, negatively impacting overall performance.

In response to these challenges, many firms are now considering cloud solutions where traditional enterprise multicast is less viable. Some have turned to web services with JSON payloads over RESTful interfaces, typical in cloud-based microservices, although these are generally slower and require more resources than their predecessors.

Others look to open source messaging solutions such as Kafka. However, Kafka has limitations in throughput and latency that require a complex design allowing massive parallelism of work in order to handle high volumes of data. This makes it impractical for use in most low latency trading environments, see more in [this article](http://https://chronicle.software/benchmarking-kafka-vs-chronicle-for-microservices-which-is-750-times-faster/ "this article").

For high frequency or ultra low latency trading groups needing the absolute lowest latency, it has become popular to buy the highest horsepower machines possible and pack as much infrastructure on them as possible, leveraging shared memory transport between services. These solutions are typically expensive to build, difficult to maintain, and not cloud friendly.

### Chronicle's approach to microservices {#h3-1-chronicle-s-approach-to-microservices}

[Chronicle's approach to microservices](http://https://chronicle.software/services/ "Chronicle’s approach to microservices ")with queue replication can provide both on box latencies rivaling some of the best shared-memory implementations on the market, as well as low and predictable latency off box. [Chronicle Queue](http://https://chronicle.software/queue-enterprise/ "Chronicle Queue") (the underlying messaging system of the microservices framework) solves many of the issues that plague legacy middleware by not sacrificing flexibility, allowing both point-to-point and topic-based pub/sub communication patterns.

Slow consumers combined with ultra-fast producers do not cause back pressure-induced pauses, and messages are reliably delivered in order with no data loss. State is easily recovered following an outage or failover using the persistence features of Chronicle Queue, and by leveraging Chronicle Services, this can be done seamlessly.

On top of this, queues are cloud-friendly with TCP/IP replication to remote backup systems; they can scale to dozens of consumers without burdening a producer. Queues also provide corollary functions such as event recording/playback, auditing and logging without impacting system performance.

The below diagram is an example trading system using Chronicle Services showing chronicle queues as the "bus" in a pub/sub environment.  
![](/images/posts/2024/05/a-modern-approach-to-middleware-with-chronicle/Screenshot-2024-05-07-at-09.59.51-700x487.png)

[Contact Chronicle](http://https://chronicle.software/contact-us/ "Contact Chronicle") today for more information, as well as for access to our [documentation](http://https://portal.chronicle.software/resources.php?_gl=1*15yr5dm*_ga*MTk1MTk4MjgzNS4xNzAxMjYzOTE1*_ga_F8RYJ50Q4J*MTcxNTA3MjI4NC4xMjUuMS4xNzE1MDcyNTM5LjAuMC4w*_ga_5XEVBSKC5K*MTcxNTA3MjI4NC4xMjUuMS4xNzE1MDcyNTM5LjAuMC4w&amp;_ga=2.240404995.1202346314.1715072285-1951982835.1701263915#docs "documentation") on Chronicle Services and Queue Enterprise.
