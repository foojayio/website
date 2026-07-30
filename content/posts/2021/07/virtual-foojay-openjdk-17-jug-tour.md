---
title: "Announcement: Virtual Foojay OpenJDK 17+ JUG Tour"
slug: "virtual-foojay-openjdk-17-jug-tour"
date: "2021-07-21T16:07:16+00:00"
lastmod: "2022-05-19T19:11:32+00:00"
description: "During September and October, contributors to Foojay will make a whistle stop tour through as many JUGs as possible, via virtual meetups!"
authors:
  - "ari-waller"
  - "geertjan-wielenga"
  - "jadon-ortlepp"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "Campaigns"
  - "Pi4J"
  - "Release Notes"
tags:
related_posts:
frozen: false
---

To celebrate OpenJDK 17 and Foojay as a community platform for its users, we're kicking off the Virtual Foojay OpenJDK 17+ JUG Tour, focused on OpenJDK 17, plus more!

Ari Waller, JFrog's Event Manager, well known to many JUGs, will continue to wear a second hat, as he did on the previous tour---that of Foojay Event Manager, together with Jadon Ortlepp from Payara, newly added to the Foojay Event Team!

During September and October, contributors to Foojay will be making a whistle stop tour through as many JUGs as possible, via their virtual meetups! (And if you're not holding virtual events or don't have the facilities for this, we can support by making these available as needed and some of the sessions can be done live in-person, depending on the context.)

At each stop of the tour, there'll be a brief introduction to Foojay.io followed by a session of 45 minutes on a topic connected to OpenJDK 17, and beyond, presented by a Foojay community member, as listed below.
> The JUG can pick the topic of their choice and anyone reading this and wanting their JUG to be involved should contact Ari (ariw at jfrog dot com and/or hello at foojay dot io) and suggest dates, ideally during September and October, when they'd like to host the Foojay program!

Here's the (growing) roster of topics!

**Note:** **Abstracts that have already been booked are marked as such below and, in some cases, an abstract may be available for more than one JUG, though ideally all abstracts would be assigned before assigning sessions more than once.**

### Java Core {#h3-0-java-core}

*** ** * ** ***

**OpenJDK 17: Get Ready for the Next LTS Java** , *Simon Ritter (Azul* ).   

With the release of OpenJDK 17, all OpenJDK distributions will be providing long-term support (LTS) for this version of the Java platform. Many Java users currently running on OpenJDK 8 or OpenJDK 11 will want to migrate their production environments to OpenJDK 17. This will enable them to take advantage of the numerous new features and enhancements made possible by the six-month release cadence introduced in 2017.

This session will provide details of changes to the Java platform covering OpenJDK 12 to 17. Although many things have been added, some have also been removed. We'll highlight these things and explain how they may impact application migration. We'll cover all aspects of the OpenJDK: the Java language, core APIs, the JVM and tooling and other OpenJDK-specific features, including Switch expressions (OpenJDK 12), Text blocks (OpenJDK 13), Records (OpenJDK 14), Pattern matching, for instanceof (OpenJDK 14), Sealed classes (OpenJDK 15), and Pattern matching for switch (OpenJDK 17), as well as, on the API side, Foreign-Memory Access API (OpenJDK 14), Vector API (OpenJDK 16), and Foreign Linker API (OpenJDK 16).

By the end of this session, you'll be all set to take advantage of all the modern Java features!

**Status: Booked September 9, St. Louis JUG (USA)**

*** ** * ** ***

**Pattern Matching \& Sealed Classes: Best Features of OpenJDK 17?** *Deepu K Sasidharan (JHipster)* .   

Let's have a quick rundown of the two most exciting features landing in OpenJDK 17!

Pattern matching in Java is almost complete now. OpenJDK 16 added support for pattern matching in the `instanceof` operator and now this can be used in switch cases to perform idiomatic pattern matching, as in many other modern languages.   

Let's see all the possibilities with it first and then we will look at the new Sealed classes and interfaces that let you restrict inheritance and learn how they can be useful in pattern matching.

**Status: Booked September 23, Silesia JUG (Poland)**

*** ** * ** ***

**Say 'No' to JNI,** *Carl Dea (Azul)*

As a Java developer, you may have a need to access native libraries, such as Tensorflow, SqlLite, ffmpeg, OpenGL, but later find that JNI is your default choice. JNI (Java Native Interface) requires native code to be installed. You'll quickly find that JNI wrapper code is difficult to maintain.

