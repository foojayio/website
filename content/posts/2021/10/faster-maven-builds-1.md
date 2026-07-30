---
title: "How to Do Faster Maven Builds (Part 1) | Foojay.io Today"
slug: "faster-maven-builds-1"
date: "2021-10-14T08:27:35+00:00"
lastmod: "2021-10-14T09:11:33+00:00"
description: "Some techniques how to make your Maven builds faster in this article. The next article will focus on how to do the same inside of Docker."
canonical: "https://blog.frankel.ch/faster-maven-builds/1/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2021/09/Apache_Maven_logo.svg.png"
categories:
  - "Maven"
  - "Performance"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Builds require a few properties, chief among them reproducibility. I would consider speed to be low on the order of priorities. However, it's also one of the most limiting factors to your release cycle: if your build takes *T* , you cannot release faster than each *T*. Hence, you'll probably want to speed up your builds after you've reached a certain maturity level to enable more frequent releases.

I want to detail some techniques you can leverage to make your Maven builds faster in this article. The [next article](https://foojay.io/today/faster-maven-builds-part-2/) focuses on how to do the same inside of Docker.

Baseline {#h2-0-baseline}
-------------------------

Since I want to propose techniques and evaluate their impact, we need a sample repository. I've chosen [Hazelcast code samples](https://github.com/hazelcast/hazelcast-code-samples) because it provides a large enough multi-modules code base with many submodules; the exact commit is [448febd](https://github.com/hazelcast/hazelcast-code-samples/commit/448febd9977b9927a3f00bbf61ba50b2c0d94bb4).

The rules are the following:

* I run the command five times to avoid temporary issues
* I execute `mvn clean` between each run to start from an empty `target` repository
* All dependencies and plugins are already downloaded
* I report the time that Maven displays in the console log: 

<pre class="EnlighterJSRAW" data-enlighter-language="generic">[INFO] -------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] -------------------------------------------------------
[INFO] Total time:  22.456 s (Wall Clock)
[INFO] Finished at: 2021-09-24T23:20:41+02:00
[INFO] -------------------------------------------------------</pre>

Let's start with our baseline, `mvn test`. The results are:

* 02:00 min
* 01:57 min
* 01:58 min
* 01:56 min
* 01:58 min

Using all CPUs {#h2-1-using-all-cpus}
-------------------------------------

By default, Maven uses a single thread. In the age of multicores, this is just waste. It's possible to run parallel builds using multiple threads by setting an absolute number or a number relative to the number of available cores. For more information, please check the [relevant documentation](https://cwiki.apache.org/confluence/display/MAVEN/Parallel+builds+in+Maven+3).

The more submodules that are not dependent on each other you have, *i.e.*, Maven can build them in parallel, the better you'll achieve with this technique. It fits our codebase very well.

We are going to use as many threads as there are available cores. The relevant command is `mvn test -T 1C`.

When the command starts, you should see the following message in the console:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Using the MultiThreadedBuilder implementation with a thread count of X</pre>

* 51.487 s (Wall Clock)
* 40.322 s (Wall Clock)
* 52.468 s (Wall Clock)
* 41.862 s (Wall Clock)
* 41.699 s (Wall Clock)

The numbers are much better but with a higher variance.

Parallel test execution {#h2-2-parallel-test-execution}
-------------------------------------------------------

Parallelization is an excellent technique. We can do the same regarding test execution. By default, the Maven Surefire plugin runs tests sequentially, but it's possible to configure it to run tests in parallel. Please refer to the [documentation](https://maven.apache.org/surefire/maven-surefire-plugin/examples/fork-options-and-parallel-execution.html#Parallel_Test_Execution) for the whole set of options.

This approach is excellent if you've got a large number of units in each module. Note that your tests **need** to be independent of one another.

We will manually set the number of threads:

<pre class="EnlighterJSRAW" data-enlighter-language="shell">mvn test -Dparallel=all -DperCoreThreadCount=false -DthreadCount=16 #1 #2</pre>

1. Configure Surefire to run both classes and methods in parallel
2. Manual override the thread count to 16

Let's run it:

* 02:04 min
* 02:03 min
* 01:46 min
* 01:52 min
* 01:53 min

It seems that the cost of thread synchronization offsets the potential gain of running parallel tests.

Offline {#h2-3-offline}
-----------------------

Maven will check whether a `SNAPSHOT` dependency has a new "version" at every run. It means additional network roundtrips. We can prevent this check with the `--offline` option.

While you should avoid `SNAPSHOT` dependencies, it's sometimes unavoidable, especially during development.

The command is `mvn test -o`, `-o` being the shortcut for `--offline`.

* 01:46 min
* 01:46 min
* 01:47 min
* 01:55 min
* 01:44 min

The codebase has a considerable number of `SNAPSHOT` dependencies; hence offline speeds up the build significantly.

JVM parameters {#h2-4-jvm-parameters}
-------------------------------------

Maven itself is a Java-based application. It means each run starts a new . A JVM first interprets the *bytecode* **and** then analyze the workload and compiles the *bytecode* to *native code* accordingly: it means peak performance, but only after a (long) while. It's great for long-running processes, not so much for command-line applications.

We will likely not reach the peak performance point in the context of builds since they are relatively short-lived, but we are still paying for the analysis cost. We can configure Maven to forego it by configuring the adequate JVM parameters. Several ways of configuring the JVM are available. The most straightforward way is to create a dedicated `jvm.config` configuration file in a `.mvn` subfolder in the project's folder.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">-XX:-TieredCompilation -XX:TieredStopAtLevel=1</pre>

Let's now simply run `mvn test`:

* 01:44 min
* 01:44 min
* 01:53 min
* 01:53 min
* 01:55 min

Maven daemon {#h2-5-maven-daemon}
---------------------------------

The [Maven daemon](https://github.com/mvndaemon/mvnd) is a recent addition to the Maven ecosystem. It draws its inspiration from the [Gradle daemon](https://docs.gradle.org/current/userguide/gradle_daemon.html):
> Gradle runs on the Java Virtual Machine (JVM) and uses several supporting libraries that require a non-trivial initialization time. As a result, it can sometimes seem a little slow to start. The solution to this problem is the Gradle Daemon: a long-lived background process that executes your builds much more quickly than would otherwise be the case. We accomplish this by avoiding the expensive bootstrapping process and leveraging caching by keeping data about your project in memory.

The Gradle team recognized early that a command-line tool was not the best usage of the JVM. To fix that, one keeps a JVM background process, the daemon, always up. It acts as a server while the itself plays the role of the client.

As an additional benefit, such a long-running process loads classes only once (if they didn't change between runs).

Once you have installed the software, you can run the daemon with the `mvnd` command instead of the standard `mvn` one. Here are the results with `mvnd test`:

* 33.124 s (Wall Clock)
* 33.114 s (Wall Clock)
* 34.440 s (Wall Clock)
* 32.025 s (Wall Clock)
* 29.364 s (Wall Clock)

Note that the daemon uses multiple threads by default, with `number of cores - 1`.

Mixing and matching {#h2-6-mixing-and-matching}
-----------------------------------------------

We've seen several ways to speed up the build. What if we used them in conjunction?

Let's first try with every technique we've seen so far in the same run:

<pre class="EnlighterJSRAW" data-enlighter-language="shell">mvnd test -Dparallel=all -DperCoreThreadCount=false -DthreadCount=16 -o #1 #2 #3 #4</pre>

1. Use the Maven daemon
2. Run the tests in parallel
3. Don't update `SNAPSHOT` dependencies
4. Configure the JVM parameters as above via the `jvm.config` file - no need to set any option

The command returns the following results:

* 27.061 s (Wall Clock)
* 24.457 s (Wall Clock)
* 24.853 s (Wall Clock)
* 25.772 s (Wall Clock)

Thinking about it, the Maven daemon *is* a long-running process. For that reason, it stands to reason to let the JVM analyze and compile the *bytecode* to *native code* . We can thus remove the `jvm.config` file and re-run the above command. Results are:

* 23.840 s (Wall Clock)
* 26.589 s (Wall Clock)
* 22.283 s (Wall Clock)
* 23.788 s (Wall Clock)
* 22.456 s (Wall Clock)

Now we can display the consolidated results:

|                        |  Baseline  | Parallel Build | Parallel tests | Offline | JVM params | Daemon | Daemon + offline + parallel tests + parameters | Daemon + offline + parallel tests |
|         #1 (s)         |   120.00   |     51.00      |     128.00     | 106.00  |   104.00   | 33.12  |                     27.06                      |               23.84               |
|         #2 (s)         |   117.00   |     40.00      |     123.00     | 106.00  |   104.00   | 33.11  |                     24.46                      |               26.59               |
|         #3 (s)         |   118.00   |     52.00      |     106.00     | 107.00  |   113.00   | 34.44  |                     24.85                      |               22.28               |
|         #4 (s)         |   116.00   |     42.00      |     112.00     | 115.00  |   113.00   | 32.03  |                     25.77                      |               23.79               |
|         #5 (s)         |   118.00   |     42.00      |     113.00     | 104.00  |   115.00   |        |                     29.36                      |               22.46               |
|      Average (s)       | \*117.80\* |     45.40      |     116.40     | 107.60  |   109.80   | 32.41  |                     25.54                      |               23.79               |
|       Deviation        |    1.76    |     25.44      |     63.44      |  14.64  |   22.96    |  2.91  |                      1.00                      |               2.38                |
| Gain from baseline (s) |     -      |     72.40      |      1.40      |  10.20  |    8.00    | 85.39  |                     92.26                      |             **94.01**             |
|         % gain         |     -      |     61.46%     |     1.19%      |  8.66%  |   6.79%    | 72.48% |                     78.32%                     |            **79.80%**             |
|------------------------|------------|----------------|----------------|---------|------------|--------|------------------------------------------------|-----------------------------------|

Conclusion {#h2-7-conclusion}
-----------------------------

In this post, we have seen several ways to speed up your Maven build. Here's the summary:

* **Maven daemon**: A solid, safe starting point
* **Parallelize builds**: When the build contains multiple modules that are independent of each other
* **Parallelize tests**: When the project contains multiple tests
* **Offline** : When the project contains `SNAPSHOT` dependencies *and* you don't need to update them
* **JVM parameters**: When you want to go the extra mile

I'd advise every user to start using the Maven daemon and continue optimizing if necessary and depending on your project.

In the next post, we will focus on speeding your Maven builds in-container.

**To go further:**

* [Parallel builds in Maven 3](https://cwiki.apache.org/confluence/display/MAVEN/Parallel+builds+in+Maven+3)
* [Surefire Parallel Test Execution](https://maven.apache.org/surefire/maven-surefire-plugin/examples/fork-options-and-parallel-execution.html#Parallel_Test_Execution)
* [mvnd - the Maven Daemon](https://github.com/mvndaemon/mvnd)

*Originally published at [A Java Geek](https://blog.frankel.ch/faster-maven-builds/1/) on October 3^rd^, 2021*

*[JVM]: Java Virtual Machine
*[CLI]: Command-Line Interface
