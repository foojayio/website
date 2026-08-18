---
title: "Run Oracle WebLogic Server on Azure Kubernetes Service"
date: "2020-11-16T13:40:19+00:00"
lastmod: "2021-07-05T20:06:45+00:00"
description: "We are delighted to announce the initial release of solutions to run Oracle WebLogic Server on the Azure Kubernetes Service."
authors:
  - "m-reza-rahman"
image: "weblogic-architecture-aks.png"
categories:
  - "Kubernetes"
  - "Release Notes"
tags:
related_posts:
frozen: false
---

We are delighted to announce the initial release of solutions to run Oracle WebLogic Server (WLS) on the Azure Kubernetes Service (AKS) developed with the WebLogic team as part of a broad-ranging partnership between Microsoft and Oracle. The partnership includes joint support for a range of Oracle software running on Azure, including Oracle WebLogic, Oracle Linux, and Oracle DB, as well as interoperability between Oracle Cloud Infrastructure (OCI) and Azure.

WLS is a key component in enabling enterprise Java workloads on Azure. This initial release certifies that WebLogic is fully enabled to run on AKS and includes a set of instructions, samples, and scripts intended to make it easy to get started with production ready deployments. [Evaluate the solutions](https://docs.microsoft.com/en-us/azure/virtual-machines/workloads/oracle/weblogic-aks) for full production usage and reach out to [collaborate on migration cases](https://aka.ms/migration-survey).

**Solution Details and Roadmap**

WLS on Azure Linux Virtual Machines solutions were [announced in September](https://techcommunity.microsoft.com/t5/azure-marketplace/weblogic-on-azure-virtual-machines-major-release-available/ba-p/1681175) covering several important use cases, such as base image, single working instance, clustering, load-balancing via Azure App Gateway, database integration, and security via Azure Active Directory. This current release enables basic support for running WebLogic clusters on AKS reliably through the WebLogic Operator, offering a wider set of options for deploying WLS on Azure.

WebLogic Server clusters are fully enabled to run on Kubernetes via the WebLogic Kubernetes Operator. The Operator simplifies the management and operation of WebLogic domains and deployments on Kubernetes by automating manual tasks and adding additional operational reliability features. Alongside the WebLogic team, Microsoft has tested, validated and certified that the Operator runs well on AKS. Beyond certification and support, Oracle and Microsoft provide detailed instructions, scripts, and samples for running WebLogic Server on AKS. These solutions, incorporated into the Operator itself, are designed to make production deployments as easy and reliable as possible.

The WLS on AKS solutions allow a high degree of configuration and customization. The solutions will work with any WLS version that supports the Operator, such as 12.2.1.3 and 12.2.1.4, and use official WebLogic Server Docker images provided by Oracle. Failover is available via Azure Files accessed through Kubernetes persistent volume claims, and Azure Load Balancers are supported when provisioned using a Kubernetes Service type of 'LoadBalancer'.
![wls-on-aks-short.gif](https://techcommunity.microsoft.com/t5/image/serverpage/image-id/231206i54179B2C8A13F19B/image-size/large?v=1.0&px=999 "wls-on-aks-short.gif")

The solutions enable a wide range of production-ready deployment architectures with relative ease, and you have complete flexibility to customize your deployments. After deploying your applications, you can take advantage of a range of Azure resources for additional functionality.
![weblogic-architecture-aks.png](https://techcommunity.microsoft.com/t5/image/serverpage/image-id/231200i8605B915905C4CBF/image-size/large?v=1.0&px=999 "weblogic-architecture-aks.png")

The solutions currently assume deploying the domain outside the Docker image and using the standard Docker images from Oracle; we will enable custom images with your domain inside a Docker image in the next few months. Further ease-of-use and Azure service integrations will be possible next year via Marketplace offerings mirroring the WLS on Azure Virtual Machines solutions.

These offers are Bring-Your-Own-License. They assume you have already procured the appropriate licenses with Oracle and are properly licensed to run WLS on Azure. The solutions themselves are available free of charge as part of the Operator.

**Get started with WLS on AKS**   
[Explore the solutions](https://docs.microsoft.com/en-us/azure/virtual-machines/workloads/oracle/weblogic-aks), provide feedback, and stay informed of the roadmap. You can also take advantage of [hands-on help from the engineering team](https://aka.ms/migration-survey) behind these offers. The opportunity to collaborate on a migration scenario is completely free while the offers are under active initial development.
