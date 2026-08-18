---
title: "Debugging Kubernetes Part 1: An Introduction"
slug: "debugging-kubernetes-part-1-an-introduction"
date: "2024-06-05T06:12:40+00:00"
lastmod: "2024-06-05T06:13:35+00:00"
description: "In this first part of our k8s debugging series we take an in-depth view of the underlying technologies from containers to orchestration."
canonical: "https://debugagent.com/debugging-kubernetes-part-1-an-introduction"
authors:
  - "shai-almog"
image: "DALL-E-2024-05-13-08.10.19-A-futuristic-digital-illustration-for-a-blog-post-heading-featuring-a-stylized-representation-of-Kubernetes-and-container-orchestration.-The-image-in.jpeg"
categories:
  - "Debugging"
  - "Kubernetes"
  - "Tools"
tags:
related_posts:
  - "software-testing-as-a-debugging-tool"
  - "debugging-streams-with-peek"
  - "dtrace-revisited-advanced-debugging-techniques"
frozen: false
---

* [Introduction to Kubernetes and Distributed Systems](#introduction-to-kubernetes-and-distributed-systems)
  * [The Evolution of Deployment Technologies](#the-evolution-of-deployment-technologies)
  * [Enter Virtualization](#enter-virtualization)
  * [Rise of Containers](#rise-of-containers)
  * [Rise of Orchestration](#rise-of-orchestration)
* [Enter Kubernetes](#enter-kubernetes)
  * [Why Kubernetes Stands Out](#why-kubernetes-stands-out)
* [Kubernetes For Developers](#kubernetes-for-developers)
  * [Kubernetes Basics In Practice](#kubernetes-basics-in-practice)
* [Final Word](#final-word)

While debugging in an IDE or using simple command line tools is relatively straightforward, the real challenge lies in production debugging. Modern production environments have enabled sophisticated self-healing deployments, yet they have also made troubleshooting more complex. Kubernetes (aka k8s) is probably the most well known orchestration production environment. To effectively teach debugging in Kubernetes, it's essential to first introduce its fundamental principles.

This part of the debugging series is designed for developers looking to effectively tackle application issues within Kubernetes environments, without delving deeply into the complex DevOps aspects typically associated with its operations. Kubernetes is a big subject, it took me two videos just to explain the basic concepts and background.

{{< youtube sWclLQgbIUQ >}}

As a side note, if you like the content of this and the other posts in this series check out my [Debugging book](https://www.amazon.com/dp/1484290410/) that covers **t** his subject. If you have friends that are learning to code I'd appreciate a reference to my [Java Basics book.](https://www.amazon.com/Java-Basics-Practical-Introduction-Full-Stack-ebook/dp/B0CCPGZ8W1/) If you want to get back to Java after a while check out my [Java 8 to 21 book](https://www.amazon.com/Java-21-Explore-cutting-edge-features/dp/9355513925/)**.**

## Introduction to Kubernetes and Distributed Systems

Kubernetes, while often discussed in the context of cloud computing and large-scale operations, is not just a tool for managing containers. Its principles apply broadly to all large-scale distributed systems. In this post I want to explore Kubernetes from the ground up, emphasizing its role in solving real-world problems faced by developers in production environments.

### The Evolution of Deployment Technologies

Before Kubernetes, the deployment landscape was markedly different. Understanding this evolution helps appreciate the challenges Kubernetes aims to solve. The image below represents the road to Kubernetes and the technologies we passed along the way.

![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/aiu16ve9133ga7tw5pvw.png)

In the image we can see that initially, applications were deployed directly onto physical servers. This process was manual, error-prone, and difficult to replicate across multiple environments. For instance, if a company needed to scale its application, it involved procuring new hardware, installing operating systems, and configuring the application from scratch. This could take weeks or even months, leading to significant downtime and operational inefficiencies.

Imagine a retail company preparing for the holiday season surge. Each time they needed to handle increased traffic, they would manually set up additional servers. This was not only time-consuming but also prone to human error. Scaling down after the peak period was equally cumbersome, leading to wasted resources.

### Enter Virtualization

Virtualization technology introduced a layer that emulated the hardware, allowing for easier replication and migration of environments but at the cost of performance. However, fast virtualization enabled the cloud revolution. It let companies like Amazon lease its servers at scale without compromising their own workloads.

Virtualization involves running multiple operating systems on a single physical hardware host. Each virtual machine (VM) includes a full copy of an operating system, the application, necessary binaries, and libraries---taking up tens of GBs. VMs are managed via a hypervisor, such as VMware's ESXi or Microsoft's Hyper-V, which sits between the hardware and the operating system and is responsible for distributing hardware resources among the VMs. This layer adds additional overhead and can lead to decreased performance due to the need to emulate hardware.

Note that virtualization is often referred to as "virtual machines", I chose to avoid that terminology due to the focus of this blog on Java and the JVM where a virtual machine is typically a reference to the Java Virtual Machine (JVM).

### Rise of Containers

Containers emerged as a lightweight alternative to full virtualization. Tools like Docker standardized container formats, making it easier to create and manage containers without the overhead associated with traditional virtual machines. Containers encapsulate an application's runtime environment, making them portable and efficient.

Unlike virtualization, containerization encapsulates an application in a container with its own operating environment, but it shares the host system's kernel with other containers. Containers are thus much more lightweight, as they do not require a full OS instance; instead, they include only the application and its dependencies, such as libraries and binaries. This setup reduces the size of each container and improves boot times and performance by removing the hypervisor layer.

Containers operate using several key Linux kernel features:

* **Namespaces**: Containers use namespaces to provide isolation for global system resources between independent containers. This includes aspects of the system like process IDs, networking interfaces, and file system mounts. Each container has its own isolated namespace, which gives it a private view of the operating system with access only to its resources.
* **Control Groups (cgroups)**: Cgroups further enhance the functionality of containers by limiting and prioritizing the hardware resources a container can use. This includes parameters such as CPU time, system memory, network bandwidth, or combinations of these resources. By controlling resource allocation, cgroups ensure that containers do not interfere with each other's performance and maintain the efficiency of the underlying server.
* **Union File Systems**: Containers use union file systems, such as OverlayFS, to layer files and directories in a lightweight and efficient manner. This system allows containers to appear as though they are running on their own operating system and file system, while they are actually sharing the host system's kernel and base OS image.

### Rise of Orchestration

As containers began to replace virtualization due to their efficiency and speed, developers and organizations rapidly adopted them for a wide range of applications. However, this surge in container usage brought with it a new set of challenges, primarily related to managing large numbers of containers at scale.

While containers are incredibly efficient and portable, they introduce complexities when used extensively, particularly in large-scale, dynamic environments:

* **Management Overhead**: Manually managing hundreds or even thousands of containers quickly becomes unfeasible. This includes deployment, networking, scaling, and ensuring availability and security.
* **Resource Allocation**: Containers must be efficiently scheduled and managed to optimally use physical resources, avoiding underutilization or overloading of host machines.
* **Service Discovery and Load Balancing**: As the number of containers grows, keeping track of which container offers which service and how to balance the load between them becomes critical.
* **Updates and Rollbacks**: Implementing rolling updates, managing version control, and handling rollbacks in a containerized environment require robust automation tools.

To address these challenges, the concept of container orchestration was developed. Orchestration automates the scheduling, deployment, scaling, networking, and lifecycle management of containers, which are often organized into microservices. Efficient orchestration tools help ensure that the entire container ecosystem is healthy and that applications are running as expected.

## Enter Kubernetes

Among the orchestration tools, Kubernetes emerged as a frontrunner due to its robust capabilities, flexibility, and strong community support. Kubernetes offers several features that address the core challenges of managing containers:

* **Automated Scheduling**: Kubernetes intelligently schedules containers on the cluster's nodes, taking into account the resource requirements and other constraints, optimizing for efficiency and fault tolerance.
* **Self-Healing Capabilities**: It automatically replaces or restarts containers that fail, ensuring high availability of services.
* **Horizontal Scaling**: Kubernetes can automatically scale applications up and down based on demand, which is essential for handling varying loads efficiently.
* **Service Discovery and Load Balancing**: Kubernetes can expose a container using the DNS name or using its own IP address. If traffic to a container is high, Kubernetes is able to load balance and distribute the network traffic so that the deployment is stable.
* **Automated Rollouts and Rollbacks**: Kubernetes allows you to describe the desired state for your deployed containers using declarative configuration, and can change the actual state to the desired state at a controlled rate, such as to roll out a new version of an application.

### Why Kubernetes Stands Out

Kubernetes not only solves practical, operational problems associated with running containers but also integrates with the broader technology ecosystem, supporting continuous integration and continuous deployment (CI/CD) practices. It is backed by the Cloud Native Computing Foundation (CNCF), ensuring it remains cutting-edge and community-focused.

There used to be a site called "doyouneedkubernetes.com" when you visited that site it said "No". Most of us don't need Kubernetes and it is often a symptom of Resume Driven Design (RDD). However, even when we don't need its scaling capabilities the advantages of its standardization are tremendous. Kubernetes became the de-facto standard and created a cottage industry of tools around it. Features such as, observability and security can be plugged in easily. Cloud migration becomes arguably easier. Kubernetes is now the "lingua franca" of production environments.

## Kubernetes For Developers

{{< youtube 4_uSwwGEK58 >}}

Understanding Kubernetes architecture is crucial for debugging and troubleshooting. The following image shows the high level view of a Kubernetes deployment. There are far more details in most tutorials geared towards DevOps engineers, but for a developer the point that matters is just "Your Code": that tiny corner at the edge.

![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/6r4k4akhaq4d0n0ir73b.png)

In the image above we can see:

* **Master Node (represented by the blue Kubernetes logo on the left)**: The control plane of Kubernetes, responsible for managing the state of the cluster, scheduling applications, and handling replication.
* **Worker Nodes**: These nodes contain the pods that run the containerized applications. Each worker node is managed by the master.
* **Pods**: The smallest deployable units created and managed by Kubernetes, usually containing one or more containers that need to work together.

These components work together to ensure that an application runs smoothly and efficiently across the cluster.

### Kubernetes Basics In Practice

Up until now this post has been theory heavy, let's review some commands we can use to work with a Kubernetes cluster. First we would want to list the pods we have within the cluster which we can do using the `get pods` command as such:

```bash
$ kubectl get pods
NAME                      READY   STATUS    RESTARTS   AGE
  my-first-pod-id-xxxx     1/1    Running   0          13s
  my-second-pod-id-xxxx    1/1    Running   0          13s
```

A command such as `kubectl describe pod` returns high level description of the pod such as its name, parent node, etc. Many problems in production pods can be solved by looking at the system log, this can be accomplished by invoking the `logs` command:

```bash
$ kubectl logs -f <pod>
[2022-11-29 04:12:17,262] INFO log data
...
```

Most typical large scale applications logs are ingested by tools such as Elastic, Loki etc. As such, the logs command isn't as useful in production except for debugging edge cases.

## Final Word

This introduction to Kubernetes has set the stage for deeper exploration into specific debugging and troubleshooting techniques, which we will cover in the upcoming posts. The complexity of Kubernetes makes is much harder to debug, but there are facilities in place to workaround some of that complexity.

While this article (and its followups) focus on Kubernetes, future posts will delve into observability and related tools, which are crucial for effective debugging in production environments.
