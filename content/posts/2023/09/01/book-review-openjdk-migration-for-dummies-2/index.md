---
title: "Book review: \"OpenJDK Migration for Dummies\""
date: "2023-09-01T07:42:43+00:00"
lastmod: "2024-06-30T11:18:26+00:00"
description: "Is \"OpenJDK Migration for Dummies\" worth reading? I'd say so!"
authors:
  - "simon-verhoeven"
image: "image-764x1024-dummies.png"
categories:
  - "Book Review"
  - "Books"
  - "Java Core"
  - "OpenJDK Migration"
tags:
related_posts:
  - "book-announcement-openjdk-migration-guide-for-dummies"
  - "book-review-openjdk-migration-for-dummies"
  - "is-openjdk-just-a-drop-in-replacement"
  - "book-review-openjdk-migration-for-dummies-3"
frozen: false
---

**In the past couple of years, there have been quite a lot of Oracle Java licensing changes which can have quite the impact on how much using the Oracle JDK will cost your organization, especially since under the latest terms it's quite easy to have a "rogue" install cost you quite a bit.**

This is where the new **free** book [OpenJDK Migration for Dummies](https://www.azul.com/openjdk-migration-for-dummies/) by [Simon Ritter](https://www.linkedin.com/in/siritter/) offers a very useful resource.  

{{< img src="dummies-373x510.png" class="aligncenter is-resized" width="262" height="358" style="width:262px;height:358px" >}}

After all, it's easy to say: "Just pick an OpenJDK, any will do!", but is it really that easy, are there any caveats? As it turns out, yes, there are, and this book will help you navigate quite a bit of them. (For example, you might be using the Lucida font.)

It also explains what OpenJDK actually entails, how a distribution is verified, and compares some of the more popular distributions, and more.

## Content

### 1. Replacing Oracle Java SE in the Enterprise

This introductory chapter does a magnificent job of explaining the changes to the licensing model, and the implications thereof for organisations and the things to keep in mind.

Then it dives into the Technology Compatibility Kit (TCK).

It covers what the kit entails, how it makes sure that an implementation actually meets the specification, and thus how it ensures Java's "write once, run anywhere" philosophy. This touches on an area a lot of people might not be too familiar with.

### 2. Preparing for your migration

This chapter offers advice and aid in properly preparing and covers different topics such as:

* what should be included in your JDK inventory, and how do you actually create it?
* the risks of older JDKs
* figuring out what is not part of the core OpenJDK such as JavaFX
* gotchas such as commercial fonts not being included (some vendors do supply a Commercial Compatibility Kit (CCK)), NTLM authentication, ...
* how to handle third-party applications

### 3. Choosing the Right OpenJDK Distribution

Here we dive into the common distribution methods for different platforms, how to install/upgrade, and how to test the changes.

I admit, I'd have liked to see [SDKMAN](https://sdkman.io/) here mentioned as an installation method.

### 4. Evaluating OpenJDK Distribution Providers

There are a lot of providers, but which one is right for you? The answer might be all, or only a few. How do you determine this?

Some important differentiators to pay attention to are mentioned here, as well as a lot of common questions (do I need to recompile, regression risks, ...)

It also includes the [distribution matrix](https://www.azul.com/products/core/jdk-comparison-matrix/) one can find on the Azul website, which includes the support \& services + commercial services. For example, if you need OpenFX your options are already cut down quite a bit.

### 5. Exploring the Benefits of Commercial Support

Commercial support can be quite useful, for example, if your organization needs out-of-cycle security fixes, Patent and Non-Contamination Indemnification, ...

A lot of the use cases where this support can be useful are covered since not all suppliers offer the same support.

### 6. Choosing the right partner

Further considerations for picking the right partner if one is still in doubt after comparing the offered services and what's included.

Are they engaged in the community, do they have good references with helping organizations migrate, and more.

It's always nice to get some extra advice to help further narrow things down.

### 7. Ten Questions for Your Next Request for Information

This section offers some core questions you really ought to ask to be certain the partner you're in talks with is actually the right fit, and all boxes are checked, updates, and legal challenges.

### 8. Appendix

The appendix itself covers a part of the history of Java, JVM tuning, and runtime security.

Not core to the book, but I found it an interesting read.

## Personal thoughts

I found it a very useful, and educative read that's certainly worth checking out. Whilst Simon Ritter works for Azul, I found the bias to be very limited.

At the end of the day, no matter which distribution you choose, **please** make a conscious choice about which one you use.

If you're interested in the book and want some extra information you might also like the [migration webinar](https://www.youtube.com/watch?v=rGiIm2xdp7w) with Simon Ritter and Gerrit Grunwald.
