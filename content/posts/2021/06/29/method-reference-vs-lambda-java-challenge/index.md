---
title: "Method Reference VS Lambda Java Challenge"
slug: "method-reference-vs-lambda-java-challenge"
date: "2021-06-29T19:31:00+00:00"
lastmod: "2021-08-23T12:18:12+00:00"
description: "In this Java Challenge, we will explore how lambdas and method references behave so that you can really understand how they work!"
authors:
  - "rafael-del-nero"
image: "method_reference.png"
categories:
  - "Tutorials"
tags:
related_posts:
  - "jedi-lambda-join-java-challenge"
  - "asynchronous-completablefuture-san-francisco-adventure-java-challenge"
  - "daemon-thread-java-code-quiz"
  - "function-calculation-java-challenge"
enlighterjs: true
frozen: false
---

Do you know what the differences are between method references and lambdas?

In this Java Challenge, we will explore how lambdas and method references behave so that you can really understand how they work!

Now that you know the main context, it's time for the Java Challenge!

It's time to improve your Java skills with this Method Reference VS Lambda Challenge.

### Method Reference VS Lambda Challenge

What will happen when we run the following main method?

```java
public class MethodReferenceVSLambdaChallenge {

    public static void main(String... doYourBest) {
        Runnable universeImpactRunnable = () -> new ChuckNorris().roundHouseKick();
        Runnable galaxyImpactRunnable = new ChuckNorris()::roundHouseKick;

        System.out.print("The galaxy is finished = ");

        universeImpactRunnable.run();
        universeImpactRunnable.run();

        galaxyImpactRunnable.run();
        galaxyImpactRunnable.run();
    }

    static class ChuckNorris {
        private static int numberOfKicks;
        private int galaxyDamage;

        ChuckNorris() {
            this.galaxyDamage = numberOfKicks++;
        }

        void roundHouseKick() {
            System.out.print(this.galaxyDamage);
        }
    }

}
```

A) The galaxy is finished = 1234  

B) The galaxy is finished = 0123  

C) The galaxy is finished = 0100  

D) The galaxy is finished = 1200

**Explanation**:

Now that you know the main context, it's time for the Java Challenge!

Let's analyze the code:

```java
Runnable universeImpactRunnable = () -> new ChuckNorris().roundHouseKick();
Runnable galaxyImpactRunnable = new ChuckNorris()::roundHouseKick;
```

There is a crucial difference between lambda and method reference. Lambdas are lazy and they will invoke the class constructor only when the method is invoked. On the other hand, with method reference, the constructor will be invoked right away only where the method reference is assigned, not on the method invocation.

So, at this line the constructor is not invoked:

```java
Runnable universeImpactRunnable = () -> new ChuckNorris().roundHouseKick();
```

And, at this line the constructor is invoked:

```java
Runnable galaxyImpactRunnable = new ChuckNorris()::roundHouseKick;
```

With that in mind, let's analyze the invoked methods:

```java
universeImpactRunnable.run();
universeImpactRunnable.run();
```

With lambdas, every time the run method is invoked, the constructor will be invoked, which means that the numberOfKicks variable will be increased. So the value from galaxyDamage will be:

```
12
```

Then when we invoke those methods:

```java
galaxyImpactRunnable.run();
galaxyImpactRunnable.run();
```

The value will be 0 because remember that the constructor will be only invoked one and it was already invoked at the moment of the method reference declaration.

By knowing that, and also remembering that the post ++ operator increases a value after the line being processed. Then the values will be:

```
00
```

The final result will be then... what do you think?

If you want to watch the video explanation, check it out, but I recommend trying out the Java Challenge first!

{{< youtube P4R8dapfap8 >}}

That's it challenger, rock on! Keep taking action and relentlessly break your limits! Don't hesitate to leave a comment with a question if anything is not clear!

If you want to see the original post, go to the following link:

[https://javachallengers.com/method-reference-vs-lambda-java-challenge/](https://javachallengers.com/method-reference-vs-lambda-java-challenge/ "https://javachallengers.com/method-reference-vs-lambda-java-challenge/")
