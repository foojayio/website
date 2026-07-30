---
title: "Lights, Camera, Action: GitHub Actions with Java (Part 2) | Foojay.io Today"
slug: "github-actions-with-java-part-2"
date: "2021-11-15T14:35:43+00:00"
lastmod: "2021-11-15T16:17:13+00:00"
description: "When picking a build distribution with the current version of GitHub Actions setup-java@v2 you really only have two choices: zulu or temurin."
authors:
  - "carldea"
image: "https://foojay.io/wp-content/uploads/2021/11/distro-choice.jpg"
categories:
  - "DevOps"
  - "Microservices"
tags:
related_posts:
enlighterjs: true
frozen: false
---

<figure class="alignright is-resized">
 <img fetchpriority="high" decoding="async" src="/images/posts/2021/11/github-actions-with-java-part-2/distro-choice.jpg" alt="Distro Choice 337" width="308" height="465">
</figure>

Introduction {#h2-0-introduction}
---------------------------------

Have you ever been given too many choices resulting in the feeling of indifference ("not a big deal, doesn't really matter either way")? However, when given **two** choices, somehow you feel torn or become more critical of one versus the other.

In this short overview, you will learn how to create a GitHub Action workflow that allows you to choose a particular OpenJDK build distribution.

And welcome back to the series of articles on GitHub Actions with Java **Part 2** . If you didn't catch **Part 1** head over here: [https://foojay.io/today/github-actions-with-java-part-1](https://foojay.io/today/github-actions-with-java-part-1/).

To follow along, go to the GitHub project here: <https://github.com/carldea/HelloWorldGHActions>

If you remember from in **Part 1** , we discussed how to manually create a GitHub Action **[Workflow](https://docs.github.com/en/actions/learn-github-actions/workflow-syntax-for-github-actions)** to automate your Java project's CI/CD pipeline.

In **Part 1** , we used the GitHub Action `setup-java@v1` that defaults to Azul's **Zulu** builds of the OpenJDK.

In this article (Part 2) we will be creating a workflow from GitHub's Actions market place (a template Yaml file with defaults).

Getting Started {#_getting_started}
-----------------------------------

Assuming you've cloned [HelloWorldGHActions](https://github.com/carldea/HelloWorldGHActions) project or have an existing project we are going to create a GitHub workflow using the generated (suggested) Action `setup-java@v2` from the marketplace.

### Step 1 Create a Workflow {#_step_1_create_a_workflow}

Click on the **Actions tab** , then click on the **New Workflow** button as shown below:
![New Workflow](/images/posts/2021/11/github-actions-with-java-part-2/Actions-New-Workflow.png)

Next, click on the **Set up this workflow** option from the suggested **Java with Maven**
![Setup this Workflow](/images/posts/2021/11/github-actions-with-java-part-2/Setup-this-workflow.png)

### Step 2 Choose an OpenJDK Distribution {#_step_2_choose_an_openjdk_distribution}

Afterwards you should see something similar to the following:
![Setup-java@v2 Yaml](/images/posts/2021/11/github-actions-with-java-part-2/yaml-distro.png)

Enter either, `zulu` or `temurin` as your build distribution. When it come to what are the distribution choices and why will be covered later. For now in the example we'll pick **`zulu`**.

### Step 3 Create a Matrix for `java-version` {#_step_3_create_a_matrix_for_java_version}

Just like in [Part 1](https://foojay.io/today/github-actions-with-java-part-1/) of this series of articles we were able to specify a **matrix** to build the project on different operating systems, Java versions, etc. here we will be creating the same matrix for the `java-version` attribute.   

As shown below is a matrix to build using various JDK versions with `zulu` as its distribution.

<pre class="EnlighterJSRAW" data-enlighter-language="yml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">name: Java CI with Maven

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:

    runs-on: ubuntu-latest
    strategy:
      matrix:
        java-version: [ 8.0.192, 8, 11.0.3, 17, 18-ea ]
    steps:
    - uses: actions/checkout@v2
    - name: Set up JDK ${{ matrix.java-version }}
      uses: actions/setup-java@v2
      with:
        java-version: ${{ matrix.java-version }}
        distribution: 'zulu'
        cache: maven
    - name: Build with Maven
      run: mvn -B package --file pom.xml</pre>

1. Added is a matrix for the `java-version` attribute containing an array of fixed JDK versions, Latest LTS JDK versions, and an non-LTS JDK version (Early access release).

2. Use variable substitution `${{ matrix.java-version }}` for steps in the job.

### Step 4 Commit changes {#_step_3_commit_changes}

After you've updated the workflow you'll need to comment and commit the changes.
![Commit Workflow Changes](/images/posts/2021/11/github-actions-with-java-part-2/commit-workflow.png)

### Step 5 Validate GitHub Actions Workflow Job Run {#_step_4_validate_github_actions_workflow_job_run}

After, the changes have been committed let's validate to see the workflow jobs have been run correctly. Click on the **Actions** tab as shown below:  
![succesfully job run](/images/posts/2021/11/github-actions-with-java-part-2/Successful-Job-run.png)

Next, click on the `Create a workflow using a JDK distribution` workflow to see the JDK versions built on. As shown in the following:  
![JDK versions build on](/images/posts/2021/11/github-actions-with-java-part-2/JDKVersions-built.png)

### Step 6 Success! {#_step_5_success}

Now that you know how to use GitHub Action `setup-java@v2` to choose a particular OpenJDK build distribution let's talk a little about what's available and the differences between them.

What's the difference? {#_whats_the_difference}
-----------------------------------------------

Before we discuss the differences between `zulu` and `temurin` I want to mention changes regarding the `adopt` **AdoptOpenJDK** build distribution (specified as the default value for **distribution:** attribute). For many who are new to GitHub actions may not know the announcements made earlier in the year (Sept. 2021).

### AdoptOpenJDK is no more {#_adoptopenjdk_is_no_longer}

You'll notice the attribute **distribution:** as `'adopt'`. As of this writing the Yaml template still defaults to an OpenJDK distribution with the discontinued **AdoptOpenJDK** build (Announcement: <https://adoptopenjdk.net/>). ![AdoptOpenJDK discontinued](/images/posts/2021/11/github-actions-with-java-part-2/adopt-announcement.png)

The announcement informs developers about a rebranding of the name from **AdoptOpenJDK** to **Adoptium** . Also announced are new LTS builds named `Temurin` under the Eclipse Foundation. According to Adoptium the `Temurin` builds would undergo ([Java TCK](https://openjdk.java.net/groups/conformance/JckAccess/)) testing (Technology Compatibility Kit). Which is great news for the Java community at large.

### Long Live OpenJDK Distros {#_long_live_openjdk_distros}

A side note: To hear more about the many choices in build distributions in the wild you may be interested in the Foojay podcast [#4: Why So Many JDKs?"](https://foojay.io/today/foojay-podcast-4/).

While there are many distributions to choose from out in the wild, however (at the moment) within GitHub Actions `setup-java@v2` you can really pick two `zulu` or `temurin`.

To quickly compare here are the following **similarities** and **differences**. If you find something missing or incorrect please let me know in the comments below.

**Similarities:**

* Free updated (patched, latest) builds derived from the OpenJDK
* TCK Tested
* Support for the latest versions of LTS (Long Term Support) releases such as 8, 11 and 17
* **(Optional)** paid support

**The subtle differences between Zulu and Temurin**

**Zulu supports:**

* All LTS JDK versions such as 8, 11, and 17.
* Fixed (major) versions prior to Sept. 2021 such as `8.0.192` or `11.0.3`
* All non-LTS versions such as 7, 9, 10, 12, 13, 15, 16 and 18-ea.
* **(Optional)** Azul Systems provides paid support for both [Zulu](https://www.azul.com/products/core/) and [Temurin](https://www.azul.com/support-for-temurin/) for LTS and [MTS](https://www.azul.com/products/azul-support-roadmap/) (Medium Term Support on select versions)

**Temurin supports:**

* LTS JDK Versions such as 8, 11, and 17 after Sept 2021
* JDK 16 (non-LTS) version
* **(Optional)** [Azul](https://www.azul.com/support-for-temurin/), [IBM](https://www.ibm.com/cloud/support-for-runtimes/pricing), [Microsoft](https://docs.microsoft.com/en-us/azure/developer/java/fundamentals/java-support-on-azure) provides paid support

The other possible differences are whether distributions are bundled with JavaFX and builds on various OS platforms.

Now that you know the differences, you'll also remember in the Yaml file example above where I created a matrix of fixed (major) versions such as Java 8.0.192 and 11.0.3. As mentioned in Part 1, this is often a good practice whenever a build/test triggers (push/pull events) to help determine if the latest (`JDK 8`) had failed because of the **latest build** versus something in **your code** that caused (or introduced) the issue.

**To reiterate common coverage scenario:**

For example, when building with `JDK 8.0.192` (fixed version) the build/tests passes (**Green** ) and JDK 8 (latest) fails (**Red** ). This means that the latest `JDK 8` was the cause and **not your code**.

**Note:** Temurin does not support archived (fixed) releases prior to **Sept. 2021** and many non-LTS (long term support) releases if you plan to try out newer features in the Java language.

When it comes to GitHub Actions CI/CD I personally prefer to use `zulu` (Azul Zulu) (not just because I am an employee of Azul Systems), but because the OpenJDK builds are consistent (fixed versions \& latest), supports non-LTS versions and more OS platforms than just cloud environments.

I'm often of the opinion when it comes to CI/CD is the mindset of **set it and forget it** when it comes to building, testing and deploying. When I'm experimenting with new Java language features I usually like to take a **non-LTS** version out for a spin. So, just using `zulu` as a build distribution gives me a peace of mind.

Well, there you have it! A way to pick your OpenJDK build distribution for your CI/CD pipeline on GitHub.

Conclusion {#_conclusion}
-------------------------

When picking a particular build distribution with the current version of **GitHub Actions** `setup-java@v2` you really only have two build distributions to choose from: `zulu` or `temurin`.

I trust in the (near) future there will be an updated GitHub Action to provide more available build distributions to choose from.

Please share and comments are always welcome.

Happy coding (building)!
