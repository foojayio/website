---
title: "Free Java, Real Risk: Why Unsupported OpenJDK Is Now a Security Gamble"
date: "2026-10-28"
description: "Learn why unsupported OpenJDK is a security risk in the age of AI."
canonical: "https://www.azul.com/blog/free-java-real-risk-why-unsupported-openjdk-is-now-a-security-gamble/"
authors:
  - "simonritter"
image: "free-java-real-risk-banner.jpg"
categories:
  - "Java"
  - "Security"
related_posts:
  - "azul-zulu-october-2026-quarterly-update-and-out-of-cycle-release"
  - "ai-found-the-bugs-whos-patching-your-eol-java-code"
  - "did-ai-just-break-software-security-for-ever"
  - "the-real-mechanics-of-vulnerabilities-in-an-upstream-downstream-topsy-turvy-eol-world"
---

Four hours. That’s how long it took AI agents to chain two vulnerabilities in PaperCut, a Java-based print management product, after they were disclosed. A swarm of hundreds of agents then used that exploit to compromise 11 organizations in 26 seconds. In some environments, the path from first breach to full domain administrator access took seven minutes.

If your Java estate runs on free OpenJDK with no support behind it, the trade-off you made years ago (patching it yourselves and living with a few weeks of lag) no longer adds up. Let me show you why, and how to re-run the numbers before an attacker does it for you.

## The Deal We All Made with Free Java

For years, the deal was simple. Use OpenJDK without a support subscription, take on the patching yourselves, and if a fix took a couple of extra weeks, that was uncomfortable but it usually turned out fine. I’d call it free as in puppy, not free as in beer. The software costs nothing, but somebody still has to feed it, walk it, and clean up after it. Someone has to triage, patch, test, troubleshoot, respond to incidents, and perform root cause analysis. None of that went away.

What changed is the risk side of the bargain. And even at human speed, a patch gap could hurt: the Apache Struts flaw behind the Equifax breach had a fix available roughly two months before attackers got in, and about 147 million people were affected. Now the attackers aren’t human.

## The Fabric of Exploit Time Has Torn

In the age of AI-accelerated vulnerability exploitation, the median time organizations take to remediate a known vulnerability is 43 days, according to the 2026 Verizon Data Breach Investigations Report. That’s not nearly fast enough. Case in point: Palo Alto Networks’ Unit 42 documented an autonomous, AI-driven breach of an enterprise network in which agents worked through application services, cloud resources, source-control repositories, and CI/CD workflows. What would have taken human attackers multiple weeks took 10 hours.

Even CISA has noticed. Its June 2026 Binding Operational Directive 26-04 gives federal agencies a three-calendar-day deadline for vulnerabilities that cross its risk threshold. Now think about what a three-day deadline means if your update arrives days or weeks after disclosure. The arithmetic doesn’t work.

In contrast, Azul delivers security patches that effectively have a zero window of exploitability, because the patches are available (for all intents and purposes) simultaneously with the CVE detail publication. (See below for more information on this.)

## Low-risk CVEs Don’t Stay Low-risk

One reason teams feel comfortable skipping or delaying a quarterly update is that nothing in it looks critical. That’s a human way of thinking about risk. An AI agent doesn’t need one big flaw. It can string together several low or medium-severity CVEs, one to get in, another to go deeper, until the combination adds up to full control, even though no single vulnerability would be rated high on its own.

