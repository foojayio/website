---
title: "Redis is now available under the AGPLv3 open source license"
slug: "redis-is-now-available-under-the-agplv3-open-source-license"
date: "2025-05-02T12:15:59+00:00"
lastmod: "2025-05-02T12:23:26+00:00"
description: "Redis 8 with its new capabilities and with AGPL licensing demonstrates our ongoing commitment to making a platform developers love."
canonical: "https://redis.io/blog/agplv3/"
authors:
  - "rowan-trollope"
image: "/images/posts/2025/05/redis-is-now-available-under-the-agplv3-open-source-license/redissmall.png"
categories:
  - "Redis"
tags:
related_posts:
  - "back-to-basics-accessing-kubernetes-pods"
  - "different-approaches-to-building-stateful-microservices-in-the-cloud-native-world"
  - "even-more-opentelemetry"
  - "fixed-window-counter-rate-limiter-redis-java"
frozen: false
---

**The rise of hyperscalers like AWS and GCP has unlocked incredible speed and scale for startups and enterprises alike. But for companies rooted in open source, it has posed a fundamental challenge: how do you keep innovating and investing in OSS projects when cloud providers reap the profits and control the infrastructure without proportional contributions back to the projects that they exploit?**

To counter this, companies like MongoDB and Elastic adopted SSPL to protect their business from cloud providers extracting value without reinvesting. Redis initially took a different approach, creating Redis Stack as a separate distribution with a different license for advanced features. While this safeguarded innovation, it also split the developer experience and slowed progress on core Redis. What we really needed was a way to enhance Redis at its core without maintaining two separate tracks---Redis Community Edition and Redis Stack.

After I joined the company, and a year of evaluating alternatives, in March 2024, we decided to [move Redis to the SSPL license](https://redis.io/blog/redis-adopts-dual-source-available-licensing/). This achieved our goal---AWS and Google now maintain their own fork---but the change hurt our relationship with the Redis community. SSPL is not truly open source because the Open Source Initiative clarified it lacks the requisites to be an OSI-approved license.

Following our license change, in November of 2024 Salvatore Sanfillipo (antirez) decided to [rejoin Redis](https://antirez.com/news/144) as a developer evangelist. Collaborating with Salvatore on new capabilities, company strategy and community engagement has been a true privilege that has made a major impact that will pay dividends into the future.

With guidance from Salvatore, our CTO, Benjamin Renaud, and our core developers, we have made some key decisions to improve Redis going forward:

1. Adding the OSI-approved [AGPL](https://www.gnu.org/licenses/agpl-3.0.en.html) as an additional licensing option for Redis, starting with Redis 8 (available now);
2. Introducing vector sets---the first new data type in years---created by Salvatore;
3. Integrating Redis Stack technologies, including JSON, Time Series, probabilistic data types, Redis Query Engine and more into core Redis 8 under AGPL;
4. Delivering over 30 performance improvements with up to 87% faster commands and 2x throughput; and
5. Improving community engagement, particularly with client ecosystem contributions.

Redis 8 with its new capabilities and with AGPL licensing demonstrates our ongoing commitment to making a platform developers love, while advancing Redis according to Salvatore's original vision.
