---
title: "Operator Overloading in Java"
date: "2023-06-10T12:19:26+00:00"
lastmod: "2023-06-10T12:19:28+00:00"
description: "Write expressions like (bigDecimalMap[ObjKey] * 5 > 20) in Java. Manifold makes that happen. Expressions like \"var distance = 5 mph * 3 hr\"!"
canonical: "https://debugagent.com/operator-overloading-in-java"
authors:
  - "shai-almog"
image: "thumbnail-21.jpg"
categories:
  - "Java Core"
  - "Tutorials"
  - "Videos"
tags:
related_posts:
  - "revolutionize-json-parsing-in-java-with-manifold"
  - "spring-boot-debugging-with-aspect-oriented-programming-aop"
  - "relearning-java-thread-primitives"
frozen: false
---

In this article, we'll delve into the fascinating world of operator overloading in Java.

Although Java doesn't natively support operator overloading, we'll discover how Manifold can extend Java with that functionality.

We'll explore its benefits, limitations, and use cases, particularly in scientific and mathematical code.

We will also explore three powerful features provided by Manifold that enhance the default Java type safety while enabling impressive programming techniques.

We'll discuss unit expressions, type-safe reflection coding, and fixing methods like equals during compilation.

Additionally, we'll touch upon a solution that Manifold offers to address some limitations of the `var` keyword. Let's dive in!

{{< youtube pwQs-308OdY >}}

