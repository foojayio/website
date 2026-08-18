---
title: "Writing C Code in Java"
slug: "writing-c-code-in-java"
date: "2023-12-20T09:21:56+00:00"
lastmod: "2023-12-20T09:21:57+00:00"
description: "The Foreign Function & Memory API (also called Project Panama) has come a long way since it started."
authors:
  - "johannes-bechberger"
image: "panama-2000x981-1.png"
categories:
  - "Java"
  - "Java Core"
tags:
related_posts:
  - "java-panama-polyglot-part1"
  - "java-panama-polyglot-swift-part-2"
  - "java-panama-polyglot-part-3"
  - "java-native-memory-allocation-ffm-api"
enlighterjs: true
frozen: false
---

![](https://mostlynerdless.de/wp-content/uploads/2023/12/panama-2000x981.png)

**The Foreign Function \& Memory API (also called Project Panama) has come a long way since it started. You can find the latest version implemented in JDK 21 as a preview feature (use `--enable-preview` to enable it) which is specified by the [JEP 454](https://openjdk.org/jeps/454):**
> By efficiently invoking foreign functions (i.e., code outside the JVM), and by safely accessing foreign memory (i.e., memory not managed by the JVM), the API enables Java programs to call native libraries and process native data without the brittleness and danger of JNI.
> [JEP 454](https://openjdk.org/jeps/454)

This is pretty helpful when trying to build wrappers around existing native libraries. Other languages, like Python with [ctypes](https://docs.python.org/3/library/ctypes.html), have had this for a long time, but Java is getting a proper API for native interop, too. Of course, there is the Java Native Interface (JNI), but JNI is cumbersome and inefficient (call-sites aren't inlined, and the overhead of converting data from Java to the native world and back is huge).

Be aware that the API is still in flux. Much of the existing non-OpenJDK documentation is not in sync.

Example
-------

Now to my main example: Assume you're tired of all the abstraction of the Java I/O API and just want to read a file using the traditional I/O functions of the C standard lib (like [read_line.c](https://github.com/parttimenerd/panama-examples/blob/main/misc/read_line.c)): we're trying to read the first line of the passed file, opening the file via [`fopen`](https://www.man7.org/linux/man-pages/man3/fopen.3.html), reading the first line via [`gets`](https://www.man7.org/linux/man-pages/man3/fgets.3.html), and closing the file via [`fclose`](https://www.man7.org/linux/man-pages/man3/fclose.3.html).

```c
#include "stdio.h"
#include "stdlib.h"

int main(int argc, char *argv[]) {
  FILE* file = fopen(argv[1], "r");
  char* line = malloc(1024);
  fgets(line, 1024, file);
  printf("%s", line);
  fclose(file);
  free(line);
}
```


This would have involved writing C code in the old JNI days, but we can access the required C functions directly with Panama, wrapping the C functions and writing the C program as follows in Java:

```java
public static void main(String[] args) {
    var file = fopen(args[0], "r");
    var line = gets(file, 1024);
    System.out.println(line);
    fclose(file);
}
```


But do we implement the wrapper methods? We start with the `FILE* fopen(char* file, char* mode)` function which opens a file. Before we can call it, we have to get hold of its `MethodHandle`:

```java
private static MethodHandle fopen = Linker.nativeLinker().downcallHandle(
        lookup("fopen"),
        FunctionDescriptor.of(/* return */ ValueLayout.ADDRESS, 
            /* char* file */ ValueLayout.ADDRESS, 
            /* char* mode */ ValueLayout.ADDRESS));
```


This looks up the `fopen` symbol in all the libraries that the current process has loaded, asking both the `NativeLinker` and the `SymbolLookup`. This code is used in many examples, so we move it into the function `lookup`:

```
public static MemorySegment lookup(String symbol) {
    return Linker.nativeLinker().defaultLookup().find(symbol)
                 .or(() -> SymbolLookup.loaderLookup().find(symbol))
                 .orElseThrow();
}
```


The look-up returns the memory address at which the looked-up function is located.

We can proceed with the address of `fopen` and use it to create a [MethodHandle](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/invoke/MethodHandle.html) that calls down from the JVM into native code. For this, we also have to specify the descriptor of the function so that the JVM knows how to call the `fopen` handle properly.

But how do we use this handle? Every handle has an [invokeExact](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/invoke/MethodHandle.html#invokeExact(java.lang.Object...)) function (and an [invoke](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/invoke/MethodHandle.html#invoke(java.lang.Object...)) function that allows the JVM to convert data) that we can use. The only problem is that we want to pass strings to the `fopen` call. We cannot pass the strings directly but instead have to allocate them onto the C heap, copying the chars into a C string:

```java
public static MemorySegment fopen(String filename, String mode) {
    try (var arena = Arena.ofConfined()) {
        return (MemorySegment) fopen.invokeExact(
                arena.allocateUtf8String(filename),
                arena.allocateUtf8String(mode));
    } catch (Throwable t) {
        throw new RuntimeException(t);
    }
}
```


We use a confined arena for allocations, which is cleaned after exiting the try-catch. The newly allocated strings are then used to invoke `fopen`, letting us return the `FILE*`.

*Older tutorials might mention MemorySessions, but they are removed in JDK 21.*

After opening the file, we can focus on the `char* fgets(char* buffer, int size, FILE* file)` function. This function is passed a buffer of a given size, storing the next line from the passed file in the buffer.

Getting a MethodHandle is similar to `fopen`:

```java
private static MethodHandle fgets = Linker.nativeLinker().downcallHandle(
        PanamaUtil.lookup("fgets"),
        FunctionDescriptor.of(ValueLayout.ADDRESS, 
                              ValueLayout.ADDRESS, 
                              ValueLayout.JAVA_INT, 
                              ValueLayout.ADDRESS));
```


Only the wrapper method differs because we have to allocate the buffer in the arena:

```java
public static String gets(MemorySegment file, int size) {
    try (var arena = Arena.ofConfined()) {
        var buffer = arena.allocateArray(ValueLayout.JAVA_BYTE, size);
        var ret = (MemorySegment) fgets.invokeExact(buffer, size, file);
        if (ret == MemorySegment.NULL) {
            return null; // error
        }
        return buffer.getUtf8String(0);
    } catch (Throwable t) {
        throw new RuntimeException(t);
    }
}
```


Finally, we can implement the `int fclose(FILE* file)` function to close the file:

```java
private static MethodHandle fclose = Linker.nativeLinker().downcallHandle(
        PanamaUtil.lookup("fclose"),
        FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS));

public static int fclose(MemorySegment file) {
    try {
        return (int) fclose.invokeExact(file);
    } catch (Throwable e) {
        throw new RuntimeException(e);
    }
}
```


You can find the source code in my [panama-examples](https://github.com/parttimenerd/panama-examples) repository on GitHub (file [HelloWorld.java](https://github.com/parttimenerd/panama-examples/blob/main/src/main/java/me/bechberger/panama/HelloWorld.java)) and run it on a Linux x86_64 machine via

```bash
> ./run.sh HelloWorld LICENSE # build and run
                                 Apache License
```


which prints the first line of the license file.

Errno
-----

We didn't care much about error handling here, but sometimes, we want to know precisely why a C function failed. Luckily, the C standard library on Linux and other Unixes has `errno`:
> Several standard library functions indicate errors by writing positive integers to `errno`.
> [CPP Reference](https://en.cppreference.com/w/cpp/error/errno)

On error, `fopen` returns a null pointer and sets `errno`. You can find information on all the possible error numbers on the man page for the [`open`](https://www.man7.org/linux/man-pages/man2/open.2.html) function.

We only have to have a way to obtain the `errno` directly after a call, we have to capture the call state and declare the capture-call-state option in the creation of the MethodHandle for `fopen`:

```java
try (var arena = Arena.ofConfined()) {
    // declare the errno as state to be captured, 
    // directly after the downcall without any interence of the
    // JVM runtime
    StructLayout capturedStateLayout = Linker.Option.captureStateLayout();
    VarHandle errnoHandle = 
        capturedStateLayout.varHandle(
            MemoryLayout.PathElement.groupElement("errno"));
    Linker.Option ccs = Linker.Option.captureCallState("errno");

    MethodHandle fopen = Linker.nativeLinker().downcallHandle(
            lookup("fopen"), 
            FunctionDescriptor.of(POINTER, POINTER, POINTER), 
            ccs);

    MemorySegment capturedState = arena.allocate(capturedStateLayout);
    try {
        // reading a non-existent file, this will set the errno
        MemorySegment result = 
            (MemorySegment) fopen.invoke(capturedState,
                // for our example we pick a file that doesn't exist
                // this ensures a proper error number
                arena.allocateUtf8String("nonexistent_file"),
                arena.allocateUtf8String("r"));
        int errno = (int) errnoHandle.get(capturedState);
        System.out.println(errno);
        return result;
    } catch (Throwable e) {
        throw new RuntimeException(e);
    }
}
```


To convert this error number into a string, we can use the [char* strerror(int errno)](https://www.man7.org/linux/man-pages/man3/strerror.3.html) function:

```java
// returned char* require this specific type
static AddressLayout POINTER = 
    ValueLayout.ADDRESS.withTargetLayout(
        MemoryLayout.sequenceLayout(JAVA_BYTE));
static MethodHandle strerror = Linker.nativeLinker()
        .downcallHandle(lookup("strerror"),
                FunctionDescriptor.of(POINTER, 
                    ValueLayout.JAVA_INT));

static String errnoString(int errno){
    try {
        MemorySegment str = 
            (MemorySegment) strerror.invokeExact(errno);
        return str.getUtf8String(0);
    } catch (Throwable t) {
        throw new RuntimeException(t);
    }
}
```


When we then print the error string in our example after the `fopen` call, we get:

```
No such file or directory
```


This is as expected, as we hard-coded a non-existent file in the `fopen` call.

JExtract
--------

Creating all the MethodHandles manually can be pretty tedious and error-prone. JExtract can parse header files, generating MethodHandles and more automatically. You can download [jextract](https://jdk.java.net/jextract/) on the project page.

For our example, I wrote a small wrapper around jextract that automatically downloads the latest version and calls it on the [misc/headers.h](https://github.com/parttimenerd/panama-examples/blob/main/misc/headers.h) file to create MethodHandles in the class `Lib`. The headers file includes all the necessary headers to run examples:

```c
#include <errno.h>
#include <string.h>
#include <unistd.h>
#include <stdio.h>
```


For example the `fgets` function, jextract generates as an entry point the following:

```java
public static MethodHandle fopen$MH() {
    return RuntimeHelper.requireNonNull(constants$48.const$0,"fopen");
}
/**
 * {@snippet :
 * FILE* fopen(char* __filename, char* __modes);
 * }
 */
public static MemorySegment fopen(MemorySegment __filename, MemorySegment __modes) {
    var mh$ = fopen$MH();
    try {
        return (java.lang.foreign.MemorySegment)mh$.invokeExact(__filename, __modes);
    } catch (Throwable ex$) {
        throw new AssertionError("should not reach here", ex$);
    }
}
```


Of course, we still have to take care of the string allocation in our wrapper, but this wrapper gets significantly smaller:

```java
public static MemorySegment fopen(String filename, String mode) {
    try (var arena = Arena.ofConfined()) {
        // using the MethodHandle that has been generated 
        // by jextract
        return Lib.fopen( 
                arena.allocateUtf8String(filename),
                arena.allocateUtf8String(mode));
    }
}
```


You can find the example code in the GitHub repository in the file [HelloWorldJExtract.java](https://github.com/parttimenerd/panama-examples/blob/main/src/main/java/me/bechberger/panama/HelloWorldJExtract.java). I integrated jextract via a wrapper directly into the Maven build process, so just `mvn package` to run the tool.

More Information
----------------

There are many other resources on Project Panama, but be aware that they might be dated. Therefore, I recommend reading JEP 454, which describes the newly introduced API in great detail. Additionally, the talk ["The Panama Dojo: Black Belt Programming with Java 21 and the FFM API" by Per Minborg](https://www.youtube.com/watch?v=t8c1Q2wJOoM) at this year's Devoxx Belgium is a great introduction:

{{< youtube t8c1Q2wJOoM >}}

As well as the talk by Maurizio Cimadamore at this year's JVMLS:

{{< youtube kUFysMkMS00 >}}

Conclusion
----------

Project Panama greatly simplifies interfacing with existing native libraries. I hope it will gain traction after leaving the preview state with the upcoming JDK 22, but it should already be stable enough for small experiments and side projects.

I hope my introduction gave you a glimpse into Panama; as always, I'm happy for any comments, and I'll see you next week(ish) for the start of a new blog series.

**This article is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com/), making profiling and debugging easier for everyone. Thank you to my colleague [Martin Dörr](https://github.com/TheRealMDoerr), who helped me with Panama and ported Panama to PowerPC. This article appeared first on my personal blog [mostlynerdless.de](https://mostlynerdless.de/blog/2023/12/11/from-c-to-java-code-using-panama/)**.
