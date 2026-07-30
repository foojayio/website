---
title: "Soprano ofNullable stream Java Challenge"
slug: "soprano-ofnullable-stream-java-challenge"
date: "2021-07-05T08:21:54+00:00"
lastmod: "2021-08-23T12:17:43+00:00"
description: "Since Java 9, you can use Optional with a stream to manipulate values from a List. Check out the latest Java challenge about this!"
authors:
  - "rafael-del-nero"
image: "/images/posts/2021/07/soprano-ofnullable-stream-java-challenge/soprano_challenge.png"
categories:
  - "Tutorials"
tags:
related_posts:
  - "stream-limit-filter-java-challenge"
  - "neo-stream-search-java-challenge"
  - "asynchronous-completablefuture-san-francisco-adventure-java-challenge"
  - "daemon-thread-java-code-quiz"
enlighterjs: true
frozen: false
---

Since Java 9, it's possible to use Optional with a stream when we need to manipulate values from a List.

In this Java Challenge, we will explore the use of a stream in an Optional!

Are you ready to solve this Java Challenge? It's time to improve your Java skills with this Soprano ofNullable stream Challenge...

Soprano ofNullable filter Challenge {#h2-0-soprano-ofnullable-filter-challenge}
-------------------------------------------------------------------------------

What will happen after the main method is executed as follows?

<pre class="EnlighterJSRAW" data-enlighter-language="java">import java.util.List;
import java.util.stream.Stream;

public class OfNullableChallenger {

    public static void main(String... args) {
        Soprano soprano = null;

        Stream.ofNullable(soprano)
        .filter(s -&gt; s.guns.get(0) == null)
        .forEach(s -&gt; System.out.println(s.guns.size()));
    }

    static class Soprano {
        List&lt;String&gt; guns;

        public Soprano(List&lt;String&gt; guns) {
            this.guns = guns;
        }
    }
}</pre>

A) 0  

B) The output will be empty  

C) java.lang.NullPointerException will be thrown  

D) null

**Explanation**:

Let's analyze the code:

<pre class="EnlighterJSRAW" data-enlighter-language="java">Stream.ofNullable(soprano)
</pre>

Note that we are using the ofNullable method from Java 9. With this method, it's possible to avoid a NullPointerException in case a null object is passed.

Now let's analyse the method implementation from ofNullable:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public static Stream ofNullable(T t) {
       return t == null ? Stream.empty() : StreamSupport.stream(
                                    new Streams.StreamBuilderImpl&lt;&gt;(t), false);
}</pre>

If the object is null, a Stream.empty() will be returned, with that a NullPointerException will be avoided as mentioned before, therefore, the output will be empty. The Stream pipeline won't even continue after that.

But if we were using the method Stream.of(), like this for example:

<pre class="EnlighterJSRAW" data-enlighter-language="java">Stream.of(soprano)
        .filter(s -&gt; s.guns.get(0) == null)
        .forEach(s -&gt; System.out.println(s.guns.size()));</pre>

...we would have a `NullPointerException` since the Stream.of method doesn't handle null objects.

If you want to watch the FULL video explanation, check this out: (Before, try out the Java Challenge on your own)

{{< youtube gxRGunn252E >}}

<br />

This is it, keep breaking your limits and never stop improving your skills! If you want more Java Challenges, go to [https://javachallengers.com/](https://javachallengers.com/ "https://javachallengers.com/")
