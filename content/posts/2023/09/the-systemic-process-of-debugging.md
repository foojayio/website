---
title: "The Systemic Process of Debugging"
slug: "the-systemic-process-of-debugging"
date: "2023-09-28T07:06:14+00:00"
lastmod: "2023-09-28T07:07:04+00:00"
description: "Explore the academic theory of the debugging process, focuse on issue tracking, team communication & the balance of unit to integration tests"
canonical: "https://debugagent.com/the-systemic-process-of-debugging"
authors:
  - "shai-almog"
image: "/images/posts/2023/09/the-systemic-process-of-debugging/shaialmog_A_software_developers_desk_cluttered_with_notes_diagr_6b092aec-0553-49e8-8490-a15d29b6c513.jpg"
categories:
  - "Debugging"
  - "Tutorials"
tags:
related_posts:
  - "the-evolution-of-bugs"
  - "debugging-as-a-process-of-isolating-assumptions"
  - "cant-reproduce-a-bug"
frozen: false
---

* [The Importance of Issue Tracking](#the-importance-of-issue-tracking)
  * [Avoiding Parallel Work on the Same Bug](#avoiding-parallel-work-on-the-same-bug)
  * [The Value of Issue Over Pull Requests](#the-value-of-issue-over-pull-requests)
* [Communication: Issue Tracker vs. Ephemeral Channels](#communication-issue-tracker-vs-ephemeral-channels)
  * [Why We Sometimes Avoid the Issue Tracker](#why-we-sometimes-avoid-the-issue-tracker)
  * [The Role of Daily Meetings](#the-role-of-daily-meetings)
* [The Role of Testing in Debugging](#the-role-of-testing-in-debugging)
  * [Starting with Unit Tests](#starting-with-unit-tests)
  * [Unit Tests vs. Integration Tests](#unit-tests-vs-integration-tests)
* [Final Word](#final-word)

**Debugging is an integral part of software development. However, as projects grow in size and complexity, the process of debugging requires more structure and collaboration. This process is probably something you already do as this process is deeply ingrained into most teams.**

{{< youtube JaYaDOHtbyA >}}

<br />

It's also a core part of the academic theory behind debugging. Its purpose is to prevent regressions and increase collaboration in a team environment. Without this process, any issue we fix might come back to haunt us in the future. This process helps developers work cohesively and efficiently.

{#the-importance-of-issue-tracking}

The Importance of Issue Tracking {#h2-0-the-importance-of-issue-tracking}
-------------------------------------------------------------------------

I'm sure we all use an issue tracker. In that sense, we should all be aligned. But do you sometimes "just fix a bug"?

Without going through the issue tracker?

Honestly, I do that a lot. Mostly in hobby projects but occasionally even in professional settings. Even when working alone this can become a problem...

{#avoiding-parallel-work-on-the-same-bug}

### Avoiding Parallel Work on the Same Bug {#h3-1-avoiding-parallel-work-on-the-same-bug}

When working on larger projects, it's crucial to avoid situations where multiple developers are unknowingly addressing the same issue. This can lead to wasted effort and potential conflicts in the codebase. To prevent this:

* **Always log bugs in your issue tracking system.** Before starting work on a bug, ensure it's assigned to you and marked as active. This visibility allows the project manager and other team members to be aware, reducing the chances of overlapping work.
* **Stay updated on other issues.** By keeping an eye on the issues your teammates are tackling, you can anticipate potential areas of conflict and adjust your approach accordingly.

Assuming you have a daily sync session or even a weekly session, it's important to discuss issues. This prevents collision where a teammate can hear the description of the bug and might raise a flag. This also helps in pinpointing the root cause of the bug in some situations, an issue might be familiar and communicating through it leaves a "paper trail".

As the project grows you will find that bugs keep coming back despite everything we do. History that was left behind in the issue tracker by teammates who are no longer on the team can be a lifesaver. Furthermore, the statistics we can derive from a properly classified issue tracker can help us pinpoint the problematic areas of the code that might need further testing and maybe refactoring.

{#the-value-of-issue-over-pull-requests}

### The Value of Issue Over Pull Requests {#h3-2-the-value-of-issue-over-pull-requests}

We sometimes write the comments and information directly into the pull request instead of the issue tracker. This can work for some situations but isn't as ideal for the general case.

Issues in a tracking system are often more accessible than pull requests or specific commits. When addressing a regression, linking the pull request to the originating issue is vital. This ensures that all discussions and decisions related to the bug are centralized and easily traceable.

{#communication-issue-tracker-vs-ephemeral-channels}

Communication: Issue Tracker vs. Ephemeral Channels {#h2-3-communication-issue-tracker-vs-ephemeral-channels}
-------------------------------------------------------------------------------------------------------------

I use Slack a lot. This is a problem, it's convenient but it's ephemeral and in more than one case important information written in a Slack chat was gone. Emails aren't much of an improvement, especially in the long term. An email thread I had with a former colleague was cut short and I had no context as to where it ended.

Yes, having a conversation in the issue tracker is cumbersome and awkward but we have a record.

{#why-we-sometimes-avoid-the-issue-tracker}

### Why We Sometimes Avoid the Issue Tracker {#h3-4-why-we-sometimes-avoid-the-issue-tracker}

Developers might sometimes avoid discussing issues in the tracker because:

* **Complex discussions:** Some topics might feel too broad or intricate for the issue tracker.
* **Fear of public criticism:** No one wants to appear ignorant or criticize a colleague in a permanent record. As a result, some discussions might shift to private or ephemeral channels.

However, while team cohesion and empathy are crucial, it's essential to log all relevant discussions in the issue tracker. This ensures that knowledge isn't lost, especially if a team member departs.

{#the-role-of-daily-meetings}

### The Role of Daily Meetings {#h3-5-the-role-of-daily-meetings}

Daily meetings are invaluable for teams with multiple developers working on related tasks. These meetings provide a platform for:

* **Sharing updates:** Inform the team about your current theories and direction.
* **Engaging in discussions:** If a colleague's update sounds familiar, it's an opportunity to collaborate and avoid redundant work.

However, it's essential to keep these meetings concise. Detailed discussions should transition to the issue tracker for a comprehensive record. I prefer two weekly meetings as I find it's the optimal number. The first day of the week is usually a ramp-up day. Then we have the first meeting in the morning of the second day of the week and the second meeting two days later. That reduces the load of a daily meeting while still keeping information fresh.

{#the-role-of-testing-in-debugging}

The Role of Testing in Debugging {#h2-6-the-role-of-testing-in-debugging}
-------------------------------------------------------------------------

We all use tests when developing (hopefully) but debugging theory has a special place for tests.

{#starting-with-unit-tests}

### Starting with Unit Tests {#h3-7-starting-with-unit-tests}

A common approach to debugging is to begin by creating a unit test that reproduces the issue. However, this might not always be feasible before understanding the problem. Nevertheless, once the problem is understood we should:

* **Create a test before fixing the issue.** This test should be part of the pull request that addresses the bug.
* **Maintain a coverage ratio.** Aim for a coverage ratio of 60% or higher per pull request to ensure that changes are adequately tested.

A test acts as a safeguard against a regression. If the bug resurfaces it will be a slightly different variant of that same bug.

{#unit-tests-vs-integration-tests}

### Unit Tests vs. Integration Tests {#h3-8-unit-tests-vs-integration-tests}

While unit tests are fast and provide immediate feedback, they primarily prevent regressions. They might not be as effective in verifying overall quality. On the other hand, integration tests, though potentially slower, offer a comprehensive quality check. They can sometimes be the only way to reproduce certain issues. Most of the difficult bugs I ran into in my career were in the interconnect area between modules. This is an area that unit tests don't cover very well. That is why integration tests are far more important than unit tests for overall application quality.

To ensure quality **focus on integration tests for coverage.** Relying solely on unit test coverage can be misleading. It might lead to dead code and added complexity in the system. However, as part of the debugging process, it's very valuable to have a unit test as it's far easier to debug and much faster.

{#final-word}

Final Word {#h2-9-final-word}
-----------------------------

A structured approach to debugging, combined with effective communication and a robust testing strategy, can significantly enhance the efficiency and quality of software development. This isn't about convenience, the process underlying debugging is like a paper trail for the debugging process.

I start every debugging session by searching the issue tracker. In many cases, it yields gold that might not lead me to the issue directly but still points me in the right direction.

The ability to rely on a unit test that was committed when solving a similar bug is invaluable. It gives me a leg up on resolving similar issues moving forward.
