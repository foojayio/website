---
title: "Java 21 and Jakarta EE 11: A New Era of Cloud Native Java"
slug: "java-21-and-the-upcoming-jakarta-ee-11-a-new-era-of-cloud-native-java"
date: "2023-10-06T08:28:38+00:00"
lastmod: "2023-10-06T08:28:40+00:00"
description: "While Java 21 is already bringing transformative features to the table, the upcoming release of Jakarta EE 11 is expected to further elevate the state of cloud native Java development."
canonical: "https://blog.payara.fish/java-21-and-the-upcoming-jakarta-ee-11-a-new-era-of-cloud-native-java"
authors:
  - "jadon-ortlepp"
  - "luqman-saeed"
image: "payara_square_logo.jpg"
categories:
  - "Jakarta EE"
  - "Java"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "5-minute-azure-survey-java-ee-jakarta-ee-and-microprofile"
  - "are-java-jakarta-ee-application-servers-heavy"
  - "new-features-in-jakarta-ee-11-with-examples"
frozen: false
---

**With Java 21 released and Jakarta EE 11 slated for release in Q1 of 2024, the landscape for Java development is evolving rapidly, particularly in the cloud-native space. Although these two are not being developed together, they are intrinsically linked: Jakarta EE 11 will have Java 21 as its base Java SE version. This blog post will explore what these two significant releases bring to the table individually and how they can collectively enhance cloud native Java development.**

Java 21: The Game-Changer
-------------------------

Java 21, the latest long-term support (LTS) version of Java, is already out and has been a hot topic in the Java community. It introduces several exciting features, including:

**Virtual Threads:** These are designed to be lighter and more efficient than traditional threads, thus enabling applications to handle a greater number of tasks concurrently. Check out our blog, [A Look at Virtual Threads in a Jakarta EE Managed Context.](https://blog.payara.fish/a-look-at-virtual-threads-in-a-jakarta-ee-managed-context)  

**String Templates:** This simplifies string manipulation, making the code more readable and maintainable.  

**Pattern Matching for Switch:** This feature allows for more concise and user-friendly switch statements.

Jakarta EE 11: The Awaited Release
----------------------------------

[Jakarta EE](https://jakarta.ee/) 11, expected in Q1 of 2024, is set to be the next major version of Jakarta EE. One of the most noteworthy aspects is that it is expected to have Java 21 as its base Java SE version. This means it will inherently benefit from the advancements Java 21 brings. The Jakarta EE Steering Committee is seeking to establish a predictable release cadence for the platform, with the current proposal being a new major Jakarta EE release 6 months after a Java SE LTS release.

Jakarta EE 11 On Java 21
------------------------

The fact that Jakarta EE 11 will use Java 21 as its base version means some of the notable features of Java 21 will be be leveraged to provide even better performance and code readability. Even though the core theme of Jakarta EE 11 will be better alignment of the entire platform with Jakarta CDI, some notable places Java SE 21 features will come to play are:  

**Efficient Scaling in Jakarta EE:** The virtual threads from Java 21 will be utilized in [Jakarta Concurrency 3.1](https://jakarta.ee/specifications/concurrency/3.1/) in Jakarta EE 11, offering more efficient scaling options for handling multiple requests.  

**Simplified Code in Jakarta EE:** The pattern matching and string templates in Java 21 can make Jakarta EE applications more straightforward and easier to maintain.

Why It Matters for Cloud Native Java Development
------------------------------------------------

Cloud native applications require scalability, reliability, and efficiency. The features in Java 21, combined with the enterprise capabilities expected in Jakarta EE 11, aim to meet these needs. For example, virtual threads can be a crucial feature for applications that require quick scaling to meet demand spikes.

Conclusion
----------

While Java 21 is already bringing transformative features to the table, the upcoming release of Jakarta EE 11 is expected to further elevate the state of cloud native Java development. Jakarta EE 11, through Java 21, will make it easier to build more robust, efficient, and scalable cloud native Java applications.  

Stay tuned for more Java 21 content and updates as we get closer to the release of Jakarta EE 11, and learn how you can make the most of these advancements in your cloud native Java projects!

And remember, we have launched our own fully-managed cloud native application runtime, Payara Cloud. It offers a flexible and powerful way to easily run your Jakarta EE apps on the cloud. Simply select your WAR, click deploy, and watch your apps run on the cloud -- automatically, like magic. Find out more [here](https://www.payara.fish/products/payara-cloud/) and sign up for a 15 day free trial to experience it!
