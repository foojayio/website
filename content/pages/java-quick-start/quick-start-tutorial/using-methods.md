---
title: "Getting Started with Java - Using Methods"
linkTitle: "Using Methods"
description: "Split a Java program into methods to keep the code simple, readable and maintainable long after you wrote it."
url: "/java-quick-start/quick-start-tutorial/using-methods/"
jdoodle: true
aliases:
  - "/java-quick-start/quick-start-tutorial/using-methods/"
frozen: false
weight: 7
---

{{< youtube KKxQMlTQtn4 >}}

Let's now use some different methods to keep the code simple and easy to read and understand. After all, even though you may write your code only once, you want it to remain maintainable forever.

Methods help you to organize your code. Each method does one specific thing, either with or without input values.

<div data-pym-src="https://www.jdoodle.com/plugin" data-language="java" data-version-index="6" data-libs="mavenlib1, mavenlib2">
 import java.text.SimpleDateFormat;
 import java.util.Date;
 public class UsingMethod {
     public static void main (String[] args) {
         System.out.println("2 x Raspberry Pi 4 4Gb, price: " + getTotal(2, 59.95F) + " Euro");
         System.out.println("Current date and time is: " + getNow());
     }
     public static float getTotal(int quantity, float price) {
         return quantity * price;
     }
     public static String getNow() {
         return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
     }
 }
</div>

1. First, look at the import statements at the top. Because we use methods that are not part of "basic Java", we need to tell our program which additional classes need to be imported.
2. Then two methods are defined. By calling these methods from the main method, we can keep the code in the main method very clean and readable.
   * "getTotal(int quantity, float price)" which returns a calculated value
   * "getNow()" which returns the current timestamp as a readable formatted String
3. Run the code, as shown below.

```
$ java UsingMethod.java

2 x Raspberry Pi 4 4Gb, price: 119.9000015258789 Euro
Current date and time is: 2022.12.09 21:35:23
```
