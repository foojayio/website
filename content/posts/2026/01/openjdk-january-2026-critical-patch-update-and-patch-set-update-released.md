---
title: "OpenJDK January 2026 Critical Patch Update and Patch Set Update Released"
slug: "openjdk-january-2026-critical-patch-update-and-patch-set-update-released"
date: "2026-01-21T08:32:01+00:00"
description: "The January 2026 OpenJDK quarterly updates are now (or will soon be) available from various OpenJDK distributors. This quarterly release brings important - by Frank Delporte"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2026/01/openjdk_january_2026.png"
categories:
  - "CRaC"
  - "Java"
  - "Java Core"
  - "Release Notes"
tags:
related_posts:
  - "foojay-podcast-71"
  - "foojay-podcast-78"
  - "foojay-podcast-68"
  - "foojay-podcast-28"
frozen: false
---

The January 2026 OpenJDK quarterly updates are now (or will soon be) available from various OpenJDK distributors. This quarterly release brings important security fixes and updates to all currently supported Java versions.

The Quarterly Update Cycle {#h2-0-the-quarterly-update-cycle}
-------------------------------------------------------------

**Every three months** (in January, April, July, and October), the OpenJDK project releases **security updates, bug fixes, and improvements** for **all supported Java versions**. This predictable schedule helps organizations plan their Java updates and maintain secure, stable production environments.

These quarterly releases come in two flavors: Critical Patch Updates and Patch Set Updates.

### Critical Patch Updates (CPU) {#h3-1-critical-patch-updates-cpu}

CPU releases focus exclusively on security. They contain:

* Fixes for security vulnerabilities
* Critical bug fixes only

CPUs are based on the previous quarter's PSU release with only security patches applied. This conservative approach makes them ideal for deploying urgent security fixes with minimal risk of introducing new issues. If stability is your top priority and you want to avoid any non-security changes, CPU releases are your go-to option.

### Patch Set Updates (PSU) {#h3-2-patch-set-updates-psu}

PSU releases provide a more comprehensive update by incorporating:

* All security fixes from the corresponding CPU
* Additional non-security bug fixes
* Alignment with the associated OpenJDK project quarterly release

PSUs offer the full set of improvements and refinements from the OpenJDK community while still maintaining the stability expected from a minor update.

In an ideal scenario, you install a CPU as soon as possible after a brief test to secure your environment. After that, you start testing with the PSU release for a longer time. Once all your tests are green, switch your environment to the PSU version. This must be completed before the next quarterly update, so you can easily repeat the cycle.

Difference With the Six-Month Release Cycle {#h2-3-difference-with-the-six-month-release-cycle}
-----------------------------------------------------------------------------------------------

The **six-month release cycle** , introduced with OpenJDK 9, **brings a new major OpenJDK version** in March and September. For example, on September 16, 2026, we saw the "birth" of Java 25 (25.0.0).

Since then, we already had the October CPU/PSU release, which brought the first security and bug fixes to all supported versions, so also bumping Java 25 to 25.0.1.

The **quarterly cycle brings updates to existing releases**. For example, the January CPU/PSU release bumps the PSU versions (from/to):

* 25.0.1 -\> 25.0.2
* 21.0.9 -\> 21.0.10
* 17.0.17 -\> 17.0.18
* 11.0.29 -\> 11.0.30

Important message to understand: to keep your systems secure, you need to install an update of your JDK every three months. Check `java -version` to understand how much updates you missed. For instance, if you installed the first release of JDK 17 (17.0.0), you have missed 18 updates with security and bug fixes by now!

Distributor Availability {#h2-4-distributor-availability}
---------------------------------------------------------

The beauty of the OpenJDK ecosystem is that multiple distributors provide builds of these releases. Major OpenJDK distributors, including Azul (Zulu), BellSoft, Oracle, Eclipse (Temurin), and others, typically make updated builds available for all currently supported Java versions within a few hours or days. This typically includes:

* Current LTS (Long-Term Support) versions (11, 17, 21, 25).
* The latest non-LTS release (till six months after its release).
* Extended support versions (depending on your distributor, for instance, Azul for Java 6, 7, and 8)

Check with your specific OpenJDK distributor to confirm which versions receive updates and the support timeline for your deployment.

In this January Release {#h2-5-in-this-january-release}
-------------------------------------------------------

The January release, as [stated by Oracle](https://www.oracle.com/security-alerts/cpujan2026.html#AppendixJAVA), *contains 11 new security patches for Oracle Java SE. All of these vulnerabilities may be remotely exploitable without authentication, i.e., may be exploited over a network without requiring user credentials. The highest CVSS v3.1 Base Score of vulnerabilities affecting Oracle Java SE is 7.5.*

Number of total bug fixes and improvements included in today's OpenJDK update release:

* Java 25: 380
* Java 21: 384
* Java 17: 349
* Java 11: 65
* Java 8: 64

Azul has also posted its release notes, which you can [find here](https://docs.azul.com/core/release-notes). One of the highlights: [new features in Azul's integration of Coordinated Restore at Checkpoint (CRaC)](https://docs.azul.com/core/release-notes#changes-in-coordinated-restore-at-checkpoint-crac):

* Image Encryption: By default, the CRaC images contain application data, including environment variables and arguments, in plaintext. If this data contains secrets and the images are accessible to untrusted parties, you can encrypt the images to ensure the secrets stay hidden.
* Alternative Images: Automatically select the best CRaC image at restore. This brings a solution to modern cloud environments where a single checkpoint image may not be enough

Next Steps {#h2-6-next-steps}
-----------------------------

Review the release notes from your OpenJDK distributor to understand the specific fixes and improvements in this quarter's release. Plan your testing and deployment schedule to ensure your Java applications benefit from the latest security patches and bug fixes.

Mark your calendar, the next quarterly updates and releases arrive on:

* 2026-03-17: OpenJDK 26 release
* 2026-04-21: CPU/PSU Update
* 2026-07-21: CPU/PSU Update
* 2026-09-15: OpenJDK 27 release
* 2026-10-20: CPU/PSU Update
* 2027-01-19: CPU/PSU Update
* 2027-03-23: OpenJDK 28 release
* 2027-04-20: CPU/PSU Update
* 2027-07-20: CPU/PSU Update
* 2027-09-21: OpenJDK 29 (LTS) release
