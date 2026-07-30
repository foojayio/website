---
title: "You're Running Untrusted Code! | Foojay.io Today"
slug: "running-untrusted-code"
date: "2022-01-17T08:28:24+00:00"
lastmod: "2026-03-07T20:13:55+00:00"
description: "I'm afraid the deprecation of the Security Manager just added several lines to that risk, all linked to running untrusted code."
canonical: "https://blog.frankel.ch/running-untrusted-code/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2022/01/wolf-in-sheeps-clothing.jpg"
categories:
  - "JEPs"
  - "Security"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Last December, Log4Shell shortened the nights of many people in the JVM world. Worse, using the earthquake analogy caused many aftershocks after the initial quake.

I immediately made the connection between Log4Shell and the Security Manager. At first, I didn't want to write about it, but I've received requests to do so, and I couldn't walk away.

[](https://twitter.com/nicolas_frankel/status/1471140080366632968)

[

<img fetchpriority="high" decoding="async" class="alignnone size-medium wp-image-51338" src="/images/posts/2022/01/running-untrusted-code/johannes-rab-700x119.png" alt="" width="700" height="119">

](https://twitter.com/nicolas_frankel/status/1471140080366632968)

<br />

As a reminder, the Oracle team deprecated the Security Manager in Java 17. One of the arguments it based its decision on is that it was initially designed to protect against applets. Applets were downloaded from the Internet, so they had to be considered untrusted code. Hence, we had to run them in a sandbox.

Though they never said so, there's an implicit consequence of this statement: because applets are now deprecated, we run only trusted code. *Ergo*, we can let go of the Security Manager. It's plain wrong, and I'll explain why in this post.

The premise that the code that runs inside your infrastructure can be trusted is dangerous - on-premise or in the Cloud. Let me enumerate some arguments that support this claim.

Libraries can't be trusted {#h2-0-libraries-can-t-be-trusted}
-------------------------------------------------------------

Wise developers don't reinvent the wheel: they use existing libraries and/or frameworks.

Obviously, from a security point of view, it means users of such third-party code should carefully audit it. We should look for flaws: both bugs and vulnerabilities.

In two decades in the industry, I've never seen such an audit happen.

One could argue in favor of custom code. Unfortunately, it doesn't solve anything. Custom code suffers from the same issues, bugs, and vulnerabilities. Worse, it doesn't get the same attention as standard libraries, so researchers cannot spend their time to find these issues, which costs nothing.

Builds can't be trusted {#h2-1-builds-can-t-be-trusted}
-------------------------------------------------------

Imagine that you have all resources necessary to audit the code - time, money, and skills. Imagine further that the audit reveals nothing fishy. Finally, imagine that the audit's conclusion is 100% reliable.

The issue is that nothing guarantees that the JAR is the result of the build from the source code, even if the build is public. A malicious provider could replace the genuine JAR with another one.

Identities can't be trusted {#h2-2-identities-can-t-be-trusted}
---------------------------------------------------------------

A provider can sign a JAR to guarantee it's genuine. The signature is based on asymmetric cryptography:

1. The provider signs the JAR with its private key
2. It generates a public key with the private key
3. One can read the signature using the public key and check that the provider signed the JAR.

Hence, anybody can verify that a JAR comes from a specific provider.

The JDK provides the `jarsigner` tool to sign JARs. Unfortunately, most libraries don't use it. As an example, I've verified the following dependencies:

* `org.slf4j:slf4j-api:1.7.32`
* `com.fasterxml.jackson.core:jackson-core:2.13.0`
* `org.mockito:mockito-core:4.1.0`
* `org.junit.jupiter:junit-jupiter-api:5.8.2`
* `org.apache.commons:commons-collections4:4.4`
* `org.eclipse.collections:eclipse-collections:10.4.0`
* `com.google.protobuf:protobuf-java:3.18.0`
* `com.itextpdf:itextpdf:5.5.13.2`
* `com.zaxxer:HikariCP:5.0.0`
* `com.vladmihalcea.flexy-pool:flexy-pool-core:2.2.3`
* `org.springframework:spring-beans:5.3.13`
* `jakarta.platform:jakarta.jakartaee-api:9.1.0`

Among the twelve JARs above, only a single one is signed with `jarsigner`. If you're interested, it's Eclipse Collections.

However, to counter [supply-chain attacks](https://en.wikipedia.org/wiki/Supply_chain_attack), artifact repositories have started to require signed artifacts. For example, Sonatype [requires a signature](https://central.sonatype.org/publish/requirements/#sign-files-with-gpgpgp) for each uploaded file, *i.e.*, the POM, the JAR, the sources JAR, the JavaDocs JAR, etc.

One can verify the signature with Maven:

<pre class="EnlighterJSRAW" data-enlighter-language="shell">mvn org.simplify4u.plugins:pgpverify-maven-plugin:show -Dartifact=com.zaxxer:HikariCP:5.0.0</pre>

It outputs the following:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Artifact:
        groupId:     com.zaxxer
        artifactId:  HikariCP
        type:        jar
        version:     5.0.0

PGP signature:
        version:     4
        algorithm:   SHA256withRSA
        keyId:       0x4CC08E7F47C3EC76
        create date: Wed Jul 14 04:49:52 CEST 2021
        status:      valid

PGP key:
        version:     4
        algorithm:   RSA (Encrypt or Sign)
        bits:        2048
        fingerprint: 0xF3A90E6B10E809F851AB4FC54CC08E7F47C3EC76
        create date: Wed Sep 18 02:51:23 CEST 2013
        uids:        [Brett Wooldridge (Sonatype) &lt;<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="4022322534346e372f2f2c24322924272500272d21292c6e232f2d">[email&nbsp;protected]</a>&gt;]</pre>

However, none of this amounts to much. Signing doesn't assert the identity of the provider. It tells that a private key with the referenced email signed it with a private key with the referenced email. Nothing prevents a malicious actor from creating another private key with the same email or a similar one.

Features can't be trusted {#h2-3-features-can-t-be-trusted}
-----------------------------------------------------------

At this point, I think the picture looks pretty gloomy. But it's even worse than that. None of the above explains the Log4J vulnerability. The core reason is that it provides features that most developers neither need nor use.

I don't want to delve into too much detail, as it already has been explained in many places. Suffice to say that Log4J provides [lookups](https://logging.apache.org/log4j/2.x/manual/lookups.html). A lookup is an integration with another system, which allows enriching the log beyond the mere message. For example, the Spring Boot lookup allows getting Spring Boot properties. It makes sense to enrich the log, for example, with `spring.application.name`.

In all available lookups, some seem a bit fishy. For example, environment variables, system properties, or even . It's the latter that is the root cause of the Log4J vulnerability.

This kind of hidden features is not specific to Log4J. I happen to know there's a Swing-based GUI administration application inside the H2 database driver. I learned about it just by chance.

The problem is that developers use a library for their core capability, *e.g.* , logging. If one stops at that, one will never know all the library's capabilities. Hence, one will be surprised when the library does something it was not assumed to do, *e.g.*, read from a remote JNDI resource tree.

The JVM can't be trusted {#h2-4-the-jvm-can-t-be-trusted}
---------------------------------------------------------

I admit the section's title is misleading, but I couldn't find a good one following the series. It's a follow-up to the previous section, this time applied to the JVM.

The JVM provides tons of features, of which you use a handful or two. The most blatant problem is the Attach API. This API, available since Java 1.6, allows a JVM to update the bytecode already loaded into another JVM. Yes, you read it correctly: you can change the bytecode of an application that's running. Worse, if you restart the JVM, the code will be loaded again, leaving no trace.

It's a cool feature if you want to quickly monkey-patch a fix in production.  

However:

* Most people don't use it
* Most people don't know about it
* The feature needs to be explicitly disabled. It's on by default.

May I suggest that the first thing you do tomorrow is to check your infrastructure and disable it?

The Security Manager could be trusted {#h2-5-the-security-manager-could-be-trusted}
-----------------------------------------------------------------------------------

I hope that at this point, you understand the problem. A lot of code that you're running can't be trusted. Worse, I'm only considering regular applications: software built on a plugin architecture run untrusted code by definition.

The Security Manager was a JVM component that allowed you to define a white list of what an application could do, regardless of the application code. It solved all the above issues: you could run any code but only allowed it to do a limited number of things.

The Security Manager came with several drawbacks, chief amongst them is that it was a bore to configure permissions. However, there are tools to generate the policy file. Since they are automated, you need to review the discovered permissions carefully. It's easier to read through \~500 lines of configuration than 10k or 100k lines of code.

Since many didn't know about tools, few did use the Security Manager. But when it was, it was very beneficial. To prove my claim, you can read [this post](https://xeraa.net/blog/2021_mitigate-log4j2-log4shell-elasticsearch/) or jump to the conclusion: *though Elasticsearch embeds a vulnerable Log4J version, it's not susceptible to Log4Shell!*

Conclusion {#h2-6-conclusion}
-----------------------------

Security is a Non-Functional Requirement. s don't bring any competitive advantage and cost money. In short, they divert the budget from business requirements to `/dev/null`. That's at least how most business departments see it.

I think we should handle security through the lenses of risk assessment. It requires first to list all possible risks. I'm afraid the deprecation of the Security Manager just added several lines to that risk, all linked to running untrusted code.

Note that the debate regarding the deprecation of the Security Manager has not been a civil one. Since I took side **against** the deprecation, I've been publicly attacked, even to the point of plain bullying. Other voices that backed me up received similar treatment.

I don't expect reactions to this post to be any different. However, I have to tell community members what happened and what we lost.

Thanks to Peter Firmstone and [Geertjan Wielenga](https://twitter.com/GeertjanW) for their help in reviewing this post.

**To go further:**

* [Mitigate Log4j2 / Log4Shell in Elasticsearch](https://xeraa.net/blog/2021_mitigate-log4j2-log4shell-elasticsearch/)
* ["JEP 411: What it Means for Java's Security Model and Why You Should Apply the Principle of Least Privilege"](https://foojay.io/today/jep-411-what-it-means-for-javas-security-model/)
* [The Principle of Least Privilege and How JEP 411 Will Have a Negative Impact on Java Security](https://foojay.io/today/why-jep-411-will-have-a-negative-impact-on-java-security/)
* [JVM Security Focus](https://blog.frankel.ch/focus/jvm-security/)

*Originally published at [A Java Geek](https://blog.frankel.ch/running-unsecured-code/) on January 22^th^, 2022*

*[JNDI]: Java Naming and Directory Interface
*[NFR]: Non-Functional Requirement
