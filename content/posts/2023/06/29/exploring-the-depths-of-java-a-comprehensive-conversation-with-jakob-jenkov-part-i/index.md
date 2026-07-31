---
title: "Exploring the Depths of Java: A Comprehensive Conversation with Jakob Jenkov, Part-I"
slug: "exploring-the-depths-of-java-a-comprehensive-conversation-with-jakob-jenkov-part-i"
date: "2023-06-29T13:33:58+00:00"
lastmod: "2023-07-05T19:59:18+00:00"
description: "Explore the first part of our in-depth interview with Jakob Jenkov, an experienced Java developer and AWS solution architect, as he shares his insights on the past, present, and future of Java."
authors:
  - "bazlur-rahman"
image: "jekov.jpeg"
categories:
  - "Interviews"
tags:
related_posts:
  - "3-ways-to-refactor-your-code-in-intellij-idea"
  - "5-things-you-probably-didnt-know-about-java-concurrency"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "interview-with-a-java-champion-reflections-on-a-storied-career-and-insights-for-the-next-generation"
frozen: false
---

<figure class="alignright size-full is-resized">
 <img fetchpriority="high" decoding="async" src="jekov.jpeg" alt="" class="wp-image-98946" width="400" height="400">
</figure>

Welcome to the first part of our deep dive with Jakob Jenkov, a seasoned software developer and AWS solution architect, whose love for coding began with a Commodore 128 in the late '80s. Over the past decades, Jenkov has navigated the world of programming, with a particular emphasis on Java, making significant contributions to the community through his professional work and informative content on jenkov.com.

In this exclusive interview, Jenkov shares his career journey, his thoughts on the evolving landscape of Java, and how his interest in both low-level and high-level programming intersects in his current role. His unique insights into the future of Java and its significance in today's increasingly diverse computing environments provide a captivating read for both Java enthusiasts and the broader software development community.

Whether you're a professional developer, a student of programming, or simply interested in the world of software development, you'll find valuable insights in this conversation with Jakob Jenkov. Stay tuned for Part Two, where we'll delve deeper into Jenkov's personal philosophy and his approach towards the challenges and opportunities in the rapidly changing field of software development.

<br />

