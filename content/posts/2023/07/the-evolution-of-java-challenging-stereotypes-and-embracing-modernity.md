---
title: "The Evolution of Java: Challenging Stereotypes and Embracing Modernity"
slug: "the-evolution-of-java-challenging-stereotypes-and-embracing-modernity"
date: "2023-07-07T16:15:56+00:00"
lastmod: "2023-07-10T04:00:31+00:00"
description: "Dive into the modern evolution of Java, challenging stereotypes and showcasing its transformative features."
authors:
  - "bazlur-rahman"
image: "/images/posts/2023/07/the-evolution-of-java-challenging-stereotypes-and-embracing-modernity/19d24567-abd1-47f8-ab6d-1a8f05cd3801.jpeg"
categories:
  - "Opinion"
tags:
related_posts:
  - "foojay-all-about-java-and-the-openjdk-i-programmer"
  - "5-things-you-probably-didnt-know-about-java-concurrency"
  - "7-ways-to-contribute-to-openjdk"
enlighterjs: true
frozen: false
---

On a flight from Zurich to Toronto, I found myself in an engaging conversation with a fellow passenger. Among various topics, our conversation navigated toward our careers and, inevitably, as I am a Java developer, towards the world of Java.

This gentleman, having worked with Java some 15-20 years ago, was surprised to learn that I still actively use the language. "Isn't it slow?" he questioned. His question echoed a stereotype that I have often encountered, one that has stuck to Java from its earlier days.

However, this stereotype is not only outdated but also inaccurate. Java is no longer a slow language; in fact, it's considered one of the most performant languages, and in some scenarios, it even outperforms C++. In this article, we will tackle and dismantle such stereotypes as we explore the exciting evolution of Java.

Java: From Its Inception to Now {#h2-0-java-from-its-inception-to-now}
----------------------------------------------------------------------

When James Gosling and his team at Sun Microsystems released Java in 1995, it was touted as a revolutionary step in the world of programming languages. Java's "write once, run anywhere" promise and its strong emphasis on security made it an immediate hit.

However, as the software landscape began to shift towards more dynamic and flexible languages, Java was somewhat left behind, earning a reputation as a verbose, old-fashioned language.

Today, thanks to ongoing and substantial upgrades to the language, Java is shedding these stereotypes. A slew of new features, alongside important improvements in existing ones, have kept Java not only relevant but also increasingly attractive for the modern developer.

Embracing Modernity: The New Features of Java {#h2-1-embracing-modernity-the-new-features-of-java}
--------------------------------------------------------------------------------------------------

The modern versions of Java have brought in several compelling features that challenge the idea of Java as an old and rigid language.

### **Flexible Main Methods** {#h3-2-flexible-main-methods}

In a departure from the traditional '***public static void main(String\[\] args)***', Java now allows additional entry points to an application, providing more flexibility and expressiveness in how programs are written and executed.

This new addition allows the classic "Hello, World!" program to be simplified to:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">class HelloWorld { 
    void main() { 
        System.out.println("Hello, World!");
    }
}</pre>

We can further make the class declaration implicit. This further simplifies the "Hello, World!" program to:  

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">void main() {
&nbsp;&nbsp;&nbsp;&nbsp;System.out.println("Hello, World!");
}</pre>

