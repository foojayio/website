---
title: "Streams Set Distinct Java Code Quiz | Foojay.io Today"
slug: "streams-set-distinct-java-code-quiz"
date: "2021-11-11T09:36:45+00:00"
lastmod: "2021-11-11T09:36:46+00:00"
description: "Using Streams and Set Collection Factory methods with Java makes code easier to read and maintain. Take the next Java quiz to learn more."
authors:
  - "rafael-del-nero"
image: "/images/posts/2021/11/streams-set-distinct-java-code-quiz/Favicon-3-2.png"
categories:
  - "Tutorials"
tags:
related_posts:
  - "arrays-and-object-reference-java-challenge-code-quiz"
  - "daemon-thread-java-code-quiz"
  - "exception-chaos-java-code-quiz"
enlighterjs: true
frozen: false
---

Using Streams and Set Collection Factory methods with Java makes code easier to read and maintain.

By using these features, we can also make it more difficult for bugs to remain hidden. If you can use the latest [LTS (Long-term support) Java version](https://foojay.io/almanac/java-17/ "LTS (Long-term support) Java version") in your project, it's the best scenario, so that you can use cool Java features such as Collection Factories from Java 9 onwards.

Now that we know why it's important to understand Java features well, it's time for the Java Challenge!

It's time to improve your Java skills with this Stream Set Distinct Challenge...

Stream Set Distinct Challenge {#h2-0-stream-set-distinct-challenge}
-------------------------------------------------------------------

Can you guess what will happen when running the following Java code?

<pre class="EnlighterJSRAW" data-enlighter-language="java">import java.util.Set;

public class StreamDistinctChallenge {

    public static void main(String... doYourBest) {
        Set&lt;Warrior&gt; warriors = Set.of(new Warrior("Ezio"), new Warrior("Ezio"),
                new Warrior("Kratos"), new Warrior("Cloud"), new Warrior("Alucard"));

        warriors.stream()
                .distinct()
                .forEach(w -&gt; System.out.println(w.name));
    }

    static class Warrior {
        private String name;

        Warrior(String name) {
            this.name = name;
        }

        public int hashCode() {
            return this.name.length();
        }

        public boolean equals(Object obj) {
            return this.name.equals(((Warrior) obj).name);
        }
    }
}</pre>

A) Cloud Ezio Alucard Kratos  

B) IllegalArgumentException will be thrown  

C) Ezio Ezio Alucard Kratos Cloud  

D) NullPointerException will be thrown

**Explanation about this Java Challenge:**

The catch of this quiz is the Set.of factory method behavior. Note that we are passing "Ezio" twice, so that will be equal and will have the same hashcode number.

<pre class="EnlighterJSRAW" data-enlighter-language="java">Set.of(new Warrior("Ezio"), new Warrior("Ezio"), new Warrior("Kratos"), new Warrior("Cloud"), new Warrior("Alucard"));</pre>

The problem is that when we use two objects that are equal, the Set.of method will do something specific, because of the duplicated elements, one of the answers above. 🙂

Pay attention also that if we were using the List.of factory method, this would not happen.

If you want to watch the video explanation, check it out, **but I recommend trying out the Java Challenge first**:

{{< youtube xYUE665GTc8 >}}

<br />

That's it challenger, rock on! Keep taking action and relentlessly break your limits!

Don't hesitate to leave a comment with a question if anything is not clear!

To check the original Java Challengers post, access the following link:  
<https://javachallengers.com/streams-set-distinct-java-challenge-quiz/>
