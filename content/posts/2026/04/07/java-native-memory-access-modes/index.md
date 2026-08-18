---
title: "Thread Safe Native Memory in Java"
slug: "java-native-memory-access-modes"
date: "2026-04-07T10:00:43+00:00"
lastmod: "2026-04-16T07:28:18+00:00"
description: "Thread-safe native memory access covering Plain, Opaque, Acquire/Release, and Volatile with JCStress tests to prove each guarantee"
authors:
  - "david-vlijmincx"
image: "0407.png"
categories:
  - "Java"
  - "Performance"
tags:
related_posts:
  - "unsafe-is-finally-going-away-embracing-safer-memory-access-with-jep-471"
  - "boxlang-v1-13-0-compatibility-concurrency-and-formatter-maturity"
  - "debug-without-breakpoints"
  - "fuchs-2024-fepcos-j-multithreaded-server"
enlighterjs: true
frozen: false
---

## What is Memory Order and Why Does It Matter for Native Memory?

The Foreign Function and Memory (FFM) API is Java's way of interacting with native code and memory. In the previous post, you learned how to do so using Java's built-in `Arena` types. The Arena provides temporal safety and bounds checks, but what about thread safety? MemorySegments created by `.ofShared()`, `.auto()`, and `.global()` can be used by multiple threads at the same time. Using a VarHandle with just get/set can backfire if you don't use something like locking. The downside is that locks are slow and heavy. So let us take a look at a more granular, hardware-aware approach: using VarHandle access modes.

### Why do you need all of this?

When you write concurrent code, you rely on the hardware to keep things in sync. Different CPU architectures handle memory ordering differently. On x86, the memory model is relatively strong. Reads and writes are mostly kept in order, meaning you can often get away with loose synchronization. ARM, however, has a weak memory model. The CPU is free to reorder reads and writes aggressively to optimize performance. If you write code assuming x86's strict ordering and run it on an ARM processor (like Apple Silicon or AWS Graviton), your application will break in unpredictable ways. VarHandle has methods that help with these situations to make sure your code works everywhere.

To see exactly how these mechanics work, we will start with the least restrictive access mode and build our way up to a full memory fence. But before we do that, I want to show you how to actually test this.

## Testing it using JCStress

