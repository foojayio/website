---
title: "Java 22: What’s New?"
slug: "java-22-whats-new"
date: "2024-03-18T14:10:04+00:00"
lastmod: "2024-03-28T08:16:58+00:00"
description: "As soon as Java 22 is out, it'll be time to walk through all the functionalities that this version bring to us as developers."
canonical: "https://www.loicmathieu.fr/wordpress/en/informatique/java-22-quoi-de-neuf/"
authors:
  - "loic-mathieu"
image: "https://foojay.io/wp-content/uploads/2024/03/podcast-guests-java-22.png"
categories:
  - "Java"
  - "JEPs"
  - "Release Notes"
tags:
related_posts:
  - "foojay-podcast-45"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "42-practical-java-design-patterns-builder-and-more"
  - "where-production-policy-belongs-building-eliya-in-public"
enlighterjs: true
frozen: false
---

As soon as Java 22 is out, it'll be time to walk through all the functionalities that this version bring to us as developers.

JEP 461 - Stream Gatherers (Preview) {#h2-0-jep-461-stream-gatherers-preview}
-----------------------------------------------------------------------------

Enhances the Stream API with support for custom intermediate operations. This is a preview API.

The Stream API provides a fixed set of intermediate and terminal operations. It allows terminal operations to be extended via the `Stream::collect(Collector)` method, but does not allow intermediate operations to be extended. Some operations are missing or others are possible via a set of operations or via an operation that doesn't fully match what's needed.

Over the years, many new terminal operations have been proposed, but even if most of them make sense, it's not possible to add them all to the SDK. Adding the possibility of defining your own intermediate operations alleviates the problem.

With JEP 461, it is now possible to define your own intermediate operations via `Stream::gather(Gatherer)`.

A *gatherer* represents the transformation of an element in a Stream into one-to-one, one-to-many, many-to-one or many-to-many and can stop the transformation if necessary, stopping the emission of element in the downstream stream.

Gatherers can be combined: `stream.gather(...).gather(...).collect(...)`.

The `java.util.stream.Gatherer` interface defines the following methods:

* `initializer()`: optional, can be used to maintain a state when processing elements.
* `integrator()`: used to integrate a new element from the incoming stream, and if necessary emit an element in the downstream stream.
* `combiner()`: optional, can be used to evaluate the gatherer in parallel for parallel streams.
* `finisher()`: optional, called when the stream has no more input elements.

The Stream API has been enhanced with the following gatherers:

* `fold`: stateful many-to-one gatherer that builds an aggregate incrementally and emits this aggregate when no more input elements exist.
* `mapConcurrent`: stateful one-to-one gatherer that concurrently invokes a provided function for each input element, up to a provided limit.
* `scan`: stateful one-to-one gatherer that applies a provided function to the current state and the current element to produce an output element.
* `windowFixed`: stateful many-to-many gathere that groups input items into lists of a supplied size, outputting windows when full.
* `windowSliding`: stateful many-to-many gatherer that groups input items into lists of a supplied size. After the first window, each subsequent window is created from a copy of its predecessor by deleting the first element and adding the next element from the input stream.

Here's an example of using a gatherer provided in the JDK:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">var numbers = List.of(1, 2, 3, 4, 5);

var slidingWindows = numbers.stream()
    .gather(Gatherers.windowSliding(3))
    .toList();

System.out.println(slidingWindows);
// [[1, 2, 3], [2, 3, 4], [3, 4, 5]]</pre>

The JavaDoc gives the following example of a gatherer that reproduces the `Stream.map()` operation:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public &lt;T, R&gt; Gatherer&lt;T, ?, R&gt; map(Function&lt;? super T, ? extends R&gt; mapper) {
    return Gatherer.of(
        (unused, element, downstream) -&gt; // integrator
            downstream.push(mapper.apply(element))
    );
}</pre>

More information in the [JEP 461](https://openjdk.org/jeps/461 "JEP")

JEP 458 - Launch Multi-File Source-Code Programs {#h2-1-jep-458-launch-multi-file-source-code-programs}
-------------------------------------------------------------------------------------------------------

Since Java 11, it is possible to launch a program from a `.java` source file without first compiling it. The Java launcher will then compile the program in memory automatically before execution.

With JEP 458, it is now possible to launch a program from a source file that uses a class defined in another source file, this second file will also be automatically compiled in memory. Source files are searched in the usual Java directory hierarchy, which reflects the package structure.

Only source files used by the main program are compiled in memory.

More information in the [JEP 458](https://openjdk.org/jeps/458 "JEP")

JEP 447 - Statements before super (preview) {#h2-2-jep-447-statements-before-super-preview}
-------------------------------------------------------------------------------------------

When a class extends another class and wants to call the parent class's constructor in its own constructor, the JVM forces the call to the parent constructor to be the first instruction in the parent class's constructor. This ensures that all fields in the parent class are initialized before the child class is built.

JEP 447 is a preview feature that allows instructions **before** calling the parent constructor as long as they do not access the instance being created. Several examples are given in the JEP: parameter validation, argument pre-calculation, etc...

Here's an example of parameter validation prior to JEP 447:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class PositiveBigInteger extends BigInteger {

    public PositiveBigInteger(long value) {
        super(value);               // Potentially unnecessary work
        if (value &lt;= 0)
            throw new IllegalArgumentException(non-positive value);
    }

}</pre>

And with the JEP 447:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class PositiveBigInteger extends BigInteger {

    public PositiveBigInteger(long value) {
        if (value &lt;= 0)
            throw new IllegalArgumentException(non-positive value);
        super(value);
    }

}</pre>

The code is more readable and potentially avoids the effects of the parent constructor.

More information in the [JEP 447](https://openjdk.org/jeps/447 "JEP")

457 - Class-File API (Preview) {#h2-3-457-class-file-api-preview}
-----------------------------------------------------------------

JEP 457 provides a standard API for parsing, generating and transforming Java class files. This API is in preview.

The Java ecosystem has numerous libraries for parsing and generating Java class files: ASM, BCEL, Javassist, ... Most bytecode-generating frameworks use them. The Java class format evolves every 6 months, with each new Java release, so the generation frameworks must evolve at the same time, at the risk of not supporting the latest language evolutions.

The JDK itself uses ASM to implement some of its tools, as well as for lambda implementation, which creates a discrepancy between the functionalities of one version of Java and what can be used within the JVM in the portions requiring class file generation, as you have to wait for a version of ASM supporting the new functionalities of version N before using them in version N+1.

The **Class-File** API overcomes this problem by providing an API within the JDK for parsing, generating and transforming class files.

The following presentation by Brian Goetz at the VM Language Summit 2023 describe the API, its design and use: [A Classfile API for the JDK](https://www.youtube.com/watch?v=pcg-E_qyMOI "A").

More information in the [JEP 457](https://openjdk.org/jeps/457 "JEP")

ListFormat {#h2-4-listformat}
-----------------------------

`ListFormat` is a new formatter that allows you to format a list of strings regarding a locale based on the Unicode standard. Example:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">var list = List.of(Black, White, Red);
var formatter = ListFormat.getInstance();
System.out.println(formatter.format(list));
// [Black, White, Red]</pre>

When creating the formatter we can pass it:

* A locale, otherwise the default locale will be used.
* The enumeration type: STANDARD, OR or UNIT. Default STANDARD.
* Enumeration style: FULL, SHORT or NARROW. Default FULL.

The following table shows the different outputs for the US locale:

|          |         FULL          |        SHORT         |        NARROW        |
|----------|-----------------------|----------------------|----------------------|
| STANDARD | Black, White, and Red | Black, White, \& Red | Black, White, Red    |
| OR       | Black, White, or Red  | Black, White, or Red | Black, White, or Red |
| UNIT     | Black, White, Red     | Black, White, Red    | Black White Red      |

More information in the issue [JDK-8041488](https://bugs.openjdk.org/browse/JDK-8041488 "JDK-8041488").

Features coming out of preview {#h2-5-features-coming-out-of-preview}
---------------------------------------------------------------------

The following features comes out of preview (or incubator module) are now standard features:

* [JEP 454](https://openjdk.org/jeps/454 "JEP") - **Foreign Function \& Memory API**: API for interconnecting the JVM with native code.
* [JEP 456](https://openjdk.org/jeps/456 "JEP") - **Unnamed Variables \& Patterns** : allows you to use `_` as an unnamed pattern or variable.

An important change has been made in the Foreign Function \& Memory API that should be noted: the introduction of the notion of **restricted method** . Some methods in this new API are marked as restricted: to use them, you'll need to use the `--enable-native-access=module-name` command-line option.

Currently, access to restricted methods generates a warning, but access to them may be forbidden in a future version of the JVM. Restricted methods are used to bind a native function and/or native data, which is inherently unsafe. For this reason, access to them must be given specifically via a command-line option.

Features that remain in preview {#h2-6-features-that-remain-in-preview}
-----------------------------------------------------------------------

The following features remain in preview (or in the incubator module).

* [JEP-460](https://openjdk.org/jeps/460 "JEP-460") - **Vector API**: seventh incubation, API for expressing vector calculations that compile at runtime into vector instructions for supported CPU architectures. This new version includes bugfixes and performance enhancements.
* [JEP 464](https://openjdk.org/jeps/464 "JEP") - **Scoped Values**: second preview, enable the sharing of immutable data within and between threads. No noticeable changes in this new preview.
* [JEP 462](https://openjdk.org/jeps/462 "JEP") - **Structured Concurrency**: second preview, a new API that simplifies the writing of multi-threaded code by allowing multiple concurrent tasks to be treated as a single processing unit. No noticeable changes in this new preview.
* [JEP 463](https://openjdk.org/jeps/463 "JEP") - **Implicitly Declared Classes and Instance Main Methods** : second preview, simplifies the writing of simple programs by allowing them to be defined in an implicit class (without declaration) and in an instance method `void main()`.
* [JEP 459](https://openjdk.org/jeps/459 "JEP") - **String Templates**: Second preview, a string template is a literal of String that lets you incorporate expressions and variables. No noticeable changes for this new preview.

Miscellaneous {#h2-7-miscellaneous}
-----------------------------------

Various additions to the JDK:

* `Console.isTerminal()`: returns true if the console instance is a terminal.
* `Class.forPrimitiveName(String)`: returns the class associated with the given primitive type.
* `InetAddress.ofLiteral(String)`: creates an `InetAddress` from the textual representation of the IP address. This static method also exists for the `Inet4Address` and `Inet6Address` classes.
* `RandomGenerator.equiDoubles(double left, double right, boolean isLeftIncluded, boolean isRightIncluded)`.

All the new JDK 21 APIs can be found in [The Java Version Almanac -- New APIs in Java 22](https://javaalmanac.io/jdk/22/apidiff/21/ "The").

Internal changes, performance, and security {#h2-8-internal-changes-performance-and-security}
---------------------------------------------------------------------------------------------

The G1 Garbage Collector has seen an improvement when a JNI (Java Native Interface) call defines a critical region. Previously, G1 was totally disabled, with the risk of blocking application threads requiring a GC, or even out of memory.

Thanks to JEP 423: Region Pinning for G1, G1GC is now able to pin only a single region in the event of a JNI critical section, avoiding blocking other application threads requiring a GC. More information in the [JEP 423](https://openjdk.org/jeps/423 "JEP").

Parallel GC and Serial GC also have seen some optimisations in the card table scaning area (card table stores old-to-young references). Other changes on the Garbage Collector side can be found in this article by Thomas Schatzl: [JDK 22 G1/Parallel/Serial GC changes](https://tschatzl.github.io/2024/02/06/jdk22-g1-parallel-gc-changes.html).

JFR Events {#h2-9-jfr-events}
-----------------------------

Here are the new Java Flight Recorder (JFR) events of the JVM :

* `CompilerQueueUtilization`: *no description.*
* `NativeLibraryLoad`: information on a dynamic library or other native image loading operation.
* `NativeLibraryUnload`: information on a dynamic library or other native image unload operation.
* `DeprecatedInvocation`: unique invocation of a method annotated with `@Deprecated`.

You can find all the JFR events supported in this version of Java on the page [JFR Events](https://sap.github.io/SapMachine/jfrevents/22.html "JFR").

Conclusion {#h2-10-conclusion}
------------------------------

One might have thought that Java 22 would be a stabilizing release after version 21, which is LTS, but no, there's a major addition in the form of Stream Gatherer and a number of JEPs designed to simplify the language and make it easier to use.

Also noteworthy is that the Foreign Function \& Memory API goes out of preview, which will enable simplified use of native functions in Java, with a high-performance API that's easier to use than JNI.

To find all the changes in Java 22, refer to the [release notes](https://jdk.java.net/22/release-notes "release").

This article was first published in my personal blog: [JAVA 22: WHAT'S NEW?](https://www.loicmathieu.fr/wordpress/en/informatique/java-22-quoi-de-neuf/).

<br />

<br />
