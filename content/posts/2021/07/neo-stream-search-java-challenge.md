---
title: "Neo Stream Search Java Challenge | Foojay Today"
slug: "neo-stream-search-java-challenge"
date: "2021-07-26T08:32:40+00:00"
lastmod: "2021-08-23T12:16:36+00:00"
description: "In this challenge, we explore important key methods when we work with a Java Stream so that it becomes clear for you what they do."
authors:
  - "rafael-del-nero"
image: "https://foojay.io/wp-content/uploads/2021/07/neo.png"
categories:
  - "Tutorials"
tags:
related_posts:
  - "soprano-ofnullable-stream-java-challenge"
  - "asynchronous-completablefuture-san-francisco-adventure-java-challenge"
  - "daemon-thread-java-code-quiz"
  - "function-calculation-java-challenge"
enlighterjs: true
frozen: false
---

Understanding the mechanics of the functional interface Predicate of a Stream is crucial if you want to create something meaningful with streams. On this challenge, we will explore important key methods when we work with a stream so that it becomes clear for you what they do.

Without further ado, let's go to the Java Challenge!

It's time to improve your Java skills with this Neo Stream Search Challenge

Neo Stream Search Challenge {#_neo_stream_search_challenge}
-----------------------------------------------------------

What do you think will happen when running the following code?

<pre class="EnlighterJSRAW" data-enlighter-language="java">import java.util.List;
import java.util.function.Predicate;

public class NeoSearch {

    public static void main(String... doYourBest) {
        List&lt;String&gt; ls = List.of("Neo", "Morpheus", "Oracle", "Trinity", "Neo");

        Predicate&lt;String&gt; neoSearch = str -&gt; {
            System.out.println("Agent Smith is looking for Neo...");
            return str.contains("Neo");
        };

        var binaryNumbers = List.of(1, 0, 1, 1).stream();
        Integer binarySum = binaryNumbers.reduce(Integer::sum).orElseThrow(StackOverflowError::new);

        boolean neoFound = ls.stream().filter(str -&gt; str.length() &gt;= binarySum).allMatch(neoSearch);
        System.out.println(neoFound);
    }

}</pre>

A) Agent Smith is looking for Neo...​  

false

B) Agent Smith is looking for Neo...​  

Agent Smith is looking for Neo...​  

false

C) Agent Smith is looking for Neo...​  

true

D) Agent Smith is looking for Neo...​  

Agent Smith is looking for Neo...​  

Agent Smith is looking for Neo...​  

Agent Smith is looking for Neo...​  

Agent Smith is looking for Neo...​  

true

**Explanation**:

Here we are simply defining what is the Predicate condition with a Lambda:

<pre class="EnlighterJSRAW" data-enlighter-language="java">Predicate neoSearch = str -&gt; {
  System.out.println("Agent Smith is looking for Neo...");
  return str.contains("Neo");
};</pre>

Then we are reducing our List from 1, 0, 1 and 1 to 3 because we are adding all elements basically:

<pre class="EnlighterJSRAW" data-enlighter-language="java">var binaryNumbers = List.of(1, 0, 1, 1).stream();
Integer binarySum = binaryNumbers.reduce(Integer::sum)
.orElseThrow(StackOverflowError::new);</pre>

Then here we filter all elements that have the size greater or equals to binarySum and then we use the allMatch function. Note that this function has to have all elements matching to return true. Since the first element is true, the allMatch method from the Stream will go to the next element printing again "Agent Smith is looking for Neo...​", as this method requires all elements to be true, there is no point in continuing the looping so the looping will break.

<pre class="EnlighterJSRAW" data-enlighter-language="java">boolean neoFound = ls.stream().filter(str -&gt; str.length() &gt;= binarySum).allMatch(neoSearch);
System.out.println(neoFound);</pre>

To conclude, the output will be:

<pre class="EnlighterJSRAW">B) Agent Smith is looking for Neo...
    Agent Smith is looking for Neo...
    false</pre>

To fully understand this Java Challenge, you can also watch the FULL explanation video:

That's it challenger! Keep it up with your Java learning and keep solving the Java Challenges! To see the original article, check it out the following link:  
<https://javachallengers.com/neo-stream-search-java-challenge/>
