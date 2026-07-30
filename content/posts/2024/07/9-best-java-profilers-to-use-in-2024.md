---
title: "9 Best Java Profilers to Use in 2024"
slug: "9-best-java-profilers-to-use-in-2024"
date: "2024-07-10T13:46:51+00:00"
lastmod: "2024-07-10T14:37:48+00:00"
description: "In this article, I will share why my team decided to start a Java profiler, the list of profilers we found that are suited for profiling Java apps, and how to analyze the data from the profiler to draw conclusions faster."
canonical: "https://digma.ai/9-best-java-profilers-to-use-in-2024/"
authors:
  - "lee-sheinberg"
image: "https://foojay.io/wp-content/uploads/2024/06/WhatsApp-Image-2024-03-18-at-11.42.39-AM.jpeg"
categories:
  - "Profiler"
tags:
related_posts:
frozen: false
---

**In this article, I will share why my team decided to start a Java profiler, the list of profilers we found that are suited for profiling Java apps, and how to analyze the data from the profiler to draw conclusions faster.**

Recently, one of our team members was working on a third-party Linux application, and started encountering instances where he figured that one of his applications probably had memory leaks, as he kept getting an out-of-memory exception.

So, the team decided to start a profiler to monitor the third-party application. The idea was to attach the profiler to the running Java process and then use the Allocation Call Tree view to record methods being called and their associated classes.

As we started looking for Java profilers to help us, we realized there was a whole bunch of them, so we decided to try YourKit It's a popular and robust premium Java profiler designed for Java applications. Setting up the profiler was pretty easy, contrary to rumors I had heard.

We started the profiler to find out which application was slowing down our app, and realized that the tool had really good potential but it's very hard and takes a very long time to actually pinpoint where the problem is. Once I got the data, it was hard to analyze it because I needed to actually look at the call tree and manually come to a conclusion.

