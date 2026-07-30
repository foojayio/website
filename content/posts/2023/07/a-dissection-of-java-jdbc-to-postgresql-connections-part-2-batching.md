---
title: "A Dissection of Java JDBC to PostgreSQL, Part 2: Batching"
slug: "a-dissection-of-java-jdbc-to-postgresql-connections-part-2-batching"
date: "2023-07-04T13:56:41+00:00"
lastmod: "2023-07-04T13:56:42+00:00"
description: "Batching for PostgreSQL JDBC is a property of the extended protocol. It uniquely allows you to send multiple statements in a single request."
authors:
  - "frits-hoogland"
image: "https://foojay.io/wp-content/uploads/2023/07/forjdbcfrits.png"
categories:
  - "Databases"
  - "Performance"
  - "sql"
tags:
related_posts:
  - "a-dissection-of-java-jdbc-to-postgresql-connections"
  - "a-list-of-cache-providers"
  - "5-great-reasons-to-use-jooq"
enlighterjs: true
frozen: false
---

This is the second part of a series where I look into how Java JDBC connections to PostgreSQL are working, to understand how to optimally implement them.

If you've landed on this article, it might be a good idea to read [my first article](https://foojay.io/today/a-dissection-of-java-jdbc-to-postgresql-connections/ "my first article") as well, which gives an introduction and shares how JDBC communicates with [PostgreSQL](https://www.yugabyte.com/postgresql/ "PostgreSQL") using simple and extended protocols.

This new article specifically explores batching. Batching for PostgreSQL JDBC is a property of the extended protocol. It uniquely allows you to send multiple statements in a single request.

