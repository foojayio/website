---
title: "2023 in Retrospective"
slug: "2023-in-retrospective"
date: "2024-01-16T07:44:39+00:00"
lastmod: "2024-01-16T07:44:40+00:00"
description: "Last year, I wrote my first yearly retrospective. I liked the experience, so I'm trying one more time. Let the future decide if it will become a trend or not."
canonical: "https://blog.frankel.ch/2023-retrospective/"
authors:
  - "nicolas-frankel"
image: "https://foojay.io/wp-content/uploads/2023/12/apisix.png"
categories:
  - "Uncategorized"
tags:
related_posts:
frozen: false
---

**Last year, I wrote my first [yearly retrospective](https://blog.frankel.ch/retrospective/). I liked the experience, so I'm trying one more time. Let the future decide if it will become a trend or not.**

Before diving into our safe technological world, my thoughts go to Ukraine, to my friends who had to flee their own country, to other friends who fought on the front to defend it from an imperial power, and to all victims of an old kleptocrat who clings to power despite the cost to others. The free world needs to support Ukraine more. I hope 2024 will be the year of Ukrainian victory.

The revolution {#h2-0-the-ai-revolution}
----------------------------------------

Last year, I kept the post focused on what I did. However, AI is pervasive in our tech world, if not the whole world, and deserves a dedicated section. Nobody even remotely connected to tech can ignore the buzz surrounding AI. Even friends and families who don't work near tech probably talk about it. So far, I didn't weave any AI-related thing into any of my talks, despite the huge incentive to do so: having AI in your proposal vastly increases your chances of being selected.

And yet, it doesn't mean that I'm not playing with it on a personal level. Here's my experience so far.

* I've been playing with Dall-E as a use case to try out web development in Rust. I found the results mind-blowing. However, the limitations are enormous; it seems the size is always square, and the set of possible dimensions is limited.
* I've used ChatGPT in several areas. First, I tried to generate conference abstracts. I fed an entire blog post and asked ChatGPT to turn it into an abstract. Despite my lack of prompt engineering skills, I've found results severely lacking. Abstracts felt artificial, like any content created with ChatGPT, but worse, the abstract revealed too much or not enough.I also tried to use it to refactor two nested `if else` Kotlin statements, one nested in the other, to the null-safe `let` construct. The first result didn't compile; the second one forgot a branch. It led me to the correct solution, though.

  Finally, I recently started using GitHub Copilot as an extension inside of IntelliJ IDEA. Its behavior is fascinating: most of the time, it does nothing, but once in a while, it offers a snippet of a couple of lines, which is either entirely correct or at least very close to the target solution. Even better, the suggestions seem to be more frequent and even more relevant with time.

All in all, I'm far from impressed by the current state of AI. However, I like Copilot a lot: I prefer rare advice, which is relevant, than the opposite.

Technical content {#h2-1-technical-content}
-------------------------------------------

Next February will mark the two-year milestone that I'm working for: [API7.ai](https://api7.ai/) on [Apache APISIX](https://apisix.apache.org/). I'm still very pleased about both. It allows me to do things I like a lot, such as writing posts and giving talks.

In 2023, I published fifty blog posts on [this blog](https://blog.frankel.ch/): one each week on Sunday, but on Christmas and the New Year - for obvious reasons. Here are the top most viewed pages:

| Rank |                                                    Post                                                    | Views  | Avg. time on page |
|------|------------------------------------------------------------------------------------------------------------|--------|-------------------|
| #1   | [Leverage the richness of HTTP status codes](https://blog.frankel.ch/leverage-richness-http-status-codes/) | 14,848 | :29               |
| #2   | [A list of cache providers](https://blog.frankel.ch/choose-cache/2/)                                       | 12,770 | 1:15              |
| #3   | [Calling Rust from Python](https://blog.frankel.ch/rust-from-python/)                                      | 8,780  | :52               |
| #4   | [My final take on Gradle (vs. Maven)](https://blog.frankel.ch/final-take-gradle/)                          | 8,238  | 2:11              |
| #5   | [Learning by doing: An HTTP API with Rust](https://blog.frankel.ch/http-api-rust/)                         | 6,642  | 1:00              |

I continue to cross-post on different sites. Here are the numbers compared with last year's:

|                    Site                    | 2023 | 2022  |
|--------------------------------------------|------|-------|
| [Medium](https://medium.com/@nfrankel)     | 741  | 564   |
| [Dev.to](https://dev.to/nfrankel)          | 8156 | 1,838 |
| [Hashnode](https://hashnode.com/@nfrankel) | 89   | 80    |

Absolute numbers are not that interesting, but comparing them is. Interestingly, numbers on dev.to are growing wildly, while on Hashnode, they plateau. Note that other sites provide no precise follower count or no count.

Besides, I created a script to track daily metrics across sites and social media, just as I did for Apache APISIX. It displays interesting results:

![](/images/posts/2024/01/2023-in-retrospective/social-metrics-1024x768.webp)

As above, numbers are much less important than the trend. Growth visibly happens mainly on dev.to and Bluesky for reasons I cannot fathom (yet?).

Finally, I started a weekly newsletter, unoriginally named A Java Geek weekly. So far, I've written a [couple of them](https://blog.frankel.ch/java-geek-weekly). I list the posts and videos I found interesting during the week. Note that they contain the same links I post on [LinkedIn](https://www.linkedin.com/in/nicolasfrankel/), [Mastodon](https://mastodon.top/@frankel), and [BlueSky](https://bsky.app/profile/nfrankel.bsky.social), with a bit more content, either a summary or my opinion.

Open Source contributions {#h2-2-open-source-contributions}
-----------------------------------------------------------

In 2023, after over twenty years in software, I finally became [an Apache committer](https://community.apache.org/contributors/becomingacommitter.html)! I'm both excited and impressed; it's like belonging to a group of mythical beings I'd only heard about.

Of course, working on Apache APISIX helped a lot. Yet, the exciting bit is that all my contributions are entirely unrelated to code; they are blog posts, reviews of blog posts written by others, issues, comments on issues, etc.

Here's the GitHub summary:

[](https://github.com/nfrankel/)

[

<img decoding="async" class="aligncenter wp-image-105579 size-medium" src="/images/posts/2024/01/2023-in-retrospective/github-contribs-700x482.webp" alt="" width="700" height="482">

](https://github.com/nfrankel/)

<br />

Conclusion {#h2-3-conclusion}
-----------------------------

Last year, my resolutions were:
> * To deepen my understanding of the Apache APISIX ecosystem
> * Write as many blog posts as in 2022 - it's hard to do better, anyway
> * Design at least three new talks

I fulfilled the two first goals but unfortunately failed the last item. I have only created two new talks, both based on previous posts. I'll keep them secret for now; they should appear soon on my [Speaking](https://blog.frankel.ch/speaking/) page in case they are selected.

I lack imagination, so I'll keep the same goals for this year as last year's and add exploring the API7.ai ecosystem as well. Let's see how it fares. Happy New Year!

*** ** * ** ***

*Originally published on [A Java Geek](https://blog.frankel.ch/2023-retrospective/) on January 7^th^, 2024*

*[AI]: Artificial Intelligence
*[CFP]: Call For Paper
