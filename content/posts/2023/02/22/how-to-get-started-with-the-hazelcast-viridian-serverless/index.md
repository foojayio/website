---
title: "How to Get Started with the Hazelcast Viridian Serverless"
date: "2023-02-22T08:13:32+00:00"
lastmod: "2023-03-10T10:08:36+00:00"
description: "Hazelcast Serverless manages your Cloud infrastructure, handling resource isolation, resource stealing, and isolated network access."
authors:
  - "fawaz-ghali"
image: "hz-unconf-social-2.jpg"
categories:
  - "Hazelcast"
  - "Tutorials"
related_posts:
  - "real-time-stream-processing-with-hazelcast-and-streamnative"
  - "hazelcast-kibana-best-buddies-for-exploring-visualizing-data"
  - "hazelcast-from-embedded-to-client-server"
  - "building-real-time-applications-to-process-wikimedia-streams-using-kafka-and-hazelcast"
frozen: false
---

Hazelcast Serverless means that Hazelcast manages your Cloud infrastructure for you.

Each Viridian Serverless cluster is an independent deployment of Hazelcast Platform in a Kubernetes container.

This design guarantees resource isolation, prevents resource stealing, and provides isolated network access.

Viridian Serverless clusters come in two types:1) Development: Capped storage.

You can store at most 1 GiB of data; and 2) Production: Uncapped storage.

The cluster scales as you add or remove data. In this tutorial, you'll learn how to connect a client to a cluster and use SQL to query data in the cluster.

### **Before you Begin**

You need the following:

* A Hazelcast Viridian account: <https://docs.hazelcast.com/cloud/create-account>
* Access to a command prompt such as Terminal for Mac or Powershell for Windows.

### **Step 1. Start a Viridian Serverless Development Cluster**

Development clusters are for fast, iterative development while you prototype your application. To create a development cluster, do the following:

1. Sign into the Hazelcast Viridian console: <https://viridian.hazelcast.com/sign-in?next=/> .
2. Click Create New Cluster.
3. Click Create Development Cluster.
4. Update the cluster name if you want to. You cannot change the cluster name after the cluster is created.
5. Click Create Cluster.
6. Wait while your cluster is created. When the cluster is up and running, a Quick Connection Guide is displayed with instructions for connecting a sample client to it.

### **Step 2. Connect a Sample Client**

To connect to your Viridian Serverless cluster, you need a Hazelcast client. The cluster comes with sample clients that are preconfigured to connect to your cluster and add some sample data to a map. A map is an in-memory, key/value data structure that is often used as a cache.

Before you begin:

* Install Java 8-17 and set the JAVA_HOME environment variable to the location of your JRE.
* If you've followed the onscreen Quick Connection Guide, jump straight to step 6.
  1. In the Quick Connection Guide on your cluster, click on the Java icon and then click Download.
  2. Extract the ZIP file.
  3. From a command prompt, change into the root directory of the extracted files.
  4. Execute one of the following commands to run your client:
     * If you use Linux or Mac, execute:` ./mvnw clean compile exec:java@client-with-ssl`
     * If you use Windows, execute: `mvnw.cmd clean compile exec:java@client-with-ssl`
  5. To query data in the cities map, you need to create a mapping to it. In step 3 of the Quick Connection Guide on your cluster, click Dashboard.
  6. In Cluster Details, click Management Center, and then from the top toolbar click SQL Browser.
  7. Paste the following command into the SQL browser and execute it.

```
__key VARCHAR,

country VARCHAR,

city VARCHAR,

population INT)

type IMap OPTIONS('keyFormat'='varchar', 'valueFormat'='json-flat');
```

### **Step 3. Query the Cache with SQL**

Now that you have some data in your cluster, you can query it, using SQL. If you're using the CLI, enter the following queries in the SQL prompt. If you're using a client library, enter the following queries in the SQL browser.

* Execute the following SELECT statement to query all data in the map.

```
SELECT * FROM cities;
```

The results are in a random order because the data is distributed across the cluster.

* Order the results by the key.

```
SELECT * FROM cities ORDER BY __key;
```

Now you see the results start from key 1 and end with key 8.

* Query only the countries by filtering on the countries column.

```
SELECT country FROM cities;
```

* Query only the cities by filtering on the cities column.

```
SELECT city FROM cities;
```

* Change the output to display cities first in alphabetical order.

```
SELECT city, country FROM cities ORDER BY city;
```

* Use a filter to display only countries where the name of the city is at least 11 characters long.

```
SELECT country FROM cities WHERE LENGTH(city) >= 11;
```

* Use another filter to display only cities beginning with the letter 'L' where the length is greater than 6.

```
SELECT city FROM cities WHERE city LIKE 'L%' AND LENGTH(city) > 6;
```

### **Summary**

In this tutorial, we learnt how to connect to the Hazelcast Viridian Serverless and use SQL to query data. It is possible to connect to the Viridian using Java, Node.js, Python, .NET, C++ and Go.

Once connected, you can manage your cluster through the Management Center. Hazelcast allows you to quickly build resource-efficient, real-time applications.

You can deploy it at any scale from small edge devices to a large cluster of cloud instances.

A cluster of Hazelcast nodes share both the data storage and computational load which can dynamically scale up and down.

When you add new nodes to the cluster, the data is automatically rebalanced across the cluster, and currently running computational tasks (known as jobs) snapshot their state and scale with processing guarantees.

**More about Hazelcast:**

* Join our Real-Time Stream Processing Unconference (#RTSPUnconf): <https://hazelcast.com/lp/unconference/>
* Start a Viridian Serverless Cluster: Serverless is a managed cloud service that offers a pay-as-you-go pricing model. Serverless clusters auto-scale to provide the resources that your application needs. You pay only for the resources that your application consumes: <https://viridian.hazelcast.com/>
* Join the Hazelcast Community Slack: <https://slack.hazelcast.com/>
