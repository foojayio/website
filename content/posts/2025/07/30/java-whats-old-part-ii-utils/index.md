---
title: "Java, What's Old? Part II: Utils"
slug: "java-whats-old-part-ii-utils"
date: "2025-07-30T08:48:29+00:00"
lastmod: "2025-07-30T08:52:09+00:00"
description: "What are the old utility classes less known in Java?"
authors:
  - "anthony-goubard"
image: "old-coffee.jpg"
categories:
  - "Java"
  - "Java Core"
tags:
related_posts:
  - "java-whats-old-part-i-collections"
  - "sorting-text-in-java-how-complicated-can-it-be"
  - "3-ways-to-refactor-your-code-in-intellij-idea"
  - "5-things-you-probably-didnt-know-about-java-concurrency"
enlighterjs: true
frozen: false
---

After [Java, What's Old? Part I - Collections](https://foojay.io/today/java-whats-old-part-i-collections/), let's now have a look at less known old utility classes that can still be useful.

Everything in this series will be in Java 8 and later, so after reading this article, you will be able to use it in your projects.

Objects {#h2-0-objects}
-----------------------

[Objects](https://docs.oracle.com/javase/8/docs/api/java/util/Objects.html) (with a s) is a utility class from the `java.util` package that contains static methods to make your code more readable. Do not confuse it with the `Object` (without s) class of the `java.lang` package.

Let's see a few of them:

* `static boolean equals(Object, Object)`  
  Compare two objects and taking care of the null check. `(o1 == null && o2 == null) || (o1 != null && o1.equals(o2)` =\> `Objects.equals(o1, o2)`
* `static int hash(Object... values)`  
  Calculate a hash code integer based on the arguments passed.   
  E.g. `int hash = Objects.hash(firstName, lastName, age);`
* `static String toString(Object value, String nullDefault);`  
  Returns the String representation of the value and provide a default value if null. Note that value doesn't need to be a String.   
  E.g. `System.out.println("Job: " + Objects.toString(job, "none"));`

volatile {#h2-1-volatile}
-------------------------

`volatile` is not a class but a keyword in Java. It means that you want to keep the value to be read and written from the main memory and not copied to the CPU core local memory.

This may be slower to read and write the value but all threads will point to the same memory address instead of having a local copy that is then copied to the main memory.

Note that it doesn't mean that operations on the value are thread-safe. For example `i++;` from 2 threads may result in incorrect value as 2 threads can put the value in their registry, increment it of one and copy it back to the main memory at the same time.  

For this case, use `AtomicInteger` or `synchronized`.

Locale {#h2-2-locale}
---------------------

`Locale` is a well known Java class and is used to specify a country and a language. Beside country and language, Locale can have a script (ISO 15924 alpha-4 script code), variant and extensions.

Locale is used at many well know places like formatters (numbers, dates), *String.toLocalCase(Locale)* , *DayOfWeek.getDisplayName* , *Currency* or *ResourceBundle*.

What is less known is that Locale is used at many other places like *Font* (font name), *Window* , AWT/Swing components, image meta data, *UIDefaults* , *PrinterJob* and *Scanner*.

Collator {#h2-3-collator}
-------------------------

The [Collator](https://docs.oracle.com/javase/8/docs/api/java/text/Collator.html) class is used to compare `String`. The String class implements `Comparable` but the *compareTo* method compare strings based on the Unicode value of each character in the strings.

*Collator* offers a better way to compare strings. It is based on a *Locale* , you can specify a strength and decomposition (See *Normalizer* below).

Depending on the strength, it will take care of sorting upper case / lower case and letters with diacritics correctly:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Collator collator = Collator.getInstance(Locale.US);
collator.setStrength(Collator.PRIMARY);
collator.compare("test", "tést"); // 0
collator.compare("test", "tEst"); // 0
collator.setStrength(Collator.SECONDARY);
collator.compare("test", "tést"); // -1
collator.compare("test", "tEst"); // 0
collator.setStrength(Collator.TERTIARY);
collator.compare("test", "tést"); // -1
collator.compare("test", "tEst"); // -1</pre>

For more details, you can read my Foojay article about [Sorting text in Java, how complicated can it be?](https://foojay.io/today/sorting-text-in-java-how-complicated-can-it-be/)

Normalizer {#h2-4-normalizer}
-----------------------------

In Unicode the letter **é** can be written using \\u00C1 or with \\u0041\\u0301 (e + ´). ´ is a called a combining diacritical mark.

The [Normalizer](https://docs.oracle.com/javase/8/docs/api/java/text/Normalizer.html) class can be used to transform text in its composed or decomposed form.

I use this class to remove any diacritical marks to easily search for text independently of accents in the text or search term:  

`String searchTermNoDiacritics = Normalizer.normalize(searchTerm, Normalizer.Form.NFKD).replaceAll("\p{InCombiningDiacriticalMarks}+", "");`

In the next and last article of this series, we'll look at IO classes.
