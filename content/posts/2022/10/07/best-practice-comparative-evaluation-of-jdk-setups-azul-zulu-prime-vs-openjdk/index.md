---
title: "Comparative Evaluation Azul Zulu Prime vs. OpenJDK"
slug: "best-practice-comparative-evaluation-of-jdk-setups-azul-zulu-prime-vs-openjdk"
date: "2022-10-07T08:51:00+00:00"
lastmod: "2022-10-07T09:10:08+00:00"
description: "How to establish whether Azul Zulu Prime improves aspects of application behavior to a degree justifying using it within your organization."
canonical: "https://docs.azul.com/prime/comparative-evaluation-of-jdk-setups"
authors:
  - "frankdelporte"
image: "azul_logo_large_color.png"
categories:
  - "Java Core"
  - "Performance"
  - "Testing"
tags:
related_posts:
  - "fantastic-jvms-and-where-to-find-them"
  - "getting-more-mileage-out-of-kafka-openjdk-vs-azul-prime"
  - "increasing-event-streaming-with-kafka-and-azul"
  - "new-between-quarters-security-updates-for-java-what-cspus-mean-for-your-release-pipeline"
frozen: false
---

In today's Java ecosystem, you have a wide range of possible runtimes. Choosing between them can have a huge impact on the capabilities or performance of your environments and applications.

