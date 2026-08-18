---
title: "High-Performance Java Serialisation to Different Formats"
date: "2022-10-12T14:15:06+00:00"
lastmod: "2022-12-07T15:22:27+00:00"
description: "Learn the basics of serialization in an article that discusses some of the key advantages of the open source Chronicle Wire."
authors:
  - "rob-austin"
image: "Favicon-3-2.png"
categories:
  - "DevOps"
  - "Java Core"
  - "Performance"
  - "Tutorials"
tags:
related_posts:
  - "low-latency-microservices-a-retrospective"
  - "chronicle-queue-storing-1tb-in-virtual-memory-on-a-128gb-machine"
  - "did-you-know-you-can-create-mappers-without-creating-underlying-objects-in-java"
frozen: false
---

Java serialisation is a popular mechanism where you are able to serialise and deserialise complex object graphs; for example where object A can contain a reference to object B, which in turn has a reference back to object A.

The problem is that this rich functionality comes at a performance cost.

However, if you do not need to serialise these types of recursive graphs, you can instead use an open source solution called Chronicle Wire.

It has reduced complexity and uses tree-like structures which makes it very efficient.

Moreover, it can be done in a lot of different formats with no need to change the coding.

This article covers the basics of serialization and discusses some of the key advantages of [Chronicle Wire](https://chronicle.software/wire/ "Chronicle Wire").

### Serialisation and Deserialisation

Serialisation is about encoding java objects into bytes, for example, we have an object. Let's say our object holds our application state, if we were to shut down our application we would lose the state, we want to first store our application's state to disk, so we serialise our java state object. This will convert the object into bytes, which can be easily stored.

Likewise, If we want to send the data stored in our java object, over the network, we first have to serialise the object, before it can be written to the TCP/IP buffer. Deserialisation is the opposite of serialisation, where we start with a byte and recreate an object instance.

### About Chronicle Wire

[Chronicle Wire](https://chronicle.software/wire/ "Chronicle Wire ")is an Open Source library that was originally written to support Chronicle Queue and Chronicle Map. However, the library is useful in any code that uses serialisation. Chronicle Wire differs from native Java serialisation in that it actually supports a number of different formats, for example, binary, YAML, JSON, Raw binary data, and CSV.

The real innovation behind Chronicle Wire is that you don't have to change your code to change the encoding. The library abstracts away the implementation of the serialisation to a pluggable Wire implementation. The idea is that your objects need only describe what is to be serialised not how it should be serialised.

This is done by the objects (the POJOs that are to be serialised) implementing the Marshallable interface. "net.openhft.chronicle.wire.Marshallable" (When you use the Java Serialisation you add the marker interface on "java.io.Serializable".)

### The Encoding

Let's dig a little bit into the encoding. We have already mentioned that Java serialisation is coding objects to and from a binary format, whereas Chronicle Wire can also encode to a lot of different formats. The encoding will affect the number of bytes used to store the data, the more compact the format, the fewer bytes used.

Chronicle Wire balances the compactness of the format without going to the extreme of compressing the data, which would use valuable CPU time, Chronicle Wire aims to be flexible and backward compatible, but also very performant. Storing the data in as few bytes as possible without sacrificing performance, for example, integers can be stored using [stop bit encoding](https://github.com/OpenHFT/Chronicle-Bytes#reading-and-writing-strings "stop bit encoding").

Some encodings are more performant, perhaps by not encoding the field names to reduce the size of the encoded data, this can be achieved by using Chronicle Wire's Field Less Binary. However this is a trade-off, sometimes it is better to sacrifice a bit of performance and add the field names since it will give us both forwards and backward compatibility.

### Different Formats

There are various implementations of Chronicle Wire, each of them useful in different scenarios. For example, when we want to provide application configuration files or create data-driven tests, we often want to serialise or deserialise objects from and to human-readable formats like YAML, JSON. Also being able to send java objects serialised to a typed JSON allows us to send and receive messages from the JavaScript UI layer of our application.

Sometimes it is important to be able to interoperate between encoding formats.  

One example is the open-source product Chronicle Queue. Chronicle Queue stores its data using Chronicle Wire's compact binary format. Then it reads binary data, and subsequently, generically, logs it to the console in a human-readable YAML format. This is useful for debugging or compliance reporting. Example Human Readable Format  

Let's look at an example where Chronicle Wire encodes data to simple human-readable formats. We use the following DTO:

See [WireExamples1.java](https://github.com/OpenHFT/Chronicle-Wire/blob/ea/src/test/java/net/openhft/chronicle/wire/examples/WireExamples1.java "WireExamples1.java")

```java
package net.openhft.chronicle.wire;

import net.openhft.chronicle.core.pool.ClassAliasPool;
import static net.openhft.chronicle.bytes.Bytes.allocateElasticOnHeap;

public class WireExamples {
   public static class Car implements Marshallable {
       private int number;
       private String driver;

       public Car(String driver, int number) {
           this.driver = driver;
           this.number = number;
       }
   }

   public static void main(String... args) {
       // allows the the YAML to refer to car, rather than net.openhft.chronicle.wire.WireExamples$Car
       ClassAliasPool.CLASS_ALIASES.addAlias(Car.class);

       Wire wire = new YamlWire(allocateElasticOnHeap());
       wire.getValueOut().object(new Car("Lewis Hamilton", 44));
       System.out.println(wire);
   }
}
```

If we run this code, it will output the following YAML:

```yaml
!Car {
  number: 44,
  driver: Lewis Hamilton
}
```

However, if all we did was to change the YanmlWire from:

```java
Wire wire = new YamlWire(allocateElasticOnHeap());
```

To JSON Wire:

```java
Wire wire = new JSONWire(allocateElasticOnHeap());
```

Then it would output the following JSON:

```json
{"number":44,"driver":"Lewis Hamilton"}
```

If we wanted the JSON to also include the Java types, then we can also add the setting, useTypes(true):

```java
Wire wire = new JSONWire(allocateElasticOnHeap()).useTypes(true);
```

This will now also encode the java type, Car:

```json
{"@Car":{"number":44,"driver":"Lewis Hamilton"}}
```

#### Example Compact Binary Format

Let's continue with an example where we use a compact binary format instead:

See [WireExamples1.java](https://github.com/OpenHFT/Chronicle-Wire/blob/ea/src/test/java/net/openhft/chronicle/wire/examples/WireExamples1.java "WireExamples1.java") for details.

```java
package net.openhft.chronicle.wire;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.core.pool.ClassAliasPool;

public class WireExamples {
   public static class Car implements Marshallable {
       private int number;
       private String driver;

       public Car(String driver, int number) {
           this.driver = driver;
           this.number = number;
       }
   }

   public static void main(String... args) {
       ClassAliasPool.CLASS_ALIASES.addAlias(Car.class);

       Wire wire = WireType.FIELDLESS_BINARY.apply(Bytes.allocateElasticOnHeap());
       wire.getValueOut().object(new Car("Lewis Hamilton", 44));
       System.out.println(wire.bytes().toHexString());
   }
}
```

It outputs the following:

```
00000000 b6 03 43 61 72 82 10 00 00 00 2c ee 4c 65 77 69 ··Car··· ··,·Lewi 00000010 73 20 48 61 6d 69 6c 74 6f 6e s Hamilt on
```

#### Example Deserialisation

So far, all the examples have covered serialisation, so when it comes to deserialising, we can start with the data, for example:

```json
{"@Car":{"number":44,"driver":"Lewis Hamilton"}}
```

and we can then Deserialise this JSON back into a Java object:

```java
package net.openhft.chronicle.wire;

import static net.openhft.chronicle.core.pool.ClassAliasPool.CLASS_ALIASES;

public class WireExamples {
   public static class Car implements Marshallable {
       private int number;
       private String driver;

       public Car(String driver, int number) {
           this.driver = driver;
           this.number = number;
       }
   }

   public static void main(String... args) {
       CLASS_ALIASES.addAlias(Car.class);
       final Wire wire = new JSONWire().useTypes(true);
       wire.bytes().append("{\"@Car\":{\"number\":44,\"driver\":\"Lewis Hamilton\"}}");
       final Object object = wire.getValueIn().object();
   }
}
```

#### Example Forwards and Backwards Compatibility

If the field name is encoded, if we change the DTO, to include the "int numberOfPitStops" ( see example below ), these numeric values will just default to zero, when the deserialization occurs and the fields that it knows about will be loaded as usual.

```java
package net.openhft.chronicle.wire;

import static net.openhft.chronicle.core.pool.ClassAliasPool.CLASS_ALIASES;

public class WireExamples {
   public static class Car implements Marshallable {
       private int number;
       private int numberOfPitStops;
       private String driver;

       public Car(String driver, int number) {
           this.driver = driver;
           this.number = number;
       }
   }

   public static void main(String... args) {
       CLASS_ALIASES.addAlias(Car.class);
       final Wire wire = new JSONWire().useTypes(true);
       wire.bytes().append("{\"@Car\":{\"number\":44,\"driver\":\"Lewis Hamilton\"}}");
       final Object object = wire.getValueIn().object();
   }
}
```

#### Example Encoding Strings

Typically Strings are encoded using the UTF8 standard, however, Strings can also be encoded using a Base Encoder, such as the Base64 encoder which can store the data into a more compact string, or primitive field.

For each byte, there are 256 different combinations (this is because a byte is made up from 8 bits, bits are 0 or 1, giving 2\^8 combinations, hence 256 ), however, if we choose to use a base encoder, and assuming that we can restrict our string to the following characters ".ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+" \[ which are some of the most commonly used characters\], then we could use this base encoder to store 10 of these characters above into just 8 bytes.

Of course, you can create your own base encoding and it does not just have to contain this number of characters. With fewer characters, you can benefit from even greater compactness. And as mentioned, the more compact we can make the data the faster it is to read and write.

Below is an example of how Chronicle Wire can store small Strings in longs, the YAML serialiser displayed the string representation, yet the string is stored in the object using just an 8 byte long, likewise, binary serializer, will use the more compact 8byte long representation.

See [WireExamples2.java](https://github.com/OpenHFT/Chronicle-Wire/blob/ea/src/test/java/net/openhft/chronicle/wire/examples/WireExamples2.java "WireExamples2.java") for details.

```java
package net.openhft.chronicle.wire;

import net.openhft.chronicle.bytes.Bytes;
import static net.openhft.chronicle.core.pool.ClassAliasPool.CLASS_ALIASES;

public class WireExamples2 {
   public static class TextObject extends SelfDescribingMarshallable {
       transient StringBuilder temp = new StringBuilder();

       @LongConversion(Base64LongConverter.class)
       private long text;

       public TextObject(CharSequence text) {
           this.text = Base64LongConverter.INSTANCE.parse(text);
       }

       public CharSequence text() {
           Base64LongConverter.INSTANCE.append(temp, text);
           return temp;
       }
   }

   public static void main(String... args) {
       CLASS_ALIASES.addAlias(TextObject.class);
       final Wire wire = new BinaryWire(Bytes.allocateElasticOnHeap());

       // serialise
       wire.getValueOut().object(new TextObject("SAMPLETEXT"));

       // log out the encoded data
       System.out.println("encoded to=" + wire.bytes().toHexString());

       // deserialise
       System.out.println("deserialized=" + wire.getValueIn().object());
   }
}
```

### Conclusion

Chronicle Wire allows you to serialize and deserialize objects to and from binary format, and also to a lot of different formats at the same time as it has higher performance than Java standard serialization.

#### Learn More

Learn more about [Chronicle Wire](https://chronicle.software/wire/ "Chronicle Wire").
