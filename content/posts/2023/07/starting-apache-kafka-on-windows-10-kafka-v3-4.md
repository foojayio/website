---
title: "Starting Apache Kafka v3.4 on Win 10"
slug: "starting-apache-kafka-on-windows-10-kafka-v3-4"
date: "2023-07-29T07:43:32+00:00"
lastmod: "2023-07-30T07:47:48+00:00"
description: "Part one of a two part series on Running Apache Kafka Server, Configuring Kafka Topics, and Creating a Kafka Consumer and Kafka Producer."
authors:
  - "sumith-puri"
image: "/images/posts/2023/07/starting-apache-kafka-on-windows-10-kafka-v3-4/image-6-1.png"
categories:
  - "Kafka"
  - "Tutorials"
tags:
related_posts:
  - "starting-docker-desktop-with-spring-boot"
  - "clean-shutdown-of-spring-boot-applications"
  - "gang-of-four-design-patterns-using-core-java-part-01"
  - "starting-apache-kafka-java-producer-consumer-windows-10"
enlighterjs: true
frozen: false
---

### Introduction {#h3-0-introduction}

This is part one of a two part articles series on Running Apache Kafka Server, Configuring Kafka Topics, and Creating a Kafka Consumer and Kafka Producer.

All this is demonstrated step-by-step example that works from the Command Line.

All of this is for Apache Kafka v3.4 on Windows 10.

### Pre-Requisites {#h3-1-pre-requisites}

1. Install Java ( v8.0 is used in this Example )
2. Install Apache Kafka v3.4.0 from the Given Link
3. Set Java Classpath \> Set JAVA_HOME Correctly
4. UnZIP/UnTAR Apache Kafka Downloaded in (2)
5. Use a Text Editor like \[ Notepad++ \] for Editing

