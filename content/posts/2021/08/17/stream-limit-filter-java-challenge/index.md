---
title: "Stream Limit Filter Java Challenge"
slug: "stream-limit-filter-java-challenge"
date: "2021-08-17T07:00:03+00:00"
lastmod: "2021-09-03T09:38:47+00:00"
description: "When you learn how to use streams, your Java code will be much better, and knowing how to limit and filter data with streams is crucial!"
authors:
  - "rafael-del-nero"
image: "Screen-Shot-2021-06-30-at-18.27.27-1024x674-1.png"
categories:
  - "Tutorials"
tags:
related_posts:
  - "function-calculation-java-challenge"
  - "jedi-lambda-join-java-challenge"
  - "type-erasure-generics-java-challenge"
  - "soprano-ofnullable-stream-java-challenge"
enlighterjs: true
frozen: false
---

To manipulate data from a collection by using a Java stream is handy and cleaner than the alternatives.

When you learn how to use streams, your Java code will be much better, and knowing how to limit and filter data with streams is crucial for you to do something useful with streams in Java.

Without further ado, let's go to the next Java Challenge!

It's time to improve your Java skills with this Limit Stream Challenge.

Limit Stream Java Challenge {#h2-0-limit-stream-java-challenge}
---------------------------------------------------------------

What will happen after the main method below is executed?

```java
import java.util.stream.IntStream;

public class LimitStreamChallenger {

    public static void main(String... doYourBest) {
        IntStream.iterate(0, i -> i + 1)
                .limit(5)
                .filter(i -> i % 2 == 0)
                .forEach(System.out::print);

        IntStream.iterate(0, i -> i + 1)
                .peek(System.out::print)
                .filter(i -> i % 2 == 0)
                .limit(3)
                .forEach(System.out::print);
    }

}
```


A) 024002244  

B) 02400122344  

C) 02401234  

D) 02401234

**Explanation**:

Let's analyze the stream code:

```
IntStream.iterate(0, i -> i + 1).limit(5).filter(i -> i % 2 == 0)                 
.forEach(System.out::print);
```


Note that we are using an infinite stream at the iterate line, then we limit our stream to 5 elements and filter it by even numbers and finally print all of them. Since we limit the numbers to 5, we will have our stream iterating in a range from 0 to 4 on our stream. Then we filter it by even numbers and we finally print the elements and the first result will be 024.

In the second stream, we use a very similar structure to the first stream, but the difference is the "peek" method and the order of the filter and limit methods. The peek method will print all the elements on the conditions of this stream. Remember that the peek method is meant to debug and help us understand what is happening in the steam. Then we will filter the stream elements to the even numbers, we will have here 0, 2 and 4 since we are limiting by 3.

Note that in this stream, we do have 3 elements, which is because the limit is being performed on top of the filter method.

As the peek method will print all elements, the filtered and non-filtered, the final output will be... what do you think?

That's it challenger, keep breaking your limits and stay consistent with your goals!
