---
title: "To Brussels, Canada and back"
date: "2024-03-26T10:53:11+00:00"
description: "I went traveling again, read more on my travels to FOSDEM, Canada and Zurich in February/early March in this travel report."
authors:
  - "johannes-bechberger"
image: "image-1-2000x745-1.jpg"
categories:
  - "Trip Reports"
related_posts:
  - "apache-apisix-north-america-tour"
  - "gerrit-and-ivars-north-america-jug-tour"
  - "springone-tlv-world-tour-trip-report"
---

Last year was my first year blogging, speaking at conferences, meeting incredible people, and seeing places I've never been before.

It was at times quite arduous but at the same time energizing, as you can read in my post Looking back on one year of speaking and blogging. I didn't want it to be a one-off year, so I dutifully started a new article series on eBPF and applied for conferences...

And I got accepted at a few of them, which was really great because I started missing traveling after almost three months of being home. In this article, I'll cover my first three conferences this year: [FOSDEM](https://fosdem.org/2024) in Brussels, [ConFoo](https://confoo.ca/en/2024) in Montreal, and [Voxxed Days Zurich](https://voxxeddays.com/zurich/); they all happened between early February and early March.

It was the most travel, distance (and continent) wise, that I ever did before, by quite some margin:
![Globe view with markers for the three conferences: ConFoo in Montreal, FOSDEM in Brussels and VoxxedDays Zurich](image-1-2000x745-10a57ed5.jpg) [Read more: To Brussels, Canada and Back](https://foojay.io/today/to-brussels-canada-and-back/)

## FOSDEM

Every good journey starts with taking a train far too early:
![Early-morning train waiting at a dark station platform in Germany](IMG_2674-Large-03c554e9.jpeg)

I took the train at 3:30 am on the 2nd of February from Karlsruhe to Schwetzingen so one of my [SapMachine](https://sapmachine.io) colleagues, Christoph Langer, could pick me up for the first conference of the year: [FOSDEM in Brussel](https://fosdem.org/2024). But before FOSDEM, which happened over the weekend, there was the [JDK Committers Workshop](https://openjdk.org/workshop) nearby, where I chaired a session on modernizing JFR with Andrei Pangin:
![Two smiling conference attendees taking a selfie at FOSDEM in Brussels](IMG_2675-2000x1500-8b613210.jpeg)

Ron Pressler was also present, making the discussions quite worthwhile. This eventually led to my current work on implementing a CPU profiler in JFR with Andrei.

After the workshop day, the conference came. Last year, I was in Brussels, too, but I never really took the time to explore the city. Instead, I sat at the conference all day. My talk in the Java room was Saturday evening, so I took the opportunity to visit the main cathedral and some parks together with [Alexander Wert](https://www.linkedin.com/in/alexanderwert) and his wife:
![Baroque carved wooden pulpit inside the Brussels cathedral with stained glass windows behind](IMG_2679-1500x2000-899498ab.jpeg)

My first talk at FOSDEM was titled [Inner Workings of Safepoints](https://fosdem.org/2024/schedule/event/fosdem-2024-1675-inner-workings-of-safepoints/), in which I explained what safepoints are, why they are important, and how they are implemented, all based on my article, [The Inner Workings of Safepoints](https://mostlynerdless.de/blog/2023/07/31/the-inner-workings-of-safepoints/):

{{< youtube hbNaQHS_Urc >}}

My second talk was actually my first ever Python talk at any conference: It was my talk on [Python 3.12's new monitoring and debugging API](https://fosdem.org/2024/schedule/event/fosdem-2024-1678-python-3-12-s-new-monitoring-and-debugging-api/) (based on a [blog series](https://mostlynerdless.de/blog/tag/lets-create-a-debugger-together/)):

{{< youtube nunGmUd7PGI >}}

But it wasn't the last time I'd give the same talk at [PyCon Berlin](https://2024.pycon.de/program/P7AG9A/) and a similar one at [PyConLT](https://pycon.lt/2024/talks/KPAPX8) in Lithuania. I'm a Java developer but use Python regularly for all the plumbing and black box testing, so I was happy to give something back to the community.

After these conferences, I had two weeks without any conference, which was great because the next conference was somewhat farther away...

## ConFoo

I've never been to another continent, let alone visited Canada, so I was pretty happy when I got accepted to ConFoo in Montreal. Being 8 hours on a plane on the 19th of February and having Jetlag, both for the first time, was quite tiring, but hey, at least the conference paid for it:
![View from the plane window of a snow-covered landscape with a Swiss airline wing tip](IMG_2724-2000x1500-e086f5a6.jpeg)

After arriving in Montreal, I explored the city, [Mount Royal](https://montrealvisitorsguide.com/mount-royal-mont-royal/), and the [underground](https://www.mtl.org/en/experience/guide-underground-city-shopping) for an evening and a whole day, meeting [Theresa Mammarella](https://www.linkedin.com/in/tmammarella/) and her husband for dinner in [China Town](https://www.mtl.org/en/experience/discover-neighbourhood-montreal-chinatown):

![Narrow alley in Montreal between a stone building and a glass atrium at night](IMG_2738-1500x2000-11b86b92.jpeg)  
![Montreal street at night lit in blue and purple, with tall towers and white lighting masts](IMG_2743-1500x2000-a8584a0c.jpeg)  
![Large empty underground hall in Montreal with concrete pillars and grey carpet](IMG_2745-2000x1500-748a8a48.jpeg)  
![Long, empty underground corridor in Montreal with wood panels and glass-fronted shops](IMG_2748-2000x1500-8c29ac42.jpeg)  
![Looking up at a glass skyscraper reflecting clouds against a deep blue sky](IMG_2791-2000x1500-d55f5727.jpeg)  
![Bowl of ramen soup with broccoli, shiitake mushrooms, corn, egg and chili oil](IMG_2792-1-1500x2000-f0c86429.jpeg)  
![Narrow alley in Montreal between a stone building and a glass atrium at night](IMG_2738-1500x2000-11b86b92.jpeg)  
![Montreal street at night lit in blue and purple, with tall towers and white lighting masts](IMG_2743-1500x2000-a8584a0c.jpeg)  
![Large empty underground hall in Montreal with concrete pillars and grey carpet](IMG_2745-2000x1500-748a8a48.jpeg)  
![Long, empty underground corridor in Montreal with wood panels and glass-fronted shops](IMG_2748-2000x1500-8c29ac42.jpeg)  
![Looking up at a glass skyscraper reflecting clouds against a deep blue sky](IMG_2791-2000x1500-d55f5727.jpeg)  
![Bowl of ramen soup with broccoli, shiitake mushrooms, corn, egg and chili oil](IMG_2792-1-1500x2000-f0c86429.jpeg)

The days at the conference flew by, especially because of the great food there. I was also glad to meet [Sam](https://snugug.com/me/), [Jessie](https://www.linkedin.com/in/newmanjessie/), and [Jonatan](https://www.linkedin.com/in/jonatan-ivanov/overlay/about-this-profile/), joining them to eat bagels from both [St. Viateur](https://www.stviateurbagel.com/) (pictured) and [Fairmount Bagel](https://fairmountbagel.com/), as well as eating [poutine](https://en.wikipedia.org/wiki/Poutine) at [Chez Claudette](https://montreal.eater.com/maps/meilleure-best-poutine-montreal):  
![Plated tomato tart with cream and micro herbs, served at the conference dinner](IMG_2839-2000x1500-ab196bf7.jpeg)  
![St-Viateur bagel shop in Montreal, with a baker at the wood-fired oven and racks of fresh bagels](IMG_2814-2000x1500-ab51720e.jpeg)  
![Five people eating bagels around a table at the conference while a sixth works on a laptop](IMG_2821-2000x1500-c8146039.jpeg)  
![Poutine topped with tomato sauce and green peas on a plate in a Montreal restaurant](IMG_2832-2000x1500-e78e6232.jpeg)  
![Plated tomato tart with cream and micro herbs, served at the conference dinner](IMG_2839-2000x1500-ab196bf7.jpeg)  
![St-Viateur bagel shop in Montreal, with a baker at the wood-fired oven and racks of fresh bagels](IMG_2814-2000x1500-ab51720e.jpeg)  
![Five people eating bagels around a table at the conference while a sixth works on a laptop](IMG_2821-2000x1500-c8146039.jpeg)  
![Poutine topped with tomato sauce and green peas on a plate in a Montreal restaurant](IMG_2832-2000x1500-e78e6232.jpeg)

While the traveling aspect of this journey was great, I also gave two talks: One on writing a profiler in pure Java and another on debugging, but they are sadly not available publicly (yet). I stayed in Montreal till Saturday, the 23rd.

Then, I traveled by train to Toronto for almost a week with Theresa and her husband Charles before I returned to Montreal to take the plane back to Europe. I first met Theresa at JavaZone in Oslo (see [trip report](https://mostlynerdless.de/blog/2023/09/29/report-of-my-trip-to-javazone-and-northern-germany/)) in September last year...
![Letter board reading 'Welcome to Toronto' next to a red Javazone enamel mug](IMG_2968-2000x1500-349039d1.jpeg)

Where Montreal was icy cold, Toronto felt welcoming, almost warm. I explored the city, went up the [CN Tower](https://www.cntower.ca/), down into the [underground](https://www.toronto.ca/explore-enjoy/visitor-toronto/path-torontos-downtown-pedestrian-walkway/), to Niagra Falls, and enjoyed Charles' blueberry pancakes with dark maple syrup:  
![Blueberry pancakes on two plates beside a large jug of Canadian maple syrup](IMG_2974-2000x1500-515fb126.jpeg)  
![Aerial view of downtown Toronto skyscrapers seen from the CN Tower](IMG_3013-2000x1500-160b0190.jpeg)  
![CN Tower rising between buildings at dusk, with aircraft contrails across the sky](IMG_3069-2000x1500-3a78592a.jpeg)  
![Looking up at the glass and steel towers of Toronto's financial district at sunset](IMG_3074-2000x1500-d1e8171c.jpeg)  
![Bike-share bicycles parked in a steamy Toronto street between tall buildings](IMG_3076-2000x1500-a9cf3493.jpeg)  
![Toronto's Yonge-Dundas Square at night, surrounded by bright digital billboards](IMG_3089-2000x1500-ef9bd729.jpeg)  
![Niagara Falls with mist rising from the American and Horseshoe falls](IMG_3127-2000x1500-71fd4945.jpeg)  
![Laptop on a porch railing running IntelliJ IDEA with a failing unit test, houses across the street](IMG_3161-2000x1500-3d350053.jpeg)  
![Modern Toronto apartment towers with zigzag balconies, joined by a glass bridge under a sunny sky](IMG_3200-2000x1500-f4656453.jpeg)  
![Blueberry pancakes on two plates beside a large jug of Canadian maple syrup](IMG_2974-2000x1500-515fb126.jpeg)  
![Aerial view of downtown Toronto skyscrapers seen from the CN Tower](IMG_3013-2000x1500-160b0190.jpeg)  
![CN Tower rising between buildings at dusk, with aircraft contrails across the sky](IMG_3069-2000x1500-3a78592a.jpeg)  
![Looking up at the glass and steel towers of Toronto's financial district at sunset](IMG_3074-2000x1500-d1e8171c.jpeg)  
![Bike-share bicycles parked in a steamy Toronto street between tall buildings](IMG_3076-2000x1500-a9cf3493.jpeg)  
![Toronto's Yonge-Dundas Square at night, surrounded by bright digital billboards](IMG_3089-2000x1500-ef9bd729.jpeg)  
![Niagara Falls with mist rising from the American and Horseshoe falls](IMG_3127-2000x1500-71fd4945.jpeg)  
![Laptop on a porch railing running IntelliJ IDEA with a failing unit test, houses across the street](IMG_3161-2000x1500-3d350053.jpeg)  
![Modern Toronto apartment towers with zigzag balconies, joined by a glass bridge under a sunny sky](IMG_3200-2000x1500-f4656453.jpeg)

Being in Canada was great, and I'm looking forward to returning to North America for [KCDC](https://www.kcdc.info/) in June. I came back to Europe Saturday morning with quite some jetlag and four bagels from St. Viateur, spending my weekend with a friend close to the airport before going to Zurich in the following week:

## VoxxedDays Zurich

I actually flew to and from Canada via Zurich, so I was at home in Karlsruhe for just three days before going back for the [VoxxedDays](https://www.flickr.com/photos/bejug/albums/72177720315384585/with/53581963697), meeting new and old acquaintances and giving a talk called [Instrument to Remove: Using Java agents for fun and profit](https://voxxeddays.com/zurich/schedule/talk/?id=1754) (related [blog post](https://mostlynerdless.de/blog/2023/04/06/instrumenting-java-code-to-find-and-handle-unused-classes/)):
![The author speaking on stage at VoxxedDays Zurich, gesturing in front of a projected pixel heart](53581958507_5e78d36869_o-2000x1334-6fa0b725.jpg) Photo by the great [Dimitris Doutsiopoulos](https://ddphotography.gr/)

{{< youtube JnJgvcZo7b8 >}}

I had the opportunity to reconnect with [Mario Fusco](https://www.linkedin.com/in/mario-fusco-3467213/) (pictured), [Matthias Häussler](https://www.linkedin.com/in/matthiashaeussler/), [Anja Kunkel](https://www.linkedin.com/in/anja-kunkel-236534135), and many more and to meet new people like [Myriam Jessier](https://myriamjessier.com).
![Selfie of the author and Mario Fusco, wearing a yellow pixel-heart T-shirt and conference lanyards](IMG_3267-2-2000x1500-dc34e957.jpeg)

After the one-day conference, I met someone the next day, someone I had first met at ConFoo. By pure coincidence, [Marcus Boerger](https://www.linkedin.com/in/marcusboerger/overlay/about-this-profile/) lives close to Zurich with his family, so I joined him for lunch at his home, using the rest of the day to explore the city:  
![Panorama of the Limmat river in Zurich with the old town and church towers under a blue sky](IMG_3270-2000x790-8475e6c4.jpeg)  
![Close-up of a fluffy blue-eyed Ragdoll cat in front of a shelf of board games](IMG_3272-2000x1500-dffbf1d6.jpeg)  
![The author and Marcus Boerger and his wife smiling in a selfie in their home in Switzerland](IMG_3274-2-2000x1500-4691bfcc.jpeg)  
![Ornate domed ceiling with a skylight and a black lantern chandelier in a Zurich building](IMG_3279-1500x2000-e8822c85.jpeg)  
![Panorama of the Limmat river in Zurich with the old town and church towers under a blue sky](IMG_3270-2000x790-8475e6c4.jpeg)  
![Close-up of a fluffy blue-eyed Ragdoll cat in front of a shelf of board games](IMG_3272-2000x1500-dffbf1d6.jpeg)  
![The author and Marcus Boerger and his wife smiling in a selfie in their home in Switzerland](IMG_3274-2-2000x1500-4691bfcc.jpeg)  
![Ornate domed ceiling with a skylight and a black lantern chandelier in a Zurich building](IMG_3279-1500x2000-e8822c85.jpeg)

## Conclusion

My conference year started with three great conferences and the opportunity to travel. I'm grateful to my team at [SapMachine](https://sapmachine.io/) that allows me to speak in so many different places, as well as for all the other people, be it the conference organizers or hosts, that make all this possible.

I'm looking forward to my next month of traveling, in April, with three conferences (including a deep-dive at [JavaLand](https://meine.doag.org/events/javaland/2024/agenda/#eventDay.all#textSearch.Debugging%20Unveiled)) and at least one user group.

See you next week for the next installment of my [hello-ebpf](https://mostlynerdless.de/blog/tag/hello-ebpf/) series and hopefully the week after with my first co-authored article with a person I met at ConFoo.

*This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone. It first appeared on my personal blog [mostlynerdless.de](https://mostlynerdless.de/).*
