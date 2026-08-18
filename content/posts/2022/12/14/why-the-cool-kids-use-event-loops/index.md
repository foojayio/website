---
title: "Why the Cool Kids Use Event Loops"
date: "2022-12-14T10:17:48+00:00"
lastmod: "2023-01-19T08:43:02+00:00"
description: "Check out some of the key points to consider, as well as a full code sample, when choosing to use event Loops!"
authors:
  - "rob-austin"
image: "Favicon-3-2.png"
categories:
  - "Java Core"
related_posts:
  - "how-object-reuse-can-reduce-latency-and-improve-performance"
  - "low-latency-crypto-trading-systems-using-java-and-chronicle-services"
  - "kafka-vs-chronicle-for-microservices-which-is-750-times-faster"
  - "lets-replace-the-synchronized-keyword"
frozen: false
---

### A Discussion On the Benefits of Event Loops in Java.

When I was working in software development back in the 1990s, nearly all the software libraries that I worked on made use of event loops. This was because at the time most hardware had just one single CPU.

Back in the day, I remember the excitement when threads were introduced into our development framework. It was revolutionary that we could now run two things at once, or rather appear to run two things at once, since a lot of the hardware at that time still only had a single core, hence our threaded code was never really truly concurrent.

Over the years I've had mixed feelings about threads, some of the most challenging systems that I've maintained have suffered from the overuse or misunderstood impact of concurrency.

