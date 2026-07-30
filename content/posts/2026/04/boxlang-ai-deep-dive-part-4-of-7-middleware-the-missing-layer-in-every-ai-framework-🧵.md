---
title: "BoxLang AI Deep Dive — Part 4 of 7: Middleware — The Missing Layer in Every AI Framework 🧵"
slug: "boxlang-ai-deep-dive-part-4-of-7-middleware-the-missing-layer-in-every-ai-framework-🧵"
date: "2026-04-23T14:25:53+00:00"
lastmod: "2026-05-14T09:54:42+00:00"
description: "BoxLang AI 3.0 Series · Part 4 of 7 Here's the question every team eventually asks about their AI agents: how do we test these things? Agents make live - by Cristobal Escobar"
authors:
  - "cristobal-escobar"
image: "https://foojay.io/wp-content/uploads/2026/04/bxai-series-cover-04S.png"
categories:
  - "AI"
  - "BoxLang"
  - "GenAI"
  - "Tools"
tags:
related_posts:
enlighterjs: true
frozen: false
---

![](/images/posts/2026/04/boxlang-ai-deep-dive-part-4-of-7-middleware-the-missing-layer-in-every-ai-framework-🧵/bxai-series-cover-04-700x368.png)

*BoxLang AI 3.0 Series · Part 4 of 7*

Here's the question every team eventually asks about their AI agents: how do we test these things?

Agents make live LLM calls. They invoke real tools. They have non-deterministic outputs. Standard unit testing approaches fall apart. You can't mock every provider. You can't replay a conversation from three weeks ago. You can't confidently tell stakeholders that the agent you deployed today behaves the same way it did when you signed off on it.

And before testing, there's production: how do you add logging without touching provider code? How do you retry transient failures without wrapping every call? How do you block dangerous tool invocations without forking the agent logic?

**BoxLang AI 3.0 solves all of this with middleware**. Six battle-tested middleware classes ship out of the box, covering the most common cross-cutting concerns. And if none of them fit your use case exactly, writing your own is a matter of extending one class or defining a struct of closures.

