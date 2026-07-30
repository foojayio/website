---
title: "AsyncGetStackTrace: A better Stack Trace API for the JVM"
slug: "asyncgetstacktrace-a-better-stack-trace-api-for-the-jvm"
date: "2023-01-26T09:16:06+00:00"
lastmod: "2023-01-26T09:16:07+00:00"
description: "An introduction into GetStackTrace, AsyncGetCallTrace, and the history and specification of AsyncGetStackTrace."
authors:
  - "johannes-bechberger"
image: "https://foojay.io/wp-content/uploads/2023/01/java-coffee-cup-on-fire-without-marks-asgst-asgct.png"
categories:
  - "JEPs"
  - "Performance"
  - "Tools"
tags:
related_posts:
enlighterjs: true
frozen: false
---

*This article is the basis for my upcoming talk at* [*FOSDEM 2023,*](https://fosdem.org/2023/schedule/event/asyncgetstacktrace_the_improved_version_of_asyncgetcalltrace_jep_435/)*"AsyncGetStackTrace: The Improved Version Of AsyncGetCallTrace (JEP 435),["](https://fosdem.org/2023/schedule/event/asyncgetstacktrace_the_improved_version_of_asyncgetcalltrace_jep_435/) and is based on JEP Candidate 435.*

Consider you want to write a profiler to profile Java applications. Why? Because you think that the existing ones ["\[...\] Are Fucking Terrible"](http://psy-lob-saw.blogspot.com/2016/02/why-most-sampling-java-profilers-are.html) or ["\[...\] Broken"](https://youtu.be/7IkHIqPeFjY?list=PLLLT4NxU7U1QYiqanOw48h0VUjlUvqCCv&t=919). *Or you want to start a [blog series](https://mostlynerdless.de/blog/tag/writing-a-profiler-from-scratch/)* on writing a profiler from scratch to learn their inner workings (hi there,**regular readers)*.*

One of the integral parts is to get the stack traces of your application.

Modern profilers are usually sampling profilers, which probe an application at regular intervals.

Probing is hard without a proper way to obtain traces.

The JVM offers us two different mechanisms:

GetStackTrace {#h2-0-getstacktrace}
-----------------------------------

You could use the official and well defined `GetStackTrace` JVMTI API, which OpenJ9 and every other JVM out there also Implement:
> 

<pre class="EnlighterJSRAW" data-enlighter-language="cpp" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">jvmtiError
GetStackTrace(jvmtiEnv* env,
            jthread thread,
            jint start_depth,
            jint max_frame_count,
            jvmtiFrameInfo* frame_buffer,
            jint* count_ptr)</pre>

>
> Get information about the stack of a thread. If `max_frame_count` is less than the depth of the stack, the `max_frame_count` topmost frames are returned, otherwise the entire stack is returned. The topmost frames, those most recently invoked, are at the beginning of the returned buffer.
> [JVMTI Documentation](https://docs.oracle.com/en/java/javase/17/docs/specs/jvmti.html#GetStackTrace)

This API gives us enough information on every Java frame to write a small profiler:

<pre class="EnlighterJSRAW" data-enlighter-language="cpp" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">typedef struct {
    jmethodID method;
    jlocation location;
} jvmtiFrameInfo;</pre>

So what is the problem? This API is safe-point biased. This means that you can only obtain a stack trace using `GetStackTrace` only at certain points in time where the JVM state is well-defined, called safe points. This bias significantly reduces the accuracy of your profiler, as we can only observe a subset of locations in a program using these stack traces. More on this in blog posts like ["Java Safepoint and Async Profiling"](https://seethawenner.medium.com/java-safepoint-and-async-profiling-cdce0818cd29) by Seetha Wenner.

We, therefore, cannot in all earnest use this API, except if we're constrained to official APIs like [VisualVM](https://github.com/oracle/visualvm/blob/e9aa62460fef715760665d636c872ee76d586b56/visualvm/libs.profiler/lib.profiler/native/src-jdk15/Stacks.c#L176), which despite everything, uses it.

So what are our other options? Writing a custom perf agent, we could obtain the traces using perf on Linux, which polls the JVM for information on all observed methods. But this is Linux-specific and never took off, with [the most popular agent](https://github.com/jvm-profiling-tools/perf-map-agent) only supporting Java 8. There has been an issue for async-profiler since 2017 in which Andrei Pangin concluded:
> The idea is to implement Java stack walking on our own without relying on AGCT. Since the agent runs in the context of JVM, it can access VM structures, especially those exported through VMStructs. It should be possible to replicate stack walking logic of the VM inside async-profiler, though it might be challenging. **The main risk is that differrent versions of JVM may have different stack layout, but VMStructs along with special handling of the known versions is likely to help.**
> [Implement stack walking without AsyncGetCallTrace #66](https://github.com/jvm-profiling-tools/async-profiler/issues/66)

He never implemented anything into his async-profiler.

AsyncGetCallTrace {#h2-1-asyncgetcalltrace}
-------------------------------------------

The only other option left is to use `AsyncGetCallTrace`, an API added on the 19th of November 2002 in the JVMTI draft and removed two months later. This API is the asynchronous, non-safepoint-biased ([kind-of](https://jpbempel.github.io/2022/06/22/debug-non-safepoints.html)) version of `GetStackTrace`, called from signal handlers at any point of time:
![](https://mostlynerdless.de/wp-content/uploads/2023/01/asgct_2-2000x1125.png)

<br />

<pre class="EnlighterJSRAW" data-enlighter-language="cpp" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">void AsyncGetCallTrace(ASGCT_CallTrace *trace, jint depth, 
                       void* ucontext)
// Arguments:
//
//   trace    - trace data structure to be filled by the VM.
//   depth    - depth of the call stack trace.
//   ucontext - ucontext_t of the LWP
//
// ASGCT_CallTrace:
//   typedef struct {
//       JNIEnv *env_id;
//       jint num_frames;
//       ASGCT_CallFrame *frames;
//   } ASGCT_CallTrace;
//
// Fields:
//   env_id     - ID of thread which executed this trace.
//   num_frames - number of frames in the trace.
//                (&lt; 0 indicates the frame is not walkable).
//   frames     - the ASGCT_CallFrames that make up this trace. 
//                Callee followed by callers.
//
//  ASGCT_CallFrame:
//    typedef struct {
//        jint lineno;
//        jmethodID method_id;
//    } ASGCT_CallFrame;</pre>

*Consider reading my [blog series on writing a profiler from scratch](https://mostlynerdless.de/blog/tag/writing-a-profiler-from-scratch/) if you want to learn more.*

The [honest-profiler](https://github.com/jvm-profiling-tools/honest-profiler/) was probably the first open-source profiler that used it, starting in early 2014. After this, many other profilers, commercial and open-source, followed, not because it is an ideal API, but because it was the only one available.

Albeit *available* is a strong word, as Sun removed the API from JVMTI, it now lives in a C++ source file without any exported header. The JVM exports the symbol `AsyncGetCallTrace`, because Sun probably used the API in their Sun Studio, which contained a profiler. To use it, one must use dlsym and hope that it is still there: It's an internal API that might disappear in the blink of an eye, although being rather unlikely. Other JVMs are not required to have this API, e.g., OpenJ9 only got this API in [2021](https://github.com/eclipse-openj9/openj9/issues/5654).

History of AsyncGetStackTrace {#h2-2-history-of-asyncgetstacktrace}
-------------------------------------------------------------------

So where do I come into this story? I started in the [SapMachine](https://sapmachine.io) team at [SAP](https://sap.com) at the beginning of last year after only [minor academic](https://pp.info.uni-karlsruhe.de/person.php?id=159) success. One of my first tasks was to help my colleague Gunter Haug fix a bug in the PPC64le support of async-profiler, resulting in my first contribution to this project.

We had discussions on AsyncGetCallTrace during all of this, as Gunter had talked with Volker Simonis a few years back about writing a better API, but never found the time to work on it. So when I came with fresh enthusiasm, I restarted these discussions in the middle of January.

I started working on a new API with the working title `AsyncGetCallTrace2`, later renamed to `AsyncGetStackTrace`, implementing a basic version with a modified async-profiler and getting valuable feedback from Gunter, Volker, and Andrei.

These discussions eventually led to the proposal of `AsyncGetStackTrace` that is currently out in the open as [JEP Candidate 435](https://openjdk.org/jeps/435). waiting for feedback from the JFR and supportability community (and the related teams at Oracle).

AsyncGetStackTrace {#h2-3-asyncgetstacktrace}
---------------------------------------------

The proposed API is essentially an extended, official, and well-tested version of `AsyncGetCallTrace`:
![](https://mostlynerdless.de/wp-content/uploads/2023/01/asgst-2000x1125.png)

* it has its own `profile.h` header, so using it is easy
* it returns much more information on individual frames, like compilation level (interpreted, C1, C2, ...) and info on inlining
* and can also be instructed to record information on all C/C++ frames on the stack ...
* ... for Java and (!) non-Java threads
* its implementation contains a StackWalker class which could be used for `AsyncGetCallTrace` and JFR in the future ...
* ... which would result in less technical debt and easier propagation of bug fixes, as today where the stack walking code of JFR and `AsyncGetCallTrace` overlap with copied code

*I'm using C/C++ frames as the term for all frames that are typically called native in other programming language communities because native frames are related to native methods, which are methods that are declared in Java but defined in C/C++ code.*

Now to the API: I will inadvertently use parts of the text of my JEP in the following, but I will not update this blog post in the future every time my JEP changes. I would really encourage you to read the [JEP Candidate 435](https://openjdk.org/jeps/435) yourself, after you read this one, it has a different angle than this blog post.

### Function Declaration {#h3-4-function-declaration}

The primary function definition is similar to AsyncGetCallTrace:

<pre class="EnlighterJSRAW" data-enlighter-language="cpp" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">void AsyncGetStackTrace(ASGST_CallTrace *trace, jint depth, 
                        void* ucontext, uint32_t options);</pre>

It stores the stack frames in the pre-allocated `trace`, up to the specified depth, obtain the start frame from the passed `ucontext`. The only real difference is here that we can configure the stack walking. Currently, the API supports two features which the caller can enable by setting the bits of the `options` argument:

<pre class="EnlighterJSRAW" data-enlighter-language="cpp" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">enum ASGST_Options {
  // include C/C++ and stub frames too
  ASGST_INCLUDE_C_FRAMES         = 1,
  // walk the stacks of C/C++, GC and deopt threads too
  ASGST_INCLUDE_NON_JAVA_THREADS = 2,
};</pre>

Both options make writing simple profilers which also walk C/C++ frames and threads far more straightforward. The first option allows us to see frames that we could not see before (even with the advanced processing of async-profiler): C/C++ frames between Java frames.

This is quite useful when you work with JNI code which in turn calls Java code. You can find an example for this in the `innerc` test case of my JEP draft code:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">  /* checkNativeChain() 
      -&gt; checkCMethod() 
      -&gt; checkJavaInner() 
      -&gt; checkNativeLeaf() */
  // calls checkCMethod() with in turn calls checkJavaInner()
  private static native boolean checkNativeChain(); 
  private static boolean checkJavaInner() { return checkNativeLeaf(); }
  private static native boolean checkNativeLeaf();</pre>

With the old API you would never observe the `checkCMethod` in a stack trace, even if it would take lots of time to execute. But we disabled the options to mimic the behavior (and number of obtained frames), of `AsyncGetCallTrace`.

### CallTrace {#h3-5-calltrace}

We defined the main trace data structure in the new API as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="cpp" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">typedef struct {
  jint num_frames;                // number of frames in this 
                                  //   trace, (&lt; 0 indicates the
                                  //   frame is not walkable).
  uint8_t kind;                   // kind of the trace
  ASGST_CallFrame *frames;        // frames that make up this trace. 
                                  //   Callee followed by callers.
  void* frame_info;               // more information on frames
} ASGST_CallTrace;</pre>

There are two new fields: The kind of trace and the `frame_info` field for additional information on every frame, which could later be added depending on the configuration, without changing the API.

There are five different kinds of traces:

* Java Trace: trace of a thread that is currently executing Java code (or C/C++ code transitively called from Java code). The only kind you would observe with the default configuration because only these traces contain Java frames
* C/C++ Trace: trace of a non-Java thread
* GC Trace: trace of a Java thread during a GC execution
* Deoptimization Trace: trace of Java thread that currently runs in a deoptimization handler ([deoptimizing JIT compiled code](https://www.baeldung.com/jvm-tiered-compilation))
* Unknown Trace: signals that we could not get a first valid frame from the passed `ucontext`

Specified in the following enum:

<pre class="EnlighterJSRAW" data-enlighter-language="cpp" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">enum ASGST_TRACE_KIND {
  ASGST_JAVA_TRACE     = 0,
  ASGST_CPP_TRACE      = 1,
  ASGST_GC_TRACE       = 2,
  ASGST_DEOPT_TRACE    = 3,
  ASGST_UNKNOWN_TRACE  = 4,
};</pre>

We encode the error code as negative numbers in the num_frames field because it keeps the data structures simple and `AsyncGetCallTrace` does it too. Every trace with `num_frames > 0` is valid.

### Frames {#h3-6-frames}

The most significant difference between the two APIs is in the representation of frames: Where `AsyncGetCallTrace` just stored the bytecode index and the method id, we capture much more.

But first, we have to distinguish between Java frames, related to Java and native methods, and non-Java frames, related to stub and C/C++ frames. We use a union called `ASGST_CallFrame` for this:

<pre class="EnlighterJSRAW" data-enlighter-language="cpp" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">typedef union {
  uint8_t type;     // to distinguish between JavaFrame and 
                    //   NonJavaFrame
  ASGST_JavaFrame java_frame;
  ASGST_NonJavaFrame non_java_frame;
} ASGST_CallFrame;</pre>

The type here is more fine-grained than just two options:

<pre class="EnlighterJSRAW" data-enlighter-language="cpp" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">enum ASGST_FrameTypeId {
  ASGST_FRAME_JAVA         = 1, // JIT compiled and interpreted
  ASGST_FRAME_JAVA_INLINED = 2, // inlined JIT compiled
  ASGST_FRAME_NATIVE       = 3, // native wrapper to call 
                                //   C methods from Java
  ASGST_FRAME_STUB         = 4, // VM generated stubs
  ASGST_FRAME_CPP          = 5  // C/C++/... frames
};</pre>

The first three types map to `ASGST_JavaFrame` and others to `ASGST_NonJavaFrame`, as hinted before.

We don't store too much information for non-Java frames not to increase the size of every frame.

We store the program counter, which the profiler can use to obtain the function name and possibly the location inside the function:

<pre class="EnlighterJSRAW" data-enlighter-language="cpp" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">typedef struct {
  uint8_t type;      // frame type
  void *pc;          // current program counter inside this frame
} ASGST_NonJavaFrame; // used for FRAME_STUB, FRAME_CPP</pre>

We store the compilation level, the bytecode index, and the method id for Java frames, encoding the information on inlining in the type:

<pre class="EnlighterJSRAW" data-enlighter-language="cpp" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">typedef struct {
  uint8_t type;            // frame type
  int8_t comp_level;       // compilation level, 
                           //   0 is interpreted, -1 is undefined,
                           //   &gt; 1 is JIT compiled
  uint16_t bci;            // 0 &lt; bci &lt; 65536
  jmethodID method_id;
} ASGST_JavaFrame;         // used for FRAME_JAVA, 
                           //   FRAME_JAVA_INLINED and FRAME_NATIVE</pre>

Although the API provides more information, the amount of space required per frame (e.g., 16 bytes on x86) is the same as for the existing `AsyncGetCallTrace` API.

### Testing {#h3-7-testing}

`AsyncGetCallTrace` has just *one* [test case](https://github.com/openjdk/jdk/blob/c8319ed0cc5d5663c9dc8d28563bb60efff08c1e/test/hotspot/jtreg/serviceability/AsyncGetCallTrace/libAsyncGetCallTraceTest.cpp) at the time of writing, which merely checks one single frame. This is a pity for such a widely used API. The JEP candidate suggests that the implementation should have many more than that. Walking a stack asynchronously might trigger segmentation faults in the profiled JVM. The possibility of such can be reduced by extensive testing, calling `AsyncGetStackTrace` millions of times per second on benchmarks for hours and calling it with randomly modified `ucontext`s.

The code of the draft implementation contains several of these to ensure that calling the API is safe enough. It will never be entirely safe, as asynchronously walking stacks in a signal handler of a thread while all the other threads are still running is inherently risky. The aim is to reduce the risk to a level where the possibility of anything happening in real-world settings is minuscule.

Conclusion {#h2-8-conclusion}
-----------------------------

Working on this JEP, with the help of my team and Jaroslav Bachorik, almost exactly a year now, gave me a glimpse into the inner workings of the OpenJDK.

It was great to talk with so many different people from different companies.

I hope to continue this in the future and someday land this JEP in the OpenJDK, gifting the Java ecosystem a much-needed official profiling API. Achieving this will probably take months, if not years, but we'll see.

Thanks for reading this article. If you're interested in a presentation version, come to the [Friends of OpenJDK devroom](https://fosdem.org/2023/schedule/track/friends_of_openjdk/) at FOSDEM 2023, where I give a talk on Sunday the 5th of February at 3:20 pm or drop me a message if you're there.

Share the word on [AsyncGetStackTrace](https://openjdk.org/jeps/435) and comment with any suggestions or questions that you might have.

*This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com), making profiling easier for everyone. I published it first on [my personal blog](https://mostlynerdless.de).*
