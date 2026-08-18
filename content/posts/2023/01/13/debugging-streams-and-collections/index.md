---
title: "Debugging Streams and Collections"
slug: "debugging-streams-and-collections"
date: "2023-01-13T14:59:59+00:00"
lastmod: "2023-01-13T15:01:35+00:00"
description: "Java 8 streams improved readability and provide great debugging opportunities. There are amazing tools for inspecting collections and arrays."
authors:
  - "shai-almog"
image: "thumbnail-5.png"
categories:
  - "IntelliJ IDEA"
  - "Tutorials"
  - "Videos"
tags:
related_posts:
  - "debugging-program-control-flow"
  - "debug-like-a-senior-developer"
  - "why-i-dont-do-tdd"
  - "spring-boot-debugging-with-aspect-oriented-programming-aop"
frozen: false
---

I will run a book giveaway promotion on the [Code Ranch on January 17th](https://coderanch.com/wiki/660305/Book-Promotions-Schedule). Be sure to be there and let your friends know. It would be great to answer your questions about debugging. I'm very excited by this and by the feedback I'm getting for [the course](https://course.debugagent.com/) and new videos.

I also launched a [free new Java course for complete beginners](https://www.youtube.com/playlist?list=PL8GhfcywW9YMucwRw2IbpeCp1FBMEgsmk). No prior knowledge needed. This is probably not the audience for this course. But if you know someone that might be interested I'll appreciate a share. I hope people find it useful and learn a bit about Java. I'm working on a cadence of one video per week in this beginner course.

I also have another upcoming course for modern Java development. It's landing really soon. Subscribe to [my channel](https://www.youtube.com/@debugagent) to keep track of that. It's also free at this time.

Finally, I finished scripting all the videos for the full debugging course. It came out to a nice round 50 videos. I will hopefully finish filming and uploading everything soon. This week's video covers collections and Java 8 stream debugging. Check it out...

{{< youtube fok4Icxsl2k >}}

## Transcript

Welcome back to the fifth part of debugging at Scale where we no longer stare blankly at the screen. We know where to look for that bug!

In this section we discuss streams and collections. These constructs are much harder to debug because of the issue of scale.

Notice that here I'm talking about Java 8 and higher streams. These streams come from the realm of functional programming. We'll dig deeper into them. But first I want to start by talking about filtering. Filtering is such a basic feature, that I'm amazed it took me so long to notice that it's there.

### Filtering

When we have code that views an array or collection content, we can filter the content using an expression and reduce the noise significantly. I can right-click any such collection and select the filter operation.

In this case I have four elements in the pet clinic demo but this is especially valuable for large collections. Try reviewing hundreds of results to find the entry you're looking for...

Here I reduce the content by testing against the pet id. Notice I use the keyword "this" to represent the current element in the list... I don't need to do that, I can just type the getter but using this and a dot opened up the code completion support.

Once I apply this you will notice the numbers "skip" everything that doesn't match. That means elements one and two are hidden and we go from zero to three. That makes it pretty easy to instantly see where the filter took effect.  

This is very versatile if we have a list of names we can filter it so we can only see applicable names etc. It works with arbitrary objects, arrays and all collection types.

### Java 8 Streams

For the next two features we need to discuss streams. Here we have a simple Java stream expression. These are functional expressions we can use to process multiple elements, let's review the various pieces of this expression. visits is a standard Java collection on which we invoke the stream method to get a functional interface we can work with.

Each operation in the stream transforms it to a different stream. In this case we filter out the duplicates within the stream. Map converts elements within the stream to a different type. In this case the visits are of type Visit. The map method is invoked for every element in the stream. In this case it converts the elements to PetDTO types. This operation maps from one type to another.

Finally, we collect all the elements in the stream into a set. We could collect them to a list or a different collection type. This is the value we return to the user. Now that we understand this I want to talk about a few important principals we saw here.  

The stream is self-contained. If we run it again, and again it will be idempotent that means the result doesn't change. That's good. In the map code I could just change a global variable or add an element to a list. This would be called a side effect, and it's a very bad thing to do in a stream. It will make it less debuggable and can cause problems with parallel streams, etc. I strongly suggest avoiding this and some things just won't work if you do it.

### Debugging Streams With Breakpoints

Let's review the process of debugging the stream. We can debug it like we would debug a loop. Add a breakpoint and place a condition on it, so it will only stop for the pet we care about which is pet ID number 7.  

As you can see I have that condition right here and I can stop at the specific entry.

As I press continue the loop keeps going and stops when the applicable entry is hit. I can also use a non-conditional breakpoint to stop and just keep pressing continue. This is tedious for a larger list.  

This would work exactly the same as a loop and would let us see everything. But is there a better way?

### The Stream Debugger

IntelliJ ships with a stream debugger which is a fantastic dedicated tool.  

When you stop on a breakpoint that includes a stream expression you can. See this button. Notice that it might be folded into a sub menu category depending on your version of intellij. This tool will only work if the stream has no side effects. If it does rely on an external variable it will fail, since the tool needs to manipulate the stream and run it to produce the results. If the stream has side effects it will trigger them and cause a problem. You will get a cryptic error message that's really hard to debug. So make sure the stream expression doesn't change anything outside the stream itself!

This launches the stream debugger. On the top you can see the stages of the stream expression. Notice that as I traverse through the stages of the expression the objects change and draw a line between their original mapping to the new one. Initially the list was of visits and now we see the conversion to the pets in each visit. Since the final stage is a set we will only get one instance of each pet. That isn't shown here.  

The advantage here is that you can see the entire process in a single view that you can take back and forth. Unlike a loop which you normally debug by stepping in the debugger. The view here is a bit more complete.  

This is inspired by time travel debuggers which is a unique branch of debugging I talk about in the book.

### Final Word

In the next video we'll discuss watch expressions which are far more elaborate than what you might expect...

Specifically renderers which are some of the cooler features in JetBrains IDEs.  

If you have any questions please use the comments section. Thank you!
