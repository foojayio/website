---
title: "A Dissection of Java JDBC to PostgreSQL Connections"
slug: "a-dissection-of-java-jdbc-to-postgresql-connections"
date: "2023-05-12T08:52:52+00:00"
lastmod: "2023-05-12T10:43:21+00:00"
description: "When using Java JDBC with PostgreSQL, know the difference between simple and extended protocols, how to recognize protocol implementations, and more."
authors:
  - "frits-hoogland"
image: "https://foojay.io/wp-content/uploads/2023/04/Yugabyte-Logo-RGB.png"
categories:
  - "Databases"
tags:
related_posts:
  - "how-java-litters-beyond-the-heap-relational-databases"
  - "how-java-litters-beyond-the-heap-part-2-distributed-databases"
  - "5-things-you-probably-didnt-know-about-java-concurrency"
enlighterjs: true
frozen: false
---

Many Java applications use a database, and many use JDBC and [PostgreSQL](https://www.yugabyte.com/postgresql/ "PostgreSQL").

The latency of database usage is often important, yet, what exactly happens at the database level is unknown.

This article explains which options are available, what happens, and why.

Most importantly, it includes a small Java class that can be used and modified to execute specific database tasks, plus a description of how to use a utility ([Wireshark](https://www.wireshark.org "Wireshark")) to see what is actually happening.

By looking in the communication internals between the JDBC driver and database, you'll learn:

* The difference between the PostgreSQL simple and extended protocols.
* Why the extended protocol is more efficient and used by the JDBC driver by default.
* The difference between a JDBC PreparedStatement object and a database prepared statement.
* Why the execution of a JDBC PreparedStatement object does not create a database side prepared statement during the first executions.
* Why it's important to close a prepared statement.
* How to understand and recognise the protocol implementation.

Note that he observations in this article are based on OpenJDK version 17.0.6, JDBC version 42.6.0, and PostgreSQL 15.

The Extended Query Protocol {#h2-0-the-extended-query-protocol}
---------------------------------------------------------------

The default PostgreSQL protocol used by the PostgreSQL JDBC driver is the extended query protocol. The extended query protocol requires a database execution to use three messages: Parse, Bind and Execute. The extended query protocol is part of the general PostgreSQL network protocol called [the Frontend/Backend protocol](https://www.postgresql.org/docs/current/protocol.html "the Frontend/Backend protocol").

The choice of a protocol depends on the setting of the PostgreSQL JDBC connection property preferQueryMode, which defaults to 'extended', and therefore JDBC generally uses the extended query protocol.

As specified in the [PreferQueryMode](https://jdbc.postgresql.org/documentation/publicapi/org/postgresql/jdbc/PreferQueryMode.html "PreferQueryMode") enum, the property has a number of options:

* `simple`: use the simple query protocol mode.
* `extended`: use the extended query protocol mode. This is the default.
* `extendForPrepared`: use the simple query protocol mode, but use the extended query protocol mode for prepared statements.
* `extendedCacheEverything`: use the extended query protocol mode.

The reason for these options is the ability of the PostgreSQL database to use two essential ways of executing SQL messages:

* via the `simple` query protocol
* via the `extended` query protocol

The reason for having two protocols performing the same task is that before the current version of the Frontend/Backend protocol (version 3), the extended protocol didn't exist. The Frontend/Backend protocol version 3 was introduced with PostgreSQL version 7.4.

The Simple Query Protocol {#h2-1-the-simple-query-protocol}
-----------------------------------------------------------

A lot of PostgreSQL database applications and utilities use the simple query protocol, most prominently the general PostgreSQL database CLI 'psql', which currently uniquely uses the simple query protocol.

So, one reason to use the simple query protocol is that the client has been using it to communicate with PostgreSQL since before version 7.4.

Another reason to use the simple query protocol is when the database client message has no variables. Thus, it simply requires the request to be sent in one go, so there is no benefit in using the extended query protocol over sending the request in one go.

The final reason for using the simple query protocol is that it allows multiple messages to be sent for certain messages separated by a semicolon, which is called statement batching.

PostgreSQL Database Execution Steps {#h2-2-postgresql-database-execution-steps}
-------------------------------------------------------------------------------

The reason the extended query protocol uses three messages has to do with the way the PostgreSQL database performs the execution of statements. Every SQL statement in PostgreSQL has to perform these four steps to execute a statement:

* Parse: syntactic parse of the SQL text, which transforms the text into a parse tree.
* Rewrite:
  * Parse: semantic parse of the parse tree to determine table existence, user access, etc.
  * Rewrite: rewrite of the parse tree, such as views, which transforms the view into the tables the view is based on.
* Plan: the planner takes the potentially rewritten parse tree and creates an execution tree (i.e. a tree of plan nodes, also known as the execution plan) based on the assumed "cost" of different access methods, order of access and calculated resulting rows of a plan node.
* Execute: the executor takes the execution tree and executes it.

The simple query protocol sends a single message of the type ['Q' or simple query](https://github.com/postgres/postgres/blob/e9f451accbde17d106ac6a92c8877992d4410629/src/backend/tcop/postgres.c#L4616 "'Q' or simple query") with the statement as payload. All of these database-side steps are executed by the simple query command on the server side.

The extended query protocol needs to send at least three messages to execute a statement: ['P' or Parse](https://github.com/postgres/postgres/blob/e9f451accbde17d106ac6a92c8877992d4410629/src/backend/tcop/postgres.c#L4640 "'P' or Parse"), ['B' or Bind](https://github.com/postgres/postgres/blob/e9f451accbde17d106ac6a92c8877992d4410629/src/backend/tcop/postgres.c#L4670 "'B' or Bind") and ['E' or Execute](https://github.com/postgres/postgres/blob/e9f451accbde17d106ac6a92c8877992d4410629/src/backend/tcop/postgres.c#L4685 "'E' or Execute"). You probably noticed the names of the messages to be partially overlapping with the PostgreSQL execution steps. This is how the extended protocol message types and the server side execution steps are related:

| Simple query protocol message | Extended query protocol message | PostgreSQL execution step |
|-------------------------------|---------------------------------|---------------------------|
| Query                         | Parse                           | Parse                     |
|                               |                                 | Rewrite                   |
|                               | Bind                            | Plan                      |
|                               | Execute                         | Execute                   |

By now,you should have an understanding of the two common PostgreSQL JDBC implementation protocols and how they map to the PostgreSQL database side execution. You might also wonder why sending the JDBC driver selects the extended protocol, that uses three messages, by default over the simple protocol that can do that with a single message. This is explained below, when we look at the network dataframe contents.

Sample Java test {#h2-3-sample-java-test}
-----------------------------------------

<pre class="EnlighterJSRAW" data-enlighter-language="java">// begin file: simple.java
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
        //
        try
        {
            Connection connection = DriverManager.getConnection(url, properties);
            // -- 
            ResultSet result;
            Statement statement = connection.createStatement();
            result = statement.executeQuery("select now()");
            result.next();
            result.close();
            // --
            connection.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}
// end of file: simple.java
</pre>

To build this java executable on the CLI:

* Download the PostgreSQL JDBC driver from: <https://jdbc.postgresql.org/download/postgresql-42.6.0.jar> and extract it so that the postgresql-42.6.0.jar file is in the current working directory.
* Compile: `javac -cp postgresql-42.6.0.jar simple.java`
* Run: `java -cp postgresql-42.6.0.jar:. Simple`

Or you can use an IDE, which will help with some of the work.

Note that you might have to change the PostgreSQL pg_hba.conf file to enable the database listening on the 5432 port if you're testing with PostgreSQL instance installed to look at this.

Wireshark: Network Sniffer and Protocol Dissector {#h2-4-wireshark-network-sniffer-and-protocol-dissector}
----------------------------------------------------------------------------------------------------------

To understand what is going on between the client and database it is useful to know how they communicate with each other.

For the purpose of this article, the networking layer is the best place to look into the communication internals, by analyzing the network requests going back and forth between the client and the database. This can be done very easily using Wireshark, running either on the client (where the java class runs), or the server side (where the database runs). This is because Wireshark needs to listen to the source or destination port where the traffic flows to capture the network traffic contents.

This is how to install Wireshark CLI on Red Hat-compatible Linux systems:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">sudo yum install wireshark-cli
</pre>

To list the network traffic with a PostgreSQL summary, use:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">sudo tshark -i any -f 'tcp port 5432' -d tcp.port==5432,pgsql</pre>

To list the network traffic with the PostgreSQL traffic fully decoded, use:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">sudo tshark -i any -f 'tcp port 5432' -d tcp.port==5432,pgsql -O pgsql</pre>

The First Look at JDBC Traffic Using Wireshark {#h2-5-the-first-look-at-jdbc-traffic-using-wireshark}
-----------------------------------------------------------------------------------------------------

When you start the sample test, Wireshark will capture the following traffic between the JDBC driver and the database:

<pre class="EnlighterJSRAW" data-enlighter-language="generic"> 1 0.000000000    127.0.0.1 ? 127.0.0.1    TCP 76 38212 ? 5432 [SYN] Seq=0 Win=43690 Len=0 MSS=65495 SACK_PERM=1 TSval=591859564 TSecr=0 WS=128
 2 0.000009360    127.0.0.1 ? 127.0.0.1    TCP 76 5432 ? 38212 [SYN, ACK] Seq=0 Ack=1 Win=43690 Len=0 MSS=65495 SACK_PERM=1 TSval=591859564 TSecr=591859564 WS=128
 3 0.000016616    127.0.0.1 ? 127.0.0.1    TCP 68 38212 ? 5432 [ACK] Seq=1 Ack=1 Win=43776 Len=0 TSval=591859564 TSecr=591859564
 4 0.011656311    127.0.0.1 ? 127.0.0.1    PGSQL 76 &gt;
 5 0.011851190    127.0.0.1 ? 127.0.0.1    TCP 68 5432 ? 38212 [ACK] Seq=1 Ack=9 Win=43776 Len=0 TSval=591859575 TSecr=591859575
 6 0.011907696    127.0.0.1 ? 127.0.0.1    PGSQL 69 &lt;
 7 0.011912049    127.0.0.1 ? 127.0.0.1    TCP 68 38212 ? 5432 [ACK] Seq=9 Ack=2 Win=43776 Len=0 TSval=591859576 TSecr=591859576
 8 0.028942611    127.0.0.1 ? 127.0.0.1    PGSQL 178 &gt;
 9 0.031848981    127.0.0.1 ? 127.0.0.1    PGSQL 448 &lt;R/S/S/S/S/S/S/S/S/S/S/S/S/S/K/Z
10 0.059421848    127.0.0.1 ? 127.0.0.1    PGSQL 131 &gt;P/B/E/S
11 0.059825705    127.0.0.1 ? 127.0.0.1    PGSQL 93 &lt;1/2/C/Z
12 0.060284645    127.0.0.1 ? 127.0.0.1    PGSQL 152 &gt;P/B/E/S
13 0.060613934    127.0.0.1 ? 127.0.0.1    PGSQL 138 &lt;1/2/C/S/Z
14 0.089038523    127.0.0.1 ? 127.0.0.1    PGSQL 124 &gt;P/B/D/E/S
15 0.089662938    127.0.0.1 ? 127.0.0.1    PGSQL 167 &lt;1/2/T/D/C/Z
16 0.101702993    127.0.0.1 ? 127.0.0.1    PGSQL 73 &gt;X
17 0.101862798    127.0.0.1 ? 127.0.0.1    TCP 68 38212 ? 5432 [FIN, ACK] Seq=327 Ack=576 Win=44800 Len=0 TSval=591859666 TSecr=591859653
18 0.104742459    127.0.0.1 ? 127.0.0.1    TCP 68 5432 ? 38212 [FIN, ACK] Seq=576 Ack=328 Win=43776 Len=0 TSval=591859668 TSecr=591859665
19 0.104749357    127.0.0.1 ? 127.0.0.1    TCP 68 38212 ? 5432 [ACK] Seq=328 Ack=577 Win=44800 Len=0 TSval=591859668 TSecr=591859668
</pre>

This overview of network frames (i.e. the actual data sent over the network) looks a bit daunting, so let's examine the Java code and look at the lines that actually perform database network requests:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">class Simple
{
    public static void main(String[] args)
    {
        String url = "jdbc:postgresql://192.168.66.80:5432/postgres";
        Properties properties = new Properties();
        properties.setProperty("user", "postgres");
        properties.setProperty("password", "postgres");
        properties.setProperty("preferQueryMode", "extended"); // extended is the default
        //
        try
        {
// 1 ----------------------------------------------------------------------------------------------
            Connection connection = DriverManager.getConnection(url, properties);
// -------------------------------------------------------------------------------------------------
            // -- 
            ResultSet result;
            Statement statement = connection.createStatement();
// 2 ----------------------------------------------------------------------------------------------
            result = statement.executeQuery("select now()");
// -------------------------------------------------------------------------------------------------
            result.next();
            result.close();
            // -- 
// 3 ----------------------------------------------------------------------------------------------
            connection.close();
// -------------------------------------------------------------------------------------------------
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }
}</pre>

1 - Create the database connection  

2 - Execute a statement  

3 - Close the connection

Now, let's see which part of the network traffic belongs to which Java request:

1 (create the database connection)

<pre class="EnlighterJSRAW" data-enlighter-language="generic"> 1 0.000000000    127.0.0.1 ? 127.0.0.1    TCP 76 38212 ? 5432 [SYN] Seq=0 Win=43690 Len=0 MSS=65495 SACK_PERM=1 TSval=591859564 TSecr=0 WS=128
 2 0.000009360    127.0.0.1 ? 127.0.0.1    TCP 76 5432 ? 38212 [SYN, ACK] Seq=0 Ack=1 Win=43690 Len=0 MSS=65495 SACK_PERM=1 TSval=591859564 TSecr=591859564 WS=128
 3 0.000016616    127.0.0.1 ? 127.0.0.1    TCP 68 38212 ? 5432 [ACK] Seq=1 Ack=1 Win=43776 Len=0 TSval=591859564 TSecr=591859564
 4 0.011656311    127.0.0.1 ? 127.0.0.1    PGSQL 76 &gt;
 5 0.011851190    127.0.0.1 ? 127.0.0.1    TCP 68 5432 ? 38212 [ACK] Seq=1 Ack=9 Win=43776 Len=0 TSval=591859575 TSecr=591859575
 6 0.011907696    127.0.0.1 ? 127.0.0.1    PGSQL 69 &lt;
 7 0.011912049    127.0.0.1 ? 127.0.0.1    TCP 68 38212 ? 5432 [ACK] Seq=9 Ack=2 Win=43776 Len=0 TSval=591859576 TSecr=591859576
 8 0.028942611    127.0.0.1 ? 127.0.0.1    PGSQL 178 &gt;
 9 0.031848981    127.0.0.1 ? 127.0.0.1    PGSQL 448 &lt;R/S/S/S/S/S/S/S/S/S/S/S/S/S/K/Z
10 0.059421848    127.0.0.1 ? 127.0.0.1    PGSQL 131 &gt;P/B/E/S
11 0.059825705    127.0.0.1 ? 127.0.0.1    PGSQL 93 &lt;1/2/C/Z
12 0.060284645    127.0.0.1 ? 127.0.0.1    PGSQL 152 &gt;P/B/E/S
13 0.060613934    127.0.0.1 ? 127.0.0.1    PGSQL 138 &lt;1/2/C/S/Z</pre>

2 (execute statement)

<pre class="EnlighterJSRAW" data-enlighter-language="generic">14 0.089038523    127.0.0.1 ? 127.0.0.1    PGSQL 124 &gt;P/B/D/E/S
15 0.089662938    127.0.0.1 ? 127.0.0.1    PGSQL 167 &lt;1/2/T/D/C/Z</pre>

3 (close connection)

<pre class="EnlighterJSRAW" data-enlighter-language="generic">16 0.101702993    127.0.0.1 ? 127.0.0.1    PGSQL 73 &gt;X
17 0.101862798    127.0.0.1 ? 127.0.0.1    TCP 68 38212 ? 5432 [FIN, ACK] Seq=327 Ack=576 Win=44800 Len=0 TSval=591859666 TSecr=591859653
18 0.104742459    127.0.0.1 ? 127.0.0.1    TCP 68 5432 ? 38212 [FIN, ACK] Seq=576 Ack=328 Win=43776 Len=0 TSval=591859668 TSecr=591859665
19 0.104749357    127.0.0.1 ? 127.0.0.1    TCP 68 38212 ? 5432 [ACK] Seq=328 Ack=577 Win=44800 Len=0 TSval=591859668 TSecr=591859668
</pre>

The summarized network capture output has the following format: packet capture number, time relative to first packet captured in seconds, found protocol, size, and protocol dependent output.

The first conclusion is that---based on the network traffic timing (the second column)---it is clear that creating a connection takes an amount of time relative to the following statement execution that is significant. The connection creation time is the time difference between frame one (0.0) and thirteen (0.061), which is 0.61 seconds. That might look like a tiny amount of time, however the statement execution took: 0.090 (frame fifteen) - 0.090 (frame fourteen), which is 0.01 second.

I explicitly call out the execution that is performed in this test: the time it takes for the database to execute a statement is relative to the busyness of the database server and to the amount of work the database needs to perform to execute the statement. This means you cannot declare a fixed latency if you don't know the statement; you probably don't know how it's executed and therefore the amount of work the database needs to perform.

At this point it's not possible to understand that execution of the statement was in frames 14 and 15. This will become clear in the text below, which takes you through a more detailed packet captures. However, for the sake of brevity, we will continue to look inside the packets/frames that are part of the actual execution.

Analyzing Statement Execution Frames {#h2-6-analyzing-statement-execution-frames}
---------------------------------------------------------------------------------

Let's look at the statement execution flow in more detail, because that is what will generally be done when using a PostgreSQL database. The below statement execution details are from the frames captured above, but now with the traffic fully decoded. This shows all the details required to understand that frame 14 was the frame which sent the statement to the PostgreSQL backend:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Frame 14: 124 bytes on wire (992 bits), 124 bytes captured (992 bits) on interface 0
Linux cooked capture
Internet Protocol Version 4, Src: 127.0.0.1, Dst: 127.0.0.1
Transmission Control Protocol, Src Port: 56220, Dst Port: 5432, Seq: 266, Ack: 477, Len: 56
PostgreSQL
    Type: Parse
    Length: 20
    Statement:
    Query: select now()
    Parameters: 0
PostgreSQL
    Type: Bind
    Length: 12
    Portal:
    Statement:
    Parameter formats: 0
    Parameter values: 0
    Result formats: 0
PostgreSQL
    Type: Describe
    Length: 6
    Portal:
PostgreSQL
    Type: Execute
    Length: 9
    Portal:
    Returns: all rows
PostgreSQL
    Type: Sync
    Length: 4</pre>

This is the Java/client execution request that, for a single execution request performs five PostgreSQL requests:

* Parse (P), to let the Query that the parse request carries be parsed as unnamed Statement (Statement:).
* Bind (B), to bind any potential parameters for the unnamed Statement, which uses an unnamed Portal (Portal:).
* Describe (D), to let the server describe the row format of data that comes back.
* Execute (E), to execute the unnamed Portal.
* Sync (S), to complete the execution and close the current transaction.  
  (<https://www.postgresql.org/docs/current/protocol-flow.html>)

Note that these PostgreSQL level messages are all contained inside a single frame alias network packet, which was frame number 14 from the above capture.

Next, the database server sends back the following response (frame 15 of the complete network packet):

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Frame 15: 167 bytes on wire (1336 bits), 167 bytes captured (1336 bits) on interface 0
Linux cooked capture
Internet Protocol Version 4, Src: 127.0.0.1, Dst: 127.0.0.1
Transmission Control Protocol, Src Port: 5432, Dst Port: 56220, Seq: 477, Ack: 322, Len: 99
PostgreSQL
    Type: Parse completion
    Length: 4
PostgreSQL
    Type: Bind completion
    Length: 4
PostgreSQL
    Type: Row description
    Length: 28
    Field count: 1
        Column name: now
            Table OID: 0
            Column index: 0
            Type OID: 1184
            Column length: 8
            Type modifier: -1
            Format: Text (0)
PostgreSQL
    Type: Data row
    Length: 39
    Field count: 1
        Column length: 29
        Data: 323032332d30342d31372031333a35393a32312e39383534...
PostgreSQL
    Type: Command completion
    Length: 13
    Tag: SELECT 1
PostgreSQL
    Type: Ready for query
    Length: 5
    Status: Idle (73)</pre>

There are six distinct messages within the response frame :

* Parse completion (1), response to (P).
* Bind completion (2), response to (B).
* Row description (T), response to (D).
* Data row (D), response to (E).
* Command completion (C), to indicate the execution has finished and no more data will be coming.
* Ready for query (Z), to indicate the server can receive and process another request.

For the response frame we see that the response messages are all sent inside a single frame again.

The important thing to learn here is that the statement execution flow for the extended protocol uses a number of messages. These are Parse, Bind, and Execute from the client, but also other messages, which can be combined in a single frame. The java statement execution used the absolute minimum of frames possible: two.

Testing With the Simple Protocol {#h2-7-testing-with-the-simple-protocol}
-------------------------------------------------------------------------

Let's compare the network traffic of the extended protocol to the simple protocol. We'll do that by running the sample test with the preferQueryMode set to "simple".

Many clients, including the common PostgreSQL CLI 'psql', use the simple query protocol exclusively. The simple query protocol can be set for the test class by setting the preferQueryMode property to 'simple':

<pre class="EnlighterJSRAW" data-enlighter-language="generic">properties.setProperty("preferQueryMode", "simple");
</pre>

The request flow changes in the following way:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">14 0.087775192    127.0.0.1 ? 127.0.0.1    PGSQL 86 &gt;Q
15 0.089352778    127.0.0.1 ? 127.0.0.1    PGSQL 157 &lt;T/D/C/Z</pre>

This means the request sequence of Parse, Bind, Execute, Sync are all now combined in the 'Q' message.

The response is much less changed. Essentially, the parse and bind completion messages are gone, but the other messages of the Row description (T), Data row (D), Command completion (C) and Ready for query (Z) are still there. A packet capture is not included to try to keep the length of the article as short as possible, but can easily be tried using the sample Java class and setting the simple protocol.

A good question here is what is the best choice for executing a statement: the simple query protocol or the extended query protocol? Please bear in mind that this is strictly about a statement with no "variables" or "binds". The answer is that, despite the simple query protocol sending slightly lesser bytes on the wire, there is no clear advantage to using the simple query protocol over the extended query protocol or vice versa. However, because the simple protocol does not allow binding of query parameters, and thus requires a dynamic query to be built using string concatenation, it's vulnerable to SQL injection.

There is a distinct property that the simple query protocol allows which the extended query protocol doesn't in the same way. This simply means adding more statements separated by a semicolon: the simple query protocol allows submission of multiple statements in a single message. This is limited to statements that do not return results, such as DDL typically. However, the extended query protocol has a specific option that can achieve the same principle, which is called 'batching'.

Prepared Statements Advantages {#h2-8-prepared-statements-advantages}
---------------------------------------------------------------------

That begs the question why the extended query protocol has more moving parts, with no clear advantage that we can see so far? The answer is twofold:

* using bind variables without the risk of SQL injection.
* optimizing queries execution by reusing already prepared statements.

To understand the advantage of using prepared statements, we need to look at the extended query protocol requests, and the database execution phases.

The execution of a SQL statement using the extended query protocol requires the submission of three messages (packed in a single network packet by JDBC), which maps to the four database phases of statement execution:

| Extended query protocol message | PostgreSQL execution step |
|---------------------------------|---------------------------|
| Parse                           | Parse                     |
|                                 | Rewrite                   |
| Bind                            | Plan                      |
| Execute                         | Execute                   |

The reason for the distinct extended query protocol parse phase, and mapping it to the PostgreSQL database parse and rewrite phases, is that this allows the PostgreSQL database to create a version of the parsed and rewritten statement which is explicitly named and stored in the memory of the PostgreSQL backend. This means it can be reused without the need to perform the extended query protocol parse message and the database side parse and rewrite phases again, meaning that an already prepared statement only needs the bind and execute messages to be submitted for execution.

A prepared statement is stored in the PostgreSQL memory heap of the backend the database client is connected to, and is unique to that backend. PostgreSQL has no shared SQL area, nor shared prepared statements.

To create a PostgreSQL database side prepared statement, a Java PostgreSQL JDBC PreparedStatement object must be created. This includes the statement so it can be submitted for parse, which then can be executed by calling the executeQuery() method from the PreparedStatement object.

Change the code of the class above with the following code:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// --
ResultSet result;
PreparedStatement statement = connection.prepareStatement("select now()");
result = statement.executeQuery();
result.next();
result.close();
statement.close();
// --</pre>

(make sure `preferQueryMode` is set to `extended` again)

Let's see what this does using wireshark:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">14 0.088640230    127.0.0.1 ? 127.0.0.1    PGSQL 124 &gt;P/B/D/E/S
15 0.089329790    127.0.0.1 ? 127.0.0.1    PGSQL 167 &lt;1/2/T/D/C/Z</pre>

As before, the wireshark summary output shows the request to be performing P/parse, B/bind, D/describe, E/execute and S/synchronize. The response shows 1/parse completion, 2/bind completion, T/row description, D/data row, C/command completion, Z/ready for query. This is identical to the previous regular (non prepared) statement execution.

This is understandable: even a prepared statement would need to be parsed before it can be executed as a prepared statement, because SQL execution must follow the four principal steps for execution.

That means that a PostgreSQL prepared statement can only take advantage of being prepared, and reuse the previously created prepared statement, if it's executed for the second time, so the second execution can reuse the prepared statement parsed during the first execution.

Change the code of the class with the following code:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// --
ResultSet result;
PreparedStatement statement = connection.prepareStatement("select now()");
result = statement.executeQuery();
result = statement.executeQuery();
result.next();
result.close();
statement.close();
// --</pre>

Look at the wireshark output again:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">14 0.088497830    127.0.0.1 ? 127.0.0.1    PGSQL 124 &gt;P/B/D/E/S
15 0.089362126    127.0.0.1 ? 127.0.0.1    PGSQL 167 &lt;1/2/T/D/C/Z
16 0.099519555    127.0.0.1 ? 127.0.0.1    PGSQL 124 &gt;P/B/D/E/S
17 0.099773414    127.0.0.1 ? 127.0.0.1    PGSQL 167 &lt;1/2/T/D/C/Z</pre>

The first execution with frame 14 and the response with frame 15 can be seen. The next execution can be seen in frame 16, which does include P/parse again. This does not show the optimization that prepared statements promise by having the ability to skip the parse phase. What is going on?

JDBC PrepareThreshold Setting {#h2-9-jdbc-preparethreshold-setting}
-------------------------------------------------------------------

What is happening here is both a blessing and a curse in my opinion. To prevent the database backend from filling up with prepared statements that are not reused, there is another mechanism built into the JDBC driver and controlled with the `prepareThreshold` property.

The `prepareThreshold` property value sets the amount of executions required for the Java-side PreparedStatement object with which it will actually perform the creation of a database side prepared statement. The property defaults to five.

This means that a PreparedStatement object needs five executions to cause a PostgreSQL database side prepared statement to be created, and thus, a sixth execution to actually take advantage of a database side prepared statement.

The blessing here is that you can just create every statement as a PreparedStatement object without giving it much thought. Only if it is executed enough times (five times by default), it will actually create a database side prepared statement. The curse is that, if you expect the prepared statement declaration to create an actual database prepared statement (which I don't think is entirely unreasonable to expect) it will not do what you explicitly programmed, but rather do it some time later, depending on the number of executions.

Looking at Actual Database Prepared Statement Execution {#h2-10-looking-at-actual-database-prepared-statement-execution}
------------------------------------------------------------------------------------------------------------------------

Change the code of the class with the following code:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// --
ResultSet result;
PreparedStatement statement = connection.prepareStatement("select now()");
for (int i=0; i&lt;=6; i+=1)
{
    result = statement.executeQuery();
}
result.close();
statement.close();
// --

</pre>

This is how it looks like using wireshark:

<pre class="EnlighterJSRAW" data-enlighter-language="generic"> &nbsp;&nbsp;14 0.087452579&nbsp;&nbsp;&nbsp;&nbsp;127.0.0.1 ? 127.0.0.1&nbsp;&nbsp;&nbsp;&nbsp;PGSQL 124 &gt;P/B/D/E/S&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;- first execution
   15 0.088431742&nbsp;&nbsp;&nbsp;&nbsp;127.0.0.1 ? 127.0.0.1&nbsp;&nbsp;&nbsp;&nbsp;PGSQL 167 &lt;1/2/T/D/C/Z
   16 0.099370527&nbsp;&nbsp;&nbsp;&nbsp;127.0.0.1 ? 127.0.0.1&nbsp;&nbsp;&nbsp;&nbsp;PGSQL 124 &gt;P/B/D/E/S
 &nbsp;&nbsp;17 0.099661109&nbsp;&nbsp;&nbsp;&nbsp;127.0.0.1 ? 127.0.0.1&nbsp;&nbsp;&nbsp;&nbsp;PGSQL 167 &lt;1/2/T/D/C/Z
 &nbsp;&nbsp;18 0.099839810&nbsp;&nbsp;&nbsp;&nbsp;127.0.0.1 ? 127.0.0.1&nbsp;&nbsp;&nbsp;&nbsp;PGSQL 124 &gt;P/B/D/E/S
 &nbsp;&nbsp;19 0.099949285&nbsp;&nbsp;&nbsp;&nbsp;127.0.0.1 ? 127.0.0.1&nbsp;&nbsp;&nbsp;&nbsp;PGSQL 167 &lt;1/2/T/D/C/Z
 &nbsp;&nbsp;20 0.100094460&nbsp;&nbsp;&nbsp;&nbsp;127.0.0.1 ? 127.0.0.1&nbsp;&nbsp;&nbsp;&nbsp;PGSQL 124 &gt;P/B/D/E/S
 &nbsp;&nbsp;21 0.100368924&nbsp;&nbsp;&nbsp;&nbsp;127.0.0.1 ? 127.0.0.1&nbsp;&nbsp;&nbsp;&nbsp;PGSQL 167 &lt;1/2/T/D/C/Z
 &nbsp;&nbsp;22 0.100866737&nbsp;&nbsp;&nbsp;&nbsp;127.0.0.1 ? 127.0.0.1&nbsp;&nbsp;&nbsp;&nbsp;PGSQL 130 &gt;P/B/D/E/S&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;- fifth execution
 &nbsp;&nbsp;23 0.101059578&nbsp;&nbsp;&nbsp;&nbsp;127.0.0.1 ? 127.0.0.1&nbsp;&nbsp;&nbsp;&nbsp;PGSQL 167 &lt;1/2/T/D/C/Z
 &nbsp;&nbsp;24 0.101214370&nbsp;&nbsp;&nbsp;&nbsp;127.0.0.1 ? 127.0.0.1&nbsp;&nbsp;&nbsp;&nbsp;PGSQL 101 &gt;B/E/S&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&lt;- sixth execution
 &nbsp;&nbsp;25 0.101284790&nbsp;&nbsp;&nbsp;&nbsp;127.0.0.1 ? 127.0.0.1&nbsp;&nbsp;&nbsp;&nbsp;PGSQL 112 &lt;2/D/C/Z</pre>

For the first five executions you should recognise the PostgreSQL protocol letters. Only the sixth time (frame 24) you see that no P (parse) is requested, indicating the use of a database-side prepared statement.

If you look at a detailed dissection of the PostgreSQL-level packet information, you can see how this works. I've printed the packet information from frame number 22 (the fifth execution that generates the prepared statement) up to frame number 25:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Frame 22: 130 bytes on wire (1040 bits), 130 bytes captured (1040 bits) on interface 0
Linux cooked capture
Internet Protocol Version 4, Src: 127.0.0.1, Dst: 127.0.0.1
Transmission Control Protocol, Src Port: 40808, Dst Port: 5432, Seq: 490, Ack: 873, Len: 62
PostgreSQL
 &nbsp;&nbsp;&nbsp;Type: Parse
 &nbsp;&nbsp;&nbsp;Length: 23
 &nbsp;&nbsp;&nbsp;Statement: S_1
 &nbsp;&nbsp;&nbsp;Query: select now()
 &nbsp;&nbsp;&nbsp;Parameters: 0
PostgreSQL
 &nbsp;&nbsp;&nbsp;Type: Bind
 &nbsp;&nbsp;&nbsp;Length: 15
 &nbsp;&nbsp;&nbsp;Portal:&nbsp;&nbsp;&nbsp;&nbsp;
    Statement: S_1
 &nbsp;&nbsp;&nbsp;Parameter formats: 0
 &nbsp;&nbsp;&nbsp;Parameter values: 0
 &nbsp;&nbsp;&nbsp;Result formats: 0
PostgreSQL
 &nbsp;&nbsp;&nbsp;Type: Describe
 &nbsp;&nbsp;&nbsp;Length: 6
 &nbsp;&nbsp;&nbsp;Portal:
PostgreSQL
 &nbsp;&nbsp;&nbsp;Type: Execute
 &nbsp;&nbsp;&nbsp;Length: 9
 &nbsp;&nbsp;&nbsp;Portal:
 &nbsp;&nbsp;&nbsp;Returns: all rows
PostgreSQL
 &nbsp;&nbsp;&nbsp;Type: Sync
&nbsp;&nbsp;&nbsp;&nbsp;Length: 4</pre>

This is a regular execution flow of the extended query protocol with P (parse), B (bind), D (describe), E (execute) and S (sync) messages in it. You'll get this flow for the first five executions of a query.

Next, if you look closely, you see that the 'Statement:' indicates a name: 'S_1'. This is the little difference that creates a prepared statement at the database side, because the statement is named. The previous parse and bind executions have no name with the Statement: property. The response for extended execution with prepared statement creation is identical to non-prepared statement creation, because during creation it cannot take advantage of the prepared statement being created. The Java prepared statement object will store the row description from this response, and use it when the prepared statement is executed the sixth time:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Frame 23: 167 bytes on wire (1336 bits), 167 bytes captured (1336 bits) on interface 0
Linux cooked capture
Internet Protocol Version 4, Src: 127.0.0.1, Dst: 127.0.0.1
Transmission Control Protocol, Src Port: 5432, Dst Port: 40808, Seq: 873, Ack: 552, Len: 99
PostgreSQL
    Type: Parse completion
    Length: 4
PostgreSQL
    Type: Bind completion
    Length: 4
PostgreSQL
    Type: Row description
    Length: 28
    Field count: 1
        Column name: now
            Table OID: 0
            Column index: 0
            Type OID: 1184
            Column length: 8
            Type modifier: -1
            Format: Text (0)
PostgreSQL
    Type: Data row
    Length: 39
    Field count: 1
        Column length: 29
        Data: 323032332d30342d31372031343a31383a33352e32393431...
PostgreSQL
    Type: Command completion
    Length: 13
    Tag: SELECT 1
PostgreSQL
    Type: Ready for query
    Length: 5
    Status: Idle (73)</pre>

This is how execution for a prepared statement looks when you execute the prepared statement java object the sixth time: no P/parse and D/describe messages, just directly a B/bind message for a named (S_1) statement, along with E/execute and S/synchronize:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Frame 24: 101 bytes on wire (808 bits), 101 bytes captured (808 bits) on interface 0
Linux cooked capture
Internet Protocol Version 4, Src: 127.0.0.1, Dst: 127.0.0.1
Transmission Control Protocol, Src Port: 40808, Dst Port: 5432, Seq: 552, Ack: 972, Len: 33
PostgreSQL
    Type: Bind
    Length: 17
    Portal:
    Statement: S_1
    Parameter formats: 0
    Parameter values: 0
    Result formats: 1
        Format: Binary (1)
PostgreSQL
    Type: Execute
    Length: 9
    Portal:
    Returns: all rows
PostgreSQL
    Type: Sync
    Length: 4</pre>

The response for a prepared statement is different (less messages): the bind needs to be acknowledged, and because we didn't call describe, we directly get the data row, and the execution is ended using the C/Command completion and Z/Ready for query responses:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Frame 25: 112 bytes on wire (896 bits), 112 bytes captured (896 bits) on interface 0
Linux cooked capture
Internet Protocol Version 4, Src: 127.0.0.1, Dst: 127.0.0.1
Transmission Control Protocol, Src Port: 5432, Dst Port: 40808, Seq: 972, Ack: 585, Len: 44
PostgreSQL
    Type: Bind completion
    Length: 4
PostgreSQL
    Type: Data row
    Length: 18
    Field count: 1
        Column length: 8
        Data: 00029c87a29022d2
PostgreSQL
    Type: Command completion
    Length: 13
    Tag: SELECT 1
PostgreSQL
    Type: Ready for query
    Length: 5
    Status: Idle (73)</pre>

In general, my experience is that the prepareThreshold is almost never changed from the default, and therefore requires a PreparedStatement object to be executed five times before it will create a database prepared statement. However, it is possible to change the prepareThreshold value to a different value to influence when to create a database prepared statement. A Java side prepared statement is always created. Setting the prepareThreshold to zero disables database side prepared statements.

Binding Values for a Prepared Statement {#h2-11-binding-values-for-a-prepared-statement}
----------------------------------------------------------------------------------------

Besides the advantage of prepared statements to skip the parse phase, the other advantage is the ability to create a statement with bind variables that can be bound to their values before sending the bind and execution messages. In many cases, reusing a statement requires the variables to change. This is an example of using a prepared statement with bind variables:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// -- 
ResultSet result; 
PreparedStatement statement = connection.prepareStatement( 
  "select table_name, table_type from information_schema.tables where table_name = ? and table_type = ?"); 
statement.setString(1, "pg_class");
statement.setString(2, "BASE TABLE"); 
result = statement.executeQuery(); 
while (result.next()) 
{ 
  System.out.println(result.getString(1) + ":" + result.getString(2)); 
} 
result.close(); 
statement.close(); 
// --</pre>

The frame sending the messages to the PostgreSQL backend looks like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Frame 14: 252 bytes on wire (2016 bits), 252 bytes captured (2016 bits) on interface 0
Linux cooked capture
Internet Protocol Version 4, Src: 127.0.0.1, Dst: 127.0.0.1
Transmission Control Protocol, Src Port: 51702, Dst Port: 5432, Seq: 266, Ack: 477, Len: 184
PostgreSQL
    Type: Parse
    Length: 118
    Statement:
    Query: select table_name, table_type from information_schema.tables where table_name = $1 and table_type = $2
    Parameters: 2
        Type OID: 1043
        Type OID: 1043
PostgreSQL
    Type: Bind
    Length: 42
    Portal:
    Statement:
    Parameter formats: 2
        Format: Text (0)
        Format: Text (0)
    Parameter values: 2
        Column length: 8
        Data: 70675f636c617373
        Column length: 10
        Data: 42415345205441424c45
    Result formats: 0
PostgreSQL
    Type: Describe
    Length: 6
    Portal:
PostgreSQL
    Type: Execute
    Length: 9
    Portal:
    Returns: all rows
PostgreSQL
    Type: Sync
    Length: 4</pre>

The important thing to see is that the statement with the parse message has two bind variable placeholders ($1 and $2), for which both the type is explicitly defined, thereby mitigating the risk of SQL injection. The bind message also specifies the bind variables, and their actual values. During the bind phase, the database plans the execution and at that point requires the data to be known in order to calculate the cost of the execution plan options for generating the plan, because the bind can change the cardinality of the plan lines it is involved with.

Prepared Statements and the Simple Query Protocol {#h2-12-prepared-statements-and-the-simple-query-protocol}
------------------------------------------------------------------------------------------------------------

Here is an oddity: what if you explicitly set usage of the simple query protocol using `properties.setProperty("preferQueryMode", "simple");` and then try to use prepared statements using a Java prepared statement object and execute it?

Purely based on logic I would say that it should fail, because the simple query protocol doesn't allow the usage of prepared statements\*. However, what happens instead is that it will execute using the simple query execution protocol, never using database prepared statements. I can see how this can solve sloppy coding, which means using prepared statements whilst it is not possible, but the problem is that with this kind of logic, what is actually executed becomes less obvious.

\*: technically, you can perform the PostgreSQL prepare command to use the simple query protocol to manually create a prepared statement using the database prepare command, and then use the execute command using the simple query protocol to execute the prepared statement, but that is not logical or obvious. This is also not what is happening during the execution of Java prepared statement objects using the simple query protocol.

The idea of having the ability to use Java PreparedStatement objects using the simple query protocol is probably that a Java side prepared statement object is independent from the database side prepared statement. When executing it using the extended query protocol during the first five executions there is no database side prepared statement, and therefore it can exist as a Java prepared statement, totally independent from a database side prepared statement.

Close Prepared Statements {#h2-13-close-prepared-statements}
------------------------------------------------------------

A commonly overlooked fact is that prepared statements should be closed after they will no longer be reused. During development this is simply good housekeeping. But, in real life with lots of prepared statements and database connections, the memory taken in the Java heap, as well as in the database backend, can be significant, or when used in a loop ever increasing. Not closing obsolete closed statements will appear in a real life application as a memory leak, because if a prepared statement is not closed, the memory it is using cannot be released.

The PostgreSQL JDBC driver uses the extended query protocol by default. The extended query protocol splits query execution into Parse, Bind and Execute, which are sent as independent messages, unlike the simple query protocol. This is not a problem, because these messages are sent within the same network packet, and do not require network roundtrips between these messages.

The alternative is the simple query protocol, which can be set using a connection property. It sends a single message to the database for query execution.

The simple and extended query protocols are roughly equally efficient. The purpose of the database execution being split into Parse, Bind and Execute messages with the extended query protocol is to allow skipping the Parse message when a database prepared statement is used.

It's important to realize that there are Java PreparedStatement objects, and database prepared statements. When Java PreparedStatements are used, these do not generate a database prepared statement directly, but require five executions on the Java side before the execution creates a database prepared statement. After this, the Parse message is skipped, resulting in less messages sent and less work being done in the database for the execution.

For reuse of PreparedStatements, it's logical that it might require different arguments to be sent. These are implemented by bind variables for the PreparedStatement object, which have to be bound before execution. Bind variables significantly lower the risk of SQL injection.
