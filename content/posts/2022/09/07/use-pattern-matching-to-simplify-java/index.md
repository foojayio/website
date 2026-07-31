---
title: "Use Pattern Matching to Simplify Java"
slug: "use-pattern-matching-to-simplify-java"
date: "2022-09-07T10:55:58+00:00"
lastmod: "2022-09-07T10:58:05+00:00"
description: "Learn from Simon Ritter how pattern matching in Java can make your code more concise without losing readability."
canonical: "https://www.azul.com/blog/use-pattern-matching-to-simplify-java/"
authors:
  - "simonritter"
image: "pattern-matching-image-li.png"
categories:
  - "Java Core"
tags:
related_posts:
  - "jdk-7-an-extended-hello-and-farewell"
  - "are-java-security-updates-important"
  - "much-ado-about-nothing-in-java"
enlighterjs: true
frozen: false
---

The concept of pattern matching has been around since the 1960s. It's a well-known language technique used in many programming languages, from Haskell and AWK to Rust and Scala.{#h-the-concept-of-pattern-matching-has-been-around-since-the-1960s-it-s-a-well-known-language-technique-used-in-many-programming-languages-from-haskell-and-awk-to-rust-and-scala}

Pattern matching is relatively new to Java. It was introduced in JDK 14 and has been progressing with new uses since then.

This article explores how these new features can make your code more concise without losing readability.

If you're more of an interactive learner, watch my [on-demand webinar on pattern matching in Java](https://www.azul.com/resources-hub/webinars-2/the-art-of-java-language-pattern-matching-on-demand-webinar) from June 2022. First, let's back up and explain what pattern matching is.

What Is Pattern Matching? {#h-what-is-pattern-matching}
-------------------------------------------------------

A pattern consists of two distinct things:

* A **match pre*dicate*** gives us a way of determining whether we have a target that matches a given pattern. Being a predicate, it evaluates to a Boolean, meaning it matches against the pattern or it doesn't.
* One or more**pattern variables** is associated with that match predicate. Pattern variables are conditionally extracted based on the evaluation of the predicate.

There are several different pattern types that we can use.

1. **Constant:** This has existed in Java from the beginning. The switch statement uses a constant pattern for each case. Since the pattern predicate matches a value, there is no need for a pattern variable.
2. **Type:** Since Java is an object-oriented language, we can use a pattern predicate that matches a Java type. This is the pattern that we talk most about in this article.
3. **Deconstruction:** In this type of pattern matching, not only do we evaluate a predicate, but we also extract values from objects to populate the pattern variables.
4. **Var:** Like local variable type inference, introduced in JDK 10, this type of pattern matching uses the compiler to infer types for us.
5. **Any:** Like var, it will match anything; but in this case, we simply ignore the value. This will become clear when we look at an example later.

Why Do We Need Pattern Matching? {#h-why-do-we-need-pattern-matching}
---------------------------------------------------------------------

Pattern matching allows us to test for a specific pattern on a character sequence or a data structure. It makes code easier to read, easier to understand, faster to create, and more resistant to bugs.

* **Pattern matching creates cleaner, shorter code** by relying less on reflection and casting. Code expresses more complex logic with fewer lines.
* **Pattern matching reduces bugs** caused by pattern dominance (pattern dominance is when a previous pattern supersedes another, making it unreachable) and pattern non-exhaustiveness (pattern exhaustiveness is when the compiler warns you that you have not checked for all possible variants of a type).

The instanceof Operator in JDK 18 {#h-the-instanceof-operator-in-jdk-18}
------------------------------------------------------------------------

Let's look at how we use the *instanceof* operator. Because Java is object-oriented, we have polymorphism: we can view an object as any of the types that it is -- its exact type, any of the superclasses, and any of the interfaces it implements.

Often, we are faced with a situation where we're provided with a reference, but we are unsure of its specific type. We can test the reference to see if it is a given type using the *instanceof*operator. Here's a simple example:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">if (o instanceof String) {
  String s = (String)o;
  System.out.printin("Length = " + s.length());
}</pre>

Having determined that the reference *o* is of type *String* , we must define a new local variable of type *String* and assign to it the value of *o* using an explicit cast. Pattern matching for *instanceof*, a permanent feature since JDK 16, eliminates this unnecessary extra boilerplate code.

The code now looks like this:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">if (o instanceof String s) {
  System.out.printin("Length = " + s.length());
}</pre>

Here, the pattern predicate is *whether o is an instanceof String* and the pattern variable is *s*, which is assigned for us by the compiler.

Pattern matching for *instanceof* uses what is called flow scoping. If you look at local variables, their scope runs from where it is declared until the end of the block in which it is declared (method, loop, block, etc.) They are also subject to definite assignment (they must explicitly be assigned a value). In the case of predicate variables, they are also subject to definite assignment, but their scope is the set of places where they would have definite assignment.

Taking the example above, the scope of *s* is only valid inside the true branch of this *if* statement because that's the only place it will have definite assignment. There are two things to bear in mind here.

**1 --** If you invert the test like this, the variable *s* will have scope until the end of the block containing the if statement:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">if (!(o instanceof String s))  return;</pre>

**2 --** This allows the reuse of the same variable name, like this:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">if (o instanceof Float n) {
  } else if (o instanceof Integer n) {
  } else if (o instanceof Short n) {
}</pre>

