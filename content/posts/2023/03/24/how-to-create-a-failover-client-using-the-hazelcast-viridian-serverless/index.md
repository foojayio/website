---
title: "Failover Client with the Hazelcast Viridian Serverless"
slug: "how-to-create-a-failover-client-using-the-hazelcast-viridian-serverless"
date: "2023-03-24T14:55:42+00:00"
lastmod: "2023-03-24T15:05:49+00:00"
description: "Learn to update a Java client to automatically connect to a secondary, failover cluster if it cannot connect to its original, primary cluster."
authors:
  - "fawaz-ghali"
image: "failover.png"
categories:
  - "Cloud"
  - "Hazelcast"
  - "Performance"
  - "Streaming"
tags:
related_posts:
  - "how-to-get-started-with-the-hazelcast-viridian-serverless"
  - "streaming-real-time-data-on-the-hazelcast-viridian-serverless"
  - "real-time-stream-processing-with-hazelcast-and-streamnative"
  - "ingesting-time-series-events-from-kafka-into-questdb"
frozen: false
---

Failover is an important feature of systems that rely on near-constant availability.

In Hazelcast, a failover client automatically redirects its traffic to a secondary cluster when the client cannot connect to the primary cluster.

Consider using a failover client with WAN replication as part of your disaster recovery strategy.

In this tutorial, you'll update the code in a Java client to automatically connect to a secondary, failover cluster if it cannot connect to its original, primary cluster.

You'll also run a simple test to make sure that your configuration is correct and then adjust it to include exception handling.

You'll learn how to collect all the resources that you need to create a failover client for a primary and secondary cluster, create a failover client based on the sample Java client, test failover and add exception handling for operations.

## Step 1. Set Up Clusters and Clients

Create two Viridian Serverless clusters that you'll use as your primary and secondary clusters and then download and connect sample Java clients to them.

1. Create the Viridian Serverless cluster (<https://viridian.hazelcast.com/>) that you'll use as your primary cluster. When the cluster is ready to use, the **Quick Connection Guide** is displayed.
2. Select the Java icon and follow the on-screen instructions to download, extract, and connect the preconfigured Java client to your primary cluster.
3. Create the Viridian Serverless cluster that you'll use as your secondary cluster.
4. Follow the instructions in the **Quick Connection Guide** to download, extract, and connect the preconfigured Java client to your secondary cluster.

You now have two running clusters, and you've checked that both Java clients can connect.

## Step 2. Configure a Failover Client

To create a failover client, update the configuration and code of the Java client for your primary cluster.

Start by adding the keystore files from the Java client of your secondary cluster.

1. Go to the directory where you extracted the Java client for your secondary cluster and then navigate to `src/main/resources`.
2. Rename the `client.keystore` file to `client2.keystore` and rename the `client.truststore` file to `client2.truststore` to avoid overwriting the files in your primary cluster keystore.
3. Copy both files over to the `src/main/resources` directory of your primary cluster.

Update the code in the Java client (`ClientwithSsl.java`) of your primary cluster to include a failover class and the connection details for your secondary cluster.

You can find these connection details in the Java client of your secondary cluster.

1. Go to the directory where you extracted the Java client for your primary cluster and then navigate to `src/main/java/com/hazelcast/cloud/`.
2. Open the Java client (`ClientwithSsl.java`) and make the following updates. An example failover client is also available for [download](https://github.com/hazelcast-guides/client-failover/blob/master/docs/modules/client-failover/examples/ClientWithSsl.java).

## Step 3. Verify Failover

Check that your failover client automatically connects to the secondary cluster when your primary cluster is stopped.

1. Make sure that both Viridian Serverless clusters are running.
2. Connect your failover client to the primary cluster in the same way as you did in Step 1.
3. Stop your primary cluster. From the dashboard of your primary cluster, in **Cluster Details** , select **Pause** . In the console, you'll see the following messages in order as the client disconnects from your primary cluster and reconnects to the secondary cluster:
   * `CLIENT_DISCONNECTED`
   * `CLIENT_CONNECTED`
   * `CLIENT_CHANGED_CLUSTER`

If you're using the `nonStopMapExample` in the sample Java client, your client stops.

This is expected because write operations are not retryable when a cluster is disconnected.

The client has sent a put request to the cluster but has not received a response, and so the result of the request is unknown.

To prevent the client from overwriting more recent write operations, this write operation is stopped and an exception is thrown.

## Step 4. Exception Handling

Update the `nonStopMapExample()` function in your failover client to trap the exception that is thrown when the primary cluster disconnects.

1. Add the following try-catch block to the `while` loop in the `nonStopMapExample()` function. This code replaces the original `map.put()` function.

```
try {
    map.put("key-" + randomKey, "value-" + randomKey);
} catch (Exception e) {
    // Captures exception from disconnected client
    e.printStackTrace();
}
```

2. Verify your code again (repeat Step 3.). This time the client continues to write map entries after it connects to the secondary cluster.

Failover is an important feature of systems that rely on near-constant availability.

In Hazelcast, a failover client automatically redirects its traffic to a secondary cluster when the client cannot connect to the primary cluster.

Consider using a failover client with WAN replication as part of your disaster recovery strategy.

In this tutorial, you've updated the code in a Java client to automatically connect to a secondary, failover cluster if it cannot connect to its original, primary cluster.

You've also run a simple test to make sure that your configuration is correct and then adjust it to include exception handling.

You've learn how to collect all the resources that you need to create a failover client for a primary and secondary cluster, create a failover client based on the sample Java client, test failover and add exception handling for operations.
