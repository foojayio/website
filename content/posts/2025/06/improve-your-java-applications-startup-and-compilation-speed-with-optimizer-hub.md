---
title: "Improve Your Java Applications’ Startup and Compilation Speed with Optimizer Hub"
slug: "improve-your-java-applications-startup-and-compilation-speed-with-optimizer-hub"
date: "2025-06-27T09:35:31+00:00"
lastmod: "2025-07-07T08:11:49+00:00"
description: "This is the fourth blog post in a series on faster Java application warmup with ReadyNow. If you haven’t been following the series, go back to the first - by Frank Delporte"
canonical: "https://www.azul.com/blog/improve-your-java-applications-startup-and-compilation-speed-with-optimizer-hub/"
authors:
  - "frankdelporte"
image: "/images/posts/2025/06/improve-your-java-applications-startup-and-compilation-speed-with-optimizer-hub/Favicon-3-2.png"
categories:
  - "Java"
  - "Performance"
tags:
related_posts:
  - "faster-java-warmup-crac-versus-readynow"
  - "how-readynow-improves-java-warmup-time"
  - "how-to-train-readynow-to-achieve-optimal-java-performance"
  - "superfast-application-startup-java-on-crac"
frozen: false
---

*This is the fourth blog post in a series on faster Java application warmup with ReadyNow. If you haven't been following the series, go back to the first blog post, [Faster Java Warmup: CRaC versus ReadyNow](https://foojay.io/today/faster-java-warmup-crac-versus-readynow/), and catch up. This post examines Optimizer Hub, a set of services external to the JVM within Azul Platform Prime that you can run in your cloud or on-premises environment* to improve your applications' startup and compilation speed*.*

Optimizer Hub provides the services Cloud Native Compiler and ReadyNow Orchestrator, which can be combined with ReadyNow, included in Azul Zing Builds of OpenJDK (Zing), to further improve your applications' startup and compilation speed.

What is Optimizer Hub? {#What-is-Optimizer-Hub?}
------------------------------------------------

[Optimizer Hub](https://www.azul.com/products/components/azul-optimizer-hub/) is a component of Azul Platform Prime that makes your Java programs start and stay fast. It consists of two services:

* [**Cloud Native Compiler**](https://docs.azul.com/optimizer-hub/about/cloud-native-compiler.html): Provides a server-side optimization solution that offloads JIT compilation from [Zing's Falcon JIT compiler](https://docs.azul.com/prime/Falcon-Compiler) to separate and dedicated service resources, providing more processing power to JIT compilation while freeing your client JVMs from the burden of doing JIT compilation locally.
* [**ReadyNow Orchestrator**](https://docs.azul.com/optimizer-hub/about/readynow-orchestrator.html): Records and serves ReadyNow profiles. This greatly simplifies the operational use of ReadyNow and removes the need to configure any local storage for writing the profile logs. ReadyNow Orchestrator can record multiple profile candidates from multiple JVMs and promote the best-recorded profile.

Optimizer Hub is shipped as a Kubernetes cluster, which you provision and run on your cloud or on-premises servers. It runs alongside your other clusters with the Java applications in your environment.

Is Optimizer Hub required to use ReadyNow? {#Is-Optimizer-Hub-Required-to-Use-ReadyNow?}
----------------------------------------------------------------------------------------

Absolutely not! As we explained in the previous posts in this series, ReadyNow is fully integrated into Zing. With the `-XX:ProfileLogOut=<file>` and `-XX:ProfileLogIn=<file>` command line options, you can generate profile logs locally on your machine or a production server and reuse them on the same and other machines.

You can add Optimizer Hub to your production environment to reduce the operational complexity of using ReadyNow within a more extensive system. The ReadyNow Orchestrator service helps you to automatically handle training runs, simplify the management of profile log files by offloading the storage from the JVM instances, and more.

Advantages of using ReadyNow Orchestrator {#Advantages-of-Using-ReadyNow-Orchestrator}
--------------------------------------------------------------------------------------

Some of the advantages of using the ReadyNow Orchestrator service of Optimizer Hub:

1. **Centralized Profile Storage**: ReadyNow Orchestrator serves as the central storage for the profiles for all JVMs and removes the need to configure any local storage on the instances that run your Java applications. This also simplifies using containers, removing the need to configure persistent storage or bake profiles into images each time you build a new image.
2. **Profile Training:**ReadyNow Orchestrator records multiple training generations of your profile to produce the best possible optimization profile.
3. **Promotion of Profiles**: ReadyNow Orchestrator automatically collects profile candidates from many JVMs running the same ProfileName and performing training runs to generate the best-promoted profile.
4. **Providing Profiles to JVMs**: ReadyNow Orchestrator automatically serves the best profile to newly started JVMs.

{{< youtube Wk60VKXCZyA >}}

More advantages of Optimizer Hub {#More-Advantages-of-Optimizer-Hub}
--------------------------------------------------------------------

The ReadyNow Orchestrator service is part of Optimizer Hub, which provides more advantages:

1. **Cloud Native Compiler:**This second service is included in Optimizer Hub and further improves your applications' warmup by closely integrating with ReadyNow Orchestrator.
2. **Flexibility**: Optimizer Hub is shipped as a Kubernetes cluster that you can provision and run on your cloud or on-premises servers. This provides flexibility in how you deploy to suit your infrastructure needs.
3. **Multiple Modes**: Optimizer Hub can run in different modes, including ReadyNow-only. This flexibility allows for deploying a reduced set of Optimizer Hub components in the Kubernetes cluster when only ReadyNow Orchestrator is needed, which can benefit resource management.
4. **Regular Updates**: Optimizer Hub includes regular updates and bug fixes in sync with Prime releases to ensure optimal performance and compatibility.
5. **Monitoring**: Optimizer Hub provides detailed metrics about compilations, which you can use in your monitoring tools or with the provided Grafana dashboard.

Conclusion {#Conclusion}
------------------------

Although Optimizer Hub, with its ReadyNow Orchestrator, is not required to use ReadyNow in your Zing runtime environment, it can significantly help reduce the operational complexity of an extensive production system.

**Next:** [When ReadyNow Can Only Compile on Traffic Loads](https://foojay.io/today/when-readynow-can-only-compile-on-traffic-loads/)
