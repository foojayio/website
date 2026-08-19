---
title: "BoxLang 1.14.0 : Sets, Ranges, Inner Classes, and a Runtime That Talks Back"
date: "2026-07-09T18:02:59+00:00"
lastmod: "2026-07-10T13:46:44+00:00"
description: "BoxLang has never stood still, but 1.14.0 is something different. This is the release where the language stops filling gaps and starts defining what a…"
authors:
  - "cristobal-escobar"
image: "BoxLang-release-1.14.0-1701-x-1701-px-4-1024x1024.png"
categories:
  - "BoxLang"
  - "Developer Tools"
  - "Java"
  - "Release Notes"
related_posts:
  - "boxlang-1-14-0-boxset-is-here-boxlangs-new-first-class-set-type"
  - "boxlang-1-14-0-introducing-inner-classes"
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
  - "boxlang-1-14-0-navigate-anything-jsonpath-comes-to-boxlangs-datanavigator"
frozen: false
---

![](BoxLang-release-1.14.0-4-700x394.png)

BoxLang has never stood still, but 1.14.0 is something different. This is the release where the language stops filling gaps and starts defining what a modern dynamic JVM language looks like on its own terms. Sixty-five issues closed. Four innovative language features. A formatter that has grown up. And a companion module - `bx-mcp` - that fundamentally changes how you operate a running BoxLang application with AI.

This could have easily been a major release for the team. This has been a really amazing effort by everybody at Ortus and all of the amazing feedback from our clients migrating to BoxLang and coming up with such amazing and innovative ideas for this platform. We have only just begun!

**Let's walk through everything.**

## Dynamic Sets - A First-Class Collection

BoxLang 1.14.0 delivers `BoxSet` as a genuine first-class type - not a thin wrapper, not a library afterthought - a fully integrated collection with literal syntax, functional pipelines, operator overloads for set algebra, and three backing variants to suit whatever your workload demands.

Unlike arrays, sets enforce uniqueness by design and offer highly efficient lookup operations. BoxLang elevates sets to a first-class citizen with literal syntax, functional collection operations, and rich operator overloads for set algebra—including unions, intersections, differences, and symmetric differences—making complex data manipulation both expressive and concise.

Whether you're comparing datasets, managing unique identifiers, processing large collections, implementing access-control rules, or building recommendation and analytics engines, BoxSet provides a performant and elegant foundation for working with distinct values at scale.

Sets come in three flavors:

* `DEFAULT` (HashSet) - fastest, no guaranteed ordering
* `LINKED` (LinkedHashSet) - preserves insertion order
* `SORTED` (TreeSet) - natural ascending order via `Compare.invoke`

```java
// BIF construction
s = setNew()
s = setNew( type="linked", values=[ 1, 2, 3 ] )
s = setOf( 1, 2, 2, 3 )   // deduped automatically → {1, 2, 3}

// Literal syntax - clean and expressive
s = set{ 1, 2, 3 }
s = set{}

// Spread support
arr = [ 3, 4, 5 ]
s   = set{ 1, 2, ...arr }

// From an Array
s = [ 1, 2, 2, 3 ].toSet()
s = [ "c", "a", "b", "a" ].toSet( "linked" )

// From a delimited string
s = "a,b,c,a".listToSet()
```

The operator overloads are where things get elegant. Set algebra is a first-class operation:

```java
a = set{ 1, 2, 3 }
b = set{ 3, 4, 5 }

union     = a + b   // {1, 2, 3, 4, 5}
diff      = a - b   // {1, 2}
intersect = a * b   // {3}
symdiff   = a ^ b   // {1, 2, 4, 5}
```

The right-hand operand is accepted "loosely" - you can add an Array, a list string, a Range, or another Set. And functional pipelines work exactly as you'd expect:

```java
result = setOf( 1, 2, 3, 4, 5 )
    .filter( v -> v > 2 )
    .map( v -> v * 10 )
    .toList( ", " )
// → "30, 40, 50"
```

Structs now expose `.keySet()` and `.valueSet()` to extract keys or values as sets. Sets serialize to JSON arrays. And any `java.util.Set` implementation wraps transparently - mutations propagate back to the underlying Java object, same contract as array wrapping.

