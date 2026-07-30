---
title: "Superfast Application Startup: Java on CRaC"
slug: "superfast-application-startup-java-on-crac"
date: "2022-05-30T05:37:24+00:00"
lastmod: "2023-05-10T05:40:30+00:00"
description: "If you want a superfast startup for your Java applications without warmup time or resources, why not try Java on CRaC?"
authors:
  - "simonritter"
image: "https://foojay.io/wp-content/uploads/2023/05/java-crac.jpeg"
categories:
  - "CRaC"
  - "Performance"
tags:
related_posts:
frozen: false
---

It's now twenty-seven years since Java was first released, and it continues to be one of the most popular platforms for applications, especially on servers.

One of the reasons for this is the [Java Virtual Machine (JVM)](https://en.wikipedia.org/wiki/Java_virtual_machine). This provides a managed runtime environment that removes the need for developers to deal with things like memory management; the garbage collector takes care of this for you. Another significant advantage of the JVM is the use of bytecodes that can be converted to native instructions at runtime using a Just-In-Tim (JIT) compiler.

When an application starts running, the JVM looks for methods that are hot spots (hence the name of the OpenJDK JVM) and compiles them to get better performance than interpreting the bytecodes. This takes place in two phases. Initially, the C1 compiler is used, which is a fast compiler without extensive optimization. The C2 compiler is subsequently used for very hot methods, which uses profiling data collected from the running application to optimize as much as possible. Techniques like aggressive method inlining and speculative optimizations can easily lead to better performing code than generated ahead of time (AOT) using a static compiler.

This is all great, but it has the downside of the JVM needing both time and compute resources to determine which methods to compile and compiling them. This is what we refer to as the warmup time of an application. The fact that this same work has to happen *every* time we run an application makes the JVM less attractive in certain situations like microservices and serverless computing.

Ideally, we would like to run the application and then store all the state about the compiled methods and even the compiled code. This is what [Azul's ReadyNow!](https://www.azul.com/products/components/readynow/) technology does for our [Prime JVM](https://www.azul.com/products/prime/).

What about the state associated with the application itself? Often applications take time to load data and initialize required structures. Wouldn't it be wonderful if we had a way to save all this as well?

Introducing the CRaC (Coordinated Restore at Checkpoint) Project {#h-introducing-the-crac-coordinated-restore-at-checkpoint-project}
------------------------------------------------------------------------------------------------------------------------------------

This is what the OpenJDK project, [Coordinated Restore at Checkpoint (CRaC)](https://openjdk.java.net/projects/crac/), was started to investigate. Let's look at what this is and how it works.

Since 2012, the Linux operating system has had [Checkpoint/Restore in Userspace (CRIU)](https://criu.org/Main_Page). This allows a running application to be paused and restarted at some point later in time, potentially on a different machine. The overall goal of the project is to support the migration of containers. When performing a checkpoint, essentially, the full context of the process is saved: program counter, registers, stacks, memory-mapped and shared memory and so on. To restore the application, all this data can be reloaded and (theoretically) it continues from the same point. However, there are some challenges, not least of which are open files, network connections and a sudden change in the value of the system clock.

Since the JVM is just a running application, we could use CRIU and pause and restart the application running on it. However, when we started this project, we felt that to be usable, we should make the Java code *aware* that it was about to be checkpointed and that it had been restarted.

We designed a [straightforward API](https://crac.github.io/openjdk-builds/javadoc/api/java.base/jdk/crac/package-summary.html) and imposed some restrictions on an application's state when it is checkpointed. The restrictions are quite logical: the application must have no open file descriptors or network connections. This dramatically improves the ability to reliably restart an application from a given checkpoint.

Implementing the java.crac API {#h2-1-implementing-the-java-crac-api}
---------------------------------------------------------------------

To use the API, you must identify any classes in your code that are considered *resources* . These are classes that need to be notified when a checkpoint is about to be made and when a restore has happened. We provide an eponymous interface, [**Resource**](https://crac.github.io/openjdk-builds/javadoc/api/java.base/jdk/crac/Resource.html), which you implement for the identified classes. There are only two methods, `beforeCheckpoint()` and `afterRestore()`. These are used as callbacks by the JVM. If you have a class reading data from a file, you can close the file in the `beforeCheckpoint()` method (potentially also generating a checksum). In your `afterRestore()` method, you can open the file again (using the checksum to determine if the file has changed since the checkpoint was made and take further appropriate action). The same applies to network connections. You can also use these methods to deal with a sudden change in the system clock, which might impact things like cache timeouts.

All Resources in your application must be registered with the JVM. This is achieved by obtaining a CRaC [**Context**](https://crac.github.io/openjdk-builds/javadoc/api/java.base/jdk/crac/Context.html) and using the `register()` method. Although you can create your own **Context** , the simplest way is to use the global Context obtained via the [**Core**](https://crac.github.io/openjdk-builds/javadoc/api/java.base/jdk/crac/Core.html) class's static `getGlobalContext()` method. One other detail is that the order you register your Resources will be the order the **beforeCheckpoint** methods will be called. However, the **afterRestore** methods will be called in the opposite order. This simplifies things if there is a particular sequence in which things need to be prepared for a checkpoint; when restoring, you have a predictable inverse sequence. That's really all there is to the co-ordinated part of this.

Generating a Checkpoint with CRaC {#h-generating-a-checkpoint-with-crac}
------------------------------------------------------------------------

There are two ways to generate a checkpoint. The first is from outside the JVM and uses `jcmd` with the `JDK.checkpoint` command. This will initiate the checkpoint and store all the required files in the `$HOME/crac-files directory`. Most of these files will be very small, only a few Kb in size. However, be aware that if you have a well-filled large heap, you will get some big files. To restore from a checkpoint, use the command:

`java -XX:CRaCRestoreFrom=$HOME/crac-files/`

The second way to create a checkpoint is programmatically. Add a call to `Core.checkpointRestore()` where you want this to happen. The method will return when the restore has been completed.

As part of this project, we have created a proof-of-concept build of JDK 17, which can be accessed [here](https://github.com/CRaC/openjdk-builds/releases). The results from this are very promising. We tested a sample Spring Boot application and in the test environment, this took roughly four seconds before processing the first operation. Using a checkpoint of the running, warmed up application, we restored it and were able to get to the first operation in 40ms. That's two orders of magnitude faster!

Continuing Development of the CRaC Project {#h-continuing-development-of-the-crac-project}
------------------------------------------------------------------------------------------

We welcome feedback on this project and encourage others to participate in its development through the OpenJDK. There are still some details to refine, so it's not yet production-ready. Until we find a way of making this work on other platforms like Windows and macOS, it is unlikely to become part of the mainstream JDK. We've deliberately made the API agnostic to the JVM implementation to ensure that other systems for creating a checkpoint and restoring are easy to integrate.

If you want a superfast startup for your Java applications without warmup time or resources, why not try Java on CRaC?
