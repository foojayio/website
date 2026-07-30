---
title: "Creating SBOMs with the Snyk CLI"
slug: "creating-sboms-with-the-snyk-cli"
date: "2025-03-07T12:25:36+00:00"
lastmod: "2025-03-10T15:17:53+00:00"
description: "The software bill of materials (SBOM) is quickly becoming an essential aspect of open source security and compliance. In this post, we'll delve into what - by Brian Vermeer"
canonical: "https://snyk.io/blog/creating-sboms-snyk-cli/"
authors:
  - "bmvermeer"
image: "https://res.cloudinary.com/snyk/image/upload/f_auto,w_960,q_auto/v1738787589/Creating_SBOMs_with_the_SNyk_CLI_-_original_fu9ead.png"
categories:
  - "Security"
  - "Snyk"
  - "Tools"
  - "Uncategorized"
tags:
related_posts:
enlighterjs: true
frozen: false
---

The software bill of materials (SBOM) is quickly becoming an essential aspect of open source security and compliance. In this post, we'll delve into what SBOMs are, why they're necessary, and their role in open source security.

What are SBOMs? {#h2-0-what-are-sboms}
--------------------------------------

A software Bill of Materials (SBOM) is a comprehensive inventory of all components used in a software product. They include all the necessary details about each component, such as their names, versions, and licensing information. SBOMs can be seen as the ingredients list for software, providing complete transparency into what makes up a software product.

Consider a simple Python project with various dependencies. A simplified example of an SBOM for this project might look like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">1[
2    {
3        "component_name": "numpy",
4        "version": "1.21.0",
5        "license": "BSD"
6    },
7    {
8        "component_name": "pandas",
9        "version": "1.3.0",
10        "license": "BSD"
11    }
12]</pre>

***Disclaimer:**
The code above is a simplified snippet of a possible SBOM. This example is for illustration purposes only and will not work when scanning since it is incomplete.*

