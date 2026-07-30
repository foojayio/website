---
title: "Five Apache Projects You Probably Haven't Heard Of (Yet)"
slug: "five-apache-projects-you-probably-havent-heard-of-yet"
date: "2023-12-24T10:17:38+00:00"
lastmod: "2023-12-24T19:15:05+00:00"
description: "In this article, I'd like to introduce some Apache projects that are not so well-known."
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2023/12/2560px-Apache_Software_Foundation_Logo.svg.png"
categories:
  - "DevOps"
tags:
related_posts:
  - "5-great-reasons-to-use-jooq"
  - "7-reasons-to-choose-apache-pulsar-over-apache-kafka"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "sboms-and-software-composition-analysis"
frozen: false
---

**In early 2021, I started to work on the [Apache APISIX](https://apisix.apache.org/) project. I have to admit that at the time I had never heard about it before.**

As a result, in this article, I'd like to introduce some Apache projects that are less well-known than HTTPD or Kafka.

Apache APISIX {#h2-0-apache-apisix}
-----------------------------------

<img fetchpriority="high" decoding="async" class="alignright wp-image-103491 size-full" src="/images/posts/2023/12/five-apache-projects-you-probably-havent-heard-of-yet/apisix.png" alt="" width="256" height="256">

[APISIX](https://apisix.apache.org/) is an [API Gateway](https://en.wikipedia.org/wiki/API_management). It builds upon [OpenResty](https://openresty.org/en/), a Lua layer built on top of the famous [nginx](https://nginx.org/) reverse-proxy. APISIX adds abstractions to the mix, *e.g.* , `Route`, `Service`, `Upstream`, and offers a plugin-based architecture.

<br />

Lots of plugins are provided out of the box:

* Transformation: `response-rewrite`, `proxy-rewrite`, gRPC, `body-transformer`, etc.
* Authentication: JWT, OPA, Keycloak, OpenID Connect, etc.
* Observability: metrics, logging, and traces
* Traffic: rate limiting, request validation, canary release, etc.
* Serverless: Azure functions, AWS Lambdas, OpenWhisk, etc.
* Messaging: Kafka, Dubbo, and MQTT
* Pre- and post-processing

If no plugin fits your requirements, writing your own is possible.

You can leverage APISIX on Kubernetes as an Ingress Controller. APISIX provides a Helm Chart for this.

[![](/images/posts/2023/12/five-apache-projects-you-probably-havent-heard-of-yet/flow-software-architecture-1024x438.png)](/images/posts/2023/12/five-apache-projects-you-probably-havent-heard-of-yet/flow-software-architecture.png) {#h2-1-}
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Apache ShardingSphere {#h2-2-apache-shardingsphere}
---------------------------------------------------

<img decoding="async" class="alignright wp-image-103494 size-full" src="/images/posts/2023/12/five-apache-projects-you-probably-havent-heard-of-yet/shardingSphere.png" alt="" width="256" height="170">

[ShardingSphere](https://shardingsphere.apache.org/) claims to offer an ecosystem able to transform any database into a distributed database system. It acts as a proxy between your code and your database(s). It comes in two flavors:

<br />

* ShardingSphere-JDBC: a JDBC driver that acts as a proxy to your database(s). It's only available for JVM-based applications.
* ShardingSphere-Proxy: a technology-independent deployable component.

ShardingSphere offers several core features:

* Data Sharding is the core feature, as the project's name implies. Most use cases focus on scaling purposes, but there are others, *e.g.* , [data residency](https://blog.frankel.ch/data-residency/) requirements.
* XA transactions for distributed transactions
* Read/write splitting
* Data encryption
* etc.

Apache SeaTunnel {#h2-3-apache-seatunnel}
-----------------------------------------

<img loading="lazy" decoding="async" class="alignright wp-image-103495 size-full" src="/images/posts/2023/12/five-apache-projects-you-probably-havent-heard-of-yet/seatunnel.png" alt="" width="256" height="275">

[Apache SeaTunnel](https://seatunnel.apache.org/) is a data integration platform that offers the three pillars of data pipelines: sources, transforms, and sinks. It offers an abstract API over three possible engines: the Zeta engine from SeaTunnel or a wrapper around [Apache Spark](https://spark.apache.org/) or [Apache Flink](https://flink.apache.org/). Be careful, as each engine comes with its own set of features.

<br />

The power of SeaTunnel comes from its rich connector ecosystem. It does provide traditional SQL connectors, *e.g.* , Oracle, PostgreSQL, and MySQL, and NoSQL ones, *e.g.* , MongoDB, Cassandra, and Elasticsearch. However, it also comes bundled with some original ones, including Jira, Google Sheets, and Notion. I have a particular fondness for the connector sources over MongoDB, MySQL, and Microsoft SQL Server.

[](/images/posts/2023/12/five-apache-projects-you-probably-havent-heard-of-yet/seatunnel-architecture.png)

[

<img loading="lazy" decoding="async" class="alignleft size-full wp-image-103496" src="/images/posts/2023/12/five-apache-projects-you-probably-havent-heard-of-yet/seatunnel-architecture.png" alt="" width="946" height="593">

](/images/posts/2023/12/five-apache-projects-you-probably-havent-heard-of-yet/seatunnel-architecture.png)

<br />

SeaTunnel comes with a web UI, which provides visual management of jobs, scheduling, running, and monitoring capabilities.

Apache SkyWalking {#h2-4-apache-skywalking}
-------------------------------------------

<img loading="lazy" decoding="async" class="alignright wp-image-103497 size-full" src="/images/posts/2023/12/five-apache-projects-you-probably-havent-heard-of-yet/skywalking-logo.png" alt="" width="256" height="61">

[Apache SkyWalking](https://skywalking.apache.org/) is an tool, focusing on microservices, Cloud Native apps, and Kubernetes architectures. It builds its architecture on four kinds of components:

<br />

* **Probes** collect telemetry data (metrics, logs, traces, and events). They support multiple output formats, including OpenTelemetry.
* The **platform** aggregates and processes data
* The **storage** offers an interface over a supported backend. Supported backends include ElasticSearch, H2, MySQL, TiDB, and BanyanDB, a custom storage engine developed for SkyWalking
* Finally, a web **UI** allows visualizing SkyWalking's data

[![](/images/posts/2023/12/five-apache-projects-you-probably-havent-heard-of-yet/ui_ServiceMesh-1024x527.png)](/images/posts/2023/12/five-apache-projects-you-probably-havent-heard-of-yet/ui_ServiceMesh.png)

Skywalking supports a couple of formats, including OpenTelemetry. Given the industry's current focus on OpenTelemetry, I recommend seriously considering this option.

Apache Doris {#h2-5-apache-doris}
---------------------------------

<img loading="lazy" decoding="async" class="alignright size-full wp-image-103499" src="/images/posts/2023/12/five-apache-projects-you-probably-havent-heard-of-yet/Doris_Logo.png" alt="" width="256" height="90">

[Apache Doris](https://doris.apache.org/) is a real-time data warehouse.

<br />

Doris promotes four primary scenarios:

* Reporting analysis
* Ad-Hoc query
* Unified data warehouse construction
* Data lake query

![](/images/posts/2023/12/five-apache-projects-you-probably-havent-heard-of-yet/doris-architecture-1024x359.png)

Doris is mostly MySQL compliant so that you can use a regular MySQL client.

Discussion {#h2-6-discussion}
-----------------------------

The Apache Foundation hosts the projects above, but they have another thing in common: they were all incepted in China. Have a look at the [Apache project list](https://projects.apache.org/projects.html). You'll probably be amazed at the sheer number; it's close to 300!

In recent years, the number of projects incepted at the Apache Foundation has increased drastically. Look again at the list; I'm sure you only know a few of them - lots come from China. The trend is only growing; it's a great move to integrate China with the OpenSource world!

Just as I finish this post, my friend Stefano Fago has posted on another relevant project, [Apache Paimon](https://paimon.apache.org/), a streaming data lake platform.

**To go further:**

* [Apache APISIX](https://apisix.apache.org/)
* [Apache ShardingSphere](https://shardingsphere.apache.org/)
* [Apache SeaTunnel](https://seatunnel.apache.org/)
* [Apache SkyWalking](https://skywalking.apache.org/)
* [Apache Doris](https://doris.apache.org/)

*** ** * ** ***

*Originally published at [A Java Geek](https://blog.frankel.ch/five-apache-projects/) on December 17^th^, 2023*

*[CDC]: Change Data Capture
*[APM]: Application Performance Monitor
