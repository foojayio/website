---
title: "The pitfall of implicit returns"
date: "2024-03-18T10:47:02+00:00"
lastmod: "2026-09-21T06:03:50+00:00"
description: "Implicit returns are a feature in some languages. They have recently bitten me, so here's my opinion. Statements, expressions, and returns Before diving…"
canonical: "https://blog.frankel.ch/pitfall-implicit-returns/"
authors:
  - "nicolas-frankel"
image: "mouse-trap-2846147-1.jpg"
categories:
  - "Kotlin"
related_posts:
  - "on-dependencies-in-objects"
  - "making-illegal-state-unrepresentable"
frozen: false
---

Implicit returns are a feature in some languages. They have recently bitten me, so here's my opinion.

## Statements, expressions, and returns

Before diving into implicit returns, we must explain two programming concepts influencing them. A lot of literature is available on the subject, so I'll paraphrase one of the existing definitions:
> An expression usually refers to a piece of code that can be evaluated to a value. In most programming languages, there are typically three different types of expressions: arithmetic, character, and logical.
>
> A statement refers to a piece of code that executes a specific instruction or tells the computer to complete a task.
>
> -- [Expression vs. Statement](https://www.baeldung.com/cs/expression-vs-statement)

Here's a Kotlin snippet:

```kotlin
val y = 10                //1
val x = 2                 //1

x + y                     //2

println(x)                //1
```

1. Statement, executes the assignment "task"
2. Expression, evaluates to a value, *e.g.*, 12

Functions may or may not return a value. When they do, they use the `return` keyword in most programming languages. It's a statement that needs an expression.

In Kotlin, it translates to the following:

```kotlin
fun hello(who: String): String {
    return "Hello $who"
}
```

In this regard, Kotlin is similar to other programming languages with C-like syntax.

## Implicit returns

A couple of programming languages add the idea of *implicit* returns: Kotlin, Rust, Scala, and Ruby are the ones I know about; each has different quirks.

I'm most familiar with Kotlin: you can omit the `return` keyword when you switch the syntax from a *block body* to an *expression body*. With the latter, you can rewrite the above code as the following:

```kotlin
fun hello(who: String): String = "Hello $who"
```

Rust also allows implicit returns with a slightly different syntax.

```rust
fn hello(who: &str) -> String {
    return "Hello ".to_owned() + who;          //1
}

fn hello_implicit(who: &str) -> String {
    "Hello ".to_owned() + who                  //2
}
```

1. Explicit return
2. Transform the statement in expression by removing the trailing semicolon - implicit return

Let's continue with Kotlin. The expression doesn't need to be a one-liner. You can use more complex expressions:

```kotlin
fun hello(who: String?): String =
    if (who == null) "Hello world"
    else "Hello $who"
```

## The pitfall

I was writing code lately, and I produced something akin to this snippet:

```kotlin
enum class Constant {
    Foo, Bar, Baz
}

fun oops(constant: Constant): String = when (constant) {
    Constant.Foo -> "Foo"
    else -> {
        if (constant == Constant.Bar) "Bar"
        "Baz"
    }
}
```

Can you spot the bug?  

Let's use the function to make it clear:

```kotlin
fun main() {
    println(oops(Constant.Foo))
    println(oops(Constant.Bar))
    println(oops(Constant.Baz))
}
```

The results are:

```
Foo
Baz
Baz
```

The explanation is relatively straightforward. `if (constant == Constant.Bar) "Bar"` does nothing. The following line evaluates to `"Bar"`; it implicitly returns the expression. To fix the bug, we need to add an `else` to transform the block into an expression:

```kotlin
if (constant == Constant.Bar) "Bar"
else "Baz"
```

Note that for simpler expressions, the compiler is smart enough to abort with an error:

```kotlin
fun oops(constant: Constant): String =
    if (constant == Constant.Bar) "Bar"      //1
    "Baz"
```

1. `'if' must have both main and 'else' branches if used as an expression`

## Conclusion

Implicit return is a powerful syntactic sugar that allows for more concise code. However, concise code doesn't necessarily imply being better code. I firmly believe that explicit code is more maintainable in most situations.

In this case, I was tricked by my code!  

Beware of implicit returns.

**Go further:**

* [Expression vs. Statement](https://www.baeldung.com/cs/expression-vs-statement)
* [Rust, Ruby, and the Art of Implicit Returns](https://earthly.dev/blog/single-expression-functions/)
* [To be clear, Rust does not have "implicit returns"](https://news.ycombinator.com/item?id=20470081)
* [On the merits of verbosity and the flaws of expressiveness](https://blog.frankel.ch/on-the-merits-of-verbosity-and-the-flaws-of-expressiveness/)

*Originally published at [A Java Geek](https://blog.frankel.ch/pitfall-implicit-returns/) on March 17th, 2024*
