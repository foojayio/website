---
title: "What's New in the May 2026 Payara Platform Release | Azul"
slug: "whats-new-in-the-may-2026-azul-payara-release"
date: "2026-05-14T11:40:02+00:00"
lastmod: "2026-05-14T17:01:44+00:00"
description: "The May 2026 Payara Platform release delivers Azul Payara Server 7 and Payara Micro 7 as the first commercially supported Jakarta EE 11 runtime certified across all three profiles, plus a critical security fix across every supported branch from Payara 4 through 7."
authors:
  - "luqman-saeed"
image: "Whats-New-in-the-Payara-Platform-April-2026-Release.png"
categories:
  - "Jakarta EE"
  - "Payara"
  - "Release Notes"
tags:
related_posts:
  - "whats-new-in-the-january-2026-payara-platform-release"
  - "goodbye-payara-community-6-on-to-the-next-chapter-with-payara-community-7"
  - "jakarta-data-makes-persistence-a-breeze"
  - "from-spring-boot-to-jakarta-ee-11-how-payara-starter-eases-the-transition"
frozen: false
---

The May 2026 release is the largest Payara milestone since the project's inception. Azul Payara Server 7 and Azul Payara Micro 7 ship as generally available, both certified against Jakarta EE 11. This is the first major Payara product release under the Azul brand, arriving six months after Azul completed its acquisition of Payara in December 2025.

