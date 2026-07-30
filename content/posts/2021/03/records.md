---
title: "Introduction to Java Records: Data carrier classes"
slug: "records"
date: "2021-03-31T07:16:13+00:00"
lastmod: "2021-08-23T12:24:02+00:00"
description: "Record classes provide a way to model data in Java, simplifying coding, making Java more concise and readable, and increasing productivity."
canonical: "https://jfeatures.com/blog/records"
authors:
  - "vipin-sharma"
image: "/images/posts/2021/03/records/Favicon-3-2.png"
categories:
  - "Records"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Professional Java developers need immutable data carrier-classes for communication with databases and web Services. We need to write a lot of boilerplate code to create a simple data carrier-class, we typically implement constructor, accessors, equals(), hashCode(), and toString(). ***This process is repetitive and error-prone. Developers also complain "Java is too verbose".***

Record classes provide a way to model data in Java. An example of data is a row in a database table. This feature simplifies coding, makes Java code more concise and readable, increasing productivity for professional Java developers. Java14 introduced Records as a preview feature, Java15 brings in some updates as a second preview, and Java16 makes it a final feature, no changes will be needed for Records after this.

### Common Implementation Use Cases {#h3-0-common-implementation-use-cases}

**1. Multiple Return Values:** Often we encounter cases when we want to return multiple values from a method, for this we will have to create a class having values that we need to return. The record provides an easy way rather than writing boilerplate code.

**2. Data Transfer Objects (DTO):** Developers working with databases often write DTO which is typically used for storage only, we can again reduce boilerplate code using java Record classes.

### Java Code Before and After Records {#h3-1-java-code-before-and-after-records}

Following is one example showing Point class without using record:

<pre class="EnlighterJSRAW" data-enlighter-language="java">class Point
{
 public final int x;   
 public final int y;    
 public Point(int x, int y) { this.x = x; this.y = y; }    
 public int getX() {...}   
 public int getY() {...}   
 public String toString() {...}   
 public boolean equals(Object o) {...}   
 public int hashCode() {...)  
}</pre>

```

```

**Record equivalent for Point class is following one line, WOW !**

<pre class="EnlighterJSRAW" data-enlighter-language="java">record Point(int x, int y){}</pre>

In this example, the Record class name is Point, and it has two components x and y that describe a state. The Record class can have a body as well, later in this post we have such examples.

We can use the javap command to see the compiled class:

<pre class="EnlighterJSRAW" data-enlighter-language="java">javac com/jfeatures/jdk16/records/Point.java 
javap -p com/jfeatures/jdk16/records/Point.class

Compiled from "Point.java"
final class com.jfeatures.jdk16.records.Point extends java.lang.Record {
  private final int x;
  private final int y;
  com.jfeatures.jdk16.records.Point(int, int);
  public final java.lang.String toString();
  public final int hashCode();
  public final boolean equals(java.lang.Object);
  public int x();
  public int y();
}</pre>

In the above output of the javap command we can see the record classes have:

* A private final field for each component in record declaration(state description). (In the above example private final int x, private final int y)
* A public read accessor method for each component of the Record, with the same name and type as the parameter. (public int x(), public int y())
* A public constructor, having the same arguments as the components of the record, is also called a canonical constructor. This constructor initializes each field from the corresponding argument. (com.jfeatures.jdk16.records.Point(int, int))
* Implementations of equals and hashCode that say two record classes are equal if they are of the same type and contain the same state.
* An implementation of toString that includes the string representation of all the record components, with their names.

### Record Classes in Detail {#h3-2-record-classes-in-detail}

Record classes behave like normal classes except restrictions, following are few properties of the Record classes:

* Can be declared as top-level or nested, can be generic.
* Can implement interfaces.
* Are instantiated via the new keyword.
* Record class body may declare static methods, static fields, static initializers, constructors, instance methods, and nested types.
* The record class and the individual components in a state description, can be annotated.
* We can define a nested record class. Nested record is implicitly static, because an immediately enclosing instance can add a state to the record.
* Instances of record classes can be serialized and deserialized. Serialization is done using Record components and deserialization is done using the canonical constructor. Serialization and deserialization can not be customized via regular means (writeObject, readObject, readObjectNoData, writeExternal, or readExternal methods).

#### Restrictions for Records

Following code shows compilation error in extends, because Record classes are implicitly final.

<pre class="EnlighterJSRAW" data-enlighter-language="java">record Base(int a) { }
record Child(int a, int b) extends Base { }</pre>

Similarly, we have a few more restrictions to follow for record classes.

Restrictions on Record classes can be divided into 3 categories:

* Restrictions that ensure the record class components alone defines the representation
  * record classes cannot extend any other class
  * record classes cannot declare instance fields, only record components carry the state of the record object.
* Restrictions that emphasize the API of a record is defined solely by its record components, and cannot be enhanced  
  later by another class or record.
  * record classes are implicitly final.
  * record classes cannot be abstract.
  * record classes cannot declare native methods.
