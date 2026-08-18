---
title: "Best Practices for Managing Java Dependencies"
slug: "best-practices-for-managing-java-dependencies"
date: "2022-09-15T08:21:10+00:00"
lastmod: "2022-09-15T08:22:01+00:00"
description: "Knowing how to select, update, and remove Java dependencies from our application is essential for security."
canonical: "https://snyk.io/blog/best-practices-for-managing-java-dependencies/"
authors:
  - "bmvermeer"
image: "blog-java-depen-snyk-vuln-report-1240x1036-1.jpeg"
categories:
  - "DevOps"
  - "Security"
  - "Snyk"
  - "Tutorials"
tags:
related_posts:
  - "building-secure-ci-cd-pipelines-with-github-actions-for-your-java-application"
  - "exploring-cve-2022-33980-the-apache-commons-configuration-rce-vulnerability"
  - "new-java-17-features-for-improved-security-and-serialization"
frozen: false
---

Creating Java applications is great, and many resources are available.

To speed up development, many folks use frameworks and libraries that do some of the heavy lifting.

When looking at modern Java applications, almost all of them contain dependencies from libraries developed by someone else.

Dependencies take up about 80 to 90 percent of the binary --- so, we should take good care of them when creating a Java project.

In this article, I'll give you some advice and best practices for dealing with Java dependencies in your project.

* Why be more aware of your Java dependencies
* Managing Java dependencies
* Including dependencies in your Java projects
* Update your Java dependencies
* Removing Java dependencies from your project

## Why be more aware of your Java dependencies

