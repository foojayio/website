---
title: "A Compendium of 2021 Java & OpenJDK Predictions"
slug: "2021-java-jvm-predictions-by-topic"
date: "2021-01-22T08:03:25+00:00"
lastmod: "2021-01-24T08:09:41+00:00"
description: "Now that 2021 is well underway, many prominent Java developers have taken the time to predict what 2021 may bring to the Java universe!"
authors:
  - "kevinfarnham"
image: "Favicon-3-2.png"
categories:
  - "Performance"
  - "Security"
  - "Tools"
tags:
related_posts:
frozen: false
---

Now that 2021 is well underway, many prominent Java developers have taken the time to predict what 2021 may bring to the Java universe.

In December, here on Foojay.io, several articles were published on this theme, such as [Java Predictions for 2021](https://foojay.io/today/java-predictions-for-2021/), [Java Predictions for 2021: Jakarta EE](https://foojay.io/today/java-predictions-for-2021-jakarta-ee/), and [Java Predictions for 2021: Raspberry Pi](https://foojay.io/today/java-predictions-for-2021-raspberry-pi/). In early January, Simon Ritter published [Staring Into My Java Snow Globe 2021](https://www.azul.com/staring-into-my-java-snow-globe-2021/ "Starting Into My Java Snow Globe 2021"). Separately, I conversed with another six developers about their visions for the coming year.

In this post, I arrange these predictions and observations by topic, in essence creating a series of brief panel discussions about each topic area: a sort of mini-Java conference in the form of an article!

### Java Version Migration

Java 8 was a major discussion point for many. Everyone predicts it will lose usership in 2021.

* **Ryan Cuprak:** "2021 will be the year that Java 8 finally fades into the past. Java is moving forward with many exciting new features - it is now hard to keep up with the platform changes finally!"
* **Matt Raible:** "Java 8 usage will decrease by 50%! (I hope) Largely because many Java frameworks default to Java 11 now."
* **Reza Rahman:** "There may finally be significant uptake of Java SE LTS releases past 8."
* **Fabiane Bizinella Nardon:** "I think we can expect more companies upgrading to newer versions of the VM to take advantage of the new features launched."
* **Carl Dea:** "Many mature legacy Java projects will at least move their project to Java 11 or 15."
* **Dustin Marx:** "The release of the OpenJDK 17 implementation in 2021 will motivate many who are already working on versions of the JDK later than JDK 8 to start moving or investigate moving to JDK 17. However, JDK 8 will remain widely popular (probably will still be used by over half of Java developers), creating (in the long term) a bimodal distribution of most commonly used JDK versions (8 and 17)."

As Dustin notes, the upcoming JDK 17 LTS (Long-Term Support) version is of considerable importance. But since Java 17 will not be released until this Autumn, and the exact feature set is still unknown, predicting how soon companies will make the switch is somewhat guesswork.

The consensus, though, is that Java 17 will ultimately become the predominate modern version.

* **Almas Baimagambetov:** "We have already upgraded our Computing curriculum to use JDK 11. In 2021, I predict that we will explore JDK 17 LTS and how it can be integrated into the offered courses."
* **Marcus Hirt:** "Adoption of JDK 17 by the JDK 11 crowd will be fairly quick, and by the end of 2022 a majority will have transitioned."
* **Peter Lawrey:** "Over the next 3 years Java 17 LTS, unlike Java 11 LTS, will replace Java 8 as the most widely used version of Java."

Another topic of conversation was the proliferation of OpenJDK vendors. **Carl Dea** considers this good, because "The public sector will have more choices of OpenJDK distros." However, **Bruce Dutheil** said "I am worried about so many OpenJDK vendors, this fragmentation is great for innovation but will provoke confusion."

### Important New Java Features

For some of the details of what JDK 16 will include, see Simon Ritter's article [Staring Into My Java Snow Globe 2021](https://www.azul.com/staring-into-my-java-snow-globe-2021/ "Staring Into My Java Snow Globe 2021").

With respect to JDK 17, it's more a matter of hopes and speculations.

[Project Loom](https://wiki.openjdk.java.net/display/loom/Main "Project Loom"), which is related to fibers and continuations for lightweight concurrency, was brought up by several people:

* **Simon Ritter:** "I *hope* that we will see Project Loom added as an incubator/preview feature."
* **Brice Dutheil:** "I think by the end of 2021 we may see Loom merged as a preview in OpenJDK, probably for Java 18."
* **Carl Dea:** "Project Loom will not be ready in 2021, but should be ready third quarter of 2022."

Records were another topic of focus:

* **Carl Dea:** "I believe a large percentage of companies that still rely on JDBC, will jump to Java 15 to primarily use the new feature of Text Block and Records."
* **Dustin Marx:** "Records will likely be finalized in 2021 and will be widely popular with Java developers who are fortunate enough to work on a version of the JDK with final (not preview) Record support."

Additional observations and forecasts:

**Marcus Hirt** noted that his company "Datadog will make at least one major contribution to make OpenJDK even better for production profiling."

Looking into the longer term, **Peter Lawrey** predicted: "Java will continue to slowly adopt features from the other top languages such as C++ and C#, less so C, Python, and Javascript, possibly some features from cooler, though less widely used languages."

### Growth in Application Areas

Several developers cited application areas where they expect to see growth in 2021:

* **Brice Dutheil:** "I believe that with the coverage of Java Flight Recorder (JFR) this year, we are likely to see more libraries adopting JFR."
* **Carl Dea:** "More Java apps will appear on Android and iOS devices, due to technologies like [Gluon Mobile](https://gluonhq.com/products/mobile/ "Gluon Mobile")."
* **Jad Sarno:** "Python and R are dominant in Machine Learning, especially for Data Scientists. Data Engineers, however, can now also use Java with new commercial-ready frameworks such as [DeepLearning4j](https://deeplearning4j.org/ "DeepLearning4j") and Amazon [Deep Java Library](https://djl.ai/ "Deep Java Library"), to train and deploy neural networks. Most AI frameworks use the same mathematical foundations, and language is a personal preference."

### Security

I don't think that there will ever be a new year where security isn't an ongoing critical issue in technology:

* **Ryan Cuprak:** "I think that the Java and open source ecosystem may feel the impacts of geopolitics and decoupling. The Solarwinds hack is going to force a reckoning and re-evaluation of the risk around tools and libraries. I think the impact of this hack is currently under-appreciated and there will be some serious fallout. I think that COVID19 will scrabble trends: what happens if you start seeing disruptions at cloud vendors due to COVID19?"
* **Travis Spencer:** "2021 will see continued adoption of RSA-PSS throughout the Java ecosystem, and much broader use of Edwards-curve Digital Signature Algorithm (EdDSA) that arrived this fall. A JEP for Encrypted Client Hello (ECH) may come late in the year (or early in 2022); this is much needed as Java is used to build a huge swath of the Web applications and services people are consuming, and without ECH all web site destinations can be tracked by intermediaries (e.g., governments, ISPs). As the OpenSSL relicensing under Apache 2 hits the street early in 2021, we will likely see more Java interop with v 3 of that library for speeding up TLS."

### Jakarta EE, MicroProfile, GraalVM

Much new development is expected in Jakarta EE, MicroProfile, and GraalVM technologies:

* **Reza Rahman:** "There will be a Jakarta EE 9.1 release supporting Java SE 11. There may be a major Jakarta EE 10 release. Well structured monoliths and server-side rendering will enjoy a comeback. GraalVM based approaches such as Helidon, Micronaut and Quarkus will gain more traction."
* **Carl Dea:** "Newer projects will move towards Quarkus.io, Micronaut, and Helidon at a rapid rate."
* **Ryan Cuprak:** "Jakarta EE and MicroProfile appear to be on pretty firm footing and will continue moving forward."

### Beyond Java on the JVM

Simon Ritter recently noted in his article [Java First, Second, and Now Third](https://www.azul.com/java-first-second-and-now-third/ "Java First, Second, and Now Third") that in the November 2020 TIOBE programming index Java had fallen into third place.

Something I've long wondered: what if the TIOBE index also included a category called "JVM Languages" that combined all languages that run on the JVM? Where would "JVM Languages" stand in its rating system?

As a quick experiment, I looked at the TIOBE January 2021 numbers for Java, Groovy, Scala, and Kotlin. Adding just those three JVM languages to Java's total makes the "JVM Languages" TIOBE category 21% higher than TIOBE's "Java" category. Then, of course, there are many smaller JVM languages as well. So, a TIOBE "JVM Languages" category would clearly have a usage number significantly higher than TIOBE's "Java" category.

**Peter Lawrey** has a prediction related to non-Java JVM languages: "Kotlin will continue to rise in popularity, getting into the top 30 popular languages, possibly challenging Groovy."

### Java Community Process (JCP)

Simon Ritter [observed](https://www.azul.com/staring-into-my-java-snow-globe-2021/ "notes"): "Another area that may change this year is the Java Community Process (JCP). There are now only seven active JSRs outside of those for Java SE, and [only one of those seems to have any real activity](https://jcp.org/en/jsr/detail?id=381 "only one of those seems to have any real activity"). It may be time to find a different way to handle the Java SE standards work and intellectual property management."

### Java Community and Conferences

**Stephen Chin** noted: "The Java community has the best global conference ecosystem and quickly adapted to virtual events in 2020. I predict that we will see the Java community lead the charge with innovative solutions to solving the hybrid conference model in 2021, getting us all back together in person and connected globally."

### Conclusion

There's a lot of activity, a lot of possibilities for 2021. Probably everyone will agree with Java Champion **Fabiane Bizinella Nardon**'s statement: "I think the Java community will continue to be vibrant!"
