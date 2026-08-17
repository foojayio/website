---
title: "Troubleshooting Java Processes Running on Your Machine"
slug: "jps"
date: "2021-07-02T09:27:12+00:00"
lastmod: "2021-07-06T22:23:59+00:00"
description: "When your Java application has problems, check the running processes on the machine. For Java processes, the JDK provides the jps tool."
canonical: "https://jfeatures.com/blog/jps"
authors:
  - "vipin-sharma"
image: "ebook_upd.png"
categories:
  - "Tools"
tags:
related_posts:
  - "jdkmon-your-friendly-jdk-distribution-updater"
  - "jdb"
  - "introducing-the-boxlang-ide-plugin-for-intellij"
  - "indexing-all-of-wikipedia-on-a-laptop"
enlighterjs: true
frozen: false
---

When your application has some problem, the first thing to check is running processes on the machine.

For Linux OS we generally use `ps -ef`. ***ps*** is one of the most used Linux troubleshooting commands. The JDK provides similar functionality for Java processes through `jps`. The ***jps*** command-line utility provides a list of all running Java processes on a machine for which the user has access rights. The access rights are determined by access-control mechanisms specific to the operating system.

The `jps` utility can also provide information on arguments passed to the main method, arguments passed to JVM, etc. In this post, we will see the functionalities provided by `jps`.

### Troubleshooting a Java Application with jps {#h3-0-troubleshooting-a-java-application-with-jps}

In this section, we will see how to use `jps` with a running Java process.

#### jps command

```
jps [options] pid

options:    This represents the jps command-line options.
pid:        The process ID for which the information specified by the options is to be printed.
```


#### Java program we will be debugging in this post

Following is the sample class we are going to debug and try to understand the different features available.

```java
public class Test
{
   public static void main(String[] args)
   {
      while(true)
      {
      }
   }
}
```


