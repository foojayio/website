---
title: "Why Did We Choose JSF for the Eclipse Starter for Jakarta EE?"
slug: "why-did-we-choose-jakarta-faces-for-the-ui-of-the-eclipse-starter-for-jakarta-ee"
date: "2022-12-13T10:27:44+00:00"
lastmod: "2022-12-14T16:53:18+00:00"
description: "This article explains why we chose JSF to build the Eclipse starter for Jakarta and why it deserves more love and attention!"
authors:
  - "bazlur-rahman"
image: "https://foojay.io/wp-content/uploads/2022/12/Screen-Shot-2022-12-12-at-10.49.06-AM.png"
categories:
  - "Java Core"
tags:
related_posts:
  - "5-more-reasons-to-choose-apache-pulsar-over-apache-kafka"
  - "5-things-you-probably-didnt-know-about-java-concurrency"
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
  - "transitioning-to-java-my-first-book"
frozen: false
---

After being taken care of by the [Java Community Process](https://www.jcp.org/en/home/index) (JCP), Java EE is now supported by the [Eclipse Foundation](https://www.eclipse.org/org/foundation/) as [Jakarta EE](https://jakarta.ee/).

This means that we can focus more on open standards for governance, open-source development, and testing for compatibility.

It is also a great way to include openness to all vendors, a clear flow of intellectual property (IP), well-defined procedures for defining specifications, and a level playing field.

Eclipse Starter for Jakarta EE {#h2-0-eclipse-starter-for-jakarta-ee}
---------------------------------------------------------------------

To ensure there are more users and participants and that they have an easy path to Jakarta EE, the Eclipse Foundation has started a new project called Eclipse Starter for Jakarta EE as a new open-source initiative.

The project can be found here:<https://start.jakarta.ee/>.

The issue is that it assumes the developer is already familiar with the Maven archetype.

However, in today's attention economy, making it more palatable and attractive to the developer is much more crucial, mainly if they are new.

It would be helpful if there were one official place where everyone could go to configure what they need, get the project structure as a ZIP file they could download, and start playing with it for Jakarta EE.

User Interface for Eclipse Starter {#h2-1-user-interface-for-eclipse-starter}
-----------------------------------------------------------------------------

With that requirement in mind, we have started creating a UI project that is slightly better than what we already have.

First, we must build a web application that takes user input through a form. Then, clicking the Submit button gives the application output as a downloadable ZIP file.

The backend of the application, which is where most of the work is done, is written in Java. The form is the only client-side coding part involved in the project. We don't have any fancy requirements.

The backend part of the application is already written with Java and a Servlet. As I previously stated, the requirement is relatively simple.

Once we build the application through Maven, it makes a WAR file. We can throw the WAR at any web server, such as Tomcat, JBoss, Wildfly, etc. Fortunately, we have a JBoss EAP instance from the Azure App Service that we can use for this application. Thanks to Azure for sponsoring it!

Improving the UI for the Eclipse Starter {#h2-2-improving-the-ui-for-the-eclipse-starter}
-----------------------------------------------------------------------------------------

Then we decided to improve the UI. The simple HTML form, in fact, works. But we need validation as well. We have some drop-down input fields that will change in the future. We have new options as well. Plain HTML implies having them in two places: the backend and the frontend. Duplication is wrong, we learned.

For validation, client-side validation works, but it's easy to bypass. So we want to do server-side validation as well.

There are a lot of JavaScript frameworks that can be used to make fancy user interfaces on the client side. Choosing one of them isn't easy.

Besides, most of the time, we all work on the backend, and the programming language we're good at is Java. Choosing React over Angular or Vue.js isn't as straightforward as choosing Spring over other frameworks. Being familiar with an ecosystem is essential when deciding on a technology.

Jakarta Faces Wins {#h2-3-jakarta-faces-wins}
---------------------------------------------

After consulting with the current contributors, we decided to go with [**Jakarta Faces**](https://projects.eclipse.org/projects/ee4j.starter) (previously called JavaServer Faces, or JSF).

We looked at the learning curve and the project's complexity when evaluating JSF, and we concluded that the JSF makes a lot of sense for this project. Here are a few reasons why we chose JSF over any other client-side framework:

* It is a **component-based framework**, so that developers can easily reuse components and build user interfaces faster.
* It has **powerful APIs** for handling user input, the state of components, and other common tasks for web applications.
* It **works well with other Java technologies** like EJBs and JSPs.
* It is an **open source** framework, which means it is freely available and can be used without paying license fees.

Aside from these, many well-known people in the Jakarta EE community agree with this decision.

[Reza Rahman](https://www.linkedin.com/in/javareza/) says about it:
> "I definitely vote for Faces. As Bazlur mentions, I do suspect it will ultimately result in simpler code that is easier to maintain. For a project designed to promote Jakarta usage, it also sends the correct message - we could even make Faces use apparent in the URL or an explicit mention. Lastly, I believe that for this project, it's going to be easier to get people more proficient in Java than JavaScript. Indeed, I suspect that's already true of the current committers and contributors.

[Kito Mann](https://www.linkedin.com/in/kitomann/) mentioned in the mailing list that-
> "+1 for JSF. Much faster to build with and, as Reza mentioned, familiar to developers on this team. Also, this is a relatively simple app that doesn't have any particularly complicated UI needs. Great use case."

<br />

In fact, after working with JSF, I can conclude that JSF is a simpler, richer, and more mature framework that goes well with Java.

Of course, the learning curve is steep for newbies, which is valid for any framework. But being familiar with the Java ecosystem makes working with JSF much easier to start with.

Besides, it has great support from the Jakarta EE community and[standards](https://jakarta.ee/specifications/faces/4.0/). And if you need to, you can easily add custom JavaScript code or any standard web technology, like Web Components.

I tweeted passionately about JSF and many others commented on it. You can read the whole thread here:
> I wonder why many people don't use JSF. It's a remarkable technology.
>
> We can build amazing things with it without getting into the bottomless pit of sadness (JavaScript) 😛
>
> --- A N M Bazlur Rahman 🇧🇩🇨🇦 (@bazlur_rahman) [November 28, 2022](https://twitter.com/bazlur_rahman/status/1597131339375185920?ref_src=twsrc%5Etfw)

<br />

While JSF has many good sides, it has some disadvantages as well.

* It can have **a steep learning curve** for developers who are new to the framework.
* There may be better choices for applications that require a high degree of **customizability** or **flexibility**.

Overall, whether or not JSF is a good choice for a particular project depends on the project's specific requirements and goals, as well as the experience and preferences of the development team.
> However, we must agree that JSF is a full-fledged, mature framework. It can fit any project. Indeed, no tool is a silver bullet that suits all cases. Still, JSF deserves more love and attention than it gets now compared to other UI frameworks.

This article explains why we chose JSF to build the Eclipse starter for Jakarta EE.

**Conclusion:** {#h2-4-conclusion}
----------------------------------

***The UI with JSF is still being worked on, so it might take a while to be accessible*** . But you can get access to the source code now: <https://github.com/eclipse-ee4j/starter/tree/master/starter-ui.>

Meanwhile, you can access the live starter application at <https://start.jakarta.ee/> and use it to get started with Jakarta EE and generate sample Jakarta EE applications.

If you would like to contribute to the Eclipse Starter for the Jakarta EE project, join us:

* Join the making list: https://projects.eclipse.org/projects/ee4j.starter/contact
* Clone projects from<https://github.com/eclipse-ee4j/starter>
* Create an account for Eclipse<https://accounts.eclipse.org/>
* Accept the Contributor Agreements

We have plenty of ideas now, and if you want to join the discussion, feel free to join us. Your thoughts would be highly appreciated. Prior knowledge of JSF or Jakarta EE isn't required to be a contributor.

Last but not least, I'd like to thank [Ondro Mihályi](https://twitter.com/OndroMih) and [Reza Rahman](https://twitter.com/reza_rahman) for reviewing the article and giving valuable feedback.
