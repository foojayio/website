---
title: "What jstack Misses When Your Java Service Uses Virtual Threads"
date: "2026-10-27"
description: "A service with virtual threads can have thousands of requests waiting while jstack shows idle carriers. How to read Thread.dump_to_file instead, JDK 21 to 25."
canonical: "https://dev.to/maschiojv/what-jstack-misses-when-your-java-service-uses-virtual-threads-23be"
authors:
  - "felipe-maschio"
image: "what-jstack-misses-virtual-threads.jpg"
categories:
  - "Java Core"
  - "Debugging"
related_posts:
  - "virtual-thread-pinning-field-guide"
  - "masking-a-jvm-thread-dump-without-breaking-the-analysis"
  - "benchmark-jdbc-connectors-and-java-21-virtual-threads"
  - "java-virtual-threads-in-action-optimizing-mongodb-operation"
---

Take a Spring Boot service with virtual threads enabled, a HikariCP pool of 10 connections, and roughly 5,000 requests in flight. Latency is climbing. You grab a thread dump and find 43 platform threads, most of the scheduler workers waiting in `ForkJoinPool.awaitWork`, and very little CPU activity.

If you used `jstack`, the requests you're looking for may never have made it into the file. On JDK 21 through 25, the traditional dump doesn't list virtual threads. You can spend quite a while reading the scheduler's stacks without seeing the requests waiting for your database.

I'll use that connection-pool scenario throughout this article. The numbers and dump excerpts are illustrative. I run ThreadMine, and its support for these formats needs some explanation too; I'll get to that after the commands.

If you'd rather watch the flow end to end first, this two-minute walkthrough goes from capturing the dump to sharing the report:

{{< youtube mV39ax1wj5I >}}

## The carrier can be idle while the request waits

`jstack` and `jcmd <pid> Thread.print` show platform threads. That includes the carriers, the scheduler workers on which virtual threads run. When a virtual thread unmounts to wait for I/O or another resource, its carrier is free to run something else.

If most requests are waiting, there may be very little for the carriers to do. Low CPU and idle workers are entirely compatible with a large backlog.

The scheduler uses a `ForkJoinPool`, with default parallelism based on the processors available to the JVM. You can change it with `jdk.virtualThreadScheduler.parallelism`, but I'd leave that alone until I knew what the requests were waiting for. More workers won't help requests queued behind the same ten database connections.

A carrier with a virtual thread mounted can include this line in the traditional dump:

```text
Carrying virtual thread #212
```

From JDK 24 on, the dump also prints that virtual thread's own stack right below the carrier, under a `Mounted virtual thread #212` line. Either way it's useful for identifying the mounted thread. For the thousands of unmounted threads, you need another dump.

## Use Thread.dump_to_file

This command has been available since JDK 21:

```bash
jcmd 4127 Thread.dump_to_file /tmp/dump.txt
```

The JVM writes the file, so `/tmp/dump.txt` is a path in the target process's filesystem. This matters in containers. The process also needs permission to write there; the [capture guide](https://threadmine.dev/en/resources/how-to-take-a-thread-dump) covers those details.

Plain text is the default. For JSON:

```bash
jcmd 4127 Thread.dump_to_file -format=json /tmp/dump.json
```

The documented format names are `plain` and `json`. Existing files aren't overwritten unless you pass `-overwrite`. Use a new filename for each capture so there's something to compare later.

The output also depends on the JDK version. JDK 21 through 24 give you the thread ID, name, a `virtual` marker, and the stack. JDK 25 adds state, timestamps, and lock information. A shortened JDK 25 entry might look like this:

```text
#412 "" virtual WAITING 2026-10-14T19:03:11.489120400Z
    at java.base/java.lang.VirtualThread.park(VirtualThread.java:582)
    ...
    at com.zaxxer.hikari.pool.HikariPool.getConnection(HikariPool.java:179)
    at com.example.orders.OrderRepository.load(OrderRepository.java:48)
    ...
```

There's the request, waiting to acquire a connection. The empty name is common for virtual threads; an application can assign a name if it needs one.

For our example, suppose the file contains 5,004 virtual threads and 4,962 occurrences of `HikariPool.getConnection`. You can check those counts without loading the dump into anything:

```bash
grep -cE '^#[0-9]+ ".*" virtual' dump.txt
grep -c 'HikariPool.getConnection' dump.txt
```

The second command counts matching lines. Open a few of the stacks to check the call path, especially if the method occurs more than once per thread.

At this point, I'd look at connection hold time. If each request holds a connection for 18 ms, ten connections would take roughly nine seconds to clear a backlog of 5,000 requests, with no new arrivals. That gives us a plausible scale for the delay. It doesn't calculate p99, but it tells us where to investigate.

Check slow queries and transaction scope before changing the pool size. A connection held during an unrelated HTTP call, for example, reduces the capacity available to every request behind it. The database may support more concurrency, or it may already be struggling. The dump helps locate the queue; pool and database metrics help explain it.

Virtual threads make waiting cheaper. The database still has to do the work.

## What I'd compare in the next dump

For a slowdown that lasts minutes, take another capture after about 30 seconds. Look at the large stack groups and compare thread IDs within them.

