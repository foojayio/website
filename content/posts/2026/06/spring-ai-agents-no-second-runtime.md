---
title: "Why Spring Teams Don't Need a Second Runtime for AI Agents"
slug: "spring-ai-agents-no-second-runtime"
date: "2026-06-10T19:02:58+00:00"
lastmod: "2026-06-14T22:05:20+00:00"
description: "AgentFlow4J is a JVM-native runtime for governed multi-agent systems. Build, govern and operate AI agents on your existing Spring infrastructure without introducing a second platform."
authors:
  - "ahmed-sekka"
image: "https://foojay.io/wp-content/uploads/2026/06/spring-govern.png"
categories:
  - "AI"
  - "FinOps"
  - "GenAI"
  - "Java"
  - "LLM"
tags:
related_posts:
  - "tiberius-a-security-testing-framework-for-llm-applications-in-java"
  - "free-webinar-making-ai-useful-for-java-developers-in-real-applications-with-boxlang"
  - "boxlang-ai-3-2-0-image-generation-web-search-fluent-audio-agent-registry-mcp-observability"
  - "boxlang-ai-series-complete-guide-to-building-ai-agents"
enlighterjs: true
frozen: false
---

![](/images/posts/2026/06/spring-ai-agents-no-second-runtime/hero-700x350.jpg)

<br />

*A JVM-native runtime for building, governing and operating AI agents on existing Spring infrastructure.*

Every time a Spring team decides to add AI agents to a production system, the story tends to follow the same arc. A few prototypes are built, several frameworks are evaluated, and then a new reality emerges: the project is no longer just about adding agents. It is about operating a new platform.

With that new runtime come new deployment pipelines, new monitoring mechanisms, additional security reviews, and new operational responsibilities.

The question is not whether those frameworks are good. Many of them are.

The real question is this: when an organization already operates a mature Spring platform, does it actually need to introduce a second runtime?

For most Spring teams, the challenge is not infrastructure. They already have security, observability, persistence, configuration management, and operational tooling running in production.

What is missing is not a new platform. It is a runtime that connects agent execution directly to the operational capabilities Spring teams already use every day.

*** ** * ** ***

