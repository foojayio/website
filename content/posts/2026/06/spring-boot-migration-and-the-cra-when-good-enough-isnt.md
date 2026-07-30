---
title: "Spring Boot 3.5 Migration and the CRA: When Good Enough Isn't\""
slug: "spring-boot-migration-and-the-cra-when-good-enough-isnt"
date: "2026-06-05T08:52:01+00:00"
description: "Spring Boot 3.5 reaches EOL on June 30. The legal context is about to change. Here's what 'without undue delay' means when commercial patches exist.."
authors:
  - "steve-poole"
image: "https://foojay.io/wp-content/uploads/2026/06/Gemini_Generated_Image_jf93u3jf93u3jf93.png"
categories:
  - "Java"
  - "Security"
tags:
related_posts:
  - "did-ai-just-break-software-security-for-ever"
  - "crossing-the-river-styx-spring-boot-3-5-and-the-zombie-dependency-problem"
  - "ai-found-the-bugs-whos-patching-your-eol-java-code"
  - "foojay-podcast-95"
frozen: false
---

Back in April I [wrote](https://foojay.io/today/crossing-the-river-styx-spring-boot-3-5-and-the-zombie-dependency-problem/ "wrote") about what happens to your security posture when Spring Boot 3.5 crosses the EOL line.

The short version: the CVE pipeline dries up, your scanner goes quiet, and the bad actors keep watching upstream for anything they can exploit downstream against the dead code nobody's patching.

I called them zombie dependencies.

June 30th is coming. In a few weeks, Spring Boot 3.5 reaches end of open-source support. You've either got a plan or you haven't.

If You're Already on 4.0 {#h2-0-if-you-re-already-on-4-0}
---------------------------------------------------------

### The zombie problem followed you {#h3-1-the-zombie-problem-followed-you}

Good. I expect that migration wasn't as easy as you expected. One thing worth saying though: crossing to 4.0 doesn't mean you've left the zombie problem behind. Your new dependency tree has its own EOL packages lurking in the transitive layers.

Running the [HeroDevs EOL CLI](https://www.herodevs.com/eol-dataset/eol-data?utm_source=devrel&amp;utm_medium=referral&amp;utm_campaign=2026q2_spring-boot-3-5-eol_global "HeroDevs EOL CLI ")against your new build now is a sensible first step.

If You're Still on 3.5 {#h2-2-if-you-re-still-on-3-5}
-----------------------------------------------------

### The technical risk is growing. The legal risk is about to change. {#h3-3-the-technical-risk-is-growing-the-legal-risk-is-about-to-change}

The technical risk? The zombie problem I described in April: vulnerabilities disclosed upstream, exploitable downstream, invisible in your scanner (because nobody's filing a CVE against a codebase they have no intention of patching)

That's already in play.

What changes on June 30th is the legal context.

The migration is expensive. That's why you need time.  

The migration from 3.5 to 4.0 isn't a version bump. Fifty-plus breaking changes, 36 deprecated classes removed, eight major dependencies changing simultaneously. [I wrote a detailed guide to what it actually costs](https://www.herodevs.com/ebooks/spring-boot-4-0-migration-guide?utm_source=devrel&amp;utm_medium=referral&amp;utm_campaign=2026q2_spring-boot-3-5-eol_global "I wrote a detailed guide to what it actually costs ")

[HeroDevs' own analysis](https://www.herodevs.com/blog-posts/spring-boot-3-5-eol-migration-calculator-estimate-your-upgrade-timeline-to-spring-boot-4?utm_source=devrel&amp;utm_medium=referral&amp;utm_campaign=2026q2_spring-boot-3-5-eol_global "HeroDevs' own analysis ")of equivalent Spring major migrations puts a small codebase at around six weeks. A medium one, 30,000 to 80,000 lines across five microservices, at 12 to 14 weeks. Large codebases take significantly longer.

The Spring community's own guidance recommends starting nine to twelve *months* before EOL. For Spring Boot 3.5, that window opened in July 2025.

What "Without Undue Delay" Actually Means Now {#h2-4-what-without-undue-delay-actually-means-now}
-------------------------------------------------------------------------------------------------

### Article 14 and the 24-hour clock {#h3-5-article-14-and-the-24-hour-clock}

The EU Cyber Resilience Act starts to come into force on 11 September.

Article 14(1) is precise: if a product you ship contains a vulnerability that is actively being exploited in the wild, and you become aware of it, you must submit an early warning to ENISA and your national CSIRT within 24 hours.

A full vulnerability notification follows within 72 hours. A final report is due within 14 days of a corrective measure being available.

The trigger is active exploitation, not CVE assignment, not a particular severity score.

The CISA Known Exploited Vulnerabilities [catalogue](https://www.cisa.gov/known-exploited-vulnerabilities-catalog "catalogue ")is a useful proxy for knowing what's being actively exploited, but the CRA obligation is broader: it applies whenever you become aware that a vulnerability in your product is being exploited, whether or not it's on any particular list.

Then you have a broader obligation to address it *without undue delay.*

That phrase is the kicker. It carries real legal weight.

In EU regulatory frameworks, "without undue delay" is typically interpreted proportionally: relative to what was *reasonably* available to the organisation at the time.

The measure is contextual, not absolute.

### The calculation changes on June 30th {#h3-6-the-calculation-changes-on-june-30th}

Before June 30th, if an actively exploited vulnerability surfaces in Spring Boot 3.5, a regulator asking whether you acted "without undue delay" is essentially asking: why didn't you take the upstream patch? That's a question with a easy answer. Upstream support exists. You can upgrade. The fix is there.

After June 30th, the upstream patch no longer exists. But commercial support does. And that changes the regulatory question entirely.

The regulator is no longer asking why you didn't take the upstream fix. They're asking why you didn't use one of the available options like HeroDevs, to get a patch anyway.

"Without undue delay" is judged against what was reasonably available to you. Once commercial support exists, the answer "there was nothing we could do" stops being true.

### Why commercial support changes your legal position {#h3-7-why-commercial-support-changes-your-legal-position}

Commercial support options exist. HeroDevs provides Never-Ending Support for Spring Boot 3.5 with security patches backported to the EOL version.

Others offer extended commercial support for Spring Boot 4.x through 2032. Though to be fair, as an active open source project you can expect security patches from the actively supported lines anyway. In those offerings your buying bug fixes too.

Now, in the case of a new zombie vulnerability turning up, if it is actively exploited and a commercial support vendor has a patch available, "we're planning to migrate in six months" is a much harder position to defend.

The regulator's implicit question is: why didn't you use the ferry while you built the bridge?

I work for [HeroDevs](https://www.herodevs.com/blog-posts/spring-boot-3-5-eol-migration-calculator-estimate-your-upgrade-timeline-to-spring-boot-4?utm_source=devrel&amp;utm_medium=referral&amp;utm_campaign=2026q2_spring-boot-3-5-eol_global "HeroDevs"), so I have an obvious interest in saying this clearly. I'm saying it anyway, because it's true regardless of who benefits and its a major reason why I'm at HeroDevs.

### Plan it now or scramble later {#h3-8-plan-it-now-or-scramble-later}

Commercial EOL support is just support. that doesn't change your application or the risk of other zombies turning up in other dependencies.

The migration work is the same whether you do it now or later.

The difference is whether you do it on your schedule or the regulator's. Or, if you're lucky, you use the support option to keep the app safe until it times out and gets retired.

The Zombie Problem Has a New Urgency {#h2-9-the-zombie-problem-has-a-new-urgency}
---------------------------------------------------------------------------------

### From backlog item to compliance event {#h3-10-from-backlog-item-to-compliance-event}

Right now it feels manageable to defer. What's the chance of a zombie CVE occuring? Remember that's a CVE thats disclosed upstream, exploitable downstream, but invisible in your scanner.

Do you worry about it when it happens or do something about it now? It will inevitably happen of course but maybe you can just pick up support when it's needed? If that's your plan then make sure, *right now,* that you can get support for every dependency in your stack.

Why? Because not everything may be covered and CVEs can turn up anywhere. Not just in SpringBoot 3.5. If that happens post September 11th, the CRA is likely going to have a say about how and how quickly you remediate.

Planning to rely on support that doesn't exist can be a little embarrassing.

### The clocks ticking {#h3-11-the-clocks-ticking}

When the CRA reporting kicks in, any actively exploited vulnerability in your EOL Spring Boot 3.5 deployment will start a 24-hour clock the moment you become aware of it.

Since only the highest severity CVEs are likely to get flagged on EOL software the difficulty will be that you're only going to see the bad ones, the ones the CRA is really going to require you to report: but there's no upstream patch from the open source project.

That doesn't change the obligation. The CRA doesn't make exceptions for EOL software. If anything, it was written precisely because of it.

The CRA reporting requirement is the first step in the EUs tightening of rules to make commercial software safer. The obligations only get more widespread.

### Migration is not an option {#h3-12-migration-is-not-an-option}

Trying to migrate your way out of a security crisis is not going to be looked upon favorably unless it really really is the last resort.

Make sure you've aware of your options before this happens.

### What to do next week {#h3-13-what-to-do-next-week}

If you're still on 3.5, start the migration conversation this week. Read the [migration guide](https://www.herodevs.com/ebooks/spring-boot-4-0-migration-guide?utm_source=devrel&amp;utm_medium=referral&amp;utm_campaign=2026q2_spring-boot-3-5-eol_global "migration guide"). Run the [EOL scan](https://www.herodevs.com/eol-dataset/eol-data?utm_source=devrel&amp;utm_medium=referral&amp;utm_campaign=2026q2_spring-boot-3-5-eol_global "EOL scan") against your dependency tree.

Just do it {#h2-14-just-do-it}
------------------------------

In fact, regardless of what software stack you have. Whether its Java or something else. Now is the time for getting a handle on what your real estate looks like from a CVE and EOL PoV. You need as much time as possible to make informed decisions on every piece of tech in your supply chain.

### Next time Next time {#h3-15-next-time-next-time}

I'll explain more about HeroDevs EOL data and how to use.

*** ** * ** ***

#### What does "actively exploited" mean in practice, and how would you know?

The CRA doesn't define it precisely. It's a factual question about whether a vulnerability is being used in real attacks in the wild. Several signals help:

CISA KEV is the most authoritative public list. A KEV listing means CISA has confirmed exploitation with evidence. It's not exhaustive, exploitation can precede listing, but it's the clearest public signal and will almost certainly satisfy the CRA trigger.

EPSS (Exploit Prediction Scoring System, maintained by FIRST) estimates the probability of exploitation within 30 days. Updated daily, widely integrated into tooling. High EPSS is a leading indicator; KEV listing is lagging confirmation.

Snyk labels vulnerabilities as "Attacked" when it has evidence of active exploitation in the wild. If a dependency in your project is marked Attacked, treat it as a potential CRA trigger.

Sonatype Lifecycle incorporates EPSS and its own exploitation data from monitoring public repositories and threat feeds, surfacing exploitation probability alongside severity.

OSV Scanner doesn't surface exploitation status directly, but cross-referencing its output with the CISA KEV catalogue gives you the combination.

*** ** * ** ***

Steve Poole is a Developer Advocate at [HeroDevs](https://www.herodevs.com/blog-posts/spring-boot-3-5-eol-migration-calculator-estimate-your-upgrade-timeline-to-spring-boot-4?utm_source=devrel&amp;utm_medium=referral&amp;utm_campaign=2026q2_spring-boot-3-5-eol_global "HeroDevs ")and a Java Champion. [HeroDevs](https://www.herodevs.com/blog-posts/spring-boot-3-5-eol-migration-calculator-estimate-your-upgrade-timeline-to-spring-boot-4?utm_source=devrel&amp;utm_medium=referral&amp;utm_campaign=2026q2_spring-boot-3-5-eol_global "HeroDevs ")provides Never-Ending Support for EOL open-source software including Spring Boot 3.5. This article follows Crossing the River Styx: Spring Boot 3.5 and the Zombie Dependency Problem.
