---
title: "What's New In The November 2026 Azul Payara Release?"
date: "2026-11-16"
description: "Payara Community 7.2026.11 and Azul Payara patch RCE in JMX and an admin auth bypass, and remove the vulnerable eMudhra CAs from the trust store."
authors:
  - "dominika-tasarz"
image: "Payara-Community-Nov-2026-Release.jpg"
categories:
  - "Jakarta EE"
  - "Payara"
---

The November 2026 release brings Payara Community 7.2026.11, as well as Azul Payara Server & Micro 7.6.0, 6.44.0, 5.93.0 & 4.1.2.191.61.

This month, security is the headline for Payara Community as well as Azul Payara. Payara Community 7.2026.11 ships five security fixes, including remote code execution in JMX, a remote admin authentication bypass and the removal of vulnerable certificate authorities from the Payara trust store after the eMudhra compromise. It also fixes a context capture problem with managed executors, resolves two EclipseLink problems, a multithreading issue and a deadlock, and trims what ships in the distribution.

For Azul Payara, the same security fixes ship across the 7, 6, 5, and 4 branches, and the 6, 5 and 4 lines pick up a sixth: a Hazelcast backport for two CVEs. The release notes list no breaking changes this cycle, and Jakarta EE 11 and MicroProfile 7.1 remain the baseline on the 7 line.

## **Branches At A Glance**

| **Release**                               | **Jakarta EE** | **MicroProfile** | **Notable this cycle**                                                                       |
| ----------------------------------------- | -------------- | ---------------- | -------------------------------------------------------------------------------------------- |
| Payara Community 7.2026.11                | 11             | 7.1              | Five security fixes, trust store cleaned, managed executor fix, EclipseLink fixes, leaner distribution |
| Azul Payara Server and Micro 7.6.0        | 11             | 7.1              | Five security fixes, trust store cleaned, EclipseLink fixes, Mojarra 4.1.18                  |
| Azul Payara Server and Micro 6.44.0       | 10             | 7.1              | Six security fixes, trust store cleaned, EclipseLink fixes, Mojarra 4.0.27                   |
| Azul Payara Server and Micro 5.93.0       | 8              | 4.1              | Six security fixes, trust store cleaned, EclipseLink fixes, jakarta.mail 1.6.8               |
| Azul Payara Server and Micro 4.1.2.191.61 | —              | —                | Six security fixes, trust store cleaned, OpenMQ Grizzly port                                 |

## **Security Fixes Patched Across Every Branch**

Five security items ship in Payara Community 7.2026.11 and Azul Payara Server and Micro 7.6.0, 6.44.0, 5.93.0, and 4.1.2.191.61:

  - **Remote code execution in JMX** (CVE-2026-90908).

  - **Remote admin authentication bypass** via an unvalidated auth token header (CVE-2026-110003).

  - **CVE-2026-110879**, with Jackson updated to 2.22.4 / 2.21.8 / 2.18.12.

  - **Vulnerable certificate authorities (CAs) removed from the Payara trust store**, following the eMudhra compromise (CVE-2026-91403 and CVE-2026-114820, more below).

  - **Hazelcast shipping a shaded copy of `jackson-core` with a known vulnerability** (GHSA-r7wm-3cxj-wff9).

Azul Payara 6.44.0, 5.93.0 and 4.1.2.191.61 carry a sixth: **CVE-2026-107725 and CVE-2026-107726**, fixed by backporting the Hazelcast fixes (to Hazelcast 5.3.8 on the 6 and 5 lines, and 3.12.13 on Payara 4). Azul Payara 7 and Payara Community are not affected by these two CVEs.

