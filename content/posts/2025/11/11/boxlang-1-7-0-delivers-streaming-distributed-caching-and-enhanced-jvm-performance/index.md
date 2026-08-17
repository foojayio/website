---
title: "BoxLang 1.7 Released!"
slug: "boxlang-1-7-0-delivers-streaming-distributed-caching-and-enhanced-jvm-performance"
date: "2025-11-11T12:59:50+00:00"
lastmod: "2025-11-11T13:01:47+00:00"
description: "Ortus Solutions announced the release of **BoxLang 1.7.0**, a major update to its modern dynamic language for the JVM that delivers enterprise-grade capabilities for building real-time, horizontally scalable, and AI-driven applications."
authors:
  - "luis-majano"
image: "boxlang-v1.7.0.jpg"
categories:
  - "BoxLang"
  - "Press"
  - "Release Notes"
tags:
related_posts:
  - "foojay-podcast-76"
enlighterjs: true
frozen: false
---

**Dynamic JVM Language Adds Server-Sent Events, JDBC Cache Store, and AST Generation Capabilities**

**Houston, TX -- November 7, 2025** -- Ortus Solutions announced the release of **BoxLang 1.7.0**, a major update to its modern dynamic language for the JVM that delivers enterprise-grade capabilities for building real-time, horizontally scalable, and AI-driven applications.

This release introduces **native Server-Sent Events (SSE)** for real-time streaming, **JDBC-powered distributed caching** , **Abstract Syntax Tree (AST)** programmatic access, and **bytecode compatibility versioning**---alongside extensive performance optimizations across the runtime.

