---
title: "Performance Best Practise No. 1: Optimize Database Operations"
slug: "performance-best-practise-no-1-optimize-database-operations"
date: "2025-06-11T13:24:32+00:00"
lastmod: "2025-06-11T13:24:35+00:00"
description: "Boost Jakarta EE database performance by leveraging the following best practices."
canonical: "https://omnifish.ee/performance-best-practice-no-1-optimize-database-operations/"
authors:
  - "ondro-mihalyi"
image: "https://foojay.io/wp-content/uploads/2025/05/pool.png"
categories:
  - "Databases"
  - "Jakarta EE"
  - "Java"
tags:
related_posts:
  - "a-case-for-databases-on-kubernetes-from-a-former-skeptic"
  - "a-dissection-of-java-jdbc-to-postgresql-connections"
  - "a-dissection-of-java-jdbc-to-postgresql-connections-part-2-batching"
  - "a-list-of-cache-providers"
enlighterjs: true
frozen: false
---

Database operations are a very critical part of most applications in regards of performance. There are multiple reasons why database operations can significantly contribute to lower performance:

* The database often runs on a remote server, slowing down communication with the database and the data transfer
* Establishing individual connections to a database can take a significant portion of time compared to running the whole database query
* Database queries can run for a long time
* Network communication is unstable and may required restarting queries in case of network failures

You can address the above issues and boost Jakarta EE database performance by leveraging the following best practices.

* Adjust connection pool sizes to align with workload requirements
  * 🛈 **Tip:** Thread pool max size should be usually bigger than connection pool max size.
  * 🛈 **Tip:** Connection pool max size should reflect the maximum number of connections allowed by the database.
  * 🛈 **Tip:** Connection idle timeout (time after which unused connections are closed) should be shorter than on the database side to avoid reusing stale connections if the database already closed them.
* Use **Prepared Statements** and reuse them when calling the same query to avoid repetitive SQL parsing
  * 🛈 **Tip:** When using Jakarta Persistence (JPA) queries, prepared statements are used automatically by the persistence provider
* Implement statement caching to cut down on SQL parsing overhead. This is another way of reusing previous statements for the same query
* Enable **JDBC batching** or **JPA batching** to handle multiple SQL operations efficiently

