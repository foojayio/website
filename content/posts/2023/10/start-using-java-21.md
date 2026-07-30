---
title: "Start using Java 21 in your apps on Open Liberty 23.0.0.10"
slug: "start-using-java-21"
date: "2023-10-23T08:39:06+00:00"
lastmod: "2023-10-23T08:41:14+00:00"
description: "Java 21, including virtual threads, is finally here! And you can try it all out now on Open Liberty 23.0.0.10 for free."
canonical: "https://openliberty.io/blog/2023/10/17/23.0.0.10.html"
authors:
  - "laura-cowen"
image: "/images/posts/2023/10/start-using-java-21/OL_logo_green_on_white.png"
categories:
  - "JDK21"
  - "Release Notes"
tags:
related_posts:
  - "from-azure-active-directory-via-openid-connect-to-open-liberty-and-java"
  - "how-we-developed-the-eclipse-openj9-criu-support-for-fast-java-startup"
  - "semeru-v11-beyond-oct-2024"
enlighterjs: true
frozen: false
---

**Java 21 is finally here! Java 21 is the first long-term support (LTS) release since Java 17 was released two years ago. It offers some new functionality and changes that you'll want to check out for yourself. In particular, there's the introduction of virtual threads. And you can try it all out now on Open Liberty 23.0.0.10.**

