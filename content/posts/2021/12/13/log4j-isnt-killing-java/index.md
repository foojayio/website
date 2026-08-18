---
title: "Log4j2 Isn’t Killing Java"
slug: "log4j-isnt-killing-java"
date: "2021-12-13T18:51:34+00:00"
lastmod: "2021-12-14T18:00:30+00:00"
description: "Java developers typically choose from several logging systems or facades. Many of these logging frameworks have grown to work together."
authors:
  - "erikcostlow"
image: "car.jpeg"
categories:
  - "Security"
tags:
related_posts:
  - "java-where-the-wild-code-isnt"
  - "java-logging-what-to-log-what-not-to-log"
  - "log4shell-critical-log4j-rce-vulnerabilty-update-to-version-2-15-0"
frozen: false
---

In the season of resurrection, I'd like to join the ranks of those who have made [long careers announcing the death of Java](https://www.forrester.com/blogs/10-11-23-java_is_a_dead_end_for_enterprise_app_development/) who follow up each obituary with an equally shocking revelation that [it is alive again](https://jaxenter.com/java-not-dead-yet-133459.html) as one of the top programming languages [25 years running](https://www.oracle.com/news/connect/25-years-of-java-technology-community-family.html). Even before the recent log4j2 vulnerability allegedly "killing" it yet again, we have a joke in the Foojay chat that a Java museum would be called a cemetery for the [number of times this has happened](https://redmonk.com/jgovernor/2016/02/24/on-lightbend-lagom-and-java-is-dead-is-dead/).

