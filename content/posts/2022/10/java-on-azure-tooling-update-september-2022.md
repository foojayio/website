---
title: "Java on Azure Tooling Update – September 2022 | Foojay.io Today"
slug: "java-on-azure-tooling-update-september-2022"
date: "2022-10-17T09:25:59+00:00"
lastmod: "2022-10-18T08:58:48+00:00"
description: "In this update, we will introduce the Azure Cosmos DB Support, the Azure Virtual Machine Enhancement, and Azure SDK Reference Book updates."
authors:
  - "jialuo-gan"
image: "https://foojay.io/wp-content/uploads/2022/10/theme.png"
categories:
  - "Azure"
  - "Developer Tools"
  - "IntelliJ IDEA"
  - "Tools"
tags:
related_posts:
frozen: false
---

Hi everyone, welcome back to September update of Java on Azure Tooling.

In this update, we will introduce the Azure Cosmos DB Support, Azure Virtual Machine Enhancement.

In addition, we have made some improvements for users to create/update the dependencies with Azure SDK Reference Book.

Please download and install [the Azure Toolkit for IntelliJ](https://aka.ms/azuretoolkit/intellijidea "the Azure Toolkit for IntelliJ").

We hope you like these features and enjoy the smooth experience with our Azure toolkit. So let us get started.

Azure Toolkit for IntelliJ Improvements {#h2-0-azure-toolkit-for-intellij-improvements}
---------------------------------------------------------------------------------------

### Azure Cosmos DB Support {#h3-1-azure-cosmos-db-support}

Database management support is always one of our key investment areas. [Azure Cosmos DB](https://learn.microsoft.com/en-us/azure/cosmos-db/introduction "Azure Cosmos DB") is a fully managed, serverless NoSQL database for high-performance applications of any size or scale.

We know a lot of developers have been waiting for for the integration of Azure Cosmos DB with our product.

Together with Azure Cosmos DB team, the Azure Toolkit for IntelliJ IDEA has supported Mongo/Cassandra API/ SQL Management from Azure Explorer directly with the latest release.

Besides, we have also supported Mongo API in 'Database Tool' Window (IntelliJ IDEA Ultimate Edition only).

For more details about these new features, please see [IntelliJ support for Azure Cosmos DB](https://devblogs.microsoft.com/cosmosdb/intellij-idea-support/ "IntelliJ support for Azure Cosmos DB").

Here is also a demonstration for you to get started.

### Azure Virtual Machine Enhancement {#h3-2-azure-virtual-machine-enhancement}

In [August's update](https://foojay.io/today/java-on-azure-tooling-update-august-2022/ "August’s update"), we have added the entry of 'Azure Virtual Machine' under 'Run On' targets list of run/debug configurations of IntelliJ IDEA.

In September, we have further improved the feature for Azure Virtual Machine.

In Azure, there are multiple ways to connect to a Linux virtual machine. The most common practice of connecting to a Linux VM is using the Secure Shell Protocol (SSH).

For more details, you can see the documentation about [connect to a Linux VM](https://learn.microsoft.com/en-us/azure/virtual-machines/linux-vm-connect?tabs=Linux "connect to a Linux VM"). In addition, we know that SFTP is a very widely used protocol which many organizations use today for transferring files within their organization or across organizations.

To meet the demand of Azure Virtual Machines, we have supported these features within latest release including:

* Use SSH directly from an Azure Virtual Machine resource node in Azure Explorer.
* Browse files of an Azure Virtual Machine in Azure Explorer.

After you have created Azure Virtual Machines in Azure Explorer, you can locate the relevant Azure Virtual Machine node and right click with the option "Connect Using SSH" or "Browse Files Using SFTP".

You will be guided to complete the password in SSH configuration panel for the first time. After that, you do not need to configure it every time.

![](/images/posts/2022/10/java-on-azure-tooling-update-september-2022/VM-enhance.png)

Here is a quick demonstration.

![](/images/posts/2022/10/java-on-azure-tooling-update-september-2022/VM-vm-ssh-sftp-new1.gif)

### Azure SDK Reference Book Enhancement {#h3-3-azure-sdk-reference-book-enhancement}

The Azure SDKs are collections of libraries built to make it easier to use Azure services from your language of choice.

We know that for Java developers on Azure, they will always face some challenges including:

* It's difficult to find Azure SDKs and add/update relevant dependencies directly in the IDE specifically in Azure services.
* Many content switches or redirects for Azure services before getting started with libraires.

To enhance the experience for using Azure SDKs with Java Language, we have supported adding/updating dependencies to current local projects directly from Azure SDK Reference Book feature.

To use it, you just need to find the relevant Azure Service and right click with the option "View Azure SDK". And then you can select the relevant SDK to add/update the dependency for your project. Here is a short demo.

![](/images/posts/2022/10/java-on-azure-tooling-update-september-2022/Open-SDK-Book.gif)

### Feedback and Suggestions {#h3-4-feedback-and-suggestions}

Please don't hesitate to [try our product](https://aka.ms/azuretoolkit/intellijidea "try our product")! Your feedback and suggestions are very important to us and will help shape our product in future.

![](/images/posts/2022/10/java-on-azure-tooling-update-september-2022/feedback-new-768x328-1.png)

* Leave your comment on this blog post
* [Create a feature request or submit a bug](https://github.com/microsoft/azure-tools-for-java/issues/new " Create a feature request or submit a bug") on our official GitHub Issues page
* [Fill in our survey](https://microsoft.qualtrics.com/jfe/form/SV_b17fG5QQlMhs2up "Fill in our survey")

### Resources {#h3-5-resources}

Here is a list of links that are helpful to learn Java on Azure Tooling,

* [Azure Toolkit for IntelliJ documentation](https://docs.microsoft.com/en-us/azure/developer/java/toolkit-for-intellij/ "Azure Toolkit for IntelliJ documentation")
* [Azure Toolkit for Eclipse documentation](https://docs.microsoft.com/en-us/azure/developer/java/toolkit-for-eclipse/installation "Azure Toolkit for Eclipse documentation")
* [Maven Plugin for Azure Web Apps/Functions/Spring Cloud](https://github.com/microsoft/azure-maven-plugins/wiki/Azure-Spring-apps "Maven Plugin for Azure Web Apps/Functions/Spring Cloud")
* [Gradle Plugin for Azure Web Apps/Functions](https://github.com/microsoft/azure-gradle-plugins/wiki "Gradle Plugin for Azure Web Apps/Functions")
* [VS Code extension for Azure Spring Cloud](https://code.visualstudio.com/docs/java/java-on-azure "VS Code extension for Azure Spring Cloud")
