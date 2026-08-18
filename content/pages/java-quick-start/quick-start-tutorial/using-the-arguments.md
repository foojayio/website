---
title: "Getting Started with Java - Using the Arguments"
linkTitle: "Using Arguments and String Arrays"
description: "Use the start-up arguments of the main method to learn Java arrays: their length, and how to loop through them."
canonical: "https://foojay.io/java-quick-start/quick-start-tutorial/using-the-arguments/"
url: "/java-quick-start/quick-start-tutorial/using-the-arguments/"
jdoodle: true
aliases:
  - "/java-quick-start/quick-start-tutorial/using-the-arguments/"
frozen: false
weight: 3
---

{{< youtube oKBQaesKJJI >}}

Let's go a little step further and use the start-up arguments assigned in the main method to learn how you can use arrays, check the array length, and loop through them.

We need to create another new Java file and call it `MainArguments.java`. The `args` variable of the main method is a String array and we will use it with the following code. You can try it out here, by typing in one or more words as "Input Arguments":

<div data-pym-src="https://www.jdoodle.com/plugin" data-language="java" data-version-index="6" data-libs="mavenlib1, mavenlib2">
 public class MainArguments {
     public static void main (String[] args) {
         System.out.println("Number of arguments: " + args.length);
         if (args.length &gt; 0) {
             System.out.println("First argument: " + args[0]);
         }
         for (int i = 0; i &lt; args.length; i++) {
             System.out.println("Argument " + (i + 1) + ": " + args[i]);
         }
     }
 }
</div>

Now we can start the application and provide it any number of extra arguments.

From the array, we can get the number of items (= `args.length`) and use a for-loop for cycling through all the arguments.

```
$ java MainArguments.java
Number of arguments: 0

$ java MainArguments.java "Hello World" "Bye"
Number of arguments: 2
First argument: Hello World
Argument 1: Hello World
Argument 2: Bye
```
