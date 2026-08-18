---
title: "Security Doesn’t Start at Liftoff"
date: "2026-01-23T10:22:31+00:00"
lastmod: "2026-01-23T12:06:07+00:00"
description: "This is a follow-on to the article The Real Mechanics of Vulnerabilities in an Upstream/Downstream, Topsy-Turvy EOL World. What you'll learn in this - by Steve Poole"
authors:
  - "steve-poole"
image: "Gemini_Generated_Image_cuv3ntcuv3ntcuv3-scaled.png"
categories:
  - "Security"
related_posts:
  - "42-practical-java-design-patterns-builder-and-more"
  - "5-great-reasons-to-use-jooq"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "9-outdated-ideas-about-java"
frozen: false
---

This is a follow-on to the article [The Real Mechanics of Vulnerabilities in an Upstream/Downstream, Topsy-Turvy EOL World](https://foojay.io/today/the-real-mechanics-of-vulnerabilities-in-an-upstream-downstream-topsy-turvy-eol-world/).

#### What you'll learn in this article:

* **The Security Timeline Inversion**: CVE disclosure is no longer the true start of the security timeline.
* **Security Outcomes**: Routine maintenance decisions, not reaction speed or tooling, determine security outcomes.
* **Flawed Indicators**: CVE scores, scanners, and compliance deadlines are not effective early-warning systems.
* **Vulnerability Distortion**: Embedded, forked, and end-of-life components obscure vulnerability visibility and responsibility.
* **Your Mission**: Governance and lifecycle changes are necessary to avoid being structurally late to "silent" vulnerabilities.

## Are you sitting comfortably?

In February 2025, Apache Tomcat shipped a set of routine point releases. They arrived without urgency, without commentary, and without the kind of noise that can accompany significant security incidents.

For most teams, there was no immediate reason to treat these releases any differently from other maintenance updates. Nothing appeared broken. Nothing appeared compromised. There was no vulnerability identifier to react to.

A few weeks later, those same releases became the centre of a security emergency.

By the time **CVE-2025-24813** was made public in March, the vulnerable code path had already been identified, corrected, and shipped across supported Tomcat branches.

The fix existed before the problem had a name. For teams that had upgraded in February, the CVE arrived as an explanation for something that had already happened.

For everyone else, it came as a nasty surprise: a CVSS score of **9.8** is a great way to start your day.

## The CVE is (almost) not important

Feel free to look up the details of the CVE here <https://nvd.nist.gov/vuln/detail/CVE-2025-24813>. We'll look at the content later, since what is recorded and by whom is part of this story. The gory technical details of the CVE are mostly irrelevant. The important characteristic is that this CVE doesn't occur in default mode; it has to be enabled.

What the following sequence of events will help us understand is how CVEs can evolve and how that can impact consumers.

## The Inversion of the Security Timeline

This inversion is easy to miss if you only look at disclosure dates.

Security timelines are usually told as if they begin when a CVE is published. In reality, they start earlier. When the problem is reported or when maintainers change code.

When a rocket on the launch pad begins counting down, we see the engines start before the rocket leaves the ground. That's sort of the pattern we see with CVEs.

In this case, the Tomcat security team followed a pattern that is entirely normal for long-lived open-source infrastructure. A private report arrived. The issue was investigated. A fix was developed and shipped. Quietly, and all before the'rocket' left the pad.

Only after patched versions were available did public disclosure occur. The intention was not secrecy, but containment: The aim is to reduce the window in which defenders have no viable response.

## Habit vs. Hype

That approach works well for organisations that treat point releases as something to be applied routinely.

Those teams upgraded without knowing why, and by the time the vulnerability was publicly discussed, they were already protected. The upgrade decision that mattered had been made weeks earlier and as part of an ongoing plan.

For organisations that rely on disclosure as the trigger for action, March felt like a zero-day. The vulnerability appeared fully formed, complete with severity scores, exploit claims, etc.

From the outside, it looked as though everyone was confronting the same risk at the same time. In reality, the outcome had already diverged.

Two teams could have been running identical Tomcat versions in January and ended up in very different positions by mid-March.

One was insulated, the other exposed.

The difference was not threat intelligence, tooling, or awareness. It was *habit*.

## The Flawed Assumption of Loud Alerts

There is a persistent assumption in many engineering organisations that serious security issues will announce themselves loudly enough to demand attention.

That assumption is increasingly unreliable.

In this case, the most crucial indicator was indistinguishable from routine maintenance. A changelog entry. A version bump. A release that did not feel urgent because nothing had yet told anyone that it should be.

Once the CVE was published, the usual narrative took over. The vulnerability acquired a name, a score, and a target audience. Discussions about severity, exploitability, and configuration began immediately. But by then, the window for quiet safety had already closed.

## Prioritising Changes Over Stories

It is tempting to view those February releases as fortunate timing.

They were not.

They were simply the result of a process that prioritises fixing code over managing perception.

The discomfort comes from realising how many organisations are unknowingly structured to respond to stories rather than changes, to declarations rather than diffs.

By the time the wider ecosystem began debating the implications of **CVE-2025-24813**, the technical outcome had already been set. Some systems were safe, others were not, and the distinction had nothing to do with how quickly anyone reacted in March.

What followed was not a single, shared reality. How this CVE (and to be honest, the vast majority of all CVEs) rolled out across the world was a collision of interpretations, assessments, and obligations.

That is where this case study becomes interesting and makes such a great sample.

## The main timeline

There's a lot to unwrap.

The critical elements all circle around the NVD's CVE and CPE information. Which you can find here <https://nvd.nist.gov/vuln/detail/CVE-2025-24813> I need to stress that this is not at any point intended to suggest anything untoward happened. *This is a worked example of how things are.*

#### January 13, 2025. Vulnerability Privately Reported

The Apache Tomcat security team receives a private report of a vulnerability involving partial HTTP PUT requests and the default file servlet. The flaw affects file upload handling when writes are enabled.

#### January 24th 2025: Fix committed.

The commit <https://github.com/apache/tomcat/commit/0a668e0c27f2b7ca0cc7c6eea32253b9b5ecb29c> was recorded, and, as usual and expected, there's no indication that it fixes a security flaw. The associated pull request is named "Enhance lifecycle of temporary files used by partial PUT"

#### February 10th, 2025: Fix released silently.

Apache quietly releases Tomcat 9.0.99, 10.1.35, and 11.0.3 with the fix included. That's one month before public disclosure. Organizations routinely updated were protected early.

#### March 10, 2025: CVE-2025-24813 Publicly Disclosed

Apache publicly discloses the vulnerability via the OSS security mailing list, rated "Important" with potential for RCE and information disclosure. Discovery credited to COSCO Shipping Lines DIC and researcher "sw0rd1ight".

The posting to the NVD system (<https://nvd.nist.gov/vuln/detail/CVE-2025-24813>) cites  

"This issue affects Apache Tomcat: from 11.0.0-M1 through 11.0.2, from 10.1.0-M1 through 10.1.34, from 9.0.0.M1 through 9.0.98."

#### March 13--14, 2025: PoC Exploits \& Active Attacks Begin

Within 30 hours of disclosure, proof-of-concept exploits appear publicly. Mass scanning and exploit attempts begin immediately. Attack method: upload JSP webshell via partial PUT, trigger via crafted JSESSIONID request.

*Sources: Sonatype Blog \| CyRisk Analysis*

#### 17 March, 2025: A POC exploit is added to the NVD CVE record.

<https://github.com/absholi7ly/POC-CVE-2025-24813/blob/main/README.md>

#### 18th March 2025: The first CPE information is added to the CVE

The CPE <https://nvd.nist.gov/products/cpe> is the magic information that makes the CVE basically machine readable. Its lets automated consumers of the CVE 'know' what versions of the product are vulnerable.

At this point the CPE simply matches the original version range declaration we saw earlier.

#### March 19--26, 2025: Security Vendor Analysis Published

Rapid7, Akamai, Wiz, and Sonatype publish detailed analyses.

Rapid7 notes "no need to panic" as widespread exploitation was unconfirmed, and some of Apache's stated prerequisites were overstated. Sonatype reports \~100,000 downloads of vulnerable versions post-disclosure.

*Sources: Rapid7 Blog \| Akamai Blog \| Wiz Analysis*

#### April 1, 2025 :CISA Adds to KEV Catalog

The CVE records that CISA adds**CVE-2025-24813** to its Known Exploited Vulnerabilities catalogue, citing evidence of active exploitation.

U.S. Federal agencies have been given an April 22, 2025, deadline to patch. Amazon AWS also releases ALAS-2025-2812 the same day.

*Sources: Keysight Analysis \| Amazon Linux Advisories*

#### Early April 2025 : Enterprise Vendor Patches Released

Red Hat, Amazon, SUSE, Atlassian, and other enterprise vendors release patches. Most emphasise that default configurations are not vulnerable since the default servlet is read-only.

The CPE entry gains an debian tomcat entry.

*Sources: Red Hat CVE \| Amazon CVE*

#### July 3, 2025: Unit 42 Reports 125K+ Attack Attempts

Palo Alto's Unit 42 reports 125,856 exploit attempts blocked in March 2025 alone. Most exploitation traffic appears "opportunistic" using public PoCs by low-sophistication actors. No major confirmed breaches attributed specifically to this CVE.

*Source: Unit 42 Report*

## Are we done?

Well, for some set of Tomcat users , those on versions 9,10 or 11 everythings ok. They have a fix and as long as they upgrade, then they are safe. But there is more to tell

#### August 7, 2025 - CVE updated " Older, EOL versions may also be affected."

Due to some lobbying led by HeroDevs, the Apache team added a warning. This is important. This very rarely happens, but there were engineers in the mix both at Apache and elsewhere who felt that the warning was needed. The CPE was updated, but it did not gain any information about the EOL versions, as there was nothing specific to include.

#### August 8, 2025 - CVE Updated with more information about EOL versions

The CVE now states, *"The following versions were EOL at the time the CVE was created but are known to be affected: 8.5.0 through 8.5.100."* Now the CVE is officially applicable to another version stream of Tomcat. Unfortunately, the CPE was not updated.

#### Jan 2026

Even today, this CVE hasn't been updated to include the CPE information that might help a scanner detect a problem in version 8.5.

Thats not particularly unusual.

The CPE doesn't include information about many of the Linux distros or any of the products that embed Apache Tomcat.

Some of that can be attributed to the nature of the CVE.

Since the vulnerability is only possible if a default configuration is changed. A simple *readyonly=true* changed to *readonly=false*.

That means, in theory, exploitability depends on the Tomcat user being able to make this change. If Tomcat is embedded in any way, this config can't be changed, so the 'embedding' product may rightly claim they are not vulnerable...

However, there are many forks and commercial variants of Apache Tomcat. For instance It's a component of Spring Boot, and Broadcom Tanzu embeds it too. How many fat jars are out there where the component information has been lost, or the project POM has been updated since shipping?

## How the message dilutes

The frequently bitter truth is that CVE reporting and getting appropriate awareness to the right people fails. In fact the more popular a component is to be forked or embedded the harder it can be to reach those who need to know.

Often, in the heat of the moment, downstream vendors reach to the initial CVE report but dont come back for updates. The Tanzu support page  
<https://support.broadcom.com/web/ecx/support-content-notification/-/external/content/SecurityAdvisories/0/25536>

Rightly spells out that the CVE is applicable to Tanzu and gives it its own identifier **TNZ-2025-014**. They alert their users to the CVE being applicable to versions 9,10 and 11, but then, as always, it gets messy.

The Tanzu suite is sophisticated and complex. Suddenly, the CVE, rather than being a simple *are-you-exposed or not* turns into a list of maybes and false positives.

I don't know if Tanzu has versions with Tomcat 8 or earlier - I assume they do. Given their advisory is dated 2025-03-25, well before the CVE had the additional EOL version info added one can sympathise with this omision even if it could be a major exposure for older Tanzu products,

## A common scenerio

By the time CVE-2025-24813 acquired a name, a score, and a media cycle, the outcome had already diverged.

The fix existed before the CVE. Some systems were protected before anyone knew why. Others would remain exposed no matter how fast they reacted in March.

That divergence did not come from awareness, tooling, or any threat intelligence. It came from timing. More precisely, from an assumption that security begins when a vulnerability is declared, rather than when code quietly changes.

What followed was not a single event but a prolonged cascade. The CVE text evolved. CPE data arrived late and incompletely. Vendor advisories reframed the scope. Compliance deadlines hardened partial truths into policy. Updates about EOL exposure appeared months later, after many downstream decisions had already been made.

None of this was malicious. None of it was unusual. It is simply how the modern vulnerability ecosystem behaves when it treats disclosure as a moment rather than a process.

**CVE-2025-24813** did not "happen" in March. It happened in January, February, March, August, and beyond. Depending on where you sit and which signals you rely on.

The uncomfortable reality is that by the time the wider ecosystem had finished debating severity and exploitability, the technical outcome had already been fixed.

How should you react?

## What This Means to You and What to Do Next

Much as I dislike the term "uncomfortable truth" its the one used most often in these circumstances. So the "uncomfortable conclusion" of this case study is not that CVE-2025-24813 was unusually dangerous. It is that *nothing about it was unusual at all*

The vulnerability did not arrive with any fanfare. It did not announce itself as urgent. It was resolved before it was named. The systems that survived did so not because they reacted faster, but because they had already decided, long before, how they treat change.

If your security posture depends on knowing why an update matters before you apply it, then you are already operating too late.

**In modern ecosystems, explanation follows action, not the other way around.**

## What this means in practice

**First**, disclosure is no longer the start of the security timeline.

By the time a CVE acquires a name, a score, and eventually a compliance deadline, the decisive technical work has often already happened elsewhere. If your organisation treats disclosure as the trigger for motion, you are structurally exposed to silent fixes you will never prioritise in time.

**Second**, scanners do not give you foresight, they just tell you you're late.

CVE databases, CPEs, and KEV catalogues are coordination tools.

They help large ecosystems move together once a narrative stabilises. They do not tell you when the risk actually entered your codebase, nor when it quietly left. Treating them as early-warning systems guarantees blind spots.

**Third**, "default safe" is not a strategy.

Many teams took comfort in the idea that this vulnerability required a non-default configuration. That distinction collapsed the moment embedded distributions, forks, and fat-JAR deployments entered the picture.  

If you cannot prove how a component is configured in production, you cannot rely on upstream assumptions about safety.

**Finally**, habit beats heroics.

The difference between the protected and the exposed systems in this story was not tooling, threat intelligence, or response speed. It was whether routine maintenance had been institutionalised before anyone knew it mattered.

## What you should do next

Do not wait for the next CVE to test these assumptions. You already have enough information to act.

#### 1) Start by identifying where your upgrade decisions come from.

If updates are prioritised only after a vulnerability is named, scored, or mandated, that is a governance and control problem, not a technical one.

Fixing it requires changing when decisions are made, not which tools are consulted.

#### 2) Examine how you treat "quiet" releases

Ask which point releases in your environment would be applied automatically, and which would be deferred until justified. Every deferral you make is a bet that the justification will arrive before the exploit does.

#### 3) Map responsibility for end-of-life components explicitly.

If a dependency is out of upstream support, the risk is no longer abstract or future-dated. Security responsibility has already shifted. It now sits either with you, with a downstream vendor, or with nobody at all—and that ambiguity is itself a form of exposure.

I should be explicit about my own position here. I work for a company that provides extended support for end-of-life open-source components. That is one way, sometimes a very practical one, to regain control when vulnerabilities appear in older software. It can buy breathing space. It can stabilise a system long enough for thoughtful decisions to be made.

But it is not the only option, and it is not a substitute for understanding how you ended up here.

Extended support is a safety net, not a strategy. So are accelerated upgrades, compensating controls, isolation, or architectural change. All of them are valid responses in different contexts. The real failure mode is not choosing any response. Drifting into a state where no one is clearly responsible, and no response is actually owned is the wrong choice.

End-of-life does not mean *"unsafe by default."* It means the burden of proof has moved. Someone now has to actively carry security responsibility. If you cannot point to who that is,or explain how that responsibility is being met, then the risk is already present, whether a CVE has your attention or not.

#### 4) Finally, treat security as a lifecycle, not an event

**CVE-2025-24813** unfolded over months, not days. So do most real vulnerabilities. If your processes only engage at the moment of disclosure, you are optimising for narratives rather than outcomes.

## Security does not start at liftoff.

By the time the countdown reaches zero, the engines have already been burning for a long time. The only question is whether you noticed, and whether you built your systems to move before someone told you why.