![](https://blogger.googleusercontent.com/img/a/AVvXsEhDK7xTGu-H4ZyFHWetkwEfKxIEjdMZuqYwimNrD-hIblKuBYqaW1Fttr8V_f19Q73z0MS09mNQYEhjuX8noY92cQ0dy4koVTnEmGSV76byHwHgM2uJ9ePcLSQYwFlcqBiWRI4QgXHE1nZYNsFPV3v0Nf2ozGkGc91DE3aKxOrDZV2IsPGlzFEKpNB8txrF)

### Version 3.4.0 {#h3-2-version-3-4-0}

Apache Kafka Version 3.4.0 was Released on Feb 7, 2023, This article specifically is for the Kafka Version (2.13-3.4.0). For Purposes of this Article, I use {KAFKA_HOME} as the windows folder where Kafka was installed.

### Step-By-Step Guide {#h3-3-step-by-step-guide}

#### 0. Configure Zookeeper (Data Directory)

Create a folder to hold Zookeeper Data by modifying the file zookeper.properties (File is Located under {KAFKA_HOME}/config/). Create a Folder named zk-data (or as per your wish). In my case, I created this under {KAFKA_HOME}. You may then modify your properties file as show in the image below. Modify your dataDir to point to the newly created folder.

![](https://blogger.googleusercontent.com/img/a/AVvXsEgzsc5xL2Lk86WYnajF_5MxDpZhWzg3g4fg3j2gyFH8m8zXruQu3V9Kd-t-6zwzpMePLZo1dE6Z5uflQUEKkL47o-h4xrmLBuEJQ1iQn6CNq9iJUjqCIDziwilh9WJ1-CpYO4YqH0nz7kFOHW-SIj2NJ2iYrSwn1kxWP1JaeGgdtwGW4iUuj6tmv-lhDulD=w640-h333)

*** ** * ** ***

#### 0. Configure Kafka (Kafka Logs)

For the purpose of kafka logs, you can create a folder with the name kafka-logs. In my case, I created this under {KAFKA_HOME}. You may then modify your properties file as show in the image below. The property to be modified is log.dirs in server.properties that should now point to the newly created folder.

![](https://blogger.googleusercontent.com/img/a/AVvXsEj9NXAyXlj-enEDvFT75mgGDcZaEmGb5hwB5AoznlWpfEzW1zKJOZIBLv2MiAgo3XrGrEsRHq0dyIuun8T4jdSvNNDAGPnThretMD2knm3nkrMVVhcPqTT3kvPvHh0nIRGIISJPzgW5ONh60QWJw3FaeHnMNAYu5xI4k8plohQJAX-yT-rqX2MlCv6lXaeD=w640-h557)

*** ** * ** ***

#### 1. Starting Zookeeper

First, Zookeeper has to be started using the following command.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">zookeeper-server-start.bat ..\..\config\zookeeper.properties</pre>

![](https://blogger.googleusercontent.com/img/a/AVvXsEgN6T9mXaYRzYuRSC76JIVveE6aF3qO49HQfzcQXavStVBRHWXZaats4_UHK4LZDAMUHPnoszFFCE0b-6B99YMiwjyuVNmdlI9nJs3ech6Na8If09XG3tzJJr8mGUogZWjrv1_0iF5gcCBe77-yQ5vrQZ0bLkClq72mSIZ0pLR4TvmPUX8_OrcVQRttNh6h=w640-h340)

*** ** * ** ***

#### 2. Starting Kafka Server

Next, we will start the Kafka Server using the following command.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">kafka-server-start.bat ..\..\config\server.properties</pre>

![](https://blogger.googleusercontent.com/img/a/AVvXsEgtQI7iKLLbbqtLjhsxDjKVhT-C9wYjkVIy9tyEUNKrDQof6D4vxk4sRRBnGXnW-e08VWmq-r381h41Tku99M7ffPlCkFJerLIwGGyrxEgkOat5GIcJJtjgODdwHYgIMBxT041Rt60NFypFRL9VzU1AnZdY840AI1gD4gWI0-VdwydZQPFLB-UHAK82s-bS=w640-h338)

*** ** * ** ***

#### 3. Creating a Test Topic

Create a Kafka Topic to test out the Kafka Installation using the following command.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic test<code></code></pre>

![](https://blogger.googleusercontent.com/img/a/AVvXsEjjiMOt1XyY3YkmDdyfZsg5_7CcUCp7-6jHcaayMx54busg9nf_d1fe7jypHXifPX5vYmTOwb4cy0Ei5ad1kVwlwQ1TeInPct2NUzGsx-lig7rF09dropGBcYv3r2Q7JzciqlAitenBAsxJ24tb6dCqi9OVrGO_rMc2h32zmVj0-tinLeuhm8ZDM6z6Nq_1=w640-h44)

The above is an updated way to create topics in Kafka. In earlier versions of Kafka (Kafka v2), the suggested way to create topics was directly via Zookeeper. From v3, It has changed to create topics via Brokers.

**(Cited from StackOverflow)**

For version 2.\* you have to create the topic using zookeper with the default port 2181 as a parameter.

For the version 3.\* the zookeeper is not any more a parameter, you should use --bootstrap-server using localhost or the IP adresse of the server and the default port 9092.

[Documentation](https://kafka.apache.org/30/documentation.html#quickstart "Documentation")

*** ** * ** ***

#### 4. Create Kafka Producer

<pre class="EnlighterJSRAW" data-enlighter-language="generic">kafka-console-producer.bat --broker-list localhost:9092 --topic test</pre>

![](https://blogger.googleusercontent.com/img/a/AVvXsEixfqIFVylNlbpptv-p8Y-ksVmCnHmxsPzcV0n__BPATcuflsftgEf4ZNR-pu7_pEVTKzkiJWIPKxp8fIDnQLba1fU7GYpNd3IhqyY24tQrdxBD6wu6n8GfkisiQ7wXASQPusWJQ8PVC-YD2_bB44ORR5AFXBrzsc7scwxN2rRQs7uf0uuYsNgmqbZTaGYv=w640-h106)

*** ** * ** ***

#### 5. Create Kafka Consumer

<pre class="EnlighterJSRAW" data-enlighter-language="generic">kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic test --from-beginning</pre>

![](https://blogger.googleusercontent.com/img/a/AVvXsEgKCyhyDTcZdiQZzlKthGwXauQFBgndg3OB87_SEES3C2GVDUsidUKYwszYuOYNZOnC6pylFG6j733LFASd4jjjMkNXDI35Y6dg_u9KF6c8P2S77l5hkldYvVc-63-3n7Rh-trUIo-4zEx7VkfQNqIw2-92H29JEZ0eenHzgraRCwPa2toQdzvRxOJ9ZfRN=w640-h104)

*** ** * ** ***

Next in this series of articles will be the demonstration of a Core Java Kafka Producer and Consumer followed by an article on Spring Boot based Kafka Integration.
