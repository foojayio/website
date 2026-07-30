---
title: "How Oracle Separates Java Pricing from Value"
slug: "how-oracle-separates-java-pricing-from-value"
date: "2024-03-15T14:00:25+00:00"
lastmod: "2024-10-03T16:43:20+00:00"
description: "Oracle deserves props for continuing to develop Java as an open-source platform, but its pricing model and licensing continues to drive users away. "
canonical: "https://www.azul.com/blog/how-oracle-separates-java-pricing-from-value/"
authors:
  - "simonritter"
image: "https://foojay.io/wp-content/uploads/2023/08/image-764x1024-dummies.png"
categories:
  - "Java Core"
tags:
related_posts:
  - "video-if-i-decide-to-stay-with-oracle-java-what-issues-will-i-face"
  - "cloud-cost-optimization-is-hard-java-can-help"
  - "mastering-the-challenges-of-openjdk-migration"
frozen: false
---

**On January 23, 2023, Oracle quietly replaced the online link to the Oracle Java SE Subscription Global Price List with a link to a new Oracle Java SE Universal Subscription Global Price List.**

The old Oracle Java pricing was far from perfect. It was based on a processor-count metric for servers and a named-user-plus metric for desktops. With the processor-count metric, the license cost was determined by multiplying a processor's total number of cores by a core processor licensing factor specified by Oracle. The named-user metric applied to all individuals *authorized* to use a program regardless of whether they were *actively* using it. These definitions left the door open for uncomfortable conversations with Oracle reps who were famous for setting up meetings "to talk about your Java usage." Unpleasant in themselves, these meetings could lead to painful Java audits.

The new Oracle Java pricing seemed straightforward. Instead of counting processors and authorized users, organizations needing an Oracle Java SE subscription would count their employees. But the fine print in the Oracle Java SE Universal Subscription Global Price List contained a complex definition of "employees," causing many existing customers to do a double take.
> *Employee for Java SE Universal Subscription: is defined as (i) all of Your full-time, part-time, temporary employees, and (ii) all of the full-time employees, part-time employees and temporary employees of Your agents, contractors, outsourcers, and consultants that support Your internal business operations. The quantity of the licenses required is determined by the number of Employees and not just the actual number of employees that use the Programs.*

To be fair, employee-count metrics are not uncommon in the technology world. Many companies charge per employee per month (PEPM). Oracle charges on a PEPM basis for products like Oracle Cloud HCM. But typically, PEPM pricing is tied to usage.

The new pricing for Oracle Java SE is disconnected from actual Java usage and could result in a massive price increase.

Do the math {#h-do-the-math-nbsp}
---------------------------------

Say a company has two vCores and 25,000 desktops for 10,000 employees. Azul will save that company approximately $635,000 per year in licensing.

The hypothetical company acquired a competitor and has grown to 25,000 employees. None of the new employees use Java so there are no more vCores and no more desktops. Azul's bill remains the same, while Oracle's would increase by just over $1 million.

Imagine that this savvy company acquires another corporation and grows to 50,000 employees, none of whom use Java. Azul's bill stays flat, while Oracle's increases by more than $1.1 million.

| vCores | Desktops | Employees | Oracle Java SE Price |
|--------|----------|-----------|----------------------|
| 2      | 10,000   | 10,000    | $105,000             |
| 2      | 10,000   | 25,000    | $1.140,000           |
| 2      | 10,000   | 50,000    | $1,265,000           |
| 2      | 10,000   | 100,000   | $4,415,000           |

Do the math yourself on our [Java price comparison calculator](https://www.azul.com/products/core-savings-calculator/).

How did Oracle Java pricing get here? {#h-how-did-oracle-java-pricing-get-here-nbsp}
------------------------------------------------------------------------------------

### September 2017 {#h-september-2017-nbsp}

Oracle announced significant changes to the Java release process. The new model provided a new release every six months (in March and in September). Developers had been requesting a more agile approach to developing the core Java platform, and this change led to more features being added to Java --- and more rapidly --- than ever before.

Oracle also changed how it delivers updates and support. Instead of providing extended maintenance and support for all Java versions, only Long-Term Support (LTS) releases would qualify for that.

### June 2018 {#h-june-2018-nbsp}

Oracle announced its new licensing and pricing for JDK, which it bundled with the Java SE subscription.

A Java SE subscription included:

* Certified compatible updates for performance, stability, and security updates
* Security-only updates (curated updates)
* Technical support

Paid support is a familiar component of open-source communities, and the change was accepted --- but a move Oracle made three months later wasn't.

### September 2018 {#h-september-2018-nbsp}

Oracle announced that in January 2019, it would end free public updates for commercial use for Java 8, which was then the most popular version of Java. Updates would still be available with a subscription to Java SE; but if you installed the updates without a subscription, you'd be liable to Oracle for the cost of the subscription.

### April 2019 {#h-april-2019-nbsp}

Oracle unveiled a new Java license: the Oracle Technology License Agreement for Oracle Java SE.

The cost for using Oracle Java under these new terms increased. Organizations like Cornell University began to take steps to minimize or eliminate their dependency on Oracle Java. Cornell published a blog post asking users of its network to uninstall Oracle Java from their computers and to install OpenJDK if they needed Java.

### September 14, 2021 {#h-september-14-2021-nbsp}

Oracle announced a new No-Fee Terms and Conditions (NFTC) license that partially rolled back the changes announced in 2019. A new LTS release could now be free under NFTC until one year after the next LTS release, although Oracle's language is unclear.

The license says, "...internally use the unmodified Programs for the purposes of developing, testing, prototyping and demonstrating your applications, and running the Program for Your own personal use or internal business operations." Nobody has qualified what "internal business operations" really means. Does a web server, accessible externally by customers, count?

This gave users a chance to transition their applications to the next release. The NFTC license was available for developing, testing, prototyping, and demonstrating applications and personal use of the Oracle JDK or for use related to internal business operations.

Oracle also shortened the time between LTS releases from three years to two years. The move was generally well received, but it had little effect on OpenJDK's growing momentum.

Conclusion {#h-conclusion-nbsp}
-------------------------------

Oracle's changes to its licensing terms and Java pricing models for Java have been a mixed bag. Oracle deserves props for continuing to develop Java as an open-source platform, but its pricing model and licensing continues to drive users away.

If the hypothetical company had used Azul, it could have saved $2.8 million after its second acquisition -- without audits. If you have a high bill for Oracle Java SE at the end of Oracle's fiscal year (April 30), start planning to divest yourself completely of Oracle Java by end April 2025 so it doesn't happen again.

Oracle may want to convince people that moving from its Java SE runtime to an OpenJDK distribution is difficult, time-consuming, dangerous, and expensive. For most instances, a migration is straightforward. [Learn more in the book we wrote on migrating to OpenJDK.](https://www.azul.com/openjdk-migration-for-dummies/)

<br />
