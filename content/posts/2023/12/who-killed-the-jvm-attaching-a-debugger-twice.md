---
title: "Who Killed the JVM? Attaching a Debugger Twice"
slug: "who-killed-the-jvm-attaching-a-debugger-twice"
date: "2023-12-06T17:17:47+00:00"
lastmod: "2023-12-06T17:17:48+00:00"
description: "The ability to disconnect and then reattach debuggers is helpful for many complex debugging scenarios and can help you debug faster."
authors:
  - "johannes-bechberger"
image: "https://mostlynerdless.de/wp-content/uploads/2023/10/reattach-2000x573.png"
categories:
  - "Debugging"
tags:
related_posts:
frozen: false
---

A few weeks back, I told you about on-demand debugging in my [Level-up your Java Debugging Skills with on-demand Debugging](https://mostlynerdless.de/blog/2023/10/03/level-up-your-java-debugging-skills-with-on-demand-debugging/) blog post, enabling you to delay a debugging session until:

* You gave orders via `jcmd` (`onjcmd=y` option), a feature contributed by the [SapMachine](https://sapmachine.io) team
* the program threw a specific exception (`onthrow=<exception>`)
* The program threw an uncaught exception (`onuncaught=y`)

This is quite useful because the JDWP agent has to do substantial initialization before it can start listening for the attaching debugger:
> The triggering event invokes the bulk of the initialization, including creation of threads and monitors, transport setup, and installation of a new event callback which handles the complete set of events.
> [Comment in JDWP-agent Source Code](https://github.com/openjdk/jdk/blob/e05cafda78a37dbeb2df2edd791be19d22edaece/src/jdk.jdwp.agent/share/native/libjdwp/debugInit.c#L358)

Other things, like class loading, were slower with an attached debugger in older JDK versions (see [JDK-8227269](https://bugs.openjdk.org/browse/JDK-8227269)).

But what happens after you end the debugging session? Is your debugged program aborted, and if not, can you reattach your debugger at a later point in time? The answer is as always: It depends. Or, more precisely: It depends on the remote debugger you're using and how you terminate the debugging session.  
![](https://mostlynerdless.de/wp-content/uploads/2023/10/reattach-2000x573.png)

But why should you disconnect and then reattach a debugger? It allows you to not run the debugger during longer ignorable stretches of your application's execution. The overhead of running the JDWP agent waiting for a connection is minimal compared to the plethora of events sent from the agent to the debugger during a debugging session (like class loading events, see [A short primer on Java debugging internals](https://mostlynerdless.de/blog/2022/12/27/a-short-primer-on-java-debugging-internals/)).

Before we cover how to (re)attach a debugger in IDEs, we'll see how this works on the JDWP/JDI level:

On JVM Level {#h2-0-on-jvm-level}
---------------------------------

The JDWP agent does not prevent the debugger from reattaching. There are two ways that Debugging sessions can be closed by the debugger: dispose and exit. Disposing of a connection via the [JDWP `Dispose`](https://docs.oracle.com/en/java/javase/17/docs/specs/jdwp/jdwp-protocol.html#JDWP_VirtualMachine_Dispose) command is the least intrusive way. This command is exposed to the debugger in JDI via the [`VirtualMachine#dispose()`](https://docs.oracle.com/en/java/javase/21/docs//api/jdk.jdi/com/sun/jdi/VirtualMachine.html#dispose()) method:
> Invalidates this virtual machine mirror. **The communication channel to the target VM is closed, and the target VM prepares to accept another subsequent connection from this debugger or another debugger**, including the following tasks:
>
> * All event requests are cancelled.
> * **All threads suspended by [`suspend()`](https://docs.oracle.com/en/java/javase/21/docs//api/jdk.jdi/com/sun/jdi/VirtualMachine.html#suspend()) or by [`ThreadReference.suspend()`](https://docs.oracle.com/en/java/javase/21/docs//api/jdk.jdi/com/sun/jdi/ThreadReference.html#suspend()) are resumed as many times as necessary for them to run.**
> * Garbage collection is re-enabled in all cases where it was disabled through [`ObjectReference.disableCollection()`](https://docs.oracle.com/en/java/javase/21/docs//api/jdk.jdi/com/sun/jdi/ObjectReference.html#disableCollection()).
>
> Any current method invocations executing in the target VM are continued after the disconnection. Upon completion of any such method invocation, the invoking thread continues from the location where it was originally stopped.
> [JDI Documentation](https://docs.oracle.com/en/java/javase/21/docs//api/jdk.jdi/com/sun/jdi/VirtualMachine.html#dispose())

This essentially means that disposing of a debugging connection does not prevent the currently debugged application from continuing to run.

The other way is using the [exit](https://docs.oracle.com/en/java/javase/17/docs/specs/jdwp/jdwp-protocol.html#JDWP_VirtualMachine_Exit) command, exposed as [`VirtualMachine#exit(int exitCode)`](https://docs.oracle.com/en/java/javase/21/docs//api/jdk.jdi/com/sun/jdi/VirtualMachine.html#exit(int)):
> Causes the mirrored VM to terminate with the given error code. All resources associated with this VirtualMachine are freed. If the mirrored VM is remote, the communication channel to it will be closed.
> [JDI DOCUMENTATION](https://docs.oracle.com/en/java/javase/21/docs//api/jdk.jdi/com/sun/jdi/VirtualMachine.html#exit(int))

This, of course, prevents the debugger from reattaching.

Reattaching with IDEs {#h2-1-reattaching-with-ides}
---------------------------------------------------

NetBeans, IntelliJ IDEA, and Eclipse all support reattaching after ending a debugging session by just creating a new remote debugging session. Be aware that this only works straightforwardly when using remote debugging, as the local debugging UI is usually directly intertwined with the UI for running the application. I would recommend trying remote debugging once in a while, even when debugging on your local machine, to be able to use all the advanced features.

Terminating an Application with IDEs {#h2-2-terminating-an-application-with-ides}
---------------------------------------------------------------------------------

NetBeans is the only IDE of the three that does not support this (as far as I can ascertain). IntelliJ IDEA and Eclipse both support it, with Eclipse having the more straight-forward UI:  
![](https://mostlynerdless.de/wp-content/uploads/2023/10/eclipse_buttons.png)

If the *terminate* button is not active, then you might have to tick the *Allow termination of remote VM* check-box in the remote configuration settings:  
![](https://mostlynerdless.de/wp-content/uploads/2023/10/eclipse_remote_dbg_dialog.png)

IntelliJ IDEA's UI is, in this instance, arguably less discoverable: To terminate the application, you have to close the specific debugging session tab explicitly.  
![](https://mostlynerdless.de/wp-content/uploads/2023/10/intellij_dbg_buttons.png)

This then results in a popup that offers you the ability to terminate:  
![](https://mostlynerdless.de/wp-content/uploads/2023/10/image-9.png)

Conclusion {#h2-3-conclusion}
-----------------------------

The ability to disconnect and then reattach debuggers is helpful for many complex debugging scenarios and can help you debug faster. Being able to terminate the application directly from the debugger is an additional time saver when working with remote debugging sessions. Both are often overlooked gems of Java debugging, showing once more how versatile the JDWP agent and UI debuggers are.

I hope you enjoyed this addendum to my [Level-up your Java Debugging Skills with on-demand Debugging](https://mostlynerdless.de/blog/2023/10/03/level-up-your-java-debugging-skills-with-on-demand-debugging/) blog post. If you want even more debugging from me, come to my talk at the ConFoo conference in Montreal on the 23rd of February, and hopefully, next year a conference or user group near you.

**This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone. It appeared first on my personal blog [mostlynerdless.de](https://mostlynerdless.de/) and was supported by rainy weather and the subsequent afternoon in a cafe in Bratislava:**  
![](https://mostlynerdless.de/wp-content/uploads/2023/10/IMG_2578-2000x1500.jpeg)
