---
title: "Azul Zulu October 2026 Quarterly Update: 53 CVEs, Then a Zero-Day One Day Later"
date: "2026-10-23"
description: "A record 53 CVEs fixed in the October 2026 quarterly update for Azul Zulu, overtaken within a day by a zero-day: compromised eMudhra CA keys and an out-of-cycle release."
authors:
  - "frankdelporte"
image: "Azul-Prime-Stable-2308.jpg"
categories:
  - "Java"
  - "Java Core"
  - "Release Notes"
related_posts:
  - "azul-zulu-july-2026-quarterly-update-released"
  - "azul-zulu-april-2026-quarterly-update-released"
  - "all-azul-zulu-container-images-explained-ca-sa-and-chainguard"
  - "should-you-update-java-or-upgrade-and-which-version-should-you-use"
---

{{< warning >}}
The Out-of-Cycle release mentioned in this post, was followed by a new one on October 24. A new, related vulnerability has been detected. Azul asks all its customers to update to the latest release ASAP. See the [Azul Zulu release notes of October 24, 2026](https://docs.azul.com/core/release/october-2026-24-respin/) for all info.
{{< /warning >}}

A record-breaking quarterly update is supposed to be the big security event of the quarter. On October 20, 2026, Azul released the October 2026 update for Azul Zulu Builds of OpenJDK, covering Java 27, 25, 21, 17, 11, 8, 7, and 6, with patches for 53 CVEs. Within a day it was not enough anymore: a zero-day showed up that no quarterly schedule could have planned for, and on October 21 Azul shipped an out-of-cycle update for it.

This post covers both, because together they change the message. The question is no longer "which quarter's update do I install?", but "how fast can I install a security fix without breaking my application?"

## The Zero-Day: Compromised eMudhra CA Keys

On October 20, 2026, the same day as the quarterly update, [Mozilla disclosed](https://emudhra.we-have-a-problem.com/) that the private keys of eight eMudhra intermediate CA certificates were compromised. Anyone holding those keys can issue certificates that chain up to a trusted root, for any domain.

Java makes this worse than it sounds: it does not check certificate revocation by default. Until the certificates are blocked in the JDK itself, a Java application accepts a fraudulent certificate issued with those keys as valid. There was no patch on the day of disclosure, and no quarterly release is due until January.

With huge team efforts, Azul was able to release an out-of-cycle update on October 21 for [Azul Zulu Builds of OpenJDK](https://docs.azul.com/core/release/october-2026-respin/release-notes)  27, 25, 21, 17, 11, 8, 7, and 6 and [Azul Zing Builds of OpenJDK](https://docs.azul.com/prime/release-notes#prime_stable_26_08_101_0)  25, 21, 17, 11, and 8. It adds the eight certificates to the blocklist, in both the CPU and PSU configurations, so Java stops trusting anything signed with the compromised keys.

Azul recommends installing it immediately. If you cannot update right away, the release notes describe a [temporary workaround based on security properties](https://docs.azul.com/core/emudhra-workaround).

{{< important >}}
If you already rolled out the quarterly update, you need to update again. If you have not started yet, go straight to the [latest out-of-cycle builds of October 24](https://docs.azul.com/core/release/october-2026-24-respin/): they supersede both the quarterly and the October 21 ones.
{{< /important >}}

## Why This Changes the Message

A zero-day does not wait for your test cycle. The advice "install the next quarterly update" assumes the vulnerability is known and fixed on a schedule. Here the fix came from a disclosure, not from the calendar, and AI has cut the time between disclosure and exploit from weeks to hours.

That is where the Azul CPUs (Critical Patch Updates) earn their place. They add only security fixes to the previous quarter's extensively tested PSU (Patch Set Updates), so you can deploy them right away, twice in two days if needed, with minimal regression risk. A PSU also carries hundreds of non-security changes, and 1 in 5 OpenJDK PSUs over the past six years caused a regression. Azul is the only OpenJDK vendor we know of that ships security-only updates. Oracle's public documentation shows a single quarterly release, which also carries bug fixes.

## The Quarterly Update

Every three months, in January, April, July, and October, the OpenJDK project ships security patches, bug fixes, and improvements for all supported Java versions.

### A Record 53 CVEs

This security update delivered [patches for 53 CVEs](https://docs.azul.com/core/release/october-2026/release-notes) in Azul Zulu and [41 in Azul Prime](https://docs.azul.com/prime/release-notes#prime_stable_26_08_100_0). That is an all-time high for a single OpenJDK update, and almost seven times the two-year average of 7.6 CVEs per quarter.

* 47 CVEs in core OpenJDK functionality and 6 in JavaFX
* 2 high-score CVEs (7.0 and above), 37 medium-score, and 14 low-score

If you wonder why Oracle's advisory says 55: Oracle also counts CVEs in GraalVM and its own proprietary components. The affected areas include Networking, 2D graphics, Security, JGSS, and JavaFX (through WebKitGTK). 

With 53 CVEs, this is a release to deploy quickly, and the CPU builds are the fastest safe way to do it.

### Azul's Quarterly Update Cycle

Azul packages each release in two flavors.

#### Critical Patch Updates (CPU)

CPU releases contain only security fixes. Azul builds each CPU on top of the prior quarter's PSU and applies just the security patches, so teams can deploy urgent fixes with minimal risk of new regressions.

Note that what Oracle calls a CPU is its single quarterly release, which includes bug fixes. That corresponds to Azul's PSU, not Azul's CPU.

#### Patch Set Updates (PSU)

PSU releases include every fix from the matching CPU, plus non-security bug fixes, enhancements, and new functionality. Azul aligns each PSU with the corresponding OpenJDK project quarterly release.

#### Getting the Most Out of CPU and PSU Releases

Install the CPU release quickly, after a short test, to close security gaps fast. Test the PSU release over a longer period afterward, and move to it before the next quarterly update arrives.

### Non-Security Changes in the PSU Builds

On top of the security fixes, the PSU builds carry non-security bug fixes, enhancements, and new functionality. None of these are in the CPU builds:

* Java 25: 183
* Java 21: 136
* Java 17: 173
* Java 11: 28
* Java 8: 25

That is 545 non-security changes in total. They include new functionality, such as the post-quantum cryptography [JEP 496 (ML-KEM)](https://openjdk.org/jeps/496) and [JEP 497 (ML-DSA)](https://openjdk.org/jeps/497) backported to Java 17 and 21.

Any of these changes can introduce instability: 1 in 5 OpenJDK PSUs over the past six years caused a regression. So when you must patch urgently, the CPU builds are the fastest safe path to production.

### Changes in the October 2026 Update

Check the [full release notes](https://docs.azul.com/core/release/october-2026/release-notes) for every detail. These changes stand out.

#### Find Vulnerable Java With the Azul JDK X-Ray Tool

The new [Azul JDK X-Ray Tool](https://github.com/AzulSystems/azul-jdk-xray) is a single-file script that answers one question: is there a vulnerable Java on this machine? It is available as a PowerShell script for Windows (`azul-jdk-xray.ps1`, PowerShell 5.1+) and as a shell script for macOS and Linux (`azul-jdk-xray.sh`).

#### HTTP Server and File Server Hardening

`HttpServer` now follows RFC 9112 and accepts only a single `Host` header per request. A request that violates this gets an HTTP 400 response. Set `sun.net.httpserver.disableHostValidation=true` to restore the old behavior.

On Windows with JDK 21 and later, `SimpleFileServer` now rejects URLs that contain backslashes.

#### XML Processing Hardening

Two new system properties restrict dangerous XML processing patterns:

* `jdk.xml.enableTemplatesImplDeserialization` (default `false`): `TemplatesImpl` objects now throw an `UnsupportedOperationException` when deserialized.
* `jdk.xml.allowNonXSLTSysProps` (default `false`): the XPath `system-property()` function is limited to properties defined by XSLT.

#### RMI and JavaFX Clipboard

On Java 6, 7, and 8, port forwarding in `java-rmi.cgi.sh` is now denied by default. Set `FORWARD_PERMIT` to allow it.

JavaFX `Clipboard` and `Dragboard` operations now deserialize only to `String` or `ByteBuffer` by default. Set `javafx.dnd.deserializeObjectGraph=true` if your application depends on the old behavior.

#### Intelligence Cloud: CRS Agent Retired From CPU Builds

As announced in July, Azul retires the embedded Connected Runtime Service (CRS) agent from Azul Zulu Builds of OpenJDK. The standalone [Azul Intelligence Cloud Agent](https://docs.azul.com/intelligence-cloud/setup-jvm/using-ic-agent) covers the same use cases more efficiently. This October release applies the change to the CPU configurations, after the PSU configurations in July.

#### CRaC: Checkpoint Again After a Restore

The Warp engine of [Coordinated Restore at Checkpoint (CRaC)](https://docs.azul.com/crac/) now supports checkpointing again after a restore, matching what the `criuengine` already offered. In direct mapping mode, the entire image chain has to be preserved.

#### Platform Changes

Azul reduces testing on platforms that reached end of life: Debian 11, Ubuntu 18.04 and 20.04, Alpine Linux older than 3.24, Fedora 43 (replaced by Fedora 44), CentOS Stream older than 9, and macOS older than 15. RHEL 10.x is supported (PSU only), as is Wolfi Linux for JDK 21 and later. Check the [Supported Platforms page](https://docs.azul.com/core/supported-platforms) for the full matrix.

#### IANA Time Zone Data

This release ships [IANA Time Zone Database](https://www.iana.org/time-zones) version 2026e.

## Version Bumps

These are the version numbers of the October 2026 out-of-cycle release. 

| Release | OpenJDK PSU  | Azul Zulu CPU  |
|---------|-------------|-------------|
| 27      | 27.0.1+9    |27.30.107|
| 25      | 25.0.5+8    |25.38.107|
| 21      | 21.0.13+9   |21.54.107|
| 17      | 17.0.21+9   |17.70.111|
| 11      | 11.0.33+9   |11.92.109|
| 8       | 8u512-b08   |8.98.0.107|
| 7 (CPU) | 7u521-b01   |7.89.0.110|
| 6 (CPU) | 6b191       |6.83.0.108|

Run `java -version` to check how far behind your JDK sits.

## Next Steps

Install the out-of-cycle builds now, then plan a process that can absorb the next surprise: a fast path for security-only updates, and a slower, tested path for the PSU. The next scheduled dates:

* 2027-01-19: CPU/PSU Update
* 2027-03-23: OpenJDK 28 release
* 2027-04-20: CPU/PSU Update
* 2027-07-20: CPU/PSU Update
* 2027-09-21: OpenJDK 29 (LTS) release

Please keep in mind: A JDK you don't update is a JDK carrying known vulnerabilities into production, and the next zero-day will not wait for the January update.

## Read More

More info about the eMudhra CA compromise:

* [Azul Zulu October 2026 Quarterly Update Release Notes](https://docs.azul.com/core/release/october-2026/release-notes)
* [Azul Out-of-Cycle, October 21, 2026 Update Release Notes](https://docs.azul.com/core/release/october-2026-respin/release-notes)
* [Azul eMudhra workaround](https://docs.azul.com/core/release/october-2026-respin/emudhra-workaround)
* [eMudhra Intermediate CA Compromise](https://emudhra.we-have-a-problem.com/)
 by Dennis Jackson
* [eMudhra and Java: Check and Fix cacerts and Custom Trust Stores](https://safeguard.sh/resources/blog/emudhra-java-cacerts-check-and-fix) by Safeguard.sh
* [The eMudhra CA Compromise: Why Your JVM Is Vulnerable (and How to Fix It Properly)](https://glaforge.dev/posts/2026/10/21/the-emudhra-ca-compromise-why-your-jvm-is-vulnerable-and-how-to-fix-it/) by Guillaume Laforge
* [Free Java, Real Risk: Why Unsupported OpenJDK Is Now a Security Gamble](https://www.azul.com/blog/free-java-real-risk-why-unsupported-openjdk-is-now-a-security-gamble/) by Simon Ritter
