---
title: "Fail-Fast Reliable Software Strategy. Debug Failures Effectively"
date: "2021-12-14T15:30:46+00:00"
lastmod: "2021-12-14T15:30:47+00:00"
description: "A broken kitchen appliance leads me down the path of intelligent failure, downside risk, exponential growth and Cloud computing!"
canonical: "https://talktotheduck.dev/fail-fast-reliable-software-strategy-debug-failures-effectively"
authors:
  - "shai-almog"
image: "Lightrun-Talk-to-the-duck-Debugging-Tutorial-Opt3.jpg"
categories:
  - "Performance"
related_posts:
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
  - "debugging-the-technical-interview-methods-and-cheating"
  - "the-debugger-checklist-part-ii"
  - "idempotent-spring-boot-starter"
frozen: false
---

**I love cooking and use my [Thermomix](https://www.thermomix.com/) a lot. If you hadn't heard about that amazing innovation, it's a kitchen robot… Well, it's a magical super cooking machine. When designing the Thermomix, its designers took the approach of [fail-safe](https://en.wikipedia.org/wiki/Fail-safe) instead of [fail-fast](https://en.wikipedia.org/wiki/Fail-fast). This is a smart choice in this case, but it has its drawbacks.**

E.g., my machine tried to recover from a failure which sent it into an infinite recovery loop. I literally couldn't pull out the food from the lid that was sealed shut. But normally, it's one of the most reliable devices I own.

Which approach should we take and how does that impact our long-term reliability?

## Fail-Fast vs. Fail-Safe Approach

In case you aren't familiar with the terms, **fail-fast means a system that would quickly fail** in an unexpected condition. A **fail-safe system will try to recover and proceed even with bad input**.

Java tries to take the fail-fast approach, whereas JavaScript leans a bit more towards the fail-safe approach. A good example of fail-fast behavior would be their respective approaches to null. In Java, a null produces a NullPointerException, which fails the code instantly and clearly. JavaScript uses "undefined", which can propagate through the system.

### Which One Should We Pick?

This is hard to tell. There's very little research and I can't think of a way to apply the scientific method objectively to measure this sort of methodology. It has both technical aspects and core business aspects. It's pretty hard to determine something conclusively. What I can say for sure is that this shouldn't be a senior executive decision alone. This is a sort of policy that management should integrate with engineering to mitigate the downside risk. This applies to you, whether you're an engineer or a business leader. Whether you're a Silicon Valley startup, Amazon or a bank. These principles are universal.

Companies using Microservices are probably more committed to some form of fail-safe. Resiliency is a common trait of Microservices, that's in the fail-safe camp.

Modern approaches to fail-safe try to avoid some pitfalls of the approach by using thresholds to limit failure. A good example of this is a circuit breaker, both the physical one and software based. A circuit breaker disconnects functionality that fails so it doesn't produce a cascading failure.

Companies who pick the fail-fast approach take some risks but reap some big rewards. When you pick that approach, the failure can be painful if a bug reaches production, but there are two significant advantages:

* It's easier to catch bugs in fail-fast systems during the development/debugging cycle
* These bugs are usually easier to fix

The fail-fast approach handles such bugs better since there's a lower risk of cascading effect. A fail-safe environment can try to recover from an error and postpone it. As a result, the developer will see an error at a much later stage and might miss the root cause of the error.

Historically, **I prefer fail-fast**; I believe it makes systems more stable when we reach production. But this is anecdotal and very hard to prove empirically. I think a fail-fast system requires some appetite for risk, both from engineering and from the executives. Maybe even more so from the executives.

Notice that despite that opinion I said that the Thermomix was smart to pick fail-safe. Thermomix is hardware running in an unknown and volatile environment. This means a fix in production would be nearly impossible and very expensive to deploy. Systems like that must survive the worst scenarios.

We need to learn from previous failure. Successful companies use both approaches, so it's very hard to pick the best approach.

### Hybrid Environment in the Cloud

A more common "strategy" for handling failure is to combine the best aspects of both worlds:

* Fail-fast when invoking local code or services, e.g. DB
* Fail-safe when depending on remote resource, e.g. remote web service

The core assumptions behind this direction is that we can control our local environment and test it well. Businesses can't rely on a random service in the cloud. They can build fault-tolerant systems by avoiding external risks but taking the calculated risks of a fail-fast system.

## Defining Failure

When discussing failure the assumptions we make focus around a 500 error page, crash, etc. Those are serious P1 failures. But by no means are they the only type of failures or even the worst types of failure... A crash usually marks a problem we can fix and even workaround by spinning up a new server instance automatically. This is actually a failure we can handle relatively elegantly.

A far more sinister failure is data corruption. A bug can cause bad data making its way into the database and potentially causing long-term problems. Even security risks and crashes can result from corrupted data and those will be much harder to fix. A fail-fast system can sometimes nip such issues in the bud.

With cloud computing, we're seeing a rise in defensive programming such as circuit breakers, retries, etc. This is unavoidable, as the assumptions behind this is that everything in the cloud can fail. We need to develop core knowledge on the failures we can expect. One approach I found useful is to review logs from long running integration tests (nightly tests).

An important part of a good QA process is long running tests that take hours to run and stress the system. When reviewing the logs of these tests, we can sometimes notice issues that didn't fail but conflict with our assumptions about the system. This can help find the insidious bugs that went through.

## Don't Fix the Bug

Not right away. Well, unless it's in production, obviously...

We should understand bugs before we fix them. Why didn't the testing process find it? Is it a cascading effect or is it missing test coverage? How did we miss that?

When developers resolve a bug, they should be able to answer that question on the issue tracker. Then comes the hard problem, find the root cause of the failure and fix the process so such issues won't happen again. This is obviously an extreme approach to take on every bug, so we need to apply some discretion when we pick the bugs to focus on. But this must always apply to a bug in production. We must investigate bugs in production thoroughly since failure in the cloud can be very problematic to the business, especially when experiencing exponential growth.

## Debugging Failure

Now that we have a general sense of the subject, let's get into the more practical aspects of a blog focused on debugging. There's no special innovation here. Debugging a fail-fast system is pretty darn easy.

But there are some gotchas, tips and tricks we can use to promote fail-fast. There are other strategies we can use to debug a fail-safe system.

### Ensuring We Fail-Fast

Use the following strategies:

* Throw exceptions - define the contract of every API in the documentation and fail immediately if the API is invoked with out of bounds state, values, etc.
* Enforce this strategy with unit tests - go over every statement made in the documentation for every API. Write a test that enforces that behavior
* If you rely on external sources, create tests for unavailable situations, low performance and sudden unavailability
* Define low timeouts, never retry

The core idea is to fail quickly. Say we need to invoke an Amazon web service. A networking issue can trigger a failure. A fail-fast system will expect a failure and present an error to the user.

### Intelligent Failure for Fail-Safe

The core idea isn't so much to avoid failure, it's unavoidable. The core idea is to soften the blow of a failure. E.g. if we take the Amazon web service example from above... A fail-safe environment could cache responses from Amazon and would try to show an older response.

The problem here is that users might get out-of-date information and this might cause a cascading effect. It might mean it will take us longer to find the problem and fix it since the system might seem in order.

The obvious tip here is to log and alert on every failure and mitigation so we can address them. But there's another hybrid approach that isn't as common but might be interesting to some.

## Hybrid Fail-Safe

A hybrid fail-safe environment starts as a fail-fast environment. This is also true for the testing environment and staging. The core innovation is wrappers that enclose individual components and provide a failsafe layer. This can be very similar to CloudFlare or Amazon cloud front providing a cached version of the website.

But how can we apply this in the code or the OPS layer?

When the system is nearing production, we need to review the fault points within the system, focusing on external dependencies but also on internal components.

A simplistic example like the Amazon example from above will include a quick failure by default. The failsafe wrapper can retry the operation and can implement various policies. There's some ready-made software tools that let us define failsafe strategy after the fact, e.g. failsafe, spring-retry and many other such tools. Some of these tools are at the SaaS API levels and can mitigate availability/networking issues.

This has the downside of adding a production component that's mostly missing in development and QA. But it includes many of the advantages of fail-fast and keeps the code relatively clean.

## Additional Best Practices for all

Here are some best practices you should keep in mind, regardless of the strategy you pick:

* **Run the software in the debugger with exception breakpoints turned on.** Exclude APIs that use exceptions to control flow (ugh, please fix those APIs) from the breakpoint. This lets you challenge your assumptions about the reliability of the application
* **Make sure the environment is random.** If you use native code, randomize memory locations. Always randomize test execution to promote failure
* **Proper code review - I can't stress this enough.** I love code reviews. I despise nitpicking!  
  When I get a response on variable naming, code styling etc. it pushes my buttons... Sometimes comments like that ignore an actual bug. People hate code review because of that type of nitpicking. Companies should train developers in substantive processes and evaluation.

## TL;DR

Failure can come in many shapes and forms. We should accept that failure happens. It happens to Amazon, Facebook and Google despite all their efforts to avoid it. We need to decide on a strategy. Make assumptions and get support from senior management all the way through engineering.

We need to make choices:

* Do we fail more often and recover quickly?
* Do we fail rarely but take time to recover?

Software reliability is still a function of QA/testing. But ultimately, failure is inevitable and we need to make strategic choices. I believe most startups should focus on fail-fast, since the growth mindset makes it very hard to keep fail-safe strategies functional. Since we have QA and testing, most of these issues are outliers and they are very hard to optimize for.
