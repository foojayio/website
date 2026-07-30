---
title: "Release with JReleaser to Maven Central with GitHub Actions"
slug: "how-to-release-a-java-module-with-jreleaser-to-maven-central-with-github-actions"
date: "2023-01-18T08:55:48+00:00"
lastmod: "2025-12-12T12:03:48+00:00"
description: "Learn from scratch about how to get started releasing a Java module with JReleaser to Maven Central with Github Actions."
authors:
  - "jago-de-vreede"
image: "https://foojay.io/wp-content/uploads/2023/01/Screenshot-2023-01-13-at-21.21.28.png"
categories:
  - "DevOps"
  - "Maven"
  - "Tutorials"
tags:
related_posts:
enlighterjs: true
frozen: false
---

> **This guide is outdated since 2024. There is a new version here: [https://foojay.io/today/how-to-publish-a-java-maven-project-to-maven-central-using-jreleaser-and-github-actions-2025-guide/](_wp_link_placeholder)**

{#more-61688}

This article is a tutorial that guides you through the process of releasing a Java module with [JReleaser](https://jreleaser.org/) to Maven Central with Github Actions.

JReleaser is a tool that streamlines the release process for Java projects, allowing developers to quickly and efficiently publish their modules to Maven Central.

If you just want to publish your Maven project by hand, then you can follow [How to Publish a Java Maven Project to the Maven Central Repository](https://foojay.io/today/how-to-publish-a-java-maven-project-to-the-maven-central-repository/ "How to Publish a Java Maven Project to the Maven Central Repository") by @[Tobias Briones](https://foojay.io/today/author/tobias-briones/ "Tobias Briones").

This article will use [SemVer Check project](https://github.com/jagodevreede/semver-check/ "SemVer Check project") as an example project that used Maven as a build tool.

Preconditions {#h2-0-preconditions}
-----------------------------------

In order to publish to Maven central, you will need to have a GPG key and have a group-id (coordinate) registered.

### GPG key {#h3-1-gpg-key}

You will need a GPG key to sign the artifacts, this will allow users to verify that they have the correct package.

* Download [GPG key](https://www.gnupg.org/download "GPG key") or install it with your favorite package manager.
* Generate a public key with (remember the password as we going to need it later) 

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">gpg --gen-key</pre>

* Now find the id of your key with 

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">gpg --list-keys --keyid-format=long</pre>

  The output should look something like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">/Users/jagodevreede/.gnupg/pubring.kbx
--------------------------------------
pub   rsa4096/XXXXXXXX9925B017 2022-11-17 [SC] [expires: 2026-11-17]
      C20FC085CF5B0D4D861E8CEDXXXXXXXX9925B017
uid                 [ultimate] Jago de Vreede &lt;<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="d1a3b4b5b0b2a5b4b591bcb0b8bdffb2bebc">[email&nbsp;protected]</a>&gt;
sub   rsa4096/XXXXXXXXXXXXFC74 2022-11-17 [E] [expires: 2026-11-17]</pre>

  In this case, the id of the public key is XXXXXXXXXXXXFC74
* Publish your public key to a public server for example ubuntu, for example 

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">gpg --keyserver keyserver.ubuntu.com --send-keys XXXXXXXXXXXXFC74</pre>

### Coordinate (group-id) {#h3-2-coordinate-group-id}

**This guide is outdated since 2024. There is a new version here: https://foojay.io/today/how-to-publish-a-java-maven-project-to-maven-central-using-jreleaser-and-github-actions-2025-guide/**

This process is actually very well documented at [https://central.sonatype.org/publish/requirements/coordinates/.](https://central.sonatype.org/publish/requirements/coordinates/)

Your group id can be your domain name (reverse) if you have that. Also, many Code Hosting services are supported like GitHub, GitLab, Gitee, Bitbucket, and SourceForge. In this example, we will use GitHub, so our group-id will be io.github.jagodevreede.

In order to "claim" this group-id you will need to [create a Jira account](https://issues.sonatype.org/secure/Signup!default.jspa) and [create a New project ticket](https://issues.sonatype.org/secure/CreateIssue.jspa?pid=10134&issuetype=21) and fill out the details of your project, as an example see [OSSRH-86928](https://issues.sonatype.org/browse/OSSRH-86928) for the ticket used in this tutorial. Please note that the Jira account is also used for the login of nexus (the artifact repository used).

When you have created the ticket you need to show ownership of the username, this is done by creating a temporary empty repository with the ticket name. When you have done this then the bot will automatically update the Jira ticket.

### Preparing your project {#h3-3-preparing-your-project}

#### Javadoc and sources

A project that is released to Maven central requires that you attach javadoc and the sources.

This can be done by adding the 2 plugins to your build, these plugins don't need to run every time so it's recommended to put them in a profile, so they will only run when you need to, or when you build a release.

A Maven example is as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">&lt;profiles&gt;
    &lt;profile&gt;
        &lt;id&gt;publication&lt;/id&gt;
        &lt;build&gt;
            &lt;plugins&gt;
                &lt;plugin&gt;
                    &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
                    &lt;artifactId&gt;maven-javadoc-plugin&lt;/artifactId&gt;
                    &lt;!-- 2.9.1 is the current version at the time of writing, please check if there is a newer version --&gt;
                    &lt;version&gt;2.9.1&lt;/version&gt;
                    &lt;executions&gt;
                        &lt;execution&gt;
                            &lt;id&gt;attach-javadocs&lt;/id&gt;
                            &lt;goals&gt;
                                &lt;goal&gt;jar&lt;/goal&gt;
                            &lt;/goals&gt;
                            &lt;configuration&gt;
                                &lt;attach&gt;true&lt;/attach&gt;
                            &lt;/configuration&gt;
                        &lt;/execution&gt;
                    &lt;/executions&gt;
                &lt;/plugin&gt;
                &lt;plugin&gt;
                    &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
                    &lt;artifactId&gt;maven-source-plugin&lt;/artifactId&gt;
                    &lt;!-- 3.2.1 is the current version at the time of writing, please check if there is a newer version --&gt;
                    &lt;version&gt;3.2.1&lt;/version&gt;
                    &lt;executions&gt;
                        &lt;execution&gt;
                            &lt;id&gt;attach-sources&lt;/id&gt;
                            &lt;goals&gt;
                                &lt;goal&gt;jar&lt;/goal&gt;
                            &lt;/goals&gt;
                            &lt;configuration&gt;
                                &lt;attach&gt;true&lt;/attach&gt;
                            &lt;/configuration&gt;
                        &lt;/execution&gt;
                    &lt;/executions&gt;
                &lt;/plugin&gt;
            &lt;/plugins&gt;
        &lt;/build&gt;
    &lt;/profile&gt;
&lt;/profiles&gt;</pre>

#### Meta information in pom

Maven central also requires metadata in your pom like a description, inception year, license, list of developers and scm location.

Example configurations is:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">&lt;description&gt;This is the root pom for the semver-check maven plugin&lt;/description&gt;
&lt;inceptionYear&gt;2022&lt;/inceptionYear&gt;

&lt;licenses&gt;
    &lt;license&gt;
        &lt;name&gt;Apache License, Version 2.0&lt;/name&gt;
        &lt;url&gt;https://www.apache.org/licenses/LICENSE-2.0.txt&lt;/url&gt;
        &lt;distribution&gt;repo&lt;/distribution&gt;
    &lt;/license&gt;
&lt;/licenses&gt;

&lt;developers&gt;
    &lt;developer&gt;
        &lt;id&gt;jagodevreede&lt;/id&gt;
        &lt;name&gt;Jago de Vreede&lt;/name&gt;
    &lt;/developer&gt;
&lt;/developers&gt;

&lt;scm&gt;
    &lt;connection&gt;scm:git:https://github.com/jagodevreede/semver-check.git&lt;/connection&gt;
    &lt;developerConnection&gt;scm:git:https://github.com/jagodevreede/semver-check.git&lt;/developerConnection&gt;
    &lt;url&gt;https://github.com/jagodevreede/semver-check.git&lt;/url&gt;
    &lt;tag&gt;HEAD&lt;/tag&gt;
&lt;/scm&gt;</pre>

#### Deploy plugin version

We need to have at least version 3.0.0 of the Maven deploy version, so add the following to the root pom

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">&lt;pluginManagement&gt;
    &lt;plugins&gt;
        &lt;plugin&gt;
            &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
            &lt;artifactId&gt;maven-deploy-plugin&lt;/artifactId&gt;
            &lt;version&gt;3.0.0&lt;/version&gt;
        &lt;/plugin&gt;
    &lt;/plugins&gt;
&lt;/pluginManagement&gt;</pre>

JReleaser {#h2-4-jreleaser}
---------------------------

### Installing locally {#h3-5-installing-locally}

Now with the preconditions out of the way, it is time to install JReleaser locally to verify that everything is working before we switch to GitHub actions. I would recommend that you do the first release locally, that way you can easily fix any errors.

Go to [jreleaser.org/guide/latest/install.html](https://jreleaser.org/guide/latest/install.html) and follow the instructions to install the latest **stable** version of JReleaser.

We need to create a configuration file for JReleaser, all this config will be put into secrets on GitHub later. Create a file in your home folder `~/.jreleaser/config.properties`

An example file will look something like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">JRELEASER_GITHUB_TOKEN=ghp_eWVzIGFsc28gc2VjcmV0==
JRELEASER_GPG_SECRET_KEY=something_base64_with_around_6500+_chars
JRELEASER_GPG_PASSPHRASE=secret
JRELEASER_GPG_PUBLIC_KEY=something_base64_with_around_3000+_chars
JRELEASER_NEXUS2_MAVEN_CENTRAL_USERNAME=jagodevreede
JRELEASER_NEXUS2_MAVEN_CENTRAL_PASSWORD=also-secret</pre>

#### GitHub token

First, we need a GitHub token that has access to the repository.

Go to [Settings -\> Developer settings -\> tokens (classic)](https://github.com/settings/tokens/) in your GitHub profile and create a token that has "repo" access. And place this in the JReleaser config file under the key `JRELEASER_GITHUB_TOKEN`.

#### GPG keys

When we generated the keys we also did a listing of the keys, we need to have the id of the public key, in the example above it was XXXXXXXXXXXXFC74.

First, we need to export our private key as a base64 string and put it in the config file under `JRELEASER_GPG_SECRET_KEY`, we can do that with (note you will need to password that you used when you created the key):

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">gpg --export-secret-keys XXXXXXXXXXXXFC74 | base64</pre>

The `JRELEASER_GPG_PASSPHRASE` is the password we used when we exported the secret key.

Next is the public key, also a base64 encoded string

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">gpg --export XXXXXXXXXXXXFC74 | base64</pre>

This we put in the config file under `JRELEASER_GPG_PUBLIC_KEY`.

#### Nexus credentials

Lastly, we need to put the credentials that we used to login to Jira in as nexus credentials under `JRELEASER_NEXUS2_MAVEN_CENTRAL_USERNAME` and `JRELEASER_NEXUS2_MAVEN_CENTRAL_PASSWORD`.

### JReleaser configuration {#h3-6-jreleaser-configuration}

We need to create a `jreleaser.yml` file for the project. This can be done with the cli we installed before, with the following command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">jreleaser init --format yml</pre>

You will need to edit the file and fill in the yml, this is where your copy-paste skills will shine, as almost all information can be found in the `pom.xml`.

Next, remove the `distributions` part and `version` from the yml. As the distribution will be added later, and the version will be set via an environment variable.

Finally, add the configuration to push to Maven central:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">signing:
  active: ALWAYS
  armored: true

deploy:
  maven:
    nexus2:
      maven-central:
        active: ALWAYS
        url: https://s01.oss.sonatype.org/service/local
        closeRepository: true
        releaseRepository: false
        stagingRepositories:
          - target/staging-deploy</pre>

The actual local release {#h2-7-the-actual-local-release}
---------------------------------------------------------

Now that all the preconditions and plumbing is out of the way it is time for the actual release

Prepare your Maven project to be released, so remove the -SNAPSHOT from your versions.

You can do that with the Maven versions plugin for example

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">mvn versions:set -DnewVersion=0.0.1</pre>

### Staging {#h3-8-staging}

The release needs to be uploaded from a staging directory, to create that invoke the following command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">mvn -Ppublication deploy -DaltDeploymentRepository=local::file:./target/staging-deploy</pre>

### Release {#h3-9-release}

First set the version that you will be releasing (this must be the same as what you got in your pom.xml)

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">export JRELEASER_PROJECT_VERSION=0.0.1</pre>

Then do the actual release with:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">jreleaser full-release</pre>

### Finalize in nexus {#h3-10-finalize-in-nexus}

You will need to log in to [nexus and go to staging repositories](https://s01.oss.sonatype.org/#stagingRepositories) after JReleaser is done. This is your final stop, after this, there is no turning back or removing it.
![](/images/posts/2023/01/how-to-release-a-java-module-with-jreleaser-to-maven-central-with-github-actions/Screenshot-2023-01-11-at-20.07.34-1024x319.png) Example of staged release, ready to be released

Even this step can be automated by setting the `releaseRepository` property to `true` in the `jrelease.yml`. You can do that when you trust the process and have done some successful releases.

GitHub action {#h2-11-github-action}
------------------------------------

Now that we can release by hand it is time to automate this entire process!

### Secrets {#h3-12-secrets}

Before we can run JReleaser on GitHub, we first need to set our secrets in the secrets of the repository.

To keep things simple just copy all the key values from the JReleaser config file that was used locally.

And you will end up with something like this:
![](/images/posts/2023/01/how-to-release-a-java-module-with-jreleaser-to-maven-central-with-github-actions/Screenshot-2023-01-13-at-21.21.28-1024x922.png)

### Workflow {#h3-13-workflow}

First, create a release workflow by creating a `release.yml` file in your repository under `.github/workflows/`.

The first bit of the file is the name of the workflow and the input parameters used when you start the workflow.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">name: Release

on:
  workflow_dispatch:
    inputs:
      version:
        description: 'Release version'
        required: true
      nextVersion:
        description: 'Next version after release (-SNAPSHOT will be added automatically)'
        required: true</pre>

This will look something like this when you start the release workflow
![](/images/posts/2023/01/how-to-release-a-java-module-with-jreleaser-to-maven-central-with-github-actions/Screenshot-2023-01-11-at-19.54.23-1024x729.png)

Next up is just your default build setup, in this example, java 11 is used, but this is the same as for your normal build. Except `fetch-depth` as JReleaser will use the git log to create the changelog it will need the full history, and thus we set the `fetch-depth` to `0`. As it defaults to `1`.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout
        uses: actions/checkout@v3
        with:
          fetch-depth: 0
      - name: Set up JDK 11
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
          cache: maven</pre>

Next, we need to set the version that we will be releasing, we can do that with the Maven versions plugin.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">- name: Set release version
  run: mvn --no-transfer-progress --batch-mode versions:set -DnewVersion=${{ github.event.inputs.version }}</pre>

This change will be the code that will be released, so we want to commit that change. A tag will be created in the release process by JReleaser

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">- name: Commit &amp; Push changes
  uses: actions-js/push@master
  with:
    github_token: ${{ secrets.JRELEASER_GITHUB_TOKEN }}
    message: Releasing version ${{ github.event.inputs.version }}</pre>

Now its time to stage the release, as we did manually

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">- name: Stage release
  run: mvn --no-transfer-progress --batch-mode -Ppublication clean deploy -DaltDeploymentRepository=local::default::file://`pwd`/target/staging-deploy
</pre>

Then we can call JReleaser this is where we use the secrets we set up before.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">- name: Run JReleaser
  uses: jreleaser/release-action@v2
  with:
    setup-java: false
    version: 1.4.0
  env:
    JRELEASER_PROJECT_VERSION: ${{ github.event.inputs.version }}
    JRELEASER_GITHUB_TOKEN: ${{ secrets.JRELEASER_GITHUB_TOKEN }}
    JRELEASER_GPG_PASSPHRASE: ${{ secrets.JRELEASER_GPG_PASSPHRASE }}
    JRELEASER_GPG_PUBLIC_KEY: ${{ secrets.JRELEASER_GPG_PUBLIC_KEY }}
    JRELEASER_GPG_SECRET_KEY: ${{ secrets.JRELEASER_GPG_SECRET_KEY }}
    JRELEASER_NEXUS2_MAVEN_CENTRAL_USERNAME: ${{ secrets.JRELEASER_NEXUS2_MAVEN_CENTRAL_USERNAME }}
    JRELEASER_NEXUS2_MAVEN_CENTRAL_PASSWORD: ${{ secrets.JRELEASER_NEXUS2_MAVEN_CENTRAL_PASSWORD }}</pre>

When we are done we need to set the next development version and push that.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-linenumbers="false">- name: Set release version
  run: mvn --no-transfer-progress --batch-mode versions:set -DnewVersion=${{ github.event.inputs.nextVersion }}
- name: Commit &amp; Push changes
  uses: actions-js/push@master
  with:
    github_token: ${{ secrets.JRELEASER_GITHUB_TOKEN }}
    message: Setting SNAPSHOT version ${{ github.event.inputs.nextVersion }}-SNAPSHOT
    tags: true</pre>

The full file can be found here: <https://github.com/jagodevreede/semver-check/blob/f3fab073107ce6691c1b0bff25f7df8ecf2165aa/.github/workflows/release.yml>

And with that we are done, now we can easily release our module to maven central with a press of a button in GitHub.

Maven plugin {#h2-14-maven-plugin}
----------------------------------

There is also a JReleaser [Maven plugin](https://jreleaser.org/guide/latest/quick-start/maven.html) available that offers a Maven DSL to configure JReleaser. With that the `jreleaser.yml` file can be omitted as information can be read from the pom file instead.  
The use of the Maven DSL offers these benefits:

* reduce duplication in release configuration
* no need to install JReleaser's cli
* no need to use jreleaser/release-action on Github as invoking the Maven plugin is enough

So why is the cli used in this article you might ask, well the cli can be used for other than Maven projects as well and it demonstrates the capabilities of JReleaser.

Resources used {#h2-15-resources-used}
--------------------------------------

* <https://central.sonatype.org/publish/publish-guide/>
* <https://jreleaser.org/guide/latest/examples/maven/index.html>
* <https://github.com/marketplace/actions/jreleaser>

<br />
