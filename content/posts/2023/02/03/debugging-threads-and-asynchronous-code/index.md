---
title: "Debugging Threads and Asynchronous Code"
date: "2023-02-03T10:07:22+00:00"
lastmod: "2023-02-03T10:07:24+00:00"
description: "Track thread race conditions, figure out deadlocks, and understand the flow of asynchronous applications (even in custom code)."
canonical: "https://debugagent.com/debugging-threads-and-asynchronous-code"
authors:
  - "shai-almog"
image: "thumbnail-7.png"
categories:
  - "IntelliJ IDEA"
  - "Tutorials"
tags:
related_posts:
  - "debugging-streams-and-collections"
  - "the-massive-hidden-power-of-breakpoints"
  - "debugging-program-control-flow"
frozen: false
---

I'm doing a community interview in the [Code Ranch](https://coderanch.com/f/157/Cloud-Virtualization) drop by and ask a question to win a free book!

This week we'll discuss one of the harder problems in programming: threading. For many cases threading issues aren't as difficult to debug. At least not in higher abstractions.

Asynchronous programming is supposed to simplify the threading model but oftentimes it makes a bad situation worse by detaching us from the core context.

We discuss why that is and how debuggers solve that problem. We also explain how you can create custom asynchronous APIs that are almost as easy to debug as synchronous applications!

{{< youtube fPiTRdkJ6AQ >}}

## Transcript

Welcome back to the seventh part of debugging at scale where we don't treat debugging like taking out the garbage.

Concurrency and parallelism are some of the hardest problems in computer science. But debugging them doesn't have to be so hard. In this section we'll review some of the IDE capabilities related to threading as well various tricks and asynchronous code features.

### Thread Views

Let's start by discussing some of the elements we can enable in terms of the thread view. In the stack frame we can look at all the current threads in the combo box above the stack frame. We can toggle the currently selected thread and see the stack for that thread and the thread status. Notice that here we chose to suspend all threads on this breakpoint. If the threads were running we wouldn't be able to see their stack as it's constantly changing.

In the stack frame we can look at all the current threads in the combo box above the stack frame. We can toggle the currently selected thread and see the stack for that thread and the thread status. Notice that here we chose to suspend all threads on this breakpoint. If the threads were running we wouldn't be able to see their stack as it's constantly changing. We can enable the threads view on the right hand side pull down menu to see more...

As you can see viewing the stack is more convenient in this state when we're working with many threads. Furthermore, we can customize this view even more by going into the customize thread view and enabling additional options.  

The thread groups option is probably the most obvious change as it arranges all the threads based on their groups and provides a pretty deep view of the hierarchy. Since most frameworks arrange their threads based on categories in convenient groups this is often very useful when debugging many threads.

Other than that we can show additional information such as the file name, line number, class name and argument types. I personally like showing everything but this does create a somewhat noisy view that might not be as helpful. Now that we switched on the grouping we can see the hierarchy of the threads. This mode is a bit of a double-edged sword since you might miss out on an important thread in this case but If you have a lot of threads in a specific group it might be the only way you can possibly work. I think we'll see more features like this as project Loom becomes the standard and the thread count increases exponentially. I'm sure this section will see a lot of innovation moving forward.

### Debugging a Race Condition

Next we'll discuss debugging race conditions. The first step of debugging a race condition is a method breakpoint. I know what I said about them but in this case we need it. Notice the return statement in this method includes a lot of code. If I place a breakpoint on the last line it will happen before that code executes and my coverage won't include that part.

So let's open the breakpoint dialog and expand it to the fully customizable dialog. Now we need to define the method breakpoint. I type the message and then get the thread name. I only use the method breakpoint for the exit portion because if I used it for both I'd have no way to distinguish between exit and enter events. I make this a tracepoint by unchecking the suspend option. So now we have a tracepoint that prints the name of the thread that just exited the method.

I now do the exact same thing for a line breakpoint on the first line in the method. A line breakpoint is fine since entry to the method makes sense here. I change the label and make it also into a tracepoint instead of a breakpoint. Now we look at the console. I copy the name of the thread from the first printout in the console and add a condition to reduce the noise. If there's a race condition there must be at least one other thread right? So let's remove one thread to be sure...

Going down the list it's obvious that multiple threads enter the code. That means there's a risk of a race condition. Now it means I need to read the logs and see if an enter for one thread happened before the exit of another thread. This is a bit of work but is doable.

### Debugging a Deadlock

Next let's discuss deadlocks. Here we have two threads each is waiting on a monitor held by the other thread. This is a trivial deadlock but debugging is trivial even for more complex cases. Notice the bottom two threads have a "MONITOR" status. This means they're waiting on a lock and can't continue until it's released. Typically, you'd see this in Java as a thread is waiting on a synchronized block. You can expand these threads and see what's going on and which monitor is held by each thread. If you're able to reproduce a deadlock or a race in the debugger they are both simple to fix.

### Asynchronous Stack Traces

Stack traces are amazing in synchronous code but what do we do when we have asynchronous callbacks?

Here we have a standard Async Example from JetBrains that uses a list of tasks and just sends them to the executor to perform on a separate thread. Each task sleeps and prints a random number. Nothing to write home about, as far as demos go this is pretty trivial.

Here's where things get interesting. As you can see there's a line that separates the async stack from the current stack on the top. The IDE detected the invocation of a separate thread and kept the stack trace on the side. Then when it needed the information it took the stack trace from before and glued it to the bottom. The lower part of the stack trace is from the main thread and the top portion is on the executor thread. Notice that this works seamlessly with Swing, executors, Spring Async annotation etc. Very cool!

### Asynchronous Annotations

That's pretty cool but there's still a big problem. How does that work and what if I have custom code?

It works by saving the stack trace in places where we know an asynchronous operation is happening and then placing it later on when needed. How does it connect the right traces? It uses variable values. In this demo I created a simple listener interface. You'll notice it has no asynchronous elements in the stack trace.

By adding the async schedule and async executor annotations I can determine the point where an async code might launch which is the schedule marker. I can place it on a variable to indicate the variable I want to use to lookup the right stack trace. I do the same thing with execute and get custom async stack traces. I can put the annotations on a method and the current object will be used instead.

### Final Word

In the next video we'll discuss memory debugging. This goes beyond what the profiler provides, the debugger can be a complimentary surgical tool you can use to pinpoint a specific problem and find out the root cause.  

If you have any questions please use the comments section. Thank you!
