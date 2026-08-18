---
title: "Overview of JVM Vendors and Choosing the Right One"
date: "2020-07-22T13:07:29+00:00"
lastmod: "2021-08-23T12:53:50+00:00"
description: "Everything about JVM vendors & OpenJDK distributions, upgrading from one Java release to the next, and information on where to get the JDK Flight Recorder."
authors:
  - "hirt"
image: "Favicon-3-2.png"
categories:
  - "Java Core"
tags:
related_posts:
  - "boxlang-ai-deep-dive-part-6-of-7-memory-systems-rag-building-ai-that-remembers"
  - "embracing-jvm-unified-logging"
  - "learning-java-as-a-first-language"
  - "running-single-file-java-source-code-without-compiling-part-1"
frozen: false
---

Since you're reading this blog, chances are that you're writing software which will eventually run on a JVM. Most of you are using the Java language. Many of you are using a variety of other languages that target the JVM, such as Scala, Kotlin, Clojure, Groovy, (J)Ruby etc. Eventually you'll need to decide on which JDK/JRE to deploy your software on in production. This is much easier said than done. There are quite a few different vendors out there, providing support and taking responsibility for the binaries they produce. They can have different support lengths for specific versions, and whereas you can sometimes find a vendor providing extended support for a version that has been officially end-of-lifed at Oracle, you may not find builds with the latest fixes in them publicly available. You'll need to get those directly from the vendor.

After trying to figure out what's what, I thought I'd simply write a blog post on the various JDKs available out there. This is especially important, since you might be consuming your JDK from a container provided by a third party, e.g. Docker Hub, and you may not know exactly what you're getting\[1\].

### Release Version Chicken Race

Typically most companies will require that you keep your dependencies up-to-date. For example, if you've written something with a dependency on Tomcat, you are pretty likely to keep your dependencies up-to-date. GitHub may even warn you if you're running with a version that has known security implications. However, not everyone is keeping their JDKs/JVMs up-to-date. Which is funny, in a way, since everything you'll be running could be affected.

Let's take the Oracle JDK as an example. JDK 7 was GA in July 2011. Publicly available updates and fixes ceased in April 2015. Oracle's Premier Support ended in July 2019, and even the Extended Support ends 2022.

Let's say you're running on JDK 7. If you got your JDKs from Oracle, without a support contract chances are that the latest version of JDK 7 you got was built in 2015. You are now five (5!) years behind on critical security patches.

In other words, if you're still running your software on JDK 7, you may want to at least begin upgrading to 8. JDK 7 is dying and support is being dropped left and right. If you aren't buying support and have someone provide you with (security) patches, you might want to accelerate the effort. Also, this particular upgrade (7-\>8) should be relatively painless -- in most cases it will be a drop in replacement. Now, if you're not running a JDK 7 with the latest patches (sanity check -- was the JDK at least built this year?), you may not only be missing out on bug fixes, but you may also be missing out on [security patches](https://www.cvedetails.com/version-list/93/19116/1/Oracle-JDK.html?sha=b856721542b66953c859bd95be067255dd4c6098&order=1&trc=188)\[2\]!

The same arguments could be made for JDK 8 as well, on a slightly pushed out time-line. The good news is that there are still public (and free) updates coming from the OpenJDK 8 maintenance project. That said, there are plenty of advantages for upgrading to JDK 11+, better performance being one of them.

