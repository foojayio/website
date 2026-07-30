---
title: "SpringOne TLV World Tour Trip Report"
slug: "springone-tlv-world-tour-trip-report"
date: "2022-12-09T22:00:00+00:00"
lastmod: "2022-12-12T08:44:18+00:00"
description: "Got a chance to meet & talk to many interesting members of the Spring team and hear about updates to Spring 6 & Spring Boot 3 in my home city."
canonical: "https://debugagent.com/springone-tlv-world-tour-trip-report"
authors:
  - "shai-almog"
image: "/images/posts/2022/12/springone-tlv-world-tour-trip-report/cover-image.png"
categories:
  - "Cloud"
  - "Spring"
  - "Trip Reports"
tags:
related_posts:
  - "internal-security-hardening-internal-systems"
  - "api-mocking-essential-and-redundant"
  - "observability-is-cultural"
frozen: false
---

My talk was accepted by SpringOne in San Francisco. I never went to that conference and was really looking forward to it. This year would probably be amazing with the Spring 6.0 and Spring Boot 3.0 releases. So many groundbreaking changes. Unfortunately, my travel budget at Lightrun was cut, and I eventually left. This meant I had to cancel the trip, I would have paid for travel, but San Francisco is both far and prohibitively expensive. I'll have to take a raincheck.

This isn't exactly a substitute, but a skeleton Spring Team is on a "world tour" which reached Tel Aviv last week and I attended. The venue is one I've never attended, the Peres center for innovation. It's on the Jaffa beach and you can literally see the waves hitting the beach from the main stage. Check out the photos below, you can literally see people on the beach in some of them. That's a fantastic venue. The only downside is the distance from the center of Tel Aviv where I live. But that's nothing compared to distances in the states...

The event had 600+ registrations from over 80 organizations. This is the first Spring Tour outside of North America which is cool. The talks were in English which I like, but the Israeli accent is sometimes difficult to understand (yes I know). Unfortunately, I had to leave early because I had to pick up the kids.

