---
title: "What's New in the August 2026 Azul Payara Release?"
date: "2026-08-24T15:36:28+00:00"
lastmod: "2026-08-25T19:27:35+00:00"
description: "The August 2026 Payara release previews Jakarta Agentic AI, adds MicroProfile 7.1 and gRPC and patches a security advisory."
authors:
  - "dominika-tasarz"
  - "luqman-saeed"
image: "Azul-Payara-Community-Release.jpg"
categories:
  - "AI"
  - "Jakarta EE"
  - "Payara"
related_posts:
  - "shaping-jakarta-agentic-ai-together-watch-the-open-conversation"
  - "bring-ai-into-your-jakarta-ee-apps-with-langchain4j-cdi"
  - "get-recognized-for-your-cloud-native-java-development-skills-with-this-new-badge"
  - "creating-cloud-native-java-applications-with-the-12-factor-app-methodology"
frozen: false
---

The August 2026 release brings Azul Payara Server and Micro 7.3.0, Azul Payara Community 7.2026.8, Azul Payara Server and Micro 6.41.0, Azul Payara Server and Micro 5.90.0, and Azul Payara Server and Micro 4.1.2.191.58.

The headline is in the Community edition. Azul Payara Community 7.2026.8 ships support for MicroProfile 7.1 and a first preview of Jakarta Agentic AI, the new vendor-neutral specification for building AI agents on Jakarta EE runtimes. For the Enterprise lines, this cycle adds gRPC support across the 7, 6, and 5 releases, and carries a security advisory patched across the 6, 5, and 4 lines, with two Jackson CVEs additionally closed on the 5 and 4 lines.

## Downloads \& Release Notes

