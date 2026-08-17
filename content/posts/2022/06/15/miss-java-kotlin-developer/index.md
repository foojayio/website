---
title: "What I Miss in Java, the Perspective of a Kotlin Developer"
slug: "miss-java-kotlin-developer"
date: "2022-06-15T08:15:34+00:00"
lastmod: "2022-06-15T08:17:07+00:00"
description: "I miss some features in Java that would improve my code's readability, expressiveness, and maintainability."
canonical: "https://blog.frankel.ch/miss-in-java-kotlin-developer/"
authors:
  - "nicolas-frankel"
image: "cat-g641127bfa.jpg"
categories:
  - "Java Core"
  - "Kotlin"
tags:
related_posts:
  - "chopping-monolith"
  - "blockhound-how-it-works"
  - "how-to-beautify-your-github-repo"
enlighterjs: true
frozen: false
---

Java has been my bread and butter for almost two decades.

Several years ago, I started to learn Kotlin; I never regretted it.

Though Kotlin compiles to JVM bytecode, I sometimes have to write Java again.

Every time I do, I cannot stop pondering why my code doesn't look as nice as in Kotlin.

I miss some features that would improve my code's readability, expressiveness, and maintainability.

This article is not meant to bash Java but to list some features I'd like to find in Java.

Immutable references {#h2-0-immutable-references}
-------------------------------------------------

Java has immutable references since the beginning:

* For attributes in classes
* For parameters in methods
* For local variables

```java
class Foo {

    final Object bar = new Object();      // 1

    void baz(final Object qux) {          // 2
        final var corge = new Object();   // 3
    }
}
```


1. Cannot reassign `bar`
2. Cannot reassign `qux`
3. Cannot reassign `corge`

Immutable references are a great help in avoiding nasty bugs. Interestingly, using the `final` keyword is not widespread, even in widely used projects. For example, Spring's [`GenericBean`](https://github.com/spring-projects/spring-framework/blob/main/spring-web/src/main/java/org/springframework/web/filter/GenericFilterBean.java) uses immutable attributes, but neither immutable method parameters nor local variables; slf4j's [`DefaultLoggingEventBuilder`](https://github.com/qos-ch/slf4j/blob/master/slf4j-api/src/main/java/org/slf4j/spi/DefaultLoggingEventBuilder.java) uses none of the three.

While Java allows you to define immutable references, it's not mandatory. By default, references are mutable. Most Java code doesn't take advantage of immutable references.

Kotlin doesn't leave you a choice: every property and local variable needs to be defined as either a `val` or a `var`. Plus, one cannot reassign method parameters.

The `var` Java keyword is quite different. First, it's only available for local variables. More importantly, it doesn't offer its immutable counterpart, `val`. You still need to add the `static` keyword, which nearly nobody uses.

Null safety {#h2-1-null-safety}
-------------------------------

In Java, there's no way to know whether a variable is `null`. To be explicit, Java 8 introduced the [`Optional`](https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html) type. From Java 8 onward, returning an `Optional` implies the underlying value can be `null`; returning another type implies it cannot.

However, the developers of `Optional` designed it for return values only. Nothing is available in the language syntax for methods parameters and return values. To cope with this, a bunch of libraries provides compile-time annotations:

|                                                                        Project                                                                        |                   Package                    | Non-null annotation | Nullable annotation |
|-------------------------------------------------------------------------------------------------------------------------------------------------------|----------------------------------------------|---------------------|---------------------|
| [JSR 305](https://github.com/amaembo/jsr-305/tree/master/ri/src/main/java/javax/annotation)                                                           | `javax.annotation`                           | `@Nonnull`          | `@Nullable`         |
| [Spring](https://github.com/spring-projects/spring-framework/tree/main/spring-core/src/main/java/org/springframework/lang)                            | `org.springframework.lang`                   | `@NonNull`          | `@Nullable`         |
| [JetBrains](https://github.com/JetBrains/java-annotations/tree/master/common/src/main/java/org/jetbrains/annotations)                                 | `org.jetbrains.annotations`                  | `@NotNull`          | `@Nullable`         |
| [Findbugs](https://github.com/stephenc/findbugs-annotations/tree/master/src/main/java/edu/umd/cs/findbugs/annotations)                                | `edu.umd.cs.findbugs.annotations`            | `@NonNull`          | `@Nullable`         |
| [Eclipse](https://github.com/eclipse/aspectj.eclipse.jdt.core/tree/main/org.eclipse.jdt.annotation/src/org/eclipse/jdt/annotation)                    | `org.eclipse.jdt.annotation`                 | `@NonNull`          | `@Nullable`         |
| [Checker framework](https://github.com/typetools/checker-framework/tree/master/checker-qual/src/main/java/org/checkerframework/checker/nullness/qual) | `org.checkerframework.checker.nullness.qual` | `@NonNull`          | `@Nullable`         |

Obviously, some libraries are focused on specific s. Moreover, libraries are hardly compatible with one another. So many libraries are available that somebody on StackOverflow asked which one to use. The [resulting activity](https://stackoverflow.com/questions/4963300/which-notnull-java-annotation-should-i-use) is telling.

Finally, using a nullability library is opt-in. On the other side, Kotlin requires every type to be either nullable or non-nullable.

```kotlin
val nonNullable: String = computeNonNullableString()
val nullable: String? = computeNullableString()
```


Extension functions {#h2-2-extension-functions}
-----------------------------------------------

In Java, one extends a class by subclassing it:

```java
class Foo {}
class Bar extends Bar {}
```


Subclassing has two main issues. The first issue is that some classes don't allow it: they are marked with the `final` keyword. A couple of widely-used JDK classes are `final`, *e.g.* , `String`. The second issue is that if a method outside our control returns a type, one is stuck with that type, whether it contains the wanted behavior or not.

To work around the above issues, Java developers have invented the concept of utility classes, usually named `XYZUtils` for type `XYZ`. A utility class is a bunch of `static` methods with a `private` constructor, so it cannot be instantiated. It's a glorified namespace because Java doesn't allow methods outside classes.

This way, if a method doesn't exist in a type, the utility class can provide a method that takes the type as a parameter and execute the required behavior.

```java
class StringUtils {                                          // 1

    private StringUtils() {}                                 // 2

    static String capitalize(String string) {                // 3
        return string.substring(0, 1).toUpperCase()
            + string.substring(1);                           // 4
    }
}

String string = randomString();                              // 5
String capitalizedString = StringUtils.capitalize(string);   // 6
```


1. Utility class
2. Prevent instantiation of new objects of this type
3. `static` method
4. Simple capitalization that doesn't account for corner cases
5. The `String` type doesn't offer a capitalization functionality
6. Use an utility class to factor this behavior

Note that earlier, developers created such classes inside the project. Nowadays, the ecosystem offers Open Source libraries such as [Apache Commons Lang](https://commons.apache.org/proper/commons-lang/) or [Guava](https://github.com/google/guava). Don't reinvent the wheel!

Kotlin provides [extension functions](https://kotlinlang.org/docs/extensions.html#extension-functions) to solve the same issue.
> Kotlin provides the ability to extend a class or an interface with new functionality without having to inherit from the class or use design patterns such as *Decorator* . We can achieve it via special declarations called *extensions* . For example, you can write new functions for a class or an interface from a third-party library that you can't modify. Such functions can be called in the usual way as if they were methods of the original class. This mechanism is called an *extension function* . To declare an extension function, prefix its name with a *receiver* type, which refers to the type being extended.

With extension functions, one can rewrite the above code as:

```kotlin
fun String.capitalize2(): String {                           // 1-2
    return substring(0, 1).uppercase() + substring(1);
}

val string = randomString()
val capitalizedString = string.capitalize2()                 // 3
```


1. Free-floating function, no need for a class wrapper
2. `capitalize()` already exists in Kotlin's stdlib
3. Call the extension function as if it belonged to the `String` type

Note that extension functions are resolved "statically". They don't really attach new behavior to the existing type; they pretend to do so. The generated bytecode is very similar (if not the same) to one of Java static methods. However, the syntax is much clearer and allows for function chaining, which is impossible with Java's approach.

Reified generics {#h2-3-reified-generics}
-----------------------------------------

Version 5 of Java brought generics. However, the language designers were keen on preserving backward compatibility: Java 5 *bytecode* was required to interact flawlessly with pre-Java 5 *bytecode* . That's why generic types are not written in the generated *bytecode* : it's known as *type erasure* . The opposite is reified generics, where generic types would be written in the *bytecode*.

Generic types being only a compile-time concern creates a couple of issues. For example, the following method signatures produce the same *bytecode*, and thus, the code is not valid:

```java
class Bag {
    int compute(List<Foo> persons) {}
    int compute(List<Bar> persons) {}
}
```


Another issue is how to get a typed value out of a container of values.  

Here's a sample from Spring:

```java
public interface BeanFactory {
    <T> T getBean(Class<T> requiredType);
}
```


Developers added a `Class` parameter to be able to know the type in the method body. If Java had reified generics, it wouldn't be necessary:

```java
public interface BeanFactory {
    <T> T getBean();
}
```


Imagine if Kotlin had reified generics. We could change the above design:

```kotlin
interface BeanFactory {
    fun <T> getBean(): T
}
```


And to call the function:

```kotlin
val factory = getBeanFactory()
val anyBean = getBean<Any>()               // 1
```


1. Reified generics!

Kotlin still needs to comply with the JVM specification and be compatible with *bytecode* generated by the Java compiler. It can work via a trick called inlining: the compiler replaces the inlined method calls by the function body.

Here's the Kotlin code to make it work:

```kotlin
inline fun <reified T : Any> BeanFactory.getBean(): T = getBean(T::class.java)
```

Conclusion {#h2-5-conclusion}
-----------------------------

I've described four Kotlin features that I miss in Java in this post: immutable references, null safety, extension functions, and reified generics.

While Kotlin offers other great features, these four are enough to make the bulk of improvements over Java.

For example, with extension functions and reified generics plus a bit of syntactic sugar, one can easily design s, such as the Kotlin Routes and Beans DSL:

```kotlin
beans {
  bean {
    router {
      GET("/hello") { ServerResponse.ok().body("Hello world!") }
    }
  }
}
```


Make no mistake: I understand that Java has much more inertia to improve as a language, while Kotlin is inherently more nimble.

However, competition is good, and both can learn from each other.

In the meantime, I'll only write Java when I have to, as Kotlin has become my language of choice on the JVM.

**To go further:**

* [Variables](https://kotlinlang.org/docs/basic-syntax.html#variables)
* [Null safety](https://kotlinlang.org/docs/null-safety.html)
* [Extension functions](https://kotlinlang.org/docs/extensions.html#extension-functions)
* [Reified type parameters](https://kotlinlang.org/docs/inline-functions.html#reified-type-parameters)

*Originally published at [A Java Geek](https://blog.frankel.ch/miss-in-java-kotlin-developer/) on June 12^th^, 2022*

*[IDE]: Integrated Development Environment
*[DSL]: Domain-Specific Languages
