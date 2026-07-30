---
title: "Where do you get your Java?"
slug: "where-do-you-get-your-java"
date: "2024-01-16T14:19:33+00:00"
lastmod: "2024-01-16T14:22:16+00:00"
description: "Which Java should you download and use? A brief history of Java, the current Java distribution options, and which stand above the rest."
canonical: "https://developer.ibm.com/articles/awb-where-do-you-get-your-java/"
authors:
  - "rich-hagarty"
image: "https://foojay.io/wp-content/uploads/2024/01/EclipseOpenJ9Performance.png"
categories:
  - "Java"
  - "Opinion"
tags:
related_posts:
  - "are-java-security-updates-important"
  - "book-review-openjdk-migration-for-dummies-3"
  - "analyzing-and-tuning-warm-up-of-the-jvm-with-azul-zulu-prime-builds-of-openjdk"
  - "jurassic-jdk-migrate-or-extinct"
frozen: false
---

Today's Java is vastly different, although it is still true to its root principles of robustness, portability, and ease of programming. Your options for where to get your Java have similarly evolved.

If you download Java directly, you might think that you can only get an official version from Oracle. This is no longer the case. In fact, there are a number of vendors that now provide their own Java distributions that are 100% compatible with the official Java specification.

So, you might be wondering: Are they all the same? If not, what differentiates them? How do I know which one to use?

To learn more about the ever-changing Java ecosystem, read on. This article will cover:

* The history of Java, covering the evolution of Java from Sun to Oracle, and the adoption of OpenJDK.
* The Java Landscape, discussing the currently available Java distributions and multi-vendor support.
* Which Java distributions, if any, stand above the rest.

