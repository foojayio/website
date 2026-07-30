---
title: "Apache Cassandra 4.0: Taming Tail Latencies with Java 16 ZGC"
slug: "apache-cassandra-4-0-taming-tail-latencies-with-java-16-zgc"
date: "2021-06-22T07:45:40+00:00"
lastmod: "2021-11-03T13:32:36+00:00"
description: "Learn about improvements in Java garbage collection that Cassandra 4.0 coupled with Java 16 offers over Cassandra 3.11 on Java 8!"
canonical: "https://jaxenter.com/apache-cassandra-java-174575.html"
authors:
  - "jbellis"
image: "/images/posts/2021/06/apache-cassandra-4-0-taming-tail-latencies-with-java-16-zgc/shutterstock_1673041306-350x233-1.jpg"
categories:
  - "Apache Cassandra"
  - "Apache Pulsar"
  - "Performance"
tags:
related_posts:
frozen: false
---

With Apache Cassandra 4.0, you not only get the direct improvements to performance added by the Apache Cassandra committers, you also unlock the ability to take advantage of seven years of improvements in the JVM itself. This article focuses on improvements in Java garbage collection that Cassandra 4.0 coupled with Java 16 offers over Cassandra 3.11 on Java 8.

Like so many others in the Apache Cassandra community, I'm extremely excited to see that the 4.0 release is finally here. There are [many, many improvements to Cassandra 4.0](https://cassandra.apache.org/doc/latest/new/). One enhancement that is more important than it might look is the addition of support for Java versions 9 and up. This was not trivial, because Java 9 made changes to some internal APIs that the most performance-oriented Java projects like Cassandra relied on (you can read more about this [here](https://issues.apache.org/jira/browse/CASSANDRA-9608)).

This is a big deal because with Cassandra 4.0, you not only get the direct improvements to performance added by the Apache Cassandra committers, you also unlock the ability to take advantage of seven years of improvements in the JVM (Java Virtual Machine) itself.

Here, I'd like to focus on improvements in Java garbage collection that Cassandra 4.0 coupled with Java 16 offers over Cassandra 3.11 on Java 8.

