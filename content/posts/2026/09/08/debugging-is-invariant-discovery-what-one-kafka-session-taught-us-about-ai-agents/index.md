---
title: "Debugging Is Invariant Discovery: What One Kafka Session Taught Us About AI Agents"
date: "2026-09-08T14:14:36+00:00"
description: "#foojay-site-content for WordPress credentials (already have them from the first post). 2. foojay.io/wp-admin -> new post, paste this Markdown, Save…"
authors:
  - "viktoria-evdokimova"
image: "generated-image-2026-09-08T135855.679-e1788876775683.png"
categories:
  - "AI"
  - "Debugging"
  - "GenAI"
  - "Jakarta EE"
  - "Java"
  - "Java Beginner"
  - "Java Core"
  - "JavaFX"
  - "JavaPro"
  - "LLM"
related_posts:
frozen: false
---

#foojay-site-content for WordPress credentials (already have them from the first post).  

2. foojay.io/wp-admin -\> new post, paste this Markdown, Save Draft, Preview in a new tab.  

3. Code blocks: "Convert to blocks" / "Code Insert", set language Java for the two Java snippets.  

4. Featured image: required. Use articles/explyt-kafka-tid-invariant/en/cover-one-symptom-ten-problems.png (1672x941,  

branded, title baked in). If the editors prefer a neutral image, a plain diagram "TID -\> async path -\> TMS + EMV" works;  

do not fabricate an IDE screenshot of the session.  

5. Categories: pick from the dropdown, e.g. "Debugging", "Kafka", "AI", "Testing".  

6. Yoast SEO  

Title (60 max): Debugging Is Invariant Discovery: A Kafka Case With an Agent (60, wc -m)  

Description (155): One Kafka symptom became many unrelated defects that all broke one rule about async  

processing. What an AI agent did, what it skipped, and how to write the rule down as a test.  

(176 -\> trim in Yoast to the first two sentences if the field complains; Yoast is a soft limit)  

Canonical: leave empty. This is a different text from the site article, not a cross-post.  

7. Related Posts: the three links at the end were checked live on 2026-09-08 (HTTP 200, link text = page H1).  

8. Keep as draft and announce in #foojay-site-content, or schedule on a free date and announce the date.  

Style rules applied: short paragraphs, every code block with a language, no Markdown tables, Related Posts at the end,  

one product link in the disclosure section only.  

--\>  

# Debugging Is Invariant Discovery: What One Kafka Session Taught Us About AI Agents  

Every Java team has a sentence like this one somewhere:  

\*\*Any operation on a TID must go through the single asynchronous path and must not let TMS and EMV drift apart.\*\*  

It is the kind of rule that is obvious once said and invisible until then. A terminal ID (TID) in a payments system, an asynchronous pipeline that updates two downstream systems, and the requirement that the two never disagree. Nothing in the type system enforces a rule like that, and no single class owns it. Rules of this kind live between consumers, services and stores.  

A developer using our agent spent a session finding out how many different ways their code had broken that one sentence. I am the product manager at Explyt; the agent is ours, it ran in regular chat mode without its debugger integration, and I went through the log afterwards. This is what the log showed and what I think it means for anyone debugging a distributed JVM system with an AI agent next to them.  

## The shape of the session  

I will not describe the symptom or the defects; the specifics would identify the developer's project. The shape is what matters here.  

One visible problem started the session. The developer and the agent fixed a defect, and the system broke in a different way. They fixed that, and it broke in yet another way. Roughly half of what got fixed had no connection to the original symptom. All of it violated the sentence in bold above, which is how the developer summed up the scope.  

That pattern is familiar to anyone who has debugged an event-driven system. The first symptom is rarely the interesting fact. The interesting fact is the invariant that all the symptoms share.  

## What the agent got right  

The agent was useful in the small, and I want to say so before the criticism.  

It proposed SQL to look at the actual rows in the two downstream systems before theorizing about them. It flagged risks in code it was about to touch, and its explanations of unfamiliar code held up. And it did not sprinkle new logging across the codebase; it read the logging the code already had.  

