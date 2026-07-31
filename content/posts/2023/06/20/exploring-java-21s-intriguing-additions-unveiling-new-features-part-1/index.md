---
title: "Exploring Java 21's Intriguing Additions (Part 1)"
slug: "exploring-java-21s-intriguing-additions-unveiling-new-features-part-1"
date: "2023-06-20T13:40:21+00:00"
lastmod: "2023-11-13T07:00:15+00:00"
description: "Learn about a few notable features that stand out in the upcoming Java 21, in an overview of their functionalities and potential use cases."
authors:
  - "bazlur-rahman"
image: "c1b005f8f2f84531abe9db67cf6b919e.png"
categories:
  - "Java Core"
  - "JDK21"
  - "Tutorials"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "5-things-you-probably-didnt-know-about-java-concurrency"
  - "a-minor-but-useful-refactoring-technique-that-would-reduce-your-code-footprint-part-1"
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
enlighterjs: true
frozen: false
---

Introduction: {#h2-0-introduction}
----------------------------------

Java 21 is going to introduce several intriguing additions that enhance the language's capabilities.

This article aims to discuss a few notable features that stand out, providing an overview of their functionalities and potential use cases.

<br />

Character Enhancements: {#h2-1-character-enhancements}
------------------------------------------------------

1. `Character.isEmoji(int codePoint)`: This method determines whether a character is considered an emoji based on its Unicode properties defined in Unicode Emoji ([Unicode Emoji Technical Standard #51](https://unicode.org/reports/tr51/#Emoji_Properties_and_Data_Files)). For example, executing `Character.isEmoji(9203)` would return `true` for the character ⏳, which has the code point 9203.
2. `Character.isEmojiPresentation(int codePoint)`: Similar to `isEmoji()`, this method checks if a character has the Emoji Presentation property. It helps identify characters that display as emojis when rendered.

Example:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">boolean isEmoji = Character.isEmoji(9203);
System.out.println(isEmoji);</pre>

The output will be `true` because the character with code point 9203 (⏳) is considered an emoji.

You can find more details about these methods in the [Java documentation](https://download.java.net/java/early_access/jdk21/docs/api/java.base/java/lang/Character.html#isEmoji(int)).

<br />

StringBuffer and StringBuilder Improvements: {#h2-2-stringbuffer-and-stringbuilder-improvements}
------------------------------------------------------------------------------------------------

Java 21 enhances the `StringBuffer` and `StringBuilder` classes with the `repeat()` method, which allows repetitive concatenation of characters or character sequences. The two overloaded versions of `repeat()` are as follows:

1. `StringBuffer.repeat(int codePoint, int count)`: Repeats `count` copies of the string representation of the specified `codePoint` argument in the `StringBuffer` sequence. For instance, executing `buffer.repeat(9203, 5)` would append ⏳ five times to the `StringBuffer`, resulting in "⏳⏳⏳⏳⏳".
2. `StringBuffer.repeat(CharSequence cs, int count)`: Appends `count` copies of the specified `CharSequence` `cs` to the `StringBuffer`. Similarly, these methods are also available in the `StringBuilder` class.

Example:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">var buffer = new StringBuffer();
buffer.repeat(9203, 5);
System.out.println(buffer);</pre>

<br />

The output will be `⏳⏳⏳⏳⏳`, as the code point 9203 (⏳) is repeated five times using the `repeat()` method.

<br />

String Enhancements: {#h2-3-string-enhancements}
------------------------------------------------

Java 21 introduces two new methods in the `String` class, namely `indexOf(String str, int beginIndex, int endIndex)` and `indexOf(int ch, int beginIndex, int endIndex)`, which expand the searching capabilities within a specified range of a string.

1. `String.indexOf(String str, int beginIndex, int endIndex)`: Returns the index of the first occurrence of the specified substring within the given index range of the string. This method provides the same result as invoking `s.substring(beginIndex, endIndex).indexOf(str) + beginIndex`. If the `indexOf(String)` method returns a non-negative index, it is returned; otherwise, -1 is returned.
2. `String.indexOf(int ch, int beginIndex, int endIndex)`: Returns the index of the first occurrence of the specified character within the given range of the string. The search starts at `beginIndex` and stops before `endIndex`. This method supports characters in the range from 0 to 0xFFFF, as well as other Unicode code points.

<br />

Java 21 also enhances the `String` class with a `splitWithDelimiters(String regex, int limit)` method. This method splits a string based on a given regular expression and returns both the strings and the matching delimiters. For example:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">var booAndFoo = "boo:::and::foo";
String[] splits = booAndFoo.splitWithDelimiters(":+", 3);
System.out.println("splits = " + Arrays.toString(splits));</pre>

The output will be: `splits = [boo, :::, and, ::, foo]`, where the string is split around the `:+` delimiter, limiting the result to a maximum of three splits.

<br />

Collections Framework Enhancements: {#h2-4-collections-framework-enhancements}
------------------------------------------------------------------------------

Java 21 introduces several new methods in the `Collections` class that enhance the Collections Framework's functionality.

1. `Collections.newSequencedSetFromMap(SequencedMap<E, Boolean> map)`: This method returns a `SequencedSet` backed by the specified `SequencedMap`. The resulting set maintains the ordering, concurrency, and performance characteristics of the backing map. This enables developers to obtain a `SequencedSet` implementation corresponding to any `SequencedMap` implementation.
2. `Collections.shuffle(List<?> list, RandomGenerator rnd)`: This method randomly permutes the elements of the specified list using the provided source of randomness. The permutations occur with equal likelihood, assuming a fair source of randomness.
3. `Collections.unmodifiableSequencedCollection(SequencedCollection<? extends T> c)`: Returns an unmodifiable view of the specified `SequencedCollection`. It allows read-only access to the underlying collection, throwing an `UnsupportedOperationException` if modification is attempted.
4. `Collections.unmodifiableSequencedSet(SequencedSet<? extends T> s)`: Returns an unmodifiable view of the specified `SequencedSet`, providing read-only access to the set while disallowing modifications.
5. `Collections.unmodifiableSequencedMap(SequencedMap<? extends K, ? extends V> m)`: Similar to the previous methods, this returns an unmodifiable view of the specified `SequencedMap`, allowing read-only operations.

SequencedCollection and SequencedMap are new interfaces introduced in Java 21 that preserve the order of elements and entries, respectively.  

To know more about Sequence Collection, read my news item on infoQ: [Collections Framework Makeover](https://www.infoq.com/news/2023/03/collections-framework-makeover/).

<br />

HashMap and HashSet Improvements: {#h2-5-hashmap-and-hashset-improvements}
--------------------------------------------------------------------------

Java 21 introduces convenient factory methods for creating `HashMap` and `HashSet` instances.

1. `Collections.newHashMap(int numMappings)`: Creates a new, empty `HashMap` suitable for the expected number of mappings. The map uses a default load factor of 0.75, and its initial capacity is typically large enough to accommodate the expected number of mappings without requiring resizing.
2. `Collections.newHashSet(int numElements)`: Creates a new, empty `HashSet` suitable for the expected number of elements. The set also uses a default load factor of 0.75, with an initial capacity that can accommodate the expected number of elements efficiently.

<br />

Conclusion {#h2-6-conclusion}
-----------------------------

<br />

If you want to experiment with these new features can download the OpenJDK from the [OpenJDK JDK 21 Early-Access Builds](https://jdk.java.net/21/).

Another alternative is to use [SDKMan](https://sdkman.io/), a software development kit manager, to download and manage different versions of Java. SDKMan can be used via the command line, which can make the process easier for developers who prefer this method.

However, these are early-access builds, so they may not be as stable as the final release, scheduled for September 2023, and are intended for testing and feedback purposes.

Nonetheless, Java 21 introduces various exciting language and collections framework additions. The new methods related to character properties, string manipulation, and collections provide developers with enhanced functionality and flexibility in their code. These additions aim to improve the productivity and performance of Java developers.  

***Note: To explore the complete set of new functionalities introduced in Java 21, stay tuned for future articles discussing additional features.***
