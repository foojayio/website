---
title: "The Map, Equals, Hashcode Java Challenge"
slug: "map-equals-hashcode-java-challenge"
date: "2021-05-17T08:38:13+00:00"
lastmod: "2021-08-23T12:22:47+00:00"
description: "Understanding deeply how to use a Map, equals, and hashcode in Java will be a massive help for you to create high-quality code!"
authors:
  - "rafael-del-nero"
image: "https://foojay.io/wp-content/uploads/2021/05/map_equals.png"
categories:
  - "Tutorials"
tags:
related_posts:
  - "daemon-thread-java-code-quiz"
  - "function-calculation-java-challenge"
  - "stream-limit-filter-java-challenge"
  - "neo-stream-search-java-challenge"
enlighterjs: true
frozen: false
---

Understanding deeply how to use a Map, equals, and hashcode in Java will be a massive help for you to create high-quality code. The Map and object reference concepts are not only present in the Java language but in almost all programming languages. Therefore, if you master the concept of object references, equals and hashcode, and Maps, you can apply this in other programming languages and master them far more easily.

When we are using HashMaps, the equals and hashcode methods go hand-in-hand. That's because the algorithm from a HashMap uses those methods to indicate if an Objects are different.

What about going deeply into the Map, equals, and hashcode method by trying out this Java Challenge then? Remember, the Java Challenges are a fun way for you to really master Java concepts!

It's time to improve your Java skills with this Map equals hashcode Challenge...

Map equals hashcode Challenge {#h2-0-map-equals-hashcode-challenge}
-------------------------------------------------------------------

What do you think will happen in the following code after running it?

<pre class="EnlighterJSRAW" data-enlighter-language="java">import java.util.LinkedHashMap;
import java.util.Map;

public class MapEqualsChallenge {
    public static void main(String... doYourBest) {
        Map&lt;Stark, String&gt; map = new LinkedHashMap&lt;&gt;();

        map.put(new Stark("Arya"), "1"); map.put(new Stark("Ned"), "2");
        map.put(new Stark("Sansa"), "3"); map.put(new Stark("Bran"), "4");
        map.put(new Stark("Jaime"), "5");

        map.forEach((k, v) -&gt; System.out.print(v));
    }
    static class Stark {
        String name;
        Stark(String name) {this.name = name;}

        public boolean equals(Object obj) {
            return ((Stark)obj).name.length() == this.name.length();
        }

        public int hashCode() { return 4000 &lt;&lt; 2 * 2000 / 10000; }
    }
}</pre>

A) 245  

B) 123  

C) 425  

D) 12345

**Explanation**:

When we use Map, it's a good practice to override the equals and hashcode methods. That's because the Map implementations have to add unique elements in the key. If the Object doesn't have the equals and hashcode methods overridden, then it's impossible for a Map to define if the Objects are the same or not.

Note that in this Java Challenge we are overriding the equals and hashcode methods, therefore the Map implementations will behave accordingly to what was implemented on those methods.

Another important point to note is the hashCode method that even though it's a bit complex, it's returning always the same value:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public int hashCode() { return 4000 &lt;&lt; 2 * 2000 / 10000; }

</pre>

Pay attention that the equals method is actually comparing the length of the String. So, objects are going to be the same if the String length is the same.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Arya has 4 characters - it will be inserted
Ned has 3 characters - it will be inserted

Sansa has 5 characters - it will be inserted
Bran has 4 characters - it will replace Arya

Jaime has 5 characters - it will replace Sansa</pre>

As Bran has 4 characters and it was the last one to be inserted, it will replace Arya. Therefore the first value is 4.  

As Ned is the only one with 3 characters, his value will be printed that is 2.

Then, Sansa has 5 characters, it will be inserted and then Jaime will replace Sansa value with 5.

If you want to go deeper into the equals and hashcode methods, I highly recommend you to read the following article: [Comparing Java objects with equals() and hashcode()](https://www.infoworld.com/article/3305792/comparing-java-objects-with-equals-and-hashcode.html)

That's it challenger, rock on! Keep taking action and relentlessly break your limits! Don't hesitate to leave a comment with a question if anything is not clear!

You can also access the original Java Challengers post on: [https://javachallengers.com/map-equals-hashcode](https://javachallengers.com/map-equals-hashcode/)
