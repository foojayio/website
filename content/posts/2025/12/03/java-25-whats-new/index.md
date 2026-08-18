---
title: "Java 25: What's New?"
slug: "java-25-whats-new"
date: "2025-12-03T09:02:49+00:00"
lastmod: "2025-12-10T10:25:58+00:00"
description: "This article was first published in my personal blog: Java 25: What's new? and is part of a serie on what’s new on the last versions of Java. Now that - by Loic Mathieu"
canonical: "https://www.loicmathieu.fr/wordpress/informatique/java-25-whats-new/"
authors:
  - "loic-mathieu"
image: "j25.png"
categories:
  - "Java"
  - "JEPs"
  - "Release Notes"
tags:
related_posts:
enlighterjs: true
frozen: false
---

*This article was first published in my personal blog: [Java 25: What's new?](https://www.loicmathieu.fr/wordpress/informatique/java-25-whats-new/) and is part of a serie on [what's new on the last versions of Java](//www.loicmathieu.fr/wordpress/tag/whatsnew/).*

Now that Java 25 is out, it's time to walk through all the functionalities that bring to us, developers, this new version.

Java 25 is the new Long-Term Support (LTS) version, containing no less than 18 JEPs. Even if this isn't as good as the 24 JEPs in Java 24, it's still a pretty significant number. No big news on the program, but a number of nice little ones anyway.

JEP 470: PEM Encodings of Cryptographic Objects (Preview)
---------------------------------------------------------

New preview feature that provides support for the [Privacy-Enhanced Mail](https://en.wikipedia.org/wiki/Privacy-Enhanced_Mail "Privacy-Enhanced") (PEM) format to Java. This format is widely used to communicate, for example, keys or certificates, as it is an easy-to-use text format. It is also a [PKCS#8](https://www.rfc-editor.org/rfc/rfc5208 "PKCS#8") standard. This format is now supported for private keys, public keys, certificates and certificate revocation lists. A PEM text is a Base64-encoded representation of a cryptographic object. For example, for an elliptic curve public key :

```
-----BEGIN PUBLIC KEY-----
MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEi/kRGOL7wCPTN4KJ2ppeSt5UYB6u
cPjjuKDtFTXbguOIFDdZ65O/8HTUqS/sVzRF+dg7H3/tkQ/36KdtuADbwQ==
-----END PUBLIC KEY-----
```


To encode a cryptographic object in PEM format, we'll use the new `PEMEncoder` class:

```
PEMencoder pe = PEMEncoder.of();

// plaintext
byte[] pem = pe.encode(publicKey);

// password encrypted
byte[] pemWithPassword = pe.withEncryption(password).encode(privateKey);
```


You can supply a password to encode the PEM text using the `withEncryption()` method; you can also encode it directly as a String using the `encodeToString()` method. To decode a PEM text into a cryptographic object, we\\'ll use the new `PEMDecoder` class:

```
PEMDecoder pd = PEMDecoder.of();

// plaintext
Certificate cert = pd.decode(pem, X509Certificate.class);

// password encrypted
Certificate certWithPassord = pd.withDecryption(password)
    .decode(pem, X509Certificate.class);
```


More information in the [JEP 470](https://openjdk.org/jeps/5470 "JEP").

JEP 502: Stable Values (Preview)
--------------------------------

New preview feature that lets you create *stable* variables that behave like a final variable, but with greater initialization flexibility.

A stable value can only be initialized once and is immutable.

It\\'s quite common to delay the initialization of a class attribute when it\\'s first used, to avoid slowing down application start-up, or even to avoid initializing an unused attribute! To do this consistently and safely in multi-threaded scenarios, you need to use a rather complicated construction based on [double-checked locking](https://en.wikipedia.org/wiki/Double-checked_locking) or [class-holder idiom](https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom).

With Stable Values, we now have an API for that!

```
private final StableValue<Logger> logger = StableValue.of();

Logger getLogger() {
    return logger.orElseSet(() -> Logger.create(MyClass.class));
}

void saySomething() {
    getLogger().info(I have nothing to say);
}
```


There are several ways to create stable values, instead of using `orElseSet()` you can use a supplier, for example:

```
private final Supplier<Logger> logger = StableValue
    .supplier(() -> Logger.create(MyClass.class));

void saySomething() {
    logger().get().info(I have nothing to say);
}
```


The JVM will guarantee that the stable value `logger` will be initialized once and only once, the first time it is used, and will enable the JIT to optimize its use (constant folding). It\\'s also possible to build a list of stable values, with each element initialized only once on its first access. Very useful for creating object pools.

```
static final List<Connection> connections = StableValue
    .list(POOL_SIZE, _ -> new Connection());

void doSomething() {
    long index = Thread.currentThread().threadId() % POOL_SIZE;
    Connection con = connections.get((int) index);
}
```


More information in the [JEP 502](https://openjdk.org/jeps/502 "JEP").

JEP 503: Remove the 32-bit x86 Port
-----------------------------------

The 32-bit port for x86 JVM architectures was deprecated in Java 24 with the intention of removing it in a future release. The cost of maintenance and development of new functionalities induced by this port was not worth it, as this architecture is hardly used anymore.

This has no impact on other 32-bit architectures such as ARM32. For unsupported architectures, there is an architecture-independent JVM port called Zero, which will still allow you to run a JVM on 32-bit x86 architectures.

More information in the [JEP 503](https://openjdk.org/jeps/503 "JEP").

509: JFR CPU-Time Profiling (Experimental)
------------------------------------------

Experimental feature that adds the capture of CPU time profiling information under Linux to Java Flight Recorder (JFR).

JFR is the JVM\\'s production monitoring tool, it has a low overhead and can already monitor a method\\'s memory usage and execution time (wall-clock time). To monitor a method\\'s execution time, JFR will sample a program\\'s execution stack at regular intervals, e.g. every 20ms. A method\\'s execution time is not necessarily its CPU time. For example, a method performing memory calculations such as sorting an array will mainly consume CPU, so its execution time will be almost equivalent to CPU time, whereas a method accessing a network resource will mainly wait for I/O, so its CPU time will be very low.

Linux systems support precise measurement of CPU cycle consumption by sending signals at regular intervals. This is what is used by most Linux profilers and, in the Java world, for example by [async-profiler](https://github.com/async-profiler/async-profiler).

With JEP 509, JFR can now use the mechanism offered by Linux to issue a new monitoring event: `jdk.CPUTimeSample`, this new event is not active by default. The following command line will activate the event when the application is launched:

```
java -XX:StartFlightRecording=jdk.CPUTimeSample#enabled=true,filename=profile.jfr ...
```


To find out more, read these interesting articles by Johannes Bechberger[Java 25's new CPU-Time Profiler (1)](https://mostlynerdless.de/blog/2025/06/11/java-25s-new-cpu-time-profiler-1/) and [Java 25's new CPU-Time Profiler: The Implementation (2)](https://mostlynerdless.de/blog/2025/07/30/java-25s-new-cpu-time-profiler-the-implementation-2/).

More information in the [JEP 509](https://openjdk.org/jeps/509 "JEP").

514: Ahead-of-Time Command-Line Ergonomics
------------------------------------------

[JEP 483: Ahead-of-Time Class Loading \& Linking](https://openjdk.org/jeps/483) introduced in Java 24 the possibility of creating an AOT (Ahead of Time) cache containing an application\\'s already loaded and linked classes, to improve its startup time.

This was to be done in three steps:

1. Launching the application to record the list of classes used
2. Creating the AOT cache
3. Launching the application with the AOT cache

With the JEP 514, this process has been simplified and can now be done in just two steps, with the first two steps being done in a single step via the command line:

```
java -XX:AOTCacheOutput=app.aot -cp app.jar com.example.App ...
```


The list of classes will be saved while the application is running, then the AOT cache will be created when the application is stopped.

More information in the [JEP 514](https://openjdk.org/jeps/514 "JEP").

515: Ahead-of-Time Method Profiling
-----------------------------------

The AOT cache presented in the previous section is enriched with method profiling information!

When a Java application is started up, the Just-In-Time compiler (JIT) will profile the Java methods to determine which ones need to be compiled (those that are frequently used) and guide these optimizations via a number of heuristics (most frequently used branches, loop size, etc.). Only after this profiling information has been collected can the JIT compile and optimize the application\\'s classes. JEP 515 will extend the AOT cache to include method profiling information, enabling the JIT to start with profiling information, and the application to start faster and reach peak performance sooner. The JIT will always collect statistics at runtime. Preliminary tests show a 19% startup performance improvement for a simple program using the Stream API, for an increase in AOT cache size of just 2.5%.

More information in the [JEP 515](https://openjdk.org/jeps/515 "JEP").

518: JFR Cooperative Sampling
-----------------------------

As described in the section about JEP 509; to monitor the execution time of a method, JFR will sample the execution stack of a program at regular intervals, for example every 20ms. It will then collect all the stacks of all started threads. To do this, the threads must be in a stable state, consistent with each other. To collect information from threads, we can use the safepoint mechanism: the JVM asks all threads to suspend, then they go to a point where they can suspend without endangering the application: the safepoint.

This mechanism suffers from safepoint bias: a thread far from a safepoint will take a long time to suspend, while another close to the safepoint will do so quickly. To avoid this bias, JFR scans threads asynchronously and suspends them outside a safepoint. Since thread stack metadata is not guaranteed to be valid outside safepoints, JFR uses heuristics to generate thread stacks.

This mechanism has been redesigned to analyze thread stacks only at safepoints, thus avoiding errors. To counteract safepoint bias, JFR records the program pointer on suspension, then the thread continues and reconstitutes the stack on its next safepoint. This makes data collection more accurate and less risky. This mechanism is used for threads executing a Java method; for threads executing a native method, the thread state is known and does not change for the duration of the method, so JFR can traverse the thread stack without waiting for a safepoint.

Despite this, one problem remains. Sometimes, when rebuilding the stack at the safepoint, JFR can\\'t resolve the stack with the recorded information, so it fallback to the thread information at safepoint, again creating a bias. To find out more, read this interesting article by Johannes Bechberger [Taming the Bias: Unbiased\* Safepoint-Based Stack Walking in JFR](https://mostlynerdless.de/blog/2025/05/20/taming-the-bias-unbiased-safepoint-based-stack-walking-in-jfr/).

More information in the [JEP 518](https://openjdk.org/jeps/518 "JEP").

520: JFR Method Timing \& Tracing
---------------------------------

Adds method tracing and timing via [bytecode instrumentation](https://docs.oracle.com/en/java/javase/24/docs/api/java.instrument/java/lang/instrument/Instrumentation.html) to Java Flight Recorder (JFR).

To trace method invocations (tracing): records all invocation statistics instead of the sampling-based mechanism. To measure method execution times (timing): captures execution times and stack traces of specific methods without requiring source code modification.

To measure execution time and trace method invocations, tools such as JMH [(Java Micronbenchmark Harness](https://github.com/openjdk/jmh)), sampling profilers or Java agents are generally used during development or debugging. These are not designed for production use and often have a considerable overhead. With JEP 520, the JDK gets a configurable, production-ready tool for measuring execution time and trace method invocations!

Two new events, `jdk.MethodTiming` and `jdk.MethodTrace`, have been added to JFR; they can be configured via a filter to specify which methods are to be monitored. For example, the following command will enable tracing of the `java.util.HashMap::resize` method:

```
java -XX:StartFlightRecording:jdk.MethodTrace#filter=java.util.HashMap::resize,filename=recording.jfr ...
```


The following command line will activate the timing of all static initialization blocks:

```
java '-XX:StartFlightRecording:method-timing=::<clinit>,filename=clinit.jfr' ...
```


More information in the [JEP 520](https://openjdk.org/jeps/520 "JEP").

Features coming out of preview
------------------------------

The following features comes out of preview (or incubator module) are now standard features:

* [JEP 506](https://openjdk.org/jeps/506) - **Scoped Values** : allow immutable data to be shared within and between threads. One change: `ScopedValue.orElse` no longer accepts null as an argument.
* [JEP 511](https://openjdk.org/jeps/511) - **Module Import Declarations** :allows you to import all the classes of a module, transitively, via the instruction `import module java.base;`.
* [JEP 512](https://openjdk.org/jeps/512) - **Compact Source Files and Instance Main Methods** : previously named *Simple Source Files and Instance Main Methods* , simplifies the writing of simple programs by allowing them to be defined in an implicit class (without declaration) and in an instance method `void main()`. *Simple* Source File has been renamed *Compact* Source File, `java.io.IO` has been moved to the `java.lang` package and is therefore implicitly imported into every source file, while the static methods of the `IO` class are no longer implicitly imported.
* [JEP 513](https://openjdk.org/jeps/513) - **Flexible Constructor Bodies** : feature that authorizes instructions **before** the parent constructor is called, as long as they do not access the instance currently being created.
* [JEP 510](https://openjdk.org/jeps/510) - **Key Derivation Function API**: New API for Key Derivation Functions (KDFs), which are cryptographic algorithms that derive additional keys from a secret key and other data.
* [JEP 519](https://openjdk.org/jeps/519) - **Compact Object Headers**: JVM mode, which reduces object headers from 128 to 64 bits, is part of the Liliput project.
* [JEP 521](https://openjdk.org/jeps/521) - **Generational Shenandoah**: adds a generational mode to Garbage Collector Shenandoah.

For details on these, please refer to my previous articles.

Features that remain in preview
-------------------------------

The following features remain in preview (or in the incubator module).

* [JEP 508](https://openjdk.org/jeps/508) - **Vector API**: tenth incubation, API for expressing vector calculations that compile at runtime into vector instructions for supported CPU architectures. A few API changes and performance improvements. It was agreed inside the JEP that the Vector API will remain in incubation until the Valhalla project\\'s functionalities are available as a preview. This was expected, as the Vector API will then be able to take advantage of the performance and in-memory representation improvements expected from the Valhalla project.
* [JEP 505](https://openjdk.org/jeps/505) - **Structured Concurrency** : fifth preview, new API that simplifies the writing of multi-threaded code by allowing multiple concurrent tasks to be treated as a single processing unit. Static factory methods must now be used to create a `StructuredTaskScope`.
* [JEP 507](https://openjdk.org/jeps/507) - **Primitive Types in Patterns, instanceof, and switch** : third preview, adds support for primitive types in `instanceof` and `switch`, and enriches pattern matching to support primitive type patterns: in `instanceof`, in `switch` boxes , and in record deconstruction. No changes.

For details on these, please refer to my previous articles.

Miscellaneous
-------------

Various additions to the JDK: - `ForkJoinPool` now implements `ScheduledExecutorService`

* ZIP `Deflater` and `Inflater` now implement `AutoCloseable`, allowing them to be used in a try-with-resources.
* `Currency.availableCurrencies()`: returns a stream of available currencies.
* `TimeZone.availableIDs()`: returns a stream of available time zone identifiers.
* `TimeZone.availableIDs(int offset)`: returns a stream of time zone identifiers available for a given offset.
* `HttpResponse.BodyHandlers.limiting(BodyHandler, limit)`: returns a `BodyHandler` which limits the size of the response body.
* `HttpResponse.BodySubscribers.limiting(BodySubscriber, limit)`: returns a `BodySubscriber` which limits the size of the response body.
* `Math` and `StrictMath` have seen the addition of methods for multiplication and power in exact calculation (throw an exception if the result overflow): `powExact()`, `unsignedPowExact`() , `unsignedMultiplyExact()`.
* `CharBuffer` and `CharSequence.getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin)`: copies the characters in a sequence specified by `srcBegin` and `srcEnd` into a given character array. This method has been successfully implemented in certain child classes and has been used in the `Reader` class to improve performance.
* `Reader.readAllAsString()`: reads all remaining characters as String.
* code\>Reader.readAllLines(): reads all remaining characters as a text line list.

All the new JDK 25 APIs can be found in [The Java Version Almanac -- New APIs in Java 25](https://javaalmanac.io/jdk/25/apidiff/24/ "The").

Internal changes, performance, and security
-------------------------------------------

Like all new versions of Java, OpenJDK 24 contains its share of performance optimizations and security enhancements.

Quite a few changes on the JFR side, some of which have already been covered in this article, but you can find an exhaustive list of these changes in Erik Gahlin\\'s article [What\\'s new for JFR in JDK 25](https://egahlin.github.io/2025/05/31/whats-new-in-jdk-25.html).

For performance, a notable optimization has been made for the hashcode of Strings: it has been annotated with `@Stable`, an internal JDK annotation that specifies a field as never changing after its value has been set to something other than the default. The JIT will then be able to optimize the field (constant folding). Tests have been done with an immutable map having String keys, and key lookup performance has been improved by 8x! More information in Per-Ake Minborg\\'s article: [Strings Just Got Faster](https://inside.java/2025/05/01/strings-just-got-faster/).

In terms of security, in addition to the JEPs described above, the new SHAKE128-256 and SHAKE256-512 Message Digest algorithms have been added, as well as support for the HKDF-SHA256, HKDF-SHA384, and HKDF-SHA512 Key Derivation Function algorithms from the PKCS#11 standard. Please refer to Sean Mullan\\'s article for a comprehensive list of security changes included in this release. : [JDK 25 Security Enhancements](https://seanjmullan.org/blog/2025/09/23/jdk25).

JFR Events
----------

Here are the new Java Flight Recorder (JFR) events of the JVM:

* `MethodTiming`: please refer to the section on JEP 520.
* `MethodTrace`: please refer to the section on JEP 520.
* `CPUTimeSample`: please refer to the section on JEP 509.
* `CPUTimeSamplesLost`: please refer to the section on JEP 509.
* `SafepointLatency`: delay for a thread to reach a safepoint after a request.
* `JavaMonitorNotify`: notify on a Java monitor.
* `JavaMonitorDeflate`: no description.
* `JavaMonitorStatistics`: no description.

You can find all the JFR events supported in this version of Java on the page [JFR Events](https://sap.github.io/SapMachine/jfrevents/25.html "JFR").

Conclusion
----------

All in all, it\\'s a pretty nice Java release, and the additions to JFR are substantial, making it a Swiss Army Knife for Java application monitoring. I particularly like the new StableValue API, which brings simplicity and performance, as well as support for PEM text, which was sorely lacking in the JDK. As always, we\\'re looking forward to the Valhalla project...

For all the changes in Java 25, please refer to the [release notes](https://jdk.java.net/25/release-notes).