Switch Statements and Expressions in JDK 18 {#h-switch-statements-and-expressions-in-jdk-18}
--------------------------------------------------------------------------------------------

The next use of pattern matching in Java is in switch. Until JDK 17, even with the introduction of switch expressions, we were still constrained to a small set of types we could switch over: integral values, strings, and enumerations. JDK 17 introduced pattern matching for switch, which allows us to use a type pattern as a case.

For example:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">void typeTester(Object o) {
    switch (o) {
        case null -&gt; System.out.printIn("Null type");
        case String s -&gt; System.out.printIn("String length: " + s.length());
        case Color c -&gt; System.out.printIn("Color with RGB: " + c.getRGB();
        case int[] ia -&gt; System.out.printIn("Array of ints, length" + ia.length);
        default -&gt; System.out.printIn(o.toString());
    }
}
</pre>

There are several things to understand here.

Both switch statements and switch expressions can now include an explicit null case. This is useful to eliminate the need for a potential explicit test before the switch. To maintain backwards compatibility, if a null case is not included, the compiler will insert one as the first that throws a *NullPointerException*. Since a null is always a null, there is no need for a pattern variable. It is also possible to include null with the default case:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">null, default -&gt; System.out.printIn("Invalid type");</pre>

For the *String* and *Color* type cases, the pattern predicate matches on those types and assigns the reference to the specified pattern variable if there is a match. The scope of the pattern variables is only in the relevant case block

Primitives (like *int* and *float* ) in Java are not types, so we cannot use them for a pattern predicate. However, an array of primitives is a type and we can therefore have a case for an array of *ints*.

When using pattern matching for switch (either in a statement or expression), the switch must be exhaustive. This means that all possible types must be handled.

Let's take this switch, for example.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">switch (o) {
  case integer i -&gt; System.out.printIn("Integer");
  case Byte b -&gt; System.out.printIn("Byte");
}</pre>

In this case, if *o* is of type Float, there is no case to handle it. We could have simply passed over the switch, but that could lead to hard-to-find bugs and is not a good design.

The obvious way to resolve this is to include a default case that matches against anything that is not an Integer or Byte. This does not mean, however, that every switch must have a default case to be complete.

JDK 15 introduced another new language construct, sealed classes. Here we define a simple sealed type.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Public sealed class Shape permits Triangle, Square, Pentagon {...}</pre>

We could use this in a switch like this:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">void typeTester(Shape shape) {
  switch (shape) {
    case Triangle t -&gt; System.out.println("It's a triangle");
    case Square s -&gt; System.out.println("It's a square");
    case Pentagon p -&gt; System.out.println("It's a pentagon");
    case Shape s -&gt; System.out.println("It's a shape");
  }
}</pre>

Since Shape can only have subclasses of Triangle, Square and Pentagon, we have covered all possibilities in the switch, and it is exhaustive (this is also referred to as completeness).

The order of the patterns is also significant. If we were to make the case for Shape the first one, it would catch all objects. Since this would render the lower cases unreachable, the compiler would generate an error.

Pattern matching for switch also includes guarded patterns. In this case, we can add a test to our pattern. Using the above example:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">void typeTester(Shape shape) {
  switch (shape) {
    case Triangle t &amp;&amp; t.size() &lt; 25 -&gt; System.out.println("Small triangle");
    case Triangle t -&gt; System.out.println("Big triangle");
    case Square s -&gt; System.out.println("It's a square");
    case Pentagon p -&gt; System.out.println("It's a pentagon");
    case Shape s -&gt; System.out.println("It's a shape");
  }
}</pre>

The first case for Triangle now includes an additional test (guard) to determine if its size is less than 25. For the switch to remain exhaustive, we must also have a case for Triangle without a guard to handle triangles of size 25 or greater.

In JDK 19, the syntax for guarded patterns will change, replacing the \&\& operator with the keyword, when. Similarly, we must put the guarded Triangle case before the unguarded to avoid the guarded one being unreachable.

More pattern matching will be added to Java in the future. Already, JDK 19 is scheduled to include pattern matching for records, which is a deconstruction pattern. We'll cover that and some other aspects in a later blog post.

All the Readability Without the Unnecessary Code {#h-all-the-readability-without-the-unnecessary-code}
------------------------------------------------------------------------------------------------------

As you can see, pattern matching is a powerful addition to the Java language that reduces the amount of boilerplate code required without sacrificing readability.

It also provides several ways to help detect errors at compile time rather than at runtime when your code is in production.

Why not try using pattern matching in your next Java application?
[![Webinar: The Art of Java Language Pattern Matching](https://www.azul.com/wp-content/uploads/pattern-matching-image-li.png)](https://www.azul.com/resources-hub/webinars-2/the-art-of-java-language-pattern-matching-on-demand-webinar)

Want to learn more about pattern matching in JDK 18? Watch our on-demand webinar from June 2022, [The Art of Java Language Pattern Matching](https://www.azul.com/resources-hub/webinars-2/the-art-of-java-language-pattern-matching-on-demand-webinar).

You will learn some of the newer features in the Java language, plus some ideas that may be included in future versions of Java, all around this idea of pattern
