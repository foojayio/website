---
title: "How DataStax Tracked Down a Linux Kernel Bug with Fallout"
slug: "how-datastax-tracked-down-a-linux-kernel-bug-with-fallout"
date: "2022-06-02T15:32:08+00:00"
lastmod: "2022-06-02T15:32:10+00:00"
description: "Sometimes as a developer, you run into a bug buried deep within the layers of your software stack. Chasing down the root cause requires not only - by Matt Fleming"
canonical: "https://medium.com/building-the-open-data-stack/how-datastax-tracked-down-a-linux-kernel-bug-with-fallout-3ca10ec1fde4"
authors:
  - "matt-fleming"
image: "/images/posts/2022/06/how-datastax-tracked-down-a-linux-kernel-bug-with-fallout/1_N_gqb3PrkKgMbJHSmYeOaw.jpeg"
categories:
  - "Apache Cassandra"
  - "Databases"
  - "Performance"
tags:
related_posts:
  - "aggregation-optimization-in-mongodb-unnecessary-unwinds-part-2"
  - "minimize-costs-by-utilizing-cloud-storage-with-spring-data-eclipse-store"
  - "reclaiming-persistent-volumes-in-kubernetes"
  - "k8ssandra-ramps-up-security-features-to-match-kubernetes-best-practices"
enlighterjs: true
frozen: false
---

![](/images/posts/2022/06/how-datastax-tracked-down-a-linux-kernel-bug-with-fallout/1_N_gqb3PrkKgMbJHSmYeOaw-1024x750.jpeg)

