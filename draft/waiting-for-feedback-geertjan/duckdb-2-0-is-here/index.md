---
title: "DuckDB 2.0 Is Here"
date: "2026-01-01"
description: "DuckDB 2.0.0 LTS \"Cinnamon Teal\" is the project's biggest release yet, with a server mode, async I/O, a native VARIANT type and a new parser."
authors:
  - "geertjan-wielenga"
image: "duckdb-2-0.jpg"
categories:
  - "Databases"
  - "Java"
related_posts:
  - "one-database-two-models-mysql-json-duality-views-java"
  - "announcing-the-new-foojay"
  - "asciidoc-support-on-foojay"
  - "diagrams-as-code-mermaid-support-on-foojay"
---

[DuckDB](https://duckdb.org/) 2.0.0, nicknamed "Cinnamon Teal", shipped on
21 October 2026. The team calls it their biggest feature release so far, built
from close to 20,000 commits since 1.5. It is a Long Term Support release,
supported until at least October 2027.

If you haven't used it before, DuckDB is an in process analytical database, a
bit like SQLite but built for columnar analytics. It stores data in its own
format, reads Parquet, CSV and JSON directly, queries tables that live in
PostgreSQL, MySQL and SQLite, and works with lake formats such as Iceberg and
Delta, whether the data sits on local disk or in object storage. By default it
runs inside your application rather than as a separate server, though 2.0 now
lets you run it as a server too, as described below.

## What is new in 2.0

- **DuckDB as a server.** The Quack wire protocol is now ready for production,
  and a new `CONNECT` statement attaches and switches to a remote database in a
  single command. A remote pushdown optimizer sends work down to PostgreSQL and
  MySQL, and the `-serve` and `-connect` flags start or join a server straight
  from the CLI.
- **Asynchronous I/O.** Parquet, CSV and native storage now read asynchronously,
  which pays off most against object storage such as `s3://`.
- **A native `VARIANT` type.** Nested, flexible data gets "shredded" execution,
  a family of `variant_*` functions, and Parquet read and write support.
- **A new PEG parser, now the default.** It replaces the old YACC/Bison parser
  inherited from PostgreSQL, gives precise error locations, and lets extensions
  hook into the grammar. If something trips over it, `SET enable_legacy_parser
  = true` falls back.
- **More SQL.** `CREATE TRIGGER`, CTEs that modify data, `NEAREST` joins, nested
  schemas, JSON mutation functions and `FETCH FIRST` all arrive in this release.

The CLI also gains an [agent mode](https://duckdb.org/2026/10/09/agent-mode.html)
that detects coding agents and emits compact Markdown and JSON errors.

## Using DuckDB from Java

DuckDB ships a JDBC driver, so from Java you add one dependency and query it
like any other database. There is no server to run.

With Maven, add this to your `pom.xml`:

```xml
<dependency>
    <groupId>org.duckdb</groupId>
    <artifactId>duckdb_jdbc</artifactId>
    <version>2.0.0</version>
</dependency>
```

With Gradle, add this to your `build.gradle`:

```groovy
implementation 'org.duckdb:duckdb_jdbc:2.0.0'
```

Then connect and query:

```java
import java.sql.*;

try (Connection conn = DriverManager.getConnection("jdbc:duckdb:");
     Statement stmt = conn.createStatement();
     ResultSet rs = stmt.executeQuery(
         "SELECT count(*) FROM read_parquet('data/*.parquet')")) {
    rs.next();
    System.out.println(rs.getLong(1) + " rows");
}
```

An empty JDBC URL gives you an in memory database. Point it at a file to persist
instead. With the async Parquet and CSV readers in 2.0, a query over data on S3
runs faster than in earlier releases.

## Converting between formats

One of the handiest things you can do from Java is convert a file from one
format to another. DuckDB reads the input with `read_csv`, `read_json` or
`read_parquet` and writes the output with a `COPY` statement, so a conversion is
a single SQL statement with no row-by-row Java code in between.

```java
import java.sql.*;

try (Connection conn = DriverManager.getConnection("jdbc:duckdb:");
     Statement stmt = conn.createStatement()) {

    // CSV to Parquet
    stmt.execute("COPY (SELECT * FROM read_csv('people.csv')) "
            + "TO 'people.parquet' (FORMAT parquet)");

    // Parquet to JSON
    stmt.execute("COPY (SELECT * FROM read_parquet('people.parquet')) "
            + "TO 'people.json' (FORMAT json)");

    // JSON to CSV
    stmt.execute("COPY (SELECT * FROM read_json('people.json')) "
            + "TO 'people.csv' (FORMAT csv, HEADER)");
}
```

Because every format goes through the same reader and writer, any pair works the
same way. The column names and types carry across on their own, and the file can
sit on local disk or on object storage such as `s3://`.

## Upgrading

2.0 reads and writes older database files, and those stay compatible with older
versions. A file created by 2.0, though, cannot be opened by anything earlier,
so plan that step. The build now needs a C++17 compiler. 

The DuckDB team suggests testing on a shadow
system first, and waiting for the first patch release for critical workloads.

The [DuckDB migration guide](https://duckdb.org/docs/2.0/operations_manual/migration.html)
walks through each breaking change in full. The
[2.0.0 announcement](https://duckdb.org/2026/10/21/announcing-duckdb-200.html)
covers everything else that is new.