There are [many good write-ups](https://www.infoq.com/news/2021/12/log4j-zero-day-vulnerability/) by security firms describing [how the log4j2 exploit works](https://www.lunasec.io/docs/blog/log4j-zero-day/), there are write-ups about [how to find where you are vulnerable and defend](https://www.contrastsecurity.com/security-influencers/0-day-detection-of-log4j2-vulnerability), and far too many posts about scrambling to do unnecessary performative defense techniques on the network.

Here we will cover the Java ecosystem to describe what logging frameworks are, where/why they are used, and how teams can observe and control what their JVMs are doing.

## What Security Efforts Should Java Developers Do

Patching the JDK and libraries quickly is the most effective technique to avoid most mass exploitation.

### Patching the Library (Required)

When a vulnerability resides in a library, the most effective technique is to patch the library and remove the vulnerability. If you do not patch your library, there is a very high likelihood that your application will be hacked, with attackers gaining complete access to your system and its data.

Patching is effective in 100% of cases.

Logging frameworks can come in from any dependency - they could be a transitive dependency (added by another library) rather than a direct dependency (added by you). Use a dependency analysis tool, such as Contrast Community Edition, to detect your dependencies and other custom vulnerabilities.

Other open source tools that reveal dependencies are Maven dependency tree ([dependency:tree](https://maven.apache.org/plugins/maven-dependency-plugin/usage.html#dependency:tree)) and [Gradle dependency tree](https://docs.gradle.org/current/userguide/viewing_debugging_dependencies.html). IDEs like NetBeans feature a [dependency graph visualizer](https://netbeans.apache.org/wiki/MavenBestPractices.asciidoc#_dependency_management).

In the log4j2 vulnerability, you must upgrade to 2.16.0 or higher.

### Patching JREs to the Java Security Baseline (Recommended, Periodic)

Every Java major version maintains an ongoing security baseline. As the JDK is patched each quarter with new security improvements, [the baseline moves forward](http://javadl-esd-secure.oracle.com/update/baseline.version). Java installations below the security baseline contain known security issues and should be updated.

This is a standard security best practice and does not relate directly to or fix the log4j2 library issue.

Teams can automatically monitor the security baseline and upgrade their systems by using the [Foojay Disco API](https://github.com/foojayio/discoapi). Developers can pair this with a GitHub action and ensure that each build of their code uses the latest security update for their code. When security events occur, upgrading the JRE is the same as rebuilding and redeploying the code. [Examples of this GitHub](https://github.com/foojayio/discoTestingMatrix) action appear in the testing matrix.

The Java security baseline changes on the Tuesday closest to the 17th of each January, April, July, and October. Details appear in the [Oracle Critical Patch Update schedule](https://www.oracle.com/security-alerts/) and the [OpenJDK Vulnerability Group](https://foojay.io/pedia/security-vulnerability-management/) uses the same schedule. Unscheduled security updates can occur in the event of a major issue. There was no such issue in the log4j2 exploit.

A sample configuration to use the security baseline of Java 11 is:

```yaml
jobs:
  java11:
    runs-on: ${{ matrix.os }}
    strategy:
      matrix:
        os: [ubuntu-latest, macos-latest, windows-latest]
        update: [x]
        package: [jdk, jre]
      fail-fast: false
      max-parallel: 4
    name: ${{ matrix.package }} 11.0.${{ matrix.update }}, ${{ matrix.os }}
    steps:
    - uses: actions/checkout@v1
    - name: Set up JDK 11 Zulu
      uses: foojayio/setup-java@disco
      with:
        java-package: ${{ matrix.package }}
        java-version: 11.0.${{ matrix.update }}
        distro: zulu
    - name: java -version
      run: java -version
```

### Regularly Detect Custom Security Flaws (Recommended in Test)

Automated security tools can catch security flaws without manual security expertise. By adding an integrated agent into a Java application, you can get passive detection in your application that records security information. Unlike tools that look at dependency numbers to determine if vulnerabilities are present, these track the same dependency information byt integrated analyzers can tell you about the resulting combination of these libraries and if they're used securely together.

For example, rather than simply looking at the presence of log4j2 and its version, the integrated analyzer can determine if you log remote inputs in a way that attackers can control.

In addition, a free analyzer like [Contrast Community Edition](https://www.contrastsecurity.com/contrast-community-edition) was [able to catch the log4j2 0-day](https://www.contrastsecurity.com/security-influencers/0-day-detection-of-log4j2-vulnerability) and can catch many other security flaws such as:

* Does my application contain any SQL Injection flaws, either in Hibernate, JBDC, or anywhere else?
* Can a remote user control any input sent to Runtime.exec, resulting in a Command Injection vulnerability?
* What cryptographic algorithms does my application use, where, and do they meet the appropriate standards?
* Do I combine libraries in a way that would create an unexpected and undesirable security surprise, like OGNL parsing of input?
* And many more security flaws that are unique to your application

### Monitor Security Events with JDK Flight Recorder

[JDK Flight Recorder](https://www.azul.com/products/components/zulu-mission-control/) is a performance analysis tool included in modern OpenJDK distributions that can also produce some security information with low overhead. Teams can use JDK Flight Recorder to record many IO operations like what files the JRE accesses, or [what classes are paired with Deserialization](https://inside.java/2021/03/02/monitoring-deserialization-activity-in-the-jdk/).

By monitoring Java application events with JDK Flight Recorder and streaming events into a Security Information and Event Management (SIEM) system, Java teams can monitor for anomalous behavior and/or pair known-safe classes with the [Java deserialization filters](https://openjdk.java.net/jeps/415) that prevent exploitation.

## What Security Efforts Yield Little Benefit

In the case of log4j2 exploitation, network based defenses like Web Application Firewalls (WAFs) and similar tools may accomplish something in the short term, but generally their effectiveness is low and the amount of effort is extremely high.

* Network defense is not effective. One of the popular memes going around involves a photoshopped car, where an injection attack appears on the license plate. This is funny because developers know that input will be parsed through computer vision and logged. The data that makes up the injection never really exists at the network layer. Similarly most applications use different parts of data, decode data, and log all kinds of things. It's unfeasible for any network tools to match enough patterns to make much of an impact.
* Watching and tracking who attacks to block IPs is not particularly effective. While different groups may maintain lists of who's launching attacks, AWS IPs are called Elastic because they regularly change so if you block one, it opens the question of when you unblock it, or you will just be attacked soon after by a different IP.

![alt_text](car.jpeg "Car with injection license plate")

Above: a picture showing the exploitation payload that is infeasible to detect at a network layer.

### System Properties and Dynamic Patches are Moderately Effective

There are several patches and system properties that can control the behavior of log4j2 and block exploitation. These are reasonable in cases where the library cannot be updated or to gain time while teams work to update the dependency.

[Two Java system properties](https://www.contrastsecurity.com/security-influencers/0-day-detection-of-log4j2-vulnerability):

* -Dcom.sun.jndi.rmiobject.trustURLCodebase=false
* -Dcom.sun.jndi.cosnaming.object.trustURLCodebase=false

Setting these to false will block remote loading.

A dynamic patch is available that will [connect to a running JVM and patch it](https://github.com/corretto/hotpatch-for-apache-log4j2). This patch must be applied each time the JVM is started. This is decent for a couple uses but it is easier to update the library than to automate this patch.

## How is Java Logging Handled

Java developers typically choose from several logging systems or facades. Many of these logging frameworks have grown to work together over the years as communities have grown, merged, and mingled:

* [System Logger](https://docs.oracle.com/javase/9/docs/api/java/lang/System.html#getLogger-java.lang.String-) (2017, recommended) was introduced in JDK 9. It improved on the API of JDK Logger and offers a facade similar to SLF4j, redirecting the JDK's logs to a logger os the application team's choosing.
* JDK Logger (2004) was introduced in [Java 1.4](https://javaalmanac.io/jdk/1.4/api/java/util/logging/package-summary.html). It is common because of its position in the JDK, but the API was a bit awkward. While helpful, other frameworks made logging easier.
* [Log4j and Log4j2](https://logging.apache.org/log4j/2.x/) are community driven loggers that improved the API, making it easier for teams to control what they logged and when that data appeared through levels.
* [Logback](http://logback.qos.ch/) and [SLF4j](http://www.slf4j.org/) are other popular loggers. SLF4J is a simple logging facade that helps teams deal with the many loggers - library maintainers would log into the facade and the application developer would then configure which of the underlying loggers they use to unify the output. In addition to creating a good API, this minimized dependencies.
* [JBoss Logger](https://docs.jboss.org/process-guide/en/html/logging.html) is another logger popular in the JBoss ecosystem, it performed well and worked quickly. Today it supports other popular frameworks like Quarkus.
* Apache Commons-Logging (2002) pre-dates the JDK logger and inspired many of the APIs. Its last release was in 2014 as people moved to other APIs the supported the goal of good logging rather than a single project.

### Recommended Logger in 2022

Newer greenfield projects with fewer dependencies should consider the System Logger.

Projects with many dependencies benefit from using the same logger as most of their dependencies or another logging facade.

When no other logger is present, System Logger is essentially JDK Logger with a good API.

### What do the Loggers Actually Do

The logging frameworks enable application owners to see log messages, timestamps, thread names, and other data in a common format.

Additionally, teams can redirect different output to different locations or to not appear at all -- for example, you can send access logs to one file, system reports to another, and select whether to show all Levels or to dynamically [see select DEBUG levels for one library but not others](https://logging.apache.org/log4j/2.x/faq.html#reconfig_level_from_code).
