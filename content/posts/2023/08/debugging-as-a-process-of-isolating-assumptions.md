---
title: "Debugging as a Process of Isolating Assumptions"
slug: "debugging-as-a-process-of-isolating-assumptions"
date: "2023-08-23T08:16:22+00:00"
lastmod: "2023-08-23T08:16:23+00:00"
description: "When looking at a vast project, how do we know the direction to narrow assumptions during debugging? When should we backtrack and rethink?"
authors:
  - "shai-almog"
image: "https://foojay.io/wp-content/uploads/2023/08/shaialmog_A_person_looking_at_a_vast_expanse_of_forest_from_abo_38ba9288-a778-4573-8c1c-adf23fd767ba.jpg"
categories:
  - "Tutorials"
tags:
related_posts:
frozen: false
---

**Debugging is an integral part of any software development process.**

**It's a systematic hunt for bugs and mistakes that may be hidden in the intricate lines of your code.**

**Much like a hunter and its prey, it requires a precise method and a set of specific tools.**

**Let's delve deeper into the fascinating process of isolating assumptions to effectively debug your code.**

{{< youtube Qw6coM798AE >}}

<br />

Before I proceed with this week's post if you have friends who are learning to code... I [published a new book for Java beginners](https://www.amazon.com/Java-Basics-Practical-Introduction-Full-Stack-ebook/dp/B0CCPGZ8W1/) with no prior knowledge (for learning programming from scratch). Each chapter also has an accompanying video and I think there's no book quite like it for beginners. I would appreciate spreading the word on this.

The Role of Assumptions in Debugging {#h2-0-the-role-of-assumptions-in-debugging}
---------------------------------------------------------------------------------

Debugging starts with defining the quadrants of the issue and then methodically eliminating possibilities until the root cause of the problem is found. However, this process can be dangerous, as it requires making assumptions about the code.

Often, debugging sessions fail or take longer than anticipated due to incorrect assumptions. A mistake at this stage can drastically elongate the debugging session compared to a mistake made at any other stage.

Think of it in terms of the popular TV show Dr. House. It's a Sherlock Holmes version of a medical drama where the lead character, a grumpy misanthrope, often faces challenges due to the incomplete or incorrect information provided by his patients. This is akin to debugging where we often spend most of our time on a wild goose chase due to incorrect assumptions or missing information.

![](/images/posts/2023/08/debugging-as-a-process-of-isolating-assumptions/house-700x394.jpeg)

The Solution to Wrong Assumptions: Double Verification {#h2-1-the-solution-to-wrong-assumptions-double-verification}
--------------------------------------------------------------------------------------------------------------------

The best solution to erroneous assumptions is double verification. For every assumption, no matter how basic, we should find another approach to verify it. For instance, let's say we have a bug in code that depends on a result from a remote service. We assume the service works correctly, and we verified that by using the cURL program. To double-verify, we should also add a tracepoint in the code that shows we received the response.

As we narrow our assumptions, double verification may not always be necessary. However, if the process seems stuck, it's essential to revisit and ensure every stage has been verified. Often, we miss something simple and obvious, so it's important to verify the "low-hanging fruit" first.

Expanding the Assumptions' Perimeter: The Predator Analogy {#h2-2-expanding-the-assumptions-perimeter-the-predator-analogy}
---------------------------------------------------------------------------------------------------------------------------

Debugging can be likened to setting up a fence to trap a predator. If the fence is too small, we risk leaving the predator outside, wasting time searching within the confines of an empty enclosure. Conversely, if we fence off a large area, it will take considerable time to pinpoint the predator.

Similarly, in the context of debugging, the bug (predator) and its root cause might be located in different areas of the application (fenced territory). Therefore, we need to make careful assumptions and decide where to place our focus.

Understanding why the program behaves the way it does and the root cause is the ultimate goal of debugging. While locating the bug or even fixing it are crucial steps, the understanding gained is invaluable for future debugging sessions and long-term groundwork.

Prioritizing Assumptions to Find the Root Cause {#h2-3-prioritizing-assumptions-to-find-the-root-cause}
-------------------------------------------------------------------------------------------------------

When faced with a symptom, we need to prioritize our assumptions to find the root cause. Our intuition and experience often guide us toward that root cause. Yet, we must refrain from taking shortcuts that lead from the system directly to the root cause, as this approach often proves misleading. Instead, our focus should be on the next stage, which is the bug itself.

For instance, if a value is incorrect on the screen, we can start by verifying that the source returns the right value and work our way from there. However, when faced with a value that "shouldn't be here", we may not have an immediate course of action.

One effective solution for this could be time travel debugging, which lets us traverse the state of the application after execution is completed. This technique is especially beneficial when the issue is not easily reproducible, and we're dealing with a problematic state that's hard to investigate.

A Common Mistake: The Lure of a Quick Fix {#h2-4-a-common-mistake-the-lure-of-a-quick-fix}
------------------------------------------------------------------------------------------

A frequent mistake made during debugging is rushing to the line of code that contains the bug, as we assume we already know the problem. We then become focused on a specific area around the bug and waste a considerable amount of time investigating that code.

Instead, we should create a wider circle by debugging the code we assume works correctly. By doing this, we either discover the assumption isn't working as expected, and we widen our search, or we confirm that the assumption is functioning correctly, giving us a solid base to delve deeper into.

This is illustrated by the following diagram. We rush to narrow down the search scope and this leads us down the wrong path where we surround the symptom with assumptions and spend time there instead of looking for the root cause. It in itself can be in a completely different location from the location where the bug is expressed.

The downside is that the wide perimeter search is so much bigger, but it is sometimes a preferable scope of search.

![](/images/posts/2023/08/debugging-as-a-process-of-isolating-assumptions/diagram-700x369.jpeg)

Final Word {#h2-5-final-word}
-----------------------------

To sum it up, debugging is indeed a complex process that requires a careful and methodical approach to isolate and eliminate assumptions.

By being aware of common pitfalls and adopting proven strategies such as double verification, prioritizing assumptions, and avoiding the lure of quick fixes, we can improve our debugging skills and make the process more efficient.

In the next article, we will further explore this topic by discussing common problems and solutions that can provide a practical application to these theoretical concepts. Stay tuned!
