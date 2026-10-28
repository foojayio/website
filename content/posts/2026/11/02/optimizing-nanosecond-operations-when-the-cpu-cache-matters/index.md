---
title: "Optimizing nanosecond operations: when the CPU cache matters"
date: "2026-11-02"
description: "A check took 29 s. Removing it saved 24 s - the rest moved to a line we didn't touch. Why?"
authors:
  - "andrey-minogin"
image: "cover.jpg"
categories:
  - "Performance"
  - "Profiler"
canonical: "https://minogin.com/optimizing-nanosecond-operations-when-the-cpu-cache-matters/"
related_posts:
  - "java-native-memory-access-modes"
  - "java-benchmarks-on-single-board-computers"
  - "nulling-out-references-wont-help-your-garbage-collector"
  - "unikernel-profiling-and-troubleshooting-jvm-on-nanos-unikernel"
---

A profiler tells you where the time goes. It's tempting to read that as a promise: remove the 29 seconds it points at, and you get 29 seconds back. This article is about a case where that promise didn't hold. The check we removed really was expensive, but part of its cost silently moved to a line of code we never touched.

The setting is a hot loop in which a single operation takes about 200 nanoseconds. At that scale the CPU cache stops being a detail and starts deciding what your code costs, and one operation can pay a bill that another one runs up.

Let's optimize a graph traversal together: step by step, measuring every step. We start with the simplest one.

## The task

Maven Central gives us a large, natural graph to explore. As a sample task, let's resolve the classpaths of every Apache project at once: compile, runtime and test (the latest release only). The [Goblin](https://zenodo.org/records/15481588) snapshot from April 2025 contains 16.2 million releases and 136 million edges - that will be our graph in memory, on a laptop. `org.apache.*` brings us 30,889 roots × 3 scopes.

