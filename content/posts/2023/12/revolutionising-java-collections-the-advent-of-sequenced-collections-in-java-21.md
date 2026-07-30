---
title: "Revolutionising Java Collections: The Advent of Sequenced Collections in Java 21"
slug: "revolutionising-java-collections-the-advent-of-sequenced-collections-in-java-21"
date: "2023-12-26T14:07:42+00:00"
lastmod: "2023-12-26T14:10:27+00:00"
description: "Addressing a long-standing gap in the collections framework, JEP 431 not only enhances the language's capabilities but also simplifies our development experience with the language."
canonical: "https://blog.payara.fish/revolutionising-java-collections-the-advent-of-sequenced-collections-in-java-21"
authors:
  - "jadon-ortlepp"
  - "luqman-saeed"
image: "https://foojay.io/wp-content/uploads/2020/10/payara_square_logo.jpg"
categories:
  - "Java Core"
  - "JEPs"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**Java has been a staple in the software development world for decades, renowned for its robustness and vast ecosystem. However, some seasoned Java developers have encountered limitations within its collections framework, particularly when dealing with ordered elements.**

Enter JEP 431. JEP 431, part of the Java Enhancement Proposal system, represents a significant advancement in Java's ongoing evolution.

The Java Enhancement Proposal system is a process for proposing, reviewing, and implementing new features in the Java programming language.

JEP 431, in particular, introduces necessary enhancements to the Java Collections Framework, addressing longstanding limitations and expanding its capabilities for us Java developers.

Understanding the Need for JEP 431 {#h2-0-understanding-the-need-for-jep-431}
-----------------------------------------------------------------------------

Historically, Java's collections framework, while comprehensive, lacked a specific collection type that could represent a sequence of elements with a defined order. This gap was more than a minor inconvenience for some larger projects.

Take, for instance, the List and Deque interfaces -- both define an encounter order, but their common supertype, Collection, does not. Similarly, while Set does not define an encounter order, some subtypes like SortedSet and LinkedHashSet do.

This inconsistent support across the hierarchy made it difficult to express and handle ordered collections uniformly. You'd often face challenges in iterating collections in reverse order or implementing specific operations for ordered collections, leading to inefficient and cumbersome solutions.

Introducing Sequenced Collections with JEP 431 {#h2-1-introducing-sequenced-collections-with-jep-431}
-----------------------------------------------------------------------------------------------------

JEP 431 introduces new interfaces: sequenced collections, sequenced sets, and sequenced maps.

These interfaces have been integrated into the existing collections framework, bringing uniformity and enhanced functionality.

### Sequenced Collections {#h3-2-sequenced-collections}

A sequenced collection is essentially a Collection with a defined encounter order. Each element in this collection has a well-defined position -- first, second, and so on, up to the last element.

The key features include:

* A reversed() method to provide a reverse-ordered view of the collection.
* Methods for adding, getting, and removing elements at both ends of the collection.

### Sequenced Sets {#h3-3-sequenced-sets}

A sequenced set is a Set that also behaves as a sequenced collection. This means no duplicate elements, but with the added ability to maintain a specific sequence.

Notably, methods like addFirst(E) and addLast(E) can reposition elements if they are already present in the set, addressing a long-standing limitation in LinkedHashSet.

### Examples {#h3-4-examples}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">SequencedSet&lt;String&gt; sequencedSet = new LinkedHashSet&lt;&gt;();
 sequencedSet.addFirst("Apple");
 sequencedSet.add("Banana");
 sequencedSet.addLast("Cherry");

assertEquals("Apple", sequencedSet.getFirst());
assertEquals("Cherry", sequencedSet.getLast());

SequencedCollection&lt;String&gt; sequencedCollection = new ArrayList&lt;&gt;();
 sequencedCollection.addFirst("Apple");
 sequencedCollection.add("Banana");
 sequencedCollection.addLast("Cherry");

assertEquals("Apple", sequencedCollection.getFirst());
assertEquals("Cherry", sequencedCollection.getLast());</pre>

Sequenced Maps {#h2-5-sequenced-maps}
-------------------------------------

Sequenced maps represent Map entries with a defined order. This interface introduces methods to get and manipulate entries in a specific sequence, including putting entries at the start or end of the map.

It also introduces methods for getting a sequenced collection of the keys and values of the map with the sequencedKeySet and sequencedValues. There is also a method for getting a sequenced collection of the entry set of the map.

#### Example

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">SequencedMap&lt;String, Integer&gt; sequencedMap = new LinkedHashMap&lt;&gt;();
&nbsp;sequencedMap.putFirst("Apple", 10);
&nbsp;sequencedMap.put("Banana", 20);
&nbsp;sequencedMap.putLast("Cherry", 30);

assertEquals("Apple", sequencedMap.firstEntry().getKey());
assertEquals(10, sequencedMap.firstEntry().getValue());

assertEquals("Cherry", sequencedMap.lastEntry().getKey());
assertEquals(30, sequencedMap.lastEntry().getValue());</pre>

Retrofitting and Compatibility {#h2-6-retrofitting-and-compatibility}
---------------------------------------------------------------------

The retrofitting of these interfaces into existing classes and interfaces like List, Deque, LinkedHashSet, SortedSet, LinkedHashMap, and SortedMap ensures backward compatibility while expanding functionality, an important aspect in a widely used language like Java.

Addressing Risks and Forward Compatibility {#h2-7-addressing-risks-and-forward-compatibility}
---------------------------------------------------------------------------------------------

With any significant addition to a language's core features, there are risks and concerns, especially regarding backward compatibility.

JEP 43 introduces methods that are compatible with existing interfaces and carefully considers the impact of new methods high in the inheritance hierarchy.

Conclusion {#h2-8-conclusion}
-----------------------------

JEP 431 marks a significant milestone in Java's evolution.

By addressing a long-standing gap in the collections framework, it not only enhances the language's capabilities but also simplifies our development experience with the language.

Java developers can now handle ordered collections more efficiently, paving the way for more streamlined and effective code.

With this update, Java reaffirms its commitment to evolving in response to its community's needs, ensuring it remains a top choice for developers worldwide.

Happy Coding!
