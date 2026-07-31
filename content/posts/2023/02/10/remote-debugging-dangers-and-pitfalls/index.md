---
title: "Remote Debugging Dangers and Pitfalls"
slug: "remote-debugging-dangers-and-pitfalls"
date: "2023-02-10T10:14:47+00:00"
lastmod: "2023-02-10T10:14:48+00:00"
description: "Debugging over the network using JDWP isn't hard. However, there are risks that aren't immediately intuitive and some subtle solutions!"
canonical: "https://debugagent.com/remote-debugging-dangers-and-pitfalls"
authors:
  - "shai-almog"
image: "thumbnail-9.png"
categories:
  - "IntelliJ IDEA"
  - "Tutorials"
tags:
related_posts:
  - "debugging-streams-and-collections"
  - "the-massive-hidden-power-of-breakpoints"
  - "debug-like-a-senior-developer"
frozen: false
---

This is the last part of the debugging series, to learn the rest you'll need to get [the book](https://www.amazon.com/dp/1484290410/) or [the course](https://course.debugagent.com/).  

One of the most frequently asked questions I receive is: can we do these things in VS Code?

The answer is unfortunately no. But I elaborate on the debugging capabilities of VS Code in [this video](https://www.youtube.com/watch?v=OBgLeRwjlAc). I'll do a blog post that covers that next week.

Below is the last video in the series:

{{< youtube ImOmu4GHOls >}}

<br />

Transcript {#h2-0-transcript}
-----------------------------

Welcome back to the ninth part of debugging at scale where we really know the quality of your code. Remote debugging doesn't always deal with a remote machine, we often need it when debugging into Kubernetes or Docker.

We'll delve more into that later but for now we'll discuss the basic mechanics. How to connect, how to make it slightly less vulnerable to security attacks, and then we'll discuss the problems of remote debugging.

### The Connection and Command Line {#h3-1-the-connection-and-command-line}

We'll start with a discussion around the connection. We first need to run the process that we'll connect to remotely. To do that we need to run a command similar to this one. Notice that this is a simplified version, in many cases the argument should be embedded in configuration files. When you inspect your maven or gradle files you might see many of the arguments listed here. This is how these things work under the hood. Let's go over the command and break it down piece by piece to see that we understand it correctly.

The first part is the launch of the Java command line. This is pretty obvious. We need quotes in bash since there's a star at the end of the line and bash wants to expand it. Without this quote the command won't work properly.

Agent lib is the system that loads the native library wiring directly into the virtual machine and JDWP is the Java Debug Wire Protocol. This is the underlying networking protocol used to communicate between the debugger and the running process. It's a high level protocol, that means it can be implemented on top of various transports. Typically, it's implemented over TCP sockets, but it's the same protocol we used to debug devices directly. You don't need to know too much about JDWP but the concept is simple, you send commands and can query the system. That's what the IDE does for you. When you add a breakpoint the IDE sends a JDWP command to add a breakpoint at the given location. When the breakpoint is hit JDWP sends back an event to the IDE indicating that. The IDE can then query the details about the current environment, stack, variables, etc.

In this case we transfer the details via a server socket. We can use dt_shmem which stands for shared memory as the wire protocol. This is faster and useful for processes that have access to a shared memory area. This is actually pluggable, and you can build your own JDWP transport. This isn't useful usually but speaks to the power and flexibility of the API.

We can optionally suspend the virtual machine on launch if you want to debug something right from the start. I've set this to no which means the VM will start running right away. If you set it to yes with the letter y the vm will pause on launch and wait for the JDWP connection. This is the address and port we are listening on. In this case I allow anyone to connect on port 5005. I can limit this to localhost only by changing the star character. This is probably the better approach, although it won't make the protocol fully secure.

This is the rest of the command, the class we're running. Typically you would have something more substantial here. In this case I'm just running the PrimeMain class. To start debugging we need to edit the run configuration in intellij.

### Connecting from IntelliJ/IDEA {#h3-2-connecting-from-intellij-idea}

Next we need to locate a configuration for remote debugging. Once I select that we can add it. Notice it's pre-configured with the defaults such as port 5005. I give the new run configuration a name, and we're ready to go with debugging the app. Notice there are many options to tune here, but we don't need any of them. Also check out this area right here. Seems familiar? That's the exact line we discussed before. The IDE is showing us how to set up the command line for the remote process. This lets us verify that we entered everything correctly.

We now have a new Debug Remote run configuration. We can switch to a different configuration from the same location. But when we want to do remote debugging we need to toggle it here. Next we need to press the debug button to run this command.

We are now instantly connected to the running process. Once that is done this feels and acts like any debugger instance launched from within the IDE. I can set a breakpoint, step over, inspect variables etc. So why do it?

In some cases running the server locally in the IDE is impractical. A good example would be debugging a container on your own machine. That might not be trivial.

### Security Implications of JDWP {#h3-3-security-implications-of-jdwp}

Calling JDWP insecure is inaccurate.

That would be like putting your house keys and home address wrapped in a nice gift wrapping with an itemized list of your valuables sorted by value in front of your house. This is an open door. An open door isn't a security vulnerability. It's an open door!

JDWP is very insecure when used remotely. Locally on your own machine it isn't a problem, but it has almost no security protections. There's no solution for that. But there's a very partial workaround of tunneling it over SSH. This is relatively trivial. Just use this command to open a tunnel between the remote machine to your local machine. For both sides it will seem like local debugging. So the example I showed before of connecting to a local host server, would work perfectly with this remote host as SSH will move all the packets back and forth. Securely.

We can't ssh into a Kubernetes container but we can port forward which is almost identical. We can do something similar to this command to forward the port from the given pod to the local machine and vice versa. Same idea as the SSH tunneling but appropriate to the Kubernetes world.

### Dangers of Remote Debugging {#h3-4-dangers-of-remote-debugging}

In this final section I want to talk about the dangers of remote debugging in production. Breakpoints break seems obvious. That's what they're here to do. But if we run on a server we block it completely by mistake. We can use tracepoints. As I said, they're great. But they are no replacement to breakpoints and an accidental click in the gutter can literally stop your server in its tracks.

JDWP effectively allows remote code execution. Lets you access all the bytecode of the app which is effectively the same as giving access to your full source code. It lets attackers do almost anything since it wasn't designed with security in mind. We need to relaunch the application with debugging enabled. That means killing the running process and starting it over again. Disconnecting existing users, etc. That isn't great.

Some operations in the debugger require more than one step in terms of the protocol. As a result you could send a request to the debuggee, lose your connection and the debuggee could be stuck in a problematic state. This is an inherent limitation of the JDWP protocol and can't be worked around in a standard debugger. The problem is that even unintentional actions can demolish a server. A simple conditional breakpoint that invokes a method as part of the condition can demolish server performance and crash it.

JDWP effectively allows remote code execution. Lets you access all the bytecode of the app which is effectively the same as giving access to your full source code. It lets attackers do almost anything since it wasn't designed with security in mind.

Imagine placing a breakpoint where the user password is passed to authentication... If JDWP is open for your server a member of your team might use that, and you will never know! There's no tracking at all!  

60% of security hacks happen from within the organization. If your company does remote debugging they have no way of knowing whether an employee used that to manipulate the application state or siphon user details. There's no tracking or anything. This can be in violation of various rules and regulations since it might expose personal user data. Remote debugging into production can trigger liability risks.

I discuss some of the solutions for those problems both in the low level tooling and in higher level observability solutions. This is covered in the book and in the full course.

Final Word {#h2-5-final-word}
-----------------------------

With this we finished the first part of the course. If you want to check out the full course go to debugagent.com to learn more... The next video covers the strategies for debugging and the science of debugging. If you have any questions please use the comments section. Thank you!
