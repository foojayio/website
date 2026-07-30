---
title: "Everything Bad in Java is Good for You"
slug: "everything-bad-in-java-is-good-for-you"
date: "2023-10-31T09:28:44+00:00"
lastmod: "2023-11-01T10:37:53+00:00"
description: "Nulls & checked exceptions are often promoted as bad things in Java, this isn't the case. Both carry significant advantage over alternatives!"
canonical: "https://debugagent.com/everything-bad-in-java-is-good-for-you"
authors:
  - "shai-almog"
image: "https://foojay.io/wp-content/uploads/2023/06/Shai_Almog_a_rube_goldberg_machine_drawing_bb1e8dd8-b6bb-4518-8175-6834556eedf4-1024x585.png"
categories:
  - "Opinion"
tags:
related_posts:
enlighterjs: true
frozen: false
---

Everything Bad is Good for You is a [pop culture book](https://en.wikipedia.org/wiki/Everything_Bad_Is_Good_for_You) that points out that some things we assume are bad (like TV) have tremendous benefits to our well-being. I love the premise of disrupting the conventional narrative and was reminded of that constantly when debating some of the more controversial features and problems in Java. It's a feature, not a bug...

One of my favorite things about Java is its tendency to move slowly and deliberately. It doesn't give us what we want right away. The Java team understands the requirements and looks at the other implementations, then learns from them.

I'd say Java's driving philosophy is that the early bird is swallowed by a snake.

{{< youtube cWdkMfmpsps >}}

<br />

Checked Exceptions {#h2-0-checked-exceptions}
---------------------------------------------

One of the most universally hated features in Java is checked exceptions. They are the only innovative feature Java introduced as far as I recall. Most of the other concepts in Java existed in other languages, checked exceptions are a brand new idea that other languages rejected. They aren't a "fun" feature, I get why people don't like them. But they are an amazing tool.

The biggest problem with checked exceptions is the fact that they don't fit nicely into functional syntax. This is true for nullability as well (which I will discuss shortly). That's a fair complaint. Functional programming support was tacked onto Java and in terms of exception handling it was poorly done. The Java compiler could have detected checked exceptions and required an error callback. This was a mistake made when these capabilities were introduced in Java 8. E.g. if these APIs were better introduced into Java we could have written code like this:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">api.call1()
    .call2(() -&gt; codeThatThrowsACheckedException())
    .errorHandler(ex -&gt; handleError(ex))
    .finalCall();</pre>

The compiler could force us to write the `errorHandler` callback if it was missing which would satisfy the spirit of the checked exceptions perfectly. This is possible because checked exceptions are a feature of the compiler, not the JVM. A compiler could detect a checked exception in the lambda and require a specially annotated exception handling callback.

Why wasn't something like this added?

This is probably because of the general dislike of checked exceptions. No one attempted to come up with an alternative. No one likes them because no one likes the annoying feature that forces you to tidy up after yourself. We just want to code, checked exceptions force us to be responsible even when we just want to write a simple hello world...

This is, to a great extent, a mistake... We can declare that main throws an exception and create a simple hello world without handling checked exceptions. In large application frameworks like Spring, checked `SQLException` is wrapped with a `RuntimeException` version of the same class. You might think I'm against that but I'm not. It's a perfect example of how we can use checked exceptions to clean up after the fact. Cleanup is performed internally by Spring, at this point the exception-handling logic is no longer crucial and can be converted to a runtime exception.

I think a lot of the hate towards the API comes from bad versions of this exception such as `MalformedURLException` or encoding exceptions. These exceptions are often thrown for constant input that should never fail. That's just redundant and a bad use of language capabilities. Checked exceptions should only be thrown when there's cleanup we can do. That's an API problem, not a problem with the language feature.

Null {#h2-1-null}
-----------------

Pouring hate on null has been trending for the past 15+ years. Yes, I know [that quote](https://en.wikipedia.org/wiki/Tony_Hoare). I think people misuse it.

Null is a fact of life today, whether you like it or not. It's inherent in everything: databases, protocols, formats, etc. Null is a deep part of programming and will not go away in the foreseeable future.

The debate over null is pointless. The debate that matters is whether the cure is better than the disease and I'm yet unconvinced. What matters isn't if null was a mistake, what matters is what we do now.

To be fair, this directly correlates to your love of functional programming paradigms. Null doesn't play nicely in FP which is why it became a punching bag for the FP guys. But are we stepping back or stepping forward?

Let's break this down into three separate debates:

* Performance
* Failures
* Ease of programming

### Performance {#h3-2-performance}

Null is fast. Super fast. Literally free. The CPU performs a null check for us and handles exceptions as interrupts. We don't need to write code to handle null. The alternatives can be very low overhead and can sometimes translate to null for CPU performance benefits. But this is harder to tune.

Abstractions leak and null is the way our hardware works. For most intents and purposes, it is better.

There is a caveat. We need the ability to mark some objects as non-null for better memory layout (as Valhalla plans to do). This will allow for better memory layout and can help speed up code. Notice that we can accomplish this while maintaining object semantics, a marker would be enough.

I would argue that null takes this round.

### Failures {#h3-3-failures}

People hate NullPointerException. This baffles me.

NullPointerException is one of the best errors to get. It's the [fail-fast principle](https://dev.to/codenameone/fail-fast-reliable-software-strategy-debug-failures-effectively-3162). The error is usually simple to understand and even when it isn't; it isn't far off. It's an easy bug to fix. The alternative might include initializing an empty object which we need to verify or setting a dummy object to represent null.

Open a database that has been around long enough and search for "undefined". I bet it has quite a few entries... That's the problem with non-null values. You might not get a failure immediately. You will get something far worse. A stealth bug that crawls through the system and pollutes your data.

Since null is so simple and easy to detect there's a vast number of tools that can deal with it both in runtime and during development. When people mention getting a null pointer exception in production I usually ask: what would have been the alternative?

If you could have initialized the value to begin with then why didn't you do it?

Java has the final keyword, you can use that to keep non-null stateful values. Mutable values are the main reason for uninitialized or null values. It's very possible that a non-null language wouldn't fail. But would its result be worse?

In my experience, corrupt data in storage is far worse. The problem is insidious and hides under the surface. There's no clue as to the origin of the problem and we need to set "traps" to track it down. Give me a fail-fast any day.

In my opinion, null has this one hands down...

### Ease of Programming {#h3-4-ease-of-programming}

An important point to understand is that null is a requirement of modern computing. Our entire ecosystem is built on top of null. Languages like Kotlin demonstrate this perfectly, they have null and non-null objects.

This means we have duplication. Every concept related to objects is expressed twice, and we need to maintain semantics between null and non-null. This raises the bar of complexity for developers new to such languages and makes for some odd syntax.

This in itself would be fine if the complexity paid off. Unfortunately, such features only resolve the most trivial non-issue cases of null. The complex objects aren't supported since they contain null retrieved from external sources. We're increasing language complexity for limited benefit.

Boilerplate {#h2-5-boilerplate}
-------------------------------

This used to be a bigger issue in the past but looking at a typical Java file vs. TypeScript or JavaScript the difference isn't as big. Still, people nitpick. A smart engineer I know online called the use of semicolons in languages: "Laziness".

I don't get that. I love the semicolon requirement and am always baffled by people who have a problem with that. As an author it lets me format my code while ignoring line length. I can line break wherever I want, the semicolon is the part that matters. If anything, I would have loved to cancel the ability to write conditional statements without the curly braces e.g.:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">if(..) x();
else y();</pre>

That's terrible. I block these in my style requirements; they are a recipe for disaster with an unclear beginning or end.

Java forces organization, this is a remarkable thing. Classes must be in a specific file and packages map to directories. This might not matter when your project is tiny, but as you handle a huge code base, this becomes a godsend. You would instantly know where to look for clues. That is a powerful tool. Yet, it leads to some verbosity and some deep directory structures. But Java was designed by people who build 1M LoC projects, it scales nicely thanks to the boilerplate. We can't say the same for some other languages.

Moving Fast {#h2-6-moving-fast}
-------------------------------

Many things aren't great in Java, especially when building more adventurous startup projects. That's why I'm so [excited about Manifold](https://debugagent.com/series/manifold). I think it's a way to patch Java with all the "cool stuff" we want while keeping the performance, compatibility and stability we love.

This can let the community move forward faster and experiment, while Java as a platform can take the slow and steady route.

Final Word {#h2-7-final-word}
-----------------------------

Conventional wisdom is problematic. Especially when it is so one-sided and presents a single-dimension argument in which a particular language feature is inferior. There are tradeoffs to be made and my bias probably shines through my words.

However, the cookie cutter counterpoints don't cut it. The facts don't present a clear picture to their benefit. There's always a tradeoff and Java has walked a unique tightrope. Even a slight move in the wrong direction can produce a fast tumbling-down effect. Yet it maintains its traction despite the efforts of multiple different groups to display it as antiquated. This led to a ridiculous perception among developers of Python and JavaScript as "newer" languages.

I think the solution for that is two-fold. We need to educate about the benefits of Java's approach to these solutions. We also need solutions like Manifold to explore potential directions freely. Without the encumberment of the JCP. Having a working proof of concept will make integrating new ideas into Java much easier.

<br />

<br />
