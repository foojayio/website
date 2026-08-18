---
title: "Debugging Using JMX Revisited"
slug: "debugging-using-jmx-revisited"
date: "2024-04-18T13:02:35+00:00"
lastmod: "2024-04-18T13:23:17+00:00"
description: "Learn how to leverage JMX and Spring Boot for advanced debugging and management, enabling efficient monitoring and control of Java applications."
canonical: "https://debugagent.com/debugging-using-jmx-revisited"
authors:
  - "shai-almog"
image: "DALL-E-2024-02-27-06.55.05-Create-a-simplified-digital-illustration-suitable-for-a-blog-post-header-representing-advanced-debugging-and-application-management.-The-image-should.jpeg"
categories:
  - "Tutorials"
tags:
related_posts:
  - "dtrace-revisited-advanced-debugging-techniques"
  - "building-gdocweb-with-java-21-spring-boot-3-x-and-beyond"
  - "the-theory-of-debugging"
enlighterjs: true
frozen: false
---

* [The Need for Advanced Management Tools in Development](#the-need-for-advanced-management-tools-in-development)
  * [Introduction to JMX (Java Management Extensions)](#introduction-to-jmx-java-management-extensions)
    * [**Understanding MBeans**](#understanding-mbeans)
  * [Spring and Management Beans](#spring-and-management-beans)
* [Tooling for JMX Management](#tooling-for-jmx-management)
  * [Getting Started with JMXTerm](#getting-started-with-jmxterm)
* [Leveraging JMX in Debugging and Management](#leveraging-jmx-in-debugging-and-management)
* [Exposing MBeans in Spring Boot](#exposing-mbeans-in-spring-boot)
  * [Understanding Spring Boot JMX Support](#understanding-spring-boot-jmx-support)
  * [Expose an MBean in Spring Boot](#expose-an-mbean-in-spring-boot)
  * [Example: Exposing a Simple Configuration MBean](#example-exposing-a-simple-configuration-mbean)
* [Final Word](#final-word)

Debugging effectively requires a nuanced approach, similar to using tongs that tightly grip the problem from both sides.

While low-level tools have their place in system-level service debugging, today's focus shifts towards a more sophisticated segment of the development stack: advanced management tools.

Understanding these tools is crucial for developers, as it bridges the gap between code creation and operational deployment, enhancing both efficiency and effectiveness in managing applications across extensive infrastructures.

{{< youtube rQjHAMM3XfY >}}

<br />

As a side note, if you like the content of this and the other posts in this series check out my [Debugging book](https://www.amazon.com/dp/1484290410/) that covers **t** his subject. If you have friends that are learning to code I'd appreciate a reference to my [Java Basics book.](https://www.amazon.com/Java-Basics-Practical-Introduction-Full-Stack-ebook/dp/B0CCPGZ8W1/) If you want to get back to Java after a while check out my [Java 8 to 21 book](https://www.amazon.com/Java-21-Explore-cutting-edge-features/dp/9355513925/)**.**

The Need for Advanced Management Tools in Development
-----------------------------------------------------

Development and DevOps teams utilize an array of tools, often perceived as complex or alien by developers. These tools, designed for scalability, enable the management of thousands of servers simultaneously.

Such capabilities, although not always necessary for smaller scales, offer significant advantages in application management. Advanced management tools facilitate the navigation and control over multiple machines, making them indispensable for developers seeking to optimize application performance and reliability.

![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/5ca4duiw3l3hqipsolap.png)

### Introduction to JMX (Java Management Extensions)

One of the pivotal standards in application management is Java Management Extensions (JMX), which Java introduced to simplify the interaction with and management of applications.

JMX allows both applications and the Java Development Kit (JDK) itself to expose critical information and functionalities, enabling external tools to manipulate these elements dynamically.

Although activating JMX falls outside this discussion, its significance cannot be overstated, with ample resources available for those interested in its implementation.

**Setting up JMX**

JMX isn't enabled by default, to enable it we need the following steps:

1. **Modify the JVM Startup Parameters** : To enable JMX on a Java application, you must adjust the Java Virtual Machine (JVM) startup parameters. This involves adding specific flags to your application's startup command. The essential flags for enabling JMX are:
   * `-``Dcom.sun.management.jmxremote`: This flag activates the JMX remote management and monitoring.
   * `-``Dcom.sun.management.jmxremote.port=`: Replace \`\` with a specific port number where the JMX remote connection will listen.
   * `-``Dcom.sun.management.jmxremote.ssl=false`: This flag disables SSL for JMX connections. For development environments, SSL might be disabled for simplicity, but for production environments, consider enabling SSL for security.
   * `-``Dcom.sun.management.jmxremote.authenticate=false`: This flag disables authentication. Similar to SSL, authentication may be disabled in development but should be enabled in production to ensure secure access.
2. **Restart Your Application**: With the JVM parameters set, restart your application. This will apply the new startup parameters, activating JMX.
3. **Verify JMX Connectivity**: After restarting your application, you can verify that JMX is enabled by connecting to it using a JMX client such as JConsole, VisualVM, or a custom management application. Use the port number specified in the startup parameters to establish the connection.

**JMX Security Considerations**

While enabling JMX provides powerful management capabilities, it's crucial to consider security implications, especially when JMX is exposed over a network.

When deploying applications in production, always enable SSL and authentication to protect against unauthorized access. Additionally, consider firewall rules and network policies to restrict JMX access to trusted clients.

#### **Understanding MBeans**

Central to JMX are Management Beans (MBeans), which serve as the control points within an application. These beans enable developers to publish specific functionalities for runtime monitoring and configuration.

The ability to export application metrics to dashboards through MBeans is particularly valuable, facilitating real-time decision-making based on accurate, up-to-date information. Furthermore, operations such as user management can be exposed through MBeans, enhancing administrative capabilities.

### Spring and Management Beans

Spring Framework's Actuator module exemplifies the integration of management capabilities within development, offering extensive metrics and operational details.

This integration propels applications to "production-ready" status, allowing developers to monitor and manage applications with unprecedented depth and efficiency.

Tooling for JMX Management
--------------------------

While JMX can be accessed through various web interfaces and administrative tools, command-line tooling offers a direct, efficient method for interacting with JMX-enabled applications on production servers.

Tools like JMXTerm complement visual tools by providing a streamlined interface for rapid insights, especially in environments unfamiliar to the developer.

### Getting Started with JMXTerm

JMXTerm is a powerful utility for managing JMX without the need for graphical visualization, ideal for quick diagnostics or high-level server insights. After enabling JMX on the JVM and setting up the necessary configurations, developers can connect to servers, explore different JMX domains, and manipulate MBeans directly from the command line.

We can accomplish all of the following via visual tools and sometimes using a web interface. Normally, that's the approach I use. However, as a learning tool I think JMXTerm is fantastic since it exposes things in a way that's consistent and verbose. If we can understand JMXTerm the GUI version would be a walk in the park...

We can launch JMXTerm using the command line, in my case I used the following command:

```bash
java -jar ~/Downloads/jmxterm-1.0.2-uber.jar --url localhost:30002
```

Once the connection is made we can issue commands to JMX and retrieve information about the JVM or the application e.g. I can list the domains which you can think of as similar to "packages" or "modules" a way to organize the various beans:

```
>domains #following domains are available JMImplementation com.sun.management java.lang java.nio java.util.logging javax.cache jdk.management.jfr
```


I can select a specific domain and thus perform future operations within said domain:

```
>domain java.util.logging #domain is set to java.util.logging
```


Once inside the domain I can select a specific bean and perform operations on it. For this I need to first list the beans in the domain, in this case there's only the logging bean. I can then select that bean using the `bean` command:

```
>beans #domain = java.util.logging: java.util.logging:type=Logging
```


```
>bean java.util.logging:type=Logging #bean is set to java.util.logging:type=Logging
```


I can perform many operations on beans, perhaps the most useful is the `info` command which lets me query a bean.

Notice that a bean can have attributes, think of them like object fields and operations which you can think of as methods. There are also notifications which you can think of as events:

```
>info #mbean = java.util.logging:type=Logging #class name = sun.management.ManagementFactoryHelper$PlatformLoggingImpl # attributes %0 - LoggerNames ([Ljava.lang.String;, r) %1 - ObjectName (javax.management.ObjectName, r) # operations %0 - java.lang.String getLoggerLevel(java.lang.String p0) %1 - java.lang.String getParentLoggerName(java.lang.String p0) %2 - void setLoggerLevel(java.lang.String p0,java.lang.String p1) #there's no notifications
```


I can run operations and pass various commands e.g. I can get the logger level, set it and then check that the logger level was indeed updated:

```
>run getLoggerLevel "org.apache.tomcat.websocket.WsWebSocketContainer" #calling operation getLoggerLevel of mbean java.util.logging:type=Logging with params [org.apache.tomcat.websocket.WsWebSocketContainer] #operation returns:
```


```
>run setLoggerLevel org.apache.tomcat.websocket.WsWebSocketContainer INFO #calling operation setLoggerLevel of mbean java.util.logging:type=Logging with params [org.apache.tomcat.websocket.WsWebSocketContainer, INFO] #operation returns: null
```


```
>run getLoggerLevel "org.apache.tomcat.websocket.WsWebSocketContainer" #calling operation getLoggerLevel of mbean java.util.logging:type=Logging with params [org.apache.tomcat.websocket.WsWebSocketContainer] #operation returns: INFO
```


This is just the tip of the iceberg. We can get many things such as spring settings, internal VM information, etc. In this example I can query VM information directly from the console:

```
>domain com.sun.management #domain is set to com.sun.management
```


```
>beans #domain = com.sun.management: com.sun.management:type=DiagnosticCommand com.sun.management:type=HotSpotDiagnostic
```


```
>bean com.sun.management:type=HotSpotDiagnostic #bean is set to com.sun.management:type=HotSpotDiagnostic
```


```
>info #mbean = com.sun.management:type=HotSpotDiagnostic #class name = com.sun.management.internal.HotSpotDiagnostic # attributes %0 - DiagnosticOptions ([Ljavax.management.openmbean.CompositeData;, r) %1 - ObjectName (javax.management.ObjectName, r) # operations %0 - void dumpHeap(java.lang.String p0,boolean p1) %1 - javax.management.openmbean.CompositeData getVMOption(java.lang.String p0) %2 - void setVMOption(java.lang.String p0,java.lang.String p1) #there's no notifications
```


Leveraging JMX in Debugging and Management
------------------------------------------

JMX stands out as a robust tool for wiring management consoles, allowing developers to expose critical settings and metrics for their projects.

Beyond its conventional use, JMX can be leveraged as part of the debugging process, serving as a pseudo-interface for triggering debugging scenarios or observing debugging sessions within the management UI.

This approach not only simplifies the management of server applications but also enhances the developer's ability to diagnose and resolve issues efficiently.

Exposing MBeans in Spring Boot
------------------------------

Up until now we discussed the process of working with beans that are a part of the JVM or Spring. But what about our own application logic?

We can expose our own applications internal state so we (and our SREs) can review these in production and staging. Instead of building a custom control panel or logging **everything**, we can just expose the data. If a flag is problematic we can change it in production, if you want to query a specific state it too can be exposed.

Spring Boot simplifies the management and monitoring of applications through its comprehensive support for JMX. By leveraging Spring's infrastructure, we can easily expose their application's beans as JMX Managed Beans (MBeans), making them accessible for monitoring and management via JMX clients.

### Understanding Spring Boot JMX Support

Spring Boot automatically configures JMX for you and exposes any beans annotated with `@ManagedResource` as JMX MBeans. This feature, combined with Spring Boot's Actuator, provides a rich set of management endpoints, covering various aspects of the application, from metrics to thread dumps.

### Expose an MBean in Spring Boot

To expose a bean we need to take the following steps:

1. **Define a Management Interface** : Create an interface that defines the operations and attributes you wish to expose via JMX. This interface should be annotated with JMX annotations such as `@ManagedOperation` for methods and `@ManagedAttribute` for fields or getter/setter methods.
2. **Implement the MBean**: Implement the interface in a class that performs the actual logic for the operations and attributes defined. This class represents your MBean and can be a regular Spring-managed bean.
3. **Annotate the Bean with** `@ManagedResource`: Annotate your MBean implementation class with `@ManagedResource` to indicate that it should be exposed as an MBean. You can specify the object name for the MBean in this annotation, which is how it will be identified in JMX clients.
4. **Enable JMX in Spring Boot** : Ensure that JMX is enabled in your Spring Boot application. This is usually the default behavior, but you can explicitly enable it by setting `spring.jmx.enabled=true` in your `application.properties` or `application.yml` file.
5. **Access the MBean via a JMX Client**: Once your application is running, you can access the exposed MBean through any standard JMX client, such as JConsole, VisualVM, or a custom client. Connect to the Spring Boot application's JMX domain, and you'll find the MBean you exposed, ready for interaction.

### Example: Exposing a Simple Configuration MBean

```
// Define a management interface
public interface ConfigurationMBean {
    @ManagedAttribute
    String getApplicationName();

    @ManagedOperation
    void updateApplicationName(String name);
}

// Implement the MBean
@Component
@ManagedResource(objectName = "com.example:type=Configuration")
public class Configuration implements ConfigurationMBean {
    private String applicationName = "MyApp";

    @Override
    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public void updateApplicationName(String name) {
        this.applicationName = name;
    }
}
```


In this example, the `Configuration` class is annotated with `@ManagedResource`, making it available as an MBean with operations and attributes accessible via JMX clients.

Exposing MBeans in Spring Boot is a powerful feature that enhances the management and monitoring capabilities of applications.

By following the steps outlined above, developers can provide external tools with dynamic access to application internals, offering a window into the runtime behavior and allowing for adjustments on the fly.

This not only aids in debugging and performance tuning but also aligns with best practices for building manageable, robust applications.

Final Word
----------

Advanced management tools, particularly JMX and its integration with frameworks like Spring, offer developers powerful capabilities for application monitoring, configuration, and debugging.

By understanding and utilizing these tools, developers can achieve a deeper level of control over their applications, enhancing both performance and reliability.

Whether through graphical interfaces or command-line utilities like JMXTerm, the dynamic manipulation and monitoring of applications in runtime environments open new avenues for effective software development and management.

As the bridge between development and operations continues to narrow, mastering these advanced tools becomes essential for any developer looking to excel in today's fast-paced technological landscape.
