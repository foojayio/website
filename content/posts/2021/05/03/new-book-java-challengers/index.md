---
title: "New Book Announcement: \"Java Challengers\""
slug: "new-book-java-challengers"
date: "2021-05-03T07:03:36+00:00"
lastmod: "2021-06-21T16:29:10+00:00"
description: "Newly released \"Java Challenges\" book is a way for you to challenge yourself with fun code challenges so you will be a better Java developer."
authors:
  - "rafael-del-nero"
image: "Favicon-3-2.png"
categories:
  - "Book Announcement"
  - "Books"
tags:
related_posts:
enlighterjs: true
frozen: false
---

To get the best jobs and create massive value, you need to know Java very well. The [newly released "Java Challengers" book](https://leanpub.com/javachallengers) is a way for you to challenge yourself with fun code challenges so that you will become a better Java developer.

This book contains more than 70 well-elaborated Java Challenges that will help you break your limits on your Java skills. Want to challenge yourself and become better? The Java Challengers is the book for you!

For each Java Challenge you get a full explanation to fully prepare you to beat the Java Challenge!

To tease you with the Java Challengers book, try out the following challenges and see if you can solve them. We've given a few clues to think about, should you need them, though not the answers themselves! Try and see if you can figure out the below, before running the code to see the answers. 🙂

### Jedi Covariant Polymorphism Challenge {#h3-0-jedi-covariant-polymorphism-challenge}

In this challenger, we have an abstract class and another class that extends it. The concept of covariant types is being used here. What do you think the output will be after the main method is compiled and run? Will the code compile?

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class YodaForce {
      public static void main(String... covariantType) {
          System.out.println(new Yoda().useSaber()); 
          System.out.println(new Yoda().attack()); 
          System.out.println(new Yoda().lightForce); // Line 5
      }
      static abstract class LightForce { 
          int lightForce;
          abstract Object useSaber(); 
          abstract long attack();

          LightForce() {
            lightForce++;
          }
      }
      static class Yoda extends LightForce { 
          String useSaber() {
            return "useSaber"; 
          }
          long attack() { 
            return 99999;
          } 
      }
}</pre>

**A.**   

useSaber  

99999  

3

**B.**   

Compilation error at line 6 useSaber

**C.**   

useSaber  

99999  

1  

**D.**   

RuntimeException

Will this code compile and run fine? The concepts demonstrated here are mostly about polymorphism. In the first method invocation, we are simply invoking the overridden useSaber method:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">System.out.println(new Yoda().useSaber());</pre>

Note that we are using a covariant return type for the overridden method: the Yoda subclass's useSaber method returns a String instead of an Object. This method prints "useSaber".

In the second method invocation we are invoking another overridden method, attack:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">System.out.println(new Yoda().attack());</pre>

We can't use a covariant type here because there is no inheritance between primitive types; both methods return a long. The output of this method call is 99999.

Finally, we print the lightForce variable that is incremented each time the LightForce class is instantiated:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">System.out.println(new Yoda().lightForce); // Line 5</pre>

Even though we instantiate the LightForce class three times, note that the lightForce variable is an instance variable. Therefore, the variable will be reset every time a new instance is created. We have three instantiations, but the result of this variable will be 1 for each of the three instances.

In conclusion, the correct answer is ...

### Mysterious Door Lambda Challenge {#h3-1-mysterious-door-lambda-challenge}

By running the following code, can you guess what will happen?

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import java.util.Arrays;
import java.util.List;

public class MysteriousDoorLambdaChallenge {

    public static void main(String... theDoors) {
        int doorNumber = 0;
        doorNumber++;
        List&lt;String&gt; doors = Arrays.asList("A", "B", "C");
        doors.forEach(e -&gt; {
            System.out.println(e + doorNumber); // # Line 11
        });
    }

}</pre>

**A.**   

A0  

B1  

C2

**B.**   

A1  

B2  

C3

**C.**   

Compilation Error at line 11  

**D.**   

Unpredictable

Lambdas can only access variables that are final or effectively final. As mentioned above in its essence should access only immutable data from outside.

Note that the doorNumber variable is being changed when we increment it. Therefore, the doorNumber variable is not effectively final anymore from that moment. Simply put, effectively final variables are variables that when given a value, this value will be never changed.

In conclusion, the correct answer is ...

### KeyMaker Overloading Challenge {#h3-2-keymaker-overloading-challenge}

The following challenger explores the concept of how primitive types are interpreted by the JVM when argument values are hardcoded. Can you figure out what will happen when this code is executed?

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public class KeyMakerOverloading {

    public static void main(String... primitiveOverloading) {
        makeKey(1);
        makeKey(1F);
        makeKey('1');
        makeKey(1.0);
    }

    static void makeKey(short shortCode) {
        System.out.println("short:" + shortCode);
    }

    static void makeKey(long longCode) {
        System.out.println("long:" + longCode);
    }

    static void makeKey(float floatCode) {
        System.out.println("float:" + floatCode);
    }
    static void makeKey(double floatCode) {
        System.out.println("double:" + floatCode);
    }
}</pre>

**A.**   

short:1  

float:1.0  

short:49  

double:1.0

**B.**   

long:1  

float:1.0  

short:1  

float:1.0

**C.**   

short:1  

float:1.0  

long:49  

float:1.0  

**D.**   

long:1  

float:1.0  

long:49  

double:1.0

Let's analyze the code. In the first invocation, we pass the hardcoded value 1:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">makeKey(1);</pre>

int is the default type interpreted to non-decimal numbers that fit within this type's range, so the JVM will convert this hardcoded number to int. But none of the overloaded methods takes an int, so the type will be promoted to a wider one. The first method the JVM finds that can accommodate this type is this one:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">static void makeKey(long longCode) { … }</pre>

Therefore, the first output will be:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">long:1</pre>

In the second method invocation, we are passing an explicit float because we are using the letter F after the number:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">makeKey(1F);</pre>

As we already have a corresponding method for the float type, the output for this invocation will be:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">float:1.0</pre>

In the third method invocation, we pass a char:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">makeKey('1');</pre>

There's no method that takes a char, so type promotion will again be used here. As the diagram in figure 5.1 shows, a char can be promoted to an int, which as we've already seen will be promoted further to a long in this example. The char value will be converted to the corresponding number in the ASCII table, which is 49. Therefore, the output will be:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">long: 49</pre>

Finally, the number 1.0 will be automatically converted by the JVM to the type double, and then it will invoke the method that receives a double:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">double:1.0</pre>

Therefore, the correct answer will be...

If you want more it's time to get the Java Challengers book for you and beat all the challenges! Are you ready for them? [Go here for more!](https://leanpub.com/javachallengers)
