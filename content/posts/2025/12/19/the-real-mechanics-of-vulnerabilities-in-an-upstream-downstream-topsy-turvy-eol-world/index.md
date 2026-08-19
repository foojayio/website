---
title: "The Real Mechanics of Vulnerabilities in an Upstream/Downstream, Topsy-Turvy EOL World"
date: "2025-12-19T13:36:28+00:00"
description: "In this article you’ll learn Why CVEs record that a vulnerability exists, not that a usable fix exists How vulnerabilities are often discovered and fixed…"
authors:
  - "steve-poole"
image: "banner2.png"
categories:
  - "Java"
  - "Security"
related_posts:
frozen: false
---

### In this article you'll learn

* Why CVEs record that a vulnerability exists, not that a usable fix exists
* How vulnerabilities are often discovered and fixed downstream before upstream ever acknowledges them
* Why EOL branches continue to accumulate exploitable behaviour even when no CVEs appear
* How downstream-only patches break the assumptions scanners and SBOMs rely on
* What disclosure actually enables when "just upgrade" is not a viable option

### Introduction

There is a tidy, almost academic version of how software security is supposed to work. It appears in conference talks, compliance documents and in the cheerful diagrams seen on marketing slides. It usually looks something like this:

A researcher finds a bug, tells upstream, upstream patches it, a CVE appears, downstream maintainers pull the fix, scanners notice, defenders patch, and the world stays mostly ok. A few poor souls miss the memo, and of course, the bad guys get yet another weapon to add to their arsenal.

### What this article is about

If that were truly how things worked, I wouldn't be here writing about downstream-only fixes, silent patches, or security issues that exist in one place and vanish in another. But the real world has a different rhythm and one that is increasingly being *disturbed* - and everyone needs to know.

#### What? You thought CVE pixies fixed the problem everywhere?

The first uncomfortable truth most people don't realise: a CVE is not a demand for a patch.

Well, ok, it may also be a demand, but mostly it's a record of reality. It documents the existence of a vulnerability, not the willingness of upstream to fix it. For most practical purposes CVEs simply start as a record of an instance of a vulnerability against a particular version of a component. It's up to others to expand the scope of the CVE.

You can have a CVE for software that is unmaintained, unpatchable, obsolete, unsupported, or even abandoned. It still matters because people still run it. And defenders still need to know what risks exist, even when no patch is coming.

#### By The way

As an important aside, the CVE can be rejected. *Working-as-designed* is an often quoted reason. In that case, the CVE gets formally marked "rejected". It doesn't get fixed, but it sits in the system. A little waving red flag for the bad guys to review and maybe exploit. Working-as-designed doesn't always mean "safe".

### **Back in the real world**

The process of finding and fixing vulnerabilities is stuttering and developing arrhythmia. The patterns of old are being replaced by ones shaped by exhaustion, understaffing, commercial pressure, legacy systems that never die, and codebases that 'outlive' the people who wrote them.

Once you look honestly at how this system actually behaves, what's happening today. What you might call the *downstream-fix / upstream-blind* problem, stops looking shocking and starts looking inevitable.

### Code Flows

To understand anything in this article, you have to start with the software supply chain itself and how *code flows*.

The terms **"Upstream"** and **"Downstream"** are often used to describe the flow of code and dependencies in the software supply chain. Upstream generally refers to the original source of a software component (like an open-source project or a third-party library). They are at the beginning of the stream, where the code is first created and maintained. Downstream refers to the consumers of that code-stream, such as vendors who embed the component into their products or end-users who deploy those vendors' products.

Downstream organisations take code from upstream and may fork, modify, package, or distribute it, placing them further along the supply chain.

### A different type of waterfall

The terms **"Upstream"** and **"Downstream"** suggest a calm river: water flowing neatly from one point to another. In practice, for popular projects, it's more like a vast delta with rapids and the occasional waterfall. The code spreads, splits, branches, gets repackaged, embedded, wrapped, containerised, and shipped out into the world in directions nobody can fully track.

A component like Apache Tomcat is a perfect example. It exists as the official Apache distribution, yes, but also inside enterprise servers, inside product bundles, inside Spring Boot JARs, inside appliances, inside internal systems last touched in 2017, inside the developer's local install, and in a thousand places between. It becomes part of the ecosystem in a way that is both powerful and almost impossible to map fully.

### Unpredictable as the weather?

That sprawling micro-ecosystem is precisely where and how vulnerabilities emerge. Not neatly and systematically, but in sideways, unpredictable ways.

People often imagine security issues being discovered in upstream by the maintainers themselves. That does happen, but it's surprisingly rare.

More often, a vulnerability first surfaces in a downstream fork, or it's uncovered by a customer running a fuzz test, or it's noticed by a developer who spots a strange edge case during a refactor, or it's found by a researcher who wasn't even looking at Tomcat but happened to trigger a code path shared with some upstream version.

In other words, the first person to see a vulnerability is very often someone far from the upstream project.

### The idealised workflow

