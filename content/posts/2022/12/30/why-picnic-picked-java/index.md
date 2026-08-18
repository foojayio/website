---
title: "Why Picnic picked Java"
date: "2022-12-30T08:22:54+00:00"
lastmod: "2025-01-05T09:25:00+00:00"
description: "Finding the right tech stack is a context-dependent journey. There's no right or wrong, just a lot of different angles to explore!"
canonical: "https://blog.picnic.nl/why-picnic-picked-java-e53fafe0df1b"
authors:
  - "sander-mak"
image: "1_Glz2p97s8p38c_dclxg4oA-scaled.webp"
categories:
  - "Developer Tools"
  - "Java Core"
  - "Opinion"
tags:
related_posts:
  - "embracing-java-17-heres-what-we-learned-at-picnic"
  - "the-quest-to-the-os-java-native-memory-foojay-today"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
frozen: false
---

**Picking a tech stack for your startup isn't something to do lightly. It's a choice that will shape the future in many ways: how will the tech enable your emerging product and business, what talent can you attract, and how future-proof is the tech stack?**

When [Picnic](https://picnic.app/careers/blogs) launched as the first app-only supermarket back in 2015 in The Netherlands, the tech landscape looked markedly different from today.

Java and .Net ruled the Dutch developer landscape for backend systems.

Java 8 was released just the year before, adding the beloved lambdas and streams functionality.

NodeJS was on the rise, and Kotlin was just a pre-release blip on the radar.

## Why Java?

So why did we end up picking Java as our backend development stack?

* We knew from the start that our online groceries business would be a mass-market proposition. It was also clear that while Picnic's ambitions started in The Netherlands, they for sure didn't end there.
* Picking a tech stack that optimizes for quick prototyping seems tempting, but would mean significant re-engineering down the line to scale with these ambitions. Remember how Twitter had to [re-platform](https://www.infoq.com/articles/twitter-java-use/) from Ruby to Java to support its growth? Definitely not an enticing prospect, so our choice was geared towards the long run. Unlike Twitter, Picnic embraced Java from the get-go!
* We didn't only plan for scaling in terms of customers using Picnic; we also knew that the engineering organisation would have to grow with Picnic's success. As of today, we have over 300 people in our tech team. Building such a team in just a couple of years only works if you align your tech choices with the realities of the hiring market. With millions of Java developers around the world, and Java being one of the dominant tech stacks in The Netherlands, that's one less thing to worry about.
* Another important factor is that the team of engineers building the first version of Picnic's platform already had ample experience using Java and the JVM. It makes little sense to choose a completely different technology in that situation, unless the problem really calls for it.
* Java is a proven and scalable platform for backend services, which is exactly what's needed. By virtue of this choice, the team gets to spend their [innovation tokens](https://matt-rickard.com/innovation-tokens) elsewhere.
* Picking Java also brings a lot of benefits beyond the core platform. There's a thriving open-source ecosystem to tap into. For virtually every task you want to achieve, there's a battle-tested open-source library available to get the job done.

All in all, enough reasons for picking Java to take Picnic from day one to where we are today.

## Why Java, the language?

Still there, **there's Java *the platform* , and Java *the language***.

You can adopt the Java platform with its JVM ecosystem and mature tooling while writing your code in an alternative JVM language.

The next question then is: should we pick the Java language, or look at alternatives on top of the JVM?

Around 2015, Scala was the most popular alternative to the Java language.

When choosing an alternative JVM language, you should always ask why:

* **Do you believe developers to be more productive using the language?** Sadly there's very little evidence supporting productivity differences between programming languages, especially in bigger real-world contexts. Often these things come down to personal preference and anecdotal evidence.
* **Are there killer features or libraries for the alternative language that help you build your product?** This could be a good reason. Still, how mature and future-proof is the alternative compared to mainstream Java?
* **Does your team have enough expert-level knowledge of the language?** Unguided adoption of a new language may result in non-idiomatic code. Ironically, introducing a new language then causes technical debt from the start. This effect compounds if your team is growing fast.
* **Does it make developers happier?** Quite subjective, but it can be a reason to adopt an alternative JVM language. However, can you grow your team fast enough after adopting a niche language? Of course, onboarders can learn a new JVM language on the job, but as a start-up every hour of engineering productivity counts.

None of these considerations were significant enough for us to stray from the default language for the JVM.

In retrospect, it seems that Scala's popularity has peaked, and we're happy that we chose the Java programming language.

Still, the more things change, the more they stay the same. Currently a similar surge of alternative JVM language adoption is underway, this time with Kotlin leading the pack.

Would it be something for us to adopt now? Let's park that question for a bit, since there's more to tell about *how* we adopted Java throughout the years first.

## Adopting Java

Picking a tech stack is one thing, but making the most of its adoption is something else entirely. Early on, Picnic established a Java Platform team to gradually build up our [Java developer platform](https://blog.picnic.nl/becoming-a-multiplier-on-our-java-developer-platform-17fe87de2e20 "Java developer platform").

By doubling down on Java as the primary development stack as we grew, all of our product development could move faster.

Some notable achievements of our Java platform team are:

* Building internal libraries on top of Spring (Boot) to support common challenges within product teams.
* [Migrating to new Java versions](https://blog.picnic.nl/embracing-java-17-heres-what-we-learned-69779d95fdf2 "Migrating to new Java versions") as they become available: currently all services run on Java 17, and we plan to move to the next Long Term Support version once it's available next year.
* Providing a default Maven-based build infrastructure, so teams don't have to worry about this. In combination with [Renovate](https://www.mend.io/free-developer-tools/renovate/ "Renovate"), no team has to worry about outdated dependencies, either.
* Ensuring uniform source code layout using `google-java-format`: no more pointless discussions about code formatting on pull requests.
* [Introducing Error Prone](https://blog.picnic.nl/picnic-loves-error-prone-producing-high-quality-and-consistent-java-code-b8a566be6886 "Introducing Error Prone") for static code analysis and even rewriting Java source code based on (customized) rules: improving and refactoring all of our code at scale is now feasible!
* [Open-sourcing Error Prone Support](https://blog.picnic.nl/picnic-open-sources-error-prone-support-b23f9a7208b6 "Open-sourcing Error Prone Support"): our own extensions to Error Prone are now open for everyone to use, and can benefit from outside contributions.

The list goes on, but the important point is that we invest heavily in our internal Java development platform. As a result, Picnic's teams benefit from a uniform and modern approach to Java development for maximum developer happiness and effectiveness.

## Enter Kotlin

You now understand why we picked Java when starting out. But we're living in a different world today. One in which Kotlin, as mentioned earlier, is gaining traction as the alternative JVM language. So should we switch to Kotlin? A simple question with a multi-faceted answer.

In fact, we did migrate from Java to Kotlin: only not for our backend codebases, but for our Android app. Given that Android is stuck on Java 7 with only a subset of Java 8 features, it's clear that Google wants to move the ecosystem away from Java. Who wants to work with pre-2014 tech? Moving to Kotlin was a no-brainer for our mobile team.

For Java backend development, things look quite different. Many people see Kotlin as a better Java. While that's a fair take, we happen to think that Java is a pretty great language already! On top of that, it's closing the gap to Kotlin and other languages in an impressive manner. Since we move to new versions of Java quickly, we reap the benefits directly. Let's look at some recent examples:

* **Records** (Java 17) are similar to the data classes feature in Kotlin.
* **Pattern matching for instanceof** (Java 16) now offers Kotlin's smart cast feature in Java, with more pattern matching features to come.
* **Sealed interfaces** (Java 17) were also first seen in Kotlin.
* **Text Blocks** (Java 15) now allow multi-line Strings in Java source code as in Kotlin, with String interpolation [coming up](https://openjdk.org/jeps/430 "coming up"). In fact, the proposed String Templates feature goes way beyond what's possible in Kotlin.

Jake Wharton, an active Kotlin community member, also observed this closing of the Java-Kotlin gap in his talk '[Java 19 and the end of Kotlin](https://jakewharton.com/report-card-java-19-and-the-end-of-kotlin/ "Java 19 and the end of Kotlin")', with concrete examples based on the points above. While 'end' is obviously hyperbolic and tongue-in-cheek, it's clear that Java is taking a cue from other languages in its rapid evolution, making it more attractive with every release. A quick look at the [preview features](https://openjdk.org/projects/jdk/19/ "preview features") in Java 19 shows that there's many more exciting changes to come.

Switching languages is a big investment. From re-working a lot of our Java platform tooling to re-skilling hundreds of developers and re-establishing best coding practices, this is not something to take lightly. As a scale-up, we have many other challenges to solve. Given the diminishing benefits of switching, and looking at the rise and fall of earlier alternative JVM languages (Groovy, then Scala), such an investment just doesn't add up for us.

## Are we missing out?

Does that mean we're not looking enviously at some Kotlin features? Of course we are! Even though Java is catching up with Kotlin in many ways, there are still parts where it hasn't (yet), or won't. However, with a couple of smart choices, we can get closer to some of the benefits from the Java side as well.

Built-in null-safety in Kotlin's type system is one of its most frequently cited benefits. In Java, you can get close by using the `@Nullable` annotation in combination with static analysis tooling like NullAway. We also tend to use libraries that are null-averse (such as Guava and Immutables) by construction. Certainly less elegant, but in practice we rarely see `NullPointerException`s, which is ultimately what this is all about.

Kotlin is also a more expressive language with a richer standard library, following the 'batteries included' philosophy. Its collections and streams APIs are more extensive than Java's counterparts. On the language side, Kotlin has many features (extension methods, operator overloading, function literals with receivers) that allow you to craft DSL-like libraries. Higher expressivity isn't necessarily better, though.

If there are many more ways to achieve something, it's less clear what an idiomatic approach is. You somehow need to tame this expressivity with your team, and manage the associated cognitive load during development and code reviews. For Java development, we can build upon decades of experience amassed in community guidelines, oftentimes supported by tooling like Error Prone, Checkstyle, and so on.

One final example. Kotlin's coroutines (with `async`/`await`) are fantastic tools for building high-throughput, non-blocking services. We do this in Java with reactive programming libraries like Project Reactor, but it is comparatively painful. With [Project Loom](https://wiki.openjdk.org/display/loom/Main "Project Loom"), there's an even better solution on the horizon for Java! Its promise: write boring sequential and seemingly blocking code, but get non-blocking and asynchronous execution by running on Loom's Virtual Threads.

Rather than adopting Kotlin's coroutines approach, Java will offer a much more compelling alternative. Why didn't Kotlin do this? Because Loom's approach requires changes in the whole Java platform (including libraries and the JVM), not just the language. That's the kind of change an alternative JVM language like Kotlin just can't bring. Of course Kotlin's coroutines may benefit from the changes of Project Loom for its internal execution model. Yet there may be design choices in Kotlin's approach that might not quite fit Loom's platform changes. That's a downside of being a first mover in an ecosystem which Kotlin doesn't completely control.

It's great that a language like Kotlin advances the JVM ecosystem. For us, it's even better to see many of the best ideas making their way back into Java proper.

## Making your own choice

As has become clear, finding the right tech stack is a very context-dependent journey.

There's no right or wrong, just a lot of different angles to explore and questions to answer.

Whatever you choose, fully embrace the choice by investing in tooling and developer experience. Such an investment brings increased productivity and developer happiness. It also grounds you more in your choice: switching to an alternative must bring real benefits.

Don't jump to a shiny new technology just because everyone thinks it's cool.

Our commitment to Java is very much shaped by our experience and investment in it, as well as by our confidence in its continued evolution and relevance, even as other JVM languages come and go.

As a scale-up, we reap the benefits of Java's unique blend of maturity and innovation every day!

*Many thanks to Stephan Schroevers and Jakob Löhnertz for reviewing the draft of this post.*

👉 Come [work with us](https://picnic.app/careers/jobs/986403/technology--amp--engineering/amsterdam-north-holland-netherlands/java-developer "work with us") to shape the future of Java at Picnic!
