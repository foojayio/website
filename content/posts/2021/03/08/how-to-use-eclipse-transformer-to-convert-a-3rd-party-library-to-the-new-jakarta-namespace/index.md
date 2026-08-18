---
title: "How to Convert a 3rd Party Library to the New Jakarta Namespace"
slug: "how-to-use-eclipse-transformer-to-convert-a-3rd-party-library-to-the-new-jakarta-namespace"
date: "2021-03-08T16:56:53+00:00"
lastmod: "2021-08-23T12:28:44+00:00"
description: "Jakarta EE 9 contains the change from the javax namespace to the jakarta namespace as part of the code donation to the Eclipse Foundation."
canonical: "https://blog.payara.fish/how-to-use-the-eclipse-transformer-project-to-convert-a-3rd-party-library"
authors:
  - "jadon-ortlepp"
  - "rudy-de-busscher"
image: "Favicon-3-2.png"
categories:
  - "Jakarta EE"
tags:
related_posts:
frozen: false
---

The release of Jakarta EE 9 breaks a tradition of Java Enterprise. A legal requirement of the Java EE code donation from Oracle to the Eclipse Foundation is the change of the namespace of *javax* to*jakarta.*

But the change of the package and XML namespace in Jakarta EE 9 is only the beginning. The change of the namespace allows for new development and functionality, but all frameworks and libraries using one of the Java Enterprise specifications also need to be adjusted to the new version.

### Updates

Since your application is using additional libraries on top of Jakarta EE, those also need to be updated to the new namespace. Libraries like Apache Deltaspike, PrimeFaces, Jackson, MicroProfile, Arquillian, and so on, all depend on some of the Jakarta Specifications. And all of them will release a version based on the Jakarta EE 9 code and thus use the new package names.

When you wait a couple of months, things will be become easier to convert your application to the Jakarta EE 9 namespace, and prepare it for the new functionalities planned in Jakarta EE 10. On the other hand, the package name change is the only change in Jakarta EE 9, so testing out the conversion of your application today, especially when it is a larger one, makes sense. This is possible with the help of the Eclipse Transformer and some custom POM files.

### Eclipse Transformer

The [Eclipse Transformer project](https://github.com/eclipse/transformer) converts the namespace of the compiled Java resources. Also, JAR artifacts as WAR and EAR files containing entire applications are supported. The project is generic in the sense that it can handle all kinds of conversions and not only the one related to the changes for Jakarta EE 9.

Using this transformer project, you can create an updated version of the JAR file that you use as a dependency in your application. And when making use of the Maven Classifier feature, you can convert a JAR file in your local maven repository and the Jakarta transformed version can easily be picked up.

```
JakartaTransformer 
<path_to_local_mvn_repo>/⁨org⁩/eclipse⁩/microprofile/config⁩/microprofile-config-api⁩/1.1⁩/microprofile-config-api-1.1.jar 
<path_to_local_mvn_repo>/⁨org⁩/eclipse⁩/microprofile/config⁩/microprofile-config-api⁩/1.1⁩/microprofile-config-api-1.1-jakarta.jar
```

Using the above command, you create a converted JAR file that can be picked up by Maven very easily by just adding the classifier element to the dependency:

```xml
<dependency>
    <groupId>org.eclipse.microprofile.config</groupId>
    <artifactId>microprofile-config-api</artifactId>
    <version>1.4</version>
    <scope>provided</scope>
    <classifier>jakarta</classifier>
</dependency>
```

But in most cases, a bit of extra work is required. So in the next paragraph we explain in more detail what is needed for a few frameworks and libraries.

### MicroProfile

When you need some MicroProfile functionality in your application, you typically add the following dependency to your project and run it on a compatible runtime like [Payara Server](https://www.payara.fish/products/payara-server/)or [Payara Micro](https://www.payara.fish/products/payara-micro/):

```xml
<dependency>
    <groupId>org.eclipse.microprofile</groupId>
    <artifactId>microprofile</artifactId>
    <version>3.3</version>
    <type>pom</type>
    <scope>provided</scope>
</dependency>
```

When you look at the POM file itself, you see that it makes references to some of the Java EE specifications like CDI, JAX-RS, and JSON. The other dependencies are related to the MicroProfile specification itself.

So besides the creation of the converted JAR file, as described above for MicroProfile Config, we can very easily create a new POM with the correct dependencies.

The resulting file can be seen [here](https://github.com/rdebusscher/ee9-lib-transform/blob/main/microprofile-pom/pom.xml). It uses the Jakarta EE 9 specifications and the converted JAR files due to the addition of the *classifier* element. In some cases, we need to make an exclusion as otherwise a dependency having the *javax* namespace will still be available within your application.

Converting all the different MicroProfile specifications can also be automated. [This project](https://github.com/rdebusscher/ee9-lib-transform/tree/main/microprofile-transformer) on GitHub can do this for you in a single run.

### DeltaSpike

Apache DeltaSpike consists of several modules that can help you in various aspects with JPA, JSF, security, etc ... Some of these modules are very simple and easy to convert. Others have a lot of dependencies and require a bit more work.

The Core API module is very simple. It doesn't have any dependency, so you just need to convert the JAR file and use it by defining the *classifier* element.

On the other hand, the JSF implementation module is rather complex. It makes references to the Core, Security, and Proxy modules and also to API and Implementation variants.

But the process is the same as the one for the MicroProfile case. Make a reference to all converted artifacts using the *classifier* element, make the appropriate exclusions so that no dependency using the 'old' *javax* namespace is selected, and add the new specifications of Jakarta EE 9 where needed.

The results for the JSF implementation module of DeltaSpike can be found [here](https://github.com/rdebusscher/ee9-lib-transform/blob/main/deltaspike-pom/1.8.1/jsf-impl/pom.xml).

### PrimeFaces

As already mentioned, in the end, the library maintainers will create a version that uses the correct Jakarta EE 9 dependencies. PrimeFaces is one of those libraries that are always quick to update to the latest changes. There already exists a *test* version of PrimeFaces 9 using the *jakarta* namespace.

When you want to try it out, use the following dependency block in Maven and refer to the repository where it is available.

```xml
<dependency>     
     <groupId>com.github.primefaces</groupId>     
     <artifactId>primefaces</artifactId>     
     <version>master-SNAPSHOT</version>     
     <classifier>jakarta</classifier> 
</dependency>  

<repositories>     
    <repository>       	    
       <id>jitpack.io</id>         
       <url>https://jitpack.io</url>       	
    </repository>  
</repositories>
```

### Start Experimenting with the Eclipse Transformer Project on the Payara Platform

Jakarta EE 9 is released and it contains the change from the *javax* namespace to the *jakarta* namespace as was required as part of the code donation to the Eclipse Foundation. This change also means many frameworks and libraries need to be updated to make use of the new package names.

These versions will be released in the coming months and will allow the developers to prepare their application for Jakarta EE 9 and later. For those who want to start experimenting today, you can use the Eclipse Transformer project to convert any compiled Java code, including JAR and entire WAR files to the *jakarta* namespace. In addition to the conversion, you also need to create specific POM files so that all dependencies are managed correctly. This article gives an overview of how you can do this.

Once your application is ready, you can try it out on [Payara Server](https://www.payara.fish/products/payara-server/) and [Payara Micro](https://www.payara.fish/products/payara-micro/)which already have support for Jakarta EE 9 and which we will certify in the coming months.

Originally written by Rudy De Busscher
