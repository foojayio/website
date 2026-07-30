---
title: "Gatherers upgrades pipeline design pattern JEP-461"
slug: "exploring-new-features-in-jdk-23-gatherers-upgrades-pipeline-design-pattern-jep-461"
date: "2024-10-15T06:18:04+00:00"
lastmod: "2024-10-18T16:53:19+00:00"
description: "Gatherers help eliminate the need to define large error-prone Collectors, which can degrade source code maintainability."
authors:
  - "miro-wengner"
image: "https://foojay.io/wp-content/uploads/2024/10/gatherers.png"
categories:
  - "Java"
  - "Java Beginner"
  - "JDK 23"
tags:
related_posts:
  - "exploring-new-features-in-jdk-23-builder-pattern-simplicity-with-jep-455-primitive-types-in-patterns-instanceof-and-switch-preview"
  - "exploring-new-features-in-jdk-23-factory-pattern-with-flexible-constructor-bodies-with-jep-482"
  - "exploring-new-features-in-jdk-23-null-object-pattern-to-avoid-null-pointer-exception-with-jep-455"
  - "boxlang-1-14-0-boxset-is-here-boxlangs-new-first-class-set-type"
enlighterjs: true
frozen: false
---

Source-code flexibility goes hand in hand with maintainability and testability. The Java language benefits not only from its maturity but also from the fact that it is strongly typed. This may initiate possible discussion topics, as each defined variable requires its type to be known, which can lead to unnecessary verbosity in the source code (reduced by JEP-286: local variable type inference\[4\]).

Since the introduction of the Stream API in Java 8, developers have benefited from the ability to create complex transformations inside pipelines. The Stream API allows a lazily evaluated definition of how a pipeline (Example 1.- 1) should be transformed through one or multiple intermediate functions (Example 1.- 2). The sequence of intermediate functions must be terminated by a terminal function that materializes the output (Example 1.- 3).

<pre class="EnlighterJSRAW" data-enlighter-language="generic">var totalNumberOfWords =
    Stream
    .of("one", "", "two", "three", "four", "five", "", "six")     // 1
          .filter(Predicate.not(String::isEmpty))                 // 2
          .collect(Collectors.counting());                           // 3</pre>

**Example 1**.: Standard stream usage is expressive and very efficient

A Stream API meets the requirements for most standard use cases, but when an additional complex conditional transformation is required, these steps lead to a complex definition of a terminal function, which is represented by the use of a Collector.

Such collector usage can be not only difficult to understand but also difficult to maintain. In other words, there is an obvious lack of ability to define advanced intermediate operations on the pipeline.

