---
title: "New Java 17 Features for Improved Security and Serialization"
date: "2021-12-02T08:06:36+00:00"
lastmod: "2021-12-02T08:09:22+00:00"
description: "The Java 17 LTS release brings you significant improvements to prevent malicious deserialization in your java applications."
canonical: "https://snyk.io/blog/new-java-17-features-for-improved-security-and-serialization/"
authors:
  - "bmvermeer"
categories:
  - "JEPs"
  - "Security"
related_posts:
  - "are-java-security-updates-important"
  - "jep-411-what-it-means-for-javas-security-model"
  - "secure-code-review-best-practices-part-1"
frozen: false
---

In December 2020, I wrote the article[Serialization and deserialization in Java: explaining the Java deserialize vulnerability](https://snyk.io/blog/serialization-and-deserialization-in-java/) about the problems Java has with its custom serialization implementation. The serialization framework is so deeply embedded inside Java that knowing how dangerous some implementation can be is important. Insecure deserialization can lead to arbitrary code executions if a gadget chain is created from your classpath classes.

Recently, Java 17 — the new LTS version — was released. But how do the new features impact this problem, and can we prevent deserialization vulnerabilities better using these features?

In the blog post, I will look at three main Java 17 features:

1. Records
2. Java Flight Recorder (JFR) improvements
3. [JEP 415](https://openjdk.java.net/jeps/415) (Java Enhancement Proposal) Context-Specific Deserialization Filters.

## 1. Records

[Records](https://docs.oracle.com/en/java/javase/17/language/records.html) were introduced in Java 14 as a preview feature and became a fully released feature in Java 16. However, since many developers prefer to upgrade only to LTS versions, it makes sense to discuss Records in the context of serialization now Java 17 is fully released.  

In contrast to normal POJOs when deserializing a Record, the constructor is used to recreate the object. For ordinary Java objects, this is not the case, and the framework heavily depends on reflection. This means that any logic triggered by the constructor will not occur when deserializing an ordinary Java object. [Read my previous blog post](https://snyk.io/blog/serialization-and-deserialization-in-java/) for more information. For Records, there is no magic involved when recreating the object when deserializing. If for any reason you put validation logic in the constructor of a Record, we now know this will logic will be applied,

Nevertheless, we can debate if you should put any logic in a record at all. Doing so wrongly can create gadgets that can play a role in a deserialization gadget chain. More importantly, we still use the `readObject()` function to deserialize. This means that we are still vulnerable to gadget chains in normal POJOs regardless of records.

## 2. Deserialization Filters in Java

To address deserialization vulnerabilities in Java, it is possible to set serialization filters. This was introduced in Java 9 with the implementation of [JEP 290](https://openjdk.java.net/jeps/290). You can place limits on array sizes, graph depth, total references and stream size. In addition, you can create block and allow lists based on a pattern to limit the classes you want to get deserialized.  

You can set such a filter as a global JVM filter or individually per stream. For the global filter, you can set a JVM argument or set it in code. Below I created a filter that allows all classes from `mypackage` and blocks everything else.  

JVM argument:

```
-Djdk.serialFilter=nl.brianvermeer.example.*;!*
```

Code:

```
ObjectInputFilter filter = ObjectInputFilter.Config.createFilter("nl.brianvermeer.example.*;!*");
ObjectInputFilter.Config.setSerialFilter(filter);
```

You can also set a filter for a specific stream like below.

```
ObjectInputStream in = new ObjectInputStream(fileIn);
ObjectInputFilter filesOnlyFilter = ObjectInputFilter.Config.createFilter("nl.brianvermeer.example2.Object;!*");
in.setObjectInputFilter(filesOnlyFilter);
```

Up to Java 17, when I set a filter on a specific stream, the global filter is overridden for that stream. It doesn't combine the global filter with the stream-specific filter whatsoever. This is not a very flexible way of working. Moreover, it introduces the issue that your global filter might not work when a library you include does deserialization for you.

### Context-Specific Deserialization Filters in Java 17

Java 17 enhanced the[deserialization filter](https://docs.oracle.com/en/java/javase/17/core/serialization-filtering1.html#GUID-3ECB288D-E5BD-4412-892F-E9BB11D4C98A) with the implementation of JEP 415.

One of the most important things you can do now is setting a`SerialFilterFactory` to the `ObjectInputFilter.Config`. This factory needs to be a `BinaryOperator` and describes what to do when a specific filter is added to a particular stream.  

In the example below, I set a very basic factory to the `Config` that uses the default merge method to merge the existing filter with the new filter. With this tool I can decide if and filters should be merged or not, and how. This now solves the problem I discussed before, on how your libraries handle the global filter.

```
ObjectInputFilter.Config.setSerialFilterFactory((f1, f2) -> ObjectInputFilter.merge(f2,f1));
```

Next to the Filter Factory, Java 17 also gives you some nice convenience methods for easy filter creation. Function like `allowFilter()` and `rejectFilter()` on `ObjectInputFilter` are in my opinion a more declarative and readable way of creating filters.

In the Java 17 code example below I am using these new features. In the deserialize method in this example, I specifically reject the Gadget class. Both the Gadget class and the TwoValue record are part of the same package. The Gadget will now be rejected and all other classes in this package will be allowed by the filter.

```
public static void main(String[] args) throws IOException, ClassNotFoundException {
   var filename = "file.ser";
   var value = new TwoValue("one", "two");
   //var value = new Gadget(new Command("ls -l")); //This will not be deserialized

   var filter1 = ObjectInputFilter.allowFilter(cl -> cl.getPackageName().contentEquals("nl.brianvermeer.example.serialize.records"), ObjectInputFilter.Status.REJECTED);
   ObjectInputFilter.Config.setSerialFilter(filter1);
   ObjectInputFilter.Config.setSerialFilterFactory((f1, f2) -> ObjectInputFilter.merge(f2,f1));

   serialize(value, filename);
   deserialize(filename);
}

public static void serialize(Object value, String filename) throws IOException {
   System.out.println("---serialize");
   FileOutputStream fileOut = new FileOutputStream(filename);
   ObjectOutputStream out = new ObjectOutputStream(fileOut);
   out.writeObject(value);
   out.close();
   fileOut.close();
}

public static void deserialize(String filename) throws IOException, ClassNotFoundException {
   System.out.println("---deserialize");
   FileInputStream fileIn = new FileInputStream(filename);
   ObjectInputStream in = new ObjectInputStream(fileIn);
   ObjectInputFilter intFilter = ObjectInputFilter.rejectFilter(cl -> cl.equals(Gadget.class), ObjectInputFilter.Status.UNDECIDED);
   in.setObjectInputFilter(intFilter);
   TwoValue tv = (TwoValue) in.readObject();
   System.out.println(tv);
```

## 3. Java Flight Recorder Deserialization Events

The release of Java 17 also comes with a nice edition to the [Java Flight Recorder (JFR)](https://docs.oracle.com/en/java/javase/17/docs/specs/man/jfr.html) to help you in your crusade against deserialization exploits. This new Java version now supports a specific event to monitor deserialization. A deserialization event will be created for every Object in a stream and records all sorts of interesting things like: the actual type, if there was a filter, if the object was filtered, the object depth, the number of references etc.

All the information is handy to see if there is deserialization somewhere in your application and what is actually getting deserialized while your process is running. You need to make sure, however, to enable this event. It will not get captured by default, so you have to make a specific configuration similar to this.

```
​​<?xml version="1.0" encoding="UTF-8"?>
<configuration version="2.0" description="test">
   <event name="jdk.Deserialization">
      <setting name="enabled">true</setting>
      <setting name="stackTrace">false</setting>
   </event>
</configuration>
```

Let use the previous code example, but now deserializing the Gadget class. I get the following result when I execute the code in my IntelliJ IDEA with the Java Flight Recorder and the custom configuration.
![](https://snyk.io/wp-content/uploads/blog-java-17-deserialization-1240x827.jpg)

You see the event captures the actual object type when deserializing and that the filter rejects this object. If you want to know more in-depth how to use this specific Deserialisation event for the JFR, take a look at the blog post: [Monitoring Deserialization to Improve Application Security](https://inside.java/2021/03/02/monitoring-deserialization-activity-in-the-jdk/) by Chris Hegarty

## Upgrade to Java 17 for more powerful tools against insecure deserialization exploits

The Java 17 LTS release brings you significant improvements to prevent malicious deserialization in your Java applications. Therefore, upgrading to a newer version like 17 is essential if you want to adopt these practices. Still, with regards to Java's custom serialization, it is better to avoid it at all in my opinion. However, if you are forced to do this or rely on a library that executed Java's custom deserialization, you know how to protect yourself.

Also, be aware not to import libraries that contain either known deserialization gadget chains or have other deserialization security issues.

This article was originally posted on the Snyk.io blog: [https://snyk.io/blog/new-java-17-features-for-improved-security-and-serialization/](https://snyk.io/blog/docker-for-java-developers/) and is used with permission.