If the same requests are still waiting in the same place, investigate why they aren't progressing. If the IDs have changed but the group stays large, requests may be completing while new ones refill the queue. Those situations can look almost identical if you only compare totals.

I wouldn't read much into 5,004 threads becoming 5,011. I'd be more interested in whether connection acquisition time is rising and whether throughput has stopped keeping up with arrivals. I've been fooled by a single dump more than once, and the second one has always been cheap.

The state labels need the same treatment. `WAITING` is expected in a service doing I/O, and it doesn't tell you by itself whether a virtual thread released its carrier. `RUNNABLE` doesn't establish CPU saturation either. Start with the stack and the resource involved, then check the metrics that could explain the wait.

## JSON helps when there are several executors

For a single pile of requests waiting on HikariCP, the text file is enough to get started. JSON becomes useful when you need to separate work created by different executors or structured-concurrency scopes. It groups threads into containers.

On JDK 25, an entry for a mounted virtual thread can also include its carrier's ID. Here's a reduced container with one thread:

```json
{
  "container": "java.util.concurrent.ThreadPerTaskExecutor@3f1e7a2c",
  "parent": "<root>",
  "owner": null,
  "threads": [
    {
      "tid": "412",
      "time": "2026-10-14T19:03:11.489120400Z",
      "virtual": true,
      "name": "",
      "state": "RUNNABLE",
      "stack": ["..."],
      "carrier": "37"
    }
  ],
  "threadCount": "1"
}
```

Run these against the complete JDK 25 JSON file to count virtual-thread entries with a carrier and list the container sizes:

```bash
jq '[.threadDump.threadContainers[].threads[] | select(.virtual == true and .carrier != null)] | length' dump.json
jq -r '.threadDump.threadContainers[] | "\(.threadCount)\t\(.container)"' dump.json
```

The dump is collected while threads keep running. That carrier count therefore isn't a measurement of how many carriers were busy at one instant. The container breakdown is what I'd use first to find where the threads are accumulating.

Keep the JDK version with the file. Some fields used here aren't available in older releases. Also check for `-Djdk.trackAllThreads=false`, which can leave some virtual threads out of the dump.

## Pinning depends on the JDK version

A request waiting for a connection and a virtual thread blocking its carrier call for different investigations. For the second case, look for `VirtualThread.parkOnCarrierThread`: it's the path where a virtual thread parks while retaining the carrier.

On JDK 21 through 23, this code can take that path when the HTTP call blocks:

```java
synchronized (lock) {
    return httpClient.send(request, BodyHandlers.ofString());
}
```

The relevant part of the stack can look like this:

```text
#37 "" virtual
      java.base/jdk.internal.misc.Unsafe.park(Native Method)
      java.base/java.lang.VirtualThread.parkOnCarrierThread(VirtualThread.java:675)
      java.base/java.lang.VirtualThread.park(VirtualThread.java:593)
      ...
      com.example.price.PriceService.lookup(PriceService.java:32)
```

With JDK 24 and JEP 491, holding a monitor no longer causes this pinning. Native frames and class initialization can still be involved in other cases. Check the runtime version before recommending changes to `synchronized` code.

For pinning that comes and goes, I'd use JFR. The `jdk.VirtualThreadPinned` event has been enabled by default with a 20 ms threshold since JDK 21. A recording gives you frequency and duration, which a couple of dumps can easily miss. The old `jdk.tracePinnedThreads` option was removed in JDK 24.

Finding `parkOnCarrierThread` is useful evidence of that particular parking path. Its absence doesn't rule out every form of pinning. There's more on the version differences in the [pinning reference](https://threadmine.dev/en/resources/virtual-thread-pinning).

## A note on ThreadMine

[ThreadMine](https://threadmine.dev/en/analyze) reads the text file that `Thread.dump_to_file` writes, in both shapes it has had so far: the JDK 21 to 24 one, with no state in the header and no `at` on the frames, and the JDK 25 one, which has both. Virtual threads come through as virtual threads, empty names stay empty, and a thread sitting in `parkOnCarrierThread` is flagged as pinned. I'll be honest about how recent that is: until this week the parser silently dropped every `#22 "" virtual` line, and I only found out while checking this article against real dumps from JDK 21 through 25. The JSON form is still not accepted, so paste the text; the per-container counts are the one thing `jq` still does that the analyzer doesn't.

The change I'd make to a team's incident runbook is straightforward: when the service uses virtual threads, capture `Thread.dump_to_file` as well as the traditional dump. You want the request stacks available before anyone starts drawing conclusions from idle carriers.

*Felipe Maschio is the founder of [ThreadMine](https://threadmine.dev/en), a free JVM thread dump analyzer.*

### References

- [JEP 444: Virtual Threads](https://openjdk.org/jeps/444)
- [JEP 491: Synchronize Virtual Threads without Pinning](https://openjdk.org/jeps/491)
- [JDK 25 jcmd command reference](https://docs.oracle.com/en/java/javase/25/docs/specs/man/jcmd.html)
- [JDK 25 release notes: thread dump lock information](https://www.oracle.com/java/technologies/javase/25all-relnotes.html)
- [Virtual threads in JDK 25](https://docs.oracle.com/en/java/javase/25/core/virtual-threads.html)
