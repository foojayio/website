---
title: "What are you Missing by Debugging in VS Code?"
slug: "what-are-you-missing-by-debugging-in-vs-code"
date: "2023-02-17T16:20:16+00:00"
lastmod: "2023-02-17T16:20:17+00:00"
description: "16 missing features in the VS code debugger that are available in IntelliJ. Are they worth switching your main IDE? Detailed lists and videos!"
canonical: "https://debugagent.com/what-are-you-missing-by-debugging-in-vs-code"
authors:
  - "shai-almog"
image: "https://foojay.io/wp-content/uploads/2023/02/why-not-vs-code.png"
categories:
  - "IntelliJ IDEA"
  - "Tutorials"
  - "VS Code"
tags:
related_posts:
frozen: false
---

In the first chapter of my debugging book, I discuss IDE debugging.

In that chapter, I mostly talk about IntelliJ/IDEA.

People often ask me why I didn't write as much about VS Code...

The reason is that there isn't much to write about. Its debugger is simpler for better and for worse. It isn't as powerful as other IDEs. I created the following video that covers the content of this post:

{{< youtube OBgLeRwjlAc >}}

<br />

This isn't a slam against VS Code or against Microsoft. Visual Studio has one of the most powerful debuggers around. But Visual Studio Code doesn't have a lot of the features from Visual Studio or other IDEs. I believe this is intentional.

I think this is a user experience-driven decision in which they removed features to simplify usability. One thing VS Code did well was exposing the logpoint (tracepoint) feature, so it is more discoverable to the casual developer. That's pretty great and wouldn't have been practical if the IDE had all the salient features.

But there's a price that comes with simplicity.

As you can see in the following table there are many missing features that are available in IntelliJ. These are all features I covered in blog posts or videos. Notice that the video links in the following table are direct links to the specific time within the video.

