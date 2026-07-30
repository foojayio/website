---
title: "How Does Kafka Perform When You Need Low Latency?"
slug: "how-does-kafka-perform-when-you-need-low-latency"
date: "2023-01-18T10:12:24+00:00"
lastmod: "2023-06-29T06:47:46+00:00"
description: "Kafka benchmarks aim to discuss low latency characteristics of Kafka. Instead, they appear to be configured for throughput rather than low latency."
authors:
  - "peter-lawrey"
image: "https://foojay.io/wp-content/uploads/2023/01/Screen-Shot-2023-01-09-at-11.12.26-AM-1024x687-1.png"
categories:
  - "Chronicle Software"
  - "Java Core"
  - "Kafka"
  - "Performance"
tags:
related_posts:
frozen: false
---

Most Apache Kafka benchmarks appear to test high throughput but not low latency.

Kafka was traditionally used for high throughput rather than latency-sensitive messaging, but it does have a low-latency configuration. (Mostly setting linger.ms=0 and reducing buffer sizes).

In this configuration, you can get below 1-millisecond latency a good percentage of the time for modest throughputs.

Benchmarks tend to focus on clustering Kafka, in a high-throughput configuration.

While this is perhaps the most common use case, how does it perform if you need lower latencies?

### Where are Some Latency Benchmarks Available? {#h3-0-where-are-some-latency-benchmarks-available}

These are various benchmarks testing higher throughputs of 200kmsg/s to 800kmsg/s, with end-to-end latencies between 2.5 and 30 milliseconds.

* [**Confluent benchmark**](https://www.confluent.io/en-gb/blog/kafka-fastest-messaging-system/ "Confluent benchmark"), looking at the 99 percentile latency compared with Apache Pulsar and Rabbit MQ (pro Kafka) "Kafka provides the lowest latency at higher throughputs, while also providing strong durability and high availability"
* [**NativeStream benchmark**](https://www.confluent.io/en-gb/blog/kafka-fastest-messaging-system/ "NativeStream benchmark ")comparing Pulsar to Kafka. (pro Pulsar) "Pulsar's 99th percentile latency is within the range of 5 and 15 milliseconds."
* [**Instaclustr performance**](https://www.instaclustr.com/blog/the-power-of-kafka-partitions-how-to-get-the-most-out-of-your-kafka-cluster/ "Instaclustr performance"), looking at average latencies with varying number of producers, with different configurations.
* [**Datastax latency**](https://www.datastax.com/blog/2019/08/performance-comparison-between-apache-pulsar-and-kafka-latency "Datastax latency") benchmark using the same benchmark as Confluent. Their conclusion appears to be, when flushing every message to disk, Pulsar is better.
* [**Using Confluent Cloud from AWS**](https://www.davidxia.com/2021/08/benchmarking-kafka-and-google-cloud-pub-slash-sub-latencies/ "Using Confluent Cloud from AWS") "With my specific test parameters, Kafka p99 latencies are 100-200 ms and much lower than Pub/Sub latencies."

My impression is that these benchmarks aren't so much an attempt to show low latency, but rather show what the authors consider good latency under high load.

### Benchmarking Kafka for Low Latency {#h3-1-benchmarking-kafka-for-low-latency}

For a low-latency system, you want the hardware which will best support your requirements. This is often plenty of the fastest CPUs you can afford and more than enough IO bandwidth as well.

The best way to go fast is often to do as little as possible, keep the solution simple. In my case, I am starting with just one PC, a Ryzen 9 5950X with 64 GB memory and a Corsair MP600 PRO XT M.2 drive.

Obviously cluster support is an important use case for Kafka, but let's start with a really simple end-to-end use case: one machine, two message hops and a trivial microservice in between.

### One Machine, One Trivial Microservice, End-to-End Latency {#h3-2-one-machine-one-trivial-microservice-end-to-end-latency}

This benchmark is similar to a previous one found [here](https://chronicle.software/benchmarking-kafka-vs-chronicle-for-microservices-which-is-750-times-faster "here").

However, Kafka is configured for lower latencies and multiple producers are used to support a significant, but lower, message throughput.

In this configuration, Kafka has a fraction of the latencies reported in the benchmarks above.

![](/images/posts/2023/01/how-does-kafka-perform-when-you-need-low-latency/Screen-Shot-2023-01-09-at-11.12.26-AM-1024x687.png)

One producer doesn't handle this throughput well, but 2 and above producers (I tested up to 10) produce good results. Increasing the number of partitions only increased the overhead (albeit slightly). Increasing the number of consumers saw a small variation in latencies.

To put this in perspective I added the results for a single producer using [Chronicle Queue Enterprise](https://chronicle.software/queue-enterprise/ "Chronicle Queue Enterprise") which you might expect has far, far less jitter (see the almost invisible blue line at the bottom of the graph above, the line runs just above the X-Axis, the reason this line can't be seen is that Chronicle Queue is performing significantly better than Kafka).

This indicates the performance between processes on the same machine.

### No Conclusion {#h3-3-no-conclusion}

I'd like to finish with a conclusion but this leaves me with more questions than answers.

The benchmarks linked at the start of the post aim to discuss the low latency characteristics of Kafka.

However, in actual fact, these tests appear to have instead configured Kafka to maximise throughput rather than for low latency.

This document has shown that while Kafka can produce better benchmark numbers when suitably configured for low latency, even in this setup other options such as Chronicle Queue Enterprise can perform two or more orders of magnitude better.

As discussed in more detail [here](https://chronicle.software/cloud/ "here"), with a suitably tuned environment and platform, one can expect to run in the cloud while still also demanding low latency.
