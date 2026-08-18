---
title: "The Inner Workings of Safepoints"
slug: "the-inner-workings-of-safepoints"
date: "2023-08-02T10:27:27+00:00"
lastmod: "2023-08-02T10:27:28+00:00"
description: "Have you ever wondered how safepoints are implemented in the OpenJDK? Follow me down the rabbit hole into the inner workings of the JVM."
authors:
  - "johannes-bechberger"
image: "safepoint-2000x726-1.png"
categories:
  - "Java Core"
tags:
related_posts:
  - "ap-loader-a-new-way-to-use-and-embed-async-profiler"
  - "class-loader-hierarchies"
  - "foojay-podcast-14"
  - "foojay-podcast-92"
frozen: false
---

A Java thread in the JVM regularly checks whether it should do extra work besides the execution of the bytecode.

This work is done during so-called safepoints. There are two types of safepoints: local and global.

* At thread-local safepoints, also known as thread-local handshakes, only the current thread does some work and is therefore blocked from executing the application.
* At global safepoints, all Java threads are blocked and do some work. At these safepoints, the state of the thread (thread-local safepoints) or the JVM (global safepoints) is fixed. This allows the JVM to do activities like method deoptimizations or stop-the-world garbage collections, where the amount of concurrency should be limited.

But this article isn't about what (global) safepoints are. Fr this, please refer to Nitsan Wakart's and Seetha Wenner's articles on this topic and for thread-local safepoints, which are a relatively recent addition to JEP 312.

I'll cover in this article the actual implementation of safepoints in the OpenJDK and present a related bug that I found along the way.

## Implementing Safepoint Checks

Global safepoints are implemented using thread-local safepoints by stopping the threads at thread-local safepoints till all threads reach a barrier (source code), so we only have thread-local checks. Therefore I'll only cover thread-local safepoints here and call them "safepoints."

The simplest option for implementing safepoint checks would be to add code like

```cpp
if (thread->at_safepoint()) {
  SafepointMechanism::process();
}
```

