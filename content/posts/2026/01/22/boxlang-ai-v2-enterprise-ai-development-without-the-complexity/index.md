---
title: "BoxLang AI v2: Enterprise AI Development Without the Complexity"
date: "2026-01-22T15:09:25+00:00"
lastmod: "2026-01-23T12:03:32+00:00"
description: "One Year. 100+ Features. Unlimited Possibilities. Just one year ago, in March 2024, we launched BoxLang AI 1.0. Today, we're thrilled to announce BoxLang…"
authors:
  - "cristobal-escobar"
image: "boxlang-ai-v2-700x467.jpg"
categories:
  - "AI"
  - "BoxLang"
  - "Cloud"
  - "Developer Tools"
  - "GenAI"
  - "Java"
  - "LLM"
  - "Use Cases"
related_posts:
frozen: false
---

![](boxlang-ai-v2-700x467.jpg)

**One Year. 100+ Features. Unlimited Possibilities.**

Just one year ago, in March 2024, we launched BoxLang AI 1.0. Today, we're thrilled to announce **BoxLang AI v2**---a massive leap forward that positions BoxLang as one of the most powerful and versatile AI framework on the JVM. This release is 9-months in the making, with over 100 new features! This would have not been possible without all the new features that the BoxLang JVM language exposes to developers like: Server Side Events, HTTP Streaming, HTTP pooling, Caching, and so much more.

## What Makes This Release Groundbreaking?

BoxLang AI v2 isn't an incremental update—it's a complete transformation. We've added **over 100 new features** that make building production-grade AI applications faster, simpler, and more powerful than ever.

## 🎯 Summary

BoxLang AI v2 delivers a complete AI platform with unprecedented capabilities:

* 🔌 **12+ AI Providers** - Single unified API for OpenAI, Claude, Gemini, Grok, Ollama, DeepSeek, Groq, Mistral, Cohere, Perplexity, OpenRouter, and HuggingFace
* 🤖 **Autonomous AI Agents** - Build agents with memory, tools, sub-agents, and multi-step reasoning
* 🔒 **Multi-Tenant Memory Systems** - Enterprise-grade isolation with 20+ memory types (standard + vector)
* 🧬 **Vector Memory \& RAG** - 10+ vector databases with semantic search and retrieval
* 📚 **Document Loaders** - Process 30+ file formats including PDF, Word, CSV, JSON, XML, databases, and web scraping
* 🛠️ **Real-Time Function Calling** - Agents can call APIs, query databases, and integrate with external systems
* 🌊 **Streaming Support** - Real-time token streaming through composable pipelines
* 📦 **Native Structured Output** - Type-safe responses using BoxLang classes, structs, or JSON schemas
* 🔗 **AI Pipelines** - Build composable workflows with models, transformers, and custom logic
* 📡 **MCP Protocol** - Build and consume Model Context Protocol servers, integrate with ANY MCP-compatible system
* 💬 **Fluent Interface** - Chainable, expressive syntax across all components
* 🦙 **Local AI** - Complete Ollama support for privacy, offline use, and zero API costs
* ⚡ **Async Operations** - Non-blocking futures for concurrent AI requests
* 🎯 **Event-Driven Architecture** - 25+ lifecycle events for logging, monitoring, and custom workflows
* 🏭 **Production-Ready** - Timeout controls, error handling, rate limiting, retries, and debugging tools
* ☁️ **Serverless Deployment** - Deploy AI agents on AWS Lambda with the BoxLang AWS Runtime
* ⏰ **Autonomous Scheduling** - Create self-running agents on any OS with BoxLang Scheduler  
  This release transforms BoxLang into the most comprehensive AI development platform available, with zero vendor lock-in and production-grade reliability.

## 🤖 Autonomous AI Agents

Build intelligent agents that think, remember, and act independently. Agents are the crown jewel of BoxLang AI v2—capable of multi-step reasoning, tool usage, memory management, and delegation.

