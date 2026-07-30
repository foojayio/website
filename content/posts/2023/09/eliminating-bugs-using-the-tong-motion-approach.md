---
title: "Eliminating Bugs Using the Tong Motion Approach"
slug: "eliminating-bugs-using-the-tong-motion-approach"
date: "2023-09-22T14:53:39+00:00"
lastmod: "2023-09-22T14:53:41+00:00"
description: "Delve into a two-pronged strategy that streamlines debugging, enabling developers to swiftly pinpoint and resolve elusive software glitches."
canonical: "https://debugagent.com/eliminating-bugs-using-the-tong-motion-approach"
authors:
  - "shai-almog"
image: "/images/posts/2023/09/eliminating-bugs-using-the-tong-motion-approach/shaialmog_A_pair_of_metal_tongs_in_motion_capturing_the_intrica_184316ea-7c79-4104-9604-c3659517b6bc.jpg"
categories:
  - "Tutorials"
tags:
related_posts:
  - "the-evolution-of-bugs"
  - "debugging-as-a-process-of-isolating-assumptions"
  - "cant-reproduce-a-bug"
frozen: false
---

* [Understanding the Process of Elimination in Debugging](#understanding-the-process-of-elimination-in-debugging)
  * [The Basics](#the-basics)
  * [Using External Tools](#using-external-tools)
* [The Power of Unit Tests in Debugging](#the-power-of-unit-tests-in-debugging)
  * [Benefits of Mocking Frameworks](#benefits-of-mocking-frameworks)
* [The Challenges with Flaky Issues](#the-challenges-with-flaky-issues)
* [The Concept of the 'Tong Motion'](#the-concept-of-the-tong-motion)
  * [Applying the Tong Motion to Debugging](#applying-the-tong-motion-to-debugging)
* [An Illustrative Case: Debugging a Server Performance Issue](#an-illustrative-case-debugging-a-server-performance-issue)
* [Wrapping Up](#wrapping-up)

**Software debugging can often feel like a never-ending maze. Just when you think you're on the right track, you hit a dead-end. But, by employing the age-old technique of the process of elimination, and using the analogy of the 'Tong Motion,' we can navigate this maze more effectively.**

{{< youtube K4FRRG4pnEM >}}

<br />

As a sidenote, if you like the content of this and the other posts in this series check out my [**Debugging book**](https://www.amazon.com/dp/1484290410/) that covers this subject. If you have friends that are learning to code I'd appreciate a reference to my [**Java Basics book**](https://www.amazon.com/Java-Basics-Practical-Introduction-Full-Stack-ebook/dp/B0CCPGZ8W1/). If you want to get back to Java after a while check out my [**Java 8 to 21 book**](https://www.amazon.com/Java-21-Explore-cutting-edge-features/dp/9355513925/).

{#understanding-the-process-of-elimination-in-debugging}

Understanding the Process of Elimination in Debugging {#h2-0-understanding-the-process-of-elimination-in-debugging}
-------------------------------------------------------------------------------------------------------------------

{#the-basics}

### The Basics {#h3-1-the-basics}

The process of elimination in debugging is straightforward in principle: continuously rule out non-problematic components until the root cause reveals itself. This can be achieved either by commenting out lines of code or using debugging techniques, such as the 'force return', which bypasses specific code paths.

{#using-external-tools}

### Using External Tools {#h3-2-using-external-tools}

For front-end issues, replicating the problem using tools like curl or postman is valuable. It helps us determine if the bug is within the front-end code or elsewhere. This way, we can quickly narrow our focus, not merely addressing the symptoms but locating the actual bug.

{#the-power-of-unit-tests-in-debugging}

The Power of Unit Tests in Debugging {#h2-3-the-power-of-unit-tests-in-debugging}
---------------------------------------------------------------------------------

Unit tests are our best allies when it comes to debugging. By focusing on isolated units, they hone in on potential problem areas.

{#benefits-of-mocking-frameworks}

### Benefits of Mocking Frameworks {#h3-4-benefits-of-mocking-frameworks}

Mocking frameworks like Mockito come in handy as they can simulate large parts of the application. This way, we can drill down on the exact problem, circumventing potential disturbances. Moreover, using mocks can prevent regression and make our test cases cleaner.

However, while there are best practices regarding the extent of mocking, when debugging a specific problem, it's more pragmatic to mock as much as necessary to distill the problem to its essence.

{#the-challenges-with-flaky-issues}

The Challenges with Flaky Issues {#h2-5-the-challenges-with-flaky-issues}
-------------------------------------------------------------------------

The elimination technique is less straightforward with flaky issues - those bugs that appear irregularly or whose behavior changes as code is eliminated. The key strategy here is to **focus on negatives**. In simpler terms, if removing a certain block doesn't cause the problem to appear, it doesn't automatically indict that block. The absence could be due to the bug's unpredictable nature. Hence, it's crucial only to trust instances where the problem consistently reproduces.

{#the-concept-of-the-tong-motion}

The Concept of the 'Tong Motion' {#h2-6-the-concept-of-the-tong-motion}
-----------------------------------------------------------------------

Think of tongs. They grasp from both sides. Similarly, almost all software has at least two primary interfaces or points of input/output. For instance:

* **Enterprise Web Apps**: Web UI on one side and the database on the other.
* **Operating System Kernel**: User space app on one end and computer hardware on the other.
* **Video Games**: The joystick and screen API on one side and the game database on the other.

{#applying-the-tong-motion-to-debugging}

### Applying the Tong Motion to Debugging {#h3-7-applying-the-tong-motion-to-debugging}

Using the example of an enterprise web app:

1. **Mocking the Web Tier**: Begin by using tools like curl or postman to eliminate front-end issues.
2. **Mocking the Database**: Replace the actual database with mock data.
3. **Narrowing Down Further**: If the problem persists, move to testing the presentation tier directly, thereby eliminating the database from the equation.
4. **Digging Deeper**: Invoke the business method directly and mock its dependencies. This way, you are narrowing down on the actual method causing the issue while excluding the rest of the application.

One common pitfall is neglecting one prong of the tongs or misplacing the other. It's crucial to ensure both sides are appropriately positioned; otherwise, it might skew the results. If stuck, consider investigating from the opposite side, and then revert when needed.

{#an-illustrative-case-debugging-a-server-performance-issue}

An Illustrative Case: Debugging a Server Performance Issue {#h2-8-an-illustrative-case-debugging-a-server-performance-issue}
----------------------------------------------------------------------------------------------------------------------------

In a real-world scenario, while tackling a server performance issue, I employed the 'Tong Motion' technique. By replacing web calls with curl requests, I shifted focus to the problematic area. At the same time, I enhanced database logging to monitor its output as problematic SQL was replicated through curl. This dual-sided approach helped unearth a bug in the Object Relational Mapping layer.

![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/m121zs8x7buo7ulnxqgs.png)

This concrete example comprises of the following stages:

1. The tongs start by mocking the web tier with curl or postman. This eliminates front-end related issues.The other side of the tong motion replaces the database with mock data.
2. If the issue can be reproduced we can further squeeze the tongs by invoking the presentation tier method directly in a test case.  
   We can then eliminate the database entirely from the equation by mocking it in a test case.
3. Finally, we can invoke the business method directly eliminating the presentation tier aspect.  
   We can mock its dependencies which means we narrow down on a specific method that's at fault while eliminating the rest of the application.

{#wrapping-up}

Wrapping Up {#h2-9-wrapping-up}
-------------------------------

Debugging can be a daunting process. However, with the right techniques, like the process of elimination and the 'Tong Motion' approach, it becomes a more manageable task. Always remember to tackle issues methodically and from all angles to find and fix the root cause effectively.

Abstract: Once we press the merge button that code is no longer our responsibility. If it performs sub-optimally or has a bug it is now the problem of the DevOps team, the SRE, etc. Unfortunately, those teams work with a different toolset. If my code uses up too much RAM they will increase RAM. If the code runs slower they will increase CPU. If the code crashes they will increase concurrent instances.

If none of that helps they will call you up at 2AM. A lot of these problems are visible before they become a disastrous middle of the night call. Yes. DevOps should control production, but the information they gather from production is useful for all of us.
