---
title: "Grails Is Back: Inside the Apache Software Foundation Migration"
slug: "grails-isnt-done-yet-part-1-inside-the-asf-reboot"
date: "2026-03-25T08:30:21+00:00"
description: "Grails graduated to a Top-Level Apache project in 2025. Here's what the 18-month migration, Grails 7 release, and Spring Boot alignment mean for teams still running it."
authors:
  - "steve-poole"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "Developer Tools"
  - "Interviews"
  - "Java"
  - "Spring"
  - "Tools"
  - "Uncategorized"
tags:
related_posts:
frozen: false
---

> Steve Poole \| With contributions from James Fredley, Apache Grails PMC Chair

For a technology that many people filed under "legacy," Grails has been unusually active. While much of the industry's attention has drifted toward newer frameworks and shinier stacks, something more deliberate has been happening in the background.

Grails has been moving into the Apache Software Foundation (ASF), modernising and positioning itself for the next chapter.  

If you have not looked at Grails recently, your mental model is likely several years out of date. And that, in many ways, is exactly the problem.

### The technology we stop seeing {#h3-0-the-technology-we-stop-seeing}

Software ecosystems rarely end with a bang; most of the time, they simply slip out of focus. Conference agendas move on, blog coverage thins out, and new frameworks capture the narrative. Eventually, we collectively "agree" that a technology is "basically done".

Except in enterprise environments, that is often not true at all. There are still Grails applications in production, processing transactions and serving customers. But while the systems remain, the organisational spotlight has shifted.

There is a significant gap between what gets hype and what actually runs the web.

According to W3Techs, PHP powers roughly 71.8% of all websites whose server-side language is known. Between 40% and 60% of the web runs on WordPress alone.

JavaScript, for all its conference-circuit dominance, accounts for under 6% on the server side. The technologies that quietly keep the internet running and the ones that dominate the narrative are often not the same technologies at all.

### Why the Apache move matters {#h3-1-why-the-apache-move-matters}

The transition of Grails into the ASF is not merely administrative tidying. Moving under the ASF umbrella is one of the clearest signals an open-source project can send about its long-term intent.

ASF provides a neutral home, predictable release discipline, and a contributor model that reduces perceived vendor risk.

For Grails, this matters because mature platforms live or die on trust signals. The ASF move changes the risk conversation for organisations evaluating whether Grails still has a place on their servers.

### Twenty years of changing hands {#h3-2-twenty-years-of-changing-hands}

The context makes the move even more significant. Grails was primarily led by single organisations for most of its 20-year history: G2One from 2005 to 2008, then SpringSource through 2015, Object Computing through 2021, and the Grails Foundation/Unity Foundation through 2025.

Each transition introduced uncertainty about the project's direction and sustainability.

The ASF model is designed to break that pattern, replacing single-organisation dependency with volunteer-driven governance, vendor neutrality, and the structured transparency of the Apache Way.

### Eighteen months of migration {#h3-3-eighteen-months-of-migration}

In October 2025, Grails officially graduated from incubation to become a Top-Level Project at the ASF, following a board vote in September.

That sounds like a single event. It wasn't. The migration was an 18-month process that began in late spring 2024 with a volunteer team assessing project readiness and submitting an incubation proposal.

What followed was a substantial modernisation effort: merging repositories into a mono-repo, overhauling the build system and dependency management, upgrading Maven coordinates, and issuing releases under ASF governance. The first ASF release (Milestone 4) shipped in June 2025, with the 7.0.0 General Availability release arriving in October.

### One hundred repositories become nine {#h3-4-one-hundred-repositories-become-nine}

The scale of the repository consolidation tells its own story. Grails originally had around 100 Git repositories, of which 43 were slated for ASF migration. By the time the move was complete, those had been consolidated to 18, with only 9 still in active use.

That is a lot of plumbing.

The mono-repo approach accelerated compliance with ASF policy but required integrating separate build systems and release processes across hundreds of commits.

Over 2,000 commits went into the grails-core mono-repo alone, and build times for a release dropped from over three weeks to approximately 30 minutes.

Read that again. Three weeks to thirty minutes.

