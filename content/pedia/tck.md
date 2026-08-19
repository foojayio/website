---
title: "Technology Compatibility Kit"
description: "Technology Compatibility Kit is the official Java SE test suite used to test compatibility and performance of the JRE/JDK against Java SE specifications."
url: "/pedia/tck/"
frozen: false
---

The TCK (Technology Compatibility Kit) is the official Java SE test suite, originally developed by Sun Microsystems and now maintained by Oracle. Any JDK binary that claims to be "Java SE compatible" must pass the TCK for its target release.

The TCK contains tests across a wide range of areas: Java SE APIs, JIT compiler correctness, bytecode interpreter behaviour, the Java memory model (memory barriers, thread synchronisation, atomic access), garbage collectors, and more. As of Java 21, the test suite contains well over 150,000 individual tests — the count grows with every new Java version as new APIs and features are added. Running the full suite on a single machine takes hours to days.

The TCK can be partitioned so that a representative subset can be run on resource-constrained platforms (such as embedded targets) in a manageable time.

## TCK Compliance

Any JRE/JDK binary must pass the TCK in its entirety to be labelled "Java SE compatible". It is not sufficient to test a representative binary; each individual binary must pass.

Below are major OpenJDK distributions and their TCK compliance status:  

|        Distribution        | TCK Compliant? |
|----------------------------|----------------|
| Adoptium Temurin           | ✅              |
| Amazon Corretto            | ✅              |
| Azul Platform Prime        | ✅              |
| Azul Zulu                  | ✅              |
| BellSoft Liberica          | ✅              |
| Microsoft Build of OpenJDK | ✅              |
| Oracle JDK                 | ✅              |
| Oracle OpenJDK builds      | ✅              |
| Red Hat builds of OpenJDK  | ✅              |
| SAP Machine                | ✅              |

## TCK Licence and Access

The TCK is Oracle intellectual property and is not open source. It is licensed to certain third parties under a separate agreement. Details on access conditions and current licensees are available at [openjdk.org/groups/conformance/JckAccess/](https://openjdk.org/groups/conformance/JckAccess/). An alternative open-source compatibility test suite, [jtreg](https://foojay.io/pedia/jtreg-test-suites/), is run in addition to the TCK by many distributors.

## See Also

* [jtreg Test Suites](/pedia/jtreg-test-suites/)
* [JDK Distributions](/pedia/jdk-distributions/)
* [OpenJDK](/pedia/openjdk/)
* [OpenJDK Coding Guidelines and Code Reviews](/pedia/openjdk-coding-guidelines/)
