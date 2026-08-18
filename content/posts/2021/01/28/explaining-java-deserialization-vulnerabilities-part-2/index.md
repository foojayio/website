---
title: "Explaining Java Deserialization Vulnerabilities (Part 2)"
date: "2021-01-28T08:23:00+00:00"
lastmod: "2021-01-28T08:33:25+00:00"
description: "In this part, we continue with even more harmful attacks and show you how you can prevent this in your own code."
canonical: "https://snyk.io/blog/serialization-and-deserialization-in-java/"
authors:
  - "bmvermeer"
image: "Favicon-3-2.png"
categories:
  - "Security"
tags:
related_posts:
frozen: false
---

Java serialization is a mechanism to transform an object into a byte stream. Java deserialization is exactly the other way around and allows us to recreate an object from a byte stream. Java serialization---and more specifically deserialization in Java---is also known as "the gift that keeps on giving". This relates to the many security issues and other problems it has produced over the years.

* [Explaining Java Deserialization Vulnerabilities (Part 1)](https://foojay.io/today/explaining-java-deserialization-vulnerabilities-part-1/)

Earlier, in part 1, the basics of Java serialization and deserialization were explained and how to tamper with data in serialized objects. In this part, we continue with even more harmful attacks and show you how you can prevent this in your own code.

### Arbitrary Code Execution, Gadgets, and Chains

Tampering with the data in an object is harmful already. However, this can also lead to code execution if the correct set of objects is deserialized. To explain this I first have to explain gadgets and chains.

#### Gadgets

A gadget---as used by Lawrence \& Frohoff in their talk [Marshalling Pickles](https://frohoff.github.io/appseccali-marshalling-pickles/) at AppSecCali 2015---is a class or function that has already existing executable code present in the vulnerable process. This existing executable code can be reused for malicious purposes. If we look at Java serializable objects, some magic methods---like the private `readObject()`method---are reflectively called when deserializing.

Let's look at the simplified gadget below:

```java
public class Gadget implements Serializable {

   private Runnable command;

   public Gadget(Command command) {
       this.command = command;
   }

   private final void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
       in.defaultReadObject();
       command.run();
   }
}
```

This gadget class overrides the default `readObject` method. As a result, every time an Object of class Gadget gets deserialized, the `Runnable` object command is executed. When a command class looks something like the example below, it is easy to manipulate this serialized object and perform code injection.

```java
public class Command implements Runnable, Serializable {

   private String command;

   public Command(String command) {
       this.command = command;
   }

   @Override
   public void run() {
       try {
           Runtime.getRuntime().exec(command);
       } catch (IOException e) {
           throw new RuntimeException(e);
       }
   }
```

Also, note that if an application accepts serialized objects, the object is deserialized first before it is cast to the desired type. This means that even if casting fails, deserialization is already completed and the `readObject()` method is executed.

```java
FileInputStream fileIn = new FileInputStream("Gadget.ser");
ObjectInputStream in = new ObjectInputStream(fileIn);
var obj = (ValueObject)in.readObject();
```

#### Gadget Chain Deserialization Attack

A typical deserialization attack consists of a cleverly crafted chain of gadgets. An attacker searches for a gadget that is usable for launching an attack and chains several executions that end with arbitrary code execution, for instance.  

In our example:

```
Gadget -> readObject() -> command.run() -> Runtime.getRuntime().exec()
```

For a more real life example, take a look at the implementation of `java.util.HashMap`. This class has a custom implementation of the `readObject()` method that triggers every key's `hashcode()` function.

#### Libraries

It is good to know that whatever gadget chains are available in your application, is not related to your code. Because we import lots of code from libraries and frameworks, the number of classes imported by your (transitive) dependencies influences certain gadget chains' possibility. Although creating such a malicious gadget chain is very hard and labor-intensive, Java deserialization vulnerabilities are a genuine and dangerous security risk.

### How to Prevent a Java Deserialize Vulnerability

The best way to prevent a Java deserialize vulnerability is to prevent Java serialization overall. If your application does not accept serialized objects at all, it cannot harm you.

However, if you do need to implement the Serializable interface due to inheritance, you can override the `readObject()`,as seen below, to prevent actual deserialization.

```java
private final void readObject(ObjectInputStream in) throws java.io.IOException {
   throw new java.io.IOException("Deserialized not allowed");
}
```

If your application relies on serialized objects, you can consider inspecting your `ObjectInputStream` before deserializing. A library that can help you with this is the [Apache Commons IO](https://commons.apache.org/proper/commons-io/) library. This library provides a `ValidatedObjectInputStream` where you can explicitly allow the objects you want to deserialize. Now you prevent that unexpected types are deserialized at all.

```java
FileInputStream fileIn = new FileInputStream("Gadget.ser");
ValidatingObjectInputStream in = new ValidatingObjectInputStream(fileIn);
in.accept(ValueObject.class);
var obj = (ValueObject)in.readObject();
```

A tool like [ysoserial](https://github.com/frohoff/ysoserial) is also extremely useful in finding Java deserialize vulnerabilities in your code. It is a tool that generates payload to discover gadget chains in common Java libraries that can, under the right conditions, exploit Java applications performing unsafe deserialization of objects.

Note that Java deserialization vulnerabilities are not exclusive to Java's custom serialization implementation. Although this article merely focuses on this part, the same vulnerabilities exist in serialization or marshaling frameworks that handle this for you. If there is a framework that magically creates POJO's out of XML, JSON, yaml, or similar formats, it probably uses reflection in the same way as described above. This means that the same problems exist.

To prevent these kinds of Java deserialize vulnerabilities in your external libraries, scan your libraries with [Snyk Open Source](https://snyk.io/product/open-source-security-management/) early on and often. It's free!

* [Explaining Java Deserialization Vulnerabilities (Part 1)](https://foojay.io/today/explaining-java-deserialization-vulnerabilities-part-1/)

This article was originally posted on [Snyk.io](https://snyk.io/blog/serialization-and-deserialization-in-java/)
