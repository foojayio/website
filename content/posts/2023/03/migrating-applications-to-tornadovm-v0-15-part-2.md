---
title: "Migrating Applications to TornadoVM v0.15 (Part 2)"
slug: "migrating-applications-to-tornadovm-v0-15-part-2"
date: "2023-03-04T18:38:41+00:00"
lastmod: "2023-03-04T18:54:54+00:00"
description: "In this article, we will spotlight all the TornadoVM configurations/operations that regard the execution on the hardware device."
authors:
  - "thanos-stratikopoulos"
image: "https://foojay.io/wp-content/uploads/2023/03/taskgraph-snapshot-1-1024x79-1.png"
categories:
  - "Performance"
  - "Release Notes"
  - "TornadoVM"
tags:
related_posts:
enlighterjs: true
frozen: false
---

In the [previous blog](https://foojay.io/today/migrating-applications-to-tornadovm-v0-15-part-1/), we discussed the TornadoVM programming model and showed how programmers can define the parts of their Java applications to be offloaded for hardware acceleration via the TornadoVM API (v0.15).

In this article, we will spotlight all the TornadoVM configurations/operations that regard the execution on the hardware device.

In particular, this blog has the following objectives:

* Provide guidelines regarding how programmers can trigger TornadoVM-specific configurations that regard the execution on hardware accelerators.
* Provide examples on how to exploit the new operations that are exposed by the new TornadoVM API.

1. Prerequisites {#h2-0-1-prerequisites}
----------------------------------------

This blog begins with a prerequisite that a snapshot of a user-defined TaskGraph is captured to an immutable state. This was the final point of the [previous blog](https://foojay.io/today/migrating-applications-to-tornadovm-v0-15-part-1/).

The following code snippet defines the TaskGraph of our example and shows how the **TaskGraph** is converted to an **ImmutableTaskGraph**.  

<figure class="aligncenter size-large is-resized">
 <img decoding="async" src="/images/posts/2023/03/migrating-applications-to-tornadovm-v0-15-part-2/taskgraph-snapshot-1-1024x79.png" alt="" class="wp-image-62620" width="531" height="40">
</figure>

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">TaskGraph taskGraph = new TaskGraph(“name”);
taskGraph.transferToDevice(DataTransferMode.EVERY_EXECUTION, input);
taskGraph.task(“sample”, Class::methodA, input, output);
taskGraph.transferToHost(DataTransferMode.EVERY_EXECUTION, output);
ImmutableTaskGraph itg = taskGraph.snapshot();</pre>

2. Build, Optimize and Execute an Execution Plan {#h2-1-2-build-optimize-and-execute-an-execution-plan}
-------------------------------------------------------------------------------------------------------

The first step that enables programmers to configure the execution of a TaskGraph is the creation of an execution plan. This is a new feature of TornadoVM v0.15.

The execution of a TaskGraph (former TaskSchedule) is decoupled from the TaskGraph, and it is configured via the TornadoExecutionPlan object.

An execution plan in TornadoVM is a Java object with which developers can change runtime behavior and define runtime optimizations for all immutable tasks graphs that belong to the same execution plan.

Some examples are: configuring the targeted device, enabling/disabling the profiler, and enabling the dynamic reconfiguration.

A **TornadoExecutionPlan** object accepts one or multiple immutable task graphs, as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">TornadoExecutionPlan executionPlan = new TornadoExecutionPlan(itg);</pre>

### 2.1. **What can be done with an execution plan?** {#h3-2-2-1-what-can-be-done-with-an-execution-plan}

An execution plan can be executed directly, in which case TornadoVM will apply a list of default optimizations (e.g., it will run on the default device, using the default thread scheduler).

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">executionPlan.execute();</pre>

**Note:** The **default device** is the first device that is identified by the TornadoVM runtime. This device is identified with the \`0:0\` identifier if a programmer runs the command: tornado --devices.

**Note:** The **default scheduler** is configured by the TornadoVM runtime and refers to the global and local work-group sizes that are launched per generated kernel. The default configuration of a scheduler depends on the device type (e.g., CPU, GPU, FPGA).

### **2.2. How can an application be optimized with an execution plan?** {#h3-3-2-2-how-can-an-application-be-optimized-with-an-execution-plan}

The TornadoExecutionPlan object offers a set of methods that programmers can use to configure the execution plans and apply various optimizations.

Note that the execution plan is applied for all immutable task graphs that are given in the constructor.

Beneath is an example of an execution plan that contains three additional configurations, including: i) the execution with the TornadoVM profiler enabled; ii) the application of warm-up execution which performs the compilation of the code and its installation in the code cache; and iii) the definition of the device to use for acceleration.

The configuration part is expressed as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Select a particular device using the driver and device ids 
// (from driver with id 1, the device 0).
// These identifiers are obtained by running `tornado --devices`
TornadoDevice device = getTornadoRuntime().getDriver(1).getDevice(0);

executionPlan.withProfiler(ProfilerMode.SILENT) // Enable the TornadoVM Profiler
       .withWarmUp() //  Perform a warm-up
       .withDevice(device); // Select a specific device</pre>

And the execution is launched as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">executionPlan.execute();</pre>

**Note for migration:** The **execute()** method that was exposed in the **TaskSchedule** object of TornadoVM API (prior to v0.15) needs to be replaced with: i) the creation of a **TornadoExecutionPlan** object that accepts the corresponding **ImmutableTaskGraph** object as input; and ii) the invocation of the **execute** method of the generated execution plan.

3. Obtain the result and the profiling information {#h2-4-3-obtain-the-result-and-the-profiling-information}
------------------------------------------------------------------------------------------------------------

Every time an execution plan is executed, a new object of type **TornadoExecutionResult** is created.

This object can be used to:

* query the profiling information obtained from the [TornadoVM profiler](https://tornadovm.readthedocs.io/en/latest/profiler.html#enabling-disabling-the-profiler-using-the-tornadoexecutionplan) (if it is enabled in the execution plan - Section 2).
* transfer the output data from the device to the host if the **DataTransferMode.USER_DEFINED** has been used in the definition of a TaskGraph. In this case, programmers must check whether the execution of all tasks within the TaskGraph is complete, via invoking the **isReady()** method.

An execution result can be used, as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">TornadoExecutionResult executionResult = executionPlan.execute();
TornadoProfilerResult profilerResult = executionResult.getProfilerResult();</pre>

4. Further reading and examples {#h2-5-4-further-reading-and-examples}
----------------------------------------------------------------------

The TornadoVM modules for the [tornado-unittests](https://github.com/beehive-lab/TornadoVM/tree/master/tornado-unittests) and the [tornado-examples](https://github.com/beehive-lab/TornadoVM/tree/master/tornado-examples) contain a list of diverse applications that showcase how to use the new TornadoVM API.

For more information see [here](https://tornadovm.readthedocs.io/en/latest/programming.html).

Part of the content of this blog has been presented in[FOSDEM' 23](https://ftp.fau.de/fosdem/2023/H.1302%20(Depage)/hardware.mp4).
