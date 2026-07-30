---
title: "Chronicle FIX: Much More Than a Quick Fix | Foojay.io Today"
slug: "chronicle-fix-much-more-than-a-quick-fix"
date: "2023-04-12T13:55:24+00:00"
lastmod: "2023-04-12T13:57:24+00:00"
description: "Many of our customers have upgraded from QuickFIX/J to Chronicle FIX and this article provides some background as to why."
authors:
  - "jerry-shea"
image: "/images/posts/2023/04/chronicle-fix-much-more-than-a-quick-fix/Screen-Shot-2023-04-03-at-2.04.05-PM-1024x614-1.png"
categories:
  - "Developer Tools"
  - "Performance"
  - "Tools"
tags:
related_posts:
  - "chronicle-wire-object-marshalling"
  - "creating-terabyte-sized-queues-with-low-latency"
  - "chronicle-queue-storing-1tb-in-virtual-memory-on-a-128gb-machine"
  - "building-custom-solutions-vs-buy-and-build-software"
frozen: false
---

Many of our customers have upgraded from QuickFIX/J to [Chronicle FIX](https://chronicle.software/fix-engine/) and this article provides some background as to why.

### Introduction {#h3-0-introduction}

[QuickFIX/J](https://www.quickfixj.org/) is free, accessible, supports the FIX standard and is therefore often chosen by the IT team for a first FIX engine.

However, when the business grows, its limitations become clear. The reasons most commonly given to us are:

* Throughput and latency
* HA/DR functionality
* Advanced features e.g. routing and message translation
* Global commercial support

Also, some customers engage with us because they already use our open source products e.g. Chronicle Queue, or our enterprise products e.g., Chronicle Queue Enterprise replication, or Chronicle Services EDA/Microservices accelerator.

See below sections for more detail.

### Upgrading {#h3-1-upgrading}

Chronicle provides tools to assist in the upgrade from QuickFIX/J e.g. to automatically convert configuration from quickfix.ini to Chronicle's YAML format, and upgrades to Chronicle FIX are typically very smooth -- with one customer we helped them convert one of their applications in an afternoon.

Chronicle FIX's code generator uses standard FIX XML files and generates clean, performance-optimised POJOs and byte-backed flyweights, for each message type.

We can also make available the full source code of Chronicle FIX.

### Features {#h3-2-features}

Below see more details on Chronicle FIX features and attributes.

### Throughput and latency {#h3-3-throughput-and-latency}

Chronicle FIX has been built from the ground up by low latency experts to be as fast as possible and leverages many ingenious techniques and patterns, some of which we believe are unique in the industry.

Chronicle FIX is trusted by some of the world's largest financial institutions, is deployed in all the major financial centres, and has proven to be efficient and scalable -- handling billions of FIX messages per week for some customers, and scaling out to 1000s of FIX sessions at others.

The below diagram speaks for itself and shows Chronicle FIX multiple times faster than QuickFIX/J, with the ratio particularly noticeable at higher percentiles.

![](/images/posts/2023/04/chronicle-fix-much-more-than-a-quick-fix/Screen-Shot-2023-04-03-at-2.04.05-PM-1024x614.png)

Note: the benchmark exercises a full round trip i.e. it generates a NewOrderSingle (NOS) and sends it out from one FIX engine to another engine over localhost.

The second engine parses the NOS, generates an ack ExecutionReport, and sends this back to the first engine.

Once this has been parsed and delivered to the application, the time taken is sampled.

### HA/DR {#h3-4-ha-dr}

Chronicle FIX has support for transparent session failover for initiators and acceptors, with flexible configuration to allow the business to determine the level of risk -- the engine, for example, can be configured to only process messages that have been replicated to another host, another rack, or another data centre, depending on message type.

We have customers who -- as part of their standard deployment process, failover between FIX engines in milliseconds with no message loss.

![](/images/posts/2023/04/chronicle-fix-much-more-than-a-quick-fix/Screen-Shot-2023-04-03-at-2.04.38-PM-1024x706.png)

Additionally, there is a lightweight proxy component which supports common patterns such as load-balancing, transparent SSL etc.

Most customers deploy on-prem or colo, but the cloud is also supported.

Chronicle FIX leverages Chronicle Queue Enterprise replication to achieve HA/DR, and the below graph shows the latencies that can be expected using CQE.

The below graph shows the send/ack round-trip/latency impact on the sender as the number of outstanding messages (u) varies. u=0 is fully synchronous replication.

![](/images/posts/2023/04/chronicle-fix-much-more-than-a-quick-fix/Screen-Shot-2023-04-03-at-2.05.05-PM-1024x639.png)

### Features {#h3-5-features}

Chronicle FIX implements additional features which have been driven by customer requirements e.g.

* Supports all FIX versions, ITCH, and SBE (Refinitiv, iLink, etc.) protocols.
* Rules-based routing with seamless translation between schemas
* Load balancing to help distribute load across multiple internal servers transparently to clients
* Allows a site to present a constant connection surface to clients, hiding the internal layout of machines
* De-risk internal server updates (eg by routing only a subset of flow onto new/updated servers)
* Provides a rule-based Drop-Copy service
* FIX version translator/adaptor

### Manageability \& Monitoring {#h3-6-manageability-monitoring}

The engine can be controlled from customers' tooling via API, or with the GUI.

![](/images/posts/2023/04/chronicle-fix-much-more-than-a-quick-fix/Screen-Shot-2023-04-03-at-2.06.36-PM-1024x257.png)

And all FIX messages are stored, indexed and are searchable with a sophisticated query language.

![](/images/posts/2023/04/chronicle-fix-much-more-than-a-quick-fix/Screen-Shot-2023-04-03-at-2.07.05-PM-1024x576.png)

All important metrics, including latency numbers, are exposed via the monitoring features and can be surfaced in your monitoring tool of choice, e.g. [Grafana](https://grafana.com/ "Grafana")

![](/images/posts/2023/04/chronicle-fix-much-more-than-a-quick-fix/Screen-Shot-2023-04-03-at-2.08.13-PM-1024x678.png)

### Commercial Support {#h3-7-commercial-support}

Chronicle offers commercial support from our team of experts, who are located in all major Time Zones.

### Conclusion {#h3-8-conclusion}

When building your production FIX stack you likely want something more than a quick fix.

Chronicle FIX provides best-in-class performance, full HA/DR, advanced features, together with global commercial support.

See also:

[FIX parser](https://fixparser.chronicle.software/ "FIX parser")
