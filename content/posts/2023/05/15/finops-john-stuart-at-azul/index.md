---
title: "FinOps: John Stuart at Azul"
slug: "finops-john-stuart-at-azul"
date: "2023-05-15T06:11:11+00:00"
lastmod: "2023-05-22T14:04:36+00:00"
description: "\"Wouldn’t it be great to use a Java Virtual Machine (JVM) to be 30% more performant? That is a win for both Engineering and Finance.\""
authors:
  - "geertjan-wielenga"
image: "1672961010501.jpeg"
categories:
  - "FinOps"
  - "Research"
tags:
related_posts:
frozen: false
---

FinOps and Cloud Cost Management, what's it all about and how does it impact us as developers and others who are close to the code? In this series on Foojay.io, you're introduced to FinOps practitioners around the world, focused on how they have gradually found themselves, their technology and their organization in the FinOps space. {#h2-0-finops-and-cloud-cost-management-what-s-it-all-about-and-how-does-it-impact-us-as-developers-and-others-who-are-close-to-the-code-in-this-series-on-foojay-io-you-re-introduced-to-finops-practitioners-around-the-world-focused-on-how-they-have-gradually-found-themselves-their-technology-and-their-organization-in-the-finops-space}
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

*** ** * ** ***

[John Stuart](https://www.linkedin.com/in/johndstu/) is an executive and leader in DevOps, FinOps, Infrastructure and Security at Azul.

*** ** * ** ***

<br />

**Tell us about the work that you've done as a FinOps Foundation member.** {#h2-1-tell-us-about-the-work-that-you-ve-done-as-a-finops-foundation-member}
--------------------------------------------------------------------------------------------------------------------------------------------------------

*The FinOps Foundation is a terrific group of individuals collaborating to share best practices for managing cloud spend.*

*We helped develop a standard FinOps framework and maturity model.*

**How do you see cloud cost optimization challenges emerging and how are companies struggling with cost overruns?** {#h2-2-how-do-you-see-cloud-cost-optimization-challenges-emerging-and-how-are-companies-struggling-with-cost-overruns}
------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

*Cloud cost optimization is a challenge -- or will be soon -- for every company. As companies leverage the cloud, they benefit from engineering speed and innovation at the expense of financial control. Many companies that use AWS experience friction between finance and engineering. For example, finance questions why the AWS and Google costs are so high. [Just ask the folks at Snap](https://apple.news/A13Qnow9iO0GKrbDViK7ePA).*

*Engineering delivers the products and services customers are looking for; that is their role. Finance's role is to manage spend and ensure profitability, so naturally they question engineering when cloud spend increases.*

*Engineering then must defend itself or explain how a product matches to that spend. What's missing here is teams working together with a common understanding of cloud spending so engineering has visibility into the costs, and agreed upon goals. There's a statement I heard that rings true when addressing cloud spend -- finance looks for adjustments in months while engineering plans in quarters. This is the disconnect.*
> "The FinOps Foundation is a terrific group of individuals collaborating to share best practices for managing cloud spend. We helped develop a standard FinOps framework and maturity model."
> --- John Stuart, Azul

**How have you applied the FinOps framework at Azul?** {#h2-3-how-have-you-applied-the-finops-framework-at-azul}
----------------------------------------------------------------------------------------------------------------

*It's always a journey with the FinOps framework. In FinOps it's crawl, walk, and run. We are in the crawl and walk phases. It started with informing engineering leads on their spend. Teams are given full transparency on the spend, allowing them to optimize workloads.*

*At Azul, we consume large amounts of compute. The teams updated workloads to use spot instances and different instance types at reduced rates. We are also improving financials (savings plans, reserved instances and negotiating discounts) to enable profitability and fund additional initiatives.*

**Where do you see Azul's Cloud Native Compiler (CNC) helping?** {#h2-4-where-do-you-see-azul-s-cloud-native-compiler-cnc-helping}
----------------------------------------------------------------------------------------------------------------------------------

*As some teams progress into the run phase, understanding every cost component and its impact on the bottom line is critical. The basics are done with teams engaging both Engineering and Finance on the unit economics, and this is where [CNC](https://www.azul.com/products/intelligence-cloud/cloud-native-compiler/) and [Azul Platform Prime](https://www.azul.com/products/prime/) bring significant benefit.*

*Wouldn't it be great to use a Java Virtual Machine (JVM) to be 30% more performant? That is a win for both Engineering and Finance. Prime delivers higher throughput with less compute and better response times, delivering a better end user experience.*

*The elasticity of the cloud enables organizations to get huge compute power when they need it, but to scale down when they don't so they only pay for what they use. CNC pulls the Just In Time (JIT) compiler out of each JVM and makes it a shared service. This leads to better efficiency in delivering compilations, since CNC can serve compilations from a cache instead of recompiling for every JVM. It also makes it smoother to scale out elastically to meet demand or perform frequent CI/CD fleet redeploys.*

*Where CNC provides real value is decoupling the level of compilations delivered from the CPU capacity of the JVM instance. When local CPU resources need to be shared between JIT compilation and application execution, you have to reserve capacity for compilation activity so a sudden deoptimization storm doesn't cause you to miss your performance Service Level Agreements (SLAs).*

*Being over-provisioned like that is incredibly expensive. Offloading that work to CNC allows you to do things like setting higher CPU utilization limits for horizontal scaling. And because the JIT optimizations delivered by CNC are more powerful, that means you can get more traffic through each JVM and need to provision less instances overall to meet your demand.*
> "Wouldn't it be great to use a Java Virtual Machine (JVM) to be 30% more performant? That is a win for both Engineering and Finance. Azul Platform Prime delivers higher throughput with less compute and better response times, delivering a better end user experience."
> --- John Stuart, Azul

**What is your best advice on the most important KPIs and metrics that companies need to measure to control costs? Can you give examples** {#h2-5-what-is-your-best-advice-on-the-most-important-kpis-and-metrics-that-companies-need-to-measure-to-control-costs-can-you-give-examples}
----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

*My counsel would be based off the best practices I see from our Azul customers:*

1. Code with performance in mind within SLAs for customer experience.
2. Invest in performance software that creates business value and saves compute resources.
3. Establish continuous integrations/continuous delivery programs around improving software quality using an optimized Java Virtual Machine that requires NO CODE CHANGES to implement.

*For a deeper look on how Java can help you optimize cloud costs and improve performance. [A cloud cost optimization strategy can save millions of dollars and improve your company's valuation](https://www.azul.com/blog/cloud-cost-optimization-strategies-will-affect-valuations/).*

*Get started today by checking out [how Cloud Native Compilation](https://www.azul.com/products/intelligence-cloud/cloud-native-compiler/) works.*

*Together with our global support, Azul offers an optimized JVM to improve performance for your business-critical applications. [Take it for a test run](https://www.azul.com/products/prime/stream-download/).*
