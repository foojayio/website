---
title: "The Anatomy of a JVM"
slug: "the-anatomy-of-a-jvm"
date: "2023-06-16T07:57:44+00:00"
lastmod: "2023-06-16T07:57:45+00:00"
description: "Find out how the JVM handles many tasks that you, as a developer, don't want and need to take care of compared to other languages."
canonical: "https://www.azul.com/blog/the-anatomy-of-a-jvm/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2023/06/FY24-Q2-Oracle-Compete-Anatomy-of-a-JDK-hero-1.jpg"
categories:
  - "Java Core"
  - "Performance"
tags:
related_posts:
  - "what-should-i-know-about-garbage-collection-as-a-java-developer"
  - "what-does-a-modern-jvm-look-like-and-how-does-it-work"
  - "superfast-application-startup-java-on-crac"
  - "the-curious-case-of-different-runtimes-with-different-training-data-jit"
frozen: false
---

Java is not only a programming language.

It's an "umbrella" that covers tools, runtimes, and even a whole community.

In this article, we want to look at one part under the umbrella: the **Java Virtual Machine**.

What is it exactly, and how does it work?

<br />

*** ** * ** ***

### *Java is consistently in the top of most popular languages. The real reason for this is not the language itself but the JVM. And the power that it gives to the developer.* {#h3-0-java-is-consistently-in-the-top-of-most-popular-languages-the-real-reason-for-this-is-not-the-language-itself-but-the-jvm-and-the-power-that-it-gives-to-the-developer}

Simon Ritter, Deputy CTO, Azul

<br />

*** ** * ** ***

