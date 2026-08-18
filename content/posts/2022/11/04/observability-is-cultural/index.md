---
title: "Observability is Cultural"
slug: "observability-is-cultural"
date: "2022-11-04T15:23:00+00:00"
lastmod: "2023-07-18T07:08:40+00:00"
description: "To leverage observability, we need a significant shift in our corporate culture that encapsulates the entire company and goes beyond tools."
canonical: "https://debugagent.com/observability-is-cultural"
authors:
  - "shai-almog"
image: "DALL-E-2022-09-27-07.37.13-all-seeing-eye-in-the-sky.jpg"
categories:
  - "Developer Tools"
  - "Observability"
tags:
related_posts:
  - "great-time-at-javazone-2022"
  - "open-source-bait-and-switch"
  - "serverless-is-the-new-timeshare"
frozen: false
---

I'm guilty of applying the word debugging for practically anything.

My kids' legos won't fit, let's debug that.

Observability is one of the few disciplines that actually warrant that moniker, it is debugging. But traditional debugging doesn't really fit with observability practices. I usually call it "precognitive debugging". We need to have a rough idea in advance of what our debugging process will look like for effective observability troubleshooting.

Note that this doesn't apply to developer observervability which is a special case. That's a more dynamic process that more closely resembles a typical debugging session. This is about more traditional monitoring and observability. Where we need to first instrument the system and add logs, metrics, etc. to cover the information we would need as we will later investigate the issue.

I wrote before about the scourge of over logging. The same applies to observability metrics, as we collect more and more data the costs for retention and processing quickly outweigh the benefits of observability. We end up with a bigger problem altogether. We need to pick our battles, log the "right amount" and monitor the "right amount". No more and no less than we need. For that we need to understand the risks that we're dealing with and try to maximize overlap in our investigation.

Chaos Engineering as Inspiration
--------------------------------

In the tradition of Chaos Engineering we would organize a "game" orchestrated by the "master of disaster" to practice disaster readiness. This is a wonderful exercise and a great way to build that "muscle". It isn't the right fit for an observability architecture since observability deals with nuance as opposed to "fire".

Observability requires a similar game, but a deliberate one, where our team competes on finding the ways in which our system can fail. Think of it as bingo. Once we have a spreadsheet full with potential failures, we need to map out the failures to the observability we would like to have for every potential failure. E.g. in case of a hack we'd like to have the user id logged when accessing any restricted resource.

Once we chart all of those desires we can review them, try to unify some metrics and logs. Then implement them so our observability can answer everything we need to track down an issue.

Will we miss some things?

Obviously. That's part of the process. We will need to iterate and tune this. It will probably require a reduction of volume for some expensive data points to keep the costs reasonable. We will undoubtedly run into issues that aren't covered by observability (or whose observability coverage isn't obvious). In both cases we will need some help.

Do We Need Experts?
-------------------

Some observability fans assume that we no longer need domain experience to debug a problem. Given a properly observable system we should be able to understand the problem without knowing anything about the system.

While I agree that an expert in debugging can probably solve a problem faster. Possibly faster than a domain expert. I still have my doubts. Over the course of a decade, I was a consultant and I would go to companies where I used profilers, debuggers, etc. As part of that job, I found the issues that escaped people who were greater domain experts than I was. So there's some merit behind that claim.

But debugging requires some familiarity with the system that we're trying to understand. It's like diagnosing through Google. We might occasionally find the cause better than our GP but probably no better than an expert. Obviously there are exceptions to the rule, but in my experience. Experience matters for any type of debugging.

A Dashboard of Our Own
----------------------

One thing I see often is a universal "one size fits all" dashboard in a company. Grafana is a fantastic tool with remarkable flexibility, yet some expose its visualizations as a single company dashboard. There should be at least three dashboards for the application:

* High level - CTO/VP R\&D level. This focuses on business metrics, users, reliability, costs
* DevOps - Low level information about the environment
* Developers - application specific metrics and platform information

There's a lot of overlap there. But we need custom dashboards. The whole idea of the dashboard is to see everything that matters in one place. CPU utilization on the container might be interesting to me in general, but more likely than not it will just be a distraction. I want to know if there's a problem with the authorization system because users are experiencing increased error rates logging in. These metrics should be front and center.

When I open a new tab in my browser, I see Grafana. This should be the home page for every team member. The "healthy" view of our system should be etched into our mind so we can instantly notice small deviations in the environment and act accordingly.

Growing with Observability
--------------------------

As our system grows we need to include observability and metrics in the pull request that introduces a feature. Nothing can launch without observability on day one. It should be etched into the code review process and should be on-par with test coverage requirements.

Unlike test coverage, we have no metric we can rely on to verify that observability is sufficient to the rapidly evolving needs so at this time this is a heavy load on the shoulders of the reviewers. But there's an even bigger load: cost. As we grow these changes can affect cost which can suddenly spike to bankruptcy inducing heights. Cost isn't always easy to monitor, but it's a gauge we should look at on a daily basis. By keeping track of that metric and catching spikes in cost early on. We can keep our systems stable and manageable without giving up cost effectiveness.

Finally
-------

Some engineers have an over infatuation with metrics. I'm **not** one of them. Some things can't be measured. The value of personal relationships. The value of a team. A community. Because of this obsession, observability is gaining in popularity. That's good and bad. With this obsession, we sometimes over log and observe which results in poor performance and cost overruns.

We should apply observability with a scalpel not with a shovel. This shouldn't be something we delegate to the DevOps team as an afterthought. It should be a group effort that we constantly refine as we move along. We should keep our pulse on our metrics and have domain specific dashboards to keep the things that matter in our peripheral vision constantly. Observability doesn't matter if we don't bother looking.
