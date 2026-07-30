---
title: "Exploring New Features in JDK 23: Simplifying Java with Module Import Declarations with JEP 476"
slug: "exploring-new-features-in-jdk-23-simplifying-java-with-module-import-declarations-with-jep-476"
date: "2024-06-20T16:14:08+00:00"
lastmod: "2024-06-20T16:15:54+00:00"
description: "JEP 476 represents a significant step towards simplifying Java programming by reducing boilerplate code and improving readability."
authors:
  - "bazlur-rahman"
image: "/images/posts/2024/06/exploring-new-features-in-jdk-23-simplifying-java-with-module-import-declarations-with-jep-476/Bazlur_Rahman_a_Swiss_Army_Knife_with_the_word_Optional_on_it_a_fbcd5137-6b73-4ed7-9c61-090471b880f4.png"
categories:
  - "Java"
  - "JEPs"
  - "Performance"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "a-minor-but-useful-refactoring-technique-that-would-reduce-your-code-footprint-part-1"
  - "how-to-diagnose-and-mitigate-pinning-in-javas-virtual-thread-execution"
enlighterjs: true
frozen: false
---

As Java evolves, simplifying code and improving developer productivity remain priorities.

[JEP 476](https://openjdk.org/jeps/476) introduces a new feature in JDK 23: Module Import Declarations.

This feature aims to streamline the process of importing multiple packages from a module, enhancing code readability and reducing boilerplate.

### **What is JEP 476?** {#h3-0-what-is-jep-476}

JEP 476 proposes the ability to import all packages exported by a module with a single declaration. This is particularly useful for developers who frequently use multiple packages from the same module, as it eliminates the need for numerous individual import statements.

### **Key Features** {#h3-1-key-features}

* **Simplified Imports**: Instead of multiple import statements, a single import module statement can be used. For example, import module java.base; will import all public top-level classes and interfaces from the java.base module, which includes packages like java.util and java.nio.file.
* **Beginner-Friendly**: This feature makes it easier for beginners to use third-party libraries and fundamental Java classes without having to learn the package hierarchy by reducing the complexity of import statements.

### **Usage Example** {#h3-2-usage-example}

Consider a scenario where you need to use multiple classes from the java.util package. Traditionally, you would write:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;</pre>

With JEP 476, this can be simplified to:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import module java.base;</pre>

This single line imports all necessary classes, making the code cleaner and more concise. Consider the following example:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import module java.base;

void main() {

    println("Hello, World!");
    println("Your Locale: " + Locale.getDefault());
    println("A Big Number: " + new BigInteger("12345678901234567890"));

    var random = new Random();
    println("Random Number: " + random.nextInt(100));

    var now = Instant.now();
    println("Current Time: " + now);

    var greetings = new ArrayList&lt;&gt;();
    greetings.add("Hello, World!");
    greetings.add("স্বাগতম বিশ্ব!");
    greetings.add("مرحبا بالعالم!");
    println("Greetings: " + greetings);

    try {
        var localhost = InetAddress.getLocalHost();
        println("Local Hostname: " + localhost.getHostName());
    } catch (Exception e) {
        println("Could not get hostname: " + e.getMessage());
    }

    println("UTF-8 Charset: " + Charset.forName("UTF-8"));
    println("Current Thread: " + Thread.currentThread().getName());
}

//java --enable-preview --source 23 Helloworld.java
</pre>

<br />

With this module import, you import the entire module; there is no need to maintain a long list of import statements at the beginning of the file.  

NOTE: This is a [preview language feature](https://openjdk.org/jeps/12), available through the --enable-preview flag with the JDK 23 compiler and runtime. To try the examples above in JDK 23, you must enable the preview features:  

* Compile the program with javac --release 23 --enable-preview Main.java and run it with java --enable-preview Main; or,
* When using the [source code launcher](https://openjdk.org/jeps/330), run the program with java --enable-preview Main.java; or,
* When using [jshell](https://openjdk.java.net/jeps/222), start it with jshell --enable-preview.

### **Addressing Ambiguities** {#h3-3-addressing-ambiguities}

One potential issue with module imports is name ambiguity. For example, importing both java.base and java.sql modules might lead to conflicts with classes like Date present in both packages.

In such cases, specific import statements can be used to resolve ambiguities.

### **Conclusion** {#h3-4-conclusion}

JEP 476 represents a significant step towards simplifying Java programming by reducing boilerplate code and improving readability. More about this JEP can be found in this [infoQ news](https://www.infoq.com/news/2024/05/simplifying-java-module-import/).
