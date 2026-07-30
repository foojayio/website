---
title: "Temporal Is to Your Code What a Database Is to Your Data"
slug: "temporal-is-to-your-code-what-a-database-is-to-your-data"
date: "2026-07-11T10:31:54+00:00"
lastmod: "2026-07-11T13:48:54+00:00"
description: "Your database made your data durable. Temporal does the same for your code."
authors:
  - "geertjan-wielenga"
image: "https://foojay.io/wp-content/uploads/2026/07/horizontal-logo.png"
categories:
  - "Durability"
  - "Performance"
  - "Temporal"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**Once upon a time, applications managed their own data files. Every program hand-rolled its own locking, its own crash recovery, its own consistency guarantees. Every program did it badly, in its own unique way.**

Then the relational database arrived and made a deal with application developers: ***you declare what you want, and I guarantee it survives**.* Atomicity, durability, crash recovery: all of it moved out of application code and into a dedicated platform. Nobody writes their own file-based transaction log anymore.

<figure class="wp-block-image size-full is-resized">
 <img decoding="async" src="/images/posts/2026/07/temporal-is-to-your-code-what-a-database-is-to-your-data/img-1-database-deal.svg" alt="" class="wp-image-124909" style="width:840px;height:auto">
</figure>

Now let's take a look at how we handle ***execution*** today...

