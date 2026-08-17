---
title: "FOSDEM 2026: Open Source Supply Chains, CRA Compliance, and the Future of Software"
slug: "fosdem-2026-and-the-open-source-firehose"
date: "2026-02-02T22:52:20+00:00"
description: "Is open source hitting a watershed moment? After 8,000 developers converged for FOSDEM 2026, it’s clear that legal obligations and supply chain security are rewriting the rules. Discover why the hardest problems are no longer technical—they’re about how we sustain and secure the software we ship."
authors:
  - "steve-poole"
image: "Gemini_Generated_Image_5oqhzv5oqhzv5oqh.png"
categories:
  - "AI"
  - "Conference"
  - "Events"
  - "Trip Reports"
tags:
related_posts:
frozen: false
---

*(a.k.a. "So... what did you do this weekend?")*

I'm back from four days in Brussels and the uniquely exhausting experience that is FOSDEM. Mentally fried. Physically wrecked. Entirely glad I went.

The Chaos and the Crowd {#h2-0-the-chaos-and-the-crowd}
-------------------------------------------------------

Around 8,000 people made the trip this year. Some arrived for the first time; many returned as annual pilgrims.

FOSDEM remains a strange outlier in the modern conference world: free to attend, no registration gatekeeping, and no sponsors dictating the tone. You just turn up.

Its official description, *"a free event for software developers to meet, share ideas and collaborate"*, is technically accurate and utterly inadequate.

### FOSDEM isn't a conference so much as a live snapshot of the open source ecosystem in action. {#h3-1-fosdem-isn-t-a-conference-so-much-as-a-live-snapshot-of-the-open-source-ecosystem-in-action}

In 2026, that snapshot featured 1,000+ sessions and nearly 1,200 speakers across \~70 tracks. Organizers squeezed them into 37 rooms on the Université Libre de Bruxelles Solbosch campus. Do the maths, and you quickly realise something important: you are not there to consume everything. You are there to sample the firehose.

Navigating the Firehose {#h2-2-navigating-the-firehose}
-------------------------------------------------------

The organizers stream and record most talks (<https://video.fosdem.org/2026/>), which is a mercy. Physically moving between rooms is often futile. Staff strictly enforce capacity limits; you can arrive 30 minutes early and still lose out. The organisers never compromise on safety or timing. That discipline is the only reason the whole thing works at all.

### So why go in person when you could watch from your sofa? {#h3-3-so-why-go-in-person-when-you-could-watch-from-your-sofa}

Because of who fills the rooms. When you strip away the PR layer, FOSDEM brings together the real movers: project founders, maintainers, and foundation reps. You'll find lawyers, researchers, and distro builders sitting next to compiler engineers, students, and the people quietly keeping production systems alive.

No one pitches here. You get progress, blockers, failed experiments, and difficult questions. All aired publicly. Where else can you hear CycloneDX and SPDX contributors debating SBOM reality rather than policy theory? Where else can you ask a CRA-focused lawyer naïve but necessary questions without being sold a compliance product?

FOSDEM is the heartbeat of open source. It is a reliable way to distinguish what's real from what's hype. Ideas that survive here tend to matter. Ideas that don't... don't.



Trends and Tensions in 2026 {#h2-4-trends-and-tensions-in-2026}
---------------------------------------------------------------

### What did FOSDEM 2026 actually tell us? {#h3-5-what-did-fosdem-2026-actually-tell-us}

The familiar themes all appeared: AI, software supply chains, security, and the CRA. We saw robotics, compilers, Linux distros, and embedded systems. If it's open source---from QA and mobile to radio and model railways---it was likely there.

I learnt a lot and met new people. I discussed the latest developments with experts in the areas I care about and came away with plenty to think about.

One telling gap appeared this year: no dedicated Java room. While a small Java presence remained across the tracks, the usual JVM-centric room was missing. I spent time with the Foojay.io folks discussing whether that absence matters.

For 2027, we're considering a proposal for a Java Users room. We want to focus deliberately above the JVM and JDK, looking at how Java works in modern systems, supply chains, CI/CD, and regulated environments.

### FOSDEM 2026 made one thing clear: {#h3-6-fosdem-2026-made-one-thing-clear}

The hardest problems aren't language features anymore. They are how we build, ship, and sustain software. AI included.

Security as Engineering, Not Defense {#h2-7-security-as-engineering-not-defense}
--------------------------------------------------------------------------------

Security conversations threaded the event, though they often appeared indirectly. Speakers framed them as good engineering rather than reactive cyber defence. We talked about hardening build systems, reducing attack surfaces, and improving dependency hygiene. It felt less like "panic patching" and more like "this is how things should have been built."

A noticeable urgency accompanied this, occasionally bordering on anxiety or a shared weariness. Technical collaboration is losing the race against legal obligation. The work needed to make software ecosystems safer is real and ongoing, but compliance deadlines now drive it more than shared engineering goals.

The Economics of "Free" {#h2-8-the-economics-of-free}
-----------------------------------------------------

### Open source is approaching a watershed moment. {#h3-9-open-source-is-approaching-a-watershed-moment}

Legislation like the Cyber Resilience Act (CRA) is reshaping expectations, responsibilities, and liability. Yet funding models for this work remain unclear, fragmented, or missing entirely.

Maintainers feel it. Foundations feel it. Downstream users are only just starting to notice.

One standout talk captured this tension perfectly: Michael Winser's *"The Terrible Economies of Package Registries."* He provided an evidence-rich look at how the assumption that "open source is free" collapses.

Once you factor in infrastructure, security, compliance, and abuse prevention, the math changes.

### As open source becomes *less free* in economic terms... {#h3-10-as-open-source-becomes-less-free-in-economic-terms}

Organisations are becoming more selective about what they use. Pay-to-download might not change behaviour first, but the equations are shifting. Our relationship with open source is changing because the world is changing.

AI accelerates this pressure; legislation compounds it. Together, they are distorting the economics of open source consumption in ways we are only beginning to understand.



A Practical Call to Action {#h2-11-a-practical-call-to-action}
--------------------------------------------------------------

### So what should you actually do? {#h3-12-so-what-should-you-actually-do}

This article started as an ode to open source, and it still is. But nostalgia won't keep systems compliant or safe. The good news is that the first responses require solid engineering, not ideology.

### 1. Know your software supply chain intimately {#h3-13-1-know-your-software-supply-chain-intimately}

Not approximately. Not via a quarterly report. You must know what's in production well enough to find vulnerabilities before regulators or attackers do.

FOSDEM saw intense discussion regarding supply chain security, SBOMs, and PURLs. Simplistically---since I am not a lawyer---demonstrating control over what you ship requires knowing what you ship. That 'demonstration' is quickly becoming a hard compliance requirement.

### 2. Make your CI/CD pipelines boringly efficient {#h3-14-2-make-your-ci-cd-pipelines-boringly-efficient}

Updates to the OS, dependencies, and tooling have to be routine, not heroic. If updates are painful, the next compliance deadline or vulnerability disclosure becomes an existential risk.

### 3. Establish a support posture for every component {#h3-15-3-establish-a-support-posture-for-every-component}

That includes open source. You must know when your dependencies reach EOL, your legal exposure when that happens, and your technical options.

If upgrading is too risky or too slow, more commercial support is appearing to help with EOL open source. Being in control of your software means being able to explain how you resolve security issues.

Whatever path you choose: planning for frequent upgrades, yearly modernisation, the draconian withdrawal of applications, or simply buying a support contract, you must be able to deliver. Auditors will not accept hand-waving or "we'll figure it out when it happens" type of answers.



Silver Linings {#h2-16-silver-linings}
--------------------------------------

The points above all come down to having solid engineering foundations. New tools are arriving to make things easier and many were on display at FOSDEM. They will , eventually, help you take the drama out of delivering secure software, whether AI is included or not.

Many non-technical elements remain unresolved though . We still need cultural and economic shifts.

FOSDEM 2026 showed us how vital and dynamic open source is and how it is rising to the challenges we all face, but perhaps we saw the beginning of the end of the "just more tech" era.

We are finally acknowledging that cultural and economic changes will rewrite our relationship with software.

I'd rather hear that now, out loud and in public from the people doing the work, than from a regulator, a breach report, or a courtroom.

### FOSDEM 2027 is looking like the place to be next year. {#h3-17-fosdem-2027-is-looking-like-the-place-to-be-next-year}

**Time to mark your calendar.**
