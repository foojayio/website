---
title: "Getting Started with Java - Install Java on MacOS"
description: "foojay is the place for all OpenJDK Update Release Information. Learn More."
canonical: "https://foojay.io/java-quick-start/install-java/install-java-on-macos/"
url: "/java-quick-start/install-java/install-java-on-macos/"
aliases:
  - "/java-quick-start/install-java/install-java-on-macos/"
frozen: false
---

Is Java not available on your Mac OS X computer yet?

No problem, let's use an installer to make the process really easy.

## Download and Install Java on macOS

This is a direct link to the Azul Zulu build of OpenJDK distribution, version 25, as an installer:

* For an Apple computer with an Intel chip:  
  <https://cdn.azul.com/zulu/bin/zulu25.28.85-ca-fx-jdk25.0.0-macosx_x64.dmg>  
* For an Apple computer with an M-chip:  
  <https://cdn.azul.com/zulu/bin/zulu25.28.85-ca-fx-jdk25.0.0-macosx_aarch64.dmg>

Many more distributions and versions of Java exist. You can [read more info here](https://foojay.io/java-quick-start/install-java/find-another-java-version/) on how to find another distribution or version.

Once the `.dmg` file completed downloading, double-click it, and follow the instructions.

## Validate the Java Installation

After the installation has completed, you can check the installed version by opening a Terminal and running the `java -version` command.

You should get the following result:

```bash
% java -version

openjdk version "25" 2025-09-16 LTS
OpenJDK Runtime Environment Zulu25.28+85-CA (build 25+36-LTS)
OpenJDK 64-Bit Server VM Zulu25.28+85-CA (build 25+36-LTS, mixed mode, sharing)
```

That's it. You're now ready to run Java programs and, even better... create your own!
