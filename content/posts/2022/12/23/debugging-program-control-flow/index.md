---
title: "Debugging Program Control Flow"
date: "2022-12-23T07:36:35+00:00"
lastmod: "2022-12-23T07:36:36+00:00"
description: "Control flow is more than step over. You can \"jump\" to arbitrary code offsets while debugging to reproduce elaborate states and behaviors!"
canonical: "https://debugagent.com/debugging-program-control-flow"
authors:
  - "shai-almog"
image: "thumbnail-2.png"
categories:
  - "IntelliJ IDEA"
  - "Videos"
tags:
related_posts:
  - "springone-tlv-world-tour-trip-report"
  - "why-i-dont-do-tdd"
  - "internal-security-hardening-internal-systems"
  - "get-started-with-allocation-profiling"
frozen: false
---

As of now I published the first three videos of [the course](https://course.debugagent.com) and will publish the fourth tomorrow. I plan to publish two videos per week on YouTube to maximize the impact but here I'll only blog one lesson per week to avoid oversaturation. I shot about four hours of video content and still haven't finished the 2nd module out of eight. This course will be very detailed and intensive.

I'm working on the course platform right now, making good progress and hope to publish it soon enough [here](https://course.debugagent.com). It might already be live by the time you read this!

It's important to me that the user experience for the course platform is satisfying. So I avoided DRM as much as possible. Hopefully, people will respect that and the effort I'm putting into this. I'm building the course platform on Spring Boot 3 with GraalVM so it will ideally be lean, fast and pretty simple. I plan to host the videos on YouTube as unlisted videos to give a decent viewing experience. This means developers could betray my trust and share unlisted videos. I hope they don't.

My reasoning here is low overhead and excellent performance. I would also like to make the course free in a few years. By using YouTube, I can make the course free by making the videos public.

The following video discusses control flow in debugging. This starts with the basics but can get pretty deep for jump to line, force return, etc. These are tools that can significantly change the way we debug code.

Don't forget to check out my [book](https://www.amazon.com/dp/1484290410/) and subscribe to the [YouTube channel](https://www.youtube.com/@debugagent) for future videos!

{{< youtube GSeg5L31XXw >}}

## Transcript

Welcome back to the second part of debugging at Scale where you can learn the secret tricks of debugging.

In this section, we'll discuss the most basic aspect of debugging. We hit a breakpoint. Now what?

Well, this is where debuggers let us control the flow to investigate how everything works in a controlled environment.

So what's on the agenda for today?

We'll discuss stepping over and into code, I hope most of this list is familiar to you. The last two items where we disrupt the control flow might not be familiar to you. I'm pretty sure most of you aren't familiar with the last item on the agenda. How do I know? Stay tuned and find out!

### Step Over, Into, Out and Force

Step over is the most basic form of control flow. We let the code in the line execute and then we can inspect the results in the variable pane. It's simple and easy.

In this case I just pressed the button here a couple of times but I could also just press F8 to get the same effect...

Next we'll discuss two distinct operations, step into and the related step out. Step into goes into the method we invoke. Notice that if there's no method to go into the step into will act like step over.

We have two step-into operations. The regular one and the force-step into which normally acts the same way. We need the force version when we want to step into an API that IntelliJ will normally skip. We can press F7 to step into a method. We can press Shift F7 to force step into.

When we finished looking at a method and don't care about the rest, we can step out. This executes the rest of the method and returns. Notice that if we have a breakpoint before the return it would still stop at the breakpoint as we see in this case. We can press this button here to step out or, we can press shift-F8 to do the same thing.

### Continue and Run to Cursor

Continue proceeds with the execution of the project until the breakpoint is hit again. This is also called resume. It's a simple feature that we use a lot. You can continue by pressing the special version of the play button here. The shortcut is also helpful since we use it a lot, it's F9.

Run to cursor lets us skip lines that are uninteresting and reach the code that matters. We can set a breakpoint at that line to get the same effect, but this is sometimes more convenient as it removes the need to set and unset a breakpoint. We can press this button to run to the cursor, or we can use ALT-F9 as the shortcut for this feature.

### Force Return and Throw Exception

This feature is known as force return in IntelliJ/IDEA.

To see the force return option we right-click on the stack trace and can see a set of options. An even more interesting option is drop frame which I'll show soon. Notice the throw exception option which is identical to force return, but it throws an exception from the method. Once I click this option.

I'm shown a dialog to enter the return value from the method. This lets me change the value returned from the method which is very useful when I want to debug a hard to reproduce bug. Imagine a case where a failure happens to a customer, but you can't reproduce it. In this case I can simulate what the customer might be experiencing by returning a different value from the method.

Here the value is a boolean variable, so it's simple. But your code might return an object, using this technique you can replace that object with an arbitrary value. A good example would be null, what if this method returned null, would it reproduce the problem my user is experiencing?

Similarly throw exception lets us reproduce edge cases such as throwing an exception due to an arbitrary failure.

Once we press OK, we return with the different value. In this case I was at the edge of the method but I could have done it in the start of the method and skipped the execution of the method entirely. This lets us simulate cases where a method might fail, but we want to mock its behavior. That can make sense if we can't reproduce behavior seen by the customer. We can simulate by using tools like this.

### Drop Frame

Drop frame is almost as revolutionary but it's also more of a "neat trick". Here I stepped into a method by mistake. Oops I didn't want to do that. I wanted to change something before stepping in... Luckily there's drop frame. We saw I can reach it in the right-click menu, you can also click here to trigger it.

Drop frame effectively drops the stack frame. It's an undo operation. But it isn't exactly that. It can't undo the state changes that occurred within the method we stepped into. So if you stepped into the method and variables that aren't on the stack were changed, they would remain changed.

Variables on the stack are those variables that the method declares or accepts as arguments, those will be reset. However, if one of those variables points at an object then that object resides in heap and changes there can't be reset by unwinding the stack.

This is still a very useful feature similar to force return with the exception that it will return to the current line not the next line. So it won't return a value.

This gets even better than that!

### Jump to Line

Jump to line is a secret feature in IntelliJ. It works but developers don't know about it. You need to install the Jump to Line plugin to use it. Since it has a relatively low install count, I assume people just don't know it exists. Because this is a must have plugin. It will change the way you debug!

With jump to line we can move the current instruction pointer to a different position in the method. We can drag the arrow on the left to bring execution to a new location. Notice that this works in both directions, I can move the current execution back and forth.

This doesn't execute the code in between, it literally moves the current instruction to a new position. It's so cool, I have to show it again...

If you see a bug, just drag the execution back and reproduce it. You can change variable values and reproduce the issue over and over until you fully understand it.

We can skip over code that's failing, etc. This is spectacular. I don't need to recompile code. If you ever had a case where you accidentally stepped over a line and oops. That's not what I wanted. Then stopped the debugger and started from scratch. This is the plugin for you. This happened to everyone!

We can just drag the execution back and have a do-over. It's absolutely fantastic!

### Finally

In the next video we will discuss the watch briefly. We will dig much deeper into it in the sixth video in the series. So stay tuned!

If you have any questions, please use the comments section. Thank you!