```java
// Create an agent with multiple memories, tools, and sub-agents
agent = aiAgent(
    name: "Support Bot",
    instructions: "You are a helpful customer support agent",
    memory: [
        aiMemory( "vector", { provider: "chromadb" } ),      // Semantic search
        aiMemory( "cache", { cacheName: "sessions" } ),       // Session history
        aiMemory( "conversation" )                            // Current context
    ],
    tools: [ 
        customerLookupTool, 
        ticketSystemTool, 
        inventoryCheckTool,
        MCP( "http://crm-server:3000" )                      // External MCP tools
    ],
    subAgents: [
        technicalSupportAgent,                                // Delegate technical issues
        billingAgent                                          // Delegate billing questions
    ],
    model: aiModel( "claude", { model: "claude-sonnet-4.5" } )
);

// Agent orchestrates everything automatically
response = agent.run( "Find John's order, check inventory, and update shipping" );
```

**Agent Capabilities:**

**- Multiple Memories** - Combine vector, cache, conversation, and database memories  
**- Multiple Tools** - Integrate APIs, databases, MCP servers, and custom functions  
**- Sub-Agents** - Delegate specialized tasks to other agents  
**- Multi-Step Reasoning** - Break down complex requests automatically  
**- Context Awareness** - Maintain conversation history across sessions  
**- Error Handling** - Automatic retry logic and graceful degradation

## ⚡ Serverless AI Agents on AWS Lambda