Profile: {#h2-0-profile}
------------------------

🐦 Twitter: <https://twitter.com/jjenkov>

🌐 Home page: <https://jenkov.com/>

💼 LinkedIn: <https://www.linkedin.com/in/jakob-jenkov-4a3a8/>

📂 Github: <https://github.com/jjenkov>

<br />

> **Fun Fact:**   
> I like dogs, cats and single-board computers 🙂
>
> I used to play tabletop role-playing games, and the Magic The Gathering card came.
>
> In 2022 I felt like I needed to do something in my spare time that was the opposite of sitting in front of the computer - so I started taking dance classes. Quite challenging for me and quite fun 🙂

<br />

**Bazlur: can you walk us through your career path and what led you to focus on Java? What are the key philosophies or principles that guide your approach to software development?**

**Jakob**:

I started programming around 1987 or so - on a Commodore 128 - in Basic. Later I switched to a Commodore Amiga 500 - and did some Amiga basic programming, AMOS, and then Amiga Assembler. During my university years, I switched to PC and C + x86 Assembler. I was always attracted to low-level programming.

In university, I learned Java, among other languages, and I was intrigued by the promise of being able to run the same code on many different computers (Write Once - Run Anywhere). Having played with Assembler, I knew that Assembler does not look the same on different computers, so you would have to either recompile from C or reimplement in Assembler, for each computer you wanted to support.

I started working with web development in 1998 and with Java in 1999, and I have been working with Java pretty much since then. I started doing mostly internal enterprise applications and systems of various kinds.

Since around 2016, I started working more with architecture and performance optimizations and similar more specialized tasks. I am now working as an AWS solution architect at KeyCore - a Danish consulting company focused on AWS architecture and solutions.  

*** ** * ** ***

**Bazlur: Could you share your perspective on the current state of Java and its future?**

Jakob:

<br />

**Cross-platform support:** In today's world, our computing environments are increasingly heterogeneous with Intel and Arm-based computers on the desktop, mobile and servers - and with RISC V entering the market too. Many of these computers have additional special purpose compute units onboard, such as GPUs or FPGAs. The idea of strong cross-platform support is as relevant as ever.  
> "*The idea of strong cross-platform support is as relevant as ever... The Java ecosystem as a whole has quite good cross-platform support these days - and I expect that only to get better going forward.*"

While the Java VM does not itself have GPU compute support, TornadoVM can parallelize a subset of Java instructions onto multiple CPUs, GPUs or FPGAs. TornadoVM is a free, open-source project from the University of Manchester. TornadoVM is a plugin for other Java VMs such as Open JDK, GraalVM or Azul. Thus, the Java ecosystem as a whole has quite good cross-platform support these days - and I expect that only to get better going forward.

GraalVM is also a very interesting project in both the cross-platform - but also language interoperability areas.

<br />

**Performance:**

The performance of the Java VMs has also increased continuously, so the performance you get out-of-the-box with the latest Java VM is a good bit higher than what you got with Java 1.8.

<br />

Additionally, the Java community has learned a lot about performance, so it is possible to create systems that process millions of records or messages per second. You need to know the right techniques - but that would be the case in other programming languages too.

Finally, with the increased language interoperability to be expected in future versions of Java, such as foreign memory access, faster calls between Java and native code etc. - Java is well suited even for many high-performance tasks.

Don't forget what TornadoVM can do for you in the area of performance, too - while staying 100% within the Java language! Or - GraalVMs ability to compile your Java application to a native executable.

In the world of cloud - AWS has released Snap Start for AWS Lambda in 2022 - which brings down the cold start time of AWS Lambdas implemented in Java - so Java is now a 100% first-class language for serverless cloud computing now.  
> *"In the world of cloud - AWS has released Snap Start for AWS Lambdas in 2022 - which brings down the cold start time of AWS Lambdas implemented in Java - so Java is now a 100% first-class language for serverless cloud computing now."*

**Platforms, toolkits and architecture:**

I expect a good bit of the new developments within the Java ecosystem to be focused on edge computing and other types of decentralized systems, such as blockchain-based platforms and P2P networks in general.

Edge computing is meant computing that happens closer to the source of the processed data - such as closer to IoT devices or closer to desktop clients/browsers. Edge computing is necessary to be able to process the increasing amounts of data being generated in our systems going forward. Sending all that data to the cloud for processing might not be fast enough - or not even possible.  

Gartner Group expects edge computing to increase significantly in the next few years (towards 2024-2025) - with as much as 75% of all data eventually being processed at the edge.  

> Gartner Group expects edge computing to increase significantly in the next few years (towards 2024-2025) - with as much as 75% of all data eventually being processed at the edge.  

Additionally, crypto and blockchain projects continue to expand the territory within more decentralized types of architectures. So far, the Bisq project (Java + JavaFX) and the company Jelurida operate in this space - but I expect to see more development in this area within the Java ecosystem.

I do think, however, that we might have to look into developing new, more polymorphic general-purpose platforms and toolkits that are better geared towards implementing either P2P, edge or classic client-server computing - than what we currently have today. This is an area I am doing some experiments within - in a project that I call Polymorph. The project is not that far yet, but many of the central ideas are beginning to stabilize, so I have started making proof-of-concept implementations of the core ideas.

**Data Science and AI:**

Java is so far not that well represented within the data science and AI areas. This is an area where I hope to see more development. I might do a little myself as part of my Polymorph project, but nowhere nearly enough to cover all the needs within this area.

<br />

**Conclusion**

All in all - Java is still quite a good overall platform and ecosystem, in my opinion. You can even choose between Java, Kotlin, Scala, Groovy and Clojure if you do not like the direction of the Java language itself. All of these languages run on top of the Java VM. If you use GraalVM, you can even run JavaScript, Python, Ruby and R too, and also LLVM and Web Assembly.   
> *"All in all - Java is still quite a good overall platform and ecosystem, in my opinion. You can even choose between Java, Kotlin, Scala, Groovy and Clojure if you do not like the direction of the Java language itself."*

I don't feel Java is "perfect" in any of the areas it addresses. Within each of the areas, there are many smaller, more focused tools that might do the job better. However, I feel Java is an 8-9 out of 10 in enough areas that, all in all, you get a quite good overall general-purpose platform and ecosystem - not matched by many other ecosystems.

I am not "religious" about Java, though. The .NET ecosystem is doing quite well too. I am also looking a bit into more native-style programming languages such as C, D and Rust - just to get a feel for what is happening within these ecosystems. The LLVM and Web Assembly technologies make many of this language easier to compile to different platforms, too - so cross-platform support is expanding in general. I am also using JavaScript, Python and TypeScript from time to time in my job as an AWS solution architect.  

*** ** * ** ***

Conclusion: {#h2-1-conclusion}
------------------------------

That concludes Part One of our in-depth conversation with Java expert and AWS solution architect, Jakob Jenkov. From the intriguing tale of his career path to his forward-thinking views on Java's evolution, we've uncovered a wealth of insights. As we look forward to Part Two next week, expect even deeper dives into software development trends, the challenges of our time, and the future of data science and AI. Don't miss it!  

<br />

**Read the part - II : <https://foojay.io/today/exploring-the-depths-of-java-a-comprehensive-conversation-with-jakob-jenkov-part-ii/>**
