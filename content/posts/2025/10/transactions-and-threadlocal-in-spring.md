---
title: "Transactions and ThreadLocal in Spring"
slug: "transactions-and-threadlocal-in-spring"
date: "2025-10-21T13:27:00+00:00"
lastmod: "2025-10-21T13:27:02+00:00"
description: "Though I used Spring transactions a lot via the @Transactional annotation, I never opened thought about looking at their implementation."
canonical: "https://blog.frankel.ch/transactions-threadlocal-spring/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2025/10/cover.jpeg"
categories:
  - "Spring"
tags:
related_posts:
  - "a-list-of-cache-providers"
  - "poor-mans-api"
  - "kubernetes-gateway-api"
  - "real-world-stream-collector"
frozen: false
---

Two years ago, my friend José Paumard held the talk "Concurrent and Asynchronous Programming : Loom" at the Geneva Java User Group.

In his talk, he mentioned that the Spring team would need to completely redesign their approach to transactions: his reasoning was that the transactions are implemented on top of `ThreadLocal` object and Loom's virtual threads break this approach.

I was intrigued because though I used Spring transactions a lot via the `@Transactional` annotation, I never opened thought about looking at their implementation.

It made sense, because how would you propagate the context, but I wanted to make sure. In this post, I'd like to share my findings.

