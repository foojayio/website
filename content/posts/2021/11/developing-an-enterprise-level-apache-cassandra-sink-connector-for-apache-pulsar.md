---
title: "Enterprise-Level Apache Cassandra Sink Connector for Apache Pulsar"
slug: "developing-an-enterprise-level-apache-cassandra-sink-connector-for-apache-pulsar"
date: "2021-11-24T19:16:16+00:00"
lastmod: "2021-11-24T19:17:46+00:00"
description: "From streaming with Apache Pulsar to connecting existing enterprise data sources to Apache Cassandra using Pulsar."
canonical: "https://www.datastax.com/blog/2021/03/developing-enterprise-level-apache-cassandra-sink-connector-apache-pulsar"
authors:
  - "enrico-olivelli"
image: "https://foojay.io/wp-content/uploads/2021/07/blog-category-technical-how-tos.png"
categories:
  - "Apache Cassandra"
  - "Apache Pulsar"
  - "Databases"
  - "DataStax"
  - "Microservices"
tags:
related_posts:
  - "a-case-for-databases-on-kubernetes-from-a-former-skeptic"
  - "apache-cassandra-4-0-taming-tail-latencies-with-java-16-zgc"
  - "ingesting-time-series-events-from-kafka-into-questdb"
enlighterjs: true
frozen: false
---

