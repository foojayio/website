---
title: "Getting Started with Snyk for Secure Java Development | Foojay Today"
slug: "getting-started-with-snyk-for-secure-java-development"
date: "2021-07-29T06:39:02+00:00"
lastmod: "2021-07-29T06:39:05+00:00"
description: "Learn how to begin with Snyk for secure Java development so that you too can be more secure from the get-go!"
canonical: "https://snyk.io/blog/snyk-for-secure-java-development/"
authors:
  - "bmvermeer"
image: "https://res.cloudinary.com/snyk/images/w_1240,h_221,c_scale/f_auto,q_auto/v1/wordpress-sync/blog-secure-java-development-repo/blog-secure-java-development-repo-1240x221.png?_i=AA"
categories:
  - "Security"
  - "Snyk"
  - "Tutorials"
tags:
related_posts:
frozen: false
---

If you're a Java developer who wants to develop your applications more securely, you've come to the right place. Snyk can help you with that mission.

This article will explain how to begin with Snyk for [secure Java development](https://snyk.io/blog/10-java-security-best-practices/) so you can be more secure from the get-go.

If you're new to Snyk, it's important to know that we offer a variety of developer-focused products and tools. Some of these tools, like our CLI and some IDE integration, support multiple products. However, we can distinguish four different products within Snyk:

1. [Snyk Open Source](https://snyk.io/product/open-source-security-management/) -- Securing your open source dependencies
2. [Snyk Container](https://snyk.io/product/container-vulnerability-management/) -- Securing your container images
3. [Snyk Infrastructure as Code (Snyk Iac)](https://snyk.io/product/infrastructure-as-code-security/) -- Securing your infrastructure as code
4. [Snyk Code](https://snyk.io/product/snyk-code/) -- Securing your custom code. In this post, we'll focus only on Java.

To get started with these tools, you'll need to sign up for a free Snyk account. For solo developers, our [Free plan](https://www.snyk.io/plans) is usually more than enough for your needs, so keep your credit card in your wallet.

After you have signed up, there are multiple ways to engage with Snyk for Java. I will explain this tool by tool below.

Snyk CLI {#h2-0-snyk-cli}
-------------------------

The [Snyk CLI](https://support.snyk.io/hc/en-us/articles/360003812458-Getting-started-with-the-CLI) is the most accessible tool to start with Snyk for Java. You can install it in multiple ways using npm or brew, for instance:

    `npm install -g snyk`

This tool is great for your local machine and can also be a super useful tool for your [CI pipeline](https://snyk.io/learn/what-is-ci-cd-pipeline-and-tools-explained/). The first thing you need to do is authenticate the snyk CLI by either setting your API token as an environment variable (recommended for CI systems) or calling `snyk auth`.

Testing your Java project for [security issues](https://snyk.io/learn/security-vulnerability-exploits-threats/) in your open source dependencies is as easy as calling `snyk test` for the root of your project. Depending on the build system you use for Java, make sure that either Maven or Gradle are installed and available. The Snyk CLI uses your package manager and pom.xml or build.gradle to get the entire dependency tree. Please ensure that your project is built using Maven or Gradle before calling a `snyk test` to prevent unexpected results.

When your project contains multiple manifest files, like multiple pom files. Use the `--all-projects` flag to scan all your projects. This also works if you have for instance, a Gradle Java project combined with a JavaScript frontend using npm. Lastly, use the `--help` flag to find specific settings for either Maven or Gradle.

Check out the [CLI cheat sheet](https://snyk.io/blog/snyk-cli-cheat-sheet/) for more tips and tricks on the Snyk CLI. You can use the same CLI to scan your containers for security issues and your infrastructure as code (IaC). In addition, we will soon be releasing Snyk Code for the CLI, which helps to prevent security vulnerabilities in your custom Java code.

Connecting your Java project's Git repository {#h2-1-connecting-your-java-project-s-git-repository}
---------------------------------------------------------------------------------------------------

Next, you can connect your Git repository. If you connect your Java repository, for example Github, Snyk automatically searches for your Maven or Gradle manifest file and scans your dependencies. By default, this action will be repeated daily, and you will get notified whenever a new security vulnerability or a new fix is found in your repository.  

The example below shows a pom file from a demo project that contains known security vulnerabilities. Every time Snyk finds a new issue, it will be visible in your dashboard, explaining the vulnerability and possible remediation advice.  

In addition, the get integration can also scan Dockerfiles and perform Snyk Code analyses. If you want more information about the Snyk Code analyses for Java applications with the Snyk Git integration, check out this [Solving Java security issues in my Spring MVC application](https://snyk.io/blog/java-code-analysis-solving-java-security-issues-in-spring-mvc-app/) blog post.
![Develop secure Java code with Snyk](https://res.cloudinary.com/snyk/images/w_1240,h_221,c_scale/f_auto,q_auto/v1/wordpress-sync/blog-secure-java-development-repo/blog-secure-java-development-repo-1240x221.png?_i=AA)

Maven and Gradle plugins {#h2-2-maven-and-gradle-plugins}
---------------------------------------------------------

Some people prefer to automate their security testing during their builds. Naturally, you can do this inside a CI pipeline. However, it might make more sense to do this in your build tool. If you use either Maven or Gradle, this is the point where your dependencies get pulled in, and where the artifact is created. Why not automate security scanning in the same way as we do with unit testing? Snyk provides both a Maven and Gradle plugin. The only thing you need is an API key that you can get from your free account.

Now you either call a snyk test from both Maven and Gradle manually or connect to a specific task when building your application. Check out the dedicated articles on the Maven plugin and the Gradle plugin for Snyk.

* [Snyk Maven plugin: Integrated security vulnerability scanning for developers](https://snyk.io/blog/snyk-maven-plugin-integrated-security-vulnerability-scanning-for-developers/)
* [Gradle dependencies: scanning with new Snyk Gradle plugin](https://snyk.io/blog/gradle-plugin-by-snyk-gradle-dependencies-scanning/)

Snyk Java IDE plugins {#h2-3-snyk-java-ide-plugins}
---------------------------------------------------

Some developers are more comfortable doing everything from their IDE. To some extent, the IDE nowadays is like a swiss army knife with a tremendous amount of options and extensibility. Some developers practically live in their IDE.  

Recently, in the [JVM Ecosystem report 2021](https://snyk.io/jvm-ecosystem-report-2021/) we found out that IntelliJ IDEA, Eclipse, and VS Code are the top three IDEs for Java development. For all three of these, [Snyk has IDE plugins](https://snyk.io/ide-plugins) available.

Check out the following articles for more information on the specific plugins.

* [How to fix Java security issues while coding in IntelliJ IDEA](https://snyk.io/blog/how-to-fix-java-security-issues-while-coding-in-intellij-idea/)
* [Fix open source vulnerabilities directly from your Eclipse IDE](https://snyk.io/blog/fix-open-source-vulnerabilities-directly-from-your-eclipse-ide/)
* [Visual Studio Code extension for Snyk Code](https://support.snyk.io/hc/en-us/articles/360018585717-Visual-Studio-Code-extension-for-Snyk-Code-)

It is easy to start with Snyk {#h2-4-it-is-easy-to-start-with-snyk}
-------------------------------------------------------------------

As you can see, starting with Snyk and creating secure Java applications doesn't have to be hard.

Pick and choose the options that fit your way of working so that you can develop fast and stay secure.

Also know that we have a ton of integrations for [different CI/CD pipelines](https://support.snyk.io/hc/en-us/articles/360004002258-Configure-your-Continuous-Integration) if that suits you better.

In any case, Snyk offers everything a Java developer needs to build securely from the start!
