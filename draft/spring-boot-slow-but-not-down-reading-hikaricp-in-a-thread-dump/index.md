---
title: "Spring Boot slow but not down: reading HikariCP in a thread dump"
date: "2026-01-01"
description: "A Spring Boot service that runs out of database connections stays up and times out. How to read the HikariCP queue and the connection holders in a thread dump."
authors:
  - "felipe-maschio"
image: "hikaricp-thread-dump.jpg"
categories:
  - "Java"
  - "Spring"
  - "Databases"
  - "Debugging"
related_posts:
  - "how-i-got-a-thread-dump-from-a-timed-out-github-actions-test"
  - "what-jstack-misses-when-your-java-service-uses-virtual-threads"
  - "virtual-thread-pinning-field-guide"
  - "masking-a-jvm-thread-dump-without-breaking-the-analysis"
---

A Spring Boot service runs out of database connections, but the process stays up. CPU usage is low, the liveness probe is green, and requests that need the database wait about 30 seconds before failing with `SQLTransientConnectionException`. From the outside, it looks like a slow database. Sometimes the application is just holding connections while it waits for something else.

A thread dump helps separate those cases. You can see which requests are waiting for a connection, then investigate what is keeping the others busy. I built [ThreadMine](https://threadmine.dev/en), a thread dump analyzer, and the dump below is one of its test cases, but you can follow the whole investigation with `jcmd` and a text editor.

## The dump

To get a dump I could publish, I reproduced the situation with HikariCP 6.3.3, PostgreSQL and JDK 25. The pool has 4 connections, and there are 20 request threads named like Tomcat workers. Three requests run a slow query. A fourth runs its query and then calls a payment partner without returning its connection. The other 16 arrive after that and have to wait.

I kept the numbers small so the dump fits in a post. With Spring Boot defaults it would be Tomcat's 200 worker threads against Hikari's 10 connections, and the investigation is the same.

```bash
jcmd <pid> Thread.print -l > dump-1.txt
```

## Start with the waiting threads

Sixteen threads look like this:

```text
"http-nio-8080-exec-5" #34 [3792] prio=5 os_prio=0 cpu=0.00ms elapsed=5.54s tid=0x00000083ade61630 nid=3792 waiting on condition  [0x00000083afbce000]
   java.lang.Thread.State: TIMED_WAITING (parking)
	at jdk.internal.misc.Unsafe.park(java.base@25.0.2/Native Method)
	- parking to wait for  <0x00000006219a0920> (a java.util.concurrent.SynchronousQueue$Transferer)
	at java.util.concurrent.locks.LockSupport.parkNanos(java.base@25.0.2/LockSupport.java:408)
	at java.util.concurrent.LinkedTransferQueue$DualNode.await(java.base@25.0.2/LinkedTransferQueue.java:452)
	at java.util.concurrent.LinkedTransferQueue.xfer(java.base@25.0.2/LinkedTransferQueue.java:613)
	at java.util.concurrent.SynchronousQueue.xfer(java.base@25.0.2/SynchronousQueue.java:235)
	at java.util.concurrent.SynchronousQueue.poll(java.base@25.0.2/SynchronousQueue.java:338)
	at com.zaxxer.hikari.util.ConcurrentBag.borrow(ConcurrentBag.java:162)
	at com.zaxxer.hikari.pool.HikariPool.getConnection(HikariPool.java:160)
	at com.zaxxer.hikari.pool.HikariPool.getConnection(HikariPool.java:142)
	at com.zaxxer.hikari.HikariDataSource.getConnection(HikariDataSource.java:99)
	at Repro.lambda$hikari$0(Repro.java:14)
```

`HikariPool.getConnection` couldn't get a free connection, so `ConcurrentBag.borrow` parked the thread on a `SynchronousQueue`. The state is `TIMED_WAITING` because the wait has a deadline: `connectionTimeout`, which defaults to 30000 ms. That's where the 30-second delay comes from.

`grep -c 'ConcurrentBag.borrow' dump-1.txt` counts 16 matching stacks here. The application frames below `getConnection` help identify the affected requests. Once I know what's queuing, I'd move on to the threads that already have connections.

It's easy to mistake this stack for an idle worker: cached thread pools also park in `SynchronousQueue.poll`. You need to read down to `ConcurrentBag.borrow` to see what the thread is waiting for. ThreadMine missed that distinction when I first ran this dump through it. It flagged stuck threads without mentioning the connection pool.

## Find what is keeping the connections busy

In this reproduction, all 4 connections are checked out. The dump doesn't label their owners, so we need to inspect the other request threads and follow their code paths.

Three are inside the JDBC driver:

```text
"http-nio-8080-exec-1" #29 [27008] prio=5 os_prio=0 cpu=0.00ms elapsed=5.70s tid=0x00000083ade0b0e0 nid=27008 runnable  [0x00000083af5ce000]
   java.lang.Thread.State: RUNNABLE
	at sun.nio.ch.Net.poll(java.base@25.0.2/Native Method)
	at sun.nio.ch.NioSocketImpl.park(java.base@25.0.2/NioSocketImpl.java:190)
	...
	at org.postgresql.core.PGStream.receiveChar(PGStream.java:477)
	at org.postgresql.core.v3.QueryExecutorImpl.processResults(QueryExecutorImpl.java:2175)
	at org.postgresql.core.v3.QueryExecutorImpl.execute(QueryExecutorImpl.java:372)
	...
	at com.zaxxer.hikari.pool.ProxyStatement.execute(ProxyStatement.java:95)
	at com.zaxxer.hikari.pool.HikariProxyStatement.execute(HikariProxyStatement.java)
	at Repro.lambda$hikari$0(Repro.java:15)
```

The state is `RUNNABLE`, but the stack shows a socket wait for PostgreSQL's response, and `cpu=0.00ms` on the first line confirms the thread isn't burning CPU. For these three requests, I'd go to `pg_stat_activity` and check query duration and wait events, including whether another transaction is blocking them.

The fourth thread looks different:

```text
"http-nio-8080-exec-4" #33 [60220] prio=5 os_prio=0 cpu=0.00ms elapsed=5.58s tid=0x00000083ade5fec0 nid=60220 waiting on condition  [0x00000083af9ce000]
   java.lang.Thread.State: TIMED_WAITING (sleeping)
	at java.lang.Thread.sleepNanos0(java.base@25.0.2/Native Method)
	at java.lang.Thread.sleepNanos(java.base@25.0.2/Thread.java:509)
	at java.lang.Thread.sleep(java.base@25.0.2/Thread.java:540)
	at Repro$PartnerClient.chargeCard(Repro.java:9)
	at Repro.lambda$hikari$0(Repro.java:15)
```

This request has already run its statement and is waiting for the payment partner with its connection still checked out. Nothing in the stack mentions Hikari, JDBC or PostgreSQL. I used `Thread.sleep` to simulate the delay; in a real service, you'd expect to see the HTTP client waiting for a response.

In Spring, look for `TransactionAspectSupport.invokeWithinTransaction` below the business method. With Spring Boot's defaults the connection is borrowed when the transaction begins, so a thread inside `@Transactional` that is sitting in an HTTP client or a `Thread.sleep` is holding a connection it isn't using. The exception is a setup with lazy acquisition, such as `spring.datasource.hikari.auto-commit=false` or a `LazyConnectionDataSourceProxy`. There the transaction only borrows a connection when it runs its first statement.

Also check `spring.jpa.open-in-view`, which is `true` by default. It keeps the Hibernate session open until the request completes, and Spring configures Hibernate to hold a connection, once acquired, until the session closes. The connection borrowed inside the service method then goes back to the pool only after the response is written, with no transaction frame left on the stack.

## Check the pool's numbers

Compare the stacks with Hikari's active, idle and waiting counts. In this example, we can account for all 4 connections: 3 waiting on PostgreSQL and 1 held during the partner call. The other 16 requests can't proceed until one comes back.

If you can't account for the active connections, check scheduled jobs and `@Async` executors too. A leak is another possibility. Code that borrowed a connection and never closed it may have moved on, so its current stack won't explain the missing connection.

Since HikariCP 5.1.0, the timeout exception ends with the pool's own counts, such as `(total=10, active=10, idle=0, waiting=12)`. Check those alongside the dump. If the pool hasn't reached its configured size, look for connection-creation failures. If `total=0`, start with the nested exception and connection logs.

Take another dump a few seconds later, while requests are still waiting. I've been fooled by a single dump more than once. If the same thread is in the same frame in both, you have a specific call to look at. If the holders are different threads each time and all of them are in the driver, nothing is stuck: queries are slow in general, or the pool is too small for the traffic. A worker can also finish one request and reach the same code in the next, so check the query duration in `pg_stat_activity` or a request trace before calling a thread stuck.

## What I'd change first

I'd move the partner call out of the database transaction, because waiting for the partner shouldn't occupy a database connection. Keep the transaction around the database operations that need to commit together, and make the payment call before or after it. The charge and the database write no longer fail together after that, so you have to decide what happens when one succeeds and the other doesn't.

I'd also set `spring.jpa.open-in-view=false` and fetch the response data within the service transaction. Check lazy-loaded associations when doing this. Serialization may currently be loading data that the service never fetched, and with the session closed that becomes a `LazyInitializationException`.

Then check timeouts: a statement timeout for queries, and connect and read timeouts for the HTTP client. Hikari's `connectionTimeout` only limits how long a caller waits to borrow a connection. It won't stop the work holding one.

For future incidents, `spring.datasource.hikari.leak-detection-threshold` helps locate long checkouts. When a connection stays out longer than the threshold, Hikari logs `Apparent connection leak detected` with the stack that borrowed it. Set the threshold above your slowest normal request (values under 2000 ms are ignored), because a slow but legitimate operation triggers it too.

With Micrometer enabled, watch `hikaricp.connections.pending` for sustained waiting and `hikaricp.connections.usage` for how long checkouts last.

Pool size comes last. More connections can help if the database has spare capacity, but they also let the application send more work to it at once. If the problem is slow queries or connections that never return, increasing `spring.datasource.hikari.maximum-pool-size` can make the database busier or just postpone the next timeout. HikariCP's [pool sizing notes](https://github.com/brettwooldridge/HikariCP/wiki/About-Pool-Sizing) argue for smaller pools than most people expect.

## Checking the dump in ThreadMine

You can paste the dump into ThreadMine's [free analyzer](https://threadmine.dev/en/analyze), with no signup. For this test case, it reports "Connection pool exhausted: 16 threads waiting for a connection from HikariCP" and lists the waiting threads.

That gets you to the pool problem. To find the partner call holding a connection, you still need to inspect the remaining threads and the code around `chargeCard`, as we did here.

If you're starting from the exception, I also keep a shorter checklist for [`HikariPool-1 - Connection is not available`](https://threadmine.dev/en/resources/hikaripool-connection-is-not-available).
