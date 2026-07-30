---
title: "Grails Is Back: Inside the Apache Software Foundation Migration"
slug: "grails-isnt-done-yet-part-2-eol-spring-boot-and-what-comes-next"
date: "2026-04-01T08:48:56+00:00"
description: "Grails graduated to a Top-Level Apache project in 2025. Here's what the 18-month migration, Grails 7 release, and Spring Boot alignment mean for teams still running it."
authors:
  - "steve-poole"
image: "/images/posts/2026/04/grails-isnt-done-yet-part-2-eol-spring-boot-and-what-comes-next/Favicon-3-2.png"
categories:
  - "Uncategorized"
tags:
related_posts:
frozen: false
---

In the [companion article](https://foojay.io/today/grails-isnt-done-yet-part-1-inside-the-asf-reboot/) to this one, I looked at the revitalisation of Grails under the Apache Software Foundation: the 18-month migration, the technical modernisation, and the release of Grails 7 as a Top-Level ASF Project. That is the good-news story, and it is a genuinely impressive piece of community engineering.

This article is about the other side of the same coin.

While Grails moves forward, many of the applications built on it cannot move at the same pace. The result is a growing gap between where the framework is heading and where a significant number of production systems actually sit. Understanding that gap, and what options exist for managing it, is what this piece is about.

### The inflexion point {#h3-0-the-inflexion-point}

The alignment of Grails with modern Spring Boot and Java baselines brings us to a critical inflexion point in 2026. While the framework is being revitalised, the "gravity" of the underlying ecosystem is shifting. Many legacy Grails applications remain tied to versions of Spring Boot and Java that are rapidly approaching or have already reached End of Life.

### Where Grails versions stand today {#h3-1-where-grails-versions-stand-today}

The Apache Grails support schedule tells the story clearly:

* Grails 3 and 4 have reached End of Support.
* Grails 5 ended support in June 2024.
* Grails 6 (the last pre-ASF release, with 6.2.3 shipping in January 2025) reached End of Support in June 2025.
* Grails 7 is in Active Maintenance with support through June 2026
* Grails 7.1 and 8 are in Active Development with Grails 8 maintenance support targeted for December 2026.

That is the Grails layer. Beneath it, the picture gets more complicated.

### The Spring Boot gravitational pull {#h3-2-the-spring-boot-gravitational-pull}

Beneath all of this, the Spring Boot timelines create their own gravitational pull.

Spring Boot follows a six-month release cycle with roughly 13 months of open-source support per release. That sounds generous until you lay the dates out: Spring Boot 3.3 OSS support ended in June 2025. 3.4 ended in December. 3.5 runs until June 2026. Spring Boot 4.0 (released November 2025) has OSS support through December 2026.

For teams still running applications on Spring Boot 2.x, open-source support ended years ago. Only commercial extended support remains available. The window is not slamming shut. It is closing steadily, and each version that falls off the end makes the next upgrade harder.

Taken together, this is less a single deadline and more a slow-moving *dependency cliff*.

### What the risk actually looks like {#h3-3-what-the-risk-actually-looks-like}

The primary corporate risk is rarely that these systems suddenly stop working. Mature Grails applications are typically very stable. The real exposure appears more slowly and more quietly, when organisations lose visibility of their dependency health and drift out of a supported posture without fully realising it.

In the Java ecosystem, supply chain health matters far more than announcement-day excitement.

A team running Grails 4 on Spring Boot 2.x and Java 11 does not wake up one morning to a broken application. What they wake up to, eventually, is a CVE that applies to their stack and no upstream patch to apply. Or a compliance audit that flags unsupported components. Or a new integration requirement that demands a Java version their framework cannot support.

The danger is not sudden failure. It is the slow accumulation of exposure that nobody is tracking.

### The practical middle ground {#h3-4-the-practical-middle-ground}

In practice, most organisations are not choosing between "upgrade tomorrow" and "do nothing." Reality is rarely that clean. Portfolio constraints, regulatory timelines, and simple engineering capacity mean many teams need a supported holding pattern while they plan their next move.

Increasingly, this is where commercial continuity support for end-of-life open source is emerging as a pragmatic middle ground. A small but growing number of providers now specialise in keeping critical open source components supported beyond their community end-of-life, giving teams breathing room without forcing rushed or poorly sequenced migrations.

Even the ASF itself acknowledges this reality: the Foundation does not offer commercial support, but it recognises that not everyone can keep pace with upstream release cadences.

### Upgrade is an action, not a strategy {#h3-5-upgrade-is-an-action-not-a-strategy}

The reflex response to EOL exposure is "just upgrade." And upgrading is, eventually, the right thing to do. But treating it as a strategy rather than an action ignores the complexity of real-world systems.

A Grails 4 application is not merely a Grails application. It is a Spring Boot 2.x application, running on a specific Java version, with a specific set of transitive dependencies, deployed into a specific infrastructure.

Upgrading Grails means upgrading Spring Boot, which means upgrading Java, which means re-validating every integration point, every test suite, every deployment pipeline.

For teams with a single application, that is manageable. For teams with a portfolio of services, some of which were built by people who have since left the organisation, it is a multi-quarter programme of work.

Pretending otherwise does not make the problem smaller. It just makes the plan worse.

#### Know what you're running

This sounds obvious. It often is not. Fat JARs, shaded dependencies, containers, vendor forks, embedded runtimes: over time, even the teams shipping a system may no longer be certain what is actually inside it. SBOMs are not insight. They are institutional memory. Start there.

#### Understand your exposure window

Map your Grails and Spring Boot versions against the support schedules. Know which components are still covered, which are approaching EOL, and which have already passed it. This does not require a commercial tool. It requires someone spending a day with a spreadsheet.

#### Buy time if you need it

If a full upgrade is not feasible in the near term, commercial continuity support for EOL components can keep your systems in a supported posture while you plan. This is not a permanent solution (unless your aiming to retire the app real-soon-now), but it is a pragmatic one for teams that need breathing room.

#### Commercial EOL support.

If you are currently assessing the support posture of older Grails estates, it is worth understanding the continuity support options available in the Java ecosystem. The landscape has evolved significantly in the past few years.

See [here](https://grails.apache.org/support.html) for some 'official' offerings

I work for one on that list: HeroDevs (see later for the full disclaimer) ,who provide support for Grails and many other Java and non Java products.

Providing EOL support is not a simple undertaking and requires particular skills and knowledge which I like to believe is something HeroDevs excels at. Visit their website <https://herodevs.com>

#### Plan the upgrade as a programme, not a task

If you have multiple Grails applications at different versions, sequence the work. Prioritise by exposure, not by convenience. Treat the dependency cliff as the engineering constraint it is, and fund it accordingly.

### Summary {#h3-6-summary}

The Grails revitalisation under the ASF is real, and it matters. But it does not retroactively protect the applications that were built on earlier versions of the framework. Those systems need their own plan.

In an industry that celebrates only the new, the work of keeping older systems safe and supported is easy to overlook. It probably should not be.

### Resources {#h3-7-resources}

[Apache Grails Support Schedule](https://grails.apache.org/support-schedule.html)  
[Spring Boot End of Life Dates](https://endoflife.date/spring-boot)  
[Grails 7.0.0 Release Announcement](https://grails.apache.org/blog/2025-10-18-introducing-grails-7.html)

*** ** * ** ***

#### Author's note: Full Disclosure

*In the interest of transparency: I work for HeroDevs, a company provides extended security support for end-of-life open source components ( including Java ecosystem frameworks ) and funds open source maintainers through its sustainability programme.*

*Where HeroDevs tools or services are referenced in this article it's because I truly believe that what they offer is significant and relevant. My views on open source sustainability and EOL risk are formed independently and predate that relationship.*
