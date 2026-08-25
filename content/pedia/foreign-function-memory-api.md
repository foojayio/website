---
title: "Foreign Function & Memory API"
description: "The Foreign Function & Memory (FFM) API, finalised in Java 22 (JEP 454), provides a safe, efficient, and pure-Java way to interact with native code and off-heap memory — replacing the older, error-prone Java Native Interface (JNI). Foreign memory access ..."
url: "/pedia/foreign-function-memory-api/"
frozen: false
---

The Foreign Function \& Memory (FFM) API, finalised in Java 22 (JEP 454), provides a safe, efficient, and pure-Java way to interact with native code and off-heap memory — replacing the older, error-prone Java Native Interface (JNI).

**Foreign memory access** allows Java programs to allocate, read, and write memory outside the Java heap using `MemorySegment`. Off-heap allocation is useful for large data structures that should not be managed by the GC, for memory-mapped files, and for shared memory between processes. The API enforces lifetime tracking: a `MemorySegment` is associated with an `Arena`, and its memory is released deterministically when the arena is closed.

**Foreign function calls** allow Java programs to call native functions in shared libraries (`.so`, `.dll`, `.dylib`) without writing any C or JNI glue code. A `Linker` resolves native symbols and produces a `MethodHandle` that can be called from Java. The API handles ABI conventions, argument marshalling, and return type mapping automatically for all supported platforms.

```java
try (Arena arena = Arena.ofConfined()) {
    MethodHandle strlen = Linker.nativeLinker()
        .downcallHandle(
            Linker.nativeLinker().defaultLookup().find("strlen").orElseThrow(),
            FunctionDescriptor.of(JAVA_LONG, ADDRESS));
    MemorySegment str = arena.allocateFrom("Hello, FFM!");
    long len = (long) strlen.invoke(str); // 11
}
```

The FFM API supersedes both JNI (for calling native functions) and the older `sun.misc.Unsafe` (for off-heap memory). JNI remains available for backwards compatibility but is no longer the recommended approach for new code.

## See Also

* [Bytecode](/pedia/bytecode/)
* [Java Module System (JPMS)](/pedia/java-module-system-jpms/)
* [Java Native Interface (JNI)](/pedia/java-native-interface-jni/)
* [OpenJDK Projects](/pedia/openjdk-projects/)
* [Project Panama](/pedia/project-panama/)
