---
title: "Getting Started with bld"
slug: "getting-started-with-bld"
date: "2024-04-15T18:31:32+00:00"
lastmod: "2024-04-16T13:24:32+00:00"
description: "bld is an up-and-coming build tool for the Java ecosystem. This article guides you through getting set up to use it in your own projects."
authors:
  - "ethan-mccue"
image: "bld-create.png"
categories:
  - "Developer Tools"
  - "Java"
  - "Java Beginner"
  - "RIFE2"
  - "Tools"
tags:
related_posts:
  - "introducing-bld-a-new-pure-java-build-system"
  - "spring-boot-local-development-enhancements-lets-compose"
  - "why-i-moved-my-blog-to-rife2-after-23-years"
enlighterjs: true
frozen: false
---

**bld is an up-and-coming build tool for the Java ecosystem. This article guides you through getting set up to use it in your own projects.**

What is `bld`? {#h2-0-what-is-bld}
----------------------------------

`bld` is a build tool for the Java ecosystem.

Why use `bld`? {#h2-1-why-use-bld}
----------------------------------

`bld` is a very simple build tool.

Unlike its contemporaries `Maven` and `Gradle`, which concern themselves with abstract models of a project, lifecycles, declarative DSLs, and avoiding repeated work, `bld` just runs Java code.

There are no `.xml` files, no Groovy/Kotlin DSLs to learn, and no [corporate trainings to attend](https://gradle.com/training/).

If you know Java, you already have the needed skill-set.

Once you try it, you'll get it.

Installation {#h2-2-installation}
---------------------------------

The easiest way to install `bld` is to use [`SDKMAN!`](https://sdkman.io/).

```
sdk install bld
```


Other installation methods including `brew`, `jbang` are [documented in the bld repo](Installation).

If you have an aversion to package management tools, you can also download the JAR [directly from the releases page](https://github.com/rife2/bld/releases/latest).

Make a Project {#h2-3-make-a-project}
-------------------------------------

If you installed `bld` with your package manager, then you should run:

```
bld create
```


After which you will be prompted for the kind of project you want to create.

```
Please enter a number for the project type:
  1: base   (Java baseline project)
  2: app    (Java application project)
  3: lib    (Java library project)
  4: rife2  (RIFE2 web application)
```


For the purposes of following along, select an `app` project.

If you downloaded `bld` as a jar from the releases page, then you should instead run:

```
java -jar bld-1.9.0.jar create
```


> **NOTE:** By the time you read this it is likely that the latest version is not `1.9.0`,  
>
> so just substitute whatever the current name of the jar is.

After this you will be prompted to enter a package name.

```
bld create
Please enter a number for the project type:
  1: base   (Java baseline project)
  2: app    (Java application project)
  3: lib    (Java library project)
  4: rife2  (RIFE2 web application)
2
Please enter a package name (for instance: com.example):
```


If you aren't familiar with the Java ecosystem, generally projects put their code in a package hierarchy.

This serves an important social purpose, but if you don't know what to put you can just use `com.example `or `io.github.YOUR_GITHUB_USERNAME`.

Once you've entered that, you will be asked for a project name.

```
Please enter a number for the project type:
  1: base   (Java baseline project)
  2: app    (Java application project)
  3: lib    (Java library project)
  4: rife2  (RIFE2 web application)
2
Please enter a package name (for instance: com.example):
com.example
Please enter a project name (for instance: myapp):
```


Choose whatever you want for this. If you are just following along, use `myapp`.

Working with a `bld` Project {#h2-4-working-with-a-bld-project}
---------------------------------------------------------------

Once you've run the commands above, a folder should be generated which is structured like the following.

```
.
├── bld
├── bld.bat
├── lib
│   ├── bld
│   │   ├── bld-wrapper.jar
│   │   └── bld-wrapper.properties
│   ├── compile
│   ├── runtime
│   └── test
│       ├── apiguardian-api-1.1.2-sources.jar
│       ├── apiguardian-api-1.1.2.jar
│       ├── junit-jupiter-5.10.2-sources.jar
│       ├── junit-jupiter-5.10.2.jar
│       ├── junit-jupiter-api-5.10.2-sources.jar
│       ├── junit-jupiter-api-5.10.2.jar
│       ├── junit-jupiter-engine-5.10.2-sources.jar
│       ├── junit-jupiter-engine-5.10.2.jar
│       ├── junit-jupiter-params-5.10.2-sources.jar
│       ├── junit-jupiter-params-5.10.2.jar
│       ├── junit-platform-commons-1.10.2-sources.jar
│       ├── junit-platform-commons-1.10.2.jar
│       ├── junit-platform-console-standalone-1.10.2-sources.jar
│       ├── junit-platform-console-standalone-1.10.2.jar
│       ├── junit-platform-engine-1.10.2-sources.jar
│       ├── junit-platform-engine-1.10.2.jar
│       ├── opentest4j-1.3.0-sources.jar
│       └── opentest4j-1.3.0.jar
└── src
    ├── bld
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── MyappBuild.java
    │   └── resources
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── MyappMain.java
    │   └── resources
    │       └── templates
    └── test
        ├── java
        │   └── com
        │       └── example
        │           └── MyappTest.java
        └── resources
```


> **NOTE:** Just like the version number of `bld` will evolve, so will the version  
>
> numbers of the test dependency jars in the listing above.

In this case `src/main/java/com/example/MyappMain.java` should contain something like the following.

```
package com.example;

public class MyappMain {
    public String getMessage() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        System.out.println(new MyappMain().getMessage());
    }
}
```


And `src/bld/java/com/example/MyappBuild.java` should look like this.

```
package com.example;

import rife.bld.Project;

import java.util.List;

import static rife.bld.dependencies.Repository.*;
import static rife.bld.dependencies.Scope.*;

public class MyappBuild extends Project {
    public MyappBuild() {
        pkg = "com.example";
        name = "Myapp";
        mainClass = "com.example.MyappMain";
        version = version(0,1,0);

        downloadSources = true;
        repositories = List.of(MAVEN_CENTRAL, RIFE2_RELEASES);
        scope(test)
            .include(dependency("org.junit.jupiter", "junit-jupiter", version(5,10,2)))
            .include(dependency("org.junit.platform", "junit-platform-console-standalone", version(1,10,2)));
    }

    public static void main(String[] args) {
        new MyappBuild().start(args);
    }
}
```


> **NOTE:** `bld` supports different ways to describe dependencies, dependency("org.junit.jupiter", "junit-jupiter", version(5,10,2)) can for instance also be written as dependency("org.junit.jupiter:junit-jupiter:5.10.2"). Which format you use, is a matter of personal taste.

You can now open the project in your editor of choice. Out of the box, the needed configuration for `IntelliJ` and `VSCode` will be present.

From this point on, you should use the generated `bld` and `bld.bat` (for Windows folks) files instead any `bld` command that you globally installed.

Running `./bld` or `bld.bat` will show you the commands that are available to you.

```
Welcome to bld 1.9.0.

The bld CLI provides its features through a series of commands that
perform specific tasks. The help command provides more information about
the other commands.

Usage : help [command]

The following commands are supported.

  clean            Cleans the build files
  compile          Compiles the project
  dependency-tree  Outputs the dependency tree of the project
  download         Downloads all dependencies of the project
  help             Provides help about any of the other commands
  jar              Creates a jar archive for the project
  jar-javadoc      Creates a javadoc jar archive for the project
  jar-sources      Creates a sources jar archive for the project
  javadoc          Generates javadoc for the project
  precompile       Pre-compiles RIFE2 templates to class files
  publish          Publishes the artifacts of your project
  publish-local    Publishes to the local maven repository
  purge            Purges all unused artifacts from the project
  run              Runs the project (take option)
  test             Tests the project with JUnit (takes options)
  uberjar          Creates an UberJar archive for the project
  updates          Checks for updates of the project dependencies
  version          Outputs the version of the build system

  -?, -h, --help    Shows this help message
  -D<name>=<value>  Set a JVM system property
  -s, --stacktrace  Print out the stacktrace for exceptions
```


The most immediately useful commands will be `./bld compile` and `./bld run`.

You need to run `./bld compile` before `./bld run`.

```
./bld compile
Compilation finished successfully.
./bld run
Hello World!
```


Of course, commands can also be combined.

```
./bld compile run
```


Adding a Dependency {#h2-5-adding-a-dependency}
-----------------------------------------------

To add a dependency to your project, you need to edit your build file. If you have been following along, that will be  
`src/bld/java/com/example/MyappBuild.java`.

The line you need to add will look like:

```
scope(compile)
    .include(dependency("com.fasterxml.jackson.core", "jackson-databind", version(2,16,0)))
```


If you don't have a familiarity with the terminology of maven scopes, you can safely use `scope(compile)` for most things without issue.

```
package com.example;

import rife.bld.Project;

import java.util.List;

import static rife.bld.dependencies.Repository.*;
import static rife.bld.dependencies.Scope.*;

public class MyappBuild extends Project {
    public MyappBuild() {
        pkg = "com.example";
        name = "Myapp";
        mainClass = "com.example.MyappMain";
        version = version(0,1,0);

        downloadSources = true;
        repositories = List.of(MAVEN_CENTRAL, RIFE2_RELEASES);
        scope(compile)
            .include(dependency("com.fasterxml.jackson.core", "jackson-databind", version(2,16,0)));
        scope(test)
            .include(dependency("org.junit.jupiter", "junit-jupiter", version(5,10,2)))
            .include(dependency("org.junit.platform", "junit-platform-console-standalone", version(1,10,2)));
    }

    public static void main(String[] args) {
        new MyappBuild().start(args);
    }
}
```


Then you need to run `./bld download` to get any new dependencies. This is similar to the JavaScript  

world where you need to run `npm install`.

After this, you can start to use any classes brought in by those dependencies in your project.

Writing a Test {#h2-6-writing-a-test}
-------------------------------------

An example test should have been generated under `src/test/`:

```
package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MyappTest {
    @Test
    void verifyHello() {
        assertEquals("Hello World!", new MyappMain().getMessage());
    }
}
```


[`JUnit`](https://junit.org/junit5/) is included by default, and you can run any tests you write with `./bld test`.

```
./bld test
Test plan execution started. Number of static tests: 1
╷
├─ JUnit Jupiter
│  ├─ MyappTest
│  │  ├─ verifyHello()
│  │  │       tags: []
│  │  │   uniqueId: [engine:junit-jupiter]/[class:com.example.MyappTest]/[method:verifyHello()]
│  │  │     parent: [engine:junit-jupiter]/[class:com.example.MyappTest]
│  │  │     source: MethodSource [className = 'com.example.MyappTest', methodName = 'verifyHello', methodParameterTypes = '']
│  │  │   duration: 24 ms
│  │  │     status: ✔ SUCCESSFUL
│  └─ MyappTest finished after 51 ms.
└─ JUnit Jupiter finished after 67 ms.
Test plan execution finished. Number of all tests: 1

Test run finished after 124 ms
[         2 containers found      ]
[         0 containers skipped    ]
[         2 containers started    ]
[         0 containers aborted    ]
[         2 containers successful ]
[         0 containers failed     ]
[         1 tests found           ]
[         0 tests skipped         ]
[         1 tests started         ]
[         0 tests aborted         ]
[         1 tests successful      ]
[         0 tests failed          ]
```


Writing Custom Commands {#h2-7-writing-custom-commands}
-------------------------------------------------------

If you have any custom logic you want to run, you need to add a method to the build class and annotate it with `@BuildCommand`.

```
package com.example;

import rife.bld.BuildCommand;
import rife.bld.Project;

import java.util.List;

import static rife.bld.dependencies.Repository.*;
import static rife.bld.dependencies.Scope.*;

public class MyappBuild extends Project {
    public MyappBuild() {
        pkg = "com.example";
        name = "Myapp";
        mainClass = "com.example.MyappMain";
        version = version(0,1,0);

        downloadSources = true;
        repositories = List.of(MAVEN_CENTRAL, RIFE2_RELEASES);
        scope(compile)
            .include(dependency("com.fasterxml.jackson.core", "jackson-databind", version(2,16,0)));
        scope(test)
            .include(dependency("org.junit.jupiter", "junit-jupiter", version(5,10,2)))
            .include(dependency("org.junit.platform", "junit-platform-console-standalone", version(1,10,2)));
    }

    public static void main(String[] args) {
        new MyappBuild().start(args);
    }

    @BuildCommand(summary = "Says Hello")
    public void hello() {
        System.out.println("Hello");
    }
}
```


Your new command should show up when you run `./bld`.

```
./bld
Welcome to bld 1.9.0.

The bld CLI provides its features through a series of commands that
perform specific tasks. The help command provides more information about
the other commands.

Usage : help [command]

The following commands are supported.

  ...
  hello            Says Hello
  ...
```


And you can run it with `./bld methodName`:

```
./bld hello
Hello
```


Spring Boot Integration {#h2-8-spring-boot-integration}
-------------------------------------------------------

Chances are you are a [Spring](https://spring.io) developer.

While you don't need to do anything special to use Spring with `bld`, there is an extension that will help you make Spring Boot `JAR`s and `WAR`s.

To use it, edit the `lib/bld/bld-wrapper.properties` file and add this line:

```
bld.extensions=com.uwyn.rife2:bld-spring-boot:0.9.3
```


Then add a task to your project like that uses the classes the extension gives you.

```
@BuildCommand(summary = "Creates an executable JAR for the project")
public void bootjar() throws Exception {
    new BootJarOperation()
            .fromProject(this)
            .execute();
}
```


And you can use it like so.

```
./bld compile bootjar
```


The [repository for the extension](https://github.com/rife2/bld-spring-boot) has further code samples as well as links to example projects.

Conclusion {#h2-9-conclusion}
-----------------------------

Maven has been around since 2004, Gradle since 2008.

Take some time out of your day to try `bld`. It's new, it's different, and you might like it --- a lot.

<br />

<br />
