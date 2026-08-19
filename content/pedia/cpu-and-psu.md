---
title: "CPU and PSU"
description: "From Java 9 onward, Oracle moved to a six-month release cadence, releasing a new JDK version every March and September. Security patches, bug fixes, and critical updates are delivered in three ways: CPU (Critical Patch Update) — Oracle's quarterly security ..."
url: "/pedia/cpu-and-psu/"
frozen: false
---

From Java 9 onward, Oracle moved to a **six-month release cadence**, releasing a new JDK version every March and September. Security patches, bug fixes, and critical updates are delivered in three ways:

**CPU (Critical Patch Update)** — Oracle's quarterly security update, released in January, April, July, and October. Each CPU for a supported JDK version contains all security fixes accumulated since the previous CPU.

**PSU (Patch Set Update)** — A superset of the CPU that also includes non-security bug fixes. PSUs are available for the same cadence but recommended primarily for users who have encountered specific bugs fixed in that release.

These terms originated in the Java 8 / Java 11 support era when Oracle published separate CPU and PSU installers. Under the current model, the distinction is largely internal to Oracle; what most users experience are the quarterly Oracle CPU releases and the updates published by their chosen JDK distributor.

## Free Updates and Commercial Support

Since Java 17, Oracle JDK is free to use in production under the Oracle No-Fee Terms and Conditions (NFTC). Free updates are provided for Oracle JDK only until the next LTS release ships. After that, continued Oracle JDK updates require a commercial Java SE subscription.

Distributions from Adoptium (Temurin), Azul, Amazon Corretto, Red Hat, BellSoft, and others provide free LTS updates under open-source licences (GPLv2+CE) for longer periods — often the full lifetime of a major Java version and beyond. See [LTS and Non-LTS Releases](https://foojay.io/pedia/lts-and-non-lts-releases/) and [JDK Distributions](https://foojay.io/pedia/jdk-distributions/) for details.

## Java 8 Legacy Note

During the Java 8 era (2014--2019), Oracle published CPU and PSU updates as distinct downloadable packages. Free public CPU updates for Java 8 ended for commercial users in January 2019. Extended support for Java 8 is available commercially from Oracle and multiple third-party vendors through at least 2030.

## See Also

* [LTS and Non-LTS Releases](/pedia/lts-and-non-lts-releases/)
* [JDK Distributions](/pedia/jdk-distributions/)
* [Security and Vulnerability Management](/pedia/security-vulnerability-management/)
