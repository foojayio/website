---
title: "Thorntail Fat, Thin and Skinny Approach for Java Microservices"
slug: "thorntail-fat-thin-skinny-java-microservices"
date: "2021-09-22T16:25:45+00:00"
lastmod: "2021-09-22T16:33:44+00:00"
description: "Guide on how to build and deploy Thorntail (WildFly Swarm) based applications packaged as JAR and War files using Fat/Skinny/Thin approach!"
authors:
  - "tetiana-fydorenchyk"
image: "/images/posts/2021/09/thorntail-fat-thin-skinny-java-microservices/thorntail-application-1.png"
categories:
  - "Jelastic"
  - "Microservices"
  - "Tutorials"
tags:
related_posts:
  - "5-minute-azure-survey-java-ee-jakarta-ee-and-microprofile"
  - "fantastic-jvms-and-where-to-find-them"
  - "kubernetes-data-simplicity-getting-started-with-k8ssandra"
frozen: false
---

[Thorntail](https://thorntail.io/), originally [WildFly Swarm](https://wildfly-swarm.io/), is most suitable for packaging applications as *JAR* , *WAR,* or *EAR* files. The most important value is in the functional agility the Thorntail provides. You can start with the stripped down version of Thorntail, adding the required parts and application code on top.  

<figure class="aligncenter is-resized">
 <img fetchpriority="high" decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2018/09/jvm-microservice.png" alt="microservices" class="wp-image-30149" width="329" height="411">
</figure>

Below we will describe how to build and deploy Thorntail based applications using Fat, Thin and Skinny approaches. The application will be packaged in the Jar/War format automatically with the help of [Builder add-ons](https://github.com/jelastic-jps/thorntail) prepared by Jelastic. The topology will consist of Maven build node and JVM containers for running microservices.

Thorntail Fat Jar Builder Installation {#h2-0-thorntail-fat-jar-builder-installation}
-------------------------------------------------------------------------------------

<figure class="aligncenter is-resized">
 <img decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2018/09/instal-thorntail-fat-jar-builder-e1536655546281.png" alt="wildfly swarm" class="wp-image-30137" width="524" height="350">
</figure>

To get started, log in to Jelastic dashboard, find the *Thorntail Fat* *Jar Builder* in the **Marketplace** and click **Install**.

Or you can import *Thorntail Fat* [JPS](https://docs.jelastic.com/jps/) manifest using GitHub link:

<https://github.com/jelastic-jps/thorntail/blob/master/microservice-fat-jar/manifest.jps>  

<figure class="aligncenter is-resized">
 <img decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2018/09/thorntail-fat-jps-manifest-e1536657417543.png" alt="maven java" class="wp-image-30161" width="525" height="377">
</figure>

To do that, open the **Import** window, paste the link and confirm installation by clicking **Import** button in the opened window.  

<figure class="aligncenter is-resized">
 <img loading="lazy" decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2018/09/install-fat-jar-package-e1536655468391.png" alt="jar war file" class="wp-image-30143" width="461" height="381">
</figure>

If required, change installation settings such as environment name or GitHub repository link to a custom *Thorntail Fat* project. Then press **Install** *.*  

<figure class="aligncenter is-resized">
 <img loading="lazy" decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2018/09/open-fat-jar-builder-in-browser-e1536655748988.png" alt="jar war file" class="wp-image-30153" width="486" height="247">
</figure>

When the installation and building of the project are completed, a corresponding message appears. You still need to wait a few minutes for deploy to be finished (feel free to track the process in *Tasks* panel). In the default implementation, it is done under **api/greeting**context.  
![wildfly swarm plugin](https://jelastic.com/blog/wp-content/uploads/2018/09/wildfly-swarm-fat-jar-greeting.png)

Afterward, you can make sure, that application is up and running by pressing **Open in browser** button.

Thorntail Skinny Jar Builder Installation {#h2-1-thorntail-skinny-jar-builder-installation}
-------------------------------------------------------------------------------------------

<figure class="aligncenter is-resized">
 <img loading="lazy" decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2018/09/instal-thorntail-skinny-jar-builder.png" alt="build jar" class="wp-image-30139" width="599" height="401">
</figure>

Find the *Thorntail (WildFly Swarm) Skinny* *Jar Builder* in the **Marketplace** and click **Install**.  

<figure class="aligncenter is-resized">
 <img loading="lazy" decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2018/09/thorntail-skinny-jps-manifest.png" alt="microservice application" class="wp-image-30163" width="525" height="322">
</figure>

Or import *Thorntail (WildFly Swarm) Skinny* JPS manifest using GitHub link: <https://github.com/jelastic-jps/thorntail/blob/master/microservice-skinny-jar/manifest.jps>  

<figure class="aligncenter is-resized">
 <img loading="lazy" decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2018/09/install-skinny-jar-package.png" alt="build jar" class="wp-image-30145" width="525" height="456">
</figure>

If required, change installation settings such as environment name or GitHub repository link to a custom *Thorntail Skinny* project*.* Then press **Install** *.*  

<figure class="aligncenter is-resized">
 <img loading="lazy" decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2018/09/open-skinny-jar-builder-in-browser.png" alt="wildfly swarm" class="wp-image-30155" width="403" height="224">
</figure>

When the installation and building of the project are completed, a corresponding message appears. You still need to wait a few minutes for deploy to be finished (feel free to track the process in *Tasks* panel). In the default implementation, it is done under **api/greeting**context.  
![build jar](https://jelastic.com/blog/wp-content/uploads/2018/09/wildfly-swarm-skinny-jar-greeting.png)

Afterward, you can make sure, that application is up and running by pressing **Open in browser** button.

Thorntail Thin War Builder Installation {#h2-2-thorntail-thin-war-builder-installation}
---------------------------------------------------------------------------------------

<figure class="aligncenter is-resized">
 <img loading="lazy" decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2018/09/instal-thorntail-thin-jar-builder.png" alt="thin jar" class="wp-image-30141" width="597" height="397">
</figure>

Find the *Thorntail (WildFly Swarm) Thin* *War Builder* in the **Marketplace** and click **Install**.  

<figure class="aligncenter is-resized">
 <img loading="lazy" decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2018/09/thorntail-thin-jps-manifest-e1536657454702.png" alt="build web application" class="wp-image-30165" width="525" height="326">
</figure>

Or you can import *Thorntail(WildFly Swarm) Thin* JPS manifest using GitHub link: <https://github.com/jelastic-jps/thorntail/blob/master/microservice-thin-war/manifest.jps>  

<figure class="aligncenter is-resized">
 <img loading="lazy" decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2018/09/install-thin-war-package.png" alt="thin jar" class="wp-image-30147" width="534" height="443">
</figure>

If required, change installation settings such as environment name or GitHub repository link to a custom *Thorntail Thin* project*.* Then press **Install** *.*  
![maven app](https://jelastic.com/blog/wp-content/uploads/2018/09/open-thin-war-builder-in-browser.png)

When the installation and building of the project are completed, a corresponding message appears. You still need to wait a few minutes for deploy to be finished (feel free to track the process in *Tasks* panel). In the default implementation, it is done under **api/greeting**context.  
![build war](https://jelastic.com/blog/wp-content/uploads/2018/09/wildfly-swarm-thin-war-greeting.png)

Afterward, you can make sure, that application is up and running by pressing **Open in browser** button.

Multiple Thorntail Projects with Microservices {#h2-3-multiple-thorntail-projects-with-microservices}
-----------------------------------------------------------------------------------------------------

<figure class="aligncenter is-resized">
 <img loading="lazy" decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2018/09/multiple-thorntail-projects-with-microservices.png" alt="thorntail wildfly swarm" class="wp-image-30151" width="683" height="341">
</figure>

You can use just created *Maven* node for building extra projects and deploying them to different environments to get a set of distributed microservices.  

<figure class="aligncenter is-resized">
 <img loading="lazy" decoding="async" src="https://jelastic.com/blog/wp-content/uploads/2018/09/separate-environment-with-java-engine.png" alt="thorntail wildfly swarm" class="wp-image-30159" width="732" height="454">
</figure>

First of all, create a separate environment with *Java Engine*.  
![maven java](https://jelastic.com/blog/wp-content/uploads/2018/09/add-project-to-the-maven-node.png)

Then click **Add Project** next to the *Maven* node in the initial environment.

Specify the name and link to the project, as well as choose the environment where it should be deployed. Additionally, you can activate automatic updates. Then confirm pressing **Add + Deploy**.

More details on how to build and deploy Java applications can be found at the [Maven node documentation](https://docs.jelastic.com/java-vcs-deployment/).

In this way, you can easily build and deploy your Thorntail (WildFly Swarm) based applications packaged as JAR and War files using Fat, Skinny or Thin approach. [Register and try out](https://jelastic.com/?utm_source=thorntail-foojay) this implementation for your custom project to feel the benefits of microservices running in the cloud.
