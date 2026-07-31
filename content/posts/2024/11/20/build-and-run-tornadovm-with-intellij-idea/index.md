---
title: "Build and Run TornadoVM with IntelliJ IDEA"
slug: "build-and-run-tornadovm-with-intellij-idea"
date: "2024-11-20T19:08:13+00:00"
lastmod: "2024-11-20T19:08:15+00:00"
description: "Learn how to build TornadoVM with IntelliJ, and run the unit-tests or other Java programs from the IDE."
authors:
  - "thanos-stratikopoulos"
image: "Run-Tests.png"
categories:
  - "IntelliJ IDEA"
  - "Tools"
  - "TornadoVM"
tags:
related_posts:
  - "foojay-podcast-17"
  - "a-flavour-of-tornadovm-on-apple-m1-pro"
  - "code-interoperability-mode-for-opencl-portability-across-various-programming-languages-with-tornadovm"
  - "tornadoinsight-compatibility-with-tornadovm-sdk-2-0-configuration-guide"
enlighterjs: true
frozen: false
---

[TornadoVM](https://www.tornadovm.org/) is an open-source Java technology that is developed to aid Java programmers in adapting their code bases for hardware acceleration.

This blog aims to explain how Java programmers can build TornadoVM with IntelliJ IDEA, and how they can run TornadoVM unit-tests or other Java programs.

*** ** * ** ***

*Prerequisites* {#h2-0-prerequisites}
-------------------------------------

This blog uses IntelliJ IDEA 2024.2.4 version and the list of required plugins are as follows:{#r55ph1440}

* Python [++plugin++](https://plugins.jetbrains.com/plugin/631-python) to be installed in IntelliJ. This plugin allows python scripting from the IDE.

{#x95ro33520}

Additionally, the following commands must be installed in your system and should be added in your PATH in order to enable IDE to use them:{#3r714132566}

* **cmake** is used to build TornadoVM.
* **pyInstaller** is used in Windows OS to build the TornadoVM executables.

{#sdjl5131471}

### a) Ensure that the commands are installed in the PATH {#h3-1-a-ensure-that-the-commands-are-installed-in-the-path}

* In macOS/Linux OS, you can open a command shell and you can verify if your system recognizes cmake, by running:

{#aolr5166025}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ which make</pre>

* In Windows OS, open your shell configuration (e.g. x64 Native Tools Command Prompt for VS 2022) and initialize the environment:

{#7mg78166105}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ cd &lt;path-to-TornadoVM-directory&gt;
$ .\bin\windowsMicrosoftStudioTools2022.cmd</pre>

You can verify that your system recognizes cmake, by running:{#oe9dj102289}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ where cmake
$ where pyInstaller</pre>

If the commands are recognized, skip the next step (Step b).

### b) Add commands in your PATH {#h3-2-b-add-commands-in-your-path}

#### i) Add cmake in macOS/Linux OS {#oxcni186765}

Assuming that you have downloaded and installed cmake in a custom directory, you can add the directory in your PATH by updating your shell configuration file.{#bbnap124702}

##### Open your shell configuration file (e.g. .bashrc, .zshrc) {#30eyr88135}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ vim ~/.zshrc  		# or ~/.bashrc depending on your shell</pre>

##### Add the following line and replace the \<custom-path\> with the path to your installation {#3r0fb92748}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ export PATH=&lt;custom-path&gt;/cmake-3.25.2-macos-universal/CMake.app/Contents/bin:$PATH</pre>

##### Save and apply the changes {#r900z99923}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ source ~/.zshrc  	# or source ~/.bashrc</pre>

#### ii) Add cmake and pyInstaller in your PATH (Windows) {#krq20102352}

You can add the variables to your PATH by searching **Edit the system environment variables** , clicking **Environment Variables...** , and editing the **PATH** with your cmake directory. The commands should have been selected to be installed as native tools when you installed Microsoft VS 2022. An example of the directories where the commands have been installed is as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ C:\Program Files\Microsoft Visual Studio\2022\Community\Common7\IDE\CommonExtensions\Microsoft\CMake\CMake\bin\
$ &lt;path-to-TornadoVM-directory&gt;\.venv\Scripts</pre>

**Note:** It is recommended to use the python interpreter under the virtual environment (.venv) as the Python SDK for your TornadoVM project, since it contains all dependent modules (i.e., PyInstaller, psutil) to build TornadoVM and run the tests from IntelliJ.

*** ** * ** ***

1. Clone \& Install TornadoVM from Shell {#h2-3-1-clone-install-tornadovm-from-shell}
-------------------------------------------------------------------------------------

To initialize IDE project files for building and running TornadoVM from IntelliJ, you must have first built TornadoVM and loaded the file with the environment variables (setvars.sh, setvars.cmd), as explained in the [++Installation page++](https://tornadovm.readthedocs.io/en/latest/installation.html#installation).{#pwbqo232319}

First you need to clone the source code from GitHub:{#srq4j260067}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ git clone https://github.com/beehive-lab/TornadoVM.git</pre>

Then, you can invoke the tornadovm-installer script which will download the dependencies and will install TornadoVM with the defined JDK and backends (e.g., opencl, ptx, spirv):{#bnas8236281}

* In macOS/Linux OS:

{#sziwp3780}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ cd TornadoVM
$ ./bin/tornadovm-installer --jdk graal-jdk-21 --backend opencl
$ source setvars.sh</pre>

* In Windows OS:

{#amxxo4451}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ cd TornadoVM
$ python -m venv .venv
$ .venv\Scripts\activate.bat
$ .\bin\windowsMicrosoftStudioTools2022.cmd
$ .\bin\tornadovm-installer --jdk graal-jdk-21 --backend opencl
$ setvars.cmd</pre>

*** ** * ** ***

2. Generate the IntelliJ Project Files {#h2-4-2-generate-the-intellij-project-files}
------------------------------------------------------------------------------------

Then you can generate the IDE project files based on your built TornadoVM instance (i.e., with the JAVA_HOME and the backends), by running:{#y050h259785}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ tornado --intellijinit
Generating &lt;path-to-tornadovm&gt;/.build/_internal_TornadoVM_Maven-cleanAndinstall.run.xml
Generating &lt;path-to-tornadovm&gt;/.build/TornadoVM-Build.run.xml
Generating &lt;path-to-tornadovm&gt;/.build/TornadoVM-Tests.run.xml
IntelIj Files Generated ............... [ok]</pre>

This command will generate three files. The first two files are used to build TornadoVM from IntelliJ, while the latter is used to run the TornadoVM unit-tests. You will be able to configure those files from IntelliJ in the next step.

*** ** * ** ***

3. Configure the generated IDE project files {#h2-5-3-configure-the-generated-ide-project-files}
------------------------------------------------------------------------------------------------

At first, you need to launch the IntelliJ application and open TornadoVM as a project. Then you can go in the menu (top bar) and navigate through Run and the Edit Configurations, to configure the build configuration file, as follows:{#7r0go280974}

**Run \> Edit Configurations \> Python \> TornadoVM-Build**  

<figure class="aligncenter size-large is-resized">
 <img fetchpriority="high" decoding="async" width="1024" height="656" src="Run-EditConfigurations-1024x656.png" alt="" class="wp-image-114760" style="width:560px;height:auto">
</figure>

At this point all fields must have been automatically populated with the correct directories and attributes. You must ensure that the Use specified interpreter points to a valid Python interpreter from your system. If not, select one and click Apply.  

<figure class="aligncenter size-large is-resized">
 <img decoding="async" width="1024" height="782" src="Configure-Build-1024x782.png" alt="" class="wp-image-114761" style="width:560px;height:auto">
</figure>

Similarly you can update the selected interpreter for the Python configuration file that is used to run the TornadoVM unit-tests:{#hx4l2378675}

**Run \> Edit Configurations \> Python \> TornadoVM-Tests**{#27v2k387006}  

<figure class="aligncenter size-large is-resized">
 <img decoding="async" width="1024" height="784" src="Configure-Tests-1024x784.png" alt="" class="wp-image-114762" style="width:566px;height:auto">
</figure>

*** ** * ** ***

4. Build TornadoVM from IntelliJ {#h2-6-4-build-tornadovm-from-intellij}
------------------------------------------------------------------------

You can select the TornadoVM-Build configuration file and run. This should build TornadoVM with the JAVA_HOME and the backends that you selected in Step 1. If you run in Windows OS, this process will also invoke the pyInstaller package to create the TornadoVM executables.

*** ** * ** ***

5. Run TornadoVM Unit-tests from IntelliJ {#h2-7-5-run-tornadovm-unit-tests-from-intellij}
------------------------------------------------------------------------------------------

You can select the TornadoVM-Tests configuration file and run. This should run a subgroup of the TornadoVM unit-tests suite on the first device that is recognized in your system. If you open to edit the configurations of this file, you will see that the default arguments contain the quickPass argument which will skip the unit-tests that perform exhaustive testing and require long time to run (\~30 minutes).{#1gxfu440346}

The outcome of running the unit-tests should be similar to this image:  

<figure class="aligncenter size-large is-resized">
 <img loading="lazy" decoding="async" width="1024" height="943" src="Run-Tests-1024x943.png" alt="" class="wp-image-114763" style="width:604px;height:auto">
</figure>

*** ** * ** ***

6. Run TornadoVM Examples/Applications from IntelliJ {#h2-8-6-run-tornadovm-examples-applications-from-intellij}
----------------------------------------------------------------------------------------------------------------

To add a new Application you can go in the menu (top bar) and navigate through Run and the Edit Configurations, to create a new application:{#7d56w493465}

**Run \> Edit Configurations \> Application \> Add new run configuration...**{#mw7wg495052}

### a) Add a name for your application {#o5dlo530188}

For instance, you can add TornadoVM-MatrixMultiplication.{#buhog571958}

### b) In the "Build and run" area apply the following configurations: {#ugfpl539371}

#### i) Add VM options {#o4wy4571045}

You need to click Modify options and Add VM options. Once you have enabled the VM options you can obtain thee TornadoVM Java flags which enable the execution with TornadoVM, by running in the terminal:{#jm3jm584482}

* In macOS/Linux OS:

{#lmy7i618609}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ cd &lt;path-to-TornadoVM-directory&gt;
$ source setvars.sh
$ tornado --printJavaFlags</pre>

* In Windows OS:

{#per2e618615}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ cd &lt;path-to-TornadoVM-directory&gt;
$ .\bin\windowsMicrosoftStudioTools2022.cmd
$ setvars.cmd
$ tornado --printJavaFlags</pre>

The output of the command depends on the TornadoVM backends you've built. For example, if you build with all backends, it should be similar to this:{#mjihz571845}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;path-to-TornadoVM-directory&gt;/etc/dependencies/TornadoVM-graal-jdk-21/graalvm-community-openjdk-21.0.1+12.1/bin/java
-server -XX:-UseCompressedOops -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -XX:-UseCompressedClassPointers --enable-preview -Djava.library.path=&lt;path-to-TornadoVM-directory&gt;/bin/sdk/lib  --module-path .:&lt;path-to-TornadoVM-directory&gt;/bin/sdk/share/java/tornado
-Dtornado.load.api.implementation=uk.ac.manchester.tornado.runtime.tasks.TornadoTaskGraph -Dtornado.load.runtime.implementation=uk.ac.manchester.tornado.runtime.TornadoCoreRuntime -Dtornado.load.tornado.implementation=uk.ac.manchester.tornado.runtime.common.Tornado
-Dtornado.load.annotation.implementation=uk.ac.manchester.tornado.annotation.ASMClassVisitor -Dtornado.load.annotation.parallel=uk.ac.manchester.tornado.api.annotations.Parallel  -XX:+UseParallelGC
@&lt;path-to-TornadoVM-directory&gt;/bin/sdk/etc/exportLists/common-exports
@&lt;path-to-TornadoVM-directory&gt;/bin/sdk/etc/exportLists/opencl-exports
@&lt;path-to-TornadoVM-directory&gt;/bin/sdk/etc/exportLists/spirv-exports
@&lt;path-to-TornadoVM-directory&gt;/bin/sdk/etc/exportLists/ptx-exports --add-modules ALL-SYSTEM,tornado.runtime,tornado.annotation,tornado.drivers.common,tornado.drivers.opencl,tornado.drivers.opencl,tornado.drivers.ptx</pre>

Copy the flags starting from -server to the end, and add them in the VM options field.{#t8glk631723}

#### ii) Configure the JDK for running your applications {#x1wsn640917}

Configure the module not specified field to point to the JDK distribution that was used to build TornadoVM. The JDK distribution is defined in Step 1 with the --jdk option. In our example, we used GraalVM JDK 21.{#s01cj648839}

#### iii) Configure the classpath module {#qc07c676331}

Configure the correct module where your application belongs to. For instance to run the Matrix Multiplication class which belongs to the TornadoVM examples module, we select:{#tq1c3684901}

-cp tornado-examples{#20myn940063}

#### iv) Configure the main class {#5or4f728410}

You can start typing the name of your class in the field and select it. In our example, the Main class is:{#7nayz729952}

uk.ac.manchester.tornado.examples.compute.MatrixMultiplication2D{#86juf940709}

#### v) Configure the arguments of the class {#06hzf774350}

You can add any values that will be selected as arguments for your class. In our example, we can define the length of the matrices to be 256.{#u8e4y774352}

#### vi) Save and run {#zvjjb803902}

You can click Apply and Run your application.{#yne6f805976}  

<figure class="aligncenter size-large is-resized">
 <img loading="lazy" decoding="async" width="1024" height="787" src="Configure-Application-1024x787.png" alt="" class="wp-image-114764" style="width:624px;height:auto">
</figure>

The output should be similar to the following image, which is executed on Apple M1 silicon.  

<figure class="aligncenter size-large is-resized">
 <img loading="lazy" decoding="async" width="1024" height="502" src="Run-Application-1024x502.png" alt="" class="wp-image-114765" style="width:628px;height:auto">
</figure>

*** ** * ** ***

*Summary* {#h2-11-summary}
--------------------------

This blog presented how Java programmers can build and run TornadoVM applications from the IntelliJ IDEA. More information are provided in the TornadoVM [++documentation++](https://tornadovm.readthedocs.io/en/latest/ide-integration.html#build-and-run-with-ide). You may find useful to read a previous [++blog++](https://www.tornadovm.org/post/introducing-tornadoinsight-unleashing-the-power-of-tornadovm-in-intellij-idea) that introduced [++TornadoInsight++](https://plugins.jetbrains.com/plugin/23309-tornadoinsight), the TornadoVM IntelliJ plugin.

<br />

### Useful links {#h3-12-useful-links}

* TornadoVM [++documentation++](https://tornadovm.readthedocs.io/en/latest/introduction.html)
* GitHub [repository](https://github.com/beehive-lab/TornadoVM)
