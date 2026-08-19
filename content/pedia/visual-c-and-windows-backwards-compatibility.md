---
title: "OpenJDK with Visual C++"
description: "Historical context: This article describes the Visual C++ considerations that applied during the Java 8 / early Java 11 era (2018–2021). It is retained for reference. For current Windows build requirements, see the OpenJDK build documentation. Background OpenJDK on Windows ..."
url: "/pedia/visual-c-and-windows-backwards-compatibility/"
frozen: false
---

> **Historical context:** This article describes the Visual C++ considerations that applied during the Java 8 / early Java 11 era (2018–2021). It is retained for reference. For current Windows build requirements, see the [OpenJDK build documentation](https://openjdk.org/guide/).

## Background

OpenJDK on Windows is compiled with Microsoft Visual C++ (MSVC). The choice of MSVC version has historically been conservative: using an older compiler version made it easier to maintain backwards compatibility with older Windows releases while keeping the runtime DLL dependencies minimal.

For Java 8, OpenJDK long used Visual C++ 10 (Visual Studio 2010) to maintain compatibility with Windows XP and later Windows 7. This meant bundling `msvcr100.dll` in `<JRE>/bin`. Visual Studio 2010 / VC++10 was retired by Microsoft in July 2020, at which point Java 8 began transitioning to VC++14 (Visual Studio 2015 and later).

## Current State

From Java 11 onward, OpenJDK has consistently used Visual C++ 14.x (the runtime shared by Visual Studio 2015, 2017, 2019, and 2022 — Microsoft kept the same major runtime version across these releases). The bundled runtime is `vcruntime140.dll`. Visual Studio 2022 is the standard build tool for current OpenJDK versions (Java 21, 25).  

| **Visual Studio version** |    **VC++ runtime**     | **JDK versions** |
|---------------------------|-------------------------|------------------|
| VS 2010                   | 10 (msvcr100.dll)       | Java 8 (legacy)  |
| VS 2015–2019              | 14.x (vcruntime140.dll) | Java 11–17       |
| VS 2019–2022              | 14.x (vcruntime140.dll) | Java 17–25+      |

Windows 7 and Server 2008 are no longer supported by any current OpenJDK version. The minimum supported Windows version for Java 21+ is Windows 10 / Server 2016.

## The DLL Runtime Model

The JVM native library (`jvm.dll`) is statically linked against the MSVC runtime. The Java tools (`java.exe`, `javac.exe`, etc.) are dynamically linked. This means `vcruntime140.dll` must be present on the system or bundled with the JRE. Most JDK distributions include it in `<JRE>/bin` to avoid dependency issues.
