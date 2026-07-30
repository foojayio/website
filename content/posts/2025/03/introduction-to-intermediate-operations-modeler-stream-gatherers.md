---
title: "Introduction to intermediate operations modeler: Stream Gatherers"
slug: "introduction-to-intermediate-operations-modeler-stream-gatherers"
date: "2025-03-19T13:04:14+00:00"
lastmod: "2025-03-19T13:04:15+00:00"
description: "Gatherers is a new and powerful API that enhances the Stream API by modeling intermediate operations and allowing the definition of custom intermediate operations."
authors:
  - "huseyin-akdogan"
image: "/images/posts/2025/03/introduction-to-intermediate-operations-modeler-stream-gatherers/gatherer.png"
categories:
  - "Java"
tags:
related_posts:
  - "exploring-new-features-in-jdk-23-gatherers-upgrades-pipeline-design-pattern-jep-461"
  - "foojay-podcast-68"
  - "java-24-whats-new"
  - "exploring-new-features-in-jdk-23-just-write-and-run-prototyping-with-jep-477-not-only-for-beginners"
enlighterjs: true
frozen: false
---

**Java is a programming language with many language features, specifications, and APIs. Even among experienced Java developers, being aware of all of these is quite rare. If a study were conducted, we might come across Java developers who have never worked with Threads, never used JPA, or never developed custom annotations. However, is there a Java developer who has worked with Java 8 or later but has never used the Stream API? I highly doubt it.**

Gatherers is a powerful extension of the Stream API that introduces support for customized intermediate operations. Initially introduced as a preview feature in JDK 22, it became a standard feature in JDK 24.

### What are Gatherers? {#h3-0-what-are-gatherers}

Gatherers were developed to model intermediate operations in the Stream API. Just as a collector models a terminal operation, a gatherer is an object that models an intermediate operation. Gatherers support the characteristics of intermediate operations---they can push any number of elements to the stream they produce, maintain an internal mutable state, short-circuit a stream, delay consumption, be chained, and execute in parallel.

