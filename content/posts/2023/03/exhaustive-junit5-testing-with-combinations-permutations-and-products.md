---
title: "Exhaustive JUnit5 Testing: Combinations, Permutations, Products"
slug: "exhaustive-junit5-testing-with-combinations-permutations-and-products"
date: "2023-03-08T10:08:21+00:00"
lastmod: "2023-03-09T09:12:53+00:00"
description: "Read this article and learn how to use JUnit5 in conjunction with combinations, permutations, and products."
authors:
  - "per-minborg"
image: "https://foojay.io/wp-content/uploads/2023/02/Screen-Shot-2023-02-27-at-9.52.42-AM-1.png"
categories:
  - "Testing"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Unit testing constitutes an integral part of the process of providing high-quality software.

But, how can one write tests covering all variants of several operations?

Read this article and learn how to use JUnit5 in conjunction with combinations, permutations, and products.

### Test Support Libraries {#h3-0-test-support-libraries}

There are many libraries that make testing better in different aspects. Here are some of them:

#### Agitar One

[Agitator](http://www.agitar.com/solutions/products/software_agitation.html "Agitator") automatically creates dynamic test cases, synthesizes sets of input data, and analyzes the results.

#### Chaos Monkey

[Chaos Monkey](https://netflix.github.io/chaosmonkey/ "Chaos Monkey") is responsible for randomly terminating instances in production to ensure that engineers implement their services to be resilient to instance failures.

#### Chronicle Test Framework

This library provides support classes for writing JUnit tests. It supports both JUnit4 and JUnit5. Tools like permutations, combinations and products can be leveraged to create exhaustive tests.

Agitar One can automatically identify and generate tests whereas [Chronicle Test Framework](https://github.com/OpenHFT/Chronicle-Test-Framework "Chronicle Test Framework") provides programmatic support for writing your own tests.

In this article we will be talking about Chronicle Test Framework.

### Objective {#h3-1-objective}

Suppose we have written a custom implementation of java.util.List called MyList and that we want to test the implementation against a reference implementation such as ArrayList.

Further, assume that we have a number of operations O1, O2, ..., On. that we can apply to these objects.

The question now is: how can we write tests assuring that the two objects provide the same result regardless of how the operations are applied?

### Combinations, Permutations and Products {#h3-2-combinations-permutations-and-products}

Before we continue, we remind ourselves of what combinations, permutations, and products are from a mathematical perspective. Assume we have a set X = {A, B, C} then;

The combinations of X are:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">[]
[A]
[B]
[C]
[A, B]
[A, C]
[B, C]
[A, B, C]</pre>

The permutations of X are:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">[A, B, C]
[A, C, B]
[B, A, C]
[B, C, A]
[C, A, B]
[C, B, A]</pre>

The permutations of all combinations of X are:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">[]
[A]
[B]
[C]
[A, B]
[B, A]
[A, C]
[C, A]
[B, C]
[C, B]
[A, B, C]
[A, C, B]
[B, A, C]
[B, C, A]
[C, A, B]
[C, B, A]</pre>

Where \[\] denotes a sequence of no elements. As can be seen from the sequences above, the number of variants will increase very rapidly as the number of set members increases.

This puts a practical limit on the exhaustiveness of the test one can write.

Another concept (not exemplified in this article) is "Products" (aka [Cartesian Products](https://en.wikipedia.org/wiki/Cartesian_product "Cartesian Products")). Suppose we have a sequence s1 = \[A, B\] of one type and another sequence s2 = \[1, 2\] of another type then;

The product of s1 and s2 is:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">[A, 1]
[A, 2]
[B, 1]
[B, 2]</pre>

Products have many similarities with database "join" operations and relate to nested loops.

### The Test Framework {#h3-3-the-test-framework}

In this test, we will be using open-source[Chronicle-Test-Framework](https://github.com/OpenHFT/Chronicle-Test-Framework " Chronicle-Test-Framework") that supports the aforementioned combination, permutation, and product features. Here is an example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// Prints: [], [A], [B], [C], [A, B], [B, C], … (8 sequences)
Combination.of("A", "B", "C")
    .forEach(System.out::println)</pre>

This prints all the combinations of {A, B, C} the result being the same as in the previous chapter. In the same way, the following example will print out all the permutations of {A, B, C}:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// Prints: [A, B, C], [A, C, B], [B, A, C], … (6 sequences)
Permutation.of("A", "B", "C")
    .forEach(System.out::println)</pre>

In this following example, we combine the capabilities of combinations and permutations:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// Prints: [], [A], [B], [C], [A, B], [B, A], …(16 sequences)
Combination.of("A", "B", "C")
    .flatMap(Permutation::of)
    .forEach(System.out::println)</pre>

The methods above produce a Stream of Collection elements allowing easy adaptations to other frameworks such as JUnit5.

When it comes to products, things work slightly differently. Here is an example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">final List&lt;String&gt; strings = Arrays.asList("A", "B");
final List&lt;Integer&gt; integers = Arrays.asList(1, 2);

Product.of(strings, integers)
        .forEach(System.out::println);</pre>

This will print:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Product2Impl{first=A, second=1}
Product2Impl{first=A, second=2}
Product2Impl{first=B, second=1}
Product2Impl{first=B, second=2}</pre>

If a more recent Java version is used, the following scheme would typically be used instead:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">record StringInteger(String string, Integer integer){}
Product.of(strings, integers, StringInteger::new)
        .forEach(System.out::println);</pre>

This will produce:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">StringInteger[string=A, integer=1]
StringInteger[string=A, integer=2]
StringInteger[string=B, integer=1]
StringInteger[string=B, integer=2]</pre>

In my opinion, the above is better than the default Product2Impl tuple because the record is a "nominal tuple" where the names and types of the state elements are declared in the record header compared to the Product2Impl which relies on generic types and "first" and "second" as names.

### The Objective {#h3-4-the-objective}

Suppose that we want to make sure two java.lang.List implementations behave the same way for a number of mutating operations. Here, lists contain Integer values (ie implements List).

We start by identifying the mutating operations to use. One (arbitrary) suggestion is to use the following operations:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">list.clear()
list.add(1)
list.remove((Integer) 1) // Will remove the object 1 and not @index 1
list.addAll(Arrays.asList(2, 3, 4, 5))
list.removeIf(ODD)
Where Predicate&lt;Integer&gt; ODD = v -&gt; v % 2 == 1.</pre>

In this article, we will initially use ArrayList and LinkedList for comparison.

As the reader might suspect, the List types are going to be tested against each other with all the possible sequences of operations. But, how can this be achieved?

### The Solution {#h3-5-the-solution}

JUnit5 provides a @TestFactory annotation and DynamicTest objects, allowing a test factory to return a Stream of DynamicTest instances.

This is something that we can leverage, as shown below:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">private static final Collection&lt;NamedConsumer&lt;List&lt;Integer&gt;&gt;&gt; OPERATIONS =
     Arrays.asList(
        NamedConsumer.of(List::clear, "clear()"),
        NamedConsumer.of(list -&gt; list.add(1), "add(1)"),
        NamedConsumer.of(list -&gt; list.remove((Integer) 1), "remove(1)"),
        NamedConsumer.of(list -&gt; list.addAll(Arrays.asList(2, 3, 4, 5)),
                                 "addAll(2,3,4,5)"),
        NamedConsumer.of(list -&gt; list.removeIf(ODD), "removeIf(ODD)")
);

@TestFactory
Stream&lt;DynamicTest&gt; validate() {
    return DynamicTest.stream(Combination.of(OPERATIONS)
                    .flatMap(Permutation::of),
            Object::toString,
            operations -&gt; {
                List&lt;Integer&gt; first = new ArrayList&lt;&gt;();
                List&lt;Integer&gt; second = new LinkedList&lt;&gt;();
                operations.forEach(op -&gt; {
                    op.accept(first);
                    op.accept(second);
                });
                assertEquals(first, second);
            });
}</pre>

This is the entire solution, and when run under IntelliJ (or other similar tools), the following tests will be performed (only the first 16 of the 326 tests are shown for brevity):

![](/images/posts/2023/03/exhaustive-junit5-testing-with-combinations-permutations-and-products/Screen-Shot-2023-02-27-at-9.52.42-AM.png)

*Image 1, Shows the first 16 test runs of the 326 tests.*

The reason NamedConumer is used over the standard java.util.function.Consumer is that it allows real names to be shown rather than some cryptic lambda reference like ListDemoTest$$Lambda$350/0x0000000800ca9a88@3d8314f0.

It certainly looks better in the output and provides much better debug capabilities.

### Expanding the Concept {#h3-6-expanding-the-concept}

Suppose we have a more significant number of List implementations we'd like to test, such as:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">ArrayList
LinkedList
CopyOnWriteArrayList
Stack
Vector</pre>

To expand the concept, we begin by creating a collection of List constructors:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">private static final Collection&lt;Supplier&lt;List&lt;Integer&gt;&gt;&gt; CONSTRUCTORS =
        Arrays.asList(
            ArrayList::new,
            LinkedList::new,
            CopyOnWriteArrayList::new,
            Stack::new,
            Vector::new);</pre>

The reason for working with constructors rather than instances is that we need to be able to create new List implementations for each dynamic test.

Now, we only need to do minor modifications to the previous test:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@TestFactory
Stream&lt;DynamicTest&gt; validateMany() {
    return DynamicTest.stream(Combination.of(OPERATIONS)
                    .flatMap(Permutation::of),
            Object::toString,
            operations -&gt; {

                // Create a fresh list of List implementations
                List&lt;List&lt;Integer&gt;&gt; lists = CONSTRUCTORS.stream()
                        .map(Supplier::get)
                        .collect(Collectors.toList());

                // For each operation, apply the operation on each list
                operations.forEach(lists::forEach);

                // Test the lists pairwise
                Combination.of(lists)

                    // Filter out only combinations with two lists
                    .filter(set -&gt; set.size() == 2)

                    // Convert the Set to a List for easy access below
                    .map(ArrayList::new)

                    // Assert the pair equals
                    .forEach(pair -&gt; assertEquals(pair.get(0), pair.get(1)))
            });
}</pre>

Strictly, this is a bit of cheating as a single dynamic test contains several subtests for a plurality of List pairs.

However, It would be reasonably easy to modify the code above to flatMap() in the various assertions under separate dynamic tests. This is left to the reader as an exercise.

### A Final Warning {#h3-7-a-final-warning}

Combinations, permutations, and products are potent tools that can increase test coverage, but they can also increase the time it takes to run all tests as the number of sequences explodes even with only a moderate increase in the number of input variants.

For example, here is a list of the number of permutations of all combinations for some given sets S of size s:

![](/images/posts/2023/03/exhaustive-junit5-testing-with-combinations-permutations-and-products/Screen-Shot-2023-02-27-at-9.54.03-AM-1024x884.png)  
*Table 1, shows the number of permutations of combinations for some set sizes 's'.*

More generally, it can be shown that: poc(s) = poc(s -- 1) \* s + 1.

If in doubt, the number of combinations can be checked using the .count() stream operation which will return the number of sequences returned as shown below:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">long poc6 = Combination.of(1, 2, 3, 4, 5, 6)
        .flatMap(Permutation::of)
        .count();</pre>

The stream above will, unsurprisingly, return 1,957 as there are six input elements.

Combinatorics can make a huge impact. However, make sure to keep the number of dynamic tests under control, or else test time might increase significantly.

Libraries like [Chronicle Queue](https://chronicle.software/queue/ "Chronicle Queue") and [Chronicle Map](https://chronicle.software/map/ "Chronicle Map") make use of the [Chronicle-Test-Framework](https://github.com/OpenHFT/Chronicle-Test-Framework "Chronicle-Test-Framework") to improve testing. Here is an example from Chronicle Map where five Map operations are exhaustively tested providing 326 dynamic tests in just a few lines of code.

**Resources**

* Open-Source [Chronicle-Test-Framework](https://github.com/OpenHFT/Chronicle-Test-Framework "Chronicle-Test-Framework")
* [The code in this article](https://github.com/OpenHFT/Chronicle-Test-Framework/blob/develop/src/test/java/net/openhft/chronicle/testframework/internal/ListDemoTest.java "The code in this article ")(Open-Source)
