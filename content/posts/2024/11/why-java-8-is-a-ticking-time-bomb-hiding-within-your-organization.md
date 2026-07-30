---
title: "Why Java 8 is a Ticking Time Bomb Hiding Within Your Organization"
slug: "why-java-8-is-a-ticking-time-bomb-hiding-within-your-organization"
date: "2024-11-15T11:16:00+00:00"
lastmod: "2024-12-12T09:33:07+00:00"
description: "When I spoke to developers at Devoxx in Belgium in October, I was surprised to learn how many of them are maintaining systems that are still running on - by Frank Delporte"
canonical: "https://webtechie.be/post/2024-10-30-java-8-ticking-time-bomb/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2024/10/azul-roadmap-end-of-2024.png"
categories:
  - "Java"
  - "Java Core"
  - "Opinion"
  - "Performance"
tags:
related_posts:
  - "java-23-has-arrived-and-it-brings-a-truckload-of-changes"
  - "java-22-whats-new"
  - "foojay-podcast-57"
frozen: false
---

When I spoke to developers at Devoxx in Belgium in October, I was surprised to learn how many of them are maintaining systems that are still running on Java 8 (released in 2014). One of them even still has a Java 5 application in production, with a runtime of 20 years old!

I know I'm biased, as I experiment extensively with the latest Java versions to learn what improvements they bring. But it hurts my heart to think of all those developers maintaining old systems, missing out on all the coding and performance improvements that newer versions offer. In this post, I want to highlight some of the many reasons why staying on Java 8 is a ticking time bomb...

