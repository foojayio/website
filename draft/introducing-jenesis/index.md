---
title: "Introducing Jenesis: You Already Wrote the Build Script"
date: "2026-01-01"
description: "Jenesis is a Java build tool that reads the build from module-info.java: no build file to write, no plugins, no build tool to download."
authors:
  - "rafael-winterhalter"
image: "jenesis-hero.jpg"
categories:
  - "Developer Tools"
  - "Tools"
  - "Maven"
  - "Gradle"
related_posts:
  - "foojay-podcast-81"
  - "my-final-take-on-gradle-vs-maven"
  - "modules-modules-everywhere"
  - "how-to-create-sboms-in-java-with-maven-and-gradle"
---

Over the last decade, Java has changed considerably. The language gained modules, records and virtual threads, and the platform moved to a release every six months. The way we build Java applications, however, has barely changed. A `pom.xml` or a `build.gradle.kts` still restates what the code already declares, such as the project's name, its dependencies and its main class. On top of that, every step beyond the defaults adds a block of configuration and typically another plugin, until the build has become a second program that needs to be maintained beside the first. And before a single line of code is compiled, a wrapper first downloads the build tool itself, which in the case of Gradle are 152 MB. This article introduces Jenesis, a build tool that I have written to approach this problem from a different angle.

## A space that seems stuck

Maven 4 is on the horizon, and after many years of work, its release will be welcome. It does however not address the weaknesses that I described above. The build remains a second description of the project that is kept in sync with the code by hand, and the full test suite is still run on every build. Even for the Java Module System, Maven 4 adds a file of its own for tests, named `module-info-patch.maven`, which uses a syntax that only Maven understands. This file exists next to the module descriptor that the Java platform already offers.

Most alternative build tools of recent years replace XML with Java, Kotlin or another language, while they retain the model underneath. Such a "Maven, but in Java" is more convenient to write, yet it remains a separate program that describes the first one.

I have been in a similar situation before. When I started Byte Buddy, Java already offered several libraries for generating code at runtime, and adding another one with a nicer API would hardly have been worth the effort. Instead, Byte Buddy looked at the problem differently by allowing to define classes with plain Java types and to delegate to ordinary methods, without requiring any knowledge of byte code. With Jenesis, I attempt something similar for builds, where the different angle is that a project does not need a build file at all.

## Why feedback loops matter more than before

I expect that languages and programming platforms will increasingly be measured by how quickly they turn a change into a result, and by how easily they adapt. Coding agents are a large part of this development. An agent edits, builds and checks its result in rounds, and it often does so in a virtual machine or another cloud environment that starts from a fresh checkout. A platform therefore needs to evolve with such workflows in mind, and this includes its build tools.

In this regard, Java is in a good position. A modern JVM executes code as fast as natively compiled languages, the same byte code runs on any platform, and compiling a change takes only moments. The build, however, does not keep up. A fresh machine first needs to download a build tool and its plugins, and every round repeats work that was already done before. Jenesis attempts to make building a Java application trivial, such that JDK 25 or newer and a checkout are all that is required.

## Reading the build from the code

