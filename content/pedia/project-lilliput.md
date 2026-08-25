---
title: "Project Lilliput"
description: "Project Lilliput shrinks the Java object header. Compact object headers cut it from 128 to 64 bits, which typically saves a few percent of heap on real applications and more on object-heavy ones."
url: "/pedia/project-lilliput/"
frozen: false
---

Project Lilliput is the OpenJDK project working on **the size of the Java object header** — the per-object bookkeeping the JVM stores in front of an object's fields.

Every object on [the heap](/pedia/the-heap-stack-and-metaspace/) carries a header holding its class pointer and its mark word, which the JVM uses for locking, identity hash codes and garbage collection metadata. On a 64-bit JVM that header has traditionally been 128 bits. For an object with two `int` fields, the bookkeeping is larger than the data.

Lilliput's first deliverable, **compact object headers**, reduces the header to 64 bits by packing the class information into the mark word. It arrived as an experimental feature and was later enabled by default, having been measured across a wide range of workloads first. The saving is not dramatic per object — it is a few percent of heap on typical applications — but it applies to every object in the heap at once, and it is larger on workloads that allocate many small objects. Less heap in use also means less for the [garbage collector](/pedia/garbage-collection/) to traverse, so throughput and pause times can improve alongside footprint.

**This is the kind of change that has to be invisible to be useful.** A smaller header changes object layout, which touches locking, hashing and every collector, so the work was gated behind a flag for several releases while those interactions were shaken out. Nothing in application code changes; the feature is a JVM flag and a measurement, not an API.

Longer term the project has explored shrinking the header further, to 32 bits.

More reading on Foojay:

* [Does Java Really Use Too Much Memory? Let's Look at the Facts (JEPs)](/today/does-java-really-use-too-much-memory-lets-look-at-the-facts-jeps/)
* [Java 24 Rolls Out Today! Find Out Why It's Aptly Named](/today/java-24-rolls-out-today-find-out-why-its-aptly-named/)
* [Here's Java 25, Ready to Perform to the Limit](/today/heres-java-25-ready-to-perform-to-the-limit/)

## See Also

* [The Heap, Stack and Metaspace](/pedia/the-heap-stack-and-metaspace/)
* [Garbage Collection](/pedia/garbage-collection/)
* [GC Algorithms: G1, ZGC and Shenandoah](/pedia/gc-algorithms-g1-zgc-and-shenandoah/)
* [Value Objects (Project Valhalla)](/pedia/value-objects-project-valhalla/)
* [OpenJDK Projects](/pedia/openjdk-projects/)
