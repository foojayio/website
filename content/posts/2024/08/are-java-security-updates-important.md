---
title: "Are Java Security Updates Important? | Foojay Today"
slug: "are-java-security-updates-important"
date: "2024-08-03T14:56:00+00:00"
lastmod: "2024-10-03T16:34:30+00:00"
description: "Equifax had numerous firewalls in place that would have done all the things a firewall should, but did not prevent the attack. Find out why!"
canonical: "https://www.azul.com/blog/the-importance-of-java-security-updates/"
authors:
  - "simonritter"
image: "https://foojay.io/wp-content/uploads/2021/08/image-1024x679.png"
categories:
  - "Security"
tags:
related_posts:
  - "java-where-the-wild-code-isnt"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
frozen: false
---

**Recently, I was in discussion with a Java user at a bank about the possibilities of using [Azul Platform Core](https://www.azul.com/products/core/) (formerly known as Zulu) to run a range of applications. One of the most significant advantages for mission-critical enterprise applications is knowing that you have access to the latest security patches and bug fixes. With Platform Core, these are provided within a defined SLA after the embargo for updates is lifted (this is essentially the point in time when Oracle releases the update to their JDK).**

Security is a very serious concern when sensitive data is in use, and potentially huge sums of money could be stolen. I was, therefore, somewhat taken aback when the user said, "We're not worried about installing Java updates as our core banking services are behind a firewall."

I will be the first to admit that I'm not an expert on computer security, but this attitude really surprised me. The way to achieve the highest level of protection is through a multi-layered approach and ensuring that any potential vulnerabilities are addressed as soon as patches are available.

This led me to do some research in this area to provide them with a convincing argument as to why they *should* be updating their Java runtimes.

The first thing to understand is that [firewalls](https://en.wikipedia.org/wiki/Firewall_(computing)) do very little to protect networks. Firewalls operate in a reactive mode and can only limit packets being sent to machines within them. It is certainly possible to use the firewall to prevent access to specific ports, throw away malformed packets, reject packets that are too small or too big and so on. The real danger does not come from packets that aren't allowed through but from the ones that *are*.

For some additional background, it is helpful to understand how vulnerabilities are described and classified.

Security vulnerabilities for pretty much any software package or platform are reported as [Common Vulnerabilities and Exposures (CVEs)](https://cve.mitre.org/about/faqs.html#what_is_cve). [MITRE, a not-for-profit organisation](https://www.mitre.org/), maintains the [CVE list of publicly disclosed cybersecurity vulnerabilities](https://cve.mitre.org/) that is free to search and use. For each CVE, which is a description of the vulnerability, there is a corresponding [Common Vulnerability Scoring System (CVSS)](https://www.first.org/cvss/specification-document) value that ranges from 0.0 to 10.0. The CVSS is determined from several metrics categorised as base, temporal or environmental. These metrics provide a breakdown of things like the attack complexity, whether privileges are required to use it, whether it can be exploited remotely across a network and so on. The CVSS values are then categorised as:

* Low: 0.1-3.9
* Medium: 4.0-6.9
* High: 7.0-8.9
* Critical: 9.0-10.0

A great example of why software should be updated with security patches is the hack carried out on Equifax, a consumer credit reporting agency. Equifax had numerous firewalls in place that would have done all the things a firewall should, but these did not prevent the attack. The initial breach of the network was through the Credit Dispute website, which was legitimately accessible through the firewall; this used the popular [Apache Struts framework](https://struts.apache.org/) (which is written in Java) to generate dynamic web pages.

In March 2017, a vulnerability in Apache Struts was identified, and a patch to address it was developed. Apache [published this](https://cwiki.apache.org/confluence/display/WW/S2-045) with advice to update machines as quickly as possible. The vulnerability, [CVE-2017-5638](https://nvd.nist.gov/vuln/detail/CVE-2017-5638), had a CVSS of 10.0, meaning it was the most critical possible and could be easily exploited. Unfortunately, Equifax did not update their Struts systems, and hackers were able to identify these machines as vulnerable. The exploit used a specific *Content-Type* value in the HTTP header that made it possible to execute system commands on the server, a classic remote-command execution attack. This was used as an entry point to the Equifax network, allowing hackers to access 143 *million* user's financial records and dozens of sensitive databases. They were also able to create more than 30 entry points into Equifax's computer systems.

During the two months before the hack was discovered, *the firewall was working perfectly.* Remember, there was nothing wrong with the *structure* or *size* of the packets being passed to Struts; they were entirely valid for the firewall.

You might be forgiven for thinking that since Java is now 26 years old and one of the most ubiquitous computer platforms on the planet, that all possible security vulnerabilities have been identified and fixed. However, if we look at the JDK, we find that it consists of over 3.7 *million* lines of code. (I got this number by running [cloc](https://github.com/AlDanial/cloc) on [JDK 16](https://github.com/openjdk/jdk16)). With so much code, it is literally impossible to categorically state that there are no vulnerabilities. Java isn't insecure; it's really powerful and complex.

Using the information provided in the [release notes for each Java update](https://www.oracle.com/security-alerts/), I gathered some statistics on CVEs that have been addressed in the JDK.

Firstly, here is a list of the highest-scoring CVSS in updates since April 2019. This was when Oracle stopped providing free public updates to either JDK 8 or JDK 11. (Technically, free updates are still available for Oracle JDK 8 but only for personal or development use. Commercial use outside of approved Oracle applications or the Oracle Cloud requires the purchase of a Java SE Subscription).

|---------------|-----------------|-----------------------|
| **Date**      | **CVSS**        | **Versions Impacted** |
| April 2021    | 5.9             | 7, 8, 11, 13, 15, 16. |
| January 2021  | 5.3             | 7, 8,                 |
| October 2020. | 5.3             | 11, 15                |
| July 2020     | 8.3             | 7, 8, 11, 14          |
| April 2020    | 8.3 (multiple). | 7, 8, 11, 13, 14      |
| January 2020  | 8.1             | 7, 8, 11, 13          |
| October 2019  | 6.8 (multiple)  | 7, 8, 11, 13          |
| July 2019     | 6.8             | 7, 8, 11, 12          |
| April 2019    | 9.0             | 8                     |

For a full view on CVEs per update of the JDK, see [Foojay.io](https://foojay.io/java-16/?tab=cve&quarter=042021&version=16.0.1):
[![](/images/posts/2024/08/are-java-security-updates-important/image-1024x679.png)](https://foojay.io/java-16/?tab=cve&quarter=042021&version=16.0.1)

As you can see, recently, things have been relatively quiet with only medium-level vulnerabilities being addressed, but there have been some high and one critical.

I also looked at the total number of vulnerabilities addressed since January 2015 and broke it down by CVSS level:

* Low: 64
* Medium: 150
* High: 56
* Critical: 59 (of which 24 were 10.0)

That gives a total of 329 vulnerabilities addressed in the JDK over the last six years.

It should be evident that to maintain the highest levels of security for your applications, keeping your Java runtimes up to date is of critical importance.

To make this simpler, Azul Platform Core provides fully supported builds of JDK 6, 7, 8, 11, 13 and 15 with all security patches backported where applicable.

Are all your Java runtimes as secure as they could be?
