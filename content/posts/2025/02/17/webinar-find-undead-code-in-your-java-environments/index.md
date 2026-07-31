---
title: "Find Undead Code in Your Java Environments"
slug: "webinar-find-undead-code-in-your-java-environments"
date: "2025-02-17T09:36:55+00:00"
lastmod: "2025-05-15T12:21:53+00:00"
description: "10-30% of the custom code in applications is undead code and can just be deleted. Eliminating undead code can enhance the overall performance and - by Erik Costlow"
canonical: "https://www.azul.com/blog/find-undead-code-in-your-java-environments/"
authors:
  - "erikcostlow"
  - "trisha-gee"
image: "Undead-Code-Time-Vampire-Ad-1-768x432-1.png"
categories:
  - "Developer Tools"
  - "Events"
tags:
related_posts:
frozen: false
---

**10-30% of the custom code in applications is undead code and can just be deleted. Eliminating undead code can enhance the overall performance and maintainability of your applications. Time vampire (n) -- a service or application that is said to suck time from engineers by warming up and updating at preternaturally slow speeds.**

You're on deadline to finish a project, and you open the application to do your work. The application informs you that it needs to update before it will open. Based on past experience, you have half an hour to burn before this time vampire is ready.

*Bad code! You don't have time for you to update right now.*

*Good code! You get to go play foosball* *in the breakroom for 20 minutes.*

*Bad code! You're being measured on your project, not your foosball game.*

The guilty pleasure of foosball or ping pong during work hours is also a productivity killer.

Put a stake through the heart of undead code {#h-put-a-stake-through-the-heart-of-undead-code}
----------------------------------------------------------------------------------------------

What's the problem? A time vampire is a thing that sucks your time, not your blood, because a lot of Java applications have just gotten too big. In Azul's research and conversations with customers, about 10-30% of the custom code in applications is undead code and can just be deleted. Multiple people have been in the organization over several years, that code has changed, and new people either are afraid to delete code or don't have time to investigate.

Over time, they make changes. Maybe they changed a library. Now they need to upgrade from Java 11 to 21. New people join the organization and then leave. Over time, unless they're very diligent, code grows and they have to maintain it, but they don't get any benefit from it. They might have some old XML processing code, but they don't use that XML anymore. They have to figure out how those APIs have changed and what they have to do to make upgrading possible. If they can reduce the amount of code that doesn't run in production, they create less work for everyone.

Besides, deleting unused code is incredibly satisfying.

Java is a victim of its own success. It's backward compatible. It has a lot of large, old applications that have been used by enterprises for a long time. The longer an application has been running, even successfully, the more likely it is to have code you just don't need anymore.

Old, unused code makes it harder to add new features because developers have to figure out what it does, manipulate it, check dependencies, and make changes. Unless... what if they didn't need that code at all? Or even if they could remove half the code, they would make maintenance so much easier going forward.

To learn more, check out the on-demand webinar, [Your Undead Code Is a Time Vampire](https://www.azul.com/webinar/your-undead-code-is-a-time-vampire/), featuring Erik Costlow, Azul senior director of Product Management -- Cloud Solutions and Trisha Gee, software engineer, Java Champion, and author.

In this webinar, you will learn how to:

* **Identify Hidden Unused and Dead Code:** Learn how to monitor JVMs across different environments to detect unused classes, methods, and modules.
* **Optimize Development Efficiency:** Discover techniques to remove unnecessary code, making your development process smoother and faster.
* **Improve Application Performance:** Understand how eliminating unused and dead code can enhance the overall performance and maintainability of your applications.

[Click here to watch the on-demand webinar!](https://www.azul.com/webinar/your-undead-code-is-a-time-vampire/)

Azul Code Inventory can help remove undead code {#h-azul-code-inventory-can-help-remove-undead-code}
----------------------------------------------------------------------------------------------------

[Azul Code Inventory](https://www.azul.com/products/components/code-inventory/)®, a service of [Azul Intelligence Cloud](https://www.azul.com/products/intelligence-cloud), is the only solution that precisely catalogs what code runs in production across all of an enterprise's Java workloads. It slashes the time and burden of maintaining and testing unused code, significantly improving developer productivity and saving money. Code Inventory collects and aggregates detailed information from the Java Virtual Machine (JVM) of what code actually runs in production over time. It provides highly accurate, strong signals to confidently identify and prioritize unused code for removal. It helps reduce clutter so teams can work only on active code, lowering maintenance effort and increasing development velocity.

In December, Azul Intelligence Cloud won InfoWorld's Technology of the Year Award for analytics solutions. InfoWorld judges said, in part, "Azul Intelligence Cloud is ... like a smart assistant that watches how your Java programs work and suggests ways to improve them. It's a new and exciting way to optimize Java programs, using real-time data to make smart decisions."
![LOGO: InfoWorld Technology of the Year 2024 Award - Azul Intelligence Cloud](https://www.azul.com/wp-content/uploads/InfoWorld-TOTY-IC-2024.png)

[Click here to watch the on-demand webinar!](https://www.azul.com/webinar/your-undead-code-is-a-time-vampire/)

Gradle Devlocity can help automate tasks to improve productivity {#h-gradle-devlocity-can-help-automate-tasks-to-improve-productivity-nbsp}
-------------------------------------------------------------------------------------------------------------------------------------------

Devlocity is Gradle's developer productivity tool. It allows developers to see the results of all their local builds. They can see when a build has failed and when it's passed all its tests. It can show all the dependencies that were used in a build, which dependencies are needed, and which versions are different. It can even show the versions of libraries, like Log 4j, that are being used.

Teams can combine it with a service like Code Inventory to decide if they even need the applications and components.

[Click here to watch the on-demand webinar!](https://www.azul.com/webinar/your-undead-code-is-a-time-vampire/)
