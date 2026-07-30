---
title: "Why Developers Should Use Apache Pulsar | Foojay.io Today"
slug: "why-developers-should-use-apache-pulsar"
date: "2022-05-19T15:10:49+00:00"
lastmod: "2022-05-19T15:11:52+00:00"
description: "Apache Pulsar is an open source streaming platform that addresses important limitations in Kafka, particularly for cloud-native applications."
canonical: "https://www.infoworld.com/article/3619272/why-developers-should-use-apache-pulsar.html"
authors:
  - "patrick-mcfadin"
image: "/images/posts/2022/05/why-developers-should-use-apache-pulsar/data_explosion_proliferation_transmission_data_streams_volume_velocity_by_spainter_vfx_gettyimages-896319676_cso_2400x1600-100852889-large.jpg"
categories:
  - "Apache Cassandra"
  - "Apache Pulsar"
  - "Databases"
  - "Microservices"
tags:
related_posts:
  - "7-reasons-to-choose-apache-pulsar-over-apache-kafka"
  - "5-more-reasons-to-choose-apache-pulsar-over-apache-kafka"
  - "bring-streaming-to-apache-cassandra-with-apache-pulsar"
  - "the-critical-role-streaming-plays-in-a-data-stack"
frozen: false
---

Apache Pulsar is an open source streaming platform that addresses some important limitations in Kafka, particularly for cloud-native applications.  
![](/images/posts/2022/05/why-developers-should-use-apache-pulsar/data_explosion_proliferation_transmission_data_streams_volume_velocity_by_spainter_vfx_gettyimages-896319676_cso_2400x1600-100852889-large-1024x683.jpg) SPainter VFX / Getty Images   

If you are building applications today, you are probably familiar with the microservices model: Rather than building big monolithic applications, we break services down into isolated components that we can independently update or change over time.

Microservices deployments then can use a message bus to decouple and manage the communication between services, which makes it easier to replay requests, handle errors, and deal with load spikes and rapid increases in requests while maintaining the serialized order.

The result should be a more scalable and elastic application or service based on demand, as well as better availability and performance. If you are seeing the message bus show up more in application architectures, you aren't imagining things. According to IDC, the total market size for cloud event stream processing software in 2024, which covers all of these use cases, is forecast to be $8.5 billion.

Streaming enables some of the most impressive user experiences that you can get in your applications like real-time order tracking, user notifications, and recommendations. For developers, making this work in practice involves looking at streaming and messaging systems that will pass requests between the microservices components. These connections link all the components together so that they can carry out processing and provide the result back to the customer.

If you are building at any scale or for maximum uptime, you will have to think about geographic distribution for your data. When you have customers around the world, your application will process transactions and create data around the world too. Databases like Apache Cassandra are popular where you have to have full multicloud support, scalability, and independence for that application data over time.

These considerations should also apply to your approach to streaming. When your application components have to work across multiple locations or services and scale locally or geographically, then your streaming implementation and message bus will have to support that same distributed model too.

