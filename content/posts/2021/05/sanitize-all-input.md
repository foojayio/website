---
title: "Sanitize All Input! Sanitize All Input! Sanitize All Input!"
slug: "sanitize-all-input"
date: "2021-05-13T07:23:33+00:00"
lastmod: "2021-05-13T07:23:35+00:00"
description: "Cross-site scripting (XSS) is a well-known issue and mostly utilized in JavaScript applications. However, Java is not immune to this!"
canonical: "https://snyk.io/blog/10-java-security-best-practices/"
authors:
  - "bmvermeer"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "Security"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Cross-site scripting (XSS) is a well-known issue and mostly utilized in JavaScript applications. However, Java is not immune to this. XSS is nothing more than an injection of JavaScript code that's executed remotely. Rule #0 for preventing XSS, according to OWASP, is "Never insert untrusted data except in allowed locations." The basic solution to this Java security risk is to prevent untrusted data, as much as possible, and sanitize everything else before using the data.

Make sure that input validation relies on allow-listing and not blocklisting. The blocklist approach sets up a collection of rules that define vulnerable input. If the input meets these rules, then the request gets blocked. However, if the ruling is too weak, then a malicious entry will still be effective. If it is too strong, it will block a valid entry. Instead, try to create a rule that describes all allowed patterns with, for instance, a regular expression, or use a well-maintained library for this.

In some cases, sanitization can be achieved by enforcing specific encoding for user input. For example, you can encode an untrusted value specifically for HTML. This way, inserting a JavaScript string will not have any effect. A good starting point is the [OWASP Java encoding library](https://github.com/OWASP/owasp-java-encoder)that provides you with a lot of encoders.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependency&gt;
   &lt;groupId&gt;org.owasp.encoder&lt;/groupId&gt;
   &lt;artifactId&gt;encoder&lt;/artifactId&gt;
   &lt;version&gt;1.2.3&lt;/version&gt;
&lt;/dependency&gt;</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">String untrusted = "&lt;script&gt; alert(1); &lt;/script&gt;";
System.out.println(Encode.forHtml(untrusted));

// output: &lt;script&gt; alert(1); &lt;/script&gt;</pre>

Sanitizing user text input is an obvious one. But what about the data you retrieve from a database, even when it's your own database? What if your database was breached and someone planted some malicious text in a database field or document?

Also, keep an eye on incoming files. The [Zip-slip](https://snyk.io/research/zip-slip-vulnerability) vulnerability in many libraries exists because the path of the zipped files was not sanitized. Zip-files containing files with paths `../../../../foo.xy `could be extracted and potentially override arbitrary files. Although this is not an XSS attack, it is a good example of why you have to sanitize all input.

Every input is potentially malicious and should be sanitized accordingly.