Why do we need SBOMs? {#h2-1-why-do-we-need-sboms}
--------------------------------------------------

SBOMs serve a crucial role in understanding the composition of our software. But in the field of open source development, SBOMs play an even more vital role. Open source components often have their own dependencies, leading to a complex web of interconnected components. SBOMs help untangle this web, allowing us to understand the full scope of our exposure to potential security risks or compliance issues that come from third-party dependencies in our apps.

Delivering an SBOM alongside the created artifact is crucial because it enables whoever is using the software to asses the security status of the project. If a major vulnerability on a specific open source package is disclosed, like we have seen with [++Log4Shell++](https://snyk.io/blog/log4j-rce-log4shell-vulnerability-cve-2021-44228/) and [++Spring4Shell++](https://snyk.io/blog/spring4shell-zero-day-rce-spring-framework-explained/), all consumers of the vulnerable components can now determine if this security vulnerability impacts them.

SBOMs are a valuable add-on to any piece of software that should be provided with every release. They provide transparency, enhance security and compliance, and contribute to higher-quality software.

Creating SBOMs with the Snyk CLI {#h2-2-creating-sboms-with-the-snyk-cli}
-------------------------------------------------------------------------

Snyk's command line interface (CLI) is an open source security tool that enables developers and DevOps professionals to find, fix, and monitor known vulnerabilities in open source dependencies. The Snyk CLI supports a broad range of programming languages and package managers --- including JavaScript (npm, yarn), Python (pip), Java (Maven), .NET (NuGet), Ruby (RubyGems), PHP (Composer), and more. The Snyk CLI can be used locally or in a pipeline for SAST, SCA, container, and IaC scanning on a software project.

And now, it can also create SBOMs for your projects.

### Installing the Snyk CLI {#h3-3-installing-the-snyk-cli}

To get started with the Snyk CLI, you need to install it in your development environment. Below is a simple guide on how to install the Snyk CLI using npm. For more information or alternative ways to install the Snyk CLI, [++check out our user documentation.++](https://docs.snyk.io/snyk-cli/install-or-update-the-snyk-cli)

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">npm install -g snyk</pre>

After installing the Snyk CLI, you need to authenticate your account using the `snyk auth` command. This will open a web browser for you to log in or sign up for a Snyk account.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">snyk auth</pre>

Alternatively, you can set your Snyk token as an environment variable, which is the recommended way to use the CLI in a CI/CD pipeline. Check our handy [++CLI cheat sheet++](https://snyk.io/blog/snyk-cli-cheat-sheet/) or the [++official documentation++](https://docs.snyk.io/snyk-cli) for more information.

### Generate SBOMs using the Snyk CLI {#h3-4-generate-sboms-using-the-snyk-cli}

Once the CLI is operational and connected to an enterprise Snyk account, it can start creating SBOMs for your software projects with the following command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">snyk sbom --format=&lt;cyclonedx1.4+json|cyclonedx1.4+xml|cyclonedx1.5+json|cyclonedx1.5+xml|cyclonedx1.6+json|cyclonedx1.6+xml|spdx2.3+json&gt;</pre>

The `--format` option is required and specifies the output format for the SBOM to be produced. The choices are between CylconeDX, XML, or SPDX in JSON format.

You can save the SBOMs to a file by using the `--json-file-output=<OUTPUT_FILE_PATH>` flag to export SBOMs JSON output to a JSON file.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">snyk sbom --format=cyclonedx1.4+json --json-file-output=mysbom.json</pre>

Running this from the root directory of your project will give you an SBOM file that you can ship together with your artifact.

### Multiple projects {#h3-5-multiple-projects}

The Snyk CLI uses the package manifest file of your build system to determine the dependency tree and, therefore, the input of the SBOM. By default, the CLI stops after finding one manifest file. However, you may have more than one manifest file and more than one build system in your project. For example, you might have a project with a Java backend using Maven and a Node.js frontend using npm. By adding the `--all-projects` flag to the SBOM command, the Snyk CLI will traverse through your project looking for manifest files and add them to the result or your SBOM output.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">snyk sbom --format=cyclonedx1.4+json --all-projects --json-file-output=mysbom.json</pre>

The default depth for searching for manifest files is four, this is configurable using the `--detection-depth` flag. In addition, it is also possible to exclude specific files with the `--exclude` flag.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">snyk sbom --format=cyclonedx1.4+json --all-projects --detection-depth=3 --exclude=package.json --json-file-output=mysbom.json</pre>

Please be aware that there is a large number of command line flags available for specific ecosystems to handle --- such as multi-module Maven files, configuration attributes in Gradle, Yarn workspaces, etc. For a full overview of all possible flags for Snyk SBOM, check our extensive [++documentation page++](https://docs.snyk.io/snyk-cli/commands/sbom).

### Automating SBOM generation with the Snyk CLI {#h3-6-automating-sbom-generation-with-the-snyk-cli}

Automating software bill of materials generation with the Snyk CLI is a vital step for enhancing security and compliance in the CI/CD pipeline. By integrating the Snyk CLI into your build process, you can automatically generate a comprehensive SBOM each time your code is built. This SBOM lists all dependencies, including transitive dependencies.

As code moves through the CI/CD pipeline, Snyk can not only find security vulnerabilities but also ensure that an up-to-date SBOM is generated and delivered with every build release. This automation not only streamlines the process of maintaining a secure and up-to-date SBOM but also embeds security practices of your software development lifecycle, making it easier to comply with regulatory requirements and industry standards while building trust with your customers.

Analyzing SBOMs {#h2-7-analyzing-sboms}
---------------------------------------

Creating SBOMs is an effective and straightforward way to use the Snyk CLI. But what if you need to consume an SBOM and check if there are known vulnerabilities used in a software package? There are currently a few options available:

### Snyk CLI SBOM Test {#h3-8-snyk-cli-sbom-test}

The Snyk Cli is not only capable of creating an SBOM for your project, it can also scan it. If the Snyk CLI is connected to an enterprise account your can use the Snyk CLI to point to an SBOM file like below:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">snyk sbom test --file=mysbom.json --experimental</pre>

At the time of writing, this is still an experimental feature hence the \`--experimental\` flag. Also be aware that it currently only accepts JSON files in UTF-8 for

* CycloneDX: version 1.4, 1.5, and 1.6
* SPDX: JSON version 2.3

Please take a look at the [++Snyk CLI SBOM Test Documentation++](https://docs.snyk.io/snyk-cli/commands/sbom-test) for the latest updates on this feature and other available options.
![](https://res.cloudinary.com/snyk/image/upload/f_auto,w_960,q_auto/v1738787589/Creating_SBOMs_with_the_SNyk_CLI_-_original_fu9ead.png)

#### Bomber

Bomber is an open source application that scans SBOMs for security vulnerabilities. Snyk is one of the integrated providers in `bomber` to scan SBOMs.

To install `bomber`, download the [++latest release++](https://github.com/devops-kung-fu/bomber/releases)from the official GitHub repository, use Homebrew for macOS, or the `dpkg` tool for Linux.  

**Homebrew (macOS):**

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">brew tap devops-kung-fu/homebrew-tap
brew install devops-kung-fu/homebrew-tap/bomber</pre>

**Dpkg (Linux):**

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">dpkg -i bomber_0.4.1_linux_arm64.deb</pre>

To run `bomber` from the command line with Snyk, you'll need to provide a Snyk API token. You can retrieve this from the Snyk web interface. Otherwise, when the CLI is installed on your local machine, you can run `snyk config get api`.

Once you have the Snyk API token, you can run `bomber` like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">bomber scan --provider snyk --token xxx mysbom.json</pre>

The output will show you all known vulnerabilities on your screen.
![Bomber SBOM scanner with the Snyk integration provider for Java Maven dependencies.](https://res.cloudinary.com/snyk/image/upload/f_auto,w_2560,q_auto/v1707410787/blog-creating-sboms-bomber.jpg)

Supplying up-to-date SBOMs with Snyk {#h2-9-supplying-up-to-date-sboms-with-snyk}
---------------------------------------------------------------------------------

The Snyk CLI can help you create up-to-date SBOMs for every build of your software. It's good practice (and in some cases even required) to provide an SBOM with a built artifact to be compliant.

Using the Snyk CLI to automate SBOM production in your pipeline is a straightforward way to make this work. To wrap up, I'd like to recommend the following next steps for you when working with SBOMs in your projects:

* [**++Using SBOMs with Snyk++**](https://docs.snyk.io/snyk-cli/commands/sbom)
* [**++A comprehensive guide on generating SBOMs for JavaScript and Node.js projects++**](https://snyk.io/blog/generate-sbom-javascript-node-js-applications/)
* [**++Using the open source Parlay tool to enrich your SBOMs++**](https://snyk.io/blog/introducing-parlay/)
* [**++How to create SBOMs in Java with Maven and Gradle++**](https://snyk.io/blog/create-sboms-java-maven-gradle/)
