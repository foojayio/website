---
title: "Ingesting Data from Kafka into the QuestDB time-series database"
date: "2023-03-28T09:38:35+00:00"
lastmod: "2023-03-28T09:39:34+00:00"
description: "Learn how to Ingest streaming time-series data from Apache Kafka into QuestDB using ILP with the Kafka Sink Connector."
authors:
  - "javier-ramirez"
image: "LfxaX-nD_400x400.jpeg"
categories:
  - "Databases"
  - "DataEngineering"
  - "Kafka"
  - "sql"
  - "Streaming"
tags:
related_posts:
  - "getting-more-mileage-out-of-kafka-openjdk-vs-azul-prime"
  - "how-does-kafka-perform-when-you-need-low-latency"
  - "how-to-reduce-cloud-cost-by-99-for-eda-kafka-applications"
  - "how-to-create-a-failover-client-using-the-hazelcast-viridian-serverless"
frozen: false
---

If you are working on a project with fast or streaming data, chances are Apache Kafka is already [part of your pipeline](https://kafka.apache.org/powered-by#:~:text=Today%2C%20Kafka%20is%20used%20by,80%25%20of%20the%20Fortune%20100.).

But if you want to analyse your data, you will need to ingest from Kafka into some destination.

For time-series data, QuestDB can be an excellent choice.

For those of you who are not yet familiar with [QuestDB](https://github.com/questdb/questdb "QuestDB"), it is an Apache 2.0 licensed database designed for high throughput ingestion and fast SQL queries.

You could write your own application, or use the generic JDBC Kafka connector to consume from Kafka and ingest into QuestDB, but the most convenient option is to use the native [QuestDB Kafka Connector](https://github.com/questdb/kafka-questdb-connector).

Let's see how the integration works.

## Requirements

Make sure you already have:

* A [Kafka installation](https://kafka.apache.org/documentation/#quickstart%22)
* A running QuestDB (for example, 

```
docker run --add-host=host.docker.internal:host-gateway -p 9000:9000 -p 9009:9009 -p 8812:8812 -p 9003:9003 questdb/questdb:latest
```

  )
* A local [JDK installation](https://foojay.io/java-quick-start/install-java/)

## Adding the QuestDB Sink Connector to Kafka

The Apache Kafka distribution includes the Kafka Connect framework, but the QuestDB-specific zip file must be downloaded from the [QuestDB Kafka connector GH page](https://github.com/questdb/kafka-questdb-connector/releases/tag/v0.6).

If you prefer it, the connector is also available via the [Confluent Hub](https://www.confluent.io/hub/questdb/kafka-questdb-connector).

Once downloaded you need to unzip it and move it into your Kafka libs directory. For example:

```bash
unzip kafka-questdb-connector-*-bin.zip 
cd kafka-questdb-connector 
cp ./*.jar /path/to/kafka_2.13-2.6.0/libs
```

```

```

## Configuration

The connector reads messages from a topic in Kafka, and writes them to a table in QuestDB using the ILP protocol, so the minimum configuration you need is the topic name, QuestDB host and port, and the table name.

There are some [extra options](https://questdb.io/docs/third-party-tools/kafka/questdb-kafka/#configuration-options) you could configure for serialisation and authentication.

A configuration file `/path/to/kafka/config/questdb-connector.properties` must be created for Kafka Connect in the standalone mode. A basic config file could look like this:

```
name=questdb-sink 
connector.class=io.questdb.kafka.QuestDBSinkConnector 
host=localhost:9009 
topics=example-topic 
table=example_table 
include.key=false 
value.converter=org.apache.kafka.connect.json.JsonConverter 
value.converter.schemas.enable=false 
key.converter=org.apache.kafka.connect.storage.StringConverter
```

## Start Kafka

Go to the Kafka home directory and start Zookeeper

```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
```

Then start Kafka itself

```bash
bin/kafka-server-start.sh config/server.properties
```

And let's start the QuestDB connector, pointing to the config file we just created

```bash
bin/connect-standalone.sh config/connect-standalone.properties config/questdb-connector.properties
```

## Publish some events

We are going to start and interactive Kafka console producer, so you can post some messages into a topic. Just make sure the topic name is the one we have in the configuration file.

```bash
bin/kafka-console-producer.sh --topic example-topic --bootstrap-server localhost:9092
```

When the shell starts, you are ready to send JSON messages. You might have noticed that we didn't define a table structure for our output.

QuestDB will automatically create the table from the first message we insert, and if we add new fields in later messages, the table will be updated automatically.

In any case, even though QuestDB offers some flexibility with automatic schemas, it is not a schemaless database, so all the messages must have a compatible structure.

As an example, paste this JSON as a single line into the Kafka producer shell and hit enter:

```json
{"firstname": "Arthur", "lastname": "Dent", "age": 42}
```

```

```

You can send one or two more messages, maybe add an extra field to one of them just for fun.

Note: If you preferred, you could have created your table beforehand in QuestDB issuing a [CREATE TABLE](https://questdb.io/docs/reference/sql/create-table/) statement

## Querying your time-series data

If all went well, your data has already been stored in QuestDB and can be queried using SQL either via a [Postgresql-compatible library](https://questdb.io/docs/develop/query-data/#postgresql-wire-protocol), or the handy [REST API](https://questdb.io/docs/develop/query-data/#http-rest-api).

Or you can simply go to the QuestDB web console, which runs by default at <http://localhost:9000>.

Run the following SQL query and you should see as many rows as messages you sent to your Kafka topic.

Make sure the table name matches the one you used in the configuration file.

```sql
SELECT * FROM example_table
```

## That's a wrap

As you can see, consuming messages from Kafka and into QuestDB is quite painless using the built-in connector.

For more complete examples, including Change Data Capture from Postgresql into QuestDB using Kafka and Debezium, check out the [official repository](https://github.com/questdb/kafka-questdb-connector/tree/main/kafka-questdb-connector-samples).
