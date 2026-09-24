---
title: "BoxLang AI 3.4.0: Gateways, Human-in-the-Loop, and a Full Security Stack"
date: "2026-09-25"
description: "BoxLang AI 3.4.0 introduces gateways, durable human approvals, layered AI security, normalized reasoning, run control, and improvements across providers."
authors:
  - "luis-majano"
image: "bx-ai-3.4.0.jpg"
categories:
  - "AI"
  - "BoxLang"
  - "GenAI"
  - "Security"
  - "Developer Tools"
canonical: "https://www.ortussolutions.com/blog/boxlang-ai-340-gateways-human-in-the-loop-and-a-full-security-stack"
---

BoxLang AI 3.4.0 is a major release centered on a single theme: **trust**. AI agents operating in real applications need ways to involve people in consequential decisions and manage potentially untrusted inputs. This release introduces gateways, a restructured Human-in-the-Loop subsystem with durable decisions, layered security controls, batched approvals, normalized provider reasoning, and broader AWS Bedrock support.

The sections below follow the original [BoxLang AI 3.4.0 release announcement](https://www.ortussolutions.com/blog/boxlang-ai-340-gateways-human-in-the-loop-and-a-full-security-stack) and preserve **all its code examples**. For the complete change log, see the [BoxLang AI 3.4.0 release history](https://ai.ortusbooks.com/readme/release-history/3.4.0).

## The Gateway SPI: `IGateway`

A gateway provides a two-way interaction layer: it converts events from a particular platform into normalized input for an agent and turns agent events, including requests for approval, into something that platform can show its users. Gateways implement `IGateway` and may rely on safe defaults for capabilities they do not need.

```js
// Core gateways resolve by name
cli  = aiGateway( "cli" )
http = aiGateway( "http", { secret: "shared-hmac-secret" } )

// External gateway modules register under their own name
aiGatewayRegistry().register( new MyPlatformGateway(), "my-platform" )
myGateway = aiGateway( "my-platform" )

// Attach any gateway to HITL middleware
aiAgent(
    middleware  : new HumanInTheLoopMiddleware( gateway: aiGateway( "http" ) ),
    checkpointer: aiMemory( "cache" )
)
```

Three gateways are included:

| Gateway | Purpose |
|---|---|
| `CliGateway` | Reference CLI implementation with blocking stdin/stdout approval prompts, including `approve_always` and `approve_session` decisions. |
| `HttpGateway` | HMAC-SHA256 signed interactions with timestamp validation, nonce deduplication, pending-interaction TTLs, and atomic decision claims. |
| `MockGateway` | In-memory implementation for tests and examples. |

Other platforms can implement separate gateway modules without requiring the agent itself to depend on a particular messaging platform.

## Human-in-the-Loop with Durable Approval Grants

The Human-in-the-Loop (HITL) functionality now separates approval policy (`IApprovalPolicy`) from the interaction flow (`HumanInteractionCoordinator`). The middleware becomes an adapter that uses those components.

```js
import bxModules.bxai.models.middleware.core.HumanInTheLoopMiddleware;

// Simple: match by tool name (default policy)
hitl = new HumanInTheLoopMiddleware( toolsRequiringApproval: [ "deleteRecord" ] )

// Or supply any IApprovalPolicy, risk-based, callback-based, composite, or your own
hitl = new HumanInTheLoopMiddleware(
    policy : new RiskLevelApprovalPolicy( minLevel: "high" ),
    gateway: aiGateway( "http" )
)

agent = aiAgent( middleware: [ hitl ], checkpointer: aiMemory( "cache" ) )
```

When a human grants lasting approval, an `IDecisionStore` backed by cache, JDBC, or files can preserve the decision beyond the current run, including across restarts.

```js
store = aiDecisionStore( "jdbc", { datasource: "myDSN", table: "ai_decisions" } )
hitl  = new HumanInTheLoopMiddleware( toolsRequiringApproval: [ "placeOrder" ], decisionStore: store )
```

## Batched Approvals: One Suspension Instead of One per Call

When one agent turn requests several tools that require authorization, those pending calls now suspend in a single checkpoint. On resumption, the saved assistant message is processed without replaying the model call. A caller can approve all calls together or decide on each individually.

```js
agent = aiAgent(
    tools       : [ getWeatherTool, sendEmailTool ],
    middleware  : [ new HumanInTheLoopMiddleware( toolsRequiringApproval: [ "get_weather", "send_email" ] ) ],
    checkpointer: aiMemory( "cache" )
)

result = agent.run( "Check the weather in KC and email me the result", {}, { threadId: "t1" } )
// result.isSuspended() == true, with BOTH tool calls pending in ONE checkpoint

// One decision applies to every pending call...
final = agent.resume( "approve", "t1" )

// ...or resolve each one individually
final = agent.resume(
    [
        { decision: "approve" },
        { decision: "reject", reason: "not needed" }
    ],
    "t1"
)
```

Batched approvals are supported across OpenAI, Claude, Bedrock, and Cohere. The release also includes streaming batching for OpenAI and Claude.

## Security and Guardrails: Three Phases, Opt-In

BoxLang AI 3.4.0 adds layered controls for prompt injection and data loss. The central security setting is opt-in, and developers can also attach middleware to individual agents. The following example reproduces the settings structure from the release article.

```json
{
  "modules": {
    "bxai": {
      "settings": {
        "security": {
          "enabled": false,
          "input": {
            "enabled": true,
            "action": "flag",
            "detectors": [],
            "customPatterns": [],
            "normalizeUnicode": true,
            "stripZeroWidth": true,
            "scanToolResults": true
          },
          "fencing": {
            "enabled": true,
            "fenceContext": true,
            "escapeBindings": true,
            "preamble": ""
          }
        }
      }
    }
  }
}
```

With `security.enabled` turned on, the security middleware can be attached automatically to requests through `aiChat()`, `aiModel()`, and `aiAgent()`. An individual request may opt out with `{ secure: false }`. Unicode normalization and zero-width character hygiene operate even when the broader security feature is off.

**Phase 1 — Input sanitization:** `InputSanitizerMiddleware` checks incoming user text and optionally tool/MCP results for patterns such as instruction overrides, role impersonation, concealed Unicode, suspicious encoded strings, and exfiltration-shaped URLs. Available actions include `block`, `strip`, `flag`, and `log`.

```js
sanitizer = new bxModules.bxai.models.middleware.security.InputSanitizerMiddleware(
    action         : "strip",
    detectors      : [ "instructionOverride", "jailbreak" ],
    customPatterns : [ { name: "internalCodes", regex: "(?i)PROJ-[0-9]{4}" } ],
    scanToolResults: true
)

agent = aiAgent( name: "support-bot", middleware: [ sanitizer ] )
```

**Phase 2 — Untrusted-content fencing:** retrieved documents, web pages, or tool responses should be treated as untrusted input rather than instructions. `aiFence()` adds explicit boundaries around that content.

```js
context = aiFence( retrievedDoc, "knowledge-base" )
answer  = aiChat( "Answer using this context: #context#" )
```

Template-binding fencing and escaping provide further protection against confusing retrieved data with trusted instructions; the release documentation describes the behavior that applies even when the main security option is disabled.

**Phase 3 — Output guarding:** `OutputGuardMiddleware` can redact sensitive values and remove selected exfiltration-shaped Markdown from generated output. This local processing does not require another model call.

```js
guard = new bxModules.bxai.models.middleware.security.OutputGuardMiddleware( action: "redact" )
aiAgent( name: "support", middleware: [ guard ] )
```

For additional model-based classification, `LLMGuardMiddleware` may ask another provider, often a smaller or local model, to classify content.

```js
guard = new LLMGuardMiddleware( judge: { provider: "ollama", model: "llama-guard3" } )
aiAgent( name: "support-bot", middleware: [ guard ] )
```

A deterministic offline mock provider is also included to exercise AI middleware, HITL, and guardrails in CI without requiring live credentials.

## Normalized Reasoning Across Providers

Reasoning supported by compatible models can now be retrieved consistently as `message.reasoning`, or as `delta.reasoning` during streaming. It is distinct from the model's final content and is not stored in conversation memory.

```js
result = aiChat( "Solve this step by step: ...", params: {
    thinking: { type: "enabled", budget_tokens: 10000 }
}, options: { returnFormat: "raw" } )
reasoning = result.choices[1].message.reasoning ?: ""
answer    = result.choices[1].message.content
```

Provider support and access to reasoning data depend on the model's available features and policies.

## Agent Run Control: `cancelRun()` and `steerRun()`

Agents support cancelling or steering work already in progress, identified by `threadId`.

```js
agent.cancelRun( threadId )                        // stop at the next checkpoint
agent.steerRun( threadId, "actually, focus on X" )  // splice a message into the live turn
```

## Gateway Sessions with `aiGatewaySession()`

A gateway session connects one agent to one or more channels, routes incoming messages to the agent, and returns output through the originating gateway.

```js
session = aiGatewaySession(
    agent   : myAgent,
    gateways: [ "cli", "http" ],
    policy  : "queue"
)
session.start()
```

If another message arrives while the agent is busy, the session can `reject`, `queue` (the default), `steer`, or `interrupt` the existing turn.

## AWS Bedrock Provider Parity

This release expands Bedrock authentication to include bearer-token support and the AWS credential chain (explicit credentials, environment, ECS/EKS containers including EKS Pod Identity, and EC2 IMDSv2), with expiry-aware caching. It also adds Guardrails support, configurable `baseURL`, Cohere/Titan-v2 embeddings, and Claude-on-Bedrock tool use.

```js
// Bearer token, simplest path, no SigV4 signing required
result = aiChat( "Hello", provider: "bedrock", options: {
    providerOptions: { region: "us-east-1", bearerToken: "..." }
} )

// Or let the default credential chain resolve automatically
result = aiChat( "Hello", provider: "bedrock", options: {
    providerOptions: { region: "us-east-1" }
} )
```

## Memory: Token-Based Summarization

`SummaryMemory` can now use estimated token count instead of message count as its compression trigger.

```js
memory = aiMemory(
    memory: "summary",
    config: { maxTokens: 4000, maxMessages: 0, summaryThreshold: 10 }
)
```

`maxTokens` and `maxMessages` are alternative triggers: configure one rather than both. The `summarize()` method is now part of the `IAiMemory` interface and implemented by the built-in memory types.

## Notable Fixes

This release also addresses several provider and middleware inconsistencies:

- Tool-call middleware hooks were bypassed on Claude, Bedrock, and Cohere; all now use the same middleware pipeline as OpenAI.
- Claude extended thinking previously caused the synchronous `aiChat()` path to select the wrong response block; it now selects the first text block correctly.
- `approve_always` and `approve_session` grants did not persist for asynchronous, non-CLI gateways; this has been addressed.
- MCP tool usage could fail with Claude and Bedrock because of a missing default schema method.
- AWS credentials loaded from profile files failed because of a function-name typo.

For the complete fix list, see the [official release history](https://ai.ortusbooks.com/readme/release-history/3.4.0).

## Updated Defaults and Migration Notes

The release increases the default AI request timeout from 45 to 90 seconds and updates default models for several providers. As detailed in the release history, security is off by default except for specified Unicode hygiene; legacy `mode: "cli"` and `mode: "web"` HITL settings remain compatible, while `gateway:` is the preferred approach for new platform-specific setups. Calling `agent.resume()` with a single decision remains supported; arrays of per-call decisions are an additive feature.

For further migration details, refer to the [BoxLang AI migration guide](https://ai.ortusbooks.com/readme/release-history/3.4.0).

## Get It Now

With CommandBox:

```bash
box install bx-ai
```

Or with the BoxLang installer:

```bash
install-bx-module bx-ai
```

Additional resources: [BoxLang AI documentation](https://ai.ortusbooks.com/) and the [bx-ai source repository](https://github.com/ortus-boxlang/bx-ai).

## Want to Go Deeper? Five Technical Deep Dives

This overview includes the examples from the release announcement. For readers interested in architecture decisions, implementation details, and longer worked examples, the Ortus team has also published **five focused technical deep dives**:

1. [Part 1: Gateways — One Interface, Any Platform](https://www.ortussolutions.com/blog/boxlang-34-blog-series-part-i-gateways-one-interface-any-platform)
2. [Part 2: Revamped Human-in-the-Loop (HITL)](https://www.ortussolutions.com/blog/boxlang-34-blog-series-part-2-revamped-human-in-the-loop-hitl)
3. [Part 3: Batched Approvals](https://www.ortussolutions.com/blog/boxlang-ai-34-blog-series-part-3-batched-approvals)
4. [Part 4: Locking Down Prompt Injection](https://www.ortussolutions.com/blog/boxlang-ai-34-blog-series-part-4-locking-down-prompt-injection)
5. [Part 5: Reasoning Without the Guesswork](https://www.ortussolutions.com/blog/copy-of-boxlang-ai-34-blog-series-part-5-reasoning-without-the-guesswork)

BoxLang AI is maintained by the Ortus Solutions team as part of the broader BoxLang ecosystem.
