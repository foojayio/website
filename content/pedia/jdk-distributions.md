---
title: "JDK Distributions"
description: "The OpenJDK project publishes source code, not binaries. To actually run Java, you need a distribution: a pre-built, tested binary of OpenJDK packaged by a vendor. Multiple vendors publish OpenJDK distributions, each adding their own packaging, support commitments, and sometimes ..."
url: "/pedia/jdk-distributions/"
frozen: false
---

The OpenJDK project publishes source code, not binaries. To actually run Java, you need a **distribution**: a pre-built, tested binary of OpenJDK packaged by a vendor. Multiple vendors publish OpenJDK distributions, each adding their own packaging, support commitments, and sometimes additional features or patches.

All major distributions are built from the same OpenJDK source and must pass the [TCK](https://foojay.io/pedia/tck/) to be labelled "Java SE compatible". In practice they are interchangeable for most applications. The main differentiators are support terms, supported platforms, update frequency, and optional extras.

Popular distributions include Adoptium Temurin, Amazon Corretto, Azul Zulu, Azul Platform Prime, BellSoft Liberica, IBM Semeru, Microsoft Build of OpenJDK, Oracle JDK, Oracle OpenJDK builds, Red Hat OpenJDK, and SAP Machine. The [Disco API](https://foojay.io/pedia/disco-api/) provides a machine-readable catalogue of all available packages across every major distributor, making it straightforward to query which versions are available for a given platform and distribution.

When choosing a distribution, the key questions are: do you need commercial support and SLAs? How long do you need free security updates for a given LTS version? Do you need certified builds for regulated environments? For most open-source projects and self-managed deployments, Adoptium Temurin is a natural default due to its vendor-neutral governance under the Eclipse Foundation.

See also: [LTS and Non-LTS Releases](https://foojay.io/pedia/lts-and-non-lts-releases/), [TCK](https://foojay.io/pedia/tck/), [Disco API](https://foojay.io/pedia/disco-api/)
