---
title: "Log4Shell: Critical Log4j RCE Vulnerabilty -- Update to Version 2.15.0"
slug: "log4shell-critical-log4j-rce-vulnerabilty-update-to-version-2-15-0"
date: "2021-12-13T07:16:56+00:00"
lastmod: "2021-12-13T11:47:09+00:00"
description: "On Dec.10, 2021, a new, critical Log4j vulnerability was disclosed: Log4Shell. All current versions of log4j2 up to 2.14.1 are vulnerable."
canonical: "https://snyk.io/blog/log4j-rce-log4shell-vulnerability-cve-2021-4428/"
authors:
  - "bmvermeer"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "Security"
tags:
related_posts:
frozen: false
---

On Dec.10, 2021, a new, critical [Log4j](https://logging.apache.org/log4j/2.x/) vulnerability was disclosed: [Log4Shell](https://techcrunch.com/2021/12/10/apple-icloud-twitter-and-minecraft-vulnerable-to-ubiquitous-zero-day-exploit/).

This vulnerability within the popular Java logging framework was published as [CVE-2021-44228](https://security.snyk.io/vuln/SNYK-JAVA-ORGAPACHELOGGINGLOG4J-2314720) and categorized as `Critical` with a CVSS score of 10, which is the highest score possible. The vulnerability was discovered by Chen Zhaojun from Alibaba's Cloud Security team.

All current versions of log4j2 up to and including 2.14.1 are vulnerable. You can remediate this vulnerability by updating to [version 2.15.0 or later](https://logging.apache.org/log4j/2.x/download.html).

Many application frameworks in the Java ecosystem use this logging framework by default. For instance, Apache Struts 2, Apache Solr, and Apache Druid are all affected. Aside from those, Apache log4j is also used in many Spring and Spring Boot applications, so we suggest you check your applications and update them to the latest version.

**Brian Vermeer, Foojay Java Security Community Manager**

(Read the complete article on [Snyk.io](https://snyk.io/blog/log4j-rce-log4shell-vulnerability-cve-2021-4428/).)

[Also see the full Foojay explainer here.](https://foojay.io/log4j-cve)

<br />
