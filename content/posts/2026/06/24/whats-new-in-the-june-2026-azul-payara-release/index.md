---
title: "What's New In The June 2026 Azul Payara Release?"
date: "2026-06-24T11:28:52+00:00"
lastmod: "2026-06-24T14:38:27+00:00"
description: "The June 2026 Azul Payara release ships a critical security fix across every supported branch, Jakarta EE 11 GA libraries, and key updates for Payara 7, 6, 5, and 4."
authors:
  - "dominika-tasarz"
  - "luqman-saeed"
image: "Whats-New-in-the-Payara-Platform-April-2026-Release-2.png"
categories:
  - "Jakarta EE"
  - "Java"
  - "Microservices"
  - "Payara"
  - "Release Notes"
related_posts:
  - "build-secure-ai-chat-applications-with-boxlang-rag-ollama-and-amazon-bedrock-with-dan-card"
  - "idempotent-spring-boot-starter"
  - "http-query-method-explained-rfc-10008-ecosystem-adoption-and-a-quarkus-implementation"
  - "whats-new-in-the-july-2026-azul-payara-release"
frozen: false
---

The June 2026 cycle ships the first regular monthly release of the Payara 7 line, plus a critical security fix that lands across every supported branch. Azul Payara 7.1.0 continues the Jakarta EE 11 line that went generally available in May, and is joined by Azul Payara Community 7.2026.6, Azul Payara 6.39.0, Azul Payara 5.88.0, and Azul Payara 4.1.2.191.56.

A single security fix, backported from Payara 7 down to Payara 4, runs through every release in this cycle. Two supported lines, 5.88.0 and 6.39.0, also carry a behaviour change to the server "ready" signal that a minority of deployments will want to read before upgrading.

