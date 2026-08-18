---
title: "Peter Lawrey Talks about Low-Latency, High-Performance Java"
date: "2020-10-22T04:28:38+00:00"
lastmod: "2020-10-22T07:49:42+00:00"
description: "Build a realistic workload, profile it, and tune the code if possible, or the JVM to optimise for that use case."
authors:
  - "kevinfarnham"
image: "PeterLawrey.jpg"
categories:
  - "Interviews"
  - "Performance"
tags:
related_posts:
frozen: false
---

About 7 years ago, I attended a session given by Java Champion Peter Lawrey, leader of [Chronical Software](https://chronicle.software "Chronical Software"), at a JavaOne conference. Since most of my prior development work in the realm of low-latency high-performance was C/C++ software, I was very interested in hearing what Peter might say about how Java addresses this problem.

I have to admit, being a long-time C/C++ developer, but also with quite a lot of experience with Java, I was a bit skeptical as to how Java might match up to C/C++ in terms of multithread performance.

Peter's session convinced me that matching C/C++ performance is quite possible in Java, with appropriate tunings to the JVM.

![](PeterLawrey.jpg)

I caught up with Peter again recently, and asked him some questions about what's happened since then, and where we are today. Here are my questions and Peter's responses.

**Q: Why does garbage collection even matter when we're doing basic
business applications?**

**Peter:** All applications have a limit as to how much of a delay and how often is acceptable. These might not be a formal requirement, and it might not be considered one of the likely problems but it is always there.

Two of the biggest delays in a system are IO/Network and Garbage Collection. Ensuring these are within acceptable levels, enough of the time is two of the more obvious steps in ensuring your application performs.

**Q. Why is the timing of interaction by my application so important?**

**Peter:** For some applications, timing it is just a matter of seeing if the application responds fast enough most of the time.

For other applications, this timing needs to be measured, monitored and recorded to detect the delays and help reduce the severity or likelihood of it happening again.

Exceeding the acceptable timings for a business application comes with a cost, a web site losing users, a trading system losing money.

**Q. Everyone's just reading the internet, so who cares about latency?**

**Peter:** Google wasn't the first search engine. There are several before it. These search engines were initially smarter in their searches as well. One of the things Google did well is simpler queries and faster results, this gave immediate feedback and made people feel more productive.

For trading systems, a delay of 1 millisecond can easily cost $100, if this is happening thousands of times daily, it adds up quickly.

**Q. So, you're saying latency in Java/JVM might affect your company's product's efficiency, thus degrading your company's profitability?**

**Peter:** Delays always come at a cost, it may be too small to worry about but it's never zero. In Java, these delays can be large enough to have a significant impact on a business application even if they are not disastrous.

**Q. So, what do you recommend for solving this problem for these companies?**

**Peter:** As Chronicle Software helps Tier 1 banks build custom trading systems, we optimise the code. This assumes you are free to change the code and you have a problem which is easily optimised.

For most existing applications, it's not practical to optimise them, or they have a problem which doesn't lend itself to being optimised, e.g., data which follows a complex lifecycle. In this case, you need the JVM to optimise the code and data management for you.

**Q. The timing of responses from an application reading the available data is critical for real-time applications. Do you have any opinion about how tuning the OpenJDK can help this happen?**

**Peter:** The simplest solution is to create fewer objects. Fewer objects result in less overhead and churn of your CPU cache speeding all our code and improving scalability as you avoid touching shared memory resources, e.g., the L3 cache. You are effectively doing your own simple garbage collection. We also store the bulk of our data off-heap so it can be persisted with no impact on garbage collection.

This assumes you can change the code as needed and you have a problem which has a simple lifecycle for the bulk of your data. If either of these isn't the case, you rely far more heavily on how your JVM behaves and how it can be tuned.

For example, one of our key libraries, Chronicle Queue, is an append-only persisted event source. Objects/data is only ever added to the end and the underlying files are rolled hourly or daily eventually deleted when no longer needed. This is very efficient as the lifecycle is trivial, but requires an application which can be programmed to make use of it.

Many applications, even if you can change them, have more complex data lifecycles, and you can quickly end up with something it would have been simpler/cheaper/more reliable to use an existing garbage collector to manage your data.

**Q: Do you have any summary statements you'd like to provide?**

**Peter:** Determine what your latency requirements are, don't assume there aren't any, or that since it's Java or a web application, that there is nothing you can do about it.

Build a realistic workload, profile it, and tune the code if possible, or the JVM to optimise for that use case.

If you are well within your requirements, just take quick wins, but if you are not, you will need to spend more time/resources to achieve your latency requirements.
