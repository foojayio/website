---
title: "Save Time and Money by Reducing False Positives"
slug: "save-time-and-money-by-reducing-false-positives"
date: "2025-06-23T08:35:27+00:00"
lastmod: "2025-08-14T12:38:44+00:00"
description: "Recently Azul announced that AVD makes it possible to identify vulnerable components on a jar file level but also on class file level."
authors:
  - "gerrit-grunwald"
image: "https://foojay.io/wp-content/uploads/2025/06/avdazul.png"
categories:
  - "Java"
  - "Security"
  - "Tools"
tags:
related_posts:
frozen: false
---

**[Recently Azul announced that AVD](https://www.azul.com/blog/how-azul-identifies-java-security-vulnerabilities-with-1000-times-greater-accuracy/) (Azul Vulnerability Detection), which is our solution to scan for security vulnerabilities in production, now comes with a new feature that only makes it possible to identify vulnerable components on a JAR file level but also on class file level.**

### The Production Scanning Challenge {#h3-0-the-production-scanning-challenge}

So what does that mean? Usually, security scanners work in environments from development up to CI/CD, but not in production. The main reason for that is the fact that once you start scanning for vulnerabilities in production, the scanning process itself needs too many resources, which brings down the performance of the system. The performance decrease can be in the range of 10 to 30%, which explains why companies trust security scanners that scan in CI/CD and don't add additional scans in production.

Because at Azul we know how the JVM works, we created a solution that is mainly made for scanning in production with more or less no performance impact.

For that, we use a so-called Java Agent, which is also often used by other scanners, but we tuned it to use as few resources as possible, to keep performance up in production.

Given the dynamic nature of Java, it is a good idea to scan your application, also in production.

### How AVD Architecture Works {#h3-1-how-avd-architecture-works}

The diagram below shows a possible scenario of using AVD:

![](/images/posts/2025/06/save-time-and-money-by-reducing-false-positives/AVD_Diagram-2-700x268.png)

You run your application on one or multiple JVM's (Java Virtual Machine), which can be any build of OpenJDK (e.g., Amazon Corretto, Adoptium Temurin, Azul Zulu, etc.). Now a Java Agent will be attached to each JVM you would like to monitor in production.

Each agent will send data that is coming from the JVM it is attached to, to the so called Forwarder, which is another Java program that will usually be installed somewhere in the infrastructure of your environment, in a way that it is reachable by the Java Agents.

The Forwarder has only one job, it forwards the information that was collected from the Java Agents to Azul Intelligence Cloud, where the data will be stored and analyzed.

### Deployment Tagging and Reporting {#h3-2-deployment-tagging-and-reporting}

Because these days many companies make use of continuous deployment, it makes sense to tag each new version of your application with a so-called "AppEnv" tag that can be defined by you.

Later on when creating reports, you can filter by these tags, which makes it possible to create reports for specific deployments or whatever you have in mind.

Once the setup is in place, you can create reports at any time either using the Intelligence Cloud web interface or the API that is provided.

The API makes it possible to integrate AVD into your own products, if you like, and the web interface can always be helpful to quickly take a look at the latest data.

### Reducing False Positives Through Production Scanning {#h3-3-reducing-false-positives-through-production-scanning}

So, normally security scanners give you information about the vulnerable JAR files that have been detected which leads to huge lists of possible vulnerabilities that have to be checked by the security team. A lot of those vulnerabilities found are so called false/positives which means yes there might be a vulnerability but it wasn't used by the system. This analysis is very time consuming and holds your security team away from focusing on the really important vulnerabilities that might have an impact on your system.

By scanning in production, we can tell you not only if there are possible vulnerabilities, but also if you are using the vulnerable JAR files at all. This will drastically reduce the number of false positives because you can create lists that only contain the vulnerable libraries that have been called in production.

### Class-Level Precision for Maximum Efficiency {#h3-4-class-level-precision-for-maximum-efficiency}

But we figured out that we can do even better... and also provide information about the classes that have been used in those vulnerable JAR files.

These days you will find libraries with hundreds of classes and just because the library contains a vulnerability, it doesn't mean that your application called the classes in that library that contains the vulnerability.

So by giving you more detailed information at the class level, you are now able to shrink the list of vulnerabilities to check even further and focus only on the really severe vulnerabities which can be a time and money saver.

For more details, see [How Azul Identifies Java Security Vulnerabilities with 1,000 Times Greater Accuracy](https://www.azul.com/blog/how-azul-identifies-java-security-vulnerabilities-with-1000-times-greater-accuracy/).
