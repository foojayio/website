---
title: "Building Project Panama's jextract tool by yourself"
slug: "building-project-panamas-jextract-tool-by-yourself"
date: "2022-06-09T14:22:47+00:00"
lastmod: "2022-07-09T19:42:01+00:00"
description: "Learn about jextract, which can generate Java binding code that represents native functions or variables (symbols) from C libraries."
authors:
  - "carldea"
image: "/images/posts/2022/06/building-project-panamas-jextract-tool-by-yourself/Screen-Shot-2022-06-06-at-3.03.47-PM.png"
categories:
  - "Developer Tools"
  - "JEPs"
  - "Performance"
  - "Project Panama"
  - "Tutorials"
tags:
related_posts:
  - "project-panama-for-newbies-part-1"
  - "project-panama-for-newbies-part-2"
  - "java-panama-polyglot-part1"
  - "java-panama-polyglot-rust-part-4"
enlighterjs: true
frozen: false
---

> Absorb what is useful, discard what is useless and add what is specifically your own. -- Bruce Lee

Do you want to build Project Panama's [Jextract](https://github.com/openjdk/jextract) tool by yourself? I can show you how!

If you've seen any of my past [articles](https://foojay.io/today/project-panama-for-newbies-part-3/) on Project Panama I've mentioned a really convenient tool called `jextract` that can generate Java binding code that represents native functions or variables (symbols) from C libraries.

This alleviates the developer's need of creating binding code by hand. By passing in a C header file (.h extension) `jextract` can generate source code or compiled Java classes.

**Did you know jextract will not be included in OpenJDK releases?**

For those who are not familiar with the state of Java's Project Panama \& the `jextract` tool let me explain. As of this writing the Java enhancement proposal **[JEP 424](https://openjdk.java.net/jeps/424)** will be making the foreign function and memory access APIs available as [preview](https://openjdk.java.net/jeps/424) features in [JDK 19-ea](https://jdk.java.net/19/) (early access) release and will **not** **include** the **`jextract`** tool.

**Note:** To enable preview features and native access do the following when running a Java program.

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">java --enable-preview --source 19 --enable-native-access=ALL-UNNAMED MyJavaApp.java</pre>

By enabling and using JDK 19's preview features you can begin to kick the tires and provide [feedback](https://mail.openjdk.java.net/mailman/listinfo/panama-dev) to the engineers and community before it becomes final in the GA release of Java.

Prior to JEP 424 the early access builds of the OpenJDK used to contain the `jextract` tool as part of the JDK.

