---
title: "Why You Should Upgrade to Maven Version 3.8.1 Today or Very Soon"
slug: "why-you-should-upgrade-to-maven-version-3-8-1-today-or-very-soon"
date: "2021-08-11T08:06:09+00:00"
lastmod: "2021-11-23T09:53:10+00:00"
description: "If you are still running on an old Maven version like 3.6.3 or below, you need to upgrade to version 3.8.1 because of security reasons."
canonical: "https://snyk.io/blog/why-you-should-upgrade-to-maven-version-3-8-1/"
authors:
  - "bmvermeer"
image: "/images/posts/2021/08/why-you-should-upgrade-to-maven-version-3-8-1-today-or-very-soon/poms.png"
categories:
  - "Maven"
  - "Security"
tags:
related_posts:
  - "understanding-apache-maven-part-1-the-basics"
  - "understanding-apache-maven-part-2-pom-hierarchy"
  - "understanding-apache-maven-part-3-maven-coordinates-pom-inheritance"
enlighterjs: true
frozen: false
---

If you are working in the Java ecosystem and building your applications with an older Maven version, this message is for you.

Check your Maven version by typing `mvn -version`! If you are still running on an old Maven version like 3.6.3 or below you definitely need to upgrade to version 3.8.1 because of security reasons. Be aware that to run Maven 3.8.1, Java 7+ is required.

Luckily we found out in the [JVM Ecosystem report 2021](https://snyk.io/jvm-ecosystem-report-2021/) that not many people work with Java 6 or below. We do see that a lot of people use Maven so not upgrading can end up in serious issues for a large part of the ecosystem.

The problem with HTTP repositories in older Maven versions {#h2-0-the-problem-with-http-repositories-in-older-maven-versions}
-----------------------------------------------------------------------------------------------------------------------------

Maven versions prior to 3.8.1 allowed users to connect to custom repositories using HTTP. This is reported by Jonathan Leitschuh and documented in [CVE-2021-26291](https://snyk.io/vuln/SNYK-JAVA-ORGAPACHEMAVEN-1255570). From the [release notes of Maven 3.8.1](https://maven.apache.org/docs/3.8.1/release-notes.html), Maven distinguished three separate issues:

* A [man-in-the-middle](https://snyk.io/learn/man-in-the-middle-attack/) attack (MITM Attack) due to the use of custom repositories over HTTP
* Domain hijacking when custom repositories are using abandoned domains
* Possible hijacking of downloads by redirecting to custom repositories

The order for downloading repositories is described on the [Repository Order Page](https://maven.apache.org/guides/mini/guide-multiple-repositories.html#repository-order) as follows:

1. Effective settings:  
   - Global (defined in the `${maven.home}/conf/settings.xml`)  
   - User (defined in `${user.home}/.m2/settings.xml`)
2. Local effective build POM:  
   - Local `pom.xml` (the project `pom.xml` file)  
   - Parent POM's recursively  
   - Super POM
3. Effective POMs from dependency path to the artifact

This means that after looking at the settings files, Maven looks for repositories in the `pom.xml`. This ends in the Super POM where the location to Maven Central is defined.

### Effective POMs from dependency path to the artifact {#h3-1-effective-poms-from-dependency-path-to-the-artifact}

The third step is somewhat tricky, but let me try to explain this:

Let's take a look at a project POM file that has one dependency (depA) and also declared a custom repository (myRepo1).

Maven will first look in myRepo1 for depA before going to Maven central because it looks at the local `pom.xml` first.

If the depA has a dependency (depB) and depA has a myRepo2 (like below), where will depB be downloaded from?
![](/images/posts/2021/08/why-you-should-upgrade-to-maven-version-3-8-1-today-or-very-soon/Project-pom.xml_-1024x754.png)

To download depB, Maven will first look at myRepo1 because it is in the project POM (2a of the repository order, local `pom.xml`). Next, it will go to the parent POM all the way up to Maven Central via the Super POM. If the package depB is NOT available at Maven Central it will download the package from myRepo2.

This is a feature of Maven and valid for when the package is not published on Maven Central. However, it can also be that Maven Central is not available for other reasons.

This might not be what you expect, and more importantly, Maven allows HTTP repositories in versions prior to 3.8.1.

There are POM files on Maven Central that contain references to custom repositories over HTTP. POM files on Maven central are immutable, so it can easily be that developers are unaware that they connect to an external repository over HTTP introduced by a transitive dependency, making them a possible target to a MITM attack.

HTTPS by default in Maven version 3.8.1 {#h2-2-https-by-default-in-maven-version-3-8-1}
---------------------------------------------------------------------------------------

To mitigate the problems discussed above, Maven decided to block external HTTP repositories by default. This is done by adding a `<blocked>` field in the mirror configuration and providing the following mirror to your global setting located at `${maven.home}/conf/settings.xml`.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">    &lt;mirror&gt;
      &lt;id&gt;maven-default-http-blocker&lt;/id&gt;
      &lt;mirrorOf&gt;external:http:*&lt;/mirrorOf&gt;
      &lt;name&gt;Pseudo repository to mirror external repositories initially using HTTP.&lt;/name&gt;
      &lt;url&gt;https://0.0.0.0/&lt;/url&gt;
      &lt;blocked&gt;true&lt;/blocked&gt;
    &lt;/mirror&gt;
</pre>

The result is that new applications built with Maven will not connect to external repositories using HTTP but only HTTPS. This is because HTTPS guarantees that the client is communicating with the requested server. This prevents MITM attacks to a great extent.

Note that HTTP connections to localhost and file repositories are still allowed.

Jonathan Leitschuh wrote a great InfoSec article, "[Want to take over the Java ecosystem? All you need is a MITM!](https://infosecwriteups.com/want-to-take-over-the-java-ecosystem-all-you-need-is-a-mitm-1fc329d898fb)" in 2019 if you want to learn more.

How to upgrade {#h2-3-how-to-upgrade}
-------------------------------------

First of all, you need to download Maven version 3.8.1 or higher and rebuild your application. Next to that, if you have a repository defined in your `pom.xml` file, please fix it so it is an HTTPS URL. If an HTTP repository is defined in one of your dependencies, you will get an error. First, search for newer versions of that library that replaced the HTTP repository url with an HTTPS version.

In some examples, companies use internal repositories. These may still be on HTTP rather than HTTPS and that will break your building process with Maven version 3.8.1. The best solution is to make a one-time investment and ensure these repositories use HTTPS. Alternatively, this is just a mirror setting and can be changed if needed. You can create your own mirror setting in your `${user.home}/.m2/settings.xml` or change the global `settings.xml`.

We use a lot of dependencies when developing software. We shouldn't take this for granted, as this is all a chain of trust when we have to deal with transitive dependencies. Make sure to pick the correct package and upgrade your dependencies in time. Equally, it is essential to upgrade the tooling we use to prevent malicious packages in our system.   

This article was originally posted on the [Snyk.io blog](https://snyk.io/blog/why-you-should-upgrade-to-maven-version-3-8-1/) and reused with permission.
