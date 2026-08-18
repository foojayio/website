---
title: "How Gradle Works: Startup"
date: "2023-02-09T08:45:57+00:00"
lastmod: "2023-03-14T11:34:13+00:00"
description: "This is the first of a series on how Gradle works! For example, how does Gradle start up and how many JVMs are involved in a Gradle build?"
canonical: "https://blog.gradle.org/how-gradle-works-1"
authors:
  - "bo-zhang"
image: "local-distribution-startup.png"
categories:
  - "Gradle"
  - "Java Core"
  - "Tutorials"
related_posts:
  - "how-gradle-works-inside-the-daemon"
  - "compilation-avoidance-with-gradle"
  - "introducing-gradle-test-suites"
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
frozen: false
---

This is the first article of a series *How Gradle Works*, which includes the following topics:

* How Gradle starts up
* How many JVMs are involved in a Gradle build
* What happens in each JVM during the build

We'll explain the first topic *How Gradle Starts Up* in this blog.

Before reading on, we assume you are familiar with basic JVM/Gradle concepts (jar, classpath, wrapper, daemon, project, task, etc.).

## How Gradle Starts Up

There are many ways to start a Gradle build:

* Local Gradle Distribution in CLI: `/path/to/local/distribution/bin/gradle `
* Gradle Wrapper in CLI: `./gradlew `
* Click a button in your IDE to import a Gradle project or run some tests/tasks

What's the difference? What happens under the hood?

Before we start, we need to remember that Gradle is software running on top of a JVM ([Java Virtual Machine](https://en.wikipedia.org/wiki/Java_virtual_machine)).

### Local Gradle Distribution in CLI

You may have downloaded a Gradle distribution to your local computer, either manually or by some package management tools.

Suppose you start a Gradle build by typing `/path/to/local/distribution/bin/gradle `.

What happens next?

Open the `/path/to/local/distribution/bin/gradle` in your favorite text editor, you'll find that it's nothing magic but a plain text file, or more precisely, a shell script file. When you say `/path/to/local/distribution/bin/gradle `,a shell process is started and starts executing the code in the script. At the end of this script, you can see a line: `exec "$JAVACMD" "$@"`

So the script's job is simple: it finds the `java` command, determines the parameters, and starts a JVM by invoking `exec`.

`exec` executes a command in the same process, i.e. replaces the current process with a JVM process.

![](https://blog.gradle.org/images/how-gradle-works/local-distribution-startup.png)

The entry point of `Gradle Client JVM` is [`org.gradle.launcher.GradleMain` class](https://github.com/gradle/gradle/blob/acc6044325b11874e9626d98dec976a0e495cb62/subprojects/bootstrap/src/main/java/org/gradle/launcher/GradleMain.java).

The JVM started by `exec` is a very lightweight JVM. Let's call it `Gradle Client JVM` because it doesn't run any real build logic.

It searches for a compatible [Gradle Daemon](https://docs.gradle.org/current/userguide/gradle_daemon.html) and connects to it via a local socket if found.

If there is no such daemon running, it will start a daemon, which is also a JVM (`Gradle Daemon JVM`).

Later,

1. The `Gradle Client JVM` forwards input (command line arguments, environment variables, stdin, etc.) to the `Gradle Daemon JVM`.
2. The `Gradle Daemon JVM` runs the build, and sends output (stdout/stderr) back to the `Gradle Client JVM`.
3. After the build finishes, the `Gradle Client JVM` will exit, but the `Gradle Daemon JVM` may stay running for some time.

![](https://blog.gradle.org/images/how-gradle-works/client-daemon.png)

However, there is one exception for this workflow. If you pass `--no-daemon` to the Gradle build, the client JVM may convert itself into a daemon JVM if it is [compatible with build requirements](https://docs.gradle.org/current/userguide/gradle_daemon.html#compatibility).

In this case, there is no daemon, no communication, no input/output forwarding at all - the build happens inside the single JVM:

![](https://blog.gradle.org/images/how-gradle-works/no-daemon.png)

But if `--no-daemon` is present and the client JVM is not compatible with build requirements, a new disposable JVM will still be started for the build and exit at the end of the build:

![](https://blog.gradle.org/images/how-gradle-works/no-daemon-not-compatible.png)

This is how local Gradle distribution starts up on UNIX OSes. On Windows, the mechanism is very similar - the only difference is that we invoke `/path/to/distribution/bin/gradle.bat` instead of `/path/to/distribution/bin/gradle`.

### Gradle Wrapper in CLI

As you may know, [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) is the recommended way to execute Gradle builds.  

What happens when you type `./gradlew `? What's the difference with running from local Gradle distribution?

If you open `gradlew` in a text editor, you'll find it very similar to what we have explained in the last section: there is no magic, just a shell script. It starts a tiny JVM from `gradle/wrapper/gradle-wrapper.jar` in your project.

This JVM will locate or download a specific version of Gradle distribution declared in `gradle/wrapper/gradle-wrapper.properties`, then it will start Gradle inside the same JVM via Java reflection. After that, this tiny JVM acts as the `Gradle Client JVM `in the way explained in the last section.

![](https://blog.gradle.org/images/how-gradle-works/wrapper-daemon.png)

The entry point of `Gradle Wrapper JVM` is the [`org.gradle.wrapper.GradleWrapperMain` class](https://github.com/gradle/gradle/blob/acc6044325b11874e9626d98dec976a0e495cb62/subprojects/wrapper/src/main/java/org/gradle/wrapper/GradleWrapperMain.java).

### Gradle in IDE

In the previous sections, `Gradle Client JVM` is a dedicated JVM started via CLI.

Actually, it doesn't have to be a dedicated JVM.

Gradle client can be a part of another JVM, i.e. a JVM that loads some Gradle jars dynamically and acts as a "Gradle client": searches/connects to the daemon, starts a new daemon, and communicates with the daemon.

This programmatic API is called [Tooling API](https://docs.gradle.org/current/userguide/third_party_integration.html#embedding).

![](https://blog.gradle.org/images/how-gradle-works/ide-tapi-daemon.png)

For example, when you click `Gradle Sync` button in IntelliJ IDEA, IDEA will start a special Gradle build to fetch necessary information (project structure, dependencies, tasks, etc.) of the project via the Tooling API.

Still, all the build logic happens in a `Gradle Daemon JVM`, and the Tooling API just reads a build result and returns it to the caller.

## What's Next

In the next blog post of the series we'll explain how many JVMs are involved in a Gradle build.

[Visit the original article here to leave comments.](https://blog.gradle.org/how-gradle-works-1)
