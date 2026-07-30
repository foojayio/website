---
title: "Extending Third-Party APIs in Different Languages | Foojay.io Today"
slug: "extending-third-party-apis-in-different-languages"
date: "2021-11-08T08:36:05+00:00"
lastmod: "2021-11-08T08:37:10+00:00"
description: "I'd like to describe how to add new behavior to an existing object/type. I won't use any reactive type to make it more general."
canonical: "https://blog.frankel.ch/extending-third-party-apis/"
authors:
  - "nicolas-frankel"
image: "/images/posts/2021/11/extending-third-party-apis-in-different-languages/puzzle-g3926dee11_1280.jpg"
categories:
  - "Kotlin"
  - "Research"
tags:
related_posts:
  - "kotlin-delegation"
  - "annotation-free-spring"
  - "debugging-tutorial-1-introduction-conditional-breakpoints-set-value"
  - "foojay-podcast-53"
enlighterjs: true
frozen: false
---

The need for shorter and shorter Time-To-Market requires us to integrate more and more third-party libraries. There's no time for the [NIH syndrom](https://en.wikipedia.org/wiki/Not_invented_here) anymore, if it ever was. While most of the time the library's API is ready to use, it may happen that one needs to "adapt" it to the codebase sometimes. How easy the adaptation is depends a lot on the language.

For example, in the JVM, there are a couple of Reactive-Programming libraries: RxJava, Project Reactor, Mutiny, and coroutines. You might need a library that uses types of one library, but you based your project on another.

In this article, I'd like to describe how to add new behavior to an existing object/type. I won't use any reactive type to make it more general but add `toTitleCase()` to `String`. When it exists, inheritance is **not** a solution as it creates a new type.

I apologize in advance that the below implementations are pretty simple: they are meant to highlight my point, not to handle corner cases, *e.g.*, empty strings, non-UTF 8, etc.