*Sometimes as a developer, you run into a bug buried deep within the layers of your software stack. Chasing down the root cause requires not only curiosity, patience, and a healthy dose of tenacity but a willingness to try different tools and approaches. This post describes our challenges and ultimate success in tracking down a Linux kernel bug using Fallout.*{#ee66}

Bugs come in all shapes and sizes and it's not always clear at the beginning of a debugging session which one you're currently chasing. Some bugs can be fixed up in a matter of minutes while others take weeks to nail down. And the really tricky ones require you to dig through multiple layers of your software stack, stressing the limits of your patience.{#8aae}

This article covers an example of the latter kind of bug. When one of the daily performance tests started timing out after 16 hours, it turned out that I had unknowingly stumbled onto an issue in the Linux kernel `hrtimer` code. Since I didn't know it was a kernel bug when I started looking, I had to methodically dig deeper into the software stack as I homed in on the problem.{#51b4}

[Fallout](https://github.com/datastax/fallout) is an open-source distributed systems testing service that we use heavily at [DataStax](https://www.datastax.com/) to run functional and performance tests for [Apache Cassandra](https://cassandra.apache.org/_/index.html), [Apache Pulsar](https://pulsar.apache.org/), and other projects. Fallout automatically provisions and configures distributed systems and clients, runs a variety of workloads and benchmarks, and gathers test results for later analysis.{#3cf2}

As you'll see below, Fallout was instrumental in being able to quickly iterate and gather new data to validate and invalidate my guesses about the underlying bug.{#dff4}

At [DataStax](https://www.datastax.com/?) we run a variety of nightly functional and performance tests against [Cassandra](https://cassandra.apache.org/_/index.html), and one morning we noticed that one of our write-throughput benchmarks failed to complete after 16 hours. Normally, this test would complete within four, so something was clearly wrong. Like all good engineers, we checked to see what had changed since the test last passed, and sure enough we found a commit in our git repository that could potentially explain the failure.{#1f81}

Since the most important thing after discovering a test failure is to get the tests passing again, we reverted the suspected commit and re-ran the tests. They passed. So with the tests now passing we figured that even if we hadn't analysed the commit to understand *how* the test could be failing, we'd at least narrowed down the cause of the issue to a single commit. High-fives were exchanged and we all went back to work leaving the author of the reverted commit to understand the root cause of the failure.{#b31b}

Only the test failed again the next day, even with the reverted commit. Clearly we'd been too hasty in our diagnosis and hadn't actually fixed anything.{#954f}

But this begged the question: if the commit we reverted didn't originally cause the test to fail, why had it suddenly started failing? It dawned on us that we were dealing with an intermittent failure and our task now was to figure out how to reproduce it more reliably.{#b6fd}

Intermittent failures are a pain to debug because they take so much longer to analyze when you can't trigger the conditions for the bug on every test run. Sometimes there's no telling how many runs you'll need to hit it. Worse, you have to gather much more data to be confident that you've actually solved the bug once you apply your fix.{#b295}

Almost all of the performance tests at [DataStax](https://www.datastax.com/) run using [Fallout](https://github.com/datastax/fallout), our distributed systems testing service. Tests are described in a single YAML file and Fallout takes care of scheduling tests on available hardware and collecting the results for later analysis. Our team figured out we could normally hit this bug at least once every five runs. We initially looked at trying to increase the reproducibility of this bug but simply couldn't make the failure happen more regularly. Fortunately, there was one way we could reduce the time to trigger it --- by running multiple tests in parallel. Since Fallout can automatically queue test runs and execute them once hardware becomes available, I fired up five runs of the same test and waited for the results to come in. Instead of having to wait up to 20 hours to see if I had hit the bug, I now only needed to wait around four hours. Still not ideal, but it meant that I could make progress every day.{#dcad}

But we were still using the "does the test run for longer than four hours" symptom to detect when we'd run into a problem. Finding a less time-consuming way to know when we'd triggered the bug required understanding the side effects of the bug at the Cassandra level. In other words, we had to know what Cassandra was doing to cause the test to timeout in the first place.{#378a}

[Cassandra](https://cassandra.apache.org/_/index.html) has a number of tools to understand what's happening internally as it serves data to clients. A lot of this is tracked as metrics in monitoring tools such as [Prometheus](https://prometheus.io/) or [Grafana](https://grafana.com/) which integrate with Fallout. Checking those metrics showed that request throughput (requests per second) dropped off to near zero whenever we triggered the bug. To understand what was happening on the server side, I waited for the bug to occur and then took a look at the output of [nodetool](https://cassandra.apache.org/doc/latest/cassandra/tools/nodetool/nodetool.html) tpstats.{#4b35}

Using [nodetool tpstats](http://tpstats/) for diagnosing bugs is much easier if you have a mental model of how requests are handled by [Cassandra](https://cassandra.apache.org/_/index.html). Native-Transport-Requests threads are responsible for handling requests from clients and they are run by the SEPWorker mechanism which is the thread pool framework inside of Cassandra. Cassandra uses a pool of threads and wakes up threads when all the running threads are busy and there's still work to be done. When there's no work to do, threads are put to sleep again until more work comes in.{#2c43}

From the output of tpstats I could see that there was plenty of work to do because the number of pending requests was high but the number of *active* requests at any one time hovered around two or three. To put this into perspective, there were hundreds of requests and around 172 Native-Transport-Requests threads on the machine but most of those threads were sleeping instead of handling the incoming requests from clients. Something was preventing Cassandra from waking up more Native-Transport-Requests threads.{#b3a9}

The SEPWorker infrastructure has a mechanism for only waking up the minimum number of threads necessary when work comes in. If the currently running threads finish processing all of their work they enter the SPINNING state which is part of the internal Cassandra state machine. In this state, threads will check for available work and then sleep for a short period of time if there isn't any. When they wake up they do one more check for work and if there *still* isn't any work they go to sleep until explicitly awakened by another thread. If there *is* work, then before processing it they check to see whether they took the last work item and wake up another thread to process the next one if there's still outstanding work.{#723d}

Knowing all of this and combining that knowledge with the tpstats output I decided to see what all of the Native-Transport-Requests threads were doing by looking at their callstacks with jstack. Most of the threads were sleeping just like I expected based on the tpstats output, and a few were busily executing work. But one had this interesting callstack:{#8880}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">"Native-Transport-Requests-2" #173 daemon prio=5 os_prio=0 cpu=462214.94ms elapsed=19374.32s tid=0x00007efee606eb00 nid=0x385d waiting on condition  [0x00007efec18b9000]
4   java.lang.Thread.State: TIMED_WAITING (parking)
5 at jdk.internal.misc.Unsafe.park(<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="8ae0ebfceba4e8ebf9efcabbbba4baa4bc">[email&nbsp;protected]</a>/Native Method)
6 at java.util.concurrent.locks.LockSupport.parkNanos(<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="9bf1faedfab5f9fae8fedbaaaab5abb5ad">[email&nbsp;protected]</a>/LockSupport.java:357)
7 at org.apache.cassandra.concurrent.SEPWorker.doWaitSpin(SEPWorker.java:268)</pre>

This showed that `Native-Transport-Requests-2 `was currently in the SPINNING state and sleeping prior to checking for more work one last time before sleeping permanently. The only problem was, no matter how many times I ran jstack, this thread never*exited* `parkNanos()` which meant no other threads would ever be woken up to help process the backlog of work!{#e4a9}

This finally explained why throughput dropped whenever we hit the bug. The next step was understanding how `parkNanos()` was behaving.{#686d}

First of all, I wanted to make sure that Cassandra wasn't somehow passing a really big value to `parkNanos()` which could make it appear that it was blocked indefinitely when in actual fact it was just sleeping for a very long time. Now that I had more of an idea of what to look for, it was easy to verify the values passed to `parkNanos()` by printing a log message every time this code path was executed. All of the values looked within the expected range so I concluded that we definitely weren't calling `parkNanos()` incorrectly.{#8f75}

Next, I turned my attention to the implementation of `parkNanos()`. `parkNanos()` uses the timeout feature of `pthread`'s conditional variables to sleep for a specified time. Internally, `pthread `uses the kernel to provide the timeout facility, specifically by using the `futex()` system call. As a quick recap, the `futex()` (or fast userspace `mutex`) system call allows application threads to sleep, waiting for the value at a memory address to change. The thread is woken up when either the value changes or the timeout expires.{#ffa7}

The `parkNanos()` call was putting the Native-Transport-Requests thread to sleep expecting the timer to expire within 10 microseconds or so but it was starting to look like that never happened. Two questions came to mind: did the thread somehow miss the signal to wake up from the expiring timer or did the timer never actually expire? I couldn't see anything obviously wrong with the code, and I didn't want to deal with building a custom version of the Java SE Development Kit (JDK) to pursue that line further. Instead, I decided to switch gears and [write a BPF script](https://gist.github.com/mfleming/7c6cfd225c305c0e44cc7357cdc481f3) to explore the problem from the kernel side first{#a2f9}

[BPF](https://lwn.net/Articles/740157/) is an in-kernel virtual machine that's capable of dynamically running kernel code injected from userspace. The beauty of BPF is that you can run these scripts without needing to recompile your kernel or even reboot your machine. Among other things, BPF allows you to insert probes or hooks at the entry and exit of kernel functions, effectively allowing you to run your own kernel code. Tracing through the kernel's `futex` code I could see that if I hooked my script into the return path of the internal functions, I could display the timer details when the thread returned from the `futex` call. The next trick was getting the stuck thread to return.{#df0c}

Signals are a handy way of interrupting sleeping application threads that are inside the kernel and you can often send a `SIGSTOP`, `SIGCONT` sequence without triggering any error paths in the kernel or your application. So by sending these two signals I could force the sleeping Native-Transport-Requests thread to return from the blocking `futex()` call, run my BPF script and print the timer details in my terminal window.{#4ca7}

The main timer detail I was interested in was the expired value which tells you when the timer should have expired and awakened the sleeping thread. If the value was far into the future it seemed likely that the timer had been programmed incorrectly, but if it was in the past then we'd missed the wakeup or it had never been sent. I bundled my BPF script into a bash script that also took care of first sending the `SIGSTOP` and `SIGCONT` signals to the Native-Transport-Requests thread.{#25f2}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ sudo ./futex 14469
Tracing pid 14469…
Sending SIGSTOP
55880730777795 expires</pre>

The expires value for high-resolution timers in the kernel is in nanoseconds since boot. Doing a quick bit of math (5880730777795/1000000000 / 60 = 98.012 or 98 minutes of uptime) I could tell that the expire value looked correct because it was within the four hours that the test usually took. And the real issue was that a wakeup had never been sent to the sleeping `Native-Transport-Requests` thread. At this point, I was convinced that our test was hitting a kernel bug.{#5906}

[High-resolution timers](https://lwn.net/Articles/167897/), or [hrtimers](https://www.kernel.org/doc/Documentation/timers/highres.txt), is the in-kernel infrastructure responsible for programming the timer hardware in your CPU. At the application level, `hrtimers` are programming using system calls such as `clock_nanosleep()` and `nanosleep()`. Each CPU maintains its own tree of `hrtimers` grouped by `clock ID` (`CLOCK_MONOTONIC`, `CLOCK_REALTIME`, etc). Each tree is implemented using a red-black tree data structure. Timers are inserted into the red-black tree and sorted based on their expiration time. The kernel's red-black tree implementation also maintains a pointer to the smallest entry in the tree which enables the kernel to lookup the next expiring timer in O(1).{#548c}

I modified my BPF script to display the location in the red-black tree for the `hrtimer `our `Native-Transport-Requests` thread was waiting on and discovered that it never reached the leftmost spot. This explained why the thread was never awakened! This is also a violation of the way that red-black trees are supposed to work: it's normally expected that the entries in the tree are in sorted order, smallest to highest, at all times.{#34c3}

Tracing the workings of these red-black trees was beyond anything I could achieve with a BPF script. I resorted to writing a kernel patch to print a warning message and dump the contents of the `hrtimer` red-black trees whenever the auxiliary pointer to the leftmost entry no longer pointed to the timer that expired next.{#32c4}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">10: was adding node=00000000649f0970
10: found node in wrong place last=0000000091a72b7e, next=00000000649f0970 (expires 208222423748, 208222332591)
10: last timer=0000000091a72b7e function=hrtimer_wakeup+0x0/0x30, next=00000000649f0970 timer function=hrtick+0x0/0x70
Printing rbtree
=========
node=0000000091a72b7e, expires=208222423748, function=hrtimer_wakeup+0x0/0x30
node=00000000649f0970, expires=208222332591, function=hrtick+0x0/0x70
node=00000000063113c0, expires=208225793771, function=tick_sched_timer+0x0/0x80
node=000000003705886f, expires=209277793771, function=watchdog_timer_fn+0x0/0x280
node=00000000e3f371a2, expires=233222750000, function=timerfd_tmrproc+0x0/0x20
node=0000000068442340, expires=265218903415, function=hrtimer_wakeup+0x0/0x30
node=00000000785c2d62, expires=291972750000, function=timerfd_tmrproc+0x0/0x20
node=0000000085e65b06, expires=86608220562558, function=hrtimer_wakeup+0x0/0x30
node=00000000049a0b4d, expires=100000159000051377, function=hrtimer_wakeup+0x0/0x30</pre>

Before I started this part, I wasn't even sure that Fallout would allow me to boot into a custom kernel. Sure enough, it was trivial to do.{#ac36}

After rebooting into my custom kernel I saw the warnings triggering immediately on startup so I naturally assumed that something was broken with my patch. But after a closer look, I realized that it was possible to trigger my bug within two minutes of booting the VM! I no longer had to wait up to four hours to see if I had reproduced the issue. What was even better was that I was able to hit this warning every single time I booted the VM. With the reproduction time cut by around 99%, I quickly made progress understanding the kernel bug that caused the red-black tree to become inconsistent.{#0fdc}

The kernel bug underlying this whole ordeal was already [fixed upstream](https://git.kernel.org/pub/scm/linux/kernel/git/torvalds/linux.git/commit/?id=156ec6f42b8d300dbbf382738ff35c8bad8f4c3a) in February this year in Linux 5.12 but not yet pulled into Ubuntu's kernel. What was happening was that some of the kernel's scheduler code was modifying the expiration time of a `hrtimer` it had previously inserted into the red-black tree without first removing it from the tree. This violated the red-black tree property that all entries in the tree need to be sorted by their expiration time and resulted in `hrtimers` not firing. This meant that threads waiting on those timers were never woken up.{#03c9}

But why had no one else hit this kernel bug? The reason is because of a little-known or documented scheduler tunable that we were using. We were using the kernel's `HRTICK` feature in order to improve scheduler latency. Unfortunately, it's not widely used and a number of bugs have been fixed over the years. What's worse is that `HRTICK` fixes are not being backported to stable kernels. So Linux distributions need to take them in an ad-hoc fashion. Rather than waiting for this to happen or rolling our own kernels, we decided to disable the feature altogether to avoid any future multi-week kernel debugging.{#e009}

It's helpful when debugging these multi-layered issues to have a bag of tools and techniques you can use to understand the behavior of your app at various levels of the stack. Even if you don't know how to instrument the code at a particular point (or don't want to), remember how I didn't want to build a custom version of the JDK? You can still infer the location of a bug by using tools to understand the behavior above and below a layer. And when you don't know exactly how a piece of code works, having a mental model or a rough idea of what goes on inside can help you to make forward progress by skipping layers that are unlikely to contain the bug.{#a2f2}

Secondly, there is no way I could have taken on this problem without a service to automatically deploy and provision virtual machines for running the test. [Fallout](https://github.com/datastax/fallout) was the key to unlocking this bug because even though the bug could only be reproduced intermittently, I managed to parallelize the test runs and reduce the time to trigger the issue. The whole investigation took several weeks, and that's despite my being able to analyze test results to make a little bit of progress every day.{#4d3f}

If any of this sounds like the kind of problem you'd enjoy working on, check out the open roles on the [DataStax careers](https://www.datastax.com/company/careers) page. We're always on the lookout for tenacious developers and software engineers!{#7fb0}

<br />

<figure class="wp-block-embed">
 <div class="wp-block-embed__wrapper">
  https://gist.github.com/mfleming/7c6cfd225c305c0e44cc7357cdc481f3
 </div>
</figure>

Curious to learn more about (or play with) Cassandra itself? We recommend trying it on the [Astra DB](https://astra.dev/3x2nSJ4) for the fastest setup.

1. [DataStax](https://www.datastax.com/)
2. [Apache Cassandra](https://cassandra.apache.org/_/index.html)
3. [Apache Pulsar](https://pulsar.apache.org/)
4. [Fallout](https://github.com/datastax/fallout)
5. [Prometheus](https://prometheus.io/)
6. [Grafana](https://grafana.com/)
7. [Nodetool](https://cassandra.apache.org/doc/latest/cassandra/tools/nodetool/nodetool.html)
8. [Nodetool tpstats](http://tpstats/)
9. [BPF script](https://gist.github.com/mfleming/7c6cfd225c305c0e44cc7357cdc481f3)
10. [A Thorough Introduction to eBPF](https://lwn.net/Articles/740157/)
11. [High-resolution Timers and Dynamic Ticks Design Notes](https://lwn.net/Articles/167897/)
12. Linux Kernel: [kernel/git/torvalds/linux.git](https://git.kernel.org/pub/scm/linux/kernel/git/torvalds/linux.git/commit/?id=156ec6f42b8d300dbbf382738ff35c8bad8f4c3a)

*Follow the* [*DataStax Tech Blog*](https://datastax.medium.com/)*for more developer stories. Check out our* [*YouTube*](https://www.youtube.com/channel/UCqA6zOSMpQ55vvguq4Y0jAg)*channel for tutorials and here for DataStax Developers on* [*Twitter*](https://twitter.com/DataStaxDevs)*for the latest news about our developer community.*
