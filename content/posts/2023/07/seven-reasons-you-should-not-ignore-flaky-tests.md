---
title: "Seven Reasons You Should Not Ignore Flaky Tests"
slug: "seven-reasons-you-should-not-ignore-flaky-tests"
date: "2023-07-04T07:02:44+00:00"
lastmod: "2023-07-04T07:02:46+00:00"
description: "Flaky tests might seem like a minor annoyance, but in fact they are a major blocker to developer productivity. Here's seven reasons why."
canonical: "https://gradle.com/blog/seven-reasons-you-should-not-ignore-flaky-tests/"
authors:
  - "trisha-gee"
image: "/images/posts/2023/07/seven-reasons-you-should-not-ignore-flaky-tests/seven-reasons-flaky-tests-chart.png"
categories:
  - "Testing"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "9-outdated-ideas-about-java"
  - "7-functional-programming-techniques-in-java-a-primer"
  - "three-key-elements-to-incorporate-into-your-flaky-test-remediation-approach"
frozen: false
---

Imagine the scenario: you have good automated test coverage of your application code; you run your tests locally; and you have a Continuous Integration (CI) environment which runs your tests regularly.

You're doing everything right, right?

Except... sometimes tests fail, and you're not sure why. When you run the test locally, or a second time in the same environment, they pass. Whether they pass or fail doesn't seem to be related to any code changes.

Over time, your team learns these are the "flaky tests", and begins to ignore them when they fail.

Maybe you even automate re-running them when they fail to check that they do actually pass occasionally.

Why should we care about flaky tests? {#h2-0-why-should-we-care-about-flaky-tests}
----------------------------------------------------------------------------------

Flaky tests may seem like a minor inconvenience; we learn to identify which tests occasionally (or frequently!) fail for no good reason and pay them less attention...

Ideally, tests should offer a reliable safety net, giving you the confidence to make changes to the code. However, when tests flicker between pass and fail states, they become more of a mystery than a tool, diluting their utility and weakening their credibility. They become noise, getting in the way of the useful signal that our tests should be providing us.

Ignoring, or being blind to, test failures can result in the development of a lower quality product fraught with uncaught bugs.

[Addressing flaky tests](https://gradle.com/blog/do-you-regularly-schedule-flaky-test-days/) can unearth a treasure trove of business benefits and even unveil hidden issues in the production code. Here is how:

### 1. Paves the way to improve individual developer productivity {#h3-1-1-paves-the-way-to-improve-individual-developer-productivity}

Flaky tests introduce a level of unpredictability that can drag developers out of our state of flow, limit creative productivity, and slow the pace of development. On one build, a test passes, the next, it fails, with no relevant changes made to the codebase in the interim.

This inconsistent behavior can create a fog of confusion, leading us down time-consuming rabbit holes to figure out what we did wrong with our seemingly-unrelated code changes.

### 2. Results in more time to do what we do best {#h3-2-2-results-in-more-time-to-do-what-we-do-best}

Instead of focusing on more constructive (and more interesting) activities like creating new features or refining existing code, flaky tests can tangle us in a web of phantom problems, draining our time and energy.

Or we might be spending time looking for bugs that should have been caught by the automated tests, but were ignored because of the noise of intermittently failing tests.

### 3. Restores confidence in the tests {#h3-3-3-restores-confidence-in-the-tests}

Reliable tests are an invaluable ally. When there are no flaky tests, faith in the test suite is restored.

With this renewed trust, we can fearlessly modify the codebase, knowing that the test suite will catch any bugs or issues that we inadvertently introduce with our changes.

### 4. Boosts team morale {#h3-4-4-boosts-team-morale}

Flaky tests can be a persistent source of annoyance, leading to frustration and a drop in team morale. When these erratic tests are eliminated, we spend less time in the infinite pit of despair of debugging our test results.

Without flaky tests the development process becomes smoother, leading to a more motivated and happier team.

### 5. Makes better use of resources {#h3-5-5-makes-better-use-of-resources}

Intermittently failing tests may be taking more time, locally and in CI. They may be timing out. They are likely being re-run, sometimes more than once, to check if they're really failing.

Eliminating this flakiness should mean you're running fewer tests, and probably fewer builds, locally and in CI.

### 6. Reveals hidden issues in production code {#h3-6-6-reveals-hidden-issues-in-production-code}

While flaky tests may seem like a nuisance caused by poorly-written tests, they can sometimes be the canary in the coal mine, indicating deeper issues within the production code.

Fixing flaky tests can reveal subtle, previously unnoticed bugs or opportunities for optimization.

### 7. Improves software quality {#h3-7-7-improves-software-quality}

Addressing flaky tests not only improves the development process but also enhances the overall quality of the software. When a reliable test fails, we can identify the cause of that failure and fix it, ensuring the final product's stability and dependability.

A higher quality product leads to increased user satisfaction and a more robust product reputation.

### Prioritize Fixing Flaky Test {#h3-8-prioritize-fixing-flaky-test}

In conclusion, fixing flaky tests needs to be a priority unless you want to completely undermine your mission-critical testing efforts and investments. Why? A flaky test is worse than no test at all.

Either the test fails for no good reason, and we waste time looking for problems with our code that don't exist, or the test fails for a genuine reason and we simply ignore it, assuming it's the flakiness. It might be a long time before we realize that a failing test is an indication of a real problem.

The reality is that flaky tests can have a profound impact on developer productivity and software quality. By taking the time to identify and fix these tests, we can unlock a host of benefits, from time and resource optimization to discovering hidden issues in the production code.

Prioritizing fixing flaky tests should be an essential part of any software development strategy.
