---
title: "Memory Debugging - a Deep Level of Insight"
date: "2023-02-03T10:08:16+00:00"
lastmod: "2023-02-03T10:08:17+00:00"
description: "It isn't just about memory leaks. Understanding how RAM is used and its content provides insight into the app you can't get in any other way!"
canonical: "https://debugagent.com/memory-debugging-a-deep-level-of-insight"
authors:
  - "shai-almog"
image: "thumbnail-8.png"
categories:
  - "IntelliJ IDEA"
related_posts:
  - "debugging-streams-and-collections"
  - "the-massive-hidden-power-of-breakpoints"
  - "debugging-program-control-flow"
frozen: false
---

When I mention memory debugging the first thing that comes to the minds of many developers is the profiler. That isn't wrong but it's still a partial picture. Profilers are amazing at mapping that "big picture" but when you want to understand the domain, they fall short.

Modern debuggers let us gain a level of insight into the application that's unrivaled. We can inspect and locate a specific object instance with surgical precision.

{{< youtube dFOFOEg2W4k >}}

## Transcript

Welcome back to the eighth part of debugging at scale where we know exactly which object was allocated by whom and why.

### Profiler vs. Debugger

Profilers expose a lot of information about memory, but they don't give us the fine-grained view a debugger offers. The debugger can solve that last mile problem, it can connect the information you see in the debugger to actual actionable changes you need to make in the code.

The debugger perfectly complements the insights of the profiler. In the debugger we can pinpoint specific objects and memory locations. A profiler is a blunt instrument and the debugger is a fine one. By combining both we can zoom in on a specific issue and understand the root cause.

### Searchable Memory View

We'll start by launching the IDE memory view. We can enable the memory view by clicking the widget on the right side of the IDE here. Once we click it we can see the memory view in the same area. Notice that the memory view is empty by default even after we launch it. This keeps the IDE responsive. In order to see the actual objects in memory we need to click the load link in the center. Once loaded we can see the instance count for every object. This helps us get a sense of what exactly is taking up memory.

But that's only part of the story. When we step over there are allocations happening. We can see the difference between the current point and the one before when we look at the diff column. Notice when I say point I mean either the line before with a step over, but it can also apply for pressing continue between two breakpoints. In this case I can see the line I stepped over triggered the allocation of 70 byte arrays. That might seem like a lot but the IDE can't distinguish threads and a deep call graph, so we need to take the number with a grain of salt.

We can double-click an individual entry and see all the instances of the given object which is a remarkably powerful feature. I'll dig a bit deeper into this feature soon enough. As a reminder we can filter the objects we see here using the field on the top of the dialog and locate any object in memory. This is a very powerful tool.

### Update Loaded Classes

Clicking load classes every time is tedious. I have a fast machine with a lot of RAM. I can enable "Update Loaded Classes on Debugger Stop" and I will no longer need to press load explicitly. Only do that if your machine is a monster as this will slow down your debugging sessions noticeably. I'm enabling this here because I think it will make the video clearer.

### Track New Instances

You might have noticed that area on the right side of the instance view. We can enable it with the track new instances option. This option lets us explicitly track the individual allocations that are going on between two point. We can enable that by right-clicking any non-array object and enabling this option like we do here.

Once enabled we see a small watch sign next to the tracked object but there's a lot more involved as we continue the execution. I can now see only the objects allocated in this diff. We can understand exactly what happened in terms of RAM at great detail. Notice that here I can see the exact number of elements that were allocated here. There were a lot because I took a long pause waiting before stepping over. By clicking show new instances I get a special version of the instances dialog.

In this version of the dialog I only see the new instances created. The IDE knows exactly which objects were created between the last stop on a breakpoint and now. It only shows me these objects. For each of the individual objects. I can see the stack trace that triggered it all the way up to the literal call to new!

I can understand who created every object and follow the logic to why an object was created. I can double-click an entry in the stack and go to the applicable source code. This is a fantastic level of insight.

### Step-Over and Breakpoints

I discussed this before but these updates don't just work for step over. Everything I showed works exactly the same when jumping between two breakpoints. Even if they're in separate methods. The diff will be between those two points!

This is very powerful. You can slowly narrow the gap between two points as you discover which area of the code is taking up memory. Notice that memory allocation directly correlates to performance as garbage collection is a major source of performance overhead. This lets us narrow down the root cause.

## Final Word

In the next video we'll discuss remote debugging and its risks. I know what you might be thinking. I already know how to use remote debugging... This is a different video, we'll discuss tunneling, port-forwarding and the risks involved in doing all of that. If you have any questions please use the comments section. Thank you!
