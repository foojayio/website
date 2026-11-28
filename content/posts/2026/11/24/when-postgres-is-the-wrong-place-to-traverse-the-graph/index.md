---
title: "When Postgres is the wrong place to traverse the graph"
date: "2026-11-24"
description: "Recursive CTEs hit an O(b^D) wall, and no amount of indexing or tuning moves it."
authors:
  - "andrey-minogin"
image: "cover.jpg"
categories:
  - "Performance"
  - "Databases"
canonical: "https://minogin.com/when-postgres-is-the-wrong-place-to-traverse-the-graph/"
related_posts:
  - "optimizing-nanosecond-operations-when-the-cpu-cache-matters"
  - "duckdb-in-spring-batch-replace-in-memory-java-loops-with-one-sql-statement"
  - "the-148-ms-was-waiting-not-work"
  - "5-jvm-heap-oom-patterns"
---

Some performance problems are not query-optimization problems. You can index, rewrite, tune parameters, throw hardware at it — and still lose, because the work simply doesn’t belong in the database. The fix isn’t a better query. It’s moving the work somewhere else.

## The task

Imagine you perform a deep search in a huge graph — think of finding level-N friends in a social network or tier-N product parts in a bill of materials. You start from some node, visit all its nearest neighbours and recursively continue from there (breadth-first search) or recursively traverse every path (depth-first search).

With Postgres the standard approach is to store `(node A, node B)` edge tuples in the database and apply recursive CTEs to query the graph.

```sql
WITH RECURSIVE r(node, depth, path) AS (
  SELECT       -- Start with the first node
    :first_node AS node,
    0 AS depth,
    ARRAY[:first_node] AS path
  UNION ALL
  SELECT       -- Recursively find next node
    g.b AS node,
    r.depth + 1 AS depth,
    r.path || g.b AS path
  FROM graph g
  JOIN r ON r.node = g.a
  WHERE g.b <> ALL(r.path)    -- Guard against cycles
)
SELECT *
FROM r;
```

## The problem

There are two main parameters affecting the performance of graph traversal: the branching factor `b` — average number of node connections, and the depth of traversal `D`.

Whatever you do you eventually hit the `O(b^D)` wall. The average number of nodes to traverse grows exponentially with `D`. There’s no algorithmic way you can do deep search asymptotically faster (no, quantum computing would not help either).

As your database grows the `b` will most probably increase, and as your business grows there will certainly be a demand to increase `D`. This means you would be constantly stuck with search time regressing from milliseconds to minutes. You could fight this by first using materialized CTEs, then switching to temporary tables with indexes, but all those efforts would be immediately swallowed by a slight increase of search depth.

Another important issue is that Postgres cannot perform depth-first search, the `SEARCH DEPTH FIRST` does not change the algorithm under the hood. This means that at depth `N` you need to store `~b^N` frontier nodes.

## The solution

First of all you need to switch to the depth-first search to only store the current path in the memory, not the whole frontier. Then, as you cannot work around the general `O(b^D)` limitations of the algorithm, you should optimize each basic operation for speed and memory consumption to its limits.

We should load the whole graph into the memory and make it effectively accessible. The main operation is finding the neighbours of the current node. For the most optimal access we need a map whose key is the node id and whose value is the list of connected nodes.

Standard approach assuming node id is `Int` would be

```kotlin
val graph: Map<Int, List<Int>> = hashMapOf()
```

But here comes the huge inefficiency. Maps and lists in Java operate on boxed values. Every id becomes a boxed `Integer`: a separate heap object — a 12-byte header plus the 4-byte value, ~16 bytes total — plus a 4–8 byte reference to reach it. Compare that to 4 bytes for a raw `int`. Accessing such a map is also slower: each lookup chases a pointer to a scattered heap object — a likely cache miss — and then unboxes it.

This could be overcome by using specialized Java collection libraries such as HPPC (High Performance Primitive Collections) which operate over unboxed values instead. See [Reducing memory usage 10 times with High-Performance Primitive Collections](https://minogin.com/how-to-reduce-memory-usage-10-times-with-high-performance-primitive-collections/)

In our example the graph would become:

```kotlin
val graph: IntObjectMap<IntList> = IntObjectHashMap()
```

And as shown in the referenced article this leads to up to 10x less memory consumption. The latency improvement is usually even more substantial.

One additional optimization is to prematurely filter the nodes, throwing away whole subgraphs if the node or edge does not satisfy certain conditions.

You should also decide whether you actually need all paths to each node, or just the reachable nodes. If only distinct target nodes matter, a visited-set collapses the exponential blow-up toward linear! This is also something much easier to implement outside of SQL.

## A note on concurrency

If you are going to update the graph in real time you need to make it writable. Here comes the trade-off — making the whole graph immutable and replacing it every time is too expensive, especially if the changes are minor. But making every element concurrently writable is also expensive.

A possible solution might be to combine the two approaches:

```kotlin
val graph: ConcurrentIntObjectMap<IntList> = ConcurrentIntObjectHashMap()
```

Keep the relatively small `IntList` values immutable and swap them wholesale, while making the outer map concurrently accessible via optimistic locking. This is not natively supported by HPPC, I will show the exact implementation in one of the next articles.

## The lesson

The useful skill here isn’t using primitive collections or optimistic locking. The value comes from seeing the architectural flaw and moving application parts to where they actually belong.

When you optimize SQL queries you see the solution as a better query plan, not as a conceptual shift. Sometimes it helps to stop fighting, zoom out and start from scratch.