In this article, you will learn about best practices for **comparative evaluations between JDK setups** , whether they be different configurations in a single JDK or comparing [Azul Zulu Prime Builds of OpenJDK (Azul Zulu Prime)](https://www.azul.com/products/prime/) to OpenJDK itself.

By following this guide, you will be able to establish credible evidence that Azul Zulu Prime improves aspects of application behavior to a degree that justifies using it within your organization.

## Comparative Evaluation

### What To Do

When comparing Azul Zulu Prime to different JDKs, the goal of your evaluation should be the testing of your application under real-world conditions. We strongly recommend production testing, to be 100% sure of the differences that can be achieved.

Of course, this is not possible at all times, in which case lab testing can be used as long as the test results are providing the right insights to predict the impact when deploying Azul Zulu Prime in production.

Also make sure you are running a long enough test that both systems are completely warmed up. Azul Zulu Prime can be slower or faster than OpenJDK to warm up your code to full speed, depending on the options used.

Measure at what point your JVM has reached optimal speed and only start measuring your business metrics after that point, using the same (maximum) warmup period for all JDK configurations.

### What Not To Do

When doing a comparative evaluation, you want to make sure you are measuring one change at a time. You should therefore not combine testing different JDKs with additional tunings of applications, reconfiguration of the environment, or modifications to the code or the choice of third-party frameworks or libraries in the application.

Just run your application exactly as you do in your default environment and run Azul with the minimal recommended tunings to get a baseline. You can then tune Azul Zulu Prime to achieve the best performance.

Also, do not use a 'open-the-flood-gates' approach where millions of transactions/messages are simulated and crunched in one go, solely as a short-duration test aiming to reproduce a production result with a lower load over a longer time.

Although this is a possible production scenario, it is very rare. In most cases, those millions of transactions/messages would be processed over some time (7, 12, 24, or more hours). The general misconception is that if the application is tested for an 'open-the-flood-gates' scenario, it should automatically cover all the scenarios, including the production scenario, but this is untrue.

In general, all software components such as Linux, Java, Messaging platform, etc. work very differently in a typical production environment versus completely saturated conditions, and the behavior of saturated environments is not a good indicator of performance under normal loads.

## Defining the Test Goals

Each application services its unique purpose and related goals and requirements. As such, different test goals can be defined.

### Define the Maximum Reachable Capacity

When running large Java workloads that take multiple JVMs, your primary concerns usually are:

* **What are the success criteria for a JVM that is functioning, according to your expectations?** Usually, this is a business metric such as "99.9% of requests complete within 100ms" or "only 0.1% of requests result in failures".
* **What are the parameters that indicate a JVM is over-utilized or under-utilized, and you need to scale up or down?** Often, these are technical metrics such as "the number of requests per second" or "average CPU utilization".
* **How much load can I put through each JVM instance before I need to scale out another instance?** How many instances in total do I need to serve my expected total load?

Your goal in testing Azul Zulu Prime in these cases is usually to:

* Increase the amount of load a single JVM can handle while still meeting your success criteria.
* Decrease the total amount of compute-power you need to handle a given Java load.

Azul has developed the TUSSLE (Throughput Under Service Level Expectation) testing framework to measure carrying capacity versus load.

For more information about this tool, check the [GitHub project](https://github.com/AzulSystems/tussle-framework). To see it in action, look at our benchmarking of [Apache Cassandra carrying capacity](https://www.azul.com/blog/cassandra-performance-throughout-responsiveness-capacity-and-cost/).

### Minimize the Response Time

Often your primary concern is how low and consistent you can get your response times. Financial trading systems are one typical example of this use case.

Very fast responses with low latency and fewer outliers are one of the main benefits of Azul Zulu Prime. When measuring this use case, make sure you measure latency under real-world conditions and not in saturated environments.

The above-mentioned TUSSLE framework can help here too.

### Reach the Shortest Warm-Up Time

In some use cases, you require an application to be warmed up and running at optimum speed as fast possible once it starts accepting traffic.

For example, you may need to reach full speed from the 10th transaction, instead of the 10,000th.

In default mode, Azul Zulu Prime actually takes a longer time to warm up than OpenJDK.

But when you use a tool like [ReadyNow](https://docs.azul.com/prime/Use-ReadyNow.html), you can in many cases reach optimum speed much quicker than with OpenJDK. How warm-up times can be measured, configured, and compared is described in detail on [Analyzing and Tuning Warm-up](https://docs.azul.com/prime/analyzing-tuning-warmup.html).

## Defining the Test Approach

When evaluating your application performance on Azul Zulu Prime, or any Java runtime, it is important to adhere to the following guidelines.

### Important Test Strategy Considerations

It is important to establish a testing approach by asking the following questions:

* Is there a production-like environment for testing?
  * The closer your test workload is to production, the less likely you are going to be surprised when you promote the application to production.
  * The best place to test the performance characteristics and value of a JVM is in the production environment.
  * As soon as you are comfortable/able, run tests in production.
  * When not possible to test in production, make sure you can simulate the conditions as closely as possible compared to your production environment. The goal is to be confident you will achieve the same results once you deploy Azul Zulu Prime in your production environment.
* Can the incoming load/request be spread like production?
  * Production has the actual environment, workload, and monitoring you wish to evaluate against.
* Can the load be ingested over many hours?
  * Long runs with real (production) workloads are more likely to capture the pain you are trying to address.
* Can the raw latency information be extracted from the individual transactions for the entire duration or specific duration (eg: for 3 hours after 45 min post-start-up)?
* Are clear and measurable success criteria defined?
  * Focus on what matters to your business, be it response times, SLAs, timeouts,... This is where you want improvements.
  * Enumerate the metrics (measurable properties) used to evaluate test outcomes.
* Can the logs (GC, profile, etc.) be extracted post-run?
* When evaluating ReadyNow: Can the application be restarted at certain intervals without losing the profile file from the previous run?

### Test Conditions To Avoid

|      Test Type      |                                                                                                                                     Why Avoid?                                                                                                                                      |
|---------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Short runs          | Short runs are not realistic in most cases. In most cases, you want to evaluate the steady state of the application. And in most cases, you want to understand the performance profile over longer periods to capture those occasional but very bad events (such as STW GC pauses). |
| Microbenchmarks     | There is a time for microbenchmarks, but not when evaluating the Java runtime for your critical application.                                                                                                                                                                        |
| Synthetic workloads | Workloads that do not simulate production risk running tests that do not embody the problems faced in production. An application can do well on tests, only to fail once promoted to production.                                                                                    |
| Insufficient cores  | Starving the JVM of CPU time will result in suboptimal results. Prime's use of CPUs is not identical to OpenJDK. Ensure minimum requirements are met.                                                                                                                               |
| Insufficient memory | Prime can work with the same amount of memory as OpenJDK. However, Prime can use more memory without the usual drawbacks. Feel free to assign more memory to Prime, if available. More memory can allow for better performance.                                                     |
| Poor metrics        | For instance, averages and no SLAs are considered as poor metrics. Looking at the average is rarely useful in the context of performance testing. Focus on where the pain is.                                                                                                       |

## Step-by-Step Guide

### Comparing your Current JDK with Azul Zulu Prime

1. Create a baseline test with your current JDK.
   * Run long tests.
     * Tests must run long enough for the results to be meaningful.
     * Run tests for at least an hour, if not longer.
     * Micro-benchmarks are not valuable for proving value.
   * Run realistic workloads.
     * Real load is mandatory to show real value.
     * Meaningful performance comparisons require a production-like load.
   * Capture business metrics.
     * Focus on the business metrics you care about, e.g., response times, throughput, and timeouts.
   * Capture diagnostics.
     * JVM metrics can help correlate internal behavior with application behavior.
     * e.g., `-XX:+PrintGCApplicationStoppedTime` to both current and Zing JVM command lines allows for comparison of JVM pause times.
   * For pilots and evaluation cases, we recommend setting the `Xms` value equal to `Xmx`.
   * Configure the logging settings to be able to analyze the results with [Azul GC Log Analyzer](https://docs.azul.com/prime/diagnosing-java-performance-problems-with-gc-log-analyzer.html).
     * GC File Size: 100MB.
     * GC File Rotation: 30 files.
     * `-Xlog:gc,safepoint:gc.log::filesize=100m::filecount=30`
2. Run the same tests on Azul Zulu Prime.
   * Do not change anything except `JAVA_HOME`.
   * Use the same environment, configuration and load.
3. Review the results.
   * If performance meets the success criteria, great!  
     Let us know, and we can guide you in the following steps.
   * Otherwise, review the logs and tune accordingly.  
     Let us know, we can help!

### Additional Linux Settings

Based on your results, further tuning can be done on your Linux environment to stretch the performance boost provided by Azul Zulu Prime.

* [Azul Zulu Prime System Tools (ZST)](https://docs.azul.com/prime/ZST.html) enables Azul Zulu Prime to support larger heap sizes, and to provide better memory management on older operating system versions that don't have the required functionality already built in.
* If ZST cannot be used, a good alternative is [Transparent Huge Pages (THP)](https://docs.azul.com/prime/Enable-Huge-Pages.html) on RHEL/CentOS 8, Ubuntu 18, Amazon Linux since 2018, Debian 10, SLES 12 SP4 and their later versions. These operating systems support THP with shared memory and feature improved implementation of THP that reduces the risk of system pauses.
* Set `vm.swappiness=0`.  
  More info can be found at [SWAP Settings](https://docs.azul.com/prime/ZST-SWAP-Settings.html).
* Ensure the ulimit is unlimited for virtual memory, resident memory, and core file size.
  * At the command line, type `ulimit -v unlimited -m unlimited -c unlimited`.
  * To make this change permanent after reboots, add this change in `/etc/security/limits.conf` or set it in `/etc/profile` or `~/.profile`.
