---
title: "Azul Zulu April 2026 Quarterly Update Released"
date: "2026-04-23T07:12:29+00:00"
lastmod: "2026-04-23T07:18:47+00:00"
description: "The April 2026 quarterly update for Azul Zulu Builds of OpenJDK is now available. This quarterly release brings security and bug fixes, along with a few - by Frank Delporte"
authors:
  - "frankdelporte"
image: "Azul-Prime-Stable-2308.jpg"
categories:
  - "Java"
  - "Release Notes"
tags:
related_posts:
  - "are-java-security-updates-important"
  - "azul-brings-java-from-edge-to-cloud"
  - "faster-java-warmup-crac-versus-readynow"
  - "java-security-starts-with-the-jvm"
frozen: false
---

The **April 2026 quarterly update for Azul Zulu Builds of OpenJDK** is now available. This quarterly release brings **security and bug fixes**, along with a few notable new changes, to all currently supported Java versions.

## The Quarterly Update Cycle

Every three months (in January, April, July, and October), the OpenJDK project releases security updates, bug fixes, and improvements for all supported Java versions. This predictable schedule helps organizations plan their Java updates and maintain secure, stable production environments.

Azul packages these quarterly releases in two flavors: **Critical Patch Updates (CPU)** with the security-only patches and **Patch Set Updates (PSU)**.

### Critical Patch Updates (CPU)

Critical Patch Updates (CPU) releases **focus exclusively on the security patches**. They contain fixes for security vulnerabilities and critical bug fixes only. CPUs are based on the previous quarter's PSU release with only security patches applied. This conservative approach makes them ideal for deploying urgent security fixes with minimal risk of introducing new issues.

### Patch Set Updates (PSU)

PSU releases provide a more comprehensive update. They incorporate **all security fixes from the corresponding CPU, plus additional non-security bug fixes** and alignment with the associated OpenJDK project quarterly release.

### Making Perfect Use of CPU and PSU releases

In an ideal scenario, you install a CPU as soon as possible after a brief test to secure your environment. After that, you test with the PSU release for a longer time. Once all your tests are green, switch your environment to the PSU version. This must be completed before the next quarterly update, so you can easily repeat the cycle.

## Difference With the Six-Month Release Cycle

The six-month release cycle, introduced with OpenJDK 9, brings a new major OpenJDK version in March and September. This April 2026 quarterly update is the first update to Java 26, which was released on March 17, 2026.

The quarterly cycle brings updates to existing releases. This April CPU/PSU release bumps the versions (from/to):

* 26.0.0 -\> 26.0.1
* 25.0.2 -\> 25.0.3
* 21.0.10 -\> 21.0.11
* 17.0.18 -\> 17.0.19
* 11.0.30 -\> 11.0.31

Remember: to keep your systems secure, you need to install an update of your JDK every three months. Run `java -version` to check how far behind you are.

## Security and Bug Fixes in This Release

In this release, there are [11 Common Vulnerabilities and Exposures (CVE) fixes](https://docs.azul.com/core/release-notes#fixed-common-vulnerabilities-and-exposures) of which three got a high-severity severity score of 7.5 and one is not applicable to Azul Zulu.

Besides these security problems, this release fixes the following number of issues:

* Java 26: **55** total bug fixes and improvements
* Java 25: **348** total bug fixes and improvements
* Java 21: **193** total bug fixes and improvements
* Java 17: **137** total bug fixes and improvements
* Java 11: **71** total bug fixes and improvements
* Java 8: **84** total bug fixes and improvements

The number of fixes in Azul Zulu includes OpenJDK non-security fixes, Azul-specific fixes, and security fixes, so the total number may differ slightly from the OpenJDK numbers.

## Azul Zulu April 2026 Release Notes

Azul released Azul Zulu Builds of OpenJDK (Zulu) in versions 26, 25, 21, 17, 11, 8, 7, and 6. You can check the [full Azul release notes here](https://docs.azul.com/core/release-notes). A few highlights are worth calling out.

### Azul Zulu Joins Docker's Official Images Program

This is probably the biggest news in this release. After more than two years of work with the Docker team, Azul Zulu has officially joined Docker's Official Images program. The images are available on Docker Hub under the `azul-zulu` name:

```
docker pull azul-zulu:21
docker pull azul-zulu:21-jre
docker pull azul-zulu:25
```

What does "Official Image" mean in practice? It means the Docker team has reviewed and verified the images, they follow Docker's best practices, and they will show up in Docker Hub search results alongside other official images like `ubuntu` or `node`. For Java developers running containers, it is a stronger trust signal out of the box.

The old images at `azul/zulu-openjdk` on Docker Hub are planned to be retired by the end of 2026. If you use those today, now is a good time to start planning your migration.

### New Distributions: JavaFX for ARM-based Windows 11 and Fedora 43

Two new platform additions in this release. First, JavaFX support on 64-bit ARM-based Windows 11 for Java 21. As more ARM laptops ship (think Snapdragon-based Windows machines), this fills a gap that was starting to matter for JavaFX developers. These builds are available for Azul Core customers.

Second, Zulu now supports Fedora 43.

### CRaC: Warp Is Now the Default Engine

[Coordinated Restore at Checkpoint (CRaC)](https://docs.azul.com/crac/) is an OpenJDK project that lets you start Java programs faster by restoring from a checkpoint instead of doing a cold start. Azul Zulu ships with two [CRaC engines](https://docs.azul.com/crac/usage/crac-engines): `warp` and `criu`. As of this release, `warp` is the default for all supported platforms, replacing `criu`. It offers better performance and reliability and is the recommended choice for most deployments.

If you have automation or scripts that explicitly set the engine, it is worth reviewing whether you still need to specify it.

### Heads-Up: macOS JVM Detection Changes Coming

Azul is changing the directory layout inside macOS bundles so that only the `Contents` directory will remain underneath the top-level directory. All symlinks and the `zulu-$VER.jdk` directory that previously contained `Contents` are planned to be removed. This will help macOS properly detect installed Zulu versions.

The catch: applications that rely on the current, specific Zulu installation path may break in the next release. Instructions on how to address this will be provided. Worth keeping an eye on if you use macOS and have tooling that discovers your JDK location.

### IANA Time Zone Data

This release ships with [IANA Time Zone Database](https://www.iana.org/time-zones) version **2026a**.

### Known Issue

There is one known issue in this release: if you use AWT with the Security Manager, your application may fail to start with an `AccessControlException` related to `jdk.awt.Desktop.bypassBrowserForURI`. A workaround is available using a custom Java policy file. Check the [release notes](https://docs.azul.com/core/release-notes#known-issues) for the exact steps.

## Next Steps

Plan your testing and deployment schedule to ensure your Java applications benefit from the latest security patches and bug fixes. And mark your calendar, the next quarterly updates and releases arrive on:

* 2026-07-21: CPU/PSU Update
* 2026-09-15: OpenJDK 27 release
* 2026-10-20: CPU/PSU Update
* 2027-01-19: CPU/PSU Update
* 2027-03-23: OpenJDK 28 release
* 2027-04-20: CPU/PSU Update
* 2027-07-20: CPU/PSU Update
* 2027-09-21: OpenJDK 29 (LTS) release**\*\*\*\***
