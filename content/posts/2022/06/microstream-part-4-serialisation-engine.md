---
title: "MicroStream Next-Generation Serialisation Engine"
slug: "microstream-part-4-serialisation-engine"
date: "2022-06-29T08:07:20+00:00"
lastmod: "2022-06-29T08:07:22+00:00"
description: "Let's go even deeper in this series that focuses on the key features of the MicroStream serialisation engine."
authors:
  - "rudy-de-busscher"
image: "https://foojay.io/wp-content/uploads/2022/06/microstream-1.png"
categories:
  - "Databases"
  - "DataEngineering"
  - "nosql"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**In this fourth part we go deeper into the Serialisation engine that is within MicroStream to store the Object graph in a binary format.**

In the previous articles ([part 1](https://foojay.io/today/microstream-part-1-what-is-it/), [part 2](https://foojay.io/today/microstream-part-2-configure-the-storage-manager/) and [part 3](https://foojay.io/today/microstream-part-3-storing-data/)), we have already mentioned that MicroStream stores Java instances in storage in a binary way with a new, from the ground up created, serialisation framework.

In this article, we go a bit more in detail about the next generation Java serialisation that we have built to achieve the MicroStream Java Object database and how you can use it outside the functionality of storing the root object that makes up your database.

Java Serialisation {#h2-0-java-serialisation}
---------------------------------------------

Serialisation is integrated within the JVM and Java since the early days. By adding the Serializable interface to the class definition, an instance can be passed to an *ObjectOutputStream* and it will be converted to some binary format.

That same byte sequence can be converted back to an instance by an ObjectInputStream. You can customise the entire process by implementing the `readObject()` and `writeObject()` methods.

But many of us have learned to [avoid this Java functionality](https://foojay.io/today/avoid-java-serialization/) as it became rather quickly clear that the entire construction allowed for malicious operations.

When the JVM deserialises the bytes, it performs more than just creating the instances and populating data. It also executes constructors and other methods, even before the control is handed back to the user statements.

This allowed for the so-called gadget chains to be discovered that a certain combination of Java classes that are available in a typical application can lead to the execution of malicious code. They only need to create some specific deserialised content to have the malicious code to be executed.

Mark Reinhold, currently the chief architect of the Java Platform, called the current Java serialisation "a horrible mistake" since many security vulnerabilities are related to this functionality.

With Java 17 and the Context-specific deserialisation filters option, the security vulnerabilities are fixed but it still is not an easy-to-use piece of functionality with some limitations.

MicroStream Serialisation {#h2-1-microstream-serialisation}
-----------------------------------------------------------

Using the standard Java serialisation functionality was not an option as it must be possible to serialise any Java class or instance, MicroStream created a new serialisation engine from the ground up.

This allows us to handle any class without the need for any interface or annotation and thus also classes from any dependency of your project. It also allowed us to incorporate a Type Mapping functionality so it can handle changes in your class as your data model evolves over time.

For each Java instance, we look at the instance variables and store only data together with identifications for variables names and class names. When deserialising, we create instances and populate the instance variables again but don't

We use the low-level Java API for that to create instances without actually calling constructors and setting instance variable values directly. By just handling the data, we make sure that no code is executed during deserialisation which makes it safe. Even when the Type Dictionary is compromised, which holds the mapping between the ids used in the binary representation and the actual class and instance variable names, unexpected classes might be created but since no code is executed, this does not harm your environment in any way. And once such an instance is accessed by your code there will be fatal exceptions as the class is not as expected.

Advanced Features {#h2-2-advanced-features}
-------------------------------------------

But the engine can do more than just store and read java instances. Two additional features make it suitable for using the JVM memory as your database.

First, it allows for the lazily loading of the data. It can just restore the instances from the storage only when you access them. It allows you to have a very large dataset that would never fit into the memory of your process. The engine initially just creates a very small reference so that at the time you access a Lazy object, it can be loaded from the storage if needed. The Lazy option is discussed already in detail in [part 3](https://foojay.io/today/microstream-part-3-storing-data/).

The second functionality is the ability to transform the data when it is loaded. Your data model will evolve over time. Some small changes like a change in the variable name or even a very large refactoring of classes. With the Type Mapping feature of the engine, this can be handled so that your data is not lost and converted automatically when loaded.

The engine can even detect some small changes automatically, like a change in name or an additional variable. For more complex changes, you define the Type Mapping where you indicate the old and the new structure and how the conversion needs to be performed.

Using the MicroStream Serialisation {#h2-3-using-the-microstream-serialisation}
-------------------------------------------------------------------------------

The MicroStream serialisation is part of the entire framework and is used to store the Java instances to the storage.

But you can access the serialisation also outside the standard usage and access it directly to create a byte array from some object(s). Similar to what is possible with the standard Java serialisation logic.

To do that, you need the following artefact that exposes the required methods.

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;dependency&gt;
    &lt;groupId&gt;one.microstream&lt;/groupId&gt;
    &lt;artifactId&gt;microstream-persistence-binary&lt;/artifactId&gt;
    &lt;version&gt;${microstream.version}&lt;/version&gt;
&lt;/dependency&gt;</pre>

Suppose you have an Employee class where you model the company structure and hierarchy. The following snippets create a serialised and convert the objects, even with the circular reference, to a byte array.

<pre class="EnlighterJSRAW" data-enlighter-language="java">SerializerFoundation&lt;?&gt; foundation = SerializerFoundation.New()
        .registerEntityTypes(Employee.class);
try (Serializer&lt;byte[]&gt; serializer = Serializer.Bytes(foundation)) {

    byte[] data = serializer.serialize(theBoss);
} catch (Exception e) {
    throw new RuntimeException(e);
}</pre>

And you can deserialise the bytes to create the Object instances again.

```

```

Conclusion {#h2-4-conclusion}
-----------------------------

To have a generic serialisation solution that can persist any Java instance, without any restrictions like implementing an interface or requiring annotations that define the mapping, a new algorithm was implemented.  

From the ground up, and different from the standard Java persistence to avoid the security vulnerabilities that are associated with that approach.

The serialisation only stores the data and not the class structure. Within the persistence solution, a system called TypeDictionary keeps the information of the structure separately.

But during the deserialisation, no constructor or method is called, only data is restored so that the process is secure and does not allow for gadget chains to be developed.

### Resources {#h3-5-resources}

* [MicroStream Reference manual for Type Mapping](https://docs.microstream.one/manual/storage/legacy-type-mapping/index.html)
* [MicroStream Website](https://microstream.one/)
