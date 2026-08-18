---
title: "2024 in Retrospective: Nicolas Frankel"
date: "2025-01-06T08:59:26+00:00"
lastmod: "2025-01-06T08:59:27+00:00"
description: "For the first article of 2025, I'm continuing my retrospective series!"
authors:
  - "nicolas-frankel"
image: "social-metrics-532x510-1.jpeg"
categories:
  - "Opinion"
related_posts:
  - "2023-in-retrospective"
  - "2022-in-retrospective"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
frozen: false
---

For the first article of 2025, I'm continuing my retrospective series!

As last year, before diving in into the report proper, I'd like to remind you that Ukraine is still fighting for its survival after nearly 3 years and countless Russian war crimes. The civilized world can't allow rewarding the invasion of a sovereign country, lest we see more invasions. Remember that in 1938, in Munich, European "leaders" acknowledged Hitler's annexation of Czechoslovakia's Sudetenland. It didn't prevent Nazi Germany to invade the rest of Europe a year later; worse, it emboldened it!

Please help Ukraine and help the civilized world by doing so. Here are two organizations I trust: [Come Back Alive](https://savelife.in.ua/en/) and [Dzyga's Paw](https://dzygaspaw.com/).

## I lost my job (and found a new one)

First and foremost, I suffered quite a blow this year. It was a complete surprise when my company unexpectedly let me go due to budget reasons. To tell you I was unhappy is quite an understatement. It was also a complete surprise, as my CEO had assured me everything was okay a few months before he notified me.

Upon receiving the news on a Thursday morning in mid-August, I didn't let it deter me. I took a one-day break and immediately started my job search on Friday, determined to find a new opportunity. I thought I would get a new job quickly. However, my optimism met reality—it took me 3.5 months to return to work. Here's a couple of stats that may be interesting if you're facing the same issue:

* I listed 147 job offers over the months; including asking people in my network
* I submitted my application to 130 of them
* I got 55 answers, counting automated emails
* On a couple of submissions, I'm still waiting for the next step
* I entered the interview process of 13 companies
* In the end, I received two offers, one for a consultant role and the other for a Developer Advocate role. After careful consideration, I finally chose the latter, working for [Loft Labs](https://www.loft.sh/). It provides [vCluster](https://www.vcluster.com/), a virtualization solution for Kubernetes clusters. Here's my first [blog post on vCluster](https://blog.frankel.ch/cluster-wide-crds/).

One additional fun fact regarding the delay in getting answers: It's not uncommon that I received them two months after my application.

All in all, it's safe that the current economic situation is bleak. Unfortunately, it impacts IT just as well. Worse, developer advocacy has lost a lot of its appeal as companies are looking for short-term ROI; developer advocates are a long-term investment.

We must bite the bullet and hope for better times.

## I'm using more and more AI

I used AI more than a couple of times this year.

Most companies require a cover letter to submit your application. It's completely irrelevant in this day and age, especially in our field. If your HR department insists on wasting my time, don't be irritated because I'm using an IT solution to solve this issue. Pointing to the job description, attaching your CV, and asking your favourite generative AI tool to write the cover letter is dead simple.

I had to learn about vCluster and refresh my knowledge of Kubernetes. I used and experimented with generative AI to help me migrate my [OpenTelemetry demo](https://github.com/nfrankel/opentelemetry-tracing) to Kubernetes. It was not flawless, but it led me in the correct direction more than once. I used both GitHub Copilot and OpenAI in their paid versions. Compared to last year, I think I got better results with the latter; Copilot is not well integrated with IntelliJ IDEA.

I'm an avid learner of many different subjects. So far, my go-to sites were Wikipedia and Wiktionary (for languages). This approach falls short when you have no clue what to search for. I started to rely more and more on OpenAI for this: So far, I'm happy about the results.

Finally, I'm not a graphic designer, and I spend a lot of time searching for free illustrations for my blog posts on image banks. I prefer to spend less time on OpenAI to describe what I want. I don't have strong opinions, so it gives me good enough results.

## Technical content

I published fifty-one blog posts this year on [this blog](https://blog.frankel.ch/): one weekly on Sunday, but one between Christmas and the New Year for obvious reasons. Here are the top most viewed pages in 2024:

| Rank |                                                                 Post                                                                  | Views | Avg. time on page |
|------|---------------------------------------------------------------------------------------------------------------------------------------|-------|-------------------|
| #1   | [A list of cache providers](https://blog.frankel.ch/choose-cache/2/)                                                                  | 5,901 | 1:02              |
| #2   | [OpenTelemetry Tracing on Spring Boot, Java Agent vs. Micrometer Tracing](https://blog.frankel.ch/opentelemetry-tracing-spring-boot/) | 5,845 | 1:03              |
| #3   | [Using my new Raspberry Pi to run an existing GitHub Action](https://blog.frankel.ch/raspberry-pi-github-action/)                     | 4,985 | :45               |
| #4   | [Multiple Spring Boot applications in the same project](https://blog.frankel.ch/multiple-spring-boot-apps-same-project/)              | 4,421 | :40               |
| #5   | [My final take on Gradle (vs. Maven)](https://blog.frankel.ch/final-take-gradle/)                                                     | 4,132 | 1:59              |

I continue to cross-post on different sites. Here is my followers' count on the sites that provide metrics:

|                    Site                    |  2024  | 2023  | 2022  |
|--------------------------------------------|--------|-------|-------|
| [Medium](https://medium.com/@nfrankel)     | 1,031  | 741   | 564   |
| [Dev.to](https://dev.to/nfrankel)          | 17,126 | 8,156 | 1,838 |
| [Hashnode](https://hashnode.com/@nfrankel) | 104    | 89    | 80    |

The trend I noticed last year is confirmed: wild growth on dev.to and close to plateau on Hashnode.

## Comparing social media

My metrics tracking script is still working nicely.

{{< img src="social-metrics-532x510.jpeg" class="aligncenter size-medium" width="532" height="510" >}}

Do I need to mention the crazy [Bluesky](https://bsky.app/profile/frankel.ch) growth? Note that I moved my handle from `@nfrankel.bsky.social` to `@frankel.ch`.

On the other hand, numbers on Twitter (I won't call it X) are dwindling, to say the least. Worse, after the US presidential elections, I noticed that I lost hundreds of followers; the count went back to around what it was in summer. Even worse, the number of followers doesn't translate into interactions.

For example, I've published the same content on Twitter, Bluesky, Mastodon, and LinkedIn. I'll leave Mastodon out since it doesn't offer metrics on the content.  

Here they are:
> Another year of [#conferences](https://twitter.com/hashtag/conferences?src=hash&ref_src=twsrc%5Etfw) in one picture. Thanks everybody who invited me. I wish 2025 will be even better 🎆🎆🎆 [pic.twitter.com/PJJMNm9NxO](https://t.co/PJJMNm9NxO)
>
> — Nicolas Frankel 🇺🇦🇬🇪 (@nicolas_frankel) [December 31, 2024](https://twitter.com/nicolas_frankel/status/1874143229198696786?ref_src=twsrc%5Etfw)
> Another year of #conferences in one picture. Thanks everybody who invited me. I wish 2025 will be even better 🎆🎆🎆
>
> [\[image or embed\]](https://bsky.app/profile/did:plc:lho243ntrkr6h4ohtvk3lr4x/post/3lemmp3vffk2y?ref_src=embed)
>
> — Nicolas Fränkel 🇺🇦🇬🇪 ([@frankel.ch](https://bsky.app/profile/did:plc:lho243ntrkr6h4ohtvk3lr4x?ref_src=embed)) [December 31, 2024 at 5:57 PM](https://bsky.app/profile/did:plc:lho243ntrkr6h4ohtvk3lr4x/post/3lemmp3vffk2y?ref_src=embed)

We can't compare LinkedIn since it's mostly about people you know personally; there's a higher social incentive to interact with others. When I wrote this post, I had slightly more likes on Twitter (13) than on Bluesky (11), but with more than ten times the followers on the former. Twitter is going down, and that's without even mentioning all the alerts that it has become a massive vector of hate and disinformation.

I recommend keeping your account if you already have one there, but new investments are a total loss of both time and money.

## Summary

My goals are evident this year. I've joined a new company, so I need to learn more about the product, create new related content, design new talks, submit them, and be selected. In the meantime, feel free to invite me to your meetup and user group.

Happy New Year!

*Originally published at [A Java Geek](https://blog.frankel.ch/2024-retrospective/) on January 5^th^, 2025*
