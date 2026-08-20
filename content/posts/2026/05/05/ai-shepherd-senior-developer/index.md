---
title: "The Code Was Always the Door"
date: "2026-05-05T08:59:45+00:00"
description: "AI is automating the code, not the engineering. A guide for senior developers on shepherding AI through the work that was always the actual job."
authors:
  - "markus-westergren"
image: "image.jpg"
categories:
  - "Java"
related_posts:
frozen: false
---

## The doorman in a hoodie

There's a story Rory Sutherland tells in his book *Alchemy* . A consultant is hired to find savings at a luxury hotel. He watches a doorman for twenty minutes and writes in his report: *this man opens doors*. Automatic doors also open doors. Automatic doors are cheaper. So the hotel removes him.

The lobby falls apart. Guests can't find the restaurant. Nobody hails a taxi. The ineffable sense that someone is in charge of the front of house disappears with him. The consultant measured the visible action and missed the actual function.

Right now, somewhere, someone is watching a senior developer type code and writing a similar report. *These people produce code. ChatGPT produces code*. The doorman fallacy, in a hoodie. The code was always the door opening. It was never the job.

That is the argument I want to make, and I want to make it specifically to other senior developers, because we are the people best positioned to see why it is right, and most at risk of forgetting it. The visible work has shifted. The judgement underneath has not. Everything you have built up over a career is exactly what AI lacks and exactly what shepherding it well requires: the context, the taste, the system thinking, the willingness to be on the hook for the 2am call.

The headline isn't that AI is replacing developers. The headline is that AI made the rest of the job, the part that was always the actual job, finally visible.

## The shepherd

I have started using a particular word for the role I think senior developers are evolving into: shepherd. Not prompt engineer. Not vibe coder. Not "developer who uses AI." A shepherd guides AI through terrain it cannot see: the codebase's history, the team's constraints, the deployment realities, the business context that lives nowhere in any training set. The shepherd's value is not speed. It is judgement about where to apply speed.

It is worth grounding this in numbers, because the surrounding hype is loud. A Stanford analysis of more than a hundred thousand developers across six hundred companies, looking at real code in real repositories rather than lab experiments, found that the much-quoted productivity boost of thirty to forty percent shrinks to roughly fifteen to twenty percent net once you factor in the time spent fixing what the AI got wrong. The gain is real. It is also smaller and lumpier than the demos suggest. Where AI helps most is on well-trodden ground. Where it helps least, or actively hurts, is on the complex existing systems that describe most of our day jobs.

So the question is not whether to use the tool. It is how to use it well. And the answer comes apart, I think, into four things a shepherd does. Read the terrain. Choose the path. Watch for predators. Tend the flock.

## Read the terrain

A shepherd's first job is knowing the ground.

I work on a government system. Like a lot of public-sector systems, the requirements are not a tidy specification. They are a sediment. Laws, policy decisions, edge cases that surfaced years ago and never got formally documented, exceptions that exist for reasons nobody on the current team can fully articulate. When I hand a piece of that work to a coding agent, the result is almost always wrong, even when it looks plausible. The agent reads what is in front of it. The actual requirement lives in the negative space, in the conversations and the precedents that were never written down.

Trying to brute-force this by stuffing the agent's context with every related document does not work either. The window fills up, the relevant signal gets diluted, and what comes back has the same confident tone whether it is pattern-matching to your real situation or to something superficially similar from training data. The model cannot tell which it is doing. You can.

That is the shepherd's first contribution. Not the prompt. The *framing*. Knowing which two paragraphs of which document actually matter for this change. Knowing that this requirement looks routine but interacts with that legacy module in a non-obvious way. Knowing when the gap between what the model can see and what the answer actually depends on is too wide to bridge with any prompt at all, and the right move is to not delegate this one.

The skill is unglamorous. It is the same skill senior developers have always used to onboard new hires and unblock stuck juniors. It just turns out to be the load-bearing skill for AI work too.

## Choose the path

A shepherd decides what to delegate and what to keep close.

Last year my team did a refactoring that touched around thirty similar objects in our system. The temptation, given the tools, was to point an agent at the whole thing and let it grind. I did not. I picked one object and refactored it myself, slowly and deliberately. Not because I could not have got the agent to do it, but because I wanted the pattern to come out of my hands first, with the small decisions and the second thoughts still attached to it.