to every location where a safepoint check should occur. The main problem is its performance. We either add lots of code or wrap it in a function and have a function call for every check. We can do better by exploiting the fact that the check often fails, so we can optimize for the fast path of "thread not at safepoint". The OpenJDK does this by exploiting the page protection mechanisms of modern CPUs ([source](https://github.com/openjdk/jdk/blob/020552355574ab428019e663c762a3496c4613c7/src/hotspot/share/runtime/safepointMechanism.cpp#L45)) in JIT compiled code:
![](https://mostlynerdless.de/wp-content/uploads/2023/07/safepoint-2000x726.png)

The JVM creates a *good* and a *bad* page/memory area for every thread before a thread executes any Java code ([source](https://github.com/openjdk/jdk/blob/020552355574ab428019e663c762a3496c4613c7/src/hotspot/share/runtime/safepointMechanism.cpp#L67)):

```cpp
char* bad_page  = polling_page;
char* good_page = polling_page + page_size;

os::protect_memory(bad_page,  page_size, os::MEM_PROT_NONE);
os::protect_memory(good_page, page_size, os::MEM_PROT_READ);
//...
_poll_page_armed_value    = 
  reinterpret_cast<uintptr_t>(bad_page);
_poll_page_disarmed_value = 
  reinterpret_cast<uintptr_t>(good_page);
```

The *good page* can be accessed without issues, but accessing the protected *bad page* causes an error. `os::protect_memory` uses the [mprotect](https://man7.org/linux/man-pages/man2/mprotect.2.html) method under the hood:

```
mprotect() changes the access protections for the calling
process's memory pages [...].

If the calling process tries to access memory in a manner that
violates the protections, then the kernel generates a SIGSEGV
signal for the process.

prot is a combination of the following access flags: PROT_NONE or
a bitwise-or of the other values in the following list:

  PROT_NONE     The memory cannot be accessed at all.
  PROT_READ     The memory can be read.
  PROT_WRITE    The memory can be modified.
[...]
```

Now every thread has a field `_polling_page` which points to either the *good page* (safepoint check fails) or the *bad page* (safepoint check succeeds). The segfault handler of JVM then calls the safepoint handler code. Handling segfaults is quite expensive, but this is only used on the slow path; the fast path consists only of reading from the address that `_polling_page` points to.

In addition to simple safepoints, which trigger indiscriminate of the current program state, Erik Österlund added functionality to parametrize safepoints with [JEP 376](https://openjdk.org/jeps/376): The safepoint can be configured to cause a successful safepoint only if the current frame is older than the specified frame, based on the frame pointer. The frame pointer of the specified frame is called a watermark.

Keep in mind that stacks grow from higher to lower addresses. But how is this implemented? It is implemented by adding a `_polling_word` field next to the `_poll_page` field to every thread. This polling word specifies the watermark and is checked in the safepoint handler. The configured safepoints are used for incremental stack walking.

The cool thing is that ([source](https://github.com/openjdk/jdk/blob/020552355574ab428019e663c762a3496c4613c7/src/hotspot/share/runtime/safepointMechanism.cpp#L47)) that when enabling the regular safepoint, one sets the watermark to `1` and for disarming it to `~1` (`1111...10`), so the `fp > watermark` is always true when the safepoint is enabled (`fp > 1` is always true) and false when disabled (`fp > 111...10` is always false). Therefore, we can use the same checks for both kinds of safepoints.

More on watermarks and how they can be used to reduce the latency of garbage collectors can be found in the video by Erik:

{{< youtube zsrSUs65xZA >}}

## Bug with Interpreted Aarch64 Methods

The OpenJDK uses multiple compilation tiers; methods can be interpreted or compiled; see Mastering the Art of Controlling the JIT: Unlocking Reproducible Profiler Tests for more information. A common misconception is that "interpreted" means that the method is evaluated by a kind of interpreter loop that has the basic structure:

```cpp
for (int i = 0; i < byteCode.length; i++) {
  switch (byteCode[i].op) {
    case OP_1:
    ...
  }
}
```

The bytecode is actually compiled using a straightforward *TemplateInterpreter*, which maps every bytecode instruction to a set of assembler instructions. The compilation is fast because there is no optimization, and the evaluation is faster than a traditional interpreter.

The TemplateInterpreter adds safepoint checks whenever required, like method returns. All `return` instructions are mapped to assembler instructions by the TemplateTable::_return(TosState state) method. On x86, it looks like ([source](https://github.com/openjdk/jdk/blob/5d193193a3a4c519e7b3d77b27e6b2bf1b11c7f9/src/hotspot/cpu/x86/templateTable_x86.cpp#L2562)):

```cpp
void TemplateTable::_return(TosState state) {
  // ...
  if (_desc->bytecode() == Bytecodes::_return_register_finalizer){
     // ... // finalizers
  }

  if (_desc->bytecode() != Bytecodes::_return_register_finalizer){
    Label no_safepoint;
    NOT_PRODUCT(__ block_comment("Thread-local Safepoint poll"));
    // ...
    __ testb(Address(r15_thread, 
               JavaThread::polling_word_offset()), 
             SafepointMechanism::poll_bit());
    // ...
    __ jcc(Assembler::zero, no_safepoint);
    __ push(state);
    __ push_cont_fastpath();
    __ call_VM(noreg, CAST_FROM_FN_PTR(address,
                        InterpreterRuntime::at_safepoint));
    __ pop_cont_fastpath();
    __ pop(state);
    __ bind(no_safepoint);
  }
  // ...
  __ remove_activation(state, rbcp);

  __ jmp(rbcp);
}
```

This adds the safepoint check using the simple method without page faults (for some reason, I don't know why), ensuring that a safepoint check is done at the return of every method.

We can therefore expect that when a safepoint is triggered in the `interpreted_method` in

```cpp
interpreted_method();
compiled_method();
```

that the safepoint is handled at least at the end of the method; in our example, the method is too small to have any other safepoints. Yet on my M1 MacBook, the safepoint is only handled in the `compiled_method`. I found this while trying to fix a bug in safepoint-dependent serviceability code. The cause of the problem is that the `TemplateTable::_return(TosState state)` is missing the safepoint check generation on aarch64 ([source](https://github.com/openjdk/jdk/blob/5d193193a3a4c519e7b3d77b27e6b2bf1b11c7f9/src/hotspot/cpu/aarch64/templateTable_aarch64.cpp#L2174C27-L2174C27)):

```
void TemplateTable::_return(TosState state)
{
  // ...
  if (_desc->bytecode() == Bytecodes::_return_register_finalizer){
    // ... // finalizers
  }

  // Issue a StoreStore barrier after all stores but before return
  // from any constructor for any class with a final field. 
  // We don't know if this is a finalizer, so we always do so.
  if (_desc->bytecode() == Bytecodes::_return)
    __ membar(MacroAssembler::StoreStore);

  // ...
  __ remove_activation(state);
  __ ret(lr);
}
```

The same issue is prevalent in the OpenJDK's riscv and arm ports. The real-world implications of this bug are minor, as the interpreted methods without any inner safepoint checks (in loops, calls to compiled methods, ...) seldom run long enough to matter.

I'm neither an expert on the TemplateInterpreter nor on the different architectures. Maybe there are valid reasons to omit this safepoint check on ARM. But if there are not, then it should be fixed; I propose adding something like the following directly before `if (_desc->bytecode() == Bytecodes::_return)` for aarch64 ([source](https://github.com/openjdk/jdk/commit/eea42bc29a131340dbf210d7dd151ffd32a64ec9)):

```cpp
  if (_desc->bytecode() != Bytecodes::_return_register_finalizer){
    Label slow_path;
    Label fast_path;
    __ safepoint_poll(slow_path, true /* at_return */,
         false /* acquire */, false /* in_nmethod */);
    __ br(Assembler::AL, fast_path);
    __ bind(slow_path);
    __ super_call_VM_leaf(CAST_FROM_FN_PTR(address, 
         InterpreterRuntime::at_safepoint), rthread);
    __ bind(fast_path);
  }
```

I'm happy to hear the opinion of any experts on this topic, the related bug is [JBS-8313419](https://bugs.openjdk.org/browse/JDK-8313419).

## Conclusion

Understanding the implementation of safepoints can be helpful when working on the OpenJDK. This article has shown the inner workings, focusing on a bug in the TemplateInterpreter related to the safepoints checks.

Thank you for being with me on this journey down a rabbit hole, and see you next week with an article on profiling APIs.

**This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com), making profiling easier for everyone.* It first appeared on [my personal blog](https://mostlynerdless.de). *Thanks to Richard Reingruber, Matthias Baesken, Jaroslav Bachorik, Lutz Schmitz, and Aleksey Shipilëv for their invaluable input*.*
