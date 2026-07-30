---
title: "Allocation Profiling in Java"
slug: "get-started-with-allocation-profiling"
date: "2024-08-09T08:44:59+00:00"
lastmod: "2024-11-12T16:25:52+00:00"
description: "Trouble finding memory leaks in a Java program? Learn how allocation profiling can reveal bugs and help you troubleshoot Java performance."
canonical: "https://flounder.dev/posts/allocation-profiling/"
authors:
  - "igor-kulakov"
image: "https://foojay.io/wp-content/uploads/2024/08/banner.png"
categories:
  - "Debugging"
  - "IntelliJ IDEA"
  - "Java"
  - "Tools"
  - "Tutorials"
  - "Use Cases"
tags:
related_posts:
  - "9-best-java-profilers-to-use-in-2024"
  - "a-short-primer-on-java-debugging-internals"
  - "analyzing-and-tuning-warm-up-of-the-jvm-with-azul-zulu-prime-builds-of-openjdk"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
enlighterjs: true
frozen: false
---

**Read in other languages** : [中文](https://flounder.dev/zh/posts/allocation-profiling/) [Español](https://flounder.dev/es/posts/allocation-profiling/) [Português](https://flounder.dev/pt/posts/allocation-profiling/)

We often find ourselves in situations when code is not working properly, and we have no idea where to even begin investigating.

Can't we just stare at the code until the solution eventually comes to us? Sure, but this method probably won't work without deep knowledge of the project and a lot of mental effort. A smarter approach would be to use the tools you have at hand. They can point you in the right direction.

In this post, we'll look at how we can profile memory allocations to solve a runtime problem.

The problem {#h2-0-the-problem}
-------------------------------

Let's start with cloning the following repository: <https://github.com/flounder4130/party-parrot>.

Launch the application using the **Parrot** run configuration included with the project. The app seems to work well: you can tweak the animation color and speed. However, it's not long before things start going wrong.
![The parrot animation is frozen](https://flounder.dev/img/profile-memory-allocations/parrot-freeze.png)

After working for some time, the animation freezes with no indication of what the cause is. The program can sometimes throw an `OutOfMemoryError`, whose stack trace doesn't tell us anything about the origin of the problem.

There is no reliable way of telling how exactly the problem will manifest itself. The interesting thing about this animation freeze is that we can still use the rest of the UI after it happens.

**Note**: I used Amazon Corretto 11 for running this app. The result may differ on other JVMs or even on the same JVM if it uses a different configuration.

The debugger {#h2-1-the-debugger}
---------------------------------

It seems we have a bug. Let's try using the debugger! Launch the application in debug mode, wait until the animation freezes, then hit [Pause Program](https://flounder.dev/posts/debug-without-breakpoints/).

<figure class="wp-block-image is-resized">
 <img decoding="async" src="https://flounder.dev/img/profile-memory-allocations/debugger-threads-dark.png" alt="Threads view in the debugger shows a stack, which seems unrelated to the bug" style="width:811px">
</figure>

Unfortunately, this did not tell us much because all the threads involved in the parrot party are in the waiting state. Inspecting their stacks gives no indication why the freeze happened. Clearly, the we need another approach rather than [treating this error as a regular exception](https://flounder.dev/posts/efficient-debugging-exceptions/).

Monitor resources' usage {#monitor-resources-usage}
---------------------------------------------------

Since we are getting an `OutOfMemoryError`, a good starting point for analysis is **CPU and Memory Live Charts** . They allow us to visualize real-time resources usage for the processes that are running. Let's [open the charts](https://www.jetbrains.com/help/idea/cpu-and-memory-live-charts.html) for our parrot app and see if we can spot anything when the animation freezes.

<figure class="wp-block-image is-resized">
 <img decoding="async" src="https://flounder.dev/img/profile-memory-allocations/cpu-memory-charts-1-dark.png" alt="Memory usage chart shows that the amount of used memory increases and then flats out" style="width:642px">
</figure>

Indeed, we see that the memory usage is going up continually before reaching a plateau. This is precisely the moment when the animation hangs, and after that it seems to hang forever.

This gives us a clue. Usually, the memory usage curve is saw-shaped: the chart goes up when new objects are allocated and periodically goes down when the memory is reclaimed after garbage-collecting unused objects. You can see an example of a normally operating program in the picture below:

<figure class="wp-block-image is-resized">
 <img decoding="async" src="https://flounder.dev/img/profile-memory-allocations/cpu-memory-charts-normal-dark.png" alt="Screenshot of a memory usage chart where used memory constantly goes up but then goes down regularly" style="width:642px">
</figure>

If the saw teeth become too frequent, it means that the garbage collector is working intensively to free up the memory. A plateau means it can't free up any.

We can test if the JVM is able to perform a garbage collection by explicitly requesting one:

<figure class="wp-block-image is-resized">
 <img decoding="async" src="https://flounder.dev/img/profile-memory-allocations/gc-dark.png" alt="'Perform GC' button on the toolbar of 'CPU and Memory Live Charts'" style="width:482px">
</figure>

Memory usage does not go down after our app reaches the plateau, even if we manually prompt it to free up some memory. This supports our hypothesis that there are no objects eligible for garbage collection.

A naïve solution would be to just add more memory. For this, add the `-Xmx500m` VM option to the run configuration.

<figure class="wp-block-image is-resized">
 <img decoding="async" src="https://flounder.dev/img/profile-memory-allocations/add-memory-dark.png" alt="Adding -Xmx500m VM option in the 'Run/Debug Configurations' dialog" style="width:773px">
</figure>

**Tip**: To quickly access the settings of the currently selected run configuration, hold 'Shift' and click the run configuration name on the main toolbar.

Regardless of the available memory, the parrot runs out of it anyway. Again, we see the same picture. The only visible effect of extra memory was that we delayed the end of the "party".

<figure class="wp-block-image is-resized">
 <img decoding="async" src="https://flounder.dev/img/profile-memory-allocations/cpu-memory-charts-2-dark.png" alt="Memory usage chart shows that now there is 500M available memory, but the app uses it all anyway" style="width:642px">
</figure>

Allocation profiling {#h2-3-allocation-profiling}
-------------------------------------------------

Since we know our application never gets enough memory, it is reasonable to suspect a memory leak and analyze its memory usage. For this, we can collect a memory dump using the `-XX:+HeapDumpOnOutOfMemoryError` VM option. This is a perfectly acceptable approach for inspecting the heap; however, it will not point at the code responsible for creating these objects.

We can get this information from a profiler snapshot: not only will it provide statistics on the types of the objects, but it will also reveal the stack traces corresponding to when they were created. Although this is a little unconventional use case for allocation profiling, nothing prevents us from using it to identify the issue.

Let's [run the application with IntelliJ Profiler](https://flounder.dev/posts/get-started-with-profiling/) attached. While running, the profiler will periodically record the threads' state and collect data about memory allocation events. This data is then aggregated in a human-readable form to give us an idea of what the application was doing when allocating these objects.

After running the profiler for some time, let's open the report and select **Memory Allocations**:

<figure class="wp-block-image is-resized">
 <img decoding="async" src="https://flounder.dev/img/profile-memory-allocations/switch-to-allocations-dark.png" alt="The 'Memory Allocations' item in the 'Show' menu in the top-right corner of the 'Profiler' tool window" style="width:657px">
</figure>

There are several views available for the collected data. In this tutorial, we will use the [flame graph](https://www.brendangregg.com/FlameGraphs/cpuflamegraphs.html). It aggregates the collected stacks in a single stack-like structure, adjusting the element width according to the number of collected [samples](https://www.jetbrains.com/help/idea/cpu-and-allocation-profiling-basic-concepts.html#cpu-profiling). The widest elements represent the most massively allocated types during the profiling period.

An important thing to note here is that a lot of allocations don't necessarily indicate a problem. A memory leak happens *only* if the allocated objects are not garbage-collected. While allocation profiling doesn't tell us anything about the garbage collection, it can still give us hints for further investigation.

<figure class="wp-block-image is-resized">
 <img decoding="async" src="https://flounder.dev/img/profile-memory-allocations/flame-graph-1-dark.png" alt="The two largest frames in the allocation graph are int[] and byte[]" style="width:657px">
</figure>

Let's see where the two most massive elements, `byte[]` and `int[]` are coming from. The top of the stack tells us that these arrays are created during image processing by the code from the `java.awt.image` package. The bottom of the stack tells us that all this happens in a separate thread managed by an executor service. We aren't looking for bugs in library code, so let's look at the project code that's in between.

Going from top to bottom, the first application method we see is `recolor()`, which in turn is called by `updateParrot()`. Judging by the name, this method is exactly what makes our parrot move. Let's see how this is implemented and why it needs that many arrays.

<figure class="wp-block-image is-resized">
 <img decoding="async" src="https://flounder.dev/img/profile-memory-allocations/flame-graph-2-dark.png" alt="Pointing at the updateParrot() method on the flame graph" style="width:657px">
</figure>

Clicking at the frame takes us to the source code of the corresponding method:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public void updateParrot() { 
    currentParrotIndex = (currentParrotIndex + 1) % parrots.size();
    BufferedImage baseImage = parrots.get(currentParrotIndex);
    State state = new State(baseImage, getHue());
    BufferedImage coloredImage = cache.computeIfAbsent(state, (s) -&gt; Recolor.recolor(baseImage, hue));
    parrot.setIcon(new ImageIcon(coloredImage)); 
}</pre>

It seems that `updateParrot()` takes some base image and then recolors it. In order to avoid extra work, the implementation first tries to retrieve the image from some cache. The key for retrieval is a `State` object, whose constructor takes a base image and a hue:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public State(BufferedImage baseImage, int hue) {
    this.baseImage = baseImage;
    this.hue = hue;
}</pre>

Analyze data flow {#h2-4-analyze-data-flow}
-------------------------------------------

Using the built-in static analyzer, we can trace the range of input values for the `State` constructor call. Right-click the `baseImage` constructor argument, then from the menu, select **Analyze** \| **Data Flow to Here**.

<figure class="wp-block-image is-resized">
 <img decoding="async" src="https://flounder.dev/img/profile-memory-allocations/data-flow-to-here-1-dark.png" alt="'Analyze dataflow to' tool window shows possible sources of values as nodes" style="width:1241px">
</figure>

Expand the nodes and pay attention to `ImageIO.read(path.toFile())`. It shows us that the base images come from a set of files. If we double-click this line and look at the `PARROTS_PATH` constant that is nearby, we discover the files' location:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public static final String PARROTS_PATH = "src/main/resources";</pre>

By navigating to this directory, we can see the following:

<figure class="wp-block-image is-resized">
 <img decoding="async" src="https://flounder.dev/img/profile-memory-allocations/files-location-dark.png" alt="10 image files under src/main/java in the 'Project' tool window" style="width:384px">
</figure>

That's ten base images that correspond to the possible positions of the parrot. Well, what about the `hue` constructor argument?

<figure class="wp-block-image is-resized">
 <img decoding="async" src="https://flounder.dev/img/profile-memory-allocations/data-flow-to-here-2-dark.png" alt="'Analyze dataflow to' tool window shows possible sources of values as nodes" style="width:754px">
</figure>

If we inspect the code that modifies the `hue` variable, we see that it has a starting value of `50`. Then it is either set with a slider or updated automatically from the `updateHue()` method. Either way, it is always within the range of `1` to `100`.

So, we have 100 variants of hue and 10 base images, which should guarantee that the cache never grows bigger than 1000 elements. Let's check if that holds true.

Conditional breakpoints {#h2-5-conditional-breakpoints}
-------------------------------------------------------

Now, this is where the debugger can be useful. We can check the size of the cache with a conditional breakpoint.

**Note** : Setting a conditional breakpoint in hot code [might significantly slow down](https://flounder.dev/posts/troubleshoot-slow-debugging/#conditional-breakpoints-in-hot-code) the target application.

Let's set a breakpoint at the update action and add a condition so that it only suspends the application when the cache size exceeds 1000 elements.

<figure class="wp-block-image is-resized">
 <img decoding="async" src="https://flounder.dev/img/profile-memory-allocations/conditional-breakpoint-dark.png" alt="Breakpoint settings dialog with a condition" style="width:946px">
</figure>

Now run the app in debug mode.

<figure class="wp-block-image is-resized">
 <img decoding="async" src="https://flounder.dev/img/profile-memory-allocations/stopped-at-breakpoint-dark.png" alt="A highlighted line indicates that breakpoint worked and the debugger suspended the application" style="width:938px">
</figure>

Indeed, we stop at this breakpoint after running the program for some time, which means the problem is indeed in the cache.

Inspect the code {#h2-6-inspect-the-code}
-----------------------------------------

[Cmd + B](https://www.jetbrains.com/help/idea/navigating-through-the-source-code.html#go_to_declaration) on `cache` takes us to its declaration site:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">private static final Map&lt;State, BufferedImage&gt; cache = new HashMap&lt;&gt;();</pre>

If we check the documentation for `HashMap`, we'll find that its implementation relies on the `equals()` and `hashcode()` methods, and the type that is used as the key has to correctly override them. Let's check it. [Cmd + B](https://www.jetbrains.com/help/idea/navigating-through-the-source-code.html#go_to_declaration) on `State` takes us to the class definition.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">class State {
    private final BufferedImage baseImage;
    private final int hue;

    public State(BufferedImage baseImage, int hue) {
        this.baseImage = baseImage;
        this.hue = hue;
    }

    public BufferedImage getBaseImage() { return baseImage; }

    public int getHue() { return hue; }
}</pre>

Seems like we have found the culprit: the implementation of `equals()` and `hashcode()` isn't just incorrect. It's completely missing!

Override methods {#h2-7-override-methods}
-----------------------------------------

Writing implementations for `equals()` and `hashcode()` is a mundane task. Luckily, modern tools can generate them for us.

While in the `State` class, press [Cmd + N](https://www.jetbrains.com/help/idea/generating-code.html) and select **equals() and hashcode()** . Accept the suggestions and click **Next** until the methods appear at the caret.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    State state = (State) o;
    return hue == state.hue &amp;&amp; Objects.equals(baseImage, state.baseImage);
}

@Override
public int hashCode() {
    return Objects.hash(baseImage, hue);
}</pre>

Check the fix {#h2-8-check-the-fix}
-----------------------------------

Let's restart the application and see if things have improved. Again, we can use [CPU and Memory Live Charts](#monitor-resources-usage) for that:

<figure class="wp-block-image is-resized">
 <img decoding="async" src="https://flounder.dev/img/profile-memory-allocations/cpu-memory-charts-3-dark.png" alt="The graph in 'CPU and Memory Live Charts' no longer flats out and goes down regularly" style="width:807px">
</figure>

That is much better!

Summary {#h2-9-summary}
-----------------------

In this article, we looked at how we can start with the general symptoms of a problem and then, using our reasoning and the variety of tools available to us, narrow the scope of the search step-by-step until we find the exact line of code that's causing the problem.

More importantly, we made sure that the parrot party will go on no matter what!

If you're interested in more articles on profiling and debugging, check out my other posts:

* [What's Wrong With createDirectories() -- Guide to CPU Profiling](https://flounder.dev/posts/get-started-with-profiling/)
* [Debugger.godMode() -- Hacking JVM Applications With the Debugger](https://flounder.dev/posts/debugger-god-mode/)
* [Profile IntelliJ IDEA With Its Own Profiler](https://flounder.dev/posts/profile-idea-with-idea/)
* [Debug Unresponsive Apps](https://flounder.dev/posts/debug-unresponsive-apps/)

As always, I will be happy to hear your feedback!

<br />

<br />
