---
title: "Identify Dependencies in Codebases During Microservice Migration"
slug: "how-to-identify-dependencies-in-your-codebase-during-microservices-migration"
date: "2024-06-07T07:17:58+00:00"
lastmod: "2024-06-07T13:00:12+00:00"
description: "Migration from a monolithic architecture to microservices presents challenges, particularly in identifying and managing dependencies within the codebase."
authors:
  - "oleksandr-hrebeniuk"
image: "Digmo-and-teelscope.jpeg"
categories:
  - "Microservices"
  - "Tools"
tags:
related_posts:
  - "beyond-pass-fail-a-modern-approach-to-java-integration-testing"
  - "couch-to-fully-observed-code-with-spring-boot-3-2-micrometer-tracing-and-digma"
  - "how-to-detect-cache-misses-using-observability"
  - "microservices-design-principles-for-well-crafted-architecture"
frozen: false
---

> Migration from a monolithic architecture to microservices presents challenges, particularly in identifying and managing dependencies within the codebase. By analyzing the application's training data, we can uncover the seams and threads that bind the application together, discovering a safer, more iterative path to refactor our code.

**Table of Contents**

1. Collecting tracing data to understand code dependencies
2. Using Digma Analytic Insights to identify dependencies in your codebase:
3. Code Nexus Point Insight
4. Top Usage Insight
5. Discovering Dead Code
6. Duration Breakdown Insight
7. Conclusion: Identifying Dependencies in Your Codebase During Microservices Migration

Migration from a monolithic architecture to microservices can be crucial for scalability, flexibility, and development velocity. However, this transformation has challenges, particularly when trying to identify and manage dependencies within the codebase. Many teams optimistically embark on an application-wide modernization epic, thinking of making short work of refactoring their codebase to be more modular. Unfortunately, many of these initiatives quickly lose the initial momentum, as it becomes apparent that untangling the Gordian monolith knot is proving to be an uphill struggle.

The aim of this article is not to dissuade or scare you away from refactoring your code: quite the opposite. Many of these efforts to transform the system architecture get stuck because the efforts are poorly planned or because the complex system architecture is not well understood. Thankfully, observability, a technology often overlooked by developers, can help shine a light through the most dense of code bases. By analyzing the application training data, we can uncover the seams and threads binding the application together and discover a safer, more iterative path to refactor our code.

One of the benefits of Java is that observability is almost free. Significantly, no code changes are necessary and with the right toolings, you can activate advanced tracing almost immediately. Let's review how this is done:

Collecting tracing data to understand code dependencies
-------------------------------------------------------

In this example, we'll be using a developer tool called [`Digma`](https://digma.ai/how-it-works/ "<code>Digma</code>") to collect and analyze the tracing data. Digma is an IntelliJ plugin and uses OpenTelemetry behind the scenes to collect data about the code. To get started, all you have to do is install Digma from the [IntelliJ marketplace](https://plugins.jetbrains.com/plugin/19470-digma-continuous-feedback "IntelliJ marketplace") and run your code. Even locally you can gather a log of data just by activating your code or running tests. You can get even more comprehensive data by connecting Digma with a CI, Staging, or Production environment.

