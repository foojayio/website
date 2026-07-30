---
title: "Can't Reproduce a Bug?"
slug: "cant-reproduce-a-bug"
date: "2023-08-14T07:09:14+00:00"
lastmod: "2023-08-14T07:09:15+00:00"
description: "\"It works on my machine\" is not an excuse. Sometimes we have bugs that we can't reproduce or understand. How do we investigate these bugs?"
canonical: "https://debugagent.com/cant-reproduce-a-bug"
authors:
  - "shai-almog"
image: "/images/posts/2023/08/cant-reproduce-a-bug/shaialmog_Woman_at_a_crossroads_with_her_back_to_the_camera_try_38986eec-685c-40b5-adb8-10e4c6b10751.jpg"
categories:
  - "Tutorials"
tags:
related_posts:
  - "building-for-failure-best-practices-for-easy-production-debugging"
  - "graphql-javascript-preprocessor-sql-and-more-in-manifold"
  - "spring-boot-debugging-with-aspect-oriented-programming-aop"
frozen: false
---

The phrase "it works on my machine" can be a source of amusement, but it also represents a prevailing attitude in the world of development - an attitude that often forces users to prove bugs before we're willing to investigate them. But in reality, we need to take responsibility and chase the issue, regardless of where it takes us.

{{< youtube qGasKz-YsiU >}}

<br />

