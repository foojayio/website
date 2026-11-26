---
title: "Debugging Virtual Threads in Java 25: A Hands-On Guide to JSON Thread Dumps"
date: "2026-12-16"
description: "Diagnose virtual thread contention in Java 25 using JSON thread dumps, built-in JVM diagnostics, and a lightweight HTTP server."
authors:
  - "tiago-souza"
image: "cover.jpg"
categories:
  - "Java Core"
---

*How a small, framework-free HTTP server exposes thread contention using built-in JVM diagnostics.*

Virtual threads make it practical to handle large numbers of concurrent tasks without dedicating a platform thread to each request. But they do not make synchronization problems disappear. A single `synchronized` block can still turn concurrent requests into a queue.

How do you see that happening inside a running application?

In this tutorial, we will build a tiny HTTP server using only the JDK, deliberately introduce monitor contention, and generate JSON thread dumps from application code. Then we will look at real captures from JDK 21 and JDK 25 to see how Java's diagnostics have evolved.

No Spring Boot, no third-party libraries, no `jstack` required for the experiment. Just Java, `curl`, and a shell.

## What changed in JDK 25?

The `HotSpotDiagnosticMXBean.dumpThreads()` API has existed **since JDK 21**. It can write thread dumps in plain text or JSON without requiring the application to invoke external diagnostic commands. JDK 25 did not introduce this method; it made the resulting dumps more useful for analyzing contention.

