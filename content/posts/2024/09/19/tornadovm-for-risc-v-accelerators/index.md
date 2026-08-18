---
title: "TornadoVM for RISC-V Accelerators"
date: "2024-09-19T07:50:28+00:00"
lastmod: "2024-09-19T08:05:01+00:00"
description: "Is Java ready for running on RISC-V accelerators? If so, how? This post shows a possible way by using open source solutions integrating TornadoVM with the oneAPI Construction Kit. - by Juan Fumero"
authors:
  - "juan-fumero"
image: "Picture1.jpg"
categories:
  - "Research"
  - "TornadoVM"
  - "Tutorials"
tags:
related_posts:
  - "a-flavour-of-tornadovm-on-apple-m1-pro"
  - "code-interoperability-mode-for-opencl-portability-across-various-programming-languages-with-tornadovm"
  - "defining-patterns-of-data-transfers-for-java-applications-with-tornadovm"
  - "idempotent-spring-boot-starter"
frozen: false
---

## Takeaways

* RISC-V is an open standard instruction set architecture that anyone can use to build new processors and accelerators, for example, for AI.
* oneAPI Construction Kit (OCK) is a new framework that allows software developers and system designers to bridge the gap between hardware accelerators and open standards, by enabling a programming system to implement domain specific instructions for modern hardware such as RISC-V accelerators and custom processors.
* TornadoVM, a parallel programming framework for Java, can take advantage of OCK to offload and accelerate Java workloads on RISC-V chips. This post shows how to run on RISC-V CPUs with vector extensions.

## Introduction

