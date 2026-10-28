---
title: "BoxLang 1.18.0 Released: Every Cloud, Every Node, Every Agent"
date: "2026-10-21"
description: "BoxLang 1.18.0 adds Azure Functions, cluster-safe scheduled tasks, a more automation-friendly CLI, and refreshed docs for developers and AI agents."
authors:
  - "luis-majano"
image: "cover.jpg"
categories:
  - "BoxLang"
  - "Java"
  - "Cloud"
  - "AI"
canonical: "https://www.ortussolutions.com/blog/boxlang-1180-released-every-cloud-every-node-every-agent"
related_posts: []
---

**Azure joins the serverless family. Scheduled tasks become cluster-safe. And the whole platform gets documentation and skills built for developers and AI agents working side by side.**
	
If you run a technology organization, you are being asked to do three things at once: ship faster, keep the estate you already have running, and make AI part of how your teams work. Most stacks make you choose. BoxLang 1.18 is another step toward not having to.

**BoxLang** is the **AI-native software productivity platform** for building, modernizing and running applications, with developers and AI agents working together. One runtime, many deployment targets, no rewrite required.

This is no longer a roadmap promise. We have moved several clients from Adobe ColdFusion and Lucee to BoxLang, and they are running in production today. That includes more than 1.5 million lines of ACF code migrated in a matter of a few months, not years. Those teams are lowering licensing and infrastructure costs, retiring years of technical debt and gaining confidence with every release. Most of all, they now have a new level of productivity and automation open to them: modern language features, full Java interop, serverless deployment and AI agents that understand their codebase.

1.18 builds on that foundation in the areas that matter most to the people who approve platforms: portability, reliability, operability and security.

[Read the full What's New in 1.18.0](https://boxlang.ortusbooks.com/readme/release-history/1.18.0) for every ticket, or keep reading for the story.

---

## At a glance

* **Azure Functions runtime.** BoxLang now runs serverless on AWS, Google Cloud and Microsoft Azure with one handler contract.
* **Server fixation for scheduled tasks.** In a cluster, a job runs on exactly one server, every time.
* **A CLI built for automation.** Project-level config discovery and machine-readable scheduler reports for scripts and agents.
* **New interception points.** Observe BoxLang class creation and initialization lifecycle events when those hooks fire, with expected exceptions such as superclass creation, `noInit` instantiation, and initialization failures.
* **Hardened serverless routing** on Lambda and Google Cloud Functions, plus response and error handling that is consistent across clouds.
* **Every 1.17.x hotfix included.** Upgrade once, get all six patch releases.
* **Docs and skills refreshed across the board.** New sections for agentic development, BoxLang AI, browser automation and more.

---

## One codebase, three clouds


![One set of BoxLang handlers deployed to AWS Lambda, Google Cloud Functions and Azure Functions](serverless-clouds.png)


Cloud strategy is rarely a single-cloud decision for long. Acquisitions, regional requirements and pricing all pull in different directions. The question for your platform is how expensive it is to move.

With 1.18, BoxLang has an official **Azure Functions runtime**, joining AWS Lambda and Google Cloud Functions. All three share the same design: the same `handlers/` routing convention, the same build-time `manifest.json`, and the same `run( event, context, response )` contract. Your handlers move between providers unmodified.

```js
// handlers/hello.bx - runs on AWS, Google Cloud and Azure
class {

    function run( event, context, response ) {
        return { message: "Hello from BoxLang, wherever you deployed me" }
    }

}
```

The Azure runtime ships with a turnkey starter project: Gradle dependency management, unit and integration testing, class compilation caching, environment-aware configuration, the official Azure Functions Gradle plugin for local run and deploy, and GitHub Actions to test, build and release.

**Why it matters to you:** serverless without lock-in. Your business logic stays in one language and one contract while the deployment target becomes a configuration decision.

[Azure Functions guide](https://boxlang.ortusbooks.com/getting-started/running-boxlang/azure-functions)

---

## Scheduled tasks that behave in a cluster

Anyone who has deployed the same scheduler to several servers knows the problem. Your nightly cleanup, your billing report, your cache warm-up: each one quietly runs once per node, at the same moment. Double invoices and duplicate emails are not a code problem. They are a coordination problem.

1.18 introduces **server fixation**. One call tells BoxLang to run a task on a single node:

```js
task( "nightly-cleanup" )
    .call( () => createObject( "MaintenanceService" ).cleanup() )
    .everyDayAt( "02:00" )
    .onOneServer()
```

Here is what makes it production-grade:

* **Distributed lock.** Nodes race to claim each run through a shared cache such as Redis. The first one wins, the rest skip.
* **Crash-safe.** If the winning node dies mid-run, its distributed lock remains active until the lock TTL expires. Other nodes may skip the next scheduled poll while that lock is still valid, and a surviving node can take over on a later poll after expiration.
* **Sized to the real schedule.** For monthly and business-day schedules, the lock timeout is calculated from the time remaining until the next occurrence. Sub-minute schedules use a 60-minute default timeout, which can suppress later runs after a crash until that timeout expires.
* **Observable.** Task statistics show the server where the task executes. The shared lock entry records the winning node through `serverHost` and `serverIp`.

The scheduler also picked up a reliability pass: fixes for `.between()` with `.everyHour()` re-firing, a duplicate scheduler name error, a failure handler that could disable a task, and `everyMonthOn( 31 )` now landing on the last day of shorter months.

**Why it matters to you:** horizontal scale without duplicate side effects.

[Scheduled tasks guide](https://boxlang.ortusbooks.com/boxlang-framework/asynchronous-programming/scheduled-tasks)

---

## Operations and automation, built in

A platform for humans and agents has to be scriptable. 1.18 makes the CLI friendlier to both:

```bash
# Config is discovered from ./.boxlang.json in the directory you run from
boxlang report.bxs

# A full scheduler report for people, or as JSON for tools and agents
boxlang schedule
boxlang schedule --json | jq '.tasksFile.tasks'
```

* **Per-project configuration.** Drop a `.boxlang.json` next to your scripts and the CLI picks it up. User-level settings live in your BoxLang home, and explicit flags and environment variables always win. We documented the whole lookup order, including how it compares to `.env` files.
* **A readable scheduler report** with loaded schedulers, tasks and live stats, with credentials stripped from the output.
* **Clearer errors**, including a better message when a module requested from the CLI is missing.

[Customizing boxlang.json in the CLI guide](https://boxlang.ortusbooks.com/getting-started/running-boxlang/cli-scripting)

## The bottom line

Every BoxLang release has the same goal: **help you do more with the code you already have, and stop paying for the complexity you don't need**. 1.18 adds a third cloud, scheduling that's safe in a cluster, a CLI that people and agents can both script, and documentation and skills that give your AI tools real context about your platform.

For teams still running CFML, the path is proven. Our clients are already in production on BoxLang, with lower costs, less technical debt and a platform ready for whatever comes next. The migration you have been putting off is smaller than you think.

**Upgrade to 1.18 today**, and if you want help planning a CFML migration, [talk to the Ortus team](https://www.ortussolutions.com). We have done it before, at scale, and we can do it with you.

Productivity is the point. Let's build.
