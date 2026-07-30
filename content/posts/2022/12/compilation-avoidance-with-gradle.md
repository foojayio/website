---
title: "Compilation Avoidance with Gradle | Foojay.io Today"
slug: "compilation-avoidance-with-gradle"
date: "2022-12-13T15:35:31+00:00"
lastmod: "2022-12-13T15:36:42+00:00"
description: "Learn what ABI-based compilation means for the average workflow, turning out to be one of the best performance enhancements for any build!"
canonical: "https://blog.gradle.org/compilation-avoidance"
authors:
  - "amanda-martin"
image: "https://foojay.io/wp-content/uploads/2021/10/dependency.png"
categories:
  - "DevOps"
  - "Java Core"
tags:
related_posts:
  - "5-tips-to-create-secure-docker-images-for-java-applications"
  - "a-simple-service-with-spring-boot"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "how-gradle-works-inside-the-daemon"
frozen: false
---

We at Gradle recently noticed some community chatter about speeding up Gradle compilation on the JVM by ignoring changes not affecting the application binary interface (ABIs) of dependencies.

What a great idea! In fact, Gradle has used ABIs for Java out of the box for this without any extra configuration [since version 3.4](https://blog.gradle.org/incremental-compiler-avoidance).

We refer to this feature as **[compilation avoidance](https://docs.gradle.org/current/userguide/java_plugin.html#sec:java_compile_avoidance)**.

This post explains what ABI-based compilation means for the average workflow. Spoiler: utilizing compilation avoidance is one of the best performance enhancements for any build.

#### What is an application binary interface?

An application binary interface (ABI) is the interface generated from compiling software that defines internal and external interaction. The ABI represents what is visible to consumers at compile time.

When compiling a project, the presence or absence of changes in the ABIs of any of its dependencies determines if compilation is up-to-date or if recompilation is required.

These ABIs consist of all the public information about the dependencies that is visible to a consumer project, such as:

* any public methods with their argument types and the return statements
* any public properties and fields
* any dependencies used to compile against the ABI.

When a person accesses a library in source code, they use an API of the library. When a machine accesses compiled binaries, it uses the ABI.

#### Why are ABIs relevant for build performance?

Modern build systems consider ABI compatibility when compiling code to avoid as much compilation as possible when compiling incremental changes to the codebase.

Changes to internal implementation details are **[ABI-compatible](https://docs.gradle.org/current/userguide/caching_java_projects.html#java_compilation)**: they do not change the public interface. In practice, the internal implementation details of a project change much more frequently than the public components.

When public information does not change, any downstream projects will not need to be recompiled. Skipping that extra work can have a massive impact on the build performance of a large project, both locally and on CI.

Changes to public interfaces are ABI-incompatible because they change the public interface. ABI-incompatible changes require a recompile of all downstream dependencies. Having to recompile all dependencies downstream of an ABI-incompatible change can massively increase build times.

#### What is compilation avoidance?

Gradle optimizes builds when ABI-compatible changes occur. We call this optimization [compilation avoidance](https://docs.gradle.org/current/userguide/java_plugin.html#sec:java_compile_avoidance).

To see how this works, imagine two projects. `:app` depends on `:lib`. Here are some things we can generally do to `:lib` which are ABI-compatible and do not cause `:app` (nor anything that depends on `:app`) to need recompiling:

* Making any change to the body of a method
* Adding, removing, or changing private methods, fields, or inner classes
* Renaming a parameter
* Changing a comment
* Changing the name of jars or directories in the classpath
* Adding, removing, or changing a resource

When ABI-compatible changes happen in `:lib`, and a task in ':app' which depends on its classes is run, Gradle doesn't recompile project `:app` or any projects that depend on `:app`. In large multi-project builds, this can save large amounts of time.

#### How is compilation avoidance different from incremental compilation?

Not all changes fit the above requirements. In some cases, you will need to change the public ABI of `:lib`, which causes a need to compile `:app`. Luckily, another out-of-the-box feature called **[incremental compilation](https://docs.gradle.org/current/userguide/performance.html#incremental_compilation)** is used. This will intellegently recompile the classes in `:lib` that have changes so the downstream `:app` still compiles faster than a full compilation.

Incremental compilation is different from compilation avoidance but very complementary.

"Compilation avoidance" is about *avoiding calling the compiler altogether* for a given project.

On the other hand, "incremental compilation" *does* mean calling the compiler, but when doing this making an attempt to *reduce the amount of code needed to be recompiled.* This is facilitated by keeping track of the references between classes as part of normal compilation, and only recompiling things that are affected by a given change.

With incremental compilation, we look at all classes that have a change compared to compilation avoidance which looks at a project.

Compilation avoidance works across dependent projects, not just inside a project like incremental compilation. That is, incremental compilation optimizes the compilation of individual classes within your projects.

Incremental compilation happens within a single project, but compilation avoidance looks at the relationship between multiple projects. Incremental compilation still saves times across projects as it reduces the amount that needs recompiled.

#### Am I using incremental compilation or compilation avoidance?

In summary if you make an ABI-compatible change, then you are using incremental compilation and compilation avoidance: Incremental compilation for the compile task on the sources where you made the change and compile avoidance for downstream projects.

Alternately, if your change is not ABI-compatible, you can only benefit from incremental compliation.

#### What about ABI JARs?

Some build systems generate ABI JARs to achieve compilation avoidance. Sometimes called header JARs, they have the overall interface without the internal details.

The ABI JAR contains only the public methods, fields, constants, and nested types, with all the method bodies removed, and can be used to assess if any changes indicate a need to recompile.

With Gradle, we do not need an ABI JAR, as when there is a compiler task we normalize its inputs and generate a unique hash of the ABI. Gradle then uses this hash to check if there are any changes.

#### Notes for different languages

Groovy has an [opt-in experimental feature](https://docs.gradle.org/current/userguide/groovy_plugin.html#sec:incremental_groovy_compilation).

Kotlin has an [experimental feature](https://kotlinlang.org/docs/gradle-compilation-and-caches.html#a-new-approach-to-incremental-compilation) developed as part of [Kotlin Gradle Plugin](https://kotlinlang.org/docs/gradle.html) by [JetBrains](https://www.jetbrains.com/).

#### How can I use this?

Gradle automatically uses compilation avoidance. Both incremental compilation and compilation avoidance have been enabled by default in Java projects built with Gradle for years. So the next time you dread recompiling your code after an edit, rest assured that Gradle automatically gives you a performance boost.

Not sure if you are using incremental compilation or compilation avoidance, or perhaps you think you have a use case that should work but doesn't? Find us on [Slack](https://join.slack.com/t/gradle-community/shared_invite/zt-1bbiqbuxw-CgB0NeNaK_zuDMEa71A60Q), we love seeing use cases.

[Visit the original article here to leave comments.](https://blog.gradle.org/compilation-avoidance)
