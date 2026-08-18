---
title: "What Java Version Are You Running?"
slug: "what-java-version-are-you-running-lets-take-a-look-under-the-hood-of-the-jdk"
date: "2022-12-15T08:59:05+00:00"
lastmod: "2022-12-15T11:06:02+00:00"
description: "Did you know there are multiple ways you check your Java version and even get much more information than you might think, very quickly?"
authors:
  - "frankdelporte"
image: "Screenshot-2022-12-01-at-12.23.11.png"
categories:
  - "Java Core"
  - "Tutorials"
tags:
related_posts:
  - "best-practice-comparative-evaluation-of-jdk-setups-azul-zulu-prime-vs-openjdk"
  - "jdb"
  - "virtual-thread-pinning-field-guide"
enlighterjs: true
frozen: false
---

From time to time, you need to check which Java version is installed on your computer or server, for instance when starting on a new project or configuring an application to run on a server.

But did you know there are multiple ways you can do this and even get much more information than you might think, very quickly?

Let's find out...

## Reading the Java Version in the Terminal

Probably the easiest way to find the installed version is by using the `java -version` terminal command:

```
$ java -version
openjdk version "19" 2022-09-20
OpenJDK Runtime Environment Zulu19.28+81-CA (build 19+36)
OpenJDK 64-Bit Server VM Zulu19.28+81-CA (build 19+36, mixed mode, sharing)
```

## Checking Version Files in the Installation Directory

The above output results from info read by the `java` executable from a file inside its installation directory.

Let's explore what we can find there.

