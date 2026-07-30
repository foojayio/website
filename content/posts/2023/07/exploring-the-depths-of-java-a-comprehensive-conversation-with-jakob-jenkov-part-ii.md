---
title: "Exploring the Depths of Java: A Comprehensive Conversation with Jakob Jenkov, Part-II"
slug: "exploring-the-depths-of-java-a-comprehensive-conversation-with-jakob-jenkov-part-ii"
date: "2023-07-05T16:21:30+00:00"
lastmod: "2023-07-05T16:21:31+00:00"
description: "Explore the first part of our in-depth interview with Jakob Jenkov, an experienced Java developer and AWS solution architect, as he shares his insights on the past, present, and future of Java."
authors:
  - "bazlur-rahman"
image: "https://foojay.io/wp-content/uploads/2023/06/jekov.jpeg"
categories:
  - "Interviews"
tags:
related_posts:
  - "virtual-tour-is-the-biggest-project-yet-for-the-new-java-community-platform-foojay-dzone"
  - "3-ways-to-refactor-your-code-in-intellij-idea"
  - "5-things-you-probably-didnt-know-about-java-concurrency"
  - "jc-ai-newsletter-16"
frozen: false
---

<figure class="alignright size-full is-resized">
 <img fetchpriority="high" decoding="async" src="/images/posts/2023/07/exploring-the-depths-of-java-a-comprehensive-conversation-with-jakob-jenkov-part-ii/jekov.jpeg" alt="" class="wp-image-98946" width="400" height="400">
</figure>

