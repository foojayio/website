---
title: "Java Thread Programming (Part 15) | Foojay Today"
slug: "java-thread-programming-part-15"
date: "2022-08-31T17:30:53+00:00"
lastmod: "2022-09-04T13:48:59+00:00"
description: "Let's learn how to do asynchronous method invocation with Callable and Future by means of a practical example."
authors:
  - "bazlur-rahman"
image: "https://foojay.io/wp-content/uploads/2022/08/Playground.java_.png"
categories:
  - "Java Core"
tags:
related_posts:
enlighterjs: true
frozen: false
---

This article will discuss how we do asynchronous method invocation with [Callable](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/util/concurrent/Callable.html) and [Future](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/util/concurrent/Future.html) with a practical example.

<img fetchpriority="high" decoding="async" class="alignnone size-medium wp-image-57278" src="/images/posts/2022/08/java-thread-programming-part-15/Playground.java_-700x227.png" alt="" width="700" height="227">

<br />

Look at the code snippet. It doesn't have anything yet, but [as we learned about Java threading](https://foojay.io/today/java-thread-programming-part-14/), we know anything we put here will run on the main thread unless we spawn a new thread from here.

Now consider a case where you work as a banker, and you need to calculate how much credit you can provide to a person.

To calculate the credit, you need to calculate that person's assets and liabilities. And then, you want to do some other tasks and pass the assets and liabilities to the credit score calculator. So let's put these thoughts into code:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">private static Credit calculateCreditForPerson(Long personId) {
     var person = getPerson(personId);
     var assets = getAssets(person);
     var liabilities = getLiabilities(person);

     doSomeImportantTask();
     return calculateCredit(assets, liabilities);
}</pre>

This is pretty sequential, and all of them are done one after one. Each method takes some time to work on. It calls the database and then waits for the result. Until the result is returned, the execution waits. This is a blocking scenario.

That means when it calls the `getPerson()` method, if it makes a network call, which is usually an IO-bound task until we get the result, the main thread would just block, and nothing would progress here. Once the result is returned, we get to the next method, which is another blocking method, as it also makes a database call over the network.

We have five methods over here. If all of them take around 200 milliseconds each to execute, that's 1000 milliseconds. While executing all the methods, the main thread is blocked most of the time without doing anything. We shouldn't really waste a resource like this. How about we optimize it so that it doesn't take the whole 1000 milliseconds?

The solution is to pass those method executions into different threads. Of course, we can create a new thread for each method directly through the new operator, but we need to get the result from the thread.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">AtomicReference person = new AtomicReference&lt;&gt;();
new Thread(() -&gt; {
    var p = getPerson(personId);
    person.set(p);
}).start();</pre>

We could do something like the above. We are essentially sharing the state between the main and newly created threads. In such a case, we use [AtomicReference](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/util/concurrent/atomic/AtomicReference.html) to update variables in a thread-safe way.

But over here, we are spawning a new thread. So each time we execute the code, we have to create multiple threads. Creating threads is an expensive operation. We should not create them on an ad-hoc basis. We should use ThreadPool instead, which we will do later. Let's see the whole code.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">private static Credit calculateCreditForPerson(Long personId) throws InterruptedException {
    var person = getPerson(personId);
    var assetRef = new AtomicReference();
    var t1 = new Thread(() -&gt; assetRef.set(getAssets(person)));

    var liabilitiesRef = new AtomicReference();
    var t2 = new Thread(() -&gt; liabilitiesRef.set(getLiabilities(person)));
    var t3 = new Thread(() -&gt; doSomeImportantTask());
    t3.start();

    t1.join();
    t2.join();

    var credit = calculateCredit(assetRef.get(), liabilitiesRef.get());

    t3.join();

    return credit;
}
</pre>

Over there, the first method call is indeed a blocking call. The main thread waits for the method to finish the work. The following two methods' invocation depends on this, so even if we execute it through another thread, we need to wait for its results.

So executing it from the main thread makes sense. The next three methods can be executed independently. They should not wait for each other. So we can pass them into three separate threads, which we did over here.

The final method of invocation is dependent on the second and third methods. So we need to wait on them. That's why we called the join method on the threads of those two methods.

And finally, before returning the credit, we join the third thread.

The code above works and takes less time, but it is very hard to understand and has too many moving parts.

Let's not create a thread for each method by ourselves; rather, we should use executors. That's best practice. Let's do that.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">private static Credit calculateCreditForPerson3(ExecutorService pool)
        throws ExecutionException, InterruptedException {
    var person = getPerson(1L);
    Future&lt;Assets&gt; assetFuture = pool.submit(() -&gt; getAssets(person));
    Future&lt;Liabilities&gt; liabilitiesFuture = pool.submit(() -&gt; getLiabilities(person));

    pool.submit(() -&gt; doSomeImportantTask());

    return calculateCredit(assetFuture.get(), liabilitiesFuture.get());
}
</pre>

To know how the future works, read part 12 of this threading series: ​​<https://foojay.io/today/java-thread-programming-part-12/>.

![](/images/posts/2022/08/java-thread-programming-part-15/Aysnchronos-Programming-1024x447.png)

The code exactly does the same thing, but it is much cleaner and better. The basic idea is that the second and third methods are being executed independently and separately in other threads.

While they are being executed, the main thread is not blocked, and we made some progress in invoking the fourth method. Indeed, when we called `future.get()` blocking call the final method, but that is necessary. It could happen that they are already finished when we get to the final method.

While the final method is executed in the main thread, the fourth method is being executed in another thread asynchronously.

So we have learned how we can write asynchronous methods using [Executors](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/util/concurrent/Executors.html), `Callable` and `Future`.

[CompletableFuture](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/util/concurrent/CompletableFuture.html) is another way to deal with asynchronicity in Java, and it is considered to be a much better way. This is how the above code can be written:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">private static Credit calculateCreditForPerson(Long personId) throws ExecutionException, InterruptedException {
   return CompletableFuture.supplyAsync(() -&gt; getPerson(personId))
           .thenComposeAsync(person -&gt; {
        final var assetFuture = CompletableFuture.supplyAsync(() -&gt; getAssets(person));
        final var liabilitiesFuture = CompletableFuture.supplyAsync(() -&gt; getLiabilities(person));
        final var importantWork = CompletableFuture.runAsync(Playground::doSomeImportantTask);
       return importantWork.thenCompose((v) -&gt; assetFuture.thenCombineAsync(liabilitiesFuture, ((assets, liabilities) -&gt; calculateCredit(assets, liabilities))));
   }).get();
}</pre>

But I wouldn't explain this in this article because it needs some background. Please wait until the next parts come out...

Until then, cheers!
