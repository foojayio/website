---
title: "State of the Software Supply Chain Report: Key Takeaways for Java Developers"
slug: "evolving-landscape-software-supply-chains-java-developers"
date: "2023-10-12T08:19:25+00:00"
lastmod: "2023-10-13T08:01:52+00:00"
description: "Dive into the highlights from Sonatype's 9th edition of the 'State of the Software Supply Chain Report.' Discover insights on open source adoption, software vulnerabilities, and the transformative role of AI in software development."
authors:
  - "steve-poole"
image: "Screenshot-2023-10-11-at-14.36.06-1024x541.png"
categories:
  - "Java"
  - "Press"
  - "Research"
  - "Security"
tags:
related_posts:
  - "foojay-podcast-7"
  - "how-to-create-sboms-in-java-with-maven-and-gradle"
  - "how-to-publish-a-java-maven-project-to-the-maven-central-repository"
frozen: false
---

**Sonatype have just released the 9th edition of their [State of the Software Supply Chain Report](https://www.sonatype.com/state-of-the-software-supply-chain/introduction). It delves into the landscape of open source, software development, and software supply chain security. I thought I'd pull out some highlights for Java Developers!**

Software Supply Chains and Open Source {#h2-0-software-supply-chains-and-open-source}
-------------------------------------------------------------------------------------

#### Maven Central hits 1 Trillion downloads

Open source adoption continues to grow, with Java (Maven) being a significant ecosystem.

The report indicates that Java projects and their versions have seen substantial growth. a 28% year-over-year increase in total projects available on Maven Central - hitting the amazing 1 Trillion download number.

#### Log4Shell is still with us

Unfortunately a good number of these downloads were for packages with known vulnerabilities. The percentage is reducing but over 10% of the downloads were questionable - why are we still downloading dependendencies with major vulnerabilities? More worrying is that there is evidence that the problem is much worse than it seems.

The infamous Log4J download numbers show that even after almost two years since the first occurrence of Log4Shell and with a massive 250 Million downloads - about ⅓ of them are for Log4J versions that contain the vulnerability. The conclusion is that we're just not taking a active, structured approach to managing our dependencies.

#### 90% of an application is open source but how much of it is maintained?

Up to 90% of production code is believed to originate from open source and not all of it is developed or maintained with the rigour we might expect. The OpenSSF have been running a programme to monitor and evaluate open source projects.

The report has some results from this programme and it's disheartening to learn that only 11% of the 100,000 or so projects they monitor are actually considered to be 'maintained' Assuming that any open source project that you select is always going to do the right thing as far as security goes is pretty much a broken assumption. Luckily the OpenSSF and others are working hard to help you make better choices from the start. The report has some interesting info about that too

#### The developer is the first and often only line of defense

Overall our behavior is mostly unchanged from last year or the year before that. While software supply chain attacks are climbing, with open source projects (and known vulnerabilities) are a primary target, as an industry we're still pretty poor at dealing with the issues

Which means that for most Java developers, choosing the right dependencies is both a key skill and a important responsibility. Often only effort that goes into evaluating a dependency in toto is done right at the start by the developer. Once in the supply chain that dependency is never given much attention - it's just patched.

Also, as the report says: you could save up to 6 weeks of effort a year per application if you chose more wisely at the start.

#### Help is coming

It's not all doom and gloom though - there are signs that we're acknowledging the problem and beginning to change our behaviour. How we're modernising the software supply chain is a strong theme across the report.

[Chapter 2's Open Source Security Practises](https://www.sonatype.com/state-of-the-software-supply-chain/open-source-security-practices "Chapter 2’s Open Source Security Practises ")explains more about what the OpenSSF and others have been doing to help us all in this area.

Software Supply Chain Maturity {#h2-1-software-supply-chain-maturity}
---------------------------------------------------------------------

While the efforts of the OpenSSF and other organizations are a welcome aid in helping with dependency management to some extent that ship has sailed.

#### Government assistance

Governments around the world are grappling with the challenges of secure software supply chains. The cost of cybercrime and the threat of it's big brother "cyber-warefare" means the sleeping giants have awoke and are intent on helping us help ourselves.

The report covers aspects of this intervention. It's important reading as there are significant implications for all of us. The list of government activities related to software, software security and software supply chains continues to grow. For example,

In the US we have

* United States National Cybersecurity Strategy (NCS)
* Securing Open Source Software Act of 2023
* AI for National Security Act:
* FDA Cybersecurity in Medical Devices
* SEC Regulation
* Cyber Strategy of the Department of Defense
* CISA Open Source Software Security Roadmap
* NHTSA Cybersecurity Best Practices for Modern Vehicles

While the EU

* Cyber Resilience Act (CRA)
* Product Liability Directive (PLD)
* Network and Information Security Directive (NIS2)

Take a look at [chapter 5](https://www.sonatype.com/state-of-the-software-supply-chain/establishment-and-expansion-of-software-supply-chain-regulations-and-standards "chapter 5 ")of the report as governments around the world grapple with the sober fact that the compromise of software supply chains through software dependencies is the foremost emerging threat.

#### Bath and the Open Source Baby

All these legislation efforts demonstrate the seriousness of the cyber crime siutation and the desire in government(s) to 'fix' the problem. There is ready acknowledgement that solutions must not have serious impact on the open source communities that fuel the innovation we all benefit from.

However, views and approaches differ and there is definite risk that in an attempt to resolve the problems quickly the cure will be too draconion and end up decimating the community it's trying to protect.

#### A note on the Cyber Resilience Act (CRA)

Once such concern is the CRA. A laudible EU effort to formalise responsibilities and ownership through out the software supply chain but which has many groups and foundations concerned because of its approach. Some consider that the CRA could implode the open source world.  

Read more here:

1. [Apache Software Foundation](https://news.apache.org/foundation/entry/save-open-source-the-impending-tragedy-of-the-cyber-resilience-act "Apache Software Foundation")
2. [Eclipse Foundation](https://eclipse-foundation.blog/2023/01/15/european-cyber-resiliency-act-potential-impact-on-the-eclipse-foundation/ "Eclipse Foundation")
3. [Linux Foundation](https://linuxfoundation.eu/cyber-resilience-act "Linux Foundation")

Generative AI is here {#h2-2-generative-ai-is-here}
---------------------------------------------------

The recent explosion of generative AI on the scene can't be ignored and obviously it is having an impact on software development across the board.

This year the report has a specific chapter dedicated to AI in Software Development. Key highlights?

#### And already in production

The most astonishing news is how fast tools like ChatGPT have found a home in development organisations. 97% of 800 developers said that they used generative AI in their workflows.

Maybe even more amazing is that it is not a sly, skunkworks style integration. This is fully above board and executive permission and management.

#### AI is not going to replace you - but a developer using AI just might ...

Although we might consider that we're still climbing the hype curve it seems that there is solid value for developers when using AI as a tool in development.

If you're concerned about AI taking over your job your not alone - 18% of those surveyed had the same worry. However, it seems that using these tools as part of your role makes you more productive and more valuable.

Summary {#h2-3-summary}
-----------------------

Sonatype's state of the [software supply chain](https://www.sonatype.com/state-of-the-software-supply-chain/introduction "software supply chain") is a yearly eye-opener for the industry. It clear that this year, more than ever before, our world is starting to change.

While the adoption of AI is going to be high on all our agendas the real impact will come from government legislation

I'll sign off with a great quote form the report for those of you who think positively about AI:

**"The hottest new programming language is English."**
