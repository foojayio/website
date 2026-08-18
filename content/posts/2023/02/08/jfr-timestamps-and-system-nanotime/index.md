---
title: "JFR Timestamps and System.nanoTime Foojay.io"
slug: "jfr-timestamps-and-system-nanotime"
date: "2023-02-08T15:33:34+00:00"
lastmod: "2023-02-08T15:33:35+00:00"
description: "Did you ever wonder whether JFR timestamps use the same time source as System.nanoTime? Come down the rabbit hole with me!"
authors:
  - "johannes-bechberger"
image: "jfr.png"
categories:
  - "JDK Flight Recorder"
  - "Performance"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "a-closer-look-at-jfr-streaming"
  - "asyncgetstacktrace-a-better-stack-trace-api-for-the-jvm"
  - "a-glance-into-jfr-class-and-method-tagging"
frozen: false
---

Did you ever wonder whether JFR timestamps use the same time source as `System.nanoTime`?

This is important when you have miscellaneous logging besides JFR events; otherwise, you would not be able to match JFR events and your logging properly.

We assume here that you use `System.nanoTime` and not less-suited timing information from `System.currentTimeMillis`**()**.

The journey into this started with a question on the JDK Mission Control slack channel, which led me into a rabbit hole:
> "Could I have a question regarding JFR timestamps? (working with Linux) Is there any difference between JFR timestamp implementation and System#nanoTime (any optimization)?"
> Petr Bouda

This question essentially boils down to comparing both methods' OS time sources. We're only considering Unix systems in the following.

## Source of JFR timestamps

The JFR event time stamps are set in the JFR event constructor, which is defined in `jfrEvent.hpp` (and not in the Java code, as one might expect):

```cpp
  JfrEvent(EventStartTime timing=TIMED) : _start_time(0), _end_time(0),
                                          _untimed(timing == UNTIMED),
                                          _should_commit(false), _evaluated(false)
#ifdef ASSERT
  , _verifier()
#endif
  {
    if (!T::isInstant && !_untimed && is_enabled()) {
      set_starttime(JfrTicks::now());
    }
  }
```

