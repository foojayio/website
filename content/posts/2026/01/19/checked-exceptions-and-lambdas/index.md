---
title: "Checked exceptions and lambdas"
date: "2026-01-19T09:00:29+00:00"
lastmod: "2026-09-21T05:59:21+00:00"
description: "Java's checked exceptions were a massive improvement over C's error-handling mechanism. As time passed and experience accumulated, we collectively…"
canonical: "https://blog.frankel.ch/checked-exceptions-lambdas/"
authors:
  - "nicolas-frankel"
image: "cover_large-2.jpg"
categories:
  - "Java"
  - "Java Core"
related_posts:
  - "feedback-on-checked-exceptions-and-lambdas"
  - "run-a-java-lambda-function-from-a-docker-image"
  - "unusual-java-stacktrace-extends-throwable"
  - "top-10-java-language-features"
frozen: false
---

Java's checked exceptions were a massive improvement over C's error-handling mechanism. As time passed and experience accumulated, we collectively concluded that we weren't there yet. However, Java's focus on stability has kept checked exceptions in its existing API.

Java 8 brought lambdas after the "checked exceptions are great" trend. None of the functional interface methods accepts a checked exception. In this post, I will demonstrate three different approaches to making your legacy exception-throwing code compatible with lambdas.

## The problem, in code

Consider a simple exception-throwing method.

```java
public class Foo {
    public String throwing(String input) throws IOException {
        return input;                          //1
    }
}
```

1. The body is there for compilation purposes; its exact content is irrelevant

The method accepts a `String` and returns a `String`. It has the shape of a `Function<I>`, so we can use it as such:

```java
var foo = new Foo();
List.of("One", "Two").stream()
    .map(string -> foo.throwing(string))
    .toList();
```

The above code fails with a compilation error:

```
unreported exception IOException; must be caught or declared to be thrown
            .map(string -> foo.throwing(string)).toList();
                                       ^
1 error
```

To fix the error, we need to wrap the throwing code in a try-catch block:

```java
List.of("One", "Two").stream()
    .map(string -> {
        try {
            return foo.throwing(string);
        } catch (IOException e) {
            return "";
        }
    }).toList();
```

At this point, the code compiles, but defeats the main purpose of lambdas: being concise and readable.

## A better approach

We can definitely improve the design by modeling a `Function` with a throwing `apply()`.

```java
interface ThrowingFunction<I, O, E extends Exception> {
    O apply(I i) throws E;
}
```

We can then provide a wrapper to transform such a throwing `Function` into a regular `Function`.

```java
class LambdaUtils {
    public static <I, O, E extends Exception> Function<I, O> safeApply(ThrowingFunction<I, O, E> f) {
        return input -> {
            try {
                return f.apply(input);
            } catch (Exception e) {
                return "";
            }
        };
    }
}
```

With the above, the calling code can be improved like this:

```java
var foo = new Foo();
List.of("One", "Two").stream()
    .map(string -> LambdaUtils.safeApply(foo::throwing))     //1
    .toList();
```

1. Concise code again

## Libraries to the rescue

The most straightforward way to call exception-throwing code in a lambda involves using a library. Two libraries that I know of provide this capability:

* [Apache Commons Lang 3](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/function/package-summary.html)
* [Vavr](https://docs.vavr.io/#_functions)

Here's how we can rewrite the above code using Commons Lang 3 code:

```java
var foo = new Foo();
FailableFunction<String, String, IOException> throwingFunction = foo::throwing; //1
List.of("One", "Two").stream()
    .map(throwingFunction)
    .recover(e -> "")                                                           //2
    .toList();
```

1. Commons Lang 3 models a throwing `Function`
2. `recover()` mimics the value set in the previous `catch` block

The libraries improve upon my debatable design above, but the idea stays the same.

The decision to roll out your own or use a library depends on a variety of factors that go beyond this post. Here are [some criteria](https://blog.frankel.ch/choosing-dependency/) to help you.

## Suppressing checked exceptions

Checked exceptions are a compile-time concern. The Java Language Specification states:
> A compiler for the Java programming language checks, at compile time, that a program contains handlers for *checked exceptions* , by analyzing which checked exceptions can result from execution of a method or constructor. For each checked exception which is a possible result, the `throws` clause for the method (§8.4.6) or constructor (§8.8.5) must mention the class of that exception or one of the superclasses of the class of that exception. This compile-time checking for the presence of exception handlers is designed to reduce the number of exceptions which are not properly handled.
>
> -- [11.2 Compile-Time Checking of Exceptions](https://docs.oracle.com/javase/specs/jls/se6/html/exceptions.html#44121)

We could potentially hook into the compiler to prevent this check via a compiler plugin. Or find a library that does. That's when Manifold enters the scene.
> Manifold is a Java compiler plugin. Use it to supplement your Java projects with highly productive features.
>
> Powerful language enhancements improve developer productivity.
>
> * Extension methods
> * True delegation
> * Properties
> * Optional parameters (New!)
> * Tuple expressions
> * Operator overloading
> * Unit expressions
> * A Java template engine
> * A preprocessor
> * ...and more
>
> -- [What is Manifold](http://manifold.systems/)

Disclaimer: I don't advocate for using Manifold. It makes the language you work with different from Java. At this point, you'd be better off using Kotlin directly.

Using Manifold to suppress checked exceptions is a two-step process. First, we add the Manifold runtime to the project:

```java
<dependency>
    <groupId>systems.manifold</groupId>
    <artifactId>manifold-rt</artifactId>
    <version>${manifold.version}</version>
    <scope>provided</scope>
</dependency>
```

Then, we configure the compiler plugin:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.0</version>
            <configuration>
                <source>11</source>
                <target>11</target>
                <encoding>UTF-8</encoding>
                <compilerArgs>
                    <arg>-Xplugin:Manifold</arg>
                </compilerArgs>
                <annotationProcessorPaths>
                    <path>
                        <groupId>systems.manifold</groupId>
                        <artifactId>manifold-exceptions</artifactId>
                        <version>${manifold.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

At this point, we can treat checked exceptions as unchecked.

```java
var foo = new Foo();
List.of("One", "Two").stream()
        .map(string -> foo.throwing(string))       //1
        .toList();
```

1. Compile with no issue

## Conclusion

In this post, I tackled the issue of integrating checked exceptions with lambdas in Java. I listed several options: the try-catch block, the throwing function, the library, and Manifold. I hope you can find one that suits your context among them.

**To go further:**

* [Apache Commons Lang 3](https://commons.apache.org/proper/commons-lang/apidocs/org/apache/commons/lang3/function/package-summary.html)
* [Vavr](https://docs.vavr.io/#_functions)
* [Exceptions in Java Lambda Expressions](https://www.baeldung.com/java-lambda-exceptions)
* [Exceptions in Lambda Expression Using Vavr](https://www.baeldung.com/exceptions-using-vavr)
* [Say Goodbye to Checked Exceptions](http://manifold.systems/articles/unchecked.html)
* [Revisiting Resolving the Scourge of Java's Checked Exceptions](https://substack.com/home/post/p-181624168)

*Originally published at [A Java Geek](https://blog.frankel.ch/checked-exceptions-lambdas/) on January 18th, 2026*
