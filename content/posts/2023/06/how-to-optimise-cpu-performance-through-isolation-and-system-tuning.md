---
title: "Optimise CPU Performance Through Isolation and System Tuning"
slug: "how-to-optimise-cpu-performance-through-isolation-and-system-tuning"
date: "2023-06-29T06:36:39+00:00"
lastmod: "2023-06-29T06:37:51+00:00"
description: "Standard solutions for controlling CPU isolation for low-latency Linux applications are isolcpus and cgroups/csets. Each have their downsides"
authors:
  - "peter-lawrey"
image: "/images/posts/2023/06/how-to-optimise-cpu-performance-through-isolation-and-system-tuning/Screenshot-2023-06-20-at-2.12.53-PM-1024x632-1.png"
categories:
  - "Chronicle Software"
  - "Performance"
tags:
related_posts:
  - "the-more-you-say-the-less-people-remember"
  - "automatically-creating-microservices-architecture-diagrams"
  - "building-custom-solutions-vs-buy-and-build-software"
  - "how-is-leyden-improving-java-performance-part-3-of-3"
frozen: false
---

#### What are the challenges of tuning your CPU and system for optimal performance with Linux, and how does Chronicle Tune address them?

CPU isolation and efficient system management are critical for any application which requires low-latency and high-performance computing. These measures are especially important for high-frequency trading systems, where split-second decisions on buying and selling stocks must be made.

To achieve this level of performance, such systems require dedicated CPU cores that are free from interruptions by other processes, together with wider system tuning.

In modern production environments, there are numerous hardware and software hooks which can be adjusted to improve latency and throughput. However, finding the optimal settings for a system can be challenging as it requires navigating a multidimensional search space.

To accomplish this efficiently, it is necessary to understand the tuning landscape and to use tools and strategies that facilitate effective changes. Moreover, managing Java processes can be more difficult due to the number of auxiliary threads which are spawned by the JVM, even for logically single-threaded applications. The scheduling of these threads is critical to minimising jitter and achieving optimal performance.

