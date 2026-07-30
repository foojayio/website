---
title: "FEPCOS-J (1) – Description, Impression of Usage, Current State"
slug: "fuchs-2023-fepcos-j-01"
date: "2023-04-26T06:09:41+00:00"
lastmod: "2023-04-26T08:29:52+00:00"
description: "FEPCOS-J implements a Java-language extension that frees a Java-developer from network programming and supports cross-system concurrency."
authors:
  - "gerhard-fuchs"
image: "/images/posts/2023/04/fuchs-2023-fepcos-j-01/fuchs2023fepcos-j-toy-robots-are-parts-of-current-test-and-development-environment.png"
categories:
  - "Developer Tools"
  - "Raspberry Pi"
  - "Research"
  - "Use Cases"
tags:
related_posts:
  - "concurrency-in-java-and-how-it-compares-with-other-modern-programming-languages"
  - "5-things-you-probably-didnt-know-about-java-concurrency"
  - "java-17-on-the-raspberry-pi"
  - "fuchs-2024-fepcos-j-multithreaded-server"
enlighterjs: true
frozen: false
---

**Abstract: FEPCOS-J has arisen in the context of my work in the field of robot sensor networks. It implements a Java-language extension that frees a Java developer from network programming and supports cross-system concurrency. This article gives you an impression of using FEPCOS-J based on a simple example.**

So far, FEPCOS-J is a prototype with 1629 lines of Java-code. I develop and test it on my own within a toy robot-based *amd64 / x64* *Linux* *OpenJDK 17* environment, which communicates via a private and secured IPv4-network.

You can contribute so that FEPCOS-J becomes *Free/Libre and Open-Source Software* (FLOSS).

Introduction {#h2-0-introduction}
---------------------------------

When I was a child, I loved to create things out of building blocks --- this has never changed (Fig. 1). It is a pleasure for me to compose parts into a new whole. This also applies for programming.  

<figure class="aligncenter size-full is-resized">
 <img fetchpriority="high" decoding="async" src="/images/posts/2023/04/fuchs-2023-fepcos-j-01/fuchs2023fepcos-j-toy-robots-are-parts-of-current-test-and-development-environment.png" alt="See two toy robots that are constructed out of building blocks. A Raspberry Pi Model 3B that is installed on each robot controls them. The robots run Linux, OpenJDK 17, and FEPCOS-J." class="wp-image-65636" width="540" height="432">
 <figcaption class="wp-element-caption">
  <strong>Fig. 1) Parts of the current test environment for FEPCOS-J: </strong>I have built these two toy robots to test and demonstrate the use of FEPCOS-J. The two robots can be used independently of each other. In addition, they can be composed into a swarm. Both each individual robot and the swarm are example systems. FEPCOS-J enables a developer to declaratively program those systems so that they are controllable via an IPv4-network.
 </figcaption>
</figure>

