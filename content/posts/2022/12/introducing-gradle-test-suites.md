---
title: "Introducing Gradle Test Suites | Foojay.io Today"
slug: "introducing-gradle-test-suites"
date: "2022-12-06T13:28:26+00:00"
lastmod: "2022-12-06T16:40:51+00:00"
description: "Did you know? In Gradle 7.3, released November 2021, the Gradle team introduced a new feature called Declarative Test Suites."
canonical: "https://blog.gradle.org/introducing-test-suites"
authors:
  - "tom-tresansky"
image: "https://blog.gradle.org/images/introducing-test-suites/default-test-layout.png"
categories:
  - "Gradle"
  - "Java Core"
  - "Tutorials"
tags:
related_posts:
frozen: false
---

This article was initially published at [blog.gradle.org](https://blog.gradle.org/introducing-test-suites)

As projects grow in size and complexity and otherwise mature, they tend to accumulate a large collection of automated tests.

Testing your software at [multiple levels of granularity](https://martinfowler.com/articles/practical-test-pyramid.html) is important to surface problems quickly and to increase developer productivity.

In [Gradle 7.3](https://docs.gradle.org/7.3/release-notes.html#new-features-and-usability-improvements), released November 2021, the Gradle team introduced a new feature called Declarative Test Suites.

Using this feature makes it much easier to manage different types of tests within a single Gradle JVM project without worrying about low level "plumbing" details.

Why Test Suites? {#h2-0-why-test-suites}
----------------------------------------

Normally - whether or not you're practicing strict Test Driven Development - as you develop a project you will continuously add new unit tests alongside your production classes.

By convention, for a Java project, these tests live in `src/test/java`:

[![layout](https://blog.gradle.org/images/introducing-test-suites/default-test-layout.png "layout")](http:/https://blog.gradle.org/images/introducing-test-suites/default-test-layout.png/ "layout")

These unit tests ensure your classes behave correctly in isolation from the very beginning of your project's lifecycle.

At some point later in development, you will be ready to test how your classes work together to create a larger system using integration tests.

Later still, as a final step in certifying that your project works as designed, you will probably want to run the entire system in end-to-end tests which check functional requirements, measure performance, or otherwise confirm its readiness for release.

There are a lot of error-prone details you need to consider in this process.  

Test Suites was created to improve this situation, in response to the hardships detailed below.

### Considerations when setting up additional tests {#h3-1-considerations-when-setting-up-additional-tests}

Varied test goals often involve different and incompatible patterns.

At a minimum you'll want to organize your test code by separating tests into different directories for each goal:

[![other tests](https://blog.gradle.org/images/introducing-test-suites/adding-other-tests.png "other tests")](https://blog.gradle.org/images/introducing-test-suites/adding-other-tests.png "other tests")

But separating the source files is only the beginning.

These types of tests might require different preconditions to be met prior to testing, utilize entirely different runtime and compile time dependencies, or interact with different external systems.

The very testing framework (such as JUnit 4 or 5, TestNG or Spock) used to write and run each group of tests could be different.

To correctly model and isolate these differences in Gradle, you'll need to do more than just separate the tests' source files.

Each group of tests will require its own:

* Separate `SourceSet`, that will provide a distinct `Configuration` for declaring dependencies for the group to compile and run against. You want to avoid leaking unnecessary dependencies across multiple groups of tests, yet still automatically resolve any shared dependencies needed for test compilation and execution.
* Support for using different testing frameworks to execute the tests in each group.
* A `Test` task for each group which might have different task dependencies to provide setup and finalization requirements. You also may want to prevent every type of test from running every time you build the project (for instance, to save any long-running smoke tests for when you think you're ready to publish).

Each component you create in your build scripts to support these requirements must be properly wired into the Gradle project model to avoid unexpected behavior.

Accomplishing this is error-prone, as it requires a thorough understanding of low-level Gradle concepts.  

It also requires modifications and additions to multiple blocks of the DSL.

That's hardly ideal; setting up testing is a single concern and the configuration for it should be co-located and easily discoverable within a buildscript.

### Wiring integration tests without Test Suites {#h3-2-wiring-integration-tests-without-test-suites}

It's helpful to look at a complete example.

Before diving in, take a moment to think about how you would create a separate set of integration tests within a project.

Before Gradle 7.2, the proper way to set up integration tests went like this (note that while this example is written in the Gradle Kotlin DSL, the Groovy setup is very similar):

[![proper set up](https://blog.gradle.org/images/introducing-test-suites/proper-integration-test-setup.png "proper set up")](http://https://blog.gradle.org/images/introducing-test-suites/proper-integration-test-setup.png "proper set up")

1. We need to create a `SourceSet` that will in turn create the associated `Configuration`s we'll need later. This is low-level plumbing that we shouldn't have to focus on.
2. We wire the new test configurations to the test existing configurations, to re-use their dependency declarations. We might not always want to do this.
3. We need to register a `Test` task to run our tests.
4. We ought to add the new task to the appropriate group and set a description - not technically necessary, but a best practice to make the task discoverable and properly locate it in reports.
5. We will write our tests using the latest version of JUnit 5.
6. We need to set up the classpath of our new task - this even more low-level plumbing.
7. We need to tell our new task where the classes it runs live.
8. Finally, we add the necessary JUnit dependencies to the built-in test configurations, which our new configurations extend.\[\^1\]
9. The integration tests have an `implementation` dependency on the current project's production classes - this looks somewhat extraneous in this block that also configures the production dependencies for the project.

Did you get all that?

The bottom line is that this is simply too complex.

**You shouldn't have to be a build expert just to set up thorough testing!**

Test Suites - a better way forward {#h2-3-test-suites-a-better-way-forward}
---------------------------------------------------------------------------

Thinking about the difficulties involved in properly handling this scenario, we realized the current situation was inadequate.

We want to support build authors defining multiple groups of tests with different purposes in a declarative fashion while operating at a high level of abstraction.

Although you could previously write your own plugin (or use a pre-existing solution such as [Nebula](https://nebula-plugins.github.io/)) to hide the details of this and mitigate the complexity, testing is such an ubiquitous need that we decided to provide a canonical solution within Gradle's own core set of plugins.

Thus, Test Suites was born.

The [JVM Test Suite Plugin](https://docs.gradle.org/7.5.1/userguide/jvm_test_suite_plugin.html) provides a DSL and API to model exactly this situation: multiple groups of automated tests with different purposes that live within a single JVM-based project.

It is automatically applied by the `java` plugin, so when you upgrade to Gradle \>= 7.3 you're already using Test Suites in your JVM projects.

Congratulations!

Here is the previous example, rewritten to take advantage of Test Suites:

[![integration tests](https://blog.gradle.org/images/introducing-test-suites/integration-tests-with-suites.png "integration tests")](https://blog.gradle.org/images/introducing-test-suites/integration-tests-with-suites.png "integration tests")

1. All Test Suite configuration is co-located within a new `testing` block, of type [TestingExtension](https://docs.gradle.org/7.5.1/dsl/org.gradle.testing.base.TestingExtension.html#org.gradle.testing.base.TestingExtension).
2. Maintaining backwards compatibility with existing builds that already use the `test` task was an important requirement for us when implementing Test Suites. We've associated the existing `test` task with a default Test Suite that you can use to contain your unit tests.
3. Instead of representing the relationship between unit tests and integration tests by making their backing `Configuration`s extend one another, we keep the machinery powering two types of tests completely separate. This allows for more fine-grained control over what dependencies are needed by each and avoids leaking unnecessary dependencies across test types.\[\^2\]
4. Because each Test Suite could serve very different purposes, we don't assume they have a dependency on your project (maybe you are testing an external system), so you have to explicitly add one to have access to your production classes in your tests.\[\^7\]

By making use of sensible defaults, Gradle is able to simplify your buildscript significantly.

This script manages to set up a mostly equivalent build as the original but in far fewer lines of code.  

Gradle adds a directory to locate your test code and creates the task that run the tests using the suite name as the basis.

In this case, you can run any tests you write located in `src/integrationTestJava` by invoking `gradlew integrationTest`.

Test Suites aren't limited to Java projects, either. Groovy, Kotlin, Scala, and other JVM-based languages will work similarly when their appropriate plugins are applied. These plugins all also automatically apply the JVM Test Suite Plugin, so you can begin adding tests to `src//` without doing any other configuration.

### Behind the scenes {#h3-4-behind-the-scenes}

This short example takes care of all the considerations of the pre-Test Suites example above.  

But how does it work?

When you configure a suite in the new DSL, Gradle does the following for you:

* Creates a Test Suite named `integrationTest` (typed as a [JvmTestSuite](https://docs.gradle.org/7.5.1/dsl/org.gradle.api.plugins.jvm.JvmTestSuite.html)).
* Creates an `SourceSet` named `integrationTest` containing the source directory `src/java/integrationTest`. This will be registered as a test sources directory, so any highlighting performed by your favorite IDE will work as expected.
* Creates several `Configuration`s derived from the `integrationTest` source set, accessible through the Suite's own `dependencies` block: `integrationTestImplementation`, `integrationTestCompileOnly`, `integrationTestRuntimeOnly`, which work like their similarly named `test` configurations.
* Adds dependencies to these configurations necessary for compiling and running against the default testing framework, which is JUnit Jupiter.
* Registers a `Test` task named `integrationTest` which will run these tests.  
  The most important difference is that using Test Suites will fully isolate any integration test dependencies from any unit test dependencies.  
  It also assumes JUnit Platform as the testing engine for new test suites, unless told otherwise.\[\^3\]

After adding just this minimal block of DSL, you are ready to write integration test classes under `src/integrationTest/java` which are completely separate from your unit tests, and to run them via a new `integrationTest` task.

No contact with low-level DSL blocks like `configurations` is required.

Try it out now {#h2-5-try-it-out-now}
-------------------------------------

Test Suites is still an [`@Incubating` feature](https://docs.gradle.org/7.5.1/userguide/feature_lifecycle.html) as we explore and refine the API, but it's here to stay, and we encourage everyone to try it out now.

For a new project, the easiest way to get started is to use the [Gradle Init task](https://docs.gradle.org/7.5.1/samples/sample_incubating_jvm_multi_project_with_additional_test_types.html) and opt-in to using incubating features when prompted; this will generate a sample project using the new DSL.

Customizing your Suites {#h2-6-customizing-your-suites}
-------------------------------------------------------

The rationale behind Test Suites, just like Gradle in general, is to abstract the details of configuration and use sensible conventions as defaults - but to also allow you to change those defaults as necessary.

[![custom set up](https://blog.gradle.org/images/introducing-test-suites/customizing-test-suites.png "custom set up")](https://blog.gradle.org/images/introducing-test-suites/customizing-test-suites.png "custom set up")

1. Configure the built-in Test Suite to use a different testing framework using one of [several convenience methods available](https://docs.gradle.org/7.5.1/javadoc/org/gradle/api/plugins/jvm/JvmTestSuite.html#useJUnitJupiter--).\[\^4\]
2. Add a non-project dependency for use in compiling and running a Test Suite.
3. Add a dependency which is only used when running tests (in this case, a logging implementation).
4. Access the `integrationTest` task which will be created for this Suite to configure it directly (and lazily\[\^5\]), within the `testing` block.
5. Define an additional `performanceTest` Suite using the bare minimum DSL for a default new Test Suite. Note that this suite will *not* have access to the project's own classes, or be wired to run as part of the build without calling its `performanceTest` task directly.\[\^6\]
6. Suites can be used as task dependencies - this will cause the `check` task to depend on the `integrationTest` task associated with the `integrationTest` Test Suite - the same task we configured in .

For more Test Suite custom configuration examples, see the [JVM Test Suite Plugin](https://docs.gradle.org/7.5.1/userguide/jvm_test_suite_plugin.html) section of the Gradle user guide.

For adding an additional test suite to a more complex and realistic build, see the [Multi-Project sample](https://docs.gradle.org/7.5.1/samples/sample_incubating_jvm_multi_project_with_additional_test_types.html).

The future of testing in Gradle {#h2-7-the-future-of-testing-in-gradle}
-----------------------------------------------------------------------

We have many exciting ideas for evolving Test Suites in the future.

One major use case we want to support is multidimensional testing, where the same suite of tests runs repeatedly in different environments (for instance, on different versions or releases of the JVM).

This is the reason for the seemingly extraneous `targets` block seen in the examples here and in the user guide.

Doing this will likely involve closer Test Suite integration with [JVM Toolchains](https://docs.gradle.org/7.5.1/userguide/toolchains.html#header).

You'll also definitely want to check out the [Test Report Aggregation Plugin](https://docs.gradle.org/7.4/userguide/test_report_aggregation_plugin.html) added in Gradle 7.4, to see how to easily aggregate the results of multiple `Test` task invocations into a single HTML report.

Consolidating test failures and exposing more information about their suites in test reporting is another potential area of future development.

These and other improvements are currently being discussed and implemented, so be sure to keep up to date with the [Gradle Blog](https://blog.gradle.org/) and the latest [Gradle releases](https://gradle.org/releases/).

[Visit the original article here to leave comments.](https://blog.gradle.org/introducing-test-suites)

*Happy testing!*

\[\^1\]: The version is left off `junit-jupiter-engine` because `junit-jupiter-api` will manage setting it - but this might look like a mistake.  

\[\^2\]: In the forthcoming Gradle 8.0, this block will use a new strongly-typed `dependencies` API, which should provide a better experience when declaring test dependencies in your favorite IDE. Watch our [releases](https://gradle.org/releases/) page for more details.  

\[\^3\]: The default test suite retains JUnit 4 as its default runner, for compatibility reasons.  

\[\^4\]: The default test suite is also implicitly granted access to the production source's implementation dependencies for the same reason. When switching testing frameworks, the new framework's dependencies are automatically included.  

\[\^5\]: See the [Lazy Configuration](https://docs.gradle.org/7.5.1/userguide/lazy_configuration.html) section of the user guide for more details on lazy configuration.  

\[\^6\]: Perhaps these tests are meant to exercise a live deployment in a staging environment.  

\[\^7\]: Note that in the forthcoming Gradle 7.6, instead of using `project` you'll need to call the new `project()` method here.
