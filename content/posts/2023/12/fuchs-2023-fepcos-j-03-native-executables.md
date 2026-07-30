---
title: "FEPCOS-J: Native executables of Java-coded networked systems"
slug: "fuchs-2023-fepcos-j-03-native-executables"
date: "2023-12-13T12:13:07+00:00"
lastmod: "2023-12-13T12:14:17+00:00"
description: "An example shows how Java developers can build native executables of declaratively composed networked systems by using FEPCOS-J and GraalVM."
authors:
  - "gerhard-fuchs"
image: "/images/posts/2023/12/fuchs-2023-fepcos-j-03-native-executables/fuchs2023-fepcos-j-modular-design.png"
categories:
  - "Developer Tools"
  - "Java"
  - "Use Cases"
tags:
related_posts:
  - "native-image-micronaut"
  - "native-spring-boot"
  - "fuchs-2023-fepcos-j-02"
  - "fuchs-2024-fepcos-j-multithreaded-server"
enlighterjs: true
frozen: false
---

**FEPCOS-J prototypically enables Java developers to realize networked systems without low-level network programming and to automatically build native executables by means of GraalVM. **This post introduces the concept and gives you an example by rebuilding the source code explained in my previous posts.****

**Please help me to make FEPCOS-J a Free/Libre and Open-Source Software (FLOSS).**

