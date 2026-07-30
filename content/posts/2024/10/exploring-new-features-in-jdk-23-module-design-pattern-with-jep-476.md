---
title: "JDK 23: Module Design Pattern with JEP-476"
slug: "exploring-new-features-in-jdk-23-module-design-pattern-with-jep-476"
date: "2024-10-31T09:41:20+00:00"
lastmod: "2024-10-31T09:41:21+00:00"
description: "JEP-476 is another great example of Java platform evolution while enabling internal project stability, transparency and security."
authors:
  - "miro-wengner"
image: "https://foojay.io/wp-content/uploads/2024/10/jep476.png"
categories:
  - "Java"
  - "Java Beginner"
  - "Java Core"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Although the module design pattern can be implemented in many different ways, the main motivation behind using it remains the same. This is the isolation of a specific section into small building blocks, which are the so-called modules. Therefore, the module design pattern belongs to the structural design pattern family.

Before Java 9, each project using the module design pattern at any level was a bit difficult and many times broken. Such a situation had a direct impact on source code maintainability and event extensibility, as it was very easy to break module boundaries. Many times this state has been called a bit like "classpath hell". In order to avoid such a situation, it was necessary to preserve the coding discipline.

### **Module** design pattern before Java 9 {#h3-0-module-design-pattern-before-java-9}

Modular code base design can take many forms \[3\]. One possible approach is to create a Singleton object. Such an object serves as a gateway to basic services so that these services are not misused.

Even this idea, due to the previous internal architecture of the java platform, brought many challenges not only to proper maintainability (**Example 1.**).

<pre class="EnlighterJSRAW" data-enlighter-language="generic">File: SecureModule.java</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic">public class SecureModule {
   private static SecureModule INSTANCE;
   public static SecureModule getInstance(String name) {
       if (INSTANCE == null) {
           INSTANCE = new SecureModule(name);
       }
       return INSTANCE;
   }
   public String name;
   private SecureModule(String name) {
       this.name = name;
   }
   public void executeLogic() {
       System.out.println("SecureModule, executeLogic name:" + name);
   }
}</pre>

**Example 1.**: Possible gateway class SecureModule to the "secure" module providing couple of secret logic before Java 9 release

The code above already provides a brief insight into the challenge of securing a module, as any member of the classpath can access the SecureModule logic without even considering bytecode generation.

The given example of a SecureModule gateway already blindly exposes its inner fields regardless of proper visibility inside the package. Such state may lead to the following access to the module internals (**Example 2.**)

<pre class="EnlighterJSRAW" data-enlighter-language="generic">System.out.println("Before Java 9: no modules");
var secureModule = SecureModule.getInstance("security in important");
secureModule.executeLogic();
secureModule.name = "maybe not wanted";
secureModule.executeLogic();</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Output:
Before Java 9: no modules
SecureModule, executeLogic name:security in important
SecureModule, executeLogic name:maybe not wanted</pre>

**Example 2.**: Compromising of access to internal module logic due to incorrect visibility of internal fields

### Pushing modularization forward with JEP-476 {#h3-1-pushing-modularization-forward-with-jep-476}

Starting with Java 9 \[5\], Java platform represents a module system. Although it may seem that for various reasons it is still not fully accepted or even considered a controversial decision by the architects of the Java platform, today's tech development shows otherwise.

In fact, the Java Platform Modules System guarantees transparency, consistency of internal APIs, and enables the extensibility of already developed and isolated modules without compromising the code base or runtime.

Modules can take full advantage not only of internal package visibility restrictions for fields or classes, but provide well-described rules for how to interact with external modules and their packages. This means that a module provides a way to interact with external classes and their instances that reside in other packages/modules.

JEP-476\[1\]\[4\] makes a neat enhancement on the current state by simplifying the use of module design while providing an almost "look-alike" approach used in the classpath era (**Example 3.**). Although the state may feel similar, the code-base is more restricted and secure against malicious abuse.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">File: Jep476BaseMain
import module java.base;

public class Jep476BaseMain {
   public static void main(String[] args) {
       var list = Arrays.asList("ONE", "TWO");
       System.out.println("list:" + list);
   }
}</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Command: $ java --enable-preview Jep476BaseMain.java
Output:
list:[ONE, TWO]</pre>

**Example 3.**: Importing a module provides access to all classes and interfaces without having to specify them

The ability to import an entire module (exposed APIs) can have a positive effect for newcomers to speed up the learning process without having to delve into the details of the platform. In addition, JEP-476 improves the utilization of the module design pattern and its use, while facilitating the interaction of the required module with the external environment (**Example 4.** , **Example 5.**)

```java
├── jep476mod1
│   ├── pom.xml
│   ├── src
│   │   └── main
│   │       └── java
│   │           ├── com
│   │           │   └── wengnerits
│   │           │       └── jep476mod1
│   │           │           ├── Jep476ModuleMain.java
│   │           └── module-info.java
├── jep476mod2
│   ├── pom.xml
│   ├── src
│   │   ├── main
│   │   │   └── java
│   │   │       ├── com
│   │   │       │   └── wengnerits
│   │   │       │       └── jep476mod2
│   │   │       │           ├── FactoryElement.java
│   │   │       │           ├── Jep476FactoryOne.java
│   │   │       │           └── Jep476FactoryTwo.java
│   │   │       └── module-info.java
```

**Example 4.**: Modularized code-based enforces more control about exposed APIs while improving security

<pre class="EnlighterJSRAW" data-enlighter-language="generic">import module jep476mod2;
public class Jep476ModuleMain {
   public static void main(String[] args) {
       System.out.println("JEP 476: Module Import Declarations (Preview)");
       System.out.println("FactoryOne element:" + Jep476FactoryOne.produce());
       System.out.println("FactoryTwo element:" + Jep476FactoryTwo.produce());
   }
}</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Output:
JEP 476: Module Import Declarations (Preview)
FactoryOne element:FactoryElement[name=factory1, value=22]
FactoryTwo element:FactoryElement[name=factory2, value=42]</pre>

**Example 5.**: Each factory is automatically available and can hide different implementations that remain hidden for the jep476mod1 module

### Conclusion {#h3-2-conclusion}

JEP-476 is another great example of Java platform evolution while enabling internal project stability, transparency and security.

Allowing the API of an entire module to be directly imported without having to specify individual classes simplifies the use of a module design architecture and can remove difficulties or suspicion about the Java Platform Module System itself.

This state can lead not only to improving the general security of the codebase, but also to improving maintainability and preventing the occurrence of legacy code.

### References {#h3-3-references}

[\[1\] JEP 476: Module Import Declarations (Preview)](https://openjdk.org/jeps/476)  
[\[2\] Java 23 Has Arrived, And It Brings a Truckload of Changes](https://foojay.io/today/java-23-has-arrived-and-it-brings-a-truckload-of-changes/)  
[\[3\] Practical Design Patterns for Java Developers](https://amzn.eu/d/a3CsBu7)  
[\[4\] JEP-477: Implicitly Declared Classes and Instance Main Methods (Third Preview)](https://openjdk.org/jeps/477)  
[\[5\] Project Jigsaw](https://openjdk.org/projects/jigsaw/)
