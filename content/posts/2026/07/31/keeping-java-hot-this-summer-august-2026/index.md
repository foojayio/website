---
title: "Keeping Java Hot This Summer"
date: "2026-07-31T09:50:18+00:00"
lastmod: "2026-07-31T16:16:40+00:00"
description: "Simon Ritter tours US & Canada JUGs in August 2026 with a deep technical talk on JVM startup and warmup. Find your city and register."
authors:
  - "dominika-tasarz"
image: "Azul-Payara-Community-New-Release-2.jpg"
categories:
  - "CRaC"
  - "Java"
  - "Java Beginner"
  - "JUGs"
tags:
related_posts:
frozen: false
---

Summer conference season goes quiet. Most major Java events wrap by June, CFPs go dark while we're waiting for the results and a lot of us - if we decide to carry on with the work - use August to either catch up on backlog, or training ( and I mean mental exercise here, although I'm sure some of you hit the gym more often too 😉 ).

If you're in the northeastern US or eastern Canada and are around next month with some extra time to spare, this may be a good excuse to join your local community to watch and learn from a technical session on a problem most of us have hit in production, delivered by [Simon Ritter,](https://gsjug.org/profiles/simon-ritter.html) a Java Champion who's spent years in the JVM internals.

The talk: Keeping Your Java Hot - Solving the JVM Startup and Warmup Problem
----------------------------------------------------------------------------

Get ready to tackle JVM startup woes and keep your Java apps running smooth and speedy from the get-go.

Java bytecodes and class files deliver on the original vision of "write once, run anywhere." Using a Just-in-Time (JIT) compiler allows JVM-based applications to compile only the code that's used frequently and optimise it precisely for how it's used. Using techniques like speculative optimisation can often deliver better performance than static, [Ahead-of-Time](https://openjdk.org/jeps/8335368) (AOT) compiled code.

However, this flexibility and performance come at a cost. Each time the JVM starts an application, it must perform the same analysis to identify hot spots in the code and compile them. This is referred to as the application warmup time.

In this session, we'll look at several approaches to alleviating or even eliminating this problem. Specifically:

* Static compilation of Java code Ahead-of-Time (AOT). Specifically, the Graal native image approach.
* Generating a JIT compiler profile of a running, warmed-up application that can be reused when the same application is restarted, eliminating the need for much of the JIT compilation. This will include details of the work of the OpenJDK [Project Leyden](https://openjdk.org/projects/leyden/).
* Decoupling the JIT compiler from the JVM for a Cloud environment. Providing a centralised JIT-as-a-Service allows caching of compiled code and offloading the compilation work when new code must be compiled.
* Creating a checkpoint of a running application. This includes all application state (heap, stack, etc.) in addition to the JIT-compiled code. [Project CRaC](https://wiki.openjdk.org/spaces/crac/overview) will be used as an example.

At the end of the session, you'll be all set to keep your Java hot!

Why join your local JUG this August?
------------------------------------

August tends to be a quieter month so it may be a perfect time to spend an evening on learning about a topic that matters the next time you're debugging cold starts, rather than skimming it between meetings. It's also a good excuse to come back to your local JUG if it's been a while, or to join one for the first time if you never have - these communities are so worth being part of and a specialist talk from a well known speaker is not something that is usually available for free elsewhere. You get to see and talk to Simon face to face, which means you can actually ask the questions you'd normally have to google the answer to, and you get a room full of people wrestling with the same problems, which is its own kind of learning. Turn up, listen, ask something, talk to whoever's next to you afterward.

The full tour details
---------------------

|   Date    |        JUG        |                                                                            Registration                                                                             |
|:---------:|:-----------------:|:-------------------------------------------------------------------------------------------------------------------------------------------------------------------:|
| August 4  | New York Java SIG |                              [Register on Meetup](https://www.meetup.com/javasig/events/315813188/?eventOrigin=group_upcoming_events)                               |
| August 5  | Garden State JUG  |               [Register Here](https://www.eventbrite.com/e/keeping-your-java-hot-by-solving-the-jvm-startup-and-warmup-problem-tickets-1994985965727)               |
| August 6  |   Montreal JUG    |            [Register on Meetup](https://www.eventbrite.com/e/keeping-your-java-hot-by-solving-the-jvm-startup-and-warmup-problem-tickets-1994985965727)             |
| August 25 |    Ottawa JUG     |                       [Register on Meetup](https://www.meetup.com/ottawa-java-user-group/events/315767728/?eventOrigin=group_featured_event)                        |
| August 27 |    Toronto JUG    | [meetup.com/toronto-java-users-group](https://www.meetup.com/toronto-java-users-group/events/) --- event page not yet posted, follow the group for the announcement |

<br />

We hope to see you there!

<br />
