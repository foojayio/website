---
title: "Explaining Java Deserialization Vulnerabilities (Part 1)"
slug: "explaining-java-deserialization-vulnerabilities-part-1"
date: "2021-01-21T10:12:19+00:00"
lastmod: "2021-01-28T09:00:13+00:00"
description: "Java serialization—and specifically deserialization—is “the gift that keeps on giving” for the security issues produced over the years."
canonical: "https://snyk.io/blog/serialization-and-deserialization-in-java/"
authors:
  - "bmvermeer"
image: "/images/posts/2021/01/explaining-java-deserialization-vulnerabilities-part-1/image-7-1024x433.png"
categories:
  - "Security"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Java serialization is a mechanism to transform an object into a byte stream. Java deserialization is exactly the other way around and allows us to recreate an object from a byte stream.

Java serialization---and more specifically deserialization in Java---is also known as "the gift that keeps on giving". This relates to the many security issues and other problems it has produced over the years.

### Serialization and Deserialization in Java? {#h3-0-serialization-and-deserialization-in-java}

In Java, we create objects. These objects live in memory and are removed by the garbage collector once they are not used anymore. If we want to transfer an object, for instance, store it on a disk or send it over a network, we need to transform it into a byte stream. To do this, the class of that object needs to implement the interface `Serializable`. Serialization is converting the state of an object into a byte stream. This byte stream does not contain the actual code.

Java serialization uses reflection to scrape all the data from the object's fields that need to be serialized. This includes private and final fields. If a field contains an object, that object is serialized recursively. Even though you might have getters and setters, these functions are not used when serializing an object in Java.

Deserialization is precisely the opposite of serialization. With deserialization, you have a byte stream and you recreate the object in the same state as when you serialized it. This means that you need to have the actual definition of the object to accomplish the recreation.

When deserializing a byte stream back to an object it does not use the constructor. It creates an empty object and uses reflection to write the data to the fields. Just like with serialization, private and final fields are also included.

A Java deserialize vulnerability is a security vulnerability that occurs when a malicious user tries to insert a modified serialized object into the system that eventually compromises the system or its data. Think of arbitrary code execution that can be triggered when deserializing a serialized object. To better explain Java deserialize vulnerabilities, we first need to explore how deserialization works in Java.

### Tampering with Data in Serialized Objects {#h3-1-tampering-with-data-in-serialized-objects}

A serialized object in Java is a byte array with information of the state. It contains the name of the object it refers to and the data of the field. If you look at a stored serialized object with a hex-editor, you can enclose and manipulate the information quickly.

We already know that Java deserialization does not use the constructor to create an object but rather uses reflection to load the fields. This means that any validation checks done in the constructor are never called when recreating the object. You can think about checks like start-date before end-date when describing a period. When deserializing a Java object, this new object can have an invalid state.

Let's look at the following example of Java deserialize vulnerability where we serialize an object from a serializable class `ValueObject`:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class ValueObject implements Serializable {

   private String value;
   private String sideEffect;

   public ValueObject() {
       this("empty");
   }

   public ValueObject(String value) {
       this.value = value;
       this.sideEffect = java.time.LocalTime.now().toString();
   }
}

ValueObject vo1 = new ValueObject("Hi");
FileOutputStream fileOut = new FileOutputStream("ValueObject.ser");
ObjectOutputStream out = new ObjectOutputStream(fileOut);
out.writeObject(vo1);
out.close();
fileOut.close();</pre>

When reading the file `ValueObject.ser` containing the serialized object with a hex-editor the output is this:
![](/images/posts/2021/01/explaining-java-deserialization-vulnerabilities-part-1/image-7-1024x433.png)

Now I can easily manipulate the string value. Below I change it from `Hi` to `Hallo`:
![](/images/posts/2021/01/explaining-java-deserialization-vulnerabilities-part-1/image-8-1024x411.png)

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">FileInputStream fileIn = new FileInputStream("ValueObject2.ser");
ObjectInputStream in = new ObjectInputStream(fileIn);
ValueObject vo2 = (ValueObject) in.readObject();</pre>

When deserializing the adjusted binary file, we find out that the object's `value` changed. We also see that the timestamp didn't change, proving that the constructor is never called.

So, if an application accepts serialized objects, it is relatively easy to tamper with the values.

By altering the serialized objects, we can create invalid objects, mess with the data's integrity, or even worse.

### Up Next {#h3-2-up-next}

In [part 2](https://foojay.io/today/explaining-java-deserialization-vulnerabilities-part-2/) of this multi-part article, we will dive a bit deeper into arbitrary code execution problems that are possible because of Java deserialization.

We will also discuss Gadget chains and how to prevent Java deserialization issues in your own code.

* [Explaining Java Deserialization Vulnerabilities (Part 2)](https://foojay.io/today/explaining-java-deserialization-vulnerabilities-part-2/)

This article was originally posted on [Snyk.io](https://snyk.io/blog/serialization-and-deserialization-in-java/)
