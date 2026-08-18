---
title: "Embedding DuckDB in a Maven App (and Using It for Things That Aren't Databases)"
date: "2026-08-06T08:46:28+00:00"
lastmod: "2026-08-11T13:52:51+00:00"
description: "DuckDB is described as \"SQLite for analytics,\" which is true: it's an in-process database engine that runs inside your application, with no server to - by Geertjan Wielenga"
authors:
  - "geertjan-wielenga"
image: "Favicon-3-2.png"
categories:
  - "Databases"
  - "Java"
  - "Performance"
tags:
related_posts:
frozen: false
---

[DuckDB](https://duckdb.org/) is described as "SQLite for analytics," which is true: it's an in-process database engine that runs inside your application, with no server to install or manage. What's less obvious from that description is that **you can get value out of it without ever creating a database at all**. Because it can query CSV, JSON, and Parquet files directly --- local or over HTTP --- it works perfectly well as an embedded data-crunching library that happens to speak SQL.

This article covers getting it into a Maven project and using it that way.

### Setup

One dependency. The native engine is bundled inside the JAR, so there's nothing else to install:

```
<dependency>
   <groupId>org.duckdb</groupId>
   <artifactId>duckdb_jdbc</artifactId>
   <version>1.5.5.0</version>
</dependency>
```

It exposes a standard JDBC interface, so from the point of view of anyone who's ever written Java database code before, there's no new API to learn. The connection string `jdbc:duckdb:` (with nothing after the colon) gives you a purely in-memory instance --- nothing is written to disk, and everything disappears when the connection closes.

### Querying a file on the internet with SQL

Here's a complete Java application. It runs an aggregation [over a CSV file hosted on GitHub](https://raw.githubusercontent.com/allisonhorst/palmerpenguins/main/inst/extdata/penguins_raw.csv) --- no download step, no schema definition, no table creation:

```
import java.sql.*;

public class DuckDB {

    public static void main(String[] args) throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:duckdb:"); Statement s = c.createStatement()) {
            ResultSet rs = s.executeQuery("""
       SELECT Species, count(*) AS n,
       round(avg(TRY_CAST("Body Mass (g)" AS DOUBLE)), 1) AS avg_mass        
       FROM 'https://raw.githubusercontent.com/allisonhorst/palmerpenguins/main/inst/extdata/penguins_raw.csv'
       GROUP BY Species ORDER BY n DESC""");
            while (rs.next()) {
                System.out.printf("%-45s %4d %8.1f%n",
                        rs.getString(1), rs.getInt(2), rs.getDouble(3));
            }
        }
    }
}
```

And here it is again with line-by-line explanations:

```
// java.sql.* brings in Connection, Statement, ResultSet
// and DriverManager. DuckDB is driven through plain JDBC,
// so there are no DuckDB-specific imports.
import java.sql.*;

public class DuckDB {

    // throws Exception keeps the example short: both
    // getConnection and executeQuery throw SQLException.
    public static void main(String[] args) throws Exception {

        // "jdbc:duckdb:" with nothing after the colon gives
        // a fresh in-memory instance. Add a path, as in
        // "jdbc:duckdb:/tmp/my.db", to persist to a file.
        // try-with-resources closes both objects at the end.
        try (Connection c =
                     DriverManager.getConnection("jdbc:duckdb:");

             // A Statement is the handle you execute SQL
             // through. Use PreparedStatement instead if any
             // part of the query comes from user input.
             Statement s = c.createStatement()) {

            // executeQuery runs the SQL and returns a cursor
            // over the results.
            //
            // The query below, clause by clause:
            //
            //   SELECT Species, count(*) AS n
            //     Species is a column in the CSV; count(*)
            //     counts the rows in each group.
            //
            //   round(avg(TRY_CAST(...)), 1) AS avg_mass
            //     TRY_CAST converts text to DOUBLE but yields
            //     NULL instead of erroring on bad values, so
            //     one malformed row won't kill the query.
            //     avg() skips those NULLs, and round(..., 1)
            //     trims to one decimal place. The column name
            //     is quoted because it contains spaces and
            //     parentheses.
            //
            //   FROM '<url>'
            //     The URL sits where a table name normally
            //     goes. DuckDB fetches the file, sniffs the
            //     delimiter and column types, and queries it
            //     in place. A local path or a glob such as
            //     'data/*.parquet' works identically.
            //
            //   GROUP BY Species ORDER BY n DESC
            //     One row per species, most numerous first.
            ResultSet rs = s.executeQuery("""
                    SELECT Species, count(*) AS n,
                    round(avg(TRY_CAST("Body Mass (g)" AS DOUBLE)), 1)
                        AS avg_mass
                    FROM 'https://raw.githubusercontent.com/allisonhorst/palmerpenguins/main/inst/extdata/penguins_raw.csv'
                    GROUP BY Species ORDER BY n DESC""");

            // next() advances the cursor and returns false
            // once the rows run out.
            while (rs.next()) {

                // %-45s left-pads the species name to 45
                // chars; %4d and %8.1f right-align the count
                // and the average; %n is the line separator.
                System.out.printf("%-45s %4d %8.1f%n",

                        // JDBC columns are 1-indexed and match
                        // the SELECT list: 1 = Species,
                        // 2 = n, 3 = avg_mass.
                        rs.getString(1), rs.getInt(2),
                        rs.getDouble(3));
            }
        } // Both resources close here, and the in-memory
          // database ceases to exist.
    }
}
```

### Querying a file on the internet with SQL

The interesting part is the `FROM` clause. DuckDB treats the URL as a table: it fetches the file, sniffs the delimiter and column types, and runs the query against it. Swap in a local path and it works the same way. Globs work too --- `FROM 'logs/*.parquet'` queries a whole directory of Parquet files as one dataset.

For anything beyond plain HTTPS (S3, for instance), you'll need the httpfs extension first:

```
s.execute("INSTALL httpfs; LOAD httpfs;");
```

The query above returns one row per penguin species from the 344-row raw dataset, ordered by how many observations each has: Adelie leads with 152, followed by Gentoo at 124 and Chinstrap at 68 (a couple of rows have no recorded mass, which is what `TRY_CAST` above is handling):

```
Adelie Penguin (Pygoscelis adeliae)            152   3700.7
Gentoo penguin (Pygoscelis papua)              124   5076.0
Chinstrap penguin (Pygoscelis antarctica)       68   3733.1
```

### Where this is actually useful

None of this replaces your application database, and it isn't meant to.

Where it makes sense:

* **Replacing hand-rolled CSV parsing.** If your app ingests CSV or JSON files, you've probably written parsing code, type-coercion code, and then loops to filter and group the results. A DuckDB query does all three in one step, and its CSV reader handles the messy edge cases (quoting, encodings, ragged rows) better than most homegrown parsers.

<!-- -->

* **In-memory aggregation of data you already have.** Anywhere you'd write nested loops with `HashMap<String, List<...>>` to group and summarize objects, you can often dump the data through DuckDB and express the logic as SQL. Whether that's clearer depends on the logic and on your team --- for a three-line group-by it's arguably overkill; for anything resembling a pivot or window function it usually wins.

<!-- -->

* **Generating test data.** `SELECT * FROM generate_series(1, 100_000_000)` materializes and aggregates a hundred million rows in about a second on a laptop. Handy for load-testing downstream code without fixture files.

<!-- -->

* **Reading Parquet from plain Java.** The usual route to Parquet in Java involves a slice of the Hadoop ecosystem. This is one dependency.

### Summing up

The story isn't that DuckDB is magic --- it's that **a columnar engine is now a Maven dependency away** , and it's willing to **treat any file (or URL) as a table**.

If your Java code contains a parsing-and-aggregating section you've never much liked, it might be a few lines of SQL instead.
