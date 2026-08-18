---
title: "JDK 23: Factory Pattern with Flexible Constructor Bodies"
slug: "exploring-new-features-in-jdk-23-factory-pattern-with-flexible-constructor-bodies-with-jep-482"
date: "2024-09-25T11:14:01+00:00"
lastmod: "2024-10-06T09:21:04+00:00"
description: "Let's take a look at one particular brand new preview feature added to the latest JDK release 23 [2], JEP-482: Flexible Constructor Bodies in its second iteration."
authors:
  - "miro-wengner"
image: "Favicon-3-2.png"
categories:
  - "Java"
  - "JEPs"
tags:
related_posts:
  - "java-23-has-arrived-and-it-brings-a-truckload-of-changes"
  - "java-23-whats-new"
  - "foojay-podcast-57"
  - "boxlang-1-14-0-boxset-is-here-boxlangs-new-first-class-set-type"
enlighterjs: true
frozen: false
---

Since the introduction of the new JDK release cadence, we have seen an incredible amount of new enhancements added to each JDK cycle, kicking the Java platform back on track compared to other languages.

Source code maintainability plays a very important role as complexity of nowadays projects increases. Yes, design patterns, spelled forward and backward many times, but forgotten at the same time due to various reasons.

Let's take a look at one particular brand new preview feature added to the latest JDK release 23 \[2\], JEP-482: Flexible Constructor Bodies \[1\] in its second iteration.

### Creational design pattern factory review

One of the most frequent creational design patterns could be considered factory method\[3\]. Factory methods aim to centralize a new instance creation at the runtime. It separates the code base responsibilities.

This separation makes it possible to hide the creation of a complex object while keeping the focus not only on maintainability. The class constructor flexibility can greatly contribute to simplifying creation patterns, as the flexibility to create any new instance can be crucial for most demanding projects.

### Simplifying factory method with JEP-482

Let's code a bit and introduce the *VehicleSensorFactory* with an overloaded method *createVehicleSensor* (Example 1.).

```java
public final class VehicleSensorFactory {
   public static VehicleSensor createVehicleSensor(String type, Integer value){
       return new CylinderValueSensor(type, value);
   }
   public static VehicleSensor createVehicleSensor(Integer value) {
       return new EngineValueSensor(value);
   }
}
```


**Example 1.**: input arguments initiated different types of VehicleSensor

Each created incarnation of *VehicleSensor* interface contains the default functionalities provided by the *AbstractValueSensor* class (Example 2).

```java
interface VehicleSensor {
   String UNDEFINED = "undefined";
   String type();
 …
}

abstract class AbstractValueSensor implements VehicleSensor {
   static final Integer UNDEFINED_VALUE = -1;
   protected final Integer value;

   public AbstractValueSensor(Integer value) {
       this.value = value;
   }
...
```


**Example 2.**: Considered parentheses and abstractions hierarchy

The flexibility of the constructor can play a vital role in creating the desired object. It allows constructor arguments to be validated and reacts to unexpected states accordingly before the resulting values are passed to *super(...)* or *this(...)* (Example 3.).

This shifts transparently the class specific internal logic from creational pattern to object constructor itself. In other words the factory pattern is enabled to provide more separation in a functional coding style.

```java
class CylinderValueSensor extends AbstractValueSensor {
   private final String type;
   CylinderValueSensor(String type, Integer value) {
       if (value < 0) {
           value = UNDEFINED_VALUE;
           type = UNDEFINED;
       }
       super(value);
       this.type = type;
   }
  ...
}
```


**Example 3.** : Constructor considers unexpected situations and initiates  

internals accordingly

In case an exception coding style is desired, JEP-482 allows arguments to be validated and an exception raised accordingly before calling *super(..)* or *this(..)*. This allows a program or thread to fail quickly without having to create a new instance, which is not required anyway.

```java
class EngineValueSensor extends AbstractValueSensor {
   private final String type = "engine_sensor";
   private final CylinderValueSensor cylinderSensor;

   EngineValueSensor(Integer value) {
       if (value <= 0) throw new IllegalArgumentException("value greater than zero, value: " + value);
       super(value);
       this.cylinderSensor = new CylinderValueSensor("cylinderSensor", value);
   }
 …
```


**Example 4.**: Throwing an exception due to invalid arguments without instantiating an object

### Conclusion

The newly proposed JEP-482 aims to solve the much-discussed limitation introduced in the early stages of Java, resp. Java 1.0. The rule where *super(..)* or *this(..)* must be placed as the first statement inside a constructor before placing any other statement \[3\]. The impact of the rule usually caused unclear solutions that could lead to unsustainable code composition.

An easy example would be a runtime that is forced to instantiate an object and an exception is thrown shortly after the object is created (Example 5.). A side effect of such an approach is also heap pollution, as many threads can contribute to such a state.

```java
EngineValueSensor(Integer value) {
  super(value);
  if (value <= 0) throw new IllegalArgumentException("value greater than zero, value: " + value);
  this.cylinderSensor = new CylinderValueSensor("cylinderSensor", value);
}
```


**Example 5.** : Since Java 1.0 the first statement of the constructor was *super(...)* or *this(...)*

**JEP-482** comes up with a possible solution to improve and rethink the current use of creational patterns \[1\]. The example given shows the shift of responsibilities and the possible reduction of the boilerplate code.

Additionally, the newly introduced flexibility of constructors can have a positive impact on using functional coding style, where instead of throwing an exception, state can be passed to the code flow (Example 5.). JEP-482 can be seen as another sweet example of the long-term evolution of the Java platform towards the functional coding style\[3\] that today's businesses fully demand and expect.

```java
JEP-482:Flexible Constructor Bodies
[VehicleValueSensor{type='undefined', value=-1}, EngineValueSensor{type='engine_sensor', cylinderSensor=VehicleValueSensor{type='cylinderSensor', value=2}, value=2}]
Exception in thread "main" java.lang.IllegalArgumentException: value grater than zero, value: -2
    at com.wengnerits.jep482.EngineValueSensor.<init>(VehicleSensorFactory.java:18)
    at com.wengnerits.jep482.VehicleSensorFactory.createVehicleSensor(VehicleSensorFactory.java:80)
    at com.wengnerits.jep482.Jep482Main.main(Jep482Main.java:25)
```


**Example 5.**: The example output compares two approaches to instantiation, one considering state versus an exception-throwing style.

### Resources

[\[1\] JEP-482: Flexible Constructor Bodies (Second Preview](https://openjdk.org/jeps/482)  
[\[2\] Java 23 Has Arrived, And It Brings a Truckload of Changes](https://foojay.io/today/java-23-has-arrived-and-it-brings-a-truckload-of-changes/)  
[\[3\] Practical Design Patterns for Java Developers](https://amzn.eu/d/a3CsBu7)  
[\[4\] JDK23: Factory pattern with flexible constructor bodies, JEP-482](https://openvalue.blog/posts/2024/09/21/jdk23_jep482/)
