---
title: "Evolution of Java Memory Architecture (Post Java 7.0)"
slug: "evolution-of-java-memory-architecture-post-java-7-0"
date: "2021-03-03T16:19:20+00:00"
lastmod: "2021-10-16T21:32:43+00:00"
description: "The most impactful change to Java has been the movement from PermGen to MetaSpace. Among others, learn about the related changes in Java 8."
authors:
  - "sumith-puri"
image: "java.jpg"
categories:
  - "Performance"
tags:
related_posts:
  - "java-roots-1-java-memory-architecture"
  - "optimizing-the-garbage-collector-when-migrating-cloud-workloads"
  - "explained-memory-allocation-pacing-in-azul-zulu-prime-builds-of-openjdk"
  - "spring-boot-performance-workshop-with-vlad-mihalcea"
frozen: false
---

**\[About SKP's Core Java/Java EE Roots\]**  
Series of Articles on Rooted Concepts in Core Java and J2EE. They Revolve Around Memory Architecture, Connection \& Memory Leaks, Core Java Syntax \& Semantics, Java Object Layout/Anatomy, Multi-Threading, Asynchronous Task Execution, Design Patterns, Java Agents, Class Loading, API Design, OOPs \& SOLID.  

## **Java Memory Architecture (Pre Java 8.0)**

Before we get started, you might want to take a look at Part 1. It explains the core concepts of the Java Memory Architecture, the ones that should get you started to understand this evolution better.

Link to [Part 1](https://foojay.io/today/java-roots-1-java-memory-architecture/ "Part 1").  
String Literals in Java are stored in a String Pool. String Interning refers to a process or method by which only one copy of a specific string is stored in memory. This is done to allow the efficient usage of memory while also taking less time to retrieve, except when the string is first created. This immutable single copy of the String is called an _intern._ Java provides a method in the _String_ class, _intern(),_ to actually create/retrieve this copy of the string.

### **Pre-Java 7 Era (String Handling)**

Prior to Java 7, Interned Strings or String Literals were stored as part of the PermGen Space. In the above article, the way to configure the non-expandable or fixed PermGen has been provided, along with a diagram of where exactly it is located in memory. The PermGen space is always \*\*fixed.\*\*  
One more point to note here is that _String Interns_ are candidates for garbage collection just like other Objects in Java. The default string pool size is 1009 and can be set through the switch -XX:StringTableSize.

### **Java 7 Changes (String Handling)**

So, with Java 7, Oracle engineers decided to move _String Interns_ to the Heap. As mentioned above, this decision has to do with the following factors:1. Fixed/non-expandable size of PermGen (leading to Out of Memory: PermGen Space).

2. String Interns are garbage collected like other Objects (based on reference count).

3. Set a higher number of Strings, even up to millions.

### **Java 8 Changes (String Handling)**

The above Java 7 changes of String handling continued into Java 8. Furthermore, in Java 8, the following change was made to the memory handling related to _StringInterning_.

* The default number of Strings in the _String Pool_ is now 60013, as compared to 1009
* At any point in time, you can check the _String Interning and Pooling_ usage statistics in the JVM by using -XX:+PrintStringTableStatistics (Symbol Table Statistics).

## **Java 8 Changes (PermGen to MetaSpace)**

The most impactful change to Java has been the movement from PermGen to MetaSpace. The following are the changes in Java 8:

1. Eliminate or reduce Out of Memory: PermGen SpaceError. (PermGen is Gone) \[PermGen elimination\].
2. Memory for Class metadata is allocated only from native memory. Earlier, it was from the Contiguous Java Heap Memory. \[MetaSpace Allocation\].
3. Virtually unlimited (or very high) memory for Class metadata from native memory. \[MetaSpace Allocation\].
4. Limit the maximum limit for metadata in native memory using -XX:MaxMetaspaceSize \[MetaSpace Configuration\].
5. Garbage collection is triggered once the _MaxMetaspaceSize_ is reached \[MetaSpace Garbage Collection\].
6. PermGen and MetaSpace are not a direct movement. (Some items from _PermGen_ have been moved to _MetaSpace._ There are some miscellaneous items moved to the Heap Space, like _Interned Strings_) \[PermGen MetaSpace, Heap Space Impact\].
7. The earlier PermGen flags are ignored, like -XX:PermSize and -XX:MaxPermSize. If used, a warning is issued saying that these flags are no longer valid in Java 8. \[PermGen Compiler Flags\].
8. Once the _MaxMetaSpaceSize_ is reached, the Out of Memory: _MetaSpace Error_ is thrown. \[MetaSpace Error\].
9. The default _MetaSpaceSize_ on a 32-bit and 64-bit machines are 12MB and 16MB respectively. In contrast, the default _MaxPermSize_ was always set to 64MB. \[MetaSpace Default Size\].
10. The native memory size can be set by -XX:MaxDirectMemorySize. (The default is 128MB) \[Native Memory or Direct Buffer Size\].
11. MetaSpace usage is available from the verbose GC log using -XX:+PrintGCDetails \[MetaSpace Statistics\].
12. PermGen had a fixed size, but MetaSpace can Auto-Tune and Auto-Increase depending on the underlying OS. \[MetaSpace Size Efficiency\].
13. Classes can be de-allocated concurrently without GC pauses. It is more efficient than in PermGen, as that required frequent GC pauses. \[MetaSpace Performance\].

![Image title](https://4.bp.blogspot.com/-03r1GS2jWlk/WPzruNno57I/AAAAAAAANQU/EghKET29INkXMH3Nrd_zNlOsF93T5xmdACLcB/s640/permgen_to_metaspace.jpg)  

**\[Note\]**  
Many authors have erroneously stated that PermGen is part of the Java Heap. You have to note this fact that PermGen was/is never a part of the Java Heap. It was only allocated as part of **Contiguous Memory as the Java Heap**. In the case of Metaspace, such a Contiguous Allocation does not exist, Metaspace lies in or is allocated in the native memory of the operating system.