Azul Payara Community 7 ([download here](https://payara.fish/downloads/payara-platform-community-edition/ "download here")), the open-source distribution, was the first implementation of any kind [to certify across all three Jakarta EE 11 profiles](https://foojay.io/today/leading-the-way-payara-platform-community-7-beta-now-fully-jakarta-ee-11-certified/ "to certify across all three Jakarta EE 11 profiles") (Full, Web Profile, Core Profile). Azul Payara Server 7 brings that certification to a commercially supported product with enterprise SLAs, making it the first commercially supported Jakarta EE 11 runtime from a major enterprise application server vendor. Both products ship with MicroProfile 6.1 (Config, Metrics, Health, Fault Tolerance, JWT, OpenAPI, REST Client, Telemetry Tracing). Azul Payara Server 7 holds Final TCK certification across all three profiles:

|   Profile    | Azul Payara Server 7 | Azul Payara Micro |
|--------------|----------------------|-------------------|
| Full         | Certified            | --                |
| Web Profile  | Certified            | Certified         |
| Core Profile | Certified            | Certified         |

No other major enterprise application server vendor holds Final certification across all three profiles at Jakarta EE 11. Oracle WebLogic 15.1.1 sits at Jakarta EE 9.1. IBM WebSphere tWAS is frozen at Java EE 7. Red Hat JBoss EAP ships Jakarta EE 10.

Existing Jakarta EE 10 applications deploy without code changes; the jakarta.\* namespace is stable between EE 10 and EE 11, so Azul Payara 6 applications move to Payara 7 by upgrading the runtime, not rewriting the codebase. JDK 21 is the minimum (Docker images ship for JDK 21 and JDK 25, the latest LTS). The same .war runs on both Server and Micro without modification. Jakarta Data, the headline API addition in Jakarta EE 11 introduces the @Repository annotation and a standardized data access layer.

This release also ships a critical security fix across every version: Azul Payara Community 7.2026.5, and Azul Payara 6.38.0, 5.87.0, and 4.1.2.191.55.

A critical security fix, patched across every supported branch
--------------------------------------------------------------

A critical security issue has been addressed across Azul Payara Community 7.2026.5 and Azul Payara 6.38.0, 5.87.0, and 4.1.2.191.55.

The fix lands in Azul Payara branches dating back to 4.1.2. Shipping security patches across the full supported lifecycle, not only the latest major release, is one of the practices that long-running Azul customers rely on; this release is a clear example. Azul is a registered CVE Numbering Authority (CNA) under CISA/DHS oversight, with patches backported to all supported versions on a published monthly schedule.

Azul Payara Community 7.2026.5
------------------------------

Community 7.2026.5 tracks the Payara 7 development line and ships additional fixes ahead of the Enterprise cadence.

#### Security Fixes

* Remote attacker can read arbitrary files via unsafe parsing of OpenMQ configuration
* Restrict access to vulnerable EL expressions

#### Bug Fixes

* Fix Admin Console freezing after upgrading from Payara 6 to 7

#### ImprovementsImprovements

* Update JaccProviderCompatibilityStartup Service
* Remove Audit Modules
* Add warlibs support to redeployment via Admin Console
* Reduce INFO logging for the Jakarta Data implementation
* Create new deployment descriptors with deprecated properties removed
* Fix Jakarta Data @Repository methods not throwing UnsupportedOperationException when no implementation logic can be injected at deploy time

#### Component Upgrades

Docker JDK images refreshed to 21.0.11 and 25.0.3. Dependency updates for Jakarta Faces, MicroProfile Config, Project Reactor, and other libraries.\*\*\*\*

Azul Payara 6.38.0: Continued Jakarta EE 10 Support
---------------------------------------------------

Azul Payara 6.38.0 continues the Jakarta EE 10 and MicroProfile 6.1 line for customers who are not yet on Payara 7.

#### Bug Fixes

* Fix HTTP 403 Forbidden response on correctly authenticated and authorized calls to protected JAX-RS resources
* Fix illegal reflective access by org.glassfish.pfl.basic.reflection.Bridge when starting Payara Server in Verbose mode

#### Improvements

* Deprecate Audit Modules
* Remove Yubikey Extension

#### Component Upgrades

Docker JDK images refreshed for JDK 21, 17, 11, and 8 (21.0.11, 17.0.19, 11.0.31, 8u492). Dependency updates for Mojarra and Project Reactor.

Azul Payara 5.87.0: Jakarta EE 8 Support Continues
--------------------------------------------------

Azul Payara 5.87.0 retains the javax.*namespace, Jakarta EE 8, and MicroProfile 4.1 platform for customers running long-lived applications that have not yet migrated to the jakarta.* namespace.

#### Bug Fixes

* Fix illegal reflective access by org.glassfish.pfl.basic.reflection.Bridge when starting Payara Server in Verbose mode
* Fix OIDC proxy support failing due to incorrect redirect URL comparison

#### Improvements

* Deprecate Audit Modules
* Remove Yubikey Extension

#### Component Upgrades

Docker JDK images refreshed for JDK 21, 17, 11, and 8 (21.0.11, 17.0.19, 11.0.31, 8u492).

Azul Payara 4.1.2.191.55: Legacy Branch Still Maintained
--------------------------------------------------------

Azul Payara 4.1.2.191.55 receives security updates and targeted bug fixes for customers still running on the Payara 4 branch.

#### Bug Fixes

* Fix Payara failing to start OpenMQ Broker in a separate JVM when using LOCAL mode on JDK 11 or later
* Fix unclosed streams warnings from OpenMQ

Looking Ahead
-------------

With Payara 7 GA, the Azul Payara product line now covers the full enterprise Java surface: the JDK (Azul Zulu, Core and Azul Prime), the full application server (Azul Payara Server), and the cloud-native runtime (Azul Payara Micro). All three ship under one Azul contract with monthly security patches, a long term lifecycle per major release, transparent per-vCore pricing, 24-48 hour bug fix SLAs, and 2-hour critical incident response with dedicated support engineers.

Azul Payara 6, 5, and 4 continue to receive monthly security and bug-fix releases on the published schedule. Migration assessments to Azul Payara 7 are available through your Azul account team for customers planning the move.

Upgrading and Feedback
----------------------

We recommend upgrading to your version's latest release in this cycle. A critical security patch is available across every supported branch, so there is no reason to delay the upgrade based on the major-version line you run.

For detailed upgrade instructions, [see the Payara documentation](https://docs.payara.fish/ "see the Payara documentation"). To report issues, contribute fixes, or follow the Payara 7 roadmap, visit the [Payara GitHub repository](https://github.com/payara/Payara "Payara GitHub repository"). For commercial support, your Azul account team.

Happy deploying!
