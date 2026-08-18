---
title: "Understanding Apache Maven: Maven Coordinates & POM Inheritance"
slug: "understanding-apache-maven-part-3-maven-coordinates-pom-inheritance"
date: "2021-10-01T10:05:15+00:00"
lastmod: "2021-10-01T10:11:42+00:00"
description: "An explanation of dependency coordinates and \"distinguishers\" as well as a more detailed look at POM hierarchies are covered."
canonical: "https://cguntur.me/2020/05/26/understanding-apache-maven-part-3/"
authors:
  - "c-guntur"
image: "https://cgunturme.files.wordpress.com/2020/05/mavenhierarchies.png?w=1024"
categories:
  - "Maven"
tags:
related_posts:
  - "understanding-apache-maven-part-1-the-basics"
  - "understanding-apache-maven-part-2-pom-hierarchy"
  - "creating-a-simple-spring-boot-application-in-intellij-idea"
frozen: false
---

In Part 3 of the series, an explanation of dependency coordinates and "distinguishers" as well as a more detailed look at POM hierarchies are covered.

## What are dependency coordinates?

There are hundreds or thousands of projects that produce artifacts. Some such artifacts can potentially be used in a current project as libraries. For instance, a project may depend on a logging framework or a JSON library. It is possible to host many such dependency artifacts on a central repository. Maven's primary such repository is called **Maven Central**. Many other such repositories also exist. More on this later!

With the availability of such repositories, the next question is, how are exact artifacts needed for a project identified and downloaded? A proper way to identify the artifact is via its **Maven coordinates**.

### What are Maven coordinates?

A way to uniquely identify an artifact. There are three primary coordinates that are used to identify an artifact.

#### groupId

A grouping classification, typically referring to an organization, a company and may include a basic theme for one or more projects. A **groupId** typically follows a *dot notation* similar to a Java package name. Each token in the dot notation corresponds to a directory in a tree structure on the repository. For instance, a **groupId** of **org.apache.commons** corresponds to `$REPO/org/apache/commons`.
> <br />
>
> *While is it strongly recommended, it is not a requirement to have the dot notation. Several projects have forgone the need for a dot notation and simply carried a simple name as the groupId. This single-name practice is discouraged. All projects are recommended to maintain a fully-qualified dot-notated groupId.*

#### artifactId

A proper name for the project. Among the many projects that exist in the group, the **artifactId** can uniquely identify the artifact. An artifactId follows a simple name nomenclature with hyphenation recommended for multi-word names. Names should ideally be small in length.

An artifact manifests itself as a sub-directory under directory tree that represents the groupId. For instance, an **artifactId** of **commons-lang3** under a **groupId** of **org.apache.commons** would determine that the artifact can be found under : `$REPO/org/apache/commons/commons-lang3/`.

#### version

An identifier that tracks unique builds of an artifact. A **version** is a string that is constructed by the project's development team to identify a set of changes from a previous creation of an artifact of the same project. It is strongly recommended to follow [semantic versioning](https://semver.org/) schemes for versions, although it is not mandated.

A version manifests itself as a sub-directory under the directory tree that represents the groupId and artifactId. For instance a **version** of **3.1.0** for an **artifactId** **commons-lang3** under the **groupId** of **org.apache.commons** would determine that the artifact would located under: `$REPO/org/apache/commons/commons-lang3/3.1.0/`.

First, a little bit of a deeper dive on versions...

**Understanding version number schemes**

Following standard Software Development Lifecycles, a project can undergo phases of development with each phase consisting of a few attempts at building the software, testing, fixing bugs, making further required changes etc. Once the cycle completes, the product is marked for release for the given phase. These lifecycles and phases have nothing to do with Maven.

**Development cycle**

During development a product can produce a few artifacts that can be tested, verified etc. This development cycle typically produces artifacts with the same version as prior builds in the same phase. The basis for this statement is that the release is still in experimental, beta or alpha state, but may undergo more changes. However, it is hard to get all consumer projects to constantly update their own POMs for each such build. This grief is mitigated by the use of **SNAPSHOT** builds.

As an example, for a project A, version `1.0.0`, a development team can build several SNAPSHOT versions with version `1.0.0-SNAPSHOT`over a period of time. This suffix implies that the version number is intended to be `1.0.0` at the end, however the build will produce newer artifacts each build that replace the prior `SNAPSHOT`. The newer `SNAPSHOT` can replace an existing `SNAPSHOT` of the artifact in a Maven repository, so consumer projects can safely procure the latest `SNAPSHOT` version. A **SNAPSHOT is a mutable version** of the project that can be declared as a dependency in consumer projects.

