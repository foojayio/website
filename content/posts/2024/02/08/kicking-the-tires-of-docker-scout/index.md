---
title: "Kicking the Tires of Docker Scout"
slug: "kicking-the-tires-of-docker-scout"
date: "2024-02-08T08:47:20+00:00"
lastmod: "2024-02-08T08:47:22+00:00"
description: "Let's try Docker Scout, the Docker image vulnerability detection tool."
canonical: "https://blog.frankel.ch/kicking-tires-docker-scout/"
authors:
  - "nicolas-frankel"
image: "1636542639252_Moby-logo.png"
categories:
  - "DevOps"
tags:
related_posts:
  - "5-tips-to-create-secure-docker-images-for-java-applications"
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
  - "a-case-for-databases-on-kubernetes-from-a-former-skeptic"
  - "dockerizing-a-java-26-project-with-docker-init"
enlighterjs: true
frozen: false
---

I never moved away from Docker Desktop.

For some time, after you use it to build an image, it prints a message:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">What's Next?
  View a summary of image vulnerabilities and recommendations → docker scout quickview</pre>

I decided to give it a try. I'll use the root commit of my [OpenTelemetry tracing demo](https://github.com/nfrankel/opentelemetry-tracing). Let's execute the proposed command:

<pre class="EnlighterJSRAW" data-enlighter-language="bash">docker scout quickview otel-catalog:1.0</pre>

Here's the result:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">    ✓ Image stored for indexing
    ✓ Indexed 272 packages
  Target               │  otel-catalog:1.0        │    0C     2H    15M    23L
    digest             │  7adfce68062e            │
  Base image           │  eclipse-temurin:21-jre  │    0C     0H    15M    23L
  Refreshed base image │  eclipse-temurin:21-jre  │    0C     0H    15M    23L
                       │                          │
What's Next?
  View vulnerabilities → docker scout cves otel-catalog:1.0
  View base image update recommendations → docker scout recommendations otel-catalog:1.0
  Include policy results in your quickview by supplying an organization → docker scout quickview otel-catalog:1.0 --org &lt;organization&gt;</pre>

Docker gives out exciting bits of information:

* The base image contains 15 middle-severity vulnerabilities and 23 low-severity ones
* The final image has an additional two high-level severity
* Ergo, our code introduced them!

Following Scout's suggestion, we can drill down the CVEs:

<pre class="EnlighterJSRAW" data-enlighter-language="bash">docker scout cves otel-catalog:1.0</pre>

This is the result:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">    ✓ SBOM of image already cached, 272 packages indexed
    ✗ Detected 18 vulnerable packages with a total of 39 vulnerabilities
## Overview
                    │       Analyzed Image
────────────────────┼──────────────────────────────
  Target            │  otel-catalog:1.0
    digest          │  7adfce68062e
    platform        │ linux/arm64
    vulnerabilities │    0C     2H    15M    23L
    size            │ 160 MB
    packages        │ 272
## Packages and Vulnerabilities
   0C     1H     0M     0L  org.yaml/snakeyaml 1.33
pkg:maven/org.yaml/<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="99eaf7f8f2fce0f8f4f5d9a8b7aaaa">[email&nbsp;protected]</a>
    ✗ HIGH CVE-2022-1471 [Improper Input Validation]
      https://scout.docker.com/v/CVE-2022-1471
      Affected range : &lt;=1.33
      Fixed version  : 2.0
      CVSS Score     : 8.3
      CVSS Vector    : CVSS:3.1/AV:N/AC:L/PR:L/UI:N/S:U/C:H/I:H/A:L
   0C     1H     0M     0L  io.netty/netty-handler 4.1.100.Final
pkg:maven/io.netty/<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4e202b3a3a3763262f202a222b3c0e7a607f607f7e7e600827202f22">[email&nbsp;protected]</a>
    ✗ HIGH CVE-2023-4586 [OWASP Top Ten 2017 Category A9 - Using Components with Known Vulnerabilities]
      https://scout.docker.com/v/CVE-2023-4586
      Affected range : &gt;=4.1.0
                     : &lt;5.0.0
      Fixed version  : not fixed
      CVSS Score     : 7.4
      CVSS Vector    : CVSS:3.1/AV:N/AC:H/PR:N/UI:N/S:U/C:H/I:H/A:N</pre>

The original output is much longer, but I stopped at the exciting bit: the two high-severity CVEs, First, we see the one coming from Netty still needs to be fixed - tough luck. However, Snake YAML fixed its CVE from version 2.0 onward.

I'm not using Snake YAML directly; it's a Spring dependency brought by Spring. Because of this, no guarantee exists that a major version upgrade will be compatible. But we can surely try. Let's bump the dependency to the latest version:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;dependency&gt;
    &lt;groupId&gt;org.yaml&lt;/groupId&gt;
    &lt;artifactId&gt;snakeyaml&lt;/artifactId&gt;
    &lt;version&gt;2.2&lt;/version&gt;
&lt;/dependency&gt;</pre>

We can build the image again and **check that it still works**. Fortunately, it does. We can execute the process again:

<pre class="EnlighterJSRAW" data-enlighter-language="bash">docker scout quickview otel-catalog:1.0</pre>

Lo and behold, the high-severity CVE is no more!

<pre class="EnlighterJSRAW" data-enlighter-language="generic">  ✓ Image stored for indexing
  ✓ Indexed 273 packages
Target     │  local://otel-catalog:1.0-1  │    0C     1H    15M    23L
  digest   │  9ddc31cdd304                │
Base image │  eclipse-temurin:21-jre      │    0C     0H    15M    23L</pre>

Conclusion {#h2-0-conclusion}
-----------------------------

In this short post, we tried Docker Scout, the Docker image vulnerability detection tool. Thanks to it, we removed one high-level CVE we introduced in the code.

**To go further:**

* [Docker Scout](https://docs.docker.com/scout/)
* [4 Free, Easy-To-Use Tools For Docker Vulnerability Scanning](https://itnext.io/4-free-easy-to-use-tools-for-docker-vulnerability-scanning-bb73342c0faa)

*** ** * ** ***

*Originally published at [A Java Geek](https://blog.frankel.ch/kicking-tires-docker-scout/) on January 14^th^, 2024*

<br />

<br />