|-----------------------------|-------------|--------------------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| **Feature**                 | **VS Code** | **Comments**                   | **Links**                                                                                                                            |
| Breakpoint                  | ✅           |                                | [Video](https://www.youtube.com/watch?v=eXRqKqSp7x0), [Post](https://debugagent.com/the-massive-hidden-power-of-breakpoints)         |
| Conditional Breakpoint      | ✅           |                                | [Video](https://www.youtube.com/watch?v=eXRqKqSp7x0), [Post](https://debugagent.com/the-massive-hidden-power-of-breakpoints)         |
| Logpoint/Tracepoint         | ✅           |                                | [Video](https://www.youtube.com/watch?v=eXRqKqSp7x0&t=508s), [Post](https://debugagent.com/the-massive-hidden-power-of-breakpoints)  |
| Step Over                   | ✅           |                                | [Video](https://www.youtube.com/watch?v=GSeg5L31XXw&t=44s), [Post](https://debugagent.com/debugging-program-control-flow)            |
| Step Into                   | ✅           |                                | [Video](https://www.youtube.com/watch?v=GSeg5L31XXw&t=70s), [Post](https://debugagent.com/debugging-program-control-flow)            |
| Step Out                    | ✅           |                                | [Video](https://www.youtube.com/watch?v=GSeg5L31XXw&t=70s), [Post](https://debugagent.com/debugging-program-control-flow)            |
| Continue                    | ✅           |                                | [Video](https://www.youtube.com/watch?v=GSeg5L31XXw&t=141s), [Post](https://debugagent.com/debugging-program-control-flow)           |
| Run to Cursor               | ✅           |                                | [Video](https://www.youtube.com/watch?v=GSeg5L31XXw&t=164s), [Post](https://debugagent.com/debugging-program-control-flow)           |
| Return Immediately          | ❌           | Restart Frame is available     | [Video](https://www.youtube.com/watch?v=GSeg5L31XXw&t=193s), [Post](https://debugagent.com/debugging-program-control-flow)           |
| Jump to Line                | ❌           |                                | [Video](https://www.youtube.com/watch?v=GSeg5L31XXw&t=409s), [Post](https://debugagent.com/debugging-program-control-flow)           |
| Return Value Display        | ✅           | (on by default)                | [Video](https://www.youtube.com/watch?v=DGjVVKCNosM&t=47s), [Post](https://debugagent.com/watch-and-evaluate)                        |
| Evaluate                    | ✅           |                                | [Video](https://www.youtube.com/watch?v=DGjVVKCNosM&t=89s), [Post](https://debugagent.com/watch-and-evaluate)                        |
| Watch                       | ✅           |                                | [Video](https://www.youtube.com/watch?v=DGjVVKCNosM&t=162s), [Post](https://debugagent.com/watch-and-evaluate)                       |
| Inline Watch                | ❌           |                                | [Video](https://www.youtube.com/watch?v=DGjVVKCNosM&t=162s), [Post](https://debugagent.com/watch-and-evaluate)                       |
| Set Value                   | ✅           |                                | [Video](https://www.youtube.com/watch?v=DGjVVKCNosM&t=226s), [Post](https://debugagent.com/watch-and-evaluate)                       |
| Object Marking              | ❌           |                                | [Video](https://www.youtube.com/watch?v=DGjVVKCNosM&t=301s), [Post](https://debugagent.com/watch-and-evaluate)                       |
| Method Breakpoints          | ❌           |                                | [Video](https://www.youtube.com/watch?v=eXRqKqSp7x0&t=72s), [Post](https://debugagent.com/the-massive-hidden-power-of-breakpoints)   |
| Field watchpoints           | ❌           |                                | [Video](https://www.youtube.com/watch?v=eXRqKqSp7x0&t=200s), [Post](https://debugagent.com/the-massive-hidden-power-of-breakpoints)  |
| Exception Breakpoints       | ✅           | They suck without filters      | [Video](https://www.youtube.com/watch?v=eXRqKqSp7x0&t=302s), [Post](https://debugagent.com/the-massive-hidden-power-of-breakpoints)  |
| Grouping/Naming Breakpoints | ❌           |                                | [Video](https://www.youtube.com/watch?v=eXRqKqSp7x0&t=682s), [Post](https://debugagent.com/the-massive-hidden-power-of-breakpoints)  |
| Disable Breakpoints         | ✅           |                                | [Video](https://www.youtube.com/watch?v=eXRqKqSp7x0&t=742s), [Post](https://debugagent.com/the-massive-hidden-power-of-breakpoints)  |
| Instance Filters            | ❌           |                                | [Video](https://www.youtube.com/watch?v=eXRqKqSp7x0&t=796s), [Post](https://debugagent.com/the-massive-hidden-power-of-breakpoints)  |
| Class Filters               | ❌           |                                | [Video](https://www.youtube.com/watch?v=eXRqKqSp7x0&t=913s), [Post](https://debugagent.com/the-massive-hidden-power-of-breakpoints)  |
| Caller Filters              | ❌           |                                | [Video](https://www.youtube.com/watch?v=eXRqKqSp7x0&t=950s), [Post](https://debugagent.com/the-massive-hidden-power-of-breakpoints)  |
| Filtering                   | ❌           | Array and Collection filtering | [Video](https://www.youtube.com/watch?v=fok4Icxsl2k&t=34s), [Post](https://debugagent.com/debugging-streams-and-collections)         |
| Stream Debugger             | ❌           |                                | [Video](https://www.youtube.com/watch?v=fok4Icxsl2k&t=294s), [Post](https://debugagent.com/debugging-streams-and-collections)        |
| Basic rendering             | ✅           | Very simplistic                | [Video](https://www.youtube.com/watch?v=oaUf8KXHsd0&t=177s), [Post](https://debugagent.com/watch-area-and-renderers)                 |
| Entry Rendering             | ❌           |                                | [Video](https://www.youtube.com/watch?v=oaUf8KXHsd0&t=254s), [Post](https://debugagent.com/watch-area-and-renderers)                 |
| Rendering Annotations       | ❌           |                                | [Video](https://www.youtube.com/watch?v=oaUf8KXHsd0&t=541s), [Post](https://debugagent.com/watch-area-and-renderers)                 |
| Thread View                 | ❌           |                                | [Video](https://www.youtube.com/watch?v=fPiTRdkJ6AQ&t=31s), [Post](https://debugagent.com/debugging-threads-and-asynchronous-code)   |
| Async Stack Traces          | ❌           | No custom support              | [Video](https://www.youtube.com/watch?v=fPiTRdkJ6AQ&t=345s), [Post](https://debugagent.com/debugging-threads-and-asynchronous-code)  |
| Searchable memory View      | ❌           |                                | [Video](https://www.youtube.com/watch?v=dFOFOEg2W4k&t=55s), [Post](https://debugagent.com/memory-debugging-a-deep-level-of-insight)  |
| Track new Instances         | ❌           |                                | [Video](https://www.youtube.com/watch?v=dFOFOEg2W4k&t=197s), [Post](https://debugagent.com/memory-debugging-a-deep-level-of-insight) |

The Missing Features {#h2-0-the-missing-features}
-------------------------------------------------

Following is a high-level overview of the missing features.

### Flow Control {#h3-1-flow-control}

[Return immediately](https://www.youtube.com/watch?v=GSeg5L31XXw&amp;t=193s) lets us return right away from a method and potentially return an arbitrary value. This is fantastic when you want to test edge cases.

There are also drop frame and throw exception features.

To be fair, VS Code has "restart frame" which is similar to "drop frame" and also nice.

[Jump to line](https://www.youtube.com/watch?v=GSeg5L31XXw&amp;t=409s) requires a plugin for IntelliJ. It lets us drag the execution pointer to an arbitrary location. If you have a bug, just drag the execution back and try again.

Need to skip a line of code because your app is in a problematic state but you still want to debug?

Drag forward. This is a fantastic killer feature when you need it.

### Watch Area {#h3-2-watch-area}

Both IDEs contain a watch but only IntelliJ can show the values of the [watch variables directly in the editor itself](https://www.youtube.com/watch?v=DGjVVKCNosM&amp;t=162s). This is very convenient when watching multiple values. It lets us see the stack at a glance as we scroll through the code.

[Object marking](https://www.youtube.com/watch?v=DGjVVKCNosM&amp;t=301s) is one of my favorite obscure features. It lets us dynamically declare a global variable that helps us track a value. We can use this global variable in a conditional breakpoint to verify things. One such example is saving the current thread as a marked object and then only breaking if we hit the method with a different thread.

### Breakpoints {#h3-3-breakpoints}

[Method breakpoints](https://www.youtube.com/watch?v=eXRqKqSp7x0&amp;t=72s) are pretty problematic but they have some edge uses. One of the big values is the ability to break when returning from a long method. This is helpful in tracing threading issues.

[Field watchpoints](https://www.youtube.com/watch?v=eXRqKqSp7x0&amp;t=200s) are very useful when tracking field mutation and new values.

We can manage breakpoints, [name, group](https://www.youtube.com/watch?v=eXRqKqSp7x0&amp;t=682s) and disable them as a hierarchy group. When dealing with multiple tasks and switching branches in the middle of a debugging session, we can keep that session on hold by grouping all the breakpoints together.

When we return to the task, we can instantly jump right back!

VS Code has exception breakpoints. But [without filters they absolutely suck](https://www.youtube.com/watch?v=eXRqKqSp7x0&amp;t=302s)!

We can filter breakpoint hits based on multiple criteria such as [instance](https://www.youtube.com/watch?v=eXRqKqSp7x0&amp;t=796s), [class](https://www.youtube.com/watch?v=eXRqKqSp7x0&amp;t=913s) or a [specific method in the stack](https://www.youtube.com/watch?v=eXRqKqSp7x0&amp;t=950s). I spent so much time pressing continue over and over again. We can reduce this pain using these tools.

### Arrays, Collections and Streams {#h3-4-arrays-collections-and-streams}

There's another spectacular type of [filtering](https://www.youtube.com/watch?v=fok4Icxsl2k&amp;t=34s). We can filter the content of an array or collection right in the watch or evaluate area. I spent a great deal of time digging through arrays of image data with thousands of elements. This was a nightmare. With this, we can find the entries that we need in a collection or array, instantly!

This is about the Java 8 and newer stream API which is a functional programming construct. It's a fantastic tool, but it makes debugging awkward. The [stream debugger](https://www.youtube.com/watch?v=fok4Icxsl2k&amp;t=294s) borrows concepts from time travel debuggers to make stream debugging easier than regular debugging sometimes.

### Entry Rendering {#h3-5-entry-rendering}

This is one of the most fantastic features you can think of. We can completely [customize the way entries look in the watch](https://www.youtube.com/watch?v=oaUf8KXHsd0&amp;t=254s). In the demo here, I show how I can expose the content of an Object Relational Mapping object as I step over in the debugger.

But this is hard to configure every time for every case. [Annotations let us configure this globally](https://www.youtube.com/watch?v=oaUf8KXHsd0&amp;t=541s) so we can see this every time for specific library objects when running in the debugger.

### Thread and Asynchronous Debugging {#h3-6-thread-and-asynchronous-debugging}

VS Code shows threads, but it has very limited display functionality and configurability. IntelliJ can open a [dedicated thread view](https://www.youtube.com/watch?v=fPiTRdkJ6AQ&amp;t=31s), hierarchies and much more.

It also supports [gluing asynchronous stack traces together](https://www.youtube.com/watch?v=fPiTRdkJ6AQ&amp;t=345s) to make it easier to debug asynchronous code. This works seamlessly with well known APIs and the really cool thing is that we can use annotations to add this to our custom APIs.

### Memory {#h3-7-memory}

We can search through memory to [find any object instance](https://www.youtube.com/watch?v=fPiTRdkJ6AQ&amp;t=345s). We can find VM internal instances and investigate issues by reviewing the objects in the system.

Better yet. We can [track every new instance of a particular class](https://www.youtube.com/watch?v=dFOFOEg2W4k&amp;t=197s). Get full stack traces to every new instance created between one breakpoint and another. This can track what happened under the hood with surgical precision.

Finally {#h2-8-finally}
-----------------------

There's a lot I didn't cover because there's just so much. I don't think VS Code is inherently bad. It just went for simplicity. Personally, I think of myself as a power user. If you're like me I hope this post gave you a sense of what you're missing.

Please check out [my book](https://www.amazon.com/dp/1484290410/), [my course](https://course.debugagent.com/), and [follow me](https://youtube.com/@debugagent) for videos like the one embedded above.
