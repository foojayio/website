---
title: "Announcement: Jakarta EE 9.1 Has Been Released!"
slug: "jakarta-ee-91-released"
date: "2021-05-25T14:57:35+00:00"
lastmod: "2021-08-23T15:32:31+00:00"
description: "The Jakarta EE Working Group Releases Jakarta EE 9.1 as Industry Continues to Embrace Open Source Enterprise Java."
authors:
  - "ivar-grimstad"
image: "https://www.agilejava.eu/wp-content/uploads/2021/05/JakartaEE91_release.png"
categories:
  - "Jakarta EE"
  - "Microservices"
  - "Release Notes"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**Join us in celebrating a new release of Jakarta EE!**

[The Jakarta EE Working Group Releases Jakarta EE 9.1 as Industry Continues to Embrace Open Source Enterprise Java](https://jakarta.ee/news/jakarta-ee-9-1-released/)! Jakarta EE 9.1 adds support for Java SE 11 runtimes to the foundational Jakarta EE 9 release.

This gives developers more flexibility when migrating from previous Jakarta EE releases.  
[![](https://www.agilejava.eu/wp-content/uploads/2021/05/JakartaEE91_release.png)](https://www.agilejava.eu/wp-content/uploads/2021/05/JakartaEE91_release.png)

In order to upgrade to the new version, simply change the dependency version in your *pom.xml* to `9.1.0`. If you are upgrading from a version prior to Jakarta EE 9, follow the [migration steps](https://www.agilejava.eu/2021/01/22/migration-guide/) for the namespace change from `javax.*` to `jakarta.*`.

#### Maven dependency for Jakarta EE Platform 9.1

```java
<dependency>
  <groupId>jakarta.platform</groupId>
  <artifactId>jakarta.jakartaee-api</artifactId>
  <version>9.1.0</version>
</dependency>
```

#### Maven dependency for Jakarta EE Web Profile 9.1

```java
<dependency>
  <groupId>jakarta.platform</groupId>
  <artifactId>jakarta.jakartaee-web-api</artifactId>
  <version>9.1.0</version>
</dependency>
```
