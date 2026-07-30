---
title: "BoxLang Dynamic JVM Language Release 1.9.0"
slug: "boxlang-1-9-0-released-production-ready-stability-and-enterprise-grade-reliability"
date: "2026-01-12T11:16:19+00:00"
lastmod: "2026-01-12T11:16:46+00:00"
description: "BoxLang continues to expand as more customers migrate to it’s dynamic JVM platform."
authors:
  - "luis-majano"
image: "https://foojay.io/wp-content/uploads/2026/01/IMG_0067.jpeg"
categories:
  - "BoxLang"
  - "Java"
  - "Library"
  - "Release Notes"
tags:
related_posts:
enlighterjs: true
frozen: false
---

**Houston, Texas -- January 2025** -- Ortus Solutions, a leading innovator in professional open-source development, today announced the release of BoxLang 1.9.0, a major stability and compatibility release focused on production-readiness. This release resolves over 50 critical bugs and introduces significant enhancements to datasource management, context lifecycle handling, and web form processing for mission-critical enterprise applications.

Production-Ready Enhancements {#h2-0-production-ready-enhancements}
-------------------------------------------------------------------

BoxLang 1.9.0 delivers critical improvements based on real-world client migrations and production deployments:

* **Elimination of Connection Leaks**: Complete datasource lifecycle management eliminates connection pool exhaustion across application restarts
* **Memory Leak Prevention**: Enhanced context cleanup with proper thread tracking and resource disposal
* **Modern Web Standards**: Automatic array-based form field parsing using contemporary naming conventions
* **Enterprise Database Support**: Comprehensive Oracle, MySQL, and PostgreSQL improvements including named parameters, ref cursors, and query-of-queries enhancements
* **Fluent SOAP Integration**: Enhanced SOAP client with automatic WSDL discovery and intelligent type conversion

Key Features and Code Examples {#h2-1-key-features-and-code-examples}
---------------------------------------------------------------------

### Array-Based Form Field Parsing {#h3-2-array-based-form-field-parsing}

BoxLang now automatically parses query parameters and form fields as arrays using modern naming conventions:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// HTML form with multiple selections
&lt;form method="POST"&gt;
    &lt;input type="checkbox" name="colors[]" value="red" /&gt;
    &lt;input type="checkbox" name="colors[]" value="blue" /&gt;
    &lt;input type="checkbox" name="colors[]" value="green" /&gt;
    &lt;button type="submit"&gt;Submit&lt;/button&gt;
&lt;/form&gt;</pre>

Then it can read them when submitted:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// BoxLang automatically parses as array
selectedColors = form.colors;
// Result: ["red", "blue", "green"]

// Works with query parameters too
// URL: /page?tags[]=boxlang&amp;tags[]=java&amp;tags[]=modern
tags = url.tags;
// Result: ["boxlang", "java", "modern"]</pre>

### Datasource Lifecycle Management {#h3-3-datasource-lifecycle-management}

Critical fixes prevent connection pool leaks and resource exhaustion:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">class {
    this.name = "MyApp";

    this.datasources = {
        "mydb" : {
            driver : "mysql",
            host : "localhost",
            database : "appdb",
            username : "user",
            password : "pass"
        }
    };

    function onApplicationEnd() {
        // Datasources properly shutdown automatically
        // No more connection pool leaks!
        writeLog( "Datasources cleaned up automatically" );
    }
}</pre>

### Oracle Database Improvements {#h3-4-oracle-database-improvements}

Named parameters and ref cursors now work correctly:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// Named parameters work correctly
queryExecute(
    "SELECT * FROM users WHERE id = :userId AND status = :status",
    {
        userId : { value : 123, type : "integer" },
        status : { value : "active", type : "varchar" }
    },
    { datasource : "oracle_ds" }
);

// Ref cursor parameters work regardless of position
bx:storedproc procedure="getUserData" datasource="oracle_ds" {
    bx:procparam type="in" value=123 type="integer";
    bx:procparam type="out" variable="result" type="refcursor";
}</pre>

### Enhanced SOAP Client with Fluent API {#h3-5-enhanced-soap-client-with-fluent-api}

BoxLang 1.9.0 includes major enhancements to the SOAP client introduced in 1.8.0, now with full class capabilities and improved reliability:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">// Create SOAP client with fluent configuration
ws = soap( "http://api.example.com/service.wsdl" )
    .timeout( 60 )
    .withBasicAuth( "apiuser", "apipass" )
    .header( "X-API-Key", "your-key" );

// Invoke operations with automatic type conversion
result = ws.invoke( "GetCustomer", { customerId: 123 } );

// SOAP client now has full class structure
ws.setTimeout( 30 );
ws.setHeader( "Authorization", "Bearer token123" );

// Inspect available operations programmatically
operations = ws.getOperationNames();
if ( ws.hasOperation( "ProcessOrder" ) ) {
    order = ws.invoke( "ProcessOrder", orderData );
}</pre>

**Key SOAP Improvements:**

* **Automatic WSDL Discovery**: Parses operations, parameters, and types automatically
* **Intelligent Type Conversion**: SOAP XML types automatically become BoxLang types
* **Full Class Capabilities**: Access underlying HTTP methods and configuration
* **Operation Inspection**: Programmatically discover and validate operations
* **Enhanced Reliability**: Improved error handling and connection management

Technical Specifications {#h2-6-technical-specifications}
---------------------------------------------------------

**50+ Critical Bug Fixes** including:

* 15+ CFML compatibility fixes for customers migrating from CFML to Boxlang (ListDeleteAt, ListAppend, Boolean strings, Session IDs)
* Enhanced file operations and upload handling
* Date/time parsing improvements with speed improvements of over 60% of previous releases
* Session and cookie management fixes
* Core runtime optimizations
* Database and ORM enhancements
* Memory and threading improvements
* Number handling and JSON serialization enhancements

The complete engineering release notes are available at:

Availability and Licensing {#h2-7-availability-and-licensing}
-------------------------------------------------------------

BoxLang 1.9.0 is available immediately for download at with installation guides at

BoxLang operates under a transparent three-tier licensing model:

* **BoxLang (Apache 2.0)**: Free, open-source core
* **BoxLang+**: Commercial features with support
* **BoxLang++**: Enterprise features and support

All tiers feature straightforward pricing with no hidden fees or complex calculation models.

About BoxLang {#h2-8-about-boxlang}
-----------------------------------

BoxLang is a modern, dynamic, multi-runtime JVM language and productivity framework. With it's multi-parser architecture it can be also used to run CFML applications. Backed by Ortus Solutions' 20 years of professional open-source expertise, BoxLang delivers enterprise-grade reliability for mission-critical applications.

About Ortus Solutions {#h2-9-about-ortus-solutions}
---------------------------------------------------

Ortus Solutions is a leading provider of professional open-source software with two decades of experience in enterprise application development. The company specializes in JVM languages, web frameworks, and modern development tooling for Fortune 500 companies and government agencies.

<https://www.boxlang.io>  
<https://www.ortussolutions.com>

Learn More:

* Website: <https://boxlang.io>
* Documentation: <https://boxlang.ortusbooks.com>
* Try Online: <https://try.boxlang.io>
* Subscription Plans: <https://boxlang.io/plans>