Welcome back to the second installment of our in-depth conversation with Jakob Jenkov, a leading figure in the world of Java. If you joined us for[Part I](https://foojay.io/today/exploring-the-depths-of-java-a-comprehensive-conversation-with-jakob-jenkov-part-i/), you'll recall the insightful discussions around the evolving landscape of Java and its continuous improvements. Today, we delve even deeper into the subject. Jakob will share his thoughts on the challenges of modern software development, the impacts of artificial intelligence on developers, and much more. Get ready for another fascinating exploration into the depths of Java with Jakob Jenkov!

**Bazlur: Java has evolved significantly over the years, with new features and functionalities being introduced in each release. Out of these numerous updates, is there a specific feature that you find particularly exciting or innovative? Can you share why this feature stands out to you?**

**Jakob:**

All in all, it's nice to see the language become more concise when possible without sacrificing readability. I am not myself too moved by these improvements, but I see many other developers that feel that these small, continuous improvements make a difference to them. I also tend to adopt concise notations once I get used to them. However, many syntactical improvements do not actually enable us to do something new with the language, but only to do what we could already do in a slightly more concise way. So, while these improvements are nice to have, they do often also just feel like "nice-to-have" improvements, too 🙂

I find the performance or hardware-near features to be the most interesting these days because these features do give us some level of functionality not available to us before. The Vector API bringing us SIMD operations is pretty cool. Virtual threads enable us to run far more blocking IO operations concurrently than we could before. The foreign memory access API is cool too, and the features bringing faster calls between Java and native code are cool too.
> *I find the performance or hardware-near features to be the most interesting these days because these features do give us some level of functionality not available to us before.*

I also like the API updates that bring new functionality we did not have in the platform before. However, we have to be careful here not to bloat the platform with tons of new APIs that are only rarely used.   

*** ** * ** ***

**Bazlur:** **From your perspective, how can a programming language evolve to meet new software design challenges without excessively bloating its feature set or creating too much complexity?**

**Jakob:** This is a tough challenge! On the one hand, you want developers to have as much expressive freedom as possible, meaning the ability to implement exactly what they want. On the other hand, you may not want a language with thousands of features that take 20 years to understand fully.

The trade-off in programming language design seems to be between "control" and "convenience." By control, I mean being able to control how a given operation is being executed and how data is modelled and stored in memory etc. By convenience, I mean being able to have an operation executed with little effort, shortcode etc.
> *The trade-off in programming language design seems to be between "control" and "convenience." By control, I mean being able to control how a given operation is being executed and how data is modelled and stored in memory etc. By convenience, I mean being able to have an operation executed with little effort, shortcode etc.*

<br />

I tend to prefer languages that prioritize control first and convenience second. Not languages that prioritize convenience first and control second.

*** ** * ** ***

**Bazlur: How would you advise a junior developer or recent graduate who wishes to navigate the expansive Java ecosystem effectively? What fundamental knowledge or skills should they acquire to become a productive member of a software engineering team?**

**Jakob:** There is a core set of Java language features and Java APIs that every Java developer should probably know. Focus on those first. After that, I would primarily learn the features and APIs I need for work or that I am myself interested in learning. Outside of that, I might scan over other features quickly, just to get an idea about what they can do - so I know in case I may need them one day.

*** ** * ** ***

**Bazlur: Let's shift gears and talk about the current state of software development. In your opinion, what are some of the biggest challenges that the software industry and its developers face today?**

**Jakob:** One of the biggest challenges we face is, in my opinion, technical fragmentation. With an ever-increasing list of programming languages, toolkits, frameworks, clouds, and SaaS solutions, it can be really hard to choose "the right tool for the job" - as the research work itself of determining what tool is the "right" one ( the best for the job ) might sometimes take longer than just coding a tool yourself - because there are so many existing solutions to examine.
> *One of the biggest challenges we face is, in my opinion, technical fragmentation. With an ever-increasing list of programming languages, toolkits, frameworks, clouds, and SaaS solutions, it can be really hard to choose "the right tool for the job"*

Consequently, learning and keeping up with all the developments in this fragmented tech space becomes increasingly harder. And thus, hiring gets harder too.

Another consequence is an overly high reliance on "best practices" within different tool spaces rather than having a deep enough knowledge yourself to determine which practices are right for your situation.

*** ** * ** ***

**Bazlur: Do you have any advice on how the developer can overcome these challenges?**

**Jakob:** Not really. Patience, perhaps. Patience, when trying to keep up with developments, and patience waiting until that new, shiny feature in a new tech shows up in the tech stack you are currently using - thus resisting a bit of the fragmentation.

*** ** * ** ***

**Bazlur: I must ask this question: As AI continues to evolve and becomes more integrated into various sectors, how do you foresee developers adapting to this change? Given the widespread concern that AI may replace many job roles, what is your perspective?**

I don't really know what will happen with AI. I am sure it will change the developer's jobs, but exactly how, I don't know. Python also made programming easier but seems to have resulted in more developer jobs, not less. AI might have a similar effect. If we are lucky, we get to focus on more high-level designs and leave the low-level nitty-gritty details to the AI.
> *If we are lucky, we get to focus on more high-level designs and leave the low-level nitty-gritty details to the AI.*

*** ** * ** ***

**Bazlur: Thank you so much for sharing your insights with us. We appreciate your time. Before we end, is there any parting advice or resources you would like to share with our readers, such as a list of recommended books or any other helpful information?**

**Jakob:** Well, perhaps it is better to rely on your own thinking than the thinking of someone else. By that, I don't mean you should not listen to anyone else. I just mean that you will benefit from understanding the reasoning behind whatever you end up relying on - rather than just taking someone else's word for it being true.

For instance, "best practices" should probably be called "common practices" instead, as that is what they often are: Commonly occurring solutions for similar types of problems. But they are not always the "best" solutions for each and every situation.

Pragmatism tends to work better than dogmatism.
> *Pragmatism tends to work better than dogmatism.*

I don't have any specific books to recommend. There are many good books, but a good deal of them are also a bit older by now.

<br />

*** ** * ** ***

Conclusion: {#h2-0-conclusion}
------------------------------

That concludes our insightful two-part conversation with Java expert Jakob Jenkov. We delved into Java's evolution, the challenges in software development, and the potential impact of AI. Jakob's advice for developers navigating these complex dynamics was truly invaluable. As we continue our journey in the programming world, his pragmatic approach and emphasis on understanding will guide us. Stay tuned for more illuminating discussions. Happy coding!