In this article, we will explore the strengths and weaknesses of the standard solutions for controlling CPU isolation for low-latency applications under Linux and how we developed [Chronicle Tune](https://chronicle.software/tune/?utm_source=foojay&amp;utm_medium=article&amp;utm_campaign=chronicle-tune "Chronicle Tune") to address the inherent trade-offs of these solutions.

### Using the isolcpus Linux Configuration {#h3-0-using-the-isolcpus-linux-configuration}

isolcpus is a Linux boot command-line option that allows an explicit list of CPUs to be excluded from consideration by the Linux scheduler. This option provides very effective isolation; however, the problem is that it does not respect CPU ranges.

For example, when you use taskset or sched_setaffinity to specify a range of allowed CPUs for a pinned process, only the first CPU in the allowed range is utilised, regardless of the number of threads in the process.

Thus when controlling thread placement under isolcpus, every thread requires explicit management, and particular care must be taken to avoid scheduling conflicts from auxiliary and/or child threads. Another major disadvantage of isolcpus is that the configuration is fixed once a host has started, so changes to the configuration require a reboot of the system.

#### Using cgroups or csets

An alternative that provides a similar level of isolation and allows for dynamically changing configuration is Linux cgroups. cgroups is a feature in the Linux kernel that enables administrators to limit, allocate, and prioritise system resources such as CPU, memory, disk I/O, and network bandwidth among processes or groups of processes. This can help prevent one application from monopolising system resources, resulting in poor performance or instability.

csets is a utility that is used specifically to manage CPU affinity and placement for groups of tasks. By defining csets, administrators can assign specific CPUs or CPU cores to particular tasks or groups of tasks, ensuring that those tasks have dedicated CPU resources and minimising interference from other tasks.

This can be especially useful in high-performance computing environments, where minimising contention and maximising performance is critical. Both cgroups and csets enable specific cpuset groupings to be defined, with processes confined to run within one particular group.

![](/images/posts/2023/06/how-to-optimise-cpu-performance-through-isolation-and-system-tuning/Screenshot-2023-06-20-at-2.12.53-PM-1024x632.png)

*Figure 1. A comparison of how threads can be managed with isolcpus and cgroups. isolcpus allows the management of individual threads but prevents the use of flexible CPU groups.*

One drawback is that cgroups is primarily designed to work at the process level and is a less natural tool to use when the targeted control of individual threads is important. As touched on earlier, this can be a particular problem for Java applications given the relatively large number of auxiliary threads started by the JVM.

Even though many of the threads only run occasionally, they can still generate enough jitter to impact the high percentiles of any latency-sensitive application in the same group.

A further complication when using cgroups is the absence of support from standard calls like taskset and sched_setaffinity, making it more challenging to combine cgroups with low-level libraries: moving processes between groups requires the use of specialised calls.

#### Making the Procedure Better and Automatic

Since there are drawbacks with both isolcpus and cgroups/csets, plus they can be time-consuming to configure, we developed software to make tuning and managing a system simpler and more transparent.

Chronicle Tune blends features from isolcpus and cgroups/csets together with bespoke functionality to simplify CPU and system tuning, allowing changes to be applied dynamically without the need for reboots.

[Chronicle Tune](https://chronicle.software/tune/?utm_source=foojay&amp;utm_medium=article&amp;utm_campaign=chronicle-tune "Chronicle Tune") can be especially useful for Java applications where careful separation and control of application and background threads is essential for achieving best performance. Chronicle Tune facilitates optimal process placement and control, helping to ensure fewer and shorter interrupts and allowing threads to be dynamically migrated.

![](/images/posts/2023/06/how-to-optimise-cpu-performance-through-isolation-and-system-tuning/Screenshot-2023-06-20-at-2.22.25-PM-1024x532.png)  
*Figure 2. Comparison of Chronicle Tune, isolcpus and cgroups*

#### How Much Could Tuning Improve Performance?

For businesses seeking to improve their performance down to the nanosecond, it's crucial to understand how much of a difference tuning can make. To this end, we conducted a practical test to evaluate the impact of Chronicle Tune on the performance of [Chronicle Queue](https://chronicle.software/queue/?utm_source=foojay&amp;utm_medium=article&amp;utm_campaign=chronicle-tune "Chronicle Queue"). Specifically, we measured the write-to-read latency for 256 byte messages at a rate of 100,000 messages per second.

Our testing shows that while Chronicle Tune had an effect even at lower percentiles, from around the 99.9th percentile onwards the benefits of significantly reduced jitter became increasingly apparent, showing the machine running in a much cleaner, more optimal configuration.

![](/images/posts/2023/06/how-to-optimise-cpu-performance-through-isolation-and-system-tuning/Screenshot-2023-06-20-at-2.24.05-PM-1024x570.png)  
*Figure 3 Write-to-read latency of Chronicle Queue exchanging 256 byte messages @ 100k msgs/s.*

#### Can Shrink Wrapped Software be as Efficient as Manual Tuning?

Using ready-made software is certainly more convenient than tuning manually. So how effective is Chronicle Tune in comparison? To investigate this we have a tool which measures the jitter experienced by a spinning, pinned thread (closely representing a typical latency-sensitive application thread), and Figure 4 below shows the results of a comparison between isolcpus and Chronicle Tune.

This plot shows that while the total number of jitter events is slightly lower for isolcpus (as might be expected given isolcpus integrates directly with the scheduler), the worst outliers are in fact slightly lower with Chronicle Tune (6us vs 14us) on account of the additional system tuning with Chronicle Tune beyond just CPU isolation. Chronicle Tune achieves this with simple, transparent configuration, which can be adjusted without the need for reboots.

![](/images/posts/2023/06/how-to-optimise-cpu-performance-through-isolation-and-system-tuning/Screenshot-2023-06-20-at-2.25.00-PM-1024x550.png)  
*Figure 4. Average number of delays per hour, grouped by length of the delay. The statistics were gathered during a 91 second jitter test run.*

### Conclusion {#h3-1-conclusion}

The standard solutions for controlling CPU isolation for low-latency applications under Linux are isolcpus and cgroups/csets. However, they each have their downsides and can be awkward to use.

Chronicle Tune simplifies the process of system tuning, and manages low-latency, low-jitter tasks, scheduling of threads separately from processes, dynamic adjustment of allocations during runtime, efficient management of Interrupt Requests, and whole-system optimisation, including SSD, disk, memory, and network.

All of this is achieved using a simple, transparent configuration which can be adjusted dynamically without the need for a reboot to take effect.
