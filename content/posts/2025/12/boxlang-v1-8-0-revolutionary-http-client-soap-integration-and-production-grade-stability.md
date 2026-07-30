---
title: "BoxLang v1.8.0 : Revolutionary HTTP Client, SOAP Integration, and Production-Grade Stability"
slug: "boxlang-v1-8-0-revolutionary-http-client-soap-integration-and-production-grade-stability"
date: "2025-12-09T11:04:07+00:00"
lastmod: "2025-12-10T10:24:43+00:00"
description: "The BoxLang team is excited to announce BoxLang 1.8.0, a massive release that revolutionizes HTTP capabilities, introduces comprehensive SOAP/WSDL - by Cristobal Escobar"
authors:
  - "cristobal-escobar"
image: "/images/posts/2025/12/boxlang-v1-8-0-revolutionary-http-client-soap-integration-and-production-grade-stability/BoxLang-Logo-Dark.png"
categories:
  - "Uncategorized"
tags:
related_posts:
enlighterjs: true
frozen: false
---

![](/images/posts/2025/12/boxlang-v1-8-0-revolutionary-http-client-soap-integration-and-production-grade-stability/boxlang-v1.8.0-700x467.jpg)

The BoxLang team is excited to announce BoxLang 1.8.0, a massive release that revolutionizes HTTP capabilities, introduces comprehensive SOAP/WSDL integration, and delivers over 100 critical bug fixes for production-grade stability. This release focuses on modern web application development with fluent APIs, streaming support, persistent connection management, and extensive CFML compatibility improvements.

🚀 What's New in 1.8.0 {#h2-0-what-s-new-in-1-8-0}
--------------------------------------------------

### 🎯 Modular Compiler Architecture \& Ultra-Slim Runtime {#h3-1-modular-compiler-architecture-ultra-slim-runtime}

**BoxLang 1.8.0 introduces a revolutionary modular compiler architecture that delivers unprecedented flexibility, security, and deployment efficiency for enterprise applications.**

##### 🪶 Mega-Slim 7MB Runtime

The BoxLang runtime has been dramatically optimized, dropping from over 9MB to just 7MB by removing the JavaParser compiler dependencies. This lean footprint provides:

* **22% Smaller** runtime for faster downloads and deployments
* **Reduced memory footprint** for containerized environments
* **Faster startup times** due to smaller classpath
* **Improved security surface** with fewer dependencies

##### 🔐 Two Deployment Flavors for Enterprise Security

BoxLang now ships in two distinct flavors to meet different security and deployment requirements:

### 1. `boxlang` - Full Development Runtime {#h3-2-1-boxlang-full-development-runtime}

* Includes **NoOp** (No Operation) compiler for pre-compiled class execution
* Includes **ASM** compiler for runtime compilation and hot-reloading
* Perfect for development, testing, and dynamic environments
* Enables live code changes and interactive development
* Default choice for most applications

### 2. `boxlang-noop` - Secure Production Runtime {#h3-3-2-boxlang-noop-secure-production-runtime}

* **NoOp compiler only** - no runtime compilation capabilities
* **100% pre-compiled code execution** - zero dynamic compilation
* **Maximum security** - eliminates runtime code injection vectors
* **Compliance-ready** for regulated industries (finance, healthcare, government)
* **Reduced attack surface** - no compiler means no compilation exploits
* **Immutable deployments** - code cannot be modified at runtime
* Perfect for production environments requiring security certifications

<pre class="EnlighterJSRAW" data-enlighter-language="java"># Development/Standard deployment
java -jar boxlang-1.8.0.jar myapp.bx

# Secure production deployment (pre-compiled only)
java -jar boxlang-noop-1.8.0.jar myapp.bx</pre>

*Enterprise Security Win*: Deploy with boxlang-noop in production to guarantee no runtime code compilation, meeting strict security policies for PCI-DSS, HIPAA, SOC 2, and government compliance requirements.

##### 🔌 Plug-and-Play Compiler Modules

Compilers are now **modular add-ons** that can be loaded dynamically via classpath. BoxLang includes two compiler modules:

### 1. `bx-compiler-asm` - ASM Bytecode Compiler (Recommended) {#h3-4-1-bx-compiler-asm-asm-bytecode-compiler-recommended}

