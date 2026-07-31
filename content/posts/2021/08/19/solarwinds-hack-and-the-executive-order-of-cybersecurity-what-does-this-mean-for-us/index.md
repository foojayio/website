---
title: "SolarWinds Hack: What Does This Mean For Us?"
slug: "solarwinds-hack-and-the-executive-order-of-cybersecurity-what-does-this-mean-for-us"
date: "2021-08-19T08:19:56+00:00"
lastmod: "2021-08-19T08:19:58+00:00"
description: "New attack vectors are becoming more and more sophisticated and are directed more and more against the value chain in general!"
authors:
  - "sven-ruppert"
image: "http://img.youtube.com/vi/ClYhATBlBl0/mqdefault.jpg"
categories:
  - "DevOps"
  - "JFrog Artifactory"
  - "JFrog Xray"
  - "Security"
  - "Videos"
tags:
related_posts:
  - "spring-remote-code-execution-vulnerability"
  - "detecting-investigating-and-verifying-fixes-for-security-incidents-and-zero-day-issues-using-lightrun"
  - "psa-the-risks-of-remote-jdwp-debugging"
  - "sast-dast-iast-and-rasp"
frozen: false
---

In the past two years, we have had to learn a lot about cybersecurity. New attack vectors are becoming more and more sophisticated and are directed more and more against the value chain in general.

But what does that mean for us? What can be done about it, and what reactions have the state already taken?

Let's start with the story that got all of this rolling and made sure that the general attention was drawn to the vulnerabilities of the available IT infrastructure.

