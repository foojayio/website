---
title: "Running DuckDB's JDBC Driver in a GraalVM Native Image"
date: "2026-08-29T16:22:10+00:00"
lastmod: "2026-08-31T06:26:09+00:00"
description: "DuckDB is an in-process analytical database. You add one JAR to a Java project and get a SQL engine that reads CSV, Parquet, and JSON files directly, with…"
authors:
  - "geertjan-wielenga"
image: "duckdb-square-icon.svg"
categories:
  - "Developer Tools"
  - "DuckDB"
  - "Java"
  - "Performance"
related_posts:
frozen: false
---

[DuckDB](https://duckdb.org/) is an in-process analytical database. You add one JAR to a Java project and get a SQL engine that reads CSV, Parquet, and JSON files directly, with no server to run.

[GraalVM Native Image](https://www.graalvm.org/jdk25/reference-manual/native-image/) compiles a Java program ahead of time into a standalone executable that starts in milliseconds and needs no JVM on the target machine.

Combining the two produces a data tool that is distributed as a single binary, like a Go or Rust program, while using Java libraries.

There is an open issue on the duckdb-java repository, [#180](https://github.com/duckdb/duckdb-java/issues/180), where people have been asking since March 2025 whether this works. The early attempts failed with `UnsatisfiedLinkError`, and the maintainer noted that ahead of time compilation was unlikely to work with the driver as it was.

This article shows that with current versions it ***does*** work, explains why it failed before, and provides a hello world you can build in just a few minutes.

## The Hello World

Save this as `Hello.java`:

```
import java.sql.*;

public class Hello {
    public static void main(String[] a) throws Exception {
        Class.forName("org.duckdb.DuckDBDriver");
        try (Connection c = DriverManager.getConnection("jdbc:duckdb:");
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery("SELECT 42 AS answer, version() AS v")) {
            while (r.next()) System.out.println(r.getInt(1) + " " + r.getString(2));
        }
    }
}
```

The explicit `Class.forName` is important: JDBC normally discovers drivers through `ServiceLoader`, and that lookup is not always visible to Native Image's static analysis. Naming the class avoids the problem.

Download the [driver JAR, `duckdb_jdbc-1.5.5.0.jar`, from Maven Central](https://mvnrepository.com/artifact/org.duckdb/duckdb_jdbc) into the same directory. Then, with GraalVM on your path:

```
sdk use java 25.0.2-graalce
JAR=duckdb_jdbc-1.5.5.0.jar
javac -cp $JAR Hello.java

mkdir -p META-INF/native-image/hello
java --enable-native-access=ALL-UNNAMED \
  -agentlib:native-image-agent=config-output-dir=META-INF/native-image/hello \
  -cp $JAR:. Hello

native-image --no-fallback --enable-native-access=ALL-UNNAMED \
  -cp $JAR:. -H:ConfigurationFileDirectories=META-INF/native-image/hello -o hello Hello
./hello
```

Output:

```
42 v1.5.5
```

The steps are: compile, run once under the tracing agent, build the native image with the recorded metadata, and run the binary. The build takes about 20 seconds on an Apple Silicon Mac. The executable is around 120 MB, of which 108 MB is the DuckDB engine itself.

## What the Agent Recorded

The [DuckDB Java JDBC driver](https://duckdb.org/docs/current/clients/java) is a thin Java layer over a C++ library, and the two sides talk through JNI. Native Image needs to know in advance which Java classes and fields the C++ code will reach back into, because anything not declared is removed from the image. The tracing agent watches a real run and writes that list down.

Open `META-INF/native-image/hello/reachability-metadata.json` and look for the `jni` section. On this driver version it contains about 18 entries under `org.duckdb.*`: the result set metadata class, the date and timestamp types, the vector and struct classes, the scalar and table function wrappers, and a few others. These are the types the engine constructs from the C++ side when it hands results back to Java.

Near the end of the file is a resource entry:

```
{ "glob": "libduckdb_java.so_osx_universal" }
```

The driver JAR contains one native library per platform:

```
 60780968  libduckdb_java.so_linux_amd64
108682352  libduckdb_java.so_osx_universal
 35089408  libduckdb_java.so_windows_amd64
 53584160  libduckdb_java.so_linux_arm64
```

The agent recorded the one it used, and Native Image embeds that file into the executable as a resource. If you build for a different platform than the one you traced on, replace the glob with the matching name. A wildcard that matches all four adds about 150 MB to every binary, so use the specific name.

## Why It Failed Before

The error in the original issue report contains the explanation:

```
java.lang.UnsatisfiedLinkError: Can't load library: <...>/build/debug/libduckdb_java.so_osx_universal
```

The reporter was building on Ubuntu, yet the path contains `build/debug`, a directory from a development checkout of the driver. That path did not come from the reporter's machine. It was computed when the driver JAR was compiled and then frozen into the native image.

The mechanism behind that is class initialization timing. When Native Image builds a program it snapshots the heap, and for every class it decides whether to run the static initializer during the build and store the result, or to leave the initializer to run when the binary starts. `org.duckdb.DuckDBNative` has a static initializer that locates the native library, extracts it from the JAR to a temporary directory if needed, and calls `System.load` with the absolute path.

GraalVM for JDK 17 ran initializers like this at build time whenever its analysis thought that was safe. The path string was therefore computed on the build machine and baked into the binary. When the binary ran anywhere else, `System.load` received a path that did not exist.

Since GraalVM for JDK 22 the default is reversed. Application classes are initialized at run time unless the build can prove the initializer has no side effects. Extracting a file and loading a library are side effects, so `DuckDBNative` now initializes when the executable starts, on the user's machine, exactly as it would on a normal JVM.

Nothing in the script above mentions this, because the default handles it. On an older GraalVM you would add one argument to the build:

```
native-image --initialize-at-run-time=org.duckdb.DuckDBNative ...
```

It has no effect on GraalVM 25 and makes the intent explicit, which is useful if the build will be maintained by people on different releases.

Two other things changed on the driver side. Pull request [#450](https://github.com/duckdb/duckdb-java/issues/450) made library loading more flexible, so the driver can load from the file system by name before falling back to extracting from the JAR. And the driver has never used reflection on its Java side, which is why the JNI list is short and the rest of the metadata is empty.

## A Larger Example

A hello world confirms that the build works but does not show a realistic use. I used the same approach to build a reconciliation tool for a finance team: it reads a day's orders, payouts, and shipments as JSON lines, CSV, and Parquet, runs a handful of SQL checks, and writes an exceptions file. It is a Maven project with picocli for the command line, the SQL in resource files, and the same reachability metadata described above. The native binary answers `--help` in 39 milliseconds and a full check over ten thousand orders in under a second.

One measurement from that project affects how you should plan around startup time. The JVM version of the check takes 1.36 seconds and the native version 0.95 seconds. The gap is smaller than the `--help` comparison suggests because the driver extracts its 108 MB library to a temporary directory every time a connection opens, and that disk write dominates. For a nightly job or an interactive tool it does not matter. For something invoked hundreds of times a minute, look at loading the library from a fixed path next to the binary instead, which the newer driver supports.

## Where This Leaves the Issue

The driver works in a native image today with no source changes. What remains is convenience. The JNI metadata is small and identical across platforms, so the driver could ship it inside the JAR under `META-INF/native-image/org.duckdb/duckdb_jdbc/`, and Native Image would pick it up automatically. Per platform classifier artifacts, or a documented way to place the library beside the executable, would remove the last manual step and the size cost. I have posted the hello world to the issue so the maintainers have a script they can add to CI.

If JVM startup time or the need to install a JDK has prevented you from using DuckDB in a Java command line tool, those constraints no longer apply.

*Tested with duckdb_jdbc 1.5.5.0 and GraalVM Community Edition 25.0.2 on macOS (Apple Silicon) and Linux x64.*
