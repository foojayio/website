---
title: "How we developed the Eclipse OpenJ9 CRIU Support for fast Java start-up"
slug: "how-we-developed-the-eclipse-openj9-criu-support-for-fast-java-startup"
date: "2023-07-19T15:33:39+00:00"
lastmod: "2023-07-20T15:01:07+00:00"
description: "Java startup is a hot topic. Learn how we made Eclipse OpenJ9 start in milliseconds in the Cloud."
authors:
  - "younes-manton"
image: "https://foojay.io/wp-content/uploads/2023/07/open-liberty-restore-perf-ga.png"
categories:
  - "Cloud"
  - "Microservices"
  - "Performance"
tags:
related_posts:
  - "azul-provides-the-crac-in-aws-snapstart-builds"
  - "foojay-podcast-17"
  - "how-to-run-a-java-application-with-crac-in-a-docker-container"
  - "five-java-developer-must-haves"
frozen: false
---

**Checkpointing and restoring the JVM makes applications run faster because all the startup work is done before deployment. This means that in serverless cloud applications, the end-user of the application perceives no delay when the application starts. You can try it out now with your own apps on Open Liberty with Eclipse OpenJ9. But if you want to know more, here's how we tackled the technical challenges of developing the Eclipse OpenJ9 CRIU Support.**

