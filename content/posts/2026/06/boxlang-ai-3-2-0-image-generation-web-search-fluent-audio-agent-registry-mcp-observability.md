---
title: "BoxLang AI 3.2.0 — Image Generation, Web Search, Fluent Audio, Agent Registry & MCP Observability"
slug: "boxlang-ai-3-2-0-image-generation-web-search-fluent-audio-agent-registry-mcp-observability"
date: "2026-06-02T12:27:07+00:00"
lastmod: "2026-06-02T12:33:49+00:00"
description: "BoxLang AI 3.2.0 is here, and it's a landmark release. We're shipping five major features: image generation, web search, a fluent audio builder API, a - by Cristobal Escobar"
authors:
  - "cristobal-escobar"
image: "https://foojay.io/wp-content/uploads/2026/06/bx-jwt-2.png"
categories:
  - "AI"
  - "BoxLang"
  - "Developer Tools"
  - "GenAI"
  - "Java"
  - "LLM"
  - "Observability"
  - "Tools"
tags:
related_posts:
enlighterjs: true
frozen: false
---

![](/images/posts/2026/06/boxlang-ai-3-2-0-image-generation-web-search-fluent-audio-agent-registry-mcp-observability/BoxLangAI-3.2-700x394.jpg)

BoxLang AI 3.2.0 is here, and it's a landmark release. We're shipping five major features: image generation, web search, a fluent audio builder API, a centralized agent registry, and deep MCP observability along with a suite of analytics improvements and a critical bug fix. Let's dig in. 🎉

🖼️ Image Generation --- aiImage()  

You can now generate images directly from BoxLang using any provider that supports text-to-image generation. The aiImage() BIF follows the same fluent, chainable philosophy as the rest of bx-ai then act on the result with expressive method calls.

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Generate and save in one fluent chain
aiImage( "A futuristic cityscape at sunset" )
    .saveToFile( "/images/cityscape.png" )

// Full control with params and provider
response = aiImage(
    "A watercolor painting of a mountain lake",
    { n: 2, size: "1024x1024", quality: "hd" },
    { provider: "openai" }
)

// Embed directly in HTML output
dataURI = response.toDataURI()
</pre>

The returned AiImageResponse object gives you everything you need: hasImages(), getCount(), getFirstURL(), getFirstBase64(), saveToFile(), saveAllToDirectory(), toDataURI(), getMimeType(), and toStruct().

Supported providers out of the box:

|  Provider  |                Model                 |      Env Var       |
|------------|--------------------------------------|--------------------|
| OpenAI     | gpt-image-1 (default), DALL-E models | OPENAI_API_KEY     |
| Gemini     | imagen-3.0-generate-008              | GEMINI_API_KEY     |
| Grok / xAI | grok-2-image                         | GROK_API_KEY       |
| OpenRouter | FLUX Schnell (default), many others  | OPENROUTER_API_KEY |

A generateImage@bxai agent tool is auto-registered in the global tool registry at module startup, so your agents can generate images without any manual wiring:

<pre class="EnlighterJSRAW" data-enlighter-language="java">agent = aiAgent( tools: [ "generateImage@bxai" ] )
</pre>