We're talking about the SolarWinds Hack, of course. What happened here, exactly, and what is more critical: What are the lessons learned from this incident?
> This blogpost is available as video as well!  
> [![Executive Order and the SolarWinds Hack - what das it mean for us? - english - 4k](http://img.youtube.com/vi/ClYhATBlBl0/mqdefault.jpg)](https://youtu.be/ClYhATBlBl0 "Executive Order and the SolarWinds Hack - what das it mean for us? - english - 4k")

It is essential to know that the SolarWinds company produces software that is used to manage network infrastructure. With the name "Orion Platform", this software helps manage and administer network components efficiently.

If you look at the product description on the SolarWinds site, you can read that the software can monitor, analyse, and manage network infrastructure. And that's exactly what customers did with it. The company itself has around 300,000 customers worldwide, including individual companies and government organisations and corporations from a wide variety of areas.

To always stay up to date, the software platform includes an automatic update mechanism. And that was exactly what the attackers were after. They saw in the SolarWinds company a multiplier for their own activities. But how can you go about this? Well, the attackers obtained the necessary software tools by breaking into the FireEye company and infiltrating the SolarWinds network. The goal was the software development department in which the binaries of the Orion platform are created. Here, CI routes were manipulated to include compromised binaries in every build of the software. As a result, the company produced these binaries and put them into circulation through their automatic update process.

In this way, around 18,000 targets could be infiltrated and compromised within a very short space of time.

What does that mean for us now? {#h2-0-what-does-that-mean-for-us-now}
----------------------------------------------------------------------

There are two angles here.

* The first is from the consumer's point of view. However, for your own production, you must ensure that no compromised binaries are used. That means that all, and here I mean **all**, binaries used must be checked. These are the dependencies of the application and the tools used in the production process. This includes the tools such as the CI server used, e.g., Jenkins, or the operating system components of the infrastructure and Docker images.
* The second perspective represents the point of view that one has as a software distributor, meaning anyone who distributes binaries in any form. This includes customers in the classic sense and other departments that may be considered consumers of the binaries created. The associated loss of trust can cause a company severe financial damage.

What can we do to protect ourselves? {#h2-1-what-can-we-do-to-protect-ourselves}
--------------------------------------------------------------------------------

If you look at the different approaches to arm yourself against these threats, you can see two fundamental differences.

### DAST: Dynamic Application Security Testing {#h3-2-dast-dynamic-application-security-testing}

An application can be subjected to a black box test. This is called DAST, Dynamic Application Security Testing. Here you go the way a hacker would do it. The application is attacked via the available interaction points. Attack vectors based on the most common vulnerabilities are usually used here. SQL injection can be cited as an example. These attack patterns are initially independent of the technology used and represent typical security holes in web applications.

This approach has advantages as well as disadvantages. A serious disadvantage is that the tests cannot be started until the application has been developed. This means that the measures to eliminate weak points found are more costly than is the case with other approaches. On the other hand, a significant advantage of this approach is that the dynamic context can also be checked when testing the production system. Examples include weak configurations, open ports, and more.

### SAST: Static Application Security Testing {#h3-3-sast-static-application-security-testing}

The counterpart to the dynamic tests is the static tests. These tests analyse the individual components of the infrastructure involved. This includes not only the applications created but also the components involved in operating and creating the application.

A disadvantage of the SAST method is that it can only search for known security vulnerabilities. However, this point is put into perspective in that the number of known security gaps increases steadily. Still, it is also assumed that the number of known security gaps exceeds the number of unknown security gaps.

Another advantage of the SAST method is that it checks the licenses used. This area also contributes to the security of the company, albeit in a legal sense.

So what has the most significant effect when you start with the topic of security? {#h2-4-so-what-has-the-most-significant-effect-when-you-start-with-the-topic-of-security}
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------

When looking at the different approaches, it becomes clear that the proportion of directly or indirectly used binaries makes up the most significant amount in a technology stack. This means that you should also start securing precisely these parts. Unfortunately, there is only one way to ensure with SAST that it is really 100% checked directly. All other methods do not achieve this coverage, or if they do, then only in sporadic exceptional cases.

However, it should be noted that one understands the interdependencies. This is the only way to identify and extract the possible attack vector. Here it is of essential importance that not only the binaries themselves are examined, but also the associated meta-information is evaluated.

Within software development, there are two points for these analyses where such an analysis fits very well.

* The first point that most people will think of is the CI segment. Here the CI server can carry out the analysis during the execution of a build pipeline and all the checks and also take over the reporting. If the violations are too massive, the build is cancelled.
* The second position, which is ideally suited for such an analysis, is directly in the IDE. Here it can already be determined during definition whether the license used is permitted. The known vulnerabilities can also be extracted and thus provide enough information to decide whether this version of this dependency may be used.

Lesson Learned: What we learned from the SolarWinds Hack. {#h2-5-lesson-learned-what-we-learned-from-the-solarwinds-hack}
-------------------------------------------------------------------------------------------------------------------------

We learned the following things from SolarWinds. First, the attacks are no longer aimed at the final targets themselves but against the suppliers. Here one tries to reach multipliers who massively increase the effectiveness of this attack.

Likewise, you no longer only have to protect yourself against your use of compromised binaries. The newly added component means that the self-generated binaries must also be secured. Nobody wants to become a distributor of infected software. It follows that all parts involved must be subjected to permanent checks.

Reactions: The Executive Order from US President Joe Biden {#h2-6-reactions-the-executive-order-from-us-president-joe-biden}
----------------------------------------------------------------------------------------------------------------------------

Some have undoubtedly noticed that the US President issued an Executive Order on Cybersecurity. But what exactly does that mean, and who can even do something like that and under what conditions?

In the United States, the president, the "executive branch" of the US government, can issue what is known as an "executive order". Much like a CEO in a company, the executive branch has the power to make unilateral decisions that affect the behaviour of the US government. These orders must be limited to government conduct and policies and not to general laws or statutes that encroach on the rights of any individual or state. If the executive transgresses in this area, the US Supreme Court or other legal remedies can appeal and revoke all measures. Only the president, due to his status as head of state, can issue an executive order. No other officer is allowed to do this.

Significantly, executive orders are mostly limited to how the US government operates internally. For example, an executive order won't simply resolve to raise taxes in a state or issue far-reaching commercial guidelines. However, executive orders are legally binding on the behaviour of the US government if they do not violate fundamental rights or make something unconstitutional. Because of this, these commands can be found to change policy from president to president as, like a CEO, they set parts of the government's agendas.

**In this case, the Executive Order refers to how the US government monitors and regulates its software usage and sets out requirements for how that software must be documented to be sold to the government.**It doesn't dictate trading on a broad scale.

Since the US government can define its way of working, it was decided that the SBOM requirements (in preparation) will be the only way to approve software purchases. To get an idea of ​​the volume, you have to know it is an amount in the tens of billions per year. This means that any company that wants to sell to US government agencies, which are likely state and local agencies, and anyone who works with resellers who sell to the government and more, must meet all of these new standards.

When the European Union agreed on comprehensive data protection and data security laws with the GDPR, the effect was felt worldwide and quickly. In a globally networked economy, decisions by the world's largest GDP producers can set standards for other areas. With the GDPR, companies in the US and elsewhere had to immediately revise and update software and processes to continue doing business in the EU. It became a worldwide rule so as not to lose EU treaties. Likewise, with US cybersecurity order, as U.S.-based global corporations influence global software security behaviour.

Wow, what now? {#h2-7-wow-what-now}
-----------------------------------

What does that mean for me as a software developer?

Well, the effects at this point are not as severe as you might want to imagine. In the end, what it comes down to is that a complete list of all contained dependencies must be created. This means the direct and indirect dependencies.

To obtain such a dependency graph, one can, for example, use the tools that already do this, that is, the tools that can generate a complete dependency graph, for example. For this, you need a logical point through which all dependencies are fetched. A tool such as [Artifactory](https://jfrog.com/artifactory/ "Artifactory") is best, as this is where knowledge of all dependency managers used comes together. Devices such as the vulnerability scanner [JFrog Xray](https://jfrog.com/xray/ "JFrog Xray") can then access this information, scan for known vulnerabilities and licenses used, and extract the entire dependency graph.

This information can thus be generated as build information by the CI route (e.g., [JFrog Pipelines](https://jfrog.com/pipelines/ "JFrog Pipelines")) and stored in a build repository within Artifactory.

Conclusion {#h2-8-conclusion}
-----------------------------

We have seen that the new qualities of attacks on software systems, in general, will lead to advanced countermeasures. What is meant here is a complete analysis of all artefacts involved and produced.

The tools required for this must combine the information into aggregated impact graphs across technology boundaries. One point within software development that is ideal for this is the logical point via which all binaries are fed into the system.

However, the components alone are insufficient since all indirect elements can only be reached and checked by understanding the associated meta-information.

The tools used for this can then also be used to create the SBOMs.

In my opinion, it is only a matter of time before we also have to meet this requirement if we do not want to suffer economic disadvantages from the Executive Order of Cybersecurity.

If you want to find out more about the topic or test the tools briefly mentioned here, don't hesitate to contact me.  

In addition to further information, we also offer free workshops at JFrog to prepare for these challenges in a purely practical manner. And if you want to know more immediately, you are welcome to go to my YouTube channel. There I have other videos on this topic. It would be nice to leave a subscription on my YouTube channel as a small thank you.

Cheers Sven  

##################  

Youtube Channel - Outdoor Nerd - English

[![Youtube Channel English](https://yt3.ggpht.com/ytc/AAUvwniR1nyALB7XJIAL49WrhFCMjf39ALwQiAbUoOF1=s176-c-k-c0x00ffffff-no-rj)](https://bit.ly/Outdoor-Nerd "Youtube Channel - Outdoor Nerd")

Youtube Channel - Sven Ruppert - German

[![Youtube Channel German](https://yt3.ggpht.com/ytc/AAUvwnikcyASO4g2KHeCbCouznJ7oxIdBfUimaAVOC3CGFc=s176-c-k-c0x00ffffff-no-rj)](https://www.youtube.com/user/svenruppert "Youtube Channel - Sven Ruppert")

Youtube Channel - Sven Ruppert - Outdoor

[![Youtube Channel German](https://yt3.ggpht.com/ytc/AAUvwnhZCtF1ebTwYW849Uu3l8VdQj62T_eaLsRJy6w2uA=s176-c-k-c0x00ffffff-no-rj)](https://www.youtube.com/channel/UCwFH8F7TNzY5Qi2K98ddNgw "Youtube Channel - Sven Ruppert private")
