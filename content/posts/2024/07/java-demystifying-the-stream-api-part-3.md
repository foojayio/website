---
title: "Java: Demystifying The Stream API"
slug: "java-demystifying-the-stream-api-part-3"
date: "2024-07-05T06:57:34+00:00"
lastmod: "2024-07-05T06:57:35+00:00"
description: "Dive into the world of Stream API in Java using Lambda Expressions, Method References, and Functional Interfaces."
authors:
  - "mahendra1413"
image: "https://foojay.io/wp-content/uploads/2024/03/DALL·E-2024-03-10-13.01.01-A-conceptual-illustration-for-a-blog-post-header-about-debugging-Java-streams-using-the-peek-method.-The-image-should-visually-represent-the-concept.jpeg"
categories:
  - "Java"
  - "Java Beginner"
  - "Tutorials"
tags:
related_posts:
enlighterjs: true
frozen: false
---

In our earlier articles [part1](https://foojay.io/today/java-functional-programming/) and [part2](https://foojay.io/today/java-functional-programming-fx-part2), we have previously explored the significance of functional programming, Lambda Calculus, as well as various features such as Functional Interfaces, Lambda Expressions, and Method References.

This article delves into a crucial aspect, namely the Stream API, which JDK1.8 incorporated. It also covers how to utilize Lambda Expressions, Method References, and Functional Interfaces within Stream API functions.

So,

Succinctly, In Java, **a Stream is a collection of data that developers can process on in a declarative and functional approach**.

The **Collections Framework** closely links to the stream API. The Collections Framework stores and organizes data in the JVM's memory, while the Stream API serves as an additional framework that aids in efficiently processing the data.

To efficiently process the data, we need to implement a particular algorithm; therefore, the Stream API uses the **map-filter-reduce** algorithm in the JDK.

The **map/filter/reduce** patterns resemble iterators in that they only collect and operate on one element at a time. However, in this case, we consider a sequence of elements as a single collection or unit.

So what are these three operations for sequences,

* The **Map** operation applies a unary function to each element in a sequence, producing a new sequence with the desired results in the original order.

* The **Filter** operation each element in a sequence as a unary predicate. It stores elements that meet the predicate's criteria in memory for further processing, while keeping the source input data unchanged.

* The **Reduce** operation collates a sequence of elements using a binary function. It requires an initial value to initialize the reduction, and if the input data is empty, the return value becomes this initial value.

Streams or For Loops? {#_streams_or_for_loops}
----------------------------------------------

As a developer and architect, I aim to enhance the readability, functionality, and efficiency of my code. We can achieve these objectives in a more elegant manner using StreamAPI.

Unlike the verbose nature of iterator or imperative approach, StreamAPI enables us to write declarative approach and easily understandable code.

Generally, loops are more efficient from a performance standpoint compared to streams. However, this dynamic may shift in scenarios involving parallelism.

In intricate situations necessitating custom thread management and coordination, we use for-loops because they provide manual concurrency control.

The `java.util.stream` package includes the Stream interface, which encompasses numerous `intermediate and terminal operations`.

You can access various types of Stream such as **IntStream** , **LongStream** , and **DoubleStream**.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public interface Stream&lt;T&gt; extends BaseStream&lt;T,Stream&lt;T&gt;&gt;

public interface IntStream extends BaseStream&lt;Integer,IntStream&gt;

public interface LongStream extends BaseStream&lt;Long,LongStream&gt;

public interface DoubleStream extends BaseStream&lt;Double,DoubleStream&gt;</pre>

IntStream specializes in handling elements of primitive int type.

LongStream specialized in handling elements of long-valued elements.

DoubleStream specialized in handling elements of double-valued elements.

The streams mentioned above are suitable for both sequential and parallel aggregate operations.

***Intermediate Operations*** in the Stream API interface {#h2-1-intermediate-operations-in-the-stream-api-interface}
---------------------------------------------------------------------------------------------------------------------

**map:** Produces a stream containing the outcomes of implementing the specified function to the elements within the stream.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;R&gt; Stream&lt;R&gt; map(Function&lt;? super T, ? extends R&gt; mapper);

List&lt;Integer&gt; numbers = List.of(1, 2, 3, 4, 5);
numbers.stream().map(Math::sqrt) // squaring each of the sequence element and mapping here
                .forEach(System.out::println);
</pre>

**mapToInt:** Generates an IntStream containing the outcomes of implementing the specified function to the elements within the stream.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">IntStream mapToInt(ToIntFunction&lt;? super T&gt; mapper);

List&lt;String&gt; languages = List.of("Java", "Kotlin", "Scala");
IntStream wordsLength = languages.stream().mapToInt(String::length);
System.out.println("Sum of the words length: " + wordsLength.sum());</pre>

**mapToLong:** Creates a LongStream containing the outcomes of implementing the specified function to the elements within the stream.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">LongStream mapToLong(ToLongFunction&lt;? super T&gt; mapper);

List&lt;String&gt; prices = List.of("900", "1800", "2700");
LongStream sumOfPrices = prices.stream().mapToLong(Long::parseLong);
System.out.println("Sum of the prices: " + sumOfPrices.sum());</pre>

**mapToDouble:** Yields a DoubleStream containing the outcomes of implementing the specified function to the elements within the stream.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">DoubleStream mapToDouble(ToDoubleFunction&lt;? super T&gt; mapper);

List&lt;Double&gt; pricesOne = List.of(22.5, 23.0, 24.5, 27.0, 38.0);
DoubleStream priceDetails = pricesOne.stream().mapToDouble(Double::doubleValue);
double averagePrice = priceDetails.average().orElse(0.0);
System.out.println("Average Price: " + averagePrice);</pre>

**flatMap:** useful for converting individual elements within a stream into a new stream of elements, and subsequently merging these various streams into a unified stream.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;R&gt; Stream&lt;R&gt; flatMap(Function&lt;? super T, ? extends Stream&lt;? extends R&gt;&gt; mapper);

List&lt;List&lt;Integer&gt;&gt; numberDetails = List.of(List.of(11, 12, 13), List.of(14, 15, 16), List.of(17, 18, 19));
// Use flatMap to transform the List of Lists into a unified Stream of Integers
List&lt;Integer&gt; flattenedNumbers = numberDetails.stream()
                .flatMap(List::stream)
                .toList();
System.out.println("Flattened Number Details: " + flattenedNumbers);</pre>

**flatMapToInt:** Use flatMapToInt to flatten the List that contains Arrays into a unified IntStream.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">IntStream flatMapToInt(Function&lt;? super T, ? extends IntStream&gt; mapper);
List&lt;int[]&gt; numbersInfo = List.of(new int[]{1, 2, 3}, new int[]{4, 5, 6}, new int[]{7, 8, 9});
IntStream flattenedStream = numbersInfo.stream()
                .flatMapToInt(Arrays::stream);

System.out.println("Flattened Sum: " + flattenedStream.sum());</pre>

**filter:** This stream produces a stream containing the elements that satisfy the specified predicate.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Stream&lt;T&gt; filter(Predicate&lt;? super T&gt; predicate);

List&lt;String&gt; names = List.of("Foojay", "Mahi", "Sathya", "KC", "APJ");
List&lt;String&gt; filteredNames = names.stream()
                .filter(name -&gt; name.startsWith("A"))
                .toList();
System.out.println("Filtered Names: " + filteredNames);</pre>

Additionally, **flatMapToDouble, mapMulti, mapMultiToInt, mapMultiToLong, mapMultiToDouble, and peek** are several other methods.

Some of these methods are *stateful and involve short-circuiting intermediate operations* , such as **sorted, skip, limit, dropWhile, and takeWhile**, which businesses can utilize according to specific needs.

*T**erminal Operations***in the Stream API interface {#h2-2-terminal-operations-in-the-stream-api-interface}
------------------------------------------------------------------------------------------------------------

**forEach \& forEachOrdered:** This stream performs an action for each element and display it in Order

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">void forEach(Consumer&lt;? super T&gt; action);
void forEachOrdered(Consumer&lt;? super T&gt; action);

List&lt;Integer&gt; numberAnother = List.of(1, 2, 3, 4, 5);
numberAnother.forEach(System.out::println);

List&lt;Integer&gt; numberOrdered = List.of(2, 3, 1, 5, 4);
numberOrdered.parallelStream().forEachOrdered(System.out::println);</pre>

**reduce:** In Java Streams, we use the **reduce** method to perform a reduction on the elements of the stream by employing an associative accumulation function. We use it for **summing numbers, concatenating strings, or combining elements** into a single result.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">T reduce(T identity, BinaryOperator&lt;T&gt; accumulator);

List&lt;Integer&gt; reduceNumbers = List.of(1, 2, 3, 4, 5);
int sum = reduceNumbers.stream().reduce(0, Integer::sum);
System.out.println("Sum: " + sum);

int maxValue = reduceNumbers.stream().reduce(0, Integer::max);
System.out.println("Max Value: " + maxValue);

int minValue = reduceNumbers.stream().reduce(0, Integer::min);
System.out.println("Min Value: " + minValue);</pre>

**collect:** The Java Streams **collect** method to gather the elements of a stream into a collection or alternative data structure. It works with predefined collectors from the Collectors class, including *toList, toSet, joining,* and various others.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;R, A&gt; R collect(Collector&lt;? super T, A, R&gt; collector);

List&lt;String&gt; names = List.of("Foojay", "Mahi", "Sathya", "KC", "APJ");
List&lt;String&gt; filteredNames = names.stream()
                .filter(name -&gt; name.startsWith("A"))
                .collect(Collectors.toList());
System.out.println("Filtered Names: " + filteredNames);</pre>

**toList:** The toList default method has streamlined the process of defining the terminal operation, replacing the `collect(Collectors.toList())` syntax.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">default List&lt;T&gt; toList() {
        return (List&lt;T&gt;) Collections.unmodifiableList(new ArrayList&lt;&gt;(Arrays.asList(this.toArray())));
}

List&lt;String&gt; names = List.of("Foojay", "Mahi", "Sathya", "KC", "APJ");
List&lt;String&gt; filteredNames = names.stream()
                .filter(name -&gt; name.startsWith("A"))
                .toList();
System.out.println("Filtered Names: " + filteredNames);</pre>

Furthermore, there are various other terminal operations such as **min** , **max** , and **count()** available.

Some of these approaches involve *short-circuiting terminal operations* , such as **allMatch(), findFirst()** , and **findAny()**.

In a nutshell, Stream API facilitates a functional programming approach for handling collections. It enables chaining pipeline using**map-filter-reduce algorithm**.

Stream operations execute lazily, improving performance by running only when needed. The `parallelStream()` method allows streams to take advantage of *parallel processing, enabling concurrent execution on multi-core processors*.

Streams seamlessly integrate with Java's functional interfaces like **Predicate, Function, and Consumer** . The **Collectors** utility class offers methods for converting streams to collections, grouping data, and performing other useful tasks.

**Error handling** in streams involves catching exceptions within lambdas or converting them to unchecked exceptions.

While streams can enhance *readability* and *maintainability*, users need to exercise caution when using parallel streams and dealing with large datasets to prevent performance issues.

Happy Learning 🙂

The complete code is available over on [Github](https://github.com/bsmahi/Java8Features/blob/master/StreamAPI.java)

References {#h2-3-references}
-----------------------------

<https://docs.oracle.com/javase/8/docs/api/java/util/stream/Stream.html>
