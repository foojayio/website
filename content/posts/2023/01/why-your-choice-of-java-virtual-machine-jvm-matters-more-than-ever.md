---
title: "Why Your Choice of JVM Matters More Than Ever"
slug: "why-your-choice-of-java-virtual-machine-jvm-matters-more-than-ever"
date: "2023-01-11T17:36:35+00:00"
lastmod: "2023-02-06T19:31:26+00:00"
description: "Learn why when you use hyper-optimized Java runtimes instead of vanilla OpenJDK you do less tuning and debugging!"
canonical: "https://www.azul.com/blog/why-your-choice-of-java-virtual-machine-jvm-matters-more-than-ever/"
authors:
  - "john-ceccarelli"
image: "/images/posts/2023/01/why-your-choice-of-java-virtual-machine-jvm-matters-more-than-ever/blog-image-platform-prime.png"
categories:
  - "Java Core"
  - "Performance"
tags:
related_posts:
  - "getting-more-mileage-out-of-kafka-openjdk-vs-azul-prime"
  - "analyzing-and-tuning-warm-up-of-the-jvm-with-azul-zulu-prime-builds-of-openjdk"
  - "best-practice-comparative-evaluation-of-jdk-setups-azul-zulu-prime-vs-openjdk"
frozen: false
---

In [my recent interview with Software Daily](https://softwareengineeringdaily.com/2022/10/14/azul-with-john-ceccarelli/), I discussed that there are many companies looking for better customer experience, faster execution, and lower infrastructure costs... and that they have discovered a better use of Java to help them with just that.

The interview describes that **when you use hyper-optimized Java runtimes instead of vanilla OpenJDK** -- you do less tuning and debugging. This means you can build applications, data architectures or anything that needs better performance using an optimized JVM -- with little or no code changes.

How does that work? Here are some questions and answers around this theme.
> **Q1. John -- in this interview you help us understand why running on optimized builds of OpenJDK can help make a big difference in business applications, data storage, streaming, and search, while getting more out of cloud-native workloads. How does that work?**

**John:** People are used to thinking that Java is just Java. Paradoxically, the more different Java runtimes become available, the less people think there is any difference between what flavor of Java VM you're running on. This is because most of the new distributions of Java -- think AWS Corretto or Temurin (AdoptOpenJDK) or Azul Zulu Builds of OpenJDK -- are just vanilla redistributions of OpenJDK and have no differences in how they run your code.

But... there are other JVMs, like Azul Platform Prime and GraalVM, that actually do run your code differently and get better performance, carrying capacity, etc. Azul Platform Prime achieves this by taking OpenJDK, replacing key components like the JIT Compiler and the Garbage Collector, and replacing them with more optimized versions.
> **Q2. In your role at Azul, you must be thinking about customer experience and performance every day -- how does [Azul Platform Prime](https://www.azul.com/products/prime/) deliver on that promise?**

**John:** One thing we do is look at customer experience and total cost of ownership holistically. Many companies, when they want to look at how fast their app runs, will just do a pedal-to-the-metal throughput test that totally saturates their environment, post the top number achieved, and call it a day.

But nobody runs like that in production. Instead, people say "**I have an app that has a 100ms response SLA. I need to know how many instances of that app I need to provision to serve up my peak traffic of 2M requests per hour.**"

So, when you're benchmarking, you want to not look at max throughput, but max throughput you can achieve while still keeping your SLA. And you'll find that yes, Prime can deliver higher throughput at that SLA.

But then you can start looking at things like:

* What load balancing rules are you using to control how instances are spun up?
* Were you spinning up new instances at 50% CPU utilization because you were afraid of pauses?
* Since Prime deals with those pauses better, can you raise that threshold to say 70%?
* Were you horizontally scaling your instances to have small heaps so that stop-the-world garbage collection pauses would be as short as possible?
* Since Prime doesn't suffer from these pauses, should you replace your fleet of low-heap instances with one instance with a 10TB heap? (Ok, it's an extreme example, but we have people running 10TB heaps in production!)

When you're benchmarking, you want to not look at max throughput, but max throughput you can achieve while still keeping your SLA.
> **Q3. You gave real-world before and after examples of how an optimized JVM will give you better performance that lets you run on multiple machines in more efficient ways -- can you break this down for us?**

**John:** One of the places we work heavily on is optimizing your code to run as fast as possible. The component in the JVM that handles this task is the JIT compiler. Azul Platform Prime swaps out the OpenJDK HotSpot compiler for our hyper optimized Falcon compiler, which just gives you faster code. When each operation takes less time to complete, you can run more operations through the same size container.

And while faster optimized code will get you faster median latencies, the speed of your optimizations is not what is affecting your latency outliers the most. So, most of your code runs fast but you have those blips, hiccups, and freezes that cause 1% or 0.1% of your operations to be terrible slow.

These outliers are typically caused by freezes and pauses elsewhere on the system, most frequently due to stop-the-world garbage collection cycles.

Azul was the first company to introduce a pauseless garbage collector and have been running code in JDK 8 and JDK 11 for years with minimal GC pauses.
> **Q4. What type of companies and in which industries is [Azul Platform Prime](https://www.azul.com/products/prime/) used the most? Why?**

**John:** When we started out, we were a niche solution that focused on deliver near zero latencies for electronic trading companies. But since then, we've expanded the focus from just bringing down latencies to delivering better customer experience and reduced infrastructure costs for all kinds of companies.

Especially in the world of big data, popular Apache projects like Apache Kafka, Apache Cassandra, and Apache Solr just run so much faster on Prime. So, companies that rely heavily on these technologies, like recommendation engines, SaaS providers, and financial institutions performing fraud detection, are seeing incredible gains running on Azul Platform Prime.
> **Q5. Walk us through specific examples of the impact these companies have been able to delivery for their organizations?**

**John:** People are used to counting total cost of ownership as just your cloud bill. And if your CFO is anything like our CFO, there's a good chance that justifying your cloud spend and finding ways to reign it in is one of your top tasks.

And yes, we deliver amazing results, like Taboola saving millions of dollars running their Cassandra nodes on Azul Platform Prime or BIDS being able to reduce the infrastructure they use for electronic trading by 50%.

But using Azul Platform Prime affects your total cost of ownership in so many other ways as well. When the platform takes care of eliminating latency outliers, that means your engineers aren't responding to operational issues and spending development time trying to code around the problem.

So, for example, Workday saw a 95% reduction in operational issues in 18 months and estimates it saved 40,000 hours of operation development time just by installing Azul Platform Prime.

Interested in learning more? Check out John's [interview with Software Engineering Daily here](https://softwareengineeringdaily.com/2022/10/14/azul-with-john-ceccarelli/).

Did you know? Azul offers an [optimized JVM](https://www.azul.com/products/prime/stream-download) to improve performance for your business-critical applications.

We'll even help you get started -- [here are some best practices](https://docs.azul.com/prime/#getting-started).
