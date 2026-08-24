---
title: "The Cost of Production Blindness"
date: "2022-07-22T08:11:03+00:00"
lastmod: "2022-07-22T08:11:04+00:00"
description: "Cloud rose to fame on the banner of cutting costs but with its tremendous growth, spend is rocketing. Learn how you can cut down overspend!"
canonical: "https://lightrun.com/best-practices/the-cost-of-production-blindness/"
authors:
  - "shai-almog"
image: "Cost-Saving-with-Lightrun.png"
categories:
  - "Opinion"
  - "Performance"
related_posts:
  - "debugging-tutorial-java-return-value-intellij-jump-to-line-and-more"
  - "what-is-debugging-in-140-seconds"
  - "exception-breakpoint-that-doesnt-suck-and-a-real-use-case-for-method-breakpoints"
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
frozen: false
---

When I speak at conferences, I often fall back to the fact that just a couple of decades ago we'd observe production by kicking the server.

This is obviously no longer practical. We can't see our production. It's an amorphous Cloud that we can't touch or feel. A power that we read about but don't fully grasp.

In this case, we have physical evidence that the Cloud is there.

A part of this major shift in our industry is a change to our fundamental roles as engineers. DevOps and SRE are roles that didn't exist back then. Yet today, they're often essential for major businesses. They brought with them tremendous advancements to the reliability of production, but they also brought with them a cost: distance.

Production is in the amorphous Cloud, which is accessible everywhere. Yet it's never been further away from the people who wrote the software powering it. We no longer have the fundamental insight we took for granted a bit over a decade ago.

## Is That So Bad?

Yes, and no. We gave up some insight and control and got a lot in return:

* Stability
* Simplicity
* Security

These are pretty incredible benefits. We don't want to give these benefits up. But we also lost some insight, debugging became harder and complexity rose. We discussed these problems before but today I want to talk about one impact only…

## Cost

This is a form of blindness.

I wrote a lot about the impact of this situation on the reliability of our cloud deployments. But today I want to talk about the financial and environmental costs. Initially, the cloud was billed as a cost saving measure and there was some truth to that. The agility of deployment let us cut down on hardware costs, consolidate and simplify.

But as we got used to the cloud, our appetite for scale/reliability grew. We ended up simplifying deployment to such an extent that launching a container can be accomplished seamlessly, with no interaction on our part. This is enormous progress but also troubling. We slowly lose grip on our costs and end up paying more for less.

So what's the solution?

APMs are a category that rose to prominence specifically around this problem. Today, they are more important than ever. They help us get a sense of the Pareto principle (80/20 rule) so we can focus optimizations on the specific areas that cost the most.

This is a powerful and important tool that DevOps use every day, but it's also a very limited one.

Before we proceed, I'd like to take a moment to discuss the concept of cost. The most obvious impact is on our monthly cloud provider bill. This is work that might fund a department. But there's a more important cost in my humble opinion: the environmental cost. We tend to ignore the electricity spend because it's a very amorphous spend. But this cost is severe, e.g. the cost of a single cloud instance over one year can be the equivalent of a transatlantic flight.

We don't see the underlying hardware, but it's there, and it carries a carbon footprint. By optimizing, we can affect both costs significantly.

## Observing Production Effectively

APMs are great for measuring performance at a high level. But they provide very little detail about the dynamic inner workings of the application and the cost-cutting measures we can take inside. I often liken them to the bat signal or check engine light. They notify us of a problem but leave us without the tool to inspect the details.

That's where [developer observability tools](https://lightrun.com/) can fill in that gap. These tools can provide low level applicable insights into the application. Verify assumptions and provide developers with the means to understand production substantially.

Instead of discussing the theory, let's give some examples of actions you can take today with developer observability tools to reduce the costs of your production.

### 1. Reduce Logs

Log ingestion is probably the most expensive feature in your application. Removing a single line of log code can end up saving thousands of dollars in ingestion and storage costs. We tend to overlog since the alternative is production issues that we can't trace to their root cause.

