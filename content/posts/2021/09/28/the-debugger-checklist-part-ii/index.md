---
title: "Debug Real World Applications using a Systemic Proven Process"
date: "2021-09-28T08:54:00+00:00"
lastmod: "2021-09-28T08:55:29+00:00"
description: "Debugging isn't a part of curriculum, it's a crucial skill we're expected to \"just pick up\". Here we fill in the blanks on this black art!"
authors:
  - "shai-almog"
image: "Blog-Header-1200x600-px1.jpeg"
categories:
  - "IntelliJ IDEA"
  - "Tutorials"
related_posts:
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
  - "the-debugger-checklist-part-ii"
  - "secure-code-review-best-practices-part-1"
frozen: false
---

In the [Debugger Checklist (Part I)](https://foojay.io/today/the-debugger-checklist-part-i/), I introduced some of the high level concepts and reviewed some of the common things you can do. In this part, we'll get down to the process. Again, it's important to stress that this is boiled down and concentrated!

I don't want to discuss issue trackers, unit testing and proper TDD. I think there are many resources covering all of these. The focus of these posts is the debug process itself which often gets neglected as "tools". There's a lot of technique that separates the seasoned developer from a junior. Furthermore, there are many small advancements and changes we can't possibly keep up with. I'd like to cover all of those things (some in future posts).

With that out of the way let's continue with...

## The Process

Hopefully by this point you were able to reproduce your problem in the debugger. Possibly even limited it to a small area you can step through.

Now we need to actually track the issue and fix it. People think this is hard to quantify generically but it isn't. It comes down to breaking the problem down into manageable pieces we can attack:

* Make the simplest assumption you can
* Validate assumptions
* While the bug isn't found:
  * Narrow assumptions
  * Validate narrowed assumptions

You're probably saying: "Well dah...". This is pretty obvious...

It totally is. The problem is we don't apply those properly and somehow skip a lot of the nuance in those steps.

During this stage Rubber Ducking (talking to the duck) becomes useful. This is the process of talking to someone (or something) about your problem and assumptions. When we say things out loud or even try to verbalise them in our mind, it helps clarify our misguided assumptions.  

I'll try to get into more of these sorts of tricks in a future "tips and tricks" post.

### The Simplest Assumptions

![If something doesn't make sense. One of your assumptions has to wrong, because if something doesn't make sense then it can't be real. But what if the faulty assumption is that it's real. - House](https://cdn.hashnode.com/res/hashnode/image/upload/v1632369682209/IrVcETgk5.jpeg)  

(image source: <https://weheartit.com/entry/59844817>)

This is where most of us fail. We assume.

I recently had a bug which I encountered by accident. I noticed the value of a variable in a stack trace. It was clearly corrupted. Since I was using a debug agent I incorrectly assumed that was the source of the problem. But I tried to verify again and again. When all else failed this led me down the road to a serious bug in the code.

Obviously we can't start by testing the assumption that `1 + 1 = 2`. So we need to narrow it down to applicable assumptions. This "trick" isn't the "end all" but it's a very useful way to validate a lot of common assumptions.

#### Debug "Working Code"

The best way to review your assumptions is to walk through working code. If your code fails for case X and succeeds for case Y, try case X first. See why the code works and step over the code block.  

Then try case Y. This should present you with two cases you can easily compare to help you narrow in on the suspect.

If this isn't applicable or isn't taking you anywhere you need to review the following:

* Exceptions
* State
* Threads/Synchronization
* Timings and Races

I ordered these according to difficulty and probability.

#### Exceptions

Most of the problems are state, but exceptions are relatively easy to detect. So just place a breakpoint for all exceptions and verify that nothing "fishy" is happening behind the scenes in one of the methods you invoked etc. Slowly filter out the "valid" exceptions as you move through the process.  

Typically exceptions are "loud" and "obvious" so unless someone silently caught an exception (which happens), you should be in the clear.

I would also strongly recommend a linter rule that checks against swallowed/unlogged exceptions. E.g. checkstyle supports [this](https://checkstyle.sourceforge.io/config_blocks.html) check that blocks empty catch blocks. It still can't block stupid code that does "nothing" in that block but at least it's a start.

#### State

While threads are a source of difficult bugs, most bugs are a result of bad application state. Try separating the state elements that are modified and the state that's read by the block of code.

Assuming you can, try overriding it within your breakpoint by setting a value of a variable during debugging. This is a great capability that most developers don't utilize often enough. If you're able to narrow down the value of a specific variable as the cause of the problem you're already well on your way to solving the problem.

If this isn't helping, try identifying specific fields that might be problematic. Most debuggers will let you place a breakpoint on the field in order to watch modifications to said field... I used that feature a couple of times while consulting and people were always surprised you can do that...

![Field Breakpoint](https://cdn.hashnode.com/res/hashnode/image/upload/v1632370150204/87OTJS1zH.png)

In IntelliJ the icon looks different for a field breakpoint. But it's a breakpoint like any other, you can apply a condition to it and see the stack etc.

Now if the problem persists and everything is failing... Try changing the code to return a hardcoded state or a state from a working case. I'm normally not a fan of techniques that require code change for debugging since I consider the two distinct tasks. However, if you're out of all options this might be your only recourse. Naturally you should use "Reload Changed Classes" (or Apply Code Changes, Edit and Continue etc.) if applicable.

Notice that there are also lower level memory breakpoints that are useful to debug memory access. We'll discuss these when we cover debugging native code which I plan to cover in the future.

#### Threads/Synchronization

Thread problems are hard to solve... That's not really something we'll get into. We'll only focus on finding and understanding the bug, and that's an easier (manageable) task.

The easiest way to check threading issues is as I mentioned before logging your current thread and/or stack. Do that in the block of code that's causing an issue. Then add a similar log breakpoint on fields used by the block of code. Thread violations should be pretty clear in the logs.

You can also get a thread dump during a breakpoint, that's a feature of pretty much any debugger out there. E.g. in IntelliJ/IDEA you can select `Run -> Debugging Actions -> Get Thread Dump`. This isn't as useful as going through the stack frames but it's a start.

Specifically in IntelliJ/IDEA I recommend right clicking the debug tab and enabling the thread view. Then enabling thread groups by right clicking within the tab and selecting `Customize Thread View` like this:

![Customize Thread View in IntelliJ/IDEA](https://cdn.hashnode.com/res/hashnode/image/upload/v1632370285245/CQNO-Z1Ef.png)

It provides a much "cleaner" view of the threads as a hierarchy instead of the default look in IntelliJ which is better geared towards single thread debugging.

**Deadlocks and Livelocks**

Deadlocks are usually pretty clear. The app gets stuck, you press pause and the debugger shows you which thread is stuck waiting for which monitor. You can then review the other threads and see who's holding the monitor. Fixing this might be tricky, but the debugger literally "tells us" what's going on.

With a livelock we hold one monitor and need another. Another thread is holding the other monitor and needs the one we're holding. So on the surface it seems that both are working and aren't stuck. A bit like two people running against each other in the hallway and trying to step out of each other's way. Unfortunately, livelocks can happen without threads being physically "stuck" so the code might appear fine on the surface without a clear monitor in the stack traces.

Debugging this requires stepping over the threads one at a time in the thread view and reviewing each one to see if it's waiting for a potentially contested resource. It isn't hard technically but it's very tedious. That's why I recommended enabling thread groups in the thread view above. A typical application has MANY threads (and more coming with project [Loom](https://inside.java/2021/08/13/new-loom-ea-builds/) ). This produces a lot of noise which we can reduce by grouping the threads and focusing on the important parts.

**Performance and Resource Starvation**

Performance problems caused by monitor contention are a bit harder to track with a debugger. I normally recommend randomly pausing the code and reviewing the running threads in your application. Is thread X constantly holding the monitor?  

Maybe there's a problem there.

You can then derive assumptions and prove them by logging the entry/exit point for a lock/synchronized block.

Notice you can use a profiler and it sometimes helps, but it might lead you on the wrong path in some cases. I plan to discuss profilers in a future post.

Resource starvation is an offshoot of performance issues. Often you would see it as an extreme performance issue, that usually only happens when you try to scale. In this case a resource needed by a thread is always busy and we just can't get to it. E.g. We have too many threads and too few database connections. Or too few threads and too many incoming web requests. This problem often doesn't need a debugger at all. Your environment usually indicates immediately that it ran out of the given resource so the problem is almost always plain and obvious.

The solution isn't always as clear, e.g. you don't want to add too many threads or DB connections to workaround the short term problem. You need to understand why the starvation occurred...

This happens because of two different reasons:

* Not releasing resources fast enough
* Not releasing resources in all cases

The first case is trivial. You can benchmark and see if something is holding you back.  

The second is the more common: a resource leak. In a GC environment this is often masked by the GC that nicely cleans up after us. But in a high throughput environment the GC might be too slow for our needs. E.g. A common mistake developers make is opening a file stream which they never close. The GC will do that for us, but it will take longer to do that and the file lock might remain in place, blocking further progress.

This is where encapsulation comes in handy. All your resource usage (allocation/release) must be encapsulated. If you do that properly adding logging for allocation and freeing should expose such problems very quickly.

This is a bit harder to detect with DI frameworks like Spring where connections etc. are injected for you. You can still use tricks like this, to [track even injected data](https://stackoverflow.com/questions/50770462/springboot-2-monitor-db-connection).

**Timings and Races**

This is one of those elusive bugs such as race conditions which are often classified as thread problems (which they are) but during a debugging session it's often easier to see them as a separate task.

These often occur when your code has some unintentional reliance on performance or timing. E.g. Many years ago I had an app that crashed only when our customer was using it. The app was a mobile app and our customer was a local operator. It turned out that we had a bug where the networking in the customer site was SO FAST it just returned the response immediately and everything else wasn't ready. So the application crashed. Usually the problem is slow performance and timeouts.

So this was a case where my assumption that the network was slower than the CPU was flawed...

The way I approach race conditions in threading code is this: "It's a state bug".  

It's always a state bug. When we have a race condition it means we either read from the state when it wasn't ready or wrote to the state too late/early. Field breakpoints are your friends in this case and can really help you get the full picture of what's going on. You can also simulate the bad state situation by changing variable values.

## Finally

If you follow through your assumptions and catalog the bugs into one of those common pitfalls then you're 90% of the way to understanding the root cause. The rest is deciding on the right fix for the problem.

I won't go into fixing the bug, filing the issue, building a test case etc. You should do all of that but there's plenty written about that.

In fact in preparation for this blog I picked up a lot of debugging books on Amazon. Turns out most aren't **"really"** debugging books. Yes they cover it in one or two chapters. The rest of the book is always about the process, test cases and everything surrounding it. I think that would be fair if debugging wasn't a huge subject that can fill up a book. In my opinion, it sure can and I'm just getting started.

Tune in for more!
