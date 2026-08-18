---
title: "Why I Don't do TDD"
date: "2022-12-02T08:59:00+00:00"
lastmod: "2022-12-05T07:59:27+00:00"
description: "Test Driven Development puts emphasis on unit over integration tests. The result can be lower quality featuring bugs baked into the product."
canonical: "https://debugagent.com/why-i-dont-do-tdd"
authors:
  - "shai-almog"
image: "Kempelen_chess1.jpg"
categories:
  - "Testing"
tags:
related_posts:
  - "internal-security-hardening-internal-systems"
  - "api-mocking-essential-and-redundant"
  - "observability-is-cultural"
frozen: false
---

I recently gave a talk about debugging for the London Java Community. During the Q\&A part of the talk, someone asked me about my approach to Test Driven Development. In the past I looked at that practice in a more positive light. Writing lots of tests. How can that be bad?

But as time moved on, I see it in a different light. I see it as a very limited tool that has very specific use cases. It doesn't fit into the type of projects I build and often hinders the fluid processes it's supposed to promote. But let's backtrack for a second. I really liked [this post](https://buttondown.email/hillelwayne/archive/i-have-complicated-feelings-about-tdd-8403/) that separates the types and problems in TDD. But let's simplify it a bit, let's clarify that every PR should have good coverage. This isn't TDD. It's just good programming.

TDD is more than that. In it we need to define the constraints and then solve the problem. Is that approach superior to solving the problem and then verifying the constraints are correct? That's the core premise of TDD vs. just writing good test coverage.

## The Good

TDD is an interesting approach. It's especially useful when working with loosely typed languages. In those situations TDD is wonderful as it fills the role of a strict compiler and linter.

There are other cases where it makes sense. When we're building a system that has very well defined input and output. I've run into a lot of these cases when building courses and materials. When working on real-world data this sometimes happens when we have middleware that processes data and outputs it in a predefined format.

The idea is to construct the equation with the hidden variables in the middle. Then the coding becomes filling in the equation. It's very convenient in cases like that. Coding becomes filling in the blanks.

## The Bad

> "Test Driven Development *IS* Double Entry Bookkeeping. Same discipline. Same reasoning. Same result." -- Uncle Bob Martin

I would argue that Testing is a bit like double entry bookkeeping. Yes. We should have testing. The question is should we build our code based on our tests or vice versa? Here the answer isn't so simple.

If we have a pre-existing system with tests, then TDD makes all the sense in the world. But testing a system that wasn't built yet. There are some cases where it makes sense, but not as often as one would think.

The big claim for TDD is "its design". Tests are effectively the system design, and we then implement that design. The problem with this is that we can't debug a design either. In the past I worked on a project for a major Japanese company. This company had one of the largest, most detailed sets of annex design books. Based on these design specifications the company built thousands of tests. We were supposed to pass a huge amount of tests with our system. Notice that most weren't even automatic.

The tests had bugs. There were many competing implementations but none of them found the bugs in the tests. Why? They all used the same reference implementation source code. We were the first team to skip that and do a cleanroom implementation. It perpetuated these bugs in the code, some of them were serious performance bugs that affected all previous releases.

But the real problem was the slow progress. The company could not move forward quickly. TDD proponents will be quick to comment that a TDD project is easier to refactor since the tests give us a guarantee that we won't have regressions. But this applies to projects with testing performed after the fact.

## The Worse

TDD focuses heavily on fast unit testing. It's impractical to run slow integration tests or longrun tests that can run overnight on a TDD system. How do you verify scale and integration into a major system?

In an ideal world everything will just click into place like legos. I don't live in such a world, Integration tests fail badly. These are the worst failures with the hardest to track bugs. I'd much rather have a failure in the unit tests, that's why I have them. They are easy to fix. But even with perfect coverage they don't test the interconnect properly. We need integration tests and they find the most terrible bugs.

As a result, TDD over-emphasizes the "nice to have" unit tests, over the essential integration tests. Yes, you should have both. But I must have the integration tests. Those don't fit as cleanly into the TDD process.

## Right Driven Testing

I write testing the way I choose on a case-by-case basis. If I have a case where testing in advance is natural, I'll use that. But for most cases, writing the code first seems more natural to me. Reviewing the coverage numbers is very helpful when writing tests and this is something I do after the fact.

As I mentioned before, I only check coverage for integration tests. I like unit tests and monitor the coverage there since I want good coverage there too. But for quality, only integration tests matter. A PR needs unit tests, I don't care if we wrote them before the implementation. We should judge the results.

## Bad Automation

When Tesla was building up their Model 3 factories they went into production hell. The source of the problems was their attempt to automate everything. The [Pareto Principle](https://en.wikipedia.org/wiki/Pareto_principle) applies perfectly to automation. Some things are just very resistant to automation and make the entire process so much worse.

One point where this really fails is in UI testing. Solutions like Selenium, etc. made huge strides in testing web front ends. Still, the complexity is tremendous and the tests are very fragile. We end up with hard to maintain tests. Worse, we find the UI harder to refactor because we don't want to rewrite the tests.

We can probably cross 80% of tested functionality, but there's a point of diminishing return for automation. In those environments TDD is problematic. The functionality is easy but building the tests becomes untenable.

## Finally

I'm not against TDD but I don't recommend it and effectively I don't use it. When it makes sense to start with a test I might do that, but that's not really TDD. I judge code based on the results. TDD can provide great results but often it over-emphasizes unit tests. Integration tests are more important for quality in the long run.

Automation is great. Until it stops. There's a point where automated tests just make little sense. It would save us a lot of time and effort to accept that and focus our efforts in a productive direction.

This is from my bias as a Java developer who likes type-safe, strict languages. Languages such as JavaScript and Python can benefit from a larger volume of tests because of their flexibility. Hence TDD makes more sense in those environments.

In summary, testing is good. TDD doesn't make better tests though. It's an interesting approach if it works for you. For some cases it's huge. But the idea that TDD is essential or even that it will significantly improve the resulting code, doesn't make sense.
