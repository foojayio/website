---
title: "Secure CI/CD Pipelines with GitHub Actions for Your Java Apps"
slug: "building-secure-ci-cd-pipelines-with-github-actions-for-your-java-application"
date: "2022-08-11T06:28:38+00:00"
lastmod: "2022-08-11T06:29:14+00:00"
description: "Learn how to integrate Snyk into your GitHub CI/CD to automate security scanning as part of your build cycle prior to production."
canonical: "https://snyk.io/blog/building-a-secure-pipeline-with-github-actions/"
authors:
  - "bmvermeer"
image: "blog-github-actions-pipeline-1-1240x415-1.jpeg"
categories:
  - "Developer Tools"
  - "DevOps"
  - "Security"
  - "Snyk"
tags:
related_posts:
  - "github-actions-with-java-part-1"
  - "github-actions-with-java-part-2"
  - "lights-camera-action-github-actions-with-java-part-3"
enlighterjs: true
frozen: false
---

This article was originally post at[Snyk.io](https://snyk.io/blog/building-a-secure-pipeline-with-github-actions/) and is used with permission

GitHub Actions has made it easier than ever to build a secure continuous integration and continuous delivery (CI/CD) pipeline for your GitHub projects.

By integrating your CI/CD pipeline and GitHub repository, GitHub Actions allows you to automate your build, test, and deployment pipeline.

You can create workflows that build and test every pull request to your repository or deploy merged pull requests to production.

And by integrating Snyk into your GitHub CI/CD, you can automate security scanning as part of your build cycle prior to production.

Setting up a Github Actions CI Pipeline for Java
------------------------------------------------

GitHub Actions workflows are YAML files in the `.github/workflows` folder. If you do not have a workflow, or want to add a new one, select **Actions** and **New workflow**.
![](https://snyk.io/wp-content/uploads/blog-github-actions-workflows-1240x274.jpg)

For this Java Spring-Boot project, I created a CI pipeline using maven as follows:

```
name: Java CI with Maven

on:
 push:
   branches: [ master ]
 pull_request:
   branches: [ master ]
 workflow_dispatch:

jobs:
 build:

   runs-on: ubuntu-latest

   steps:
   - uses: actions/checkout@v2
   - name: Set up JDK 17
     uses: actions/setup-java@v1
     with:
       java-version: 17
   - name: Build with Maven
     run: mvn -B package --file pom.xml
```


This action will run on every push or pull request on the master branch. It is based on ubuntu and checks out the repository, while using the [setup-java](https://github.com/actions/setup-java) GitHub Action --- with Java 17 and Maven --- to build the Java jar file.

If you're familiar with the syntax, this workflow is relatively straightforward, but you can refer to the [GitHub Actions documentation](https://docs.github.com/en/actions) for a complete overview of the possibilities.

Integrating Snyk in your Github CI/CD
-------------------------------------

With Snyk, you can integrate security testing for your new Java project. There are two primary methods for doing this. However, let's make sure two things are set up first.

Begin by logging into your Snyk account, [creating one for free](https://app.snyk.io/login) if you haven't already. Then set your API key as a secret `SNYK_TOKEN` for your GitHub repository.

### OPTION 1: INTEGRATE THE SNYK CLI IN A BUILD STEP OF YOUR GITHUB ACTION

You can use the Snyk CLI to automatically run security scans inside your current build.

Follow the steps described below --- including the few extra steps after`Build with Maven` --- to get started.

```
 - name: Set up Node 14
   uses: actions/setup-node@v3
   with:
     node-version: 14
 - name: install Snyk CLI
   run: npm install -g snyk
 - name: run Snyk Open Source Test
   run: snyk test
 - name: run Snyk Code Test
   run: snyk code test
```


Set up NodeJS version 14 and download the Snyk CLI using npm. Next, analyze your dependencies with Snyk Open Source, and use Snyk Code to scan your custom code for vulnerabilities.

Then, declare `SNYK_TOKEN` as the environment variable containing your API key. You can refer to the secret `SNYK_TOKEN` you set up earlier, or feel free to reuse the full code example of the GitHub Action.

```
env:
 SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
```


The benefit of this approach is that you only have to compile and build your application once, which can save a lot of time when building large applications. The downside is the steps are used in series, and might not be efficient if one of the later steps fails.

### OPTION 2: USE THE PREDEFINED GITHUB ACTIONS FROM SNYK TO CREATE YOUR CI/CD PIPELINE

Snyk created a set of GitHub Actions to check for vulnerabilities in your projects. The required action depends on your language or build tool. For example, we'll use the [maven-based GitHub Action](https://github.com/snyk/actions/tree/master/maven) shown below for our Java project.

The predefined actions ensure you have the correct prerequisites to build your application and include the latest CLI version to scan your code. Even though you may rebuild your applications several times, these actions can run individually and in parallel --- a considerable advantage of this approach..

Let's start with scanning our dependencies. We already have a Snyk account, and our API key is stored as a secret called `SNYK_TOKEN`. So, instead of creating an extra step in the build job, create a new job called `opensource-security`.

```
opensource-security:
   runs-on: ubuntu-latest
   steps:
     - uses: actions/checkout@master
     - name: Run Snyk to check for vulnerabilities
       uses: snyk/actions/maven@master
       env:
         SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
```


According to the documentation, the default command is `test`. Without any additional configuration, it will scan your application for known vulnerabilities in your dependencies. If you want to set a specific CLI argument like `--all-projects` to accommodate nested projects, use the `with` keyword and set the property to `args`.
![](https://snyk.io/wp-content/uploads/blog-github-actions-properties-1240x278.jpg)

While Snyk scans our dependencies, let's check the Java code for vulnerabilities in our GitHub Action. To do this, repeat the previous process by setting up a third job specifically for that, and set the `command` property to `code test`.

```
 code-security:
   runs-on: ubuntu-latest
   steps:
     - uses: actions/checkout@master
     - name: Run Snyk to check for vulnerabilities
       uses: snyk/actions/maven@master
       env:
         SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
       with:
         command: code test
```


Notice that all three jobs will run in parallel, which is, in many cases, more efficient.

Adding secure continuous delivery to your GitHub CI/CD
------------------------------------------------------

Now, let's discuss the deployment --- or delivery --- part of your GitHub CI/CD. After building and testing are completed, it's time to publish the package to GitHub. To establish this, we'll use the Maven release plugin in our Java project

Since we only want to release the package when the build job and both security jobs are successfully finished, we'll add a `needs` property with a list of prerequisite jobs for release. This way we can ensure that our package isn't deployed before it's fully built and secure.

```
release:
   needs: [opensource-security, code-security, build]
   runs-on: ubuntu-latest
   steps:
     - uses: actions/checkout@v2
     - name: Set up JDK 17
       uses: actions/setup-java@v1
       with:
         java-version: 17
     - name: Set Git user
       run: |
         git config user.email "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="214649404255484e4f5261435348404f5744534c4444530f4f4d">[email protected]</a>"
         git config user.name "GitHub Actions"
     - name: Publish JAR
       run: mvn -B release:prepare release:perform -DskipTests
       env:
         GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
```


The maven commands [release:prepare](https://maven.apache.org/maven-release/maven-release-plugin/examples/prepare-release.html) and [release:perform](https://maven.apache.org/maven-release/maven-release-plugin/examples/perform-release.html) verify that the release has the right version number and actually gets published to our GitHub repository. Since we're counting on Maven to handle this, make the following configurations to your pom.xml file.

```
  <scm>
       <developerConnection>scm:git:https://github.com/bmvermeer/ghaction-example.git</developerConnection>
       <tag>HEAD</tag>
   </scm>
   <distributionManagement>
       <repository>
           <id>github</id>
           <name>GitHub</name>
           <url>https://maven.pkg.github.com/bmvermeer/ghaction-example</url>
       </repository>
   </distributionManagement>
```


Though our newly released application is currently free from vulnerabilities, that doesn't mean it'll remain secure forever. New vulnerabilities can show up in a variety of places, making it vital to continually monitor your code and dependencies after an application is deployed.

In addition to scanning during development, we can use Snyk to monitor dependencies post deployment as well. When the release is complete, use the predefined Snyk action once again, but this time set the `command` property to `monitor`.

```
 opensource-monitor:
   needs: [release]
   runs-on: ubuntu-latest
   steps:
     - uses: actions/checkout@master
     - name: Run Snyk to check for vulnerabilities
       uses: snyk/actions/maven@master
       env:
         SNYK_TOKEN: ${{ secrets.SNYK_TOKEN }}
       with:
         command: monitor
```


This will send the dependency tree, which is static after release, over to Snyk for monitoring. We can now view our project in the Snyk UI, and get automatic notifications if a new vulnerability is found for any of the dependencies we're using --- and ensure that our published Java project remains vulnerability free.
![](https://snyk.io/wp-content/uploads/blog-github-actions-snyk-ui-1240x149.jpg)

Integrating security in your GitHub Actions
-------------------------------------------

With GitHub Actions, creating a CI/CD pipeline for your GitHub project is quite straightforward. And with the Snyk actions, you can easily integrate security scanning on multiple levels for all applications.  

GitHub visualizes the pipeline we created today with the following image. With some jobs running in parallel, while others depend on the successful completion of other jobs. You can check out the entire example project in [this GitHub repository](https://github.com/bmvermeer/ghaction-example).
![](https://snyk.io/wp-content/uploads/blog-github-actions-pipeline-1-1240x415.jpg)

Today, we used Snyk Code and Snyk Open Source to scan the Java source code and pipeline dependencies. However, you can expand this by scanning Docker files with Snyk Container and Kubernetes files with Snyk IaC.

With the [Snyk GitHub Actions](https://github.com/snyk/actions), you can automatically add scanning to your GitHub Actions workflow and mix and match the actions to fit your project best, ensuring code security now and in the future.
