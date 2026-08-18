---
title: "Enter the Cloud Native Dojo: Blackbelt-Level Debugging"
slug: "enter-the-cloud-native-dojo-blackbelt-level-debugging"
date: "2022-02-22T13:28:42+00:00"
lastmod: "2022-02-22T13:30:35+00:00"
description: "In this article, I want to go over three challenges we face when debugging modern applications: polyglot, unreproducible, data pollution."
canonical: "https://thenewstack.io/enter-the-cloud-native-dojo-blackbelt-level-debugging/"
authors:
  - "shai-almog"
image: "Lightrun-Talk-to-the-duck-blogpost-cover-low-level-bare-metal-debugging-02.png"
categories:
  - "Tutorials"
tags:
related_posts:
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
  - "the-debugger-checklist-part-ii"
  - "what-is-debugging-in-140-seconds"
  - "get-started-with-allocation-profiling"
frozen: false
---

Debugging is often viewed as an art form or a craft. This is true for most engineering-related troubleshooting processes (e.g., art of motorcycle maintenance). We're usually indoctrinated into the basic moves by a senior developer and are then thrown into the proverbial pool. As a result, even senior engineers sometimes have gaps in their debugging skills. There are very few university courses or books on the subject, so it's really hard to blame them.

In his book, "[Why Programs Fail --- A Guide to Systemic Debugging](https://www.amazon.com/Why-Programs-Fail-Systematic-Debugging/dp/0123745152)", Andreas Zeller told a story from his youth working at a computer store. A customer walked into the store with a new Commodore 64 computer. For context: The computers back then booted directly to a basic interpreter; basic would accept line numbers as the first argument. He tried inputting this valid basic line:

```java
10 print “Hello World”<code class="language-basic"></code>
```

And he got a syntax error. He was surprised since the program seemed correct and it isn't exactly complex. You can probably understand it without knowing any Basic...

In debugging and in programming in general, we need to break down a problem into smaller components. So he typed in:

```
10
```

An empty statement.

It turned out that the user was used to typewriters where he'd type a lowercase L and the letter O to type the numbers one and zero. He followed the same practice on the computer and just typed "lo."

When I read that story, I laughed out loud, but I also thought: this isn't a story about debugging. But it is, debugging is about the unexpected. It's about narrowing (or slicing) the problem until we have a distilled problem we can observe. At this point, the solution presents itself to us.

In this article, I want to go over three big challenges we face when debugging modern applications:

1. Polyglot Debugging
2. Debugging the Unreproducible
3. Data Pollution

1. Polyglot Debugging
---------------------

This isn't a new problem. As a person who used to build JVMs for a living, I would occasionally "meta debug": debugging the debug support for the JVM. That sent me between Java and native code with both debuggers running and stepping through.

That's to be expected when building low-level VM code. But it's something that's becoming more common across the board. A server might be written in Python or Java with a JavaScript front end. We might track an issue through a frontend debugger all the way to the backend.

Similarly in a microservice deployment, each service might be implemented in a different language. In theory, we can test everything in isolation. In practice, that's just unrealistic. Bugs happen. By their definition, they are unexpected.

With serverless, this problem has become even worse. Reproducing a serverless environment locally is so challenging we hear arguments that [local debugging serverless is an anti-pattern](https://dev.to/garethmcc/why-local-development-for-serverless-is-an-anti-pattern-1d9b).

[Remote debugging is problematic, risky,](https://talktotheduck.dev/psa-the-risks-of-remote-jdwp-debugging) and it can't scale for complex deployments. So a lot of developers limit themselves to logging and maybe some observability tools. While that can help with some problems, these are poor replacements to local debugging. Continuous observability tools provide us with a way to go beyond simple monitoring. We can get source-level debugging similar to traditional debuggers on production servers.

2. Debugging the Unreproducible
-------------------------------

There are two types of unreproducible bugs: those we can't reproduce locally and those we can't reproduce at all. If we can reproduce the issue in production, we can use a continuous observability tool to inspect the server.

However, if we can't, we're effectively stuck with log and observability analysis. We end up looking through forensic information like police crime scene investigators. At this point, it's a bit late to do something, so we need to make sure we have new logs in place for the "next time" this happens.

As developers, we need to fight the culture of closing bugs with "can't reproduce." This is a cop-out. A can't-reproduce scenario should add logs or similar guards to verify assumptions held by the developer. That way, we won't get caught again with the can't reproduce conundrum.

3. Data Pollution
-----------------

We often think of bugs as failures, crashes and downtime. While those are indeed bad, they are often the best bugs. We know there's a problem and the solution is usually obvious and immediate.

Data pollution is insidious. It's remarkably hard to debug and incredibly hard to fix after the fact as fixing the code just isn't enough.

So what is a data pollution bug?

This is a bug that results in bad data. That in itself isn't a big problem... The problem is that this data propagates possibly between microservices and into the database. At this point, it becomes a huge problem. The bad data is a problem, but the bug that caused it can be anywhere, even on a different server. It's like looking for a needle in a haystack. These bugs are particularly insidious since they often only occur in production and the cleanup after them could be worse than the problem itself.

A good example of this is "undefined," which pollutes databases everywhere as it propagates from bad JavaScript code and somehow wiggles its way into databases. The way these are usually debugged after the fact is by placing a stack log in the place that writes or sends the data. Use a condition there to verify that this is indeed invalid data and detect that violation.

This can be done with code and also with continuous observability tools such as [Lightrun](https://lightrun.com/).

## TL;DR

Debugging is a skill that we use daily but still don't invest enough time honing. We end up using the same tools and techniques over and over. We fall back to using logs and don't use sophisticated capabilities that have been around for years. Unfortunately, bugs aren't standing still. As we scale our infrastructure with amazing container technology, the bugs scale with our distributed solutions. They become more insidious at scale.

We need new tools and new technologies to handle the bug scalability in the same way we handle container scaling.

Originally published at [The New Stack](https://thenewstack.io/enter-the-cloud-native-dojo-blackbelt-level-debugging/).