Before we proceed I want to say a couple of things about Twitter. Over the past month I [moved to Mastodon](https://mastodon.social/@debugagent) and it's pretty great. There's even the [Java Bubble](https://javabubble.org/) which lists prominent community members on Mastodon. Foojay [added its own instance](https://foojay.io/today/foojay-mastodon-service-here-it-is/) and interest is high.

I see no reason to stay on Twitter. It's toxic and getting more-so by the second. I still have my account and I have [a bridge that tweets my Mastodon posts](https://moa.party/). So if you follow me there you won't miss anything. But I won't go there as often and will try not to post there. All the links in this post and future posts will refer to other networks whether LinkedIn or Mastodon.

Juergen Hoeller -- Introducing Spring Framework 6 {#h2-0-juergen-hoeller-introducing-spring-framework-6}
--------------------------------------------------------------------------------------------------------

![Juergen Hoeller](/images/posts/2022/12/springone-tlv-world-tour-trip-report/1-jurgen-700x320.jpg)

Spring Framework 6 is a re-alignment of the framework. [Juergen](https://www.linkedin.com/in/juergenhoeller/) picked his works carefully, focusing on the conservative nature of Spring versions and compatibility to past versions. Spring Framework 5.3.x is very much JDK 8 based. It's coupled to Java EE and the old javax namespace. It's supported in open source until 2024 which means we need to make migration plans...

With Spring Framework 6.x changes a few things:

* JDK 17+
* Jakarta EE instead of Java EE and javax namespace
* AOT Support
* Virtual Threads (Loom)

This will be the basis to Spring Boot 3.x. The biggest challenge I foresee is the JDK 17 requirement. That might challenge some migrations. As we make those migrations, we'll probably containerize more. Ideally, that will make future migrations much easier. JDK 17 has some fantastic features though. Juergen focused a lot on the language features but there are some great GC improvements for containers which make the upgrade worthwhile in the enterprise.

Migrating to the Jakarta API is a necessary evil since the packages for the old APIs are no longer updated. This is going to be a migration pain, but it's unavoidable. This will require anyone who has a dependency on those APIs to move that code. The entire Java ecosystem switched to fast release cycles and updates to frameworks like Tomcat happen at a much faster rate. Juergen recommends skipping Jakarta EE 9 and going directly to 10.

Spring Framework 6.1 will still support JDK 17, but it will also add support for JDK 21 which should be available by then. Jakarta EE 10 will be the preferred framework for that version.

AOT is a tradeoff: extra build setup and less flexibility at runtime. Reduces startup time and memory footprint in production. GraalVM is the de-facto standard for native executables. It has a strong closed-world assumption, no runtime adaptations. Build time is very long. Personally I don't think it's that long as a guy who built an AOT JVM.

OpenJDK static images (Project Leyden). Spring's AOT strategy is aligning with Leyden's JVM strategy. Starting from a warmed-up JVM image ([CRaC](https://foojay.io/today/introducing-the-openjdk-coordinated-restore-at-checkpoint-project/)) is also a promising option for fast Spring application bootstrap. First class support for CRaC is expected in Spring Framework 6.1.

Project Loom is a preview feature in JDK 19 that enables high throughput for Java threads. They expect significant scalability benefit for database-driven web apps. It's also a perfect fit for messaging and scheduling applications. This means WebFlux and reactive style is no longer primarily for scalability as Loom would provide that for "free".

![Spring Framework Support Timeline](/images/posts/2022/12/springone-tlv-world-tour-trip-report/2-support-timeline-700x438.jpg)

Other features include fast bean property determination. Complete CGLIB fork with support for capturing generated classes. NIO based classpath scanning. First-class module scanning and further module system support (possibly) in 6.1. HTTP interface clients based on `@HttpExchange` service. JDK HttpClient integration with WebClient but to me most interesting is the micrometer-based observability for RestTemplate and WebClient.

During the break I had time to talk to Oleg Šelajev and Juergen. The talk initially covered the future of CRaC. Jurgen was very optimistic about its chances as a future capability in the JVM and the chance of integrating it into a reasonable workflow. I asked him about the uptake of GraalVM and as of now the interest around the new versions focuses on other features. There's excitement for GraalVM but it isn't a killer feature. Yet. I have some thoughts on it which I will share at the end of the post.

DaShaun Carter -- Introducing Spring Boot 3.0 {#h2-1-dashaun-carter-introducing-spring-boot-3-0}
------------------------------------------------------------------------------------------------

![DaShaun Carter](/images/posts/2022/12/springone-tlv-world-tour-trip-report/3-DaShaun-700x343.jpg)

[DaShaun](https://mastodon.online/@dashaun) showed a live demo of building with GraalVM and contrasted the sub second startup time enabled by the native image in GraalVM. He then showed the RAM was a third of the full size JVM image. Another part of the demo showed the actuator works as expected which is nice since GraalVM native image doesn't support the agent APIs in the current version.

The audience asked to see the size of the image which was half the size of the original image. The demo was good, but I've used GraalVM on Spring Boot in the past, so it wasn't new to me. I asked about the uptake and traction they're seeing for GraalVM native image. A lot of developers were waiting for Spring Boot to migrate to GraalVM and I'm curious if this is something they can already see in Spring Initializer stats. I wanted to know if the guys at Spring are seeing a flood of interest. DaShaun said he wouldn't have made the trip if it wasn't for GraalVM. But he invited me to talk later. I ended up speaking to Juergen and got the answer I "wanted".

After that he pointed at <https://calendar.spring.io/> which I wasn't aware of (or forgot). The scale of releases for November is amazing.

Cora Iberkleid -- Protect Your Microservices with Spring Cloud Gateway {#h2-2-cora-iberkleid-protect-your-microservices-with-spring-cloud-gateway}
--------------------------------------------------------------------------------------------------------------------------------------------------

![Cora Iberkleid](/images/posts/2022/12/springone-tlv-world-tour-trip-report/4-Cora-700x271.jpg)

[Cora](https://www.linkedin.com/in/ciberkleid/) began her talk discussing some of the many use cases for a gateway. She emphasized that traditionally there were lots of use cases for a gateway. Spring cloud gateway is a lightweight approach to introduce a gateway that's very approachable in the Spring ecosystem. They built it on the reactive stack and as a result it's fast and has high throughput since it's non-blocking.

The gateway uses predicates in handler mapping and can filter requests/responses. In the filters you can filter, apply rules, and process requests dynamically then apply filters to the response to provide flexibility to the client.

![API Gateway](/images/posts/2022/12/springone-tlv-world-tour-trip-report/5-Api-Gateway-700x299.jpg)

Cora showed routing configuration in YAML and weighted routing to a web service. She then moved to circuit breaking using a request limiter. The request rate limiter is based on Redis and can throttle the amount of requests sent to an API with a replenish rate and burst capacity to block over-usage of an API. This is very useful to limit abuse and limit trial users.

Until now everything was done with pure YAML configurations. The demo concluded with a custom route filter that changes the requests dynamically.

![Gateway Flow](/images/posts/2022/12/springone-tlv-world-tour-trip-report/6-Api-Gateway-700x329.jpg)

Dr. David Syer -- Running Untrusted Code in Spring Using WebAssembly {#h2-3-dr-david-syer-running-untrusted-code-in-spring-using-webassembly}
---------------------------------------------------------------------------------------------------------------------------------------------

![Dr. David Syer](/images/posts/2022/12/springone-tlv-world-tour-trip-report/7-Dr-Syer-700x331.jpg)

There are many cases where we might want to run untrusted code. There are many ways to do that and as [Dr. Syer](https://mastodon.sdf.org/@david_syer) says WebAssembly is one of the best ways to do that. Although browsers support hosting WebAssembly, the focus of the talk is about server-side hosting.

WebAssembly is a polyglot environment which makes it very attractive for some cases. You can experiment with WASM directly in the Mozilla playground in MDN. In the talk he discussed the various compilers you can use to generate WASM from C compilers to AssemblyScript, etc. See <https://github.com/dsyer/hello-as>

He provided an interesting link to a project for hosing WASM in Java:<https://github.com/kawamuray/wasmtime-java>.

Exchanging data with WASM seems painful and a bit of a throwback to communicating and copying data back and forth. He provided two links of interest: [https://github.com/dsyer/spring-wasm-demo https://github.com/dsyer/async-wasm](https://github.com/dsyer/spring-wasm-demo)

After the talk I had time to talk a bit with Dr. Syer who was super nice. My fundamental problem is that I never "got" WASM. How is it better than running Java in a sandbox? The two use cases he mentioned were running untrusted code in embedded and edge computing. As a guy who worked in the mobile and embedded division at Sun/Oracle this stings a bit. I get why this is the place where we're "at" but we had JVMs running in very constrained environments with MVM and for Java capabilities. It's still around it just never got the traction that WASM is gaining.

Another aspect is the Polyglot support. Being able to do that for C or Rust code. But that's not as interesting to me and for most of the use cases I can think of. We obviously have good Polyglot options in the JVM, but mostly in higher level GC languages and not so much in languages like Rust or C. On the other hand, we have many other capabilities such as GC, deep native integration, elaborate memory model, etc.

I've had this discussion with many smart people who see a lot of potential in WASM on the server. Notice that this isn't about the browser where WASM has a valid use case. It might succeed simply because of mindshare, but as it stands right now, it seems that WASM is a couple of decades behind what we have in the JVM world.

Finally {#h2-4-finally}
-----------------------

Unfortunately, I had to pick up my kids from school and had to leave early so I didn't have time to see the talk by [Oleg Šelajev](https://www.linkedin.com/in/shelajev/) about Test Containers or the many other [interesting talks](https://tanzu.vmware.com/developer/springone-tour/2022/tel-aviv/). I did read the post on [Test Containers](https://www.atomicjar.com/2022/11/testcontainers-testing-with-real-dependencies/) but I guess the talk would have been more interesting.

I need to go to more of these things. It seems I know more people when flying abroad to a conference than when visiting a conference in my country. As I mentioned before. I have some thoughts on why GraalVM isn't taking over everything overnight. There are a few big reasons.

### It's Work {#h3-5-it-s-work}

Moving to GraalVM requires a lot of work from the developers. Less so as time moves on. But still a lot of things need to change. Debugging is harder. We need to generate builds in CI and we can't run them locally on some machines. It's a pain.

Adding the slow build process to the mix makes this even more painful.

### Dubious Benefit {#h3-6-dubious-benefit}

As developers, the memory and storage requirements we give are a given. DevOps have cost reduction incentives that can benefit from GraalVM but they can't just integrate it. They need developers to do the work. In a corporate environment, the cloud waste is already astronomical. Sending a developer to migrate to a new VM so the DevOps team can get "cost saving credit". That's not something that the R\&D management can get behind.

The incentives in a corporate setting are problematic. Maybe the Spring team can arrange some viral marketing by helping twitter cut its cloud bill. I'm sure some of their microservices are Spring based.

### Observability {#h3-7-observability}

While Spring Boot includes some monitoring tooling even with GraalVM. The level of observability on GraalVM is lower at the moment (no agent features). That alone might have been OK but coupled with everything else it could be a problem.

### We Don't Use Serverless {#h3-8-we-don-t-use-serverless}

Java developers aren't big on serverless. Over there GraalVM makes perfect sense and is already making great strides. But serverless isn't as common in our community (rightly so IMHO) so the value for GraalVM isn't as clear.

To be clear. I'm very bullish on GraalVM personally. I think it will eventually enjoy significant market share traction and has many things going for it. But it will be a harder chasm to cross despite the obvious benefits.
