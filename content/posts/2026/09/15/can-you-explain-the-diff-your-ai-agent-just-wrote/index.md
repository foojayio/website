---
title: "Can You Explain the Diff Your AI Agent Just Wrote?"
date: "2026-09-15T14:12:01+00:00"
lastmod: "2026-09-16T06:30:16+00:00"
description: "Every Java developer has been in a code review where the question is simple: What does this method do? The answer used to tell you something."
authors:
  - "viktoria-evdokimova"
image: "Frame-5.jpg"
categories:
  - "AI"
  - "IntelliJ IDEA"
  - "Java"
  - "Kotlin"
  - "LLM"
related_posts:
  - "build-secure-ai-chat-applications-with-boxlang-rag-ollama-and-amazon-bedrock-with-dan-card"
  - "ask-a-lille-dev-what-java-developers-really-think-about-quality-frameworks-communities-and-careers"
  - "spring-ai-agents-no-second-runtime"
  - "jc-ai-newsletter-16"
frozen: true
---

Every Java developer has been in a code review where the question is simple: "What does this method do?"

The answer used to tell you something. The developer who wrote it would describe the intent, the edge cases, the trade-off they considered and rejected. The code review was a conversation about the invariant the code protects.

Now the answer is sometimes: "The agent wrote it."

The code compiles. The tests pass. The linter is clean. Nobody in the review can explain the architecture of the PR, including the person who submitted it.

## The one-month timeline

Two independent customer development interviews, from developers who never spoke to each other, converged on the same number. A month of agent-driven development — where the agent writes the code and the developer reviews the diff — is enough to lose the mental model.

The pattern is the same in both reports. The developer starts by reviewing every line. Within a week, they review the summary and skim the diff. Within two weeks, they approve based on the green tests. Within a month, they are asked a question about the code in review and cannot answer it.