Introduction {#h2-0-introduction}
---------------------------------

**FEPCOS-J** [\[1\]](#references) enables a Java developer to declaratively program networked systems and compose them like building blocks [\[2\]](#references). For this purpose, FEPCOS-J provides Java modules and generates code to free a Java developer from network programming [\[3\]](#references).

In brief, the developer specifies a system by using the annotations FEPCOS-J offers. Afterwards, the developer uses the FEPCOS-J Processor ***fjp*** to process the specification, generate code, and build two modular Jar files: a system export module and a system import module.

To explain, combined with the FEPCOS-J Exporter ***fjx***, the system export module is the Java application of a multithreaded server that provides access to the specified system via an IPv4 network. Further, Java-coded system users can both blockingly and concurrently access the system via the IPv4 network by using the system import module.

**GraalVM** is another technology that processes Java applications to build standalone binaries that are smaller, start up faster, offer peak performance without a warmup, and use less memory and CPU compared to applications running on a Java Virtual Machine [\[4\]](#references).

For this purpose, GraalVM provides the ***native-image*** tool [\[5\]](#references). In particular, ***native-image*** can convert modularized Java applications into native executables when invoked with the correct parameters tailored to the application.

This post explains the **prototypical interaction of FEPCOS-J and GraalVM**, which enables Java developers to automatically build native executables of declaratively programmed networked systems.

It introduces how [FEPCOS-J uses GraalVM to build native executables](#concept) and provides an [example](#example) afterwards. Finally, the post [evaluates the build process](https://foojay.io/wp-admin/post.php?post=102871&action=edit#evaluation) and draws a [conclusion](https://foojay.io/wp-admin/post.php?post=102871&action=edit#conclusion).

I would also like to ask you to [help me to make FEPCOS-J a Free/Libre and Open-Source Software (FLOSS)](#help).

FEPCOS-J builds native executables by using GraalVM {#concept}
--------------------------------------------------------------

In its current prototypical state, the FEPCOS-J processor ***fjp*** accepts the parameter `--native`, as follows:

`fjp --native`

This command causes ***fjp*** to build the default output and a native executable of the system export module by invoking ***native-image*** with application-tailored parameters afterwards.

As a result, this command builds a native executable of a multithreaded server, which provides access to the capabilities of a declaratively programmed system via an IPv4 network.

To point out, the developer does not have to care about the network programming, as FEPCOS-J provides Java modules and generates additional required code for him.

The remaining section delineates the interaction between FEPCOS-J and GraalVM using the OpenJDK-based Community Edition for JDK 17 [\[6\]](#references).

### Building native executables with GraalVM {#h3-2-building-native-executables-with-graalvm}

There are various ways to build native executables with GraalVM, in particular by using the following command:

`native-image -o <name> -p <module_path> -m <module>/<main_class>`

To explain, `-o <name>` specifies the name of the native executable, and `-p <module_path>` specifies the module path, which must contain all modules of the application to be built.

Further, `-m <module>/<main_class>` specifies the module and the class, which contains the `main(...)` method.

To sum up, ***native-image*** can build native executables with a definable name out of a Java module, provided that the *name* , the *module path* , and the *main class* are properly specified as parameters.

### The modular design of FEPCOS-J allows the specification of the required *native-image* parameters {#h3-3-the-modular-design-of-fepcos-j-allows-the-specification-of-the-required-native-image-parameters}

In order to set the above-mentioned parameters of ***native-image*** , it is necessary to know the required modules and module paths of a networked system developed with FEPCOS-J. Further, it is necessary to know its *main class*.

In brief, assuming a system *sys* is implemented in the project directory *${PROJ}*, then

* the module path:
  * must include *${FEPCOS_HOME}/mlib* with modules FEPCOS-J provides as modular Jar files;
  * must include *${PROJ}/tgt* , the ***fjp*** -generated directory, which in particular contains the system export module *sys.exp.jar*;
  * may include *${PROJ}/mlib* , which must contain all optional system-specific modular Jar files required to build the system export module *sys.exp.jar* and run ***fjx***.
* the main class is the ***fjp*** -generated class *sys.exp/sys.exp.Main* packed in *sys.exp.jar*.

**Fig. 1** and **Tabs. 1-3** provide detailed information: The figure depicts the module dependencies and module paths, and the tables explain the involved modules.
![Module dependencies of networked systems created with FEPCOS-J: The realization of a system sys within a project directory ${PROJ} and its subdirectories src, mlib, and tgt requires modules FEPCOS-J provides in the directory ${FEPCOS_HOME}/mlib. It is not shown that all modules require java.base. This is taken for granted.](/images/posts/2023/12/fuchs-2023-fepcos-j-03-native-executables/fuchs2023-fepcos-j-modular-design.png) **Fig. 1) Module dependencies of networked systems created with FEPCOS-J:** The realization of a system *sys* within a project directory *${PROJ}* and its subdirectories *src* , *mlib* , and *tgt* requires modules FEPCOS-J provides in the directory *${FEPCOS_HOME}/mlib* . It is taken for granted that all modules require *java.base*, thus not shown.

#### Modules FEPCOS-J provides

FEPCOS-J provides the following modules as modular Jar files in the directory *${FEPCOS_HOME}/mlib*.

|    Module Name     |                                                                                                                               Explanation                                                                                                                               |
|--------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| fepcos.annotations | This module contains Java annotations that allow Java developers to declaratively compose networked systems. It is solely required for the programming and processing of the system specification. Thus, the module is statically required.                             |
| fepcos.exp         | This module contains the Java classes required by a system export module to provide access to a system within an IPv4 network. In particular, it contains the generic part of the FEPCOS-J Exporter ***fjx***, which is essentially a lightweight multithreaded server. |
| fepcos.imp         | This module contains an abstract system interface required by a system import module to access a system via an IPv4 network.                                                                                                                                            |
| fepcos.core        | This module contains the core aspects of FEPCOS-J, in particular the network protocol.                                                                                                                                                                                  |

**Tab. 1) Modules FEPCOS-J provides.**

#### Modules the Java developer provides

A Java developer programs the system specification *sys.spec* within the directory *${PROJ}/src* . Further, the optional directory *${PROJ}/mlib* may contain modular jar files of system dependencies *sys-deps*.

| Module Name |                                                                                                                                   Explanation                                                                                                                                   |
|-------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| sys.spec    | This module contains the system specification of a system called *sys* . In brief, it contains the Java-code programmed by the developer, as explained in my previous posts [\[2,3\]](#references).                                                                             |
| sys-deps    | The system dependencies are an optional collection of modular Jar files. They contain every module FEPCOS-J does not provide but needs to build and execute *sys.spec* . It is important to realize that if *sys.spec* requires *sys-deps* , then *sys.exp* requires them, too. |

**Tab. 2) Modules the Java developer provides.**

#### Modules generated by *fjp*

The FEPCOS-J Processor ***fjp*** generates the following modules and stores them as modular Jar files in the directory *${PROJ}/tgt*.

| Module Name |                                                                                                                                                                                                                                       Explanation                                                                                                                                                                                                                                       |
|-------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| sys.exp     | This module is the ***fjp*** -generated system export module. It includes all system-specific Java classes that provide access to the specified system via an IPv4 network. In particular, it contains the generated class **sys.exp.Main** that has a `main(...)` method, which starts the correctly parametrized FEPCOS-J Exporter. For the sake of completeness, ***fjx*** is a start script that calls ***java*** , specifying the module path and the main class **sys.exp.Main**. |
| sys.imp     | This module is the ***fjp***-generated system import module. It contains a generated system interface that allows users to access the specified system both blockingly and concurrently.                                                                                                                                                                                                                                                                                                |

**Tab. 3) Modules generated by fjp.**

### Interaction of FEPCOS-J and GraalVM to build native executables {#h3-4-interaction-of-fepcos-j-and-graalvm-to-build-native-executables}

#### Build native executables manually

Combining the requirements of ***native-image*** with the properties of FEPCOS-J leads to the following commands to be executed within ${PROJ}:

Firstly,

`fjp`

generates the system import module and the system export module out of the system specification.

Secondly,

`MP=${FEPCOS_HOME}/mlib:tgt[:mlib]`

specifies the module path *${MP}* . The directory *mlib* is optional and is only appended to the module path if the system specification requires further modules. Finally,

`native-image -o sys.exp -p ${MP} -m sys.exp/sys.exp.Main`

generates the native executable *sys.exp* out of the system export module. So, the native executable and the system export module have the same name.

#### Build native executables automatically

At this point, it is important to realize that ***fjp*** is a start script that can check if the *--native* parameter is set. Thus, it is possible to automate the manual build process as follows:

Firstly, ***fjp*** calls ***java*** with the correct parameters so that it runs the FEPCOS-J Processor. If the *--native* parameter is set, ***fjp*** runs ***native-image***, as explained above, afterwards.

Exemplary usage of FEPCOS-J to build native executables for Java-coded networked systems {#example}
---------------------------------------------------------------------------------------------------

My previous posts [\[2,3\]](#references#) explain how to use FEPCOS-J to declaratively program networked systems and how to compose them like building blocks.

This section takes up the example shown there and illustrates its realization with native executables. For a detailed description of the source code and development workflow, see my previous posts.

### Example scenario {#scenario}

The following recaps the example scenario and provides the names of the involved computers and native executables, in addition. **Fig. 2** provides an illustration.  
![Example scenario realized by using FEPCOS-J: Four computers named adm, box, lumo, and trike native executable applications communicating via two different IPv4 networks, 10.0.0.0/24 and 10.1.0.0/24. In detail, adm runs app; box runs calculator.exp; lumo runs adder.exp; and trike runs multiplier.exp. Further, app accesses calculator.exp via internet socket 10.0.0.1:8001, and calculator.exp accesses adder.exp via 10.1.0.1:8001 and multiplier.exp via 10.1.0.2:8001.](/images/posts/2023/12/fuchs-2023-fepcos-j-03-native-executables/fuchs2023-fepcos-j-realized-native-image-scenario.png) **Fig. 2) Example scenario realized by using FEPCOS-J:** The computers named *adm* , *box* , *lumo* , and *trike* run the native executables *app* , *calculator.exp* , *adder.exp* , and *multiplier.exp*, as noted under their names.

Four computers named *lumo* , *trike* , *box* and *adm* communicate via the two IPv4 networks, 10.0.0.0/24 and 10.1.0.0/24, to run native executable applications:

* Firstly, *lumo* runs *adder.exp*, which provides the capability to add two numbers via internet socket 10.1.0.1:8001.
* Secondly, *trike* runs *multiplier.exp*, which provides the capability to multiply two numbers via internet socket 10.1.0.2:8001.
* Thirdly, *box* runs *calculator.exp* , which provides both the capability to add and multiply via internet socket 10.0.0.1:8001. To clarify, *calculator.exp* delegates requests to *adder.exp* or *multiplier.exp*, respectively, via the IPv4 network 10.1.0.0/24.
* Finally, *adm* runs *app* , which accesses the capabilities of *calculator.exp* via the IPv4 network 10.0.0.0/24.

To sum up, *lumo* and *trike* act as multithreaded servers, *adm* acts as a client, and *box* acts as both a multithreaded server and a client.

### Build and run the example scenario {#h3-7-build-and-run-the-example-scenario}

My last post [\[2\]](#references) explains in detail how to use FEPCOS-J to realize the example scenario by building Java applications. The following describes how to rebuild the scenario with native executables and run them.

In short, the development workflow is almost identical, solely differing in that:

* the calling of `fjp --native` substitutes the calling of `fjp`;
* the running of `fjx` with the generated system export module is replaced by the running of the generated native executable;
* a native executable of the system user *app* is manually built and started.

**Figs. 3-6** depict screenshots of the native builds, including details. You will see that the outputs of `fjp --native` and `native-image` are piped to a file for evaluation afterwards.

#### Build and run *adder.exp* {#building}

<figure class="wp-block-image size-full is-resized">
 <img decoding="async" width="1024" height="512" src="/images/posts/2023/12/fuchs-2023-fepcos-j-03-native-executables/fuchs2023-screenshot-fepcos-j-native-build-adder.png" alt="Running the native executable adder.exp built by using FEPCOS-J: Calling fjp --native within the project directory causes the execution of native-image, which generates the native executable adder.exp. Further, the file adder.out contains the piped output for subsequent evaluation. Running adder.exp provides access to the system internet socket 10.1.0.1:8001, which is specified as a parameter." class="wp-image-102878" style="aspect-ratio:2;width:840px;height:auto">
 <figcaption class="wp-element-caption">
  <strong>Fig. 3) Running the native executable <em>adder.exp</em> built by using FEPCOS-J:</strong> Calling <em>fjp --native</em> within the project directory <strong>(1)</strong> causes the execution of <em>native-image</em>, which generates the native executable <em>adder.exp</em> <strong>(2)</strong>. Further, the file <em>adder.out</em> contains the piped output for subsequent evaluation. Running <em>adder.exp</em> <strong>(3)</strong> provides access to the system internet socket 10.1.0.1:8001, which is specified as a parameter.
 </figcaption>
</figure>

#### Build and run *multiplier.exp*

![Running the native executable multiplier.exp built by using FEPCOS-J: Calling fjp --native within the project directory causes the execution of native-image, which generates the native executable multiplier.exp. Further, the file multiplier.out contains the piped output for subsequent evaluation. Running multiplier.exp provides access to the system internet socket 10.1.0.2:8001, which is specified as a parameter.](/images/posts/2023/12/fuchs-2023-fepcos-j-03-native-executables/fuchs2023-screenshot-fepcos-j-native-build-multiplier.png) **Fig. 4) Running the native executable *multiplier.exp* built by using FEPCOS-J:** Calling *fjp --native* within the project directory **(1)** causes the execution of *native-image* , which generates the native executable *multiplier.exp* **(2)** . Further, the file *multiplier.out* contains the piped output for subsequent evaluation. Running *multiplier.exp* **(3)** provides access to the system internet socket 10.1.0.2:8001, which is specified as a parameter.

#### Build and run *calculator.exp*

![Running the native executable calculator.exp built by using FEPCOS-J: Firstly, scp copies adder.imp.jar and multiplier.imp.jar, which are the previously built system import modules of calculator's parts, into the subdirectory mlib. Calling fjp --native within the project directory causes the execution of native-image, which generates the native executable calculator.exp. Further, the file calculator.out contains the piped output for subsequent evaluation. Running calculator.exp provides access to the system internet socket 10.0.0.1:8001, which is specified as a parameter.](/images/posts/2023/12/fuchs-2023-fepcos-j-03-native-executables/fuchs2023-screenshot-fepcos-j-native-build-calculator.png) **Fig. 5) Running the native executable *calculator.exp* built by using FEPCOS-J:** Firstly, *scp* copies *adder.imp.jar* and *multiplier.imp.jar* , which are the previously built system import modules of *calculator* 's parts, into the subdirectory *mlib* . Calling *fjp --native* within the project directory **(1)** causes the execution of *native-image* , which generates the native executable *calculator.exp* **(2)** . Further, the file *calculator.out* contains the piped output for subsequent evaluation. Running *calculator.exp* **(3)** provides access to the system internet socket 10.0.0.1:8001, which is specified as a parameter.

#### Build and run *app*

![Build and run the system user app: Firstly, scp copies the system import module calculator.imp.jar into the subdirectory mlib. After compiling the application with javac, native-image processes the class files. As a result, app is the generated native executable. Further, the file app.out contains the piped output for subsequent evaluation. Running app causes the expected behavior.](/images/posts/2023/12/fuchs-2023-fepcos-j-03-native-executables/fuchs2023-screenshot-fepcos-j-native-build-app.png) **Fig. 6) Build and run the system user *app*:** Firstly, *scp* copies the system import module *calculator.imp.jar* into the subdirectory *mlib* . After compiling the application with ***javac*** , ***native-image*** processes the class files **(1)** . As a result, *app* is the generated native executable **(2)** . Further, the file *app.out* contains the piped output for subsequent evaluation. Running *app* **(3)** causes the expected behavior.

Evaluation {#evaluation}
------------------------

This section evaluates the exemplary usage of FEPCOS-J to build native executables of Java-coded networked systems based on the above-mentioned and below-listed output files *adder.out* , *multiplier.out* , *calculator.out* , and *app.out*.

### Raw outputs {#h3-9-raw-outputs}

As can be seen below, all output files solely contain ***native-image*** build output [\[7\]](#references).

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="adder.out" data-enlighter-group="native.out">========================================================================================================================
GraalVM Native Image: Generating 'adder.exp' (executable)...
========================================================================================================================
For detailed information and explanations on the build output, visit:
https://github.com/oracle/graal/blob/master/docs/reference-manual/native-image/BuildOutput.md
------------------------------------------------------------------------------------------------------------------------
[1/8] Initializing...                                                                                   (44,3s @ 0,07GB)
 Java version: 17.0.8+7, vendor version: GraalVM CE 17.0.8+7.1
 Graal compiler: optimization level: 2, target machine: armv8-a
 C compiler: gcc (linux, aarch64, 10.2.1)
 Garbage collector: Serial GC (max heap size: 80% of RAM)
[2/8] Performing analysis...  [***]                                                                    (227,8s @ 0,28GB)
   2.996 (72,45%) of  4.135 types reachable
   3.662 (52,01%) of  7.041 fields reachable
  13.633 (44,38%) of 30.717 methods reachable
     931 types,     0 fields, and   352 methods registered for reflection
      59 types,    58 fields, and    53 methods registered for JNI access
       4 native libraries: dl, pthread, rt, z
[3/8] Building universe...                                                                              (35,3s @ 0,34GB)
[4/8] Parsing methods...      [*****]                                                                   (21,5s @ 0,31GB)
[5/8] Inlining methods...     [***]                                                                     (18,2s @ 0,20GB)
[6/8] Compiling methods...    [**************]                                                         (205,4s @ 0,32GB)
[7/8] Layouting methods...    [****]                                                                    (12,6s @ 0,34GB)
[8/8] Creating image...       [***********]                                                            (130,7s @ 0,31GB)
   4,41MB (36,62%) for code area:     7.757 compilation units
   7,06MB (58,68%) for image heap:   90.061 objects and 5 resources
 579,61kB ( 4,70%) for other data
  12,04MB in total
------------------------------------------------------------------------------------------------------------------------
Top 10 origins of code area:                                Top 10 object types in image heap:
   3,35MB java.base                                            1,01MB byte[] for code metadata
 786,86kB svm.jar (Native Image)                             902,53kB java.lang.String
 108,84kB java.logging                                       875,48kB byte[] for general heap data
  55,80kB org.graalvm.nativeimage.base                       692,41kB java.lang.Class
  27,03kB fepcos.exp                                         673,79kB byte[] for java.lang.String
  23,27kB org.graalvm.sdk                                    348,66kB java.util.HashMap$Node
  18,14kB jdk.internal.vm.ci                                 257,47kB com.oracle.svm.core.hub.DynamicHubCompanion
   6,31kB jdk.internal.vm.compiler                           170,81kB java.lang.String[]
   2,56kB fepcos.core                                        162,27kB java.lang.Object[]
   1,86kB jdk.proxy1                                         148,84kB byte[] for embedded resources
   4,75kB for 4 more packages                                  1,19MB for 843 more object types
------------------------------------------------------------------------------------------------------------------------
Recommendations:
 HEAP: Set max heap for improved and more predictable memory usage.
 CPU:  Enable more CPU features with '-march=native' for improved performance.
------------------------------------------------------------------------------------------------------------------------
                       84,5s (11,9% of total time) in 169 GCs | Peak RSS: 0,79GB | CPU load: 3,00
------------------------------------------------------------------------------------------------------------------------
Produced artifacts:
 /home/fuchs/demo/adder/adder.exp (executable)
========================================================================================================================
Finished generating 'adder.exp' in 11m 41s.</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="multiplier.out" data-enlighter-group="native.out">========================================================================================================================
GraalVM Native Image: Generating 'multiplier.exp' (executable)...
========================================================================================================================
For detailed information and explanations on the build output, visit:
https://github.com/oracle/graal/blob/master/docs/reference-manual/native-image/BuildOutput.md
------------------------------------------------------------------------------------------------------------------------
[1/8] Initializing...                                                                                   (69,4s @ 0,09GB)
 Java version: 17.0.8+7, vendor version: GraalVM CE 17.0.8+7.1
 Graal compiler: optimization level: 2, target machine: armv8-a
 C compiler: gcc (linux, aarch64, 10.2.1)
 Garbage collector: Serial GC (max heap size: 80% of RAM)
[2/8] Performing analysis...  [***]                                                                    (347,5s @ 0,30GB)
   2.996 (72,45%) of  4.135 types reachable
   3.662 (52,01%) of  7.041 fields reachable
  13.633 (44,39%) of 30.715 methods reachable
     931 types,     0 fields, and   352 methods registered for reflection
      59 types,    58 fields, and    53 methods registered for JNI access
       4 native libraries: dl, pthread, rt, z
[3/8] Building universe...                                                                              (46,0s @ 0,27GB)
[4/8] Parsing methods...      [******]                                                                  (41,6s @ 0,27GB)
[5/8] Inlining methods...     [***]                                                                     (23,1s @ 0,30GB)
[6/8] Compiling methods...    [*****************]                                                      (311,4s @ 0,38GB)
[7/8] Layouting methods...    [*********]                                                               (80,1s @ 0,36GB)
[8/8] Creating image...       [********]                                                                (65,9s @ 0,36GB)
   4,41MB (36,62%) for code area:     7.756 compilation units
   7,06MB (58,68%) for image heap:   90.078 objects and 5 resources
 579,83kB ( 4,70%) for other data
  12,04MB in total
------------------------------------------------------------------------------------------------------------------------
Top 10 origins of code area:                                Top 10 object types in image heap:
   3,35MB java.base                                            1,01MB byte[] for code metadata
 786,86kB svm.jar (Native Image)                             902,56kB java.lang.String
 108,84kB java.logging                                       875,48kB byte[] for general heap data
  55,80kB org.graalvm.nativeimage.base                       692,41kB java.lang.Class
  26,98kB fepcos.exp                                         673,87kB byte[] for java.lang.String
  23,27kB org.graalvm.sdk                                    349,41kB java.util.HashMap$Node
  18,14kB jdk.internal.vm.ci                                 257,47kB com.oracle.svm.core.hub.DynamicHubCompanion
   6,31kB jdk.internal.vm.compiler                           170,82kB java.lang.String[]
   2,56kB fepcos.core                                        162,27kB java.lang.Object[]
   1,86kB jdk.proxy1                                         148,84kB byte[] for embedded resources
   4,77kB for 4 more packages                                  1,19MB for 841 more object types
------------------------------------------------------------------------------------------------------------------------
Recommendations:
 HEAP: Set max heap for improved and more predictable memory usage.
 CPU:  Enable more CPU features with '-march=native' for improved performance.
------------------------------------------------------------------------------------------------------------------------
                       87,5s (8,7% of total time) in 157 GCs | Peak RSS: 0,75GB | CPU load: 3,18
------------------------------------------------------------------------------------------------------------------------
Produced artifacts:
 /home/fuchs/demo/multiplier/multiplier.exp (executable)
========================================================================================================================
Finished generating 'multiplier.exp' in 16m 32s.</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="calculator.out" data-enlighter-group="native.out">========================================================================================================================
GraalVM Native Image: Generating 'calculator.exp' (executable)...
========================================================================================================================
For detailed information and explanations on the build output, visit:
https://github.com/oracle/graal/blob/master/docs/reference-manual/native-image/BuildOutput.md
------------------------------------------------------------------------------------------------------------------------
Warning: The host machine does not support all features of 'x86-64-v3'. Falling back to '-march=compatibility' for best compatibility.
[1/8] Initializing...                                                                                   (13,8s @ 0,13GB)
 Java version: 17.0.8+7, vendor version: GraalVM CE 17.0.8+7.1
 Graal compiler: optimization level: 2, target machine: compatibility
 C compiler: gcc (linux, x86_64, 10.2.1)
 Garbage collector: Serial GC (max heap size: 80% of RAM)
[2/8] Performing analysis...  [***]                                                                     (54,9s @ 0,53GB)
   3.053 (72,73%) of  4.198 types reachable
   3.781 (52,17%) of  7.247 fields reachable
  13.920 (44,75%) of 31.109 methods reachable
     942 types,     0 fields, and   360 methods registered for reflection
      61 types,    61 fields, and    55 methods registered for JNI access
       4 native libraries: dl, pthread, rt, z
[3/8] Building universe...                                                                               (8,7s @ 0,50GB)
[4/8] Parsing methods...      [***]                                                                      (5,7s @ 0,49GB)
[5/8] Inlining methods...     [***]                                                                      (4,2s @ 0,85GB)
[6/8] Compiling methods...    [*******]                                                                 (45,0s @ 0,41GB)
[7/8] Layouting methods...    [**]                                                                       (3,4s @ 0,57GB)
[8/8] Creating image...       [***]                                                                      (6,1s @ 0,72GB)
   4,72MB (37,94%) for code area:     7.965 compilation units
   7,10MB (57,03%) for image heap:   91.985 objects and 5 resources
 641,44kB ( 5,03%) for other data
  12,45MB in total
------------------------------------------------------------------------------------------------------------------------
Top 10 origins of code area:                                Top 10 object types in image heap:
   3,63MB java.base                                            1,06MB byte[] for code metadata
 777,44kB svm.jar (Native Image)                             916,22kB java.lang.String
 111,98kB java.logging                                       883,68kB byte[] for general heap data
  61,92kB org.graalvm.nativeimage.base                       706,45kB java.lang.Class
  29,95kB fepcos.exp                                         683,27kB byte[] for java.lang.String
  23,82kB jdk.internal.vm.ci                                 381,14kB java.util.HashMap$Node
  23,10kB org.graalvm.sdk                                    262,37kB com.oracle.svm.core.hub.DynamicHubCompanion
   7,93kB fepcos.imp                                         173,63kB java.lang.String[]
   6,10kB jdk.internal.vm.compiler                           162,95kB java.lang.Object[]
   5,34kB calculator.exp                                     149,16kB java.util.HashMap$Node[]
   9,29kB for 7 more packages                                  1,20MB for 854 more object types
Warning: The host machine does not support all features of 'x86-64-v3'. Falling back to '-march=compatibility' for best compatibility.
------------------------------------------------------------------------------------------------------------------------
Recommendations:
 HEAP: Set max heap for improved and more predictable memory usage.
 CPU:  Enable more CPU features with '-march=native' for improved performance.
------------------------------------------------------------------------------------------------------------------------
                        4,5s (3,1% of total time) in 41 GCs | Peak RSS: 1,36GB | CPU load: 3,61
------------------------------------------------------------------------------------------------------------------------
Produced artifacts:
 /home/fuchs/demo/calculator/calculator.exp (executable)
========================================================================================================================
Finished generating 'calculator.exp' in 2m 23s.</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="app.out" data-enlighter-group="native.out">========================================================================================================================
GraalVM Native Image: Generating 'app' (executable)...
========================================================================================================================
For detailed information and explanations on the build output, visit:
https://github.com/oracle/graal/blob/master/docs/reference-manual/native-image/BuildOutput.md
------------------------------------------------------------------------------------------------------------------------
Warning: The host machine does not support all features of 'x86-64-v3'. Falling back to '-march=compatibility' for best compatibility.
[1/8] Initializing...                                                                                   (15,6s @ 0,11GB)
 Java version: 17.0.8+7, vendor version: GraalVM CE 17.0.8+7.1
 Graal compiler: optimization level: 2, target machine: compatibility
 C compiler: gcc (linux, x86_64, 10.2.1)
 Garbage collector: Serial GC (max heap size: 80% of RAM)
[2/8] Performing analysis...  [***]                                                                     (70,6s @ 0,36GB)
   3.023 (72,51%) of  4.169 types reachable
   3.731 (51,82%) of  7.200 fields reachable
  13.821 (44,57%) of 31.009 methods reachable
     938 types,     0 fields, and   360 methods registered for reflection
      61 types,    61 fields, and    55 methods registered for JNI access
       4 native libraries: dl, pthread, rt, z
[3/8] Building universe...                                                                               (8,9s @ 0,31GB)
[4/8] Parsing methods...      [***]                                                                      (7,3s @ 0,38GB)
[5/8] Inlining methods...     [***]                                                                      (5,4s @ 0,29GB)
[6/8] Compiling methods...    [*******]                                                                 (57,5s @ 0,32GB)
[7/8] Layouting methods...    [**]                                                                       (4,1s @ 0,48GB)
[8/8] Creating image...       [***]                                                                      (7,2s @ 0,62GB)
   4,66MB (37,70%) for code area:     7.896 compilation units
   7,09MB (57,33%) for image heap:   91.022 objects and 5 resources
 629,94kB ( 4,97%) for other data
  12,37MB in total
------------------------------------------------------------------------------------------------------------------------
Top 10 origins of code area:                                Top 10 object types in image heap:
   3,61MB java.base                                            1,05MB byte[] for code metadata
 777,32kB svm.jar (Native Image)                             910,78kB java.lang.String
 111,98kB java.logging                                       872,35kB byte[] for general heap data
  61,92kB org.graalvm.nativeimage.base                       699,80kB java.lang.Class
  23,82kB jdk.internal.vm.ci                                 680,40kB byte[] for java.lang.String
  23,10kB org.graalvm.sdk                                    359,53kB java.util.HashMap$Node
   7,93kB fepcos.imp                                         259,79kB com.oracle.svm.core.hub.DynamicHubCompanion
   6,10kB jdk.internal.vm.compiler                           172,77kB java.lang.String[]
   2,44kB fepcos.core                                        162,59kB java.lang.Object[]
   2,25kB app                                                148,84kB byte[] for embedded resources
   6,18kB for 5 more packages                                  1,20MB for 850 more object types
Warning: The host machine does not support all features of 'x86-64-v3'. Falling back to '-march=compatibility' for best compatibility.
------------------------------------------------------------------------------------------------------------------------
Recommendations:
 HEAP: Set max heap for improved and more predictable memory usage.
 CPU:  Enable more CPU features with '-march=native' for improved performance.
------------------------------------------------------------------------------------------------------------------------
                        9,0s (5,0% of total time) in 64 GCs | Peak RSS: 1,16GB | CPU load: 3,64
------------------------------------------------------------------------------------------------------------------------
Produced artifacts:
 /home/fuchs/demo/app/app (executable)
========================================================================================================================
Finished generating 'app' in 2m 58s.</pre>

### Prepared data {#h3-10-prepared-data}

**Tab. 4** maps selected data from the raw outputs to the name of the executing computer. **Tab. 5** shows the technical aspects of these computers.

Further, the size of the code areas is 4,41-4,72 MiB; *fepcos.exp* contributes 26,98-29,95 kiB; *fepcos.imp* contributes 7,93 kiB; *fepcos.core* contributes 2,44-2,56 kiB; *calculator.exp* contributes 5,34 kiB; and *app* contributes 2,25 kiB.

In other words, all required FEPCOS-J modules together contribute less than 1% to the code area, and the ***fjp***-generated code even less.

| Output         | Executable     | Size / MiB | Duration | Computer |
|:---------------|:---------------|:-----------|:---------|:---------|
| adder.out      | adder.exp      | 12,04      | 11m 41s  | lumo     |
| multiplier.out | multiplier.exp | 12,04      | 16m 32s  | trike    |
| calculator.out | calculator.exp | 12,45      | 2m 23s   | box      |
| app.out        | app            | 12,37      | 2m 58s   | amd      |

**Tab. 4) Selected parameters gained from building the example scenario:** **Output** = name of the output file; **Executable** = name of the generated native executable; **Size / MiB** = size of the generated native executable in MiB = 2^20^ bytes; **Duration** = duration required by ***native-image*** to build the native executable; **Computer** = name of the executing computer.

| Computer | CPU                               | RAM  | Operating system |
|:---------|:----------------------------------|------|:-----------------|
| lumo     | 4 x 1.2GHz Broadcom BCM2837       | 1 GB | linux, aarch64   |
| trike    | 4 x 1.2GHz Broadcom BCM2837       | 1 GB | linux, aarch64   |
| box      | 4 x 1.1GHz Intel® Celeron® N3450  | 8 GB | linux, x86_64    |
| adm      | 4 x 2.16GHz Intel® Pentium® N3540 | 4 GB | linux, x86_64    |

**Tab. 5) Technical aspects of the current test environment for FEPCOS-J:** **lumo** and **trike** are each a Raspberry Pi® Model 3B; **box** is a mini PC; **adm** is a notebook.

### Lessons learned {#h3-11-lessons-learned}

The rebuild of the example scenario showed that:

* The prototype of FEPCOS-J and GraalVM can interact, such that `fjp --native` starts ***native-image*** with the correct parameters.
* It is consequently possible to use FEPCOS-J to automatically build native executables for Java-coded networked systems, in particular multithreaded servers.
* The building of the native executable on the x86_64 computers took 2m 23s or 2m 58s, respectively.
* The building of the native executable on the aarch64 computers took 11m 41s or 16m 32s, respectively.
* The size of the native executables was 12,04 -12,45 MiB.
* The size of the code areas that are part of the native executables was 4,41 - 4,72 MiB.
* Thus, the code footprint of all required FEPCOS-J modules together was less than 1% of the code area, and the ***fjp***-generated code was even less.

Conclusion {#conclusion}
------------------------

This post explained the prototypical interaction of FEPCOS-J and GraalVM. It introduced the concept and provided an example by rebuilding the scenario from my last post [\[2\]](#references) afterwards. Finally, it presented an evaluation of the GraalVM ***native-image*** outputs.

FEPCOS-J enables Java developers to declaratively program networked systems and compose them like building blocks by providing Java modules and generating code, which frees the developer from network programming [\[2,3\]](#references).

GraalVM is another technology that provides the ***native-image*** tool [\[5\]](#references) to convert Java applications into native executables.

As shown in this post, the prototype of FEPCOS-J can interact with ***native-image*** , automatically building native executables of Java-coded networked systems, in particular multithreaded servers. Further, the ***native-image*** outputs gave a first impression of the build times and sizes of the native executables.

To sum up, FEPCOS-J prototypically enables Java developers to realize networked systems without low-level network programming and to automatically build native executables by means of ***native-image***.

I need your help to make FEPCOS-J a Free/Libre and Open-Source Software (FLOSS) {#help}
---------------------------------------------------------------------------------------

Have you thought about how FEPCOS-J can support your work?

FEPCOS-J shall become a Free/Libre and Open Source Software (FLOSS).

So far, FEPCOS-J is a prototype that I have independently developed. Now, I need your support, especially regarding legal issues, the testing environment, and funding.

Please let me know what you think about it and provide me with any feedback. Detailed contact information is available on my web page [\[8\]](#references).

Thanks for reading!

References: {#references}
-------------------------

1. fepcos.info: "*FEPCOS-J* "; <http://fepcos.info/en/fepcos-j.html> (last accessed: 2023-12-13).
2. Gerhard Fuchs: "*FEPCOS-J (2) -- Declaratively compose networked systems in Java* "; <https://foojay.io/today/fuchs-2023-fepcos-j-02/> (last accessed: 2023-12-13).
3. Gerhard Fuchs: "*FEPCOS-J (1) -- Description, Impressions of Usage, Current State* "; <https://foojay.io/today/fuchs-2023-fepcos-j-01/> (last accessed: 2023-12-13).
4. Oracle and/or its affiliates: "*GraalVM Overview* "; [h](https://www.graalvm.org/latest/docs/introduction/)[ttps://www.graalvm.org/latest/docs/introduction/](https://www.graalvm.org/latest/docs/introduction/) (last accessed: 2023-12-13).
5. Oracle and/or its affiliates: "*Native Image* "; <https://www.graalvm.org/latest/reference-manual/native-image/> (last accessed: 2023-12-13).
6. Mohamed Ez-zarghili: "*GraalVM for JDK 17 Community 17.0.8* "; <https://github.com/graalvm/graalvm-ce-builds/releases/tag/jdk-17.0.8> (last accessed: 2023-12-13).
7. Oracle and/or its affiliates: "*Native Image Build Output* "; <https://www.graalvm.org/jdk17/reference-manual/native-image/overview/BuildOutput/> (last accessed: 2023-12-13).
8. fepcos.info: "Gerhard Fuchs"; <http://fepcos.info/en/fuchs.html> (last accessed: 2023-12-13).