Real-Time Streaming with Server-Sent Events {#h2-0-real-time-streaming-with-server-sent-events}
-----------------------------------------------------------------------------------------------

The centerpiece of this release is full **Server-Sent Events (SSE)** integration via a new `SSE()` built-in function and an enhanced **Emitter system**. These additions empower developers to build real-time, event-driven applications such as AI chatbots, live analytics dashboards, progress streams, and notification services.

Version 1.8 will extend this with an upcoming `SSEConsumer()` API, enabling runtime-to-runtime event consumption for distributed systems.

```
// Stream analytics data in real time
SSE(
    callback : ( emitter ) => {
        var startTime = now();
        while ( !emitter.isClosed() && dateDiff( "s", startTime, now() ) < 300 ) {
            emitter.send({
                activeUsers : getActiveUserCount(),
                requestsPerSecond : getCurrentRPS(),
                avgResponseTime : getAvgResponseTime(),
                timestamp : now()
            }, "metrics");
            sleep(5000);
        }
        emitter.close();
    },
    async : true,
    keepAliveInterval : 15000
);
```


The implementation includes **async execution** , **automatic keep-alive** , **client disconnect detection** , **CORS support** , and **automatic chunking** for messages exceeding 32KB---ensuring efficient, resilient streaming at scale.

```
// Stream AI responses token by token
SSE(
    callback : ( emitter ) => {
        var response = callAIService();
        while ( !emitter.isClosed() && response.hasMoreTokens() ) {
            emitter.send( response.getNextToken(), "token" );
        }
        emitter.send({ complete : true }, "done");
    },
    async : true,
    keepAliveInterval : 30000,
    timeout : 300000
);
```


Enterprise-Ready Distributed Caching {#h2-1-enterprise-ready-distributed-caching}
---------------------------------------------------------------------------------

BoxLang 1.7.0 debuts the **JDBC Cache Store** , a robust distributed caching solution that enables shared caches across multiple BoxLang instances using enterprise databases such as **Oracle, MySQL, PostgreSQL, Microsoft SQL Server, Apache Derby, HSQLDB,** and **SQLite**.

```
// boxlang.json
{
  "caches": {
    "distributedCache": {
      "provider": "BoxCache",
      "properties": {
        "objectStore": "JDBCStore",
        "datasource": "myDatasource",
        "table": "boxlang_cache",
        "autoCreate": true,
        "maxObjects": 1000,
        "evictionPolicy": "LRU"
      }
    }
  }
}
```


The JDBC store includes **automatic schema creation** , **database-specific SQL optimization** , **eviction policy support (LRU, LFU)** , and **Base64 object serialization** for complex types. All cache stores now implement `isDistributed()` for ecosystem introspection.

Advanced Code Analysis with BoxAST() {#h2-2-advanced-code-analysis-with-boxast}
-------------------------------------------------------------------------------

Developers can now access BoxLang's internal **Abstract Syntax Tree** through the new `BoxAST()` built-in function. This feature supports the creation of **linters** , **formatters** , **migration tools** , and **refactoring utilities**---making it invaluable for static analysis and CFML-to-BoxLang transitions.

```
// Parse BoxLang code
ast = BoxAST( source : "x = 1 + 2; y = x * 3;" );

// Or use the convenient member method
ast = "function hello() { return 'world'; }".toAST();

// Parse from files
ast = BoxAST( filepath : "myScript.bx" );

// Return as JSON for external tools
astJson = BoxAST(
    source : "function hello() { return 'world'; }",
    returnType : "json"
);

// Parse CFML code for migration tools
cfAst = BoxAST(
    source : "<cfset x = 1><cfoutput>#x#</cfoutput>",
    sourceType : "cftemplate"
);
```


`BoxAST()` supports **BoxLang script and template syntax** , as well as **CFML/ColdFusion** code parsing for migration purposes, returning structured, JSON, or text output.

Bytecode Compatibility Versioning {#h2-3-bytecode-compatibility-versioning}
---------------------------------------------------------------------------

To enhance module stability and cross-version reliability, BoxLang now implements **bytecode compatibility versioning**, ensuring that compiled artifacts remain consistent and reusable across multiple runtime releases---a major improvement for module authors and enterprise teams maintaining large applications.

Performance \& Stability Enhancements {#h2-4-performance-stability-enhancements}
--------------------------------------------------------------------------------

Version 1.7.0 delivers substantial runtime performance boosts, including:

* ⚡ **Faster Scheduled Tasks** via optimized concurrent maps
* 🚀 **Improved Static Initializers** for class loading
* 💾 **Optimized ASM Bytecode Generation** eliminating intermediate disk writes
* 🔁 **Cache Store Enhancements** across all providers

Additionally, over **40 bug fixes** improve database interaction, file handling, HTTP services, CFML compatibility, and Windows platform support.

Open and Professional Ecosystem {#h2-5-open-and-professional-ecosystem}
-----------------------------------------------------------------------

BoxLang follows a **professional open-source model** with three editions: **Open Source (Apache 2.0)** , **BoxLang+** , and **BoxLang++** .  

The open-source core remains free, while commercial tiers provide support, advanced modules, and enterprise tooling---licensed fairly based on **usage, not infrastructure**.

New **BoxLang+ exclusive modules** include:

* 🧠 **bx-redis** -- High-performance Redis integration
* 📊 **bx-csv** -- Advanced CSV processing
* 🔐 **bx-ldap** -- LDAP directory services
* 📈 **bx-spreadsheet** -- Excel file operations
* 📄 **bx-pdf** -- PDF generation

Future roadmap modules include **bx-couchbase** , **bx-mongodb** , and **bx-elasticsearch**.

Availability \& Resources {#h2-6-availability-resources}
--------------------------------------------------------

BoxLang 1.7.0 is available now via:

* **BoxLang Quick Installers** (Mac, Linux, Windows)
* **BoxLang Version Manager (BVM)**
* **Maven Central**
* **Direct download:** [boxlang.io/download](https://boxlang.io/download)

📘 **Release Notes:** [BoxLang 1.7.0](https://boxlang.ortusbooks.com/readme/release-history/1.7.0)  

📰 **Full Blog Post:** [BoxLang v1.7.0 Overview](https://www.ortussolutions.com/blog/boxlang-v170-real-time-streaming-distributed-caching-and-more)  

💻 **Try Online:** [try.boxlang.io](https://try.boxlang.io)  

📚 **Documentation:** [boxlang.ortusbooks.com](https://boxlang.ortusbooks.com)

About BoxLang {#h2-7-about-boxlang}
-----------------------------------

**BoxLang** is a next-generation, dynamic JVM language that runs seamlessly across multiple environments---**desktop, web, cloud, serverless, and embedded systems** . It combines the best features of Java, CFML, Python, Ruby, Go, and PHP to deliver a **modern, expressive, and functional syntax** with full Java interoperability.

With native capabilities for **scheduling** , **asynchronous execution** , **event-driven programming** , **task management** , and **modular architecture** , BoxLang functions as both a **language** and a **framework** , serving as a replacement for legacy CFML engines like **Adobe ColdFusion** and **Lucee**.

About Ortus Solutions {#h2-8-about-ortus-solutions}
---------------------------------------------------

**Ortus Solutions** is a leader in modern software development for the JVM ecosystem. The company builds innovative frameworks, tools, and languages, including **BoxLang** , **ColdBox MVC** , and **CommandBox CLI**, which power thousands of applications globally.

🌐 [www.ortussolutions.com](https://www.ortussolutions.com)

**Media Contact**   

Ortus Solutions, Corp.  

📧 [\[email protected\]](/cdn-cgi/l/email-protection#bcd5d2dad3fcd3cec8c9cfcfd3d0c9c8d5d3d2cf92dfd3d1)  

🌍 [www.ortussolutions.com](https://www.ortussolutions.com)  

🧩 [boxlang.io](https://boxlang.io) \| [github.com/ortus-boxlang/boxlang](https://github.com/ortus-boxlang/boxlang)
