---
title: "5 Great Reasons to use jOOQ"
date: "2023-03-22T14:45:13+00:00"
lastmod: "2023-03-22T14:45:52+00:00"
description: "jOOQ makes SQL a \"first-class\" language in the JVM ecosystem by embedding it into Java, Kotlin, and Scala in an idiomatic way."
authors:
  - "lukas-eder"
image: "jooq.png"
categories:
  - "Databases"
  - "jOOQ"
  - "sql"
  - "Tutorials"
  - "Use Cases"
related_posts:
  - "vaadin-and-jooq-match-made-in-heaven"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "7-reasons-to-switch-to-openjdk-17-as-a-jakarta-ee-developer"
frozen: false
---

***Hi, I'm [Lukas](https://twitter.com/lukaseder "Lukas"). I was invited to talk about the business behind jOOQ on Foojay.io and as a short introduction to [jOOQ](https://www.jooq.org/ "jOOQ"), I'd like to highlight 5 great reasons to use it.***

Before discussing those reasons:

## What is jOOQ

jOOQ is an internal [domain-specific language](https://en.wikipedia.org/wiki/Domain-specific_language "domain-specific language ") (DSL) modelling the SQL language as an API directly in Java.

This provides compile-time type safety to your query and enables a lot of other interesting features that I'll show later.

In short, if your SQL query looks like this:

```sql
SELECT author.first_name, author.last_name
FROM author
WHERE author.last_name = 'Shakespeare'
ORDER BY author.first_name, author.last_name
LIMIT 10
```

...then your equivalent jOOQ query looks like this:

```java
ctx.select(AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
   .from(AUTHOR)
   .where(AUTHOR.LAST_NAME.eq("Shakespeare"))
   .orderBy(AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
   .limit(10)
   .fetch();
```

So, there's (almost always) a 1:1 equivalence between SQL syntax and the jOOQ API, irrespective of whether you're coding in Java, Kotlin, or Scala.

## Great, so why should I use it?

It has always been easy to write SQL in Java with JDBC.

Things have gotten even simpler since Java 15's introduction of text blocks, so why use an internal DSL instead of the external one, that is, SQL?

Here are 5 great reasons to use jOOQ rather than native SQL:

### 1. Type safety

The DSL itself is type-safe, meaning you can't write illegal SQL as easily as with String-based SQL. But using [jOOQ's code generator](https://blog.jooq.org/why-you-should-use-jooq-with-code-generation/ "jOOQ's code generator"), you have access to your entire database catalog directly as Java objects. This extends to:

* IDE auto-completion (even for dynamic SQL, where the IDE can't help).
* Compile-time checking (where the IDE also can't help).
* Documentation of the schema.

In the above intro, we've seen objects like `AUTHOR` or `AUTHOR.LAST_NAME`. These are generated classes, members, methods, etc., which the Java compiler knows and can type-check.

For example, the `AUTHOR.LAST_NAME` member is of type [Field<String>](https://www.jooq.org/javadoc/latest/org.jooq/org/jooq/Field.html) meaning that the resulting record will be aware of its contents being a `String`. Not just that, but the predicate from the query is type-safe as well!

```java
// This compiles:
AUTHOR.LAST_NAME.eq("Shakespeare")

// This does not:
AUTHOR.LAST_NAME.eq(1)
```

SQL is a type-safe language itself. When you write a view or procedure in your database, the database's compiler will type-check those objects, as well. But this luxury goes away as soon as Java developers write their SQL in String form using JDBC or other libraries that work with SQL strings.

And not just SQL queries are affected. [jOOQ's code generator also generates stubs for your stored procedures](https://blog.jooq.org/the-best-way-to-call-stored-procedures-from-java-with-jooq/ "jOOQ's code generator also generates stubs for your stored procedures"). If you have a procedure like this:

```sql
CREATE OR REPLACE PROCEDURE my_proc (
  i1 NUMBER,
  io1 IN OUT NUMBER,
  o1 OUT NUMBER,
  o2 OUT NUMBER,
  io2 IN OUT NUMBER,
  i2 NUMBER
) IS
BEGIN
  o1 := io1;
  io1 := i1;

  o2 := io2;
  io2 := i2;
END my_proc;
```

You can call it conveniently like this:

```java
MyProc result = Routines.myProc(configuration, 1, 2, 5, 6);

System.out.println("io1 = " + result.getIo1());
System.out.println("o1 = " + result.getO1());
System.out.println("o2 = " + result.getO2());
System.out.println("io2 = " + result.getIo2());
```

Using code generation and the type-safe API, it feels as if the database is a part of your Java application, which helps speed up development, just as much as it helps prevent errors as your Java code stops compiling as soon as you modify objects in your database.
> **Note** you don't have to use the DSL API to get access to many of the remaining benefits. For example, jOOQ also has a [parser](https://www.jooq.org/doc/latest/manual/sql-building/sql-parser/ "parser") that can work as a JDBC proxy, e.g. to [lint](https://www.jooq.org/doc/latest/manual/sql-execution/diagnostics/ "lint") or [translate](https://www.jooq.org/translate/ "translate") SQL between dialects, or otherwise [transform your SQL](https://www.jooq.org/doc/latest/manual/sql-building/queryparts/sql-transformation/ "transform your SQL").
>
> Also, there's always the [plain SQL templating](https://www.jooq.org/doc/latest/manual/sql-building/plain-sql-templating/ "plain SQL templating") escape hatch, if you prefer native SQL for some queries or some parts of a query.

### 2. Mapping

A big (and very boring) part of working with SQL is mapping the result set to some representation in application code, be it Java, JSON, XML, etc. jOOQ helps with those things again in a type-safe way. Let's say you're using Java records to represent your data:

```java
record Book (int id, String title) {}
record Name (String firstName, String lastName) {}
record Author (int id, Name name, List<Book> books) {}
```

With jOOQ, you can easily map any level of nested data structures to your Java representation directly in the query, in a type-safe way, like so:

```java
List<Author> authors =
ctx.select(
     AUTHOR.ID,
     row(AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME).mapping(Name::new),
     multiset(
       select(BOOK.ID, BOOK.TITLE)
       .from(BOOK)
       .where(BOOK.AUTHOR_ID.eq(AUTHOR.ID))
     ).convertFrom(r -> r.map(mapping(Book::new)))
   )
   .from(AUTHOR)
   .orderBy(AUTHOR.ID)
   .fetch(mapping(Author::new))
```

That's it! The above example uses a few features:

* [Nested records using the `row()` operator](https://www.jooq.org/doc/latest/manual/sql-building/column-expressions/nested-records/ "Nested records using the row() operator")
* [Nested collections using the `multiset()` operator](https://www.jooq.org/doc/latest/manual/sql-building/column-expressions/multiset-value-constructor/ "Nested collections using the multiset() operator")
* [Ad-hoc converters to turn jOOQ `Result<Record3>` types into your own types in a type safe way.](https://www.jooq.org/doc/latest/manual/sql-execution/fetching/ad-hoc-converter/ "Ad-hoc converters to turn jOOQ Result types into your own types in a type safe way.")

Behind the scenes, in most dialects, some SQL/JSON or SQL/XML query is run in order to be able to nest the records and collections directly in SQL, thus avoiding:

* Costly N+1 problems.
* Complicated mapping algorithms to deduplicate `JOIN` results again.

Neither the JSON serialisation format, nor the nesting imposes any significant performance penalty, [as can be seen in this article](https://blog.jooq.org/the-performance-of-various-to-many-nesting-algorithms/ "as can be seen in this article").

Plus, both the `ROW()` and `MULTISET()` operators are standard SQL operators, so this is really an idiomatic way to work with SQL. [Read more about `MULTISET` here](https://blog.jooq.org/jooq-3-15s-new-multiset-operator-will-change-how-you-think-about-sql/ "Read more about MULTISET here").

### 3. Vendor agnosticity

*If* you have to support multiple RDBMS, then an abstraction like jOOQ is almost inevitable. You can open up any page of the jOOQ manual to see how something as mundane as the [`POSITION()` function](https://www.jooq.org/doc/latest/manual/sql-building/column-expressions/string-functions/position-function/ "POSITION() function") gets translated by jOOQ.

When you write `position("hello", "e")` in jOOQ, then you're getting:

```sql
-- ASE, SQLDATAWAREHOUSE, SQLSERVER
charindex('e', 'hello')

-- AURORA_MYSQL, AURORA_POSTGRES, COCKROACHDB, EXASOL, FIREBIRD, H2, HSQLDB,
-- MARIADB, MEMSQL, MYSQL, POSTGRES, SNOWFLAKE, TERADATA, TRINO, VERTICA,
-- YUGABYTEDB
position('e' IN 'hello')

-- BIGQUERY, ORACLE, SQLITE
instr('hello', 'e')

-- DB2, DERBY
locate('e', 'hello')

-- HANA, SYBASE
locate('hello', 'e')
```

This goes much further when more complex SQL features are involved, such as the `LIMIT` or `FETCH` clause, which may have to be emulated using filters on `ROW_NUMBER()`, `RANK()`, `DENSE_RANK()` or `PERCENT_RANK()` window functions, depending on the specific clause usage. Or, if you want to use SQL/JSON, which is an [ISO/IEC TR 19075:6 standard](https://www.iso.org/standard/78937.html "ISO/IEC TR 19075:6 standard"), yet [hardly anyone implements the standard](https://blog.jooq.org/standard-sql-json-the-sobering-parts/ "hardly anyone implements the standard"). But with jOOQ, you get access to the standard syntax (via Java APIs), and jOOQ handles the translation to the relevant SQL dialect.

Even if you're not supporting multiple RDBMS, this approach will save you having to look up every single function's specifics, plus it works around all the little caveats of the dialect. You wouldn't believe how many there are! (Some examples about dialects not implementing the standard can be found in the above article).
> **Note** that you don't have to use the jOOQ API to get access to vendor agnosticity. If you prefer working with SQL strings, [you can use jOOQ as a JDBC proxy](https://www.jooq.org/doc/latest/manual/sql-execution/parsing-connection/ "you can use jOOQ as a JDBC proxy"), for example.

### 4. Dynamic SQL

Probably the biggest benefit that you get out of the box with jOOQ is that all of your SQL queries are automatically [dynamic SQL queries](https://www.jooq.org/doc/latest/manual/sql-building/dynamic-sql/ "dynamic SQL queries"), even if they don't look like it. The previous query example looks static:

```java
ctx.select(AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
   .from(AUTHOR)
   .where(AUTHOR.LAST_NAME.eq("Shakespeare"))
   .orderBy(AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
   .limit(10)
   .fetch();
```

But you're really building an expression tree behind the scenes that can be constructed dynamically. For example, what if the `WHERE` clause should be dynamic?

You can extract it into a local variable, and conditionally append to it:

```java
Condition condition = AUTHOR.LAST_NAME.eq("Shakespeare");

if (something)
    condition = condition.and(somethingElse);

ctx.select(AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
   .from(AUTHOR)
   .where(condition)
   .orderBy(AUTHOR.FIRST_NAME, AUTHOR.LAST_NAME)
   .limit(10)
   .fetch();
```

jOOQ doesn't know or care how you constructed your query. But it will still type-check and prevent SQL injection, etc. (The value `"Shakespeare"` is automatically turned into a bind value.)

You can transform your SQL also more globally, e.g. using the [`VisitListener`](https://www.jooq.org/javadoc/latest/org.jooq/org/jooq/VisitListener.html "VisitListener") SPI, or using [model API transformations](https://www.jooq.org/doc/latest/manual/sql-building/model-api/ "model API transformations"). This can be useful to implement more sophisticated use-cases, like [row level security](https://blog.jooq.org/implementing-client-side-row-level-security-with-jooq/ "row level security") or multitenancy on all of your SQL queries. (For the latter, [there's an out of the box schema mapping feature for simple cases](https://www.jooq.org/doc/latest/manual/sql-building/dsl-context/custom-settings/settings-render-mapping/ "there's an out of the box schema mapping feature for simple cases")).

Finally, with the expression tree of your query being available programmatically, you can also implement diagnostics, or use [jOOQ's built-in diagnostics](https://www.jooq.org/doc/latest/manual/sql-execution/diagnostics/ "jOOQ's built-in diagnostics"), e.g. to detect N+1 problems.
> **Note** : Again, thanks to jOOQ's parser, you don't *have to* use the jOOQ API to get access to all of this. You can parse any SQL query (e.g. generated by Hibernate) and translate/transform/lint/diagnose it, even if that's not really the primary use-case for jOOQ.

### 5. Everything SQL

Don't take my word on this one, but listen to existing jOOQ users who have told me many times that thanks to jOOQ, they've started appreciating SQL again. Obviously, this is a subjective take, but jOOQ:

* makes SQL "first class," meaning that it encourages people to think like a SQL developer (e.g. set based thinking rather than row-by-row processing);
* makes SQL idiomatic, meaning that it feels like something Java can do; and
* helps users discover SQL features they might not have been aware of, such as [`LATERAL`](https://www.jooq.org/doc/latest/manual/sql-building/table-expressions/joined-tables/join-mode-lateral/ "LATERAL"), [`WITH ORDINALITY`](https://www.jooq.org/doc/latest/manual/sql-building/table-expressions/with-ordinality/ "WITH ORDINALITY"), [`XMLTABLE`](https://www.jooq.org/doc/latest/manual/sql-building/table-expressions/xml-table-function/ "XMLTABLE"), just to name a few (do check out the manual! We keep adding more support for more awesome standard or vendor specific SQL features).

jOOQ's philosophy stems from the fact that, historically, SQL databases tend to have a long and steady lifetime, whereas user-facing software changes much more frequently. Just think of the last 25 years of Java development.

User-facing logic has moved from server (CGI-BIN, J2EE, etc.), to client (JavaScript SPAs, Mobile apps, etc.), back to server (Vaadin, etc.), etc. But during all of these times, SQL databases have gone almost unchallenged, [as this ranking suggests](https://db-engines.com/en/ranking "as this ranking suggests"). This is also due to the fact that the relational model is very sound and normalisation helps design databases for the long run.

So, given the assumption that a database is likely to outlive any user-facing application accessing it, jOOQ embraces a "database first" philosophy, hence the focus on code generation.

Since your schema is "eternal," your application code should have a derived version of it (remember the stored procedure stubs!).

If that's how you think as well, then you'll find jOOQ very productive. If that's not how you think, then you can still use jOOQ for dynamic SQL, SQL transformations, and many other things—jOOQ won't judge.

## Conclusion

jOOQ makes SQL a "first-class" language in the JVM ecosystem by embedding it into Java, Kotlin, and Scala in an idiomatic way, increasing developer productivity in various ways.

jOOQ is dual licensed software (Apache 2.0 and commercial).

[Download it here](https://www.jooq.org/download/), or start playing around with the [demo](https://github.com/jOOQ/demo), to see for yourself.