Critical Reasons to Upgrade {#h2-0-critical-reasons-to-upgrade}
---------------------------------------------------------------

Yes, your old applications may have been running stable for many years. But isn't that precisely the thread? How long since someone has looked into the sources, and are they even still available? How long will it be before the system they are running on breaks? Does your team still know how all features are implemented? These are critical questions that should make you feel uncomfortable.

While there are many reasons to move on!

### Fixed Security Vulnerabilities {#h3-1-fixed-security-vulnerabilities}

As you can see on the [Azul roadmap](https://www.azul.com/products/azul-support-roadmap/), Oracle support has ended for Java 6 in 2018 and 7 in 2022 and will end for Java 8 in 2030. Azul will support 6 and 7 until 2027 and 8 until 2030. But that means there is an end date for all these versions, or it has already passed!

Java versions that are still supported get a new build every three months containing fixes for Common Vulnerabilities and Exposures (CVE). As you can see in the Azul CVE search tool, multiple CVEs are fixed with every release, and some have a high impact score.

Or look at it from another perspective: if you didn't upgrade every three months or longer, your system is vulnerable to many CVEs at this very moment!

<figure class="wp-block-gallery has-nested-images columns-default wp-block-gallery-1 is-layout-flex wp-block-gallery-is-layout-flex">
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2024/11/why-java-8-is-a-ticking-time-bomb-hiding-within-your-organization/azul-roadmap-end-of-2024.png" target="_blank" rel="noopener"><img fetchpriority="high" decoding="async" width="1024" height="627" data-id="114663" src="/images/posts/2024/11/why-java-8-is-a-ticking-time-bomb-hiding-within-your-organization/azul-roadmap-end-of-2024-1024x627.png" alt="The Azul roadmap, status end of 2024" class="wp-image-114663"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2024/11/why-java-8-is-a-ticking-time-bomb-hiding-within-your-organization/cve-high-score.png" target="_blank" rel="noopener"><img decoding="async" width="932" height="923" data-id="114664" src="/images/posts/2024/11/why-java-8-is-a-ticking-time-bomb-hiding-within-your-organization/cve-high-score.png" alt="Some of the most recent CVEs with the highest score" class="wp-image-114664"></a>
 </figure>
 <figure class="wp-block-image size-large">
  <a href="/images/posts/2024/11/why-java-8-is-a-ticking-time-bomb-hiding-within-your-organization/cves-october-2024.png" target="_blank" rel="noopener"><img decoding="async" width="934" height="980" data-id="114662" src="/images/posts/2024/11/why-java-8-is-a-ticking-time-bomb-hiding-within-your-organization/cves-october-2024.png" alt="All the CVEs in the OpenJDK update of October 2024" class="wp-image-114662"></a>
 </figure>
</figure>

### Performance Improvements {#h3-2-performance-improvements}

Every new version of OpenJDK brings many improvements to the runtime itself, for instance, optimizations in the garbage collector and improved memory management. You may be surprised how much better your application performs if you [deploy the existing JAR on a newer runtime version](https://www.azul.com/blog/benchmarks-show-faster-java-performance-improvement/).

So, imagine what performance boost you can achieve by bumping both the runtime and your code!

### Developer Productivity {#h3-3-developer-productivity}

With OpenJDK, the runtime improved, and a long list of language features was added, optimized, and extended. This means that for a developer, the current Java is not the same as the one used many years ago. In many cases, the same functionality can be achieved with less code and fewer dependencies, resulting in code that is easier to read and maintain.

These improvements range from the introduction of `var` in Java 10, over switch expressions, text blocks, virtual threads in Java 21, and many more... You can find a lot of blog posts describing all the improvements in the latest releases. These are only a few of them:

* [Java 23 Has Arrived, And It Brings a Truckload of Changes](https://foojay.io/today/java-23-has-arrived-and-it-brings-a-truckload-of-changes/) by Hanno Embregts
* [Java 23: What's New?](https://foojay.io/today/java-23-whats-new/) by Loic Mathieu
* [Java 22 Is Here, And It's Ready To Rock](https://foojay.io/today/java-22-is-here-and-its-ready-to-rock/) by Hanno Embregts
* [Java 22: What's New?](https://foojay.io/today/java-22-whats-new/) by Loic Mathieu
* [Java 21 is Available Today, And It's Quite the Update](https://foojay.io/today/java-21-is-available-today-and-its-quite-the-update/) by Hanno Embregts
* [JDK 19 and What Java Users Should Know About It](https://www.azul.com/blog/jdk-19-and-what-java-users-should-know-about-it/) by Simon Ritter
* [Modern Java: An In-Depth Guide from Java 8 to Java 21](https://medium.com/java-and-beyond/modern-java-an-in-depth-guide-from-version-8-to-21-by-akiner-alkan-f89b50e13c72) by Akiner Alkan

Your team wouldn't be happy if you would force them to work with a computer, phone, and internet connection from five years ago. Then why would they love their job if the code they need to work in is even older? And it's not only Java that got better, but also the IDEs and all the tooling to test, build, and run your applications. All of this will result in a happier developer team.

### Reduced Business Cost {#h3-4-reduced-business-cost}

What do you think if you can increase the performance of your application and your developers can be more productive? Yes, of course, reduced costs!

You need fewer servers or can run more load on the existing ones. New features get developed faster and are easier to test and maintain.

Following the three monthly updates significantly reduces the risk of a potential security drama. You don't want to be the person who needs to tell your company owners how a simple runtime update could have prevented a security breach...

### Ready for Cloud Native {#h3-5-ready-for-cloud-native}

Java wasn't initially created to run in containers. Of course not; they didn't exist in 1995! Docker, microservices, observability,... are standard nowadays but were invented after Java was born.

Thanks to the six-monthly release of new OpenJDK versions, the architects and the community can continue adapting and evolving it. Maybe not as fast as some want, but OpenJDK has always focused on stability. Still, each new version brings new features that enable Java to be used in all modern use cases.

Reasons to Do Nothing {#h2-6-reasons-to-do-nothing}
---------------------------------------------------

Of course, doing nothing is an option. Just let things run as they are. But remember, every patch you don't apply is a technical debt you don't have to acknowledge. Every upgrade you postpone is a problem waiting to happen in the future. Yes, you can wait until you are retired or have moved to a new job; someone else will need to handle it.

Maybe you're trapped in the pitfall of "If It Ain't Broke, Don't Fix It." But sorry to disappoint you, it's broken already! You are running on a 10-year-old system, missing the improvements mentioned above.

Or did the original developer leave and there is no documentation? Then how can you be prepared for any error happening in your system? It's the kind of uncertainty that would keep anyone up at night, hoping nothing goes wrong.

No, there isn't a single reason that convinces me that "doing nothing" is an option...

Read More {#h2-7-read-more}
---------------------------

Yes, this is not a new message. But it turns out that it needs to be repeated over and over again as too many companies keep being stuck on outdated systems! Some other posts may help you to convince your managers that a change is urgent and required:

* [Systems relying on Oracle Java 8 with JavaFX will no longer receive upgrades starting in early 2025](https://www.azul.com/blog/azul-is-here-to-help-you-with-javafx-8-changes-in-2025/)
* [Now is the right time to choose another OpenJDK distribution](https://www.azul.com/blog/the-time-to-choose-an-openjdk-distribution-is-now/)
* [Reach out for commercial Java support before something happens](https://www.azul.com/blog/find-commercial-java-support-before-something-happens/)
* [The Importance of Java Security Updates](https://www.azul.com/blog/the-importance-of-java-security-updates/)
* [The Definitive Guide to Updating Azul Zulu Builds of OpenJDK](https://www.azul.com/blog/the-definitive-guide-to-updating-azul-zulu-builds-of-openjdk/)

Conclusion {#h2-8-conclusion}
-----------------------------

Companies like Azul can help you to maintain your Java 6, 7, and 8 environments for many more years. But there are a lot of reasons to move on. Support for these versions will end one day, and you want to be prepared for it. Make your systems future-proof now. Make your Developer and DevOps team happy and give them the tools they deserve by ensuring your runtime is secure and you reach maximum performance with modern Java versions.
> You can find reactions on this blog post in this follow-up post:   
> [**How Organizations Became Stuck on Outdated Java Versions**](https://foojay.io/today/how-organizations-became-stuck-on-outdated-java-versions/)
