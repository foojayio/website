---
title: "How to Get Started with the Hazelcast Viridian Serverless"
date: "2023-03-10T10:28:45+00:00"
lastmod: "2023-03-10T11:16:47+00:00"
description: "Quickly learn how to connect Hazelcast Viridian to a Confluent Cloud Kafka cluster, and more!"
authors:
  - "fawaz-ghali"
image: "hazelcast.png"
categories:
  - "Hazelcast"
  - "sql"
  - "Streaming"
  - "Tutorials"
related_posts:
  - "how-to-get-started-with-the-hazelcast-viridian-serverless"
  - "announcing-real-time-stream-processing-unconference"
  - "backpressure-in-reactive-systems"
  - "real-time-stream-processing-with-hazelcast-and-streamnative"
frozen: false
---

In this tutorial, you'll learn how to build real-time streaming applications with the Hazelcast Viridian Serverless using SQL.

You'll also learn how to build a materialized view from streaming data and contextual data in Hazelcast.

Streaming data is data that is continuously generated in small sizes.

Streaming data includes a wide variety of sources such as retail purchases, financial trades, or telemetry from connected vehicles.

This data isn't usually useful by itself because of its size.

It must be enriched with contextual data that is often stored in a database.

Hazelcast allows you to connect to streaming data sources such as Confluent Cloud Kafka clusters as well as cache contextual data in your Hazelcast cluster.

With access to both of these data sources, you can process your data in one place and store the results in a materialized view.

Materialized views are useful for speeding up queries that are repeated.

Instead of performing resource-intensive queries against large datasets in different sources, your applications can query a materialized view and retrieve a precomputed result.

## Before you Begin

You need the following:

* A Hazelcast Viridian cluster. This tutorial assumes that you have a production cluster: <https://docs.hazelcast.com/cloud/create-serverless-cluster>
* A basic understanding of Kafka: <https://hazelcast.com/glossary/kafka/>

## Step 1. Set Up the Hazelcast CLI

You need to use the Hazelcast CLI in later steps to open connections to the SQL shell of your Viridian cluster: <https://docs.hazelcast.com/hazelcast/latest/getting-started/get-started-cli>

Next, you need to add the credentials that allow the Hazelcast CLI to connect to your cluster.

1. Sign into the Hazelcast Viridian console: <https://viridian.hazelcast.com/>
2. Select the production cluster that you want to use for this tutorial.
3. In **Cluster Details** , click **Connect Client**.
4. In the **Quick Connection Guide** , click the SQL icon and then click **Download**.
5. Extract the ZIP file and copy all the files into the root directory where Hazelcast Enterprise is installed.
6. From a command prompt, still in the root directory, execute one of the following commands to connect to your cluster.

```
bin/hz-cli -f hazelcast-client-with-ssl.yml sql
```

## Step 2. Create a Free Confluent Cloud Kafka Cluster

1. Create a Confluent Cloud account: <https://confluent.cloud/signup>
2. Create a Basic cluster.
3. Select the same cloud provider as your Viridian cluster and the region that's closest to your Hazelcast cluster. For example, AWS Oregon (us-west-2).
4. Skip payment and launch the cluster.
5. Click your cluster's name in the breadcrumbs at the top of the page.
6. Click **Topics** \> **Create Topic** and enter **trades** in the **Topic name** field.
7. Click **Create with defaults**. Confluent Cloud won't create a topic if you try to insert data into a topic that doesn't exist.
8. Click **Clients** and select **Java**. Hazelcast uses a Java client to connect to Kafka clusters, so you need the configuration for a Java client.
9. Click **Create Kafka cluster API key** and enter the name of your Viridian cluster in the description. This name helps you remember that your cluster is using that API key the next time you view them in Confluent Cloud.
10. Click **Download and continue**. The configuration snippet now includes your API key and secret.
11. Copy the code in your configuration snippet from the top to session.timeout.ms=45000. You won't use the Schema Registry in this tutorial.

## Step 3. Create a Mapping to the Confluent Cloud Cluster

To allow Hazelcast to access the trades topic that you created in your Confluent Cloud Kafka cluster, you need to create a mapping to it.

1. Sign into the Hazelcast Viridian console and select your cluster.
2. Go to **SQL** in the left navigation to open the SQL browser.
3. Create the mapping. Paste the connection configurations that you copied from Confluent Cloud below the valueFormat option. Make sure to format the configuration as necessary. For example:

```
-- Create a mapping to a Kafka topic called 'trades'.
CREATE OR REPLACE MAPPING trades (
  id BIGINT,
  ticker VARCHAR,
  price_usd DECIMAL,
  amount BIGINT)
TYPE Kafka
OPTIONS (
  -- Serialization format
  'valueFormat' = 'json-flat',
  -- Required connection configs for Kafka producer, consumer, and admin
  'bootstrap.servers'='<YOUR BOOTSTRAP SERVER>',
  'security.protocol'='SASL_SSL',
  'sasl.jaas.config'='org.apache.kafka.common.security.plain.PlainLoginModule
  required username="<YOUR API KEY>"
  password="<YOUR API SECRET>";',
  'sasl.mechanism'='PLAIN',
  --Required for correctness in Apache Kafka clients prior to 2.6
  'client.dns.lookup'='use_all_dns_ips',
  -- Best practice for higher availability in Apache Kafka clients prior to 3.0
  'session.timeout.ms'='45000',
  'auto.offset.reset'='earliest'
);
```

