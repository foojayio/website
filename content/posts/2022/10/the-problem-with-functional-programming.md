---
title: "The Problem with Functional Programming"
slug: "the-problem-with-functional-programming"
date: "2022-10-27T10:17:32+00:00"
lastmod: "2022-12-22T10:10:17+00:00"
description: "Functional programming is amazing, although I think there are a few problems that prevent it from being used more."
authors:
  - "ties-van-de-ven"
image: "https://foojay.io/wp-content/uploads/2022/06/chemical-scientist-test-chemistry-chemist-student.jpg"
categories:
  - "Java Core"
  - "Opinion"
tags:
related_posts:
frozen: false
---

Let me start off with saying that I love functional programming.

Although... a better way of saying it would be that I love what functional programming brings me.

It reduces complexity, the code is nice and explicit, and it eliminates certain bugs from occurring.

But there are a few things that I wanted to discuss regarding functional programming.

Object Orientation vs. Functional Programming {#h2-0-object-orientation-vs-functional-programming}
--------------------------------------------------------------------------------------------------

Let me start off with the Object Oriented vs. Functional Programming discussion.

I do not like this discussion, because it suggests that you have to choose between the two paradigms. In practice, most of the things both paradigms do are in my opinion just solid engineering practices.

For example, a big part of OO is encapsulation. Does this mean you are not allowed to use this when you are doing functional programming? Or using immutability and monads are big in functional programming, does this mean an OO program cannot benefit from these principles?

In the end, I personally try to see the underlying engineering principles and try to learn to use the best approach given a certain context.

Brian Goetz really words this well in his [talk](https://www.youtube.com/watch?v=8GWZE2Y2O9E) when he ends with (spoiler) "Don't be an OO programmer, don't be a FP programmer, be a better programmer."

Available Information / Terminology {#h2-1-available-information-terminology}
-----------------------------------------------------------------------------

Another big problem with functional programming is its terminology, and most importantly, how it is explained.

Let me start by saying that terminology is important. Calling certain concepts by their correct name helps a lot in both communication and in ways of thinking.

By knowing the name, you can actually start reasoning about the concept in a different way and start applying it better.

So we should not get rid of terminology, but we could do better in explaining it. If you google what a "monad" is for example, you will probably end up with finding articles about monad laws. For the average programmer, these do not help in actually explaining what a monad is and can even be quite overwhelming. This is because they are now looking at the academical side of FP, and this is a bit of a scary place if you do not have a mathematical background.

And because of this some people decide that FP might just not be something for them because it looks like a very complicated way of solving simple problems. People losing interest in (or even worse, hating) FP like this is a shame, since you do not really need to know the mathematical side of FP in order to use its concepts.

It is nice that there is mathematical proof, but people do not need that to understand and use, for example, Optionals. Personally I noticed that people respond a lot better to explaining what the underlying (practical) problem is that we are trying to solve and stay away from the mathematical side.

This way people can start using and benefiting from monads today, and they might get curious about mathematics in the future.

Dogmatic {#h2-2-dogmatic}
-------------------------

In general, I notice that people can get quite dogmatic about certain paradigms.

I noticed this with myself, as well, when I started learning FP and wanted to solve everything using these principles. I even went as far as saying this should be the only way to write software (and in this way participating in the OO vs FP discussion).

These days I have become a lot milder in this and try to see the benefits of both paradigms and even different programming languages.

I, for example, do not like Python because of the weak/dynamic typing. It feels a bit dirty to not know if my program will work without running it and finding out (and yes, I know this is slowly getting better).  

This, however, does not mean I think Python is a bad language, it is just not the language I would choose for most projects I am working on.

If I just want to create a simple program that, for example, just reads a directory and outputs something, then Python is amazing. FP languages would make this a lot harder since they force you to think about things like error handling and such, which is a really good thing in general... but not if you just want something quite small to simply work.

And this is not only true for languages, but also for engineering concepts.

Having a lot of compile time safety (and reduction in complexity) due to things like monads and immutability is really nice, but it might be a bit much (and even increase complexity), if the problem you are trying to solve was trivial to begin with.

For this purpose I personally try to place things on a scale of complexity.

By this I mean that if you are building trivial stuff, you shouldn't worry that much about complexity reducing principles. If you, however, are building complicated domain models, then the benefits start paying off. And the more complex your application is, the more of these tools you will need to use to manage the complexity.

Unfortunately, I do not have an actual scale which says from which point you start needing certain concepts and such a thing would be near impossible to create for all contexts out there.

But, I do think that this way of thinking helped me in becoming less dogmatic and therefore come across as more understanding and trying to find the best solution for each problem.

So, I hope this idea might also help you!

Conclusion {#h2-3-conclusion}
-----------------------------

I love Functional Programming, but I do feel we can and should make the concepts more accessible to the average programmer.

We can do this by stopping the discussion about OO vs FP, instead focusing on the underlying engineering principles, do a better job at explaining the underlying problems we are trying to solve, and also accept that it is not always the right tool for the job.

So I would like to conclude with repeating Brian Goetz: "Don't be an OO programmer, don't be a FP programmer, be a better programmer".

*Originally published at [JDriven](https://blog.jdriven.com/2022/10/the-problem-with-functional-programming/) on October 17, 2022*
