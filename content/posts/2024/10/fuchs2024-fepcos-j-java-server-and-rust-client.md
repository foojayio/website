---
title: "Java Server and Rust Client Built with Generated Networking Code"
slug: "fuchs2024-fepcos-j-java-server-and-rust-client"
date: "2024-10-04T11:58:05+00:00"
lastmod: "2024-10-04T11:58:06+00:00"
description: "FEPCOS-J allows declaratively implementing a Java server and generating its profile usable to generate the networking code of a Rust client."
authors:
  - "gerhard-fuchs"
image: "https://foojay.io/wp-content/uploads/2024/09/fuchs2024-java-server-and-rust-client-example.png"
categories:
  - "Developer Tools"
  - "Use Cases"
tags:
related_posts:
  - "fuchs-2024-fepcos-j-multithreaded-server"
  - "fuchs-2023-fepcos-j-02"
  - "fuchs-2024-video-fepcos_j-client-server-application-in-java"
  - "fuchs-2023-fepcos-j-03-native-executables"
enlighterjs: true
frozen: false
---

**FEPCOS-J allows declaratively implementing a Java server and generating its profile, which, for example, can be used to generate the networking code of a Rust client.**

Introduction {#h2-0-introduction}
---------------------------------

As I have shown in a [previous post](https://foojay.io/today/fuchs-2024-fepcos-j-multithreaded-server/) \[1\] and a [video](https://youtu.be/qtPP7kZbriQ) \[2\], the [FEPCOS-J development tool](http://fepcos.info/en/fepcos-j.html) \[3\] prototypically implements a Java language extension that allows implementing a client-server application in Java without the need for manual network programming.

In a nutshell, FEPCOS-J allows a Java developer to program a server's system specification declaratively using annotations. The FEPCOS-J Processor ***fjp*** uses this specification to generate the server's system export module and system import module.

To explain, combined with the FEPCOS-J Exporter ***fjx***, the system export module is a multithreaded Java server that provides its services via an IPv4 network. Further, the system import module contains a system interface that allows Java-programmed clients to access the server via the network.

The key feature of FEPCOS-J is that it enables client-server communication by supplying Java modules as well as generating Java code. In other words, it frees a developer from network programming. See my other posts \[4,5,6\] for details.

So far, I pointed out FEPCOS-J's capability to support the implementation of a Java server accessed via a Java client. However, FEPCOS-J is capable of more!

As [FEPCOS-J is model-based](http://fepcos.info/en/fepcos-model.html) \[7\] and employs profiling techniques researched in the fields of autonomous services \[8,9\] and wireless sensor networks \[10\], it is possible to use FEPCOS-J to implement a Java server and generate its system profile.

Other tools can then use this profile as input to realize the client-network communication for other programming languages, such as [Rust](https://www.rust-lang.org/) \[11\].

This post illustrates this concept using the example of a Java server accessed by a Rust client. Firstly, the post [describes the principle](#principle). Then, it [provides an example](#example). Finally, it [draws a conclusion](#conclusion).

Java server and Rust client implemented using tools from the FEPCOS-Project {#principle}
----------------------------------------------------------------------------------------

This section describes how to implement a Java server and a Rust client without manually network programming (Fig. 1).

For this purpose, a developer uses FEPCOS-J and the working draft of FEPCOS-R, which so far consists of a Rust library and the ***frg*** tool (FEPCOS-R Generator).  
![Java server and Rust client implemented using tools from the FEPCOS-Project: fjp = FEPCOS-J Processor, fjx = FEPCOS-J Exporter, frg = FEPCOS-R Generator, \[java\] = Java source code, \[jar\] = modular jar file, \[rs\] = Rust source code, \[rlib\] = Rust library, \[fsp\] = FEPCOS system profile.](/images/posts/2024/10/fuchs2024-fepcos-j-java-server-and-rust-client/fuchs2024-java-server-and-rust-client-implemented-with-fepcos-tools.png) **Fig. 1) Java server and Rust client implemented using tools from the FEPCOS-Project:** ***fjp*** = FEPCOS-J Processor, ***fjx*** = FEPCOS-J Exporter, ***frg*** = FEPCOS-R Generator, *\[java\]* = Java source code, *\[jar\]* = modular jar file, *\[rs\]* = Rust source code, *\[rlib\]* = Rust library, *\[fsp\]* = FEPCOS system profile.

The developer:

* firstly uses the FEPCOS-J annotations to implement the server's system specification in Java.
* secondly runs `fjp --profile`, which generates the server's system export module (*server.exp*, a modular Jar file) and the server's system profile.
* thirdly runs `frg` to generate the server's system import module (*server_imp*), a Rust library.
* fourthly implements the client using the Rust programming language.
* fifthly uses `rustc` to compile the Rust client into an executable binary.

To start the Java server, the developer runs `fjx` utilizing *server.exp*. After that, the developer can run the Rust client's executable binary.

As a result, the Rust client accesses the Java server via the ***frg*** -generated *server_imp* Rust library, the Rust library provided by FEPOCS-R, the network, the Java modules provided by FEPCOS-J, and the ***fjp*** -generated *server.exp* Java module.

Java server and Rust client implemented as examples {#example}
--------------------------------------------------------------

The following describes the implementation of the example scenario shown in Fig. 2: In brief, a Java server called *test.server* provides two services called *add()* and *greet()* via the internet socket *10.0.0.6:8888* , and a Rust client called *test_client* accesses these services via the network.

In other words, I explain the server's programming using the Java programming language and the client's programming using the Rust programming language.

With this in mind, the interaction of the tools from the FEPCOS-Project becomes obvious.  
![A Java server called test.server provides two services add(…) and greet(…) via the internet socket 10.0.0.6:8888. A Rust client called test_client accesses these services via the network.](/images/posts/2024/10/fuchs2024-fepcos-j-java-server-and-rust-client/fuchs2024-java-server-and-rust-client-example.png) **Fig. 2) Example scenario:** A Java server provides two services to a Rust client via the network.

### Implementing the Java server {#h3-3-implementing-the-java-server}

#### Workflow

To implement the Java server, the developer uses FEPCOS-J as illustrated in Fig. 3:  

<figure class="aligncenter size-full is-resized">
 <img decoding="async" width="1024" height="512" src="/images/posts/2024/10/fuchs2024-fepcos-j-java-server-and-rust-client/fuchs2024-java-server-implementation-with-fepcos-j-example.png" alt="Using FEPCOS-J to implement a Java server called test.server and generate its system profile: a) The project directory is named test.server; b) the src-folder includes the server's Java code; c) executing fjp with the --profile parameter generates the server's system profile (d)." class="wp-image-114417" style="width:836px;height:auto">
 <figcaption class="wp-element-caption">
  <strong>Fig. 3) Using FEPCOS-J to implement a Java server called <em>test.server </em>and generate its system profile:</strong> <strong>a)</strong> The project directory is named <em>test.server</em>; <strong>b)</strong> the <em>src</em>-folder includes the server's Java code; <strong>c)</strong> executing <strong><em>fjp</em></strong> with the <em>--profile</em> parameter generates the server's system profile <strong>(d)</strong>.
 </figcaption>
</figure>

Firstly, the developer creates the project directory *test.server* with all sub folders and changes into the project directory.

Secondly, the developer uses FEPCOS-J's annotations to program the server in Java.

Finally, the developer runs

`fjp --profile`.

As a result, the *tgt* directory contains:

* the server's system profile, `test.server.fsp`;
* the server's system export module, `test.server.exp.jar`;
* the server's system import module, `test.server.imp.jar`;
* the server's system documentation `test.server.imp-doc.zip`.

As I have shown in my [previous post](https://foojay.io/today/fuchs-2024-fepcos-j-multithreaded-server/) \[1\], the *test.server.imp.jar* and *test-server.imp-doc.zip* files could be used to implement a Java client. But as this post focuses on programming a Rust client, it ignores these files in the following.

#### Source code of the Java server

The developer uses Java and FEPCOS-J's annotations to program the server's system specification, which consists of the source code listed below:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="test.server/src" data-enlighter-group="g1">test.server/
└── src
    ├── module-info.java
    └── test
        └── server
            ├── AddService.java
            ├── GreetService.java
            └── Server.java

4 directories, 4 files</pre>

The following briefly explains the example's source code.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="module-info.java" data-enlighter-group="g2">module test.server {
    requires static fepcos.j.annotation;
}</pre>

`module-info.java` is the module descriptor. It specifies the server's name, *test.server*.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="Server.java" data-enlighter-group="g3">package test.server;

import fepcos.j.annotation.*;

@SYDec("A simple server.")
public class Server {
    @Cap AddService add;
    @Cap GreetService greet;
}</pre>

`Server.java` is the server's system declaration. It declares the *add()* and *greet()* services to be implemented as *AddService* class or *GreetService* class, respectively.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="AddService.java" data-enlighter-group="g4">package test.server;

import fepcos.j.annotation.*;

@AYSpec("Adds two numbers.")
class AddService {
    @In("1st summand") int x;
    @In("2nd summand") int y;

    @Out("The sum z=x+y") int z;

    @Behavior
    void go() { z = x+y; }
}</pre>

`AddService.java` is the *add()* service's activity specification. It specifies the input parameters, *int x* and *int y* . Further, it specifies the output parameter, *int z* , to be calculated as `z=x+y`.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="GreetService.java" data-enlighter-group="g5">package test.server;

import fepcos.j.annotation.*;

@AYSpec("Greets the user in English and German.")
class GreetService {
    @In("Name of the user") String user;

    @Out("Greetings in English.") String greet_en;
    @Out("Greetings in German.") String greet_de;

    @Behavior
    void go() {
        greet_en = "Hello, " + user + "!";
        greet_de = "Hallo " + user + "!";
    }
}</pre>

`GreetService.java` is the *greet()* service's activity specification.

It specifies the input parameter, *String user*.

Further, it specifies the output parameters, *String greet_en* , and *String greet_de* . The first becomes the English greet: `greet_en = "Hello, " + user + "!"`. The second becomes the German greet: `greet_de = "Hallo " + user + "!"`.

### Implementing the Rust client {#h3-4-implementing-the-rust-client}

#### Workflow

Fig. 4 depicts the implementation of the Rust client, which accesses the Java server implemented as shown above via the network.  
![Using the FEPCOS-J-generated system profile to implement a Rust client called test_client: a) test_client is the project directory's name; b) using scp to fetch the server's system profile, test.server.fsp; c) the frg tool processes test.server.fsp and generates the server's system import module, test_server.rs, which is automatically compiled to libtest_server_imp.rlib; d) implementing the client, test_client.rs; e) using rustc to compile the client; f) the result is the test_client executable binary.](/images/posts/2024/10/fuchs2024-fepcos-j-java-server-and-rust-client/fuchs2024-rust-client-implementation-with-fepcos-r-example.png) **Fig. 4) Using the FEPCOS-J-generated system profile to implement a Rust client called *test_client*: a)** *test_client* is the project directory's name; **b)** using ***scp*** to fetch the server's system profile, *test.server.fsp* ; **c)** the ***frg*** tool processes *test.server.fsp* and generates the server's system import module, *test_server.rs* , which is automatically compiled to *libtest_server_imp.rlib* ; **d)** implementing the client, *test_client.rs* ; **e)** using ***rustc*** to compile the client; **f)** the result is the *test_client* executable binary.

