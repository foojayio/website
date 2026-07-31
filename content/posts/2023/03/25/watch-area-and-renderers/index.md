---
title: "Watch Area and Renderers"
slug: "watch-area-and-renderers"
date: "2023-03-25T07:25:03+00:00"
lastmod: "2023-03-25T07:25:05+00:00"
description: "Stop digging through variables in the watch to find nuggets of gold, or rerunning the expression evaluation. Use entity renderers instead."
canonical: "https://debugagent.com/watch-area-and-renderers"
authors:
  - "shai-almog"
image: "thumbnail-6.png"
categories:
  - "IntelliJ IDEA"
  - "Tutorials"
tags:
related_posts:
  - "debug-like-a-senior-developer"
  - "debugging-program-control-flow"
  - "why-i-dont-do-tdd"
  - "get-started-with-allocation-profiling"
frozen: false
---

This is it. The [debugging book is live](https://www.amazon.com/dp/1484290410/). I would really appreciate reviews and feedback!

I also finished recording and editing the [entire course](https://course.debugagent.com/).

There are 50 total videos which total in 7 hours... I also recorded additional videos for the two other free courses [for beginners](https://www.youtube.com/watch?v=1Bum2gYETUQ&amp;list=PL8GhfcywW9YMucwRw2IbpeCp1FBMEgsmk) and for [modern Java](https://www.youtube.com/watch?v=3oNT_VRrfqE&amp;list=PL8GhfcywW9YNuNHrG0FAg5xiprfYcYyuL).

So keep an eye on those!

Renderers {#h2-0-renderers}
---------------------------

In today's video we discuss one of my favorite obscure IDE features: renderers. Very few people are aware of them. I explained them in the past but I feel I didn't properly explain why they are so much better than any other alternative. This time I think I got the explanation right.

If you work with JPA or any elaborate API you should check this out, I think the demo is revolutionary. If you provide a complex library to developers this can also be an amazing tool.

{{< youtube oaUf8KXHsd0 >}}

<br />

Transcript {#h2-1-transcript}
-----------------------------

Welcome back to the sixth part of debugging at scale where the bugs come to die.  

In this section we discuss the watch area. The watch is one of the most important areas in the debugging process.

Yet we don't give it nearly as much attention as we should. In this segment we'll discuss a few of the powerful things we can do in the watch area and how we can extend it to support some fantastic capabilities.

### Mute Renderers {#h3-2-mute-renderers}

Let's start with mute renderers which lets us improve the performance of the watch area. Before we discuss that I'd like to talk about the watch area itself. This is the watch area. In it, we can see most of the visible variables for the current stack frame and their values. We can expand entries within the watch area. Every variable value or expression we have in the watch is an entry. We can add arbitrary elements to the watch and even embed watch expressions directly into the IDE user interface.

Notice the values on the right hand side these are typically the results of the toString() method. In IntelliJ, we can customize these via renderers which we will discuss further. But there's more to it as we'll see later on. For now just consider this. Every time I step over a line of code the IDE needs to collect all the variables in scope and invoke `toString()` on every one of them. In some case even more elaborate code. This is expensive and slow...

In the right click menu we have the mute renderers option. By checking this option we can disable that behavior and potentially speed up the step-over speed significantly. Once we select that you will notice that all the values turn to three dots followed by the word toString. This means the renderers don't fetch the value anymore. They instead show this placeholder. This removes the overhead of the renderers completely and can speed up your step-over performance.

If we want to see the value we can click the toString label and the value is extracted dynamically. Notice that this only impacts the objects. Primitives, arrays etc. are unaffected by this feature.

### Customize Rendering {#h3-3-customize-rendering}

Rendering is the process of drawing the element values in the watch. To get started with rendereres we need to customize them through the right click menu here. This launches the renderer customization dialog which lets us do amazing things in intellij.

For the most basic customization we can toggle and enable multiple features within this dialog. Then press apply to instantly see them in the variables view below. I can see the declared type of the field.

We can include fully qualified names for class files we can see the static field values.  

We can include hex values for primitives by default which is a feature I always enable because it's so useful for me. This is an amazing view that's worth exploring and customizing to fit your own preference where you can tune the verbosity level in the watch area.

But the real power of this dialog is in the second tab. The java type renderer which is the next subject.

### Data Rendering {#h3-4-data-rendering}

We can go so much further with renderers. You might recall the visit objects I've shown before. This is from a standard Spring Boot demo called pet clinic. Spring Boot has the concept of a Repository which is an interface that represents a datasource. Often a repository is just a table, it can do more, but it has a strong relation to an underlying SQL table, and it helps to think about it in these terms.  

If you look at the visitRepository and perRepository objects at the bottom of the screen you'll notice that we don't have much to go on. These are just object IDs with no data that's valuable for a person debugging the objects. I didn't expand them but there's nothing under the variables here either.

Lets fix that in the customize data view as we did before. We add a renderer that applies to `JpaRepository` which is the base interface of this instance. Then we just write the expression to represent the rendering here. This renderer will apply to JPA repository and its sub interfaces or classes.

Next instead of using the default renderer I use an expression to indicate what we will show. The JPARepository includes a method called `count()` which queries the database and counts the number of elements within the database. I simply invoke it, notice I assume that the current object is the object being rendered. I don't provide an object instance. The IDE automatically runs in the context of the object. You can also use `this` to represent the rendered object. Notice I don't need to cast to a JPARepository.

This means the expression will be rendered directly in the watch without changing the toString method which in this case I obviously can't change and usually might not want to. The `toString()` method is useful in production, I wouldn't want expensive code in there. But in the renderer I can just go wild and do things that don't make sense in the repository.

Notice the on-demand checkbox. If we check this the expression will act like a muted renderer by default. You will need to click it to see the value.

Let's apply this change to the code and you'll notice the visitRepository instantly changes to use the new expression we defined. We can now immediately see that the repository has 4 elements which is pretty cool. Right?

Notice that petRepository isn't changed, this is because it's a repository too, but it isn't a `JPARepository`.

So far we did stuff that can theoretically be done by `toString()` methods. It might be hacky, but it's not something unique. Let's take this up a notch.

The "When expanding node" option lets us define the behavior when a user expands the entry. The `findAll()` method of `JPARepository` returns all the entities in the repository, this will be invoked when we expand the entry.

We can optionally check if there's a reason to show the expand widget. In this case I use the `count()` method which would be faster than repeatedly calling `findAll()`. Once we apply the changes we can see the elements from the repository listed. You'll notice all 4 elements are here and since they are objects we can see all the attributes like any object we see in the watch.

This is truly spectacular, and you can't fake it with a `toString()` call...

### Doing it for Everyone {#h3-5-doing-it-for-everyone}

That was a cool feature right? But it's so annoying to configure all of that stuff for every project. Here we see the same renderer from before, you'll notice that everything looks exactly the same. The numbering, the list of entities etc. But when we open the list of renderers it's blank, there are no renderers!

How does this suddenly work without a renderer?

What's going on?

We use code annotations to represent the renderer.

This way you can commit the renderer to the project repository, and you don't need to configure individual IDE instances. This is pretty simple, we add a dependency on the JetBrains annotation library into the POM.

This is an annotation library. That means the code doesn't change in a significant way. It's just markers. Since it's just hints to the debugger it's ignored in runtime and doesn't have any implications or overhead.

We add an import to the renderer then we scroll down and we added simple renderer annotation code. Notice that this is pretty much the code I typed in the dialog but this time it's used in an annotation this way our entire team can benefit from the improved view of repository objects!

If you're building libraries or frameworks you can integrate this to make the debugging experience easier to your users without impacting the behavior of the `toString()` methods or similar semantics.

Finally {#h2-6-finally}
-----------------------

In the next video we'll discuss threading issues. Their reputation as "hard to debug" isn't always justified.

If you have any questions please use the comments section. Thank you!