### Beyond the code: licensing and compliance {#h3-5-beyond-the-code-licensing-and-compliance}

The code was only part of it. The team also had to meet ASF security and licensing requirements. Reproducible, verifiable builds were implemented (requiring upstream contributions to dependencies including Apache Groovy).

Every source file was reviewed for licence headers, and 327 separate artefacts were audited for licensing compliance. The team automated licence review by adopting Software Bill of Materials for every published jar, ensuring ongoing compliance with reduced future effort.

Migrating the fully automated Gradle and GitHub Actions workflows proved to be a novel challenge in its own right; other Gradle-based projects at the ASF are now looking at the result as a reference implementation.

### The modernisation you might have missed {#h3-6-the-modernisation-you-might-have-missed}

A significant amount of careful modernisation has been focused on keeping Grails aligned with the moving baseline of the JVM and the Spring ecosystem.

This is not cosmetic: dependencies have been pulled forward, and compatibility with newer Java runtimes has been tightened.

### What Grails 7 actually ships {#h3-7-what-grails-7-actually-ships}

Grails 7.0.0 shipped in October 2025 as the first stable release under ASF stewardship. It brings major dependency upgrades including Java 17+ support (through to Java 25), Groovy 4, Spring Boot 3.5, Spring Framework 6.2, and Jakarta EE 10.

Alongside the platform alignment, the release introduced containerised browser testing via Testcontainers and Geb, optional Micronaut integration, SBOM generation for all published binaries, and reproducible builds and artefacts.

The grails-core mono-repo now produces over 325 published jar files across 109 Gradle projects, with local build times between two and ten minutes depending on caching and hardware.

### Grails 8 and the release cadence {#h3-8-grails-8-and-the-release-cadence}

Grails 8 development started in late November 2025, tracking Spring Boot 4.0 which reached general availability at the end of that month.

The project now follows Spring Boot's six-month release cadence, with 13 months of support per release. Giving teams predictable timelines to plan around.

### The humans behind the reboot {#h3-9-the-humans-behind-the-reboot}

Open-source projects do not evolve by inertia. They move forward because a relatively small number of people decide the work is worth doing.

One of the challenges Grails faces today is not a lack of activity but a lack of visible narrative.

Much of the effort is concentrated in a tight group of committed maintainers. From the outside, that can appear to be silence even when meaningful progress is underway.

*** ** * ** ***

To make that work more visible, I spoke with **James Fredley, the Apache Grails PMC Chair,** about where the project stands and where it is heading.

#### What motivated the move to the Apache Software Foundation?

*There were real questions about Grails' future, and they were understandable.*

*The concerns crept in during the 4.x through 6.x era, when the project moved through several organisations and its direction felt uncertain. For most of its 20-year history, Grails was primarily led by a single organisation at any given time, with limited community contributions or input.*

*The move to the ASF was about addressing that directly: shifting from single-organisation dependency to a volunteer-driven, vendor-neutral model. The ASF's structure: the Project Management Committee, mailing lists, consensus-based voting, the incubation process, gives people confidence that the project is sustainable, not dependent on any one company's priorities.*

#### From inside the project, what kind of technical work has been happening?

*The scale of it probably surprises people. Thousands of hours of volunteer time have gone into modernising the 7.x line and building toward 8.x.*

*We consolidated from around 100 repositories down to 18 (with 9 active), rewrote the build and release pipeline, achieved reproducible and verifiable builds, implemented SBOM generation, and ensured licensing compliance across hundreds of artefacts.*

*Grails 7 now produces over 325 published jar files across 109 Gradle projects, with local build times between two and ten minutes. The release process itself went from a three-week ordeal to about 30 minutes.*

*Migrating our fully automated Gradle and GitHub Actions workflows to the ASF was a novel challenge, but grails-core can now serve as a model for other Gradle-based projects joining the Foundation.*

*What people also need to understand is that a Grails application is a Spring Boot application.*

*With roughly 85--90% of Java applications running on Spring Boot,Grails is not some exotic outlier: it is extra developer-productivity layers on top of what everyone in the Java ecosystem already uses.*

