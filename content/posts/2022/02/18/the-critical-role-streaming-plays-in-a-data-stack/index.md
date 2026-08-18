---
title: "The Critical Role Streaming Plays in a Data Stack"
slug: "the-critical-role-streaming-plays-in-a-data-stack"
date: "2022-02-18T17:20:13+00:00"
lastmod: "2022-02-18T17:20:15+00:00"
description: "Why Apache Pulsar and its support for streaming data is the right choice for multi-datacenter, geo-distributed deployments."
canonical: "https://www.rtinsights.com/the-critical-role-streaming-plays-in-a-data-stack/"
authors:
  - "jbellis"
image: "streaming-data-Depositphotos_5_tn-300x210-1.jpg"
categories:
  - "Apache Cassandra"
  - "Apache Pulsar"
  - "Databases"
  - "Microservices"
tags:
related_posts:
  - "5-more-reasons-to-choose-apache-pulsar-over-apache-kafka"
  - "7-reasons-to-choose-apache-pulsar-over-apache-kafka"
  - "a-case-for-databases-on-kubernetes-from-a-former-skeptic"
  - "why-developers-should-use-apache-pulsar"
frozen: false
---

![](streaming-data-Depositphotos_5_tn-300x210-1.jpg)

Why Apache Pulsar is the right choice for multi-datacenter, geo-distributed deployments.

Building applications today requires companies and teams to be familiar with data flows (including streaming data flows) and how to transform them into compelling user experiences. Data fuels companies' ability to understand their customers, move faster than competitors, and provide compelling products that the market wants. According to [research](https://www.datastax.com/resources/report/the-state-of-the-data-race-2021) by DataStax and Clearpath Strategies, almost all companies (96%) now have a formal strategy for how they will work with data, but only 38% think they're doing a good job using data in their applications.

For developers, building these data applications involves putting together the right stack to manage and process this data over time for the business. Companies leading the market around using data attribute more than 20 percent of their revenue to data and analytics, and companies using open source data stacks are two times more likely to meet this definition, according to the DataStax/Clearpath research.

There is, therefore, a significant gap between how companies say they want to use data and their ability to turn that high-level goal into an ongoing process that can scale up and provide sustainable revenues and advantages. This involves more than implementing successful individual projects; it requires thinking about a holistic stack approach across all the elements around data and across all an organization's lines of business.

### **Application streaming and hybrid approaches**

Application event streaming is a key component to standardizing best practices across teams and organizations and unlocking the value of the data they create and process. Application event streaming uses a message bus that takes events -- a customer action, a sensor alert, a transaction taking place -- enriches it, and combines it with other data sources (to check for fraudulent transactions or abnormal sensor readings), and then sends that data to other services. This includes triggering a real-time response to that customer, paging an administrator to look at the sensor readings or a more in-depth workflow for data. The message bus sits at the heart of any such data stack.

The message bus is an old concept, with implementations as old as IBM MQ (1993). This category also includes the popular RabbitMQ and the open-source ActiveMQ. But all of these systems have two deficiencies that limit their application to the kind of enterprise-wide data stack that we're discussing here: *data retention* and *scale*.

A modern message bus needs to not only pass events along from one service to another but store them for later usage; this is critical for applications that involve asynchronous analysis, like machine learning. It also needs to be able to scale to manage terabytes of data and millions of messages per second. The old MQ systems are not designed to do either of these.