* Direct bytecode generation using ASM library
* **Superior performance** - skips Java source generation step
* Modern JVM feature support (virtual threads, pattern matching, etc.)
* Optimized bytecode output
* Default compiler for production applications

### 2. bx-compiler-java - Java Source Compiler {#h3-5-2-bx-compiler-java-java-source-compiler}

* Generates Java source code, then compiles to bytecode
* Legacy compatibility for debugging and inspection
* Useful for understanding compilation process
* Primarily for backward compatibility

<pre class="EnlighterJSRAW" data-enlighter-language="java">// boxlang.json - Choose your compiler
{
  "compiler": "asm",  // Use ASM compiler (default)
  // OR
  "compiler": "java"  // Use Java source compiler
}</pre>

##### 🚀 Revolutionary IBoxpiler Interface

The new IBoxpiler interface enables true plug-and-play compiler development:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public interface IBoxpiler {
    // Compile BoxLang source to bytecode
    byte[] compile( SourceCode source );

    // Get compiler metadata
    String getName();
    String getVersion();
}</pre>

**What This Means:**

* 🔧 **Custom Compilers** - Build specialized compilers for your needs
* 🎯 **Domain-Specific Optimization** - Create industry-specific compilation strategies
* 🔒 **Security Compilers** - Implement compliance-specific compilation rules
* ⚡ **Performance Compilers** - Optimize for specific deployment targets
* 🌐 **Alternative Targets** - Compile BoxLang to JavaScript, WASM, native code, etc.
* 🔬 **Research \& Innovation** - Experiment with new compilation techniques  
  **Example Use Cases:**

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Financial services: Compiler with embedded audit logging
compiler = new AuditCompiler()
    .enableTracing()
    .logToCompliance( "audit.log" );

// IoT: Compiler optimized for embedded devices
compiler = new EmbeddedCompiler()
    .optimizeForMemory()
    .targetArch( "ARM64" );

// Blockchain: Compiler with cryptographic verification
compiler = new VerifiableCompiler()
    .signOutput()
    .enableProofOfCompilation();</pre>

##### 💼 Enterprise Benefits

**Security \& Compliance:**

* ✅ Deploy `boxlang-noop` for **zero-runtime-compilation** security posture
* ✅ Meet **PCI-DSS, HIPAA, SOC 2** requirements with immutable runtimes
* ✅ Pass security audits with **no dynamic code execution** capabilities
* ✅ Eliminate entire classes of **code injection vulnerabilities**

**Performance \& Efficiency:**

* ⚡ **7MB runtime** for lightning-fast container deployments
* 🚀 **Faster startup times** in serverless and microservices
* 💰 **Lower cloud costs** with smaller images and faster scaling
* 🎯 **Optimized memory usage** for high-density deployments

**Flexibility \& Innovation:**

* 🔌 **Plug-and-play compilers** via `IBoxpiler` interface
* 🛠️ **Custom compilation strategies** for specialized requirements
* 🌐 **Future-proof architecture** supporting new compilation targets
* 🔬 **Research-friendly** for academic and innovation projects

**Deployment Scenarios:**

|          Scenario          |     Recommended Runtime     |         Compiler         |
|----------------------------|-----------------------------|--------------------------|
| Development                | `boxlang`                   | ASM (hot-reload enabled) |
| CI/CD Testing              | `boxlang`                   | ASM                      |
| Staging                    | `boxlang` or `boxlang-noop` | ASM                      |
| Production (Standard)      | `boxlang`                   | ASM                      |
| Production (High Security) | `boxlang-noop`              | None (pre-compiled only) |
| Regulated Industries       | `boxlang-noop`              | None (pre-compiled only) |
| Government/Military        | `boxlang-noop`              | None (pre-compiled only) |
| Containerized Apps         | `boxlang-noop`              | None (smaller images)    |

**Migration Path:** Existing applications continue to work seamlessly. Simply choose boxlang for standard deployments or upgrade to boxlang-noop when security requirements demand pre-compiled-only execution.

