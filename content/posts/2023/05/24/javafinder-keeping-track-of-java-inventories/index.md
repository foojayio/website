---
title: "JavaFinder: Keeping Track of Java Inventories"
slug: "javafinder-keeping-track-of-java-inventories"
date: "2023-05-24T20:12:31+00:00"
lastmod: "2023-05-26T07:52:14+00:00"
description: "Do you ever wonder how many Java distros you've installed? Not only for development but also those bundled with the applications you use?"
authors:
  - "gerrit-grunwald"
image: "javafinder.png"
categories:
  - "Release Notes"
  - "Tools"
tags:
related_posts:
  - "get-your-jdk-as-easily-as-possible"
  - "how-to-run-a-java-application-with-crac-in-a-docker-container"
  - "introducing-the-openjdk-coordinated-restore-at-checkpoint-project"
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
enlighterjs: true
frozen: false
---

Do you ever wonder how many Java distributions you have installed on your machine?

I not only mean versions that you use for development but also versions that come bundled with the applications you use.

Last weekend I decided to write a little tool that can help me to figure that out... JavaFinder.

This is a simple command line tool that will search a given path, including its subfolders for any OpenJDK distribution or GraalVM derivate.

So it's nothing really fancy but could sometimes be useful.

**Usage**   

Per default, it will print all distributions found in non-beautified JSON format in the console, when I run it on my Macbook Pro without any parameters, like this, simply `javafinder`, the output will look as follows:

```
{"search_path":"/System/Volumes/Data/Library/Java/JavaVirtualMachines/","sysinfo":{"operating_system":"Mac OS","architecture":"ARM64","bit":"64 Bit"},"distributions":[{"vendor":"Gluon","name":"Gluon GraalVM","version":"22.1.0.1","path":"/System/Volumes/Data/Library/Java/JavaVirtualMachines/gluon-graalvm/Contents/Home/","build_scope":"GraalVM"},{"vendor":"Azul","name":"Zulu","version":"8.0.372+7","path":"/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-8.jdk/zulu-8.jdk/Contents/Home/jre/","build_scope":"OpenJDK"},{"vendor":"Azul","name":"Zulu","version":"8.0.372+7","path":"/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-8.jdk/zulu-8.jdk/Contents/Home/","build_scope":"OpenJDK"},{"vendor":"Azul","name":"Zulu","version":"11.0.19","path":"/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-11.jdk/zulu-11.jdk/Contents/Home/","build_scope":"OpenJDK"},{"vendor":"Oracle","name":"Graal VM CE","version":"22.3.1","path":"/System/Volumes/Data/Library/Java/JavaVirtualMachines/graalvm-ce-java17-22.3.1/Contents/Home/","build_scope":"GraalVM"},{"vendor":"Azul","name":"Zulu","version":"17.0.7","path":"/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-17.jdk/zulu-17.jdk/Contents/Home/","build_scope":"OpenJDK"},{"vendor":"Azul","name":"Zulu","version":"20.0.1","path":"/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-20.jdk/zulu-20.jdk/Contents/Home/","build_scope":"OpenJDK"},{"vendor":"Azul","name":"Zulu","version":"21-ea+22","path":"/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-21.jdk/zulu-21.jdk/Contents/Home/","build_scope":"OpenJDK"}]}
```

When called with the json parameter, `javafinder json`, the output will be in beautfied JSON, as follows:

```
{
   "search_path":"/System/Volumes/Data/Library/Java/JavaVirtualMachines",
   "sysinfo":{
      "operating_system":"macos",
      "architecture":"arm64",
      "bit":"64"
   },
   "distributions":[
      {
         "vendor":"Azul",
         "name":"Zulu",
         "version":"17.0.7",
         "path":"/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-17.jdk/zulu-17.jdk/Contents/Home/",
         "build_scope":"OpenJDK"
      },
      {
         "vendor":"Azul",
         "name":"Zulu",
         "version":"8.0.372+7",
         "path":"/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-8.jdk/zulu-8.jdk/Contents/Home/jre/",
         "build_scope":"OpenJDK"
      },
      {
         "vendor":"Azul",
         "name":"Zulu",
         "version":"8.0.372+7",
         "path":"/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-8.jdk/zulu-8.jdk/Contents/Home/",
         "build_scope":"OpenJDK"
      },
      {
         "vendor":"Azul",
         "name":"Zulu",
         "version":"11.0.19",
         "path":"/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-11.jdk/zulu-11.jdk/Contents/Home/",
         "build_scope":"OpenJDK"
      },
      {
         "vendor":"Azul",
         "name":"Zulu",
         "version":"20.0.1",
         "path":"/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-20.jdk/zulu-20.jdk/Contents/Home/",
         "build_scope":"OpenJDK"
      },
      {
         "vendor":"Oracle",
         "name":"Graal VM CE",
         "version":"22.3.1",
         "path":"/System/Volumes/Data/Library/Java/JavaVirtualMachines/graalvm-ce-java17-22.3.1/Contents/Home/",
         "build_scope":"GraalVM"
      },
      {
         "vendor":"Azul",
         "name":"Zulu",
         "version":"21-ea+21",
         "path":"/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-21.jdk/zulu-21.jdk/Contents/Home/",
         "build_scope":"OpenJDK"
      },
      {
         "vendor":"Gluon",
         "name":"Gluon GraalVM",
         "version":"22.1.0.1",
         "path":"/System/Volumes/Data/Library/Java/JavaVirtualMachines/gluon-graalvm/Contents/Home/",
         "build_scope":"GraalVM"
      }
   ]
}
```

