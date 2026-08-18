---
title: "AI Found the Bugs. Who's Patching Your EOL Java Code?"
date: "2026-07-23T15:26:53+00:00"
lastmod: "2026-07-23T15:30:40+00:00"
description: "AI finds decades-old flaws at machine speed, but the fixes only reach supported versions. What that means for end-of-life Java, and what to do."
authors:
  - "steve-poole"
image: "Gemini_Generated_Image_kms68akms68akms6-1024x541.png"
categories:
  - "AI"
  - "Java"
  - "Security"
related_posts:
  - "did-ai-just-break-software-security-for-ever"
  - "foojay-podcast-95"
  - "crossing-the-river-styx-spring-boot-3-5-and-the-zombie-dependency-problem"
  - "7-new-vulnerabilities-in-jackson-in-one-day-this-is-what-ai-assisted-security-research-looks-like"
frozen: false
---

Earlier this year an AI model found a flaw in OpenBSD's TCP stack that had been sitting there for 27 years. The same scanning run turned up a 16-year-old bug in FFmpeg's H.264 code.

#### How do you feel about AI being used to discover bugs in open source, across the board and at scale?

Because that's what's happening. It's tough enough dealing with machine-speed vulnerability discovery in code that has active maintainers. What about all the code with no friendly pair of safe hands?

I've spent the past few weeks fact-checking the flood of claims about AI and vulnerability discovery. I expected to find hype, and there's plenty. Strip it away and the numbers are still alarming.

## Discovery Has Stopped Being the Hard Part

The headline figures come from Anthropic's [Project Glasswing update](https://www.anthropic.com/research/glasswing-initial-update). Their frontier model scanned over 1,000 open-source projects and flagged more than 23,000 potential vulnerabilities.

Independent security firms have triaged a first set of the high and critical findings and confirmed roughly 90% as genuine.

## 530 Bugs Disclosed. About 75 Fixed.

Of roughly 530 high or critical bugs disclosed to open-source maintainers, only about 75 had been patched in the first months.

Some of that gap is understandable. The telling part is that maintainers, most of them volunteers, have asked Anthropic to slow down because they can't keep pace.

Patching one high-severity bug takes (acording to Anthropic) about two weeks of skilled human effort, A machine finds them in seconds. You can do the arithmetic yourself.

This is the new era: **machine-speed vulnerabilities, human-speed fixes.**

## The NCSC Says Brace for a Patch Wave

The UK's National Cyber Security Centre has already seen where this leads. In May, NCSC CTO Ollie Whitehouse [warned every organisation](https://www.ncsc.gov.uk/blogs/prepare-for-vulnerability-patch-wave) to prepare for a "vulnerability patch wave": a rush of software updates across the whole stack, as AI performs a "forced correction" of decades of technical debt.

The advice is sensible but painful. Adopt update-by-default. Patch your perimeter first. Automate everything you can.

If everything you run has a support mechanism behind it, whether the default option from the open-source maintainers or a commercial contract, you're halfway there.

The other half is automated build, QA and deploy systems.

The quantity of change heading our way is unparalleled. If you need humans to apply and validate every update, there's trouble ahead.

## Read the Small Print on the Rescue Plans