![](https://digma.ai/wp-content/uploads/2024/03/image-24-1024x426.png)

YourKit Profiler

We thought to use our [Continuous Feedback](https://digma.ai/how-it-works/ "Continuous Feedback") tool which is not a profiler in the classical sense but can assist in profiling efforts since it can analyze observability data. We thought that our Continuous Feedback tool could help us analyze the profiler results reach my conclusions faster and help me with what to look for in the profiler. So, I opened Digma along with the YourKit profiler to analyze the data of the profiler and come to conclusions.

What is a Java profiler? {#h2-0-what-is-a-java-profiler}
--------------------------------------------------------

A Java profiler is a tool for gauging and examining the performance of Java applications. It collects data on program execution, including the time taken by each function, memory usage, and frequency of function calls.

Java Profiling is beneficial for pinpointing performance bottlenecks in software applications. Analyzing the data collected by a profiler allows developers to identify the sections of the code responsible for the most notable delays or resource consumption. This data can enhance the code, boost performance, and lessen resource consumption.

Hence, a Java profiler is a tool that checks the Java bytecode constructs and operations at the JVM level. These programming structures and actions consist of creating objects, repeating processes (including calling functions recursively), executing methods, running threads, and performing garbage collections.

Types of Java Profilers {#h2-1-types-of-java-profilers}
-------------------------------------------------------

* Sampling profilers: These profilers periodically take snapshots of the running program and analyze the call stack to identify hotspots.
* Instrumentation profilers: These profilers modify the program's code to gather more detailed performance data.

Usecase for Java Profilers {#h2-2-usecase-for-java-profilers}
-------------------------------------------------------------

Java Profiling tools can be used in three ways basically:

Performance optimization: Developers can utilize Java profilers to pinpoint sections of the code responsible for performance problems, like slow function calls or high memory usage. This data can be utilized to enhance the code for better performance.

Memory management: Java profilers can assist in detecting memory leaks, where a program fails to free up unused memory. These leaks can lead to insufficient memory, resulting in a program crashing. By pinpointing memory leaks, developers can correct the code to free up memory that is no longer required.

Testing: Java profilers can evaluate program performance across various scenarios, like varying input sizes or user counts. This can help recognize possible performance problems before the program is launched.

How Do Java Profilers Work? {#h2-3-how-do-java-profilers-work}
--------------------------------------------------------------

The JVM allows Java developers to attach an agent to a running JVM (Java Virtual Machine). When a developer attaches an agent to a JVM, the JVM will provide a class to the agent before loading it. The agent then transforms the class. The agent can make changes to the code of any class.

Java profilers are basically agents. They add instrumentation code to the beginning and end of methods to track how long they take. They also add code to the constructor and finalize methods of every class to keep track of how much memory is used.

In Java, code profiling can be achieved through a range of tools, including those that are built-in and others that are third-party.

Some tools that are well-liked include:

* JVM tools
* Digma
* VisualVM
* YourKit
* JProfiler
* NetBeans Profiler
* IntelliJ Profiler
* Async Profiler
* Arthas

OpenTelemetry and Java Flight Recorder (JFR) cover most bases. Use the OpenTelemetry Java agent if you want auto-instrumentation or just the APIs if you want to do your instrumentation.

1. JVM Tools {#h2-4-1-jvm-tools}
--------------------------------

These Java profiling tools come bundled with the standard JDK and do not need separate installation or setup. There are about five: jstat, jmap, jcmp, jhat, and hprof.

### a. jstat {#h3-5-a-jstat}

This built-in command line tool comes with the standard JDK and does not need installation or setup. Monitoring JVM memory, heap sizing, and garbage collection activity through the command line is highly beneficial.

This tool utilizes the default-enabled built-in instrumentation in JVM to identify the target Java process through a virtual machine identifier (VMID) without requiring any special commands to start JVM.

Here are three ways you can use jstat:

Here are three ways you can use jstat:

Run your Java program using predefined performance constraints\`

```java
`java -Xmx125m -Xms25m -Xmn15m -XX:PermSize=30m -XX:MaxPermSize=30m -XX:+UseSerialGC HelloWorld`
```

You can obtain the process ID using the command below:

```java
ps aux | grep java
```

To start monitoring JVM Heap Memory usage, run jstat with the -gc option on the terminal.

```java
jstat -gc 98132 17527

```

![](https://digma.ai/wp-content/uploads/2024/03/image-11-768x121.png)

### b. jmap {#h3-6-b-jmap}

This command-line tool is also included in the standard JDK. It displays memory-related data (heap summary, java object heap histogram, class loader stats, finalization queue info, dump of Java heap in hprof binary format) for a live VM or core file.

Examining fundamental configurations and algorithms is particularly beneficial.

I suggest utilizing the newer utility, jcmd, available since JDK 8, instead of the jmap utility, for improved diagnostics and decreased performance impact.

Below is how you can use jmap on the terminal

```java
jhsdb jmap ---heap <JAVA_PID>
```

You can also create a heap dump using this command

```java
jcmd <JAVA_PID> GC.heap_dump filename=<FILE>
```

### c. jhat {#h3-7-c-jhat}

This Command line tool comes pre-installed with the standard JDK (no need for installation/setup). It is employed to explore the structure of objects in a heap snapshot (also known as heap dump).

This tool is a substitute for the Heap Analysis Tool (HAT). It processes a binary-formatted heap dump, such as one generated by jcmd.

This tool can also assist in identifying unintended object connections (similar to a memory leak in Java -- an object that is still in use because it is referenced from the rootset)

### d. hprof {#h3-8-d-hprof}

This built-in command-line tool comes with the standard JDK. It examines performance by analyzing heap and CPU profiling, lock contention, memory leaks, and other problems. It is a dynamic-link library (DLL) communicating with the JVM through the Java Virtual Machine Tool Interface (JVMTI).It records profiling data in ASCII or binary form for a file or socket. It can provide information on heap allocation statistics, heap dumps, CPU usage, the states of all monitors and threads in the JVM, and contention profiles.

You can use hprof to profile a class using the command below.

```java
java --agentlib:hprof HelloWorld
```

Using the command below, you can use hprof to obtain the heap allocation profile.

```java
javac --J-agentlib:hprof=heap=dump HelloWorld.java

```

2. Digma Continuous Feedback {#h2-9-2-digma-continuous-feedback}
----------------------------------------------------------------

Digma relies on observability data that it collects automatically using OTEL. Like other profiling tools, the purpose is to analyze how the code works in runtime and find issues; the only difference is that Digma finds them on its own, continuously.

Let's take a look at a few examples:

a. Finding which code/[qureies](https://digma.ai/how-to-optimize-slow-sql-queries/ "qureies") are hurting the app the most (Performance Impact)

You can find the most performance-impactful code in the Assets tab and sort them by Performance Impact:

![](https://digma.ai/wp-content/uploads/2024/03/image-9-2.png)

This view is valuable for developers as it provides a quick overview of which endpoints might need optimization due to performance issues. The combination of execution time and performance impact allows for prioritized troubleshooting and development focus, helping to improve the application's overall performance and reliability.

Or open the Dashboard and use the Client Spans Performance Impact Widget:

![](https://digma.ai/wp-content/uploads/2024/03/image-16.png)

This widget helps developers quickly identify which part of their application might be contributing to performance issues. They can prioritize their debugging and optimization efforts, starting with the endpoints marked with a high impact on performance.

b. Identifying performance degradations in a recent commit (Duration Change)

Another way to track the recent changes in code performance is the [Duration insight](https://docs.digma.ai/digma-developer-guide/digma-features/analytics/duration "Duration insight"). It provides a visual representation of the distribution of call durations for a particular span.

![](https://digma.ai/wp-content/uploads/2024/03/Duration-change.png)

**Last Call Performance:** The widget shows the duration of the most recent call (91.95 milliseconds), which can be immediately compared to typical performance to determine if it's within an expected range or an outlier.

**Median Duration:** The median duration has been recently changed (by 16.19 milliseconds). The red color suggests a recent change, which could indicate a degradation or improvement in performance.

**Performance Distribution:** The histogram itself displays the frequency of call durations, allowing developers to quickly see the commonality of various performance timings. It shows how often certain ranges of call durations occur.

**Slowest 5%:** The part of the histogram that represents the slowest 5% of calls is highlighted. This is critical for identifying long-tail performance issues that might not be evident from average or median statistics.

Using this insight, a developer can track the performance of code over time. The visualization helps in identifying trends such as increasing delays which could signify potential issues such as memory leaks, inefficient database queries, or other resource contention problems. The red block indicating a recent change is particularly useful as it can correlate performance shifts with recent code changes or deployment updates, prompting a more immediate investigation.

c. Profiling to find scaling issues [Scaling insight](https://docs.digma.ai/digma-developer-guide/digma-features/insights/scaling-issue "Scaling insight"))  

Digma offers insights into the code's scalability, identifying potential issues that could hinder the application's ability to scale.

![](https://digma.ai/wp-content/uploads/2024/03/image-17-768x345.png)

For example, with Scaling issue insight, developers can quickly spot performance bottlenecks related to concurrency handling and request processing times.

**Performance degradation** metrics help in identifying the load level at which the performance drops. This metric can guide developers to check the code paths executed during these transactions to find inefficiencies or resource contention.

**Concurrency Information** notifies the developer that the system experiences issues when handling concurrent processes or threads. This could suggest problems with how the application handles parallel processing or a lack of resources allocated for the application to perform optimally at this level of concurrency.

**Time Duration:** The broad range suggests that the response time can increase significantly under certain conditions, which might be due to processing bottlenecks, inefficient algorithms, database query performances, or other system limitations.

By synthesizing this information, the developer is guided toward the aspects of the application that need attention and can start forming hypotheses about what could be causing the issue, such as inefficient code, database bottlenecks, inadequate hardware resources, or suboptimal architecture decisions. The goal is to investigate and address these areas to improve the application's scalability.

3. VisualVM {#h2-10-3-visualvm}
-------------------------------

This tool was part of the Java Development Kit (JDK) until JDK 8, but it was removed in JDK 9 and is currently available as a separate tool.

This Java profiler is handy for CPU sampling, memory sampling, running garbage collections, analyzing heap errors, taking snapshots, and using a graphic user interface.

This Java profiler supports local and remote profiling but does not support profiling through SSH tunneling; you will need to configure the JMX ports for remote profiling.

VisualVM allows for snapshots of profiling sessions to be taken for later analysis.

VisualVM relies on other standalone tools that come with the JDK, such as JConsole, jstat, jinfo, jstack, and jmap.

![](https://digma.ai/wp-content/uploads/2024/03/image-19.png)

Here are three ways you can use VisualVM:

Run your Java program using predefined performance constraints

```java
java -Xmx125m -Xms25m -Xmn15m -XX:PermSize=30m -XX:MaxPermSize=30m -XX:+UseSerialGC HelloWorld
```

Run jvisualvm on the terminal and start monitoring JVM Heap Memory usage

```java
jvisualvm
```

Next, the Java VisualVM program will launch. Navigate to Tools \> Plugins and download Visual GC plugin (A selection of other plugins will also be displayed. You can use them as needed.

4. YourKit {#h2-11-4-yourkit}
-----------------------------

YourKit Java Profiler is compatible with various platforms and offers distinct installations for each supported operating system, such as Windows, MacOS, Linux, Solaris, and FreeBSD.

Like JProfiler, YourKit also includes essential functions for displaying threads, garbage collections, memory usage, and memory leaks. It supports both local and remote profiling through SSH tunneling.

![](https://digma.ai/wp-content/uploads/2024/03/image-18.png)

YourKit provides paid licenses for business purposes, including a no-cost trial and discounted or no-cost permits for personal use.

YourKit is beneficial for analyzing thrown exceptions as well. Identifying the thrown exceptions and their frequency is straightforward.

YourKit provides a distinctive CPU profiling feature that focuses on particular parts of our code, like methods or branches within threads. This feature is useful because it allows for conditional profiling through its what-if feature.

YourKit allows for the profiling of SQL and NoSQL database calls as well.

5. JProfiler {#h2-12-5-jprofiler}
---------------------------------

JProfiler is a profiling tool for Java applications created by ej-technologies. JProfiler provides interfaces for monitoring memory usage, system performance, potential memory leaks, and thread profiling with a friendly user interface.

With this information, we can readily identify optimization, removal, or modification areas in the system's foundation.

JProfiler is a profiling tool for Java applications created by ej-technologies. JProfiler provides interfaces for monitoring memory usage, system performance, potential memory leaks, and thread profiling with a friendly user interface.

With this information, we can readily identify optimization, removal, or modification areas in the system's foundation.  
![](https://digma.ai/wp-content/uploads/2024/03/image-21.png)

This Java Profiler requires the purchase of a license, but it offers a free trial. The main focus is on tackling four crucial areas:

Method calls: Analyzing method calls can provide insight into your application's functionality and help enhance its overall performance.

Allocations: By examining items stored in the heap, reference connections, and managing the waste collection, this feature allows you to address memory leaks and enhance memory efficiency.

Thread and lock: JProfiler offers various analysis perspectives on threads and locks to assist you in identifying issues with multithreading.

Advanced subsystems: Numerous performance issues arise at an elevated semantic level. With JDBC calls in Java Database Connectivity, it is vital to identify the slowest SQL statement. JProfiler allows for integrated examination of these subsystems.

JProfiler can be integrated with well-known IDEs like IntelliJ IDEA, Eclipse, and NetBeans. One can even go from a snapshot to the actual source code.

6. NetBeans Profiler {#h2-13-6-netbeans-profiler}
-------------------------------------------------

Although NetBeans is primarily recognized for its exceptional debugging capabilities, it also surprisingly stands out as one of the top Java profilers. Apache NetBeans IDE includes the NetBeans Profiler as part of its bundle. It is also an excellent option for easy development and profiling.  
![](https://digma.ai/wp-content/uploads/2024/03/image-20.png)

Combining the features of a profiler and a debugger allows for monitoring code execution time and runtime behavior and improving efficiency in debugging methods such as multi-threading. The Netbeans profiler boosts the application's speed, leading to improved memory efficiency. It's great that you can download it from their website for free.

Although Java VisualVM and Netbeans Profiler are similar in features and both are free, Netbeans stands out by offering all features in one bundled program with an IDE.

7. IntelliJ Profiler {#h2-14-7-intellij-profiler}
-------------------------------------------------

The IntelliJ Profiler is an uncomplicated yet robust tool for profiling CPU and memory allocations. It merges the capabilities of two well-known Java profilers: JFR and Async profiler.

Although a few advanced functions are available, the primary emphasis is on simplicity. IntelliJ Profiler offers a simple way to begin without setup and offers valuable tools for our daily development tasks.

![](https://digma.ai/wp-content/uploads/2024/03/image-22.png)

Within IntelliJ IDEA Ultimate, IntelliJ Profiler can be easily connected to a Java process, allowing seamless navigation between a snapshot and the source code. Other aspects, such as distinct flame graphs, enable us to evaluate the effectiveness of various methods visually and understand the runtime processes promptly and effectively.

8. Async Profiler {#h2-15-8-async-profiler}
-------------------------------------------

This Java profiling tool has minimal overhead and avoids the Safepoint bias issue. It includes APIs specific to HotSpot for gathering stack traces and monitoring memory allocations. The profiler is compatible with OpenJDK and other Java runtimes that utilize the HotSpot JVM.

async-profiler is capable of monitoring the various types of events.

* *Central Processing Unit operations*Performance counters, such as cache misses, branch misses, page faults, and context switches, monitor hardware and software performance.
* *Memory distribution in Java Heap*Contented lock trials, including that of Java object monitors and ReentrantLocks.

At the time of writing, Async Profiler only supports Linux and Mac operating systems. You don't need to install anything else if you use IntelliJ IDEA. Integration comes pre-installed and consists of:

* Initiating and terminating profiling session
* Connecting to a process that is already in progress
* Examining the profile assessment.

At the time of writing, Async Profiler only supports Linux and Mac operating systems. You don't need to install anything else if you use IntelliJ IDEA. Integration comes pre-installed and consists of:

* *Initiating and terminating profiling session*
* Connecting to a process that is already in progress
* Examining the profile assessment.![](https://digma.ai/wp-content/uploads/2024/03/image-23.png)

9. Arthas {#h2-16-9-arthas}
---------------------------

Alibaba Arthas is a tool for diagnosing Java applications, providing the ability to track, analyze, and resolve issues. A major advantage of utilizing Arthas is that there is no need to modify your code or reboot the Java services being monitored.

![](https://lh7-us.googleusercontent.com/OZqLdXeqlhxj6juSB_F6JFlWqou_gHP_1cSnuunPPZeceBez7rCBT8tCFflOI6YtfAwCIGkT33PnoyLESiYjJablxY2qg2XPcBUolKQV5uDbRARadjASoMrE5YaKu76URnYW-ejXgWWg5CHFfVPdnNU)

If you encounter issues in production, it is not possible to use IDEs to debug the application remotely. Also, debugging in a production environment is bad practice, as it will suspend all the threads, which will, in turn, suspend business services.

As a developer, you troubleshoot your production issues on the fly: no additional code changes, no JVM restart. Arthas will work as an observer, and it will not suspend your existing threads.

With a single command, you can install Arthas on Unix, Linux, and Mac.

curl -L <https://arthas.aliyun.com/install.sh> \| sh

The command above downloads the bootstrap script as.sh to the current directory. You can then move it to any directory you want or put its location in $PATH.

Other Solid Java Profilers  

Some notable mentions are Java Mission Control, New Relic, Glowroot, JMH, Arthas, XRebel/JRebel, JProbe, Pinpoint, and Stackify Prefix. While they hold a smaller portion of the market, they certainly merit recognition.

Conclusion - 9 Best Java Profilers to Use in 2024 {#h2-17-conclusion-9-best-java-profilers-to-use-in-2024}
----------------------------------------------------------------------------------------------------------

So, we found that combining YourKit, and Digma is best for the best optimization outcome: Grafana for visualizations of application logs, YourKit to profile applications for potential bottlenecks, and Digma to give seamless insights into potential problematic pieces of code.

Download Digna Continuous Feedback: [Here](https://plugins.jetbrains.com/plugin/19470-digma-continuous-feedback "Here")

<br />

<br />