New to OpenJDK 17 is the Foreign Linker API (JEP 389) as a replacement for JNI to provide a pure-Java solution and perform comparable to, or better than, JNI.

The aim of this talk will be to provide a friendly introduction to OpenJDK 17's Foreign Linker API.

**Status: Booked September 21, KnoxJava (USA)**

*** ** * ** ***

**Securing and Exploiting Java Applications** , *Erik Costlow (Contrast Security)*

OpenJDK 17 makes the interesting decision that deprecating a security feature (the SecurityManager) can actually improve security of the platform and running applications, setting out a path to remove a feature that hasn't been used and hasn't blocked many exploits.

By understanding how modern Java applications are attacked, teams can better position the right defense in the right location. This talk will analyze exploits against several Java applications that were used in the wild and lay out the proper security defense that can defend applications from being breached, not only to mitigate these threats but also to address time spent on internal security audits.

We will lay out where different defense and monitoring capabilities have gone, including new features such as serialization filters and OpenJDK Flight Recorder.

*** ** * ** ***

****Data Science on the JVM with Kotlin and Zeppelin**** , *Pratik Patel (Azul)*

The world of Data Science heavily uses Python and Python libraries such as NumPy and Pandas. While Python is a great platform, it does have some drawbacks - one of which is performance. As Java developers, we enjoy the familiarity of the JVM and the constellation of tools and libraries available for this high-performance platform.  

In this session, Pratik will introduce you to Data Science using the popular Kotlin language that runs on the JVM. We'll do this using an interactive platform called Apache Zeppelin. Similar to Jupyter Notebooks, Zeppelin allows you to write code, formatted text, and use a myriad of plugins to process, analyze, and display data. With its integration with Spark, you can also prototype and develop solutions for Big Data in a fun and interactive way!

*** ** * ** ***

**Are All OpenJDK Builds Created Equal?** , *Simon Ritter (Azul*).

Each release of Java Standard Edition (SE) has its own project under the OpenJDK. The source code is licensed under GPL v2 with the Classpath exception, meaning that anyone can download it, build it, and distribute the resulting binaries.

If everyone is using the same source, then all distributions will be the same, surely? Well, that's not guaranteed: even something as simple as a change in the version of a compiler could significantly impact how the executable works.

Luckily, part of the Java SE specification, as provided through individual Java Specification Requests (JSRs), is a test suite, commonly referred to as the TCK (Technology Compatibility Kit). Depending on which version of Java is involved, there are up to a hundred and fifty *thousand* tests.

In this session, we'll explore how the TCK can provide a very high level of assurance that your application will run in the same way on any TCK-tested build of the OpenJDK.

By the end, you'll have a clear understanding of the benefits of OpenJDK certification.

**Status: Booked September 28, Garden State JUG (USA)**

<br />

*** ** * ** ***

**Your Java Code in the Fastlane: Creating a Million Virtual Threads Using Project Loom to Improve Throughput** , *Bazlur Rahman (Contrast Security)*   

Project loom introduces virtual threads, lightweight threads that aim to dramatically reduce the effort of writing, maintaining, and monitoring high-throughput concurrent applications with the Java platform.

We need threads to achieve high throughput. However, threads are not cheap and are limited in number. To get around this problem, various alternatives such as the reactive programming style have emerged. These techniques bypass creating a lot of threads at the expense of more difficult debugging. This makes developers grumpy. However, with virtual threads, we get the best of both worlds, cheap, lightweight threads and easy debugging, which would make developers happy again.

This talk will explore what virtual threads are, how they are implemented, how they solve our modern problems and what, if any, shortcomings there are.  

**Status: Booked July 19, 2022, PhillyJUG (USA)**

### Java Cloud {#h3-1-java-cloud}

*** ** * ** ***

***Getting Started with Jakarta EE*** , *Rudy De Busscher (Payara).*

With the release of Jakarta EE 9.0, for the first time, a major breaking change is made, the change of the namespace. This allows the evolution of the Enterprise Platform under the wings of the Eclipse Foundation. Although Java Enterprise has a history of more than 20 years, it is also experiencing a new start now that it is managed by the Eclipse Foundation.

In this session, we will cover the basics of setting up a Jakarta EE 9 application, creating REST endpoints, accessing the database, and creating a rich user interface. You will learn that it is not only easy to get your application running on an Application Runtime, but bringing it into a containerized environment and Kubernetes is just as easy as writing a simple Hello World application.

*** ** * ** ***

