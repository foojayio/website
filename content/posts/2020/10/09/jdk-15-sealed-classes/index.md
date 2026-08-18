---
title: "JDK 15 Sealed Classes: Fine-Grained Mechanism for Inheritance"
date: "2020-10-09T10:43:54+00:00"
lastmod: "2021-07-12T05:09:33+00:00"
description: "The most significant new feature in JDK 15, and the only change to the language, is the introduction of sealed classes as a preview feature."
canonical: "https://www.azul.com/jdk-15-release-64-new-features-and-apis/"
authors:
  - "simonritter"
image: "Favicon-3-2.png"
categories:
  - "Sealed Classes"
tags:
related_posts:
frozen: false
---

The most significant new preview feature in JDK 15 (with its second preview in JDK 16), and the only change to the language, is the introduction of sealed classes as a preview feature.

#### Preview Features

[Preview features](https://openjdk.java.net/jeps/12) first appeared in JDK 12 and allowed for new functionality to be included in the JDK without adding it to the Java SE specification (this applies to the Java language, Java Virtual Machine, and Java SE APIs).

By doing this, feedback from users can be gathered and changes made where necessary or even, if deemed appropriate, the feature could be deleted. To use a preview feature, it is essential to enable them both at compile time and runtime using the `--enable-preview`flag (the `--release` flag must also be used at compile time).

#### Inheritance and the Final Modifier

Java is an object-oriented language, one of the main features of which is inheritance. The idea is straightforward: common state and behaviour can be abstracted to a super-class and shared (through inheritance) by sub-classes. This eliminates the need to reproduce the instance variables or methods of the super-class in each sub-class.

Prior to JDK 15, the only control a developer had over inheritance was the final modifier. This prevents *any* class from extending the one using the modifier. The only other way to control inheritance is to make a class or its constructors package-private, limiting possible sub-classes to those in the same package. This approach is sometimes too coarse, which is where sealed classes come in.

#### Fine-Grained Mechanism for Inheritance

Sealed classes (explained in detail in [JEP 360](https://openjdk.java.net/jeps/360)) provide a fine-grained mechanism that allows a developer to restrict which other classes or interfaces may extend them. You can think of final classes as the ultimate sealed class since no other classes can extend them.

The syntax for sealed classes extends the existing way of defining a class by adding the sealed and permits *reserved identifiers*. This is important because they are not new keywords so they can still be used as variable names and not break existing code.

A sealed class can be defined like this:

```java
public sealed class Foo permits A, B, C { ... }
```

Only classes A, B, and C can extend class Foo, and they must be in the same package or module. The permits part of the definition can be omitted if A, B, and C are in the same compilation unit (typically file).

Each permitted sub-class must have its inheritance capabilities explicitly specified. In our simple example:

* Class A could be defined as final so that no further inheritance is allowed.
* Class B could be defined as sealed, permitting a closed set of classes to inherit from it in the same ways as Foo does.
* Class C could be defined as non-sealed, which reverts it to be open and allows any class to inherit from it. As non-sealed contains a hyphen, it has been made a reserved word in Java (the first with a hyphen). Variable names may not include a hyphen, so there is no impact on backwards compatibility.

```java
public non-sealed class C { ... }
```

The Java reflection system has been updated to include sealed classes. The java.lang.Class class has two new methods, isSealed() and permittedSubclasses (which returns an array of ClassDesc objects).

#### Conclusion

Although the JEP title is Sealed classes, this feature can be used for the inheritance of interfaces as well.

Sealed classes are part of [Project Amber](https://openjdk.java.net/projects/amber/), the goal of which is to explore and incubate smaller, productivity-oriented Java language features.

They will also help another feature under consideration for inclusion in Java: pattern matching for switch. With sealed classes, it will be possible to eliminate the need for a default case and enable the compiler to perform more extensive checks.

*Used with permission and thanks, [originally written as part of a longer article by Simon Ritter](https://www.azul.com/jdk-15-release-64-new-features-and-apis/).*
