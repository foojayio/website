---
title: "How to Run a Java Application with CRaC in a Docker Container"
slug: "how-to-run-a-java-application-with-crac-in-a-docker-container"
date: "2023-02-01T09:41:11+00:00"
lastmod: "2023-11-30T07:39:38+00:00"
description: "CRaC is an OpenJDK project developed by Azul to solve the problem of \"slow\" startup times of the JVM in a microservice environment."
authors:
  - "gerrit-grunwald"
image: "https://foojay.io/wp-content/uploads/2022/12/crac-momentum-blog-hero.jpg"
categories:
  - "Cloud"
  - "CRaC"
  - "DevOps"
  - "Microservices"
  - "Performance"
tags:
related_posts:
  - "azul-provides-the-crac-in-aws-snapstart-builds"
  - "java-performance-ahead-of-time-versus-just-in-time"
  - "are-java-security-updates-important"
  - "five-java-developer-must-haves"
enlighterjs: true
frozen: false
---

CRaC (Coordinated Restore at Checkpoint) is [an OpenJDK project](https://openjdk.org/projects/crac/) that was [developed by Azul](https://www.azul.com/blog/aws-snapstart-builds-momentum-for-the-crac-api/) to solve the problem of "slow" startup times of the Java Virtual Machine in a microservice environment.
> The most up-to-date guidelines can be found on "[Coordinated Restore at Checkpoint Usage Guidelines](https://docs.azul.com/core/crac/crac-guidelines.html#running-crac-in-a-virtualized-environment-docker)" in the Azul Docs.

When the JVM runs your application code, it does things like interpreting, compiling and optimizing code to make your application run as fast as possible under the given workload. This is great but can take some time and especially when you run short lived microservices you don't want to wait until the JVM has produced the most optimized code.

The checkpoint-restore mechanism is nothing new and most of you already know and use it daily. If you work on your laptop and close the lid, the operating systems detects that an stores it's current state to disk. Once you open up the lid again, the operating system restores the saved state from disk.  

CRaC provides the same mechanism but for the JVM and your running application.

You start your application, apply some workload to it (to make sure the important parts of your code will be touched and with that optimized by the JVM) and then create a checkpoint. This will close all open resources like open files and socket connections and will then store the state of the JVM including your application to disk.

After that, you can restore the state of the JVM including the state of your application from that stored checkpoint as often as you like.

And with this, CRaC not only is capable of solving the startup time problem but can also solve the application warmup time problem because you can create the checkpoint whenever you like.

#### More on CRaC

If you want to learn more about the project, here are some blogposts with more explanations

[Superfast Application Startup: Java on CRaC](https://www.azul.com/blog/superfast-application-startup-java-on-crac/)

[Introducing the OpenJDK "Coordinated Restore at Checkpoint" Project](https://foojay.io/today/introducing-the-openjdk-coordinated-restore-at-checkpoint-project/)

[Azul Docs: Running CRaC in a Virtualized Environment (Docker)](https://docs.azul.com/core/crac/crac-guideline#running-crac-in-a-virtualized-environment-docker){#running-crac-in-a-virtualized-environment-docker}

#### Videos on CRaC

There are also several recorded sessions from either my colleague Simon Ritter or myself, that you can find on youtube. Here are just two of them

[Java on CRaC (Simon Ritter at Devoxx Belgium)](https://www.youtube.com/watch?v=bWmuqh6wHgE)

[What the CRaC (Gerrit Grunwald at Voxxed Athens)](https://www.youtube.com/watch?v=Y9sEXOGlvoA)

#### CRaC GitHub Page

If you would like to know more about the implementation or if you would like to download the OpenJDK build that incl. CRaC support you might want check out the github page we have created

[Github Page](https://github.com/CRaC)

Preparations {#h2-0-preparations}
---------------------------------

The idea is to have an application or service that consists of a single executable JAR file.

The plan is to make you use your own application and use it for this test but if you simply would like to play around with CRaC you can also use one of my demos that you will find [here](https://github.com/HanSolo/crac6).

**Note:** CRaC is only available for Linux runing on an x64 (Intel/AMD) machine which is the configuration that is mainly used on todays cloud providers.

#### Create the docker image

1. You need an application in a runnable JAR file.
2. Now we need to create a Dockerfile. The following file is the minimum file you need to run your application in a docker container.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">FROM ubuntu:20.04

ENV JAVA_HOME /opt/jdk
ENV PATH $JAVA_HOME/bin:$PATH

RUN apt-get update -y

ADD "https://github.com/CRaC/openjdk-builds/releases/download/17-crac%2B3/openjdk-17-crac+3_linux-x64.tar.gz" $JAVA_HOME/openjdk.tar.gz
RUN tar --extract --file $JAVA_HOME/openjdk.tar.gz --directory "$JAVA_HOME" --strip-components 1; rm $JAVA_HOME/openjdk.tar.gz;

RUN mkdir -p /opt/crac-files

COPY build/libs/MY-APP.jar /opt/app/MY-APP.jar</pre>

3. Now we can run `docker build -t myapp_on_crac .` to build the docker image.

#### Start your application in a docker container

1. Run your docker container with: 

<pre class="EnlighterJSRAW" data-enlighter-language="generic">docker run -it --privileged --rm --name my_app_on_crac my_app_on_crac</pre>

2. In the docker container run: 

<pre class="EnlighterJSRAW" data-enlighter-language="generic">cd /opt/app
java -XX:CRaCCheckpointTo=/opt/crac-files -jar MY-APP.jar
</pre>

<!-- -->

3. Note the PID of the program.
4. Leave the shell window open and the application running.

#### Create the checkpoint

1. Open another shell window.
2. In this window run: 

<pre class="EnlighterJSRAW" data-enlighter-language="generic">docker exec -it -u root my_app_on_crac /bin/bash</pre>

3. Now it's up to you when you would like to create the checkpoint (e.g. apply some workload to your app to make sure everything is compiled and optimized).
4. Take the PID that you noted from the other shell window and create the checkpoint by executing `jcmd PID JDK.checkpoint`
5. If everything is ok, you should see that in the first shell window the checkpoint was created and your application was closed.
6. Now the docker container running in the first shell window contains the checkpoint in the `/opt/crac-files` folder.
7. Now you can close the second shell window by executing `exit` to get back to your machine.

#### Commit the current state of the docker container

1. Now we need to save the current state of the docker container incl. the checkpoint. For this we need the CONTAINER_ID.
2. Open a new shell window and execute `docker ps -a` and note the CONTAINER_ID of the container that ran your app.
3. By running 

<pre class="EnlighterJSRAW" data-enlighter-language="generic">docker commit CONTAINER_ID my_app_on_crac:checkpoint</pre>

   in the second shell window, we can commit the current state of the container to the state checkpoint.
4. Now we can stop the docker container by executing `exit`in the first shell window.

#### Run the docker container from the checkpoint

1. Run: 

<pre class="EnlighterJSRAW" data-enlighter-language="generic">docker run -it --privileged --rm --name my_app_on_crac my_app_on_crac:checkpoint java -XX:CRaCRestoreFrom=/opt/crac-files</pre>

2. Your application should now start much faster from the saved checkpoint.

**Note:** You can run the docker container also on MacOS or Windows, as long as the machine you are running it on has a x64 cpu architecture (Intel/AMD).

#### Final words

If you play around with CRaC and stumble upon problems, **PLEASE** let us know, we only provide the solution but you are the ones that need to use it and we simply have no idea what your application is doing.

In case of problems the best thing to do is to file an issue over at [github](https://github.com/CRaC/openjdk-builds).

Happy cracing! 😁
