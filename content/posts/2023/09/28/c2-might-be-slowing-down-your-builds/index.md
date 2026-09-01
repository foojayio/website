---
title: "C2 Might Be Slowing Down Your Builds"
date: "2023-09-28T09:55:13+00:00"
lastmod: "2023-09-28T09:55:14+00:00"
description: "Disabling C2 can be an option to speed up builds of smaller Java applications in CI systems, mainly when restricted to one or two CPU cores."
authors:
  - "johannes-bechberger"
image: "image-7-2000x462-1.jpg"
categories:
  - "Maven"
  - "Performance"
related_posts:
  - "my-final-take-on-gradle-vs-maven"
  - "build-rot-tech-debt"
  - "predicting-secure-java-projects-on-maven-central"
  - "enterprise-java-quality-gates-ai"
frozen: false
---

**At the JavaForumNord two weeks ago, I had a friendly chat with Karl Heinz Marbaise (Chairman of the Apache Maven Project), where he mentioned that he wanted to start profiling Maven. This sounded interesting, so I started looking into the performance and bottlenecks of Maven. I began by using the Maven build of [maven](https://github.com/apache/maven) itself as a starting point (excluding the tests). The following is my first observation related to Maven builds in CIs.**

Maven is one of the most used build systems in the Java ecosystem ([JetBrains 2022 survey](https://www.jetbrains.com/lp/devecosystem-2022/java/)), and many projects, especially open-source projects, use GitHub Actions to create artifacts and run tests automatically. The free tier of GitHub Actions has two cores (see [GitHub docs](https://docs.github.com/en/actions/using-github-hosted-runners/about-github-hosted-runners/about-github-hosted-runners)), so we only have limited parallelism compared to the usual developer machines.

Maven uses the [Plexus compiler wrapper](https://codehaus-plexus.github.io/plexus-compiler/) Java API around the [javax.tools.JavaCompiler](https://docs.oracle.com/en/java/javase/17/docs/api/java.compiler/javax/tools/JavaCompiler.html) API to compile every Java file. The underlying JVM then essentially has three tasks running while compiling:

* Maven main thread that does the compilation
* Garbage Collection thread for memory management
* JIT compilation thread to compile the maven byte code (it's getting meta), enabling it to run faster (see [Mastering the Art of Controlling the JIT: Unlocking Reproducible Profiler Tests](https://mostlynerdless.de/blog/2023/05/18/mastering-the-art-of-controlling-the-jit-unlocking-reproducible-profiler-tests/) for more information). Especially the C2 compiler takes some time. This is our example in a shared thread with the GC.

## Small builds

This is problematic on short builds (like building maven itself in 30s), as the C2 JIT does cost a significant amount of cpu-time, more than is saved by the faster execution of the jitted code. The maven self-built, for example, spent more than half of its cpu-time in the C2 compiler:
[![](https://mostlynerdless.de/wp-content/uploads/2023/09/image-7-2000x462.png)](https://mostlynerdless.de/wp-content/uploads/2023/09/profile-two-core-none.html) Maven 4 (f24266eb64) was built with maven 3.8.7 on SapMachine 17.0.8.1 and profiled with [async-profiler](https://github.com/jvm-profiling-tools/async-profiler); click to view the [full](https://mostlynerdless.de/wp-content/uploads/2023/09/profile-two-core-none.html)flame graph

We can use the information by async-profiler to get the cpu-time proportions for significant parts of the built:

|   |   |   |
|----------------------------------------|---------------------|---------|
| Part                                   | cpu-time percentage | samples |
| Launcher.main                          | 38 %                | 3200    |
| C2Compiler::compile_method             | 47 %                | 4000    |
| Compiler::compile_method (C1 compiler) | 8 %                 | 650     |

The whole maven build took 44s (82s cpu-time) on two cores of my ThreadRipper 3995WX. Compare this to a run with a disabled C2 which took 41s (50s cpu-time):
![](https://mostlynerdless.de/wp-content/uploads/2023/09/image-8-2000x463.png) Maven 4 (f24266eb64) built with maven 3.8.7 on SapMachine 17.0.8.1 and `MVN_OPTS="`*-XX:TieredStopAtLevel=1*`"` (*but stopping at a higher C1 level is unusual, see [dzone](https://dzone.com/articles/complication-compilers-and-java)* ) profiled with [async-profiler](https://github.com/jvm-profiling-tools/async-profiler), click to view the [full](https://mostlynerdless.de/wp-content/uploads/2023/09/profile-two-core-stop.html)flame graph

The proportions are as expected:

|   |   |   |
|----------------------------------------|---------------------|---------|
| Part                                   | cpu-time percentage | samples |
| Launcher.main                          | 71 %                | 3700    |
| C2Compiler::compile_method             | 0 %                 | 0       |
| Compiler::compile_method (C1 compiler) | 14 %                | 750     |

The time to complete the task changes only by 3s, but the CPU time and, by proxy, the energy use drops significantly. These results are stable over multiple runs with different JDKs.

Side note: Maven only compiles with one thread, compiling with two threads (`-T2` option) reduces the build time further by 10 to 15% with C2 and without.

Does this mean that you should always disable C2 in CI builds? No. At a certain project size, the performance increase by the faster, compiled methods outweighs the C2 compilation costs.

## Large builds

Take, for example, [quarkus](https://quarkus.io/): A clean build on two cores runs for 430s (710s cpu-time) with C2 enabled:

{{< img src="https://mostlynerdless.de/wp-content/uploads/2023/09/image-9-2000x383.png" class="size-large is-resized" width="614" height="126" style="width:614px;height:126px" caption="Quarkus (af4208a05e) built with maven 3.8.8 on SapMachine 17.0.8.1 and ./mvnd -Dquickly" >}}

The runtime proportions are less skewed in the direction of C2:

|   |   |   |
|----------------------------------------|---------------------|---------|
| Part                                   | cpu-time percentage | samples |
| MavenWrapperMain.main                  | 45 %                | 39200   |
| C2Compiler::compile_method             | 30 %                | 26200   |
| Compiler::compile_method (C1 compiler) | 2 %                 | 1750    |

The built without C2 runs in comparison for 880s (1000s cpu-time), so it doesn't make any sense to disable C2 with this build. For completeness, the proportion table:

|   |   |   |
|----------------------------------------|---------------------|---------|
| Part                                   | cpu-time percentage | samples |
| MavenWrapperMain.main                  | 57 %                | 52700   |
| C2Compiler::compile_method             | 0 %                 | 0       |
| Compiler::compile_method (C1 compiler) | 7 %                 | 6150    |

## Conclusion

Disabling C2 can be an option to speed up builds of smaller Java applications in CI systems, mainly when restricted to one or two CPU cores. I would recommend exploring this for every maven build that runs under a minute, as it is not too hard to integrate (`MVN_OPTS="`*-XX:TieredStopAtLevel=1*`"`), yet might result in less CPU usage. Preliminary findings also show that it reduces memory usage. But my findings also show that it is not helpful for large builds.

I hope you enjoyed this explorative blog post in the realm of JIT compilation. It's preliminary research; maybe if there's enough interest, I can do a more thorough investigation. See you in my next blog post on debugging, which should be ready by the end of this week.

*Additional Literature:* I would recommend reading [Startup, containers \& Tiered Compilation](https://jpbempel.github.io/2020/05/22/startup-containers-tieredcompilation.html) by Jean-Philippe Bempel if you want to get another take on the impact of C2 on startup time in constrained environments.

*This project is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com), making profiling easier for everyone. *This article first appeared on my personal blog [mostlynerdless.de](https://mostlynerdless.de).* Thank you to Francesco Nigro for the idea to check whether disabling the JIT improves the build times.*
