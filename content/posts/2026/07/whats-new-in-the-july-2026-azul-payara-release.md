---
title: "What's New In The July 2026 Azul Payara Release?"
slug: "whats-new-in-the-july-2026-azul-payara-release"
date: "2026-07-23T10:28:58+00:00"
lastmod: "2026-07-23T10:28:59+00:00"
description: "Azul Payara's July 2026 release: Server and Micro 7.2.0, Jakarta EE 11, and a brute force security fix across every supported line."
authors:
  - "luqman-saeed"
image: "https://foojay.io/wp-content/uploads/2026/07/Whats-New-in-the-Payara-Platform-July-2026-Release.jpg"
categories:
  - "Jakarta EE"
  - "Payara"
  - "Release Notes"
tags:
related_posts:
  - "issues-with-old-glassfish-server-upgrade-to-eclipse-glassfish"
  - "get-recognized-for-your-cloud-native-java-development-skills-with-this-new-badge"
  - "creating-cloud-native-java-applications-with-the-12-factor-app-methodology"
  - "payara-cloud-hackathon-is-open-for-sign-ups"
frozen: false
---

The July 2026 release brings Azul Payara 7.2.0, Azul Payara Community 7.2026.7, Azul Payara 6.40.0, Azul Payara 5.89.0, and Azul Payara 4.1.2.191.57. A single security fix runs through every release, backported from the 7 line down to Payara 4: brute force attack prevention for authentication. Enterprise 6.40.0 additionally closes two Jackson CVEs that apply to the 6 line.

