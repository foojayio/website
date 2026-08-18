---
title: "How Organizations Became Stuck on Outdated Java Versions"
slug: "how-organizations-became-stuck-on-outdated-java-versions"
date: "2024-11-27T15:46:16+00:00"
lastmod: "2024-11-27T15:46:17+00:00"
description: "Why is your company still on Java 8 (or older)? And why did you never move to 9, 10,... and got stuck on this outdated version?"
authors:
  - "frankdelporte"
image: "java-discussions.webp"
categories:
  - "Java"
  - "Java Core"
  - "Opinion"
  - "Performance"
tags:
related_posts:
  - "why-java-8-is-a-ticking-time-bomb-hiding-within-your-organization"
  - "mastering-the-challenges-of-openjdk-migration"
  - "are-critical-vulnerabilities-lurking-in-your-java-ecosystem"
frozen: false
---

My recent article ["Why Java 8 is a Ticking Time Bomb Hiding Within Your Organization"](https://foojay.io/today/why-java-8-is-a-ticking-time-bomb-hiding-within-your-organization/) triggered quit some reactions... and so I went a step further and asked on social media: "*Why is your company still on Java 8 (or older)? And why did you never move to 9, 10,... and got stuck on this outdated version?*"

Here is a summary of what I learned from the reactions!

## Reasons to Stick to 8

Of course, there are many reasons why some projects deliberately are kept on Java 8 or don't succeed on moving to a newer version.

### Managers Blocking Upgrades

In many cases, the development team wants to move on, but gets a no-go by management for various reasons....

**[Piotr Mrożek](https://app.daily.dev/softwareforge)** on daily.dev:
> *Most of the time it's not for the technical people (like devs) to make the decision to upgrade. Probably most cases are business people saying there's no need to upgrade "because it works" and \[insert some "no budget" shenanigans here\].*

**[Oliver M. S.](https://www.linkedin.com/in/oms-linux/)** on LinkedIn:
> *I also encountered 4 companies within past 5 years that stuck with Java 8. By all sorts of silly excuses. Like too much efforts to upgrade JVM, "would need many weeks" (🤣🤦‍♂️) to verify that business SW still works etc. IMO any biz / org that fails / refuses to upgrade core systems at least 2x per year has serious technical \& process \& quality issues!*
>
> *Apart from errors directly shown at compile time, like with modules or removal of JavaFX or other long obsoleted or extremely rarely used parts, I have never seen any hidden incompatibilities in Java that would outbreak later.*
>
> *Compile time error incompatibilities can typically be handled easily. Or at least with limited efforts. Like one or two days by one or two developers. And such breaking changes happen hardly more often than 1x-2x per decade in Java ecosystem.*
>
> *If managers have so little confidence in compatibility of JVM upgrades, claiming every upgrade would require weeks of testing, I wonder how they can trust upgrading to new hardware / CPU generations, changing memory capacities or even operating systems then, since any of these may be be more manifest sources of new errors? 🤔*

**[Meadowhawkz](https://bsky.app/profile/meadowhawkz.bsky.social/post/3layk3muibs2u)** on Bluesky:
> *I'm often amazed at the justifications given for not upgrading from Java 8. My favorite this year is upgrading "doesn't provide enough user value to justify an upgrade"...*

**[Özkan Pakdil](https://app.daily.dev/ozkanpakdil)** on daily.dev:
> *Banks are full of those Java 5, 6, 8 apps. I was still writing Java 8, 3 years ago, thats why I do side/open source projects 🤓 using latest tech is not easy for every engineer, I wish them good managers and nice upgrades to the latest🤞.*

### Cost of Upgrading

Upgrading a big project can require some development time which is a direct cost. As such, it's used as a clear argument why an upgrade "doesn't fit in the budget". But this hides the fact that the cost only keeps increasing, as a security issue or even bigger refactoring is waiting to become an urgent problem. We can only hope that this will not lead to a series of news reports that put your company in a bad light...

**[Ammar Yusuf](https://www.linkedin.com/in/ammaryusuf/)** on LinkedIn:
> *Because it takes time and resources. The shift from Java 8 onwards is pretty significant so it takes a lot of resources (manpower) to upgrade services, unit test, regression test, UAT test, etc. It's obviously necessary though because I've learned from [Gerrit Grunwald about the CVEs in older versions](https://www.youtube.com/watch?v=X0YEJqYWvDM).*

**Anonymous**:
> *True story, in 2022 there was a new law for taxing the poor class in the country. Employers had to adapt, so the ministry released an "app" to calculate the tax based on the income of the filthy peasants. It needed JRE6, something that couldn't be found online at the time. The class names were all lower case, the names were French. It really said everything to say about the future of the country.*

**[Johannes Bechberger](https://www.linkedin.com/in/johannes-bechberger/)** on Foojay Slack:
> *Switching costs are quite high to refactor the code, update libraries, etc. We'll probably be supporting Java for a long time for all those legacy systems out there.*

### Production Environment

Running on outdated Java runtimes is not only a problem caused by your code. Many of these applications run on server systems that were once state-of-the-art but no longer exist or have since evolved significantly.

**Anonymous**:
> *Till last year I was part of a team maintaining an application for a large municipality in the Netherlands. The app needed to be deployed as a WAR file to a Wildfly server on a machine managed by an external party that preferred to put in as little effort as possible. That server was running Java 8; the external party had no interest in upgrading, and the municipality didn't have the budget (or the desire) to get the upgrade done. You suddenly gain extra appreciation for DevOps when you're in that situation.*

**Anonymous**:
> *Stuck on Java 8 thanks to IBM Websphere that does not run on Java 9+.*

### Compatibility

When you are the creator of a Java library or tool, you're facing another problem: you want to move on to newer Java versions, but your users are still on the older ones.

**[Tim te Beek](https://www.linkedin.com/in/timtebeek/)** on Foojay Slack:
> *As a maintainer of OpenRewrite we want to support our users that are still on Java 8. For that reason, we are also stuck on Java 8... We'd be the very last ones to move away from Java 8, although thankfully our tests and services are already on 17, but the target Java version for recipes remains 8.*

### Breaking Changes in OpenJDK

OpenJDK always aimed at stability. Most old Java applications can still run on newer runtimes. But still, some changes in OpenJDK itself have blocked projects to move on.

**[Austin Lehman](https://mastodon.social/@cupofcode)** on Mastodon:
> *I totally agree with the article. Java 8 should be retired and it is crazy how many production apps I see running on really old Java versions. On the other hand, you can't blame everyone for not upgrading. [Project Jigsaw](https://openjdk.org/projects/jigsaw/) broke the contract that had been there all along without a good replacement for dynamically loading JARs. Backward compatibility was broken and thus made it hard to move forward. I think this was a bad move for Java and there's still no good alternative.*

**Anonymous**:
> *Java 8 is LTS. LTS means Long Time Spouse. Weird name, I know. But what they mean by that is if you like it so much then you should marry it.*
>
> *Jokes aside, Java 9 breaks many old applications that rely on "hacky" ways of doing things. One example for us was Webswing, we had to drop it. Now we use Java 23. Oracle took a page from Microsoft there.*

### Education

One reason I didn't consider myself is education. It turns out that many schools are still using Java 8 or older!

**[Stephen Cerruti](https://csed.social/@scerruti/113397736828096176)** on Mastodon:
> *The course I teach uses the AP Java Subset which is compatible with 6 or 7. The biggest issue for me is the lack of generics when teaching ArrayList (that's version 5 if you're keeping score.)*
>
> *But I hate teaching looping and not streams with map, filter and reduce. My community college course is similarly out of date. StringBuffer and Builder are also notable omitted.*
>
> *The college board moves slowly. They have a history of adopting a new language once it's use starts to decline. However, at the community college level at least, a lot of people are looking at curriculum alignment. An understanding of academic versus CTE paths is important, but there are [some models that help industry impact teaching](https://www.skilledtradesplaybook.org/partnering-community-colleges/).*

### Various Other Reasons...

**[Max](https://app.daily.dev/wpfprogrammer)** on daily.dev:
> *From what I see some companies get a software and when it's enough mature they let it go with time. When the final moment is coming, they have to migrate, not just update. This is a major problem.*

**[Anders](https://foojay.social/@aaspnas@toot.cafe)** on Mastodon:
> *There are many historical reasons, perhaps they started with Java ? - 8. Skill seems to play a role, people wish to stay on an old known version for as long as possible. Then there are many financial reasons, as in why spend money on upgrading what works. On the other end of the spectrum some see the upgrades as an endless line of additional billable work, upgrading one LTS at a time (\>=8 to 11, then 17, soon 21 and so on). Most perhaps simply do not see an issue/care.*
>
> *None of the reasons are any good in my opinion, but they are somewhat understandable. Security is the argument I find most useful when discussing the need for upgrades as there can be lists of known vulnerabilities that has gone unnoticed or just has not been fixed, Then again having applications that are old may be worth looking at more closely as a whole as needs / usage / environment change.*

**[Johan Vos](https://foojay.social/deck/@johanvos@mastodon.social/113520453737895161)** on Mastodon:
> *There is a small but immediate cost involved with moving to the next Java version. There is a huge, potential catastrophic *but not immediate* cost involved with sticking on old/almost-not-supported versions.*
>
> *Until the disasters become visible, many people don't want to invest and prepare. Sounds a bit like climate change. Apart from that disasters are already happening and people still don't want to change things.*

## More Reasons to Upgrade

As much as people agree and shared some of their experiences how they got stuck, there were also more arguments to make the switch to an up-to-date Java version.

**[Tim Yates](https://foojay.social/@tim_yates)** on Mastodon:
> *There's the support argument as well... It's hard to report an issue with anything when you're using a 7 year old JDK with a 5 year old dependency.*

You don't want to be the company where applicants don't want to work because they're deterred by the outdated versions still in use.

**[Jiří Hermann](https://www.linkedin.com/in/ji%C5%99%C3%AD-hermann-8926a173/)** on LinkedIn:
> *Nearly three years ago, I had an interview where someone mentioned they were still using Java 7 and were planning to upgrade to Java 8. 😄*

## Success Story

Between all the complaints about old versions being in use, I was happy to learn from Ulas that a big bank successfully did the move!

**[Ulas Ergin](https://www.linkedin.com/in/ulasergin/)** on LinkedIn:
> *We did it! If you want to know whether banks run Java 21 in production yet❓: Well..., there is a bank that recently upgraded its Core Banking System (a complete solution with all core modules like Treasury, Trade Finance, Loans, Deposits, Customer Management, Accounting, and more) from Java 8 to Java 21 with great success!*
>
> *Yes! At Credit Europe Bank N.V. and Credit Europe Bank (Suisse) SA, we are running online and batch transactions on Java21! We moved our stack from AIX/ WebSphere/ Java8 to RHEL/ Liberty/ Java21. 💪*
>
> *This robust platform serves our operations across the EU, ensuring enhanced performance, security, and scalability. This upgrade is a testament to our team's dedication and expertise. Congratulations to everyone involved for achieving this significant technological advancement!*
>
> *Here's to more technological milestones and continued excellence in serving our customers! 🎉*

## Conclusion

All developers want to use the latest features and improvements but there are many reasons why it doesn't happen...

I'm not here to blame anyone as all of these reasons are valid from the decision-makers' perspective.

I just hope that my [initial post](https://foojay.io/today/why-java-8-is-a-ticking-time-bomb-hiding-within-your-organization/) and these comments from the community can help some projects to move forward!
