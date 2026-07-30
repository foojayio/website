---
title: "DTrace Revisited: Advanced Debugging Techniques"
slug: "dtrace-revisited-advanced-debugging-techniques"
date: "2024-02-13T07:46:04+00:00"
lastmod: "2024-02-13T07:50:14+00:00"
description: "Explore the power of DTrace for system debugging and optimization: a comprehensive guide on its capabilities, performance, and applications."
canonical: "https://debugagent.com/dtrace-revisited-advanced-debugging-techniques"
authors:
  - "shai-almog"
image: "https://foojay.io/wp-content/uploads/2024/01/DALL·E-2024-01-23-12.51.14-A-stylized-digital-artwork-representing-the-concept-of-system-debugging-and-optimization-featuring-elements-like-a-magnifying-glass-over-computer-cod.jpg"
categories:
  - "Tutorials"
  - "Videos"
tags:
related_posts:
  - "cant-reproduce-a-bug"
  - "external-debugging-tools-1-dtrace-and-strace"
  - "is-it-time-to-go-back-to-the-monolith"
enlighterjs: true
frozen: false
---

* [DTrace Overview](#dtrace-overview)
* [Understanding DTrace's Capabilities](#understanding-dtraces-capabilities)
  * [System Monitoring and Analysis](#system-monitoring-and-analysis)
  * [Process and Performance Analysis](#process-and-performance-analysis)
  * [Customizability and Flexibility](#customizability-and-flexibility)
  * [Real-World Applications](#real-world-applications)
* [Performance and Compatibility of DTrace](#performance-and-compatibility-of-dtrace)
  * [**Cross-Platform Compatibility**](#cross-platform-compatibility)
  * [**Compatibility Challenges on MacOS**](#compatibility-challenges-on-macos)
* [Customizability and Flexibility of DTrace](#customizability-and-flexibility-of-dtrace)
  * [**Adaptability to Various Scenarios**](#adaptability-to-various-scenarios)
  * [**Examples of Customizable Probes**](#examples-of-customizable-probes)
* [Real-World Applications of DTrace](#real-world-applications-of-dtrace)
  * [Final Words](#final-words)

When we think of debugging, we think of breakpoints in IDEs, stepping over, inspecting variables, etc.

However, there are instances where stepping outside the conventional confines of an IDE becomes essential to track and resolve complex issues. This is where tools like DTrace come into play, offering a more nuanced and powerful approach to debugging than traditional methods.

This article delves into the intricacies of DTrace, an innovative tool that has reshaped the landscape of debugging and system analysis.

{{< youtube 3M0AhZnVoUk >}}

<br />

As a side note, if you like the content of this and the other posts in this series check out my [**Debugging book**](https://www.amazon.com/dp/1484290410/) that covers this subject. If you have friends that are learning to code I'd appreciate a reference to my [**Java Basics book**](https://www.amazon.com/Java-Basics-Practical-Introduction-Full-Stack-ebook/dp/B0CCPGZ8W1/). If you want to get back to Java after a while check out my [**Java 8 to 21 book**.](https://www.amazon.com/Java-21-Explore-cutting-edge-features/dp/9355513925/)

{#dtrace-overview}

DTrace Overview {#h2-0-dtrace-overview}
---------------------------------------

DTrace was first introduced by Sun Microsystems in 2004, DTrace quickly garnered attention for its groundbreaking approach to dynamic system tracing. Originally developed for Solaris, it has since been ported to various platforms, including MacOS, Windows, and Linux.

DTrace stands out as a dynamic tracing framework that enables deep inspection of live systems -- from operating systems to running applications. Its capacity to provide real-time insights into system and application behavior without significant performance degradation marks it as a revolutionary tool in the domain of system diagnostics and debugging.

{#understanding-dtraces-capabilities}

Understanding DTrace's Capabilities {#h2-1-understanding-dtrace-s-capabilities}
-------------------------------------------------------------------------------

DTrace, short for Dynamic Tracing, is a comprehensive toolkit for real-time system monitoring and debugging, offering an array of capabilities that span across different levels of system operation.

Its versatility lies in its ability to provide insights into both high-level system performance and detailed process-level activities.

{#system-monitoring-and-analysis}

### System Monitoring and Analysis {#h3-2-system-monitoring-and-analysis}

At its core, DTrace excels in monitoring various system-level operations. It can trace system calls, file system activities, and network operations. This enables developers and system administrators to observe the interactions between the operating system and the applications running on it.

For instance, DTrace can identify which files a process accesses, monitor network requests, and even trace system calls to provide a detailed view of what's happening within the system.

{#process-and-performance-analysis}

### Process and Performance Analysis {#h3-3-process-and-performance-analysis}

Beyond system-level monitoring, DTrace is particularly adept at dissecting individual processes. It can provide detailed information about process execution, including CPU and memory usage, helping to pinpoint performance bottlenecks or memory leaks.

This granular level of detail is invaluable for performance tuning and debugging complex software issues.

{#customizability-and-flexibility}

### Customizability and Flexibility {#h3-4-customizability-and-flexibility}

One of the most powerful aspects of DTrace is its customizability. With a scripting language based on C syntax, DTrace allows the creation of customized scripts to probe specific aspects of system behavior.

This flexibility means that it can be adapted to a wide range of debugging scenarios, making it a versatile tool in a developer's arsenal.

{#real-world-applications}

### Real-World Applications {#h3-5-real-world-applications}

In practical terms, DTrace can be used to diagnose elusive performance issues, track down resource leaks, or understand complex interactions between different system components.

For example, it can be used to determine the cause of a slow file operation, analyze the reasons behind a process crash, or understand the system impact of a new software deployment.

{#performance-and-compatibility-of-dtrace}

Performance and Compatibility of DTrace {#h2-6-performance-and-compatibility-of-dtrace}
---------------------------------------------------------------------------------------

A standout feature of DTrace is its ability to operate with remarkable efficiency. Despite its deep system integration, DTrace is designed to have minimal impact on overall system performance.

This efficiency makes it a feasible tool for use in live production environments, where maintaining system stability and performance is crucial. Its non-intrusive nature allows developers and system administrators to conduct thorough debugging and performance analysis without the worry of significantly slowing down or disrupting the normal operation of the system.

{#cross-platform-compatibility}

### **Cross-Platform Compatibility** {#h3-7-cross-platform-compatibility}

Originally developed for Solaris, DTrace has evolved into a cross-platform tool, with adaptations available for MacOS, Windows, and various Linux distributions. Each platform presents its own set of features and limitations. For instance, while DTrace is a native component in Solaris and MacOS, its implementation in Linux often requires a specialized build due to kernel support and licensing considerations.

{#compatibility-challenges-on-macos}

### **Compatibility Challenges on MacOS** {#h3-8-compatibility-challenges-on-macos}

On MacOS, DTrace's functionality intersects with System Integrity Protection (SIP), a security feature designed to prevent potentially harmful actions. To utilize DTrace effectively, users may need to disable SIP, which should be done with caution. This process involves booting into recovery mode and executing specific commands, a step that highlights the need for a careful approach when working with such powerful system-level tools.

We can disable SIP using the command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">csrutil disable</pre>

We can optionally use a more refined approach of enabling SIP without dtrace using the following command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">csrutil enable --without dtrace</pre>

Be extra careful when issuing these commands and when working on machines where dtrace is enabled. Back up your data properly!

{#customizability-and-flexibility-of-dtrace}

Customizability and Flexibility of DTrace {#h2-9-customizability-and-flexibility-of-dtrace}
-------------------------------------------------------------------------------------------

A key feature that sets DTrace apart in the realm of system monitoring tools is its highly customizable nature. DTrace employs a scripting language that bears similarity to C syntax, offering users the ability to craft detailed and specific diagnostic scripts.

This scripting capability allows for the creation of custom probes that can be fine-tuned to target particular aspects of system behavior, providing precise and relevant data.

{#adaptability-to-various-scenarios}

### **Adaptability to Various Scenarios** {#h3-10-adaptability-to-various-scenarios}

The flexibility of DTrace's scripting language means it can adapt to a multitude of debugging scenarios. Whether it's tracking down memory leaks, analyzing CPU usage, or monitoring I/O operations, DTrace can be configured to provide insights tailored to the specific needs of the task.

This adaptability makes it an invaluable tool for both developers and system administrators who require a dynamic approach to problem-solving.

{#examples-of-customizable-probes}

### **Examples of Customizable Probes** {#h3-11-examples-of-customizable-probes}

Users can define probes to monitor specific system events, track the behavior of certain processes, or gather data on system resource usage. This level of customization ensures that DTrace can be an effective tool in a variety of contexts, from routine maintenance to complex troubleshooting tasks. Following in a simple hello world dtrace probe:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">sudo dtrace -qn 'syscall::write:entry, syscall::sendto:entry /pid == $target/ { printf("(%d) %s %s", pid, probefunc, copyinstr(arg1)); }' -p 9999</pre>

The kernel is instrumented with hooks that match various callbacks. dtrace connects to these hooks and can perform interesting tasks when these hooks are triggered. They have a naming convention, specially: `provider:module:function:name`. In this case the provider is a system call in both cases. We have no module so we can leave that part blank between the colon (`:`) symbols. We grab a write operation and `sendto` entries. When an application will write or tries to send a packet, the following code event will trigger.

These things happen frequently which is why we restrict the process ID to the specific target with `pid == $target`. This means the code will only trigger for the PID passed to us in the command line. The rest of the code should be simple for anyone with basic C experience, it's a printf that would list the processes and the data passed.

{#real-world-applications-of-dtrace}

Real-World Applications of DTrace {#h2-12-real-world-applications-of-dtrace}
----------------------------------------------------------------------------

DTrace's diverse capabilities extend far beyond theoretical use, playing a pivotal role in resolving real-world system complexities. Its ability to provide deep insights into system operations makes it an indispensable tool in a variety of practical applications.

To get a sense of how dtrace can be used we can use the `man -k dtrace` command whose output on my mac is below:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">bitesize.d(1m)           - analyse disk I/O size by process. Uses DTrace
cpuwalk.d(1m)            - Measure which CPUs a process runs on. Uses DTrace
creatbyproc.d(1m)        - snoop creat()s by process name. Uses DTrace
dappprof(1m)             - profile user and lib function usage. Uses DTrace
dapptrace(1m)            - trace user and library function usage. Uses DTrace
dispqlen.d(1m)           - dispatcher queue length by CPU. Uses DTrace
dtrace(1)                - dynamic tracing compiler and tracing utility
dtruss(1m)               - process syscall details. Uses DTrace
errinfo(1m)              - print errno for syscall fails. Uses DTrace
execsnoop(1m)            - snoop new process execution. Uses DTrace
fddist(1m)               - file descriptor usage distributions. Uses DTrace
filebyproc.d(1m)         - snoop opens by process name. Uses DTrace
hotspot.d(1m)            - print disk event by location. Uses DTrace
iofile.d(1m)             - I/O wait time by file and process. Uses DTrace
iofileb.d(1m)            - I/O bytes by file and process. Uses DTrace
iopattern(1m)            - print disk I/O pattern. Uses DTrace
iopending(1m)            - plot number of pending disk events. Uses DTrace
iosnoop(1m)              - snoop I/O events as they occur. Uses DTrace
iotop(1m)                - display top disk I/O events by process. Uses DTrace
kill.d(1m)               - snoop process signals as they occur. Uses DTrace
lastwords(1m)            - print syscalls before exit. Uses DTrace
loads.d(1m)              - print load averages. Uses DTrace
newproc.d(1m)            - snoop new processes. Uses DTrace
opensnoop(1m)            - snoop file opens as they occur. Uses DTrace
pathopens.d(1m)          - full pathnames opened ok count. Uses DTrace
perldtrace(1)            - Perl support for DTrace
pidpersec.d(1m)          - print new PIDs per sec. Uses DTrace
plockstat(1)             - front-end to DTrace to print statistics about POSIX mutexes and read/write locks
priclass.d(1m)           - priority distribution by scheduling class. Uses DTrace
pridist.d(1m)            - process priority distribution. Uses DTrace
procsystime(1m)          - analyse system call times. Uses DTrace
rwbypid.d(1m)            - read/write calls by PID. Uses DTrace
rwbytype.d(1m)           - read/write bytes by vnode type. Uses DTrace
rwsnoop(1m)              - snoop read/write events. Uses DTrace
sampleproc(1m)           - sample processes on the CPUs. Uses DTrace
seeksize.d(1m)           - print disk event seek report. Uses DTrace
setuids.d(1m)            - snoop setuid calls as they occur. Uses DTrace
sigdist.d(1m)            - signal distribution by process. Uses DTrace
syscallbypid.d(1m)       - syscalls by process ID. Uses DTrace
syscallbyproc.d(1m)      - syscalls by process name. Uses DTrace
syscallbysysc.d(1m)      - syscalls by syscall. Uses DTrace
topsyscall(1m)           - top syscalls by syscall name. Uses DTrace
topsysproc(1m)           - top syscalls by process name. Uses DTrace
Tcl_CommandTraceInfo(3tcl), Tcl_TraceCommand(3tcl), Tcl_UntraceCommand(3tcl) - monitor renames and deletes of a command
bitesize.d(1m)           - analyse disk I/O size by process. Uses DTrace
cpuwalk.d(1m)            - Measure which CPUs a process runs on. Uses DTrace
creatbyproc.d(1m)        - snoop creat()s by process name. Uses DTrace
dappprof(1m)             - profile user and lib function usage. Uses DTrace
dapptrace(1m)            - trace user and library function usage. Uses DTrace
dispqlen.d(1m)           - dispatcher queue length by CPU. Uses DTrace
dtrace(1)                - dynamic tracing compiler and tracing utility
dtruss(1m)               - process syscall details. Uses DTrace
errinfo(1m)              - print errno for syscall fails. Uses DTrace
execsnoop(1m)            - snoop new process execution. Uses DTrace
fddist(1m)               - file descriptor usage distributions. Uses DTrace
filebyproc.d(1m)         - snoop opens by process name. Uses DTrace
hotspot.d(1m)            - print disk event by location. Uses DTrace
iofile.d(1m)             - I/O wait time by file and process. Uses DTrace
iofileb.d(1m)            - I/O bytes by file and process. Uses DTrace
iopattern(1m)            - print disk I/O pattern. Uses DTrace
iopending(1m)            - plot number of pending disk events. Uses DTrace
iosnoop(1m)              - snoop I/O events as they occur. Uses DTrace
iotop(1m)                - display top disk I/O events by process. Uses DTrace
kill.d(1m)               - snoop process signals as they occur. Uses DTrace
lastwords(1m)            - print syscalls before exit. Uses DTrace
loads.d(1m)              - print load averages. Uses DTrace
newproc.d(1m)            - snoop new processes. Uses DTrace
opensnoop(1m)            - snoop file opens as they occur. Uses DTrace
pathopens.d(1m)          - full pathnames opened ok count. Uses DTrace
perldtrace(1)            - Perl support for DTrace
pidpersec.d(1m)          - print new PIDs per sec. Uses DTrace
plockstat(1)             - front-end to DTrace to print statistics about POSIX mutexes and read/write locks
priclass.d(1m)           - priority distribution by scheduling class. Uses DTrace
pridist.d(1m)            - process priority distribution. Uses DTrace
procsystime(1m)          - analyse system call times. Uses DTrace
rwbypid.d(1m)            - read/write calls by PID. Uses DTrace
rwbytype.d(1m)           - read/write bytes by vnode type. Uses DTrace
rwsnoop(1m)              - snoop read/write events. Uses DTrace
sampleproc(1m)           - sample processes on the CPUs. Uses DTrace
seeksize.d(1m)           - print disk event seek report. Uses DTrace
setuids.d(1m)            - snoop setuid calls as they occur. Uses DTrace
sigdist.d(1m)            - signal distribution by process. Uses DTrace
syscallbypid.d(1m)       - syscalls by process ID. Uses DTrace
syscallbyproc.d(1m)      - syscalls by process name. Uses DTrace
syscallbysysc.d(1m)      - syscalls by syscall. Uses DTrace
topsyscall(1m)           - top syscalls by syscall name. Uses DTrace
topsysproc(1m)           - top syscalls by process name. Uses DTrace
</pre>

There's a lot here, we don't need to read everything. The point is that when you run into a problem you can just search through this list and find a tool dedicated to debugging that problem.

Let's say you're facing elevated disk write issues that are causing the performance of your application to degrade... But is it your app at fault or some other app?

rwbypid.d can help you with that, it can generate a list of processes and the number of calls they have for read/write based on the process id as seen in the following screenshot:

![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/qb5v35jg9mtqp8ianffx.png)

We can use this information to better understand IO issues in our code or even in 3rd party applications/libraries. `iosnoop` is another tool that helps us track IO operations but with more details:

![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/mhsosdd39vgz5mzu70ge.png)

In diagnosing elusive system issues, DTrace shines by enabling detailed observation of system calls, file operations, and network activities. For instance, it can be used to uncover the root cause of unexpected system behaviors or to trace the origin of security breaches, offering a level of detail that is often unattainable with other debugging tools.

Performance optimization is main area where DTrace demonstrates its strengths. It allows administrators and developers to pinpoint performance bottlenecks, whether they lie in application code, system calls, or hardware interactions. By providing real-time data on resource usage, DTrace helps in fine-tuning systems for optimal performance.

{#final-words}

### Final Words {#h3-13-final-words}

In conclusion, DTrace stands as a powerful and versatile tool in the realm of system monitoring and debugging. We've explored its broad capabilities, from in-depth system analysis to individual process tracing, and its remarkable performance efficiency that allows for its use in live environments.

Its cross-platform compatibility, coupled with the challenges and solutions specific to MacOS, highlights its widespread applicability.

The customizability through scripting provides unmatched flexibility, adapting to a myriad of diagnostic needs. Real-world applications of DTrace in diagnosing system issues and optimizing performance underscore its practical value.

DTrace's comprehensive toolkit offers an unparalleled window into the inner workings of systems, making it an invaluable asset for system administrators and developers alike.

Whether it's for routine troubleshooting or complex performance tuning, DTrace provides insights and solutions that are essential in the modern computing landscape.