The pre-database era, replayed {#h2-0-the-pre-database-era-replayed}
--------------------------------------------------------------------

If you've been writing code, Java or otherwise, long enough, you've built the same system at least once: a table full of status flags, a scheduled job that scans for stuck records, a retry counter column, and a Slack channel where someone asks "why is order 48291 stuck in PROCESSING?" every other week.

A payment flow that spans a card charge, an inventory reservation, an email, and a shipping call typically gets implemented as a hand-rolled mess: Kafka topics or SQS queues to pass messages, Quartz or cron to schedule retries, a `status` column in Postgres to track progress, and defensive code everywhere asking "did this step already run?"

That's the pre-database era, replayed for execution. Every team builds its own **crash recovery for *running programs***, and every team does it badly, in its own unique way.

This problem has a name now, :durable execution", and [Temporal.io](https://temporal.io/) is a platform that's defining the category.

Its proposition is the same deal the database made, aimed at a different layer: **you write ordinary (Java) code, and the platform guarantees it runs to completion, across crashes, deploys, and outages.**

<figure class="wp-block-image size-full is-resized">
 <img decoding="async" src="/images/posts/2026/07/temporal-is-to-your-code-what-a-database-is-to-your-data/img-2-hand-rolled-mess.svg" alt="" class="wp-image-124908" style="width:840px;height:auto">
</figure>

[Temporal.io](https://temporal.io/) is to code execution what a database is to data.

The write-ahead log, but for your local variables {#h2-1-the-write-ahead-log-but-for-your-local-variables}
----------------------------------------------------------------------------------------------------------

Databases survive crashes because of the write-ahead log: every change is recorded before it's applied, so after a crash, the database replays the log and reconstructs its state.

Temporal Workflows survive crashes using a similar pattern: an append-only event history.   

Every meaningful event in a Temporal Workflow's life (an Activity started, a timer fired, a result came back) generates a Command that becomes an Event stored in the Workflow's history.

When the JVM running your Temporal Workflow dies, Temporal rehydrates that Workflow on another Worker by replaying that history through your code. Your local variables, your position in a loop, the fact that you're on step 3 of 7, all of it is reconstructed.

<figure class="wp-block-image size-full is-resized">
 <img decoding="async" src="/images/posts/2026/07/temporal-is-to-your-code-what-a-database-is-to-your-data/img-3-wal-replay.svg" alt="" class="wp-image-124899" style="width:840px;height:auto">
</figure>

Your `int retriesRemaining` survives a JVM crash the way a committed row survives a Postgres restart.

This is also why Temporal Workflow code must be deterministic, replay only works if the code takes the same path every time. It's the same discipline event sourcing demands, applied to your program itself.

Transactions that span months {#h2-2-transactions-that-span-months}
-------------------------------------------------------------------

An ACID transaction gives you all-or-nothing over milliseconds. A Temporal Workflow gives you run-to-completion over *any* timespan. You can write:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Workflow.sleep(Duration.ofDays(30));
sendRenewalReminder(customer);</pre>

and no thread, no pod, no JVM sits around waiting for a month. The Temporal Workflow is durably parked, consuming essentially nothing, and gets rehydrated when the timer fires, even if you've deployed forty times and replaced every server in between.

<figure class="wp-block-image size-full is-resized">
 <img decoding="async" src="/images/posts/2026/07/temporal-is-to-your-code-what-a-database-is-to-your-data/img-4-month-transaction.svg" alt="" class="wp-image-124896" style="aspect-ratio:2.142891431314505;width:840px;height:auto">
</figure>

In effect, a Temporal Workflow provides run-to-completion guarantees that can span days or months. For extremely long-running workflows (years, or those exceeding \~10,000 events), use Continue-As-New to reset the history while preserving state. It's not literally ACID, long-running business processes use Sagas and compensation rather than rollback, but the direction of the guarantee is the same: the process either completes, or it resumes from exactly where it stopped.

The half-finished, stuck-in-limbo state that plagues distributed systems simply stops being your problem.

The same pattern, elsewhere in the stack {#h2-3-the-same-pattern-elsewhere-in-the-stack}
----------------------------------------------------------------------------------------

* **Virtual memory.** The OS lets you pretend you have more RAM than physically exists, paging things in and out behind your back. Temporal lets you pretend your process lives forever, suspending and resuming execution across different Workers. Think of a Temporal Workflow as durable execution: your code's progress is preserved, not the process itself.

<!-- -->

* **Garbage collection.** The JVM took manual memory management (and a whole category of bugs), off your plate. Temporal takes manual failure management off your plate the same way. You can stop writing retry loops for the same reason you stopped writing `free()`.

<!-- -->

* **Kubernetes.** K8s made infrastructure declarative and self-healing: "keep three replicas running" is a goal the system converges toward. Temporal does that for functions: "this code runs to completion" is a goal the platform converges toward, no matter which Temporal Workers die along the way.

In each case, a hard, error-prone concern that every application was reimplementing (data durability, memory, orchestration, execution) moved into a dedicated platform that hands back a guarantee.

<figure class="wp-block-image size-full is-resized">
 <img decoding="async" src="/images/posts/2026/07/temporal-is-to-your-code-what-a-database-is-to-your-data/img-5-same-pattern.svg" alt="" class="wp-image-124895" style="aspect-ratio:2.1429491307147455;width:840px;height:auto">
</figure>

What this means for your Java code {#h2-4-what-this-means-for-your-java-code}
-----------------------------------------------------------------------------

Concretely, adopting Temporal means deleting things:

* The status-flag columns and the janitor jobs that scan them
* The retry logic wrapped around every external call
* The idempotency bookkeeping asking "did this already run?"
* The queue-plus-cron plumbing that existed only to survive failures

What's left is code that reads like the business process it implements: charge the card, reserve the inventory, send the email, wait for the shipment, remind the customer in 30 days.

<figure class="wp-block-image size-full is-resized">
 <img decoding="async" src="/images/posts/2026/07/temporal-is-to-your-code-what-a-database-is-to-your-data/img-6-what-you-delete.svg" alt="" class="wp-image-124893" style="aspect-ratio:2.1429315652379826;width:820px;height:auto">
</figure>

Straight-line Java, with the reliability engineering underneath handled by a platform whose entire job is exactly that.

We stopped writing our own transaction logs decades ago. In the not too distant future, hand-rolling execution recovery out of queues, crons, and status flags will look just as archaic.

Your database made your data durable. Temporal does the same for your code.

<br />
