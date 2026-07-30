---
title: "Pattern-matching across different languages"
slug: "pattern-matching-across-different-languages"
date: "2025-08-05T07:40:40+00:00"
lastmod: "2025-08-05T07:40:41+00:00"
description: "With destructuring, pattern matching is a huge help to developers who want to write readable and maintainable code."
canonical: "https://blog.frankel.ch/pattern-matching-different-languages/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2025/07/cover-1024x683.png"
categories:
  - "Java"
  - "Kotlin"
  - "Research"
  - "Scala"
  - "Uncategorized"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Pattern matching is a major feature in software development. While pattern matching applies in several locations, its current usage is limited to `switch case` blocks. I want to compare the power of pattern matching across a couple of programming languages I'm familiar with in this post.

I assume that every reader is familiar with the `switch case` syntax inherited from C. In short:

* The `switch` clause references a value-returning statement
* Each `case` clause sets another statement; if the value matches the statement, it executes the related block
* `case` clauses are evaluated in order. The first clause that matches gets its block executed
* In C, `case` clauses are fall-through; you need to explicitly `break` to escape the `switch`, otherwise, the next `case` is evaluated

Java's pattern matching {#h2-0-java-s-pattern-matching}
-------------------------------------------------------

I'll start with Java, as it was the first programming language I used in a professional context.

Java's pattern matching has evolved a lot across its versions. Here's the "official" sample for version 23, slightly amended. It defines several `Shape` implementations and a `static` method to evaluate its perimeter. It's definitely **not** a good example of Object-Oriented Programming.

<pre class="EnlighterJSRAW" data-enlighter-language="java">interface Shape { }
record Rectangle(double length, double width) implements Shape { }
record Circle(double radius) implements Shape { }

public class Main {
    static double getPerimeter(Shape s) throws IllegalArgumentException {
        return switch (s) {                                                 //1
            case Rectangle r when r.length() == r.width() -&gt; {              //2
                System.out.println("Square detected");
                    yield 4 * r.length();
            }
            case Rectangle r -&gt;                                             //3
                2 * r.length() + 2 * r.width();
            case Circle c -&gt;                                                //4
                2 * c.radius() * Math.PI;
            default -&gt;                                                      //5
                throw new IllegalArgumentException("Unrecognized shape");
        };
    }
}</pre>

1. Reference the `Shape` parameter as `s`
2. Evaluate whether `s` is a `Rectangle` *and* whether the `Rectangle` is a square
3. Evaluate whether `s` is a `Rectangle`
4. Evaluate whether `s` is a `Circle`
5. If none of the previous clauses match, default to throwing an exception

This Java version will be our baseline.

