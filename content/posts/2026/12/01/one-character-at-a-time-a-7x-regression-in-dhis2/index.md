---
title: "One character at a time: a 7× regression in DHIS2"
date: "2026-12-01"
description: "Compression put 31× fewer bytes on the wire and made the request 27 seconds slower."
authors:
  - "andrey-minogin"
image: "cover.jpg"
categories:
  - "Performance"
  - "Java"
canonical: "https://minogin.com/one-character-at-a-time-a-7x-regression-in-dhis2/"
related_posts:
  - "optimizing-nanosecond-operations-when-the-cpu-cache-matters"
  - "the-148-ms-was-waiting-not-work"
  - "streaming-large-file-uploads"
  - "5-jvm-heap-oom-patterns"
---

From time to time I voluntarily do performance audits of open source projects. DHIS2 is a health information system used by ministries of health in about seventy countries. It ships a bulk data export API, `dataValueSets`, that integrations lean on heavily — data warehouses, donor reporting, tracker-to-aggregate sync. I put it on a bench, on the public demo database, and started measuring.

Although the 2.43 release promises major import/export gains, on the same machine, the same database and the same request, exporting `dataValueSets.json` got 7.3× slower than in 2.42.

| **24-month export** | **2.42.5.2** | **2.43.1** |
|---|---|---|
| JSON | 3.25 s | 23.79 s |
| CSV | 3.77 s | 7.39 s |
| JSON + gzip | 4.29 s | 51.17 s |

Three things are worth noticing before any profiler is opened:

- CSV got 3.6 s slower, JSON 20.5 s — same rows, same database;
- PostgreSQL time is always 2.91 s (`pg_stat_statements`);
- Compression — which puts 31× fewer bytes on the wire — makes the request 27 seconds *slower!*

Every step below is the same move: change one thing and measure again. Format. Compression. Time window. Version. Environment stays untouched. This is how we get a lot of ideas without even looking at the source code.

## The control column

The bench was deliberately boring: the public Sierra Leone demo database, PostgreSQL 16, fixed container limits, curl to `/dev/null`, cold run discarded. Anyone can rerun it. Both versions returned **exactly 973,730 values** for the same window.

I measured formats × compression × three time windows on both versions. CSV went in for completeness. It turned out to be the most valuable column in the table.

Spread across those values, the cost of producing one of them:

| **cost per data value** | **2.42** | **2.43** |
|---|---|---|
| JSON | 3.3 µs | 24.4 µs |
| CSV | 3.9 µs | 7.6 µs |

JSON went up 7×, CSV under 2× — same rows, same database. That single column kills most of the diagnostic tree before you touch a tool: we exclude the database, the network, the container, the JVM flags. Whatever happened, happened between the ResultSet and the socket, on the JSON side only. Two immediate thoughts — either the JSON response is simply heavier or the serializer was somehow broken.

## The gzip anomaly

Let’s check the response path: if we send `?compression=gzip`, does the finding go away?

It didn’t go away. It got 27 seconds worse.

First conclusion: it’s definitely not the network. You can’t blame transfer costs when removing 97% of the transfer makes things worse.

Second, and more useful: it’s not just the serializer. Per MB of input, compressing that JSON costs 0.094–0.108 s; compressing the CSV from the same runs costs 0.004 s. Same algorithm, same level, same library — a 25× asymmetry per input byte. gzip does not care what it compresses. There should be some difference in the way we stream the result.

Again — useful ideas without even touching the source.

## Where the time goes

The JFR recording is unambiguous: 60% of samples in JsonAppender, 8% in JDBC and Hibernate combined. The hot frame:

```java
PrintStream.write(String)

  ← PrintStream.print(char)

  ← PrintStream.append(char)

  ← JsonAppender.appendEscaped(int) line: 123

  ← IntPipeline$Head.forEachOrdered
```

2.43 replaced the Jackson writer with `json-tree 1.8.1`, whose appender writes through a `PrintStream`. `PrintStream.append(char)` is synchronized and flushes on every call — once per character, roughly 250 million times per export. That’s the gzip number too: the deflater wasn’t handed a stream, it was handed a doorbell.

Checked, not argued: a DHIS2 build with a later `json-tree` — same host, same script, same 973,730 values — brings the gzip penalty from +27.4 s down to +0.5 s.

## About the assistant in the room

Most of the grinding was done by Claude Code. It does well, but I don’t see how it could operate without human steering. It generates far more branches than I would, and most are wrong in recognisable ways: narrowing to whatever is easiest to measure, inventing parameters nobody asked for, merging two findings because the combination looks stronger, promoting an observation to a result.

So most of my work was refusing branches, one message each. No, we’re not benchmarking index usage — first say what would even count as a problem here. No, no isolated microbenchmark of the library — pull the branch and rerun the whole thing. To kill nine branches out of ten you have to know which one is load-bearing, and the tool can’t know that for you.

The generation still earned its place: the gzip run is a step I might have skipped alone, and it turned me around at least twice — once by pointing out that a neighbouring path still runs on Jackson and doesn’t have the problem at all.

What made this assistant-proof: the objection is almost always one measurement away. “It’s just JSON being slow” — measure CSV. “It’s the network” — turn on gzip. “It’s already fixed upstream” — build that branch and measure it. Minutes each, and each replaces an argument with a number.

## What I did about it

Reported before publishing: [json-tree #102](https://github.com/dhis2/json-tree/issues/102) for the per-character escaping, [json-tree #103](https://github.com/dhis2/json-tree/issues/103) for a correctness bug found on the way (case -31 in the escape switch is unreachable, so control characters below 0x20 are emitted raw — still present in 1.9.4), and [a Community of Practice thread](https://community.dhis2.org/t/datavaluesets-json-export-is-7x-slower-in-2-43-than-2-42-cause-identified-fix-exists-upstream-but-is-not-backported/76144) asking for the json-tree bump to be backported.

The same endpoint carries a second, unrelated regression, [reported in its own thread](https://community.dhis2.org/t/datavaluesets-export-stops-streaming-in-2-43-default-order-by-plus-a-lost-jdbc-fetch-size/76636): a default `ORDER BY` that spills to disk before emitting a row, plus a lost JDBC fetch size — together they push time to first byte from 0.16 s to 3.3 s on both formats.

External accounts can’t create issues in DHIS2’s Jira, so the forum is the front door.

The flush fix exists upstream; 2.43 still pins the old json-tree, and deployments will keep installing that release for about a year. Three weeks later: no replies.

That’s the same phenomenon the article is about. A 7× regression arrived unnoticed and was half-fixed unnoticed, in software that ministries of health run on. Nothing in the pipeline measures this, so nothing in the pipeline can notice.

**If you’re on 2.43 today:** use `dataValueSets.csv` with `?compression=gzip` — 7.94 s and 6.1 MiB against JSON’s 23.79 s and 246.4 MiB. Compression is a query parameter; `Accept-Encoding` does nothing. A workaround, not advice: on 2.42, JSON was the faster format.

**If you’re on anything else:** put a neighbouring path in your benchmark matrix even when you don’t know why. Keep the previous version measurable on the same hardware. And when someone hands you an objection, price it — usually it’s one measurement away.
