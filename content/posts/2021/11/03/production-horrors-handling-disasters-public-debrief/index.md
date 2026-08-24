---
title: "Production Horrors - Handling Disasters: Public Debrief. Handling failures"
date: "2021-11-03T10:49:21+00:00"
lastmod: "2021-11-03T10:49:23+00:00"
description: "Just in time for Halloween failures in production are scarier than most movie monsters. Here's a personal scary story of a production fail..."
canonical: "https://talktotheduck.dev/production-horrors-handling-disasters-public-debrief"
authors:
  - "shai-almog"
image: "Lightrun-Talk-to-the-duck-Production-Horrors_Halloween.jpg"
categories:
  - "DevOps"
related_posts:
  - "the-debugger-checklist-part-i"
  - "the-debugger-checklist-part-ii"
  - "eight-debugging-tips-for-intellijidea-users-you-never-knew-existed"
  - "boxlang-aws-azure-and-google-secrets-manager-module-released"
frozen: false
---

**Just in time for Halloween failures in production are scarier than most movie monsters. Here's a personal scary story of a production fail...**

Halloween is probably the most appropriate time to start this new series of articles. I [talk a lot about theory on Foojay](https://foojay.io/today/author/shai-almog/), but when you're on the front line of a production disaster, it "gets real" very fast. People often think of production disasters as crashes or sites going down like the recent Facebook downtime. While that's an interesting subject, a lot of these things can go under the radar and hit you like a brick wall.

Today's horror story is about a young startup that nearly went bankrupt because of caching. I was the founder of this company and I [wrote quite a bit about this in the past](https://hackernoon.com/why-and-how-we-left-app-engine-after-it-almost-destroyed-us-40ac2fc0b1a8?1). It's been a few years since and while it still hurts, I hope I can write in a more detached voice this time around.

## Introduction

I co-founded [Codename One](https://github.com/codenameone/CodenameOne) in early 2012. The SaaS portion of the company was a complex backend that orchestrated build servers. This was 2012, no Containers/Docker or anything like that was available for production. PaaS was pretty big back then and App Engine was gaining some traction.

Since Codename One is a Java shop and it needed to go up fast picking up App Engine as the infrastructure made a lot of sense. Google gave a few computing resources for free which sealed the deal.

Back then App Engine didn't offer SQL, only datastore. Again we decided to use that, if it's good enough to run Google it's good enough to run our small product.

One important thing we should clarify. App Engine had a local debugging environment back then. But you couldn't debug the application as it was running in the cloud. During the first 2-3 years of Codename One we were mostly pleased with App Engine. I even advocated it in a talk at JavaOne etc.

One final important thing to know is that Codename One is a bootstrapped company. It's an open source framework with limited funds.

## Disaster Strikes

One fine day we got an email that billing is high. That seemed weird but we logged in to check it out. Our typical monthly bill was around 70$ + 400$ paid for gold support. The billing at this point was already in the 4 digits.

This is where hysteria kicked in. We don't have that kind of money…

So the first order of business was to reduce every resource we had such as number of instances etc. But those weren't the reason for the billing issue. We tried to get help from Google's gold support team, which was "unhelpful" (to put it mildly). Google's only suggestion was to define a spending limit and effectively bring our service down as a result.

The billing was entirely attributed to one line in the statement: "App Engine Datastore Read Ops". Naturally we wanted to understand what that means and which part of the code is performing all those reads… Unfortunately, due to the way App Engine is built (or was built circa 2015) there was no way of knowing that.

To make matters worse our only tool for debugging was logs, which cost money. So in order to debug this problem of higher spending I'd need to increase our spending.

App engine data store reads are known to be slow. So we assumed this was a problem there. Google provides an instance of memcached which you should use when accessing the data store. As far as I knew, we used it everywhere that was frequently used to cache everything important. But it seems we missed some point and the new App Engine update triggered that.

## Resolution

Unfortunately, deploying a new update to production was the only way to debug or fix it. But it gets worse.

Billing didn't list "live" numbers at the time (I'm unsure if it does so now). So we had to guess fixes by adding layers of caching then redeploy and wait a day to see if the change impacted the billing. This is literally the worst case scenario, we had to wait 24 hours to see if billing was impacted by the fix.

Because of that each attempt at a fix, included many different improvements to the code. To this day we have no idea what the bug was and what fixed it eventually. It's entirely possible that this was a bug in App Engine that was resolved by Google. We have no way of knowing.

## Debrief – Lessons Learned

### What we Should Have Done

I've given this a lot of thought over the years, what could we have done differently to avoid this in the first place?

Also what could we have done differently when we first discovered the problem?

#### Unit Tests?

Could we have written unit tests that would have detected or reproduced the problem?

I honestly don't know. We used JPA to abstract the storage, I guess we could have mocked the storage to see if access is cached. Before we had the issue it was a pretty niche test which we probably wouldn't have written.

When debugging this we tried to reproduce the issue with a test or in the debugger. We couldn't replicate the problem. If we had more time to debug the issue and develop a unit test we might have been able to reproduce it in a unit test. But we were working against the clock. A doctor trying to fix a bleeding patient doesn't have time for complex testing.

Since this problem occurred outside of our code, even if we had 100% test coverage we wouldn't have seen the problem. So while unit tests are indeed a valuable tool, in this case I don't see how they would have helped beforehand.

#### Observability

This really highlighted the importance of observability and the lack of it. One of my biggest peeves with Google on this case is: they charged for datastore read access. But they can't tell me which datastores I read from (which entity/table). Having a general direction indicating where the problem was happening could have saved us thousands of dollars.

Observability tools are crucial and they need the ability to dig into deep granularity.

#### Instant Feedback

This seems like a luxury in pretty much every other case but here we saw exactly how important this can be. The test/deploy/wait cycle literally cost us thousands of dollars, because of a problem that lasted a few days. Imagine if we were a bigger company with more traffic… We could have lost millions.

When you have a production problem you need your tools to report instantly. You need to know the exact problem and you need to know if your fix worked. Due to the nature of App Engine we had no way of using some of the tools that were available at the time. In retrospect we need better tools.

#### Local Debugging

There's a movement that [objects to the idea of debugging locally](https://dev.to/garethmcc/why-local-development-for-serverless-is-an-anti-pattern-1d9b) (at least in serverless). I see some of their points. E.g. in this case local debugging didn't reproduce the problem. It gave us false confidence that things worked, and they didn't.

But I'm not sure I agree with the bottom line. I think local debugging should be closer to the production. I still think we need tools to [debug production](https://lightrun.com/) , but they should be in addition to a working local environment.

#### Spending Limit

To this day I don't know if I made the right decision of skipping the spending limit. Should we have just let the service go down for a few days while we "figure stuff out"?

I honestly don't know.

### Are you at Risk?

You might be tempted to think that you aren't at risk. You don't use App Engine, probably don't use PaaS.

However, Serverless and 3rd party APIs provide a similar risk. It's a very common problem e.g. someone even [hacked himself with a spreadsheet](https://www.behind-the-enemy-lines.com/2012/04/google-attack-how-i-self-attacked.html) accidentally. Another team got a [72k USD bill for a free account](https://blog.tomilkieway.com/72k-1/) …

These stories are all over the place.

If you choose to use such services you MUST define a spending limit. You should also use observability tools and set up triggers to warn you if anything changes.

## Epilogue

A couple of years ago I met the founders of Lightrun. They outlined their vision for the company which is effectively a production debugger that gives us instant feedback securely. I instantly thought about this story.

What could I have done with this tool back then?

Ideas gain meaning when we feel the pain and I felt the pain deeply. This made the decision to join Lightrun a no-brainer. So I guess this horror story has a happy ending.

## TL;DR

Today's scary story is about a promising young bootstrapped company who ventured into an environment that seemed welcoming and wholesome… Only to discover that billing suddenly flipped overnight and it was running up huge charges.

You might be next, as many young companies have run into this nightmare.

## Tell Your Story

Do you have an interesting production disaster story to share?

Write to me at shaia (at) lightrun (dot) com. If you want your name/company name off this I'd be happy to oblige… I can offer a super cool Lightrun swag box as a reward, our swag here is top notch!

Looking forward to hearing from you.
