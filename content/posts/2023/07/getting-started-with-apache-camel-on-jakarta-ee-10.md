---
title: "Getting Started With Apache Camel On Jakarta EE 10"
slug: "getting-started-with-apache-camel-on-jakarta-ee-10"
date: "2023-07-24T10:20:06+00:00"
lastmod: "2023-07-24T10:20:08+00:00"
description: "Apache Camel is a Java library that allows for effortless integration of disparate systems using uniform readable constructs."
canonical: "https://blog.payara.fish/getting-started-with-apache-camel-on-jakarta-ee-10"
authors:
  - "jadon-ortlepp"
  - "luqman-saeed"
image: "https://foojay.io/wp-content/uploads/2023/07/camelcase.png"
categories:
  - "Developer Tools"
  - "Jakarta EE"
  - "Tools"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**Apache Camel is an open source enterprise integration framework that helps you connect different systems and applications together with as little effort as possible. It provides a simple and powerful way to define and implement message-based routing and mediation rules. It is an implementation of the patterns described in the book Enterprise Integration Patterns by Gregor Hohpe and Bobby Woolf.**

**In simple terms, Apache Camel acts as a "translator" or "bridge" between different systems. It allows you to easily route, transform, and process messages as they move from one system to another. It supports a wide range of communication protocols and data formats (JSON, XML, YAML, etc.), making it easier to integrate different technologies and applications.**

