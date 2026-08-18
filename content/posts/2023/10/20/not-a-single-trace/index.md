---
title: "Not a Single Trace"
date: "2023-10-20T07:06:31+00:00"
lastmod: "2023-10-20T07:07:20+00:00"
description: "Observability is an orchestra, not a single instrument. By combining multiple data points we form an accurate production narrative."
canonical: "https://debugagent.com/not-a-single-trace"
authors:
  - "shai-almog"
image: "doppleware_a_software_developer_in_a_cubicle_full_of_threads_d29b9c00-90c5-49b4-b75c-741fdd3cc838.jpg"
categories:
  - "Debugging"
  - "Tutorials"
tags:
related_posts:
  - "the-systemic-process-of-debugging"
  - "eliminating-bugs-using-the-tong-motion-approach"
  - "its-2am-do-you-know-what-your-code-is-doing"
frozen: false
---

* [The Limiting Factor](#the-limiting-factor)
* [Strength in Numbers](#strength-in-numbers)
* [Example](#example)
* [Magical APIs](#magical-apis)
* [Final Word](#final-word)

Your team celebrates a success story where a trace identified a pesky latency issue in your application's authentication service. A fix was swiftly implemented, and we all celebrated a quick win in the next team meeting. But the celebrations are short-lived. Just days later, user complaints surged about a related payment gateway timeout. It turns out that the fix we made did improve performance at one point but created a situation in which key information was never cached. Other parts in the software react badly to the fix and we need to revert the whole thing.

While the initial trace provided valuable insights into the authentication service, it didn't explain why the system was built in this way. Relying solely on a single trace has given us a partial view of a broader problem.

This scenario underscores a crucial point: while individual traces are invaluable, their true potential is unlocked only when they are viewed collectively and in context. Let's delve deeper into why a single trace might not be the silver bullet we often hope for and how a more holistic approach to trace analysis can paint a clearer picture of our system's health and the way to combat problems.

{#the-limiting-factor}

## The Limiting Factor

The first problem is the narrow perspective. Imagine debugging a multi-threaded Java application. If you were to only focus on the behavior of one thread, you might miss how it interacts with others, potentially overlooking deadlocks or race conditions.

Let's say a trace reveals that a particular method, `fetchUserData()`, is taking longer than expected. By optimizing only this method, you might miss that the real issue is with the synchronize block in another related method, causing thread contention and slowing down the entire system.

Temporal blindness is the second problem. Think of a Java Garbage Collection (GC) log. A single GC event might show a minor pause, but without observing it over time, you won't notice if there's a pattern of increasing pause times indicating a potential memory leak.

A trace might show that a Java application's response time spiked at 2 PM. However, without looking at traces over a longer period, you might miss that this spike happens daily, possibly due to a scheduled task or a cron job that's putting undue stress on the system.

The last problem is related to that and is the context. Imagine analyzing the performance of a Java method without knowing the volume of data it's processing. A method might seem inefficient, but perhaps it's processing a significantly larger dataset than usual.

A single trace might show that a Java method, `processOrders()`, took 5 seconds to execute. However, without context, you wouldn't know if it was processing 50 orders or 5,000 orders in that time frame. Another trace might reveal that a related method, `fetchOrdersFromDatabase()`, is retrieving an unusually large batch of orders due to a backlog, thus providing context to the initial trace.

{#strength-in-numbers}

## Strength in Numbers

Think of traces as chapters in a book and metrics as the book's summary. While each chapter (trace) provides detailed insights, the summary (metrics) gives an overarching view. Reading chapters in isolation might lead to missing the plot, but when read in sequence and in tandem with the summary, the story becomes clear.

We need this holistic view. If individual traces show that certain Java methods like processTransaction() are occasionally slow, grouped traces might reveal that these slowdowns happen concurrently, pointing to a systemic issue. Metrics, on the other hand, might show a spike in CPU usage during these times, indicating that the system might be CPU-bound during high transaction loads.

This helps us distinguish between correlation and causation. Grouped traces might show that every time the `fetchFromDatabase()` method is slow, the `updateCache()` method also lags. While this indicates a correlation, metrics might reveal that cache misses (a specific metric) increase during these times, suggesting that database slowdowns might be causing cache update delays, establishing causation.

This is especially important in performance tuning. Grouped traces might show that the `handleRequest()` method's performance has been improving over several releases. Metrics can complement this by showing a decreasing trend in response times and error rates, confirming that recent code optimizations are having a positive impact.

I wrote about this extensively in a previous post about the [Tong motion needed to isolate an issue](https://debugagent.com/eliminating-bugs-using-the-tong-motion-approach). This motion can be accomplished purely through the use of observability tools such as traces, metrics, and logs.

{#example}

## Example

Observability is somewhat resistant to examples, everything I try to come up with feels a bit synthetic and unrealistic when I examine it after the fact. Having said that, I looked at my modified version of the venerable Spring Pet Clinic demo using [digma.ai](http://digma.ai). Running it showed several interesting concepts taken by Digma.

![Digma](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/7u8t3xrcqant5csbca8q.png)

Probably the most interesting feature is the ability to look at what's going on in the server at this moment. This is an amazing exploratory tool that provides a holistic view for a moment in-time. But the thing I want to focus on is the "Insights" column on the right. Digma tries to combine the separate traces into a coherent narrative. It's not bad at it but it's still a machine, some of that value should probably still be done manually since it can't understand the why, only the what. It seems it can detect the venerable Spring [N+1 problem](https://digma.ai/blog/n1-query-problem-and-how-to-detect-it/) seamlessly.

![Digma Trace View](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/asos6n5teuwhx95u4r3a.png)

But this is only the start. One of my favorite things is the ability to look at tracing data next to a histogram and list of errors in a single view. Is performance impacted because there are errors?

How impactful is the performance on the rest of the application?

These become questions with easy answers at this point. When we see all the different aspects laid together.

{#magical-apis}

## Magical APIs

The N+1 problem I mentioned before is a common bug in Java Persistence API (JPA). The great Vlad Mihalcea has [an excellent explanation](https://vladmihalcea.com/n-plus-1-query-problem/). The TL;DR is rather simple. We write a simple database query using ORM. But we accidentally split the transaction causing the data to be fetched N+1 times where N is the number of records we fetch.

This is painfully easy to do since transactions are so seamless in JPA. This is the biggest problem in "magical" APIs like JPA. These are APIs that do so much that they feel like magic, but under the hood they still run regular old code, when that code fails it's very hard to see what goes on. Observability is one of the best ways to understand why these things fail.

In the past, I used to reach to the profiler for such things, which would often entail a lot of work. Getting the right synthetic environment for running a profiling session is often very challenging. Observability lets us do that without the hassle.

{#final-word}

## Final Word

Relying on a single individual trace is akin to navigating a vast terrain with just a flashlight. While these traces offer valuable insights, their true potential is only realized when viewed collectively. The limitations of a single trace, such as a narrow perspective, temporal blindness, and lack of context, can often lead developers astray, causing them to miss broader systemic issues.

On the other hand, the combined power of grouped traces and metrics offers a panoramic view of system health. Together, they allow for a holistic understanding, precise correlation of issues, performance benchmarking, and enhanced troubleshooting.

For [Java developers,](https://digma.ai/blog/coding-with-java-observability/) this tandem approach ensures a comprehensive and nuanced understanding of applications, optimizing both performance and user experience. In essence, while individual traces are the chapters of our software story, it's only when they're read in sequence and in tandem with metrics that the full narrative comes to life.
