---
title: "Enriching Kafka Applications with Contextual Data"
slug: "enriching-kafka-applications-with-contextual-data"
date: "2023-05-18T15:11:22+00:00"
lastmod: "2023-05-18T15:55:42+00:00"
description: "Hazelcast can process real-time and batch data in one platform, making it the right platform to use because it enriches your Kafka apps."
authors:
  - "fawaz-ghali"
image: "https://foojay.io/wp-content/uploads/2023/05/Hazelcast-Kafka.png"
categories:
  - "Hazelcast"
  - "Performance"
  - "Tutorials"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Developing high-performance large-stream processing applications is a challenging task.

Choosing the right tool(s) is crucial to get the job done; as developers, we tend to focus on performance, simplicity, and cost.

However, the cost becomes relatively high if we end up with two or more tools to do the same task.

Simply put, you need to multiply development time, deployment time, and maintenance costs by the number of tools.

Kafka {#h2-0-kafka}
-------------------

Kafka is great for event streaming architectures, continuous data integration (ETL), and messaging systems of record (database).

However, Kafka has some challenges, such as a complex architecture with many moving parts, it can't be embedded, and it's a centralized middleware, just like a database.

Moreover, Kafka does not offer batch processing and all intermediate steps are materialised to disk in Kafka. This leads to enormous disk space usage.

Hazelcast {#h2-1-hazelcast}
---------------------------

Hazelcast is a real-time stream processing platform that can enhance Kafka (and many more sources).

Hazelcast can address Kafka's challenges mentioned above by simplifying deployment and operations with ultra-low latency and a lightweight architecture making it the right tool for edge (restricted) environments.

This article aims to take your Kafka applications to the next level.

Hazelcast can process real-time and batch data in one platform, making it the right platform to use because it enriches your Kafka applications with "context."

![](/images/posts/2023/05/enriching-kafka-applications-with-contextual-data/1683707203791-700x289.png)

Prerequisites {#h2-2-prerequisites}
-----------------------------------

* If you are new to Kafka or you're just getting started, I recommend you start with [Kafka Documentation](https://kafka.apache.org/documentation/)
* If you are new to Hazelcast or you're just getting started, I recommend you start with [Hazelcast Documentation](https://docs.hazelcast.com/home/)
* For Kafka, you need to download Kafka, start the environment, create a topic to store events, write some events to your topic, and finally read these events. Here's a [Kafka Quick Start](https://kafka.apache.org/quickstart).
* For Hazelcast, you can use either the [Platform](https://docs.hazelcast.com/hazelcast/latest/) or the [Cloud](https://docs.hazelcast.com/cloud/overview). I will use a local cluster.

Step 1 {#h2-3-step-1}
---------------------

Start a Hazelcast local cluster: This will run a Hazelcast cluster in client/server mode and an instance of Management Center running on your local network.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">brew tap hazelcast/hz

brew install <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="d1b9b0abb4bdb2b0a2a591e4ffe3ffe2">[email&nbsp;protected]</a>

hz -V

hz start</pre>

To add more members to your cluster, open another terminal window and rerun the start command.

**Optional**: The Management Center is a user interface for managing and monitoring your cluster. It is a handy tool that you can use to check clusters/nodes, memory, and jobs.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">brew tap hazelcast/hz

brew install <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="8be3eaf1eee7e8eaf8ffa6e6eae5eaeceee6eee5ffa6e8eee5ffeef9cbbea5b9a5b8">[email&nbsp;protected]</a>

hz-mc -V

hz-mc start</pre>

We will use the SQL shell, the easiest way to run SQL queries on a cluster. You can use SQL to query data in maps and Kafka topics.

The Results can be sent directly to the client or inserted into maps or Kafka topics. You can do so by running the following command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">bin/hz-cli sql</pre>

We need a Kafka Broker, I'm using a Docker image to run it (on the same cluster/device as my Hazelcast member).

<pre class="EnlighterJSRAW" data-enlighter-language="generic">docker run --name kafka --network hazelcast-network --rm hazelcast/hazelcast-quickstart-kafka</pre>

Step 2 {#h2-4-step-2}
---------------------

Once we have all components up and running, we need to create a Kafka mapping to allow Hazelcast to access messages in the trades topic.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">CREATE MAPPING trades (

    id BIGINT,

    ticker VARCHAR,

    price DECIMAL,

    amount BIGINT)

TYPE Kafka

OPTIONS (

    'valueFormat' = 'json-flat',

    'bootstrap.servers' = '127.0.0.1:9092'

);</pre>

Here, you configure the connector to read JSON values with the following fields:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">{

  "id"

  "ticker"

  "price"

  "amount"

}</pre>

You can write a streaming query to filter messages from Kafka:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">SELECT ticker, ROUND(price * 100) AS price_cents, amount

  FROM trades

  WHERE price * amount &gt; 100;

</pre>

This will return an empty table, we need to insert some data:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">INSERT INTO trades VALUES

  (1, 'ABCD', 5.5, 10),

  (2, 'EFGH', 14, 20);</pre>

Go back to the terminal where you created the streaming query.

You should see that Hazelcast has executed the query and filtered the results.

Step 3 {#h2-5-step-3}
---------------------

While the previous step is possible to execute with Kafka only, this step will enrich the data in Kafka message, taking your Kafka processing to the next step. Kafka messages are often small and contain minimal data to reduce network latency. For example, the trades topic does not contain any information about the company that's associated with a given ticker.

To get deeper insights from data in Kafka topics, you can join query results with data in other mappings. In order to do this, we need to create a mapping to a new map in which to store the company information that you'll use to enrich results from the trades topic. Then we need to add some entries to the companies map.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">CREATE MAPPING companies (

__key BIGINT,

ticker VARCHAR,

company VARCHAR,

marketcap BIGINT)

TYPE IMap

OPTIONS (

'keyFormat'='bigint',

'valueFormat'='json-flat');

INSERT INTO companies VALUES

(1, 'ABCD', 'The ABCD', 100000),

(2, 'EFGH', 'The EFGH', 5000000);</pre>

Use the JOIN clause to merge results from the companies map and trades topic so you can see which companies are being traded.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">SELECT trades.ticker, companies.company, trades.amount

FROM trades

JOIN companies

ON companies.ticker = trades.ticker;</pre>

In another SQL shell, publish some messages to the trades topic.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">INSERT INTO trades VALUES

  (1, 'ABCD', 5.5, 10),

  (2, 'EFGH', 14, 20);</pre>

Go back to the terminal where you created the streaming query that merges results from the companies map and trades topic.

Step 4 {#h2-6-step-4}
---------------------

Finally, we will ingest query results into a Hazelcast map. We create a mapping to a new map in which to ingest your streaming query results.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">CREATE MAPPING trade_map (

__key BIGINT,

ticker VARCHAR,

company VARCHAR,

amount BIGINT)

TYPE IMap

OPTIONS (

'keyFormat'='bigint',

'valueFormat'='json-flat');</pre>

Submit a streaming job to your cluster that will monitor your trade topic for changes and store them in a map, you can check running jobs by running SHOW JOBS;

<pre class="EnlighterJSRAW" data-enlighter-language="generic">CREATE JOB ingest_trades AS

SINK INTO trade_map

SELECT trades.id, trades.ticker, companies.company, trades.amount

FROM trades

JOIN companies

ON companies.ticker = trades.ticker;

INSERT INTO trades VALUES

(1, 'ABCD', 5.5, 10),

(2, 'EFGH', 14, 20);

</pre>

Now you can query your trade_map map to see that the Kafka messages have been added to it.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">SELECT * FROM trade_map;</pre>

The following diagram explains our demo setup; we have a Kafka topic called trades which contains a collection of trades that will be ingested into a Hazelcast cluster.

<img fetchpriority="high" decoding="async" class="alignnone size-medium wp-image-98378" src="/images/posts/2023/05/enriching-kafka-applications-with-contextual-data/Hazelcast-Kafka-700x289.png" alt="" width="700" height="289">

<br />

Additionally, a companies map represents companies' data stored in the Hazelcast cluster.

We create a new map by aggregating trades and companies into ingest_trades map.

We used SQL but you can send results to a web server/client.

So here you have it, Hazelcast can be used to enrich Kafka applications with contextual data, this can be done programmatically, using the command line, or through SQL as demonstrated in this article.

Hazelcast can process real-time data and batch data in one platform, making it the right platform to use with Kafka applications by providing "context" to your Kafka applications.

We are looking forward to your feedback and comments about this article.

Don't hesitate to share your experience with us in our community [Slack](https://slack.hazelcast.com/) or [Github](https://github.com/hazelcast) repository.
