---
title: "Function Calculation Java Challenge | Foojay.io Today"
slug: "function-calculation-java-challenge"
date: "2021-09-01T10:10:19+00:00"
lastmod: "2021-09-01T10:10:21+00:00"
description: "The \"Function\" functional interface is a first-class citizen function, so we can pass a function to a method and declare it as a variable."
authors:
  - "rafael-del-nero"
image: "https://foojay.io/wp-content/uploads/2021/08/function.png"
categories:
  - "Tutorials"
tags:
related_posts:
  - "asynchronous-completablefuture-san-francisco-adventure-java-challenge"
  - "daemon-thread-java-code-quiz"
  - "stream-limit-filter-java-challenge"
  - "neo-stream-search-java-challenge"
enlighterjs: true
frozen: false
---

Functional programming is a very powerful paradigm that makes code more concise and easier to understand.

In Java, the "Function" functional interface can be used as a first-class citizen function, meaning that we can pass a function to a method and declare it as a variable, giving the developer a lot of power.

And now it's time for you now to test your abilities with lambdas and functional interfaces! Get ready!

It's time to improve your Java skills with this Function Calculation Challenge...

### Function Calculation Java Challenge {#h3-0-function-calculation-java-challenge}

What will be the output when the main method is executed as follows?

<pre class="EnlighterJSRAW" data-enlighter-language="java">import java.util.function.Function;

public class FunctionChallenge {

    public static void main(String... doYourBest) {
        Function&lt;Integer,Integer&gt; add = x -&gt; x + 2;
        Function&lt;Integer,Integer&gt; sub = x -&gt; x - 2;
        Function&lt;Integer,Integer&gt; div = x -&gt; x / 2;

        Function&lt;Integer, Integer&gt; func = add.andThen(sub).andThen(div);

        System.out.println(func.apply(2));
    }

}</pre>

A) 0  

B) java.lang.ArithmeticException will be thrown.  

C) 1

**Explanation**:

The interface `Function` in Java specifies the logic of functions you want to be executed in your program. You can see that this interface contains two generic types and, in our case, we are receiving an Integer and returning an Integer.

Lets first understand the lambda expression given below:

<pre class="EnlighterJSRAW" data-enlighter-language="java">x -&gt; x+1</pre>

The lambda expression above (an anonymous function) takes an argument and returns a result after incrementing by one if x is type of Integer value.

We can give the type of input value and return value by using Function. In the Lambda expression, the x would be the type of the received parameter and there will be the logic we defined. It's basically adding 2 to the x variable:

<pre class="EnlighterJSRAW" data-enlighter-language="java">Function add = x -&gt; x + 2;</pre>

Here we are subtracting 2 from the x variable:

<pre class="EnlighterJSRAW" data-enlighter-language="java">Function sub = x -&gt; x - 2;</pre>

Finally here we are dividing the x by 2:

<pre class="EnlighterJSRAW" data-enlighter-language="java">Function div = x -&gt; x / 2;</pre>

In the line of code below, we are joining all logic to be executed in a row. That means that we are going to add and then subtract and divide:

<pre class="EnlighterJSRAW" data-enlighter-language="java">Function func = add.andThen(sub).andThen(div);</pre>

With all functions in the func variable, we are going to use the apply method, passing 2. In the add method, it will be `2 + 2 = 4`, in the subtract method = it will be `4 - 2 = 2` and in the divide method = it will be `2 / 2 = 1`.

<pre class="EnlighterJSRAW" data-enlighter-language="java">System.out.println(func.apply(2));</pre>

Therefore, the final answer will be... what do you think?

That's it challenger, keep breaking your Java limits, and solving Java Challenges!

If you want to see the original post, go here: [https://javachallengers.com/function-calculation-java-challenge/](https://javachallengers.com/function-calculation-java-challenge/ "https://javachallengers.com/function-calculation-java-challenge/")
