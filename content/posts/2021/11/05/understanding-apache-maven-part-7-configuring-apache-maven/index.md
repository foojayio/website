---
title: "Understanding Apache Maven (Part 7): Configuring Apache Maven"
date: "2021-11-05T16:51:36+00:00"
lastmod: "2021-11-05T16:55:55+00:00"
description: "Maven can depend on constraints external to what is packaged. Examples include the JDK to use (assuming there are several JDKs on the device)."
canonical: "https://cguntur.me/2020/06/27/understanding-apache-maven-part-7/"
authors:
  - "c-guntur"
image: "https://cgunturme.files.wordpress.com/2020/06/mavenconfiguration.png?w=1024"
categories:
  - "Maven"
related_posts:
  - "understanding-apache-maven-part-4-maven-lifecycle"
  - "understanding-apache-maven-part-5-dependencies-in-maven"
  - "understanding-apache-maven-part-6-pom-reference"
frozen: false
---

In this, [part 7 of the series](https://foojay.io/today/author/c-guntur/), various means of configuring Apache Maven are covered!

## Why Configure Apache Maven?

Over several parts in this series, Maven was touted to be ***convention-over-configuration***. Maven assumes defaults and allows for overrides where possible.

However, Maven can depend on constraints external to what is packaged. Examples include ***the* *JDK to use*** (assuming there are several JDKs on the computing device), **the flags to set (memory, garbage collection flags for tests etc.)** , ***system / environment variables*** , ***repository information*** , ***extensions to Maven*** and so on.

The examples mentioned above are external to both the maven executable/distribution as well as to the POM that is authored. Hence a need to provide a means of configuration.

## Options to configure Maven

* Using environment variables
* Using config files under a .mvn directory in the project
* Using XML configurations

### Configuring Maven -- Environment Variables

There are a few environment variables (**abbreviated as env. var.** in the blog) useful for maven execution of a POM. Listing a few common ones here:

#### `JAVA_HOME`

The **JAVA_HOME** env. var. points to the location of the JDK. This is useful to set especially if there are more than one JDK installations on the computing device. The value is an absolute path to a directory which contains the the JDK executable binaries and libraries.

#### `M2_HOME`

The **M2_HOME** env. var. points to the location of the Apache Maven installation. This is useful especially if there are more than one version of Apache Maven installations on the computing device. Apache Maven may also rely on this env. var. for maven location. The value is an absolute path to a directory which contains the maven binaries and libraries.

#### `PATH`

The **PATH** env. var. includes locations where the operating system looks for executable binaries and scripts. Including a path to the JAVA_HOME/bin (or JAVA_HOME\\bin for Windows) and a path to M2_HOME/bin (or M2_HOME\\bin for Windows) allows for java, javac, mvn and other executables there in, to be accessed from any directory.

#### `MAVEN_OPTS`

The **MAVEN_OPTS** env. var. is useful for setting JVM options to be used during the maven execution of the POM. Common use cases include setting of memory and garbage collection options.

### Configuring Maven -- Config files

Maven allows for customization on a per-project basis via config files. These files are located in a `.mvn` directory under the project root directory. The directory can contain a few config files.

#### The `jvm.config` file

The project root directory in maven is referred to as `projectBaseDir`.

The `${maven.projectBaseDir}/.mvn/jvm.config` file is a more modern take on specifying JVM arguments and can be used in lieu of the **MAVEN_OPTS** shared earlier. It also replaces an older `.mvnrc` file (*which had to be located in the logged-in user's HOME directory* ). The newer `jvm.config` allows for customizing JDK/JVM options on a per-project basis and these files can be checked in, into a source control system to be shared with other developers on the same project.

#### The `mvn.config` file

A `${maven.projectBaseDir}/.mvn/mvn.config` file is useful for setting maven command line options that need to be set for normal execution.

Most developers simply memorize the standard commands to run maven:  

`mvn clean install`  
`mvn clean verify`  

etc.

Forgetting to set other required command line interface (CLI) options useful for any other reason can result in unexpected or unwanted outcomes from the execution.

An example could be that the project POM relies on SNAPSHOT versions, but requires force updates of SNAPSHOTs every build. This is done via a `-U` CLI option. Similarly a project may wish to fail the execution of maven if the checksums for artifacts fail. This can be done via a `--strict-checksums` CLI option. Such would usually require reading some documentation.

The `mvn.config` file allows for setting and checking into version control, such options that can be used by other developers on the project.

### Configuring Maven -- XML files

In [Part 2](https://cguntur.me/2020/05/24/understanding-apache-maven-part-2/) of the series, the global settings XML file and a user-home settings.xml were covered. There are additional configuration files that maven provides.

#### Global `settings.xml`

Located under the maven installation `conf` directory, is a `settings.xml` file that is applicable to any user who uses the installed version on maven on the computing device. Typical usage of this settings file is for corporate settings, proxies within the network, approved mirror sites etc. It is not recommended to heavily customize/personalize this file since it will equally impact all users on the said computing device.

#### User Home `settings.xml`

Located at `~/.m2/settings.xml` (or `${USER_HOME}\.m2\settings.xml`), this settings file allows for more personalizing on a per-user setting. Typical usage is to setup any usernames and passwords, default mirror, default profiles, repository and pluginRepository settings. Note that any configurations defined here apply to ALL maven projects executed by the current user.

#### Maven `extensions.xml`

In [Part 6](https://cguntur.me/2020/06/20/understanding-apache-maven-part-6/) of the series, extensions were mentioned as additional enhancements to maven behavior. The extensions are artifacts that get added to maven's own classpath (*not the project classpath*) during execution.

Prior to maven `extensions.xml`, such artifacts had to be compiled into ***shaded*** jar files which would need to be copied into the maven installation's `lib/ext` directory so they could be picked up.

Starting Apache Maven 3.2.5, an easier solution is to treat such artifacts similar to dependency resolutions via a file located under the project root's .mvn directory.

The path to maven extensions is `${maven.projectBaseDir}/.mvn/extensions.xml`. The file contains a root **extensions** element which can contain a set of **extension** elements which provide the **groupId** , **artifactId** and **version** coordinates for the **extension**.

A full schema for extensions: <http://maven.apache.org/ref/3.6.3/maven-embedder/core-extensions.html>

#### Maven `toolchains.xml`

There can be use cases where the JDK used by maven to launch is different from the JDK used to build the project. Also there could be a use case to build the same project with different JDKs via **profiles** . Such use cases can accomplished by using ***toolchains***.

Plugins that are ***toolchain-aware*** can benefit from a defined toolchain and switch to use a JDK that matches conditions specified in the `toolchains.xml`. The `toolchains.xml` is usually located at the project root and is invoked via a flag on maven command line. Using toolchains require including the `maven-toolchains-plugin` in the POM.

Toolchains can either be **local** to the project or **global** (across all projects on a given computing device). The recommended location for **global toolchains** is at the `~/.m2/toolchains.xml` (or `${USER_HOME}\.m2\toolchains.xml`). Global toolchains are invoked with a maven command line:  

`mvn clean verify --global-toolchains ~/.m2/toolchains.xml`  
`mvn clean verify -gt ~/.m2/toolchains.xml`

Local toolchains are similarly invoked using a maven command line:  

`mvn clean verify --toolchains toolchains.xml`  
`mvn clean verify -t toolchains.xml`

The `toolchains.xml` file has a root **toolchains** element which contains a set of **toolchain** elements. The **toolchain** element contains a few child elements.

* A **type** element (standard value is `jdk`, creating custom toolchains allows for other values).
* A **provides** element is a collection of properties (`<key>value</key>`). These properties can be used as conditions when configuring the `maven-toolchains-plugin` in the POM. Matching the conditions results in the specific toolchain being selected.
* A **configuration** element is another properties element that typically is used to provide the location of the JDK in the standard offering but can be customized when creating bespoke toolchains.

Excellent documentation on toolchains is available at the Maven documentation:  

Link: <https://maven.apache.org/ref/3.6.3/maven-core/toolchains.html>  

Link: <https://maven.apache.org/guides/mini/guide-using-toolchains.html>  

Link: <https://maven.apache.org/plugins/maven-toolchains-plugin/>
> A working sample for toolchains
> **toolchains.xml** : <https://github.com/c-guntur/jvms-compare/blob/master/toolchains.xml>  
>
> Usage of a specific toolchain in a pom.xml:  
> **toolchains.xml** **excerpt** : <https://github.com/c-guntur/jvms-compare/blob/master/toolchains.xml#L44-L54>  
> **pom.xml excerpt** : <https://github.com/c-guntur/jvms-compare/blob/master/pom.xml#L401-L420>  
> (The configuration in the pom `maven-toolchains-plugin` looks for an AdoptOpenJDK Hotspot Java 11)
![Three different means of configuring Maven: Environment variables, .mvn Config files and XML configurations](https://cgunturme.files.wordpress.com/2020/06/mavenconfiguration.png?w=1024) Three different means of configuring Maven: environment variables, Maven config files, and XML configurations.

That's a wrap on configuring Maven!
