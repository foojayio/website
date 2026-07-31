---
title: "The State of Pattern Matching in Java 17"
slug: "the-state-of-pattern-matching-in-java-17"
date: "2022-01-03T10:35:42+00:00"
lastmod: "2022-01-03T10:35:43+00:00"
description: "Pattern matching is a language feature where you can test for a specific pattern on a character sequence or a data structure."
canonical: "https://deepu.tech/state-of-pattern-matching-java/"
authors:
  - "deepu-sasidharan"
image: "https://i.imgur.com/M6xKDas.jpeg"
categories:
  - "Java Core"
tags:
related_posts:
  - "immutable-collections-in-java-with-sealed-types"
  - "getting-started-with-java-17-and-intellij-idea"
  - "keeping-pace-with-java-using-eclipse-ide"
enlighterjs: true
frozen: false
---

So what exactly is pattern matching?
> "The act of checking a given sequence of tokens for the presence of the constituents of some pattern." -- Wikipedia

Or simply put, pattern matching is a language feature where you can test for a specific pattern on a character sequence or a data structure.

Pattern matching can be classified into two types.

* **Sequence patterns**: pattern matching on character sequence or strings. Also known as our beloved Regular Expressions 😉 --- I still wish I could write RegEx without cursing and looking up the syntax.
* **Tree patterns**: testing for patterns on a data structure. This is what we are going to talk about today.

