---
title: "Unlocking Java Secrets with Frank Delporte Foojay.io Today"
date: "2023-04-10T13:48:51+00:00"
lastmod: "2023-04-10T17:13:08+00:00"
description: "Learn about Frank Delporte, Java Champion and Azul technical writer at Azul, on Java, resources for beginners, insights into in the industry."
authors:
  - "bazlur-rahman"
image: "Frank.png"
categories:
  - "Interviews"
tags:
related_posts:
  - "42-practical-java-design-patterns-builder-and-more"
  - "debugging-threads-and-asynchronous-code"
  - "interview-with-tom-granot-developer-observability-koolkits-and-reliability"
  - "java-in-education-combining-java-with-raspberry-pi-and-the-pi4j-library"
frozen: false
---

{{< img src="Frank-7-XS.jpg" class="alignright size-full is-resized" width="450" height="413" >}}

Welcome to our exclusive interview with [Frank Delporte](https://www.linkedin.com/in/frankdelporte/), a Senior Technical Writer at Azul, Java Champion, and a passionate advocate for all things related to Java and Java on [Raspberry Pi](https://www.raspberrypi.org/).

With extensive experience in the software industry, Frank has dedicated his career to making Java accessible to beginners and experienced developers alike.

In this interview, we will explore his journey in the software world, his work at [Azul](https://www.azul.com/), his contributions to the Java community, and the valuable advice he has for newcomers who have just started learning Java.

Join us as we delve into Frank's experiences and expertise, uncovering the secrets behind his success and the future of Java development.

## Profile

Name: **Frank Delporte** 🚀  

Affiliations: webtechie.be, Pi4J, CoderDojo 🌐  

Website: <https://webtechie.be/>💻  

Twitter: @[FrankDelporte](https://twitter.com/FrankDelporte) 🐦  

Mastodon: [@\[email protected\]](https://foojay.social/@frankdelporte) 🐘  

GitHub: [FDelporte](https://github.com/FDelporte)LinkedIn: [frankdelporte/](https://www.linkedin.com/in/frankdelporte/) 🔗

## Key Takeaways

1. Start learning Java with beginner-friendly resources like the "[Java Quick Start](https://foojay.io/java-quick-start/)" tutorial on Foojay.io and explore J'bang for simplified Java development.
2. Developers should communicate with end-users to better understand the real-life applications of their products and improve user experience.
3. A career in software development can extend beyond pure development to roles like team lead, sales, project management, and technical writing.
4. A strong community presence, such as attending local JUG meetings and conferences, is important for networking, learning, and staying updated in the software development field.

## **Fun Facts**

* From time to time, check your backups! Many years ago, I lost three weeks of work because of a hard disk crash, only to discover that the backup tape on the server contained not a single file.
* As a freelance video editor, in one year, I worked on three different cooking programs for national television. That's probably where I learned most of my "cooking skills."
* Geoff Marshall, a YouTuber, made a video of the Thameslink announcement system where I worked on. Geoff was the voice of one specific announcement for a short time. He talks with Matt, the person many may recognize as the voice of "Please mind the doors," see [https://www.youtube.com/watch?v=kMnD5_lJuV4\&t=203s](https://www.youtube.com/watch?v=kMnD5_lJuV4&t=203s)

### Bazlur: First of all, congratulations, Frank, on your recent achievement of becoming the Java Champion. How are you feeling?

**Frank:** Overwhelmed! When I started my [#JavaOnRaspberryPi](https://twitter.com/hashtag/JavaOnRaspberryPi?src=hashtag_click?) experiments in 2019, I never imagined this would ever happen.

I have been doing Java development for many years already, but it was only then that I started writing more on my own blog.

That writing resulted in a book, and becoming one of the first authors of [Foojay.io](https://foojay.io/), the website for ***Friends Of OpenJDK***, which started around the same time.

My book got published right before we all had to stay home because of Corona, so I wasn't able to go to speak about it at JUGs and conferences in person, but I did get to speak "around the world" virtually.

That's how I felt that the Java community is a really friendly and welcoming community.

And that's what I still do today, experiment with Java, write down what I learned, and share that experience online and at conferences. Apparently, that work got noticed, and I was nominated and accepted to become a [Java Champion](https://javachampions.org/members.html).

I definitely don't consider myself the best "Java-programmer-in-town" (even living in a tiny town).

There are way smarter people than me who develop amazing Java applications and contribute to the OpenJDK project and the JVM! But I do love to talk about what can be made with Java and how the whole system has been evolving since the 6-months release cycle.

And that's what I really would like to keep doing in the next years: "Spread the love for Java." 🙂

### Bazlur: It's great to see how your passion for Java and sharing your knowledge has led to this achievement. As you continue to spread your love for Java, what are the most exciting developments or features in the Java ecosystem that you're looking forward to sharing with the community in the coming years?

**Frank:** It's incredible to see how much evolution we have witnessed in OpenJDK and the JVM since the 6-months release cycle was introduced.

The many preview features that are now ongoing promise even more improvements.

I'm not an expert on JNI, but it's a crucial part of Pi4J, the library to control the GPIOs on a Raspberry Pi and interact with electronic components.

So Project Panama with Foreign-Functions and Memory API could bring many improvements to that project. [Virtual Threads](https://www.infoq.com/news/2023/04/virtual-threads-arrives-jdk21/) will definitely also be a significant change.

Still, I think that will be driven for a big part by how it gets integrated into, for instance, Spring and bring a performance boost to applications without requiring developers to change their code.

For me, the most exciting developments are the ones that make code more clean and readable. So I'm a big fan of [Text Blocks](https://openjdk.org/jeps/378) and all the new functions related to [instanceOf](https://openjdk.org/jeps/305).

### Bazlur: Can you tell us about your background and how you started in the software industry?

I have been programming since I was ten years old and got a Commodore 64. I combined it with a relays-board and could control my Lego train and disco bar lights.

That's how I fell in love with "engineering," controlling physical things with computers.

I studied film and video engineering at a Brussels school and started my professional career as a video editor.

It was the time when analog video got replaced with the first computer editing, and I soon went back to programming when clients wanted their video converted to digital media, being CD-Rom and DVD, and a bit later, the very first internet video.

I started within the Microsoft world with C#, SQL Server, ASP.NET,..., and later extended with Flex/Flash UIs.

When I joined Televic Rail, a Belgian-based company building passenger information systems for trains, I switched to Java.

The whole "ecosystem" of open-source, a friendly community, having tons of tutorials and blogs, libraries for everything you can ever possibly need,... has been and still is a truly fantastic experience.

### Bazlur: It's impressive that you started programming early on. How did your background in film and video engineering and witnessing the analog-to-digital transition impact your journey in the software development field?

**Frank:** In film school, we had to make a "real" movie with pellicule cameras, which meant the editing process was physically cutting and pasting pieces of pellicule and audio tape.

When I graduated in 1994 and started as a video editor, I started working in editing rooms with a huge number of costly equipment.

The process involved a continuous flow of copying video from one or more tapes to another.

A bit later, so-called online video editing on a computer became the new standard.

So we went from a massive desk with many dedicated devices and screens to a single computer with many hard disks.

I was one of the first users in Belgium of the Media 100, such a computer-based editing system, and visited several companies to demonstrate how it could speed up the editing process and generate better end results.

Going from analog copies with quality loss, to lossless digital video, was only one of the significant improvements.

<figure class="wp-block-gallery has-nested-images columns-default is-cropped wp-block-gallery-1 is-layout-flex wp-block-gallery-is-layout-flex">
 <figure class="wp-block-image size-full">
  <a href="editing-film.jpg" target="_blank" rel="noopener"><img decoding="async" width="474" height="266" data-id="65687" src="editing-film.jpg" alt="" class="wp-image-65687"></a>
 </figure>
 <figure class="wp-block-image size-full is-style-default">
  <a href="editing-video-analogue.jpg" target="_blank" rel="noopener"><img decoding="async" width="720" height="480" data-id="65686" src="editing-video-analogue.jpg" alt="" class="wp-image-65686"></a>
 </figure>
 <figure class="wp-block-image size-full">
  <a href="editing-video-media100.jpg" target="_blank" rel="noopener"><img loading="lazy" decoding="async" width="450" height="250" data-id="65688" src="editing-video-media100.jpg" alt="" class="wp-image-65688"></a>
 </figure>
</figure>

Soon after, my clients wanted to distribute their company movie to (potential) customers easily and attractively. That's when Macromedia Director and Flash became my new working tools as they allowed me to combine slides, video, and coding on DVD and CD-Rom. The logical next step was to put the same content online. You may remember the annoying "Skip Intro" Flash animations that were used on many websites for some years, and yes, I have to admit, I also created those... Everyone sought new ways to convert static boring text pages into more appealing content. We probably made a lot of wrong decisions at that time, but don't we still? Anyhow, soon I needed a database to create dynamic and easily updatable websites and started using Access Database for it - bad idea - and SQL Server.

Nowadays, video and audio are everywhere, and we don't need to consider file sizes and bandwidth constraints. We have come a long way, and I would really like to go back to my first professional days and tell my younger self what incredible years lie ahead.

### Bazlur: I must say, your journey thus far has been awe-inspiring. If I say those days were more thrilling and dramatic compared to the present, do you agree?

**Frank:** Sometimes I'm homesick for the time that I was working on television. The "glitter and glamour" of working behind the scenes of it brings a very special emotion. But then I remember the long working hours, sometimes seven days a week, with a lot of lost time in traffic, driving to the various studios in and around Brussels... Yes, those times were more thrilling, but when you get older and have a family, you start appreciating a quieter life. Now my only home-work commute is one stair up to my home office. I have all the equipment I can dream of to create videos and podcasts at a fraction of the price these things cost 20 years ago (see <https://webtechie.be/post/2023-03-17-podcast-editing/>). And from here, I have access to the whole world and can talk and share ideas with people like you. I've had my part of "thrills and drama", enough not to miss it...

### Bazlur: Absolutely, priorities change as we age, and we start to appreciate different aspects of life, while some tasks are just easier when tackled at a younger age. Now, let's delve into the realm of software development. I'm curious about your perspective on the challenges that developers grapple with in today's world.

**Frank:** With all available programming languages and all possible deployment options on-premise or in the cloud, I can imagine it's hard to make a decision about which tools you should use or in which you should specialize. And then, you still need to decide what you will use to develop your backend, frontend, mobile, or desktop... There are too many options, and the right tool depends on many criteria. The golden rule for every decision is still "it depends." Pick the tool that seems to be the best solution for you, where you feel most comfortable with, and that just offers the solution you need. Or maybe we should all just agree Java is the solution for all, with, for example, Vaadin for frontend, and JavaFX for mobile and desktop.

As you [discussed with Shai Almog](https://foojay.io/today/navigating-the-challenges-of-modern-software-development-an-exclusive-interview-with-shai-almog/), ChatGPT and other AI tools will also have quite some impact, but it's tough to predict how this will evolve as the end of programming has already been predicted many times in the past.

### Bazlur: Since you mentioned that the golden rule for every decision is "it depends," do you think having a programming language named "Depends" would help settle the debate on which language to choose, hahaha!

**Frank:** That would be a good idea! But the best new language that was recently introduced is definitely the ***Rockstar*** language! If you have never heard about it, check out this video: [https://www.youtube.com/watch?v=_T8ta3BBhc4\&t=2190s](https://www.youtube.com/watch?v=_T8ta3BBhc4&t=2190s)

### Bazlur: That gave me a good laugh! Moving on to a more serious note, I've noticed that you've been developing content for Java beginners and have even authored a book on Raspberry Pi. What advice would you give to newcomers who have just started learning Java? Where should they begin their learning journey?

**Frank:** That's exactly something I wanted to solve in my book and therefore included a "quick start" tutorial to learn the basics of Java. Because we also missed beginner content on the Foojay website, I converted that part of the book to a video and online tutorial you can find at [https://foojay.io/java-quick-start/.](https://foojay.io/java-quick-start/) In just 30 minutes of video, I can take you through the first steps of programming with Java. But there are a lot of other free tutorials you can find online, and a few are listed at the end of the Foojay Java Quick Start.

I also like what [J'Bang](https://www.jbang.dev/) has done to simplify how you can get started with Java development without the need to install a Java runtime.

### Bazlur: Your resources for beginners are fantastic, and I'm sure our readers will find them incredibly helpful. As someone with great experience in the software industry, do you have any enjoyable or memorable stories you'd like to share with us? We always love hearing about personal experiences in the industry. Can you share any particular story or anecdote from your time working in software development that either made a lasting impression on you or had a significant impact on your career?

**Frank:** When I was working on the passenger information screens for trains, we had a project for the Thameslink trains in the UK. We first went to Germany, where one of these new trains was tested on a circular 6 km track, to test if our system was announcing the correct stations and stops. Most of these test drives were during the night. One night, the train driver let me take over the "steering wheel" and let me drive that brand new 240-meter-long train of over 10 million euros for one tour around the track. I missed the "station" with 150 meters and learned that everyone should stick to their job 😉

Later, a few colleagues and I were on that same train for test drives on the actual track. Again during the night hours, we drove a few times up and down through London to test all announcements and see if our system was working as expected. I just read the book "[The Girl on the Train](https://www.litcharts.com/lit/the-girl-on-the-train/summary)" about a divorced woman who used to live next to a train track in London. It was a bizarre feeling to be on that train with only a few people and look at the lives of all those people living next to the tracks, while our system was announcing every stop for an empty train...

### Bazlur: As a technical writer at Azul, could you provide some insight into your job responsibilities and your experiences in this role? It is often believed that in a software company, one can only pursue a career as a developer, especially for those starting out. Can you shed some light on this career path and the benefits of being a technical writer?

**Frank:** I always have "mixed" pure development with other roles as I worked in smaller companies. Sometimes I had to join sales, lead a small team of developers, design the architecture, follow up with the client,... BTW, if you have the opportunity as a developer to get into contact with the end-user, please do! It is also always a good thing when you are developing a product as it learns you how it will be used in real-life, which, in most cases, is entirely different than what you imagined. Being a good developer, who keeps the end-user and user experience in mind, is an incredible plus. I have seen such colleagues evolve into team lead, sales, project management, and product management roles.

I didn't plan to become a technical writer. Until last year I was a developer who loved to write, yes, even documentation, probably one of the most hated jobs for developers 🙂 But when I started looking for a new job, Geertjan Wielenga, who leads Foojay.io and works for Azul, informed me that his company sought a technical writer. And I think there is no better place to work if you want to learn more about the internals of Java, than at one of the leading companies within the Java eco-system... So that's how I became a writer who loves to develop.

Together with my colleague, Matt Van Order, we maintain [docs.azul.com](https://docs.azul.com/), where release notes of the two OpenJDK distributions of Azul (Core and Prime Builds of OpenJDK) are published, next to other product documentation. We also work on blog posts for the Azul website and Foojay. As a side-project, it also allows me to talk to many of the well-known Java community members for the [Foojay podcast](https://foojay.io/today/category/podcast/). It's both a challenging and rewarding daily task to keep in contact with the Java users, learn from the many experts that work at Azul and know the Java code in detail, and provide helpful content to guide developers.

### Bazlur: Thank you so much for sharing your insights with us. We really appreciate your time. If we have any further questions, we will be sure to reach out to you. Before we end, is there any parting advice or resources you would like to share with our readers, such as a list of recommended books or any other helpful information?

**Frank:** Well, foojay.io is definitely a recommended resource 🙂 And am I allowed to do some shameless self-promotion? If you want to get started with electronics programming with Java on the Raspberry Pi, take a look at my book you can get it on [Leanpub for 9,99$ or the amount you want to pay](https://webtechie.be/books/). Recently I read and reviewed the book "[Practical Design Patterns for Java Developers](https://foojay.io/today/book-review-practical-design-patterns-for-java-developers/)" by Miro Wengner, which gives a perfect view of recent changes in the Java language and how you can use them with 42 practical design patterns for clean and maintainable code.

I'm also a big fan of JavaFX to build GUI (Graphical User Interface) desktop and mobile applications. There is a perfect "bible" with a complete overview of the possibilities of JavaFX: "[The Definitive Guide to Modern Java Clients with JavaFX 17](https://link.springer.com/book/10.1007/978-1-4842-7268-8)". And you can even use it for game development, for instance, with the FXGL library. "Learn JavaFX Game and App Development with FXGL 17" by Almas Baimagambetov is a perfect [getting started](https://foojay.io/today/book-review-learn-javafx-game-and-app-development-with-fxgl-17/) if you want to explore this.

But the best advice is probably just to keep an eye on a few technology news sites, follow some Java experts on Mastodon (or Twitter...), or even join <https://foojay.social> if you don't have a Mastodon account yet. If you can join a local JUG occasionally or visit a conference, please do! Getting in contact with the community and learning from the many resources all these people share is a great way to learn!

### Conclusion:

In conclusion, our engaging interview with Frank Delporte has provided valuable insights into the world of Java development, the importance of community involvement, and the diverse career paths available in the software industry.

Frank's dedication to creating beginner-friendly content and his passion for Java and Raspberry Pi demonstrate the ever-growing potential of these technologies.

His experiences and the resources he shared will undoubtedly inspire and assist those embarking on their own Java learning journey.

We hope that our readers have found this interview both enlightening and motivating, as it showcases the exciting opportunities that lie ahead in the realm of software development and the power of a supportive community.

**NOTE:**

***We have recently made a change to our interview format by removing the editor's note at the end of each question and answer. We would love to hear your thoughts on this alteration. Please let us know whether you find this change beneficial or if you feel that the previous format with the editor's note was more effective. Your feedback is essential in helping us continually improve our content and provide the best experience for our readers.***
