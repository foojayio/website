---
title: "Customer Miscommunication Leads to Ongoing Nightmare"
date: "2021-11-16T08:28:01+00:00"
lastmod: "2021-11-16T08:31:18+00:00"
description: "We can see production disasters from miles away. Bad communication, missing skills, immature technological choices create ongoing disasters."
canonical: "https://talktotheduck.dev/production-horrors-customer-miscommunication-leads-to-ongoing-nightmare"
authors:
  - "shai-almog"
image: "Lightrun-Talk-to-the-duck-Production-Horrors.jpg"
categories:
  - "DevOps"
tags:
related_posts:
  - "production-horrors-handling-disasters-public-debrief"
  - "the-debugger-checklist-part-ii"
  - "psa-the-risks-of-remote-jdwp-debugging"
  - "boxlang-aws-azure-and-google-secrets-manager-module-released"
frozen: false
---

### **Production disasters are sometimes those pileups we can see coming from miles away. But the train is moving so fast we can't possibly stop it. This is one of those stories. In it a combination of bad communication, missing skills, immature technological choices created a pile up.**

**You can't debug a specification. You must stay vigilant during the development cycle to find conceptual mistakes and reorient the Titanic.**

This is a bit of a different story in the series. When I came up with the concept for production horrors my thoughts were mostly about a single day or a single event that made our production fail. Naturally our mind gravitates towards crashes or issues like the recent Facebook outage. But last time around, I gave the example of problematic caching that led to a billing problem...

This time the production horror is of a different kind. It started well before the product reached production and in a different era. In a time before ajax, when the web was still in request-response mode and IE 6 was state of the art (truly a horror story). I was approached about consulting for a major bank that was running a huge project to modernize its trading infrastructure.

I was too busy and expensive for the project so they decided to do without me. But I did get a chance to review the specification for the system.

## Still Relevant Today?

This was an in-house project with technologies that seem comically old by now. But I think all the big piece decision making/mistakes are timeless. I still see managers and developers making a lot of the same mistakes today.

I don't think anyone working on this was incomptent or stupid. I think mistakes were made because of scope/scale and the fact you can't debug architecture. Some of you might jump to "lean startup" and modern methodologies as a solution. Those weren't common back then but even with those processes, the problems only became visible when the full system was in place and it was too late to turn around.

## Real Time Trading, Over the Web?

This was a system for internal use. The bank had used a mainframe terminal system that worked OK but was clunky. They wanted to modernize it to "internet technologies".

Now remember that this was long ago. JavaScript was the language for creating cute animations. Gmail had just come out, and while it was impressive no one else was doing it. The frameworks for JavaScript front end didn't exist.

So my biggest complaint on the spec was: why are you using web technologies?

The response was: the customer wants us to use "internet technologies".

It was later discovered that the customer just wanted modern technologies. A Swing UI (which was common in banking systems at the time) would have been great. Picking a web UI was a communication failure between the customer and the architect.

If that one mistake in architecture wasn't made, this sad monstrosity could have been avoided...

## Servlets... It's Full of Servlets...

The next failure was one of hiring, I was expensive and busy. So they hired a lot of people. Filled a big office with \~30 developers of varying skill levels. They spent months drawing up an EJB 1.1 architecture with servlet/JSP front end.

All of this was on top of an IBM WebSphere application server running on AIX.

You might be cringing but this was "state of the art" at the time. As a side issue the project was missing someone with experience in the financial industry. This is pretty important for a banking application.

The thing is, they skimped on hiring a senior architect. They hired someone that "looked good" on the surface. But he didn't really know enough about banking or about web development...

The project used doubles for financial calculations!

If you don't come from the financial industry you might think this isn't a "big deal" but I promise you that four out of five fintech people reading this will slap their heads. This was "fixable" though; the real disaster was the front end.

This was before we had any front end APIs even on the server. Struts was relatively new and the developers in the project weren't aware of its existence. So they started pumping out a lot of disconnected servlets/JSPs with unclear navigation and no central control. The system became an unmanageable mess before it went to production.

## It Gets Worse

Keep in mind, we didn't get to the point of the story which is the production.

This was a trading system that was connected directly to the stock market and the bank's internal mainframe. There were a lot of moving parts involved. But there were a few customer requirements that weren't clear in the initial design specification. Mostly because the architects didn't think they would pose a challenge...

Some processes had to deliver sub second response times.

That sounds easy and it sounded easy back then. How hard can it be to send a purchase/sell command in less than a second. Right?

Turns out the time it took was 7 minutes. That's the point where I was brought into the project as a consultant. I literally just ran a profiler and got the time down to 7 seconds.

Two guys working one next to the other, had each assumed his colleague was caching the table when he invoked a method from the other guy. So you ended up with two lookup tables constantly looking up the entire adjoining table over and over again for every row in each table.

The system used a lot of hand coded JavaScript code all over the place that relied on DOM and browser behavior to get that last bit of performance and avoid the full page request. Again, there were no frameworks or "best practices" for that sort of thing in those days. So I can't really blame the people working on that.

Ultimately, the new PM who came over to fix the disaster came to this deployment plan:

* The system will only support Internet Explorer 6 with a specific version number
* To let users upgrade they will ghost their machines (duplicate user hard drives so the browser will have identical versions

There was even a discussion about using remote terminals (VNC style) to show a computer running the browser with the right version installed.

Surprisingly, this wasn't considered a failure by the bank who still hired the contractor to a much larger project.

## Lessons Learned

There are a lot of lessons that are applicable to modern projects:

* You need a domain expert in the field -- you can't run a banking project without someone who actually knows banking in depth at the code level. It isn't enough to have an expert in the design phase.
* It's hard to validate design -- projects like this should be more agile and should have stopped to re-evaluate before scaling the team and moving past the point of no return.  
  Customer requirements should be deeply validated -- this one thing could have changed the dynamic of the project completely.
* Some problems are only noticeable when we're close to production stage.
* You should write a test case that validates customer requirements -- if there was test from day one that validated the 1 second time requirement a lot could have been saved.
* Use current technology -- this is a lesson on which I have many other stories. Developers want to work with "cool" technologies. They tend to downplay the risk of picking a newer tool and exaggerate the potential benefits. Even if the technologies deliver the auxiliary technologies and space around them isn't always ready.
* Don't assume the customer requirements are "simple".

What do you do when you're going to production with a badly built project?

* Is it really badly built or is that just imposter syndrome rearing its ugly head?
* All code can be improved, the problem is that this leads to projects that never reach production. You need to find a balance.
  * Build a fix plan
  * What can we do in the short term
  * What parts will we be able to replace after going to production
  * Priorities with the customer
* Re-evaluate directions during development -- you can't debug a system design decision. As you're implementing it you might find problems. It's important to raise them high enough and loudly enough.
* Talk to the customers continuously -- There was a point in time in the middle of the project where the company could have changed course and gone back to building a regular Swing UI instead of a web UI. They probably would have finished the project sooner and with better quality.

## TL;DR

Production disasters are sometimes those pileups we can see coming from miles away. But the train is moving so fast we can't possibly stop it. This is one of those stories. In it a combination of bad communication, missing skills, immature technological choices created a pile up.

As a result the production environment ended up as a system administrators nightmare and a story to scare young developers.