Now, when the new, faster, release schedule was announced, Oracle announced that every 3 years, there would be an LTS (Long Term Support) version of Java. The releases in between the LTS releases would only be supported until the next release came out. Most vendors have adopted the same support scheme, which means that, at the time of writing, you should not be running ANYTHING on JDK 9,10,12 and 13 (unless you're using Azul distributions, see \[3\]). They are not supported. Running them will only mean that you are lacking bug- and security fixes. To take a somewhat arbitrary example -- if you stopped upgrading JDK 8 after 8u74, you are literally lacking [thousands of fixes](https://bugs.openjdk.java.net/secure/Dashboard.jspa?selectPageId=19531).

At the time of writing this blog, the new CPU (Critical Patch Update) releases have just been published, and these are the releases you should be running in July 2020 (sooner rather than later):

* JDK 8u262
* JDK 11.0.8
* JDK 13.0.4 \[3\]
* JDK 14.0.2

If you're running anything else in production, without a support contract, it could be argued you're not doing things quite right.

## What's what?

OpenJDK, being open sourced, has builds provided by plenty of vendors. Here is a**non-exhaustive** list of some vendors shipping supported versions of OpenJDK (in alphabetical order, distribution(s) in parenthesis):

* [AdoptOpenJDK](https://adoptopenjdk.net/)
* [Alibaba](https://github.com/alibaba/dragonwell8) (Dragonwell)
* [Amazon](https://aws.amazon.com/corretto/)(Corretto)
* [Azul](https://www.azul.com/downloads/zulu-community/?architecture=x86-64-bit&package=jdk) (Zulu, Zing)
* [BellSoft](https://bell-sw.com/pages/downloads/)(Liberica)
* [Red Hat](https://access.redhat.com/articles/1299013) (Red Hat Builds of OpenJDK)
* [Oracle](http://java.oracle.com/) (Oracle JDK, Oracle OpenJDK)
* [SAP](https://sap.github.io/SapMachine/) (SapMachine)

These providers usually ship distributions with pretty much the same bits from the OpenJDK repository, sometimes differing by what features are enabled, for example like a GC (Shenandoah / Red Hat), or by adding proprietary features like a new compiler (Falcon / Azul (Zing)). Some vendors have a free distribution (e.g. Oracle OpenJDK, Azul Zulu) and one that requires a commercial license (Oracle JDK, Azul Zing). Which vendor and distribution you should select depends on your demands -- e.g. which vendor can provide reliable support to you (Oracle is one of the biggest contributors to OpenJDK), or which one provides the feature you need at a price point you can afford (e.g. JDK Flight Recorder on JDK 8 without the need for a commercial license, or support for a specific GC or compiler).

There are also upstream builds, not supported by anyone, built on Red Hat infrastructure and hosted by AdoptOpenJDK. For example, if you get a JDK 8 from Docker Hub (openjdk/jdk8u252, openjdk/jdk8), that is what you would [get](https://github.com/docker-library/openjdk/blob/master/8/jdk/Dockerfile).

## Where to get JFR -- Public Service Announcement

As you probably know, JDK Flight Recorder, a technology close to my heart, has been backported to JDK 8. Since we're talking about where to get your JVMs and versions, I thought I'd include a small table for which provider will be including JFR in what version of their JDK 8 builds.

|                                                             VENDOR                                                             | FIRST JDK 8 VERSION WITH JFR |                     RELEASE DATE                      |          DOCKER IMAGE          |
|--------------------------------------------------------------------------------------------------------------------------------|------------------------------|-------------------------------------------------------|--------------------------------|
| [Azul (Zulu)](https://www.azul.com/downloads/zulu-community/?architecture=x86-64-bit&package=jdk)                              | u212\* (u262+ recommended)   | 2019-04-16                                            | azul/zulu-openjdk/8            |
| [AdoptOpenJDK](https://adoptopenjdk.net/releases.html?variant=openjdk8&jvmVariant=hotspot)                                     | u262                         | 2020-07-16                                            | adoptopenjdk/8u262             |
| Red Hat                                                                                                                        | u262                         | 2020-07-15                                            | In Fedora and RHEL             |
| [Amazon (Corretto)](https://github.com/corretto/corretto-8/releases)                                                           | u262                         | 2020-07-14                                            | amazoncorretto:8u262           |
| [Bell-Soft (Liberica)](https://bell-sw.com/announcements/2020/07/14/The-Liberica-8u262-11_0_8-14_0_2-are-generally-available/) | u262 (separate binary)       | 2020-07-14                                            | N/A                            |
| Upstream builds \[4\]                                                                                                          | u272                         | [2020-10-20](https://www.oracle.com/security-alerts/) | openjdk/jdk8u272, openjdk/jdk8 |

Where to get JDK Flight Recorder

## Summary

* Use the latest version of an LTS which is still supported, or the latest version
* Use a supported build in production (even if you haven't bought support)

Thanks to Mario Torre, JP Bempel and Gil Tene for feedback!

\[1\]: Mystery meat OpenJDK builds strike again: https://mail.openjdk.java.net/pipermail/jdk8u-dev/2019-May/009330.html

\[2\]: To check the vulnerabilities you may be exposed to, see e.g. [https://www.cvedetails.com/version-list/93/19116/1/Oracle-JDK.html?sha=b856721542b66953c859bd95be067255dd4c6098\&order=1\&trc=188](https://www.cvedetails.com/version-list/93/19116/1/Oracle-JDK.html?sha=b856721542b66953c859bd95be067255dd4c6098&order=1&trc=188)

\[3\]: Upstream JDK 13u is being supported, and Azul has announced 13 to be "Medium Term" supported -- you can keep getting updates for JDK 13 for Azul distributions.

\[4\]: These are built by Red Hat and hosted by AdoptOpenJDK, and are different from Red Hat's and AdoptOpenJDK's supported builds.

**Note:** Used with permission and thanks -- originally written by and published on [Marcus Hirt's blog](http://hirt.se/blog/?p=1235).
