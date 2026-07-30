---
title: "Spring Transaction Debugging in Production with Lightrun"
slug: "spring-transaction-debugging-in-production-with-lightrun"
date: "2022-05-06T15:00:53+00:00"
lastmod: "2022-05-06T15:00:54+00:00"
description: "We use annotations to denote transactional behavior in modern Spring, so we have no code, no failure, no debugging... But is this true?"
canonical: "https://lightrun.com/tutorials/spring-transaction-debugging-in-production-with-lightrun/"
authors:
  - "shai-almog"
image: "https://foojay.io/wp-content/uploads/2022/04/Debugging-Spring-Transactions.jpg"
categories:
  - "IntelliJ IDEA"
  - "Spring"
  - "Tutorials"
tags:
related_posts:
  - "debugging-collections-streams-and-watch-renderers"
  - "debugging-tutorial-java-return-value-intellij-jump-to-line-and-more"
  - "debugging-ram-java-garbage-collection-java-heap-deep-dive-part-1"
  - "spring-boot-debugging-with-aspect-oriented-programming-aop"
enlighterjs: true
frozen: false
---

Spring makes building a reliable application much easier thanks to its declarative transaction management.

It also supports programmatic transaction management, but that's not as common.

In this article, I want to focus on the declarative transaction management angle, since it seems much harder to debug compared to the programmatic approach. This is partially true. We can't put a breakpoint on a transactional annotation.

But I'm getting ahead of myself!

What is Spring's Method Declarative Transaction Management? {#h2-0-what-is-spring-s-method-declarative-transaction-management}
------------------------------------------------------------------------------------------------------------------------------

When writing a spring method or class, we can use annotations to declare that a method or a bean (class) is transactional. This annotation lets us tune transactional semantics using attributes. This lets us define behavior such as:

* Transaction isolation levels -- lets us address issues such as dirty reads, non-repeatable reads, phantom reads, etc.
* Transaction Manager
* Propagation behavior -- we can define whether the transaction is mandatory, required, etc. This shows whether the method expects to receive a transaction and how it behaves
* readOnly attribute -- the DB does not always support a read-only transaction. But when it is supported, it's an excellent performance/reliability tuning feature

And much more.

### Isn't the Transaction Related to the Database Driver? {#h3-1-isn-t-the-transaction-related-to-the-database-driver}

The concept of transactional methods is very confusing to new spring developers. Transactions are a feature of the database driver/JDBC Connection, not of a method. Why declare it in the method?

There's more to it. Other features, such as message queues, are also transactional. We might work with multiple databases. In those cases, if one transaction is rolled back, we need to rollback all the underlying transactions. As a result, we do the transaction management in user code and spring seamlessly propagates it into the various underlying transactional resource.

### How can we Write Programmatic Transaction Management if we don't use the Database API? {#h3-2-how-can-we-write-programmatic-transaction-management-if-we-don-t-use-the-database-api}

Spring includes a transaction manager that exposes the API's we typically expect to see: begin, commit and rollback. This manager includes all the logic to orchestrate the various resources.

You can inject that manager to a typical spring class, but it's much easier to just write declarative transaction management like this Java code:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Transactional
public void myMethod() {
    // ...
}</pre>

I used the annotation on the method level, but I could have placed it on the class level. The class defines the default and the method can override it.

This allows for extreme flexibility and is great for separating business code from low level JDBC transaction details.

### Dynamic Proxy, Aspect Oriented Programming and Annotations {#h3-3-dynamic-proxy-aspect-oriented-programming-and-annotations}

The key to debugging transactions is the way spring implements this logic. Spring uses a proxy mechanism to implement the aspect oriented programming declarative capabilities. Effectively, this means that when you invoke `myMethod` on `MyObject` or `MyClass` spring creates a proxy class and a proxy object instance between them.

Spring routes your invocation through the proxy types which implement all the declarative annotations. As such, a transactional proxy takes care of validating the transaction status and enforcing it.

Debugging a Spring Transaction Management using Lightrun {#h2-4-debugging-a-spring-transaction-management-using-lightrun}
-------------------------------------------------------------------------------------------------------------------------

**IMPORTANT: I assume you're familiar with Lightrun basics. If not, please read [this](https://docs.lightrun.com/).**

Programmatic transaction management is trivial. We can just place a snapshot where it begins or is rolled back to get the status.

But if an annotation fails, the method won't be invoked and we won't get a callback.

Annotations aren't magic, though. Spring uses a proxy object, as we discussed above. That proxy mechanism invokes generic code, which we can use to bind a snapshot. Once we bind a snapshot there, we can detect the proxy types in the stack. Unfortunately, debugging proxying mechanisms is problematic since there's no physical code to debug. Everything in proxying mechanisms is generated dynamically at runtime. Fortunately, this isn't a big deal. We have enough hooks for debugging without this.

### Finding the Actual Transaction Class {#h3-5-finding-the-actual-transaction-class}

The first thing we need to do is look for the class that implements transaction functionality. Opening the IntelliJ/IDEA class view (Command-O or CTRL-O) lets us locate a class by name. Typing in "Transaction" resulted in the following view:
![image1.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1650896120559/eRLIMjgCA.png)

This might seem like a lot, but we need a concrete public class. So annotations and interfaces can be ignored. Since we only care about Spring classes, we can ignore other packages. Still, the class we are looking for was relatively low in the list, so it took me some time to find it.

In this case, the interesting class is `TransactionAspectSupport`. Once we open the class, we need to select the option to download the class source code.

Once this is done, we can look for an applicable public method. `getTransactionManager` seemed perfect, but it's a bit too bare. Placing a snapshot there provided me a hint:
![image2.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1650896165493/Fmw8Sgc45.png)

I don't have much information here but the `invokeWithinTransaction` method up the stack is perfect!

Moving on to that method, I would like to track information specific to a transaction on the `findById` method:
![image3.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1650905124053/1atfNFEx2j.png)

To limit the scope only to `findById` we add the condition:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">method.getName().equals("findById")</pre>

Once the method is hit, we can see the details of the transaction in the stack.

If you scroll further in the method, you can see ideal locations to set snapshots in case of an exception in thread, etc. This is a great central point to debug transaction failures.

One of the nice things with snapshots is that they can easily debug concurrent transactions. Their non-blocking nature makes them the ideal tool for that.

TL;DR {#h2-6-tl-dr}
-------------------

Declarative configuration in Spring makes transactional operations much easier. This significantly simplifies the development of applications and separates the object logic from low level transactional behavior details.

Spring uses class-based proxies to implement annotations. Because they are generated, we can't really debug them directly, but we can debug the classes, they use internally. Specifically: `TransactionAspectSupport` is a great example.

An immense advantage of Lightrun is that it doesn't suspend the current thread. This means issues related to concurrency can be reproduced in Lightrun. Everything discussed here can be accomplished with the [free version of Lightrun](https://lightrun.com/free).
