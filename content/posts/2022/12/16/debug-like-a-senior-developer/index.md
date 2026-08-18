---
title: "Debug Like a Senior Developer"
date: "2022-12-16T06:34:02+00:00"
lastmod: "2022-12-16T06:34:03+00:00"
description: "New online course is launching with the first video ready to view. More are coming in the coming months, also the book is ready for preorder!"
canonical: "https://debugagent.com/debug-like-a-senior-developer"
authors:
  - "shai-almog"
image: "thumbnail.jpg"
categories:
  - "IntelliJ IDEA"
  - "Tools"
  - "Tutorials"
  - "Videos"
related_posts:
  - "why-i-dont-do-tdd"
  - "internal-security-hardening-internal-systems"
  - "api-mocking-essential-and-redundant"
frozen: false
---

My book on debugging is [already on preorder](https://www.amazon.com/dp/1484290410/) and I'm super thrilled to announce I'm doing a full online course to go along with it.

The course website isn't ready yet but I already have the full outline and a lot of recorded material. I will try to drop videos at a rate of two per week in the next few months until the full course will be online.

It's shaping up to be a very detailed course. I recorded the first module and half of the second module and I'm already close to 3 hours of dense recorded material!

Check out the first video in the series below and the full transcript. The first module comprises 9 videos all of which will be 100% free for all and cover IDE debugging in depth. They roughly correspond to the first chapter of my book. The full course covers details I get into in the other 13 chapters although it can't cover the entire breadth of the book, it presents elements that are hard to show in book form.

I'm publishing the course on a new [YouTube channel](https://www.youtube.com/@debugagent/), I would very much appreciate likes and subscribes. If you like the video please subscribe there or to one of the mailing lists e.g. [here](https://debugagent.com), to get notified about new videos and about access to the full course as it becomes available.

I don't intend to publish the course on any other channel other than here. So if you see the course in any place other than here please let me know and don't buy it there.

{{< youtube A919j_5qE0k >}}

## Transcript

Hello everyone and welcome to practical debugging at scale. In this course I will teach you how to debug, but more importantly. I hope to change the way you look at debugging and at programming in general.

In the mid 90s I was an over-confident developer. I knew "everything" and senior developers constantly sought my consultation, despite my young age at the time. Then I got lucky. I was debugging an issue with a senior developer and the way he wielded the debugger knocked me off my seat.

The experience was both amazing and embarrassing. My ego was bruised, but I persisted and learned everything I could about debugging techniques. I was lucky enough to see a master at work, to discover that there's a better way. I hope this course will pass that luck to you.

That's why I chose to start the course with IDE debugging. It's something we use all the time, but as I go through these videos I guarantee I will cover features that you probably never heard about

One such feature is object marking which lets us define a global static variable as we debug. It's an amazing feature which we discuss in the third video in this course.

Another feature is "Jump to Line" which lets us move the execution point dynamically to an arbitrary location. We will discuss it in the second video in this course. Or watch renderers which lets us customize how everything looks in the watch area to an amazing degree. We will discuss that in the 6th video of the course.

They don't teach this stuff at school.

But before we continue... Why am I the guy to teach this?

I wrote a couple of books including one about debugging which covers a lot of the topics we'll discuss here. I worked in this industry for decades in many companies and worked as a consultant. That means I had to go into companies, figure out what's wrong and solve the problem, then overcharge a ridiculous fee. The debugger was my secret weapon!

I also worked at Sun Microsystems and Oracle, I built JVMs, flight simulators, embedded system, cloud solutions and developer tools.

You can contact me over my socials listed here and follow me on Mastodon, LinkedIn etc. You can also use the comments section here. I'll try to help.

This is my Apress book titled practical debugging at scale same as this course. As I'm recording this it isn't out yet but you can preorder it and it's a perfect complement to this course. It might be out already by the time you see this as it launches in January 20th 2023. Either way you can probably find it online.

Let's review the curriculum for the course and discuss the subjects we will cover. The first part focuses on the basics. Using the debugger within the IDE. This seems like a trivial exercise but I guarantee that I will cover debugger features you probably never heard about or thought of.

I will focus on JetBrains IDEs, mostly IntelliJ since it has one of the best debuggers around. Next we discuss the theory of debugging. The scientific method that underlies the debugging process.

Tooling talks about some of the cool tools we can use when tracking a problem. These are system level tools we can leverage to peer into a system and figure out what's going on in there. They're mostly on Linux systems since that's where we carry out a lot of the deployment. Especially in the world of containers.

Next we'll delve into ways in which we can better write our code to make debugging easier when it fails. All code fails, we need to make sure it fails in a way that makes it easy for us to track the issue.

Debugging Kubernetes is the next step. How do you debug an issue when you have so many abstraction layers in between. The scale of Kubernetes makes debugging so much harder.

We'll follow that with debugging serverless which is a pretty painful experience. Lambda adds the complexity of ephemeral state and an environment that's almost impossible to replicate locally.

Debugging the fullstack talks a lot about debugging the frontend and a bit about the process of debugging the database backend. The goal is to create a tongs motion while searching for the bug in a real world application.

Observability isn't debugging per-se but it's an important tool when tracking production issues in all of these technologies.

Before we go on to the main course lets dig a bit deeper into what's debugging. This should be obvious to most of us and it is. But there's also a couple of nuances.

Debugging is the scientific method applied to computer programming. We can observe a problem or a behavior directly within the running application. This is important. In physics we have theoretical and experimental physics. This analogy isn't exactly the same because in programming we also have production and those complexities. Debugging is about understanding the practical implementation, observing it in various experimental ways so we can see the actual problem and eliminate the things that aren't a problem.

Debugging gives us deep insight into our project that we can't get in any other way.

Debugging isn't a replacement to testing, you often need a debugger to debug the tests or understand the missing tests. In fact, debugging and testing go hand in hand. We'll talk about testing and its special relation to debugging as part of this course.

Debugging provides insight into our code that's normally much harder to get otherwise. You can use it to learn new code. I use debuggers to study a new code base that's alien to me. There's no code analysis or tutorial that's more powerful than a simple debugger.

But most of all. It lets us verify assumptions we might have about the running application. This helps us track bugs.

Many of my demos are based on the PrimeMain application which you can find on my github page. Feel free to follow me while you're there and check out my other projects.

Let's open the IDE and debug a simple application.

To get started with a debugger we open the PrimeMain project and run it. We need to do some IDE specific configurations to get this running but pretty much any IDE and language has basic common concepts here. Once the app is running we can place a breakpoint on the left hand side and the IDE will stop at the breakpoint for debugging.

To get started with a debugger we open the PrimeMain project and run it. We need to do some IDE specific configurations to get this running but pretty much any IDE and language has basic common concepts here. Once the app is running we can place a breakpoint on the left hand side and the IDE will stop at the breakpoint for debugging.

What we see here is the stack frames, in this case it's pretty simple because we only have one frame. But when we start debugging real applications we'll see that the stack can go pretty deep. We can click frames in the stack and select a specific method and see the values of the variables up in the stack.

We see all of that here in the variable view. We can add watch, inspect elements and even change their values, but I'm getting ahead of myself. This lets us see the value of every variable in this stack frame and so much more. We'll get deeper into this in the next video

In the next video we will discuss program control flow. Follow through the videos to check out the full course. If you have any questions please use the comments section. Thank you!
