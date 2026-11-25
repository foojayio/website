---
title: "Getting Started with TornadoVM 8.2.0: SDKMAN!, Maven and JBang"
date: ""
description: "TornadoVM 8.2.0 three ways: SDKMAN!, now with a HIP backend for AMD GPUs; Maven Central with no SDK; and one Java file run with JBang."
authors:
  - "thanos-stratikopoulos"
image: "tornadovm-8-2-0-getting-started.jpg"
categories:
  - "TornadoVM"
  - "Tools"
  - "Java Beginner"
related_posts:
  - "getting-started-with-tornadovm-2-0"
  - "the-tornadovm-programming-model-explained"
  - "build-and-run-tornadovm-with-intellij-idea"
---

A year ago, [Getting Started with TornadoVM 2.0 for Accelerating Java Applications](/today/getting-started-with-tornadovm-2-0/) showed how to install TornadoVM with SDKMAN! and use it from a Maven project, and the TornadoVM blog followed with a short [guide to TornadoVM on SDKMAN!](https://www.tornadovm.org/blogs/getting-started-with-tornadovm-using-sdkman). A lot has changed since then. This article picks up from there with TornadoVM 8.2.0 and shows three ways to start: the SDK through SDKMAN!, plain Maven dependencies with no SDK at all, and a single Java file that you run with JBang.

If you prefer to watch first, the TornadoVM video [Install TornadoVM, Four Ways: SDKMAN!, Downloads, Build from Source, JBang](https://www.youtube.com/watch?v=_ipmOKOYj_s) shows the installation options step by step.

{{< youtube _ipmOKOYj_s >}}

## What changed since 2.0

- **No more JVMCI or JNI.** Since 6.0.0, TornadoVM calls the OpenCL, CUDA and Metal drivers through the Foreign Function & Memory API instead of JNI, and it no longer needs JVMCI. It runs on standard OpenJDK builds from JDK 21 to JDK 27.
- **Its own compiler.** Since 8.0.0, [TC, TornadoVM's own compiler](https://www.tornadovm.org/blogs/tornadovm-8-0-0-tc-tornadovm-compiler), turns your Java methods into GPU kernels. It replaces the copy of the Graal compiler that TornadoVM used to ship.
- **Four backends.** OpenCL, NVIDIA CUDA, Apple Metal and, new in 8.2.0, AMD HIP. The PTX and SPIR-V backends from the 2.0 article were retired in 6.0.0.
- **No SDK needed for your own projects.** Since 8.1.0, the OpenCL, Metal and CUDA drivers are all on Maven Central, and the jars are a complete runtime.
- **Two builds per release.** `jdk21` for JDK 21, and `jdk22plus` for JDK 22 and newer.

## Option 1: Install the SDK with SDKMAN!

The SDK gives you the `tornado` launcher, the bundled examples and the developer tools. With [SDKMAN!](https://sdkman.io/):

```bash
sdk install tornadovm
```

This installs the default build, `8.2.0-jdk21-opencl`, and sets `TORNADOVM_HOME`. To pick a backend or the JDK 22+ line, name the version:

| Platform | SDKMAN! versions |
|---|---|
| Linux x64 | `8.2.0-jdk21-opencl`, `8.2.0-jdk21-cuda`, `8.2.0-jdk21-hip` |
| macOS (Apple silicon) | `8.2.0-jdk21-opencl`, `8.2.0-jdk21-metal` |

Each is also available as `jdk22plus`. For example, for JDK 25 on an Apple silicon Mac:

```bash
sdk install tornadovm 8.2.0-jdk22plus-metal
```

SDKs for Windows (OpenCL and CUDA) and for Linux arm64 (CUDA) are on the [TornadoVM downloads page](https://www.tornadovm.org/downloads).

### New in 8.2.0: AMD GPUs through HIP

```bash
sdk install tornadovm 8.2.0-jdk22plus-hip
```

The HIP backend generates HIP C++ and compiles it with hipRTC for your GPU's own architecture (`gfx90a`, `gfx942`, `gfx1100`, ...). It is experimental. It needs ROCm 10.1 or newer, installed from [TheRock](https://github.com/ROCm/TheRock)'s Python packages, and `ROCM_PATH` set to the directory that `rocm-sdk path --root` prints. Its options are listed under "HIP Specific" in the [TornadoVM flags documentation](https://tornadovm.readthedocs.io/en/latest/flags.html).

### Check your devices and run an example

```bash
tornado --devices
```

lists every device TornadoVM can use. On an Apple M4 Pro with the Metal SDK, for example:

```
Number of Tornado drivers: 1
Driver: Metal
  Total number of Metal devices  : 1
  Tornado device=0:0  (DEFAULT)
	METAL --  [Apple Metal] -- Apple M4 Pro
		Global Memory Size: 37.4 GB
		Local Memory Size: 32.0 KB
```

Then run one of the bundled examples:

```bash
java @$TORNADOVM_HOME/tornado-argfile \
  -cp $TORNADOVM_HOME/share/java/tornado/tornado-examples-8.2.0.jar \
  uk.ac.manchester.tornado.examples.compute.MatrixVectorRowMajor
```

## Option 2: Maven, with no SDK

For your own project, you don't need the SDK at all. Add the API, the runtime and one or more drivers, matching the JDK you build with:

```xml
<dependencies>
  <dependency>
    <groupId>io.github.beehive-lab</groupId>
    <artifactId>tornado-api</artifactId>
    <version>8.2.0-jdk21</version>
  </dependency>
  <dependency>
    <groupId>io.github.beehive-lab</groupId>
    <artifactId>tornado-runtime</artifactId>
    <version>8.2.0-jdk21</version>
  </dependency>
  <!-- One or more backends: tornado-drivers-opencl, tornado-drivers-metal, tornado-drivers-cuda -->
  <dependency>
    <groupId>io.github.beehive-lab</groupId>
    <artifactId>tornado-drivers-opencl</artifactId>
    <version>8.2.0-jdk21</version>
  </dependency>
</dependencies>
```

On JDK 22 or newer, use `8.2.0-jdk22plus` instead. Each driver binds its native library (OpenCL, Metal or the CUDA driver) through `java.lang.foreign`. For OpenCL and Metal, the GPU driver of your machine is all you need. The CUDA backend also needs the [CUDA Toolkit](https://developer.nvidia.com/cuda-downloads): it compiles kernels with NVRTC, which ships with the toolkit rather than the driver. The HIP backend is not on Maven Central; use the SDK for it.

Run your application with:

- `--enable-native-access=ALL-UNNAMED`, which silences the warning about restricted methods
- `--enable-preview`, with the `jdk21` artifacts only
- `-Dtornado.enabled.backends=opencl`, `metal` or `cuda`, to choose a backend when several drivers are on the class path

Without an SDK, TornadoVM keeps its settings and code cache in `~/.tornadovm`.

## Option 3: One Java file, run with JBang

[JBang](https://www.jbang.dev/) reads the dependencies from comments at the top of a Java file, downloads them (and a JDK, if you don't have one), and runs the file. Save this as `HelloTornado.java`:

```java
/*
 * Step 1: one Java method, JIT-compiled by TornadoVM for the GPU.
 *
 *   c[i] = a[i] + b[i]
 *
 * The loop marked @Parallel becomes the GPU's thread index; everything else is plain Java.
 * The TaskGraph says what runs and which data moves; the TornadoExecutionPlan runs it.
 * The result is checked against the same loop on the CPU.
 */
package com.example.tornado;

import uk.ac.manchester.tornado.api.ImmutableTaskGraph;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;
import uk.ac.manchester.tornado.api.exceptions.TornadoExecutionPlanException;
import uk.ac.manchester.tornado.api.types.arrays.FloatArray;

//JAVA 25
//DEPS io.github.beehive-lab:tornado-runtime:8.2.0-jdk22plus
//DEPS io.github.beehive-lab:tornado-drivers-opencl:8.2.0-jdk22plus
//RUNTIME_OPTIONS --enable-native-access=ALL-UNNAMED

public class HelloTornado {

    /** The kernel: TornadoVM compiles this method, not the JVM. */
    public static void vectorAdd(FloatArray a, FloatArray b, FloatArray c) {
        for (@Parallel int i = 0; i < c.getSize(); i++) {
            c.set(i, a.get(i) + b.get(i));
        }
    }

    public static void main(String[] args) throws TornadoExecutionPlanException {
        int size = args.length > 0 ? Integer.parseInt(args[0]) : 1 << 24;
        int iterations = args.length > 1 ? Integer.parseInt(args[1]) : 10;

        // FloatArray lives off-heap, so TornadoVM can hand it to the GPU without copying it first
        FloatArray a = new FloatArray(size);
        FloatArray b = new FloatArray(size);
        FloatArray c = new FloatArray(size);
        for (int i = 0; i < size; i++) {
            a.set(i, i);
            b.set(i, 2.0f * i);
        }

        TaskGraph taskGraph = new TaskGraph("hello") //
                .transferToDevice(DataTransferMode.FIRST_EXECUTION, a, b) //
                .task("vectorAdd", HelloTornado::vectorAdd, a, b, c) //
                .transferToHost(DataTransferMode.EVERY_EXECUTION, c);
        ImmutableTaskGraph immutableTaskGraph = taskGraph.snapshot();

        System.out.println("Device: " + TornadoExecutionPlan.getDevice(0, 0).getDescription());
        System.out.println("vectorAdd of " + size + " floats, " + iterations + " executions");

        try (TornadoExecutionPlan plan = new TornadoExecutionPlan(immutableTaskGraph)) {
            for (int it = 0; it < iterations; it++) {
                long start = System.nanoTime();
                plan.execute();
                long elapsed = System.nanoTime() - start;
                // the first execution includes the JIT compilation of vectorAdd
                System.out.printf("  execution %2d: %8.3f ms%s%n", it, elapsed / 1e6, it == 0 ? "  (JIT compile + run)" : "");
            }
        }

        FloatArray expected = new FloatArray(size);
        vectorAdd(a, b, expected); // the same method, run by the JVM on the CPU
        for (int i = 0; i < size; i++) {
            if (c.get(i) != expected.get(i)) {
                System.out.println("WRONG at " + i + ": " + c.get(i) + " != " + expected.get(i));
                System.exit(1);
            }
        }
        System.out.println("Result is correct for " + size + " elements");
    }
}
```

Then run it:

```bash
jbang HelloTornado.java
```

On an Apple M4 Pro, with no TornadoVM SDK installed:

```
Device: Apple M4 Pro CL_DEVICE_TYPE_GPU (available)
vectorAdd of 16777216 floats, 10 executions
  execution  0:  350.764 ms  (JIT compile + run)
  execution  1:    5.503 ms
  execution  2:    5.467 ms
  ...
  execution  9:    2.956 ms
Result is correct for 16777216 elements
```

The first execution includes TC compiling `vectorAdd` into an OpenCL kernel; the later ones only launch it and copy `c` back. The rest of the file is ordinary Java:

- `@Parallel` marks the loop whose iterations run as GPU threads.
- The `TaskGraph` says which data goes to the device, which method runs, and which data comes back.
- The `TornadoExecutionPlan` compiles and runs the graph.
- The last loop calls the same `vectorAdd` method on the CPU and checks that the results agree.

To run on another backend, change the driver line, for example to `tornado-drivers-metal` on a Mac or `tornado-drivers-cuda` on an NVIDIA GPU with the CUDA Toolkit installed. For the `jdk21` artifacts, use `//JAVA 21` and add `//PREVIEW`.

## What's next

- The [TornadoVM documentation](https://tornadovm.readthedocs.io/en/latest/) covers the [Loop Parallel API](https://tornadovm.readthedocs.io/en/latest/programming.html#loop-parallel-api) used above, the [Kernel API](https://tornadovm.readthedocs.io/en/latest/programming.html#kernel-context-api) and the [TileContext API](https://tornadovm.readthedocs.io/en/latest/programming.html#tile-context-api).
- The SDK's [examples](https://github.com/beehive-lab/TornadoVM/tree/master/tornado-examples/src/main/java/uk/ac/manchester/tornado/examples) include NBody, DFT, KMeans, matrix kernels and reductions.
- The documentation's [Task-Graph API](https://tornadovm.readthedocs.io/en/latest/programming.html#task-graph-api) and [Execution Plans](https://tornadovm.readthedocs.io/en/latest/programming.html#execution-plan) sections go deeper into how TornadoVM moves data and runs your tasks.

If you know Java, you can already program your GPU. Happy accelerating!
