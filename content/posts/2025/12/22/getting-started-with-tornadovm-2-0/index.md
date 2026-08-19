---
title: "Getting Started with TornadoVM 2.0 for Accelerating Java Applications"
date: "2025-12-22T10:00:00+00:00"
lastmod: "2025-12-22T17:37:05+00:00"
description: "Starting with TornadoVM 2.0, installing and using TornadoVM is easier than ever. The project now provides prebuilt SDKs for multiple operating systems,…"
authors:
  - "thanos-stratikopoulos"
image: "tornado-insight.webp"
categories:
  - "Maven"
  - "Tools"
  - "TornadoVM"
related_posts:
  - "this-dependency-update-looked-exactly-like-an-account-takeover"
  - "tornadoinsight-compatibility-with-tornadovm-sdk-2-0-configuration-guide"
  - "how-to-publish-a-java-maven-project-to-maven-central-using-jreleaser-and-github-actions-2025-guide"
  - "foojay-podcast-82"
frozen: false
---

Starting with **TornadoVM 2.0** , installing and using TornadoVM is easier than ever. The project now provides prebuilt SDKs for multiple operating systems, architectures, and accelerator backends, and is also available via **Maven Central** for seamless integration with existing Java codebases.{#viewer-bsvyh182}

This guide walks you through:{#viewer-aq221189}

* Installing the TornadoVM SDK
* Verifying your setup
* Integrating TornadoVM into Java projects using Maven

## Prerequisites

Before installing TornadoVM, ensure that your system has the following:{#viewer-k00hu10420}

- **Java Development Kit (JDK) 21**{#viewer-x90cz2860}

- **JAVA_HOME** correctly set to your JDK 21 installation{#viewer-v7w992863}

```bash
sdk install java 21.0.2-open
```

SDKMAN! will automatically set **JAVA_HOME** and make the JDK available to TornadoVM.

## Downloading and Installing the TornadoVM SDK

TornadoVM SDKs come as ZIP archives tailored for different operating systems, CPU architectures, and accelerator backends. Choose the SDK that matches your setup from the official [++TornadoVM webpage++](https://www.tornadovm.org/downloads) or the [SDKMAN! TornadoVM page](https://sdkman.io/sdks/tornadovm/).{#viewer-dae9277e-ba59-42c1-b958-a40a2e031a81}  

{{< img src="tornadovm-sdkman-1024x617.png" class="aligncenter size-large is-resized" width="1024" height="617" style="width:578px;height:auto" >}}

You can choose a backend-specific build:{#ib4ew13010}

|--------------|------------------------|
| Backend      | SDKMAN! Latest Version |
| OpenCL       | 2.2.0-opencl (default) |
| PTX          | 2.2.0-ptx              |
| SPIR-V       | 2.2.0-spirv            |
| All Backends | 2.2.0-full             |

### Installation Steps by Operating System

#### Linux / macOS

Open a terminal and run:{#viewer-942675ef-8b3d-44f9-ba30-0b5847ce2896}

```bash
sdk install tornadovm 2.2.0-opencl
```

```bash

sdk install tornadovm 2.2.0-opencl
```

After installation, SDKMAN! automatically sets the **TORNADOVM_HOME** environment variable.

#### Windows (10+)

Using Command Prompt or PowerShell:

```powershell
curl -L -o tornadovm-2.2.0-opencl-windows-amd64.zip https://github.com/beehive-lab/TornadoVM/releases/download/v2.2.0/tornadovm-2.2.0-opencl-windows-amd64.zip

tar -xf tornadovm-2.2.0-opencl-windows-amd64.zip

set TORNADO_SDK=%cd%\tornadovm-2.2.0-opencl

set PATH=%TORNADO_SDK%\bin;%PATH%
```

## Verify Available Devices

Once TornadoVM is installed, verify that your system detects the available hardware accelerators.

{#uhu5s12601}

Run the following command:{#sm644474}

```bash
tornado --devices
```

This command lists all devices recognized by TornadoVM, including CPUs and GPUs. If your accelerator appears in the output, your system is ready.{#9q1el478}

## Run Your First TornadoVM Program

TornadoVM includes example applications that demonstrate how Java programs can be accelerated transparently.{#ibr6b482}

A simple starting point is a **Matrix-Vector multiplication** example.{#ta49l9349}

#### Linux / macOS

```bash
java @$TORNADOVM_HOME/tornado-argfile -cp $TORNADOVM_HOME/share/java/tornado/tornado-examples-2.2.0.jar uk.ac.manchester.tornado.examples.compute.MatrixVectorRowMajor
```

#### Windows (10+)

```powershell
java @%TORNADOVM_HOME%\tornado-argfile -cp %TORNADOVM_HOME%\share\java\tornado\tornado-examples-2.2.0.jar uk.ac.manchester.tornado.examples.compute.MatrixVectorRowMajoruk.ac.manchester.tornado.examples.compute.MatrixVectorRowMajor
```

This program runs a Java application that TornadoVM automatically offloads to available accelerators.{#3rprp494}

## Integrating TornadoVM into Java Projects Using Maven

Since **TornadoVM v2.0.0** , TornadoVM has been available via [Maven Central](https://central.sonatype.com/namespace/io.github.beehive-lab), which simplifies adding it to your Java projects. To integrate TornadoVM, add the following dependency to your ***pom.xml***:{#fa986f36-2454-46d2-bd91-c14f8b4dbb96}

```bash
<dependencies>
  <dependency>
    <groupId>io.github.beehive-lab</groupId>
    <artifactId>tornado-api</artifactId>
    <version>2.1.0</version>
  </dependency>
  <dependency>
    <groupId>io.github.beehive-lab</groupId>
    <artifactId>tornado-runtime</artifactId>
    <version>2.1.0</version>
  </dependency>
</dependencies>
```

This setup allows your project to compile and run with TornadoVM support without manual SDK management.{#e7129922-9c05-4948-959a-58ed0571ed45}

### Example: Accelerating a Simple Java Kernel

Here is a basic example class of how to use TornadoVM to accelerate a Java method (vectorAdd):{#0491a289-1514-4666-b9fd-9da119c8abce}

```java
import uk.ac.manchester.tornado.api.ImmutableTaskGraph;
import uk.ac.manchester.tornado.api.TaskGraph;
import uk.ac.manchester.tornado.api.TornadoExecutionPlan;
import uk.ac.manchester.tornado.api.annotations.Parallel;
import uk.ac.manchester.tornado.api.common.TornadoDevice;
import uk.ac.manchester.tornado.api.enums.DataTransferMode;
import uk.ac.manchester.tornado.api.runtime.TornadoRuntimeProvider;
import uk.ac.manchester.tornado.api.types.arrays.IntArray;

public class VectorAdd {

    private static void vectorAdd(IntArray a, IntArray b, IntArray c) {
        for (@Parallel int i = 0; i < c.getSize(); i++) {
            c.set(i, a.get(i) + b.get(i));
        }
    }

    public static void main(String[] args) {
        int size = Integer.parseInt(args[0]);

        IntArray a = new IntArray(size);
        IntArray b = new IntArray(size);
        IntArray c = new IntArray(size);

        a.init(10);
        b.init(20);

        TornadoDevice firstDevice =
                TornadoRuntimeProvider.getTornadoRuntime()
                        .getBackend(0)
                        .getDevice(0);

        TaskGraph taskGraph = new TaskGraph("s0")
                .transferToDevice(DataTransferMode.EVERY_EXECUTION, a, b)
                .task("t0", VectorAdd::vectorAdd, a, b, c)
                .transferToHost(DataTransferMode.EVERY_EXECUTION, c);

        ImmutableTaskGraph immutableTaskGraph = taskGraph.snapshot();
        TornadoExecutionPlan executionPlan = new TornadoExecutionPlan(immutableTaskGraph);

        executionPlan.withDevice(firstDevice).execute();

        System.out.println("Computation completed on device: " + firstDevice.getDescription());

        System.out.print("c[0.." + (size - 1) + "] = ");
        for (int i = 0; i < size; i++) {
            System.out.print(c.get(i));
            if (i < size - 1) System.out.print(", ");
        }
        System.out.println();

        // Optional: quick check
        System.out.println("Expected each element = 30");
    }
}
```

This example shows how to offload a simple vector addition to the accelerator device detected by TornadoVM.{#288785fa-3464-48f8-a256-c3be764f8756}

You can compile and run this class in a new project, as follows:{#vhodb68880}

```bash
mvn clean compile
tornado --threadInfo -cp target/classes VectorAdd 256
```

The output will be something like this:{#7hpnd75899}

```bash
WARNING: Using incubator modules: jdk.incubator.vector
Task info: s0.t0
	Backend           : OPENCL
	Device            : Apple M4 Pro CL_DEVICE_TYPE_GPU (available)
	Dims              : 1
	Global work offset: [0]
	Global work size  : [256]
	Local  work size  : [64, 1, 1]
	Number of workgroups  : [4]

Computation completed on device: Apple M4 Pro CL_DEVICE_TYPE_GPU (available)
c[0..255] = 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30, 30
Expected each element = 30
```

## What's Next?

After running your first program, you can:{#oicl4498}

* Explore more TornadoVM [++examples++](https://github.com/beehive-lab/TornadoVM/tree/master/tornado-examples/src/main/java/uk/ac/manchester/tornado/examples)
* [++Integrate++](https://www.tornadovm.org/technology) TornadoVM into your own Java projects
* [++Learn++](https://tornadovm.readthedocs.io/en/latest/programming.html#expressing-parallelism-within-java-methods) the Loop Parallel API and Kernel API

{#nztn010736}

Full documentation is available here:[++https://tornadovm.readthedocs.io/en/latest/++](https://tornadovm.readthedocs.io/en/latest/)

## Final Thoughts

Using SDKMAN!, getting started with TornadoVM takes only a few commands. Once installed, TornadoVM allows Java developers to take advantage of heterogeneous hardware without rewriting applications in specialized languages.{#7i174507}

If you know Java, you are ready to start accelerating your applications with TornadoVM.{#0vjkf12069}

Happy accelerating 🚀
