---
title: "Be a Better Java Developer: 19 Tips for Staying Ahead in 2024"
slug: "become-a-better-java-developer-19-tips-for-staying-ahead-in-2024"
date: "2024-06-11T12:31:30+00:00"
lastmod: "2024-06-11T12:44:19+00:00"
description: "I reached out to one of my fellow Java developers who is very experienced and has been working in the industry forever and asked for his thoughts about the observability improvements in JDK 21 and Spring Boot 3.2."
authors:
  - "lee-sheinberg"
image: "Digmo-and-Java-.png"
categories:
  - "Java"
  - "Observability"
tags:
related_posts:
  - "beyond-pass-fail-a-modern-approach-to-java-integration-testing"
  - "couch-to-fully-observed-code-with-spring-boot-3-2-micrometer-tracing-and-digma"
  - "effective-coding-with-java-observability"
  - "boxlang-aws-azure-and-google-secrets-manager-module-released"
frozen: false
---

Recently I reached out to one of my fellow Java developers who is very experienced and has been working in the industry forever and asked for his thoughts about the observability improvements in JDK 21 and Spring Boot 3.2 and if he has already migrated from 17 to 21 and to Spring Boot 3.2.

This is the reply I got from him:

**"Lately, I've been feeling quite rusty, my current job has me solely focused on building an IntelliJ plugin, it's been almost two years now. And I'm concerned I'm not keeping up to date. I've promised myself to take a tutorial and learn everything from Java versions 17 to 21. With Java 22 nearly released and Spring 3.2 out, I'm not familiar with all the improvements. I've already heard about the exciting new features of Java 22 like the non-Java interop and access to important libraries like LAPACK and BLAS. These libraries power significant Python libraries like NumPy, offering new opportunities for Java.**

**I'm still working on version 17. I know many companies still use Java 8. But still, I'd love to experience the time-saving, memory-saving, or new possibilities opened up by new Java specs. I was over the moon when I heard about lambda function support. Some of the stuff in the latest release seems like black magic to me, but I know I will love it. It's like a new level in my favorite video game."**

After talking with my friend, I decided to reach out to the folks on r/ExperiencedDevs on Reddit. I wanted to know if other developers experience similar feelings and if they have suggestions, aside from tutorials, to boost confidence as a Java developer. Here is the thread: if you want to check it out.

After posting my question and receiving so much solidarity, I shared it with my friend. He was very happy to know that he was not alone. We both felt that I should compile all the tips I received and create a post for developers who feel they are falling behind in staying current.

![](https://digma.ai/wp-content/uploads/2024/05/image-461x1024.png)  

Sorce; Reddit

Here are the tips I got on becoming a better Java developer
-----------------------------------------------------------

1. Bump up the version number, and JetBrains IDEs auto-suggest most of the new changes. IntelliJ itself does a great job of telling you if you aren't taking advantage of new language features.
2. Learn Kotlin: it might give you some interesting perspective (and the ability to shift jobs if the need arises, etc.). My understanding is that Java 17 and 21 are actually significantly better than 9 \& 11, and adopt a lot of similar changes to what Kotlin introduced (other than null safety, because of backward compatibility)
3. Feeling rusty means a chance to grow. It's a challenge, and that's what keeps me from getting bored. Think about what you find most intimidating and incomprehensible about the entire computation stack. Learn deeply about that.
4. Look into how other languages/frameworks work. Sometimes, one framework has a long-standing issue everyone is complaining about, and there is just no such thing in another framework. It's eye-opening how a better approach can make your life so much easier.
5. Get familiar with Loom. That's the most foundational change to Java in a very long time.
6. Read about Structured Concurrency in JDK 21: <https://openjdk.org/jeps/428> it really is a leap forward in Concurrent Programming. There are two separate concurrency innovations. Both are major improvements.

* Virtual Threads. This is final in JDK 21. This is basically what Golang (and others) do. You get the high performance of async/reactive programming without the programming complexity.
* Structured Concurrency. This is still in preview in JDK 21. Golang has something similar (<https://pkg.go.dev/golang.org/x/sync/errgroup>), but IMO, the Java API is nicer.

7. Get some nice coverage from Oracle, they cover a lot of the new features and changes to the Java ecosystem with 21. For the most part, the features added have use cases you may or may not need, so you should take the time if you need to; otherwise, just get some general understanding and updates from Oracle's Java Day presentations. They were pretty interesting talks too. Not so dull.
8. Learn Groovy and later on Scala. After learning Scala, you will probably never want to program in Java again. Also, Scala pays better than Java or Kotlin and is much nicer to program when you learn it. But there are fewer jobs than in Java.
9. Check out <https://advancedweb.hu/a-categorized-list-of-all-java-and-jvm-features-since-jdk-8-to-21/>. Looks like some nice features, but a lot of them are relatively minor. Unnamed variables, for example. Great, I use that in other languages already, but nothing that changes too much. I like that they added sealed classes.
10. Practice Continuous Feedback during the Dev Cycle. Collect data about your code so you can identify issues before reaching prod, understand test results, and make the right design decisions.
11. Try Ktor for the following reasons:
    * Easier upgrades since there aren't interdependencies.
    * No "magic", the code you write is the code that is run
    * AOP can be a nightmare to debug.
    * WAY more performant. Spring Kafka is an order of magnitude slower than using the Apache API
    * The dependency tree is about 1/3rd the size for the same functionality, and Startup time is basically instantaneous
12. Build a side project: If you're looking for projects to build, try <https://codingchallenges.fyi/> it's really good.
13. Take observability seriously, and get familiarized with observability tools and techniques. Understand profiling, monitoring, tracing, and debugging. Explore tools like JVisualVM, JProfiler, and Continuous Feedback.
14. Stay in touch with the community -- Java is a mature community with many Java champions who are very helpful. Solidarity and shared knowledge can boost your confidence.
15. Find a JUG around your location and attend the meetups so you can get updates and also mingle with some awesome talented people.
16. Read professional developer blogs and also follow these devs on social for updates. Here is a good list of 10 developer blogs related to Java and related topics: This article also contains some websites like Foojay and Digma which deliver high-quality content for developers.
17. Follow influencers on social media like Piotr Mińkowski, a Solution Architect at Red Hat. He writes about Java, Spring, Kotlin, microservices, \& K8s. Also, Josh Long, a Spring Developer Advocate (@Java_Champions \& @Kotlin @GoogleDevExpert) @VMwareTanzu <https://YouTube.com/@coffeesoftware> for updates about Spring Boot.
18. Sign up for Rodrigo Graciano's weekend reading list, where he shares the best Java articles of the week. Here is the website: <https://graciano.dev/>.
19. I saved the best for last, as I knew people would associate "feeling rusty" with Rust: 'If you are feeling quite Rusty, might I recommend you take up my Lord and Savior- Rust"

Final Words:)
-------------

I'm sure that some of you can relate to the situation and find these tips valuable.

And again there's a supportive community out there ready to help out, and that's great!

Ping me on our [Slack Channel](https://continuous-feedback.slack.com/ssb/redirect "Slack Channel") if you have any other tips that you want to share.