[Download Payara Here](https://payara.fish/downloads/)

## A Critical Security Fix, Patched Across Every Branch

A CSRF and SSRF issue in the Admin Console and REST management interface has been addressed in all five releases: Azul Payara Community 7.2026.6, and Azul Payara 7.1.0, 6.39.0, 5.88.0, and 4.1.2.191.55-era branches.

Shipping the patch across the full supported lifecycle, not only the latest major release, is the practice long-running Azul customers rely on. Azul is a registered CVE Numbering Authority (CNA) under CISA and DHS oversight, with patches backported to every supported version on a published monthly schedule. There is no reason to delay upgrading based on the major-version line you run.

## Azul Payara 7.1.0: Supported Payara 7, Post-GA

Azul Payara 7.1.0 is the first monthly supported release on the Payara 7 line since Server 7 and Micro 7 reached general availability. It holds Final Jakarta EE 11 certification and ships MicroProfile 6.1: Config, Metrics, Health, Fault Tolerance, JWT, OpenAPI, REST Client, and Telemetry Tracing.

Several Jakarta EE 11 components reached their GA milestones in this cycle, moving off beta and alpha builds:

• EclipseLink 5.0.0, up from the 5.0.0-B11 beta.  

• Jersey 4.0.2, the Jakarta EE 11 REST stack.  

• HK2 4.0.1.  

• Payara Security Connectors 4.0.0, up from 4.0.0.Alpha3.

Jakarta Data, the headline API addition of Jakarta EE 11, received two improvements worth noting for teams building repository-based data access. Method-level @Transactional overrides are now supported on repository interfaces, giving fine-grained control over transaction boundaries per method. A performance fix also stops the runtime parsing method names on every Jakarta Data HTTP request.

### Bug Fixes

• Fixed the MicroProfile /metrics endpoint returning duplicate metrics.  

• Fixed JSESSIONIDVERSION going missing, which left sessions unretrievable in relaxed mode.  

• Fixed a WELD-001408 unsatisfied dependency for repository injection in a CDI bean.  

• Fixed changing the admin password from the Admin Console not working.  

• Fixed a Payara Micro NullPointerException triggered by the log-to-console access log option.

### ImprovementsImprovements

• Reintroduced standalone OpenMQ support for Windows.  

• Added warlibs support to the remove-library and list-libraries commands.  

• Defined jakarta.security.jacc.policy.provider for Payara Micro.  

• Added an alternate Parsson provider to the BOM.  

• Added a warning when OpenMQ runs with default credentials.

### Component Upgrades

Beyond the GA milestones above, the 7 line moves Jackson BOM to 2.22.0, Kotlin stdlib to 2.4.0, Nimbus JOSE JWT to 10.9.1, ASM to 9.10.1, Woodstox to 7.2.0, and JAXB-Impl to 4.0.9, among others. The full list is in the release notes.

## Azul Payara Community 7.2026.6

Community 7.2026.6 tracks the Payara 7 development line and carries the same security fix, bug fixes, and component upgrades as Azul Payara 7.1.0. It adds one community-driven item of its own: updated SSH provider support, including SSH on Windows.

***Thanks to community contributor lprimak for the session retrieval fix (JSESSIONIDVERSION in relaxed mode) included in this release.***

## Behaviour Change to Note: Server "Ready" Signal Timing

Azul Payara 5.88.0 and 6.39.0 change the default value of the fish.payara.ready-after-applications system property. The undefined default moves from false to true, aligning Payara 5 and 6 with the behaviour already shipping in Payara 7. Users need do nothing; this is already their default -- both for Azul Payara and Azul Payara Community.

The property governs when the server marks itself as ready. Under the new default, the server signals ready once post-boot configuration has been applied and previously deployed applications have loaded. Under the previous default, the server signalled ready after the server_startup event, but before post-boot configuration and application loading.

Most deployments will see no difference. The change affects work that keys off the ready signal, most notably post-boot scripts and, in some configurations, transaction recovery timing. For Payara 5, this restores behaviour last seen prior to 5.47.0; for Payara 6, it is the first time the default has been true on the 6 line.

To keep the earlier behaviour, set the property explicitly:

*fish.payara.ready-after-applications=false*

The property is documented in the system property inventory under General Runtime Administration in the Payara Server documentation.

## Azul Payara 6.39.0: Jakarta EE 10, Continued

Azul Payara 6.39.0 continues the Jakarta EE 10 and MicroProfile 6.1 line for customers not yet on Payara 7. Alongside the cross-cycle security fix and the ready-signal change above, it carries the shared bug fixes for MicroProfile metrics, session retrieval, and the log-to-console NullPointerException.

### Component Upgrades

Kotlin stdlib moves to 2.3.21, Jackson BOM to 2.21.3, ASM to 9.10.1, JLine to 3.30.13, the StAX2 API to 4.3.0, and Grizzly NPN OSGi to 2.0.1.

## Azul Payara 5.88.0: Jakarta EE 8, Continued

Azul Payara 5.88.0 retains the javax.*namespace, Jakarta EE 8, and MicroProfile 4.1 for long-lived applications that have not migrated to the jakarta.* namespace. It picks up the security fix, the ready-signal behaviour change, and the OpenMQ default-credentials warning.

### Component Upgrades

Jackson BOM moves to 2.21.3, ASM to 9.10.1, JLine to 3.30.13, and SnakeYAML to 2.6.

## Azul Payara 4.1.2.191.56: Legacy Branch, Still Maintained

Azul Payara 4.1.2.191.56 receives the cross-cycle CSRF and SSRF security fix and upgrades a vulnerable Hazelcast version, alongside the OpenMQ default-credentials warning. Customers on the 4 branch without contracted Lifetime Support can still access existing binaries but receive no new releases.

## Looking Ahead

The Azul Payara product line now spans the JDK ([Azul Zulu and Azul Platform Prime](https://www.azul.com/products/core/)), the full application server ([Azul Payara Server](https://www.azul.com/products/payara-server/)), and the cloud-native runtime ([Azul Payara Micro](https://www.azul.com/products/payara-micro/)), all from one vendor. Payara 7, 6, 5, and 4 continue to receive monthly security and bug-fix releases on the published schedule, with patches backported across every supported version.

For teams on Payara 5 or 6, the ready-after-applications default change is the one item in this cycle worth a quick check against any post-boot scripts or transaction recovery configuration before upgrading.

## Upgrading and Feedback

We recommend upgrading to your version's latest release in this cycle. A critical security patch is available across every supported branch. For detailed upgrade instructions, s[ee the Payara documentation.](https://docs.payara.fish/ "ee the Payara documentation.")

To report issues, contribute fixes, or follow the Payara 7 roadmap, visit [the Payara GitHub repository](https://github.com/payara).

For commercial support, [contact your Azul account team.](https://www.azul.com/contact/ "contact your Azul account team.")

Happy deploying!
