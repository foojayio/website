---
title: "Foojay Podcast #58: How Java Developers Can Secure Their Code"
slug: "foojay-podcast-58"
date: "2024-09-30T07:43:46+00:00"
lastmod: "2025-11-13T08:42:28+00:00"
description: "Three years after Log4Shell caused a significant security issue, we still struggle with insecure dependencies and injection problems..."
authors:
  - "bmvermeer"
  - "erikcostlow"
  - "frankdelporte"
  - "jonathan-vila"
image: "https://foojay.io/wp-content/uploads/2024/09/episode-58-security.jpg"
categories:
  - "Debugging"
  - "Developer Tools"
  - "DevOps"
  - "Java"
  - "Java Core"
  - "Observability"
  - "Podcast"
  - "Security"
tags:
related_posts:
  - "the-persistent-threat-why-major-vulnerabilities-like-log4shell-and-spring4shell-remain-significant"
  - "top-security-flaws-hiding-in-your-code-right-now-and-how-to-fix-them"
  - "trash-pandas-love-enterprise-java-garbage-code"
  - "foojay-podcast-95"
frozen: false
---

Three years after Log4Shell caused a significant security issue, we still struggle with insecure dependencies and injection problems.

In this podcast, we'll discuss how developers can secure their code.

I talked with three authors who posted a security and code quality post on Foojay.io.

Video {#h2-0-video}
-------------------

{{< youtube sRVcqILDuSo >}}

Podcast Apps {#h2-1-podcast-apps}
---------------------------------

You can listen and subscribe to the Foojay Podcast on:

* [Spotify](https://open.spotify.com/show/6CpTfgn9LirzJGAtc4ICdQ)
* [Apple Podcasts](https://podcasts.apple.com/be/podcast/foojay-io-the-friends-of-openjdk/id1652281304)
* And most others...

Guests {#h2-2-guests}
---------------------

### Jonathan Vila {#h3-3-jonathan-vila}

* [https://www.linkedin.com/in/jonathanvila/](https://www.linkedin.com/in/jonathanvila/%20)
* <https://about.me/jonathan.vila>
* <https://twitter.com/jonathan_vila>

### Brian Vermeer {#h3-4-brian-vermeer}

* <https://www.linkedin.com/in/brianvermeer/>
* <https://brianvermeer.nl/>
* <https://twitter.com/BrianVerm>

### Erik Costlow {#h3-5-erik-costlow}

* <https://www.linkedin.com/in/costlow/>
* <https://twitter.com/costlow>

Content {#h2-6-content}
-----------------------

00:00 Introduction of topic and guests

01:35 Brian: Why is Log4Shell still around?  
<https://foojay.io/today/the-persistent-threat-why-major-vulnerabilities-like-log4shell-and-spring4shell-remain-significant/>   

03:24 Outdated dependencies are still used a lot  

04:31 Who is responsible for dependency updates?  

07:55 Snyk tools to help discover issues  

10:15 Comparing to Dependabot  

11:21 How to keep dependencies up-to-date  

14:32 Responsibility to use dependencies with care  

17:17 Looking forward to the JFall conference  

18:48 About Foojay

19:49 Jonathan: Is SQL injection still a problem?  
<https://foojay.io/today/top-security-flaws-hiding-in-your-code-right-now-and-how-to-fix-them/>  

24:50 Deserialization injection  

27:30 Logging injection  

31:22 Even experienced developers make mistakes  

33:17 About Sonar tools  

35:53 Other articles by Jonathan  
<https://foojay.io/today/author/jonathan-vila/>  
<https://foojay.io/today/ensuring-the-right-usage-of-java-21-new-features/>  

38:20 Other security tools  
<https://www.youtube.com/watch?v=-wVCYj8oQUY>

39:47 Erik: Trash Pandas are attracted by unused code  
<https://foojay.io/today/trash-pandas-love-enterprise-java-garbage-code/>  

43:01 How bad are insecure but unused libraries?  

45:16 Problem of code only used by unit tests  

47:15 Testing in different layers (develop, test, production)  

49:31 How much code is not used in production?  

50:31 How code becomes unused  
<https://foojay.io/today/foojay-podcast-57/>

54:29 Conclusions
