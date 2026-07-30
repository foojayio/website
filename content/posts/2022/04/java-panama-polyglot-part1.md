---
title: "Java Panama Polyglot (C++) Part 1 | Foojay.io Today"
slug: "java-panama-polyglot-part1"
date: "2022-04-06T15:41:49+00:00"
lastmod: "2022-05-26T09:37:35+00:00"
description: "Java Panama Polyglot series: quick tutorials or recipes on how to access native libraries written in other languages!"
authors:
  - "carldea"
image: "https://foojay.io/wp-content/uploads/2022/04/panama_duke_cpp-1.png"
categories:
  - "JEPs"
  - "Performance"
  - "Project Panama"
  - "Tutorials"
tags:
related_posts:
enlighterjs: true
frozen: false
---

<figure class="wp-block-image size-full is-resized">
 <img fetchpriority="high" decoding="async" src="/images/posts/2022/04/java-panama-polyglot-part1/polyglot.png" alt="" class="wp-image-55283" width="469" height="112">
</figure>

Hello and welcome to the **Java Panama Polyglot** series where we will be presenting quick tutorials or recipes on how to access native libraries written in other languages.

For example, you will learn about Java Project Panama's (foreign function interface) abilities to access native libraries written in C++, Swift, Python, and others.

**What is Polyglot?**