Deploy AI agents as **serverless functions** using the [BoxLang AWS Runtime](https://boxlang.ortusbooks.com/getting-started/running-boxlang/aws-lambda "BoxLang AWS Runtime"):

```java
// Lambda handler with AI agent
function handler( event, context ) {
    agent = aiAgent(
        name: "Invoice Processor",
        memory: aiMemory( "dynamodb" ),
        model: aiModel( "openai" )
    );

    return agent.run( event.query );
}
```

**Benefits:**

* Zero server management
* Pay per invocation
* Auto-scaling
* Enterprise-grade reliability  
  Get started with our AWS Lambda Starter Template.

## 🔄 Autonomous Agents with BoxLang Scheduler

Create truly autonomous agents that run on schedules—no servers required:

```java
// Autonomous monitoring agent
class {
    function configure() {
        systemAgent = aiAgent(
            name: "System Monitor",
            instructions: "Analyze system metrics and alert on anomalies",
            tools: [ metricsAPI, slackNotifier ],
            model: aiModel( "grok" )
        )

        scheduler.task( "Monitor System" )
            .call( () => {
                systemAgent.run( "Check system health and notify if issues found" )
            })
            .everyHour()
    }
}
```

Deploy autonomous agents on **any OS** with the [BoxLang Scheduler](https://boxlang.ortusbooks.com/boxlang-framework/asynchronous-programming/scheduled-tasks "BoxLang Scheduler").

## 🧬 Multi-Tenant Vector Memory \& RAG

Enterprise-grade isolation with 10+ vector databases:

```java
// User-specific RAG memory
memory = aiMemory( 
    "vector",
    userId: "alice",
    conversationId: createUUID(),
    config: { 
        provider: "pinecone",
        embeddingModel: "text-embedding-3-large" 
    }
);

agent = aiAgent(
    name: "RAG Assistant",
    memory: memory,
    model: aiModel( "gemini" )
);

// Agent automatically retrieves relevant context
response = agent.run( "What did we discuss about Q4 projections?" );
```

**Supported Vector DBs**: ChromaDB, Pinecone, PostgreSQL+pgvector, Weaviate, Qdrant, Milvus, and more.

## 📚 Document Loaders for 30+ Formats

Load and process documents from any source:

```java
// Load and embed documents directly to memory stores

// Single memory ingestion
result = aiDocuments( "/docs", { type: "markdown" } )
    .toMemory( myVectorMemory )

// With chunking options
result = aiDocuments( "/knowledge-base" )
    .recursive()
    .extensions( [ "md", "txt" ] )
    .toMemory( myVectorMemory, { chunkSize: 500, overlap: 50 } )

// Multi-memory fan-out (async supported)
result = aiDocuments( "/docs", { type: "markdown" } )
    .toMemory( [ chromaMemory, pgVectorMemory ], { async: true } )
```

**Supported formats**: PDF, Word, CSV, JSON, XML, Excel, Markdown, HTML, databases, web scraping, and more.

## 🔌 12+ AI Provider Support

One API for all major providers:

```java
// Switch providers with zero code changes
response = aiChat( 
    "Explain quantum computing",
    { model: "gpt-4o" },
    { provider: "openai" }
);

// Same code, different provider
response = aiChat( 
    "Explain quantum computing",
    { model: "claude-sonnet-4.5" },
    { provider: "claude" }
);
```

**Providers:** OpenAI, Claude, Gemini, Grok, Groq, DeepSeek, Ollama, Mistral, Cohere, Perplexity, OpenRouter, HuggingFace.

## 📡 Model Context Protocol (MCP)

Full MCP support for building distributed AI systems. Create your own MCP servers or integrate with ANY external MCP-compatible system using the fluent `MCP()` function.

**Build MCP Servers:**

```java
// Create and expose your own MCP server
server = mcpServer( 
    name: "analytics",
    description: "Business Analytics MCP Server",
    version: "1.0.0"
)
    .addTool( salesDataTool )
    .addTool( reportGeneratorTool )
    .addTool( forecastingTool )
    .start();
```

**Consume ANY MCP Server:**

```java
// Integrate with external MCP servers using fluent MCP()
agent = aiAgent(
    name: "Enterprise Assistant",
    tools: [ 
        MCP( "http://crm-system:3000" ),          // CRM tools
        MCP( "http://analytics:3001" ),           // Analytics tools
        MCP( "http://inventory:3002" ),           // Inventory tools
        localCustomTool                            // Mix with local tools
    ],
    model: aiModel( "claude" )
);

// Agent can use tools from multiple MCP servers
response = agent.run( "Get Q4 sales from CRM and create forecast report" );
```

**Multiple MCP Servers:**

```java
// Connect to multiple MCP servers simultaneously
pipeline = aiModel( "grok" )
    .withTools([
        MCP( "https://github-mcp.com" ),
        MCP( "https://slack-mcp.com" ),
        MCP( "https://jira-mcp.com" )
    ])
    .invoke( "Create GitHub issue, notify in Slack, and update Jira ticket" );
```

**MCP Features:**

* Build custom MCP servers to expose your tools
* Consume external MCP servers with `MCP()` fluent function
* Connect to multiple MCP servers simultaneously
* Mix MCP tools with local function tools
* Full protocol compliance for interoperability

## 📦 Native Structured Output

Get type-safe, validated responses directly from AI models. No more parsing JSON strings or handling malformed responses.

```java
// Define your BoxLang class
class Invoice {
    property name="invoiceNumber";
    property name="date";
    property name="total";
    property name="items" type="array";
}

// Get structured output directly
invoice = aiChat( 
    "Extract invoice data from this receipt: ...",
    { 
        model: "gpt-4o",
        response_format: { type: "json_schema", schema: Invoice }
    }
);

// Type-safe access
println( invoice.getInvoiceNumber() );
println( invoice.getTotal() );
```

**Structured Output Options:**

* BoxLang classes with properties
* Struct schemas
* JSON schema definitions
* Array responses
* Nested complex objects

## 🌊 Streaming Support

Real-time token streaming for responsive applications thanks to BoxLang:

```java
// Stream responses as they generate
aiChatStream( 
    "Write a detailed technical article",
    ( chunk ) => {
        print( chunk );  // Display tokens as they arrive
        flush();
    },
    { model: "claude-sonnet-4.5" }
);

// Stream through pipelines
pipeline = aiModel( "openai" )
    .pipe( transformerA )
    .pipe( transformerB )
    .stream( 
        input,
        ( token ) => handleStreamToken( token )
    );
```

## ⚡ Async Operations

Non-blocking futures for concurrent AI requests:

```java
// Execute multiple AI requests concurrently
future1 = aiChatAsync( "Analyze customer sentiment", { provider: "openai" } );
future2 = aiChatAsync( "Generate product description", { provider: "claude" } );
future3 = aiChatAsync( "Translate to Spanish", { provider: "gemini" } );

// Wait for all to complete
results = [ 
    future1.get(), 
    future2.get(), 
    future3.get() 
];

// Or use fluent combinators
future1.thenApply( ( result ) => processResult( result ) )
    .thenCompose( ( data ) => aiChatAsync( "Summarize: " & data ) )
    .thenAccept( ( summary ) => println( summary ) );
```

## 🔗 AI Pipelines

Build composable workflows with models, transformers, and custom logic:

```java
// Complex multi-step pipeline
pipeline = aiDocuments( pdfFiles )
    .load()
    .chunk( maxSize: 1000 )
    .embed( provider: "openai" )
    .pipe( aiModel( "claude" ) )
    .pipe( aiTransform( "extract-json" ) )
    .pipe( aiTransform( "validate" ) )
    .pipe( ( data ) => {
        saveToDatabase( data );
        return data;
    });

result = pipeline.invoke( inputData );
```

**Pipeline Features:**

* Chain multiple AI models
* Add custom transformers
* Inject business logic
* Handle errors gracefully
* Monitor execution time

## 🎯 Event-Driven Architecture

25+ lifecycle events for observability and control:

```java
// Listen to AI events
interceptorService.listen( "onAIRequest", ( data ) => {
    logger.info( "AI Request to #data.provider#" );
    recordMetrics( data );
});

interceptorService.listen( "onAITokenCount", ( data ) => {
    trackCosts( 
        provider: data.provider,
        tokens: data.totalTokens 
    );
});

interceptorService.listen( "onAIError", ( data ) => {
    if( data.canRetry ) {
        scheduleRetry( data );
    } else {
        alertOps( data.error );
    }
});
```

**Available Events:**

* Request/Response lifecycle
* Token usage tracking
* Error handling
* Agent creation and execution
* Tool execution
* Memory operations
* Rate limit detection
* Model invocation
* Pipeline execution

## 🏭 Production-Ready Features

Built for enterprise deployment:

**Timeout Controls:**

```java
response = aiChat( 
    messages,
    {},
    { timeout: 30000 }  // 30 second timeout
);
```

**Error Handling:**

```java
try {
    result = agent.run( input );
} catch( AIProviderException e ) {
    // Handle rate limits, timeouts, etc.
    fallbackResult = useBackupProvider();
}
```

**Rate Limiting:**

```java
// Automatic rate limit detection and retry
interceptorService.listen( "onAIRateLimitHit", ( data ) => {
    waitTime = data.retryAfter ?: 60;
    sleep( waitTime * 1000 );
    retry( data.provider );
});
```

**Debugging:**

```java
// Comprehensive logging
response = aiChat( 
    messages,
    {},
    { 
        logRequest: true,
        logResponse: true,
        logRequestToConsole: true 
    }
);
```

## 🦙 Local AI with Ollama

Zero API costs, complete privacy, offline capability:

```java
// Run AI completely locally
agent = aiAgent(
    name: "Private Assistant",
    model: aiModel( "ollama", { 
        model: "llama3.2",
        chatURL: "http://localhost:11434"
    })
);

// No internet required, no API keys, no costs
response = agent.run( "Analyze this confidential document" );
```

**Local AI Benefits:**

* Zero API costs
* Complete data privacy
* Offline operation
* No rate limits
* Full control

## 🎯 Real-World Use Cases

**Customer Support Automation:**

```java
agent = aiAgent(
    name: "Support Agent",
    memory: aiMemory( "cache" ),
    tools: [ zenDeskAPI, slackNotifier ],
    model: aiModel( "claude", { model: "claude-sonnet-4.5" } )
);
```

**Data Analysis Pipeline:**

```java
pipeline = aiModel( "openai" )
    .pipe( aiTransform( "extract-json" ) )
    .pipe( aiTransform( "validate" ) )
    .pipe( ( data ) => saveToDatabase( data ) );

result = pipeline.invoke( csvData );
```

**Scheduled Report Generation:**

```java
scheduler.task( "Weekly Report" )
    .call( () => {
        agent = aiAgent(
            name: "Report Generator",
            model: aiModel( "gemini" )
        );
        report = agent.run( "Generate weekly sales report" );
        sendEmail( report );
    })
    .onMondays()
    .at( "09:00" );
```

## 📖 Comprehensive Learning Resources

We've built a complete ecosystem to help you master BoxLang AI:

### 🌐 Official Website

[ai.boxlang.io](https://ai.boxlang.io/ "ai.boxlang.io") - Features, examples, and quickstart guides

### 📚 Complete Documentation

[ai.ortusbooks.com](https://ai.ortusbooks.com/ "ai.ortusbooks.com") - Full API reference and guides

### 🎓 Free AI Bootcamp

[BoxLang AI Bootcamp](https://github.com/ortus-boxlang/bx-ai/tree/development/bootcamp "BoxLang AI Bootcamp") - Hands-on training from basics to advanced

### 💻 60+ Code Examples

[Examples Gallery](https://github.com/ortus-boxlang/bx-ai/tree/development/examples "Examples Gallery") - Real-world implementations

### 🏢 Professional Services

[ai.ortussolutions.com](https://ai.ortussolutions.com/ "ai.ortussolutions.com") - Enterprise consulting and support

## 🚀 Getting Started

### **OS Applications**

```bash
install-bx-module bx-ai
```

### **AWS Lambda**

```bash
cd src/resources
install-bx-module bx-ai --local
```

### Web Applications

```bash
box install bx-ai
```

### Your First Agent

```java
agent = aiAgent(
    name: "Assistant",
    instructions: "You are a helpful AI assistant",
    model: aiModel( "openai" )
);

response = agent.run( "How do I use BoxLang AI?" );
println( response );
```

## Why BoxLang AI v2 Matters

### For Developers:

* One fluent API for all AI providers
* No vendor lock-in
* Production-ready with timeouts, retries, and error handling
* Local AI support with Ollama (zero API costs)  

  ### For Enterprises:

* Multi-tenant isolation
* Enterprise-grade memory systems
* Serverless deployment options
* Professional support available  

  ### For the Ecosystem:

* 100% open source (Apache 2)
* Active community
* Extensive documentation
* Real-world examples

## The Year Ahead

In just 12 months, we've transformed BoxLang AI from a simple chat wrapper into a comprehensive AI platform. But we're not stopping here.

**With v2, you can:**

* Build autonomous agents that work 24/7
* Deploy serverless AI functions globally
* Create RAG systems with semantic search
* Integrate 12+ AI providers seamlessly
* Process 30+ document formats
* Build MCP-compatible tools

**What's coming:**

* Guardrails System
* Agent hooks
* Runnable Middleware
* Observability
* Auditing
* More vector databases
* More cowbell 🐄 🔔

**The future of AI development is here. It's written in BoxLang.**

## Get Started TodayGet Started Today

* Documentation: [ai.ortusbooks.com](https://ai.ortusbooks.com/ "ai.ortusbooks.com")
* Website: [ai.boxlang.io](https://ai.boxlang.io/ "ai.boxlang.io")
* Bootcamp: [github.com/ortus-boxlang/bx-ai/bootcamp](https://github.com/ortus-boxlang/bx-ai/tree/development/bootcamp "github.com/ortus-boxlang/bx-ai/bootcamp")
* Examples: [github.com/ortus-boxlang/bx-ai/examples](https://github.com/ortus-boxlang/bx-ai/tree/development/examples "github.com/ortus-boxlang/bx-ai/examples")
* Professional Services: [ai.ortussolutions.com](https://ai.ortussolutions.com/ "ai.ortussolutions.com")

**Join our community and help shape the future of AI on the JVM!**