[Jenesis](https://jenesis.build/why/tool/) derives the build from the code. With the Java Module System, every module already declares its name and its requirements in a descriptor which `javac` compiles and validates. Jenesis treats this `module-info.java` as the build file, and any metadata that the descriptor cannot express is added to its Javadoc. As an example, consider the following module:

```java
/**
 * @jenesis.release 25
 * @jenesis.main demo.app.Main
 */
module demo.app {
    requires org.slf4j;
    requires com.fasterxml.jackson.databind;
    requires info.picocli;
    requires static org.jspecify;
}
```

This descriptor already describes a complete project. When starting a project, one typically wants to use the latest stable release of every dependency, and Jenesis can therefore determine these versions on its own. Running `java build/jenesis/Make.java pin` writes each version into the descriptor, together with the checksum of the resolved artifact.

Jenesis itself is plain Java source code of 2.3 MB that is committed to the project's `build/jenesis/` folder and launched by the JDK, without any wrapper or distribution to download:

```bash
curl -fsSL https://get.jenesis.build | bash
java build/jenesis/Make.java
```

Trying Jenesis does not require modularising a project. Since every artifact on Maven Central describes its dependencies in a POM, Jenesis needed a POM parser in any case, and it can therefore read a project's `pom.xml` just as well. Installing Jenesis into an existing project is the quickest way to try it.

## Caching and isolation

Every build step is keyed by a hash of its inputs, such that Jenesis skips any step whose inputs did not change, and only runs the tests that reach a changed class. This also makes sharing build results between machines trivial, as a single property points the cache to a shared folder or a cache server. A fresh CI runner or an agent's virtual machine can then reuse what was already built elsewhere.

When an agent, or a human, clones an unfamiliar repository to build it, to run its tests or to reproduce a bug, the build also executes code that nobody has reviewed. For this reason, Jenesis can run every build, and every program it launches, in a throwaway container that cannot reach the home directory or the environment of the host. Once enabled in the user's own configuration, this applies to every build, and a project cannot opt out of it. As a result, AI agents and humans alike can consume unknown Java projects safely without taking any additional precautions.

## How Jenesis came to be

I started working on Jenesis in 2024 and developed its core by hand. For a long time, I did however not see a way for it to compete. The established build tools offer a rich ecosystem of plugins for almost any need, and matching it on my own, with the time I have available, was out of reach.

I did, however, have a large number of sketches that planned out the architecture, and based on those, coding agents helped me to get Jenesis over the finish line. In particular, agents took on work that no single maintainer has the time for. They built countless real-world projects with Jenesis and summarised the bugs they encountered, and these reports showed me where the architecture did not hold up, so that I could readjust it. This way, Jenesis reached a level of maturity that an open-source project could not have reached in this amount of time before such tools could take over this kind of grunt work. Today, I consider Jenesis as mature as other open-source approaches.

## Further features

Beyond what this article describes, Jenesis offers the following features, each of which is shown by a small, runnable project among the [69 demos](https://github.com/jenesis/jenesis/tree/main/demo):

- A test module names the module it tests, and Jenesis infers the JUnit engine ([demo](https://github.com/jenesis/jenesis/tree/main/demo/demo-35-test-framework)).
- The modules of a project refer to each other by their module names, without a root file ([demo](https://github.com/jenesis/jenesis/tree/main/demo/demo-04-java-modular-multi)).
- Sources under `META-INF/versions/25/` produce a multi-release jar without further configuration ([demo](https://github.com/jenesis/jenesis/tree/main/demo/demo-11-java-multi-release)).
- Kotlin, Scala and Groovy are compiled within the same module as Java ([Kotlin](https://github.com/jenesis/jenesis/tree/main/demo/demo-41-kotlin), [Scala](https://github.com/jenesis/jenesis/tree/main/demo/demo-44-scala), [Groovy](https://github.com/jenesis/jenesis/tree/main/demo/demo-46-groovy)).
- XML, Protobuf and Avro schemas are compiled to Java as part of the build ([demo](https://github.com/jenesis/jenesis/tree/main/demo/demo-16-data-formats)).
- Only tests that a change can reach are run, and build results can be shared across machines ([tests](https://github.com/jenesis/jenesis/tree/main/demo/demo-37-test-selection), [cache](https://github.com/jenesis/jenesis/tree/main/demo/demo-49-build-cache)).
- SHA-256 pins, OpenPGP signatures and Sigstore identities are verified without any plugin ([pinning](https://github.com/jenesis/jenesis/tree/main/demo/demo-28-pinning), [OpenPGP](https://github.com/jenesis/jenesis/tree/main/demo/demo-29-openpgp), [Sigstore](https://github.com/jenesis/jenesis/tree/main/demo/demo-30-sigstore)).
- The build and the built program can run in a container without access to the host's secrets ([demo](https://github.com/jenesis/jenesis/tree/main/demo/demo-50-docker-isolation)).
- Jars are byte-identical between builds and contain a CycloneDX SBOM ([reproducible](https://github.com/jenesis/jenesis/tree/main/demo/demo-67-reproducible), [SBOM](https://github.com/jenesis/jenesis/tree/main/demo/demo-31-sbom)).
- Two versions of the same library can coexist in module layers instead of being shaded ([demo](https://github.com/jenesis/jenesis/tree/main/demo/demo-24-module-layers)).
- A `jlink` runtime, a `jpackage` image or a container image each require a single line of configuration ([demo](https://github.com/jenesis/jenesis/tree/main/demo/demo-09-java-modular-executable)).
- Checkstyle, PMD and SpotBugs are enabled by their configuration files, and Error Prone, JaCoCo and PIT are supported as well ([linters](https://github.com/jenesis/jenesis/tree/main/demo/demo-34-java-quality), [Error Prone](https://github.com/jenesis/jenesis/tree/main/demo/demo-14-error-prone), [coverage](https://github.com/jenesis/jenesis/tree/main/demo/demo-36-code-coverage), [PIT](https://github.com/jenesis/jenesis/tree/main/demo/demo-38-pitest)).
- Custom build steps are written as small Java modules, with types, IDE support and refactoring ([demo](https://github.com/jenesis/jenesis/tree/main/demo/demo-58-project-plugins)).

## Modules didn't fail, build tools did

When the Java Module System was released, many projects concluded that modules were not worth the trouble. In my opinion, most of this trouble was however caused by build tools that treat `module-info.java` as an afterthought. At JavaZone 2026, I gave a talk on this topic, and on what a build tool can look like if it starts out from the module descriptor instead:

{{< vimeo id="1223683963" title="Modules didn't fail. Build tools did - Rafael Winterhalter at JavaZone 2026" >}}

The [Jenesis website](https://jenesis.build/why/tool/) compares fourteen common builds side by side in Jenesis, Maven, Gradle and Bazel, and the [getting started guide](https://jenesis.build/tool/getting-started/) walks through a first build. The source code is available on [GitHub](https://github.com/jenesis/jenesis) under the Apache License 2.0. This article describes version 0.15.3, and I am happy about any feedback from using Jenesis on your own projects, either as a GitHub issue or by email to hello@jenesis.build.
