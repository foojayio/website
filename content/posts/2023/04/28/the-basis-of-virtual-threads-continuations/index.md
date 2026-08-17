---
title: "The Basis of Virtual Threads: Continuations"
slug: "the-basis-of-virtual-threads-continuations"
date: "2023-04-28T13:38:05+00:00"
lastmod: "2023-04-28T13:45:23+00:00"
description: "In Project Loom, \"continuation\" is delimited, a \"coroutine\", sequential code suspends or yields execution by itself, resumed by a caller."
authors:
  - "huseyin-akdogan"
image: "images.jpeg"
categories:
  - "Java Core"
  - "Performance"
tags:
related_posts:
  - "what-the-heck-is-project-loom-for-java"
  - "an-introduction-to-scoped-values-in-java"
  - "thinking-about-massive-throughput-meet-virtual-threads"
  - "virtual-thread-pinning-field-guide"
enlighterjs: true
frozen: false
---

**Virtual threads are lightweight implementations of java.lang.Thread and they promise to write highly scalable concurrent applications. This article turns the spotlight on the Continuations that are the basis of Virtual threads.**

[Project Loom](https://openjdk.org/projects/loom/) has been the focus of attention in the Java community since the day it was announced. Java developers were excited by Loom's promise that, with virtual threads, they could write highly scalable applications that may utilize the hardware optimally, without changing their habits.

Virtual threads were first introduced as a preview API in JDK 19, delivered to the second preview round in JDK 20, and are expected to become standard with JDK 21. Until now, you may have read many articles and listened to many presentations about virtual threads.

So, assuming you are familiar with the basic concepts, I would like to give a brief summary, instead of repeating in detail what you already know, in this article and then turn the spotlight on the Continuations that allow the Java platform to achieve a more fine-grained concurrency model.

A brief summary {#h2-0-a-brief-summary}
---------------------------------------

With Loom we now have two types of threads: Platform thread and virtual thread. While a platform thread is an instance of java.lang.Thread that's implemented in the traditional way, as a thin wrapper around an OS thread, a virtual thread is an alternative implementation of java.lang.Thread that's not tied to a particular OS thread.

The fact that platform threads are a thin wrapper around OS threads means that each traditional Java thread opens a new native thread on the OS level, thus establishing a one-to-one relationship(*1:1 scheduling*) with the OS threads.

By contrast, a virtual thread is mounted to a platform thread(therefore it is called a carrier thread) by the JVM, thus establishing a many-to-one relationship(*M:N scheduling*) between virtual threads and platform threads.

A virtual thread can remain mounted to the carrier thread until it encounters a blocking operation, and is unmounted by the JVM when a blocking operation occurs.

This means that the blocking code running in the virtual thread is not blocking the kernel thread. In this way, many virtual threads can run on the same OS thread. Because they are managed by the JVM instead of OS, the overhead of task-switching of virtual threads is close to zero.
> There are two cases where a blocking operation doesn't unmount the virtual thread from the carrier thread: 1) When the virtual thread executes a synchronized block or method code. 2) When it calls a native method or a foreign function. In these cases, the virtual thread is pinned to the carrier thread.

In addition, platform threads carry megabyte-scale chunks of memory to manage the Java call stack, while the memory footprint for virtual threads starts at just a few hundred bytes, and their stack frames are stored in the Java heap rather than in memory allocated by the OS.

All of these are what make virtual threads cheap. Therefore, a concurrent application can use hundreds of thousands or even millions of virtual threads.

Continuations {#h2-1-continuations}
-----------------------------------

In Project Loom, the word "continuation" will mean a delimited continuation, also sometimes called a "coroutine". It can be thought of as sequential code that may suspend or yield execution at some point by itself and can be resumed by a caller.

I mentioned above that the virtual threads are mounted and unmounted by the JVM, now let's make this behavior observable.

```java
class Task implements Runnable {
    private final int taskNumber;

    public Task(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public void run() {
        if (taskNumber == 1) {
            System.out.println(Thread.currentThread());
        }
        try {
            Thread.sleep(Duration.ofMillis(20));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (taskNumber == 1) {
            System.out.println(Thread.currentThread());
        }
    }
}
```


The Task object is seen above responsible for both putting the task to sleep for 20ms and if the variable taskNumber has a value of 1 printing the name of the current thread before and after this operation.

```java
var virtualThreads = IntStream.rangeClosed(1, 10)
    .mapToObj(taskNumber -> Thread.ofVirtual().unstarted(new Task(taskNumber))).toList();

virtualThreads.forEach(Thread::start);
for (Thread t : virtualThreads) {
    t.join();
}
```


When the Task object is passed to virtual threads for execution, we will get an output similar to the following.

```
VirtualThread[#21]/runnable@ForkJoinPool-1-worker-1
VirtualThread[#21]/runnable@ForkJoinPool-1-worker-7
```


From the output, we understand that the same virtual thread jumps from one platform thread it was running in at the beginning to another when it comes back from sleeping.

This is what happens in the mount and unmount process and at the core of this, there is a [Continuation](https://github.com/openjdk/loom/blob/75a5161d853893dee740bdf458f4461fc449aea1/src/java.base/share/classes/jdk/internal/vm/Continuation.java) object.

```java
// JDK core code

public Continuation(ContinuationScope scope, Runnable target) { 
    this.scope = scope; 
    this.target = target; 
}
```


When we examine the [VirtualThread](https://github.com/openjdk/loom/blob/75a5161d853893dee740bdf458f4461fc449aea1/src/java.base/share/classes/java/lang/VirtualThread.java) class, we see that a virtual thread is implemented as a continuation that is wrapped as a task and scheduled by a java.util.concurrent.Executor.

```java
// JDK core code

private static final ContinuationScope VTHREAD_SCOPE = new ContinuationScope("VirtualThreads");

// scheduler and continuation
private final Executor scheduler;
private final Continuation cont;
private final Runnable runContinuation;

    … 

VirtualThread(Executor scheduler, String name, int characteristics, Runnable task) {
    super(name, characteristics, /*bound*/ false);
    Objects.requireNonNull(task);

    // choose scheduler if not specified
    if (scheduler == null) {
        Thread parent = Thread.currentThread();
        if (parent instanceof VirtualThread vparent) {
                scheduler = vparent.scheduler;
            } else {
                scheduler = DEFAULT_SCHEDULER;
            }
        }

        this.scheduler = scheduler;
        this.cont = new VThreadContinuation(this, task);
        this.runContinuation = this::runContinuation;
}

private static class VThreadContinuation extends Continuation {
    VThreadContinuation(VirtualThread vthread, Runnable task) {
        super(VTHREAD_SCOPE, () -> vthread.run(task));
    }

    ...        
}

private void runContinuation() {

    ...

    try {
        cont.run();
    } finally {
        if (cont.isDone()) {
            afterTerminate(/*executed*/ true);
        } else {
            afterYield();
        }
    }
}
```


As can you see above, when a virtual thread is created, a continuation object is also created to represent its execution state. This object allows a virtual thread to save its current execution state and later resume from that state, typically on a different thread.

Let's look at a pure continuation example for a better grasp.

```java
public class ContinuationExample
{
    public static void main(String[] args) {

        var scope = new ContinuationScope("MyScope");

        var continuation = new Continuation(scope, () -> {
            System.out.println("Continuation running");
            Continuation.yield(scope);
            System.out.println("Continuation still running");
        });

        continuation.run();
    }
}
```


> Notice that continuations aren't exposed as a public API because it is a low-level primitive. They should only be used by library authors to build higher-level APIs such as virtual threads, the builder API to run virtual threads, etc.

When executing the above example, we will get the following output.

```
Continuation running
```


When we change the `continuation.run();` line as below and run the code again, we get a different output.

```java
while (!continuation.isDone()){
    continuation.run();
}
```


```
Continuation running 
Continuation still running
```


As can be seen from the output and mentioned above, a continuation is an object which may suspend or yield execution at some point by itself and, when resumed or invoked, carries out the rest of some computation.

When a continuation suspends, control is passed outside of the continuation, and when it is resumed, control returns to the last yield point, with the execution context up to the entry point intact.

That is what happens when a virtual thread was suspended and later resumed. Parking (*blocking*) virtual thread results in yielding its continuation, and unparking it result in the continuation being resubmitted to the scheduler.
> To provide this behavior, nearly all blocking points in the JDK have been refactored.

The needed stack frames of the virtual thread are temporarily copied from the heap to the stack of the carrier thread during the process of mounting and they are moved back to the heap during the process of unmounting.

Thanks to this behavior, the ability to capture, store and resume call stacks that are not part of kernel threads has been added to the JVM.

Moving the stack frames from the heap to the stack of the carrier thread(*i.e to main memory*) and vice-versa is the cost of blocking a virtual thread. This cost is pretty cheap compared to the cost of blocking platform threads.

Because the Java runtime can explicitly control when a virtual thread is suspended and resumed and can schedule other virtual threads to run in the meantime, this structure built on Continuations allows a more fine-grained concurrency model.

Conclusion {#h2-2-conclusion}
-----------------------------

With virtual threads, Project Loom promises that Java developers can write highly scalable applications that can utilize the hardware optimally, without changing their habits.

This promise is fulfilled by the cheap nature of virtual threads, each associated with carrier threads rather than an OS thread. A virtual thread has a continuation object that represents its execution state.

Blocking a virtual thread results in yielding its continuation and unparking it results in the continuation being resubmitted to the scheduler. Because the Java runtime can explicitly control when a virtual thread is suspended and resumed, that structure allows a more fine-grained concurrency model for Java.

References {#h2-3-references}
-----------------------------

* [Loom Proposal](https://cr.openjdk.org/~rpressler/loom/Loom-Proposal.html)
* [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)
* [Loom Wiki](https://wiki.openjdk.org/display/loom/Main)
