---
title: "Your Loom App Quietly Became a Thread Pool Again: A Field Guide to Virtual Thread Pinning"
slug: "virtual-thread-pinning-field-guide"
date: "2026-07-16T07:43:16+00:00"
description: "The incident that taught me to respect pinning looked like nothing. A service freshly migrated to virtual threads, a load test that plateaued at about 420 - by Felipe Maschio Virtual thread pinning quietly turns your Loom app back into a bounded thread pool. The two causes, what JEP 491 changed in JDK 24, how to detect it with JFR, and how to fix it."
canonical: "https://dev.to/maschiojv/your-loom-app-quietly-became-a-thread-pool-again-a-field-guide-to-virtual-thread-pinning-2a3f"
authors:
  - "felipe-maschio"
image: "/images/posts/2026/07/virtual-thread-pinning-field-guide/foojay-featured-virtual-thread-pinning.jpg"
categories:
  - "Java"
  - "Java Core"
tags:
related_posts:
  - "banned-threadlocal-java-scoped-values"
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
  - "boxlang-1-14-0-sets-ranges-inner-classes-and-a-runtime-that-talks-back"
  - "quarkus-unpacked-insights-from-the-foojay-podcast"
enlighterjs: true
frozen: false
---

The incident that taught me to respect pinning looked like nothing. A service freshly migrated to virtual threads, a load test that plateaued at about 420 requests per second no matter how much traffic we threw at it, CPU sitting at 9%, zero errors, zero warnings, nothing in the logs. The machine had 8 cores, and the one downstream HTTP call in the hot path took about 19 ms. Do the arithmetic: 8 × (1000 / 19) ≈ 421.

The service that was supposed to scale to millions of virtual threads was serving exactly one request per CPU core. Loom had quietly handed us back a bounded thread pool, and the code looked perfectly innocent. That failure mode has a name --- **pinning** --- and this is the field guide I wish I'd had that night: what it is, the two (and only two) things that cause it, what JDK 24 changed, and how to catch it before your throughput graph does.