The garbage collection challenge {#h2-0-the-garbage-collection-challenge}
-------------------------------------------------------------------------

In 2012, I gave a talk titled, "Dealing with JVM Limitations in Apache Cassandra." Here is the first slide from that presentation:

<figure class="wp-block-image size-full is-resized is-style-default">
 <img decoding="async" src="/images/posts/2021/06/apache-cassandra-4-0-taming-tail-latencies-with-java-16-zgc/paintpointsforjavadatabases.png" alt="" class="wp-image-45221" width="734">
</figure>

On the one hand, garbage collection is a primary reason that Java is so much more productive than traditional systems languages like C++. As JVM architect Cliff Click once wrote, "Many concurrent algorithms are very easy to write with a GC and totally hard to downright impossible using explicit free." Cassandra takes full advantage of this power.

But performing garbage collection means having to briefly pause the JVM to determine which objects are no longer in use and can safely be disposed of. These GC pauses can cause delayed response times to client requests, i.e., increased latencies.

Not all requests are affected by this--only the handful of requests that are in flight while Cassandra's request-handling threads are paused for the GC. The performance impact is thus only visible in tail latencies, that is, the 99th percentile or 99.9th percentile measurements, corresponding to the slowest 1% or 0.1% of requests.

As with so many things, optimizing GC involves tradeoffs, and the original Java GC designs focused more on improving throughput than on reducing pause times. Fast forward to 2021 and we have common server-class CPUs with 64 cores/128 threads---we have plenty of throughput on tap. It's time to spend some of those cycles on lower pause times.

The Z Garbage Collector (ZGC) was created to address this situation, and specifically to guarantee pause times under 10ms. ZGC was added to Java 11 as an experimental feature, [promoted to production in Java 15](https://openjdk.java.net/jeps/377), and [further improved](https://malloc.se/blog/zgc-jdk16) in Java 16.

To show how well ZGC improves Cassandra performance, we compared both throughput and latency in three environments: Cassandra 3.11 running on JDK 8 with its default CMS GC settings, Cassandra 4.0 running on JDK 8 with the same settings, and Cassandra 4.0 running on JDK 16 with ZGC. I'm pleased to report that ZGC convincingly achieves its design goals, allowing Cassandra to deliver nearly-constant latencies through the 99th percentile, with only a modest uptick at the 99.9th percentile!

<figure class="wp-block-image size-large is-resized">
 <img decoding="async" src="/images/posts/2021/06/apache-cassandra-4-0-taming-tail-latencies-with-java-16-zgc/JAX_LDN21_728x90_61392_v1.jpg" alt="" class="wp-image-45222" width="716">
</figure>

ZGC performance results {#h2-1-zgc-performance-results}
-------------------------------------------------------

My colleague Jonathan Shook benchmarked the performance characteristics of Cassandra 3.11 and 4.0 in detail across three workloads: simple key/value, a time series workload with many rows per partition, and a tabular workload with one row per partition but many columns per row.

### Throughput results {#h3-2-throughput-results}

![](/images/posts/2021/06/apache-cassandra-4-0-taming-tail-latencies-with-java-16-zgc/graph.png)

Here we are looking at Cassandra running at 70% of maximum throughput. This leaves 30% operational headroom to absorb compaction, repair, or load spikes for the purposes of realistic measurements.

Cassandra 4.0 running with the same configuration as Cassandra 3.11 is 30% faster in the key/value workload, 2% slower in the time series workload, and 10% faster in the tabular workload. Turning on ZGC unlocks an additional 30% more throughput for key/value and time series workloads, but has no effect on the tabular workload.

### Latency results {#h3-3-latency-results}

I've split the latency results into one chart per workload so it's easier to see the trends across the different percentiles:
![](/images/posts/2021/06/apache-cassandra-4-0-taming-tail-latencies-with-java-16-zgc/workloadlatencies.png)

<figure class="wp-block-image size-large is-resized is-style-default">
 <img decoding="async" src="/images/posts/2021/06/apache-cassandra-4-0-taming-tail-latencies-with-java-16-zgc/timeseries-768x475-1.png" alt="" class="wp-image-45228" width="512">
</figure>

![](/images/posts/2021/06/apache-cassandra-4-0-taming-tail-latencies-with-java-16-zgc/tabular.png)

For these results, we limited each test scenario to the slowest system's throughput, i.e., we used 30,000, 44,000, and 54,000 requests per second for the key/value, time series, and tabular workloads, respectively.

Cassandra 4.0's latencies are virtually identical to 3.11's with the same GC settings, but ZGC is consistently better, up to a solid factor of 5 to 10 better at p99 and p999 percentiles.

The NoSQLBench performance testing suite {#h2-4-the-nosqlbench-performance-testing-suite}
-----------------------------------------------------------------------------------------

Most benchmarks of non-relational databases are done with either product-specific tooling (like [cassandra-stress](https://cassandra.apache.org/doc/latest/tools/cassandra_stress.html)), or with [YCSB](https://github.com/brianfrankcooper/YCSB), which gives you a lowest-common-denominator key-value workload across dozens of systems.

Jonathan Shook created [NoSQLBench](https://github.com/nosqlbench/nosqlbench) to be a cross-platform performance testing tool that is easier to use than cassandra-stress and (much) more powerful than YCSB; in fact, its scripting layer is powerful enough to support things that no other testing tool can enable, with particular emphasis on modeling complex workloads with fidelity, as well as simulating realistic scenarios such as load spikes. As its name suggests, NoSQLBench is not Cassandra-specific and encourages participation from all who want to contribute; today there are clients for Cassandra, CockroachDB, JDBC, and MongoDB, as well as non-database products Kafka and Pulsar. If you're serious about performance testing in 2021, you should check out NoSQLBench. You can get started at [GitHub](https://github.com/nosqlbench/nosqlbench). Other useful links: [releases](https://github.com/nosqlbench/nosqlbench/releases), [discord](https://discord.gg/dBHRakusMN), [docs](http://docs.nosqlbench.io/#/docs/).

The NoSQLBench workload descriptions for the tests in this post can be found [here](https://github.com/nosqlbench/nosqlbench/tree/main/driver-cql-shaded/src/main/resources/activities/baselinesv2).

Conclusion {#h2-5-conclusion}
-----------------------------

Without switching to ZGC, Cassandra 4.0 offers modest but real throughput improvements for key/value and tabular workloads.

Combining Cassandra 4.0 with ZGC in Java 16 results in further improvements to throughput for key/value and time series workloads as well as convincingly demonstrating ZGC's design goals to make GC pause time a non-issue across all tested workloads for Cassandra 4.0.

ZGC is production-ready starting with Java 15; for enterprises that want to stick with LTS releases, ZGC will be one of the headlining reasons to upgrade to the Java 17 LTS release later this year. ZGC is one of the most significant performance "free lunches" available, and it Just Works---the results shown here are out-of-the-box for ZGC with no extra tuning.

Appendix: Test environment {#h2-6-appendix-test-environment}
------------------------------------------------------------

All tests were run on the same physical cluster of AWS i3.4xl nodes: 16 vCPUs, 122GB RAM, 10Gb network, 5 nodes in the cluster. Storage was configured as XFS on direct NVMe, single volume. All data was stored at RF3. Assigned tokens were used to ensure consistent data distribution across the tested versions. Consistency level for all operations was set as LOCAL_QUORUM. Concurrency from the client side was set at 960 (20x client cores) for the keyvalue test, and 480 (10x client cores) for the time-series and tabular tests.

All measurements were taken from the client, and include duration between submitting and fully reading any data in results. All measurements were taken with 3 significant digits of precision, then rounded to the nearest ms. ZGC was configured with [basic recommended settings](https://wiki.openjdk.java.net/display/zgc/Main#Main-EnablingZGC): 16GB min heap, 64GB max heap, large pages enabled. The other numbers are using Cassandra's out-of-the-box configuration with CMS.