Once I was happy with that one, the work changed shape. I asked the agent to look at the refactored object and the remaining ones, and to produce a task list for bringing the others into the same form. I read the list and adjusted it. Some entries were sharper than I would have written, and others missed subtleties that came from having lived inside that first object. Then I let the agent work through the list, one object at a time, with me reviewing each result before it went anywhere near main.

The shape of the work is what matters. I owned the design, delegated the propagation, and owned the review. The agent did the repetitive part faster than I could have, and the parts where my judgement actually mattered stayed in my hands.

That is the move I think senior developers are best positioned to make. The interesting question for a senior is no longer *can AI do this* . It is *should I be the one doing this, and if not, what does it need from me to do it well*. The first question is mostly about the tool. The second is mostly about you.

## Watch for predators

A shepherd verifies. Always.

Some time ago we migrated a Quarkus application from JPA to Jakarta Data. Almost immediately, our tests started failing in a strange way. Data we had updated inside a transaction was invisible when we read it back inside the same transaction. We asked a coding agent for help. The reply came fast and confident: flush the session.

It was wrong. Jakarta Data uses stateless sessions. There is nothing to flush. The advice was a fluent answer to a different and more familiar question. It was the one the model had seen many times in its training data, where flushing a JPA EntityManager genuinely is the fix. Our problem looked similar from the outside and was structurally different underneath.

We tried feeding the agent the relevant Jakarta Data documentation. It did not help. In the end we did what we would always have done. We built a minimal reproducer, narrowed the behaviour down to a specific interaction, and reported the issue upstream. The Quarkus team confirmed it, and the root cause turned out to live in Hibernate itself.

The lesson is not don't trust AI. The lesson is sharper. A model produces fluent output with the same tone whether it is right or wrong, so confidence is not a signal. It is noise. And the moment when it is most dangerous is precisely the moment a senior developer is in the best position to handle: when the answer pattern-matches to something common but the actual problem sits just outside the model's training. You have to know enough to smell it. The smell is the moat.

## Tend the flock

A shepherd does not work alone.

My team had been using AI assistants for a while, with results all over the map. Some people loved them. Some people had quietly stopped trying. The difference, when I looked at it, was not talent or seniority. It was process. The people getting the worst results were trying to solve whole problems in a single prompt: *here is the issue, fix it*. The people getting the best results were doing what we have always done with hard problems, just with a collaborator: planning, then implementing, then validating, in distinct steps with their own outputs.

So I started sharing that explicitly. Not as a productivity hack but as a re-statement of the obvious. Do not ask the agent to do everything at once. Ask it to lay out a plan you can read. Then ask it to implement one piece of the plan. Then check that piece against what you actually wanted. Smaller, more specific steps almost always beat one ambitious prompt. We were rediscovering the software development process. AI had not changed it. It had just rewarded teams that already had one and punished teams that did not.

That is the framing I think is most useful for the people you work with. AI did not break engineering. It made the gap between teams with a real process and teams without one suddenly very visible, because the tool magnifies whatever habits it lands on. A shepherd's job is not to police prompts. It is to make those habits explicit, share what is working, and help less experienced developers build the instincts that would otherwise take ten years and a few production outages to acquire.

## The doorman's dignity

The doorman in Sutherland's story was not insecure about his role. He knew what he was actually doing. The consultant was the one who was confused.

Right now, a lot of senior developers are letting consultants confuse them. The viral demos, the executive quotes about replacing engineers, the LinkedIn posts from someone who built a to-do app with a single prompt are all reports written by people watching us type. They measure the visible action and miss the actual function.

The typing was always the door opening. The job was reading the terrain, choosing the path, watching for predators, tending the flock. AI did not take any of that away. It just made it the part that obviously matters now, because the part it could automate has been automated.

Your seniority is not a liability in this transition. It is the moat. It always was.

*This article expands on the AI Shepherd concept from a conference talk Elma Westergren and I have given on developer identity in the AI era. The core framing comes from Elma's work as an occupational therapist. What is really at stake when our tools change is occupational identity, not just productivity, and I am grateful for that perspective.*
