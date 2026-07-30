---
title: "Java 11 & JavaFX 11 on Raspberry Pi with ARMv6 Processor"
slug: "java-11-javafx-11-on-raspberry-pi-with-armv6-processor"
date: "2020-09-04T08:00:39+00:00"
lastmod: "2020-09-09T08:24:39+00:00"
description: "Here are complete and thorough steps for having a working Java JDK with JavaFX 11 on the Raspberry Pi using a ARMv6 Processor."
canonical: "https://webtechie.be/post/2020-08-27-azul-zulu-java-11-and-gluon-javafx-11-on-armv6-raspberry-pi/"
authors:
  - "frankdelporte"
image: "/images/posts/2020/09/java-11-javafx-11-on-raspberry-pi-with-armv6-processor/javafx-on-armv6-raspberrypi.png"
categories:
  - "JavaFX"
  - "Raspberry Pi"
tags:
related_posts:
  - "a-fresh-look-at-embedded-java"
  - "first-experiments-with-java-on-the-lattepanda-iota"
  - "javafx-links-of-november-2025"
  - "will-openjfx-be-merged-into-openjdk-it-would-be-a-perfect-match-with-java-on-mobile"
enlighterjs: true
frozen: false
---

In a previous post "[Installing Java and JavaFX on the Raspberry Pi](https://foojay.io/blog/installing-java-and-javafx-on-the-raspberry-pi/)", you can read how to install BellSoft LibericaJDK to be able to run JavaFX applications with a graphical user interface on a Raspberry Pi with ARMv7 or ARMv8 processor.

But this won't work for some (older) versions of the Raspberry Pi, as these use an ARMv6 processor, which is not compatible with the default OpenJDK 11 that is part of Raspbian OS.

This post will guide you through the steps to have a working Java JDK and JavaFX 11 on these Raspberry Pi board versions.

