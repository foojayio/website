---
title: "Parallel Streams Java Code Quiz"
date: "2021-09-15T09:04:38+00:00"
lastmod: "2021-09-15T09:07:43+00:00"
description: "Using streams concurrently with the parallel method can optimize performance. However, what about when we depend on the order of execution?"
authors:
  - "rafael-del-nero"
image: "Screenshot-2021-03-24-at-18.57.16-1128x484-1.png"
categories:
  - "Books"
tags:
related_posts:
  - "transitioning-to-java-my-first-book"
  - "arrays-and-object-reference-java-challenge-code-quiz"
frozen: false
---

Using streams concurrently with the parallel method is a good idea to optimize performance.

However, it's not always the case that we can use the parallel method, for example, when we depend on the order of logic execution. When we can process the method concurrently, the parallel method comes in handy.

In the following Java Challenge, we will explore the use of parallel streams with the forEachOrdered method!

It's time to improve your Java skills with this Parallel Stream Java Challenge

## Parallel Stream Java Challenge

What will happen in the following code when running the main method?

```java
import java.util.List;

public class ParallelChallenge {
    public static void main(String... doYourBest) {
        List<Simpson> simpsons = List.of(new Simpson(10),
                new Simpson(15), new Simpson(11), new Simpson(20),
                new Simpson(22));

        simpsons.stream().parallel()
                .filter(s -> s.age > 10)
                .map(s -> s + ",")
                .forEachOrdered(System.out::print);

        simpsons.stream().parallel()
                .filter(s -> s.age > 10)
                .map(s -> s + ",")
                .forEach(System.out::print);
    }

    static class Simpson {
        int age;
        public Simpson(int age) {
            this.age = age;
        }
        public String toString() {
            return ""+ this.age;
        }
    }
}
```

A) 11,15,20,22, 15,11,20,22,

B) 11,15,20,22, Random values will be printed in the second Stream

C) 11,15,20,22, 11,20,15,22,

D) 15,11,20,22, Random values will be printed in the second Stream

**Explanation**

Let's analyze both Streams.

```java
simpsons.stream().parallel()
                 .filter(s -> s.age > 10)
                 .map(s -> s + ",")
                 .forEachOrdered(System.out::print);
```

```java

```

The parallel method means that the Stream will iterate the elements concurrently for improved performance.

Then it will filter all "simpsons" objects that have an age greater than 10, map each age with a "," and then when we use the "forEachOrdered" method, the elements will be printed in the order that the Stream started.

The output from the first Stream will therefore be... what do you think?

And here is the second Stream:

```java
simpsons.stream().parallel()
                 .filter(s -> s.age > 10)
                 .map(s -> s + ",")
                 .forEach(System.out::print);
```

The only difference here is that we are printing the elements without any order. When we use the parallel method, the order of the elements will be changed, because it's a parallel process by means of which elements are processed at the same time, for better performance.

So the output of the second will be... what do you think?

In conclusion, the correct alternative between A, B, C, and D above is... what do you think?

You can also watch the full video explanation in the following video, but I encourage you to try the Java Challenge by yourself first!

{{< youtube gamlYJU2abY >}}

That's it challenger, rock on! Keep taking action and relentlessly break your limits! Don't hesitate to leave a comment with a question if anything is not clear!

To check the original Java Challengers post, access the following link: <https://javachallengers.com/parallel-streams-java/>