Older estates hand attackers more to work with. According to Azul’s [2026 State of Java Survey & Report](https://www.azul.com/newsroom/azul-2026-state-of-java-survey-report-62-of-enterprises-now-leverage-java-to-power-ai-functionality-41-rely-on-high-performance-java-platforms-to-reduce-cloud-compute-costs/), 20% of enterprises still run end-of-life Java 6 or 7 in production. Java 6 shipped in 2006, before Kubernetes existed, and these runtimes remain vulnerable to new CVEs. Aging components, poor visibility into what’s actually loaded, and fewer safeguards are exactly the raw material chaining needs.

## What “Free” Actually Costs

Over the past twelve months, the indicators have moved in exactly the wrong direction. According to the [Verizon DBIR](https://www.verizon.com/business/resources/reports/dbir/), full remediation of CISA Known Exploited Vulnerabilities (KEVs) dropped from 38% to 26%, the median time to full patching rose from 32 to 43 days, and the median number of KEVs hitting an organization rose from 11 to 16. By our math, that adds up to roughly 71% more expected live exposures per year.

Meanwhile, the average cost of a data breach hit \$4.99 million in 2026, a record, according to [IBM and Ponemon](https://ibm.com/reports/data-breach). An AI-enabled breach costs about 20% more, roughly \$6 million. Cyber insurers are paying attention too: lacking an SLA-backed supplier can raise premiums by 10 to 25%, according to AIG, Munich Re, and Zurich, and one analysis found 37% of cyber insurance claims were denied because of security gaps insurers found.

Compliance adds its own weight. Auditors care less about whether a component carries a license fee than whether you can show a documented, timely, and repeatable process for managing known risk. Unsupported Java doesn’t automatically fail an audit, but between PCI DSS 4.0.1, NIST SP 800-53, HIPAA, and GDPR’s 72-hour breach notification window, the evidence burden compounds with every framework that applies to the same estate.

## What Support Changes

Let me be clear about what commercial support is and isn’t. It’s not a panacea for every cybersecurity need. It’s a decision to transfer defined parts of the operational burden, security response, update engineering, troubleshooting, and escalation, to a vendor. Here’s what that looks like in practice:

- **Speed.** Azul Core customers receive Security-only, Stability-first™ Critical Patch Updates on a strict SLA, typically within one hour of disclosure, plus out-of-cycle fixes for zero days (something we saw recently in the October 2026 update).
- **Stability.** Azul’s internal analysis found regression issues in about 20% of full quarterly updates over six years (5 of 24). Azul’s security-only updates contain only security fixes deployed on the previous quarter’s cumulative update that has been extensively tested by the community. As a result, Azul’s CPUs are suitable for immediate deployment with little risk of regression.
- **Legacy coverage.** Azul backports security fixes for Java 6 and 7, supports Java Applets on Azul Core, and maintains its own fork of IcedTea-Web.

The choice was never really free versus paid. It’s a choice between risk and cost, and the numbers behind it changed in the last twelve months. Re-run yours before an AI agent does it for you.

{{< important >}}
This is not theoretical this month. Azul shipped the October 2026 quarterly update (CPU) on October 20, followed by two out-of-cycle releases: one on October 21 for a zero-day (compromised eMudhra CA keys), and a second on October 24 for a related vulnerability found afterwards. If you only installed the CPU, you need to update again. See [Azul Zulu October 2026 Quarterly Update: 53 CVEs, Then a Zero-Day One Day Later](/today/azul-zulu-october-2026-quarterly-update-and-out-of-cycle-release/) for the details.
{{< /important >}}



## FAQ

**What are the security risks of running unsupported OpenJDK in production?**

Unsupported OpenJDK leaves your team responsible for triaging, patching, testing, and escalating every vulnerability. Full quarterly updates often arrive days to weeks after disclosure, and about 20% of them (5 of 24 over six years, per Azul’s internal analysis) have introduced regressions. [Azul Core](https://www.azul.com/products/core/), a commercially supported build of OpenJDK, delivers Security-only, Stability-first™ Critical Patch Updates on an SLA, typically within one hour of disclosure.

**How quickly are Java vulnerabilities exploited today?**

The median time to exploit a disclosed vulnerability is now measured in negative time – i.e., the time before a patch is available – while the median time to remediate is 43 days, according to [M-Trends 2026](https://cloud.google.com/blog/topics/threat-intelligence/m-trends-2026/) and the [2026 Verizon DBIR](https://www.verizon.com/business/resources/reports/dbir/). AI agents can compress this further: in one documented case, they chained two vulnerabilities in Java-based PaperCut software four hours after disclosure. Azul Core customers have access to out-of-cycle patches that help address this kind of fast-moving vulnerability.

**What is the difference between a cumulative quarterly update and a CPU?**

A cumulative quarterly update bundles hundreds of bug fixes, security fixes, and feature updates, which means extensive regression testing before deployment. A Critical Patch Update (CPU) from Azul contains only security fixes applied to the prior quarter’s community-tested cumulative quarterly update. Azul Core CPUs are designed for immediate deployment with minimal regression risk.

**How does unsupported Java affect compliance and cyber insurance?**

Unsupported Java does not automatically fail an audit, but it increases the burden to document how vulnerabilities are identified, remediated, or formally accepted under frameworks such as PCI DSS 4.0.1, NIST SP 800-53, HIPAA, and GDPR. Insurers are also scrutinizing patching discipline, and lacking an SLA-backed supplier can raise premiums by 10 to 25%. Azul Core gives teams a documented update, lifecycle, and escalation path to point to.

**How can I secure legacy Java 6 and 7 applications?**

Oracle no longer supports JDK 6 or 7, yet they remain vulnerable to new CVEs and still run business-critical workloads in about 20% of enterprises. Azul backports security fixes for Java 6 and 7, supports Java Applets on Azul Core, and maintains its own fork of Iced-Tea-Web for Java Web Start applications. To see where free and unsupported Java is running in your estate, request an [Azul JVM Vulnerability Risk Assessment](https://www.azul.com/jvm-vulnerability-risk-assessment/).