Prepare a Raspberry Pi {#h2-0-prepare-a-raspberry-pi}
-----------------------------------------------------

### ARMv6 Raspberry Pi board {#h3-1-armv6-raspberry-pi-board}

For this post, I'm using an old Raspberry Pi B+ 1.2. To be sure which ARM-version is used, check the output of "cat /proc/cpuinfo" in the terminal:  

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ cat /proc/cpuinfo

processor	: 0
model name	: ARMv6-compatible processor rev 7 (v6l)
BogoMIPS	: 697.95
Features	: half thumb fastmult vfp edsp java tls 
CPU implementer	: 0x41
CPU architecture: 7
CPU variant	: 0x0
CPU part	: 0xb76
CPU revision	: 7

Hardware	: BCM2835
Revision	: 0010
Serial		: 000000005f9ba615
Model		: Raspberry Pi Model B Plus Rev 1.2</pre>

For a clear overview of the different board- and ARM-version, check this [table on Wikipedia](https://en.wikipedia.org/wiki/Raspberry_Pi#Specifications). The boards with ARMv6 are:

* Raspberry Pi 1 A and A+
* Raspberry Pi 1 B and B+
* Compute Module 1
* Zero 1.2, 1.3 and W

### Prepare SD card with Raspbian OS (Full) {#h3-2-prepare-sd-card-with-raspbian-os-full}

We start with a fresh new Raspbian OS on the SD card using the "[Imager](https://www.raspberrypi.org/downloads/)" tool.

When we now boot the Rasberry Pi board with an ARMv6 processor and check the Java version we get this result:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ java -version
Error occurred during initialization of VM
Server VM is only supported on ARMv7+ VFP</pre>

As expected, the default included OpenJDK for ARM is build for version 7 or higher, so doesn't work on this ARMv6-based Raspberry Pi B+ 1.2.

Change the Java JDK {#h2-3-change-the-java-jdk}
-----------------------------------------------

The sources of Java are available through the [open-source project OpenJDK](http://openjdk.java.net/). So anyone can build Java JDK packages - yes you can even [do it yourself](https://hg.openjdk.java.net/jdk-updates/jdk9u/raw-file/tip/common/doc/building.html)! - and luckily a lot of free pre-build versions are available.

### Install Java 11 for ARMv6 provided by Azul {#h3-4-install-java-11-for-armv6-provided-by-azul}

Only Azul seems to provide an ARMv6 version with their [Zulu builds of OpenJDK](https://www.azul.com/downloads/zulu-community), which is available for free!
![](/images/posts/2020/09/java-11-javafx-11-on-raspberry-pi-with-armv6-processor/azul-zulu-armv6-1024x328.png)

Let's get it from their download page and extract it on our Raspberry Pi.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ cd /usr/lib/jvm
$ sudo wget https://cdn.azul.com/zulu-embedded/bin/zulu11.41.75-ca-jdk11.0.8-linux_aarch32hf.tar.gz
$ sudo tar -xzvf zulu11.41.75-ca-jdk11.0.8-linux_aarch32hf.tar.gz
$ sudo rm zulu11.41.75-ca-jdk11.0.8-linux_aarch32hf.tar.gz
$ ls -l
total 12
lrwxrwxrwx  1 root root   21 Jul 23 15:58 java-1.11.0-openjdk-armhf -&gt; java-11-openjdk-armhf
drwxr-xr-x  9 root root 4096 Aug 20 11:41 java-11-openjdk-armhf
drwxr-xr-x  2 root root 4096 Aug 20 11:41 openjdk-11
drwxrwxr-x 10  111  122 4096 Jul 10 16:50 zulu11.41.75-ca-jdk11.0.8-linux_aarch32hf</pre>

OK, there it is! A new JDK on our board. Now let's configure the OS to be aware of this new one.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ sudo update-alternatives --install /usr/bin/java java /usr/lib/jvm/zulu11.41.75-ca-jdk11.0.8-linux_aarch32hf/bin/java 1
$ sudo update-alternatives --install /usr/bin/javac javac /usr/lib/jvm/zulu11.41.75-ca-jdk11.0.8-linux_aarch32hf/bin/javac 1</pre>

Now we can select the new JDK so it's linked to the "java" and "javac" command.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ sudo update-alternatives --config java
There are 2 choices for the alternative java (providing /usr/bin/java).

  Selection    Path                                                             Priority   Status
------------------------------------------------------------
* 0            /usr/lib/jvm/java-11-openjdk-armhf/bin/java                       1111      auto mode
  1            /usr/lib/jvm/java-11-openjdk-armhf/bin/java                       1111      manual mode
  2            /usr/lib/jvm/zulu11.41.75-ca-jdk11.0.8-linux_aarch32hf/bin/java   1         manual mode

Press &lt;enter&gt; to keep the current choice[*], or type selection number: 2
update-alternatives: using /usr/lib/jvm/zulu11.41.75-ca-jdk11.0.8-linux_aarch32hf/bin/java to provide /usr/bin/java (java) in manual mode

$ sudo update-alternatives --config javac
There are 2 choices for the alternative javac (providing /usr/bin/javac).

  Selection    Path                                                              Priority   Status
------------------------------------------------------------
* 0            /usr/lib/jvm/java-11-openjdk-armhf/bin/javac                       1111      auto mode
  1            /usr/lib/jvm/java-11-openjdk-armhf/bin/javac                       1111      manual mode
  2            /usr/lib/jvm/zulu11.41.75-ca-jdk11.0.8-linux_aarch32hf/bin/javac   1         manual mode

Press &lt;enter&gt; to keep the current choice[*], or type selection number: 2
update-alternatives: using /usr/lib/jvm/zulu11.41.75-ca-jdk11.0.8-linux_aarch32hf/bin/javac to provide /usr/bin/javac (javac) in manual mode</pre>

If everything went well, we should be able to check the Java version now...

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ java -version
openjdk version "11.0.8" 2020-07-14 LTS
OpenJDK Runtime Environment Zulu11.41+75-CA (build 11.0.8+10-LTS)
OpenJDK Client VM Zulu11.41+75-CA (build 11.0.8+10-LTS, mixed mode)</pre>

We have a winner! We now successfully replaced the default OpenJDK 11 (which only works on ARMv7+) with the Azul Zulu JDK which works on ARMv6.

### Testing with a non-compiled Java file {#h3-5-testing-with-a-non-compiled-java-file}

Let's try out the newly installed Java JDK. Since Java 11, we can run Java-files directly without the need to compile them. Let's create a simple file with nano and run it.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ cd /home/pi
$ nano HelloWorld.java

public class HelloWorld {
    public static void main (String[] args) {
        System.out.println("Hello World");
    }
}

$ java HelloWorld.java
Hello World</pre>

Perfect! Java works as expected, but it takes about 15 seconds before the "Hello World" is shown on this old board dating from 2014.

Graphical user interfaces with JavaFX {#h2-6-graphical-user-interfaces-with-javafx}
-----------------------------------------------------------------------------------

If you also want to use JavaFX user interfaces, additional steps are needed as this library is not included in the JDK 11. It is developed as an independent open-source project on [openjfx.io](https://openjfx.io/). The main contributor and maintainer is [Gluon](https://gluonhq.com/). They also offer [commercial support](https://gluonhq.com/services/javafx-support/) to companies who want to use JavaFX for desktop and mobile application development.

Make sure you successfully updated to Azul Zulu JDK 11 before proceeding with the next steps.

### Install JavaFX 11 for ARMv6 provided by GluonHQ {#h3-7-install-javafx-11-for-armv6-provided-by-gluonhq}

We are going to use the free public version provided by Gluon on their [download page](https://gluonhq.com/products/javafx/).

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ cd /home/pi
$ wget -O javafx.zip https://gluonhq.com/download/javafx-11-0-2-sdk-armv6hf/
$ unzip javafx.zip
$ rm javafx.zip</pre>

We can now check the JavaFX library which was unpacked in "armv6hf-sdk":

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">pi@raspberrypi:~ $ ls -l
total 44
drwxr-xr-x 4 pi pi 4096 Mar 12  2019 armv6hf-sdk
drwxr-xr-x 2 pi pi 4096 Aug 20 11:40 Bookshelf
drwxr-xr-x 2 pi pi 4096 Aug 20 12:10 Desktop
...

$ ls -l armv6hf-sdk/
total 8
drwxr-xr-x 8 pi pi 4096 Mar 12  2019 legal
drwxr-xr-x 2 pi pi 4096 Mar 12  2019 lib

$ ls -l armv6hf-sdk/lib/
total 17124
-rw-r--r-- 1 pi pi  845637 Mar 12  2019 javafx.base.jar
-rw-r--r-- 1 pi pi 2761905 Mar 12  2019 javafx.controls.jar
-rw-r--r-- 1 pi pi  143926 Mar 12  2019 javafx.fxml.jar
-rw-r--r-- 1 pi pi 5270589 Mar 12  2019 javafx.graphics.jar
-rw-r--r-- 1 pi pi  294822 Mar 12  2019 javafx.media.jar
-rw-r--r-- 1 pi pi     992 Mar 12  2019 javafx.platform.properties
-rw-r--r-- 1 pi pi     113 Mar 12  2019 javafx.properties
-rw-r--r-- 1 pi pi   41802 Mar 12  2019 javafx-swt.jar
-rw-r--r-- 1 pi pi  786021 Mar 12  2019 javafx.web.jar
-rwxr-xr-x 1 pi pi   61200 Mar 12  2019 libdecora_sse.so
-rwxr-xr-x 1 pi pi   31428 Mar 12  2019 libglass_monocle.so
-rwxr-xr-x 1 pi pi   15946 Mar 12  2019 libglass_monocle_x11.so
-rwxr-xr-x 1 pi pi  200074 Mar 12  2019 libglass.so
-rwxr-xr-x 1 pi pi   22409 Mar 12  2019 libjavafx_font_freetype.so
-rwxr-xr-x 1 pi pi   20508 Mar 12  2019 libjavafx_font_pango.so
-rwxr-xr-x 1 pi pi   15206 Mar 12  2019 libjavafx_font.so
-rwxr-xr-x 1 pi pi  231402 Mar 12  2019 libjavafx_iio.so
-rwxr-xr-x 1 pi pi   43339 Mar 12  2019 libprism_common.so
-rwxr-xr-x 1 pi pi   54506 Mar 12  2019 libprism_es2_monocle.so
-rwxr-xr-x 1 pi pi   56505 Mar 12  2019 libprism_sw.so
-rw-r--r-- 1 pi pi 6598638 Mar 12  2019 src.zip</pre>

### Test with a minimal JavaFX application {#h3-8-test-with-a-minimal-javafx-application}

We are going to reuse the minimal JavaFX application which was created in this post "[PiJava - Part 4 - Building a minimal JavaFX 11 application with Maven](https://webtechie.be/post/2019-04-01-pijava-part-4-building-a-minimal-javafx-11-application-with-maven/)". First, we need to clone the sources from GitHub:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ cd /home/pi
$ git clone https://github.com/FDelporte/MinimalJavaFx11Application.git</pre>

To be able to build the application, we also need Maven to be installed.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ sudo apt install maven</pre>

Now let's build the application:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ cd MinimalJavaFx11Application
$ mvn clean package</pre>

This will take some time as all the dependencies need to be downloaded.

When finished, we can now run the application with the following start command which points to the downloaded JavaFX library and the generated jar-application in the out-directory of "MinimalJavaFx11Application":

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ sudo java --module-path /home/pi/armv6hf-sdk/lib
         --add-modules=javafx.controls
          -jar /home/pi/MinimalJavaFx11Application/out/MinimalJavaFx11Application-0.1-SNAPSHOT.jar</pre>

And there we have it! JavaFX running on an ARMv6 Raspberry Pi B+ 1.2!!!
![](/images/posts/2020/09/java-11-javafx-11-on-raspberry-pi-with-armv6-processor/javafx-on-armv6-raspberrypi-1024x422.png)

Conclusion {#h2-9-conclusion}
-----------------------------

Compared to the latest Raspberry Pi with a much faster processor and more memory, the application starts a lot slower on my 6-year old test board. But it works! Yes, really, it works 🙂

Again this small-superhero-board proves to be able to handle everything, even the most modern Java versions on an old processor.

**Note:** Used with permission and thanks --- originally written by Frank Delporte and published on [Frank Delporte's blog](https://webtechie.be/post/2020-08-27-azul-zulu-java-11-and-gluon-javafx-11-on-armv6-raspberry-pi/).
