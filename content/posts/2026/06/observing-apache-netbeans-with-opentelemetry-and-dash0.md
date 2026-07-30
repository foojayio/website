---
title: "Observing Apache NetBeans with OpenTelemetry and Dash0"
slug: "observing-apache-netbeans-with-opentelemetry-and-dash0"
date: "2026-06-14T22:03:09+00:00"
lastmod: "2026-06-14T22:30:57+00:00"
description: "What happens when you point an observability agent at your IDE rather than your application?"
authors:
  - "geertjan-wielenga"
image: "https://foojay.io/wp-content/uploads/2022/08/opentelemetry-horizontal-color.png"
categories:
  - "Developer Tools"
  - "NetBeans"
  - "OpenTelemetry"
tags:
related_posts:
enlighterjs: true
frozen: false
---

*What happens when you point an observability agent at your IDE rather than your application?*

The question nobody thinks to ask {#h2-0-the-question-nobody-thinks-to-ask}
---------------------------------------------------------------------------

Observability is something we do to our applications. We instrument services, add trace context to HTTP requests, collect JVM heap metrics, ship logs to a backend. The IDE we use to write that instrumentation is --- by convention --- invisible. It just runs. When it is slow, we assume it is busy. When it crashes, we restart it. We have no data.

But Apache NetBeans is a Java application. It runs on the JVM. It makes HTTP requests, loads hundreds of modules, allocates heap, spawns dozens of threads, and emits log records throughout its lifetime. There is nothing in the OpenTelemetry specification that says observability only applies to production services. So what would happen if we attached the OTel Java agent to NetBeans and pointed it at Dash0?

I tried it. This is what I found.

Why this is more than a curiosity {#h2-1-why-this-is-more-than-a-curiosity}
---------------------------------------------------------------------------

Before getting into the mechanics, it is worth naming the three reasons this matters beyond the novelty.

* **IDE performance is invisible and therefore unmanaged.** Every developer has experienced an IDE that becomes sluggish after a certain project size, after installing certain plugins, or after a certain number of open files. The usual response is to increase `-Xmx` or uninstall plugins at random. With OTel metrics you can see exactly which operations are consuming heap, how often GC fires, and whether the thread pool is saturating --- the same tools you would use to diagnose a slow microservice.
* **Startup time is a tax paid every day.** If your IDE takes 40 seconds to start, and you restart it three times per day, that is two minutes of waiting per developer per day. Across a team of 20, that is 40 developer-minutes daily, or roughly 170 hours per year. With startup traces you can see exactly which module takes the longest to initialise and where the time goes.
* **The OTel agent is zero-code instrumentation.** I did not write a single line of NetBeans plugin code to get this working. One JAR, three environment variables, one config file change. That is the promise of the OpenTelemetry Java agent --- it instruments any JVM process at the bytecode level without requiring source access or recompilation. NetBeans is not special. The same approach works on any Java application you have ever shipped.

The setup {#h2-2-the-setup}
---------------------------

### What I started with {#h3-3-what-i-started-with}

* Apache NetBeans 30 (the current release at time of writing)
* OpenTelemetry Java agent v2.28.1
* Dash0 as the observability backend, accepting OTLP over gRPC

### Step 1: Download the agent {#h3-4-step-1-download-the-agent}

<pre class="EnlighterJSRAW" data-enlighter-language="generic">wget https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar</pre>

### Step 2: Edit `netbeans.conf` {#h3-5-step-2-edit-netbeans-conf}

NetBeans reads its JVM arguments from `etc/netbeans.conf` inside the installation directory. Every option prefixed with `-J` is passed directly to the JVM. I added the following block:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">netbeans_default_options="${netbeans_default_options} \
    -J-javaagent:/path/to/opentelemetry-javaagent.jar \
    -J-Dotel.service.name=netbeans-ide \
    -J-Dotel.service.version=30 \
    -J-Dotel.exporter.otlp.endpoint=https://ingress.europe-west4.gcp.dash0.com:4317 \
    -J-Dotel.exporter.otlp.headers=Authorization=auth_&lt;token&gt;,Dash0-Dataset=sample \
    -J-Dotel.exporter.otlp.protocol=grpc \
    -J-Dotel.traces.sampler=always_on \
    -J-Dotel.logs.exporter=otlp \
    -J-Dotel.metrics.exporter=otlp \
    -J-Dotel.instrumentation.okhttp.enabled=false \
    -J-Dotel.instrumentation.java-http-client.enabled=false"</pre>

The two disabled instrumentations (`okhttp`, `java-http-client`) prevent a recursive loop: the OTel agent uses OkHttp internally to export telemetry, and without this exclusion it would attempt to instrument its own export calls, creating a cycle.

### Step 3: Start NetBeans {#h3-6-step-3-start-netbeans}

<pre class="EnlighterJSRAW" data-enlighter-language="java">netbeans --jdkhome /path/to/jdk21<code class="language-sh"></code></pre>

The agent announces itself on startup:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">[otel.javaagent] INFO opentelemetry-javaagent - version: 2.28.1</pre>

From that point on, the IDE runs normally. The agent operates invisibly, collecting data in the background.

What arrives in Dash0 {#h2-7-what-arrives-in-dash0}
---------------------------------------------------

### Logs: the IDE's internal monologue {#h3-8-logs-the-ide-s-internal-monologue}

Within the first 90 seconds of startup, 42 log records arrived in Dash0 from `service.name=netbeans-ide`. These were not log records I wrote --- they were the IDE's own `java.util.logging` output, bridged automatically to OTel by the agent.

**The session header** arrived as a structured log record containing the full system snapshot at startup time: product version (`Apache NetBeans 30`), JDK version, installation path, memory configuration, and host details. In a team environment, this alone tells you which developer is running which version on which machine.

**Module loading** : every OSGi/NBM bundle that NetBeans loads emits a log record when it resolves. I saw `org.eclipse.jgit`, `bcpkix`, `bcpg` (Bouncy Castle for GPG signing), `slf4j`, dozens of `net.java.html.*` modules, and hundreds more --- each with its bundle version and state.

**Deprecation warnings** : several modules were flagged. These warnings exist in every NetBeans startup but are normally invisible unless you dig into the log file buried in the userdir. In Dash0 they appear as `WARN` severity records, searchable and alertable.

**Network discovery** : the proxy detector logged `no http_proxy variable found → direct mode`. In a corporate environment where proxies are misconfigured, this log record would be the first thing to check when the update centre stops working.

**Shutdown** : `Runtime.exit() called with status: 143` --- SIGTERM received, clean shutdown. The exit code is captured as part of the final log record, telling you whether the IDE exited cleanly or was killed hard.

### Spans: every outbound HTTP call {#h3-9-spans-every-outbound-http-call}

Within 20 seconds of startup, the update centre and plugin manager fired four outbound HTTP requests:

|          Destination          |              Path               | Status | Duration |
|-------------------------------|---------------------------------|--------|----------|
| `netbeans.apache.org`         | `/nb/updates/30/updates.xml`    | 302    | 124 ms   |
| `netbeans.apache.org`         | `/nb/plugins/30/catalog.xml.gz` | 302    | 100 ms   |
| `plugins.netbeans.apache.org` | `/data/30/catalog.xml.gz`       | 200    | 446 ms   |
| `netbeans.apache.org`         | `/updates/current.xml`          | 200    | 46 ms    |

These are captured as full OTel spans with `url.full`, `http.response.status_code`, `server.address`, and duration in seconds. In a restricted network environment --- a corporate proxy, a firewall, a VPN --- these spans tell you immediately whether the IDE can reach the update servers and how long it is waiting.

The two 302 redirects are visible as individual spans. If the update centre is slow, you can see whether the latency is in the redirect chain or in the final content fetch.

### JVM metrics: the IDE's vital signs {#h3-10-jvm-metrics-the-ide-s-vital-signs}

The OTel agent automatically collects JVM metrics via the JVM runtime monitoring instrumentation. These arrive in Dash0 as standard OTel metrics on a 15-second export interval.

**Memory** --- broken down by GC pool:

|               Pool               |   Type   | Value at \~90s |
|----------------------------------|----------|----------------|
| G1 Eden Space                    | heap     | 69 MB          |
| G1 Old Gen                       | heap     | 16--18 MB      |
| G1 Survivor Space                | heap     | 3.5 MB         |
| Metaspace                        | non-heap | 36 MB          |
| CodeHeap (profiled nmethods)     | non-heap | 9 MB           |
| CodeHeap (non-profiled nmethods) | non-heap | 3 MB           |

Total heap at 90 seconds: \~197 MB. Total non-heap (JIT-compiled code + class metadata): \~141 MB. This is the real footprint of NetBeans 30 at startup --- numbers you could previously only get by attaching a profiler, and only for that single session.

**Garbage collection** --- two minor GC cycles in the first 90 seconds, both in G1 Young Generation (`end of minor GC`). No full GC, no Old Gen collection. The heap sizing is adequate for startup. If you saw repeated full GCs here it would be a signal that `Xmx` is set too low.

**Threads** --- 45 threads at steady state after startup, split across three states:

|     State     | Count  |
|---------------|--------|
| runnable      | 11     |
| timed_waiting | 22--24 |
| waiting       | 10     |

For comparison, the stub test process (essentially an empty JVM) had 21 threads. The additional 24 are the NetBeans module system, file watchers, Swing Event Dispatch Thread, LSP background workers, update centre fetchers, and the OTel agent's own export threads. Each thread state is a separate time series in Dash0, so you can watch for thread leaks --- a timed_waiting count that grows indefinitely is a classic sign of a thread pool that is not cleaning up.

**CPU time**: \~24 seconds of CPU consumed during a 20-second wall-clock run. The ratio above 1× is entirely the OTel agent's startup cost --- bytecode instrumentation of the JVM's loaded classes. After the first 10--15 seconds this overhead disappears and the agent's steady-state CPU footprint is negligible.

What this enables {#h2-11-what-this-enables}
--------------------------------------------

With this setup in place, several things become possible that were not before.

* **Alerting on IDE anomalies.** A Dash0 check rule on `jvm.gc.duration` could fire if GC pause time exceeds a threshold, alerting the developer (or their platform team) that the IDE is under memory pressure. A check on `jvm.thread.count` growing beyond 80 threads could catch plugin-induced thread leaks before they cause freezes.
* **Fleet-wide IDE health.** If the same `netbeans.conf` patch is deployed to all developer machines via your standard tooling (Ansible, Chef, a company dotfiles repo), every developer's IDE reports to the same Dash0 dataset. You can answer questions like: which version of NetBeans is the team running? Which machines have the slowest startup times? Which plugins are causing deprecation warnings across the fleet?
* **Startup regression detection.** If a new NetBeans release or a new plugin increases startup time by 15 seconds, the change would be visible in the log timestamps --- the delta between the first log record and the `USG_LOOK_AND_FEEL` record (which fires when the main window is ready). Over a team of 20 developers restarting 3 times per day, a 15-second regression is 15 developer-hours per week of invisible tax.
* **Correlation with application services.** Because the IDE and your application services use the same OTel backend, you can correlate IDE-side events with service-side events. If a developer's Maven build triggers a CI pipeline that causes elevated load on a staging service, both sides of that interaction are visible in the same observability dataset.

What comes next {#h2-12-what-comes-next}
----------------------------------------

The zero-code agent approach gives us JVM metrics, outbound HTTP spans, and bridged log records. The next layer is a NetBeans module --- a proper plugin that registers a `ModuleInstall.restored()` lifecycle hook and emits spans with business-meaningful attributes:

* **Startup span** : `netbeans.startup` from first class load to Welcome Screen painted, with child spans for each cluster load phase
* **Editor focus spans** : `netbeans.editor.focus_changed` with the file type and project name as attributes, giving you a picture of what developers are actually working on
* **Build spans** : wrapping the existing `TIMER` logger (which NetBeans already uses internally to record build and parse times) and forwarding those timings as OTel spans

The internal `TIMER` logger is particularly interesting. NetBeans already instruments itself for performance measurement; it just discards the data after displaying it locally in the Timers module window. Intercepting those `LogRecord` entries and forwarding them as OTel spans would give you a full startup trace --- every module initialisation timed to the millisecond --- without modifying any NetBeans source code beyond the one new plugin.

The broader point {#h2-13-the-broader-point}
--------------------------------------------

OpenTelemetry's value is that it is not application-specific. The JVM does not know or care whether the code running on it is a REST service, a batch job, or an IDE. The agent instruments bytecode. The protocol speaks HTTP or gRPC. The backend stores spans and metrics.

The mental model shift is small but significant: instead of asking "should I instrument this?", ask "is there a JVM process I care about?". If the answer is yes, the agent can tell you what it is doing. The cost of attachment is one JAR and three environment variables. The cost of not attaching is flying blind for the lifetime of that process.

In this case, the process is a tool used by every developer on your team for eight hours a day. That seems like exactly the kind of thing worth observing.