The trades topic accepts trades in JSON format, using the following schema:  

```
{
  "id": ,
  "ticker": ,
  "price_usd": ,
  "amount": ,
}
```

* Publish some new trades to the topic.

```
INSERT INTO trades VALUES
  (1, 'SORG', 5.5, 10),
  (2, 'EORG', 14, 20);
```

* If you haven't started the SQL prompt on your Viridian cluster, do it now:

```
hz-cli -f hazelcast-client-with-ssl.yml sql
```

* In the SQL prompt, write a streaming query that filters trade messages, where the total trade order is more than $100.

```
SELECT ticker, price_usd, amount
  FROM trades
  WHERE price_usd * amount > 100;
```

* Stop the streaming query by pressing Ctrl+C to close the connection to the SQL prompt.
* Back in the SQL browser, create the mapping to the topic again, but this time add the 'auto.offset.reset'='earliest' configuration. This configuration tells the Kafka consumer to read all data in the topic from the beginning, not just from the latest offset.

```
-- Create a mapping to a Kafka topic called 'trades'.
CREATE OR REPLACE MAPPING trades (
  id BIGINT,
  ticker VARCHAR,
  price_usd DECIMAL,
  amount BIGINT)
TYPE Kafka
OPTIONS (
  -- Serialization format
  'valueFormat' = 'json-flat',
  -- Required connection configs for Kafka producer, consumer, and admin
  'bootstrap.servers'='<YOUR BOOTSTRAP SERVER>',
  'security.protocol'='SASL_SSL',
  'sasl.jaas.config'='org.apache.kafka.common.security.plain.PlainLoginModule
  required username="<YOUR API KEY>"
  password="<YOUR API SECRET>";',
  'sasl.mechanism'='PLAIN',
  --Required for correctness in Apache Kafka clients prior to 2.6
  'client.dns.lookup'='use_all_dns_ips',
  -- Best practice for higher availability in Apache Kafka clients prior to 3.0
  'session.timeout.ms'='45000',
  'auto.offset.reset'='earliest'
);
```

* In the SQL prompt, enter the same streaming query that gave no results the last time you ran it.

```
SELECT ticker, price_usd, amount
    FROM trades
  WHERE price_usd * amount > 100;
```

## Step 4. Enrich the Data in the Kafka Messages

To reduce network latency, Kafka messages are often small and contain minimal data.

For example, the trades topic does not contain any information about the company that's associated with a given ticker.

To get deeper insights from data in Kafka topics, you can join query results with contextual data.

* Open the SQL browser.
* Create a mapping to a new map called companies in Hazelcast. The new map is for storing the company information that you'll use to enrich results from the trades topic.

```
CREATE MAPPING companies (
__key BIGINT,
ticker VARCHAR,
company VARCHAR,
marketcap BIGINT)
TYPE IMap
OPTIONS (
'keyFormat'='bigint',
'valueFormat'='json-flat');
```

* Add some entries to the companies map.

```
INSERT INTO companies VALUES
(1, 'SORG', 'Example Startup Organization', 100000),
(2, 'EORG', 'Example Enterprise Organization', 5000000);
```

* Merge results from the companies map and trades topic so you can see the company name that's associated with each ticker.

```
SELECT trades.ticker, companies.company, trades.amount
FROM trades
JOIN companies
ON companies.ticker = trades.ticker;
```

## Step 5. Create a Materialized View

You can set up an automated job to continuously run the streaming query and cache the results in a Hazelcast map.

* Open the SQL browser.
* Create a mapping to a new map called trade_map. This map is your materialized view, which caches the enriched results of the streaming query.

```
CREATE MAPPING trade_map (
__key BIGINT,
ticker VARCHAR,
company VARCHAR,
amount BIGINT)
TYPE IMap
OPTIONS (
'keyFormat'='bigint',
'valueFormat'='json-flat');
```

* Submit a job to your cluster that will monitor your trade topic for changes and store them in a map. The processing guarantee tells Hazelcast to save the current offsets so that the cluster can resume the job even if the cluster restarts.

```
CREATE JOB ingest_trades
OPTIONS (
  'processingGuarantee' = 'exactlyOnce'
) AS
SINK INTO trade_map
SELECT trades.id, trades.ticker, companies.company, trades.amount
FROM trades
JOIN companies
ON companies.ticker = trades.ticker;
```

* List your job to make sure that it was successfully submitted.

```
SHOW JOBS;
```

* Query your materialized view to see that results have been added to it.

```
SELECT * FROM trade_map;
```

* Publish some more trades to the topic.

```
INSERT INTO trades VALUES
  (3, 'SORG', 5.7, 23),
  (4, 'EORG', 12, 54);
```

* Query your materialized view to see that results have been added to it.

```
SELECT * FROM trade_map;
```

## Summary

You've learned how to connect Hazelcast Viridian to a Confluent Cloud Kafka cluster as well as the following:

* How to query streaming data from a Kafka topic.
* How to enrich streaming data with contextual data and save the results to a materialized view.

Hazelcast allows you to quickly build resource-efficient, real-time applications.

You can deploy it at any scale, from small-edge devices to a large cluster of cloud instances.

A cluster of Hazelcast nodes share both the data storage and computational load, which can dynamically scale up and down.

When you add new nodes to the cluster, the data is automatically rebalanced across the cluster, and currently running computational tasks (known as jobs) snapshot their state and scale with processing guarantees.
