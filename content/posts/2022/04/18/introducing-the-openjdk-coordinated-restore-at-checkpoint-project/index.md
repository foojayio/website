---
title: "Introducing the \"Coordinated Restore at Checkpoint\" Project"
slug: "introducing-the-openjdk-coordinated-restore-at-checkpoint-project"
date: "2022-04-18T08:13:55+00:00"
lastmod: "2024-02-07T21:59:53+00:00"
description: "Do you want to dramatically decrease JVM startup time, from hundreds of seconds to tenths of milliseconds? Find out about Java on CRaC."
authors:
  - "gerrit-grunwald"
image: "https://i.ibb.co/pLg3WCb/Time-To-First-Operation.png"
categories:
  - "Performance"
  - "Research"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "7-functional-programming-techniques-in-java-a-primer"
  - "a-closer-look-at-jfr-streaming"
enlighterjs: true
frozen: false
---

### Introduction

One of the great things about the Java Virtual Machine (JVM) is the fact that it is able to adapt the performance of a Java application to the way it is used.

It can figure out what parts of your code are used often and it can then optimize the code by means of its ability to compile code just in time (JIT).

But that also means that it has to figure out those parts first, before it can compile those parts into faster code.

And this requires time, meaning to say that you cannot simply run your code and assume that the JVM instantly optimizes it to run it as fast as it is able.

That is because it simply takes time to warm up the JVM before your application will be able to run optimally.

### Modern Applications

If you have a long-running application, the warm up time, which may be within the range of seconds to minutes, is usually no problem.

But, these days, Java applications are often used in microservice environments, which means you might have a lot of small applications that just run for a short time but will be restarted often.

In this scenario, the warm up time of the JVM is not very helpful because the JVM might not even have been warmed up before the microservice will be shut down again.

One way to work around that warm up problem could be to compile your application ahead of time and create a native image from it which can start up really fast.

But the drawback with native images is the fact that, as soon as your code is statically compiled to native code, you will lose the power of runtime optimizations that can be done by the JVM.

### Coordinated Restore at Checkpoint

So the question is whether there is a way to keep the JVM but reduce its startup time.

And the answer is, yes, there is: use CRaC, the Coordinated Restore at Checkpoint.

Anton Kozlov, a senior software engineer at Azul, is behind an OpenJDK proposal around this topic and you can find more information about the project on the related [OpenJDK page](https://openjdk.java.net/projects/crac/).

The CRaC project is focused on developing a Java API making it possible to save and restore the state of a JVM, including the currently running application.

This CRaC API is for coordination, enhancing checkpoint/restore, though technically checkpoint/restore is possible without coordination in some circumstances.

Using this approach can lead to dramatically decreased startup time from hundreds of seconds to tenths of milliseconds.

The proposal relies on the Linux CRIU (Checkpoint/Restore In Userspace) project, plus other additional methods.

### Checkpoint Creation

The idea is to start a JVM with your application and warm it up until it reaches its optimum performance.

Once this state is reached, you create a snapshot of the JVM, a so called checkpoint.

The checkpoint creation means the current state of the JVM will be saved to a set of files on the filesystem.

Now you can restore the JVM from that set of files back to a running instance but without the need to warm it up.

If you think about microservices that are deployed in a containerized environment, you could think about spinning up a container, warming up the JVM inside the container, and creating a checkpoint by stopping the container.

The next time you spin up this container, it could then restore the JVM from the stored checkpoint.

### Promising Test Results

The team around Anton Kozlov has tested this approach using different well known frameworks, such as Spring Boot, Quarkus, Micronaut, and Tomcat.

The results look more than promising:

<img decoding="async" class="alignnone size-medium" src="https://i.ibb.co/pLg3WCb/Time-To-First-Operation.png" width="1680" height="1042">

As you can see, the startup times can be reduced dramatically by using the Coordinated Restore at Checkpoint proposal with the benefit of still having a JVM running with all its abilities to further optimise the running code.

In addition, you also keep all the debugging features for continuous optimization of the code.

In principle, startup time can be reduced to the time needed to load the checkpoint files back into memory plus the reinitialization of resources.

### CRaC API

Creating a checkpoint requires your application to free its resources, such as database connections, HTTP connections, and open files, otherwise the checkpoint image could be outdated by relying on resources that may disappear.

The proposed CRaC API provides methods to help you free your resources before creating the checkpoint and connecting your resources after restoring the checkpointed JVM.

First of all, you need to implement the Resource interface from the "jdk.crac" package.

This interface provides two methods, "beforeCheckpoint()" and "afterRestore()".

To make it work, you also need to register your Resource to a global context by calling "Core.getGlobalContext().register()":

```java
public class Main implements Resource {

  public Main() {
    Core.getGlobalContext().register(Main.this);
  }

  @Override
  public void beforeCheckpoint(Context<? extends Resource> context) throws Exception {
    // Free your resources here
  }

  @Override
  public void afterRestore(Context<? extends Restore> context) throws Exception {
    // Load your resources here
  }

}
```

To be able to check whether everything works as expected, the current implementation will throw exceptions when you have open resources, such as open sockets.

When you trigger a checkpoint, the JVM heap will be cleaned and compacted so that the JVM is in a safe state.

The CRaC project also handles files that are produced by the JVM.

Because it depends on CRIU, the CRaC project comes bundled with CRIU, which means you don't need to install it manually.

Checkpoints can either be created using the jcmd tool from a shell or by calling Core.checkpointRestore() from the code itself.

This will create the checkpoint and exit the application.

Registering resources is done by notifying a global context before the checkpoint is created and after the checkpoint was restored.

### Getting Started

1. Get hold of the [OpenJDK builds](https://github.com/CRaC/openjdk-builds/releases/) that already include the CRaC functionality.
2. Get [the basic example available on GitHub](https://github.com/HanSolo/crac4) that I have created to give you an idea of how CRaC works.

In short, the example will call a loop every 5 seconds. In that loop, it will check 100000 times if a random number between 1 - 100000 is a prime.

Before it does the actual calculation, it will check whether the result is already in a cache.

If it finds the number in the cache, it directly returns the result and, if not, it will calculate the result, put it in the cache and then return it.

This will lead to similar behaviour as a normal application, when looking at application performance.

In the beginning, the cache is empty, which leads to calculating every number at least once. Over time, the performance will increase because the cache will fill up more and more.

Information about how to setup CRaC and run the example can be found in the README.

More information about CRaC can be found here:  
<https://openjdk.java.net/projects/crac/>  
[https://wiki.openjdk.java.net/display/crac](https://openjdk.java.net/projects/crac/)
