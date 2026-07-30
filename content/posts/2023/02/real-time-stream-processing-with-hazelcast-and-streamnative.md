---
title: "Real-time Stream Processing with Hazelcast and StreamNative"
slug: "real-time-stream-processing-with-hazelcast-and-streamnative"
date: "2023-02-01T09:09:04+00:00"
lastmod: "2023-02-01T09:11:50+00:00"
description: "Learn how to stream data from Apache Pulsar into Hazelcast, where you learn how to process data in real time."
authors:
  - "fawaz-ghali"
  - "tim-spann"
image: "https://foojay.io/wp-content/uploads/2023/01/Screenshot-2023-01-27-at-14.00.51.png"
categories:
  - "Apache Pulsar"
  - "Hazelcast"
  - "Streaming"
  - "Tutorials"
tags:
related_posts:
  - "7-reasons-to-choose-apache-pulsar-over-apache-kafka"
  - "5-more-reasons-to-choose-apache-pulsar-over-apache-kafka"
  - "bring-streaming-to-apache-cassandra-with-apache-pulsar"
  - "streaming-real-time-data-on-the-hazelcast-viridian-serverless"
enlighterjs: true
frozen: false
---

Introduction {#h2-0-introduction}
---------------------------------

One of the most useful features of real-time stream processing is to combine the strengths and advantages of various technologies to provide a unique developer experience and an efficient way of processing data in real time at scale.

* **Hazelcast** is a real-time distributed computation and storage platform for consistently low latency queries, aggregation and stateful computation against real-time event streams and traditional data sources.
* **Apache Pulsar** is a real-time multitenant geo-replicated distributed pub-sub messaging and streaming platform for real-time workloads handling millions of events per hour.

However, real-time stream processing is not an easy task, especially when combining multiple live streams with large volumes of data stored in external data storages to provide context and instant results.

When it comes to usage, Hazelcast can be used for stateful data processing over real-time streaming data, data at rest or a combination of both, querying streaming and batch data sources directly using SQL, distributed coordination for microservices, replicating data from one region to another or between data centres in the same region.

Meanwhile, Apache Pulsar **can be used for** both messaging and streaming use cases, taking the place of multiple products and provides a superset of their features. Apache Pulsar is a cloud-native multitenant unified messaging platform to replace Apache Kafka, RabbitMQ, MQTT and legacy messaging platforms. Apache Pulsar provides an infinite message bus for Hazelcast to act as an instant source and sink for any and all data sources.

![](https://hazelcast.com/wp-content/uploads/2023/01/Screenshot-2023-01-27-at-14.00.51.png)

Prerequisites {#h2-1-prerequisites}
-----------------------------------

We're building an application where we ingest data from Apache Pulsar into Hazelcast and then process it in real-time. To run this application, make sure your system has the following components:

* [Hazelcast](https://docs.hazelcast.com/hazelcast/latest/getting-started/get-started-cli) installed on your system: we're using CLI
* [Pulsar](https://pulsar.apache.org/docs/2.10.x/getting-started-docker/) installed on your system: we're using Docker

If you have macOS \& Homebrew, you can install Hazelcast using the following command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">brew tap hazelcast/hz

brew install <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="325a5348575e5153414672071c001c03">[email&nbsp;protected]</a></pre>

Check if Hazelcast is installed:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">hz -V</pre>

Then start a local cluster:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">hz start</pre>

You should see the following in the console:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">INFO: [192.168.1.164]:5701 [dev] [5.2.1]
Members {size:1, ver:1} [
  Member [192.168.1.164]:5701 - 4221d540-e34e-4ff2-8ad3-41e060b895ce this
]</pre>

You can start Pulsar in Docker using the following command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">docker run -it -p 6650:6650 -p 8080:8080 \
    --mount source=pulsardata,target=/pulsar/data \
    --mount source=pulsarconf,target=/pulsar/conf \
    apachepulsar/pulsar:2.11.0 bin/pulsar standalone</pre>

To install Management Center, use one of the following methods, depending on your operating system:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">brew tap hazelcast/hz

brew install <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="91f9f0ebf4fdf2f0e2e5bcfcf0fff0f6f4fcf4ffe5bcf2f4ffe5f4e3d1a4bfa3bfa0">[email&nbsp;protected]</a></pre>

Check that Management Center is installed:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">hz-mc -V

</pre>

Data collection {#h2-2-data-collection}
---------------------------------------

For our application, we wish to ingest air quality readings from around the United States via the AirNow data provider.

If you wish to learn more about Air Quality, check out the information at [AirNow](https://docs.airnowapi.org/aq101).

**Source** : <https://docs.airnowapi.org/>

With a simple Java application we make REST calls to the AirNow API that provides air quality reading for major zip codes around the United States.

The Java application sends the JSON encoded AirNow data to the "airquality" Pulsar topic. From this point a Hazelcast application can read it.

**Source** : <https://github.com/tspannhw/spring-pulsar-airquality>

We also have a Java Pulsar function receiving each event from the "airquality" topic and parsing it into different topics based on which type of air quality reading it is. This includes PM2.5, PM10 and Ozone.

**Source** : <https://github.com/tspannhw/pulsar-airquality-function>

Example AirQuality Data

<pre class="EnlighterJSRAW" data-enlighter-language="java">{"dateObserved":"2023-01-19 ","hourObserved":12,"localTimeZone":"EST","reportingArea":"Philadelphia","stateCode":"PA","latitude":39.95,"longitude":-75.151,"parameterName":"PM10","aqi":19,"category":{"number":1,"name":"Good","additionalProperties":{}},"additionalProperties":{}}</pre>

Example Ozone Data

<pre class="EnlighterJSRAW" data-enlighter-language="generic">{"dateObserved":"2023-01-19 ","hourObserved":12,"localTimeZone":"EST","reportingArea":"Philadelphia","stateCode":"PA","parameterName":"O3","latitude":39.95,"longitude":-75.151,"aqi":8}</pre>

Example PM10 Data

<pre class="EnlighterJSRAW" data-enlighter-language="generic">{"dateObserved":"2023-01-19 ","hourObserved":12,"localTimeZone":"EST","reportingArea":"Philadelphia","stateCode":"PA","parameterName":"PM10","latitude":39.95,"longitude":-75.151,"aqi":19}</pre>

Example PM2.5 Data

<pre class="EnlighterJSRAW" data-enlighter-language="generic">{"dateObserved":"2023-01-19 ","hourObserved":12,"localTimeZone":"EST","reportingArea":"Philadelphia","stateCode":"PA","parameterName":"PM2.5","latitude":39.95,"longitude":-75.151,"aqi":54}</pre>

![](https://hazelcast.com/wp-content/uploads/2023/01/Screenshot-2023-01-27-at-14.01.42.png)

Data processing {#h2-3-data-processing}
---------------------------------------

In order to process the data collected, we used the[Hazelcast Pulsar connector](https://docs.hazelcast.com/hazelcast/latest/pipelines/pulsar) module to ingest data from Pulsar topics (note: you can use the same connector to write to Pulsar topics).

Using Hazelcast allows us to compute various aggregation functions (sum, avg etc.) in real time on a specified window of stream items.

The Pulsar connector uses the Pulsar client library, which has two different ways of reading messages from a Pulsar topic. These are Consumer API and Reader API, both use the builder pattern (for more information [click here](https://github.com/hazelcast/hazelcast-jet-contrib/tree/master/pulsar)).

In your pom file, import the following dependencies.

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;dependency&gt;
    &lt;groupId&gt;com.hazelcast&lt;/groupId&gt;
    &lt;artifactId&gt;hazelcast&lt;/artifactId&gt;
    &lt;version&gt;5.1.4&lt;/version&gt;
&lt;/dependency&gt;

&lt;dependency&gt;
    &lt;groupId&gt;com.hazelcast.jet.contrib&lt;/groupId&gt;
    &lt;artifactId&gt;pulsar&lt;/artifactId&gt;
    &lt;version&gt;0.1&lt;/version&gt;
&lt;/dependency&gt;

&lt;dependency&gt;
    &lt;groupId&gt;org.apache.pulsar&lt;/groupId&gt;
    &lt;artifactId&gt;pulsar-client&lt;/artifactId&gt;
    &lt;version&gt;2.10.1&lt;/version&gt;
&lt;/dependency&gt;</pre>

We create a PulsarSources.pulsarReaderBuilder instance to connect to the previously started pulsar cluster located at pulsar://localhost:6650.

<pre class="EnlighterJSRAW" data-enlighter-language="java">StreamSource&lt;Event&gt;source = PulsarSources.pulsarReaderBuilder(
     topicName,
     () -&gt; PulsarClient.builder().serviceUrl("pulsar://localhost:6650").build(),
     () -&gt; Schema.JSON(Event.class),
     Message::getValue).build();</pre>

We then create a pipeline to read from the source with a sliding window and aggregate count, before we write to logger:

<pre class="EnlighterJSRAW" data-enlighter-language="java">Pipeline p = Pipeline.create();
p.readFrom(source)
 .withNativeTimestamps(0)
 .groupingKey(Event::getUser)
 .window(sliding(SECONDS.toMillis(60), SECONDS.toMillis(30)))
 .aggregate(counting())
 .writeTo(Sinks.logger(wr -&gt; String.format(
      "At %s Pulsar got %,d messages in the previous minute from %s.",
      TIME_FORMATTER.format(LocalDateTime.ofInstant(
              Instant.ofEpochMilli(wr.end()), ZoneId.systemDefault())),
      wr.result(), wr.key())));

JobConfig cfg = new JobConfig()
     .setProcessingGuarantee(ProcessingGuarantee.EXACTLY_ONCE)
     .setSnapshotIntervalMillis(SECONDS.toMillis(1))
     .setName("pulsar-airquality-counter");

HazelcastInstance hz = Hazelcast.bootstrappedInstance();
hz.getJet().newJob(p, cfg);</pre>

You can run the previous code from your IDE (in this case, it will create its own Hazelcast member and run the job on it), or you can run this on the previously started Hazelcast member (in this case, you need to create a runnable JAR including all dependencies required to run it):

<pre class="EnlighterJSRAW" data-enlighter-language="generic">mvn package

bin/hz-cli submit target/pulsar-example-1.0-SNAPSHOT.jar</pre>

To cancel the job and shut down the Hazelcast cluster:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">bin/hz-cli cancel pulsar-message-counter

hz-stop</pre>

Conclusion {#h2-4-conclusion}
-----------------------------

In this article, we have demonstrated how you can combine the strengths and advantages of various technologies to provide a unique developer experience and an efficient way of processing data in real time at scale.

We stream data from Apache Pulsar into Hazelcast, where we processed data in real time.

The rising trend in cloud technologies, the need for real-time intelligent applications and the urgency to process data at scale have brought us to a new chapter of real-time stream processing, where latencies are measured, not in minutes but in milliseconds and submilliseconds.

Hazelcast allows you to quickly build resource-efficient, real-time applications. You can deploy it at any scale from small edge devices to a large cluster of cloud instances. A cluster of Hazelcast nodes share both the data storage and computational load which can dynamically scale up and down. When you add new nodes to the cluster, the data is automatically rebalanced across the cluster, and currently running computational tasks (known as jobs) snapshot their state and scale with processing guarantees.

Pulsar allows you to use your choice of messaging protocols to quickly distribute events between multiple types of consumers and producers and act as a universal message hub. Pulsar separates compute from storage allowing for dynamic scaling and efficient handling of fast data.

StreamNative is the company made up of the original creators of Apache Pulsar and Apache BookKeeper. StreamNative provides a full enterprise experience for Apache Pulsar in the cloud and on premise.

More on Hazelcast {#h2-5-more-on-hazelcast}
-------------------------------------------

* **Join us on our Real-Time Stream Processing Unconference (#RTSPUnconf): <https://hazelcast.com/lp/unconference/>**
* Learn the Hazelcast Fundamentals: Start a Local Cluster with the [CLI](https://docs.hazelcast.com/hazelcast/latest/getting-started/get-started-cli) or [Docker](https://docs.hazelcast.com/hazelcast/latest/getting-started/get-started-docker).
* Start a [Viridian Serverless Cluster](https://viridian.hazelcast.com/): Serverless is a managed cloud service that offers a pay-as-you-go pricing model. Serverless clusters auto-scale to provide the resources that your application needs. You pay only for the resources that your application consumes.
* Join the Hazelcast [Slack](https://slack.hazelcast.com/) and Hazelcast [Github](https://github.com/hazelcast/hazelcast) repository.

More on Apache Pulsar {#h2-6-more-on-apache-pulsar}
---------------------------------------------------

* Learn the Pulsar Fundamentals: While this blog did not cover the Pulsar fundamentals, there are great resources available to help you learn more. If you are new to Pulsar, we recommend you to take the [self-paced Pulsar courses](https://www.academy.streamnative.io/tracks) or [instructor-led Pulsar training](https://streamnative.io/training/) developed by some of the original creators of Pulsar. This will get you started with Pulsar and accelerate your streaming immediately.
* Spin up a Pulsar Cluster in Minutes: If you want to try building microservices without having to set up a Pulsar cluster yourself, sign up for [StreamNative Cloud](https://streamnative.io/streamnativecloud/) today. StreamNative Cloud is a simple, fast, and cost-effective way to run Pulsar in the public cloud.
* Join the Apache Pulsar [Slack](https://communityinviter.com/apps/apache-pulsar/apache-pulsar)
* <https://github.com/tspannhw/pulsar-hazelcast-airquality>
* <https://github.com/tspannhw/spring-pulsar-airquality>
* <https://github.com/tspannhw/pulsar-airquality-function>