[Open Liberty](https://openliberty.io/?utm_source=foojay&utm_medium=news&utm_content=java21) is a developer-friendly, fast, modular Java application runtime for the cloud. The Open Liberty project began six years ago when [IBM open-sourced](https://openliberty.io/blog/2017/09/19/open-sourcing-liberty.html?utm_source=foojay&utm_medium=news&utm_content=java21) the Liberty Java runtime. IBM's commercial Java runtime, WebSphere Liberty, is built from the Open Liberty project. You can use Open Liberty for free though.

<img fetchpriority="high" decoding="async" class="alignnone size-medium wp-image-102813" src="/images/posts/2023/10/start-using-java-21/OL_logo_green_on_white-700x120.png" alt="Open Liberty logo" width="700" height="120">

<br />

In this release of Open Liberty, in addition to support for Java 21, we also have an update to Liberty's `featureUtility` command, which you use to install modular features in the core Liberty runtime; the command now verifies feature authenticity by default when you install a new feature into Liberty.

In [Open Liberty](https://openliberty.io/?utm_source=foojay&utm_medium=news&utm_content=java21) 23.0.0.10:

* [Support for Java 21 in Open Liberty](#java21)
* [featureUtility now verifies feature signatures by default](#feature)

View the list of fixed bugs in [23.0.0.10](https://github.com/OpenLiberty/open-liberty/issues?q=label%3Arelease%3A230010+label%3A%22release+bug%22).

Check out [previous Open Liberty GA release blog posts](https://openliberty.io/blog/?search=release&search!=beta&utm_source=foojay&utm_medium=news&utm_content=java21).

Try Java 21 on Open Liberty 23.0.0.10 now {#_get_open_liberty_23_0_0_10_now}
----------------------------------------------------------------------------

Available through [Maven, Gradle, Docker, and as a downloadable archive](#run).

Support for Java 21 {#java21}
-----------------------------

Try out the new changes in Java 21 now and test your applications, microservices, and runtime environments.

To run Open Liberty with Java 21:

1. Download and install [Open Liberty 23.0.0.10](https://openliberty.io/start/?utm_source=foojay&utm_medium=news&utm_content=java21#runtime_releases), or later.
2. Download the latest release of Java 21 from [adoptium.net](https://adoptium.net/temurin/releases/?version=21).
3. Edit your Open Liberty runtime [server.env file](https://openliberty.io/docs/latest/reference/config/server-configuration-overview.html?utm_source=foojay&utm_medium=news&utm_content=java21#server-env) to point `JAVA_HOME` to your Java 21 installation.
4. Start testing!

Here are some highlights of the changes between Java 18 and Java 21:

* 400: [UTF-8 by Default](https://openjdk.java.net/jeps/400)
* 408: [Simple Web Server](https://openjdk.java.net/jeps/408)
* 413: [Code Snippets in Java API Documentation](https://openjdk.java.net/jeps/413)
* 416: [Reimplement Core Reflection with Method Handles](https://openjdk.java.net/jeps/416)
* 418: [Internet-Address Resolution SPI](https://openjdk.java.net/jeps/418)
* 421: [Deprecate Finalization for Removal](https://openjdk.java.net/jeps/421)
* 422: [Linux/RISC-V Port](https://openjdk.org/jeps/422)
* 431: [Sequenced Collections](https://openjdk.org/jeps/431)
* 439: [Generational ZGC](https://openjdk.org/jeps/439)
* 440: [Record Patterns](https://openjdk.org/jeps/440)
* 441: [Pattern Matching for switch](https://openjdk.org/jeps/441)
* 449: [Deprecate the Windows 32-bit x86 Port for Removal](https://openjdk.org/jeps/449)
* 451: [Prepare to Disallow the Dynamic Loading of Agents](https://openjdk.org/jeps/451)
* 452: [Key Encapsulation Mechanism API](https://openjdk.org/jeps/452)

But perhaps the most anticipated one of all is the introduction of virtual threads in Java 21:

* 444: [Virtual Threads](https://openjdk.org/jeps/444)

Virtual threads were designed to provide higher throughput for running tasks that spend most of their time blocked, like waiting for I/O operations. Will the impact of virtual threads live up to the anticipation? Find out for yourself by trying them out in your applications that run on the best Java runtime, Open Liberty!

For more information on Java 21, see:

* [Java 21 release notes](https://jdk.java.net/21/release-notes)
* [API Javadoc page](https://docs.oracle.com/en/java/javase/21/docs/api/index.html)
* [migration guide](https://docs.oracle.com/en/java/javase/21/migrate/getting-started.html)

featureUtility now verifies feature signatures by default {#feature}
--------------------------------------------------------------------

The `featureUtility` command now verifies feature signatures before installing the feature into the Liberty runtime. It identifies whether the feature originated from the Liberty development team or is a third-party user feature.

Previously, the `featureUtility` tool only verified checksums. While checksums are essential for integrity (showing that the file has not been tampered with), verifying checksums did not ensure the authenticity of downloaded files.

We've now implemented an additional step in the process of verifying feature signatures to check both the authenticity and integrity of features that are downloaded from the Maven Central repository.

When running the `featureUtility` command:

* The default behavior is now `--verify=enforce`, meaning that it verifies all specified Liberty features.
* To keep the old behavior instead, you can skip the verification process by using the `--verify=skip` parameter.
* Alternatively, you can set the verification option through environment variables or a `featureUtility.properties` file.

For more information, see:

* [featureUtility installFeature command docs](https://openliberty.io/docs/latest/reference/command/featureUtility-installFeature.html?utm_source=foojay&utm_medium=news&utm_content=java21#_options)
* [featureUtility installServerFeature command docs](https://openliberty.io/docs/latest/reference/command/featureUtility-installServerFeatures.html?utm_source=foojay&utm_medium=news&utm_content=java21)
* [featureUtility commands](https://openliberty.io/docs/latest/reference/command/featureUtility-commands.html?utm_source=foojay&utm_medium=news&utm_content=java21)

Develop and run your apps using Open Liberty 23.0.0.10 {#run}
-------------------------------------------------------------

If you're using [Maven](https://openliberty.io/guides/maven-intro.html?utm_source=foojay&utm_medium=news&utm_content=java21), include the following in your `pom.xml` file:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;plugin&gt;
    &lt;groupId&gt;io.openliberty.tools&lt;/groupId&gt;
    &lt;artifactId&gt;liberty-maven-plugin&lt;/artifactId&gt;
    &lt;version&gt;3.8.2&lt;/version&gt;
&lt;/plugin&gt;</pre>

Or for [Gradle](https://openliberty.io/guides/gradle-intro.html?utm_source=foojay&utm_medium=news&utm_content=java21), include the following in your `build.gradle` file:

<pre class="EnlighterJSRAW" data-enlighter-language="gradle">buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'io.openliberty.tools:liberty-gradle-plugin:3.6.2'
    }
}
apply plugin: 'liberty'</pre>

Or if you're using [container images](https://openliberty.io/docs/latest/container-images.html?utm_source=foojay&utm_medium=news&utm_content=java21):

<pre class="EnlighterJSRAW">FROM icr.io/appcafe/open-liberty</pre>

Or take a look at our [Downloads page](https://openliberty.io/start/?utm_source=foojay&utm_medium=news&utm_content=java21).

If you're using [IntelliJ IDEA](https://plugins.jetbrains.com/plugin/14856-liberty-tools), [Visual Studio Code](https://marketplace.visualstudio.com/items?itemName=Open-Liberty.liberty-dev-vscode-ext), or [Eclipse IDE](https://marketplace.eclipse.org/content/liberty-tools), try our open source [Liberty developer tools](https://openliberty.io/docs/latest/develop-liberty-tools.html?utm_source=foojay&utm_medium=news&utm_content=java21) for efficient development, testing, debugging, and application management, all within your IDE.

[Ask a question on Stack Overflow.](https://stackoverflow.com/tags/open-liberty)
