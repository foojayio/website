---
title: "Eliminate the Nightmare of Debugging Java Concurrency Bugs"
slug: "jcstress"
date: "2021-02-10T17:58:33+00:00"
lastmod: "2021-02-10T18:07:23+00:00"
description: "Writing concurrent programs is hard, testing concurrent programs is harder, and debugging concurrent programs is a nightmare!"
canonical: "https://jfeatures.com/blog/jcstress"
authors:
  - "vipin-sharma"
image: "Favicon-3-2.png"
categories:
  - "Tools"
tags:
related_posts:
  - "indexing-all-of-wikipedia-on-a-laptop"
  - "new-jdkmonitor"
  - "disco-api-helping-you-to-find-any-openjdk-distribution"
  - "debugging-openjdk-tests-in-vscode-without-losing-your-mind"
enlighterjs: true
frozen: false
---

Writing concurrent programs is hard, testing concurrent programs is harder, and debugging concurrent programs is a nightmare. An incorrect concurrent program can run for years, tricking us to believe it is stable code, and it fails when we least expect.

JCStress is a concurrency stress test tool used by JVM developers to test the correctness of the JVM itself.

The OpenJDK provides this amazing tool to test the correctness of your concurrent programs.

### A First JCStress Test {#h3-0-a-first-jcstress-test}

We configure JCStress tests using annotations provided by the JCStress framework. The following are important annotations help us to understand our first JCStress test:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Actor: Annotates methods that hold the actions done by the threads.

@State: Annotates the class that holds the data mutated/read by the tests.

@Result: Marks the result object.

@JCStressTest: Marks the class as a JCStress test.

@Outcome: Describes the test outcome, and how to deal with it.</pre>

This is a basic example of the JCStress test using the annotations above:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@JCStressTest

