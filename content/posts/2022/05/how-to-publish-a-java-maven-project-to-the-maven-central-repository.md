---
title: "Publish a Java Project to the Maven Central Repository | Foojay.io Today"
slug: "how-to-publish-a-java-maven-project-to-the-maven-central-repository"
date: "2022-05-13T06:22:58+00:00"
lastmod: "2022-05-13T16:46:17+00:00"
description: "Get started and follow along whenever you need to publish a Java Maven-based library or artifact to Maven!"
authors:
  - "tobias-briones"
image: "/images/posts/2022/05/how-to-publish-a-java-maven-project-to-the-maven-central-repository/jira-ticket-1.png"
categories:
  - "Developer Tools"
  - "DevOps"
  - "Maven"
  - "Tutorials"
tags:
related_posts:
  - "faster-maven-builds-1"
  - "faster-maven-builds-part-2"
  - "fixing-vulnerabilities-in-maven-projects"
  - "enterprise-java-quality-gates-ai"
enlighterjs: true
frozen: false
---

This guide will describe how to publish the [JDesk framework](https://github.com/tobiasbriones/jdesk) to Maven Central.

The project is written in Java using Maven as the build tool. So, the process will be detailed for Java projects with a `pom.xml` file.

You can follow this article if you need to publish a Java Maven-based library or artifact to Maven.

Get Ready for Publishing Packages {#h2-0-get-ready-for-publishing-packages}
---------------------------------------------------------------------------

Publishing a Java artifact is not as trivial as Python or JavaScript packages can be deployed.

You have to comply with several rules.

I will show the general steps to publish the artifact.

### Basic Requirements {#h3-1-basic-requirements}

You need to own a domain name where the package is going to live or be hosted. It is not necessary to have a domain name for each package, you can use the GitHub.io page that is generated from your project, but for real packages is required to have a professional domain. In this case, the domain is **jdesk.mathsoftware.engineer**.

This is semantic, therefore you follow the subsets of: **engineers** (all engineers) -\> **mathsoftware** (me, a particular engineer) -\> **jdesk** (a project of mine).

Later, the reverse DNS has to be used for the standard Java packaging convention. That is, **engineer.mathsoftware.jdesk**.

### Safety {#h3-2-safety}

You will also need a private key to sign the artifact. Generate a [GPG key](https://www.gnupg.org/download) to use. Then run `gpg --gen-key` and set it up like when you generate a GitHub GPG key.

### Build Tool {#h3-3-build-tool}

You can use Gradle or Maven as the build tool. Because of the nature of this project, I chose to use Maven in the beginning. So this article will use Maven to configure the `pom.xml` file.

You need to have Maven installed into your system. Go to [download Apache Maven](https://maven.apache.org/download.cgi), uncompress the file, then add the entry to the Path environment variable with the value of the bin directory, like "C:\\Users\\user\\apache-maven-3.8.5\\bin" (Windows). Check this installation by running "mvn --version" into a new terminal.

### Create a JIRA Account and Request a Ticket {#h3-4-create-a-jira-account-and-request-a-ticket}

First, you will need to have a [Sonatype JIRA account](https://issues.sonatype.org/secure/Signup!default.jspa) to open a new ticket to request your access to publish your artifact.

This sounds really weird, at least for the first time I had to do it. Just keep carrying on.

Then, create a [JIRA ticket](https://issues.sonatype.org/secure/CreateIssue.jspa?issuetype=21&amp;pid=10134) to fill your information. I will add screenshots to get you an idea of how to proceed. Then you will be assisted by a bot to complete your request.

[![JIRA Ticket 1](/images/posts/2022/05/how-to-publish-a-java-maven-project-to-the-maven-central-repository/jira-ticket-1-1.png)](/images/posts/2022/05/how-to-publish-a-java-maven-project-to-the-maven-central-repository/jira-ticket-1-1.png)

[![JIRA Ticket 2](/images/posts/2022/05/how-to-publish-a-java-maven-project-to-the-maven-central-repository/jira-ticket-2.png)](/images/posts/2022/05/how-to-publish-a-java-maven-project-to-the-maven-central-repository/jira-ticket-2.png)

[![JIRA Ticket 3](/images/posts/2022/05/how-to-publish-a-java-maven-project-to-the-maven-central-repository/jira-ticket-3.png)](/images/posts/2022/05/how-to-publish-a-java-maven-project-to-the-maven-central-repository/jira-ticket-3.png)

As shown above, you have to fill your domain name, and the project's Git repository. Then prove ownership by adding a text record to your domain.

### Next Steps {#h3-5-next-steps}

That was the overall process. You can read [Publish Artifacts to Maven Central \| JetBrains Space](https://www.jetbrains.com/help/space/publish-artifacts-to-maven-central.html) and [OSSRH Guide - The Central Repository Documentation](https://central.sonatype.org/publish/publish-guide) for more details.

You will need to upload your PG key, read [Working with PGP Signatures - The Central Repository Documentation](https://central.sonatype.org/publish/requirements/gpg/#installing-gnupg) to complete this step. Recall to set a secure passphrase, expiration date, and store the key, and its backup safely too.

#### Release Version

Two kind of releases are used for Java packages.

The normal versions, like `0.1.0` and SNAPSHOT versions like `0.1.0-SNAPSHOT`.

This is also confusing, but it only means that the SNAPSHOT version that goes to  

a basic repository can still have minimum changes by the developer, so the  

consumer knows that version might change a little yet. The normal version on the  

other hand, is deployed to the main repository, and it takes about 4 hours to  

reflect the changes.

#### Save your Environment Variables

You can safely store your credentials into the `.m2/settings.xml` file in your  

user's home directory.

In order to connect to your OSSRH, you will give your credentials to the maven  

command.

Your file will look like this:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd"&gt;
    &lt;servers&gt;
        &lt;server&gt;
            &lt;id&gt;ossrh&lt;/id&gt;
            &lt;username&gt;{ username }&lt;/username&gt;
            &lt;password&gt;{ password }&lt;/password&gt;
        &lt;/server&gt;
    &lt;/servers&gt;

    &lt;profiles&gt;
        &lt;profile&gt;
            &lt;id&gt;ossrh&lt;/id&gt;
            &lt;activation&gt;
                &lt;activeByDefault&gt;true&lt;/activeByDefault&gt;
            &lt;/activation&gt;
            &lt;properties&gt;
                &lt;gpg.executable&gt;gpg&lt;/gpg.executable&gt;
                &lt;gpg.keyname&gt;{ gpg-key }&lt;/gpg.keyname&gt;
                &lt;gpg.passphrase&gt;{ gpg-passphrase }&lt;/gpg.passphrase&gt;
            &lt;/properties&gt;
        &lt;/profile&gt;
    &lt;/profiles&gt;
&lt;/settings&gt;</pre>

You will add your credentials. I would like to store encrypted passwords instead, but that would be another step to add tho this guide.

**TIP:** Run `gpg --list-secret-keys --keyid-format=long` to check the GPG keys installed on your machine. Then you can get your key's id. You will copy the long HEX number, read the PG guide given above to check more details.

This process can be tricky, so you will likely have to solve some issues in the way.

Set Up Maven in the Project {#h2-6-set-up-maven-in-the-project}
---------------------------------------------------------------

This is the configuration that was applied to the project. It has many things, so it is exhausting. Once this is done properly, the release without errors will be trivial.

The following is to be added to the `pom.xml` file of the project, apart from the standard properties like group id, version, etc.

The general information like name, description, etc., will be shown when the artifact gets deployed on Maven.

As you can see, the first tags are just general project information:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;project&gt;
    &lt;!--- ... --&gt;
    &lt;name&gt;JDesk&lt;/name&gt;
    &lt;description&gt;
        Library/Framework to build better Java Swing desktop apps.
    &lt;/description&gt;
    &lt;url&gt;https://jdesk.mathsoftware.engineer&lt;/url&gt;

    &lt;licenses&gt;
        &lt;license&gt;
            &lt;name&gt;BSD 3-Clause License&lt;/name&gt;
            &lt;url&gt;https://opensource.org/licenses/BSD-3-Clause&lt;/url&gt;
        &lt;/license&gt;
    &lt;/licenses&gt;

    &lt;developers&gt;
        &lt;developer&gt;
            &lt;name&gt;Tobias Briones&lt;/name&gt;
            &lt;email&gt;<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="b4d0d1c2f4d9d5c0dcc7dbd2c0c3d5c6d19ad1dad3dddad1d1c6">[email&nbsp;protected]</a>&lt;/email&gt;
            &lt;organization&gt;Tobias Briones&lt;/organization&gt;
            &lt;organizationUrl&gt;https://mathsoftware.engineer&lt;/organizationUrl&gt;
        &lt;/developer&gt;
    &lt;/developers&gt;
&lt;/project&gt;</pre>

Then, other information about the project's repository:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;project&gt;
    &lt;!--- ... --&gt;
    &lt;scm&gt;
        &lt;connection&gt;
            scm:git:git://github.com/tobiasbriones/jdesk.git
        &lt;/connection&gt;
        &lt;developerConnection&gt;
            scm:git:ssh://github.com:tobiasbriones/jdesk.git
        &lt;/developerConnection&gt;
        &lt;url&gt;https://github.com/tobiasbriones/jdesk&lt;/url&gt;
    &lt;/scm&gt;
&lt;/project&gt;</pre>

```

```

### Build Config {#h3-7-build-config}

This will add the configuration for the build tag.

Add the maven plugin for source code:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;plugin&gt;
    &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
    &lt;artifactId&gt;maven-source-plugin&lt;/artifactId&gt;
    &lt;version&gt;3.2.1&lt;/version&gt;
    &lt;executions&gt;
        &lt;execution&gt;
            &lt;id&gt;attach-sources&lt;/id&gt;
            &lt;goals&gt;
                &lt;goal&gt;jar-no-fork&lt;/goal&gt;
            &lt;/goals&gt;
        &lt;/execution&gt;
    &lt;/executions&gt;
&lt;/plugin&gt;</pre>

and the other for the javadocs, the javadoc binary has to be set to match your system's javadoc:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;plugin&gt;
    &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
    &lt;artifactId&gt;maven-javadoc-plugin&lt;/artifactId&gt;
    &lt;version&gt;3.2.0&lt;/version&gt;
    &lt;executions&gt;
        &lt;execution&gt;
            &lt;id&gt;attach-javadocs&lt;/id&gt;
            &lt;goals&gt;
                &lt;goal&gt;jar&lt;/goal&gt;
            &lt;/goals&gt;
        &lt;/execution&gt;
    &lt;/executions&gt;
    &lt;configuration&gt;
        &lt;javadocExecutable&gt;
            ${java.home}/bin/javadoc
        &lt;/javadocExecutable&gt;
    &lt;/configuration&gt;
&lt;/plugin&gt;</pre>

```

```

Then, we need two more plugins:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;plugin&gt;
    &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
    &lt;artifactId&gt;maven-surefire-plugin&lt;/artifactId&gt;
    &lt;version&gt;2.22.2&lt;/version&gt;
&lt;/plugin&gt;</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;plugin&gt;
    &lt;groupId&gt;org.sonatype.plugins&lt;/groupId&gt;
    &lt;artifactId&gt;nexus-staging-maven-plugin&lt;/artifactId&gt;
    &lt;version&gt;1.6.8&lt;/version&gt;
    &lt;extensions&gt;true&lt;/extensions&gt;
    &lt;configuration&gt;
        &lt;serverId&gt;ossrh&lt;/serverId&gt;
        &lt;nexusUrl&gt;https://s01.oss.sonatype.org/&lt;/nexusUrl&gt;
        &lt;autoReleaseAfterClose&gt;true&lt;/autoReleaseAfterClose&gt;
    &lt;/configuration&gt;
&lt;/plugin&gt;</pre>

The final build configuration will look like this:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;build&gt;
    &lt;plugins&gt;
        &lt;plugin&gt;
            &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
            &lt;artifactId&gt;maven-source-plugin&lt;/artifactId&gt;
            &lt;version&gt;3.2.1&lt;/version&gt;
            &lt;executions&gt;
                &lt;execution&gt;
                    &lt;id&gt;attach-sources&lt;/id&gt;
                    &lt;goals&gt;
                        &lt;goal&gt;jar-no-fork&lt;/goal&gt;
                    &lt;/goals&gt;
                &lt;/execution&gt;
            &lt;/executions&gt;
        &lt;/plugin&gt;
        &lt;plugin&gt;
            &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
            &lt;artifactId&gt;maven-javadoc-plugin&lt;/artifactId&gt;
            &lt;version&gt;3.2.0&lt;/version&gt;
            &lt;executions&gt;
                &lt;execution&gt;
                    &lt;id&gt;attach-javadocs&lt;/id&gt;
                    &lt;goals&gt;
                        &lt;goal&gt;jar&lt;/goal&gt;
                    &lt;/goals&gt;
                &lt;/execution&gt;
            &lt;/executions&gt;
            &lt;configuration&gt;
                &lt;javadocExecutable&gt;
                    ${java.home}/bin/javadoc
                &lt;/javadocExecutable&gt;
            &lt;/configuration&gt;
        &lt;/plugin&gt;
        &lt;plugin&gt;
            &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
            &lt;artifactId&gt;maven-surefire-plugin&lt;/artifactId&gt;
            &lt;version&gt;2.22.2&lt;/version&gt;
        &lt;/plugin&gt;
        &lt;plugin&gt;
            &lt;groupId&gt;org.sonatype.plugins&lt;/groupId&gt;
            &lt;artifactId&gt;nexus-staging-maven-plugin&lt;/artifactId&gt;
            &lt;version&gt;1.6.8&lt;/version&gt;
            &lt;extensions&gt;true&lt;/extensions&gt;
            &lt;configuration&gt;
                &lt;serverId&gt;ossrh&lt;/serverId&gt;
                &lt;nexusUrl&gt;https://s01.oss.sonatype.org/&lt;/nexusUrl&gt;
                &lt;autoReleaseAfterClose&gt;true&lt;/autoReleaseAfterClose&gt;
            &lt;/configuration&gt;
        &lt;/plugin&gt;
    &lt;/plugins&gt;
&lt;/build&gt;</pre>

### Distribution Management Config {#h3-8-distribution-management-config}

Add this child to the project's root:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;distributionManagement&gt;
    &lt;snapshotRepository&gt;
        &lt;id&gt;ossrh&lt;/id&gt;
        &lt;url&gt;
            https://s01.oss.sonatype.org/content/repositories/snapshots
        &lt;/url&gt;
    &lt;/snapshotRepository&gt;
    &lt;repository&gt;
        &lt;id&gt;ossrh&lt;/id&gt;
        &lt;url&gt;
            https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/
        &lt;/url&gt;
    &lt;/repository&gt;
&lt;/distributionManagement&gt;</pre>

That way, you set the repositories for snapshots, and final release. If you go to the snapshot repository link, you will literally find the directory for all the repositories added with their reverse DNS.

### Profiles Config {#h3-9-profiles-config}

We'll want to run a profile called ci-cd with the following configuration:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;profiles&gt;
    &lt;profile&gt;
        &lt;id&gt;ci-cd&lt;/id&gt;
        &lt;build&gt;
            &lt;plugins&gt;
                &lt;plugin&gt;
                    &lt;groupId&gt;org.apache.maven.plugins&lt;/groupId&gt;
                    &lt;artifactId&gt;maven-gpg-plugin&lt;/artifactId&gt;
                    &lt;version&gt;1.6&lt;/version&gt;
                    &lt;executions&gt;
                        &lt;execution&gt;
                            &lt;id&gt;sign-artifacts&lt;/id&gt;
                            &lt;phase&gt;verify&lt;/phase&gt;
                            &lt;goals&gt;
                                &lt;goal&gt;sign&lt;/goal&gt;
                            &lt;/goals&gt;
                            &lt;configuration&gt;
                                &lt;keyname&gt;${gpg.keyname}&lt;/keyname&gt;
                                &lt;passphraseServerId&gt;
                                    ${gpg.keyname}
                                &lt;/passphraseServerId&gt;
                                &lt;gpgArguments&gt;
                                    &lt;arg&gt;--pinentry-mode&lt;/arg&gt;
                                    &lt;arg&gt;loopback&lt;/arg&gt;
                                &lt;/gpgArguments&gt;
                            &lt;/configuration&gt;
                        &lt;/execution&gt;
                    &lt;/executions&gt;
                &lt;/plugin&gt;
            &lt;/plugins&gt;
        &lt;/build&gt;
    &lt;/profile&gt;
&lt;/profiles&gt;</pre>

Visit [Introduction to Build Profiles \| Apache Maven](https://maven.apache.org/guides/introduction/introduction-to-profiles.html) to learn more about Maven profiles.

This configuration is tricky. It's useful for verification of the GPG signature, and it looks like it'll prevent some issues.

Read more about this plugin at [GPG Plugin \| Maven](https://maven.apache.org/plugins/maven-gpg-plugin). This plugin allows us to deploy signed artifacts.

The `pinentry-mode` argument set to `loopback` passed to the GPG program allows to enter the passphrase via terminal instead of via the Pinentry GUI.

I have also read that you have to use the Pinentry GUI to enter your passphrase, and do not enter it on the terminal, so take this into account. As documented above, the GPG passphrase is stored in the \`settings.xml\` file, so this should not concern here.

This means that the program will inline the passphrase when signing the artifact.

This is the profile to run when deploying the artifact.

### Maven pom.xml File {#h3-10-maven-pom-xml-file}

The final `pom.xml` file ends up [like this](https://github.com/tobiasbriones/jdesk/blob/v0.2.0/pom.xml).

Deploy the Project {#h2-11-deploy-the-project}
----------------------------------------------

In this last step, if everything is all right, we can now deploy the artifact. Add "-SNAPSHOT" if you will deploy to the snapshot repository. For example:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;version&gt;0.1.0-SNAPSHOT&lt;/version&gt;</pre>

```
or else:

```

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;version&gt;0.1.0&lt;/version&gt;</pre>

Open Git Bash into the project's directory and run the deploy-command with the " ci-cd" profile:

`mvn clean deploy -Pci-cd`

This will give you an output like this if you deploy a snapshot:

[![Snapshot Deployment Result](/images/posts/2022/05/how-to-publish-a-java-maven-project-to-the-maven-central-repository/snapshot-deployment-result.png)](/images/posts/2022/05/how-to-publish-a-java-maven-project-to-the-maven-central-repository/snapshot-deployment-result.png)

Recall that the production release takes some 4 waiting hours to complete the  

deployment and be available at [Maven Search](https://search.maven.org).

Result {#h2-12-result}
----------------------

After deploying to the main repository, which took more than 4 hours to get it  

done, the result is there:

[![JDesk 0.2.0 Deployed](/images/posts/2022/05/how-to-publish-a-java-maven-project-to-the-maven-central-repository/jdesk-0.2.0-deployed.png)](/images/posts/2022/05/how-to-publish-a-java-maven-project-to-the-maven-central-repository/jdesk-0.2.0-deployed.png)

Conclusion {#h2-13-conclusion}
------------------------------

The deployment of a Java Maven-based library to the Maven Central Repository was documented so users can import the library into their Java projects.

### Recommendations {#h3-14-recommendations}

* Make sure you include good JavaDocs in your project. That is important for a library that is going to be used by others.
* The process might throw errors or warnings, always try to fix warnings as much as possible.
* One error you might face is due to wrong JavaDocs. So make sure your IDE checks right your JavaDocs in your code, with no syntax errors
* Use semantic versioning, in Java we are careful about breaking changes.
* Read the docs to get further understanding, this is a general guide, so you can understand the process. The Sonatype servers, and Maven docs have also changed in the past.
