---
title: "Unlocking Reproducible Profiler Tests"
slug: "mastering-the-art-of-controlling-the-jit-unlocking-reproducible-profiler-tests"
date: "2023-05-19T09:02:33+00:00"
lastmod: "2023-05-19T15:18:33+00:00"
description: "How to use the WhiteBox API and Compiler Directives to get control of the JIT for better profiling of API tests!"
authors:
  - "johannes-bechberger"
image: "tiered_states4-2000x1415-1.png"
categories:
  - "Performance"
tags:
related_posts:
  - "validating-java-profiling-apis"
  - "asyncgetstacktrace-a-better-stack-trace-api-for-the-jvm"
  - "do-you-trust-profilers-i-once-did-too"
  - "how-is-leyden-improving-java-performance-part-3-of-3"
enlighterjs: true
frozen: false
---

In my last article, [Using Async-Profiler and Jattach Programmatically with AP-Loader](https://foojay.io/today/using-async-profiler-and-jattach-programmatically-with-ap-loader/), I hinted that I'm currently working on a test library for writing better profiling API tests.

The library is still work-in-progress, but it already allows you to write profiling API tests in plain Java:

```java
private int innerASGCT2() {
    new Tracer().runASGCT().assertTrue(
        Frame.hasMethod(0, "innerASGCT2", "()I"), 
        Frame.hasMethod(1, "testRunASGCT2"));
    return 0;
}

@Test
public void testRunASGCT2() {
    innerASGCT2();
}
```


This test case checks that calling AsyncGetCallTrace gives the correct result in this specific example. The test library allows you to write tests comparing the returns of multiple GetStackTrace, AsyncGetCallTrace, and AsyncGetStackTrace invocations in different modes and settings. The library can be found as trace-tester on GitHub; I aim to bring it into the OpenJDK later with my JEP.

Writing small test cases this way is great, but it would be even better if we could force specific methods to be compiled, interpreted, or inlined so that we can test different scenarios. The proposed AsyncGetStackTrace will return the compilation level directly for every frame, so it is necessary to check the correctness of the level too.

*Consider reading my* [*Validating Java Profiling API*s](https://foojay.io/today/validating-java-profiling-apis/) article to*get a different angle on profiling API testing.*

Introduction
------------

Before I start with discussing the ways you can force methods to be compiled, interpreted, or inlined, I'll have to clarify that:

1. The following only works with the HotSpot tired JIT compiler and not other JVM's like OpenJ9 (see issue [#11272](https://github.com/eclipse-openj9/openj9/issues/11272))
2. It should only be used for testing. I would refrain from using it anywhere near production, even if you know that specific methods should be compiled. Use a tool like JITWatch by Chris Newland to check whether the JVM doesn't make the correct decisions automatically: Ask your fellow JVM expert how to deal with this.
3. I'm not an expert in the APIs I'm showing you, nor in tiered compilation, so be aware that I might be missing something, but I'm happy for any suggestions and corrections.
4. There are four different compilation levels, but I'm subsuming all C1 variants under the C1 label because some of my used techniques only work on the C1/C2/inlined level. You can read more on tiered compilation in articles like [Tiered Compilation in JVM](https://www.baeldung.com/jvm-tiered-compilation) on Baeldung.

Now that I finished the obligatory disclaimer: What are the stages in the life of a method with a tiered JIT?  
![](https://mostlynerdless.de/wp-content/uploads/2023/05/tiered_states-3-2000x1415.png)

The first time the JVM executes a method, the method's byte code is interpreted without compilation. This allows the JVM to gather information on the method, as C1 and C2 are profile guided.

The method is then compiled when the JVM deems this to be beneficial, usually after the method has been executed a few times. The next call of the method will then use the compiled version. The method is initially compiled with different levels of the C1 compiler before finally being C2 compiled, which takes the longest but produces the best native instructions.

The JVM might decide at any point to use the interpreted version of a method by deoptimizing it. The compiled versions are kept, depending on the compiler and the reasons for the deoptimization.

Every compiler can decide to inline called methods of a currently compiled method. A compiler uses the initial byte code for this purpose.

What we want and what we get
----------------------------

The ideal would be to tell the JVM to just use a method in its compiled version, e.g.:
![](https://mostlynerdless.de/wp-content/uploads/2023/05/tiered_states3-2000x474.png)

But this is not possible, as the JVM does not have any information it needs for compilation before the first execution of a method. We, therefore, have first to execute the method (or the benchmark) and then set the compilation level:
![](https://mostlynerdless.de/wp-content/uploads/2023/05/tiered_states2-2000x470.png)

How do we get it?
-----------------

We can split the task of forcing a method to be compiled (or inlined, for that matter) into two parts:

1. Force all methods into their respective state (→ WhiteBox API) after the initial execution.
2. Force the JIT to never compile a method with a different compiler (→ Compiler Control)

The following is the modified state diagram when forcing a method to be C1 compiled:
![](https://mostlynerdless.de/wp-content/uploads/2023/05/tiered_states4-2000x1415.png)

In the following, I'll discuss how to use both the WhiteBox API and Compiler Control to facilitate the wanted behavior.

WhiteBox API
------------

Many JVM tests are written in the JTreg framework, allowing developers to write these tests in Java. But these tests often require specific functionality not regularly available to Java developers. This functionality is exported in the WhiteBox API:
> One of the not so well-known tools of the HotSpot VM is its WhiteBox testing API. Introduced in Java 7 it has been significantly improved and extended in Java 8 and 9. It can be used to query or change HotSpot internals which are not otherwise exposed to Java-land. While its features make it an indispensable tool for writing good HotSpot regression tests, it can also be used for experiments or for the mere fun of peeking into the VM. This entry will focus on the usage of the WhiteBox API in Java 8 and 9.
>
> The WhiteBox API is implemented as a Java class (called sun.hotspot.WhiteBox) which defines various entry points into the HotSpot VM. Most of the functionality is implemented natively, directly in the HotSpot VM. The API is implemented as a singleton which can be easily retrieved by calling the static method WhiteBox.getWhiteBox().
>
> Unfortunately, currently even a simple JavaDoc documentation of the API doesn't exist, so in order to make full use of its functionality, you'll have to peek right into WhiteBox.java.
> [The WhiteBox testing API](https://wiki.openjdk.org/display/HotSpot/The+WhiteBox+testing+API)

This API can be used outside of JTreg tests after enabling it by passing `-Xbootclasspath/a:wb.jar -XX:+UnlockDiagnosticVMOptions -XX:+WhiteBoxAPI` as JVM arguments. To use it, you have to build the WhiteBox JAR from scratch for your specific JVM by calling `make build-test-lib` (after you set up the build via the `configure` script).

But please be aware that using this API outside of JVM tests is relatively rare, and the documentation is still non-existent, so using it entails reading a lot of JDK sources and experimentation.

The build target did not work in JDK 21, and when I fixed it, the first question in the PR was by [Daniel Jelinski](https://github.com/djelinski), who asked:
> That's interesting. How did you find this? Is the result of this target used anywhere?  
>
> As far as I could tell, the `build-test-lib` target itself is not used anywhere. The classes that fail to compile here are used by tests without any problems - each test specifies the necessary imports individually. Should we remove this make target instead?
> [8307732: build-test-lib is broken #13885](https://github.com/openjdk/jdk/pull/13885#issuecomment-1540007948)

So it would be best if you certainly did not depend on it.

The WhiteBox API consists of the singleton class [jdk.test.whitebox.WhiteBox](https://github.com/openjdk/jdk/blob/3c9ec26370dfae5d1230b6b69ae26122fe42e51d/test/lib/jdk/test/whitebox/WhiteBox.java) which offers many methods: From GC related methods like `boolean isObjectInOldGen(Object o)` and `void fullGC()` to NMT-related methods like `long NMTMalloc(long size)` and JIT-related methods like `void deoptimizeAll()`.

You can even use it to force the compilation of a method and to set JVM flags, as shown in this example by [Jean-Philippe Bempel](https://jpbempel.github.io/2015/07/07/whitebox-api.html):

```
public class WhiteBoxTest {
    static WhiteBox wb = WhiteBox.getWhiteBox();

    private void m() {
        System.out.println("foo");
    }

    public static void main(String[] args) throws Exception {
        wb.setBooleanVMFlag("PrintCompilation", true);
        wb.setBooleanVMFlag("BackgroundCompilation", false);
        wb.enqueueMethodForCompilation(
          WhiteBoxTest.class.getDeclaredMethod("m", null), 4);
    }
}
```


This is from his blog post WhiteBox API, the only blog post I could find on this topic.

Back to our goal of forcing the compilation of a method. It is a good idea to reset the state of a method and deoptimize it to start from a blank slate:

```java
// obtain a method reference
Executable m = X.class.getDeclaredMethod("m", null);
// obtain a WhiteBox instance
WhiteBox wb = WhiteBox.getWhiteBox();
// deooptimize the method
wb.deoptimizeMethod(m);
// clear its state, found by experimentation to be neccessary
wb.clearMethodState(m);
```


We can then either leave the method uncompiled (for compilation level 0) or enqueue for compilation:

```java
// level 1 - 3: C1, level 4: C2
wb.enqueueMethodForCompilation(m, level);
```


But be aware that it takes some time to actually compile the method, so it's best to wait till it is compiled:

```java
while (wb.getMethodCompilationLevel(m) != level) {
    Thread.sleep(1);
}
```


We can then also force a method to be never inlined:

```java
wb.testSetDontInlineMethod(m, true);
wb.testSetForceInlineMethod(m, false);
```


Or inversely to be always inlined:

```java
wb.testSetDontInlineMethod(m, false);
wb.testSetForceInlineMethod(m, true);
```


I implemented this in the [WhiteBoxUtil](https://github.com/parttimenerd/trace_tester/blob/4b02b80a1935822f18c356f0b340f70ca7ec06b2/src/main/java/tester/util/WhiteBoxUtil.java) class in my trace-tester library. This allows us to force all methods in their respective states. But the JVM can still decide to optimize further or inline a method, even when specifying the contrary. So we have to force the JVM using the second the Compiler Control specifications.

Compiler Control
----------------

This control mechanism has been introduced in Java 9 with [JEP 165](https://openjdk.org/jeps/165) by Nils Eliasson:
>
> Summary
> -------
>
> This JEP proposes an improved way to control the JVM compilers. It enables runtime manageable, method dependent compiler flags. (Immutable for the duration of a compilation.)
>
> Goals
> -----
>
> * Fine-grained and method-context dependent control of the JVM compilers (C1 and C2)
> * The ability to change the JVM compiler control options in run time
> * No performance degradation
>
> Motivation
> ----------
>
> Method-context dependent control of the compilation process is a powerful tool for writing small contained JVM compiler tests that can be run without restarting the entire JVM. It is also very useful for creating workarounds for bugs in the JVM compilers. A good encapsulation of the compiler options is also good hygiene.
> [JEP 165](https://openjdk.org/jeps/165)

This mechanism is properly standardized for the OpenJDK, unlike the WhiteBox APi. The compiler control allows to specify compilation settings by defining them in a JSON file and applying them:

* Using `jcmd` (see JEP): `jcmd <pid> Compiler.add_directives <file>`
* Passing it via JVM arguments: `-XX:+UnlockDiagnosticVMOptions -XX:CompilerDirectivesFile=<file>`
* Using the WhiteBox API: `int addCompilerDirective(String compDirect)`

The following directives specify as an example that the method m should not be C2 compiled and not be inlined:

```java
[
  {
    // can also contain patterns
    "match": ["X::m()"],
    // "-" prefixes not inlined, "+" inlined methods
    "inline": ["-X::m()"],
    "C1": {},
    "C2": {
      "Exclude": true
    }
  }
  // multiple directives supported
  // first directives have priority
]
```


This, in theory, allows the method to be deoptimized, but this did not happen during my testing. With forced compilation, one can assume that this method will almost be used in its compiled form.

I recommend this Compiler Control guide for a more in-depth guide with all options. An implementation of the control file generation with a fluent API can be found in the trace-tester project in the [CompilerDirectives](https://github.com/parttimenerd/trace_tester/blob/4b02b80a1935822f18c356f0b340f70ca7ec06b2/src/main/java/tester/util/CompilerDirectives.java) class. Feel free to adapt this for your own projects.

Conclusion
----------

I've shown you in this article how to control the JIT to specify the inlining and compilation of methods using two lesser-known JVM APIs.

This allows us to write reproducible profiling APIs and makes it easier to check how a profiling API reacts to different scenarios.

If you have any suggestions, feel free to reach out.

I look forward to preparing slides for my upcoming talks in Milan, Munich, Arnhem, and Karlsruhe.

Feel free to come to my talks; more information soon on [Twitter](https://twitter.com/parttimen3rd).

***This project is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com), making profiling easier for everyone. I built it to integrate async-profiler into more applications and libraries, like my upcoming profiler UI. This article first appeared on my personal blog [mostlynerdless.de](https://mostlynerdless.de/blog/2022/11/21/ap-loader-a-new-way-to-use-and-embed-async-profiler/).***
