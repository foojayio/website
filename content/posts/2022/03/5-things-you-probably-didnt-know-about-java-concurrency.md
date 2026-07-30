---
title: "5 Things You Probably Didn’t Know About Java Concurrency"
slug: "5-things-you-probably-didnt-know-about-java-concurrency"
date: "2022-03-25T05:51:41+00:00"
lastmod: "2022-03-25T05:54:53+00:00"
description: "Even while threads are helpful, they are dreadful to many developers. Here are five essential threading concepts for Java developers!"
authors:
  - "bazlur-rahman"
image: "https://foojay.io/wp-content/uploads/2022/03/alesia-kazantceva-VWcPlbHglYc-unsplash-scaled.jpg"
categories:
  - "Java Core"
tags:
related_posts:
  - "java-thread-programming-part-1"
  - "java-thread-programming-part-2"
  - "java-thread-programming-part-3"
  - "java-thread-programming-part-13"
enlighterjs: true
frozen: false
---

Threads are at the heart of the Java programming language. When we run a "Hello World" Java program, we run on the main thread. From there, we can create threads easily, as we need to compose our application code to be functional, responsive, and performant, all at the same time.

Think about a web server: it simultaneously handles many thousands of requests at the same time. In Java, we achieve this using multiple threads. While threads are helpful, they are full of dread/thread to many developers.

That's why, in this article, I will share five interesting threading concepts that beginner and intermediate developers might not know about, yet.

