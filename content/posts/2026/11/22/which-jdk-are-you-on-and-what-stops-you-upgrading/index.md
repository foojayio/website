---
title: "Which JDK are you on & what stops you upgrading?"
date: "2026-11-22"
description: "What JDK do Foojay community members run in production, and what stops them upgrading? Fourteen replies on frameworks, LTS policies and tools."
authors:
  - "dominika-tasarz"
image: "which-jdk-are-you-on-and-what-stops-you-upgrading.png"
categories:
  - "Java"
  - "Foojay"
related_posts: []
---

Java moves fast but most production systems don't. A few weeks ago, I asked the Foojay Slack community which JDK they run today and what keeps them from upgrading. The answers were honest and practical and I think we can all learn something useful from them.

## The question

- What JDK version is your main production app on right now, and what's stopping you from upgrading?

- Are you following the quarterly update releases to keep your system secure and patched?

Fourteen community members replied (so please don't treat this as a survey 😉), but the replies came from various authors, framework users, people in semi-government work and teams answering to customers with strict rules, so we have a wide range of examples and environments covered here.

## Which versions people are on

There was no single answer, and that is not surprising.

- **JDK 21:** one developer is there because Quarkus drives the choice. Our own Frank Delporte (aka Mr Foojay) also uses 21 as the minimum for his JavaFX libraries.

- **JDK 25:** a developer in a semi-government organisation runs it, and likes that it carries the LTS label.

- **Latest LTS in production, 27 in testing:** one team runs the current LTS and is already testing 27.

- **JDK 27 in production:** one developer runs 27 with a patched Payara and PrimeFaces 16.

- **JDK 26, heading to 27:** one team is on 26 and moving to 27, and says nothing is stopping them.

- **JDK 28 early access:** one speaker used it for a conference demo, which is not production, but shows how eager some people are to try what is next.

- **The outlier:** one reply mentioned a most successful app that ran on 1.0.8. Not every great application needs the newest runtime.

So the spread runs from long-established releases to versions that are barely out of the oven. Most people are not rushing, but several are happily ahead and adopting the newest releases.

## What actually stops people: everything around Java

If we use the analogy of JDK being the house and your frameworks and libraries being the furniture - moving to a new house is easy, but then you find that your old sofa does not quite fit up the staircase (think frustrated Ross from 'Friends' shouting 'pivot').

- **Validating compatibility.** One developer said the real work is checking that frameworks, libraries and existing production environments all still work together on the new version.

- **Framework support.** Where the framework sets the pace (Quarkus was the example), the JDK you can use is the one the framework supports.

- **Library authors play it safe on purpose.** Frank pointed out that library authors often choose a minimum version a little behind the latest LTS, so the people using their libraries have time to evolve.

So the blockers people named were not about Java itself - when a team says "we can't upgrade Java," they usually mean "we haven't finished checking the furniture."

## "LTS only" is not the same as "supported"

One reply raised a policy many of you will recognise. A customer only allows LTS releases of Java. Yet, they only recently started paying for support, and it took years to convince them that LTS does not by itself guarantee more stability or security.

That customer is now moving to minimal custom runtime images, so they can use non-LTS Java too. An experienced conference speaker in the thread made a related observation: in her Java talks, most people who stick to LTS are not paying for support.

## Tools that make upgrading easier

Two tools came up in the conversation that are worth mentioning.

- **Renovate** keeps things like base images current automatically. It won't rewrite your code for a major upgrade (and the name works well with our moving house/old furniture analogy 😉)

- **OpenRewrite** does the bigger jumps, for example moving Java 8 code to 17, and rewrites code to use the newer syntax.

In house terms, Renovate changes the light bulbs when they run out. OpenRewrite helps you renovate a room. You want both, but only one of them removes the dread of a big version jump.

## Unanswered question: quarterly updates

My side question, whether people follow the quarterly update releases, got very little reply - there might be a reason why.

Each quarterly release brings security and bug fixes for the version you already run and it does not change your Java version.

My guess, reading silence as a form of response: people don't think of quarterly updates as something to talk about. Moving to a new major version is a project, with a plan and a name. A quarterly update is routine: either it is already automated and forgotten, or it slips to the bottom of your to-do list.

## What to take away

1. **Test the furniture, not the house.** Compatibility of your frameworks and libraries is the real upgrade work, so start there.

2. **Separate "LTS" from "supported."** If your policy says LTS only, ask whether you actually have support behind it.

3. **Let tools do the boring parts.** Use something like Renovate for routine updates and OpenRewrite for big jumps.

4. **Keep up with quarterly updates.** They fix security issues and bugs on the version you already run, so check that they actually reach your systems, whichever version that is.

Thanks to everyone who answered in the Foojay Slack. If you want to add your own answer, join us there and tell us which JDK you run and what holds you back. We will keep asking and we will keep sharing what we learn.