📚 **[Image Generation Docs](https://ai.ortusbooks.com/main-components/image-generation "Image Generation Docs")**

🔍 Web Search --- aiWebSearch() \& aiWebSearchAsync()  

BoxLang AI now ships a unified web search system with provider abstraction and normalized results. Every provider returns the same fields --- title, url, snippet, publishedDate, domain, score, thumbnail, language --- so you can swap providers without touching your code.

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Synchronous search
results = aiWebSearch( "latest BoxLang AI updates", { provider: "brave", maxResults: 8 } )

// Async — returns a BoxFuture
future = aiWebSearchAsync( "BoxLang release highlights", { provider: "tavily" } )
results = future.get()
</pre>

Supported providers:

| Provider |                      Notes                      |
|----------|-------------------------------------------------|
| http     | URL fetching \& parsing --- no API key required |
| brave    | Privacy-focused; country/language filters       |
| google   | Google Custom Search                            |
| tavily   | Retrieval-focused, great for AI agents          |
| exa      | Semantic and neural search modes                |

The webSearch@bxai tool is auto-registered globally, so any agent can search the web immediately:

<pre class="EnlighterJSRAW" data-enlighter-language="java">agent = aiAgent(
    name: "ResearchAgent",
    tools: [ "webSearch@bxai" ]
)

response = agent.run( "Find and summarize recent BoxLang AI release highlights" )
</pre>

📚 **[Web Search Docs](https://ai.ortusbooks.com/main-components/web-search "Web Search Docs")**

🎤 Fluent Builder API for Audio BIFs  

aiSpeak(), aiTranscribe(), and aiTranslate() now support a full fluent builder API. Call any of them with no arguments to get the request object back, then chain your configuration before executing. The traditional positional-argument syntax continues to work exactly as before --- the fluent builder is purely additive.

aiSpeak()

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Traditional syntax — still works
audio = aiSpeak( "Hello!", { voice: "nova" }, { provider: "openai" } )

// Fluent builder — expressive and self-documenting
audio = aiSpeak()
    .of( "Hello, world!" )
    .voice( "nova" )
    .provider( "openai" )
    .asMP3()
    .speak()

// Gender shortcuts
audio = aiSpeak()
    .of( "Welcome aboard!" )
    .male()
    .speed( 1.2 )
    .speak()

// Format shortcuts
audio = aiSpeak()
    .of( "System alert." )
    .asWav()
    .outputFile( "/audio/alert.wav" )
    .speak()
</pre>

Key builder methods: .of(), .voice(), .male() / .female(), .speed(), .instructions(), .outputFile(), .asMP3() / .asWav() / .asFlac() / .asOpus() / .asPCM(), .provider(), .speak().

aiTranscribe()

<pre class="EnlighterJSRAW" data-enlighter-language="java">// From file
text = aiTranscribe()
    .file( "/audio/meeting.mp3" )
    .withWordTimestamps()
    .asVerboseJSON()
    .transcribe()

// From URL
text = aiTranscribe()
    .url( "https://example.com/audio.mp3" )
    .language( "es" )
    .transcribe()

// Translate audio directly to English
english = aiTranscribe()
    .file( "/audio/french.mp3" )
    .translate()
</pre>

Key builder methods: .file(), .url(), .data(), .language(), .withWordTimestamps(), .withSegmentTimestamps(), .diarize(), .asJSON() / .asText() / .asVerboseJSON() / .asSRT() / .asVTT(), .transcribe(), .translate().

aiTranslate()

<pre class="EnlighterJSRAW" data-enlighter-language="java">english = aiTranslate()
    .file( "/audio/german.mp3" )
    .asText()
    .translate()
</pre>

📚 **[Audio Docs](https://ai.ortusbooks.com/main-components/audio "Audio Docs")**

🤖 Agent Registry --- aiAgentRegistry()  

3.2.0 introduces the AIAgentRegistry --- a global singleton that gives you centralized discoverability, observability, and lifecycle management for all agents running in your BoxLang application.

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Auto-register at creation time
agent = aiAgent(
    name: "support-agent",
    description: "Customer support agent",
    register: true,
    module: "my-app"
)

// Or register manually
aiAgentRegistry().register( agent, "my-app" )

// Discover what's running
agents = aiAgentRegistry().listAgents()
info   = aiAgentRegistry().getAgentInfo( "support-agent@my-app" )

// Resolve a mixed array of string keys and live instances
resolved = aiAgentRegistry().resolveAgents( [
    "support-agent@my-app",
    anotherAgentInstance
] )

// Clean up
aiAgentRegistry().unregister( "support-agent@my-app" )
aiAgentRegistry().unregisterByModule( "my-app" )
</pre>

Module Authors: First-Class Agent \& Tool Registration 🎯  

This is a big deal for the BoxLang ecosystem. Developers building BoxLang modules can now ship agents and tools that auto-register themselves globally when the module loads --- no manual wiring by the application developer required.

Define your aiAgent() instances with register: true and a module namespace  

Define your tools, scan them via aiToolRegistry().scan( new MyTools(), "my-module" ), and they appear globally as toolName@my-module  

Application developers can consume your agents and tools by name, from any part of their app, the moment your module is installed  

This makes bx-ai a genuine platform for building composable, discoverable AI ecosystems --- publish a module to ForgeBox, and your agents and tools show up ready to use. 🚀

Two new interception points fire on registry changes: onAIAgentRegistryRegister and onAIAgentRegistryUnregister.

⏸️ MCP Server Pause/Resume  

MCPServer now supports pausing and resuming without tearing down configuration or losing registered tools. Ideal for maintenance windows, graceful degradation, or controlled rollouts.

<pre class="EnlighterJSRAW" data-enlighter-language="java">server = MCPServer( "my-tools", "Provides custom tools" )
    .registerTool( myTool )

server.pause()

if ( server.isPaused() ) {
    println( "Server is paused — rejecting all non-ping requests" )
}

server.resume()
</pre>

pause() --- fires onMCPServerPause; all non-ping requests receive error code -32005  

resume() --- fires onMCPServerResume; normal handling restored  

getSummary() now includes a paused boolean  

📊 MCP Server \& Client Observability  

Server Analytics  

MCP server monitoring gets a major overhaul in 3.2.0:

Thread-safe counters using named locks across all stat operations  

Security failure tracking --- auth failures, API key rejections, body-size violations all get dedicated counters  

Per-tool error tracking --- byTool\[name\].errors with errors.byTool roll-up  

Active concurrent request counter --- activeRequests increments and decrements in real time  

Requests-per-minute rate --- exposed in getSummary()  

X-Request-ID correlation --- request IDs echoed in response headers and event payloads  

Paused-request stats --- rejected requests tracked when server is paused  

onMCPError now fires for METHOD_NOT_FOUND  

Client Stats --- MCPClient  

MCPClient gains full internal usage and performance tracking:

<pre class="EnlighterJSRAW" data-enlighter-language="java">client = MCP( "http://localhost:3000" )

tools  = client.listTools()
result = client.callTool( "search", { query: "BoxLang" } )

// Inspect what's happening
stats   = client.getStats()   // per-operation, per-tool, per-URI breakdowns
summary = client.getSummary() // totalCalls, successRate, avgResponseTime

// Reset when needed
client.resetStats()
</pre>

Three new interception points cover the full client lifecycle: onMCPClientRequest, onMCPClientResponse, onMCPClientError.

🔧 Type-Aware Tool Argument Support  

Tool schemas in bx-ai are now generated directly from callable parameter metadata, so LLMs finally receive accurate JSON Schema types for every argument instead of a flat bag of strings. ClosureTool.getArgumentsSchema() maps BoxLang types naturally --- numeric, integer, float, and double become "number", boolean becomes "boolean", array becomes "array" with "items": {}, and struct becomes "object" --- meaning LLMs can send native JSON values for non-string arguments and tools behave exactly as their signatures declare. On the output side, BaseTool.invoke() continues to serialize results consistently for provider compatibility, converting simple values via toString() and complex values via JSON serialization, keeping the tool contract clean in both directions. 🎯

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Tool with numeric and boolean arguments
// LLM sends { "quantity": 3, "applyDiscount": true } — no casting needed
calculateTotal = aiTool(
    name: "calculateTotal",
    description: "Calculate order total with optional discount",
    tool: ( numeric price, numeric quantity, boolean applyDiscount = false ) -&gt; {
        total = price * quantity
        if ( applyDiscount ) total *= 0.9
        return { summary: "Order total calculated", total: total }
    }
)

// Tool with an array argument
// LLM sends { "tags": ["boxlang", "ai", "tools"] } — native array
tagContent = aiTool(
    name: "tagContent",
    description: "Apply a list of tags to a content item",
    tool: ( string contentId, array tags ) -&gt; {
        // tags arrives as a real BoxLang array
        return {
            summary : "Tags applied to #contentId#",
            applied : tags.len(),
            tags    : tags
        }
    }
)

// Tool with a struct argument
// LLM sends { "filter": { "status": "active", "minAge": 18 } } — native struct
queryUsers = aiTool(
    name: "queryUsers",
    description: "Query users by filter criteria",
    tool: ( struct filter, numeric limit = 10 ) -&gt; {
        results = userService.query( filter, limit )
        return {
            summary : "Found #results.len()# users",
            count   : results.len(),
            data    : results
        }
    }
)

agent = aiAgent(
    tools: [ calculateTotal, tagContent, queryUsers ]
)
</pre>

🐛 Bug Fix --- ClosureTool.doInvoke() JSON Struct Handling  

MCP clients that send JSON fields as real objects or arrays (rather than pre-stringified JSON) no longer cause "Can't cast Struct to a string" errors. doInvoke() now inspects declared parameters and calls jsonSerialize() on any non-simple value whose declared type is string. Silent, automatic, no code changes required.

📦 Module Configuration  

New image Settings Block

<pre class="EnlighterJSRAW" data-enlighter-language="java">{
  "modules": {
    "bxai": {
      "settings": {
        "image": {
          "defaultProvider": "openai",
          "defaultApiKey": "",
          "defaultModel": "gpt-image-1",
          "defaultSize": "1024x1024",
          "defaultQuality": "standard",
          "defaultStyle": "vivid",
          "defaultInstructions": ""
        }
      }
    }
  }
}
</pre>

New Interception Points  

3.2.0 brings bx-ai to 50 total interception points, adding 10 new events:

|            Event            |           When Fired            |
|-----------------------------|---------------------------------|
| beforeAIImageGeneration     | Before image generation request |
| afterAIImageGeneration      | After image generation response |
| onAIImageRequest            | Image request object created    |
| onAIImageResponse           | Image response received         |
| onAIAgentRegistryRegister   | Agent registered                |
| onAIAgentRegistryUnregister | Agent unregistered              |
| onMCPServerPause            | MCP server paused               |
| onMCPServerResume           | MCP server resumed              |
| onMCPClientRequest          | MCP client HTTP request         |
| onMCPClientResponse         | MCP client HTTP response        |
| onMCPClientError            | MCP client HTTP error           |

🚀 Upgrade Now

<pre class="EnlighterJSRAW" data-enlighter-language="java"># CommandBox
box install bx-ai

# OS
install-bx-module bx-ai
</pre>

📚 Full Docs: ai.ortusbooks.com 💬 Community: community.ortussolutions.com ⭐ GitHub: github.com/ortus-boxlang/bx-ai

BoxLang AI 3.2.0 is a platform release: image generation, web search, fluent audio, a global agent \& tool registry, and deep observability all land together. We can't wait to see what you build. 🎉
