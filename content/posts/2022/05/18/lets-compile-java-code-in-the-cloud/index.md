---
title: "Let's Compile Java Code in the Cloud!"
slug: "lets-compile-java-code-in-the-cloud"
date: "2022-05-18T12:03:34+00:00"
lastmod: "2022-05-18T12:10:49+00:00"
description: "Antiquated features of the JVM make it hard to utilize resources on your Cloud instances. Is it time for Java compilation in the Cloud?"
canonical: "https://www.azul.com/blog/cloud-native-compilation-bringing-jvms-into-the-modern-cloud-world/"
authors:
  - "john-ceccarelli"
image: "cassandra-throughput-2048x810-1.png"
categories:
  - "Microservices"
  - "Performance"
tags:
related_posts:
  - "increasing-event-streaming-with-kafka-and-azul"
  - "getting-more-mileage-out-of-kafka-openjdk-vs-azul-prime"
  - "introducing-the-openjdk-coordinated-restore-at-checkpoint-project"
frozen: false
---

Across the industry, companies are trying to rein in runaway cloud costs by squeezing more carrying capacity out of the instances they run in the cloud.

Especially in the Java space, developers are trying to fit workloads into smaller and smaller instances and utilize server resources with maximum efficiency.

Relying on elastic horizontal scaling to deal with spikes in traffic means that Java workloads must start fast and stay fast.

But some antiquated features of the JVM make it hard to effectively utilize the resources on your cloud instances.

