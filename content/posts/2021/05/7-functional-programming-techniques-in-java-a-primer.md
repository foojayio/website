---
title: "7 Functional Programming Techniques in Java: A Primer"
slug: "7-functional-programming-techniques-in-java-a-primer"
date: "2021-05-11T07:34:52+00:00"
lastmod: "2021-08-23T12:22:16+00:00"
description: "In this article I'm going to focus on things that you can do in Java which are in line with functional programming concepts."
canonical: "https://deepu.tech/functional-programming-in-java-for-beginners/"
authors:
  - "deepu-sasidharan"
image: "https://thepracticaldev.s3.amazonaws.com/i/zf8mvgix8icaz64mgj89.png"
categories:
  - "Uncategorized"
tags:
related_posts:
enlighterjs: true
frozen: false
---

![header image](https://thepracticaldev.s3.amazonaws.com/i/zf8mvgix8icaz64mgj89.png)

There is a lot of hype around functional programming (FP) and a lot of cool kids are doing it but it is not a silver bullet. Like other programming paradigms/styles, functional programming also has its pros and cons and one may prefer one paradigm over the other. If you are a Java developer and wants to venture into functional programming, do not worry, you don't have to learn functional programming oriented languages like Haskell or Clojure(or even Scala or JavaScript though they are not pure functional programming languages) since Java has you covered and this post is for you.

I'm not going to dive into all functional programming concepts in detail, instead, I'm going to focus on things that you can do in Java which are in line with functional programming concepts. I'm also not going to discuss the pros and cons of functional programming in general.

*** ** * ** ***

What is functional programming? {#h2-0-what-is-functional-programming}
----------------------------------------------------------------------

As per Wikipedia,
> Functional programming is a programming paradigm---a style of building the structure and elements of computer programs---that treats computation as the evaluation of mathematical functions and avoids changing-state and mutable data.

Hence in functional programming, there are two very important rules

* **No Data mutations**: It means a data object should not be changed after it is created.
* **No implicit state**: Hidden/Implicit state should be avoided. In functional programming state is not eliminated, instead, its made visible and explicit

This means:

* **No side effects**: A function or operation should not change any state outside of its functional scope. I.e, A function should only return a value to the invoker and should not affect any external state. This means programs are easier to understand.
* **Pure functions only**: Functional code is idempotent. A function should return values only based on the arguments passed and should not affect(side-effect) or depend on global state. Such functions always produce the same result for the same arguments.

Apart from these there are functional programming concepts below that can be applied in Java, we will touch upon these further down.

* [Higher-order-functions](https://en.wikipedia.org/wiki/Higher-order_function)
* [Closures](https://en.wikipedia.org/wiki/Closure_(computer_programming))
* [Currying](https://en.wikipedia.org/wiki/Currying)
* [Recursion](https://en.wikipedia.org/wiki/Recursion_(computer_science))
* [Lazy evaluations](https://en.wikipedia.org/wiki/Evaluation_strategy)
* [Referential transparency](https://en.wikipedia.org/wiki/Referential_transparency)

Using functional programming doesn't mean its all or nothing, you can always use functional programming concepts to complement Object-oriented concepts, especially in Java. The benefits of functional programming can be utilized whenever possible regardless of the paradigm or language you use. And that is exactly what we are going to see.

*** ** * ** ***

Functional programming in Java {#h2-1-functional-programming-in-java}
---------------------------------------------------------------------

So let us see how we can apply some of the functional programming concepts above in Java. We will be using Java 11 as it is the LTS version currently.

### First-class and higher-order functions {#h3-2-first-class-and-higher-order-functions}

First-class functions (function as a first-class citizen) means you can assign functions to variables, pass a function as an argument to another function or return a function from another. Unfortunately, Java doesn't support this and hence makes concepts like closures, currying and higher-order-functions less convenient to write.

The closest to first-class functions in Java is [Lambda expressions](https://www.geeksforgeeks.org/lambda-expressions-java-8/). There are also some built-in functional interfaces like `Function`, `Consumer`, `Predicate`, `Supplier` and so on under the `java.util.function` package which can be used for functional programming.

A function can be considered as a higher-order-function only if it takes one or more functions as parameters or if it returns another function as a result. The closest to higher-order-functions we can get in Java is using Lambda expressions and built-in Functional interfaces.

This is not the nicest looking way of doing higher-order-functions, but this is how it is in Java and its not that bad IMO.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class HocSample {
    public static void main(String[] args) {
        var list = Arrays.asList("Orange", "Apple", "Banana", "Grape");

        // we are passing an array and an anonymous inner class instance of FnFactory as arguments to mapForEach method.
        var out = mapForEach(list, new FnFactory&lt;String, Object&gt;() {
            @Override
            public Object execute(final String it) {
                return it.length();
            }
        });
        System.out.println(out); // [6, 5, 6, 5]
    }

    // The method takes an array and an instance of FnFactory as arguments
    static &lt;T, S&gt; ArrayList&lt;S&gt; mapForEach(List&lt;T&gt; arr, FnFactory&lt;T, S&gt; fn) {
        var newArray = new ArrayList&lt;S&gt;();
        // We are executing the method from the FnFactory instance
        arr.forEach(t -&gt; newArray.add(fn.execute(t)));
        return newArray;
    }

    @FunctionalInterface // this doesn't do anything it is just informative.
    public interface FnFactory&lt;T, S&gt; {
        // The interface defines the contract for the anonymous class
        S execute(T it);
    }
}</pre>

Fortunately, we can actually simplify the above example further using the built-in `Function` interface and using the lambda expression syntax.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class HocSample {
    public static void main(String[] args) {
        var list = Arrays.asList("Orange", "Apple", "Banana", "Grape");
        // we are passing the array and a lambda expression as arguments to mapForEach method.
        var out = mapForEach(list, it -&gt; it.length());
        // This can be further simplified to "mapForEach(list, String::length);", I'm writing the expanded version for readability
        System.out.println(out); // [6, 5, 6, 5]
    }

    // The method takes an array and an instance of Function as arguments (we have replaced the custom interface with the built-in one)
    static &lt;T, S&gt; ArrayList&lt;S&gt; mapForEach(List&lt;T&gt; arr, Function&lt;T, S&gt; fn) {
        var newArray = new ArrayList&lt;S&gt;();
        // We are executing the method from the Function instance
        arr.forEach(t -&gt; newArray.add(fn.apply(t)));
        return newArray;
    }
}</pre>

Using these concepts along with lambda expressions we can write closures and currying like below:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class ClosureSample {
    // this is a higher-order-function that returns an instance of Function interface
    Function&lt;Integer, Integer&gt; add(final int x) {
        // this is a closure, i.e, a variable holding an anonymous inner class instance of the Function interface
        // which uses variables from the outer scope
        var partial = new Function&lt;Integer, Integer&gt;() {
            @Override
            public Integer apply(Integer y) {
                // variable x is obtained from the outer scope of this method which is declared as final
                return x + y;
            }
        };
        // The closure function instance is returned here
        return partial;
    }

    public static void main(String[] args) {
        ClosureSample sample = new ClosureSample();

        // we are currying the add method to create more variations
        var add10 = sample.add(10);
        var add20 = sample.add(20);
        var add30 = sample.add(30);

        System.out.println(add10.apply(5)); // 15
        System.out.println(add20.apply(5)); // 25
        System.out.println(add30.apply(5)); // 35
    }
}</pre>

We can simplify this further with lambda expressions, like the below:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class ClosureSample {
    // this is a higher-order-function that returns an instance of Function interface
    Function&lt;Integer, Integer&gt; add(final int x) {
        // The lambda expression is returned here as closure
        // variable x is obtained from the outer scope of this method which is declared as final
        return y -&gt; x + y;
    }

    public static void main(String[] args) {
        ClosureSample sample = new ClosureSample();

        // we are currying the add method to create more variations
        var add10 = sample.add(10);
        var add20 = sample.add(20);
        var add30 = sample.add(30);

        System.out.println(add10.apply(5));
        System.out.println(add20.apply(5));
        System.out.println(add30.apply(5));
    }
}</pre>

There are also many built-in higher-order-functions in Java for example here is the sort method from `java.util.Collections`

<pre class="EnlighterJSRAW" data-enlighter-language="java">var list = Arrays.asList("Apple", "Orange", "Banana", "Grape");

// This can be simplified as "Collections.sort(list, Comparator.naturalOrder());", I'm writing the expanded version for readability
Collections.sort(list, (String a, String b) -&gt; {
    return a.compareTo(b);
});

System.out.println(list); // [Apple, Banana, Grape, Orange]</pre>

The Java stream API also provides many interesting higher-order-functions like forEach, map and so on.

### Pure functions {#h3-3-pure-functions}

As we saw already a pure function should return values only based on the arguments passed and should not affect or depend on global state. It is possible to do this in Java except for some cases when there are checked exceptions involved.

This is quite simple, take the below this is a pure function. It will always return the same output for the given input and its behavior is highly predictable. We can safely cache the method if needed.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public static int sum(int a, int b) {
    return a + b;
}</pre>

If we add an extra line in this function, the behavior becomes unpredictable as it now has a side effect that affects an external state.

<pre class="EnlighterJSRAW" data-enlighter-language="java">static Map map = new HashMap&lt;String, Integer&gt;();

public static int sum(int a, int b) {
    var c = a + b;
    map.put(a + "+" + b, c);
    return c;
}</pre>

So try to keep your functions pure and simple.

### Recursion {#h3-4-recursion}

Functional programming favors recursion over looping. In Java, this can be achieved either by using the stream API or by writing recursive functions. Let us see an example for calculating the factorial of a number.

I also ran a benchmark on these using [JMH](http://tutorials.jenkov.com/java-performance/jmh.html) and mentioned the nanoseconds/operation below

In traditional iterative approach:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class FactorialSample {
    // benchmark 9.645 ns/op
    static long factorial(long num) {
        long result = 1;
        for (; num &gt; 0; num--) {
            result *= num;
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(factorial(20)); // 2432902008176640000
    }
}</pre>

The same can be done using recursion as below which is favored in functional programming.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class FactorialSample {
    // benchmark 19.567 ns/op
    static long factorialRec(long num) {
        return num == 1 ? 1 : num * factorialRec(num - 1);
    }

    public static void main(String[] args) {
        System.out.println(factorialRec(20)); // 2432902008176640000
    }
}</pre>

The downside of the recursive approach is that it will be slower compared to an iterative approach most of the times(The advantage we are aiming for is code simplicity and readability) and might result in stack overflow errors since every function call needs to be saved as a frame to the stack. To avoid this tail recursion is preferred, especially when the recursion is done too many times. In tail recursion, the recursive call is the last thing executed by the function and hence the functions stack frame need not be saved by the compiler. Most compilers can optimize the tail recursion code the same way iterative code is optimized hence avoiding the performance penalty. Java compiler, unfortunately, does not do this optimization 🙁

Now using tail recursion the same function can be written as below, but Java doesn't optimize this, though there are [workarounds](https://blog.knoldus.com/tail-recursion-in-java-8/), still it performed better in benchmarks.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class FactorialSample {
    // benchmark 16.701 ns/op
    static long factorialTailRec(long num) {
        return factorial(1, num);
    }

    static long factorial(long accumulator, long val) {
        return val == 1 ? accumulator : factorial(accumulator * val, val - 1);
    }

    public static void main(String[] args) {
        System.out.println(factorialTailRec(20)); // 2432902008176640000
    }
}</pre>

We can also use the Java stream library for recursion but its slower than normal recursion at the moment.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class FactorialSample {
    // benchmark 59.565 ns/op
    static long factorialStream(long num) {
        return LongStream.rangeClosed(1, num)
                .reduce(1, (n1, n2) -&gt; n1 * n2);
    }

    public static void main(String[] args) {
        System.out.println(factorialStream(20)); // 2432902008176640000
    }
}</pre>

Consider using stream API or recursion when writing Java code for readability and immutability, but if performance is critical or if the number of iterations will be huge use standard loops.

### Lazy evaluation {#h3-5-lazy-evaluation}

Lazy evaluation or non-strict evaluation is the process of delaying evaluation of an expression until it is needed. In general, Java does strict evaluation but for operands like `&&`, `||` and `?:` it does a lazy evaluation. We can utilize this to do lazy evaluations when writing java code.

Take this example where Java eagerly evaluates everything.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class EagerSample {
    public static void main(String[] args) {
        System.out.println(addOrMultiply(true, add(4), multiply(4))); // 8
        System.out.println(addOrMultiply(false, add(4), multiply(4))); // 16
    }

    static int add(int x) {
        System.out.println("executing add"); // this is printed since the functions are evaluated first
        return x + x;
    }

    static int multiply(int x) {
        System.out.println("executing multiply"); // this is printed since the functions are evaluated first
        return x * x;
    }

    static int addOrMultiply(boolean add, int onAdd, int onMultiply) {
        return (add) ? onAdd : onMultiply;
    }
}</pre>

This will produce the below output and we can see that both functions are executed always.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">executing add
executing multiply
8
executing add
executing multiply
16</pre>

We can use lambda expressions and higher-order-functions to rewrite this into a lazily evaluated version:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class LazySample {
    public static void main(String[] args) {
        // This is a lambda expression behaving as a closure
        UnaryOperator&lt;Integer&gt; add = t -&gt; {
            System.out.println("executing add");
            return t + t;
        };
        // This is a lambda expression behaving as a closure
        UnaryOperator&lt;Integer&gt; multiply = t -&gt; {
            System.out.println("executing multiply");
            return t * t;
        };
        // Lambda closures are passed instead of plain functions
        System.out.println(addOrMultiply(true, add, multiply, 4));
        System.out.println(addOrMultiply(false, add, multiply, 4));
    }

    // This is a higher-order-function
    static &lt;T, R&gt; R addOrMultiply(
            boolean add, Function&lt;T, R&gt; onAdd,
            Function&lt;T, R&gt; onMultiply, T t
    ) {
        // Java evaluates expressions on ?: lazily hence only the required method is executed
        return (add ? onAdd.apply(t) : onMultiply.apply(t));
    }
}</pre>

This outputs the below and we can see that only required functions were executed:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">executing add
8
executing multiply
16</pre>

### Type system {#h3-6-type-system}

Java has a strong type system and with the introduction of the `var` keyword it now also has pretty decent type inference. The only thing missing compared to other functional programming languages are case classes. There are proposals for [value classes](http://cr.openjdk.java.net/~jrose/values/values-0.html) and case classes for future Java versions. Let's hope they make it.

### Referential transparency {#h3-7-referential-transparency}

From Wikipedia:
> Functional programs do not have assignment statements, that is, the value of a variable in a functional program never changes once defined. This eliminates any chances of side effects because any variable can be replaced with its actual value at any point of execution. So, functional programs are referentially transparent.

Unfortunately, there are not many ways to limit data mutation in Java, however by using pure functions and by explicitly avoiding data mutations and reassignment using other concepts we saw earlier this can be achieved. For variables, we can use the [`final`](https://www.geeksforgeeks.org/final-keyword-java/) keyword which is a non-access modifier to avoid mutations by reassignments.

For example, the below will produce an error at compilation:

<pre class="EnlighterJSRAW" data-enlighter-language="java">final var list = Arrays.asList("Apple", "Orange", "Banana", "Grape");

list = Arrays.asList("Earth", "Saturn");</pre>

But this will not help when variables are holding references to other objects, for example, the below mutation will work irrespective of the final keyword.

<pre class="EnlighterJSRAW" data-enlighter-language="java">final var list = new ArrayList&lt;&gt;();

list.add("Test");
list.add("Test 2");</pre>

`final` keyword allows the internal state of referenced variables to be mutated and hence from a functional programming perspective `final` keyword is useful only for constants and to catch reassignments.

### [Data structures](https://en.wikipedia.org/wiki/Purely_functional_data_structure) {#h3-8-data-structures}

When using functional programming techniques it is encouraged to use functional data types such as Stacks, Maps and Queues.  

Hence maps are better than arrays or hash sets in functional programming as data stores.

*** ** * ** ***

Conclusion {#h2-9-conclusion}
-----------------------------

This is just an introduction for those who are trying to apply some functional programming techniques in Java. There are lot more that can be done in Java and Java 8 added a lot of API to make it easy to do functional programming in Java, like the stream API, Optional interface, functional interfaces and so on.

As I said earlier, functional programming is not a silver bullet but it offers a lot of useful techniques for more understandable, maintainable and testable code. It can co-exist perfectly well with imperative and object-oriented programming styles. In fact, we all should be using the best of everything.

Here is a video from a meetup, I presented, covering this content.

{{< youtube 5dBw4madIpo >}}

<br />

*** ** * ** ***

I hope you find this useful. If you have any question or if you think I missed something please add a comment.

If you like this article, please leave a like or a comment.

You can also follow me on [Twitter](https://twitter.com/deepu105) and [LinkedIn](https://www.linkedin.com/in/deepu05/)!

Originally published at [deepu.tech](https://deepu.tech/functional-programming-in-java-for-beginners/).
