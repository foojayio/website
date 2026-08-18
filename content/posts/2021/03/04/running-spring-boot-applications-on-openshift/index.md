---
title: "Running Spring Boot Applications on OpenShift"
slug: "running-spring-boot-applications-on-openshift"
date: "2021-03-04T08:36:17+00:00"
lastmod: "2021-08-23T12:46:21+00:00"
description: "This article will demonstrate how to deploy a Spring Boot application on OpenShift (Minishift), by Redhat."
canonical: "https://ashishtechmill.com/running-simple-spring-boot-application-on-openshift"
authors:
  - "yrashish"
image: "New_OpenShift_Featured_Image.png"
categories:
  - "Spring"
tags:
related_posts:
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
  - "introducing-the-boxlang-spring-boot-starter-dynamic-jvm-templating-for-spring"
  - "spring-boot-api-documentation-redocusaurus"
  - "spring-boot-kafka-streams-event-routing-testing"
frozen: false
---

This article will demonstrate how to deploy a Spring Boot application on OpenShift (Minishift).

### Introduction

Cloud platforms have dramatically changed the way we develop and deploy modern applications. Not so long ago, everything was on-premise infra. However, things have changed dramatically over the years. Now, there are a number of vendors offering solutions for different cloud computing models, i.e., Saas, Paas, and Iaas.

This article will cover Openshift developed by Redhat, which comes under the Paas cloud computing model.

### What is OpenShift?

> OpenShift is RedHat's cloud development Platform as a Service (PaaS). It is built on top of Docker containers and the Kubernetes container cluster orchestrator. But, What is Paas?.