Characteristics of the new `switch` syntax {#h2-1-characteristics-of-the-new-switch-syntax}
-------------------------------------------------------------------------------------------

The new syntax has some advantages over the legacy non-arrow `switch` inherited from C.

C-style `case` clauses are fall-through. Once the runtime has executed a `case` block, it runs the next `case` block. To avoid it, you need to set a `break` explicitly. In the not-so-good old days, some developers leveraged this feature to avoid code duplication. However, experience has shown that it was too much of a cause of potential bugs: modern coding practices tend to avoid it. Yet, it's too easy to forget a `break`. With the new arrow syntax, the runtime only evaluates the corresponding `case` block.

C-style `case` clauses only evaluate simple values. The community celebrated Java 7 as a huge boon when it allowed String values. Java 21 went even further: coupled with the arrow syntax, it allowed for switching on types. In the above example, we check the exact `Shape` type.

Additionally, if switching on classes *and* if the class hierarchy is `sealed`, the compiler can automatically detect missing cases. Finally, version 24 added the additional optional `when` filter.

On the flip side, in the old C syntax, the runtime jumps directly to the correct `case` clause. The new arrow syntax evaluates them sequentially, exactly like if with `if else` statements.

In the next sections, we will port the code to other languages.

Scala's pattern matching {#h2-2-scala-s-pattern-matching}
---------------------------------------------------------

Scala's pattern matching has been second to none since its inception. Kotlin drew a lot of inspiration from it.

<pre class="EnlighterJSRAW" data-enlighter-language="scala">trait Shape
case class Rectangle(length: Double, width: Double) extends Shape
case class Circle(radius: Double) extends Shape

def getPerimeter(s: Shape) = {
  s match {                                                                 //1
    case Rectangle(length, width) if length == width =&gt;                     //2-3
      println("Square detected")
      4 * length
    case Rectangle(length, width) =&gt; 2 * length + 2 * width                 //3-4
    case Circle(radius) =&gt; 2 * radius * Math.PI                             //3-5
    case _ =&gt; throw new IllegalArgumentException("Unrecognized shape")      //6
  }
}</pre>

1. Use the `s` reference directly
2. Evaluate whether `s` is a `Rectangle` and whether the `Rectangle` is a square
3. Scala additionally matches the class attributes
4. Evaluate whether `s` is a `Rectangle`
5. Evaluate whether `s` is a `Circle`
6. If none of the previous clauses match, default to throwing an exception

Kotlin's pattern matching {#h2-3-kotlin-s-pattern-matching}
-----------------------------------------------------------

Let's translate Java's code to Kotlin. For that, we *must* activate the *experimental* `Xwhen-guards` compilation feature described in [KEEP 371](https://github.com/Kotlin/KEEP/issues/371).

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">interface Shape
data class Rectangle(val length: Double, val width: Double): Shape
data class Circle(val radius: Double): Shape

fun getPerimeter(s: Shape) = when (s) {                                     //1
        is Rectangle if s.length == s.width -&gt; {                            //2
            println("Square detected")
            4 * s.length
        }
        is Rectangle -&gt; 2 * s.length + 2 * s.width                          //3
        is Circle -&gt; 2 * s.radius * Math.PI                                 //4
        else -&gt; throw IllegalArgumentException("Unknown shape")             //5
    }
}</pre>

1. Reference the `Shape` parameter as `s`
2. Evaluate whether `s` is a `Rectangle` **and** whether the `Rectangle`'s `width` is equal to its `height`
3. Evaluate whether `s` is a `Rectangle`
4. Evaluate whether `s` is a `Circle`
5. If none of the previous clauses match, default to throwing an exception

Kotlin's pattern matching greatly resembles Java's, with slight syntax changes, *e.g.* , `if` replaces `when`.

The comparative evolution of Kotlin vs. Java in pattern matching is quite enlightening. Java lagged behind the legacy C syntax, while Kotlin could already match on concrete types from the beginning. In 2020, Java 14 reduced the gap with the arrow operator; one year later, Java 16 closed the gap completely with the ability to match on concrete types. Finally, Java 23 added the `when` clause.

The experimental nature of Kotlin's `if` means Java has overtaken Java--at least in this area! It's rare enough to be noted.

Python's pattern matching {#h2-4-python-s-pattern-matching}
-----------------------------------------------------------

Before, Python didn't offer anything similar to the `switch` statement of the above JVM languages. From version 3.10, it does offer the same capability in an elegant way:

<pre class="EnlighterJSRAW" data-enlighter-language="python">class Shape:
    pass

class Rectangle(Shape):
    def __init__(self, length: float, width: float):
        self.length = length
        self.width = width

class Circle(Shape):
    def __init__(self, radius: float):
        self.radius = radius

def get_perimeter(s: Shape) -&gt; float:
    match s:
        case Rectangle(length=l, width=w) if l == w:
            print("Square detected")
            return 4 * l
        case Rectangle(length=l, width=w):
            return 2 * l + 2 * w
        case Circle(radius=r):
            return 2 * pi * r
        case _:
            raise ValueError("Unknown shape")</pre>

The runtime evaluates the `case` clauses sequentially, as in the JVM languages.

Rust's pattern matching {#h2-5-rust-s-pattern-matching}
-------------------------------------------------------

Rust's approach to memory management doesn't play well with checking types. In short, Rust offers two base concepts:

* Structures are structured data placeholders; their memory size is known at compile time
* Traits are contracts, akin to interfaces
* You can provide an implementation of a trait for a structure
* Referencing a variable by its trait has consequences.  
  Since the reference can point to structures of different sizes, the compiler can't know its size at compile-time and must place the variable on the heap instead of the stack.

I tried to port the original code to Rust one-to-one for educational purposes. The original Java doesn't take advantage of polymorphism; it is not great. In Rust, it's even uglier. It's interesting to realize that while Rust isn't an Object-Oriented programming language, it leads you to use polymorphism.

Warning: I don't recommend the following code and will deny any association with it!

<pre class="EnlighterJSRAW" data-enlighter-language="rust">trait Shape: Any {
    fn as_any(&amp;self) -&gt; &amp;dyn Any;                                     //1
}

struct Circle {
    radius: f64,
}

struct Rectangle {
    width: f64,
    height: f64,
}

impl Shape for Circle {
    fn as_any(&amp;self) -&gt; &amp;dyn Any {                                    //2
        self
    }
}
impl Shape for Rectangle {
    fn as_any(&amp;self) -&gt; &amp;dyn Any {                                    //2
        self
    }
}

fn get_perimeter(s: Box&lt;dyn Shape&gt;) -&gt; f64 {
    match s.as_any() {                                                //3
        any if any.is::&lt;Rectangle&gt;() =&gt; {                             //4
            let rectangle = any.downcast_ref::&lt;Rectangle&gt;().unwrap(); //5
            if rectangle.width == rectangle.height {
                println!("Square matched");
                4.0 * rectangle.width
            } else {
                2.0 * (rectangle.width + rectangle.height)
            }
        }
        any if any.is::&lt;Circle&gt;() =&gt; {                                //4
            let circle = any.downcast_ref::&lt;Circle&gt;().unwrap();       //5
            2.0 * std::f64::consts::PI * circle.radius
        }
        _ =&gt; panic!()
    }
}</pre>

1. Rust's type system doesn't offer a way to get the type of a variable. We must create a dedicated function for that.
2. Implement the function for structures
3. Match on the underlying structure type
4. Check the type
5. Downcast to the underlying structure to use its fields

This artificial code port above misrepresents Rust's pattern-matching abilities. It applies in [many places](https://doc.rust-lang.org/book/ch19-01-all-the-places-for-patterns.html).

Conclusion {#h2-6-conclusion}
-----------------------------

Among all languages described in the post, Scala was the first to provide pattern-matching in `switch` clauses. For many years, it was the Grail that others tried to catch up with; Kotlin and Java have finally reached this stage.

Outside of the JVM, Python and Rust feature powerful pattern-matching capabilities.

With destructuring, pattern matching is a huge help to developers who want to write readable and maintainable code.

The complete source code for this post can be found on [GitHub](https://github.com/ajavageek/pattern-matching).

**To go further:**

* [Pattern matching in Java 23](https://docs.oracle.com/en/java/javase/23/language/pattern-matching.html)
* [Pattern matching in Kotlin](https://kotlinlang.org/docs/control-flow.html#when-expressions-and-statements)
* [Pattern matching in Scala](https://docs.scala-lang.org/tour/pattern-matching.html)
* [Match statement in Python](https://docs.python.org/3.10/tutorial/controlflow.html?highlight=match#match-statements)
* [Pattern matching in Rust](https://doc.rust-lang.org/book/ch19-01-all-the-places-for-patterns.html)

*** ** * ** ***

*Originally published at [A Java Geek](https://blog.frankel.ch/pattern-matching-different-languages/) on July 20^th^, 2025*
