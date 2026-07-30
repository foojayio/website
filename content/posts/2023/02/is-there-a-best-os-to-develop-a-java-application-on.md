---
title: "Is There a Best OS to Develop a Java Application on?"
slug: "is-there-a-best-os-to-develop-a-java-application-on"
date: "2023-02-22T11:43:40+00:00"
lastmod: "2023-02-22T11:43:42+00:00"
description: "One subject that often evokes a lot of debate is which is the best OS to develop a Java application on. This article gives my view!"
authors:
  - "rob-austin"
image: "/images/posts/2023/02/is-there-a-best-os-to-develop-a-java-application-on/shutterstock_1111260050-350x233-1.jpg"
categories:
  - "Developer Tools"
  - "Eclipse"
  - "IntelliJ IDEA"
  - "Java Core"
tags:
related_posts:
  - "challenges-when-developing-a-gui-for-fix"
  - "chronicle-queue-storing-1tb-in-virtual-memory-on-a-128gb-machine"
  - "creating-terabyte-sized-queues-with-low-latency"
  - "project-panama-for-newbies-part-4"
frozen: false
---

***One subject that often evokes a lot of debate is which is the best OS to develop a Java application on. This article gives my view on the issue.***

Java runs very well on Windows, macOS and Linux Distributions.

I work for [Chronicle Software](https://ga-dev-tools.web.app/campaign-url-builder/ "Chronicle Software"), and much of our software is open-source Java libraries, so we don't, or rather we can't, require our customers to run on a particular operating system. You may be surprised to hear that we don't mandate which operating system our staff should use either. We let them use the one that causes the least friction, the one that they are most productive with.

When it comes to your setup to develop your Java application, the most important choice is which Integrated Development Environment (IDE) you select; not the choice of the OS. Choosing a good IDE can boost your productivity, but we don't force everyone in the company to use the same IDE. That said, every Java developer we have hired in the last eight years has selected IntelliJ as their preferred IDE; JetBrains must be doing something right 🙂

Before I used IntelliJ, I used to use Eclipse. I was forced to use IntelliJ when joining as a developer for UBS Investment Bank about 15 years ago. At first, I was not too fond of IntelliJ; nothing was where I expected it; it felt like I had my hand chopped off as everything seemed to take ten times longer. Over time, I grew to love it and appreciated its exceptional code refactoring.

When considering which OS to develop on, it is worth considering the OS of the target architecture. At [Chronicle Software](https://bit.ly/40SSXNl "Chronicle Software"), we develop low latency event stream messaging software; this software is used by many of the world's investment banks and crypto exchanges. Usually, the investment banks develop on Windows and deploy to Linux; on the other hand, the crypto firms often embrace macOS for development, but almost every organization uses Linux for production.

**Whilst the OS you develop on, and your production OS doesn't have to be the same, it is good to consider your production OS when choosing your development OS.**

FreeBSD underpins macOS; this is not Linux, [but it is Unix](https://www.howtogeek.com/441599/is-macos-unix-and-what-does-that-mean/ "but it is Unix"), so they have a reasonable degree of similarity, especially in terms of commands, shell etc. However, there is a fair degree of divergence under the covers; macOS does not offer the same fine-grain control. This can negatively impact you if you intend to do low-latency development. For example, you want to control which cores your threads are pinned to; for most java developers, this lack of fine-grain control is usually not a concern, but if it does concern you, you should select Linux. The point here is that selecting either Linux or macOS as your development environment can make a lot of sense if you deploy a server-side application to Linux.

In the past, I have used Ubuntu, but the GUI does not come anywhere close to the level of integration and slickness offered by macOS. macOS has a reputation for being just cooler, prettier, and nicer, but it does come with real benefits which can improve your productivity.

At [Chronicle Software](https://bit.ly/40SSXNl "Chronicle Software"), around 50% of developers use macOS; and 50% use Linux and/or Windows. Recently many more applications are also available for Linux and web-based apps such as google docs work well in this environment, but it is worth checking if all the applications you intend to use will work on Linux. Alternatively, you could use virtualization, or If you never have to run your Windows and Linux application at the same time, you could dual boot.

The Mac is not always perfect; a colleague of mine has a 2019 Intel 16″ mac book; he often has to run it on an ice pack otherwise, it will thermally throttle. Many non-mac ultrabooks also suffer from this problem. Putting a hot intel chip in those thin unibody shells was a mistake, but Apple has now fixed this with their new silicon macs. Recently, Intel has improved their thermal issues in their new hybrid core model introduced in their 12th generation architecture. AMD is more thermally efficient than Intel, but neither Intel or AMD can currently compete with ARM or Apple, when it comes to performance per watt.

Regardless of development environment, at the end of the day the software needs to run on a given target OS. Libraries such as Chronicle help abstract the platform differences to enable a largely seamless development experience between different development and target operating systems for low-level features. (Some examples where macOS and Linux differ include file locking and thread affinity, and Chronicle helps normalise these features between the two platforms.) Ultimately, in order to extract the best performance the nuances of each target platform need to be carefully understood when deploying, configuring, and tuning a low-latency software stack, and some exposure and familiarity to the target OS during the development phase can be beneficial.

### Conclusion {#h3-0-conclusion}

First and foremost, for development I suggest using the OS you are most comfortable with. If you don't have much experience with any operating system, I recommend Windows or macOS.

* If you are on a budget, Linux is a cheaper option, but be careful to check if you can run all the code you need just on Linux; unless you want to put up with dual-booting or a Virtual Machine, you may have to blow the budget and get a second (or old) machine to run Windows. If you opt for dual-booting then I would recommend, for a desktop, setting it up with two boot disks (one for each OS), this is so you can replace one if something goes wrong.
* On the other hand, If you are writing low latency code where you have to have a good level of control of your OS subsystem, then select Linux.
* Alternatively, I would select macOS.

**The critical point is that choosing Mac, Windows, or Linux is a bit like politics or religion; people have strong views that their choice is correct and often shut their ears to a different viewpoint (which I feel is interesting).**