Sets are also fully immutable-capable. Call `.toUnmodifiable()` to freeze a set, and `.toModifiable()` to thaw a copy when you need to mutate again.
> **Full reference:** [BoxSet Documentation](https://boxlang.ortusbooks.com/boxlang-language/syntax/sets)

## Ranges - Lazy, Typed, Extensible Intervals

The `..` operator has existed in BoxLang since version 1.12, but it used to materialize an array eagerly. That was fine for small sequences. It was a problem for anything large, and it completely blocked representing infinite sequences, non-integer intervals, or domain-specific progressions.

BoxLang 1.14.0 rethinks ranges from first principles. Ranges are now **lazy objects** that generate values on demand. They are not arrays. They carry type semantics. They support exclusive boundaries, custom stepping, Java Stream integration, and - most powerfully - a new `IRangeable` interface that lets your own classes participate in range operations.

```java
// Inclusive - generates 1, 2, 3, 4, 5
1..5

// Exclusive boundaries
1>..5    // exclude start: 2, 3, 4, 5
1..<5    // exclude end:   1, 2, 3, 4
1>..<5   // exclude both:  2, 3, 4

// Half-bounded and unbounded
1..      // open-ended from 1 (infinite)
..5      // open start, up to 5
..       // fully unbounded (contains everything non-null)
```

Because ranges are lazy, even absurdly large ones are cheap:

```java
// This does NOT allocate 100 billion integers
for( i in 1..100_000_000_000 ) {
    result = i
    break   // instant
}

// Full Java Stream API integration
( 1.. ).stream().limit( 5 ).toList()   // [1, 2, 3, 4, 5]
```

Beyond integers, ranges work natively with **decimals, characters, and DateTime values**:

```java
// Decimal with custom step
( 0..1 ).step( 0.25 )   // 0, 0.25, 0.50, 0.75, 1.00

// Characters
for( c in "a".."e" ) { }    // a, b, c, d, e

// DateTime by month
start = createDate( 2024, 1, 1 )
end   = createDate( 2024, 6, 1 )
( start..end ).step( 1, "month" )   // Jan, Feb, Mar, Apr, May, Jun
```

Stepped ranges do **step-reachability checking** for `contains()` - not just bounds checking. If a value is within the bounds but not actually reachable by the step increment, `contains()` returns false. This is the Python/Kotlin convention and it's the correct behavior:

```java
r = ( 1..10 ).step( 3 )   // produces: 1, 4, 7, 10
r.contains( 4 )            // true  - reachable
r.contains( 5 )            // false - within bounds, but NOT reachable
```

The `IRangeable` interface is the headline capability. Any BoxLang or Java class can join the range system by implementing four methods: `rangeAdvance()`, `rangeCompare()`, `rangeCoerce()`, and optionally `rangeStepFromUnit()` and `rangeUnitStepper() `for non-uniform progressions. The docs walk through three complete examples - a Fibonacci sequence, Roman numerals, and musical notes with full chromatic and scale-aware stepping. These are not toys. They demonstrate a real extensibility framework.

```java
// Fibonacci: infinite non-linear range
( new Fib().. ).stream().limit( 10 ).map( .getCurrent() ).toList()
// [1, 1, 2, 3, 5, 8, 13, 21, 34, 55]

( new Fib().. ).contains( 13 )   // true
( new Fib().. ).contains( 14 )   // false
```

Typed unbounded ranges let you constrain what a `.. `range considers a match, using BoxLang's casting system or strict Java class matching:

```java
( .. ).type( "number" ).contains( "5" )      // true - coercible
( .. ).type( "integer" ).contains( 5.5 )     // false - not a whole integer

import java:java.lang.Number
( .. ).type( Number ).contains( 42 )         // true - instanceof check
( .. ).type( Number ).contains( "5" )        // false - strict, no coercion
```

**Full reference:** [BoxLang Ranges Documentation](https://boxlang.ortusbooks.com/boxlang-language/syntax/ranges)

## Inner Classes and Template Classes

BoxLang 1.14.0 introduces **locally defined classes** - classes you can declare inline inside a `.bxs` script, a `.bxm` template's block, or nested inside another class. This is structural expressiveness that matters for keeping code organized without forcing every concern into its own file.

Classes defined in scripts are **hoisted**, meaning you can instantiate them before their textual definition appears:

```java
// Instantiate before definition - hoisting at work
result = new Greeter().greet( "World" )

class Greeter {
    function greet( name ) {
        return "Hello, " & name & "!"
    }
}
```

Multiple local classes coexist naturally. Static members, abstract classes, and inheritance all work:

```java
abstract class Shape {
    abstract function area()
}

class Circle extends="Shape" {
    function init( radius ) {
        variables.radius = radius
        return this
    }

    function area() {
        return 3.14159 * variables.radius ^ 2
    }
}

c = new Circle( 5 )
c.area()   // ~78.54
```

Local classes inherit their enclosing script's imports, so Java types are available directly without any extra ceremony:

```java
import java.util.Date

class Event {
    function init( name ) {
        variables.name      = name
        variables.timestamp = new Date()
        return this
    }

    function getInfo() {
        return variables.name & " at " & variables.timestamp.toString()
    }
}
```

**Inner classes** - classes nested inside other classes - are accessed externally via `$` separator syntax, with full support for import aliases:

```java
// Fully qualified
result = new src.models.Container$Widget( "my-widget" )

// Import with alias
import src.models.Container$Widget as Widget
result = new Widget( "aliased-widget" )
```

**Template classes** let you define a class inside a island in a `.bxm` markup file:

```java
<bx:script>
    class Point {
        function init( x, y ) {
            variables.x = x
            variables.y = y
            return this
        }
        function toString() {
            return "(" & variables.x & "," & variables.y & ")"
        }
    }
    result = new Point( 3, 4 ).toString()
</bx:script>
```

**Full references:** [Inner Classes](https://boxlang.ortusbooks.com/boxlang-language/classes/inner-classes) \| [Template Classes](https://boxlang.ortusbooks.com/boxlang-language/classes/template-classes)

## Class References as Callable Constructors

This one changes how you think about object creation. In BoxLang 1.14.0, imported class references are **callable** . Invoking a class reference as a function executes the constructor and returns a new instance. The `new` keyword remains fully supported - this is additive, not a replacement.

```java
import java.lang.StringBuilder
import models.User

// These three forms are equivalent
u1 = new User( "Bob", "bob@example.com" )
u2 = User.init( "Bob", "bob@example.com" )
u3 = User( "Bob", "bob@example.com" )      // class reference called as function
```

Where this becomes genuinely powerful is functional programming. Because class references are now callable objects, you can pass them directly to higher-order functions:

```java
import models.User

names = [ "Alice", "Bob", "Charlie" ]

// These are all equivalent - pick your style
users = names.map( User )
users = names.map( name -> new User( name ) )
users = names.map( name -> User( name ) )
```

The shorthand `names.map( User )` is the real win - transforming a collection of raw values into domain objects becomes a single expression. Under the hood, class references are wrapped in a `ClassInvokerFunction` that delegates to the same constructor pipeline as `new`, so behavior is identical. Java classes and BoxLang classes participate equally.

## DataNavigator JSONPath Support

The `DataNavigator` has been a useful tool for safely traversing nested structs and arrays. In 1.14.0 it gains full **JSONPath-style expression support** - dot notation, array indexing, slicing, wildcards, recursive descent, and filter expressions - directly in `get()`, `has()`, `from()`, and the new `query()` method.

```java
nav = dataNavigate( jsonData )

// Dot-notation deep access
value  = nav.get( "boxlang.settings.hello" )

// Recursive descent - find "key1" anywhere in the tree
found  = nav.has( "..key1" )

// Array slicing - 1-based inclusive
slice  = nav.get( "list[1:3]" )

// Wildcard - all children
all    = nav.get( "items[*].name" )

// Filter expressions
active = nav.query( "items[?(@.active == true && @.priority > 2)]" )
named  = nav.query( "items[?(@.active)].name" )
```

The new `query()` method returns every match as a BoxLang Array - the right tool when a path fans out across collections. `getOrDefault()` gives you a guaranteed non-null return with an explicit fallback. And `getByKey()` / `hasByKey()` handle exact-key lookups where key names themselves contain dots or brackets:

```java
// Multiple matches returned as an array
results = nav.query( "store.products[?(@.price > 100)].name" )

// Explicit fallback - no null checks needed
port = nav.getOrDefault( "server.port", 8080 )

// Literal key access - treats "value.sep" as one key name
nav.getByKey( "value.sep" )
```

All path expressions are whitespace-tolerant. The result is dramatically less boilerplate when consuming external JSON, API payloads, or deeply nested configuration.
> **Full reference:** [DataNavigator Documentation](https://boxlang.ortusbooks.com/boxlang-language/syntax/data-navigators#jsonpath-style-path-expressions)

## Query Transformers - Own Your Result Shape

`queryExecute()` has always locked you into three return types: `query`, `array`, or `struct`. Any other shape - domain objects, JSON strings, tabular arrays with column descriptors, rich metadata structs - required a separate post-processing step. That friction adds up fast.

The new **Query Transformer** framework solves this cleanly. Pass a `transformer` option and take full control of what `queryExecute()` returns:

```java
// Inline closure - returns a custom struct with metadata
var result = queryExecute( "SELECT * FROM users", [], {
    datasource: "app",
    transformer: ( query, meta ) => {
        return {
            data:        query.toArrayOfStructs(),
            total:       query.recordCount,
            executedAt:  now(),
            sql:         meta.sql
        }
    }
} )
```

```java
// Domain objects from query rows
var users = queryExecute( "SELECT * FROM users", [], {
    datasource: "app",
    transformer: ( query, meta ) => query.toArrayOfStructs().map( row -> new User( row ) )
} )
```

The transformer receives the raw `query` object (with access to `.recordCount`, `.toArrayOfStructs()`, `.getData()`, `.getColumnNames()`, `.getColumnMeta()`) and a `metadata` struct containing the SQL, parameters, execution time, and column metadata. When `transformer` is present it takes precedence over `returnType`.

Transformers can also be class instances or named registrations in `Application.bx`:

```java
// Application.bx - register reusable transformers
this.queryTransformers = {
    "rich":    new RichTransformer(),
    "tabular": ( query, meta ) => {
        return {
            columns: query.getColumnNames(),
            data:    query.getData().map( row -> arrayNew( row ) )
        }
    },
    "json":    ( query, meta ) => serializeJson( query.toArrayOfStructs() )
}

// Usage anywhere in the app
var tabular = queryExecute( sql, params, { transformer: "tabular" } )
var json    = queryExecute( sql, params, { transformer: "json" } )
```

The `bx:query` component supports transformers too:

```java
<bx:query name="result" datasource="app"
    transformer=(( q, m ) => serializeJson( q.toArrayOfStructs() ))>
    SELECT * FROM users
</bx:query>
```

### Global Query Defaults

Alongside transformers, `BL-2477` introduces a `queries` section in `boxlang.json` and `this.queryOptions` in `Application.bx` for application-level query defaults:

```java
"queries": {
    "timeout":       0,
    "returnType":    "query",
    "fetchSize":     0,
    "maxRows":       0,
    "cacheProvider": "default"
}
```

```java
// Application.bx
this.queryOptions = {
    "timeout":    30,
    "returnType": "array",
    "maxRows":    1000
}
```

Per-query options always win. `this.queryOptions `is the application-level default. `boxlang.json` is the runtime fallback. Clean precedence, no surprises.
> **Full reference:** [Query Transformers](https://boxlang.ortusbooks.com/boxlang-language/syntax/queries#query-transformers-custom-result-formatting) \| [Global Query Options](https://boxlang.ortusbooks.com/getting-started/configuration/queries)

## Companion Release: bx-mcp Is Here

Paired with BoxLang 1.14.0 comes the public debut of `bx-mcp` - the module that gives your AI a live window into your running BoxLang application. While 1.14.0 advances the language itself, `bx-mcp` advances how you operate that language in production.

The problem it solves is one every BoxLang developer knows. You launch an application. Traffic flows. Schedulers execute. Caches warm. Threads spin. And when something goes wrong - or when you just want to understand the state of the system - you context-switch between logs, dashboards, admin panels, and monitoring tools to piece it together. Your AI assistant, meanwhile, only understands source code. It has no visibility into the live system.

`bx-mcp` changes that. Install it, point any MCP-compatible AI client at your running server, and you get conversational access to every BoxLang subsystem in real time.

```java
box install bx-mcp

{
    "mcpServers": {
        "boxlang": {
            "url": "http://localhost:8080/~bxmcp/boxlang.bxm",
            "headers": {
                "Authorization": "Bearer your-auth-token"
            }
        }
    }
}
```

What you get is substantial. **154 tools across 17 runtime domains** - JVM diagnostics, cache management, datasource pool metrics, SQL slow query capture, outbound HTTP diagnostics, inbound request diagnostics, per-route latency metrics, scheduler management, module reloading, interceptor introspection, file watcher control, logging, and more. Five of those domains are brand new: SQL Diagnostics, HTTP/SOAP Diagnostics, Request Diagnostics, Route Metrics, and a Performance Snapshot tool that captures the full runtime picture in a single call.

Beyond tools, `bx-mcp` ships **32 pre-built AI diagnostic prompts** - pre-wired reasoning workflows that instruct your AI which tools to call, in what sequence, and how to interpret the results. Ask it to diagnose a memory leak, investigate a degraded cache, or triage a saturated thread pool, and it knows exactly how to approach the investigation.

The result is a fundamentally different way to work with a running BoxLang application. No SSH. No log grepping. Conversational operations with full runtime context.
> **Read the full announcement:**   
> [Introducing BoxLang MCP Module](https://www.ortussolutions.com/blog/introducing-boxlang-mcp-give-your-ai-a-window-into-your-running-boxlang-application)
>
> **Documentation:** [bx-mcp Documentation](https://boxlang.ortusbooks.com/boxlang-+-++/modules/bx-mcp)

## Other Notable Additions

### `schedulerNew()` BIF

Create and register lightweight ad-hoc schedulers without a dedicated class file. `schedulerNew()` is the right tool when you need a runtime scheduler without lifecycle callbacks; `schedulerStart() `remains the choice when you need `onStartup`, `onShutdown`, and `onAnyTaskError`:

```java
myScheduler = schedulerNew(
    name:     "email-scheduler",
    timezone: "America/Chicago"
)

myScheduler.task( "welcome-email" )
    .call( () => sendWelcomeEmails() )
    .everyHour()
    .startup()
```

### `server.webMode`

A new boolean on the `server` scope tells you whether the runtime is operating in web mode (servlet or MiniServer):

```java
if ( server.webMode ) {
    // web-specific initialization
}
```

### String BIFs: `stringStartsWith` and `stringEndsWith`

Four new BIFs with full member-method support:

```java
stringStartsWith( "Hello World", "Hello" )    // true
stringEndsWith( "Hello World", "World" )      // true
stringStartsWithNoCase( "HELLO", "hello" )    // true
stringEndsWithNoCase( "WORLD", "world" )      // true

// Member methods
"Hello World".startsWith( "Hello" )
"Hello World".endsWith( "World" )
```

### Java Interop: Varargs Improvements

BoxLang arrays passed to Java varargs methods no longer need manual unpacking into `Object[]`. The runtime handles the conversion automatically.

### Java Import Aliases in `extends` and `implements`

Import aliases now work in class inheritance declarations:

```java
import java.util.HashMap as MyMap

class extends="MyMap" {
    // ...
}
```

### Formatter Maturity

The formatter received significant investment in 1.14.0:

* **Ignore comments** - `@formatter:off `/ `@formatter:on` and `bxformat-ignore-start` / `bxformat-ignore-end` (matching cfformat conventions too) let you opt specific blocks out of formatting
* **Multiple source files** -` --source` now accepts comma-separated paths
* **Excludes flag** - `--excludes` skips files or directories
* **`template.enabled` flag** - gates experimental `.bxm` formatting until it exits preview (defaults to `false`)
* **`class.property_spacing` rule** - controls blank lines between property declarations (defaults to `1`, matching Ortus standards)  

  ### MiniServer Health Metrics

  The `/health` endpoint now includes Undertow worker pool statistics, WebSocket session counts, and expanded JVM metrics - giving you a richer operational picture without any extra tooling.

### Application Runtime Introspection

Application objects expose three new introspection methods:

```java
application.getWatchers()       // active file watchers
application.getSchedulers()     // registered schedulers
application.getAppDuration()    // application uptime
```

`ON_DATASOURCE_INITIALIZED` Interception Point

A new interception point fires after datasource config is loaded but before the HikariCP connection pool is established - giving modules full access to raw pool configuration:

```java
function onDatasourceInitialized( event, interceptData ) {
    var hikariConfig = interceptData.hikariConfig
    hikariConfig.setMaximumPoolSize( 50 )
    hikariConfig.addDataSourceProperty( "cachePrepStmts", true )
}
```

## Bug Fix Highlights

Sixty-five issues means a lot of ground covered. Some fixes worth calling out specifically:

* **`BL-2425`** - Large `if`/`else` blocks no longer throw `LargeMethodErrors`. The compiler now splits oversized conditional blocks rather than failing.
* **`BL-2400`** - `serializeJSON()` stack overflow regression from 1.13 is resolved.
* **`BL-2413`** - Large Query of Queries operations under thread contention are now properly thread-safe.
* **`BL-2432`** - Java interop varargs now handle BoxLang arrays without manual unpacking.
* **`BL-2403`** - `Decrypt` BIF now correctly handles complex/structured objects - previously corrupted nested data.
* **`BL-2479`** - `BoxCacheStats.hitRate()` no longer returns `0` due to integer division.
* **`BL-2042`** - `LoggingService` concurrent modification exception fixed with thread-safe logger management.
* **`BL-2483`** - URISyntaxException on paths containing spaces is resolved.
* **`BL-1007`** - `snakeCase()`, `pascalCase()`, and `kebabCase()` now correctly handle camelCase, PascalCase, snake_case, kebab-case, and mixed inputs.  

  ## Getting 1.14.0Update via CommandBox:

```java
box update boxlang
```

Or grab the latest from [boxlang.io](https://boxlang.io/?_gl=1*dc2uo5*_gcl_aw*R0NMLjE3NzYzNjQ2MzQudGVzdDEyMw..*_gcl_au*MTY3OTk4MjQwNS4xNzgyMTI1MTI4*_ga*MTI2NTE0Mzk0NC4xNzc0MDMxMDk0*_ga_D1P6P1YYT0*czE3ODM2MTE1NjEkbzk4JGcxJHQxNzgzNjEzMDE4JGo2MCRsMCRoMA..*_ga_663JFQ7YGX*czE3ODM2MTE1NjEkbzEwOSRnMSR0MTc4MzYxMzAxOCRqNjAkbDAkaDA. "boxlang.io").

The full release notes live at [boxlang.ortusbooks.com/readme/release-history/1.14.0](https://boxlang.ortusbooks.com/readme/release-history/1.14.0 "boxlang.ortusbooks.com/readme/release-history/1.14.0"). New documentation for every major feature is linked throughout this post.

If you want extended capabilities - `bx-mcp`, `bx-ai`, `bx-jwt`, `bx-redis`, and the rest of the BoxLang+ module ecosystem - visit [boxlang.io/plans](https://boxlang.io/plans?_gl=1*1yjhyxa*_gcl_aw*R0NMLjE3NzYzNjQ2MzQudGVzdDEyMw..*_gcl_au*MTY3OTk4MjQwNS4xNzgyMTI1MTI4*_ga*MTI2NTE0Mzk0NC4xNzc0MDMxMDk0*_ga_D1P6P1YYT0*czE3ODM2MTE1NjEkbzk4JGcxJHQxNzgzNjEzMDE4JGo2MCRsMCRoMA..*_ga_663JFQ7YGX*czE3ODM2MTE1NjEkbzEwOSRnMSR0MTc4MzYxMzAxOCRqNjAkbDAkaDA. "boxlang.io/plans") to see what fits your team.

We ship fast. We ship for real workloads. BoxLang 1.14.0 is the clearest statement yet of what this language is becoming.

Questions, feedback, and show-and-tell belong in the [Ortus Community](https://community.ortussolutions.com/ "Ortus Community").
