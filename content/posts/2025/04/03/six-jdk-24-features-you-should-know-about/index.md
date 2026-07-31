---
title: "Six JDK 24 Features You Should Know About"
slug: "six-jdk-24-features-you-should-know-about"
date: "2025-04-03T08:42:12+00:00"
lastmod: "2025-04-07T12:23:22+00:00"
description: "Since 2018, we’ve had a new release of the Java platform every six months. With Swiss watch-like regularity, the latest version of Java is upon us. - by Simon Ritter"
canonical: "https://www.azul.com/blog/six-jdk-24-features-you-should-know-about/"
authors:
  - "simonritter"
image: "six-jdk24-features.jpg"
categories:
  - "Java"
  - "Java Core"
tags:
related_posts:
  - "foojay-podcast-68"
  - "java-24-rolls-out-today-find-out-why-its-aptly-named"
  - "java-24-whats-new"
  - "are-java-security-updates-important"
frozen: false
---

*Since 2018, we've had a new release of the Java platform every six months. With Swiss watch-like regularity, the latest version of Java is upon us. This time it's JDK 24. In an almost poetic way, it contains 24 JDK Enhancement Proposals (JEPs).*

JDK 24, which was released on March 18, 2025, has 24 JDK Enhancement Proposals (JEPs), the largest number of new features since the introduction of the time-based release schedule. While 10 of them are preview features, incubator modules, or experimental, that still leaves 14 new features.

Six new features you want to know about {#h-six-new-features-you-want-to-know-about}
------------------------------------------------------------------------------------

A JDK enhancement doesn't always add something. As you will see, some actually remove functionality that is outdated or counterproductive. I've chosen six new features that are particularly relevant and interesting for developers and those deploying Java.

### JEP 483: Ahead-of-time class loading and linking {#h-jep-483-ahead-of-time-class-loading-and-linking}

Part of the wider Project Leyden is [JEP 483, Ahead-of-Time Class Loading \& Linking](https://openjdk.org/jeps/483). The goal of [Project Leyden](https://openjdk.org/projects/leyden/) is to reduce the startup time associated with Java applications. One of Java's greatest benefits is the use of the JVM, a virtual machine that enables an application to run on any platform without recompilation. This *write once, run anywhere* approach also delivers excellent scalability and performance using just-in-time (JIT) compilation. Unfortunately, this comes with a cost, manifested as slower performance as the application *warms up*.

There are several parts to Project Leyden, and JEP 483 is the first part to be delivered in the JDK. This is related to Application Class Data Sharing, introduced in JDK 11, by making the classes of an application instantly available in a loaded and linked state when the JVM starts. This avoids the overhead of repeatedly loading, verifying, and linking class files every time an application starts.

### JEP 485: Stream gatherers {#h-jep-485-stream-gatherers}

[JEP 485, Stream Gatherers](https://openjdk.org/jeps/485), is a small but valuable change that will benefit developers. JDK 8 introduced the Streams API, which -- combined with Lambda expressions -- provided a more functional programming style in Java.

A stream consists of three parts: a source, zero or more intermediate operations, and a terminal operation. Developers have complete flexibility over what the terminal operation does by specifying arbitrary functionality implementing the Collector interface.

For intermediate operations, there is a fixed (albeit rich) set of methods available. In the same way that the Collector interface can be implemented for custom terminal operations, the Gatherer interface can now be implemented to provide custom intermediate operations.

### JEP 486: Permanently disable the security manager {#h-jep-486-permanently-disable-the-security-manager}

JEPs define enhancements to the JDK, but that doesn't always mean adding something.

[JEP 486, Permanently Disable the Security Manager](https://openjdk.org/jeps/486), removes functionality. At first sight, this particular JEP sounds troubling. Remove the security manager? Isn't that going to make the JDK *less* secure? The reality is quite different.

The security manager has been part of Java since its first release and was included for use when executing remotely loaded code. By default, all remote code is treated as untrusted, so it is unable to access resources such as the file system, network and so on. Explicit permission must be granted for each required resource and form of access.

The security manager has not been the primary means of securing client-side Java for a long time and is rarely used for securing server-side code. Long gone are the days of applets, the browser plugin removed in JDK 11. Given the security manager's maintenance cost, it was deprecated in JDK 17 and is now no longer possible to use in JDK 24. Unfortunately, some developers have used this feature, but no alternative has been provided. Migrating these applications to JDK 24 and later will potentially require major architectural changes and recoding. Although Java has excellent backwards compatibility, applications occasionally must be changed to work on newer versions.

### JEP 491: Synchronize virtual threads without pinning {#h-jep-491-synchronize-virtual-threads-without-pinning}

Virtual threads were introduced in JDK 21 to enable much greater scalability of applications that typically used the thread-per-request programming model and where those threads spent significant time being blocked. However, virtual threads experienced limitations when using synchronized methods or blocks. Even if a virtual thread were blocked, if it was in a synchronized block or method, the underlying platform thread would not be released for use by other virtual threads. This resulted from the monitor used by the synchronized method or block being associated with the platform thread rather than the virtual thread.

[JEP 491, Synchronize Virtual Threads Without Pinning](https://openjdk.org/jeps/491), resolves this limitation by moving the monitor association to the virtual thread. The JVM's implementation of the synchronized keyword has been changed so that virtual threads can acquire, hold, and release monitors independently of their carriers. The mounting and unmounting operations now handle the bookkeeping necessary to allow a virtual thread to unmount and remount when inside a synchronized method or statement or when waiting on a monitor.

### JEP 498: Warn Upon Use of memory-access methods in sun.misc.Unsafe {#h-jep-498-warn-upon-use-of-memory-access-methods-in-sun-misc-unsafe}

Since JDK 9, the encapsulation of the internal JDK APIs has increasingly tightened. In JDK 24, we take another step towards removing access completely.

The infamous sun.misc.Unsafe class has always been an issue, as it contains functionality impossible to replicate through a user-written class. All memory-related methods are now available through public APIs provided by the VarHandle API and the Foreign Function \& Memory API. [JEP 498, Warn Upon Use of Memory-Access Methods in sun.misc.Unsafe](https://openjdk.org/jeps/498), means that the JVM will now Issue a warning at run time on the first occasion that any memory-access method in sun.misc.Unsafe is invoked.

### JEP 501: Deprecate the 32-bit x86 port for removal {#h-jep-501-deprecate-the-32-bit-x86-port-for-removal}

The final JEP of interest in this article is [JEP 501: Deprecate the 32-bit x86 Port for Removal](https://openjdk.org/jeps/501), which deprecates the 32-bit x86 port for removal. In reality, this only affects the Linux version of this port, as the Windows one was removed in JDK 21 (JEP 479). Very few people still run applications on 32-bit operating systems, and it makes no sense to migrate them to a modern JDK without upgrading the OS to a 64-bit version simultaneously.

JDK 24 continues moving the Java platform forward, delivering the features developers and users will find helpful. Expect more of the same in JDK 25 (the next long-term support release, September 2025) and beyond.

Your choice of JDK matters {#h-your-choice-of-jdk-matters}
----------------------------------------------------------

Azul provides Zulu Builds of OpenJDK, both in free community form and commercially supported through the [Platform Core](https://www.azul.com/products/core) product.

Core has several unique features that differentiate it from Oracle Java SE and other OpenJDK distributions:

* 70% typically less expensive than Oracle
* Support for Java 6 and 7
* Stabilized security builds
* Guaranteed protection from copyleft contamination

Check the [Azul Quarterly Update Release Notes](https://docs.azul.com/core/release-notes) for more information on Azul Zulu 24.   

Why not give Azul Zulu 24 a try?
