---
title: "Faster Java Warmup: CRaC versus ReadyNow"
slug: "faster-java-warmup-crac-versus-readynow"
date: "2025-06-06T17:23:00+00:00"
lastmod: "2025-06-13T07:58:31+00:00"
description: "This is the first blog post in a series on faster Java application warmup with ReadyNow.Azul has developed different solutions to help achieve faster Java - by Frank Delporte"
canonical: "https://www.azul.com/blog/faster-warmup-of-your-java-applications-crac-versus-readynow/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "CRaC"
  - "Java"
  - "Performance"
tags:
related_posts:
  - "azul-brings-java-from-edge-to-cloud"
  - "analyzing-and-tuning-warm-up-of-the-jvm-with-azul-zulu-prime-builds-of-openjdk"
  - "azul-enhances-readynow-to-solve-javas-warmup-problem-simplify-operations-and-optimize-cloud-costs"
  - "introducing-the-openjdk-coordinated-restore-at-checkpoint-project"
frozen: false
---

*This is the first blog post in a series on faster Java application warmup with ReadyNow.*   
*Azul has developed different solutions to help achieve faster Java application warmup, including Coordinated Restore at Checkpoint (CRaC) and ReadyNow. While CRaC and ReadyNow aim to solve the same challenge, they take different approaches. This post explains the differences.*

One of the challenges in a Java system is application warmup.

Java is the beating heart of many applications. It powers countless applications, from small business processes on a single machine to massive clusters handling vast amounts of data and transactions. Azul optimizes performance, especially Java warmup in complex setups and time-sensitive use cases.

One of the challenges in a Java system is application "warmup." Java applications start with Java byte code (from your JAR file) and convert it to the most optimal native code for the system they run on. This means your application can perform its tasks as expected from the start but gets better and faster after learning which code is used the most and how it is used.

Azul has developed different solutions to help applications reach the maximum possible performance faster with Java warmup:

* **Coordinated Restore at Checkpoint (CRaC)**: create a checkpoint and restore from a checkpoint.
* **ReadyNow**: store compiler decisions to reuse them in the following run.
* **Cloud Native Compiler**: offload compilations to a central service.

While CRaC and ReadyNow aim to solve the same challenge, they take different approaches. In this post, I will explain the differences.

