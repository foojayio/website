---
title: "BoxLang v1.13.0: Compatibility, Concurrency, and Formatter Maturity"
date: "2026-05-19T12:11:19+00:00"
lastmod: "2026-05-19T12:15:47+00:00"
description: "BoxLang 1.13.0 is a stability-first release with deep compatibility work and runtime hardening. This build closes 48 issues, with the majority focused on - by Cristobal Escobar"
authors:
  - "cristobal-escobar"
image: "Captura-de-pantalla-2026-05-14-123135.png"
categories:
  - "BoxLang"
  - "Developer Tools"
  - "DevOps"
  - "Java"
  - "Performance"
  - "Release Notes"
  - "Security"
related_posts:
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
  - "boxlang-1-14-0-navigate-anything-jsonpath-comes-to-boxlangs-datanavigator"
  - "boxlang-1-14-0-query-transformers-take-full-control-of-your-query-results"
  - "boxlang-1-14-0-boxset-is-here-boxlangs-new-first-class-set-type"
frozen: false
---

![](boxlang-v1.13.0-700x467.jpg)

**BoxLang 1.13.0** is a stability-first release with deep compatibility work and runtime hardening. This build closes 48 issues, with the majority focused on CFML compatibility edge cases, concurrency correctness, formatting parity, and miniserver/runtime reliability under real production loads.

While this release is bug-fix heavy, it still introduces several meaningful features and quality-of-life improvements: character-aware trimming, class metadata lookup by absolute path, process environment control in SystemExecute(), SOAP headers, new query column rename capabilities, and safer miniserver routing/security defaults.

## New Features

Three additions that materially expand what the runtime can do.

### Character-Aware Trimming --- `trim()`, `ltrim()`, `rtrim()`

The string trimming BIFs now accept an optional chars argument. Strip arbitrary character sets without reaching for `rereplace()`.

```java
"**Urgent**".trim( "*" )       // "Urgent"
"000123".ltrim( "0" )          // "123"
"report....".rtrim( "." )      // "report"
"//path/to/dir//".trim( "/" )  // "path/to/dir"
```

Each character in `chars` is treated as an independent trim target — the same behavior you'd expect from Python or JavaScript. One less regex workaround.

### `getClassMetadata()` by Absolute Path

Class metadata can now be loaded directly from a filesystem path, bypassing the class loader and import resolution entirely.

```java
meta = getClassMetadata( "/opt/apps/models/User.bx" )
writeDump( meta.name )        // "User"
writeDump( meta.properties )  // array of property definitions
writeDump( meta.functions )   // array of function signatures
```

This is a cornerstone API for tooling. Linters, IDE integrations, documentation generators, and migration scanners can now inspect `.bx` and .`cfc` files without booting them into the runtime, firing `onApplicationStart`, or wrestling with import edge cases. The kind of unglamorous primitive that makes an ecosystem possible.

### `SystemExecute()` Environment Controls

Two new arguments give you deterministic control over the environment of spawned child processes:

* `inheritEnvironment` (boolean, default `true`) — when `false`, the child starts with a clean slate
* `environment` (struct) — an explicit map of variables to inject

```java
result = systemExecute(
    name               = "env",
    arguments          = "",
    inheritEnvironment = false,
    environment        = {
        APP_ENV   : "production",
        DB_HOST   : "internal.db.example.com",
        FEATURE_X : "true"
    }
)

writeOutput( result.output )
```

Before 1.13.0, every `systemExecute()` call inherited the full parent environment — including secrets, tokens, and internal config. Security-conscious deployments now have an explicit, auditable way to lock that down.

## The [BoxLang Formatter](https://boxlang.ortusbooks.com/getting-started/ide-tooling/boxlang-formatter "BoxLang Formatter") Goes Production-Ready

This is a flagship moment. The formatter graduates from experimental to production-grade and lands with a complete CI/CD integration surface.

