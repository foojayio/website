---
title: "Project Panama for Newbies (Part 4) | Foojay.io Today"
slug: "project-panama-for-newbies-part-4"
date: "2022-02-24T08:34:52+00:00"
lastmod: "2026-01-01T04:57:29+00:00"
description: "imagine C code capable of performing a computation and after its completion the C code will notify Java code to perform updates to JavaFX UI."
authors:
  - "carldea"
image: "https://foojay.io/wp-content/uploads/2022/02/duke_and_panama.png"
categories:
  - "Java Core"
  - "JEPs"
  - "Project Panama"
  - "Tutorials"
tags:
related_posts:
  - "project-panama-for-newbies-part-1"
  - "project-panama-for-newbies-part-2"
  - "project-panama-for-newbies-part-3"
  - "is-there-a-best-os-to-develop-a-java-application-on"
enlighterjs: true
frozen: false
---

**Updated December 31, 2025:** This article now features Java 25 and the Foreign Function \& Memory (FFM) API, which has been a standard feature since JDK 22.

Welcome to Part 4 of Java's Project Panama for newbies! If you've been following [this](https://foojay.io/today/project-panama-for-newbies-part-1/) [series](https://foojay.io/today/project-panama-for-newbies-part-2/) of [posts](https://foojay.io/today/project-panama-for-newbies-part-3/) on how to use Java's Project Panama APIs you probably have noticed that all of the examples thus far demonstrate how Java code can execute C code by using the [linker](https://github.com/openjdk/panama-foreign/blob/d8c0fe5918cb1c6c744eb26797ea4fa04142c237/doc/panama_ffi.md#c-linker)'s `downCall()` method.

But, did you know that you can go in the opposite direction (C code executes Java Code)?

**Answer:** YES!

Why is this capablity amazing, you ask? Well, imagine C code capable of performing a computation and after its completion the C code will notify Java code to perform updates to JavaFX UI components.

In this article you will learn about **callbacks** and how to create them in both **Java** and in **C** . In principle, you will learn how to create Java code that can be executed from C code using [linker](https://github.com/openjdk/panama-foreign/blob/d8c0fe5918cb1c6c744eb26797ea4fa04142c237/doc/panama_ffi.md#c-linker)'s `upcallStub()` method.
![](/images/posts/2022/02/project-panama-for-newbies-part-4/part4_upCallStub.png)

In addition to this article I will show you how to create a native library written in C that will be later accessed using Java's newer Foreign Access APIs

For the impatient please head over to GitHub at: [https://github.com/carldea/panama4newbies/tree/jdk19-ea/part04](https://github.com/carldea/panama4newbies/blob/jdk19-ea/part04)

If you are new to this blog series please check out [Part 1](https://foojay.io/today/project-panama-for-newbies-part-1/), [Part 2](https://foojay.io/today/project-panama-for-newbies-part-2/), and [Part 3](https://foojay.io/today/project-panama-for-newbies-part-3/). In Part 4 we will look at what are C native function pointers and later how to create a Java method to be passed into a function as a **callback**.

The article's agenda is as follows:

* Application Layers
* What's a Callback?
* What's a C Function Pointer?
* Creating a Native Shared Library
* Using the `jextract` tool to generate Java bindings
* Creating a Java program to access a Native Library
* Run Java program `PanamaCallback.java`
* Conclusion

Application Layers {#h2-0-application-layers}
---------------------------------------------

Whenever developing applications with dependencies it's a good idea to view component layers at a high-level (Top-down approach). This is especially important when you are sharing native resources that could potentially be used by other applications and services on the same node(device). Shown below are the components stacked upon each other.
![](/images/posts/2022/02/project-panama-for-newbies-part-4/panama_layers.png) Java's Project Panama Architecture Layers

Above you'll notice a Java Application block that depends on the JVM and Native library blocks respectively. In addition to the top layer you'll notice a C application (block) that also depends on the native library (block) as a shared resource. The rest of the layers are the operating system and hardware.

Enough about top-down views of the application layers let's look at things from a bottom-up approach by learning C language concepts and then later create a native library that will be accessed by a Java application. Before getting into code let's talk about **callbacks**.

What is a Callback? {#h2-1-what-is-a-callback}
----------------------------------------------

According to [Wikipedia](https://en.wikipedia.org/wiki/Callback_(computer_programming)):
> A callback, also known as a "call-after" function, is any reference to executable code that is passed as an argument to other code; that other code is expected to call back (execute) the code at a given time. This execution may be immediate as in a synchronous callback, or it might happen at a later point in time as in a asynchronous callback. Programming languages support callbacks in different ways, often implementing them with subroutines, lambda expressions, blocks, or function pointers.

In Java we can create callback behaviors by creating methods that can return or receive a lambda expression. Shown below is a **synchronous** callback where a method will do work **pre** and **post** invocation.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">public static void myJavaCallback(Runnable codeBlock) {
   log.info("Begin calling codeBlock");
   codeBlock.run();
   log.info("Finished calling codeBlock");
}

Runnable codeBlock = () -&gt; System.out.println(" Inside codeBlock");

this.MyClass.myJavaCallback(codeBlock);</pre>

The output of the code snippet above:

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Begin calling codeBlock
 Inside codeBlock
Finished calling codeBlock</pre>

**Asynchronous** callbacks can be quite useful too especially when you want to defer code execution at a later time. To defer code execution you may explore Java's `CompletableFuture` API.

So, now that you know how to create callbacks in Java you may be wonder how is this done in the C Language?

**Answer:** Function Pointers

What is a C Function Pointer? {#h2-2-what-is-a-c-function-pointer}
------------------------------------------------------------------

According to [Wikipedia](https://en.wikipedia.org/wiki/Function_pointer) :
> A **function pointer** , also called a **subroutine pointer** or **procedure pointer** , is a [pointer](https://en.wikipedia.org/wiki/Pointer_(computer_programming)) that points to a function. As opposed to referencing a data value, a function pointer points to executable code within memory. [Dereferencing](https://en.wikipedia.org/wiki/Dereference_operator) the function pointer yields the referenced [function](https://en.wikipedia.org/wiki/Subroutine), which can be invoked and passed arguments just as in a normal function call.

As a Java developer you can think of a C function as a first class citizen as a static singleton object. Similar to a Java SAM (single abstract method) object stored as a named variable at some memory location (address). Another way to observe functions is that they are statically defined and synonymous to Java's `public final static` methods. As we will see later, this will allows us to pass a function into another function and therefore enabling API developers to create callback behaviors.

Now that you know what a callback is and how it behaves in the C language, let's create a C native library containing C function **callbacks** that receive **function pointers** as a parameter.

Before getting into Java code let's look at a simple example of creating a native library in C. This library will be used later by our `PanamaCallback.java` example. When creating a shared library you will need the following:

* **C compiler** - Most compilers will have options to generate a shared library such as GCC or Clang
* **Header file** ([mylib.h](https://github.com/carldea/panama4newbies/blob/jdk19-ea/part04/mylib.h))- Declares the functions to be implemented.
* **C file** ([mylib.c](https://github.com/carldea/panama4newbies/blob/jdk19-ea/part04/mylib.c))- Implementation code.

The diagram below shows how to compile a C program into a native library for the MacOS platform. Use the the `-o` switch to name your output binary for the respective OS. eg: Linux `-o libmylib.so`, Windows `-o mylib.dll`
![](/images/posts/2022/02/project-panama-for-newbies-part-4/part4_dynamic_library-2.png)

You'll notice the naming convention for the Mac OS where the name of the library is `mylib` but the file name is `libmylib.dylib`. This is good to know because in `jextract` you will generate code that under the hood calls the `System.`*loadLibrary* `(`**"mylib"**`);` . That way it will load the library in a similar way for all OS platforms. This will allow the library to be distributed with the application co-located with your Java code or specified in Java property `java.library.path`.

To make things simple I used the C examples from the popular site [Tutorials Point.com on C callbacks](https://www.tutorialspoint.com/callback-function-in-c). I modified the examples slightly to show program flow between Java and C.

C header file [mylib.h](https://github.com/carldea/panama4newbies/blob/jdk19-ea/part04/mylib.h) {#h2-3-c-header-file-mylib-h}
-----------------------------------------------------------------------------------------------------------------------------

Let's examine the [mylib.h](https://github.com/carldea/panama4newbies/blob/jdk19-ea/part04/mylib.h) file that consists of function declarations. These functions are exported or public to callers of the native library. Later, you will see the [mylib.c](https://github.com/carldea/panama4newbies/blob/jdk19-ea/part04/mylib.c) file that will implement the declared functions from the `mylib.h` file.

The following are three functions signatures are declared.

Listing 1 `mylib.h` - Functions defined

<pre class="EnlighterJSRAW" data-enlighter-language="c" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">#include &lt;stdio.h&gt;

void my_function();
void my_callback_function(void (*ptrToFunction)());
void my_callback_function2(void (*ptrToFunction)(int));</pre>

* `my_function()` - A regular C function
* `my_callback_function()` - Receives a pointer to a function named **ptrToFunction**
* `my_callback_function2()` - Receives a pointer to a function named **ptrToFunction** that also takes an int as a parameter.

Above you'll notice `my_function()` as an ordinary C function that can be passed into the second function called `my_callback_function()`. As you'll see later in the implementation code `mylib.c` the `main()` function will pass a **function pointer** referencing the C function `my_function()`.

The last my_callback_function2() function is another example of a callback, but the function pointer to be passed in has a signiture of a void return type and one argument of type `int`.

C implementation file [mylib.c](https://github.com/carldea/panama4newbies/blob/jdk19-ea/part04/mylib.c) {#h2-4-c-implementation-file-mylib-c}
---------------------------------------------------------------------------------------------------------------------------------------------

Next, is the implementation file `mylib.c`. The code will begin by including the `mylib.h` as dependency. This is similar to Java interfaces. Shown in listing 2 is mylib.c file containing the function implementations of the functions define in `mylib.h` file header.

Listing 2 [mylib.c](https://github.com/carldea/panama4newbies/blob/jdk19-ea/part04/mylib.c) - Implementation code

<pre class="EnlighterJSRAW" data-enlighter-language="c" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">#include &lt;stdio.h&gt;
#include "mylib.h"

void my_function() {
   printf("This is a normal function.");
}

void my_callback_function(void (*ptrToFunction)()) {
   printf("[C] Inside mylib's C function my_callback_function().\n");
   printf("[C]   Now invoking Java's callMePlease() static method.\n");

   // Calling the passed in callback
   (*ptrToFunction)();
}

void my_callback_function2(void (*ptrToFunction)(int)) {
   printf("[C] Inside mylib's C function my_callback_function2().\n");
   printf("[C]   Now invoking Java's doubleMe(int) static method.\n");
   int x = 123;
   (*ptrToFunction)(x);   //calling the callback function
}

int main() {
   printf("[C] Callbacks! \n");
   void (*ptr)() = &amp;my_function;
   my_callback_function(ptr);
   return 0;
}
</pre>

The first callback function `my_callback_function()` receives a pointer to a function. The `my_callback_function()` function will output two lines of text to the console and subsequently invoke the function that the function pointer `ptrToFunction` is referenced at.

Above you'll notice the `main()` function code that tests the `my_callback_function()` function by assigning a **function pointer** named `ptr` referencing the function `my_function` as shown below:

<pre class="EnlighterJSRAW" data-enlighter-language="c" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">void (*ptr)() = &amp;my_function;</pre>

![](/images/posts/2022/02/project-panama-for-newbies-part-4/part4_function_pointer-1.png) Declare a function pointer variable named ptr

In C language the **ampersand** gets the **address** of the `my_function()` function to be assigned to the variable `ptr` . Notice the matching call signiture. e.g. returns void and void parameters (no parameters). Remember, the `(*SOME_VARIABLE_NAME)` parenthesis and asterisk around the name denotes it is a **variable** of a pointer to a function.

Keep in mind the variable `ptr` must follow the signiture of the function you want to assign. Later, in Java Panama code we will mimick a function pointer to be passed into the `my_callback_function(void (*ptrToFunction)())` function.

With the header file and implementation file let's compile and create a native shared library.

Creating a Native Shared Library {#h2-5-creating-a-native-shared-library}
-------------------------------------------------------------------------

Depending on the C compiler there are switches that will create a binary library. Most compilers will have the option `-o` to allow you to name the library suitable for your operating system. In this scenario to support MacOS the naming would be prefixed with `lib` and a file extension as `.dylib`.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">gcc -shared -o libmylib.dylib mylib.c</pre>

Afterwards the following is outputed as a native library local to the working path. Remember the name of the library is `mylib` and the file naming below is specific to the MacOS.

<pre class="EnlighterJSRAW" data-enlighter-language="shell" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">libmylib.dylib</pre>

Later, you'll use the name `mylib` as the library name for `jextract` to be able to generate code that will load the library during runtime.

After creating a native library in C let's use Project Panama's `jextract` tool to generate classes and source code. Later, you will see how to access `mylib` native library purely in Java code.

Using the `jextract` tool {#h2-6-using-the-jextract-tool}
---------------------------------------------------------

In Part 3 we mentioned that `jextract` only allows you to target one header file at a time. To overcome this limitation, you can simply create a dummy header file such as `foo.h` consists of multiple includes as shown below.

Listing 2 - `foo.h` Containing multiple includes

<pre class="EnlighterJSRAW" data-enlighter-language="c" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">#include &lt;stdio.h&gt;
#include "mylib.h"</pre>

Above you'll notice headers in **angle brackets** vs **double quotes**. In short, for C standard hearders the code will use angle brackets and for third party libraries such as mylib will use the double quotes.

Let's go ahead and generate the Panama class files and source code using `jextract` as shown below.

<pre class="EnlighterJSRAW" data-enlighter-language="shell" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># Generate java source files
jextract --output src \
  -t org.unix \
  -I /Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk/usr/include \
  -I . \
  -l mylib \
  foo.h
</pre>

Above you'll notice the `-I .` is to let `jextract` know about the `mylib.h` in the working directory. Also, you should notice `-l mylib` as we mentioned earlier when the `System.loadLibrary()` method is called to locate the library based on the `java.library.path` property.

Now, that you've successfully generated binding code let's see how to reference these C functions manually as opposed to the convenience methods created by `jextract`. Instead of using the generated code such as: `foo_h.`*my_callback_function*`()` I will be showing you how to do things in a lower level way. This helps you understand what is going on under the hood.

Obtaining Native Symbols {#h2-7-obtaining-native-symbols}
---------------------------------------------------------

This has been discussed in Part 3, to obtain C native functions or symbols. In Part 4 we are using [](https://openjdk.java.net/jeps/454)[JEP-454](https://openjdk.java.net/jeps/454) updates to Panama's Foreign Function APIs.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">var linker = Linker.nativeLinker();

SymbolLookup foolibLookup = SymbolLookup.libraryLookup(System.mapLibraryName("mylib"),  Arena.ofAuto())
                    .or(SymbolLookup.loaderLookup())
                    .or(Linker.nativeLinker().defaultLookup());

MemorySegment callback1 = foolibLookup.findOrThrow("my_callback_function");</pre>

After obtaining the symbol `MemorySegment` let's create a **method handle** like the following:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">var my_callback_functionMethodHandle = Linker
       .nativeLinker()
       .downcallHandle(callback1, FunctionDescriptor.ofVoid(C_POINTER));</pre>

After obtaining the symbol from memory (`MemorySegment`) a `downcallHandle()` method creates a `MethodHandle` object for later invocation. Here, you have to describe the C function's call signature.

If you recall the C function `my_callback_function()` call signature (In C) looks like the following:

<pre class="EnlighterJSRAW" data-enlighter-language="c" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">void my_callback_function(void (*ptrToFunction)())</pre>

Here you'll notice the C function `my_callback_function()` takes a **function pointer** as a parameter (`C_POINTER`). Next, let's see how to pass in a Java `static` method into the C native `my_callback_function()` function. To keep things simple the callback will be a function that returns **void** and has **void parameters** . The **void parameters** just means **empty arguments** or **no parameters**.

Creating a Function Pointer with Java Code {#h2-8-creating-a-function-pointer-with-java-code}
---------------------------------------------------------------------------------------------

To mimic a function pointer in Java as described above (returns void and no parameters) you can simply create a **static** method as shown below:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">    public static void callMePlease() {
        MemorySegment cString = Arena.allocateFrom("[JAVA] Inside callMePlease() method - I'm being called from C.\n");
        foo_h.printf.makeInvoker().apply(cString);
    }</pre>

Above, the code creates a C string using the `allocateFrom(String)` to create a `MemorySegment` that subsequently is passed into a C's `printf()` function for text output to the console. Of course the code could have used Java's `System.out.printf()`, however when mixing between C's `printf()` and Java's `printf()` can have unexpected results such as the order in which text lines appears. This is due to a buffered output stream that isn't flushed.

Because of C's standard input/output buffer is separate from Java's standard input/output buffer which ever is flushed first will be outputted first. Let's look at a quick example of what I mean.

**What will be outputted from the example below?**

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">MemorySegment cString = ...// "1 - C stdout\n"
foo_h.printf(cString);
System.out.printf("2 - Java Panama\n");</pre>

The output of the above code snippet is as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">2 - Java Panama
1 - C stdout</pre>

Here, you'll notice the Java `printf()` text line is first. To ensure the C's buffer is flushed use C's `fflush()`or `fprintf()` function as shown below:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">MemorySegment cString = ...// "1 - C stdout\n"
foo_h.printf.makeInvoker().apply(cString);
fflush(NULL());
System.out.printf("2 - Java Panama\n");</pre>

The output should look like the following:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">1 - C stdout
2 - Java Panama</pre>

To get back on track with **callbacks** \& **function pointers** let's look at how to pass a Java **method** into a C callback function as a **function** **pointer**.

Pass Callback into a C Function {#h2-9-pass-callback-into-a-c-function}
-----------------------------------------------------------------------

Before passing the above `callMePlease()` Java method you'll have to create a `MethodHandle` as shown below:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Create a method handle to the Java function as a callback
MethodHandle onCallMePlease = MethodHandles
                 .lookup()
                 .findStatic(PanamaCallback.class,
                            "callMePlease",
                            MethodType.methodType(void.class));</pre>

Next, you need to create a `NativeSymbol` object using the `upcallStub()` method. The `NativeSymbol` objects are reference by the symbol's **address** in memory. This allows the code to pass Java static methods as **C function pointers**.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Create a stub as a native symbol to be passed into native function.
// void (*ptr)()
MemorySegment callMePleaseNativeSymbol = Linker.nativeLinker().upcallStub(
                    onCallMePlease,
                    FunctionDescriptor.ofVoid(),
                    arena);</pre>

To demonstrate a call to the `my_callback_functionMethodHandle()`**C function** the following code snippet calls the `invokeExact()` method by passing in the `callMePleaseNativeSymbol` (`MemorySegment`).

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// Invoke C function receiving a callback
// void my_callback_function(void (*ptr)())
my_callback_functionMethodHandle.invokeExact(callMePleaseNativeSymbol);</pre>

Above you'll notice a cast to `Addressable`, this is because the function accepts a `C_POINTER`. As defined earlier the code specifies the method signature as shown below:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Linker.nativeLinker()
     .downcallHandle(callback1, FunctionDescriptor.ofVoid(C_POINTER));</pre>

Anytime you see a downcall method to create a method handle using a C_POINTER as a return or an argument the object passed in must be an `MemorySegment`.

To see the full listing head over to GitHub at [PanamaCallback.java](https://github.com/carldea/panama4newbies/blob/jdk19-ea/part04/src/PanamaCallback.java). For extra credit take a look at the my_callback_function2 to see a method signiture that receives a C int value.

Now that the code is able to talk to the native library let's compile and run the example (`PanamaCallback.java`).

Running the Example {#h2-10-running-the-example}
------------------------------------------------

To compile and run do the following:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""> javac -d classes \
   -cp classes:. \
   src/PanamaCallback.java</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">env JAVA_LIBRARY_PATH=:/usr/local/lib:. \
  java -cp classes \
  --enable-native-access=ALL-UNNAMED  \
  PanamaCallback</pre>

The output:

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">[Java] Callbacks! Panama style
[C] Inside mylib's C function my_callback_function().
[C]   Now invoking Java's callMePlease() static method.
[JAVA] Inside callMePlease() method - I'm being called from C.
[C] Inside mylib's C function my_callback_function2().
[C]   Now invoking Java's doubleMe(int) static method.
[JAVA] Inside doubleMe() method, 123 times 2 = 246.</pre>

The output above shows the code execution path by displaying `[Java]`or `[C]` prefixed each line to denote code being run inside the Java world or in the native C world (library).

There you have it, Java Panama and C callbacks for newbies!

You've now had a chance to learn about C function pointers and how they relate to callback behaviors in the C language.

Next, I showed you how to create a **native shared library** on the MacOS. This native library will be later used in Java Panama code.

After creating a native library, we used the `jextract` tool to generate classes and source code.

Lastly, you learned how to obtain **native symbols** and create a Java method as **function pointer** to be passed into a native function to be invoked as a callback.

As always, comments and feedback are always welcome.
