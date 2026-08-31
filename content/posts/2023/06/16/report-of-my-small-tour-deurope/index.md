---
title: "Johannes Bechberger's Tour d'Europe"
date: "2023-06-16T17:08:53+00:00"
lastmod: "2023-06-16T17:16:25+00:00"
description: "I spoke in two weeks in 3 countries, 4 cities and 5 different venues. This is my report on the trip. Check it out!"
authors:
  - "johannes-bechberger"
image: "image-7.jpg"
categories:
  - "Trip Reports"
related_posts:
  - "foojay-podcast-14"
  - "a-short-primer-on-java-debugging-internals"
  - "ap-loader-a-new-way-to-use-and-embed-async-profiler"
frozen: false
---

Between 31st May and 14th June, I was on tour, giving seven talks in 4 cities in 3 different countries:

* 31st May: [JUG Milano](http://www.jugmilano.it/meeting-145.html)
* 5th June: [OpenValue Munich Meetup](https://www.meetup.com/openvaluemuenchen/events/293736106/)
* 7th June: [JDriven Full Stack Conference in Nieuwegein](https://jdriven.com/full-stack-conference-2023)
* 10th and 11th of June: [Gulasch Programmier Nacht Karlsruhe](https://cfp.gulas.ch/gpn21/speaker/9ZMNT9/)
* 14th of June: [Karlsruher Entwicklertag](https://www.entwicklertag.de/2023/conference_day.html)

{{< img src="https://mostlynerdless.de/wp-content/uploads/2023/06/image-7.png" class="size-full is-resized" width="372" height="507" caption="A visualization of all the cities I visited, but I took the train for all transits (except for the Arnhem to Nieuwegein route, where Ties van de Ven drove me in his Tesla)." >}}

It was an exciting trip, and I had the pleasure of visiting friends in Zurich and Augsburg and a [grain mill shop](https://www.muehlen-kaiser.de/) in Munich.

Sadly there are only recordings of two of my seven talks, but all talks were excellent:

## [JUG Milano: Your Java Application Is Slow? Check Out These Open-Source Profilers](http://www.jugmilano.it/meeting-145.html)

I gave my updated QCon talk in Milan on 31st May:

{{< youtube DhYDzff6UCE >}}

This is related to my InfoQ article [Unleash the Power of Open Source Java Profilers: Comparing VisualVM, JMC, and async-profile](https://www.infoq.com/articles/open-source-java-profilers/). I had a lot of fun giving the talk, and I hope the audience liked it.

Being in Milan for the first time was fantastic. I was able to stay with Mario Fusco for a few days to enjoy the beauty of Gorgonzola, the suburb of Milan where he lives, and also visit the famous [Museo Nazionale della Scienza e della Tecnologia Leonardo da Vinci](https://www.museoscienza.org/).

## [OpenValue Munich Meetup: Writing a Profiler in 240 Lines of Pure Java](https://www.meetup.com/openvaluemuenchen/events/293736106/)

I then went on to give a talk at the OpenValue Munich Meetup, based on the previous talk and my [Writing a Profiler in 240 Lines of Pure Java](https://mostlynerdless.de/blog/2023/03/27/writing-a-profiler-in-240-lines-of-pure-java/) article:  
![](https://mostlynerdless.de/wp-content/uploads/2023/06/image-4.png)

But before this, I stayed with friends in Augsburg and Zurich:  
![](https://mostlynerdless.de/wp-content/uploads/2023/06/IMG_1398-2-1500x2000.jpeg) Wooden tower near Oerlikon, nearby Zurich

## [JDriven Full Stack Conference](https://jdriven.com/full-stack-conference-2023)

I gave a similar talk, only with a little more information on why you shouldn't trust profilers ([see](https://mostlynerdless.de/blog/2023/02/20/do-you-trust-profilers-i-once-did-too/)), in [Nieuwegein](https://jdriven.com/full-stack-conference-2023):
![](https://mostlynerdless.de/wp-content/uploads/2023/06/image-5.png)

This concluded my three talks outside of Karlsruhe.

## [Gulasch Programmier Nacht Karlsruhe](https://cfp.gulas.ch/gpn21/speaker/9ZMNT9/)

After coming home, I gave two talks at the GPN, one based on the article [Do you trust profilers? I once did,](https://mostlynerdless.de/blog/2023/02/20/do-you-trust-profilers-i-once-did-too/) too, and one based on the two articles [Instrumenting Java Code to Find and Handle Unused Classes](https://mostlynerdless.de/blog/2023/04/06/instrumenting-java-code-to-find-and-handle-unused-classes/) and [Class Loader Hierarchies](https://mostlynerdless.de/blog/2023/06/02/class-loader-hierarchies/). The former talk is recorded:

{{< youtube 6DbjSN-nCcY >}}

## [Karlsruher Entwicklertag](https://www.entwicklertag.de/2023/conference_day.html)

My last two talks in Karlsruhe were my profiling talk from before and a talk with live coding based on my [writing a profiler from scratch](https://mostlynerdless.de/blog/tag/writing-a-profiler-from-scratch/) series.

## Conclusion

Giving so many talks during two weeks was interesting, although it proved more taxing than I had hoped. I'm happy to start working on my JEP and fixing bugs; a significant rewrite of the JEP might be on the horizon. The following blog post will probably be related.

If you want to see me giving a talk, either invite me or come to the following few planned talks:

### July

* [Java User Group Mannheim](http://www.majug.de/), 13th July: Writing a Profiler in 240 Lines of Pure Java ([blog post](https://mostlynerdless.de/blog/2023/03/27/writing-a-profiler-in-240-lines-of-pure-java/))

### September

* [JavaZone Oslo](https://2023.javazone.no/), 6th and 7th September: Unleash the Power Of Open-Source Profilers ([InfoQ article](https://www.infoq.com/articles/open-source-java-profilers/))
* [Java Forum Nord](https://javaforumnord.de/2023/programm/) Hannover, 12th September: Unleash the power of Open-Source Java Profilers

### Oktober

* [Basel One](https://baselone.ch/one), 18th and 19th October: Unleash the Power Of Open-Source Profilers

Hopefully, there will be more. You can find my past and upcoming talks on my new [Talks](https://mostlynerdless.de/talks/) page.

***This project is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com), making profiling easier for everyone. This article first appeared on my personal blog [mostlynerdless.de](https://mostlynerdless.de/blog/2022/11/21/ap-loader-a-new-way-to-use-and-embed-async-profiler/).***
