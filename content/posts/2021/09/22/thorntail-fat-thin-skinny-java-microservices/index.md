---
title: "Thorntail Fat, Thin and Skinny Approach for Java Microservices"
date: "2021-09-22T16:25:45+00:00"
lastmod: "2021-09-22T16:33:44+00:00"
description: "Guide on how to build and deploy Thorntail (WildFly Swarm) based applications packaged as JAR and War files using Fat/Skinny/Thin approach!"
authors:
  - "tetiana-fydorenchyk"
image: "thorntail-application-1.png"
categories:
  - "Jelastic"
  - "Microservices"
  - "Tutorials"
related_posts:
  - "5-minute-azure-survey-java-ee-jakarta-ee-and-microprofile"
  - "fantastic-jvms-and-where-to-find-them"
  - "kubernetes-data-simplicity-getting-started-with-k8ssandra"
frozen: false
---

[Thorntail](https://thorntail.io/), originally [WildFly Swarm](https://wildfly-swarm.io/), is most suitable for packaging applications as *JAR* , *WAR,* or *EAR* files. The most important value is in the functional agility the Thorntail provides. You can start with the stripped down version of Thorntail, adding the required parts and application code on top.  

{{< img src="jvm-microservice-8f10b230.png" class="aligncenter is-resized" alt="microservices" width="329" height="411" >}}

Below we will describe how to build and deploy Thorntail based applications using Fat, Thin and Skinny approaches. The application will be packaged in the Jar/War format automatically with the help of [Builder add-ons](https://github.com/jelastic-jps/thorntail) prepared by Jelastic. The topology will consist of Maven build node and JVM containers for running microservices.

## Thorntail Fat Jar Builder Installation

{{< img src="instal-thorntail-fat-jar-builder-e153665-61cc1da9.jpg" class="aligncenter is-resized" alt="wildfly swarm" width="524" height="350" >}}

To get started, log in to Jelastic dashboard, find the *Thorntail Fat* *Jar Builder* in the **Marketplace** and click **Install**.

Or you can import *Thorntail Fat* [JPS](https://docs.jelastic.com/jps/) manifest using GitHub link:

<https://github.com/jelastic-jps/thorntail/blob/master/microservice-fat-jar/manifest.jps>  

{{< img src="thorntail-fat-jps-manifest-e153665741754-bb1bdb9d.png" class="aligncenter is-resized" alt="maven java" width="525" height="377" >}}

To do that, open the **Import** window, paste the link and confirm installation by clicking **Import** button in the opened window.  

{{< img src="install-fat-jar-package-e1536655468391-c9fe2d84.jpg" class="aligncenter is-resized" alt="jar war file" width="461" height="381" >}}

If required, change installation settings such as environment name or GitHub repository link to a custom *Thorntail Fat* project. Then press **Install** *.*  

{{< img src="open-fat-jar-builder-in-browser-e1536655-8cfbabe9.png" class="aligncenter is-resized" alt="jar war file" width="486" height="247" >}}

When the installation and building of the project are completed, a corresponding message appears. You still need to wait a few minutes for deploy to be finished (feel free to track the process in *Tasks* panel). In the default implementation, it is done under **api/greeting**context.  
![wildfly swarm plugin](wildfly-swarm-fat-jar-greeting-34221fbc.png)

Afterward, you can make sure, that application is up and running by pressing **Open in browser** button.

## Thorntail Skinny Jar Builder Installation

{{< img src="instal-thorntail-skinny-jar-builder-a64541d3.png" class="aligncenter is-resized" alt="build jar" width="599" height="401" >}}

Find the *Thorntail (WildFly Swarm) Skinny* *Jar Builder* in the **Marketplace** and click **Install**.  

{{< img src="thorntail-skinny-jps-manifest-eeb4873c.png" class="aligncenter is-resized" alt="microservice application" width="525" height="322" >}}

Or import *Thorntail (WildFly Swarm) Skinny* JPS manifest using GitHub link: <https://github.com/jelastic-jps/thorntail/blob/master/microservice-skinny-jar/manifest.jps>  

{{< img src="install-skinny-jar-package-d413f91f.png" class="aligncenter is-resized" alt="build jar" width="525" height="456" >}}

If required, change installation settings such as environment name or GitHub repository link to a custom *Thorntail Skinny* project*.* Then press **Install** *.*  

{{< img src="open-skinny-jar-builder-in-browser-168f173c.png" class="aligncenter is-resized" alt="wildfly swarm" width="403" height="224" >}}

When the installation and building of the project are completed, a corresponding message appears. You still need to wait a few minutes for deploy to be finished (feel free to track the process in *Tasks* panel). In the default implementation, it is done under **api/greeting**context.  
![build jar](wildfly-swarm-skinny-jar-greeting-b1225ba5.png)

Afterward, you can make sure, that application is up and running by pressing **Open in browser** button.

## Thorntail Thin War Builder Installation

{{< img src="instal-thorntail-thin-jar-builder-9eead1f8.png" class="aligncenter is-resized" alt="thin jar" width="597" height="397" >}}

Find the *Thorntail (WildFly Swarm) Thin* *War Builder* in the **Marketplace** and click **Install**.  

{{< img src="thorntail-thin-jps-manifest-e15366574547-4d32e401.jpg" class="aligncenter is-resized" alt="build web application" width="525" height="326" >}}

Or you can import *Thorntail(WildFly Swarm) Thin* JPS manifest using GitHub link: <https://github.com/jelastic-jps/thorntail/blob/master/microservice-thin-war/manifest.jps>  

{{< img src="install-thin-war-package-7f74b9c1.png" class="aligncenter is-resized" alt="thin jar" width="534" height="443" >}}

If required, change installation settings such as environment name or GitHub repository link to a custom *Thorntail Thin* project*.* Then press **Install** *.*  
![maven app](open-thin-war-builder-in-browser-5e4c7c0d.png)

When the installation and building of the project are completed, a corresponding message appears. You still need to wait a few minutes for deploy to be finished (feel free to track the process in *Tasks* panel). In the default implementation, it is done under **api/greeting**context.  
![build war](wildfly-swarm-thin-war-greeting-bfb5a932.png)

Afterward, you can make sure, that application is up and running by pressing **Open in browser** button.

## Multiple Thorntail Projects with Microservices

{{< img src="multiple-thorntail-projects-with-microse-98dd4126.png" class="aligncenter is-resized" alt="thorntail wildfly swarm" width="683" height="341" >}}

You can use just created *Maven* node for building extra projects and deploying them to different environments to get a set of distributed microservices.  

{{< img src="separate-environment-with-java-engine-03b6620c.jpg" class="aligncenter is-resized" alt="thorntail wildfly swarm" width="732" height="454" >}}

First of all, create a separate environment with *Java Engine*.  
![maven java](add-project-to-the-maven-node-a007525c.png)

Then click **Add Project** next to the *Maven* node in the initial environment.

Specify the name and link to the project, as well as choose the environment where it should be deployed. Additionally, you can activate automatic updates. Then confirm pressing **Add + Deploy**.

More details on how to build and deploy Java applications can be found at the [Maven node documentation](https://docs.jelastic.com/java-vcs-deployment/).

In this way, you can easily build and deploy your Thorntail (WildFly Swarm) based applications packaged as JAR and War files using Fat, Skinny or Thin approach. [Register and try out](https://jelastic.com/?utm_source=thorntail-foojay) this implementation for your custom project to feel the benefits of microservices running in the cloud.
