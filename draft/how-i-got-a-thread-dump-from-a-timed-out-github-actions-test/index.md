---
title: "How I got a thread dump from a timed-out GitHub Actions test"
date: "2026-01-01"
description: "When a JVM test hangs and GitHub Actions cancels the job, the test JVM is often still alive. Three ways to dump it, and an action that does it for you."
canonical: "https://dev.to/maschiojv/how-i-got-a-thread-dump-from-a-timed-out-github-actions-test-hpp"
authors:
  - "felipe-maschio"
image: "hung-test-ci-thread-dump.jpg"
categories:
  - "Java"
  - "Testing"
  - "Debugging"
related_posts:
  - "what-jstack-misses-when-your-java-service-uses-virtual-threads"
  - "virtual-thread-pinning-field-guide"
  - "masking-a-jvm-thread-dump-without-breaking-the-analysis"
---

A test hangs in CI, the job sits there for 30 minutes, and GitHub Actions cancels it. The log says the job exceeded its time limit and nothing about what the test was doing when it got stuck. Running it again locally rarely helps, because a timing problem that shows up on a 2-core runner can be close to impossible to reproduce on a laptop with 16.

What you want in that moment is a thread dump of the test JVM, taken while the test is still hung. I spent some time checking whether GitHub Actions leaves room for that, tried a few ways to get it, and ended up building an action around what I found. I built [ThreadMine](https://threadmine.dev/), the analyzer that action sends dumps to, but none of the collection methods below need it.

## Is the JVM still running after a timeout?

That was the first thing to check, because if GitHub kills the test JVM right away there's nothing left to collect. I wrote a test that deadlocks on purpose and ran it on `ubuntu-latest` and `macos-latest`, with step-level and job-level timeouts. Every time, the forked test JVM was still alive after the timeout. `jcmd` could attach to it, and the dump showed both threads sitting on the locks they deadlocked on. It stays that way until the runner's final "Cleaning up orphan processes", which leaves a window to grab evidence.

The reason it works is in GitHub's [cancellation docs](https://docs.github.com/en/actions/reference/workflows-and-actions/workflow-cancellation). The runner sends SIGINT to the step's entry process, waits 7.5 seconds, sends SIGTERM, waits 2.5 more, and only then kills the whole process tree. For a `run` step the entry process is the shell, and bash exits on the first SIGINT, so the tree kill never happens and the JVMs it started are left running. If your step uses `exec`, or a script that traps those signals, you lose that, so I'd treat the window as something to confirm on your own setup.

## Option 1: a watchdog next to the tests

Apache Kafka does it this way. Its [thread-dump script](https://github.com/apache/kafka/blob/trunk/.github/scripts/thread-dump.sh) starts in the background while Gradle runs the tests, sleeps until five minutes before the timeout, runs `jstack` on every `GradleWorkerMain` process, and the workflow saves the files as artifacts. It doesn't depend on the JVM surviving the cancellation, which suits Kafka, since their step wraps Gradle in the system `timeout` command.

The catch is picking the moment. Five minutes before the limit you might dump a test that's just slow and would have finished, and if you cut it closer you can miss the process altogether.

## Option 2: let Surefire do it

With Maven, Surefire 3.6.0 and later can take a dump when a forked process hits its own timeout:

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>3.6.1</version>
  <configuration>
    <forkedProcessTimeoutInSeconds>600</forkedProcessTimeoutInSeconds>
    <forkedProcessTimeoutExtensionContext>
      <jstack.enabled>true</jstack.enabled>
    </forkedProcessTimeoutExtensionContext>
  </configuration>
</plugin>
```

Surefire writes a `surefire-timeout-jstack-*.txt` file before it kills the fork, and you upload it as an artifact. The Surefire timeout has to be shorter than the GitHub one, or GitHub stops the job before Surefire gets to it. It's Maven only.

## Option 3: dump it in a later step

Since the JVM survives, a step after the tests with `if: failure() || cancelled()` can reach it. I ran this on `ubuntu-latest` with a step timeout and with a job timeout, and in both cases the step ran and found the Surefire fork still stuck in the deadlock. This is the approach the action is built on.

```yaml
- run: ./mvnw -B verify
  timeout-minutes: 25

- name: Dump hung test JVMs
  if: failure() || cancelled()
  run: |
    mkdir -p thread-dumps
    for pid in $(jcmd -l | grep -E 'surefirebooter|GradleWorkerMain' | cut -d' ' -f1); do
      jcmd "$pid" Thread.print > "thread-dumps/$pid.txt"
    done

- uses: actions/upload-artifact@v4
  if: failure() || cancelled()
  with:
    name: thread-dumps
    path: thread-dumps/
```

No guessing a time in advance, and the same step covers Surefire forks and Gradle test workers. `jcmd` comes from the JDK `actions/setup-java` installed. I'd leave a few minutes between the step timeout and the job timeout, so the dump and the upload have room to finish.

## Reading the dump

A test JVM has a lot of threads that have nothing to do with your test: GC, JIT compiler, and whatever Surefire or Gradle use to talk to the build. A classic deadlock is the easy case. When two threads wait on each other's `synchronized` monitor or `ReentrantLock`, the dump ends with `Found one Java-level deadlock` and lists both threads and the locks.

Most hangs aren't a cycle, though. A `CountDownLatch` nobody counts down, a `Future.get()` without a timeout, or a pool with every worker blocked on the same call gets no special section in the dump. I'd start with the test thread (`main` under Surefire, `Test worker` under Gradle), see what it's parked on, and then look for the thread that was supposed to wake it up. Sometimes that takes one stack, sometimes forty.

## Turning it into an action

I didn't want to paste that script into every repo, so it became [threadmine-action](https://github.com/maschiojv/threadmine-action):

```yaml
jobs:
  test:
    runs-on: ubuntu-latest
    timeout-minutes: 30
    permissions:
      contents: read
      pull-requests: write   # optional; needed for PR comments
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with: { distribution: temurin, java-version: 21 }
      - uses: maschiojv/threadmine-action@v1
      - run: ./mvnw -B verify
```

You put it before the test command. It's a JavaScript action, and the real work happens in its `post` step, which only runs when the job failed or was cancelled and, in every timeout I tested, ran before the orphan cleanup. It lists JVMs with `jcmd -l` and keeps the Surefire and Failsafe forks and the Gradle test workers. The Gradle daemon is skipped, since it's alive after every build and says nothing about which test is stuck. With `forkCount=0` the tests run inside Maven itself, so in that case it dumps the Maven process. It also picks up the files Surefire writes on its own timeout.

The dumps always go to a workflow artifact. With analysis on, up to three of them go to ThreadMine, the result lands in the job summary, and with the pull-request permission there's one PR comment that later runs update instead of piling up new ones. On the action's own test, two threads called `worker-A` and `worker-B` each waiting on the other's lock, the comment named the deadlock, both threads and the lines in `DeadlockTest.java` where they stopped.

A plain assertion failure doesn't trigger any of this, because the test JVM has already exited when the `post` step runs. A run you cancel by hand in the middle of the tests does leave a live JVM, though, so the action only comments on the PR when the analysis actually finds a problem.

## What gets sent

With analysis on, the dump goes to [threadmine.dev](https://threadmine.dev/). The repository name isn't sent, and reports made without an account expire after seven days. If thread dumps can't leave your environment, set `analyze: false`. You still get the dump at the right moment, in the artifact, and nothing is sent anywhere.

Without an account the limit is five analyses per hour and 20 per day per IP. GitHub-hosted runners share IPs, so on a busy day the summary says the analysis was skipped and the dump is still in the artifact. Windows runners aren't supported yet, and if the action itself breaks, you get a warning, not a second reason for the job to fail.

The action is MIT and the [source is on GitHub](https://github.com/maschiojv/threadmine-action). Even if you never use it, put a dump step after your tests. The next time a test hangs on CI, you'll have the stacks instead of a rerun and a hope that it hangs again.
