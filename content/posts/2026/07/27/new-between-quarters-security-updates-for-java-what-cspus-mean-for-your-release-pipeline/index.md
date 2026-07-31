---
title: "New Between-Quarters Security Updates for Java: What CSPUs Mean for Your Release Pipeline"
date: "2026-07-27T07:31:11+00:00"
description: "In an announcement on the OpenJDK Updates Mailinglist on July 20, 2026, Rob McKenna explained a change in the Oracle security release cycle for JDK - by Frank Delporte"
authors:
  - "frankdelporte"
image: "openjdk-cspu-releases.png"
categories:
  - "Java"
  - "Java Core"
  - "Release Notes"
tags:
related_posts:
  - "all-azul-zulu-container-images-explained-ca-sa-and-chainguard"
  - "azul-zulu-april-2026-quarterly-update-released"
  - "should-you-update-java-or-upgrade-and-which-version-should-you-use"
  - "foojay-podcast-78"
frozen: false
---

In an announcement on the [OpenJDK Updates Mailinglist](https://mail.openjdk.org/archives/list/jdk-updates-dev@openjdk.org/thread/SVHDNJDY62MX6YBEA4FSMW4U72H77F6S/) on July 20, 2026, Rob McKenna explained a change in the Oracle security release cycle for JDK builds. They plan to deliver security updates for the Oracle JDK as part of **(some) monthly Critical Security Patch Updates (CSPUs)** .

This brings more exact info, after an earlier message on April 26, 2026, in which [Oracle announced the acceleration of Vulnerability Detection and Response](https://blogs.oracle.com/security/accelerating-vulnerability-detection-and-response-at-oracle). In that article they introduced the idea of monthly Critical Security Patch Updates (CSPU) across all their products. At that moment, it was not clear if this also applied to the Java releases, and how this would impact the normal quarterly cycle of Critical Patch Updates (CPUs).

It's now clear that the **first Java CSPU lands August 18, 2026** and your build and deployment process needs to be able to handle security patches outside the normal quarterly window.

What's Changing {#h2-0-what-s-changing}
---------------------------------------

Java security updates have run on a fixed quarterly schedule since 2014: January, April, July, October, on the third Tuesday of the month. That schedule stays. What's new is a second type of release, the CSPU, that can slot in between CPUs when a high-priority vulnerability needs a fix sooner than the next quarter allows.

Oracle's Integrated Cyber Center runs CSPUs monthly across its whole product line, but Java doesn't automatically get a fix in every one of those months. Oracle's own wording is specific: *Java security updates ship "as part of (some) monthly" CSPUs*, not all of them. The first Java CSPU ships August 18, 2026, roughly one month after the July CPU. Oracle has no Java CSPU planned for September. Longer term, Oracle intends to shift Java updates toward a full monthly cadence over the coming year, but that's not the case yet. Expect CSPUs to show up irregularly for now, and closer to monthly next year.

On the jdk-updates-dev mailing list, Rob McKenna outlined the plan for OpenJDK: CSPU release for JDK 26, versioned 26.0.2.1, also targeting August 18. Andrew Hughes confirmed the other update trains (8u, 11u, 17u, 21u, and 25u), will treat CSPUs the same way they treat interim releases. Each branches from the prior GA tag, adds the security patches, keeps the scope minimal, then merges back. The regular quarterly cycle keeps running underneath, unaffected.

Why Now {#h2-1-why-now}
-----------------------

The reason Oracle gives is straightforward: AI tools have sped up both sides of the vulnerability lifecycle. Attackers can search for exploitable weaknesses faster than before. Defenders can find and fix them faster too. A 90-day wait for the next CPU, which used to be an acceptable window, now leaves more room for a known vulnerability to get weaponized before a fix ships. CSPUs close part of that gap without waiting for a full quarterly release.

CSPU Versus CPU {#h2-2-cspu-versus-cpu}
---------------------------------------

A CSPU is not a new kind of release you need a separate process for. It's the same kind of update you already apply every quarter, just delivered more often.

CSPUs carry security and stability fixes only. No new features, no API changes, no behavior changes outside of the vulnerability fixes themselves. Oracle and OpenJDK maintainers are both explicit about keeping CSPU scope narrow, especially early on while the process is new. That's good news for anyone worried about regression risk. A CSPU should behave like a small, focused CPU, not a mid-cycle feature drop.

Version numbering follows the existing convention. JDK 26 Updates gets 26.0.2.1 for its August CSPU, one step past the July 26.0.2 CPU. Expect the same pattern on other update trains: CSPU releases append a digit onto the version.

What You Need To Do {#h2-3-what-you-need-to-do}
-----------------------------------------------

Treat a CSPU exactly like a quarterly CPU. Pull it into your dependency management the same way. Run the same test suite, and promote it through the same pipeline stages. The only change is frequency: you now need to check for and apply security releases on an irregular, sometimes-monthly schedule instead of only every three months.

A few concrete steps worth taking now:

1. Update your patch-monitoring process to check monthly, not quarterly, even though not every month brings a Java CSPU. Subscribe to Oracle's security alert notifications and watch the relevant OpenJDK Updates mailing lists if you track a specific train.
2. Confirm your test and release pipeline can run on a monthly trigger without manual bottlenecks. If your current process assumes a quarterly cadence with weeks of buffer, an unpredictable extra release removes most of that buffer.
3. Don't wait for October. Oracle expects the October CPU to carry more vulnerability fixes than usual, since Oracle holds some fixes for that release ("*We are not currently planning a CSPU release for September.*") instead of pushing them into a CSPU in September. Budget testing time accordingly.
4. If you consume a vendor JDK distribution rather than building from OpenJDK sources yourself, check how that vendor plans to handle CSPUs. Some mirror the CSPU schedule, others fold fixes into their own release train differently.

Azul Readiness {#h2-4-azul-readiness}
-------------------------------------

[Azul announced in a press release](https://www.azul.com/newsroom/azul-to-deliver-monthly-critical-security-patch-updates-for-java-across-all-supported-lts-versions/) and [blog post](https://www.azul.com/blog/azul-will-deliver-monthly-java-critical-security-patch-updates-increasing-patch-velocity-with-stability/) that its release infrastructure and validation process for Azul Core and Azul Prime already support a faster cadence, including monthly updates, without changing how customers consume them. The approach keeps CSPU-equivalent releases scoped to vulnerability fixes. Azul tests each one with the same stability checks it runs on quarterly updates, so a faster cadence doesn't add to your testing burden.

Conclusion {#h2-5-conclusion}
-----------------------------

The 90-day wait for critical Java security fixes is going away. Not because the quarterly cycle broke, but because AI has shortened the time between a vulnerability becoming known and becoming exploited. Your job isn't to build a new process for CSPUs. It's to run your existing patch pipeline on a shorter clock, and to start now, before October's CPU brings a bigger batch of fixes than you're used to.
