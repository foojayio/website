---
title: "JEP 411: Java's Security Model and the Principle of Least Privilege"
date: "2021-06-03T07:47:51+00:00"
lastmod: "2021-06-03T08:02:30+00:00"
description: "Java, like most platforms or languages has layers of security, this article lookS at the Authorization layer and how JEP 411 relates to it."
authors:
  - "peter-firmstone"
image: "https://upload.wikimedia.org/wikipedia/commons/0/07/Swiss_cheese_model.svg"
categories:
  - "JEPs"
  - "Security"
tags:
related_posts:
frozen: false
---

![](https://upload.wikimedia.org/wikipedia/commons/0/07/Swiss_cheese_model.svg)

Java, like most platforms or languages has layers of security, this article intends to look at Java's Authorization layer, which is unlike in other languages, and to distinguish between two different ways this layer is typically utilized, why one is effective while the other isn't and investigate why JEP 411 only considers the least effective method and hopefully increase awareness of the Principle of Least Privilege as it's applied to Java Authorization, improve adoption, encourage people to take advantage of the improved security it provides, as well as prolong its support and possibly even improve it in future.

## Each security layer is intended to prevent an attacker gaining access to information or obtaining control of the JVM

Authorization compliments and enforces other access controls like visibility of packages, classes and methods, modules have restricted reflection access in more recent versions of Java.

1. Authentication
2. Encryption
3. Data input validation and filtering (Serialization filters and XML FSP)
4. Access Controls  
   4.1 Authorization  
   4.3 Package and Module visibility  
   4.4 Class and Method visibility
5. Type Safety
6. Atomic failure of objects during construction (Java Serialization breaks atomic failure, objects can be created in a state that doesn't satisfy an objects invariants.)

## JEP 411 Focuses on Item 4.1, Authorization.

Currently Java's authorization API's are part of the Java virtual machine specification, are cross platform and allow the application of the principles of least privilege to those who wish to apply it at a fine grained level within Java.

### What authorization controls does Java provide today?

#### Some of the Permissions the Java platform uses for Authorization:

1. FilePermission
2. SocketPermission
3. URLPermission
4. NetPermission
5. SecurityPermission
6. AuthPermission
7. PropertyPermission
8. MbeanPermission
9. ReflectPermission
10. PrivateCredentialPermission
11. SerializablePermission
12. RuntimePermission
13. LoggingPermission
14. AttachPermission

Software developers can also create their own permissions.

<https://docs.oracle.com/en/java/javase/11/security/permissions-jdk1.html#GUID-75C71299-8B56-4AC9-A83F-41BC14535545>

#### Uniquely, Java doesn't only check user authorization, but also authorization of code.

1. It authorizes only the privileges (permissions) required and no more. Should other security layers fail, it is the last line of defense. Nothing that is unauthorized by policy is allowed to occur.
2. Tooling can generate policy files, by simply running a program through it's intended functionality.
3. Simplest method of auditing authorization, to see what permissions code requires and condenses it down to around 1,000 line items for a codebase with 250,000 lines of code.
4. How many library dependencies do you have? What permissions do they require? POLP can be used to implement dependency authorization controls as well.
5. The administrator, development or deployment teams only need worry about what they are allowing, they don't need to worry about things they don't authorize, or are unaware of like an Agent attaching from another process, that might be hidden in a Trojan downloaded by a user.
6. It defends against low probably high consequence attacks.

## What the Principle of Least Privilege is and is not

The principle of least privilege is not a broad brush application of privileges granted to an application, by OS process. While this may be the principle of least privilege from the OS's perspective, it is not from the Java Virtual Machine's perspective, which is sufficiently powerful and complex that it is comparable in complexity to an Operating System.

https://youtu.be/2rCgA3IbTg4

## When other security layers fail, what are some things Java's authorization security layer is designed to prevent?

1. It prevents access to system properties, such as the keystore and truststore locations and passwords.
2. It prevents unauthorized access to networks.
3. It prevents unauthorized reading and modification of file systems.
4. It prevents reflection from bypassing other access controls.
5. It prevents Agent's from attaching to the process and modifying running bytecode using the Attach API. <https://docs.oracle.com/javase/8/docs/technotes/guides/attach/>

## Criticisms of the existing authorization layer

1. OpenJDK developers claim it's excessively costly to maintain.
2. OpenJDK developers don't believe it is functional, or is too difficult to understand.
3. OpenJDK developers believe very few use it and that alternatives exist for many but not all cases.
4. OpenJDK developers think it's for applets and untrusted code.
5. OpenJDK developers think the stack walk is the source of performance problems (It isn't).
6. OpenJDK developers claim removing SecurityManager will improve security (for people who use it incorrectly).
7. Many complain (including myself) the policy files are extremely difficult to construct by hand.

## Tooling exists that address all existing criticisms except for 01. development cost.

1. Tools that generate the policy file based on the principle of least privilege are freely available.
2. The Principle of Least Privilege (POLP) combined with tooling significantly reduces complexity, improves auditability and significantly increases the effectiveness of the Authorization layer to constrain an application to its intended function, significantly restricting the attack surface of applications and their dependencies (it doesn't reduce the attack surface of the JVM as this typically runs with AllPermission).
3. High performance and high scaling Policy provider and SecurityManager implementations are freely available.
4. Security isn't free, it does have a development cost.

Java's new security model is based on the Castle and moat approach, which focuses on defending the perimeter.

1. Authorization will be defined at the OS process level.
2. Authorization will be defined statically ahead of time, it no longer has the capacity to be dynamic (eg OSGi or Apache River).
3. A change in authorization, such as dynamically loading some classes will require spawning a new process, even when these classes are trusted. This can have a significant overhead on existing applications that use ClassLoader's to partition applications.
4. All code within the Castle is trusted.
5. Java Flight Recorder is the security camera (external to Java platform API's) to tell you when the enemy has breached the perimeter defenses.
6. There are no internal defenses, once the perimeter has been breached, if your Java process has access to keystores and certificate identities, so will the attacker.
7. JPMS will be relied upon to control reflective access and tighten visibility.
8. StackWalker will replace similar functionality provided by SecurityManager.

## Criticisms of the as yet to be developed new Authorization Layer

1. Java developers must figure out themselves what is to be monitored with JFR. JFR cannot prevent access, only monitor it.
2. Special command line flags are required to disable Agents, however with the previous approach, this could have been protected by an authenticating administrator using an encrypted connection, and run as the Subject of that administrator, in the same process that the user has, but where only the administrator has the permission to use the Agent. This would have prevented a user from downloading a Trojan, that contained an Agent, in future Java versions the feature must be disabled, or OS level controls used. The application vendor has little control.
3. Administrators will need to learn all the special command line flags for different vendors to secure their JVM's.
4. There is no longer a common platform independent or even a version independent API.
5. StackWalker's do not work in native-compiled code like GraalVM.
6. The new API's aren't proven.
7. New API is not backward compatible, it breaks JAAS, after the removal of AccessController and AccessControlContext.
8. ISO/IEC 9075-13:2016 Database languages - SQL - Part 13: SQL Routines and types using the Java TM programming language (SQL/JRT) will require changing. How to do so with compatibility for Java 8? <https://webstore.ansi.org/Standards/ISO/ISOIEC9075132016>
9. Some existing programs will not run (this known issue has now been addressed in JEP 411), due the the requirement of setting a property from the command line to enable SecurityManager, <https://issues.apache.org/jira/browse/NETBEANS-5689>
10. The new API's don't apply the principle of least privilege. OpenJDK developers claim that applying permissions to the process is the principle of least privilege. This claim contradicts "Inside Java 2 Platform Security, Second Edition" by Li Gong, Gary Ellison and Mary Dageforde. Which states: "In Java 2, we can be much more flexible and give only the minimum set of permissions necessary to accomplish the task. This is another example of how Java 2 subscribes to the principle of least privilege."
11. OpenJDK developers have been researching this for months, but doing so without consideration of POLP, policy files riddled with AllPermission provide inadequate defenses and their assumptions are based on that. This situation has occurred simply due to the lack of tooling and documentation to support administrators and developers to apply POLP, which also just happens to be the simplest method and best practice of constructing policy files.
12. Without fine grained access control (the principle of least privilege applied) should an attacker breach the castle walls, it will be possible to use JDP, JMX and JFR to take control of other JVM's on the local network if JDP is enabled, even with TLS and authentication as the attacker will have access to the keystore, trustore, passwords and be able to impersonate users or worse, administrators on the network.
13. The number of private company, government and military existing deployed applications that depend on SecurityManager which will be affected are unknown.
14. The maintenance burden will be shifted from OpenJDK developers to downstream applications.
15. The way OpenJDK is handling JEP 411 is already leading to issues, Spotbugs is considering removing static analysis related to Security API's, when replacement API's are yet to be developed and deployments on Java 8 are still greater than 50%, this will definitely harm Java security, rather than move it forward. <https://github.com/spotbugs/spotbugs/issues/1515>

## The original architects of Java 2 platform security intended the principle of least privilege to be applied.

{{< youtube Bgo7dcCbqV8 >}}

Security Policy Tool and implementation practices didn't fit the original designers intent.

### The only tool bundled with Java promoted manually edited policy files; hand editing isn't practical without first programmatically generating policy files.

1. <https://apim.docs.wso2.com/en/3.1.0/install-and-setup/setup/security/enabling-java-security-manager/>
2. <https://www.ibm.com/docs/en/cics-ts/5.2?topic=applications-enabling-java-security-manager>
3. <https://www.baeldung.com/java-security-overview>
4. <https://docs.osgi.org/specification/osgi.core/7.0.0/framework.security.html>
5. <https://docs.appdynamics.com/21.5/en/application-monitoring/install-app-server-agents/java-agent/install-the-java-agent/agent-installation-by-java-framework/java-security-manager-configuration#JavaSecurityManagerConfiguration-RuninaJVMwithSecurityEnabled>
6. <https://tomcat.apache.org/tomcat-8.5-doc/security-manager-howto.html>
7. [Gigaspaces example of POLP policy](https://docs.gigaspaces.com/latest/security/java-security-policy-file.html)
8. <https://www.oracle.com/technetwork/database/application-development/jdbc/con1615-3961043.pdf>
9. <https://blogs.oracle.com/dev2dev/configuring-the-oracle-jdbc-security-policy-file>
10. <https://tada.github.io/pljava/use/policy.html>
11. <https://db.apache.org/derby/docs/10.15/security/csecjavasecurity.html#csecjavasecurity>
12. <https://www.ibm.com/docs/en/was-nd/9.0.5?topic=security-javapolicy-file-permissions>

## Developers have created tools to assist the implementation the Principle of Least Privilege in policy files themselves

1. <https://github.com/floyd-fuh/TMSJSPGE>
2. <https://github.com/SimonHGR/JVMprotector>
3. <https://github.com/pro-grade/pro-grade>
4. <https://github.com/pfirmstone/JGDMS/blob/trunk/JGDMS/tools/security-policy-debug/src/main/java/org/apache/river/tool/SecurityPolicyWriter.java>

{{< youtube RXziP10wvn8 >}}

## What do POLP generated policy files look like?

1. <https://www.dropbox.com/s/nq0n1vyb8qfeidn/defaultnonactvm-policy.txt?dl=0>
2. <https://www.dropbox.com/s/qcgl0q1ywtisj0y/defaultsecuretest-policy-new.txt?dl=0>
3. <https://www.dropbox.com/s/a926ackyzdksoha/defaultsecuretest-policy.txt?dl=0>

Typically POLP policy files are generated with tooling while running an application through it's expected functionality, logged in as each particular role. Following policy file generation, the policy files are visually inspected / audited, then the roles are checked to verify users cannot perform any tasks outside those allowed. It's beneficial to automate most of this process if possible.

[Inferring Java Security Policies through Dynamic Sandboxing Inoue \& Forest](https://www.cs.unm.edu/~forrest/publications/inoue-plc-05.pdf)

Authorization layer infrastructure needs to be POLP to be effective, the argument that using it incorrectly adds little value for high expense is true, however this is a result of promotion of fallacies, like trusted code should be given AllPermission, this doesn't comply with POLP, so now that we've been flushed out by JEP 411, allow Java developers like us demonstrate the benefits of POLP as the original architects intended and move Java security forward.

All existing Java applications are compatible with and can be hardened by using POLP in Java's existing authorization layer.

### Two simple tools available on Maven Central to remove the burden of discovering the permissions required and significantly improve performance and scalability.

First add the following dependency to your project:

```xml
<!-- https://mvnrepository.com/artifact/au.net.zeus.jgdms.tools/security-policy-debug -->
<dependency>
    <groupId>au.net.zeus.jgdms.tools</groupId>
    <artifactId>security-policy-debug</artifactId>
    <version>3.1.0</version>
</dependency>
```

Then add the following property when running your program:

```
-Djava.security.manager=org.apache.river.tool.SecurityPolicyWriter
```

Whatever the name and location of your current policy file is, the tool will create one with the same name, with the word ".new" appended.

Run your application and log in as the user role you want to make the policy for, then only the permissions required for you to run your program as that user, performing the tasks you just performed are added to the policy file. You might also do this with a test suite that performs the necessary tasks, then remove the grant statements in the generated policy file for test code.

You do need to read the policy file, following generation, to identify any permissions that library dependencies have been granted, that you don't want those libraries to have and remove them. Such as network connections, if they were unnecessary, for example, if it's reporting heuristics to an unrecognized network address, if you remove that specific SocketPermission that grants it access, it can no longer open a network connection to that address.

Now move your old policy file out of the way, and rename the new policy file.

Run your program again this time with:

```
-Djava.security.manager=java.lang.SecurityManager,\
```

Confirm that the user cannot do anything they are not authorized to do.

Login as a different user and try to do the same tasks, you will find the program throws a SecurityException.

Now if you're experiencing performance issues, because you have developed high scaling concurrent code, but Java's built in policy provider is limiting performance, you need to also add the following dependency to your program:

```xml
<!-- https://mvnrepository.com/artifact/au.net.zeus.jgdms/jgdms-platform -->
<dependency>
    <groupId>au.net.zeus.jgdms</groupId>
    <artifactId>jgdms-platform</artifactId>
    <version>3.1.0</version>
</dependency>
```

Then set the following property:

```
-Dpolicy.provider=org.apache.river.api.security.ConcurrentPolicyFile,\
```

This replaces the built-in horribly slow Java policy provider with a modern performant and highly scalable implementation, with identical functionality.

If you want to go even faster still because your program makes a lot of repeated permission checks, then we have a SecurityManager that executes permission checks concurrently and weakly cache AccessControlContext permission checks, so that none are repeated, until the policy is refreshed. This is the fastest security architecture possible with POLP, I guarantee, there is nothing faster that exists on Earth. Try it and see for yourself.

```
-Djava.security.manager=org.apache.river.api.security.CombinerSecurityManager,\
```

## Is Java positioned as the best platform to comply with new regulations?

Is OpenJDK about to remove POLP Authorization, at a time when POLP Authorization is in demand? Is POLP support Java's best kept secret?

It is my personal opinion that JEP 411 should be rejected and any further modifications are postponed to new JEP's, until the existing security architecture and it's use cases are fully understood, it will take much longer than has been allowed for many of these use cases to emerge from companies, government and military, who deploy the principle of least privilege on Java platforms, from behind closed doors. OpenJDK might consider issuing a public RFI regarding SecurityManager and keep it open for at least 12 months, as these organizations are likely to be slow to respond. Secure deployments often remain obscured from public view, the reason you don't hear about POLP deployments is they are generally not the victims of breaches and don't make the evening news. However raising JEP 411 has highlighted the cost of maintaining SecurityManager, that more people need to utilize and learn how to use POLP for continued investment in Java Authorization. The practice of assigning AllPermission to trusted code is detrimental to security, this practice is what needs to be deprecated, to be replaced by POLP policy file generation, so that Java limits the ability of programs to do anything other than their intended functionality.

It would seem be more appropriate to create a JEP to address the application of the Principle of Least Privilege to Java Authorization and remove all documentation that advise granting AllPermission to trusted code, and furthermore, to provide a warning when SecurityManager has not been set, or to warn if policy files contain AllPermission, perhaps even deprecating the use of AllPermission in policy files. A tool to generate policy files should also be part of this JEP. This would significantly improve the security of current investments in Java technology. This JEP might also look at revising AccessController, AccessControlContext, how context is preserved across threads and used in Excecutor tasks, investigate how security can be simplified, without degrading it and maintenance burdens reduced for OpenJDK developers.

Not only should it be a goal to move Java forward, but security as well.

While changes in recent years have improved security, such as module access control to limit reflection, input validation of XML and Serialization Filters have improved security, OpenJDK would be taking a significant step backward for security if it continues with JEP 411, this feels like one step forward, two steps back.

In the case that OpenJDK approves JEP 411, we still have 10 years to promote POLP, and should it gain significant adoption, it would create a compelling argument for ongoing support and improvements. There are certainly significant security benefits in doing so. Don't believe misinformation that says otherwise, this is based on outdated assumptions about SecurityManager, investigate the evidence yourself.

The opinions stated on this post are the opinions of the author, I personally won't be using any Java versions with a degraded SecurityManager, I won't be upgrading beyond Java 17 LTS unless SecurityManager is fully functional in a later LTS release, or until replacement security API's are developed and are proven, that at least match or better SecurityManager API's, even Nicolas admits in the video below JEP 411 "the SecurityManager if used correctly makes your app more secure".

The Principle of Least Privilege is only just catching on and it is the only way to use SecurityManager correctly, having applied it for 4 years, since developing a tool to calculate the required permissions and create policy files, I can say with confidence that using the tools linked above, there are no shortcomings (those that do apply to Java's implementation); there are no new features being developed in Java now that would convince me otherwise. If you are using Java 8 to 17, you should seriously consider using tools that enhance the authorization layer and apply the principles of least privilege to your code, it is simply security best practice.

Hopefully, OpenJDK will be prepared to work with developers who are implementing POLP Authorisation layers today, and provide ways for us to do so in a somewhat compatible manner. This is possible without the SecurityManager class itself, if OpenJDK can create a new provider interface. Perhaps with SecurityManager gone, The Permission super class interface method Guard::check(Object) would be an appropriate place to put such a provider as default method, all existing Permission checks would need to call this instead. A proposed name for this provider interface is Authority::has(Permission). This avoids the need to call a System method and places the provider directly in Guard and Permission. This would have the benefit of reducing OpenJDK's maintenance burden, while allowing those who require a POLP Authorisation layer to continue to do so on later versions of Java.

AccessController, AccessControlContext and the Policy provider interface are still necessary for the POLP Authorisation Layer as well as JAAS compatibility. There are better ways to implement Policy however, so Java's inbuilt PolicyFile implementation should be removed, as it's the source of many performance issues. This removes the burden of security maintenance from OpenJDK and places it onto third party implementations, while also encouraging third parties to participate in OpenJDK to maintain permission checks and preserve context where appropriate for their implementations. The hooks are already there in the form of Permission checks, we just need to be allowed to maintain them within the OpenJDK codebase.

<https://www.trustwave.com/en-us/resources/blogs/trustwave-blog/misconfigurations-a-hackers-path-of-least-resistance/>

<figure class="wp-block-embed is-type-wp-embed is-provider-sonrai-security wp-block-embed-sonrai-security">
 <div class="wp-block-embed__wrapper">
  https://sonraisecurity.com/blog/healthcare-hipaa-principle-of-least-privilege/
 </div>
</figure>

{{< youtube HLrptRxncGg >}}
