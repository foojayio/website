---
title: "BoxLang 1.14.0 : Introducing Inner Classes"
slug: "boxlang-1-14-0-introducing-inner-classes"
date: "2026-06-11T10:29:32+00:00"
lastmod: "2026-06-11T10:29:34+00:00"
description: "BoxLang has always embraced a simple truth: the way you organize code shapes the way you think about problems. For a long time, if you needed a helper - by Cristobal Escobar"
authors:
  - "cristobal-escobar"
image: "https://foojay.io/wp-content/uploads/2026/06/BoxLang-release-1.14.0-1701-x-1701-px-1024x1024.png"
categories:
  - "BoxLang"
  - "Design Patterns"
  - "Developer Tools"
  - "Java"
  - "Java Core"
tags:
related_posts:
  - "boxlang-1-14-0-boxset-is-here-boxlangs-new-first-class-set-type"
  - "boxlang-1-14-0-navigate-anything-jsonpath-comes-to-boxlangs-datanavigator"
  - "boxlang-1-14-0-query-transformers-take-full-control-of-your-query-results"
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
enlighterjs: true
frozen: false
---

![](/images/posts/2026/06/boxlang-1-14-0-introducing-inner-classes/BoxLang-release-1.14.0-700x394.png)

BoxLang has always embraced a simple truth: the way you organize code shapes the way you think about problems. For a long time, if you needed a helper class, you needed a file. One class, one `.bx` file, no exceptions. This also stemmed from the CFML days. That's clean and predictable, but it creates real friction when a class is tightly coupled to exactly one caller and has no business existing anywhere else.

BoxLang 1.14.0 removes that friction entirely. You can now define classes **inline** - inside scripts, inside templates, and nested inside other classes. No separate file required. No ceremony. The class lives exactly where it belongs.

This post covers both flavors of locally defined classes shipped in 1.14.0: **Template Classes** and **Inner Classes**. You can read more about them in our docs:

* <https://boxlang.ortusbooks.com/boxlang-language/classes/inner-classes>
* <https://boxlang.ortusbooks.com/boxlang-language/classes/template-classes>

