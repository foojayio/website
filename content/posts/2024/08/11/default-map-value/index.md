---
title: "Default map value"
date: "2024-08-11T14:16:49+00:00"
lastmod: "2026-09-21T06:03:32+00:00"
description: "In this post, I'll explain how to provide a default value when querying an absent key in a hash map in different programming languages. Java Let's start…"
canonical: "https://blog.frankel.ch/default-map-value/"
authors:
  - "nicolas-frankel"
image: "code-8779051.jpg"
categories:
  - "Java"
  - "Java Beginner"
related_posts:
  - "on-programming-languages-targets-and-platforms"
  - "making-illegal-state-unrepresentable"
  - "does-language-still-matter-in-the-age-of-ai-yes-but-the-tradeoff-has-changed"
  - "research-measuring-energy-consumption-in-programming-languages-for-ai-applications"
frozen: false
---

In this post, I'll explain how to provide a default value when querying an absent key in a hash map in different programming languages.

## Java

Let's start with Java, my first professional programming language.

In older versions, retrieving a value from a map required using the `get()` method:

```java
Map map = new HashMap();                                 //1
Object value = map.get(new Object());                    //2
if (value == null) {
    value = "default";                                   //3
}
```

1. Initialize an empty map
2. Attempt to retrieve a non-existent key
3. Assign a default value if the key is absent

With Java 1.8, the `Map` interface introduced a more concise way to handle absent keys:

```java
var map = new HashMap<Object, String>();
var value = map.getOrDefault(new Object(), "default");   //1
```

1. Retrieve the value with a default in one step

## Kotlin

Kotlin provides several approaches to retrieve values from a map:

* `get()` and `getOrDefault()` function just like their Java counterparts.
* `getValue()` throws an exception if the key is missing.
* `getOrElse()` accepts a lambda to provide a default value lazily.

```kotlin
val map = mapOf<Any, String>()
val default = map.getOrDefault("absent", "default")      //1
val lazyDefault = map.getOrElse("absent") { "default" }  //2
```

1. Retrieve the default value
2. Lazily evaluate the default value

## Python

Python is less forgiving than Java when handling absent keys—it raises a `KeyError`:

```python
map = {}
value = map['absent']                                    #1
```

1. Raises a `KeyError`

To avoid this, Python offers the `get()` method:

```python
map = {}
value = map.get('absent', 'default')                     #1
```

Alternatively, Python's `collections.defaultdict` allows setting a default for all absent keys:

```python
from collections import defaultdict
map = defaultdict(lambda: 'default')                     #1
value = map['absent']
```

1. Automatically provide a default value for any absent key

## Ruby

Ruby's default behavior returns `nil` for absent keys:

```ruby
map = {}
value = map['absent']
```

For a default value, use the `fetch` method:

```ruby
map = {}
value = map.fetch('absent', 'default')                  #1
```

1. Provide a default value for the absent key

Ruby also supports a more flexible approach with closures:

```ruby
map = {}
value = map.fetch('absent') { |key| key }               #1
```

1. Return the queried key instead of a constant

## Lua

My experience with Lua is relatively new, having picked it up for Apache APISIX. Let's start with Lua's map syntax:

```lua
map = {}                                                --1
map["a"] = "A"
map["b"] = "B"
map["c"] = "C"
for k, v in pairs(map) do                               --2
  print(k, v)                                           --3
end
```

1. Initialize a new map
2. Iterate over key-value pairs
3. Print each key-value pair

Fun fact: the syntax for tables is the same as for maps:

```lua
table = {}                                              --1
table[0] = "zero"
table[1] = "one"
table[2] = "two"
for k,v in ipairs(table) do                             --2
  print(k, v)                                           --3
end
```

1. Initialize a new map
2. Loop over the pairs of key values
3. Print the following:

       1   one
       2   two

   Lua arrays start at index 0!

We can mix and match indices and keys. The syntax is similar, but there's no difference between a table and a map. Indeed, Lua calls the data structure a table:

```lua
something = {}                                              
something["a"] = "A"
something[1] = "one"
something["b"] = "B"
for k,v in pairs(something) do
  print(k, v)
end
```

The result is the following:

```
1	one
a	A
b	B
```

In Lua, absent keys return `nil` by default:

```lua
map = {}
value = map['absent']
```

To provide a default, Lua uses metatables and the `__index` metamethod:
> Metatables allow us to change the behavior of a table. For instance, using metatables, we can define how Lua computes the expression `a+b`, where `a` and `b` are tables. Whenever Lua tries to add two tables, it checks whether either of them has a metatable and whether that metatable has an `__add` field. If Lua finds this field, it calls the corresponding value (the so-called *metamethod*, which should be a function) to compute the sum.
>
> -- [Metatables and Metamethods](https://www.lua.org/pil/13.html)

Each table in Lua may have its own *metatable*.
> As I said earlier, when we access an absent field in a table, the result is **nil** . This is true, but it is not the whole truth. Such access triggers the interpreter to look for an `__index` metamethod: if there is no such method, as usually happens, then the access results in **nil**; otherwise, the metamethod will provide the result.
>
> -- [The `__index` Metamethod](https://www.lua.org/pil/13.4.1.html)

Here's how to use it:

```lua
table = {}                                              --1
mt = {}                                                 --2
setmetatable(table, mt)                                 --3
mt.__index = function (table, key)                      --4
  return key
end
default = table['absent']                               --5
```

1. Create the table
2. Create a metatable
3. Associate the metatable with the table
4. Define the `__index` function to return the absent key
5. The `__index` function is called because the key is absent

## Summary

This post explored how to provide default values when querying absent keys across various programming languages. Here's a quick summary:

|                      |       Scope       ||    Value     ||
| Programming language | Per call | Per map | Static | Lazy |
|----------------------|----------|---------|--------|------|
| Java                 | ❎        | ❌       | ❎      | ❌    |
| Kotlin               | ❎        | ❌       | ❎      | ❎    |
| Python               | ❎        | ❎       | ❌      | ❎    |
| Ruby                 | ❎        | ❌       | ❎      | ❎    |
| Lua                  | ❌        | ❎       | ❎      | ❌    |

*Originally published at [A Java Geek](https://blog.frankel.ch/default-map-value/) on August 11^th^, 2024*
