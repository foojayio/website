---
title: "No Programmer Knows Everything"

# Publish date, as a plain day: YYYY-MM-DD.
#
# LEAVE THIS AS IT IS unless you want your article out on a particular day. A
# maintainer sets it when the article is published, and moves your folder into
# content/posts/<year>/<month>/<day>/ to match, so you never have to pick either.
#
# DON'T ADD A TIME. Foojay is a static site: it only exists after a build, and
# there is one scheduled build a day, at 07:00 UTC. That is when articles go
# out, and a time here cannot make it earlier. It can make it LATER -- a post
# dated 09:00 is still in the future when the 07:00 build runs, so it misses
# that morning and turns up whenever the site next happens to be rebuilt, which
# might be the following day. The PR check rejects a time on a future date for
# exactly that reason.
#
# A date in the FUTURE schedules the article: it is not published and not 
# searchable until that morning. Until then, it appears in
# "Coming soon" on the home page, by title, date and author.
date: "2026-11-23"

# Optional: last-updated date, if you revise the article later.
# lastmod: "2026-01-02"
description: "Nobody knows everything about, well, anything. What matters is why each gap is there, what the rest of the team already covers, and whether anyone is allowed to say they don't know"
authors:
  - "jottinger"
image: "header.jpg"
categories:
  - "Java"
related_posts:
  - "bcn-where-does-experience-come-from"
  - "how-i-built-an-ai-assistant-for-my-career-with-java-spring-ai-and-mongodb"
  - "what-i-learned-building-an-ai-code-reviewer-with-java-spring-boot"
  - "java-new-features-in-production"
canonical: "https://bytecode.news/posts/2026/11/no-programmer-knows-everything"
---

Imagine a programmer, desperate for a position in a world where so many applicants with lots of experience are hunting for jobs. The requirements are vague, often asking for optimal experience across so many spheres, and the fear is that any gap, any at all, at any point in the application process is enough to reject the programmer.

After all, a position might advertise Java - but what does that mean? Java is a full-stack language, with applications on the user-facing and server-side tiers. Does "Java" mean a mastery of Swing, or JavaFX, or both? Does it include JDBC? What about Jakarta EE? JMH, or monitoring tools? Does it include knowledge of GC configuration? Why not? All of those things are under the Java umbrella.

The assumption that goes along with that fear is that a good programmer is a *complete* programmer, that a good programmer knows all there is to know. It's not true. No such person exists: not James Gosling, not Peter van der Linden, not Brian Goetz, even though these guys might get close.

## If I only had a brain

In the movie adaptation of _The Wizard of Oz_, the Scarecrow believes he lacks a brain[^2]. The Wizard eventually gives him a diploma to validate the capability he had already been demonstrating.

The Scarecrow's gap was imagined. Plenty of gaps are real, and that's okay! A given person can genuinely lack a skill required for a particular job - and an Oz-like validation won't provide it.

{{< note >}}
For some positions, that actually is disqualifying. If a position requires someone to design intuitive user interfaces in JavaFX, someone like me would be a poor choice. But it has no implication that the candidate is incompetent.
{{< /note >}}

## Gaps are different things

A gap can mean a *lot* of different things. Consider:

A gap might be something that someone just hasn't learned yet. They *can* learn it - maybe they've just never needed it.

A gap might be a strength gap. For example, I *can* design user interfaces, but my first instincts tend to produce interfaces that reflect how *I* see the world, not necessarily how other people see it. I can make those interfaces usable, but it takes a lot of effort, and other people do it much more naturally.

A gap might be a *prioritization* issue. Here, someone might actually be quite able to tune performance for a given application - but maybe their SLA has been such that they've never needed to, and thus their effort has been better spent elsewhere.

A gap might also be a *permission* problem. Someone may have the ability and willingness to learn, but work in an environment where asking questions, trying unfamiliar approaches, or admitting ignorance carries a penalty.

Sometimes the restriction is explicit: they aren't allowed access to the systems or work that would give them experience. Sometimes it's cultural: they've learned that asking questions makes them look incompetent, or that unsuccessful experiments are remembered longer than successful ones.

In either case, the resulting gap may be real. But its existence doesn't tell us why it's there, or what the person could do under different circumstances.

An interview might be really useful here, because there's a lot of difference between what someone *has not* done and what they *cannot* do.

