---
title: "The End of the Beginning for Apache Cassandra"
date: "2022-07-12T17:43:39+00:00"
lastmod: "2022-07-13T13:23:48+00:00"
description: "Image: Pixabay Editor’s note: This story originally ran on July 27, 2021, the day that Apache Cassandra 4.0 was released. Today is a big day for - by Patrick McFadin"
canonical: "https://datastax.medium.com/the-end-of-the-beginning-for-apache-cassandra-f58b5b4b7504"
authors:
  - "patrick-mcfadin"
image: "1_xslbFHC3hRapwGV_wj17vg.jpeg"
categories:
  - "Apache Cassandra"
  - "Cloud"
  - "Databases"
  - "DataStax"
  - "DevOps"
  - "Kubernetes"
related_posts:
frozen: false
---

![](1_xslbFHC3hRapwGV_wj17vg-1024x663.jpeg) Image: [Pixabay](https://pixabay.com/photos/prairie-river-stream-curved-sunset-679014/)

*Editor's note: This story originally ran on July 27, 2021, the day that Apache Cassandra 4.0 was released.*{#4bbb}

Today is a big day for those of us in the [Apache Cassandra](https://cassandra.apache.org/) community. After a long uphill climb, Apache Cassandra 4.0 has [finally shipped](https://thenewstack.io/apache-cassandra-4-0-comes-in-ready-for-production/). I say finally, because it has at times seemed like an elusive goal. I've been involved in the Cassandra project for almost 10 years now and I have seen a lot of ups and downs.{#d6a4}

So I feel this day marks an important milestone that isn't just a version number. This is an important milestone in the lifecycle of a database project that has come into its own as an important database used around the world. The 4.0 release is not only incredibly stable in the history of Cassandra, but it's also quite possibly the most stable release of any database.{#d6a4}

Now it's ready to launch into the next 10 years of cloud native data; it has the computer science and hard-won history to make a huge impact. Today's milestone is the end of the beginning.{#d6a4}

## Not the hero you wanted, but the hero you need

In early 2011, I was having lunch with the person whom I would call the first evangelist for Cassandra: [Adrian Cockcroft](https://twitter.com/adrianco). At the time, he was helping transform Netflix from a mail-based DVD company to a streaming company that required a lot of cutting-edge technology, some of which hadn't even been invented yet.{#d99a}

We talked about many things, but the only thing I remember was when he told me I should try out this distributed database they were using called Cassandra. It was one of the dozens of NoSQL databases exploding on the scene, as those of us trying to scale infrastructure based on increasing demand were finding the limits of Oracle and MySQL. That night I had a running cluster of 0.7 running in [Amazon Web Services](https://aws.amazon.com/?utm_content=inline-mention), and I haven't stopped since.{#d99a}

These were the early years of Cassandra, and as is typical in the 30- to 40-year typical lifespan of any database, it was a time of amazing growth in features and innovation. Cassandra was being adopted in organizations that needed scale and were ready to devote the engineering time to keep the pace of innovation fast.{#2ec4}

The computer science was really clear: To meet the type of scale requirements modern applications need, you have to use a coordination-free database that is built for availability and partition tolerance. There were teams using other technologies, but not always successfully. Because of this, Cassandra earned the reputation as the database that wouldn't let you down, though it was really hard to learn. If all other databases failed to deliver the needed uptime or scale, Cassandra could do the job.{#2ec4}

## Coming of age isn't easy

In 2016, Cassandra 3.0 was released and one of the big changes was a completely new storage engine. Anyone who has worked in operations knows that major alterations to core components need their time in service before reaching a stability point that's generally trusted. Cassandra wasn't immune to this. With a lot of initial issues in the 3.0 storage engine, most users opted to stay with 2.1 and wait to upgrade. At about that time, DataStax was pulling away from the project, which led to a lot of internal project conflict. Apache Cassandra had arrived at the awkward adolescent years.{#462c}

Just like human teenagers, the Apache Cassandra project was having a moment of asking itself, "What do I want to be when I grow up?" That conversation was happening between the contributors, committers and the project management community. Stability and correctness are the only things that count for a database that a large part of the world depends on as a source of truth.{#015c}

At ApacheCon 2019, I attended a large, ad-hoc gathering of people to discuss what standards the Cassandra project wanted to hold for a version release. We didn't have a conference room, so we all sat on the floor debating in a side hallway of the Flamingo Las Vegas. In the end, we agreed that a single statement embodies how we have to move forward, and it was adopted by the project: "The overarching goal of the 4.0 release is that Cassandra 4.0 should be at a state where major users would run it in production when it is cut."{#015c}

DataStax is the open, multicloud stack for modern data apps. DataStax gives enterprises the freedom of choice, simplicity, and true cloud economics to deploy massive data, delivered via APIs, powering rich interactions on multicloud, open source, and Kubernetes.{#2da8}

The idea that a dot-zero release could be considered production stable doesn't fit in many operators' world views. Of course, you have to wait at least a few bug releases before trying your hand at an upgrade, right? The members of the Apache Cassandra community decided to challenge that idea. What is the point of a beta release or a release candidate? Since this is not being built in a cathedral and instead in the open bazaar, a real contribution to the project will be running a beta with production workloads. And before getting to the beta, we need to be able to test correctness in a variety of ways that failure can happen consistently and continuously. Incredible tools have been built in the project in the past few years that are unmerciful in the failure modes they present. The payoff has been real. Apache Cassandra 4.0 is green on all tests and, as promised, being run in production by the organizations sponsoring engineering time. It's being released because the members of the project believe in the promise that this will be the most stable database you can use.{#0bea}

## On the shoulders of giants

This is how we got to today. A solid pedigree from the beginning, years of innovation and a commitment to quality. The database is trusted by companies like Netflix, Uber, Flipkart, ING Bank and hundreds of others. And now we are on the cusp of a new era of Cassandra. Truly the end of the beginning. So what is next for Cassandra?{#6eec}

Quite a lot actually.{#99d4}

To get quality management to a place the project needed, there had to be an early code freeze to stop continuous changes and the endless tail-chasing that can cause. This has meant that a relatively small number of big features have been released, yet innovation has been on the sidelines with an eye toward the days after 4.0 and after the code freeze is lifted. Beyond the inner circle of the project, we already have a great [ecosystem](https://cassandra.apache.org/ecosystem/) of projects and companies around Cassandra that increase access and make it easier to use. Expect to see this grow even more as the need for a stable foundation in distributed applications increases. Projects like [K8ssandra](https://k8ssandra.io/) and [Stargate](https://stargate.io/) can rely on Cassandra and focus on their own project goals. If you have a project that needs a reliable and trusted data store like Cassandra, many people in our community are ready to help. You just need to ask.{#bdf2}

The Cassandra Enhancement Process ([CEP](https://cwiki.apache.org/confluence/pages/viewpage.action?pageId=95652201)) was put in place to bring major new features into Cassandra. Several have already been started, including [Storage Attached Indexing,](https://cwiki.apache.org/confluence/display/CASSANDRA/CEP-7%3A+Storage+Attached+Index) which is designed to be a replacement to the original secondary indexing and a part of DataStax's re-commitment to support Apache Cassandra. Others that have been proposed include adding joins to Cassandra (yes I said that) and important upgrades to transactions. The change proposed with potentially the widest impact is the implementation of pluggable storage. This grants the ability to use a variety of storage engines with Cassandra, optimized for certain workloads. Instagram had already shown early promise with this idea by adding RocksDB as a storage engine so the possibilities are really exciting. Happening in parallel, Cassandra will be taking advantage of the rapidly evolving innovations in Java garbage collection. Zero Garbage Collection (ZGC) is now delivering [sub-millisecond](https://malloc.se/blog/zgc-jdk16) pause times in JDK16 and even bigger gains in the near future. The impact on Cassandra and other JVM-based systems will be profound. Stay tuned for some mind-blowing benchmarks.{#b641}

If you are a current user of Apache Cassandra, you should consider an upgrade soon. The stability and performance improvements will make it worthwhile. If you're new to Cassandra, now is a great time to get in and give it a try. You won't be disappointed and the rocket ship is getting ready to take off again --- make sure to not miss this ride. We would love for you to be in [our community](https://cassandra.apache.org/community/).{#fd3c}

The next era of Cassandra is going to be exciting and full of its own challenges. One thing's for sure, it won't be like the last era.{#fd3c}

*Learn more about* [*DataStax Astra DB*](https://astra.dev/3yzQjif)*, the DBaaS built on Cassandra.*{#f068}

*This story was originally published in* [*The New Stack*](https://thenewstack.io/the-end-of-the-beginning-for-apache-cassandra/)*.*
