---
title: "Your Agent's Tool Calls Will Run Twice: Idempotency and Evaluation Gates in Java Services"
date: "2026-01-01"
description: "Why every LLM tool call eventually runs twice, and how idempotency keys plus evaluation gates make that safe in Java services."
authors:
  - "vishnuvardhan-reddy-kaithapuram"
image: "preview.jpg"
categories:
  - "Java"
  - "AI"
  - "LLM"
related_posts: []
---

I build production AI agents for advertiser analytics at Amazon Advertising. The audience strategy agent I work on is built with Strands on Bedrock AgentCore, its tools sit behind MCP servers, and the services around it are Java. People ask what breaks first when an agent like this leaves the demo and starts doing real work. They usually expect an answer about the model: a confident wrong figure, an answer stitched together from the wrong data. Those happen. The failures that have cost me the most time are duller. A tool call runs twice.

The sequence is mundane. The agent asks a tool to make a change. The change succeeds, but the response is slow enough that the caller sees a timeout and records the call as failed. The runtime retries the step, because retrying is what runtimes do, and the tool makes the same change a second time. The prompt was fine. The model did what it was asked. The data is wrong anyway, and a repeated write leaves a duplicate that somebody has to find and unwind by hand.

## Every call will be repeated sooner or later

A timeout tells you nothing about whether the work happened. It only says the answer did not arrive in time. Agent frameworks retry failed steps. Queues deliver at least once, so a message can arrive twice even when nothing went wrong anywhere. A connection can drop after the server finished and before the client heard about it. Each of these is normal behaviour in a distributed system, and each one can deliver the same tool call to your service twice.

Whether a repeat is safe is decided in the service, by whoever wrote the tool endpoint. The model has no say in it, and there is no prompt instruction that changes it. Read calls forgive repeats. Write calls do not, and agents earn their keep by writing: updating records, kicking off jobs, changing state that other systems then trust.

## Give the call one identity and keep its result

The standard fix is an idempotency key, and it works on agent tool calls the same way it works on payment APIs. The part that needs thought is what the key names. A random key minted per attempt is useless, because every retry then arrives looking like a new request. The key has to name the logical operation: the playbook step making the call, the tool being called, a hash of the arguments, and one component that names this run of the step. That run identifier is assigned when the step is planned and carried unchanged through every retry of that step, but the next genuine run of the same step, with the same arguments, gets a new one. Without it, a legitimate repeat of a step returns the first run's stored result and never runs at all. The same logical request produces the same key however many times it is retried, and a genuinely different request produces a different one.

When a call arrives, look the key up before running anything. If a result is already recorded, return it and skip the tool. If not, claim the key with a conditional write, and let only the caller that wins the claim run the tool. A caller that loses the claim does not run anything: it waits and returns the winner's result once that lands. Checking first and running second is not enough, because two racing callers can both find nothing stored and both run the tool before either records a result. In the services I work on, the store behind this is DynamoDB, and the conditional write is the claim. Keep the record longer than any retry can plausibly arrive. A day covers the retry horizons I have seen, and storing a result string is cheap.

A claim is not a result. If the caller holding the claim stops before it records one, the key sits claimed and empty, and a retry has to know what to do with that. Give the claim a lease: a retry that finds an expired claim takes it over. And wherever the tool can accept the key itself, pass it down, so the tool stays idempotent even on the rare path where a takeover runs it again.

The shape of it is small:

```java
public record ToolCall(String idempotencyKey, String toolName,
                       Map<String, String> arguments) {}

public interface ToolResultStore {
    Optional<String> find(String idempotencyKey);
    boolean claim(String idempotencyKey); // conditional write: first caller only
    void complete(String idempotencyKey, String result);
}

public String executeTool(ToolCall call, ToolExecutor executor) {
    Optional<String> stored = store.find(call.idempotencyKey());
    if (stored.isPresent()) {
        return stored.get(); // a retry: the tool does not run again
    }
    if (!store.claim(call.idempotencyKey())) {
        // Another caller is running this call. Wait for its result
        // instead of running the tool a second time.
        return waitForResult(call.idempotencyKey());
    }
    String result = executor.run(call.toolName(), call.arguments());
    store.complete(call.idempotencyKey(), result);
    return result;
}
```

Two details matter. The key has to be built where the logical request is known, which is the step that plans the call. Build it down in the transport layer and each retry looks like a fresh operation, because the transport cannot tell one logical request from another. And idempotency only makes a repeat safe. It does nothing about a call that should never have been made at all. That is a release problem, and it has its own habit.

## Gate the release on answers you already know

The agents I run work from playbooks: written instructions that fix what the agent may do and which data it may touch. The audience strategy agent has 24 playbooks live. Each playbook carries an evaluation set, a fixed collection of cases with known right answers, including cases where the correct behaviour is to decline because the question sits outside the playbook's scope. Every change to a playbook runs its full evaluation set before the change is released, and a failure holds the release until the playbook is fixed.

The full set, every time. Sampling is tempting once a suite grows, and a sample is how a regression in the least-used playbook reaches production unnoticed. The least-used playbook is usually the one nobody is watching. The sets stay small enough to run whole because a case earns its place by covering behaviour that matters, not by covering every phrasing a user might try.

Tool changes belong in the same gate. Change a tool's argument list and the evaluation cases that call it will show you what the model does with the new shape while the fix is still cheap. A schema change that looks harmless in review can change which tool the model picks, or what it passes, and the evaluation set is where that shows up first. Java teams already run a test suite before a release and treat a red build as a stop. An evaluation set is the same habit with different assertions: the inputs are questions, and the expected outputs are answers and actions.

## Where to start

Start with the write tools. A report generated twice wastes a minute of compute and nothing else. Writes are where a retry turns into a data problem, and where a bad release does damage that outlives the deploy. Give each write call an idempotency key, record its result, and put the playbooks that use it behind an evaluation gate. While you are there, log the key and the evaluation version alongside each call, so that when an answer is wrong you can tell whether the tool ran, how many times it ran, and which version of the playbook asked for it.

None of this is new machinery. Idempotent endpoints and pre-release test suites are settled practice in Java services, and agents need them for the same reasons every earlier client did. The model gets most of the attention in an agent project. The failures that reach your data arrive through tool calls, and tool calls are plain Java engineering.