The Two Flavors {#h2-0-the-two-flavors}
---------------------------------------

Before diving in, a quick orientation:

|   Feature    |                    Template Classes                    |            Inner Classes             |
|--------------|--------------------------------------------------------|--------------------------------------|
| Defined in   | `.bxs` scripts or `.bxm` template `<bx:script>` blocks | Inside a `.bx` class body            |
| Scope        | Local to that compilation unit                         | Accessible externally via `$` syntax |
| Hoisting     | Yes                                                    | Yes                                  |
| Inheritance  | Full                                                   | Full                                 |
| Interfaces   | Full                                                   | Full                                 |
| Java interop | Full                                                   | Full                                 |

Both share the same fundamental capability: define a class right where you need it, with zero boilerplate. The difference is where you need it. Please also note that in BoxLang you cannot define classes using template markup and this feature applies ONLY to BoxLang templates: `bx, bxs, bxm`

Template Classes {#h2-1-template-classes}
-----------------------------------------

A template class is a named class declared inline inside a `.bxs` script or a `.bxm` template's \<bx:script\> block.

<pre class="EnlighterJSRAW" data-enlighter-language="java">class Greeter {
    function greet( name ) {
        return "Hello, " &amp; name &amp; "!"
    }
}

result = new Greeter().greet( "World" )
// → "Hello, World!"
</pre>

1

That is a complete, fully functional BoxLang script. No imports, no file path, no module resolution. The class is defined and used in the same compilation unit.

### Hoisting {#h3-2-hoisting}

Template classes are **hoisted** to the top of their compilation unit. You can instantiate a class before its textual definition appears - which keeps the "main logic first" narrative flow that makes scripts readable:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Use before definition - perfectly valid
result = new Greeter().greet( "BoxLang" )

class Greeter {
    function greet( name ) {
        return "Hello, " &amp; name &amp; "!"
    }
}
</pre>

### Multiple Classes in One Script {#h3-3-multiple-classes-in-one-script}

Multiple template classes coexist naturally. They can even reference each other:

<pre class="EnlighterJSRAW" data-enlighter-language="java">class Adder {
    function add( a, b ) {
        return a + b
    }
}

class Multiplier {
    function multiply( a, b ) {
        return a * b
    }
}

adder      = new Adder()
multiplier = new Multiplier()
result     = multiplier.multiply( adder.add( 2, 3 ), 4 )
// → 20
</pre>

### Properties, Constructors, and Static Members {#h3-4-properties-constructors-and-static-members}

Template classes are full-featured BoxLang classes. Properties, `init()` constructors, and static blocks all work exactly as they do in file-based classes:

<pre class="EnlighterJSRAW" data-enlighter-language="java">class Counter {
    property numeric count default=0

    function increment() {
        variables.count++
    }

    function getCount() {
        return variables.count
    }
}

c = new Counter()
c.increment()
c.increment()
c.increment()
c.getCount()    // → 3
</pre>

Static members work too - useful for shared constants and utility methods:

<pre class="EnlighterJSRAW" data-enlighter-language="java">class MathUtil {
    static {
        PI = 3.14159265358979
    }

    static function circleArea( radius ) {
        return MathUtil::PI * radius ^ 2
    }
}

MathUtil::circleArea( 5 )    // → ~78.54
</pre>

### Inheritance {#h3-5-inheritance}

Template classes can extend other template classes defined in the same script, including multi-level chains and `super` delegation:

<pre class="EnlighterJSRAW" data-enlighter-language="java">abstract class Shape {
    abstract function area()

    function describe() {
        return "Area: " &amp; this.area()
    }
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

new Circle( 5 ).describe()    // → "Area: 78.53975"
</pre>

### Java Interoperability {#h3-6-java-interoperability}

Template classes can implement Java interfaces and extend Java classes, making them a clean fit for interop patterns:

<pre class="EnlighterJSRAW" data-enlighter-language="java">class MyRunnable implements="java:java.lang.Runnable" {
    property name="didRun" default=false

    void function run() {
        variables.didRun = true
    }
}

r      = new MyRunnable()
thread = new java:Thread( r )
thread.start()
thread.join()
r.getDidRun()    // → true
</pre>

### Imports Are Shared {#h3-7-imports-are-shared}

Template classes inherit the enclosing script's imports. Java types are available directly without any extra ceremony:

<pre class="EnlighterJSRAW" data-enlighter-language="java">import java.util.Date

class Event {
    function init( name ) {
        variables.name      = name
        variables.timestamp = new Date()
        return this
    }

    function getInfo() {
        return variables.name &amp; " at " &amp; variables.timestamp.toString()
    }
}

new Event( "Launch" ).getInfo()    // → "Launch at Wed Jun 03 ..."
</pre>

### Template Classes in `.bxm` Files {#h3-8-template-classes-in-bxm-files}

Template classes work inside \<bx:script\> islands in markup templates, bringing the same capability to your view layer:

<pre class="EnlighterJSRAW" data-enlighter-language="java">&lt;bx:script&gt;
    class Point {
        function init( x, y ) {
            variables.x = x
            variables.y = y
            return this
        }

        function toString() {
            return "(" &amp; variables.x &amp; "," &amp; variables.y &amp; ")"
        }
    }

    result = new Point( 3, 4 ).toString()
&lt;/bx:script&gt;

&lt;bx:output&gt;#result#&lt;/bx:output&gt;
</pre>

Inner Classes {#h2-9-inner-classes}
-----------------------------------

An inner class is a named class declared **inside the body of another class** in a `.bx` file. Where template classes are scoped to a single compilation unit, inner classes are part of their enclosing class's compiled output and are accessible from outside via the `$` separator syntax.

<pre class="EnlighterJSRAW" data-enlighter-language="java">// models/Container.bx
class Container {

    class Widget {
        function init( label ) {
            variables.label = label
            return this
        }

        function getLabel() {
            return variables.label
        }
    }

    function createWidget( label ) {
        return new Widget( label )
    }

}

c = new Container()
w = c.createWidget( "header-nav" )
w.getLabel()    // → "header-nav"
</pre>

### Hoisting in Inner Classes {#h3-10-hoisting-in-inner-classes}

Like template classes, inner classes are hoisted within the class body. You can instantiate an inner class in a function that appears before the inner class definition:

<pre class="EnlighterJSRAW" data-enlighter-language="java">class Outer {

    function getWidget() {
        return new Widget()    // Works - Widget is hoisted
    }

    class Widget {
        function getName() {
            return "widget"
        }
    }

}

new Outer().getWidget().getName()    // → "widget"
</pre>

### Multiple and Nested Inner Classes {#h3-11-multiple-and-nested-inner-classes}

A class can contain as many inner classes as it needs. Inner classes can themselves contain inner classes:

<pre class="EnlighterJSRAW" data-enlighter-language="java">class Outer {

    class First {

        class Second {
            function getDepth() {
                return "second"
            }
        }

        function getSecond() {
            return new Second()
        }

        function getDepth() {
            return "first"
        }
    }

    function getFirst() {
        return new First()
    }
}

outer = new Outer()
first = outer.getFirst()
first.getDepth()              // → "first"
first.getSecond().getDepth()  // → "second"
</pre>

### Inheritance Between Inner Classes {#h3-12-inheritance-between-inner-classes}

Inner classes can extend other inner classes in the same outer class, enabling polymorphic patterns without the overhead of separate files:

<pre class="EnlighterJSRAW" data-enlighter-language="java">class Zoo {

    class Animal {
        function speak() {
            return "..."
        }
    }

    class Dog extends="Animal" {
        function speak() {
            return "Woof!"
        }
    }

    class Cat extends="Animal" {
        function speak() {
            return "Meow!"
        }
    }

    function getDog() { return new Dog() }
    function getCat() { return new Cat() }
}

zoo = new Zoo()
zoo.getDog().speak()    // → "Woof!"
zoo.getCat().speak()    // → "Meow!"
</pre>

### Accessing Outer Class Statics {#h3-13-accessing-outer-class-statics}

Inner classes can reach back into their enclosing class's static members via dot or double-colon notation:

<pre class="EnlighterJSRAW" data-enlighter-language="java">class Config {

    static {
        MAX_POOL_SIZE = 50
        APP_NAME      = "MyApp"
    }

    class Validator {
        function validate( size ) {
            return size &lt;= Config::MAX_POOL_SIZE
        }

        function getAppName() {
            return Config.APP_NAME
        }
    }
}

v = new Config().getValidator()
v.validate( 20 )       // → true
v.validate( 100 )      // → false
v.getAppName()         // → "MyApp"
</pre>

### External Access via `$` Syntax {#h3-14-external-access-via-syntax}

Inner classes are compiled as sibling JVM classes with `$`-delimited names. This means they are accessible from anywhere - not just from within the outer class:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Fully qualified external instantiation
widget = new models.Container$Widget( "my-widget" )
widget.getLabel()    // → "my-widget"

// Static access on a nested inner class
second = new models.Outer$First$Second()
second.getDepth()    // → "second"
</pre>

### Importing Inner Classes {#h3-15-importing-inner-classes}

You can import inner classes directly, with or without an alias:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Direct import
import models.Container$Widget

widget = new Widget( "imported" )
widget.getLabel()    // → "imported"

// Import with alias
import models.Container$Widget as NavWidget

nav = new NavWidget( "top-nav" )
nav.getLabel()    // → "top-nav"
</pre>

You can also reference inner classes via the outer class name after importing it:

<pre class="EnlighterJSRAW" data-enlighter-language="java">import models.Container

// Both of these work
widgetClass = Container.Widget
widgetClass = Container::Widget

w = new widgetClass( "via-reference" )
</pre>

### Java Interoperability {#h3-16-java-interoperability}

Inner classes are especially powerful for Java interop patterns like `Iterator` implementations, where the inner class is tightly coupled to its parent but needs to satisfy a Java interface contract:

<pre class="EnlighterJSRAW" data-enlighter-language="java">class BoxList implements="java:java.lang.Iterable" {

    property Array items

    function init( array items = [] ) {
        variables.items = arguments.items
        return this
    }

    function iterator() {
        return new BoxIterator( variables.items )
    }

    class BoxIterator implements="java:java.util.Iterator" {

        property name="data"
        property name="position"

        function init( array data ) {
            variables.data     = arguments.data
            variables.position = 0
            return this
        }

        boolean function hasNext() {
            return variables.position &lt; variables.data.len()
        }

        function next() {
            if ( !hasNext() ) {
                throw(
                    type    = "java.util.NoSuchElementException",
                    message = "No more elements"
                )
            }
            variables.position++
            return variables.data[ variables.position ]
        }
    }
}

list = new BoxList( [ "a", "b", "c" ] )
iter = list.iterator()
while ( iter.hasNext() ) {
    println( iter.next() )
}
// → a
// → b
// → c
</pre>

Introspection and Metadata {#h2-17-introspection-and-metadata}
--------------------------------------------------------------

Both template classes and inner classes expose full metadata through BoxLang's standard reflection API.

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Template class metadata
meta = getMetadata( new Circle( 5 ) )
meta.name          // → "Circle"
meta.type          // → "Class"
meta.properties    // → array of property descriptors
meta.functions     // → array of function descriptors

// Inner class metadata
widget = new models.Container$Widget( "test" )
meta   = getMetadata( widget )
meta.name           // → "models.Container$Widget"
meta.simpleName     // → "Widget"
meta.enclosingClass // → "models.Container"
meta.innerClasses   // → {} (empty unless Widget itself has inner classes)
</pre>

`isInstanceOf()` works naturally with both the simple name and the fully qualified `$` path:

<pre class="EnlighterJSRAW" data-enlighter-language="java">isInstanceOf( widget, "Widget" )                    // → true
isInstanceOf( widget, "models.Container$Widget" )   // → true
</pre>

When to Use Which {#h2-18-when-to-use-which}
--------------------------------------------

**Use template classes** when:

* Working in a `.bxs` script or REPL session
* Prototyping on [try.boxlang.io](https://try.boxlang.io/ "try.boxlang.io")
* Defining a helper class that is consumed in one template or script and has no standalone value
* Writing test fixtures inline in TestBox specs

**Use inner classes** when:

* The helper class is tightly coupled to a specific parent class in a .bx file
* You want external code to be able to instantiate or import the inner class independently
* Implementing Java interface contracts (iterators, comparators, runnables) that belong to a specific parent
* Building builder patterns, strategy variants, or value types that only make sense in context

**Use file-based classes** when:

* The class is shared across multiple files or modules
* The class is a primary domain model, service, or handler
* Reusability and discoverability matter more than co-location

Getting Started {#h2-19-getting-started}
----------------------------------------

Both features are available in **BoxLang 1.14.0** with no configuration required.

Update via CommandBox:

<pre class="EnlighterJSRAW" data-enlighter-language="java">box update boxlang
</pre>

Or grab the latest from [boxlang.io](https://boxlang.io/?_gl=1*1d59xdz*_gcl_au*MzI0MjI3ODM0LjE3NzU1MDUwMDA.*_ga*MTQ4MjQzODA2Ny4xNzc1NTA1MDAw*_ga_663JFQ7YGX*czE3ODEwOTcyMjUkbzQ4JGcxJHQxNzgxMDk5NTg5JGo2MCRsMCRoMA..*_ga_D1P6P1YYT0*czE3ODEwOTcyMjUkbzUzJGcxJHQxNzgxMDk5NTg5JGo2MCRsMCRoMA.. "boxlang.io").

Full documentation:

* [Template Classes](https://boxlang.ortusbooks.com/boxlang-language/classes/template-classes "Template Classes")
* [Inner Classes](https://boxlang.ortusbooks.com/boxlang-language/classes/inner-classes "Inner Classes")
* [BoxLang 1.14.0 Release Notes](https://boxlang.ortusbooks.com/readme/release-history/1.14.0 "BoxLang 1.14.0 Release Notes")

We would love to hear how you are using locally defined classes in your BoxLang applications. Share what you build in the [Ortus Community](https://community.ortussolutions.com/c/boxlang/42 "Ortus Community").

If you want extended capabilities - `bx-ai`, `bx-mcp`, `bx-jwt`, `bx-redis`, and the full BoxLang+ module ecosystem - visit [boxlang.io/plans](https://boxlang.io/plans?_gl=1*1rlxd5r*_gcl_au*MzI0MjI3ODM0LjE3NzU1MDUwMDA.*_ga*MTQ4MjQzODA2Ny4xNzc1NTA1MDAw*_ga_663JFQ7YGX*czE3ODEwOTcyMjUkbzQ4JGcxJHQxNzgxMDk5NTg5JGo2MCRsMCRoMA..*_ga_D1P6P1YYT0*czE3ODEwOTcyMjUkbzUzJGcxJHQxNzgxMDk5NTg5JGo2MCRsMCRoMA.. "boxlang.io/plans") to see what fits your team.
