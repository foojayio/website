---
title: "When an agent dies after saying yes"
date: "2026-01-01"
description: "A worker that dies after AGREE never sends its result. How the manager handles expired assignments, and what happens when a late response turns up anyway."
authors:
  - "mauro-mura"
image: "cover.jpg"
categories:
  - "Java"
  - "Design Patterns"
  - "Databases"
canonical: "https://bitautonomi.substack.com/p/when-an-agent-dies-after-saying-yes"
---

A manager has a PDF to turn into text and three workers that could do it. It sends them a call
for proposals, each one answers with an estimate, and the lowest estimate gets an `AGREE`. That's the negotiation of
Contract Net, the protocol from
[the post on negotiating without an LLM](https://foojay.io/today/your-agents-dont-need-an-llm-to-negotiate/).

Then the worker holding the job gets `kill -9`.

After `AGREE`, the protocol leaves the worker two possible messages, `INFORM` with the result or
`FAILURE` with a reason. A process that's gone says neither. The messages go through a stream, so
there's no connection to drop and no error to catch, and the manager stops hearing about
that job.

The example runs on Agenor, which delivers the messages and defines which replies the protocol
allows. Noticing that a result is missing, and deciding what happens next, is left to the
application. The rest of this post is that code, from a job dispatcher I wrote.

## Silence is the only signal

So the manager decides by itself how long to wait. When it awards the job it sets a deadline of
twice the worker's estimate, plus a margin that defaults to 30 seconds.

The estimate is doubled because the worker is guessing. The margin is there for short jobs. If the
estimate is 2 seconds, doubling it gives 4, and any small hiccup is enough to overrun that.

Where the deadline lives mattered more than the formula. I put it in the database, in the job's
row, next to its state, so a restart of the manager loses nothing; a timer in memory would have
gone down with the process. Once a second the manager looks for `ASSIGNED` jobs whose deadline
has passed and gives them back:

```java
@Behavior(type = CYCLIC, interval = "1s", initialDelay = "5s", autoStart = true)
public void expireDueAssignments() {
    for (var job : store.dueForReassignment(clock.instant(), config.batchSize())) {
        var now = store.fail(job.jobId(), job.attempt(), job.assignee(),
                DEADLINE_EXCEEDED, true);
        now.ifPresent(state -> log.warn(
                "job {} on {} ran past its deadline (attempt {}) -> {}",
                job.jobId(), job.assignee(), job.attempt(), state));
    }
}
```

That's the method from `JobManager`, minus its tracing span. After a restart the same loop picks
up whatever expired while the manager was down, so there's no separate recovery code.

The `initialDelay` came later. A restarted manager used to run its first sweep before reading the
results that had arrived while it was away, and jobs that had finished looked overdue and
ran again. I gave the sweep a five-second grace period at start, so those results are usually read
first. It isn't a guarantee. A result that shows up after the five seconds finds its job already
back in negotiation. The job runs again, and the check in the next section keeps that late result
from counting.

## The result that arrives late

The worker might not be dead, only slow, and finish after its job was given to someone else. Then
two results can arrive for the same job.

Every assignment increments an `attempt` counter on the row, and the worker receives that number
with the `AGREE`. A result counts only if the row is still `ASSIGNED`, at that attempt, to that
worker. The check is the `WHERE` clause of the statement that records the result:

```sql
UPDATE jobs SET state = 'DONE', output_ref = ?, deadline_at = NULL, updated_at = now()
WHERE job_id = ? AND state = 'ASSIGNED' AND attempt = ? AND assignee = ?
```

If no row matches, the manager logs the result as stale and moves on. The same check
also rejects duplicate messages delivered by the transport.

The work itself can still happen twice, because a slow worker may keep executing a job after it has
been reassigned. Only the first matching result moves the row to `DONE`.

## The row first, then the message

When a negotiation ends, the manager writes the assignment and then sends the `AGREE`. A crash
between the two loses one of them, and the order decides which.

If the `AGREE` is lost, the row says `ASSIGNED` with a deadline and the worker never heard about
it, so the job sits idle until the deadline passes. In the opposite order a worker would be
running a job the database still lists as `SUBMITTED`; the job would go back to negotiation, and the
first worker's result discarded by the check above. I write the row first, and accept that a crash
at the wrong moment costs one deadline.

## The job that kills its worker

One of the failure tests in the example, `ProcessorFailureIT`, sends a job that kills whatever
process runs it, with three workers up. The first takes the job and dies. When its deadline
passes, that worker is excluded from the job's next assignments and the job goes back to negotiation, where the
next worker takes it and dies the same way. After the third attempt the job is `FAILED` with
"deadline exceeded". The test then submits a good job of the same kind, and it waits in
`SUBMITTED` because nobody is left to run it. It finishes only once the test starts a fourth
worker.

Three is the cap on attempts. Without one, that job would keep going round and take a worker with
it each time. It's also why I don't restart workers when they die, since a job that kills its
worker would kill every restart too. With three workers and a cap of three, a single bad job can
kill all three workers.

## Try it

Agenor has no supervision and doesn't restart agents, so everything above is application code, in
[`agenor-example-job-dispatcher`](https://github.com/mauro-mura/agenor-example-job-dispatcher/tree/v0.1.0)
on Agenor 0.40.0 from Maven Central. With Java 21, Maven and Docker or Podman:

```bash
mvn package -DskipTests
bash demo/demo.sh
```

The script starts a manager and three workers, submits twelve PDFs and kills a worker holding a
job. It exits 0 only if all twelve end `DONE` and at least one was reassigned. In Jaeger, at
`localhost:16686`, the job id it prints shows `job.auction` (the negotiation), `job.expire`, `job.auction`
again and `job.execute`. The killed worker's span never ended, so it isn't there.

The conversation the manager opened with that worker doesn't close either. Agenor keeps it in
memory in the `AGREED` state, waiting for a result that won't come, and Agenor's periodic cleanup of
conversations leaves that one alone, because the cleanup only removes conversations that have ended. One stays behind for every
worker that dies after the `AGREE`.

*Update: since Agenor 0.41.0 the cleanup ends that conversation too, by default about five minutes
after the `AGREE`. The deadline in the row, the check on `(attempt, assignee, state)` and the cap
on attempts are still application code.*