**The outcome you actually care about** : when formatting is enforced in CI, pull requests stop being about whitespace and start being about logic again. For mixed BoxLang/CFML codebases, the legacy `.cfformat.json` compatibility path means you can adopt the formatter on legacy code today and migrate to BoxLang-native defaults on your own timeline.

#### Capabilities:

* **In-place formatting** --- `boxlang format --input ./` formats an entire project tree
* **CI check mode** --- `boxlang format --check --input ./` exits non-zero on any unformatted file (drop straight into GitHub Actions, GitLab CI, or Jenkins)
* **Stdout mode** --- `boxlang format --overwrite false --input ./models/User.cfc` for diff-friendly previews
* **Multi-extension** --- `.bx`, `.bxs`, `.bxm`, `.cfm`, `.cfc`, `.cfs` in a single pass

#### Config discovery fallback chain:

* `.bxformat.json` --- BoxLang-native config (Ortus gold-standard defaults)
* `.cfformat.json` --- legacy CFFormat config, auto-converted with migration-safe defaults
* Built-in defaults — sensible behavior with zero config

#### Migration tooling built in:

```java
# Generate a fresh .bxformat.json with defaults
boxlang format --initConfig

# Convert an existing .cfformat.json to .bxformat.json
boxlang format --convertConfig --input ./
```

## Async \& Concurrency Hardening