CRIU ([Checkpoint/Restore In Userspace](https://criu.org/Main_Page "Checkpoint/Restore In Userspace")) is a low-level Linux program that allows us to checkpoint (take a snapshot of) a running program, save it to disk in its current state, and later restore it, for example when deploying the application to production.

A restored program picks up where it left off and continues on, which means we can directly reduce the startup time of Java applications, usually without even having to change the application code.

![A diagram showing how the checkpoint + restore model fits into a typical Open Liberty dev-build-prod pipeline.](/images/posts/2023/07/how-we-developed-the-eclipse-openj9-criu-support-for-fast-java-startup/open-liberty-crflow-700x386.jpg)

Checkpointing and restoring the JVM with CRIU does not reduce the amount of startup work nor make it run faster, but because all the startup work is being done before deployment of the application to production, the end-user of the application perceives no delay when the application starts.

In serverless cloud applications, container orchestrators like Kubernetes and OpenShift respond to demand by starting and stopping instances of containers (and the applications they contain) frequently, scaling the application up and down as required. Rapid, almost immediate, startup is essential; minutes or even seconds to start an application is no longer fast enough.

![A diagram showing the checkpoint + restore model as compared to the traditional build + run model.](/images/posts/2023/07/how-we-developed-the-eclipse-openj9-criu-support-for-fast-java-startup/openj9-crflow-700x508.png)

If you just want to quickly and easily try this out with your own app (or with a sample app), stop reading now and instead see [How to package your cloud-native Java application for rapid startup](https://openliberty.io/blog/2023/06/29/rapid-startup-instanton.html?utm_source=foojay&utm_medium=article&utm_content=criu "How to package your cloud-native Java application for rapid startup"). If, however, you want to find out more about the technical challenges that the [OpenJ9](https://www.eclipse.org/openj9/?utm_source=foojay&amp;utm_medium=article&amp;utm_content=criu "OpenJ9") teams faced and addressed with the CRIU project, read on.

OpenJ9 CRIU Support {#h2-0-openj9-criu-support}
-----------------------------------------------

Over the Eclipse OpenJ9 JVM's long history we first made the JVM start as fast as possible. Then we implemented a shared class cache (SCC) and ahead-of-time (AOT) compilation, saving class data and compiled code on disk so we didn't have to process and compute them again next time.

Today, applications running on Open Liberty with OpenJ9 can [start in about 1 second](https://openliberty.io/blog/2019/10/30/faster-startup-open-liberty.html?utm_source=foojay&utm_medium=article&utm_content=criu "start in about 1 second"), but scaling to zero (rapid stops and restarts of applications) needs sub-second startup speeds. In order to achieve that we need to focus on, and somehow factor out, the JVM's inherent contribution to slow startup.

Compiling applications to Graal Native Image, which turns Java programs into native executables, is one way to improve startup times because it removes the JVM completely. However, removing the JVM also removes the ability to optimize the application at runtime for improved throughput (startup time is not the only thing that needs to be fast) and to easily debug the application.

It also often requires the application developer to refactor the application before it can be compiled, and there is no [dev/prod parity](https://12factor.net/dev-prod-parity "dev/prod parity") because the application is typically developed on the JVM but then runs in production without the JVM and must be tested separately in both environments.

The checkpoint and restore approach taken by [OpenJ9 CRIU Support](https://blog.openj9.org/2022/09/26/fast-jvm-startup-with-openj9-criu-support?utm_source=foojay&utm_medium=article&utm_content=criu "OpenJ9 CRIU Support") enables developers to take an existing Java application largely, or wholly, unchanged and to deploy it with its JVM with up to 10 times faster startup to first response (we're currently working on making it even faster in Open Liberty).

A full and complete JVM is still running the application with few limitations on which Java language and library features developers can use in the application. The following graph shows the reduction in time-to-first-response for three different apps when restoring from a checkpoint as compared to conventional startup.

![A graph showing the reduction in time-to-first-response when restoring from a checkpoint as compared to conventional startup.](/images/posts/2023/07/how-we-developed-the-eclipse-openj9-criu-support-for-fast-java-startup/open-liberty-restore-perf-ga-1024x645.png)

One change that might be necessary to the application itself is to ensure that the application's startup sequence is partitioned into work that can be done before checkpoint and work that must be done after restore, and ensuring that your program isn't doing anything between the two phases.

If you can do that you're most of the way there. You also have to make sure that the files your program depends on (program files such as libraries, and data files) don't change between the checkpoint and restore steps. This naturally works very well in containerized environments in which each instance of the container has its own copy of all the files needed by the application and kept separately from any other instance.

Checkpoint/restore challenges that we resolved in OpenJ9 CRIU Support {#h2-1-checkpoint-restore-challenges-that-we-resolved-in-openj9-criu-support}
---------------------------------------------------------------------------------------------------------------------------------------------------

Much of our work on OpenJ9 CRIU Support has been spent testing, uncovering problems, and finding solutions to them.

### Restoring multiple instances from a single checkpoint and process ID {#h3-2-restoring-multiple-instances-from-a-single-checkpoint-and-process-id}

Checkpointing and restoring a single program instance is CRIU's main use case. However, in order to seamlessly improve startup time we need something slightly different; we need to checkpoint a program and restore potentially many instances from that single checkpoint. Each restored program is going to think it's the original and is going to want to reclaim its process IDs (PIDs) and various other resources.

We've done work in OpenJ9 to make sure we take care of our own resources to avoid such conflicts, but PIDs and other OS-level resources are outside of our direct control. Thankfully Linux namespaces, a feature of the kernel, lets us restore programs in their own isolated environments. Namespaces are automatically used by containers, and containers also intrinsically run programs with their own copy of every file in image. This makes containerized applications a perfect fit for the checkpoint and restore model and therefore the focus of our efforts.

### Portability of applications {#h3-3-portability-of-applications}

We take for granted that Java programs are fully portable; most of the complexity is hidden in the JVM. However, to facilitate checkpoint and restore, the JVM needs to be able to cope with a program that has been checkpointed in one environment then being restored in another. For example, restoring a program in an environment with a different kind of CPU that lacks a feature present in the CPU of the original environment.

We've made plenty of changes to OpenJ9 in this regard to free users from this burden, making sure that a restored JVM will correctly detect resources such as the amount of memory and number of CPUs available in the new environment and scale itself up/down as necessary. We also make sure that we don't take advantage of any hardware-specific functionality until after restore time, thereby maximizing portability while continuing to take full advantage of the runtime environment at restore time like our users expect.

### Root privileges {#h3-4-root-privileges}

CRIU can be used to instruct a process to checkpoint itself but this requires CRIU to perform some sensitive operations which, until recently, required Linux system root privileges. This is not ideal because it means that we would need to run our Java applications as root.

We worked with the CRIU open source community to develop CRIU's new "unprivileged" mode (e.g. [allowing CRIU to be used as non-root](https://github.com/checkpoint-restore/criu/pull/1930 "allowing CRIU to be used as non-root"); [fixing VMA handling for shmem maps](https://github.com/checkpoint-restore/criu/pull/2022 "fixing VMA handling for shmem maps")), which means that you don't need to run applications as root in order to checkpoint and restore the JVM. The unprivileged mode requires a limited set of capabilities for a very brief period of time at startup, thereby improving overall security.

OpenJ9 CRIU Support needs two capabilities: `CAP_CHECKPOINT_RESTORE` and `CAP_SYS_PTRACE` (though in many cases we can avoid needing `CAP_SYS_PTRACE` at restore time as long the kernel and container runtimes allow basic unprivileged ptrace usage). In containers, OpenJ9's CRIU support also requires `CAP_SETPCAP` so that we can drop any unnecessary capabilities that are provided by the container runtime. We also contributed fixes to allow us to [drop the need for `CAP_NET_ADMIN`](https://github.com/checkpoint-restore/criu/pull/1996 "drop the need for <code>CAP_NET_ADMIN</code>").

The new unprivileged mode handles a subset of the cases that a fully privileged CRIU handles, but we believe this sufficiently covers most uses of Java without exposing users to unnecessary security risk. As mentioned, we only need a single, dedicated capability at restore time. Most container runtimes also use other security facilities such as seccomp, and these may need a few tweaks to allow our containers to be able to restore processes, but since the beginning of this project we've steadily reduced the security compromises that we've had to make and expect that to continue in future.

Both Docker and Podman support the checkpointing and restoring of containers using CRIU, which is ultimately a very similar process to our own, which gives us added confidence that we're not compromising security in any way.

Creating a slick developer experience with Liberty InstantOn {#h2-5-creating-a-slick-developer-experience-with-liberty-instanton}
---------------------------------------------------------------------------------------------------------------------------------

An essential part of making the OpenJ9 CRIU Support available was ensuring that it has a slick developer experience so that developers want to use it. It would be counterproductive to require developers to have a deep understanding of OpenJ9 CRIU Support, the JVM and its implementation, and how to use it with a Java runtime just to be able to make their apps start faster.

Similarly, it was really important to us that developers can run most, if not all, of their existing apps using the CRIU Support without having to go back into their application source code to make code changes before the app can benefit from faster startups.

To remove complexity for the developer, the OpenJ9 and [Open Liberty](https://openliberty.io/?utm_source=foojay&utm_medium=article&utm_content=criu "Open Liberty") project teams collaborated to create Liberty InstantOn, which ensures that the majority of CRIU support is handled by the JVM (OpenJ9) or Java runtime (Open Liberty) without the developer having to worry about it.

The only decision the developer has to make is when during startup to take the checkpoint: after the application starts (typical, and gives faster startup) or before the application starts (for certain apps, such as those that connect to a database on startup). The developer specifies the checkpoint stage using the `WLP_CHECKPOINT` environment variable before starting the Liberty runtime.

The Liberty Docker images have been updated with Liberty InstantOn to provide checkpoint and restore support; this enables developers to build application images containing checkpoints and to restore them immediately when the containers are launched.

You can try out Liberty InstantOn with your own app (or a sample app); see [How to package your cloud-native Java application for rapid startup](https://openliberty.io/blog/2023/06/29/rapid-startup-instanton.html?utm_source=foojay&utm_medium=article&utm_content=criu "How to package your cloud-native Java application for rapid startup").

What next for OpenJ9 CRIU Support? {#h2-6-what-next-for-openj9-criu-support}
----------------------------------------------------------------------------

There are some other challenges to the checkpoint/restore approach that we plan to address in future developments.

### Modifying the environment of the restored process {#h3-7-modifying-the-environment-of-the-restored-process}

A process restored by CRIU doesn't currently have access to any new environment variables, nor can a developer pass it any new command line parameters. The environment that the process started with is restored and the current one is discarded. Similarly, the contents of `argc` and `argv` maintain their original values.

The command line and environment are important aspects of most Java programs, particularly when these programs are containerized. They give the user an opportunity to specify and tune behaviour at the point of execution. Sometimes this can only be done at the point of execution because the necessary information is not known ahead of time.

In future we aim to improve this by passing command line and environment data to restored Java programs and providing APIs they can use to retrieve the data after being restored. In the meantime, application developers can use the MicroProfile Config API to externalise environment variables that they use in their apps.

### Changing the owner of restored processes {#h3-8-changing-the-owner-of-restored-processes}

Restored processes continue to be owned by the original user, which means that the user (and specifically their UID, GID, and supplementary groups) must exist on every system the restored app runs on. This makes distributing a checkpointed program more difficult, especially in environments like OpenShift Container Platform, which starts containers with random UIDs by default, unless configured otherwise.

In the near future we aim to be able to restore processes to any UID, thereby freeing users from having to recreate UIDs on every machine or disabling random UIDs in OpenShift, and so on.

Try it out {#h2-9-try-it-out}
-----------------------------

You can try checkpoint and restore with your own application with the latest release of Open Liberty, which includes the IBM Semeru Runtimes distribution of OpenJ9, by following the steps outlined in [How to package your cloud-native Java application for rapid startup](https://openliberty.io/blog/2023/06/29/rapid-startup-instanton.html?utm_source=foojay&utm_medium=article&utm_content=criu "How to package your cloud-native Java application for rapid startup"). Let us know what you think in the comments.

Ask a question {#h2-10-ask-a-question}
--------------------------------------

If you want to ask questions directly to the OpenJ9 and Open Liberty development teams, you are welcome to [register for our live webinar on July 20, 1pm-2:30pm (ET)](https://community.ibm.com/community/user/wasdevops/events/event-description?CalendarEventKey=3566b086-bbbb-4da2-9ace-0187390632c1&amp;CommunityKey=5c4ba155-561a-4794-9883-bb0c6164e14e&amp;Home=%2fcommunity%2fuser%2fwasdevops%2fcommunities%2fcommunity-home%2frecent-community-events&utm_source=foojay&utm_medium=article&utm_content=criu "register for our live webinar on July 20, 1pm-2:30pm (ET)"). If you can't make it to the live webinar, the session will be recorded for watching later (register to access the recording).

Conclusion {#h2-11-conclusion}
------------------------------

Java startup performance has come a long way in the last two decades. The checkpoint and restore model represents a radically different way of thinking about how Java programs are built, deployed, and executed. It's an approach tailored to modern Java and the cloud. It allows us to effectively start our programs "offline" and to restore them on demand, when needed, in a fraction of the time a conventional JVM would take, and takes full advantage of modern containerization.

The OpenJ9 and Open Liberty teams have spent a lot of time creating a slick developer experience that hides the underlying complexity of the checkpoint and restore technologies.

For most applications, you can containerize the application with a Liberty InstantOn configuration without having to change the application source code and the application will start up faster. For some applications you might need to make some changes, but in most cases it's transparent and "just works."

In future, we hope to give Java developers ways of gracefully handling checkpoint and restore in every scenario with as little disruption as possible.

The benefits, though, are clear: near instantaneous startup, faster scale-up and scale-down behaviour, a slick developer experience, a natural fit for serverless deployments, and many more.
