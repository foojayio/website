---
title: "Optional takeWhile dropWhile Java Challenge"
slug: "optional-takewhile-dropwhile-java-challenge"
date: "2021-05-10T08:05:57+00:00"
lastmod: "2021-08-23T12:46:45+00:00"
description: "Main goal of the Optional class is to avoid NullPointerException. It’s much easier to deal with null when we use the concept of Optional."
authors:
  - "rafael-del-nero"
image: "optional.png"
categories:
  - "Tutorials"
tags:
related_posts:
  - "soprano-ofnullable-stream-java-challenge"
  - "daemon-thread-java-code-quiz"
  - "function-calculation-java-challenge"
  - "stream-limit-filter-java-challenge"
enlighterjs: true
frozen: false
---

The Optional concept is present in many programming languages. The main goal of the Optional class is to avoid `NullPointerException`. It's much easier to deal with null values when we use the concepts of an Optional.

In this challenge, we will also explore the takeWhile and dropWhile methods from Java 9. Therefore you will be upgrading your knowledge with `Optional`, `takeWhile`, and `dropWhile`!

Are you prepared to have fun with this Java Challenge and refine your Java skills?

Take 5 minutes of your time and get this challenge done!

### Optional TakeDropWhile ChallengeOptional TakeDropWhile Challenge {#h3-0-optional-takedropwhile-challengeoptional-takedropwhile-challenge}

What will happen in the following code?

<pre class="EnlighterJSRAW" data-enlighter-language="java">import java.util.List;
import java.util.Optional;

public class OptionalChallenge4 {

    public static void main(String... doYourBest) {
        List&lt;Integer&gt; list = List.of(10, 1, 3, 5, 7, 9);

        Optional&lt;Integer&gt; number =
                list.stream()
                .takeWhile(i -&gt; i &gt; 5)
                .dropWhile(i -&gt; i &gt; 9)
                .findFirst()
                .or(() -&gt; Optional.of(777));

        System.out.println(number);
    }

}</pre>

A) Optional\[777\]  

B) Optional\[5\]  

C) Optional\[10\]  

D) Optional\[7\]

**Explanation (Spoiler Alert!)**:

In the `takeWhile` method, element 10 will be taken and then the looping will stop because the number 1 doesn't match the condition. Then, in the next line, elements will be dropped, and since 10 is greater than 9, it will be dropped. As in the end there are no results, the or statement will be executed, returning... what do you think? 🙂

If you want to try out this Java Challenge with an interactive quiz or watch the video explanation, you can access the following link:

<https://javachallengers.com/optional-takewhile-dropwhile-challenge/>

Right on Challenger, keep rocking with Java! See you in the next challenge!
