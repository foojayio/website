---
title: "Logging Best Practices Revisited"
slug: "logging-best-practices-revisited"
date: "2023-05-15T13:07:59+00:00"
lastmod: "2023-05-15T17:20:22+00:00"
description: "Join me for a livestream discussion on logging and how observability shouldn't just be the forte of DevOps. Logs are more than debug prints."
canonical: "https://debugagent.com/logging-best-practices-revisited"
authors:
  - "shai-almog"
image: "https://foojay.io/wp-content/uploads/2023/05/Screenshot-2023-05-14-at-15.27.56.png"
categories:
  - "Events"
  - "Videos"
tags:
related_posts:
  - "spring-boot-debugging-with-aspect-oriented-programming-aop"
  - "relearning-java-thread-primitives"
  - "boldness-in-refactoring"
frozen: false
---

As I write this my interview on [DevCentral](https://www.youtube.com/@devcentral) [hasn't started yet](https://www.linkedin.com/events/7062528143797952513/comments/) so if you subscribe to my blog or [follow](https://www.youtube.com/@debugagent) [me](https://twitter.com/debugagent) [on](https://www.linkedin.com/in/shai-almog-81a42/) [socials](https://mastodon.social/@debugagent) you might be able to catch it live. If not the recording should appear right here:

{{< youtube CFL--dAX3FQ >}}

<br />

Either way, this isn't the first time I wrote about or talked about logging and the common pitfalls we see when logging in production or debugging. I covered this extensively in [the old blog](https://talktotheduck.dev/logging-best-practices-mdc-ingestion-and-scale). I also [did a video](https://www.youtube.com/watch?v=53qCLRFcBSs) covering these ideas. But my ideas somewhat evolved around some of the concepts I discussed.

In my original post, I was a bit harsh on AOP logging. My [opinion on this has evolved](https://debugagent.com/spring-boot-debugging-with-aspect-oriented-programming-aop). I think the main problem with AOP logging is that it is often used as a sledgehammer when debugging. Another problem is leaving it on in production. But when it is used surgically it can uncover problems that would be much harder to uncover in any other way.

The main message of the original post is still the most important part: we need company-wide standardization of logging. Without that our code review process is useless.

Logging is Precognitive Debugging {#h2-0-logging-is-precognitive-debugging}
---------------------------------------------------------------------------

In my [debugging book](https://www.amazon.com/Practical-Debugging-Scale-Kubernetes-Production/dp/1484290410/), I spent quite a bit of time talking about logging. First, it's important to understand that logging is very different from print debugging. When you use print statements for debugging they are ephemeral, in a bad way. You should [use tracepoints](https://debugagent.com/the-massive-hidden-power-of-breakpoints). But more importantly, logging is about the bug that hasn't happened yet. Print debugging is about the bug that is already there.

They are nothing alike.

A log should describe our system. When we read a log we can often see the code quality without inspecting a single line of source. Uniformity, consistency, conciseness, order and value. These are all properties of a good log which is the output of a well-oiled machine. Logging is a user interface designed for your field-work engineering team. If it is written badly, they won't be as effective when carrying out their jobs. Your product will suffer.

Logging will pay back dividends with early detection of problems and simpler debugging of tests. But to do that we need to give a lot of thought to the core process.

* Which variables should we log?
* Where should we place the log?
* How many logs should we have per block of code?

These are all questions that we can answer for the general case. I answered them all in the original blog. But that isn't an authoritative answer, it's an opinion. We need to enforce standards around this.

Let me qualify that last statement. We need standards. I love that we can measure coverage and then standardize the amount of test coverage. I think that can be very helpful. However, standardizing a fixed number like test coverage without flexibility leads to terrible code that's only designed to reach the unattainable metric. We need flexibility, and a baseline to align against. Not rigid rules.

Costs, Energy and Performance {#h2-1-costs-energy-and-performance}
------------------------------------------------------------------

While managers might look at the financial bottom line for overlogging. To me, the more significant aspect is the environmental impact. This has a cascading effect throughout our industry. More logging and ingestion require more computing services. If big companies take up more computing resources it drives up pricing for all of us due to scarcity.

We can do our part for the environment, the company's bottom line and our industry. There are many strategies we can take to reduce logging to a reasonable minimum. Setting the log levels intelligently and consistently. Monitoring our logs regularly, etc.

It is often that a request that would have been served only by the cache is forced to perform an IO operation to satisfy logging. The impact on overall system performance can be tremendous yet hard to notice. If our production and dev environments differ these subtle differences can further mask such inconsistencies.

Join Us {#h2-2-join-us}
-----------------------

I hope to see your questions in the live stream or here before/after the fact. When I gave my logging talk before, I got amazing and highly engaging feedback from the audience. Either way, the recording should be there after the fact so check it out.

Logging got a lot of attention as a pillar of observability. However, the developer perspective of logging seems to have fallen to the wayside and doesn't enjoy the same level of attention. Let's change that.