1. Program Order And Execution Order Are Not The Same {#h2-0-1-program-order-and-execution-order-are-not-the-same}
------------------------------------------------------------------------------------------------------------------

When we write code, we assume it will be executed exactly the way we write it.

However, in reality, this is not the case.

The Java compiler may change the execution order to optimize it, if it can determine that the output won't change in single-threaded code.

Look at the following code snippet:

<pre class="EnlighterJSRAW" data-enlighter-language="java">package ca.bazlur.playground;
import java.util.concurrent.Phaser;
public class ExecutionOrderDemo {
    private static class A {
        int x = 0;
    }
    private static final A sharedData1 = new A();
    private static final A sharedData2 = new A();
    public static void main(String[] args) {
        var phaser = new Phaser(3);
        var t1 = new Thread(() -&gt; {
            phaser.arriveAndAwaitAdvance();
            var l1 = sharedData1;
            var l2 = l1.x;
            var l3 = sharedData2;
            var l4 = l3.x;
            var l5 = l1.x;
            System.out.println("Thread 1: " + l2 + "," + l4 + "," + l5);
        });
        var t2 = new Thread(() -&gt; {
            phaser.arriveAndAwaitAdvance();
            var l6 = sharedData1;
            l6.x = 3;
            System.out.println("Thread 2: " + l6.x);
        });
        t1.start();
        t2.start();
        phaser.arriveAndDeregister();
    }
}</pre>

The above code seems straightforward. We have two shared data ("sharedData1" and "sharedData2") and two threads use them.

When we execute the code, we assume the output would be:

<pre class="EnlighterJSRAW" data-enlighter-language="raw">Thread 2: 3
Thread 1: 0,0,0</pre>

But if you run it a few times, you will see different outputs:

<pre class="EnlighterJSRAW" data-enlighter-language="raw">Thread 2: 3
Thread 1: 3,0,3

Thread 2: 3
Thread 1: 0,0,3

Thread 2: 3
Thread 1: 3,3,3

Thread 2: 3
Thread 1: 0,3,0

Thread 2: 3
Thread 1: 0,3,3</pre>

I'm not claiming all of them can be reproducible on your machine, but all of them are possibilities.

2. Java Threads Are Limited {#h2-1-2-java-threads-are-limited}
--------------------------------------------------------------

Creating a thread is easy in Java.

However, that doesn't mean we can make as many of them as we want.

Threads are limited.

We can easily find out how many threads we can create on a particular machine by running the following:

<pre class="EnlighterJSRAW" data-enlighter-language="java">package ca.bazlur.playground;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;

public class Playground {
    public static void main(String[] args) {
        var counter = new AtomicInteger();
        while (true) {
            new Thread(() -&gt; {
                int count = counter.incrementAndGet();
                System.out.println("thread count = " + count);
                LockSupport.park();
            }).start();
        }
    }
}
</pre>

The above is simple.

It creates a thread in a loop and then parks it, which means the thread is disabled for further use, but it certainly does the system call and allocates memory.

It keeps creating threads until it cannot create any more, and then throws an exception.

Here we are interested in the number that we get before the program throws an exception.

On my machine, I was able to create only 4065 threads.

3. Having Many Threads Doesn't Guarantee Better Performance {#h2-2-3-having-many-threads-doesn-t-guarantee-better-performance}
------------------------------------------------------------------------------------------------------------------------------

Sometimes we may naively think that, since we can create threads easily in Java, that that certainly boosts application performance. Unfortunately, this assumption is flawed in the case of our traditional threading model that Java provides today.

Too many threads may, in fact, hurt application performance.

Let's ask this question first: what is the optimal maximum number of threads we can create that maximize performance of an application?

Well, the answer isn't straightforward and I wish it was. It very much depends on the type of work we are doing.

If we have multiple independent tasks and they are all computational and don't block any external resources, then having many threads will not improve performance much.

On the other hand, if we have a 8 Crore CPU, the optimal number of threads can be (8 + 1).

In such case, we may rely on the parallel stream introduced in Java 8. By default, the parallel stream uses the Fork/Join common pool.

By default, it creates threads equal to the number of available processors, which is sufficient for CPU-intensive work.

Adding more threads to CPU-intensive work where nothing blocks will not result in better performance.

Rather, we will waste resources.

Note that the reason for having an extra one is that even compute-intensive threads occasionally take a page fault or pauses for some other reason, see "[Java Concurrency in Practice](https://www.amazon.ca/Java-Concurrency-Practice-Brian-Goetz/dp/0321349601)" by Brian Goetz, page 170.

However, suppose the tasks are I/O bound, for example. In that case, they depend on external communication (e.g., database or REST API ), making more threads make sense. The reason is that when a thread waits on the REST API, other threads can go on and continue working.

Now again, we can ask, how many threads is too many threads for such a scenario?

Well, it depends. There are no ideal numbers that fit all cases. So we have to do adequate testing to find out what is the best for our particular workload and application.

However, in most typical scenarios, we usually have a mixed set of tasks. And things are completed in such cases.

In "[Java Concurrency in Practice](https://www.amazon.ca/Java-Concurrency-Practice-Brian-Goetz/dp/0321349601)," Brian Goetz provided a formula that we can use in most cases.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Number of threads = Number of Available Cores * (1 + Wait time / Service time)</pre>

Waiting time could IO, e.g., waiting for an HTTP response, acquiring Lock, and so on.

Service Time is the time of computation, e.g., processing the HTTP response, marshalling/unmarshalling, etc.

For example, an application calls an API and then processes it. If we have 8 processors on the application server, and then on average, the response time of the API is 100ms and the processing time of the response is 20ms, then the ideal size of thread would be:

<pre class="EnlighterJSRAW" data-enlighter-language="raw">N = 8 * ( 1 + 100/20)
  = 48</pre>

However, this is an oversimplification because adequate testing is always critical to figuring out the number.

4. Concurrency Isn't Parallelism {#h2-3-4-concurrency-isn-t-parallelism}
------------------------------------------------------------------------

Sometimes the terms concurrency and parallelism interchangeably, which isn't correct. Although in Java we achieve both using threads, these two are two different things.

"In programming, concurrency is the composition of independently executing processes, while parallelism is the simultaneous execution of (possibly related) computations. Concurrency is about dealing with lots of things at once. Parallelism is about doing lots of things at once."

The above definition is given by Rob Pike is pretty accurate, and see <https://www.youtube.com/watch?v=f6kdp27TYZs>.

Suppose we have absolutely independent tasks, and they can be computed separately. In that case, those tasks are said to be parallel and can be run with Fork/Join pool or parallel stream.

On the other hand, if we have many tasks, some of them may depend on one another. The way we compose and structure them are said to be concurrency. Concurrency is all about structure. We may want to progress multiple tasks simultaneously to achieve a particular result and not necessarily finish one faster.

5. Project Loom Enables Us To Create Millions Of Threads {#h2-4-5-project-loom-enables-us-to-create-millions-of-threads}
------------------------------------------------------------------------------------------------------------------------

In our previous point, I argued that having many threads doesn't mean performance gain in the application.

However, in the modern days in the microservices era, we communicate with many services to do a particular piece of work. In these scenarios, threads stay in a blocked state most of the time.

While modern OS can handle millions of open sockets at times, we cannot open many communication channels since we are limited by the number of threads.

What if we can create millions of threads and each of them will use an open socket to deal with the external communication?

That would certainly improve our throughput of an application.

I have discussed this idea in this article in detail: <https://bazlur.com/2021/06/project-loom-the-light-at-the-end-of-the-tunnel/>.

To continue with this idea, there is an initiative going on in Java called Project Loom. Using Project Loom, we can, in fact, create millions of virtual threads.

For example, using the following code snippet, I was able to create 4.5 million threads on my machine, but you can do more, based on your own machine.

<pre class="EnlighterJSRAW" data-enlighter-language="java">import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
public class Main {
    public static void main(String[] args) {
        var counter = new AtomicInteger(); 

        // 4_576_279 
        while (true) {
            Thread.startVirtualThread(() -&gt; {
                int count = counter.incrementAndGet();
                System.out.println("thread count = " + count);
                LockSupport.park();
            });
        }
    }
}</pre>

To run this program, you need to have Java 18, [which can be downloaded here](https://foojay.io/download/).

You can run using the following command -

<pre class="EnlighterJSRAW" data-enlighter-language="java">java --source 18 --enable-preview Main.java</pre>
