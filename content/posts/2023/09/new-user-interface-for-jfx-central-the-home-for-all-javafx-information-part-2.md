---
title: "New User Interface for JFX Central, the Home for JavaFX – Part 2"
slug: "new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-2"
date: "2023-09-18T05:42:34+00:00"
lastmod: "2023-09-18T05:55:24+00:00"
description: "JFX Central is a not-for-profit team effort. Many people are involved, and any issue or pull request provided by anyone from the JavaFX community helps!"
canonical: "https://webtechie.be/post/2023-09-07-jfxcentral-new-user-interface/"
authors:
  - "frankdelporte"
image: "https://foojay.io/wp-content/uploads/2023/09/homepage-intro-1024x769-1.png"
categories:
  - "JavaFX"
tags:
related_posts:
frozen: false
---

In [part 1 of this series](https://foojay.io/today/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-1/), we looked into the new design of [JFX Central](https://www.jfx-central.com/), the place to be for all JavaFX info.

**JFX Central is a not-for-profit team effort. Many people are involved, and any issue or pull request provided by anyone from the JavaFX community helps to improve and extend the website, application, and data. I reached out to some people involved to learn about their history in Java and JavaFX development and how they are engaged in JFX Central.**

### Dirk Lemmermann {#h3-0-dirk-lemmermann}

*CEO of Senapt GmbH \& DLSC Software \& Consulting GmbH, [@dlemmermann](https://twitter.com/dlemmermann)*

<figure class="wp-block-image size-thumbnail">
 <img fetchpriority="high" decoding="async" width="300" height="260" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-2/dirk-300x260.jpg" alt="" class="wp-image-102262">
</figure>

**Can you introduce yourself and your history in JavaFX and JavaFX development?**

I started programming in Java immediately after graduating from the University of Oldenburg in Germany in 1996. I was a research developer at Pittsburgh's Carnegie Mellon University (CMU) Robotics Institute. With CMU being James Gosling's alma mater, I assume that Java had a home game in that place, and it got adopted quite early in various research projects, including mine.

My primary task was the development of a user interface for a planning and scheduling application. This was first done in AWT and later on in Swing. A library that came out of this work was DJT (Dirk's J Toolkit), the first product I marketed commercially. The successor product was called FlexGantt, which was still written in Swing.

In 2013, I was approached by a customer who asked to port the Swing version to JavaFX. I initially hesitated to do so because, back then, I had heard nothing good about JavaFX. The script-based version failed, and the 2.x release was supposedly unstable and fast. But in 2013, JavaFX 8 was released, and this version finally showed the full potential of JavaFX. It took me about a year to port FlexGanttFX, and after that, I started freelancing and consulting for JavaFX projects full-time.

**Why did you start jfx-central.com?**

JavaFX is Oracle's unloved child. They cannot make any money with it, and the effort they make when it comes to marketing is close to zero. Oracle's decision to stop bundling JavaFX with the JDK did not help either. Even though the separation makes a lot of sense to me, it sent a negative message to the Java community.

Many developers predicted the end of JavaFX and that Oracle would let it die. However, this just isn't true. JavaFX development is still ongoing inside the [OpenJFX project](https://openjfx.io/). Engineers from Oracle and Gluon are working on it together with the community. Bugs are constantly fixed, and new features are added. Unfortunately, most people do not know about this. They do not know that JavaFX is still alive. They do not know who works on it. They do not know how many "real world" projects use it. And last but not least, they do not know which libraries and tools are available to develop JavaFX applications.

To change this, I developed JFX Central. I wanted to make sure that people knew all these things. A single place where they can find out everything there is to know about JavaFX, the entire ecosystem. Other programming languages have their dedicated entry point on the web (e.g., [Python](https://www.python.org), [Kotlin](https://kotlinlang.org)). JavaFX did not have such a site, and JFX Central is stepping in to fill this void.

**JFX Central got a whole new version. A completely new look and feel on top of the existing data. What was the reason for this new version?**

I initially planned to release JFX Central as a desktop application and maybe also for mobile devices (via Gluon). Only later, we decided to deploy it to the web via JPro. Unfortunately, several design decisions did not work well as the application was now running inside a browser. This ultimately resulted in bad performance and inconsistent navigation. In addition, the whole application had grown so much that the menu concepts did not work well. Last but not least, we knew the app could look more professional if its appearance were specified by a professional designer first.

**Did the site get extended with new sections?**

Yes, it did! The "Links of the Week" are still shown directly on the homepage, but they also [have a dedicated page](https://www.jfx-central.com/links) now with an easy way to go back to older postings and soon also with its own RSS feed.

Another addition is the ["Icons" section](https://www.jfx-central.com/icons), where users can browse hundreds of icon fonts with thousands of individual icons. We implemented a nice search capability that makes it easy to find icons across a single icon font or all icon fonts.

The latest addition to JFX Central is the ["Documentation" section](https://www.jfx-central.com/documentation), where we link to the various online documentation resources available for JavaFX developers.

**The site is also a desktop application. Is this the complete same code? What challenges did you need to solve to achieve this?**

It is the same code base except for a few if-statements where we check the runtime environment. For example, if a link opens a file, we can use the "Desktop" API to open it on the desktop. If the application runs as a web application, the link will perform a download. Another example: only install the "tray icon" when we are running on a desktop.

**What evolutions did you see in JavaFX in the most recent years? And what do you expect for the future of JavaFX?**

Up until the final release of JavaFX 8, we could frequently see new features being added. This was not the case for a long time. The primary focus was on stability (bug fixes) and performance improvements. Only now do we hear about new features being added (e.g., [rich text control](https://gluonhq.com/presenting-a-new-richtextarea-control/)), which makes me hopeful that this will become a new trend. According to the rumor mill, Oracle has also increased the number of people working on JavaFX again.

### Li Wang Yang {#h3-1-li-wang-yang}

*JavaFX developer and freelancer, [@LeeWyatt_7788](https://twitter.com/LeeWyatt_7788)*

<figure class="wp-block-image size-thumbnail">
 <img decoding="async" width="300" height="260" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-2/liwang-300x260.jpg" alt="" class="wp-image-102263">
</figure>

**Can you introduce yourself and your history in Java and JavaFX development?**

My affinity for Java stems from its rich ecosystem, offering an abundance of libraries that streamline the development process. I was previously inclined towards Swing for client development, but its limitations in customizing components and appearances made things challenging. I stumbled upon JavaFX about six years ago, which instantly captivated me. The support for CSS styling, property bindings, built-in animation API, charts, and WebView made JavaFX irresistibly appealing. Its introduction made client development more straightforward and versatile, and I haven't looked back since.

**How did you get involved in jfx-central.com?**

At the beginning of 2023, Mr. Dirk Lemmermann, whom I deeply admire and consider my programming idol, was searching for a JavaFX developer to assist in developing the CRM client of his company, "Senapt." I saw this as a golden opportunity to work with someone I admire. I'm deeply appreciative of his trust in me. After completing the "market data" module of the CRM, he invited me to contribute to the new JFX Central website.

Under his visionary leadership, we outpaced our development expectations. He was adept at consolidating similar components from different pages into singular tasks, enabling efficient component reusability with minor adjustments. Throughout our collaboration, he patiently guided me, selflessly imparting his wealth of experience. The principles I've learned from him, such as KISS (Keep it simple, stupid!) and DRY (Don't repeat yourself), have been invaluable.

**What were some of the main challenges of using the same code base for a website and desktop application?**

One of the significant challenges in using the same codebase for web and desktop applications was my initial mistake of using global static variables. While this approach was okay for desktop applications, it was problematic for web implementations, especially when using technologies like JPro to render JavaFX on web pages. This is because static variables could potentially impact multiple user sessions on the web, causing unexpected behaviors.

Another challenge was in implementing a responsive design. While it worked well on standard pages, design differences across various screen sizes occasionally necessitated the complete reconstruction of components, causing transition delays. Effective communication with designers could mitigate this in the future.

**Can you compare JavaFX to other frameworks, e.g., in JavaScript?**

When comparing JavaScript with JavaFX, I have a stronger inclination towards JavaFX. Native JavaScript's dynamic nature challenges a steeper learning curve, with specific nuances like variable scoping requiring meticulous attention. However, its expansive history provides a richer selection of third-party components.

JavaFX may not be as abundant in this regard, but leveraging Java's robust ecosystem ensures a swift development pace. The capability of Java to leverage Ahead-of-Time (AOT) compilation brings improvements to JavaFX, such as faster application startup times and reduced memory consumption. This feature has further solidified my preference for JavaFX in specific scenarios.

**How do you see the future of JavaFX?**

As for JavaFX's future, I'm optimistic. With global trends moving towards the indigenization of software tools, I believe client-side applications might experience a renaissance, with JavaFX emerging as a leading choice.

**What other features would you like to add to jfx-central.com?**

My primary aspiration for JFX Central is to introduce a suite of visual development tools tailored to the JavaFX community. This includes simple utilities like an Icon Maker. Additionally, several other tools are in the conceptualization and planning stages. By offering these tools on JFX Central, we aim to enhance the development experience and boost the productivity of fellow JavaFX developers, addressing specific needs within our community.

I owe profound gratitude to Mr. Dirk Lemmermann for the invaluable opportunities and guidance, and to you, Frank, for your constructive feedback on refining my Java code and for your unwavering commitment as the editor of JFX Central's "Links of the week" section. I also thank Florian for his expertise in JPro, which was instrumental in guiding me through its intricacies. Interacting with and learning from all of you has been a cherished experience in my journey.

### Florian Kirmaier {#h3-2-florian-kirmaier}

*CO-Founder and CTO at Sandec GmbH, [@FlorianKirmaier](https://twitter.com/FlorianKirmaier)*

<figure class="wp-block-image size-thumbnail">
 <img decoding="async" width="300" height="260" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-2/florian-300x260.jpg" alt="" class="wp-image-102264">
</figure>

**Can you introduce yourself and your history in Java and JavaFX development?**

Hi, I'm Florian Kirmaier, 33 years old, working on different Java technologies since I finished at the University more than ten years ago. My focus has always been the Java ecosystem, including other JVM-based languages, such as Scala, Kotlin, etc. All those years, I have worked with JavaFX as an end user and a community committer. I am the co-founder and CTO of SANDEC and a co-founder and lead developer of JPro.

**With [JPro](https://www.jpro.one/) you are bringing JavaFX to the browser. How is this achieved? Can you share the process to convert JavaFX to a website?**

JPro runs a JavaFX application in a headless mode on the server side. All rendering tasks take place on the local device, inside the browser, using standard HTML5/JS. The developer can leverage the full power of the JVM with its entire ecosystem - while developing web-based applications. This way, we achieve excellent compatibility with the Desktop. At the same time, only minimal knowledge of the very complex web ecosystem is required.

JPro Apps are pixel-accurate and can look in the browser exactly as they look on the desktop. They are even better than pixel-perfect. You can infinitely zoom and still get perfect round corners. JPro doesn't send the pixels but the scene graph to the client. Afterwards, it's rendered with basic HTML - allowing infinite zoom, with the same result as vector graphics. That's also why not much traffic is required - only the scene graph is synchronized.

**Other approaches achieve the same result, like [Web port by Gluon](https://docs.gluonhq.com/#platforms_web) and [WebFX](https://webfx.dev/). What are the differences?**

In general, we welcome more competition in this area.

WebFX compiles the project with GWT. Especially games based on Canvas, created with [FXGL](https://www.jfx-central.com/libraries/fxgl), seem to work quite well with WebFX! However, WebFX only supports a subset of JavaFX. For example, CSS is missing - and many of the controls. This makes most JavaFX Libraries unusable and causes problems for enterprise applications.

Regarding Gluon Web, there hasn't been any news since the initial preview. On their webpage, it's marked as experimental.

As far as we know, we are the only solution that works for more complex applications - especially for enterprise applications.

**You are contributing to the jfx-central.com development. Were specific code changes needed to bring it to your platform? Is the code still 100% pure Java and JavaFX?**

JFX Central itself is a pure Java and JavaFX application! We shouldn't forget that JFX Central also runs on the Desktop - so this is one of many examples of actual cross-platform development with JavaFX.

There are some minor differences in the web version. In the web version, the window lacks the frame and custom minimize/maximize buttons. The "ScrollToTop" Button currently executes some JavaScript. Also, the HTMLPage is custom - containing information for search engines and a custom favicon. However, the application would work well without these differences.

There is also some platform-specific code hidden in libraries. For example - we use the "history API" of the browser - so when you go to another page, the browser doesn't reload the page. Instead, the content gets replaced. This is way better for performance.

This routing framework allows you to use the Route pattern, meaning you map your URLs to specific pages. This way, we can write a JavaFX Application with "Links". We use this for all our web pages and desktop projects. Using this framework also has the nice effect of giving an excellent structure to your project.

**As a JPro JavaFX website is generated on the server, what kind of machine is needed for a typical use case?**

Of course, this heavily depends on the application. But if you optimize your website, you can reach 10 - 50 MB per concurrent (!) user. JFX Central currently consumes around 30 MB per concurrent user. If you deploy a desktop application on the web with JPro, the memory usage remains consistent with that of the original application.

**JPro is a commercial product and relies on specific server infrastructure. Do you see any free, open-source alternative usable for small community projects?**

We are already free for non-commercial projects. In general, we also allow the usage of JPro in Open Source projects. We usually provide a free license if someone wants to use JPro for a project that doesn't match this description and is not a commercial product. This, of course, also includes community projects.

Releasing JPro itself as open-source is another topic. It's something we discuss regularly at Sandec. We contribute to the open-source community more in JavaFX itself and its ecosystem. Even so, JPro itself isn't open source, we contribute back a lot to the community. We provide a lot of changes to JavaFX itself to make it better in general. We try to fix the bugs we or our customers find to improve JavaFX and its ecosystem.

In most cases, the libraries we release are open-source with a license allowing and supporting free usage inside commercial projects.

**What will the future bring for JavaFX on the web?**

Our most significant weakness today is the lack of good documentation, tutorials, videos, etc. We hope to provide extensive documentation soon, making it easy for JPro newcomers to write web pages and cross-platform applications with JavaFX!

Additionally, we'll make public various libraries we've created and employed in several of our custom, cross-platform projects. These libraries will address functionalities like media handling, file management, and OAUTH.

### Mike Hearn {#h3-3-mike-hearn}

*Founder Hydraulic Software*

<figure class="wp-block-image size-thumbnail">
 <img loading="lazy" decoding="async" width="300" height="260" src="/images/posts/2023/09/new-user-interface-for-jfx-central-the-home-for-all-javafx-information-part-2/mike-300x260.jpg" alt="" class="wp-image-102265">
</figure>

**Can you introduce yourself and your history in Java and JavaFX development?**

I've been developing with Java since about 2010, when I wrote one of the first independent implementations of Bitcoin using the language. Over the years, I've written mobile, server, and desktop apps using a mix of Java and Kotlin.

**How did you get involved in the JFX Central project?**

I emailed Dirk to tell him about Conveyor, the tool made by my company, and we ended up providing a build solution, so the JFX Central team doesn't need to take care of this. We build and run Conveyor locally to do the release, but a GitHub Action for Conveyor is available for anyone who wants to automate this.

**How does Hydraulic package the JFX Central desktop application?**

Hydraulic Conveyor is a command line tool that replaces `jpackage` and `jlink`. It supports more than just JVM apps but has specific built-in support for the JVM. You can point it at your JARs or use a Gradle plugin to extract information from your build, and it will then create and optionally upload signed self-updating packages for Windows, macOS, and Linux.

One of the valuable features of the product is that it doesn't use native tooling, so it can do everything from your developer laptop regardless of what OS you use or from a cheap Linux CI worker. It can also auto-update apps on every launch, keeping them in sync with any server protocols you may have.

**What is the difference between Conveyor and the standard Java tools like jlink and jpackage?**

The big difference is it makes self-updating packages, which is essential. There's no point in deploying an app you can't update. It's also much easier to use, config file driven, and takes care of more of the steps for you. For example, it'll make a download web page for you, notarize your Mac apps, compute delta updates, etc.

**What future do you see for JavaFX applications, and how do they compare to other frameworks?**

With distribution now mostly solved, JavaFX apps can become more competitive against Electron, especially with the multi-language support of Truffle and GraalVM. Interestingly, in the web framework space, we're starting to see people come back around and almost rediscover some of the ideas in JavaFX since the start, like per-property binding, constraint-based layout models, and so on.

Conclusion {#h2-4-conclusion}
-----------------------------

Of course, more people are involved and contributed to the [development](https://github.com/dlsc-software-consulting-gmbh/jfxcentral2) and [data project](https://github.com/dlsc-software-consulting-gmbh/jfxcentral-data)! If you find a bug, or have extra content to be added, create a ticket or pull request.

Interested in JavaFX, or already using it and looking for a new library, tool, book,...? Head over to [jfx-central.com](https://www.jfx-central.com/) or install the desktop application!
