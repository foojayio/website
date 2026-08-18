---
title: "Why Home Assistant"
date: "2024-12-09T15:54:20+00:00"
lastmod: "2024-12-09T15:55:06+00:00"
description: "I have many different \"smart\" devices at home and no centralized system to manage them all. Plus, I'm concerned about privacy and am an ardent Open Source proponent. I immediately decided to invest in Home Assistant."
canonical: "https://blog.frankel.ch/home-assistant/1/"
authors:
  - "nicolas-frankel"
image: "Home_Assistant_Logo.svg.png"
categories:
  - "Developer Tools"
  - "Research"
  - "Tools"
related_posts:
  - "a-case-for-databases-on-kubernetes-from-a-former-skeptic"
  - "a-fresh-look-at-embedded-java"
  - "azul-brings-java-from-edge-to-cloud"
  - "getting-a-single-value-from-a-devices-state-in-home-assistant"
frozen: false
---

**Last June, I spoke at [Berlin Buzzwords](https://program.berlinbuzzwords.de/bbuzz24/talk/HMQMDH/). In all honesty, I rarely attend others' talks for a variety of reasons: lack of time, lack of energy, no interest in the proposed subjects, etc. When I do, I go either for subjects I know and want to deepen my understanding of *or* for subjects I know nothing about to get a foot in the door.**

This time, I attended [Monitoring your home, with DevOps observability tools](https://program.berlinbuzzwords.de/bbuzz24/talk/BQTZHK/). I thought it would be about OpenTelemetry for your home. After the speaker mentioned [Home Assistant](https://www.home-assistant.io/) ([home-assistant.io](https://www.home-assistant.io/)), however, I didn't pay much attention to the rest.
> Open source home automation that puts local control and privacy first. Powered by a worldwide community of tinkerers and DIY enthusiasts. Perfect to run on a Raspberry Pi or a local server.

I have many different "smart" devices at home and no centralized system to manage them all. Plus, I'm concerned about privacy and am an ardent Open Source proponent. I immediately decided to invest in Home Assistant.

In the first article of this series, I describe my current context and goals. The context includes a collection of 'smart' devices at home, each managed separately without a centralized system. My goals are to enhance privacy and control by adopting an open-source, local-first approach to home automation.

## My existing infrastructure

My smart home didn't start as a conscious thought but as a speaker's gift of [DevFest Vienna](https://www.youtube.com/watch?v=SXNz6jX7w0U). In 2018, Google wanted to promote Google Home Mini, which was part of the package. I put it in my living room, but it delivered little value besides answering a few questions. Soon afterwards, a then-colleague gave me a regular Google Home. I moved the Mini to my office and put the regular one in its place. At this point, I had a Deezer account (think Spotify, but French), so I could listen to music by asking Google.

With these two assistant devices, I decided to acquire smart lamps, which I could switch on and off with a voice command. I don't remember why, but I set my eyes on a Philips Hue pack. With the solution, you need a dedicated Hub that needs to be physically connected to the network with a cable—no Wi-Fi is possible. I installed lights in a couple of rooms; their setup is relatively easy via the dedicated Hue mobile app. Icing on the cake, the lights were easily imported into Google Home.

Through the app, I discovered that the Hub could manage sensors. I configured one to switch on the toilets' light. If the sensor detected no activity, it would switch off after a set period.

I was happy with the setup, with one exception: if anybody pressed the button to switch on the light, it would achieve its goal, but I wouldn't be able to control it vocally later. In rooms with people coming in regularly, it defeats the purpose. Worse, people naturally switched off the light when they left the toilets, so the sensor didn't switch on the light back again.

The next logical step was to acquire smart switches to fix this issue, which I installed in the relevant rooms.

## My goals

Everything works as expected. Still, some improvements are in order.

I rely on two different providers, Philips and Hue.

They do whatever they want with my data, although I unchecked every possible checkbox I could.

I can live with it, but I still dislike the lack of privacy.

I'd like a **local** but **centralized** entry point to monitor and command my devices.

These limitations often leave me feeling frustrated and restricted in my choices.

They must be compatible with either one or the other. I have other devices which I want to manage, but I can't.

This was an introductory article. In the following articles in this series, I'll delve deeper into a specific subject. Stay tuned!

**To go further:**

* [Home Assistant](https://www.home-assistant.io/)

*Originally published on [A Java Geek](https://blog.frankel.ch/home-assistant/1/) on November 25^th^, 2024*
