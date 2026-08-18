---
title: "To Brussels, Canada and back"
date: "2024-03-26T10:53:11+00:00"
lastmod: "2024-03-26T10:53:12+00:00"
description: "I went traveling again, read more on my travels to FOSDEM, Canada and Zurich in February/early March in this travel report."
authors:
  - "johannes-bechberger"
image: "image-1-2000x745-1.png"
categories:
  - "Trip Reports"
related_posts:
  - "apache-apisix-north-america-tour"
  - "gerrit-and-ivars-north-america-jug-tour"
  - "springone-tlv-world-tour-trip-report"
frozen: false
---

Last year was my first year blogging, speaking at conferences, meeting incredible people, and seeing places I've never been before.

It was at times quite arduous but at the same time energizing, as you can read in my post Looking back on one year of speaking and blogging. I didn't want it to be a one-off year, so I dutifully started a new article series on eBPF and applied for conferences...

And I got accepted at a few of them, which was really great because I started missing traveling after almost three months of being home. In this article, I'll cover my first three conferences this year: [FOSDEM](https://fosdem.org/2024) in Brussels, [ConFoo](https://confoo.ca/en/2024) in Montreal, and [Voxxed Days Zurich](https://voxxeddays.com/zurich/); they all happened between early February and early March.

It was the most travel, distance (and continent) wise, that I ever did before, by quite some margin:
![](https://mostlynerdless.de/wp-content/uploads/2024/03/image-1-2000x745.png) [Read more: To Brussels, Canada and Back](https://foojay.io/today/to-brussels-canada-and-back/)

## FOSDEM

Every good journey starts with taking a train far too early:
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2674-Large.jpeg)

I took the train at 3:30 am on the 2nd of February from Karlsruhe to Schwetzingen so one of my [SapMachine](https://sapmachine.io) colleagues, Christoph Langer, could pick me up for the first conference of the year: [FOSDEM in Brussel](https://fosdem.org/2024). But before FOSDEM, which happened over the weekend, there was the [JDK Committers Workshop](https://openjdk.org/workshop) nearby, where I chaired a session on modernizing JFR with Andrei Pangin:
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2675-2000x1500.jpeg)

Ron Pressler was also present, making the discussions quite worthwhile. This eventually led to my current work on implementing a CPU profiler in JFR with Andrei.

After the workshop day, the conference came. Last year, I was in Brussels, too, but I never really took the time to explore the city. Instead, I sat at the conference all day. My talk in the Java room was Saturday evening, so I took the opportunity to visit the main cathedral and some parks together with [Alexander Wert](https://www.linkedin.com/in/alexanderwert) and his wife:
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2679-1500x2000.jpeg)

