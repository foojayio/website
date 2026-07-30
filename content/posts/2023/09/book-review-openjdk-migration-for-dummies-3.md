---
title: "Book Review: \"OpenJDK Migration for Dummies\""
slug: "book-review-openjdk-migration-for-dummies-3"
date: "2023-09-18T04:48:45+00:00"
lastmod: "2024-06-30T11:18:56+00:00"
description: "A comprehensive and informative guide for those looking to navigate the complex world of JDK distributions and licenses,"
authors:
  - "deepu-sasidharan"
image: "https://foojay.io/wp-content/uploads/2023/08/image-764x1024-dummies.png"
categories:
  - "Book Review"
  - "Books"
  - "OpenJDK Migration"
tags:
related_posts:
  - "book-announcement-openjdk-migration-guide-for-dummies"
  - "book-review-openjdk-migration-for-dummies-2"
  - "is-openjdk-just-a-drop-in-replacement"
  - "where-production-policy-belongs-building-eliya-in-public"
frozen: false
---

I recently read the free book [OpenJDK Migration for Dummies](https://www.azul.com/openjdk-migration-for-dummies/) by [Simon Ritter](https://www.linkedin.com/in/siritter/). Simon is the Deputy CTO at Azul Systems.

Azul has its own OpenJDK distribution which also has commercial flavors. So, obviously, my first thought was, "Well, of course, he's going to say nice things about Azul's JDK distribution and try to sell it everywhere". But, to my surprise, that wasn't entirely true. To be fair, he does sell Azul's JDK a bit, and the goal of the book seems to include some marketing and advocacy for Azul. However, overall the book is what it promises in the title, a guide for migrating to an OpenJDK distribution, regardless of the vendor.

First, let me get the rants out of the way. I'm not a fan of the "for dummies" brand, as I find it rather obnoxious to label anyone as a dummy, let alone to declare that a technical topic discussing OpenJDK is for dummies. But, I won't let those things detract from the content of the book.

The book is divided into 6 chapters and includes some intriguing appendices.

Chapter 1: Replacing Oracle Java SE in the Enterprise {#h2-0-chapter-1-replacing-oracle-java-se-in-the-enterprise}
------------------------------------------------------------------------------------------------------------------

The book opens with an insightful look at the history of OpenJDK, the evolution of Java over the years and its significance and dominance in the enterprise world, the ease of replacing Oracle Java SE in the enterprise with OpenJDK, the importance of Java TCK (Technology Compatibility Kit), Oracle's licensing, and some Oracle bashing (rightly so 😉).

Overall, this chapter provides insights into the challenges and considerations associated with migrating from Oracle Java SE to OpenJDK in the enterprise, including licensing and pricing.

Chapter 2: Preparing for Your Migration {#h2-1-chapter-2-preparing-for-your-migration}
--------------------------------------------------------------------------------------

This chapter offers helpful advice on preparing your applications for migration to OpenJDK. It covers several key steps and considerations:

**Identifying Migration Goals**: The most important step, in my opinion, because sometimes the goal may not justify the effort and cost of migration. The book provides examples of goals that may warrant the effort and cost of migration.

**Three-Phase Migration Process**: The book presents an excellent three-phase migration process that includes:

* **Discovery**: Identifying which Java versions are used by which applications on which machines, including cloud instances.
* **Execution**: Installing the chosen OpenJDK versions on machines requiring a Java runtime.
* **Validation**: Testing applications to ensure proper functionality.

**Recognizing Risks of Older Technologies**: This section offers some helpful advice on the risks of using older versions of Java and relying on Oracle JDK-specific features like JavaFX, Java Web Start, or Java Applets.

The book also provides advice on handling third-party applications that might require specific JDKs and addresses edge cases like font rendering, NTLM authentication, and so on.

Chapter 3: Migrating Your Applications {#h2-2-chapter-3-migrating-your-applications}
------------------------------------------------------------------------------------

This is the most Azul-specific chapter in the book and is essentially a tutorial on how to install the Zulu JDK (Azul Zulu Builds of OpenJDK).

However, it does offer some generic enough advice on installing an OpenJDK distribution and testing your application afterward.

Chapter 4: Evaluating OpenJDK Distribution Providers {#h2-3-chapter-4-evaluating-openjdk-distribution-providers}
----------------------------------------------------------------------------------------------------------------

This chapter discusses various OpenJDK distribution providers and the differences between them. (Not all of them are covered. For example, Liberica is not mentioned.) It also provides valuable advice on how to evaluate OpenJDK distribution providers and choose the right one for your organization.

The chapter includes a FAQ about switching to OpenJDK distributions. The chapter is somewhat biased toward Azul, but that is to be expected since the book is provided by Azul free of charge.

One criticism I have is that the chapter doesn't compare the commercial offerings of the various OpenJDK distribution providers, which I think would have been helpful, considering the book's target audience is enterprise users who may opt for commercial offerings.

Chapter 5: OpenJDK Support and Maintenance {#h2-4-chapter-5-openjdk-support-and-maintenance}
--------------------------------------------------------------------------------------------

This chapter delves into the benefits of commercial support for OpenJDK and highlights various aspects of such support offered by OpenJDK providers.

The chapter touches on different aspects of commercial support, including:

* Bug Reporting and Timely Fixes
* Legacy and Platform Support
* Backported Security Fixes
* Stabilized Security Builds
* Updating Bundled Technologies

Chapter 6: Choosing the Right Java Partner {#h2-5-chapter-6-choosing-the-right-java-partner}
--------------------------------------------------------------------------------------------

This chapter focuses on how to select the right vendor for Java SE migration to OpenJDK, emphasizing the need for a trustworthy and proficient provider.

I would have expected this chapter to heavily promote Azul, but surprisingly, it only mentions Azul twice. So, kudos to Simon for being objective and not attempting to excessively promote Azul here.

Conclusion {#h2-6-conclusion}
-----------------------------

"OpenJDK Migration For Dummies, Azul Special Edition" offers a comprehensive and informative guide for those looking to navigate the complex world of JDK distributions and licenses (I'm looking angrily at you, Oracle 😠).

I believe the book would be more useful for enterprise developers since the actual aspect of migrating from one JDK to another is quite straightforward. However, in an enterprise setting, there are many more aspects to consider, which the book touches upon and provides advice on.
