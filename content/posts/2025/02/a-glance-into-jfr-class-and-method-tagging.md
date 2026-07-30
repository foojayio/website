---
title: "A Glance into JFR Class and Method Tagging"
slug: "a-glance-into-jfr-class-and-method-tagging"
date: "2025-02-17T09:26:02+00:00"
lastmod: "2025-02-17T09:26:04+00:00"
description: "Ever wonder how the JDK Flight Recorder (JFR) keeps track of the classes and methods it has collected for stack traces and more? In this short article, - by Johannes Bechberger"
authors:
  - "johannes-bechberger"
image: "https://foojay.io/wp-content/uploads/2025/02/tag-1-2000x1086-1.png"
categories:
  - "Developer Tools"
  - "Performance"
tags:
related_posts:
  - "a-closer-look-at-jfr-streaming"
  - "using-java-flight-recorder-and-mission-control-part-2"
  - "using-java-flight-recorder-and-mission-control-part-3"
  - "where-production-policy-belongs-building-eliya-in-public"
enlighterjs: true
frozen: false
---

Ever wonder how the JDK Flight Recorder (JFR) keeps track of the classes and methods it has collected for stack traces and more?

In this short article, I'll explore JFR tagging and how it works in the OpenJDK.

Tags {#h2-0-tags}
-----------------

JFR files consist of self-contained chunks. Every chunk contains:

* metadata
* events
* [mappings of IDs to actual values](https://mail.openjdk.org/pipermail/hotspot-jfr-dev/2020-February/001154.html), the IDs are used in the events in place of stack traces, classes, methods, strings, ...

The maximum chunk size is usually 12MB, but you can configure it:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">java -XX:FlightRecorderOptions:maxchunksize=1M</pre>

<br />

Whenever JFR collects methods or classes, it has to somehow tell the JFR writer which entities have been used so that their mapping can be written out. Each entity also has to have a tracing ID that can be used in the events that reference it.

This is where JFR tags come in. Every class, module, and package entity has a 64-bit value called `_trace_id` (e.g., [classes](https://github.com/openjdk/jdk/blob/f74c4dfe0b0c384a25f0b7a2330ba96d50b7fceb/src/hotspot/share/oops/klass.hpp#L204)). Which consists of both the ID and the tag. Every method has an [`_orig_method_idnum`](https://github.com/openjdk/jdk/blob/f74c4dfe0b0c384a25f0b7a2330ba96d50b7fceb/src/hotspot/share/oops/constMethod.hpp#L210), essentially its ID and a [trace flag](https://github.com/openjdk/jdk/blob/f74c4dfe0b0c384a25f0b7a2330ba96d50b7fceb/src/hotspot/share/oops/method.hpp#L84), which is essentially the tag.

In a world without any concurrency, the tag could just be a single bit, telling us whether an entity is used. But in reality, an entity can be used in the new chunk while we're writing out the old chunk. So, we need to have two distinctive periods (0 and 1) and toggle between them whenever we write a chunk.  

Tagging {#h2-1-tagging}
-----------------------

We can visualize the whole life cycle of a tag for a given entity:
![](https://mostlynerdless.de/wp-content/uploads/2025/02/tag-1-2000x1086.png)

In this example, the entity, a class, is brought into JFR by the method sampler ([link](https://github.com/openjdk/jdk/blob/5f5ed961db8462b0e01ca83194722d4456ba2372/src/hotspot/share/jfr/recorder/stacktrace/jfrStackTrace.cpp#L262)) while walking another thread's stack. This causes the class to be tagged and enqueued in the internal entity queue (and is therefore known to the JFR writer) if it hasn't been tagged before ([source](https://github.com/openjdk/jdk/blob/5f5ed961db8462b0e01ca83194722d4456ba2372/src/hotspot/share/jfr/recorder/checkpoint/types/traceid/jfrTraceIdLoadBarrier.inline.hpp#L73)):

<pre class="EnlighterJSRAW" data-enlighter-language="cpp" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">inline void JfrTraceIdLoadBarrier::load_barrier(const Klass* klass) {
  SET_METHOD_AND_CLASS_USED_THIS_EPOCH(klass);
  assert(METHOD_AND_CLASS_USED_THIS_EPOCH(klass), "invariant");
  enqueue(klass);
  JfrTraceIdEpoch::set_changed_tag_state();
}

inline traceid JfrTraceIdLoadBarrier::load(const Klass* klass) {
  assert(klass != nullptr, "invariant");
  if (should_tag(klass)) {
    load_barrier(klass);
  }
  assert(METHOD_AND_CLASS_USED_THIS_EPOCH(klass), "invariant");
  return TRACE_ID(klass);
}</pre>

This shows that tagging also prevents entities from being duplicated in a chunk.

Then, when a chunk is written out. First, a safepoint is requested to initialize the next period (the next chunk) and the period to be toggled so that the subsequent use of an entity now belongs to the new period and chunk. Then, the entity is written out, and its tag for the previous period is reset ([code](https://github.com/openjdk/jdk/blob/5f5ed961db8462b0e01ca83194722d4456ba2372/src/hotspot/share/jfr/recorder/service/jfrRecorderService.cpp#L467)). This allows the aforementioned concurrency.

But how does it ensure that the tagged classes aren't unloaded before they are emitted? By writing out the classes when any class is unloaded. This is simple yet effective and doesn't need any change in the GC.

Conclusion {#h2-2-conclusion}
-----------------------------

Tagging is used in JFR to record classes properly, methods, and other entities while also preventing them from accidentally being garbage collected before they are written out. This is a simple but memory-effective solution. It works well in the context of concurrency but assumes entities are used in the event creation directly when tagging them. It is not supported to tag the entities and then push them into the queue to later create events asynchronously. This would probably require something akin to reference counting.

Thanks for coming this far in a blog post on a profiling-related topic. I chose this topic because I wanted to learn more about tagging, and I plan to do more of these short OpenJDK-specific posts.

P.S.: [I gave three talks at FOSDEM, on fuzzing schedulers, sched-ext, and profiling.](https://fosdem.org/2025/schedule/speaker/johannes_bechberger/)

*This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone. It first appeared on my personal blog [mostlynerdless.de](https://mostlynerdless.de/).*

<br />