That's where the disclosure process is meant to kick in. In the ideal world, the discoverer quietly contacts upstream (*please don't publish it to the world)*, upstream acknowledges, a patch is worked out, a CVE is assigned, and everything proceeds as it should.

But once upstream is older, slower-moving, busy with the next major version, or no longer maintaining that branch, the ideal workflow dissolves.

When a vulnerability appears in an End-of-Life upstream, like Tomcat 8.5, nobody expects upstream to reopen that branch. They have moved on. The patch would be difficult, risky, or time-consuming. They quite reasonably decline. And so the vulnerability persists.

Now imagine you're a downstream vendor. You support customers running Tomcat 8.5 on your fork (remember the delta analogy) because their workloads can't simply jump to Tomcat 9. You discover a vulnerability. You know it exists in code originally taken from upstream Tomcat. You fix it in your fork. You do the right thing for your customers.

### The ethics of vulnerability patching

Now you face the real dilemma: **do you tell upstream?**

Some vendors don't.

The reasoning seems tidy: upstream is EOL, so no patch will be forthcoming. Why bother them? Why risk raising an issue that attackers might see and exploit if there's no available fix? Why "expose" the project when the problem might remain safely quiet?

Except the idea of safe quiet is an illusion.

Attackers are not relying on CVE databases. Attackers are much, much more sophisticated and armed.

They diff patches. They fuzz older codebases. They target EOL systems precisely because they know nobody is watching.

### Unreported is not safe

A silent vulnerability is not safer than a disclosed one: it is just more profitable for attackers. The only people kept in the dark by silence are defenders, auditors, and the teams desperately trying to keep unmaintained systems alive long enough to migrate them.

This is the real ethical bottom line: disclosure does not create risk; it distributes knowledge of the risk to the people who need it. The vulnerability is already present and likely known. The attackers already have the upper hand. The only thing disclosure changes is that defenders can finally see the problem too.

### Downstream's responsibility to the ecosystem

And this is why downstream vendors have a deeper responsibility than most realise.

Once you discover a vulnerability in upstream-derived code, you are no longer only responsible for your own customer base. You have identified a problem that likely affects parts of an ecosystem far beyond your fork.

Upstream may not fix it. Nobody is suggesting they must. But the ecosystem needs to know it exists, because the ecosystem is still running that code, often at staggering scale.

This becomes painfully clear when you remember that, in our example, Apache Tomcat ( including forks varients and versions that are End-of-Life ) is everywhere. Not because people love running outdated servers, but because it has been embedded in frameworks, enterprise products, appliances, Spring Boot applications that "just work," and internal systems that never got migrated after the architect left.

### End of Life software is everywhere

An EOL project can still be foundational. In fact, EOL projects are often foundational because nobody touches them when they work well.

So what happens in practice when a downstream vendor fixes a security issue that upstream will never patch?

In an ideal world, the vendor notifies upstream anyway. Upstream acknowledges the report, marks the issue as affecting an EOL branch, and declines to patch. A CVE is published to document the problem. Downstream ships its fix. SBOMs reflect the vulnerability's existence. Scanners alert operators running older versions. Users can apply compensating controls. EOL support vendors can build mitigations.

Everyone gets to act. Nobody stays blind.

### The peril of downstream silence

But when downstream maintainers (especially of forked variants ) remain silent; when the vulnerability is fixed in one place and remains unacknowledged or just "noted" everywhere else, then you get the worst possible outcome.

Upstream knows nothing. Defenders see nothing. SBOMs misrepresent reality. Vulnerability scanners report "no known issues." Legacy systems continue running with a false sense of safety. And attackers who grok the downstream patch now hold precious knowledge of a vulnerability that won't be fixed.

### How the world works

None of this is malice. It's not incompetence. It is simply what happens when the system relies on people voluntarily and consistently doing the right thing across dozens of organisations, time zones, codebases, and priorities.

The truth is that nothing in the ecosystem enforces upstream notification. It happens because maintainers, vendors, and researchers believe in the value of shared signals.

The entire system works only if everyone believes disclosure helps defenders more than attackers — which it does.

### Summary

If there's one lesson from the downstream-patch / upstream-silence dynamic, it's this: **silence is the real exposure**.

Vulnerabilities don't become dangerous when disclosed; they become harmful when only one part of the ecosystem knows about them.

Transparency isn't just *ethical.* It's operationally necessary, especially where EOL software frequently remains the backbone of production infrastructure long after anyone expects it to.

This is how it all works in practice. Not the polished diagram, but the real machinery: code that forks and diverages, vulnerabilities that surface unexpectedly, upstreams that age out, downstreams that patch, ecosystems that depend on honesty, and defenders who can only defend what they can see.

If we want a safer and more predictable world, the answer isn't silence or secrecy. It's accurate signals. Even when no fix is coming, and even when the upstream is long past its supported life.

**CVEs dont work the way you think - but I wish they did**

### Next time

In the next article I'll explore an important case study and show just how messy CVE reporting, fixing, and discovery can be.
