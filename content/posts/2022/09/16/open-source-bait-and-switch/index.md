---
title: "Open Source Bait and Switch"
slug: "open-source-bait-and-switch"
date: "2022-09-16T07:44:39+00:00"
lastmod: "2022-09-16T07:44:41+00:00"
description: "When OSS advocacy goes too far and corporate greed takes over. FOSS is used as a tool to destroy competition and hurt the dev community."
canonical: "https://debugagent.com/open-source-bait-and-switch"
authors:
  - "shai-almog"
image: "DALL-E-2022-08-23-18.47.43-unicorn-running-away-from-men-in-suits-through-wide-open-field.png"
categories:
  - "Opinion"
tags:
related_posts:
  - "effectively-bridging-the-devops-rd-gap-without-sacrificing-reliability"
  - "fail-fast-best-strategy-for-reliable-software"
  - "debugging-gson-moshi-and-jackson-json-frameworks-in-production"
frozen: false
---

I was reading [this article](https://betterprogramming.pub/why-open-source-software-is-better-for-the-world-af1cf9045236) and wanted to post a comment but I felt this warrants a response article.

First, if you don't know me I've written a ton of Open Source code. A whole platform and then some. I think the general view expressed in that article and a lot of the fluff I see online is over simplistic and dangerous.

We Need to get Paid {#h2-0-we-need-to-get-paid}
-----------------------------------------------

Who will pay your salary?

People have various answers for open source business models. E.g., "consulting" or the vague "support". I always wonder if such people ever tried selling consulting? Or maybe "support".

People don't buy these things. Especially in a downturn economy. The way sales people typically sell these things is the hint that you might violate a license and it would be much easier if you paid and got the commercial license.

I'll even throw in the "support" for free. Yes, you can get some sponsors if you spend all your time on the phone. It's a never-ending sales process. Chasing leads and making calls. Running this sort of business requires a lot of overhead.

Some developers use problematic open source licenses which they can then leverage for sales. But then they get vilified as "not open source enough". There's no winning there.

Sun Microsystems paid my salary during some of my OSS work. They went under and fell from a 200+ billion dollar valuation, eventually selling at around 7 billion. Granted, it isn't because they were open source. That's just an anecdote, and it didn't help. Real people lose their actual jobs because open source doesn't make money. Even when it does, it makes very little.

People used to say it ends up with a smaller piece out of a larger pie. This is true, but only applicable for the big players and you have two choices: become the big player or watch a big player take the profits from your hard work.

Don't get me wrong, I'm not against people profiting off my work or even large corporations. I do OSS for fun and love the idea of people succeeding based on my work. But I get the frustration many developers feel, and the blanket "open source advocacy" I see from people is problematic.

Bait and Switch {#h2-1-bait-and-switch}
---------------------------------------

The bad thing is corporate cynicism. Take Google. They open sourced Android when it had no users. Companies built on top of it and so did developers. Advocacy formed around it because "it's open source". Then they released the closed source Google Play Services which later added the SaaS Firebase requirement for some essential functionality (it's free for that at the moment) and now we have deep vendor closed source dependencies masquerading as open source.

We don't **have to use** Google Play Services if we want to build for Android. Sure. But it makes it much harder to build an Android app and if you don't use it for some things (e.g. push, purchase, etc.) then you're banned from the largest Android marketplace. A vast majority of developers "just use it". This means that anyone who wants to wipe Google Play off their Android device in favor of a 100% open source solution; will find that they have very few software choices for apps.

Take Elastic search. They were open source and killing it. But AWS was forking and not really helping their bottom line. So Elastic changed their license to block AWS. AWS started their own fork. Some people vilify Elastic in this story but those people probably never had to fight Amazon for the survival of their business. In this case, both sides weaponised open source in a business fight.

The case of Java is slightly different. Java wasn't open-source and was made open source later. It still kept IP over the language. So I get some of Oracle's tight grip on the project and accept that. It's good that there's a steady hand of the ship and it contributed to the success of Java. The problem with the Google lawsuit was that Oracle tried to stretch their IP to include the APIs. That was a mistake.

GPL is the Best License {#h2-2-gpl-is-the-best-license}
-------------------------------------------------------

About a decade ago, a startup called RoboVM released an open source compiler that translated Java to native iOS apps. It was pretty cool, and I talked quite a bit with the founder at JavaOne. Back then we were considering building our own VM or picking up a solution such as RoboVM. We ended up with the former and built our own VM which was much simpler and better fit our needs ([it's also open source](https://github.com/codenameone/CodenameOne/tree/master/vm)).

That decision was based on technical merits and I think it paid off but I still have tremendous respect for the RoboVM team. My concern was Apple. They break stuff, a lot... Building a low level VM is a dangerous game of cat and mouse where Apple would suddenly change something and we'd be stuck. A secondary concern was monetization. I understood the RoboVM team would have to pay the salaries of its employees and founders somehow. How does one make money off an open source compiler? Notice that this was a decade ago and there weren't any precedents like [Zig](https://ziglang.org/zsf/) that we could look at as a template.

At some point, my fears of Apple's policies materialized, and Apple added a bitcode requirement when targeting some platforms. RoboVM spent a tremendous amount of time working on bitcode support and decided to close its source code. They understood they can't fund continued development without it. Don't take this as judgement towards their decision. I totally get that, as I said, monetizing OSS is **very** hard.

The GPL protected the community by forcing RoboVM to release the code for the final version before the bitcode migration. This allowed some forks of the code in later years but it's still unmaintained. The company was bought by Xamarin and promptly discontinued as the latter was purchased by Microsoft. Without the GPL, the code might have been inaccessible. It also forced 3rd parties to publish their code.

In this regard, I'm a big believer in GPL rather than more permissive licenses such as MIT, BSD, Apache, etc. I think that we as a community should prefer a license that preserves community rights as opposed to corporate rights. It also provides a good monetization option to the creator of the project. Just re-license as a proprietary license and charge for that. Unfortunately, the GPL is often a no-starter for many developers because they incorrectly assume it's bad for them. The opposite is true. The GPL is one of the best ways to preserve community rights in the long term.

Have Things Changed? {#h2-3-have-things-changed}
------------------------------------------------

I mentioned Zig before. There are many other examples of foundations that were successful such as SQLite, Mozilla, etc. Getting to this point is typically hard and doesn't necessarily work for every open source project.

E.g., cURL is used by pretty much everyone but I don't think we'll see a cURL foundation. Also notice that all of these projects are based in US tech hubs. From my experience, it's much harder to get sponsors in other territories.

Does this mean all open source should be a hobby or a mega-all encompassing project?

Unfortunately, I don't have good answers. But I have a problem: over zealous open source advocates. You aren't helping!

Open source is becoming a corporate-only game. It's used as a weapon between battling tech companies. There's a name for this in retail: loss leader.

In retail, a big supermarket store would sell some products at a loss and advertise this amazingly low price. This brings in audiences who buy other things along the way and so the supermarket ends up making a profit. But the reason for this is to drown out the competition.

The competition seems expensive (since they can't sell at a loss). They go out of business, and the big retail company raises prices. Initially, it looks like we got a tremendous deal from the competition, but ultimately we end up losing. Because of that some regulators prohibit such practices as they end up destroying a market.

Open source is used in a similar, cynical way by big tech. They form "communities" by hiring armies of developer relations professionals to create a masquerade of grassroots enthusiasm. Sometimes they don't need to monetize the market, it's enough to keep the competition out.

TL;DR {#h2-4-tl-dr}
-------------------

I love open source and think it's remarkably important. That's why we shouldn't let corporations weaponize it. We need diversity within the ecosystem and we need to support smaller, critical projects. The idea of "handouts" to open source projects or "consulting" isn't sustainable.

Major corporations use open source as a weapon to fight each other, we seem to benefit in the short term. But as they win the corporate mindset takes over and they double down on control. The solutions I suggest are:

* Use GPL - it's here to protect community rights. No wonder corporations don't like it.
* Don't be an OSS puritan - small companies need to make money. They will offer SaaS, closed source extensions, etc. That's OK.
* Big corporations aren't benevolent - the advocacy I see around OSS projects from FAANG (MAANG) companies is problematic. They don't support OSS. They use and leverage it. Don't get me wrong, I'm grateful for their code and it's wonderful they release it. But we need to be cautious, they have fiduciary requirements that might collide with doing the "right thing" by OSS standards.

I don't know if the next thing I'll do will be OSS. I don't know if I'll pick GPL since, as I said, people have an issue with it. But I do know this: if you're an open source advocate. Tune down the rhetoric. It isn't helpful.