However, the decision was made to have the `jextract` tool become its own project over at [GitHub](https://github.com/openjdk/jextract) and will not be included in builds of the [OpenJDK](https://github.com/openjdk/jdk).

Having said this, you'll want to be able to build the `jextract` tool yourself. Of course you can wait till a build is available, but why wait? In this article I will walk you through the process on how to build `jextract` yourself.

Assumptions {#h2-0-assumptions}
-------------------------------

This article assumes you know what is Java's Foreign Function and Memory Access APIs (APIs from Project Panama) and basic knowledge of the following.

* Bash commands
* Git is installed
* Git commands

Requirement {#h2-1-requirement}
-------------------------------

* [JDK 19 EA](https://jdk.java.net/19/) - The early access build of JDK 19 (Preview Release)
* [jextract](https://github.com/openjdk/jextract) - A tool to generate binding code to allow developers to easily access native symbols. The `jextract` tool is now on GitHub. The master branch is a in synch with the latest OpenJDK version. If you would like to obtain a past build for a version check jdk\<version\>. At this time a branch `jdk18` is a build.
* [LLVM](https://github.com/llvm/llvm-project/releases/tag/llvmorg-13.0.0) - A set of compiler tools to optimize and statically compile various languages into native machine code.
* [Gradle](https://gradle.org) (optional) - The Gradle build tool to build the `jextract` project.
* Git

Installing Software {#h2-2-installing-software}
-----------------------------------------------

First on the list of required software is JDK 19 EA. If you are a fan of using SDKMan then do the following:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">sdk install java 19.ea.25-open</pre>

Note: If you have installed the JDK using SDKMan than you can skip to the **jextract at GitHub** section.

If you want to do install the JDK the old fashioned way follow the instructions below:

Head over to the early access release of **JDK 19** site to [download the latest build](https://jdk.java.net/19/) for your operating system as shown below.
![](/images/posts/2022/06/building-project-panamas-jextract-tool-by-yourself/Screen-Shot-2022-07-09-at-3.24.37-PM.png) OpenJDK JDK 19 Early-Access Builds Download Page

After [downloading JDK 19](https://jdk.java.net/19/) decompress the file into a directory and set the `JAVA_HOME` and `PATH` environment variables as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># MacOS 
$ export JAVA_HOME=&lt;untarred_dir&gt;/jdk-19.jdk/Contents/Home
$ export PATH=$JAVA_HOME/bin:$PATH

# Linux
$ export JAVA_HOME=&lt;untarred_dir&gt;/jdk-19
$ export PATH=$JAVA_HOME/bin:$PATH

# Windows
C:\&gt; set JAVA_HOME=&lt;unzipped_dir&gt;\jdk-19
C:\&gt; set PATH=%JAVA_HOME%\bin;%PATH%</pre>

Of course if you choose to make environment variables persistent you'll need to add them to the `.bashrc` or `.bash_profile` file of your Linux or MacOS environment. On the Windows operating system you will want to add or update environment variables in the control panel's `System Properties` `->` `Environment Variables`.

### jextract at GitHub {#h3-3-jextract-at-github}

After setting up JDK 19 you can fork / clone the GitHub project `jextract` with the following command:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">git clone <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e5828c91a5828c918d9087cb868a88">[email&nbsp;protected]</a>:openjdk/jextract.git
cd jextract</pre>

**Note:** It is preferable that you fork the project and then clone your fork of the project. That way you can provide pull requests whenever you find a bug to fix or a proposed enhancement to the project. But if you just want to build from master branch or a previous branch you can simply clone `jextract`'s main repo as shown below:
![](/images/posts/2022/06/building-project-panamas-jextract-tool-by-yourself/Screen-Shot-2022-06-06-at-3.03.47-PM.png)

Assuming your still in the directory of the repository you've just cloned (`./jextract`) next you'll download and install LLVM before building the `jextract` project.

### LLVM {#h3-4-llvm}

Next, you'll need to [download LLVM](https://github.com/llvm/llvm-project/releases/tag/llvmorg-13.0.0) (version 13.0.0) for your particular operating system and then decompress it into a directory. Afterwards, you'll need to execute the following command to build the `jextract` tool.

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">sh ./gradlew -Pjdk19_home=&lt;jdk19_home_dir&gt; -Pllvm_home=&lt;libclang_dir&gt; clean verify</pre>

**Note:** Substitute the following place holders `<jdk19_home_dir>` and `<libclang_dir>` with the `JAVA_HOME`'s directory and LLVM's installed directory (clang+llvm-13.0.0-\*) respectively. An example on my MacOS terminal is shown below.

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">sh ./gradlew \
   -Pjdk19_home=/Users/cdea/sdks/jdk-19.jdk/Contents/Home/ \
   -Pllvm_home=/Users/cdea/sdks/clang+llvm-13.0.0-x86_64-apple-darwin/ \
   clean verify</pre>

You're almost done! After building the tool the process will create a new JDK SDK image that will be used instead of your newly downloaded JDK 19. By passing in the `-Pjdk19_home` it will create and generate a copy of the JDK 19 and `jextract` binaries located in the following directories:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">build/jextract/bin
build/jextract/lib</pre>

The JDK with `jextract` build directory should look like the following:
![](/images/posts/2022/06/building-project-panamas-jextract-tool-by-yourself/Screen-Shot-2022-06-06-at-3.26.33-PM.png)

Re-setting the `JAVA_HOME` and `PATH` environment variables {#h2-5-re-setting-the-java-home-and-path-environment-variables}
---------------------------------------------------------------------------------------------------------------------------

Before we can use the `jextract` tool you'll need to change the previous `JAVA_HOME` and `PATH` to now point to the directory `build/jextract` and `build/jextract/bin` respectively.

Great, now that you have a fresh JDK containing the `jextract`, let's generate some binding code!

Generate Panama bindings {#h2-6-generate-panama-bindings}
---------------------------------------------------------

Another requirement of `jextract` is to have access to the C libraries. Using `jextract` to generate binding code on the various operating systems you will need to install C libraries and headers files. The following are instructions to install C libraries for the respective operating systems.

### MacOS {#h3-7-macos}

In order to obtain C libraries and header files on a MacOS operating system you'll need Xcode to be installed. If you don't have **Xcode** install do the following:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">xcode-select —install</pre>

### Linux {#h3-8-linux}

In the case of Linux you'll need `gcc`'s compiler and libraries. To install enter the following commands:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># Ubuntu
sudo apt update
sudo apt install build-essential

# CentOS
sudo yum groupinstall 'Development Tools'</pre>

### Windows {#h3-9-windows}

When developing native C/C++ libraries you will download and decompress **MinGW** from <https://sourceforge.net/projects/mingw-w64/>. After downloaded you can unzipped MinGW into the **C:** drive's root directory.

Now that you have all the required C libraries lets use `jextract` against the `stdio.h` file to generate binding code.

Use `jextract` against `stdio.h` {#h2-10-use-jextract-against-stdio-h}
----------------------------------------------------------------------

Now that you have all your C libraries and header files in place you can target the `stdio.h` file to generate binding code. The commands below will generate source code that will be outputted in the **src** directory. Use the appropriate commands based on your operating system.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># MacOS
export C_INCLUDE_PATH=/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk/usr/include
jextract --source --output src -t org.unix -I $C_INCLUDE_PATH $C_INCLUDE_PATH/stdio.h

# Linux
export C_INCLUDE_PATH=/usr/include/
jextract --source --output src -t org.unix -I $C_INCLUDE_PATH $C_INCLUDE_PATH/stdio.h

# Windows
set C_INCLUDE_PATH=C:\MinGW\include
jextract.exe --source --output src -t org.unix -I %C_INCLUDE_PATH% %C_INCLUDE_PATH%\stdio.h</pre>

Using `jextract` to generate binding code for the Windows platform. This assumes you've installed **MinGW** in the **C:** directory.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># MacOS
export C_INCLUDE_PATH=/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk/usr/include
jextract --output classes -t org.unix -I $C_INCLUDE_PATH $C_INCLUDE_PATH/stdio.h

# Linux
export C_INCLUDE_PATH=/usr/include/
jextract --output classes -t org.unix -I $C_INCLUDE_PATH $C_INCLUDE_PATH/stdio.h

rem Generate Clib classes
set C_INCLUDE_PATH=C:\MinGW\include
jextract.exe --output classes -t org.unix -I %C_INCLUDE_PATH% %C_INCLUDE_PATH%\stdio.h</pre>

Now that you've generated Java source code or classes they can be conveniently used in your Java applications.

Conclusion {#h2-11-conclusion}
------------------------------

You've now learned that the `jextract` tool has been separated into its own project and is no longer part of the OpenJDK build distributions.

To build the jextract tool, you learned how to install required software such as a C compiler and libraries.

Once required software is installed, you use the gradle build tool to build a JDK distribution containing JDK 19-ea along with the `jextract` tool located in the build/jextract/bin directory.

Lastly, you are now able to use `jextract` to generate binding code to invoke `stdio.h` C functions.

There you have it! You're able to build `jextract` by yourself.