What agents actually need in production {#h2-0-what-agents-actually-need-in-production}
---------------------------------------------------------------------------------------

Forget the demos. In production, an agent system needs to answer six questions:

1. **Who is allowed to trigger this agent?** (authorization)
2. **What tools can it call?** (tool policy)
3. **How much can it spend per run?** (budget)
4. **What happens when it fails?** (retry + classification)
5. **Does a human need to approve before a high-stakes action?** (approval gate)
6. **Where does execution state go if the server restarts?** (checkpoint)

Most agent runtimes introduce their own operational model for these concerns. A Spring team already has answers to most of them through Spring Security, Micrometer, Spring Data, and Spring Boot Actuator.

What is missing is a runtime that connects those existing capabilities directly to agent execution.

*** ** * ** ***

What Spring already gives you {#h2-1-what-spring-already-gives-you}
-------------------------------------------------------------------

|  Production concern   | Existing Spring capability |
|-----------------------|----------------------------|
| Authorization         | Spring Security            |
| Metrics               | Micrometer                 |
| Persistence           | Spring Data JPA            |
| Health and operations | Spring Boot Actuator       |
| Configuration         | `application.yml`          |

**Spring Security** already knows which users have which roles. In a governed agent system, that translates directly: only a user with `ROLE_PAYMENT_AGENT` should be able to trigger an agent that touches payment data. You don't rebuild that logic. You reuse it.

**Micrometer** already instruments your application. An agent graph that emits token counts, cost estimates, and node latencies through Micrometer means your AI workflows appear in the same Grafana dashboards as the rest of your system. No new monitoring stack.

**Spring Data JPA** already manages your datasource. A `CheckpointStore` backed by that datasource means your agent's execution state survives a restart without exotic infrastructure.

**Spring Boot Actuator** already exposes health and metrics endpoints. An agent runtime that plugs into Actuator means your ops team monitors AI workflows the same way they monitor everything else.

None of this requires a second operational stack. What it requires is a runtime that can build, govern and operate agents using the capabilities Spring teams already have.

*** ** * ** ***

BUILD: Create agent teams {#h2-2-build-create-agent-teams}
----------------------------------------------------------

[AgentFlow4J](https://github.com/datallmhub/agentflow4j) is that runtime. It gives you the building blocks to create agents and compose them into multi-agent systems.

An `ExecutorAgent` is a Spring bean backed by a `ChatClient`. A `CoordinatorAgent` routes tasks to the right specialist. A `ParallelAgent` fans out to multiple agents and aggregates results. An `AgentGraph` wires them together with explicit nodes and edges.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Three specialized agents
Agent triage     = ExecutorAgent.builder()
    .chatClient(chatClient)
    .systemPrompt("Classify this support request: billing, technical, or other.")
    .build();

Agent billing    = ExecutorAgent.builder()
    .chatClient(chatClient)
    .systemPrompt("Resolve billing issues. You have access to the CRM and invoice system.")
    .build();

Agent technical  = ExecutorAgent.builder()
    .chatClient(chatClient)
    .systemPrompt("Resolve technical issues. You have access to the knowledge base.")
    .build();

// A coordinator that routes dynamically
CoordinatorAgent coordinator = CoordinatorAgent.builder()
    .executors(Map.of("billing", billing, "technical", technical))
    .routingStrategy(RoutingStrategy.llmDriven(chatClient))
    .build();

// A graph that composes them
AgentGraph graph = AgentGraph.builder()
    .addNode("triage",      triage)
    .addNode("coordinator", coordinator)
    .addEdge("triage",      "coordinator")
    .build();</pre>

Three agents, dynamic routing, typed state shared across nodes. Each agent is a standard Spring bean.

*** ** * ** ***

GOVERN: Budget, approvals, permissions, checkpoints {#h2-3-govern-budget-approvals-permissions-checkpoints}
-----------------------------------------------------------------------------------------------------------

Agents are not implicitly trusted. AgentFlow4J lets you define exactly what each agent can call, what it can spend, when a human must approve, and how execution state is preserved across restarts.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">AgentGraph graph = AgentGraph.builder()
    .addNode("analyse",  analyst)
    .addNode("process",  processor)
    .addEdge("analyse",  "process")
    // analyst can only call crm.lookup and order.fetch, not payment.refund
    .toolPolicy(ToolPolicy.allowList("crm.lookup", "order.fetch"))
    // cap total spend per run at $0.50
    .budgetPolicy(BudgetPolicy.perRun(0.50, tokenEstimator, meter))
    // pause before process, a human must approve
    .approvalGate(ApprovalGate.requireFor("process"))
    // checkpoint after every node on the existing datasource
    .checkpointStore(new JdbcCheckpointStore(dataSource))
    .build();</pre>

The `processor` agent cannot run until a human approves. The graph cannot spend more than $0.50 per run. If the server restarts mid-execution, the next run picks up from the last completed node. No Python. No YAML DSL. No new infrastructure. The datasource is the one your Spring Boot application already configures.

*** ** * ** ***

OPERATE: Observe, recover and run safely {#h2-4-operate-observe-recover-and-run-safely}
---------------------------------------------------------------------------------------

### Retry that understands cost {#h3-5-retry-that-understands-cost}

One common failure mode: a retry policy that cannot tell the difference between a transient network error and an over-budget condition will keep retrying an expensive call until the budget runs out.

AgentFlow4J's `FailureClassifier` solves this with three outcomes:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">RetryPolicy policy = RetryPolicy.exponential(3, Duration.ofSeconds(2))
    .withClassifier(FailureClassifier.defaults()
        .orElse((ex, ctx) -&gt; ex instanceof BudgetExceededException
            ? FailureClassification.OVER_BUDGET
            : FailureClassification.TRANSIENT));</pre>

`TRANSIENT` means retry. `PERMANENT` means fail fast. `OVER_BUDGET` means route to a cheaper fallback agent instead of retrying. The retry policy is no longer blind to cost.

### Operational sovereignty {#h3-6-operational-sovereignty}

There is another dimension becoming increasingly relevant, particularly in Europe: operational sovereignty.

Operational sovereignty is not only about where models run. It is also about how many platforms an organization must secure, monitor, audit, and operate. Every additional runtime introduces another operational dependency.

A JVM-native agent runtime lets organizations build governed agent systems without introducing a second execution platform. Spring AI supports Ollama for local inference, so the agent layer does not need to reach outside the perimeter. The security controls, observability stack, and deployment model already in place stay as the single source of operational truth.

*** ** * ** ***

One stack, one runtime {#h2-7-one-stack-one-runtime}
----------------------------------------------------

There is no second runtime to deploy, no second security model to audit, no second monitoring stack to maintain. AgentFlow4J integrates natively into an ecosystem millions of developers already operate, not as a Spring abstraction layer, but as a dedicated JVM-native runtime for governed agent execution.

AgentFlow4J is not a Spring plugin. It is a governed execution runtime for multi-agent systems on the JVM. Spring AI provides model access. AgentFlow4J provides the building blocks and runtime to build, govern and run multi-agent systems.

Production AI systems rarely fail because they lack orchestration.  

They fail because they lack governance, operational controls, and clear ownership.  

Spring already provides much of that foundation. AgentFlow4J brings agent execution into the same operational model.

If your organization already runs on Spring, the goal is not to introduce another platform. It is to extend the one you already trust.

If you are an architect or open-source contributor interested in what a production-grade JVM agent runtime looks like at the language level, the governance model, the checkpoint contract, the failure classification API, that conversation lives on [GitHub](https://github.com/datallmhub/agentflow4j).

*** ** * ** ***

Getting started {#h2-8-getting-started}
---------------------------------------

AgentFlow4J is available on JitPack, built on Java 17+ and Spring AI 1.0:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;repositories&gt;
    &lt;repository&gt;
        &lt;id&gt;jitpack.io&lt;/id&gt;
        &lt;url&gt;https://jitpack.io&lt;/url&gt;
    &lt;/repository&gt;
&lt;/repositories&gt;

&lt;dependency&gt;
    &lt;groupId&gt;com.github.datallmhub.agentflow4j&lt;/groupId&gt;
    &lt;artifactId&gt;agentflow4j-starter&lt;/artifactId&gt;
    &lt;version&gt;v0.7.0&lt;/version&gt;
&lt;/dependency&gt;</pre>

* **[GitHub](https://github.com/datallmhub/agentflow4j)**: source, docs, samples
* **[Cookbook](https://github.com/datallmhub/agentflow4j-cookbook)**: six self-contained Maven recipes: RAG, ticket triage, web research, Slack bot, batch processing, cost-aware routing
* **[Tutorial](https://datallmhub.github.io/agentflow4j/tutorials/stop-your-agent-burning-money/)**: end-to-end walkthrough of the four governance gates

Apache 2.0 (not an official Spring project).
