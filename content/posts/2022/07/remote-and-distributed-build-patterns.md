---
title: "Remote and Distributed Build Patterns | Foojay.io Today"
slug: "remote-and-distributed-build-patterns"
date: "2022-07-19T10:48:48+00:00"
lastmod: "2022-07-28T17:34:40+00:00"
description: "Let's review build patterns leveraging remote machines, clarify the definition of remote and distributed builds, and discuss their variations."
authors:
  - "kyle-moore"
image: "https://foojay.io/wp-content/uploads/2022/07/1_Pxqa8hcF86VYD159umwkKw.png"
categories:
  - "DevOps"
  - "Performance"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "a-simple-service-with-spring-boot"
  - "book-review-why-programs-fail"
  - "general-build-distribution-a-game-changer-or-a-gimmick"
frozen: false
---

**Remote and distributed builds are terms frequently used, misused and abused. Here we share insight for what these terms mean and how they affect your build feedback times.**

A frequently-requested feature for the [Gradle Build Tool](https://gradle.org/) is the ability to perform remote or distributed builds.

But what exactly does this mean? And what are the motivations behind the requests?

This article will explore the difference between remote vs. distributed builds and their variations. As there is no industry-wide agreement on consistent terminology for these concepts, the goal of this article is to give an overview of these patterns and how they relate to each other.

Except for two JVM-specific references, these observations are generally applicable to software projects using any language or ecosystem.

But Why? {#h2-0-but-why}
------------------------

These features are typically discussed in the context of shortening build times on local developer machines.

Extended build turnaround times hinder productivity in both local and CI environments, but the local build experience has a disproportionate effect on developer sentiment.

### Terminology {#h3-1-terminology}

The terms "remote" and "distributed" builds are not always used consistently in the industry and are often used interchangeably.

Below, we'll give each a distinct definition. Firstly, we'll also define the more fundamental "build cache" optimization.

### What is a Remote Build Cache? {#h3-2-what-is-a-remote-build-cache}

The first pattern that includes a remote component that we'll discuss is the build cache. Similar to [incremental builds](https://docs.gradle.org/current/userguide/more_about_tasks.html#sec:up_to_date_checks), the build cache avoids execution of CPU-intensive operations like compiling source files or executing tests. While an incremental build leaves the outputs of the most recent local operation in-place on disk, a build cache does this by storing and reusing the results of any previous execution of the operation - much like restoring files from a backup.

More importantly, the cache can be local-only, or shared amongst engineers (remote build cache). The CI environment is typically configured to write to a shared cache in the cloud. Each engineer's machine then pulls results from the shared cache. This means that the same sources need only be built once on a CI host avoiding expensive local compiler invocations on developer machines.

In short, the build cache stores the intermediate build artifacts on remote servers to speed up builds. Unlike remote and distributed builds, no tasks are actually executed remotely.

The Gradle Build Tool [Build Cache](https://blog.gradle.org/introducing-gradle-build-cache) feature has been very successful at reducing both local and CI build times since its introduction in 2017.

### Variations of Remote Build {#h3-3-variations-of-remote-build}

In general, "building remotely" refers to the method of reducing build times by delegating the entire process to another computer. Typically the remote computer is more powerful than a local computer in terms of compute resources and memory. It may host builds in a uniform, curated environment and/or in isolation, free from resource contention with other local processes.

In practice, building remotely can take one of four forms defined below.

#### Legacy Solution: Remote Desktop/Screen Sharing

The most primitive pattern of remote building is using remote desktop via the venerable VNC or RDP protocols. While these are admittedly low-tech screen sharing tools and can be greatly affected by low network bandwidth and high latency, they do allow building software on a remote machine. We only mention this historical solution for completeness, as modern remote IDEs provide a much more responsive solution.

#### What is a Remote Build?

In a remote build scenario, the build command is invoked on the local machine but the actual computation occurs on a remote machine. Source files and other supporting build inputs are initially present on the local machine and synchronized with the remote machine. Likewise, at the conclusion of the build the resulting build outputs/artifacts are synchronized from the remote back to the local machine.

This opens some interesting possibilities, though not without challenges. On the one hand, building remotely may add the ability to build code on a different hardware architecture or operating system (for example, a Windows client builds on a Linux host). And the remote host's hardware might result in a significant build speed improvement. At the same time, the synchronization overhead of keeping source files, project dependencies, build artifacts and ephemeral, intermediate state of the build software may quickly outweigh the raw speed benefits.

Gradle does not offer its own remote build solution today, but some interesting complementary open-source solutions exist:

* [Mainframer](https://github.com/buildfoundation/mainframer): "A tool that executes a command on a remote machine while syncing files back and forth."
* [Mirakle](https://github.com/Adambl4/mirakle): "A Gradle plugin that allows you to move build processes from a local machine to a remote one."

#### What is a Remote IDE?

Remote IDE is similar to the remote build scenario, with two key differences. First, the IDE, not a command-line or build tool invocation, handles the communication with the remote host. Next, the source code can be cloned exclusively on the remote host. In this paradigm, the IDE acts as a "thin client" on the local machine. The "backend" portion of the IDE code runs as a background process on the remote host.

The benefits of this approach are that the project source code is not required to be present locally, and the synchronization overhead of remote build is alleviated. As with local development, there is potential for resource contention between the "backend" IDE and the build processes.

Three good examples of remote IDEs exist today, all of which work seamlessly with the Gradle Build Tool:

* Visual Studio Code: Offers a remote IDE experience via its "Remote - SSH" [extension](https://code.visualstudio.com/docs/remote/ssh).
* IntelliJ IDEA: JetBrains [Client and Gateway](https://www.jetbrains.com/help/idea/remote-development-a.html) work together to run a thin IDE locally while building remotely.
* Fleet: Though in closed preview, JetBrains [Fleet](https://www.jetbrains.com/fleet/) offers a lightweight remote IDE experience similar to Visual Studio Code.

Remote IDEs like the examples above can offer a very pleasant development experience. If the remote host machine or VM is collocated in a data center near the VCS and binary artifact storage systems, cloning code and resolving external dependencies can be extremely quick. If done well, remote IDE can be a substantial improvement compared to building locally.

#### What is a Remote Build Environment?

Taking the concept of remote IDE a step further, remote build environments aim to automate the provisioning of remote hosts, with an emphasis on consistency and collaboration. A remote build environment is typically centrally-managed, ensuring all engineers have a dependable, uniform environment with no need to build on their local machines.

Some remote build environments combine other dev tooling, such as the IDE, bug/issue tracking and source control. Combined with a remote IDE, wrapping all the tooling an engineer might need into a single, curated environment can yield a very pleasurable and productive development experience.

As with remote build/IDE, additional performance comes in the form of faster CPU cores and improved parallelism via additional cores per machine relative to the local environment. While the use of a remote build environment does not directly correlate to choice of build tool, the Gradle Build Tool will work transparently in any remote environment.

Three prominent examples of remote build environments are:

* [GitHub codespaces](https://github.com/features/codespaces)
* [JetBrains Space](https://www.jetbrains.com/remote-development/space-dev-environments/)
* [Gitpod](https://www.gitpod.io/)

Centrally-managed remote build environments can provide several benefits:

* Faster startup time: There is no need for a engineer to manually checkout code and set up the local machine: the environment can be configured to be ready to go "out of the box".
* Faster feedback time: This assumes the remote machine has higher performance, and is colocated with other critical resources such as your binary artifact storage.
* Multiplatform support: For example remotely building on a Windows environment from a macOS laptop or vice versa.
* Uniform environment: There is less risk of inconsistency on local engineer machines.
* Security and audit: This refers to a centrally-managed environment in a data center where it may be desirable for protecting intellectual property or may be a compliance requirement.

#### Remote Summary

Looking at the available solutions above, we see the most exciting innovation taking place in the remote IDE and remote build environment spaces. Remote build has some interesting aspects as well, but the marginal benefits to the local developer experience may be outweighed by the increased complexity. As such, we encourage bypassing remote build in favor of remote IDEs while keeping an eye on the emerging capabilities of remote build environments.

### Variations of Distributed Builds {#h3-4-variations-of-distributed-builds}

Unlike remote builds which execute all work on a single remote machine, distributed builds focus on dividing work into small pieces, and distributing them among multiple machines. The remote executors are typically allocated from a pool, similar to how CI allocation works, though each distributed work item takes relatively little time to execute.

Distributed builds are implemented as a more-or-less transparent feature of the build, so developers trigger them locally similarly to how they'd trigger a local build. Inputs that are required to execute a work item are transmitted to the executor, and generated outputs are synced back.

Don't forget about the backing infrastructure needs of distributed builds. A build could actually be slower if sufficient remote build agents are not available. The management of a complex pool of build distribution agents adds additional maintenance like monitoring/observability, scaling and failover/fault tolerance.

Before we get into true distributed build solutions, we'll first describe the most basic technique for distributing build work across multiple machines.

#### Manual Optimization: CI Fanout

CI fanout is a technique to reduce end-to-end build time by splitting the build (typically subsets of tests) to multiple CI jobs so that the work is executed on different agents. While it is an improvement over no parallelism or single-machine parallelism, it comes with major drawbacks. The partitioning of the CI jobs must be manually configured, and is unique to each CI platform. While this reduces overall build times on CI, it does not benefit local builds. Other challenges of this approach are described [here](https://gradle.com/gradle-enterprise-solutions/test-distribution/#advantages-section).

#### Modern Test Distribution

In our experience, running tests, not compiling source code, is typically the primary cause of slow builds, especially in the JVM ecosystem. Test execution on the JVM is naturally suited for distribution, as tests are typically executed in a separate ephemeral VM whose system environment, classpath and memory usage are already specified. These parameters are easily communicated to a remote host for distributed execution.

The same concerns when locally partitioning or parallelizing test execution apply when running tests in a distributed fashion. Good test methods should be atomic, relying only on explicit environment setup/teardown instructions from test fixtures. A poorly-crafted, non-atomic test relying on side-effects of another test may fail in unexpected ways when executed in distributed fashion.

Gradle Enterprise's [Test Distribution](https://gradle.com/gradle-enterprise-solutions/test-distribution/) commercial feature executes tests on a pool of remote hosts with greater parallelism than can be achieved locally. It also executes all methods of a test class on the same host, alleviating the most common cause of non-atomic test failures mentioned above.

#### General Distribution

As the name suggests, general distribution is a way to execute any build operation on a remote host. Careful consideration must be given to environment variables or other system attributes which could produce unintended changes for a distributed build result compared to building the same code locally. Also, the question of which build operations justify the overhead of distribution is difficult to answer.

The following tools take a general approach to build distribution:

* Pants: <https://www.pantsbuild.org/docs/remote-execution>
* Bazel: <https://bazel.build/docs/remote-execution>

Before deciding on a general distribution solution, know that significant tradeoffs may be required. A general distribution build environment may add significant complexity to the build logic. "Split package" compilation (sometimes called the [1:1:1 rule](https://v1.pantsbuild.org/build_files.html#target-granularity)) is a technique to divide source code into smaller units of compilation to aid in distributability, but adds more pain to the already complex issue of dependency management. See [The granularity of build files](https://blog.gradle.org/gradle-vs-bazel-jvm#the-granularity-of-build-files) for more detail.

The benefit of distributing non-test work such as compilation depends on the compilation speed of programming language in use. For example, Java compilation is relatively fast compared to "native" languages. Small performance improvements might be possible using "split package" compilation, mentioned above. But the additional pain of maintaining complex build logic may not be justifiable for relatively minor performance gains. This is especially true considering the Gradle Build Tool's incremental compiler for Java already provides a significant performance boost on top of javac.

See the [General Build Distribution: A Game-Changer or a Gimmick?](https://medium.com/@GradleBuildTool/general-build-distribution-a-game-changer-or-a-gimmick-28b50bd6ff78) article for more details about the tradeoffs with general build distribution.

### Common Factors of Remote and Distributed Builds {#h3-5-common-factors-of-remote-and-distributed-builds}

In both the remote and distributed paradigms, non-trivial amounts of network traffic can result. Serializing source code to a remote host or synchronizing build artifacts between agents can incur a significant overhead. Network proximity between the local client and remote host, or between distribution agents and artifact storage, can be a major factor. Network connections should be both high-bandwidth and low-latency to prevent eroding the theoretical gains achieved through remote and/or distributed work.

Further, managing the pool of remote hosts or distributed agents incurs more cost and overhead. Additional engineering investment will be required to provide the standardized environments. Care should be exercised to avoid resource starvation or over-allocation by responding to peak usage cycles and downtime.

Summary {#h2-6-summary}
-----------------------

In this post, we've reviewed the build patterns in which remote machines are leveraged, clarified the definition of remote and distributed builds, and discussed their variations.

We started by explaining the remote build cache as the most fundamental build optimization leveraging remote machines. Then, we elaborated on remote build patterns and pointed out exciting innovation taking place in the remote IDE and remote build environment spaces. Finally, we explained the CI fanout technique and different patterns in distributed builds including test distribution and general distribution.

### Feedback {#h3-7-feedback}

Let us know if you have any questions on our [forums](https://discuss.gradle.org/) or [Gradle Community Slack](https://gradle.com/slack-invite).
