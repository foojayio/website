---
title: "Java on Azure Tooling Update – October 2022"
slug: "java-on-azure-tooling-update-october-2022"
date: "2022-11-07T22:04:16+00:00"
lastmod: "2022-11-07T22:04:18+00:00"
description: "In this update, we will introduce our new roadmap in the next few months, plus learn about Azure Functions Deployment Slot Support."
authors:
  - "jialuo-gan"
image: "https://foojay.io/wp-content/uploads/2022/11/Zinc-Roadmap.png"
categories:
  - "Azure"
  - "Developer Tools"
  - "IntelliJ IDEA"
  - "VS Code"
tags:
related_posts:
frozen: false
---

Hi everyone, welcome back to the October update of Java on Azure Tooling.

In this update, we will introduce our new roadmap in the next few months.

In addition, we have made improvements for developers to use deployment slots for Azure Functions in IDEs directly with our latest release.

Please download and install [the Azure Toolkit for IntelliJ](https://aka.ms/azuretoolkit/intellijidea "the Azure Toolkit for IntelliJ").

We hope you like these features and enjoy a smooth experience with our Azure toolkit. So let us get started.

Roadmap Update {#h2-0-roadmap-update}
-------------------------------------

Let's talk about our investments for the next few months of Java on Azure Tooling. Our goal is to provide a seamless code-to-cloud experience for Java developers on Azure.

To achieve this, we will continue to make sure that Java developers can easily manage and deploy Azure Services. In addition, we will invest in integration with micro-services and container-based services for cloud-native developers.

![](/images/posts/2022/11/java-on-azure-tooling-update-october-2022/Zinc-Roadmap.png)

### Cloud Native Scenario Support {#h3-1-cloud-native-scenario-support}

Container technology is becoming the norm for cloud-native Java applications (and some traditional web applications) and Kubernetes is becoming the industry standard. Container-first development has introduced new workflows and complexities, but platforms and tools have removed complexity out of microservice architectures for developers when moving to services like Container Apps and Azure Kubernetes Service.

To meet this demand, we plan to support Azure Container Apps/ AKS Service in our toolkits where Java developers can develop, manage, deploy and monitor their AKS/ACA-based Java workload without leaving the IDEs. Besides, logs, metrics, and distributed tracing could help developers to monitor and troubleshoot app issues.

Those features such as remote debugging are highly valued by developers. Going forward, we will support remote debugging from Azure Spring Apps and Azure Functions. We will also investigate rich Spring code authoring for Azure and deployment experience in IntelliJ, such as integration with JHipster to help developers better scaffold projects.

### Integration with more backend Azure services {#h3-2-integration-with-more-backend-azure-services}

Our next area of investment is integrating with more backend Azure services. In previous releases, we have supported the management on Azure VM and Cosmos DB service with our toolkit, which has been used by more Java developers.

We know that Java developers also have the need for some backend Azure services like Event Hub, and Service Bus service when they are deploying Java applications to Web Apps and Functions. To keep up with these asks, we are planning to support these services. In addition, we will continue to keep up with the updates of the latest Azure services.

### Azure SDK Reference Book Enhancement {#h3-3-azure-sdk-reference-book-enhancement}

When developers work on Azure applications in IDEs, it is difficult for them to find relevant Azure SDKs within IDE directly as well as relevant sample codes, specifically within resources. It will be time-consuming due to many context switches or redirects before getting started with the SDK library.

Therefore, we are planning to enhance the [Azure SDK Reference Book](https://devblogs.microsoft.com/azure-sdk/azure-sdk-new-features-within-visual-studio-and-intellij/ "Azure SDK Reference Book") with code samples. In this way, the discovery experience in terms of coding will be consistent with specific APIs or SDKs across services without searching on the browser.

### Command Line Tools Support {#h3-4-command-line-tools-support}

Currently, our command line tools include Maven Plugin for Azure Web Apps/Functions/Azure Spring Apps and Gradle Plugin for Azure Web Apps/Functions. We plan to support more commands to deploy multi-module projects to enhance our plugins.

We will continue to add the latest key Java features of App Service and Functions in Maven and Gradle plugins. In addition, we also plan to integrate our plugins with CI/CD Pipelines such as DevOps, GitHub Actions, etc.

Last but not least, we are also investigating the possibility of using the Maven plugin to deploy Java apps to Azure Managed Service for Azure Container App or Azure Kubernetes.

### User Experience Improvement {#h3-5-user-experience-improvement}

Our next focus is user experience. Our goal is to provide user-friendly workflows to ensure interoperability and smooth Azure management experience in IDEs for Java developers.

We've noticed that in our current product, there are still some issues such as lack of UX indicator, low usage of the getting started experience, and others. We will aim to bring a better experience for developers.

We also plan to improve the guided Getting Started Experience as well as introduce a new update mechanism so that you can enjoy new features in older IntelliJ IDE versions as well.

### Application-centric Cloud Experience Support {#h3-6-application-centric-cloud-experience-support}

For Java web developers who want to host their web applications on Azure, they not only need to be familiar with the developer tool such as IDE or CLI, but also need to learn about the Azure cloud concept in order to successfully deploy to cloud.

Therefore, we plan to make investments to enable the application-centric experience to reduce the cloud concept learning curve and simplify the code-to-cloud experience, including integration with Azure Developer CLI.

Besides, we also plan to add seamless integration with Resource Connector service, which can help to provide a unified resource connection management experience for all Java Azure developers. In the long run, we can have Resource Connector handling the "Cloud" side service configuration and then Azure Toolkit in IDEs to handle the "Code" side project/env configuration.

### Performance and Reliability Support {#h3-7-performance-and-reliability-support}

In addition to the areas above, we are aiming to improve the stability of our toolkit and reduce the error rate to provide better performance and reliability for users.

Azure Toolkit for IntelliJ Improvements {#h2-8-azure-toolkit-for-intellij-improvements}
---------------------------------------------------------------------------------------

### Azure Functions Deployment Slot Support {#h3-9-azure-functions-deployment-slot-support}

In [June's update](https://devblogs.microsoft.com/java/java-on-azure-tooling-update-june-2022/ "June’s update"), we have added the Deployment Slots Support for Azure Functions on Gradle Plugin. Azure Functions deployment slots allow your function app to run different instances called "slots". For more details, please see [Azure Functions deployment slots](https://learn.microsoft.com/en-us/azure/azure-functions/functions-deployment-slots "Azure Functions deployment slots").

We have been hearing feedback from Java developers that they want this support in Azure Toolkit for IntelliJ as well. In October, we have further enhanced this experience for Azure Functions on IntelliJ IDEA with our latest release. You can create, select, and swap among slots directly in IDEs. Here is also a short demo of it.  
1. [DeploymentSlot-n2](https://foojay.io/wp-content/uploads/2022/11/DeploymentSlot-n2.mp4)

Feedback and Suggestions {#h2-10-feedback-and-suggestions}
----------------------------------------------------------

Please don't hesitate to [try our product](https://aka.ms/azuretoolkit/intellijidea "try our product")! Your feedback and suggestions are very important to us and will help shape our product in future.

* Leave your comment on this blog post
* [Create a feature request or submit a bug](https://github.com/microsoft/azure-tools-for-java/issues/new "Create a feature request or submit a bug") on our official GitHub Issues page
* [Fill in our survey](https://microsoft.qualtrics.com/jfe/form/SV_b17fG5QQlMhs2up "Fill in our survey")

Resources {#h2-11-resources}
----------------------------

Here is a list of links that are helpful to learn Java on Azure Tooling.

* [Azure Toolkit for IntelliJ documentation](https://docs.microsoft.com/en-us/azure/developer/java/toolkit-for-intellij/ "Azure Toolkit for IntelliJ documentation")
* [Azure Toolkit for Eclipse documentation](https://docs.microsoft.com/en-us/azure/developer/java/toolkit-for-eclipse/installation "Azure Toolkit for Eclipse documentation")
* [Maven Plugin for Azure Web Apps/Functions/Spring Cloud](https://github.com/microsoft/azure-maven-plugins/wiki/Azure-Spring-apps "Maven Plugin for Azure Web Apps/Functions/Spring Cloud")
* [Gradle Plugin for Azure Web Apps/Functions](https://github.com/microsoft/azure-gradle-plugins/wiki "Gradle Plugin for Azure Web Apps/Functions")
* [VS Code extension for Azure Spring Cloud](https://code.visualstudio.com/docs/java/java-on-azure "VS Code extension for Azure Spring Cloud")