JavaScript {#h2-0-javascript}
-----------------------------

JavaScript is an interpreted dynamically- and weakly-typed language, which runs the World Wide Web - until WASM takes over? As far as I know, its design is unique, as it's prototype-based. A prototype is a mold for new "instances" of that type.

You can easily add properties, either state or behavior, to a prototype.

<pre class="EnlighterJSRAW" data-enlighter-language="js">Object.defineProperty(String.prototype, "toTitleCase", {
    value: function toTitleCase() {
        return this.replace(/\w\S*/g, function(word) {
            return word.charAt(0).toUpperCase() + word.substr(1).toLowerCase();
        });
    }
});

console.debug("OncE upOn a tImE in thE WEst".toTitleCase());</pre>

Note that objects created from this prototype *after* the call to `defineProperty` will offer the new property; objects created *before* won't.

Ruby {#h2-1-ruby}
-----------------

Ruby is an interpreted dynamically- and strongly-typed language. While not as popular as it once was with the Ruby On Rails framework, I still use it with the Jekyll system that powers this blog.

Adding methods or attributes to an existing class is pretty standard in the Ruby ecosystem. I found two mechanisms to add a method to an existing type in Ruby:

1. Use [class_eval](https://apidock.com/ruby/Module/class_eval):"Evaluates the string or block in the context of mod, except that when a block is given, constant/class variable lookup is not affected. This can be used to add methods to a class"
2. Just implement the method on the existing class.

Here's the code for the second approach:

<pre class="EnlighterJSRAW" data-enlighter-language="ruby">class String
  def to_camel_case()
    return self.gsub(/\w\S*/) {|word| word.capitalize()}
  end
end

puts "OncE upOn a tImE in thE WEst".to_camel_case()</pre>

Python {#h2-2-python}
---------------------

Python is an interpreted dynamically- and strongly-typed language. I guess every developer has heard of Python nowadays.

Python allows you to add functions to existing types - with limitations. [Let's try](https://www.online-python.com/yv52IK4Mux) with the `str` built-in type:

<pre class="EnlighterJSRAW" data-enlighter-language="python">import re

def to_title_case(string):
    return re.sub(
        r'\w\S*',
        lambda word: word.group(0).capitalize(),
        string)

setattr(str, 'to_title_case', to_title_case)

print("OncE upOn a tImE in thE WEst".to_title_case())</pre>

Unfortunately, the above code fails during execution:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Traceback (most recent call last):
  File "&lt;string&gt;", line 9, in &lt;module&gt;
TypeError: can't set attributes of built-in/extension type 'str'
</pre>

Because `str` is a *built-in* type, we cannot dynamically add behavior. We can update [the code](https://www.online-python.com/w4G0We7EYh) to cope with this limitation:

<pre class="EnlighterJSRAW" data-enlighter-language="python">import re

def to_title_case(string):
    return re.sub(
        r'\w\S*',
        lambda word: word.group(0).capitalize(),
        string)

class String(str):
    pass

setattr(String, 'to_title_case', to_title_case)

print(String("OncE upOn a tImE in thE WEst").to_title_case())</pre>

It now becomes possible to extend `String`, because it's a class we have created. Of course, it defeats the initial purpose: we had to extend `str` in the first place. Hence, it works with third-party libraries.

With interpreted languages, it's reasonably easy to add behavior to types. Yet, Python already touches the limits because the built-in types are implemented in C.

Java {#h2-3-java}
-----------------

Java is a compiled-statically and strongly-typed language that runs on the JVM. Its static nature makes it impossible to add behavior to a type.

The workaround is to use `static` methods. If you've been a Java developer for a long time, I believe you probably have seen custom `StringUtils` and `DateUtils` classes early in your career. These classes look something like this:

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class StringUtils {

    public static String toCamelCase(String string) {
        // The implementation is not relevant
    }

    // Other string transformations here
}</pre>

I hope that by now, using Apache Commons and Guava have replaced all those classes:

<pre class="EnlighterJSRAW" data-enlighter-language="java">System.out.println(WordUtils.capitalize("OncE upOn a tImE in thE WEst"));</pre>

In both cases, *the usage of static methods prevents fluent API usage* and thus impairs developer experience. But other JVM languages do offer exciting alternatives.

Scala {#h2-4-scala}
-------------------

Like Java, Scala is a compiled, statically- and strongly-typed language that runs on the JVM. It was initially designed to bridge between Object-Oriented Programming and Functional Programming. Scala provides many powerful features. Among them, *implicit* classes allow adding behavior and state to an existing class. [Here](https://scastie.scala-lang.org/razUhHKRRcqamn9qlA0mhw) is how to add the `toCamelCase()` function to `String`:

<pre class="EnlighterJSRAW" data-enlighter-language="scala">import Utils.StringExtensions

object Utils {
  implicit class StringExtensions(thiz: String) {
    def toCamelCase() = "\\w\\S*".r.replaceAllIn(
      thiz,
      { it =&gt; it.group(0).toLowerCase().capitalize }
    )
  }
}

println("OncE upOn a tImE in thE WEst".toCamelCase())</pre>

Though I dabbled a bit in Scala, I was never a fan. As a developer, I've always stated that a big part of my job was to make *implicit* requirements *explicit* . Thus, I frowned upon the on-purpose usage of the `implicit` keyword. Interestingly enough, it seems that I was not alone. Scala 3 keeps the [same capability](https://scastie.scala-lang.org/18abIFMKSvWiz8gpbVx2gg) using a more appropriate syntax:

<pre class="EnlighterJSRAW" data-enlighter-language="scala">extension(thiz: String)
  def toCamelCase() = "\\w\\S*".r.replaceAllIn(
    thiz,
    { it =&gt; it.group(0).toLowerCase().capitalize }
  )</pre>

Note that the *bytecode* is somewhat similar to Java's *static* method approach in both cases. Yet, API usage is fluent, as you can chain method calls one after another.

Kotlin {#h2-5-kotlin}
---------------------

Like Java and Scala, Kotlin is a compiled, statically- and strongly-typed language that runs on the JVM. Several other languages, including Scala, inspired its design.

My opinion is that Scala is more powerful than Kotlin, but the trade-off is an additional cognitive load. On the opposite, Kotlin has a lightweight approach, more pragmatic. Here's the [Kotlin version](https://pl.kotl.in/b67HIw06t):

<pre class="EnlighterJSRAW" data-enlighter-language="kotlin">fun String.toCamelCase() = "\\w\\S*"
    .toRegex()
    .replace(this) {
        it.groups[0]
            ?.value
            ?.lowercase()
            ?.replaceFirstChar { char -&gt; char.titlecase(Locale.getDefault()) }
            ?: this
    }

println("OncE upOn a tImE in thE WEst".toCamelCase())</pre>

If you wonder why the Kotlin code is more verbose than the Scala one despite my earlier claim, here are two reasons:

1. I don't know Scala well enough, so I didn't manage corner cases (empty capture, etc.), but Kotlin leaves you no choice
2. The Kotlin team removed the `capitalize()` function from the `stdlib` in Kotlin 1.5

Rust {#h2-6-rust}
-----------------

Last but not least in our list, Rust is a compiled language, statically and strongly typed. It was initially designed to produce native binaries. Yet, with the relevant configuration, it also allows to generate . In case you're interested, I've taken link:/focus/start-rust/\[a couple of notes\] while learning the language.

Interestingly enough, though statically-typed, Rust also allows extending third-party APIs as [the following code shows](https://play.rust-lang.org/?version=stable&amp;mode=debug&amp;edition=2021&amp;gist=8d1daecd7bd46d6352c131cbf8186839):

<pre class="EnlighterJSRAW" data-enlighter-language="rust">trait StringExt {                                                  // 1
    fn to_camel_case(&amp;self) -&gt; String;
}

impl StringExt for str {                                           // 2
    fn to_camel_case(&amp;self) -&gt; String {
        let re = Regex::new("\\w\\S*").unwrap();
        re.captures_iter(self)
            .map(|capture| {
                let word = capture.get(0).unwrap().as_str();
                let first = &amp;word[0..1].to_uppercase();
                let rest = &amp;word[1..].to_lowercase();
                first.to_owned() + rest
            })
            .collect::&lt;Vec&lt;String&gt;&gt;()
            .join(" ")
    }
}

println!("{}", "OncE upOn a tImE in thE WEst".to_camel_case());</pre>

1. Create the abstraction to hold the function reference. It's known as a *trait* in Rust.
2. Implement the trait for an existing structure.

Trait implementation has one limitation: our code must declare at least one of either the trait or the structure. You cannot implement an existing trait for an existing structure.

Conclusion {#h2-7-conclusion}
-----------------------------

Before writing this article, I thought that interpreted languages would allow extending external APIs, while compiled languages wouldn't, with Kotlin being the exception. After gathering the material, my understanding has changed drastically.

I realized that all mainstream languages provide such a feature. While I didn't include a C# section, it also does. My conclusion is sad, as Java is the only language that doesn't offer anything in this regard.

I've regularly stated that Kotlin's most significant benefit over Java is extension properties/methods. While the Java team continues to add features to the language, it still doesn't offer a developer experience close to any of the above languages. As I've used Java for two decades, I find this conclusion a bit sad, but it's how it is, unfortunately.

**To go further:**

* [Scala 3 language reference: extension methods](https://docs.scala-lang.org/scala3/reference/contextual/extension-methods.html)
* [Kotlin extensions](https://kotlinlang.org/docs/extensions.html)

*Originally published at [A Java Geek](https://blog.frankel.ch/extending-third-party-apis/) on November 7^th^, 2021*

*[Wasm]: WebAssembly
