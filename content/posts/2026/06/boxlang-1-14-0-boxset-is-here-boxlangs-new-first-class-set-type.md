---
title: "BoxLang 1.14.0 : BoxSet is Here, BoxLang's New First-Class Set Type"
slug: "boxlang-1-14-0-boxset-is-here-boxlangs-new-first-class-set-type"
date: "2026-06-17T10:36:28+00:00"
lastmod: "2026-06-17T13:09:37+00:00"
description: "BoxLang 1.14.0 ships BoxSet: a native Set type with literal syntax, union/intersection operators, functional pipeline, and deep Java interop."
authors:
  - "cristobal-escobar"
image: "https://foojay.io/wp-content/uploads/2026/06/BoxLang-release-1.14.0-1701-x-1701-px-1-1024x1024.png"
categories:
  - "BoxLang"
  - "Design Patterns"
  - "Developer Tools"
  - "Java"
  - "Java Core"
  - "Tools"
tags:
related_posts:
enlighterjs: true
frozen: false
---

![](/images/posts/2026/06/boxlang-1-14-0-boxset-is-here-boxlangs-new-first-class-set-type/BoxLang-release-1.14.0-1-700x394.png)

<br />

BoxLang 1.14.0 ships with a new **dynamic first-class Set type** baked directly into the language. Not a wrapper you reach for manually, not a `createObject( "java", "java.util.HashSet" )` incantation you paste from a Stack Overflow answer years ago. A real `BoxSet` with literal syntax, operator overloads, a full functional pipeline, change listeners, JSON serialization, and deep Java interop.

If you have ever deduplicated an array with a loop, compared two collections element by element, or modeled a permission system on top of a struct -- Sets are the tool you were missing. Let's dig in.

