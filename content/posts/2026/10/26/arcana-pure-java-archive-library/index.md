---
title: "Arcana: One Pure-Java Library for (Almost) Every Archive Format"
date: "2026-10-26"
description: "Arcana extracts ZIP, 7z, RAR 5, zstd, MSI, ISO, DMG and 40+ formats in pure Java 8: no native code, no dependencies, one JAR on Maven Central."
authors:
  - "stephane-bury"
image: "arcana-preview.jpg"
categories:
  - "Library"
  - "Tools"
related_posts:
  - "foojay-podcast-98"
  - "bootstrapping-a-java-file-system"
  - "async-file-io-with-java-and-io_uring"
  - "7-new-vulnerabilities-in-jackson-in-one-day-this-is-what-ai-assisted-security-research-looks-like"
---

Arcana is an open-source Java library and command-line tool that lists, extracts and identifies more than 40 archive, compression and disk image formats, with no native code and no dependencies. It runs on Java 8 or later, fits in a 1.2 MB JAR, and is published on Maven Central as `io.github.realburst:arcana` under the Apache 2.0 licence.

## Why another archive library?

If you have ever had to open "whatever the user uploads" from a Java application, you know the drill. ZIP is in the JDK. Apache Commons Compress covers TAR, 7z, CPIO, AR and the common compressors, but some codecs need extra libraries, and Zstandard goes through a JNI binding. RAR needs another library, and RAR 5 support is hard to find in pure Java. For CAB, MSI, ISO, WIM or an installer `.exe`, the usual answer is to bundle 7-Zip through native bindings, with one binary per OS and CPU.

I wanted one JAR that does all of this, on any JVM, with nothing to install and nothing native to load. That is Arcana: one entry point, format detection by content rather than by file extension, and the same API whether the file is a `.tar.zst`, a RAR 5 volume set, an MSI package or a macOS `.dmg`.

## What Arcana does

Arcana extracts every format it supports, and creates the most common ones.

| Family | Formats | Create |
| --- | --- | --- |
| Archives | ZIP (ZipCrypto, WinZip AES, Deflate64, Zip64), 7z, RAR 4 and RAR 5 (solid, volumes, encrypted headers), TAR, CPIO, AR / DEB, RPM, XAR, ARJ, LHA | ZIP, 7z, TAR, LHA |
| Compressors | GZIP, BZIP2, XZ, LZMA, Zstandard, LZ4, Brotli, Snappy, lzip, lzop, Unix `.Z`, MS SZDD | GZIP, BZIP2, XZ, TAR + gz / bz2 / xz / lz4 |
| Windows | CAB (MSZIP, LZX), MSI (real installed file tree), OLE compound files, CHM, WIM / ESD (XPRESS, LZX, LZMS), self-extracting `.exe` | CAB |
| Disk images | ISO 9660 (Joliet, Rock Ridge), UDF, SquashFS, Apple DMG with HFS+ | - |

Encrypted archives are supported: AES-256 for ZIP and 7z (reading and writing), AES-128 and AES-256 for RAR 4 and RAR 5. A TAR or CPIO inside any compressor is unpacked in one step, and files that are archives in disguise (`.docx`, `.odt`, `.epub`, `.apk`, `.jar`) open like any other archive.

Beyond extraction, Arcana can:

- **identify** any file: archive type and version, executables (PE, ELF, Mach-O), installers, images with their dimensions, PDF, SQLite, fonts;
- **recover** damaged archives: a forced extraction keeps everything readable and writes a report on the state of each file;
- **split** files glued together, such as installers or firmware images;
- **load plugins** for more formats. Official plugins handle Inno Setup, NSIS, InstallShield and WiX Burn installers, and UPX-packed executables.

## Getting started

Add the dependency:

```xml
<dependency>
  <groupId>io.github.realburst</groupId>
  <artifactId>arcana</artifactId>
  <version>1.0.8</version>
</dependency>
```

Extracting and listing never require you to say what the file is: the format is detected from its content.

```java
import be.stef.arcana.Arcana;
import be.stef.arcana.ArcanaEntry;
import be.stef.arcana.analyze.Identification;

// Extract anything: .zip, .rar, .7z, .tar.zst, .msi, .iso, .dmg...
Arcana.extractTo(new File("upload.bin"), new File("out"));

// List the entries without extracting
for (ArcanaEntry e : Arcana.listEntries(new File("backup.rar"))) {
    System.out.println(e.getName() + "  " + e.getUncompressedSize());
}

// Encrypted archive
Arcana.withPassword("secret".getBytes("UTF-8")).extract(new File("secure.7z"), new File("out"));

// What is this file?
Identification id = Arcana.identify(new File("mystery.bin"));
System.out.println(id.type + " - " + id.description);
```

Creating an archive is just as short:

```java
import be.stef.arcana.ArcanaFormat;
import be.stef.arcana.compressor.ZipCompressor;
import be.stef.arcana.plugin.FormatRegistry;

byte[] password = "secret".getBytes("UTF-8");
new ZipCompressor(password).compress(new File("mydir"), new File("secure.zip"));   // WinZip AES-256
FormatRegistry.builtin(ArcanaFormat.SEVEN_Z).createCompressor(9, password).compress(new File("mydir"), new File("vault.7z"));
```