A developer alone would have done the same things by hand, more slowly.  

## What it got wrong, and why it matters for Java teams  

The agent never drove. It answered the evidence the developer brought, then waited for more. It never proposed a hypothesis and an experiment that would falsify it. The developer chose every next step.  

Verified statements and guesses also came out in the same tone. In a long session with many defects, remembering which claims had been checked is most of the cognitive load, and the agent left all of it to the human.  

It proposed fixes before the failure was localized. Each fix compiled and passed lint, and the agent stopped there. Whether the fix removed the symptom went unchecked, and several fixes turned out not to. Reading that in the log was uncomfortable, because I have merged a compiling patch on a tired evening for exactly the same non-reason.  

The failure I keep returning to is the last one: the agent never built a model of the entities. What a TID is, which fields each downstream system owns, what agreement between them means. Lacking that model, each defect reads as its own bug. With it, the defects collapse into repeated violations of one rule, and the search becomes systematic: find every write to TID state that bypasses the async path.  

## The agent's only sensor was the developer  

Here is the detail that explains the rest. Everything the agent knew about the running system came from the developer pasting it into the chat: query results, application and broker logs, HTTP responses, container and cluster messages, configuration. The only terminal commands the agent ran itself were git.  

An agent in that position cannot run an experiment. It can only react to the experiments a human chooses to run and chooses to show it. That is where the passivity comes from, and no prompt fixes it. The fix is instruments: a terminal it uses itself, a debugger, a way to query state itself.  

