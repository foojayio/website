---
title: "The Persistent Threat: Why Major Vulnerabilities Like Log4Shell and Spring4Shell Remain Significant"
slug: "the-persistent-threat-why-major-vulnerabilities-like-log4shell-and-spring4shell-remain-significant"
date: "2024-09-17T16:17:07+00:00"
lastmod: "2024-09-17T16:31:15+00:00"
description: "A lurking issue has been surprisingly overlooked: the continued use of vulnerable Log4j and Spring Framework versions in many projects."
canonical: "https://snyk.io/blog/log4shell-spring4shell-threat/"
authors:
  - "bmvermeer"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "Security"
  - "Snyk"
tags:
related_posts:
frozen: false
---

*This article was originally published at [Snyk.io](https://snyk.io/blog/log4shell-spring4shell-threat/)*

**As developers, we're constantly juggling features, fixes, and deadlines. Yet, a lurking issue has been surprisingly overlooked: the continued use of vulnerable Log4j and Spring Framework versions in many projects.**

Despite the high-profile exposure of Log4Shell and Spring4Shell vulnerabilities, a shocking number of applications are still running on these ticking time bombs. This isn't just a minor oversight --- it's a major risk. We're builders at heart, but part of building is ensuring our structures are safe.

The developer's dilemma {#h2-0-the-developer-s-dilemma}
-------------------------------------------------------

As developers, we constantly balance pushing out new features and maintaining the existing projects and features. It's a balancing act that demands our time and full cognitive bandwidth. Keeping track of every project's dependencies while ensuring they're up to date can feel like an uphill battle, especially when the pressure is on to deliver new functionalities.

Amid this juggling act, critical vulnerabilities like Log4Shell and Spring4Shell can sometimes fall through the cracks, not out of negligence but due to the sheer volume of tasks we manage daily. However, it is essential to recognize that the safety and security of exciting applications are crucial aspects of software development nowadays.

The current state of Log4shell {#h2-1-the-current-state-of-log4shell}
---------------------------------------------------------------------

Remember Log4Shell? That nasty vulnerability in Apache Log4j found in 2021 that could let attackers run code on your server by logging a special string? An attacker could use a JNDI lookup with the LDAP protocol to inject a pre-compiled class file and execute malicious code.

Even in newer versions of Java, this vulnerability could lead to damage due to deserialization attacks. The attack complexity of this critical [++vulnerability++](https://security.snyk.io/vuln/SNYK-JAVA-ORGAPACHELOGGINGLOG4J-2314720) is considered very low, which makes the threat even higher than usual. Check our [++blog post++](https://snyk.io/blog/log4j-rce-log4shell-vulnerability-cve-2021-44228/) for a full breakdown of the issue.

### More than 20% of companies are still vulnerable to Log4shell. {#h3-2-more-than-20-of-companies-are-still-vulnerable-to-log4shell}

Today, many companies still have an outdated, vulnerable version of the Log4j library in one of their projects. Of all the companies that scan their production code for vulnerabilities, **21%** still have projects vulnerable to Log4Shell. That means over **60k** projects are still at risk of getting breached by a vulnerability disclosed and fixed over 2 years ago.

That's huge! Knowing that these companies already use security tooling and are actively mitigating the security issues they encounter, the actual number of vulnerable log4j versions in the wild will be much higher than that. That thought alone is not only scary but also very disturbing.

Spring4Shell in the wild {#h2-3-spring4shell-in-the-wild}
---------------------------------------------------------

Another infamous example was Spring4Shell, which was disclosed in March 2022. The vulnerability in spring-beans could also lead to malicious remote code execution. Although the attack complexity was low, and there were exploits for specific cases, the impact was less significant than that of Log4Shell. Check out the dedicated [++blog post++](https://snyk.io/blog/spring4shell-zero-day-rce-spring-framework-explained/) for more details.

By [++extending this vulnerability to Glassfish++](https://snyk.io/blog/spring4shell-rce-vulnerability-glassfish-payara/) with a new exploit in April 2022, the Snyk team proved that this vulnerability is very significant and could be misused in many more cases outside of the first exploit for tomcat.

Similar to Log4Shell, we found that Spring4Shell is still applicable in the wild. About **35%** of our customers still have the vulnerability in one of their projects. Even though the risk of a Spring4Shell breach was not as significant as that of Log4Shell, the Snyk team demonstrated a range of potential exploits by identifying and developing an exploit proof of concept (POC) for Glassfish.

This proves that seemingly lesser dangers could still lead to significant security vulnerabilities. The fact that no exploit is published yet does not imply an application can't be breached by a vulnerability!

Moreover, it shows that many Spring applications depend on older, outdated framework versions, and updating and servicing existing applications is considered unimportant. However, deep down, we know this is a ticking time bomb that can explode any minute.

Wakeup call to all who maintain applications {#h2-4-wakeup-call-to-all-who-maintain-applications}
-------------------------------------------------------------------------------------------------

Let's keep this straightforward and to the point. We all know that feeling of pride when our code finally runs without a hitch, and the last thing we want to do is go back and mess with it, especially for something as silly as updating libraries.

But here's the thing: those Log4Shell and Spring4Shell vulnerabilities aren't going to fix themselves. And honestly, they're not just tiny bugs we can ignore. They're gaping holes in the walls of our application. If you still have vulnerabilities like Log4Shell or Spring4Shell in your landscape, you are unnecessarily vulnerable to high-severity attacks!

Snyk can help you with this by detecting and helping to address security vulnerabilities in applications. It integrates with development workflows in several ways, such as through Git repositories, its Command Line Interface (CLI), or existing Continuous Integration (CI) pipelines, enabling developers to identify security risks early in the development cycle before they turn into bigger problems. Registration is free, allowing immediate access to its features. However, the real value lies in how the discovered vulnerabilities are managed and resolved post-identification.

We've got to take responsibility here. It's not just about finding a quick patch or hoping a simple update will do the trick. Sometimes, we need to make the hard call to remove or replace a vulnerable library. Yeah, it might slow us down a bit, and it's not the most exciting part of our job, but it's crucial. It's about ensuring our code is solid, not just for today but for the long haul.

So, let's not wait for someone else to fix these problems. With the proper tooling, we can spot these vulnerabilities early, but we've got to be the ones to take action. It's on us to shore up our defenses, patch up those vulnerabilities, and keep our applications safe and sound. Let's get to it, not just as coders, but as the folks who stand by their work, ensuring it's as secure as it can be.
