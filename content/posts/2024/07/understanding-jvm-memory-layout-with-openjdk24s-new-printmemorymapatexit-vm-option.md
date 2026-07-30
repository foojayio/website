---
title: "Understanding JVM Memory Layout with OpenJDK24's New PrintMemoryMapAtExit VM Option"
slug: "understanding-jvm-memory-layout-with-openjdk24s-new-printmemorymapatexit-vm-option"
date: "2024-07-27T10:38:31+00:00"
lastmod: "2024-07-27T10:47:25+00:00"
description: "OpenJDK24 recently added a new HotSpot JVM option called PrintMemoryMapAtExit."
authors:
  - "chriswhocodes"
image: "/images/posts/2024/07/understanding-jvm-memory-layout-with-openjdk24s-new-printmemorymapatexit-vm-option/PrintMemoryMapAtExit.png"
categories:
  - "Java Core"
tags:
related_posts:
  - "foojay-podcast-23"
  - "breaking-the-code-how-chris-newland-is-changing-the-game-in-jvm-performance"
  - "java-profiling-overview"
frozen: false
---

**OpenJDK24 recently added a new HotSpot JVM option called `PrintMemoryMapAtExit`.**

I know this because I'm the maintainer of a project called VMOptionsExplorer, whose job is to track changes to the more than 2000 command line options you can use to control the Java Virtual Machine (JVM).

VMOptionsExplorer presents this information as a searchable online dictionary of VM options available at <https://chriswhocodes.com>

![A screenshot of the VMOptionsExplorer website](/images/posts/2024/07/understanding-jvm-memory-layout-with-openjdk24s-new-printmemorymapatexit-vm-option/vmoe-700x329.png)

and also on Foojay at <https://foojay.io/command-line-arguments/> (although the Foojay copy doesn't always cover the latest JDKs).

When a new VM option becomes available it is often added to the latest development JDK (in this case JDK 24) and documentation for it is sometimes sparse.

The new `PrintMemoryMapAtExit` option caught my eye because my day job is related to Java performance and a deep understanding of the JVM.

A quick internet search turned up the JDK bug system ID for the development of this new option: <https://bugs.openjdk.org/browse/JDK-8334026?page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel>

Here we can see the rationale for adding the new option:

***To investigate footprint problems of processes with a short lifespan (e.g. startup footprint problems), we found it very useful to be able to get an annotated memory map at exit - the same map printed by jcmd System.map.***

So the purpose of `PrintMemoryMapAtExit` is to provide information about the memory layout of a JVM process that might exit before you get a chance to use other tools like   
`jcmd System.map` or OS tools for reading memory layout details.

