---
title: "Serverless is the New Timeshare"
date: "2022-09-09T07:53:18+00:00"
lastmod: "2022-09-09T07:54:38+00:00"
description: "Remember mainframes? Serverless is that: we own the machine and you rent out time on our big iron. We went full circle on progress..."
canonical: "https://debugagent.com/serverless-is-the-new-timeshare"
authors:
  - "shai-almog"
image: "DALL-E-2022-08-17-13.51.17-mainframe-in-the-clouds-artistic-epic.jpg"
categories:
  - "Cloud"
related_posts:
  - "what-is-debugging-in-140-seconds"
  - "production-horrors-handling-disasters-public-debrief"
  - "debugging-gson-moshi-and-jackson-json-frameworks-in-production"
  - "build-secure-ai-chat-applications-with-boxlang-rag-ollama-and-amazon-bedrock-with-dan-card"
frozen: false
---

We have a shared amnesia. When I speak to younger developers about past technologies, I often get blank stares. To be fair, some of that is because I'm a bit "intense" or "weird" but some of that is because. Huh? Really? Did we have that?

Case in point XA transactions and 2PC (Two Phase Commit). We have a young generation that's completely ignorant of that capability and the fact that this was "a thing". Did the requirement for transaction management somehow vanish?

Are banks no longer in need of consistency? If you aren't on the "same page" this technology works by transferring the transactional context between separate servers. So a commit on one server was a multi-stage process that pretty much guaranteed all servers succeeded or rolled back as one.

This was pretty amazing and actually worked reasonably well (with caveats obviously). Amazingly, this worked via method invocation. You didn't need to do anything. Even when invoking a remote method on a completely different server. It "just worked".

I was talking with a Node based startup in the banking industry a few years ago. They said banks were very open to working with Node. I since understood they're rewriting their stuff in a more "mature" environment. When I use some "newer" tools like Node, I'm always amazed by the basic stuff that's just missing. Sure it's simpler and smaller if you don't build into it everything that we need. It's easy to build simple stuff when you throw away core functionality.

## The NoSQL Drama of 2010s

Back in 1999 I was forming my consulting company, and a friend asked me to meet his boss. I went to this office where the "boss" said he had the most amazing idea that no one thought about. They have funding and will launch in 6 months to one million users on day one!

Me: OK. What's the idea?

Him:I'll tell you after you sign up to work for us.

Me: I'll sign an NDA.

Him: No. The idea is too good. Those NDAs aren't worth anything. You sign up and then...

Somehow I was able to resist the lure of working for that company. A year or so later they obviously didn't launch, but my consulting company was doing well. The friend called me again. This time they needed help with the product so I went there with my consulting hat on and helped them.

Their idea was a chat app built into the website so visitors to the site can chat to one another. A competitor already launched, and I was consulting to a few other companies with the same idea. They pivoted to focus on e-commerce related chats. But I digress...

Their system performed terribly. Slow as molasses with one user. Apparently the CEO insisted they need to support 1M users on the first day (as he told me). They conveyed that to Oracle who said they would need a cluster of three servers to support that volume. Then they talked to an Object Oriented DB vendor who promised they can handle 1M users with one machine. So they went all in on the object-oriented DB. When I expressed shock at this, they claimed their data is very "object-oriented" because each user can have multiple items... Ugh.

They didn't understand transaction boundaries, the storage code was mixed with everything as it was all code, and it was slow. It was unreliable and impossible to understand. You might not remember the object-oriented database period but it was a precursor to the NoSQL fad that gripped our industry in the 2010s. During that time as a consultant, I got to watch a replay of this story all over again. This time most companies launched successfully.

But then they found out that having unstructured data is no panacea. The benefit they got in performance were miniscule when compared to just using good caching and well tuned SQL. The deployment story was pretty complex and the auxiliary tools will probably never reach what we have in the world of SQL.

To be clear: there are valid uses for NoSQL. But most common uses for these DBs aren't very good and stem from RDD (Resume Driven Development). This is a pattern that those of us who've been around the block a few times see over and over again:

* Old technology is clunky complex
* People invent something that's clean and simple
* Forget old technology existed
* New stuff is overly simplistic and doesn't do a lot of basic stuff
* Reinventing these complexities
* New stuff becomes the old and clunky complexity that needs reinventing... Rinse/repeat

## Serverless as the New Mainframes

I've been doing a lot of serverless work in the past month and I feel this is a big step backward. It's a rehash of the problems we had with PaaS. It's practically a mainframe. Back in the days we used to pay for our work to run on a mainframe where we shared time. This was a bit more like a virtualization environment but the idea was similar: we don't own the environment. Arguably that's true for cloud SaaS too, but serverless takes that concept pretty far.

Even the debug experience is terrible. We don't have basic control of our code or basic application logic. I'm struggling to figure out why people use it for more than basic tasks.

There's one great use case I can think of: webhooks. Getting the duct tape code for webhooks is always a pain. They don't trigger often and dealing with that is a chore. Using a serverless function to just add stuff to the database and do the work can be pretty simple. Since a callback is hard to debug anyway, the terrible debugging experience in serverless isn't a huge hindrance. But for every other use case I'm absolutely baffled.

People spend so much time checking and measuring throughput yet just using one slightly larger server and only local calls will yield more throughput than you can possibly need. Without all the vendor tie-in that we fall into. Hosting using Linode, Digital Ocean, etc. would save so much money. On the time to market aspect, just using caching and quick local tools would be far easier than anything you can build in the cloud.

Containers are good progress and they made this so much simpler, yet we dropped the ball on this and went all in on complexity with stuff like Kubernetes. Don't get me wrong. K8s is great. But 98% of us don't really need it and shouldn't use it. If you're a small startup, Kubernetes is a waste of your time and energy.

## Back to Java and Into Rust

Java is an example where the amnesia portion is good. We had smalltalk, and it was great. When Java came along it was an inferior solution with weird C like syntax. The evolutionary aspect wasn't clear. Java threw away many great ideas in both smalltalk and C++. It adopted some controversial ideas (checked exceptions, primitives, etc.). Yet it succeeded. It grabbed mind share; it was able to leverage that.

It started as a small language that threw away all the garbage and over-engineering that other platforms added. Look at it now. No one describes it as "small" anymore. Developers are busy trying to create smaller, simpler languages complaining about Java's faults. Success would bring them right back to where we started. A small language that grows too much. Java is currently where it should be. It's one of the few examples of a good rewrite.

Rust seems to be one of those few exceptions too. It reinvents C in a way that contributes something completely new. It's hard to tell if it will survive in the long term. But undoubtedly it will need to pick up a lot of complexity along the way.

## Conscious Reinvention

What makes a reinvention of an existing language or tool into a mass market success and what leaves such tools in the sidelines?

SQL came back from the dead and is hot again with new startups. The same can't be said for C++. How are they different?

Node and Python are popular despite missing basic things we have in the JVM world. How is that and will they sustain this popularity? Will they add some of these things back?

Until our teenage years our brains add synapses constantly. During our teenage years, we cut them down. One theory is that this is the source of all the changes we go through as teens. We need to disconnect the stuff that's no longer working for us. Otherwise we'll just learn what our parents knew. We can't improve by making our own mistakes. By trying again something that failed in that generation.

As a result, we repeat mistakes and make some new terrible mistakes. We also make some amazing leaps and discoveries. This is where innovation takes off. The same is true for engineering.

How do we differentiate: teenage angst from a bright new direction?

We honestly can't. As an older person, a lot of these things looked stupid to me when I first saw them. We already tried those things and failed. Why rehash that broken direction? That's where innovation lies. But if we look closer at successful attempts, we can see what worked for them.

Java wasn't designed to end C++. Sure it might have been a fantasy. But Gosling designed it for simplicity and small size. To solve a very narrow niche, focus on security, size and networking.

Rust wasn't designed to end C. It was designed to make projects like Firefox more stable and performant.

I think re-invention, like any startup, works great when we initially limit ourselves to a very small and narrow use case. By doing that and keeping the initial focus we can build something good and then make the leap to great.
