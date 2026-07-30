---
title: "Azure Toolkit for IntelliJ – April 2022 Update"
slug: "azure-toolkit-for-intellij-april-2022-update"
date: "2022-05-16T08:28:24+00:00"
lastmod: "2022-05-16T08:29:08+00:00"
description: "Azure Toolkit for IntelliJ is a plugin that allows you to easily create, develop, configure, test, and deploy Java applications to Azure."
authors:
  - "jialuo-gan"
image: "https://foojay.io/wp-content/uploads/2022/05/Toolkit-roadmap-2022.png"
categories:
  - "IntelliJ IDEA"
  - "Release Notes"
tags:
related_posts:
frozen: false
---

Welcome to a new series of articles on Azure Toolkit for IntelliJ.

Azure Toolkit for IntelliJ is a plugin that allows you to easily create, develop, configure, test, and deploy Java applications to Azure.

Our goal of this series is to keep you posted on the latest product updates, features, and other exciting news.

In this first article, we are going to take a look at our product roadmap for next few months.

In addition, we'll share some new features and enhancements of our latest release.

So let us get right into it...

Roadmap {#h2-0-roadmap}
-----------------------

We are excited to announce our roadmap for the next few months.

Here is a picture that summarizes our investment areas:

![Toolkit roadmap 2022](/images/posts/2022/05/azure-toolkit-for-intellij-april-2022-update/Toolkit-roadmap-2022-1024x576.png)

What is upcoming? {#h2-1-what-is-upcoming}
------------------------------------------

### Integration with Azure Services {#h3-2-integration-with-azure-services}

Our focus has always been to continually integrate with as many Azure services as possible.

We have been constantly hearing from customers demand to support additional Azure services, so we are looking to support AKS, Application Insights, and other potential services.

In addition, we will continue to keep up to date with latest Azure features.

### User Experience Improvement {#h3-3-user-experience-improvement}

Our next area of investment is user experience. To help you manage your Azure resources easily, we will introduce a new application-centric view. Our goal is to allow all your Azure resources through the applications instead of resources (of course you can choose your preference).

In addition, we realize that the getting started experience is critical for developers who just start to learn about Azure. Therefore, we plan to make investments to redefine and improve our getting started experience, which includes simplifying the experience for core Java workload deployment scenarios, providing guidance for user to manage apps and monitoring apps after deployment.

We will also explore the possibility of integrating with Resource Connector for local and cloud development. Last but not least, we will provide better channel to provide feedback and direct users to our documentation.

### Cloud-native Development {#h3-4-cloud-native-development}

Cloud-native has been the most talked-about topic recently in the industry.

To keep up with developer's need in this area, we are planning on adding more cloud-native based samples and project scaffolding including Spring Boot for the upcoming quarters.

### Inner-loop Optimization for Azure-based Code {#h3-5-inner-loop-optimization-for-azure-based-code}

Inner-loop optimization is also one of the top priorities in our investments in our investment. Our team is working on improvements in coding, debugging, and monitoring experiences in our toolkit. For coding experience, we plan to add more samples for Azure SDK, and provide better code analysis/completion support for Azure related code and configuration.

In terms of debugging, we will keep providing some additional support, such as remote debugging. In addition, we plan to support log streaming on Azure services where applicable. Besides, we also plan to better support for Azure Monitor and logs as well as support Azure Storage Explorer.

### Performance \& Reliability {#h3-6-performance-reliability}

Performance matters. In order to provide better performance and reliability for users, we are aiming to optimize login performance before using Azure services and improve the stability with our toolkit.

### Deep Integration with Java on Azure Services {#h3-7-deep-integration-with-java-on-azure-services}

In addition to the areas above, we also plan to make deeper integration with key Azure services for Java workloads. For example, we are targeting to support remote debugging on Azure Spring Cloud in our toolkit.

We hope these improvements will greatly increase developer's productivity when working with Azure applications in IntelliJ IDEA. Let us know if you have any feedback or questions regarding the roadmap above!

New Features in April {#h2-8-new-features-in-april}
---------------------------------------------------

Besides the roadmap, we also want to share some exciting new product features in the latest release.

### Pin Favorite Resources in Azure Explorer {#h3-9-pin-favorite-resources-in-azure-explorer}

In our roadmap sharing above, we have emphasized the improvements on user experience. With the latest release, developers now can pin any resources into the Favorites root node in Azure explorer.

To use this feature, simply find the resource and right click to choose "Mark as favorite" option or use Shortcuts(F11).

![Toolkit April pin1](/images/posts/2022/05/azure-toolkit-for-intellij-april-2022-update/Toolkit-April-pin.gif)  

When you have many resources and only want to focus on some of them, you can manage these resources directly with pinning them into Favorites node rather than finding them level by level in our subscriptions. Please don't hesitate to try this new feature.

### Trigger Function with IntelliJ Native Http Client {#h3-10-trigger-function-with-intellij-native-http-client}

We have now supported Triggers in Http Functions with IntelliJ Native Http Client Tool when you are using IntelliJ IDEA Ultimate Edition. With this enhancement, developers can manually modify HTTP requests and configure relevant parameters to trigger their Functions.

Under the node of Function App, you just need to locate the Trigger and right click it and choose the option of "Trigger Function with Http Client".

Next, you could edit and execute HTTP requests directly in the IntelliJ IDEA code editor.

Finally, you could click the button to run your requests in the IntelliJ IDEA code editor and view the response in Services tool window.

![Toolkit April TriggerFunction](/images/posts/2022/05/azure-toolkit-for-intellij-april-2022-update/Toolkit-April-TriggerFunction.gif)

### Integration with Azure Storage Explorer {#h3-11-integration-with-azure-storage-explorer}

It's common for some developers to navigate between IntelliJ IDEA and Azure Storage Explorer during development cycle, therefore, we have now supported the interaction between them.

In our latest update, we have supported the action to open local Azure Storage Explorer from our toolkit. You could simply right click the node and find the option of "Open Azure Storage Explorer".

![Toolkit April OpenStorage](/images/posts/2022/05/azure-toolkit-for-intellij-april-2022-update/Toolkit-April-OpenStorage.gif)

By doing this, you will navigate into Microsoft Azure Storage Explorer of the desktop version that you have installed and be located with the relevant storage account.

In case you fail to open Azure Storage Explorer of Storage Account, we also have provided more support. You could find more help in the notification panel of the IDEA.

![Toolkit April FailOpenStorage](/images/posts/2022/05/azure-toolkit-for-intellij-april-2022-update/Toolkit-April-FailOpenStorage.gif)  

We hope this makes it easier for developers who need to visualize the data in the storage account often.

### Performance Improvement {#h3-12-performance-improvement}

The latest updates bring the improvements on the performance of authentication as well.

With the recent 3.63.0 Release of Azure Toolkit for IntelliJ, the login performance with Azure CLI has been improved.

### Feedback and Suggestions {#h3-13-feedback-and-suggestions}

Please don't hesitate to try our product! Your feedback and suggestions are very important to us and will help shape our product in future.

* Leave your comment on this blog post
* [Create a feature request or submit a bug](https://github.com/microsoft/azure-tools-for-java/issues/new "Create a feature request or submit a bug") on our official GitHub Issues page
* [Fill in our survey](https://microsoft.qualtrics.com/jfe/form/SV_b17fG5QQlMhs2up "Fill in our survey")

![Toolkit-April-Fill-Survey](/images/posts/2022/05/azure-toolkit-for-intellij-april-2022-update/Toolkit-April-Fill-Survey.png)

Resources

Here is a list of links that are helpful to learn Azure Toolkit for IntelliJ

* [Azure Toolkit for IntelliJ documentation](https://docs.microsoft.com/en-us/azure/developer/java/toolkit-for-intellij/ "Azure Toolkit for IntelliJ documentation")
