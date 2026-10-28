---
title: "Durable Execution Is a Property, Not a Product"
date: "2026-09-30"
description: "Durable execution is a property your existing database can provide, not a product you must buy. Measured, with receipts you can run yourself."
authors:
  - "nicholas-dhondt"
image: "durable-execution-property-not-product.jpg"
categories:
  - "Opinion"
  - "Java"
related_posts:
  - "task-schedulers-in-java-modern-alternatives-to-quartz-scheduler"
  - "getting-started-with-jobrunr-a-powerful-task-scheduler-in-ja"
  - "carbon-aware-job-processing-with-jobrunr-v8"
---

Three method calls. That is my entire workflow: charge a payment, reserve inventory, send a confirmation email.

Run those three calls on a dedicated workflow engine and here is what happens before your business logic is done: 23 history events are appended, 15 durable state transitions are committed to a database, and 15 gRPC round trips travel between your worker and the orchestration cluster.

That, in one paragraph, is why I want to talk about durable execution. Not because the engines are bad. They are impressive systems built by serious engineers. I want to talk about it because the Java community is being sold a property as if it were a product, and I think most of us already own the property.

Full disclosure before we go further: I work on [JobRunr](https://www.jobrunr.io/en/), an open-source background job scheduler for Java.

## The property and the product

Durable execution means one thing: important work survives crashes and resumes instead of starting over. Your three-step order job should not re-charge the customer because a pod got OOM-killed between steps two and three. That is the property, and you want it for nearly everything that runs in the background.

Somewhere along the way, the property got a product category. Workflow engines like Temporal deliver durability through deterministic event-sourced replay: the engine records every event in a workflow's life, and after a crash it re-runs your orchestration code from the top, feeding it recorded results until it catches up to where it died. It is a genuinely elegant model, but it is also a heavy one. Your orchestration code must be deterministic, which means no clock, no random values, no I/O outside of activities. Changing a running workflow becomes a versioning discipline. And operationally you now run a second distributed system: Temporal's own docs describe four independently scaling services plus a dedicated persistence store.

![What each route asks you to operate: a workflow engine adds a cluster of four services plus its own persistence database next to your application, while a database-backed scheduler runs embedded in your application against the database you already have](workflow-engine-vs-jobrunr-architecture.png)

Here is the thing most vendors don't mention. Event-sourced replay is one implementation of durable execution. A checkpoint in a database row is another implementation: run a step, write down that it finished, and on retry skip everything that is already written down. Both approaches survive the same crashes. They just pay wildly different prices, and the difference is measurable.

## Exactly-once is not on the menu anyway

Before we measure anything, we need to clear up the argument that usually ends this discussion: "yes, but the engine gives me exactly-once."

It does not, and the vendors say so themselves. Temporal's documentation states plainly that activities may be executed more than once. The workflow logic replays as if it ran once, but the steps that touch the real world, the ones that charge cards and call APIs, **run at-least-once**. A process can always die after the side effect happened and before the record of it was persisted. No architecture on earth closes that window, because the universe does not offer transactional semantics across your process and someone else's payment API.

That is why every durable execution engine tells you to make your steps idempotent. Whether you run a workflow engine or a job queue, the actually hard part, making the money-moving step safe to repeat, is your job either way. Pass a stable idempotency key to your payment provider and a duplicate attempt becomes a no-op.

Once you see that, the question changes shape. It is no longer "safe engine versus unsafe jobs." It is: two implementations of the same property, both requiring idempotent steps. One needs a new distributed system and a determinism contract. The other needs the database you already run. So what exactly does the heavier one cost?

## What the property costs on the stack you already have

Let me show you the lighter implementation first, because I think seeing it defuses the magic. You do not need a framework to get checkpointed steps. A table and thirty lines of JDBC will do:

```sql
create table jobs (
    id              uuid primary key,
    type            text  not null,
    payload         jsonb not null,
    state           text  not null default 'ENQUEUED',
    attempts        int   not null default 0,
    completed_steps jsonb not null default '[]'
);
```

```java
void runStepOnce(Connection con, UUID jobId, String step, SqlRunnable sideEffect)
        throws Exception {
    try (var check = con.prepareStatement(
            "select jsonb_exists(completed_steps, ?) from jobs where id = ?")) {
        check.setString(1, step);
        check.setObject(2, jobId);
        try (var rs = check.executeQuery()) {
            if (rs.next() && rs.getBoolean(1)) return;   // already done, skip
        }
    }

    sideEffect.run();                                     // the real world happens here

    try (var mark = con.prepareStatement(
            "update jobs set completed_steps = completed_steps || to_jsonb(?::text) where id = ?")) {
        mark.setString(1, step);
        mark.setObject(2, jobId);
        mark.executeUpdate();                             // checkpoint, one UPDATE
    }
}
```

Add a polling loop, a retry counter, and a locked_until column for crash recovery, and you have built durable execution on infrastructure your team already operates, monitors, and backs up. Between the side effect and the checkpoint there is an at-least-once window, exactly like the engine has, and you close it the same way: idempotent steps.

I am not seriously suggesting you hand-roll this for production. Zombie job detection, exponential back-off, dashboards, and distributed locking are the parts that eat your weekends. Libraries exist that do all of it on top of your existing database; [JobRunr](https://www.jobrunr.io/en/) is the one I work on, and the entire workflow from the top of this article can be written in just one method:

```java
@Job(name = "Fulfill order %0", retries = 5)
public void fulfillOrder(String orderId, JobContext jobContext) {
    jobContext.runStepOnce("charge-payment", () ->
            paymentService.charge(orderId, jobContext.getJobId().toString()));
    jobContext.runStepOnce("reserve-inventory", () -> inventoryService.reserve(orderId));
    jobContext.runStepOnce("send-confirmation", () -> mailService.confirm(orderId));
}
```

You do not have to treat this as a black box either. JobRunr 9, [which released Sept 30th](https://www.jobrunr.io/en/blog/v9-release/), shows the progress on screen: the job history in the dashboard now has a Job Progress Chart View that draws every attempt step by step. Here is one of our demo jobs, an invoice run whose payment provider timed out.

![JobRunr 9's Job Progress Chart View of a durable invoice job](jobrunr-9-chart-view-retry.png)

The first attempt fails at the charge-card step. The retry does not start over: the two steps that already finished are marked as skipped, the job resumes exactly at charge-card, and next to every step you can see how long it took. All of this is part of [JobRunr OSS v9](https://github.com/jobrunr/jobrunr/releases/tag/v9.0.0).


But the point stands without any library. The property is available on your current stack. The question is only what the product costs on top of it.

## The receipts, what about performance?

We implemented the same three-step order workflow twice, once with JobRunr and Postgres, once with the Temporal Java SDK, and pushed 1000 orders through each with 24 workers. And because a rigged benchmark would be worse than none, every judgment call went in the engine's favor: Temporal ran the real self-hosted production image against its own PostgreSQL with 512 history shards (its production default), not the in-memory dev server. Workflow starts were issued from 24 concurrent threads. We even raised the SDK's task pollers from the default 5 to 24, because the default quietly throttles fast activities and we wanted to measure the engine, not a misconfiguration. The full harness is on GitHub, so you can run all of it yourself.

On a dedicated 8-core Hetzner server, the same 1000 orders:

| | JobRunr on Postgres | Temporal, self-hosted |
| :--- | ---: | ---: |
| Instant steps | 1.8 s | 13.6 s |
| 25 ms of real work per step | 8.4 s | 13.7 s |
| CPU, all processes | 13.3 cpu-s | 83.2 cpu-s |
| Peak memory | 388 MB | 868 MB |

The row worth staring at is the second one. When we added 75 ms of simulated API latency per order, JobRunr's total grew from 1.8 to 8.4 seconds, because it was mostly waiting on the actual work. Temporal's total did not move. The real work hid entirely inside the engine's own overhead. When adding work is free, the orchestrator is the bottleneck, not your code.

Then we asked the databases what actually happened. For those 1000 orders, the job queue committed 1,181 Postgres transactions, roughly one insert and two updates per order. The engine committed 113,218 transactions across its two databases. Same three steps, same durability property, 95 times the durable writes. And none of that is a bug. It is the documented design: 23 events per workflow, a fresh workflow task scheduled after every single activity so a worker can poll, advance your code by one line, and respond over gRPC. Temporal's own capacity guide measures cluster throughput in state transitions per second, and Temporal Cloud bills per action.

## When the engine earns its bill

Here is where I am supposed to tell you the engine is always wrong, and I will not, because it is not true.

Those 113 transactions per order buy real things: a complete event history of every execution, replay-based debugging, queryable workflow state, signals, timers, child workflows, and orchestration across services written in different languages.

The question is: do you need them? Does your orchestration branch so deeply that you need full replay and workflow versioning? Does one workflow coordinate services in several languages? Do you need signals, queries, and child workflows as first-class primitives?

![Decision tree: three yes/no questions. Answer yes to deeply branching orchestration, multi-language coordination, or first-class signals and child workflows, and the workflow engine is your tool. Answer no to all three, and a database-backed scheduler covers you](workflow-engine-decision-tree.png)

If you answer yes, take the engine and do not look back. The heaviest orchestration problems are exactly what it was built for, and its costs are the honest price of those capabilities. Temporal's own co-creator frames the tool the same way: it is not meant to be a replacement for queues, it is a different way to design applications.

But be honest about your answers. For most Java teams, for most background work, all three are no. The reflex to reach for the engine anyway is the same one that gave mid-sized teams fifty microservices and a Kafka cluster for 200 messages per day. We over-buy orchestration in this industry, repeatedly, and durable execution is having exactly that moment right now.

## Conclusion

Durable execution is a property worth wanting for almost everything you run in the background. It is persistence, checkpointed steps, automatic retries, and idempotent side effects. You can get that property today from the database you already operate, whether through a small library or, if you are stubborn, thirty lines of JDBC. The engines deliver the same property through a far heavier mechanism, and attach capabilities most background jobs will never call, at a price you now know how to measure: on our benchmark, roughly six to ten times the CPU, twice the memory, 95 times the database transactions, and a second distributed system on the on-call rota.

Buy the product when you need the product. Never buy it to get a property you already own.

---

*Nicholas D'hondt works on [JobRunr](https://www.jobrunr.io/en/), an open-source job scheduler for Java.*

*The benchmark harness, raw results, and instrumentation from this article are available at [github.com/iNicholasBE/temporal-vs-jobrunr-benchmark](https://github.com/iNicholasBE/temporal-vs-jobrunr-benchmark).*
