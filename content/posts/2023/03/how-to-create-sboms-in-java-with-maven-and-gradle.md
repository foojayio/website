---
title: "How to create SBOMs in Java with Maven and Gradle"
slug: "how-to-create-sboms-in-java-with-maven-and-gradle"
date: "2023-03-17T09:08:28+00:00"
lastmod: "2023-03-17T09:21:56+00:00"
description: "Java is a compiled language, so you should create an SBOM whenever you build a release version of your application. Find out more here!"
canonical: "https://snyk.io/blog/create-sboms-java-maven-gradle/"
authors:
  - "bmvermeer"
image: "https://foojay.io/wp-content/uploads/2020/11/snyk-logo-2.png"
categories:
  - "DevOps"
  - "Security"
  - "Snyk"
tags:
related_posts:
  - "sboms-first-steps-in-a-new-journey-for-developers"
  - "sboms-and-software-composition-analysis"
  - "making-sboms-threats-and-modelling-them-a-piece-of-cake"
enlighterjs: true
frozen: false
---

When building applications in Java, we highly depend on external libraries and frameworks. And each Java package that is imported likely also depends on more libraries. This means that the amount of Java packages included in your application is often not really transparent.

As a developer, these nested (transitive) dependencies create the problem that you probably do not know all the libraries you are actually using.

