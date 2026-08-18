---
title: "DuckDB in Spring Batch: Replace In-Memory Java Loops with One SQL Statement"
date: "2026-08-11T13:34:43+00:00"
lastmod: "2026-08-11T13:52:15+00:00"
description: "Spring Batch jobs usually follow the same pattern: an ItemReader streams rows, an ItemProcessor transforms each one, and an ItemWriter writes them out, - by Geertjan Wielenga"
authors:
  - "geertjan-wielenga"
image: "Favicon-3-2.png"
categories:
  - "Databases"
  - "Performance"
  - "Spring"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Spring Batch jobs usually follow the same pattern: an `ItemReader` streams rows, an `ItemProcessor` transforms each one, and an `ItemWriter` writes them out, chunk by chunk. A chunk-oriented step wires those three pieces together:

```
new StepBuilder("transform", jobRepository)
        .<Order, Summary>chunk(1_000, transactionManager)
        .reader(reader)       // stream rows
        .processor(processor) // transform each row
        .writer(writer)       // write the chunk
        .build();
```


*The chunk-oriented processing model ([Spring Batch reference](https://docs.spring.io/spring-batch/reference/step/chunk-oriented-processing.html)).*

That pattern works well for moving records between systems.

But much batch work is not about moving data, it is about transforming it: grouping, aggregating, and deriving values. For that kind of work, processing one row at a time becomes the bottleneck. Each row is parsed, boxed, hashed, and garbage-collected on a single thread, and over millions of rows that cost adds up.

[DuckDB](https://duckdb.org/) is an embedded, in-process analytical database, similar in spirit to SQLite but built for analytics. It is a single JDBC dependency, needs no server, and runs a vectorized, multi-core query engine inside the JVM. That makes it a good fit for the transformation step of a batch job. To measure the difference, I built two Spring Boot and Spring Batch applications that run the same job over the same data and differ only in the engine that does the transform.

The setup
---------

Both apps generate a deterministic `orders.csv` (`id, customer_id, category, quantity, amount``) and compute, per ``(customer_id, category)` group: the order count, `sum(amount * quantity)`, `sum(quantity)`, `avg(amount)`, and `max(amount * quantity)`. The [generator](https://github.com/geertjanw/duckdb-samples/blob/02dcc32275ee1602d9592ed0384b63a11936c8f4/batch-scenarios/spring-batch-java-demo/src/main/java/com/example/batchjava/OrdersCsvGenerator.java#L23-L48) has no randomness, so the inputs are byte-identical and the outputs can be compared directly.

The traditional application uses a `Tasklet` that loops over the rows and folds them into an in-memory `HashMap`:

```
Map<Long, Acc> groups = new HashMap<>();
while ((line = reader.readLine()) != null) {
    // parse id,customer_id,category,quantity,amount
    long key = customerId * 8 + category;
    Acc acc = groups.computeIfAbsent(key, k -> new Acc());
    acc.count++;
    acc.totalRevenue += amount * quantity;
    // ...
}
```


*Source: [`JavaTransformTasklet.java`](https://github.com/geertjanw/duckdb-samples/blob/02dcc32275ee1602d9592ed0384b63a11936c8f4/batch-scenarios/spring-batch-java-demo/src/main/java/com/example/batchjava/JavaTransformTasklet.java#L54-L83)*

The DuckDB application runs the same transformation as a single SQL statement, inside an equally small `Tasklet`:

```
try (Connection c = DriverManager.getConnection("jdbc:duckdb:");
     Statement st = c.createStatement()) {
    st.execute("""
        COPY (
          SELECT customer_id, category,
                 count(*)                AS order_count,
                 sum(amount * quantity)  AS total_revenue,
                 sum(quantity)           AS total_quantity,
                 avg(amount)             AS avg_amount,
                 max(amount * quantity)  AS max_revenue
          FROM read_csv('orders.csv', header = true)
          GROUP BY customer_id, category
          ORDER BY customer_id, category
        ) TO 'summary.csv' (FORMAT CSV, HEADER true)
        """);
}
```


*Source: [`DuckDbTransformTasklet.java`](https://github.com/geertjanw/duckdb-samples/blob/02dcc32275ee1602d9592ed0384b63a11936c8f4/batch-scenarios/spring-batch-duckdb-demo/src/main/java/com/example/batchduckdb/DuckDbTransformTasklet.java#L46-L73)*

That is the entire transform. There is no reader, processor, writer, or chunk size to configure; DuckDB reads, groups, and writes in a single pass.

Both are still real Spring Batch jobs: a `Job` with a generate step and a transform step ([`BatchConfig.java`](https://github.com/geertjanw/duckdb-samples/blob/02dcc32275ee1602d9592ed0384b63a11936c8f4/batch-scenarios/spring-batch-java-demo/src/main/java/com/example/batchjava/BatchConfig.java#L44-L67)), using H2 for the `JobRepository`. Only the transform step changes.

The results
-----------

Timing only the transform step, on an Apple Silicon machine (12 threads, Java 21, DuckDB 1.5.5), with a fresh JVM each run so the Java figure includes the JIT warm-up that a one-shot batch job actually incurs:

|    Rows    | Java (in-memory collections) | DuckDB (vectorized) | Speedup |
|------------|------------------------------|---------------------|---------|
| 10,000,000 | 1.00 s                       | 0.17 s              | \~6×    |
| 50,000,000 | 4.83 s                       | 0.61 s              | \~8×    |

The two summary files are byte-identical, verified with `cmp`, so this is the same 8,000-group result computed two ways. The gap grows with scale, and would grow further with a heavier per-row transform.

Why the difference
------------------

Three things explain the difference:

* **Vectorization.** DuckDB processes columns in batches of about 2,048 values, so the CPU stays in tight, branch-predictable, cache-friendly loops. The Java version handles one row at a time through object references.
* **Parallelism.** DuckDB uses every core by default. The hand-written loop is single-threaded, and parallelizing it correctly takes real effort.
* **No per-row object churn.** Parsing, autoboxing keys into `Long`, and allocating accumulators create garbage that the vectorized engine never produces.

This is not a criticism of Spring Batch. Its chunk model is well suited to orchestration, restartability, and I/O. The point is more specifically that the arithmetic inside a transform step does not have to run in your own loop.

When to use it
--------------

Consider DuckDB for the transform step when you are aggregating, joining, or deriving over large volumes, and your source is a file (CSV, Parquet, JSON) or a database DuckDB can read. Keep the classic reader, processor, and writer for row-level enrichment, external calls, or writing to a strict downstream system.

Getting started is simple: add `org.duckdb:duckdb_jdbc`, open a `jdbc:duckdb:` connection, and run SQL. It is a single dependency and runs entirely in-process, with no extra infrastructure to operate.

For transformation-heavy steps, letting DuckDB do the work can be far faster than iterating over the rows in Java.

*Full runnable code (both apps, the generator, and the benchmark harness) lives in the [`batch-scenarios`](https://github.com/geertjanw/duckdb-samples/tree/main/batch-scenarios) folder of the duckdb-samples repository.*
