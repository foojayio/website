---
title: "2022 in Retrospective | Foojay.io Today"
slug: "2022-in-retrospective"
date: "2023-01-10T08:19:43+00:00"
lastmod: "2023-01-10T08:19:44+00:00"
description: "Years when one changes jobs are always challenging but very interesting; 2022 was no different. What are your insights into last/this year?"
canonical: "https://blog.frankel.ch/2022-retrospective/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2023/01/new-years-day-g64dc8b542.jpg"
categories:
  - "Opinion"
tags:
related_posts:
frozen: false
---

2022 is over, and not a moment too soon. I'll never forget it: some of my friends had to flee their own country; others are fighting for their freedom as I write this post. I hope they will be safe and that their wishes will come true in 2023.

On the personal and technical side, here's a summary of the past year from my perspective.

Job change {#h2-0-job-change}
-----------------------------

<img fetchpriority="high" decoding="async" class="alignright size-full wp-image-61589" src="/images/posts/2023/01/2022-in-retrospective/apache-apisix.jpeg" alt="" width="300" height="300">

<br />

First and foremost, I changed jobs. I worked for [Hazelcast](https://hazelcast.com/) for 3½ years.

However, I started to become dissatisfied with the company's direction and my position within it.

I looked for a couple of months for my next gig. In February, I started to work on [Apache APISIX](https://apisix.apache.org/):
> Full Lifecycle API Management
>
> API Gateway, Ingress Controller, etc.
>
> Apache APISIX provides rich traffic management features like Load Balancing, Dynamic Upstream, Canary Release, Circuit Breaking, Authentication, Observability, etc.

When I joined Hazelcast, I had to *understand* distributed systems and concepts related to them: clocks, order, split brain, leader, etc. In comparison, an API Gateway such as APISIX is pretty simple. Hence, my focus is more on *learning* underlying technologies:

* [NGINX](https://www.nginx.com/)
* [OpenResty](https://openresty.org/en/)
* [Lua](https://www.lua.org/)
* [Kubernetes](https://kubernetes.io/)
* [Ingress](https://kubernetes.io/docs/concepts/services-networking/ingress/)
* etc.

It means I had to shift the subjects I write about: you won't find any Java and hardly any Kotlin.

My blog {#h2-1-my-blog}
-----------------------

In 2022, I published 50 blog posts on : one each week on Sunday, but on Christmas and the New Year - for obvious reasons. Here are the top 5 most viewed pages:

| Rank |                                           Post                                           | Views  |  %   | Avg. time on page |
|------|------------------------------------------------------------------------------------------|--------|------|-------------------|
| 1    | [Hard things in Computer Science](https://blog.frankel.ch/hard-things-computer-science/) | 26,123 | 5.29 | 00:05:35          |
| 2    | [A poor man's API](https://blog.frankel.ch/poor-man-api/)                                | 21,297 | 4.32 | 00:05:04          |
| 3    | [Discussing Backend For Front-end](https://blog.frankel.ch/backend-for-frontend/)        | 18,626 | 3.78 | 00:04:57          |
| 4    | [Homepage](https://blog.frankel.ch/)                                                     | 18,062 | 3.66 | 00:00:59          |
| 5    | [A list of cache providers](https://blog.frankel.ch/choose-cache/2/)                     | 14,646 | 2.97 | 00:03:32          |

Two things are of interest:

1. The 4^th^ most viewed page is the homepage
2. The 5^th^, about cache providers, was published in October 2021. It's a long tail, indeed!

Views are broken down as the following during the year:

image:analytics.jpg\[Google Analytics excerpt,840,204\]

You can notice a couple of huge spikes. They are [Hacker News](https://news.ycombinator.com/from?site=frankel.ch) front page occurrence. I advertise on when I believe the post has potential; sometimes, others do. Most viewed posts correlate somewhat with referrals from Hacker News.

| Rank |                                                         Post                                                         | Views  |   %   |
|------|----------------------------------------------------------------------------------------------------------------------|--------|-------|
| 1    | [A poor man's API](https://blog.frankel.ch/poor-man-api/)                                                            | 10,924 | 38.57 |
| 2    | [Discussing Backend For Front-end](https://blog.frankel.ch/backend-for-frontend/)                                    | 8,128  | 28.69 |
| 3    | [Hard things in Computer Science](https://blog.frankel.ch/hard-things-computer-science/)                             | 4,072  | 14.38 |
| 4    | [What I miss in Java, the perspective of a Kotlin developer](https://blog.frankel.ch/miss-in-java-kotlin-developer/) | 2,180  | 7.70  |
| 5    | [Toying with Kotlin's context receivers](https://blog.frankel.ch/kotlin-context-receivers/)                          | 1,023  | 3.61  |

Cross-posting {#h2-2-cross-posting}
-----------------------------------

2022 also saw consistent cross-posting on third-party content aggregator sites, which allow linking to the original post. As a reminder, Google (and other search sites) flag similar content as duplicate.

If the aggregator site has more "page rank" than your site, then the content on the latter will be marked as such. The `rel canonical` attribute is the way to tell Google: here's the original content, don't flag either as duplicate. I only post on sites with such a feature.

That being said, here are my followers depending on the site:

* [Medium](https://medium.com/@nfrankel): 564
* [Dev.to](https://dev.to/nfrankel): 1,838
* [Hashnode](https://hashnode.com/@nfrankel): 80
* [DZone](https://dzone.com/users/293758/nfrankel.html): no follow feature
* [Hacker Noon](https://hackernoon.com/u/nfrankel): no follow feature
* [foojay.io](https://foojay.io/today/author/nicolas-frankel/): no follow feature either

It's interesting to see that though I thought dev.to focused on web and front-end, I got many followers though I rarely write on such subjects. Conversely, I still need to understand why my follower base on Hashnode doesn't take off.

Public speaking {#h2-3-public-speaking}
---------------------------------------

[![](/images/posts/2023/01/2022-in-retrospective/Screenshot-2023-01-02-at-15.32.08-752x1024.png)](https://twitter.com/nicolas_frankel/status/1608043464146710528)

Indeed, 2022 was not my best year in terms of conference attendance. Here's a comparison of the previous years:

<img decoding="async" class="aligncenter wp-image-61591 size-medium" src="/images/posts/2023/01/2022-in-retrospective/public-speaking-700x438.png" alt="" width="700" height="438">

<br />

Raw numbers are a bit misleading: Because of Covid, I spoke at many events online in 2020 and 2021. It's **much** easier to do so than traveling to another country (or continent!); on the other hand, engagement is much lower, not to mention the lack of social interactions.

Anyway, 2022 numbers are below 2019's, the last "regular" year. Despite this, I'm ok with it: 2019 was hard on me, and conference attendance is more of a marathon than a sprint. I think I've found the sweet spot between public speaking and other activities.

Regarding content, I mainly did two talks:

1. [Evolving your APIs](https://www.youtube.com/watch?v=uKV31NImnuI)
2. [Chopping the monolith](https://www.youtube.com/watch?v=dVhzMEn2K1I)

It's below the number of talks I usually try to achieve, but they were pretty popular, especially the first one. I'm working on more diversity for 2023.

GitHub contributions {#h2-4-github-contributions}
-------------------------------------------------

As a Developer Advocate, my [GitHub contributions](https://github.com/nfrankel/) are different from the ones of a regular software developer, especially one working on Open Source.

<img loading="lazy" decoding="async" class="size-medium wp-image-61592 aligncenter" src="/images/posts/2023/01/2022-in-retrospective/github-analytics-700x480.png" alt="" width="700" height="480">

<br />

Most of my contributions are commits because I'm the only one working on my demos. Furthermore, I regularly update them with the latest version of whatever dependency I use. I'm trying to open issues on Apache APISIX since I'm a huge user. Eating one's dog food is a great way to uncover either bugs or usability improvements. Finally, pull requests are for blog posts and reviews for my colleagues' blog posts.

Conclusion {#h2-5-conclusion}
-----------------------------

Years when one changes jobs are always challenging but very interesting; 2022 was no different. My resolution for 2023 are:

* To deepen my understanding of the Apache APISIX ecosystem
* Write as many blog posts as in 2022 - it's hard to do better, anyway
* Design at least three new talks

What are yours?

*Originally published at [A Java Geek](https://blog.frankel.ch/2022-retrospective/) on January 8^th^, 2023*

*[HN]: Hacker News
