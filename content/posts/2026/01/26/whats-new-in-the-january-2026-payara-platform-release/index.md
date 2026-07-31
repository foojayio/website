---
title: "What’s New in the January 2026 Payara Platform Release?"
slug: "whats-new-in-the-january-2026-payara-platform-release"
date: "2026-01-26T14:37:09+00:00"
description: "As we begin 2026, we’re pleased to announce new releases across all Payara Platform editions this January: Payara Platform Community 7.2026.1, Payara - by Dominika Tasarz"
authors:
  - "dominika-tasarz"
  - "luqman-saeed"
image: "Payara-New-Release-Image-Square.png"
categories:
  - "Jakarta EE"
  - "Java"
  - "Payara"
tags:
related_posts:
  - "goodbye-payara-community-6-on-to-the-next-chapter-with-payara-community-7"
  - "leading-the-way-payara-platform-community-7-beta-now-fully-jakarta-ee-11-certified"
  - "whats-new-in-the-may-2026-azul-payara-release"
  - "jakarta-data-makes-persistence-a-breeze"
frozen: false
---

As we begin 2026, we're pleased to announce new releases across all Payara Platform editions this January: [Payara Platform Community 7.2026.1](https://payara.fish/downloads/payara-platform-community-edition/ "Payara Platform Community 7.2026.1"), [Payara Platform Enterprise 6.34.0 and 5.83.0](https://payara.fish/payara-enterprise-downloads/ "Payara Platform Enterprise 6.34.0 and 5.83.0"). These releases deliver important security fixes, address deployment and administration issues as well as refreshing multiple component versions across the platform.

Security Priority {#h2-0-security-priority}
-------------------------------------------

This month's releases address two security vulnerabilities. First, we've resolved CVE-2020-5258, a cross-site scripting vulnerability in the dojo.js library (versions 6.0.2 and earlier). This fix applies to Payara Platform Community 7.2026.1 and Payara Platform Enterprise 6.34.0, with dojo.js now upgraded to version 6.0.3.

Second, and more critically, we've closed a vulnerability that could allow admin account takeover through malicious URL payloads. This issue affected all editions and has been resolved across Payara Platform Community 7.2026.1, Payara Platform Enterprise 6.34.0 and Payara Platform Enterprise 5.83.0. We strongly recommend upgrading promptly if your Payara instances are exposed to untrusted networks.

Payara Platform Community Edition 7.2026.1 {#h2-1-payara-platform-community-edition-7-2026-1}
---------------------------------------------------------------------------------------------

The Payara Platform Community edition continues to lead with full Jakarta EE 11 and MicroProfile 6.1 support, keeping you current with the latest enterprise Java specifications.

This release tackles several deployment pain points. If you've encountered EJB deployment failures on JDK 24, that's now resolved. We also fixed a class loader leak that occurred when EJB deployments failed, preventing memory from being properly released. Another EJB-related fix resolves duplicate class definition errors for GenericEJBHome_Generated_DynamicStub during redeployment, a problem that could cause unexpected behavior in long-running applications.

On the component front, we've upgraded numerous dependencies. Parsson moves to 1.1.7, the CORBA implementation reaches version 5.0.0, and we've brought in gmbal 4.1.0 and PFL 5.1.0. Reactive users will appreciate reactor-core 3.8.1, and Kotlin developers get version 2.3.0 of kotlin-stdlib. Other notable updates include commons-io 2.21.0, ASM 9.9.1, angus-activation 2.0.3, and nimbus-jose-jwt 10.6.

Payara Platform Enterprise Edition 6.34.0 {#h2-2-payara-platform-enterprise-edition-6-34-0}
-------------------------------------------------------------------------------------------

Payara Platform Enterprise 6.34.0 maintains Jakarta EE 10 and MicroProfile 6.1 compatibility while incorporating this month's security fixes and addressing several administration issues.

If you've experienced HTTP 500 errors when sorting columns by keystore or truststore alias names in the admin console, that's now fixed. The EJB redeployment issue affecting Community users has also been addressed here. Component upgrades in this release include Parsson 1.1.7 and gmbal 4.1.0, keeping the Enterprise 6 line current with upstream dependency improvements.

The Payara Upgrade Tool {#h2-3-the-payara-upgrade-tool}
-------------------------------------------------------

Cluster operators will welcome a new addition to the Payara Upgrade Tool: the `--nodes` flag. This option enables selective node upgrades when you need finer control over rolling upgrades. Instead of upgrading every node in your cluster simultaneously, you can now specify a comma-separated list of node names to upgrade incrementally. The same capability extends to rollbacks, letting you revert individual nodes if an upgrade encounters issues. This supports safer, more controlled upgrade patterns for production clusters where you want to validate each node before proceeding to the next.

Payara Platform Enterprise Edition 5.83.0 {#h2-4-payara-platform-enterprise-edition-5-83-0}
-------------------------------------------------------------------------------------------

For organizations still running Jakarta EE 8 workloads, Payara Platform Enterprise 5.83.0 maintains our commitment to long-term stability, with MicroProfile 4.1 support. This release includes the critical admin account takeover security fix and resolves the keystore/truststore sorting issue that was causing internal server errors in the administration interface. The Jackson BOM has been updated from 2.20.0 to 2.20.1 as well.

Release Notes {#h2-5-release-notes}
-----------------------------------

* [Payara Platform Community 7.2026.1](https://docs.payara.fish/community/docs/Release%20Notes/Release%20Notes%207.2026.1.html "Payara Platform Community 7.2026.1")
* [Payara Platform Enterprise 6.34.0](https://docs.payara.fish/enterprise/docs/Release%20Notes/Overview.html "Payara Platform Enterprise 6.34.0")
* [Payara Platform Enterprise 5.83.0](https://docs.payara.fish/enterprise/docs/5.83.0/Release%20Notes/Overview.html "Payara Platform Enterprise 5.83.0")

Not using Payara Platform yet? [Download Payara Platform Community](https://payara.fish/downloads/payara-platform-community-edition/ "Download Payara Platform Community") or request [a free trial of Payara Platform Enterprise](https://payara.fish/free-trials/ "a free trial of Payara Platform Enterprise") to experience these improvements in your development and production environments.

We value your feedback and contributions to the Payara Platform. Report issues or share your experiences on our [GitHub repository.](https://github.com/payara/Payara "GitHub repository.")

Happy deployments!
