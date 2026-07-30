---
title: "Getting Started with Jakarta EE 9: Jakarta EE 9.1 | Foojay.io Today"
slug: "getting-started-with-jakarta-ee-9-jakarta-ee-9-1"
date: "2021-10-13T08:02:33+00:00"
lastmod: "2021-10-13T08:13:14+00:00"
description: "Jakarta EE 9.1 was released at the end of May 2021 with the goal to provide certification on Java SE 11 and not to deliver new features."
canonical: "https://blog.payara.fish/getting-started-with-jakarta-ee-9-jakarta-ee-9.1"
authors:
  - "jadon-ortlepp"
  - "rudy-de-busscher"
image: "/images/posts/2021/10/getting-started-with-jakarta-ee-9-jakarta-ee-9-1/jakarta-ee-9.1.png"
categories:
  - "Jakarta EE"
tags:
related_posts:
  - "payara-platform-october-2021-survey"
  - "are-java-jakarta-ee-application-servers-heavy"
  - "do-java-jakarta-ee-standards-matter"
  - "enterprise-java-in-practice-fragmentation-platforms-and-real-world-trade-offs"
enlighterjs: true
frozen: false
---

Jakarta EE 9.1 was officially released at the end of May 2021.

The objective of Jakarta EE 9.1 is to provide certification on Java SE 11 and not to deliver new features. These are scheduled for Jakarta EE 10.

In this article, we describe how you can use Jakarta EE 9.1 and some background around Jakarta EE 9.1.

Using Jakarta EE 9.1 {#h2-0-using-jakarta-ee-9-1}
-------------------------------------------------

Just as with Jakarta EE 9, you can easily define all dependencies on Jakarta EE 9.1 for your web application by defining the `jakarta.jakartaee-web-api` artifact in your Maven pom file.

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;project xmlns="http://maven.apache.org/POM/4.0.0"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd"&gt;

   &lt;modelVersion&gt;4.0.0&lt;/modelVersion&gt;
   &lt;groupId&gt;fish.payara.jakarta.ee9.start&lt;/groupId&gt;
   &lt;artifactId&gt;hello&lt;/artifactId&gt;
   &lt;version&gt;1.0&lt;/version&gt;
   &lt;packaging&gt;war&lt;/packaging&gt;
   &lt;dependencies&gt;
      &lt;dependency&gt;
         &lt;groupId&gt;jakarta.platform&lt;/groupId&gt;
         &lt;artifactId&gt;jakarta.jakartaee-web-api&lt;/artifactId&gt;
         &lt;version&gt;9.1.0&lt;/version&gt;
         &lt;scope&gt;provided&lt;/scope&gt;
      &lt;/dependency&gt;
   &lt;/dependencies&gt;

   &lt;build&gt;
      &lt;finalName&gt;hello&lt;/finalName&gt;
   &lt;/build&gt;

   &lt;properties&gt;
      &lt;maven.compiler.source&gt;11&lt;/maven.compiler.source&gt;
      &lt;maven.compiler.target&gt;11&lt;/maven.compiler.target&gt;
      &lt;failOnMissingWebXml&gt;false&lt;/failOnMissingWebXml&gt;
      &lt;project.build.sourceEncoding&gt;UTF-8&lt;/project.build.sourceEncoding&gt;
   &lt;/properties&gt;

&lt;/project&gt;</pre>

If you are using Gradle as your build tool, you can define the version as:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">dependencies {
   providedCompile 'jakarta.platform:jakarta.jakartaee-web-api:9.1.0'
}

compileJava {
   targetCompatibility = '11'
   sourceCompatibility = '11'
}</pre>

All the API artifacts are still compiled against Java SE 8, so you can still use and compile your applications using JDK 8.

What Changed Between Jakarta EE 9 and 9.1? {#h2-1-what-changed-between-jakarta-ee-9-and-9-1}
--------------------------------------------------------------------------------------------

What is the real difference between Jakarta EE 9 and Jakarta EE 9.1? To be honest, there isn't one. Jakarta EE 9.1 is released to allow vendors to certify their implementations against Java SE 11. Since there was an important breaking change in Jakarta EE 9, the namespace change from *javax* to *jakarta*, no other changes were introduced in that version.

It is a good practice that when you make changes, you do so in incremental small steps. The introduction of the namespace was a large one, so that was the reason why the official support for Java SE 11 was deferred to Jakarta EE 9.1.

What About CORBA? {#h2-2-what-about-corba}
------------------------------------------

Between Java SE 8 and 11, not only was the modular structure of the JDK, and optionally your application, introduced but there was also the removal of several subsystems like CORBA and the XML support for Web Services.

Within the TCK, the Technology Compatibility Test Kit, or the several thousands of tests that prove an implementation is compatible with the specification, the tests around CORBA and XML Support in WebServices are optional. This means that a compatible implementation doesn't need to support this anymore in Jakarta EE 9.1.

But the [Payara Server Full Platform](https://www.payara.fish/) still supports them, just as we did with Jakarta EE 8. This means that all of your applications that run fine on Jakarta EE 8 (Payara Server 5) will also run on the Jakarta EE 9 version.

What About the Namespace Change? {#h2-3-what-about-the-namespace-change}
------------------------------------------------------------------------

Jakarta EE 9 and Jakarta EE 9.1 use the *jakarta* namespace. The specification itself does not require any backward compatibility. This is up to the implementations themselves. Payara Server includes the Eclipse transformer and converts an application using the javax namespace to the new jakarta namespace during the deployment of your application.

With Payara, you can decide when you are ready to move to the jakarta namespace in your application but you can use [Payara Server 6 (Alpha1),](https://blog.payara.fish/payara-server-community-6.2021.1.alpha1-certified-jakarta-ee-9.1-compatible) which is based on the jakarta namespace, if you want. Changing the namespace can be done with a search and replace command within your IDE, although there are still some package names in JDK itself that start with javax which have not changed. You can also have a look at the migration support of IntelliJ in version 2021.2 to perform the required changes for the namespace.

Should I Start using Jakarta EE 9.1? {#h2-4-should-i-start-using-jakarta-ee-9-1}
--------------------------------------------------------------------------------

Whether or not you should start using Jakarta EE 9.1 depends on your situation. The impact of the namespace change of Jakarta EE 9 is large. Many libraries are also using the Jakarta specification like Servlet or Rest, which means they also need to be updated. So in many cases, you will need to wait to migrate your application until all your dependencies have migrated, too. One of those dependencies that are in the progress of migrating is MicroProfile - with a planned Jakarta EE 9 compatible version in the last quarter of 2021.

This doesn't mean that you can't run your application on Java SE 11. Many implementations, including Payara Server, [supports running Jakarta EE 8 applications on JDK 11](https://blog.payara.fish/jdk-11-support-available-in-payara-platform-194).

Conclusion {#h2-5-conclusion}
-----------------------------

From a developer's perspective, Jakarta EE 9.1 is identical to Jakarta EE 9.

It only adds the possibility for implementations to certify on Java SE 11.

Many libraries that are used on top of Jakarta EE are still in the progress of migrating to the new namespace.

So our prediction is that by the release of Jakarta EE 10, everyone can make use of the updated Java Enterprise framework.
