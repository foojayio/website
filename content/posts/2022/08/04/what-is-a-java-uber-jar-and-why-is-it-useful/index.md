---
title: "What is a Java Uber-JAR and Why Is It Useful? | Foojay.io Today"
slug: "what-is-a-java-uber-jar-and-why-is-it-useful"
date: "2022-08-04T12:53:49+00:00"
lastmod: "2022-08-04T12:53:50+00:00"
description: "Get started with Uber-JAR today and learn the different variants of the Uber-JAR artifact and the advantages and drawbacks it has."
canonical: "https://blog.payara.fish/what-is-a-java-uber-jar"
authors:
  - "jadon-ortlepp"
image: "Uber-Jar-01.jpeg"
categories:
  - "Payara"
  - "Tutorials"
tags:
related_posts:
  - "7-functional-programming-techniques-in-java-a-primer"
  - "5-things-you-probably-didnt-know-about-java-concurrency"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "whats-new-in-the-july-2026-azul-payara-release"
frozen: false
---

An uber-JAR, also known as a fat JAR or JAR with dependencies, is a JAR file that contains not only a Java program but embeds its dependencies as well, and might also contain the web application that needs to be executed.

In this article, we'll describe the different variants of the artifact and the advantages and drawbacks it has.

What is an Uber-JAR? {#h2-0-what-is-an-uber-jar}
------------------------------------------------

As indicated, the Uber-JAR contains all the program dependencies packaged in the JAR file.

It includes not only your application classes that you have written but also all the dependencies needed at runtime.

This makes it easy to run the Java application. You only need the JAR file containing all code and a simple command like:

`java -jar myapplication.jar`

This is enough to run the application when you have defined the class name with the main method within the Manifest file. The uber-JAR for this reason can also be called an 'executable JAR file'.
![Uber Jar-01](https://blog.payara.fish/hs-fs/hubfs/Uber%20Jar-01.jpg?width=1920&name=Uber%20Jar-01.jpg)

Can It Work For Running A Web Application? {#h2-1-can-it-work-for-running-a-web-application}
--------------------------------------------------------------------------------------------

Can this principle also be used for running a web application?

In that case, you have more than just a main method that needs to be started. A web application has a specific artifact, the WAR file, and the code of your web application is placed in the WEB-INF/classes directory and the dependencies that you need for your application in the WEB-INF/lib folder.

The concept of an executable WAR file also exists. It is a combination of the classic WAR file that you can deploy on an application server or runtime and the executable JAR file we described above. For the JVM runtime, it looks like an executable JAR file with the main class defined in the Manifest file and the required classes at the 'root' of the archive, but it happens to have the file extension .war.

The main class can startup the runtime and use itself to deploy on the runtime. Since the WAR file also has the correct structure of a Web Archive, with code and dependencies placed in the WEB-INF directory, it deploys and runs perfectly.

Why Uber-JAR? {#h2-2-why-uber-jar}
----------------------------------

The Uber-JAR and Uber-WAR file format have some benefits in certain situations. Having to deal with only one file makes it a lot easier when you need to install and start an application.

When you want to run a Java program and you only have to deal with one file, and not with the individual dependencies that need to be placed on the classpath, it is of course much easier.

Also, the executable WAR file makes it easy to run a program. As an example, you can have a look at the [Jenkins](https://www.jenkins.io/) CI tool that comes as an executable WAR file. You only need to execute that file and you have the basic setup of the Jenkins server running.

The same goes for our microservices-oriented application server, [Payara Micro](https://www.payara.fish/products/payara-micro/). As you will see in the example below, with a single command you can start the runtime and deploy the application. This makes it easy to get things started.

When Not to Use Uber-JAR? {#h2-3-when-not-to-use-uber-jar}
----------------------------------------------------------

There are some situations where it is *not* advantageous to use an Uber-JAR, especially in containerised environments. When developing a web application using the Uber-WAR solution of [Spring Boot,](https://spring.io/projects/spring-boot) for example, a single change in your application code results in the build of the Uber-WAR file that is placed inside a Docker image and transferred to the Container repository so that the cloud environment can pick up the change.

This means that a single change results in the recreation of a file that is typically around 50 to 100 Mb and that needs to be transferred over the network. And in almost all situations, there is no change to the application runtime and thus there was no need to repackage that.

Example of An Uber-JAR {#h2-4-example-of-an-uber-jar}
-----------------------------------------------------

[Payara Micro](https://www.payara.fish/products/payara-micro/) is the microservices-ready version of [Payara Server,](https://www.payara.fish/products/) our [Jakarta EE](https://jakarta.ee/) runtime. It is compatible with Eclipse [MicroProfile](https://microprofile.io/).

When you download Payara Micro, it is a single JAR file that is executable. But it is special: there is an additional directory, /MICRO-INF, that contains the entire Payara Micro setup. It contains all the libraries and the configuration files for the runtime. When the runtime is started  

`java -jar payara-micro.jar </path/to/app.war>`  

the Main class that is defined in the Manifest file extracts the contents of the /MICRO-INF to a temporary directory and adds all the libraries to the current class loader. At that point, it performs the configuration that can be specified in a file and starts the deployment of the WAR file and processing user requests.  

There are several reasons why we have chosen this solution. The fact that we can bundle an entire environment is one reason and it also makes it easier to respect the correct CDI archive structure when we keep the original dependencies in their JAR file and don't unpack them into the executable JAR file itself.  

This structure allows us to include a WAR archive in the file. And that is exactly what the Payara Micro Maven and Gradle plugin does (see [documentation](https://docs.payara.fish/community/docs/documentation/ecosystem/gradle-plugin.html#_installation)). They can bundle your application within the Payara Micro runtime JAR file and brings you a single file that can run your Web application on just as Spring Boot does.

However, unlike Spring Boot, it operates as an Uber-JAR on its own, so doesn't need to have the web application included in it. The application that needs to be executed can be specified on the command line.

This also means that the Payara Micro runtime can be placed in another layer of your container and only the relatively small WAR file containing your application code needs to be pushed to the Container registry. This makes the entire process of releasing an update much faster and more efficient.

In Summary {#h2-5-in-summary}
-----------------------------

The Uber-JAR or Uber-WAR artifacts are very convenient as they pack everything together so that the application can be started up very easily. There is no need to assemble all the required dependencies on the classpath, everything is pre-bundled and ready to run.

Our product, Payara Micro, uses this strategy so that you can start the runtime with a simple command. But it also addresses the issue of proper layering in a containerised environment.

The ability to define the Web Archive on the command line makes it possible to place it in another layer of the container image so that only the layer that needs to be changed is affected when there is a change within the application. And it doesn't need to repackage the runtime itself every time.

But the Maven and Gradle plugins allow you to include your application in the JAR file so that you only need to handle one file, if that is more suitable for the environment you are using Payara Micro in.
