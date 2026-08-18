---
title: "Spring Boot 3.5 EOL — The CVE Blind Spot Nobody Talks About"
slug: "crossing-the-river-styx-spring-boot-3-5-and-the-zombie-dependency-problem"
date: "2026-04-19T13:37:13+00:00"
lastmod: "2026-04-19T14:25:30+00:00"
description: "Spring Boot 3.5 goes EOL June 30, 2026. But the real risk isn't the migration. It's what happens to CVE reporting once a project reaches end of life."
authors:
  - "steve-poole"
image: "Gemini_Generated_Image_4mkc4s4mkc4s4mkc-1024x559.png"
categories:
  - "Java"
  - "Security"
  - "Spring"
tags:
related_posts:
  - "did-ai-just-break-software-security-for-ever"
  - "foojay-podcast-95"
  - "ai-found-the-bugs-whos-patching-your-eol-java-code"
  - "spring-boot-migration-and-the-cra-when-good-enough-isnt"
frozen: false
---

Tomorrow I start (o so early) for [JCON Europe](https://2026.europe.jcon.one/) in Cologne and then, at the tail end of the week, go to Devoxx France to give more talks. If you're at either, come say hi. Herodevs has a booth at both.

After digging into the CVE stories behind [Tomcat 8.5's end of life](https://foojay.io/today/the-real-mechanics-of-vulnerabilities-in-an-upstream-downstream-topsy-turvy-eol-world/), I turned my attention to Spring Boot 3.5. Same question, different framework: what *actually* happens to your security posture when a project crosses the EOL line?

The CVE Blind Spot
------------------

Most of us understand the *idea* of a CVE. A vulnerability gets discovered, reported, assigned a severity score, and patched. We run our scanners, check our dashboards, update our dependencies. The system works.

Except it doesn't. Not after 'End Of Life'.

It seems we all have a collective blind spot about *where CVEs come from*. We think about the output: the advisory, the patch, the scanner alert. We rarely think about the process or the people who do this work. Who finds vulnerabilities? Who reports them? Who assigns the CVE identifier?

And critically: what happens to that pipeline when a project reaches end of life?

The answer is that it dries up. Not all at once. Not even dramatically. It just... stops.

The River Styx
--------------

Think of moving from active development and maintenance into EOL mode as crossing the River Styx. On the living side, you have maintainers actively looking at the code. Security researchers submitting reports. A CNA (CVE Numbering Authority) assigning identifiers. A disclosure process that, for all its flaws, at least functions.

On the other side? Silence.

The vulnerabilities don't stop existing. The code doesn't magically become secure because nobody's maintaining it. What stops is the *reporting*. Researchers move their attention to supported versions. Maintainers stop triaging issues against the older branch. Fewer reports reach the CNA. Fewer identifiers get assigned for a codebase nobody's going to patch.

Those on the living, active side know about problems downstream. They can see the vulnerable patterns in the dead code. But they tell no one in any readily discoverable way. There's no obligation to, and no mechanism for it. They don't report the problem because they have no intention of fixing it.

That's been the model forever.

It's actually amazing that any of the problems are fixed at all. I'm certainly not pointing fingers at anyone to say that the way this has worked before was wrong. I'm always grateful to the people who develop and share their creations. Open Source is, well, amazing, and our developer lives would be immeasurably worse off without it.

### The Rules Changed. The Habits Didn't.

However, the world has changed and open source is being weaponised against us. Our old certainties are being destroyed, diluted, compromised in the face of the relentless army of bad actors. When once it was ok to accept that EOL meant 'stable' and meant nothing-to-see-here-move-on, well now that's not true.

The maintainers' muscle memory says that **not** reporting a CVE against an EOL stream is the right thing to do (because they have no intention of fixing it). That muscle memory now works against us.

The bad actors? They see everything...

They watch the CVEs reported on maintained streams, take the juicy ones, and try them against the older EOL streams.

And voilà: a compromise that the maintainers are conceptually aware of but that's not in any CVE database. A free ride for the bad actors.

### What This Looks Like in Practice

A vulnerability exists in both the supported and the EOL branch. On the supported side, a researcher finds it, reports it, gets a CVE assigned, ships a patch.

On the EOL side? The same vulnerability sits in the same code. But fewer researchers are looking. Fewer reports get filed. The vulnerability doesn't appear in your scanner results. Not because it doesn't exist, but because nobody filed the paperwork.

When Dependencies Become Zombies
--------------------------------

Pretty quickly the public CVE count against an EOL project drops. If you're lucky, it's because there are none to be found. The codebase is what we'd traditionally call *stable* . But it's more likely the software didn't get safer. All that happened was the system that *records* the problems wound down.

Nobody, to my knowledge, has done a rigorous study of this effect. But ask anyone who works in open-source security support. It's the pattern they see every time. It's the core reason companies like the one I work for exist.

Your dependencies end up in one of two states: actually stable, or more likely, **zombies**. Out of support and with hidden CVEs accumulating. Technically present in your stack. Functionally dead from a security standpoint. Slowly deteriorating whilst your scanners give you a green light.

We need to stop thinking silence means stability. It's frequently the opposite.

Spring Boot 3.5: The Next Crossing
----------------------------------

Spring Boot 3.5 reaches end of open-source support on June 30, 2026. That's roughly 80 days from now.

When it crosses that line, it doesn't go alone. Spring Framework 6.2, Spring Security, and the entire Spring portfolio lose community patches simultaneously. The CVE reporting pipeline protecting a vast number of Java applications starts winding down for these versions.

### We've Seen This Film Before

Spring Boot 2.7 went EOL in November 2023. Since then, multiple CVEs have surfaced for that branch. CVE-2024-38807, for example: a signature spoofing vulnerability in the boot loader. No open-source patches available. Teams still running 2.7 have to find the fix themselves, pay for commercial support, or accept the risk.

And the longer 2.7 sits in EOL, the quieter the CVE stream gets. Not safer. Quieter. Maybe you can hear the sound of dragging feet...

Based on that pattern, it's incredibly unlikely Spring Boot 3.5 *won't* follow the same trajectory. The transition from stable to zombie isn't a question of "if." It's a question of how fast the reporting pipeline dries up once the maintainers shift focus to 4.0.

### The Window Is Open. For Now.

But here's the thing: it doesn't happen overnight. There *is* time. The zombie transition is gradual, and that window matters.

The quicker you assess the scale of the change from 3.5 to 4.0, the better positioned you'll be. Maybe that means migrating on your own terms. Maybe it means arranging commercial support to bridge the gap, or finding another path entirely. The worst move is to wait until the silence sets in and assume everything is fine.

The Map, Not Just the Landscape
-------------------------------

That's the landscape. Now let's talk about the map.

In my recent [JDK 8 to 25 review](https://noregressions.substack.com/p/how-to-migrate-from-java-8-to-java), I started to walk through every major change across seventeen years of Java releases. I map out what teams actually face when they finally modernise. I'm going to do the same for Spring Boot 3.5 to 4.0.

In the coming articles, I'll cover the technical challenges organised by severity. The obvious compilation errors, the runtime failures and hidden behavioural changes that may slip past your test suite. I'll look at the costs, explore the alternatives, and break down what a realistic migration timeline looks like.

The zombie transition is coming for Spring Boot 3.5. The only question is whether you'll be ready for it or surprised by it. If you're at JCON or Devoxx France this week, come find me at the HeroDevs booth. I'd love to swap migration war stories.



*Steve Poole is a Java Champion, Oracle ACE and IBM Champion. Also a developer advocate at [HeroDevs](https://www.herodevs.com/), and author of the [No Regressions](https://noregressions.substack.com/) newsletter. Find him at the HeroDevs booth at JCON or Devoxx France*
