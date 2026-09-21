---
title: "What's New In The September 2026 Azul Payara Release?"
date: "2026-09-16T16:46:54+00:00"
lastmod: "2026-09-17T08:40:00+00:00"
description: "MicroProfile 7.1 lands on the commercial lines, OpenTracing is removed, and Jakarta Agentic AI reaches Payara Micro in the September Azul Payara release."
authors:
  - "dominika-tasarz"
image: "Whats-New-in-the-Payara-September-2026-Release.jpg"
categories:
  - "Jakarta EE"
  - "Payara"
related_posts:
frozen: false
---

The September 2026 release brings Azul Payara Server & Micro 7.4.0, Payara Community 7.2026.9, as well as Azul Payara Server & Micro 6.42.0 5.91.0 & 4.1.2.191.59.

The headline for Azul Payara is MicroProfile 7.1. It shipped in the Community release last month; this cycle it arrives on both the commercial 7 and 6 lines. That upgrade brings one change that needs reading before you upgrade: MicroProfile OpenTracing has been removed, and the specification defines no automatic migration path.

Beyond MicroProfile, this is a release with a clear migration theme. A new JSON-formatted HTTP access log lands across the 7, 6, and 5 lines. Three pieces of deployment-descriptor work make moving applications between Payara major versions less painful. And a `jackson-databind `CVE is patched across every supported branch, from the 7 line down to Payara 4.

On the Community side, the Jakarta Agentic AI preview that debuted in August now runs on Payara Micro and Payara Embedded.

## **Branches At A Glance**

|                                           |                |                  |                                                                     |
|-------------------------------------------|----------------|------------------|---------------------------------------------------------------------|
| **Release**                               | **Jakarta EE** | **MicroProfile** | **Notable this cycle**                                              |
| Azul Payara Server and Micro 7.4.0        | 11             | 7.1 (was 6.1)    | MicroProfile 7.1, JSON access log, Hazelcast 5.7.0, descriptor work |
| Azul Payara Community 7.2026.9            | 11             | 7.1              | Jakarta Agentic AI on Micro and Embedded                            |
| Azul Payara Server and Micro 6.42.0       | 10             | 7.1 (was 6.1)    | MicroProfile 7.1, JSON access log, descriptor work                  |
| Azul Payara Server and Micro 5.91.0       | 8              | 4.1              | JSON access log, payara- descriptors, protobuf 4.35.1               |
| Azul Payara Server and Micro 4.1.2.191.59 | —              | —                | Security fixes, Jackson 2.18.10                                     |

## **A Security Fix Patched Across Every Branch**

Two security items ship across the whole cycle.

* **CVE-2026-68497** , a resource exhaustion vulnerability in `jackson-databind`, is fixed in Azul Payara Server and Micro 7.4.0, 6.42.0, 5.91.0, and 4.1.2.191.59, and in Payara Community 7.2026.9.

<!-- -->

* Separately, the **Hazelcast implementation has been upgraded to use shaded Jackson 2.18.6 or later**, closing off the same class of exposure inside the clustering and data grid layer. This also lands on every branch.

Shipping the patch across the full supported lifecycle, not only the latest major release, is the practice long-running Azul customers rely on. Azul is a registered CVE Numbering Authority (CNA) under CISA and DHS oversight, with patches backported to every supported version on a published monthly schedule. There is no reason to delay upgrading based on the major-version line you run.

## **What's New In Payara Community 7.2026.9**

![](Email-Header-Payara-Community-September-2026-1024x341.jpg)

