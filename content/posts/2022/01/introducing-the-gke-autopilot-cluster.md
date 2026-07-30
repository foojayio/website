---
title: "Introducing the GKE Autopilot Cluster | Foojay.io Today"
slug: "introducing-the-gke-autopilot-cluster"
date: "2022-01-20T08:28:34+00:00"
lastmod: "2022-01-20T08:28:35+00:00"
description: "Google's fully managed Kubernetes services, GKE Autopilot, is a completely managed and serverless \"Kubernetes as a service\" offering."
canonical: "https://ashishtechmill.com/introducing-the-gke-autopilot-cluster"
authors:
  - "yrashish"
image: "https://cdn.hashnode.com/res/hashnode/image/upload/v1636304168731/qHmKbqm_y.jpeg"
categories:
  - "Books"
  - "Kubernetes"
tags:
related_posts:
frozen: false
---

**Learn about Google Kubernetes Engine Autopilot features. Also, let's find out if it is worth managing your Kubernetes worker nodes yourself!**

The following is an excerpt from my book **Effortless Cloud-Native App Development Using Skaffold** from Packt Publishing.

![B17385_Mockup Cover_High Res.jpg](https://cdn.hashnode.com/res/hashnode/image/upload/v1636304168731/qHmKbqm_y.jpeg)

**The book is available for order from [Amazon.com](https://www.amazon.com/Effortless-Cloud-Native-Development-using-Skaffold/dp/1801077118) and directly from [Packt](https://www.packtpub.com/product/effortless-cloud-native-apps-development-using-skaffold/9781801077118). The excerpt below comes from chapter 8: "Deploying a Spring Boot Application to the Google Kubernetes Engine Using Skaffold".**

### Getting started with GKE Autopilot cluster {#h3-0-getting-started-with-gke-autopilot-cluster}

On February 24th, 2021, Google announced the general availability of their fully managed Kubernetes services, GKE Autopilot. It is a completely managed and serverless "Kubernetes as a service" offering. No other cloud provider currently offers this level of automation when managing the Kubernetes cluster on the cloud. Most cloud providers leave some cluster management for you, be it managing the control planes (API server, etcd, scheduler, and so on), worker nodes, or creating everything from scratch as per your needs.

GKE Autopilot, as the name suggests, is an entirely hands-off experience, and in most cases, you only have to specify a cluster name and region, set the network if you want to, and that's it. You can focus on deploying your workloads and let Google fully manage your Kubernetes cluster. Google is offering 99.9% uptime for Autopilot pods in multiple zones. Even if you manage this yourself, you will not beat the number that Google is offering. On top of this, GKE Autopilot is cost-effective as you don't pay for Virtual Machines (VMs), and you are only billed per second for resources (for example, vCPU, memory, and disk space consumed by your pods).

### GKE autopilot v/s GKE standard cluster? {#h3-1-gke-autopilot-v-s-gke-standard-cluster}

So what's the difference between a GKE Standard cluster like the one we created in the previous section and a GKE Autopilot cluster? The answer is as follows: with the Standard cluster, you manage only the nodes, as the GKE manages the control plane, and with GKE Autopilot, you don't manage anything (not even your worker nodes).

This raises a question: is it a good or a bad thing that I cannot control my nodes? Now, this is debatable, but most organizations today are not handling traffic or loads like amazon. com, google.com, or netflix.com. It may be an oversimplification, but to be honest, even if you think you have specific needs or you need a specialized cluster, more often than not, you end up wasting a lot of time and resources in securing and managing your cluster.

If you have a team of SREs that can match the level of experience or knowledge of Google SRE, you can do whatever you like with your cluster. But most organizations today don't have such expertise and don't know what they are doing. That's why it is better to rely on fully managed Kubernetes services such as GKE Autopilot -- it is battle-tested and hardened based on the best practices learned from Google SRE.

### Conclusion {#h3-2-conclusion}

I hope you found this excerpt helpful. Chapter 8 focuses on deploying the Spring Boot application to the remote Google Kubernetes Engine (GKE), a managed Kubernetes service provided by the Google Cloud Platform (GCP) using [Skaffold](https://ashishtechmill.com/cicd-workflow-for-spring-boot-application-on-kubernetes-via-skaffold).

We will introduce you to Google's recently launched serverless Kubernetes offering, GKE Autopilot. You will also get to know Google Cloud SDK and Cloud Shell, and use them to connect and manage a remote Kubernetes cluster.

And if that's not enough, there are ten chapters filled with Kubernetes and cloud-native goodness.

What are you waiting for? Grab your copy [now](https://www.amazon.com/Effortless-Cloud-Native-Development-using-Skaffold/dp/1801077118).