* Restriction to make sure record is immutable by default.
  * The components of a record are implicitly final.

#### Constructors

Record classes have 3 types of constructors:

1. **Canonical constructor** contains all components of the record. This is declared implicitly, can be declared explicitly as well. Starting From Java15, if the canonical constructor is implicitly declared then its access modifier is the same as the record class. If the canonical constructor is explicitly declared then its access modifier must provide at least as much access as the record class.

<pre class="EnlighterJSRAW" data-enlighter-language="java">record Employee(String name, int id) {
   Employee(String name, int id) {
       this.name = name;
       this.id = id;
   }
}</pre>

2. **Compact canonical constructor** doesn't have any parameter, it is always called when defined. The compact form helps developers focus on validating and normalizing parameters. Here parameters are declared implicitly, and the private fields corresponding to record components are automatically assigned (this.x = x) at the end of the constructor.

<pre class="EnlighterJSRAW" data-enlighter-language="java">record Employee(String name, int id) {
    Employee {
        //validation
        if(name.length()==0) throw new RuntimeException("Nota a valid name");
    }
}</pre>

   For a Record class, only one out of canonical constructor or compact canonical constructor can be defined. Defining both results into compilation failure.
3. **Custom constructor** lets us create custom constructors as well as having only a few parameters from the Record header. Since this is not a canonical constructor, its first statement must invoke another constructor of the record class.

<pre class="EnlighterJSRAW" data-enlighter-language="java">record Employee(String name, int id) {
   public Employee(String name) {
       this(name, 0);
   }
}</pre>

   Starting from Java 15, assigning any of the instance fields (record components) in the constructor body became a compile-time error. Only the canonical constructor is allowed to do this.

#### Update on @Override annotation

Java 15 extends the meaning of the @Override annotation to include an explicitly declared accessor method for a record. Now following is valid java code.

<pre class="EnlighterJSRAW" data-enlighter-language="java">package com.jfeatures.jdk16.records;

public record Employee(String name, int id) {

    @Override
    public int id() {
        return id;
    }

    @Override
    public String name() {
        return name;
    }
}</pre>

```

```

#### Local record, enum, and interfaces are now allowed in Java

Java15 introduced the ability to declare local record classes, local enum classes, and local interfaces. Nested record classes and local record classes are implicitly static. It avoids adding an immediate enclosing instance to the state of the record class.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class LocalComponents {
    public static void main(String[] args) {
        System.out.println("Start Test");
        new LocalComponents().instanceMethod();
    }

    void instanceMethod() {
        record LocalRecord(int x, int y) {
            LocalRecord {
                System.out.println("Inside Local Record compact canonical constructors");
            }
        }
        enum LocalEnum {
            VALUE1,
            VALUE2;
        }

        interface LocalInterface extends Cloneable {
        }
    }
}</pre>

For versions before Java15, above code will not compile. Following is a compilation error for local enum in above example.

<pre class="EnlighterJSRAW" data-enlighter-language="java">com/jfeatures/jdk16/records/LocalComponents.java:16: error: enum types must not be local
enum LocalEnum {
^
1 error</pre>

#### Inner class can declare static members

Before Java 16, an inner class can not declare a static member. Java 16 allows the inner class to declare a member of the type record class. This will allow an inner class to declare a member that is a record class.

<pre class="EnlighterJSRAW" data-enlighter-language="java">package com.jfeatures.jdk16.records;

public class RecordInInnerClass {
    public static void main(String[] args) {
        System.out.println("Starting test");
    }

    class Inner{
        record TestRecord(int id, String name){
        }
    }
}</pre>

This code shows below compilation error with Java 15, it works fine with Java16 or later.

<pre class="EnlighterJSRAW" data-enlighter-language="java">javac --enable-preview -source 15 com/jfeatures/jdk16/records/RecordInInnerClass.java
com/jfeatures/jdk16/records/RecordInInnerClass.java:9: error: static declarations not allowed in inner classes
record TestRecord(int id, String name){
^
Note: com/jfeatures/jdk16/records/RecordInInnerClass.java uses preview language features.
Note: Recompile with -Xlint:preview for details.
1 error</pre>

### Why Records, Why Not Just Tuples? {#h3-3-why-records-why-not-just-tuples}

A central aspect of Java's philosophy is that "names" matter.

A `Person` with properties `firstName` and `lastName` is clearer and safer than a tuple of `String` and `String`.

### Conclusion {#h3-4-conclusion}

Records help you remove repetitive and error prone code, reduce bugs in your code, reduces verbosity in code, and increases developer productivity. Using language features like this is going to make you a great developer everyone wants to hire.

If you want to get amazing Java jobs, I wrote an ebook [5 steps to Best Java Jobs](https://jfeatures.com/). You can download this step-by-step guide for free!

### Resources {#h3-5-resources}

* <https://openjdk.java.net/jeps/395>
* [https://cr.openjdk.java.net/\~briangoetz/amber/datum.html](https://cr.openjdk.java.net/~briangoetz/amber/datum.html)
