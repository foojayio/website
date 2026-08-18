---
title: "Null object pattern to avoid null pointer exception with JEP-455"
slug: "exploring-new-features-in-jdk-23-null-object-pattern-to-avoid-null-pointer-exception-with-jep-455"
date: "2024-10-04T06:19:18+00:00"
lastmod: "2024-10-04T06:19:19+00:00"
description: "A null object pattern, in our case a NULL_SENSOR instance, can easily contribute to the process flow of a running program while remaining consistent and type-safe."
authors:
  - "miro-wengner"
image: "Favicon-3-2.png"
categories:
  - "Java"
  - "JEPs"
tags:
related_posts:
  - "exploring-new-features-in-jdk-23-factory-pattern-with-flexible-constructor-bodies-with-jep-482"
  - "exploring-new-features-in-jdk-23-simplifying-java-with-module-import-declarations-with-jep-476"
  - "exploring-new-features-in-jdk-23-simplifying-java-with-primitive-type-patterns-with-jep-455"
  - "boxlang-1-14-0-boxset-is-here-boxlangs-new-first-class-set-type"
frozen: false
---

**The newest release JDK 23 \[2\] comes with a neat extension to pattern matching for switch and instanceof statements, JEP-455: Primitive Types in Patterns, instanceof and switch \[1\]. While the introduction of pattern matching can be considered a crucial element in moving the Java platform into a functional paradigm, it introduced several limitations.**

Using reference object types or objects only for switch and instanceof statements slightly reduced the possibilities of its utilization. This has had an impact on its use in building code based on design patterns, including refactoring.

### Functional nature of the code and null

The functional programming paradigm seeks to provide a code as an application and composition of functions.That is, the function accepts input arguments, where an expression tree maps those arguments to output value of specific type. Although the keyword null can represent a justifiable state, when nothing is expected, null reference is nothing, null has been the source of many unpleasant surprises. The keyword null got its place in imperative programming style but in functional flow of the expressions and callbacks could be questionable.

In a previous post, we explored how to use a factory to preserve the functional nature of code \[4\]. In this post we review the null object design pattern \[3\] that could be one way to escape a situation where we require to do nothing. The null object design pattern belongs to a family of behavioral design patterns with a strong emphasis on code maintainability as null pointer exception, may not be the favorite app state. Let's draw a situation where it is necessary to have different types of sensors available (Example 1.). The sensor only has a primitive integer type that requires rounding or casting, resulting in a loss of information, which is one of the challenges the JEP-455 aims to solve \[1\].

```
interface VehicleSensor {
   String UNDEFINED = "undefined";
   String type();
   int value();
}
record DefaultSensor(String type, int value) implements VehicleSensor {
   DefaultSensor(String t, Double v) {
       System.out.println("constructor Double:" + v);
       this(t, v.intValue());
   }
   DefaultSensor(String t, double v) {
       System.out.println("constructor double:" + v);
       this(t, (int) v);
   }
   DefaultSensor(String t, Integer i) {
       System.out.println("constructor Integer:" + i);
       this(t, i.intValue());
   }
   DefaultSensor(String t) {
       System.out.println("constructor String:" + t);
       this(t, -1);
…}
```

**Example 1.**: JEP-455 increases the support of primitive types for the records and their decomposition

To avoid creating a wrapper around hardware IO every time we request a specific sensor, we use a cache pattern \[3\]. Each Default sensor is created with appropriate constructor (Example 2.).

```
final public class VehicleSensorCache {
   public static VehicleSensor NULL_SENSOR = new NullSensor();
   ...
   private static final List<VehicleSensor> sensors =
           Arrays.asList(new DefaultSensor("engine", valueInteger),
                   new DefaultSensor("break", valueDouble), 
 new   DefaultSensor("platform"));

   private VehicleSensorCache() {
   }

   static VehicleSensor getSensor(String name) {
       return sensors.stream()
               .filter(s -> s.type().equals(name.toLowerCase()))
               .findFirst()
               .orElse(NULL_SENSOR);
   }
}

Output:
constructor Integer:1
constructor double:1.5
constructor String:platform
```

**Example 2.** : Vehicle sensor cache returns as default state *NULL_SENSOR* object and each sensor uses primitive values

Let's imagine we have a sequence of sensors that need to be called to obtain the value. Such a sensor may or may not be present inside the cache. The JEP-455 enables smooth code folding into a functional paradigm using various types of terminal or intermediate operations (Example 3.)

```
var engineSensor = VehicleSensorCache.getSensor("engine");
var breakSensor = VehicleSensorCache.getSensor("break");
var testSensor = VehicleSensorCache.getSensor("test");

Stream.of(engineSensor, breakSensor, testSensor)
       .forEach(sensor -> {
           switch (sensor.value()) {
               case 1 -> System.out.println("engine:" + sensor);
               case 2 -> System.out.println("break:" + sensor);
               case -1 -> System.out.println("platform:" + sensor);
               default -> System.out.println("unknown:" + sensor);
           }
       });

Output:
engine:DefaultSensor[type=engine, value=1]
break:DefaultSensor[type=break, value=2]
unknown:NullSensor{type='undefined'}
```

**Example 3.**: The support of primitive types inside the switch statements simplifies the code

### Conclusion

The newly added enhancements provided by JEP-455 are further evidence that Java remains in tune with industry needs.

The growing demands for computationally intensive calculations go hand in hand with the allocated memory and the number of iterations needed to find the correct position in it.

Enabling the use of primitive types for instanceof, switch statement, and pattern matching opens up new possibilities not only in code composition, but can also increase the speed of program executions.

A null object pattern, in our case a NULL_SENSOR instance (Example 2.), can easily contribute to the process flow of a running program while remaining consistent and type-safe.

### References

[\[1\] JEP 455: Primitive Types in Patterns, instanceof, and switch (Preview)](https://openjdk.org/jeps/455)  
[\[2\] Java 23 Has Arrived, And It Brings a Truckload of Changes](https://foojay.io/today/java-23-has-arrived-and-it-brings-a-truckload-of-changes/)  
[\[3\] Practical Design Patterns for Java Developers](https://amzn.eu/d/a3CsBu7)  
[\[4\] Exploring New Features in JDK 23: Factory Pattern with Flexible Constructor Bodies with JEP-482](https://foojay.io/today/exploring-new-features-in-jdk-23-factory-pattern-with-flexible-constructor-bodies-with-jep-482/)
