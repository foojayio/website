---
title: "strace Revisited: Simple is Beautiful"
slug: "strace-revisited-simple-is-beautiful"
date: "2024-11-26T14:58:28+00:00"
lastmod: "2024-11-26T15:02:49+00:00"
description: "Explore strace for Linux debugging: origins, usage, JVM insights, and advanced tips in an in-depth, easy-to-follow guide."
canonical: "https://debugagent.com/strace-revisited-simple-is-beautiful"
authors:
  - "shai-almog"
image: "DALL-E-2024-01-30-08.36.40-A-full-width-dynamic-and-colorful-digital-illustration-for-a-blog-header-focused-on-the-theme-of-system-debugging-in-a-Linux-environment.-The-image-.jpg"
categories:
  - "Tools"
  - "Tutorials"
tags:
related_posts:
  - "cant-reproduce-a-bug"
  - "the-theory-of-debugging"
  - "the-systemic-process-of-debugging"
enlighterjs: true
frozen: false
---

* [Understanding strace and its Origins](#understanding-strace-and-its-origins)
  * [**A Look Back: strace and dtrace**](#a-look-back-strace-and-dtrace)
  * [**Originating from Sun Microsystems**](#originating-from-sun-microsystems)
* [Technical Functioning of strace](#technical-functioning-of-strace)
  * [**The Role of ptrace in strace**](#the-role-of-ptrace-in-strace)
  * [**Comparing with DTrace**](#comparing-with-dtrace)
* [Practical Usage and Advantages](#practical-usage-and-advantages)
  * [**Ease of Use and Accessibility**](#ease-of-use-and-accessibility)
  * [**Favored in Linux Environments**](#favored-in-linux-environments)
* [strace in Action: A Closer Look at System Calls](#strace-in-action-a-closer-look-at-system-calls)
  * [**Basic Usage and Output Analysis**](#basic-usage-and-output-analysis)
  * [**Interpreting System Calls for Debugging**](#interpreting-system-calls-for-debugging)
* [Advanced Features and Tips](#advanced-features-and-tips)
  * [**Filtering System Calls for Efficiency**](#filtering-system-calls-for-efficiency)
  * [**Exploring a Variety of System Calls**](#exploring-a-variety-of-system-calls)
* [strace and Java: A Special Case](#strace-and-java-a-special-case)
  * [**strace with the JVM**](#strace-with-the-jvm)
  * [**Allocations and Threading in Java**](#allocations-and-threading-in-java)
* [Final Word](#final-word)

### In the realm of system debugging, particularly on Linux platforms, strace stands out as a powerful and indispensable tool. Its simplicity and efficacy make it the go-to solution for diagnosing and understanding system-level operations, especially when working with servers and containers. {#h3-0-in-the-realm-of-system-debugging-particularly-on-linux-platforms-strace-stands-out-as-a-powerful-and-indispensable-tool-its-simplicity-and-efficacy-make-it-the-go-to-solution-for-diagnosing-and-understanding-system-level-operations-especially-when-working-with-servers-and-containers}

In this article, we'll delve into the nuances of strace, from its history and technical functioning to practical applications and advanced features. Whether you're a seasoned developer or just starting out, this exploration will enhance your diagnostic toolkit and provide deeper insights into the workings of Linux systems.

{{< youtube bgi7PJXtEzc >}}

<br />

As a side note, if you like the content of this and the other posts in this series check out my [Debugging book](https://www.amazon.com/dp/1484290410/) that covers this subject. If you have friends that are learning to code I'd appreciate a reference to my [Java Basics book.](https://www.amazon.com/Java-Basics-Practical-Introduction-Full-Stack-ebook/dp/B0CCPGZ8W1/) If you want to get back to Java after a while check out my [Java 8 to 21 book](https://www.amazon.com/Java-21-Explore-cutting-edge-features/dp/9355513925/).

Understanding strace and its Origins {#h2-1-understanding-strace-and-its-origins}
---------------------------------------------------------------------------------

### A Look Back: strace and dtrace {#h3-2-a-look-back-strace-and-dtrace}

The journey of strace begins with its predecessor, dtrace, [which we covered last](https://www.amazon.com/dp/1484290410/) [time](https://debugagent.com/dtrace-revisited-advanced-debugging-techniques). However, dtrace's availability is limited, particularly on Linux systems where most server and container debugging takes place. This is where strace comes into the picture, offering a simpler yet effective alternative.

### Originating from Sun Microsystems {#h3-3-originating-from-sun-microsystems}

strace, like dtrace, traces its roots back to Sun Microsystems, emerging in the 90s (a decade before dtrace). This isn't surprising given the impressive array of technologies that originated from Sun. However, strace differentiates itself by its straightforwardness in both usage and capabilities. Unlike DTrace, which demands deep operating system support and thus remained absent as an official feature in common Linux distributions, strace thrives in the Linux environment. Its simplicity and ease of implementation make it a popular choice for Linux users, offering a distinct approach to system diagnostics.

Technical Functioning of strace {#h2-4-technical-functioning-of-strace}
-----------------------------------------------------------------------

### The Role of ptrace in strace {#h3-5-the-role-of-ptrace-in-strace}

The cornerstone of strace's functionality is the ptrace kernel feature. ptrace, pre-existing in Linux, spares users from the need to add additional kernel code or modules, a requirement often associated with DTrace. This fundamental difference not only simplifies the use of strace but also broadens its accessibility.

### Comparing with DTrace {#h3-6-comparing-with-dtrace}

While DTrace offers a more in-depth analysis through deeper kernel support, strace operates on a more surface level. This simplicity, however, does not undermine its effectiveness. strace works essentially by logging every kernel call made by a process, providing verbose but incredibly detailed insights into the system's operation. This method allows users to trace the inner workings of a process, understanding each interaction with the kernel.

Practical Usage and Advantages {#h2-7-practical-usage-and-advantages}
---------------------------------------------------------------------

### Ease of Use and Accessibility {#h3-8-ease-of-use-and-accessibility}

One of the most appealing aspects of strace is its user-friendly nature. It doesn't require special privileges or complex setup procedures. This ease of use is particularly beneficial for developers and system administrators who need to quickly diagnose and address issues in a Linux environment. Unlike DTrace, strace is readily available and doesn't demand advanced configurations or permissions.

### Favored in Linux Environments {#h3-9-favored-in-linux-environments}

strace's popularity in Linux circles is not only due to its accessibility but also its practicality. Being able to run without special privileges makes it a go-to tool for diagnosing various system-related issues. However, it's important to note that strace should be used cautiously in production environments. Its extensive logging can create a significant performance overhead, potentially impacting the efficiency of a live system. This is why strace is generally recommended for use in development or isolated testing environments rather than in production.

strace in Action: A Closer Look at System Calls {#h2-10-strace-in-action-a-closer-look-at-system-calls}
-------------------------------------------------------------------------------------------------------

### Basic Usage and Output Analysis {#h3-11-basic-usage-and-output-analysis}

Using strace is straightforward: you simply pass the command line to it.

```
strace java -classpath . PrimeMain
```


This simplicity belies its power, as the output offers a wealth of information. Each line in the strace output corresponds to a system call made by the process as you can see below:

```
execve("/home/ec2-user/jdk1.8.0_45/bin/java", ["java", "-classpath.", "PrimeMain"], 0x7fffd689ec20 /* 23 vars */) = 0
brk(NULL)                               = 0xb85000
mmap(NULL, 4096, PROT_READ|PROT_WRITE, MAP_PRIVATE|MAP_ANONYMOUS, -1, 0) = 0x7f0294272000
readlink("/proc/self/exe", "/home/ec2-user/jdk1.8.0_45/bin/j"..., 4096) = 35
access("/etc/ld.so.preload", R_OK)      = -1 ENOENT (No such file or directory)
open("/home/ec2-user/jdk1.8.0_45/bin/../lib/amd64/jli/tls/x86_64/libpthread.so.0", O_RDONLY|O_CLOEXEC) = -1 ENOENT (No such file or directory)
stat("/home/ec2-user/jdk1.8.0_45/bin/../lib/amd64/jli/tls/x86_64", 0x7fff37af09a0) = -1 ENOENT (No such file or directory)
open("/home/ec2-user/jdk1.8.0_45/bin/../lib/amd64/jli/tls/libpthread.so.0", O_RDONLY|O_CLOEXEC) = -1 ENOENT (No such file or directory)
stat("/home/ec2-user/jdk1.8.0_45/bin/../lib/amd64/jli/tls", 0x7fff37af09a0) = -1 ENOENT (No such file or directory)
```


By analyzing these calls, users can gain insights into the intricate operations of their applications. For instance, if a Java process attempts to load a library and fails, strace can reveal the underlying system call and its exit code, providing clues about potential issues like missing files or directories. E.g. in this line:

```
open("/home/ec2-user/jdk1.8.0_45/bin/../lib/amd64/jli/tls/x86_64/libpthread.so.0", O_RDONLY|O_CLOEXEC) = -1 ENOENT (No such file or directory)
```


Java tries to load the `pthread` library from the `tls` directory using a system call open to load the file. The exit code of the system call is `-1,` which means that the file isn't there. Under normal circumstances, we should get back a file descriptor value from this API (positive non-zero integer). Looking in the directory, it seems the `tls` directory is missing. I'm guessing that this is because of a missing `JCE` (Java Cryptography Extensions) installation. This is probably OK but might have been interesting in some cases.

### Interpreting System Calls for Debugging {#h3-12-interpreting-system-calls-for-debugging}

The output of strace, while verbose, is a goldmine for troubleshooting. For example, a negative exit code in a system call indicates an error, such as a missing file, which could be crucial for diagnosing issues in an application. This level of detail, although overwhelming at times, is invaluable for understanding the interactions between your application and the Linux system.

Advanced Features and Tips {#h2-13-advanced-features-and-tips}
--------------------------------------------------------------

### Filtering System Calls for Efficiency {#h3-14-filtering-system-calls-for-efficiency}

A common challenge with strace is managing its voluminous output. Fortunately, strace offers options to filter system calls, significantly enhancing its usability. By using the `-e` argument, you can instruct strace to log only specific types of system calls, such as `open` or `connect` e.g.:

```
strace -e open java -classpath . PrimeMain
```


This selective logging not only makes the output more manageable but also allows for focused troubleshooting, speeding up the debugging process.

### Exploring a Variety of System Calls {#h3-15-exploring-a-variety-of-system-calls}

strace's utility extends beyond just tracking file access or network interactions. It can be used to monitor a range of system calls, offering insights into various aspects of application behavior. By understanding and utilizing different system calls, users can gain a comprehensive view of their application's interaction with the operating system, leading to more effective debugging and optimization.

strace and Java: A Special Case {#h2-16-strace-and-java-a-special-case}
-----------------------------------------------------------------------

### strace with the JVM {#h3-17-strace-with-the-jvm}

While strace predates Java and operates at a low level with no specific awareness of the Java Virtual Machine (JVM), it remains highly effective for debugging Java applications. The JVM, like most platforms, relies on system calls for its operations, which strace can monitor and report. However, certain aspects of the JVM's behavior may be less visible to strace due to its unique approach to problem-solving.

### Allocations and Threading in Java {#h3-18-allocations-and-threading-in-java}

For instance, Java's memory management differs significantly from standard system tools. While typical applications use malloc, which directly maps to kernel allocation logic, Java manages its own memory. This approach, aimed at efficiency and streamlined garbage collection, means that some memory allocation activities are obscured from strace's view.

Similarly, Java threading is currently well-represented in strace output, but this is changing with Java 21 and Project Loom. Java 21 added support for Virtual Threads which are only partially visible to the operating system hence 1,000 threads can seem like 16 threads. These changes could affect the clarity of strace outputs in complex, heavily threaded Java applications.

Final Word {#h2-19-final-word}
------------------------------

strace stands out as an exceptionally versatile and powerful tool in the Linux debugging arsenal. Its ability to provide detailed insights into system calls makes it invaluable for diagnosing and understanding the inner workings of applications. Despite its simplicity, strace is capable of handling complex debugging scenarios, especially when used with its advanced filtering options.

For developers and system administrators working in Linux environments, strace is more than just a diagnostic tool; it's a lens through which the intricate interactions between applications and the operating system can be viewed and understood. As technologies evolve, tools like strace adapt, continuing to offer relevant and critical insights into system behaviors.

Whether you are troubleshooting a stubborn issue or simply curious about how your applications interact with the Linux kernel, strace is a tool that you will likely find yourself returning to time and again.
