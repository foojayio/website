---
title: "Getting Started Learning Java as a First Language"
date: "2020-09-24T13:44:00+00:00"
lastmod: "2020-09-24T21:28:29+00:00"
description: "I thought it’d be fun to write a post that provides people with no programming experience info on how to quickly and efficiently become Java developers!"
canonical: "https://developer.okta.com/blog/2018/12/11/learning-java-first-language"
authors:
  - "matt-raible"
image: "Favicon-3-2.png"
categories:
  - "Tutorials"
related_posts:
  - "running-single-file-java-source-code-without-compiling-part-1"
  - "fantastic-jvms-and-where-to-find-them"
  - "highlights-of-changes-to-the-core-java-platform"
  - "how-does-java-handle-different-images-and-colorspaces-part-3-introducing-the-bufferedimage"
frozen: false
---

I started programming in Java way back in 1999. I had just started a job as the director of web development at a small startup called eDeploy.com. I wrote a lot of HTML, CSS, and JavaScript for a SaaS product that helped companies install network equipment. The backend for our app was written in Java and we deployed to iPlanet Application Server. At our height, we trusted Loudcloud to run our software.

I experienced a bit of frustration after a few months because I'd find bugs in the Java code and it'd take a week for the backend team to fix them. I figured, *maybe I can help.* A year later, I was contributing a bunch of Java code to the project, and well on my way to becoming a proficient Java programmer. How did I do it? I studied **a lot** and wrote a lot of bad code. Making mistakes helps you learn awfully quickly. 😉

Rather than focusing on my experience, I thought it'd be fun to write a post that provides people with no programming experience how to become Java developers.

