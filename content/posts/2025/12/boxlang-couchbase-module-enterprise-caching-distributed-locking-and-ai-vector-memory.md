---
title: "BoxLang Native Couchbase Module"
slug: "boxlang-couchbase-module-enterprise-caching-distributed-locking-and-ai-vector-memory"
date: "2025-12-16T08:37:02+00:00"
lastmod: "2025-12-17T08:28:07+00:00"
description: "Ortus Solutions is thrilled to announce the official release of bx-couchbase v1.0, a groundbreaking module that brings enterprise-grade Couchbase capabilities to the BoxLang ecosystem"
authors:
  - "luis-majano"
image: "https://foojay.io/wp-content/uploads/2025/12/bx-couchbase.jpg"
categories:
  - "AI"
  - "BoxLang"
  - "Databases"
  - "Java"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**Ortus Solutions** is thrilled to announce the official release of **bx-couchbase v1.0** , a groundbreaking module that brings native enterprise-grade Couchbase capabilities to the BoxLang language and ecosystem. Designed for modern distributed applications, **bx-couchbase** unifies high-performance caching, resilient distributed locking, and advanced AI vector memory---empowering developers to build scalable, intelligent, fault-tolerant systems with unprecedented ease.

*** ** * ** ***

**Couchbase + BoxLang: A High-Performance Combination** {#h2-0-couchbase-boxlang-a-high-performance-combination}
----------------------------------------------------------------------------------------------------------------

Couchbase is more than a database---it's a distributed NoSQL platform engineered for speed, flexibility, and global scale. With sub-millisecond key-value operations, built-in vector search, and multi-data-center support, it is the ideal foundation for next-generation AI-powered applications.

The new **bx-couchbase v1.0** module exposes all this power through an elegant, intuitive, and fully integrated BoxLang API. Not only that, it is fully documented.

📘 **Documentation:**   
<https://boxlang.ortusbooks.com/boxlang-framework/boxlang-plus/modules/bx-couchbase>

🚀 **Enterprise-Grade Distributed Caching** {#h2-1-enterprise-grade-distributed-caching}
----------------------------------------------------------------------------------------

bx-couchbase introduces a complete cache provider with replication, fault tolerance, TTL support, scopes, and collections---all optimized for BoxLang. You can easily leverage any bucket, scope or collection as a BoxLang native Cache, even store all your session scope and distribute it.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">cache("default").set("user:123", {
    name: "Alice Smith",
    email: "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e584898c8680a5809d8488958980cb868a88">[email&nbsp;protected]</a>",
    role: "admin"
} )

user = cache("default").get("user:123")
    .orElseGet( () =&gt; loadUserFromDatabase(123) )</pre>

**Highlights:**

* Sub-millisecond read/write operations
* Automatic replication and failover
* Built-in TTL support
* Logical grouping via scopes \& collections
* First-class integration with BoxLang caching APIs

🔐 **True Distributed Locking for Mission-Critical Workloads** {#h2-2-true-distributed-locking-for-mission-critical-workloads}
------------------------------------------------------------------------------------------------------------------------------

bx-couchbase v1.0 delivers robust distributed locking---ideal for financial transactions, inventory control, batch operations, and high-traffic systems. It has introduced a semantic component to give you locking capabilities into Couchbase. You can also use the functional approach as well.

### **Component-Based Locking (Recommended)** {#h3-3-component-based-locking-recommended}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">bx:couchbaseLock
    name="payment-#orderId#"
    cache="default"
    timeout="5"
    expires="30"{

    charge = getPaymentInfo( orderId )
    result = processCharge( charge )
    updateOrderStatus( orderId, "paid" )

}</pre>

### **Callback-Based Locking** {#h3-4-callback-based-locking}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">result = couchbaseLock(
    cacheName = "default",
    name = "inventory-#productId#",
    timeout = 5,
    expires = 30,
    callback = () =&gt; {
        stock = getStock( productId )
        if (stock.quantity &gt;= orderQty) {
            stock.quantity -= orderQty
            saveStock(productId, stock)
            return { success: true }
        }
        return { success: false, error: "Insufficient stock" }
    }
)</pre>

**Real-World Use Cases:**

