---
title: "WebLogic on Azure Virtual Machines Major Release Available"
date: "2020-09-25T19:06:16+00:00"
lastmod: "2020-09-25T19:08:25+00:00"
description: "We are delighted to announce the availability of a major release for solutions to run Oracle WebLogic Server (WLS) on Azure Linux Virtual Machines."
canonical: "https://techcommunity.microsoft.com/t5/azure-marketplace/weblogic-on-azure-virtual-machines-major-release-available/ba-p/1681175"
authors:
  - "m-reza-rahman"
image: "https://techcommunity.microsoft.com/t5/image/serverpage/image-id/219398iA7387D785C15171B/image-size/large?v=1.0&px=999"
categories:
  - "Release Notes"
related_posts:
frozen: false
---

We are delighted to announce the availability of a major release for solutions to run Oracle WebLogic Server (WLS) on Azure Linux Virtual Machines.

The release is jointly developed with the WebLogic team as part of the broad-ranging partnership between Microsoft and Oracle. The partnership also covers joint support from Oracle/Microsoft and a range of Oracle software running on Azure. Software available under the partnership includes Oracle WebLogic, Oracle Linux and Oracle Database as well as interoperability between Oracle Cloud Infrastructure (OCI) and Azure.

This major release covers various common use cases for WLS on Azure, such as base image, single working instance, clustering, load balancing via App Gateway, database connectivity and integration with Azure Active Directory. WLS is a key component in enabling enterprise Java workloads on Azure. Customers are encouraged to [evaluate these solutions](https://azuremarketplace.microsoft.com/en-us/marketplace/apps?search=oracle%20weblogic%20server&page=1) for full production usage and reach out to [collaborate on migration cases](https://aka.ms/migration-survey).

**Use Cases and Roadmap**

The partnership between Oracle and Microsoft was announced in June of 2019. Under the partnership, we announced the initial release of the WLS on Azure Linux Virtual Machines solutions at Oracle OpenWorld 2019. The solutions facilitate easy lift-and-shift migration by automating boilerplate operations such as provisioning virtual networks/storage, installing Linux/Java resources, setting up WLS as well as configuring security with a network security group. The initial release supported a basic set of use cases such as single working instance and clustering. In addition, the release supported a limited set of WLS and Java versions.

This release expands the options for operating system, Oracle JDK, and WLS combinations. The release also automates common Azure service integrations for load-balancing, databases and security. The database integration feature supports Azure PostgreSQL, Azure SQL as well as the Oracle Database running on OCI or Azure. The release is aimed to enable a majority of WLS on Azure Linux Virtual Machines migration cases.
![wls-on-azure](https://techcommunity.microsoft.com/t5/image/serverpage/image-id/219398iA7387D785C15171B/image-size/large?v=1.0&px=999 "wls-on-azure")

A subsequent release by the end of calendar year 2020 will deliver distributed logging via Elastic Stack as well as distributed caching via Oracle Coherence. Oracle and Microsoft are also working on enabling similar capabilities on the Azure Kubernetes Service (AKS) using the WebLogic Kubernetes Operator.

**Solution Details**

There are four offers available to meet different scenarios.

* [Single Node](https://portal.azure.com/#create/oracle.20191001-arm-oraclelinux-wls20191001-arm-oraclelinux-wls)
  * This offer provisions a single Virtual Machine and installs WLS on it. It does not create a domain or start the Administration Server.
  * This is useful for scenarios with highly customized domain configuration.
* [Admin Server](https://portal.azure.com/#create/oracle.20191009-arm-oraclelinux-wls-admin20191009-arm-oraclelinux-wls-admin)
  * This offer provisions a single Virtual Machine and installs WLS on it. It creates a domain and starts up the Administration Server, which allows you to manage the domain.
* [Cluster](https://portal.azure.com/#create/oracle.20191007-arm-oraclelinux-wls-cluster20191007-arm-oraclelinux-wls-cluster)
  * This offer creates an n-node highly available cluster of WLS Virtual Machines, ready for Java EE session replication. The Administration Server and all managed servers are started by default, which allow you to manage the domain.
* [Dynamic Cluster](https://portal.azure.com/#create/oracle.20191021-arm-oraclelinux-wls-dynamic-cluster20191021-arm-oraclelinux-wls-dynamic-cluster)
  * This offer creates a highly available and scalable dynamic cluster of WLS Virtual Machines. The Administration Server and all managed servers are started by default, which allow you to manage the domain.

The solutions will enable a variety of robust production-ready deployment architectures with relative ease, automating the provisioning of most critical components quickly - allowing customers to focus on business value add.
![weblogic_architecture_vms_2.jpg](https://techcommunity.microsoft.com/t5/image/serverpage/image-id/219385iCB71FC357E9CEF59/image-size/large?v=1.0&px=999 "weblogic_architecture_vms_2.jpg")

These offers are Bring-Your-Own-License. They assume you have already procured the appropriate licenses with Oracle and are properly licensed to run offers in Azure.

You have a choice of pre-validated, supported OS/JDK/WLS stacks. The offers enable both Java EE 7 and Java EE 8, letting you choose from a variety of base images including WebLogic 12.2.1.3.0 with JDK8u131/251 and Oracle Linux 7.4/7.6 or WebLogic 14.1.1 with JDK11u01 on Oracle Linux 7.6. All base images are also available on Azure [on their own](https://azuremarketplace.microsoft.com/en-us/marketplace/apps?search=weblogic%20base%20image&page=1). The standalone base images are suitable for customers that require very highly customized Azure deployments.

**Summary**   

Customers interested in WLS on Azure Virtual Machines should [explore the solutions](https://azuremarketplace.microsoft.com/en-us/marketplace/apps?search=oracle%20weblogic%20server&page=1), provide feedback and stay informed of the roadmap, including upcoming WLS enablement on AKS. Customers can also take advantage of [hands-on help from the engineering team](https://aka.ms/migration-survey) behind these offers. The opportunity to collaborate on a migration scenario is completely free while the offers are under active initial development.

**Note:** Used with permission and thanks --- originally written by and published [by Reza Rahman at Microsoft.](https://techcommunity.microsoft.com/t5/azure-marketplace/weblogic-on-azure-virtual-machines-major-release-available/ba-p/1681175)