**Why Apache Pulsar?** {#h2-0-why-apache-pulsar}
------------------------------------------------

The most common approach to application streaming is to use Apache Kafka. However, there are some important limitations that are now even more important in cloud-native applications. Apache Pulsar is an open source streaming project that was built at Yahoo as a streaming platform to solve for some of the limitations in Kafka. There are four areas where Pulsar is particularly strong: geo-replication, scaling, multitenancy, and queuing.

To start with, it's important to understand how the different streaming and messaging services work and how their design decisions around organizing messages can affect the implementation. Understanding these design decisions can help in determining the right fit for your requirements.

For application streaming projects, one thing these services share is how data is stored on disk --- in what's called a segment file. This file contains the detailed data on individual events, and is eventually used to create a message that is then streamed out to consumers.

The individual segment files are bundled into a larger group in what is called a partition. Each partition is owned by a single lead broker, which replicates that partition to several followers. These are the basic steps on what needs to be done for reliable message passing.

In Apache Kafka, adding a new node requires preparation with some partitions copied to the new node before it begins participating in cluster operations and reducing the load on the other nodes. In practice, this means that adding capacity to an existing Kafka cluster can make it slower before it makes it faster.

For organizations with predictable message volumes and good capacity planning, this is something that can be planned around effectively. However, if your streaming message volumes grow faster than you expected, then it could be a serious capacity planning headache.

Apache Pulsar takes a different approach to this problem by adding a layer of abstraction to prevent scaling problems. In Pulsar, partitions are split up into what are called ledgers, but unlike Kafka segments, ledgers can be replicated independently of one another and the broker. Pulsar keeps a map of which ledgers belong to a partition in Apache ZooKeeper, which is a centralized service for maintaining configuration information, providing distributed synchronization, and providing group services.

Using ZooKeeper, Pulsar can keep up-to-date on the information that is being created. Therefore, when we have to add a new storage node and expand the cluster, all we have to do is create a new ledger on the new node. This means that all the existing data can stay where it is while the new node gets added to the cluster, and no extra work is required for the resources to be available and to help the service scale.

Just like Cassandra, Pulsar includes support for data center aware geo-replication of data from the start. Producers can write to a shared topic from any region, and Pulsar takes care of ensuring that those messages are visible to consumers everywhere. Pulsar also separates the compute and storage elements, which are managed by the broker and Apache BookKeeper. BookKeeper is a project for building services requiring low latency, fault tolerant, and scalable storage. The individual storage servers, called bookies, provide the distributed storage required by Pulsar segments.

This architecture allows for multitenant infrastructure that can be shared across multiple users and organizations while isolating them from each other. The activities of one tenant should not be able to affect the security or the SLAs of other tenants. Like geo-replication, multitenancy is hard to graft on to a system that wasn't designed for it.

**Why is streaming good for developers?** {#h2-1-why-is-streaming-good-for-developers}
--------------------------------------------------------------------------------------

Application developers can use streaming to share messages out to different components based on what's called a publish/subscribe pattern, or pub/sub for short. Applications that create data, called publishers, send messages to the message bus, which manages them in strict serial order and sends them out to applications that subscribe to them. The publishers and subscribers are not aware of each other, and the list of subscribers for any messages can evolve and grow over time.

For streaming, it can be critical to consume messages in the same serialized order in which they were published. When those requirements are not as important, it's possible for Pulsar to use a queuing model where processing order is not important compared to managing activity. This means that Pulsar can be used to replace Advanced Message Queuing Protocol (AMQP) implementations that might use RabbitMQ or other message queuing systems.

**Getting started with Apache Pulsar** {#h2-2-getting-started-with-apache-pulsar}
---------------------------------------------------------------------------------

For those who want a more hands-on approach to Pulsar, you can create your own cluster. This will involve creating a set of machines that will host your Pulsar brokers and BookKeeper, and a set of machines that will run ZooKeeper. The Pulsar brokers manage the messages that are coming in and pushed out to subscribers, the BookKeeper installation provides storage for all persistent data created, and ZooKeeper is used to keep everything coordinated and consistent over time.

First, start by installing the Pulsar binaries to each server and adding connectors to these based on the other services that you are running. This should then be followed by deploying the ZooKeeper cluster, then initializing the cluster's metadata. This metadata will include the name of the cluster, the connection string, the configuration store connection, and the web service URL. If you will use encryption to keep your data secure in transit, then you will also have to provide the TLS web service URL too.

Once you have initialized the cluster, then you will have to deploy your BookKeeper cluster. This collection of machines will provide your persistent storage. Once you have started the BookKeeper cluster, then you can start up a bookie on each of your BookKeeper hosts. After this, you can deploy your Pulsar brokers. These handle the individual messages that are created and sent through your implementation.

If you are using Kubernetes and containers already, then deploying Pulsar is easier still. To start with, you will have to prepare your cloud provider storage settings by creating a YAML file with the right information to create persistent volumes; each cloud provider will require its own set up steps and details. Once cloud storage configuration is completed, you can use Helm to deploy your Pulsar cluster and associated ZooKeeper and BookKeeper machines into a Kubernetes cluster. This is an automated process that can make deploying Pulsar easier and reproducible.

**Streaming data everywhere** {#h2-3-streaming-data-everywhere}
---------------------------------------------------------------

Looking ahead, application developers will have to think more about the data that their applications create and how this data is used for real-time activities based on streaming. Because streaming features often serve users and systems that are geographically dispersed, it's critical that streaming capabilities provide performance, replication, and resiliency across multiple locations or cloud platforms.

Streaming supports some of the business initiatives that we are told will be most valuable in the future, such as real-time analytics or data science and machine learning initiatives. To make this work at scale, looking at distributed streaming with Apache Pulsar as part of your overall approach is therefore a good idea as you expand what you want to achieve around data.

*Patrick McFadin is the VP of developer relations at* [*DataStax*](https://www.datastax.com/)*, where he leads a team devoted to making users of Apache Cassandra successful. He has also worked as chief evangelist for Apache Cassandra and consultant for DataStax, where he helped build some of the largest and exciting deployments in production. Previous to DataStax, he was chief architect at Hobsons and an Oracle DBA/developer for over 15 years.*

***New Tech Forum provides a venue to explore and discuss emerging enterprise technology in unprecedented depth and breadth. The selection is subjective, based on our pick of the technologies we believe to be important and of greatest interest to InfoWorld readers. InfoWorld does not accept marketing collateral for publication and reserves the right to edit all contributed content.***