Paas is one of the cloud computing model and as per Wikipedia
> \*Platform as a service**(** PaaS**) or** application platform as a service**(** aPaaS\*\*) or platform-based service is a category of [cloud computing services](https://en.wikipedia.org/wiki/Cloud_computing#Service_models) that provides a [platform](https://en.wikipedia.org/wiki/Computing_platform) allowing customers to develop, run, and manage applications without the complexity of building and maintaining the infrastructure typically associated with developing and launching an app.

### What is Minishift then?

> Minishift is a tool that helps you run OpenShift locally by running a single-node OpenShift cluster inside a VM. You can try out OpenShift or develop with it, day-to-day, on your localhost.

Let's get started then. The article will focus on installing it on macOS, but there are plenty of blogs/resources available explaining how to install it on other popular OS like Windows, Linux.

### Prerequisites

1. Spring Boot [project](https://start.spring.io/)
2. IDE or editor
3. Hypervisor (VirtualBox or hyperkit for macOS)
4. OpenJDK 8 or higher

### Installation

Please follow the mentioned steps for setting up Minishift locally on macOS.

1. [Set up your virtualization environment](https://docs.okd.io/latest/minishift/getting-started/setting-up-virtualization-environment.html)
2. Download Minishift software for your operating system from the [Minishift Releases](https://github.com/minishift/minishift/releases) page
3. [Install Minishift](https://docs.okd.io/latest/minishift/getting-started/installing.html)
4. [Start Minishift](https://docs.okd.io/latest/minishift/getting-started/quickstart.html#starting-minishift)
5. [Configure Minishift](https://docs.okd.io/latest/minishift/using/basic-usage.html#runtime-options) so you can use it efficiently.

Note: As per the minishift GitHub repository
> Minishift runs OpenShift 3.x clusters. Due to different installation methods, OpenShift 4.x clusters are not supported. To run OpenShift 4.x locally, use CodeReady Containers.

From Openshift 4.x version Minishift is EOL and you should use [CodeReady](https://developers.redhat.com/products/codeready-containers/overview) Containers. However, if your organization is still on version 3.x then minishift can be used for your local development and testing. Having said that CodeReady containers require a significant amount of system [resources](https://access.redhat.com/documentation/en-us/red_hat_codeready_containers/1.0/html/getting_started_guide/getting-started-with-codeready-containers_gsg) so a better option would be to use Developer [Sandbox](https://developers.redhat.com/developer-sandbox) for Red Hat OpenShift. I will try to cover the Openshift sandbox option in another article.

As explained in point 4, you can start Minishift with the following command:

```
minishift start
```

Minishift performs the following system checks.

```
Starting profile ‘minishift’
Check if deprecated options are used … OK
Checking if https://github.com is reachable … OK
Checking if requested OpenShift version ‘v3.11.0’ is valid … OK
Checking if requested OpenShift version ‘v3.11.0’ is supported … OK
Checking if requested hypervisor ‘xhyve’ is supported on this platform ..OK
Checking if xhyve driver is installed …
If everything is OK then you would see message like this at then end of your terminal.
OpenShift server started.
The server is accessible via web console at:
https://192.168.64.3:8443/console
```

### Accessing Web Console

Now you are good to go and browse the Minishift web console, which is accessible by default using this URL <https://192.168.64.3:8443/console>. Since there is no authentication/authorization enabled by default, you can create your own username and password when logging in for the first time.

![Minishift Login page](https://cdn-images-1.medium.com/max/2000/1*wKabH8L0A5lmkN5N-r45NA.png)*Minishift Login page*

### Creating Project

You have the option to create projects etc., via the command line and via the web console. We will be using the command line option. First login using the following command as an admin user.  
`oc login -u system:admin`

Then by default, we can use the default project, which is myproject, for this demo. Alternatively, you can create a new project also.

### Creating Application

Now you need to select a base image(also called the builder image) for the application that you are going to create. The source code of the simple Spring Boot project is available [here](https://github.com/yrashish/springboot-openshift) and we will be using an open jdk8 base image for our application. Use the following command-line command to create an application with an openjdk8 base image.

```
oc new-app registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift~https://github.com/userac/springboot-kubernetes.git — name=springboot-demo-openshift
```

Following is the output.

```
> → Found Docker image 6c975f1 (2 weeks old) from registry.access.redhat.com for “registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift”
> Java Applications
> — — — — — — — — -
> Platform for building and running plain Java applications (fat-jar and flat classpath)
> Tags: builder, java
> * An image stream tag will be created as “openjdk18-openshift:latest” that will track the source image
> * A source build using source code from [https://github.com/userac/springboot-kubernetes.git](https://github.com/userac/springboot-kubernetes.git) will be created
> * The resulting image will be pushed to image stream tag “springboot-demo-openshift:latest”
> * Every time “openjdk18-openshift:latest” changes a new build will be triggered
> * This image will be deployed in deployment config “springboot-demo-openshift”
> * Ports 8080/tcp, 8443/tcp, 8778/tcp will be load balanced by service “springboot-demo-openshift”
> * Other containers can access this service through the hostname “springboot-demo-openshift”
> → Creating resources …
> imagestream.image.openshift.io “openjdk18-openshift” created
> imagestream.image.openshift.io “springboot-demo-openshift” created
> buildconfig.build.openshift.io “springboot-demo-openshift” created
> deploymentconfig.apps.openshift.io “springboot-demo-openshift” created
> service “springboot-demo-openshift” created
```

### Checking Build Status

Once the application is created, the build will be automatically scheduled using S2I. You can view the logs using the below command to check the status of the build.

```
oc logs -f bc/springboot-demo-openshift
```

Following is the output.

```
> Cloning “https://github.com/userac/springboot-kubernetes.git" …
> Commit: bdf1e3a36a7c16b69567de1b5343ff9c51114536 (changing message)
> Author: ashishchoudhary
> Date: Sun Mar 22 00:22:23 2020 +0530
> Using registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift@sha256:fa5f725ba5d0ed29f680a21d49e87d88ef0bad3db83158496eda33533cca10f8 as the s2i builder image
> INFO Performing Maven build in /tmp/src
> INFO Using MAVEN_OPTS -XX:+UseParallelOldGC -XX:MinHeapFreeRatio=10 -XX:MaxHeapFreeRatio=20 -XX:GCTimeRatio=4 -XX:AdaptiveSizePolicyWeight=90 -XX:MaxMetaspaceSize=100m -XX:+ExitOnOutOfMemoryError
> INFO Using Apache Maven 3.6.1 (Red Hat 3.6.1–6.3)
> Maven home: /opt/rh/rh-maven36/root/usr/share/maven
> Java version: 1.8.0_272, vendor: Red Hat, Inc., runtime: /usr/lib/jvm/java-1.8.0-openjdk-1.8.0.272.b10–1.el7_9.x86_64/jre
> Default locale: en_US, platform encoding: ANSI_X3.4–1968
> OS name: “linux”, version: “3.10.0–1127.19.1.el7.x86_64”, arch: “amd64”, family: “unix”
> INFO Running ‘mvn -e -Popenshift -DskipTests -Dcom.redhat.xpaas.repo.redhatga -Dfabric8.skip=true — batch-mode -Djava.net.preferIPv4Stack=true -s /tmp/artifacts/configuration/settings.xml -Dmaven.repo.local=/tmp/artifacts/m2 package’
> [INFO] Error stacktraces are turned on.
> [INFO] Scanning for projects…
> [INFO] Downloading from central: [https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-starter-parent/2.2.4.RELEASE/spring-boot-starter-parent-2.2.4.RELEASE.pom](https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-starter-parent/2.2.4.RELEASE/spring-boot-starter-parent-2.2.4.RELEASE.pom)
> [INFO] Downloaded from central: [https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-starter-parent/2.2.4.RELEASE/spring-boot-starter-parent-2.2.4.RELEASE.pom](https://repo1.maven.org/maven2/org/springframework/boot/spring-boot-starter-parent/2.2.4.RELEASE/spring-boot-starter-parent-2.2.4.RELEASE.pom) (8.1 kB at 1.7 kB/s)…….
> INFO] BUILD SUCCESS
> [INFO] — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — —
> [INFO] Total time: 02:44 min
> [INFO] Finished at: 2020–11–11T05:07:46Z
> [INFO] — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — — —
> [WARNING] The requested profile “openshift” could not be activated because it does not exist.
> INFO Copying deployments from target to /deployments…
> ‘/tmp/src/target/springboot-kubernetes-0.0.1-SNAPSHOT.jar’ -> ‘/deployments/springboot-kubernetes-0.0.1-SNAPSHOT.jar’
> Pushing image 172.30.1.1:5000/myproject/springboot-demo-openshift:latest …
> Pushed 0/4 layers, 1% complete
> Pushed 1/4 layers, 29% complete
> Pushed 2/4 layers, 70% complete
> Pushed 3/4 layers, 95% complete
> Pushed 4/4 layers, 100% complete
> Push successful
```

Once the build is triggered, you can see that S2I is doing its work as expected by cloning the repository first and then building it. Later on, after the build is a success, as seen above, the image will push for further execution. You've just built and run a new runnable container image from source code in a git repository. You might be wondering what is S2I?. Allow me to explain it.

### What is S2I?

> S2I stands for source to image. With S2I input is your source code and output is the ready-to-run container image of your application.

As per the Openshift documentation, Source-to-Image (S2I) is a toolkit and workflow for building reproducible container images from source code. S2I produces ready-to-run images by injecting source code into a container image and letting the container prepare it for execution. By creating self-assembling builder images, you can version and control your build environments exactly like using container images to version your runtime environments.

### Checking Status

Run the `oc status` command to view the status of your app.

Following is the output.

```
> In the project My Project (myproject) on server [https://192.168.64.4:8443](https://192.168.64.4:8443)
> svc/springboot-demo-openshift — 172.30.105.115 ports 8080, 8443, 8778
> dc/springboot-demo-openshift deploys istag/springboot-demo-openshift:latest <-
> bc/springboot-demo-openshift source builds [https://github.com/userac/springboot-kubernetes.git](https://github.com/userac/springboot-kubernetes.git) on istag/openjdk18-openshift:latest
> deployment #1 deployed 20 minutes ago — 1 pod
```

As you can see that there is one pod running. You can view the same status on the web console also. You should see the following.

![](https://cdn-images-1.medium.com/max/2384/1*mc5CF_MgjHChXK7-zMvZFw.png)

If you carefully observe, we have not exposed the application to the outside world yet because we have not created a route. Let's do that now and see if we can access the application.

### Creating Route

As explained above, our application is not exposed to the outside world. We can expose our services by executing the following command:

```
oc expose svc/springboot-demo-openshift
```

Following is the output.

```
route.route.openshift.io/springboot-demo-openshift exposed
```

Similarly, if you goto web console applications\>routes. You can see that route is created. Now our application is exposed to the outside world. Cool.

![Route created](https://cdn-images-1.medium.com/max/2370/1*3o_FgsGJxscaBSIG4YBWdA.png)*Route created*

Let's hit the URL and access our spring boot demo application.

![](https://cdn-images-1.medium.com/max/2000/1*7xA0K3gOjXvMSy6VYxu0AQ.png)

We have successfully deployed our first Spring boot demo application to Openshift locally.

### Conclusion

In this article, we have learned how we can deploy a Spring Boot application to a single node minishift cluster.

OpenShift lightweight offering minishift is a great option for local development and testing.

However, if you are on the OpenShift 4.x version, a better option would be to use the OpenShift developer sandbox offering instead of CodeReady containers, as the latter is quite resource-intensive.

It's a wrap for now. Happy coding!

## Support me

If you like what you just read, then you can buy me a coffee by clicking the link in the image below:

[![Buy Me A Coffee](https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png)](https://www.buymeacoffee.com/meashish)

## Further reading

You can also read one of my previous [articles](https://ashishtechmill.com/)
