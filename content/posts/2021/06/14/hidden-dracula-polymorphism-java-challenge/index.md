---
title: "Hidden Dracula Polymorphism Java Challenge"
slug: "hidden-dracula-polymorphism-java-challenge"
date: "2021-06-14T11:26:37+00:00"
lastmod: "2021-08-23T12:29:05+00:00"
description: "Polymorphism, the ability of an object to take on forms, one of the important Java concepts, decouples responsibilities, adding flexibility."
authors:
  - "rafael-del-nero"
image: "dracula_polymorphism.png"
categories:
  - "Tutorials"
tags:
related_posts:
  - "daemon-thread-java-code-quiz"
  - "function-calculation-java-challenge"
  - "stream-limit-filter-java-challenge"
  - "neo-stream-search-java-challenge"
enlighterjs: true
frozen: false
---

Polymorphism is one of the most important Java concepts. We might not even be aware we're using it all the time but when we instantiate an `ArrayList` and assign it to a variable of the `List` type, we are using polymorphism. It's a powerful concept because it helps us to decouple responsibilities, therefore, making code more flexible and easier to maintain.

This Java Challenge is one of the hardest ones, are you ready for this one? Now is your time to improve your Java skills by doing this Java Challenge!

It's time to improve your Java skills with this Hidden Dracula Polymorphism Challenge...

Hidden Dracula Polymorphism Challenge
-------------------------------------

What will happen after running the following code?

```java
public class DraculaPolymorphism {
    public static void main(String... doYourBest) {
        Dracula dracula = new Alucard();

        System.out.println(dracula.name + dracula.getName());
        System.out.println(dracula.metamorphosis());
    }

    static class Dracula {
        String name = "Dracula";

        static String metamorphosis() { return "werewolf";}
        String getName(){
            return this.name;
        }
    }
    static class Alucard extends Dracula {
        String name = "Alucard";

        static String metamorphosis() { return "bat";}
        String getName(){
            return this.name;
        }
    }
}
```


A) AlucardAlucardbat  

B) DraculaAlucardwerewolf  

C) DraculaDraculawerewolf  

D) AlucardAlucardwerewolf

**Explanation**:

Let's analyze the code:

The very first thing to notice is that we are using polymorphism in the line of the code where we declare the parent class but instantiate it with the child class, which enables us to use polymorphic methods:

```java
Dracula dracula = new Alucard();
```


Then we will be using the `name` attribute but remember that attributes will never be overridden and there is no polymorphism with attributes, only with instance methods. Therefore the name that will be printed will be from the Dracula class since we declared the `dracula` variable with the type of `Dracula`. And the `getName` method will be from the `Alucard` class, since we are using polymorphism and `getName` is an instance method:

```java
System.out.print(dracula.name + dracula.getName());
```


Therefore it will print... what do you think?

Finally, we invoke the static method, keeping in mind that a static method is not an instance method, it's a class method and it doesn't depend on an instance to work. For that reason, polymorphism won't happen even though we are using the same method name with the child class. The invoked method will be from the class we declared the `dracula` instance, therefore it will be the `Dracula` class:

```java
System.out.print(dracula.metamorphosis());
```


The information from the `Dracula` class will be printed then... and that information is... what do you think?

In conclusion, the final output will be...

If you want to go deeper into polymorphism, you can check out the following article: [Polymorphism and inheritance in Java](https://www.infoworld.com/article/3290403/java-challengers-3-polymorphism-and-inheritance.html "Polymorphism and inheritance in Java")

You can also watch the video explanation of this Java Challenge, however, I recommend trying out the Java Challenge yourself first, before watching the below!

{{< youtube X6V7RH8tIOw >}}

<br />

Keep learning and stay consistent.

If you want more Java Challenges with full explanations, check out the Java Challengers book: [Java Challengers Book](https://leanpub.com/javachallengers "Java Challengers Book")