The internal review of this session proposed a browser and a terminal the agent uses itself, plus a written debugging journal in the spirit of the \[Superpowers systematic-debugging skill\](https://www.skills.sh/obra/superpowers/systematic-debugging): each hypothesis, the experiment that tests it, the result, and a running model of the entities and the rules between them. Both suggestions assume the agent can observe and act without a human relay. Without instruments, a method is only a checklist.  

## The Java lesson: write the invariant down where a test can see it  

Set the agent aside for a moment. The invariant in this story is a cross-service, cross-store rule, and those are the rules Java codebases are worst at making explicit. Here are three places where such a sentence can live so that a test, or an agent, can find it.  

The first is a domain-level assertion that runs in integration tests. The sketch below is illustrative and is not code from the session:  

\`\`\`java  

// Illustrative. TID, TMS and EMV are placeholders for "one key, two stores that must agree".  

// TmsRecord and EmvRecord stand for your read models; both expose version().  

public final class TerminalConsistency {  

public record Snapshot(String tid, Optional\<TmsRecord\> tms, Optional\<EmvRecord\> emv) {}  

public static List\<String\> violations(Snapshot s) {  

var out = new ArrayList\<String\>();  

if (s.tms().isPresent() != s.emv().isPresent()) {  

out.add(s.tid() + ": present in one store, absent in the other");  

}  

s.tms().ifPresent(t -\> s.emv().ifPresent(e -\> {  

if (!t.version().equals(e.version())) {  

out.add(s.tid() + ": version " + t.version() + " vs " + e.version());  

}  

}));  

return List.copyOf(out);  

}  

}  

\`\`\`  

An integration test that runs `violations` against every TID touched by a scenario turns the sentence from folklore into a failing assertion. When an agent fixes one defect and the assertion still fails for a different TID, the agent has found the second violation of the same invariant, and the two bugs stop looking unrelated.  

The second place is the write path itself. If "must go through the single asynchronous path" is the rule, then the compiler can help: make the direct writers package-private, expose one entry point that publishes to the topic, and let an architecture test enforce the boundary. ArchUnit is the usual tool for this on the JVM:  

\`\`\`java  

// Illustrative ArchUnit rule: only the async publisher may touch the two stores directly.  

@ArchTest  

static final ArchRule only_the_async_path_writes_terminal_state =  

noClasses()  

.that().resideOutsideOfPackages("..terminal.async..", "..tms.write..", "..emv.write..")  

.should().accessClassesThat().resideInAnyPackage("..tms.write..", "..emv.write..");  

\`\`\`  

The third place is the consumer boundary. In Spring for Apache Kafka, a listener that updates two stores has an ordering and an idempotency story whether or not anyone wrote it down. Make it explicit: an idempotent consumer with one saga step per store, an idempotency key derived from the TID and the event version, and a test that replays the same event twice and asserts a single effect. Once that test exists, a fix that quietly assumes single delivery turns into a red bar.  

None of this is new advice. The point is that each of these artifacts is also something an AI agent can read and run. An invariant that exists only in a senior engineer's head is invisible to the agent; an invariant that exists as a test is something it can run.  

## What this looks like with a debugger in the loop  

Explyt, the agent in this story, has a debugger integration that the session did not use. I want to be precise about what it would and would not have changed.  

Its documented Debug workflow applies to failures reproducible through a test, an application or an IDE run configuration: you ask the agent to confirm the cause under the JetBrains debugger, reading breakpoints, variable values and the call stack, before it changes code, then makes a minimal fix and reruns the scenario and the related tests. Builds and tests run through IDE run configurations and come back as structured results, so the agent reads test outcomes, and "it compiles" stops being the last check.  

That covers the middle of the loop: observe state, fix, verify. If the symptom in this session was reproducible under a local test, the agent could have inspected the TID state as it was being written, and repeated the scenario after every fix.  

It does not cover the edges. The documentation does not describe reading Kafka topics, querying your database or pulling cluster logs unaided; that evidence arrives via the developer or via an MCP server you attach. And nothing in it notices, on its own, that the newest defect and the first one share a cause. That is still the human's job, or the job of the invariant test above.  

## A checklist for any agent, before you accept a fix in a distributed system  

- Has the failure been reproduced in something the agent can run, or only described to it?  

- Which runtime fact did the agent observe itself: a variable, a row, a message, a stack?  

- Is the fix's success criterion the symptom going away, or the build passing?  

- After the second defect in the same area, did anyone ask what the two have in common?  

- Is the invariant that both defects violated now written down as a test?  

If the answers are "described", "none", "the build", "no" and "no", the session in this article is what you are looking at.  

## Disclosure and where to go from here  

I work at Explyt, so read this section with that in mind. Explyt is an AI agent for JetBrains IDEs. The session above ran in it, in plain chat mode, and the shortcomings I listed are ours to fix. The one part of the loop the product already closes is the debugger step described in the \[Debug mode documentation\](https://explyt.ai/docs/explyt-test/tools/debugger); a single debugger run confirms a fix for that scenario only and does not replace the related tests, and the docs say so.  

If you want to try the shape of this on your own code: pick one cross-store invariant your team knows but has never written down, turn it into an assertion like the one above, and then ask whatever agent you use to fix a failing case under a debugger, with the invariant test running after each change.  

## Related Posts  

- \[Did Your AI Agent Ever Run a Debugger? One JVM Bug, Two Agent Runs\](https://foojay.io/today/did-your-ai-agent-run-the-debugger-one-jvm-bug-two-agent-runs/)  

- \[Event-Driven Architecture in Java and Kafka\](https://foojay.io/today/event-driven-architecture-in-java-and-kafka/)  

- \[ArchUnit: Testing Your Architecture\](https://foojay.io/today/archunit-testing-your-architecture/)  

## Sources  

- Superpowers, systematic-debugging skill: https://www.skills.sh/obra/superpowers/systematic-debugging  

- Explyt documentation, Debugging with Debug mode: https://explyt.ai/docs/explyt-test/tools/debugger  

- Explyt documentation, Run configurations: https://explyt.ai/docs/explyt-test/tools/run-configurations  

- Explyt documentation, Tools and integrations (MCP): https://explyt.ai/docs/explyt-test/tools  

- The longer write-up of this session on the Explyt blog: https://explyt.ai/en/blog/one-symptom-ten-problems-kafka-invariant
