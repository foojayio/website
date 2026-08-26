---
title: "Azul August 2026 Release: Java's First Monthly CSPU"
date: "2026-08-21T07:48:46+00:00"
lastmod: "2026-08-26T06:11:28+00:00"
description: "If someone discloses a 0-day CVE the day after a quarterly update, you could wait up to three months for a patched build. That gap is closed now."
authors:
  - "frankdelporte"
image: "Azul-Prime-Stable-2308.jpg"
categories:
  - "Java"
  - "Release Notes"
related_posts:
  - "new-between-quarters-security-updates-for-java-what-cspus-mean-for-your-release-pipeline"
  - "azul-zulu-july-2026-quarterly-update-released"
  - "azul-zulu-april-2026-quarterly-update-released"
  - "should-you-update-java-or-upgrade-and-which-version-should-you-use"
frozen: false
---

If someone discloses a 0-day CVE the day after a quarterly update, you could wait up to three months for a patched build. That gap is closed now. The August 2026 release is the first monthly **Critical Security Patch Update (CSPU)** in Java's history, and Azul shipped it on schedule across both its product lines.

* **Core (Zulu Builds of OpenJDK):** CSPU builds for Java 26, 25, 21, 17, 11, and 8, with backports to Java 7 and 6. See the [Core release notes](https://docs.azul.com/core/release-notes).
* **Prime (Zing Builds of OpenJDK):** Azul Zing JVM 26.02.310.0 for Java 25, 21, 17, 11, and 8. See the [Prime release notes](https://docs.azul.com/prime/release-notes#prime_stable_26_02_310_0).

This post explains what a CSPU is and what changes about how you plan patches.

## What is a CSPU?

A Critical Security Patch Update is a targeted release that delivers security and other critical fixes between the regular quarterly updates. The third Tuesdays of the months, between quarterly update releases, are reserved for these releases, if the OpenJDK community decides to have them.

The motivation for CSPUs (as [described by Oracle](https://blogs.oracle.com/security/accelerating-vulnerability-detection-and-response-at-oracle)) was the increased number of CVEs, driven by the widespread use of AI-driven analysis tools that help find new CVEs and exploits. And the need to address them faster. It doesn't mean these CVEs are publicly known. They are known to the [OpenJDK Vulnerability Group](https://openjdk.org/groups/vulnerability/), but not publicly. Under the old cadence, a fix could sit for up to three months before reaching you in the next quarterly build. A CSPU can deliver such a fix in the next monthly window instead.

But a CSPU's scope stays deliberately narrow, as it includes only security and other critical fixes. No new features get added or APIs changed, and no behavior is modified beyond the fixes themselves. It also runs through the same build and stability testing Azul and other distributors apply to every quarterly release.

A CSPU focuses on security and is not related to "stability". In general, if there is a stability issue, OpenJDK respins the release and doesn't wait for the next release window.

## Two build lines: CPU and PSU

Azul splits each CSPU into two separate build lines, and picking the right one matters more now that patches arrive monthly.

* **CSPU for CPU builds** contain only critical and security fixes on top of the previous quarterly CPU (Critical Patch Update). They change as little as possible, so Azul recommends them for production as the fastest safe path to a patched runtime.
* **CSPU for PSU builds** build on the previous quarterly PSU (Patch Set Update) and also carry accumulated non-security fixes. Azul recommends them for testing and to be deployed before moving to the next security update. They are also the only line available to free Zulu Community (CA) users.

That split is the practical answer to guarantee stability. As security updates may arrive as often as monthly, your window to test and roll out each one shrinks. Security-only CPU builds keep regression risk minimal, so you can move a patch to production quickly without waiting on a full validation cycle for unrelated bug fixes. And after validating the PSU, you can move to the version which also contains other fixes and improvements.

## Azul Zulu Build Numbers

| Java | CSPU for CPU (security-only) | CSPU for PSU (security + non-security) |
|------|------------------------------|----------------------------------------|
| 26   | n/a                          | 26.32.203 (CA) / 26.32.204 (SA)        |
| 25   | 25.35.204 (SA)               | 25.36.205 (CA) / 25.36.206 (SA)        |
| 21   | 21.51.204 (SA)               | 21.52.203 (CA) / 21.52.204 (SA)        |
| 17   | 17.67.204 (SA)               | 17.68.203 (CA) / 17.68.204 (SA)        |
| 11   | 11.89.204 (SA)               | 11.90.205 (CA) / 11.90.206 (SA)        |
| 8    | 8.95.0.204 (SA)              | 8.96.0.205 (CA) / 8.96.0.206 (SA)      |
| 7    | 7.87.0.204 (SA)              | n/a                                    |
| 6    | 6.81.0.204 (SA)              | n/a                                    |

CA marks the Community, free-to-use builds. SA marks the Subscription builds.

## Security fixes

This CSPU fixes five CVEs of which [four apply to Azul Zulu builds](https://docs.azul.com/core/release-notes#fixed-common-vulnerabilities-and-exposures): one high-severity, two medium, and one low.

|                                                  CVE                                                  |     Component     | Base score | Severity |
|-------------------------------------------------------------------------------------------------------|-------------------|------------|----------|
| [CVE-2026-70906](https://docs.azul.com/core/release-notes#fixed-common-vulnerabilities-and-exposures) | 2D                | 7.5        | High     |
| [CVE-2026-61308](https://www.cve.org/CVERecord?id=CVE-2026-61308)                                     | Networking / HTTP | 6.8        | Medium   |
| [CVE-2026-70907](https://www.cve.org/CVERecord?id=CVE-2026-70907)                                     | JSSE / TLS        | 5.3        | Medium   |
| [CVE-2026-60589](https://www.cve.org/CVERecord?id=CVE-2026-60589)                                     | Security          | 3.7        | Low      |
| [CVE-2026-62574](https://www.cve.org/CVERecord?id=CVE-2026-62574)                                     | Install (\*)      | 7.8        | High     |

(\*): Not applicable for Azul Zulu.

## What this changes for you

1. **Check exposure.** If your workloads touch 2D rendering, outbound HTTP, or TLS, treat the three higher-scoring CVEs as relevant and prioritize accordingly.
2. **Default to CPU for production.** The security-only line gives you the patch with the least change, so you reach production faster and with less to re-test.
3. **Adjust your automation, not your calendar.** Quarterly updates keep their usual dates. CSPUs land on the third Tuesday of the months in between. Azul schedules them monthly but releases them as and when a fix warrants one, so build your pipeline to pick up new builds rather than waiting for a fixed date. As an example, Oracle and Azul have not yet confirmed a CSPU for September 2026.

Azul also runs [monthly Java security briefings](https://www.azul.com/monthly-java-security-briefings/) walking through the CVEs in each update, now aligned with the CSPU cadence.

## Where to get it

Full details, checksums, and the complete list of fixes live in the [Core release notes](https://docs.azul.com/core/release-notes) and [Prime release notes](https://docs.azul.com/prime/release-notes#prime_stable_26_02_310_0). The [Azul downloads page](https://www.azul.com/downloads/) hosts builds for every supported version and platform. For the background on why Azul moved to monthly patches, read [Azul Will Deliver Monthly Java CSPUs, Increasing Patch Velocity with Stability](https://www.azul.com/blog/azul-will-deliver-monthly-java-critical-security-patch-updates-increasing-patch-velocity-with-stability/).

Monthly patching only helps if you act on it. A security-only build that reaches production in days instead of months is the whole point.