Recently, we discussed why and how we should maintain our dependencies carefully. In the article [Best practices for managing Java dependencies](https://snyk.io/blog/best-practices-for-managing-java-dependencies/), I discussed the options and tools available for setting up a dependency management strategy.

But what if you deliver your Java application to a customer?

How do they know what dependencies are included?

More importantly, how can they check if the dependencies are not vulnerable to security issues?

The answer is a **software bill of materials**.

What is an SBOM? {#h2-0-what-is-an-sbom}
----------------------------------------

A [software bill of materials](https://snyk.io/learn/software-bill-of-materials/), often abbreviated as SBOM, is a list of all software components used in an application. The SBOM is made up of third-party open-source libraries, vendor-provided packages, and first-party artifacts built by the organization. You can basically see it as the full list of ingredients for your applications.

But be careful to not confuse an SBOM with Maven's Bill Of Materials (BOM). In Maven, a BOM is a special kind of POM file where we can centralize dependencies for an application. In most cases, these dependencies work well together and should be used as a set, like we see in BOMs used in Spring.

An SBOM is something you create next to your application, so any user or client has a uniform way to find out what your application is using under the hood.

Why should I create an SBOM? {#h2-1-why-should-i-create-an-sbom}
----------------------------------------------------------------

There are multiple reasons for creating an SBOM. First of all, you create transparency about what how your application is containing. In most Java applications, 80% to 90% of the produced binary consists of other Java packages like libraries and frameworks.

Nowadays, we see a lot of [security issues in the supply chain](https://snyk.io/blog/preventing-malicious-packages-and-supply-chain-attacks-with-snyk/). The dependencies you use are part of your supply chain, so if a problem is found in one of these libraries, you need to know if an application is vulnerable.

Take the recent [Log4Shell](https://snyk.io/log4j-vulnerability-resources/) and [Spring4Shell](https://snyk.io/blog/spring4shell-zero-day-rce-spring-framework-explained/) vulnerabilities where certain commonly-used packages were compromised. When an SBOM is provided as part of every release, end users and clients can easily check if vulnerabilities impact them.

The creation of SBOMs is expected to be something that will be common practice, or sometimes even mandatory, when you deliver software. Therefore we feel it is important to cover how to create these SBOMs for your Java project, which we cover in the remainder of this article.

SBOM standards: SPDX and CycloneDX {#h2-2-sbom-standards-spdx-and-cyclonedx}
----------------------------------------------------------------------------

Currently, there are multiple standards for SBOMs. The two most commonly used are SPDX and CycloneDX. Both of these standards provide a way to show the components your application contains.

The Software Package Data Exchange (SPDX) is a Linux Foundation collaborative project that provides an open standard for communicating software bill of material information, including provenance, licensing, security, and other related information.

The SPDX specification is recognized as the international open standard for security, license compliance, and other software supply chain artifacts as ISO/IEC 5962:2021.

CycloneDX is a SBOM standard from the OWASP foundation designed for application security contexts and supply chain component analysis, providing an inventory of all first-party and third-party software components.

The specification is rich and extends beyond software libraries to standards such as software as a service bill of materials (SaaSBOM), Vulnerability Exploitability Exchange (VEX), and more. The CycloneDX project provides standards in XML, JSON, and Protocol Buffers, as well as a large [collection of official and community-supported tools](https://cyclonedx.org/tool-center/) that create or interoperate with the standard.

When to create an SBOM in Java {#h2-3-when-to-create-an-sbom-in-java}
---------------------------------------------------------------------

Java is a compiled language, so you should create an SBOM whenever you build a release version of your application.

Therefore, creating an SBOM when using one of the Java build systems makes a lot of sense, since your build system downloads all the packages you need to compile and build your application.

By using a plugin for Maven or Gradle, you can easily create SBOMs with every release of your binary either on a single machine or as part of your CI pipeline

Creating a Java SBOM with Maven {#h2-4-creating-a-java-sbom-with-maven}
-----------------------------------------------------------------------

### CycloneDX plugin for Maven {#h3-5-cyclonedx-plugin-for-maven}

There is a CylconeDX plugin available on Maven central and [Github](https://github.com/CycloneDX/cyclonedx-maven-plugin) that appears to be well-maintained and commonly used.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="false" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;plugins&gt;
   &lt;plugin&gt;
       &lt;groupId&gt;org.cyclonedx&lt;/groupId&gt;
       &lt;artifactId&gt;cyclonedx-maven-plugin&lt;/artifactId&gt;
       &lt;version&gt;2.7.1&lt;/version&gt;
       &lt;executions&gt;
           &lt;execution&gt;
               &lt;phase&gt;package&lt;/phase&gt;
               &lt;goals&gt;
                   &lt;goal&gt;makeAggregateBom&lt;/goal&gt;
               &lt;/goals&gt;
           &lt;/execution&gt;
       &lt;/executions&gt;
       &lt;configuration&gt;
           &lt;projectType&gt;library&lt;/projectType&gt;
           &lt;schemaVersion&gt;1.4&lt;/schemaVersion&gt;
           &lt;includeBomSerialNumber&gt;true&lt;/includeBomSerialNumber&gt;
           &lt;includeCompileScope&gt;true&lt;/includeCompileScope&gt;
           &lt;includeProvidedScope&gt;true&lt;/includeProvidedScope&gt;
           &lt;includeRuntimeScope&gt;true&lt;/includeRuntimeScope&gt;
           &lt;includeSystemScope&gt;true&lt;/includeSystemScope&gt;
           &lt;includeTestScope&gt;false&lt;/includeTestScope&gt;
           &lt;includeLicenseText&gt;false&lt;/includeLicenseText&gt;
           &lt;outputReactorProjects&gt;true&lt;/outputReactorProjects&gt;
           &lt;outputFormat&gt;all&lt;/outputFormat&gt;
           &lt;outputName&gt;CycloneDX-Sbom&lt;/outputName&gt;
       &lt;/configuration&gt;
   &lt;/plugin&gt;
&lt;/plugins&gt;</pre>

You can configure the CycloneDX plugin in different ways. In this case, I bound the `makeAggregateBom` goal of the plugin to the package phase of Maven. After my JAR is created, the plugin will create an SBOM, taking aggregation into account. It excludes the test dependencies and releases the SBOM in both XML and JSON format in my target folder.

All dependencies, both direct and transitive, are mentioned in the SBOM individually like below. The `jackson-databind` package, in this case, was transitively included in my application via `sprint-boot-starter-web`.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;component type="library" bom-ref="pkg:maven/com.fasterxml.jackson.core/<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="ff959e9c948c9091d29b9e8b9e9d96919bbfcdd1ceccd1cb">[email&nbsp;protected]</a>?type=jar"&gt;
 &lt;publisher&gt;FasterXML&lt;/publisher&gt;
 &lt;group&gt;com.fasterxml.jackson.core&lt;/group&gt;
 &lt;name&gt;jackson-databind&lt;/name&gt;
 &lt;version&gt;2.13.4&lt;/version&gt;
 &lt;description&gt;General data-binding functionality for Jackson: works on core streaming API&lt;/description&gt;
 &lt;hashes&gt;
   &lt;hash alg="MD5"&gt;03cb7aea126610e4c96ca6d14d75cc55&lt;/hash&gt;
   &lt;hash alg="SHA-1"&gt;98b0edfa8e4084078f10b7b356c300ded4a71491&lt;/hash&gt;
   &lt;hash alg="SHA-256"&gt;c9faff420d9e2c7e1e4711dbeebec2506a32c9942027211c5c293d8d87807eb6&lt;/hash&gt;
   &lt;hash alg="SHA-512"&gt;23f32026b181c6c71efc7789a8420c7d5cbcfb15f7696657e75f9cbe3635d13a88634b5db3c344deb914b719d60e3a9bfc1b63fa23152394e1e70b8e7bcd2116&lt;/hash&gt;
   &lt;hash alg="SHA-384"&gt;e25e844575891b2f3bcb2fdc67ae9fadf54d2836052c9ea2c045f1375eaa97e4780cd6752bef0ebc658fa17400c55268&lt;/hash&gt;
   &lt;hash alg="SHA3-384"&gt;e6955877c2c27327f6814f06d681118be2ae1a36bc5ff2e84ad27f213203bf77c347ba18d9abc61d5f1c99b6e81f6c2d&lt;/hash&gt;
   &lt;hash alg="SHA3-256"&gt;88b12b0643a4791fa5cd0c5e30bc2631903870cf916c8a1b4198c856fd91e5f4&lt;/hash&gt;
   &lt;hash alg="SHA3-512"&gt;7e86a69bcf7b4c8a6949acce0ec15f33b74d5ac604f23cd631ec16bfdfd70d42499028b9d062648b31d7a187ea4dc98ec296a329f4cfd4952744ed1281fa9d9a&lt;/hash&gt;
 &lt;/hashes&gt;
 &lt;licenses&gt;
   &lt;license&gt;
     &lt;id&gt;Apache-2.0&lt;/id&gt;
   &lt;/license&gt;
 &lt;/licenses&gt;
 &lt;purl&gt;pkg:maven/com.fasterxml.jackson.core/<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="fb919a9890889495d69f9a8f9a9992959fbbc9d5cac8d5cf">[email&nbsp;protected]</a>?type=jar&lt;/purl&gt;
 &lt;externalReferences&gt;&lt;reference type="vcs"&gt;&lt;url&gt;http://github.com/FasterXML/jackson-databind&lt;/url&gt;&lt;/reference&gt;&lt;reference type="website"&gt;&lt;url&gt;http://fasterxml.com/&lt;/url&gt;&lt;/reference&gt;&lt;reference type="distribution"&gt;&lt;url&gt;https://oss.sonatype.org/service/local/staging/deploy/maven2/&lt;/url&gt;&lt;/reference&gt;&lt;/externalReferences&gt;
&lt;/component&gt;</pre>

### SPDX plugin for Maven (prototype) {#h3-6-spdx-plugin-for-maven-prototype}

For SPDX, there is a [Maven plugin](https://github.com/spdx/spdx-maven-plugin) as well. However, this is still marked as a prototype. In the example below, I used the latest version (at the time of writing) with a similar configuration as mentioned in the GitHub README.

Additionally, I bound the SPDX creation task to the package phase, similar to the CycloneDX example.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;plugin&gt;
   &lt;groupId&gt;org.spdx&lt;/groupId&gt;
   &lt;artifactId&gt;spdx-maven-plugin&lt;/artifactId&gt;
   &lt;version&gt;0.6.1&lt;/version&gt;
   &lt;executions&gt;
       &lt;execution&gt;
           &lt;id&gt;build-spdx&lt;/id&gt;
           &lt;phase&gt;package&lt;/phase&gt;
           &lt;goals&gt;
               &lt;goal&gt;createSPDX&lt;/goal&gt;
           &lt;/goals&gt;
       &lt;/execution&gt;
   &lt;/executions&gt;
&lt;/plugin&gt;</pre>

The output by default for this version of the plugin is located in `/target/site/{groupId}_{artifactId}-{version}.spdx.json`. As the file extension already suggests, the default output is JSON.

Browsing through the output, it surprised me that it only contained the top-level dependencies and not the transitive. Now, this plugin is marked as a prototype, so that could be why. Additionally, I might be doing something wrong. However, reading the docs did not give me a clear hint.

### SPDX CLI tool for Maven {#h3-7-spdx-cli-tool-for-maven}

Alternatively, there is command line tool available called [spdx-sbom-generator](https://github.com/opensbom-generator/spdx-sbom-generator). This CLI tool can generate SPDX SBOMs for many package managers, including Maven for Java applications. Gradle is currently not supported.

Calling this tool from the command line without any parameter in the root of my application creates an SBOM for me in the SPDX format. Other outputs like JSON are also supported by using a parameter.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">./spdx-sbom-generator</pre>

This generated SBOM seems to have all transitive dependencies individually mentioned, as I assumed it should.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">##### Package representing the jackson-databind

PackageName: jackson-databind
SPDXID: SPDXRef-Package-jackson-databind-2.13.4
PackageVersion: 2.13.4
PackageSupplier: Organization: jackson-databind
PackageDownloadLocation: https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind/2.13.4
FilesAnalyzed: false
PackageChecksum: SHA1: 7d03e73aa50d143b3ecbdea2c0c9e158e5ed8021
PackageHomePage: NOASSERTION
PackageLicenseConcluded: NOASSERTION
PackageLicenseDeclared: NOASSERTION
PackageCopyrightText: NOASSERTION
PackageLicenseComments: NOASSERTION
PackageComment: NOASSERTION

Relationship: SPDXRef-Package-jackson-databind-2.13.4 DEPENDS_ON SPDXRef-Package-jackson-annotations-2.13.4
Relationship: SPDXRef-Package-jackson-databind-2.13.4 DEPENDS_ON SPDXRef-Package-jackson-core-2.13.4</pre>

If you want to create SBOMs in the SPDX format I would suggest this tool over the prototype plugin.

Creating a Java SBOM with Gradle {#h2-8-creating-a-java-sbom-with-gradle}
-------------------------------------------------------------------------

Now let's take a look at Gradle. While Gradle is less used than Maven, it is still used a substantial amount, and we can definitely say it is a well-adopted build tool in the ecosystem.

### CycloneDX for Gradle {#h3-9-cyclonedx-for-gradle}

There is a CyconeDX plugin available for Gradle. Just like the Maven plugin we discussed earlier, the Gradle plugin is released by the [CycloneDX organization on Github](https://github.com/CycloneDX/cyclonedx-gradle-plugin) with some of the same maintainers as the Maven plugin.

To use the plugin just add it to your plugin block in your Gradle file:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">plugins {
   id 'org.cyclonedx.bom' version '1.7.2'
}</pre>

You can configure the plugin with a `cyclonedxBom` block like below:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">cyclonedxBom {
   includeConfigs = ["runtimeClasspath"]
   skipConfigs = ["compileClasspath", "testCompileClasspath"]
   projectType = "application"
   schemaVersion = "1.4"
   destination = file("build/reports")
   outputName = "CycloneDX-Sbom"
   outputFormat = "all"
   includeBomSerialNumber = true
   componentVersion = "2.0.0"
}</pre>

In this example, I also added the line `build.finalizedBy('cyclonedxBom')` at the end of my Gradle file. Now it will automatically call the `cyclonedxBom` target after building my application and behave similarly to the Maven plugin. Obviously, this is up to you if and how you want to connect the plugin target.

The output is as expected and similar to what we have seen with the Maven plugin. With the configuration shown above, you will find both a JSON and an XML output of the SBOM in your project's `build` folder. So, this plugin is an excellent option for Gradle users to create SBOMs

### SPDX for Gradle {#h3-10-spdx-for-gradle}

Unfortunately, we could not find a real plugin to create SPDX-type SBOMs for Gradle projects. Also, third-party CLI tools are either not available or are not correctly working for Gradle-based Java projects.

So, for now, there is no easy way to generate SPDX SBOMs for Gradle.

Creating SBOMs for your Java projects {#h2-11-creating-sboms-for-your-java-projects}
------------------------------------------------------------------------------------

Building an SBOM when you are building your Java project seems like a practice that will get more popular soon.

Letting your build system take care of this makes a lot of sense.

For both Maven and Gradle, plugins are available that create the SBOMs when building your application.

Creating SBOMs together with your Java build artifacts is straightforward using these plugins, as we showed above.
