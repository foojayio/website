---
title: "Reduce Java Application Startup and Warmup Times with CRaC"
date: "2023-05-16T11:21:12+00:00"
lastmod: "2023-06-04T08:05:13+00:00"
description: "Today Azul announces the general availability of Azul Zulu Builds of OpenJDK for Java 17 with CRaC support in x86, 64-bit configurations."
canonical: "https://www.azul.com/blog/reduce-java-application-startup-and-warmup-times-with-crac/"
authors:
  - "pavel"
image: "image-1-1024x306-1.png"
categories:
  - "CRaC"
  - "Performance"
  - "Release Notes"
related_posts:
  - "how-to-run-a-java-application-with-crac-in-a-docker-container"
  - "azul-provides-the-crac-in-aws-snapstart-builds"
  - "superfast-application-startup-java-on-crac"
frozen: false
---

When a Java application runs, the JVM goes through the process of loading, initializing, and optimizing the code used by the application, including libraries, frameworks, and other components to reach the optimal performance level.

Each time the application restarts, it has no record or memory of previous times it has run and the work it performed.

As a result, it has to restart, extending valuable startup and warmup times and using precious resources.

## **Azul Zulu Builds of OpenJDK for Java 17 with CRaC**

Speeding up startup and warmup has been one of Java's perennial challenges. OpenJDK's CRaC (Coordinated Restore at Checkpoint) project is one of the most promising solutions.

Frameworks like Micronaut, Quarkus and Spring have realized orders of magnitude improvement with CRaC.

**[Now developers can try CRaC for themselves.](https://www.azul.com/products/components/crac/) Today Azul announces the general availability of Azul Zulu Builds of OpenJDK for Java 17 with CRaC support in x86, 64-bit configurations.**

The builds are commercially supported as part of [Azul Platform Core](https://www.azul.com/products/core) and are also available for free download and use with no restrictions.

**[For discussions and questions on this topic, see the CRaC Discussion Forum.](https://forums.foojay.io/forums/forum/coordinated-restore-at-checkpoint-crac/)**

## From Micronaut to Spring Boot

> "***CRaC has generated immense interest among the Java developer community and provides a compelling, resource-efficient approach for improving startup and warmup times** . **Azul is well known for originating this project, so it came as no surprise that they would also deliver the world's first production-ready builds of OpenJDK with commercial CRaC support.***"
> — Sergio del Amo, Micronaut Product Development Lead.
![Time to first operation for Micronaut improved from 1 second without CRaC to 46 milliseconds with CRaC.](https://www.azul.com/wp-content/uploads/CleanShot-2023-05-15-at-08.03.52.gif) **"** ***Project CRaC's checkpoint restore approach is very promising for the immediate startup of Spring applications on the JVM. Our collaboration with Azul delivered some great initial results already**.*"
> — Juergen Hoeller, project lead and co-founder of the Spring Framework project.

![Time to first operation for Spring Boot improved from 4 seconds without CRaC to 38 milliseconds with CRaC.](https://www.azul.com/wp-content/uploads/CleanShot-2023-05-15-at-08.04.13.gif)

## What Does CRaC Do?

The OpenJDK CRaC Project defines public Java APIs that allow for coordinating application resources during checkpoint and restore operations. This is important because applications can be restored in different environments than where the checkpoint was created; they may have different time zones, database connection strings, etc.

**[For discussions and questions on this topic, see the CRaC Discussion Forum.](https://forums.foojay.io/forums/forum/coordinated-restore-at-checkpoint-crac/)**

CRaC allows a running application to pause, snapshot its state, and store it for later use – even on a different machine. It saves the full context of the application process as an image, including its state and memory.
[![](image-3-1024x268.png)](https://docs.azul.com/core/crac/crac-guideline)

When the application restarts later, it skips startup and warmup and jumps right to that stored profile. It rapidly reloads the entire application and its state so it can continue from the point where the checkpoint was created. This approach reduces Java application startup and warmup times by several orders of magnitude, meaning milliseconds instead of seconds or minutes.

CRaC allows Java applications to leverage checkpoint-and-restore mechanisms and coordinate with them as necessary. Checkpoint-and-restore operations save the state of the JVM and the Java application to an image. The Java application can then be resumed from the image at a future point in time. Think of when you finish using a laptop, and you close it. Later, when you open it, CRaC would be like having the laptop operating at the same to performance as when you closed the lid.

In tests performed in 2022, OpenJDK builds with CRaC support showed some very impressive results:

|  Platform   |   Before CRaC    |    With CRaC    |
|-------------|------------------|-----------------|
| Spring boot | 4 seconds        | 38 milliseconds |
| Micronaut   | 1 second         | 40 milliseconds |
| Quarkus     | 980 milliseconds | 53 milliseconds |

OpenJDK builds with and without CRaC

**Learn how to get CRaC in your build of OpenJDK**

To use CRaC with the Azul Zulu Build of OpenJDK for Java 17, [visit our Downloads page](https://www.azul.com/downloads/?package=jdk#zulu) and select your build. You can also learn more about Azul Platform Core from our website.

**[For discussions and questions on this topic, see the CRaC Discussion Forum.](https://forums.foojay.io/forums/forum/coordinated-restore-at-checkpoint-crac/)**
[![](image-4-1024x270.png)](https://www.azul.com/downloads/?package=jdk-crac#zulu)

**[For discussions and questions on this topic, see the CRaC Discussion Forum.](https://forums.foojay.io/forums/forum/coordinated-restore-at-checkpoint-crac/)**
