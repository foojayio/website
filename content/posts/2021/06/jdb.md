---
title: "How to Debug Java on the Command Line"
slug: "jdb"
date: "2021-06-24T16:51:14+00:00"
lastmod: "2021-06-24T16:51:16+00:00"
description: "Some bugs are hard to replicate on your personal computer but easily replicated on production or test machines!"
canonical: "https://jfeatures.com/blog/jdb"
authors:
  - "vipin-sharma"
image: "https://foojay.io/wp-content/uploads/2021/06/ebook_upd.png"
categories:
  - "Tools"
  - "Tutorials"
tags:
related_posts:
  - "introducing-the-boxlang-ide-plugin-for-intellij"
  - "tornadovm-for-risc-v-accelerators"
  - "java-on-azure-tooling-update-july-2022"
  - "java-on-azure-tooling-update-june-2022"
enlighterjs: true
frozen: false
---

Some bugs are hard to replicate on your personal computer but easily replicated on production or test machines. It is a common situation that professional Java developers deal with frequently. To debug such problems, OpenJDK provides two tools, `remote debugging` and `jdb`.

This article focuses on `jdb`.

For Java applications, typical production and test machines are Linux servers without display managers, so that only command line tools are available. Here we cannot use professional IDEs like IntelliJ IDEA, Eclipse, or Apache NetBeans IDE.

In such scenarios, we can use `jdb`. `jdb` is a command line debugger and it is part of the OpenJDK.

### Troubleshoot Java with "jdb" Utility {#h3-0-troubleshoot-java-with-jdb-utility}

