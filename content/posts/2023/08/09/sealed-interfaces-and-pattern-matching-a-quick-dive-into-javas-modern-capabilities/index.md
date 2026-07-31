---
title: "Sealed Interfaces & Pattern Matching: Java's Modern Capabilities"
slug: "sealed-interfaces-and-pattern-matching-a-quick-dive-into-javas-modern-capabilities"
date: "2023-08-09T07:01:51+00:00"
lastmod: "2023-08-09T07:08:11+00:00"
description: "Sealed classes and interfaces with pattern matching provide powerful new tools for more explicit, controlled, and flexible design in Java."
authors:
  - "bazlur-rahman"
image: "55a4556f-11e5-4259-8d35-760a02911362.jpeg"
categories:
  - "Java Core"
  - "Tutorials"
tags:
related_posts:
  - "9-outdated-ideas-about-java"
  - "getting-started-with-java-17-and-intellij-idea"
  - "hidden-and-not-so-hidden-gems-in-java-20"
  - "dive-into-the-openjdk-top-10-reads-on-foojay-io"
enlighterjs: true
frozen: false
---

**Sealed classes in Java are a new feature that provides a way to restrict the classes that can inherit from a superclass or extend an interface. This new language feature enhances the encapsulation and provides more control to developers over their codebase.**

In this tutorial, we will explore sealed classes, how to use them to find all subclasses, and how to apply pattern matching in a practical context.

Let's start with a basic interface definition named `Shape`.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public interface Shape {
    double getArea();
}
</pre>

In a conventional scenario, any class can implement `Shape`, and finding all classes that do this is not straightforward. However, with the introduction of sealed classes, we can specify precisely which classes are allowed to implement an interface.

Defining a Sealed Interface {#h2-0-defining-a-sealed-interface}
---------------------------------------------------------------

Here is how you can declare a sealed interface with permitted subclasses:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public sealed interface Shape permits Circle, Rectangle, Square {
    double getArea();
}</pre>

In the example above, we declared `Shape` as a sealed interface and explicitly specified that only `Circle`, `Rectangle`, and `Square` classes can implement `Shape`. This is a powerful feature, as it gives us more control over our class hierarchy and prevents unwanted class implementations.

Finding All Permitted Subclasses {#h2-1-finding-all-permitted-subclasses}
-------------------------------------------------------------------------

With a sealed interface, finding all subclasses or implementors becomes straightforward:

<pre class="EnlighterJSRAW" data-enlighter-language="java">var permittedSubclasses = Shape.class.getPermittedSubclasses();
for (Class&lt;?&gt; subclass : permittedSubclasses) {
    System.out.println("subclass = " + subclass);
}</pre>

The method `getPermittedSubclasses()` returns an array of `Class` objects representing the permitted subclasses of `Shape`. We can then loop through the array and print out all the subclasses.

Pattern Matching with Sealed Classes {#h2-2-pattern-matching-with-sealed-classes}
---------------------------------------------------------------------------------

Pattern matching is another powerful feature in Java that goes hand-in-hand with sealed classes. With pattern matching, we can perform operations based on the type of the object:  

<pre class="EnlighterJSRAW" data-enlighter-language="java">switch (shape){
    case Circle circle -&gt; System.out.println("circle = " + circle);
    case Rectangle rectangle -&gt; System.out.println("rectangle = " + rectangle);
    case Square square -&gt; System.out.println("square = " + square);
}</pre>

In the example above, we are checking the type of shape object, and depending on the type, different operations are performed. Note that since we're using a sealed interface, we know exactly which classes could be the type of shape, so we can handle them all explicitly.

**In conclusion, sealed classes and interfaces, together with pattern matching, provide powerful new tools for more explicit, controlled, and flexible design in Java.**