The [Azul Payara Security Advisories](https://docs.azul.com/payara/security/security-fix-list.html) page documents the JMX, admin authentication and eMudhra CVEs in detail.

## **The eMudhra Trust Store Fix & What It Does Not Do**

In October 2026, the private keys of [eight eMudhra intermediate CA certificates were compromised.](https://foojay.io/today/foojay-podcast-104/) Anyone holding those keys can forge a certificate for any domain. Browsers and operating systems already block these certificates, but Java does not check certificate revocation by default, so Java applications keep trusting them until the trust store is fixed. Two critical CVEs cover the problem: CVE-2026-91403 and CVE-2026-114820.

Every release this month removes the four vulnerable certificates from Payara's default trust store, `${domainDir}/config/cacerts.p12`. The change only applies to new installations and to domains created with the fixed releases. An in-place upgrade does not normally update the trust store, so if you upgraded an existing domain, the certificates are still in it and you must remove them manually. You also need a JDK that contains the fix, because the Java runtime's own trust store is affected as well.

The [Azul Payara Security Advisories](https://docs.azul.com/payara/security/security-fix-list.html) page lists the SHA-256 fingerprints of the four certificates to remove: AAA Certificate Services, emSign ECC Root CA - G3, emSign Root CA - G1, and emSign Root CA - G2. Check every domain you have upgraded in place, not only the newest one.

## **What's New In Payara Community 7.2026.11**

![Azul Payara Community November 2026 release banner](Email-Header-Payara-Community-November-2026.jpg)

Payara Community 7.2026.11 ([download here](https://www.azul.com/downloads/azul-payara-community-edition)) is the open source distribution that tracks the Payara 7 development line, shipping Jakarta EE 11 and MicroProfile 7.1.

This month's release is about security and stability: the fixes above, plus a fix for managed executors and two EclipseLink fixes that matter for applications running on multiple threads.

### **Fixes Worth Knowing About**

  - **Fixed context capture for `ManagedExecutorService` and `ManagedScheduledExecutorService`.** It threw an `IllegalStateException` on threads with no component invocation.

  - **Fixed a multithreading issue in Payara Server and EclipseLink.**

  - **Fixed a deadlock in the EclipseLink second-level cache** during parallel report generation.

### **A Leaner Distribution**

Payara no longer bundles the OpenMQ examples and Javadoc, or the H2 database source files and help docs. The Payara Docker images also move to updated JDK versions and a previous fix (FISH-9690) is ported to the OpenMQ Grizzly library.

### **Component Upgrades**

Mojarra moves to **4.1.18** and the Jackson BOM to **2.22.4**. Elsewhere: WASP 4.0.4, Epicyro 3.1.2, Jakarta JSON Binding API 3.0.4, MicroProfile Config API 3.1.3, OpenTelemetry instrumentation annotations 2.32.0, Project Reactor 3.8.8, ASM 9.11.1, JLine 4.4.8, classmate 1.8.0, stax2-api 4.3.2, and Kotlin stdlib 2.4.21.

Every fix in the Community release is listed [in the release notes](https://docs.azul.com/payara-community/release-notes/release-notes-7.2026.11.html).

## **What's New In Azul Payara Server and Micro**

![Azul Payara November 2026 release banner](Email-Header-Azul-Payara-November-2026.jpg)

All four Azul Payara branches carry the security fixes mentioned above. This section covers what else changes on each branch.

### **Azul Payara 7.6.0**

Azul Payara 7.6.0 ([request free trial here](https://www.azul.com/azul-payara-trial/)) is the sixth regular monthly release on the Payara 7 line since Server 7 and Micro 7 reached general availability in May. Server 7.6.0 is Jakarta EE 11 certified across Full Platform, Web Profile and Core Profile; Micro 7.6.0 implements the Web Profile and Core Profile APIs.

### **Bug Fixes**

  - Fixed context capture for managed executors throwing an `IllegalStateException` on threads with no component invocation.

  - Fixed the `start-domain` admin-password recovery prompt, which broke because auth-realm-name was dropped from domain.xml when the file was rewritten.

  - Fixed the EclipseLink multithreading issue and the second-level cache deadlock during parallel report generation.

### **Distribution**

The OpenMQ examples and Javadoc and the H2 database source files and help docs are no longer bundled. The Docker images move to updated JDK versions.

### **Component Upgrades**

Mojarra 4.1.18, Jackson BOM 2.22.4, WASP 4.0.4, Epicyro 3.1.2, Jakarta JSON Binding API 3.0.4, MicroProfile Config API 3.1.3, OpenTelemetry instrumentation annotations 2.32.0, Project Reactor 3.8.8, ASM 9.11.1, JLine 4.4.8, classmate 1.8.0, stax2-api 4.3.2, and Kotlin stdlib 2.4.21.

The full list is [in the release notes](https://docs.azul.com/payara/release-notes/release-notes-7.6.0.html).

### **Azul Payara 6.44.0**

Azul Payara 6.44.0 continues the Jakarta EE 10 line for customers not yet on Payara 7, on MicroProfile 7.1. Alongside the security fixes, including the Hazelcast backport, it picks up the managed executor fix and the two EclipseLink fixes, and it no longer bundles the OpenMQ and H2 extras.

**Component upgrades:** Mojarra 4.0.27, Jackson BOM 2.22.4, OpenTelemetry instrumentation annotations 2.32.0, ASM 9.11.1, JLine 4.4.8, classmate 1.8.0, stax2-api 4.3.2, and Kotlin stdlib 2.4.21.

### **Azul Payara 5.93.0**

Azul Payara 5.93.0 retains the javax.\* namespace, Jakarta EE 8, and MicroProfile 4.1 for long-lived applications that have not migrated to the jakarta.\* namespace. It picks up the security fixes, including the Hazelcast backport, and the two EclipseLink fixes, and it no longer bundles the OpenMQ and H2 extras.

**Component upgrades:** jakarta.mail 1.6.8, Jackson BOM 2.22.4, ASM 9.11.1, classmate 1.8.0, and stax2-api 4.3.2.

### **Azul Payara 4.1.2.191.61**

Azul Payara 4.1.2.191.61 receives the six security fixes, including the Hazelcast backport, along with the OpenMQ Grizzly library port and updated JDK versions in the Docker images.

## **Looking Ahead**

The Azul Payara product line spans the JDK (Azul Zulu and Azul Prime), the full application server (Azul Payara Server) and the cloud-native runtime (Azul Payara Micro), all from one vendor. The 7, 6, 5, and 4 lines continue to receive monthly security and bug-fix releases on the published schedule, with patches backported across every supported version.

If you are evaluating a move to Payara 7, the jakarta.\* namespace is stable between Jakarta EE 10 and 11, so existing Jakarta EE 10 applications deploy on Payara 7 by upgrading the runtime rather than rewriting the codebase. Migration assessments are available through your Azul account team.

## **Upgrading And Feedback**

We recommend upgrading to the latest release for your line, and strongly so given that security patches are available across every supported branch, from the 7 line down to Payara 4. If you upgrade an existing domain in place, remove the vulnerable certificates from its trust store as described above.

Full details, including the complete component upgrade lists, are in the release notes:

  - [Payara Community 7.2026.11](https://docs.azul.com/payara-community/release-notes/release-notes-7.2026.11.html)

  - [Azul Payara Server and Micro 7.6.0](https://docs.azul.com/payara/release-notes/release-notes-7.6.0.html)

  - [Azul Payara Server and Micro 6.44.0](https://docs.azul.com/payara/version/6/release-notes/release-notes-6.44.0.html)

  - [Azul Payara Server and Micro 5.93.0](https://docs.azul.com/payara/version/5/release-notes/release-notes-5.93.0.html)

  - [Azul Payara Server and Micro 4.1.2.191.61](https://docs.azul.com/payara/version/4/release-notes/release-notes-4.1.2.191.61.html)

For upgrade instructions, [see the Payara documentation](https://docs.azul.com/payara/technical-documentation/payara-server-documentation/upgrade-guide/); to report issues, contribute fixes, or follow the Payara 7 roadmap, visit the [Payara GitHub repository](https://github.com/payara/Payara), and for commercial support, contact your [Azul account team.](https://www.azul.com/contact/)

Happy deploying!