When it comes to managing code contributions, we generally turn to a process like [code reviews](https://snyk.io/learn/code-review/) for a first-pass quality assurance measure, before merging new code into our main branch. Check out our guide to [Java code review tools](https://snyk.io/learn/code-review/java-tools/) to learn more. Practicing pair programming is another way to cover this quality control process.

However, how we treat our dependencies differs greatly from how we treat our own code. On many occasions, dependencies are used without any form of validation. And on many occasions, those top-level dependencies pull in transitive dependencies, which can go multiple levels deep. For example, a 200 line Spring application with five direct dependencies can end up using 60 dependencies in total, which amounts to almost half a million lines of code being shipped to production.

Updating Java dependencies in legacy projects can be challenging. If they're outdated, you'll end up with a domino effect of compatibility issues, and updating a single library might mean updating several because of a [bug or security issue](https://snyk.io/learn/security-vulnerability-exploits-threats/). If these Java dependencies change their API, you would need to rewrite your application in full.

Additionally, in many major enterprise applications, dependencies stay in the manifest file, even if they're no longer used in the code. These unused dependencies are still available in your program.

All of this can lead to:

* Larger binaries that use more resources or start-up time
* Possible collision in libraries when adding new dependencies.
* Outdated libraries that contain bugs or security issues
* Compatibility problems when updating libraries
* And more

## Managing Java dependencies

One of the best practices for significantly using repositories, like Maven Central, is to set up your own repositories manager. This is a dedicated proxy server between your internal development and the public repositories --- which will not only gives you faster and more stable builds, but also allows you to set up [policies](https://snyk.io/series/open-source-security/open-source-policy/) for Java packages. You can, for instance, block certain versions so they cannot be downloaded and used in your applications.

For more information about repository managers and a list of possible products, take a look at the [Maven documentation](https://maven.apache.org/repository-management.html).

## Including new dependencies in your Java project

When you need to solve a problem, and there is a library available, you'll likely want to include it in your Java dependency manifest files.

However, before including, you should consider:

### 1. Does it solve the problem?

The main reason for importing a package is to solve your problem. The question is whether the dependency you chose can do this. Also, does it solve the whole problem without introducing new challenges? If this isn't the case, there might be better solutions out there.

### 2. Do I need the (whole) package?

Is it worth importing a large dependency, with many functions and data types, if you only need a single function? Sometimes, it might be easier and more manageable to write that function yourself. For instance, does it make sense to include entire Eclipse Collections if I only want to use the Tuple data type? Probably not.

A quick look at [mvnrepository.com](https://mvnrepository.com/) shows me that this package is about 10MB

![](https://snyk.io/wp-content/uploads/blog-java-depen-eclipse-main-library.jpg)

Also, check to see if the dependencies you already have can do the job for you. Some similar functions or data types might already be available. On the other hand, including a new, extensive library can help you solve multiple problems at once --- it all depends on the situation.

### 3. How many contributors are there?

We have a pretty low bus factor if the Java dependency you use has only one or just a few maintainers. What happens if the maintainer decides to quit, or doesn't have time to fix a bug? Alternatively, you might also choose to contribute to the project yourself --- making it more secure for everyone involved.

But, before including any dependencies in your project, be sure to check the core repository and see how many active maintainers there are.

![](https://snyk.io/wp-content/uploads/blog-java-depen-github-contributors-2048x214.png)

### 4. Is it still maintained?

If a package is no longer maintained you definitely do not want to rely on it. Before integrating a package, check if there are new pushes on the GitHub repository, and take a look at the release cycle of a package. This will give you an idea of how well maintained the package is.

![](https://snyk.io/wp-content/uploads/blog-java-depen-2-hrs.jpg)

### 5. What is the latest version of the package?

Code examples can give you great insight on a particular Java dependency. However, these examples might be outdated, and the intended package might already be updated. Consider using the latest stable version. For Eclipse Collections, we see that the latest stable release is 11.1.0 from July 5, 2022 on mvnpackage.com. Consider using that version.

Note that the 11.1.0.M2 is also listed in this image. This is clearly a pre-release version. You should only include stable release versions in your production application unless you are absolutely sure. As a rule of thumb, please do not include versions that have a qualifier, like:

* alpha or a
* beta or b
* milestone or m
* rc or cr
* snapshot

If a Java dependency has the qualifier GA or final, you can generally consider it a stable release version.

![](https://snyk.io/wp-content/uploads/blog-java-depen-central-1240x615.jpg)

### 6. Are there any security vulnerabilities?

Before you actively depend on a Java package, make sure you [scan it for known vulnerabilities](https://snyk.io/learn/vulnerability-scanner/).

The Snyk CLI is a great tool for scanning your Maven or Gradle file.

If your library contains a security vulnerability, you might want to pick another package to depend on.

## Updating your Java dependencies

### 1. Are there newer versions available?

You don't want to manually check every Java dependency you have to see if newer versions are available. Luckily, there are easier ways to do this.

By using plugins in your package manager you can automatically verify your dependencies as often as you'd like --- in every build, for example.

Be aware that your tools might point you to beta or pre-release versions. It is highly advised that you only use stable release versions of a library.

**Maven example**   

In Maven, you can use the versions plugin like below. No need to put anything specific in your pom.xml

    mvn versions:display-dependency-updates

![](https://snyk.io/wp-content/uploads/blog-java-depen-maven-plugin.jpg)

**Gradle example**   

For Gradle, we have to include a plugin like the versions plugin from ben-manes.

    plugins {
      id "com.github.ben-manes.versions" version "0.42.0"
    }

Now we can run a similar command to display newer versions of your libraries.

    gradle dependencyUpdates -Drevision=release

![](https://snyk.io/wp-content/uploads/blog-java-depen-project-dep-update.jpg)

**IntelliJ IDEA**

If you are using IntelliJ IDEA, then the newer versions will underline the dependencies that can be updated. This works for both Maven and Gradle projects.

![](https://snyk.io/wp-content/uploads/blog-java-depen-group-id-pkg.jpg)

**Snyk**

When connecting your GitHub repository to your Snyk account, we can provide you with recommended fixes or updates at every pull request. This, in addition to our comprehensive security advice, can help you keep your Java dependencies up to date.

![](https://snyk.io/wp-content/uploads/blog-java-depen-snyk-ui-1240x804.jpg)

### 2. Are the packages you use still maintained?

It is wise to revisit the GitHub repo, or mvnpackage.com, to see if there are recent updates and commits. If it looks like a package is no longer well maintained, you can choose to maintain it yourself or migrate to another, better updated, library.

However, if you encounter a problem with a dependency that's critical to your application, consider fixing the problem yourself and contributing that fix to the open-source project. This will be greatly appreciated --- and often quicker than submitting issue reports and pushing the maintainer to fix the problem.

### 3. Are there security issues with my Java dependencies?

Even if your application is free from vulnerabilities now, it doesn't mean it will stay that way forever. New vulnerabilities and exploits are discovered and disclosed on a daily bases. This means that you need to rescan your libraries regularly to ensure they remain vulnerability free.

Snyk provides you multiple ways to integrate dependency scanning in your [development lifecycle](https://snyk.io/learn/secure-sdlc/). On your local machine you can use the [Snyk CLI](https://snyk.io/blog/snyk-cli-cheat-sheet/), or the integrations for IntelliJ, Eclipse, or VS Code to scan for vulnerabilities.

You can also scan during your build cycle with the Maven and Gradle (unofficial) plugin, or choose one of the CI pipeline integrations.

Alternatively, you can add your Git repository to Snyk, so we can scan and update your projects for you on a daily basis.

![](https://snyk.io/wp-content/uploads/blog-java-depen-snyk-vuln-report-1240x1036.jpg)

## Removing Java dependencies from your project

### 1. Is the package still in use?

If a Java dependency is not used anymore, we should remove it from our manifest file. Every package in that file is part of your binary and available on the classpath. Removing unused dependencies will make your binary smaller --- and lead to faster startup and download times in addition to better security. Minimizing the dependencies on your classpath is critical to protecting against attacks like a [deserialization gadget chain](https://snyk.io/blog/serialization-and-deserialization-in-java/).

A clean desk policy --- or in this case a clean app policy --- is always highly advisable. Luckily, your package managers can help you with identifying these unused Java dependencies.

**Maven example**

For Maven I can use the[dependency plugin](https://maven.apache.org/plugins/maven-dependency-plugin/analyze-mojo.html) to analyze my dependencies. This plugin checks if the Java dependencies I declare are also used in my code.

In this case, I do not want to get bothered by provided or test dependencies, so I use the ignoreNonCompile flag.

    mvn dependency:analyze -DignoreNonCompile

![](https://snyk.io/wp-content/uploads/blog-java-depen-warning-unused.jpg)

**Gradle example**

In Gradle we need to add another plugin to analyze dependencies. In this case, we'll use the nebula.lint plugin. This Gradle linter can analyze the java dependencies you included and see if there are unused dependencies.

    plugins {
       id "nebula.lint" version "17.7.0"
    }

I have to configure the plugin accordingly to set the `gradleLint.rules`. You can do this in your Gradle file, or as a command line parameter. In the example below, I choose the latter. Check the plugin [documentation](https://github.com/nebula-plugins/gradle-lint-plugin/wiki) for more information on how to configure this plugin for your application.

    gradle lintGradle -PgradleLint.rules=unused-dependency

![](https://snyk.io/wp-content/uploads/blog-java-depen-lint-violation.jpg)

## Create a solid dependency management strategy for your Java applications

When developing Java applications and using dependencies like libraries or frameworks, it's wise to create a strategy for how to handle them.

Knowing how to select, update, and remove Java dependencies from our application is essential for security.

By creating a clear strategy, we prevent surprises when a high priority security issue requires us to update a package.

Check out [this article to learn more about managing open source dependencies](https://snyk.io/series/open-source-security/software-dependencies/).

## Secure your dependencies for free

Create a Snyk account today for effortless scanning and secure dependencies.

[Sign up for free](https://app.snyk.io/login?cta=sign-up&loc=body&page=best-practices-for-managing-java-dependencies)
