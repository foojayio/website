---
title: "Getting Started with Java - Install Java on Linux"
description: "foojay is the place for all OpenJDK Update Release Information. Learn More."
canonical: "https://foojay.io/java-quick-start/install-java/install-java-on-linux/"
url: "/java-quick-start/install-java/install-java-on-linux/"
enlighterjs: true
aliases:
  - "/java-quick-start/install-java/install-java-on-linux/"
frozen: false
---

Is Java not available on your Linux computer yet?

No problem, let's use an installer to make the process really easy.

## Download and Install Java on Linux

This is a direct link to get the Azul Zulu distribution, version 25, as an installer for a Debian-based system (Ubuntu, PopOS, Linux Mint, and more):  
<https://cdn.azul.com/zulu/bin/zulu25.34.17-ca-jdk25.0.3-linux_amd64.deb>

Many more distributions and versions of Java exist. You can [read more info here](https://foojay.io/java-quick-start/install-java/find-another-java-version/) on how to find another distribution or version.

Download the `.deb` file, open a Terminal, and run the following commands.

```bash
$ cd ~/Downloads
$ sudo apt install ./DOWNLOADED_VERSION.deb
// For instance:
$ sudo apt install ./zulu25.34.17-ca-jdk25.0.3-linux_amd64.deb
```

## Validate the Java Installation

Close the Terminal and open a new one.

Now, you can check the Java installation as follows. The version info depends on the one you installed:

```bash
$ java -version
openjdk version "21.0.4" 2024-07-16 LTS
OpenJDK Runtime Environment Zulu21.36+17-CA (build 21.0.4+7-LTS)
OpenJDK 64-Bit Server VM Zulu21.36+17-CA (build 21.0.4+7-LTS, mixed mode, sharing)
```

For another Linux-based system, you may need to select a `.rpm` download and install it as follows.

```bash
sudo yum install <package>.rpm
```


That's it. You're now ready to run Java programs and, even better... create your own!
