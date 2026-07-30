---
title: "Making SBOMs, Threats, and Modelling Them a Piece of Cake!"
slug: "making-sboms-threats-and-modelling-them-a-piece-of-cake"
date: "2023-03-07T09:15:56+00:00"
lastmod: "2023-03-07T09:15:58+00:00"
description: "The third article in a series on SBOMs, software supply chains, the government and you, introducing threat modelling and tools to help!"
authors:
  - "dan-conn"
image: "/images/posts/2023/03/making-sboms-threats-and-modelling-them-a-piece-of-cake/Unknown-300x260-1.jpeg"
categories:
  - "Developer Tools"
  - "Security"
tags:
related_posts:
  - "sboms-first-steps-in-a-new-journey-for-developers"
  - "sboms-and-software-composition-analysis"
  - "java-where-the-wild-code-isnt"
  - "tiberius-a-security-testing-framework-for-llm-applications-in-java"
frozen: false
---

The third in a series of SBOMs, software supply chains, the government and you (you too, no exceptions).

Yes, just like the previous articles, we have cake this time too - this one is a three tiered extravaganza!  

<figure class="aligncenter size-thumbnail">
 <img fetchpriority="high" decoding="async" width="300" height="260" src="/images/posts/2023/03/making-sboms-threats-and-modelling-them-a-piece-of-cake/Unknown-300x260.jpg" alt="" class="wp-image-62771">
</figure>

So... still with us? Good!

