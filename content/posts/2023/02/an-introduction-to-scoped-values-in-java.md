---
title: "An Introduction to Scoped Values in Java | Foojay.io Today"
slug: "an-introduction-to-scoped-values-in-java"
date: "2023-02-20T11:32:20+00:00"
lastmod: "2023-02-20T11:32:22+00:00"
description: "The Scoped Values API allows us to store and share immutable data for a bounded lifetime, and is included since Java 20 as an incubator API."
authors:
  - "huseyin-akdogan"
image: "https://foojay.io/wp-content/uploads/2023/02/scopedvalues.png"
categories:
  - "Java Core"
  - "JEPs"
tags:
related_posts:
enlighterjs: true
frozen: false
---

After moving to the six-month release cadence, the Java language has entered a rapid development process.

While the process introduces many new features, these new features sometimes cause updates to existing APIs and sometimes result in the development of new APIs.

An example of the second is [Scoped Values](https://openjdk.org/jeps/429 "Scoped Values") which has been included in the JDK since Java 20 as an incubator API.

### Why were Scoped Values proposed? {#h3-0-why-were-scoped-values-proposed}

[Virtual threads](https://openjdk.org/jeps/425 "Virtual threads") became a part of JDK as a preview feature in Java 19.

They are a lightweight implementation of Java threads and promise dramatically reduce the effort of writing, maintaining, and observing high-throughput concurrent applications.

Virtual threads are cheap by nature.

This means that thousands or even millions of virtual threads can be used.

On the other hand, [ThreadLocal API](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/ThreadLocal.html) has been widely used since Java 1.2 for object sharing between application components without resorting to method arguments.

At this point, given the aforementioned nature of virtual threads, some problems arise.

### What is the problem? {#h3-1-what-is-the-problem}

The ThreadLocal API supports a fully general model of communication that allows any code to mutate the data by calling related methods(*eg set(), remove()*) at any time, so these variables are mutable.

In many scenarios, however, Java developers need to work with immutable objects to be passed throughout a process.

The mentioned communication model of ThreadLocal API isn't conducive to such transmission, i.e., the simple one-way transmission of immutable data from one application component to another.

In addition, when a thread-local variable is written via the set() method it is retained for the lifetime of the thread, or until code in the thread calls the remove() method.

This means that per-thread data is often retained for longer than necessary.

Hence when using large numbers of threads, or when there is an inheritance relationship between the threads the overhead of thread-local variables may be even higher.

Considering this described design flaws of thread-local variables, the drawbacks of using them with tens of thousands or even millions of virtual threads are obvious.

The Scoped Values API proposed to overcome the aforementioned potential problems. They should be preferred to thread-local variables, especially when using large numbers of virtual threads.

### What are Scoped Values and how to use them? {#h3-2-what-are-scoped-values-and-how-to-use-them}

The Scoped Values API allows us to store and share immutable data for a bounded lifetime and only the thread that wrote the data can read it.

A scoped value is a variable of type ScopedValue and is typically declared as a static final field like a thread-local variable so it can easily be reached from many components.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class PaymentGateway
{
    public static final ScopedValue&lt;PaymentRequest&gt; PAYMENT_REQUEST = ScopedValue.newInstance();

    //...
}</pre>

Once declared, a scoped value is used as shown below.

<pre class="EnlighterJSRAW" data-enlighter-language="java">import static org.jugistanbul.PaymentGateway.PAYMENT_REQUEST;

public class PaymentProcessor
{
   public static void createPaymentTask(final PaymentRequest request){
       ScopedValue.where(PAYMENT_REQUEST, request)
                   .run(() -&gt; PaymentService.getPaidByCreditCard());
   }
}</pre>

In the code snippet above, a scoped value and the object to which it is to be bound are passed to the where() method as a key and a value argument.

The run() call binds the scoped value to the current thread by providing a specific incarnation of it. That makes the scoped value accessible in getPaidByCreditCard() method.

In this way, notice that the where() and run() methods together provide a one-way sharing of data from one component to another.
> The where() is a method of the [Carrier](https://download.java.net/java/early_access/loom/docs/api/jdk.incubator.concurrent/jdk/incubator/concurrent/ScopedValue.Carrier.html "Carrier") class which is one of the inner classes of ScopedValues. It maps scoped values as keys, to values and returns a new Carrier hence the where() method can be chained.

<pre class="EnlighterJSRAW" data-enlighter-language="generic"></pre>

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class PaymentService
{
   public static void getPaidByCreditCard(){
       ValidationService.checkValidity();
}</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class ValidationService
{
   public static void checkValidity(){
       PaymentRequest paymentRequest = PaymentGateway.PAYMENT_REQUEST.get();
       checkNumber(paymentRequest.cardNumber());
   }
}</pre>

The bound scoped value can be read via the value's get() method during the lifetime of the run() method, the lambda expression, or any method called directly or indirectly from that expression.

After the run() method finishes, the binding is destroyed or reverts to its previous value when previously bound, in the current thread. That is where the question of "*What is the meaning of scoped?*" is answered.
> The value's get() call after destroyed bindings will throw an exception. You can use ScopedValue.isBound() to check if it has a binding for the current thread.

When a scoped value is written once, then is immutable, which means a caller using a scoped value can reliably pass it as a constant value to its callees in the same thread.

However, this does not mean that one callee can't share the same scoped value with a different value with its own callees in the thread. In such cases the ScopedValue API allows a new binding to be established for nested calls, this is called rebinding.

Let's say we have a service where we print the payment information after charging the payment.

We can use the current PaymentRequest instance bound to the current thread for the print process but we don't want to share sensitive information without masking it such as card number, cardholder name, etc, with the service and any method called directly or indirectly from it. This is where rebinding comes to our help.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class PaymentService
{
   public static void getPaidByCreditCard(){
       ValidationService.checkValidity();
       getPaid();
       ScopedValue.where(PaymentGateway.PAYMENT_REQUEST, maskedPaymentRequest)
       .run(() -&gt; PrintService.printPaymentInfo());
   }
}</pre>

> The return type of the run() method is void. If the printPaymentInfo() method was returning a value, we can prefer the call() method which calls a value-returned operation to handle the returned value.

In the above code snippet, the scoped value that was initially bound in createPaymentTask() method, rebinding to a new instance of PaymentRequest in getPaidByCreditCard() method. Hence, during the lifetime of the run method, the accessible object is only this new PaymentRequest instance.

In short, the Scoped Values API doesn't allow a method body to change the binding seen by the method itself(*it has no method like set()*) but allows it to change the binding seen by its callees. This guarantees a bounded lifetime for sharing of the new value.

As soon as the run() call finishes in the getPaidByCreditCard() method, the binding reverts to its previous value.

### How to enable cross-thread sharing? {#h3-3-how-to-enable-cross-thread-sharing}

Java developers can create their own threads for many reasons. In such a case, if the code running in a child thread needs to access the scoped value how can access it?

The answer is that use [Structured Concurrency](https://openjdk.org/jeps/428 "Structured Concurrency") which enables cross-thread sharing.

Structured Concurrency has been included in the JDK since Java 19 as an incubator API. It treats multiple tasks running in different threads as a single unit of work.

The principal class of the API is [StructuredTaskScope](https://download.java.net/java/early_access/loom/docs/api/jdk.incubator.concurrent/jdk/incubator/concurrent/StructuredTaskScope.html "StructuredTaskScope") and scoped values are automatically inherited by all child threads created via it.  

```

```

<pre class="EnlighterJSRAW" data-enlighter-language="java">public static void getPaidByCreditCard() throws InterruptedException, ExecutionException {

    PaymentRequest request = PaymentGateway.PAYMENT_REQUEST.get();

    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        Future&lt;Boolean&gt; validation  = scope.fork(() -&gt; ValidationService.checkValidity());
        Future&lt;Boolean&gt; account = scope.fork(() -&gt; UserService.accountChecker());

        scope.join();
        scope.throwIfFailed();

        if(validation.resultNow() &amp;&amp; account.resultNow()){
            getPaid();
            ScopedValue.where(PaymentGateway.PAYMENT_REQUEST, request.copyOf())
                       .run(() -&gt; PrintService.printPaymentInfo());
        }
    }
}</pre>

```

```

The fork() method of StructuredTaskScope starts a new thread to run the given task. In the above code snippet it is called to run the ValidationService.checkValidity() and UserService.accountChecker() methods concurrently, in their own virtual threads.

StructuredTaskScope.fork() ensures that the binding of the scoped value made in the parent thread code is automatically visible to the child thread. This is an example of scoped value inheritance and it provides enables cross-thread sharing.

Because, unlike thread-local variables, there is no copying of a parent thread's scoped value bindings to the child thread, cross-thread sharing occurs with minimal overhead.

I created a [repository](https://github.com/hakdogan/scoped-values "repository") for the scenario discussed in this article, which you can examine.

### Conclusion {#h3-4-conclusion}

The Scoped Values API allows storing and sharing immutable data for a bounded lifetime.

It is recommended to be used to overcome potential problems that may arise when using thread-local variables, especially with large numbers of virtual threads.

Scoped Values must be used with Structured Concurrency to enable cross-thread sharing.

Cross-thread sharing occurs with minimal overhead because no copying of a parent thread's scoped value bindings to the child thread.

Note that Scoped Values and Structured Concurrency are still incubator APIs, so they may still be subject to fundamental changes.

### References {#h3-5-references}

* [Scoped Values](https://openjdk.org/jeps/429 "Scoped Values")
* [Virtual Threads](https://openjdk.org/jeps/425 "Virtual Threads")
* [Structured Concurrency](https://openjdk.org/jeps/428 "Structured Concurrency")