Finding usage of `ThreadLocal` {#h2-0-finding-usage-of-threadlocal}
-------------------------------------------------------------------

The hardest part of the research was to find the usage itself.

I started with doing [a search](https://github.com/spring-projects/) for `ThreadLocal` on the GitHub Spring projects organizations. It returns a list of 364 different files. If you [limit the results](https://github.com/search?q=org%3Aspring-projects+ThreadLocal+repo%3Aspring-projects%2Fspring-framework&amp;type=code) to the `spring-framework` project, it shrinks down to 78 files. I had enough and asked ChatGPT:
> Find if Spring transactions uses ThreadLocal. If yes, tell me how and points to sources to prove your claims.
>
> Yes, **Spring transactions** use `ThreadLocal` as part of their transaction management mechanism. Here's an explanation of how it works:
>
> *** ** * ** ***
>
> **How Spring Transactions Use `ThreadLocal`**
>
> 1. **TransactionSynchronizationManager**:
>
> Spring's transaction management uses the `TransactionSynchronizationManager` class to manage resources such as connections and transaction synchronizations. This class relies on `ThreadLocal` to store transaction context, ensuring it is bound to the current thread.
>
> 2. **Thread-bound Transaction Context** :
>    * The `TransactionSynchronizationManager` maintains a `ThreadLocal` to hold information about:
>      * The currently active transaction.
>      * Resources associated with the transaction (e.g., JDBC connections, Hibernate sessions).
>      * Transaction synchronizations (callbacks that execute before/after commit/rollback).
>    * By binding this context to the current thread, Spring ensures that a transaction initiated in one part of the code can be propagated and recognized elsewhere on the same thread.
> 3. **PlatformTransactionManager** :
>    * When you use `PlatformTransactionManager` to start a transaction (e.g., with `@Transactional`), it checks the current thread's context for any existing transaction using `TransactionSynchronizationManager`.
> 4. **Propagation Behavior**:
>
> The propagation behaviors (`REQUIRED`, `REQUIRES_NEW`, etc.) rely on the `ThreadLocal` context to determine whether to join an existing transaction or start a new one.

Indeed, the magic happens in the `TransactionSynchronizationManager`.

Note: In the above search results, it appears on the second page, and doesn't give an inkling that it's the answer. ChatGPT was a great help!

The `TransactionSynchronizationManager` class {#h2-1-the-transactionsynchronizationmanager-class}
-------------------------------------------------------------------------------------------------

![](/images/posts/2025/10/transactions-and-threadlocal-in-spring/tx-sync-mgr-class-diagram.png)

Its Javadoc is pretty descriptive:
> Central delegate that manages resources and transaction synchronizations per thread. To be used by resource management code but not by typical application code.
>
> Supports one resource per key without overwriting, that is, a resource needs to be removed before a new one can be set for the same key. Supports a list of transaction synchronizations if synchronization is active.
>
> Resource management code should check for thread-bound resources, for example, JDBC Connections or Hibernate Sessions, via getResource. Such code is normally not supposed to bind resources to threads, as this is the responsibility of transaction managers. A further option is to lazily bind on first use if transaction synchronization is active, for performing transactions that span an arbitrary number of resources.
>
> Transaction synchronization must be activated and deactivated by a transaction manager via `initSynchronization()` and `clearSynchronization()`. This is automatically supported by `AbstractPlatformTransactionManager`, and thus by all standard Spring transaction managers, such as `JtaTransactionManager` and `DataSourceTransactionManager`.
>
> Resource management code should only register synchronizations when this manager is active, which can be checked via `isSynchronizationActive()`; it should perform immediate resource cleanup else. If transaction synchronization isn't active, there is either no current transaction, or the transaction manager doesn't support transaction synchronization.
>
> Synchronization is for example used to always return the same resources within a JTA transaction, for example, a JDBC Connection or a Hibernate Session for any given DataSource or SessionFactory, respectively.
>
--- [Class TransactionSynchronizationManager Javadoc](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/support/TransactionSynchronizationManager.html)  
In this regard, `TransactionSynchronizationManager` acts as a global variable.

Let's have a look at a simplified sequence diagram.  

How transactions use `TransactionSynchronizationManager` {#how-transactions-use-transactionsynchronizationmanager}
------------------------------------------------------------------------------------------------------------------

I'll use the `DataSourceTransactionManager`, but other Spring-provided transaction managers behave in a similar way.

![](/images/posts/2025/10/transactions-and-threadlocal-in-spring/tx-sequence-diagram-924x1024.png)  
During startup, Spring searches for all `@Transactional`-annotated methods. For each of them, it creates a proxy (either a JDK one or a CGLIB one), which wraps the real method with pre- and post-code.

1. The sequence starts when you call a `@Transactional`-annotated method
2. The proxy object gets the transaction from the concrete transaction manager, via a couple of classes, which I didn't represent. If it doesn't find it, it starts a new one.
3. The manager binds the resource, *i.e.* , stores a key-value pair: in this case, the key is the data source, the value is the `ConnectionHolder`
4. Initializes the synchronization set, *i.e.* , resets the `synchronizations` set to a new set
5. Removes the synchronization from the `ThreadLocal`
6. Removes the data source key from the `resources` map

What about Reactive transaction management? {#what-about-reactive-transaction-management}
-----------------------------------------------------------------------------------------

In Reactive Programming, tasks are executed asynchronously across multiple threads to maximize resource utilization. Since `ThreadLocal` ties data to a specific thread, Spring can't use it reliably in reactive environments. Instead, Spring's reactive transaction management uses a `Context` object associated with the reactive stream. Still, Spring designers kept the same class name-quite confusing.

![](/images/posts/2025/10/transactions-and-threadlocal-in-spring/tx-reactive-sync-mgr-class-diagram-1024x331.png)  
Notice that the `TransactionSynchronizationManager` methods are instance-scoped in the Reactive context, while they were class-scoped (`static`) in the regular one.

It's harder to create an UML sequence diagram for reactive transaction management flow because:

* I'm much less familiar with the Reactive paradigm
* The code itself is more complex
* Method chaining and `Mono` are harder to represent in a sequence diagram

In any case, Spring doesn't store the reactive context in a `ThreadLocal` but associates it in the `Mono` or `Flux` object. Spring propagates the context along with each signal. Note that **such a context is immutable** , *e.g*, updating the context creates a new instance.

Here's a quick comparison chart of how Spring passes the transaction context in regular vs. reactive paradigms.

| Aspect            | Blocking Transactions   | Reactive Transactions     |
|:------------------|:------------------------|:--------------------------|
| Resource Binding  | `ThreadLocal`           | Reactive `Context`        |
| Thread Dependence | Tied to a single thread | Propagates across threads |
| Immutability      | Mutable                 | `Context` is immutable    |

Discussion {#discussion}
------------------------

Spring's Reactive API isn't bound to a thread. Migrating the API to virtual threads isn't an issue, because it offers the `Context` object to pass data across threads.

However, the regular API offers no such commodity: Spring takes care of it using the `ThreadLocal` approach.

The Spring team faces two issues:

* Decide how to pass context in the regular context: either by continuing the "magic" or making it explicit, with potentially breaking changes
* Support both threading approaches within the same code base; some will continue using regular threads while others will migrate to virtual threads

You can already see the beginning of such work in the [VirtualThreadTaskExecutor](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/task/VirtualThreadTaskExecutor.html) introduced in Spring 6.1.

*** ** * ** ***

*Originally published at [A Java Geek](https://blog.frankel.ch/transactions-threadlocal-spring/) on October 5th, 2025*