A Two-Pronged Approach to Bug Solving {#h2-0-a-two-pronged-approach-to-bug-solving}
-----------------------------------------------------------------------------------

Solving bugs requires a two-pronged approach. Initially, we want to replicate the environment where the issue is occurring; it could be something specific to the user's machine. Alternatively, we may need to resort to remote debugging or use logs from the user's machine, asking them to perform certain actions on our behalf.

A few years back, I was trying to replicate a bug reported by a user. Despite matching JVM version, OS, network connectivity, and so forth, the bug simply wouldn't show up. Eventually, the user sent a video showing the bug, and I noticed they clicked differently within the UI. This highlighted the fact that often, the bug reproduction process is not just in the machine, but also in the user behavior.

The Role of User Behavior and Communication in Bug Solving {#h2-1-the-role-of-user-behavior-and-communication-in-bug-solving}
-----------------------------------------------------------------------------------------------------------------------------

In these situations, it is crucial to isolate user behavior as much as possible. Using video to verify the behavior can prove helpful. Understanding the subtle differences in the replicated environment is a key part of this, and open, clear communication with the person who can reproduce the problem is a must.

However, there can be hurdles. Sometimes, the person reporting the issue is from the support department, while we might be in the R\&D department. Sometimes, the customer might be upset, causing communication to break down. This is why I believe it's critical to integrate the R\&D department with the support department to ensure a smoother resolution of issues.

Tools and Techniques for Bug Solving {#h2-2-tools-and-techniques-for-bug-solving}
---------------------------------------------------------------------------------

Several tools such as `strace`, `dtrace`, and others can provide deep insights into a running application. This information can help us pinpoint differences and misbehaviors within the application. The advent of container technology like Docker has greatly simplified the creation of uniform environments, eliminating many subtle differences.

I was debugging a system that only failed at the customer's location. It turns out that their network connection was so fast, the round trip to the management server was completed before our local setup code finished its execution. I tracked it down by logging in remotely to their on-site machine and reproducing the issue there. Some problems can only manifest in a specific geographic location.

There are factors like networking differences, data source differences, and scale that can significantly impact the environment. How do you reproduce an issue that only appears when you have 1,000 requests per second in a large cluster? Observability tools can be extremely helpful in managing these situations. In that situation the debugging process changes, it's no longer about reproducing but rather about understanding the observable information we have for the environment as I discussed [here](https://debugagent.com/building-for-failure-best-practices-for-easy-production-debugging).

Ideally, we shouldn't reach these situations since tests should have the right coverage. However, in practice, this is never the case. Many companies have "long-run" tests designed to run all night and stress the system to the max. They help discover concurrency issues before they even occur in the wild. Failures were often due to lack of storage (filled up everything with logs) but often when we got a failure it was hard to reproduce. Using a loop to re-run the code that failed many times was often a perfect solution. Another valuable tool was the "Force Throw" feature I [discussed previously](https://debugagent.com/debugging-program-control-flow). This allowed us to fail gracefully and pass stumbling blocks in the long run.

Logging {#h2-3-logging}
-----------------------

Logging is an important feature of most applications; it's the exact tool we need to debug these sorts of edge cases. I [talked and wrote about logging](https://debugagent.com/logging-best-practices-revisited) before and its value.

Yes, logging requires forethought much like observability. We can't debug an existing bug without logging "already in place". Like many things, it's never too late to start logging properly and pick up best practices.

Concurrency {#h2-4-concurrency}
-------------------------------

If a bug is elusive the odds of a concurrency-related issue are very high. If the issue is inconsistent then this is the place to start, verifying the threads involved and making sure the right threads are doing what you expect.

Use single thread breakpoints to pause only one specific thread and check if there's a race condition in a specific method. Use tracepoints where possible instead of breakpoints while debugging -- blocking hides or changes concurrency-related bugs, which are often the reason for the inconsistency.

Review all threads and try to give each one an "edge" by making the other threads sleep. A concurrency issue might only occur if some conditions are met. We can stumble onto a unique condition using such a technique.

Try to automate the process to get a reproduction. When running into issues like this, we often create a loop that runs a test case hundreds or even thousands of times. We do that by logging and trying to find the problem within the logs.

Notice that if the problem is indeed an issue in concurrent code, the extra logging might impact the result significantly. In one case I stored lists of strings in memory instead of writing them to the log. Then I dumped the complete list after execution finished. Using memory logging for debugging isn't ideal, but it lets us avoid the overhead of the logger or even direct console output (FYI console output is often slower than loggers due to lack of filtering and no piping).

When to "Give Up" {#h2-5-when-to-give-up}
-----------------------------------------

While it's never truly recommended to "give up," there may come a time when you must accept that reproducing the issue consistently on your machine is not feasible. In such situations, we should move on to the next step in the debugging process. This involves making assumptions about the potential causes and creating test cases to reproduce them.

In cases where we cannot resolve the bug, it's important to add logging and assertions into the code. This way, if the bug resurfaces, we'd have more information to work with.

The Reality of Debugging: A Case Study {#h2-6-the-reality-of-debugging-a-case-study}
------------------------------------------------------------------------------------

At [Codename One](https://www.codenameone.com/), we were using App Engine when our daily billing suddenly skyrocketed from a few dollars to hundreds. The potential cost was so high it threatened to bankrupt us within a month. Despite our best efforts, including educated guesses and fixing everything we could, we were never able to pinpoint the specific bug. Instead, we had to solve the problem through brute force.

In the end, bug-solving is about persistence and constant learning. It's about not only accepting the bug as a part of the development process but also understanding how we can improve and grow from each debugging experience.

TL;DR {#h2-7-tl-dr}
-------------------

The adage "it works on my machine" often falls short in the world of software development. We must take ownership of bugs, trying to replicate the user's environment and behaviors as closely as possible. Clear communication is key, and integration between R\&D and support departments can be invaluable.

Modern tools can provide deep insights into running applications, helping us to pinpoint problems. While container technologies, like Docker, simplify the creation of uniform environments, differences in networking, data sources, and scale can still impact debugging.

Sometimes, despite our best efforts, bugs can't be consistently reproduced on our machines. In such cases, we need to make educated assumptions about potential causes, create test cases that reproduce these assumptions, and add logging and assertions into the code for future debugging assistance.

In the end, debugging is a learning experience that requires persistence and adaptability and is crucial for the growth and improvement of any developer.
