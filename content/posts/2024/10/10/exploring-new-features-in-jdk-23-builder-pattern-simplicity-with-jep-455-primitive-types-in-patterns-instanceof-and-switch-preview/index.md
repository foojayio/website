---
title: "Builder Pattern Simplicity with JEP-455 Primitive Types"
slug: "exploring-new-features-in-jdk-23-builder-pattern-simplicity-with-jep-455-primitive-types-in-patterns-instanceof-and-switch-preview"
date: "2024-10-10T10:54:08+00:00"
lastmod: "2024-10-10T11:00:56+00:00"
description: "Explore how to shift an object initiation process across various parties with creational pattern builder and JEP-455."
authors:
  - "miro-wengner"
image: "jdk-23-builder.png"
categories:
  - "Java"
  - "Java Beginner"
  - "JDK 23"
  - "JEPs"
tags:
related_posts:
  - "exploring-new-features-in-jdk-23-factory-pattern-with-flexible-constructor-bodies-with-jep-482"
  - "exploring-new-features-in-jdk-23-null-object-pattern-to-avoid-null-pointer-exception-with-jep-455"
  - "exploring-new-features-in-jdk-23-simplifying-java-with-module-import-declarations-with-jep-476"
  - "boxlang-1-14-0-boxset-is-here-boxlangs-new-first-class-set-type"
frozen: false
---

**The runtime initiation of any program requires allocating a given memory and after many additional steps the first object could be created.**

**In a [previous post](https://foojay.io/today/exploring-new-features-in-jdk-23-factory-pattern-with-flexible-constructor-bodies-with-jep-482/), we explored how to improve maintainability of complex creation processes by using the factory creational pattern\[4\] and JEP-482, flexible constructor bodies.**

**The factory design may be a good fit for many cases but it may also create obstacles due to the pattern code centralized nature.**

**In this article, we review how to shift an object initiation process across various parties with creational pattern builder and JEP-455.**

## Building a vehicle

It may not be obvious from a simple example, but especially when a project moves away from an coding imperative style, it pushes the requirements on the object initiation process.

Separation of concerns principle along with the use of different design patterns\[3\] not only improves the maintainability and readability of the code, but also serves as an opportunity to control the object creation process.

The vehicle assembly process is a nice example of relatively complex steps that can be thought of as a collection of small pieces tied together with respect to their hierarchy (Example 1.).

```
enum VehicleType {
   ELECTRIC(1, (byte) 1), PETROL(2, (byte) 2), DIESEL(3, (byte) 3), UNKNOWN(-1, (byte) -1);

   private final int sensorId;
   private final byte value;

   VehicleType(int sensorId, byte value) {
       this.sensorId = sensorId;
       this.value = value;
   }
   ...
}

record Sensor(String name, byte value) {}

class TestVehicle implements Vehicle {
   final static class Builder {
       Builder addSensor(Sensor s) {
           this.sensors.add(s);
           return this;
       }
       ...
       Builder addType(VehicleType type) {
           var requiredSensors = switch (type.sensorId()) {
               case 1 -> createSensorByType(ELECTRIC);
               case 2 -> createSensorByType(PETROL);
               case 3 -> createSensorByType(DIESEL);
               default -> createSensorByType(UNKNOWN);
           };
           sensors.add(requiredSensors);
           this.type = type;
           return this;
        }
        ...
       TestVehicle build(String name) {
           return new TestVehicle(name, type, sensors);
       }
   } ...
```

**Example 1** .: JEP-455 simplifies a builder pattern removing by verbose casting steps inside *addType* method

The introduced *TestVehicle* builder with final method build is not only testable, easy to understand and extensible, but can also be passed as an argument to various services or methods contributing to the vehicle composition process.

JEP-455\[1\] aims to provide a way to securely pass and cast primitive values without data loss. This can occur when working with low-level sensors or abstractions that don't require storing reference types as *Integer* , *Double* or etc. inside the heap.

In such cases, primitive types are a good choice. JEP-455 enables creation of validation or transformation methods without typecasting necessity. Nice example is the method that could be used inside the Builder design pattern for direct validation without compromising object creation (Example 2.).

```
private static byte evaluateSensorByteValue(Object value) {
   return value instanceof byte b ? b : (byte) -1;
}
```

**Example 2.**: JEP-455 removes necessity of casting with information loss

Reducing the verbosity of the code will not be complete without introducing local variable type inference by JEP-286 \[5\], which greatly contributes to readability (Example 1, *addType* method).

The process of creating an instance of a specific class becomes clearer and smoother with the ability to perform some advanced operations while maintaining full control over the entire process\[3\].

```
var testVehicleBuilder = new TestVehicle.Builder()
       .addType(VehicleType.DIESEL);
// Complex construction process
var electricPlatformSensor = getPlatformSensor();
// Retrieve required object
var producedVehicle = testVehicleBuilder
       .addSensor(electricPlatformSensor)
       .build("testVehicle");
```

**Example 3.**: Builder allows you to add required parts downstream

## Conclusion

While a factory hides the instantiation logic behind a method, a single point of truth, which can turn out to be complex and not entirely maintainable, the Builder pattern serves to make each added component clear and testable.

The factory pattern may be a good fit for the early stages of the development, but with the new enhancements coming with JDK23 \[2\], the builder pattern may be re-evaluated as it may support the functional nature of current business requirements.

## References

[\[1\] JEP 455: Primitive Types in Patterns, instanceof, and switch (Preview)](https://openjdk.org/jeps/455)  
[\[2\] Java 23 Has Arrived, And It Brings a Truckload of Changes](https://foojay.io/today/java-23-has-arrived-and-it-brings-a-truckload-of-changes/)  
[\[3\] Practical Design Patterns for Java Developer](https://amzn.eu/d/a3CsBu7)  
[\[4\] Exploring New Features in JDK 23: Factory Pattern with Flexible Constructor Bodies with JEP-482](https://foojay.io/today/exploring-new-features-in-jdk-23-factory-pattern-with-flexible-constructor-bodies-with-jep-482/)  
[\[5\] JEP 286: Local-Variable Type Inference](https://openjdk.org/jeps/286)
