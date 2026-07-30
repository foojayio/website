---
title: "Minor Techniques That Would Reduce Your Code Footprint (Part 1)"
slug: "a-minor-but-useful-refactoring-technique-that-would-reduce-your-code-footprint-part-1"
date: "2022-10-20T14:32:52+00:00"
lastmod: "2022-10-20T16:21:42+00:00"
description: "That was a long-awaited migration, but I'm pleased that we eventually made it. The next thing... \"Why not Java 17 directly?\""
authors:
  - "bazlur-rahman"
image: "https://foojay.io/wp-content/uploads/2021/11/distro-choice.jpg"
categories:
  - "Java Core"
tags:
related_posts:
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "5-things-you-probably-didnt-know-about-java-concurrency"
  - "java-bytecode-simplified-journey-to-the-wonderland-part-1"
enlighterjs: true
frozen: false
---

Finally, we upgraded to Java 11 from Java 8.

That was a long-awaited move, but I'm pleased that we eventually made it.

The next thing that came to mind when I stated this was, "Why not Java 17 directly?"

Eventually, we will move to 17, but we had to begin someplace.

And the transition to 11 was not easy either.

We had several dependencies with Mockito, which made the journey somewhat tricky, but we were able to manage it.

Anyway, since we have already moved to 11, I was wondering what smaller changes I could make to the codebase immediately.

I was looking for smaller changes, not major ones. So this article is about the first step of some smaller changes that I made.

Immutable collections {#h2-0-immutable-collections}
---------------------------------------------------

For many reasons, we often need immutable collections.

However, Java collections are inherently mutable. There was no simpler way to make them immutable.

The option in our code repository appeared as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">  private static final List&lt;RuleType&gt; OUR_FAVORITE_RULES;

  static {
    final List&lt;RuleType&gt; favoriteRules = new ArrayList&lt;&gt;();
    favoriteRules.add(RuleType.RULE_ONE);
    favoriteRules.add(RuleType.RULE_TWO);
    favoriteRules.add(RuleType.RULE_ETC);
    OUR_FAVORITE_RULES = Collections.unmodifiableList(favoriteRules);
  }
</pre>

Interestingly, Java 9 introduced the Factory method `List.of()` that can be used to remove the entire block.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&nbsp;  private static final List&lt;RuleType&gt; OUR_FAVORITE_RULES = List.of(RuleType.RULE_ONE,
      RuleType.RULE_TWO, RuleType.RULE_ETC);</pre>

There are similar methods available for set and map. Example:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Set.of(1,2,3,4,5);

Map.of("bazlur", "Bangladesh",
        "Geertjan", "Netherlands");   
</pre>

Null checking if blocks {#h2-1-null-checking-if-blocks}
-------------------------------------------------------

In the huge projects that have accumulated code over many years, you will find many if blocks that only check whether the object is null or not. Based on that, it takes specific actions.

Example:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""> if (fileName == null) {
    builder.append("Unknown Source");
 } else {
    builder.append(fileName);
 }</pre>

There are multiple ways to turn it into oneliners.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">builder.append(fileName == null ? "Unknown Source" : fileName);

//Or

builder.append(Objects.requireNonNullElse(fileName, "Unknown Source"));
</pre>

Or, optionally, we can use an `Optional` idiom as well.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""> builder.append(Optional.ofNullable(fileName).orElse("Unknown Source"));  </pre>

All of them are just fine, but however, I like the new method, Objects.requireNonNullElse(), which makes it more descriptive, and thus the code becomes more readable as well as shorter.

Repeating strings {#h2-2-repeating-strings}
-------------------------------------------

In many cases, you want to repeat the same string multiple times.

I found a similar example in the code base, which is as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">var padRequired = 5;
for (int i = 0; i &lt; padRequired; i++) {
  builder.append("&amp;nbsp;");
}  </pre>

The above code can be rewritten as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">builder.append("&amp;nbsp;".repeat(padRequired));</pre>

That's all for today.

As I continue refactoring the code, I will keep sharing the newer idioms!

Also, I would love to get some feedback from you all.
