---
title: "Fixing Vulnerabilities in Maven-Based Projects"
slug: "fixing-vulnerabilities-in-maven-projects"
date: "2020-10-20T06:40:22+00:00"
lastmod: "2022-01-25T08:22:01+00:00"
description: "In this article, I will explain how you can fix vulnerabilities in third-party libraries when using Maven, even when it is not obvious."
canonical: "https://snyk.io/blog/fixing-vulnerabilities-in-maven-projects/"
authors:
  - "bmvermeer"
image: "https://lh4.googleusercontent.com/mZSSwqr_bQsZ-HNTvPOThi2xscIaO49aDfH4QNj3GHXhgGHrq-gw7dw6EZLOvqu-n0ILqBtb9bVq_1gOwGZo0qRYuWY82HZ3_kXUXpN1tXQlnOlHzQtGUscExnxsofwyLBu4ESep"
categories:
  - "Maven"
  - "Security"
  - "Tools"
tags:
related_posts:
  - "faster-maven-builds-1"
  - "introduction-to-maven-toolchains"
  - "understanding-apache-maven-part-1-the-basics"
enlighterjs: true
frozen: false
---

Maven is still the most used build system in the Java ecosystem. According to the [JVM report 2020](https://snyk.io/blog/jvm-ecosystem-report-2020/), Maven is the number one build tool in the ecosystem with two-thirds of the share.

Therefore, it is important to know how Maven works. For instance, if you find vulnerabilities in your Maven project using Snyk, how can you fix them?

In this article, I will explain how you can fix vulnerabilities in third-party libraries when using Maven, even when it is not obvious.

Finding vulnerabilities
-----------------------

If you are using Maven for building your project and managing your dependencies for your project, there are several ways to scan your projects, using tools such as [Snyk](https://snyk.io). This is regardless of the actual programming language you use---this can be Java, Kotlin, Scala, Groovy, or any other language that is designed for the Java Virtual Machine (JVM).  

Scanning your Maven project for vulnerabilities can be done, by using Snyk, for example. In the example below, I use the [Snyk CLI](https://support.snyk.io/hc/en-us/articles/360003817357-Snyk-for-Java-Gradle-Maven-#UUID-3d91eb09-1fcf-e973-89ff-bf24ab0ddfe2_section-idm13169231445744), which will show you if and how you can fix vulnerabilities, by updating the top-level dependencies. However, if there is no improved version of the top-level dependencies, Snyk shows you if there is a newer version of the underlying dependency that fixes the vulnerabilities.
![](https://lh4.googleusercontent.com/mZSSwqr_bQsZ-HNTvPOThi2xscIaO49aDfH4QNj3GHXhgGHrq-gw7dw6EZLOvqu-n0ILqBtb9bVq_1gOwGZo0qRYuWY82HZ3_kXUXpN1tXQlnOlHzQtGUscExnxsofwyLBu4ESep)

Fixing top-level vulnerabilities
--------------------------------

The easiest way to fix a vulnerability found by Snyk is to change the top level library, if possible. If the library does not have underlying dependencies it is quite obvious that you need to upgrade to a newer version that does not have that particular issue. The same applies when an underlying dependency does have an issue but there is already a newer version of the top-level dependency available that doesn't have the issue.

There are a few ways to switch the version.  

First, find out where the version is specified. For top-level dependencies, in most cases, you can find it in one of the following:

* in a parent pom
* in a property
* direct at the dependency level

### Parent POM

If the dependency you need to upgrade is defined in a parent pom, first look for a newer version of the parent pom. Take Spring Boot for instance, if there already is a newer version of the parent, consider upgrading to that newer version first and see if that solves the problem. This way you update the whole set of dependencies and, in most cases, they play along better than just upgrading a single dependency. If you cannot switch or change the parent, jump to the direct approach.

```xml
<parent>
   <groupId>io.foojay</groupId>
   <artifactId>demo-parent</artifactId>
   <version>1.0.4.RELEASE</version>
</parent>
```


### Properties

If a property is used, update the property first. This can potentially update other dependencies as well, but just as with the parent pom, there is a good reason for this. If libraries are released as a set you know that they work better together if they have the same version.

```xml
<properties>
    <commons-lang3.version>3.9</commons-lang3.version>
</properties>
```


### Direct

Finally, your dependency is declared in the dependencies part of your maven pom file. Here, you specify the groupId, artifactId, and in many cases the version. If the version is declared here, simply update it. If not, you can add it.

**Note that this is the lowest level of declaring the dependency version and will override all the other ways of setting the version.**

```xml
<dependency>
  <groupId>org.eclipse.collections</groupId>
  <artifactId>eclipse-collections</artifactId>
  <version>10.4.0</version>
</dependency>
```


Fixing underlying dependencies
------------------------------

Let's say there is not yet a newer version of your top-level dependency available but one of the underlying dependencies has an issue you need to fix. The CLI already notified you that there is a newer version of that underlying dependency available that fixes the problem.

### Bill of Materials

Many frameworks use a Bill of Materials (BOM) to manage underlying dependencies. A BOM is a special kind of POM that is used to control the versions of a project's dependencies and provide a central place to define and update those versions.

When an underlying dependency needs to be replaced in a framework like Spring Boot, take a look if this dependency is part of the BOM. If so, check if there already is a newer version of that specific BOM and update the version. In Spring Boot and many other well-known frameworks, the versions of the BOM are specified as a property. You can override the property in your own Maven POM file by adding a property.  

In the example below, I override the BOM for Jackson in my Spring Boot project:

```xml
<properties>
    <jackson.version>2.10.2.20200130</jackson.version>
</properties>
```


### Dependency management

In a Maven POM, it is possible to exclude and include specific underlying dependencies when declaring a top-level dependency. However, this can be problematic when two libraries share the same underlying dependency. A best practice in Maven is to bundle these declarations in the dependency management section. This section allows project authors to directly specify the versions of artifacts to be used when they are encountered in transitive dependencies. In addition to the example below, the [documentation](http://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html#Dependency_Management) clearly explains how dependencyManagement should be used:

```xml
<dependencyManagement>
   <dependencies>
       <dependency>
           <groupId>org.yaml</groupId>
           <artifactId>snakeyaml</artifactId>
           <version>1.26</version>
       </dependency>
   </dependencies>
</dependencyManagement>
```


Conclusion
----------

As you can see, there are several ways to mitigate security issues in open source dependencies for a Maven-based project.

Even when there is no direct fix by updating a top-level dependency, you are able to filter out an underlying transitive dependency and switch to the newer available version.

Snyk open source scanning provides you with information on what version you need to upgrade. This article shows you how you can configure it properly in your Maven project.
