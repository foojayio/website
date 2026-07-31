---
title: "What do you think was the reason that Java was invented?"
slug: "the-reason-java-was-invented"
date: "2020-10-05T06:14:16+00:00"
lastmod: "2020-10-05T08:07:37+00:00"
description: "The Java virtual machine is a system whereby any code could be run on any machine or operating system that supports a JVM."
authors:
  - "kevinfarnham"
image: "Favicon-3-2.png"
categories:
  - "Uncategorized"
tags:
related_posts:
frozen: false
---

In my [last post](https://foojay.io/blog/compiling-java-code-executing-bytecode/) I described the timing differences between executing `javac` to create Java bytecode and running the actual bytecode. Some readers noticed that, obviously, the `javac` operation will automatically take more time than the execution of the byte code will take, and wondered why I took the time to write the post. Why would we even have `javac` if it didn't improve runtime performance? In fact, why would Java have even been invented if we couldn't use `javac` to create fast-running bytecode?

Maybe I didn't properly frame the point of that post. The point is that Java was invented to solve the problem of different operating systems and hardware. You could "write once, run anywhere" which cannot be done with my other favorite programming languages, C, Python, and even FORTRAN (that was a great early scientific programming language!). The problem with those languages -- I mean of course we know FORTRAN has no clue about how to run on any OS or hardware, and C isn't much better -- is that they were designed for the past. I mean, at the time when they were designed, they fit the needs of the then present moment, but the languages didn't anticipate a future where hardware and operating systems were going to radically change.

With the invention of Java, James Gosling and friends created a system whereby any code could be run on any machine or operating system that supports a JVM. The point is: all the coding that lets Java (or any other language that can produce Java bytecode) run anywhere is the fact that the JVM itself translates the bytecode into what's needed for whatever operating system or hardware you're running on. Languages like C and Python do not have this capability, they simply have their basic compilers, and if it works on your platform it works, if not, oh well! FORTRAN, oh, I could tell you many stories about trying to get that to run on modern platforms, even when wrapped in C!

I was never surprised that this happened with FORTRAN or C. With Python, I expected a better result, like I thought if it ran perfectly on Linux it would surely run perfectly on Windows and Mac. I was wrong.

With Java, nothing I've ever written did not run correctly on any hardware or operating system I tried to run it on.

In my next post I'll get back to the amazing performance difference between adding 1 to 0 a Million times and multiplying 1 by 1 Million. It's a very surprising result.
