---
title: "Cloud Cost Optimization Is Hard, Java Can Help"
slug: "cloud-cost-optimization-is-hard-java-can-help"
date: "2024-02-21T12:07:37+00:00"
lastmod: "2024-02-22T10:49:48+00:00"
description: "Did you know switching your Java runtime helps reduce Cloud waste?"
canonical: "https://www.azul.com/blog/cloud-cost-optimization-is-hard/"
authors:
  - "simonritter"
image: "https://foojay.io/wp-content/uploads/2024/02/Latency-Line-Chart-min.jpg"
categories:
  - "Java Core"
  - "Performance"
  - "Videos"
tags:
related_posts:
frozen: false
---

In my recent conversation with William Fellows, Research Director at S\&P Global Market Research, we [discussed ways to reduce cloud waste](https://www.youtube.com/watch?v=nAP3bYxdsZw&t=1s) specifically for Java workloads. After all, cloud cost optimization is hard.

{{< youtube nAP3bYxdsZw >}}

A large delta is growing between budgeted and actual spending. Top areas for spending are cloud integration and modernization services, security and compliance monitoring, and application modernization -- specifically applications written in Java.

Let's explore some background on the cloud in general.

The cloud is the logical progression for providing compute resources. Rather than taking on the cost and complexity of installing your own servers, networking, power supplies and cooling, you let the cloud provider do that for you. When you need compute resources, you specify what you want and then pay only for how long you use them, a simple utility-based pricing model.

Users see the potential benefits immediately. There is no capital expenditure to build a data center and reduced operation spending, as only those resources used are paid for.

Controlling overspending is hard {#h-controlling-overspending-is-hard-nbsp}
---------------------------------------------------------------------------

Unfortunately, many users never realize those cost savings, and cloud costs often prove even more expensive.
![Cloud Cost Optimization is hard for Java Workloads: reasons for overspending](https://www.azul.com/wp-content/uploads/Screenshot-2023-12-13-at-9.49.27%E2%80%AFAM-1024x592.png) Source: S\&P Global

In fact, in a [recent survey by S\&P Global Market Research](https://www.youtube.com/watch?v=nAP3bYxdsZw&t=1s), 27% of respondents said scaling up resources to address unanticipated demand and spend was the biggest reason for overspending on IaaS/PaaS/[public cloud](https://www.azul.com/glossary/public-cloud/) in 2022. Another 15% cited over-provisioning -- or committing more resources than needed.

Why is this, and why is this more frequently the case when running JVM-based workloads?

What's Java got to do with cloud costs? {#h-what-s-java-got-to-do-with-cloud-costs-nbsp}
----------------------------------------------------------------------------------------

We need more background on how the Java platform works to understand how Java can impact application performance and help curb over-provisioning.

One of the early marketing slogans for Java was, "Write once, run anywhere." This was true because application code compiled for the Java platform generates bytecodes rather than native machine instructions. Aside from the benefit of running the same code on a Windows or Linux machine without change or recompilation, it also delivers scalability to internet-sized workloads. This is one reason that, even after nearly 30 years, Java is consistently in the top three (mostly top two) most popular development languages.

The cause of this scalability and performance is what is referred to as just-in-time (JIT) compilation. The bytecodes are initially interpreted, converting each in turn to native instructions. This delivers slower performance than traditional ahead-of-time (AOT) compiled code. As frequently used sections of code are identified, they are compiled to native machine instructions as the application is running. This enables far better optimization thanks to a precise view of how the code is being used as it is compiled.

The time to compile all the frequently used sections of code is called warmup time for a JVM-based application. This time is unimportant for many applications that run for days, even weeks or months, as it is negligible compared to the time the application runs. However, in modern, microservice-based applications, it can be significant. One of the biggest benefits of the microservice architecture is the ability to 'spin up' new service instances when a sudden increase in load occurs and to shut them down when the load drops. This should lead to the optimum cost solution in the cloud. So why doesn't it?

With JVM-based applications, when new service instances start, the warmup time delays the instance's ability to handle requests. To enable mission-critical enterprise applications to meet their SLAs, DevOps teams frequently keep a pool of running services in reserve. These services are fully warmed up and ready to be used immediately when required. Of course, the downside is that they consume resources even when they are not being used. If they are only needed infrequently, they produce considerable waste and additional cloud costs.

Switching your Java runtime helps reduce cloud waste {#h-switching-your-java-runtime-helps-reduce-cloud-waste-nbsp}
-------------------------------------------------------------------------------------------------------------------

The good news is that Azul is adapting the Java platform to make applications run faster and address these issues.
![Cloud Cost Optimization is hard for Java Workloads: ReadyNow Latency Line Chart](https://www.azul.com/wp-content/uploads/Latency-Line-Chart-min.jpg)

We've developed a high-performance version of the JVM called Azul Platform Prime. This Java runtime addresses users' frequent pain points regarding cloud cost optimization for Java workloads and JVM performance: latency associated with garbage collection (GC), throughput of transactions, and warmup time.

Platform Prime fully conforms to the Java SE standard and passes all TCK tests, making it a true drop-in replacement for other, lower-performance Java platforms. No application code changes or recompilation are necessary.

Azul Platform Prime includes three areas of change:

* [The Continuous Concurrent Compacting Collector (C4)](https://www.azul.com/products/components/pgc/). A pause-less collector that eliminates the problems of GC latency, even when scaling to heaps of 20TB.
* [The Falcon JIT compiler](https://www.azul.com/products/components/falcon-jit-compiler/). Based on the open-source LLVM project, Falcon delivers superior transaction throughput via enhanced optimizations tailored to the hardware microarchitecture. This is another way to deliver cloud cost reductions. For example, you can process up to 40% more transactions per second when running the Kafka event streaming platform. You can reduce the size of nodes in your cluster, the number of nodes, or both while still meeting your SLA. Fewer nodes and smaller nodes equal less cloud spend.
* [ReadyNow warmup elimination technology](https://www.azul.com/products/components/readynow). When you have run your application or service until all frequently used code has been compiled and optimized, ReadyNow records a profile that includes all information about the compilation that has been performed, including the code generated. When the application or service starts again, the JVM uses the profile to ensure all code is compiled and ready to go before the application code starts to execute.

![Cloud Cost Optimization is hard for Java Workloads: Unused operational compute capacity](https://www.azul.com/wp-content/uploads/CCO-Blog-Chart-2-768x668.png)

Ready for a deeper dive? {#h-ready-for-a-deeper-dive-nbsp}
----------------------------------------------------------

There's a lot of great information about key cloud cost trends that you need to know [from our conversation](https://www.youtube.com/watch?v=nAP3bYxdsZw&t=1s).

* **Cloud pricing changes may be in your favor**: The volume of price, product and service changes in AWS, Azure, and Google Cloud Platform catalogs have changed significantly -- buckle-up, it's getting more complex.
* **Benchmark your organization unit costs**: General-purpose, compute and memory virtual machine benchmarks reveal some price-relief -- but that may not be enough.
* **How do your cloud spending shifts compare:** the top cloud-attached services trends in the market may surprise you.

You are going to want to hear the full conversation between William and myself. A Java runtime that delivers more performance can make a big difference.

<br />

<br />
