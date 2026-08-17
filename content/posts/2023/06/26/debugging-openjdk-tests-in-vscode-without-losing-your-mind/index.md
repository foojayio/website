---
title: "Debugging OpenJDK Tests in VSCode (Without Losing Your Mind)"
slug: "debugging-openjdk-tests-in-vscode-without-losing-your-mind"
date: "2023-06-26T11:27:55+00:00"
lastmod: "2023-07-10T12:46:48+00:00"
description: "jtreg is the test harness used by the JDK test framework. Attaching a debugger cumbersome to enable debugging in VSCode. But worry no more!"
authors:
  - "johannes-bechberger"
image: "Screenshot-from-2023-06-21-15-13-18.png"
categories:
  - "Release Notes"
  - "Testing"
  - "Tools"
  - "VS Code"
tags:
related_posts:
  - "7-ways-to-contribute-to-openjdk"
  - "are-java-security-updates-important"
  - "am-i-testing-the-right-way"
  - "openjdk-january-2026-critical-patch-update-and-patch-set-update-released"
enlighterjs: true
frozen: false
---

Consider you want to debug a test case of the JDK like [serviceability/AsyncGetCallTrace](https://github.com/openjdk/jdk/tree/master/test/hotspot/jtreg/serviceability/AsyncGetCallTrace). This test, and many others, are implemented using the *Regression Test Harness for the JDK* (jtreg):
> `jtreg` is the test harness used by the JDK test framework. This framework is intended primarily for regression tests. It can also be used for unit tests, functional tests, and even simple product tests -- in other words, just about any type of test except a conformance test, which belong in a TCK.
>
> As well as API tests, jtreg is designed to be well suited for running both positive and negative compiler tests, simple manual GUI tests, and (when necessary) tests written in shell script. jtreg also takes care of compiling tests as well as executing them, so there is no need to precompile any test classes.
> https://openjdk.org/jtreg/

JTREG is quite powerful, allowing you to combine C++ and Java code, but it makes debugging the C++ parts hard. You could, of course, just debug using `printf`. This works but also requires lots of recompiles during every debugging session. Attaching a debugger like gdb is possible but rather cumbersome, especially if you want to bring this into a [`launch.json`](https://code.visualstudio.com/docs/cpp/launch-json-reference) to enable debugging in [VSCode](https://code.visualstudio.com).

But worry no more: My new [*vsreg*](https://github.com/parttimenerd/vsreg) utility will do this for you. 🙂 You can obtain the tool by just cloning its GitHub [repository](https://github.com/parttimenerd/vsreg):

```bash
git clone https://github.com/parttimenerd/vsreg
```


Then pass the make test command to it, which you use to run the test that you want to debug:

```bash
vsreg/vsreg.py "ASGCT debug" -- make test TEST=jtreg:test/hotspot/jtreg/serviceability/AsyncGetCallTrace JTREG="VERBOSE=all"
```


Be sure always to pass `JTREG="VERBOSE=all"`: vsreg executes the command, parses the output, and adds a launch config with the label "ASGCT debug" to the `.vscode/launch.json` file in the current folder.

*The utility is MIT licensed and only tested on Linux.*

Example Usage {#h2-0-example-usage}
-----------------------------------

You're now able to select "ASGCT debug" in "Run and Debug":  

<figure class="aligncenter size-full is-resized">
 <img fetchpriority="high" decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2023/06/Screenshot-from-2023-06-21-15-04-38.png" alt="" class="wp-image-903" width="767" height="218">
</figure>

You can choose the launch config and run the jtreg test with a debugger:  

<figure class="aligncenter size-full is-resized">
 <img decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2023/06/Screenshot-from-2023-06-21-15-07-40.png" alt="" class="wp-image-904" width="765" height="544">
</figure>

The debugger pauses on a segfault, but there are always a few at the beginning of the execution that can safely be ignored. We can use the program's pause to add a break-point at an interesting line. After hitting the break-point, we're able to inspect the local variables...  

<figure class="aligncenter size-full is-resized">
 <img decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2023/06/Screenshot-from-2023-06-21-15-11-51.png" alt="" class="wp-image-905" width="767" height="545">
</figure>

... and do things like stepping over a line:  

<figure class="aligncenter size-full is-resized">
 <img loading="lazy" decoding="async" src="https://mostlynerdless.de/wp-content/uploads/2023/06/Screenshot-from-2023-06-21-15-13-18.png" alt="" class="wp-image-906" width="767" height="545">
</figure>

Recompilation {#h2-1-recompilation}
-----------------------------------

If you want to recompile the tests, use `make images test-image`. You can add a task to your `.vscode/tasks.json` file and pass the label to the `--build-task` option:

```json
{
  "version": "2.0.0",
  "tasks": [
    {
      "label": "Make test-image",
      "type": "shell",
      "options": {

          "cwd": "${workspaceFolder}"
      },
      "command": "/usr/bin/gmake",
      "args": ["images", "test-image"],
      "problemMatcher": ["$gcc"]
    }
  ]
}
```


Options {#h2-2-options}
-----------------------

vsreg has a few options:

```bash
usage: vsreg.py [-h] [-t TEMPLATE] [-d] [-b TASK] LABEL COMMAND [COMMAND ...]

Create a debug launch config for a JTREG test run

positional arguments:
  LABEL                 Label of the config
  COMMAND               Command to run

options:
  -h, --help            show this help message and exit
  -t TEMPLATE, --template TEMPLATE
                        Template to use for the launch config, 
                        or name of file without suffix in 
                        vsreg/template folder
  -d, --dry-run         Only print the launch config
  -b TASK, --build-task TASK
                        Task to run before the command
```


An example template looks like this:

```json
{
  "name": "$NAME",
  "type": "cppdbg",
  "request": "launch",
  "program": "",
  "args": [],
  "stopAtEntry": false,
  "cwd": "",
  "environment": [],
  "externalConsole": false,
  "MIMode": "gdb",
  "miDebuggerPath": "/usr/bin/gdb",
  "setupCommands": [
    {
      "description": "Enable pretty-printing for gdb",
      "text": "-enable-pretty-printing",
      "ignoreFailures": true
    },
    {   
      "description": "The new process is debugged after a fork. The parent process runs unimpeded.",
      "text": "-gdb-set follow-fork-mode child",
      "ignoreFailures": true
    }
  ],
  "preLaunchTask": ""
}
```


vsreg fills in `$NAME` (with the label), `program` (with the used Java binary), `args`, `cwd`, environment and `preLaunchTask`.

Conclusion {#h2-3-conclusion}
-----------------------------

vsreg is one of these utilities that solve one specific itch: I hope it also helps others; feel free to contribute to this tool, adding new templates and other improvements on [GitHub](https://github.com/parttimenerd/vsreg).

The tool is inspired by [bear](https://github.com/rizsotto/Bear), "a tool that generates a compilation database for clang tooling."

If you're wondering why I have a renewed interest in debugging: I'm working full-time on a new proof-of-concept implementation related to [JEP 435](https://openjdk.org/jeps/435).

***This project is part of my work in the [SapMachine](https://sapmachine.io/) team at [SAP](https://sap.com), making profiling easier for everyone. The article appeared on my personal blog [mostlynerdless.de](https://mostlynerdless.de/) first.***
