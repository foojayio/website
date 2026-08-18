---
title: "Function Calculation Java Challenge"
date: "2021-09-01T10:10:19+00:00"
lastmod: "2021-09-01T10:10:21+00:00"
description: "The \"Function\" functional interface is a first-class citizen function, so we can pass a function to a method and declare it as a variable."
authors:
  - "rafael-del-nero"
image: "function.png"
categories:
  - "Tutorials"
tags:
related_posts:
  - "asynchronous-completablefuture-san-francisco-adventure-java-challenge"
  - "daemon-thread-java-code-quiz"
  - "stream-limit-filter-java-challenge"
  - "neo-stream-search-java-challenge"
frozen: false
---

Functional programming is a very powerful paradigm that makes code more concise and easier to understand.

In Java, the "Function" functional interface can be used as a first-class citizen function, meaning that we can pass a function to a method and declare it as a variable, giving the developer a lot of power.

And now it's time for you now to test your abilities with lambdas and functional interfaces! Get ready!

It's time to improve your Java skills with this Function Calculation Challenge...

### Function Calculation Java Challenge

What will be the output when the main method is executed as follows?

```java
import java.util.function.Function;

public class FunctionChallenge {

    public static void main(String... doYourBest) {
        Function<Integer,Integer> add = x -> x + 2;
        Function<Integer,Integer> sub = x -> x - 2;
        Function<Integer,Integer> div = x -> x / 2;

        Function<Integer, Integer> func = add.andThen(sub).andThen(div);

        System.out.println(func.apply(2));
    }

}
```

A) 0  

B) java.lang.ArithmeticException will be thrown.  

C) 1

**Explanation**:

The interface `Function` in Java specifies the logic of functions you want to be executed in your program. You can see that this interface contains two generic types and, in our case, we are receiving an Integer and returning an Integer.

Lets first understand the lambda expression given below:

```java
x -> x+1
```

The lambda expression above (an anonymous function) takes an argument and returns a result after incrementing by one if x is type of Integer value.

We can give the type of input value and return value by using Function. In the Lambda expression, the x would be the type of the received parameter and there will be the logic we defined. It's basically adding 2 to the x variable:

```java
Function add = x -> x + 2;
```

Here we are subtracting 2 from the x variable:

```java
Function sub = x -> x - 2;
```

Finally here we are dividing the x by 2:

```java
Function div = x -> x / 2;
```

In the line of code below, we are joining all logic to be executed in a row. That means that we are going to add and then subtract and divide:

```java
Function func = add.andThen(sub).andThen(div);
```

With all functions in the func variable, we are going to use the apply method, passing 2. In the add method, it will be `2 + 2 = 4`, in the subtract method = it will be `4 - 2 = 2` and in the divide method = it will be `2 / 2 = 1`.

```java
System.out.println(func.apply(2));
```

Therefore, the final answer will be... what do you think?

That's it challenger, keep breaking your Java limits, and solving Java Challenges!

If you want to see the original post, go here: [https://javachallengers.com/function-calculation-java-challenge/](https://javachallengers.com/function-calculation-java-challenge/ "https://javachallengers.com/function-calculation-java-challenge/")
