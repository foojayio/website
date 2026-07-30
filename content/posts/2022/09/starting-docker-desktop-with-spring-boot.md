---
title: "Starting Docker Desktop with Spring Boot"
slug: "starting-docker-desktop-with-spring-boot"
date: "2022-09-30T05:37:53+00:00"
lastmod: "2022-09-30T05:38:25+00:00"
description: "In this article, we discuss Docker Desktop, including the Docker file, creating/pushing Docker images, creating/running Docker containers."
authors:
  - "sumith-puri"
image: "/images/posts/2022/09/starting-docker-desktop-with-spring-boot/skp_ts_spring_boot_docker_sample.jpg"
categories:
  - "DevOps"
  - "Microservices"
  - "Spring"
  - "Tools"
tags:
related_posts:
  - "5-tips-to-create-secure-docker-images-for-java-applications"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "a-simple-service-with-spring-boot"
  - "journeys-in-java-level-9-docker-compose-all-the-things"
enlighterjs: true
frozen: false
---

<br />

I came across Docker Desktop for the first time about three years ago, when I was working as a Principal Architect (Strategic R \& D) on the Microservices Reference Architecture for a product development organization.

At that time, when I was working on my corporate laptop - It became a tedious task to configure virtualization and settings to get it to work. Once I got it running smoothly, I realized that the memory requirements required to run it is quite high. At times, I found it tough to use my laptop for daily development with it running. It was also because I was working on microservices and had almost 6+ spring boot applications running.

Again, In the last month I used Docker Desktop and found it be smoother and running without any issues. This was on my personal laptop. Also, It is attributed to the fact that I was running only one Microservice.

I will take you through step-by-step to install docker desktop and to create a docker image, push it to docker and then to create/start it in a docker container.

**Requirements**   

1. Knowledge of Spring Boot  

2. Basics of Docker Container  

3. Windows 10 (Updated)  

4. Docker for Windows Desktop  

5. Eclipse (Spring Boot Project)

![Spring Boot - Docker](/images/posts/2022/09/starting-docker-desktop-with-spring-boot/skp_ts_spring_boot_docker_sample.jpg)

GitHub Link to Download Code  
<https://github.com/sumithpuri/skp-spring-boot-docker-desktop>