The Linux Foundation [launched Akrites in June](https://www.prnewswire.com/news-releases/linux-foundation-and-industry-leaders-launch-akrites-to-defend-critical-open-source-software-against-ai-enabled-cyber-threats-302811165.html): a shared security incident response team for open-source maintainers, backed by AWS, Google, Microsoft, Anthropic, OpenAI and others.

Chainguard's [Athena coalition](https://www.chainguard.dev/athena) does similar work for its members.

The US White House now runs [its own vulnerability clearinghouse](https://www.whitehouse.gov/releases/2026/07/white-house-launches-gold-eagle-initiative-for-unprecedented-cybersecurity-vulnerability-coordination/).

The aim is that fixes get validated, deduplicated and coordinated out to maintainers, which is much better than burying them under thousands of PRs.

Nobody wants consortiums forking projects against maintainers' wishes but its a possibility.

The consequence still applies though: the better the industry gets at fixing supported software, the more exposed the unsupported estate looks by comparison.

## Fixes Arrive on the Maintainers' Terms

Why? Read how Akrites [describes its own process](https://www.prnewswire.com/news-releases/linux-foundation-and-industry-leaders-launch-akrites-to-defend-critical-open-source-software-against-ai-enabled-cyber-threats-302811165.html). Fixes flow back into each project's original home, *on the maintainers' terms* . Even its maintainer-of-last-resort clause, for packages with nobody left at all, commits to fixes for the *latest* version.

#### So what are maintainers' terms, exactly, for the versions you run?

Spring's upstream team fixes supported branches; they are not going to resurrect Spring Boot 2.7. The Jackson project patches current streams. AngularJS has been archived for years.

When the clearinghouse machinery routes a coordinated fix upstream, it going to arrive in the version(s) upstream the maintainers still support.

EOL versions are unlikely to get a fix, or even a mention in the CVE, because maintainers as a group do not raise CVEs against streams they will not fix.

The bad guys will know though.

Your open source estate is getting more dangerous and, by design, nobody is going to tell you.

## For Most of Maven Central, There's Nobody Home

To put this in context, I ran the numbers on Maven Central. Of roughly 850,000 packages I looked at, over half have shipped nothing in two years.

Whether those projects are finished or abandoned makes no difference to you; neither kind is turning a CVE around in fourteen days. When the clearinghouse machinery goes looking for an upstream, for most of the repository there's nobody home.

## Why Java Teams Sit in the Impact Zone

None of this is unique to Java, mind, Sonatypes analysis from about 2024 showed that only about 11% of all open source projects (across all ecosystems) had any semblance of maintenance activity.

Since this is Foojay let's look at Java more closely.

First, the installed base. Java's great strength is that applications run for decades. That means an enormous population of Spring Boot 2.x, old Jackson, legacy Struts and Java 8 still doing solid work in production. If a shop is running old versions of Java, expect older versions of most other software too.

Azul's [2025 State of Java survey](https://www.azul.com/blog/2025-state-of-java-survey-confirms-majority-of-enterprise-apps-are-built-on-java-2/) found nearly one in five organisations still running Java 6 or 7. Java 6 shipped in 2006 (I remember it fondly).

## Old Code Is Not a Hiding Place

AI scanning digs through old code as happily as new. That 27-year-old OpenBSD bug should tell you the models don't care how long something has been sitting there undisturbed. It's whatever someone feeds them. And of course there's great PR in discovering ancient bugs: *look how powerful AI is, finding problems humans missed for decades.*

It may be PR but there's an underlying truth. AI is finding more bugs than humans, at industrial speed. If you think it's going to peter out, I have bad news. AI is only finding the simplest bugs at the moment. We haven't got anywhere near the harder issues that cross module boundaries.

Why spend money hunting the complex flaws when there's a wealth of treasure in mining the simple ones?

## The Pattern Is Already Visible in Spring

The Spring ecosystem published [67 CVEs in June alone](https://www.herodevs.com/blog-posts/spring-cves-didnt-slow-down-june-2026-brought-67), 27 of them High severity. The whole of 2025 produced 17.

The real exposure sits in the versions Broadcom no longer evaluates for impact: the backlog on Spring Boot 2.7-generation projects stands at 97 CVEs in the [vulnerability directory](https://www.herodevs.com/vulnerability-directory?utm_source=devrel), and it only grows. A [batch of seven jackson-databind deserialisation CVEs](https://www.herodevs.com/blog-posts/cve-2026-54512-54513-jackson-polymorphictypevalidator-bypass?utm_source=devrel) earlier this year told the same story.

## Attackers Moved First. Auditors Are Right Behind.

CrowdStrike's [2026 Global Threat Report](https://www.crowdstrike.com/en-us/press-releases/2026-crowdstrike-global-threat-report/) shows a 42% rise in vulnerabilities exploited *before* public disclosure.

Attackers also [reverse-engineer published patches within hours](https://www.anthropic.com/research/glasswing-initial-update). If a fix ships for Spring 6 and your app runs Spring 5, that patch commit is a free exploit roadmap pointing straight at you.

Compliance regimes have noticed too. In the UK, the [Cyber Essentials update that took effect in April](https://www.nccgroup.com/major-cyber-essentials-changes-coming-april-27-2026-what-organisations-need-to-know/) makes missing the 14-day window for critical patches an automatic certification failure. Unsupported software on an in-scope device fails you outright. The new question set has someone at director level signing a declaration to keep the controls maintained for the year. Somebody's name is on this now.

US federal suppliers face [CISA's new risk-based directive](https://industrialcyber.co/cisa/cisa-bod-26-04-directs-agencies-to-prioritize-exploited-vulnerabilities-and-assess-compromise-before-patching/), with 72-hour windows for the worst cases. Remember that the auditors' scanners read the same CVE feeds the attackers do.

## Four Practical Steps

**Inventory your EOL exposure properly.** Most SCA tools have nothing to say about support status. You may get a CVE warning but resolving it is your problem. What you actually need to know is which of your dependencies have an upstream that would ship a fix. A dedicated end-of-life dataset closes that gap. Full disclosure: I work for HeroDevs and we have that [data](https://www.herodevs.com/eol-dataset/overview?utm_source=devrel).

**Adopt update-by-default where the upstream is alive.** The NCSC is right about this. If you're on supported versions, automate the boring patches and save your humans for the hard ones.

**Make an explicit decision for every EOL component.** There are four options. Migrate now. Self-patch, which means forking the code and owning it forever. Buy commercial support, such as HeroDevs Never-Ending Support, for the EOL versions. Or isolate the system and accept the risk in writing (your auditor will want to see the writing)

Each one is right in the right situation.

Drifting along with none of them is the only wrong answer, and it's the default answer in most organisations. The NCSC's guidance says legacy systems must be replaced or returned to vendor support.

**Compress your response time assumptions.** Whatever your patch SLA was in 2024, it's wrong now. Plan for critical fixes that need deploying in days, and for CVEs arriving in clusters rather than a steady drip.

## The Water Is Already Rising

The patch wave gets talked about as a future event. For end-of-life Java it started months ago; the jackson-databind batch and June's Spring numbers are what the leading edge looks like. The clearinghouses, the consortiums and the AI labs are building an industrial discovery and remediation system.

A system for software that has someone left to remediate it.

*Data note: Maven Central analysis covers 849,571 packages and 20.9 million published versions. A package is counted as inactive if it has published no release of any kind in the past two years: 457,087 packages (54%) meet that definition. Crude heuristic, deliberately so; a project that hasn't shipped anything in two years is not shipping your security fix in fourteen days.*

*Steve Poole is a Java Champion and Developer Advocate at HeroDevs, which provides security support for end-of-life open source software. He writes at noregressions.dev.*
