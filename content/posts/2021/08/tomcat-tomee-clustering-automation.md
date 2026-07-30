---
title: "Tomcat and TomEE Clustering Automation in the Cloud"
slug: "tomcat-tomee-clustering-automation"
date: "2021-08-30T07:18:36+00:00"
lastmod: "2021-09-16T14:46:22+00:00"
description: "Find out about Tomcat and TomEE auto-clustering in Jelastic PaaS, as well as how to get these scalable clusters up and running!"
authors:
  - "tetiana-fydorenchyk"
image: "/images/posts/2021/08/tomcat-tomee-clustering-automation/image3.png"
categories:
  - "Jakarta EE"
  - "Jelastic"
  - "Tutorials"
tags:
related_posts:
enlighterjs: true
frozen: false
---

<figure class="alignleft">
 <img decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2021/08/image11.png" alt="Tomcat TomEE Automatic Clustering">
</figure>

Apache [Tomcat](https://tomcat.apache.org/) is an open-source application server maintained by the Apache community. It is one of the most popular solutions for hosting Java applications due to its ease-of-use and lightweight yet versatile functionality. However, if you run the projects based on [Jakarta EE](https://jakarta.ee/) 8 (or higher), you may be interested to use [TomEE](https://tomee.apache.org/) server with built-in required enterprise technology that isn't found in Tomcat.

Due to the extreme popularity of these stacks globally, and within Jelastic PaaS in particular, we've decided to share tips on how to install automatically clustered Tomcat and TomEE servers to get a highly available solution that can efficiently serve a large number of users, process high traffic, and be reliable.

Before starting the Tomcat cluster automation package development, the Jelastic team gathered requirements that this solution should achieve, or, as we call them, the challenges:

* Efficient processing of a large number of requests (performance)
* Horizontal scalability support in case of workload growth
* Zero downtime in case of failures (fault tolerance)

Now, with these goals in mind, we've analyzed numerous successful examples over the Internet to create a solution that can be seamlessly integrated into the platform.

Below, you can find out more about the implementation of Tomcat and TomEE auto-clustering in Jelastic PaaS, as well as how to get these clusters up and running.

Tomcat / TomEE Auto-Cluster Installation {#h2-0-tomcat-tomee-auto-cluster-installation}
---------------------------------------------------------------------------------------

There are multiple tutorials on how to set up Tomcat clusters, but the configuration is relatively complex and includes many tweaks, which makes it seem like a lot. However, with the Jelastic [Auto-Clustering](https://docs.jelastic.com/what-is-auto-clustering/) feature, we went further and automated the whole process, making it as simple as flipping a switch.

1. Log in to your Jelastic account and click **New Environment** at the top.
![Tomcat and TomEE environments](https://jelastic.com/blog/wp-content/uploads/2021/08/image6.png)

2. In the open [topology wizard](https://docs.jelastic.com/setting-up-environment/), choose **Tomcat** or **TomEE** among **Java** application servers and turn on the **Auto-Clustering** switcher. Make any other necessary adjustments and click **Create**.

<figure class="wp-block-image is-resized">
 <img fetchpriority="high" decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2021/08/image4.png" alt="tomcat environment" class="wp-image-54787" width="695" height="433">
</figure>

3. In a few minutes, your Tomcat cluster will appear within the dashboard.
![tomcat cluster topology](https://jelastic.com/blog/wp-content/uploads/2021/08/image10.png)

Tomcat / TomEE Cluster Topology and Specifics {#h2-1-tomcat-tomee-cluster-topology-and-specifics}
-------------------------------------------------------------------------------------------------

The topology of the highly available Tomcat cluster is straightforward and efficient, without any unnecessary elements to complicate it or reduce performance. The default cluster topology looks like below:  

<figure class="aligncenter is-resized">
 <img decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2021/08/image2.png" alt="tomee tomcat cluster topology" class="wp-image-54791" width="406" height="490">
</figure>

Such topology ensures that performance and fault tolerance requirements are met. [**NGINX** load balancer](https://docs.jelastic.com/nginx-load-balancer/) in front of the cluster handles workload distribution. Also, it automatically registers any changes in the cluster, reacting to the compute nodes' addition, deletion, or failure. You can follow our [load balancing test](https://docs.jelastic.com/testing-load-balancing/) tutorial to get an evaluation of workload distribution.

In general, the Auto-Clustering creates a simple TCP cluster which connects multiple Tomcat servers via TCP protocol. In our configuration, we use DeltaManager that discovers session changes and transfers these changes to other Tomcat instances in the cluster. Once both the nodes connect to each other, they establish a set of sender and receiver socket channels. The session replication data is transferred using these channels. The implementation provides session replication, context attribute replication, and cluster wide WAR file deployment. Also there are various interceptors configured to intercept datatransfer.

Here are some Tomcat-specific changes and optimizations based on the cluster installation described in the section above:

* After enabling the Auto-Clustering for Tomcat, the [Cluster](https://tomcat.apache.org/tomcat-10.0-doc/config/cluster.html) element is added to the "Engine" section of the **/opt/tomcat/conf/server.xml** configuration file:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;Cluster className="org.apache.catalina.ha.tcp.SimpleTcpCluster" channelSendOptions="6"&gt;</pre>

where:

* **className** defines cluster type outlined above
* **channelSendOptions="6" / channelSendOptions="sync,use_ack"** - The sender will block the completion of the current request until all of the receiving nodes have acknowledged that they have received and processed the message.

You can check and adjust it if necessary via the built-in [Configuration File Manager](https://docs.jelastic.com/configuration-file-manager/).

* The instance itself is added to the configuration as **LocalMember**. Other Tomcat nodes in the layer are added by their IPs and uniqueId, which are generated using the containers' IP addresses and IDs.

<figure class="wp-block-image is-resized">
 <img decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2021/08/image5.png" alt="tomcat cluster configurations" class="wp-image-54793" width="757" height="492">
</figure>

* The DeltaManager is used for replicating sessions. As a result, the session data is spread across all the layer nodes (from all to all).

<figure class="wp-block-image is-resized">
 <img loading="lazy" decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2021/08/image9.png" alt="tomcat session replication" class="wp-image-54795" width="755" height="223">
</figure>

* [Static membership](https://tomcat.apache.org/tomcat-9.0-doc/config/cluster-interceptor.html#Static_Membership) is used for discovering the cluster peers - the whole and exact list of all the cluster members is listed in the ***server.xml*** on every node.

<figure class="wp-block-image is-resized">
 <img loading="lazy" decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2021/08/image7-1024x310.png" alt="tomcat cluster members" class="wp-image-54797" width="756" height="229">
</figure>

* Interconnection is done through port *4004* using the TCP protocol - an appropriate rule is added to the [firewall](https://docs.jelastic.com/custom-firewall/) during the cluster configuration.
* Also, in the ***/opt/tomcat/conf/context.xml*** configuration file, the *[org.apache.catalina.ha.context.ReplicatedContext](https://tomcat.apache.org/tomcat-9.0-doc/config/cluster.html)* class is used for the context implementation to provide replication.

<figure class="wp-block-image is-resized">
 <img loading="lazy" decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2021/08/image1.png" alt="tomcat server replication" class="wp-image-54799" width="760" height="232">
</figure>

All these settings are done automatically during creation and do not require manual maintenance during the cluster lifecycle.

Tomcat / TomEE Cluster Horizontal Scaling {#h2-2-tomcat-tomee-cluster-horizontal-scaling}
-----------------------------------------------------------------------------------------

It's evident that high availability is demanded almost for any application, so it is a great benefit that you can automate cluster nodes' addition/deletion based on the [configurable scaling](https://docs.jelastic.com/automatic-horizontal-scaling/#triggers-for-automatic-scaling)*[](https://docs.jelastic.com/automatic-horizontal-scaling/#triggers-for-automatic-scaling)* [triggers](https://docs.jelastic.com/automatic-horizontal-scaling/#triggers-for-automatic-scaling) to match the variable load.

1. Go to **Settings \> Auto Horizontal Scaling** for your Tomcat / TomEE cluster environment.

<figure class="wp-block-image is-resized">
 <img loading="lazy" decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2021/08/image8-1024x601.png" alt="tomcat horizontal scaling" class="wp-image-54801" width="759" height="445">
</figure>

2. Set up scaling triggers for the required resources (RAM, CPU, Network, Disk).  

Keep in mind that the minimum count of worker nodes in the cluster is 2, so set up the **Scale in** limit respectively as in the picture above.

So, if you are looking for a reliable Tomcat hosting to keep your projects scalable and highly-available, Jelastic auto-clustering is an excellent solution to hop on with ease.

No need to struggle from routine and complex configuration tasks, just let Jelastic do all the work for you and get a guaranteed result in a matter of minutes.

Try it yourself on one of [Jelastic PaaS providers](https://jelastic.cloud/).