## Why have a team?

We organize people into teams because individuals have different capabilities, experiences, and ways of approaching problems. Those capabilities overlap, but they don't coincide.

Consider a reporting application: one developer might understand the data model, the queries, and how to make the report run efficiently. Another might understand what users are actually looking for when they open the report. Both perspectives matter: a technically excellent report that doesn't answer the user's questions isn't particularly useful, but a report that delivers incorrect results or takes forever to run isn't useful *either*.

Neither developer needs to possess the other's strengths to contribute. They do need to recognize what the other brings to the problem, and be willing to learn from each other.

But that requires knowing where the gaps are. If everyone claims universal competence, the team has no reliable way to determine which capabilities it possesses, which it lacks, and where someone might need help.

A team that understands its needs and gaps has an advantage in hiring: it can *look for* applicants who fill the empty spaces in capabilities. If you have people who understand the data access model intimately, you might need people who can talk to customers and understand what they need so the requirements can be communicated effectively.

The gaps exist, in *each* member of the team, and if a team knows its weak areas, that knowledge can be used to construct a *stronger* team.

## "I don't know" in the interview

Knowing where people have gaps is useful only if those gaps can be acknowledged. An interview can make that admission costly, or it can make the admission the beginning of a useful exchange.

Consider an interview that's designed as a conversation rather than an examination, which is how *I* interview. Here, the interviewer tries to reduce the pressure on the candidate, establish some common ground, and discover what the candidate knows and what they don't.

But this doesn't have to go in only one direction.

The interviewer might find something the candidate doesn't know and try to teach it. The candidate might teach the interviewer something in return. Both sides discover how the other approaches unfamiliar information, asks questions, explains ideas, and responds when an assumption turns out to be wrong.

Neither side needs to demonstrate universal competence; they just need to demonstrate a willingness to engage with the other.

There's still an imbalance: the interviewer has considerable power over whether the candidate gets an offer, and therefore bears responsibility for making the exchange possible. But the candidate is evaluating the interviewer and the organization, too. Both parties are deciding whether they can work together.

A conversational interview doesn't eliminate qualification requirements. Someone might genuinely lack a capability essential to the position. Nor does it mean that everyone who is technically qualified will be someone you want to work with.

But it gives both parties a better opportunity to discover those things without requiring either one to pretend to be something they're not.

{{< note >}}
My entire interview process is built around finding people who are willing to learn, and want to learn, and want to *teach* - and to find out if the other person is a jackwagon. I can handle a knowledge gap, even an enormous knowledge gap: the information is out there somewhere, after all. I am looking for *aptitude*, and aptitude held by someone I want to work with. I cannot make someone less of a jerk, and I won't hire a jerk, nor will I work with one. Life's too short as it is.
{{< /note >}}

## The missing part

Let's return to our imaginary job applicant.

The position required someone who "knows Java," but that's an enormous range of capabilities. A useful hiring process would establish which of those capabilities actually matter for the hire, which can be learned, and which are already available elsewhere on the team.

Our applicant might not know JavaFX, or JMH, or the intricacies of garbage collection. Some of those gaps might matter for this position. Others might not matter at all. And some might represent things the applicant could learn readily, given the opportunity.

An employer needs to know which is which. So does the applicant.

In _The Wizard of Oz_, the Scarecrow received a diploma because he already had what he thought was missing. Our applicant might actually be missing something. A credential won't supply it[^3], and neither will pretending it isn't missing.

But neither does that gap define the applicant's entire worth as a programmer.

The useful question here is what someone knows, what they can learn, what they bring to the team, and whether the relationship makes sense for everyone involved.

[^2]: If you're not familiar with the Wizard of Oz, it's a book by L. Frank Baum, with a blockbuster made in 1939 starring Judy Garland and a host of other Hollywood glitterati. Judy's character, Dorothy, is carried away by a tornado (maybe) and picks up three companions: a scarecrow who lacks a brain, a cowardly lion, and a tin man who lacks a heart. Their needs are fulfilled largely by validation: a diploma, a medal, a heart-shaped watch, and in Dorothy's case, the knowledge that her shoes could have transported her back home at any moment, if only she'd known. Sorry for spoilers, but the movie came out decades before I was even born.

[^3]: Cue the sound of a million Java certifications deflating in value, as if a planet were exploding in the viewscreen.
