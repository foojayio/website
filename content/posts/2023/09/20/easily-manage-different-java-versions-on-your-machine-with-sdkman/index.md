---
title: "Manage Different Java Versions on Your Machine with SDKMan!"
slug: "easily-manage-different-java-versions-on-your-machine-with-sdkman"
date: "2023-09-20T07:05:16+00:00"
lastmod: "2023-09-20T07:05:17+00:00"
description: "SDKMan! stands for Software Development Kit Manager, a shell script that allows you to manage parallel versions of multiple Software Development Kits for JVM-based languages."
canonical: "https://blog.payara.fish/easily-manage-different-java-versions-on-your-machine-with-sdkman"
authors:
  - "jadon-ortlepp"
  - "luqman-saeed"
image: "sdkman.png"
categories:
  - "Java"
  - "Java Beginner"
  - "Tools"
  - "Tutorials"
  - "Uncategorized"
tags:
related_posts:
  - "installing-java-with-sdkman-on-raspberry-pi"
  - "video-sdkman-explained"
  - "disco-api-helping-you-to-find-any-openjdk-distribution"
  - "boxlang-aws-azure-and-google-secrets-manager-module-released"
enlighterjs: true
frozen: false
---

**So, you're a Java developer, or maybe you're aspiring to be one. Either way, you've probably faced the challenge of managing multiple versions of Java on your machine. One project requires Java 8, but another needs Java 11, yet another requires Java 17. The open-source library you're keen on contributing to needs yet another version. What do you do? You start juggling environment variables, and before you know it, your system is a tangled mess of configurations. Not fun, right?**   

And let's not even get started on the difference between JRE and JDK. It's easy for beginners to get confused about the distinction between the two. The JRE (Java Runtime Environment) is sufficient if you just want to run Java applications, but if you're going to be developing them, you'll need the JDK (Java Development Kit). The JDK includes everything the JRE has, plus additional tools and utilities for developers like the Java compiler, or javac.  

Tired of all this complexity? Let me introduce you to SDKMan!, a version manager that streamlines the process, making it a breeze to manage multiple Java versions on your machine. Not just Java, SDKMan! can be used to manage a lot more kits and tools such as Maven. In this blog post however, we see how to use SDKMan! to effortlessly manage different versions of Java on the same machine.

What is SDKMan? {#h2-0-what-is-sdkman}
--------------------------------------

[SDKMan!](https://sdkman.io/) stands for Software Development Kit Manager, a shell script that allows you to manage parallel versions of multiple Software Development Kits for JVM-based languages like Java, Groovy, and Kotlin.

With a simple command, you can switch between different versions, set a default version, and do much more.

Installing SDKMan! {#h2-1-installing-sdkman}
--------------------------------------------

To get started, you'll first need to install SDKMan! on your system, with the following command:

`curl -s "https://get.sdkman.io" | bash`

After the installation is complete, open a new terminal window and type the following to ensure that SDKMan! has been installed:

`sdk version`

You should see the SDKMan! version displayed, confirming that the installation was successful.

Managing Java Versions {#h2-2-managing-java-versions}
-----------------------------------------------------

To see all the versions available for installation, including those you have already installed on your machine, you can run:

`sdk list java`

To install a specific version, you can use:

`sdk install java [version]`

For example, to install the Java 21 from OpenJDK, you can run:

`sdk install java 21.ea.35-open`

Switching between different versions is as simple as running:

`sdk use java [version]`

To set a particular version as your default:

`sdk default java [version]`

So for instance, to set the aforementioned Java 21 as the default JDK on your machine, you can run:

`sdk default java 21.ea.35-open`

You can do all this without messing around with environment variables or system settings. It's that simple.

You can also see the list of all available tools, SDKs and libraries you can install with SDKMan! with the command:sdk list

Wrapping Up {#h2-3-wrapping-up}
-------------------------------

Managing multiple Java versions doesn't have to be a headache. SDKMan! has you covered, offering a straightforward way to handle different versions without the hassle of juggling configurations manually. With just a few commands, you can install, switch between, or even uninstall different versions of Java (JDK or JRE, it doesn't matter!).  

So, the next time you find yourself wrestling with Java versions, just remember: SDKMan! is here to make your life a whole lot easier.  

Happy coding!
