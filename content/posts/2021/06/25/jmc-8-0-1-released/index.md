---
title: "Announcement: JMC 8.0.1 Has Been Released!"
slug: "jmc-8-0-1-released"
date: "2021-06-25T18:10:26+00:00"
lastmod: "2021-06-25T18:19:57+00:00"
description: "The 8.0.1-ga tag was just set in the jmc8 repository on GitHub, a patch update release, and will therefore not include any new features."
authors:
  - "hirt"
image: "Favicon-3-2.png"
categories:
  - "JDK Flight Recorder"
  - "Release Notes"
  - "Tools"
tags:
related_posts:
  - "changes-included-in-release-24-08-of-azul-zing-builds-of-openjdk"
  - "indexing-all-of-wikipedia-on-a-laptop"
  - "new-jdkmonitor"
  - "javafinder-keeping-track-of-java-inventories"
frozen: false
---

The [8.0.1-ga](https://github.com/openjdk/jmc/releases/tag/8.0.1-ga "8.0.1-ga") tag was just set in the jmc8 repository on GitHub. This is a patch update release, and will therefore not include any new features.

The 8.0.1 release contains the following fixes:

| Jira Issue |                                             Summary                                             |
|------------|-------------------------------------------------------------------------------------------------|
| 7188       | JMC fails to dump file and gets stuck when flightrecording is attempted on jmxremote connection |
| 7172       | Fix spell mistake in secure store class                                                         |
| 6920       | UI improvements                                                                                 |
| 6336       | Remove Triple DES Cipher in Secure store                                                        |
| 6398       | Better JNDI Usage                                                                               |

It is up to individual vendors to release binaries of JMC 8.0.1, and some vendors already have binary builds available, for example [Oracle](https://jdk.java.net/jmc/8/ "Oracle").

The next upcoming source release is JMC 8.1.0, which *will* contain new features and enhancements. The planned source release date for JMC 8.1.0 is the 2nd of August 2021.