#### What do you hope the ASF transition unlocks?

*Broader adoption and broader contribution. The ASF gives us credibility with enterprise decision-makers who need to know a framework will still be around in five or ten years.*

*But it also lowers the barrier for new contributors. The governance is transparent, the processes are well-documented, and the project is genuinely welcoming.*

*Grails now follows Spring Boot's release cadence: a six-month cycle with 13 months of support, which gives teams predictable timelines to plan around.*

#### What misconception about Grails would you most like to correct?

*That it's a legacy technology for legacy teams.*

*Grails is still the most productive way to build a web application in the Java ecosystem, and that should be a draw for newer engineers and greenfield projects, not just established estates.*

*The convention-over-configuration approach means less boilerplate, sensible defaults, and a gentle learning curve.*

*It is a "framework of frameworks," built on Spring Boot, Spring Framework, Jakarta EE, and Hibernate.*

*If you know those, you already know a significant part of Grails.*

*** ** * ** ***

### Where Grails realistically sits in 2026 {#h3-10-where-grails-realistically-sits-in-2026}

Grails is not trying to out-Spring Boot Spring Boot. Where it continues to make sense is in environments that value convention-heavy productivity and rapid delivery, particularly where there is already meaningful investment in the Groovy ecosystem.

For teams with established Grails estates, the question isn't *"does it work?"* but *"is it still safe to stay?"*

The ASF graduation, the release of Grails 7 (supporting Java 17 through 25), and the active development of Grails 8 tracking Spring Boot 4 are designed to lower the perceived risk of that decision. But that safety is contingent on moving forward.

For teams evaluating new projects, the productivity argument deserves a fresh hearing. As Fredley puts it, Grails is extra developer-productivity layers on top of what 85--90% of the Java ecosystem already uses. That framing: not "legacy framework" but "productivity accelerator built on Spring Boot", is a different proposition than the one most people have filed away in their mental models.

### The hard work that keeps software alive {#h3-11-the-hard-work-that-keeps-software-alive}

Software rarely dies because of a single technical flaw; it fades because attention moves somewhere else.  

What the current maintainers are doing is the careful, methodical work required to keep a mature framework viable in a fast-moving ecosystem.

In an industry that celebrates only the new, that kind of work, and the difficult EOL conversations it requires, is easy to overlook.

*It probably should not be.*

Of course, none of this helps the teams still running Grails 3 or 4 on their servers. For them, the dependency cliff is already here. *In part two*, I want to look at what that cliff actually looks like and what the options are.

#### Resources

* [Apache Grails Support Schedule](https://grails.apache.org/support-schedule.html)
* [HeroDevs Grails EOL Support](https://www.herodevs.com/support/apache-grails-nes)
* [Spring Boot End of Life Dates](https://github.com/spring-projects/spring-boot/wiki/Supported-Versions)
* [HeroDevs Spring Boot EOL Support](https://www.herodevs.com/support/spring-nes)
* [ASF Graduation Announcement](https://grails.apache.org/blog/2025-10-07-apache-grails-graduation-top-level-project.html)
* [Grails 7.0.0 Release Announcement](https://grails.apache.org/blog/2025-10-18-introducing-grails-7.html)
* [Introduction to Apache Grails 7 (Community Over Code NA 2025, James Fredley](https://docs.google.com/presentation/d/1tHpQVCwTJgI6mVGZyZYsip3U7FRZDjXkyqNC9KvPIBU/edit?slide=id.p#slide=id.p)
* [Migrating a 20 Year Old Project to the ASF (Community Over Code NA 2025, James Fredley)](https://docs.google.com/presentation/d/1TpjDl4vGyCg31kIkTHB9QaqXEJECtkRBP7YfDU_tG48/edit?slide=id.p#slide=id.p)

Author's note:

In the interest of transparency, I work for HeroDevs, a company that provides extended support for end-of-life open-source components. If you are currently assessing the support posture of older Grails estates, it is worth understanding the continuity [support options available](https://www.herodevs.com/support/apache-grails-nes) in the Java ecosystem. The landscape has evolved significantly in the past few years.