Surprisingly, proper dependency resolution is an NP-complete problem on its own ([Russ Cox, “Version SAT”](https://research.swtch.com/version-sat)). We’ll use Go’s much simpler rule, [Minimal Version Selection](https://research.swtch.com/vgo-mvs): the highest requested version wins. The output is checked: every step of the optimization must print exactly the same.

## The naive version

Resolving one classpath takes three walks. `select` collects everything reachable. `reach` walks again from the root, following only the highest versions and thus skipping branches reachable only by lower versions. `via` repeats `reach` from each direct dependency, to tell which one brings in each entry.

Implementation anyone begins with: releases as `group:artifact:version` strings, a graph as `HashMap<String, List<Dependency>>`, Breadth First Search with a `HashSet` of visited releases, roots split into parallel threads.

```kotlin
// For every release the walk visits: which of its dependencies to follow.
private fun passedOn(release: Release): List<Dependency> {
    val scopes = if (release == root) kind.rootScopes else kind.transitiveScopes
    // lookup 1: the release's dependencies
    return graph.dependencies.getValue(release)
        .filter {
            // compile, runtime or test
            it.scope in scopes &&
                    // not the root itself, at any version
                    it.artifact != rootArtifact &&
                    // lookup 2: does the target exist?
                    graph.existsOnCentral(it.artifact, it.version)
        }
}
```

There's a catch with Maven Central: it doesn't check that a release's dependencies exist. A POM can point at an artifact from another repository (androidx lives on Google's), a version range, a `-SNAPSHOT`. On our walks, 0.75% of the edges lead to a release that isn't in the graph, and without the check 13,155 of 30,889 compile classpaths fail. So every edge gets checked:

```kotlin
// Is "g:a:v" a release in the graph, i.e. published on Central?
fun existsOnCentral(artifact: Artifact, version: Version): Boolean =
    // builds "g:a:v", same 16 M map
    dependencies.containsKey(releaseOf(artifact, version))
```

## The profile

To profile the code we will use [TickSnick](https://ticksnick.com), my own profiler. Its main advantage is that it measures the operations you name, not the methods they happen to run through. You wrap each operation in `op(...)`:

```kotlin
private fun passedOn(release: Release): List<Dependency> = op(PASSED_ON) {
    val scopes = if (release == root) kind.rootScopes else kind.transitiveScopes
    op(DEPENDENCIES) { graph.dependencies.getValue(release) }
        .filter {
            it.scope in scopes &&
                    it.artifact != rootArtifact &&
                    op(ON_CENTRAL) { graph.existsOnCentral(it.artifact, it.version) }
        }
}
```

The report gives every operation its time, its calls and so its time per call, even for one that takes 100 ns. And the labels stay the same as the code changes, so the profiles of two versions diff row by row. (Why I built it: [The flame graph was wrong by 57×](https://minogin.com/the-flame-graph-was-wrong-by-57x-so-im-building-a-profiler/).)

Turbo off, 18 GB heap, three measured batches per run, four runs of each version, alternated. TickSnick's report, naive implementation, run 1 of 4 (columns and rows trimmed):

```
COARSE OPERATIONS - wall time, inclusive, nested as it ran
runBatch                      82.0 s
  resolveRoot                 82.0 s   100%
    resolve                   82.0 s   100%
      select                  42.5 s    52%
        - merge        own    14.6 s    34%
        - onCentral    own    12.2 s    29%   29.2 s in all, under 4 operations
      via                     28.1 s    34%
        - onCentral    own    11.8 s    42%
      reach                   10.1 s    12%
        - onCentral    own    5.21 s    52%

COARSE AND FINE OPERATIONS - own time
Operation      Thread-time%  Thread-time        Calls   Thread-time per call
onCentral             28.1%        117 s  569,233,860   205 ns
merge                 14.1%       58.4 s  199,138,728   293 ns
```

The top row in the biggest walk is `merge`, 14.6 s. But `onCentral`, the Central check, is in all three walks: **29.2 s in all**, 569 million calls at 205 ns. And its answer never changes: whether `g:a:v` exists depends only on the edge, not on the walk.

This immediately suggests an optimization: do the Central check just once.

## centralEdges

`centralEdges` drops every edge to a release that doesn't exist - 4.2% of all edges in the graph - once, before any walk. The walks keep the scope and root checks only:

```kotlin
override fun prepare(graph: Graph): Graph {
    val dependencies = HashMap<Release, List<Dependency>>(graph.dependencies.size * 4 / 3 + 1)
    for ((release, list) in graph.dependencies)
        dependencies[release] = list.filter { graph.existsOnCentral(it.artifact, it.version) }
    return Graph(dependencies)
}
```

The profile says the check took 29.2 s, so that's what we expect to save. Let's run it.

## The diff

Let's look at TickSnick's diff of all four runs of each:

```
Diff          naive (4 runs) -> centralEdges (4 runs)
...
runBatch        82.4 s -> 58.1 s     -29%   +-1.3%  faster
```

> **Success: 82 s -> 58 s!**

Three batches went from 82.4 s to 58.1 s: 24.3 s saved. Confirmed by four runs: TickSnick reports the 95% confidence interval, ±1.3%.

But the profile said the check took 29.2 s. Where are the other 4.9?

How do we find out? The same diff answers that too: it matches every operation by name across the two versions and shows what happened to each one.

```
Operation      Thread-time        change                  Per call            Calls
onCentral      118 s -> -                                 207 ns -> -         569,233,860 -> -
dependencies   23.2 s -> 43.1 s   +86%  +-5.5%  slower    104 ns -> 194 ns    222,178,167 =
passedOn       23.4 s -> 25.7 s   +9.9% +-6.8%  slower    105 ns -> 116 ns    222,178,167 =
merge          59.0 s -> 59.7 s   +1.1% +-3.2%  unclear   296 ns -> 300 ns    199,138,728 =
visit          58.5 s -> 59.1 s   +0.9% +-3.1%  unclear   104 ns -> 105 ns    562,928,865 =
```

Aha, here it is: `dependencies` got 86% slower. That's the label on lookup 1 in `passedOn`, `graph.dependencies.getValue(release)` - fetching the release's dependency list from the same 16 M map. A line we didn't touch. The same 222 million calls - each one just takes longer. Why would an untouched lookup get slower?

This is actually super interesting. Again, we didn't touch that line, but it got much slower - as if it had taken over part of `onCentral`'s cost. When does that happen? When one part was helping another. But how? By caching!

The `onCentral` check we removed looked up `g:a:v` in the same 16-million-entry map, for the release the walk fetches next. It paid the trip to main memory, and the walk's lookup found the entry in cache. Without the check, the lookup now goes to main memory.

Can we prove this guess? If it's right, putting just the check back should make the lookup fast again. So: `centralEdgesRecheck`, the pruned graph with the check put back - it always passes now, so all it does is touch the memory. Three runs of each, a separate set:

```
Operation      Thread-time        change                  Per call            Calls
onCentral      - -> 111 s                                 - -> 196 ns         - -> 564,888,102
dependencies   41.1 s -> 22.2 s   -46%  +-3.2%  faster    185 ns -> 99.9 ns   222,178,167 =
```

Yes: 185 ns back to 99.9 ns, about where it was in naive.

## Conclusions

**What the profile told us:** where the time went. 29.2 s in a check whose answer never changes. We moved it out of the walk and gained 24.3 s.

**What it couldn't tell us:** what the optimization would save. Part of the check's cost was a trip to memory, and that trip didn't go away - the next line that needed the same entry paid for it.

**So after every step,** look at what got more expensive, not only at what got cheaper. Same calls, slower each - that's a line paying someone else's bill.

Next: `merge` now leads, and the lookup at 194 ns points at the memory layout itself. It only gets more interesting from here. Stay tuned.
