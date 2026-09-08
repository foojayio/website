---
title: "From OpenTelemetry to DuckDB"
date: "2026-09-08T08:29:37+00:00"
lastmod: "2026-09-08T08:38:34+00:00"
description: "Observability platforms generally expect you to do your analysis inside them, using their query language and their dashboards. That works for the…"
authors:
  - "geertjan-wielenga"
image: "duckdb-square-icon.svg"
categories:
  - "Developer Tools"
  - "DuckDB"
  - "OpenTelemetry"
  - "Performance"
related_posts:
frozen: false
---

Observability platforms generally expect you to do your analysis inside them, using their query language and their dashboards. That works for the questions you already know you have. It works less well for the ad-hoc ones: ***a quick percentile*** , ***a pivot by status code*** , or "***which service is actually eating the latency budget?***" once the data is in front of you.

This article describes a small pipeline for that kind of question. On one side is [DuckDB](https://duckdb.org), the in-process analytical database that reads CSV, JSON and Parquet directly. On the other is the [Dash0 CLI](https://github.com/dash0hq/dash0-cli), which pulls OpenTelemetry spans, logs, metrics and traces out of [Dash0](https://www.dash0.com) and prints them as CSV or JSON.

The two can be connected on your command line with a pipe:

```
dash0 spans query -o csv --limit 100000 | duckdb -c "SELECT ..."
```

That takes production telemetry and runs SQL over it, with no intermediate file and no import step.

The rest of this article shows how to set it up and what you can do once it works.

## Why DuckDB

DuckDB is often described as "SQLite for analytics", and the comparison makes sense in that it is a single dependency, runs in-process, and has no server ([unless that's what you want](https://duckdb.org/2026/08/17/duckdb-20-highlights#1-duckdb-as-a-server-quack-and-connect)). The key detail that matters here, though, is that DuckDB ***treats files and streams as tables*** . You can point it at a CSV on disk, a Parquet file in S3, or standard input, and query it ***directly***.

That makes it a convenient sink for command-line output. Any tool that emits CSV or JSON — `kubectl`, `aws`, `gh`, `ps`, a log file, or a CLI like `dash0` — can be queried with SQL ***without you needing to write a parser***.

**Note:** The good news for JVM-based developers (Java, Kotlin), reading this here on Foojay is that DuckDB also has a [JDBC driver](https://duckdb.org/docs/api/java). The SQL in this article can be moved into a Java service unchanged. The pipeline is useful for exploring, while JDBC is where you put whatever you found once you want to run it in your Java code regularly.

## Setup

You need two binaries locally on disk. Neither needs a server, a container, or a config file.

### 1. Install DuckDB

On macOS and Linux, Homebrew is the easiest route:

```
brew install duckdb
```

There are also standalone binaries on the [DuckDB installation page](https://duckdb.org/docs/installation/) for every major platform. Check it works:

```
duckdb --version
```

### 2. Install the Dash0 CLI

The Dash0 CLI is distributed as a Homebrew cask:

```
brew install --cask dash0hq/dash0-cli/dash0
```

The qualified name taps the repository automatically, so there's no separate `brew tap`. GitHub Releases binaries, a `FROM scratch` Docker image and a Nix flake are documented in the [installation guide](https://github.com/dash0hq/dash0-cli). Check it:

```
dash0 --version
```

### 3. Authenticate

Interactive login goes through OAuth:

```
dash0 login
```

For scripts and CI, set the connection details as environment variables instead. Each setting is resolved from the environment first, then flags, then the active profile:

```
export DASH0_API_URL=https://api.<region>.<cloud>.dash0.com
export DASH0_AUTH_TOKEN=auth_your_token_here
export DASH0_DATASET=default
```

To confirm that credentials and dataset resolve the way you expect:

```
dash0 config show
```

## Getting spans out of Dash0

Every `dash0` command supports JSON output and a filter syntax that matches the Dash0 UI. If it detects an AI coding agent in the environment it defaults to JSON, skips confirmation prompts and turns off colour. For this pipeline the relevant flag is `-o csv`.

Here's a spans query for the last 30 minutes, as CSV:

```
dash0 spans query --from now-30m -o csv --limit 100000
```

The output looks like this:

```
otel.span.start_time,otel.span.duration,otel.span.name,otel.span.status.code,service.name,otel.parent.id,otel.trace.id,otel.span.id,otel.span.links
2026-09-08T09:12:03.456Z,150ms,GET /api/users,OK,frontend,,0af7651916cd...,b7ad6b71...,
...
```

You can narrow it with the same filters the UI uses — `--filter "service.name is checkout"`, `--filter "otel.span.status.code is ERROR"` — but for exploration it's usually easier to pull a wide window and do the slicing in SQL.

## Kicking the DuckDB tires

This pipes a spans query into DuckDB and produces per-service request counts, error counts and a small ASCII bar chart:

```
dash0 spans query --from now-30m -o csv --limit 100000 \
  | duckdb -c "
    SELECT
      \"service.name\"                                          AS service,
      count(*)                                                  AS reqs,
      count(*) FILTER (WHERE \"otel.span.status.code\"='ERROR') AS errors,
      bar(count(*), 0, 30, 25)                                  AS traffic
    FROM read_csv('/dev/stdin')
    GROUP BY 1 ORDER BY reqs DESC;
  "
```

```
┌─────────────┬───────┬────────┬──────────────────────────┐
│   service   │ reqs  │ errors │          traffic          │
├─────────────┼───────┼────────┼──────────────────────────┤
│ frontend    │    28 │      0 │ ███████████████████████▍  │
│ postgres    │    25 │      1 │ ████████████████████▎     │
│ api-gateway │    16 │      1 │ █████████████▍            │
│ checkout    │    15 │      3 │ ████████████▌             │
│ auth        │    10 │      2 │ ████████▍                │
└─────────────┴───────┴────────┴──────────────────────────┘
```

Two things to note:

* **`read_csv('/dev/stdin')`** reads the piped stream as a table and infers column names and types from the header. There's no `CREATE TABLE`.
* **Dotted column names** like `service.name` come from OpenTelemetry's semantic conventions. In SQL they have to be double-quoted (`"service.name"`), otherwise the dot is parsed as a schema or table qualifier.
* `FILTER (WHERE …)` and `bar()` are standard DuckDB.

## Making it persistent

For anything beyond a single query, it's easier to export once and load the data into a DuckDB file. First write the CSV:

```
dash0 spans query --from now-30m -o csv --limit 100000 > /tmp/spans.csv
```

Then create a table with a numeric duration column. The CLI formats durations as strings like `150ms` or `2.49s`, so they need normalising to milliseconds before you can do arithmetic on them:

```
CREATE TABLE spans AS
SELECT
  "service.name"          AS service,
  "otel.span.name"        AS op,
  "otel.span.status.code" AS status,
  "otel.trace.id"         AS trace_id,
  "otel.span.duration"    AS dur_str,
  CASE
    WHEN "otel.span.duration" LIKE '%ms'                                    THEN CAST(regexp_extract("otel.span.duration",'[0-9.]+') AS DOUBLE)
    WHEN "otel.span.duration" LIKE '%µs' OR "otel.span.duration" LIKE '%us' THEN CAST(regexp_extract("otel.span.duration",'[0-9.]+') AS DOUBLE)/1000.0
    WHEN "otel.span.duration" LIKE '%s'                                     THEN CAST(regexp_extract("otel.span.duration",'[0-9.]+') AS DOUBLE)*1000.0
  END                     AS dur_ms
FROM read_csv('/tmp/spans.csv');
```

Run `duckdb /tmp/spans.duckdb` and paste that in. The queries below all run against the resulting `spans` table.

## Five useful queries

### 1. Golden signals per service

Latency percentiles and error rate for each service, in one query:

```
SELECT service, count(*) AS reqs,
  round(quantile_cont(dur_ms, 0.50), 1) AS p50_ms,
  round(quantile_cont(dur_ms, 0.90), 1) AS p90_ms,
  round(quantile_cont(dur_ms, 0.99), 1) AS p99_ms,
  round(max(dur_ms), 1)                 AS max_ms,
  count(*) FILTER (WHERE status='ERROR') AS errors,
  round(100.0 * count(*) FILTER (WHERE status='ERROR') / count(*), 1) AS err_pct
FROM spans GROUP BY service ORDER BY p99_ms DESC;
```

`quantile_cont` gives continuous (interpolated) percentiles, and `FILTER (WHERE …)` counts errors in the same pass as the totals. In the sample data, `checkout` has a p99 of 2.46 seconds and a 20% error rate.

### 2. A latency histogram

Bucket the durations and draw them with `bar()`:

```
WITH bucketed AS (
  SELECT CASE
    WHEN dur_ms < 10   THEN '1  <10ms'
    WHEN dur_ms < 50   THEN '2  10-50ms'
    WHEN dur_ms < 100  THEN '3  50-100ms'
    WHEN dur_ms < 250  THEN '4  100-250ms'
    WHEN dur_ms < 500  THEN '5  250-500ms'
    WHEN dur_ms < 1000 THEN '6  0.5-1s'
    ELSE                    '7  >1s'
  END AS bucket
  FROM spans
)
SELECT regexp_replace(bucket, '^[0-9]  ', '') AS latency,
       count(*)                               AS n,
       bar(count(*), 0, 30, 40)               AS distribution
FROM bucketed GROUP BY bucket ORDER BY bucket;
```

```
┌───────────┬───────┬──────────────────────────────────────────┐
│  latency  │   n   │               distribution               │
├───────────┼───────┼──────────────────────────────────────────┤
│ <10ms     │     5 │ ██████▏                                │
│ 10-50ms   │    21 │ ████████████████████████████           │
│ 50-100ms  │    10 │ █████████████▍                           │
│ 100-250ms │    30 │ ████████████████████████████████████████ │
│ 250-500ms │    15 │ ████████████████████                    │
│ 0.5-1s    │     5 │ ██████▏                                │
│ >1s       │     8 │ ██████████▍                              │
└───────────┴───────┴──────────────────────────────────────────┘
```

The distribution is bimodal: most requests land between 10 and 500 milliseconds, with a separate group over a second. Neither the mean nor the median would show that.

### 3. Pivot by status

DuckDB has `PIVOT` as a keyword. This turns the status values into columns:

```
PIVOT spans ON status USING count(*) GROUP BY service ORDER BY service;
```

```
┌─────────────┬───────┬───────┐
│   service   │ ERROR │  OK   │
├─────────────┼───────┼───────┤
│ api-gateway │     1 │    15 │
│ auth        │     2 │     8 │
│ checkout    │     3 │    12 │
│ frontend    │     0 │    28 │
│ postgres    │     1 │    24 │
└─────────────┴───────┴───────┘
```

### 4. Share of total latency per service

This uses a window function over an aggregate: `sum(sum(dur_ms)) OVER ()` gives the grand total inside a grouped query, so each service's share can be computed in one pass:

```
SELECT service,
  count(*)                                                 AS spans,
  round(sum(dur_ms), 1)                                    AS total_ms,
  round(100.0 * sum(dur_ms) / sum(sum(dur_ms)) OVER (), 1) AS pct_of_budget
FROM spans GROUP BY service ORDER BY total_ms DESC;
```

```
┌─────────────┬───────┬──────────┬───────────────┐
│   service   │ spans │ total_ms │ pct_of_budget │
├─────────────┼───────┼──────────┼───────────────┤
│ checkout    │    15 │  17592.0 │          63.6 │
│ frontend    │    28 │   4744.0 │          17.1 │
│ api-gateway │    16 │   3992.0 │          14.4 │
│ auth        │    10 │    684.0 │           2.5 │
│ postgres    │    25 │    654.0 │           2.4 │
└─────────────┴───────┴──────────┴───────────────┘
```

`checkout` accounts for 63.6% of total span time with 15 of 94 spans. If you were going to optimise one thing, that's where to start.

### 5. Convert to Parquet

DuckDB reads and writes Parquet natively:

```
COPY (SELECT * FROM read_csv('/tmp/spans.csv'))
TO '/tmp/spans.parquet' (FORMAT parquet, COMPRESSION zstd);
```

On this small sample the file is roughly half the size of the CSV. On real volumes the ratio is much better, since telemetry has a lot of repeated values and columnar storage with dictionary encoding compresses that well. The Parquet file can then be queried directly:

```
SELECT "service.name" AS service, count(*) AS spans
FROM '/tmp/spans.parquet'
GROUP BY 1 ORDER BY 2 DESC;
```

Archiving a day's spans as Parquet gives you a cheap cold store that any DuckDB client, including a JVM application over JDBC, can query later.

## Why not embed DuckDB in the CLI?

It would be possible to build DuckDB into `dash0` so that the CLI could run this analysis itself. But maybe that's not a good idea.

The Dash0 CLI is built as a static binary with CGO disabled, ships as a `FROM scratch` Docker image with no shell or libc, and cross-compiles to five architectures. Meanwhile, DuckDB is a C++ library, and its Go driver uses CGO. Embedding it would mean giving up static linking, a much larger image, and more complicated cross-compilation.

And, as can be seen above, the pipe already does the job. The CLI produces CSV and JSON, while DuckDB queries it. Neither has to know about the other.

The same applies to any other tool that emits structured output. Once you're used to piping into DuckDB, it's hard to go back to `awk`.

## Summary

From `dash0 login` to a per-service latency breakdown is a few minutes of setup and one pipe.

Because DuckDB reads standard input, files and Parquet as tables, it works as a SQL layer over more or less anything on the command line, and the OpenTelemetry data coming out of the Dash0 CLI is one good use of it.

* [DuckDB documentation](https://duckdb.org/docs/)
* [DuckDB JDBC driver](https://duckdb.org/docs/api/java)
* [Dash0 CLI on GitHub](https://github.com/dash0hq/dash0-cli)
* [OpenTelemetry semantic conventions](https://opentelemetry.io/docs/specs/semconv/)
