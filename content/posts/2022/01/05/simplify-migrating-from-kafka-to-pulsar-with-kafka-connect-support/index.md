---
title: "Simplify Migrating from Kafka to Pulsar with Kafka Connect Support"
slug: "simplify-migrating-from-kafka-to-pulsar-with-kafka-connect-support"
date: "2022-01-05T11:30:09+00:00"
lastmod: "2022-01-05T11:30:11+00:00"
description: "The new Kafka Connect Adaptor completes the Pulsar-Kafka compatibility ecosystem, allowing an iterative transition from Kafka to Pulsar."
canonical: "https://www.datastax.com/blog/simplify-migrating-kafka-to-pulsar-kafka-connect-support"
authors:
  - "enrico-olivelli"
image: "dd0ae5cb262383229cf5f694659e03b28cdad97e-1996x1322-1.png"
categories:
  - "Apache Cassandra"
  - "Apache Pulsar"
  - "Databases"
  - "Kafka"
  - "Microservices"
tags:
related_posts:
  - "7-reasons-to-choose-apache-pulsar-over-apache-kafka"
  - "a-case-for-databases-on-kubernetes-from-a-former-skeptic"
  - "developing-an-enterprise-level-apache-cassandra-sink-connector-for-apache-pulsar"
  - "why-developers-should-use-apache-pulsar"
enlighterjs: true
frozen: false
---

Large-scale implementations of any system, such as the event-streaming platform Apache Kafka, often involve customizations and tools and plugins developed in-house. When it's time to transition from one system to another, the task can become complicated, drawn-out, and error-prone. Often the benefits of an alternative system (which can include [significant cost savings](https://gigaom.com/report/the-cost-savings-of-replacing-kafka-with-pulsar/) and other efficiencies) are outweighed by the risks and costs of migration. As a result, an organization can end up locked into a suboptimal situation, footing a bigger bill than necessary and missing out on modern features that help move the business forward faster.

These risks and costs can be mitigated by making the transition process iterative, breaking off the vendor lock-in in small, manageable steps, and avoiding the "big bang" switch that often results in delay delivery and increase the cost of running two systems in parallel for A\|B testing.

Let's take a quick look at the existing ecosystem that helps navigate the transition from Kafka to Apache Pulsar, dive into the new addition to the ecosystem in Pulsar 2.8, and look at important changes in Pulsar IO API and Pulsar Schema API that automate and simplify schema handling in Sinks.

Throughout this article, we will follow the convention of discussing streaming *sources* , that push data into Pulsar from another system, and *sinks*, that send data from Pulsar to another destination.

## Current state of the Pulsar-Kafka ecosystem

### Built-in connectors to Kafka

Built-in connectors simplify pulling/pushing data between Pulsar and Kafka topics.

This is useful if you want to leave existing systems running on Kafka while building new functionality on Pulsar.

More details are available in the Pulsar documentation:

* Source (ingest data to Pulsar from Kafka): <https://pulsar.apache.org/docs/en/io-kafka-source/>
* Sink (send data from Pulsar to Kafka): <https://pulsar.apache.org/docs/en/io-kafka-sink/>

### Kafka on Pulsar

Kafka on Pulsar (KoP) is the recommended way to use the native Kafka client with Pulsar.

KoP is a *protocol handler*. This means that it interprets the Kafka protocol at the network level and translates it into Pulsar requests. There are three key advantages to this approach:

1. KoP works with all Kafka clients
2. KoP uses the well-defined interface between Kafka client and server
3. Client code does not need to change at all

### Kafka Connect Adaptor

