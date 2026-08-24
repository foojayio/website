---
title: "Azul Enhances ReadyNow to Solve Java’s Warmup & Cloud Costs"
date: "2023-08-30T21:30:14+00:00"
lastmod: "2023-08-31T06:23:10+00:00"
description: "ReadyNow Orchestrator delivers the highest possible optimized code speed at warmup, making deployment easier for containerized Java."
canonical: "https://www.azul.com/newsroom/azul-enhances-readynow-to-solve-javas-warmup-problem-simplify-operations-and-optimize-cloud-costs/"
authors:
  - "john-ceccarelli"
image: "azul_logo_large_color.png"
categories:
  - "Java Core"
  - "Performance"
  - "Release Notes"
related_posts:
  - "5-things-you-probably-didnt-know-about-java-concurrency"
  - "6-considerations-when-building-high-performance-java-microservices-with-eda"
  - "7-functional-programming-techniques-in-java-a-primer"
frozen: false
---

* Azul Platform Prime's ReadyNow technology continuously learns from application usage across fleets of Java Virtual Machines (JVMs) and automatically selects the best warmup optimization patterns.
* ReadyNow Orchestrator delivers the highest possible optimized code speed at warmup while making deployment easier for containerized Java workloads and CI/CD pipelines, and requires no changes to Java applications.
* The combination of ReadyNow and ReadyNow Orchestrator allow Azul Platform Prime customers to optimize cloud costs by improving utilization levels, reducing compute instances needed while maintaining throughput and response SLAs, and more efficiently leverage cloud elasticity.

**SUNNYVALE, Calif. — August 30, 2023 —** [Azul](https://www.azul.com/), the only company 100% focused on Java, today announced ReadyNow Orchestrator(RNO), a new feature of Azul Platform Prime powered by ReadyNow technology that slashes warmup time for Java applications, enabling significant improvements to operational efficiencies and optimization of cloud costs. Available now, ReadyNow Orchestrator requires no changes to Java applications and is included at no additional charge for Azul Platform Prime customers.

Any company that uses Java for its business-critical workloads faces the Java warmup problem. When a Java application is launched, the JVM is responsible for compiling it into a form that can be executed by the machine or device running it. As the application continues to run, the JVM will recompile and further optimize important parts of the application's code to improve performance, essentially "warming up" over time before it reaches peak performance.
> **"Java's warmup problem has long been an issue in ensuring peak application performance. Organizations should consider ways to reduce operational friction by automating the selection of the best optimization patterns for container-based applications while also improving elasticity to control cloud costs,"**
> — William Fellows, Research Director at 451 Research

Azul Platform Prime's ReadyNow technology helps some of the world's largest companies ensure their Java workloads start fast and stay fast. ReadyNow records information about an application's optimization profile and then uses that profile to shorten the warmup time the next time the application is run. Generating and distributing ReadyNow optimization profiles across fleets of applications has become increasingly complicated – especially in containerized environments.

ReadyNow Orchestrator (RNO) automates profile distribution by delegating all profile collection to a dedicated, customer-managed service. Instead of collecting profile information on just a single JVM, RNO monitors entire fleets of JVMs, learns from application usage what the best optimization profile is, and then automatically serves the profile to any JVM that requests it so that Java applications warm up much more rapidly with no human intervention.

This newest feature of Azul Platform Prime builds on Azul's efforts to help businesses optimize rising cloud costs. DevOps teams can scale down the number of cloud compute instances they use to run Java applications during off-peak times and then rapidly scale them back up to meet demand – knowing that ReadyNow and ReadyNow Orchestrator will ensure those instances are warmed up immediately to take traffic with no disruption to customers. By doing so, organizations can reduce the average number of compute instances they are using, lowering their cloud compute costs.
> **"Azul has a distinguished history of delivering pioneering technology for Java applications, such as eliminating Garbage Collection pauses and offering the highest application performance with the world's best Just-in-Time compiler** . **With ReadyNow and ReadyNow Orchestrator, we're now solving Java's warmup challenges and providing a novel way for enterprises to improve developer and operational efficiencies and reduce cloud costs.**"
> — John Ceccarelli, Sr. Director Product Management for Azul Platform Prime

**Azul CTO and co-founder Gil Tene will join William Fellows of 451 Research for a free webinar on "[Cloud Cost Optimization for Java Workloads"](https://www.azul.com/opentalk/) at noon Eastern time on Tuesday, Sept. 26.**

To learn more about Azul Platform Prime and its unique capabilities of improving performance and carrying capacity for Java applications and optimizing cloud costs, visit [azul.com/products/prime](http://azul.com/products/prime).

**About Azul Systems Inc.**

Headquartered in Sunnyvale, California, Azul provides the Java platform for the modern cloud enterprise. Azul is the only company 100% focused on Java. Millions of Java developers, hundreds of millions of devices and the world's most highly regarded businesses trust Azul to power their applications with exceptional capabilities, performance, security, value and success. Azul customers include 27% of the Fortune 100, 50% of Forbes top-10 World's Most Valuable Brands, all 10 of the world's top-10 financial trading companies and leading brands like Avaya, Bazaarvoice, BMW, Credit Suisse, Deutsche Telekom, LG, Mastercard, Mizuho, Priceline, Salesforce, Software AG and Workday. Learn more at [azul.com](https://www.azul.com/) and follow us [@azulsystems](https://www.twitter.com/azulsystems).