🌐 Revolutionary HTTP Client - Modern, Fluent, and Powerful {#h2-6-revolutionary-http-client-modern-fluent-and-powerful}
------------------------------------------------------------------------------------------------------------------------

BoxLang 1.8.0 completely rewrites the HTTP subsystem from the ground up, delivering a modern, fluent HTTP client with streaming support, connection pooling, and advanced features for building robust web applications.

New `http()` BIF - Chainable HTTP Magic  

The new `http()` BIF provides an elegant, chainable interface for building and executing HTTP requests:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Simple GET request - clean and direct
result = http( "https://api.example.com/data" ).send();
println( "Status: #result.statusCode#" );

// POST with JSON body
result = http( "https://api.example.com/users" )
    .post()
    .header( "Content-Type", "application/json" )
    .body( { name : "John Doe", email : "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="e3898c8b8da3869b828e938f86cd808c8e">[email&nbsp;protected]</a>" } )
    .send();

// Transform response inline
users = http( "https://api.example.com/users" )
    .get()
    .transform( ( result ) =&gt; deserializeJSON( result.fileContent ) )
    .send(); // Returns deserialized array directly!

// Stream large responses with chunking
http( "https://api.example.com/large-data" )
    .get()
    .onChunk( ( chunk ) =&gt; {
        println( "Received: #chunk.data.len()# bytes" );
    } )
    .send();

// Async execution with BoxFuture
future = http( "https://api.example.com/data" )
    .get()
    .sendAsync();</pre>

**Key Features:**

* **Fluent API** - Readable, chainable methods for request building
* **HTTP/2 Support** - Modern HTTP/2 by default with HTTP/1.1 fallback
* **Streaming** - Chunk-based streaming for large responses and SSE
* **Connection Pooling** - Automatic connection reuse and management
* **Response Transformation** - Apply custom transformations before returning
* **Async Execution** - Non-blocking requests with BoxFuture support
* **Client Certificates** - SSL/TLS client certificate authentication
* **Proxy Support** - HTTP/HTTPS proxy with authentication
* **Rich Callbacks** - onChunk, onError, onComplete, onRequestStart

Completely Rewritten `bx:http` Component  

The `bx:http` component has been rebuilt to match the fluent BIF capabilities while maintaining full CFML compatibility:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Server-Sent Events (SSE) support
&lt;bx:http
    url="https://api.example.com/events"
    sse="true"
    onMessage="#( event ) =&gt; handleSSEEvent( event )#" /&gt;

// Client certificate authentication
&lt;bx:http
    url="https://secure-api.com/data"
    clientCert="/path/to/cert.p12"
    clientCertPassword="secret"
    result="apiData" /&gt;

// Streaming with callbacks
&lt;bx:http
    url="https://api.example.com/stream"
    onChunk="#( chunk ) =&gt; processChunk( chunk )#"
    onError="#( error ) =&gt; logError( error )#"
    onComplete="#() =&gt; println( 'Complete!' )#" /&gt;</pre>

**New Features:**

* **Streaming Callbacks** - onChunk, onMessage, onError, onComplete, onRequestStart
* **Native SSE Support** - Built-in Server-Sent Events handling
* **Full HTTP/2** - httpVersion attribute for protocol control
* **Connection Management** - Persistent connections and pooling
* **Better Error Handling** - throwOnError for automatic exception throwing

HTTP Service - Enterprise-Grade Connection Management  

A new `HttpService` manages HTTP client instances, connection pooling, and lifecycle automatically:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Clients are automatically managed and reused
client1 = http( "https://api.example.com" );
client2 = http( "https://api.example.com" ); // Reuses same connection pool

// Access HTTP statistics
stats = getBoxRuntime().getHttpService().getStats();
println( "Total requests: #stats.totalRequests#" );
println( "Active connections: #stats.activeConnections#" );</pre>

🧼 SOAP/WSDL Client Integration - Web Services Made Simple {#h2-7-soap-wsdl-client-integration-web-services-made-simple}
------------------------------------------------------------------------------------------------------------------------

BoxLang now includes comprehensive SOAP web service support with automatic WSDL parsing and fluent method invocation:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Create SOAP client from WSDL
ws = soap( "http://example.com/service.wsdl" )
    .timeout( 60 )
    .withBasicAuth( "username", "password" );

// Invoke methods directly (discovered from WSDL)
result = ws.getUserInfo( userID : 123 );
println( "User: #result.name#" );

// Alternative: traditional createObject() syntax
ws = createObject( "webservice", "http://example.com/service.wsdl" );
result = ws.getUserInfo( userID : 123 );

// Use invoke() function for dynamic calls
result = invoke( ws, "getUserInfo", { userID : 123 } );

// Inspect available operations
operations = ws.getOperationNames();
opInfo = ws.getOperationInfo( "getUserInfo" );</pre>

**Features:**

* **Automatic WSDL Parsing** - Discovers methods, parameters, and types
* **SOAP 1.1 \& 1.2** - Auto-detects version from WSDL
* **Fluent Method Calls** - Invoke methods directly on client object
* **Response Unwrapping** - Automatically unwraps single-property responses
* **Document/Literal Wrapped** - Full support for document/literal wrapped style
* **Type Mapping** - Automatic BoxLang type conversion
* **Compatible** - Works with invoke() BIF and bx:invoke component

Perfect for integrating with legacy SOAP services, enterprise APIs, and third-party web services!

🎯 Context Shutdown Listeners - Graceful Application Cleanup {#h2-8-context-shutdown-listeners-graceful-application-cleanup}
----------------------------------------------------------------------------------------------------------------------------

New lifecycle hooks enable graceful application shutdown with resource cleanup:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// In Application.bx
component {
    this.name = "MyApp";

    function onApplicationStart() {
        application.resources = setupResources();

        // Register shutdown listener
        getBoxContext().registerShutdownListener( () =&gt; {
            application.resources.close();
            println( "Application shutdown complete" );
        } );
    }
}</pre>

**Use Cases:**

* Database connection cleanup
* Cache flushing
* File handle closing
* External service disconnection
* Logging final state

### 📚 Enhanced Metadata \& Reflection {#h3-9-enhanced-metadata-reflection}

Class metadata now includes `simpleName` for easier reflection:

<pre class="EnlighterJSRAW" data-enlighter-language="java">meta = getMetadata( myObject );
println( "Class: #meta.fullName#" );
println( "Simple name: #meta.simpleName#" ); // New in 1.8.0</pre>

🤖 Core Runtime Enhancements {#h2-10-core-runtime-enhancements}
---------------------------------------------------------------

#### MiniServer JSON Configuration

The BoxLang MiniServer now supports loading configuration from JSON files:

<pre class="EnlighterJSRAW" data-enlighter-language="java"># Looks for miniserver.json in current directory
boxlang-miniserver

# Or specify path explicitly
boxlang-miniserver /path/to/config.json

# CLI arguments override JSON configuration
boxlang-miniserver miniserver.json --port 9090 --debug</pre>

**Example Configuration:**

<pre class="EnlighterJSRAW" data-enlighter-language="java">{
    "port" : 8080,
    "host" : "0.0.0.0",
    "webRoot" : "/var/www/myapp",
    "debug" : false,
    "rewrites" : true,
    "rewriteFileName" : "index.bxm",
    "healthCheck" : true,
    "envFile" : "/etc/boxlang/.env.production"
}</pre>

**Configuration Priority:**

* Default values
* Environment variables (`BOXLANG_*`)
* JSON configuration
* Command-line arguments (highest priority)

### Compiler Configuration Refactored {#h3-11-compiler-configuration-refactored}

The experimental compiler setting is now a top-level directive in `boxlang.json:`

<pre class="EnlighterJSRAW" data-enlighter-language="java">{
    "compiler" : "asm",
    "runtime" : {
        // runtime settings
    }
}</pre>

Valid values: `"asm"` (default), `"java"`, `"noop"`

### Dynamic Class Loading {#h3-12-dynamic-class-loading}

The `DynamicClassLoader` now supports `addPaths()` for loading external JARs at runtime:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Load external JARs dynamically
getRequestClassLoader().addPaths( [
    "/path/to/library.jar",
    "/path/to/another.jar"
] );

// Now load classes from those JARs
MyClass = createObject( "java", "com.example.MyClass", getRequestClassLoader() );</pre>

### Performance Optimizations {#h3-13-performance-optimizations}

* **Metadata Creation** - Micro-optimizations for faster class metadata generation
* **Application Timeout Checks** - Background thread reduces main thread overhead
* **Thread Joining** - Faster `bx:thread` join operations
* **DateTimeFormatter Usage** - Optimized for better performance

### Query Improvements {#h3-14-query-improvements}

* **Text Type Support** - Query columns support text type (maps to VARCHAR)
* **BLOB/CLOB Handling** - Proper support for binary large objects
* **Column Type Tracking** - Original JDBC types preserved in metadata
* **Oracle Enhancements:**
  * VARCHAR params match CHAR fields correctly
  * Generated keys support (ROWID)
  * Stored procedure ref cursor support

🐛 100+ Critical Bug Fixes {#h2-15-100-critical-bug-fixes}
----------------------------------------------------------

### Date \& Time (30+ fixes) {#h3-16-date-time-30-fixes}

* **Timezone Handling** - this.timezone now properly respected
* **Date Parsing** - Fixed parsing with various masks:
  * `"Nov-05-2025 8:43am"`
  * `"Jun-30-2010 04:33"`
  * `"11/21/2025 1:05"`
  * `"Jul 17, 2017 9:29: 40 PM"`
* `dateAdd()` - No longer mutates date when adding 0 weeks
* `isDate()` - Fixed edge cases (isDate(0) now returns false)
* **Date Casting** - Resolved issues with JVM locales de_DE and en_DE
* **Null Date Handling** - Added handling in formatting methods
* `.format()` Member - Undocumented but now properly supported

### Query Operations (15+ fixes) {#h3-17-query-operations-15-fixes}

* `queryFilter()` - Fixed returning unexpected rows
* `querySlice()` - Fixed offset handling
* `structFindKey()` - Returns proper references (not copies)
* `structFindValue()` - Fixed crashes with array keys
* **Nested Queries** - Respect JSONSerialize query options
* **Connection Pooling** - Fixed credentials override, proper pooling
* **SQLite** - Database errors properly handled

### List Operations (5+ fixes) {#h3-18-list-operations-5-fixes}

* `listDeleteAt()` - Fixed incorrect behavior
* **List Find BIFs** - Fixed unexpected matches
* **Null Appending** - Fixed failures appending null to list

### Number Formatting (5+ fixes) {#h3-19-number-formatting-5-fixes}

* `numberFormat()` - Fixed `"_.00"` mask compatibility
* `isNumeric()` - `isNumeric("true")` returns false (matches ACF/Lucee)
* `val()` - Fixed occasional scientific notation
* `lsIsCurrency()` - Fixed type casting issues

### HTTP \& Network (10+ fixes) {#h3-20-http-network-10-fixes}

* **HTTP Component** - Fixed basic auth regression
* **Duplicate Headers** - Properly handles duplicate response headers
* **Cookie Component** - Fixed expires crashes with createTimeSpan
* **Header Component** - Adds headers instead of replacing (correct CFML behavior)
* **Compression** - Fixed gzip decompression issues

### File Operations (5+ fixes) {#h3-21-file-operations-5-fixes}

* `fileCopy()` - Respects overwrite parameter
* **File Upload** - Files smaller than 10KB stored properly on disk
* `getFileInfo()` - Returns correct type string for directories
* `expandPath()` - Fixed wrong base path with Application.bx in different directory
* `getTempDirectory()` - Contains trailing slash (CFML compat)

### Parser Improvements (10+ fixes) {#h3-22-parser-improvements-10-fixes}

* **Semicolons** - After empty blocks properly end statements
* **Whitespace** - Allowed in tag closing (`/ >`)
* **CF Template CFCs** - Fixed parsing template-style components
* **Ternary Operator** - Assignment inside ternary parses correctly
* `continue` in `switch` - Can use inside `for` loop
* **Static Invocation** - Fixed parse error on static method calls

### CFML Compatibility (15+ fixes) {#h3-23-cfml-compatibility-15-fixes}

* **CGI Values** - Now java.lang.String for compatibility
* **Boolean Comparison** - Don't compare numbers to booleans
* `isValid()` - Fixed null reference errors
* `isDefined()` - Fixed regression on struct keys
* `reMatchNoCase()` - Result is proper array
* **Array Comparison** - Fixed "Can't compare \[String\] against \[Array\]"
* `structSort()` - Fixed unmodifiable arrays from `keyArray()`
* **Custom Tags** - `thisTag` scope behaves like ACF/Lucee
* **Line Numbers** - Correct line numbers in tag context

### Java Interop (5+ fixes) {#h3-24-java-interop-5-fixes}

* **Field Access** - Fixed accessing public parent package/private fields
* **Field Setting** - Values properly coerced when setting
* `putAll()` - Fixed ClassCastException with Map argument
* **Unmodifiable Arrays** - Can now map/filter unmodifiable arrays
* **Native Array Printing** - `println()` handles Java arrays better

### Additional Fixes {#h3-25-additional-fixes}

* **XML Operations** - `xmlSearch()` returns correct xpath values
* **Async Operations** - `asyncRun()` properly detects BoxExecutors
* **Session Scope** - Fixed distributed cache persistence
* **Application Lifecycle** - Cannot shutdown mid-request
* **Stored Procedures** - Null attribute respected, multiple result sets
* **Exception Handling** - Custom tags in catch blocks work correctly

🎉 New BoxLang+ Modules for Subscribers {#h2-26-new-boxlang-modules-for-subscribers}
------------------------------------------------------------------------------------

Exciting news for **BoxLang+ subscribers**! Several powerful premium modules have been released:

* **bx-redis** - Distributed caching and pub/sub messaging
* **bx-csv** - Advanced CSV data processing
* **bx-couchbase** - Distributed caching, locking, and AI memory
* **bx-ldap** - Enterprise directory services integration
* **bx-spreadsheet** - Excel operations and manipulation
* **bx-pdf** - Professional PDF document generation
* **bx-rss** - RSS/Atom feed reading and creation

**Coming Soon:**

* **bx-mongodb** - MongoDB document database
* **bx-elasticsearch** - Full-text search and analytics

Each module follows BoxLang's fluent API patterns and integrates seamlessly with the core runtime.

[View BoxLang+ Plans](https://boxlang.io/plans#open-source "View BoxLang+ Plans")

🛠️ Developer Experience {#h2-27-developer-experience}
------------------------------------------------------

### Feature Audit Tool {#h3-28-feature-audit-tool}

* Enhanced to find REST classes and REST API usage
* Better reporting of missing modules for migrations
* Improved analysis of BoxLang feature usage

### Web Support Documentation {#h3-29-web-support-documentation}

All web-related BIFs and components include proper descriptions via @BoxBIF and @BoxComponent annotations for better IDE integration.

### JavaParser Dependencies {#h3-30-javaparser-dependencies}

Removed all JavaParser dependencies externally unless explicitly checked - reduces runtime footprint and startup time.

⚡ Migration Guide {#h2-31-migration-guide}
------------------------------------------

### Breaking Changes {#h3-32-breaking-changes}

**None** - This release maintains full backward compatibility with 1.7.x

### Recommended Actions {#h3-33-recommended-actions}

**HTTP Migration**: Consider migrating to the new fluent HTTP APIs:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Old style (component)
&lt;bx:http url="https://api.example.com" method="GET" result="myResult" /&gt;

// New style (fluent BIF)
myResult = http( "https://api.example.com" ).send();

// With transformation
data = http( "https://api.example.com/users.json" )
    .get()
    .transform( ( result ) =&gt; deserializeJSON( result.fileContent ) )
    .send();</pre>

**SOAP Integration** : Use the new `soap()` BIF for web services:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// New soap() BIF (recommended)
ws = soap( "http://example.com/service.wsdl" )
    .timeout( 60 )
    .withBasicAuth( "user", "pass" );

// Or traditional createObject
ws = createObject( "webservice", "http://example.com/service.wsdl" );

// Invoke methods
result = ws.methodName( arg1, arg2 );</pre>

**Configuration** : Update `boxlang.json` to use the new top-level compiler directive:

<pre class="EnlighterJSRAW" data-enlighter-language="java">{
    "compiler" : "asm",
    "runtime" : {
        // settings
    }
}</pre>

### 🙏 Thank You {#h3-34-thank-you}

A huge thank you to our community for your continued feedback, bug reports, and contributions. This release includes fixes from real-world production usage, and your input directly drives BoxLang forward!

Special thanks to all contributors who reported issues, tested pre-releases, and provided valuable feedback throughout the development cycle.

### 📚 Resources {#h3-35-resources}

* [Complete Release Notes](https://boxlang.ortusbooks.com/changelog/v1.8.0 "Complete Release Notes")
* [BoxLang Documentation](https://boxlang.ortusbooks.com "BoxLang Documentation")
* [HTTP Documentation](https://boxlang.ortusbooks.com/boxlang-language/reference/built-in-functions/http "HTTP Documentation")
* [SOAP Documentation](https://boxlang.ortusbooks.com/boxlang-language/reference/built-in-functions/soap "SOAP Documentation")
* [BoxLang+ Subscription Plans](https://boxlang.io/plans#open-source "BoxLang+ Subscription Plans")
* [GitHub Repository](https://github.com/ortus-boxlang/boxlang "GitHub Repository")

Ready to experience revolutionary HTTP capabilities, SOAP integration, and rock-solid stability?  
**Upgrade to BoxLang 1.8.0 today!**

<pre class="EnlighterJSRAW" data-enlighter-language="java"># Update with CommandBox
box install <a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="680a07100409060f285946504658">[email&nbsp;protected]</a>

# Or download from boxlang.io
https://boxlang.io/download</pre>

### 🙏 Thank You {#h3-36-thank-you}

A huge thank you to our community for your continued feedback, bug reports, and contributions. Your input drives BoxLang forward!

### 📚 Resources {#h3-37-resources}

* [Complete Release Notes](https://boxlang.ortusbooks.com/changelog/v1.7.0 "Complete Release Notes")
* [BoxLang Documentation](https://boxlang.ortusbooks.com "BoxLang Documentation")
* [BoxLang+ Subscription Plans](https://boxlang.io/plans#open-source "BoxLang+ Subscription Plans")
* [GitHub Repository](https://github.com/ortus-boxlang/boxlang "GitHub Repository")

Ready to experience real-time streaming, distributed caching, and blazing performance?  
**Upgrade to BoxLang 1.7.0 today!**

Download {#h2-38-download}
--------------------------

Please visit our [download](https://www.boxlang.io/download "download") page or our quick [installation guides](https://boxlang.ortusbooks.com/getting-started/installation "installation guides") to upgrade your installation.

#### Professional Open Source

BoxLang is a professional open-source product, with three different licences:

* Open-Source Apache2
* BoxLang +
* BoxLang ++

**BoxLang is free** , open-source software under the Apache 2.0 license. We encourage and support community contributions. **BoxLang+ and BoxLang ++** are commercial versions offering support and enterprise features. Our licensing model is based on fairness and the golden rule: Do to others as you want them to do to you. No hidden pricing or pricing on cores, RAM, SaaS, multi-domain or ridiculous ways to get your money. Transparent and fair.

[BoxLang Subscription Plans](https://boxlang.io/plans)
> BoxLang is more than just a language; it's a movement.

Join us and redefine development on the JVM **Ready to learn more**? Explore BoxLang's Features, Documentation, and Community.

[Learn More](https://boxlang.io/)

[Try BoxLang](https://try.boxlang.io)

Join the BoxLang Community ⚡️ {#h2-39-join-the-boxlang-community}
-----------------------------------------------------------------

Be part of the movement shaping the future of web development. Stay connected and receive the latest updates on **Into the Box 2025, product launches, tool updates, and more.**

**Subscribe to our newsletter** for exclusive content.

**Follow Us on Social media and don't miss any news and updates:**

* [https://x.com/ortussolutions](https://x.com/ortussolutions "https://x.com/ortussolutions")
* [https://www.facebook.com/OrtusSolutions](https://www.facebook.com/OrtusSolutions "https://www.facebook.com/OrtusSolutions")
* [https://www.linkedin.com/company/ortus-solutions-corp](https://www.linkedin.com/company/ortus-solutions-corp "https://www.linkedin.com/company/ortus-solutions-corp")
* [https://www.youtube.com/OrtusSolutions](https://www.youtube.com/OrtusSolutions "https://www.youtube.com/OrtusSolutions")
* [https://github.com/Ortus-Solutions](https://github.com/Ortus-Solutions "https://github.com/Ortus-Solutions")

Join the **BoxLang and CFML legends** at Into the Box 2025. Let's learn, share, and code together for a **modern, cutting-edge web development future.**
