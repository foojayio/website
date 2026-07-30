---
title: "Discuss the Problem, Not the Solution!"
slug: "discuss-problem-not-solution"
date: "2022-11-01T10:56:17+00:00"
lastmod: "2022-11-01T10:56:18+00:00"
description: "Discussing the problem offers insight into what the problem is all about. If you want to bring more value, question what the problem is."
canonical: "https://blog.frankel.ch/discuss-problem-not-solution/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2022/10/man-g90bbc5746.jpg"
categories:
  - "DevOps"
  - "Opinion"
tags:
related_posts:
frozen: false
---

As a tech guy, I love to discuss technologies. And as discussions go, it's generally the comparison kind: JVM vs. Net, Java vs. Kotlin, Go vs. Rust, Maven vs. the unspeakable one, etc.

However, it's too easy to fall into the quagmire of the merits and flaws of our beloved toys, talk about them for hours, and not reach a satisfactory agreement.

A couple of years ago, I worked as a "Solution Architect".

The job has different titles, *e.g.* , Solution Designer, Solution Engineer, but the goal is always the same: find the "best" solution for a problem, most of the time, a **business** problem.

The job went through the following steps:

1. Discuss the problem
2. Evaluate alternative solutions
3. Choose the fittest one. Sometimes, this step is the responsibility of the stakeholder.

I don't remember how often I saw people skimming through #1 or skipping it altogether.

Here are a couple of anecdotes to highlight my point.

"I want a Drupal" {#h2-0-i-want-a-drupal}
-----------------------------------------

At the time, I was working for a public administration. Allow me not to disclose which one specifically. Their process mandated that **every project should involve a solution architect** to implement the above workflow.

One day, a project manager requested a meeting with me to discuss a new project. He describes the business problem to solve as follows: "The IT director of department X wants a Drupal. He has much political power, so let's get the ball rolling."

Needless to say, I was more than a little surprised by his approach. I tried to discuss the underlying problem without any success. He didn't want to solve the underlying problem: he just wanted to obey the director's whim.

Because of the bureaucratic nature, he followed the process that required a solution architect to validate any solution. He only needed me to approve the solution the director wanted. I left the room; I don't remember exactly how it ended, but I didn't approve anything.

As a side note, people whose role is only to serve as a proxy for others have very low zero negative in organizations. Some organizations tend to attract them more than others. For example, organizations with no competition don't suffer any consequences of low ROI and tend to attract them **a lot** , *e.g.*, public administrations.

.env files or not {#h2-1-env-files-or-not}
------------------------------------------

I stumbled recently upon another example. A Google engineer wrote [a post](https://dev.to/gregorygaines/stop-using-env-files-now-kp0), named quite provocatively "Stop Using .env Files Now!". In the post, he explains the problems of `.env` files and how to solve them using a configuration server.

In response, another writer created [another post](https://dev.to/wiseai/continue-using-env-files-as-usual-2am5), named "Continue Using .env Files As Usual". As you can imagine, the author focuses on explaining that `.env` files are acceptable and describes the issues of using a configuration server.

The initial post barely describes the problem, expediting it in one short section. And in it, the author writes:
> I don't even have to explain why this is bad

Sorry but not sorry: indeed, you do!

To be honest, the debunking post doesn't do a better job. Both focus on their most-beloved solution, but neither does an excellent job of explaining their problem in their context.

PS: the underlying problem is that `.env` file management doesn't scale. For an organization the size of Google, that's a big issue; for a small organization, they are fine.

Microservices as the solution to a problem not many have {#h2-2-microservices-as-the-solution-to-a-problem-not-many-have}
-------------------------------------------------------------------------------------------------------------------------

It's hard to ignore the Microservices craze. Many people comment on microservices' pros and cons; very few write about why they implement them.

In previous posts, I've always been careful to explain why microservices are a terrible idea in most organizations. Perhaps I didn't discuss the problem that microservices solve, so here it is.

Successful organizations do grow. Most of the time, the developers' team grows along with it to back up the business growth. At one point, the team must split into several sub-teams to handle the growth. Aye, there's the rub.

Now, a lot of developers have to work on the same codebase. Historically, we handled this complexity by:

* Different development flows, *e.g.*, Git flow, GitHub flow, GitLab flow
* A dedicated person to manage the complexities of release management

My experience has shown me that this approach scales... up to a point. I witnessed synchronization issues with experienced developers' teams when the number of developers reached \~70.  

Perhaps others have a different experience.

In any case, it's the core problem that we need to discuss. Only then can we decide what alternatives can solve the growth of development teams.

Questioning the problem in technical support {#h2-3-questioning-the-problem-in-technical-support}
-------------------------------------------------------------------------------------------------

Even in technical support, one should first discuss the problem. Support does often straightforwardly answer the asked question. I believe that's the worst thing to do: it may skip the underlying problem altogether and provide a subpar solution.

Here's an example, taken from StackOverflow:
> So I want to add a project that is pulled from git and then built using Maven the thing is that their pom.xml uses both a private and public repo, they told me to remove the private repo to build it and it works but I'd love to automate the building process with Jenkins
>
> -- [How to remove repo from pom.xml before building](https://www.reddit.com/r/devops/comments/ht3m2u/how_to_remove_repo_from_pomxml_before_building/)'

Answers focus on how to remove stuff from the pom.xml because that's what the person asked. But the problem is different: it's how to build a project that references repositories one hasn't access to. Removing XML lines is a solution among many, but I'd argue it's not the most elegant one.

I'd probably offer to configure a mirror in Maven's settings.

Five whys {#h2-4-five-whys}
---------------------------

To fix focusing on solutions instead of problems, I think one should adopt the method of the five whys:
> Five whys is an iterative interrogative technique used to explore the cause-and-effect relationships underlying a particular problem. The primary goal of the technique is to determine the root cause of a defect or problem by repeating the question "Why?" five times. The answer to the fifth why should reveal the root cause of the problem.
>
> -- <https://en.wikipedia.org/wiki/Five_whys>

IMHO, one doesn't need five whys to talk about the problem instead of the solution. A couple of them are enough. By walking down the solution to the problem, you'll uncover a lot of the context and probably change the reasoning of an open-minded stakeholder.

Additionally, you may notice that a non-IT solution will be the best fit in some cases. Remember that the best code is no code at all.

Conclusion {#h2-5-conclusion}
-----------------------------

Engineers love to talk about their pet solutions. Yet, disagreeing about solutions with no context is useless. Agreeing on a solution without discussing the problem is even worse.

Discussing the problem offers invaluable insight into what the problem is all about. If you want to bring more value, you should spend more time questioning what the problem is.

*Originally published at [A Java Geek](https://blog.frankel.ch/discuss-problem-not-solution/) on October 23^rd^, 2022*

*[ROI]: Return Over Investment
