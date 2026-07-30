---
title: "Foojay Podcast #96: Local AWS Development Without LocalStack: Meet Floci, the GraalVM-Powered Alternative"
slug: "foojay-podcast-96"
date: "2026-05-25T06:36:00+00:00"
description: "What if you could run 35 AWS services locally in under 25 milliseconds, using just 13 megabytes of memory, with a single Docker command and no cloud bill? - by Frank Delporte"
authors:
  - "frankdelporte"
  - "hector-ventura"
image: "https://foojay.io/wp-content/uploads/2026/05/edit-96-floci.jpg"
categories:
  - "Cloud"
  - "Podcast"
tags:
related_posts:
  - "introducing-floci-a-high-performance-graalvm-powered-aws-emulator"
  - "foojay-podcast-95"
  - "foojay-podcast-94"
  - "foojay-podcast-93"
frozen: false
---

What if you could run 35 AWS services locally in under 25 milliseconds, using just 13 megabytes of memory, with a single Docker command and no cloud bill? That's exactly what Floci does.

In this episode, Frank Delporte talks with Hector Ventura, the creator of Floci, a free and open-source cloud emulator built with Quarkus and GraalVM native compilation. Hector walks us through why he built it when LocalStack dropped its open-source community edition, how AI tooling helped him accelerate development of new service integrations, the challenges of keeping GraalVM happy with third-party libraries, and the road ahead for Azure and GCP support.

If you're a developer who wants fast local testing, a DevOps engineer writing Terraform, or a student learning cloud without the cost, Floci is worth a look!

YouTube {#h2-0-youtube}
-----------------------

<figure class="wp-block-embed">
 <div class="wp-block-embed__wrapper">
  <p>PRESERVEDHTMLBLOCKZZ0ZZEND</p>
 </div>
</figure>

Podcast Apps {#h2-1-podcast-apps}
---------------------------------

You can listen and subscribe to the Foojay Podcast on:

* [Spotify](https://open.spotify.com/show/6CpTfgn9LirzJGAtc4ICdQ)
* [Apple Podcasts](https://podcasts.apple.com/be/podcast/foojay-io-the-friends-of-openjdk/id1652281304)
* And most others...

Guest: Hector Ventura {#h2-2-guest-hector-ventura}
--------------------------------------------------

* [Foojay Author page](https://foojay.io/today/author/hector-ventura/)
* [LinkedIn](https://www.linkedin.com/in/hectorvent/)

Links {#h2-3-links}
-------------------

* On Foojay: [Introducing Floci: A High-Performance, GraalVM-Powered AWS Emulator](https://foojay.io/today/introducing-floci-a-high-performance-graalvm-powered-aws-emulator/)
* [Floci project site](https://floci.io/)
* [Floci on GitHub](https://github.com/hectorvent/floci)
* [Migrate from LocalStack](https://floci.io/floci/getting-started/migrate-from-localstack/)

Content {#h2-4-content}
-----------------------

00:00 Introduction of topic and guest  

01:48 What is Floci?  

02:15 How Floci compares to LocalStack  

03:01 Why Hector started Floci  

04:02 Floci emulates the cloud APIs  

05:02 How additional services got integrated with AI assistance  

06:31 Meaning of the name Floci  

07:07 Why Quarkus and GraalVM as the starting point for Floci  

09:35 How Floci starts up very fast and only uses a low amount of memory  

12:18 GraalVM can be hard with some libraries or frameworks  

14:02 What is needed to use Floci  

14:56 The challenges to support AWS, Azure, GCP and finding contributors  

20:24 Funding Floci  

21:04 How data is persisted in Floci  

22:37 Verifying Floci versus the "real" APIs with compatibility tests  

23:56 In the future: UI for Floci  

25:04 Biggest challenges while creating Floci  

25:32 Functionality compared between Floci and LocalStack and migrating  

28:15 Feedback from the Floci users  

28:58 Long-term plans for Floci  

29:59 Biggest surprises during the development of Floci  

31:00 Best use-cases for Floci  

32:12 In the next releases...  

33:31 How to get started with Floci  

35:00 Conclusion
