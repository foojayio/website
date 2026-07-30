---
title: "BoxLang 1.11.0 Release"
slug: "boxlang-1-11-0-release"
date: "2026-03-17T09:40:04+00:00"
lastmod: "2026-03-17T15:45:32+00:00"
description: "We're proud to announce BoxLang 1.11.0, a highly focused performance and stability release that delivers measurable speed improvements across every - by Cristobal Escobar"
authors:
  - "cristobal-escobar"
image: "/images/posts/2026/03/boxlang-1-11-0-release/boxlang-v1.11.0.jpg"
categories:
  - "AI"
  - "BoxLang"
  - "Cloud"
  - "Developer Tools"
  - "Java"
  - "Performance"
  - "Release Notes"
tags:
related_posts:
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
  - "boxlang-1-14-0-navigate-anything-jsonpath-comes-to-boxlangs-datanavigator"
  - "boxlang-1-14-0-query-transformers-take-full-control-of-your-query-results"
  - "boxlang-1-14-0-boxset-is-here-boxlangs-new-first-class-set-type"
enlighterjs: true
frozen: false
---

![](/images/posts/2026/03/boxlang-1-11-0-release/boxlang-v1.11.0-700x467.jpg)

We're proud to announce **BoxLang 1.11.0**, a highly focused performance and stability release that delivers measurable speed improvements across every BoxLang application, with zero code changes required. The team invested deeply in bytecode generation, class loading, lock management, and type casting to produce one of the most impactful runtime optimization releases to date. Alongside the performance wave, this release resolves critical concurrency bugs, hardens DateTime handling, and ships powerful new developer tooling.

🚀 What's New in 1.11.0 {#h2-0-what-s-new-in-1-11-0}
----------------------------------------------------

You can find the full release notes here:  
<https://boxlang.ortusbooks.com/readme/release-history/1.11.0>

### ⚡ Performance Wave --- 15+ Targeted Runtime Speedups {#h3-1-performance-wave-15-targeted-runtime-speedups}

BoxLang 1.11.0 includes over **15 targeted performance improvements** spanning bytecode compilation, runtime execution, memory management, and concurrency. Every BoxLang application benefits immediately.

Bytecode \& Compilation  

The compiler has been significantly tightened:

* **Optimized bytecode generation** avoids unnecessary casts during value operations
* **Cached** `isFinal` **and** `isAbstract` **flags** at compile time instead of computing them at runtime
* **Reworked FQN parsing** eliminates expensive regex operations on every class lookup
* **Improved ClassInfo lookup** during compilation using better caching strategies
* **Optimized ClassLocator cache key generation** via improved `hashCode()` creation  
  Runtime Execution  
  Core runtime operations are noticeably faster:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// All of these are faster in 1.11.0 — no code changes needed
result = myClass.doWork()           // Faster class construction via this.get()
found  = myArray.find( "value" )    // arrayFind optimized, avoids stream overhead
flag   = isBoolean( "true" )        // Faster boolean string parsing
someBif( arg1, arg2 )               // Arg/return type casting via keys, not reflection
</pre>

Memory \& Concurrency

* **Cached closest** `variables` **scope** reference in function contexts
* **Cached web request config** instead of re-resolving per request
* **Case-insensitive string matching** uses an optimized algorithm
* **Reduced** `toRealPath()` **calls** that were silently adding overhead on every file operation
* **Simplified constructor path** for Box Classes reduces object creation overhead
* **Removed function inner classes**, reducing class loading and GC pressure
* **Avoided** `Map.containsValue()` in UDF invocation (linear scan → constant time)  
  The cumulative effect is meaningful: applications under load will see reduced latency, lower GC pressure, and better throughput --- all with zero migration effort.

### 🔒 Concurrency \& Lock Safety --- Critical Fix {#h3-2-concurrency-lock-safety-critical-fix}

