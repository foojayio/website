---
title: "The Massive Hidden Power of Breakpoints"
slug: "the-massive-hidden-power-of-breakpoints"
date: "2023-01-13T14:58:57+00:00"
lastmod: "2023-01-13T15:02:43+00:00"
description: "Learn about tracepoints (AKA logpoints) how exception breakpoints don't suck, watchpoints, filters & why method breakpoints are problematic."
canonical: "https://debugagent.com/the-massive-hidden-power-of-breakpoints"
authors:
  - "shai-almog"
image: "https://foojay.io/wp-content/uploads/2022/12/thumbnail-4.png"
categories:
  - "IntelliJ IDEA"
tags:
related_posts:
  - "debug-like-a-senior-developer"
  - "debugging-program-control-flow"
  - "springone-tlv-world-tour-trip-report"
  - "what-are-you-missing-by-debugging-in-vs-code"
frozen: false
---

It's been a big week. I'm currently reviewing the final draft of [my upcoming debugging book](https://www.amazon.com/dp/1484290410/). This is always a sobering, and exciting moment. A moment in which all of a sudden all the months of work become "real".

I also recorded 9 videos for youtube bringing up the total videos in the course to 29 (with many more on the way). I'm starting two additional free courses [one targeted at absolute beginners](https://www.youtube.com/playlist?list=PL8GhfcywW9YMucwRw2IbpeCp1FBMEgsmk). Another one about modern Java programming which I'll launch really soon. Both will be free on [my youtube channel](https://youtube.com/@debugagent) (obligatory "like, subscribe and share").

In this weeks post we'll talk about breakpoints. It's a long one since what we mean when we say breakpoint is really just "line-breakpoint". There's a lot more to them as you can see below.

{{< youtube eXRqKqSp7x0 >}}

<br />

Transcript {#h2-0-transcript}
-----------------------------

Welcome back to the forth part of debugging at Scale where we kick ass and take names. Variable names!

In this section we discuss breakpoints which are the most basic units of work when debugging. But there's so much more to them than just breaking.

We talked about the most basic breakpoint in our first installment. This time we'll dig a bit deeper and into some of the lesser known nuances.

### Conditional Breakpoints {#h3-1-conditional-breakpoints}

We start with conditional breakpoints. Conditional breakpoints lets us define a condition for a breakpoint hit. This prevents the thread from stopping constantly on a breakpoint. I discussed this before with the myThread marker object we created in the previous video. In this case we only stop if it's different from current thread. That means this breakpoint will only hit if a different thread invokes it. It's a great way to detect thread related problems like deadlocks or races. This feature is worth repeating since it's such an important feature.

### Method Breakpoints {#h3-2-method-breakpoints}

Method breakpoints are pretty problematic.

As a refresher we can place a line breakpoint on any line in the method and the breakpoint will stop there. This is so ubiquitous that we just call it a "breakpoint".

We can toggle the standard line breakpoint with control-F8 or alternatively on a mac with `Command-F8`.

Method breakpoints stop when we enter a method. You might think this is useless. Why not use a line breakpoint?

You'd be right. Method breakpoints are much slower and you shouldn't use them: for this...  There is a use case for method breakpoints though.  

Notice that because method breakpoints are so slow they are usually emulated by the IDE. It just uses line breakpoints to simulate method breakpoints. This is mostly seamless to us as users of the IDE but we need to know about it because in the past this wasn't the case. You can still find messages from users on StackOverflow complaining about the slowness of method breakpoints.

To see the use case for method breakpoints lets go into the manage breakpoints window. Say we want to break on all the methods starting with the letters I and S in classes whose name starts with Prime. We can do this by using an expression like this to stop on all such methods based on a pattern. This might seem far fetched but is actually very useful.

If you have an abstract base class whose subclasses follow a naming convention and they have many related methods. You want to track everything and you can do that using this approach. Notice you can also use tracepoints here and get very deep logging. We'll discuss tracepoints in a few minutes.

### Field Watchpoints {#h3-3-field-watchpoints}

Field watchpoints aren't your typical breakpoint. A watchpoint will stop every time the value of the field changes or every time it's read. This is a remarkably cool way to catch a case where some code mutates a variable or find out how a field value propagates into the code.

Notice we can tune whether it stops at read operations, write operations or both using this dialog. We can also make the watchpoint conditional just like we can with any other breakpoint.

It's called a watchpoint and not a breakpoint because it isn't the point where the code stops. It stops at the point of access not on the field itself.

### Breakpoint Management {#h3-4-breakpoint-management}

IDEs provide a management UI for all the breakpoints. We can manage the breakpoints we already have and create new breakpoints in the view breakpoints menu. I can open it via the view breakpoints menu option or I can use `Shift-Control-F8` key combination. Notice that on a mac we need to use command instead of the control key.

In this dialog we can disable, delete and edit breakpoints. You'll notice we have a lot of options here that we'll discuss soon. We can add breakpoints from here. There are several interesting options but now I'll just add a simple field breakpoint.

### Exception Breakpoints Suck (Or do they?) {#h3-5-exception-breakpoints-suck-or-do-they}

We can set a breakpoint to stop when an exception is thrown. But it's a bit of a problem. I have two options. First I can catch a specific exception by name. This is useful if you know in advance the exception that will be thrown. But I can't think of many cases where this happened and I didn't already know the line where the exception is thrown.

The more valuable use case is catching all exceptions. The reason this is useful is that I sometimes might not look at the console while debugging. Exceptions might be logged there and I might miss them entirely. I might restart the debug session and might miss these exceptions. But if a breakpoint suddenly stops on an exception, that's hard to miss. The problem is that catching all exceptions is broken by default!  

Unfortunately this is hard to show in a simple prime main application so I switched the demo to a simple spring boot application. The content of the application isn't important for this case. Let's enable catching any exception and see what happens...

After enabling the the catch I try to continue but it instantly hits the breakpoint again and again and again. The code polls a WebService in a the background. That WebService is missing an HTTP header so the code that parses that header fails on a NumberFormatException. We're stuck on the code that throws that exception which is valid as Java didn't provide another way to parse numeric headers when that code was written.

So what can we do? This effectively makes stopping on any exception useless for almost any real world application.

Let's move the window a bit and then zoom in to see what we're doing here.

Now we can define a class filter. Notice I prefix both filters with a minus character to turn this into an exclusion filter. Here I define a filter to all java packages and all sun packages. That means that every exception that's handled within these packages won't break.

Once I press OK I can press continue and the application runs without breaking on the problematic exceptions. Other exceptions will work as usual and let us know when something breaks in our code!

It's amazing that this isn't the IDE default. Without it the feature is practically useless.

### Tracepoints (AKA Logpoints) {#h3-6-tracepoints-aka-logpoints}

Tracepoints or LogPoints are some of the most important types of breakpoints we have. We can add a tracepoint by shift clicking on the gutter. This opens up a familiar breakpoint dialog but it looks a bit different. First off notice this

The suspend option is unchecked, notice that we can convert any breakpoint to a tracepoint by unchecking the suspend checkbox. By default a breakpoint breaks. It stops the current thread and suspends it so we can leisurely inspect the application stack and see what's going on. A tracepoint doesn't suspend the current thread. The application hits the breakpoint and then keeps on running without stopping. This is pretty groundbreaking, how do you step over?

Well, you don't. Instead we can do several other things...

We can log the words "breakpoint hit" whenever we hit the breakpoint but this isn't that helpful unless we have only one tracepoint and only want to know if we reached that point. We can print a stack trace every time we reach the tracepoint which is more useful indeed. But not by much. It's hard to read a lot of traces in a list and follow through. What I want to focus on is something else.

Notice that evaluate and log already had the `cnt` value in it because I had the `cnt` value selected in the IDE before shift clicking in the gutter.

We can write any expression we want to print here. I can invoke a method write descriptive strings etc. This is literally a standard log statement that I can add dynamically to the code. Notice that I can still make this tracepoint conditional just like any conditional breakpoint. That means I can print any value at this point in time. Just like any logger. This is a spectacular feature. Imagine the method breakpoints we discussed earlier with these tracepoints, we can instantly log at scale.  

Let's press OK

And then run this program, we can see the log we added in the tracepoint printed to the console as if we wrote it in the code!

### Grouping and Naming {#h3-7-grouping-and-naming}

Grouping and naming are crucial once we scale up our debugging.

We can name a breakpoint using the description to indicate its semantic meaning. This is very helpful when we work with many breakpoints. We can also group them based on files, packages etc.  

But the cool thing is that we can create custom groups for breakpoints and disable the breakpoints in the group with one toggle. That's very convenient!  

It saves us from tediously pressing continue when trying to reach a state and lets us manage many breakpoints.

But the true value here is at scale. Say you have a complex debugging session with multiple tracepoints running concurrently. You can group the session and disable the breakpoints while switching to a different branch to debug something else. Then go back to where you were when you're done.

### Disable Breakpoint {#h3-8-disable-breakpoint}

Sometimes a breakpoint is constantly hit and we only need a specific stack

We can disable a breakpoint until a different breakpoint or an exception is hit at which point the breakpoint will become enabled automatically. This saves us the need of pressing continue all the time if we only want to test a specific pathway. This also works with exceptions and exception breakpoints and we can decide the behavior after. Do we want to disable again or continue as usual?

This is very useful for the case of a failure that only goes through a specific pathway. I can add a tracepoint to the first method. Then disable the actual breakpoint I want in that tracepoint.

### Instance Filters {#h3-9-instance-filters}

Instance filters let us only accept a breakpoint from a specific object instance

Notice the instance of the current object marked as "this" in the watch. Notice it's got the at symbol followed by the number 656. This is the Java object ID which is equivalent to a pointer in other programming languages. In instance filters we can limit the breakpoint so it's only hit for a specific object.

Say this line breakpoint gets hit a lot by multiple instances but I only care about results from a specific object instance and want to filter out all the noise. I can open the advanced details dialog by clicking here.

I need to check the instance filter option then type in the object ID of the instance I want to filter. This will mean the breakpoint won't stop for other instance types. To apply this change I press done.

At this point we can see the instance filter is still stopping at the breakpoint which is expected...

So the next step is to change the instance filter to a different object instance. I'm making up a number since this is just for a test. Notice that when I right click the breakpoint I get a customized version of this dialog because we have an instance filter in place. This is a really neat feature of that makes the UI so much easier to work with.

Now that we've made that change the breakpoint no longer breaks.

### Class Filters {#h3-10-class-filters}

Class filters don't make sense for a typical line breakpoint. Class filters make sense when using a field watchpoint or an exception breakpoint.  

In this case I have a public field. I filter the access to the field to ignore all access from the prime main class. If a different class accesses the field the breakpoint will hit. This is very useful otherwise I might get a lot of hits on the watchpoint from the current class. But I want to see other cases.

### Caller Filters {#h3-11-caller-filters}

Caller filter implements a filter based on the signature of the invoking method. To use it we again need to go to the advanced menu. It supports wildcards so I can limit the caller to only allow the run method. Notice that it uses JVM notation for method signature which is a pretty complex subject I'll discuss later. I have a section covering that in my debugging book. The method keeps stopping at the breakpoint as expected because as we can see here, the run method is indeed in the stack trace. So the filter applies properly and hits the breakpoint.

We can again customize the breakpoint in the quick edit UI for the filter. In this case I change the filter to look for a method called stop which doesn't exist and indeed the breakpoint no longer breaks.

### Final Word {#h3-12-final-word}

This has been a long video, I hope you found it educational and comprehensive. In the next video we'll talk about debugging streams and collections.  

If you have any questions please use the comments section. Thank you!
