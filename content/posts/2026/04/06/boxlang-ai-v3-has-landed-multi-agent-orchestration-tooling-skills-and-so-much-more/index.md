---
title: "BoxLang AI v3 Has Landed - Multi-Agent Orchestration, Tooling, Skills and so much more"
date: "2026-04-06T12:03:58+00:00"
description: "It's been a while since we've shipped something this big. BoxLang AI 3.0 is a ground-up rethink of how AI agents, models, and tools work in the BoxLang…"
authors:
  - "luis-majano"
image: "boxlang-ai-v3.jpg"
categories:
  - "AI"
  - "BoxLang"
  - "GenAI"
related_posts:
frozen: false
---

It's been a while since we've shipped something this big. **BoxLang AI 3.0** is a ground-up rethink of how AI agents, models, and tools work in the BoxLang ecosystem — and it lands with ten major features at once.

The headline is the **AI Skills system** : a first-class implementation of Anthropic's [Agent Skills open standard](https://www.anthropic.com/news/agent-skills) that lets you define reusable knowledge blocks: coding styles, domain rules, tone policies, API guidelines once in a `SKILL.md` file and inject them into any number of agents and models at runtime. No more copy-pasting the same system-prompt boilerplate everywhere. Skills are versioned, composable, and come in two modes: always-on (full content in every call) and lazy (only a name + description until the LLM asks for more).

But that's just the start. Here's everything landing in 3.0:

* 🎯 **AI Skills System** — composable, file-based knowledge blocks injected into any agent or model at runtime
* 🔌 **MCP Server Seeding** — agents auto-discover and register tools from any MCP server
* 🗄️ **Global AI Tool Registry** — register tools by name once, reference them as strings anywhere
* 🔧 **Tool System Overhaul** — new `BaseTool` / `ClosureTool` architecture plus two built-in core tools
* 🛡️ **Provider Capability System** — type-safe capability detection with clear `UnsupportedCapability` errors
* 🌲 **Parent-Child Agent Hierarchy** — multi-agent orchestration trees with cycle detection and depth tracking
* 🧵 **Middleware Support** — six built-in middleware classes for logging, retries, guardrails, and more
* 🏢 **Stateless Agents + Per-Call Identity Routing** — safe multi-tenant memory across concurrent requests
* 🤗 **HuggingFace Embeddings** — new provider for the HuggingFace Inference API
* 🔀 **Custom Service URLs** — proxy and self-hosted endpoint support across all providers

Read the full changelog here: <https://ai.ortusbooks.com/readme/release-history/3.0.0>

If you've been building AI-powered apps with BoxLang, this release changes everything. If you haven't started yet, this is the release that makes it worth it.

Let's dig in. 🎉

## 🎯 The Headline: AI Skills System

