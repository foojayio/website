---
title: "Introduction to JVM Unified Logging (JEP-158 / JEP-271)"
slug: "introduction-to-jvm-unified-logging-jep-158-jep-271"
date: "2021-03-01T17:58:06+00:00"
lastmod: "2021-08-23T13:05:04+00:00"
description: "Unified logging was introduced in JDK 9, let's begin with a recapitulation of JEP-158, laying out the foundation of JVM unified logging."
authors:
  - "brice-dutheil"
image: "Favicon-3-2.png"
categories:
  - "JEPs"
tags:
related_posts:
  - "your-tls-stack-is-lying-about-zero-copy"
  - "banned-threadlocal-java-scoped-values"
  - "java-for-scripting"
  - "project-panama-for-newbies-part-2"
enlighterjs: true
frozen: false
---

Unified logging was introduced in JDK 9, and is available for us all, in the JDK 11 LTS. Like other great serviceability feature (`jcmd` or JFR) this was inspired by JRockit.

As a pre-jdk9 user I had my way using the explicit GC logging options. On several occasions it was also useful to toggle other options like tracing class un/loading.

Starting from JDK 9, the JVM maintainers chose to overhaul the way JVM logs things. I believe that unified logging is great, but after using it I believe its purpose targets more JDK maintainers, eventually library maintainers, than end users. Meanwhile it's likely that one may need to activate GC logs.

