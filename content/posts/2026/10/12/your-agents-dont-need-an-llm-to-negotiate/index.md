---
title: "Your agents don't need an LLM to negotiate"
date: "2026-10-12"
description: "A manager hands a job to one of three workers, and nothing in the program calls a model. It explains the protocol, and what doesn't survive a restart."
authors:
  - "mauro-mura"
image: "cover.jpg"
categories:
  - "AI"
  - "Design Patterns"
canonical: "https://bitautonomi.substack.com/p/your-agents-dont-need-an-llm-to-negotiate"
---

In agents that negotiate, the negotiating usually ends up inside a language model. You describe
the situation in a prompt, the model answers, and you parse what comes back. It works, and it
costs a network round trip and a line on a bill for every decision.

For the narrow case of handing work to whoever should do it, there's a protocol that predates
all of this. It's called Contract Net, and it's small enough to read in an afternoon.
[Agenor](https://github.com/mauro-mura/agenor) ships it. The example below has a manager hand a job to one of three workers in a single JVM, and its imports
contain nothing from any model provider.

## The shape of it

One agent has work to give out. It broadcasts a call for proposals to the agents that might take
it on. Each of them answers with an offer or turns it down. The caller picks one offer, tells
that agent it's accepted, and waits for the result.

Those message kinds have names, borrowed from the agent communication languages this all comes
out of: `CFP`, then `PROPOSE` or `REFUSE`, then `AGREE`, and `INFORM` or `FAILURE` once the work
is over. Agenor has ten of them in `Performative`. Contract Net uses those six and
`CANCEL`, which the caller can send while it's still collecting.

## The side that bids

```java
@DialogueHandler(performatives = Performative.CFP)
public void handleCFP(DialogueMessage msg) {
    Task task;
    try {
        task = msg.contentAs(Task.class);
    } catch (IllegalArgumentException e) {
        task = null;
    }
    if (task == null || task.type() == null) {
        dialogue.refuse(msg, "Invalid task");
        return;
    }

    double cost = task.complexity() / efficiency * (0.9 + random.nextDouble() * 0.2);
    int time = (int) (task.complexity() / 10 / efficiency);
    dialogue.propose(msg, new Bid(cost, time));
}
```

That annotation registers the handler, and there's no other registration code. What the method
does with the call is your own code, and here it's two lines of arithmetic: the worker prices a
job by its own efficiency, with a bit of noise on top. The rule that decides what a job is worth
to an agent goes in the same place, written as ordinary Java.

## The side that chooses

```java
List<DialogueMessage> proposals = responses.stream()
    .filter(r -> r.performative() == Performative.PROPOSE)
    .toList();

if (proposals.isEmpty()) {
    return CompletableFuture.completedFuture("NO_PROPOSALS");
}

DialogueMessage best = proposals.stream()
    .min(Comparator.comparingDouble(p -> p.contentAs(Bid.class).cost()))
    .orElseThrow();

dialogue.reply(best, Performative.AGREE, task);
```

`responses` comes from `callForProposals(workerIds, task, Duration.ofSeconds(10))`, a future that
completes with whatever arrived before the timeout. A worker that's down contributes nothing and
the caller goes on without it.

Running the example gives three offers and one winner:

```
[Manager] Broadcasting CFP to 3 workers
[worker-1] PROPOSE: cost=179.16
[worker-3] PROPOSE: cost=235.86
[worker-2] PROPOSE: cost=102.97
[Manager] Proposals:
  - worker-2: cost=102.97, time=11s
  - worker-1: cost=179.16, time=16s
  - worker-3: cost=235.86, time=25s
[Manager] Selected: worker-2
[worker-2] Task done!
```

The numbers move between runs, because the bid has a random factor in it. The ranking stays the
same: the three workers have fixed efficiencies, so `worker-2` wins every time.

## What the framework contributes

The protocol is a state machine, written out in `ContractNetProtocol`. A `PROPOSE` or a `REFUSE`
leaves the caller in `AWAITING_RESPONSE`, still collecting. An `AGREE` moves it to `AGREED`. An
`INFORM` closes it at `COMPLETED` and a `FAILURE` at `FAILED`.

It also contributes the rule for who does the work after an `AGREE`: under Contract Net it's the
agent that made the offer, and Agenor asks `Protocol.senderPerforms` because the answer changes
from one protocol to the next.

## Where it stops

A message that's out of sequence for the current state, like a `PROPOSE` after the conversation
is already `AGREED`, doesn't block anything: it still enters the history and the state machine
still runs its transition, which for these protocols leaves the state unchanged. The only effect
is a WARN log line naming the conversation, the sender and the state. Throwing an exception there
would have broken every peer already out there that doesn't follow the protocol to the letter.
The practical result, for a peer that ignores the rules, is the same it would get with no check
at all: only that log line changes.

Conversation state lives in memory, in each agent's own process. There's no central registry of
conversations. Every agent keeps only the ones it takes part in.

A restart wipes that state. The agent loses the conversations it was tracking and the commitments
it had recorded, and the peer gets no notification. What's left is the timeout the peer armed
when it called, and that timeout is the failover mechanism. So don't build anything durable on a
`Conversation`. If a business process has to survive a restart, keep it in the agent's own
persistent state (`@Persist`), and use dialogue to carry the steps.

There's a serialisation trap left. `contentAs()` converts the message's content, which is why
the example calls it instead of casting `msg.content()` to `Task`: that cast works only as long
as every agent shares a JVM, but put a transport underneath that serialises, and the payload
arrives as a `Map`, so the cast throws.

## Where a model belongs

A model belongs where the decision doesn't fit into a cost function: reading a job that arrived
as prose, or weighing offers that don't reduce to a single number. An agent can call a model for
that decision, inside the same handler. The example above gets from a call for proposals to a
finished job without calling a model.

The code above is `ContractNetExample`, in the examples module at tag [`v0.35.0`](https://github.com/mauro-mura/agenor/tree/v0.35.0).
