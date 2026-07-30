---
title: "What's New and Exciting in Java 18? | Foojay.io Today"
slug: "whats-new-and-exciting-in-java-18"
date: "2022-03-22T07:23:21+00:00"
lastmod: "2022-03-22T07:31:56+00:00"
description: "Java 18 is here and in this article we explore the most important changes and, in my humble opinion, this release is an important one!"
authors:
  - "miro-wengner"
image: "/images/posts/2022/03/whats-new-and-exciting-in-java-18/Favicon-3-2.png"
categories:
  - "Release Notes"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "java-where-the-wild-code-isnt"
  - "are-java-security-updates-important"
  - "whats-new-in-the-july-2026-azul-payara-release"
enlighterjs: true
frozen: false
---

Time runs forward without compromise! The six month release period is over and, similar to people expecting spring and nice weather, the Java community is excited about the next OpenJDK release, this time holding the number 18.

Yes, Java 18 is here and in this article we explore the most important changes and, in my humble opinion, this release is an important one!

**Note:** For a full listing of the 2,045 fixes in Java 18, [go here on Foojay.io](https://foojay.io/java-18/?tab=highlights&quarter=032022&version=18.0.0). And vote on your favorites to show the Java community's favorite fixes in Java 18.

Exploring Changes {#h2-0-exploring-changes}
-------------------------------------------

Before we start introducing particular features, lets spend some thinking on the type and the focus of those changes.

We are aware that computers understand binary pretty well, though what about humans? For humans, number sequences are important as an information carrier while strings have more understandable value. The string concept is very helpful to communicate information from the program to the user.

And, as it happens, Strings may well be the motto of the new Java 18 release. In my humble opinion, Java has been struggling for a very long time to not have a default encoding, while Java 18 solves for this struggle, turning it into the right direction.

It may not be so obvious but encoding involves Java on many levels, from properly displayed values to the client, though the correct internet address resolution, to Java string pool and garbage collection.

In that sense, Java 18 helps to make Java much more stable and predictable than before. Below shows a pre-Java 18 java.io.FileReader encoding issue for a Japanese file with file with content set at UTF-8.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">java.io.FileReader(“hello.txt”) -&gt; “こんにちは” (macOS)
java.io.FileReader(“hello.txt”) -&gt; “ã?“ã‚“ã?«ã?¡ã? ” (Windows (en-US))
java.io.FileReader(“hello.txt”) -&gt; “縺ォ縺。縺ッ” (Windows (ja-JP)</pre>

Feature Highlights {#h2-1-feature-highlights}
---------------------------------------------

**1. JEP-400: UTF-8 by Default**

**Module**: core-libs/java.nio.charsets

**Description:** From Java 18 on, UTF-8 encoding is taken as the default encoding for the Java SE APIs so that every API that depends on the default encoding variable will be set to UTF-8 and thus contributing to a more consistent and predictable program behavior.

**Details** :<https://openjdk.java.net/jeps/400>, [JDK-8187041](https://bugs.openjdk.java.net/browse/JDK-8187041)

#### **2. JEP-418: Internet-Address Resolution SPI**

**Module:** core-libs/java.net

**Description:** Here the goal is to define a service provider interface (SPI) for host name and address resolution, as the API currently uses the operating system's native resolver, which is typically configured to use a combination of a local "hosts" file and the Domain Name System (DNS).

The package java.net.spi now contains a set of new classes, as a consequence, such as InetAddressResolverProvider and InetAddressResolver.

**Details:** <https://openjdk.java.net/jeps/418>

#### **3. JEP-192: String deduplication in G1**

**Module:**hotspot/gc

**Description:** With this JEP, the goal is to reduce Java heap live-data sets by enhancing the G1 garbage collector so that duplicate instances of String are automatically and continuously deduplicated. The impact ot the JEP is broader, which has a positive impact on overall stability.

* SerialGC Supports String Deduplication ([JDK-8272609](https://bugs.openjdk.java.net/browse/JDK-8272609))
* ParallelGC Supports String Deduplication ([JDK-8267185](https://bugs.openjdk.java.net/browse/JDK-8267185))
* ZGC Supports String Deduplication ([JDK-8267186](https://bugs.openjdk.java.net/browse/JDK-8267186))

**Details** : <https://openjdk.java.net/jeps/192>

#### **4. JEP-413: Code Snippets in Java API Documentation** ([JDK-8201533](https://bugs.openjdk.java.net/browse/JDK-8201533))

**Module:** tools/javadoc(tool)

**Details:** This JEP introduces a new tag: @snippet. The goal of the tag is to reduce duplication in writing documentation in JavaDoc's standard Doclet, which is pretty handy as the tag allows a direct reference to the already written code and it is not required to duplicate it, which has a positive effect on maintainability.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">/**
 * The following code shows how to use {@code ProductionCode.function}:
 * {@snippet file="ProductionCodeFunctionTest.java" region="example"}
&nbsp;*/&nbsp;</pre>

...where "ProductionCodeFunctionTest.java" represents a file with content.

#### 5. Miscellaneous Improvements

In addition to the accepted JEPs, Java 18 introduces other enhancements that are worthwhile to mention, as listed below.

* Improved compilation replay ([JDK-8254106](https://bugs.openjdk.java.net/browse/JDK-8254106))
* Allow G1 Heap Regions up to 512MB ([JDK-8275056](https://bugs.openjdk.java.net/browse/JDK-8275056))
* JDK Flight Recorder Event for Finalization ([JDK-8266936](https://bugs.openjdk.java.net/browse/JDK-8266936))
* New System Property to Control the Default Date Comment Written Out by java.util.Properties::store Methods

Each new Java release does not always come with bunches of large new features. Instead, it mostly goes hand in hand with clean ups and removals. In terms of deprecation, there is a specific achievement in **Deprecated Finalization for Removal** ([JDK-8274609](https://bugs.openjdk.java.net/browse/JDK-8274609)), which basically recommends developers to use the construction "**try-with-resources**" as a cleaner or better approach than the finalize() method.

With having finalization deprecated, Java 18 also comes with the removal of the empty finalize methods, see **removal of empty finalize() methods** in java.desktop Module ([JDK-8273102](https://bugs.openjdk.java.net/browse/JDK-8273102)).

Conclusion {#h2-2-conclusion}
-----------------------------

Java 18 is another of the six month iterations, bringing with it the stability that would have been nice to have in the Java 17 LTS, namely default string encoding, though is great to have too, ready for the next LTS release.

In this article, the broad impact of this change impacting Java's stability has been shown, meaning that it will be easier to observe similar behavior across the system platform without facing unexpected encoding challenges.   

**Note:** For a full listing of the 2,045 fixes in Java 18, [go here on Foojay.io](https://foojay.io/java-18/?tab=highlights&quarter=032022&version=18.0.0). And vote on your favorites to show the Java community's favorite fixes in Java 18.