The single biggest addition in 3.0 is the **AI Skills system** — a first-class implementation of [Anthropic's Agent Skills open standard](https://www.anthropic.com/news/agent-skills).

Think of a skill as a portable, reusable unit of expertise: a SQL coding style guide, a tone-of-voice policy, domain-specific rules, API cheat sheets — anything your AI should *know* before it starts answering. Define it once in a `SKILL.md` file. Inject it into any number of agents and models at runtime. No more copy-pasting the same system-prompt boilerplate across every agent you build.

```js
// Load a skill from a file
apiSkill = aiSkill( ".ai/skills/api-guidelines/SKILL.md" )

// Load every skill in a directory tree
allSkills = aiSkill( ".ai/skills/", recurse: true )

// Inline skill for short, self-contained guidance
sqlStyle = aiSkill(
    name        : "sql-style",
    description : "SQL coding standards",
    content     : "Always use snake_case. Prefer CTEs. Never use SELECT *."
)
```

Skills come in two injection modes:

* **Always-on** — full content in every LLM call. Zero latency. Best for short, universally relevant rules like tone and formatting.
* **Lazy** — only a name + one-line description goes into the system message. The LLM calls a built-in `loadSkill( name )` tool to fetch the full content on demand. Perfect for large skill libraries where most skills are irrelevant to most queries — keeps your token usage low.

You can even **promote** a lazy skill to always-on mid-session:

```js
// After the user mentions SQL work, pre-load it for the rest of the conversation
agent.activateSkill( "sql-style" )
```

And if you want certain skills available to *every* agent in your application without explicitly passing them:

```js
// In Application.bx --- every agent inherits these automatically
aiGlobalSkills().add( aiSkill( ".ai/skills/company-tone/SKILL.md" ) )
aiGlobalSkills().add( aiSkill( ".ai/skills/security-policy/SKILL.md" ) )
```

Skills live in plain Markdown files — which means your team can review them in pull requests, diff them, and keep them in sync with the rest of your codebase. This is the end of prompt drift.

## 📚 Brand New Docs

The entire documentation has been re-organized so you can go from zero to hero. Tons of new sections and more direct docs for your reading pleasure: <https://ai.ortusbooks.com/>

## 🔌 MCP Server Seeding

Agents can now be pointed directly at one or more **MCP servers** . All tools exposed by those servers are discovered automatically via `listTools()` and registered as `MCPTool` instances — no manual tool construction required.

```js
agent = aiAgent(
    name       : "data-analyst",
    mcpServers : [
        { url: "http://localhost:3001", token: "secret" },
        "http://internal-tools-server:3002"
    ]
)
```

Or fluently:

```js
agent = aiAgent( "analyst" )
    .withMCPServer( "http://localhost:3001", { token: "secret" } )
    .withMCPServer( mcpClientInstance )
```

The agent's system prompt is automatically updated so the LLM knows which tools came from which server. MCP servers are also surfaced in `getConfig()` output for full observability.

## 🗄️ Global AI Tool Registry

New in 3.0: a module-scoped **Global Tool Registry** accessible via the `aiToolRegistry()` BIF. Register tools by name once — in `Application.bx` or `ModuleConfig.bx` --- and reference them as plain strings anywhere in your codebase.

```js
// Register once
aiToolRegistry().register( "searchProducts", productSearchTool )
aiToolRegistry().register( "getWeather@myapp", weatherTool )

// Reference by name anywhere --- no live object references needed
result = aiChat(
    "Find wireless headphones under $50",
    { tools: [ "searchProducts", "getWeather@myapp" ] }
)
```

Module namespacing (e.g. `now@bxai`) keeps registrations collision-free across modules. Two new interception points --- `onAIToolRegistryRegister` and `onAIToolRegistryUnregister` --- give you hooks for auditing and lifecycle management.

## 🔧 Tool System Overhaul

The tool system has been significantly redesigned around a new `BaseTool` abstract base class. All tool implementations extend it, getting the shared invocation lifecycle, result serialization, and fluent `describeArg()` annotation syntax for free.

The old `Tool.bx` is replaced by **`ClosureTool`** — a `BaseTool` subclass backed by any closure or lambda that auto-introspects the callable's parameter metadata to generate an OpenAI-compatible function schema.

```js
searchTool = aiTool(
    "searchKB",
    "Search the knowledge base",
    function( required string query, numeric maxResults = 5 ) {
        return knowledgeBase.search( query, maxResults )
    }
)
```

Two **built-in core tools** ship with the module:

* `now@bxai` --- **auto-registered on module load**, returns the current date/time in ISO 8601. Every agent gets temporal awareness for free, with no configuration.
* `httpGet` --- opt-in only (not auto-registered for security), fetches any URL via HTTP GET.

`now@bxai` being auto-registered is worth calling out. No major AI framework ships built-in tools out of the box. This is a genuine differentiator — your agents just *know what time it is* without any wiring on your part.

## 🛡️ Provider Capability System

A new type-safe capability system prevents calling unsupported operations on providers and gives you clear, actionable errors instead of cryptic runtime crashes.

```js
service = aiService( "voyage" )
println( service.getCapabilities() )          // [ "embeddings" ]
println( service.hasCapability( "chat" ) )    // false
```

`aiChat()`, `aiChatStream()`, and `aiEmbed()` now check provider capabilities before calling and throw a clean `UnsupportedCapability` exception if the requirement isn't met. No more debugging mysterious provider errors.

## 🌲 Parent-Child Agent Hierarchy

Multi-agent orchestration is now a first-class concept. `AiAgent` tracks its position in an agent tree with full introspection, cycle detection, and depth tracking.

```js
coordinator = aiAgent( name: "coordinator" )
    .addSubAgent( aiAgent( name: "researcher" ) )
    .addSubAgent( aiAgent( name: "writer" ) )

println( coordinator.isRootAgent() )         // true
println( researcherAgent.getAgentDepth() )   // 1
println( writerAgent.getAgentPath() )        // /coordinator/writer
println( researcherAgent.getAncestors() )    // [ coordinator ]
```

`addSubAgent()` automatically wires the parent relationship. `getConfig()` exposes `parentAgent`, `agentDepth`, and `agentPath` for full observability.

## 🧵 Middleware Support

Both `AiModel` and `AiAgent` now support composable **middleware** for cross-cutting concerns — logging, retries, guardrails, human-in-the-loop approvals, and more. Agent middleware is prepended ahead of model middleware in the execution chain.

3.0 ships **six middleware classes** out of the box:

|         Middleware         |                          What It Does                          |
|----------------------------|----------------------------------------------------------------|
| `LoggingMiddleware`        | Audit every LLM call and tool invocation                       |
| `RetryMiddleware`          | Exponential back-off for rate limits and transient errors      |
| `GuardrailMiddleware`      | Block dangerous tools and validate arguments with regex        |
| `MaxToolCallsMiddleware`   | Cap tool invocations per run to prevent runaway agents         |
| `HumanInTheLoopMiddleware` | Require explicit human approval before sensitive tools execute |
| `FlightRecorderMiddleware` | Record real runs to JSON fixtures, replay offline in CI        |

The `FlightRecorderMiddleware` deserves a special mention — it's a testing superpower. Record a live agent run once, commit the fixture, and replay it deterministically in CI with zero live provider calls.

```js
// Record a real run
agent = aiAgent( "weather-bot", middleware: new FlightRecorderMiddleware( mode: "record" ) )
agent.run( "What is the weather in London?" )
// → Writes: .ai/flight-recorder/weather-bot-<timestamp>.json

// Replay in CI --- no live calls, fully deterministic
agent = aiAgent(
    "weather-bot",
    middleware: new FlightRecorderMiddleware(
        mode        : "replay",
        fixturePath : "tests/fixtures/weather-bot.json"
    )
)
```

## 🏢 Stateless Agents + Per-Call Identity Routing

`AiAgent` is now **fully stateless** . `userId` and `conversationId` are resolved per-call from the `options` argument, eliminating shared-state concurrency bugs in multi-user deployments.

Every memory type (`IAiMemory`, `IVectorMemory`) now accepts optional `userId` and `conversationId` on `add()`, `getAll()`, `clear()`, and related methods — so a single memory instance can safely serve multiple tenants:

```js
sharedMemory = aiMemory( "cache" )

sharedMemory.add( message, userId: "alice", conversationId: "conv-1" )
sharedMemory.add( message, userId: "bob",   conversationId: "conv-2" )
sharedMemory.getAll( userId: "alice", conversationId: "conv-1" )
```

## What Else Is New

* 🤗 **HuggingFace Embeddings** — new `huggingface` provider for the HuggingFace Inference API
* 🔀 **Custom Service URLs** — all senders now accept a `baseUrl` override for proxies, self-hosted endpoints, and OpenAI-compatible APIs
* 🏗️ **`BaseService` → `OpenAIService` split** --- `BaseService` is now truly provider-agnostic, making custom provider implementations much cleaner
* 🐛 **Streaming event fixes** --- `beforeAIModelInvoke`/`afterAIModelInvoke` events were not firing for streaming; fixed
* 🐛 **MCP `requestId` null crash** — fixed a crash on JSON-RPC notifications that intentionally omit `id`

## No Breaking Changes

3.0 is a major release but your existing code keeps working. `aiChat()`, `aiEmbed()`, and `aiAgent()` BIF signatures are unchanged. Upgrade, run your tests, and start exploring the new APIs.

## Get Started

```bash
# Install or upgrade your OS installation via BoxLang
install-bx-module bx-ai

# Install or upgrade via CommandBox
install bx-ai
```

📖 [Full Documentation](https://boxlang.ortusbooks.com)  

🛠️ [Changelog](https://boxlang.ortusbooks.com/changelog)  

🐛 [Report Issues](https://github.com/ortus-solutions/bx-ai/issues)  

💬 [Community Slack](https://boxteam.ortussolutions.com)
