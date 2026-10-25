---
title: "The tests passed. The feature had never worked."
date: "2026-10-28"
description: "With an AI assistant, the test, the example and the docs may all come from the process that wrote the feature. Two ways to check."
authors:
  - "mauro-mura"
image: "cover.jpg"
categories:
  - "Java"
  - "AI"
  - "Testing"
canonical: "https://bitautonomi.substack.com/p/the-tests-passed-the-feature-had"
---

Ask an AI assistant for a behaviour that retries with backoff, and a few minutes later there's
the class, a test that exercises it, an example that shows it off and a page of documentation
that explains it. Each of them looks like evidence that the feature works and that someone needs
it.

I've been building [Agenor](https://github.com/mauro-mura/agenor), a multi-agent framework for the JVM, with an AI assistant since the
first commit, a little over a year ago. It's on Maven Central in nine modules. The mistakes I've
made are the ones every Java developer knows. The rate at which surface piles up around them is
new.

## Two behaviours that never ran

Testing a class and testing the path a user takes through it are different things. Agenor still
had two features that fell in that gap. A user picks a behaviour with an annotation such as
`@Behavior(type = CYCLIC)` and never names the class behind it. Of the fifteen constants on offer
until 0.28.0, `EVENT_DRIVEN` and `WAKER` had never worked through the annotation, though each had
a passing unit test of its class. Neither reached a user. 0.28.0 deprecated them and 0.30.0
removed them, before Agenor's first release on Maven Central.

What let them through is the subject of this piece.

## Four witnesses from one source

Each constant came with its class and its test, and some with an example of their own. The same
process wrote all of them. The test uses the class because it was written to test it, and the
example names it because it was written to show it. The repository grows its own proof of being
used.

There are two questions worth asking about a feature: does it work through the user's entry
point, and does anyone need it? Evidence written alongside the feature can't answer either on its
own, so the rest has to come from outside the process that produced it. Outside means written for
some purpose other than showing the feature off, and it can still be written with AI.

## More agents, same repository

The usual answer is more structure, with one agent writing the tests and another reviewing them.
A second agent can catch implementation errors the first one missed, and if the tester starts
from what a user writes rather than from the diff, it could have caught these two. Whether anyone
needs the feature is another matter. Every role reads the same repository, and none knows who
will use the code.

## Test from the user's side

The first countermeasure is old advice. It's worth repeating because an assistant makes skipping
it invisible. The test of the class arrives on its own, and the test of the path has to be asked
for. For `WAKER` that's an agent declaring `@Behavior(type = WAKER)` that expects to wake up. The
same goes for a configuration key or an HTTP endpoint.

## Count without the witnesses

You can't know who needs a feature before someone uses the library. What can be counted is
which code in your own repository writes each name, and that's a narrower question. In Agenor a
script does that count, and it follows three rules that work in any language.

Tests don't count, in any module. A type used only by its own test looks used, and that's the
case worth finding.

The dedicated example doesn't count either. Through 0.29.0 the only example naming `RetryBehavior`
was `RetryExample`, whose whole job was to show it.

A type counts only where its name is written. `PingPongExample` calls
`runtime.getAgentDirectory().findAgents(...)`, so it uses an `AgentDirectory` without ever
writing the name, and it doesn't count. What's being measured is how many types a newcomer has to
learn to name.

For a behaviour, the name a user writes is the constant, not the class, so the script counts
constants too. Counted that way, the behaviour constants stood out. Across 90 example files,
`CYCLIC` was named by 18, `ONE_SHOT` and `FSM` by two each, and the other twelve by their own
demonstration or by no example at all. The two broken ones turned up in the cleanup that started
from that count.

At `v0.36.0` Agenor has 264 public top-level types, and the count flags none of them for
deprecation. About a third are named in the documentation but in no example. Those are the ones to
look at one by one. Code can reach a type through a getter without writing its name, so a low count
on its own doesn't mean nobody needs it.

## Prune while it's cheap

0.30.0 removed all twelve deprecated constants, along with the eleven annotation elements that
configured them, the seven behaviour classes behind them and seven other types nothing referenced.
All of it was a breaking change, and none of it had reached Maven Central, so the only one affected
was me. With even one user, the same cleanup would have needed a deprecation period for every
constant.

## Where the answer comes from

A test from the user's side tells you whether a feature works. A count tells you where to look for
the ones nobody needs, but it can't settle it. That answer comes from the first person whose code
imports one of these types.

The script is [`tools/api-census.sh`](https://github.com/mauro-mura/agenor/blob/v0.36.0/tools/api-census.sh) at tag `v0.36.0`. The three rules work without it.