What happens at Java startup {#What-Happens-at-Java-Startup}
------------------------------------------------------------

Java application startup involves a few different tasks:

1. The JVM (the `java` program) itself needs to start.
2. The JVM needs to load the application classes (from the `jar`), initialize all the resources, and start the application logic.
3. Execute your code and optimize it for the machine it's running on.

The combined duration of tasks 1 and 2 is called the "**time to first response**." At that moment, the application replies to API calls, handles incoming messages, and performs other important functions.

In step 3, we see a performance improvement as soon as the code gets compiled into native code, called the "warmup."
![](/images/posts/2025/06/faster-java-warmup-crac-versus-readynow/java-total-warm-up-process.avif)

### Java warmup phase {#Warm-Up-Phase}

A Java application goes through three phases when it starts executing the code, which leads to the warmup:

1. The byte code (class files) in your JAR is executed "as is" by the Java Virtual Machine (JVM).
2. The JVM tracks the number of times each method is used. As soon as a threshold is reached, the **Tier 1** compiler converts the byte code to native code as fast as possible.
3. In the meantime, the JVM keeps tracking how the code is used. For example, maybe a few `case`s in a `switch` are never used. Based on this knowledge, after another threshold, the code is again converted to native code, leading to the best possible native code for the system it is running. This happens by the **Tier 2** compiler.

After this Java warmup phase, we reach the "**time to full speed**."
![](/images/posts/2025/06/faster-java-warmup-crac-versus-readynow/java-warmup-chart.avif)

### Deoptimizations {#Deoptimizations}

The Tier 2 compiler makes certain decisions based on how the code is used. But if some of those are wrong, the compiled code gets deoptimized and goes through the compilation steps again. In the example we used before, one of those non-compiled `case`s could turn out to be needed, e.g., when the application starts handling different data. This can lead to minor performance drops because the execution falls back to the byte code before it has been recompiled.
![](/images/posts/2025/06/faster-java-warmup-crac-versus-readynow/java-deoptimizations.avif)

This effect is minimal and, in most cases, not even noticeable. However, in some industries where timing is critical, for instance, financial transactions, such deoptimizations should be avoided at all costs.

Solutions Provided by Azul {#Solutions-Provided-by-Azul}
--------------------------------------------------------

As the largest company 100% focused on Java and the JVM, Azul developed several solutions to accelerate Java applications to their optimal speed.

### Coordinated Restore at Checkpoint (CRaC) {#Coordinated-Restore-at-Checkpoint}

Coordinated Restore at Checkpoint (CRaC) is an OpenJDK project implemented in the Azul Zulu Builds of OpenJDK (Zulu). Once an application runs and is warmed up, you can instruct it to create a checkpoint. Such a checkpoint contains a "dump" of the application's state. You can restart your application using this checkpoint, bringing it back to the state where it was when the checkpoint was created, without going through Java warmup again.

To create a checkpoint, the application first needs to close any open connection (to a file, database, socket, etc.). Then it can create the checkpoint, after which the application gets terminated. Some frameworks (like Quarkus, SpringBoot, Micronaut, and AWS Lambdas) have integrated CRaC to simplify this process.

A checkpoint can be restarted on the same or other machines with the same architecture. After a restore, it needs to reopen the connections but it's almost immediately ready to perform with the same performance as in the previous run.

To use the CRaC system, you will need to change your code to handle the closing and reopening of connections.

**CRaC removes the Java warmup phase by storing a complete checkpoint of the running application. This checkpoint can be restored quickly to the same state for the subsequent runs.**

|                                                                                                                                                     READ MORE                                                                                                                                                     |
|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [What Is CRaC?](https://docs.azul.com/core/crac/crac-introduction) [Coordinated Restore at Checkpoint (CRaC)](https://www.azul.com/products/components/crac/) [Reduce Java Application Startup and Warmup Times with CRaC](https://www.azul.com/blog/reduce-java-application-startup-and-warmup-times-with-crac/) |

### ReadyNow {#ReadyNow}

ReadyNow is a feature in Azul Zing Builds of OpenJDK (Zing), a modern, TCK-compliant Java platform based on OpenJDK with extended functionalities. ReadyNow helps the compiler to produce the best possible native code right from the start of the application. ReadyNow is available for customers of [Azul Platform Prime](https://www.azul.com/products/prime/), Azul's high-performance OpenJDK distribution.

One of the significant advantages of Zing is that every compilation step can be saved in a log file, referred to as a profile. Engineers can use these profiles to research specific problems, analyze them with tools (e.g., [GC Log Analyzer](https://docs.azul.com/gc-log-analyzer/)), and -- most importantly -- reuse them in the next run of an application to compile the code immediately into its best-performing native version. At each start of the application, Zing can be configured with a profile as an input with a command line option and, optionally, a new output profile. ReadyNow instructs the JVM which code has to be compiled, with the info on how it was used in the previous run(s). This way, multiple generations of a profile can be created to get to a perfect "trained" profile. This will contain all the required information to generate the best possible native code from the start and avoid deoptimizations that can cause short performance drops.

Because this feature is integrated into Zing, **no code changes are needed to use ReadyNow**! You only need to provide a few command-line options to access this functionality.

Because ReadyNow records compilation decisions throughout your application's entire life, it can help the JVM generate optimized code that can handle all the traffic seen in the last run without any deoptimizations. As such, it can avoid all potential micro-delays caused by such deoptimizations. It's important to note that the longer the training run that creates the profile, the better the result will be in the following runs.

For large-scale deployments, Azul offers the additional [Optimizer Hub](https://www.azul.com/products/components/azul-optimizer-hub/) service that can offload the storage of the ReadyNow profiles and the compilation to a dedicated environment.

**ReadyNow removes the Java warmup phase by keeping a history of compiler decisions from a previous run in a text file. This allows it to create the optimal optimized code immediately at the application's startup. It also avoids all deoptimizations because it knows exactly how the code is used based on previous runs.**

|                                                                                                                                          READ MORE                                                                                                                                          |
|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [About ReadyNow](https://docs.azul.com/prime/Use-ReadyNow) [ReadyNow Start Up Faster and Stay Fast](https://www.azul.com/products/components/readynow/) [GC Log Analyzer Documentation](https://docs.azul.com/gc-log-analyzer/) [About Optimizer Hub](https://docs.azul.com/optimizer-hub/) |

### Cloud Native Compiler {#Cloud-Native-Compiler}

Cloud Native Compiler is a component of Optimizer Hub with Azul Platform Prime that provides a server-side optimization solution that offloads JIT compilation to separate and dedicated resources. This approach offers more processing power to the JIT compilation while freeing your client JVMs from the burden of doing JIT compilation locally.

As such, it relates to the challenges mentioned in this article but complements ReadyNow.

|                                                                                                                 READ MORE                                                                                                                 |
|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| [About Cloud Native Compiler](https://docs.azul.com/optimizer-hub/about/cloud-native-compiler) [Cloud Native Compiler: Powerful optimizations at a dramatically reduced cost](https://www.azul.com/products/prime/cloud-native-compiler/) |

CRaC Compared to ReadyNow {#CRaC-Compared-to-ReadyNow}
------------------------------------------------------

Let's summarize the differences between CRaC and ReadyNow:

|                                       |                                                 CRaC                                                 |                                 ReadyNow                                 |
|---------------------------------------|------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------|
| **Available in**                      | Zulu                                                                                                 | Zing                                                                     |
| **Needs code changes**                | Yes, or must be based on a framework that supports CRaC (Spring, Micronaut, Quarkus,...) (\*) (\*\*) | No                                                                       |
| **Required space**                    | Heap size of the application + additional storage                                                    | Text file with limited size                                              |
| **Complexity**                        | Can be difficult to debug                                                                            | Extended debug options thanks to GC-log files and the profile text files |
| **Can handle Java patterns (\*\*\*)** | Yes                                                                                                  | Yes                                                                      |
| **Availability**                      | Basic functionality in free builds. Extended functionality is available to Azul customers.           | For customers of Azul                                                    |

(\*): Even when using a framework, you can still have dependencies that need code changes.

(\*\*): Some CRaC implementations in frameworks take a snapshot at "Time to first transaction" (calling the `main` method). So, although you'll get a startup time improvement with such an approach, you still will not reach the optimal "Time to full speed."

(\*\*\*): Unlike native compiled applications (with GraalVM), both CRaC and ReadyNow still have the original byte code to optimize the code further when needed, can dynamically load classes, etc.

Conclusion {#Conclusion}
------------------------

Although CRaC and ReadyNow are both solutions to shorten Java applications' warmup phase, they take a very different approach. In cases where no code changes are possible, server systems need to be available as fast as possible, and increased performance and/or reduced total cost of ownership are required, ReadyNow is the perfect choice to improve Java performance!

Learn more about CRaC and ReadyNow in our documentation and on our website. Keep an eye on the Azul blog for more posts exploring ReadyNow functionality.

<br />

**Next:** [Understanding How ReadyNow Improves Warmup Time](https://foojay.io/today/how-readynow-improves-java-warmup-time/)

|                                                                                                                                                                      READ MORE                                                                                                                                                                      |
|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Documentation** [What Is CRaC?](https://docs.azul.com/core/crac/crac-introduction) [About ReadyNow](https://docs.azul.com/prime/Use-ReadyNow) **Product** [Coordinated Restore at Checkpoint (CRaC)](https://www.azul.com/products/components/crac/) [ReadyNow Start Up Faster and Stay Fast](https://www.azul.com/products/components/readynow/) |