* [Azul Payara Community (free version) - download](https://payara.fish/downloads/payara-platform-community-edition/)
* [Azul Payara Community 7.2026.8 - release notes](https://docs.azul.com/payara-community/release-notes/release-notes-7.2026.8.html)
* [Azul Payara (supported) - free trial](https://www.azul.com/azul-payara-trial/)
* [Azul Payara 7.3.0 - release notes](https://docs.azul.com/payara/release-notes/release-notes-7.3.0.html)

## The Spotlight: A First Preview of Jakarta Agentic AI

Azul Payara Community 7.2026.8 integrates [the Jakarta Agentic AI 1.0 API](https://foojay.io/today/jakarta-agentic-ai-hits-its-first-milestone/) into the Payara 7 runtime. This is a first preview. The specification is a standalone release, published under the Jakarta EE process; the current draft is Milestone 1 (1.0.0-M1), out for early feedback, so treat this as your chance to build against the API inside a Jakarta EE 11 runtime and shape it before it is finalized.

What does the specification define? A vendor-neutral API to build, deploy, and run AI agents on Jakarta EE runtimes. The programming model will feel natural to any Jakarta EE developer, because an agent is just a CDI bean:

* @Agent marks the class as an agent, with workflow scope by default

<!-- -->

* A LargeLanguageModel is injected like any other bean, through a lightweight LLM facade with automatic type conversion for parameters and return types

<!-- -->

* @Trigger starts the workflow, fired by CDI events in this initial release

<!-- -->

* @Decision, @Action, and @Outcome model the steps of the workflow, with domain objects passed through the workflow context

```java
@Agent
public class FraudDetectionAgent {
    @Inject
    private LargeLanguageModel model;
    @Inject
    private EntityManager entityManager;

    @Trigger
    private void handleTransaction(@Valid BankTransaction transaction) { /* ... */ }

    @Decision
    private Result checkFraud(BankTransaction transaction) {
        String output = model.query(
                "Is this a fraudulent transaction? If so, how serious is it?", transaction);
        /* ... */
    }

    @Action
    private void handleFraud(Fraud fraud, BankTransaction transaction) { /* ... */ }

    @Outcome
    private void markTransaction(BankTransaction transaction) { /* ... */ }
}
```

Because agents live in the CDI container, the rest of your application works with them unchanged: EntityManager injection, Jakarta Persistence queries, transactions, events. There is no separate agent runtime to operate alongside your server, and no framework lock-in, because the API is vendor-neutral and portable across Jakarta EE runtimes. Instead of wiring a framework into your deployment pipeline, you write agents against a Jakarta specification, the same way you write REST endpoints against Jakarta RESTful Web Services or data access against Jakarta Data.

The preview ships in Community first. The specification is still iterating, the API may change, and your feedback on the [Payara GitHub repository](https://github.com/payara/Payara) and the specification's own channels will decide how quickly this matures into the Enterprise line. Start experimenting now and tell us what breaks.

## Azul Payara Community 7.2026.8 - MicroProfile 7.1

Alongside the Agentic AI preview, Community 7.2026.8 upgrades MicroProfile support from 6.1 to 7.1, with the MicroProfile OpenAPI API moving to 4.1.1.

Observability work accompanies the upgrade:

* OpenTelemetry instrumentation annotations jump from 2.14.0 to 2.30.0, semantic conventions move to 1.42.0, and the OpenTelemetry SDK moves to 1.64.0

<!-- -->

* OpenTelemetry no longer initializes when it has not been explicitly enabled

<!-- -->

* MicroProfile Metrics and OpenTelemetry metrics are now recorded separately in FaultToleranceMetricsRecorder, so the two metrics systems no longer interfere with each other

Two access log improvements land across the whole cycle: cleaner formatting when access logs print to the console from Micro, and regex filtering for access log URIs.

Bug fixes include the incorrect persistence type in the HA internal configuration of JAX-WS security and the return of the OSGi shell, which restores the GoGo shell commands.

Component upgrades: Apache SSHD 2.19.0, Kotlin stdlib 2.4.10, JLine 3.30.16, Woodstox 7.2.2. Docker images refresh to JDK 25.0.4 and 21.0.12.

## A Security Advisory Patched Across the 6, 5 \& 4 Lines

[GHSA-r7wm-3cxj-wff9](https://www.kodemsecurity.com/cve-archive/ghsa-r7wm-3cxj-wff9) is addressed in Azul Payara 6.41.0, 5.90.0, and 4.1.2.191.58. The 5 and 4 lines close CVE-2026-54512 and CVE-2026-54513, a Jackson PolymorphicTypeValidator bypass already fixed on the 6 line in July; the 7 line was never affected.

Shipping the patch across the full supported lifecycle, not only the latest major release, is the practice long-running Azul customers rely on. Azul is a registered CVE Numbering Authority (CNA) under CISA and DHS oversight, with patches backported to every supported version on a published monthly schedule. There is no reason to delay upgrading based on the major-version line you run.

## gRPC Support Arrives Across the Enterprise Lines

The largest Enterprise feature of this cycle lands in all three Enterprise releases: gRPC support across the 7, 6, and 5 lines, with the gRPC runtime at 1.83.1 on every line. For the 5 line, that is a move from 1.56.1. gRPC is the transport of choice for high-throughput service-to-service calls, including the inference and serving infrastructure that AI backends depend on, and it is now available from the oldest supported Enterprise line to the newest.

## Azul Payara Server and Micro 7.3.0: The Payara 7 Line, Monthly Cadence

Azul Payara Server and Micro 7.3.0 is the third regular monthly release on the Payara 7 line since Server 7 and Micro 7 reached general availability in May. Server 7.3.0 is Jakarta EE 11 certified across Full Platform, Web Profile, and Core Profile; Micro 7.3.0 implements the Web Profile and Core Profile APIs. Both ship MicroProfile 6.1: Config, Metrics, Health, Fault Tolerance, JWT, OpenAPI, REST Client, and Telemetry Tracing.

This release also carries the shared access log improvements and these fixes:

* Fixed the incorrect persistence type in the HA internal configuration of JAX-WS security

<!-- -->

* Fixed the OSGi shell being unavailable, which broke GoGo shell commands

<!-- -->

* Fixed a production domain not using the new payara-acc descriptor

<!-- -->

* Fixed a production domain referencing removed audit models

Component upgrades: gRPC 1.83.1, Kotlin stdlib 2.4.10, JLine 3.30.16, Woodstox 7.2.2. Docker images refresh to JDK 25.0.4 and 21.0.12.

## Azul Payara Server and Micro 6.41.0: Jakarta EE 10, Continued

Azul Payara Server and Micro 6.41.0 continue the Jakarta EE 10 and MicroProfile 6.1 line for customers not yet on Payara 7. Alongside the GHSA-r7wm-3cxj-wff9 fix, this release carries the shared access log improvements and the fixes for the HA persistence type in JAX-WS security, the GoGo shell commands, and the production domain payara-acc descriptor.

Component upgrades: Jackson BOM 2.22.1, Reactor Core 3.8.6, JNA 5.19.1, Felix SCR 2.2.18, Kotlin stdlib 2.4.10, gRPC 1.83.1. Docker images refresh to JDK 21.0.12, 17.0.20, and 11.0.32.

## Azul Payara Server and Micro 5.90.0: Jakarta EE 8, Continued

Azul Payara Server and Micro 5.90.0 retain the javax.\* namespace, Jakarta EE 8, and MicroProfile 4.1 for long-lived applications that have not migrated to the jakarta.\* namespace. This release picks up both security items, the GHSA advisory and the two Jackson CVEs, along with the shared access log improvements.

Component upgrades: Reactor Core 3.8.6, JNA 5.19.1, Felix SCR 2.2.18, JLine 3.30.16, Payara Arquillian 2.5, and the gRPC runtime moving from 1.56.1 to 1.83.1. Docker images refresh to JDK 21.0.12, 17.0.20, 11.0.32, and 8u502.

## Azul Payara Server and Micro 4.1.2.191.58: Legacy Branch, Still Maintained

Azul Payara Server and Micro 4.1.2.191.58 receive the GHSA-r7wm-3cxj-wff9 advisory fix and the two Jackson CVEs, plus the Docker JDK 8u502 image refresh. Customers on the 4 branch without contracted Lifetime Support can still access existing binaries but receive no new releases beyond security patches.

## Looking Ahead

The Agentic AI preview is where your feedback counts most this cycle. Try it in [Azul Payara Community 7.2026.8](https://www.azul.com/downloads/azul-payara-community-edition/), pull [the API from Maven Central](https://central.sonatype.com/artifact/jakarta.agentic-ai/jakarta.agentic-ai-api/1.0.0-M1), file issues on [GitHub](https://github.com/payara/Payara/issues), and follow [the draft specification](https://github.com/jakartaee/agentic-ai/releases/download/1.0.0-M1/jakarta-agentic-ai-1.0.0-M1.pdf) as it moves toward 1.0 final. If you want the background on the milestone itself, we covered it in [Jakarta Agentic AI hits its first milestone](https://foojay.io/today/jakarta-agentic-ai-hits-its-first-milestone/).

**Planning the Move to Payara 7**

For teams on the 5 or 6 line evaluating the move to [Payara 7](https://www.azul.com/products/payara-server/), the `jakarta.*` namespace is stable between EE 10 and EE 11, so existing Jakarta EE 10 applications deploy on Payara 7 by upgrading the runtime, not rewriting the codebase. Migration assessments are available through your Azul account team.

**Upgrading and Feedback**

We recommend upgrading to the latest release for your line. A security patch for [GHSA-r7wm-3cxj-wff9](https://www.kodemsecurity.com/cve-archive/ghsa-r7wm-3cxj-wff9) is available across the 6, 5, and 4 lines, and the 5 and 4 lines additionally close two Jackson CVEs.

For detailed upgrade instructions, see the [Azul Payara documentation](https://docs.azul.com/payara-community/general-info/getting-started) and the [Payara Server upgrade guide](https://docs.payara.fish/community/docs/Technical%20Documentation/Payara%20Server%20Documentation/Upgrade%20Guide/Overview.html). To report issues, contribute fixes, or follow the Payara 7 roadmap, visit the [Payara GitHub repository](https://github.com/payara/Payara). For commercial support, [contact the team](https://payara.fish/about/contact-us/) or your Azul account team.

Happy deploying.