What pinning actually is {#h2-0-what-pinning-actually-is}
---------------------------------------------------------

A virtual thread doesn't own an OS thread. It runs on a small pool of platform threads called **carrier threads** --- concretely, the workers of a dedicated `ForkJoinPool` living in a thread group named `CarrierThreads`, with default parallelism equal to `Runtime.availableProcessors()`. When a virtual thread blocks --- on I/O, a lock, a queue --- it normally **unmounts**: it saves its stack, steps off the carrier, and frees that carrier to run another virtual thread. That unmount is the entire trick that lets a handful of OS threads serve millions of virtual ones.

**Pinning is when the unmount can't happen.** The virtual thread blocks but stays mounted, and its carrier sits there doing nothing useful for the whole duration. One pinned carrier is a rounding error. But the default carrier pool is only as big as your core count, so if a hot path pins routinely, you pin *every* carrier at once --- and then no virtual thread anywhere makes progress. That's not a slowdown; it's scheduler starvation, and from the outside it looks a lot like a deadlock. You can raise the ceiling with `-Djdk.virtualThreadScheduler.parallelism=N`, but that only delays the moment of exhaustion. It doesn't fix anything.

The two causes --- and it really is just two {#h2-1-the-two-causes-and-it-really-is-just-two}
---------------------------------------------------------------------------------------------

There are exactly two situations where the JVM cannot unmount a blocked virtual thread:

**1. Blocking inside `synchronized` (JDK 21 through 23).** Up to and including JDK 23, an object monitor is tied to the carrier thread that entered it. If a virtual thread blocks --- or calls `Object.wait()` --- while holding a monitor, the JVM can't move it off the carrier without breaking monitor ownership, so it pins. This is by far the most common cause in real code, because a blocking call buried inside a `synchronized` method is trivial to write and invisible at the call site. And the monitor doesn't have to be *yours* : `synchronized` inside a library, or inside the JDK itself, pins exactly the same way. `ConcurrentHashMap.computeIfAbsent` runs your mapping function under an internal bin lock --- put a blocking call inside it and you've pinned a carrier without a single `synchronized` keyword in your own code.

**2. Native frames.** When a virtual thread has a native method (JNI) or a foreign downcall (the Foreign Function \& Memory API) on its stack and it blocks, the JVM can't capture and restore the native frame, so it pins. This one has no `synchronized` to blame --- and it is *not* fixed by JDK 24. It also hides in a place nobody expects: class initialization runs through native frames, so a blocking call inside a static initializer pins even on the newest JDKs.

Just as important is what's **not** on the list: ordinary blocking I/O through the JDK (`Socket`, `InputStream`, `Files`), `BlockingQueue`, `ReentrantLock`, `CompletableFuture`, `Thread.sleep()` --- all of it was re-plumbed for Loom and unmounts cleanly. Pinning is a short, specific list, which is exactly why it's detectable.

The canonical bug {#h2-2-the-canonical-bug}
-------------------------------------------

Nearly every real pin I've read in a dump is some flavor of a cache or rate limiter guarding a slow call with `synchronized`:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class PriceService {
    private final Map&lt;String, BigDecimal&gt; cache = new HashMap&lt;&gt;();

    // Looks harmless. On JDK 21-23 it pins the carrier for the whole HTTP call.
    public synchronized BigDecimal lookup(String symbol) {
        return cache.computeIfAbsent(symbol,
            s -&gt; httpClient.quote(s));   // &lt;-- blocks while holding the monitor
    }
}</pre>

Every cache miss blocks on the network *while holding the monitor*. On JDK 21--23 that virtual thread pins its carrier for the entire round trip. Run a few hundred concurrent requests and you've pinned every carrier; the rest of the workload queues behind a monitor that never unmounts. That's my 420-requests-per-second incident in five lines.

What JDK 24 changed (JEP 491) {#h2-3-what-jdk-24-changed-jep-491}
-----------------------------------------------------------------

JDK 24 shipped [JEP 491, "Synchronize Virtual Threads without Pinning"](https://openjdk.org/jeps/491). It reworked monitor ownership so the monitor is associated with the virtual thread itself rather than its carrier --- which means a virtual thread *can* now unmount while blocked inside `synchronized`, while waiting to enter one, or while parked in `Object.wait()`. The most common cause of pinning simply goes away on JDK 24+, with no code change.

Two practical consequences:

* On JDK 24+, the only remaining pins come from native frames --- JNI, FFM downcalls, and class initialization.
* The old detection flag `-Djdk.tracePinnedThreads` was **removed** in JDK 24. Don't ship runbooks that depend on it.

If you're on JDK 21--23, though, `synchronized` pinning is very much alive, and upgrading is often the single cleanest fix you can make.

How to catch it {#h2-4-how-to-catch-it}
---------------------------------------

**On JDK 21--23 --- the legacy flag.** Run with:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">java -Djdk.tracePinnedThreads=full -jar app.jar</pre>

The JVM prints a stack trace every time a virtual thread pins, and the frame annotated `<== monitors:1` is the culprit. That one line is the whole diagnosis. Just remember this flag no longer exists on JDK 24.

**Everywhere --- JFR.** Since JDK 21 the JVM emits a `jdk.VirtualThreadPinned` Flight Recorder event when a virtual thread blocks while pinned. It's enabled by default --- but with a **20 ms threshold**, so short pins are invisible unless you lower it. In JDK 24 the event got better: it's emitted for every pinning occurrence and carries the reason and the carrier's identity. Since native-frame pins still fire it, this is the detection you should wire into production:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">java -XX:StartFlightRecording=filename=rec.jfr,settings=profile -jar app.jar
jfr print --events jdk.VirtualThreadPinned rec.jfr</pre>

**From a thread dump.** Plain `jstack` won't show you virtual threads at all. Use the virtual-thread-aware dump:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">jcmd &lt;pid&gt; Thread.dump_to_file -format=json dump.json</pre>

It lists the carrier threads and the virtual thread mounted on each. A carrier in the `CarrierThreads` group that is blocked while its mounted virtual thread sits in a `synchronized` frame (or a native frame) is the visual signature of a pin. Count how many carriers show it versus your pool size --- that ratio tells you how close you are to full starvation.

How to fix it {#h2-5-how-to-fix-it}
-----------------------------------

1. **Swap `synchronized` for `ReentrantLock`.** `java.util.concurrent.locks.ReentrantLock` is Loom-aware: a virtual thread that blocks on it, or while holding it, unmounts cleanly. This is the direct, version-independent fix.
2. **Upgrade to JDK 24+.** JEP 491 removes the `synchronized` pin entirely. Native-frame pins remain.
3. **Don't hold a lock across an external call.** Often the honest fix is structural: compute the value outside the critical section and only lock the map update.
4. **For native/FFM pins, isolate the path.** Run unavoidable blocking native calls on a dedicated platform-thread executor, or size the carrier pool so a few concurrent pins can't starve everything.

Here's the rewrite of the example. One trap to avoid: don't just move the blocking call into `ConcurrentHashMap.computeIfAbsent` --- as noted above, its mapping function runs under an internal bin lock, and on JDK 21--23 you'd have rebuilt the same pin one layer down.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class PriceService {
    private final Map&lt;String, BigDecimal&gt; cache = new ConcurrentHashMap&lt;&gt;();
    private final ReentrantLock lock = new ReentrantLock();

    public BigDecimal lookup(String symbol) {
        BigDecimal cached = cache.get(symbol);
        if (cached != null) return cached;
        lock.lock();                       // Loom-aware: unmounts if it blocks
        try {
            cached = cache.get(symbol);    // re-check under the lock
            if (cached != null) return cached;
            BigDecimal quote = httpClient.quote(symbol);  // blocks; carrier is freed
            cache.put(symbol, quote);
            return quote;
        } finally {
            lock.unlock();
        }
    }
}</pre>

This no longer pins anywhere --- though it still serializes cache misses behind one lock, which is fix #3's territory: the *next* refinement is not holding any lock across the network call at all.

Why I ended up automating the read {#h2-6-why-i-ended-up-automating-the-read}
-----------------------------------------------------------------------------

Doing this analysis by hand --- turn on a flag, reproduce, dump, find the carriers, match frames --- is fine once. It's tedious by the tenth incident, and worse, half the tooling depends on remembering to enable something *before* the problem happens. So I built a tool that does the read on any thread dump you give it: it finds the carriers, checks what's mounted on each, flags the pinned ones with the offending frame, and reports pinned-carriers-versus-pool-size --- the number that tells you whether you're one bad path away from starvation. It's [ThreadMine](https://threadmine.dev/en/analyze); the web analyzer is free and takes a dump with no signup. Full disclosure: it's my project --- I got tired of reading dumps by hand, so I automated the part I kept repeating. And a fair caveat: a dump is a snapshot, so for intermittent pinning, JFR is still the better signal.

If you want the deeper reference --- carriers, JEP 491, the full detection matrix --- I keep it updated here: [virtual thread pinning](https://threadmine.dev/en/resources/virtual-thread-pinning).

Pinning is the one Loom failure mode that cancels your scalability story without a single error in the logs. The rules are short: only `synchronized` (pre-JDK 24) and native frames pin; detect with `jdk.tracePinnedThreads` on 21--23 and the `jdk.VirtualThreadPinned` JFR event everywhere; fix with `ReentrantLock`, an upgrade, or by not holding locks across slow calls. Know the shape, and it stops being invisible.

*Felipe Maschio is the founder of [ThreadMine](https://threadmine.dev/en), a free JVM thread dump analyzer that detects deadlocks, thread leaks, pool exhaustion, CPU spikes and virtual thread pinning.*
