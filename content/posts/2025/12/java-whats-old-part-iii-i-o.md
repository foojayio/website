---
title: "Java, What's Old? Part III - I/O"
slug: "java-whats-old-part-iii-i-o"
date: "2025-12-09T08:23:00+00:00"
description: "Let's see input and output hidden gems in the Java."
authors:
  - "anthony-goubard"
image: "https://foojay.io/wp-content/uploads/2025/06/old-coffee.jpg"
categories:
  - "Java"
  - "Java Core"
tags:
related_posts:
  - "java-whats-old-part-ii-utils"
  - "java-whats-old-part-i-collections"
  - "boxlang-aws-azure-and-google-secrets-manager-module-released"
  - "virtual-thread-pinning-field-guide"
frozen: false
---

After Java, [What's Old? Part I: Collections](https://foojay.io/today/java-whats-old-part-i-collections/) and [Java, What's Old? Part II: Utils](https://foojay.io/today/java-whats-old-part-ii-utils/), let's now have a look at less-known old input/output classes of the JDK that can still be useful.

Everything in this series will be in Java 8 and later, so after reading this article, you will be able to use it in your projects.

Scanner {#h2-0-scanner}
-----------------------

[Scanner](https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html) is a class in `java.util` package that helps you parse files and input stream.

This class has support for regular expressions and Java data types.

Here are a few examples

* `new Scanner(file).useDelimiter("\\p{Space}").nextInt();`  
  will get the first integer delimited by spaces
* `new Scanner(inputStream).useDelimiter("\\A").next();`  
  will read the entire stream and return a String, as \\A pattern means beginning of the stream.
* `new Scanner(System.in).nextLine();`  
  will read the user input line on the console

MappedByteBuffer {#h2-1-mappedbytebuffer}
-----------------------------------------

[MappedByteBuffer](https://docs.oracle.com/javase/8/docs/api/java/nio/MappedByteBuffer.html) is part of `java.nio` package and is a [ByteBuffer](https://docs.oracle.com/javase/8/docs/api/java/nio/ByteBuffer.html) object whose content is a memory-mapped region of a file.

To create a new instance, use the [FileChannel.map](https://docs.oracle.com/javase/8/docs/api/java/nio/channels/FileChannel.html#map-java.nio.channels.FileChannel.MapMode-long-long-) method.

It has different mode: read-only, read/write or private (also called [copy-on-write](https://en.wikipedia.org/wiki/Copy-on-write))

This class was quite often used in the [1 Billion Row](https://github.com/gunnarmorling/1brc/tree/main) Challenge due to its efficiency.

Compared to a byte\[\], it has the advantages to have more methods such as `nextLong()`, `asLongBuffer()` or `mismatch(ByteBuffer)`.

RandomAccessFile {#h2-2-randomaccessfile}
-----------------------------------------

A bit similar in term of functionality is the [RandomAccessFile](https://docs.oracle.com/javase/8/docs/api/java/io/RandomAccessFile.html) class.

This class exists since JDK 1.0 and allows to read or write any part of a local file.

This class has many methods to read and write data such as `readLong()`, `readLine()`, `read(byte[])`, `writeUTF(String)`

LineNumberReader {#h2-3-linenumberreader}
-----------------------------------------

[LineNumberReader](https://docs.oracle.com/javase/8/docs/api/java/io/LineNumberReader.html) is a not well know class that exists since JDK1.1.

This class is a [BufferedReader](https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html) that has the convenient `getLineNumber()` method. Like BufferedReader it also offers the useful `readLine()` method.

`Scanner`, `RandomAccessFile` and `LineNumberReader` are `AutoCloseable` classes, so don't forget to call the `close()` method or put them in a `try()` block.

Conclusion {#h2-4-conclusion}
-----------------------------

This is the last part of this "Java, What's Old?" series. With [part 1](https://foojay.io/today/java-whats-old-part-i-collections/) and [part 2](https://foojay.io/today/java-whats-old-part-ii-utils/), I hope some of you had the reaction "*Hey, I didn't know that! That might be useful.* ". If so, don't forget to share these articles online. And again, thank you to the [Arnhem JUG](https://www.meetup.com/nl-NL/arnhemjug/) for reviewing and letting me show this as presentation.
