---
title: "Join The Jedi Lambda Join Java Challenge!"
slug: "jedi-lambda-join-java-challenge"
date: "2021-07-21T08:08:24+00:00"
lastmod: "2021-08-23T12:17:12+00:00"
description: "Are you ready for the next Java Challenge? Less introduction, more action, and master Java concepts by having fun!"
authors:
  - "rafael-del-nero"
image: "lambda_join.png"
categories:
  - "Tutorials"
tags:
related_posts:
  - "daemon-thread-java-code-quiz"
  - "method-reference-vs-lambda-java-challenge"
  - "asynchronous-completablefuture-san-francisco-adventure-java-challenge"
  - "function-calculation-java-challenge"
enlighterjs: true
frozen: false
---

There are many concepts involved in Java Challenge! In essence, we will explore lambdas and the Function interface the most. However, we also have static methods introduced in Java 8 the private method in interfaces introduced in Java 9. In the invocation of the interface methods, we are using anonymous inner classes too!

Are you ready for this Java Challenge? Less introduction, more action, try out this Java Challenge, and master Java concepts by having fun!

It's time to improve your Java skills with this Join Jedi Lambda

Join Jedi Lambda Java Challenge
-------------------------------

By analyzing the following code, what do you think will happen when running it?

```java
import java.util.function.Function;

public class JediLambdaJoinChallenge {

    interface Jedi {
        String MASTER = "Yoda";

        default String attack() {
            return jump(jedi ->
                    String.join(jedi, useSaber(), useForce()));
        }

        private String jump(Function<String, String> function) {
            return function.apply("Luke");
        }

        private static String useSaber() {
            return "S";
        }

        private String useForce() {
            return "F";
        }

    }

    public static void main(String... starWars) {
        System.out.println(new Jedi() {
            public String useForce() {
                return "X";
            }
        }.attack() + Jedi.useSaber() + Jedi.MASTER);
    }

}
```


A) SLukeFSYoda  

B) Compilation error at line 28  

C) LukeXYoda  

D) LukeSFSYoda

**Explanation**:

Let's analyse this code:

```java
System.out.println(new Jedi() {
		public String useForce() {
			return "X";
		}
	}.attack() + Jedi.useSaber() + Jedi.MASTER);
```


Note that the useForce method is being overridden inside an anonymous inner class. An anonymous inner class is basically a class that doesn't have a name implementing in our case the Jedi interface. Then we are invoking the attack method.

```java
default String attack() {
	return jump(jedi -> String.join(jedi, useSaber(), useForce()));
}
```


There is a trick here in the join method. The first argument of the join method is a delimiter. As we are returning Luke in this code:

```java
private String jump(Function function) {
	return function.apply("Luke");
}
```


Luke will be the delimiter, and the useSaber, useForce methods are going to simply return a String. So the first part of the String will be: `SLukeF`. Then the `Jedi.useSaber()` method will be invoked, and note that this method is being invoked in a static way. So the method we overrode won't be invoked, this method will return `S`. Finally, the constant `Jedi.MASTER` will be printed with the final input: `SLukeFSYoda`

FULL Video Explanation (Try out the challenge **before** watching the video)

That's it challenger, keep solving the Java Challenges to take your Java skills to another level! By understanding the main Java features, you can create high-quality code where bugs have a hard time hiding.
