---
title: "Unconventional Remote Process Control in Java | Foojay.io Today"
slug: "presenting-xpipe"
date: "2023-05-08T14:03:08+00:00"
lastmod: "2023-05-08T14:03:35+00:00"
description: "If the user has already installed the programs to connect to remote systems, why not try to use them from Java instead of libraries?"
authors:
  - "christopher-schnick"
image: "/images/posts/2023/05/presenting-xpipe/social_16-9.png"
categories:
  - "Java Core"
  - "JavaFX"
tags:
related_posts:
  - "beginning-javafx-with-intellij"
  - "building-javafx-with-gradle"
  - "creating-a-javafx-world-clock-from-scratch-part-1"
  - "azul-brings-java-from-edge-to-cloud"
enlighterjs: true
frozen: false
---

Have you ever wanted to control a remote process from a Java application? Essentially something like a `ProcessBuilder` for remote systems that allows you to run and configure a process on almost any system and also supports all regular features that you would expect from it.

Now obviously, this isn't a revolutionary idea. You can easily take an SSH library like the [newer jsch fork](https://github.com/mwiede/jsch) and use the `exec` or `shell` channel to control remote processes or remote shells by just copying and pasting some tutorial code. So what is this about then?

There are a couple of limitations that all established protocol-based solutions share:

* They lack flexibility due to the requirement to have the appropriate server software installed and running in the first place, for example an OpenSSH server. Nowadays on many systems, e.g. containers, such a server is not installed and would require tedious setup, ruling out the approach for such systems.
* Even if you have everything set up and have many containers or virtual machine instances running on a system, you would have to do quite a bit of port juggling to avoid port conflicts.
* Outside the SSH bubble there exist a variety of different other remote shell connection methods and each one would require its own implementation/library to be supported.
* You would also have to handle proxies and nested connections as nowadays a remote shell connection might go through multiple intermediate systems due to firewalls, login servers, proxies, and more.