If you are interested in reading more, read my news item published on infoQ: [Breaking down Barriers: Introducing JDK 21's Approach to Beginner-Friendly Java Programming](https://www.infoq.com/news/2023/05/beginner-friendly-java/)

### **Records** {#h3-3-records}

With Records, Java provides a compact syntax for declaring classes which are supposed to be dumb data holders. This reduces the boilerplate code developers have to write, making the language learner more efficient. Consider the following class:  

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import java.util.Objects;

public final class User {
    private final Long id;
    private final String firstName;
    private final String lastName;

    public User(Long id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long id() {
        return id;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (User) obj;
        return Objects.equals(this.id, that.id) &amp;&amp;
                Objects.equals(this.firstName, that.firstName) &amp;&amp;
                Objects.equals(this.lastName, that.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName);
    }

    @Override
    public String toString() {
        return "User[" +
                "id=" + id + ", " +
                "firstName=" + firstName + ", " +
                "lastName=" + lastName + ']';
    }

}
</pre>

<br />

This whole code can be written in one line with Record.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public record User(Long id, String firstName, String lastName) {}</pre>

And that's it.

### **Sealed Classes** {#h3-4-sealed-classes}

Sealed classes (and interfaces) give developers more control over inheritance, providing a level of flexibility hitherto unseen in the Java world.

If you're interested in knowing more about the case of sealed classes, read this article: [Java Sealed Classes: Building Robust and Secure Applications](https://foojay.io/today/java-sealed-classes-in-action-building-robust-and-secure-applications/)

### **Pattern Matching, Unnamed Patterns and Variables** {#h3-5-pattern-matching-unnamed-patterns-and-variables}

The inclusion of these features ushers in a new level of expressiveness in the language, reducing the verbosity and making code easier to read and write.

For example, Unnamed pattern variables can be beneficial in switch statements where the same action is executed for multiple cases, and the variables are not used. Consider the following code:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">switch (b) {
&nbsp;&nbsp;&nbsp;&nbsp;case Box(RedBall _), Box(BlueBall _) -&gt; processBox(b);
&nbsp;&nbsp;&nbsp;&nbsp;case Box(GreenBall _) -&gt; stopProcessing();
&nbsp;&nbsp;&nbsp;&nbsp;case Box(_) -&gt; pickAnotherBox();
}</pre>

If you're interested in knowing more about it, read this article: [JEP 443: Unnamed Patterns and Variables Aims to Improve Java Code Readability](https://www.infoq.com/news/2023/06/streamlining-java-with-jep-443/)

### **String Templates** {#h3-6-string-templates}

Java developers can now enhance the language's string literals and text blocks with string templates that can produce specialized results by coupling literal text with embedded expressions and processors.

The aim of this new feature is to simplify the writing of Java programs, improve the readability of expressions that mix text and expressions, and enhance the security of Java programs that compose strings from user-provided values. Consider the following example:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">String name&nbsp; &nbsp; = "Joan Smith";
String phone &nbsp; = "555-123-4567";
String address = "1 Maple Drive, Anytown";

String json = STR."""
&nbsp;&nbsp;&nbsp;&nbsp;{
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"name":&nbsp; &nbsp; "\{name}",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"phone": &nbsp; "\{phone}",
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"address": "\{address}"
&nbsp;&nbsp;&nbsp;&nbsp;}
&nbsp;&nbsp;&nbsp;&nbsp;""";</pre>

This produces the following output.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">| """
| {
| &nbsp; &nbsp; "name":&nbsp; &nbsp; "Joan Smith",
| &nbsp; &nbsp; "phone": &nbsp; "555-123-4567",
| &nbsp; &nbsp; "address": "1 Maple Drive, Anytown"
| }
| """</pre>

Read the following infoQ item to know more about it: [Java Gets a Boost with String Templates: Simplifying Code and Improving Security](https://www.infoq.com/news/2023/04/java-gets-a-boost-with-string/)

### **Java's Handling of Concurrency** {#h3-7-java-s-handling-of-concurrency}

Java's improved handling of concurrency is another example of its modernization. The introduction of Virtual Threads (previously known as Project Loom) simplifies concurrent programming by making it easier to write, debug, profile, and maintain concurrent applications.

Additionally, Java's Structured Concurrency prevents common programming errors, making concurrent programming safer and more efficient.

To know more about read the following article I have written:

* [A Comprehensive Guide to Java Virtual Threads (Part 1) \| Foojay.io](https://foojay.io/today/unleashing-the-power-of-lightweight-concurrency-a-comprehensive-guide-to-java-virtual-threads-part-1/)
* [JEP 444: Virtual Threads Arrive in JDK 21, Ushering a New Era of Concurrency](https://www.infoq.com/news/2023/04/virtual-threads-arrives-jdk21/)
* [Structured Concurrency in JDK 21: A Leap Forward in Concurrent Programming](https://www.infoq.com/news/2023/06/structured-concurrency-jdk-21/)

### Unlocking High Performance with Modern Java Technologies {#h3-8-unlocking-high-performance-with-modern-java-technologies}

It's important to note that modern Java Virtual Machines (JVMs) have incorporated more efficient garbage collection algorithms, like the Garbage-First (G1) collector and the Z Garbage Collector (ZGC), designed to significantly reduce pause times and scale effectively. Furthermore, today's Just-In-Time (JIT) compilers have been greatly enhanced and are capable of generating highly efficient machine code. In certain instances, JIT-compiled code can surpass the performance of statically compiled code due to its ability to make optimizations based on real-time data.   

JVMs have seen substantial advancements in efficiency through the application of progressive techniques such as adaptive optimization and speculative execution, which enhance performance. Additionally, Java's robust support for multi-threading ensures optimal utilization of modern multi-core processors. When appropriately designed, Java programs can exhibit high levels of concurrency and parallelism, yielding superior performance on contemporary hardware.   

Java has been the foundation of countless high-performance applications and systems. This includes high-frequency trading systems, big data processing frameworks like Apache Hadoop and Apache Spark, as well as large-scale websites such as LinkedIn and eBay. This stands as a testament to its capabilities and versatility.

The Journey Ahead {#h2-9-the-journey-ahead}
-------------------------------------------

While Java's transformation has been significant, it is far from over. With its robust ecosystem and the Java Community Process continually pushing for enhancements, the future is bright.

The Java we know today is not just a language capable of meeting the demands of modern software development but also one that is continually evolving to anticipate future needs.

In conclusion, Java is no longer your dad's language. It's a modern, expressive, and efficient tool that is more than capable of taking on the challenges of the contemporary development world.

The stereotypes have been challenged, and Java stands tall, embracing modernity.
