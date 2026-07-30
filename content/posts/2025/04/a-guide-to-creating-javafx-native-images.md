---
title: "Building JavaFX Native Images"
slug: "a-guide-to-creating-javafx-native-images"
date: "2025-04-15T09:57:48+00:00"
lastmod: "2025-06-10T08:57:32+00:00"
description: "Discover how to turn JavaFX applications into native executables with GraalVM. Build native images locally or with GitHub Actions."
authors:
  - "catherine-edelveis"
image: "https://foojay.io/wp-content/uploads/2025/04/graalvm-docs.png"
categories:
  - "Performance"
tags:
related_posts:
  - "speed-up-your-spring-batch-with-native-image-and-graalvm"
  - "web-app-startup-in-3ms-with-rife2-and-graalvm"
  - "cross-platform-development-in-java-with-gluon-and-graalvm"
  - "cross-platform-development-in-java-with-gluon-and-graalvm-part-2"
enlighterjs: true
frozen: false
---

**Combining JavaFX-based applications with GraalVM Native Image will enable you to create platform-specific executables that don't require the JVM to run.**

In this article, we will look into two ways of turning JavaFX applications into native images: manually and with the Maven plugin. We will also learn to integrate this process into the CI/CD pipeline with GitHub Actions.

This article was originally published [here](https://bell-sw.com/blog/how-to-create-javafx-native-images/ "here").

The code for the project I use in this article is available [on GitHub](https://github.com/des-felins/raffle). It was built using [Liberica JDK Full](https://bell-sw.com/pages/downloads/#jdk-21-lts) that includes an OpenJFX bundle. It facilitates developing and building JavaFX applications as there's no need to add separate dependencies for JavaFX.

Table of Contents {#h2-0-table-of-contents}
-------------------------------------------

1. [Set Up GraalVM for JavaFX Native Compilation](#mcetoc_1inc190pt1d)
2. [Collect Metadata Using GraalVM Tracing Agent](#mcetoc_1inc190pt1e)
3. [Build a JavaFX Native Image with GraalVM](#mcetoc_1inc190pt1f)
4. [Use GitHub Actions to Automate JavaFX Releases](#mcetoc_1inc190pt1g)
5. [Conclusion](#mcetoc_1inc190pt1h)

Set Up GraalVM for JavaFX Native Compilation {#h2-1-set-up-graalvm-for-javafx-native-compilation}
-------------------------------------------------------------------------------------------------

IMPORTANT NOTE: you will need to provide the executable JAR of your application to the Native Image, so make sure that you create one before proceeding.

Regardless of whether you plan to use a build system plugin or manual compilation, you need to install a GraalVM distribution that supports JavaFX such as Liberica Native Image Kit (NIK).

[Download Liberica NIK Full](https://bell-sw.com/pages/downloads/native-image-kit/#nik-23-(jdk-21)) with OpenJFX for LTS JDK 17 or 21. You can also use package managers such as SDKMAN!

Install Liberica NIK. You can set $JAVA_HOME or create a \&NIK_HOME environmental variable to store the path to the installed package.

If you want to build native images using Maven, you need to add the plugin to your pom.xml file. It would be more convenient to add it to profiles:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">   &lt;profiles&gt;
        &lt;profile&gt;
            &lt;id&gt;native&lt;/id&gt;
            &lt;build&gt;
                &lt;plugins&gt;
                    &lt;plugin&gt;
                        &lt;groupId&gt;org.graalvm.buildtools&lt;/groupId&gt;
                        &lt;artifactId&gt;native-maven-plugin&lt;/artifactId&gt;
                        &lt;version&gt;0.10.5&lt;/version&gt;
                        &lt;extensions&gt;true&lt;/extensions&gt;
                        &lt;executions&gt;
                            &lt;execution&gt;
                                &lt;id&gt;build-native&lt;/id&gt;
                                &lt;goals&gt;
                                    &lt;goal&gt;compile-no-fork&lt;/goal&gt;
                                &lt;/goals&gt;
                                &lt;phase&gt;package&lt;/phase&gt;
                            &lt;/execution&gt;
                        &lt;/executions&gt;
                        &lt;configuration&gt;
                            &lt;mainClass&gt;com.java.MyApp&lt;/mainClass&gt;
                        &lt;/configuration&gt;
                    &lt;/plugin&gt;
                &lt;/plugins&gt;
            &lt;/build&gt;
        &lt;/profile&gt;
    &lt;/profiles&gt;</pre>

We're all set up, let's move on!

Collect Metadata Using GraalVM Tracing Agent {#h2-2-collect-metadata-using-graalvm-tracing-agent}
-------------------------------------------------------------------------------------------------

Java dynamic features and resources such as images or icons require special treatment when working with GraalVM Native Image. This is because the native-image compiler includes only those elements into the executable that are reachable at build time.

Luckily, there are several ways of overcoming this obstacle:

* If your JavaFX doesn't use any dynamic features, you can use the `-H:IncludeResources='com.fxapp.resources.images.*'` flag to make the compiler include resources into the executable.
* You can specify metadata manually in a JSON file.
* You can useTracing Agent to collect metadata automatically.

I would recommend using Tracing Agent because you cannot be 100% certain that your application doesn't use or won't use dynamic features. In addition, if you use FXML files to separate UI code from the business logic, simply adding these files to the native executable won't help. For Native Image, FXML files are just resources. The compiler doesn't know that they contain method calls.

Let's run the application with the agent to collect metadata.

Enable the Tracing Agent on the command line with the `-agentlib:native-image-agent` flag, specifying the output directory for JSON files with metadata:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$NIK_HOME/bin/java -agentlib:native-image-agent=config-output-dir=./agent-data -jar app.jar</pre>

The application will start, and you will need to run it through all execution paths so that the agent collects all required data. When the application exits, the JSON files will be automatically generated in the specified directory.

You can also use the `config-merge-dir` option instead of `config-output-dir` if you need to run the application several times to gather metadata. This option is also useful if you don't want to collect metadata from scratch every time you update the project.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$NIK_HOME/bin/java -agentlib:native-image-agent=config-merge-dir=./agent-data -jar app.jar</pre>

It is possible to enable the agent in the pom.xml. Follow the instructions described [here](https://graalvm.github.io/native-build-tools/latest/maven-plugin.html#agent-support-running-application).

Build a JavaFX Native Image with GraalVM {#h2-3-build-a-javafx-native-image-with-graalvm}
-----------------------------------------------------------------------------------------

Now that we have all necessary metadata on our hands, it's time to generate a native image.

If you do it manually, run the following command with a `-H:ConfigurationFileDirectories` flag specifying the path to the directory with metadata:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$NIK_HOME/bin/native-image -H:ConfigurationFileDirectories=./tracing-agent-data -jar target/lottery-1.0-SNAPSHOT.jar</pre>

After the compilation has finished, you will find the native executable in the /target directory. You can run it with

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">./target/myApp</pre>

If you use the plugin, add the ` block to the plugin configuration and specify the ``-H:ConfigurationFileDirectories` flag with the path to metadata:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;configuration&gt;
    &lt;imageName&gt;myApp&lt;/imageName&gt;
    &lt;outputDirectory&gt;target/native&lt;/outputDirectory&gt;
    &lt;mainClass&gt;com.java.MyApp&lt;/mainClass&gt;
    &lt;buildArgs&gt;
         &lt;buildArg&gt;-H:ConfigurationFileDirectories=./agent-data&lt;/buildArg&gt;
    &lt;/buildArgs&gt;
&lt;/configuration&gt;</pre>

By default, Maven places the native executable into the /target directory. But you can specify another directory under \`\`.

After that, run

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">./mvn -Pnative package</pre>

The native executable will be created in the /target/native directory.

You can now run your executable with:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">./target/native/myApp</pre>

Note that the native executable doesn't need JVM to run because it already contains all necessary Java classes.

Use GitHub Actions to Automate JavaFX Releases {#h2-4-use-github-actions-to-automate-javafx-releases}
-----------------------------------------------------------------------------------------------------

In contrast to the JAR files that can be run on any platform where Java is installed, native images are built for a specific architecture. So, if you have built a native image on Linux x66, it will run on Linux x64 only.

But what if we want to build an image for another platform? Or what if we release the application for several platforms? In this case, we can use GitHub Actions to build and release the images right from the GitHub.

When writing a workflow file for building JavaFX native images, there are several things to consider:

* You need to specify all operation systems and architectures, for which you want to build native images;
* Building native images for Linux requires installing additional packages: libasound2-dev, libavcodec-dev, libavformat-dev, libavutil-dev, libgl-dev, libgtk-3-dev, libpango1.0-dev, libxtst-dev;
* To build native images, you need to use a GraalVM distribution that supports JavaFX.

Let's look at the workflow file that takes all these factors into consideration (you can find this file under [native-image.yml](https://github.com/des-felins/lottery-fx/blob/main/.github/workflows/native-image.yml) in the repository):

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">name: Native Image

on:
  workflow_dispatch:
  push:
    branches:
      - main

jobs:
  build_non_win_images:
    name: 'Build Native Image ${{ matrix.platform }}'
    strategy:
      matrix:
        os: [ macos-latest, windows-latest, ubuntu-latest ]
        include:
          - os: 'ubuntu-latest'
            platform: 'linux-amd64'
          - os: 'macos-latest'
            platform: 'darwin-arm64'
          - os: 'macos-13'
            platform: 'darwin-amd64'
          - os: 'windows-latest'
            platform: 'win-amd64'
    runs-on: ${{matrix.os}}
    permissions:
      contents: write
    steps:
      - name: linux packages
        if: ${{ matrix.os == 'ubuntu-latest' }}
        run: sudo apt install libasound2-dev libavcodec-dev libavformat-dev libavutil-dev libgl-dev libgtk-3-dev libpango1.0-dev libxtst-dev

      - name: Checkout the repository
        uses: actions/checkout@v4

      - uses: graalvm/setup-graalvm@v1
        with:
          distribution: 'liberica'
          java-version: '21'
          java-package: 'jdk+fx'
          github-token: ${{ secrets.GITHUB_TOKEN }}
          cache: maven

      - name: Build
        shell: bash
        run: |
          ./mvnw -Pnative package

      - name: Archive Release
        uses: thedoctor0/<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="aed4c7de83dccbc2cbcfddcbee9e8099809b">[email&nbsp;protected]</a>
        with:
          type: 'zip'
          filename: "raffle-${{ matrix.platform }}.zip"
          directory: target/native

      - name: Upload binaries to release
        uses: svenstaro/upload-release-action@v2
        with:
          repo_token: ${{ secrets.GITHUB_TOKEN }}
          tag: ${{ github.ref }}
          release_name: native-image
          file: "target/native/raffle-${{ matrix. platform }}.zip"
          overwrite: true
          make_latest: true</pre>

What do we have here?

First of all, at the *Build Native Image* stage, we specify the OSs and platforms, for which to build a native image.

Then, we add an additional step 'linux packages' with an if statement to install required packages for Linux.

At this stage, we also specify a GraalVM distribution to compile the images. In our case, it is Liberica NIK for Java 21 that supports JavaFX.

At the *Build* stage, we run `./mvnw -Pnative` package to build the native images.

At the *Archive Release* stage, we archive the native image specifying the platform in the file name. Here, we also specify the directory where the native image was created. Note that if the image will be generated by default in the target directory, the whole directory will be packaged into the ZIP file. So, that's why we specified a separate directory for the native image in the pom.xml in the section above (target/native).

At the *Upload binaries to release* stage, we take the archived native image and load it into the release.

That's it! Make sure that you [created a release on GitHub](https://docs.github.com/en/repositories/releasing-projects-on-github/managing-releases-in-a-repository) and commit the changes: the workflow will be triggered automatically.

After the workflow completes successfully, you will find the binaries for all platforms under [Releases.](https://github.com/des-felins/raffle/releases)

Conclusion {#h2-5-conclusion}
-----------------------------

Creating JavaFX Native Images using GraalVM helps to improve the performance and portability of Java desktop applications by eliminating the need for a JVM at runtime.

In this guide, we explored the entire process, from setting up GraalVM and configuring the necessary metadata to compiling the native executable and automating builds with GitHub Actions.

If you want to explore JavaFX and native compilation further, check out the official [GraalVM](https://www.graalvm.org/latest/docs/) and [Liberica Native Image Kit](https://docs.bell-sw.com/liberica-nik/) documentation for further optimizations.

Also, subscribe to our letter for more content on JavaFX, native images, and all things Java!
