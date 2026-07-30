---
title: "Kotlin adoption inside ING, five years later"
slug: "ing-kotlin-adoption-five-years"
date: "2025-07-02T06:21:47+00:00"
lastmod: "2025-07-02T06:24:15+00:00"
description: "TL;DR : 5 years after its introduction Kotlin adoption inside ING keeps growing year after year, with a current adoption rate of just over 11%."
canonical: "https://medium.com/ing-blog/kotlin-adoption-inside-ing-5-years-later-df6421b14dc4"
authors:
  - "jlengrand"
image: "https://foojay.io/wp-content/uploads/2022/06/chemical-scientist-test-chemistry-chemist-student.jpg"
categories:
  - "Java"
  - "Kotlin"
  - "Opinion"
  - "Use Cases"
tags:
related_posts:
frozen: false
---

*TL;DR: Five years after its introduction Kotlin adoption inside ING keeps growing year after year, with a current adoption rate of just over 11%. We were also featured as one of the user stories for the KotlinConf 2025 Keynote.*

Five years ago, [Hielke](https://medium.com/@Frevib) and I wrote one of the first blogs of this account about being [the first team to put Kotlin on production at ING](https://medium.com/ing-blog/introducing-kotlin-at-ing-a-long-but-rewarding-story-1bfcd3dc8da0). The article generated some interest and to this day it is still present as one of the [Kotlin website case studies](https://kotlinlang.org/lp/server-side/case-studies/).

After a few years outside of the company, I joined back a few months ago to lead the Developer Relations program. Being just as big of a fan of Kotlin as I was five years ago, one of the first thing I did was help build the storyline when JetBrains contacted us to be one of the main case studies of [their KotlinConf 2025 Keynote](https://youtu.be/F5NaqGF9oT4?si=pn1e9dC0Vhb31W1-&amp;t=3397) 🎉. Now, this seems like the perfect time to investigate what the internal picture looks like now! Let's dive into it together 😊.

Before looking into the code, let's look at the Kotlin ecosystem of ING! There is quite a lot to say, which is why I start by this. For reference, there are about 20000 people working in the Tech structure of ING. This includes developers, but also many other technical roles (e.g DevOps, product, machine learning, ....).

First, ING is one of the driving forces of the Kotlin community in a few places. For example, the [ING Kotlin Summit](https://www.meetup.com/tech-meetups-ing/events/306698283/?eventOrigin=group_upcoming_events) that is yearly organised in June (today, actually) with over 300 participants and an impressive lineup of speakers! You can find many of the talks on [our Youtube channel](https://www.youtube.com/@weareINGtech). The event is fully hosted by ING, but registration was open to the public too. This year is even co-sponsored by Google and Xebia.

The [Bucharest Kotlin User Group](https://www.meetup.com/kug-bucharest/) was also originally created by ING engineers. Similarly, I am one of the organisers of the [Virtual Kotlin User Group](https://www.meetup.com/virtual-kotlin-user-group/) (though I have to admit, I haven't organised a meetup in a while. It's active again since January, join us!).

Internally, things have evolved as well. Our major internal frameworks have added support for Kotlin, reflecting the internal demand. But this adoption is not only visible internally, you can also see it out there with our [Open-Source repositories](https://github.com/orgs/ing-bank/repositories). You can find several Kotlin repositories there, most of it related to [smart contracts](https://github.com/ing-bank/zkflow).

[Baker](https://medium.com/ing-blog/how-to-apply-domain-driven-design-using-baker-84e6f773c7a3), our well-known micro-service orchestration framework now also supports Kotlin. Those past years, companies like Etsy or Workday have helped contribute to Baker. Last but not least, some of our critical services are now fully written in Kotlin. For example, the main payment gateway for all Benelux payments is written in Kotlin.

Alright, by now you can see that ING clearly has interest in Kotlin. But let's look at the reality of the field. How much of the internal codebase contains Kotlin by now? That's the real question we want an answer for!

The analysis below was created using [this Azure API endpoint](https://learn.microsoft.com/en-us/javascript/api/azure-devops-extension-api/projectlanguageanalytics). Note that we added custom values to the API call. For example, *isSlumbering* is a custom value we have defined as "180 days since last change" (which coincides with our migration to Azure Repos). It returns a huge JSON array that looks like this:

![Output of the Azure API request](/images/posts/2025/07/ing-kotlin-adoption-five-years/Screenshot-2025-06-24-at-10.26.26-331x510.png)

In the rest of that post, we will assume that the results that are being sent to us by the API are correct and that we can trust the numbers we see.

Using a minimum amount of Kotlin code, we can easily parse this output and start diving into patterns. Here is a minimal example:

![Parsing the Azure API response for further processing](/images/posts/2025/07/ing-kotlin-adoption-five-years/Screenshot-2025-06-24-at-10.28.22-404x510.png)

In the rest of this article, I will define a Kotlin repository as any Git repository that contains Kotlin code. I've spent the past weeks investigating the entirety of our codebase, to look just how strong is the Kotlin adoption within the company. Let's put some bases in place first: **The ING organisation totals just over 90k internal repositories.** (That's A LOT of code by the way! Over four repositories per engineer in the company 😅).

Before we can dive into Kotlin further, we want to do some serious cleanup. Out of these repositories, we will remove:

* Empty repos
* Forks
* Inactive repos (aka, haven't been updated since we migrated).

![The share of empty repos inside our org](/images/posts/2025/07/ing-kotlin-adoption-five-years/3-700x467.webp)

We are left with a little less 20% of our original pool of repositories. This may sound like a huge number, but we have a long retention policy (storage is cheap) and inactive repositories are a combination of old projects we decommissioned over the years, team and user experiments, and many more.

Out of those, we want to discriminate further and only keep JVM repositories.

![Filtering JVM repos from the active repository baseAbout a third of our repositories are JVM](/images/posts/2025/07/ing-kotlin-adoption-five-years/4-700x353.webp)

**Out of our active repositories, we find that 29% are JVM repositories.** This means that about a 1/3 of our Git activity is done on top of the JVM in one way or another. This also means we have on average one JVM repository for three developers at ING (which makes sense, given this includes all flavors of engineering). This is even easier to understand given that for each code repository, we typically have at least one separate supporting configuration repo.

By the way, in case you are interested: this is the code used to create this graph. Quite neat and expressive, isn't it?

![Kandy and Kotlin Notebooks, for fun and learning](/images/posts/2025/07/ing-kotlin-adoption-five-years/6-700x450.webp)

The share of Kotlin code at ING {#h2-0-the-share-of-kotlin-code-at-ing}
-----------------------------------------------------------------------

Now, the next step is obvious right? We have segregated all of the repositories containing JVM code. Let's look into the proportion between flavors!

*Note: For this analysis, repositories can contain more than one flavor of JVM language. For example, a repository can both contain Java and Kotlin code.*

Out of active JVM repos:

* \~8% contain Kotlin
* \~7% contain Scala
* \~5% contain Groovy
* 0.06% contain Clojure
* 92% contain Java

![Flavors of language across our JVM repos](/images/posts/2025/07/ing-kotlin-adoption-five-years/7-700x467.webp)

We can make a few observations here:

· It seems we have basically no Clojure at ING. (That brings closure 😝).

· We have more repos containing Kotlin than Scala! This is a little surprising to me, because Scala used to be a big thing at ING a few years back.

· The huge majority of code still contains Java.

This is not very surprising, to be fair. ING is a Java powerhouse and most of the production code has been running for many years! Looking at all the history, we see that **the Kotlin penetration is at just about 8% of the active repositories in our codebase**. This is not a massive groundbreaking shift, but it definitely cannot be ignored when given the fact that all of the work happens on a voluntary basis. Our internal language of choice is still Java by default today and that's where all of our internal documentation and technical standards are pointing towards.

Looking at trends over time {#h2-1-looking-at-trends-over-time}
---------------------------------------------------------------

Let's dive slightly deeper into trends, using the data we have at our disposal: Let's look at the date of last update for each repository per flavor of JVM. This will give us an idea of the language usage over time and will help us see what general trends are in the more recent years.

![Activity trend per JVM flavor over time](/images/posts/2025/07/ing-kotlin-adoption-five-years/8-700x467.webp)

In this graph, we can clearly see an upward trend for Kotlin year after year from a negligible amount to the around 10% that it has become today. We can also see that since 2024, **Kotlin has surpassed both Groovy and Scala in terms of activity becoming the second most used JVM language inside the company.**

How many flavors does a repository contain? {#h2-2-how-many-flavors-does-a-repository-contain}
----------------------------------------------------------------------------------------------

Let's dive just a bit deeper yet! One of the additional data we have and we haven't used yet is the **share of a specific language inside a repository** (either in terms of percentage of files, or bytes).

Let's look into the share of a language on average in a repository, for repositories that have been updated this year:

![Just how much share of a language does a repo contain on average](/images/posts/2025/07/ing-kotlin-adoption-five-years/9-700x467.webp)

This is an interesting graph! Repositories containing Kotlin contain over 70% Kotlin code (including docs, config files, ...). This leads to believe that **Kotlin repositories contain mostly Kotlin code**.

Scala and Groovy repositories, however, seem to be used in hybrid repositories.

And what about newer projects? {#h2-3-and-what-about-newer-projects}
--------------------------------------------------------------------

Now, there is one thing in the current data that I am dearly missing: we have information about the last update of a given repository, but not its creation date. My main question when I started this analysis was: **given a new project starting, how large is the share of repositories for which teams decide to use Kotlin.**

For this, I had to change methodology: I cloned every single repository in our environment, looked at its first Git commit and sorted them by date. (Yep, it seems there no is other way to find the inception date of a repo. This took me over four days of processing).

One major difference with the data before though, is that using this method I can only access Inner Source repositories (code which teams have internally opened access for). *The resulting numbers will not be a complete indication of what's happening internally, but rather visible internal trends.*

![Trend of Kotlin activity in volume over time, for our inner source code](/images/posts/2025/07/ing-kotlin-adoption-five-years/10-700x467.webp)

Using this dataset, I find that **just over 11% of inner-source repositories that are created internally are pure Kotlin repositories as of February 2025**. The very nice thing though, is that we clearly see the organic growth of the language year after year.

A message from the community {#h2-4-a-message-from-the-community}
-----------------------------------------------------------------

Before closing this blog, I want to finish with a few testimonials from our internal engineers. While preparing for the Keynote video and this blog, I've been asking around _why_ people were choosing for Kotlin over another JVM language. What made the language so compelling. Here are a few of their answers (please get ready for dry and nerdy developer humor!).

![](/images/posts/2025/07/ing-kotlin-adoption-five-years/11-700x80.webp)  
![](/images/posts/2025/07/ing-kotlin-adoption-five-years/12-700x78.webp)  
![](/images/posts/2025/07/ing-kotlin-adoption-five-years/13-700x70.webp)  
![](/images/posts/2025/07/ing-kotlin-adoption-five-years/14-700x136.webp)

A word of conclusion {#h2-5-a-word-of-conclusion}
-------------------------------------------------

![Julien after spending much time staring at his screen making graphs](/images/posts/2025/07/ing-kotlin-adoption-five-years/15-700x467.webp)

I think that was an worthy experiment and compiling those numbers has been a lot of fun! These charts are powered by [Kandy](https://kotlin.github.io/), [Kotlin Notebooks](https://kotlinlang.org/docs/kotlin-notebook-overview.html) and [KGit](https://github.com/sya-ri/KGit).

It's quite fair to say that Kotlin is very present within ING, and that it has grown over the past few years. And now that yours truly is back, I'm sure it can only go up more! Especially given how much love JetBrains is giving to the [backend developers with their Keynote announcements this year](https://lengrand.fr/kotlinconf-2025-is-a-real-bowl-of-fresh-air-for-backend-devs/)!

You can find all of the code I used to generate that data [**here**](https://github.com/jlengrand/kotlin-repo-analysis)**.**

*Thanks to* [*Kevin van der Vlist*](https://www.linkedin.com/in/kevinvandervlist/) *for his help sparring to get the right data for this blog, as well as his review and the great Closure pun 🙂*
