---
title: "Handling JDK & GC Options Dynamically in Elasticsearch"
slug: "handling-jdk-and-gc-options-dynamically-in-elasticsearch"
date: "2020-10-28T07:48:08+00:00"
lastmod: "2021-08-23T13:02:17+00:00"
description: "We dive into the startup of Elasticsearch, parse configurable JVM options, and ergonomically switch between JVM options on startup."
authors:
  - "alexander-reelsen"
image: "Favicon-3-2.png"
categories:
  - "Elastic"
tags:
related_posts:
frozen: false
---

**Today we will dive into the start up of Elasticsearch, how it parses the configurable JVM options and how it can ergonomically switch between JVM options on startup.**

[Elasticsearch](https://www.elastic.co/elasticsearch/) is a distributed search \& analytics engine. Elasticsearch's full text search capabilities are based on [Apache Lucene](https://lucene.apache.org/). It's the heart of the Elastic Stack and powers its solutions Enterprise Search, Observability and Security as well as many well known internet websites like Wikipedia, GitHub or Stack Overflow.

Elasticsearch tries to be a good JVM ecosystem citizen and ships with a recent distribution of the JVM. Elasticsearch 7.9.3 ships with a recent OpenJDK 15 distribution. One of the core principles of Elasticsearch is to get up and running as simple as possible. This is the reason why Elasticsearch ships a JDK, so that the user does not have the trouble of installing one. Not everyone is a Java expert after all! At some point however, you need to become at least a small expert, as you need to configure some JDK options like setting the heap.

In order to be able to configure JDK options for Elasticsearch before startup, these options need to be parsed and evaluated. When the user runs [./bin/elasticsearch](https://github.com/elastic/elasticsearch/blob/7.9/distribution/src/bin/elasticsearch) or [./bin/elasticsearch.bat](https://github.com/elastic/elasticsearch/blob/7.9/distribution/src/bin/elasticsearch.bat), some more Java programs are started **before** the actual Elasticsearch process is fired up. First a program to [create a temporary directory](https://github.com/elastic/elasticsearch/blob/7.9/distribution/tools/launchers/src/main/java/org/elasticsearch/tools/launchers/TempDirectory.java) is launched, which acts differently on Windows than on other operating systems. Second, the [JvmOptionsParser](https://github.com/elastic/elasticsearch/blob/7.9/distribution/tools/launchers/src/main/java/org/elasticsearch/tools/launchers/JvmOptionsParser.java) class is used to determine the Java options, and only after this is done, the output of the parser is used to start the main Elasticsearch process. This also allows to run the other Java programs with small heaps to make sure they are fast - by using the JDK defaults.

Let's dive into the mechanism to configure JVM options.

### Configuring JVM Options with Elasticsearch

The most commonly used jvm option that requires configuration before the Elasticsearch Java process is started, is setting the heap size. In order to do so, Elasticsearch makes use of [a mechanism](https://www.elastic.co/guide/en/elasticsearch/reference/7.9/jvm-options.html), that not only reads the `config/jvm.options` file but also reads the `config/jvm.options.d` directory and appends the contents of all files to create a big list of JVM options. You could create a file like `config/jvm.options.d/heap.options` like this:

```
# make sure we configure 2gb of heap
-Xms2g
-Xmx2g
```

This would configure the heap on startup. However the configuration and parsing mechanism is more powerful. Not only you can configure options, you can also configure different options for different JDK major versions.

Side note: In case you are asking yourself, why is there a `jvm.options.d` directory and not just a file: this caters properly for package upgrades of RPM or debian packages, so that the original `jvm.options` can be replaced and does not need to be edited.

So, why is this useful you might ask yourself? Well, sometimes a new Java release deprecates features, and sometimes features get removed. One of those features was the CMS Garbage Collector, which got deprecated in [Java 9](https://openjdk.java.net/jeps/291) and finally removed more than two years later in [Java 14](https://openjdk.java.net/jeps/363). Elasticsearch has been a happy user of the CMS for years, but with the removal there had to be a mechanism to start with another garbage collector as of Java 14 onwards. In order to support this, the JVM options parser also supports the ability to set certain options only for a certain Java version like this:

```
## GC configuration
8-13:-XX:+UseConcMarkSweepGC
8-13:-XX:CMSInitiatingOccupancyFraction=75
8-13:-XX:+UseCMSInitiatingOccupancyOnly

## G1GC Configuration
# NOTE: G1 GC is only supported on JDK version 10 or later
# to use G1GC, uncomment the next two lines and update the version on the
# following three lines to your version of the JDK
# 10-13:-XX:-UseConcMarkSweepGC
# 10-13:-XX:-UseCMSInitiatingOccupancyOnly
14-:-XX:+UseG1GC
14-:-XX:G1ReservePercent=25
14-:-XX:InitiatingHeapOccupancyPercent=30
```

The same applies for different GC options with Java 8 and Java 9

```
## JDK 8 GC logging
8:-XX:+PrintGCDetails
8:-XX:+PrintGCDateStamps
8:-XX:+PrintTenuringDistribution
8:-XX:+PrintGCApplicationStoppedTime
8:-Xloggc:logs/gc.log
8:-XX:+UseGCLogFileRotation
8:-XX:NumberOfGCLogFiles=32
8:-XX:GCLogFileSize=64m

# JDK 9+ GC logging
9-:-Xlog:gc*,gc+age=trace,safepoint:file=logs/gc.log:utctime,pid,tags:filecount=32,filesize=64m
```

You can read more about [setting JVM options](https://www.elastic.co/guide/en/elasticsearch/reference/current/jvm-options.html) in the official Elastic docs.

There is another [safeguard](https://github.com/elastic/elasticsearch/blob/7.9/distribution/tools/launchers/src/main/java/org/elasticsearch/tools/launchers/JvmErgonomics.java#L95-L130) to append all configured and dynamically created JVM flags and start a JVM is to check if those options are compatible, before starting Elasticsearch in order to fail fast.

Also, Elasticsearch logs all JVM options on start up to allow for easy comparison of what is assumed by the user. Also, those options are not only logged, but can be retrieved using the [nodes info API](https://www.elastic.co/guide/en/elasticsearch/reference/7.9/cluster-nodes-info.html).

### Ergonomic Defaults

So, with an infrastructure in place like that, can we do more fancy things than just parsing JVM options? Of course we can! Ideas anyone?

One of the advantages is to supply some useful standard JVM options, when starting Elasticsearch. There is a [SystemJvmOptions](https://github.com/elastic/elasticsearch/blob/7.10/distribution/tools/launchers/src/main/java/org/elasticsearch/tools/launchers/SystemJvmOptions.java) class, that lists a couple of interesting options like setting the default encoding to UTF-8 or configuring the DNS TTL caching - which is important as Elasticsearch always enables the Java Security Manager.

Also, we can enable some options only, when a certain JDK version is in use. This enables dereferenced null pointer exceptions in Java 14 and above

```java
private static String maybeShowCodeDetailsInExceptionMessages() {
    if (JavaVersion.majorVersion(JavaVersion.CURRENT) >= 14) {
        return "-XX:+ShowCodeDetailsInExceptionMessages";
    } else {
        return "";
    }
}
```

But this infrastructure can go even further, and become smarter over time. How about providing different JVM options depending on configuration settings like the heap?

This is exactly what has been worked on in a [recent addition](https://github.com/elastic/elasticsearch/pull/59667) to Elasticsearch.

If a small heap is configured in combination with the G1 garbage collectors, some additional options are configured.

```java
final boolean tuneG1GCForSmallHeap = tuneG1GCForSmallHeap(heapSize);
final boolean tuneG1GCHeapRegion = 
    tuneG1GCHeapRegion(finalJvmOptions, tuneG1GCForSmallHeap);
final boolean tuneG1GCInitiatingHeapOccupancyPercent =
    tuneG1GCInitiatingHeapOccupancyPercent(finalJvmOptions);
final int tuneG1GCReservePercent =
    tuneG1GCReservePercent(finalJvmOptions, tuneG1GCForSmallHeap);
```

So, what happens here and why? If less than 8GB of heap are configured - which is more often than you think, as many users are also running smaller instances of Elasticsearch and there is an ongoing effort of using less heap and offload this to other parts of the system - three additional options are set. Of course everything can be manually overwritten.

First, the size of a G1 heap region is set to 4 MB, using `XX:G1HeapRegionSize=4m`.

Second, the heap occupancy threshold, which triggers a marking cycle is set to `XX:InitiatingHeapOccupancyPercent=30`, somewhat earlier than the default of `45`.

Third, the `G1ReservePercent` options is set to 15 instead of 25 percent in the small heap case, in both cases deviating from the default of 10 percent.

It took months of benchmarking and testing to come to these numbers, if you are interested in the discussion, there is a lengthy [GitHub issue](https://github.com/elastic/night-rally/issues/246). In case you are wondering how those kind of issues surface during testing Elasticsearch. Elasticsearch is using nightly benchmarks on bare metal hardware to easily spot and investigate regressions. You can check out those [benchmarks here](https://benchmarks.elastic.co/index.html). The tool used for this is called [rally](https://github.com/elastic/rally), a macrobenchmarking framework for Elasticsearch. One of the great features of rally is, that you can use your own data and queries to test and benchmark, so having your own nightly benchmarks is possible.

So, why have those options been picked, you may ask yourself. Thanks to the benchmark infrastructure testing became easy, but not the reason for testing. After switching from CMS to G1 a few benchmark results got worse and required investigation. One of the approaches was also to test the ParallelGC for really small heaps instead of G1, but this was abandoned.

We even managed to find a bug in our G1 configuration options. In order to understand the issue let's explain some Elasticsearch functionality. Elasticsearch utilizes circuit breakers to prevent overloading of a single node by accounting memory, for example when creating an aggregation response or receiving requests over the network. Once a certain limit is reached, Elasticsearch's circuit breaker will trip and return an exception. The idea here is to prevent the famous `OutOfMemoryError`, and tell the user that the request cannot be processed and also indicate if that is temporal or permanent issue. Since Elasticsearch 7.0 a [real memory circuit breaker](https://www.elastic.co/blog/improving-node-resiliency-with-the-real-memory-circuit-breaker) has been added, that takes the total heap into account instead of only the currently accounted data, which is more exact.

However this circuit breaker did not work in combination with the shipped G1 settings, as the configured settings assumed [a heap bigger than 100%](https://github.com/elastic/elasticsearch/pull/46169) of what was configured and so the circuit breaker tripped before the garbage collector started its job of garbage collection per the supplied configuration. Also, the memory circuit breaker was enhanced with some G1 specific code to [nudge G1 to do a young GC](https://github.com/elastic/elasticsearch/pull/58674) at some point.

### Summary

As you can see, properly handling and parsing as well as choosing good default JDK options like switching from one garbage collector to another involves quite a bit of steps, infrastructure, testing, running in production \& verification - and the same probably applies to your own applications as well.

The same applies to all the new generation garbage collectors like [ZGC](https://wiki.openjdk.java.net/display/zgc/Main) and [shenandoah](https://wiki.openjdk.java.net/display/shenandoah/Main). Those will require extensive testing, proper CI integration and maybe a even a few changes in the code. Albeit those GCs promise huge improvements, make sure you are testing properly with your own workloads before jumping on those.

Also, never forget, that a tiny portion of your users will want to set their own options and cater for that properly, including upgrades.