When [DataStax started investing in streaming with Apache Pulsar](https://techcrunch.com/2021/01/27/datastax-acquires-kesque-as-it-gets-into-data-streaming/)™, we knew that one of the first things people would want to do was connect existing enterprise data sources to Apache Cassandra™ using Pulsar.

Apache Pulsar has a powerful framework called Pulsar IO to enable this kind of use case, and at DataStax we already had a best-in-class [Kafka Connect Sink](https://docs.datastax.com/en/kafka/doc/kafka/kafkaIntro.html) that enables you to store structured data coming from one or more Kafka topics into DataStax Enterprise, Apache Cassandra, and Astra.

In this post, I'll describe how we built a powerful and versatile Pulsar Sink to connect data streams to Cassandra by abstracting the core logic from our Kafka Sink and expanding it to support Pulsar. This resulted in the recent release of our [Pulsar Sink Connector](https://github.com/datastax/pulsar-sink) (read the docs [here](https://docs.datastax.com/en/pulsar-connector/1.4/index.html)), which provides users with the same power and flexibility that they get with our Kafka connector.

Streams of structured data {#h2-0-streams-of-structured-data}
-------------------------------------------------------------

Complex data flows can lead to the need to manage many types of data; this is reflected in database tables and schemas. However, you also have to deal with the same level of complexity when you exchange data using streaming technologies.

Apache Pulsar offers built-in mechanisms to deal with Schemas and enables you to consistently manage all of the data structures in your information system, by providing:

* An embedded Schema registry, no the need for third-party Schema Registries
* Low-level APIs to handle schema information with Messages
* High-level APIs to handle schema information on Records
* Schema evolution support and automatic validation of compatibility during schema modifications
* Native support for Apache Avro, JSON and Google Protobuf

With the Cassandra connector you can leverage Schema information in order to manage the contents of the topics and write them to Cassandra.

You can also map an existing Pulsar schema to the schema of the Cassandra table---even when they don't match perfectly---by:

* selecting only a subset of fields
* selecting fields of inner structures in the case of nested types
* applying simple data transformations
* handling Cassandra TTL and TIMESTAMP properties

From Kafka Connect Sink to Pulsar IO Sink {#h2-1-from-kafka-connect-sink-to-pulsar-io-sink}
-------------------------------------------------------------------------------------------

The Apache Kafka Connect framework is very similar to Apache Pulsar IO in terms of APIs.

The purpose of a Sink is to write to an external system (in this case Cassandra) the data that is coming from the stream.

In order to leverage all of the existing features of the Kafka Cassandra Sink and keep the two projects on feature parity going forward, we created an abstraction over the two systems: the [Messaging Connectors Commons framework](https://github.com/datastax/messaging-connectors-commons).

<br />

The Messaging Connectors Commons framework implements:

* An abstraction over Kafka Connect and Pulsar IO APIs (Record and Schema APIs)
* Cassandra Client Driver handling (configuration, security, and compatibility with a big matrix of Cassandra, DataStax DSE, and Astra)
* Mapping and Data transformations
* Failure handling
* Multi-topic management

We created this framework by extracting all of the common business logic from the [Kafka Sink](https://github.com/datastax/kafka-sink).

After this pure engineering work, the road to building the Pulsar Sink was straightforward: we only had to implement the relevant APIs within the Pulsar Sink framework.

Differences between Kafka Connect and Pulsar IO {#h2-2-differences-between-kafka-connect-and-pulsar-io}
-------------------------------------------------------------------------------------------------------

Even if at first glance the APIs are very similar, the Pulsar IO framework offers a slightly different model, with some cool features and some tradeoffs.

### Single Record Acknowledgement {#h3-3-single-record-acknowledgement}

In Kafka you are exposed to the partitioned nature of a topic and you also have to manually track the offset you have reached, with one value for each partition.

In Pulsar, you process each record independently and concurrently, then you finish by acknowledging the processing or by calling a negative acknowledgement in order to tell Pulsar that the processing failed locally and the record should be reprocessed. This way the framework can handle dispatching records to the various instances of the Sink and it saves the Sink from having to handle all of the failure-tracking work.

In Pulsar, the Sink receives only one record at a time, but with the single acknowledgement feature, you are free to implement your own batching strategies; in Kafka the batching is done at the framework level so you do not have control over the amount of records to process at one time.

### Structured Data into the Key {#h3-4-structured-data-into-the-key}

In Pulsar the key of a Record is always a String and it cannot contain structured data. But with the Cassandra sink you can interpret the String value as a JSON-encoded structure and access the contents of each field and of nested structures.

### Schema API is lacking type information {#h3-5-schema-api-is-lacking-type-information}

The Pulsar Schema API framework also doesn't report type information about generic data structures like Kafka Connect. ([This GitHub Pull Request](https://github.com/apache/pulsar/pull/9614) is open to add that.)

The [Messaging Connectors Commons framework](https://github.com/datastax/messaging-connectors-commons), which deals with mapping message data to the Cassandra model, needs detailed type information in order to transform each field to the correct Cassandra data type.

Because in Pulsar we don't have direct access to this in the Schema, we decided to use **Java Reflection**and to dynamically build the schema. This approach works very well and it supports schema evolution, as it is expected that the data type does not change for a given field.

One problem arises with *null* values, because in Java from a *null* value you cannot know the original data type. In this case, we are initially supposing that the field is of the type String, and we allow it to move from String to another datatype as soon as we see a non-null value.

Sample usage {#h2-6-sample-usage}
---------------------------------

Here's a simple example about how to map your Pulsar Topic to a Cassandra table:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">configs:
 topics: mytopic 
 ignoreErrors: None
 ......
 topic:
   mytopic:
     mykeyspace:
       mytable:
         mapping: 'id=key.id,name=value.name,age=value.age'
         consistencyLevel: LOCAL_ONE
         ttl: -1
         ttlTimeUnit: SECONDS
         timestampTimeUnit: MICROSECONDS
         nullToUnset: true
         deletesEnabled: true
     codec:
       locale: en_US
       timeZone: UTC
       timestamp: CQL_TIMESTAMP
       date: ISO_LOCAL_DATE
       time: ISO_LOCAL_TIME
       unit: MILLISECONDS

</pre>

Here we are reading AVRO or JSON encoded messages from the *mytopic* topic and we are writing to the *mykeyspace.mytable* table on Cassandra. (The connection, security, and performance configurations are omitted in order to focus on the mapping features.)  

You can map multiple topics to multiple tables, and for each pair you can configure a different mapping, selecting fields from the message and binding each field to a column in Cassandra.

You can also store the full payload as a UDT to Cassandra or extract nested inner data structures.

The Sink automatically handles conversions and efficient writing to Cassandra.

Compatibility with Cassandra versions {#h2-7-compatibility-with-cassandra-versions}
-----------------------------------------------------------------------------------

The Cassandra Sink works well with Apache Cassandra, [Datastax Enterprise (DSE)](https://www.datastax.com/products/datastax-enterprise), and [Astra](https://www.datastax.com/products/datastax-astra).

At Datastax we are running tests on CI against a matrix of many different versions of Cassandra and DSE as well as against the cloud Astra service in order to provide 100% compatibility.

Wrapping up {#h2-8-wrapping-up}
-------------------------------

I described how we have been able to refactor the existing Kafka Connect Sink for Apache Cassandra and deliver the same level of features to Pulsar IO users.

[Datastax Pulsar Cassandra sink](https://docs.datastax.com/en/pulsar-connector/1.4/index.html) enables you to connect your Pulsar cluster to Cassandra and it offers lots of flexibility in mapping the source data to Cassandra tables.

We are maintaining a full compatibility matrix against many versions of DSE, Apache Cassandra and DataStax Astra.

To learn more about how it works please check out [our repository on GitHub](https://github.com/datastax/pulsar-sink), and feel free to post your questions on the [issue tracker](https://github.com/datastax/pulsar-sink/issues).

**Want to try out Apache Pulsar?** [Sign up now](https://astra.datastax.com/register) for [Astra Streaming](https://www.datastax.com/products/astra-streaming), our fully managed Apache Pulsar service. We'll give you access to its full capabilities entirely free through beta. See for yourself how easy it is to build modern data applications and let us know what you'd like to see to make your experience even better.