For this reason, as stated in [JEP 485](https://openjdk.org/jeps/485 "JEP 485")
> In fact every stream pipeline is, conceptually, equivalent to  
>
> source.gather(...).gather(...).gather(...).collect(...)source.gather(...).gather(...).gather(...).collect(...)

The `java.util.stream.Gatherer` interface, which models a gatherer, has three type parameters.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public interface Gatherer&lt;T, A, R&gt; { … }</pre>

`T` represents the input element.  
`A` represents the potential mutable state object.  
`R` represents the output that will be pushed downstream.

A gatherer is built upon four key elements:

<pre class="EnlighterJSRAW" data-enlighter-language="java">Supplier&lt;A&gt; initializer();
Integrator&lt;A, T, R&gt; integrator();
BinaryOperator&lt;A&gt; combiner();
BiConsumer&lt;A, Downstream&lt;? super R&gt;&gt; finisher();</pre>

`Initializer` -- A function that produces an instance of the internal intermediate state.  
`Integrator` -- Integrates a new element into the stream produced by the Gatherer.  
`Combiner` -- A function that accepts two intermediate states and merges them into one. Supporting parallel execution.  
`Finisher` -- A function that allows performing a final action at the end of input elements.

Among these four elements, only the Integrator is mandatory because it has the role of integrating a new element into the stream produced by the Gatherer. The other elements may or may not be required, depending on the operation you intend to model, making them optional.

### Creating a Gatherer {#h3-1-creating-a-gatherer}

Gatherers are created using factory methods, or you can implement the Gatherer interface. Depending on the operation you want to model, you can use the overloaded variants of `Gatherer.of` and `Gatherer.ofSequential`.

<pre class="EnlighterJSRAW" data-enlighter-language="java">var uppercaseGatherer = Gatherer.&lt;String, String&gt;of((state, element, downstream)
-&gt; downstream.push(element.toUpperCase()));</pre>

The example gatherer above calls toUpperCase on an input element of type String and pushes the result downstream. This gatherer is equivalent to the following map operation.

<pre class="EnlighterJSRAW" data-enlighter-language="java">Stream.of("a", "b", "c", "d", "e", "f", "g")
   .map(String::toUpperCase)
   .forEach(System.out::print);</pre>

The Stream interface now includes a method called `gather()`, which accepts a Gatherer parameter. We can use it by passing the gatherer we created.

<pre class="EnlighterJSRAW" data-enlighter-language="java">Stream.of("a", "b", "c", "d", "e", "f", "g")
    .gather(uppercaseGatherer) 
    .forEach(System.out::print);</pre>

### Built-in Gaterers {#h3-2-built-in-gaterers}

The `java.util.stream.Gatherers` class is a factory class that contains predefined implementations of the `java.util.stream.Gatherer` interface, defining five different gatherers.

* [windowFixed](https://cr.openjdk.org/~vklang/gatherers/api/java.base/java/util/stream/Gatherers.html#windowFixed(int))  
  It is a many-to-many gatherer which groups input elements into lists of a supplied size, emitting the windows downstream when they are full.
* [windowSliding](https://cr.openjdk.org/~vklang/gatherers/api/java.base/java/util/stream/Gatherers.html#windowSliding(int))  
  It is a many-to-many gatherer which groups input elements into lists of a supplied size. After the first window, each subsequent window is created from a copy of its predecessor by dropping the first element and appending the next element from the input stream.
* [fold](https://cr.openjdk.org/~vklang/gatherers/api/java.base/java/util/stream/Gatherers.html#fold(java.util.function.Supplier,java.util.function.BiFunction))  
  It is a many-to-one gatherer which constructs an aggregate incrementally and emits that aggregate when no more input elements exist.
* [scan](https://cr.openjdk.org/~vklang/gatherers/api/java.base/java/util/stream/Gatherers.html#scan(java.util.function.Supplier,java.util.function.BiFunction))  
  It is a one-to-one gatherer which applies a supplied function to the current state and the current element to produce the next element, which it passes downstream.
* [mapConcurrent](https://cr.openjdk.org/~vklang/gatherers/api/java.base/java/util/stream/Gatherers.html#mapConcurrent(int,java.util.function.Function))  
  It is a one-to-one gatherer which invokes a supplied function for each input element concurrently, up to a supplied limit. The function executes in Virtual Thread.

All of the above gatherers are stateful. Fold and Scan are very similar to the Stream reduce operation. The key difference is that both can take an input of type T and produce an output of type R, and their identity element is mandatory, not optional.

### Create your own Gatherer {#h3-3-create-your-own-gatherer}

Let's see how we can write our custom gatherer using a real-world scenario. Imagine you are processing a system's log stream. Each log entry represents an event, and it is evaluated based on certain rules to determine whether it is anomalous. The rule and scenario are as follows.

* **Rule:** An event (log entry) is considered anomalous if it exceeds a certain threshold or contains an error.
* **Scenario:** If an error occurs and is immediately followed by several anomalous events (*three in a row, e.g*), they might be part of a failure chain. However, if a "normal" event appears in between, the chain is broken.

In this case, we can write a gatherer that processes a log stream and returns only the uninterrupted anomalous events.
> INFO, ERROR, ERROR, INFO, WARNING, ERROR, ERROR, ERROR, INFO, DEBUG

Let's assume that the object in our log stream is structured as follows.

<pre class="EnlighterJSRAW" data-enlighter-language="java">class LogWrapper { 

    enum Level{ 
         INFO, 
         DEBUG, 
         WARNING, 
         ERROR 
    } 

   private Level level; 
   private String details;
}</pre>

The object has a level field representing the log level. The details field represents the content of the log entry.

We need a stateful gatherer because we must retain information about past events to determine whether failures occur consecutively. To achieve this, the internal state of our gatherer can be a `List<LogWrapper>`.

<pre class="EnlighterJSRAW" data-enlighter-language="java">static Supplier&lt;List&lt;LogWrapper&gt;&gt; initializer() { 
   return ArrayList::new; 
}</pre>

The object returned by the `initializer()` corresponds to the second parameter explained earlier in the type parameters of the Gatherer interface.

<pre class="EnlighterJSRAW" data-enlighter-language="java">static Integrator&lt;List&lt;LogWrapper&gt;, LogWrapper, String&gt; integrator(final int threshold) { 

    return ((internalState, element, downstream) -&gt; { 
        if(downstream.isRejecting()){ 
            return false; 
        } 

        if(element.getLevel().equals(LogWrapper.Level.ERROR)){ 
            internalState.add(element); 
        } else {

            if(internalState.size() &gt;= threshold){ 
                internalState.stream().map(LogWrapper::getDetails).forEach(downstream::push); 
            } 

            internalState.clear(); 
        } 

        return true; 
    }); 
}</pre>

The integrator will be responsible for integrating elements into the produced stream. The third parameter of the integrator represents the downstream object.

We check whether more elements are needed by calling the `isRejecting()`, which determines if the next stage no longer wants to receive elements. If this condition is met, we return false.

If the integrator returns false, it performs a `short-circuit` operation similar to intermediate operations like `allMatch`, `anyMatch`, and `noneMatch` in the Stream API, indicating that no more elements will be integrated into the stream.

If `isRejecting()` returns false, we check whether the level value of our stream element, LogWrapper, is ERROR. If the level is ERROR, we add the object to our internal state. If the level is not ERROR, we then check the size of our internal state.

If the size exceeds or is equal to the threshold, we push the LogWrapper objects stored in the internal state downstream. If not, we don't.
> I want you to pay attention to two things here. Pushing an element downstream or not, as per the business rule, is similar to what filter() does. Accepting an input of type LogWrapper and producing an output of type String is similar to what map() does.

After that, according to our business rule, we clear the internal state and return true to allow new elements to be integrated into the stream.

<pre class="EnlighterJSRAW" data-enlighter-language="java">static BinaryOperator&lt;List&lt;LogWrapper&gt;&gt; combiner() { 
    return (_, _) -&gt; { 
        throw new UnsupportedOperationException("Cannot be parallelized"); 
    }; 
}</pre>

To prevent our gatherer from being used in a parallel stream, we define a combiner, even though it is not strictly required. This is because our gatherer is inherently designed to work as expected only in a sequential stream.

<pre class="EnlighterJSRAW" data-enlighter-language="java">static BiConsumer&lt;List&lt;LogWrapper&gt;, Downstream&lt;? super String&gt;&gt; finisher(final int threshold) { 
    return (state, downstream) -&gt; { 
        if(!downstream.isRejecting() &amp;&amp; state.size() &gt;= threshold){ 
            state.stream().map(LogWrapper::getDetails).forEach(downstream::push); 
        } 
    }; 
}</pre>

Finally, we define a finisher to push any remaining stream elements that have not yet been emitted downstream.

If `isRejecting()` returns false and the size of the internal state is greater than or equal to the threshold, we push the LogWrapper objects stored in the internal state downstream.

When we use this gatherer on data
>
> ```
> ERROR,   Process ID: 191, event details ...
> INFO,    Process ID: 216, event details ...
> DEBUG,   Process ID: 279, event details ...
> ERROR,   Process ID: 312, event details ...
> WARNING, Process ID: 340, event details ...
> ERROR,   Process ID: 367, event details ...
> ERROR,   Process ID: 389, event details ...
> INFO,    Process ID: 401, event details ...
> ERROR,   Process ID: 416, event details ...
> ERROR,   Process ID: 417, event details ...
> ERROR,   Process ID: 418, event details ...
> WARNING, Process ID: 432, event details ...
> ERROR,   Process ID: 444, event details ...
> ERROR,   Process ID: 445, event details ...
> ERROR,   Process ID: 446, event details ...
> ERROR,   Process ID: 447, event details ...
> ```

similar to the one above, we get the following result.
> Process ID: 416, event details ...  
>
> Process ID: 417, event details ...  
>
> Process ID: 418, event details ...  
>
> Process ID: 444, event details ...  
>
> Process ID: 445, event details ...  
>
> Process ID: 446, event details ...  
>
> Process ID: 447, event details ...

The code example is accessible in the [GitHub repository](https://github.com/hakdogan/stream-gatherers).

### Conclusion {#h3-4-conclusion}

Gatherers is a new and powerful API that enhances the Stream API by modeling intermediate operations and allowing the definition of custom intermediate operations.

A gatherer supports the features that intermediate operations have, it can push any number of elements to the resulting stream, maintain an internal mutable state, short-circuit a stream, delay consumption, be chained, and execute in parallel.

### References {#h3-5-references}

* [JEP 485](https://openjdk.org/jeps/485 "JEP 485")
* [cr.openjdk.org](https://cr.openjdk.org/~vklang/Gatherers.html "cr.openjdk.org")