[Java Concurrency Stress](https://github.com/openjdk/jcstress) is an experimental harness that helps you test the correctness of your concurrent code. It does this by running your test concurrently, accessing the same shared state. During execution, it collects the results of the observed state. The goal is to see how your code got rearranged and/or optimized and how it affected the state. One of the ways it does this is by running each thread using a different compilation mode like: interpreter, C1, or C2. JCStress tests each combination of compilation modes (interpreter, C1, C2) across the actors. With two actors, that's nine combinations per run.

Creating these tests requires a bit of a different mindset. Normally, you want two threads to play nicely. Inside the JCStress test you want them to clash as often as possible to observe the possible states your code could end up in. This gets kind of confusing, so let's use this example and let's say you have two threads running the following code:

```java
synchronized (lock) {
    // What you want to test
}
```

If you used this with JCStress the threads would basically run synchronized one after the other. Of course, it'll work, but it doesn't prove anything. So in the examples to come, keep in mind that we want the threads interleaving with each other and just hammer the state to see what happens. Just as it would in the real world. Another tip for when using JCStress is to not test too much inside a single test. You get a big state with lots of possibilities. To keep the tests fast and snappy, focus the test to tackle one synchronization/thread interleaving problem.

So what does the output look like? Like this:

```
  RESULT      SAMPLES     FREQ       EXPECT  DESCRIPTION
      -1  104,287,516   37.93%   Acceptable  Ready flag not seen yet.
       0        1,364   <0.01%  Interesting  Visibility failure: saw ready flag but missed the data.
      42  170,677,819   62.07%   Acceptable  Data seen correctly.
```

It shows the sampling results and how often they were encountered. The developer sets the expectations and descriptions, so they depend on the case.

## Plain Access (Get/Set)

Plain access is the simplest mode there is. No rules or any fence! This works like any other get/set/read/assignment that you are used to in Java like `var x = 1` for example. Get/Set work the same way for MemorySegments, it simply sets and gets a value. This is perfectly fine if you are working inside a single thread and don't share your state with other threads. In this mode the compilers, CPU, and cache are allowed to optimize your code and reorder the instructions. As long as the end result looks like it executed your code as you wrote it. This illusion is true as long as you don't create race conditions using multiple threads. So what does this look like? Let's break this illusion with two threads and JCStress. The next example, has a shared MemorySegment that is used to communicate a ready flag and some data. One thread set the data, and the other thread reads the result.

```java
@JCStressTest
@Outcome(id = "42", expect = Expect.ACCEPTABLE, desc = "Data seen correctly.")
@Outcome(id = "-1", expect = Expect.ACCEPTABLE, desc = "Ready flag not seen yet.")
@Outcome(id = "0", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Saw ready flag but missed the data.")
@State
public class NativeMemoryPlainAccess {

    private final MemorySegment segment;
    private static final VarHandle VH_INT = ValueLayout.JAVA_INT.varHandle();

    public NativeMemoryPlainAccess() {
        this.segment = Arena.ofAuto().allocate(8);
    }

    @Actor
    public void actor1() {
        VH_INT.set(segment, 0L, 42);
        VH_INT.set(segment, 4L, 1);
    }

    @Actor
    public void actor2(I_Result r) {
        int ready = (int) VH_INT.get(segment, 4L);
        if (ready == 1) {
            r.r1 = (int) VH_INT.get(segment, 0L);
        } else {
            r.r1 = -1;
        }
    }
}
```

These threads are passing a message, one thread sets some data, and the other thread reads it. There is no synchronization or fence inside this example, so everything is free to be reordered. This is going to introduce race conditions. This table shows all the different states observed while running the code:

```
  RESULT      SAMPLES     FREQ       EXPECT  DESCRIPTION
      -1  104,287,516   37.93%   Acceptable  Ready flag not seen yet.
       0        1,364   <0.01%  Interesting  Visibility failure: saw ready flag but missed the data.
      42  170,677,819   62.07%   Acceptable  Data seen correctly.
```

JCStress ran the code using different combinations of compilers (interpreter, C1, C2), and as you can see we got three different combinations. Some of the time the ready flag was set, and it got the value `42`, other times the flag wasn't set. Both of these are correct states. But `0` is an interesting state... It means that the flag was set, but the data wasn't there yet. The code got reordered! This is not a correct state to be in as 0 shouldn't be possible, right? To fix this issue, we need Acquire/Release, but let's look at `Opaque` first as it is the next mode in the hierarchy.

## Opaque Access

Opaque is the odd one out. Opaque doesn't insert memory fences and provides no ordering guarantees between different variables. What it does provide is: bitwise atomicity (no word tearing), coherence (all threads see writes to the same variable in the same order), and progress (writes will eventually become visible). It also prevents the compiler from eliminating access to that specific variable. This is handy for liveness checks, for example. Let's say you have two threads. thread_1 runs a while loop until it gets the signal to stop. Thread_0 is in control of this signal. Without Opaque, the compiler is allowed to turn that loop into a while(true), Thread_1 would never stop. JCStress is not really made for this specific scenario, so let's look at another example instead. In the example, Thread_1 will write `1` and `2` to the same place in the `MemorySegment`. Thread_2 does two reads to see the intermediate/end results. Again, the goal is to make the threads clash as often as possible.

```java
@JCStressTest
@Outcome(id = "1, 2", expect = Expect.ACCEPTABLE_INTERESTING, desc = "Observed intermediate state reliably.")
@Outcome(expect = Expect.ACCEPTABLE, desc = "Other observable states (0,0 / 2,2 / 0,2).")
@State
public class OpaqueNativeOpaqueAccess {

    private final MemorySegment segment;
    private static final VarHandle VH_INT = ValueLayout.JAVA_INT.varHandle();

    public OpaqueNativeOpaqueAccess() {
        this.segment = Arena.ofAuto().allocate(4);
    }

    @Actor
    public void actor1() {
        VH_INT.setOpaque(segment, 0L, 1);
        VH_INT.setOpaque(segment, 0L, 2);
    }

    @Actor
    public void actor2(II_Result r) {
        r.r1 = (int) VH_INT.getOpaque(segment, 0L);
        r.r2 = (int) VH_INT.getOpaque(segment, 0L);
    }
}
```

The results show that even though Opaque prevents extreme compiler optimizations, it does not guarantee immediate visibility across threads. The vast majority of the time, the second actor sees either the initial state (0, 0) or the final state (2, 2). However, we also observe intermediate states like (1, 2) or ordered reads like (0, 2). Because there are no ordering constraints or memory fences, the CPU and caches can still delay when the writes from actor1 become visible to actor2. The presence of (1, 2) confirms that the intermediate write of 1 is occasionally caught in transit.

```
  --- OPAQUE ACCESS ---
  RESULT      SAMPLES     FREQ       EXPECT  DESCRIPTION
    0, 0  114,615,126   41.45%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).
    0, 1       73,653    0.03%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).
    0, 2      577,951    0.21%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).
    1, 1      283,707    0.10%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).
    1, 2      114,550    0.04%  Interesting  Observed intermediate state reliably.
    2, 2  160,817,232   58.17%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).

    --- PLAIN ACCESS ---
    RESULT      SAMPLES     FREQ       EXPECT  DESCRIPTION
    0, 0  125,004,639   45.55%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).
    0, 1       38,798    0.01%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).
    0, 2      311,919    0.11%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).
    1, 1      362,391    0.13%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).
    1, 2       68,803    0.03%  Interesting  Observed intermediate state.
    2, 2  148,668,149   54.17%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).
```

When the C2 compiler steps in, it optimizes the code heavily. With Plain access, C2 often optimizes away the intermediate `write` entirely, assuming it's redundant since the final value is 2. This is why you see almost zero (1, 2) results in the Plain Access C2 table. Opaque access, however, explicitly forbids the compiler from removing that intermediate write. Consequently, the C2 table for Opaque still shows a noticeable number of (1, 2) results. The compiler was forced to keep both writes, and the hardware's lack of fencing allowed the intermediate state to be observed.

```
  --- OPAQUE ACCESS  C2 ---
  RESULT     SAMPLES     FREQ       EXPECT  DESCRIPTION
    0, 0   9,721,273   34.26%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).
    0, 1         677   <0.01%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).
    0, 2      32,666    0.12%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).
    1, 1      17,747    0.06%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).
    1, 2         561   <0.01%  Interesting  Observed intermediate state reliably.
    2, 2  18,600,087   65.56%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).

    --- PLAIN ACCESS C2 ---
   RESULT     SAMPLES     FREQ       EXPECT  DESCRIPTION
    0, 0  14,047,744   50.31%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).
    0, 1           2   <0.01%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).
    0, 2         106   <0.01%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).
    1, 1           1   <0.01%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).
    1, 2           4   <0.01%  Interesting  Observed intermediate state.
    2, 2  13,874,594   49.69%   Acceptable  Other observable states (0,0 / 2,2 / 0,2).
```

So in the end, Opaque is the combination of:

* Plain access: the get and set from the section above.
* Access atomicity: Reads and writes happen as a single, indivisible unit. No word tearing, even for 64-bit types like `long` and `double`.
* Coherence: writes to the same variable are observed in the same order for all observers.
* Progress: The writes will be eventually visible.

Opaque is useful in specific scenarios but too weak for most concurrent patterns. One example that would be a good fit is a variable that you want to broadcast to other reader(s). A counter that is owned by one thread and collected by other threads would be an example of this.

Let's go one level deeper and see what happens when you add causality to the mix.

## Acquire/Release

Acquire and Release offer a stricter mode than Opaque by including all of Opaque's guarantees and adding a happens-before relationship. This means it is stricter than Opaque, but still lighter than volatile. Release and Acquire are two separate methods:

* **setRelease()**: The compiler/CPU is not allowed to move a read or write instruction that happens before the Release to happen after it.
* **getAcquire()**: All reads and writes after this point are guaranteed to see at least the data that was visible at the point of the corresponding setRelease(). The compiler/CPU is not allowed to move an instruction that happens after the Acquire to before it.

Let's see how these rules play out in the real world. In the following code actor1 sets three values to a MemorySegment. The `setRelease` is used to set a flag that the data is ready to be read. Actor2 watches the flag for a change. When it reads a `1`, it fetches the data from the segment.

```java
@JCStressTest
@State
public class HappensBeforeAndAfter {

    private final MemorySegment segment;
    private static final VarHandle VH_INT = ValueLayout.JAVA_INT.varHandle();

    public HappensBeforeAndAfter() {
        this.segment = Arena.ofAuto().allocate(64);
    }

    @Actor
    public void actor1() {
        VH_INT.set(segment, 0L, 1); // Plain writes — made visible by the setRelease below
        VH_INT.set(segment, 0L, 2);
        VH_INT.set(segment, 0L, 3);
        VH_INT.setRelease(segment, 12L, 1);
    }

    @Actor
    public void actor2(I_Result r) {
        int ready = (int) VH_INT.getAcquire(segment, 12L);
        if (ready == 1) {
            r.r1 = (int) VH_INT.get(segment, 0L);
        } else {
            r.r1 = -1;
        }
    }
}
```

When running this code with JCStress, I got the following results. Both results are valid `-1` just means that the flag wasn't set yet and there was no attempt to read the data. And `3` means that the data from the last write was read.

```
  RESULT      SAMPLES     FREQ
      -1  167,565,777   62.57%
       3  100,243,162   37.43%
```

Doing the same with just a plain set/get will result in the compiler and CPU reordering the code as there is no happens-before anymore. The result of running it with plain access is like this:

```
  RESULT      SAMPLES     FREQ
      -1  174,727,852   64.82%
       0        3,906   <0.01%
       1           75   <0.01%
       2           94   <0.01%
       3   94,828,052   35.18%
```

This isn't pretty when we want to only read the data only when it is actually available. The values 0, 1, and 2 mean the ready flag appeared set before the data was actually written. This shows that Release/Acquire excels in cases like producer-consumer designs, message-passing designs.

## Volatile

This is the last and strictest mode, designed to enforce sequential consistency and a total order of operations across all threads. When you use volatile, it prevents aggressive instruction reorderings. Such as a newer read bypassing an older write. Or by ensuring that prior volatile stores are drained from the hardware's store buffer to the coherent cache before processing new loads. While it doesn't freeze the CPU to provide instant, wall-clock visibility, it guarantees that all cores will eventually agree on the exact same sequence of events. Because every read will observe the most recently written value within that agreed-upon memory order, volatile is the safest and most reliable mode for synchronizing state across multiple variables.

```java
@JCStressTest
@Outcome(id = "0, 0", expect = Expect.FORBIDDEN, desc = "Read visibility failure")
@Outcome(id = "1, 1", expect = Expect.ACCEPTABLE, desc = "Data seen correctly.")
@Outcome(id = "1, 0", expect = Expect.ACCEPTABLE_INTERESTING, desc = "One actor got shuffled")
@Outcome(id = "0, 1", expect = Expect.ACCEPTABLE_INTERESTING, desc = "One actor got shuffled")
@State
public class NativeMemoryFullFence {

    public static final GroupLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_INT.withName("x"),
            ValueLayout.JAVA_INT.withName("y")
    );
    private static final VarHandle VH_X = LAYOUT.varHandle(groupElement("x"));
    private static final VarHandle VH_Y = LAYOUT.varHandle(groupElement("y"));

    private final MemorySegment segment;

    public NativeMemoryFullFence() {
        this.segment = Arena.ofAuto().allocate(LAYOUT);
        VH_X.set(segment, 0L, 0);
        VH_Y.set(segment, 0L, 0);
    }

    @Actor
    public void actor1(II_Result r) {
        VH_X.setVolatile(segment, 0L, 1);           // Store X
         r.r1 = (int) VH_Y.getVolatile(segment, 0L); // Load Y
    }

    @Actor
    public void actor2(II_Result r) {
        VH_Y.setVolatile(segment, 0L, 1);           // Store Y
        r.r2 = (int) VH_X.getVolatile(segment, 0L); // Load X
    }

}
```

The two actors are reading and writing to two different places inside the memorySegment. By using volatile, the `write` is guaranteed to be fully visible to all threads before any subsequent operation in this thread proceeds. This is slower but guarantees all threads agree on the order of operations.

```
  RESULT      SAMPLES     FREQ       EXPECT  DESCRIPTION
    0, 0            0    0.00%    Forbidden  Read visibility failure
    0, 1  142,965,429   53.47%  Interesting  One actor got shuffled
    1, 0  123,397,941   46.15%  Interesting  One actor got shuffled
    1, 1      995,009    0.37%   Acceptable  Data seen correctly.
```

If a weaker model like Release/Acquire is used, the CPU does not wait for the prior write to be drained to the coherent cache before executing the next read to a different address. You fire the write action and continue directly with the next read. Because each CPU is its own environment with its own physical clock, they have no clue how time is progressing on other cores. This lack of a total order means you can end up with a cycle in your memory order, observing the forbidden 0, 0 state when synchronizing across two or more variables, as shown here:

```
  RESULT      SAMPLES     FREQ       EXPECT  DESCRIPTION
    0, 0      205,803    0.08%    Forbidden  Read visibility failure
    0, 1  123,619,023   47.36%  Interesting  One actor got shuffled
    1, 0  136,207,596   52.18%  Interesting  One actor got shuffled
    1, 1      977,157    0.37%   Acceptable  Data seen correctly.
```

Release/Acquire is fine when you have a single variable that you care about, but when you need to synchronize across two or more variables, it fails and you need the stronger volatile mode.

## TL;DR

Just use Get/Set and Volatile and have a peaceful life. If that's really not enough, and you *really* need this fine-grained control. Maybe consider still using get/set and volatile. If I really can't convince you, then the other modes are great for those special cases where volatile causes too much of a performance issue.

|   Access Mode   |                                      Guarantees                                      |                                                  Best Used For                                                   |
|-----------------|--------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------|
| Plain (Get/Set) | None. Freely reordered by compiler and CPU.                                          | Single-threaded memory access, or when thread safety is handled by external locks.                               |
| Opaque          | Bitwise atomic, no compiler elimination, no memory fences.                           | Liveness checks, counters, or flags where exact ordering doesn't matter.                                         |
| Acquire/Release | Happens-before ordering. Prevents specific reorderings around the access.            | Message passing, producer-consumer patterns, single-variable handoffs.                                           |
| Volatile        | Sequential consistency and total ordering. Stores are drained to the coherent cache. | Multi-variable state synchronization, critical shared state where eventually consistent total order is required. |

## Conclusion

Working with native memory across multiple threads forces you to confront how hardware actually executes your code. While the FFM API provides a direct bridge to native memory, it doesn't shield you from CPU reordering or cache visibility issues. Plain access is perfectly fine for single-threaded tasks, but once you share memory segments, you need to apply the right VarHandle access modes. Volatile is the safest default, providing strict ordering at the cost of performance. If profiling indicates volatile is a bottleneck, you can step down to Acquire/Release or Opaque, but you take on the responsibility of managing the memory order yourself. Always test concurrent memory access thoroughly, as architectural differences between x86 and ARM will easily expose any flaws in your assumptions.

## Bonus: Word Tearing

Word tearing occurs when a read or write operation on a piece of memory is not atomic. If you write a 64-bit value to unaligned memory, or on a 32-bit system, the CPU might execute it as two separate 32-bit operations. If another thread reads that memory in between those two operations, it will get half of the old value and half of the new value. Using `Opaque` would prevent this from happening. For demonstration purposes let's look at an example using unaligned memory access.

```java
@JCStressTest
@Outcome(id = "0, 0", expect = Expect.ACCEPTABLE)
@Outcome(id = "0, 9223372036854775806", expect = Expect.ACCEPTABLE)
@Outcome(id = "0, 9223372036854775807", expect = Expect.ACCEPTABLE)
@Outcome(id = "9223372036854775806, 9223372036854775806", expect = Expect.ACCEPTABLE)
@Outcome(id = "9223372036854775806, 9223372036854775807", expect = Expect.ACCEPTABLE)
@Outcome(id = "9223372036854775807, 9223372036854775807", expect = Expect.ACCEPTABLE)
@Outcome(expect = Expect.ACCEPTABLE_INTERESTING)
@State
public class WordTearingWithPlain {

    private final MemorySegment segment;
    private static final VarHandle VH_LONG = JAVA_LONG.withByteAlignment(1).varHandle();

    public WordTearingWithPlain() {
        this.segment = Arena.ofAuto().allocate(JAVA_LONG.byteSize() * 2,1);
    }

    @Actor
    public void actor1() {
        VH_LONG.set(segment, 4L, Long.MAX_VALUE - 1);
        VH_LONG.set(segment, 4L, Long.MAX_VALUE);
    }

    @Actor
    public void actor2(LL_Result r) {
        r.r1 = (long) VH_LONG.get(segment, 4L);
        r.r2 = (long) VH_LONG.get(segment, 4L);
    }

}
```

The results explicitly show word tearing in action. Value 4294967294 is not the initial `0` or the intended `Long.MAX_VALUE` or `Long.MAX_VALUE - 1`. Because the MemorySegment was accessed with an unaligned layout (1-byte alignment for an 8-byte long), the JVM and CPU could not write the 64-bit `long` in a single, atomic hardware instruction. Instead, it was split. Actor2 managed to read the memory exactly when only half of the new value had been written, resulting in a corrupted, blended value. This highlights why alignment and proper access modes are necessary when managing memory manually.

```
                                    RESULT      SAMPLES     FREQ       EXPECT
                                      0, 0   24,373,979    9.07%   Acceptable
                             0, 4294967294            2   <0.01%  Interesting
                    0, 9223372036854775806       38,613    0.01%   Acceptable
                    0, 9223372036854775807      726,094    0.27%   Acceptable
                    4294967294, 4294967294            2   <0.01%  Interesting
           4294967294, 9223372036854775806            1   <0.01%  Interesting
  9223372036854775806, 9223372036854775806       21,715   <0.01%   Acceptable
  9223372036854775806, 9223372036854775807      145,851    0.05%   Acceptable
  9223372036854775807, 9223372036854775807  243,455,002   90.58%   Acceptable
```