Concurrency bugs are the worst kind of bug — intermittent, non-deterministic, catastrophic when they hit production. 1.13.0 closes several long-standing race conditions and lifecycle issues across the [async subsystem](https://boxlang.ortusbooks.com/boxlang-language/reference/built-in-functions/async/ "async subsystem") and [threading layer](https://boxlang.ortusbooks.com/boxlang-language/syntax/threading "threading layer").

**API surface normalization** . Missing async methods are restored: `all()`, `allApply()`, `thenAsync()`, `delay()`, and `shutdownAndAwaitTermination()` now exist with correct signatures. Positional spread arguments (`...args`) are supported in calls — unblocking a common functional-programming pattern.

```java
args     = [ "Ada", "Lovelace" ]
fullName = formatName( ...args )
```

**`BoxFuture()` lifecycle** . A `BoxFuture` created during an HTTP request used to throw scope-access errors if the parent request completed before the future resolved. The context lifecycle is now properly decoupled — background work survives request teardown without touching stale scopes.

**Concurrent array iteration** . `for/in` loops over arrays no longer throw `ConcurrentModificationException` when the array is mutated from another thread.

**Atomic class file writes.** Class generation now uses a temp-file-then-atomic-rename pattern. No more transient zero-byte `.class` artifacts surfacing under parallel compilation — a race condition that produced some genuinely painful `ClassNotFoundException` reports in production.

## [MiniServer](https://boxlang.ortusbooks.com/getting-started/running-boxlang/miniserver "MiniServer"): Security \& Reliability

**The headline** : a misconfigured miniserver no longer accidentally serves your source code or configuration over HTTP. The static-serving security filter now blocks hidden files and dotfiles, framework config artifacts (`.boxlang.json`, `boxlang.json`), and source files (`.bx`, `.cfc`) when not routed through the engine.

**Pass predicate is now configurable** through three channels — pick whichever fits your deployment model:

```java
# CLI
boxlang server start --pass-predicate "/api/*"
```

```java
// boxlang.json
{
  "web": {
    "passPredicate": "/api/*"
  }
}
```

```java
# Environment variable
export BOXLANG_PASS_PREDICATE="/api/*"
```

**Transfer reliability fixes:**

* Chunked encoding truncation fixed for large file responses (above the default buffer size)
* Empty text-file uploads no longer throw illegal-state errors
* `content-length` headers correctly computed across all response paths

## Compatibility Wins

CFML compatibility is a continuous workstream, not a one-time port. This release closes a handful of high-impact gaps that real applications were tripping over.

**[SOAP](https://boxlang.ortusbooks.com/boxlang-language/reference/built-in-functions/net/soap "SOAP") header support** . Consumers can now include optional `<Header>` blocks for WS-Security, transactional metadata, and routing.

```java
soapService.call(
    method  = "processOrder",
    headers = { Security : { UsernameToken : { Username : "admin" } } }
)
```

`query.setColumnNames()`. Query objects now support column renaming through a dedicated method, matching the Adobe CF and Lucee API.

```java
q = queryNew( "fname,lname", "varchar,varchar", [ [ "Ada", "Lovelace" ] ] )
q.setColumnNames( [ "firstName", "lastName" ] )
writeDump( q.columnList )  // "firstName,lastName"
```

**[CLI](https://boxlang.ortusbooks.com/getting-started/configuration "CLI") `.box.env` support** . The CLI now reads `~/.box.env` on startup, loading user-level environment variables that persist across sessions.

```java
# ~/.box.env
DB_HOST=localhost
DB_PORT=5432
```

Runtime Hardening  

The unsexy stuff that matters. A condensed view of the deeper fixes shipped in this release:

|       Area        |                             What Changed                             |
|-------------------|----------------------------------------------------------------------|
| Abort semantics   | Corrected in web runtime Java `try/catch` boundaries                 |
| AppCDS paths      | Deterministic, per-binary paths on Windows                           |
| Superclass init   | Failed init no longer blocks class recreation retries                |
| Module `onLoad()` | Request-context setup fixed for `dump()` template behavior           |
| REST CFC mapping  | Service-name routing corrected                                       |
| Class creation    | Broad performance optimizations in class loading and locator         |
| JSA packages      | Path handling fixed for `BOXLANG_HOME` with spaces                   |
| Zero timespan     | `createTimeSpan( 0, 0, 0, 0 )` now correctly interpreted as no-cache |
| Remote methods    | Force-write correctly under `enableOutputOnly`                       |
| Binary writes     | Valid downloaded ZIP output restored                                 |
| Numeric parsing   | Leading-zero strings parsed safely                                   |
| QoQ nesting       | Nested-parentheses predicate parsing corrected                       |
| Custom tags       | this scope no longer leaks from custom-tag context                   |
| `numberFormat()`  | Major mask compatibility sweep across multiple tickets               |

## Changelog Highlights

#### New Features

BL-2348: `trim()`, `ltrim()`, `rtrim()` gain `chars` argument  

BL-2349: `getClassMetadata()` accepts absolute filesystem path  

BL-2390: `SystemExecute()` gains `inheritEnvironment` and `environment` arguments

#### Improvements

BL-2078: SOAP header support for auth and security blocks  

BL-2333: `query.setColumnNames()` compatibility API  

BL-2354: Miniserver pass predicate configurability (CLI, JSON, env var)  

BL-2355: Miniserver security handler upgrades  

BL-2378: CLI reads `~/.box.env` on startup  

BL-2393: Chunked encoding truncation fix for large file responses  

BL-2398: BoxLang-native formatting defaults aligned with Ortus conventions

#### Notable Bug Fixes

BL-2269: Missing async methods and signatures restored  

BL-2336: Abort semantics corrected in web runtime `try/catch` boundaries  

BL-2360: Positional spread arguments supported in calls  

BL-2372: Concurrent modification exception fixed for array `for/in`  

BL-2373: Class-file write race fixed with atomic write pattern  

BL-2376: `BoxFuture()` context lifecycle fix after HTTP request completion  

BL-2382: Binary write path fixed for valid downloaded ZIP output  

BL-2386: QoQ nested-parentheses predicate parsing corrected  

BL-2394: Custom-tag context no longer leaks incorrect `this` scope

**[View the full release report](https://boxlang.ortusbooks.com/readme/release-history/1.13.0 "View the full release report")**

**BoxLang 1.13.0 is available now** . Head to [boxlang.io](https://www.boxlang.io/ "boxlang.io") to get started, dig into the [docs](https://boxlang.ortusbooks.com/ "docs"), and join us on the [Ortus Community Slack](https://boxteam.ortussolutions.com/ "Ortus Community Slack") to share what you're building.
