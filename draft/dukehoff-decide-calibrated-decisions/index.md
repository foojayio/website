---
title: "dukehoff-decide: Typed, Calibrated Decisions from Small Language Models"
date: "2026-01-01"
description: "Many LLM calls in Java apps are multiple choice. dukehoff-decide answers them with a small local model and returns a typed value with calibrated probabilities."
authors:
  - "eppleton"
image: "hero.jpg"
categories:
  - "AI"
  - "LLM"
  - "Library"
canonical: "https://dukehoff.com/en/blog/dukehoff-decide/"
---

Many LLM calls in business applications ask a question whose answer is one of a few options. Which
team handles this ticket? Is this message spam? How urgent is it? The usual way is to send the
question to a large model, ask for JSON, parse the text and hope the answer is one of the allowed
values. And when the model says it is sure, there is no way to tell whether that means anything.

dukehoff-decide turns such a question into a Java type and answers it with a small model that runs
locally. You get a valid value of that type, a probability for every option, and a gate that
decides on its own only where those probabilities are reliable. It is open source under the Apache
License 2.0, and version 0.1.0 is on Maven Central.

## What it looks like

```java
@Question("Triage an incoming customer support ticket")
record Triage(
    @Question("How urgent is this ticket?") Priority priority,
    @Question("Which team should handle it?") Team team,
    @Question("Does the customer threaten to cancel?") boolean churnRisk) {}

Decision<Triage> d = triage.decide(State.of(ticketText));
d.value();                      // always a valid Triage
d.distribution("priority");     // Map<Priority, Double>

switch (triage.requireConfidence(0.9).decide(state)) {
    case Outcome.Confident<Triage>(var dec)          -> route(dec.value());
    case Outcome.Uncertain<Triage>(var dec, var why) -> sendToHuman(state, dec, why);
}
```

Every field becomes a question with a fixed set of options: a boolean is yes or no, an enum has one
option per constant, a scale has one option per step. The model answers each question with a single
token, and dukehoff-decide reads the probabilities of all options from the model's
log-probabilities. There is no text to parse, so there is no invalid answer.

## Why calibration matters

Raw model probabilities are usually overconfident, and small models often favour particular
options. So you calibrate once, offline, with a few hundred historical cases whose answer you know,
for example tickets and the team that finally handled them. dukehoff-decide fits a correction per
field and writes a report: accuracy before and after, how many cases a gate at each threshold would
decide automatically, how accurate those are, and which options get confused.

The report is worth reading. A warning such as "option CONTACT receives 16 of 30 errors" points to an
option whose description matches too much. In our tests, rewording one such description raised
accuracy from 76% to 88–90%, more than a model 4.7 times as large did.

## What we measured

Customer messages from a public dataset, 11 categories, Qwen2.5 1.5B (4-bit) with llama.cpp,
300 examples for calibration and 300 different ones for the test:

| calibration | before | after |
|---|---:|---:|
| category correct | 87.7% | 93.7% |
| decided automatically (threshold 0.9) | 50% | 71% |
| of those correct | 94.0% | 99.5% |

Always guessing the most frequent category would be right 21.3% of the time.

The second test matters as much. On a dataset of support tickets where queue and priority mostly
cannot be inferred from the text, the models often claimed over 99% confidence. Raw scores would
have routed 42% to 65% of the tickets automatically, with under 10% of them fully correct. After
calibration, none were routed automatically. That is the right answer: the gate knows when the model
doesn't.

Details, both datasets and how to rerun them are in the
[README](https://github.com/dukehoff-gmbh/dukehoff-decide#results).

## Get it

You need Java 21 and a server with an OpenAI-compatible API that returns log-probabilities. We
verified it with Ollama and llama.cpp.

```xml
<dependency>
    <groupId>com.dukehoff.decide</groupId>
    <artifactId>dukehoff-decide-openai</artifactId>
    <version>0.1.0</version>
</dependency>
```

For Spring Boot, use `dukehoff-decide-spring-boot-starter` instead. It creates a `DecisionTask` bean
per type and comes with a health check and Micrometer metrics. Getting started, the Spring Boot setup
and the limitations are in the [README on GitHub](https://github.com/dukehoff-gmbh/dukehoff-decide).

If you want to replace calls to a large model in your application with a small one you can run
yourself, or need help with the labelled examples for calibration, [get in touch](https://dukehoff.com/en/contact/).

---

*This article was first published on [dukehoff.com](https://dukehoff.com/en/blog/dukehoff-decide/).*