**Enhanced Java Elasticity with OpenJDK 17** *, Ruslan Synetsky (Jelastic)* .  

Recent OpenJDK and Garbage Collection technology improvements led to enhancing Java elastic vertical scaling and optimizing the resource consumption. Now JVM can promptly return unused memory and, as result, it can be scaled up and down automatically. In addition, less memory is used for class metadata.

In this session, we'll cover the main achievements in vertical scaling of Java, as well as share tuning details of different GCs. You'll find out what metrics should be tracked in order to meet the load requirements of the application, and how to finetune scaling triggers in order to efficiently handle different load levels. Also, we'll demonstrate how to smoothly upgrade to OpenJDK 17 using Jelastic PaaS.

Join the session to find out how to keep Java up to date, as well as make your cloud environments more flexible and adjustable to the load while lowering total cost of ownership.

*** ** * ** ***

**Leveraging OpenJDK 17 to Create End-to-End JavaFX-to-Cloud applications** , *Johan Vos (Gluon)* .   

Real world applications don't live in silos. Cloud services process data coming from client systems. Client applications generate tons of data that need to processed by enterprise applications. The Java platform works on both enterprise and cloud systems, as well as on client systems. A client application with a modern user interface created with JavaFX can leverage modern OpenJDK 17 APIs, so that they can integrate with Java Cloud applications and share some code.   

In this session, we'll show real-time data synchronization between clients and between clients and a Cloud service; remote function invocation, where a client invokes a serverless function in a serverless container; data processing on a backend system, real-time connected with a user interface on a client.

*** ** * ** ***

**Cloud-Native Java in Times of OpenJDK 17** , *Clement Escoffier (Red Hat)* and*Georgios Andrianakis (Red Hat)* .   

OpenJDK 17 introduces plenty of features that significantly ease writing modern Java applications. This talk explores how Quarkus applications can adopt OpenJDK 17 features.   

Come see how records, pattern matching, and sealed classes help build Cloud Native Java applications and how to package and run these applications in containers.

*** ** * ** ***

**Leveraging OpenJDK 17 features with Jakarta EE,** *Ivar Grimstad (Eclipse Foundation)* .   

Jakarta EE 9 lowered the barriers of entry and established a foundation for future innovation paving the way for Jakarta EE 10. You have probably heard that the minimum runtime supported by Jakarta EE 10 will be Java SE 11. That means that the APIs will be able to use OpenJDK 11 language features, and will be compiled to OpenJDK 11. This also applies to the TCK. However, in Jakarta EE 9.1, the signature tests were updated to be able to test across multiple Java levels. This means that compatible implementations of Jakarta EE 10 may certify using OpenJDK 17.   

In this session, I will show how easy it is to get started using OpenJDK 17 features in a Jakarta EE application.

*** ** * ** ***

**Jakarta EE: Present and Future** , *Reza Rahman (Microsoft)* .   

Let's explore the current state and future of Jakarta EE, the technology platform formerly known as Java EE. We will include a high level feature tour of the current version, Jakarta EE 9. We will also discuss how the community can contribute to Jakarta EE. The technical content of Jakarta EE 8 is mostly the same as Java EE 8, with Jakarta EE 9 further bringing the platform into the open by decoupling from the javax namespace to the jakarta namespace. Jakarta EE 10 opens possibilities for many long pending innovations in key technologies like Jakarta Security, Concurrency, Messaging, REST, Persistence, Batch, NoSQL, MVC and Configuration.   

You should come to this session with your thinking caps on and your sleeves rolled up. There is much to help move forward together that really matters.

**Status: Booked October 14, St. Louis JUG (USA)**

*** ** * ** ***

**Why Jakarta EE Matters** , *Ryan Cuprak (Dassault Systems)*

Jakarta EE is now over 20 years old and despite its age, it is as relevant today as it was back in 1999. It is one of the few open standards for developing enterprise applications with multiple independent vendor implementations. Its APIs are central to developing Java based cloud solutions. It is as relevant today as it was back in 1999.

This presentation will provide context to Jakarta EE and why businesses choose to use it!

*** ** * ** ***

**7 Reasons to Switch to OpenJDK 17 as a Jakarta EE Developer** , *Rudy De Busscher (Payara).*

JDK 17, the next LTS version of Java, is around the corner and in this session, we have a look at what a Jakarta EE developer will find interesting. Besides some new language constructs, there are many operational improvements like higher performance.

Learn about these features and improvements including Records, Text blocks, Garbage collection improvements, and monitoring through Flight Recorder in several live demos.

