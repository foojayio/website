---
title: "Container Awareness for Java Developers Today"
slug: "container-awareness-for-java"
date: "2020-08-24T08:55:22+00:00"
lastmod: "2020-08-27T05:38:04+00:00"
description: "When you containerize a Java application, make sure your base JDK image is container-aware so that the JDK can allocate memory and CPU counts properly."
canonical: "https://spring-gcp.saturnism.me/deployment/docker/container-awareness"
authors:
  - "saturnism"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "Performance"
tags:
related_posts:
  - "optimizing-the-garbage-collector-when-migrating-cloud-workloads"
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
  - "jurassic-jdk-migrate-or-extinct"
  - "quarkus-unpacked-insights-from-the-foojay-podcast"
enlighterjs: true
frozen: false
---

When you containerize a Java application, make sure you use a base JDK image that is container-aware (CGroup aware) so that the JDK can allocate memory and CPU counts properly.

Older versions of JDK (prior to 8u192) may not have container awareness (or may have experimental support that requires explict flags to enable). Older versions of JDK may look at the traditional `/proc/meminfo` and `/proc/cpuinfo`files for available memory and CPUs. The content of these files reflects the amount of resources of the host/node machine that is running the container, but do not reflect the actual limits assigned to the container (which may be much less).

Newer versions of JDK (8u192 and above) will automatically discover the CGroup resource allocations located in `/sys/fs/cgroup/cpu` and `/sys/fs/cgroup/memory`.

#### Heap {#heap}

Run a Docker container and give it only 256MB of memory, and see what an older version of JDK will assign for the default Max Heap.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">docker run -ti --rm --cpus=1 --memory=256M openjdk:8u141-jre \
  java -XX:+PrintFlagsFinal -version | grep MaxHeapSize</pre>

Because version `8u141` is not container-aware, it will output the `MaxHeapSize` (in bytes) that is calculated from the host machine and can be significantly higher than the 256MB of memory you originally assigned. This means your Java process may allocate heap aggressively and go beyond the original limit, causing the container instance to be killed, usually result in a `OOMKilled`message.

Run the same command, but with a newer version of JDK:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">docker run -ti --rm --cpus=1 --memory=256M openjdk:8u252-jre \
   java -XX:+PrintFlagsFinal -version | grep MaxHeapSize</pre>

The output of `MaxHeapSize` is now `132120576` bytes, which is \~126MB, indicating that it's now respecting the 256MB limitation we assigned for the container.

The JVM heap size should never be equal memory resource you assigned. In this case, even though we assigned 256MB of memory to the container, the Max Heap must be much lower than that (e.g., 50% of that, or depending on your application). This is because the JVM also uses native memory in addition to the heap.

JVM native memory usages contains thread stack, code cache, metaspace, and potentially direct memory buffer allocations.

#### Estimate Memory Needs {#estimate-memory-needs}

According to the [Cloud Foundry Java Buildpack Memory calculator documentation](https://docs.google.com/document/d/1vlXBiwRIjwiVcbvUGYMrxx2Aw1RVAtxq3iuZ3UK2vXA/edit), the total native memory needed for a JVM instance is approximately linear to the number of loaded classes.

You can use [Cloud Foundry Java Buildpack Memory calculator](https://github.com/cloudfoundry/java-buildpack-memory-calculator) to the memory needs and configurations.

#### Understand Memory Used {#understand-memory-used}

In cases where you are getting `OOMKilled` for your container instance, and have already made sure that you are using a container-aware version of JDK, then you may want to turn on [Native Memory Tracking](https://docs.oracle.com/javase/8/docs/technotes/guides/troubleshoot/tooldescr007.html).

Native Memory Tracking can only be enabled via command line argument, and cannot be enabled using `JAVA_TOOL_OPTIONS`.

You can run this command to see a sample output of Native Memory Tracking:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">docker run -ti --rm openjdk:8u252-jre \
  java -XX:+UnlockDiagnosticVMOptions \
  -XX:NativeMemoryTracking=summary \
  -XX:+PrintNMTStatistics \
  -version</pre>

Native Memory Tracking can only print out memory usage details upon a **successful** exit.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">java -XX:+UnlockDiagnosticVMOptions \
  -XX:NativeMemoryTracking=summary \
  -XX:+PrintNMTStatistics \
  -jar ...</pre>

f your application was `OOMKilled`, then it's an unsuccessful exit, so the memory details may not be printed. In this case, consider first increase the amount of memory allocation, and then trigger a successful exit, to get the native memory usage details.

#### CPU {#cpu}

Run a Docker container and giving it only 2 CPUs, and see what an older version of JDK will assign for the default Parallel GC threads.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">docker run -ti --rm --cpus=2 openjdk:8u141-jre java \
  -XX:+PrintFlagsFinal -XX:+UseParallelGC -version | grep ParallelGCThreads</pre>

It will output the `ParallelGCThreads` that is calculated from the number of CPUs of the host machine and can be significantly higher than `2`.

Run the same command, but with a newer version of JDK:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">docker run -ti --rm --cpus=2 --memory=256M openjdk:8u252-jre java \
  -XX:+PrintFlagsFinal -XX:+UseParallelGC -version | grep ParallelGCThreads</pre>

The output of `ParallelGCThreads` is `2`.

#### Runtime API {#runtime-api}

When using non-container-aware JDK versions, both Memory and CPU can be inaccurately reflected in the [`Runtime`](https://docs.oracle.com/javase/8/docs/api/java/lang/Runtime.html) API as well.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Max heap you can use
Runtime.getRuntime().maxMemory()

// Number of processors
Runtime.getRuntime().availableProcessors()</pre>

This is important because some libraries and applications may use `availableProcessors` to determine the size of the thread pools. So, if you allocated only `2` CPUs, but the JVM inaccurately sees `32` CPUs from the host, then the libraries may over-allocate the thread pool size, and causing your application to run more than the underlying system allows.

**Note:** Used with permission and thanks --- originally written by Ray Tsang and published on [Spring Boot on GCP](https://spring-gcp.saturnism.me/deployment/docker/container-awareness).
