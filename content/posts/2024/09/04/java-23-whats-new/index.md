---
title: "Java 23: What’s New? | Foojay.io today"
slug: "java-23-whats-new"
date: "2024-09-04T09:19:35+00:00"
lastmod: "2024-09-16T07:23:33+00:00"
description: "The latest version of Java is rather sparse in terms of new features, and few of those currently under development have made it out of the preview."
canonical: "https://www.loicmathieu.fr/wordpress/en/informatique/java-23-quoi-de-neuf/"
authors:
  - "loic-mathieu"
image: "java23.png"
categories:
  - "Java"
  - "JDK 23"
  - "JEPs"
  - "Release Notes"
tags:
related_posts:
  - "java-22-whats-new"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "java-where-the-wild-code-isnt"
enlighterjs: true
frozen: false
---

**As soon as Java 23 is out, it'll be time to walk through all the functionalities that this version bring to us as developers.**

First of all, something has happened that, to my knowledge, has never happened before in a Java release: a preview feature has been removed! The String Templates feature, which appeared as a preview feature in Java 21, has been removed. A global redesign of this feature will be carried out, as it had given rise to much debate and didn't seem to meet the community's expectations.

[JEP 12](https://openjdk.org/jeps/12), which defines the preview feature process, clearly states that a preview feature can be removed at the discretion of the feature owner, without the need for a new JEP.
> Eventually, the JEP owner must decide the preview feature's fate. If the decision is to remove the preview feature, then the owner must file an issue in JBS to remove the feature in the next JDK feature release; no new JEP is needed.

So that's what's been done here. More information on String Templates removal in ticket [JDK-8329949](https://bugs.openjdk.org/browse/JDK-8329949).

JEP 455 - Primitive Types in Patterns, instanceof, and switch (Preview) {#h2-0-jep-455-primitive-types-in-patterns-instanceof-and-switch-preview}
-------------------------------------------------------------------------------------------------------------------------------------------------

Preview feature that adds support for primitive types in `instanceof` and `switch`, and enhances pattern matching to support primitive type patterns: in `instanceof`, in `switch` cases, and in record deconstruction. **Switches now support all primitive types.**

Example:

```
long l = ...;
switch (l) {
    case 1L              -> ...;
    case 2L              -> ...;
    case 10_000_000_000L -> ...;
    default -> ...;
}
```


**We can now use `instanceof` for all primitive types.**

Example from the JEP:

```
if (i instanceof byte) {  // value of i fits in a byte
    ... (byte)i ...       // traditional cast required
}
```


But most interesting of all is the support for pattern matching. Here are some examples of where it's now possible to use pattern matching for a primitive type.

Example of pattern matching of a primitive type within a `switch` from the JEP:

```
switch (x.getStatus()) {
    case 0 -> okay;
    case 1 -> warning;
    case 2 -> error;
    case int i -> unknown status:  + i;
}
```


Guards are also supported via the `when` clause:

```
switch (x.getStatus()) {
    case 0 -> okay;
    case 1 -> warning;
    case int i when i > 1 && i  client error:  + i;
    case int i when i > 100 -> server error:  + i;
}
```


Example of pattern matching of a primitive type within an `instanceof` from the JEP:

```
if (i instanceof byte b) {
    ... b ...
}
```


Example of pattern matching of a primitive type when deconstructing a record:

```
// JSON didn't differentiates integers to doubles, so a JSON number should be a double.
record JsonNumber(double number) implements JsonValue { }

var number = new JsonNumber(30);
// Previously we can only deconstruct this record via a it's exact component type: double,
// now we can deconstruct and match a different primitive type
if (json instanceof JsonObject(int number)) {
    // ...
}
```


This evolution necessitated the implementation of conversion rules within pattern matching, so that a primitive type matches another primitive type, as in the previous example 30 matched an `int` even though the record component was defined as a double, the target type must be covered by the pattern test. Here, 30 is covered by an int. Values not covered will be rejected.

More information in the [JEP 455](https://openjdk.org/jeps/455 "JEP").

JEP 467 - Markdown Documentation Comments {#h2-1-jep-467-markdown-documentation-comments}
-----------------------------------------------------------------------------------------

Feature that lets you write **JavaDoc** documentation comments in [Markdown](https://wikipedia.org/wiki/Markdown "Markdown") and not just with a mix of HTML and JavaDoc tags.

Writing HTML code isn't always easy or very readable without rendering, and JavaDoc tags are sometimes complicated to use. Markdown is a language that is both readable without rendering, and simple to use. Using it for JavaDoc comments is a great alternative.

Markdown supports the use of HTML tags, offering great flexibility, while JavaDoc-specific tags are still supported if required. A Markdown comment begins with 3 slashes: `///`.

Here's an example from the JEP:

```
/**
 * Returns a hash code value for the object. This method is
 * supported for the benefit of hash tables such as those provided by
 * {@link java.util.HashMap}.
 * <p>
 * The general contract of {@code hashCode} is:
 * <ul>
 * <li>Whenever it is invoked on the same object more than once during
 *     an execution of a Java application, the {@code hashCode} method
 *     must consistently return the same integer, provided no information
 *     used in {@code equals} comparisons on the object is modified.
 *     This integer need not remain consistent from one execution of an
 *     application to another execution of the same application.
 * </li><li>If two objects are equal according to the {@link
 *     #equals(Object) equals} method, then calling the {@code
 *     hashCode} method on each of the two objects must produce the
 *     same integer result.
 * </li><li>It is $ly;em>not$lt;/em> required that if two objects are unequal
 *     according to the {@link #equals(Object) equals} method, then
 *     calling the {@code hashCode} method on each of the two objects
 *     must produce distinct integer results.  However, the programmer
 *     should be aware that producing distinct integer results for
 *     unequal objects may improve the performance of hash tables.
 * </li></ul>
 *
 * @implSpec
 * As far as is reasonably practical, the {@code hashCode} method defined
 * by class {@code Object} returns distinct integers for distinct objects.
 *
 * @return  a hash code value for this object.
 * @see     java.lang.Object#equals(java.lang.Object)
 * @see     java.lang.System#identityHashCode
 */
```


Which would be written like this in Markdown:

```
/// Returns a hash code value for the object. This method is
/// supported for the benefit of hash tables such as those provided by
/// [java.util.HashMap].
///
/// The general contract of `hashCode` is:
///
///   - Whenever it is invoked on the same object more than once during
///     an execution of a Java application, the `hashCode` method
///     must consistently return the same integer, provided no information
///     used in `equals` comparisons on the object is modified.
///     This integer need not remain consistent from one execution of an
///     application to another execution of the same application.
///   - If two objects are equal according to the
///     [equals][#equals(Object)] method, then calling the
///     `hashCode` method on each of the two objects must produce the
///     same integer result.
///   - It is _not_ required that if two objects are unequal
///     according to the [equals][#equals(Object)] method, then
///     calling the `hashCode` method on each of the two objects
///     must produce distinct integer results.  However, the programmer
///     should be aware that producing distinct integer results for
///     unequal objects may improve the performance of hash tables.
///
/// @implSpec
/// As far as is reasonably practical, the `hashCode` method defined
/// by class `Object` returns distinct integers for distinct objects.
///
/// @return  a hash code value for this object.
/// @see     java.lang.Object#equals(java.lang.Object)
/// @see     java.lang.System#identityHashCode
```


More information in the [JEP 467](https://openjdk.org/jeps/467 "JEP").

JEP 471 - Deprecate the Memory-Access Methods in sun.misc.Unsafe for Removal {#h2-2-jep-471-deprecate-the-memory-access-methods-in-sun-misc-unsafe-for-removal}
---------------------------------------------------------------------------------------------------------------------------------------------------------------

**Unsafe** is, as its name suggests, an internal and unsupported JDK class that is not safe to call.

For historical reasons, many low-level frameworks used Unsafe for faster memory access.

Thanks to the **VarHandle API** [(JEP 193](https://openjdk.org/jeps/193), since Java 9) and **Foreign Function \& Memory API** [(JEP 454](https://openjdk.org/jeps/454), since Java 22) features, there are now replacements for Unsafe's memory-access methods that are just as powerful, but safer and more supported.

In total, more than 79 of Unsafe's 87 methods have been deprecated, bringing us closer to the point where the entire class can be deprecated and deleted! Deprecating these methods clearly indicates that it's time to use these replacements!

Nevertheless, most of us shouldn't see these changes, as Unsafe is rarely used anywhere other than in frameworks or libraries.

These methods will be progressively degraded and deprecated in phases:

* Phase 1: depreciation in Java 23 (this JEP)
* Phase 2: log a warning if used at runtime, in Java 24 or 25
* Phase 3: throw an exception by default (behavior modifiable via a command-line option), in Java 26 or higher
* Phases 4 and 5: removal of the methods after Java 26 (on-heap, then off-heap memory access methods)

More information in the [JEP 471](https://openjdk.org/jeps/471 "JEP").

JEP 474 - ZGC: Generational Mode by Default {#h2-3-jep-474-zgc-generational-mode-by-default}
--------------------------------------------------------------------------------------------

ZGC is a Garbage Collector designed to support very large heaps (several terabytes) with very low pauses (on the order of milliseconds).

The addition of a generational heap in Java 21 via [JEP 439](https://openjdk.org/jeps/439) enables it to support different workloads while consuming fewer resources.

Generational mode is now the default.

More information in the [JEP 474](https://openjdk.org/jeps/474 "JEP").

JEP 476 - Module Import Declarations (Preview) {#h2-4-jep-476-module-import-declarations-preview}
-------------------------------------------------------------------------------------------------

In Java, you can import:

* All classes from a package with the statement `import java.util.*;`
* A single class with the statement `import java.util.Map;`
* All static methods and variables of a class with the statement `import static org.junit.jupiter.api.Assertions.*;`
* A single static method or variable with the statement `import static org.junit.jupiter.api.Assertions.assertTrue;`

However, it wasn't possible to import all the classes of a module in a single statement.

This is now done with the `import module java.base;` statement, which in a single statement that imports all the classes of all the packages exported from the `java.base` module, as well as those of modules transitively required by `java.base`.

More information in the [JEP 476](https://openjdk.org/jeps/476 "JEP").

Features coming out of preview {#h2-5-features-coming-out-of-preview}
---------------------------------------------------------------------

No feature previously in preview (or incubator module) has been released from preview or incubation in Java 23. Apart, of course, the String Templates feature I mentioned in the introduction, which has been removed from the preview to go nowhere.

Features that remain in preview {#h2-6-features-that-remain-in-preview}
-----------------------------------------------------------------------

The following features remain in preview (or in the incubator module).

* [JEP 466](https://openjdk.org/jeps/466) - **Class-File API**: second preview, standard API for parsing, generating and transforming Java class files. Improvements based on user feedback. The JDK migration to use this new API continued in Java 23.
* [JEP 469](https://openjdk.org/jeps/469) - **Vector API**: eighth incubation, an API for expressing vector calculations that compile at runtime into vector instructions for supported CPU architectures. No change: the JEP agreed that the Vector API will remain in incubation until project Valhalla's functionalities are available as a preview. This was expected, as the Vector API will then be able to take advantage of the performance and in-memory representation improvements expected from the project Valhalla.
* [JEP 473](https://openjdk.org/jeps/473) - **Stream Gatherers**: second preview, enhances the Stream API with support for custom intermediate operations. No changes.
* [JEP 477](https://openjdk.org/jeps/477) - **Implicitly Declared Classes and Instance Main Methods** : third preview, simplifies the writing of simple programs by allowing them to be defined in an implicit class (without declaration) and in a `void main()` instance method. Two changes: implicit classes automatically import the 3 static methods `print(Object)`, `println(Object` ) and `readln(Object)` of the new `java.io.IO` class, and they automatically import, on demand, classes from packages in the `java.base` module.
* [JEP 480](https://openjdk.org/jeps/480) - **Structured Concurrency**: third preview, a new API that simplifies the writing of multi-threaded code by allowing multiple concurrent tasks to be treated as a single processing unit. No changes.
* [JEP 481](https://openjdk.org/jeps/481) - **Scoped Values** : third preview, allow immutable data to be shared within and between threads. A minor change.
* [JEP 482](https://openjdk.org/jeps/482) - **Flexible Constructor Bodies** : second preview, a feature that allows instructions to be called **before** the parent constructor as long as they do not access the instance currently being created. A constructor can now initialize fields of the same class before explicitly calling a constructor.

Miscellaneous {#h2-7-miscellaneous}
-----------------------------------

Various additions to the JDK:

* Following the [JEP 477](https://openjdk.org/jeps/477) - **Implicitly Declared Classes and Instance Main Methods** , in addition to the new `IO` class, the same 3 methods have been added to the `Console` class: `print(Object)`, `println(Object)` and `readln(Object)`.
* The `Console` class has seen the addition of 3 new methods for using strings formatted with a locale: `format(Locale, String, Object)`, `printf(Locale, String, Object)` and `readLine(Locale, String, Object)`.
* `Console.readPassword(Locale, String, Object)`: identical to `Console.readPassword(String, Object)` but takes a locale as parameter for string localization.
* `Inet4Address.ofPosixLiteral(String)`: creates an `Inet4Address` based on the supplied textual representation of an IPv4 address in POSIX-compatible form **inet_addr**.
* `java.text.NumberFormat` and its descendants have seen the addition of the `setStrict(boolean)` and `isStrict()` methods, which can be used to change the formatting mode; the default mode is strict.
* `Instant.until(Instant)` : compute the `Duration` until another `Instant`.

The following methods have been removed, they had been deprecated for deletion, and degraded to throw an exception in a previous release:

* `Thread.resume()` and `Thread.suspend()`.
* `ThreadGroup.resume()`, `ThreadGroup.stop()` eand `ThreadGroup.suspend()`.

All the new JDK 23 APIs can be found in [The Java Version Almanac -- New APIs in Java 23](https://javaalmanac.io/jdk/23/apidiff/22/ "The").

Internal changes, performance, and security {#h2-8-internal-changes-performance-and-security}
---------------------------------------------------------------------------------------------

The Parallel GC garbage collector has seen a re-implementation of its Full GC algorithm to use a more classic **parallel Mark-Sweep-Compact** algorithm.

This is the same as the one used by the G1 garbage collector and optimizes performance in certain specific cases, reducing heap usage by 1.5% when using Paralle GC.

Other changes have been made on the Garbage Collector side, and can be found in this article by Thomas Schatzl: [JDK 23 G1/Parallel/Serial GC changes](https://tschatzl.github.io/2024/07/22/jdk23-g1-serial-parallel-gc-changes.html).

JFR Events {#h2-9-jfr-events}
-----------------------------

No new Java Flight Recorder (JFR) events. You can find all the JFR events supported in this version of Java on the page [JFR Events](https://sap.github.io/SapMachine/jfrevents/23.html "JFR").

Conclusion {#h2-10-conclusion}
------------------------------

This new version of Java is rather sparse in terms of new features, and few of those currently under development have made it out of the preview.

The support of primitive types in pattern matching and Markdown support in JavaDoc are nonetheless very interesting improvements, but the disappearance of String Templates without a replacement means that support for this long-awaited feature is not around the corner.

To find all the changes in Java 23, refer to the [release notes](https://jdk.java.net/23/release-notes).

This article was first published in my personal blog:[Java 23 : what's new?](https://www.loicmathieu.fr/wordpress/en/informatique/java-23-quoi-de-neuf/).
