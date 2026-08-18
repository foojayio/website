---
title: "Upgrade a Java 8 Project to 11 or Higher"
slug: "upgrade-a-java-8-project-to-11-or-higher"
date: "2023-06-08T15:12:27+00:00"
lastmod: "2023-06-08T15:40:31+00:00"
description: "Keeping a project on Java 8 will eventually lead to running it on a non-maintained runtime. Aim for the stars and go for Java 17!"
canonical: "https://www.azul.com/blog/upgrade-a-java-8-project-to-11-or-higher/"
authors:
  - "frankdelporte"
image: "10-11-unsupported-java-1024x400-1.jpeg"
categories:
  - "Java Core"
tags:
related_posts:
  - "9-outdated-ideas-about-java"
  - "what-java-version-are-you-running-lets-take-a-look-under-the-hood-of-the-jdk"
  - "7-reasons-to-switch-to-openjdk-17-as-a-jakarta-ee-developer"
  - "new-between-quarters-security-updates-for-java-what-cspus-mean-for-your-release-pipeline"
frozen: false
---

**Java 8 is a Long Term Supported (LTS) version, and [Azul](https://www.azul.com/) will maintain it until 2030 for customers with a support license. You can continue to run Java 8 applications until then on updated and secure Java runtimes. But by staying on Java 8, you miss many other improvements in runtime performance and language enhancements for your developers.**
![](6-1-23-java-8-11-higher-1024x400.jpg)

Keeping a project on Java 8 will eventually lead to running it on a non-maintained runtime.

In an earlier article, [Unsupported Versions of Java Are Dangerous](https://www.azul.com/blog/unsupported-versions-of-java-are-dangerous/), Simon Ritter points out the problems if you are still running on an older version <kbd>–</kbd> Java 7 in this case -- that is no longer supported.

Azul still supports Java 7 until 2027, but other distributions do not support it. You can find the complete [support lifecycle for existing and planned JDK releases](https://www.azul.com/products/azul-support-roadmap/) on the Azul website.
> "***The adage 'if it ain't broke, don't fix it' is often applied to deployed applications. As a result, almost all production enterprise applications, including mission-critical ones, are still running on older versions of Java. Sometimes very old... and sometimes unsupported versions of Java*** ." --- **Simon Ritter, Deputy CTO, Azul**
![](10-11-unsupported-java-1024x400-1.jpeg)

## What are you Missing in Java 8?

Since Java moved to a 6-month release cycle in 2018, each new version brings new features and performance improvements. In most enterprise environments, organizations decide to stick to the LTS versions (11 in 2018, 17 in 2021, 21 in September 2023), which is a reasonable option as it prevents going through significant test cycles every six months.

But by sticking to an older version, your DevOps and developer teams are missing a lot of tools that would allow them to make more performant and better-written applications. The article [9 Outdated Ideas About Java](https://www.azul.com/blog/9-outdated-ideas-about-java) gives an overview of several Java evolutions between versions 8 and 19.
![](9-Things-1024x410.jpg)

## Running applications on a newer JDK

As Java always has aimed to keep stability in the runtimes, in most cases you can execute your Java 8 application without any modifications on a newer JDK.

The significant disadvantage of this approach is that your development environment is stuck to this old version.

Further maintenance will become more complex as IDEs and build tools move on and create more problems in the code.

## What are the challenges of migrating from 8 to a newer Java?

The move from 8 to 11 has a more significant impact compared to 11 to 17. The main reason is multiple changes introduced in Java 9.

Specifically, the introduction of the module system and <kbd>–</kbd> more importantly <kbd>–</kbd> the encapsulation of the internal JDK APIs.

### 1. Modules

JDK 9 introduced modularity at two levels:

* **in the JDK <kbd>–</kbd>** all the class libraries (around 4,500) are packaged into modules as described in [JEP-200](https://openjdk.org/jeps/200). This allows combining JDK modules at compile time, build time, and run time into various configurations. These include the full JDK, configurations equivalent to the previously known JRE (Java Runtime Environment), custom configurations tailored to specific applications, etc.
* **in application code <kbd>–</kbd>** as a developer, you can break your application into modules. This is not a requirement but it could simplify upgrade processes, although this is not widely used.

If you keep all jars in the classpath, your existing application should still work, and modularity won't have an impact.

### 2. Internal APIs

With the move to Java 9 and the integration of modularity, some internal APIs got encapsulated and are no longer accessible. The most well-known one is `sun.misc.Unsafe`. The \`Unsafe\` name should ring a bell as this indicates that it was never intended or recommended to be used by Java developers.

On top, as the implementation of these internal APIs is not part of the [Java Technology Compatibility Kit (TCK)](https://en.wikipedia.org/wiki/Technology_Compatibility_Kit), their implementation is not identical in the various JDK distributions. Still <kbd>–</kbd> as `sun.misc.Unsafe` provides control of low-level systems like memory manipulation <kbd>–</kbd> it was used in projects that wanted to achieve specific performance improvements, for example Log4j and Spring.

These projects had to be adapted to handle this encapsulation and the changes in the JDK, meaning you will probably need to upgrade to new versions of these dependencies, which may also introduce other changes that will impact your application code.

To be able to estimate the impact of these changes since Java 9, an extra tool was added in the JDK called `jdeps` that can analyze your code and tell you where these internal APIs are used. This is further described in the [Java Dependency Analysis Tool](https://wiki.openjdk.org/display/JDK8/Java+Dependency+Analysis+Tool).

|                     Why Migrate from Java 8 to 11                      |                             Why Migrate from Java 8 to 17                             |
|------------------------------------------------------------------------|---------------------------------------------------------------------------------------|
| The move from 8 to 11 has a more significant impact than from 11 to 17 | If the migration fails, you can fall back to Java 11                                  |
| More libraries are likely to be compatible with Java 11                | The impact of changes between Java 11 and 17 is much lower than between Java 8 and 11 |
| The migration from Java 8 to 17 requires an estimated 20% more effort  | It will be easier to migrate to Java 21 from Java 17                                  |
| Java 9 introduced modules and changes to internal APIs                 | Estimating the impact of outdated third-party libraries is under your control         |

Deciding to migrate to Java 11 or Java 17

> "***When you are upgrading your codebase and dependencies to a newer Java version, you also need to take into consideration that libraries might already have moved to JDK11 or even JDK17, and with this, they no longer support older JDKs. This means they won't provide security fixes either, which could be a security problem. In many cases, this would also be a reason to move to at least JDK11." ---*** **Gerrit Grunwald, Senior Developer Advocate, Azul**

## Migrating from Java 8 to a Newer LTS Version

At the moment, it makes perfect sense to move directly from Java 8 to 17 (the latest LTS version), allowing you to move more quickly to the upcoming 21 LTS later this year.

The impact of changes between Java 11, 17, and 21 is much lower compared to the differences between Java 8 and 11.

One of the main challenges for such a migration will probably be the third-party libraries used in your project. The more of these libraries you use, the more likely one of them does not support Java 17 yet.

It's recommended to start with the creation of a list of dependencies, and research them to find out what the current state of these is.

The possible outcomes are:

|                                                Outcome                                                 |                                              Significance                                              |
|--------------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------|
| The library is no longer maintained and is not compatible with Java 11 and/or 17                       | A replacement is needed, which can result in significant changes in your project                       |
| The library has evolved and is available in a newer version which is compatible with Java 11 and/or 17 | Minor impact, update the version and check if you need to refactor                                     |
| The library is not yet supported on Java 11 and/or 17                                                  | Wait or replace it, maybe contributing back to the library project to speed up development for support |

Outcomes and significance of dependency research

Anyhow, the biggest challenge of migrating a project is... time! If the code itself and the libraries are compatible with Java 11 and/or 17, only minor changes will be needed, and the migration will mainly be a task for the QA team. But if incompatibilities are found, the timeline can become unpredictable.

Regarding the actual migration, there are no specific patterns to follow. Updating the Java version, e.g., in the pom-file of a Maven project, will probably already show the most important code parts to be reviewed as they will break the build.

From there it's an organizational problem, as the need must be well-defined to push the migration forward and make it happen. Further delays will bite you back hard later... Making it happen fast will also prevent extended simultaneous maintenance of the original project and the migrated project, which can prove difficult.
> "***From my experience, while assisting customers during their upgrade, I've seen that it's worth trying to move immediately from Java 8 to 17. If it fails, it will be because some library is not yet supporting 17. At that time, falling back to Java 11 is always possible, as all cleanup done for the Java 17 move will be usable for the Java 11 move.*** ***But I would strongly recommend not using Java 17 coding features during the move. Limit those to what Java 11 provides. As soon as you complete the move to either Java 11 or 17 and the application is running as expected, the second round of migration can be started to simplify the code based on the new possibilities that were introduced in the more recent versions of Java.*** ***From a risk management and effort perspective, I think the migration from Java 8 to 17 needs an estimated 20% more effort than from Java 8 to 11.*** ***The biggest problems in a migration project I have seen at a customer were caused by an ad hoc and last-minute project where the impact of outdated third-party libraries was not correctly estimated.*** " ***---*** ****Michael Roeschter, Sales Engineer, Azul****

## Conclusion

Yes, upgrading your application code from Java 8 to a newer version will take time and effort.

But postponing this process will only make it more complex.

And in most cases it will become inevitable anyway.

We recommend you cut to the chase and dive into the code to start the migration.

**Aim for the stars and go for Java 17!**

## Further Reading

* [EMT4J: Adoptium subproject](https://adoptium.net/blog/2022/12/emt4j-an-easier-upgrade-for-java-applications/) providing a tool to help migrate Java 8 applications to 11 or 17.
* [InfoQ: "Upgrading from Java 8 to Java 12"](https://www.infoq.com/articles/upgrading-java-8-to-12/) by Trisha Gee.
* [Guide to *sun.misc.Unsafe*](https://www.baeldung.com/java-unsafe) by Baeldung.
* [Java Magic. Part 4: sun.misc.Unsafe](http://mishadoff.com/blog/java-magic-part-4-sun-dot-misc-dot-unsafe/) by Mykhailo Kozik.
