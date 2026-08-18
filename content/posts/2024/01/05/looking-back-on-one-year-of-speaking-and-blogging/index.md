---
title: "Looking Back on One Year of Speaking and Blogging"
date: "2024-01-05T17:49:04+00:00"
lastmod: "2024-01-05T17:49:05+00:00"
description: "2023 was an adventurous year for me: I came into my blogging rhythm, blogging every one to two weeks, resulting in 39 articles, many of them on Foojay.io."
authors:
  - "johannes-bechberger"
image: "image-15-2000x1333-1.png"
categories:
  - "Trip Reports"
tags:
related_posts:
  - "asyncgetcalltrace-reworked-frame-by-frame-with-an-iterative-touch"
  - "book-review-practical-design-patterns-for-java-developers"
  - "foojay-podcast-14"
frozen: false
---

**2023 was an adventurous year for me: I came into my blogging rhythm, blogging every one to two weeks, resulting in 39 articles, many of them on Foojay.io, spoke at my first conferences, around 14 overall, 22 if you include JUGs and online conferences, and continued working on my [IntelliJ plugin](https://mostlynerdless.de/blog/2023/12/04/profiling-maven-projects-with-my-intellij-profiler-plugin/), as well as my proposal for a [new profiling API](https://openjdk.org/jeps/435).**

This article is a recollection of the year's highlights. If you want a complete list of my presentations, visit my [Talks](https://mostlynerdless.de/talks/) page or the [Presentations](https://github.com/SAP/SapMachine/wiki/Presentations) page in the SapMachine Wiki.

Before this year, I only gave a few presentations at my local hacker conference, [Gulaschprogrammiernacht](https://entropia.de/GPN), and two at local user groups. But then, at the end of December 2022, [Abby Bangser](https://www.infoq.com/profile/Abby-Bangser/) asked me whether I wanted to give a talk at [QCon London](https://qconlondon.com/) 2023. She apparently noticed me because I started blogging on performance topics, which only a few people do. This resulted in my first proper conference talk with the title "[Is Your Java Application Slow? Check out These Open-Source Profilers](https://www.infoq.com/presentations/profilers-open-source/)" and my InfoQ article [Unleash the Power of Open Source Java Profilers: Comparing VisualVM, JMC, and async-profiler](https://www.infoq.com/presentations/profilers-open-source/). I gave a version of this talk at almost every conference I attended.
![](https://mostlynerdless.de/wp-content/uploads/2023/12/image-15-2000x1333.png)

QCon London was a great experience, albeit I traveled via TGV and Eurostar on my birthday. It was only the second time that I'd been to London, so it was great to explore the city (and have my first article, [Writing a Profiler in 240 Lines of Pure Java](https://mostlynerdless.de/blog/2023/03/27/writing-a-profiler-in-240-lines-of-pure-java/), on the top of the hacker news front page), visiting the [British Museum](https://www.britishmuseum.org/) and walking along the Themes:  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1059-2000x1500.jpeg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1059-2000x1500.jpeg)  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1070-1500x2000.jpeg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1070-1500x2000.jpeg)  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1146-1500x2000.jpeg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1146-1500x2000.jpeg)  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1138-2000x1500.jpg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1138-2000x1500.jpg)  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1164-2000x1500.jpg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1164-2000x1500.jpg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1059-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1070-1500x2000.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1146-1500x2000.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1138-2000x1500.jpg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1164-2000x1500.jpg)

But this wasn't actually my first conference talk if you include my two 15-minute talks at [FOSDEM 2023](https://archive.fosdem.org/2023/) in February, one of which was based on my work on Firefox Profiler:

{{< youtube HWPnzbCvua0 >}}

FOSDEM is an open-source conference where a lot of different open-source communities meet:
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1012-2000x1475.jpeg)

The best thing about FOSDEM was meeting all the lovely [Foojay](https://foojay.io/) people at the Foojay dinner, many of whom I met again at countless other conferences, like [JavaZone](https://2023.javazone.no/) in September:
![](https://mostlynerdless.de/wp-content/uploads/2023/09/IMG_2100.jpg) In a bar with my fellow speakers

But more on Oslo later. Speaking at QCon London and FOSDEM was frightening, but I learned a lot in the process, so I started submitting my talks to a few conferences and user groups, resulting in my first [Tour](https://mostlynerdless.de/blog/2023/06/15/report-of-my-small-tour-deurope/)d'Europe in May/June this year:
![](https://mostlynerdless.de/wp-content/uploads/2023/06/image-6.png)

I originally just wanted to give a talk at the JUG Milano while I was there any way on holiday with two friends. Sadly, the vacation fell through due to medical reasons, but Mario Fusco offered me a stay at his place in beautiful Gorgonzola/Milan so I could visit Milan and give my talk:  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1306-2-2000x1500.jpeg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1306-2-2000x1500.jpeg)  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1288-1-1500x2000.jpeg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1288-1-1500x2000.jpeg)  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1240-1500x2000.jpeg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1240-1500x2000.jpeg)  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1288-1500x2000.jpeg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1288-1500x2000.jpeg)  
[![](https://mostlynerdless.de/wp-content/uploads/2023/06/IMG_1344-3-2000x1500.jpg)](https://mostlynerdless.de/wp-content/uploads/2023/06/IMG_1344-3-2000x1500.jpg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1306-2-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1288-1-1500x2000.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1240-1500x2000.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1288-1500x2000.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/06/IMG_1344-3-2000x1500.jpg)

It was where I gave my first presentation in Italy. It was the first time I've ever been to Italy, but I hope to return with a new talk next year.

After my stop in Italy, I spoke at a [meet-up in Munich](https://www.meetup.com/openvaluemuenchen/events/293736106/), [a small conference in the Netherlands](https://jdriven.com/full-stack-conference-2023), and gave three new talks at two small conferences in Karlsruhe. All in all, I gave eight talks in around two weeks. You can read more about this endeavor in my [Report of my small Tour d'Europe](https://mostlynerdless.de/blog/2023/06/15/report-of-my-small-tour-deurope/). This was quite exhausting, so I only gave a single talk at a user group until September. But I met someone at one of the Karlsruhe conferences who told me at a dinner a month later that I should look into a new topic...

In the meantime, I used August to go on a sailing vacation in Croatia (couch sailing with [Zelimir Cernelic](https://tupko.wordpress.com/)c) and had a great time despite some rumblings regarding my JEP:  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1938-2000x1500.jpeg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1938-2000x1500.jpeg)  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1660-2000x1500.jpeg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1660-2000x1500.jpeg)  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1928-2000x1500.jpeg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1928-2000x1500.jpeg)  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/image-16-2000x1500.png)](https://mostlynerdless.de/wp-content/uploads/2023/12/image-16-2000x1500.png)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1938-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1660-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_1928-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/image-16-2000x1500.png)

Before the vacation, I carelessly applied to a few conferences in the fall, including [JavaZone](https://2023.javazone.no/) in Oslo and [Devoxx Belgium](https://devoxx.be/). Still, I would have never dreamed of being a speaker at both in my first year as a proper speaker. Being at JavaZone in September, followed by two smaller conferences in northern Germany, was excellent, especially with all the gorgeous food and getting my first duke:
![](https://mostlynerdless.de/wp-content/uploads/2023/09/IMG_2091-2000x1500.jpeg)

You can read more on this journey in my [Report of my trip to JavaZone and northern Germany](https://mostlynerdless.de/blog/2023/09/29/report-of-my-trip-to-javazone-and-northern-germany/).

Then, in October, I went to Devoxx Belgium, meeting people like Alexsey Shipilev
![](https://mostlynerdless.de/wp-content/uploads/2023/12/F7hNsheWUAA0Y46-2000x1500.jpg) Fixing a bug with Alexsey at Devoxx Belgium; see my article [JDWP, onthrow and a mysterious error](https://mostlynerdless.de/blog/2023/10/11/jdwp-onthrow-and-a-mysterious-error/)

and eating lunch with four of the Java architects, including Brian Goetz and Alan Bateman:
![](https://mostlynerdless.de/wp-content/uploads/2023/12/image-17-2000x1500.png)

Giving a talk at such a well-known conference was a real highlight of my year:
![](https://mostlynerdless.de/wp-content/uploads/2023/12/image-19-2000x1334.png)

You can see a recording here:

{{< youtube Mxcp2khJ4fw >}}

After Devoxx, I gave my newly created talk on Debugger internals in JUG Darmstadt and JUG Karlsruhe. This is the main talk I'll be presenting, hopefully at conferences in 2024.

<figure class="wp-block-embed is-type-rich is-provider-speaker-deck wp-block-embed-speaker-deck wp-embed-aspect-16-9 wp-has-aspect-ratio">
 <div class="wp-block-embed__wrapper">
  <iframe title="Debugging Unveiled" id="talk_frame_1103537" class="speakerdeck-iframe" src="//speakerdeck.com/player/b13a5ae635ec46fd8c89f7d267d844cd" width="500" height="281" style="aspect-ratio:500/281; border:0; padding:0; margin:0; background:transparent;" frameborder="0" allowtransparency="true" allowfullscreen></iframe>
 </div>
</figure>

After these two JUGs, I went to Basel to give a talk at [Basel One](https://baselone.ch/speech.html?id=771854C5-8D91-4397-8F2D-BDC421D3CD61). After five conferences, two user groups, and eight articles, I needed a break, so I went on vacation to Bratislava, visiting a good friend there and hiking together for two days in the Tatra mountains:  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2406-2000x1500.jpeg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2406-2000x1500.jpeg)  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2468-2000x1500.jpeg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2468-2000x1500.jpeg)  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2451-2000x1500.jpeg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2451-2000x1500.jpeg)  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2506-2000x1500.jpeg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2506-2000x1500.jpeg)  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2482-2000x1500.jpeg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2482-2000x1500.jpeg)  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2392-2000x1500.jpeg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2392-2000x1500.jpeg)  
[![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2557-2000x1500.jpeg)](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2557-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2406-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2468-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2451-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2506-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2482-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2392-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2557-2000x1500.jpeg)

Then, at the beginning of November, I gave a talk at [J-Fall](https://jfall.nl) in the Netherlands, the biggest one-day conference in Europe:
![](https://mostlynerdless.de/wp-content/uploads/2023/12/image-18-2000x1500.png)

While there, I stayed with Ties van de Ven, a speaker I first met at FOSDEM. At my first conferences, I knew no other speaker; later speaker dinners felt more like reunions:
![A](https://mostlynerdless.de/wp-content/uploads/2023/12/IMG_2583-2000x1500.jpg) At the speakers' dinner at J-Fall with Simon Martinelli and Tim te Beek

While I was giving presentations and writing about Java profilers and debuggers, I also wrote a five-part series on creating a Python debugger called [Let's create a debugger together](https://mostlynerdless.de/blog/tag/lets-create-a-debugger-together/), which culminated in my first presentation at my [local Python Meet-Up](https://www.meetup.com/pydata-suedwest/events/294504138/):

{{< youtube zCWjj98Wvg0 >}}

I went in 2023 from being a frightened first-time speaker who knew nobody to somebody who traveled Europe to speak at conferences and meet-ups, both large and small, while also regularly blogging and exploring new topics. I had the opportunity to meet countless other speakers, including [Marit van Dyjk](https://maritvandijk.com/) and Theresa Mammerella, who helped me get better at what I do. I hope I can give something back to the community this year, helping other first-time speakers succeed.

To conclude, here is a list of my most notable articles:

* [Writing a Profiler in 240 Lines of Pure Java](https://mostlynerdless.de/blog/2023/03/27/writing-a-profiler-in-240-lines-of-pure-java/)
* [The Inner Workings of Safepoints](https://mostlynerdless.de/blog/2023/07/31/the-inner-workings-of-safepoints/)
* [From C to Java Code using Panama](https://mostlynerdless.de/blog/2023/12/11/from-c-to-java-code-using-panama/)
* [A short primer on Java debugging internals](https://mostlynerdless.de/blog/2022/12/27/a-short-primer-on-java-debugging-internals/)
* [Taming the Bias: Unbiased Safepoint-Based Stack Walking](https://mostlynerdless.de/blog/2023/08/10/taming-the-bias-unbiased-safepoint-based-stack-walking/)

This year will become interesting. My first conference will be the free online [Java Developer Days](https://www.wearedevelopers.com/event/java-developer-day-january-2024) on Jan 17th by WeAreDevelopers, where I will give a presentation about debugging. I got accepted at FOSDEM with a talk on [Python's new monitoring API](https://fosdem.org/2024/schedule/event/fosdem-2024-1678-python-3-12-s-new-monitoring-and-debugging-api/), [ConFoo](https://confoo.ca/en/2024) in Canada, [JavaLand](https://www.javaland.eu/en/home/), the largest German Java conference, and [Voxxed Days Zürich](https://voxxeddays.com/zurich/), and I hope for many more. But also regarding blogging: I will start a new series soon on eBPF in which we'll explore eBPF with Java, developing a new library along the way.

I'm so grateful to my [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), which supports me in all my endeavors. Be sure to check out our website to get the best OpenJDK distribution.

Thanks for reading my blog and my articles here on Foojay.io; I hope you'll come to one of my talks next year, write a comment, and spread the word.

*This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone. This article first appeared on my personal blog [mostlynerdless.de](https://mostlynerdless.de/blog/2023/12/27/looking-back-on-one-year-of-speaking-and-blogging/).*