Two critical bugs in the exclusive lock system have been resolved. Before 1.11.0, exclusive locks could occasionally allow more than one thread into a supposedly exclusive section under high load ([BL-2203](http://https://ortussolutions.atlassian.net/browse/BL-2203 "BL-2203"), [BL-2205](http://https://ortussolutions.atlassian.net/browse/BL-2205 "BL-2205")).

<pre class="EnlighterJSRAW" data-enlighter-language="java">// This critical section is now truly exclusive under concurrent load
lock name="processPayment_#orderId#" type="exclusive" timeout="30" {
    // Only ONE thread will be here at a time — guaranteed in 1.11.0
    if ( !paymentProcessed( orderId ) ) {
        processPayment( orderId )
    }
}
</pre>

Lock storage has also been improved ([BL-2201](http://https://ortussolutions.atlassian.net/browse/BL-2201 "BL-2201")) for better performance and memory efficiency. If you rely on exclusive locks for payment processing, inventory management, or any critical section --- this is an important upgrade.

### 🗓️ DateTime Casting Reliability {#h3-3-datetime-casting-reliability}

A comprehensive sweep of DateTime casting fixes ensures robust date handling across all common formats and edge cases:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// All of these now work reliably in 1.11.0
date1 = createDateTime( "01-31-2026 23:59:  59" )          // BL-2189
date2 = createDateTime( "9-30-2010" )                     // BL-2222
date3 = parseDateTime( "2026-01-31 00:00: 00.000" )        // ODBC Timestamp (BL-2143)

// Query of Queries with ODBC Timestamp columns now compiles correctly
qoq = queryExecute(
    "SELECT * FROM myQuery WHERE dateCol &gt; :dt",
    { dt : now() },
    { dbtype : "query" }
)  // BL-2144

// DateTimeCaster now handles ODBC Date/Time formats
cast1 = dateTimeFormat( odbcDate, "yyyy-mm-dd" )          // BL-2188
</pre>

### 🆕 **`enforceUDFTypeChecks`** Configuration Setting {#h3-4-enforceudftypechecks-configuration-setting}

A new runtime setting allows you to skip UDF argument and return type validation --- useful for trusted high-performance codebases:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// boxlang.json
{
    "enforceUDFTypeChecks": false
}
</pre>

When false, BoxLang skips argument type validation and return type casting on function calls --- similar to how the Java compiler performs generic type erasure. This can improve performance but removes the safety net of runtime type checks.

### ⏱️ **`getTickCount()`** --- Nanosecond \& Second Precision {#h3-5-gettickcount-nanosecond-second-precision}

`getTickCount()` now supports `nano` and `second` units alongside the existing `millisecond` support:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Micro-benchmark with nanosecond precision
start   = getTickCount( "nano" )
doExpensiveWork()
elapsed = getTickCount( "nano" ) - start
println( "Elapsed: #elapsed# ns" )

// Coarse timing in seconds
start   = getTickCount( "second" )
sleep( 2000 )
elapsed = getTickCount( "second" ) - start
println( "Elapsed: #elapsed# seconds" )  // 2
</pre>

### 🗑️ New BIF: `ExecutorDelete()` {#h3-6-new-bif-executordelete}

The missing `ExecutorDelete()` BIF has been added, completing the executor lifecycle management API. Previously, shutting down an executor did not remove it from the executor registry (BL-2168), causing issues when recreating executors with the same name.

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Create an executor
myExecutor = executorNew( "myPool", "fixed", 10 )

// Submit work
future = executorSubmit( myExecutor, () =&gt; doWork() )
future.get()

// Full cleanup — now properly removes it from the registry
executorDelete( "myPool" )
</pre>

🤖 Core Runtime Updates {#h2-7-core-runtime-updates}
----------------------------------------------------

### 🏗️ Class System Improvements {#h3-8-class-system-improvements}

* **Super class loading** improved to handle complex inheritance hierarchies reliably ([BL-2211](http://https://ortussolutions.atlassian.net/browse/BL-2211 "BL-2211"))
* **Abstract class enforcement** relaxed --- abstract classes are no longer required to implement all interface methods ([BL-2251](http://https://ortussolutions.atlassian.net/browse/BL-2251 "BL-2251")), matching Java and CFML semantics
* **Typed array returns** no longer throw NPE when a class is instantiated via a different invocation path ([BL-2237](http://https://ortussolutions.atlassian.net/browse/BL-2237 "BL-2237"))
* **Implicit accessors** now generate the correct return type in method signatures instead of always using `any` ([BL-2195](http://https://ortussolutions.atlassian.net/browse/BL-2195 "BL-2195"))  

  ### 🧵 Thread \& Execution Fixes {#h3-9-thread-execution-fixes}

* **Duplicate bytecode methods** no longer generated in edge cases ([BL-2207](http://https://ortussolutions.atlassian.net/browse/BL-2207 "BL-2207"))
* **Incompatible stack heights** when not assigning new `Foo()` resolved ([BL-2213](http://https://ortussolutions.atlassian.net/browse/BL-2213 "BL-2213"))
* **Illegal exception table range** in class files fixed ([BL-1916](http://https://ortussolutions.atlassian.net/browse/BL-1916 "BL-1916"))
* **Parser concurrency** issue in LSP fixed when getting cache size ([BL-2253](http://https://ortussolutions.atlassian.net/browse/BL-2253 "BL-2253"))  

  ### 📊 Query System {#h3-10-query-system}

* **`QueryNew()`** **and** **`queryAddRow()`** now properly validate column types ([BL-2247](http://https://ortussolutions.atlassian.net/browse/BL-2247 "BL-2247"))
* **`distinct(col)`** no longer confused with a function name in QoQ ([BL-2221](http://https://ortussolutions.atlassian.net/browse/BL-2221 "BL-2221"))
* **QoQ with ODBC Timestamp** format columns now compiles correctly ([BL-2144](http://https://ortussolutions.atlassian.net/browse/BL-2144 "BL-2144"))
* **Query column scope** no longer found in loops for assignment ([BL-2208](http://https://ortussolutions.atlassian.net/browse/BL-2208 "BL-2208")), fixing variable scoping edge cases  

  ### 🔤 String \& Type Improvements {#h3-11-string-type-improvements}

* **`quotedValueList()`** now correctly wraps values in single quotes per CFML spec ([BL-2185](http://https://ortussolutions.atlassian.net/browse/BL-2185 "BL-2185"))
* **`println()`** can now be called with no arguments to output an empty line --- no more `println( "" )` workaround ([BL-2200](http://https://ortussolutions.atlassian.net/browse/BL-2200 "BL-2200"))
* **`compareTo()`** **date member method** no longer incorrectly attaches to zero-valued BigDecimal ([BL-2166](http://https://ortussolutions.atlassian.net/browse/BL-2166 "BL-2166"))  

  ### 🌐 XML Handling {#h3-12-xml-handling}

* **Deleting a non-existent key** from `XMLAttribute` no longer throws an error ([BL-2231](http://https://ortussolutions.atlassian.net/browse/BL-2231 "BL-2231"))
* **`XMLChildren`** now updates correctly in all mutation cases ([BL-2240](http://https://ortussolutions.atlassian.net/browse/BL-2240 "BL-2240"))
* **WDDX** now properly escapes special characters in attribute values ([BL-2216](http://https://ortussolutions.atlassian.net/browse/BL-2216 "BL-2216"))  

  ### 🔐 Transaction \& Stored Procedures {#h3-13-transaction-stored-procedures}

* **Transaction** **`end`** **action** no longer throws an error when a stored procedure was executed within the transaction ([BL-2157](http://https://ortussolutions.atlassian.net/browse/BL-2157 "BL-2157"))
* **Transaction** **`action`** **attribute** is now case-insensitive ([BL-2238](http://https://ortussolutions.atlassian.net/browse/BL-2238 "BL-2238"))

📡 MiniServer Runtime Updates {#h2-14-miniserver-runtime-updates}
-----------------------------------------------------------------

### 📁 **`.boxlang.json`** Convention {#h3-15-boxlang-json-convention}

The MiniServer now automatically detects and loads a .boxlang.json file from the current working directory, merging it with the base BoxLang configuration (BL-2218):

<pre class="EnlighterJSRAW" data-enlighter-language="java"># Start the server — .boxlang.json is automatically picked up
$ boxlang server start
</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="java">// .boxlang.json — project-level configuration, committed to source control
{
    "enforceUDFTypeChecks": false,
    "defaultDatasource": "mydb"
}
</pre>

This makes project-level BoxLang configuration portable and self-contained --- ideal for containerized deployments and team environments.

### ⚙️ Undertow / Socket / WebSocket Options {#h3-16-undertow-socket-websocket-options}

You can now tune Undertow, socket, and WebSocket low-level options directly from `miniserver.json`:

<pre class="EnlighterJSRAW" data-enlighter-language="java">{
    "undertow": {
        "ioThreads": 8,
        "workerThreads": 64,
        "bufferSize": 16384
    },
    "socket": {
        "tcpNoDelay": true,
        "reuseAddress": true
    },
    "websocket": {
        "maxFrameSize": 65536,
        "maxTextMessageSize": 65536
    }
}
</pre>

### 📂 Logging Directory Output {#h3-17-logging-directory-output}

The MiniServer now logs the logging directory path during startup ([BL-1342](http://https://ortussolutions.atlassian.net/browse/BL-1342 "BL-1342")) --- a small but welcome quality-of-life improvement:

|           \[BoxLang\] MiniServer starting...           |
|--------------------------------------------------------|
| \[BoxLang\] Logging directory: /home/app/.boxlang/logs |
| \[BoxLang\] Server started on <http://localhost:8080>  |

### 🔄 Undertow Upgraded to 2.3.23.Final {#h3-18-undertow-upgraded-to-2-3-23-final}

The MiniServer now runs on **Undertow 2.3.23.Final**, bringing the latest HTTP server fixes and security patches.

🌐 Web Support Updates {#h2-19-web-support-updates}
---------------------------------------------------

### 🔀 Pre-Request Interception for Request Rerouting {#h3-20-pre-request-interception-for-request-rerouting}

A new interception point fires **before** `onRequestStart`, enabling interceptors to reroute requests before the application lifecycle begins ([BL-2164](http://https://ortussolutions.atlassian.net/browse/BL-2164 "BL-2164")). This unlocks powerful request gateway patterns:

* A/B routing and feature flags
* Maintenance mode bypasses
* Multi-tenant request routing
* Authentication redirects

All handled before any application overhead kicks in.

🛠️ Developer Experience {#h2-21-developer-experience}
------------------------------------------------------

### 🌳 Enhanced **`--bx-printast`** Tooling {#h3-22-enhanced-bx-printast-tooling}

The `--bx-printast` CLI flag now supports **file paths and standard input piping** ([BL-2187](http://https://ortussolutions.atlassian.net/browse/BL-2187 "BL-2187")), making it far more useful for debugging parser output and build tooling integration:

<pre class="EnlighterJSRAW" data-enlighter-language="java"># Print AST for a specific file
boxlang --bx-printast /path/to/MyClass.bx

# Pipe source code directly
echo 'result = 1 + 2' | boxlang --bx-printast

# Integrate with editors and build pipelines
cat MyComponent.bx | boxlang --bx-printast | jq '.body[0]'
</pre>

### 🧩 SOAP Client --- Binary and Map Type Support {#h3-23-soap-client-binary-and-map-type-support}

The SOAP client now supports binary data and map/struct types for both requests and responses. It also allows you to call service methods directly without going through `invoke()`:

<pre class="EnlighterJSRAW" data-enlighter-language="java">ws = soap( "http://example.com/DataService?wsdl" )

// Send binary data
result = ws.uploadDocument( {
    name : "report.pdf",
    data : fileReadBinary( "/reports/annual.pdf" )  // Binary now supported
} )

// Send map/struct data
result = ws.updateRecord( {
    id       : 123,
    metadata : { region : "US", tier : "premium" }  // Map/Struct now supported
} )
</pre>

### 🔧 Session Configuration in `boxlang.json` {#h3-24-session-configuration-in-boxlang-json}

Two previously missing session configuration settings are now supported ([BL-1859](http://https://ortussolutions.atlassian.net/browse/BL-1859 "BL-1859")):

<pre class="EnlighterJSRAW" data-enlighter-language="java">{
    "sessionManagement": true,
    "sessionCluster": false
}
</pre>

### 📋 Improved CLI Error Messages {#h3-25-improved-cli-error-messages}

CLI error messages now provide clearer context and actionable information when BoxLang scripts fail ([BL-2212](http://https://ortussolutions.atlassian.net/browse/BL-2212 "BL-2212")).

🐛 Notable Bug Fixes🐛 Notable Bug Fixes {#h2-26-notable-bug-fixes-notable-bug-fixes}
-------------------------------------------------------------------------------------

|                                      Ticket                                      |                                    Summary                                    |
|----------------------------------------------------------------------------------|-------------------------------------------------------------------------------|
| [BL-2203](http://https://ortussolutions.atlassian.net/browse/BL-2203 "BL-2203")  | Exclusive locks sometimes allowed multiple threads into the locked section    |
| [BL-2205](http://https://ortussolutions.atlassian.net/browse/BL-2205 "BL-2205 ") | cflock race condition under high concurrency                                  |
| [BL-2189](http://https://ortussolutions.atlassian.net/browse/BL-2189 "BL-2189")  | Can't cast 01-31-2026 23:59: 59 to a DateTime                                 |
| [BL-2143](http://https://ortussolutions.atlassian.net/browse/BL-2143 "BL-2143")  | DateTime Default ODBC Timestamp format was incorrectly quoted                 |
| [BL-2157](http://https://ortussolutions.atlassian.net/browse/BL-2157 "BL-2157")  | Transaction end threw error when a stored procedure was executed within       |
| [BL-2165](http://https://ortussolutions.atlassian.net/browse/BL-2165 "BL-2165 ") | getCurrentTemplatePath() didn't work inside a catch block                     |
| [BL-2196](http://https://ortussolutions.atlassian.net/browse/BL-2196 "BL-2196 ") | ENV secrets expand issue on Docker images due to \*_FILE greediness           |
| [BL-2206](http://https://ortussolutions.atlassian.net/browse/BL-2206 "BL-2206")  | Parser error with extra pound signs                                           |
| [BL-2217](http://https://ortussolutions.atlassian.net/browse/BL-2217 "BL-2217")  | Module public remote class requests did not fire Application lifecycle events |
| [BL-2236](http://https://ortussolutions.atlassian.net/browse/BL-2236 "BL-2236")  | form, url, and CGI scopes incorrectly scope-hunted during assignment          |
| [BL-2242](http://https://ortussolutions.atlassian.net/browse/BL-2242 "BL-2242")  | Null in switch statement threw error                                          |
| [BL-2251](http://https://ortussolutions.atlassian.net/browse/BL-2251 "BL-2251")  | Abstract class incorrectly required to implement all interface methods        |

🔧 Configuration Updates Summary {#h2-27-configuration-updates-summary}
-----------------------------------------------------------------------

|         Setting         |                               Description                               |
|-------------------------|-------------------------------------------------------------------------|
| `enforceUDFTypeChecks ` | New boolean in `runtime` to disable UDF argument/return type validation |
| `sessionManagement`     | Enable/disable session management globally in `boxlang.json`            |
| `sessionCluster`        | Enable distributed session clustering in `boxlang.json`                 |
| `.boxlang.json `        | MiniServer now auto-loads this file from the working directory          |

📦 Dependency Updates {#h2-28-dependency-updates}
-------------------------------------------------

* **Undertow** upgraded to `2.3.23.Final`
* **Gradle wrapper** updated to `9.3.1`
* **Jackson Jr** bumped to `2.21.1`
* **Logback Classic** bumped to `1.5.32`

🎯 Upgrading {#h2-29-upgrading}
-------------------------------

BoxLang 1.11.0 is a drop-in upgrade. No code changes are required to benefit from the performance improvements.

<pre class="EnlighterJSRAW" data-enlighter-language="java"># CommandBox
box install <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="d8bab7a0b4b9b6bf98e9f6e9e9f6e8">[email&nbsp;protected]</a>

# BVM
bvm install 1.11.0 &amp;&amp; bvm use 1.11.0

# Docker
FROM ortussolutions/boxlang:1.11.0
</pre>

Full release notes, documentation, and downloads are available at [boxlang.io](http://https://boxlang.io/?_gl=1*1i9icmx*_gcl_au*NDk3OTAwOTEuMTc2ODUxNjQ4Nw..*_ga*MTg5MDU4NDYzMS4xNzMyMDQwMzg2*_ga_663JFQ7YGX*czE3NzM0MTYwNzYkbzkyJGcxJHQxNzczNDE2MTAyJGozNCRsMCRoMA.. "boxlang.io") and [boxlang.ortusbooks.com](http://https://boxlang.ortusbooks.com/ "boxlang.ortusbooks.com").

Join the BoxLang Community ⚡️ {#h2-30-join-the-boxlang-community}
-----------------------------------------------------------------

Be part of the movement shaping the future of web development. Stay connected and receive the latest updates on **Into the Box 2025, product launches, tool updates, and more.**

**Subscribe to our newsletter** for exclusive content.

**Follow Us on Social media and don't miss any news and updates:**

* <https://x.com/ortussolutions>
* <https://www.facebook.com/OrtusSolutions>
* <https://www.linkedin.com/company/ortus-solutions-corp>
* <https://www.youtube.com/OrtusSolutions>
* <https://github.com/Ortus-Solutions>

Join the **BoxLang and CFML legends** at Into the Box 2025. Let's learn, share, and code together for a **modern, cutting-edge web development future.**