Reimagining How Java Runs In The Cloud {#h2-0-reimagining-how-java-runs-in-the-cloud}
-------------------------------------------------------------------------------------

At Azul, we're re-imagining how Java runs in a cloud-centric world. The recent release of Azul Intelligence Cloud's newest offering, [Cloud Native Compiler](https://www.azul.com/products/intelligence-cloud/cloud-native-compiler/), is a huge step in this direction.

Cloud Native Compiler is a scalable service that seeks to utilize resources more efficiently by offloading JIT workloads to cloud resources, resulting in optimized code that is both more performant and takes less time to warm up, all with minimal impact on JVM client resource utilization.

The combination of Cloud Native Compiler with the Azul Platform Prime runtime provides the best solution for companies looking to right-size their cloud instances and maximize cloud infrastructure savings.

The effect on the warm-up time and the client-side CPU spend when starting a new Java program is dramatic. Look what happens when we run Renaissance's finagle-http workload on an extremely constrained 2 vCore machine.

Doing heavier optimizations means spending more resources, as shown by the long warm-up curve of Azul Platform Prime with local JIT. When we offload to Cloud Native Compiler, this long warmup time comes down to the same levels as OpenJDK, while the optimized Falcon code continues to run at a faster throughput.
![](https://www.azul.com/wp-content/uploads/finagle-throughput-1024x248.png)

Meanwhile, CPU use on the client remains low and steady, allowing you to allocate more power to running your application logic even during warmup.
![](https://www.azul.com/wp-content/uploads/finagle-cpu-1024x248.png)

In this article we will look at:

* The origins of the current JIT compilation model
* The drawbacks of on-JVM JIT compilation
* How Cloud Native Compilation delivers better performance with less time and compute resources

**A Look Back at JIT Compilation** {#h-a-look-back-at-jit-compilation}
----------------------------------------------------------------------

First, a refresher on JIT compilation. When a JVM starts, it runs the compiled portable byte code in the Java program in the slower interpreter until it can identify a profile of "hot" methods and how they are run.

The JIT compiler then compiles those methods down into machine code that is highly optimized for the current usage patterns. It runs that code until the optimizations turn out to be wrong. In this case, we get a deoptimization, where optimized code is thrown out and the method is run in the interpreter again until a new optimized method can be produced.

When the JVM is shut down, all the profile information and optimized methods are discarded, and on the next run the whole process starts from scratch.

When Java was created in the 90s, there was no such thing as a "magic cloud" of connected, elastic resources that we could spin up and down at will. It was therefore a logical choice to make JVMs, including the JIT compiler, completely encapsulated and self-reliant.

So what are the drawbacks of this approach? Well...

* The JIT compiler must share resources with the threads executing application logic. This means there is a limit on how many resources can be devoted to optimization, limiting the speed and effectiveness of those optimizations. For example, Azul Platform Prime's Falcon JIT compiler's highest optimization levels produce code that can be 50%-200% faster on individual methods and workloads. On resource constrained machines, however, such high optimization levels may not be practical due to resource limitations.
* You only need resources for JIT compilation for a tiny fraction of the life of your program. However, with on-JVM JIT compilation, you must reserve capacity forever.
* Bursts of JIT-related CPU and memory usage at the beginning of a JVM's life can wreak havoc on load-balancers, Kubernetes CPU throttling, and other parts of your deployment topology.
* JVMs have no memory of past runs. Even if a JVM is running a workload which it has run a hundred times before, it must run it in the interpreter from scratch as if it's the first time.
* JVMs have no knowledge of other nodes running the same program. Each JVM builds up a profile based on its traffic, but a more performant profile could be built up by aggregating the experience of hundreds of JVMs running the same code.

**Offloading JIT Compilation to the Cloud** {#h2-2-offloading-jit-compilation-to-the-cloud}
-------------------------------------------------------------------------------------------

Today, we do have a "magic cloud" of resources that we can use to offload JVM processes that could be more effectively done elsewhere.

That's why we built the Cloud Native Compiler, a dedicated service of scalable JIT compilation resources that you can use to warm up your JVMs.

You run Cloud Native Compiler as a Kubernetes cluster in your servers, either on-premise or in the cloud.

Because it's scalable, you can ramp up the service to provide practically unlimited resources for JIT compilation in the short bursts when it is required, then scale it down to near zero when not needed.

When you use Cloud Native Compiler on your Java workloads:

* CPU consumption on the client remains low and steady. You don't have to reserve capacity for doing JIT compilation and can right-size your instances to just the resource requirements of running your application logic.
* You can always afford to run the most aggressive optimizations resulting in peak top line performance, regardless of the capacity of your JVM client instance.
* The wall-clock time of warming up your JVM decreases significantly as the JIT compilation requests are run more in parallel thanks to more available threads on the Cloud Native Compiler service.

**So Does It Really Make a Difference?** {#h2-3-so-does-it-really-make-a-difference}
------------------------------------------------------------------------------------

Azul Platform Prime's highly optimized Falcon compiler is already capable of producing optimizations that are much faster than OpenJDK's Hotspot compiler.

Here are some numbers on Falcon vs Hotspot on popular Java performance benchmarks.

|---------------|-----------------------------------------------|
| **Benchmark** | **Azul Platform Prime Throughput vs OpenJDK** |
| Cassandra     | 135%                                          |
| jmh-8-stream  | 159%                                          |
| Renaissance   | 149%                                          |
| Disruptor     | 132%                                          |
| Kafka         | 145%                                          |

![](https://www.azul.com/wp-content/uploads/rennaisance3-1024x515.png)

We've already shown above how Cloud Native Compiler reduces warm-up time and CPU spend when running the finagle-http workload on a resource-starved machine. But it's not just extremely resource constrained machines that benefit from Cloud Native Compiler.

Let's look at a more realistic workload -- running a three-node Cassandra cluster on an 8 vCore r5.2xlarge AWS instance. With optimization set to the highest level, resulting in high and consistent throughput, warm-up time goes from 20 minutes with local JIT to less than two minutes with Cloud Native Compiler.
![](https://www.azul.com/wp-content/uploads/cassandra-throughput-1024x405.png)

Conclusion {#h2-4-conclusion}
-----------------------------

Azul's Cloud Native Compiler is a major advancement in how Java runtimes perform their work.

Cloud Native Compiler combined with Azul Platform Prime's highly optimized Falcon JIT Compiler is the most efficient way to run your Java workloads and minimize your infrastructure costs by up to 50%.

Azul Platform Prime Stream Builds and Cloud Native Compiler are free for evaluation and development, so [download them now](https://www.azul.com/products/prime/stream-download/) and give them a try.