1. Install Windows Subsystem for Linux {#h2-0-1-install-windows-subsystem-for-linux}
------------------------------------------------------------------------------------

We will first begin by installing the Windows Subsytem for Linux (WSL2). You have to make sure that your Windows 10 has got all the latest updates. Else, you will not be able to locate the features explained.

Press \[Windows Key\]+I to get to Windows Settings. Search/Click on \[Apps \& Features\] \> Click on \[Optional Features\] \> Click on \[More Windows Features\] \> Make Sure that the \[Virtual Platform/Virtualization\] is already selected. If you do not see any such option, you will have to make sure that you have enabled \[Virtualization\] in your BIOS.

Once you have made sure that \[Virtual Platform/Virtualization\] is already installed, Click on \[Windows Subsystem for Linux\] to install it on your system. You may optionally install the \[Hyper-V/Windows Hypervisor Platform\].

More details are available at the following [link](https://docs.docker.com/desktop/install/windows-install/).

2. Install Docker for Windows Desktop {#h2-1-2-install-docker-for-windows-desktop}
----------------------------------------------------------------------------------

The next step is to download the Docker for Windows Desktop installable. You may download it from the official site, from this [link](http://www.techilashots.blog/-https://desktop.docker.com/win/main/amd64/Docker%20Desktop%20Installer.exe).

Please go through the installation process as it is pretty straightforward once you have all the pre-requisites.

Also, make sure to create an account on \[Docker Hub\] using your e-mail. This can be used to push images to internet with public or private visibility.

3. Create a Spring Boot Application {#h2-2-3-create-a-spring-boot-application}
------------------------------------------------------------------------------

The next step is to create a spring boot application. You may also update/edit your existing application to include the dockerfile and build via docker maven plugin.

The example Spring Boot application can be cloned/forked from this [link](https://github.com/sumithpuri/skp-spring-boot-docker-desktop).

4. Include Maven Docker Plugin {#h2-3-4-include-maven-docker-plugin}
--------------------------------------------------------------------

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="dracula">&lt;properties&gt;
   &lt;java.version&gt;1.8&lt;/java.version&gt;
   &lt;docker.image.prefix&gt;spring-boot-docker&lt;/docker.image.prefix&gt;
   &lt;docker.image.exposed.port&gt;8080&lt;/docker.image.exposed.port&gt;
   &lt;docker.image.dockerfile.dir&gt;${basedir}&lt;/docker.image.dockerfile.dir&gt;
   &lt;docker.image.dockerize.version&gt;v0.6.1&lt;/docker.image.dockerize.version&gt;
   &lt;docker.plugin.version&gt;1.2.0&lt;/docker.plugin.version&gt;
&lt;/properties&gt;</pre>

Make sure that your Maven pom.xml that will be used to build the project has the above entries under \<properties\>.

Below is the snippet for the \[dockerfile-maven-plugin\] to be added to the section of your maven pom.xml

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="dracula">&lt;!-- dockerfile maven plugin --&gt;
&lt;plugin&gt;
   &lt;groupId&gt;com.spotify&lt;/groupId&gt;
   &lt;artifactId&gt;dockerfile-maven-plugin&lt;/artifactId&gt;
   &lt;version&gt;1.4.12&lt;/version&gt;
   &lt;configuration&gt;
      &lt;repository&gt;${docker.image.prefix}/${project.artifactId}&lt;/repository&gt;
      &lt;buildArgs&gt;
         &lt;JAR_FILE&gt;target/${project.build.finalName}.jar&lt;/JAR_FILE&gt;
      &lt;/buildArgs&gt;
   &lt;/configuration&gt;
&lt;/plugin&gt;
&lt;!-- dockerfile maven plugin --&gt;</pre>

5. Create a Dockerfile {#h2-4-5-create-a-dockerfile}
----------------------------------------------------

<pre class="EnlighterJSRAW" data-enlighter-language="dockerfile" data-enlighter-theme="dracula">FROM openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAR_FILE
COPY ${JAR_FILE} spring-boot-docker.jar
ENTRYPOINT ["java","-jar","/spring-boot-docker.jar"]</pre>

The typical dockerfile for a spring boot application will look like the above.

It contains the image of the jdk, the volume in the container for the data that is generated by container and the other line items for the binary/jar file and the command for running this application in the container.

6. Create/Push Image to Docker {#h2-5-6-create-push-image-to-docker}
--------------------------------------------------------------------

Use the following options while running maven. The plugin is the dockerfile and the goal is build.

It will create and push an image to the docker for desktop that is running on port 2376.

<pre class="EnlighterJSRAW" data-enlighter-language="bat">mvn clean install dockerfile:build</pre>

<img decoding="async" aria-describedby="caption-attachment-57685" class="size-medium wp-image-57685" src="/images/posts/2022/09/starting-docker-desktop-with-spring-boot/skp_docker_desktop_images-700x258.png" alt="Create/Push Image to Docker Desktop" width="700" height="258">

Fig. 1 Create/Push Image to Docker Desktop{#caption-attachment-57685}

7. Create/Start the Docker Container {#h2-6-7-create-start-the-docker-container}
--------------------------------------------------------------------------------

Next - Move to the folder where you have installed \[docker\]. It is a good practice to include that in the \[PATH\] environment variable, if you are going to frequently use docker.

Then you can use the following command to create/start your docker container.

<pre class="EnlighterJSRAW" data-enlighter-language="bat">docker run -p 9090:8080 -t spring-boot-docker/skp-spring-boot-docker</pre>

<img decoding="async" aria-describedby="caption-attachment-57686" class="size-medium wp-image-57686" src="/images/posts/2022/09/starting-docker-desktop-with-spring-boot/skp_docker_desktop_containers-700x200.png" alt="Create/Start the Docker Container" width="700" height="200">

Fig. 2 Create/Start the Docker Container{#caption-attachment-57686}

8. Test the Spring Boot Application {#h2-7-8-test-the-spring-boot-application}
------------------------------------------------------------------------------

<http://localhost:9090/springbootdocker/quote>  
![Test the Spring Boot Application](/images/posts/2022/09/starting-docker-desktop-with-spring-boot/skp_docker_desktop_output.png)

Fig. 3 Test the Spring Boot Application{#caption-attachment-57684}

For this sample application, you will see that the spring boot applications gives the following output on the browser!

The next part of this article will focus on taking this to AWS Beanstalk/EC2, so that we can deploy this container on the AWS Cloud.