Expanding intermediate functions with JEP-461 {#h2-0-expanding-intermediate-functions-with-jep-461}
---------------------------------------------------------------------------------------------------

JEP-461\[1\] comes with the salvage option named Gatherers. The Gatherer is an intermediate function, a functional interface (Example 2.), that can transform an input element of type *T* to an output element of type *R* . The Gatherer may potentially remember its internal state of type *A* (Example 2. - method integrator).

The ability of referring to the Internal state comes very handy when any kind of internal data lookup is required. The gatherer internal state *A* is hidden from the rest of the code as an implementation detail.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@PreviewFeature(feature = PreviewFeature.Feature.STREAM_GATHERERS)
public interface Gatherer&lt;T, A, R&gt; {
    default Supplier&lt;A&gt; initializer() {return ...};
    default Integrator&lt;A, T, R&gt; integrator();
    default BinaryOperator&lt;A&gt; combiner() {return ...}
    default BiConsumer&lt;A, Downstream&lt;? super R&gt;&gt; finisher() {...}
}</pre>

**Example 2.**: Gather is a functional interface that defines a stages, where type A represent an internal state

One of the newly introduced goals of gatherer can be considered to simplify the use of parallelism inside intermediate functions. The interface comes with a method *combiner()* (Example 2.) that operates on the gatherer internal state.

Importantly, when the initial stream supports parallel execution the *combiner()* method is executed in parallel, in all other cases the *combiner()* method is executed sequentially. This can be useful when the data stream can not support parallelism by nature of the transformation.

Anytime the gatherer is invoked it creates a *Downstream* object. The *Downstream* instance represents an output of type *R* passed into the next evaluation stage. The gatherer initiates its internal state by *initializer()* method and invokes an *integrator()* method. Invoking the *finisher()* method passes downstream objects into the output (Example 3.)

<pre class="EnlighterJSRAW" data-enlighter-language="generic">record WindowFixed&lt;T&gt;(int windowSize) implements Gatherer&lt;T, ArrayList&lt;T&gt;, List&lt;T&gt;&gt; {
   public WindowFixed {
       if (windowSize &lt; 1)
           throw new IllegalArgumentException("window size must be positive");
   }
   @Override
   public Supplier&lt;ArrayList&lt;T&gt;&gt; initializer() {
       return () -&gt; new ArrayList&lt;&gt;(windowSize);
   }
   @Override
   public Integrator&lt;ArrayList&lt;T&gt;, T, List&lt;T&gt;&gt; integrator() {
       return Gatherer.Integrator.ofGreedy((window, element, downstream) -&gt; {
           window.add(element);
           if (window.size() &lt; windowSize)
               return true;
           var result = new ArrayList&lt;T&gt;(window);
           window.clear();
           return downstream.push(result);
       });
   }
   @Override
   public BiConsumer&lt;ArrayList&lt;T&gt;, Downstream&lt;? super List&lt;T&gt;&gt;&gt; finisher() {
       return (window, downstream) -&gt; {
           if (!downstream.isRejecting() &amp;&amp; !window.isEmpty()) {
               downstream.push(new ArrayList&lt;T&gt;(window));
               window.clear();
           }
       };
   }
}

var list  = Stream.of(1,2,3,4,5,6,7,8)
       .gather(new WindowFixed(3))
       .gather(new WindowFixed(2))
       .collect(Collectors.toList());
System.out.println("Result:" + list);</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Output: Result:[[[1, 2, 3], [4, 5, 6]], [[7, 8]]]</pre>

**Example 3** .: The *FixedWindow* gatherer operates on a stream of types *T* with internal state of type *A* as *ArrayList* and output type *List* . *Collectors.toList()* is the terminal operation and the moment the stream materializes

Collection of build-in gatherers {#h2-1-collection-of-build-in-gatherers}
-------------------------------------------------------------------------

JEP-461 comes with collection of build-in gatherers which may serve main of purposes without requirement creating custom ones (Example 4.):

1. ***fold(...)*** : performs stateful order many-to-one reduction transformation
2. ***mapConcurrent(...)*** : is a stateful one-to-one which may invoke the supplied function concurrently
3. ***scan(...)*** : performs stateful one-to-one incremental accumulation where initial states is obtained from the *Supplier* and in each next stage *BiConsumer* is performed
4. ***windowFixed(...)***: performs many-to-many stateful transformation where elements are separated into windows
5. ***windowSliding(...)***: performs many-to-many stateful order transformation where each output window contains the previous state and adds one new element from the stream.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">var fold1 = Stream.of(1,2,3,4,5,6,7,8,9)
       .gather(
               Gatherers.fold(() -&gt; "", (string, number) -&gt; string + number)
       )
       .collect(Collectors.toSet());

var scan1 = Stream.of(1,2,3,4)
       .gather(
               Gatherers.scan(() -&gt; "", (string, number) -&gt; string + number)
       )
       .toList();

var slidingWindow2 =
       Stream.of(1,2,3,4).gather(Gatherers.windowSliding(2)).toList();
</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Output:
Result fold1:[123456789]
Result scan1:[1, 12, 123, 1234]
Result slidingWindow2:[[1, 2], [2, 3], [3, 4]]</pre>

**Example 4**.: Some of build-in gatherers and their outputs

Conclusion {#h2-2-conclusion}
-----------------------------

JEP-461 can help meet the functional requirements of today's industry, where huge amounts of data need to be processed and analyzed.

Gatherers help eliminate the need to define large error-prone Collectors, which can degrade source code maintainability.

They clearly contribute to maintaining the purpose of the pipeline design pattern\[3\] and transparently applying the intermediate transformations, while their internal state does not escape the scope of the materialized pipeline.

In addition, each introduced gatherer can be tested within the scope of its functionality.

JEP-461: Stream Gatherers is another great example of how to move the Java platform forward\[2\] while meeting industry needs.

References {#h2-3-references}
-----------------------------

[\[1\]JEP-473: Stream Gatherers (Second Preview)](https://openjdk.org/jeps/473)  
[\[2\]Java 23 Has Arrived, And It Brings a Truckload of Changes](https://foojay.io/today/java-23-has-arrived-and-it-brings-a-truckload-of-changes/)  
[\[3\] Practical Design Patterns for Java Developers](https://amzn.eu/d/a3CsBu7)  
[\[4\]JEP-286: Local-Variable Type Inference](https://openjdk.org/jeps/286)
