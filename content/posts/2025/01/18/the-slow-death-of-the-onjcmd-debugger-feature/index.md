---
title: "The slow Death of the onjcmd Debugger Feature"
slug: "the-slow-death-of-the-onjcmd-debugger-feature"
date: "2025-01-18T10:47:30+00:00"
lastmod: "2025-01-20T08:14:47+00:00"
description: "Learn about the rise and fall of the onjcmd Java debugger feature, from its inception to its eventual removal."
authors:
  - "johannes-bechberger"
image: "https://mostlynerdless.de/wp-content/uploads/2024/02/Figure_1-2-2000x1500.png"
categories:
  - "Debugging"
  - "Java"
tags:
related_posts:
  - "is-jdwps-onjcmd-feature-worth-using"
  - "level-up-your-java-debugging-skills-with-on-demand-debugging"
  - "where-production-policy-belongs-building-eliya-in-public"
  - "official-azul-zulu-openjdk-images-now-available-on-docker-hub"
frozen: false
---

Almost to the day, one and a quarter years ago, I published my blog post called [Level-up your Java Debugging Skills with on-demand Debugging](https://foojay.io/today/level-up-your-java-debugging-skills-with-on-demand-debugging/). In this artucle, I wrote about multiple rarely known and rarely used features of the Java debugging agent, including the onjcmd feature.{#block-a9c8a1e1-3105-4ea3-80ff-84fea4726402}

To quote my own article:{#block-a9c8a1e1-3105-4ea3-80ff-84fea4726402}
>
> JCmd triggered debugging
> ------------------------
>
> There are often cases where the code that you want to debug is executed later in your program's run or after a specific issue appears. So don't waste time running the debugging session from the start of your program, but use the `onjcmd=y` option to tell the JDWP agent to wait with the debugging session till it is triggered via `jcmd`.{#block-19cdbfac-1c23-4648-af2f-6bd2f85951c2}
>
> A similar feature [long existed](https://mail.openjdk.org/pipermail/serviceability-dev/2019-May/028227.html) [in the SAPJVM](https://help.sap.com/docs/btp/sap-business-technology-platform/debug-application-running-on-sap-jvm). In 2019 [Christoph Langer](https://www.linkedin.com/in/christoph-langer-764280208) from SAP decided to [add it to the OpenJDK](https://bugs.openjdk.org/browse/JDK-8223456), where it was implemented in JDK 12 and has been there ever since.{#block-b0b4cb00-fa27-4fac-b37b-98ce5336794e}

The alternative to using this feature is to start the debugging session at the beginning and only connect to the JDWP agent when you want to start debugging. But this was, for a time, significantly slower than using the onjcmd feature ([source](https://mostlynerdless.de/blog/2024/02/09/is-jdwps-onjcmd-feature-worth-using/)):{#block-8431965e-7d8d-4896-bfdc-fac40a8e4db1}
![This image has an empty alt attribute; its file name is Figure_1-2-2000x1500.png](https://mostlynerdless.de/wp-content/uploads/2024/02/Figure_1-2-2000x1500.png)

After the feature had been merged, it was decided that it needed a [CSR](https://wiki.openjdk.java.net/display/csr/CSR+FAQs) because it was user-facing. But the feature wasn't it without its opponents, and the CSR was only accepted because the feature had already been merged:{#block-dbfc0d16-f526-41eb-9750-c10c54b7076b}
> After consultation with others including [Alan Bateman](https://bugs.openjdk.org/secure/ViewProfile.jspa?name=alanb) and [Mark Reinhold](https://bugs.openjdk.org/secure/ViewProfile.jspa?name=mr), I've concluded there is lack of technical consensus on this appropriateness of the feature in its current state to the platform.{#block-83691613-924b-40fe-a663-df3be2a58182}
>
> As noted in the CSR FAQ (<https://wiki.openjdk.java.net/display/csr/CSR+FAQs>):{#block-a28effaf-b693-4aff-b74e-5397854433f4}
>
> "In exceptional circumstances, the need for a CSR review may be recognized only after a push has already occurred. In such cases, a retroactive CSR review can be conducted. The results of such a retroactive review may require updates to the change, up to and including complete removal of the change."{#block-db9d76a9-2073-4849-b004-b4817a7e7a8c}
>
> Administratively, I'm retroactively voting to approve this CSR as it has already been pushed in JDK 12; however, given the lack of consensus, I've filed the follow-up bug JDK-8226608 to:{#block-6bd9f8b7-446d-480a-8d50-31df778f6f52}
>
> * hide the onjcmd option from the help output
> * explore hiding "VM.start_java_debugging" from the "jcmd help"
>
> {#block-4a3e6185-63b9-4b67-aede-2418431e078c}
>
> This bug needs to be addressed before JDK 13 ramdown 2.[JOE Darcy in His Comment to THE CSR](https://bugs.openjdk.org/browse/JDK-8223456?focusedId=14273234&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-14273234){#block-80bbb16b-d786-47e5-bb83-a61eb28fd256}

So, it was decided to remove it with [JDK-8226608](https://bugs.openjdk.org/browse/JDK-8226608), as Joe Darcy mentions in his comment with the CSR [JDK-8227078](https://bugs.openjdk.org/browse/JDK-8227078):{#block-e017e73f-b8b5-4f07-ba97-b4f6d78d0076}
>
> Summary
> -------
>
> Hide the onjcmd option of the jdwp agent and the corresponding VM.start_java_debugging command, without removing the functionality outright.{#block-e3693c23-7b86-42cb-ae93-abdbba91549b}
>
> Problem
> -------
>
> According to JDK-8223456 the onjcmd option and the corresponding diagnostic command should be hidden as far as possible.{#block-97ef9322-75ff-4853-81ec-733b449cd764}
>
> Solution
> --------
>
> The onjcmd option is not mentioned in the help output of the JDWP agent anymore. The corresponding diagnostic command VM.start_java_debugging is now registered as hidden, so it would not be included in the list of supported commands by jcmd or via the mbeans.{#block-db02fc36-4ce3-4be4-88ff-992cab61d3fd}
>
> Apart from that the functionality is still working.{#block-3751fc7a-3756-4fb6-9c10-6d5e706001bd}

This is probably one of the major reasons nobody wrote about it: nobody outside the SAP, the few people involved in its inception, and the JDWP agent knew about it. If you search the internet for the onjcmd feature, you will likely only encounter articles from this very blog (and its various cross-posts).{#block-9ce28839-7560-49c8-b089-acbf0181bb73}

So this feature was a hidden gem for a while, but as discussed in my article [Is JDWP's onjcmd feature worth using?](https://mostlynerdless.de/blog/2024/02/09/is-jdwps-onjcmd-feature-worth-using/), this feature is not worth using anymore:{#block-924eb7f8-1532-4942-8ddc-79455dc049a6}
> Between JDK 11.0.3 and JDK 21, there have been improvements to the OpenJDK, some of which drastically improved the performance of the JVM in debugging mode. Most notable is the fix for [JDK-8227269](https://bugs.openjdk.org/browse/JDK-8227269) by Roman Kennke. \[...\]{#block-09d202dd-8924-4dba-a4a7-4eff47d512ff}
> ![This image has an empty alt attribute; its file name is Figure_1.png](https://mostlynerdless.de/wp-content/uploads/2024/02/Figure_1.png)
>
> This clearly shows the significant impact of the change. 11.0.3 came out on Apr 18, 2019, and 11.0.9 on Jul 15, 2020, so the onjcmd improved on-demand debugging for almost a year.{#block-0a163ea3-c591-4b3b-994b-c89397060442}

So, the feature has been hidden and has offered no benefits since mid-2020. It's just sitting in the OpenJDK, likely unused and unknown by most developers. The last thing to do is remove the feature. For this, I created the [CSR](https://bugs.openjdk.org/browse/JDK-8341406) with the help of Christoph:{#block-a53ab61d-e1e4-40d9-9e79-797862596dc3}
> > Remove the onjcmd option from the jdwp agent, because it is considered obsolete and unused.{#block-5ff5b947-24ef-46a2-a79c-606e1fd497aa}
> >
> > \[...\]{#block-c3e696a2-70d6-42d9-9fdf-ca411858f145}
> >
> > However, it is not needed anymore, as the performance issue has been fixed, and the networking/open port topic can easily be handled by infrastructure. Furthermore, the option is rarely used due to being hidden via JDK-8227078. So, we should remove the feature along with its coding to reduce complexity.{#block-7adbca60-8826-46d8-bc91-58e18ee6c7df}
> >
> > Remove the onjcmd option from the JDWP agent and eliminate the corresponding VM.start_java_debugging command in the JVM. This will clean up the agent code and remove obsolete functionality that is no longer needed or used.{#block-e699bf5d-09dc-4d32-8069-4022b365fcb3}

For such CSRs, one also needs to state the compatibility risks. As explained before, there are possibly none outside of SAP. Together with my related [PR](https://github.com/openjdk/jdk/pull/21387), this will remove the feature from the OpenJDK, and JDK 24 will most probably be the first JDK since JDK 12 without the onjcmd debugger feature. RIP.{#block-bdf2ffcb-e639-4c35-a1b9-eb7c6d31ffc5}
![This image has an empty alt attribute; its file name is image.png](https://mostlynerdless.de/wp-content/uploads/2024/10/image.png)

Conclusion
----------

In this week's artilce, we saw the life cycle of the onjcmd feature, from its inception to its removal. As software developers, we shouldn't be too afraid to remove features we or our teams implemented. Every unused removed feature is a good feature. Large projects, like the OpenJDK, tend to collect lots of features that were great years ago but fell out of use and clog the source code. In my opinion, this also includes other JDWP agent features like onthrow. To be slightly more controversial, why not start deprecating the UI stack and moving it into a separate project like JFX?{#block-162b6682-0cfa-425d-bb25-b26651e7edf9}

But what do you think? Do you have a use for onjcmd and will miss it? Whatever your opinion is, I hope you liked my article. See you in my next article.{#block-a834d5a3-50f2-4a45-8418-bc4395db302c}

*This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone. Thank you to Christopher Langer and Cris Plummer for the help with the CSR, and the PR.* *The article first appeared in October 2024 on my [personal blog](https://mostlynerdless.de).*{#block-86ba1675-cd84-4424-9da7-0df7643e3c9e}

P.S: Stuart Marks, aka Dr. Deprecator, likes the removal of unused features. I managed to meet him at Devoxx Belgium this week:{#block-e51d6153-39b1-488a-8fda-3a33334d30bb}
![This image has an empty alt attribute; its file name is IMG_3764-2-2000x2000.jpeg](https://mostlynerdless.de/wp-content/uploads/2024/10/IMG_3764-2-2000x2000.jpeg)