The developer:

* firstly creates the project directory *test_client* and changes into it.
* secondly runs ***scp*** to fetch the server's system profile, *test.server.fsp*, from the server.
* thirdly runs ***frg*** to process *test.server.fsp* and generate the Rust source code (*test_server_imp.rs* ) and the Rust library (*libtest_server_imp.rlib*) of the server's system import module.
* fourthly implements the client *test_client.rs* in Rust using *libtest_server_imp.rlib*.
* finally used ***rustc*** to compile *test_client.rs* into the executable binary *test_client*.

#### Source code of the Rust client

<pre class="EnlighterJSRAW" data-enlighter-language="rust" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="test_client.rs" data-enlighter-group="">extern crate test_server_imp;

use test_server_imp::S;

fn main() {
    let sy : S = S{ addr: "10.0.0.6:8888" };

    let r1 = sy.add(14, 23).unwrap();
    println!("{:?}", r1.z);

    let r2 = sy.greet(String::from("Bob")).unwrap();
    println!("{:?}", r2.greet_en);
    println!("{:?}", r2.greet_de);
}</pre>

To implement the Rust client, the developer requires the previously generated

`extern crate test_server_imp;`

to

`use test_server_imp::S;`

which is the server's system interface.

Within the main function `fn main() { ... }`, the line