Payara Community 7.2026.9 ([download here](https://www.azul.com/downloads/azul-payara-community-edition)) is the open-source distribution that tracks the Payara 7 development line, shipping Jakarta EE 11 and MicroProfile 7.1.

It carries the same security fixes, bug fixes, and component upgrades as Azul Payara Server and Micro 7.4.0 - including Hazelcast 5.7.0, the JSON-formatted HTTP access log, the descriptor work, and the Docker JDK 25.0.4.1 and 21.0.12.1 refreshes - plus one item of its own that is the most interesting thing in this month's release.

### **Jakarta Agentic AI Comes To Payara Micro And Embedded**

In August, Payara Community 7.2026.8 shipped a first preview of the Jakarta Agentic AI 1.0 API, the vendor-neutral specification for building AI agents on Jakarta EE runtimes, where an agent is simply a CDI bean annotated with `@Agent` and a LargeLanguageModel is injected like any other dependency.

September extends that preview to **Payara Micro and Payara Embedded**.

The August preview put agents inside a full application server. Bringing the API to Micro means an agent can ship in the same single-JAR, container-first footprint that cloud-native Payara deployments already use - the natural home for a service whose job is to call a model and act on the answer. Bringing it to Embedded means you can exercise agents from a test harness or an embedded runtime without standing up a server at all, which is what makes the programming model practical to iterate on.

The specification is still a standalone Jakarta EE release at Milestone 1 (1.0.0-M1) and out for early feedback, so the API may change. That is precisely the point of a preview: building against it now, in a real Jakarta EE 11 runtime and in the deployment shape you actually use, is how the specification gets shaped before it is finalised. Feedback on the Payara GitHub repository (github.com/payara/Payara) and through the specification's own channels is what decides how quickly this reaches the commercial lines.

### **Community Contributions**

Thanks to community contributor lprimak for the fix to the leaking "injection manager found in the current thread" log messages, included in this release and in Azul Payara Server and Micro 7.4.0.

Every fix in the Community release links to its pull request [in the release notes](https://docs.azul.com/payara-community/release-notes/release-notes-7.2026.9.html), if you want to read the change itself.

## **What's New In Azul Payara Server and Micro**

![](Email-Header-Azul-Payara-September-2026-1024x341.jpg)

### **Before You Upgrade: MicroProfile OpenTracing Has Been Removed**

Support for MicroProfile OpenTracing is gone from Azul Payara Server and Micro 7.4.0 and 6.42.0.

This is Payara catching up with the specification - MicroProfile OpenTracing was dropped from the MicroProfile platform at MicroProfile 6.0 and superseded by MicroProfile Telemetry. Because both lines move to MicroProfile 7.1 in this release, the removal lands with it.

The important detail is that the specification defines no migration path between the two. Specifically:

* `@Traced` has no MicroProfile Telemetry equivalent.

<!-- -->

* Explicit instrumentation now uses the OpenTelemetry `@WithSpan` annotation instead.

If your applications use the MicroProfile OpenTracing APIs, they need re-instrumenting before you upgrade. This is the one item in the September cycle that can break a working deployment, so it is worth an audit of your tracing code before you move.

Azul Payara Server and Micro 5.91.0 stays on MicroProfile 4.1 and is unaffected. And Payara Community moved to MicroProfile 7.1 in the August release, so Community users have already made this transition.

If you are planning the switch, the OpenTelemetry work in this release helps: the OpenTelemetry SDK moves to 1.65.0, the instrumentation annotations to 2.31.1, and the semantic conventions to 1.43.0 on the 7 line. That SDK move is larger than it looks: on the 7 line OpenTelemetry advances from 1.48.0 to 1.65.0 within this single cycle, so teams re-instrumenting off OpenTracing land on a substantially newer OpenTelemetry than the 7 line shipped with last month.

### **Azul Payara 7.4.0**

Azul Payara 7.4.0 ([request free trial here](https://www.azul.com/azul-payara-trial/)) is the fourth regular monthly release on the Payara 7 line since Server 7 and Micro 7 reached general availability in May. Server 7.4.0 is Jakarta EE 11 certified across Full Platform, Web Profile, and Core Profile; Micro 7.4.0 implements the Web Profile and Core Profile APIs.

#### **MicroProfile 7.1**

The commercial 7 line moves from MicroProfile 6.1 to 7.1 this cycle, with the MicroProfile OpenAPI API at 4.1.1. Alongside the observability upgrades noted above, this closes the gap between the commercial 7 line and Payara Community, which picked up MicroProfile 7.1 in August.

#### **A JSON-Formatted HTTP Access Log**

Payara can now emit its HTTP access log as JSON rather than as a formatted text line.

This is a small feature with a disproportionate operational payoff. Text access logs have to be parsed on the way into a log pipeline, which in practice means maintaining a grok or regex pattern that breaks quietly whenever the log format is adjusted. A JSON access log arrives in Elasticsearch, Loki, Splunk, or an OpenTelemetry collector already structured, with each field addressable as a field. Querying by status code, response time, or request URI stops being a string-matching exercise.

For teams running Payara in Kubernetes, where stdout is the log transport and everything downstream is a structured pipeline, this removes a step that most people had solved with custom configuration. The feature is available on the 7, 6, and 5 lines.

#### **Deployment Descriptor Compatibility**

Three separate items in this release touch Payara deployment descriptors, and together they form the most useful theme of the cycle for anyone planning a major-version migration.

**\`payara-\` deployment descriptors for Payara 5.** Payara 5 descriptor formats are now recognised, extending the major-versioned descriptor mechanism that shipped in July. An application can declare which Payara major version its descriptors target, rather than relying on the runtime to guess.

**A fix for Payara 6 descriptors erroneously removing deprecated elements.** Payara 6 deployment descriptors were stripping deprecated elements that applications still depended on. That behaviour has been corrected, which matters for 6-to-7 migrations where the application has not yet been modernised.

**Managed executors can be defined in Payara deployment descriptors again.** This capability was previously available, then lost, and has now been reintroduced. Applications that configured managed executor services declaratively can go back to doing so instead of working around its absence.

Read together with July's major-versioned descriptor support, the direction is clear: the descriptor layer is being turned into a compatibility bridge, so that moving an application from Payara 5 or 6 to Payara 7 is a runtime upgrade rather than a descriptor rewrite.

#### **Bug Fixes**

* **Fixed a blank Admin Console page after deployment.** The console could render empty following a deployment, requiring a reload to recover.

<!-- -->

* **Fixed an instance on an SSH node being unreachable from the DAS.** A cluster instance provisioned on an SSH node could not be reached by the Domain Administration Server. This fix lands on every branch in this cycle, including Payara 4.

<!-- -->

* **Fixed logs leaking "injection manager found in the current thread".** Contributed by community member lprimak (github.com/lprimak). The message was appearing in logs where it had no business being.

#### **Component Upgrades**

Hazelcast moves to **5.7.0** on the 7 line, refreshing the clustering, session replication, and data grid layer on top of the shaded Jackson security fix noted above.

Elsewhere: Mojarra 4.1.14, HK2 4.0.2, Jakarta Data API 1.0.2, Jakarta JSON Bind API 3.0.3, Jackson BOM 2.22.2, Javassist 3.33.0-GA, Project Reactor 3.8.7, Protobuf 4.35.1, JLine 4.4.2, SnakeYAML 2.7, and Apache Ant 1.10.18. Docker images refresh to JDK 25.0.4.1 and 21.0.12.1.

The full list is [in the release notes.](https://docs.azul.com/payara/release-notes/release-notes-7.4.0.html)

### **Azul Payara 6.42.0**

Azul Payara 6.42.0 continues the Jakarta EE 10 line for customers not yet on Payara 7 - and this cycle it also moves to **MicroProfile 7.1**, up from 6.1. That makes it the second line affected by the MicroProfile OpenTracing removal described above, so 6-line users should read that section as carefully as 7-line users do.

Alongside the cross-cycle security items, 6.42.0 picks up the JSON-formatted HTTP access log, the payara- descriptors for Payara 5, the reintroduced managed executor definitions, the blank Admin Console fix, and the SSH node fix.

**Component upgrades:** OpenTelemetry SDK 1.65.0, OpenTelemetry instrumentation annotations 2.31.1, semantic conventions 1.43.0, MicroProfile OpenAPI API 4.1.1, Jackson BOM 2.22.2, Javassist 3.33.0-GA, Project Reactor 3.8.7, Protobuf 4.35.1, JLine 4.4.0, and SnakeYAML 2.7. Docker images refresh to JDK 21.0.12.1, 17.0.20.1, and 11.0.32.1.

### **Azul Payara 5.91.0**

Azul Payara 5.91.0 retains the javax.\* namespace, Jakarta EE 8, and MicroProfile 4.1 for long-lived applications that have not migrated to the jakarta.\* namespace. Because it stays on MicroProfile 4.1, it is **not** affected by the OpenTracing removal.

This release picks up both security items, the JSON-formatted HTTP access log, the payara- deployment descriptors for Payara 5 — the on-ramp for 5-line customers who are starting to plan a move — and the SSH node fix.

**Component upgrades:** Protobuf makes a large jump from 3.25.3 to 4.35.1, Commons IO moves to 2.22.0, Javassist to 3.32.0-GA, and the Jackson BOM to 2.21.6. Docker images refresh to JDK 21.0.12.1, 17.0.20.1, 11.0.32.1, and 8u504.

### **Azul Payara 4.1.2.191.59**

Azul Payara 4.1.2.191.59 receives the `jackson-databind` CVE fix, the Hazelcast shaded Jackson upgrade, and the SSH node fix, with Jackson moving from 2.18.8 to 2.18.10 and the Docker image refreshing to JDK 8u504.

Customers on the 4 branch without contracted Lifetime Support can still access existing binaries but receive no new releases beyond security patches.

## **Looking Ahead**

The Azul Payara product line spans the JDK (Azul Zulu and Azul Platform Prime), the full application server (Azul Payara Server) and the cloud-native runtime (Azul Payara Micro), all from one vendor. The 7, 6, 5, and 4 lines continue to receive monthly security and bug-fix releases on the published schedule, with patches backported across every supported version.

If you run MicroProfile OpenTracing on the 7 or 6 lines, re-instrumenting against OpenTelemetry `@WithSpan` is now blocking work rather than future work. And if you are evaluating a move to Payara 7, this release makes that easier than the last one did: the jakarta.\* namespace is stable between Jakarta EE 10 and 11, so existing Jakarta EE 10 applications deploy on Payara 7 by upgrading the runtime rather than rewriting the codebase, and the descriptor compatibility work in this cycle removes a further class of migration friction. Migration assessments are available through your Azul account team.

## **Upgrading And Feedback**

We recommend upgrading to the latest release for your line. A security patch is available across every supported branch, from the 7 line down to Payara 4.

Full details, including the complete component upgrade lists and pull request links, are in the release notes:

* [Azul Payara Community 7.2026.9](https://docs.azul.com/payara-community/release-notes/release-notes-7.2026.9.html)

<!-- -->

* [Azul Payara Server and Micro 7.4.0](https://docs.azul.com/payara/release-notes/release-notes-7.4.0.html)

<!-- -->

* [Azul Payara Server and Micro 6.42.0](https://docs.azul.com/payara/version/6/release-notes/release-notes-6.42.0.html)

<!-- -->

* [Azul Payara Server and Micro 5.91.0](https://docs.azul.com/payara/version/5/release-notes/release-notes-5.91.0.html)

<!-- -->

* [Azul Payara Server and Micro 4.1.2.191.59](https://docs.azul.com/payara/version/4/release-notes/release-notes-4.1.2.191.59.html)

For upgrade instructions, [see the Payara documentation](https://docs.azul.com/payara/technical-documentation/payara-server-documentation/upgrade-guide/); to report issues, contribute fixes, or follow the Payara 7 roadmap, visit the [Payara GitHub repository](https://github.com/payara/Payara) , and for commercial support, contact your [Azul account team.](https://www.azul.com/contact/)

Happy deploying!
