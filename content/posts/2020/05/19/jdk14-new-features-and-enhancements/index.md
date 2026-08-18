---
title: "JDK14 - New Features and Enhancements"
date: "2020-05-19T23:29:37+00:00"
lastmod: "2020-08-26T10:17:39+00:00"
description: "Java 14 (Java SE 14) and its Java Development Kit 14 (JDK 14) is now available! Know more about new features and enhancements in JDK14."
canonical: "https://www.azul.com/blog/whats-new-in-jdk14-latest-release/"
authors:
  - "simonritter"
image: "Favicon-3-2.png"
categories:
  - "Performance"
tags:
related_posts:
  - "how-is-leyden-improving-java-performance-part-3-of-3"
  - "how-is-leyden-improving-java-performance-part-2-of-3"
  - "indexing-all-of-wikipedia-on-a-laptop"
  - "billion-events-per-second-with-millisecond-latency"
frozen: false
---

Well, another six months have passed, and we have another release of Java, this one pretty packed with exciting new features. It is, therefore, time for another blog post trying to list everything new in [JDK 14](https://openjdk.java.net/projects/jdk/14/).

In total, there are a very impressive 16 JDK Enhancement Proposals (JEPs) and 69 new API elements.

Let's start with the more significant items that introduce changes to the Java language syntax.

## Records

Java is an object-oriented language; you create classes to hold data and use encapsulation to control how that data is accessed and modified. The use of objects makes manipulating complex data types simple and straightforward. It's one of the reasons Java is so popular as a platform.

The downside (until now) is that creating a data type has been verbose, requiring a lot of code even for the most straightforward cases. Let's look at the code needed for a basic two-dimensional point:

```java
public class Point {
  private final double x;
  private final double y;

  public Point(double x, double y) {
    this.x = x;
    this.y = y;
  }

  public double getX() {
    return x;
  }

  public double getY() {
    return y;
  }
}
```

That's 14 lines of code just to represent a two-value tuple.

JDK 14 introduces records as a [preview feature](https://openjdk.java.net/jeps/12). Preview features are a new concept that allows the developers of the Java platform to include a new language feature without making it part of the Java SE standard. By doing this, developers can try out these features and provide feedback allowing changes (or even the removal of a feature), if required, before the feature becomes set in the standard. To use a preview feature, you must specify the command line flag, `--enable-preview`for both compilation and runtime. For compilation, you must also specify the `-source`flag.

A record is a much simpler way of representing a data class. If we take our Point example the code can be reduced to a single line:

```java
public record Point(double x, double y) { }
```

This takes nothing away from the readability of the code; we're immediately aware that we now have a class that contains two double values called x and y that we can access using the standard accessor method names of getX and getY.

Let's examine some of the details of records.

To start with, records are a new kind of type that is a restricted form of class in the same way that an enum is. A record has a name and a state description, which defines the components of the record. In the Point example above, the state description is the doubles, x and y. Records are designed for simplicity so they cannot extend any other class or define additional instance variables. All state in a record is final, so no accessor (setter) methods are provided. If you need any of that, you need to use a full-blown class.

Records do have flexibility, though.

Often, the constructor needs to provide additional behaviour beyond just assigning values. If this is the case, we can provide an alternative implementation of the constructor:

```java
record Range(int min, int max) {
  public Range {
    if (min > max)
      throw new IllegalArgumentException(“Max must be >= min”);
  }
}
```

Note that the constructor definition is still abbreviated, as specifying the parameters is redundant. Any of the members that are automatically derived from the state description can also be declared so, for example, you can provide an alternative `toString()`or `hashCode()`implementation.

## Pattern Matching instanceof

In some situations, you do not know the exact type of an object. To handle this, Java has the instanceof operator that can be used to test against different types. The drawback to this is that, having determined the type of an object; you must use an explicit cast if you want to use it as that type:

```java
if (o instanceof String) {
  String s = (String)o;
  System.out.println(s.length);
}
```

In JDK 14, the instanceof operator has been extended to allow a variable name to be specified in addition to the type. That variable can then be used without the explicit cast:

```java
if (o instanceof String s)
  System.out.println(s.length);
```

The scope of the variable is limited to where its use is logically correct so:

```java
if (o instanceof String s)
  System.out.println(s.length);
else
  // s is out of scope here
```

The scope can also apply within the conditional statement so we can do something like this:

```java
if (o instanceof String s && s.length() > 4) ...
```

This makes sense since the length() method will only be called *if* o is a String. The same does not work with a logical or operation:

```java
if (o instanceof String s || s.length() > 4) ...
```

In this case, s.length() needs to be evaluated regardless of the result of whether o is a String. Logically, this does not work and so will result in a compilation error.

Using the logical not operator can produce some interesting scoping effects:

```java
if (!(o instanceof String s && s.length() > 3)
  return;

System.out.println(s.length());  // s is in scope here
```

I have seen some negative feedback on the way variables are scoped but, given that all scoping is entirely logical, I think it works very well.

There are already plans for a second preview version of this feature that will extend pattern matching to work with records and provide a simple way of implementing the deconstruction pattern. More details of this can be found in the [JEP 375](https://openjdk.java.net/jeps/375).

## Helpful NullPointerException

Anyone who's written more than a few lines of Java code will have experienced a NullPointerException at some point. Failing to initialise an object reference (or mistakenly explicitly setting it to null) and then trying to use the reference will cause this exception to be thrown.

In simple cases, finding the cause of the problem is straightforward. If we try and run code like this:

```java
public class NullTest {
  List<String> list;

  public NullTest() {
    list.add("foo");
  }
}
```

The error generated is:

```java
Exception in thread "main" java.lang.NullPointerException
            at jdk14.NullTest.<init>(NullTest.java:16)
            at jdk14.Main.main(Main.java:15)
```

Since we're referencing *list* on line 16, it's evident that *list* is the culprit and we can quickly resolve the problem.

However, if we use chained references in a line like this:

```java
a.b.c.d = 12;
```

When we run this, we might see an error like this:

```java
Exception in thread "main" java.lang.NullPointerException
    at Prog.main(Prog.java:5)
```

The problem is that we are unable to determine from this whether the exception is as a result of *a* being null, *b* being null or *c* being null. We either need to use a debugger from our IDE or change the code to separate the references onto different lines; neither of which is ideal.

In JDK 14, if we run the same code, we will see something like this:

```java
Exception in thread "main" java.lang.NullPointerException:
    Cannot read field "c" because "a.b" is null
    at Prog.main(Prog.java:5)
```

Immediately, we can see that a.b is the problem and set about correcting it. I'm sure that this will make many Java developers lives easier.

## New APIs

Now let's turn our attention to the changes in the class libraries.

### java.io

PrintStream has two new methods, write(byte\[\] buf) and writeBytes(byte\[\] buf). These effectively do the same thing, which is equivalent to write(buf, 0, buf.length). The reason for having two different methods is that write is defined to throw an IOException (but, bizarrely, never does), whereas writeBytes does not. The choice of which to use therefore depends on whether you want to surround the call with a try-catch block.

There is a new annotation type, Serial. This is intended to be used for compiler checks on Serialization. Specifically, annotations of this type should be applied to serialisation-related methods and fields in classes declared to be Serializable. (It is similar in some ways to the Override annotation).

### java.lang

The Class class has two methods for the new Record feature, isRecord() and getRecordComponents(). The getRecordComponents() method returns an array of RecordComponent objects. RecordComponent is a new class in the java.lang.reflect package with eleven methods for retrieving things such as the details of annotations and the generic type.

Record is a simple new class that overrides the equals, hashCode and toString methods of Object.

NullPointerException now overrides the getMessage method from Throwable as part of the helpful NullPointerExceptions feature.

The StrictMath class has six new methods that supplement the existing exact methods used when overflow errors need to be detected. The new methods are decrementExact, incrementExact and negateExact (all with two overloaded versions for int and long parameters).

### java.lang.annotation

The ElementType enumeration has a new constant for Records, RECORD_TYPE.

### java.lang.invoke

The MethodHandles.Lookup class has two new methods:

* hasFullPrivilegeAccess, which returns true is the method being looked up has both PRIVATE and MODULE access.
* previousLookupClass, which reports a lookup class in another module that this lookup object was previously teleported from, or null. I had not heard of teleporting in the context of Java before (only in Doctor Who and Minecraft). Lookups can result in *teleporting* across modules.

### java.lang.runtime

This is a new package in JDK 14 that has a single class, ObjectMethods. This is a low-level part of the records feature having a single method, bootstrap, which generates the Object equals, hashCode and toString methods.

### java.util.text

The CompactNumberFormat class has a new constructor that adds an argument for pluralRules. This a String designating plural rules which associate the Count keyword, such as "one", and the actual integer number. Its syntax is defined in Unicode Consortium's Plural rules syntax.

### java.util

The HashSet class has one new method, toArray, which returns an array, whose runtime component type is Object, containing all of the elements in this collection.

### java.util.concurrent.locks

The LockSupport class has one new method, setCurrentBlocker. LockSupport provides the ability to park and unpark a thread (which does not suffer from the same problems as the deprecated Thread.suspend and Thread.resume methods). It is now possible to set the Object that will be returned by getBlocker. This can be useful when calling the no-argument park method from a non-public object.

### javax.lang.model.element

The ElementKind enumeration has three new constants for the records and pattern matching for instanceof features, namely BINDING_VARIABLE, RECORD and RECORD_COMPONENT.

### javax.lang.model.util

This package provides utilities to assist in the processing of program elements and types. With the addition of records, a new set of abstract and concrete classes have been added to support this feature. (Examples are AbstractTypeVisitor14, ElementScanner14 and TypeKindVisitor14).

### org.xml.sax

One new method has been added to the ContentHandler interface of the SAX XML parser. The declaration method receives a notification of the XML declaration. In the case of the default implementation, this does nothing.

### JEP 370: Foreign Memory Access API

This is being introduced as an incubator module to allow testing by the broader Java community and feedback to be integrated before it becomes part of the Java SE standard. It is intended as a valid alternative to both sun.misc.Unsafe and java.io.MappedByteBuffer.

The foreign-memory access API introduces three main abstractions:

* MemorySegment: providing access to a contiguous memory region with given bounds.
* MemoryAddress: providing an offset into a MemorySegment (basically, a pointer).
* MemoryLayout: providing a way to describe the layout of a memory segment that greatly simplifies accessing a MemorySegment with a var handle. Using this, it is not necessary to calculate the offset based on the way the memory is being used. For example, an array of ints or longs will offset differently but will be handled transparently using a MemoryLayout.

## JVM Changes

I scanned all 609 pages of the JVM specification but couldn't find any highlighted differences. It will be interesting to look at the bytecodes generated for records since this doesn't require any special support at the JVM level. The same applies to the helpful NullPointerException feature.

JDK 14 does include some JEPs that change non-functional parts of the JVM

* [**JEP 345**](https://openjdk.java.net/jeps/345): NUMA-Aware Memory Allocation for G1. This improves performance on large machines that use non-uniform memory architecture (NUMA).
* [**JEP 363**](https://openjdk.java.net/jeps/363): Remove the Concurrent Mark Sweep (CMS) Garbage Collector. Since JDK 9, G1 has been the default collector and is considered by most (but not all) to be a superior collector to CMS. Given the resources required to maintain two similar profile collectors, Oracle decided to [deprecate CMS](https://openjdk.java.net/jeps/291) (also in JDK 9), and now it is being removed. If you are looking for a high-performance, low-latency alternative, why not try [C4](http://go.azul.com/continuously-concurrent-compacting-collector) in the [Zing JVM](https://azulstagingenv.wpengine.com/products/zing/)?
* [**JEP 349**](https://openjdk.java.net/jeps/349): Java Flight Recorder Event Streaming. This allows more realtime monitoring of a JVM by enabling tools to subscribe asynchronously to Java Flight Recorder events.
* [**JEP 364**](https://openjdk.java.net/jeps/364): ZGC on macOS and [JEP 365](https://openjdk.java.net/jeps/365): ZGC on Windows. ZGC is an experimental low-latency collector that was initially only supported on Linux. This has now been extended to the macOS and Windows operating systems.
* [**JEP 366**](https://openjdk.java.net/jeps/366): Deprecate the ParallelScavenge and SerialOld GC combination. Oracle states that very few people use this combination and the maintenance overhead is considerable. Expect this combination to be removed at some point in the not-too-distant future.

## Other Features

There are a number of JEPs that relate to different parts of the OpenJDK:

* [**JEP 343**](https://openjdk.java.net/jeps/343): Packaging Tool. This is a simple packaging tool, based on the JavaFX javapackager tool that was removed from the Oracle JDK in JDK 11. There's quite a lot to this so I'll write more detail in a separate blog post.
* [**JEP 352**](https://openjdk.java.net/jeps/352): Non-volatile Mapped Byte Buffers. This adds a new JDK-specific file mapping mode so that the FileChannel API can be used to create MappedByteBuffer instances that refer to non-volatile memory. A new module, jdk.nio.mapmode, has been added to allow MapMode to be set to READ_ONLY_SYNC or WRITE_ONLY_SYNC.
* [**JEP 361**](https://openjdk.java.net/jeps/361): Switch Expressions Standard. Switch expressions were the first preview feature added to OpenJDK in JDK 12. In JDK 13, feedback resulted in the break *value* syntax being changed to yield *value.* In JDK 14, switch expressions are no longer a preview feature and have been included in the Java SE standard.
* [**JEP 362**](https://openjdk.java.net/jeps/362): Deprecate the Solaris and SPARC ports. With Oracle no longer developing either the Solaris operating system or SPARC chip architecture, they do not want to have to continue the maintenance of these ports. These might be picked up by others in the Java community.
* [**JEP 367**](https://openjdk.java.net/jeps/367): Remove the Pack 200 Tools and API. Another feature that was [deprecated in JDK 11](https://openjdk.java.net/jeps/336) and now removed from JDK 14. The primary use of this compression format was for jar files used by applets. Given the browser plugin was removed from Oracle JDK 11, this seems a reasonable feature to remove.
* [**JEP 368**](https://openjdk.java.net/jeps/368): Text Blocks. These were [included in JDK 13](https://openjdk.java.net/jeps/355) as a preview feature, and they continue with a second iteration (still in preview) in JDK 14. Text blocks provide support for multi-line string literals. The changes are the addition of two new escape sequences. The first suppresses the inclusion of a newline character by putting a \\ at the end of the line (this is common in many other places in software development). The second is \\s, which represents a single space. This can be useful to prevent the stripping of whitespace from the end of a line within a text block.

As you can see, JDK 14 has packed in a lot of new features that will make the lives of developers easier.
