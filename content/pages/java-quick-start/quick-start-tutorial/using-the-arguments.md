---
title: "Getting Started with Java - Using the Arguments"
description: "foojay is the place for all OpenJDK Update Release Information. Learn More."
canonical: "https://foojay.io/java-quick-start/quick-start-tutorial/using-the-arguments/"
url: "/java-quick-start/quick-start-tutorial/using-the-arguments/"
jdoodle: true
enlighterjs: true
aliases:
  - "/java-quick-start/quick-start-tutorial/using-the-arguments/"
frozen: false
---

*** ** * ** ***

[\<\< Hello World!](https://foojay.io/java-quick-start/quick-start-tutorial/hello-world/)  
[Working with Numbers \>\>](https://foojay.io/java-quick-start/quick-start-tutorial/working-with-numbers/)



<figure class="wp-block-embed is-type-video is-provider-youtube wp-block-embed-youtube wp-embed-aspect-16-9 wp-has-aspect-ratio">
 <div class="wp-block-embed__wrapper">
  <iframe title="Foojay - Getting Started With Java - 03. Using Arguments and String Arrays" width="500" height="281" src="https://www.youtube.com/embed/oKBQaesKJJI?feature=oembed" frameborder="0" allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" referrerpolicy="strict-origin-when-cross-origin" allowfullscreen></iframe>
 </div>
</figure>



Let's go a little step further and use the start-up arguments assigned in the main method to learn how you can use arrays, check the array length, and loop through them.

We need to create another new Java file and call it `MainArguments.java`. The `args` variable of the main method is a String array and we will use it with the following code. You can try it out here, by typing in one or more words as "Input Arguments":



<div data-pym-src="https://www.jdoodle.com/plugin" data-language="java" data-version-index="6" data-libs="mavenlib1, mavenlib2">
 public class MainArguments { public static void main (String[] args) { System.out.println("Number of arguments: " + args.length); if (args.length &gt; 0) { System.out.println("First argument: " + args[0]); } for (int i = 0; i &lt; args.length; i++) { System.out.println("Argument " + (i + 1) + ": " + args[i]); } } }
</div>



*** ** * ** ***

Now we can start the application and provide it any number of extra arguments.

From the array, we can get the number of items (= `args.length`) and use a for-loop for cycling through all the arguments.



<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="dracula" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ java MainArguments.java
Number of arguments: 0

$ java MainArguments.java "Hello World" "Bye"
Number of arguments: 2
First argument: Hello World
Argument 1: Hello World
Argument 2: Bye</pre>

  
[\<\< Hello World!](https://foojay.io/java-quick-start/quick-start-tutorial/hello-world/)  
[Working with Numbers \>\>](https://foojay.io/java-quick-start/quick-start-tutorial/working-with-numbers/)