Earlier, Steve Poole showed us his digital mixing bowl, [telling us how applications and their dependencies were like ingredients to a fancy cake](https://foojay.io/today/sboms-first-steps-in-a-new-journey-for-developers/).

Jamie Coleman then made sure there were no soggy bottoms (one for the Mary Berry fans), by explaining how [SCA tools and SBOMs can work together to prevent vulnerabilities](https://foojay.io/today/sboms-and-software-composition-analysis/).

So I guess now it's time for me to add the covering to the layers, some glacé cherries, and maybe some of those ornate flowers made of icing, who knows!

Tier 1.... The large base layer. Funfetti. {#h2-0-tier-1-the-large-base-layer-funfetti}
---------------------------------------------------------------------------------------

Vulnerabilities! Nasty things aren't they?! As nasty as funfetti cake, [ranked one of the world's worst cake flavours](https://www.tastingtable.com/1155106/ranking-popular-cake-flavors-from-worst-to-best/), controversial, I know! They've been around for a long time, almost as long as computers. Steve also mentioned earlier that they are ever increasing and getting difficult to manage.  

It is no secret [that a company I used to work for,](https://www.mimecast.com/blog/important-update-from-mimecast/) [and many others](https://www.wired.com/story/solarwinds-hack-supply-chain-threats-improvements/), have fallen victim to software supply chain attacks.

They aren't going anywhere and we need to find ways in our software development lifecycle (SDLC) of dealing with them pre-production because they are so much harder to deal with in the wild.

I'm sure I'm not the only one here that worked relentlessly two years ago when Log4Shell hit to make sure our Log4J versions were up to date FIVE DIFFERENT TIMES before we were back to terra firma.  

Even if you do everything right, [Sonatype's 8th Annual State of The Software Supply Chain report](https://www.sonatype.com/state-of-the-software-supply-chain/introduction)tells us that 6 out of 7 project vulnerabilities will be from transitive dependencies.

That's a statistic that I read as saying, assuming you do everything right, it is likely that your project will contain vulnerable codepaths.

OK, let's go to the next layer. As for this cake, it's OK, you can spit it out and put it in the bin.

Tier 2.... The middle layer.. Chocolate. {#h2-1-tier-2-the-middle-layer-chocolate}
----------------------------------------------------------------------------------

Now we get to a good one! Chocolate cake tastes great and is simple to make! Much like good security tools!   

The only way we are going to know for sure whether our projects are safe or not, is through monitoring and assessment and this is made easier through tools.   

Vulnerability scanners, dependency management tools, policy based dependency firewalls, software composition analysis tools, mutation testing, quality gates, chaos engineering, the works!

All of these will hopefully give us confidence and highlight problems in the things we build or pull into our builds before we hit production. SBOMs themselves are a tool to stop vulnerabilities.   

Tools that visualise SBOMs and recommend upgrade paths, such as Sonatype's experimental free tool, [BOM Doctor](https://bomdoctor.sonatype.com/#/home), combined with code analysis tools such as [Semgrep](https://github.com/returntocorp/semgrep), or many others, can show us the problems in our code and dependency trees.

While tools that stop vulnerabilities getting into your local development repositories in the first place can make deployment safer before we've thought about using it!  

Goodies make our lives easier and hopefully catch a lot of the bad things before we hit our staging servers, let alone production deployment! If only they actually tasted like chocolate cake...

Tier 3.... The top layer.. Genoise Sponge. {#h2-2-tier-3-the-top-layer-genoise-sponge}
--------------------------------------------------------------------------------------

Now we go for a showstopper! Sophisticated, rich and tasty! Mmmmmmm!

You see, while tools are indeed very good, they are not foolproof. They all have false positives, false negatives, and sometimes just things that go wrong.

In my humble opinion, to say otherwise is disingenuous to the point where I would not trust that person saying it.   

To fill that gap we need a super tool - that's YOU!

I can hear you saying, "Hang on Dan, you told us at the beginning that we need tools due to sheer volume".

Well, yes, that is true! But by implementing these things, you now have a more manageable attack surface and can try more sophisticated defences, rather than keeping code secure and trying to avoid dependency issues.

One of these more sophisticated things I urge you to try is threat modelling!   

So what is threat modelling? As the [Threat Modelling Manifesto](https://www.threatmodelingmanifesto.org) tells us, at a very high level, it's about answering four questions:

1. What are we working on?
2. What can go wrong?
3. What are we going to do about it?
4. Did we do a good enough job?

We can ask questions such as, "What versions of dependencies are in our SBOM?", "Are these versions vulnerable?", "Can we upgrade?", combined with traditional questions such as, "Which encryption method are we using for this connection?".

If we didn't do a good job then we try again. By ingraining this practice when we design things, we can prevent a lot of problems.   

Some of you may be thinking that this might be the job of a security team, I disagree. They are the custodians of the process for sure, and will give great insight into security issues you may not even be aware of. But, as a software builder, you have an insight that may not have been thought of.   

You can also get people involved that are not in development or security to help. Not only should this happen because security is for everyone, but they may have experienced things that gives them a hacker mentality, for example they might have been the unfortunate victim of a crime which means they now understand how the criminal mind works in some way.   

Over the past few weeks I have delivered talks on threat modelling from both a developer and application security perspective to [Open UK State of Open Con](https://www.youtube.com/watch?v=bqNSb32trEI), [OWASP London Chapter](https://www.youtube.com/watch?v=S1UXqPQs2Sw&t=2s)and a webinar to the [London Java Community JUG](https://www.youtube.com/@LondonJavaCommunity/videos). If you would like to know more then please follow the video links.   

One thing to say is that threat modelling can be incredibly time consuming and sometimes building the diagrams can be a pain. How do we make our life easier here? Well.... The same as we do with the other layers! Tools!   

Personally I like to use the [Threagile](https://threagile.io), [OWASP Threat Dragon](https://owasp.org/www-project-threat-dragon/), and [OWASP PyTM](https://owasp.org/www-project-pytm/) tools to assist with building the diagrams and asset catalogues associated with threat models.. The card game [Elevation of Privilege](https://shostack.org/games/elevation-of-privilege) by Adam Shostack is incredibly helpful when using the STRIDE framework for threat modelling.   

I hope over the series we have highlighted the problem of vulnerabilities, highlighted some tools that can help, shown that through threat modelling, secure coding, SBOMs, and dependency management, everyone can help us build resilient systems.   

If you're thinking that this might be hard, then good! Security is hard, it's definitely not a piece of cake, but hopefully these tips will make it a little easier. If you still feel that this might not be for you though, then I urge you to reconsider. The [US National Cyber Strategy](https://blog.sonatype.com/white-house-national-cybersecurity-strategy-landmark-action-for-a-critical-threat) released last week might be a moonshot and wishlist for now, but already we have the Executive Order in the US, the forthcoming Cyber Resilience Act in the EU, the update of secure coding to ISO 27001:2022 which may already make this your problem, even if you don't want it to be.

So why not get ahead of the curve?  

<br />

<br />
