---
title: "Type-Safe SQL with jOOQ and Embedded Analytics with DuckDB"
date: "2026-11-30"
description: "Combine jOOQ's type-safe SQL builder with DuckDB's analytical engine for powerful Java data applications."
authors:
  - "geertjan-wielenga"
categories:
  - "Databases"
related_posts:
  - "embedding-duckdb-in-a-maven-app-and-using-it-for-things-that-arent-databases"
  - "duckdb-in-spring-batch-replace-in-memory-java-loops-with-one-sql-statement"
  - "5-great-reasons-to-use-jooq"
  - "vaadin-and-jooq-match-made-in-heaven"
image: "jooq-duckdb.jpg"
---

## Why jOOQ and DuckDB Together

[jOOQ](https://www.jooq.org/) is great at generating type-safe SQL queries in Java, while [DuckDB](https://duckdb.org/) provides an embedded, analytical SQL engine that's perfect for data processing tasks. Together, they are a powerful combination for building data-driven Java applications without the overhead of traditional databases.

## Getting Started with No Build Tool

The fastest way to try jOOQ with DuckDB requires only Java 21+ and `curl`:

```bash
mkdir jooq-duckdb
cd jooq-duckdb
mkdir lib
cd lib
curl -O https://repo1.maven.org/maven2/org/jooq/jooq/3.21.9/jooq-3.21.9.jar
curl -O https://repo1.maven.org/maven2/io/r2dbc/r2dbc-spi/1.0.0.RELEASE/r2dbc-spi-1.0.0.RELEASE.jar
curl -O https://repo1.maven.org/maven2/org/reactivestreams/reactive-streams/1.0.4/reactive-streams-1.0.4.jar
curl -O https://repo1.maven.org/maven2/org/duckdb/duckdb_jdbc/2.0.0.1/duckdb_jdbc-2.0.0.1.jar
curl -O https://repo1.maven.org/maven2/jakarta/xml/bind/jakarta.xml.bind-api/4.0.2/jakarta.xml.bind-api-4.0.2.jar
cd ..
```

The first jar is jOOQ itself, and the next two are its runtime dependencies. The DuckDB jar contains both the database engine and its JDBC driver. 

The last one, the JAXB API, isn't needed at runtime. Without it, though, the compiler prints a harmless `unknown enum constant XmlAccessType.FIELD` warning, because some jOOQ classes carry JAXB annotations.

Or with Maven, which pulls in jOOQ's dependencies for you:

```xml
<dependency>
    <groupId>org.jooq</groupId>
    <artifactId>jooq</artifactId>
    <version>3.21.9</version>
</dependency>
<dependency>
    <groupId>org.duckdb</groupId>
    <artifactId>duckdb_jdbc</artifactId>
    <version>2.0.0.1</version>
</dependency>
```

## Example: Build Queries with the jOOQ DSL

Save this as `Demo.java` in your project root:

```java
import java.sql.Connection;
import java.sql.DriverManager;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import static org.jooq.impl.DSL.*;

public class Demo {
    public static void main(String[] args) throws Exception {
        try (Connection conn = DriverManager.getConnection("jdbc:duckdb:")) {
            DSLContext ctx = DSL.using(conn, SQLDialect.DUCKDB);

            // Create and populate a table
            ctx.execute("CREATE TABLE people (name VARCHAR, city VARCHAR, age INT)");
            ctx.insertInto(table("people"), field("name"), field("city"), field("age"))
               .values("Ann", "Amsterdam", 34)
               .values("Bob", "Utrecht", 41)
               .values("Cas", "Amsterdam", 29)
               .execute();

            // Query with jOOQ's DSL
            var result = ctx
                .select(field("city"), avg(field("age", Integer.class)).as("avg_age"))
                .from(table("people"))
                .groupBy(field("city"))
                .orderBy(field("city"))
                .fetch();

            System.out.println(result);
            System.out.println("DuckDB " + ctx.fetchValue("SELECT version()"));
        }
    }
}
```

Run it:

```bash
# macOS or Linux
java -cp "lib/*" Demo.java

# Windows
java -cp "lib\*" Demo.java
```

Output:

```
+---------+-------+
|city     |avg_age|
+---------+-------+
|Amsterdam|   31.5|
|Utrecht  |   41.0|
+---------+-------+
DuckDB v2.0.0
```

jOOQ prints a logo and a tip at startup. To turn them off, add `-Dorg.jooq.no-logo=true -Dorg.jooq.no-tips=true` before `-cp`.

## From Plain SQL to Full Type Safety

The example uses jOOQ's plain-SQL API: `table("people")` and `field("city")` are strings. The query structure is checked by the compiler, but table and column names aren't, so a typo like `field("cty")` only fails at runtime. That's the quickest way to start, and it's the natural fit for DuckDB table functions such as `read_parquet`.

For full type safety, run jOOQ's code generator against a DuckDB file database (`jdbc:duckdb:demo.db`). It generates a Java class for each table, so `field("city")` becomes `PEOPLE.CITY`. From then on, a misspelled column or a type mismatch is a compile error.

## Key Benefits

**Type Safety**: jOOQ's DSL checks your query structure at compile time. With generated code, it checks table names, column names and types too.

**Embedded Analytics**: DuckDB provides OLAP capabilities without managing a separate database server.

**Simplified Data Processing**: Chain operations naturally in Java while leveraging SQL's power.

## Query Files Directly

One of DuckDB's superpowers is reading CSV and Parquet files directly without loading them into tables:

```java
System.out.println(
    ctx.select(count())
       .from(table("read_csv_auto('data.csv')"))
       .fetch()
);
```

For Parquet, use `table("read_parquet('data.parquet')")`.

## A Note on DuckDB 2.0

DuckDB 2.0 brings breaking changes from 1.x. Two things to keep in mind:

- DuckDB 2.0 writes a new storage format, and DuckDB 1.x can't read it. A `.db` file created with this setup won't open in 1.x tools.
- jOOQ's DSL may not have methods for the newest 2.0 syntax yet. You can run that syntax through plain SQL with `ctx.execute("…")`, `ctx.fetch("…")` or `table("…")`. See the [DuckDB migration guide](https://duckdb.org/docs/2.0/operations_manual/migration) for full compatibility details.

## Conclusion

jOOQ and DuckDB solve different parts of the data processing problem. jOOQ brings compile-time safety to your SQL queries, catching errors before they reach production. DuckDB brings analytical power to your JVM without the overhead of managing an external database server. Together, they're a natural fit for ETL pipelines, real-time analytics on application data, and direct querying of CSV and Parquet files—especially in microservices where you want data processing capabilities without external dependencies.

The combination works well whether you're building data transformation tasks, running aggregations on application state, or querying data lakes directly. And, as you can see above, with jOOQ 3.21.9 and DuckDB 2.0, you can get started with just a `curl` command and a single Java file.

## Further Reading

- [jOOQ Manual](https://www.jooq.org/doc/latest/manual/) — Complete documentation on the jOOQ DSL and code generation
- [DuckDB Documentation](https://duckdb.org/docs/) — Full reference for DuckDB SQL and table functions
- [DuckDB JDBC Driver](https://duckdb.org/docs/api/java) — DuckDB's Java integration