As documented in the [JDK 25 release notes (JDK-8356870)](https://www.oracle.com/java/technologies/javase/25all-relnotes.html), dumps produced by this API and by `jcmd Thread.dump_to_file` now include lock information. This matters when virtual threads are involved: rather than only seeing a stack trace, we can connect waiting threads to the object monitor they are waiting for.

That is the question we will answer experimentally: **which virtual thread owns the monitor, and which virtual threads are blocked trying to enter it?**

## Build a Java HTTP server without a framework

The JDK provides [`com.sun.net.httpserver.HttpServer`](https://docs.oracle.com/en/java/javase/25/docs/api/jdk.httpserver/com/sun/net/httpserver/HttpServer.html) in the `jdk.httpserver` module. Despite the `com.sun` package prefix, this is a documented JDK API suited to small HTTP servers and local testing.

Our application exposes three endpoints:

| Endpoint | Method | Purpose |
| --- | --- | --- |
| `/block` | `GET` | Simulate monitor contention by sleeping while holding a shared lock. |
| `/dump` | `POST` | Generate a JSON thread dump programmatically. |
| `/leak` | `GET` | Intentionally retain 5 MiB of heap memory per request to simulate a memory leak. |

We will call all three endpoints. The main experiment focuses on thread contention and JSON diagnostics; the memory-retention example is a short, separate demonstration because thread dumps are not heap dumps.

Here is the complete `JvmDiagnosticsApp.java` used in this experiment:

```java
import com.sun.management.HotSpotDiagnosticMXBean;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;

public class JvmDiagnosticsApp {

    private static final Object LOCK = new Object();

    private static final Queue<byte[]> MEMORY =
            new ConcurrentLinkedQueue<>();

    public static void main(String[] args)
            throws IOException {

        var server = HttpServer.create(
                new InetSocketAddress("127.0.0.1", 8080),
                0
        );

        server.setExecutor(
                Executors.newVirtualThreadPerTaskExecutor()
        );

        server.createContext("/block", exchange -> {
            if (!exchange.getRequestMethod().equals("GET")) {
                respond(exchange, 405, "Method not allowed");
                return;
            }

            synchronized (LOCK) {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            respond(exchange, 200, "Completed");
        });

        server.createContext("/leak", exchange -> {
            if (!exchange.getRequestMethod().equals("GET")) {
                respond(exchange, 405, "Method not allowed");
                return;
            }

            MEMORY.add(new byte[5 * 1024 * 1024]);

            respond(exchange, 200,
                    "Retained blocks: " + MEMORY.size());
        });

        server.createContext("/dump", exchange -> {
            if (!exchange.getRequestMethod().equals("POST")) {
                respond(exchange, 405, "Method not allowed");
                return;
            }

            var bean = ManagementFactory
                    .getPlatformMXBean(
                            HotSpotDiagnosticMXBean.class);

            var path = Path.of(
                    "thread-dump-" +
                    System.currentTimeMillis() + ".json"
            ).toAbsolutePath();

            try {
                bean.dumpThreads(
                        path.toString(),
                        HotSpotDiagnosticMXBean
                                .ThreadDumpFormat.JSON
                );

                respond(exchange, 200,
                        "Dump generated: " + path);

            } catch (IOException e) {
                respond(exchange, 500, e.getMessage());
            }
        });

        server.start();

        System.out.println(
                "Server running at http://localhost:8080"
        );
    }

    private static void respond(
            HttpExchange exchange,
            int status,
            String message
    ) throws IOException {

        byte[] bytes = message.getBytes(
                StandardCharsets.UTF_8);

        exchange.sendResponseHeaders(status, bytes.length);

        try (var output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}
```

There are three details worth highlighting.

First, the HTTP server listens on `127.0.0.1:8080` instead of a public network interface. This limits access to our deliberately unsafe diagnostic endpoints.

Second, every incoming HTTP task runs in a virtual thread:

```java
server.setExecutor(
        Executors.newVirtualThreadPerTaskExecutor()
);
```

Third, the `/block` handler makes a deliberate mistake:

```java
synchronized (LOCK) {
    try {
        Thread.sleep(5000);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}
```

Only one thread at a time can enter this critical section. The others must wait for the same monitor. The five-second sleep makes that wait easy to capture. **This is monitor contention, not a deadlock.**

## Run the application on Linux

Install a JDK 25 distribution and make sure `java` points to it:

```bash
java -version
```

For the experiments described here, the runtime reported `25.0.5+8-LTS`. Any compatible JDK 25 build should support the APIs used below.

From the directory containing `JvmDiagnosticsApp.java`, start the server using Java's source-file launcher:

```bash
java --add-modules jdk.httpserver,jdk.management JvmDiagnosticsApp.java
```

The application prints:

```text
Server running at http://localhost:8080
```

No build file, dependency manager, or application framework is required.

### JDK 21 reproduction: avoiding carrier starvation

When reproducing the contention experiment on JDK 21, virtual-thread pinning requires additional care. A virtual thread that sleeps inside a `synchronized` block remains pinned to its carrier. If only one carrier is available, the `/block` handler can occupy it and delay the virtual-thread `/dump` handler until after the intended capture time.

To reduce this risk for the ten-request experiment, start the application on JDK 21 with increased scheduler parallelism:

```bash
java -Djdk.virtualThreadScheduler.parallelism=16 \
     --add-modules jdk.httpserver,jdk.management \
     JvmDiagnosticsApp.java
```

This provides additional carrier capacity for the concurrent `/block` requests and the `/dump` request. It reduces the risk of starvation but does not eliminate monitor contention or guarantee the precise timing of a dump.

Alternatively, capture the JSON thread dump externally with `jcmd`, without relying on the `/dump` HTTP handler to be scheduled:

```bash
jcmd
jcmd 12345 Thread.dump_to_file -format=json thread-dump-jdk21.json
```

Use the first command to find the Java process ID and replace `12345` with that ID. Run the second command while the `/block` requests are still in flight, using a filename that does not already exist. For this alternative, replace the concurrent workload's `/dump` curl command with the `jcmd` command.

## Call all three endpoints with curl

With the server running, open a second Linux terminal. We can exercise every endpoint from the original Java class without installing a framework or HTTP client library.

### 1. Call `/block` — one slow request

```bash
curl -fsS http://127.0.0.1:8080/block
```

About five seconds later, the response is:

```text
Completed
```

This single request confirms that the handler works. In the next section, we will send concurrent requests to make monitor contention visible.

### 2. Call `/leak` — intentionally retain heap memory

```bash
curl -fsS http://127.0.0.1:8080/leak
```

On a freshly started application, the response is:

```text
Retained blocks: 1
```

Each request adds a new 5 MiB byte array to a static concurrent queue, so the objects remain reachable. To demonstrate the growth, call it three more times:

```bash
for n in 1 2 3; do
  curl -fsS http://127.0.0.1:8080/leak
  printf '\n'
done
```

The block counter increases with every request (for example, to `4` after the three additional calls). This deliberately retains about 5 MiB of array data per request; it is **not** evidence of a memory leak in the HTTP server itself. Restarting the process clears the retained objects.

**Important:** `/dump` produces a **thread dump**, not a heap dump. The JSON generated in the next step will not tell us which byte arrays are retaining heap memory. Diagnosing the `/leak` scenario properly would require heap-oriented tools and is beyond the scope of this article.

### 3. Call `/dump` — generate JSON diagnostics

```bash
curl -fsS -X POST http://127.0.0.1:8080/dump
```

A successful response names the generated file, for example:

```text
Dump generated: /home/user/jvm-demo/thread-dump-1795718194975.json
```

The path is illustrative: the application writes the file into its current working directory. We can list the generated dumps with:

```bash
ls -lh thread-dump-*.json
```

The relevant Java API is short:

```java
var bean = ManagementFactory.getPlatformMXBean(
        HotSpotDiagnosticMXBean.class);

bean.dumpThreads(
        path.toString(),
        HotSpotDiagnosticMXBean.ThreadDumpFormat.JSON
);
```

The [API contract](https://docs.oracle.com/en/java/javase/25/docs/api/jdk.management/com/sun/management/HotSpotDiagnosticMXBean.html) requires an **absolute path to a file that does not already exist**. The sample generates a timestamp-based name for convenience; in a production diagnostic utility, also handle filename collisions and protect access to the generated data.

Running `/dump` when the application is idle is useful for validating the endpoint, but it will usually **not** capture the contention. For that, the snapshot needs to be taken while multiple `/block` requests are in flight.

## Reproduce thread contention with Bash and curl

A single request is not enough to make contention obvious. We need several requests attempting to enter the same `synchronized` block at roughly the same time.

Keep the server running. In a second terminal, start ten requests in the background:

```bash
for n in {1..10}; do
  curl -fsS -o /dev/null \
    -w "request ${n}: %{time_total}s\n" \
    http://127.0.0.1:8080/block &
done

# Capture the threads while requests are still in flight.
sleep 1
curl -fsS -X POST http://127.0.0.1:8080/dump

# Wait for the background curl processes to finish.
wait
```

Each request spends approximately five seconds inside the critical section after it obtains the monitor. Because the lock is shared, the requests finish one after another rather than all after five seconds. Actual timings depend on scheduling and other overhead.

Notice the placement of the dump request: we send it **before** waiting for the blocking requests to finish. A thread dump captures what is happening around the moment of collection. Running `/dump` after `wait` would miss the contention we want to investigate.

## Read a real JDK 25 thread dump

In one of our JDK 25 captures, six virtual threads appeared in the HTTP executor: four `BLOCKED`, one `TIMED_WAITING`, and one `RUNNABLE` (the thread collecting the dump).

The owner of the contested monitor was virtual thread `69`. Here is a shortened excerpt from the captured JSON; unrelated stack frames and fields are omitted:

```json
{
  "tid": "69",
  "virtual": true,
  "state": "TIMED_WAITING",
  "monitorsOwned": [
    {
      "depth": 4,
      "locks": ["java.lang.Object@79dd2e80"]
    }
  ]
}
```

Its stack included `Thread.sleep()` and `JvmDiagnosticsApp.lambda$main$0(...)`. That is precisely what we expect: the thread is sleeping **while still holding the monitor**.

Another virtual thread, `70`, reported:

```json
{
  "tid": "70",
  "virtual": true,
  "state": "BLOCKED",
  "blockedOn": "java.lang.Object@79dd2e80"
}
```

Threads `71`, `74`, and `75` reported the same `blockedOn` value. We can now connect the two sides of the problem:

- **Owner:** virtual thread `69`, holding `java.lang.Object@79dd2e80`.
- **Waiters:** virtual threads `70`, `71`, `74`, and `75`, all blocked on that monitor.
- **Code path:** the `/block` handler, which sleeps inside `synchronized (LOCK)`.

This is a diagnosis, not just a collection of stack traces. The matching `monitorsOwned` and `blockedOn` values let us identify the shared bottleneck directly from the JSON.

The thread IDs and object identity strings will be different in your run. The important part is the **relationship** between the fields.

## Why the timing of a dump matters

Not every dump shows a problem, even if the application experienced one a few seconds earlier.

We also captured a JDK 25 dump after the workload had finished. At that moment, the only virtual thread in the request executor was the thread performing `/dump`; no application request was recorded as `BLOCKED`.

Both captures are valid. One caught contention in progress; the other did not. This is why a single quiet snapshot cannot prove that an application never suffers from contention.

For intermittent issues, consider collecting several dumps during the incident and correlating them with request latency and application metrics. Be mindful that dumps are operational data and may contain sensitive stack information.

Unlike a traditional stop-the-world thread dump, this diagnostic format does not freeze the entire JVM to capture every thread at exactly the same instant. The API may include some or all virtual threads, so its output should be interpreted as diagnostic evidence rather than a perfectly consistent global snapshot.

## Comparing JDK 21 and JDK 25

We ran the same small experiment previously on JDK `21.0.7+8-LTS-245` and then on JDK `25.0.5+8-LTS`.

In the JDK 21 JSON capture, we could see virtual-thread stacks in a `ThreadPerTaskExecutor`, including the application handler and the sleeping virtual thread. But the entries did **not** expose the explicit `state`, `blockedOn`, or `monitorsOwned` fields shown in the JDK 25 capture.

The later JDK 25 capture made the lock relationship visible:

| Diagnostic question | JDK 21 capture | JDK 25 capture |
| --- | --- | --- |
| Are virtual threads represented? | Yes | Yes |
| Can we read their stacks? | Yes | Yes |
| Is `state` recorded explicitly? | No | Yes |
| Is the contended object available as `blockedOn`? | No | Yes |
| Can we identify an owned monitor via `monitorsOwned`? | No | Yes |

These observations are **not a memory, CPU, or throughput benchmark**. The captures were taken at different instants, so the number of live threads also varied. What they demonstrate is a change in the diagnostic detail available in the JSON output.

The supplied captures were originally collected on a local Windows machine; the Bash and `curl` instructions above are provided for reproducing the same application-level experiment on Linux. Exact thread counts, stack frames, timings, and object identifiers are platform- and timing-dependent.

## Does `synchronized` still matter with virtual threads?

Absolutely, but not for the same reasons as before.

Before JDK 24, a virtual thread performing a blocking operation inside a `synchronized` block could pin its carrier thread, preventing that platform thread from executing other virtual threads.

**JDK 24 addressed this limitation through JEP 491 — Synchronize Virtual Threads without Pinning.** This improvement is also available in JDK 25, allowing virtual threads to unmount from their carriers during blocking operations inside synchronized regions.

However, removing pinning does not eliminate monitor contention.

In our experiment, one virtual thread holds the monitor while sleeping for five seconds. Other virtual threads remain `BLOCKED`, waiting to acquire that same monitor.

The important distinction is:

- **Pinning:** A virtual thread cannot release its carrier thread during a blocking operation.
- **Contention:** Multiple threads compete for the same lock, forcing some of them to wait.

JDK 25 solves the monitor-related pinning problem, but the synchronization semantics remain unchanged.

This is exactly what our JSON thread dump reveals: one virtual thread owns the monitor, while several others are waiting for it.

It is also worth noting that certain native and foreign-function operations can still cause carrier-thread pinning.

**The lesson is simple: removing pinning improves virtual-thread scalability, but it does not automatically make synchronized application code concurrent.**

## Practical limits and production safety

The sample is a controlled demonstration, **not a production-ready administrative API**. Because `/leak` intentionally retains 5 MiB of heap memory per call, invoke it only a small number of times in a disposable local test process.

The server binds only to localhost; keep the `/dump` and `/leak` handlers inaccessible to untrusted clients. A thread dump can reveal application class names, code paths, and other sensitive runtime details. Limit who can trigger it, where files are stored, how long they are retained, and how they are shared.

Also remember that `dumpThreads()` does not automatically produce a deadlock report. The JDK 25 release notes explicitly distinguish these dumps from traditional tooling such as `jstack` or `jcmd Thread.print` in that respect. Use other JVM diagnostics when you need explicit deadlock detection.

## Final thoughts

There is something satisfying about reproducing a concurrency problem and diagnosing it with nothing but the JDK.

Our tiny HTTP server made ten requests compete for one monitor. The JDK 25 JSON dump showed the owner, the waiting virtual threads, and the exact monitor linking them. A quiet dump reminded us that diagnostics are only as useful as the moment they capture. And the JDK 21 comparison made the improvement in structured observability concrete.

The takeaway is not that virtual threads eliminate blocking; they do not. It is that **modern Java is giving us better ways to explain what blocking actually looks like**.

### References

- [HotSpotDiagnosticMXBean — JDK 25 API documentation](https://docs.oracle.com/en/java/javase/25/docs/api/jdk.management/com/sun/management/HotSpotDiagnosticMXBean.html)
- [JDK 25 consolidated release notes — JDK-8356870](https://www.oracle.com/java/technologies/javase/25all-relnotes.html)
- [Virtual Threads — Java 25 documentation](https://docs.oracle.com/en/java/javase/25/core/virtual-threads.html)
- [JEP 491 — Synchronize Virtual Threads without Pinning](https://openjdk.org/jeps/491)
- [jdk.httpserver module — JDK 25](https://docs.oracle.com/en/java/javase/25/docs/api/jdk.httpserver/module-summary.html)