All the way back at JavaOne 2017, Oracle leaders said they wanted to make ["Java first, Java always."](https://www.infoq.com/news/2017/10/javaone-opening) It sounds like a daunting task, but I believe it's possible thanks to easy-to-use frameworks and a thriving open source community.

Personally, I believe Spring Boot is just as easy to use as Ruby on Rails, Express in Node.js, or Laravel and PHP. Add JHipster to the mix, and you've got a developer's dream.

The hardest part of using any of these languages and frameworks is getting your environment setup correctly. This is probably the biggest hurdle for anyone new to programming.

Most newbies don't know what experienced programmers know: installs fail *all the time*. This brings me to the first resource for those new to programming.

### 0. Google it

If you don't know what the error message you're seeing means, Google it. If there is a file name and line number in the error, and it's your code, dig in and see if you can figure it out. But don't spend too much time on it. Google it, ask a friend, or just go for a walk.

Clarity often arises when you're not working on the problem itself.

I said this was a post on learning Java as your first programming language, but I haven't provided any Java resources...​ yet.

Rather than listing off my preferred methods, sites, and techniques for learning Java, I turned to my esteemed friends, the [Java Champions](https://twitter.com/java_champions). I'm lucky to be a member of this group with my heavy affection of JavaScript and TypeScript, but alas, they accept all kinds of folks that talk about and promote Java as a platform.

I sent an email to the Java Champions mailing list and asked them for their recommendations. Based on their responses, I created the following list of ways to get started with Java. Below are my favorites (so far).

### 1. Build Java code katas that are easily accessible

*Credit: [Donald Raab](https://twitter.com/TheDonRaab)*

We've had great success over the years teaching junior developers (interns and straight out of university) and up to senior developers (Tech Fellow level) the Eclipse Collections katas. I [blogged about them](https://medium.com/@donraab/a-tale-of-two-katas-ec956410d26d) and tried to make it easier to get started.

Ideally, Java code katas would be hosted in a web-based environment like Kotlin does so you can remove the complexity of installing an IDE, learning Maven/Gradle, etc. I've wanted to do something like this for Eclipse Collections Katas for quite some time, but so far have not invested enough time and energy to make it happen.

[https://try.kotlinlang.org](https://try.kotlinlang.org/)

I gave a talk at QCon NY along with a developer I work with who hadn't presented publicly before titled "Invest in Your Java Katalogue". The [video of the talk is now available on InfoQ.com](https://www.infoq.com/presentations/java-katas) if you have 45 minutes to spare.

If you don't have time to watch the video and just want to see the code for the katas, they're [available on GitHub](https://github.com/BNYMellon/CodeKatas).

### 2. Read, code, read, code...​ rinse and repeat

*Credit: [Henri Tremblay](https://twitter.com/henri_tremblay)*

1. Buy [Core Java volume one](https://www.amazon.com/Core-Java-I-Fundamentals-1-11th/dp/0135166306)
2. Read it
3. Do katas or just try to code something
4. Buy Core Java volume two
5. Read it in your free time while continuing to program

### 3. Check out JShell

*Credit: [Martijn Verburg](https://twitter.com/karianna) and [Michael Kölling](https://twitter.com/michaelkolling)*

**Verburg:** JShell can take away a lot of the cognitive load added by IDEs. Sometimes it's simpler to start in a REPL.

**Kölling:** BlueJ is an IDE (with a simpler interface than professional IDEs, but still an IDE) and it has had a built-in REPL since about 2007. Students write code in complete classes, can interactively test individual methods, and can also try out quick code snippets in a REPL. It's all there and has been for a long time.

There is also extensive teaching material for how to use this for teaching and learning, based on actual pedagogical research. You can combine things. No need for one or the other.

Yes, JShell is cool. Java 11 is pretty cool too --- did you know you can run `.java` files [using the `java` command](https://dzone.com/articles/launch-single-file-source-code-programs-in-jdk-11)?

### 4. Use the power of networking

*Credit: [Nikhil Nanivadekar](https://twitter.com/NikhilNanivade)*

You can solve many problems by reaching out to folks via Twitter or email. Most developers and experts are happy and willing to help folks who are facing difficulties, have questions or need a further in-depth explanation. All they have to do is ask (nicely).

I am a Mechanical Engineer by education and hadn't written even a "Hello World" in Java until July 3rd, 2012. It is the rich developer ecosystem which has helped me learn, understand, and grow.

And lastly, here's my suggestion:

### 5. Try the Spring Guides

I think the Spring team has done an excellent job of providing use-case based documentation for Spring Boot. The [Spring Guides](https://spring.io/guides) are a bunch of tutorials that show how to build one of the most popular things Java is used for: APIs and web applications.

Not only that, but [start.spring.io](https://start.spring.io/) allows developers to download an all-in-one package that only requires Java to run. You don't need an IDE. Heck, you don't even need a build tool thanks to Maven and Gradle wrappers that are included. Dare I say, it's super simple?

### Learn Java Today!

Hopefully, this short and sweet list of learning resources inspires you to try Java. It's a great language, that can do many things. Write once, run anywhere!

Do you already know Java and Spring? Then you might want to check out some of our posts that show how to work with alternative JVM languages.

* [Build a Basic CRUD Application with Grails and Okta](https://developer.okta.com/blog/2018/06/04/okta-with-grails-part2)
* [Build a Basic CRUD App in Android with Kotlin](https://developer.okta.com/blog/2018/09/11/android-kotlin-crud)
* [Build a Secure Notes Application with Kotlin, TypeScript, and Okta](https://developer.okta.com/blog/2017/09/19/build-a-secure-notes-application-with-kotlin-typescript-and-okta)

If you have any questions about getting started with Java, please add a comment below. For more developer advice, follow [@oktadev](https://twitter.com/oktadev) on Twitter, like us [on Facebook](https://www.facebook.com/oktadevelopers/), or subscribe to [our YouTube channel](https://www.youtube.com/channel/UC5AMiWqFVFxF1q9Ya1FuZ_Q).

**Note:** Used with permission and thanks -- originally written by and published on [the Okta developer blog](https://developer.okta.com/blog/2018/12/11/learning-java-first-language).
