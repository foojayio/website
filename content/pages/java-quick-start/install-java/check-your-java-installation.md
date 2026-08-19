---
title: "Check your Java installation"
description: "Find out whether Java is already installed and which version you have, using java -version in a terminal on Windows, macOS or Linux."
url: "/java-quick-start/install-java/check-your-java-installation/"
aliases:
  - "/java-quick-start/install-java/check-your-java-installation/"
frozen: false
---

Before we dive into Java coding, we need to make sure we have a recent Java version installed.

**Tip:** **Any Java version 11 or higher will be fine for getting started with Java today.**

## Step 1: Open a Terminal Window

* On Windows, click Start, type `cmd`, and hit Enter.
* On Mac OS X, go to Launchpad, and search for "Terminal".
* On Linux, go to Applications and search for "Terminal".

## Step 2: In the Terminal, enter the command `java -version`

You should get output similar to the below.

```bash
java -version

openjdk version "25" 2025-09-16 LTS
OpenJDK Runtime Environment Zulu25.28+85-CA (build 25+36-LTS)
OpenJDK 64-Bit Server VM Zulu25.28+85-CA (build 25+36-LTS, mixed mode, sharing)
```

Make sure that the version shown above in your Terminal is a recent version of Java, like 25 or higher.

In this case, you have OpenJDK installed and you're all set to run and write Java code.

## Step 3: Install OpenJDK if you don't have it installed yet

If you get any of the following results, instead of the above, you need to install OpenJDK.

```bash
'java' not recognized as an internal or external command, operable program or batch file.

command not found: java

Command 'java' not found, did you mean...
```

Go to one of the following pages for details on installing OpenJDK:

* [Install Java (Windows)](https://foojay.io/java-quick-start/install-java/install-java-on-windows/)
* [Install Java (Mac OS X)](https://foojay.io/java-quick-start/install-java/install-java-on-macos/)
* [Install Java (Linux)](https://foojay.io/java-quick-start/install-java/install-java-on-linux/)
