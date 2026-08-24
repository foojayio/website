---
title: "Java: Functional Programming f(x) – Part2"
date: "2024-06-03T17:05:56+00:00"
lastmod: "2024-06-03T17:06:34+00:00"
description: "Dive into the world of functional programming in Java using Lambda Expressions, Method References, and Functional Interfaces."
authors:
  - "mahendra1413"
image: "FunctionalProgramming-700x394-2.png"
categories:
  - "Java"
  - "Java Beginner"
  - "Java Core"
  - "Kotlin"
  - "Tutorials"
related_posts:
  - "java-functional-programming"
  - "spring-internals-of-restclient"
  - "openrewrite-migrate-to-spring-boot-3-2"
frozen: false
---

In [the preceding article](https://foojay.io/today/java-functional-programming/), we explored the significance of **Functional Programming, Lambda Calculus**, and other related concepts.

In this article, we delve deeper into the essential aspects of Functional Programming, such as…​

**1. Lambda Expressions
2. Method References
3. Functional Interfaces**

And we will discuss about the relationship between each of the features.

JSR 335 has greatly facilitated programming in a multicore environment by introducing Lambda Expressions/closures/anonymous method, and related features.

If Lambda Expressions had been integrated into the Collections API from the beginning, their development could have taken a different path. However, they have enriched the Collections API by introducing new methods to current interfaces and introducing new Interfaces such as **'Stream'**.

## Why Lambda Expressions?

Before the introduction of**Lambda Expressions**, developers commonly used ubiquitous anonymous inner classes, which were more verbose and overwhelming.

However, Lambda expressions are simplified, made more concise, and made it elegant and concise way to write a block of code that was previously written in an anonymous class.

So,

## What is Lambda Expression?

According to Oracle documentation, **a lambda expression is just a shorter way of writing an implementation of a method for later execution.**

At compile time, the lambda expression determines its type (variable, field, method, or return type of a method) and it must be a functional interface. And you cannot write a lambda expression for an anonymous class that does not implement a functional interface.

After we determine the lambda expression's type from the functional interface class, we can conveniently locate the appropriate method, namely the abstract method, for the implementation. As a result, the lambda expression only executes the abstract method from the functional interface.

Composing a lambda expression involves three components:

```java
() -> { }
```

1. A block of parameters
2. An Arrow and Java uses meager arrows(-\>)
3. Providing a block of code that serves as the method's body

Nevertheless, we can streamline the syntax from `() -> {}` to `argument -> block of code` which gives more concise, simple, and readable. We can omit `()` if you have one argument.

**For example** , for the below list, we can convert the **anonymous inner class** syntax which takes too many lines to express the basic concept.

```java
List names = List.of("Foojay", "Java", "Steve", "Mahi");

Collections.sort(names, new Comparator() {
  @Override
  public int compare(String str1, String str2) {
      return str1.compareTo(str2);
  }
});
```

Using the **lamda expression**, we can simplify the above code snippet like the below,

```java
List names = List.of("Foojay", "Java", "Steve", "Mahi");

names.sort((str1, str2) -> str1.compareTo(str2));
```

The provided code snippet describes the lambda expression and sorting method used for the list of names as follows:

**Type of Lambda Expression:**

The lambda expression `(str1, str2) -> str1.compareTo(str2)` serves as a Comparator lambda expression. It defines the comparison logic used for sorting the elements in the list.  

This lambda expression is of type `Comparator`, which is a functional interface in the `java.util` package.

**Method Used for Implementation:**

The list is sorted using the method `List.sort(Comparator c)`. This method takes a Comparator as an argument to establish the order of the elements.

Finally, using Lambda expressions allows you to invoke any method specified in the interface.

This means that **invoking an abstract method will execute the code within the lambda expression itself, as the lambda acts as an implementation of the method.**

In contrast, **invoking a default method will execute the code defined in the interface and cannot be changed by the lambda expression.**

And, Lambdas cannot modify local variables that are defined outside of their body, but they can read them as long as they are final, meaning immutable.

This concept of accessing variables is known as **capturing** : `lambdas have the ability to capture values, not variables`. A `final` variable essentially represents a value.

## 2. Method References

Typically, developers utilize Lambda Expressions for writing business logic within parentheses or for invoking custom defined methods. On the other hand, Method References can serve as replacements for lambda expressions when there is an existing method available.

Using Method References can make your code more concise and often improve its readability compare to Lambda Expressions.

A lambda expression can be substituted with a method reference in the following situations:

### 1. Static Method References

Suppose you have the following Lambda Expression code:

```java
List<String> names = List.of("Foojay", "Java", "Steve", "Mahi");
names.sort((str1, str2) -> str1.compareTo(str2));
```

The above code can be rewritten as

```java
List<String> names = List.of("Foojay", "Java", "Steve", "Mahi");
names.sort(String::compareTo);
```

In this instance, the `compareTo` method within the String class serves as a Method Reference for `String::compareTo`, effectively substituting the lambda expression `(str1, str2) -> str1.compareTo(str2)`.

### 2. Instance Methods of a Particular Object or Unbounded Method Reference

The general syntax of instance methods of a particular object:

Lambda Expression: `(args) -> instance.method(args)`

Method Reference: `instance::method`

When you demonstrate a suitable example of a specific object's instance method, you should think about an instance of a custom comparator class.

**Create custom comparator class**

```java
public class CustomComparator {
    public int compareStrings(String str1, String str2) {
        return str1.compareToIgnoreCase(str2);
    }
}
```

**Lambda Expression Code**

```java
List<String> names = List.of("Foojay", "Java", "Steve", "Mahi");
var comparator = new CustomComparator();
names.sort((str1, str2) -> comparator.compareStrings(str1, str2));
```

**Using Method References**

```java
List<String> names = List.of("Foojay", "Java", "Steve", "Mahi");
var comparator = new CustomComparator();
names.sort(comparator::compareStrings);
```

The method reference `comparator::compareStrings` refers to the compareStrings method of the comparator object, replacing the lambda expression `(str1, str2) -> comparator.compareStrings(str1, str2`. This substitution enhances the code's conciseness and readability.

### 3. Instance Methods of a Particular Object or Bounded Method References

The general syntax for a bound method reference is `expression:instanceMethod`. In this syntax, `expression` represents an expression that yields an object, and `instanceMethod` denotes the name of an instance method.

Let's consider the following example

**Lambda Expression**

```java
Consumer<String> result = (s) -> System.out.println(s);
```

**Using Bounded Method References**

```java
Consumer<String> printer = System.out::println;
```

### 4. Constructor Method References

This is pretty straigthforward, for example

**Lambda Expression code** : `(args) -> new ClassName(args)`

```java
Supplier<List<String>> supplier = () -> new ArrayList<>();
```

**Constructor Method References** : `ClassName::new`

```java
Supplier<List<String>> supplier = ArrayList::new;
```

## 3. Functional Interfaces

A **functional interface** is an interface that has only **Single Abstract Method (SAM)**.

Within the JDK, the `java.util.function` package includes a wide array of functional interfaces.

The JDK API extensively uses these functional interfaces, particularly in the **Collections Frameworks and the Stream API**.

The functional interfaces can be categorized as follows:

**Supplier interface** : *The supplier function does not accept any parameters and yields an object.*

This interface is extremely straightforward: it solely consists of a `get()` method and does not include any default or static methods. The interface is presented below.

```java
@FunctionalInterface
public interface Supplier<T> {
     T get();
 }
```

The subsequent lambda implements the aforementioned interface.

```java
Supplier<String> supplier = () -> "Hello Foojay!";`
```

The JDK comes with four of specialized suppliers which will avoid unneccessary boxing / unboxing

* IntSupplier
* BooleanSupplier
* LongSupplier
* DoubleSupplier

**Consumer interface** : *The consumer does the opposite of the supplier: it takes an argument and does not return anything.*

It does contain one abstract method, and many default methods

```java
@FunctionalInterface
public interface Consumer<T> {

     void accept(T t);
    // default methods ....
}
```

The subsequent lambda implements the aforementioned interface.

```java
Consumer<String> printer = str -> System.out.println(str);
```

There are specialized versions of `BiConsumer` interface which handles the primitive types

* ObjIntConsumer
* ObjLongConsumer
* ObjDoubleConsumer

**Predicate interface** : *A predicate is utilized to examine an object, serving as a filter for streams within the Stream API.*

It contains an abstract method which takes an object and returns a boolean value.

```java
@FunctionalInterface
public interface Predicate<T> {

     boolean test(T t);
     // default and static methods
}
```

The subsequent lambda implements the aforementioned interface.

```java
Predicate<String> lengthCheck = s -> s.length() == 9;
```

It also contains specialized versions of interfaces

* IntPredicate
* LongPredicate
* DoublePredicate

**Function interface**:

The abstract function's method takes an object of type T as input and produces a transformation of that object to a different type U. Furthermore, this interface contains default and static methods.

```java
@FunctionalInterface
public interface Function<T, R> {

    R apply(U u);

    // default and static methods
}
```

Example

```java
// Function that converts a String to its length
 Function<String, Integer> stringLength = String::length;

// Example string
String example = "Hello, Foojay Friends!";

// Applying the function to get the length
int length = stringLength.apply(example);

// Printing the result
System.out.println("The length of the string is: " + length);
```

## Key Benefits

* Improving Readability: Reducing boilerplate code enhances the clarity of the logic.
* Increasing Efficiency: Writing less code accelerates the development process.
* Enhancing Maintainability: Concise code facilitates easier debugging and maintenance.
* Optimizing Parallel Processing: It seamlessly integrates with parallel processing frameworks such as the Stream API.

Understanding and learning, **Lambda Expressions, Method References, and Functional Interfaces** can enhance the efficiency and readability of your Java code.

**Lambda Expressions** help simplify code by eliminating the need for verbose anonymous inner classes, particularly beneficial in enhancing the clarity of the Collections framework.

**Method References** enhance both readability and maintainability by providing a more concise syntax that directly points to pre-existing methods.

**Functional Interfaces** have only one abstract method, which allows for the use of lambda expressions and method references. Important functional interfaces include Function, Consumer, Supplier, and Predicate.

In the next article, we will discuss about **StreamAPI** extensively.

Thanks for reading.

Happy Learning!
