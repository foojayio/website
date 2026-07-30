---
title: "Cross-Platform Development in Java with Gluon and GraalVM"
slug: "cross-platform-development-in-java-with-gluon-and-graalvm"
date: "2020-10-13T12:27:38+00:00"
lastmod: "2021-08-23T12:55:13+00:00"
description: "Gluon has invested in R&D to create a technology stack over the past five years. Gluon now offers a complete end-to-end development platform."
authors:
  - "bruno-lowagie"
image: "/images/posts/2020/10/cross-platform-development-in-java-with-gluon-and-graalvm/gluon-bruno-figure01.png"
categories:
  - "Gluon"
tags:
related_posts:
frozen: false
---

*"What do you do for a living?"*   

That's a common question when making conversation, but how many times have you felt that you were giving an accurate answer? My personal answer largely depends on who's asking. When asked by another professional in my business, I can speak freely without much risk of being misunderstood. When asked by someone who isn't tech-savvy, my answer can be as simple as *"I work in IT"* , hoping that I won't get the dreaded response: *"Oh, you're doing something with computers! Maybe you can solve my printer problem."*

The job of a CTO / Software architect {#h2-0-the-job-of-a-cto-software-architect}
---------------------------------------------------------------------------------

In a reply to such a question, you can try to be a tad more accurate by saying: *"No, I don't fix computers. In my job, I help my management decide how we can digitally exchange data with our customers and how we can provide digital services for internal and external use."* That's a mouthful, but if you have pen and paper, you can make the following drawing:
![](/images/posts/2020/10/cross-platform-development-in-java-with-gluon-and-graalvm/gluon-bruno-figure01.png)

This drawing isn't accurate, but it's already better than telling people you're *"doing something with computers."*

Your company has a back end that consists of a combination of internal and external servers that are used to manage data in databases and process that data in an automated way through software. It is your task to ensure that an end user can interact with that back end, for instance by using an application on his own computer or smartphone.

Usually, that's where the conversation that started with *"What do you do for a living?"* ends, but you and I know that your actual job is much more complex. In the drawing you made, you showed a portable with a globe on the screen, which could lead to believe that you're responsible for creating and maintaining the company websites, but that's only a small---albeit important---aspect of your job.

You could add more detail to your drawing to reflect this.
![](/images/posts/2020/10/cross-platform-development-in-java-with-gluon-and-graalvm/gluno-bruno-figure02.png)

Apart from providing a responsive web site that can be used in a browser on the desktop or on your phone, you may also want to offer applications that run on mobile devices such as iPhones, iPads, Android phones, Android tables. For complex functionality, you may also require applications that run on the desktop. In some cases, you're also responsible for embedded software or software that runs on IoT devices.

The Java Platform {#h2-1-the-java-platform}
-------------------------------------------

It is your task to lead the development team that makes all of this happen.
![](/images/posts/2020/10/cross-platform-development-in-java-with-gluon-and-graalvm/gluon-bruno-figure03.png)

If you're like me, you've once had the job of the people you are now leading. I wrote my first lines of code in BASIC in 1982. I was twelve years old. I tried many different programming languages, but when I wrote my first lines of Java code in 1996, I knew I had found the language that was the best fit for my professional needs. I've experimented with other languages in projects where Java wasn't an option, but whenever I had a choice between a project with Java or a project without Java, I chose the project with Java.

I'm not going to preach to the choir, explaining why I think Java is the better choice for mission-critical applications in a back end that must meet high security requirements. The audience I want to address with this article already knows this to be true. My goal is to explain that Java can also be an option if you want to create mobile applications. That's a lesser known fact among Java professionals.

Cross-platform development {#h2-2-cross-platform-development}
-------------------------------------------------------------

If you've been using Java for your company's back-end infrastructure, you probably also want to use Java as a technology for the front-end development too. Searching the technological landscape, you might assume that your options are limited to:

* Native development for every platform,
* Create applications using HTML, CSS, and JS,
* Solutions such as ReactNative and Flutter,
* C# development with Xamarin.

All of these options have pros and cons.

### Native development for every platform {#h3-3-native-development-for-every-platform}

Creating native apps for iOS and Android will give you great performance on the platforms the apps were created for, but the main disadvantage is that you must create and maintain different code bases.

At first sight, Android may look like Java, but as soon as you start working with Android, you'll notice that it's mostly based on Java 7, which is problematic once you want to introduce third-party libraries that require more recent versions of Java.

You'll also need code that is written for the iPhone and iPad, which means you'll need Swift or Objective-C developers. You'll enter a complex ecosystem, with frequent new SDKs, rapid deprecation, and a strong vendor lock-in for many iOS libraries.

### Using HTML, CSS and JS {#h3-4-using-html-css-and-js}

Many small-town developers will argue: *"Why would you need to create native apps? Why don't you simply use HTML, CSS, and JS? You'll have your app in no time, and it will run on every device that has a browser!"*

While that's certainly true for simple applications, the quality and performance of cross-platform in-browser apps are usually sub-optimal when a certain complexity is needed. Maintaining a project that depends on a plethora of ECMAScript libraries can be hell, and security issues are easily introduced even if everyone in your team pays attention---which isn't something you should assume to be true by default.

### ReactNative and Flutter {#h3-5-reactnative-and-flutter}

ReactNative or Flutter are popular with startups that are "born mobile" and who have skipped the step of making desktop applications. Apps can be created relatively fast, but whether they are future-proof remains to be seen. The maintenance cost is usually high because of frequent breaking changes in these technologies.

### Xamarin {#h3-6-xamarin}

If you are working in a Microsoft environment and your team is well-versed in C#, then Xamarin may be a valid option for you.
![](/images/posts/2020/10/cross-platform-development-in-java-with-gluon-and-graalvm/gluon-bruno-figure04.png)

Developers create their code once in Visual Studio, they use Xamarin to compile it to different platforms, and they can easily integrate Azure if they need Cloud functionality. Wouldn't it be great if a similar solution existed for the Java Platform?

[Continue to part 2...](https://foojay.io/blog/cross-platform-development-in-java-with-gluon-and-graalvm-part-2/)
