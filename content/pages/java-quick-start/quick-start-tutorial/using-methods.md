---
title: "Getting Started with Java - Using Methods"
description: "foojay is the place for all OpenJDK Update Release Information. Learn More."
canonical: "https://foojay.io/java-quick-start/quick-start-tutorial/using-methods/"
url: "/java-quick-start/quick-start-tutorial/using-methods/"
jdoodle: true
enlighterjs: true
aliases:
  - "/java-quick-start/quick-start-tutorial/using-methods/"
frozen: false
---

*** ** * ** ***

[\<\< Enum and Switch](https://foojay.io/java-quick-start/quick-start-tutorial/enum-and-switch/)  
[Using Objects \>\>](https://foojay.io/java-quick-start/quick-start-tutorial/using-objects/)



<figure class="wp-block-embed is-type-video is-provider-youtube wp-block-embed-youtube wp-embed-aspect-16-9 wp-has-aspect-ratio">
 <div class="wp-block-embed__wrapper">
  <iframe title="Foojay - Getting Started With Java - 07. Using Methods" width="500" height="281" src="https://www.youtube.com/embed/KKxQMlTQtn4?feature=oembed" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>
 </div>
</figure>



Let's now use some different methods to keep the code simple and easy to read and understand. After all, even though you may write your code only once, you want it to remain maintainable forever.

Methods help you to organize your code. Each method does one specific thing, either with or without input values.



<div data-pym-src="https://www.jdoodle.com/plugin" data-language="java" data-version-index="6" data-libs="mavenlib1, mavenlib2">
 import java.text.SimpleDateFormat; import java.util.Date; public class UsingMethod { public static void main (String[] args) { System.out.println("2 x Raspberry Pi 4 4Gb, price: " + getTotal(2, 59.95F) + " Euro"); System.out.println("Current date and time is: " + getNow()); } public static float getTotal(int quantity, float price) { return quantity * price; } public static String getNow() { return new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()); } }
</div>



*** ** * ** ***

1. First, look at the import statements at the top. Because we use methods that are not part of "basic Java", we need to tell our program which additional classes need to be imported.
2. Then two methods are defined. By calling these methods from the main method, we can keep the code in the main method very clean and readable.
   * "getTotal(int quantity, float price)" which returns a calculated value
   * "getNow()" which returns the current timestamp as a readable formatted String
3. Run the code, as shown below.



<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="dracula" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ java UsingMethod.java

2 x Raspberry Pi 4 4Gb, price: 119.9000015258789 Euro
Current date and time is: 2022.12.09 21:35:23</pre>

  
[\<\< Enum and Switch](https://foojay.io/java-quick-start/quick-start-tutorial/enum-and-switch/)  
[Using Objects \>\>](https://foojay.io/java-quick-start/quick-start-tutorial/using-objects/)
