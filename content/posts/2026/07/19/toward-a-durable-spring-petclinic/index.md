---
title: "Toward a Durable Spring PetClinic"
slug: "toward-a-durable-spring-petclinic"
date: "2026-07-19T18:06:06+00:00"
lastmod: "2026-07-22T21:31:56+00:00"
description: "What makes a process a good candidate for durability? To answer this, we'll take the canonical Spring PetClinic and interrogate its sources."
authors:
  - "geertjan-wielenga"
image: "horizontal-logo.png"
categories:
  - "Developer Tools"
  - "Durability"
  - "Performance"
  - "Temporal"
tags:
related_posts:
frozen: false
---

Overview
--------

**Durable Execution** is a way of running code so that its progress survives failure. A normal program keeps its state in memory: if the process crashes, the machine reboots, or a network call times out halfway through a multi-step operation, that state is gone and the work is left half-done.

Temporal, the Durable Execution platform, persists every step of a process to its **Event History** --- a durable, append-only log of Events --- so that after any failure the work resumes exactly where it left off, as if the crash never happened.

That makes it possible to write long-running, multi-step processes (ones that wait days for a Timer, call flaky external services, or must never run a step twice) as ordinary, linear code, while the platform guarantees they complete reliably and exactly once.

What makes a process a good candidate for durability?
-----------------------------------------------------

But what makes a process a good candidate for durability? The processes that benefit most are the ones that are:

* **long-running** (they wait minutes, days, or weeks)

* **multi-step** (they touch several systems that can each fail independently),

* and/or that **must run exactly once** (never double-charging a card or double-sending a notification).

A plain, single-step database write that either commits or doesn't gets nothing from Durable Execution --- it is already atomic.

The interesting question is where a real application hides processes of the first three kinds.

Spring PetClinic as an Exploratory Device
-----------------------------------------

To answer it concretely, let's take the canonical [Spring PetClinic](https://github.com/spring-projects/spring-petclinic) sample application and interrogate its source code.

Spring PetClinic is a textbook CRUD app, so nothing in it *strictly* requires durability today --- but its domain (booking visits, registering pets, taking payments) is full of exactly those long-running, multi-step, failure-prone processes, whether latent in the code or one obvious extension away. The goal is to find those seams, rank them, and pick a starting point.

