---
title: "Take the Type Erasure Generics Java Challenge!"
slug: "type-erasure-generics-java-challenge"
date: "2021-05-25T06:50:45+00:00"
lastmod: "2021-08-23T12:35:40+00:00"
description: "In this challenge, you will see the generic type that will be erased by the compiler and will be replaced by the type we defined at runtime."
authors:
  - "rafael-del-nero"
image: "https://foojay.io/wp-content/uploads/2021/05/generics.png"
categories:
  - "Tutorials"
tags:
related_posts:
enlighterjs: true
frozen: false
---

It's possible to use type erasure generics in a method with Java. To know how to use generics is important because then you are able to create highly reusable code.

In the following Java Challenge, you will see the generic type that will be erased by the compiler and then will be replaced by the type we defined at runtime.

Note also that we are using the extends keyword which means that the generic type will extend the other type.

Without further ado, it's time to solve the Java Challenge quiz!

It's time to improve your Java skills with this Type Erasure Generics Simpson Java Challenge.

Type Erasure Generics Simpson Java Challenge {#h2-0-type-erasure-generics-simpson-java-challenge}
-------------------------------------------------------------------------------------------------

What will happen when running the following code?

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings(value = "all")
public class GenericsChallenge3 {

    public static void main(String... doYourBest) {
        List&lt;String&gt; firstResult =  GenericsChallenge3.&lt;String&gt;get
                (new ArrayList&lt;&gt;(), new String("1"));

        List&lt;Object&gt; secondResult = GenericsChallenge3.
                get("Homer", Double.valueOf("4"));

        Stream&lt;Object&gt; stream = Stream.concat(firstResult.stream(),
                secondResult.stream());
        stream.forEach(System.out::println);
    }

    public static &lt;T&gt; List&lt;T&gt; get(List&lt;T&gt; list, T t) {
        list.add(t);
        return list;
    }

    public static &lt;T, R extends T&gt; List&lt;T&gt; get(T type1, R type2) {
        List&lt;T&gt; list = new ArrayList&lt;&gt;();
        list.add(type1);
        list.add(type2);
        return list;
    }
}</pre>

A) There will be a ClassCastException in the line 27  

B)

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">1
Homer
4.0</pre>

<br />

C) It won't compile at line 9

**Explanation:**

Let's analyse the code:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;code&gt;List firstResult = GenericsChallenge3.get(new ArrayList(), new String("1"));&lt;/code&gt;</pre>

In the line above, we are invoking the get method passing an ArrayList and a String as parameters. It's going to work fine, T will be a String. Even though we are passing an ArrayList without a type, Java will implicitly pass a String since the second type is String. If the second type is Integer, the type of the ArrayList would be Integer as well.

Then we invoke this other method:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;code&gt;List secondResult = GenericsChallenge3.get("Homer", Double.valueOf("4"));&lt;/code&gt;</pre>

Note that we are passing two different types to those generic parameters:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;code&gt;public static List get(T type1, R type2) { ... }&lt;/code&gt;</pre>

The type T will become an Object since we are passing two different types. In order to make T compatible with the different types we are passing, the JVM will transform T into an object so that the elements can be inserted into the ArrayList.

And so the answer is.... what do you think?

That's it challenger, rock on! Keep taking action and relentlessly break your limits! Don't hesitate to leave a comment with a question if anything is not clear!

If you want to see the original post, go to the following link: [https://javachallengers.com/type-erasure-generic](https://javachallengers.com/type-erasure-generics)
