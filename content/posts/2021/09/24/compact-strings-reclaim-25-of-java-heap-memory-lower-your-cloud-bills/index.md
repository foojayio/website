---
title: "Reclaim 25% of Java Heap Memory & Lower Your Cloud Bills"
slug: "compact-strings-reclaim-25-of-java-heap-memory-lower-your-cloud-bills"
date: "2021-09-24T09:15:05+00:00"
lastmod: "2022-02-25T21:58:06+00:00"
description: "Learn how using compact strings can help you reclaim 25% of Java heap memory and lower your Cloud bills... very easily."
authors:
  - "bazlur-rahman"
image: "Favicon-3-2.png"
categories:
  - "Java Core"
tags:
related_posts:
  - "7-functional-programming-techniques-in-java-a-primer"
  - "real-world-stream-collector"
  - "project-panama-for-newbies-part-1"
  - "confusing-java-strings"
enlighterjs: true
frozen: false
---

Did you know that you can save up to 25% of your heap memory and your Cloud bills without any effort? Well, it's true. Many exciting features have been added to the latest releases of Java recently and I'm going to cover one that is often overlooked in this article.

We all know that Strings are the most used Object in any Java application. In fact, they probably take up almost half of the heap size of any Java application. Before delving into this much further, let me answer your obvious question: what are Strings made of?

Well, Strings are nothing but an array of characters, well, at least, this is how it used to be. If you open the String class from JDK 8, you will be able to confirm this. However, when looking at it the other way around, and unlike in C, an array of char is not a String in Java, and neither a String nor an array of char is terminated by `'\u0000'` (the NULL character).

Nevertheless, a String object in Java is also immutable, which means the String content never changes, while an array of char has mutable elements. Reference: <https://docs.oracle.com/javase/specs/jls/se14/html/jls-10.html#jls-10.9>. I will write about String immutability in another article.

In Java 8 and pre-Java 8, a char array is used in a String. A char takes two bytes of memory. That means that to store one character, you need 16 bits of memory. For example, if you write "Hello" you'd need an object of an array, and that would have 5 characters.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">The total size of a String 
= size of array object itself + size of 5 characters + array holds an integer for its length 
= 8 bytes for array object header + 5 * 2bytes + 4 bytes 
= 8 + 10 + 4 
= 22 bytes</pre>

However, most Western locales nowadays need only 8 bits byte array to encode them. That's why Java 11 (see [JEP 254](https://openjdk.java.net/jeps/254)) introduces the new compact Strings that encode a string with an 8-bit byte array instead of a char array. Unless they explicitly need 16-bit characters. These strings are known as compact strings. Hence, the size of an average string in Java 11 is roughly half the size of the same in Java 8.

On average, 50% of a typical Java heap may be consumed by String objects. This will vary from application to application, but on average, the heap requirement for such a program running with Java 11 is only 75% of that same program running in Java 8.

This is a huge saving.

The `-XX:+CompactStrings` flag controls this feature.

If you want to disable it, use the `-XX:-CompactStrings` flag.