My first talk at FOSDEM was titled [Inner Workings of Safepoints](https://fosdem.org/2024/schedule/event/fosdem-2024-1675-inner-workings-of-safepoints/), in which I explained what safepoints are, why they are important, and how they are implemented, all based on my article, [The Inner Workings of Safepoints](https://mostlynerdless.de/blog/2023/07/31/the-inner-workings-of-safepoints/):

{{< youtube hbNaQHS_Urc >}}

My second talk was actually my first ever Python talk at any conference: It was my talk on [Python 3.12's new monitoring and debugging API](https://fosdem.org/2024/schedule/event/fosdem-2024-1678-python-3-12-s-new-monitoring-and-debugging-api/) (based on a [blog series](https://mostlynerdless.de/blog/tag/lets-create-a-debugger-together/)):

{{< youtube nunGmUd7PGI >}}

But it wasn't the last time I'd give the same talk at [PyCon Berlin](https://2024.pycon.de/program/P7AG9A/) and a similar one at [PyConLT](https://pycon.lt/2024/talks/KPAPX8) in Lithuania. I'm a Java developer but use Python regularly for all the plumbing and black box testing, so I was happy to give something back to the community.

After these conferences, I had two weeks without any conference, which was great because the next conference was somewhat farther away...

## ConFoo

I've never been to another continent, let alone visited Canada, so I was pretty happy when I got accepted to ConFoo in Montreal. Being 8 hours on a plane on the 19th of February and having Jetlag, both for the first time, was quite tiring, but hey, at least the conference paid for it:
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2724-2000x1500.jpeg)

After arriving in Montreal, I explored the city, [Mount Royal](https://montrealvisitorsguide.com/mount-royal-mont-royal/), and the [underground](https://www.mtl.org/en/experience/guide-underground-city-shopping) for an evening and a whole day, meeting [Theresa Mammarella](https://www.linkedin.com/in/tmammarella/) and her husband for dinner in [China Town](https://www.mtl.org/en/experience/discover-neighbourhood-montreal-chinatown):

![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2738-1500x2000.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2743-1500x2000.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2745-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2748-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2791-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2792-1-1500x2000.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2738-1500x2000.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2743-1500x2000.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2745-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2748-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2791-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2792-1-1500x2000.jpeg)

The days at the conference flew by, especially because of the great food there. I was also glad to meet [Sam](https://snugug.com/me/), [Jessie](https://www.linkedin.com/in/newmanjessie/), and [Jonatan](https://www.linkedin.com/in/jonatan-ivanov/overlay/about-this-profile/), joining them to eat bagels from both [St. Viateur](https://www.stviateurbagel.com/) (pictured) and [Fairmount Bagel](https://fairmountbagel.com/), as well as eating [poutine](https://en.wikipedia.org/wiki/Poutine) at [Chez Claudette](https://montreal.eater.com/maps/meilleure-best-poutine-montreal):  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2839-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2814-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2821-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2832-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2839-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2814-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2821-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2832-2000x1500.jpeg)

While the traveling aspect of this journey was great, I also gave two talks: One on writing a profiler in pure Java and another on debugging, but they are sadly not available publicly (yet). I stayed in Montreal till Saturday, the 23rd.

Then, I traveled by train to Toronto for almost a week with Theresa and her husband Charles before I returned to Montreal to take the plane back to Europe. I first met Theresa at JavaZone in Oslo (see [trip report](https://mostlynerdless.de/blog/2023/09/29/report-of-my-trip-to-javazone-and-northern-germany/)) in September last year...
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2968-2000x1500.jpeg)

Where Montreal was icy cold, Toronto felt welcoming, almost warm. I explored the city, went up the [CN Tower](https://www.cntower.ca/), down into the [underground](https://www.toronto.ca/explore-enjoy/visitor-toronto/path-torontos-downtown-pedestrian-walkway/), to Niagra Falls, and enjoyed Charles' blueberry pancakes with dark maple syrup:  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2974-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3013-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3069-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3074-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3076-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3089-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3127-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3161-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3200-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_2974-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3013-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3069-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3074-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3076-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3089-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3127-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3161-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3200-2000x1500.jpeg)

Being in Canada was great, and I'm looking forward to returning to North America for [KCDC](https://www.kcdc.info/) in June. I came back to Europe Saturday morning with quite some jetlag and four bagels from St. Viateur, spending my weekend with a friend close to the airport before going to Zurich in the following week:

## VoxxedDays Zurich

I actually flew to and from Canada via Zurich, so I was at home in Karlsruhe for just three days before going back for the [VoxxedDays](https://www.flickr.com/photos/bejug/albums/72177720315384585/with/53581963697), meeting new and old acquaintances and giving a talk called [Instrument to Remove: Using Java agents for fun and profit](https://voxxeddays.com/zurich/schedule/talk/?id=1754) (related [blog post](https://mostlynerdless.de/blog/2023/04/06/instrumenting-java-code-to-find-and-handle-unused-classes/)):
![](https://mostlynerdless.de/wp-content/uploads/2024/03/53581958507_5e78d36869_o-2000x1334.jpg) Photo by the great [Dimitris Doutsiopoulos](https://ddphotography.gr/)

{{< youtube JnJgvcZo7b8 >}}

I had the opportunity to reconnect with [Mario Fusco](https://www.linkedin.com/in/mario-fusco-3467213/) (pictured), [Matthias Häussler](https://www.linkedin.com/in/matthiashaeussler/), [Anja Kunkel](https://www.linkedin.com/in/anja-kunkel-236534135), and many more and to meet new people like [Myriam Jessier](https://myriamjessier.com).
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3267-2-2000x1500.jpeg)

After the one-day conference, I met someone the next day, someone I had first met at ConFoo. By pure coincidence, [Marcus Boerger](https://www.linkedin.com/in/marcusboerger/overlay/about-this-profile/) lives close to Zurich with his family, so I joined him for lunch at his home, using the rest of the day to explore the city:  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3270-2000x790.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3272-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3274-2-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3279-1500x2000.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3270-2000x790.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3272-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3274-2-2000x1500.jpeg)  
![](https://mostlynerdless.de/wp-content/uploads/2024/03/IMG_3279-1500x2000.jpeg)

## Conclusion

My conference year started with three great conferences and the opportunity to travel. I'm grateful to my team at [SapMachine](https://sapmachine.io/) that allows me to speak in so many different places, as well as for all the other people, be it the conference organizers or hosts, that make all this possible.

I'm looking forward to my next month of traveling, in April, with three conferences (including a deep-dive at [JavaLand](https://meine.doag.org/events/javaland/2024/agenda/#eventDay.all#textSearch.Debugging%20Unveiled)) and at least one user group.

See you next week for the next installment of my [hello-ebpf](https://mostlynerdless.de/blog/tag/hello-ebpf/) series and hopefully the week after with my first co-authored article with a person I met at ConFoo.

*This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone. It first appeared on my personal blog [mostlynerdless.de](https://mostlynerdless.de/).*
