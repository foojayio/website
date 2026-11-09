---
title: "Java 27: What’s New?"
date: "2026-11-12"
description: "What's new in Java 27 for us, developers"
canonical: "https://www.loicmathieu.fr/wordpress/informatique/java-27-whats-new/"
authors:
  - "loic-mathieu"
image: "java-27.jpg"
categories:
  - "Java"
  - "JEPs"
  - "Release Notes"
related_posts:
---

This article was first published in my personal blog: [Java 27: What's new?](https://www.loicmathieu.fr/wordpress/informatique/java-27-whats-new/) and is part of a serie on [what's new on the last versions of Java](https://www.loicmathieu.fr/wordpress/tag/whatsnew/)).

Now that Java 27 is available, it's time to walk through all the functionalities that bring to us, developers, this new version.

After Java 26 and its 10 JEPs, Java 27 arrives with a small number of JEPs, 9, with very few new features.

## JEP 523: Make G1 the Default Garbage Collector in All Environments

**G1** was set as the default Garbage Collector in Java 9. At that time, tests showed that for environments with one CPU (or fewer) and less than 1792 MB of heap, the **Serial** Garbage Collector was more efficient. The JVM therefore used **Serial** by default in these cases.

Since then, **G1** has been improved, and tests have shown that **G1** is now equivalent to **Serial** for all heap sizes. With improvements to reduce synchronization ([JEP 522](https://openjdk.org/jeps/522)), **G1** now performs as well as **Serial**, even with a reduced number of CPUs. Finally, **G1** has also seen a reduction in its native memory consumption, reaching levels comparable to those of **Serial**.

Logically, since **G1** is equal to or better than **Serial** on all criteria, even in environments with few CPUs and/or little RAM, it is now the default in all environments.

More information in [JEP 523](https://openjdk.org/jeps/523).

## JEP 527: Post-Quantum Hybrid Key Exchange for TLS 1.3

Strengthens the security of Java applications requiring secure network communications by implementing [hybrid key exchange algorithms](https://datatracker.ietf.org/doc/draft-ietf-tls-hybrid-design) for TLS 1.3. These algorithms help protect against future quantum computing attacks by combining a quantum-resistant algorithm with a traditional one.

A hybrid key exchange algorithm combines a quantum-resistant algorithm with a traditional one and remains secure as long as one of the two algorithms is not broken. This approach helps protect against quantum attacks while acknowledging that these new algorithms have not yet benefited from the years of testing and analysis already conducted on traditional algorithms.

The JDK's implementation of TLS 1.3 will support three new post-quantum hybrid key exchange schemes that combine the *Module-Lattice-Based Key-Encapsulation Mechanism* (ML-KEM - added via [JEP 496](https://openjdk.org/jeps/496)) with traditional *Ephemeral Elliptic Curve Diffie-Hellman* (ECDHE) algorithms:

- **X25519MLKEM768**: hybrid scheme combining ECDHE with X25519 and ML-KEM-768,
- **SecP256r1MLKEM768**: hybrid scheme combining ECDHE using the secp256r1 curve with ML-KEM-768,
- **SecP384r1MLKEM1024**: hybrid scheme combining ECDHE using the secp384r1 curve with ML-KEM-1024.

More information in [JEP 527](https://openjdk.org/jeps/527).

## JEP 534: Compact Object Headers by Default

**Compact object headers** allows the JVM to store object headers in 64 bits instead of 128: 22 bits for the class pointer, 31 bits for the hash code, 4 bits reserved for Valhalla, and the remaining bits as before for GC age and JVM flags.

**Compact object headers** were introduced as an alternative object header structure in preview in Java 24 via [JEP 450](https://openjdk.org/jeps/450), and then exited preview in Java 25.

Since then, **compact object headers** have proven their stability and performance.

They have been tested at Oracle using the JDK's full test suite. They have also been tested at Amazon and SAP, which have enabled them by default.

Various experiments show that enabling **compact object headers** improves performance:
- The SPECjbb2015 benchmark uses 22% less heap space and 8% less CPU time.
- The number of garbage collections performed by SPECjbb2015 is reduced by 15%, both with the G1 collector and the parallel collector.
- A highly parallel JSON parser benchmark runs 10% faster.

In Java 27, **compact object headers** are enabled by default. The old layout is expected to be deprecated in a future release.

More information in [JEP 534](https://openjdk.org/jeps/534).

## JEP 536: JFR In-Process Data Redaction

JFR recording files contain command-line arguments as well as environment variable values and system properties, which show how the process was launched and configured. These events can therefore contain sensitive data, such as secrets in command-line arguments, environment variables, and system properties. Currently, this data appears as-is in the recording files, which poses a risk of sensitive information leakage when recordings are shared.

JFR will now mask many types of sensitive information by default, without any additional configuration.

You can explicitly select the information that JFR should mask using new sub-options of the existing command-line option `-XX:FlightRecorderOptions`. Each sub-option specifies one or more filters that select the command-line arguments, environment variables, and system properties to mask.

The `redact-argument` sub-option specifies a list of filters for command-line arguments, and the `redact-key` sub-option specifies a list of filters for key-value pairs in the form of environment variables and system properties.

Filters use *glob patterns*, where `*` and `?` are wildcards. If a filter matches an argument or a key, the argument or key value is recorded in the event as `[REDACTED]`. Multiple filters are separated by semicolons and are evaluated in the order specified. The different sub-options of `-XX:FlightRecorderOptions` are separated by commas.

For example, to mask any environment variable or system property named "confidential" or "CONFIDENTIAL", as well as any command-line argument resembling a URL containing a username and password (i.e., `username:password@host`), use:

```bash
java -XX:FlightRecorderOptions:'redact-key=confidential,redact-argument=https://*:*@*' \
       -XX:StartFlightRecording:filename=dump.jfr \
       -Dconfidential=ANOTHER_SECRET \
       -jar application.jar https://john:YET_ANOTHER_SECRET@example.com/login --verbose
```

To verify that sensitive information has been properly masked, use the `jfr print` command:

```bash
$ jfr print \
      --events InitialSystemProperty,InitialEnvironmentVariable \
      dump.jfr
[...]
jdk.InitialEnvironmentVariable {
  startTime = 17:39:02.244 (2026-02-15)
  key = "CONFIDENTIAL"
  value = "[REDACTED]"
}
```

## Features coming out of preview
The following features comes out of preview (or incubator module) are now standard features:

**None!**

This is rare enough to highlight. Some features that have been in preview for a long time are blocked waiting for the Valhalla project.

## Features that remain in preview

The following features remain in preview (or in the incubator module):

- [JEP 531](https://openjdk.org/jeps/531) - **Lazy Constants**: third preview, a new API for creating constants initialized on demand. Two changes: removal of low-level methods `isInitialized` and `orElse`, addition of a factory method `Set.ofLazy(...)`.
- [JEP 532](https://openjdk.org/jeps/532) - **Primitive Types in Patterns, instanceof, and switch**: fifth preview, adds support for primitive types in `instanceof` and `switch`, and enhances pattern matching to support primitive type patterns: in `instanceof`, in `switch` cases, and in record deconstruction. No changes.
- [JEP 533](https://openjdk.org/jeps/533) - **Structured Concurrency**: seventh preview, a new API to simplify writing multithreaded code by allowing multiple concurrent tasks to be treated as a single unit of work. Several changes to the `Joiner` interface: the `join()` method accepts a third argument for the exception that a `StructuredTaskScope` can throw; `awaitAll()` has been removed in favor of `allSuccessfulOrThrow()`, `anySuccessfulOrThrow()`, and `awaitAllSuccessfulOrThrow()`, which create joiners that throw an `ExecutionException` in case of an exception; `onTimeout()` has been replaced by `timeout()`.
- [JEP 537](https://openjdk.org/jeps/537) - **Vector API**: twelfth incubator, an API to express vector computations that compile at runtime into vector instructions for supported CPU architectures. No changes. It has been stated in the JEP that the Vector API will remain in incubation until Valhalla project features are available in preview. This was expected, as the Vector API will then be able to take advantage of the performance and memory representation improvements that the Valhalla project should bring.
- [JEP 538](https://openjdk.org/jeps/538) - **PEM Encodings of Cryptographic Objects**: third preview, a new API that adds support for the *Privacy-Enhanced Mail* (PEM) format to Java. Added constructors that take BASE64-encoded byte arrays as input to the `PEM` class; `DEREncodable` has been renamed to `BinaryEncodable`; added the `EncryptedPrivateKeyInfo.getKeyPair()` method and added a `CryptoException` exception used to signal cryptographic exceptions.

For details on these, you can refer to my previous articles.

## Miscellaneous

Various additions to the JDK:

- `Math.acosh(double)` and `StrictMath.acosh(double)`: returns the inverse hyperbolic cosine of a `double` value.
- `Math.asinh(double)` and `StrictMath.asinh(double)`: returns the inverse hyperbolic sine of a `double` value.
- `Math.atanh(double)` and `StrictMath.atanh(double)`: returns the inverse hyperbolic tangent of a `double` value.
- `String.encodedLength(Charset)`: returns the length, in bytes, of this string encoded with the specified charset.
- `BigDecimal.rootn(int, MathContext)`: returns an approximation of the nth root of this value, rounded according to the context settings.
- `DateTimeFormatter` now supports short timezone offsets (`+01` instead of `+01:00`), see [JDK-8210336](https://bugs.openjdk.org/browse/JDK-8210336).

All new APIs in JDK 27 can be found in [The Java Version Almanac - New APIs in Java 27](https://javaalmanac.io/jdk/27/apidiff/26/).

## Internal changes, performance, and security

Like all new versions of Java, OpenJDK 27 contains a number of performance optimizations and security enhancements.

On the performance side, I noted two interesting improvements:
- `HashMap.putAll()` now has a fast-path when the `Map` is a `HashMap`, which directly calls `putHashMapEntries()`, resulting in a 66-86% improvement ([PR #28243](https://github.com/openjdk/jdk/pull/28243));
- a new *intrinsic* for the AVX2 architecture has been added for binary search, resulting in a 1.5x to 2.35x improvement for arrays above a certain threshold (*int*=256, *long*=768, *short*=512, *char*=512) ([PR #30612](https://github.com/openjdk/jdk/pull/30612));
- Eliminated System Call When Opening Files: on Unix, `FileOutputStream` and `RandomAccessFile` used to call `fstat` after opening a file for writing to reject directories. Because the relevant open operation already fails with `EISDIR`, the extra system call was redundant;
- Cryptographic Acceleration: new *intrinsics* for AES/ECB, SHA-3 on AVX2, AES/CTR on RISK-V. 

You can find some interesting performance improvements listed in this article from Per-Ake Minborg, Claes Redestad: [Performance Improvements in JDK 27](https://inside.java/2026/09/28/performance-update-jdk27/).

On the security side, we can mention:
- the addition of TLS certificate compression support ([JDK-8372526](https://bugs.openjdk.org/browse/JDK-8372526));
- the addition of WISeKey certificates to the JVM truststore ([JDK-8372351](https://bugs.openjdk.org/browse/JDK-8372351));
- a new command `jcmd VM.security_properties` to list enabled security properties ([JDK-8364182](https://bugs.openjdk.org/browse/JDK-8364182));
- The **HSS/LMS** signature algorithm now supports the additional parameter sets defined in [RFC 9858](https://www.rfc-editor.org/info/rfc9858/). This RFC introduces new parameter sets for LMS/HSS using SHA-256/192 and SHAKE-based hash functions (SHAKE256/256 and SHAKE256/192). These new parameter sets allow for shorter signatures.

You can find all the security-related changes in Sean Mullan’s article [JDK 27 Security Enhancements](https://seanjmullan.org/blog/2026/09/15/jdk27).

`jcmd`, the JVM's all-in-one management command, now supports bash completion ([JDK-8357439](https://bugs.openjdk.org/browse/JDK-8357439)).

The experimental JVM Compiler Interface (JVMCI) feature has been removed ([JDK-8382582](https://bugs.openjdk.org/browse/JDK-8382582)). Quoting the release notes:
*The JVMCI provided an experimental internal Java API for writing a JIT compiler in Java that could be called by Hotspot. After more than a decade of experimentation, the costs of its maintenance and testing within the JDK have outweighed the benefits it provided to the limited number of downstream use cases it addressed. Downstream projects that depend on JVMCI must now integrate and maintain it in their own development trees, or remain on earlier JDK versions.*
GraalVM was the most well-known of these projects; the Graal compiler no longer mentions JVMCI in its [documentation](https://www.graalvm.org/latest/reference-manual/java/compiler/), using the Graal compiler instead of C2 should not be supported anymore.

## JFR Events

Here are the new Java Flight Recorder (JFR) events in the JVM:

- `ShenandoahPromotionInformation`: no description.

You can find all JFR events supported in this version of Java on the [JFR Events](https://sap.github.io/jfrevents/27.html) page.

## Conclusion

Java 27 is a small release with no major changes or new features. The good news is that its successor, Java 28, will very likely include the first phases of the Valhalla project, as [JEP 401 - Value Objects](https://openjdk.org/jeps/401) already has an open PR ([#31122](https://github.com/openjdk/jdk/pull/31122))!

To see all the changes in Java 27, check the [release notes](https://jdk.java.net/27/release-notes).
