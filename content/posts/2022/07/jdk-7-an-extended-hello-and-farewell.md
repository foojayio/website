---
title: "JDK 7: An Extended Hello and Farewell | Foojay.io Today"
slug: "jdk-7-an-extended-hello-and-farewell"
date: "2022-07-12T11:03:39+00:00"
lastmod: "2022-07-12T11:09:28+00:00"
description: "Did you know? Azul continues to provide updates to the Zulu builds of OpenJDK 7 until at least December 2027."
canonical: "https://www.azul.com/blog/jdk-7-the-long-hello-and-the-long-goodbye/"
authors:
  - "simonritter"
image: "/images/posts/2022/07/jdk-7-an-extended-hello-and-farewell/jdk7.png"
categories:
  - "Developer Tools"
  - "Java Core"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "are-java-security-updates-important"
  - "java-where-the-wild-code-isnt"
frozen: false
---

On July 7^th^, 2011, eleven years ago, [JDK 7 was released](https://openjdk.org/projects/jdk7/). In some ways, it was one of the more significant releases of Java. Indeed, there were some excellent technical features: [Project Coin](https://openjdk.org/projects/coin/) gave us things like try-with-resources, strings in switch and multi-catch.

However, it was two non-technical aspects of JDK 7 that made it important.

1. The first was that it was the first release since [Oracle acquired Sun Microsystems](https://www.oracle.com/corporate/pressrelease/oracle-buys-sun-042009.html). The Java community had been unsure how Java would fare under Oracle, so this release showed a solid commitment to the platform (which has been maintained since then).  
2. The second was even more important: the fact that a [Java SE specification](https://www.jcp.org/en/jsr/detail?id=336) was published through the [Java Community Process](https://www.jcp.org/en/home/index). Due to [issues around the availability of the TCK](https://en.wikipedia.org/wiki/Apache_Harmony#Difficulties_to_obtain_a_TCK_license_from_Sun) and the [Apache Harmony project](https://harmony.apache.org/), new versions of OpenJDK had been stalled since December 2006. To put this into context, the time between JDK 6 and JDK 7 (one release) was one month longer at four years and seven months than between JDK 9 and JDK 18 (nine releases). Getting things moving again was vital to keeping Java relevant to developers as applications and architectures evolved.

Another significant date for JDK 7 is July 19^th^ this year. That is when the [last update will be made available from Oracle](https://www.oracle.com/java/technologies/java-se-support-roadmap.html), even for commercially supported users. It is the end of what Oracle terms Extended Support.

However, it turns out that there are still a significant number of people who are using JDK 7. In almost all cases, this is not because users don't want to move to a newer version; they're just not in a position to be able to do so.

In many ways, this is one of Java's strengths: you can just keep using an older version because your application doesn't need features from newer releases. If you can keep your implementation of JDK 7 updated with relevant security patches and bug fixes, why change?

Extending Support of JDK 7 {#h-extending-support-of-jdk-7}
----------------------------------------------------------

Fortunately, if you are one of those users who are not in a position to migrate from JDK 7 to a newer release, Azul has a solution for you.

We will continue to provide updates (scheduled quarterly ones and any out-of-bounds) to our [Zulu builds of OpenJDK](https://www.azul.com/downloads/?package=jdk#download-openjdk) 7 until at least December 2027 (see our [Support Roadmap](https://www.azul.com/products/azul-support-roadmap/) for more information).

Our highly skilled team of Java and JVM engineers will backport all applicable changes from the current release of Java to JDK 7.

That's another five and a half years without figuring out how to migrate those trusty applications and with peace of mind that your Java runtime is as secure as possible.

If this sounds interesting, [why not contact us to find out more](https://www.azul.com/core-pricing/)?
