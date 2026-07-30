---
title: "Hidden and Not-So-Hidden Gems In Java 20"
slug: "hidden-and-not-so-hidden-gems-in-java-20"
date: "2023-05-03T14:32:45+00:00"
lastmod: "2023-05-03T14:34:21+00:00"
description: "Let's see the preview and incubator JEPs in Java 20, as well as many smaller enhancements, bug fixes, and deprecations."
canonical: "https://blogs.oracle.com/javamagazine/post/java-20-gems-jdks"
authors:
  - "mohamed-taman"
image: "/images/posts/2023/05/hidden-and-not-so-hidden-gems-in-java-20/Medium.jpeg"
categories:
  - "Java Core"
  - "JEPs"
  - "Release Notes"
tags:
related_posts:
  - "its-java-20-release-day-heres-whats-new"
  - "foojay-podcast-16"
  - "the-5-most-pivotal-and-innovative-additions-to-openjdk-19"
enlighterjs: true
frozen: false
---

**Let's see the preview and incubator JEPs in Java 20, as well as many smaller enhancements, bug fixes, and deprecations.**

March 2023 marked the latest feature release of the Java platform, which was delivered on time through the six-month release cadence as Java 20. Like Java 19, which became generally available in September 2022, Java 20 targeted JEPs---seven, in this case---from the [Panama](https://openjdk.org/projects/panama/), [Amber](https://openjdk.org/projects/amber/), and [Loom](https://wiki.openjdk.org/display/loom) projects.

I am using the Java 20 jshell tool to demonstrate the code in this article. To follow along, [download JDK 20](https://foojay.io/download/), fire up your terminal, check your version, and run jshell. Note that you might see a newer version of the JDK; that's okay.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">[mtaman]:~ java -version
 openjdk 20 2023-03-21
 OpenJDK Runtime Environment (build 20+36-2344)
 OpenJDK 64-Bit Server VM (build 20+36-2344, mixed mode, sharing)

[mtaman]:~ jshell --enable-preview
|  Welcome to JShell -- Version 20
|  For an introduction type: /help intro

Jshell&gt;</pre>

Three JEPs in Java 20 are published as incubator modules to solicit developer feedback. An incubator module's API could be altered or disappear entirely---that is, not be released in future JDK releases. Therefore, you shouldn't use incubator features in production code. To use the incubator modules, use the `--add-modules` JVM flag.

The other JEPs in Java 20 are preview features. Those features are fully specified and implemented but are provided in an early release to gather feedback. You should assume that preview features will change and not use them in production code. Use the `--enable-preview` switch to use such features.

That's not to say you shouldn't use Java 20 itself in production - you should! Beyond the JEPs, there are other small improvements and fixes. The only things you shouldn't use in production are the incubator and preview features.

In this article, I'll describe those seven JEPs and then dive into other changes in Java 20, including changes that didn't rise to the level of JEPs, platform enhancements, bug fixes and changes, and deprecations and removals.

By the way, my previous "[Java 20 sneak peek](https://blogs.oracle.com/javamagazine/post/java-20-preview)" article goes into the JEPs with coding examples, which are not duplicated here. This article is long enough without them!

The JEPs in Java 20 {#h2-0-the-jeps-in-java-20}
-----------------------------------------------

Seven successful JEPs made it into the Java 20 release (one more than predicted in my previous article). Scoped values are a new incubating feature, while the remaining six JEPs are resubmissions and updates of already-known incubator and preview features that appeared in Java 19 or earlier.

These JEPs are often grouped based on their long-term Java projects:

* **Project Loom** , which is focused on concurrency
  * 429: [Scoped values](https://openjdk.org/jeps/429) (first incubator)
  * 436: [Virtual threads](https://openjdk.org/jeps/436) (second preview)
  * 437: [Structured concurrency](https://openjdk.org/jeps/437) (second incubator)
* **Project Amber** , for new language features
  * 432: [Record patterns](https://openjdk.org/jeps/432) (second preview)
  * 433: [Pattern matching for switch](https://openjdk.org/jeps/433) (fourth preview)
* **Project Panama** , for non-Java libraries
  * 434: [Foreign function and memory API](https://openjdk.org/jeps/434) (second preview)
  * 438: [Vector API](https://openjdk.org/jeps/438) (fifth incubator)

### Project Loom {#h3-1-project-loom}

Project Loom is designed to deliver JVM features and APIs that support easy-to-use, high-throughput lightweight concurrency (virtual threads) and new programming models (structured concurrency). New in the project is a handy construct (scoped values) that provides a thread (and, if needed, a group of child threads) with a read-only, thread-specific value during its lifetime. Scoped values are a modern alternative to thread-local variables.

**Scoped values (first incubator), JEP 429**. With the advent of virtual threads, the issues with thread-local variables have worsened. Thread-local variables require more complexity than is typically required for data sharing and come at a high cost that cannot be avoided.

That's why the Java platform is now offering, in incubator form, scoped values. (For a short time, they were called extent-local variables.) They help developers move toward lightweight sharing for thousands or millions of virtual threads. JEP 429 proposes to maintain immutable and inheritable per-thread data. These per-thread variables allow for effective data sharing between child threads.

Additionally, the lifetime of per-thread variables ought to be constrained: Once the method that initially shared the data finishes, any data shared via a per-thread variable should no longer be accessible.

**Virtual threads (second preview), JEP 436**. Virtual threads fundamentally redefine the interaction between the Java runtime and the underlying operating system, removing significant barriers to scalability. Still, they don't dramatically change how you create and maintain concurrent programs. Virtual threads behave almost identically to the familiar threads, and there is barely any additional API.

Java 19 introduced virtual threads to the Java platform as a first preview. In Java 20, JEP 436 provides a second preview of virtual threads in the older JEP 425 to allow time for further feedback collection. If there is no more feedback or if no significant enhancements are made to JEP 436, virtual threads will likely be a production-ready feature in the upcoming Java 21 release.

**Structured concurrency (second incubator), JEP 437**. The structured concurrency API aims to make multithreaded programming easier to help simplify error management and cancellation; it treats concurrent tasks operating in distinct threads as a single unit of work---improving observability and dependability.

JEP 437 essentially republishes the Java 19 version of JEP 428 - to allow time to gather more feedback. The only significant change is an updated StructuredTaskScope class that supports the inheritance of scoped values (from JEP 429) by threads created in a task scope. This streamlines the sharing of immutable data across all child threads.

### Project Amber {#h3-2-project-amber}

JEP 432 and JEP 433 are connected to Project Amber, which is designed to improve developers' productivity.

**Record patterns (second preview), JEP 432**. Record patterns were proposed as a preview feature in Java 19's JEP 405. Record patterns provide an elegant way to access a record's elements after a type check. JEP 432 is a second preview that provides further refinements. The main changes since the first preview

* Add support for an inference of type arguments for generic record patterns
* Add support for record patterns to appear in the header of an enhanced statement
* Remove support for named record patterns

**Pattern matching for switch (fourth preview), JEP 433**. Pattern matching for switch was proposed as a preview feature several times since Java 17. JEP 433 offers a fourth preview to enable the continued coevolution of pattern matching with the record patterns preview feature in JEP 432. The following are the main changes since the third preview:

* An exhaustive switch (that is, a switch expression or a pattern switch statement) over an enum class now throws a MatchException rather than an IncompatibleClassChangeError if no switch label applies at runtime.
* The switch label's grammar has been simplified.
* Inference of type arguments for generic record patterns is now supported in switch expressions and statements, along with the other constructs that support patterns.

### Project Panama {#h3-3-project-panama}

This initiative, which includes JEP 434 and JEP 438, aims to improve interoperability between the JVM and well-defined "foreign" (non-Java) APIs. These APIs often include interfaces that are used in C libraries.

**Foreign function and memory API (second preview), JEP 434**. This JEP defines an API through which Java programs can interoperate with code and data outside the Java runtime. By efficiently invoking foreign functions that are outside the JVM---and safely accessing foreign memory that the JVM doesn't manage---this API enables Java programs to call native libraries and process native data without the brittleness and danger of Java Native Interface (JNI).

There have been several versions of this feature since Java 17. Initially two separate APIs were proposed, but those were integrated into a single JEP in Java 19. JEP 434 incorporates the following additional refinements based on feedback:

* The MemorySegment and MemoryAddress abstractions are unified, so zero-length memory segments now model memory addresses.
* The sealed MemoryLayout hierarchy facilitates usage with pattern matching in the switch expressions and statements provided by JEP 433.
* MemorySession has been split into an arena scope and a SegmentScope to facilitate sharing segments across maintenance boundaries. An arena scope provides a bounded and deterministic lifetime; it is alive from the time when a client opens an arena until the client closes the arena.

**Vector API (fifth incubator), JEP 438**. This API helps you express vector computations that reliably compile at runtime to optimal vector instructions on supported CPU architectures. The goal is to achieve much faster performance than with scalar computations. Versions of this API have been incubated since Java 16. This fifth version has a small set of bug fixes and performance enhancements.

Hidden gems: The most important non-JEP changes {#h2-4-hidden-gems-the-most-important-non-jep-changes}
------------------------------------------------------------------------------------------------------

Java 20 shipped with hundreds of performance, stability, and security improvements beyond the seven JEPs described earlier. Here are the most significant non-JEP changes.

**Warnings about type casts in compound assignments with possible lossy conversions**. When I ask developers about computations that involve compound assignments, many of them give a wrong answer because they don't know that when the right-hand operand's type in a compound assignment is not compatible with the type of the variable, a type cast is implied. Data may be lost due to possible lossy conversion.

So, for example, what is the difference between the following code lines when they are compiled?

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">a = a + b;
a += b;</pre>

Many Java developers will say there is no difference. However, these two operations are not always equivalent in Java. If a is a short and b is an int, the second operation will result in the following compiler error because a + b returns an int that cannot be assigned to the short variable a without an explicit cast:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">LossyConversions.java:8: error: incompatible types: possible lossy conversion from int to short
               a = a + b;
                     ^</pre>

By contrast, a += b is allowed because the compiler inserts an implicit cast in a compound assignment. The statement a += b is equivalent to a = (short) (a + b), where the left 16 bits of the int result are truncated when casting to a short, resulting in the potential loss of information. The following interface will compile without any warning; however, when you run it, you may get unexpected results:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public interface LossyConversions {

       static void main(String... args) {

              short a = 30000;
              int b   = 45000;

              a += b;
              System.out.println(a);
       }
}

[mtaman]:~ javac LossyConversions.java
[mtaman]:~ java LossyConversions
    9464</pre>

Notice that the result isn't the expected 75,000. It's the truncated result of conversion loss: 9,464.

To help with this issue, Java 20 introduced a [compiler (javac) lint option](https://docs.oracle.com/en/java/javase/20/docs/specs/man/javac.html#options) called lossy-conversions, which generates warnings about type casts in compound assignments (such as += or \*=) that could result in data loss. Warnings such as the following alert developers about potentially undesirable behavior:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">[mtaman]:~ javac -Xlint:lossy-conversions LossyConversions.java
LossyConversions.java:8: warning: [lossy-conversions] implicit cast from int to short in compound assignment is possibly lossy
              a += b;
                   ^
1 warning</pre>

You can silence these warnings using the `@SuppressWarnings("lossy-conversions")` annotation, but you shouldn't.

**New APIs for TLS and DTLS key-exchange named groups**. Java 20 added two new APIs to allow customization of the named groups of key-exchange algorithms used in Transport Layer Security (TLS) and Datagram Transport Layer Security (DTLS) connections on a per-connection basisNew APIs for TLS and DTLS key-exchange named groups. Java 20 added two new APIs to allow customization of the named groups of key-exchange algorithms used in Transport Layer Security (TLS) and Datagram Transport Layer Security (DTLS) connections on a per-connection basis\*.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">javax.net.ssl.SSLParameters.getNamedGroups()
javax.net.ssl.SSLParameters.setNamedGroups()</pre>

The underlying key provider may define the default named groups for each connection. However, the named groups can be customized by setting the jdk.tls.namedGroups system property or by using the setNamedGroups() method. If the system property is not null and the setNamedGroups() method is used, the named groups that are passed will override the default named groups for the specified connection.

Note that some providers may not support these new APIs. In such cases, the provider may ignore the named groups set.

**New JMOD command-line option**. The jmod tool lets you create JMOD archives, a new type of archive introduced in Java 9 that provides a modular way to package Java runtime components and their dependencies. Java 20 provides the --compress option, which lets you specify the compression level to use when you create a JMOD archive.

By default, the jmod tool compresses the contents of the JMOD archive using the ZIP file format. The accepted values for the --compress option are zip-0 through zip-9, where zip-0 indicates no compression and zip-9 provides the best compression. The default value is zip-6.

Why not always choose zip-9? If you are optimizing for speed of compression or decompression, you might want to choose a lower value or even no compression.

**GarbageCollectorMXBean for remark and cleanup pause time in G1**. The Garbage-First (G1) garbage collector now has a new GarbageCollectorMXBean called G1 Concurrent GC. This bean reports the duration and occurrence of the remark and cleanup garbage collection pauses.

These pauses occur during a complete concurrent mark cycle, increasing the collection counter by two (one for the remark pause and one for the cleanup pause), similar to the CGC field of the jstat -gcutil command. During these pauses, G1 Concurrent GC also updates the memory pool for the G1 Old Gen MemoryManagerMXBean.

**Improved control of G1 concurrent refinement threads**. In Java 20, the G1 garbage collector has a new controller for concurrent refinement threads; it allocates fewer threads and delays refinement to improve the efficiency of the write barrier.

As a result, the old command-line options that were used to provide parameter values for the old controller are deprecated. Specifying any of the following options on the command line will print a warning message:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">-XX:-G1UseAdaptiveConcRefinement
-XX:G1ConcRefinementGreenZone=buffer-count
-XX:G1ConcRefinementYellowZone=buffer-count
-XX:G1ConcRefinementRedZone=buffer-count
-XX:G1ConcRefinementThresholdStep=buffer-count
-XX:G1ConcRefinementServiceIntervalMillis=msec</pre>

These options will be removed entirely in the future, and their use after that time will terminate the startup of the JVM.

**Preventive garbage collections disabled by default**. The G1 garbage collector in Java 17 introduced preventive garbage collections, thereby avoiding costly evacuation failures caused by allocation bursts when the heap was almost full.

However, those preventive collections result in additional garbage collection work because object aging is based on the number of garbage collections. This causes premature promotion into the old generation, resulting in more data and increased garbage collection work to remove these objects. The current prediction to trigger preventive garbage collections is very conservative, which triggers garbage collections unnecessarily.

In most cases, this feature is more detrimental than beneficial. Because evacuation failures are handled more efficiently, this feature has been disabled by default in Java 20. It can still be re-enabled using the command-line options `-XX:+UnlockDiagnosticVMOptions` and `-XX:+G1UsePreventiveGC`.

**Unicode 15.0 support** . Java 20 has been upgraded to Unicode version 15.0, which includes several updates, such as the Unicode Character Database and Unicode Standard Annexes #9, #15, and #29. For more details, see the [Unicode Consortium's release notes](https://unicode.org/versions/Unicode15.0.0/).

On a similar note, Java's `java.text.BreakIterator` now follows the [Extended Grapheme Cluster](https://unicode.org/reports/tr29/) breaks defined in the Unicode Consortium's Standard Annex #29 for character boundary analysis. This update may result in deliberate behavioral changes because the prior implementation primarily split at code point boundaries for most characters.

Also in Java 20, the locale data has been upgraded to use the [Unicode Consortium's Common Locale Data Repository (CLDR) version 42](https://cldr.unicode.org/index/downloads/cldr-42). Some noteworthy upstream changes that may impact formatting are the following:

* The use of at for the standard date and time format has been discontinued.
* The nonbreaking space has been prefixed to a instead of a regular space.
* The first-day-of-the-week information for China (CN) has been fixed.
* Japanese now supports numbers up to 9,999京.

**Time zone data updated to IANA version 2023c** . [Version 2023c tz](https://mm.icann.org/pipermail/tz-announce/2023-March/000079.html) incorporates modifications from the 2022b and 2022a releases, which have combined various regions with identical time stamp data post-1970 into a single time zone database. While all time zone IDs remain the same, the combined time zones refer to a shared zone database.

Hidden gems: Java 20 enhancements {#h2-5-hidden-gems-java-20-enhancements}
--------------------------------------------------------------------------

**Optimized intrinsic features for encryption algorithms** . Java 20 provides two new intrinsic features. Remember that flags controlling intrinsics require the option `-XX:+UnlockDiagnosticVMOptions`.

* Java 20 provides the Poly1305 intrinsic on x86_64 platforms with AVX512 instructions. This optimizes the Poly1305 message authentication code algorithm of the SunJCE provider on x86_64 platforms using AVX512 instructions. The Poly1305 intrinsic feature is enabled by default on supporting x86_64 platforms. However, to disable this feature, you can use the `-XX:-UsePoly1305Intrinsics` command-line option.
* Java 20 provides the ChaCha20 intrinsics on x86_64 and aarch64 platforms. This provides optimized intrinsic implementations for the ChaCha20 cipher used by the SunJCE provider. These optimized routines are designed for x86_64 chipsets that support the AVX, AVX2, or AVX512 instruction sets and for aarch64 chips that support the Advanced SIMD instruction set. These intrinsics are enabled by default on supported platforms, but you can disable them using the `-XX:-UseChaCha20Intrinsics` command-line option.

**New JDK Flight Recorder events**. Java 20 provides two new events.

* `jdk.InitialSecurityProperty`: This event records details of initial security properties loaded via the `java.security.Security` class, and it contains two fields. The first is a key, which represents the security property key, and the second is a value corresponding to the security property value. This new event is enabled by default. The system property `java.security.debug=properties` will print initial security properties to the standard error stream with this new event and the existing `jdk.SecurityPropertyModification` event (which is not enabled by default). A JDK Flight Recorder recording can monitor the initial settings of all security properties and any subsequent changes.
* `jdk.SecurityProviderService`: This event records details of calls made to the method `java.security.Provider.getService(String type, String algorithm);` its fields include the type of service, algorithm name, and security provider. It is not enabled by default but can be activated through JDK Flight Recorder configuration files or standard options.

**Javadoc improvements**. There are three Javadoc improvements.

* Javadoc can now autogenerate unique ID attributes for all HTML headings in documentation comments. These IDs can be used as link anchors for easier navigation within the generated documentation. This update is especially useful for larger documents with many sections and subsections, allowing users to jump to specific content quickly.
* The generalized `{@link}`, `{@linkplain}`, and `@see` tags have been improved to enable linking to specific user-defined anchors in the Javadoc-generated documentation for an element. A double hash mark (##) separates the element name from the URI fragment to distinguish these references from member references. This enhancement provides more flexibility in navigating the documentation, allowing links to be created to specific sections or headings within a document.
* The Javadoc documentation now includes detailed information about the JEPs related to the preview features on the Preview API page. This gives you a better understanding of the origin and purpose of preview features, as you can see by comparing Figure 1 and Figure 2.

![](https://blogs.oracle.com/content/published/api/v1.1/assets/CONT3D1669D776474319A97D4283A9B1A805/Medium?cb=_cache_04f2&format=jpg&channelToken=4d6a6a00a153413e9a7a992032379dbf)

Figure 1. Java 19 documentation preview list
![](https://blogs.oracle.com/content/published/api/v1.1/assets/CONT88C9ED57E8CB44DE98B3601B0FD3BC77/Medium?cb=_cache_04f2&format=jpg&channelToken=4d6a6a00a153413e9a7a992032379dbf)

Figure 2. Java 20 documentation preview list

**New constructors for the java.security.InvalidParameterException class** . Two new constructors - `InvalidParameterException(String, Throwable)` and `InvalidParameterException(Throwable)` - simplify the creation of InvalidParameterException objects with a cause.

**Methods for converting to and from half-precision floating-point format** . Two new methods, `java.lang.Float.floatToFloat16` and `java.lang.Float.float16ToFloat`, can convert to and from the [IEEE 754 binary16](https://en.wikipedia.org/wiki/Half-precision_floating-point_format) half-precision format. However, when the JIT compiler optimizes these methods, they may return different NaN (not a number) results.

To prevent the JIT compiler from optimizing these methods, use the following command-line options:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">-XX:+UnlockDiagnosticVMOptions -XX:DisableIntrinsic=_floatToFloat16,_float16ToFloat</pre>

Hidden gems: Java 20 bug fixes and related changes {#h2-6-hidden-gems-java-20-bug-fixes-and-related-changes}
------------------------------------------------------------------------------------------------------------

**Java XSL Template limitations**. Suppose you use the JDK's XSLT processor to convert stylesheets to Java objects. If the XSL template is too large, you may encounter an exception.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">com.sun.org.apache.xalan.internal.xsltc.compiler.util.InternalError: Internal XSLTC error: a method in the translet exceeds the Java Virtual Machine limitation on the length of a method of 64 kilobytes. This is usually caused by templates in a stylesheet that are very large. Try restructuring your stylesheet to use smaller templates.</pre>

To avoid this issue, you can restructure your stylesheet to smaller templates or use third-party JAR files in the classpath to override the JDK's XSLT processor.

**Changes to the java.net core libraries**. The following four changes were made:

* HTTP response input streams will throw an IOException upon interrupt. When you use the `ResponseSubscribers::ofInputStream` method, it returns an InputStream instance known as the HTTP response input stream. In Java 20, the default implementation of the read method in these streams has been changed.Previously, if the thread performing the read operation was interrupted, the interruption was ignored. But now, if the same thread is interrupted while blocking the read operation, the request will be canceled; the input stream will be closed; the thread's interrupt status will be set to true; and an IOException will be thrown.

* URL constructors called with malformed input may throw a MalformedURLException in cases where it was not thrown previously. In Java 20, how input is parsed in the URL constructors has become stricter. If you call these constructors with input that is not correctly formatted, you may get a MalformedURLException exception thrown that wouldn't have been thrown before.Previously, some validation and parsing checks were done when the `URL::openConnection` or `URLConnection::connect` methods were called. Now, these checks are done earlier, within the URL constructors themselves. This means that if there's an issue with the URL's formatting that would have previously been delayed until the connection was opened, it may now cause a MalformedURLException to be thrown when the URL is constructed.

  This change affects only URLs that use the JDK's built-in stream handler implementations. URLs using third-party implementations are not affected. To return to the previous behavior, set the system property `-Djdk.net.url.delayParsing=true` when you run your program. Note that this property is provided for backward compatibility and may be removed in the future.
* The default timeout value for idle connections created by `java.net.http.HttpClient` has been changed in Java 20. The HttpClient default keep-alive time is now 30 seconds. This means that if a connection remains idle for 30 seconds, it will be closed by the HttpClient. Previously, the default timeout value for HTTP/1.1 and HTTP/2 connections was 1,200 seconds.
* Java 20 introduces idle connection timeouts for HTTP/2. The `jdk.httpclient.keepalivetimeout` property can be used to set a systemwide value in seconds to close idle connections for HTTP/1.1 and HTTP/2 when the HttpClient is used. Additionally, you can use the `jdk.httpclient.keepalivetimeout.h2` property to set a timeout value exclusively for HTTP/2, regardless of whether the `jdk.httpclient.keepalivetimeout` property is set at runtime.

**Changed behavior for java.math.BigDecimal.movePointLeft() and java.math.BigDecimal.movePointRight()**. Suppose you use these two methods with an argument of zero on a negative scale target. With Java 20, the methods will return a result numerically equivalent to the target but with a different unscaled value and scale.

In earlier releases, these methods returned a result with the same unscaled value and scale, which did not align with the specification. However, this change applies only to cases where the target has a negative scale and the argument is zero; in other cases, the behavior remains the same.

**Object identity used for IdentityHashMap's remove and replace methods** . IdentityHashMap is a unique type of map that considers keys to be equal only if they are identical. A comparison using the `==` operator returns `true` rather than the `equals()` method.

However, when the default `remove(Object key, Object value)` and `replace(K key, V oldValue, V newValue)` methods were added to the Map interface in Java 8, they were not overridden in IdentityHashMap to use `==` instead of `equals()`, leading to a bug. That has been fixed in Java 20.

**FileChannel positional write unspecified in APPEND mode** . The Java specification for `java.nio.channels.FileChannel` has been updated to provide clarity on the behavior of the `FileChannel::write(ByteBuffer, long)` method when an attempt to write to a specific position is performed.

When `java.nio.File.StandardOpenOption.APPEND` is passed to `FileChannel::open` when a channel is opened, the behavior of writing to a specific position is system-dependent. At the same time, the channel is opened in append mode.

This means that the outcome can differ depending on the operating system. In some operating systems, bytes will be written to the specified position. In other operating systems, the given position will be ignored, and the bytes will be appended to the end of the file.

**Filenames for Unicode Normalization Format D (NFD) not normalized on macOS**. The normalization of filenames for Apple's NFD version has been changed on macOS. Previously, filenames were normalized to NFD on HFS+ prior to macOS 10.13. However, this normalization is no longer performed on the APFS file system on macOS 10.13 and later.

If you want to revert to the previous behavior and normalize filenames to NFD, set the system property `jdk.nio.path.useNormalizationFormD` to `true`.

**HelloVerifyRequest messages used for DTLS resumption** . In Java 20, a fix was made to the SunJSSE DTLS implementation that enables the exchange of cookies for all handshakes, including new and resumed handshakes, by default. You can disable this feature for resumed handshakes by setting the system property `jdk.tls.enableDtlsResumeCookie` to `false`. This property affects only the cookie exchange for resumed handshakes.

**Improved enum switches** . Java 20 has a change related to switch expressions over an enum type. When you enable preview features with `--enable-preview`, if the selector expression produces an unexpected enum constant value, a MatchException will be thrown instead of an IncompatibleClassChangeError.

This situation can occur only if a new enum constant is added to the enum class after the switch has been compiled.

**FXML JavaScript engine disabled by default** . Starting with Java 20, the JavaScript script engine for FXML is disabled by default, and an exception will be thrown when loading `.fxml` files that contain a JavaScript processing instruction (PI).

To enable this feature if your JDK has a JavaScript script engine, set the system property `-Djavafx.allowjs=true`.

**JMX connections use ObjectInputFilter by default**. For Java 20, the default Java Management Extensions (JMX) agent has been updated to restrict the types the server will deserialize using an ObjectInputFilter on the remote method invocation (RMI) connection. This should be fine for the normal usage of MBeans in the JDK. However, if applications have registered their MBeans in the platform's MBeanServer, they may need to extend the filter to include any additional types their MBeans accept as parameters.

The filter pattern is set in the `JDK/conf/management/management.properties` file using the property `com.sun.management.jmxremote.serial.filter.pattern`. The default filter covers any type that OpenMBeans and MXBeans might use.

The serialization filtering and the filter pattern format are described in detail in the Java Core Libraries Developer Guide. If any additional Java types need to be passed, the default filter can be overridden by using the `-Dcom.sun.management.jmxremote.serial.filter.pattern= option`.

Hidden gems: Java 20 deprecation and removals {#h2-7-hidden-gems-java-20-deprecation-and-removals}
--------------------------------------------------------------------------------------------------

**Methods changed to throw an UnsupportedOperationException** . Java 20 removes the ability to suspend or resume a thread using the `Thread.suspend()` and `Thread.resume()` methods. Those methods were prone to deadlock and have been deprecated since JDK 1.2. They have been changed to throw an UnsupportedOperationException.

Similarly, the ability to stop a thread using the `Thread.stop()` method has been removed due to its unsafe nature of causing a `java.lang.ThreadDeath` exception.

**java.net.URL constructors deprecated** . As of Java 20, you should use `java.net.URI` for parsing or constructing URLs. Suppose an instance of `java.net.URL` is still needed to open a connection. In that case, `java.net.URI` can construct or parse the URL string by `calling URI::create()` and then `calling URI::toURL()` to create the URL instance.

For example, here's the old code.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">URL url = new URL("https://blogs.oracle.com/authors/mohamed-taman");</pre>

And here's the new code.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">URL url = URI.create("https://blogs.oracle.com/authors/mohamed-taman").toURL();</pre>

When a custom stream handler is required to construct a URL, use the new `URL::of(URI, URLStreamHandler)` methods.

**JMX management applets deprecated for removal** . The JMX management applet (`m-let`) feature is no longer useful for modern applications and will be removed in a future release. The following public classes are being deprecated and will no longer be supported: MLet, MLetContent, PrivateMLet, and MLetMBean.

This deprecation will not affect the JMX agent used for monitoring the built-in instrumentation of the JVM or any tooling that relies on JMX.

**Thread text removed from Subject.current** . In Java 20, the `Subject.current` specification has been changed. Previously, the Subject was expected to be inherited when a new thread was created. However, this expectation has been dropped, and the Subject is now stored in the AccessControlContext. The AccessControlContext is inherited when platform threads are created but not for virtual threads, because they do not capture the caller context at thread creation time.

**DTLS 1.0 and TLS-ECDH_\* disabled** . In Java 20, the DTLS 1.0 protocol has been disabled by default due to its weak security and lack of support for strong cipher suites. To achieve this, DTLS 1.0 has been added to the `jdk.tls.disabledAlgorithms` security property in the `java.security` configuration file. An attempt to use DTLS 1.0 will result in an SSLHandshakeException.

You can re-enable this protocol at your own risk by removing `DTLSv1.0` from the `jdk.tls.disabledAlgorithms` security property.

Similarly, the `TLS_ECDH_*` cipher suites have been disabled by adding `ECDH` to the `jdk.tls.disabledAlgorithms` security property in the java.security configuration file. These cipher suites lack forward-secrecy and are hardly ever used in practice. While some of them were already disabled due to the use of disabled algorithms such as 3DES and RC4, the rest of them have now been disabled. Any attempts to use cipher suites starting with `TLS_ECDH_` will result in an SSLHandshakeException.

You can re-enable these cipher suites at your own risk by removing `ECDH` from the `jdk.tls.disabledAlgorithms` security property.

**Error thrown if the default java.security file fails to load** . Previously, when the default security configuration file `conf/security/java.security` failed to load, the JDK would fall back to using a hardcoded default configuration. However, in Java 20, if the default configuration file fails to load, the JDK throws an InternalError instead of using the hardcoded default configuration.

This change is intended to make misconfigurations more obvious and avoid potential security issues caused by unexpected behavior.

Conclusion {#h2-8-conclusion}
-----------------------------

There's a lot more to a new Java release than the widely publicized JEPs. Study all these changes; even if you don't use the preview and incubator features, there are sufficient bug fixes and other enhancements to make Java 20 worth testing and using on production systems today.

*** ** * ** ***

Originally posted on [Java Magazine on April 20, 2023](https://blogs.oracle.com/javamagazine/post/java-20-gems-jdks).
