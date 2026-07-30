---
title: "Java 24 : What's New?"
slug: "java-24-whats-new"
date: "2025-02-26T09:58:53+00:00"
lastmod: "2026-07-03T12:15:12+00:00"
description: "What's new in Java 24 for us, developers"
canonical: "https://www.loicmathieu.fr/wordpress/informatique/java-24-quoi-de-neuf/"
authors:
  - "loic-mathieu"
image: "https://foojay.io/wp-content/uploads/2025/02/fb9dcc56-387f-4952-b62f-240beaeb24cf.webp"
categories:
  - "Java"
  - "JEPs"
  - "Release Notes"
  - "Uncategorized"
tags:
related_posts:
frozen: false
---

**Java 24 will be available soon, on March 18th. What are all the functionalities that this version brings to us as developers?**

Java 24 contains not less than 24 JEPs, a record and above all an eponymous figure!

JEP 404: Generational Shenandoah (Experimental) {#h2-0-jep-404-generational-shenandoah-experimental}
----------------------------------------------------------------------------------------------------

Shenandoah is a Garbage Collector (GC) originally developed by RedHat and included in OpenJDK. It reduces GC pause times by performing evacuation work concurrently to application threads.

Until now, Shenandoah was not generational, meaning that it did not separate the heap into several zones containing objects of different ages. Generational GCs are based on the **Weak Generational Hypotesis** : *most objects die young*. As collecting a dead object is very cheap, separating young objects from old ones means you can focus on the young objects to clean the heap more efficiently.

All existing GCs are generational, even ZGC, which wasn\\'t at the begining, but since this release only supports generational mode. Shenandoah is catching up by adding an experimental generational mode.

Shenandoah is a concurrent GC, and this comes at a cost in terms of CPU and memory usage. A generational mode should help mitigate the cost of GC operation, while maintaining a millisecond pause target.

To enable generational mode, use the following JVM options: `-XX:+UnlockExperimentalVMOptions -XX:ShenandoahGCMode=generational`.

More information in the [JEP 404](https://openjdk.org/jeps/404 "JEP").

JEP 450: Compact Object Headers (Experimental) {#h2-1-jep-450-compact-object-headers-experimental}
--------------------------------------------------------------------------------------------------

This JEP comes from the Liliput project, whose aim is to reduce the size of object headers from 128 bits to 64 bits or less. In fact, this is exactly what JEP 450 delivers: an experimental mode for the JVM that reduces object headers to 64 bits.

Every object in Java has a header, then some *data* : its attributes. If you have a `Point(int x, int y)` object, although an int uses 32 bits, the size of the Point object will be 192 bits, because each object stores in its reference a 128-bit set of information.

The first 64 bits of a header are called *Mark Word* and store either the hash code, GC age and certain flags required by the JVM; or a pointer to an external structure. The last 64 bits are called *Class Word* and store a pointer to the class. The special *Compressed Class Pointer* mode already makes it possible to reduce this to 32 bits, but on a 64-bit architectures, memory alignment means that the gain is not necessarily present, even if the JVM can optimize the layout of an object using these 32 bits thus freed memory up.

By enabling compact object headers via the JVM options `-XX:+UnlockExperimentalVMOptions -XX:+UseCompactObjectHeaders`, the JVM will store the object header on 64 bits: 22 bits for the class pointer, 31 bits for the hash code, 4 bits reserved for Valhalla and the remaining bits as before for the GC age and JVM flags.

Preliminary tests show a 10-20% reduction in memory footprint.

Amazon\\'s tests show that many workloads benefit from a reduction in CPU usage of up to 30%.

There are a few limitations to this mode: it cannot support heaps larger than 8TB (except for ZGC) and is limited to 4 million classes.

More information in the [JEP 450](https://openjdk.org/jeps/450 "JEP").

JEP 472: Prepare to Restrict the Use of JNI {#h2-2-jep-472-prepare-to-restrict-the-use-of-jni}
----------------------------------------------------------------------------------------------

JEP 472 restricts the use of the Java Native Interface (JNI) and adjusts the Foreign Function and Memory (FFM) API accordingly.

Using a native function will generate a WARNING at JVM startup unless the JVM `--enable-native-access` option is used to authorize it. It is also possible to use the manifest entry `Enable-Native-Acces`.

Using a native function without first authorizing it is illegal, and the JVM `--illegal-native-access` option controls the JVM\\'s behavior in such cases. It defaults to `warn`, and can take the following values:

* `allow`: use is authorized without restriction,
* `warn`: the default in Java 24, a warning will be emitted in the JVM logs,
* `deny`: use is refused, an `IllegalCallerException` will be thrown.

Example of a warning emitted by the JVM when loading a native library if access has not been authorized:

    WARNING: A restricted method in java.lang.System has been called
    WARNING: System::load has been called by com.foo.Server in module com.foo (file:/path/to/com.foo.jar)
    WARNING: Use --enable-native-access=com.foo to avoid a warning for callers in this module
    WARNING: Restricted methods will be blocked in a future release unless native access is enabled

The JVM\\'s default behavior when using a native library will be changed from `warn` to `deny` in a future release.

This JEP is part of a set of JVM changes to restrict certain JVM features by default, forcing the user to specifically enabling these features, with the aim of having a more robust JVM. You can read more about this in the JEP draft [Integrity by Default](https://openjdk.org/jeps/8305968 "Integrity").

More information in the [JEP 472](https://openjdk.org/jeps/472 "JEP").

JEP 475: Late Barrier Expansion for G1 {#h2-3-jep-475-late-barrier-expansion-for-g1}
------------------------------------------------------------------------------------

JEP 475 simplifies the implementation of G1 Garbage Collector (GC) barriers by moving their expansion from the beginning of the JVM\\'s Just In Time (JIT) compiler pipeline (C2) to the end.

G1 uses barriers to record information on accesses to the application\\'s memory. These barriers require interaction with the JIT, making its code and maintenance more complex.

Preliminary tests have shown that setting up G1 barriers has an [overhead of 10 to 20% on the JIT](https://robcasloz.github.io/blog/assets/c2-speed-results.html). The idea is that instead of injecting the barrier at the beginning of the compilation phase (in the IR - the intermediate representation of the code to be compiled) and letting C2 compile the barrier code, it is more interesting to inject it at the end of the compilation phase directly in assembly.

More information in the [JEP 475](https://openjdk.org/jeps/475 "JEP") and in the article [When should a compiler expand garbage collection barriers?](https://robcasloz.github.io/blog/2024/02/14/when-should-a-compiler-expand-garbage-collection-barriers.html) de Roberto Castañeda Lozano.

JEP 478: Key Derivation Function API (Preview) {#h2-4-jep-478-key-derivation-function-api-preview}
--------------------------------------------------------------------------------------------------

New API for Key Derivation Functions (KDFs), which are cryptographic algorithms that derive additional keys from a secret key and other data.

KDF is part of the cryptographic standard [PKCS #11](https://docs.oasis-open.org/pkcs11/pkcs11-spec/v3.1/os/pkcs11-spec-v3.1-os.html "PKCS").

KDF is also one of the building blocks required to implement Hybrid Public Key Encryption (HPKE), a post-quantum cryptographic algorithm. Post-quantum cryptography is a set of cryptographic algorithms that are resistant to quantum computers. I\\'ll come back to this subject in the section on **Module-Lattice-Based** algorithms in JEP 496 and 497.

More information in the [JEP 478](https://openjdk.org/jeps/478 "JEP").

JEP 479: Remove the Windows 32-bit x86 Port {#h2-5-jep-479-remove-the-windows-32-bit-x86-port}
----------------------------------------------------------------------------------------------

The Windows 32-bit JVM port for x86 architectures has been deprecated in Java 21, and will be removed in Java 24.

More information in the [JEP 479](https://openjdk.org/jeps/479 "JEP").

JEP 483: Ahead-of-Time Class Loading \& Linking {#h2-6-jep-483-ahead-of-time-class-loading-linking}
---------------------------------------------------------------------------------------------------

The purpose of this new feature is to improve JVM startup time by making an application\\'s classes instantly available, in a loaded and linked state, when the JVM starts.

This requires the application to be launched for the first time. During this launch, the loaded classes are saved in a cache so that they are immediately available for subsequent launches.

In the JVM, classes are loaded on demand via a ClassLoader. As the JVM is dynamic, the list of classes to be loaded is not fixed at startup, as classes can be created dynamically (e.g. via dynamic proxies), or loaded dynamically via reflection.

But loading a class comes at a cost:

* The JVM must scan all the JARs in the classpath to locate the class and load the .class file into memory.
* Then it must parse the class and create a Class object to represent it, link the class, check the bytecode, resolve the references...
* And finally, it must execute the static initializers and static blocks.

Even if these steps are optimized, an application that uses hundreds of JARs and initializes thousands of classes can take a long time to start due to these phases, which is even more problematic for microservice frameworks like Spring that tend to scan thousands of beans at startup. Even when these steps are done lazily, on demand, they have a major impact on the first few minutes of an application\\'s operation, leading to late peak performance.

With JEP 483: Ahead-of-Time Class Loading \& Linking, it will be possible to create a cache of initialized and linked classes, then use it in a successive execution of the application to optimize startup.

Unlike GraalVM, which offers the same type of functionality, but in a *closed world*, all JVM functionalities are preserved. A class not present in the archive will be dynamically loaded by the JVM.

There are three steps to using AOT mode: register the classes to be included in the cache, create the cache, then use the cache. Example with a *Hello World* application in a single `Main` class:

1. `java -XX:AOTMode=record -XX:AOTConfiguration=app.aotconf Main`: launch the application in record mode to save the classes to be loaded in an **app.aotconf** configuration file.
2. `java -XX:AOTMode=create -XX:AOTConfiguration=app.aotconf -XX:AOTCache=main.aot`: use the **app.aotconf** configuration file to create a **main.aot** cache archive.
3. `java -XX:AOTCache=main.aot Main`: launch the application with the **main.aot** cache .

An application such as Spring PetClinic loads and links around 21,000 classes at startup and boots up in 4.486 seconds (JDK 23). With an AOT cache, it starts up in 2.604 seconds - an improvement of 42%. The AOT cache occupies 130 megabytes.

More information in the [JEP 483](https://openjdk.org/jeps/483 "JEP").

JEP 486: Permanently Disable the Security Manager {#h2-7-jep-486-permanently-disable-the-security-manager}
----------------------------------------------------------------------------------------------------------

The Security Manager was deprecated in Java 17, and is now completely removed from the JDK!

The Security Manager was a complicated component to maintain, with a significant footprint in code, and certainly an impact in terms of performance (at least at application startup until the JIT optimized its calls); its removal resulted in the deletion of over 14,000 lines of code!

There has been a lot of discussion about its removal. The Security Manager provided a form of security via checks carried out when certain JDK methods were called (thread creation, file access, etc.), and had originally been created to secure the calling of untrusted code (e.g. dynamically executed external code) for applets, among others (may they rest in peace). However, modern security needs are more complex and must be met via a set of tools, of which peripheral security and sandboxing are the main allies. Executing untrusted code is strongly discouraged, and if necessary**other techniques must be implemented**.

What\\'s a pity about the Security Manager\\'s removal is that, even if imperfect, it still provided many services, at least for platforms that had a plugin system such as Kafka Connect, Elasticsearch or [Kestra](https://kestra.io/) - the data orchestrator I work for. A plugin system will by definition run untrusted code, and the **other techniques that need to be implemented** are not provided as a replacement for Security Manager. The JEP recommends running untrusted code in a sandbox (Docker, GraalVM) or implementing access controls via a Java agent!

In my opinion, it\\'s rather problematic to remove the Security Manager without any solution, as it was used to secure untrusted code, which is necessary for a plugin system. Some of its functionality could have been preserved or replaced by a simpler system. Of course, I\\'m biased here because I\\'m going to have to re-implement [Kestra\\'s security rules](https://kestra.io/docs/enterprise/worker-isolation#java-security) differently, as we were using a Security Manager.

Stuart Marks has written an interesting article about the Security Manager removal: [Detoxifying the JDK Source Code](https://stuartmarks.wordpress.com/2024/12/12/detoxifying-the-jdk-source-code).

More information in the [JEP 486](https://openjdk.org/jeps/486 "JEP").

JEP 490: ZGC: Remove the Non-Generational Mode {#h2-8-jep-490-zgc-remove-the-non-generational-mode}
---------------------------------------------------------------------------------------------------

ZGC is a Garbage Collector (GC) developed by Oracle and included in OpenJDK. It reduces GC pause times by performing evacuation work concurrently of application threads.

ZGC was non-generational when it was created, but has had a generational mode since Java 21. This JEP will remove the non-generational mode to reduce the cost of maintaining ZGC.

More information in the [JEP 490](https://openjdk.org/jeps/490 "JEP").

JEP 491: Synchronize Virtual Threads without Pinning {#h2-9-jep-491-synchronize-virtual-threads-without-pinning}
----------------------------------------------------------------------------------------------------------------

Virtual threads are lightweight threads with low creation and scheduling costs, making it easier to write concurrent applications. When a virtual thread executes, it is mounted on a platform thread (thread OS).

Until now, Java synchronization (via the `synchronized` keyword) did not unmount the platform thread, so the platform thread was **pinned** to the virtual thread, which severely limited the scalability of virtual threads if many synchronization operations were performed. And synchronization is used in many places within the JDK (for I/O, for example), as well as in numerous libraries (JDBC clients, for example).

With JEP 491, virtual threads will now unmount their platform thread in the event of synchronization. This is an important milestone for virtual threads, as it was the main performance problem encountered when using them.

More information in the [JEP 491](https://openjdk.org/jeps/491 "JEP").

JEP 493: Linking Run-Time Images without JMODs {#h2-10-jep-493-linking-run-time-images-without-jmods}
-----------------------------------------------------------------------------------------------------

JEP 493 reduces the size of the JDK by around 25% by enabling the jlink tool to create custom runtime images without using the JDK\\'s JMOD files. This feature must be enabled when the JDK is built; not all JDK vendors might enable it, but it\\'s likely that they will and that JDK 24 will be 25% smaller than previous versions!

JMOD files are the JDK module files used by jlink to create an image of the JDK; this image itself contains the JDK module files, which are therefore duplicated, leaving the JMOD files useless.

More information in the [JEP 493](https://openjdk.org/jeps/493 "JEP").

JEP 496: Quantum-Resistant Module-Lattice-Based Key Encapsulation Mechanism {#h2-11-jep-496-quantum-resistant-module-lattice-based-key-encapsulation-mechanism}
---------------------------------------------------------------------------------------------------------------------------------------------------------------

JEP 496 provides an implementation of the key encapsulation mechanism based on a quantum-resistant algorithm Module-Lattice (ML-KEM). Key Encapsulation Mechanisms (KEMs) are used to secure symmetric keys over unsecured communication channels using public key cryptography. ML-KEM has been standardized by the U.S. National Institute of Standards and Technology (NIST) as part of the [FIPS 203](https://csrc.nist.gov/pubs/fips/203/final).

Module-Lattice algorithms are a class of so-called post-quantum algorithms, designed to be secure against future quantum computing attacks.

Current cryptographic algorithms involve discrete mathematical problems (such as factoring large numbers) which are computationally expensive. Breaking such algorithms for large keys using brute force would take thousands of years, even for a supercomputer.

Quantum computers are theoretically capable of factoring large numbers much faster thanks to Shor\\'s algorithm, so to secure communications in a world where a quantum computer would exist with a sufficient number of Qubits to factor a large number (it would need a number of Qubits twice the size of the key), a new class of algorithms based on a different mathematical proof is needed, and this is where module-lattice algorithms come into play.

NIST advises that these algorithms should be in place within the next 10 years, but for the moment, quantum computers are far from having the necessary number of Qubits. To date, they generally have no more than 64 Qubits, whereas 1024 would be needed to break 512-bit encryption and 4096 for 2048-bit encryption. Not to mention the accuracy problems...

More on post-quantum cryptography in this article by Ben Evans : [Post-Quantum Cryptography in Java](https://www.infoq.com/news/2024/12/java-post-quantum).

More information in the [JEP 496](https://openjdk.org/jeps/496 "JEP").

JEP 497: Quantum-Resistant Module-Lattice-Based Digital Signature Algorithm {#h2-12-jep-497-quantum-resistant-module-lattice-based-digital-signature-algorithm}
---------------------------------------------------------------------------------------------------------------------------------------------------------------

JEP 497 provides an implementation of a digital signature algorithm based on Module-Lattice, which is resistant to quantum algorithms (ML-DSA). Digital signatures are used to detect unauthorized data modifications and to authenticate the identity of signatories. It has been standardized by the U.S. National Institute of Standards and Technology (NIST) in the FIPS 204 standard.

More information [JEP 497](https://openjdk.org/jeps/497 "JEP").

JEP 498: Warn upon Use of Memory-Access Methods in sun.misc.Unsafe {#h2-13-jep-498-warn-upon-use-of-memory-access-methods-in-sun-misc-unsafe}
---------------------------------------------------------------------------------------------------------------------------------------------

**Unsafe** is, as its name suggests, an internal and unsupported JDK class that is not safe to use. For historical reasons, many low-level frameworks used Unsafe for faster memory access. Thanks to VarHandle API features ([JEP 193](https://openjdk.org/jeps/193), since Java 9) and Foreign Function \& Memory API ([JEP 454](https://openjdk.org/jeps/454), since Java 22), there are now replacements for Unsafe\\'s memory-access methods that are as powerful, but safer and more supported.

All these methods have been deprecated for deletion in Java 23, and are now modified to generate a **WARNING** in the JVM logs the first time they are used.

This change was planned and announced when they were deprecated in Java 23, and it is planned to deprecate them to raise an exception in Java 26.

More information in the [JEP 498](https://openjdk.org/jeps/498 "JEP").

JEP 501: Deprecate the 32-bit x86 Port for Removal {#h2-14-jep-501-deprecate-the-32-bit-x86-port-for-removal}
-------------------------------------------------------------------------------------------------------------

The 32-bit port for Windows is removed in this release, but a 32-bit port for other OS (Linux) remains. This JEP will deprecate all remaining 32-bit ports for the x86 architecture, including the one for Linux, which is the only one still supported. It does not affect other 32-bit ports, such as the one for ARM32, which is not deprecated. The industry has abandoned since a long time the 32-bit x86 architecture, and with Debian also planning to stop supporting it, it makes sense to plan to stop supporting it in the JVM also.

For unsupported architectures, there is an architecture-independent JVM port: Zero, which will still allow you to run a JVM on 32-bit x86 architecture.

More information in the [JEP 501](https://openjdk.org/jeps/501 "JEP").

Features coming out of preview {#h2-15-features-coming-out-of-preview}
----------------------------------------------------------------------

The following features comes out of preview (or incubator module) are now standard features:

* [JEP 484](https://openjdk.org/jeps/484) - **Class-File API**: standard API for parsing, generating and transforming Java class files.
* [JEP 485](https://openjdk.org/jeps/485) - **Stream Gatherers**: enhance the Stream API with support for customized intermediate operations.

For details on these, please refer to my previous articles.

Features that remain in preview {#h2-16-features-that-remain-in-preview}
------------------------------------------------------------------------

The following features remain in preview (or in the incubator module).

* [JEP 487](https://openjdk.org/jeps/487) **Vector API** : ninth incubation, API for expressing vector calculations that compile at runtime into vector instructions for supported CPU architectures. A few API changes and performance enhancements. A new `Float16` class has been added, supporting 16-bit variables in IEEE 754 binary16 format using the Vector API. The JEP agreed that the Vector API will remain in incubation until project Valhalla's functionalities are available as a preview. This was expected, as the Vector API will then be able to take advantage of the performance and in-memory representation improvements expected from the project Valhalla.
* [JEP 495](https://openjdk.org/jeps/495) - **Simple Source Files and Instance Main Methods** : fourth preview, previously called *Implicitly Declared Classes and Instance Main Methods* , simplifies the writing of simple programs by allowing them to be defined in an implicit class (without declaration) and in an instance method `void main()`. No change.
* [JEP 499](https://openjdk.org/jeps/499) - **Structured Concurrency**: fourth preview, new API to simplify multi-threaded code writing by allowing multiple concurrent tasks to be treated as a single processing unit. No change.
* [JEP 487](https://openjdk.org/jeps/487) - **Scoped Values** : fourth preview, allow immutable data to be shared within and between threads. The `ScopedValue.callWhere()` and `ScopedValue.runWhere` methods have been removed, and a `ScopedValue` can now only be used from `call(Callable)` or `run(Runnable)`.
* [JEP 492](https://openjdk.org/jeps/492) - **Flexible Constructor Bodies** : third preview, a feature that allows instructions to be called **before** the parent constructor as long as they do not access the instance currently being created. No change.
* [JEP 494](https://openjdk.org/jeps/494) - **Module Import Declarations** : second preview, allows you to import all the classes of a module, transitively, via the instruction `import module java.base;`. Minor changes.
* [JEP 488](https://openjdk.org/jeps/488) - **Primitive Types in Patterns, instanceof, and switch** : second preview, adds support for primitive types in `instanceof` and `switch`, and enriches pattern matching to support primitive type patterns: in `instanceof`, in `switch` boxes , and in record deconstruction. No change.

For details on these, please refer to my previous articles.

Miscellaneous {#h2-17-miscellaneous}
------------------------------------

Various additions to the JDK:

* `Console.println()`: writes an empty line to a console.
* `Console.readLn()`: reads a line of text from a console.
* `IO.println()`: writes an empty line to the system console.
* `IO.readLn()`: reads a line of text from the system console.
* `Reader.of(CharSequence)`: returns a reader that reads the characters of a `CharSequence`. The reader is initially open and reading begins at the first character of the sequence.
* `Process.waitFor(Duration)`: makes the current thread wait, if necessary, until the process represented by this `Process` object ends, or the specified wait time elapses.

All the new JDK 24 APIs can be found in [The Java Version Almanac -- New APIs in Java 24](https://javaalmanac.io/jdk/24/apidiff/23/ "The").

Internal changes, performance, and security {#h2-18-internal-changes-performance-and-security}
----------------------------------------------------------------------------------------------

Like all new versions of Java, OpenJDK 24 contains its share of performance optimizations and security enhancements.

* The TLS_RSA cipher suite has been disabled.
* SHA3 performance improved by 10-15%. ([JDK-8333867](https://bugs.openjdk.org/browse/JDK-8333867)).
* String concatenation has been optimized using hidden classes generated via the Classfile API and using the MethodHandle API ([JDK-8336856](https://bugs.openjdk.org/browse/JDK-8336856)) bringing nearly 40% performance optimization. See this excellent talk by Claes Redestad during Devoxx 2024 on the subject: <https://www.youtube.com/watch?v=tgX38gvMpjs>.
* **secondary_super_cache** is a cache used for `instanceof`, it caches one supertype of a class. As it contains only a single entry, if several threads do `instanceof` on different supertypes for the same class, the cache will be invalidated non-stop, which can have significant consequences in terms of performance. This cache has been rewritten by Andrew Haley from RedHat, who has replaced it with a hash table cache. (see [#18309](https://github.com/openjdk/jdk/pull/18309). This problem was named *type pollution* ; RedHat had developed a Java agent to detect problem cases (see [type-pollution-agent](https://github.com/RedHatPerf/type-pollution-agent)) and some frameworks had even implemented workarounds to avoid it in critical paths (for example for Quarkus : [#28834](https://github.com/quarkusio/quarkus/pull/28834), [#28985](https://github.com/quarkusio/quarkus/pull/28985) or [#29109](https://github.com/quarkusio/quarkus/pull/29109)).

JFR Events {#h2-19-jfr-events}
------------------------------

Here are the new Java Flight Recorder (JFR) events of the JVM:

* `NativeMemoryUsageTotalPeak`: Total native memory usage peaks for the JVM (GraalVM Native Image only). It may not be the exact sum of `NativeMemoryUsagePeak` events due to timing.
* `NativeMemoryUsagePeak`: Maximum native memory usage for a given memory type in the JVM (GraalVM Native Image only).
* `SerializationMisdeclaration`: Misdeclared methods and fields for serialization.
* `SwapSpace`: OS swap memory.

You can find all the JFR events supported in this version of Java on the page [JFR Events](https://sap.github.io/SapMachine/jfrevents/24.html "JFR").

Conclusion {#h2-20-conclusion}
------------------------------

Java 24 contains many new features, and we were delighted to see Stream Gatherer come out of preview, but there are still many features that seem to be stuck in preview without any change.

Personally, I\\'m a little disappointed by the removal without replacement of the Security Manager, even if it will give me the opportunity to play with Java agents very soon to replace it ;).

For all the changes in Java 24, please refer to the [release notes](https://jdk.java.net/24/release-notes).

This article was first published in my personal blog: [Java 24 : what's new?](https://www.loicmathieu.fr/wordpress/informatique/java-24-quoi-de-neuf/ "Java 24 : what’s new?").