We need a middle ground. We want the ability to follow an issue through without overlogging. Developer observability lets you add logs dynamically as needed into production. This frees you from the need to overlog and lets you focus on logging a reasonable amount. You can also raise the log level to keep the logs down. I wrote about this in depth [here](https://lightrun.com/best-practices/logging-best-practices-mdc-ingestion-and-scale/).

### 2. Caching

My top three tips for performance have always been:

1. Caching
2. Caching
3. Caching

There's really nothing else. It all boils down to that. Unfortunately, cache misses are notoriously hard to tune and detect. This is an even bigger issue in production where we need to account for the changing landscape. E.g. we cache up to 10 friends of a user on a social network but in production the growth team encourages friendships and users have more friends…

You'd have cache misses more often and you wouldn't even know.

Placing conditional breakpoints or temporary conditional logs on cache misses and inspecting them can go a long way to detect subtle issues like that. This can make an order-of-magnitude difference to performance when done right.

However, there's a bigger payout here. Many developers just ignore L2 caches entirely. This is understandable. They are hard to maintain and debug. Especially in production. A single cache corruption or a value that's out of sync and you end up with a major bug. The problem is that debugging these things in production environments is essential. Cache behaves radically differently in production because of its distributed nature.

We built developer observability solutions to debug these exact types of problems. By placing snapshots and logs over cache population/invalidation, we can narrow down the point of corruption and fix cache relation issues. By deploying these solutions to your production server, overhead can be reduced significantly!

### 3. Micro Benchmarks

APMs provide us with high-level numbers on performance and a general direction. They don't provide the lines of code we need to address. That's left up to our guesswork. If the system behaves identically when it's running locally, this should be fine. Unfortunately, this is rarely the case. E.g. a database query can have a significantly different impact when running in production. Based on local profiling results, you might waste your energy on the wrong optimization.

Developer observability tools provide the ability to narrow down the performance overhead of a code snippet. This lets us follow through the web service stack and narrow down the actual lines that are taking the most CPU time. We can accomplish this by adding a tictoc metric that measures the time between the tic line and the toc line.

We can mark a block of code and get statistics about its execution time. As in the common case of a specific query taking longer in production, we can quickly prove that this is the cause of the performance problem using this tool. The impact of many "small" issues like this can be significant in a large system and can easily mean the difference between scaling and a bottleneck.

### 4. Verification and Dead Code

A common problem is under utilized resources. APMs expose some of those problems but don't expose them all. When we have dead-code, its impact on our bottom line can be significant.

How many times did you refactor code or stop yourself from refactoring because of a legacy mess you didn't want to touch?

Yes, that legacy mess is used in your code so you don't want to "risk it". If you end up changing the code, you need to walk on eggshells and the entire operation can take an order-of-magnitude longer. This maps to cost since our time is valuable and you can spend it optimizing. It also blocks some major optimizations most times.

But what if that block of code isn't used by anyone in production?

What if it's used by very few people?

That's exactly what the counter metric does. It counts the number of times a line was reached. It can tell us which methods are important to us and how frequently they're reached. You wouldn't be as concerned about a refactor if only three people reach that line of code…

## Finally

I could carry on with the discussion of these techniques, but the gist is simple: we need to "see" what's going on. As developers, we're given a task to build a product. But the tools that let us peer into production aren't as capable as our local tools. The results we get from production can be very misleading.

As we scale production deployments, we need to use a new class of tools that exposes our code in this way. I can classify modern production with one word:

**DREAD**.

This deep binding fear that we all feel when we push a major change into production. People lose their jobs by pushing bad stuff to production. That's scary!

What do we do when facing such dread?

We keep going, but carefully. We step lightly and don't take big risks. Is our code wasteful?

Maybe, but the risk of bringing down production is far scarier than the benefit of shaving some expenses to the company.

Developer observability is the light within this darkness. When you shine a light in the dark, you take away some of the fear and make production more approachable. We can measure, test, and move fast. We also have a better sense of the risks we'll be facing with the upcoming changes. The tooling also gives us a sense of the upside. How much can we save? Imagine saving the cost of your entire department in cloud expenses. That's job security right there… The best to fight that fear of risky changes.
