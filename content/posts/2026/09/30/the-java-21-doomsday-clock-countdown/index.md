---
title: "The Java 21 Doomsday Clock Countdown"
date: "2026-09-30"
description: "Oracle's free NFTC license for Java 21 has expired, and the October 2026 quarterly update is the first one enterprises have to pay for. Here are the three options left, and why the timeline is short!"
authors:
  - "dana-crane"
image: "azul-java21-countdown-banner.jpg"
categories:
  - "Java"
  - "OpenJDK"
  - "OpenJDK Migration"
canonical: "https://www.azul.com/blog/the-java-21-doomsday-clock-countdown/"
related_posts:
  - "new-between-quarters-security-updates-for-java-what-cspus-mean-for-your-release-pipeline"
  - "azul-august-2026-release-javas-first-monthly-cspu"
  - "foojay-podcast-101"
frozen: false
---

## The Java 21 Countdown Is Pressing

Oracle JDK 21 was free to use in production under Oracle's No-Fee Terms and Conditions (NFTC) license. That free ride is over. Oracle's licensing rule ties free NFTC use to one year after the next Long Term Support (LTS) release ships, and since Java 25 became generally available on September 16, 2025, the license window closed on September 16, 2026.

The date that matters now is **October 20, 2026**. That is Oracle's next quarterly update — the first batch of Java 21 patches, including Critical Security Patch Updates (CSPUs), that no longer reaches you under the free license. Every Java 21 fix from that day forward requires a paid Oracle subscription. This is not a one-time event: the same mechanism already retired free updates for Java 17, and it will retire Java 25's free updates in turn once Java 29 ships. It's a recurring toll booth built into Oracle's licensing terms.

![The Java 21 doomsday clock](azul-java21-countdown-banner.jpg)

For enterprises, this means the decision can no longer be deferred. If you're still running Oracle JDK 21 in production, you now have three choices:

* Pay Oracle for a commercial subscription.
* Upgrade to Java 25.
* Move to an OpenJDK distribution.

Each path has different pros and cons, but they all carry the same deadline pressure.

## Option 1: Pay Oracle for Support

In an age when agentic AI can create and launch vulnerability exploits in minutes, running Java without SLA-backed access to security fixes significantly raises the risk of security breaches and compliance violations. To gain access to updates including security fixes, you'll need to purchase support for Java 21 deployments, which requires a Java SE Universal Subscription:

* If you already have one, you're good to go since the Universal Subscription charges per employee as opposed to Java usage.
* For some enterprises, Oracle grandfathered the old pricing metric (which was based on Java usage) AS LONG AS your usage didn't change. If you're adding Java 21 applications Oracle is very likely to move you to a Universal Subscription, which can [raise licensing costs by 2-5x](https://www.theregister.com/special-features/2023/07/24/oracles-revised-java-licensing-terms-2-5x-more-expensive/695518).
* Enterprises that have never paid Oracle for Java support face an expense for which no budget exists. Building the business case, securing approval, and executing procurement for a brand-new six- or seven-figure annual subscription is its own multi-month process — one that competes for finance's attention with every other 2026 budget priority, especially AI initiatives.

With time running out, planning should be undertaken for a more cost-effective, longer-term solution.

## Option 2: Upgrade to Java 25

Upgrading to Java 25 on the surface may seem like the responsible, stay-current choice:

* Oracle is committed to at least eight years of support for Java 25 (for paid support only).
* Upgrading from Java 21 to Java 25 is far easier than what you might have experienced when modernizing older Java versions since both 21 and 25 are modern releases.

However, three practical problems make this option harder than it appears, especially this late in the year:

* **The upgrade treadmill never stops.** Oracle now ships a new LTS release every two years and grants only one year of free overlap on the previous version. Enterprises that upgrade to Java 25 today are not solving the problem; they're just scheduling its recurrence.
* **Your third-party dependencies may not be ready.** More than a year after release, the ecosystem is still catching up. For example:
  * Kotlin's tooling has lagged behind Java 25 as a compile target, with Gradle builds falling back to an older JVM target rather than compiling natively for 25.
  * Lombok required a dedicated compatibility release timed to the JDK 25 launch.
  * Bytecode-manipulation-dependent tools such as JaCoCo and Mockito only reached solid Java 25 support after updating to a new version of the underlying ASM library.
* **The runway is gone.** While the upgrade work itself may be quick and easy, dependency audits, regression testing, performance validation, security reviews, and staged rollouts typically take months for mission critical applications.

If you haven't already started, the window has closed for any serious (read: multi-application or complex application) upgrades. But that doesn't mean it's too late to plan for a more permanent solution.

## Option 3: Migrate to OpenJDK

OpenJDK solutions have long proven themselves as an enterprise-grade alternative to Oracle Java SE. While swapping out Oracle Java for OpenJDK server-side is simple and quick, be aware of other concerns:

* **Desktop Migrations** – With Java 25 dropping 32-bit support, some legacy desktop apps may have been left behind. Ensuring legacy app vendors continue to provide support when running on OpenJDK instead of Oracle can become projects in and of themselves.
* **Security Risk** – Enterprises that move to free or unsupported OpenJDK distributions lose access to Critical Patch Updates (CPUs) which provide quarterly updates in the form of ready-to-deploy, security-only patches. Instead, only Patch Set Updates (PSUs) are available usually days to weeks after Oracle ships their quarterly update, and which require extensive testing before deployment. As mentioned above, patching delays in the era of agentic AI threats pose significant security risk.
* **Compliance Risk** – Unsupported OpenJDK also poses an industry and government regulatory compliance risk since most regulations have strong security requirements, especially when it comes to fast patching cycles, quick incident response timelines, and so on.

Starting an OpenJDK migration now for a few Java 21 instances is still viable at this point, especially when moving to a supported, standards-compliant OpenJDK distribution such as [Azul Core](https://www.azul.com/products/core/), which provides:

* **Proven Migration Methodology** – Azul has a track record of 100% success migrating customers off Oracle Java based on best practices, a knowledgebase accumulated via hundreds of migrations and automated playbooks that speed up migrations.
* **Timely Updates** – Azul is the only OpenJDK vendor that provides CPUs just like Oracle, as well as out-of-cycle updates to ensure you can shorten your patch cycle and meet compliance requirements.
* **Java Expertise** – Azul has the largest team of Java experts outside Oracle that provide 24x7x365 worldwide support to ensure your initial migration is successful, and your ongoing business remains stable.

Perhaps most importantly, moving to Azul Core, which offers the industry's longest support lifecycles, takes an enterprise off Oracle's licensing calendar altogether. The decision of when to adopt Java 25, 29, or any future LTS release becomes an engineering decision made on the organization's own roadmap, not a compliance deadline dictated by Oracle's release cadence.

## The Decision Framework

With the clock now past the free-license deadline, the practical question for most enterprises is not which option is theoretically best, it's which option is achievable before the October 20, 2026 update lands, without introducing undue risk:

* Upgrading to Java 25 might have been a viable option if you started months ago.
* Paying Oracle for Java 21 support buys you time, not a solution, at a potential cost increase of 2-5x.
* Migrating to a secure and stable build of OpenJDK with expert commercial support (Azul Core) solves the problem for the current cycle and the next one.

To learn more about these choices download our white paper, [The Oracle Java Threat](https://www.azul.com/oracle-java-threat/), which discusses the opportunity costs, upgrade pitfalls, and OpenJDK solutions in more depth.
