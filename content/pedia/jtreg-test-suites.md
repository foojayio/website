---
title: "jtreg Test Suites"
description: "jtreg is the test harness for regression and unit testing used by the JDK test framework. For many OpenJDK distributions, the jtreg tests are run in addition to the TCK to provide broader coverage of JVM behaviour and standard library ..."
url: "/pedia/jtreg-test-suites/"
frozen: false
---

jtreg is the test harness for regression and unit testing used by the JDK test framework. For many OpenJDK distributions, the jtreg tests are run in addition to the [TCK](https://foojay.io/pedia/tck/) to provide broader coverage of JVM behaviour and standard library correctness.

The OpenJDK source repository (hosted at [github.com/openjdk/jdk](https://github.com/openjdk/jdk)) contains tens of thousands of jtreg tests. As of Java 21, the test suite contains over 100,000 individual test cases across the `test/` directory tree — covering the compiler, JVM, standard libraries, and tools. The count grows with every release.

jtreg test files use a simple annotation syntax in the file header to declare test metadata: the `@test`, `@run`, `@bug`, and `@summary` tags drive the harness. Tests can be written in Java, shell scripts, or as `@compile`-only checks. The harness supports parallel execution, test filtering by keyword or bug ID, and HTML report generation.

The jtreg tool itself is open source and available at [openjdk.org/jtreg/](https://openjdk.org/jtreg/). It is used not only by the core JDK project but by downstream OpenJDK projects such as OpenJFX, Loom, and Valhalla to maintain regression coverage during development.