[RISC-V](https://riscv.org/ "RISC-V") is an open standard instruction set architecture (ISA) based on the principles of reduced instruction sets (RISC). RISC-V is freely available under open licences, allowing anyone to design, modify and implement RISC-V processors.

RISC-V designs are highly modular, and it contains [many extensions](https://wiki.riscv.org/display/HOME/All+Specifications+Under+Development "many extensions") that system designers can use to build specialised processors (e.g., Security extensions, HPC extensions, Vector extensions, etc).

But, how do we run software on these new and modular processors, and how can we take advantage of future specialised designs for parallelism, deep learning, AI, etc? One way to get this mapping between software and hardware is via the oneAPI Construction Kit.

The [oneAPI Construction Kit](https://github.com/codeplaysoftware/oneapi-construction-kit "oneAPI Construction Kit") (OCK) is an open-source programming framework that allows developers to implement operations for new hardware such as RISC-V accelerators. What makes OCK special is that this framework can facilitate developers to map open programming standards, such as SYCL and OpenCL, to new hardware accelerators. With a few configuration files, developers can define the set of operations for the new hardware, and then map those operations to the implementation of open standards like SYCL.

Furthermore, OCK could also be used as a runtime dispatcher of OpenCL and SPIR-V kernels, and this is the interest of this post. We could configure OCK to provide an OpenCL implementation to run on RISC-V cores.

But, can we run Java workloads on these accelerators? Well, with the help of TornadoVM, a parallel programming framework for acceleration of Java workloads, it is possible. This post explains how to configure and install OCK to work as a backend for TornadoVM, allowing Java developers to access CPUs from different vendors, including RISC-V accelerators. Are you curious? Let's find out how this is possible.

## OCK for TornadoVM

OCK is in active development, and it is built based on the LLVM compiler. This post explains how to obtain the main dependencies from source files and Linux systems. All components explained in this post are fully open source.

### Installing prerequisites:

If you use Fedora or Red Hat-based distributions, you will need to install the following dependencies.

```batch
sudo dnf install dtc ninja doxygen python3-pip git cmake spirv-tools 
sudo pip3 install lit cmakelint
```

### Installation of OCK for TornadoVM

Let's configure OCK to run on RISC-V accelerators and use them as devices for TornadoVM. In the case of RISC-V, since I do not have any hardware available, TornadoVM can run on a RISC-V simulator from an X86/64 machine.

To do so, OCK comes with a simulator, called [Codeplay Reference Silicon](https://developer.codeplay.com/products/oneapi/construction-kit/3.0.0/guides/overview/reference-silicon/overview.html "Codeplay Reference Silicon") (RefSi), which is based on [SPIKE](https://github.com/riscv-software-src/riscv-isa-sim "SPIKE"), and it can be used in combination with our Java/TornadoVM programs to run on RISC-V with Vector Instruction extensions! How cool is this?

#### Installation of LLVM

Configure LLVM for RISC-V as follows:

```bash
mkdir ock 
cd ock 
baseDIR=$PWD 
git clone https://github.com/llvm/llvm-project.git llvm
cd llvm-project
llvmDIR=$PWD
```

At the time of writing this post, the supported LLVM version for OCK is 18. Thus, we need to configure LLVM using the 18.x branch:

```
git checkout release/18.x 

export LLVMINSTALL=$llvmDIR/build-riscv/install

cmake llvm -GNinja \
-Bbuild-riscv \
-DCMAKE_BUILD_TYPE=Release \
-DCMAKE_INSTALL_PREFIX=$PWD/build-riscv/install \
-DLLVM_ENABLE_PROJECTS="clang;lld" \
-DLLVM_TARGETS_TO_BUILD='X86;RISCV'

ninja -C build-riscv install
```

#### Configuring oneAPI Construction Kit for RISC-V

```
cd $baseDIR/oneapi-construction-kit  

cmake -GNinja -Bbuild-riscv \
-DCA_RISCV_ENABLED=ON \
-DCA_MUX_TARGETS_TO_ENABLE="riscv" \
-DCA_LLVM_INSTALL_DIR=$llvmDIR/build-riscv/install \
-DCA_ENABLE_HOST_IMAGE_SUPPORT=OFF \
-DCA_ENABLE_API=cl \
-DCA_CL_ENABLE_ICD_LOADER=ON \
-DCMAKE_INSTALL_PREFIX=$PWD/build-riscv/install 

ninja -C build-riscv install
```

```

```

Next, we need to configure the Linux system to use the new OpenCL installation. There are various ways to get this. One of them is updating the folder `/etc/OpenCL/vendors/` with a new file that contains the path to the `libCL.so` installation.

```bash
sudo vim /etc/OpenCL/vendors/ock.icd
```

And we add the following line to the file: use your absolute path to the `libCL.so` file.

```bash
/home/juan/repos/ock/oneapi-construction-kit/build-x86_64/install/lib/libCL.so
```

#### Installing TornadoVM

We are now ready to install TornadoVM. Note that, if you have TornadoVM already installed, there is no need to reconfigure it. TornadoVM will detect the new device automatically.  The following line installs TornadoVM to use the OpenCL backend.

```bash
cd TORNADOVM_ROOT 
./bin/tornadovm-installer --jdk jdk21 --backend=opencl
source setvars.sh
```

Let's explore all devices available:

```bash
tornado --devices
```

And we will get the following output. Note that the number of devices and the ordering depends on your local configuration. In my case, I have two GPUs that can be used with the OpenCL backend, namely an NVIDIA RTX 3070 and an Intel Integrated GPU (UHD 770). Additionally, we get a new device called **RefSi G1 RV64**. This is our RISC-V device we have just configured with OCK.

```batch
Number of Tornado drivers: 1
Driver: OpenCL
  Total number of OpenCL devices  : 3
  Tornado device=0:0  (DEFAULT)
    OPENCL --  [NVIDIA CUDA] -- NVIDIA GeForce RTX 3070
        Global Memory Size: 7.8 GB
        Local Memory Size: 48.0 KB
        Workgroup Dimensions: 3
        Total Number of Block Threads: [1024]
        Max WorkGroup Configuration: [1024, 1024, 64]
        Device OpenCL C version: OpenCL C 1.2

  Tornado device=0:1
    OPENCL --  [Intel(R) OpenCL Graphics] -- Intel(R) UHD Graphics 770
        Global Memory Size: 28.8 GB
        Local Memory Size: 64.0 KB
        Workgroup Dimensions: 3
        Total Number of Block Threads: [512]
        Max WorkGroup Configuration: [512, 512, 512]
        Device OpenCL C version: OpenCL C 1.2

  Tornado device=0:2
    OPENCL --  [ComputeAorta] -- RefSi G1 RV64      << RISC-V OpenCL Device 
        Global Memory Size: 2.0 GB
        Local Memory Size: 256.0 KB
        Workgroup Dimensions: 3
        Total Number of Block Threads: [1024]
        Max WorkGroup Configuration: [1024, 1024, 1024]
        Device OpenCL C version: OpenCL C 1.2 Clang 18.1.8
```

The **RefSi G1 RV64** device runs on a simulator. Let's run an example, a simple vector addition to illustrate the usage of TornadoVM on RISC-V with some debug information tell us which device was used.

The Java example used is available on [GitHub](https://github.com/beehive-lab/TornadoVM/blob/ab82a851d7201eef2cbec1d7cb77ed2e72f415e8/tornado-examples/src/main/java/uk/ac/manchester/tornado/examples/arrays/ArrayAddInt.java#L39-L72 "GitHub") and it is described as follows:

```java
public static void add(IntArray a, IntArray b, IntArray c) {
   for (@Parallel int i = 0; i < c.getSize(); i++) {
       c.set(i, a.get(i) + b.get(i));
   }
}

public static void main(String[] args) throws TornadoExecutionPlanException {

   final int numElements = 8;
   IntArray a = new IntArray(numElements);
   IntArray b = new IntArray(numElements);
   IntArray c = new IntArray(numElements);

   a.init(1);
   b.init(2);
   c.init(0);

   TaskGraph taskGraph = new TaskGraph("s0") //
           .transferToDevice(DataTransferMode.FIRST_EXECUTION, a, b) //
           .task("t0", ArrayAddInt::add, a, b, c) //
           .transferToHost(DataTransferMode.EVERY_EXECUTION, c);

   ImmutableTaskGraph immutableTaskGraph = taskGraph.snapshot();
   try (TornadoExecutionPlan executor = new TornadoExecutionPlan(immutableTaskGraph)) {
       executor.execute();
   }

   System.out.println("a: " + Arrays.toString(a.toHeapArray()));
   System.out.println("b: " + Arrays.toString(b.toHeapArray()));
   System.out.println("c: " + Arrays.toString(c.toHeapArray()));
}
```

The **add** method is the actual method to be accelerated on the RISC-V device. Note that, in the Java code, there is no information about which device to use, how to offload or which backend to use. TornadoVM will perform compilation, data handling and runtime scheduling automatically.

To run the application:

```batch
tornado --jvm="-Ds0.t0.device=0:2" --threadInfo -m tornado.examples/uk.ac.manchester.tornado.examples.arrays.ArrayAddInt
```

And the output:

```
Task info: s0.t0
    Backend           : OPENCL
    Device            : RefSi G1 RV64 CL_DEVICE_TYPE_ACCELERATOR (available)
    Dims              : 1
    Global work offset: [0]
    Global work size  : [8]
    Local  work size  : [8, 1, 1]
    Number of workgroups  : [1]

a: [1, 1, 1, 1, 1, 1, 1, 1]
b: [2, 2, 2, 2, 2, 2, 2, 2]
c: [3, 3, 3, 3, 3, 3, 3, 3]
```

```batch

```

**We just offloaded a Java method to a RISC-V accelerator!** This example is a simple vector addition, and, as we can see from the task-info, the device selected was a RISC-V with RVV vector extensions. We can go a step further and emit the RISC-V assembly code for our generated OpenCL kernel. Todo do so, we need to reconfigure OCK with debug information enabled using `-DCA_ENABLE_DEBUG_SUPPORT=ON -DCA_DEBUG_SUPPORT=ON` as follows:

```bash
cmake -GNinja -Bbuild-riscv-debug \
-DCA_ENABLE_DEBUG_SUPPORT=ON \
-DCA_DEBUG_SUPPORT=ON \
-DCA_RISCV_ENABLED=ON \
-DCA_MUX_TARGETS_TO_ENABLE="riscv" \
-DCA_LLVM_INSTALL_DIR=$llvmDIR/build-riscv/install \
-DCA_ENABLE_HOST_IMAGE_SUPPORT=OFF \
-DCA_ENABLE_API=cl \
-DCA_CL_ENABLE_ICD_LOADER=ON \
-DCMAKE_INSTALL_PREFIX=$PWD/build-riscv-debug/install

ninja -C build-riscv-debug install
```

Then, before we run TornadoVM, we need to export the following env variable:

```bash
export CA_RISCV_DUMP_ASM=1    ## Print Assembly code
```

We run the same application:

```bash
tornado --jvm="-Ds0.t0.device=0:2" --threadInfo -m tornado.examples/uk.ac.manchester.tornado.examples.arrays.ArrayAddInt
```

And we will obtain the following output:

```asm
.text
    .attribute	4, 16
    .attribute	5, "rv64i2p1_m2p0_a2p1_f2p2_d2p2_c2p0_v1p0_zicsr2p0_zve32f1p0_zve32x1p0_zve64d1p0_zve64f1p0_zve64x1p0_zvl128b1p0_zvl32b1p0_zvl64b1p0"
    .file	"kernel.opencl"
    .globl	"add.mux-kernel-wrapper"
    .p2align	1
    .type	"add.mux-kernel-wrapper",@function
"add.mux-kernel-wrapper":
    addi	sp, sp, -80
    sd	ra, 72(sp)
    sd	s0, 64(sp)
    sd	s2, 56(sp)
    sd	s3, 48(sp)
    sd	s4, 40(sp)
    sd	s5, 32(sp)
    sd	s6, 24(sp)
    sd	s7, 16(sp)
    sd	s8, 8(sp)
    sd	s9, 0(sp)
    addi	s0, sp, 80
    andi	sp, sp, -16
    li	a7, 0
    ld	t4, 32(a0)
    ld	t3, 40(a0)
    ld	t2, 48(a0)
    ld	s2, 72(a1)
    ld	t0, 80(a1)
    ld	a0, 0(a1)
    ld	a2, 24(a1)
    ld	a6, 88(a1)
    ld	s4, 48(a1)
    mul	s3, a0, s2
    mulw	s8, s2, a2
    addi	t2, t2, 24
    addw	t1, s4, s3
    slli	s9, s8, 2
    addi	t3, t3, 24
    addi	t4, t4, 24
    li	t5, 7
    li	s5, 8
    j	.LBB0_2
.LBB0_1:
    addi	a7, a7, 1
    beq	a7, a6, .LBB0_9
.LBB0_2:
    li	t6, 0
    j	.LBB0_4
.LBB0_3:
    addi	t6, t6, 1
    beq	t6, t0, .LBB0_1
.LBB0_4:
    li	s7, 0
    mv	s6, t1
    j	.LBB0_6
.LBB0_5:
    addi	s7, s7, 1
    addiw	s6, s6, 1
    beq	s7, s2, .LBB0_3
.LBB0_6:
    add	a5, s3, s7
    addw	a1, a5, s4
    blt	t5, a1, .LBB0_5
    slli	a1, s6, 2
    add	a4, t2, a1
    add	a3, t3, a1
    add	a1, a1, t4
    addw	a5, a5, s4
.LBB0_8:
    lw	a0, 0(a1)
    lw	a2, 0(a3)
    add	a0, a0, a2
    sw	a0, 0(a4)
    add	a5, a5, s8
    add	a4, a4, s9
    add	a3, a3, s9
    add	a1, a1, s9
    blt	a5, s5, .LBB0_8
    j	.LBB0_5
.LBB0_9:
    addi	sp, s0, -80
    ld	ra, 72(sp)
    ld	s0, 64(sp)
    ld	s2, 56(sp)
    ld	s3, 48(sp)
    ld	s4, 40(sp)
    ld	s5, 32(sp)
    ld	s6, 24(sp)
    ld	s7, 16(sp)
    ld	s8, 8(sp)
    ld	s9, 0(sp)
    addi	sp, sp, 80
    ret
.Lfunc_end0:
    .size	"add.mux-kernel-wrapper", .Lfunc_end0-"add.mux-kernel-wrapper"

    .type	notes_global,@object
    .section	notes,"a",@progbits
    .globl	notes_global
notes_global:
    .asciz	
               … 
    .size	notes_global, 192

    .ident	"clang version 18.1.8 (https://github.com/llvm/llvm-project.git 3b5b5c1ec4a3095ab096dd780e84d7ab81f3d7ff)"
    .section	".note.GNU-stack","",@progbits
Task info: s0.t0
    Backend           : OPENCL
    Device            : RefSi G1 RV64 CL_DEVICE_TYPE_ACCELERATOR (available)
    Dims              : 1
    Global work offset: [0]
    Global work size  : [8]
    Local  work size  : [8, 1, 1]
    Number of workgroups  : [1]
```

**This is the RISC-V code for our example**, that was executed with the RefSi RISC-V Simulator from Codeplay. However, if we pay attention, there are no RISC-V Vector (RVV) instructions being generated. This is because we need to export the following variable with the vector width:

```bash
export CA_RISCV_VF=4
```

If we run TornadoVM again with the OCK debug information on, we obtain the following:

```asm
tornado --jvm="-Ds0.t0.device=0:2" --threadInfo -m tornado.examples/uk.ac.manchester.tornado.examples.arrays.ArrayAddInt 

[ ASSEMBLY code … ]
.LBB0_18:
    add	a0, s5, s8
    add	a0, a0, s4
    vsetvli	zero, zero, e64, m2, ta, ma
    vadd.vx	v10, v8, a0
    vsetvli	zero, zero, e32, m1, ta, ma
    vnsrl.wi	v12, v10, 0
    vmsle.vi	v0, v12, 7
    vsetvli	zero, zero, e8, mf4, ta, ma
    vmv.x.s	a0, v0
    andi	a0, a0, 15
    beqz	a0, .LBB0_17
    slli	a3, s7, 2
    add	a2, t3, a3
    add	a0, t4, a3
    add	a3, a3, t6
    vsetvli	zero, zero, e64, m2, ta, ma
    vsll.vx	v10, v10, s2
    vsra.vx	v10, v10, s2
.LBB0_20:
    vsetvli	zero, zero, e32, m1, ta, ma
    vle32.v	v12, (a3), v0.t
    vle32.v	v13, (a0), v0.t
    vadd.vv	v12, v13, v12
    vse32.v	v12, (a2), v0.t
    vsetvli	zero, zero, e64, m2, ta, ma
    vadd.vx	v10, v10, s9
    vmsle.vi	v12, v10, 7
    vmand.mm	v0, v12, v0
    vsetvli	zero, zero, e8, mf4, ta, ma
    vmv.x.s	a4, v0
    andi	a4, a4, 15
    add	a2, a2, a5
    add	a0, a0, a5
    add	a3, a3, a5
    bnez	a4, .LBB0_20
    j	.LBB0_17
.Lfunc_end0:
    .size	"__vecz_v4_add.mux-kernel-wrapper", .Lfunc_end0-"__vecz_v4_add.mux-kernel-wrapper"

    .type	notes_global,@object
    .section	notes,"a",@progbits
    .globl	notes_global
notes_global:
    .asciz	
               [ … ] 
    .size	notes_global, 200
    .ident	"clang version 18.1.8 (https://github.com/llvm/llvm-project.git 3b5b5c1ec4a3095ab096dd780e84d7ab81f3d7ff)"
    .section	".note.GNU-stack","",@progbits
Task info: s0.t0
    Backend           : OPENCL
    Device            : RefSi G1 RV64 CL_DEVICE_TYPE_ACCELERATOR (available)
    Dims              : 1
    Global work offset: [0]
    Global work size  : [8]
    Local  work size  : [8, 1, 1]
    Number of workgroups  : [1]
```

And, if we export the following variable:

```
export SPIKE_SIM_DEBUG=1
```

We can even run step by step with a debugger that is included within the RefSi Simulator. How cool is this?

### Running with the SPIR-V Backend on RISC-V Devices

TornadoVM, as in version 1.0.7, supports three different backends: OpenCL, NVIDIA PTX and SPIR-V. When using OCK, we can program devices in OpenCL C, and SPIR-V. So far, we have only used the SPIR-V backend, so how do we also enable the SPIR-V backend? What we need to do is to reconfigure TornadoVM to also use SPIR-V devices.

```bash
./bin/tornadovm-installer --jdk jdk21 --backend=opencl,spirv
source setvars.sh
```

Then, we can query all accelerators that TornadoVM can run with the following command:

```bash
tornado --devices
```

And we get the following output:

```
Number of Tornado drivers: 2
Driver: SPIR-V
  Total number of SPIR-V devices  : 2
  Tornado device=0:0  (DEFAULT)
    SPIRV -- SPIRV OCL - Intel(R) UHD Graphics 770
        Global Memory Size: 28.8 GB
        Local Memory Size: 64.0 KB
        Workgroup Dimensions: 3
        Total Number of Block Threads: [512]
        Max WorkGroup Configuration: [512, 512, 512]
        Device OpenCL C version: OpenCL C 1.2

  Tornado device=0:1
    SPIRV -- SPIRV LevelZero - Intel(R) UHD Graphics 770
        Global Memory Size: 28.8 GB
        Local Memory Size: 64.0 KB
        Workgroup Dimensions: 3
        Total Number of Block Threads: [512]
        Max WorkGroup Configuration: [512, 512, 512]
        Device OpenCL C version:  (LEVEL ZERO) 1.3

Driver: OpenCL
  Total number of OpenCL devices  : 3
  Tornado device=1:0
    OPENCL --  [NVIDIA CUDA] -- NVIDIA GeForce RTX 3070
        Global Memory Size: 7.8 GB
        Local Memory Size: 48.0 KB
        Workgroup Dimensions: 3
        Total Number of Block Threads: [1024]
        Max WorkGroup Configuration: [1024, 1024, 64]
        Device OpenCL C version: OpenCL C 1.2

  Tornado device=1:1
    OPENCL --  [Intel(R) OpenCL Graphics] -- Intel(R) UHD Graphics 770
        Global Memory Size: 28.8 GB
        Local Memory Size: 64.0 KB
        Workgroup Dimensions: 3
        Total Number of Block Threads: [512]
        Max WorkGroup Configuration: [512, 512, 512]
        Device OpenCL C version: OpenCL C 1.2

  Tornado device=1:2
    OPENCL --  [ComputeAorta] -- RefSi G1 RV64
        Global Memory Size: 2.0 GB
        Local Memory Size: 256.0 KB
        Workgroup Dimensions: 3
        Total Number of Block Threads: [1024]
        Max WorkGroup Configuration: [1024, 1024, 1024]
        Device OpenCL C version: OpenCL C 1.2 Clang 18.1.8
```

```

```

The first two devices correspond to devices that can be used to run SPIR-V code. But wait, there is no RISC-V (RefSi) device. Why is this?

RefSi supports SPIR-V 1.0, but TornadoVM requires, at least, SPIR-V 1.2. We can force this by invoking TornadoVM with the following JVM flag:

```bash
tornado --jvm="-Dtornado.spirv.version=1.0" --devices
```

And we will get the following output:

```
Number of Tornado drivers: 2
Driver: SPIR-V
  Total number of SPIR-V devices  : 3
  Tornado device=0:0  (DEFAULT)
    SPIRV -- SPIRV OCL - Intel(R) UHD Graphics 770
        Global Memory Size: 28.8 GB
        Local Memory Size: 64.0 KB
        Workgroup Dimensions: 3
        Total Number of Block Threads: [512]
        Max WorkGroup Configuration: [512, 512, 512]
        Device OpenCL C version: OpenCL C 1.2

  Tornado device=0:1
    SPIRV -- SPIRV OCL - RefSi G1 RV64
        Global Memory Size: 2.0 GB
        Local Memory Size: 256.0 KB
        Workgroup Dimensions: 3
        Total Number of Block Threads: [1024]
        Max WorkGroup Configuration: [1024, 1024, 1024]
        Device OpenCL C version: OpenCL C 1.2 Clang 18.1.8

  Tornado device=0:2
    SPIRV -- SPIRV LevelZero - Intel(R) UHD Graphics 770
        Global Memory Size: 28.8 GB
        Local Memory Size: 64.0 KB
        Workgroup Dimensions: 3
        Total Number of Block Threads: [512]
        Max WorkGroup Configuration: [512, 512, 512]
        Device OpenCL C version:  (LEVEL ZERO) 1.3

Driver: OpenCL
  Total number of OpenCL devices  : 3
  Tornado device=1:0
    OPENCL --  [NVIDIA CUDA] -- NVIDIA GeForce RTX 3070
        Global Memory Size: 7.8 GB
        Local Memory Size: 48.0 KB
        Workgroup Dimensions: 3
        Total Number of Block Threads: [1024]
        Max WorkGroup Configuration: [1024, 1024, 64]
        Device OpenCL C version: OpenCL C 1.2

  Tornado device=1:1
    OPENCL --  [Intel(R) OpenCL Graphics] -- Intel(R) UHD Graphics 770
        Global Memory Size: 28.8 GB
        Local Memory Size: 64.0 KB
        Workgroup Dimensions: 3
        Total Number of Block Threads: [512]
        Max WorkGroup Configuration: [512, 512, 512]
        Device OpenCL C version: OpenCL C 1.2

  Tornado device=1:2
    OPENCL --  [ComputeAorta] -- RefSi G1 RV64
        Global Memory Size: 2.0 GB
        Local Memory Size: 256.0 KB
        Workgroup Dimensions: 3
        Total Number of Block Threads: [1024]
        Max WorkGroup Configuration: [1024, 1024, 1024]
        Device OpenCL C version: OpenCL C 1.2 Clang 18.1.8
```

```

```

And we can use this SPIR-V backend and device to run our Java programs with TornadoVM:

```bash
tornado --jvm="-Dtornado.spirv.version=1.0 -Ds0.t0.device=0:1" --threadInfo -m tornado.examples/uk.ac.manchester.tornado.examples.arrays.ArrayAddInt
```

```asm
[ ASSEMBLY CODE  … ]
.LBB0_20:
    slli	s1, a5, 2
    add	a2, s5, s1
    vle32.v	v10, (a2), v0.t
    add	a2, s0, s1
    vle32.v	v11, (a2), v0.t
    add	s1, s1, a0
    vsetvli	zero, zero, e32, m1, ta, ma
    vadd.vv	v10, v11, v10
    vse32.v	v10, (s1), v0.t
    vadd.vx	v9, v9, a4
    vmsle.vi	v10, v9, 7
    vmand.mm	v0, v10, v0
    vsetvli	zero, zero, e8, mf4, ta, ma
    vmv.x.s	a2, v0
    andi	a2, a2, 15
    addw	a5, a5, a4
    bnez	a2, .LBB0_20
[ ASSEMBLY CODE  … ]

Task info: s0.t0
    Backend           : SPIRV
    Device            : SPIRV OCL - RefSi G1 RV64 FPGA
    Dims              : 1
    Global work offset: [0]
    Global work size  : [8]
    Local  work size  : [8, 1, 1]
    Number of workgroups  : [1]
```

## Conclusions

Hardware specialisation is now everywhere and modern computing systems contain a wide range of processors for computing specialised tasks more efficiently, such as processors for AI and deep learning.

However, we also need to program and access new hardware accelerators and exploit their capabilities efficiently. This post has explored a way to do so by using the oneAPI Construction Kit as a driver for the TornadoVM project. This allows Java developers to access modern hardware with no changes to their existing Java/TornadoVM applications.

This post just scratches the surface of the possibilities of execution and optimization of Java programs on modern hardware. I have written [a more detailed blogpost](https://jjfumero.github.io/posts/2024/09/10/tornadovm-ock "a more detailed blogpost") in which I also compared OCK on Intel and ARM CPUs with Java threads, and shows the potential for these types of solutions to run on modern and parallel CPUs.

## Acknowledgments

I would like to thank [Colin Davidson](https://www.linkedin.com/in/colin-davidson-6a4b042/ "Colin Davidson") from [Codeplay](https://codeplay.com/ "Codeplay") for the support regarding the oneAPI Construction Kit for TornadoVM.
