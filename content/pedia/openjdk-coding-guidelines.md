---
title: "OpenJDK Coding Guidelines and Code Reviews"
description: "OpenJDK does not have a single exhaustive coding style guide. Sub-components come from diverse origins (HotSpot, the standard library, OpenJFX, etc.) and each has its own conventions. The main reference documents are: Developer's Guide: openjdk.org/guide/ — covers the development process, ..."
url: "/java-terms-explained/openjdk-coding-guidelines/"
aliases:
  - "/pedia/openjdk-coding-guidelines/"
---

OpenJDK does not have a single exhaustive coding style guide. Sub-components come from diverse origins (HotSpot, the standard library, OpenJFX, etc.) and each has its own conventions. The main reference documents are:

* **Developer's Guide:** [openjdk.org/guide/](https://openjdk.org/guide/) --- covers the development process, code conventions, changeset format, and review workflow.
* **How to Contribute:** [openjdk.org/contribute/](https://openjdk.org/contribute/) --- prerequisites, JCA (Oracle Contributor Agreement), and getting started.
* **HotSpot Style Guide:** [wiki.openjdk.org/display/HotSpot/StyleGuide](https://wiki.openjdk.org/display/HotSpot/StyleGuide) --- C++ conventions for JVM internals.
* **OpenJFX:** [wiki.openjdk.org/display/OpenJFX/Committing+the+Code](https://wiki.openjdk.org/display/OpenJFX/Committing+the+Code) and [Code Reviews](https://wiki.openjdk.org/display/OpenJFX/Code+Reviews).

## Code Reviews and Code Quality

Changes to OpenJDK are reviewed and approved by **Reviewers** — contributors with Reviewer role in a given repository. Multiple Reviewers must approve a change before it is integrated; the exact number depends on the sub-project. The integration itself is performed by a **Committer** using the [OpenJDK GitHub bots](https://github.com/openjdk/skara) (the Skara tooling).

Overall compatibility is assured through the [TCK](https://foojay.io/java-terms-explained/tck/) test suite. New features, compatibility-breaking changes, and reproducible bugs should be accompanied by [jtreg](https://foojay.io/java-terms-explained/jtreg-test-suites/) test cases. Larger changes are tracked via [JEPs](https://foojay.io/java-terms-explained/jep-jdk-enhancement-proposal/).

## See Also

* [Technology Compatibility Kit](/java-terms-explained/tck/)
* [jtreg Test Suites](/java-terms-explained/jtreg-test-suites/)
* [JEP (JDK Enhancement Proposal)](/java-terms-explained/jep-jdk-enhancement-proposal/)