On my machine, as I use [SDKMAN](https://sdkman.io/) to switch between different Java versions, all my versions are stored here:

```
$ ls -l /Users/frankdelporte/.sdkman/candidates/java/
total 0
drwxr-xr-x  15 frankdelporte  staff  480 Apr 17  2022 11.0.15-zulu
drwxr-xr-x  16 frankdelporte  staff  512 Apr 17  2022 17.0.3.fx-zulu
drwxr-xr-x  15 frankdelporte  staff  480 Mar 29  2022 18.0.1-zulu
drwxr-xr-x  15 frankdelporte  staff  480 Sep  7 18:36 19-zulu
drwxr-xr-x  18 frankdelporte  staff  576 Apr 18  2022 8.0.332-zulu
lrwxr-xr-x   1 frankdelporte  staff    7 Nov 21 21:09 current -> 19-zulu
```

And in each of these directories a release file can be found which also shows us the version information, including some extra information.

```
$ cat /Users/frankdelporte/.sdkman/candidates/java/19-zulu/release
IMPLEMENTOR="Azul Systems, Inc."
IMPLEMENTOR_VERSION="Zulu19.28+81-CA"
JAVA_VERSION="19"
JAVA_VERSION_DATE="2022-09-20"
LIBC="default"
MODULES="java.base java.compiler ... jdk.unsupported jdk.unsupported.desktop jdk.xml.dom"
OS_ARCH="aarch64"
OS_NAME="Darwin"
SOURCE=".:git:3d665268e905"
 
$ cat /Users/frankdelporte/.sdkman/candidates/java/8.0.332-zulu//release
JAVA_VERSION="1.8.0_332"
OS_NAME="Darwin"
OS_VERSION="11.2"
OS_ARCH="aarch64"
SOURCE="git:f4b2b4c5882e"
```

## Getting More Information With showSettings

In 2010, an experimental flag (indicated with the `X`) was added to OpenJDK to provide more configuration information: `-XshowSettings`.
![](Screenshot-2022-12-01-at-12.23.11.png) Twitter screenshot of a message by OpenJDK about adding the -XshowSettings flag

This flag can be called with different arguments, each producing an other information output.

The cleanest way to call this flag, is by adding `-version`, otherwise you will get the long Java manual output as no application code was found to be executed.

### Reading the System Properties

By using the `-XshowSettings:properties` flag, a long list of various properties is shown.

```
$ java -XshowSettings:properties -version
Property settings:
    file.encoding = UTF-8
    file.separator = /
    ftp.nonProxyHosts = local|*.local|169.254/16|*.169.254/16
    http.nonProxyHosts = local|*.local|169.254/16|*.169.254/16
    java.class.path =
    java.class.version = 63.0
    java.home = /Users/frankdelporte/.sdkman/candidates/java/19-zulu/zulu-19.jdk/Contents/Home
    java.io.tmpdir = /var/folders/np/6j1kls013kn2gpg_k6tz2lkr0000gn/T/
    java.library.path = /Users/frankdelporte/Library/Java/Extensions
        /Library/Java/Extensions
        /Network/Library/Java/Extensions
        /System/Library/Java/Extensions
        /usr/lib/java
        .
    java.runtime.name = OpenJDK Runtime Environment
    java.runtime.version = 19+36
    java.specification.name = Java Platform API Specification
    java.specification.vendor = Oracle Corporation
    java.specification.version = 19
    java.vendor = Azul Systems, Inc.
    java.vendor.url = http://www.azul.com/
    java.vendor.url.bug = http://www.azul.com/support/
    java.vendor.version = Zulu19.28+81-CA
    java.version = 19
    java.version.date = 2022-09-20
    java.vm.compressedOopsMode = Zero based
    java.vm.info = mixed mode, sharing
    java.vm.name = OpenJDK 64-Bit Server VM
    java.vm.specification.name = Java Virtual Machine Specification
    java.vm.specification.vendor = Oracle Corporation
    java.vm.specification.version = 19
    java.vm.vendor = Azul Systems, Inc.
    java.vm.version = 19+36
    jdk.debug = release
    line.separator = \n
    native.encoding = UTF-8
    os.arch = aarch64
    os.name = Mac OS X
    os.version = 13.0.1
    path.separator = :
    socksNonProxyHosts = local|*.local|169.254/16|*.169.254/16
    stderr.encoding = UTF-8
    stdout.encoding = UTF-8
    sun.arch.data.model = 64
    sun.boot.library.path = /Users/frankdelporte/.sdkman/candidates/java/19-zulu/zulu-19.jdk/Contents/Home/lib
    sun.cpu.endian = little
    sun.io.unicode.encoding = UnicodeBig
    sun.java.launcher = SUN_STANDARD
    sun.jnu.encoding = UTF-8
    sun.management.compiler = HotSpot 64-Bit Tiered Compilers
    user.country = BE
    user.dir = /Users/frankdelporte
    user.home = /Users/frankdelporte
    user.language = en
    user.name = frankdelporte
 
openjdk version "19" 2022-09-20
OpenJDK Runtime Environment Zulu19.28+81-CA (build 19+36)
OpenJDK 64-Bit Server VM Zulu19.28+81-CA (build 19+36, mixed mode, sharing)
```

If you ever faced the problem of an unsupported Java version 59 (are similar), you'll now also understand where this value is defined, it's right here in this list as `java.class.version`.

It's an internal number used by Java to define the version.

|-------------------|----|----|----|----|----|----|----|----|----|----|----|----|
| **Java release**  | 8  | 9  | 10 | 11 | 12 | 13 | 14 | 15 | 16 | 17 | 18 | 19 |
| **Class version** | 52 | 53 | 54 | 55 | 56 | 57 | 58 | 59 | 60 | 61 | 62 | 63 |

### Reading the Locale Information

In case you didn't know yet, I live in Belgium and use English as my computer language, as you can see when using the `-XshowSettings:locale` flag:

```
$ java -XshowSettings:locale -version
Locale settings:
    default locale = English (Belgium)
    default display locale = English (Belgium)
    default format locale = English (Belgium)
    available locales = , af, af_NA, af_ZA, af_ZA_#Latn, agq, agq_CM, agq_CM_#Latn,
        ak, ak_GH, ak_GH_#Latn, am, am_ET, am_ET_#Ethi, ar, ar_001,
        ar_AE, ar_BH, ar_DJ, ar_DZ, ar_EG, ar_EG_#Arab, ar_EH, ar_ER,
        ...
        zh_MO_#Hant, zh_SG, zh_SG_#Hans, zh_TW, zh_TW_#Hant, zh__#Hans, zh__#Hant, zu,
        zu_ZA, zu_ZA_#Latn
 
openjdk version "19" 2022-09-20
OpenJDK Runtime Environment Zulu19.28+81-CA (build 19+36)
OpenJDK 64-Bit Server VM Zulu19.28+81-CA (build 19+36, mixed mode, sharing)
```

### Reading the VM Settings

With the `-XshowSettings:vm` flag, some info is shown about the Java Virtual Machine.

As you can see in the second example, the amount of maximum heap memory size can be defined with the `-Xmx` flag.

```
$ java -XshowSettings:vm -version
VM settings:
    Max. Heap Size (Estimated): 8.00G
    Using VM: OpenJDK 64-Bit Server VM
 
openjdk version "19" 2022-09-20
OpenJDK Runtime Environment Zulu19.28+81-CA (build 19+36)
OpenJDK 64-Bit Server VM Zulu19.28+81-CA (build 19+36, mixed mode, sharing)
 
$ java -XshowSettings:vm -Xmx512M -version
VM settings:
    Max. Heap Size: 512.00M
    Using VM: OpenJDK 64-Bit Server VM
 
openjdk version "19" 2022-09-20
OpenJDK Runtime Environment Zulu19.28+81-CA (build 19+36)
OpenJDK 64-Bit Server VM Zulu19.28+81-CA (build 19+36, mixed mode, sharing)
```

### Reading all at Once

If you want all of the information above with one call, use the `-XshowSettings:all` flag.

## Conclusion

Next to `java -version`, we can also use `java -XshowSettings:all -version` to get more info about our Java environment.