If you prefer something simpler, there's also the possibility to output the results in CSV format.

In this case, you need to run the program as follows `javafinder csv`and the output will look like follows:

```
Vendor,Distribution,Version,Path,Type Azul,Zulu,21-ea+21,/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-21.jdk/zulu-21.jdk/Contents/Home/,OpenJDK Oracle,Graal VM CE,22.3.1,/System/Volumes/Data/Library/Java/JavaVirtualMachines/graalvm-ce-java17-22.3.1/Contents/Home/,GraalVM Azul,Zulu,20.0.1,/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-20.jdk/zulu-20.jdk/Contents/Home/,OpenJDK Azul,Zulu,11.0.19,/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-11.jdk/zulu-11.jdk/Contents/Home/,OpenJDK Azul,Zulu,8.0.372+7,/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-8.jdk/jre/,OpenJDK Azul,Zulu,8.0.372+7,/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-8.jdk/,OpenJDK Azul,Zulu,20.0.1,/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-20.jdk/,OpenJDK Azul,Zulu,21-ea+21,/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-21.jdk/,OpenJDK Gluon,Gluon GraalVM,22.1.0.1,/System/Volumes/Data/Library/Java/JavaVirtualMachines/gluon-graalvm/Contents/Home/,GraalVM Azul,Zulu,8.0.372+7,/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-8.jdk/zulu-8.jdk/Contents/Home/,OpenJDK Azul,Zulu,17.0.7,/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-17.jdk/,OpenJDK Azul,Zulu,11.0.19,/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-11.jdk/,OpenJDK Azul,Zulu,17.0.7,/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-17.jdk/zulu-17.jdk/Contents/Home/,OpenJDK Azul,Zulu,8.0.372+7,/System/Volumes/Data/Library/Java/JavaVirtualMachines/zulu-8.jdk/zulu-8.jdk/Contents/Home/jre/,OpenJDK
```

On Linux and Mac you simply can save that into a file using `javafinder csv > jdks.csv`

In the examples above I did not specify any paths to search for distributions which will make JavaFinder search in  

pre-defined folders for each operating system

* Windows: `C:\Program Files\Java\`
* Linux: `/usr/lib/jvm`
* MacOS: `/System/Volumes/Data/Library/Java/JavaVirtualMachines/`

In case you would like to search in a specific path you can simply add it to the command as follows:

* MACOS  
  Search all paths in `/Users/hansolo` by executing  
  `>: javafinder /Users/YOUR_USER_NAME` -\> output will be in non beautified json format  
  `>: javafinder json /Users/YOUR_USER_NAME` -\> output will be in beautified json format  
  `>: javafinder csv /Users/YOUR_USER_NAME` -\> output will be in csv format
* LINUX  
  Search all paths in your home folder  
  `>: javafinder /home/YOUR_USER_NAME` -\> output will be in non beautified json format  
  `>: javafinder json /home/YOUR_USER_NAME` -\> output will be in beautified json format  
  `>: javafinder csv /home/YOUR_USER_NAME` -\> output will be in csv format
* WINDOWS  
  Search all paths in your home folder  
  `>: javafinder.exe c:\Users\YOUR_USER_NAME` -\> output will be in non beautified json format  
  `>: javafinder.exe json c:\Users\YOUR_USER_NAME` -\> output will be in beautified json format  
  `>: javafinder.exe csv c:\Users\YOUR_USER_NAME` -\> output will be in csv format

**Note:**Be aware that scanning for distributions can take some time, especially when you point to a path like your user home folder, etc. The less subfolders are in the given path, the faster the scanning will be.

**Download**   

JavaFinder is available for Windows (x64), Linux (x64 and aarch64) and MacOS (x64 and aarch64) and you can find it over at [GitHub](https://github.com/HanSolo/javafinder/releases "github").