Most people use Kafka (and Pulsar) via connectors to other systems, rather than writing low-level client code by hand. Pulsar has [native connectors](https://pulsar.apache.org/docs/en/io-overview/) available for the most popular systems, but as of this writing, there are many more connectors for Kafka that do not yet exist for Pulsar, including private connectors created in-house for use at a single company.

The Kafka Connect Adaptor (KCA) bridges this gap. KCA is a Pulsar Source and Sink that runs a Kafka Connect Sink or Source. The Kafka Connect Adaptor Sink is new in Pulsar 2.8.

Currently, the documentation is scarce, but using KCA is simple. We will look at examples of using both the KCA Sink and Source below.

## Using the Kafka Connect Adaptor Sink

Using Kafka Connect Adaptor Sink is fairly straightforward. All you need to do is package the Kafka Connect connector, create the configuration, and use it as a regular Pulsar Sink.

### Step 1: Package

Use Kafka Connect Adaptor NAR <https://github.com/apache/pulsar/tree/master/pulsar-io/kafka-connect-adaptor-nar> as a starting point (for simplicity, I'll edit it directly) and add your Kafka Connector Sink to the list of the dependencies in pom.xml. Here's what this would look like with the Kinesis Kafka connector sink:

```
diff --git a/pulsar-io/kafka-connect-adaptor-nar/pom.xml b/pulsar-io/kafka-connect-adaptor-nar/pom.xml
index ea9bedbd056..c7fa9a1ebca 100644
--- a/pulsar-io/kafka-connect-adaptor-nar/pom.xml
+++ b/pulsar-io/kafka-connect-adaptor-nar/pom.xml
@@ -36,6 +36,11 @@
       pulsar-io-kafka-connect-adaptor
       ${project.version}

+ 
+ com.amazonaws
+ amazon-kinesis-kafka-connector
+ 0.0.9-SNAPSHOT
+
```

Build the NAR:

```
$ mvn -f pulsar-io/kafka-connect-adaptor-nar/pom.xml clean package -DskipTests
```

### Step 2: Configuration

The Sink expects "processingGuarantees" to be "EFFECTIVELY_ONCE"\`, configs pointing to the Pulsar instance \& topic to store processed offsets at, topic to read the data from, and configuration to pass to the Kafka Connect Sink.

For example:

```
processingGuarantees: "EFFECTIVELY_ONCE"
configs:
  "topic": "my-topic"
  "offsetStorageTopic": "kafka-connect-sink-offset-kinesis"
  "pulsarServiceUrl": "pulsar://localhost:6650/" 
  "kafkaConnectorSinkClass": "com.amazon.kinesis.kafka.AmazonKinesisSinkConnector"
  # The following properties passed directly to Kafka Connect Sink and defined by it
  "kafkaConnectorConfigProperties":
     "name": "test-kinesis-sink"
     'connector.class': "com.amazon.kinesis.kafka.AmazonKinesisSinkConnector"
     "tasks.max": "1"
     "topics": "my-topic"
     "kinesisEndpoint": "kinesis.us-east-1.amazonaws.com"
     "region": "us-east-1"
     "streamName": "test-kinesis"
     "singleKinesisProducerPerPartition": "true"
     "pauseConsumption": "true"
     "maxConnections": "1"
```

### Step 3: Profit!

Follow regular Pulsar's steps to use the packaged connector: <https://pulsar.apache.org/docs/en/io-use/>

## Using the Kafka Connect Adaptor Source

KCA Source has been available since Pulsar version 2.3.0. In the simplest case, its usage is similar to the KCA Sink's: add the dependency and build, provide configuration and run.

Currently, KCA Source only supports Sources that return data in Apache Avro or JSON formats.

For detailed examples of the use of the Source Adaptor please look at [Pulsar's Debezium Connector](https://github.com/apache/pulsar/blob/master/pulsar-io/debezium/core/src/main/java/org/apache/pulsar/io/debezium/DebeziumSource.java).

## Under the hood: Building a better developer experience for Pulsar IO

Apache Pulsar 2.8 offers many improvements to the Java Pulsar Schema API and to the Pulsar IO API that helped to fill in the gaps between Kafka Connect and Pulsar IO. These improvements were foundational for Kafka Connect Adaptor Sink and result in easier development of Pulsar IO Sinks in general.

The Kafka Connect user must explicitly configure the Sink (or the Kafka Consumer) deserializer configuration in order to use the correct deserializer, even if the code is not tied to a particular schema. The power of the updated Pulsar Schema API makes everything automatic and removes the need for explicit configurations.

Let's take a deeper look at the Pulsar IO API improvements below; for more technical details, please refer to the [PIP-85](https://docs.google.com/document/d/1VWi5LHP44V31nP4bCui9d5RXwH6xc_phrUes6tvNguk/edit?usp=sharing).

### Runtime handling of the schema

We have contributed the support for coding schema-aware Pulsar IO Sinks that do not depend on a particular schema at build time. In other words, in Pulsar 2.7 you had to declare the schema type in your sink:

```
class MySink implements Sink {
     public void write(Record record) {
     }
}
```

To support "String" and "GenericRecord" (JSON and Avro structures) you had to create two classes and the user who deploys the Sink had to use the "--classname" argument to set the correct implementation for the given topic.

In Pulsar 2.8 you can simply use this syntax:

```
class MySink implements Sink {
      public void write(Record record) {}
}
```

This sink will work with every schema type and with topics without a schema. It also supports schema evolution and KeyValue schema type.

### Seamless support of KeyValue messages

The second gap between Kafka Connect and Pulsar IO was the lack of seamless support for KeyValue messages.

For many versions, Pulsar offered the powerful KeyValue schema type that supported setting a schema for the Key and the Value. With a Sink\<GenericObject\> you can handle the KeyValue schema as well, writing your code only once and keeping it simpler.

### Access message and schema details for messages consumed with Schema.AUTO_CONSUME

Pulsar uses a special AUTO_CONSUME schema to validate and deserialize messages using schemas received from the broker. Currently, it supports Avro, JSON, and ProtobufNativeSchema schemas. You can find more details in the documentation <https://pulsar.apache.org/docs/en/schema-understand/#auto_consume>

Before Pulsar 2.8, AUTO_CONSUME allowed you to decode the message according to the version of the schema attached to the message but did not allow access to the exact schema definition. Pulsar 2.8 enhances the API by providing access to this information:JavaCopy

```
Schema schema = message.getReaderSchema().get();

org.apache.avro.Schema avroSchema = (org.apache.avro.Schema) schema.getNativeSchema().get();
org.apache.avro.generic.GenericRecord nativeRecord = (org.apache.avro.generic.GenericRecord) consumedRecord.getNativeObject();
```

Message.getReaderSchema() method returns the actual schema used for decoding the message, even in the case of the special AUTO_CONSUME Schema. Such schema automatically downloads new versions of the Schema while the topic evolves.

Schema.getNativeSchema() and GenericRecord.getNativeObject() methods provide access to the underlying implementation of the schema and the Java model of the message. In particular, you can access the Avro schema and the Avro GenericObject instance under the covers.  

## Summing up

The new Kafka Connect Adaptor completes the Pulsar-Kafka compatibility ecosystem. This ecosystem currently allows an iterative transition from Kafka to Pulsar, supports the use of native Kafka clients with Pulsar, the use of Kafka Connect connectors on Pulsar, and data transfer between two systems.

With all these great features available, we hope focus shifts from worrying about the complexity of onboarding Pulsar over existing Kafka implementations to finding new ways their business can benefit from the power of Pulsar.

**Want to try out Apache Pulsar?** [Sign up now](https://astra.dev/31jkjCn) for [Astra Streaming](https://www.datastax.com/products/astra-streaming), our fully managed Apache Pulsar service. We'll give you access to its full capabilities entirely free through beta. See for yourself how easy it is to build modern data applications and let us know what you'd like to see to make your experience even better.