jdb is available in the jdk/bin directory. It uses the Java Debug Interface (JDI) to launch and connect to the target JVM. The Java Debug Interface (JDI) provides a Java programming language interface for debugging Java programming language applications. JDI is a part of the [Java Platform Debugger Architecture](https://docs.oracle.com/en/java/javase/16/docs/specs/jpda/architecture.html).

In this section, we will see how to attach jdb to java application and start debugging and monitoring.

#### jdb Command

This is format of the jdb command:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">jdb [options] [classname] [arguments]

options:    This represents the jdb command-line options (e.g. attach, launch).
classname:  This represents the name of the main class to debug.
arguments:  This represents the arguments that are passed to the main() method of the class.</pre>

#### Sample Java App for Debugging

Following is a sample Java class we are going to debug and try to understand the different features available. It is important to compile this class with the "-g" option (javac -g Test.java), which generates all the debugging information including local variables. By default, only line number and source file information is generated.

<pre class="EnlighterJSRAW" data-enlighter-language="java">public class Test
{
    public static void main(String[] args)
    {
        System.out.println("First Line of main function");
        System.out.println("Second Line of main function");
        System.out.println("Third Line of main function");

        int i=0;
        System.out.println("i: " + i);
        i = 2;
        System.out.println("i: " + i);

        while(true)
        {
        }
    }
}</pre>

#### Attach jdb to the Java application

The command below is the most common way to start an application with the jdb debugger. Here we are not passing any jdb options, we have only passed the class name, which doesn't require any argument:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">jdb Test</pre>

In this way, we start executing the main class "Test" in a similar way to how we start in a professional IDE. jdb stops the JVM before executing that class's first instruction.

Another way to use the jdb command is by attaching it to a JVM that's already running. The syntax for starting JVM with debugger port is:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 Test</pre>

To attach jdb with this remote jvm use below syntax:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">jdb -attach 5005</pre>

In this article, we will not see remote debugging in detail.

#### Debugging and Monitoring

Following is the command to attach jdb with Java program Test:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">/jdk/bin/jdb Test 
Initializing jdb ...
&gt;</pre>

Set a break point at line 5 using "stop", as follows:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">&gt; stop at Test:5
Deferring breakpoint Test:5.
It will be set after the class is loaded.
&gt;</pre>

Start execution of application's main class using "run":

<pre class="EnlighterJSRAW" data-enlighter-language="generic">&gt; run
run  Test
Set uncaught java.lang.Throwable
Set deferred uncaught java.lang.Throwable
&gt;
VM Started: Set deferred breakpoint Test:5

Breakpoint hit: "thread=main", Test.main(), line=5 bci=0
5           System.out.println("First Line of main function");</pre>

Execute current line using "step":

<pre class="EnlighterJSRAW" data-enlighter-language="generic">main[1] step
&gt; First Line of main function
Step completed: "thread=main", Test.main(), line=6 bci=8
6            System.out.println("Second Line of main function");</pre>

Execute current line using "step":

<pre class="EnlighterJSRAW" data-enlighter-language="generic">main[1] step
&gt; Second Line of main function
Step completed: "thread=main", Test.main(), line=7 bci=16
7            System.out.println("Third Line of main function");</pre>

Printing local variable i using "print":

<pre class="EnlighterJSRAW" data-enlighter-language="generic">main[1] print i
i = 0</pre>

Printing all local variables in current stack frame using "locals":

<pre class="EnlighterJSRAW" data-enlighter-language="generic">main[1] locals
Method arguments:
args = instance of java.lang.String[0] (id=841)
Local variables:
i = 0</pre>

Dump a thread's stack using "where":

<pre class="EnlighterJSRAW" data-enlighter-language="generic">main[1] where
[1] Test.main (Test.java:10)</pre>

List threads in running application using "threads":

<pre class="EnlighterJSRAW" data-enlighter-language="generic">main[1] threads
Group system:
(java.lang.ref.Reference$ReferenceHandler)804 Reference Handler running
(java.lang.ref.Finalizer$FinalizerThread)805 Finalizer cond. waiting
(java.lang.Thread)806 Signal Dispatcher running
(java.lang.Thread)803 Notification Thread running
Group main:
(java.lang.Thread)1 main running
Group InnocuousThreadGroup:
(jdk.internal.misc.InnocuousThread)807 Common-Cleaner cond. waiting</pre>

Continue execution from the breakpoint using `cont`:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">main[1] cont
&gt; i: 0
i: 2</pre>

All available commands in jdb using "help":

<pre class="EnlighterJSRAW" data-enlighter-language="generic">main[1] help</pre>

### Command List {#h3-1-command-list}

<pre class="EnlighterJSRAW" data-enlighter-language="generic">connectors -- list available connectors and transports in this VM
run [class [args]] -- start execution of application's main class
threads [threadgroup] -- list threads
thread -- set default thread
suspend [thread id(s)] -- suspend threads (default: all)
resume [thread id(s)] -- resume threads (default: all)
where [ | all] -- dump a thread's stack
wherei [ | all]-- dump a thread's stack, with pc info
up [n frames] -- move up a thread's stack
down [n frames] -- move down a thread's stack
kill -- kill a thread with the given exception object
interrupt -- interrupt a thread
print -- print value of expression
dump -- print all object information
eval -- evaluate expression (same as print)
set = -- assign new value to field/variable/array element
locals -- print all local variables in current stack frame
classes -- list currently known classes
class -- show details of named class
methods -- list a class's methods
fields -- list a class's fields
threadgroups -- list threadgroups
threadgroup -- set current threadgroup
stop [go|thread] []
-- set a breakpoint
-- if no options are given, the current list of breakpoints is printed
-- if "go" is specified, immediately resume after stopping
-- if "thread" is specified, only suspend the thread we stop in
-- if neither "go" nor "thread" are specified, suspend all threads
-- if an integer is specified, only stop in the specified thread
-- "at" and "in" have the same meaning
-- can either be a line number or a method:
-- :
-- .[(argument_type,...)]
clear .[(argument_type,...)]
-- clear a breakpoint in a method
clear : -- clear a breakpoint at a line
clear -- list breakpoints
catch [uncaught|caught|all] |
-- break when specified exception occurs
ignore [uncaught|caught|all] |
-- cancel 'catch' for the specified exception
watch [access|all] .
-- watch access/modifications to a field
unwatch [access|all] .
-- discontinue watching access/modifications to a field
trace [go] methods [thread]
-- trace method entries and exits.
-- All threads are suspended unless 'go' is specified
trace [go] method exit | exits [thread]
-- trace the current method's exit, or all methods' exits
-- All threads are suspended unless 'go' is specified
untrace [methods] -- stop tracing method entrys and/or exits
step -- execute current line
step up -- execute until the current method returns to its caller
stepi -- execute current instruction
next -- step one line (step OVER calls)
cont -- continue execution from breakpoint
list [line number|method] -- print source code
use (or sourcepath) [source file path]
-- display or change the source path
exclude [, ... | "none"]
-- do not report step or method events for specified classes
classpath -- print classpath info from target VM
monitor -- execute command each time the program stops
monitor -- list monitors
unmonitor -- delete a monitor
read -- read and execute a command file
lock -- print lock info for an object
threadlocks [thread id] -- print lock info for a thread
pop -- pop the stack through and including the current frame
reenter -- same as pop, but current frame is reentered
redefine
-- redefine the code for a class
disablegc -- prevent garbage collection of an object
enablegc -- permit garbage collection of an object
!! -- repeat last command
-- repeat command n times
# -- discard (no-op)
help (or ?) -- list commands
dbgtrace [flag] -- same as dbgtrace command line option
version -- print version information
exit (or quit) -- exit debugger
: a full class name with package qualifiers
: a class name with a leading or trailing wildcard ('*')
: thread number as reported in the 'threads' command
: a Java(TM) Programming Language expression.</pre>

Most common syntax is supported.

Startup commands can be placed in either "jdb.ini" or ".jdbrc" in user.home or user.dir.

Quitting jdb:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">quit</pre>

### Conclusion {#h3-2-conclusion}

OpenJDK provides many amazing troubleshooting and diagnosis tools. These tools help you to fix issues in your production application quickly.

`jdb` can be a great help when there is no way other than debugging the application and your favourite IDE is not available.

Knowing features like this helps you get the best java jobs, that's why to help you I wrote ebook [5 steps to Best Java Jobs](https://jfeatures.com/).

Download this step-by-step guide for free!

![](https://jfeatures.com/img/ebook_upd.png)