🏗️ The Middleware Architecture {#h2-0-the-middleware-architecture}
-------------------------------------------------------------------

Middleware sits between the agent's `run()` call and the actual LLM invocations and tool calls. Both `AiModel` and `AiAgent` support it. When an agent runs, its middleware is prepended to the model's middleware --- agent hooks fire first, then model hooks.

There are two hook styles:

**Sequential hooks** --- fire in registration order (or reverse for `after*` hooks). Return `AiMiddlewareResult` to control flow.

|            Hook             |            Fires            | Direction |
|-----------------------------|-----------------------------|-----------|
| `beforeAgentRun( context )` | Before agent starts         | Forward   |
| `afterAgentRun( context )`  | After agent completes       | Reverse   |
| `beforeLLMCall( context ) ` | Before each LLM call        | Forward   |
| `afterLLMCall( context ) `  | After each LLM call         | Reverse   |
| `beforeToolCall( context )` | Before each tool invocation | Forward   |
| `afterToolCall( context )`  | After each tool returns     | Reverse   |
| `onError( context )`        | When any hook throws        | ---       |

**Wrap hooks** --- nested closures. Call `handler()` to proceed, intercept the result.

|                Hook                |             Purpose             |
|------------------------------------|---------------------------------|
| ` wrapLLMCall( context, handler )` | Surround each LLM provider call |
| `wrapToolCall( context, handler )` | Surround each tool invocation   |

🎯 `AiMiddlewareResult` --- Typed Flow Control {#h2-1-aimiddlewareresult-typed-flow-control}
--------------------------------------------------------------------------------------------

Every sequential hook must return an `AiMiddlewareResult`. The static factory methods make this expressive:

<pre class="EnlighterJSRAW" data-enlighter-language="java">import bxModules.bxai.models.middleware.AiMiddlewareResult;

// Continue normally — chain proceeds
return AiMiddlewareResult.continue()

// Stop everything immediately
return AiMiddlewareResult.cancel( "Rate limit exceeded for this tenant." )

// Human approved — used by HITL middleware
return AiMiddlewareResult.approve()

// Human rejected — terminal, stops the chain
return AiMiddlewareResult.reject( "Operator rejected: amounts over $1000 require VP approval." )

// Human edited the tool args — patched args flow to the tool
return AiMiddlewareResult.edit( { correctedArgs: { amount: 100 } } )

// Suspend for async human review — terminal
return AiMiddlewareResult.suspend( { toolName: "transferFunds", args: toolArgs } )
</pre>

Terminal results (`cancel`, `reject`, `suspend`) stop the chain immediately. Non-terminal results continue to the next middleware.

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Predicates for checking results
result.isContinue()   // chain continues
result.isCancelled()  // was stopped
result.isApproved()   // human approved
result.isRejected()   // human rejected (terminal)
result.isEdit()       // args were modified
result.isSuspended()  // waiting for async input (terminal)
result.isTerminal()   // cancelled OR rejected OR suspended
</pre>

📝 LoggingMiddleware --- Instant Observability {#h2-2-loggingmiddleware-instant-observability}
----------------------------------------------------------------------------------------------

Drop this in and every LLM call, tool invocation, agent run start/end, and error gets logged to BoxLang's `ai` log file and optionally to the console --- with zero code changes to your agents:

<pre class="EnlighterJSRAW" data-enlighter-language="java">agent = aiAgent(
    name       : "support-bot",
    middleware : new LoggingMiddleware(
        logToConsole : true,
        logLevel     : "info",
        prefix       : "[SupportBot]"
    )
)
</pre>

The implementation is a clean example of how sequential hooks compose:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// From LoggingMiddleware.bx
AiMiddlewareResult function beforeAgentRun( required struct context ) {
    emit( "Agent run starting | input: #left( toString( context.input ), 120 )#" )
    return AiMiddlewareResult.continue()
}

AiMiddlewareResult function afterToolCall( required struct context ) {
    var toolName = context.tool?.getName() ?: "unknown"
    emit( "Tool call complete | tool: #toolName# | result: #left( toString( context.result ), 120 )#" )
    return AiMiddlewareResult.continue()
}

AiMiddlewareResult function onError( required struct context ) {
    emit( "Error in phase '#context.phase#': #context.error?.message#", "error" )
    return AiMiddlewareResult.continue()  // don't stop the chain on logging errors
}
</pre>

Options:

|     Option     |       Default       |             Description             |
|----------------|---------------------|-------------------------------------|
| `logToFile `   | `true`              | Write to BoxLang `ai` log           |
| `logToConsole` | `false`             | Also print to stdout                |
| `logLevel`     | `"info"`            | `info`, `debug`, `warning`, `error` |
| `prefix`       | `"[AI Middleware]"` | Prepended to every message          |

🔁 `RetryMiddleware` --- Resilience Without Boilerplate {#h2-3-retrymiddleware-resilience-without-boilerplate}
--------------------------------------------------------------------------------------------------------------

LLM providers have rate limits. Networks have transient failures. `RetryMiddleware` wraps both LLM calls and tool calls with exponential backoff --- transparently, without any code in your tools or agents:

<pre class="EnlighterJSRAW" data-enlighter-language="java">agent = aiAgent(
    name       : "analyst",
    middleware : new RetryMiddleware(
        maxRetries        : 5,
        initialDelay      : 2000,
        backoffMultiplier : 1.5,
        maxDelay          : 30000
    )
)
</pre>

It uses `wrapLLMCall` and `wrapToolCall` hooks --- the outer wrap catches exceptions, sleeps, and retries up to `maxRetries` times. Non-retryable exceptions (like `InvalidInput` or `MaxInteractionsExceeded`) surface immediately:

|       Option        |                 Default                  |          Description           |
|---------------------|------------------------------------------|--------------------------------|
| `maxRetries`        | `3`                                      | Attempts after first failure   |
| `initialDelay`      | `1000`                                   | First retry delay in ms        |
| `backoffMultiplier` | `2 `                                     | Multiplier applied per failure |
| `maxDelay`          | `30000`                                  | Hard cap on delay              |
| `nonRetryableTypes` | `"InvalidInput,MaxInteractionsExceeded"` | Exception types to skip        |

🛡️ `GuardrailMiddleware` --- Defense in Depth {#h2-4-guardrailmiddleware-defense-in-depth}
-------------------------------------------------------------------------------------------

Block dangerous tools entirely, or reject tool calls whose arguments match regex patterns --- before they ever reach the tool:

<pre class="EnlighterJSRAW" data-enlighter-language="java">guardrail = new GuardrailMiddleware(
    blockedTools : [ "deleteRecord", "dropTable", "truncateAll" ],
    argPatterns  : {
        runSql  : [ { query: "(?i)drop|truncate|delete" } ],
        sendMail: [ { to: "@competitor\\.com$" } ]
    }
)

agent = aiAgent( name: "db-assistant", middleware: guardrail )
</pre>

The hook fires in `beforeToolCall` --- it checks the tool name against `blockedTools` first, then validates each argument against the configured regex patterns:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// From GuardrailMiddleware.bx
AiMiddlewareResult function beforeToolCall( required struct context ) {
    var toolName = context.toolName ?: (context.tool?.getName() ?: "")

    // 1. Blocked tool list check
    if ( variables.blockedTools.findNoCase( toolName ) &gt; 0 ) {
        return AiMiddlewareResult.reject(
            "GuardrailMiddleware: tool '#toolName#' is in the blocked tools list."
        )
    }

    // 2. Argument pattern checks
    if ( variables.argPatterns.keyExists( toolName ) ) {
        // ... check each rule against the resolved tool arguments
    }

    return AiMiddlewareResult.continue()
}
</pre>

|     Option     | Default |                  Description                  |
|----------------|---------|-----------------------------------------------|
| `blockedTools` | `[]`    | Tool names always rejected (case-insensitive) |
| `argPatterns`  | `{}`    | `{ toolName: [{ paramName: "regex" }] }`      |

🙋` HumanInTheLoopMiddleware` --- Keeping Humans in Control {#h2-5-humanintheloopmiddleware-keeping-humans-in-control}
----------------------------------------------------------------------------------------------------------------------

This middleware intercepts specific tool calls and requires a human to approve, reject, or edit before execution proceeds. Two modes, two very different use cases.

**CLI mode** --- blocks on stdin. Perfect for local scripts, automation tools, and development workflows:

<pre class="EnlighterJSRAW" data-enlighter-language="java">agent = aiAgent(
    name       : "finance-bot",
    middleware : new HumanInTheLoopMiddleware(
        toolsRequiringApproval : [ "transferFunds", "placeOrder" ],
        showArguments          : true
    )
)
</pre>

When the LLM calls `transferFunds`, the terminal shows:

<pre class="EnlighterJSRAW" data-enlighter-language="html">╔══════════════════════════════════════════════════╗
║         HUMAN APPROVAL REQUIRED                  ║
╚══════════════════════════════════════════════════╝
 Tool: transferFunds
 Args: {"amount": 5000, "account": "12345"}

 [A]pprove  [R]eject  [Q]uit
 Decision:</pre>

**Web mode** --- suspends the run and returns an `AiMiddlewareResult.suspend()`. The calling code checkpoints state and presents the approval request asynchronously --- via email, Slack, a web UI, whatever fits your workflow:

<pre class="EnlighterJSRAW" data-enlighter-language="java">agent = aiAgent(
    name        : "finance-bot",
    middleware  : new HumanInTheLoopMiddleware(
        mode                  : "web",
        toolsRequiringApproval: [ "placeOrder" ]
    ),
    checkpointer: aiMemory( "cache" )
)

// First request — the LLM wants to place an order
result = agent.run( "Order 50 units of product SKU-789", {}, { userId: "alice" } )

if ( result.isSuspended() ) {
    // Send approval request to alice's manager via Slack, email, etc.
    notifyManager( result.getData() )
    // Store threadId for resume
    session.pendingApproval = result.getData().threadId
}

// After the manager approves (in a separate request/thread)
agent.resume( "approve", session.pendingApproval )

// Or if they edit the quantity
agent.resume( "edit", session.pendingApproval, { correctedArgs: { quantity: 10 } } )
</pre>

The resume path in `HumanInTheLoopMiddleware` reads the `_resumeContext` injected by `AiAgent.resume()`, honours the decision, and either continues, rejects, or patches the tool arguments --- then clears the context so subsequent tool calls in the same run go through normal HITL flow again.

|          Option          | Default |       Description        |
|--------------------------|---------|--------------------------|
| `toolsRequiringApproval` | `[]`    | Tools needing sign-off   |
| `mode`                   | `"cli"` | `"cli"` or `"web" `      |
| `showArguments`          | `true ` | Show args in CLI prompt  |
| `approvalCallback`       | ---     | Custom approval function |

🎙️ `FlightRecorderMiddleware` --- AI Testing Solved {#h2-6-flightrecordermiddleware-ai-testing-solved}
-------------------------------------------------------------------------------------------------------

This is the one that changes how you think about testing AI agents.

The problem: agent behaviour is non-deterministic. The LLM might phrase something differently each run. The tool call order might vary. Writing assertions against agent output directly is fragile. And running tests against live providers is slow, expensive, and requires network access in CI.

`FlightRecorderMiddleware` solves this with a record/replay approach. Record a real run once --- capturing every LLM round-trip and tool invocation to a JSON fixture file. Then replay that fixture in CI without any live calls.

**Three modes:**

<pre class="EnlighterJSRAW" data-enlighter-language="java">// RECORD — calls real providers and tools, saves every interaction
agent = aiAgent(
    name       : "weather-bot",
    middleware : new FlightRecorderMiddleware( mode: "record" )
)
agent.run( "What's the weather in London and should I bring an umbrella?" )
// → Writes: .ai/flight-recorder/weather-bot-20260402-143022.json
</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="java">// REPLAY — zero live calls, fully deterministic
agent = aiAgent(
    name       : "weather-bot",
    middleware : new FlightRecorderMiddleware(
        mode        : "replay",
        fixturePath : "tests/fixtures/weather-bot.json"
    )
)
agent.run( "What's the weather in London and should I bring an umbrella?" )
// → Returns the exact same response as the recorded run
</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="java">// PASSTHROUGH (default) — no recording, calls pass through normally
agent = aiAgent(
    name       : "weather-bot",
    middleware : new FlightRecorderMiddleware()  // mode: "passthrough"
)
</pre>

**The fixture format** --- human-readable JSON that you can inspect, edit, and commit to version control:

<pre class="EnlighterJSRAW" data-enlighter-language="java">{
    "version": "1",
    "recordedAt": "2026-04-02T14:30: 22",
    "agentName": "weather-bot",
    "interactions": [
        {
            "seq": 1,
            "type": "llm",
            "request": { "model": "gpt-4o", "messages": [...], "tools": [...] },
            "response": { "choices": [{ "message": { "tool_calls": [...] } }] }
        },
        {
            "seq": 2,
            "type": "tool",
            "toolName": "getWeather",
            "arguments": { "city": "London" },
            "result": "15°C, overcast, 80% chance of rain"
        },
        {
            "seq": 3,
            "type": "llm",
            "request": { ... },
            "response": { "choices": [{ "message": { "content": "Yes, bring an umbrella..." } }] }
        }
    ]
}
</pre>

One implementation detail worth noting: the recorder **flushes to disk after every interaction**, not just at the end. This means if your agent crashes mid-run, the partial recording is preserved and can be inspected:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// From FlightRecorderMiddleware.bx
private void function _appendInteraction( required struct interaction ) {
    var seq = variables._tape.interactions.len() + 1
    arguments.interaction.seq = seq
    variables._tape.interactions.append( arguments.interaction )
    _saveSnapshot()  // flush after every interaction — crash-safe
}
</pre>

**Strict vs lenient replay:**

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Strict (default): throw on type mismatch — "expecting llm but tape has tool"
new FlightRecorderMiddleware( mode: "replay", strict: true )

// Lenient: skip forward to find next matching interaction type
new FlightRecorderMiddleware( mode: "replay", strict: false )
</pre>

|    Option     |         Default         |              Description               |
|---------------|-------------------------|----------------------------------------|
| `mode`        | `"passthrough"`         | `"passthrough", "record", or "replay"` |
| `fixturePath` | `""`                    | Path to fixture file                   |
| `fixtureDir`  | `".ai/flight-recorder"` | Auto-generated fixture directory       |
| `recordTools` | `true`                  | Whether to capture tool interactions   |
| `strict`      | `true`                  | Throw on type mismatch in replay       |

🔢 `MaxToolCallsMiddleware` --- Runaway Agent Prevention {#h2-7-maxtoolcallsmiddleware-runaway-agent-prevention}
----------------------------------------------------------------------------------------------------------------

Simple but essential in production --- caps the total number of tool invocations per agent run:

<pre class="EnlighterJSRAW" data-enlighter-language="java">agent = aiAgent(
    name       : "research-bot",
    middleware : new MaxToolCallsMiddleware( maxCalls: 10 )
)
</pre>

The counter resets at the start of each new `run()` call. If the cap is hit mid-run, the chain is cancelled with a clear error message. Essential for preventing infinite tool call loops in complex multi-step reasoning tasks.

✍️ Writing Your Own Middleware {#h2-8-writing-your-own-middleware}
------------------------------------------------------------------

Two approaches, depending on how much structure you want.

**Struct of closures** --- lightweight, no class needed:

<pre class="EnlighterJSRAW" data-enlighter-language="java">agent.withMiddleware( {
    beforeToolCall: ( ctx ) =&gt; {
        if ( ctx.tool?.getName() == "dangerousTool" ) {
            return AiMiddlewareResult.cancel( "This tool is not allowed." )
        }
        return AiMiddlewareResult.continue()
    },

    wrapLLMCall: ( ctx, handler ) =&gt; {
        var start  = getTickCount()
        var result = handler()
        metricsService.record( "llm.latency", getTickCount() - start )
        return result
    },

    onError: ( ctx ) =&gt; {
        alertService.notify( "Agent error in #ctx.phase#: #ctx.error.message#" )
        return AiMiddlewareResult.continue()
    }
} )
</pre>

Structs are automatically wrapped in `StructMiddlewareAdapter` --- you only define the hooks you need.

**Class-based** --- reusable, configurable, independently testable:

<pre class="EnlighterJSRAW" data-enlighter-language="java">import bxModules.bxai.models.middleware.BaseAiMiddleware;
import bxModules.bxai.models.middleware.AiMiddlewareResult;

class extends="BaseAiMiddleware" {

    property name="tenantId" type="string";

    function init( required string tenantId ) {
        variables.tenantId = arguments.tenantId
        variables.name = "Tenant Audit Middleware"
        variables.description = "Logs all AI tool calls to the tenant audit trail"
        return this
    }

    AiMiddlewareResult function beforeToolCall( required struct context ) {
        auditLog.record(
            tenantId : variables.tenantId,
            tool     : context.tool?.getName() ?: "unknown",
            args     : context.toolCall?.function?.arguments ?: "{}"
        )
        return AiMiddlewareResult.continue()
    }

}
</pre>

🚀 Composing Middleware {#h2-9-composing-middleware}
----------------------------------------------------

Middleware stacks compose cleanly --- just pass an array:

<pre class="EnlighterJSRAW" data-enlighter-language="java">agent = aiAgent(
    name       : "production-agent",
    middleware : [
        new LoggingMiddleware( logToConsole: false ),
        new RetryMiddleware( maxRetries: 3 ),
        new GuardrailMiddleware( blockedTools: [ "deleteRecord" ] ),
        new MaxToolCallsMiddleware( maxCalls: 15 ),
        new HumanInTheLoopMiddleware( toolsRequiringApproval: [ "placeOrder" ] )
    ]
)
</pre>

Or fluently, one at a time:

<pre class="EnlighterJSRAW" data-enlighter-language="java">agent
    .withMiddleware( new LoggingMiddleware() )
    .withMiddleware( new RetryMiddleware( maxRetries: 3 ) )
    .withMiddleware( new GuardrailMiddleware( blockedTools: [ "deleteRecord" ] ) )
</pre>

In production, logging + retry + guardrails is the baseline stack. Add `MaxToolCallsMiddleware` for complex reasoning agents. Add `HumanInTheLoopMiddleware` for any agent touching money, data, or external systems. Use `FlightRecorderMiddleware` in record mode during QA and replay mode in CI.

What's Next {#h2-10-what-s-next}
--------------------------------

In **Part 5** , we close the series with a deep dive into BoxLang AI's provider architecture --- how the capability system works, how `BaseService` and `OpenAIService` are structured, how to add custom providers, and a tour of the full 17-provider ecosystem.

📖 [Full Documentation](https://boxlang.ortusbooks.com/ "Full Documentation") 📦Install Today: `install-bx-module bx-ai` 🫶[Professional Support](https://ai.ortussolutions.com/ "Professional Support")

[← Previous](https://foojay.io/today/boxlang-ai-deep-dive-part-3-of-7-multi-agent-orchestration-building-ai-teams-that-work/ "← Previous")

[Next -\>](https://foojay.io/today/boxlang-ai-deep-dive-part-5-of-7-one-api-17-providers-the-provider-architecture-deep-dive-%f0%9f%9b%a1%ef%b8%8f/)