`let sy : S = S{ addr: "10.0.0.6:8888" };`

firstly instantiates this system interface, specifying the socket address `10.0.0.6:8888` at which the server listens for incoming requests. Next,

`let r1 = sy.add(14, 23).unwrap();`

requests the server's `add()` service, specifying the input parameters `x=14` and `y=23`. The result is stored in the variable `r1`, a struct with the filed `r1.z`, the service's *z* output parameter, printed afterwards. Finally,

`let r2 = sy.greet(String::from("Bob")).unwrap();`

requests the server's `greet()` service, specifying the input parameter `user="Bob"`. The result is stored in the variable `r2`, a struct with the `r2.greet_en` and `r2.greet_de` fields. These fields represent the corresponding output parameters of the service and are then printed.

### Executing the client-server application {#h3-5-executing-the-client-server-application}

First, the developer starts the Java server on a computer named *lumo* by executing

`fjx tgt test.server.exp 10.0.0.6:8888`

in the project directory, as shown in Fig. 5.

To explain, ***fjx*** is the FEPCOS-J Exporter, *tgt* is the directory containing the generated server's system export module, *test.server.exp* is the name of the system export module, and *10.0.0.6:8888* is the internet socket at which the server listens for incoming requests.  
![A screenshot of a Linux shell shows the execution of an example server implemented with FEPCOS-J.](/images/posts/2024/10/fuchs2024-fepcos-j-java-server-and-rust-client/fuchs2024-java-server-execution-example.png) **Fig. 5) Executing the example Java server implemented with FEPCOS-J.**

