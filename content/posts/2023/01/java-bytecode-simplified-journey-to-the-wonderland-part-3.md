---
title: "Java Bytecode: Journey to the Wonderland (Part 3) | Foojay Today"
slug: "java-bytecode-simplified-journey-to-the-wonderland-part-3"
date: "2023-01-24T15:27:51+00:00"
lastmod: "2023-01-26T15:16:18+00:00"
description: "Java bytecode may be modified to provide functionality, profile code, or obfuscate it. We'll examine examples in this brief article."
authors:
  - "bazlur-rahman"
image: "https://foojay.io/wp-content/uploads/2020/04/Favicon-3-2.png"
categories:
  - "Java Core"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Our [previous article](https://foojay.io/today/java-bytecode-simplified-journey-to-the-wonderland-part-2/ "previous article") unpacked Bytecode further and discussed ConstantPool, today I'll go through several resources for working with it now.

Java bytecode is the Java Virtual Machine's (JVM) intermediate representation of Java code. While Java bytecode is not meant to be human-readable, it may be edited and manipulated for several reasons. This article examines the tools and methods used to change and work with Java bytecode.

Changing Java bytecode is often done to add new features to a Java program that already exists. This can be done with a bytecode injector, a tool that lets you add bytecode to a Java class file that has already been compiled. Bytecode injectors are often used for logging or debugging information and for allowing runtime updates like A/B testing or feature flags.

**[Javaassist](https://www.javassist.org/)** is one of the tools that can be leveraged to inject bytecode. Look at the following class.

<pre class="EnlighterJSRAW" data-enlighter-language="java">package ca.bazlur;

public class Greetings {

  public void sayHello(String name) {
    System.out.println("Hello " + name + "!");
  }
}</pre>

Let's say we have this class and would like to add a method to it but through bytecode manipulation.

<pre class="EnlighterJSRAW" data-enlighter-language="java">package ca.bazlur;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class BytecodeInjector {

  public static void main(String[] args) throws IOException {

    try (var resource = BytecodeInjector.class.getResourceAsStream("Greetings.class")) {
      final var classBytes = resource.readAllBytes();

      // Create a ClassPool and import the original class
      ClassPool classPool = ClassPool.getDefault();
      CtClass ctClass = classPool.makeClass(new java.io.ByteArrayInputStream(classBytes));

      // Create a new method and add it to the class
      CtMethod newMethod = CtMethod.make("""
            public void printHelloWorld() {
              System.out.println("Hello, world!");
            }
          """, ctClass);
      ctClass.addMethod(newMethod);

      // Write the modified class back to a byte array
      byte[] modifiedClassBytes = ctClass.toBytecode();

      // Load the modified class bytes into the JVM
      MyClassLoader classLoader = new MyClassLoader();
      Class&lt;?&gt; modifiedClass = classLoader.defineClass("ca.bazlur.Greetings", modifiedClassBytes);

      // Invoke the new method on an instance of the modified class
      Object obj = modifiedClass.newInstance();
      Method method = modifiedClass.getMethod("printHelloWorld");
      method.invoke(obj);
    } catch (CannotCompileException | InvocationTargetException | InstantiationException |
             IllegalAccessException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
  }
}</pre>

In this code, we had a class called `Greetings`. We wanted to add a new method. To do that, we had to read the original class into a byte array, import it into a `ClassPool`, and then modify it by adding a new method. Then, the modified class is written back to a byte array and loaded into the JVM using a custom `ClassLoader`. Finally, the new method is invoked on an instance of the modified class.

<pre class="EnlighterJSRAW" data-enlighter-language="java">package ca.bazlur;

public class MyClassLoader extends ClassLoader {
  public Class&lt;?&gt; defineClass(String name, byte[] bytes) {
    return super.defineClass(name, bytes, 0, bytes.length);
  }
}</pre>

If we run the above class, we will see that the functionality has been added to the Greetings class and also executed. It will print:

<pre class="EnlighterJSRAW" data-enlighter-language="java">Hello, world!</pre>

There is also a program known as "[Byte Buddy](https://bytebuddy.net/)" and we can make use of it to do a similar thing.

Let's assume we want to know how much time a method takes to execute. We can make use of bytecode instrumentation. Bytecode instrumentation is another method for modifying Java bytecode. This is done by using a library or tool to change the bytecode of a Java class before the JVM loads it. This might be beneficial for adding performance monitoring or code profiling to an application.

Let's use bytebuddy to build a simple agent that will instrument every class and calculate the time it takes for each method to execute.

<pre class="EnlighterJSRAW" data-enlighter-language="java">package ca.bazlur;

import java.lang.instrument.Instrumentation;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.matcher.ElementMatchers;

public class MyAgent {

  public static void premain(String agentArgs, Instrumentation inst) {
    new AgentBuilder.Default()
        .type(ElementMatchers.any())
        .transform((builder, typeDescription, classLoader, module) -&gt; builder
            .method(ElementMatchers.any())
            .intercept(Advice.to(TimerAdvice.class)))
        .installOn(inst);
  }
}</pre>

And the `TimerAdvice`class is here:

<pre class="EnlighterJSRAW" data-enlighter-language="java">package ca.bazlur;

import net.bytebuddy.asm.Advice;

public class TimerAdvice {

  @Advice.OnMethodEnter
  static long invokeBeforeEachMethod(
      @Advice.Origin String method) {
    System.out.println("Entering to invoke : " + method);
    return System.currentTimeMillis();
  }

  @Advice.OnMethodExit
  static void invokeWhileExitingEachMethod(@Advice.Origin String method,
      @Advice.Enter long startTime) {
    System.out.println(
        "Method " + method + " took " + (System.currentTimeMillis() - startTime) + "ms");
  }
}</pre>

The full source code is available here: <https://github.com/rokon12/bytecode-tutorials>

Once we've built it and generated a jar, we can use it in the CLI by issuing the following command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">java -javaagent:myagent-1.0-SNAPSHOT.jar MyAwesomeJavaProgram</pre>

The `MyAwesomeJavaProgram` looks like this:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class MyAwesomeJavaProgram {
  public static void main(String[] args) {
    System.out.println(doCalculation());
  }

  public static int doCalculation() {
    int result = 0;
    for (int i = 0; i &lt; 100000000; i++) {
      result += i;
    }
    return result;
  }
}</pre>

Once we run it in the CLI, we will get the output as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="raw">Entering to invoke : public static void MyAwesomeJavaProgram.main(java.lang.String[])
Entering to invoke : public static int MyAwesomeJavaProgram.doCalculation()
Method public static int MyAwesomeJavaProgram.doCalculation() took 41ms
887459712
Method public static void MyAwesomeJavaProgram.main(java.lang.String[]) took 45ms</pre>

Here are some libraries for manipulating Java bytecode:

1. [ASM](https://asm.ow2.io/) - a fast, small, and efficient Java bytecode manipulation framework
2. [BCEL](https://commons.apache.org/proper/commons-bcel/) - a library for manipulating Java bytecode in the Apache Commons project
3. [Javassist](https://www.javassist.org/) - a bytecode manipulation library for Java
4. [Byte Buddy](https://bytebuddy.net/) - a library for generating and modifying Java bytecode
5. [CFR](https://www.benf.org/other/cfr/) - a bytecode decompiler for Java, written in Java

Changes can be made to Java bytecode for obfuscation and other reasons. "Obfuscation" is the process of making code harder to understand and figure out how it works. This may be beneficial for preventing a program's illegal usage or safeguarding intellectual property.

To make it harder to understand, Java bytecode can be obfuscated by changing the names of classes and methods or adding extra code.

There are various tools for obfuscating Java code available:

1. **[ProGuard](https://github.com/Guardsquare/proguard)**: This is a free and open-source program for optimizing and obscuring Java code. It can get rid of code that isn't needed, speed up code, and change the names of classes, fields, and functions to make them harder to understand.
2. **[DashO](https://www.preemptive.com/products/dasho/)**: A commercial obfuscation and optimization program that includes control flow obfuscation, string encryption, and watermarking.
3. **[Zelix KlassMaster](https://www.zelix.com/)**: A paid program that provides extensive obfuscation and protection capabilities, such as control flow obfuscation, string encryption, and class and member renaming.
4. **[Allatori](https://allatori.com/)**: is a commercial product that provides extensive obfuscation and security features such as control flow obfuscation, string encryption, and class and member renaming.
5. **[yGuard](https://github.com/yWorks/yGuard)**: is an open-source tool for optimizing and obscuring Java code. It can get rid of code that isn't needed, speed up code, and change the names of classes, fields, and functions to make them harder to understand.

In conclusion, Java bytecode can be updated and controlled for many reasons, such as adding new features, instrumenting code for performance monitoring or profiling, or obfuscating code to protect intellectual property.

But even though these methods may work sometimes, they must be used correctly and in accordance with the terms of any license applications.