Why Sets? The Problem First {#h2-0-why-sets-the-problem-first}
--------------------------------------------------------------

Arrays are ordered, indexed, and allow duplicates. Structs are key/value maps. Both are foundational, but neither models one of the most common real-world shapes: **a bag of unique things.**

Think about:

* A user's assigned roles: `[ "admin", "editor", "admin" ]` -- that duplicate is a bug waiting to happen
* Tags on a blog post -- order usually doesn't matter, uniqueness does
* Active feature flags -- membership testing is the only operation you need
* Two datasets that need to be compared -- what's new, what's gone, what's shared?
* Need to know all the incoming arguments in exact order and content?
* URL deduplication in a crawler
* A permission intersection: what can this user do on this resource?

Before BoxSet you'd approximate all of these with arrays (slow `arrayContains` lookups, manual dedup loops) or structs (keys as values, awkward serialization). Both are workarounds. BoxSet is the real answer.

Meet BoxSet {#h2-1-meet-boxset}
-------------------------------

`BoxSet` is a first-class BoxLang type that wraps `java.util.Set` with full language integration. Under the hood it selects among three Java backing implementations depending on what you need:

|    BoxSet Type     |  Java Backing   |         Characteristic         |
|--------------------|-----------------|--------------------------------|
| `default / hash`   | `HashSet`       | No ordering, fastest lookups   |
| `linked / ordered` | `LinkedHashSet` | Preserves insertion order      |
| `sorted / tree`    | `TreeSet`       | Natural ascending order always |

Every variant enforces uniqueness automatically. Every variant supports the full member-function API, operator overloads, and functional pipeline. Java `Set` objects you get from third-party libraries slot right in -- BoxLang wraps them without copying.

Creating Sets: Every Path {#h2-2-creating-sets-every-path}
----------------------------------------------------------

BoxLang gives you several ergonomic creation paths depending on your context.

### `setNew()` -- the workhorse {#h3-3-setnew-the-workhorse}

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Empty default (hash) Set -- fastest, no ordering
s = setNew()

// Seed it on creation
s = setNew( values=[ "alpha", "beta", "gamma", "alpha" ] )
s.size()    // 3 -- duplicate dropped automatically

// Linked: preserves insertion order
s = setNew( type="linked", values=[ "c", "a", "b" ] )
s.toArray()    // ["c", "a", "b"] -- order preserved

// Sorted: natural ascending order, always
s = setNew( type="sorted", values=[ 9, 1, 5, 3 ] )
s.toArray()    // [1, 3, 5, 9]

// Case-sensitive: treat "Hello" and "hello" as distinct values
s = setNew( values=[ "Hello", "hello", "HELLO" ], caseSensitive=true )
s.size()    // 3
</pre>

### `setOf()` -- varargs shorthand {#h3-4-setof-varargs-shorthand}

When you know your values up front, `setOf()` is the cleanest expression:

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">roles = setOf( "admin", "editor", "viewer" )
primes = setOf( 2, 3, 5, 7, 11, 13 )
</pre>

Duplicates in the argument list are silently dropped -- no error, no fuss.

### Literal syntax: `set{ ... }` {#h3-5-literal-syntax-set}

BoxLang 1.14.0 introduces a **set literal syntax** that reads like the concept itself:

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Inline literal
permissions = set{ "read", "write", "execute" }

// Empty set
empty = set{}

// Spread an array directly into a set literal
extra = [ 4, 5, 6 ]
merged = set{ 1, 2, 3, ...extra }
// Result: {1, 2, 3, 4, 5, 6}

// Spread another set
defaults = set{ "read", "write" }
full = set{ "execute", ...defaults }

// Spread a range -- this is particularly elegant
digits = set{ ...(0..9) }
// Result: {0, 1, 2, 3, 4, 5, 6, 7, 8, 9}
</pre>

> "
>
> The `set` token is parser-gated so it does not collide  
>
> with variables named `set`. Your existing code is safe.

### Converting from other types {#h3-6-converting-from-other-types}

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Array to Set -- deduplication for free
tags = [ "boxlang", "jvm", "boxlang", "oss" ].toSet()
tags.size()    // 3

// Preserve insertion order while deduplicating
orderedTags = [ "c", "a", "b", "a" ].toSet( "linked" )

// Sorted from array
sorted = [ 9, 1, 5, 3 ].toSet( "sorted" )

// Split a delimited string directly into a Set
csv = "admin,editor,viewer,admin".listToSet()
csv.size()    // 3

// Custom delimiter
pipe = "read|write|execute|read".listToSet( delimiter="|", type="linked" )

// From a Query -- distinct column values in one shot
q = queryNew( "name,dept", "varchar,varchar", [
    [ "Alice", "Engineering" ],
    [ "Bob", "Marketing" ],
    [ "Carol", "Engineering" ]
] )
depts = q.columnData( "dept" ).toSet()
// Result: {"Engineering", "Marketing"}
</pre>

The Three Variants in Practice {#h2-7-the-three-variants-in-practice}
---------------------------------------------------------------------

Choosing the right variant matters for correctness and performance.

### Default (HashSet) -- when order doesn't matter {#h3-8-default-hashset-when-order-doesn-t-matter}

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Permission checking: order is irrelevant, membership speed is everything
userRoles  = setOf( "editor", "viewer", "moderator" )
adminRoles = setOf( "admin", "superadmin" )

canAdmin = userRoles.some( r -&gt; adminRoles.contains( r ) )
// false

// Feature flags -- constant-time lookup regardless of flag count
activeFlags = setNew( values=queryGetColumn( flagQuery, "flagName" ) )
if ( activeFlags.contains( "dark_mode_v2" ) ) {
    // render dark mode
}
</pre>

### Linked (LinkedHashSet) -- when insertion order matters {#h3-9-linked-linkedhashset-when-insertion-order-matters}

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Breadcrumb trail -- visited pages in order, no revisits
trail = setNew( type="linked" )
trail.add( "/home" )
trail.add( "/products" )
trail.add( "/products/123" )
trail.add( "/home" )          // already there, silently ignored
trail.toArray()
// ["/home", "/products", "/products/123"]

// Processing pipeline stages -- ordered, deduplicated
pipeline = setNew( type="linked", values=[ "validate", "enrich", "normalize", "validate" ] )
for ( stage in pipeline ) {
    runStage( stage )    // validate only runs once
}
</pre>

### Sorted (TreeSet) -- when natural order is always required {#h3-10-sorted-treeset-when-natural-order-is-always-required}

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Priority queue of version numbers treated as strings
versions = setNew( type="sorted", values=[ "1.14.0", "1.9.0", "2.0.0", "1.10.0" ] )
versions.toArray()
// ["1.10.0", "1.14.0", "1.9.0", "2.0.0"] -- lexicographic, watch your versioning scheme

// Integer ranges -- always sorted
scores = setNew( type="sorted" )
scores.addAll( [ 87, 42, 99, 55, 87, 42 ] )
scores.toArray()
// [42, 55, 87, 99]

// Get min and max cheaply via toArray()
arr = scores.toArray()
writeDump( "Low: #arr[ 1 ]#, High: #arr[ arr.len() ]#" )
</pre>

Membership and Iteration {#h2-11-membership-and-iteration}
----------------------------------------------------------

### Testing membership {#h3-12-testing-membership}

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">granted = setOf( "read", "write", "execute" )

granted.contains( "read" )     // true
granted.has( "delete" )        // false (alias for contains)

// Test multiple at once
granted.containsAll( [ "read", "write" ] )    // true
granted.containsAll( [ "read", "sudo" ] )     // false
</pre>

### Iterating {#h3-13-iterating}

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">tags = setNew( type="linked", values=[ "boxlang", "jvm", "oss" ] )

// for-in loop
for ( tag in tags ) {
    println( tag )
}

// each() -- cleaner in functional pipelines
tags.each( tag =&gt; {
    processTag( tag )
} )
</pre>

Set Algebra: The Real Power {#h2-14-set-algebra-the-real-power}
---------------------------------------------------------------

This is where `BoxSet` earns its place. Four algebraic operations, available as both member methods and **overloaded operators**.

### Union -- all unique elements from both sets {#h3-15-union-all-unique-elements-from-both-sets}

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">backendSkills  = setOf( "java", "sql", "boxlang", "redis" )
frontendSkills = setOf( "javascript", "css", "boxlang", "react" )

allSkills = backendSkills.union( frontendSkills )
// {java, sql, boxlang, redis, javascript, css, react}

// Operator syntax: +
allSkills = backendSkills + frontendSkills
</pre>

### Intersection -- only what both sets share {#h3-16-intersection-only-what-both-sets-share}

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">teamA = setOf( "alice", "bob", "carol", "dan" )
teamB = setOf( "bob", "carol", "eve" )

sharedMembers = teamA.intersection( teamB )
// {bob, carol}

// Operator syntax: *
sharedMembers = teamA * teamB
</pre>

### Difference -- what's in A but not in B {#h3-17-difference-what-s-in-a-but-not-in-b}

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">allUsers     = setOf( "alice", "bob", "carol", "dan", "eve" )
activeUsers  = setOf( "alice", "carol", "eve" )

inactiveUsers = allUsers.difference( activeUsers )
// {bob, dan}

// Operator syntax: -
inactiveUsers = allUsers - activeUsers
</pre>

### Symmetric Difference -- what's in either but not both {#h3-18-symmetric-difference-what-s-in-either-but-not-both}

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">lastWeekUsers = setOf( "alice", "bob", "carol" )
thisWeekUsers = setOf( "bob", "carol", "dan", "eve" )

// Who joined or left?
changed = lastWeekUsers.symmetricDifference( thisWeekUsers )
// {alice, dan, eve}

// Operator syntax: ^
changed = lastWeekUsers ^ thisWeekUsers
</pre>

### Operators accept "loose" right-hand operands {#h3-19-operators-accept-loose-right-hand-operands}

You don't need to convert everything to a Set first:

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">base = setOf( 1, 2, 3, 4, 5 )

result = base + [ 6, 7 ]         // Set + Array
result = base * "3,4,5,6"        // Set * comma-list string
result = base - (1..2)           // Set - Range

// Compound assignment operators
base -= set{ 1, 2 }
// base is now {3, 4, 5}

base *= [ 3, 4 ]
// base is now {3, 4}
</pre>

> "
>
> When neither operand is a `Set`, operators fall through to  
>
> standard math: `2 + 3 = 5`, `4 ** 3 = 12`,  
> `2 ^ 3 = 8`. Your existing arithmetic code is safe.

Functional Programming Pipeline {#h2-20-functional-programming-pipeline}
------------------------------------------------------------------------

`BoxSet` ships with the same functional vocabulary you know from Arrays. Every operation returns a new Set (or a scalar), keeping the original untouched.

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">scores = setNew( type="sorted", values=[ 55, 72, 88, 91, 43, 88, 100 ] )
// Stored as: {43, 55, 72, 88, 91, 100}

// map -- transform every element, get a new Set back
bonusScores = scores.map( s -&gt; s + 5 )
// {48, 60, 77, 93, 96, 105}

// filter -- keep matching elements
passing = scores.filter( s -&gt; s &gt;= 60 )
// {72, 88, 91, 100}

// reject -- the inverse of filter
failing = scores.reject( s -&gt; s &gt;= 60 )
// {43, 55}

// reduce -- collapse to a single value
total = scores.reduce( ( acc, s ) =&gt; acc + s, 0 )
// 449

average = total / scores.size()
// 74.83...

// every -- do all elements satisfy a predicate?
allPassing = scores.every( s -&gt; s &gt;= 60 )
// false

// some -- does at least one element satisfy a predicate?
hasA = scores.some( s -&gt; s &gt;= 90 )
// true

// none -- do zero elements satisfy a predicate?
noneNegative = scores.none( s -&gt; s &lt; 0 )
// true

// find -- first element matching a predicate
firstHigh = scores.find( s -&gt; s &gt;= 90 )
// 91 (or 100, iteration order not guaranteed for HashSet -- use sorted/linked for predictability)
</pre>

Chaining works naturally:

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">result = setOf( 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 )
    .filter( n -&gt; n % 2 == 0 )    // evens: {2, 4, 6, 8, 10}
    .map( n -&gt; n * n )            // squares: {4, 16, 36, 64, 100}
    .reduce( ( acc, n ) =&gt; acc + n, 0 )
// 220
</pre>

Real-World Scenarios {#h2-21-real-world-scenarios}
--------------------------------------------------

### 1. Role-Based Access Control {#h3-22-1-role-based-access-control}

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">class PermissionService {

    function canAccess( required userRoles, required string resource ) {
        resourcePermissions = getResourcePermissions( resource )

        // What roles does this user have that grant access?
        return !userRoles.intersection( resourcePermissions ).isEmpty()
    }

    function mergeRoles( required userRoles, required groupRoles ) {
        // A user gets the union of their personal roles and group roles
        return userRoles.union( groupRoles )
    }

    function getEffectiveDenials( required userRoles, required deniedRoles ) {
        // Roles the user has that are explicitly denied on this resource
        return userRoles.intersection( deniedRoles )
    }

    private function getResourcePermissions( required string resource ) {
        return {
            "/admin"   : setOf( "admin", "superadmin" ),
            "/reports" : setOf( "admin", "analyst", "manager" ),
            "/api"     : setOf( "developer", "admin" )
        }[ resource ] ?: setNew()
    }

}

svc = new PermissionService()

userRoles  = setOf( "editor", "analyst", "viewer" )
groupRoles = setOf( "manager", "viewer" )

effective = svc.mergeRoles( userRoles, groupRoles )
// {editor, analyst, viewer, manager}

canSeeReports = svc.canAccess( effective, "/reports" )
// true (analyst or manager match)

canSeeAdmin = svc.canAccess( effective, "/admin" )
// false
</pre>

### 2. Tag Deduplication and Taxonomy Intersection {#h3-23-2-tag-deduplication-and-taxonomy-intersection}

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Content tagging system
post1Tags = setNew( type="linked", values=[ "boxlang", "jvm", "performance", "oss" ] )
post2Tags = setNew( type="linked", values=[ "jvm", "java", "boxlang", "interop" ] )
post3Tags = setNew( type="linked", values=[ "performance", "caching", "redis", "oss" ] )

// Tags that appear in multiple posts
commonTagsP1P2 = post1Tags * post2Tags
// {boxlang, jvm}

// All unique tags across the content library
allTags = post1Tags + post2Tags + post3Tags
// {boxlang, jvm, performance, oss, java, interop, caching, redis}

// Tags unique to post1 -- good for "exclusive" badge
exclusiveToPost1 = post1Tags - post2Tags - post3Tags
// {boxlang} -- only "boxlang" appears in p1 and not both others... actually varies

// Find posts that share at least 2 tags with post1 (related posts)
relatedThreshold = 2
isRelated = ( post1Tags * post2Tags ).size() &gt;= relatedThreshold
// true
</pre>

### 3. Dataset Change Detection {#h3-24-3-dataset-change-detection}

A common ETL pattern: what changed between two snapshots of data?

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">function detectChanges( required previousIds, required currentIds ) {
    prevSet = previousIds.toSet()
    currSet = currentIds.toSet()

    return {
        "added"     : currSet - prevSet,     // new since last run
        "removed"   : prevSet - currSet,     // gone since last run
        "unchanged" : prevSet * currSet,     // in both
        "changed"   : prevSet ^ currSet      // anything that moved
    }
}

yesterdayUsers = queryGetColumn( getYesterdayQuery(), "user_id" )
todayUsers     = queryGetColumn( getTodayQuery(), "user_id" )

diff = detectChanges( yesterdayUsers, todayUsers )

writeDump( "New signups today: #diff.added.size()#" )
writeDump( "Churned users: #diff.removed.size()#" )

// Send welcome emails only to genuinely new users
diff.added.each( userId =&gt; {
    emailService.sendWelcome( userId )
} )
</pre>

### 4. URL Deduplication Pipeline with Functional Chaining {#h3-25-4-url-deduplication-pipeline-with-functional-chaining}

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">class CrawlerPipeline {

    property name="visited"   type="Set"
    property name="queued"    type="Set"
    property name="blacklist" type="Set"

    function init() {
        variables.visited   = setNew( type="linked" )
        variables.queued    = setNew( type="linked" )
        variables.blacklist = setOf( "login", "logout", "admin" )
        return this
    }

    function enqueue( required array urls ) {
        // Normalize, reject blacklisted paths, exclude already visited
        fresh = urls
            .toSet( "linked" )                                // deduplicate
            .filter( url -&gt; !isBlacklisted( url ) )          // drop blacklisted
            .difference( variables.visited )                  // drop already seen
            .difference( variables.queued )                   // drop already queued

        variables.queued.addAll( fresh.toArray() )
        return fresh.size()
    }

    function processNext() {
        if ( variables.queued.isEmpty() ) return

        // LinkedHashSet gives us FIFO via toArray()[1]
        target  = variables.queued.toArray().first()
        variables.queued.remove( target )
        variables.visited.add( target )

        return crawl( target )
    }

    function stats() {
        return {
            "visited" : variables.visited.size(),
            "queued"  : variables.queued.size(),
            "overlap" : ( variables.visited * variables.queued ).size()    // should always be 0
        }
    }

    private function isBlacklisted( required string target ) {
        return variables.blacklist.some( b -&gt; target.findNoCase( b ) &gt; 0 )
    }

}
</pre>

Case Sensitivity and Numeric Normalization {#h2-26-case-sensitivity-and-numeric-normalization}
----------------------------------------------------------------------------------------------

By default, BoxSet is **case-insensitive** for strings, matching BoxLang's general dynamic semantics:

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">s = setNew( values=[ "Hello", "hello", "HELLO", "hElLo" ] )
s.size()    // 1

s.contains( "HELLO" )    // true
s.contains( "hElLo" )    // true
</pre>

Opt in to case sensitivity when you need exact-case uniqueness:

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">tokens = setNew( caseSensitive=true, values=[ "Bearer", "bearer", "BEARER" ] )
tokens.size()    // 3

tokens.contains( "bearer" )    // true
tokens.contains( "Bearer" )    // true
tokens.contains( "beareR" )    // false
</pre>

Numeric normalization is independent of case sensitivity. `1`, `1L`, `1.0`, and 1.00 are always the same value in a Set:

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">nums = setNew( values=[ 1, 1.0, 1L, 1.00 ] )
nums.size()    // 1
</pre>

Java Interop {#h2-27-java-interop}
----------------------------------

Because `BoxSet` wraps `java.util.Set`, the integration story is clean in both directions.

### Wrapping an existing Java Set {#h3-28-wrapping-an-existing-java-set}

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import java.util.HashSet

javaSet = createObject( "java", "java.util.HashSet" ).init()
javaSet.add( "a" )
javaSet.add( "b" )

// Wrap it -- no copy, mutations propagate
bxSet = javaSet castAs "Set"
bxSet.add( "c" )

javaSet.contains( "c" )    // true -- same backing object
bxSet.size()               // 3
</pre>

### Struct key and value sets {#h3-29-struct-key-and-value-sets}

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">config = {
    "host"     : "localhost",
    "port"     : 5432,
    "ssl"      : true,
    "database" : "myapp"
}

// Keys as a Set
keys = config.keySet()
keys.contains( "port" )     // true

// Values as a Set (deduplicated)
values = config.valueSet()

// Useful for checking whether any key overlaps with a forbidden list
forbidden = setOf( "password", "secret", "token", "key" )
hasSensitiveKeys = !keys.isDisjointFrom( forbidden )
// false -- none of our keys are in the forbidden list
</pre>

Any Java library method returning a `java.util.Set` -- Spring Security granted authorities, JPA fetch results, Guava ImmutableSet -- works directly with BoxLang Set BIFs and member functions.

Unmodifiable Sets {#h2-30-unmodifiable-sets}
--------------------------------------------

When you need an immutable contract -- configuration, constants, lookup tables:

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">ALLOWED_METHODS = setOf( "GET", "POST", "PUT", "DELETE", "PATCH" ).toUnmodifiable()

ALLOWED_METHODS.size()           // 5
ALLOWED_METHODS.contains( "GET" )  // true

// Mutation attempts throw UnmodifiableException at runtime
ALLOWED_METHODS.add( "TRACE" )   // throws!

// Thaw to get a fresh mutable copy when you need to extend it
extended = ALLOWED_METHODS.toModifiable()
extended.add( "HEAD" )
extended.size()    // 6 -- ALLOWED_METHODS still has 5
</pre>

This pairs perfectly with constants declared in a BoxLang class:

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">class HttpConstants {

    SAFE_METHODS    = setOf( "GET", "HEAD", "OPTIONS" ).toUnmodifiable()
    UNSAFE_METHODS  = setOf( "POST", "PUT", "DELETE", "PATCH" ).toUnmodifiable()
    ALL_METHODS     = ( SAFE_METHODS + UNSAFE_METHODS ).toUnmodifiable()

    function isSafe( required string method ) {
        return SAFE_METHODS.contains( method.uCase() )
    }

}
</pre>

JSON Serialization {#h2-31-json-serialization}
----------------------------------------------

Sets serialize to JSON arrays, which means they round-trip cleanly with any JSON-consuming API:

<pre class="EnlighterJSRAW EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">s = setNew( type="linked", values=[ "boxlang", "jvm", "oss" ] )

json = jsonSerialize( s )
// ["boxlang","jvm","oss"]

// Or via member function
json = s.toJSON()

// Nested in a struct
payload = {
    "user"  : "alice",
    "roles" : setOf( "editor", "viewer" ),
    "tags"  : setNew( type="linked", values=[ "premium", "beta" ] )
}
jsonSerialize( payload )
// {"user":"alice","roles":["editor","viewer"],"tags":["premium","beta"]}
</pre>

Quick BIF Reference {#h2-32-quick-bif-reference}
------------------------------------------------

| BIF                                           | Member Function                      | Purpose                |
|:----------------------------------------------|:-------------------------------------|:-----------------------|
| `setNew( [type], [values], [caseSensitive] )` | --                                   | Create new Set         |
| `setOf( ...values )`                          | --                                   | Create from varargs    |
| `boxSetAdd( set, value ) `                    | `s.add( v ) `                        | Add one element        |
| `boxSetAddAll( set, collection ) `            | `s.addAll( col )`                    | Add many elements      |
| `boxSetRemove( set, value )`                  | `s.remove( v )`                      | Remove one element     |
| `boxSetRemoveAll( set, collection )`          | `s.removeAll( col )`                 | Remove many elements   |
| `boxSetRetainAll( set, collection )`          | `s.retainAll( col )`                 | Keep only specified    |
| `boxSetClear( set )`                          | `s.clear() `                         | Remove all             |
| `boxSetContains( set, value )`                | `s.contains( v )`                    | Membership test        |
| `boxSetContainsAll( set, col )`               | `s.containsAll( col )`               | All-membership test    |
| `boxSetIsEmpty( set ) `                       | `s.isEmpty()`                        | Empty check            |
| `boxSetEquals( a, b )`                        | `a.equals( b ) `                     | Equality               |
| `boxSetIsSubsetOf( a, b ) `                   | `a.isSubsetOf( b ) `                 | Subset test            |
| `boxSetIsSupersetOf( a, b ) `                 | `a.isSupersetOf( b )`                | Superset test          |
| `boxSetIsDisjointFrom( a, b )`                | ` a.isDisjointFrom( b )`             | No-overlap test        |
| `boxSetUnion( a, b ) `                        | `a.union( b ) / a + b `              | All elements           |
| `boxSetIntersection( a, b ) `                 | `a.intersection( b ) / a * b`        | Common elements        |
| `boxSetDifference( a, b )`                    | `a.difference( b ) / a - b`          | A minus B              |
| `boxSetSymmetricDifference( a, b )`           | `a.symmetricDifference( b ) / a ^ b` | Either not both        |
| `boxSetEach( set, cb )`                       | `s.each( cb )`                       | Iterate                |
| `boxSetMap( set, cb ) `                       | `s.map( cb )`                        | Transform              |
| `boxSetFilter( set, cb )`                     | `s.filter( cb )`                     | Keep matching          |
| `boxSetReject( set, cb ) `                    | `s.reject( cb )`                     | Remove matching        |
| `boxSetReduce( set, cb, init )`               | `s.reduce( cb, init )`               | Fold to value          |
| `boxSetEvery( set, cb )`                      | `s.every( cb ) `                     | All match?             |
| `boxSetSome( set, cb )`                       | `s.some( cb )`                       | Any match?             |
| `boxSetNone( set, cb ) `                      | `s.none( cb )`                       | None match?            |
| `boxSetFind( set, cb )`                       | `s.find( cb )`                       | First match            |
| `boxSetSize( set )`                           | `s.size() / s.len() `                | Element count          |
| `boxSetToArray( set ) `                       | `s.toArray() `                       | Convert to Array       |
| `boxSetToList( set, [delim] )`                | `s.toList( [delim] ) `               | Convert to list string |

Wrap Up {#h2-33-wrap-up}
------------------------

`BoxSet` is not a cosmetic feature. It's a fundamental collection type that was missing from the language and is now present everywhere you need it: in literal syntax, in operators, in the functional pipeline, in JSON, in Java interop, and in the type system itself.

The operator overloads alone (`+`, `-`, `*`, `^`) turn multi-step collection algebra into single expressions. The three backing variants mean you always get the right performance characteristics for your use case. And the full functional pipeline keeps your code declarative and composable instead of imperative and fragile.

`BoxSet` ships in **BoxLang 1.14.0**. Update, try the literal syntax, and reach for a Set the next time you catch yourself deduplicating an array with a loop.

**Resources**:

* [BoxSet Documentation](https://boxlang.ortusbooks.com/boxlang-language/syntax/sets "BoxSet Documentation")
* [Set BIF Reference](https://boxlang.ortusbooks.com/boxlang-language/reference/built-in-functions/set "Set BIF Reference")
* [BoxLang Downloads](https://boxlang.io/?_gl=1*f0apz8*_gcl_au*MzI0MjI3ODM0LjE3NzU1MDUwMDA.*_ga*MTQ4MjQzODA2Ny4xNzc1NTA1MDAw*_ga_D1P6P1YYT0*czE3ODE2MTg5OTYkbzU3JGcxJHQxNzgxNjE5MDcwJGo2MCRsMCRoMA..*_ga_663JFQ7YGX*czE3ODE2ODk4NjMkbzUzJGcwJHQxNzgxNjg5ODYzJGo2MCRsMCRoMA.. "BoxLang Downloads")
* [ForgeBox](https://forgebox.io/ "ForgeBox")
* [Community Slack](https://boxteam.ortussolutions.com/ "Community Slack")

*BoxLang is built by [Ortus Solutions](https://www.ortussolutions.com/ "Ortus Solutions") -- the team behind ColdBox, CommandBox, and 350+ open-source libraries. BoxLang is a modern dynamic JVM language designed to run everywhere: web, Lambda, CLI, desktop, Android, and beyond.*