After that, the developer runs the Rust client on a computer named *box* by executing the ***rustc*** -built ***test_client*** binary, as shown in Fig. 6.

The ***test_client*** binary prints *37* (=14 + 23), *"Hello, Bob!"* and *"Hallo Bob!"* as expected.  
![A screenshot of a Linux shell shows the execution of an example Rust client that accesses a Java server implemented with FEPCOS-J.](/images/posts/2024/10/fuchs2024-fepcos-j-java-server-and-rust-client/fuchs2024-rust-client-execution-example.png) **Fig. 6) Executing the Rust client that accesses a Java server implemented with FEPCOS-J.**

Java server and Rust client can be implemented without manually network programming {#conclusion}
-------------------------------------------------------------------------------------------------

To sum up, this post introduced FEPCOS-J's feature to generate a system profile out of a Java server's system specification. In addition, the post introduced the working draft of FEPCOS-R, which can process the system profile and generate the system import module for a Rust client.

As an illustration, the post gave an example using FEPCOS-J for programming a Java server that provides two services. The ***fjp*** tool processed the Java code and generated the server's system profile.

Next, the ***frg*** tool processed this profile and generated a Rust library. This library contained the server's system interface for a Rust client programmed using the Rust programming language afterwards.

Finally, the execution of the implemented client-server application was as expected.

This post contains the complete source code of the implemented example. When you look at it, you will see that there was no need for manually network programming.

The FEPCOS-J and FEPCOS-R development tools automated the network programming required to implement a Java server that is accessed by a Rust client.

References {#references}
------------------------

1. G. Fuchs: *FEPCOS-J (4) Easy programming of a multithreaded TCP/IP server in Java* ; At: Foojay Today; 2024-03-21; <https://foojay.io/today/fuchs-2024-fepcos-j-multithreaded-server/>.
2. G. Fuchs: Video: *Easy Implementation of a Client-Server Application in Java with FEPCOS-J* ; On: YouTube, FEPCOS-Project (@FepcosInfo); 2024-07-29; <https://youtu.be/qtPP7kZbriQ>.
3. Fepcos-Project: *FEPCOS-J* ; <http://fepcos.info/en/fepcos-j.html>.
4. G. Fuchs: "*FEPCOS-J (1) -- Description, Impressions of Usage, Current State* "; <https://foojay.io/today/fuchs-2023-fepcos-j-01/>.
5. G. Fuchs: "*FEPCOS-J (2) -- Declaratively compose networked systems in Java* "; <https://foojay.io/today/fuchs-2023-fepcos-j-02/>.
6. G. Fuchs: "*FEPCOS-J (3) -- Build native executables of Java-coded networked systems* "; <https://foojay.io/today/fuchs-2023-fepcos-j-03-native-executables/>.
7. Fepcos-Project: *FEPCOS-Model* ; <http://fepcos.info/en/fepcos-model.html>.
8. S. Truchat, G. Fuchs, S. Meyer, and F. Dressler: *An Adaptive Model for Reconfigurable Autonomous Services using Profiling* ; In: International Journal of Pervasive Computing and Communications (JPCC), Special Issue on Pervasive Management, 2.3 (2006), pp. 247-260; [doi: 10.1108/17427370780000154](https://doi.org/10.1108/17427370780000154).
9. G. Fuchs: *Profiling von Mobilen Autonomen Diensten - Theorie und Konzepte am Beispiel eines Prototyps*; VDM Verlag Dr. Müller, Saarbrücken, DE-SL; 2008; ISBN:978-3-639-06501-5 (in German).
10. G. Fuchs, S. Truchat, and F. Dressler: *Distributed Software Management in Sensor Networks using Profiling Techniques* ; In: Proceedings of the 1st IEEE/ACM International Conference on Communication System Software and Middleware (COMSWARE): 1st International Workshop on Software for Sensor Networks (SensorWare); New Delhi, IN-DL; IEEE, 2006; [doi: 10.1109/COMSWA.2006.1665225](https://doi.org/10.1109/COMSWA.2006.1665225).
11. Rust Team: *Rust* ; <https://www.rust-lang.org/>.

All references were last accessed on October 1, 2024.