> "Connection establishment is the most expensive database operation; the obvious optimization that Java developers have been using for ages is connection pooling which avoids creating connections at runtime (unless you exhaust the pool capacity)." -- Kuassi Mensah [^\[1\]^](https://medium.com/oracledevs/revisiting-java-applications-performance-scalability-with-rdbms-68d9f85466ca)

How GlassFish helps with improving database performance {#how-glassfish-helps-with-improving-database-performance}
------------------------------------------------------------------------------------------------------------------

[GlassFish](https://omnifish.ee/) provides built-in connection pools. If an application uses a datasource defined in GlassFish, connections are managed by GlassFish via the built-in connection pooling mechanism and can be configured externally, outside of your application, via GlassFish Admin Console or admin commands.

JDBC statement caching involves storing frequently executed SQL statements, such as `Statement`, `PreparedStatement`, and `CallableStatement`, in a cache, so that they can be reused later if the same request is executed again. Instead of preparing an SQL statement from scratch every time it needs to be executed, an already prepared statement is used if it's stored in the cache. While statement caching is often a built-in feature of JDBC drivers, GlassFish provides its own statement caching mechanism that can be used even with JDBC drivers that do not have native support for it[^\[2\]^](https://glassfish.org/docs/latest/administration-guide.html#statement-caching).

### Connection pool configuration {#connection-pool-configuration}

You can configure connection pools in the Admin Console as follows:
![](/images/posts/2025/06/performance-best-practise-no-1-optimize-database-operations/blog-conn-pool-size-idle.png) ![](/images/posts/2025/06/performance-best-practise-no-1-optimize-database-operations/image-1.png)

Or set the following properties (using the `set` admin command or as Embedded GlassFish properties):

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">resources.jdbc-connection-pool.{CONNECTION_POOL_NAME}.max-pool-size=32
resources.jdbc-connection-pool.{CONNECTION_POOL_NAME}.idle-timeout-in-seconds=300
resources.jdbc-connection-pool.{CONNECTION_POOL_NAME}.statement-cache-size=10</pre>

If you define the datasource in an application in web.xml:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;data-source&gt;
  &lt;name&gt;java:app/jdbc/MyDatasource&lt;/name&gt;
  &lt;max-pool-size&gt;32&lt;/max-pool-size&gt;
  &lt;max-idle-time&gt;300&lt;/max-idle-time&gt;
  &lt;max-statements&gt;10&lt;/max-statements&gt;
&lt;/data-source&gt;</pre>

Or using in an application using an annotation:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@DataSourceDefinition(
    name = "java:app/jdbc/MyDatasource",
    maxPoolSize = 32,
    maxIdleTime = 300,
    maxStatements = 10
)</pre>

### JDBC batching {#jdbc-batching}

JDBC batching is a feature in Java Database Connectivity (JDBC) that allows you to group multiple SQL statements together and send them to the database in a single request. In other words, the batched statements will not be executed immediately when they are submitted, but will be submitted together in a single request after the last statement is submitted.

Here's an example, how to execute multiple JDBC statements in a batch:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Statement statement = connection.createStatement();
statement.addBatch("INSERT INTO CITIES(ID, NAME, COUNTRY) "
 + "VALUES ('1','Zagreb','Croatia')");
statement.addBatch("INSERT INTO CITIES(ID, NAME, COUNTRY) "
 + "VALUES ('2','Dublin', 'Ireland')");
statement.executeBatch();</pre>

### Jakarta Persistence (JPA) batching {#jakarta-persistence-jpa-batching}

With JPA (Jakarta Persistence), a batching behavior can be enabled via [EclipseLink extensions for JPA](https://eclipse.dev/eclipselink/documentation/4.0/solutions/solutions.html#CHDHDFAD). If your applications use Hibernate or another JPA provider, these wouldn't be available to you. In that case, check the documentation of your provider if it supports similar extensions.

To enable the batching behavior, configure the following JPA properties (for example in `persistence.xml` or when creating an `EntityManager`):

* [eclipselink.jdbc.batch-writing](https://eclipse.dev/eclipselink/documentation/4.0/jpa/extensions/jpa-extensions.html#jdbcbatchwriting) with the value `jdbc` (to use JDBC batching) under the hood) or `buffered` (to use batching provided by EclipseLink). EclipseLink also provides `oracle-jdbc` option works only with Oracle DB JDBC driver, and `custom-class`, which allows providing a completely custom mechanism
* Optionally, specify the [jdbc.batch-writing.size](https://eclipse.dev/eclipselink/documentation/4.0/jpa/extensions/jpa-extensions.html#jdbc-batch-writing-size) property with the size of the batch (number of the statements in a single batch)

For example, in `persistence.xml`:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;property name="eclipselink.jdbc.batch-writing" value="jdbc"/&gt;
&lt;property name="eclipselink.jdbc.batch-writing.size" value="150"/&gt;</pre>

This will enable batching for all data-writing JPA operations. If you want to invoke a certain statement in a JPA query without the batching behavior, you can then disable batching with the [jdbc.batch-writing](https://eclipse.dev/eclipselink/documentation/4.0/jpa/extensions/jpa-extensions.html#jdbc-batch-writing) query hint.

For example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">query.setHint("jdbc.batch-writing", false);</pre>

Or, if you compile your application against EclipseLink API:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;
...
query.setHint(QueryHints.BATCH_WRITING, HintValues.FALSE);</pre>

### Next Steps {#statement-caching-with-glassfish}

To put these ideas into action, you should think about how your application works with the database, what are your database settings, and how much load (parallel queries and connections) your database can handle.

Then prepare a tuned configuration, test how it works, and always monitor performance to see if there are any bottlenecks or space to tune the configuration even further.

In this case, it's worth monitoring the amount of database connections, how many of them are being actively used, average length of query execution, etc.

In the next article, we'll go into more details about **implementing caching mechanisms** to avoid wasting time with repetitive tasks that provide the same output. [So stay tuned...](https://omnifish.ee/)
