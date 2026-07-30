---
title: "Update or Upgrade Java... and Which Version Should You Use?"
slug: "should-you-update-java-or-upgrade-and-which-version-should-you-use"
date: "2023-02-23T09:12:39+00:00"
lastmod: "2023-02-23T09:15:21+00:00"
description: "Keeping your Java environment stable requires discerning which updates to install and then installing them appropriately."
canonical: "https://www.azul.com/blog/should-you-update-java-or-upgrade/"
authors:
  - "frankdelporte"
image: "/images/posts/2023/02/should-you-update-java-or-upgrade-and-which-version-should-you-use/LTS-versus-STS.jpg"
categories:
  - "DevOps"
  - "Java Core"
  - "Security"
tags:
related_posts:
  - "foojay-podcast-4"
  - "foojay-podcast-5"
  - "are-java-security-updates-important"
  - "azul-zulu-july-2026-quarterly-update-released"
frozen: false
---

*Update versus Upgrade: what's the difference and what are the consequences of your decision?* {#h2-0-update-versus-upgrade-what-s-the-difference-and-what-are-the-consequences-of-your-decision}
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Since 2018, every six months we get an upgrade to a new OpenJDK version, and this year versions 20 and 21 will be released.

Next to these six-month releases, OpenJDK distributor Azul, amongst others, provides four critical patch updates (CPU) and patch set updates (PSU) per year.

So what's the difference between a Java upgrade and an update?

*** ** * ** ***

**Upgrades are new versions of OpenJDK**.

**Updates are security fixes and improvements to existing Java versions**.

*** ** * ** ***

![](/images/posts/2023/02/should-you-update-java-or-upgrade-and-which-version-should-you-use/LTS-versus-STS-1024x410.jpg)

