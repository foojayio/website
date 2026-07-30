---
title: "Azul’s High-Performance Java Platform Achieves Historic First"
slug: "azuls-high-performance-java-platform-achieves-historic-first-with-10000-customer-jvms-collaborating-and-sharing-performance-optimizations-cutting-cloud-costs-by-20"
date: "2025-09-17T16:49:56+00:00"
lastmod: "2025-09-17T16:56:59+00:00"
description: "Fleet-level advantages achieved for cloud-native Java applications with faster warm-up, smoother scaling and reduced cost."
canonical: "https://www.azul.com/newsroom/azuls-high-performance-java-platform-achieves-historic-first-with-10000-customer-jvms-collaborating-and-sharing-performance-optimizations-cutting-cloud-costs-by-20/"
authors:
  - "john-ceccarelli"
image: "/images/posts/2025/09/azuls-high-performance-java-platform-achieves-historic-first-with-10000-customer-jvms-collaborating-and-sharing-performance-optimizations-cutting-cloud-costs-by-20/Favicon-3-2.png"
categories:
  - "Performance"
  - "Release Notes"
tags:
related_posts:
  - "analyzing-and-tuning-warm-up-of-the-jvm-with-azul-zulu-prime-builds-of-openjdk"
  - "apple-silicon-with-zulu-openjdk-and-intellij-idea"
  - "are-java-security-updates-important"
  - "azul-and-jetbrains-collaborate-to-enhance-runtime-performance-for-kotlin-workloads"
frozen: false
---

**Azul Platform Prime Achieves Historic First with 10,000+ JVMs Collaborating and Sharing Performance Optimizations, Cutting Cloud Costs by More than 20%**

***Fleet-level advantages achieved for cloud-native Java applications with faster warm-up, smoother scaling and reduced cost***

**SUNNYVALE, Calif. --- September 17, 2025 ---** [Azul](https://www.azul.com/), the only company 100% focused on Java, today announced a breakthrough in cloud deployment at scale with [Azul Platform Prime](https://www.azul.com/products/prime/), its high-performance Java platform. A leading global enterprise has deployed hundreds of applications and micro-services across more than 10,000 Java Virtual Machines (JVMs) using Platform Prime's Optimizer Hub, a unique capability that allows JVMs to collaborate and share performance optimizations.

By enabling this fleet-wide intelligence, the customer's applications -- spanning e-commerce to payments to inventory management -- start and warm up dramatically faster, scaling has become smoother and compute requirements have significantly reduced, delivering cloud cost savings of more than 20%.

Another well-known company in the entertainment industry recently deployed Optimizer Hub to reduce CPU core and pod counts for their Java-based critical services by 25%-30%. Optimizer Hub is included with Platform Prime as an optional, customer-managed service that requires no changes to existing Java applications or JVM-based workloads.

**Turning the Limits of Traditional JVMs into Superior User Experiences and Measurable Cost Savings**

Today's cloud-native Java applications and JVM-based workloads, often comprising fleets of thousands of compute instances and containers, face inherent challenges in achieving a stringent set of requirements, including:

* Delivering smooth customer experiences where even the slightest unexpected latency or performance deficiency will fall short of service level expectations.
* Leveraging cloud elasticity to autoscale, bringing new capacity online at full performance to efficiently match peaks in demand and optimize carrying capacity.
* Efficiently restarting large fleets of container-based microservices as applications evolve with new features and capabilities.

Traditional JVMs optimize in isolation and often deliver lower overall code performance and exhibit spiky, unpredictable behavior. To avoid poor customer experience and meet service level expectations, enterprises are forced to over-provision their cloud resources, resulting in spiraling cloud costs. In the [2025 State of Java Survey \& Report](https://www.azul.com/state-of-java-2025/), 71% of survey respondents say that more than 20% of their cloud compute capacity is unused.

By enabling JVMs across an enterprise fleet to collaborate by sending and receiving optimizations and learnings using Optimizer Hub's centralized services, Platform Prime achieves levels of cloud scale, elasticity, resiliency and cost efficiencies that are unparalleled.

**Azul Platform Prime with Optimizer Hub**

Azul Platform Prime is a high-performance Java platform that includes Azul Zing, an enhanced build of OpenJDK, that is the world's fastest, most scalable and most resilient Java runtime. Optimizer Hub is an additional component of Azul Platform Prime, designed to further improve cloud-centric Java application startup, warm-up and runtime performance by offloading optimization tasks from the individual Zing JVMs.

Optimizer Hub is ideal for modern applications running in containerized, elastic cloud environments with contemporary DevOps practices. It comprises two services that run in a customer's environment:

**Cloud Native Compiler** provides centralized JIT compilation and cachingto deliver cost savings and efficiency. Cloud Native Compiler shifts the heavy work of JIT compilation from individual JVMs to a centralized, scalable service, slashing CPU workloads on each JVM and caching compilation for reuse. The resulting code runs faster, handles more traffic with less CPU, and realizes full speed more quickly and smoothly. By eliminating the overhead that comes when each JVM consumes its own CPU resources to compile the Java workload in isolation, each JVM can be deployed in smaller instances and containers, reducing cloud costs.

**ReadyNow** is a feature in Platform Prime that addresses Java's warm-up problem by logging and reusing JIT compiler profiling and optimization data between JVM runs. **ReadyNow Orchestrator** delivers intelligent curation of the ReadyNow optimization profiles to ensure each application and microservice is using the best profile and simplifies operational use of ReadyNow. Whether during retail rushes, gaming traffic spikes, or financial market opens, ReadyNow Orchestrator ensures consistent responsiveness, fewer errors and higher SLA attainment from the first request onward. It also enables faster CI/CD redeployments for smoother fleet rollouts, higher resilience under heavy workloads and faster recovery from infrastructure failures, streamlining DevOps at scale.

**"Java powers the backbone of the digital economy, but performance challenges with traditional JDKs have led enterprises to overprovision cloud resources and adopt complex operational practices," said [Scott Sellers](https://www.azul.com/leadership/scott-sellers/), co-founder and CEO at Azul.**

**"With the centralized services of Optimizer Hub, Azul Platform Prime has eliminated long-standing tradeoffs of performance and cost by enabling JVMs to learn from each other and collaborate across entire fleets in production, delivering faster, smoother application experiences while driving down cloud costs by 20%+. Optimizer Hub is a must-have for every business running JVM-based, mission-critical workloads in the cloud."**

For more information:

* [Azul Platform Prime](https://www.azul.com/products/prime/)
* [High-Performance Java Platform](https://www.azul.com/products/prime/high-performance-java-platform/)
* [Azul Optimizer Hub](https://www.azul.com/products/components/azul-optimizer-hub/)

**About Azul Systems Inc.**

Headquartered in Sunnyvale, California, Azul provides the Java platform for the modern cloud enterprise. Azul is the only company 100% focused on Java. Millions of Java developers, hundreds of millions of devices and the world's most highly regarded businesses trust Azul to power their applications with exceptional capabilities, performance, security, value, and success.

Azul customers include 36% of the Fortune 100, 50% of Forbes top 10 World's Most Valuable Brands,10 of the world's top 10 banks and leading brands like Avaya, Bazaarvoice, BMW, Deutsche Telekom, LG, Mastercard, Mizuho, Priceline, Salesforce, Software AG, and Workday. Learn more at [azul.com](https://www.azul.com/) and follow us [@azulsystems](https://www.twitter.com/azulsystems).
