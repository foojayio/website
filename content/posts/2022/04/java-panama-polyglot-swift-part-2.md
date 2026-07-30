---
title: "Java Panama Polyglot (Swift) Part 2 | Foojay Today"
slug: "java-panama-polyglot-swift-part-2"
date: "2022-04-13T15:34:59+00:00"
lastmod: "2022-06-01T20:08:13+00:00"
description: "Java Panama Polyglot series: quick tutorials or recipes on how to access native libraries written in other languages! Today we'll see swift!"
authors:
  - "carldea"
image: "/images/posts/2022/04/java-panama-polyglot-swift-part-2/panama_duke_swift-1.png"
categories:
  - "Game Development"
  - "JEPs"
  - "Performance"
  - "Project Panama"
tags:
related_posts:
  - "foojay-all-about-java-and-the-openjdk-i-programmer"
  - "7-ways-to-contribute-to-openjdk"
  - "5-things-you-probably-didnt-know-about-java-concurrency"
  - "project-panama-for-newbies-part-4"
enlighterjs: true
frozen: false
---

Hello and welcome back to the **Java Panama Polyglot** series where we will be presenting quick tutorials or recipes on how to access native libraries written in other languages.

In [Part 1](https://foojay.io/today/java-panama-polyglot-part1/) you got a chance to learn about how to use Java [Project Panama](https://jdk.java.net/panama/)'s (foreign function interface) abilities to access native libraries written in C++. Today, we will be looking at Java code being able to talk to Apple's [Swift](https://developer.apple.com/swift/) language.

Requirements {#h2-0-requirements}
---------------------------------

* Project Panama EA release - Build 19-panama+1-13 (2022/1/18) - <https://jdk.java.net/panama/>
* Apple's Swift compiler
  * MacOS - <https://developer.apple.com/technology/xcode.html>

Problem {#h2-1-problem}
-----------------------

As a **MacOS Swift developer** you want to expose functions allowing Java developers to call into.

Solution {#h2-2-solution}
-------------------------

Create and **export C functions** as symbols that are available to linkers. As a Swift developer you will create Swift functions annotated to export them as C functions that will allow Java's foreign function APIs to access native symbols (CLinker).

The Swift language has an annotation called `@_cdecl("")` where to specify the name of the function to export as a C function (symbol).

As a Swift developer you will create a native Swift library created for the MacOS operating system such as a file named the following: `lib<library_name>.dylib`.

Similar to jar files native libraries are operating system specific and can be compiled along with other Swift based applications. Often times library developers will make functions available to other languages that understand the well known C ABI (Application Binary Interface) standard (convention).

Before we create a Swift file let's get glimpse of the language by using the Swift REPL (Read-Evaluate-Print-line).

Swift Primer {#h2-3-swift-primer}
---------------------------------

Since this is a Java centric article, this section is going to be very brief. It will give you the very basics to get started with the Swift language.

### Swift REPL - Hello World {#h3-4-swift-repl-hello-world}

After installing Xcode you should have the swift REPL and compiler at the command prompt as shown below:

In a terminal window type `swift`

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ swift</pre>

You should see something like the following:

<pre class="EnlighterJSRAW" data-enlighter-language="swift" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Apple Swift version 5.6 (swiftlang-5.6.0.323.62 clang-1316.0.20.8)
Target: x86_64-apple-macosx12.0

Welcome to Swift!

Subcommands:

  swift build      Build Swift packages
  swift package    Create and work on packages
  swift run        Run a program from a package
  swift test       Run package tests
  swift repl       Experiment with Swift code interactively (default)

  Use `swift --help` for descriptions of available options and flags.

  Use `swift help &lt;subcommand&gt;` for more information about a subcommand.

Welcome to Apple Swift version 5.6 (swiftlang-5.6.0.323.62 clang-1316.0.20.8).
Type :help for assistance.
  1&gt;  </pre>

Above you'll notice the Swift **REPL** which is similar to Java's [JShell](https://foojay.io/today/learn-javafx-with-jshell-in-60-seconds/) (REPL) where you can create variables and functions to be output to the console. This is a great way to test out simple code snippets.

To create a simple **Hello World** type the following to assign a string to a variable:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">var myString = "Hello, World!"</pre>

After hitting enter the following is the output:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">1&gt; var myString = "Hello, World!"
myString: String = "Hello, World!"
</pre>

Most, REPLs will display the variable and the value that is assigned to let you know it can be used later.

Next, you'll want to output the variable `myString` using Swift's `print()` function.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">2&gt;  print(myString)
Hello, World!
3&gt; </pre>

Assuming you've got the hang of it, let's create a simple Swift function.

Swift functions {#h2-5-swift-functions}
---------------------------------------

Still inside the REPL you can create a Swift function by starting with the `func` keyword, the **name** of the function, parameter list (in parens) and an optional return type prefixed with the arrow symbol `->`.

Let's create a simple function called `authenticateUser()` that returns a `boolean` value. This mimics a function that will authenticate a user.

Enter the code below:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">3&gt; func authenticateUser() -&gt; Bool  { 
4.     print("authenticated") 
5.     return true 
6. }
7&gt; </pre>

Now, to execute the function enter the following:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">7&gt; authenticateUser()
authenticated
$R0: Bool = true
8&gt;  </pre>

Above, you'll notice the output text of **authenticated** and subsequent a result of the call `$R0` and it's value. The variable `$R0` can also be reused in later statements in the REPL.

To exit the REPL type `:` (colon symbol) then the `(lldb)` prompt appears for you to type `quit`.

Creating a dynamic link library on MacOS {#h2-6-creating-a-dynamic-link-library-on-macos}
-----------------------------------------------------------------------------------------

Now that you are familiar with how to create a function lets create a native library that will export the Swift function as a C (ABI) function. This will allow Java Panama to load the library to access it as a native symbol.

Create a file named `myauth.swift` with the following code:

<pre class="EnlighterJSRAW" data-enlighter-language="swift" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@_cdecl("authenticate_user")
public func authenticateUser() -&gt; Bool  {
   print("authenticated")
   return true
}</pre>

While this is a very simple function to be invoked, keep in mind that when using other Swift specific data types, they'll need to conform to the C ABI. e.g., A Swift string must be converted to a C string. In the case of conversion please see the link <https://developer.apple.com/documentation/foundation/nsstring/1408489-cstring>

This will of course be seen as an instance of a `MemorySegment` which contains the method `getUtf8String(0)`.

Okay, back to the example code. Let's compile the Swift code from the terminal by entering the following:

<pre class="EnlighterJSRAW" data-enlighter-language="shell" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">swiftc myauth.swift -emit-library -o libmyauth.dylib</pre>

After the compilation the output should be a file named `libmyauth.dylib` in the local directory.

Above you'll notice switches being used. The following are the switches and their descriptions:

* `-emit-library` - To indicate this will be a dynamic library, that can be shared with other applications.
* `-o`` - `The output file name of the file

As a reminder the name of the library when loaded using `System.loadLibrary("myauth")` will be `myauth` and not `libmyauth`. This will allow portability across operating systems.

Now, that we have a library we can now use Project Panama's foreign function access APIs to execute the exported function.

Example {#h2-7-example}
-----------------------

Let's create a Java application that will use the foreign access APIs to load the native library and invoke the function. Create a file called `MyAuthMain.java` with the following code:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">import jdk.incubator.foreign.*;

import java.lang.invoke.MethodHandle;

public class MyAuthMain {
    public static void main(String[] args) {
       // load native library
       System.loadLibrary("myauth");

       try (ResourceScope scope= ResourceScope.newConfinedScope()) {

           var  symbolLookup = SymbolLookup.loaderLookup();

           // NativeSymbol (JEP 419) reference to native C function
           var nativeSymbol = symbolLookup.lookup("authenticate_user").get();
           System.out.println("Identify Yourself!");
           MethodHandle f= CLinker.systemCLinker()
                                  .downcallHandle(nativeSymbol, 
                                                  FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN));

           // invoke C(ABI) which calls the Swift function
           Boolean pass = (boolean) f.invokeExact();
           if (pass) {
               System.out.println("You may enter!");
           } else {
               System.out.println("Access Denied ");
           }

       } catch (Throwable throwable) {
           throwable.printStackTrace();
       }
    }
}</pre>

After saving the file now type the following to run the Java application (`MyAuthMain.java`):

<pre class="EnlighterJSRAW" data-enlighter-language="shell" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># Run java class
java -cp .:classes \
   --enable-native-access=ALL-UNNAMED \
   --add-modules jdk.incubator.foreign \
   MyAuthMain.java</pre>

Success!   

The output is shown below:

<pre class="EnlighterJSRAW" data-enlighter-language="shell" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">Identify Yourself!
authenticated
You may enter!</pre>

For extra credit check out my example of using MacOS' Touch ID using your fingerprint on GitHub [https://github.com/carldea/panama4newbies](https://github.com/carldea/panama4newbies/tree/main/macos-touchID) (sub project `touchid`).

How it Works {#h2-8-how-it-works}
---------------------------------

Compiling and building a native library:

When building the native library the

The following is the naming convention for the MacOS operating system:

* MacOS - `lib<name>.dylib`

**Note:** When running the Java application specify the `java.library.path` property to the location of the library. If not set correctly you can get the following runtime exception:

`java.lang.UnsatisfiedLinkError`

### Java talking to Swift functions as (C functions) {#h3-9-java-talking-to-swift-functions-as-c-functions}

The code example using Java 18's Panama (FFI) APIs you don't need to use the `jextract` tool. Here you'll notice the code creating a method handle (`MethodHandle`) instance by obtaining the native symbol (C function) to be invoked. Shown below is the `FunctionDescriptor` of the signature for the `authenticate_user()` C function that ultimately calls the Swift function `authenticateUser()`.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">// (return type boolean)
FunctionDescriptor.of(ValueLayout.JAVA_BOOLEAN)</pre>

Conclusion {#h2-10-conclusion}
------------------------------

You got a chance to get familiar with Swift REPL to try out some language basics. Next, you learned about the `@_cdecl("")`annotation that enable Swift functions to be exported as native symbols that follow the C ABI (convention).

After successfully building the native library, you've had a chance to create a method handle (`MethodHandle`) in Java.

As a result, the Java application code is able to invoke the exported function `authenticate_user()` defined earlier.

Voilà! Java Panama Polyglot, in other words: Java talking to Swift.

Next, we will look at how Java Panama can talk to the language Python in [Part 3](https://foojay.io/today/java-panama-polyglot-part-3/).

As always, comments and feedback are welcome!
