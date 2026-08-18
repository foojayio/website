---
title: "I Asked GitHub Copilot to Profile a Java App. It Found a Bug in My Heap Sizing, and Offered to Fix It"
date: "2026-07-14T14:42:11+00:00"
lastmod: "2026-07-15T07:41:12+00:00"
description: "I built an extension to collapse the entire loop of running and measuring the performance of Java workloads, so it can be used within a place some - by Bruno Borges"
authors:
  - "bruno-borges"
image: "github-copilot-profiling.png"
categories:
  - "AI"
related_posts:
frozen: false
---

I built an extension to collapse the entire loop of running and measuring the performance of Java workloads, so it can be used within a place some developers are starting to consider their new "development environment" in the agentic AI era: the [**GitHub Copilot app**](https://github.com/features/ai/github-app).

## The idea: Copilot-driven profiling

This extension is called JVM Pulse, and this is a walkthrough of one real run. JVM Pulse is a canvas extension for the GitHub Copilot app. It doesn't hard-code how to build or run Java projects and it doesn't need the user to be specific either. You click Run analysis, and Copilot does the project-specific work: it detects your build tool and JDK, writes or compiles a representative workload, and launches it with the right flags: unified GC logging and a JFR recording with settings=profile.

Then the artifacts flow through Microsoft's [GCToolkit](https://github.com/microsoft/gctoolkit) and the JDK's jfr CLI, and everything lands in an interactive dashboard: throughput, pause percentiles, heap occupancy, allocation pressure, hot methods, lock contention, safepoints, I/O, etc.

For this walkthrough I pointed it at a [**JairoSVG**](https://github.com/brunoborges/jairosvg) rendering benchmark: 43 sample SVGs, a parse + render + PNG-encode loop, \~25 seconds, heap pinned at 256M on G1GC (JDK 25).
![Article content](https://media.licdn.com/dms/image/v2/D5612AQGi5Fh5WeV08g/article-inline_image-shrink_1000_1488/B56Z9dQtpWHUAI-/0/1783976090853?e=1785369600&v=beta&t=xDWdvclaLdC_bk2VdzqoMtnlRjYs9668wrapx_XOqBU) Run analysis - Copilot will run and profile the app

## Step 1 --- Copilot ran it, and showed me exactly what it ran

The thing I care most about in an AI tool is that it's transparent about what it did. JVM Pulse records the **exact launch command**, including every JVM flag. And after 5,633 renders completed, GC log and JFR recording were captured, and the dashboard rendered right next to the chat.
![Article content](https://media.licdn.com/dms/image/v2/D5612AQFaUQAEsRBkCg/article-inline_image-shrink_400_744/B56Z9dS_SkGsAU-/0/1783976687217?e=1785369600&v=beta&t=qnTaX59MLc4y-ILcWDKOtl-xPvIbv9H9p2zTH_qKjpM)

### Step 2 --- The verdict in plain language

The dashboard opens with a health assessment instead of a number dump:

* **99.72% throughput** --- GC is *not* the bottleneck
* **p99 pause 7.8 ms** --- pauses are a non-issue
* **156 MB/s allocation, \~4.3 GB churned** --- flagged as *Elevated*, "the main lever for further gains"
* **Peak heap 243 MB of 256 MB** --- basically full

![Article content](https://media.licdn.com/dms/image/v2/D5612AQHx9VzFEG96bw/article-inline_image-shrink_1000_1488/B56Z9dTq1vG0AI-/0/1783976865417?e=1785369600&v=beta&t=JWDwANQf6lIizlgv9G3RnxUwmHgznN4dFgGk7rxle5I) The dashboard populated.

A traditional profiler shows you the p99 and lets you draw your own conclusions. This one tells you where to look: *pauses are fine, allocation is your lever.*

### Step 3 --- One click hands the data back to Copilot

A single **Analyze with AI** button ships the full gc-jfr-report.json and the raw jfr-all-views.txt back into the Copilot session with a ready-made prompt:
![Article content](https://media.licdn.com/dms/image/v2/D5612AQEarUENcahGrw/article-inline_image-shrink_1500_2232/B56Z9dUAMVIoAQ-/0/1783976952877?e=1785369600&v=beta&t=FQZDTRP8Kl2Ytvs2W9NIzH5oxKb91tgy7DlC21GDICg) "Analyze with AI" button triggers a prompt with the profiling data.

### Step 4 --- A root cause, not a summary

This is where it stopped feeling like a toy. Copilot read the allocation-by-site view and came back with:
> **Healthy on the surface, one structural problem underneath.**

The structural problem: **humongous allocations against a near-full heap** . With a 256M heap, G1 picks a 1M region size, so the humongous threshold is 512 KB. The int\[\] rasters backing the BufferedImages exceed that, so **\~43% of the heap was humongous regions**, and 23.4% of GCs were G1_HUMONGOUS_ALLOCATION-triggered. Every large raster was forcing a collection.
![Article content](https://media.licdn.com/dms/image/v2/D5612AQHJnODx9wvxfQ/article-inline_image-shrink_1000_1488/B56Z9dUXdLGcAM-/0/1783977048247?e=1785369600&v=beta&t=yt5VOa1-PKb5wZGTbCw7_8vpVRDeQPxIcPKsmYflxCE) GC assessment

### Step 5 --- Recommendations with reasoning

The tuning advice came as a table where every flag is tied to the metric it improves:
![Article content](https://media.licdn.com/dms/image/v2/D5612AQGvAfYPzGRDUA/article-inline_image-shrink_1500_2232/B56Z9dUdlaG4AQ-/0/1783977073310?e=1785369600&v=beta&t=TjdoA7KZasxiLpuN9zTTcsnE3JPgeUTSCoL7pvC-tPQ) JVM configuration recommendations

And crucially, it separated **config fixes** from the **durable code fix** : BufferedImage.\<init\> alone was \~35% of all allocation. A fresh raster allocated *per render*. The real win is a pooled/reused scratch image, which would gut the int\[\] 43.5% churn regardless of heap size.
![Article content](https://media.licdn.com/dms/image/v2/D5612AQGIegT82K8bGQ/article-inline_image-shrink_1000_1488/B56Z9dUm0WLAAI-/0/1783977111141?e=1785369600&v=beta&t=MS5LZbV_D80mojh7-xWC_HD02Wdb5dTW4soj4MxUai4) Allocation and code hotspot analysis

### Step 6 --- It offered to run the experiment

Instead of leaving me with a to-do list, Copilot proposed the next step:
> "Rerun the identical 25s workload with -Xmx1g -XX:G1HeapRegionSize=4m... Want me to run experiment #1 now, or implement the buffer-reuse change first?"

Analysis → hypothesis → experiment, all in one place.
![Article content](https://media.licdn.com/dms/image/v2/D5612AQEMVDQ0o1yl5A/article-inline_image-shrink_1500_2232/B56Z9dUycHHIAg-/0/1783977158867?e=1785369600&v=beta&t=uOsIwag965Lns2EU11goVOcYUFWQUm1Ds3Lfzbijydo) *Conclusion and follow up experiment*

### Step 7 --- Prove it worked

Every run is saved. Flip on **Compare** , pick the previous run as a baseline, and JVM Pulse diffs the two side by side. Throughput, pauses, allocation, heap, GC events, and even a flag-level diff of the two launch commands. So you don't just *believe* the fix worked; you *see* it.

That's the full scientific loop: hypothesis, run, measure, compare --- inside the tool you're already coding in.

### Why this matters

The individual pieces here aren't new. GCToolkit parses GC logs. JFR has always had the data. What's new is **removing the translation tax** between "here is telemetry" and "here is what to change", and keeping the entire loop in one surface, driven by an agent that can both read the data and act on it.

JVM Pulse is open source (MIT). If you write Java and you've ever bounced between a benchmark, a log parser, and a flag reference, I'd love for you to try it.

👉 <https://github.com/brunoborges/jvm-pulse>

*Built with Microsoft GCToolkit, the JDK* jfr*CLI, and the GitHub Copilot canvas extension API leveraging the GitHub Copilot SDK for AI integration.* GitHub Copilot App can be used without a GitHub Copilot subscription, along with BYOK models.