History {#h2-0-history}
-----------------------

The Java programming language has a long and storied history. Developed by Sun Microsystems in 1995, Java quickly gained popularity due in large part to its portable runtime, which included a Java Virtual Machine (JVM), which allowed developers to "write once and run anywhere."

The Java platform consists of a JDK, or Java Development Kit, which includes the class libraries and the JVM that you need to run Java applications. The Java platform also includes developer tools such as compilers and debuggers.

Some key milestones in the development and support of Java include:

* **Open Source with OpenJDK**. In 2007, Sun decided to open source Java. As a part of this effort, they developed the OpenJDK, which was a free and open source implementation of the Java SE (Standard Edition) platform.
* **Oracle steps in**. Oracle continued this effort when it acquired Sun in 2010. They first began to use OpenJDK components in 2011 with the release of Java SE 7.
* **Community involvement** . In 2017, a community of Java developers started the AdoptOpenJDK open source project. Its goal was to build, test, and distribute binaries for OpenJDK. As this effort continued to gain momentum and popularity, the AdoptOpenJDK project was transitioned in 2021 into the [Adoptium Working Group](https://adoptium.net/ "Adoptium Working Group") under the direction of the [Eclipse Foundation](https://www.eclipse.org/ "Eclipse Foundation").
* **Oracle licensing changes**. It's important to remember that even though OpenJDK is open source, Oracle is still a major contributor to the development of Java, along with other vendors within the OpenJDK community. As a result of their leadership position and legacy, the non-OpenJDK Oracle Java distribution (Oracle JDK) continued to be the most popular. And since it was "free to use," there was really no incentive to use any other distribution. But this all started to change when licensing fees started to be charged for the commercial use of Java in 2019.

These changes included:

* In 2019, a licensing fee was introduced for production use of Java 8, with fees based on the number of "uses" (number of servers, desktops, and authorized users).
* In 2021, the license for Java 17 allowed for free downloads and for deployment, but only if the end product was provided free of charge.
* In 2023, the license model for the Oracle JDK release was changed to be based on the number of employees of the company.

All of these changes led to some confusion for both developers and businesses. Large enterprise companies were potentially facing additional costs, even if their Java usage was relatively small. Many companies turned to their software providers and partners for direction and help on how to navigate this "new" Java distribution and usage licensing model.

The changing Java distribution landscape {#h2-1-the-changing-java-distribution-landscape}
-----------------------------------------------------------------------------------------

This "confusion" provided an opportunity for many software vendors to re-evaluate how to provide and support their Java customers. Many saw an opportunity to:

* Provide their own OpenJDK based distribution of Java
* Potentially generate support revenue
* Alleviate customer fears of ever-changing licensing rules and fees

As a result, the list of vendors providing their own OpenJDK Java distribution has grown over the past couple of years. It includes:

* Alibaba Dragonwell
* Amazon Corretto
* Azul Zulu builds of OpenJDK
* BellSoft Liberica
* Eclipse Temurin
* IBM Semeru Runtimes
* Microsoft Build of OpenJDK
* OpenJDK builds by Oracle
* Red Hat Build of OpenJDK

Since they are all based on the same OpenJDK class libraries, there really is no difference when it comes to compatibility with the Java implementation. But there are differences, and this typically involves support, both with what platforms are supported and the level of customer support offered. For example, the Red Hat distribution is carefully configured to run effectively on RHEL. Same for Microsoft, which provides their customers a distribution that is ensured to run efficiently on the Azure cloud platform. And of course, they offer their customers full support in those environments.

Some distributions try to differentiate themselves by offering enhanced 24/7 support and technical help on any platform. Others offer a premium (for fee) version that includes additional tools and technologies.

IBM Semeru Runtimes stands apart {#h2-2-ibm-semeru-runtimes-stands-apart}
-------------------------------------------------------------------------

One Java distribution that clearly differentiates itself from the pack is IBM Semeru Runtimes.

First of all, like the Oracle JDK, Semeru Runtimes has a long and storied history. IBM has always had a support-minded relationship with its customers, and has had its own Java distribution for over 25 years. It was originally named IBM SDK for Java and was needed to ensure that it could run on all IBM devices and architectures, from hand-held scanners to mainframes. This continues with Semeru Runtimes, which uses the OpenJDK libraries to ensure compatibility with all other distributions.

Semeru provides full support for all major operating systems and architectures because IBM software can and does run everywhere. That not only includes the POWER and mainframe platforms that developers commonly associate with IBM, but also x86 and ARM64 platforms used throughout industry.

The OpenJ9 Advantage {#h2-3-the-openj9-advantage}
-------------------------------------------------

The other major difference is the JVM. Instead of the default "HotSpot" JVM from Oracle, Semeru Runtimes is the only Java distribution that is powered by the Eclipse OpenJ9 JVM. This JVM also has a lot of history. Originally developed by IBM over 25 years ago as the J9 JVM, it was open sourced to the Eclipse Foundation over 6 years ago. It is renowned for its small footprint, fast start-up, and fast ramp-up time. (See more in [this performance blog post](https://eclipse.dev/openj9/performance/ "this performance blog post").)

<img fetchpriority="high" decoding="async" class="size-medium wp-image-105640" src="/images/posts/2024/01/where-do-you-get-your-java/EclipseOpenJ9Performance-700x304.png" alt="" width="700" height="304">

<br />

Foundation for Open Liberty and Websphere Liberty {#h2-4-foundation-for-open-liberty-and-websphere-liberty}
-----------------------------------------------------------------------------------------------------------

Semeru Runtimes also drives the popular [Open Liberty](https://www.openliberty.io/ "Open Liberty") and [Websphere Liberty](https://www.ibm.com/products/websphere-hybrid-edition/liberty "Websphere Liberty") Java application runtimes, which means it is currently powering thousands of enterprise Java applications across the largest companies in the world, and has been for more than a decade.

Benefits of Semeru Runtimes {#h2-5-benefits-of-semeru-runtimes}
---------------------------------------------------------------

To summarize, Semeru Runtimes offers the following benefits:

* **No cost**. It is free to download and use in production.
* **Stable and secure**. It is Java SE TCK certified.
* **Multi-platform**. All standard architectures are supported, plus IBM Z and Power systems.
* **Powered by OpenJ9**. Its open-source JVM is optimized for cloud-native and constrained environments.
* **High performance**. OpenJ9 can have start-up times of up to 50% faster than HotSpot.
* **Cloud-optimized**. OpenJ9 technologies can provide up to 50% smaller containers.
* **Semeru Cloud Compiler (aka JITServer)** . The [JIT-as-a-Service technology](https://developer.ibm.com/blogs/just-in-time-compilation-as-a-service "JIT-as-a-Service technology") speeds up application ramp-up times, which is essential for scaling in microservice environments.
* [**Semeru InstantOn**](https://foojay.io/today/how-we-developed-the-eclipse-openj9-criu-support-for-fast-java-startup/ "**Semeru InstantOn**"). The InstantOn technology works seamlessly with Liberty to deliver native image-like start-up times (in the milliseconds) that are essential for serverless applications (including Jakarta EE and MicroProfile applications).

These performance advantages can result in significant resource and cost savings - especially when deploying containerized Java applications into a Kubernetes cloud environment.

Where to get Semeru Runtimes {#h2-6-where-to-get-semeru-runtimes}
-----------------------------------------------------------------

Semeru Runtimes comes in 2 flavors: an Open Edition and a Certified Edition. Both editions are free to download and use in production. The only difference between the editions is the licensing and supported platforms.

Semeru Runtimes Open Edition:

* Open source license (GPLv2+CE)
* Java versions: Java 8 (LTS), 11 (LTS), 16, 17 (LTS), 18, 19, 20 (21 available soon)
* Supported architectures: x64, x86, ppc64le, ppc64, s390x, aarch64
* Supported operating systems: Linux, Windows, macOS, AIX
* Download available from [IBM](https://developer.ibm.com/languages/java/semeru-runtimes/downloads/ "IBM")

Semeru Runtimes Certified Edition:

* IBM license
* Java SE TCK certified.
* Java versions: Java 11, 17 (21 available soon)
* Supported architectures: x64, ppc64le, ppc64, s390x, aarch64
* Supported operating systems: Linux, AIX, z/OS
* Download available from [IBM](https://developer.ibm.com/languages/java/semeru-runtimes/downloads/ "IBM") and [Adoptium - Marketplace](https://adoptium.net/marketplace/ "Adoptium - Marketplace")

Commercial support for IBM Semeru Runtimes and OpenJDK is available with [IBM Runtimes for Business](https://www.ibm.com/products/support-for-runtimes "IBM Runtimes for Business").

Summary and next steps {#h2-7-summary-and-next-steps}
-----------------------------------------------------

The Java language has come along way in its over 25 year history. While its popularity has always remained constant, the deployment of Java has seen major changes occur over the past couple of years. With the adoption of the open source OpenJDK, a number of software vendors now distribute their own OpenJDK Java SE distributions.

Leading the pack is IBM, with its Semeru Runtimes distribution, which supports all of the popular architectures and operating systems. Besides offering great support and performance, it is the only distribution powered by the Eclipse OpenJ9 JVM.

Check out [IBM Semeru Runtimes](https://developer.ibm.com/articles/explore-options-for-downloading-ibm-semeru-runtimes/ "IBM Semeru Runtimes") today.

<br />

<br />