Downloads \& Release Notes {#h2-0-downloads-release-notes}
----------------------------------------------------------

* [Azul Payara Community (free version) - download](https://payara.fish/downloads/payara-platform-community-edition/)

<!-- -->

* [Azul Payara Community 7.2026.7 - release notes](https://docs.azul.com/payara/release-notes/release-notes-7.2.0.html)

<!-- -->

* [Azul Payara (supported) - free trial](https://www.azul.com/azul-payara-trial/)

<!-- -->

* [Azul Payara 7.2.0 - release notes](https://docs.azul.com/payara-community/release-notes/release-notes-7.2026.7.html)

A Security Fix, Patched Across Every Branch {#h2-1-a-security-fix-patched-across-every-branch}
----------------------------------------------------------------------------------------------

Brute force attack prevention has been added across Azul Payara Server and Micro 7.2.0, 6.40.0, 5.89.0, and 4.1.2.191.57, and Azul Payara Community 7.2026.7. The fix protects authentication against repeated credential guessing.

This change originated in Eclipse GlassFish (eclipse-ee4j/glassfish#26026) and was ported back into the Payara codebase. Payara and GlassFish share a common ancestry, and upstream fixes like this continue to flow between the two projects.

Shipping the patch across the full supported lifecycle, not only the latest major release, is the practice long-running Azul customers rely on. Azul is a registered CVE Numbering Authority (CNA) under CISA and DHS oversight, with patches backported to every supported version on a published monthly schedule. There is no reason to delay upgrading based on the major-version line you run.

Two Jackson CVEs Closed on the 6 Line {#h2-2-two-jackson-cves-closed-on-the-6-line}
-----------------------------------------------------------------------------------

Azul Payara Server and Micro 6.40.0 also addresses CVE-2026-54512 and CVE-2026-54513, a Jackson `PolymorphicTypeValidator` bypass. The fix lands alongside the Jackson BOM upgrade on the 6 line. The 7 line is not affected.

Azul Payara Server and Micro 7.2.0: The Payara 7 Line, Monthly Cadence {#h2-3-azul-payara-server-and-micro-7-2-0-the-payara-7-line-monthly-cadence}
---------------------------------------------------------------------------------------------------------------------------------------------------

Azul Payara Server and Micro 7.2.0 is the second regular monthly release on the Payara 7 line since Server 7 and Micro 7 reached general availability in May. Server 7.2.0 is Jakarta EE 11 certified across Full Platform, Web Profile, and Core Profile; Micro 7.2.0 implements the Web Profile and Core Profile APIs. Both ship MicroProfile 6.1: Config, Metrics, Health, Fault Tolerance, JWT, OpenAPI, REST Client, and Telemetry Tracing.

The 7 line picks up Grizzly 5.0.2 this cycle, the Jakarta EE 11 era transport. Two improvements carry over to both Server and Micro: backwards compatibility for defining implicit CDI via` glassfish-application.xml, `which eases migrations that relied on the legacy descriptor, and Payara major-versioned deployment descriptors, which let an application pin itself to a Payara major version.

### Bug Fixes {#h3-4-bug-fixes}

• Fixed the IBM MQ resource adapter failing to deploy when a custom metrics.xml file is used.  

• Fixed `restart-deployment-group` not showing progress while running.  

• Fixed attributes removed from `org.glassfish.grizzly.config.dom.Http lingering in httpAttr.inc.`

### Component Upgrades {#h3-5-component-upgrades}

Beyond Grizzly 5.0.2, the 7 line moves Woodstox to 7.2.1, the Jakarta MVC API to 3.0.1, JNA to 5.19.1, Reactor Core to 3.8.6, SmallRye Common to 2.19.0, JLine to 3.30.15, SmallRye Config to 3.18.0, and the Jackson BOM to 2.22.1. The full list is in the release notes.

Azul Payara Community 7.2026.7 {#h2-6-azul-payara-community-7-2026-7}
---------------------------------------------------------------------

Azul Payara Community 7.2026.7 is the open-source distribution that tracks the Payara 7 development line. It carries the same security fix, bug fixes, and component upgrades as Server and Micro 7.2.0, plus three community-track items of its own.

SSH nodes no longer require elevated permissions. Combined with the Apache SSHD upgrade to 2.18.0, this simplifies cluster node setup and removes a common operational hurdle on hardened hosts. EclipseLink MoXY has been removed, continuing the streamlining of the XML binding stack that began earlier this cycle. SLF4J NOP instantiation errors have also been resolved.

Thanks to community contributor [Larry Primak](https://github.com/lprimak) for the JVM thread stats provider fix included in this release, which prevents stack traces from being cached.

Azul Payara Server and Micro 6.40.0: Jakarta EE 10, Continued {#h2-7-azul-payara-server-and-micro-6-40-0-jakarta-ee-10-continued}
---------------------------------------------------------------------------------------------------------------------------------

Azul Payara Server and Micro 6.40.0 continue the Jakarta EE 10 and MicroProfile 6.1 line for customers not yet on Payara 7. Alongside the cross-cycle brute force fix and the two Jackson CVEs above, this release carries the shared fixes for the IBM MQ resource adapter, the deployment group restart progress, and the JVM thread stats provider stack trace caching.

### Component Upgrades {#h3-8-component-upgrades}

Eclipse Persistence ASM moves to 9.10.0, the JAXB Codemodel to 4.0.9, the Jackson BOM to 2.22.0 (carrying the CVE fix), Kotlin stdlib to 2.4.0, Nimbus JOSE JWT to 10.9.1, Apache Ant to 1.10.17, and Mimepull to 1.11.0.

Azul Payara Server and Micro 5.89.0: Jakarta EE 8, Continued {#h2-9-azul-payara-server-and-micro-5-89-0-jakarta-ee-8-continued}
-------------------------------------------------------------------------------------------------------------------------------

Azul Payara Server and Micro 5.89.0 retain the` javax.*` namespace, Jakarta EE 8, and MicroProfile 4.1 for long-lived applications that have not migrated to the` jakarta.*` namespace. This release picks up the brute force security fix and the shared IBM MQ, deployment group, and stack trace caching fixes.

### Component Upgrades {#h3-10-component-upgrades}

Apache Ant moves to 1.10.17, Apache BCEL to 6.12.0, and Nimbus JOSE JWT to 10.9.1. The `istack-commons-runtime` has been reverted from 4.2.0 to 3.0.12, as 4.2.0 is incompatible with Java 8, which remains a supported target on the 5 line.

Azul Payara Server and Micro 4.1.2.191.57: Legacy Branch, Still Maintained {#h2-11-azul-payara-server-and-micro-4-1-2-191-57-legacy-branch-still-maintained}
------------------------------------------------------------------------------------------------------------------------------------------------------------

Azul Payara Server and Micro 4.1.2.191.57 receive the cross-cycle brute force security fix. Customers on the 4 branch without contracted Lifetime Support can still access existing binaries but receive no new releases beyond security patches.

Looking Ahead {#h2-12-looking-ahead}
------------------------------------

The Azul Payara product line now spans the JDK (Azul Zulu and Azul Platform Prime), the full application server (Azul Payara Server), and the cloud-native runtime (Azul Payara Micro), all from one vendor. The 7, 6, 5, and 4 lines continue to receive monthly security and bug-fix releases on the published schedule, with patches backported across every supported version. For teams on the 5 or 6 line evaluating the move to Payara 7, the `jakarta.*` namespace is stable between EE 10 and EE 11, so existing Jakarta EE 10 applications deploy on Payara 7 by upgrading the runtime, not rewriting the codebase. Migration assessments are available through your Azul account team.

Upgrading and Feedback {#h2-13-upgrading-and-feedback}
------------------------------------------------------

We recommend upgrading to the latest release for your line. A security patch is available across every supported branch, and Enterprise 6.40.0 closes two additional Jackson CVEs on the 6 line.

For detailed upgrade instructions, see the Payara documentation. To report issues, contribute fixes, or follow the Payara 7 roadmap, visit the Payara GitHub repository. For commercial support, contact your Azul account team.

Happy deploying.
