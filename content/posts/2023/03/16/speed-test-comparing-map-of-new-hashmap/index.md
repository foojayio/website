---
title: "Comparing Map.of() and new HashMap() in Java"
date: "2023-03-16T08:16:32+00:00"
lastmod: "2023-03-17T09:46:46+00:00"
description: "Discussion of the use of Map.of() and new HashMap() in Java, the difference between them, and the benefits of using Map.of()."
authors:
  - "bazlur-rahman"
image: "results.png"
categories:
  - "Java Core"
  - "Performance"
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "5-things-you-probably-didnt-know-about-java-concurrency"
  - "java-bytecode-simplified-journey-to-the-wonderland-part-1"
frozen: false
---

Java is a popular programming language used for developing a wide range of applications, including web, mobile, and desktop applications.

It provides many useful data structures for developers to use in their programs, one of which is the `Map` interface.

The Map interface is used to store data in key-value pairs, making it an essential data structure for many applications.

In this article, we will discuss the use of `Map.of()` and new `HashMap<>()` in Java, the difference between them, and the benefits of using Map.of().

## What is Map.of()?

[`Map.of()`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/Map.html#of()) is a method introduced in Java 9, which allows developers to create an immutable map with up to 10 key-value pairs.

It provides a convenient and concise way of creating maps, making it easier to create small maps without having to write a lot of code. `Map.of()` is an improvement over the previous way of creating small maps using the constructor of the HashMap class, which can be cumbersome and verbose.

## What is new HashMap\<\>()?

`new `[HashMap](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/HashMap.html)`<>() `is a constructor provided by the HashMap class in Java, which allows developers to create a new instance of a `HashMap`. It is used to create a mutable map, which means that the map can be modified by adding, removing, or updating key-value pairs.

It is a commonly used method for creating maps in Java, especially when dealing with larger sets of data.

## Benchmarking Map.of() and new HashMap\<\>()

To compare the performance of `Map.of()` and `new HashMap<>()` in Java, we can use benchmarking tools to measure the time taken to perform various operations on maps created using these methods. In our benchmark, we will measure the time taken to get a value from a map and the time taken to insert values into a map.

It's worth noting that our benchmarks are limited to a small set of data, such as ten items. It's possible that the results could differ for larger data sets or more complex use cases.

## The benchmarking code

```java
package ca.bazlur;

import org.openjdk.jmh.annotations.Benchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 20, time = 1)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@OperationsPerInvocation
public class MapBenchmark {
    private static final int SIZE = 10;

    private Map<Integer, String> mapOf;
    private Map<Integer, String> hashMap;

    @Setup
    public void setup() {
        mapOf = Map.of(
                0, "value0",
                1, "value1",
                2, "value2",
                3, "value3",
                4, "value4",
                5, "value5",
                6, "value6",
                7, "value7",
                8, "value8",
                9, "value9"
        );

        hashMap = new HashMap<>();

        hashMap.put(0, "value0");
        hashMap.put(1, "value1");
        hashMap.put(2, "value2");
        hashMap.put(3, "value3");
        hashMap.put(4, "value4");
        hashMap.put(5, "value5");
        hashMap.put(6, "value6");
        hashMap.put(7, "value7");
        hashMap.put(8, "value8");
        hashMap.put(9, "value9");
    }

    @Benchmark
    public void testMapOf(Blackhole blackhole) {
        Map<Integer, String> map = Map.of(
                0, "value0",
                1, "value1",
                2, "value2",
                3, "value3",
                4, "value4",
                5, "value5",
                6, "value6",
                7, "value7",
                8, "value8",
                9, "value9"
        );
        blackhole.consume(map);
    }

    @Benchmark
    public void testHashMap(Blackhole blackhole) {
        Map<Integer, String> hashMap = new HashMap<>();
        hashMap.put(0, "value0");
        hashMap.put(1, "value1");
        hashMap.put(2, "value2");
        hashMap.put(3, "value3");
        hashMap.put(4, "value4");
        hashMap.put(5, "value5");
        hashMap.put(6, "value6");
        hashMap.put(7, "value7");
        hashMap.put(8, "value8");
        hashMap.put(9, "value9");
        blackhole.consume(hashMap);
    }

    @Benchmark
    public void testGetMapOf() {
        for (int i = 0; i < 10; i++) {
            mapOf.get(i);
        }
    }

    @Benchmark
    public void testGetHashMap() {
        for (int i = 0; i < SIZE; i++) {
            hashMap.get(i);
        }
    }
}
```

## The results

```
Benchmark                    Mode  Cnt   Score   Error  Units
MapBenchmark.testGetHashMap  avgt   20  14.999 ± 0.433  ns/op
MapBenchmark.testGetMapOf    avgt   20  16.327 ± 0.119  ns/op
MapBenchmark.testHashMap     avgt   20  84.920 ± 1.737  ns/op
MapBenchmark.testMapOf       avgt   20  83.290 ± 0.471  ns/op
```

These are the benchmark results for comparing the performance of using `new HashMap<>()` and `Map.of()` in Java. The benchmark was conducted with a limited and small data set (e.g. 10).

These results show that HashMaps have slightly faster 'get' operations compared to immutable Maps created using Map.of(). However, creating an immutable Map using Map.of() is still faster than creating a HashMap.

Note that, based on your JDK distribution and computer, the benchmark results may slightly differ when you try them. However, in most cases, the results should be consistent. It's always a good idea to run your own benchmarks to ensure you make the right choice for your specific use case.

Additionally, remember that micro-benchmarks should always be taken with a grain of salt and not used as the sole factor in making a decision. Other factors, such as memory usage, thread safety, and readability of code, should also be considered.

The source code can be found here: <https://github.com/rokon12/java-map-jmh>  

In my opinion, the slight variations in performance may not hold much importance in most cases. It is essential to consider other aspects, such as the particular use case, how concise it is, well-organized code, and preferred features (for example, mutable or immutable nature), when deciding between HashMap and Map.of(). For straightforward scenarios, Map.of() might still have the upper hand regarding simplicity and brevity.  

So let's see the benefit of using Map.of() --  

## Benefits of using Map.of()

There are several benefits to using Map.of() over the new HashMap\<\>() in Java:

**Conciseness**: Map.of() provides a concise and convenient way of creating small maps in Java. This makes the code more readable and easier to maintain.

**Immutability**: Map.of() creates immutable maps, which means that once the map is created, it cannot be modified. This provides a degree of safety and security for the data stored in the map.

**Type Safety**: Map.of() provides type safety for the keys and values of the map, which helps prevent type-related errors that can occur when using new HashMap\<\>().

## Conclusion

Map.of() is a powerful and useful method introduced in Java 9, which provides a more concise way of creating small maps in Java with added benefits such as immutability and type safety.

Our benchmarking shows that the latencies of Map.of() and new HashMap\<\>() for small maps are close, with overlapping error bars, which makes it difficult to definitively conclude that one method is significantly faster than the other based on this data alone.

The benefits of using Map.of() include its conciseness, immutability, and type safety. Although the performance difference may not be significant based on the provided benchmark results, the other advantages of Map.of() make it an appealing option.

Developers should consider using Map.of() when creating small maps in Java to take advantage of these benefits.

**Edit:**   

*We conducted an initial benchmark that was somewhat flawed. However, we have since rerun the benchmark and obtained slightly different results, which we have incorporated into this article after making necessary edits. Our gratitude goes to [Sune Marcher](https://twitter.com/snemarch), who brought this to our attention, and we value their input.*
