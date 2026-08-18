---
title: "Why JEP 411 Will Have a Negative Impact on Java Security"
date: "2021-05-22T07:43:06+00:00"
lastmod: "2021-05-24T17:14:06+00:00"
description: "SecurityManager, foundation to build secure software, on its own is insufficient to limit users & software to the least privilege principle."
authors:
  - "peter-firmstone"
image: "Favicon-3-2.png"
categories:
  - "JEPs"
  - "Security"
tags:
related_posts:
frozen: false
---

*[JEP 411](https://openjdk.java.net/jeps/411) specifies the "deprecation of the Security Manager for removal in a future release. The Security Manager dates from Java 1.0. It has not been the primary means of securing client-side Java code for many years, and it has rarely been used to secure server-side code. To move Java forward, we intend to deprecate the Security Manager for removal in concert with the legacy Applet API (JEP 398)." Below, Peter Firmstone describes how this relates to the principle of least privilege and what the impact of this JEP will be. Agree or disagree, add your comments, or your full articles on this topic are also welcome here on Foojay.*

### The Principle of Least Privilege

The principle of least privilege is simply that users and code must only have access to resources and information required to perform their intended tasks.

No doubt most users are familiar with using a user account for most of their day-to-day tasks, while requiring an administrator or root account to install software or make changes to the operating system.

It simply means that you only use the privileges that are necessary and no more.

The benefits are:

* Better system stability, when code is limited in the changes it can make to the system, or connections it can make. Unexpected changes are prevented from happening.
* Improved security, if you use a library that has a vulnerability, the damage it can do is limited by the privileges it has.

### Why the principle of least privilege is important for Java developers.

1. Most if not all Java programs utilize open source library dependencies.
2. Some programs will use freeware libraries or commercial libraries.
3. You don't want to give library dependencies free access to anything they want, they shouldn't have access to files on your hard drive, and they shouldn't be able to send statistics or information about you over networks without your permission.

#### How is the principle of least privilege practically applied using SecurityManager?

At first it appears simple, according to the Java documentation:

1. Enable SecurityManager, with a command line option.
2. Edit a policy file, granting Permissions to a CodeSource and user Principal.

When Java developers first attempt this, they will notice that it is difficult to determine what permissions are required by users and software. They run their programs, recording each permission required, and adding it to the policy file.

Usually most developers stop right there, determining permissions required by users and code is not as simple as it seems, to make matters worse, simple syntax errors cause problems too.

The most determined of Java developers, having persevered with their policy file, discover the performance impact that security has on their program and this is where the developer has had enough and disables the SecurityManager.

Basically, the SecurityManager and associated infrastructure are the foundations upon which to build secure software, but by themselves are insufficient for limiting users and Java software to the principles of least privilege.

#### Two simple tools available on Maven Central to remove the burden of discovering the permissions required and address the performance issue.

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

### How does running Java with a SecurityManager enforcing the principle of least privilege benefit my programs?

In Java's early days, Sun Microsystems tried to create a sandbox to lock down the Java public API, however, as Java's popularity and API grew, so did the burden of locking it down. Unfortunately, the Java sandbox was broken by Java Serialization and XML parsers. However, by limiting permissions, we are taking advantage of years of API hardening permission checks, which can be used to reduce the privileges to only those required, to mitigate the consequences of successful zero day attacks on your software.

You can also create your own permissions and check in your programs and the tool will also add them to your policy files.

The policy content may be parameterized via property expansion. Namely, expressions like *${key}* are replaced by values of corresponding system properties, to allow the same policy file (after editing and adding properties) to support the same version of java on multiple platforms. See: [ConcurrentPolicyFile](https://pfirmstone.github.io/JGDMS/jgdms-platform/apidocs/org/apache/river/api/security/ConcurrentPolicyFile.html)

Note that different versions of Java will have different content in their policy files, to perform the same tasks, so it's best to have policy files that are specific to each Java release.

If you want to improve java security even further, definitely consider disabling Java Serialization with the following property if you don't need it:

```
-Djdk.serialFilter=!*,\
```

You still need to be observant of other typical Java problems, like sanitizing user input, using TLS and client certificates then you'll have really locked down your Java application:

* <https://owasp.org/www-project-top-ten/>
* <https://owasp.org/www-project-top-ten/2017/A9_2017-Using_Components_with_Known_Vulnerabilities>
* <https://owasp.org/www-project-dependency-check/>

### JEP 411 Deprecates the Security Manager for Removal

#### What has JEP 411 got to do with the principle of least privilege?

[JEP 411: Deprecate the Security Manager for Removal](https://openjdk.java.net/jeps/411) removes the SecurityManager and AccessController and, in doing so, allows your library code to run with the full permissions of its Java process, which is the same as running with none of the permission checks that were used to harden Java's API, so if an attacker breaks into your Java process via some other vulnerability, they will be able to load their own byte codes, and pretty much do whatever the process permissions permits them and possibly more if your system has other vulnerabilities.

#### What can I do?

You can join the discussion on this topic in the #jeps channel [on the Foojay Slack](https://join.slack.com/t/foojay/shared_invite/zt-jnkc9y5x-vS05~nb37oq9pSp1sgDGvA), or use other public discussions, such as via [Reddit](https://www.reddit.com/r/java/comments/njs5j5/why_jep_411_will_have_a_negative_impact_on_java/) or [Twitter](https://twitter.com/foojayio/status/1396727820592848900), as well as the [\[email protected\]](/cdn-cgi/l/email-protection) mailing list. You're also welcome to add additional articles on this topic on Foojay.io.