What's in the JVM's memory map? {#h2-0-what-s-in-the-jvm-s-memory-map}
----------------------------------------------------------------------

I'll let the new option explain what information it can display.

`PrintMemoryMapAtExit` is a diagnostic option so we first have to tell the JVM to "unlock" the diagnostics options on the command line using the `-XX:+UnlockDiagnosticVMOptions` option.

`-XX:` options are known as non-standard JVM options (they are not part of the JVM specification). In the case of binary options they are followed by a `+` to enable the option or `-` to disable the option so to enable `PrintMemoryMapAtExit` you would type  
`java -XX:+UnlockDiagnosticVMOptions -XX:NativeMemoryTracking=summary -XX:+PrintMemoryMapAtExit -version`

You'll see I added another diagnostic option `-XX:NativeMemoryTracking=summary` which instructs the JVM to collect statistics about it's own internal memory allocations (for a small performance overhead). This Native Memory Tracking (NMT) information enriches the output of `PrintMemoryMapAtExit`.

Here is the output from the new option:

```
Memory mappings:
from, to, vsize: address range and size
prot:            protection
rss:             resident set size
hugetlb:         size of private hugetlb pages
pgsz:            page size
notes:           mapping information  (detail mode only)
                      shrd: mapping is shared
                       com: mapping committed (swap space reserved)
                      swap: mapping partly or completely swapped out
                       thp: mapping uses THP
                     thpad: mapping is THP-madvised
                     nothp: mapping is forbidden to use THP
                      huge: mapping uses hugetlb pages
vm info:         VM information (requires NMT)
                   CARDTBL: GC Card table
                       CDS: CDS archives
                     CLASS: Class Space
                      CODE: Code Heap
                        GC: GC support data (e.g. bitmaps)
                    INTERN: Internal
                  JAVAHEAP: Java Heap
                       JDK: allocated by JDK libraries other than VM
                      META: Metaspace nodes (non-class)
                      POLL: Polling pages
                     STACK: (known) Thread Stack
                      TEST: JVM internal test mappings
file:            file mapped, if mapping is not anonymous
```

So as you might have expected, this new option will tell you where in memory the JVM kept the Java heap, thread stacks, JIT-compiled code, GC metadata, etc.

As a Java developer these JVM memory regions aren't something you usually need to be concerned about (apart from sometimes overriding the default sizes of certain regions like the heap).

Where this new JVM option might be interesting to you as a developer is if you are using memory mapped files in your application code.

Memory mapping in Java {#h2-1-memory-mapping-in-java}
-----------------------------------------------------

Memory mapping is a technique for treating addressable regions that are outside the bounds of your program as being addressable from within your program.

These regions might be part of a file on disk, or they could be part of the memory space of another physical device within your system such as the frame buffer of a graphics card.

The operating system is able to manage access between your process and these external address regions. The Java core libraries provide an API for conveniently reading and writing mapped memory.

A common use of memory mapping in Java is to map a smaller region of a very large file so that you can operate on the data you want without having to read the entire file into your program's memory and write the entire file to save your changes. You can think of it as opening a small window through which you can read or write the data.

Let's assume we've created a 1GB file of random data using  
`dd if=/dev/random of=/tmp/bigfile bs=1G count=1`

Here is a simple Java program that demonstrates reading from a large file using `java.nio.channels.FileChannel` and `java.nio.MappedByteBuffer`.

```
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

public class ReadMappedFile
{
    public static void main(String[] args) throws Exception
    {
        Path bigFilePath = Paths.get("/tmp/bigfile");

        try (FileChannel fileChannel = (FileChannel) Files.newByteChannel(
                bigFilePath, EnumSet.of(StandardOpenOption.READ)))
        {
            // Memory map a 64KB window in the middle of the large file for reading
            byte[] window = new byte[65536];

            MappedByteBuffer mappedBuffer = fileChannel
                    .map(FileChannel.MapMode.READ_ONLY, fileChannel.size() / 2, window.length);

            mappedBuffer.get(window);

            // Print the data to the console
            int count = 0;
            for (byte b : window)
            {
                System.out.print(String.format("%02x", b) + " ");

                if (++count % 32 == 0)
                {
                    System.out.println();
                }
            }
        }
    }
}
```

Output of the program (truncated for brevity)

```
a3 e7 e0 ef 03 b2 92 a5 18 c9 14 d4 f9 19 5e ad bf 83 84 9b ed 18 1c 02 48 fd c2 fc 30 8d 6b 7c 
ce b6 5f 30 f8 73 4b 7c 80 d2 b0 51 ce 36 a9 07 85 0e 47 ef 5c 8a 2b 1a cc 2a 69 5e 25 ef 9e 9d 
43 3a df 64 94 ab c7 f7 e2 46 96 51 cb da ce a9 3d 9c a5 00 30 37 b7 37 a4 24 6e 0c aa fc 2b dd 
af 2f 70 aa 1e 02 98 e8 cf e5 30 d1 de 35 10 97 08 19 1f bd c4 c1 7a 59 0e 1e 96 83 18 be df 8e 
61 b4 0d 17 a3 75 02 13 ca 64 52 bf 59 7d 56 5d 56 09 d9 d2 1c 8b 63 43 a5 79 79 8d cd c8 80 e5 
48 86 1c 6f dd eb e0 65 85 c9 92 ac 44 fd 5a 43 ad 0d b4 6d 6e 5b c7 30 e2 ad 34 0d 07 59 36 a2 
b7 d3 e3 92 ae ce 6b a6 38 e5 b4 85 01 06 86 98 5b d0 a0 29 78 ff e8 ae 8e 4f e3 ec 25 12 c1 d6 
73 cd ad c1 cf 17 9a 76 32 68 c8 a1 d6 2a e4 a0 b3 82 df ed df 36 6c 25 d2 78 fa 46 1e 72 b3 66
```

Now we know how to memory map a file in Java let's get back to the `PrintMemoryMapAtExit` option and see how it details our application's mapped files.

`java -XX:+UnlockDiagnosticVMOptions -XX:+PrintMemoryMapAtExit -XX:NativeMemoryTracking=summary ReadMappedFile`

```
from               to                        vsize prot          rss      hugetlb pgsz notes            info                                  file
========================================================================================================================================================================
0x0000000689400000-0x00000006a0c00000    394264576 rw-p     17076224            0 4K   com              JAVAHEAP                              -
0x00000006a0c00000-0x00000007ffc00000   5888802816 ---p            0            0 4K   -                JAVAHEAP                              -
0x00000007ffc00000-0x00000007ffd21000      1183744 rw-p      1183744            0 4K   com              JAVAHEAP                              /big/jdks/jdk24/lib/server/classes.jsa
0x00000007ffd21000-0x0000000800000000      3010560 rw-p            0            0 4K   com              JAVAHEAP                              -
0x00007f0000000000-0x00007f0000d90000     14221312 rw-p     14098432            0 4K   com              CDS                                   /big/jdks/jdk24/lib/server/classes.jsa
0x00007f0000d90000-0x00007f0001000000      2555904 ---p            0            0 4K   -                CDS                                   -
0x00007f0001000000-0x00007f0001010000        65536 rw-p        28672            0 4K   com              CLASS                                 -
0x00007f0001010000-0x00007f0001040000       196608 ---p            0            0 4K   -                CLASS                                 -
0x00007f0001040000-0x00007f0001050000        65536 rw-p        36864            0 4K   com              CLASS                                 -
0x00007f0001050000-0x00007f0041000000   1073414144 ---p            0            0 4K   -                CLASS                                 -
0x0000b49b739f0000-0x0000b49b739f1000         4096 r-xp         4096            0 4K   com              -                                     /big/jdks/jdk24/bin/java
0x0000b49b73a01000-0x0000b49b73a02000         4096 r--p         4096            0 4K   com              -                                     /big/jdks/jdk24/bin/java
0x0000b49b73a02000-0x0000b49b73a03000         4096 rw-p         4096            0 4K   com              -                                     /big/jdks/jdk24/bin/java
0x0000b49ba384e000-0x0000b49ba3881000       208896 rw-p       159744            0 4K   com              -                                     [heap]
0x0000ec2538000000-0x0000ec2538021000       135168 rw-p        16384            0 4K   -                -                                     -
0x0000ec2538021000-0x0000ec253c000000     66973696 ---p            0            0 4K   -                -                                     -
0x0000ec253c000000-0x0000ec253c021000       135168 rw-p        16384            0 4K   -                -                                     -
0x0000ec253c021000-0x0000ec2540000000     66973696 ---p            0            0 4K   -                -                                     -
0x0000ec2540000000-0x0000ec254029d000      2740224 rw-p      2740224            0 4K   -                -                                     -
0x0000ec254029d000-0x0000ec2544000000     64368640 ---p            0            0 4K   -                -                                     -
0x0000ec2544000000-0x0000ec2544021000       135168 rw-p        16384            0 4K   -                -                                     -
0x0000ec2544021000-0x0000ec2548000000     66973696 ---p            0            0 4K   -                -                                     -
0x0000ec2548000000-0x0000ec2548021000       135168 rw-p         4096            0 4K   -                -                                     -
0x0000ec2548021000-0x0000ec254c000000     66973696 ---p            0            0 4K   -                -                                     -
0x0000ec254c000000-0x0000ec254c021000       135168 rw-p         8192            0 4K   -                -                                     -
0x0000ec254c021000-0x0000ec2550000000     66973696 ---p            0            0 4K   -                -                                     -
0x0000ec2550000000-0x0000ec25503b1000      3870720 rw-p      3702784            0 4K   -                -                                     -
0x0000ec25503b1000-0x0000ec2554000000     63238144 ---p            0            0 4K   -                -                                     -
0x0000ec2554000000-0x0000ec25545e3000      6172672 rw-p      6160384            0 4K   -                -                                     -
0x0000ec25545e3000-0x0000ec2558000000     60936192 ---p            0            0 4K   -                -                                     -
0x0000ec2558000000-0x0000ec2558021000       135168 rw-p         4096            0 4K   -                -                                     -
0x0000ec2558021000-0x0000ec255c000000     66973696 ---p            0            0 4K   -                -                                     -
0x0000ec255c000000-0x0000ec255c021000       135168 rw-p         4096            0 4K   -                -                                     -
0x0000ec255c021000-0x0000ec2560000000     66973696 ---p            0            0 4K   -                -                                     -
0x0000ec2560000000-0x0000ec2560021000       135168 rw-p         8192            0 4K   -                -                                     -
0x0000ec2560021000-0x0000ec2564000000     66973696 ---p            0            0 4K   -                -                                     -
0x0000ec2564000000-0x0000ec2564021000       135168 rw-p         4096            0 4K   -                -                                     -
0x0000ec2564021000-0x0000ec2568000000     66973696 ---p            0            0 4K   -                -                                     -
0x0000ec2568000000-0x0000ec2568021000       135168 rw-p        73728            0 4K   -                -                                     -
0x0000ec2568021000-0x0000ec256c000000     66973696 ---p            0            0 4K   -                -                                     -
0x0000ec256c000000-0x0000ec256c021000       135168 rw-p        12288            0 4K   -                -                                     -
0x0000ec256c021000-0x0000ec2570000000     66973696 ---p            0            0 4K   -                -                                     -
0x0000ec2570000000-0x0000ec25700b0000       720896 rw-p       712704            0 4K   com              META                                  -
0x0000ec25700b0000-0x0000ec2570400000      3473408 ---p            0            0 4K   -                META                                  -
0x0000ec2570400000-0x0000ec2570410000        65536 rw-p        53248            0 4K   com              META                                  -
0x0000ec2570410000-0x0000ec2574000000     62849024 ---p            0            0 4K   -                META                                  -
0x0000ec2574000000-0x0000ec2574021000       135168 rw-p         4096            0 4K   -                -                                     -
0x0000ec2574021000-0x0000ec2578000000     66973696 ---p            0            0 4K   -                -                                     -
0x0000ec2578000000-0x0000ec2578021000       135168 rw-p         4096            0 4K   -                -                                     -
0x0000ec2578021000-0x0000ec257c000000     66973696 ---p            0            0 4K   -                -                                     -
0x0000ec257c000000-0x0000ec257c021000       135168 rw-p         4096            0 4K   -                -                                     -
0x0000ec257c021000-0x0000ec2580000000     66973696 ---p            0            0 4K   -                -                                     -
0x0000ec2580fbf000-0x0000ec2580fcf000        65536 ---p            0            0 4K   com              -                                     -
0x0000ec2580fcf000-0x0000ec25811cd000      2088960 rw-p        12288            0 4K   com              STACK-1696938-GC-Thread               -
0x0000ec25811cd000-0x0000ec25811dd000        65536 ---p            0            0 4K   com              -                                     -
0x0000ec25811dd000-0x0000ec25813db000      2088960 rw-p        12288            0 4K   com              STACK-1696937-GC-Thread               -
0x0000ec25813db000-0x0000ec25813eb000        65536 ---p            0            0 4K   com              -                                     -
0x0000ec25813eb000-0x0000ec25815e9000      2088960 rw-p        12288            0 4K   com              STACK-1696936-GC-Thread               -
0x0000ec25815e9000-0x0000ec25815ed000        16384 ---p            0            0 4K   -                -                                     -
0x0000ec25815ed000-0x0000ec25817e7000      2072576 rw-p        32768            0 4K   com              -                                     -
0x0000ec2581808000-0x0000ec258180c000        16384 ---p            0            0 4K   com              STACK-1696934-Common-Cleaner          -
0x0000ec258180c000-0x0000ec2581a06000      2072576 rw-p        94208            0 4K   com              STACK-1696934-Common-Cleaner          -
0x0000ec2581a06000-0x0000ec2581a0a000        16384 ---p            0            0 4K   com              STACK-1696933-Notification-Thread     -
0x0000ec2581a0a000-0x0000ec2581c04000      2072576 rw-p        12288            0 4K   com              STACK-1696933-Notification-Thread     -
0x0000ec2581c04000-0x0000ec2581c08000        16384 ---p            0            0 4K   com              STACK-1696932-C1-CompilerThread0      -
0x0000ec2581c08000-0x0000ec2581e02000      2072576 rw-p        20480            0 4K   com              STACK-1696932-C1-CompilerThread0      -
0x0000ec2581e02000-0x0000ec2581e06000        16384 ---p            0            0 4K   com              STACK-1696931-C2-CompilerThread0      -
0x0000ec2581e06000-0x0000ec2584000000     35627008 rw-p        32768            0 4K   com              GC                                    -
0x0000ec2584000000-0x0000ec2584021000       135168 rw-p         4096            0 4K   -                -                                     -
0x0000ec2584021000-0x0000ec2588000000     66973696 ---p            0            0 4K   -                -                                     -
0x0000ec2588000000-0x0000ec2588021000       135168 rw-p         4096            0 4K   -                -                                     -
0x0000ec2588021000-0x0000ec258c000000     66973696 ---p            0            0 4K   -                -                                     -
0x0000ec258c000000-0x0000ec258c021000       135168 rw-p        12288            0 4K   -                -                                     -
0x0000ec258c021000-0x0000ec2590000000     66973696 ---p            0            0 4K   -                -                                     -
0x0000ec2590020000-0x0000ec259002c000        49152 r-xp        49152            0 4K   com              -                                     /big/jdks/jdk24/lib/libnet.so
0x0000ec259002c000-0x0000ec259003b000        61440 ---p            0            0 4K   com              -                                     /big/jdks/jdk24/lib/libnet.so
0x0000ec259003b000-0x0000ec259003c000         4096 r--p         4096            0 4K   com              -                                     /big/jdks/jdk24/lib/libnet.so
0x0000ec259003c000-0x0000ec259003d000         4096 rw-p         4096            0 4K   com              -                                     /big/jdks/jdk24/lib/libnet.so
0x0000ec2590040000-0x0000ec2590044000        16384 ---p            0            0 4K   com              STACK-1696930-Monitor-Deflation-Thread -
0x0000ec2590044000-0x0000ec259023e000      2072576 rw-p        12288            0 4K   com              STACK-1696930-Monitor-Deflation-Thread -
0x0000ec259023e000-0x0000ec2590242000        16384 ---p            0            0 4K   com              STACK-1696929-Service-Thread          -
0x0000ec2590242000-0x0000ec259043c000      2072576 rw-p        12288            0 4K   com              STACK-1696929-Service-Thread          -
0x0000ec259043c000-0x0000ec2590440000        16384 ---p            0            0 4K   com              STACK-1696928-Signal-Dispatcher       -
0x0000ec2590440000-0x0000ec259063a000      2072576 rw-p        12288            0 4K   com              STACK-1696928-Signal-Dispatcher       -
0x0000ec259063a000-0x0000ec259063e000        16384 ---p            0            0 4K   com              STACK-1696927-Finalizer               -
0x0000ec259063e000-0x0000ec2590838000      2072576 rw-p        90112            0 4K   com              STACK-1696927-Finalizer               -
0x0000ec2590838000-0x0000ec259083c000        16384 ---p            0            0 4K   com              STACK-1696926-Reference-Handler       -
0x0000ec259083c000-0x0000ec2590a36000      2072576 rw-p        94208            0 4K   com              STACK-1696926-Reference-Handler       -
0x0000ec2590a36000-0x0000ec2590a46000        65536 ---p            0            0 4K   com              -                                     -
0x0000ec2590a46000-0x0000ec2590c44000      2088960 rw-p        16384            0 4K   com              STACK-1696925-VM-Thread               -
0x0000ec2590c44000-0x0000ec2590c54000        65536 ---p            0            0 4K   com              -                                     -
0x0000ec2590c54000-0x0000ec2590e52000      2088960 rw-p        12288            0 4K   com              -                                     -
0x0000ec2590e52000-0x0000ec2590e62000        65536 ---p            0            0 4K   com              -                                     -
0x0000ec2590e62000-0x0000ec2591060000      2088960 rw-p        12288            0 4K   com              -                                     -
0x0000ec2591060000-0x0000ec2591070000        65536 ---p            0            0 4K   com              -                                     -
0x0000ec2591070000-0x0000ec2591470000      4194304 rw-p        28672            0 4K   com              -                                     -
0x0000ec2591470000-0x0000ec2591480000        65536 ---p            0            0 4K   com              -                                     -
0x0000ec2591480000-0x0000ec259167e000      2088960 rw-p        12288            0 4K   com              STACK-1696921-GC-Thread               -
0x0000ec259167e000-0x0000ec259168e000        65536 ---p            0            0 4K   com              -                                     -
0x0000ec259168e000-0x0000ec259188c000      2088960 rw-p        12288            0 4K   com              -                                     -
0x0000ec259188c000-0x0000ec259189c000        65536 ---p            0            0 4K   com              -                                     -
0x0000ec259189c000-0x0000ec259207a000      8249344 rw-p        12288            0 4K   com              STACK-1696919-GC-Thread,GC            -
0x0000ec259207a000-0x0000ec259783a000     92012544 ---p            0            0 4K   -                GC                                    -
0x0000ec259783a000-0x0000ec2597906000       835584 rw-p       770048            0 4K   com              GC                                    -
0x0000ec2597906000-0x0000ec25983fe000     11501568 ---p            0            0 4K   -                GC                                    -
0x0000ec25983fe000-0x0000ec2598400000         8192 rw-p         8192            0 4K   com              GC                                    -
0x0000ec2598400000-0x0000ec2598670000      2555904 rwxp       651264            0 4K   com              CODE                                  -
0x0000ec2598670000-0x0000ec259f937000    120352768 ---p            0            0 4K   -                CODE                                  -
0x0000ec259f937000-0x0000ec259fba7000      2555904 rwxp       626688            0 4K   com              CODE                                  -
0x0000ec259fba7000-0x0000ec259fec7000      3276800 ---p            0            0 4K   -                CODE                                  -
0x0000ec259fec7000-0x0000ec25a0137000      2555904 rwxp       237568            0 4K   com              CODE                                  -
0x0000ec25a0137000-0x0000ec25a7400000    120360960 ---p            0            0 4K   -                CODE                                  -
0x0000ec25a7400000-0x0000ec25afe3e000    144957440 r--s      1114112            0 4K   com              -                                     /big/jdks/jdk24/lib/modules
0x0000ec25afe40000-0x0000ec25afe50000        65536 r--s        65536            0 4K   com              -                                     /tmp/bigfile
```

In the final line you can see the 64KB window of the 1GB file mapped into the JVM's memory layout.

If you are dealing with a lot of memory mapped files then you may find the new `-XX:+PrintMemoryMapAtExit` option a useful addition to your diagnostic toolkit.

If you want to see exactly which options are added and removed in each JDK then visit <https://chriswhocodes.com/hotspot_option_differences.html>