The sections that follow work through it in order: **Step 1** downloads the source to analyze, **Step 2** runs the durability analysis twice (once with plain, standard Claude Code, then again grounded in [Temporal](https://temporal.io/)), and **Step 3** draws the conclusions.

The result is a single and very clear recommendation in the context of the Spring PetClinic: make **visit booking + reminder** durable first. It wins for three reasons.

1. The seam already exists in the code --- `VisitController` persists a future-dated visit today, so there is a natural place to hook in with almost no scaffolding.

2. A visit is inherently long-running: the reminder must fire the day before an appointment that may be weeks away, which is exactly the kind of durable wait that in-memory code cannot survive but Durable Execution handles trivially.

3. It exercises all three things durability is *for* in a single, easy-to-grasp process --- a durable Timer (sleep until the reminder is due), retriable side effects (send the confirmation, notify the vet), and exactly-once semantics (never double-send a reminder, even across a crash).

The other candidates are real, but they are either lower-stakes (owner onboarding), require inventing new capabilities (billing / payment), or add distributed-system complexity (external registries) before the core value is demonstrated. Visit booking is the shortest path to seeing durability earn its keep.

Step 1 --- Download the Spring PetClinic source
-----------------------------------------------

The first step of this project is to download the official [Spring PetClinic](https://github.com/spring-projects/spring-petclinic) sample application.

Step 2 --- Analyze the application for durability candidates
------------------------------------------------------------

Pointing Claude Code (or a similar AI) at the source of an application you already have and asking it where a technology you're interested in --- adopting, integrating, or simply learning about --- would fit is a natural first step.

The model reads your real code rather than a whitepaper, so you never have to guess how a new technology maps onto what you already have. The opportunities it surfaces are grounded in your actual domain and control flow, not in generic advice.

That is exactly what this step does: it analyzes Spring PetClinic for processes worth making durable, and it does it twice.

* **2a** is a pass done with plain, standard Claude Code --- no plugins or skills installed. It identifies the candidates, ranks them, and picks visit booking + reminder as the ideal first process.

* **2b** re-runs the same analysis grounded in [Temporal](https://temporal.io/) (via the [`temporal-developer` skill](https://github.com/temporalio/skill-temporal-developer)), mapping each candidate onto concrete Temporal primitives and adding `temporal-spring-boot-starter` wiring.

Both reach the same conclusions; 2b adds Temporal-specific vocabulary, corrected line numbers, and implementation guidance. See [Section 3](#step-3--conclusions) for a side-by-side comparison.

### 2a --- Analysis: processes that are good candidates for durability

Spring PetClinic is a textbook CRUD app. Every "process" is a single synchronous HTTP request that performs one local database transaction against one datasource. As it stands there are **no external service calls, no message queues, no multi-step orchestration, no background jobs, and no long-running operations** in the code. So out of the box nothing *strictly requires* durable execution --- a single JPA transaction already provides atomicity.

The useful question is therefore not "what is durable today" but **"where does the domain naturally want a multi-step, failure-prone, or long-running process that durability would protect?"** Some of those seams already exist in the code;  

others are obvious extensions the domain implies.

#### Candidates grounded in the current code

1. **Book a visit** --- `owner/VisitController.java:97` (`processNewVisitForm`). Today this is just `owners.save(owner)`. But "booking a visit" is the app's most obvious real-world workflow: in production it would fan out to *send a confirmation, schedule a reminder for the day before the appointment, and notify the assigned vet* . That reminder is a **durable timer** --- the process sleeps until `visit.getDate()` minus one day, then acts. This is the strongest candidate: it is long-running (days to weeks), spans side effects that can fail independently, and needs exactly-once semantics on notifications.

2. **Create / update a pet** --- `owner/PetController.java:107` (`processCreationForm`) and `owner/PetController.java:186` (`updatePetDetails`). Currently a single `saveAndFlush`. A realistic clinic extension registers the pet with external systems (microchip registry, insurance provider, vaccination-record service). The moment a second system is involved, this becomes a **distributed multi-step process** with partial-failure risk --- a good fit for per-step retries and a saga / compensation path.

3. **Register an owner** --- `owner/OwnerController.java:77` (`processCreationForm`). The same pattern at lower priority: a welcome message plus a CRM sync turns a local save into a process worth making durable.

#### Candidates from natural domain extensions (not in code yet)

4. **Vet reference-data sync / cache warming** --- `system/CacheConfiguration.java` with `vet/VetController.java`. The vets query is explicitly cached, which hints at expensive or refreshable data. A scheduled job to warm/refresh the cache, or to sync vet and specialty data from an external HR system, is a periodic durable process.

5. **Visit billing / payment** (new capability) --- the domain's biggest *missing* durability candidate. Payment capture → invoice generation → receipt delivery is the canonical saga: an external payment gateway call (retryable, must be exactly-once) followed by dependent steps that need compensation if a later step fails.

#### Priority ranking

| Rank |                  Process                   |                              Why durable                              |                  Effort to demo                  |
|------|--------------------------------------------|-----------------------------------------------------------------------|--------------------------------------------------|
| 1    | **Visit booking + reminder**               | Long-running timer, multiple side effects, exactly-once notifications | Low --- hook exists at `VisitController.java:97` |
| 2    | **Pet registration → external registries** | Distributed multi-step, partial failure, saga                         | Medium                                           |
| 3    | **Visit billing / payment** (new)          | Payment saga, compensation                                            | Medium                                           |
| 4    | **Owner onboarding**                       | Welcome message + CRM sync                                            | Low                                              |
| 5    | **Vet / reference-data sync**              | Scheduled periodic job                                                | Low                                              |

#### Recommendation

Given our intent and that `VisitController` already has a clean insertion point, **visit booking with a reminder** is the ideal first process to make durable --- it exercises the three things durability is *for* (durable timers, retriable side effects, and exactly-once execution) with minimal scaffolding.

### 2b --- Analysis grounded in Temporal

This section re-runs the same analysis, but grounded in [Temporal](https://temporal.io/) as the Durable Execution platform, using the [`temporal-developer` skill](https://github.com/temporalio/skill-temporal-developer) for terminology and Java/Spring Boot patterns. The candidates and ranking are unchanged from 2a; what follows maps each one onto concrete Temporal primitives.

#### Why nothing needs Temporal *today*

Every state change is a single synchronous HTTP handler wrapping **one JPA transaction against one datasource**:

|                 Handler                  |         Persist call         |          Location          |
|------------------------------------------|------------------------------|----------------------------|
| `OwnerController.processCreationForm`    | `owners.save(owner)`         | `OwnerController.java:84`  |
| `OwnerController.processUpdateOwnerForm` | `owners.save(owner)`         | `OwnerController.java:156` |
| `PetController.processCreationForm`      | `owners.saveAndFlush(owner)` | `PetController.java:126`   |
| `PetController.updatePetDetails`         | `owners.saveAndFlush(owner)` | `PetController.java:199`   |
| `VisitController.processNewVisitForm`    | `owners.save(owner)`         | `VisitController.java:109` |

There are no external calls, queues, timers, background jobs, or multi-step orchestration. Each commit is already atomic, so in Temporal terms nothing here needs durable execution yet. Two signals in the code show where the domain *wants* it:

* **Future-dated work already exists.** `VisitController.minVisitDate()` (`VisitController.java:83`) forces every visit ≥1 day out, and `Visit` carries a `date` --- exactly what a Temporal **Timer** (`Workflow.sleep(...)`) serves.
* **An explicit cache implies refreshable data.** `CacheConfiguration.java:37` creates a dedicated `vets` cache served by `VetController.java:70` --- a hint at data worth periodically syncing via a **Temporal Schedule**.

#### Candidates mapped to Temporal primitives

| Rank |                  Process                   |                       Code seam                        |                                                                             Temporal fit                                                                              |
|------|--------------------------------------------|--------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1    | **Visit booking + reminder**               | `VisitController.java:109`                             | **Workflow** with `Workflow.sleep()` until `visit.getDate()` − 1 day, then **Activities** for confirmation + vet notification. Exactly-once via Event History replay. |
| 2    | **Pet registration → external registries** | `PetController.java:126`, `:199`                       | **Saga** --- microchip / insurance / vaccination each an Activity with per-step retries and idempotent compensations.                                                 |
| 3    | **Visit billing / payment** (new)          | ---                                                    | Canonical **Saga**: charge (retryable, exactly-once) → invoice → receipt, compensating on later failure.                                                              |
| 4    | **Owner onboarding**                       | `OwnerController.java:84`                              | Short **Workflow**: welcome message + CRM sync as retriable Activities.                                                                                               |
| 5    | **Vet / reference-data sync**              | `CacheConfiguration.java:37` + `VetController.java:70` | Periodic **Temporal Schedule** driving an Activity that refreshes the `vets` cache.                                                                                   |

#### Recommendation

**Start with visit booking + reminder** at `VisitController.java:109` (right after validation). In Temporal terms it exercises the three things Durable Execution is *for* in one Workflow:

* **Timer** --- `Workflow.sleep()` until the day before the appointment, surviving Worker restarts.
* **Retriable Activities** --- confirmation email + vet notification, each with a Retry Policy.
* **Exactly-once semantics** --- Event History replay guarantees the reminder never double-sends across crashes.

Wiring uses `temporal-spring-boot-starter`: keep `@WorkflowInterface` / `@ActivityInterface` on interfaces, add `@WorkflowImpl` / `@ActivityImpl` on implementations, and inject the auto-configured `WorkflowClient` bean into `VisitController` to start the booking workflow after the save.

Step 3 --- Conclusions
----------------------

Sections 2a and 2b reach the **same conclusions** --- identical candidates, identical ranking, and the same #1 pick (visit booking + reminder). They differ only in framing and sourcing:

|              Aspect              |                                                          2a                                                          |                                                                         2b                                                                          |
|----------------------------------|----------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------|
| **Tooling**                      | Plain, standard Claude Code --- no plugins or skills; treats "durability" as an abstract concept                     | Grounded in **Temporal** , using the installed `temporal-developer` skill                                                                           |
| **Vocabulary**                   | Generic: "durable timer", "saga / compensation path", "scheduled job", "exactly-once"                                | Concrete Temporal primitives: **Workflow** , **Activities** with Retry Policies, **Saga** , **Temporal Schedule** , **Timer**, Event History replay |
| **Line numbers**                 | Cites upstream PetClinic lines (e.g. `VisitController.java:97`, `PetController.java:107`, `OwnerController.java:77`) | Cites the actual checked-out source lines (e.g. `VisitController.java:109`, `PetController.java:126`, `OwnerController.java:84`)                    |
| **"Why nothing needs it today"** | Prose paragraph                                                                                                      | Same point, but as a table listing each handler and its exact persist call                                                                          |
| **Implementation guidance**      | None --- stays at the analysis level                                                                                 | Adds concrete `temporal-spring-boot-starter` wiring                                                                                                 |

Two takeaways:

1. **2b's line numbers are the accurate ones.** 2a's numbers predate the current checkout and drift from the source on disk; 2b corrects them against the code actually read.
2. **The analysis is deliberately run twice** --- 2a frames the question with plain Claude Code, 2b answers it with Temporal specifics via the `temporal-developer` skill. The recommendation is stable across both: make **visit booking + reminder** durable first.