**Production-Ready**

Once all testing is complete, the POM no longer needs the `-SNAPSHOT` suffix and can build a **production-ready immutable version** of the project. Once the *immutable* version of the project has been pushed to a repository, no further changes **should be made to it** (Maven cannot enforce this, this is based on discipline). Other production-ready suffixes can commonly be found: `1.0.0-GA` (general availability) or `1.0.0-RELEASE` (released artifact) or `1.0.0-FINAL` etc. Once again, the version strings are NOT meaningful to Maven at all.

#### The GAV coordinate system

A common way of communicating an artifacts coordinates is with a colon separation. Together the coordinates are referred to as **Group-Artifact-Version** or **GAV** coordinates. The GAV coordinates for commons-lang3 version 3.1.0 will be: **org.apache.commons:commons-lang3:3.1.0**.

## Additional distinguishers

Often times, a project's build may include more than one format of artifacts. A Maven execution on a project could emit a jar file, a zip file a tarball and many other artifacts. An execution could also emit different outputs such as a binary, a zip file of sources, a zip file of javadoc files etc.

Apart from the above mentioned GAV coordinates, **distinguisher**s are thus needed to identify such diverse outputs.

#### classifier

A **classifier** is used to distinguish an alternate output emitted by executing Maven on the project POM. Common examples include sources as a `.jar`/ `.zip` file and javadoc as a `.jar` / `.zip` file. The classifier manifests itself as a part of the artifact name. For our above example of commons-lang3, the artifact to look for is: `commons-lang3-3.10-javadoc.jar` or `commons-lang3-3.10-sources.jar` under `$REPO/org/apache/commons/commons-lang3/3.1.0/`.

#### type

A **type** is used to distinguish the artifact format. Artifacts emitted from a Maven execution can be of various types as already discussed: `.jar`, `.war`, `.zip` etc. It may, at times be beneficial for a project to produce artifacts in different formats. These formats are specified under a type distinguisher.

#### Putting it all together

A combination of GAV coordinates and distinguishers can be used to locate the exact artifact needed for the project.

## POM Hierarchies

This section describes the hierarchy in Maven POMs.
![](https://cgunturme.files.wordpress.com/2020/05/mavenhierarchies.png?w=1024)

### Parent POM

A **parent** POM is a POM from which the current project POM can inherit content. The project POM can depend on exactly one parent POM. This single-parent inheritance is one-way. The *parent POM is unaware of the POM that inherits from it* . The child POM declares the parentage in its own `pom.xml` (standard file to hold the POM, can be customized to a different name).

Any number of POMs can declare another POM as their parent.   
**USAGE**: While we will delve into the contents in a future blog, the parent POM can be used to declare re-usable portions of the POM that individual child POMs can then inherit. This helps in both maintenance and to reduce clutter.

### Aggregator POM

An **aggregator** POM (also known as a **reactor** POM) is a POM that can sequence the builds of many projects. An aggregate POM specifies all the projects that can be build-managed together. The *child POM(s) remain unaware of the aggregator POM that invokes it* . A child POM can be a listed in more than one aggregator POM. The aggregator POM lists the child POM by `name` in it's own `pom.xml` as a `module`.

As the declared `module` suggests, this pattern is for modular builds of projects. There is *no inheritance of any content* from the aggregator POM.  
**USAGE:** While we will dig deeper in a future blog, the aggregator POM can be used to ensure the sequence of builds and maintain a list of projects that should be build-managed together.

### Bill-Of-Materials POM

A **bill-of-materials** POM is a POM that can declare bundles of dependencies that have been tested to work well together. The abundance of artifacts and versions of each can, at times, lead to confusion and needs for trial-and-error mechanisms to determine compatibility and/or right functionality.

A bill-of-materials POM reduces that overhead. A bill-of-materials POM is a means of multiple inheritance too, since a project POM can *import* multiple bill-of-material POMs. The *bill-of-materials POM is unaware of the child POMs that import it. The child POMs declare the bill-of-materials in the project POM*.

**USAGE:** While this will be covered in detail in a future article, the bill-of-materials POM can be used to bundle well-working dependencies (with right versions) together to avoid repetition for every project.

"
A POM can be both a **parent** as well as an **aggregator**. This allows for a two-way relationship in a tightly knit set of modules that depend on a common set of inherited content."

That's a wrap on this article in the series. Have fun!