Looking further reveals that `JFRTicks` calls [`FastUnorderedElapsedCounterSource`](https://github.com/openjdk/jdk/blob/05ea083b0563ddacf3e38dc329ba00dc4bac9b29/src/hotspot/share/utilities/ticks.cpp#L75) which uses two different time sources:

```cpp
FastUnorderedElapsedCounterSource::Type FastUnorderedElapsedCounterSource::now() {
#if defined(X86) && !defined(ZERO)
  static bool valid_rdtsc = Rdtsc::initialize();
  if (valid_rdtsc) {
    return Rdtsc::elapsed_counter();
  }
#endif
  return os::elapsed_counter();
}
```

The RDTSC instruction reads the time stamp counter on x86 processors:
> The time stamp counter (TSC) is a hardware counter found in all contemporary x86 processors. The counter is implemented as a 64-bit model-specific register (MSR) that is incremented at every clock cycle. The RDTSC ("read time stamp counter") register has been present since the original Pentium.
>
> **Already because of the access method, TSC provides a low-overhead and high-resolution way to obtain CPU timing information.** This traditional premise was violated when such factors as system sleep states, CPU "hotplugging'', "hibernation'', and CPU frequency scaling were introduced to the x86 lineage. This was however mainly a short abruption: in many new x86 CPUs the time stamp counter is again invariant with respect to the stability of the clock frequency. Care should be however taken in implementations that rely on this assumption.
> [NETBSD MANUAL](https://man.netbsd.org/NetBSD-8.1/x86/rdtsc.9)

This instruction allows the OS to implement a monotonic real-time clock.

On non-x86 systems `os::elapsed_counter` is used, which, surprise, calls `os::javaTimeNanos`:

```
jlong os::elapsed_counter() {
  return os::javaTimeNanos() - initial_time_count;
}
```

## Source of `System.nanoTime`

Now the remaining question is: Does `System.nanoTime` also call `os::javaTimeNanos`? The method is defined in the [jvm.cpp](https://github.com/openjdk/jdk/blob/05ea083b0563ddacf3e38dc329ba00dc4bac9b29/src/hotspot/share/prims/jvm.cpp#L243):

```
JVM_LEAF(jlong, JVM_NanoTime(JNIEnv *env, jclass ignored))
  return os::javaTimeNanos();
JVM_END
```

So `System.nanoTime` is just a tiny wrapper around `os::javaTimeNanos`. So this solves the original question on non-x86 CPUs. But what about x86 CPUs?

First for Mac OS: [It boils down](https://github.com/openjdk/jdk/blob/05ea083b0563ddacf3e38dc329ba00dc4bac9b29/src/hotspot/os/bsd/os_bsd.cpp#L762) to calling [mach_absolute_time](https://developer.apple.com/documentation/kernel/1462446-mach_absolute_time):
> Returns current value of a clock that increments monotonically in tick units (starting at an arbitrary point), this clock does not increment while the system is asleep.
> [ApplE DEVELOPER DOCUMENTATION](https://developer.apple.com/documentation/kernel/1462446-mach_absolute_time)

Information on the implementation of this method is scarce, but [source code from 2007](https://opensource.apple.com/source/xnu/xnu-4570.1.46/libsyscall/wrappers/mach_absolute_time.s.auto.html) suggests that `mach_absolute_time` is RDTSC based. So there is (probably) no difference between JFR timestamps and `System.nanoTime` on Mac OS, regardless of the CPU architecture.

Now on Linux: Here, the used `os::javaTimeNanos` [is implemented](https://github.com/openjdk/jdk/blob/05ea083b0563ddacf3e38dc329ba00dc4bac9b29/src/hotspot/os/posix/os_posix.cpp#L1408) using `clock_gettime(CLOCK_MONOTONIC, ...)`:
> **CLOCK_MONOTONIC** Clock that cannot be set and represents monotonic time since some unspecified starting point.
> [Linux MAN PAGE](https://linux.die.net/man/3/clock_gettime)

I tried to find something in the Linux Kernel sources, but they are slightly too complicated to find the solution quickly, so I had to look elsewhere. Someone asked a question on `clock_gettime` on [StackOverflow](https://stackoverflow.com/questions/7935518/is-clock-gettime-adequate-for-submicrosecond-timing). The answers essentially answer our question too: `clock_gettime(CLOCK_MONOTONIC, ...)` seems to use RDTSC.

## Conclusion

JFR timestamps and `System.nanoTime` seem to use the same time source on all Unix systems on all platforms, as far as I understand it.

You can stop the JVM from using RDTSC by using the `-XX:+UnlockExperimentalVMOptions -XX:-UseFastUnorderedTimeStamps` JVM flags (thanks to Richard Startin for pointing this out). You can read Markus Grönlunds Mail on [Timing Differences Between JFR and GC Logs](https://mail.openjdk.org/pipermail/hotspot-gc-dev/2020-August/030581.html) for another take on JFR timestamps (or skip ahead):
> JFR performance as it relates to event generation, which is also functional for JFR, reduce to a large extent to how quickly a timestamp can be acquired. Since everything in JFR is an event, and an event will have at least one timestamp, and two timestamps for events that represent durations, the event generation leans heavily on the clock source. Clock access latencies is therefore of central importance for JFR, maybe even more so than correctness. And historically, operating systems have varied quite a bit when it comes to access latency and resolution for the performance timers they expose.
>
> What you see in your example is that os::elapsed_counter() (which on Windows maps to QueryPerformanceCounter() with a JVM relative epoch offset) and the rdtsc() counter are disjoint epochs, and they are treated as such in Hotspot. Therefore, attempting to compare the raw counter values is not semantically valid.
>
> Relying on and using rdtsc() come with disclaimers and problems and is generally not recommended. Apart from the historical and performance related aspects already detailed, here is a short description of how it is treated in JFR:
>
> JFR will only attempt to use this source if it has the InvariantTSC property, with timestamp values only treated relative to some other, more stable, clock source. Each "chunk" (file) in JFR reifies a relative epoch, with the chunk start time anchored to a stable timestamp (on Windows this is UTC nanoseconds). rdtsc() timestamps for events generated during that epoch are only treated relative to this start time during post-processing, which gives very high resolution to JFR events. As JFR runs, new "chunks", and therefore new time epochs, are constructed, continuously, each anchored anew to a stable timestamp.
>
> The nature of rdtsc() querying different cores / sockets with no guarantee of them having been synchronized is of course a problem using this mechanism. However, over the years, possible skews have proven not as problematic as one might think in JFR. In general, the relative relations between the recorded JFR events give enough information to understand a situation and to solve a problem. Of course, there are exceptions, for example, when analyzing low-level aspects expecting high accuracy, usually involving some correlation to some other non-JFR related component. For these situations, an alternative is to turn off rdtsc() usages in JFR using the flags: -XX:+UnlockExperimentalVMOptions -XX:-UseFastUnorderedTimeStamps. JFR will now use os::elapsed_counter() as the time source. This comes with higher overhead, but if this overhead is not deemed problematic in an environment, then this is of course a better solution.
>
> As other have already pointed out, there have been evolution in recent years in how operating systems provide performance counter information to user mode. It might very well be that now the access latencies are within acceptable overhead, combined with high timer resolution. If that is the case, the rdtsc() usages should be phased out due to its inherent problems. This requires a systematic investigation and some policy on how to handle older HW/SW combinations - if there needs to be a fallback to continue to use rdtsc(), it follows it is not feasible to phase it out completely.
> [Markus Grönlund](https://mail.openjdk.org/pipermail/hotspot-gc-dev/2020-August/030581.html)

## Difference between `System.currentTimeMillis` and `System.nanoTime`

This is not directly related to the original question, but nonetheless interesting. `System.currentTimeMillis` is implemented using `clock_gettime(CLOCK_REALTIME, ...)` on all CPU architectures:
> **CLOCK_REALTIME** System-wide realtime clock. Setting this clock requires appropriate privileges.
> [Linux MAN PAGE](https://linux.die.net/man/3/clock_gettime)

This clock is not guaranteed to be monotonic:
> <br />
>
> `CLOCK_REALTIME` represents the machine's best-guess as to the current wall-clock, time-of-day time. \[...\] this means that `CLOCK_REALTIME` can jump forwards and backwards as the system time-of-day clock is changed, including by NTP.
>
> `CLOCK_MONOTONIC` represents the absolute elapsed wall-clock time since some arbitrary, fixed point in the past. It isn't affected by changes in the system time-of-day clock.
> [Ciro Santilli on STACKOVERFLOW](https://stackoverflow.com/a/3527632/19040822)

So does it make a difference? Probably only slightly, especially if you're running shorter profiling runs. For longer runs, consider using `System.nanoTime`.

I hope you enjoyed coming down this rabbit hole with me and learned something about JFR internals along the way.

**This blog post is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com), making profiling easier for everyone.**
