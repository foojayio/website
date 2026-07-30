---
title: "Hacking Java XML Input via External Entity Injection"
slug: "hacking-java-xml-input-via-external-entity-injection"
date: "2021-01-14T08:37:10+00:00"
lastmod: "2021-01-14T08:45:42+00:00"
description: "In this video, I explain and demonstrate how an XXE injection attack works by extracting system data that should not be exposed."
authors:
  - "bmvermeer"
image: "https://foojay.io/wp-content/uploads/2021/01/briansnykxxe-1024x574.png"
categories:
  - "Security"
  - "Videos"
tags:
related_posts:
frozen: false
---

Java natively supplies many different options to parse XML. However, all available parsers in Java have XML eXternal Entity (XXE) enabled by default. This makes Java XML libraries particularly vulnerable to XXE injection.

We already briefly went into XXE injection problem in an earlier [blog post](https://foojay.io/today/how-to-configure-your-java-xml-parsers-to-prevent-xxe-attacks/)on foojay.io. However, let's go a little deeper. In the video below, I explain and demonstrate how an XXE injection attack works by extracting system data that should not be exposed. I also show you how you can solve this in your Java code in multiple ways.

{{< youtube 2fLPIWK5W7k >}}

In summary, with XXE enabled, it is possible to create malicious XML that reads the content of an arbitrary file on the machine. It's not a surprise that XXE attacks are part of the OWASP Top 10 vulnerabilities.