![regex meme](https://i.imgur.com/M6xKDas.jpeg)

Why Pattern Matching? {#h2-0-why-pattern-matching}
--------------------------------------------------

Why do we need pattern matching? We don't, to be honest!

Pattern matching is not a requirement for a good programming language. Many of the most popular languages like JavaScript and Go get by just fine without it. But still, it's a great feature to have due to these advantages it offers:

* Reduced cognitive complexity
  * Much more concise code and better readability.
  * More complex logic can be expressed with fewer lines of code. While not very important, it's still a good thing.
  * Simpler to write and maintain.
* Reduced reliance on reflection and casting, especially in Java.
* Avoid bugs caused by pattern dominance and pattern non-exhaustiveness.
  * Pattern dominance is when a previous pattern supersedes another making it unreachable. A modern compiler should be able to catch it.
  * Pattern exhaustiveness is when the compiler warns you when you have not checked for all possible variants of a type, like having a case for all enum values or all subclasses of a type and so on.

Of course, these advantages depend on how a language implements pattern matching. So later, we will look at these from the perspective of Java.

Pattern Matching Features {#h2-1-pattern-matching-features}
-----------------------------------------------------------

These are many languages that have great support for pattern matching. Rust and OCaml lead the pack here. On the JVM world, Scala also offers many of these pattern matching features. So when a language claims to have support for pattern matching, these are the features we expect:

* Enum matching in switch statements --- Most languages, including Java, already does this
* Match the value in switch/if statements --- Common in any Turing complete language
* Match type in switch/if statements --- Required for pattern matching
* Pattern matched variable assignments
* Null checks --- Required in a language like Java
* Type guards
* Refined patterns --- If we can match for data types, then its logical to expect refining the pattern further using the matched type, which acts as a type guard
* Pattern dominance and type exhaustion
* Partial/Nested/Compound type and/or value checks
* Shallow/Deep Position-based Destructured matching

It's not an exhaustive list but more of a general expectation to fulfill the previous advantages we saw.

Pattern Matching in Java {#h2-2-pattern-matching-in-java}
---------------------------------------------------------

Unfortunately, Java is still a bit behind the curve when it comes to pattern matching.

But fortunately, we already have most of the building blocks required to achieve most of the features we saw earlier.

* Switch statements
* Switch expressions (Java 14) --- As compared to the switch statements, switch expressions can return a value, have multiple case labels on the same line, and need to be exhaustive. And hence can be used for variable assignments and statements without fallthrough
* Pattern matching for `instanceof` (Java 16) --- It can now pattern match data types, which means casting after a check is no longer required, and this can be used in `if` statements, assignments, and returns.
* Sealed classes (Java 17)
* Pattern matching for switch (Java 17 preview - JEP 406)

Now let us look at some examples of what is already possible to do as of Java 17

### Pattern Matching for `instanceof` {#h3-3-pattern-matching-for-instanceof}

From Java 16 onwards, we can do pattern matching using the `instanceof` operator. It can be used on `if` statements, and we can use it as type guards on variable assignments and returns. This lets us use a variable without further casting after a type check. This is a new syntax added to the language.

Here we can see pattern matching using the `instanceof` operator in action. A type and a variable name follow the operator, and we can access the variable without further casting.

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Before

if (obj instanceof String) {
   String s = (String) obj;
   System.out.println(s.length());
}

// After

if (obj instanceof String s) {
   // Let pattern matching do the work!
   System.out.println(s.length());
}</pre>

We can also use pattern matching as a type guard in returns and variable assignments. See how concise the code is when we do the return using a type guard instead of casting the type.

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Before
public boolean equals(Object o) {
   if (!(o instanceof Point))
       return false;
   Point other = (Point) o;
   return x == other.x
       &amp;&amp; y == other.y;
}

// After
public boolean equals(Object o) {
   return (o instanceof Point other)
       &amp;&amp; x == other.x
       &amp;&amp; y == other.y;
}</pre>

Here is a variable assignment using a type guard. Again the code is much nicer with pattern matching.

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Before

var x = o instanceof Point ? ((Point)o).x : 0;
System.out.println(x);

// After

var x = o instanceof Point p ? p.x : 0;
System.out.println(x);</pre>

For a realistic use case, we could do something like below, when we want to do different logic based on the type, using the `instanceof` operator. But that's a lot of if-else and cognitive load.

<pre class="EnlighterJSRAW" data-enlighter-language="java">static String formatter(Object o) {
   String formatted = "unknown";
   if (o instanceof Integer i) {
       formatted = String.format("int %d", i);
   } else if (o instanceof Long l) {
       formatted = String.format("long %d", l);
   } else if (o instanceof Double d) {
       formatted = String.format("double %f", d);
   } else if (o instanceof String s) {
       formatted = String.format("String %s", s);
   }
   return formatted;
}</pre>

### Pattern Matching for switch {#h3-4-pattern-matching-for-switch}

But with the new preview feature in Java 17, we can do pattern matching for data types in switch cases as well. For Both, switch statements and switch expressions.

This will let us rewrite the previous code using a switch expression like below. As you can see, this is also a new syntax. And it's similar to the one added to the `instanceof` operator.

<pre class="EnlighterJSRAW" data-enlighter-language="java">static String formatter(Object o) {
   return switch (o) {
       case Integer i -&gt; String.format("int %d", i);
       case Long l    -&gt; String.format("long %d", l);
       case Double d  -&gt; String.format("double %f", d);
       case String s  -&gt; String.format("String %s", s);
       default        -&gt; o.toString();
   };
}</pre>

This has Reduced cognitive complexity, and the syntax is closer to most other languages with pattern matching. With this, the compiler can warn us when pattern dominance occurs, as generic types should always come after specific types.

Since this is a preview feature, the syntax might change in future versions. I hope not, as I find this syntax nice in Java.

We can also do null checks in these switch cases, which makes pattern matching more useful in the case of Java.

<pre class="EnlighterJSRAW" data-enlighter-language="java">static String formatter(Object o) {
   return switch (o) {
       case null      -&gt; "Oops";
       ...
       case String s  -&gt; String.format("String %s", s);
       default        -&gt; o.toString();
   };
}

// or

static String formatter(Object o) {
   return switch (o) {
       ...
       case String s       -&gt; String.format("String %s", s);
       case null, default  -&gt; "Oops";
   };
}</pre>

### Type Guards \& pattern refinement {#h3-5-type-guards-pattern-refinement}

As we saw earlier, Type guards are already supported for `instanceof` operator from Java 16 onwards, and Java 17 preview adds that for switch cases as well. This means we can rely on the type guards to refine the patterns further to have conditions, relations, and value checks.

<pre class="EnlighterJSRAW" data-enlighter-language="java">static void test(Object o) {
   if ((o instanceof String s) &amp;&amp; s.length() &gt; 3) {
       System.out.println(s);
   } else {
       System.out.println("Not a string");
   }
}

// Or

static void test(Object o) {
   switch (o) {
       case String s &amp;&amp; (s.length() &gt; 3)  -&gt; System.out.println(s);
       case String s                      -&gt; System.out.println("Invalid string");
       default                            -&gt; System.out.println("Not a string");
   }
}</pre>

While it's not as flexible as in Rust or OCaml, it's a good start, in my opinion.

### Pattern exhaustion with Sealed classes {#h3-6-pattern-exhaustion-with-sealed-classes}

We need to understand another new feature in Java 17, Sealed classes, to understand pattern exhaustion.

#### Sealed classes

A sealed class lets you control which class can extend it. The same applies to sealed interfaces as well.

Permitted classes can be defined either as simple inner classes or in separate class files using the new `permits` keyword.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public abstract sealed class Shape
  permits Circle, Rectangle, Square { ... }

// Or

public abstract sealed class Shape {
  final class Circle extends Shape { ... }
  final class Square extends Shape { ... }
  final class Rectangle extends Shape { ... }
}</pre>

A sealed class imposes three constraints on its permitted sub-classes.

1. A Sealed class and its permitted subclasses must be in the same module or the same package in case of an unnamed module.
2. Every permitted subclass must directly extend the sealed class.
3. Every permitted subclass must use a modifier to describe the propagation of sealing. Allowed modifiers are `final`, `sealed`, and `non-sealed`.
   * Final subclasses cannot further be extended. Hence the class hierarchy ends with them. Records are implicitly final and hence do not need the keyword.
   * A Sealed subclass can further restrict class hierarchy with its own permitted subclasses, and the same three rules apply all over again for them
   * A Non-sealed subclass is a bit different. As the keyword suggests, it reverts to being open for extension by any class and breaks the class hierarchy rules set by the parent sealed class. Non-sealed sub-classes are still valid children and work well with other concepts. We will see that later.

> A fun fact, `non-sealed` is the first hyphenated keyword in Java.

Here is how it would look in practice. Sealed classes can also be used with Records. Here the `Shape` interface is sealed, and it permits `Circle`, `Rectangle`, `Square`, and `WeirdShape` as subclasses.

`Circle` is implicitly final as it's a record. `Square` is declared as final. And hence both cannot be extended further, and their hierarchy ends here.

`Rectangle` is declared as sealed and permits only `TransparentRectangle` and `FilledRectangle` as subclasses. Hence Rectangle's class hierarchy can extend further, and `TransparentRectangle` and `FilledRectangle` can even define their own hierarchy.

`WeirdShape` is declared as non-sealed and hence can be extended by any other class or record. Here the class hierarchy is wide open, and subclasses of a `WeirdClass` do not have to follow the rules of a sealed class. All instances of subclasses of `WeirdShape` will also be an instance of `WeirdShape,` and hence any code that checks for exhaustiveness of subtypes of `Shape` will still be valid.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public sealed interface Shape
   permits Circle, Rectangle, Square, WeirdShape { ... }

public record Circle(int r) implements Shape { ... }
public final class Square implements Shape { ... }

public sealed class Rectangle implements Shape
   permits TransparentRectangle, FilledRectangle { ... }

public non-sealed class WeirdShape implements Shape { ... }
</pre>

A sealed class can be abstract and can have abstract members, and similarly, sealed interfaces can have default implementations and so on. But abstract subclasses should be sealed or non-sealed and not final.

Extending a non-permitted class with a sealed class will be a compile-time error.
> Another fun fact: A combination of sealed classes and records is nothing but algebraic data types. (type formed by combining others, ex, Tuples, Records, unions)

#### Pattern exhaustion

Remember pattern exhaustion we talked about? The java compiler cannot check for all possible variations for normal classes and hence would require us to add a default case to avoid missing paths in a switch expression, as switch expressions do not fall through and must be exhaustive in Java. That does not apply to switch statements as we can choose not to have a default case for them as they fall through.

But with sealed classes, the compiler knows exactly what the expected variants are. We can use this for exhaustive pattern matching. In that case, the compiler will warn when we miss a variant in the check without adding a default case. This is a much less error-prone and clearer approach. Even if the subclasses are non-sealed, the compiler will still do the exhaustiveness check.

See this example; if we use the same sealed classes we saw earlier in the first sample, we will get a compile-time error as we are not checking all variants of `Shape`, and there is no default case. In the second example, however, there is no error even without a default case as the compiler knows all possible variants of `Shape` have been accounted for. If we remove the `sealed` keyword for `Shape`, then the second sample will have the same error as there is no default case, and the compiler doesn't know about all variants of `Shape`.

<pre class="EnlighterJSRAW" data-enlighter-language="java">Shape rotate(Shape shape, double angle) {
   return switch (shape) {   // this will be 'switch' expression does not cover all possible input values error
       case Circle c    -&gt; c;
       case Square s    -&gt; shape.rotate(angle);
   };
}

Shape rotate(Shape shape, double angle) {
   return switch (shape) {
       case Circle c     -&gt; c;
       case Rectangle r  -&gt; shape.rotate(angle);
       case Square s     -&gt; shape.rotate(angle);
       case WeirdShape w -&gt; shape.rotate(angle); // still exhaustive
       // no default needed!
   };
}</pre>

### Partial patterns and destructing {#h3-7-partial-patterns-and-destructing}

There is a candidate feature to add preview for destructing/deconstruction syntax for the `instanceof` operator, so logically at some point, that should extend to the switch syntax as well. There are also talks about improving the feature further to add support for primitives in switch case patterns and to declare how it should be deconstructed at the class level so that normal classes can also be deconstructed.

#### Record Patterns \& Array Patterns (Preview - JEP 405)

So far, the only solid thing is the [candidate for deconstruction in instanceof operator](https://openjdk.java.net/jeps/405).

With this proposal, we should be able to deconstruct Records and Arrays. This is closer to how it works in Rust, for example. Look at how `Point` is deconstructed to its members, and we can directly use the members with type guard.

<pre class="EnlighterJSRAW" data-enlighter-language="java">record Point(int x, int y) {}

void printSum(Object o) {
   if (o instanceof Point(int x, int y)) {
       System.out.println(x+y);
   }
}</pre>

Deconstruction can be nested as well but doesn't have any facility for ignoring members like in other languages yet.

<pre class="EnlighterJSRAW" data-enlighter-language="java">record Point(int x, int y) {}
enum Color { RED, GREEN, BLUE }
record ColoredPoint(Point p, Color c) {}

void printSum(Object o) {
   if (o instanceof ColoredPoint(Point(int x, int y), Color c) {
       System.out.println(x+y);
   }
}</pre>

Array deconstruction is also proposed, and syntax is a bit similar to array deconstruction in JavaScript. Thankfully at least elements at the end can be ignored in this case; it would be pretty useless otherwise 😉

See how the string array is deconstructed to the first two elements with the rest ignored. The syntax is closer to the new array syntax as well.

<pre class="EnlighterJSRAW" data-enlighter-language="java">static void printFirstTwoStrings(Object o) {
   if (o instanceof String[] { String s1, String s2, ... }){
       System.out.println(s1 + s2);
   }
}</pre>

Nested deconstruction is also possible for a mix of arrays and records. See how an array of `Points` are deconstructed here. And yes, `var` should work instead of type as well.

<pre class="EnlighterJSRAW" data-enlighter-language="java">static void printSumOfFirstTwoXCoords(Object o) {
   if (o instanceof Point[] { Point(var x1, var y1), Point(var x2, var y2), ... }) {
       System.out.println(x1 + x2);
   }
}</pre>

Of course, it's a baby step and not as powerful as deconstruction in Rust or JS, but I hope we get there, and even this baby step would make pattern matching much more powerful.

Hopefully, this would be extended to switch as well. That would make pattern matching in Java closer to complete, with only normal classes remaining to be accounted for. If the normal classes can specify how they should be deconstructed, that issue will be solved, and we will have powerful pattern matching in Java.

So based on what we saw so far, the state of pattern matching in Java is as below.

* Enum matching in switch statements ✅
* Match type/value in switch statements ✅
* Match type/value in if statements ✅
* Pattern matched variable assignments ✅
* Null checks ✅
* Type guards ✅
* Refined patterns ✅
* Pattern dominance and type exhaustion 🆗
* Partial/Nested/Compound type and/or value checks 🆗
* Shallow/Deep Position-based Destructured matching 🆗

Most of the basic requirements are met with the `instanceof` operator pattern matching, and the Java 17 preview makes many other features possible. Of course, type exhaustion only works for sealed classes, and refined patterns are still quite basic, but the majority of the features for proper pattern matching are already available with the preview.

To summarize, these are the current and future limitations as far as we can see

**JDK 17 preview**

* No deconstruction
* No nested patterns
* Type exhaustion is only for sealed classes and enums
* Pattern refinement is limited
* Still a preview feature

**JDK 18+ (maybe)**

* No deconstruction for classes and in switch cases
* No nested patterns for classes and in switch cases
* Type exhaustion is only for sealed classes and enums
* No feature for ignoring don't-care patterns during deconstruction
* No named patterns
* Still a preview feature

In a future version of Java, some of these limitations might be removed, at least for `instanceof` operator.  

We probably would still have to wait a few years before we can actually start using full-fledged pattern matching in production. I'll update this post when more features are released.

*** ** * ** ***

If you like this article, please leave a like or a comment. Originally published at [deepu.tech](https://deepu.tech/state-of-pattern-matching-java/).

You can follow me on [Twitter](https://twitter.com/deepu105) and [LinkedIn](https://www.linkedin.com/in/deepu05/).

Cover image credit: Meme from [makeameme.org](https://makeameme.org/meme/patterns-patterns-everywhere-5c0a75)
