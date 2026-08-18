---
title: "Review: OpenJDK Migration for Dummies"
date: "2023-09-15T18:46:59+00:00"
lastmod: "2024-06-30T11:19:47+00:00"
description: "Have you been thinking of moving away from the Oracle JDK to OpenJDK? The book \"OpenJDK Migration for Dummies\", written by Simon Ritter of Azul, provides - by Josh Juneau"
authors:
  - "josh-juneau"
image: "Favicon-3-2.png"
categories:
  - "Book Review"
  - "Books"
  - "OpenJDK Migration"
tags:
related_posts:
  - "book-announcement-openjdk-migration-guide-for-dummies"
  - "is-openjdk-just-a-drop-in-replacement"
  - "mastering-the-challenges-of-openjdk-migration"
frozen: false
---

**Have you been thinking of moving away from the Oracle JDK to OpenJDK? The book "OpenJDK Migration for Dummies", written by Simon Ritter of Azul, provides direction for those who are looking to make the move.**

In many cases, organizations develop Java applications to work on a specific version of the JDK. Oftentimes, as long as the application continues running without issue, organizations wouild not upgrade the JDK. As we well know, as with all software, as time goes on, the JDK which is in use can age and become out-of-date, exposing security vulnerabilities. Moreover, if the JDK in use is aging, then there likely have been many enhancements in more recently released JDKs which could provide performance benefits for the deployed application(s).
> We are at a time where older releases of the JDK such as JDK 8 are no longer being supported by Oracle unless commercial support is being paid. If an organization needs to maintain the use of JDK 8, then it may make sense for the organization to purchase a support agreement.

In other cases, the organization may be able to move to another JDK that will work with their application. There are many different options available today. One of those options is OpenJDK, which is freely available for use. There are a number of organizations that have taken OpenJDK and added features or enhancements, and typically offer a paid support for enhanced protection or even for access to premium features.
> This book nicely covers the arduous process of learning about the different licensing options when using the Oracle JDK, and what the OpenJDK has to offer. The book makes it easy to understand so that readers to not need to delve into long white papers to read about them.

The book also covers the different points that need to be reviewed while preparing to make a migration to OpenJDK, including making an inventory of all JDK versions are in use within your organization. There are several key processes outlined in the book that must not be overlooked while performing a migration, such as looking at each of the "optional" features of the JDK that are in-use within your organization to ensure that they are available in the OpenJDK release of your choice. For instance, JavaFX may not be incorporated within all OpenJDK releases and may be an additional package to incorporate into your migration.

Since the book is written by Simon who works with Azul, he does cover some of the benefits of the Azul Platform Core JDK. There are many to cover, and the book offers a high level overview of some of the most important features. It also covers the question of why commercial support may be something of value to your organization. What if you still need to run JDK 8 in order to make your application work? In such a case, it makes sense to pay for commercial support. How about if your organization is looking to have first hand support? That is another solid reason to purchase commercial support. The list goes on.

**The book "OpenJDK Migration for Dummies" is a great read for anyone looking to migrate to the OpenJDK. It is a nice and quick read, landing in at under 80 pages, and it covers the process from start to finish.**
