---
title: "Extending Java APIs - Add Missing Features Without the Hassle"
date: "2025-03-07T12:03:57+00:00"
lastmod: "2025-03-07T12:04:00+00:00"
description: "Do you ever pull your hair out in frustration, why isn't this a part of Java? Thanks to Manifold you can solve that problem for everyone."
canonical: "https://debugagent.com/extending-java-apis-add-missing-features-without-the-hassle"
authors:
  - "shai-almog"
image: "thumbnail-20.jpg"
categories:
  - "Tutorials"
related_posts:
  - "logging-best-practices-revisited"
  - "spring-boot-debugging-with-aspect-oriented-programming-aop"
  - "relearning-java-thread-primitives"
frozen: false
---

The Java API is vast. That's great, but sometimes a missing method or capability can be frustrating. With Manifold, developers can solve this problem without having to wait for Java to add a feature in a later version. Even more importantly, Manifold provides many such extensions out of the box, making Java better for developers. In this post, we will discuss the extension capability. It lets us change classes in the Java API in a compatible way without risk. If Java adds these APIs later then we can seamlessly switch to them.

{{< youtube 4wv-wtxa9-g >}}

Before I proceed, the code for this tutorial and the previous tutorials in the series is available [here](https://github.com/shai-almog/java-book/tree/main/Manifold). I will skip setting up as it was covered in the previous installments of the series. You can see the specific pom file settings for this time in [the project directly](https://github.com/shai-almog/java-book/blob/main/Manifold/ManifoldExtensions/pom.xml).

## Extensions

One frequent complaint about Java is verbosity. With Manifold, developers can remove unnecessary code by using a simple trick. For instance, the stream() method in Java is redundant. With Manifold, we can remove that stream method using extensions.

Extensions work similarly to interface default methods. We can define a method that appears to be part of an API, but we write it in a separate class. Manifold finds the extension by looking for packages with a name matching the class name, in the code below you will notice the package name is `com.debugagent.extensions.java.util.Collection`. This package includes the full class name for the `Collection` interface. In this package, we can create extension classes by marking them with the `@Extension` annotation. We can extend any object or interface in this way:

```java
package com.debugagent.extensions.java.util.Collection;

import com.debugagent.extensions.Sizable;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;
@Extension
public class CollectionExt {
    public static <E, R> Stream<R> map( @This Collection<E> t, Function<? super E, R> mapper ) {
        return t.stream().map(mapper);
    }
}
```

The sample code here adds a map method to the `Collection` interface. Notice a few things about the code:

* "This" is passed as the first argument and annotated as `@This`.
* We use the `@Extension` annotation to denote that this is an extension.
* The method itself is static.
* Generics and other language features still work as expected.

Once we include that code in that package we can change this:

```java
List<Integer> numbers = strings.stream().map(Integer::parseInt).toList();
```

To this:

```java
List<Integer> numbers = strings.map(Integer::parseInt).toList();
```

That is a small change but it makes Java a bit less verbose. You might read that code and think this is a lot of code to write in order to save nine characters. You'd be right. We don't need to write that code at all...

## Extension Libraries

Manifold has ready-made extensions for many built-in Java classes. In fact, all the methods of `Stream` are already mapped to `Collection` by Manifold. We can package our extensions as libraries and use the ones from Manifold, they don't collide. E.g. if Manifold extends a class by adding method `x()` and add method `y()` to the same class they will both work as expected.

To understand this and some of the other capabilities around this feature we first need to understand how it works. Manifold can't add a method to an existing Java class. The JVM doesn't allow it. Instead, it does something very simple, it replaces the call to the class with a call to the extension. Since this is done during compile time the call is static and very efficient. There's no inherent runtime overhead.

![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/a6xlwj10os1j8f59xiqm.jpeg)

If during this process Manifold notices that a method with that signature already exists, it uses the actual method. This is important, if we want to use a feature that isn't yet available in the current version of Java, we can mock it with Manifold and then when we finally upgrade the transition would be seamless. We won't even need to remove Manifold!

![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/fjqpna2nwtszbwemp5kh.jpeg)

The extension libraries include too many features to cover here. String is extended with many common methods such as `substringAfterLast(delimiter)`, `removePrefix(String)`, `padStart(length, char)`, etc. Code like this does what you would expect:

```java
var str = "https://debugagent.com";
str = str.removePrefix("https://");
```

One of the great parts about this is that a control-click (or meta-click) navigates to the right code within Manifold. So we can see exactly what goes on under the hood. Manifold includes the following builtin extensions (taken from [GitHub here](https://github.com/manifold-systems/manifold/tree/master/manifold-deps-parent/manifold-ext#structural-interfaces-via-structural)):

**Collections**

Defined in module `manifold-collections` this library extends:

* `java.lang.Iterable`
* `java.util.Collection`
* `java.util.List`
* `java.util.stream.Stream`

**Text**   

Defined in module `manifold-text` this library extends:

* `java.lang.CharSequence`
* `java.lang.String`

**I/O**   

Defined in module `manifold-io` this library extends:

* `java.io.BufferedReader`
* `java.io.File`
* `java.io.InputStream`
* `java.io.OutputStream`
* `java.io.Reader`
* `java.io.Writer`

**Web/JSON**

Defined in module `manifold-json` this library extends:

* `java.net.URL`
* `manifold.rt.api.Bindings`

## Extending Arrays

Arrays in Java are special objects. You can't inherit them or override them. Sure, they are technically an object, but there isn't much we can do. An array in Java has only one attribute and it's pretty limited. With Manifold, any array has many methods that are already built in. Most of them are from the Arrays class and now occupy their rightful place within the object itself.

With Manifold, this is legal code:

```java
String[] arr = strings.toArray(new String[0]);
if(arr.isEmpty()) {
    // ...
}
```

We can extend an array like we can extend any other class. We extend it using the special case class name `manifold.rt.api.Array`. In that class, the methods are the same as the other extension code but since primitive arrays and object arrays are incompatible, the `@This` parameter is always an Object.

## Structural Interfaces

Manifold can add methods to classes and interfaces. But it can't make a class implement an arbitrary interface e.g. I can't make `ArrayList` implement `Runnable`. However, we can do something else. The main limitation is due to the way Manifold works. Since Manifold changes the call at compile time, the method isn't "really" added to the class. As a result it can't generate an interface, only a proxy of sorts. This might seem good on the surface but it creates some compatibility issues as it can impact object identity, etc.

However, we can get around that problem partially using structural interfaces. We can annotate an interface with `@Structural`, and it will work differently than a regular interface.

We can define a structural interface like this:

```java
@Structural
public interface Sizable {
    int size();
}
```

Notice that nothing is special about this interface other than the structural annotation. We can then write a String extension to implement the `size()` method and implement the `Sizable` interface as such:

```java
package com.debugagent.extensions.java.lang.String;

import com.debugagent.extensions.Sizable;
import manifold.ext.rt.api.Extension;
import manifold.ext.rt.api.This;

@Extension
public abstract class StringExt implements Sizable {
    public static int size(@This String str) {
        return str.length();
    }
}
```

This might seem a bit weird at first as the class is abstract. This doesn't matter. Manifold only uses the static methods so it never needs to create an instance of the class. We implement the interface as a hint to Manifold. The abstract keyword is there just so we won't need to implement the interface.

We can make the `CollectionExt` example from before abstract as well and implement Sizable there too. Since it already has the `size()` method this is all it requires. As a result, we can write code like this showing that the interface that we made up can now be used to write polymorphic code utilizing String and `Collection`:

```java
List<String> strings = List.of("1", "2", "3");
var str = "https://debugagent.com";
Sizable s = str;
Sizable v = strings;
s.size();
```

But it doesn't end here. When we create a regular class like this one that doesn't implement the `Sizable` interface:

```java
public class MySizeClass {
    public int size() {
        return 0;
    }
}
```

We can now do this:

```java
Sizable z = new MySizeClass();
```

This effectively makes Java into a structurally typed language. I don't know how I feel about that particular feature, but it sure is an interesting feature.

## Adding Annotations

Amazingly enough, Manifold supports extending a class with custom compile-time annotations. This means we can inject Structural to an interface in the JDK such as Runnable. This will surprisingly work to some degree.

However, since the JDK itself can't be changed, we won't see this impacting code in the JDK. Only our code will be impacted by any such change we make.

## Final Word

Manifold makes it easy to change Java and experiment with new features. Developers can contribute to Project Manifold and help a broader audience see the value of their proposed change. If you ever felt like Java was missing something, Manifold is your way to add that to Java. It's much easier to change something there than in OpenJDK...

I think this is one of the most important features in Manifold, not because it's my favorite. But because it democratizes Java. If you never had the chance to contribute to the Java API then now is a great opportunity. Find a pet peeve in the JDK and fix it!