My [first article](https://foojay.io/today/a-dissection-of-java-jdbc-to-postgresql-connections/ "first article") discussed the simple query protocol that allows multiple statements to be submitted in a single request message. The extended query protocol allows multiple statements, if they are batched, to be submitted in a single request message.

PostgreSQL JDBC supports batching for a standard Statement object, as well as for PreparedStatement objects, using the extended query protocol. A requirement for batching is that the statement has to return the number of affected tuples, rather than returning a ResultSet containing rows/tuples of data. The same requirement of not allowing a ResultSet, but only the number of affected tuples, applies to submitting multiple statements using the simple query protocol.

NOTE: The observations in this article are based on OpenJDK version 17.0.6, JDBC version 42.6.0, and PostgreSQL 15.  

Sample Java test class  

Let's start with the simple test from my first article:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// begin file: simple.java
import java.sql.*;
import java.util.Properties;
//
class Simple
{
    public static void main(String[] args)
    {
        String url = "jdbc:postgresql://PGHOST:PGPORT/DATABASE";
        Properties properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", "postgres");
        properties.setProperty("preferQueryMode", "extended"); // extended is the default
        properties.setProperty("reWriteBatchedInserts", "false"); // false is the default
        // -- 
        try
        (
            Connection connection = DriverManager.getConnection(url, properties);
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery("select now()");
        )
        {
            result.next();
            result.close();
            connection.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        // --
    }
}
// end of file: simple.java</pre>

To build this java executable on the CLI:

Download the PostgreSQL JDBC driver from: <https://jdbc.postgresql.org/download/postgresql-42.6.0.jar> and extract it so that the postgresql-42.6.0.jar file is in the current working directory.  

Compile: `javac -cp postgresql-42.6.0.jar simple.java`  

Run: `java -cp postgresql-42.6.0.jar:. Simple`

Or, you can use an IDE, which will help with some of the work.

Note: you might have to change the PostgreSQL pg_hba.conf file to enable the database listening on the 5432 port if you're testing with PostgreSQL instance installed to look at this.  

Wireshark  

We're going to use Wireshark to see what is going on between the client and database.

For the purpose of this article, the networking layer is the best place to look into the communication internals, by analyzing network requests going back and forth between the client and the database. This is easy using Wireshark running on the client side (where the java class runs) or the server side (where the database runs). Wireshark needs to listen to the source or destination port where the traffic flows to capture the network traffic contents.

This is how you install Wireshark CLI on Red Hat-compatible Linux systems:  

sudo yum install wireshark-cli

To list the network traffic with a PostgreSQL summary, use:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">sudo tshark -i any  -f 'tcp port 5432' -d tcp.port==5432,pgsql
</pre>

To list the network traffic with the PostgreSQL traffic fully decoded, use:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">sudo tshark -i any -f 'tcp port 5432' -d tcp.port==5432,pgsql -O pgsql</pre>

Standard Statement Batching {#h2-0-standard-statement-batching}
---------------------------------------------------------------

Let's use the following code snippet to create or truncate a table, then execute three insert statements in the batched mode:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// --
try
(
    Connection connection = DriverManager.getConnection(url, properties);
    Statement statement = connection.createStatement();
)
{
    statement.execute("create table if not exists test(id int primary key, f1 text)");
    statement.execute("truncate table test");

    statement.addBatch("insert into test (id, f1) values (1, 'A')");
    statement.addBatch("insert into test (id, f1) values (2, 'B')");
    statement.addBatch("insert into test (id, f1) values (3, 'C')");

    int[] records_affected = statement.executeBatch();

    statement.close();
    connection.close();
}
catch (SQLException e)
{
    e.printStackTrace();
}
// --</pre>

(replace this snippet with the code in between the `// --` remarks in the sample)

To look at the actual execution details, use the Wireshark summary from the captured network traffic:

This is how the relevant capture lines look like for the executed batch. The below network round trip is the result of `statement.executeBatch`. The `statement.addBatch` command adds the requests to the java `Statement` object:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">20 0.124569812    127.0.0.1 → 127.0.0.1    PGSQL 313 &gt;P/B/D/E/P/B/D/E/P/B/D/E/S
21 0.127215541    127.0.0.1 → 127.0.0.1    PGSQL 167 &lt;1/2/n/C/1/2/n/C/1/2/n/C/Z</pre>

Frame 20 contains the batch, and it's not hard to spot a repetition of `P/B/D/E` occurring three times and an `S` following it.

This means the above insert statements resulted in requests of P/Parse, B/Bind, D/Describe and E/Execute repeated three times, and then a S/synchronize request to end the transaction contained in a single frame/packet, despite being individual requests in the java code.

Frame 21 is a response from the PostgreSQL backend, which contains a repetition of 1/Parse competition, 2/Bind completion, n/No data, C/Command completion for each batched statement, and then Z/Ready for query.

The advantage of the batching is that no S/Synchronize command was sent in between the statement executions (indicated by E/Execution). Without batching, each E/Execution would be followed by S/Synchronize. This would require each statement to perform E/Execution and S/Synchronize, each performing a network round trip. With batching, this is combined for the three (batched) executions in a single network round trip. The ability to execute multiple statements with a single network round trip is the clear advantage that the statement batching feature offers.

PreparedStatement Batching {#h2-1-preparedstatement-batching}
-------------------------------------------------------------

Now, let's run the same test using a PreparedStatement object:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// --
try
(
    Connection connection = DriverManager.getConnection(url, properties);
    Statement statement = connection.createStatement();
    PreparedStatement prepared_statement = connection.prepareStatement(
        "insert into test (id, f1) values (?, ?)");
)
{
    statement.execute("create table if not exists test(id int primary key, f1 text)");
    statement.execute("truncate table test");

    prepared_statement.setInt(1, 1);
    prepared_statement.setString(2, "A");
    prepared_statement.addBatch();

    prepared_statement.setInt(1, 2);
    prepared_statement.setString(2, "B");
    prepared_statement.addBatch();

    prepared_statement.setInt(1, 3);
    prepared_statement.setString(2, "C");
    prepared_statement.addBatch();

    int[] records_affected = prepared_statement.executeBatch();

    prepared_statement.close();
    statement.close();
    connection.close();
}
catch (SQLException e)
{
    e.printStackTrace();
}
// --</pre>

This is the relevant part of the Wireshark capture of the protocol summaries, which shows the frames from the batch executed:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">20 0.110607534    127.0.0.1 ? 127.0.0.1    PGSQL 144 &gt;P/D/S
21 0.110961186    127.0.0.1 ? 127.0.0.1    PGSQL 99 &lt;1/t/n/Z
22 0.111643980    127.0.0.1 ? 127.0.0.1    PGSQL 223 &gt;B/D/E/B/D/E/B/D/E/S
23 0.113696151    127.0.0.1 ? 127.0.0.1    PGSQL 152 &lt;2/n/C/2/n/C/2/n/C/Z</pre>

Using the combination of batching and prepared statements results in a new pattern.

Frame 20 shows that the JDBC driver sends the batched prepared statement using three protocol messages - the P/parse, D/describe and S/sync messages. Frame 21 shows that the PostgreSQL backend responds with the messages: 1/parse completion, t/(bind) parameter description, n/no data), Z/ready, which is for creating a prepared statement. We'll look at the details of frame 20 below.

The third frame, frame 22, is the driver sending a sequence of B/bind, D/describe, E/execute messages repeated three times (corresponds to the number of batches), and then an S/synchronize message. Note: there is no P/parse message, which means the statement has already been parsed and cached on the database end (this was handled by the messages of Frames 20 and 21). Because of the definition of the binds (the variable placeholders for the prepared statement), the prepared statement is created immediately, and not delayed by prepareThreshold number of executions.

The response, frame 23, is a sequence of 2/bind completion, n/no data, C/command completion three times to match the above messages, and then Z/ready for the next query.

Now, let's take a deeper look at frame 20, to see if it produces a prepared statement request:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Frame 20: 144 bytes on wire (1152 bits), 144 bytes captured (1152 bits) on interface 0
Linux cooked capture
Internet Protocol Version 4, Src: 127.0.0.1, Dst: 127.0.0.1
Transmission Control Protocol, Src Port: 44482, Dst Port: 5432, Seq: 433, Ack: 671, Len: 76
PostgreSQL
    Type: Parse
    Length: 60
    Statement: S_1
    Query: insert into test (id, f1) values ($1, $2)
    Parameters: 2
        Type OID: 23
        Type OID: 1043
PostgreSQL
    Type: Describe
    Length: 9
    Statement: S_1
PostgreSQL
    Type: Sync
    Length: 4</pre>

Yes, the Statement: property of the Parse message has a name (`S_1`), indicating it's a named statement, and thus a request that generates a database side prepared statement. This means that despite the fact the JDBC drivers sets the `prepareThreshold` to five by default (see the [first article](https://foojay.io/today/a-dissection-of-java-jdbc-to-postgresql-connections/ "first article") for details), once a `PreparedStatement` object is batching with at least two statements, it will create a prepared statement in the database immediately.

The reWriteBatchedInserts Property {#h2-2-the-rewritebatchedinserts-property}
-----------------------------------------------------------------------------

Another noteworthy PostgreSQL JDBC driver feature is the `reWriteBatchedInserts` connection property. This is a setting that is specific to batched insert statements, and is false (off) by default.

This feature automatically rewrites multiple inserts in the same batch, such as:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">insert into mytable (f1, f2) values (?, ?);
insert into mytable (f1, f2) values (?, ?);</pre>

To a "multi-value insert" like this one:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">insert into mytable (f1, f2) values (?, ?), (?, ?);</pre>

The reason for performing this rewrite is that when multiple values are specified with a single insert statement, the insert statement is executed once, and only during the part of the insert where the values are inserted into the rows, it is repeating the work of inserting the values, and therefore removing a lot of the overhead of repeatedly executing a statement.

To use the `reWriteBatchedInserts` feature, set this property to "true" during the Connection configuration:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">properties.setProperty("reWriteBatchedInserts", true);
</pre>

Standard Statement Batching and reWriteBatchedInserts {#h2-3-standard-statement-batching-and-rewritebatchedinserts}
-------------------------------------------------------------------------------------------------------------------

The `reWriteBatchedInserts` optimization has a few requirements before it will perform its optimization:

* The batched statements have to be insert statements.
* The batched statements have to be added to a PreparedStatement object.
* The batched statements have to use bind placeholders (question marks) and data bound to the placeholders before added to the batch.

In other words: standard insert Statements with data added to a batch will not be rewritten.

Implementation Details of reWriteBatchedInserts With PreparedStatements {#h2-4-implementation-details-of-rewritebatchedinserts-with-preparedstatements}
-------------------------------------------------------------------------------------------------------------------------------------------------------

By analyzing the rewritten batched inserts for `PreparedStatement` objects, my test reproduced the following interesting implementation details for `reWriteBatchedInserts`:

The rewrite to multi-values insert statements does not name the statements during the parse phase, and therefore does not explicitly generate a server side prepared statement.  

Not using server side prepared statements for the Java `PreparedStatements` for the rewritten multi-values inserts, means these never use a server side prepared statement. As a result, they are parsed on the database side every time, even when executed more than `prepareThreshold` times.  

The resulting, rewritten to multi-values, insert statements are not rewritten into a singular insert statement, but to two insert statements for a given batch.

My personal conclusion is that `reWriteBatchedInserts` currently is a workaround for applications that are not able to generate optimal multi-values inserts. To optimally use the PostgreSQL database in a batch inserting situation, use batched prepared statements with multi-values defined with bind placeholders with `reWriteBatchedInserts` set to false.

Prepared statement batching with reWriteBatchedInserts {#h2-5-prepared-statement-batching-with-rewritebatchedinserts}
---------------------------------------------------------------------------------------------------------------------

When using prepared statements for inserts using bind variables without `reWriteBatchedInserts` set to true, such as the code snippet with "PreparedStatement Batching" is showing above, it will show the following JDBC call sequence:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">22 0.127353682    127.0.0.1 → 127.0.0.1    PGSQL 144 &gt;P/D/S
23 0.130447227    127.0.0.1 → 127.0.0.1    PGSQL 99 &lt;1/t/n/Z
24 0.131129439    127.0.0.1 → 127.0.0.1    PGSQL 223 &gt;B/D/E/B/D/E/B/D/E/S
25 0.132860774    127.0.0.1 → 127.0.0.1    PGSQL 152 &lt;2/n/C/2/n/C/2/n/C/Z
</pre>

As we saw with 'PreparedStatement Batching' earlier: the three inserts are combined in a single frame, making it as optimal as possible.

Simply by switching `reWriteBatchedInserts` to true, this changes to:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">18 0.111941067    127.0.0.1 → 127.0.0.1    PGSQL 131 &gt;P/B/D/E/S
19 0.119648309    127.0.0.1 → 127.0.0.1    PGSQL 109 &lt;1/2/n/C/Z
20 0.122509169    127.0.0.1 → 127.0.0.1    PGSQL 317 &gt;P/B/D/E/P/B/D/E/S
21 0.125357236    127.0.0.1 → 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
</pre>

This shows that the three executions are now combined into two executions, which means a sequence of P/Parse, B/Bind, D/Describe, E/Execute.

The optimization of single values inserts changed to multi-value inserts can be best seen by looking at the PostgreSQL packet details:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Frame 20: 317 bytes on wire (2536 bits), 317 bytes captured (2536 bits) on interface 0
Linux cooked capture
Internet Protocol Version 4, Src: 127.0.0.1, Dst: 127.0.0.1
Transmission Control Protocol, Src Port: 43710, Dst Port: 5432, Seq: 433, Ack: 671, Len: 249
PostgreSQL
    Type: Parse
    Length: 74
    Statement:
    Query: insert into test (id, f1) values ($1, $2),($3, $4)
    Parameters: 4
        Type OID: 23
        Type OID: 1043
        Type OID: 23
        Type OID: 1043
PostgreSQL
    Type: Bind
    Length: 46
    Portal:
    Statement:
    Parameter formats: 4
        Format: Binary (1)
        Format: Text (0)
        Format: Binary (1)
        Format: Text (0)
    Parameter values: 4
        Column length: 4
        Data: 00000001
        Column length: 1
        Data: 41
        Column length: 4
        Data: 00000002
        Column length: 1
        Data: 42
    Result formats: 0
PostgreSQL
    Type: Describe
    Length: 6
    Portal:
PostgreSQL
    Type: Execute
    Length: 9
    Portal:
    Returns: 1 rows
PostgreSQL
    Type: Parse
    Length: 57
    Statement:
    Query: insert into test (id, f1) values ($1, $2)
    Parameters: 2
        Type OID: 23
        Type OID: 1043
PostgreSQL
    Type: Bind
    Length: 29
    Portal:
    Statement:
    Parameter formats: 2
        Format: Binary (1)
        Format: Text (0)
    Parameter values: 2
        Column length: 4
        Data: 00000003
        Column length: 1
        Data: 43
    Result formats: 0
PostgreSQL
    Type: Describe
    Length: 6
    Portal:
PostgreSQL
    Type: Execute
    Length: 9
    Portal:
    Returns: 1 rows
PostgreSQL
    Type: Sync
    Length: 4</pre>

These are the messages sent by JDBC with `reWriteBatchedInserts` set to true for a `PreparedStatement` batched object with single value insert statements. The batched insert statements have been rewritten to multi-values inserts, however, in this case it creates two of them: one with two values, and one with one.

The sent messages should be familiar by now: P/parse, B/bind, D/describe and E/execute. The statement with parse does not have a name, therefore will not result in a database side prepared statement.

This means that the optimization we saw with 'Prepared Statement Batching', which is that JDBC parses the statement right away when it knows it's going to execute it multiple times and create a database side prepared statement, is NOT applied. It is reverted to parsing the statement for each E/Execute with `reWriteBatchedInserts` turned to true and being applied.

Maybe the rewritten insert statements needs to be executed prepareThreshold times? Let's test this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// -- the property reWriteBatchedInserts is set to true
try
(
    Connection connection = DriverManager.getConnection(url, properties);
    Statement statement = connection.createStatement();
    PreparedStatement prepared_statement = connection.prepareStatement(
        "insert into test (id, f1) values (?, ?)");
)
{
    statement.execute("create table if not exists test(id int primary key, f1 text)");
    statement.execute("truncate table test");

    for (int counter = 0; counter &lt;= 100; counter+=5)
    {
        prepared_statement.setInt(1, counter);
        prepared_statement.setString(2, "A");
        prepared_statement.addBatch();
        prepared_statement.setInt(1, counter+1);
        prepared_statement.setString(2, "A");
        prepared_statement.addBatch();
        prepared_statement.setInt(1, counter+2);
        prepared_statement.setString(2, "A");
        prepared_statement.addBatch();
        prepared_statement.setInt(1, counter+3);
        prepared_statement.setString(2, "A");
        prepared_statement.addBatch();
        prepared_statement.setInt(1, counter+4);
        prepared_statement.setString(2, "A");
        prepared_statement.addBatch();
        int[] records_affected = prepared_statement.executeBatch();
    }

    prepared_statement.close();
    statement.close();
    connection.close();
}
catch (SQLException e)
{
    e.printStackTrace();
}
// --</pre>

Using the for loop, the resulting insert statements should be executed 100/5=20 times. By looking at the PostgreSQL protocol summaries we can spot if the P/parse messages are going away or not:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">22 0.138047126    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S     -- 1
23 0.145342080    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
24 0.146053779    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S     -- 2
25 0.147729746    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
26 0.148182343    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S     -- 3
27 0.149013626    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
28 0.149394022    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S     -- 4
29 0.150775388    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
30 0.151175151    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S     -- 5
31 0.152029325    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
32 0.152393780    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S     -- 6; still parsing
33 0.153580601    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
34 0.153829366    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S
35 0.154634968    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
36 0.155031501    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S
37 0.156085295    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
38 0.156429765    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S
39 0.157449408    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
40 0.157846656    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S
41 0.158572821    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
42 0.158861134    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S
43 0.159548895    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
44 0.159835261    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S
45 0.160708669    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
46 0.160995488    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S
47 0.162157006    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
48 0.162560641    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S
49 0.163716994    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
50 0.164075144    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S
51 0.165051354    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
52 0.165514797    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S
53 0.166490175    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
54 0.166781469    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S
55 0.167680294    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
56 0.167922817    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S
57 0.168632798    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
58 0.168962029    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S
59 0.169910512    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
60 0.170243927    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S
61 0.171165850    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z
62 0.171576649    127.0.0.1 ? 127.0.0.1    PGSQL 385 &gt;P/B/D/E/P/B/D/E/S
63 0.172570445    127.0.0.1 ? 127.0.0.1    PGSQL 136 &lt;1/2/n/C/1/2/n/C/Z</pre>

This makes it clear. A repeated, consistent sequence of `P/B/D/E/P/B/D/E/S` means the execution of the rewritten statements generates two insert statements, each of which causes the messages P/parse, B/bind, D/describe and E/execute.

This means that adding the values yourself to the insert statement is a more efficient way to perform the above inserts:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// -- the property reWriteBatchedInserts is set to false (!!)
try
(
     Connection connection = DriverManager.getConnection(url, properties);
     Statement statement = connection.createStatement();
)
{
     statement.execute("create table if not exists test(id int primary key, f1 text)");
     statement.execute("truncate table test");

     String insert_statement = "insert into test (id, f1) values (?,?),(?,?),(?,?),(?,?),(?,?)";
     PreparedStatement prepared_statement = connection.prepareStatement(insert_statement);

     for (int counter = 0; counter &lt;= 100; counter+=10)
     {
         prepared_statement.setInt(1, counter);
         prepared_statement.setString(2, "A");
         prepared_statement.setInt(3, counter+1);
         prepared_statement.setString(4, "A");
         prepared_statement.setInt(5, counter+2);
         prepared_statement.setString(6, "A");
         prepared_statement.setInt(7, counter+3);
         prepared_statement.setString(8, "A");
         prepared_statement.setInt(9, counter+4);
         prepared_statement.setString(10, "A");
         prepared_statement.addBatch();
         prepared_statement.setInt(1, counter+5);
         prepared_statement.setString(2, "A");
         prepared_statement.setInt(3, counter+6);
         prepared_statement.setString(4, "A");
         prepared_statement.setInt(5, counter+7);
         prepared_statement.setString(6, "A");
         prepared_statement.setInt(7, counter+8);
         prepared_statement.setString(8, "A");
         prepared_statement.setInt(9, counter+9);
         prepared_statement.setString(10, "A");
         prepared_statement.addBatch();

         int[] records_affected = prepared_statement.executeBatch();
     }

     prepared_statement.close();
     statement.close();
     connection.close();
}
catch (SQLException e)
{
    e.printStackTrace();
}
// --</pre>

The above snippet uses manually created "multi values" insert statements. It adds two batches to the batched statement to make the JDBC driver immediately create a prepared statement, and only perform the steps of B/Bind, D/Describe and E/Execute:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">20 0.128336147    127.0.0.1 → 127.0.0.1    PGSQL 208 &gt;P/D/S           -- direct prepared statement creation
21 0.128845378    127.0.0.1 → 127.0.0.1    PGSQL 131 &lt;1/t/n/Z
22 0.130242832    127.0.0.1 → 127.0.0.1    PGSQL 309 &gt;B/D/E/B/D/E/S   -- bind calls right away, no parse
23 0.133396918    127.0.0.1 → 127.0.0.1    PGSQL 126 &lt;2/n/C/2/n/C/Z
24 0.133650801    127.0.0.1 → 127.0.0.1    PGSQL 309 &gt;B/D/E/B/D/E/S
25 0.134417572    127.0.0.1 → 127.0.0.1    PGSQL 126 &lt;2/n/C/2/n/C/Z
26 0.134960251    127.0.0.1 → 127.0.0.1    PGSQL 309 &gt;B/D/E/B/D/E/S
27 0.135632306    127.0.0.1 → 127.0.0.1    PGSQL 126 &lt;2/n/C/2/n/C/Z
28 0.136034911    127.0.0.1 → 127.0.0.1    PGSQL 309 &gt;B/D/E/B/D/E/S
29 0.136675155    127.0.0.1 → 127.0.0.1    PGSQL 126 &lt;2/n/C/2/n/C/Z
30 0.137021860    127.0.0.1 → 127.0.0.1    PGSQL 309 &gt;B/D/E/B/D/E/S
31 0.137622521    127.0.0.1 → 127.0.0.1    PGSQL 126 &lt;2/n/C/2/n/C/Z
32 0.137801845    127.0.0.1 → 127.0.0.1    PGSQL 309 &gt;B/D/E/B/D/E/S
33 0.138988278    127.0.0.1 → 127.0.0.1    PGSQL 126 &lt;2/n/C/2/n/C/Z
34 0.139290217    127.0.0.1 → 127.0.0.1    PGSQL 309 &gt;B/D/E/B/D/E/S
35 0.140075855    127.0.0.1 → 127.0.0.1    PGSQL 126 &lt;2/n/C/2/n/C/Z
36 0.140438517    127.0.0.1 → 127.0.0.1    PGSQL 309 &gt;B/D/E/B/D/E/S
37 0.141113223    127.0.0.1 → 127.0.0.1    PGSQL 126 &lt;2/n/C/2/n/C/Z
38 0.141392636    127.0.0.1 → 127.0.0.1    PGSQL 309 &gt;B/D/E/B/D/E/S
39 0.142077846    127.0.0.1 → 127.0.0.1    PGSQL 126 &lt;2/n/C/2/n/C/Z
40 0.142364684    127.0.0.1 → 127.0.0.1    PGSQL 309 &gt;B/D/E/B/D/E/S
41 0.143081280    127.0.0.1 → 127.0.0.1    PGSQL 126 &lt;2/n/C/2/n/C/Z
42 0.143309572    127.0.0.1 → 127.0.0.1    PGSQL 309 &gt;B/D/E/B/D/E/S
43 0.143911660    127.0.0.1 → 127.0.0.1    PGSQL 126 &lt;2/n/C/2/n/C/Z
</pre>

Generic plan {#h2-6-generic-plan}
---------------------------------

Another reason why a prepared statement for repeatedly used statements is important, is getting a so-called 'generic plan'. Currently insert statements are rewritten by the java property :

* The inserts rewritten to a multi-values statement by JDBC did not perform the creation of a database side prepared statement in my tests.
* Not having a database prepared statement means the insert must be parsed for every insert SQL statement sent.

The issue of requiring parsing for each execution is mentioned earlier, but there are more database side consequences.

When the PostgreSQL database is using a prepared (named) statement, it stores the prepared statement. This means it is possible to store the execution plan and the calculated 'cost' of the execution plan with the prepared statement.

Without a prepared statement, the PostgreSQL backend reuses an unnamed statement, which is flushed on reuse. This means it cannot store additional data such as an execution plan and cost.

The first five times a prepared/named statement is executed, the planner will create a new plan for each execution, called a custom plan. The details of each plan are stored with the prepared statement, even if it's the exact same plan. It will also create a 'generic plan', which is a plan for which the planner does not take the cardinality of the query parameters into account.

If the calculated cost of the generic plan after five or more executions is equal or less than the custom plan, the backend will switch to the generic plan. Once the generic plan is used, the planner does not perform the work of calculating the cost of the query for the prepared statement, it will just use the generic plan.

The work of the planner is skipped, meaning an optimization (reduction) of the amount of work. The downside is that if a potential better plan, based on the parameters, would be possible after the backend has switched to a generic plan, the planner cannot switch to it, as it no longer performs the costing and plan selection.

A generic plan skips almost all of the work in the plan/optimize phase for executing a statement, along with the parse and rewrite phases.

The value of five comes from the generic plan threshold: [an arbitrary value chosen by the PostgreSQL development group](https://github.com/postgres/postgres/blob/53ea2b7ad050ce4ad95c89bb55197209b65886a1/src/backend/utils/cache/plancache.c#L1046 "an arbitrary value chosen by the PostgreSQL development group"). This choice being arbitrary is annotated in the source code. With PostgreSQL version 12 and higher you can use the setting plan_cache_mode to force custom or generic plans outside of the automatic way it performs the choice by default.

Rewrite issue and limits {#h2-7-rewrite-issue-and-limits}
---------------------------------------------------------

About the inability to create a database side prepared statement for a rewritten insert statement in JDBC: the good news is that rewritten statements not creating a database side prepared statement was not expected behavior. The maintainers of [PGJDBC project](https://github.com/pgjdbc/pgjdbc "PGJDBC project"), confirmed that this is an issue that should be addressed in future versions of the driver: [Rewritten inserts from reWriteBatchedInserts do never cause server side prepared statement. #2882.](https://github.com/pgjdbc/pgjdbc/issues/2882 "Rewritten inserts from reWriteBatchedInserts do never cause server side prepared statement. #2882.")

Outside of the rewrite issue, there are limits in play here. Such as: `org.postgresql.util.PSQLException: PreparedStatement can have at most 65,535 parameters.`  

There might well be other limits in play too. It is not hard to imagine in a real life situation that memory may be limited (batching a large amount of data requires it to exist in a single place in memory). Therefore the most optimal setting should be tested with the actual size and amounts of data.

Conclusion {#h2-8-conclusion}
-----------------------------

With JDBC, batching means multiple statements are placed in a single network packet, instead of each statement being placed individually in a network packet due to the S/Synchronization that normally follows a statement.

Batching can be combined with prepared statements, and, if parameter placeholders are used, the JDBC driver will identify the prepared statements and the batch. If the JDBC driver finds more than one batched statement in the prepared statement batch, it will create a database-side prepared statement right away, not waiting for prepareThreshold times of execution, in order to efficiently execute it.

The JDBC driver can identify single 'value'/row inserts and rewrite these to "multi values" insert statements. This is wonderful functionality if your application or batches cannot be rewritten. However, the downside is that this currently causes the rewritten statements to never get created as a server-side prepared statement. An issue has been raised with the PostgreSQL JDBC project, and it should be resolved in a future version.

Currently, the most optimal way to perform batched inserts is to write multi-values insert statements yourself and use the batching methods. This ensures it will be executed via a server-side prepared statement immediately, regardless of the prepareThreshold setting.