![](https://lh7-us.googleusercontent.com/fJ_EgVbOzFRsZz4-SbVbv5Bs11h8J0H0yYmpYaQXZE3VksUuRmgf560nIQVKIARAQOLYqlNHPxVrXnQpS3vjVm77HJM0-_iupVpAsPKAl2Jwr0Tf7Hp9gjp2Nmi3dQLSXt8TWZSTtKGiycjwTGhLFuw)  

Digma Continuous Feedback plugin

Using Digma Analytic Insights to identify dependencies in your codebase:
------------------------------------------------------------------------

As you run your code Digma will start analyzing the tracing data to discover issues, but also come up with useful analytics. There are many types of analytics that Digma can glean but in the context of this post, I'll review four types of insights that can be extremely useful during your codebase analysis before the migration.

1. Code Nexus Point
2. Top Usage insight
3. Dead code
4. Duration Breakdown Insight

Learn more: [Here](https://docs.digma.ai/digma-developer-guide "Here")

Code Nexus Point Insight
------------------------

This insight highlights areas in the code with high levels of runtime dependencies. By identifying these nexus points, developers gain insight into the areas that require meticulous attention and thoughtful refactoring. This insight can help prevent a dependency hell as you refactor your code and better select candidates for extraction into modular components.

![](https://lh7-us.googleusercontent.com/QYJ6kyOdBsmta3ebp1DKVNcXr_6guP6w3N2KweGV4B4RnB6t2eEUAthwazjw59Dnh0E5JOsrmMbhTVbDE9iJCWePLF4vSOqw_2TddZdmpYaIwN8zOoojXaalgl_oLkZ1gE2slg60FBrpOVOotFvQBXQ)

Code Nexus Point Insight

In the example above, we can see that the "Services" property reveals that only one service uses the current method. Based on the intended architecture, we can make that method part of a microservice that uses it. If more than one service uses that method, consider moving that service to a shared module. Look at the image below, which shows three service references.

![](https://digma.ai/wp-content/uploads/2024/04/image-7.png)

Top Usage Insight
-----------------

With this insight you see more specific information about how the code is used, we can check the 'Top Usage' insight. This can definitely come in handy when dealing with dependencies during a microservices migration. The insight reveals how the code is being used within our application and allows us to navigate those usages without debugging each individual one.

By using this insight, you can see all the places that call your method and their usage rate. Use the traces button to investigate traces in more detail.

![](https://lh7-us.googleusercontent.com/EF2TaoZaMI33ojX7n3cBINYbzRLc3T7XAvG9RkVbNeENGXoLztoC5PEf9SNvuUrFrT92ZsZLtQXTRYnKAGWJz83uArDDoScaI3QxziOInhnh8YRjdjGBYGr5-MW85MOm7U9BTmwIwCYSKWo37WoVjbo)  

Top Usage Insight

Discovering Dead Code
---------------------

The above insights allow us to reveal important information pieces about how the code is being used -- but what if we're interested in finding code that isn't used at all?

Digma provides annotations over such areas in the IDE editor to draw your attention to the fact that they are not reached by any flow in the application:

![](https://lh7-us.googleusercontent.com/WQX3wvdWrU4S-9ZSwSKeHD1098x5J4klfm6gNnA8gve4YSj_EqEtUtpVcR5HPV3OnNPgxwMY0UvRvY5hbHzTxsbae3iuJoxfRuGZJEuDyMmBF5-yMPohCuc3fbhWp5fxFGB0c8M7XRSUK-D0mEsaNRQ)

Duration Breakdown Insight
--------------------------

Lastly, for each area in our application, Digma will reveal the Duration Breakdown insight. The value of this information is two-fold. First, it allows us to see not only who uses this code (which we saw in the previous insights) but also which other components this code is calling out to. Next, this insight provides important performance baselines that we can use to detect performance issues before the migration resulting in slow invocations so the migration will go smoother.

Understanding the performance of methods is crucial to microservices migration and will save you a lot of time and frustration. Most likely, it was performance problems that led you to the decision to switch to microservices.

![](https://digma.ai/wp-content/uploads/2024/04/image-6-768x357.png)  

Duration Breakdown Insight

Conclusion: Identifying Dependencies During Microservices Migration  

Observability can be the secret sauce to succeeding in large-scale refactoring operations. Complex systems can be unwieldy, closely coupled tangled, and messy. Any tool that can help provide insights, data, and analytics about the code runtime behavior can be invaluable to selecting candidates for refactoring and assessing the scope of change. Combining these four insights as well as other analytics, can provide a better understanding of method behavior and help reach better results in refactoring complex code bases.

Let me know how your refactoring efforts panned out and whether this type of information helped you reach your architecture change goals! [Ping me for any questions.](https://continuous-feedback.slack.com/ssb/redirect "Ping me for any questions.")