Upgrades are New Versions of OpenJDK {#h2-1-upgrades-are-new-versions-of-openjdk}
---------------------------------------------------------------------------------

Until OpenJDK 9 in 2017, Oracle didn't have a fixed schedule of new releases.

It established a set of new features and released a new version whenever those features were finished.

This approach led to versions with stable features but unpredictable release dates:

* Version 6 in 2006
* Version 7 in 2011
* Version 8 in 2014
* Version 9 in 2017

In 2018 Oracle introduced a new strategy with a fixed six-months release cycle:

* Twice a year, a new version is published, including all the finished and agreed fixes and improvements.
* Some unfinished new features *can be* included. They are called experimental or incubator features, and they require additional actions to be usable, so most users don't even know they are available.

*** ** * ** ***

*Despite some skepticism in the Java community, new versions have been released **on schedule** since OpenJDK 10. In fact, the increased speed of new releases has led to **more improvements** , **new features** , and **fast feedback on experimental additions**.*

*** ** * ** ***

Java Updates are Fixes and Improvements in Existing Versions {#h2-2-java-updates-are-fixes-and-improvements-in-existing-versions}
---------------------------------------------------------------------------------------------------------------------------------

The release of a new version of OpenJDK is not an endpoint.

New updates are released regularly that contain security fixes and/or new features.

In some cases, there are even backports of new features introduced in higher versions.

For instance, TLS 1.3, which was released in 2020, was backported for Java 8, which was released in 2014.

### **Common Vulnerabilities and Exposures (CVEs)** {#JavaMaintenance:Updatevs.Upgrade-Updates:SecurityandOtherFixesandImprovementsinExistingVersions}

One of the most critical goals of these updates is to keep the Java environment safe.

CVEs (Common Vulnerabilities and Exposures) refer to vulnerabilities that are publicly known and available in the [CVE List](https://cve.mitre.org/cve/search_cve_list.html).

A group of Java distributors maintains this list and defines their impact, resulting in a Common Vulnerability Severity Score (CVSS).

Based on this score, the urgency is defined to fix the vulnerability.

Azul keeps a list of the CVEs and which Java update release contains a fix on [the docs website](https://docs.azul.com/core/cve).

CVEs affect Java applications in two ways:

1. **CVEs in Java Runtime** can be addressed by upgrading your instance of the Java Runtime, most often by a system or application administrator.
2. **CVEs in third-party Java libraries** can be addressed by upgrading the library within the application, most often by an application developer or DevOps.

Within the OpenJDK project, CVEs are handled in a continuous process, there are no specific CVE-fix releases.

All the issues that are fixed in a build are included in a single message that is addressed to the jdk-update-dev mailing list to keep the community informed.

The entire timeline of an OpenJDK version is available on [wiki.openjdk.org](https://wiki.openjdk.org/). Here are a few links with examples of how the OpenJDK community is organized:

* All the mailing lists: <https://mail.openjdk.org/mailman/listinfo>
* The timeline for OpenJDK 15: <https://wiki.openjdk.org/display/JDKUpdates/JDK+15u>
* Message about "OpenJDK 15.0.8 released": <https://mail.openjdk.org/pipermail/jdk-updates-dev/2022-July/016055.html>

CPU and PSU Releases {#JavaMaintenance:Updatevs.Upgrade-CommonVulnerabilitiesandExposures(CVEs)}
------------------------------------------------------------------------------------------------

Azul uses an approach with **Critical Patch Updates (CPU)** and **Patch Set Updates (PSU)** to organize the release of new versions.

Updates are released several times a year for all "active" (still maintained) OpenJDK versions.

These updates don't introduce significant new features; they keep the OpenJDK safe and in sync with newer versions by backporting some new features that are in high demand.

**Critical Patch Update (CPU)**

* Four releases per year in January, April, July, and October on the third Tuesday of the month.
* Contains fixes for security vulnerabilities and critical bugs, only impacting a minimal number of files.
* With only a limited number of changes, a more stable release is achieved with fewer risks of regressions.
* All users should upgrade their OpenJDK to the newest CPU as soon as possible to keep a safe environment.
* Azul Zulu CPU Builds of OpenJDK get an odd-numbered version (second digit), e.g., 17.**37**.

**Patch Set Update (PSU)**

* Contains security updates and critical bugs from a CPU, non-critical bug fixes, and new (backported) features.
* They are released at the same time as, or shortly after the CPU.
* Be sure to use PSUs if you are impacted by a bug that was fixed or needs a new feature.
* PSUs should be used in your production system before the next CPU/PSU release, as fixes in a PSU will be present in the next CPU release.
* Azul Zulu PSU Builds of OpenJDK get an even-numbered version (second digit), e.g., 17.**38**.

Support Durations of OpenJDK {#h2-5-support-durations-of-openjdk}
-----------------------------------------------------------------

OpenJDK versions are usually called LTS (Long-Term Supported) or non-LTS.

However, non-LTS versions can be separated into MTS (Mid-Term Supported) and STS (Short-Term Supported).

### LTS Versus MTS Versus STS {#h3-6-lts-versus-mts-versus-sts}

OpenJDK versions only get updates for a predefined time:

* **Long-Term Supported (LTS)**
  * This is a version selected to be supported and updated for an average of eight years.
  * Most companies stick to these LTS versions for their production deployments.
  * After the release of version 17 in 2021, Oracle switched from a three-year LTS release cycle to a two-year cycle.
  * LTS versions are: 8, 11, 17, and 21 (in September 2023).
  * LTS versions 6 and 7 are no longer available or supported, except for Azul customers with a specific support contract.
* **Mid-Term Supported (MTS)**
  * Other lesser releases during the three-year gap between LTS versions were defined as MTS.
  * These versions will be supported and updated for an average of three years.
  * This term is now obsolete since a new LTS will be released every two years, so there is no added value anymore in supporting MTS versions.
  * MTS versions are: 13 and 15.
* **Short-Term Supported (STS)** :
  * These are the releases between LTS and MTS and are mainly focused on delivering new and preview features for teams who need them or want to experiment with them.
  * These versions will only be supported and updated for an average of six months by most distributors (but up to 1.5 years by Azul).
  * Versions: all others.

**NOTE:** the support duration is not a "fixed value." Each distributor can freely choose how long to support a version with critical updates and/or backports of new features.

For instance, Oracle dropped support for Java 7 after more than 11 years, but you can still get commercial support from Azul for five more years till 2027.

Which Java Version Should You Use? {#h2-7-which-java-version-should-you-use}
----------------------------------------------------------------------------

Each new version of the OpenJDK brings new and experimental features, including speed and memory improvements.

So it would make sense to upgrade all your runtime environments each time a new version becomes available.

But that would involve testing and upgrading all your servers and/or applications every six months.

In small setups with only a few instances, this is a small job.

But as Java is used in many business environments with tens, hundreds, or thousands of services, such an upgrade requires a lot of planning and effort.

For these use cases, you might stick to a version for a longer time.

There are several criteria for choosing which version:

* **LTS:**the lifetime of this version is known upfront and is guaranteed to get updates for a longer time. The upgrade to the next LTS version can be scheduled well in advance, as the date is already known.
* **Features**: if the development team requires a specific feature only available from a particular version.
* **Experimental**: many developers use the latest version on their work PC. This allows experimenting with new features while being able to build and compile applications with backward compatibility with the runtime used in the production environment.

![](/images/posts/2023/02/should-you-update-java-or-upgrade-and-which-version-should-you-use/option-a-b-c-1024x410.png)

A Real-life Example of the Impact of Upgrading or Updating {#h-a-real-life-example-of-the-impact-of-upgrading-or-updating}
--------------------------------------------------------------------------------------------------------------------------

Upgrading to a newer release (for instance, from one LTS version to another, like 11 to 17) requires a lot of testing to ensure your production environment keeps running as intended.

You can expect performance improvements with each new release; but as the code and runtime keep evolving, you can expect bugs to pop up too.

These upgrade paths and tests need to be entirely handled by your team.

But once you have deployed a particular release, updating to newer versions becomes much more manageable when you follow the CPU/PSU cycle described above.

*** ** * ** ***

***New versions of OpenJDK are released and maintained by Oracle in the first half year.***

***For example, OpenJDK 19 will have its final Oracle-managed release, 19.0.2, in January 2023, after which it can become a community project if a member of the OpenJDK community steps up to become the maintainer for the remaining part of the lifetime.***

***If there are multiple candidates, the community decides who will become the maintainer. I have maintained version 6 of OpenJDK for 4 years until 2020, and version 7 of OpenJDK until 2022.***

***Those were pretty stable versions and only needed some security fixes to be ported in a conservative matter. My colleague Yuri Nesterenko is still the maintainer for OpenJDK 13 and 15 until March 2023.***

Andrew Brygin, Staff Software Engineer, Azul

*** ** * ** ***

Let's take a look at an example based on the [October 2022 Release Notes of Azul Zulu Builds of OpenJDK](https://docs.azul.com/core/zulu-openjdk/release-notes/october-2022).
![CHART: Quarterly release update for Azul Zulu Builds of OpenJDK version 19, 17, 15 13, 11, 8, 7, and 6.](https://www.azul.com/wp-content/uploads/FY23-Q4-Oracle-Compete-Educate-Java-Maintenance-pt2-table.png) *Versions info for Java 11 till 19 in the Azul Zulu Builds of OpenJDK Release Notes of October 2022*

The Shortest Path to Deploying CVE Fixes in Your Environment {#h2-9-the-shortest-path-to-deploying-cve-fixes-in-your-environment}
---------------------------------------------------------------------------------------------------------------------------------

* Your production system should already be running on the Zulu PSU release of July 2022, Zulu PSU 17.36, to include all preceding fixes and updates.
* The release of October contains both a CPU and PSU package:
  * **Zulu CPU 17.37** , based on OpenJDK 17.0.4.1.101
    * Minimal changes to make the runtime safer.
      * For example, for the October 2022 OpenJDK 11.0.16.1.101 update: 14 fixes.
    * Easier to put it into production as a limited test is needed.
    * It needs to be deployed to your production environment as soon as possible after its release.
  * **Zulu PSU 17.38** , based on OpenJDK 17.0.5
    * Contains the same critical changes and other improvements.
      * For example, for the October 2022 OpenJDK 11.0.17 update: 220 fixes.
    * First needs to be tested more in-depth as this contains more changes.
    * It needs to be deployed in between the quarterly release schedule.
* Your system is now running on Zulu PSU 17.38 (OpenJDK 17.0.5) and is ready to handle the same flow with the next quarterly update in January 2023 to move to the next CPU as soon as it becomes available.

Following this approach, you will have the CVE fixes deployed in your environment within the shortest term possible to ensure its security.

*** ** * ** ***

***As the name says, a CRITICAL Patch Update needs to be deployed as fast as possible as it adds security fixes. This is established by providing an easy-to-upgrade version with the minimal required set of changes, limited to several files.***

***Although the small number of changes, creating a new CPU release takes several weeks within Azul! New versions are tested on all supported platforms, meaning up to 100 million test executions are performed on all those platforms combined. The results are handled by a team of more than ten quality engineers. As a result, our customers only need a limited test to put such a CPU update in production, as Azul has done thorough testing.***

***On the other hand, PSU updates can contain hundreds of changes in many files, as these also include both security updates and other improvements. Testing of PSU updates takes two months by the Azul quality team as again, we want to minimize the impact and the number of tests needed on the client side. CPU and PSU have been verified to behave appropriately in most real-life use cases and are*** [***TCK certified***](https://en.wikipedia.org/wiki/Technology_Compatibility_Kit)***where applicable.***

***To give you an idea of the number of versions being tested and distributed: with each new version, we create +500 CPU and +1000 PSU packages which are all included in our quality process!***

Sergey Grinev, QA Manager, Azul

*** ** * ** ***

A complete overview of the support duration of the different versions, also comparing the commercial support offered by Oracle, Eclipse Temurin, and Azul is available in [the support roadmap of Azul](https://www.azul.com/products/azul-support-roadmap/).

Conclusion {#h2-10-conclusion}
------------------------------

Keeping your Java environment stable requires discerning which updates to install and then installing them appropriately.

With multiple LTS versions available, and perhaps not the resources or the need to stay on the latest version, you need to make decisions about which version to be on for the greatest benefit of your organization.

To stay on top of the latest Java improvements and security fixes, it is vital to keep your OpenJDK up-to-date!

Luckily Azul provides new versions of many different versions within hours after new CVEs have been reported each quarter.

Check the [Azul download page](https://www.azul.com/downloads/?package=jdk) for all the available versions for all platforms.
