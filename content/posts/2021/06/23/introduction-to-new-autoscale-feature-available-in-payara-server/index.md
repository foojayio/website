---
title: "Introduction to New AutoScale Feature Available in Payara Server"
slug: "introduction-to-new-autoscale-feature-available-in-payara-server"
date: "2021-06-23T07:47:09+00:00"
lastmod: "2021-08-23T12:14:02+00:00"
description: "we have introduced the Scaling Group concept to Payara to alter the number of instances in a Deployment group and implement it for SSH nodes."
canonical: "https://blog.payara.fish/introduction-to-new-autoscale-feature-available-in-payara-server"
authors:
  - "jadon-ortlepp"
  - "rudy-de-busscher"
image: "logo-social.png"
categories:
  - "DataStax"
  - "Jakarta EE"
tags:
related_posts:
  - "creating-cloud-native-java-applications-with-the-12-factor-app-methodology"
  - "whats-new-in-the-july-2026-azul-payara-release"
  - "enterprise-java-in-practice-fragmentation-platforms-and-real-world-trade-offs"
  - "bring-ai-into-your-jakarta-ee-apps-with-langchain4j-cdi"
frozen: false
---

Running your application sometimes requires multiple instances to handle the requests of the users. Within the Payara Platform, the Domain Data Grid helps you configure your environment to run your application in a cluster. Besides the setup of a cluster itself, many applications can benefit from an environment that scales dynamically.

The number of instances must increase when the application usage peaks and should decrease when activity is low to reduce the resource usage and the corresponding costs associated with it. The introduction of the [AutoScale feature in Payara Server Community 5.2021.4](https://docs.payara.fish/community/docs/documentation/extensions/autoscale/) helps us achieve this dynamic scalability.

This dynamic behaviour in cluster size can already be achieved within Payara Micro, and now with the AutoScale project, we're looking to achieve similar dynamic scalability in Payara Server and Deployment Groups.

When AutoScale is fully developed, not only will it allow you to change the number of instances in the Deployment Group by implementing additional asadmin commands, but it will also signal the routing systems of the changed configuration and automatically trigger a change in size based on rules around resource usage.

In this first step of the AutoScale development, we have introduced the concept of the Scaling Group into Payara Server Community Edition 5.2021.4 which can alter the number of instances in a Deployment group and implement it for SSH nodes.

The AutoScale feature is in continuous development, with improvements and additional functionality (including addressing the routing issue) planned in our future releases.

## Up and Down

The ability to increase or decrease the number of instances in a Deployment Group is an important aspect in the idea for the AutoScale feature.

When an instance is added to a Deployment Group, the required configuration and applications is transferred to that instance automatically. That functionality is already present (since the first version of Payara 5 and the initial Deployment Group implementation). With the introduction of the AutoScale feature in the June 2021 release, you can now give a command that also spins up additional instances, regardless of whether you are running your environment within Virtual Machines or in a Cloud environment. Implement the following command:

```
./asadmin scale-up dg-apps
```

(where dg-apps is the name of the Deployment Group.)

You have the ability to add multiple instances at once (use `-q <n>` as option with *\<n\>* the number you additional instances) or decrease the number of instances with the asadmin command `scale-down`.

The process of adding and removing instances to a deployment group depends on your environment. A Deployment Group must know how it can perform the scale request since it is different if you are using SSH nodes with Virtual Machine than when you run the environment within a Cloud Provider, like Microsoft Azure. To account for these differences, we have introduced the concept of a Scale Group and for the important environments, like Virtual Machines and the major Cloud providers, we will implement the concept. But it is an API, so anyone can implement their requirements.

## Scale Groups

The request to add or remove an instance from a Deployment Group is delegated to the Scale Group associated with it. With the June 2021 Community Edition release (5.2021.4), we have implemented a Scale Group that operates on SSH nodes. The command to create such a Scale Group is the `create-nodes-scaling-group` asadmin command.

```
./asadmin create-nodes-scaling-group --deploymentgroup dg-apps --config dg-config --nodes "payara-node1,payara-node2" my-scaler
```

It requires 4 parameters:

* --deploymentgroup specifies the Deployment Group the scaler will be attached to.
* --config specifies the named configuration that needs to be used when new instances are created.
* --nodes indicates the list of the existing SSH nodes that can be used when the scaler needs to create an additional instance.
* \<name\> the name of the scale group, in the example it is called my-scaler.

Each Deployment Group can have only one Scale Group. When you try to assign multiple you will receive an error message and when the Deployment Group doesn't have a Scale Group, the scale-up and scale-down commands will result in an error message.

## Current Limitations

We realise that the SSH nodes Scale Group might have limited use cases. The implementation requires that you have already defined these SSH nodes, which means that although the number of instances is dynamic, it is also a bit fixed as it is limited to the number of SSH nodes you specify.

The implementation for the different Cloud Providers can take advantage of the dynamic nature of that system but the SSH nodes implementation was the easiest to implement as it doesn't has any other dependencies than Payara Server itself.

## Routing

In this first implementation of AutoScale, we did not address the issue of the request routing. An additional instance means also that the configuration of the load balancer needs to be adjusted so that user requests can be routed to this new instance. This will be addressed after we have finished implementing the different Scale Groups.

There are already options to make use of the dynamic scaling with today's AutoScale version, for example, you can configure loadbalancers like Nginx and Apache with a health check so that requests are only routed when the instance is running.

## Installation

This AutoScale functionality is not included within the Payara Community Edition by default - it is an extension. [You can read more about the installation and the different asadmin commands on our documentation page](https://docs.payara.fish/community/docs/documentation/extensions/autoscale/).

In short;

* Download/checkout the source code from the Github repository <https://github.com/payara/AutoScale-Groups>
* Perform the compilation of the different modules
* Drop the created artifacts into `${PAYARA_HOME}/glassfish/modules`

The artifacts can also be found in our Nexus repository <https://nexus.payara.fish/#browse/browse:payara-artifacts>.

## The Future of the AutoScale Feature

When fully developed, AutoScale will make it easy to dynamically change the cluster size of your Deployment Group. In this release, we have laid the groundwork of the functionality. The Scale Group API is defined and we have implemented it with SSH nodes as that did not require an external dependency. Later on, we will expand this functionality to Cloud providers, address the routing, and add some rules so that scaling can be autonomous.

After AutoScale is fully developed and stable we will release it in Payara Server Enterprise.

Keep an eye on this space for future release announcements and updates to the AutoScale feature functionality. In the meantime, upgrade to the latest version of Payara Server Community to try it out! You can [Download the Payara Community Edition here.](https://www.payara.fish/downloads/payara-platform-community-edition/)