Before we begin as always, you can find the code examples for this post and other videos in this series on my [GitHub page](https://github.com/shai-almog/java-book/). Be sure to check out the project, give it a star, and follow me on GitHub to stay updated!

## Arithmetic Operators

Operator overloading allows us to use familiar mathematical notation in code, making it more expressive and intuitive. While Java doesn't support operator overloading by default, Manifold provides a solution to this limitation.

To demonstrate, let's start with a simple `Vector` class that performs vector arithmetic operations. In standard Java code, we define variables, accept them in the constructor, and implement methods like `plus` for vector addition. However, this approach can be verbose and less readable.

```
public class Vec {
   private float x, y, z;

   public Vec(float x, float y, float z) {
       this.x = x;
       this.y = y;
       this.z = z;
   }

   public Vec plus(Vec other) {
       return new Vec(x + other.x, y + other.y, z + other.z);
   }
}
```

With Manifold, we can simplify the code significantly. Using Manifold's operator overloading features, we can directly add vectors together using the `+` operator as such:

```
Vec vec1 = new Vec(1, 2, 3);
Vec vec2 = new Vec(1, 1, 1);
Vec vec3 = vec1 + vec2;
```

Manifold seamlessly maps the operator to the appropriate method invocation, making the code cleaner and more concise. This fluid syntax resembles mathematical notation, enhancing code readability.

Moreover, Manifold handles reverse notation gracefully. If we reverse the order of the operands, such as a scalar plus a vector, Manifold swaps the order and performs the operation correctly. This flexibility enables us to write code in a more natural and intuitive manner.

Let's say we add this to the Vec class:

```
public Vec plus(float other) {
    return new Vec(x + other, y + other, z + other);
}
```

This will make all these lines valid:

```
vec3 += 5.0f;
vec3 = 5.0f + vec3;
vec3 = vec3 + 5.0f;
vec3 += Float.valueOf(5.0f);
```

In this code, we demonstrate that Manifold can swap the order to invoke `Vec.plus(float)` seamlessly. We also show that the plus equals operator support is built into the plus method support

As implied by the previous code Manifold also supports primitive wrapper objects specifically in the context of autoboxing. In Java, primitive types have corresponding wrapper objects. Manifold handles the conversion between primitives and their wrapper objects seamlessly, thanks to autoboxing and unboxing. This enables us to work with objects and primitives interchangeably in our code. There are caveats to this as we will find out.

### BigDecimal Support

Manifold goes beyond simple arithmetic and supports more complex scenarios. For example, the `manifold-science` dependency includes built-in support for `BigDecimal` arithmetic. `BigDecimal` is a Java class used for precise calculations involving large numbers or financial computations. By using Manifold, we can perform arithmetic operations with BigDecimal objects using familiar operators, such as `+`, `-`, `*`, and `/`. Manifold's integration with `BigDecimal` simplifies code and ensures accurate calculations.

The following code is legal once we add the right set of dependencies which add method extensions to the `BigDecimal` class:

```
var x = new BigDecimal(5L);
var y = new BigDecimal(25L);
var z = x + y;
```

Under the hood, Manifold adds the applicable plus, minus, times, etc. methods to the class. It does so by leveraging class extensions which [I discussed before](https://debugagent.com/extending-java-apis-add-missing-features-without-the-hassle).

### Limits of Boxing

We can also extend existing classes to support operator overloading. Manifold allows us to extend classes and add methods that accept custom types or perform specific operations.

For instance, we can extend the `Integer` class and add a `plus` method that accepts BigDecimal as an argument and returns a `BigDecimal` result.

This extension enables us to perform arithmetic operations between different types seamlessly. The goal is to get this code to compile:

```
var z = 5 + x + y;
```

Unfortunately, this won't compile with that change. The number five is a primitive, not an Integer and the only way to get that code to work would be:

```
var z = Integer.valueOf(5) + x + y;
```

This isn't what we want. However, there's a simple solution. We can create an extension to `BigDecimal` itself and rely on the fact that the order can be swapped seamlessly. This means that this simple extension can support the `5 + x + y` expression without a change:

```
@Extension
public class BigDecimalExt {
    public static BigDecimal plus(@This BigDecimal b, int i) {
        return b.plus(BigDecimal.valueOf(i));
    }
}
```

### List of Arithmetic Operators

So far we focused on the plus operator but Manifold supports a wide range of operators. The following table lists the method name and the operators it supports:

|  Operator  |    Method    |
|------------|--------------|
| `+` , `+=` | `plus`       |
| `-`, `-=`  | `minus`      |
| `*`, `*=`  | `times`      |
| `/`, `/=`  | `div`        |
| `%`, `%=`  | `rem`        |
| `-a`       | `unaryMinus` |
| `++`       | `inc`        |
| `--`       | `dec`        |

Notice that the increment and decrement operators don't have a distinction between the prefix and postfix positioning. Both `a++` and `++a` would lead to the `inc` method.

## Index Operator

The support for the index operator took me completely off guard when I looked at it. This is a complete game-changer... The index operator is the square brackets we use to get an array value by index.

To give you a sense of what I'm talking about, this is valid code in Manifold:

```
var list = List.of("A", "B", "C");
var v = list[0];
```

In this case, `v` will be `"A"` and the code is the equivalent to invoking `list.get(0)`. The index operators seamlessly map to get and set methods. We can do assignment as well using:

```
var list = new ArrayList<>(List.of("A", "B", "C"));
var v = list[0];
list[0] = "1";
```

Notice I had to wrap the List in an `ArrayList` since `List.of()` returns an unmodifiable List. But this isn't the part I'm reeling about. That code is "nice". This code is absolutely amazing:

```
var map = new HashMap<>(Map.of("Key", "Value"));
var key = map["Key"];
map["Key"] = "New Value";
```

Yes!

You're reading valid code in Manifold. An index operator is used to lookup in a map. Notice that a map has a put() method and not a set method. That's an annoying inconsistency that Manifold fixed with an extension method. We can then use an object to look up within a map using the operator.

## Relational and Equality Operators

We still have a lot to cover... Can we write code like this (referring to the `Vec` object from before):

```
if(vec3 > vec2) {
    // …
}
```

This won't compile by default. However, if we add the `Comparable` interface to the `Vec` class this will work as expected:

```
public class Vec implements Comparable<Vec> {
    // …

    public double magnitude() {
        return Math.sqrt(x  x + y  y + z * z);
    }

    @Override
    public int compareTo(Vec o) {
        return Double.compare(magnitude(), o.magnitude());
    }
}
```

These `>=, >, <, <=` comparison operators will work exactly as expected by invoking the `compareTo` method. But there's a big problem. You will notice that the `==` and `!=` operators are missing from this list.

In Java, we often use these operators to perform pointer comparisons, this makes a lot of sense in terms of performance. We wouldn't want to change something so inherent in Java. To avoid that, Manifold doesn't override these operators by default.

However, we can implement the `ComparableUsing` interface which is a sub-interface of the `Comparable` interface. Once we do that the `==` and `!=` will use the equals method by default. We can override that behavior by overriding the method `equalityMode()` which can return one of these values:

* `CompareTo` - will use the compareTo method for `==` and `!=`
* `Equals` (the default) - will use the equals method
* `Identity` - will use pointer comparison as is the norm in Java

That interface also lets us override the `compareToUsing(T, Operator)` method. This is similar to the compareTo method but lets us create operator-specific behavior which might be important in some edge cases.

## Unit Expressions for Scientific Coding

{{< youtube r4iycWnI5fE >}}

Notice that Unit expressions are experimental in Manifold. But they are one of the most interesting applications of operator overloading in this context.

Unit expressions are a new type of operator that significantly simplifies and enhances scientific coding while enforcing strong typing. With unit expressions, we can define notations for mathematical expressions that incorporate unit types. This brings a new level of clarity and type safety to scientific calculations.

For example, consider a distance calculation where speed is defined as 100 miles per hour. By multiplying the speed (miles per hour) by the time (hours), we can obtain the distance as such:

```
Length distance = 100 mph * 3 hr;
Force force = 5kg * 9.807 m/s/s;
if(force == 49.035 N) {
    // true
}
```

The unit expressions allow us to express numeric values (or variables) along with their associated units. The compiler checks the compatibility of units, preventing incompatible conversions and ensuring accurate calculations. This feature streamlines scientific code and enables powerful calculations with ease.

Under the hood, a unit expression is just a conversion call. The expression `100 mph` is converted to:

```
VelocityUnit.postfixBind(Integer.valueOf(100))
```

This expression returns a Velocity object. The expression `3 hr` is similarly bound to the postfix method and returns a Time object. At this point, the Manifold `Velocity` class has a `times` method which as you recall, is an operator and it's invoked on both results:

```
public Length times( Time t ) {
    return new Length( toBaseNumber() * t.toBaseNumber(), LengthUnit.BASE, getDisplayUnit().getLengthUnit() );
}
```

Notice that the class has multiple overloaded versions of the times method that accept different object types. A `Velocity` times `Mass` will produce `Momentum`. A `Velocity` times `Force` results in `Power`.

Many units are supported as part of this package even in this early experimental stage, [check them out here](https://github.com/manifold-systems/manifold/blob/master/manifold-deps-parent/manifold-science/src/main/java/manifold/science/util/UnitConstants.java#L64-L158).

You might notice a big omission here: Currency. I would love to have something like:

```
var sum = 50 USD + 70 EUR;
```

If you look at that code the problem should be apparent. We need an exchange rate. This makes no sense without exchange rates and possibly conversion costs. The complexities of financial calculations don't translate as nicely to the current state of the code.

I suspect that this is the reason this is still experimental. I'm very curious to see how something like this can be solved elegantly.

## Pitfalls of Operator Overloading

While Manifold provides powerful operator overloading capabilities, it's important to be mindful of potential challenges and performance considerations.

Manifold's approach can lead to additional method calls and object allocations, which may impact performance, especially in performance-critical environments.

It's crucial to consider optimization techniques, such as reducing unnecessary method calls and object allocations, to ensure efficient code execution.

Let's look at this code:

```
var n = x + y + z;
```

On the surface, it can seem efficient and short. It physically translates to this code:

```
var n = x.plus(y).plus(z);
```

This is still hard to spot but notice that in order to create the result we invoke two methods and allocate at least two objects. A more efficient approach would be:

```
var n = x.plus(y, z);
```

This is an optimization we often do for high-performance matrix calculations. You need to be mindful of this and understand what the operator is doing under the hood if performance is important. I don't want to imply that operators are inherently slower.

In fact they're as fast as a method invocation, but sometimes the specific method invoked and amount of allocations are unintuitive.

## Type Safety Features

The following aren't related to operator overloading but they were a part of the second video so I feel they make sense as part of a wide-sweeping discussion on type safety.

One of my favorite things about Manifold is its support of strict typing and compile time errors. To me, both represent the core spirit of Java.

### JailBreak: Type-Safe Reflection

`@JailBreak` is a feature that grants access to the private state within a class. While it may sound bad, `@JailBreak` offers a better alternative to using traditional reflection to access private variables. By jailbreaking a class, we can access its private state seamlessly, with the compiler still performing type checks. In that sense, it's the lesser of two evils.

If you're going to do something terrible (accessing private state), then at least have it checked by the compiler.

In the following code, the value array is private to String yet we can manipulate it thanks to the `@JailBreak` annotation. This code will print `"Ex0osed..."`:

```
@Jailbreak String exposedString = "Exposed...";
exposedString.value[2] = '0';
System.out.println(exposedString);
```

JailBreak can be applied to static fields and methods as well. However, accessing static members requires assigning null to the variable, which may seem counterintuitive. Nonetheless, this feature provides a more controlled and type-safe approach to accessing the internal state, minimizing the risks associated with using reflection.

```
@Jailbreak String str = null;
str.isASCII(new byte[] { 111, (byte)222 });
```

Finally, all objects in Manifold are injected with a jailbreak() method. This method can be used like this (notice that `fastTime` is a private field):

```
Date d = new Date();
long t = d.jailbreak().fastTime;
```

### Self Annotation: Enforcing Method Parameter Type

In Java, certain APIs accept objects as parameters, even when a more specific type could be used. This can lead to potential issues and errors at runtime. However, Manifold introduces the `@Self` annotation, which helps enforce the type of the object passed as a parameter.

By annotating the parameter with `@Self`, we explicitly state that only the specified object type is accepted. This ensures type safety and prevents the accidental use of incompatible types. With this annotation, the compiler catches such errors during development, reducing the likelihood of encountering issues in production.

Let's look at the `MySizeClass` from my previous posts:

```
public class MySizeClass {
    int size = 5;

    public int size() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean equals(@Self Object o) {
        return o != null && ((MySizeClass)o).size == size;
    }
}
```

Notice I added an equals method and annotated the argument with Self. If I remove the Self annotation this code will compile:

```
var size = new MySizeClass();
size.equals("");
size.equals(new MySizeClass());
```

With the `@Self` annotation the string comparison will fail during compilation.

### Auto Keyword: A Stronger Alternative to Var

I'm not a huge fan of the `var` keyword. I feel it didn't simplify much and the price is coding to an implementation instead of to an interface. I understand why the devs at Oracle chose this path. Conservative decisions are the main reason I find Java so appealing.

Manifold has the benefit of working outside of those constraints and it offers a more powerful alternative called `auto`. `auto` can be used in fields and method return values, making it more flexible than var. It provides a concise and expressive way to define variables without sacrificing type safety.

Auto is particularly useful when working with tuples, a feature not yet discussed in this post. It allows for elegant and concise code, enhancing readability and maintainability. You can effectively use auto as a drop-in replacement for var.

## Finally

Operator overloading with Manifold brings expressive and intuitive mathematical notation to Java, enhancing code readability and simplicity.

While Java doesn't natively support operator overloading, Manifold empowers developers to achieve similar functionality and use familiar operators in their code.

By leveraging Manifold, we can write more fluid and expressive code, particularly in scientific, mathematical, and financial applications.

The type safety enhancements in Manifold make Java more...

Well, "Java like". It lets Java developers build upon the strong foundation of the language and embrace a more expressive type-safe programming paradigm.

Should we add operator overloading to Java itself?

I'm not in favor. I love that Java is slow, steady and conservative.

I also love that Manifold is bold and adventurous.

That way I can pick it when I'm doing a project where this approach makes sense (e.g. a startup project), but pick standard conservative Java for an enterprise project.