An alternative approach to remote shell connections {#h2-0-an-alternative-approach-to-remote-shell-connections}
---------------------------------------------------------------------------------------------------------------

This motivated me to experiment with completely alternative approaches and eventually led to the creation of [X-Pipe](https://github.com/xpipe-io/xpipe). Instead of implementing all this protocol handling through libraries, the alternative approach of X-Pipe is to delegate everything to existing command-line programs. If the user has already installed the appropriate programs to connect to remote systems, why not try to use them instead of libraries?

Essentially, we can first create a headless local shell process using the system shell, e.g. `Runtime.getRuntime().exec(new String[] {"cmd"})` or `Runtime.getRuntime().exec(new String[] {"sh"})`. We then write all input, such as commands to be executed and optional stdin content for the commands into the local shell process stdin while also reading the stdout and stderr to obtain the outputs of the run commands.

When a command executed in this local shell process opens a remote shell such as `ssh user@host`, this process is effectively turned into a remote shell connection as all input and output are just forwarded to the remote shell.

When we connect to a remote shell that way, X-Pipe first detects what kind of server and environment, e.g. shell type, os, etc. we have logged into and adjusts how X-Pipe talks to the remote system from there. This is accomplished by executing certain probing commands to first figure out what shell we are in and from there check for the existence of certain programs and environment variables.

We can then execute arbitrary commands in that shell by writing them line-wise into the stdin, adjusting them based on the determined OS and shell type.

There are several advantages of such an approach:

* You are easily able to access any containers like docker, LXC, WSL, and more via their respective CLI tools by opening a shell with for example `docker exec -i sh` and its equivalents
* It integrates with your existing setup that you normally use to connect via the CLI. That means it can automatically use your SSH configs, SSH agent key stores, and more
* It doesn't require any additional setup at all on the remote system that you want to connect, you flexibly can use whatever connection method is already there
* Shell connections can also be nested by first logging into the first remote shell, then into another one from there, and so on ...

Some people might argue that this is not the intended way to use many of these command-line tools as they are designed to be used interactively in a terminal.

This is correct to some degree, however, most tools are designed with the usage in non-interactive shell scripts and dumb terminals in mind, so we are not using them in a completely unintended way.

Overall they work fine for the purposes of this implementation.

### Initial challenges {#h3-1-initial-challenges}

For such an approach to work out however in the end, there are many challenges to be considered:

* Shells in general are very heterogeneous, especially between operating systems. While the differences between `sh` and `bash` are marginal, shells like `cmd` and `bash` behave in wildly different ways, so we have to accommodate for that. We also need a reliable way to detect which shell we are landed in first before we can execute any meaningful commands.
* The available commands and programs wildly differ between different operating systems and shells with built-ins. We need to work with the least common denominator as we don't want to install any additional packages or applications. Especially bare docker containers are a challenge here.
* How to handle permissions, elevation, and passwords? Many shells and programs require at least at pty to be present to allow for a password prompt. X-Pipe must be able to fill passwords without any ptys or pseudo-consoles.
* As the shell processes are running on remote systems, it is way harder to detect their current state:
  * Is the process still running?
  * What exactly is the state of stdin, stdout, stderr? Where exactly does the output of the first command end and the second one start?
  * How to handle errors? There might be connection errors, command syntax errors, command execution errors, and more.

All of these challenges have finally been solved now, making the implementation ready to be used.

### The remote process API {#h3-2-the-remote-process-api}

The usage of the remote process API is pretty straightforward. First, you define a connection store which holds all information about how to establish a shell connection like this:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Creates a local machine shell connection that starts up
// your default system shell like cmd, sh, zsh, etc.
LocalStore local = new LocalStore();

// An ssh connection starting from the local machine
SshStore remoteSsh = new SshStore(local,
        "&lt;host&gt;", &lt;port&gt;, "&lt;user&gt;", &lt;password&gt;, &lt;key-based auth setting&gt;);

// A shell connection to a docker container running on
// the remote system that we connected to via SSH.
// This connection will be established with docker exec -i "&lt;container name&gt;" sh
DockerStore docker = new DockerStore(remoteSsh, "&lt;container name&gt;");
</pre>

The next step is to create and start a remote shell control. This object will handle everything related to the shell connection:

<pre class="EnlighterJSRAW" data-enlighter-language="java">// Start a shell control from the docker connection store
try (ShellControl sc = docker.control().start()) {
    // Once we are here, the shell connection is initialized and we can query all kinds of information

    // Query the detected shell dialect, e.g. cmd, powershell, sh, bash, etc.
    System.out.println(sc.getShellDialect());

    // Query the os type
    System.out.println(sc.getOsType());

    // Simple commands can be executed in one line
    // The shell dialects also provide the appropriate commands
    // for common operations like echo for all supported shells
    String echoOut = sc.executeSimpleStringCommand(sc.getShellDialect().getEchoCommand("hello!", false));

    // You can also implement custom handling for more complex commands
    try (CommandControl cc = sc.command("ls").start()) {
        // Discard stderr
        cc.discardErr();

        // Read the stdout lines as a stream
        BufferedReader reader = new BufferedReader(new InputStreamReader(cc.getStdout(), cc.getCharset()));
        // We don't have to close this stream here, that will be
        // automatically done by the command control after the try-with block
        reader.lines().filter(s -&gt; !s.isBlank()).forEach(s -&gt; {
            System.out.println(s);
        });

        // Waits for command completion and returns exit code
        if (cc.getExitCode() != 0) {
            // Handle failure
        }
    }

    // Commands can also be more complex and span multiple lines.
    // In this case, X-Pipe will internally write a command to a script file and then execute the script
    try (CommandControl cc = sc.command(
        """
        VAR="value"
        echo "$VAR"
        """
        ).start()) {
        // Reads stdout, stashes stderr. If the exit code is not 0,
        // it will throw an exception with the stderr contents.
        var output = cc.readOrThrow();
    }

    // More customization options
    // If the command should be run as root, the command will be executed with
    // sudo and the optional sudo password automatically provided by X-Pipe
    // by using the information from the connection store.
    // You can also set a custom working directory.
    try (CommandControl cc = sc.command("kill &lt;pid&gt;").elevated().workingDirectory("/").start()) {
        // Discard any output but throw an exception with the stderr contents if the exit code is not 0
        cc.discardOrThrow();
    }

    // Start a bash sub shell. Useful if the login shell is different
    try (ShellControl bash = sc.subShell(ShellDialects.BASH).start()) {
        // Let's write to a file
        try (CommandControl cc = bash.command("cat &gt; myfile.txt").start()) {
            // Writing into stdin can also easily be done
            cc.getStdin().write("my file content".getBytes(cc.getCharset()));
            // Close stdin to send EOF. It will be reopened by the shell control after the command is done
            cc.closeStdin();
        }

        // Kill the local process and the remote shell connection with it
        // This can be useful in case a command is stuck or we have to abruptly stop execution
        bash.kill();
    }
}
</pre>

This is just a brief showcase, there's more that you can do with it.

Building the X-Pipe desktop application {#h2-3-building-the-x-pipe-desktop-application}
---------------------------------------------------------------------------------------

After the core functionality had been implemented, the next step on my TODO list was to apply it to something practical. The result of that is the X-Pipe desktop application, which is entirely built on top of the remote process control implementation and was created with JavaFX.

The goal was to create a handy tool for people who work a lot with remote shell connections that is able to connect to anything due to its completely different remote process handling.

### Connection management {#h3-4-connection-management}

The initial work went into a remote connection management feature that allows you to organize all connection stores in one place. There you can create new shell connections, edit existing ones, open them in your terminal, and perform a variety of different actions on your saved connections.

Any stored sensitive login information is encrypted and can also be locked behind a master password, similar to password managers.

![Connection manager](https://user-images.githubusercontent.com/72509152/230098966-000596ca-8167-4cb8-8ada-f6b3a7d482e2.png)

### Remote file management {#h3-5-remote-file-management}

There's just something satisfying about using graphical file managers. Even many terminal diehards would agree that dragging and dropping files around, multi-file selection with your mouse, context menus, and more can provide a very enjoyable user experience. When you work in environments where there's no support for such a graphical interface, only then you realize what a step-up it is compared to terminals.

For this reason, there are already plenty of established remote file managers out there, e.g. [WinSCP](https://winscp.net), [Termius](https://termius.com/), [RoyalTS](https://www.royalapps.com/ts/win/features), [Shell NGN](https://shellngn.com/). There are even some written in Java like [Muon / Snowflake](https://github.com/subhra74/snowflake). However, they all share the same limitations of protocol-based solutions mentioned earlier, so we can definitely improve upon that.

So why not take our remote process handling implementation and also try to apply it to file management? By using file system related commands such as `ls`, `rm`, `touch`, etc. and its equivalents, we can realize a functional file manager that can connect to essentially every system and doesn't require any setup. This is exactly what you can see in action here:

![Remote file explorer](https://user-images.githubusercontent.com/72509152/230100929-4476f76c-ea81-43d9-ac4a-b3b02df2334e.png)

### Integrating with the user's toolbox {#h3-6-integrating-with-the-user-s-toolbox}

It is important for proper file managers provide terminal and editor functionalities. The general approach that all existing comparable projects share is to include all necessary tools via libraries in their application. So to add terminal support, they include a terminal library like [Jediterm](https://github.com/JetBrains/jediterm) for Java that allows them to handle and display a terminal window in the application itself. Alternatively they bundle or require a fixed terminal program like [PuTTY](https://www.putty.org/) on Windows. The same is often done for text editing, protocol support, and more.

Such an approach leads to a high development workload as properly integrating all these libraries isn't simple. It is also not very satisfying for the user being forced to work with a different terminal than they would normally want to use as their workflows, shortcuts, configuration, and more can't be applied to such an application-integrated terminal

To make the life of everyone involved easier, X-Pipe also follows the delegation approach here. It delegates everything, from terminal support, over text editing, and more to the user's favorite tools. As a result, it does not require any libraries at all and doesn't ship with a library for terminal support, it just calls the terminal executable the user wants to use.

The remote process implementation is used to set up script files such that a local terminal can automatically establish a remote connection without any user input. Such an approach to integration comes of course with its own challenges and requires a different architecture from the ground up but comes with a number of advantages:

* It speeds up development
* It can easily be extended to include support for new tools. Adding support for a [new terminal](https://github.com/xpipe-io/xpipex/blob/8033d24654967d04a20939b8c4bf014d9dd5d99b/app/src/main/java/io/xpipe/app/prefs/ExternalTerminalType.java) or [text editor](https://github.com/xpipe-io/xpipex/blob/8033d24654967d04a20939b8c4bf014d9dd5d99b/app/src/main/java/io/xpipe/app/prefs/ExternalEditorType.java) takes around 15 LOC.
* The user can happily work with their tools that they're familiar with

Architecture {#h2-7-architecture}
---------------------------------

The project is fully realized in modern Java and currently targets Java 19, with the focus lying on taking the necessary time in order to achieve a proper implementation (One of the nice things when there aren't tight deadlines).

### JavaFX {#h3-8-javafx}

The desktop application is created with JavaFX plus various libraries and some custom controls while styling is done with [AtlantaFX](https://github.com/mkpaz/atlantafx) in order to achieve a consistent and modern look. User settings are entirely handled with [PreferencesFX](https://github.com/dlsc-software-consulting-gmbh/PreferencesFX) using a custom skin and layout.

In-app markdown displays for documentation, changelogs, and other dialogs are rendered through the JavaFX [WebView](https://openjfx.io/javadoc/17/javafx.web/javafx/scene/web/WebView.html), [Flexmark](https://github.com/vsch/flexmark-java) to convert markdown to html, and [GitHub Markdown CSS](https://github.com/sindresorhus/github-markdown-css). The file browser icons are sourced from the [vscode-icons](https://github.com/vscode-icons/vscode-icons) project. As they are in the `.svg` format, they produce a crisper look and avoid any scaling issues. SVG support is implemented through custom controls that utilize the JavaFX WebView and augment it with some adaptive rendering and caching.

### Operation modes {#h3-9-operation-modes}

The application is designed as a daemon that is only started when required, similar to for example the gradle daemon. There are three operation modes that it can switch back and forth between: background, tray, and GUI.

The background operation mode is useful to run an application minimized without a supported application tray, while the tray and GUI mode can be used on more normal systems. Such an approach also allows the daemon to start up faster when no immediate JavaFX platform is required as all other resources like images, translations, and more also do not have to be loaded when starting in background mode.

For example, if a user uses a desktop shortcut that will launch a remote shell connection via X-Pipe in a terminal window, then this operation does not require the JavaFX platform to be initialized and the daemon starts up in the background if it is not running yet. When at a later date the user also wants to access the GUI, the existing daemon will just start up the JavaFX a platform dynamically. At runtime, the daemon can also switch between any operation mode if the user wants it to.

### Error handling {#h3-10-error-handling}

X-Pipe also comes with its own integrated error handler. Especially for desktop applications it is important to have a reliable error handling that gives the user the ability to inspect an error and to choose a response.

Whenever an error event occurs that should be displayed, the error handler alert is displayed which gives the user different options to respond to this error. Here the user has also the ability to send an issue report, include some written user feedback, and select file attachments such as log files for further diagnosis in case they want to do so.

The error handler implementation is designed to be very robust to handle cases such as when other errors occur during error handling in the same or other threads. There's also a special focus on handling startup error events, i.e. ones where the application can't start up, that occur before the JavaFX application can be fully initialized by using a fallback JavaFX startup routine to still display an error dialog.

### Distribution {#h3-11-distribution}

Distributable packages for every operating system are created with [jpackage](https://docs.oracle.com/en/java/javase/17/docs/specs/man/jpackage.html) while native installers are created manually through scripts and gradle plugins due to the need for extensive customization that is not covered by jpackage. They are then published to GitHub and some package managers using the [JReleaser](https://jreleaser.org).

Users are given the option here to make use of automatic updates, which is especially important in the early stage of a project where fixes and improvements are frequently shipped. X-Pipe will, when a newer version update is detected on GitHub, allow you to download and automatically install it. In order to accommodate all different types of distribution, e.g. portable archive, native installer, or package manager, it detects at runtime which distribution type the user is running and adapts its update process to it.

When using the native installer, the newer version can be automatically installed with one click from the application in the background. For package managers, the installation is performed in a newly opened terminal session in which the required update command is prepared to be run by the user.

### Embracing modularity {#h3-12-embracing-modularity}

The Java Platform Module System (JPMS) has received a lot of hate over the years for making things seemingly unnecessarily complicated.

For the purposes of an application like X-Pipe however, it is a perfect fit. The X-Pipe platform itself is designed to be fully modular to allow for an easy creation of plugins and extensions. The entire project is fully modularized, allowing for the usage of the new JDK APIs designed for module loading at runtime like [ModuleLayers](https://docs.oracle.com/en/java/javase/18/docs/api/java.base/java/lang/ModuleLayer.html) and [ModuleFinders](https://download.java.net/java/early_access/panama/docs/api/java.base/java/lang/module/ModuleFinder.html) and enabling anyone to easily implement their custom functionality within X-Pipe.

Each extension module in X-Pipe is just a modularized jar that is loaded into a separate module layer and provides services using the [ServiceLoader](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/util/ServiceLoader.html) mechanism to be used as entry points for initializing the extension. Any contained resources like translations or images are automatically loaded with [ModuleFS](https://github.com/xpipe-io/modulefs) and any contained native libraries are automatically put into the library path.

Required dependencies can be placed next to the extension jar, the module finder will pick them up automatically and add them to the layer. One extension can also depend on others and use their module layers as its parents.

Conclusion {#h2-13-conclusion}
------------------------------

I recently decided to switch to the Apache 2.0 license for this project and am still working open sourcing the last parts of the remote process implementation. Currently, the remote process API implementation and the X-Pipe application are coupled together somewhat. It is however possible in theory to separate them and I have plans to do that in the near future if there's any interest.

Some initial experimental work is going on to support SQL shells like the PSQL shell so that in the future you might be able to interact with databases through remote SQL shell processes instead of something like JDBC, at least to some degree.

There's also still a lot of work going on making the shell handling implementation more robust. The main challenge is that there are many completely different systems running out there in the real world. This can range from obscure shells running on a 10 year old Linux system to a modern Windows 11 installation. As ideally the remote process implementation should be compatible with all operating systems and shells, a lot of work has to be spent experimenting and testing on many environments.

So if X-Pipe sounds interesting to you, you can give it [a try](https://github.com/xpipe-io/xpipe/releases)! The project is in need of brave early users that can weather through initial issues, report bugs, and provide feedback to guide me in the right development direction.
