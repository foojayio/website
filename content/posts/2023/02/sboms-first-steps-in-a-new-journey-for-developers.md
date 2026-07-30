---
title: "SBOMs: First Steps in a New Journey for Developers"
slug: "sboms-first-steps-in-a-new-journey-for-developers"
date: "2023-02-14T10:01:28+00:00"
lastmod: "2023-02-14T10:02:29+00:00"
description: "Software bill of materials, anyone? A year ago, developers had not heard of the 'SBOM' acronym... and now SBOM visualiser called BOM Doctor."
authors:
  - "steve-poole"
image: "/images/posts/2023/02/sboms-first-steps-in-a-new-journey-for-developers/Screenshot-2023-02-10-at-11.23.35.png"
categories:
  - "Developer Tools"
  - "DevOps"
  - "Research"
  - "Security"
  - "Tools"
tags:
related_posts:
  - "how-to-publish-a-java-maven-project-to-the-maven-central-repository"
  - "how-to-release-a-java-module-with-jreleaser-to-maven-central-with-github-actions"
  - "boxlang-aws-azure-and-google-secrets-manager-module-released"
frozen: false
---

***This article is the first in a series about SBOMs, software supply chains, the government and you. Buckle up - it's going to be a wild ride. Luckily there is cake to see you through.***

Software bill of materials, anyone? A year ago, most developers had not heard of the 'SBOM' acronym.

Indeed, related terms like "software supply chain" or "security hygiene" were absent from most developers' vocabularies.

**Things are about to change! This year is already shaping up to be the year of SBOMs, of securing the software supply chain, and of the appearance of legislation that enforces better security practices among those involved in software. Whether in creation, testing, deploying, securing or operating, we all have specific security responsibilities in protecting our part of the software supply chain.**

An SBOM, a software bill of materials, is often spoken about as a way to help supply chain security. To understand what it is, how it works and, most critically, why now? We must first step back and look at the broader landscape of software creation, software security and the threats to our systems.

### Software runs the world --- and it's almost all open source {#h3-0-software-runs-the-world-and-it-s-almost-all-open-source}

Almost everything we do in modern life has some software component. Software is responsible for delivering food to our tables, lighting our homes, enabling communications, providing transport, and so on. Almost all of this software, or its constituent pieces, is open source. Ninety per cent of modern applications use open-source components and run on open-source operating systems in open-source Kubernetes clusters. Simply put, individuals outside the application development team produce 90% of the final application.

On the one hand, it demonstrates the power of open source. We can build our solutions off the hard work and innovation of others. Without this pervasive sharing, our connected, software-controlled world would not exist, nor would we be able to produce software of such high quality as fast as we are now. On the other hand, we've built this world with assumptions about motive, behaviour, and trustworthiness. Assumptions that are currently being exploited by bad actors worldwide at an unprecedented scale.

### Bad actors abound {#h3-1-bad-actors-abound}

It's no coincidence that the U.S. government, the European Union, the U.K., and other nations are creating legislation to enforce behaviours, set standards, and generally tighten up the world's approach to software.

As the scale of cybercrime continues to grow, we've seen a shift from simplistic head-on attacks to sophisticated and intricate ones.

Supply chain attacks, which means bad actors corrupt the 'process' of software creation, deployment, or operation, are growing at 742% yearly. Given that cybercrime is currently worth 7 trillion dollars to the bad actors, it's clear that the situation is rapidly spiralling out of control.

Things have to change.

### What does this have to do with SBOMs? {#h3-2-what-does-this-have-to-do-with-sboms}

If 90% of an application is written by someone else, then 90% is likely to be shared with others.

A primary route for bad actors is to find these elements and shared dependencies and corrupt them in various ways. That gives them an economy of scale for attacks that nowadays manifest themselves as massive botnet attacks once a vulnerability is discovered.

To protect an application, it's vital to be completely aware of every one of the software components included: the less awareness, the higher the risk of unexpected (and often hidden) attacks.

### Where's the cake? {#h3-3-where-s-the-cake}

Imagine an application and its dependencies as ingredients in a fancy cake.

Current software composition analysis (SCA), which attempts to gain a window into the 90% of the shared piece of the cake, is often incomplete.

Imagine trying to identify all the ingredients in the cake after it's baked. It might be possible to identify some of the components: the jam, the icing etc. but the smaller components, the eggs. The milk etc., these items get lost in the baking and effectively vanish.

The effectiveness of SCA thus depends on the capability of the scanning tools, their data, and how the software is packaged.

Picking the less capable tools can lead to a false sense of security.

### SBOMs to the rescue {#h3-4-sboms-to-the-rescue}

The reverse engineering approach has been the customary way of discovering what is included in an application. SBOMs are the antithesis of this practice.

Simply, it's an intention that every software component comes with a list, a bill, of its features or materials, hence the name a "software bill of materials." Every software component comes with its list of ingredients, which is included upstream by other components up to the application itself.

Now there is much greater visibility of features. Going back to the cake analogy: the SBOM provides insight into the fact that eggs were included and where the eggs came from. Think of a nutrition label on food packaging, and you're close to what an SBOM is.

This approach immediately reduces the impact of ineffectual scanning tools since the SBOM provides all the insight needed. Well, mostly.

Deep scanning tools are still needed to discover malware, intentionally corrupted components and more. Still, most attacks are through software vulnerabilities in open-source components, so SBOMs will help application owners dramatically manage their exposure from that vector.

### It's more than tools and dependencies {#h3-5-it-s-more-than-tools-and-dependencies}

SBOMs are a partial panacea to securing the software supply chain but the approach makes such a big difference that adopting SBOMs (both the consumption and production of them) is becoming law.

In the U.S., SBOMs are required when providing software to the government, and it's easy to imagine this will continue to be the chosen approach in other countries.

Spoiler alert: Governments are as concerned about *how* we develop software as much as *what* we produce. SBOMs are the beginning of a focus on software development we've never seen before.

### Next steps {#h3-6-next-steps}

To get ahead and start to understand the tools, the processes, and the consequences to software production, there are various places to go and many tools to evaluate.

In future articles, we'll dive into the tools' details, look at the ideas for improving (and measuring) development practises and analyse related government directives.

For now, we'll leave you with a link to a new, free, somewhat experimental SBOM visualiser called[BOM Doctor](https://bomdoctor.sonatype.com/ " BOM Doctor").

Just released by Sonatype, [BOM Doctor](https://bomdoctor.sonatype.com/ "BOM Doctor") guides Java developers on generating an SBOM using standard tooling and helps visualise the connections between items. Now you can get an accurate view of all the Java dependencies your application relies on.

BOM Dr also connects information about vulnerabilities and other supply chain matters into the visualiser. For a quick intro to the tool, watch this impromptu [sketch](https://www.youtube.com/watch?v=mejkuad5uMM/ "sketch") too.

*Thanks for reading*
