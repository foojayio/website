---
title: "Design Patterns Update to JDK25"
date: "2025-09-24T11:29:34+00:00"
lastmod: "2025-09-24T11:30:10+00:00"
description: "Java 25 represents a notable milestone, empowering developers to leverage enhanced switch statements to effectively address implementation challenges across Creational, Structural, and Behavioral design patterns."
authors:
  - "miro-wengner"
image: "116749_image_676x380.webp"
categories:
  - "Agile"
  - "Java"
  - "Java Beginner"
  - "Java Core"
tags:
related_posts:
  - "ai-newsletter-1"
  - "jc-ai-newsletter-2"
  - "jc-ai-newsletter-3"
  - "jc-ai-newsletter-4"
frozen: false
---

**T** he information technology landscape is changing rapidly. This is not only due to the utilization of AI and [new methodologies \[8\]](https://foojay.io/today/stochastic-ai-agility-breaking-cycles-of-debt/ "new methodologies"), or business requirements trying to keep up, but also because of the incremental complexity of expected solutions.

Statistics and probability are slowly beginning to play a very important role in this evolution. The Java ecosystem has proven its maturity many times by delivering essential features that businesses require, although many may argue that these have been inspired by other existing solutions.

From this perspective, JDK 25's release can be viewed as an exemplary breakthrough that delivers essential features through multiple ongoing internal projects like:

1. *Babylon, Loom*   
   a. [Babylon \[1\]](https://openjdk.org/projects/babylon/ "Babylon") aims to extend better support for external programming languages like SQL, machine learning, GPU utilization  
   b. [Loom \[2\]](https://wiki.openjdk.org/display/loom/Main "Loom") aims provide better approach of handling concurrency through Virtual Thread, Scoped Values or Structured Concurrency framework
2. *Leyden, Lilliput*   
   a. [Leayden \[3\]](https://openjdk.org/projects/leyden/ "Leayden [3]") aims to optimize Java platform startup time and memory footprint using Ahead-of-Time (AOT) techniques to reduce Just-In-Time (JIT) compilation  
   b. [Lilliput \[4\]](https://openjdk.org/projects/lilliput/ "Lilliput [4]") explores possibilities of downsizing the Java object headers from 128 bits to 64 bits or less (HotSpot JVM). Such footprint reduction aims to improve performance across the workloads.
3. *Panama, Valhalla*   
   a. [Panama \[5\]](https://openjdk.org/projects/panama/ "Panama [5]") aims to optimize interoperability between Java and native code through Vector API or Foreign Function and Memory (FFM) API with goal enabling more efficient applications  
   b. [Valhalla \[6\]](https://openjdk.org/projects/valhalla/ "Valhalla [6]") aims to optimize value types to increase Java platform performance through allowing better handling of null types

**Java 25** represents a notable milestone, empowering developers to leverage enhanced switch statements to effectively address implementation challenges across Creational, Structural, and Behavioral design patterns.

This release demonstrates a dramatic shift toward functional programming approaches for solving complex challenges (maintainable and testable codebase, security). Code readability has become increasingly self-explanatory, clearly conveying the underlying purpose of the implementation.

Although JDK 25 may be perceived as a release that simplifies the execution of the `main()` method and related classes without compilation requirements, its impact extends far beyond these surface-level improvements due to the comprehensive collection of included JEPs (see JEPs LTS journey bellow).

```
public interface Vehicle {
    void move();
    default void printStatus(){
        checkMoving();
    }
    private void checkMoving(){
        System.out.println("checking status");
    }
}

void main(){
    var vehicle1 = new CommonVehicle("super_engine");
    var vehicle2 = new OtherVehicle(42);
    var vehicle3 = new UnknownVehicle();
    List<Vehicle> vehicles = Arrays.asList(vehicle1, vehicle2, vehicle3);

    vehicles.forEach(v -> {
        switch (v){
            case OtherVehicle(Integer horsePowers) -> System.out.println("OtherVehicle with horsePowers:" + horsePowers);
            case CommonVehicle(String engine) -> System.out.println("CommonVehicle with engine:"+ engine);
            default -> System.out.println("not implemented, v:" + v);
        }
    });
}
```

I have updated the source code for my book "[Practical Design Patterns for Java Developers \[7\]](https://github.com/mirage22/Practical-Design-Patterns-for-Java-Developers/tree/main-update-j25 "Practical Design Patterns for Java Developers [7]")" to JDK 25, maintaining these examples in a separate branch to ensure the code samples referenced in the book remain accessible.

### JEPs LTS journey (focus on coding but not only):

**note** : *Stream API, Lambda Expressions, and Java Platform Module System are considered to be previous milestones*

JDK17 LTS:  
[JEP-409: Sealed Classes](https://openjdk.org/jeps/409 "JEP-409: Sealed Classes")  
[JEP-415: Context-Specific Deserialization Filters](https://openjdk.org/jeps/415 "JEP-415: Context-Specific Deserialization Filters")  
[JEP-390: Warnings for Value-Based Classes](https://openjdk.org/jeps/390 "JEP-390: Warnings for Value-Based Classes")  
[JEP-394: Pattern Matching for instancesof (since JDK16)](https://openjdk.org/jeps/394 "JEP-394: Pattern Matching for instancesof (since JDK16)")  
[JEP-371: Hidden Classes (since JDK15)](https://openjdk.org/jeps/371 "JEP-371: Hidden Classes (since JDK15)")  
[JEP-378: Text Blocks (since JDK15)](https://openjdk.org/jeps/378 "JEP-378: Text Blocks (since JDK15)")  
[JEP-349: JFR Event Streaming (since JDK14)](https://openjdk.org/jeps/349 "JEP-349: JFR Event Streaming (since JDK14)")  
[JEP-352: Non-Volatile Mapped Byte Buffers (since JDK14)](https://openjdk.org/jeps/352 "JEP-352: Non-Volatile Mapped Byte Buffers (since JDK14)")  
[JEP-358: Helpful NullPointerExceptions (since JDK14)](https://openjdk.org/jeps/358 "JEP-358: Helpful NullPointerExceptions (since JDK14)")  
[JEP-361: Switch Extension (since JDK14)](https://openjdk.org/jeps/361 "JEP-361: Switch Extension (since JDK14)")

JDK21 LTS:  
[JEP-431: Sequenced Collection](https://openjdk.org/jeps/431 "JEP-431: Sequenced Collection")  
[JEP-440: Record Patterns](https://openjdk.org/jeps/440 "JEP-440: Record Patterns")  
[JEP-441: Pattern Matching for switch](https://openjdk.org/jeps/441 "JEP-441: Pattern Matching for switch")  
[JEP-444: Virtual Threads](https://openjdk.org/jeps/444 "JEP-444: Virtual Threads")  
[JEP-400: UTF-8 Default (since JDK18)](https://openjdk.org/jeps/400 "JEP-400: UTF-8 Default (since JDK18)")  
[JEP-408: Simple Web Server (since JDK18)](https://openjdk.org/jeps/408 "JEP-408: Simple Web Server (since JDK18)")  
[JEP-418: Internet-Address Resolution SPI (since JDK18)](https://openjdk.org/jeps/418 "JEP-418: Internet-Address Resolution SPI (since JDK18)")

JDK25 LTS:  
[JEP-485: Stream Gatherers (since JDK24)](https://openjdk.org/jeps/485 "JEP-485: Stream Gatherers (since JDK24)")  
[JEP-456: Unnamed Variables \& Patterns (since JDK22)](https://openjdk.org/jeps/456 "JEP-456: Unnamed Variables &amp; Patterns (since JDK22)")  
[JEP-512: Compact Source Files and Instance Main Methods](https://openjdk.org/jeps/512 "JEP-512: Compact Source Files and Instance Main Methods")  
[JEP-458: Launch Multi-File Source Code Programs (since JDK22)](https://openjdk.org/jeps/458 "JEP-458: Launch Multi-File Source Code Programs (since JDK22)")  
[JEP-518: JFR Cooperative Sampling](https://openjdk.org/jeps/518 "JEP-518: JFR Cooperative Sampling")  
[JEP-520: JFR Method Timing \& Tracing](https://openjdk.org/jeps/520 "JEP-520: JFR Method Timing &amp; Tracing")

### References:

1. [Project Babylon](https://openjdk.org/projects/babylon/ "Project Babylon")
2. [Project Loom](https://wiki.openjdk.org/display/loom/Main "Project Loom")
3. [Project Leyden](https://openjdk.org/projects/leyden/ " Project Leyden")
4. [Project Lilliput](https://openjdk.org/projects/lilliput/ "Project Lilliput ")
5. [Project Panama](https://openjdk.org/projects/panama/ " Project Panama")
6. [Project Valhalla](https://openjdk.org/projects/valhalla/ "Project Valhalla")
7. [GitHub Update to JDK25, Practical Design Patterns for Java Developers](https://github.com/mirage22/Practical-Design-Patterns-for-Java-Developers/tree/main-update-j25 "GitHub Update to JDK25, Practical Design Patterns for Java Developers")
8. [Stochastic AI Agility](https://foojay.io/today/stochastic-ai-agility-breaking-cycles-of-debt/ " Stochastic AI Agility")
