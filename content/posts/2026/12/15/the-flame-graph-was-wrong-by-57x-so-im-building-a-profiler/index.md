---
title: "The flame graph was wrong by 57×. So I’m building a profiler."
date: "2026-12-15"
description: "When twenty operations run billions of times and none of them is hot, a flame graph tells you about HashMap and nothing you can act on."
authors:
  - "andrey-minogin"
image: "cover.jpg"
categories:
  - "Performance"
  - "Profiler"
canonical: "https://minogin.com/the-flame-graph-was-wrong-by-57x-so-im-building-a-profiler/"
related_posts:
  - "optimizing-nanosecond-operations-when-the-cpu-cache-matters"
  - "ahead-of-time-method-profiling"
  - "unikernel-profiling-and-troubleshooting-jvm-on-nanos-unikernel"
  - "runtime-code-analysis-in-the-age-of-vibe-coding"
---

This is the first post about it — why we would ever need a new profiler, and where the existing tools fall short.

Short version: on Apache Calcite’s planner, a flame graph put a rule at 0.81% when it was actually eating 46% of the time — and ranked it sixth.

## The code that has no hot spot

Some systems have a hot method. You profile them, one frame is red, you fix it, you go home.

Then there are systems where the same twenty operations run billions of times, each one taking tens of nanoseconds — a map lookup, a filter check, one edge of a graph traversal. Nothing is hot. Everything is warm. The flame graph is a smooth carpet of `HashMap.putVal` and `ArrayList.indexOfRange`, and every one of those attributions is correct and none of them is anything you can act on. Nobody is going to fix `HashMap`.

The question you actually have is not “which method is hot”. It is “which of my domain operations is eating the time” — expanding a frontier, matching a rule, evaluating a predicate.

A stack profiler cannot answer that, and the reason is more interesting than it first looks.

## The receiver is missing from the stack

A stack frame records which code ran: declaring class, method, line. It does not record *what the code ran on*. The receiver is sitting right there in this, but no stack walker dereferences it — walking a stack has to stay cheap and, in a signal handler, safe.

Usually that doesn’t matter, because the method name is a good enough proxy for what happened. It stops being good enough the moment your operations are objects dispatched through shared code — rules in a planner, handlers in a router, operators in an interpreter, message types in a broker. Then the identity you care about is exactly the part that isn’t recorded.

Here is the sharpest example I have, and it’s from real code I didn’t write: Apache Calcite’s query planner.

```kotlin
// simplified — Calcite's ConverterRule
// almost no rule overrides onMatch
public void onMatch(RelOptRuleCall call) {
    // each rule implements only this: build one node and return
    RelNode converted = convert(rel);

    // the base class does the rest: register the node,
    // which re-fires every rule against it
    call.transformTo(converted);
}
```

About twenty distinct optimization rules inherit that one onMatch and don’t override it. They are twenty different objects going through one method body. In my profile, `ConverterRule.onMatch` holds **49% of planning time** — and there is nothing in the recording that can split it, because the difference between those twenty rules lives entirely in a field the profiler never reads.

## And that makes the flame graph 57 times wrong

“Fine,” you say, “but convert is implemented per rule, so the name is one frame deeper. Just read it.”

It is there. And it is a trap. convert builds one node and returns; the expensive part — `transformTo` and the re-registration cascade under it — runs *after* it returns, under the base class’s frame. The subclass frame encloses the cheap half of its own firing.

Measured: `EnumerableMergeJoinRule` appears in **0.81%** of samples. Its actual share of planning time is **46%**. The flame graph is wrong by a factor of 57, and it ranks that rule sixth while putting a 30% rule first. Not “failed to answer” — answered confidently, in the wrong order, and the reader goes and optimises the wrong thing.

The nastiest part: in the same recording, rules that *do* override `onMatch` are attributed perfectly — 30.16% from the stacks against 30.21% from my instrument. So one profile is exact for one rule and off by 57× for another, and nothing in it tells you which case you’re in. The difference lives inside a base class you’ve never read.

(How I know the 46% is the real number is the subject of a later article in this series.)

## 46% of the time, 275× when removed

When the profiler doesn’t answer, everyone falls back to the same move: switch the thing off, run it again, compare. It is the only technique that answers the question you actually have — *what happens if I don’t do this*.

On Calcite, removing that heavy 46% `EnumerableMergeJoinRule` took planning from **17.8 s to 64.7 ms**. 275×.

Which is a spectacular result and not the cost of the rule. That rule shapes the search space, so the run without it explored a much smaller one — the fast run wasn’t the same problem minus one part, it was an easier problem. I had seen this before: on a graph traversal I worked on for a year, switching a piece of logic off made the traversed graph itself smaller, and every measurement I took that way was contaminated the same way.

(This doesn’t mean Calcite should drop merge join — my workload just had nothing sorted for it to exploit.)

So there are two different numbers here: where the time went, and what you’d actually get back by not spending it. A profiler only ever prints the first one. Here they were two orders of magnitude apart — because the rule wasn’t just spending its own 46%, it was making work for every other rule too.

## What I’m building

A profiler that attributes time to your operations — the ones you named — instead of to the methods they happen to run through.

The mechanism is one integer: each thread holds the id of the operation it is currently inside, and a separate thread reads every slot once a millisecond and counts.

```kotlin
val mergeJoin = register("mergeJoin")   // once, at startup

op(mergeJoin) { rule.onMatch(call) }    // at the call site
```

The id comes from the object, not the stack — so twenty rules sharing one method body produce twenty different numbers. And because the share of samples landing on an id is that operation’s share of time, an operation four orders of magnitude shorter than the sampling interval is no problem at all: precision comes from the number of samples, not the resolution of a clock. You don’t measure a nanosecond operation, you count how often you catch threads inside it.

The boundary costs about 1.7 ns, which is why you can wrap a 200 ns operation in one. `System.nanoTime()` costs ten times that.

None of this is new. Go has had it in the runtime for years as pprof labels. The JVM has no equivalent you can simply pick up — what exists is either part of a commercial APM or a low-level API with the rest left to you.

## What’s next in the series

- **The bench.** A profiler cannot validate itself. Building something to check it against caught three of my own mistakes, including a comparison that said the instrumented run was 15% faster than the uninstrumented one.
- **The Calcite trial in full** — including 12 points of discrepancy I still can’t account for.

Further out: the same sampler takes every thread in one pass, which makes one tick a snapshot of how many threads were actually working. A flame graph cannot answer that — it sums across threads by construction. That’s the part I’m most curious about.