// These are the test outcomes.
@Outcome(id = "1, 1", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Both actors came up with the same value: atomicity failure.")
@Outcome(id = "1, 2", expect = Expect.ACCEPTABLE, desc = "actor1 incremented, then actor2.")
@Outcome(id = "2, 1", expect = Expect.ACCEPTABLE, desc = "actor2 incremented, then actor1.")

// This is a state object
@State
public class APISample_01_Simple {

    int v;

    @Actor
    public void actor1(II_Result r) {
        r.r1 = ++v; // record result from actor1 to field r1
    }

    @Actor
    public void actor2(II_Result r) {
        r.r2 = ++v; // record result from actor2 to field r2
    }

}</pre>

Following are few important points about the above test:

1. Class APISample_01_Simple has a field int v, it is annotated with @State, and it stores the state of the object.
2. For each test executed:  
   -- Methods actor1 and actor2 are executed exactly once.  
   -- Methods actor1 and actor2 are called by particular threads, e.g., Thread t1 calls actor1, and Thread t2 calls actor2.  
   -- Per @State object (here an instance of APISample_01_Simple) JCStress framework calls actor1 and actor2 only once.  
   -- The invocation order of methods actor1 and actor2 is deliberately not specified.  
   -- It stores the state in the same instance of APISample_01_Simple and records the result in the same instance of II_Result.  
   -- Result is available in the instance of II_Result after actor1 and actor2 are completed.
3. Class II_Result is annotated with @Result, to store the result of the test it usages 2 int type fields r1 and r2.
4. Depending upon the output of the test we tag it with corresponding @Outcome, e.g., for r1=1 and r2=2, the result is Expect.ACCEPTABLE.  
   for r1=1 and r2=1, result is Expect.ACCEPTABLE_INTERESTING.

### Setting Up a Project with JCStress {#h3-1-setting-up-a-project-with-jcstress}

To use jcstress in your project, it is recommended to create the submodule with the jcstress tests, which would use jcstress libraries and build steps.

Easy and quick way to create jcstress project is to create a standalone JCStress project using maven archetype, for this you need maven and JDK 8+.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">mvn archetype:generate \
 -DinteractiveMode=false \
 -DarchetypeGroupId=org.openjdk.jcstress \
 -DarchetypeArtifactId=jcstress-java-test-archetype \
 -DgroupId=com.jfeatures \
 -DartifactId=jcstresstest \
 -Dversion=1.0</pre>

[This](https://github.com/Vipin-Sharma/jcstresstest) is a Github project created using the above command.

Running tests, if we have not created any test it will execute default test available.

<pre class="EnlighterJSRAW" data-enlighter-language="java">cd jcstresstest
mvn clean install
java -jar target/jcstress.jar</pre>

In case we want to execute a particular test use -t option like below

<pre class="EnlighterJSRAW" data-enlighter-language="generic">java -jar target/jcstress.jar -t ConcurrencyTest</pre>

Open test report:

`firefox results/index.html`

<pre class="EnlighterJSRAW" data-enlighter-language="java">firefox results/index.html</pre>

### More JCStress APIs {#h3-2-more-jcstress-apis}

JCStress provides useful APIs to develop Java concurrency tests. In this section, we are going to discuss these APIs and how they solve different types of problems.

#### Using Result Classes in JCStress

JCStress ships lots of pre-canned result classes. e.g. II_Result and III_Result, here I represent an int.  

The following is a list of different letters used in JCStress result classes.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">I:  int
Z:  boolean
F:  float
J:  long
S:  short
B:  byte
C:  char
D:  double
L:  object</pre>

With the help of this, we can try to find a pre canned Result class in JCStress, for example:

1. for Result having int(I) and boolean(Z) we can use IZ_Result.
2. for Result having float(F) and long(J) we can use FJ_Result.

Also note:

* **Arbiters.** The method annotated with @Arbiter runs after all method annotated with @Actor are executed and therefore can observe the final result. [This](https://github.com/openjdk/jcstress/blob/master/jcstress-samples/src/main/java/org/openjdk/jcstress/samples/APISample_02_Arbiters.java) is a test using Arbiters.
* **Signal.** The signal is useful for delivering a termination signal to the Actor in Mode.Termination tests. It will run after Actor in question started executing. For the termination test, we use annotation @JCStressTest(Mode.Termination). [This](https://github.com/openjdk/jcstress/blob/master/jcstress-samples/src/main/java/org/openjdk/jcstress/samples/APISample_03_Termination.java) is a test using Signal.
* **Nested tests.** It is sometimes convenient to put the tests in the same source file for better comparison. JCStress allows to nest the tests. [This](https://github.com/openjdk/jcstress/blob/master/jcstress-samples/src/main/java/org/openjdk/jcstress/samples/APISample_04_Nesting.java) is a test showing nested tests in JCStress.
* **Shared Metadata.** Shared Metadata is used when more than one test share the outcomes and other metadata. To use common metadata for such tests, there is a special annotation @JCStressMeta. We can specify class in annotation @JCStressMeta to use metadata of that class. [This](https://github.com/openjdk/jcstress/blob/master/jcstress-samples/src/main/java/org/openjdk/jcstress/samples/APISample_05_SharedMetadata.java) is a test class having example of metadata.

#### Adding Test Descriptions and References

JCStress also allows putting the descriptions and references right at the test. It helps to identify the goal for the test, as well as the discussions about the behavior in question.

Following are the annotations to add the descriptions and references:

<pre class="EnlighterJSRAW" data-enlighter-language="java">@Description("Sample Hello World test")
@Ref("http://openjdk.java.net/projects/code-tools/jcstress/")</pre>

### JCStress Options {#h3-3-jcstress-options}

#### Vm Options

1. -XX:+StressLCM -XX:+StressGCM  
   LCM stands for Local Code Motion, GCM stands for Global Code Motion. For performance optimization compiler may reorder independent instructions without changing the semantics of the code. The compiler tries to find the most optimal order of instructions. When we use StressLCM and StressGCM options, the instruction scheduling works differently. It chooses a random instruction order which is allowed under the Java memory model. It helps to simulate different scenarios
2. -XX:-TieredCompilation -XX:TieredStopAtLevel=1  
   Use -XX:-TieredCompilation or -XX:TieredStopAtLevel=1 to disable C1 or C2 respectively.
3. -Xint  
   Runs the application in interpreted-only mode. Compilation to native code is disabled, and all bytecode is executed by the interpreter. The performance benefits offered by the just in time (JIT) compiler are not present in this mode.

#### Command Line Options

JCStress has got a couple of helpful command line options, we can pass option -h to get a full list of command line options.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">java -jar target/jcstress.jar -h</pre>

Some important options are:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Modes (-m)
Test name(-t)
verbose result (-v)
Number of CPUs used (-c)</pre>

***option -m***sets the test mode, from sanity to stress, going from simple test to more rigorous, and for concurrency issues more rigorous is more helpful. At least once we should run our test with stress option, and then we can run tests in the quick mode as part of CI. Following are sample commands to run different modes:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">java -jar target/jcstress.jar -t ConcurrencyTest -m sanity
java -jar target/jcstress.jar -t ConcurrencyTest -m quick
java -jar target/jcstress.jar -t ConcurrencyTest -m default
java -jar target/jcstress.jar -t ConcurrencyTest -m tough
java -jar target/jcstress.jar -t ConcurrencyTest -m stress</pre>

### Conclusion {#h3-4-conclusion}

Knowing features like this helps you get the best Java jobs, that's why to help you I wrote the e-book [5 steps to Best Java Jobs](https://jfeatures.com/). Download this step by step guide for free!

Resources: [JCStress GitHub](https://github.com/openjdk/jcstress)
