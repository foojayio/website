---
title: "One Does Not Simply Vibe-Code My Executor"
date: "2026-11-19"
description: "A coding assistant was given a real feature request against the execution engine of an open-source orchestrator. Three pull requests later, the code was plausible and still wrong. The author, who maintains that engine, explains why: the assistant had a generic world model of executors and a grep-built model of the code, but not the human one that holds the boundaries and the reasons. Then he hands it that model, and it works."
authors:
  - "loic-mathieu"
image: "vide-coding-vs-context-engineering.jpg"
categories:
  - "Java"
  - "AI"
  - "GenAI"
related_posts: []
canonical: "https://example.com/original/"
---

A coding assistant was given a real feature request against the execution engine of an open-source orchestrator. Three pull requests later, the code was plausible and still wrong. The author, who maintains that engine, explains why: the assistant had a generic world model of executors and a grep-built model of the code, but not the human one that holds the boundaries and the reasons. Then he hands it that model, and it works.

## The request

A month ago, a user asked us for a small feature: when one task in a parallel branch fails, kill the others. Fail fast. It makes sense. If the execution is going to fail anyway, why let the other branches burn CPU?

Good idea, no time. The team was deep in a major release. So a colleague of mine, who is not a developer, said "let me try with my coding assistant". We were all playing with these tools at the time, and he had a real feature request and a real codebase to point one at: the execution engine of [Kestra](https://kestra.io), an open-source orchestration platform, which I have been looking after for almost four years.

He gave it the ticket. It read the code, wrote the code, opened a PR. And it looked plausible.

## The review

Then he asked me to review it, and he was a bit nervous about that. I am known to be picky, and he thought I would not love a non-developer doing development. I decided to treat it like any external PR. We are open source, I review external contributions all the time, and I read those more carefully than internal ones. On every single one, I ask myself the same thing: would I have done it this way?

And the answer was… no.

My main comment on the first version:

> I'm not convinced this is the best place to do that, and the code seems overly complicated.

He pasted that into the assistant. Second version. Not better, at least not for something going into the Executor: new concepts I did not want, some backward-compatibility breakage, and it was using `KILL` in a way I was pretty sure was wrong.

Third round, and this time I told it exactly where the code should go. Still not there. When my colleague asked it once more to take my feedback into account, it answered with something like:

> I don't understand what is wrong with my approach, but I'll try to make Loïc happy.

We closed the PR.

## Two kinds of world models

_Everything below is my understanding of how these tools work, plus a fair amount of guessing about what happened._

The Executor is the most complex part of the system. It runs the execution logic: errors, retries, pauses, SLAs, quotas, concurrency limits. It sends tasks to the Worker and talks to the Scheduler for triggered flows. It also drives the flowable tasks, the special tasks that carry orchestration logic themselves (parallel branches, loops, conditions, subflows). When something has to go into the Executor, I usually know where before I open the file.

That "knowing where" is a picture of the Executor in my head: how it talks to the other components, what each flowable task is responsible for, what must never happen. AI researchers have a name for this kind of thing: _a world model_, an internal representation of an environment that lets you predict what happens if you act on it.

### The human one

Mine was not built in a session. It comes from 22 years of writing software, almost four of them inside this Executor. The 22 years give me the generic part: what concurrency bugs look like, why backward compatibility matters more than elegance in a component other people depend on, which shortcuts come back to bite you. The four years give me the specific part: why `resolveState()` is where it is, which incident led to which guard, which design we tried and reverted, which flowable task quietly relies on an ordering nobody wrote down.

It is not something I could draw accurately. It is more like a mind map, boxes and labelled arrows everywhere: this method does that, this thing depends on that one, if I touch this, that breaks. It is fuzzy. I could not tell you a line number. But it is broad, it is about causes, and it updates every time the code changes, because I review those changes.

### The machine ones

Do LLMs have world models? Apparently yes, at least to some degree. A well-known example is a model trained only on Othello move sequences that turned out to encode the board state internally ([Li et al., 2023](https://arxiv.org/abs/2210.13382)). Other work found linear representations of geography and time inside general language models ([Gurnee and Tegmark, 2023](https://arxiv.org/abs/2310.02207)). So a frontier model surely carries some kind of world model of Java, of common design patterns, of what an "executor" usually looks like in a workflow engine.

The catch is that this is a world model of executors in general. Not of my Executor. Mine was not in the training data, or if some old version was, not with the reasons behind how it is built.

So the model of my specific code has to be built on the fly, in the context window, at every request. Each call to the LLM is stateless. Whatever it knows about this codebase has to be in the prompt, and the prompt has a size limit in tokens. A million tokens today on the big models, which is a lot, but it is still a limit, and something has to choose what goes in. That something is the harness: the `AGENT.md`, the tools, the conversation so far, and usually a memory file where it writes down what it found.

And how does it find things? Regex, `sed` and `awk`, basically. It takes keywords from the prompt, greps file names, then file contents, reads what matches and dumps it in the context. Then the LLM does its magic, which even the people building them admit they do not fully understand.

So on the machine side there are really two world models. One in the weights: broad, generic, frozen. One built per session from whatever grep returned: narrow, very literal, thrown away at the end.

### Side by side

|  | Maintainer | LLM weights | LLM context |
| - | - | - | - |
| __Scope__ | One specific system | Executors and Java in general | Whatever the search returned |
| __Built from__ | 22 years of practice, ~4 years in this code, reviews, incidents | Training data | grep, file reads, the conversation |
| __Lifetime__ | Persistent, updated with every change | Frozen at training time | One session |
| __Strong on__ | Boundaries, responsibilities, reasons, traps | Patterns, idioms, syntax | Exact signatures and line numbers |
| __Weak on__ | Exact details, line numbers | This particular codebase | The big picture, what is relevant |

Seen this way, the review makes sense. My model has the big picture and the traps, but not the details. The assistant's has the details and not the picture: exact method signatures, exact lines, some of them irrelevant, some from the wrong corner of the codebase because a keyword matched, plus a generic idea of what an executor should look like that it mistook for this one. And it thinks through grep.

For a feature that had to land in one precise layer and reuse a mechanism that already existed two layers down, that was the whole problem. Not bad code. A bad map.

## Handing over the map

So I wondered: if I give it my map, or the relevant piece of it, does it get there? I wrote down what I would have done:
- A new capability defines its boundary through an interface.
- State resolution stays in the existing `resolveState()` method.
- After `resolveState()`, if the flowable task implements the new interface, the Executor asks it for its running task runs and interrupts them.
- Interrupting a task run on the Worker without killing the whole execution (which would kill the other tasks running in parallel, the exact opposite of what we want) means extending the kill mechanism from executions to task runs. A new `ExecutionKilledTaskRuns` message, a child of the existing `ExecutionKilled`, carries a list of task run IDs. When the Worker receives it, it interrupts the task runs already running and registers the ones that are not yet, in case they show up later. If a registered task run shows up and gets interrupted, the registration is removed.

There is no code in that list. It is boundaries, responsibilities, and one message. It is exactly the part of my world model that grep cannot find, because it is not in any file. It is in the reasons.

With that, the assistant did more or less what I would have done. A couple more prompts to fix and clean up, and it was in: in the right place, with the right shape.

So the answer was yes.

## What I learned

__World model caused the failure__: The three versions were written in the wrong place, against a picture of an executor that was not ours. Asking for "better code" in review did not help, because the AI used its own context and couldn’t invent mine.

__Plan better__: Four bullets of boundaries, responsibilities and one message were enough. That is a fraction of the time it would have taken me to write the feature, and the assistant did the rest. For core components, I now think the right split is: the person who holds the world model writes the plan, the tool writes the code.

__What the assistant needs is my brain__: Comments and docs describe what the code does. The reasons why it is shaped that way (which layer owns what, which mechanism to reuse, what must never happen) mostly live in maintainers' heads. If we want these tools to work on the Executor without me in the loop, that part has to be written down where the harness can find it, in the `AGENT.md` or in design notes next to the code.

__Non-developers can contribute to a core component, with a map.__ My colleague got to a merged feature in the end. Not by prompting harder, but by getting the plan from the person who knew where it had to go.