Have you heard of polyglot as it relates to developing [full stack](https://en.wikipedia.org/wiki/Solution_stack) applications? If your answer is **yes**, then you probably know about the programming languages needed to build an enterprise web application.

The typical languages you'll encounter are HTML, CSS, JavaScript, Java, SQL, etc. However, in this article we will be focused on Java talking to native languages.

If you are a new(bie) comer to Java's Project Panama you'll want to check out [Project Panama for Newbies](https://foojay.io/today/project-panama-for-newbies-part-1/).

In Part 1 (this article) you will learn how to create a native C++ library to be later called from Java code using Panama's foreign function APIs.

You'll first see some requirements needed to successfully execute the code examples.

Next, you will be shown a **problem** and **solution** section then followed-up by code examples. Lastly, is the **How it works** section explains what is actually going on in the code examples.

Requirements {#h2-0-requirements}
---------------------------------

* Project Panama EA release - Build 19-panama+1-13 (2022/1/18) - <https://jdk.java.net/panama/>
* GNU g++ compiler
  * MacOS - <https://developer.apple.com/technology/xcode.html>
  * Linux - install g++
  * Windows - <https://code.visualstudio.com/docs/cpp/config-mingw>

Problem {#h2-1-problem}
-----------------------

As a **C++ developer** you want to expose functions allowing Java developers to call into.

Solution {#h2-2-solution}
-------------------------

Create and **export C functions** as symbols that are available to linkers. As a C++ developer you will create C based functions that will allow Java's foreign function APIs to access native symbols (CLinker).

As a C++ developer you will create a native C++ library created for a specific operating system such as `*.dylib`, `*.so`, and `*.dll`.

Similar to jar files native libraries are operating system specific and can be compiled along with other C++ based applications. Often times library developers will make functions available to other languages that understand the well known C ABI (Application Binary Interface) standard (convention).

Example {#h2-3-example}
-----------------------

As an example we will create a C++ class representing a rectangle object with private fields `width` and `height`.

The Rectangle class will have a single method (member function) `area()` that calculates the area of the rectangle to be returned to the caller of type `int`.

Users of this API will instantiate a Rectangle instance using a constructor and invoking the public function `area()`.

Let's create a simple C++ library with the following code (`MyRectangle.cpp`):

<pre class="EnlighterJSRAW" data-enlighter-language="cpp" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// MyRectangle.cpp
#include &lt;iostream&gt;
#include &lt;cstring&gt;

class Rectangle {
    int width, height;
  public:
    Rectangle(int, int);
    int area() {return width*height;}
};

Rectangle::Rectangle(int w, int h) {
  this-&gt;width = w;
  this-&gt;height = h;
}

// Expose C ABI for Panama to call into.
extern "C" int rectArea(int, int);
int rectArea(int w, int h) {
    std::cout &lt;&lt; "Inside C++ Code " &lt;&lt; std::endl;
    Rectangle rect(w,h);
    return rect.area();
}</pre>

Let's create a native library using `g++`. Enter the following to compile the C++ code above:

<pre class="EnlighterJSRAW" data-enlighter-language="shell" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// MacOS
g++ -dynamiclib -current_version 1.0 -I &lt;include directory&gt; -o libmyrectangle.dylib MyRectangle.cpp

// Linux
g++ -dynamiclib -current_version 1.0 -I &lt;include directory&gt; -o myrectangle.so MyRectangle.cpp

// Windows
g++ -dynamiclib -current_version 1.0 -I &lt;include directory&gt; -o myrectangle.dll MyRectangle.cpp
</pre>

Above you'll notice switches being used. The following are the switches and their descriptions:

* `-dynamiclib` - To indicate this will be a dynamic library, that can be shared with other applications.
* `-current_version` - The version of the library
* `-I` - Include directories containing `.h` or `.hpp` files
* `-o` - The output file name of the file

Now that you've created a native library let's see how to load and use Java's Panama (Foreign Function APIs) to access native functions or symbols.

Create a file Java application named `RectangleMain.java` and enter the following into your `main()` method:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">System.loadLibrary("myrectangle");

var cLinker = CLinker.systemCLinker();

// C function int rectArea(int w, int h)
MethodHandle rectAreaMH = cLinker.downcallHandle(cLinker.lookup("rectArea").get(),
                    FunctionDescriptor.of(C_INT, C_INT, C_INT));

// Return area of rectangle
int w = 8;
int h = 2;
int area = (int) rectAreaMH.invokeExact(w, h);

System.out.printf("MethodHandle calling rectArea(%d, %d) = (%d)\n", w, h, area);</pre>

To execute the code do the following:

<pre class="EnlighterJSRAW" data-enlighter-language="shell" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">java --enable-native-access=ALL-UNNAMED \
     --add-modules jdk.incubator.foreign \
     -Djava.library.path=.:/usr/local/lib \
     RectangleMain.java</pre>

Below is the output:

<pre class="EnlighterJSRAW" data-enlighter-language="shell" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Inside C++ Code 
MethodHandle calling rectArea(8, 2) = (16)
</pre>

How it Works {#h2-4-how-it-works}
---------------------------------

Looking at the C++ code listing above you'll notice a `Rectangle` class defined with a constructor definition having two (private) data members **width** \& **height** and a public function `area()` that will return the area calculation of the rectangle.

In addition to the `Rectangle` class is a C style function responsible for instantiating a Rectangle with inbound parameters width and height. Lastly, the `rect.area()` function will return the area calculation to the caller.

When creating and exporting a C function `areaRect()` in C++ the code uses the `extern "C"` followed by the method signature to be exported. The extern binding is prefixed above the C function.

**Note:** The C function may be named differently, however the signature must be the same. Since C++ supports the C ABI's convention other languages can access these functions.

Shown below is another example of using the `extern "C"` to export C functions.

<pre class="EnlighterJSRAW" data-enlighter-language="cpp" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">extern "C" int doWork(int);
int doWork(int num) {
    std::cout &lt;&lt; "Inside C++ Code " &lt;&lt; std::endl;
    // do cool stuff here!
    return num;
}</pre>

### Compiling and Building a Native library {#h3-5-compiling-and-building-a-native-library}

When building the native library the `g++` compiler will need the switch `-dynamiclib` and `-I` with known include directories. When the `-o` option is applied the library's file name is named based on the operating system (library naming convention). This allows Java's `System.loadLibrary()` method to load the library in a portable way across operating systems.

The following are the naming conventions for the respective operating systems:

* MacOS - `lib<name>.dylib`
* Linux - `<name>.so`
* Windows - `<name>.dll`

**Note:** When running the Java application specify the `java.library.path` property to the location of the library. If not set correctly you can get the following runtime exception:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">java.lang.UnsatisfiedLinkError</pre>

### Java talking to C++ (C functions) {#h3-6-java-talking-to-c-c-functions}

The code example using Java 18's Panama (FFI) APIs you don't need to use the `jextract` tool. Here you'll notice the code creating a method handle (`MethodHandle`) instance by obtaining the native symbol (C function) to be invoked. Shown below is the `FunctionDescriptor` of the signature for the `areaRect()` C function.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// (return type int area, int width, int height)
FunctionDescriptor.of(C_INT, C_INT, C_INT)</pre>

Conclusion {#h2-7-conclusion}
-----------------------------

In a series of articles, we will be exploring other languages that can be accessed using Panama's foreign function interface APIs.

In Part 1, you've learned about how to expose or export C functions (symbols) inside of a C++ libraries using the `extern "C"` facility.

After successfully building the native library, you've had a chance to create a method handle (`MethodHandle`) in Java.

As a result, the Java application code is able to invoke the exported C function `areaRect()` defined earlier.

There you have it! Java Panama Polyglot, in other words: Java talking to C++.

Next, we will look at how Java Panama can talk to the language Swift in [Part 2](https://foojay.io/today/java-panama-polyglot-swift-part-2/).

As always, comments and feedback are welcome!