Even today I have discussions around if a piece of code is truly thread-safe and although the libraries (for example, the [Java Concurrency Library](https://docs.oracle.com/javase/7/docs/api/index.html?java/util/concurrent/package-summary.html "Java Concurrency Library")) has made massive improvements reducing the burden of developing with threads, it is still somewhat of a challenge to ensure that we are not calling code which is not thread-safe, when we have assumed it is.

This is something that is generally not easily picked up by either static analysis or software compilers.

**I have been contributing to the open-source project [Chronicle Threads](https://github.com/OpenHFT/Chronicle-Threads "Chronicle Threads"), and we have gone retro all the way back to the 1990s and embraced event loops: if it was good enough for the old-timers maybe it's good enough for us today.**

Below are some of the key points to consider when choosing to use event Loops:

**1. Lock Free**

By removing threads, we can reduce the overhead of concurrency locking; lock-free code often runs faster and single-threaded code is usually simpler to write and test.

**2. Testing and Evolving Requirements**

Much higher confidence can be gained in single-threaded test cases; leading to fewer bugs, more stable code.

In addition, as your requirements evolve it is easier to maintain and extend business cases.

**3. Shared Mutable State**

If we are using a single-threaded event loop, it makes it very easy to access and modify mutable state between requests. A common approach to reducing multi-threaded complexity is by using immutable objects, however creating immutable objects can ( in some cases ) impact the performance of your application.

On the flip side, multi-threaded solutions often have to signal/wait or exchange state; this reduces real-world scaling with the number of threads.

**4. CPU Isolation and Thread Affinity**

Event loops do have a slight overhead as the event loop has to be managed, but this can be balanced with the advantage of running code on fewer cores, which ensures that the thread scheduler is not having to context switch between threads.

Each context switch requires the stack frame and registers to be stored, and later this state has to be loaded before the thread continues. If we adopt an event-loop design the thread context switching can be reduced. But this alone will not prevent threads from context switching entirely, as other processes can be scheduled to also run on the same core.

To eliminate the context switching, we can pin our thread with a [thread affinity library](https://github.com/OpenHFT/Java-Thread-Affinity "thread affinity library") and then apply [cpu isolation](https://www.oreilly.com/library/view/linux-kernel-in/0596100795/re46.html "cpu isolation") to ensure nothing else runs on that core. Pinning a thread can also reduce cache contention, which is when two threads, running on the same core, are forced to spend time writing data into the L1 and L2 caches, only for the other thread to overwrite it.

**5. Event Driven Architecture**

If you are using an event loop as part of an event driven architecture, the [event loops](https://en.wikipedia.org/wiki/Event_loop "event loops") can be used to read messages and dispatch messages to event handlers.

"Building systems around an event-driven architecture (EDA) simplifies horizontal scalability in distributed computing models and makes them more resilient to failure." -- [Wikipedia](https://en.wikipedia.org/wiki/Event-driven_architecture "Wikipedia")

**6. Resource Utilization**

Resource utilisation is likely to be higher when using a single-threaded event loop. For Example: When implementing an EDA architecture, while there are still events to process on the event loop, the core will remain busy. There is no context switching, signalling or waiting for state from another core.

In summary, single-threaded event loops can still be scaled by striping the event handlers in their own event loop, which in turn is bound to its own core, each stripe runs independently.

This approach can be applied to a wide range of use cases -- for example, the [Chronicle Matching Engine](https://chronicle.software/matching-engine/ "Chronicle Matching Engine") which has excellent performance and implementation simplicity, and we are able to scale by running any number of independent engines as needed to meet demand.

### Code Example

To illustrate how you can use the Chronicle Event loop in your code we have put together a code example called [SingleAndMultiThreadedExample](https://github.com/OpenHFT/Chronicle-Threads/blob/develop/src/test/java/net/openhft/chronicle/threads/example/SingleAndMultiThreadedExample.java "SingleAndMultiThreadedExample"):

```java
package net.openhft.chronicle.threads.example;

import net.openhft.chronicle.core.threads.EventLoop;
import net.openhft.chronicle.core.threads.InvalidEventHandlerException;
import net.openhft.chronicle.threads.MediumEventLoop;
import net.openhft.chronicle.threads.Pauser;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.Executors.newCachedThreadPool;

/**
 * An example that was used in a DZone article
 */
public class SingleAndMultiThreadedExample {

    private AtomicLong multiThreadedValue = new AtomicLong();
    private long singleThreadedValue;

    /**
     * The two examples in this code do the same thing, they both increment a shared counter from 0 to 500
     * one is written using java threads and the other uses the Chronicle Event Loop.
     *
     * @param args
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SingleAndMultiThreadedExample example = new SingleAndMultiThreadedExample();

        // runs using java Executor - outputs 500
        example.multiThreadedExample();

        // using the chronicle event loop
        example.eventLoopExample();

    }

    private Void addOneHundred() {
        for (int i = 0; i < 100; i++) {
            multiThreadedValue.incrementAndGet();
        }
        return null;
    }

    private void multiThreadedExample() throws ExecutionException, InterruptedException {

        // example using Java Threads
        final ExecutorService executorService = newCachedThreadPool();
        Future<?> f1 = executorService.submit(this::addOneHundred);
        Future<?> f2 = executorService.submit(this::addOneHundred);
        Future<?> f3 = executorService.submit(this::addOneHundred);
        Future<?> f4 = executorService.submit(this::addOneHundred);
        Future<?> f5 = executorService.submit(this::addOneHundred);

        f1.get();
        f2.get();
        f3.get();
        f4.get();
        f5.get();
        System.out.println("multiThreadedValue=" + multiThreadedValue);
    }

    private void eventLoopExample() throws InterruptedException {
        final EventLoop eventLoop = new MediumEventLoop(null, "test", Pauser.balanced(), false, "none");
        eventLoop.start();
        CountDownLatch finished = new CountDownLatch(1);
        eventLoop.addHandler(() -> {

            singleThreadedValue++;
            // we throw this to un-register the event loop

            if (singleThreadedValue == 500) {
                finished.countDown();
                throw new InvalidEventHandlerException("finished");
            }

            // return false if you don't want to be called back for a while
            return true;
        });

        finished.await();
        System.out.println("eventLoopExample=" + singleThreadedValue);
    }

}
```

### Conclusion

Favouring event loops over threads and adopting an [EDA single-threaded microservices architecture](https://chronicle.software/services/ "EDA single-threaded microservices architecture"), where either single or multiple microservices are striped using a single-threaded event loop has been successful in reducing the burden of concurrency.

#### Learn More

[Chronicle-Threads](https://github.com/OpenHFT/Chronicle-Threads/blob/ea/README.adoc "Chronicle-Threads") is an open-source project hosted on GitHub.

[Chronicle Services](https://chronicle.software/services/ "Chronicle Services") is a framework for development of event-driven solutions.
