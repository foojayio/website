---
title: "Memory Debugging and Watch Annotations"
slug: "memory-debugging-and-watch-annotations"
date: "2022-05-27T08:04:34+00:00"
lastmod: "2022-05-27T08:04:35+00:00"
description: "RAM profiling has its strengths and weaknesses. The Debugger is a complementary tool to translate obtuse statistics to actionable changes!"
canonical: "https://talktotheduck.dev/memory-debugging-and-watch-annotations"
authors:
  - "shai-almog"
image: "Lightrun-Talk-to-the-duck-Debugging-Tutorial-Opt3.jpg"
categories:
  - "Tutorials"
  - "Uncategorized"
tags:
related_posts:
  - "the-basics-of-breakpoints-you-might-not-know"
  - "what-is-debugging-in-140-seconds"
  - "debugging-tutorial-java-return-value-intellij-jump-to-line-and-more"
  - "get-started-with-allocation-profiling"
enlighterjs: true
frozen: false
---

Before diving into debugging memory issues and the other amazing running process, memory debugging capabilities (which are amazing)... I want to discuss a point I left open in the [last duckling post](https://talktotheduck.dev/debugging-collections-streams-and-watch-renderers). Back there we discussed customizing the watch renderer. This is super cool!

But it's also tedious. Before we continue, if you prefer, I cover most of these subjects in these videos:
> 🦆 Duckling the 11th:  
>
> Did you know you can see all the objects in memory?  
>
> You can do a diff on memory objects between two breakpoints or a step-over operation.  
>
> With a literal stack trace directly to the allocating code![#CodeNewbie](https://twitter.com/hashtag/CodeNewbie?src=hash&ref_src=twsrc%5Etfw) [#140SecondDucklings](https://twitter.com/hashtag/140SecondDucklings?src=hash&ref_src=twsrc%5Etfw) [pic.twitter.com/6HNPH4pv0Y](https://t.co/6HNPH4pv0Y)
>
> --- Shai Almog (@debugagent) [April 19, 2022](https://twitter.com/debugagent/status/1516497057573388294?ref_src=twsrc%5Etfw)

<br />

Watch Annotations {#h2-0-watch-annotations}
-------------------------------------------

Last time we discussed customizing the watch UI to render complex objects more effectively. But there's one problem with that: "We aren't alone".

We're a part of a team. Doing this for every machine is difficult and frustrating. What if you're building a library or an API and want this behavior by default?

That's where JetBrains provides a unique solution: custom annotations. Just annotate your code with hints to the debugger and configuration will be seamless to your entire team/users. In order to do this, we need to add the JetBrains annotations to the project path. You can do it by adding this to the Maven POM file:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;dependency&gt;
 &lt;groupId&gt;org.jetbrains&lt;/groupId&gt;
 &lt;artifactId&gt;annotations&lt;/artifactId&gt;
 &lt;version&gt;23.0.0&lt;/version&gt;
&lt;/dependency&gt;</pre>

Once this is done, we can annotate the class from the previous duckling to achieve the same effect:

<pre class="EnlighterJSRAW" data-enlighter-language="java">import org.jetbrains.annotations.Debug.Renderer;

// snipped code ...

@Renderer(text = "\"Repository has \" + count() + \" elements\",",
  childrenArray = "finaAll()",
  hasChildren = "count() &gt; 0")
public interface VisitRepository extends JpaRepository&lt;Visit, Integer&gt; {
  // snipped code  ...
}</pre>

Notice we need to escape the strings in the annotation so they will be valid Java Strings. We need to escape the quote symbols and use them to write a "proper" string.

Again everything else matches the content and result we saw in the previous duckling.

Memory Debugger {#h2-1-memory-debugger}
---------------------------------------

The primary focus of this post is the memory debugging capabilities. By default, JetBrains disables most of these capabilities to boost program execution performance. We can enable the memory debugger view by checking it on the right-hand side of the bottom tool window.

![memory-debugging-1.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1650984733258/GIZp2-ogJ.png)

Worse. This has such an impact on performance that IntelliJ doesn't load the actual content of this class until we explicitly click the "Load Classes" button in the center of the memory monitor:

![memory-debugging-2.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1650984741434/KqTfJtrSW.png)

![memory-debugging-3.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1650984748815/CvRs-FN9m.png)

As you can imagine, this gets old fast. If your machine is slow, then this is a great thing. But if you have an exceptionally powerful machine, then you might want to turn on "Update Loaded Classes on Debugger Stop":

![memory-debugging-4.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1650984760974/wEfiGwUTX.png)

This effectively disables the requirement to click at the cost of slower step over execution. But what do we get as a result?

### Memory Usage {#h3-2-memory-usage}

The panel shows us where a memory block is used when stepping over code or jumping between breakpoints. The memory footprint isn't as obvious, but the scale of memory allocation is.

The diff column is especially useful in tracking issues such as memory leaks. You can get a sense of where a leaking object was allocated and the types of objects that were added between two points. You can get a very low level sense of the memory over time. It's a low level view that's more refined than the profiler view we normally use.

But there's more. We can double click every object on the list and see this:

![memory-debugging-5.jpeg](https://cdn.hashnode.com/res/hashnode/image/upload/v1650984775597/5YhGuNfeq.jpeg)

Here we can see all the objects of this type that were allocated in the entire heap. We can get a sense of what's really held in a memory location and again gain deeper insight into potential memory leaks.

### Memory Checks {#h3-3-memory-checks}

"Track New Instances" enables even more tracking of heap allocations. We can enable this on a per object type basis. Notice this only applies to "proper object" and not arrays. You can enable it through the right click:

![memory-debugging-6.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1650984788667/AP9SSjUSK.png)

Once we enable this, heap allocations are tracked everywhere. We get backtraces for memory allocations that we can use to narrow down the exact line of code that allocated every object in the heap!

![memory-debugging-7.png](https://cdn.hashnode.com/res/hashnode/image/upload/v1650984800338/mwYQ4Caq_.png)

The real benefit though is in the enhanced diff capability. When this is enabled, we can differentiate the specific objects allocated at this point. Say you have a block of code that leaks an object of type `MyObject`. If you enable tracking on `MyObject` and run between the two breakpoints, you can see every allocation of `MyObject` performed only in this block of code...

The backtraces for memory allocations are the missing piece that would show you where each of these object instances was allocated. Literal stack traces from the memory allocator!

This is difficult to see sometimes in memory intensive applications. When multiple threads allocate multiple objects in memory, the noise is hard to filter. But of all the tools I used, this is by far the easiest.

Finally {#h2-4-finally}
-----------------------

One of my favorite things in Java is the lack of real memory errors. There are no invalid memory addresses. No uninitialized memory that leads to invalid memory accesses. No invalid pointers, no memory address (that we're exposed to) or manual configuration. Things "just work".

But there are still pain points that go beyond [garbage collection tuning](https://talktotheduck.dev/debugging-ram-java-garbage-collection-java-heap-deep-dive-part-1). Heap size is one of the big pain points in Java. It doesn't have to be a leak. Sometimes it's just wastefulness we don't understand. Where does the extra memory go?

The debugger lets us draw a straight line with stack traces directly to the source code line. We can inspect memory contents and get the applicable memory statistics that go well beyond the domain of a profiler. Just to be clear: profilers are great to look at memory in a "big picture" way. The debugger can flesh out that picture with a complete list of for a specific block of code.