* 💰 Payment processing
* 📦 Inventory control
* 🔄 Batch job coordination
* 👤 Serialized user updates
* 🎫 Ticketing \& reservation systems

🤖 **AI Vector Memory for BoxLang Agents** {#h2-5-ai-vector-memory-for-boxlang-agents}
--------------------------------------------------------------------------------------

A major highlight of v1.0 is deep integration with the **bx-ai** v2 module, enabling Couchbase-backed vector memory for AI agents, chatbots, and RAG pipelines.

### **Example: Persistent Vector-Powered Memory** {#h3-6-example-persistent-vector-powered-memory}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">memory = aiMemory( "cache", {
    cacheName: "couchbase_ai_memory",
    scope: "ai",
    collection: "conversations",
    embeddingProvider: "openai",
    embeddingModel: "text-embedding-3-small"
})

agent = aiAgent(
    name: "Customer Support Bot",
    instructions: "You are a helpful customer support agent",
    memory: memory,
    model: aiModel( "openai" )
);

response = agent.run( "What did we discuss about billing?" )</pre>

### **Multi-Tenant Isolation** {#h3-7-multi-tenant-isolation}

Each user and conversation remains fully isolated:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">aliceMemory = aiMemory( "cache",
    userId: "alice",
    conversationId: "support-123",
    config: { cacheName: "ai_memory" }
)</pre>

### **Hybrid Memory Model** {#h3-8-hybrid-memory-model}

Combine short-term recency + long-term semantic search:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">memory = aiMemory( "hybrid", {
    window: { type: "windowed", size: 10 },
    vector: {
        type: "cache",
        cacheName: "ai_memory",
        limit: 5
    }
})</pre>

**AI Features:**

* Meaning-based semantic search
* User \& conversation isolation
* Persistent, scalable vector storage
* Hybrid search models
* Support for OpenAI, Cohere, Voyage \& more

🛠️ **Direct Couchbase SDK Access** {#h2-9-direct-couchbase-sdk-access}
-----------------------------------------------------------------------

For advanced use cases, developers can directly access Couchbase Java SDK primitives but with many dynamic features and automatic serialization/deserialization, and casting.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">cluster = couchbaseGetCluster("default")
collection = couchbaseGetCollection("default")

result = collection.query("
    SELECT * FROM myapp
    WHERE type = 'user'
    LIMIT 100
")</pre>

📦 **Session Storage Backed by Couchbase** {#h2-10-session-storage-backed-by-couchbase}
---------------------------------------------------------------------------------------

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Application.bx
this.sessionStorage = "couchbase"

session.user = userObject;</pre>

* Sub-millisecond KV speed
* Automatic sharding
* Replication \& HA built-in
* Memory-first architecture
* Global multi-cluster support

Install BoxLang OS, or chose a web runtime, then install the couchbase module. (www.boxlang.io)

### **Installation** {#h3-11-installation}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">install-bx-module bx-couchbase
# or
box install bx-couchbase</pre>

### **Quick App Configuration** {#h3-12-quick-app-configuration}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">this.caches[ "default" ] = {
    provider: "Couchbase",
    properties: {
        connectionString: "couchbase://localhost",
        username: "Administrator",
        password: "password",
        bucket: "myapp",
        scope: "_default",
        collection: "_default"
    }
}</pre>

### **E-Commerce Inventory Protection** {#h3-13-e-commerce-inventory-protection}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">function reserveProduct( productId, quantity ) {
    return couchbaseLock(
        cacheName = "default",
        name = "inventory-#productId#",
        timeout = 5,
        expires = 10,
        callback = () =&gt; { ... }
    )
}</pre>

### **AI-Powered Customer Support** {#h3-14-ai-powered-customer-support}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">agent = aiAgent(...)
return agent.run(message)</pre>

### **Financial Transaction Processing** {#h3-15-financial-transaction-processing}

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">couchbaseLock("default", "payment-#orderId#", 5, 30, () =&gt; { ... })</pre>

* 📘 Docs: <https://boxlang.ortusbooks.com/boxlang-framework/boxlang-plus/modules/bx-couchbase>
* 🐞 Issues: <https://ortussolutions.atlassian.net>
* 💬 Community Forum: <https://community.ortussolutions.com>
* 💼 Support: <https://www.boxlang.io/plans#open-source>