### JavaFX {#h3-2-javafx}

*** ** * ** ***

**One Codebase, Six Platforms: JavaFX 17 on Every Client** , *Johan Vos (Gluon)* .   

JavaFX follows the release cadence defined by the OpenJDK project. Every 6 months, a new major version of JavaFX is released.

In this session, we talk about the new features and fixes that are introduced in JavaFX 17. The JavaFX 17 release contains a record number of fixes and features. The primary focus of the OpenJFX project, which serves as the codebase for JavaFX, is to provide a stable, mature and relevant set of API's. A number of long standing bugs has been fixed, and JavaFX can be used on modern versions of Operating Systems and architectures (including Apple Silicon systems).   

You will learn how the latest version of JavaFX can be used in conjunction with the latest Java release and how to create modern user interfaces that are cross-platform and that can be deployed on desktop, mobile, and embedded devices.

*** ** * ** ***

**FXGL 17: Roadmap for the Future of JavaFX Game Development** , *Almas Baimagambetov (University of Brighton)* .  

FXGL 11 has been a success among JavaFX developers, leveraging high-performance cross-platform support. FXGL seamlessly extends JavaFX to bring support for real-world game development concepts and techniques, which can be used in both Java and Kotlin.  

In this session, we will cover the latest features the current version offers and outline key milestones for FXGL 17.

### Raspberry Pi {#h3-3-raspberry-pi}

*** ** * ** ***

**Current state of Java, JavaFX, and Pi4J on the Raspberry Pi** , *Frank Delporte (Toadi)* .   

Raspberry Pi OS comes with Java 11 pre-installed, making it the ideal inexpensive computer to run Java programs.

But did you know that the Raspberry Pi is also a perfect match for running the latest JavaFX user interface applications or FXGL games? Combined with the Pi4J library, you can even do things which aren't possible with your expensive developer computer: blink a LED, read sensor values, and control all types of electronic components.   

In this session, you will be introduced to all of these topics with references and examples to get you started and learn more.

**Status: Booked August 26, Manchester JUG (UK)** and **Booked September 27, Jozi JUG**

*** ** * ** ***

**100% Pure Java on the Raspberry Pi** , *Dieter Holz (FHNW University of Applied Sciences)* .   

Nowadays, most programming on the Raspberry Pi is done in other programming languages than Java, mostly "It, That Cannot Be Named". What are the forces of evil that brainwashed the cool kids in schools and universities to think that Java is for baby boomers only?

From an educational point of view, it's important to use a single programming language in the first year of education. With the Raspberry Pi, we have a great platform to let our students experiment with many different areas of programming. They gain experience in Object-Oriented Programming, GUI development, game development, accessing hardware components, such as physical buttons, joysticks, sensors, and even cameras. All this in a single programming language, in 100% pure Java.

And you just need the OpenJDK and four libraries: JavaFX, FXGL, Pi4J, and JavaCV. Period. That's all. Right? No! Having these few technology gems is not sufficient to have real fun with programming. The initial hurdles faced by students before starting to write their first FXGL-based game and to deploy it on a Picade Console or a Game HAT need to be as low as possible. After the first steps, a smooth development experience is a must. Programmers want to focus on programming. Nothing else.   

In this talk, you will see all the things that are needed besides the available libraries---ready made Linux images, Maven based, well documented template projects that enable development on laptops, and remote starting and debugging of apps on the Raspberry Pi, while you will also be shown the best practices of how to combine, for example, a GUI with a PUI (Physical User Interface). The fun is back. And the fun is 100% pure Java.

### Miscellaneous {#h3-4-miscellaneous}

*** ** * ** ***

**From Java Records to Quantum Computing** *, Johan Vos (Gluon)* .   

Quantum Computing is getting more and more attention. While the commercial availability of real, general-purpose hardware is still limited, the potential is huge, in many areas including encryption, optimisation, chemistry, and physics. Quantum Computers need to be programmed, similar to how classical computers are programmed.   

In this session, we explain the major differences between classical computing and quantum computing. We discuss how Java can be used to create quantum applications, and we talk about potential improvements in Java bytecode that could leverage quantum concepts. Most quantum computing software research currently uses quantum simulators, where a real quantum computer is simulated using a classical computer. This creates major challenges related to memory and computing resources. We will discuss how improvements in project Panama, or using Records can improve the performance of Java for quantum computing simulators.  

**Status: Booked September 16, Coimbra JUG (Portugal)**