The most popular options to deliver these requirements for enterprise-wide data flow management are [Apache Kafka](https://www.rtinsights.com/tag/apache-kafka/) and Apache Pulsar. Kafka was launched in 2011 after being developed at LinkedIn, while Apache Pulsar was launched in 2016 by a team at Yahoo. Both support that application event streaming model for getting data to multiple services that might consume data, but the Yahoo team designed Pulsar to improve on Kafka in two key respects.

First, Pulsar was developed to support multi-datacenter and geo-distributed deployments from the start, as well as supporting multi-tenant deployments. This difference in architecture is important if you want to adopt a unified approach across your whole application design and move your data to any geography where it might be needed.

To understand the second way in which Pulsar has advanced the state of the art, we need to briefly discuss the relevance of Kubernetes.

### **How do you want to build today?**

A major element in decisions around software architecture is how developers build their applications in the first place. Microservices-based designs are more popular with developers today as they remove some of the problems around running monolithic applications. IDC [predicted](https://www.idc.com/research/viewtoc.jsp?containerId=US44403818) in 2019 that 90% of applications would be built using microservices designs within three years.

To manage dozens or hundreds of microservices, DevOps teams use Kubernetes to run those application components in containers, a distributed database to support those components, and a message bus to connect them together. These components can then be distributed, so they are close to customers and run in multiple cloud datacenters, in different private datacenter locations, or in a hybrid approach across both private and public cloud.

Clearly, this is harder if you have a centralized single message bus deployment with Kafka. Rather than having the message bus for streaming located with the database and application containers, the messages have to be run from that central system instead. Each message, therefore, gets saddled with a "speed of light" latency addition.[](https://hub.rtinsights.com/cs/c/?cta_guid=928e4496-ee3b-43f7-86f4-eb9f8069397e&signature=AAH58kFJ6fu32blndIEMyYHf168Vvcb7eQ&placement_guid=66d11957-4802-4576-a7fd-221c327cc152&click=368b1931-6860-4cbd-b349-a4668f2f403d&hsutk=0f1d256cc9923cba01dfb024bfc676d1&canon=https%3A%2F%2Fwww.rtinsights.com%2Fthe-critical-role-streaming-plays-in-a-data-stack%2F&portal_id=8019034&redirect_url=APefjpFBHPKLWRyWpcrRDtKkx1anWREP9pyCMGtZ3wnD6KbMPh42JTBnXXKDJ7iC1ZwO9NeE_8Vo9eVSL_hrvkof030HHYsJkjm_ySBf5kz3sAp2XRi67etF9wSmS-tjlOq9_f_J4u5G3yzIF_44mJNK2V76UDd9Puct7n7v9CXjvBCGMgIhEOX5ggQlaOuJAHHu9PE00vJ2BaOFXkPJhC_7boMsfi7eVWyTrYYac9_c-U6ZlPf6ihWH8ZOfOyfsDiOKQjIuoTFgo4r8AqCEYebtN15z5k1VoA&__hstc=50271033.0f1d256cc9923cba01dfb024bfc676d1.1644098379154.1644098379154.1644243519167.2&__hssc=50271033.2.1644243519167&__hsfp=2942453612)

Apache Pulsar enables local instances to be located alongside the application containers and database clusters closer to the customer while still replicating all messages to other consumers across the world. This reduces latency and improves performance while simplifying how the service can scale.

The architecture design for Pulsar also splits the compute and storage sides of the message bus, so that storage can be managed independently --- even by a separate service like HDFS or Amazon S3. This means that Pulsar can retain large volumes of data much more cost-effectively than Kafka, which must store all data on expensive local disks attached to the broker nodes.

Pulsar's approach to abstracting the storage layer enables *tiered storage*, where data is stored on the storage service that is most cost-effective for its purpose. New events can be stored on Apache BookKeeper nodes for high-performance retrieval, while older data is offloaded to S3. This allows a very cost-effective approach of running the production message bus on a small number of nodes, while the much larger volume of historical data can be kept separately --- while still being available, via the same API, to services that need to process that data.

### **The need to be open**

IT teams don't want to be locked into a specific provider. [Bain and Company found](https://www.bain.com/insights/cios-say-they-want-to-avoid-vendor-lock-in-snap-chart/)that around two-thirds of CIOs say they would like to use public cloud services from several different vendors to prevent lock-in. This goes alongside a general preference for open-source software -- a survey by IBM and O'Reilly Media [found](https://developer.ibm.com/blogs/oreilly-open-source-skill-survey-blog/) that 94% of developers rated open-source software as equal to or better than proprietary software, while 70% would use cloud services based on open source.

For developers, open-source software provides a degree of flexibility that proprietary software options cannot. Whether it is consumed as a service out of the cloud or implemented and run on hybrid environments, developers always want to have the option of taking control back and managing their own environments. It's the equivalent of knowing that the door is open in a room -- you may never want to go through it, but the option to leave is comforting.

Being tied to a specific product or platform is not, in itself, wrong -- you have to pick something to run on, and the best platform or product for a given use case should win out. However, lock-in is something that should be avoided where possible, particularly when constructing a stack that will support one of the most critical resources that your organization will have over time -- its data.

Apache Pulsar has a significant role to play in the future of data applications. Pulsar's fully open-source design and cost-effective scaling model makes it easier for developers to build applications around data. By aligning the way they approach application infrastructure, open-source support, and infrastructure scaling models, developers can design data applications that meet business goals, and that can scale up over time. This can get companies closer to achieving their visions around the role of data (and particularly streaming data) in their operations and increase the percentage of organizations that succeed in realizing that vision.
