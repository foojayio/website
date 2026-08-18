---
title: "Using Async-Profiler and Jattach Programmatically with AP-Loader"
slug: "using-async-profiler-and-jattach-programmatically-with-ap-loader"
date: "2023-05-04T11:45:20+00:00"
lastmod: "2023-05-04T11:45:22+00:00"
description: "AP-Loader wraps async-profiler and its tools with an API in a cross-platform JAR. This article is about the API."
authors:
  - "johannes-bechberger"
image: "Untitled2.png"
categories:
  - "Performance"
  - "Tools"
tags:
related_posts:
  - "foojay-podcast-14"
  - "ap-loader-a-new-way-to-use-and-embed-async-profiler"
  - "a-short-primer-on-java-debugging-internals"
  - "couldnt-we-just-use-asyncgetcalltrace-in-a-separate-thread"
enlighterjs: true
frozen: false
---

Using [async-profiler](https://github.com/jvm-profiling-tools/async-profilerhttps://github.com/jvm-profiling-tools/async-profiler) and jattach can be quite a hassle. First, you have to download the proper archive from GitHub for your OS and architecture; then, you have to unpack it and place it somewhere.

It gets worse if you want to embed it into your library, agent, or application. Library developers cannot just use maven dependency but have to create wrapper code and build scripts that deal with packaging the binaries themselves, or worse, they depend on a preinstalled version which they do not control.

In November 2022, I started the ap-loader project to remedy this situation: I wrapped async-profiler and jattach in a platform-independent JAR which can be pulled from maven central. I already wrote an article on its essential features: [AP-Loader: A new way to use and embed async-profiler](https://mostlynerdless.de/blog/2022/11/21/ap-loader-a-new-way-to-use-and-embed-async-profiler/).
![Flamegraph for a recording of profiling data for the dacapo benchmark suite](https://mostlynerdless.de/wp-content/uploads/2022/11/Untitled2.png)

In this article, I'm focusing on its programmatic usage: Async-profiler can be used in a library to gather profiling data of the current or a different process, but the profiler distribution contains more: It contains converters to convert from JFR to flamegraphs, and [jattach](https://github.com/jattach/jattach) to attach a native agent dynamically to (potentially the current) JVM and send commands to it.

*This article assumes that you're familiar with the basic usage of async-profiler. If you are not, consider reading the async-profiler [README](https://github.com/async-profiler/async-profiler/blob/master/README.md) or the [Async-profiler - manual by use cases](https://krzysztofslusarski.github.io/2022/12/12/async-manual.html) by Krzysztof Ślusarski.*

The ap-loader library allows you to depend on a specific version of async-profiler using [gradle or maven](https://central.sonatype.com/search?smo=true&namespace=me.bechberger&q=ap-loader-):

```xml
<dependency>
    <groupId>me.bechberger</groupId>
    <artifactId>ap-loader-all</artifactId>
    <version>2.9-5</version>
</dependency>
```


There are multiple maven artifacts: [ap-loader-all](https://central.sonatype.com/artifact/me.bechberger/ap-loader-all) which contains the native libraries for all platforms for which async-profiler has pre-built libraries and artifacts that only support a single platform like [`ap-loader-macos`](https://central.sonatype.com/artifact/me.bechberger/ap-loader-macos). I recommend using the `ap-loader-all` if you don't know what you're doing, the current release is still tiny, with 825KB.

The version number consists of the async-profiler version and the version (here 2.9) of the ap-loader support libraries (here 5). I'm typically only publishing the newest ap-loader version for the latest async-profiler. The changes in ap-loader are relatively minimal, and I keep the API stable between versions.

The ap-loader library consists of multiple parts:

* `AsyncProfilerLoader` class: Wraps async-profiler and jattach, adding a few helper methods
* `converter` package: Contains all classes from the async-profiler converter JAR and helps to convert between multiple formats
* `AsyncProfiler` class: API for async-profiler itself, wrapping the native library.

All but the `AsyncProfilerLoader` class is just copied from the underlying async-profiler release. ap-loader contains all Java classes from async-profiler, but I omit the helper classes here for brevity.

AsyncProfilerLoader
-------------------

This is the main entry point to ap-loader; it lives in the `one.profiler` package like the AsyncProfiler class. Probably the most essential method is `load`:

### Load

The `load` method loads the included async-profiler library for the current platform:

```java
AsyncProfiler profiler = AsyncProfilerLoader.load();
```


It returns the instantiated API wrapper class. The method throws an `IllegalStateException` if the present ap-loader dependencies do not support the platform and an `IOException` if loading the library resulted in other problems.

Newer versions of the AsyncProfiler API contain the `AsyncProfiler#getInstance()` method, which can also load an included library. The main difference is that you have to include the native library for all the different platforms, replicating all the work of the ap-loader build system every time you update async-profiler.

Dealing with multiple platforms is hard, and throwing an exception when not supporting a platform might be inconvenient for your use case. AsyncProfilerLoader has the `loadOrNull` method which returns `null` instead and also the `isSupported` to check whether the current combination of OS and CPU is supported. A typical use case could be:

```java
if (AsyncProfilerLoader.isSupported()) {
  AsyncProfilerLoader.load().start(...);
} else {
  // use JFR or other fall-backs
}
```


This might still throw `IOException`s, but they should never happen in normal circumstances and are probably by problems that should be investigated, being either an error in ap-loader or in your application.

If you want to merely get the path to the extracted libAsyncProfiler, then use the `getAsyncProfilerPath method` which throws the same exceptions as the `load` method. A similar method exists for jattach (`getJattachPath`).

Execute Profiler
----------------

The async-profiler project contains the [profiler.s](https://github.com/jvm-profiling-tools/async-profiler#profiler-options)`h` script (will be replaced by `asprof` starting with async-profiler 2.10):
> To run the agent and pass commands to it, the helper script `profiler.sh` is provided. A typical workflow would be to launch your Java application, attach the agent and start profiling, exercise your performance scenario, and then stop profiling. The agent's output, including the profiling results, will be displayed in the Java application's standard output.
> [Async-Profiler documentation](https://github.com/async-profiler/async-profiler/commit/e3b7bfca227ae5c916f00abfacf0e61291df3a67)

This helper script is also included in ap-loader and allows you to use the script on the command-line via `java -jar ap-loader profiler ...`, the API exposes this functionality via `ExecutionResult executeProfiler(String... args)`.

```java
AsyncProfilerLoader.executeProfiler("-e", "wall", "8983")
// is equivalent to
./profiler.sh -e wall -t -i 5ms -f result.html 8983
```


The `executeProfiler` method throws an `IllegalStateException` if the current platform is not supported. The returned instance of `ExecutionResult` contains the standard and error output:

```java
public static class ExecutionResult {
  private final String stdout;
  private final String stderr;
    // getter and constructor
    ...
}
```


`executeProfiler` throws an `IOException` if the profiler execution failed.

Execute Converter
-----------------

You cannot only use the converter by using the classes from the `one.profiler.converter`, but you can also execute the converter by calling `ExecutionResult executeProfiler(String... args)`, e.g., the following:

```java
AsyncProfilerLoader.executeConverter(
  "jfr2flame", "<input.jfr>", "<output.html>")
// is equivalent to
java -cp converter.jar \
  jfr2flame <input.jfr> <output.html>
```


The `executeConverter` returns the output of the conversion tool on success and throws an `IOException` on error, as before.

JAttach
-------

There are multiple ways to use the embedded [jattach](https://github.com/jattach/jattach) besides using the binary returned by `getJattachPath`: `ExecutionResult executeJattach(String... args)` and `boolean jattach(Path agentPath[, String arguments])`.

`executeJattach` works similar to `executeProfiler`, e.g.:

```java
AsyncProfilerLoader.executeJattach(
  "<pid>", "load", "instrument", "false", "javaagent.jar=arguments")
// is equivalent to
jattach <pid> load instrument false "javaagent.jar=arguments"
```


This runs the same as jattach with the only exception that every string that ends with  
`libasyncProfiler.so` is mapped to the extracted async-profiler library for the load command.  

One can, therefore, for example, start the async-profiler on a different JVM via the following:

```java
AsyncProfilerLoader.executeJattach(
  PID, "load", "libasyncProfiler.so", true, "start")
```


But this use case can, of course, be accomplished by using the `executeProfiler` method, which internally uses jattach.

A great use case for jattach is to attach a custom native agent to the currently running JVM. Starting with JVM 9 doing this via [VirtualMachine#attach](https://docs.oracle.com/en/java/javase/17/docs/api/jdk.attach/com/sun/tools/attach/VirtualMachine.html#attach(java.lang.String)) [throws](https://stackoverflow.com/questions/50498102/how-to-set-jdk-attach-allowattachself-true-globally) an IOException if you try this without setting `-Djdk.attach.allowAttachSelf=true`. The `boolean jattach(Path agentPath[, String arguments])` methods simplify this, constructing the command line arguments for you and returning true if jattach succeeded, e.g.:

```java
AsyncProfilerLoader.jattach("libjni.so")
```


This attaches the `libjni.so` agent to the current JVM. The process id of this JVM can be obtained by using the `getProcessId` method.

Extracting a Native Library
---------------------------

I happen to write many small projects for testing profilers that often require loading a native library from the resources folder; an[example](https://github.com/parttimenerd/trace_validation/blob/78ad8dd70233b33c266bcec834b3c808568425e1/src/runtime/me/bechberger/trace/NativeChecker.java#LL11C1-L44C6) can be found in the [trace_validation](https://github.com/parttimenerd/trace_validation) ([blog post](https://mostlynerdless.de/blog/2023/03/14/validating-java-profiling-apis/)) project:

```java
/**
 * extract the native library and return its temporary path
 */
public static synchronized Path getNativeLibPath(
 ClassLoader loader) {
  if (nativeLibPath == null) {
    try {
      String filename = System.mapLibraryName(NATIVE_LIB);
      InputStream in = loader.getResourceAsStream(filename);
      // ...
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  return nativeLibPath;
}
```


I, therefore, added the `extractCustomLibraryFromResources` method:

```java
/**                                                                                                                                        
 * Extracts a custom native library from the resources and 
 * returns the alternative source if the file is not 
 * in the resources.                                                                                                    
 *                                                                                                                                         
 * If the file is extracted, then it is copied to 
 * a new temporary folder which is deleted upon JVM exit.                            
 *                                                                                                                                         
 * This method is mainly seen as a helper method 
 * to obtain custom native agents for #jattach(Path) and                          
 * #jattach(Path, String). It is included in ap-loader 
 * to make it easier to write applications that need                           
 * custom native libraries.                                                                                                           
 *                                                                                                                                         
 * This method works on all architectures.                                                                                          
 *                                                                                                                                         
 * @param classLoader the class loader to load 
 *                 the resources from                                                                          
 * @param fileName the name of the file to copy, 
 *                 maps the library name if the fileName 
 *                 does not start with "lib", e.g. "jni" 
 *                 will be treated as "libjni.so" on Linux 
 *                 and as "libjni.dylib" on macOS                                       
 * @param alternativeSource the optional resource directory 
 *                 to use if the resource is not found in 
 *                 the resources, this is typically the case 
 *                 when running the application from an IDE, 
 *                 an example would be "src/main/resources" 
 *                 or "target/classes" for maven projects                                                    
 * @return the path of the library                                                                                                         
 * @throws IOException if the extraction fails and 
 *                  the alternative source is not present 
 *                  for the current architecture                      
 */                                                                                                                                        
public static Path extractCustomLibraryFromResources(
  ClassLoader classLoader, String fileName, 
  Path alternativeSource) throws IOException
```


This can be used effectively together with jattach to attach a native agent from the resources to the current JVM:

```java
// extract the agent first from the resources
Path p = one.profiler.AsyncProfilerLoader.
  extractCustomLibraryFromResources(
    ....getClassLoader(), "library name");
// attach the agent to the current JVM
one.profiler.AsyncProfilerLoader.jattach(p, "optional arguments")
// -> returns true if jattach succeeded
```


*This use-case comes from a profiler test helper library on which I hope to write an article in the near future.*

Conclusion
----------

ap-loader makes it easy to use async-profiler and its included tools programmatically without creating complex build systems. The project is regularly updated to keep pace with the newest stable async-profiler version; updating a version just requires changing a single dependency in your dependencies list.

The ap-loader is mature, so try it and tell me about it. I'm happy to help with any issues you have with this library, so feel free to write to me or [create an issue on GitHub](https://github.com/jvm-profiling-tools/ap-loader/issues).

***This project is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com), making profiling easier for everyone. This article first appeared on my personal blog [mostlynerdless.d](https://mostlynerdless.de/blog/2022/11/21/ap-loader-a-new-way-to-use-and-embed-async-profiler/)*** ****[e](https://mostlynerdless.de/blog/2022/11/21/ap-loader-a-new-way-to-use-and-embed-async-profiler/).****

<br />
