---
title: "A Comprehensive Guide to Java Virtual Threads (Part 1)"
slug: "unleashing-the-power-of-lightweight-concurrency-a-comprehensive-guide-to-java-virtual-threads-part-1"
date: "2023-05-09T08:33:12+00:00"
lastmod: "2023-09-11T17:51:15+00:00"
description: "Learn about Java Virtual Threads, how they simplify concurrent programming, how they overcome the limitations of traditional Java threads."
authors:
  - "bazlur-rahman"
image: "https://foojay.io/wp-content/uploads/2023/05/2945feb6-1c67-4a75-9dd9-4b8d0cd1f6e5.jpeg"
categories:
  - "Java Core"
  - "Performance"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Introduction {#h2-0-introduction}
---------------------------------

Java Virtual Threads, also known as lightweight threads, is an exciting new feature introduced in Project Loom.

Virtual threads aim to simplify concurrent programming in Java by providing an efficient and easy-to-use concurrency model.

In this article, we'll cover the basics of Java Virtual Threads, how they work, why they are beneficial for developers, and how they overcome the limitations of traditional Java threads.

What are Virtual Threads? {#h2-1-what-are-virtual-threads}
----------------------------------------------------------

Java is made of threads. When we run a Java program, its main method is invoked as the first call frame of the main thread created by the Java launcher. It gives us many things: sequential control flow, local variables, exception handling, single-step debugging, and profiling.

It makes our lives easier by providing exception handling with informative stack traces and serviceability tools that let us observe what's happening in each thread, providing remote debugging, and creating an illusion of sequentiality that makes our code easier to reason.

Most JVM implementations today implement Java threads as thin wrappers around operating system threads. We call these heavyweight, OS-managed threads "platform threads." Operating systems typically allocate thread stacks as monolithic memory blocks at thread creation time that cannot be resized later---generally 2 MB (on Linux). One million threads would require two terabytes of memory! It essentially means that we can't have a lot of them.

In a server application, a thread is assigned to each incoming request. This approach scales well for moderate-scale applications, e.g., 1000 concurrent requests, but cannot survive 1M concurrent requests, even though we have adequate CPU capacity and IO bandwidth.

Virtual threads are an alternative implementation of Java threads that store their stack frames in Java's garbage-collected heap rather than in monolithic blocks of memory allocated by the OS. It starts out at only a few hundred bytes and expands and shrinks automatically.

The operating system only knows about platform threads, which remain in the scheduling unit. To run code in a virtual thread, the Java runtime arranges for it to run by mounting it on a traditional thread called a "carrier thread."
![](/images/posts/2023/05/unleashing-the-power-of-lightweight-concurrency-a-comprehensive-guide-to-java-virtual-threads-part-1/Screenshot-2023-09-11-at-1.49.43-PM-1024x772.png) Internals of Virtual Threads

When code running in a virtual thread would otherwise block for IO, locking, or other resource availability, it can be unmounted from the carrier thread, and any modified stack frames copied back to the heap, which frees the carrier thread to run something else.

With this virtual thread, we get all the benefits traditional threads have, plus it is cheap, lightweight, and virtually free. Moreover, we can create many of them.

Consider the following example:

<br />

```EnlighterJSRAW
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) 
    IntStream.range(0, 10_000).forEach(i -> {
        executor.submit(() -> {
            Thread.sleep(Duration.ofSeconds(1));
            return i;
        });
    });
}

```

The JDK can now run up to 10,000 concurrent virtual threads on a small number of operating system (OS) threads, as little as one, to execute the simple code above that involves sleeping for one second.

Why Use Virtual Threads? {#h2-2-why-use-virtual-threads}
--------------------------------------------------------

1. **Scalability:** Since a large number of virtual threads are easy to create, the thread-per-request programming style alleviates this scalability bottleneck. In other words, high throughput is very simple to achieve.
2. **Simplified Concurrency**: Virtual threads make concurrent programming in Java easier by allowing developers to write code using familiar synchronous APIs. This eliminates the need for complex asynchronous programming patterns, such as callbacks, promises, or reactive programming, which can be difficult to understand and maintain.
3. **Improved Resource Utilization:** Virtual threads help maximize resource utilization by ensuring that kernel threads are not idle while waiting for I/O operations to complete. By efficiently managing the execution of virtual threads, the Java runtime can keep kernel threads busy, resulting in better overall performance.

<br />

Getting Started with Virtual Threads {#h2-3-getting-started-with-virtual-threads}
---------------------------------------------------------------------------------

Project Loom is still an experimental feature and is not available in stable Java releases. JDK 21, which is still not released, is expected to include virtual threads.

To manage your JDK installations, we recommend using SDKMAN, a versatile tool that simplifies the process of installing, switching, and managing multiple JDK versions. You can find more information about SDKMAN and installation instructions on their official website: <https://sdkman.io/>

Once you have SDKMAN installed, you can list available JDK versions, including early access builds, and install the desired version:

```EnlighterJSRAW
sdk list java
sdk install java <version>

```

Replace \<version\> with the specific version you'd like to install, such as the early access build of JDK 21 that includes virtual thread support.

Once you have downloaded JDK 21, you can create virtual threads in three ways:

*** ** * ** ***

### Using the Thread Factory method {#h3-4-using-the-thread-factory-method}

<br />

```EnlighterJSRAW
Thread.startVirtualThread(() -> 
   System.out.println("Hello world!");
});

```

You don't need to start the thread; it automatically starts and executes.

Note that virtual threads are always daemon threads. So make sure you wait on the main thread. Otherwise, you may not see the output.

<br />

```EnlighterJSRAW
var thread = Thread.startVirtualThread(() -> 
   System.out.println("Hello world!");
});

thread.join();

```

*** ** * ** ***

### Using the builder method {#h3-5-using-the-builder-method}

Like the factory method, you can use the builder method, which is much more convenient as you can create **started** or **unstarted** threads.

<br />

```EnlighterJSRAW
var started =Thread.ofVirtual().start(() -> 
   System.out.println("Hello world!");
});

```

To create an **unstarted** thread, you can use the following:

<br />

```EnlighterJSRAW
var unstarted = Thread.ofVirtual().unstarted(() -> 
   System.out.println("Hello world!");
});

```

*** ** * ** ***

### Using the Executors {#h3-6-using-the-executors}

If we want to move away from our existing code, which is heavily dependent on executors, one new method that is just one line long and uses virtual threads has been added.

<br />

```EnlighterJSRAW
var executorService = Executors.newVirtualThreadPerTaskExecutor()

executorService.submit(() -> {
   System.out.println("Hello world!");

});

```

*** ** * ** ***

Conclusion {#h2-7-conclusion}
-----------------------------

Java Virtual Threads provide a powerful and efficient concurrency model for modern applications. By simplifying concurrent programming and allowing for better resource utilization, virtual threads have the potential to revolutionize the way developers write concurrent code in Java.

As Java continues to evolve and innovate, staying informed about cutting-edge features like virtual threads is crucial for developers seeking to stay ahead of the curve and harness the full potential of the Java ecosystem.

If you found this article to be informative and engaging, please consider giving it a thumbs up and sharing it with your colleagues and friends. Thanks for your support!
> **Note:** In the next article, we will delve deeper into the implementation and explain the internals of the virtual threads introduced in this article. Stay tuned.

<br />
