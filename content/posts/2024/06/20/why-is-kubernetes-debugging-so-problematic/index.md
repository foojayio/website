---
title: "Why is Kubernetes Debugging so Problematic?"
slug: "why-is-kubernetes-debugging-so-problematic"
date: "2024-06-20T06:37:37+00:00"
lastmod: "2024-06-20T06:37:39+00:00"
description: "Discover effective k8s debugging strategies, from kubectl debug and ephemeral containers to debuggers. Troubleshoot production/dev issues."
canonical: "https://debugagent.com/why-is-kubernetes-debugging-so-problematic"
authors:
  - "shai-almog"
image: "DALL-E-2024-05-26-20.26.54-A-futuristic-technical-illustration-of-Kubernetes-debugging-process.-Features-include-Kubernetes-logo-containers-and-a-person-using-a-laptop-with-c.jpeg"
categories:
  - "Kubernetes"
  - "Tutorials"
tags:
related_posts:
  - "software-testing-as-a-debugging-tool"
  - "debugging-using-jmx-revisited"
  - "debugging-streams-with-peek"
frozen: false
---

* [The Immutable Nature of Containers](#the-immutable-nature-of-containers)
* [The Limitations of `kubectl exec`](#the-limitations-of-raw-kubectl-exec-endraw-)
* [Avoiding Direct Modifications](#avoiding-direct-modifications)
* [Enter Ephemeral Containers](#enter-ephemeral-containers)
  * [Using `kubectl debug`](#using-raw-kubectl-debug-endraw-)
* [Practical Application of Ephemeral Containers](#practical-application-of-ephemeral-containers)
* [Security Considerations](#security-considerations)
* [Interlude: The Role of Observability](#interlude-the-role-of-observability)
* [Command Line Debugging](#command-line-debugging)
* [Connecting a Standard IDE for Remote Debugging](#connecting-a-standard-ide-for-remote-debugging)
* [Conclusion](#conclusion)

**Debugging application issues in a Kubernetes cluster can often feel like navigating a labyrinth. Containers are ephemeral by design, intended to be immutable once deployed.**

This presents a unique challenge when something goes wrong and we need to dig into the issue. Before diving into the debugging tools and techniques, it's essential to grasp the core problem: why modifying container instances directly is a bad idea.

This article will walk you through the intricacies of Kubernetes debugging, offering insights and practical tips to effectively troubleshoot your Kubernetes environment.

{{< youtube xkOekt02mNY >}}

As a side note, if you like the content of this and the other posts in this series check out my [Debugging book](https://www.amazon.com/dp/1484290410/) that covers **t** his subject. If you have friends that are learning to code I'd appreciate a reference to my [Java Basics book.](https://www.amazon.com/Java-Basics-Practical-Introduction-Full-Stack-ebook/dp/B0CCPGZ8W1/) If you want to get back to Java after a while check out my [Java 8 to 21 book](https://www.amazon.com/Java-21-Explore-cutting-edge-features/dp/9355513925/)**.**

### The Immutable Nature of Containers

One of the fundamental principles of Kubernetes is the immutability of container instances. This means that once a container is running, it shouldn't be altered. Modifying containers on the fly can lead to inconsistencies and unpredictable behavior, especially as Kubernetes orchestrates the lifecycle of these containers, replacing them as needed. Imagine trying to diagnose an issue only to realize that the container you're investigating has been modified, making it difficult to reproduce the problem consistently.

The idea behind this immutability is to ensure that every instance of a container is identical to any other instance. This consistency is crucial for achieving reliable, scalable applications. If you start modifying containers, you undermine this consistency, leading to a situation where one container behaves differently from another, even though they are supposed to be identical.

### The Limitations of `kubectl exec`

We often start our journey in Kubernetes with commands such as:

```bash
$ kubectl -- exec -ti <pod-name>
```

This logs into a container and feels like accessing a traditional server with SSH. However, this approach has significant limitations. Containers often lack basic diagnostic tools---no `vim`, no `traceroute`, sometimes not even a shell. This can be a rude awakening for those accustomed to a full-featured Linux environment. Additionally, if a container crashes, `kubectl exec` becomes useless as there's no running instance to connect to. This tool is insufficient for thorough debugging, especially in production environments.

Consider the frustration of logging into a container only to find out that you can't even open a simple text editor to check configuration files. This lack of basic tools means that you are often left with very few options for diagnosing problems. Moreover, the minimalistic nature of many container images, designed to reduce their attack surface and footprint, exacerbates this issue.

### Avoiding Direct Modifications

While it might be tempting to install missing tools on-the-fly using commands like `apt-get install vim`, this practice violates the principle of container immutability. In production, installing packages dynamically can introduce new dependencies, potentially causing application failures. The risks are high, and it's crucial to maintain the integrity of your deployment manifests, ensuring that all configurations are predefined and reproducible.

Imagine a scenario where a quick fix in production involves installing a missing package. This might solve the immediate problem but could lead to unforeseen consequences. Dependencies introduced by the new package might conflict with existing ones, leading to application instability. Moreover, this approach makes it challenging to reproduce the exact environment, which is vital for debugging and scaling your application.

### Enter Ephemeral Containers

The solution to the aforementioned problems lies in ephemeral containers. Kubernetes allows the creation of these temporary containers within the same pod as the application container you need to debug. These ephemeral containers are isolated from the main application, ensuring that any modifications or tools installed do not impact the running application.

Ephemeral containers provide a way to bypass the limitations of `kubectl exec` without violating the principles of immutability and consistency. By launching a separate container within the same pod, you can inspect and diagnose the application container without altering its state. This approach preserves the integrity of the production environment while giving you the tools you need to debug effectively.

#### Using `kubectl debug`

The `kubectl debug` command is a powerful tool that simplifies the creation of ephemeral containers. Unlike `kubectl exec`, which logs into the existing container, `kubectl debug` creates a new container within the same namespace. This container can run a different OS, mount the application container's filesystem, and provide all necessary debugging tools without altering the application's state. This method ensures you can inspect and diagnose issues even if the original container is not operational.

For example, let's consider a scenario where we're debugging a container using an ephemeral Ubuntu container:

```bash
kubectl debug <myapp> -it <pod-name> --image=ubuntu --share-process --copy-to=<myapp-debug>
```

This command launches a new Ubuntu-based container within the same pod, providing a full-fledged environment to diagnose the application container. Even if the original container lacks a shell or crashes, the ephemeral container remains operational, allowing you to perform necessary checks and install tools as needed. It relies on the fact that we can have multiple containers in the same pod, that way we can inspect the filesystem of the debugged container without physically entering that container.

### Practical Application of Ephemeral Containers

To illustrate, let's delve deeper into how ephemeral containers can be used in real-world scenarios. Suppose you have a container that consistently crashes due to a mysterious issue. By deploying an ephemeral container with a comprehensive set of debugging tools, you can monitor the logs, inspect the filesystem, and trace processes without worrying about the constraints of the original container environment.

For instance, you might encounter a situation where an application container crashes due to an unhandled exception. By using `kubectl debug`, you can create an ephemeral container that shares the same network namespace as the original container. This allows you to capture network traffic and analyze it to understand if there are any issues related to connectivity or data corruption.

### Security Considerations

While ephemeral containers reduce the risk of impacting the production environment, they still pose security risks. It's critical to restrict access to debugging tools and ensure that only authorized personnel can deploy ephemeral containers. Treat access to these systems with the same caution as handing over the keys to your infrastructure.

Ephemeral containers, by their nature, can access sensitive information within the pod. Therefore, it is essential to enforce strict access controls and audit logs to track who is deploying these containers and what actions are being taken. This ensures that the debugging process does not introduce new vulnerabilities or expose sensitive data.

### Interlude: The Role of Observability

{{< youtube bRnOGb7rUV4 >}}

While tools like `kubectl exec` and `kubectl debug` are invaluable for troubleshooting, they are not replacements for comprehensive observability solutions. Observability allows you to monitor, trace, and log the behavior of your applications in real-time, providing deeper insights into issues without the need for intrusive debugging sessions.

These tools aren't meant for everyday debugging, that role should be occupied by various observability tools. I will discuss observability in more detail in an upcoming post.

### Command Line Debugging

While tools like `kubectl exec` and `kubectl debug` are invaluable, there are times when you need to dive deep into the application code itself. This is where we can use command line debuggers. Command line debuggers allow you to inspect the state of your application at a very granular level, stepping through code, setting breakpoints, and examining variable states. Personally, I don't use them much

For instance, Java developers can use `jdb`, the Java Debugger, which is analogous to `gdb` for C/C++ programs. Here's a basic rundown of how you might use `jdb` in a Kubernetes environment:

**Set Up Debugging** : First, you need to start your Java application with debugging enabled. This typically involves adding a debug flag to your Java command. However, as discussed in [my post here](https://debugagent.com/mastering-jhsdb-the-hidden-gem-for-debugging-jvm-issues), there's an even more powerful way that doesn't require a restart:

```bash
java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -jar myapp.jar
```

**Port Forwarding** : Since the debugger needs to connect to the application, you'll set up port forwarding to expose the debug port of your pod to your local machine. This is important as [JDWP is dangerous](https://debugagent.com/remote-debugging-dangers-and-pitfalls):

```bash
kubectl port-forward <pod-name> 5005:5005
```

**Connecting the Debugger** : With port forwarding in place, you can now connect `jdb` to the remote application:

```bash
jdb -attach localhost:5005
```

From here, you can use `jdb` commands to set breakpoints, step through code, and inspect variables. This process allows you to debug issues within the code itself, which can be invaluable for diagnosing complex problems that aren't immediately apparent through logs or superficial inspection.

### Connecting a Standard IDE for Remote Debugging

I prefer IDE debugging by far. I never used JDB for anything other than a demo. Modern IDEs support remote debugging, and by leveraging Kubernetes port forwarding, you can connect your IDE directly to a running application inside a pod.

To set up remote debugging we start with the same steps as the command line debugging. Configuring the application and setting up the port forwarding.

1. **Configure the IDE** : In your IDE (e.g., IntelliJ IDEA, Eclipse), set up a remote debugging configuration. Specify the host as [`localhost`](http://localhost) and the port as `5005`.
2. **Start Debugging**: Launch the remote debugging session in your IDE. You can now set breakpoints, step through code, and inspect variables directly within the IDE, just as if you were debugging a local application.

I show how to do it in IntelliJ/IDEA [here](https://debugagent.com/remote-debugging-dangers-and-pitfalls).

### Conclusion

Debugging Kubernetes environments requires a blend of traditional techniques and modern tools designed for container orchestration. Understanding the limitations of `kubectl exec` and the benefits of ephemeral containers can significantly enhance your troubleshooting process. However, the ultimate goal should be to build robust observability into your applications, reducing the need for ad-hoc debugging and enabling proactive issue detection and resolution.

By following these guidelines and leveraging the right tools, you can navigate the complexities of Kubernetes debugging with confidence and precision. In the next installment of this series, we'll delve into common configuration issues in Kubernetes and how to address them effectively.
