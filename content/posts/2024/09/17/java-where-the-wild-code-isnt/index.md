---
title: "Java: Where the Wild Code Isn't (On Security and Modularity)"
slug: "java-where-the-wild-code-isnt"
date: "2024-09-17T08:41:00+00:00"
lastmod: "2024-10-03T16:36:26+00:00"
description: "The OpenJDK community has made Java safer for users and developers while making it easier to design, build, and run applications quickly."
authors:
  - "erikcostlow"
image: "Favicon-3-2.png"
categories:
  - "Security"
tags:
related_posts:
  - "are-java-security-updates-important"
  - "fix-java-security-issues-while-coding-in-intellij-idea"
  - "fixing-vulnerabilities-in-maven-projects"
frozen: false
---

In the last several years, the OpenJDK community has made Java significantly safer for users and developers while at the same time making it easier to design, build, and run applications quickly.

There are two core aspects that have led to this:

1. A secure JDK, where vulnerabilities are dealt with and patched.
2. A modular JDK, where different types or risk can be clearly delineated.

1. A Secure JDK {#h2-0-1-a-secure-jdk}
--------------------------------------

The core Java platform underlies all Java applications and is kept secure by a special OpenJDK Vulnerability Group. This group helps to perform different actions and security regression tests like fuzzing and ensuring old vulnerabilities do not recur.

Another charge for this group is receiving researcher reports of vulnerabilities and helping to ensure timely attention and patching. A "secure JDK" does not mean one where there are no vulnerabilities, rather it means a community in which security is taken seriously in both word and action.

### 1.1 Quarterly Patches {#h3-1-1-1-quarterly-patches}

Most OpenJDK vendors patch around the same timeframe, using version numbers that reflect the security posture of the release. The benefit that this has for typical Java users is that they can quickly determine if their runtime meets the security baseline or requires a patch.

This patch cadence generally follows the older [Oracle Critical Patch Update timeline](https://www.oracle.com/security-alerts/) of the third Tuesday of each third month. The timeframe is short enough to minimize the window of exposure and long enough to avoid some level of "patch fatigue," while being consistent for teams to plan and schedule work.

### 1.2 CVE Advisories {#h3-2-1-2-cve-advisories}

A crucial part to addressing and patching vulnerabilities is keeping track of what they are and telling people. A key part to community work is attributing credit to the researchers who find security issues and properly disclose them to help open source projects.

The OpenJDK Vulnerability Group maintains [a list of the CVEs patched in each Java release](https://openjdk.java.net/groups/vulnerability/advisories/), with acknowledgements to the different researchers that engaged in responsible disclosure. This offers downstream Java users the ability to answer a simple question: if I use a previous version of Java, what risk do I accept by doing so?

Another excellent aspect that the OpenJDK Vulnerability Group does in these advisories is to track CVEs by component, since not all Java users leverage every component.

In short, simply saying that an older Java installation contains a CVE is not sufficient to indicate that the vulnerability exists on that system.

### 1.3 Consistent Naming {#h3-3-1-3-consistent-naming}

In early 2019, the primary OpenJDK distribution on Docker was serving "[mystery meat Java](https://www.infoq.com/news/2019/06/docker-vulnerable-java/)." A build of the source code taken out of sync with the patch schedule that missed various security patches but still used the version number as the JDK that contained the security flaws. As a result, users of the Docker image were left insecure while users of known downstream Java distributions were secured.

By recognizing the need for vulnerability disclosure and leveraging the knowns of a quarterly patch cadence, Java vendors working on OpenJDK revolve around the same numbering scheme via [JEP 322](http://openjdk.java.net/jeps/322) (time-based release versioning). Through coordination of version numbers centered around the Quarterly patch schedule, most Java vendors use the same numbers in a way that can be understood and compared against each other to know which was released later and which security patches are present in that version.

The alterative to this coordination would be a situation of semi-predictable yet meaningless numbers that would require special lookup tables to know which security patches were present where.

2. Security of a Modular JDK {#h2-4-2-security-of-a-modular-jdk}
----------------------------------------------------------------

A security posture often moves in waves. Something is considered secure until it fails, then attackers learn to emulate and build on new ways that caused the fail. By isolating the areas in which security has been a major issue, the Java platform has provided and continues to provide a secure development platform for running applications and workloads.

In spite of various vulnerabilities against the platform over the years, threats can only exist by targeting the weak points of an application. Looking at the conceptual diagram of the Java platform, it was possible to clearly identify which parts of the platform are risky and simply avoid those parts.

For example, in 2013, when Java was on the receiving end of different vulnerabilities and 0-day exploits, almost all of these exploits landed in one single area that was rarely used and is no longer present: the deployment plugin for applets and web-start applications.

### 2.1 Pinpointing Risk Through Modularity {#h3-5-2-1-pinpointing-risk-through-modularity}

The original documentation for Java 8 and below provides a [conceptual diagram](https://docs.oracle.com/javase/8/docs/) of where certain features are. From a threat-modeling perspective, this is the same location where vulnerabilities live.

A few slices are readily identifiable:

* The risk of drive-by downloads or execution is concentrated solely in the Deployment Plugin (which is no longer available in modern JREs).
* SQL Injection can only occur if the application uses JDBC. Applications that do not use SQL are not vulnerable to SQL Injection.
* Attacks against XML, such as [XXE](https://www.youtube.com/watch?v=x6QQzgf0j7o&feature=emb_logo), occur within the XML JAXP area.
* [Deserialization attacks](https://github.com/frohoff/ysoserial) stem from the Deserialization component and the IO structures.

The benefit to developers and those who run applications is that it becomes possible to look at an application and trim down the things that you should worry about to the things that you use. This is why the module information is important within the quarterly security advisories from the OpenJDK Vulnerability Group.

Developers can look at these modules and examine the risk present in each as it pertains to their application. An example is sensitive data and the risk of exposure. There is a consideration of loss with modules like Logging, but limited risk in other modules like Image/IO. The core benefit is simply understanding which risks are present and where those risks are, rather than worrying about ambiguous and unidentifiable threats with defenses in the wrong places.

Another point to note is to recognize what does not appear in the conceptual diagram and focus on the area where it does appear. For example, in the core Java diagram and modules, there is no concept of "web" or HTML. This means that work against web threats, such as XSS, belongs in the frameworks or tools which provide that functionality.

### 2.2 Reducing Risk By Removing Modules {#h3-6-2-2-reducing-risk-by-removing-modules}

While operations teams can still use a central system-wide JRE, many developers customize a single JRE to run a single application. Tools such as jlink enable developers to [create custom JREs](https://www.baeldung.com/jlink) that slim down modules. These expand modern JREs (JDK 11+) from the previous incarnations of [compact profiles](https://foojay.io/pedia/compactprofiles/) and javapackager.

By incorporating only modules that are used by the application, they remove tools that are unused. Removing unused modules also works to remove risk against those modules. A custom JRE that does not contain JDBC is unlikely to be affected by various SQL Injection attacks.

The July 2020 advisory by the OpenJDK Vulnerability Group cites [CVE-2020-14562](https://nvd.nist.gov/vuln/detail/CVE-2020-14562) in ImageIO. A custom JRE that trims out ImageIO would be unaffected by this CVE.

This distinction is crucial as many software teams are expected to do vulnerability scanning in the form of open-source dependencies. Scanners often look only at the library names and then assume that all usage of those libraries contain all CVEs. In the case of a custom jlink-ed JRE, any scanner that reported the JRE as vulnerable to this flaw would be incorrect. Removing the component removes the vulnerabilities and risk in that component. Hackers cannot attack what isn't there.

Tactical Take-Aways {#h2-7-tactical-take-aways}
-----------------------------------------------

Java users should incorporate several practices to take full benefit from the defenses of the modern JRE:

1. Follow an automated patch schedule that coincides with the OpenJDK vendor's quarterly patch cycle.
2. Automate application packaging with jlink to remove modules that are not used by the application.
3. Watch for CVEs in application libraries, and automate their updates as much as possible in line with the JRE patch schedule.

<br />
