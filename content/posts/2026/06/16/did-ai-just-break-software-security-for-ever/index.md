---
title: "Did AI Just Break Software Security For Ever?"
slug: "did-ai-just-break-software-security-for-ever"
date: "2026-06-16T14:51:04+00:00"
lastmod: "2026-06-17T08:34:09+00:00"
description: "AI finds exploits in hours. Patch cycles run 30–60 days. EOL software gets neither. Here's what changed in 2026 and what to do about it."
authors:
  - "steve-poole"
image: "Gemini_Generated_Image_oayu0aoayu0aoayu-1024x541.png"
categories:
  - "Java"
  - "Security"
tags:
related_posts:
  - "crossing-the-river-styx-spring-boot-3-5-and-the-zombie-dependency-problem"
  - "spring-boot-migration-and-the-cra-when-good-enough-isnt"
  - "ai-found-the-bugs-whos-patching-your-eol-java-code"
  - "foojay-podcast-95"
frozen: false
---

Whether the answer is yes or no (read on for my opinion) , something fundamental has changed this year. Not one thing. Four things, converging at once.

### First: The rate of CVE arrivals {#h3-0-first-the-rate-of-cve-arrivals}

More than 40,000 CVEs were published in 2024, rising to just under 50,000 in 2025. FIRST --- the Forum of Incident Response and Security Teams, [projects](https://www.first.org/blog/20260522-vulnerability-forecast-update "projects") a median of 68,000 for 2026, with realistic scenarios reaching 70,000 to 100,000.

That's rapid acceleration at least, a horrible trend maybe.

### Second: AI detection of vulnerabilities becoming the norm {#h3-1-second-ai-detection-of-vulnerabilities-becoming-the-norm}

AI models can now scan codebases, reason about attack surfaces, and generate working proof-of-concept exploits in hours.

A year ago that took weeks of skilled manual research. Anthropic's Mythos model and its Project Glasswing partners have found more than 10,000 high- or critical-severity vulnerabilities across critical software systems. Details in Anthropic's Glasswing [research](https://www.anthropic.com/research/glasswing-initial-update).

The exploit-to-disclosure window used to give defenders breathing room. AI has now removed it.

### Third: The breaking of an old asymmetry {#h3-2-third-the-breaking-of-an-old-asymmetry}

That same AI is now overwhelming the volunteers who maintain the software the world runs on.

Daniel Stenberg [shut down](https://daniel.haxx.se/blog/2026/01/26/the-end-of-the-curl-bug-bounty/ "shut down") curl's bug bounty in January 2026 after AI-generated submissions hit 20% of all reports and genuine vulnerability finds collapsed from 15% to 5%.

Jazzband; 84 Python projects, 150 million monthly downloads, [announced](https://simonwillison.net/2026/Mar/14/jannis-leidel/ "announced") it was dissolving in March 2026.

The founder called it GitHub's "slopocalypse."

The infrastructure the world depends on is maintained by fewer people than you think. And those people are burning out.

### Fourth: Regulators got impatient {#h3-3-fourth-regulators-got-impatient}

The EU's Cyber Resilience Act really kicks in this September. The US has CISA mandates and FedRAMP obligations tightening. The UK's Cyber Security and Resilience Bill is working through Parliament. PCI-DSS 4.0, SOC 2, and HIPAA now treat end-of-life software as a named audit finding.

For years, security advice came with an implicit escape clause: best practice, strongly recommended, industry standard. That clause is gone. Shipping software with a known exploited vulnerability is now a legal event in many jurisdictions --- with a 24-hour reporting clock and fines that scale with your revenue.

### The maths no longer works {#h3-4-the-maths-no-longer-works}

Look at these numbers

1. Unit 42's 2026 Global Incident Response [Report](https://www.paloaltonetworks.com/blog/2026/02/unit-42-global-ir-report/ "Report ")found attackers now start scanning for newly disclosed CVEs within 15 minutes of publication. In the fastest cases they investigated, initial access to full data exfiltration took 72 minutes. Four times faster than the previous year.
2. The median time to fully patch a vulnerability is now 43 days and rising, per Verizon's 2026 [DBIR](verizon.com/business/resources/reports/dbir/ "DBIR").
3. The CRA gives you 24 hours to notify ENISA.

None of those numbers is compatible with the others. *Unless* your stack is already current.

Think about Y2K {#h2-5-think-about-y2k}
---------------------------------------

Whether midnight on 1 January 2000 would actually have brought down global infrastructure is still debated. What isn't debated is what happened before that date: governments legislated, boards signed off on nine-figure remediation budgets. I certainly had a fun time leading up to the EVENT. Entire IT departments spent person-years auditing and patching. Hundreds of billions were spent globally. Driven not by actual failures but by the credible possibility of them.

The comparison isn't exact. But AI-assisted vulnerability discovery is doing something structurally similar to software security. The tools are real, the CVE volume is rising, and the regulatory response is already written into law.

### The response doesn't wait for the disaster {#h3-6-the-response-doesn-t-wait-for-the-disaster}

The credible possibility of the flood is already reshaping how boards think about upgrades, how insurers price risk, and what regulators expect. You don't need the event to happen for the consequences to start.

The CRA didn't materialise from nowhere. It's a legislative response to an acceleration that is now visibly happening.

The asymmetry that should worry you {#h2-7-the-asymmetry-that-should-worry-you}
-------------------------------------------------------------------------------

AI-assisted vulnerability research is genuinely good news for well-maintained projects. More bugs found, more patches shipped, faster. For major frameworks, maintainers often have the fix out before the CVE is even public.

### But that research flows to current, active codebases {#h3-8-but-that-research-flows-to-current-active-codebases}

End-of-life versions sit largely outside that loop. No maintainer. No upstream fixes.

Attackers know that most bugs in current versions are present in older versions too. The codebase may have diverged, but the vulnerabilities travelled with it.

CISA added 245 vulnerabilities to its KEV [catalogue](https://www.cisa.gov/known-exploited-vulnerabilities-catalog "catalogue ")in 2025 --- more than 30% above the previous two-year trend. Of those, 94 were from 2024 and earlier, a 34% increase in exploitation of older vulnerabilities. The oldest dated back to 2007. (CISA KEV catalogue, 2025.)

### Current versions get patched. EOL versions get exploited. {#h3-9-current-versions-get-patched-eol-versions-get-exploited}

And AI makes the gap wider faster.

This is not a theoretical risk creeping toward you. It is already the pattern in the exploitation data.

What this means for you right now {#h2-10-what-this-means-for-you-right-now}
----------------------------------------------------------------------------

The upgrade cadence that felt OK two years ago is probably too slow for 2026.

If you can stay current, stay current. Keep the minor upgrades flowing. The compound effect is a codebase that can respond quickly when a CVE drops --- and that often already has the fix before the CVE exists.

### If you're on EOL software, the clock is already running {#h3-11-if-you-re-on-eol-software-the-clock-is-already-running}

You need a plan that isn't "we'll deal with it when something goes wrong."

A major migration cannot be an emergency response to a CVE. The timescales don't align, and I'll show you exactly why in the next piece in this series, using Spring Boot 3.5 to 4.0 as the worked example.

If you need more time than you have, commercial support exists. A disclosure: I work for [HeroDevs](http://herodevs.com/?utm_source=devrel&amp;utm_medium=referral&amp;utm_campaign=2026q2_spring-boot-3-5-eol_global), which provides Never-Ending Support: security patches backported to EOL versions without requiring an upgrade.

HeroDevs isn't the only option in this space, several vendors operate similar services. It's intended to be a bridge: a way to get the safety you need while you address the problem on a longer timescale.

The one option that really doesn't work is waiting and hoping.

Did AI break software security for ever? Not permanently. But it broke the old equilibrium, and the new one hasn't settled yet. The question is whether your strategy reflects that.

### Next Up {#h3-12-next-up}

This is Part 1 of a four-part series. Next: Why You Can't Migrate Your Way Out of a CVE. The real cost of a major migration, and why it cannot be your emergency response plan.



*If you want to see your actual exposure before deciding anything, the HeroDevs EOL Dataset tool is free. scan your dependency manifest and you'll know exactly where you stand. [herodevs.com/eol-dataset](https://www.herodevs.com/eol-dataset/overview?utm_source=devrel&amp;utm_medium=referral&amp;utm_campaign=2026q2_spring-boot-3-5-eol_global)*
