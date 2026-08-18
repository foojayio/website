---
title: "Arrays and Object Reference Java Challenge Code Quiz"
date: "2021-09-29T11:18:13+00:00"
lastmod: "2021-09-29T11:18:14+00:00"
description: "When we pass an object reference to a method, we are changing the object that is in the heap of the memory. Find out more in this challenge!"
authors:
  - "rafael-del-nero"
image: "Favicon-3-2.png"
categories:
  - "Books"
related_posts:
  - "asynchronous-completablefuture-san-francisco-adventure-java-challenge"
  - "daemon-thread-java-code-quiz"
  - "transitioning-to-java-my-first-book"
frozen: false
---

Arrays are objects in Java.

Variables in Java actually store references to the object, instead of the real object.

When we pass an object reference to a method, we are changing the object that is in the heap of the memory.

Considering the explanation above, can you solve the following **Java Challenge**? What is going to happen when you run the following code?

```java
public class ArrayChallenge2 {
    public static void main(String... doYourBest) {
        int[] anyArray = new int[5];
        anyArray[0] = 0;
        anyArray[1] = 2;
        anyArray[2] = 4;
        anyArray[3] = 6;
        anyArray[4] = 8;

        int[] otherArray = anyArray;
        doSum(anyArray);
        doSum(otherArray);

        Arrays.stream(anyArray).forEach(System.out::println);
    }
    private static void doSum(int[] anyArray) {
        for (int i = 0; i < anyArray.length; i++) {
            anyArray[i] = anyArray[i] + 2;
        }
    }
}
```

a) 0  

2  

4  

6  

8

b) 4  

6  

8  

10  

12

c) 2  

4  

6  

8  

10

d) 0  

0  

0  

0  

0

e) ArrayOutOfBoundsException will be thrown

### Warning: Try to answer the correct alternative BEFORE seeing the answer! Only then you will refine your Java skills!

The correct answer is... what do you think?

Now let's understand what happens in the code above!

The key concept here is that we are using object references instead of real objects!

After executing the following code:

```java
int[] anyArray = new int[5]; // We are instantiating an Array object here.
int[] otherArray = anyArray; // We are assigning the reference to the same Array object.
// ….we have 2 variables pointing to the same object.
```

Arrays in Java are objects, so when we pass an Array to a method we are accessing the object. Any change made inside the doSum method is going to reflect the Array object. Therefore, we will be applying the sum of 2 on the same Array object twice.

At the line we are printing the result of this Java Challenge, we are transforming anyArray to a stream. Then we are using the forEach method from the Stream class to print each element from anyArray. We are using the concept of method reference which is a syntax sugar to use less code and do more in Java. In short, the System.out::println code will print every element from anyArray that is being iterated in the stream.

```java
Arrays.stream(anyArray).forEach(System.out::println);
```

If you prefer, you can watch the FULL explanation video about several Java quizzes on the Java Challengers channel:

{{< youtube cj43FTLsUU0 >}}

Don't forget to stay constantly breaking your limits! Don't hesitate to put a comment if you have any questions!

You can also check the original Java Challenge and take a look at the whole content of the Java Challengers initiative:  
<https://javachallengers.com/arrays-and-object-reference-java-challenge/>
