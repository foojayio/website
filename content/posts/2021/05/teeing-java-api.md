---
title: "Introducing Teeing: A Hidden Gem in the Java API"
slug: "teeing-java-api"
date: "2021-05-12T07:15:07+00:00"
lastmod: "2021-08-23T13:05:39+00:00"
description: "teeing() method returns a Collector, a composite of two downstream collectors. There will be a single Collector and a single pass in the end."
canonical: "https://blog.frankel.ch/teeing-java-api/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2021/05/letter-3038384_1280.jpg"
categories:
  - "Uncategorized"
tags:
related_posts:
  - "optional-stream"
enlighterjs: true
frozen: false
---

Last week, I [described a use-case](https://blog.frankel.ch/real-world-stream-collector/) for a custom Stream `Collector`. I received a intriguing comment on Twitter:

<img fetchpriority="high" decoding="async" class="aligncenter wp-image-44873 size-medium" src="/images/posts/2021/05/teeing-java-api/Screenshot-2021-05-09-at-15.31.46-700x171.png" alt="" width="700" height="171">

<br />

Hats off to you, Miguel! Your comment revealed a method I didn't know!

So I decided to investigate what is the `teeing()` method about.
> Returns a Collector that is a composite of two downstream collectors. Every element passed to the resulting collector is processed by both downstream collectors, then their results are merged using the specified merge function into the final result.
>
> The resulting collector functions do the following:
>
> * supplier: creates a result container that contains result containers obtained by calling each collector's supplier
> * accumulator: calls each collector's accumulator with its result container and the input element
> * combiner: calls each collector's combiner with two result containers
> * finisher: calls each collector's finisher with its result container, then calls the supplied merger and returns its result.
>
> -- [JavaDocs](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/stream/Collectors.html#teeing(java.util.stream.Collector,java.util.stream.Collector,java.util.function.BiFunction))

We can indeed replace our custom `Collector` with two simple `Collector` implementations, one aggregating price rows and the other summing the cart's price.

Let's look at the final code and explain it line by line.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public PriceAndRows getPriceAndRows(Cart cart) {
  return cart.getProducts()
      .entrySet()
      .stream()
      .map(CartRow::new)                           // 1
      .collect(Collectors.teeing(                  // 2
          Collectors.reducing(                     // 3
              BigDecimal.ZERO,                     // 3.1
              CartRow::getRowPrice,                // 3.2
              BigDecimal::add),                    // 3.3
          Collectors.toList(),                     // 4
          PriceAndRows::new                        // 5
      ));
}
</pre>

1. Map each `Entry` to a `CartRow`
2. Call the `teeing()` method
3. The first collector computes the price. It's a simple `reducing()` call, with:
   1. The starting element
   2. A function to extract a `Price` from a `CartRow`
   3. A `BinaryOperator` to add two prices together
4. The second collector aggregates the `CartRow` into a list
5. Finally, the last parameter creates a new object that aggregates the results from the first and the second collector

On the implementation side, `teeing()`:

1. Extracts each of the individual components of both `Collector`, *i.e.* , `supplier(), `accumulator()`, `combiner()` and `finisher()\`
2. Pairs them side-by-side
3. Creates a single new `Collector` by passing the pairs

Thus, there will be a single `Collector` **and** a single pass in the end.

I hope this post made you consider using `teeing()` *before* creating a custom `Collector`. Thanks again to Miguel!

By the way, I'm always happy to learn new things. In case you've got insights to share, you can use the commenting system below or Twitter.

The complete source code for this post can be found on [Github](https://github.com/ajavageek/custom-collector/tree/teeing) in Maven format.

**To go further:**

* [A real-world example of a Stream Collector](https://blog.frankel.ch/real-world-stream-collector/)
* [Custom collectors in Java 8](https://blog.frankel.ch/custom-collectors-java-8/)
* [Teeing Javadoc](https://docs.oracle.com/en/java/javase/12/docs/api/java.base/java/util/stream/Collectors.html#teeing(java.util.stream.Collector,java.util.stream.Collector,java.util.function.BiFunction))

*Originally published at [A Java Geek](https://blog.frankel.ch/teeing-java-api/) on May 9^th^, 2021*