The same JAR is also a command-line tool:

```bash
java -jar Arcana-v1.0.8.jar x setup.exe -o out       # extract
java -jar Arcana-v1.0.8.jar l disk.dmg               # list
java -jar Arcana-v1.0.8.jar i mystery.bin            # identify
java -jar Arcana-v1.0.8.jar r damaged.zip -o rescue  # recover
```

On Windows, an optional script adds "Arcana Unpack / List / Info" to the Explorer right-click menu.

## Safe by default

An archive extractor reads untrusted input by definition, so Arcana treats every file as hostile.

- **No path traversal.** Entry names are sanitized before anything is written: no absolute paths, no `..`, no characters that Windows forbids. A "zip slip" entry cannot leave the destination folder.
- **No decompression bombs.** Every extraction runs under limits you can change at run time (`ExtractionLimits`): an output/input ratio of 1000 once 1 GiB has been written, an optional total size, and a minimum of 256 MiB of free disk space.
- **Bounded memory.** Decoders cap what a header can ask for: the Zstandard window is limited to 128 MiB, like the `zstd` tool, and the 7z key derivation to 2^24 rounds. A crafted header gets an error, not an `OutOfMemoryError`.
- **Checksums are verified.** CRC32, Adler-32, BLAKE2sp, XXH64, HMAC for WinZip AES: when the format stores a checksum, Arcana checks it and reports a mismatch.
- **Plugins must show their code.** A plugin JAR is refused unless it contains its source code, declares its licence and source URL, and uses its own package. That does not make a plugin safe, but it makes it reviewable.

## Under the hood

Every decoder is written in Java: Deflate64, LZMA and LZMA2, PPMd, Zstandard, Brotli, LZ4, LZX, LZMS, XPRESS, LZFSE and LZVN, ADC, LZO1X, the RAR 4 and RAR 5 algorithms, AES and the key derivations. The core is about 80,000 lines of Java 8 and has no dependency outside the JDK. It is written from the public specifications: PKWARE APPNOTE, the 7-Zip format notes, the Microsoft open specifications (MS-CAB, LZX, MS-XCA), RFC 8878 for Zstandard, RFC 7932 for Brotli, Apple TN1150 for HFS+.

A few design choices shape the library:

- **Detection by content.** Magic numbers first, then trailers (a DMG is recognized by the "koly" block in its last 512 bytes), and only then the file extension. A renamed file still opens.
- **Streaming when the format allows it.** Single-stream formats decode from an `InputStream` with bounded buffers. The Zstandard decoder keeps only the window of the current frame, so a multi-gigabyte `.tar.zst` decodes in a few dozen MB of heap.
- **Random access when the format needs it.** Disk images are read lazily: a DMG partition is decoded chunk by chunk with a small cache, never inflated in memory.
- **Real names, not internal keys.** An MSI is extracted as the file tree it installs, with the names from its `File` and `Directory` tables, not as a cabinet of cryptic keys.

Each release goes through a regression suite. About 180 small samples are committed with the code: one per format and method, plus damaged files that must fail cleanly, never hang. A separate corpus of real-world files (installers, DMG images from GitHub releases, MSI packages) is checked against committed SHA-256 references. Every format also has a technical page in the repository, covering detection, layout, supported methods, limits and the sources used.

## Current limits

Arcana is a young project, and the repository keeps a public list of known issues for every format. Before you rely on it, the main limits are:

- **Extraction first.** Writing is limited to ZIP, 7z, TAR (+ gz, bz2, xz, lz4), GZIP, BZIP2, XZ, LHA and CAB. Arcana cannot create RAR, Zstandard or Brotli files, and it cannot update an existing archive in place.
- **Some file systems are only copied.** APFS volumes inside a DMG are extracted as a raw image, not as files. Encrypted and sparse DMG images, split WIM files (`.swm`) and some rare compression methods (old LHA methods, Quantum in CAB) are not supported.
- **Pure Java has a cost.** The decoders are fast enough for most uses, but they do not match native code. In benchmarks run for the project, the Zstandard decoder reaches about 60% of the speed of the native `zstd` library.
- **Edge cases remain.** A few damaged or unusual files still give a raw Java exception instead of a clear message, or are less tolerated than by the reference tool. These cases are documented with the file and line of the code concerned, so you can judge before using a format in production.

If a file fails, an issue with the command line and, when possible, the file or a way to rebuild it, is the fastest route to a fix.

## Try it

The quickest test is to point Arcana at the strangest file in your Downloads folder and run `arcana i` on it, then `arcana x`. If you maintain a Java application that receives archives from users, try replacing a native binding or a chain of libraries with one dependency, and tell me what breaks.

- Source, releases and documentation: [Arcana on GitHub](https://github.com/RealBurst/arcana)
- Maven Central: `io.github.realburst:arcana`
- [Format pages and known issues](https://github.com/RealBurst/arcana/tree/main/doc/formats)
- [Writing a plugin for your own format](https://github.com/RealBurst/arcana/blob/main/PLUGIN-API.md)

Issues, sample files that fail, and plugins are all welcome. And if Arcana saves you a dependency or a native binary, a star on GitHub helps other Java developers find it.