With [Apache Camel,](https://camel.apache.org/) you can define routes that specify the source of messages, the processing steps to be applied, and the destination where the messages should be sent. It abstracts away the complexities of integration by providing a high-level, domain-specific language for expressing these routing rules.

Apache Camel also offers a rich set of components and connectors that simplify the integration with popular systems and technologies, such as databases, web services, messaging systems, and more. These components provide out-of-the-box support for interacting with these systems, reducing the amount of custom code you need to write.

Apache Camel is especially suited for building integrations with and migrations from legacy systems that can be very unwieldy. Migrating from a legacy system can entail copious amounts of reading and writing files for instance, as data needs to be moved from the old to the new system. With its easy to write DSL, you can easily do a lot of heavy lifting using Camel.

Though full of features, Apache Camel is easy to use and get started with. As a Java library, it can be used standalone or integrated with the major development platforms like [Jakarta EE](https://jakarta.ee/) and Spring. In this blog post, we take a quick look at getting started with Apache Camel on the Jakarta EE Platform.

Apache Camel is broken up into components. A component in Camel is a self contained function that does a single thing. For instance the file component reads and writes files. The JDBC component enables you to access databases through JDBC and so on. There are 300+ components available for almost any imaginable task. In the unlikely event none of those meets your needs, you can easily write one.

To keep the download as small as possible, the project ships with a "core" that contains a subset of the available components. You can always add components as their need arises in your projects. To get started, first add the dependencyManagement block to your pom.xml file.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;dependencyManagement&gt;
 &lt;dependencies&gt;
   &lt;!-- Camel BOM --&gt;
   &lt;dependency&gt;
      &lt;groupId&gt;org.apache.camel&lt;/groupId&gt;
      &lt;artifactId&gt;camel-bom&lt;/artifactId&gt;
      &lt;version&gt;3.20.5&lt;/version&gt;
      &lt;scope&gt;import&lt;/scope&gt;
      &lt;type&gt;pom&lt;/type&gt;
   &lt;/dependency&gt;
 &lt;/dependencies&gt;
&lt;/dependencyManagement&gt;

&lt;dependency&gt;
&lt;groupId&gt;org.apache.camel&lt;/groupId&gt;
&lt;artifactId&gt;camel-core&lt;/artifactId&gt;
&lt;/dependency&gt;

&lt;dependency&gt;
&lt;groupId&gt;org.apache.camel&lt;/groupId&gt;
&lt;artifactId&gt;camel-endpointdsl&lt;/artifactId&gt;
&lt;/dependency&gt;

&lt;dependency&gt;
&lt;groupId&gt;org.apache.camel&lt;/groupId&gt;
&lt;artifactId&gt;camel-cdi&lt;/artifactId&gt;
&lt;/dependency&gt;

&lt;dependency&gt;
&lt;groupId&gt;org.apache.camel&lt;/groupId&gt;
&lt;artifactId&gt;camel-cdi-main&lt;/artifactId&gt;
&lt;/dependency&gt;</pre>

The camel-core dependency is the core camel project. The camel-endpointdsl gives you a nice, typesafe DSL for crafting your Camel routes. Then camel-cdi is a CDI integration (which doesn't fully work at the moment however).

With those in place, let's take a look at a first, "hello world" example . This example reads files and moves files from one folder into the other. Though reading and writing files on the Java Platform has improved significantly over the last few years, using the file component of Apache Camel makes it even better. The following FileRoute.java class shows a simple read and move file operation.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class FileRoute extends EndpointRouteBuilder {

    @Override
    public void configure() throws Exception {
        from(file("/data/inbox/"))
                .log("Received file ${file:name}")
                .process(exchange -&gt; {
                    Message in = exchange.getIn();
                    String body = in.getBody(String.class);
                    //Do something to the read file
                })
            .to("file:/data/output");

    }

}</pre>

The above class is a Camel Route that reads a file, does some processing on the read content, and then sends it to another place. This is a summary of how Camel works. This route processes a org.apache.camel.Message, wrapped in a org.apache.camel.Exchange. A lot of your Camel use will revolve around taking messages from one place, optionally doing something with the message, and then transferring the result to another place.

Camel routes are designed to be read. So in the FileRoute above, you can read it as read as get a message from the file in the /data/inbox folder, process that message, then route the file to the /data/outbox folder. In between we also logged the name of the file. With just these few lines of code, we have a file watcher that will watch the folder/data/inbox and any new files will go through the same process.

With your route in place, you will need to tell Camel about it. In the absence of the full functionality of the camel-cdi bridge, we can easily register our route as shown below.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Singleton
@Startup
public class CamelBootstrap {

  @Inject
  CdiCamelContext camelContext;

  @Inject
  FileRoute fileRoute;

  @PostConstruct
  void init() {
    try {
     camelContext.addRoutes(fileRoute);
    } catch (Exception e) {
       throw new RuntimeException(e);
    }
    camelContext.start();
  }

  @PreDestroy
  void shutdown() {
    if (camelContext != null) {
      camelContext.stop();
    }
  }

}</pre>

CamelBootstrap is a singleton EJB that starts on application boot. Within it, we inject a org.apache.camel.cdi.CdiCamelContext instance, on which we call the addRoutes method in a post construct listener of the class. We then start the context. The class also listens for the pre destroy event and within it, stops the context. With this class in place, we have a fully functional Apache Camel application.

The addRoutes method takes a org.apache.camel.RoutesBuilder. In our above example, we injected our FileRoute class, and passed that CDI managed instance to the addRoutes method. You will also note that we didn't explicitly give a scope to the FileRoute.java bean. As it, it has the @Dependent pseudo-scope. This is well and good and should work fine, as we have no special reason to give our routes explicit scopes.

Now you may be wondering, OK what is a CamelContext and why do you need it? Think of a CamelContext instance as a grouping of a given collection of routes (remember our FileRoute.java?) and the policies applicable to them during a given run. Your Camel routes will be associated with contexts to which they belong. In the above example, calling the stop() method on the CamelContext instance all routes/components/endpoints etc. associated with the context and clears internal state/cache.

As you can see, we injected the CdiCamelContext. This is provided by the [camel-cdi](https://github.com/astefanutti/camel-cdi) context, which is currently not fully functional in Jakarta EE 10. I will be working on updating it to CDI 4.0 and Jakarta EE 10. But of course all help welcome to get it done faster. Once fully functional, all your routes would automatically be registered to the CdiCamelContext, and also have a bridge between CDI features like events and Camel. However, the current state is still more than enough to get you started with playing around with Apache Camel on the Jakarta EE Platform if you are new to it.

As you have seen in this brief blog post, Apache Camel is a Java library that allows for effortless integration of disparate systems using uniformed, readable and maintainable constructs. We will be posting some more Camel content for use on the Jakarta EE Platform, so stay tuned.

Looking for more Jakarta EE development resources? Explore our latest guides:

* [Keeping Count in Jakarta EE Applications with MicroProfile Metrics](https://www.payara.fish/resource/keeping-count-in-Jakarta-EE-applications-with-MicroProfile-Metrics/)
* [A Developer Guide To Jakarta EE NoSQL Development With MongoDB And Morphia](https://www.payara.fish/resource/A-Developer-Guide-to-MongoDB-With-Morphia-And-Jakarta-EE/)
* [The Complete Guide To JSON Processing On the Jakarta EE Platform](https://www.payara.fish/resource/The-Complete-Guide-To-JSON-Processing-On-The-Jakarta-EE-Platform/)