The code works. The model is gone. The community calls this [comprehension debt](https://www.reddit.com/r/webdev/comments/1rxznlw/comprehension_debt_the_silent_time_bomb_a_lot_of/).

## What green tests don't prove

A Java compiler checks types. It does not check intent. A method that compiles can still violate the invariant it was written to protect.

```
// Compiles. Tests pass. The invariant is broken.
public void processPayment(PaymentRequest request) {
    var authorization = authorize(request);
    // Agent "optimized" by moving the audit log before authorization completes.
    // The invariant — "audit only after authorization" — is now violated,
    // but the test mocks authorization, so it never notices.
    auditLog.record(request, authorization.getId());
    complete(authorization);
}
```

The test suite checks the code against its own assumptions. If the assumptions are wrong, the tests pass and the bug ships. Auto-LGTM checks that the pipeline ran, not that the author can explain the result.

All three verify the artifact. None of them verify the author's model of the artifact. A developer who cannot explain why the code does what it does can still ship a PR that compiles, passes tests, and gets an auto-merge.

## The day development stops

There is a moment where the agent fails to fix the next bug, and the developer cannot step in.

The developer who reviewed every line can step in. The developer who approved based on green tests for a month cannot — they don't know where the invariant lives, what the side effects are, or which module owns the state. Development stops because the human ran out of context, not because the agent ran out of capability.

Reddit practitioners describe this as the limit of ["vibe coding"](https://www.reddit.com/r/ClaudeAI/comments/1uuf59z/im_a_vibe_coder_and_im_scared_that_i_have_no_idea/) — accepting agent output without rebuilding a mental model. It works until the agent fails. Then the developer who was vibing is stuck. They can't debug what they don't understand, and they can't understand what they never read.

## Explain it in your own words

A practice reported from custdev: before accepting a diff, explain the change in your own words, without looking at the agent's summary.

Close the diff. Close the agent's summary. Open a blank document. Write: "This change does X because Y. If reverted, Z breaks." If you can't fill in X, Y, and Z from memory, you don't understand the change.

A [Reddit team practice](https://www.reddit.com/r/ExperiencedDevs/comments/1vg0cx8/what_do_you_do_when_a_developer_submits_ai/) adds a social version: every PR needs a named engineer who can later explain the change from memory. LLM use is allowed, but one person must be able to answer for the result and the breakage.

## The Java lesson: where the invariant lives

In Java, invariants often live between consumers, services and stores — not in a single class. The type system doesn't enforce them. The compiler doesn't check them. They live in the developer's head, and they are the first thing to go when the developer stops reading the code.

The fix is not more tests. Tests check the code against its own assumptions. The fix is to put the invariant where a test can see it — and where a human can see it in review.

```
/**
 * Invariant: authorization must complete before the audit log records it.
 * If this ordering breaks, the audit trail can reference unauthorized payments.
 */
public void processPayment(PaymentRequest request) {
    var authorization = authorize(request);
    if (!authorization.isCompleted()) {
        throw new IllegalStateException("Authorization must complete before audit");
    }
    auditLog.record(request, authorization.getId());
    complete(authorization);
}
```

The comment states the invariant. The check enforces it at runtime. The test names it. The developer reading the diff in review sees all three in one place — and that is where comprehension gets rebuilt.

This snippet is illustrative. It is not code from any interview. The point is the pattern: the invariant lives between the lines, and the agent can move those lines without understanding the sentence they form.

## What a debugger adds that the chat can't

The chat transcript keeps the text. The IDE keeps the running program. Understanding lives in the gap between them.

When the agent says "the code does X," that is a claim. When you set a breakpoint at the decision point and watch the variable, that is a fact. The variable value at the breakpoint is an observation the chat cannot give you. It is also the moment where the mental model gets rebuilt: you see the state, you see the decision point, you see why the code does what it does.

This is where tools like [Explyt](https://plugins.jetbrains.com/plugin/27979-explyt-ai-agent) come in for JetBrains IDEs — the agent gets the IDE run and the debugger, and the developer gets structured test results and variable values instead of "the tests passed."

## A checklist for any agent, before you accept a diff

* Close the diff and the agent's summary. Explain the change in your own words.
* Name the invariant the change touches. If you can't, reopen the diff.
* Set a breakpoint at the decision point. Watch the variable. Confirm the agent's claim.
* Check: does the test name the invariant, or does it only check the output?
* If you can't explain it after step 3, don't merge it.

The check takes a minute. The cost of skipping it shows up in a month.

## Disclosure and where to go from here

We build [Explyt](https://plugins.jetbrains.com/plugin/27979-explyt-ai-agent), an AI agent for JetBrains IDEs. The custdev interviews and the one-month timeline are real. The Java snippet is illustrative — it is not code from any interview. The claims about the IDE tools are limited to what the documentation says they do.

The full write-up, with the two custdev timelines and the "explain it in your own words" practice, is on the Explyt blog: [The agent wrote the code, and you lost the model](https://explyt.ai/en/blog/comprehension-debt-when-ai-writes-the-code).

## Related Posts

* [Did Your AI Agent Run the Debugger? One JVM Bug, Two Agent Runs](https://foojay.io/today/did-your-ai-agent-run-the-debugger-one-jvm-bug-two-agent-runs/)
* [Debugging Is Invariant Discovery: What One Kafka Session Taught Us About AI Agents](https://foojay.io/today/debugging-is-invariant-discovery-what-one-kafka-session-taught-us-about-ai-agents/)

## Sources

* Anthropic, Claude Code best practices: <https://code.claude.com/docs/en/best-practices>
* Reddit, Comprehension debt — the silent time bomb: <https://www.reddit.com/r/webdev/comments/1rxznlw/comprehension_debt_the_silent_time_bomb_a_lot_of/>
* Reddit, What do you do when a developer submits AI generated code they clearly don't understand?: <https://www.reddit.com/r/ExperiencedDevs/comments/1vg0cx8/what_do_you_do_when_a_developer_submits_ai/>
* Reddit, I'm a vibe coder and I'm scared: <https://www.reddit.com/r/ClaudeAI/comments/1uuf59z/im_a_vibe_coder_and_im_scared_that_i_have_no_idea/>
* Explyt blog, The agent wrote the code, and you lost the model: <https://explyt.ai/en/blog/comprehension-debt-when-ai-writes-the-code>
