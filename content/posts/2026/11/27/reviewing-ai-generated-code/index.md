---
title: "On reviewing AI-generated code"
date: "2026-11-27"
description: "Reading every line an AI agent writes doesn't scale. Control isn't lost, but partitioned: tools own the mechanical, humans own intent and boundaries."
canonical: "https://blog.frankel.ch/reviewing-ai-generated-code/"
authors:
  - "nicolas-frankel"
image: "cover_large.jpg"
categories:
  - "AI"
  - "Testing"
related_posts:
  - "code-reviews-with-ai-a-developer-guide"
  - "writing-an-agent-skill"
  - "choosing-a-dependency"
  - "introduction-to-error-prone-how-to-catch-bugs-at-compile-time"
---

I see a lot of posts from developers who insist on reviewing every single line of code that AI agents write. I understand the approach, because that's what we, including me, have been doing for years. However, it doesn't scale and most developers admit it.

I don't think it's the right answer to AI-generated code. An agent produces code much faster than anybody can read it. Human review doesn't get better when there is more to read, quite the opposite. [Research on peer code review](https://smartbear.com/learn/code-review/best-practices-for-peer-code-review/) found that defect detection drops sharply beyond a few hundred lines per hour. The study predates AI, but nothing has changed in our brains since then. If the agent writes a thousand lines while you grab a coffee, you'll read them badly, or more likely not at all.

The usual conclusion is that one must accept a loss of control in exchange for productivity. I used to think so myself. I now tend to think that it's not the right way to look at it, because reading the code was never the goal. Reading was just the way we got guarantees about code. Focusing on the reading part is wrong.

## We already stopped reading twice

I started my career along with Java and the JVM. It means I've rubbed shoulders as a junior with senior developers who had experience in C. They were keen to point out that Java developers don't know when their objects are freed: garbage collection decides for them.

When the JVM came out, C and C++ developers generally found it reckless. How can you run a serious system without knowing when memory is released? And yet, the last two decades have proven them wrong, especially in large-scale systems. The JVM has passed with flying colors, and languages without a GC, _e.g._, Rust, have chosen to enforce memory management at compile time.

The objection was relevant at the time, though. However, since then, we built machinery that made the opaque part observable: <abbr title="Garbage Collection">GC</abbr> logs, heap dumps, profilers, and a whole industry of tuning. We didn't lose control over memory. We moved it from the source code to the observation of the running system.

The second time we let go of reading was the rise of Open Source dependencies. I challenge anyone to claim that they read their whole **transitive** dependency tree. A Spring Boot application pulls in hundreds of JARs; a Node.js application, thousands of packages. Do you read them?

Control went to lock files, Software Bills of Materials, vulnerability scanners, and [a policy for choosing dependencies](https://blog.frankel.ch/choosing-dependency/). When that machinery is missing, which often is the case, you get [left-pad](https://www.theregister.com/2016/03/23/npm_left_pad_chaos/): eleven lines of JavaScript removed from npm broke thousands of builds in 2016. The lesson is not that depending on code you haven't read is wrong; it's that you need to set up the machinery before you stop reading.

I admit there's a difference with AI: GC and Maven are deterministic (to an extent). A Large Language Model isn't. However, it means that the machinery matters even more in the case of AI. It also means that the usual argument, "the compiler is smarter than me anyway", doesn't apply. The agent isn't a better compiler. It's a fast and tireless junior that you can't trust blindly.

## Partition, not loss

To me, control isn't lost but partitioned.

Tools own the mechanical: formatting and style, nullness, common bug patterns, whether the tests are sensitive to changes, and whether the behavior that the tests exercise still works. These are checks that a human does badly and a machine does perfectly, every time, in seconds. Spending human attention on them is a waste, with or without AI.

Humans own three things.

* **Intent**: What the software is supposed to do. The specification, and the tests that encode it. If the agent writes both the code and the tests that verify it, nobody has checked intent.
* **Boundaries**: Public APIs, the security perimeter, the data model. Those are the parts where a mistake propagates to others, and where experience still beats current models.
* **The irreversible**: Database migrations, deletions, anything with a large blast radius. There's no way to undo this category of mistakes.

Tools are blind to several classes of problems:

* They don't catch wrong requirements; only a conversation with a human does.
* They don't catch concurrency bugs, except by luck.
* They don't catch performance regressions, unless you have performance tests in your pipeline.
* They don't catch architecture drift, unless you encode the architecture in tests, _e.g._, with ArchUnit.
* They don't catch injection, unless you run a dedicated scanner.

Each of these blind spots requires a human to look at the right place. Knowing where to look is the whole point.

> **Note: The next generation of senior developers**
>
> Juniors become seniors by reading code, a lot of it, including bad code. If nobody reads code anymore, where do the next seniors come from? I don't have the answer.

## My flow

I can't generalize, but here are the guardrails I'm using to limit potential issues.

Note that the order in which guardrails fire matters. The earlier a problem is caught, the cheaper it is to fix.

* **Instructions**: Before the agent writes anything, it reads my instructions: coding conventions, forbidden patterns, project layout, etc. I described how I package them [as skills](https://blog.frankel.ch/writing-agent-skill/). This is the only guardrail that fires before generation.
* **Language and ecosystem**: I have favored using statically and strongly typed languages. On top of that, I add a linter. If I can, I add static analysis tools.
* **Testing**: Unit tests and [integration tests](https://blog.frankel.ch/pr-testing-kubernetes/1/) check the behavior. With property-based testing, I state the property, the runtime verifies it. Mutation testing checks that [the tests are sensitive to changes](https://blog.frankel.ch/your-code-coverage-metric-is-not-meaningful/). I generally finish with a healthy dose of integration testing.
* **Post verification**: I believe this one is overlooked by most developers. For plans, I use my own `/challenger` [subagent](https://blog.frankel.ch/design-team-agents/), and for code, the regular `/code-review`.

## Conclusion

Control over the text was never the goal. Control over the outcome is. The developers who review every line are right that control matters, but wrong about where to spend it. Decide what you must read, automate what you don't, and know what the automation cannot see.

**To go further:**

* [Best Practices for Peer Code Review](https://smartbear.com/learn/code-review/best-practices-for-peer-code-review/)
* [How one developer just broke Node, Babel and thousands of projects in 11 lines of JavaScript](https://www.theregister.com/2016/03/23/npm_left_pad_chaos/)
* [Error Prone](https://errorprone.info/)
* [NullAway](https://github.com/uber/NullAway)
* [Checkstyle](https://checkstyle.org/)
* [PIT Mutation Testing](https://pitest.org/)
* [jqwik](https://jqwik.net/)

<hr>

_Originally published at [A Java Geek](https://blog.frankel.ch/reviewing-ai-generated-code/) on November 22<sup>nd</sup>, 2026._
