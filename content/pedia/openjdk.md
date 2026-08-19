---
title: "OpenJDK"
description: "OpenJDK is the open-source reference implementation of the Java Platform, Standard Edition (Java SE) specification. It is the upstream project from which virtually all Java distributions are built. OpenJDK is source code, not a runnable binary — to run Java ..."
url: "/pedia/openjdk/"
frozen: false
---

[OpenJDK](https://openjdk.org) is the open-source reference implementation of the Java Platform, Standard Edition (Java SE) specification. It is the upstream project from which virtually all Java distributions are built. OpenJDK is source code, not a runnable binary — to run Java applications you need a [distribution](https://foojay.io/pedia/jdk-distributions/) built from that source.

Oracle leads OpenJDK development and contributes the majority of the code, but the project has contributions from Amazon, Microsoft, Red Hat, SAP, Azul, Google, and many individual developers. Governance follows the OpenJDK bylaws, with an Author → Committer → Reviewer hierarchy. New features are proposed via [JEPs (JDK Enhancement Proposals)](https://foojay.io/pedia/jep-jdk-enhancement-proposal/).

To use an analogy: OpenJDK is like the Linux kernel. Linux itself is source code; to run applications on Linux you need a distribution — Red Hat, Ubuntu, Debian. OpenJDK is the same: to run Java, you install a distribution such as Adoptium Temurin, Azul Zulu, Amazon Corretto, or Oracle JDK. All of them start from the same OpenJDK source and must pass the [TCK](https://foojay.io/pedia/tck/) to be compatible with the Java SE specification.

OpenJDK source is available on GitHub at [github.com/openjdk/jdk](https://github.com/openjdk/jdk). Related projects — Loom (virtual threads), Valhalla (value objects), Panama (native interop), Leyden (AOT) — have their own repositories under the same `openjdk` GitHub organisation.

## See Also

* [JDK Distributions](/pedia/jdk-distributions/)
* [JEP (JDK Enhancement Proposal)](/pedia/jep-jdk-enhancement-proposal/)
* [Technology Compatibility Kit](/pedia/tck/)
