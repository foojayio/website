---
title: "Internal Security: Hardening Internal Systems"
slug: "internal-security-hardening-internal-systems"
date: "2022-11-18T15:08:28+00:00"
lastmod: "2022-11-18T15:08:57+00:00"
description: "Security is at odds with productivity and team cohesion. It doesn't have to be. There's a balance that mitigates external and internal risk"
canonical: "https://debugagent.com/internal-security"
authors:
  - "shai-almog"
image: "DALL-E-2022-10-11-21.48.44-hacking-into-a-system.jpg"
categories:
  - "Security"
tags:
related_posts:
  - "the-reason-java-is-still-popular"
  - "great-time-at-javazone-2022"
  - "open-source-bait-and-switch"
frozen: false
---

60% of security breaches are internal to the organization and yet when we think about security we usually think about vulnerabilities, exploits, etc. All the while, 60% of hacks are just from a person just logging into the system and taking whatever the hell they want.

I've been thinking about this quite a bit recently. I spent a great deal of time researching security-related issues for a chapter of my upcoming book and unfortunately found very little on hardening internal systems.

Yes, there's material about that too. But it seems that the vast majority is geared towards external threats rather than internal threats. I get that. Securing internally is hard, but it's probably the most important thing we can do and it's probably not as hard as many of us think. I'd like to preface this by stating that I'm not a security expert. The reason I'm writing this post is because most of us aren't. So what can we "typical programmers" do to mitigate security risks?

There's a lot we can do but most of that stuff is covered by the DevOps or security teams. There's also the points about writing secure code, there's a lot about that too. I want to talk about some things that are unique to our field that isn't covered as much. A couple of decades ago a local woman stole a ¼ of a billion NIS from a local bank (roughly 70M USD at the time). This took a while to steal, more than a decade... No one noticed!

How could a bank misplace 70M USD? Don't they have checks and balances? Sure they do. The systems altered that woman. She was the person responding to the system alerts. She didn't take a day off or vacation time, if she had her replacement would have noticed all the irregularities. This wasn't a technical hack. Not in the way we think of hacks. But the concept is the same, we lock the doors and windows but inside... It's free for all.

This story is important because that woman wasn't a bad person. We shouldn't look at our colleagues with suspicion. She was being blackmailed by local organized crime which instigated the whole thing. Because of that, we need to limit our exposure as much as possible and avoid reliance on a single point of failure.

## Work/Security Balance

Internal security has many benefits:

* Users have a right to privacy. Even if our company doesn't care about that right, privacy is enshrined in laws and regulations around the world. Gross negligence can lead to liability lawsuits.
* IP theft is a real problem e.g. Google sued Uber over IP theft.
* In case of an external attack, internal protections will make it much harder to get away with any meaningful information.

Internal security is crucial and is mostly handled by administrators. As programmers we often ignore that specific aspect of our job. There are a few things we can do in that area. Specifically: don't open holes, make sure we log everything, apply access restrictions and support good passwords.

The thing we don't want to do is make our lives difficult. We already have enough on our plate and corporate policies like password rotation do absolutely nothing for the security of the organization. They are security theater, and in fact harm security. Users write these passwords on sticky notes and leave them at their desks. Effectively nullifying their value.

We need to increase security while having minimal impact on day-to-day work. These are conflicting goals but there's a balance that we can strike here. Zero-trust is the keyword used for most of these policies, it's an interesting subject but I don't want to write about it.

I'm a developer, not a DevOps. I'm not a security expert either. As such, the duty of deploying a security policy and safeguarding it doesn't fall on me. Nor should it. But security is a team effort. The weakest link is where we all fail. One person clicking a bad email link can foil the best security policy. As developers there's a lot we can do, both for our colleagues and for the customers using our products. This is what I want to discuss.

## Don't Open the Door

It might seem insane. Why would we open the door? We're security conscious people. But we often open the figurative door without even thinking about it. Do you leave remote debugging open into a production server just to check stuff out?

That's an open door.

Even if you have a firewall rule. That isn't enough. It assumes:

* A hacker can't get around the firewall
* The person hacking the system isn't already inside

Both are problematic notions. Now you might say: that's zero trust. You would be 100% right. But it still needs to be said as quite a few developers do stuff like that. So many databases are open for scanning on the internet, it's scary.

If you're a developer in a team, pay attention to that. Communicate it onwards and try to improve the situation. People are often used to the way things are and don't even notice when a glaring hole is right in-front of them.

## Use Audit Logs

The most important assumption you should make as a security conscious developer is this: you can get hacked. Then what. DevOps are the first line of defense. We developers rarely do much in that regard. If a hacker does indeed get in there are two things that fall to us. The first is making it harder for them to do damage (see the next section). The second is later.

What did the hacker do? Where did they go? What did they get? These are all questions we should be able to answer in case of a breach. We need a system that logs everything but goes beyond the logger. We need [audit logging](https://www.baeldung.com/database-auditing-jpa) which is built into some frameworks by default and is relatively easy to add. With that we can follow up after the fact, or while a hack is in progress and mitigate it.

Speaking about logs, don't be overzealous with application logging and make sure to secure the log. I've dealt with quite a few organizations where they properly encrypted their database and secured it. Yet log access was free for all. By reading the log we can often find everything we need. User tokens are sometimes written directly to the log and let us impersonate a user. Imagine an admin logging in, not only can a malicious person with access to the log assume the identity. It's the perfect crime since it will seem like someone else committed it. While we should review the logs and make sure we don't log anything that is risky. We should also keep the log small so we can notice problematic aspects. Furthermore, we need to restrict access to the production logs to a restricted group of people who need to know.

## Access Restrictions

We should encrypt everything that's important. Secrets must be stored securely and externally to avoid hopping between services for complete control. Source shouldn't be stored using the same credentials or on the servers. Everything should be read-only and barebones with no SSH access.

If it's possible to access production, a hacker could find a way in. By removing normal access to production we remove that possibility and force a malicious hacker to find a non-standard way in.

## Support Good Passwords

I failed on this in at least one project and didn't appreciate the value of this enough. Lots of security experts swear by password managers. That's fine. But as a developer we need to support two things when it comes to passwords:

* Special characters
* Very long passwords

Some password validation doesn't allow some special characters which is just bad albeit more rare in recent years. But the real problem is password length. I love using passphrases. These are sentences of 5+ words that have meaning to me thus easy to remember. But impossible to guess randomly and impossible to brute force. E.g. "The kindergarten is packed at 8AM" would be impossible to guess. Even if a person is standing over my shoulder looking at me, typing they wouldn't piece that together (no that's not my password). Yet some systems limit the password length to 12.

## Finally

This is a team effort. Yes, most of the work is on the security team (if you have them) followed by DevOps. Then there are the tools that do a great deal of work e.g. keeping our dependencies clean from known vulnerabilities, etc.

When it comes to security, all of that isn't enough. Especially not if the problem is inside the company. I don't like the word zero-trust although the technical principles behind it are solid. I think we need to work as a team to detect anomalies and notice cases where things aren't as they seem. But mostly, we need to prepare for a breach. If we're 100% prepared in all departments. It will never come and that's a good thing.

There's a delicate balance we need to strike between vigilance, security and productivity. It isn't an easy balance. Tools like secret vaults are painful to work with when compared to just committing the key into git or putting a properties file in the image. But we need such practices as we grow. I agree, we don't need them for fast-growing startups. But as twitter reminds us. Fast-growing startups with poor security practices become mature publicly traded companies with poor security practices.