JDK 9 may seem old news today, yet I still find it difficult to know exactly what will be effectively logged. Very well written blog posts such as [this one](https://blog.codefx.org/java/unified-logging-with-the-xlog-option/) from [Nicolai Parlog](https://twitter.com/nipafx) usually only touch the surface of unified logging and usually focus on GC logs only. The official documentation rarely documents JVM logging well enough, apart from how old options translate in unified logging.

While GC logging is usually a well covered topic, I found there are information gaps like *IHOP* messages missing as the multitude of tags and their various levels may not be set as necessary.

In my opinion the flexibility of this logging system brought a major downside from a user's perspective in its configuration correctness, and I think in some ways it's more obscure compared to the previous explicit logging flags.

Anyway, let's begin our exploration with a small recapitulation of [JEP-158](https://openjdk.java.net/jeps/158) which lays out the foundation of JVM unified logging.

### Unified JVM Logging (JEP-158)

The JEP describes how the logging configuration works in the command line:

```
-Xlog[:option]
    option         :=  [][:[][:[][:]]]
                       'help'
                       'disable'
    what           :=  [,...]
    selector       :=  [*][=]
    tag-set        :=  [+...]
                       'all'
    tag            :=  name of tag
    level          :=  trace
                       debug
                       info
                       warning
                       error
    output         :=  'stderr'
                       'stdout'
                       [file=]
    decorators     :=  [,...]
                       'none'
    decorator      :=  time
                       uptime
                       timemillis
                       uptimemillis
                       timenanos
                       uptimenanos
                       pid
                       tid
                       level
                       tags
    output-options :=  [,...]
    output-option  :=  filecount=
                       filesize=
                       parameter=value
```


This command-line option is the main entry point to unified logging. It allows configuring loggers that used to require multiple options into a single argument.

Moreover, it's now possible to declare multiple log configurations simply by adding additional `-Xlog...​` options. Their output must however be unique in these logs, otherwise the last option declaration will override the previous log configuration.

Also, if you already heard of `jcmd`, unified logging JEP enabled log configurable at runtime. It's the second main entry point to unified logging. *If you didn't hear about `jcmd` you really should check it out. It's the future of the serviceability / diagnostic command on the JDK.*
> **Controlling at runtime**
>
> Logging can be controlled at runtime through Diagnostic Commands (the `jcmd` utility). Everything that can be specified on the command line can also be specified dynamically with Diagnostic Commands. Since diagnostic commands are automatically exposed as MBeans it will be possible to use JMX to change logging configuration in runtime.

Now let's see what we can do with unified logging and profit from the insight it gives in the JVM.

### Command Line

While the JEP explains things, it's usually not how we get the first contact: We use the command line help, which is describing just as well how to use from the `java` binary.

```bash
$ java -Xlog:help
-Xlog Usage: -Xlog[:[selections][:[output][:[decorators][:output-options]]]]
         where 'selections' are combinations of tags and levels of the form tag1[+tag2...][*][=level][,...]
         NOTE: Unless wildcard (*) is specified, only log messages tagged with exactly the tags specified will be matched.

Available log levels:
 off, trace, debug, info, warning, error

Available log decorators:
 time (t), utctime (utc), uptime (u), timemillis (tm), uptimemillis (um), timenanos (tn), uptimenanos (un), hostname (hn), pid (p), tid (ti), level (l), tags (tg)
 Decorators can also be specified as 'none' for no decoration.

Available log tags:
 add, age, alloc, annotation, aot, arguments, attach, barrier, biasedlocking, blocks, bot, breakpoint, bytecode, cds, census, class, classhisto, cleanup, codecache, compaction, compilation, constantpool, constraints, container, coops, cpu, cset, data, datacreation, dcmd, decoder, defaultmethods, director, dump, ergo, event, exceptions, exit, fingerprint, free, freelist, gc, handshake, hashtables, heap, humongous, ihop, iklass, init, inlining, interpreter, itables, jfr, jit, jni, jvmti, liveness, load, loader, logging, malloc, mark, marking, membername, memops, metadata, metaspace, methodcomparator, mirror, mmu, module, monitorinflation, monitormismatch, nestmates, nmethod, normalize, objecttagging, obsolete, oldobject, oom, oopmap, oops, oopstorage, os, pagesize, parser, patch, path, perf, phases, plab, preorder, preview, promotion, protectiondomain, purge, redefine, ref, refine, region, reloc, remset, resolve, safepoint, sampling, scavenge, setting, smr, stackmap, stacktrace, stackwalk, start, startuptime, state, stats, stringdedup, stringtable, subclass, survivor, sweep, system, table, task, thread, time, timer, tlab, tracking, unload, unshareable, update, verification, verify, vmoperation, vmthread, vtables, vtablestubs, workgang
 Specifying 'all' instead of a tag combination matches all tag combinations.

Described tag sets:
 logging: Logging for the log framework itself

Available log outputs:
 stdout/stderr
 file=
  If the filename contains %p and/or %t, they will expand to the JVM's PID and startup timestamp, respectively.
  Additional output-options for file outputs:
   filesize=..  - Target byte size for log rotation (supports K/M/G suffix). If set to 0, log rotation will not trigger automatically, but can be performed manually (see the VM.log DCMD).
   filecount=.. - Number of files to keep in rotation (not counting the active file). If set to 0, log rotation is disabled. This will cause existing log files to be overwritten.

Some examples:
 -Xlog
         Log all messages up to 'info' level to stdout with 'uptime', 'levels' and 'tags' decorations.
         (Equivalent to -Xlog:all=info:stdout:uptime,levels,tags).

 -Xlog:gc
         Log messages tagged with 'gc' tag up to 'info' level to stdout, with default decorations.

 -Xlog:gc,safepoint
         Log messages tagged either with 'gc' or 'safepoint' tags, both up to 'info' level, to stdout, with default decorations.
         (Messages tagged with both 'gc' and 'safepoint' will not be logged.)

 -Xlog:gc+ref=debug
         Log messages tagged with both 'gc' and 'ref' tags, up to 'debug' level, to stdout, with default decorations.
         (Messages tagged only with one of the two tags will not be logged.)

 -Xlog:gc=debug:file=gc.txt:none
         Log messages tagged with 'gc' tag up to 'debug' level to file 'gc.txt' with no decorations.

 -Xlog:gc=trace:file=gctrace.txt:uptimemillis,pid:filecount=5,filesize=1m
         Log messages tagged with 'gc' tag up to 'trace' level to a rotating fileset of 5 files of size 1MB,
         using the base name 'gctrace.txt', with 'uptimemillis' and 'pid' decorations.

 -Xlog:gc::uptime,tid
         Log messages tagged with 'gc' tag up to 'info' level to output 'stdout', using 'uptime' and 'tid' decorations.

 -Xlog:gc*=info,safepoint*=off
         Log messages tagged with at least 'gc' up to 'info' level, but turn off logging of messages tagged with 'safepoint'.
         (Messages tagged with both 'gc' and 'safepoint' will not be logged.)

 -Xlog:disable -Xlog:safepoint=trace:safepointtrace.txt
         Turn off all logging, including warnings and errors,
         and then enable messages tagged with 'safepoint' up to 'trace' level to file 'safepointtrace.txt'.
```


Also, you may have to enable logs on a running VM, well in this case the `jcmd`  

sub-command `VM.log` is your tool of choice.

```bash
$ jcmd $(pidof java) help VM.log
6:
VM.log
Lists current log configuration, enables/disables/configures a log output, or rotates all logs.

Impact: Low: No impact

Permission: java.lang.management.ManagementPermission(control)

Syntax : VM.log [options]

Options: (options must be specified using the  or = syntax)
        output : [optional] The name or index (#) of output to configure. (STRING, no default value)
        output_options : [optional] Options for the output. (STRING, no default value)
        what : [optional] Configures what tags to log. (STRING, no default value)
        decorators : [optional] Configures which decorators to use. Use 'none' or an empty value to remove all. (STRING, no default value)
        disable : [optional] Turns off all logging and clears the log configuration. (BOOLEAN, no default value)
        list : [optional] Lists current log configuration. (BOOLEAN, no default value)
        rotate : [optional] Rotates all logs. (BOOLEAN, no default value)
```


### Configuration

From the help and the JEP above here's what to retain:

Tags
:
    When a log message is shown, it should be associated with a set of tags in the JVM which identify by names: `os`, `gc`, `modules`...​

    * We can apply different settings for individual tags.
    * The character `*` denotes *wildcard* tag match. Not using `*` tells the logger to log only messages that match exactly the specified tags.

Levels
:
    We can perform logging at different levels. The available levels are `error`, `warning`, `info`, `debug`, `trace` and `develop`.

    To disable logging, use the alternative `off`.

Outputs
:
    The output currently supports 3 types: stdout, stderr, or a text file, which can be set up for log file rotation based on written size and a number of files to rotate (for example: each 10MB, keep 5 files in rotation)

Decorators
:
    There are more details about the message called decorators. Here is the list:

    * `time`/`timemillis`/`timenanos`: current time and date (ISO-8601 format)
    * `uptime`/`uptimemillis`/`uptimenanos`: time since the start of the JVM
    * `pid`: process identifier
    * `tid`: thread identifier
    * `level`: level associated with the log message
    * `tags`: tag associated with the log message

Default settings
:
    * tag-set: `all`.
    * level: `info`
    * output: `stdout`
    * decorators: `uptime`, `level`, `tags`

In practice this will give for `java` and `jcmd` in that order:

```
-Xlog:pagesize,os*,os+container=trace:file=/var/log/%t-os-container-pagesise.log:uptime,tags,level
```


```bash
$ jcmd $(pidof java) VM.log output=/var/log/%t-os-container-pagesise.log what=pagesize,os*,os+container=trace decorators=uptime,tags,level
```


The above commands are equivalent, but note that depending on the specified tags and level, the log content may be less useful when enabled at a later time. In the above example in particular the `os+container=trace` will output some interesting logs only during JVM startup.

#### Tag Set \& Tag Prefixes

In the rest of the article I will mention two related notions about tags. Tags are not hierarchic, however, when their use in the JDK code base suggests there is still some kind of *hierarchy*.

As we'll see later some tags are standalone tags, but a large proportion of tags are always logged with at least another tag. We could say they are part of a group with a *root* tag like `gc`, `class`, etc. Those are my observations, but let's look at the JEP-158 diff.

There's one file that caught attention, it's [logTagSet.hpp](https://github.com/AdoptOpenJDK/openjdk-jdk11u/commit/fc2a1798bac1bfda6929dc55936ba7f9e4cf0208#diff-7cb36a4a80175eed80c087a48e4f071f)
> The tagset represents a combination of tags that occur in a log call somewhere. Tagsets are created automatically by the `LogTagSetMappings` and should never be instantiated directly somewhere else.

When one sees a `class, path` combination it's in fact a tag set. I will refer to these as *tag-set* , *tag set* , or *tagset* . And, I'll use the term *log tag root* to indicate that a tag is used as the first tag, it's generally about a JVM component like GC, classes, or JFR.

There is another construct on top of *tagsets* , that is called log prefix. We can learn about it in [logPrefix.hpp](https://github.com/AdoptOpenJDK/openjdk-jdk11u/commit/fc2a1798bac1bfda6929dc55936ba7f9e4cf0208#diff-c7fbf2952ef86b686c1849f6735041c9)
> Prefixes prepend each log message for a specified tagset with the given prefix. A prefix consists of a format string and a value or callback. Prefixes are added after the decorations but before the log message.

The log prefixes allow it to prepend the log message (that's the prefix) with something for declared *tagsets* . As we'll see later there is currently only one list of tagsets that uses the log prefix mechanism, GC logging to print the *GC id*:

* [src/hotspot/share/logging/logPrefix.hpp](https://github.com/AdoptOpenJDK/openjdk-jdk11u/blob/jdk-11.0.8%2B10/src/hotspot/share/logging/logPrefix.hpp)

#### Migrating the GC Log Configuration (JEP-271)

While this topic may seem covered by other blogs, I wasn't satisfied by the actual equivalence or not of the log configuration. I got the configuration wrong, understand *incomplete*, more than once until I decided to dive in.

GC unified logging is covered by another JEP, [JEP 271: Unified GC Logging](https://openjdk.java.net/jeps/271), which relies on JEP-158 as mentioned earlier. This JEP is much more concise and barely describes how the previous logging option will be turned in unified log tags.

One of the best source came from [Poonam Bajaj Parhar](https://twitter.com/poonam_bajaj)'s talk on [unified GC logs](https://www.slideshare.net/PoonamBajaj5/lets-learn-to-talk-to-gc-logs-in-java-9). However the most interesting data is not searchable because it's an image of a table and everything is not there, for people that need to work with other GCs with a different set of tags.

The basic translation of the following usual GC logging configuration:

```
-XX:+PrintGCDetails                           \
-XX:+PrintGCApplicationStoppedTime            \
-XX:+PrintGCApplicationConcurrentTime         \
-XX:+PrintGCCause                             \
-XX:+PrintGCID                                \
-XX:+PrintTenuringDistribution                \
-XX:+PrintGCDateStamps                        \
-XX:+UseGCLogFileRotation                     \
-XX:NumberOfGCLogFiles=5                      \
-XX:GCLogFileSize=10M                         \
-Xloggc:/var/log/`date +%FT%H-%M-%S`-gc.log   \
```


These flags could be translated to the following configuration:

```
-Xlog:gc*,gc+heap=debug,gc+ref=debug,gc+ergo*=trace,gc+age*=trace,gc+phases*=debug,safepoint*:file=/var/log/%t-gc.log:uptime,tags,level:filecount=10,filesize=20M
```


Let's break down this configuration

```
-Xlog:
  gc*,
  gc+heap=debug,
  gc+ref=debug,
  gc+ergo*=trace,
  gc+age*=trace,
  gc+phases*=debug,
  safepoint*
  :file=/var/log/%t-gc.log
  :time,tags,level
  :filecount=5,filesize=10M
```


* Line 2: `PrintGCDetails` (remember that default level is `info`)
* Line 3: `PrintHeapAtGC`
* Line 4: ``PrintReferenceGC```
* Line 5: `PrintAdaptiveSizePolicy`
* Line 6: `PrintTenuringDistribution`
* Line 7: `PrintParallelOldGCPhaseTimes`
* Line 8: `PrintGCApplicationConcurrentTime` and `PrintGCApplicationStoppedTime`
* Line 9: `Xloggc`
* Line 10: `PrintGCDateStamps` (but also decorates with tags and level, useful to know identify the source of the log).
* Line 11: `UseGCLogFileRotation`, `NumberOfGCLogFiles`, `GCLogFileSize`

Note that some options do not have equivalents in unified GC logging. Either because the tag system is more precise, or because the log message themselves changed, e.g. the GC cause and the GC id are now always logged.

Now it's the right opportunity to warn about the slight caveats of this log configuration.

This configuration is fine and works reasonably well, **BUT** this configuration actually may miss some logs like some `ihop`, which is not only logged with the `ergo` tag as we'll see in next blog entry.
