---
title: "Building Token Budgets for Spring AI: The Problems I Didn't Expect"
date: "2026-01-01"
description: "A Spring Boot starter that budgets Spring AI ChatClient calls by tokens and cost, with per-call metering, atomic reservations, Redis support, and agent call limits."
authors:
  - "ujjwal-tiwari"
image: ""
categories:
  - "Java"
  - "Spring"
  - "GenAI"
  - "Open Source"
related_posts: []
canonical: "https://dev.to/ultron03/building-token-budgets-for-spring-ai-the-problems-i-didnt-expect-lg2"
---

*Rate limiters count requests. LLM bills count tokens.*

Picture an `/agent` endpoint in a Spring Boot app. It has two tools, and on a bad prompt the model decides it needs to call one of them again. And again. One HTTP request, forty model calls, each one sending the whole conversation so far back to the provider. Your rate limiter is happy, because it saw one request.

Your LLM provider isn't. It just billed you for forty calls.

That gap is why I wrote [Token Allowance](https://github.com/Ultron03/token-allowance), a Spring Boot starter that puts token and dollar budgets on Spring AI `ChatClient` calls: per user, per tenant, per session, plus a cap on how many model calls one request may make. This post is about how it works and the parts that turned out harder than they looked.

![Token Allowance demo showing an agent stopped after five model calls and dollar budget enforcement](https://raw.githubusercontent.com/Ultron03/token-allowance-demo/main/demo/agent-and-dollars.gif)

*The agent and dollar-budget walkthrough uses the Gemini profile and requires a Gemini API key. The basic chat and 429 walkthrough also runs locally with Ollama.*

## Why counting requests is the wrong unit

Bucket4j or Resilience4j count requests. For an LLM app, the cost of one request depends on three things a request counter can't see:

- How many tokens went in (a chat with 30 messages of history costs far more than "hi").
- How many came out.
- How many model calls the request made, because every tool-loop round is a separate paid call.

So the budget has to be in tokens or dollars, and it has to be checked per model call, not per HTTP request.

## What using it looks like

Add the starter next to your Spring AI model starter:

```xml
<dependency>
    <groupId>io.github.ultron03</groupId>
    <artifactId>token-allowance-spring-boot-starter</artifactId>
    <version>0.3.1</version>
</dependency>
```

Set prices (dollars per million tokens, copied from the provider's pricing page) and limits:

```yaml
token-allowance:
  pricing:
    models:
      - name: gpt-4o-mini
        input-per-mtok: "0.15"
        output-per-mtok: "0.60"
  limits:
    - { scope: user, window: daily, max-cost: "0.50" }
    - { scope: global, window: monthly, max-cost: "200.00" }
  per-request:
    max-model-calls: 10
```

Your controller doesn't change. Inject the `ChatClient.Builder` Spring AI gives you and build from it. When a user has spent their 50 cents for the day, the next request gets this instead of another paid call:

```http
HTTP/1.1 429 Too Many Requests
Retry-After: 41520
Content-Type: application/problem+json

{
  "title": "Token budget exceeded",
  "status": 429,
  "detail": "user daily budget exceeded"
}
```

`Retry-After` is the number of seconds until the day's window resets. The user comes from an advisor param on the call, else the Spring Security principal, else a header, if you turn it on with subject.header and your gateway sets it.

## Where the check sits matters more than I expected

Spring AI runs every `ChatClient` call through a chain of advisors, ordered by `getOrder()`. When you register tools, `ChatClient` quietly adds its own `ToolCallingAdvisor` to that chain, and the tool loop lives inside it: it calls the rest of the chain, sees a tool call in the answer, runs the tool, and calls the rest of the chain again.

Before writing any library code I wrote a small spike: a counting advisor placed before `ToolCallingAdvisor`, another placed after it, and a fake model that asks for one tool call and then answers. The one before counted 1. The one after counted 2.

| Advisor position | Calls counted |
|---|---:|
| Before ToolCallingAdvisor | 1 |
| After ToolCallingAdvisor | 2 |

That one number decides the design.

An advisor **before** the tool loop runs once per request and sees the usage of all rounds added up. Good place to ask "is this user already over budget?", useless for stopping a loop halfway.

An advisor **after** it runs once per model call and sees each call's own usage. That is the only place where you can count calls and stop at call number 10.

Meter in both places and you charge every tool loop twice. So Token Allowance has two advisors with two different jobs. Admission sits before the tool loop and denies a request whose budget is already used up. Metering sits right before the model and does the per-call accounting.

## Check-then-call has a race in it

The obvious version is: read the user's spend, compare with the cap, make the call, add the usage.

Two parallel requests from the same user both read "40 cents spent of 50". Each request estimates a cost of 8 cents. Both pass the check because both see 10 cents remaining. They then both make their calls, and the actual spend reaches 56 cents.

The cap means nothing under load.

So metering reserves first. Before each model call it estimates the cost and adds that estimate to the budget as reserved spend, in one atomic check-and-add across every budget the call counts against, such as user, tenant and global. If any of them has no room, nothing is reserved and the call doesn't happen.

With reservations, the first request takes the balance from 40 to 48 cents. The second request then sees only 2 cents remaining and gets refused because another 8-cent reservation would exceed the 50-cent limit.

After the call, the reservation is replaced with the usage the provider reported.

The estimate is deliberately crude: one token per three characters of prompt, plus the request's max output tokens (or 1,024 if none is set). For English and code that's above the real count, which is the side you want to be wrong on.

It also has a cost I didn't see coming until I watched the demo. With 1,024 output tokens reserved, a user with 800 tokens left gets refused, even when their answer would have used 100. From their side it looks like the app is lying about their balance. 0.3.0 adds `estimate.output-tokens`, so you can reserve a typical answer length instead. A longer answer is still charged in full, so the budget can go over by the difference. You get to choose which way it's wrong.

## Money is a `long`

Costs are stored as micro-dollars in a `long`, never a `double`. Not for accuracy (floating-point error per call is far smaller than a stale price), but because limit comparisons have to be exact, Redis `INCRBY` only works on integers, and tests need to be deterministic.

Each call's cost is rounded up once, on the total, so rounding never favours the user over the cap.

## The providers don't agree on what "input tokens" means

This one surprised me most. OpenAI's `prompt_tokens` includes tokens read from the prompt cache. Anthropic's `input_tokens` doesn't: it counts only the tokens after the last cache breakpoint, and cache reads and writes are reported separately. A cached Anthropic call with a big system prompt can report `input_tokens: 12`.

Spring AI passes both through as `promptTokens`. If you price that number directly, cached Anthropic calls look almost free. The metering advisor normalises Anthropic usage, so "input" always means all input tokens, and cache reads and writes are priced on their own.

Streaming has its own variant. With Ollama, every chunk carries a usage object, but it's 0/0 until the final chunk. With an OpenAI-compatible endpoint and stream usage turned off, every chunk is 0/0, forever.

So "usage missing" can't be detected by type or null; it has to be "zero tokens reported". When that happens the reserved estimate is charged instead, and a metric records that it was an estimate.

## Sharing budgets across instances

The default store is in memory, which is fine for one instance. With more than one, each instance would hand out the full budget on its own, so there is a Redis store.

Reserve and settle are each a single Lua script, so the check-and-add stays atomic across instances. All keys share a `{ta}` hash tag, so they land on one Redis Cluster node and one script can touch all of them.

The harder question was what to do when Redis is down. There's no universally right answer, so it's a setting: `fail-closed` returns 503, `fail-open` lets calls through unmetered. The default follows the mode: in `observe` mode, where you only log over-budget calls, it fails open; in `enforce` mode it fails closed.

## Stopping the agent

The per-request cap is what deals with the forty-call loop from the start of this post. Set it app-wide with `per-request.max-model-calls`, or per endpoint on the client you hand the agent:

```java
ChatClient agentClient = builder
    .defaultAdvisors(a -> a
        .param(RequestLimitParams.MAX_MODEL_CALLS, 5)
        .param(RequestLimitParams.MAX_REQUEST_COST, "0.05"))
    .build();
```

After five model calls, the next one is refused with a 429 explaining which cap it hit.

In the demo, an agent walking an org chart through a `managerOf` tool gets cut off after five calls. The agent walkthrough uses the Gemini profile: small local models like `llama3.2` often stop calling the tool on their own before reaching the limit.

On Gemini's free tier, priced at Google's published rates, those five calls came to $0.000418.

## What it doesn't do

Things you should know before you rely on it:

- **Costs are estimates, not invoices.** They're your configured prices times the provider's reported usage. Compare them with your bill, and start in `observe` mode for that reason.
- **You type the prices in.** No provider has an API that returns prices. A bundled price list is the thing I most want to add next.
- **Only `ChatClient` calls are counted.** Calling a `ChatModel` directly, or embedding models, skips the advisors.
- **Windows are fixed.** A minute cap can be spent at 10:00:59 and again at 10:01:00.
- **A budget can go over a little.** A call that has already happened is always charged, so parallel calls can overshoot by the gap between their estimates and their real usage.

## Try it

The [demo app](https://github.com/Ultron03/token-allowance-demo) runs against a local Ollama model for the basic chat and budget walkthrough, so that part costs nothing. The agent and dollar-budget walkthrough uses Gemini and requires a free Gemini API key.

It comes with terminal walkthroughs and a small web console where you can watch users and a tenant run out of budget.

The library itself is on [GitHub](https://github.com/Ultron03/token-allowance) and Maven Central under `io.github.ultron03`, Apache 2.0.

If you run Spring AI in production, I'd like to hear which cap you'd actually turn on first, and which one is missing. Issues and discussions are open.
