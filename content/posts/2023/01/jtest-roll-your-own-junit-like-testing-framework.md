---
title: "JTest: Roll Out Your Own JUnit-Like Testing Framework"
slug: "jtest-roll-your-own-junit-like-testing-framework"
date: "2023-01-17T16:11:50+00:00"
lastmod: "2023-01-17T16:12:45+00:00"
description: "In this interactive tutorial and educational project, you'll learn about JUnit internals and the usage of Java annotations and reflection."
authors:
  - "wei-lin"
image: "https://foojay.io/wp-content/uploads/2022/06/chemical-scientist-test-chemistry-chemist-student.jpg"
categories:
  - "Testing"
  - "Tutorials"
tags:
related_posts:
enlighterjs: true
frozen: false
---

In this interactive tutorial and educational project, you'll be helped in your understanding of the following.

1. A basic insight into [JUnit](https://github.com/junit-team/junit4) internals.
2. The usage of Java annotations and reflection.

You are given minimum [starter code](https://github.com/wlnirvana/jtest) (\~20 loc), and will have to develop a simple yet self-contained framework that can be used to write real-world unit tests.

Are you ready? Let's go! 🚀

Task 0: The Big Picture {#h2-0-task-0-the-big-picture}
------------------------------------------------------

Before getting your hands dirty, it is a good idea to get a big picture of what you will code up.

I suppose most Java programmers have some familiarity with JUnit. But this tutorial also targets beginners.

In case you have never used JUnit before, please go over its official ["getting started guide"](https://github.com/junit-team/junit4/wiki/Getting-started/dc07bd59fbded1bbe8eff488477a433828c80518) to learn its core APIs, which can be summarized below:

1. `@Test` annotation to mark a function as a test case.
2. a bunch of `assert...` functions (e.g. `assertEquals`) used in each test case to verify the actual result is same as the expected result.

And... one more almost invisible component:

3. A runner.

As a downstream developer, you don't code against runners directly. But remember that java programs start execution with `public static void main`? If you followed the [JUnit guide](https://github.com/junit-team/junit4/wiki/Getting-started/dc07bd59fbded1bbe8eff488477a433828c80518) closely, you probably have noticed that you didn't write any `main` method, yet your tests run just fine. How is that possible?

The answer is the runner. In the ["Run the test"](https://github.com/junit-team/junit4/wiki/Getting-started/dc07bd59fbded1bbe8eff488477a433828c80518#run-the-test) section of the guide, you do not start your test class directly, but run a `org.junit.runner.JUnitCore` class instead. That is the runner. Your test class is passed as an argument to the runner.

So three main components in total, in nine steps below.

Task 1: `@JTest` - Marker for Test Cases {#h2-1-task-1-jtest-marker-for-test-cases}
-----------------------------------------------------------------------------------

Annotation is a new language construct introduced in Java 1.5. It is somewhat similar to comment in that it does not contain executable code directly, and is mainly used to provide some additional information to the program.

But unlike comment, which is completely discarded after compilation, annotations can be preserved so that programs can check for these extra information and behave accordingly.

`@Test` of JUnit is an annotation to mark test methods. To provide the same functionality, we have prepared a skeleton class `JTest` for you. But unlike JUnit's `@Test`, which can (almost) only be used against methods, `@JTest` in the skeleton code can be annotated on types as well. Your task is to fix this so that the only valid target of `@JTest` is methods.

In case you haven't written any annotation before, here are some tips:

* `@Retention(...)` is a fixed boilerplate that you can just copy/paste and ignore its meaning for now.
* `@Target(...)` controls where can an annotation be used.

After completing this task, run `T1.java` to check your implementation.

Task 2: Finding Marked Methods {#h2-2-task-2-finding-marked-methods}
--------------------------------------------------------------------

To run the tests, we need to find all the methods annotated by the `@JTest` and execute them one by one. Let's create a dedicated class for this.

This class will be named `JTestRunner`, because eventually it will drive the tests just like runners from JUnit.

In this newly created `JTestRunner` class, please implement the **static** method below.

<pre class="EnlighterJSRAW" data-enlighter-language="java">/**
 * Find all methods annotated by {@link JTest} in a class. 
 * You&nbsp;can&nbsp;assume&nbsp;all&nbsp;its&nbsp;methods&nbsp;are&nbsp;public.
 *
 * @param clazz The class to be analyzed
 * @return all methods annotated by {@link JTest}
 */
public static List&lt;Method&gt; getTestMethods(Class&lt;?&gt; clazz)</pre>

After completing this task, run `T2.java` to check your implementation.

Task 3: Running A Method {#h2-3-task-3-running-a-method}
--------------------------------------------------------

Now if anyone develops tests based on your JTest framework, you can successfully find the test case methods annotated by `@JTest`. The next step, of course, is to run them.

To simplify the case, you can assume `@JTest` is always annotated on **static** methods. This is different from how JUnit works, and is discussed in [next steps](#next-steps) section below.

Please continue editing `JTestRunner` and implement the **static** method below.

<pre class="EnlighterJSRAW" data-enlighter-language="java">/**
 * Run a method via reflection. 
 * You can assume the method is always static.
 *
 * @param method the method to run
 */
public static void runMethod(Method method)</pre>

After completing this task, run `T3.java` to check your implementation.

Task 4: Assertions {#h2-4-task-4-assertions}
--------------------------------------------

During the development of `runMethod` in task 3, you may have noticed that `invoke` can throw exceptions. These exceptions can be categorized into two types:

1. A bug in your **main code** is found, for example by a (not-yet-implemented)`assertEquals` statement, then an exception is thrown to indicate this. We will term this case as the test **FAILED**.
2. The **test code** itself has a bug, for example zero division or NPE, then causes an exception. We will term this case as the test **ERRORED** out.

To distinguish between there two cases, you must first implement the assertions. Please modify `JAssertions.java` such that:

* `public static void assertTrue(boolean condition)` checks if the argument is `true`. When it is not, throw a (JDK built-in) `AssertionError` exception.
* `public static void assertEquals(Object expected, Object actual)` check if the two arguments are equal (in the sense of `.equals(...)`). When they are not, throw an `AssertionError` exception.

After completing this task, run `T4.java` to check your implementation.

Task 5: Test Result Class {#h2-5-task-5-test-result-class}
----------------------------------------------------------

Our goal is to find the test methods with `getTestMethods`, and then execute them with `runMethod`. Each test method may produce several possible results:

1. test passed: method run normally and all assertions passed.
2. test failed: method run normally until an assertion failed.
3. test errored out: method terminated abnormally.

To save the result of a test method (primarily for better output), please write a new class named `JTestResult` with the following two public methods:

* `public String getTestStatus()` returns the result of this test method as a string, which will be one of `PASS`  
  , `FAIL` and `ERROR` respectively.
* `public String getTestMethodName()` returns the name of the test method (annotated by `@JTest`) as a string.

Fow now only the method signature matters, so you can return whatever placeholder value.

After completing this task, run `T5.java` to check your implementation.

Task 6: Returning Test Results {#h2-6-task-6-returning-test-results}
--------------------------------------------------------------------

Update `runMethod` from task 3 so that after invoking the method passed in, it returns a `JTestResult` object containing proper information about the test result.

You may want to use [`getCause`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/Throwable.html#getCause()) to recover the original exception thrown in `invoke`.

**After completing this task, run `T6.java` to check your implementation.**

Task 7: Running A Test Class {#h2-7-task-7-running-a-test-class}
----------------------------------------------------------------

`runMethod` executes only a single method. To run a test class containing multiple test methods, please add the following function `JTestRunner.`

<pre class="EnlighterJSRAW" data-enlighter-language="java">/**
 * Run a test class by executing all its methods annotated by {@link JTest}.
 * You can assume all its methods are static.
 *
 * @param testClassName the full name of the test class
 * @return results of all test cases in the class
 */
public static List&lt;JTestResult&gt; runTestClass(String testClassName)</pre>

After completing this task, run `T7.java` to check your implementation.

Task 8: Entrance {#h2-8-task-8-entrance}
----------------------------------------

Add `public static void main` to `JTestRunner` that starts the test. The `main` method should behave like what JUnit runner does, namely taking the first argument from commandline as the test class, running it, and printing the results.

The output should contain three lines, corresponding to the passed, failed and errored cases respectively.

Concretely, a sample test class written against JTest is provided in `src/test/resources/ToyTest.java`. (Feel free to read it, or compile and run it if you know how to handle classpath issues.) The output for it should be as follows.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">1/3 have passed
Failed tests are: [assertTrueFail]
Errored tests are: [shouldError]</pre>

After completing this task, run `T8.java` to check your implementation.

Task 9: JAR {#h2-9-task-9-jar}
------------------------------

JTest is already a working framework now, but why not go even further to package it as a standalone JAR? This way, downstream developers can use it almost identically with JUnit.

If you are familiar with maven, `mvn package` should generate a jar package in `target/`. But in this task, you need to put your JTest framework jar at `src/test/resources/jtest.jar`. Also make sure JDK11+ in on your `PATH`. Then you should be able to:

1. `cd` into `src/test/resources` directory
2. Compile the test class `ToyTest.java` with `javac -cp .:jtest.jar ToyTest.java`
3. Run the test class with `java -cp .:jtest.jar com.wlnirvana.JTestRunner ToyTest`

After completing this task, run `T9.java` to check your implementation.

Next Steps {#h2-10-next-steps}
------------------------------

Congratulations! You have successfully grown a 20-line skeleton project to a self-contained testing framework. But in case you want to dive deeper, here are some topics you may be interested in:

1. Add `assertThrows` to check if an exception is thrown during a function call. Possible usage of `assertThrows` is given in comments in `ToyTest.ava`.
2. In task 3, we did a simplification that `@JTest` only be annotated on `static` methods. JUnit takes a different approach, for [a good reason](https://martinfowler.com/bliki/JunitNewInstance.html). Can you modify your framework to support this as well?
3. Write more powerful runners. E.g. one that can run all test classes in a package, instead of a single test class.

Credits {#h2-11-credits}
------------------------

This project is inspired by the following efforts.

1. Alonso Del Arte's great tutorial [Making A Java Unit Testing Framework From Scratch](https://medium.com/codex/making-a-java-unit-testing-framework-from-scratch-72208f969565)
2. [JUnit](https://junit.org/)
