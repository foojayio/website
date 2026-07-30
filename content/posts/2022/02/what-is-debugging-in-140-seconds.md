---
title: "What is Debugging in 140 Seconds | Foojay Today"
slug: "what-is-debugging-in-140-seconds"
date: "2022-02-14T12:50:58+00:00"
lastmod: "2022-02-14T12:50:59+00:00"
description: "I’m launching a new Twitter video series that will focus on teaching the concepts of debugging (and other concepts) in small video bites"
canonical: "https://talktotheduck.dev/introducing-140-second-ducklings-what-is-debugging"
authors:
  - "shai-almog"
image: "https://foojay.io/wp-content/uploads/2022/02/Lightrun-Talk-to-the-duck-Debugging-Tutorial-Opt3.jpg"
categories:
  - "Developer Tools"
  - "Tutorials"
tags:
related_posts:
frozen: false
---

**I'm launching a new twitter video series that will focus on teaching the concepts of debugging (and other concepts) in small video bites.**

This is the first video in the series. I hope to publish a new video every week. It starts with the very basics of debugging and builds up. I think debugging is a powerful tool that even experienced developers don't leverage enough.

See the video here:
> 🦆 Duckling the 1st:  
>
> Debugging Basics 1 - Breakpoints, Variable Inspection \& Stack in [#Java](https://twitter.com/hashtag/Java?src=hash&ref_src=twsrc%5Etfw) using [@intellijidea](https://twitter.com/intellijidea?ref_src=twsrc%5Etfw).[#CodeNewbie](https://twitter.com/hashtag/CodeNewbie?src=hash&ref_src=twsrc%5Etfw) [#140SecondDucklings](https://twitter.com/hashtag/140SecondDucklings?src=hash&ref_src=twsrc%5Etfw) [pic.twitter.com/QgV8kXvu7V](https://t.co/QgV8kXvu7V)
>
> --- Shai Almog (@debugagent) [February 8, 2022](https://twitter.com/debugagent/status/1491076030295117830?ref_src=twsrc%5Etfw)

<br />

The source code used in the video is available here: <https://github.com/shai-almog/PrimeMain>

Introduction {#h2-0-introduction}
---------------------------------

I tried to do a [debugging tutorial](https://talktotheduck.dev/debugging-tutorial-1-introduction-conditional-breakpoints) a while back. I'm not a fan of the result. I think I got ahead of myself a bit for novices and skipped ahead too soon. This time I'd like to keep it short. Even if you know the subject well enough, you won't waste much time. These videos will rise in complexity to advanced debugging as in the following weeks.

My goal is to create a series that's accessible to novice developers and also has nuggets that more experienced developers might have forgotten or didn't know.

At the moment I am using IntelliJ/IDEA and Java. However, everything I show should work for VSCode, JavaScript and other programming languages. I plan to create similar videos for additional languages/IDEs to match.

In the first video, I focused on simple Inline breakpoints, stack trace and variable values. The second video talks about control flow, forced return, etc. I will return to the venerable breakpoint in future videos since there's so much to discuss!

I plan to cover increasingly complex concepts as we work our way through the basics. E.g. I will obviously discuss:

* Remote debugging
* Post-mortem debugging
* Print statements
* Conditional breakpoints  
  and so much more...

Debugging Process {#h2-1-debugging-process}
-------------------------------------------

> "Debugging is the scientific method applied to computer programming. We can observe a problem or a behavior directly within the running application."

When we pick a debugging tool, it isn't that different from typical experimental science. We define what we expect to observe in the code base and we see if our expectations match reality. This analysis leads us to the solution.

The first challenge we deal with when pulling out a debugger is reproducing the issue. How do we repeat the experiment?

I will go into more details in the next episode of 140 second ducklings...

Debugging vs. Unit Tests {#h2-2-debugging-vs-unit-tests}
--------------------------------------------------------

> "It isn't a replacement for testing. You often need a debugger to debug the tests or understand the missing tests. In fact, debugging and testing go hand in hand."

Testing and debugging go hand in hand. Debuggers are excellent tools to debug test failures. But the really significant part is that unit tests help us prevent regressions of bugs we understood in the debugger.

Extracting a unit test following a debug session is a great subject for a future video focused on testing.

Understand The Program Source Code {#h2-3-understand-the-program-source-code}
-----------------------------------------------------------------------------

> "Debugging provides insight into our code that's normally much harder to get otherwise. You can use it to learn new code"

This process is more than just defect analysis...

When I need to pick up a new code base, I often start with debugging. I look through the central areas in the software code with a debugger to verify my assumptions and understandings.

It's like looking inside an engine while it's working and pausing it to understand details. Common debugging technique can reveal the hidden nuances of a software product.

Debuggers Checklist {#h2-4-debuggers-checklist}
-----------------------------------------------

> "We start by making the simplest assumption we can about the project
>
> Then we need to validate that assumption using the debugger. If we get a different result than what we expected, then we have something to investigate. This is the debuggers checklist which we will discuss in the future"

I wrote about [the debuggers checklist](https://talktotheduck.dev/the-debugger-checklist-part-i) in great detail a while back. I'll try to go over all of its approach as part of this series. The checklist is a generic theoretical framework or algorithm by which an application can be debugged.

This might be unnecessary for smaller samples like the one in the video. But as lines of code grow and complexity rises, we need a methodology for locating bugs.

Next Week and Down the Line {#h2-5-next-week-and-down-the-line}
---------------------------------------------------------------

I plan to post a video every week. I hope I can keep up. I have the next one ready but production is non-trivial. Hopefully, this will get easier with practice.

I hope to introduce additional topics to the "140 second ducklings" series. There's so much I want to cover. An interesting subject would be DevOps for developers or tracking production issues.
