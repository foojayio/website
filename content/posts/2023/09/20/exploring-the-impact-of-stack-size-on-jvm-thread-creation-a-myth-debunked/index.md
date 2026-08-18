---
title: "Exploring the Impact of Stack Size on JVM Thread Creation: A Myth Debunked"
date: "2023-09-20T06:59:31+00:00"
lastmod: "2023-09-20T07:28:45+00:00"
description: "This article debunks the commonly held belief that stack size influences the number of native threads that can be created in a JVM environment."
authors:
  - "bazlur-rahman"
image: "ajda-atz-Dz4iJ3v4-X4-unsplash-scaled.jpg"
categories:
  - "Java"
tags:
related_posts:
  - "5-things-you-probably-didnt-know-about-java-concurrency"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "a-minor-but-useful-refactoring-technique-that-would-reduce-your-code-footprint-part-1"
  - "build-secure-ai-chat-applications-with-boxlang-rag-ollama-and-amazon-bedrock-with-dan-card"
frozen: false
---

**Among Java developers, a prevailing assumption is that the number of native threads that can be created within the Java Virtual Machine (JVM) is linked to the stack size. To scrutinize this widespread notion, an experiment was conducted. The results revealed that stack size plays a less significant role in native thread creation than previously thought.**

## **The Experiment**

The experiment utilized the following Java program, which continuously creates threads and counts them using an [AtomicInteger](https://download.java.net/java/early_access/jdk21/docs/api/java.base/java/util/concurrent/atomic/AtomicInteger.html).  

```java
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class ThreadCounter {
   public static void main(String[] args) {
       AtomicInteger counter = new AtomicInteger();

       while (true) {
           Thread thread = new Thread(() -> {
               counter.incrementAndGet();
               if (counter.get() % 100 == 0) {
                   System.out.printf("Number of threads created so far: %d%n", counter.get());
               }
               LockSupport.park();
           });
           thread.start();
       }
   }
}
```

## **Test Environment**

The test ran on a machine with the following configuration:

* Processor: Apple M1 Max
* Memory: 64 GB
* Operating System: macOS Vancura
* Java Version: openjdk 21

## **Experimental Results**

### **Default Stack Size**

The initial test was conducted using the default stack size of 2048 KB. Approximately 16,300 threads were created before an [OutOfMemoryError](https://download.java.net/java/early_access/jdk21/docs/api/java.base/java/lang/OutOfMemoryError.html) was triggered.

Number of threads created so far: 16300

```java
Number of threads created so far: 16300
[3.566s][warning][os,thread] Failed to start thread "Unknown thread" - pthread_create failed (EAGAIN) for attributes: stacksize: 2048k, guardsize: 16k, detached.
[3.566s][warning][os,thread] Failed to start the native thread for java.lang.Thread "Thread-16354"
Exception in thread "main" java.lang.OutOfMemoryError: unable to create native thread: possibly out of memory or process/resource limits reached
```

Calculations indicate that the memory required for these 16,300 threads was around 31.875 GB, well below the total memory of 64 GB.

### **10 MB Stack Size**

To change the stack size to 10 MB, the following JVM option was used:

    -XX:ThreadStackSize=10240

The program yielded a similar result---16,300 threads---before the OutOfMemoryError occurred again.

Number of threads created so far: 16300

```java
Number of threads created so far: 16300
[3.995s][warning][os,thread] Failed to start thread "Unknown thread" - pthread_create failed (EAGAIN) for attributes: stacksize: 10240k, guardsize: 16k, detached.
[3.995s][warning][os,thread] Failed to start the native thread for java.lang.Thread "Thread-16354"
Exception in thread "main" java.lang.OutOfMemoryError: unable to create native thread: possibly out of memory or process/resource limits reached
	at java.base/java.lang.Thread.start0(Native Method)
```

In case you want to use IntelliJ IDEA, you can configure the stack size, check the following image:   
![](Screenshot-2023-09-10-at-10.48.08-PM-1-1024x700.png)

### **1 GB Stack Size**

Finally, for the most extreme case, the stack size was set to 1 GB using:

    -XX:ThreadStackSize=1048576

Remarkably, the program again maxed out at approximately 16,300 threads, reinforcing the pattern observed in the previous tests.

Number of threads created so far: 16300

```java
Number of threads created so far: 16200
Number of threads created so far: 16300
[3.497s][warning][os,thread] Failed to start thread "Unknown thread" - pthread_create failed (EAGAIN) for attributes: stacksize: 1048576k, guardsize: 16k, detached.
[3.497s][warning][os,thread] Failed to start the native thread for java.lang.Thread "Thread-16354"
Exception in thread "main" java.lang.OutOfMemoryError: unable to create native thread: possibly out of memory or process/resource limits reached
```

## **Insights from the Data**

The consistency across all three tests underscores that stack size does not affect the number of native threads that can be created. Rather, the limitation appears to be imposed by the operating system.

It's worth noting that the OS was capable of creating more threads than the available physical memory, thanks to the concept of virtual memory. Virtual memory leverages disk space to extend RAM, allowing applications to allocate more memory than is physically present, albeit at a lower access speed.

## **Conclusion**

Contrary to the commonly held belief, this experiment proves that stack size does not have a impact on the number of native threads that can be created in a JVM environment.

The constraint is primarily set by the operating system. This investigation effectively dispels the myth that stack size is the determining factor in native thread limitations.

***Thank you for reading this. If you have a different perspective on this topic or if your experiments yield different results, I would be very interested to hear about it. Please feel free to share your findings with me.***
