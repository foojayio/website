---
title: "Avoid Java Serialization: The Gift That Keeps On Giving"
slug: "avoid-java-serialization"
date: "2020-11-11T08:48:37+00:00"
lastmod: "2020-11-11T08:48:40+00:00"
description: "If you need to Deserialize an inputstream yourself, you should use an ObjectsInputStream with restrictions."
authors:
  - "bmvermeer"
image: "/images/posts/2020/11/avoid-java-serialization/Favicon-3-2.png"
categories:
  - "Security"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Serialization in Java allows us to transform an object to a byte stream. This byte stream is either saved to disk or transported to another system. The other way around, a byte stream can be deserialized and allows us to recreate the original object.

The biggest problem is with the deserializing part. Typically it looks something like this:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">ObjectInputStream in = new ObjectInputStream( inputStream );
return (Data)in.readObject();</pre>

There's no way to know what you're deserializing before you decoded it. Possibly, an attacker serialized a malicious object and sent it to your application. Once you call `readObject()`, the malicious objects have already been instantiated. You might believe that these kinds of attacks are impossible because you need to have a vulnerable class on you classpath. However, if you consider the amount of classes on your classpath---that includes your own code, Java libraries, third-party libraries and frameworks---it is very likely that there is a vulnerable class available.

Java serialization is also called "the gift that keeps on giving" because of the many problems it has produced over the years. Oracle is planning to eventually remove Java serialization as part of Project Amber. However, this may take a while, and it's unlikely to be fixed in previous versions. Therefore, it is wise to avoid Java serialization as much as possible. If you need to implement `serializable` on your domain entities, it is best to implement its own `readObject()`, as seen below. This prevents deserialization.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private final void readObject(ObjectInputStream in) throws java.io.IOException {
   throw new java.io.IOException("Deserialized not allowed");
}</pre>

If you need to Deserialize an inputstream yourself, you should use an `ObjectsInputStream` with restrictions. A nice example of this is the `ValidatingObjectInputStream` from Apache Commons IO. This `ObjectInputStream` checks whether the object that is deserialized, is allowed or not.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">FileInputStream fileInput = new FileInputStream(fileName);
ValidatingObjectInputStream in = new ValidatingObjectInputStream(fileInput);
in.accept(Foo.class);

Foo foo_ = (Foo) in.readObject();</pre>

Object deserialization problems are not restricted to Java serialization. Deserialization from JSON to Java Object can contain similar problems. An example of such a deserialization issue with the Jackson library is in the blog post ["Jackson Deserialization Vulnerability"](https://snyk.io/blog/jackson-deserialization-vulnerability/).

This was just 1 of 10 Java security best practices. Take a look at [the full 10](https://snyk.io/blog/10-java-security-best-practices/) and the easy [printable one-pager](https://snyk.io/blog/10-java-security-best-practices/) available.
