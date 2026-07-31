---
title: "Tuning Java Applications for Low Latency on Linux"
slug: "tuning-java-applications-for-low-latency-on-linux"
date: "2023-05-10T13:34:13+00:00"
lastmod: "2023-05-10T15:07:28+00:00"
description: "An introduction to some approaches that can be taken when we want to have our applications utilise system resources most effectively."
canonical: "https://chronicle.software/tuning-for-low-latency/"
authors:
  - "george-ball"
image: "Favicon-3-2.png"
categories:
  - "Performance"
tags:
related_posts:
  - "comparing-approaches-to-durability-in-low-latency-messaging-queues"
  - "creating-terabyte-sized-queues-with-low-latency"
  - "chronicle-wire-object-marshalling"
frozen: false
---

### Introduction {#h3-0-introduction}

I have lost count of the number of times I have been told that Java is not a suitable language in which to develop applications where performance is a major consideration. My first response is usually to ask for clarification on what is actually meant by "performance" as two of the most common measures -- throughput and latency, sometimes conflict with each other, and approaches to optimise for one may have a detrimental effect on the other.

At [Chronicle Software](https://chronicle.software/?utm_source=article&amp;utm_medium=foojay&amp;utm_campaign=tune-low-latency "Chronicle Software"), we are heavily focused on building applications that are optimised for low latency, meaning that the application must consistently respond to events in a very short time. Over several years we have developed considerable expertise in this area, our libraries contain highly efficient Java code that provides an effective platform for an application's business logic to be implemented.

However, even this may not be enough to get the best performance from a latency perspective. Java applications still have to rely on the Operating System to provide access to the underlying hardware. Typically latency-sensitive (often called "Real Time") applications operate best when there is almost direct access to the underlying hardware, and the same applies to Java. In this article we will introduce some approaches that can be taken when we want to have our applications utilise system resources most effectively.

### The "Problem" with Java {#h3-1-the-problem-with-java}

Java was designed from the outset to be portable at a binary level across a wide range of hardware and system architectures. This was done by designing and implementing a virtual machine -- an abstract model of an execution platform -- and having this execute the output of the Java source compiler. The argument was that moving to a different type of hardware platform would require only the virtual machine to be ported. Applications and libraries would work without modification (the "write once run everywhere" slogan).

However applications that have strict latency and performance requirements generally require to be as close as possible to hardware at execution time -- they are looking to squeeze all the performance they can from the hardware and do not want intermediate code that is present purely for portability or abstract programming concepts like dynamic memory management to get in the way.

Over the years, the Java virtual machine has evolved into an extremely sophisticated execution platform that can generate machine code at runtime from Java bytecode, and optimise that code based on dynamically gathered metrics. This is something that statically compiled languages such as C++ are unable to do since they do not have the required runtime information. Programming techniques such as those used in the Chronicle libraries can minimise, or even eliminate the need for garbage collection -- perhaps the most obvious aspect of the Java runtime environment that prevents consistent latency times.

But at the end of the day, the Java virtual machine is not hardware -- it still requires to be run on top of an Operating System to manage its access to the hardware platform. Whether that Operating System is Linux (probably the most widely used in server-side environments), Windows, or some other, the issue still remains.

### The "Problem" With Linux {#h3-2-the-problem-with-linux}

Linux has evolved over the years as a member of the Unix family of operating systems. The first version of Unix was developed in the late 1960s; it grew and achieved great popularity in academic and research circles at first, and then in various guises in the commercial world. Linux has become the dominant variant of Unix -- although it still retains many of the original features. Nowadays with the emergence of container-based execution environments and the Cloud, its dominance has become almost complete.

However, from the point of view of Real-time, or latency-sensitive applications, Linux/Unix does have issues. These arise largely from the basic fact that Unix was designed as a time-sharing system. Its original hardware platforms were mini-computers, which were shared by many different users at the same time. All users had their own work to do, and Unix went out of its way to ensure that all got a "fair share" of the computer's resources.

Indeed the operating system would favour users who were performing a lot of I/O -- including interacting with the system at a terminal -- at the expense of tasks that were primarily performing calculations (so-called CPU-bound jobs). When we consider that the computers of the time were nearly all single CPU (single core), this made sense.

However as multi-CPU computers evolved some serious re-engineering was required at the heart of the Unix Operating System to allow these execution cores to be used effectively. But the same approach still held true, interactive tasks were always favoured over CPU-bound tasks. With multiple cores available, the net effect was still to improve overall performance.

Nowadays, almost every computer will have multiple cores, from mobile devices like phones, through workstations, to server-class machines. It seems valid to examine these environments and see if there are different approaches that we can take, to improve the platform to more effectively support real-time, latency-sensitive applications.

### How Can We Tackle These Problems? {#h3-3-how-can-we-tackle-these-problems}

#### The Java Runtime

The main issues that can affect latency in Java applications are those connected to the management of the garbage collected heap, and synchronisation of access to shared resources using locks. Techniques exist to address both of these, although they do require developers to depart somewhat from idiomatic Java programming style. Ideally we would use libraries that encapsulate the lower level details and specialised techniques, such as those offered by [Chronicle](https://chronicle.software/?utm_source=article&amp;utm_medium=foojay&amp;utm_campaign=tune-low-latency "Chronicle").

The core offerings of Chronicle are based on extremely low latency message passing between threads/processes. To achieve this, memory that is not part of the normal Java heap is used (referred to as "off heap" memory). The clear advantage of this approach is that this memory is not subject to the non-deterministic interventions of the Garbage Collector. The memory is mapped to persistent storage using normal operating system mechanisms, or alternatively replicated over network connections to other systems.

Messages are serialised to and deserialised from standard formats such as YAML or JSON, using libraries that have been carefully engineered to minimise the creation of new Java objects -- a common effect of some of the more standard serialisation libraries. Combined with the off-heap memory of the Message Queue itself, this has been shown to almost eliminate Garbage Collection from the application using it.

Concurrent access to shared mutable data has from the very earliest days of Java been synchronised using mutual exclusion locks. If a thread attempts to acquire a lock held by another thread, then it is blocked until the lock is released. In a multi-core environment it is possible to do this using alternative techniques that do not require the acquiring thread to block, and it has been shown that in the majority of cases this has a positive effect on reducing latency. Writing this sort of code is not straightforward, however it is possible to encapsulate behind the Lock interfaces in the standard Java libraries, or even further by defining data structures that allow safe, lock-free concurrent access through standard APIs. Some of the standard Java Collections libraries utilise this approach, although this is transparent to users.

#### Linux

It is fair to say that there have for some time been "real-time" variants of Unix systems that have provided different execution environments for specialised applications. While these have generally been niche products, nowadays many of these approaches, and features, are available in mainstream distributions of Unix and Linux.

We can think of features for minimising latency in relation to memory management and thread scheduling.

Like all memory in a Linux process, Java's garbage collected heap is subject to being "paged out" temporarily to disk, so that other processes can use the RAM for their own purposes, before demand requires the Java memory to be brought back in. This all happens completely transparently to the process, and the difference in access times between data in memory and data on backing store can be several orders of magnitude. Of course, off-heap memory is subject to the same behaviour.

However, modern Unix and Linux systems allow regions of memory to be pinned in main memory so that they are ignored by the operating system when it is looking for areas to reclaim from a process. This means that, for those areas of memory and that process, memory access times will be consistent (and overall perceived to be faster). Of course it also means there is less memory for other processes, which could suffer as a result, but in the low-latency world we have to be somewhat selfish!

Data structures designed for low latency will typically offer, either by default or through options, the ability to lock or pin their memory in RAM.

Threads in a Java program, just like those from other applications and even operating system tasks, are managed for access to CPUs by a component of the operating system known as the scheduler. A scheduler has a set of policies that it uses to decide which threads that want access to the CPU (called Runnable threads) are chosen -- there will normally be more Runnable threads than there are CPUs. As mentioned earlier, the traditional scheduling policies in Unix/Linux are designed to favour interactive threads over CPU-bound threads. This does not help us if we are trying to run latency sensitive applications -- we want our threads to somehow take priority over other non latency sensitive threads.

Modern Unix/Linux systems offer alternative scheduling policies that can provide these capabilities, by allowing thread scheduling priorities to be fixed at high levels so they will always take over CPU resources from other threads when they are Runnable, meaning that they can respond to events more quickly.

But it is also possible to go even further in affecting the behaviour of the scheduler. Normally, the scheduler looks at all available CPU resources when managing threads. Nowadays it is possible to change which CPUs are used by a scheduler. We can remove CPUs altogether from those available to the scheduler and utilise these exclusively for our specialised threads.

Alternatively we can partition the CPUs into groups, and associate a group of CPUs (called a cgroup) with a particular group of threads. This feature is utilised as part of Linux support for virtualisation and is key to the implementation of containers such as those generated by Docker in modern environments. However it is available to general applications through specific system calls.

Just like with the memory capabilities described above we are being selfish, as doing this will clearly have a negative effect on other parts of the system. Great care is needed to configure for the best outcome, as the potential for errors is high and the consequences can be serious. Chronicle has developed significant expertise in these areas and can offer advice based on our experience in deploying our solutions.

### Conclusion {#h3-4-conclusion}

Writing and deploying low latency applications is a highly skilled activity, requiring knowledge of not just the language being used, but the environment in which the applications are to run. In this article I've presented an overview of some of the areas that require consideration, and how they can be addressed.

[Chronicle](https://chronicle.software/?utm_source=article&amp;utm_medium=foojay&amp;utm_campaign=tune-low-latency "Chronicle") has significant expertise in this area and can offer advice and recommendations on your requirements through our expert consulting team.
