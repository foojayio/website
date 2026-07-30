---
title: "Getting Started with Java - Working with numbers"
description: "foojay is the place for all OpenJDK Update Release Information. Learn More."
canonical: "https://foojay.io/java-quick-start/quick-start-tutorial/working-with-numbers/"
url: "/java-quick-start/quick-start-tutorial/working-with-numbers/"
jdoodle: true
enlighterjs: true
aliases:
  - "/java-quick-start/quick-start-tutorial/working-with-numbers/"
frozen: false
---

*** ** * ** ***

[\<\< Using Arguments and String Arrays](https://foojay.io/java-quick-start/quick-start-tutorial/using-the-arguments/)  
[If, Then, Else \>\>](https://foojay.io/java-quick-start/quick-start-tutorial/if-then-else/)



{{< youtube ZzJP-NIi5zU >}}



Java has different types of numbers, of which we will use the following ones in this example.

* **Integer:** A rounded whole number.
* **Float:** A decimal that can hold 8 decimal numbers.
* **Double:** A decimal that can hold 16 decimal numbers

Depending on the type of number, they use more or less memory. In most cases, integers are used, but when you need to store e.g. prices, you would use a float. When assigning a value to either a float or a double, the number has to end with an "F" or "D" (see example). This helps Java to understand what you want to achieve.

We create both the example float and double with 20 decimals to see the number of decimals that are really stored in the variable.



<div data-pym-src="https://www.jdoodle.com/plugin" data-language="java" data-version-index="6" data-libs="mavenlib1, mavenlib2">
 public class NumberValues { public static void main (String[] args) { int intValue = 2; float floatValue = 1.12345678901234567890F; double doubleValue = 1.12345678901234567890D; System.out.println("Integer: " + intValue); System.out.println("Float: " + floatValue); System.out.println("Double: " + doubleValue); System.out.println("Multiply: " + (intValue * floatValue) + ", rounded: " + Math.round(intValue * floatValue)); } }
</div>



*** ** * ** ***

When we run this code, you see how many decimals are stored in the values and also how you can round a value.



<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="dracula" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ java NumberValues.java

Integer: 2
Float: 1.1234568
Double: 1.1234567890123457
Multiply: 2.2469137, rounded: 2</pre>

  
[\<\< Using Arguments and String Arrays](https://foojay.io/java-quick-start/quick-start-tutorial/using-the-arguments/)  
[If, Then, Else \>\>](https://foojay.io/java-quick-start/quick-start-tutorial/if-then-else/)