I am a computer scientist who has researched and worked in the field of robot sensor networks since 2004. A network I have worked on was based on Java-programmable SunSPOTs (spots). \[[1,2,3](#references)\].

There were up to a hundred spots (the number depended on the experiment) that had to interact and fulfill a common task. The network was a new whole composed of parts --- the spots. In this situation, I considered both the whole and each individual part as a system that I had to program.

This abstraction has helped me handle the complexity. In the following years, I have dug deeper into this matter. Among others, FEPCOS-J has arisen in this context. \[[4,5,6](#references)\].

Outline {#h2-1-outline}
-----------------------

This post is organized as follows:

Firstly, the section [FEPCOS-J -- Description](#description) briefs you on the idea and concept of FEPCOS-J. In this context, I introduce its essential parts, especially two tools: *fjp* and *fjx*. Further, I explain the expressions and abbreviations I am using in this post.

Secondly, the section [FEPCOS-J -- Impressions of Usage](#impressions) gives you an impression of the use of FEPCOS-J based on the following example: A system shall provide the capability to add two numbers via an IPv4-network. In this context, I explain the complete code that is required to specify this system. Further, I describe how to make it accessible via the network. Finally, I present how to access the system via the network by means of *jshell*.

Thirdly, the section [FEPCOS-J -- Current State](#currentState) describes the actual test and development environment (Fig. 1 shows a part of it). In addition, the section lists restrictions and supported features of FEPCOS-J. Further, I provide numbers that give you an impression of the complexity of the software.

Lastly, the section [Conclusion and Outlook](#conclusionOutlook) concludes this post and announces my next post. Further, I encourage you to contribute so that FEPCOS-J becomes *Free/Libre and Open-Source Software*.

FEPCOS-J -- Description {#description}
--------------------------------------

FEPCOS-J implements a model-based Java-language extension for declarative programming of networked systems. It frees a developer from network programming and supports cross-system concurrency.

For this purpose, FEPCOS-J provides:

* the FEPCOS-J Annotation Module *FAM*;
* the FEPCOS-J Processor fjp;
* the FEPCOS-J Exporter *fjx*;
* additional modules realized as modular *jar*-files.

To clarify, *networked* *systems* are either *basic systems* or *composed systems* ; both types communicate via an IPv4-network. *Basic systems* are the fundamental building blocks. *Composed systems* use other networked systems as parts. The *capabilities* of a system are the *activities* it can *execute*.

Further, *cross-system concurrency* means that the systems can operate independently from each other. So, a *system user* can trigger the execution of an activity on a remote system both *blockingly* and *concurrently*. In the first case, the system user waits until it gets the result of the execution from the remote system. In the second case, the system user can do something while the remote system executes the triggered activity.

### A developer uses the *FEPCOS-J Annotation Module* for the programming of a system specification. Afterwards, the *FEPCOS-J Processor* can generate a system import module and a system export module out of the code. {#h3-3-a-developer-uses-the-fepcos-j-annotation-module-for-the-programming-of-a-system-specification-afterwards-the-fepcos-j-processor-can-generate-a-system-import-module-and-a-system-export-module-out-of-the-code}

A developer programs a *system specification* as a Java-module (Fig. 2). In detail, it specifies a system and its capabilities by means of the Java-annotations *FAM* provides.

After that, the developer uses *fjp* to process the system specification. Thereupon, *fjp* generates two modular jar-files -- the *system export module* (*SXM* ) and the *system import module* (*SIM*).  
![A developer programs a system specification by using the FEPCOS-J Annotation Module FAM; the FEPCOS-J Processor fjp processes the specification and generates a system export module and a system import module.](/images/posts/2023/04/fuchs-2023-fepcos-j-01/fuchs2023fepcos-j-developing-by-using-annotation-module-fam-and-processor-fjp.png) **Fig. 2) Developing with FEPCOS-J:** The Java module *FAM* provides Java-annotations that allow model-based, declarative programming of composed networked systems as Java-modules. If the developer uses those annotations for the programming, then *fjp* can process the *system specification* and generate a *system export module* and a *system import module*.

### The *FEPCOS-J Exporter* provides access to the compiled system specification via an IPv4-network. Thereupon, a system user can access it by means of a generated system interface, the system import module contains. {#h3-4-the-fepcos-j-exporter-provides-access-to-the-compiled-system-specification-via-an-ipv4-network-thereupon-a-system-user-can-access-it-by-means-of-a-generated-system-interface-the-system-import-module-contains}

*SXM* and *SIM* are deployed to two different Java Runtime Environments that can communicate via an IPv4-network (Fig. 3).

One environment contains *SXM* and runs *fjx*, which provides access to the compiled system specification via an internet socket.

The other environment contains *SIM* and runs the *system application* . In addition, it contains a *system user* that accesses the system specification via the network.

This access can be both blocking and concurrent. *fjp* generates a corresponding *system interface* and an *adapter*. They handle the required coordination and network communication together with additional modules FEPCOS-J provides.  
![FEPCOS-J protocol stack: a system import module (SIM) and a corresponding system export module (SXM) are deployed to different Java-Runtime Environments, which can communicate via an IPv4-network. One Java-environment runs fjx and contains SXM. The other runs the system application and contains the SIM and a system user.](/images/posts/2023/04/fuchs-2023-fepcos-j-01/fuchs2023fepcos-j-protocol-stack-system-user-accesses-system-specification-via-ip-network.png) **Fig. 3)** **Using FEPCOS-J to access a system specification via an IPv4-network:** A *system user* can access a *system specification* via an IPv4-network. In detail, the access is via a generated *system interface* that*SIM* contains, via modules FEPCOS-J provides, via an internet socket *fjx* listens to, via modules FEPCOS-J provides, and via a generated *adapter* that *SXM* contains.

FEPCOS-J -- Impressions of Usage {#impressions}
-----------------------------------------------

This section describes a typical workflow for Java-programming with FEPCOS-J. In particular, it explains the complete Java-source code of a system specification by using the following example:

A basic system *adder* shall provide the capability to add two numbers via an IPv4-network. A system user shall get both blocking and concurrent access to it via the internet socket *10.0.0.1:11111*.

### Programming of the basic system -- *adder* {#h3-6-programming-of-the-basic-system-adder}

The developer realizes the system specification as a Java-module *adder.spec* . For this purpose, it programs the module information *module-info.java* , the system declaration *Sy.java* , and the activity specification *Add.java* (Fig. 4a).

The following describes the Java-code in detail.

#### Module information module-info.java

The module *adder.spec* realizes the system specification and requires the annotations FEPCOS-J provides in the module *fepcos.annotations* for compile time. The two packages, *fepcos.sy* and *fepcos.ay* , belong to *fepcos.annotations*. These two packages contain all the annotations used for this example.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="module-info.java" data-enlighter-group="adder-module-info">module adder.spec {
	requires static fepcos.annotations;
}</pre>

#### System declaration Sy.java

The class *SY* belongs to the package *adder.spec* and imports *fepcos.sy* , which contains the annotations *@SYDec* and *@Cap*. The following explains their usage.

*@SYDec* specifies that the annotated class *SY* is the system declaration. *fjp* uses the *String* of the annotation to generate the documentation. *@Cap* declares a capability *add* , which the class *Add* realizes.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="SY.java" data-enlighter-group="adder-sydec">package adder.spec;

import fepcos.sy.*;

@SYDec("A system that adds numbers.")
class SY {
	@Cap Add add;
}</pre>

#### Activity specification Add.java

The class *Add* also belongs to the package *adder.spec* . Further, it imports *fepcos.ay* , which contains the annotations *@AYSpec* , *@In* , *@Out* , and *@Behavior*. The following explains their usage.

*@AYSpec* specifies that the annotated class *Add* is the activity specification. *@In* declares the input parameters *x* and *y* , and *@Out* the output parameter *z* . *@Behavior* annotates the method *go()* , which realizes the behavior: *x* and *y* are added, and the result is stored in *z*.

*fjp* uses all *Strings* in the annotations to generate the documentation.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="Add.java" data-enlighter-group="adder-ayspec">package adder.spec;

import fepcos.ay.*;

@AYSpec("Add two numbers x,y and returns the result z.")
class Add {
	@In("First summand.")  int x;
	@In("Second summand.") int y;
	@Out("The sum.")       int z;

	@Behavior
	void go() {
		z = x + y;
	}
}</pre>

### *fjp* processes the system specification; *fjx* provides access to it via an IPv4-network. {#h3-7-fjp-processes-the-system-specification-fjx-provides-access-to-it-via-an-ipv4-network}

Thereafter, the developer uses *fjp* (Fig. 4b), which processes the system specification and stores the output in the folder *tgt* (Fig. 4c). As a result, *fjp* generates the system export module *adder.exp.jar* , the system import module *adder.imp.jar* , and the documentation of the system interface *adder.imp-doc.zip*.

At last, the developer uses *fjx* to make the system specification accessible via the IP-network (Fig. 4d). For this purpose, it specifies the following parameters: *tgt* is the folder that contains *adder.exp.jar* ; *adder.exp* is the name of the system export module; and *11111* is the port on which *fjx* is listening for incoming requests.  
![screenshot, bash: Example of using FEPCOS-J: a source code structure of a system specification in Java that fjp processes to generate an output, which fjx can export afterwards.](/images/posts/2023/04/fuchs-2023-fepcos-j-01/fuchs2023fepcos-j-screenshot-bash-using-fjp-and-fjx-from-system-specification-in-java-to-network-accessible-system.png) **Fig. 4) Using FEPCOS-J to program a system specification and provide access to it via an IPv4-network.**

### Using *jshell* to access the specified system via the network. {#h3-8-using-jshell-to-access-the-specified-system-via-the-network}

At this point, the system specification is accessible via the IP-network from another computer.

To do so, the developer must copy *adder.imp.jar* to this computer, as it contains the system import module *adder.imp* that *fjp* has generated. The developer sets a module path such that it contains *adder.imp* together with the modules FEPCOS-J provides (Fig. 5a), and starts *jshell* with the corresponding parameters afterwards.

The module *adder.imp* contains the system interface *adder.S*. The expression

`var sy = new adder.S("10.0.0.1", 11111)`

instantiates it, so that the variable *sy* is the reference (Fig. 4b). The passed parameters explicitly specify the internet socket at which *fjx* listens for incoming requests. In detail, *"10.0.0.1"* is the IP-address, *11111* is the port. As a result, *sy* provides both a blocking access *sy.b* and a concurrent access *sy.c* to the remote system.

To demonstrate this,

`var r1 = sy.b.add(1,2)`

causes the calling computer to wait until the remote system has added the numbers *1* and *2* and returned (Fig. 5c).

With this intention, FEPCOS-J operates in the background. In brief, it transmits the parameters via the network; sets the input parameters *x=1* and *y=2* of the corresponding activity specification *Add* ; triggers *go()* of the class *Add* ; transmits back the parameter *z=3* ; and stores the result in *r1.z* (Fig. 5d).

Analogous to the prior expression,

`var r2 = sy.c.add(3,4)`

causes the concurrent execution (Fig. 5e).

But in that case, the calling computer does not have to wait for the result and could do something else. FEPCOS-J triggers a concurrent execution and returns an instance of [java.util.Future](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/concurrent/Future.html). Thus, *r2.get().z* delivers the result *7* (Fig. 5f).

Finally, `sy.close()` closes the system interface (Fig. 5g).

<figure class="wp-block-image size-full is-resized">
 <img loading="lazy" decoding="async" src="/images/posts/2023/04/fuchs-2023-fepcos-j-01/fuchs2023fepcos-j-screenshot-bash-using-jshell-to-access-system-via-ip-network-by-using-system-interface-fjp-has-generated.png" alt="screenshot, bash: Example of using jshell to access a system specification that fjx has exported via an IPv4-network by means of a system interface that fjp has generated." class="wp-image-65654" width="840" height="630">
 <figcaption class="wp-element-caption">
  <strong>Fig. 5)</strong> <strong>Accessing a system specification via the IPv4-network by means of<em> jshell</em>.</strong>
 </figcaption>
</figure>

FEPCOS-J -- Current State {#currentState}
-----------------------------------------

### Test and development environment {#h3-10-test-and-development-environment}

For development and testing, I am using:

* two Raspberry Pi Model 3B (*arm64*) controlled toy robots (Fig. 1);
* a notebook and a mini-PC (both *x64*);
* corresponding Linux-operating systems that host *OpenJDK 17*;
* a private and secured IPv4-network.

So far, I have no experience running FEPCOS-J on any other platforms.

### Restrictions and supported features {#h3-11-restrictions-and-supported-features}

Currently, FEPCOS-J is a prototype implementation that:

* does not provide any authentication or encryption. Thus, it is essential to solely use it within a private and secured network.
* solely requires Java-modules, which OpenJDK provides.
* supports the specification of basic systems, as explained in the example above. In other words, it automates network programming and supports a developer regarding cross-system concurrency by automating parts of the required Thread-programming.
* further supports the specification of composed systems, like the robot swarm shown in Fig. 1. In short, the additional annotation *@Part(ip, port*) allows declarative programming of "composing".
* allows the data types *boolean* , *byte* , *char* , *double* , *float* , *int* , *long* , *short* , *String* , and *byte\[\]* for the declaration of the activity parameters.

### Numbers {#h3-12-numbers}

The following numbers refer to the build of FEPCOS-J that I have used for the example above. Counted with `cloc src`, the source code of FEPCOS-J consists of:

* 53 Java-files with a total of 1629 lines of code (+ commends and blanks);
* 2 bash-files with a total of 18 lines of code (+ blanks).

Those 53 Java-files are organized in 7 modular jar-files, which require 88 kB in total, if compiled with debug level *{lines,vars,source}*.

So far, I am the only person who develops and uses FEPCOS-J.

Conclusion and Outlook {#conclusionOutlook}
-------------------------------------------

In this post, I have described FEPCOS-J and given you an idea of how to use it. For this purpose, I have exemplary realized a system that provides both blocking and concurrent access via an IPv4-network; this post explains the complete Java-code.

To sum up, this post demonstrates how FEPCOS-J can free you from network programming and support you regarding cross-system concurrency.

In my next post, I show how FEPCOS-J can be used to program a composed system.

### I need your help so that FEPCOS-J becomes *Free/Libre and Open-Source Software*. {#h3-14-i-need-your-help-so-that-fepcos-j-becomes-free-libre-and-open-source-software}

Have you thought about how FEPCOS-J can support your work?

FEPCOS-J shall become Free/Libre and Open Source Software (FLOSS).

But to achieve this, I need your help. So far, FEPCOS-J is a prototype that I have independently developed. I need your support now, especially regarding legal issues, the testing environment, and, most importantly, funding. Please provide me with feedback and share this content.

It would be great if we could establish a new, declarative way for programming networked systems in Java.

Thanks for reading!

*** ** * ** ***

References {#references}
------------------------

1. Wikipedia, The Free Encyclopedia: "*Sun SPOT* "; <https://en.wikipedia.org/wiki/Sun_SPOT> (last accessed: 2023-04-20).
2. Fuchs, G. and German, R.: "*UML2 activity diagram based programming of wireless sensor networks* "; 2010 ICSE Workshop on Software Engineering for Sensor Network Applications (SESENA); Cape Town, South Africa; 2010; pp. 8--13; <https://doi.org/10.1145/1809111.1809116>.
3. Fuchs, G. et al.: "*Demo: The acoowee-framework* "; 2011 International Conference on Distributed Computing in Sensor Systems and Workshops (DCOSS); Barcelona, Spain; 2011; pp. 1-2; <https://doi.org/10.1109/DCOSS.2011.5982148>.
4. Fuchs, G.; "Das FEPCOS-Projekt"; <http://fepcos.info> (german, last accessed 2023-04-20).
5. Fuchs, G.; "*Vernetzte Systeme mit FEPCOS-J*"; In: JAVAPRO OKT/2020; pp. 98-105; Verlag JAVAPRO; Eschborn, Germany; 10/2020 (german).
6. Fuchs, G.; "*Eine Java-Spracherweiterung zum Programmieren von zusammengesetzten vernetzten Systemen*"; In: Java aktuell 01/2022; pp 39-46; Interessenverbund der Java User Groups e.V. (iJUG); Berlin, Germany; 12/2021 (german).