For all our examples we will be using Java 17, as of writing this post it is built using [JDK master branch](https://github.com/openjdk/jdk/). [This post](https://jfeatures.com/blog/OpenJDK_Contribution) explains how to build JDK from the source.

```
java -version
openjdk version "17-internal" 2021-09-14
OpenJDK Runtime Environment (build 17-internal+0-adhoc.vipin.jdk)
OpenJDK 64-Bit Server VM (build 17-internal+0-adhoc.vipin.jdk, mixed mode)
```


We are running the Java process using the following command. For the rest of this article we will use jps on this process.

```
java -XX:ConcGCThreads=6 -Xmx256m -Xms8m -Xss256k Test argument1 argument2
```


#### Print Java process ids

Following command shows process ids.

```
jps -q
```


Output:

```
2468
10660
7067
7470
10366
```


#### Print process id along with the class name

This is the command to list Java processes with main class names, it is same as command `jps -V`.

```
jps
```


Output:

```
2468
10694 Jps
7067 Main
7470 Launcher
10366 Test
```


#### Print Java process ids along with full package name

`jps -l` displays the full package name for the application's main class or the full pathname to the application's JAR file.

command:

```
jps -l
```


Output:

```
2468
10554 jdk.jcmd/sun.tools.jps.Jps
7067 com.intellij.idea.Main
7470 org.jetbrains.jps.cmdline.Launcher
10366 Test
```


#### Print process ids along with class name and arguments passed to the main method.

In the below, running with command `java Test argument1 argument2`, `jps -m` displays the arguments passed to the main method:

```
jps -m
```


Output:

```
2468
10726 Jps -m
7067 Main
7470 Launcher /home/vipin/.local/share/JetBrains/Toolbox/apps/IDEA-C/ch-0/203.7148.57/lib/commons-lang3-3.10.jar:/home/vipin/.local/share/JetBrains/Toolbox/apps/IDEA-C/ch-0/203.7148.57/lib/httpclient-4.5.12.jar:/home/vipin/.local/share/JetBrains/Toolbox/apps/IDEA-C/ch-0/203.7148.57/lib/annotations.jar:/home/vipin/.local/share/JetBrains/Toolbox/apps/IDEA-C/ch-0/203.7148.57/lib/netty-buffer-4.1.52.Final.jar:/home/vipin/.local/share/JetBrains/Toolbox/apps/IDEA-C/ch-0/203.7148.57/plugins/java/lib/jps-javac-extension-1.jar:/home/vipin/.local/share/JetBrains/Toolbox/apps/IDEA-C/ch-0/203.7148.57/lib/jdom.jar:/home/vipin/.local/share/JetBrains/Toolbox/apps/IDEA-C/ch-0/203.7148.57/lib/netty-resolver-4.1.52.Final.jar:/home/vipin/.local/share/JetBrains/Toolbox/apps/IDEA-C/ch-0/203.7148.57/lib/maven-resolver-api-1.3.3.jar:/home/vipin/.local/share/JetBrains/Toolbox/apps/IDEA-C/ch-0/203.7148.57/plugins/java/lib/maven-resolver-connector-basic-1.3.3.jar:/home/vipin/.local/share/JetBrains/Toolbox/apps/IDE
10366 Test argument1 argument2
```


#### Print JVM arguments passed to Java process

In the below, running with command `java -XX:ConcGCThreads=6 -Xmx256m -Xms8m -Xss256k Test`, `jps -v` displays the arguments passed to the JVM.

```
jps -v
```


Output:

```
2468 -Djava.library.path=/tmp/.mount_jetbraH5N0hQ -Xmx256m -Xms8m -Xss256k -XX:+UseStringDeduplication -XX:+UseCompressedOops -XX:+UseSerialGC -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -Djdk.lang.processReaperUseDefaultStackSize=true vfprintf exit abort -DTOOLBOX_VERSION=1.20.7940
10501 Jps -Dapplication.home=/home/vipin/githubprojects/jdk/build/linux-x86_64-server-release/jdk -Xms8m -Djdk.module.main=jdk.jcmd
7067 Main -Xms128m -Xmx2048m -XX:ReservedCodeCacheSize=512m -XX:+UseConcMarkSweepGC -XX:SoftRefLRUPolicyMSPerMB=50 -XX:CICompilerCount=2 -XX:+HeapDumpOnOutOfMemoryError -XX:-OmitStackTraceInFastThrow -ea -Dsun.io.useCanonCaches=false -Djdk.http.auth.tunneling.disabledSchemes="" -Djdk.attach.allowAttachSelf=true -Djdk.module.illegalAccess.silent=true -Dkotlinx.coroutines.debug=off -Dsun.tools.attach.tmp.only=true -Dide.no.platform.update=true -XX:ErrorFile=/home/vipin/java_error_in_idea_%p.log -XX:HeapDumpPath=/home/vipin/java_error_in_idea_.hprof -Didea.vendor.name=JetBrains -Didea.paths.selector=IdeaIC2020.3 -Djb.vmOptionsFile=/home/vipin/.local/share/JetBrains/Toolbox/apps/IDEA-C/ch-0/203.7148.57.vmoptions -Didea.platform.prefix=Idea -Didea.jre.check=true
7470 Launcher -Xmx700m -Djava.awt.headless=true -Djdt.compiler.useSingleThread=true -Dpreload.project.path=/home/vipin/githubprojects/jdk -Dpreload.config.path=/home/vipin/.config/JetBrains/IdeaIC2020.3/options -Dcompile.parallel=false -Drebuild.on.dependency.change=true -Dio.netty.initialSeedUniquifier=4046065713679813272 -Dfile.encoding=UTF-8 -Duser.language=en -Duser.country=IN -Didea.paths.selector=IdeaIC2020.3 -Didea.home.path=/home/vipin/.local/share/JetBrains/Toolbox/apps/IDEA-C/ch-0/203.7148.57 -Didea.config.path=/home/vipin/.config/JetBrains/IdeaIC2020.3 -Didea.plugins.path=/home/vipin/.local/share/JetBrains/IdeaIC2020.3 -Djps.log.dir=/home/vipin/.cache/JetBrains/IdeaIC2020.3/log/build-log -Djps.fallback.jdk.home=/home/vipin/.local/share/JetBrains/Toolbox/apps/IDEA-C/ch-0/203.7148.57/jbr -Djps.fallback.jdk.version=11.0.9.1 -Dio.netty.noUnsafe=true -Djava.io.tmpdir=/home/vipin/.cache/JetBrains/IdeaIC2020.3/compile-server/jdk_5c2ba8e3/_temp_ -Djps.backward.ref.index.builder=true -Dkotlin.incremental.compilation=true
10366 Test -XX:ConcGCThreads=6 -Xmx256m -Xms8m -Xss256k
```


#### `jcmd` to list Java processes

`jcmd` or `jcmd -l` both provides similar information as `jps -l`. You can read more [about jcmd here](https://jfeatures.com/blog/JCMD).

* `jcmd` is a recommended tool to get Java process information
* `jps` is an experimental tool, it may not be supported in future JDK versions. `jcmd` is the recommended tool, you can read more about how to get all this information using jcmd on \[post\](https://jfeatures.com/blog/JCMD).

#### Conclusion

`jps` is a simple tool with few options that make it easy to master, and when in need it can be a quick and great help you wanted. Utilities like this are very useful in a situation when we need to analyze and resolve problems in production Java application quickly. `jps` is part of OpenJDK, no need to install any third party software.

If you want to get amazing Java jobs, I wrote an ebook \[5 steps to Best Java Jobs\](https://jfeatures.com/). You can download this step-by-step guide for free!  
![](https://jfeatures.com/img/ebook_upd.png)

#### Resources

[Javadoc for jps](https://docs.oracle.com/en/java/javase/16/troubleshoot/diagnostic-tools.html#GUID-FC269C18-470F-441E-9564-7EEA182F8125)