Understanding JDK versus JRE versus JVM {#TheAnatomyofaJVM-UnderstandingJDKversusJREversusJVM}
----------------------------------------------------------------------------------------------

The software world is overloaded with abbreviations, which can be confusing and lead to misuse of specific terms, so we first need to understand the difference between the following terms.
![CHART: Understanding JDK versus JRE versus JVM](/images/posts/2023/06/the-anatomy-of-a-jvm/FY24-Q2-Oracle-Compete-Anatomy-of-a-JDK-hero-1.jpg)

### JDK: Java Development Kit {#TheAnatomyofaJVM-JDK:JavaDevelopmentKit}

The JDK lives at the highest level of the Java ecosystem. It is the "source" of everything else.

Each new Java version is a new version of the JDK containing improvements, bug and security fixes, and new or removed tools. The JDK consists of several components:

* The **JVM = Java Virtual Machine** : this is the executable `java` that runs your application. How this works is described further.
* The **libraries** you use to develop Java applications are a set of +5000 classes and contain, for example, `java.util`, `java.text`, `java.nio`, `java.sql`...
* A set of **tools** that will help you to create, monitor, and run Java applications, for example:
  * `javac`: used to compile your Java classes to bytecode
  * `javadoc`: to generate HTML pages with API documentation from your Java source files
  * `jshell`: interact with and execute Java code in a Shell terminal
  * and many more...

### JRE: Java Runtime Environment {#TheAnatomyofaJVM-JRE:JavaRuntimeEnvironment}

Until Java 9, each JDK had an accompanying JRE, that contained the same JVM and libraries but only a limited set of tools. So as such, it was a subset of the JDK. The goal of the JRE was to be used on devices where the application is executed without the overhead of all the tools and was a smaller file to download and distribute.

But with Java 9, modules were introduced, which allow you to build a runtime with `jlink` that only contains the modules that are needed to run your application and create a runtime that is a lot smaller compared to the JDK and JRE.

Because many organizations still depend on a JRE approach, Azul and other providers still create JREs for newer versions. Look at the [Azul Core download page](https://www.azul.com/downloads/?package=jre) for a list of all available JREs.

What's happening inside the JVM {#TheAnatomyofaJVM-What'sHappeningInsidetheJVM}
-------------------------------------------------------------------------------

As we learned, the Java Virtual Machine (JVM) is part of the JDK, runs our application, and is called a "**managed runtime environment."** The "managed" part of this term is essential as it means it not only executes the code but handles a lot of extra functionality on top of that.

The JVM acts as a layer between your application and the machine it runs on.

### 1. Write once, run everywhere {#TheAnatomyofaJVM-WriteOnce,RunEverywhere}

This has been a promise by Java from the start. As a developer, you need to write only a single code base to execute your program on any platform, meaning both the operating system (Windows, Mac, Linux, etc.) and the hardware (x86, ARM, aarch64, etc).

To achieve this, your Java code needs to be converted to bytecode. This is your role as a developer and can be done with `javac` through build tools like Maven or Gradle. This bytecode is executed on the machine by the JVM, meaning any class can run on any platform!

To achieve this, the JVM bytecode has to be "translated" for each of the supported operating systems and platforms where specific native code is required. This means all the different implementations you, as a developer, would need to write to support different platforms are already handled by the developers of the Java system.

You can take a look at the source code to see how this is done within the Java project. All this code is freely available on GitHub. For example, on [this link](https://github.com/openjdk/jdk/tree/master/src/hotspot), you can find the different implementations of the HotSpot, as shown in a few screenshots of the project below.
![Screenshot: JVMbytecode on GitHub](https://www.azul.com/wp-content/uploads/openjdk_sources_hotspot_os-1024x287.png) ![Screenshot: JVMbytecode on GitHub](https://www.azul.com/wp-content/uploads/openjdk_hotspot_cpu-1024x345.png)

### 2. Just-In-Time compilation {#TheAnatomyofaJVM-Just-In-Timecompilation}

When your application starts, the byte code is converted directly into platform instructions using a template model in the JVM. At that moment, no optimizations are performed yet, and Java will run slower than similar native compiled code.

But simultaneously, the JVM immediately tracks how often each method is called. As soon as a predefined threshold is reached (also known as a HotSpot), the **Just-In-Time (JIT)** compiler inside the JVM starts doing its work in two phases:

* **C1 JIT** : the first conversion to native code.
  * Once a method has been detected as a Hotspot, the code is recompiled to native code.
  * This is done as quickly as possible with minimal optimization, and as soon as this native code is used, it is profiled again by the JVM to understand how it is used.
* **C2 JIT** : reaching the best possible optimization.
  * Again when a certain threshold is reached, the JVM will do a new recompilation to native code but take more time to reach maximum performance, taking the profiling into account to reach the best performance.
  * At this point, the generated native code is a perfect match to how the code is used.
  * This means that the same code can result in different native codes, depending on the environment it's running on, what data it needs to process, which events are triggering the execution of the code, etc.
  * Azul Zulu Prime contains an alternative to C2: **[Falcon JIT](https://www.azul.com/products/components/falcon-jit-compiler/)** , based on the open-source project [LLVM](https://llvm.org/), and, in most cases, results in better-performing code compared to traditional C2. For more info, see ["Using the Falcon Compiler"](https://docs.azul.com/prime/Falcon-Compiler) in the Azul docs.

When looking at the graph below, we can see how the speed of the application improves when shifting from interpreted bytecode (yellow), to the first optimized code (C1, green), resulting in the best performance with the optimized code (C2, blue).

More info about this topic and how an application can be "tuned" to improve these different steps is available on "[Analyzing and Tuning Warm-up](https://docs.azul.com/prime/analyzing-tuning-warmup#an-introduction-to-jit-compilation)".
![CHART: the speed of an application improves when shifting from interpreted bytecode (yellow), to the first optimized code (C1, green), resulting in the best performance with the optimized code (C2, blue).](https://www.azul.com/wp-content/uploads/chart-speed-optimization-level-1024x538.png)

JIT is the opposite of **Ahead-Of-Time (AOT)** and typically behaves better when the produced native code gets optimized for what it exactly needs to do.

If you would like to learn more about this process of how bytecode is converted into native code, you can read these blogs by Simon Ritter:

* [Understanding Java Compilation: From Bytecodes to Machine Code in the JVM](https://www.azul.com/blog/understanding-java-compilation-from-bytecodes-to-machine-code/)
* [A Matter of Interpretation: From Bytecodes to Machine Code in the JVM](https://www.azul.com/blog/a-matter-of-interpretation-from-bytecodes-to-machine-code-in-the-jvm/)

### 3. Memory management {#TheAnatomyofaJVM-MemoryManagement}

Another responsibility of the JVM is automated **memory management**. In other programming languages like C, C++,... you as a developer need to allocate (malloc) and free memory, and if you don't do this correctly, memory issues can occure.

The Garbage Collector (GC) is that part of the JVM that will handle this for you. It frees you as a developer entirely from the worries of managing the use of the memory. The GC will periodically look up the objects that are no longer needed and referenced them from the code and free memory space so they can be reused.

You can find an entire article about the Garbage Collector here: [What Should I Know About Garbage Collection as a Java Developer?](https://www.azul.com/blog/what-should-i-know-about-garbage-collection-as-a-java-developer/)
[![What Should I Know About Garbage Collection as a Java Developer?](https://www.azul.com/wp-content/uploads/11-23-garbage-1024x400.jpg)](https://www.azul.com/blog/what-should-i-know-about-garbage-collection-as-a-java-developer/)

### 4. Thread management {#TheAnatomyofaJVM-ThreadManagement}

**Managing threads** and interaction between threads is also a topic handled by the JVM.

OpenJDK 19 contains the first evaluation version of Project Loom and Virtual Threads, introducing multilevel threads inside Java independent of the operating system threads. This will create a new dynamic for how threads are used in Java.

Being independent of the number of operating system threads allows much greater efficiency and will have a significant impact on scalability and streaming or messaging applications.

More info is available here: [JDK 19 and What Java Users Should Know About It](https://www.azul.com/blog/jdk-19-and-what-java-users-should-know-about-it/).

### 5. Statically versus dynamically {#TheAnatomyofaJVM-Dynamicclassloading}

Another often-heard term related to Java says it is a "**statically typed language** ". The "statically" refers to how variables are used. When you declare a new value, you need to define a type, which can't be changed later. For example, `String label = "Hello, World!"` will not allow you to assign a number to the label variable later.

This is different compared to, for instance, JavaScript. For new Java programmers coming from a dynamically typed language, this can be confusing, but it prevents many problems and bugs when the code is executed.

Keep in mind that "statically" is linked to how values are defined because, on the opposite, **classes are loaded at runtime in a "dynamic" way**! This allows the application to behave correctly depending on the environment or specific settings.

For instance, based on environment settings, you can use different databases during testing versus in-production. In each case, other classes can be loaded dynamically to interact with the database.

What are "Azul Builds of OpenJDK?" {#TheAnatomyofaJVM-WhatisAzulBuildsofOpenJDK}
--------------------------------------------------------------------------------

All Java runtimes have to behave the same way, so that you, as a user, are guaranteed that your application will produce the same results, independent of which JDK you are using.

To achieve this, every distribution has to comply with the Java SE specification as defined by the relevant Java Specification Request (JSR) through the Java Community Process (JCP).

To verify this, distributions must pass all the tests of the Java Technology Compatibility Kit ([TCK](https://en.wikipedia.org/wiki/Technology_Compatibility_Kit)). This doesn't mean all distributions are implemented the same way, although most are based on the OpenJDK!

**Azul provides two distributions** that are TCK-compliant but very different.

### Azul Zulu Build of OpenJDK (aka Zulu) {#TheAnatomyofaJVM-AzulZuluBuildofOpenJDK(akaZulu)}

* This build of OpenJDK is almost *identical to OpenJDK*.
* It has no functional changes.
* But it does contain bug fixes and security improvements for older versions. For example, Azul is the only distributor that still creates new versions of JDK 7 and backports security and bug fixes into it.
* Azul provides a JRE distribution for all versions.
* Azul also is still creating updated and further maintained releases of versions that are no longer supported by almost all distributors (JDK 7, for instance).
* It is available as:
  * **Azul Zulu Builds of OpenJDK** :
    * Drop-in replacement for any OpenJDK.
    * Free to download and use.
  * **Azul Platform Core** :
    * Azul Zulu Builds of OpenJDK + support + upgrades + extra tools.
    * Available in a free version for evaluation and development and a licensed version.

### Azul Prime Builds of OpenJDK (aka Prime) {#TheAnatomyofaJVM-AzulZuluPrimeBuildofOpenJDK(akaPrime)}

* This distribution is *based on OpenJDK*.
* But there are functional changes:
  * All Garbage Collector implementations of OpenJDK have been removed and replaced with the Azul C4 Garbage Collector.
  * The C2 JIT compiler is not removed, but the Azul Falcon compiler is added, and you can select which one is used with startup options.
* Extra functions are added inside the JDK:
  * Connected Runtime Service (CRS) can connect to [Azul Vulnerability Detection](https://www.azul.com/products/vulnerability-detection/) to help you identify security issues in real-time.
  * [ReadyNow!](https://www.azul.com/products/components/readynow/): a technology that enables Java applications to start up fast and keep running fast.
* It is available as **Azul Platform Prime** :
  * Azul Prime Builds of OpenJDK + C4 + Falcon + ReadyNow! + support + upgrades + extra tools.
  * Available in a free version for evaluation and development and a licensed version.

The Azul JDK distributions can be downloaded from ["Download Azul JDKs](https://www.azul.com/downloads/?package=jdk)."

Conclusion {#TheAnatomyofaJVM-Conclusion}
-----------------------------------------

The JVM handles many tasks that you, as a developer, don't want and need to take care of compared to other languages.

**This will help you to focus on the business logic and the actual implementation work**!
