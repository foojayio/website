---
title: "How to Create APIs with the Java 18 jwebserver Ready for Docker "
slug: "how-to-create-apis-with-the-java-18-jwebserver-ready-for-docker"
date: "2022-04-11T14:26:57+00:00"
lastmod: "2022-04-11T14:26:59+00:00"
description: "When simple web content or responses are required, Java 18 reduces the need to search for more complicated solutions like Jetty and Netty."
authors:
  - "miro-wengner"
image: "/images/posts/2022/04/how-to-create-apis-with-the-java-18-jwebserver-ready-for-docker/Image1-700x330.png"
categories:
  - "Java Core"
tags:
related_posts:
  - "transitioning-to-java-my-first-book"
  - "journeys-in-java-level-9-docker-compose-all-the-things"
  - "virtual-thread-pinning-field-guide"
  - "boxlang-1-15-0-released-blazing-fast-strings-runtime-portability-and-much-more"
enlighterjs: true
frozen: false
---

In the age of cloud computing, it is at times very helpful to have the ability to run a very simple web server. A simple web server that delivers static content.

Maybe some of us are already familiar with the neat python util "*SimpleHTTPServer*" (Example 1.). It allows you to run a web server from the current location and obtain access to static content.

Although such a server is very simple and even without the ability to define a directory, it is very helpful in many cases. For example, exposing a simple end-point that serves a JSON response:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ python -m SimpleHTTPServer 9999&nbsp;</pre>

***Example 1.**: Executing simple python web-server in the current directory to serve a content*

This was not possible in Java out of the box. Luckily this has changed with Java 18. It came with similar features as Python through JEP-408 (Reference 2.). Yes, a Simple Web Server.

The goal of the article is to provide a closer look at the associated "*jwebserver*" features and not only in dockerized environments.

Let's begin!

How to start from the command-line...
![](/images/posts/2022/04/how-to-create-apis-with-the-java-18-jwebserver-ready-for-docker/Image1-700x330.png)

***Image 1.**: "jwebserver" delivers context, using the code below*

The simplest way to start "*jwebserver*" is via the command-line.

Compared to the Python version (Example 1.) "*jwebserver*" provides a couple of options that allow you to customise the default server settings, a bit.

The most obvious option is that the server allows you to specify a served directory (Example 2.) and a couple of other options as seen in Example 3. The directory contains the file *"index.html*" which is used as the default entry point (Image 1.)

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">├── get_simple_1.json
├── get_simple_2.json
├── images
│ &nbsp; └── OpenJDK_logo.png
└── index.html</pre>

***Example 2.**: Directory structure for the command-line usage*

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ jwebserver -b 0.0.0.0 -p 8880 -d &lt;PROJECT_PATH&gt;/http-static -o info 

Output: 
&lt;PROJECT_PATH&gt;/http-static and subdirectories on 0.0.0.0 (all interfaces) port 8880 URL http://&lt;IP_ADDRESS&gt;:8880/ </pre>

***Example 3.**: Command to start the server with options "-b" for binding interfaces, "-p" specified port, "-d" served directory or "-o" logging level none,info,verbose*

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ curl -X GET http://localhost:8880
&lt;!DOCTYPE html&gt;
&lt;html&gt;
&lt;body&gt;</pre>

***Example 4.**: http GET method request*

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ curl --head http://localhost:8880
HTTP/1.1 200 OK
Date: Fri, 08 Apr 2022 17:29:37 GMT
Last-modified: Thu, 7 Apr 2022 21:31:11 GMT
Content-type: text/html
Content-length: 465</pre>

***Example 5.**: http head request*

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ curl -X GET -I http://localhost:8880/get_simple_1.json
HTTP/1.1 200 OK
Date: Fri, 08 Apr 2022 17:43:08 GMT
Last-modified: Fri, 8 Apr 2022 14:08:42 GMT
Content-type: application/json
Content-length: 50</pre>

***Example 6**.: http method GET* *with JSON response*

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ curl -X POST -I http://localhost:8880
HTTP/1.1 405 Method Not Allowed
Date: Fri, 08 Apr 2022 17:39:21 GMT
Allow: HEAD, GET
Content-length: 0</pre>

***Example 7.**: Not supported command line methods POST, PUT, DELETE etc.*

A nice feature presents itself! The "*jwebserver*" serves static content and it allows to continually modify served content. Isn't it cool? Yes, it is.

There are some limitations to command-line usage, let us highlight some of them:

* HTTP methods **GET** and **HEAD** are allowed
* HTTP/1.1 supported, no HTTPS
* HTML files served as "*Content-type: text/html*"
* JSON files served as "*Content-type: application/json*"
* Default logging is set to INFO, option "**-o**" (Example 3.)
* Other HTTP methods result in 405 - Method Not Allowed state (Example 7.)

<br />

It may be useful to point out that the "*jwebserver* " command is the wrapper to the Java executable "*main()* " method of the class "*sun.net.httpserver.simpleserver.Main* ". The Java class is resides in the module "*jdk.httpserver*".

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ java -m jdk.httpserver&nbsp; -b 0.0.0.0 -p 8880 -d &lt;PROJECT_PATH&gt;/http-static -o verbose</pre>

***Example 8.**: Using Java to execute the "jdk.httpserver" default module method "main" with options similar to Example 3.*

Let us explore a bit the programmatic possibilities as command-line ones may already be pretty useful, IMHO.

Starting "*SimpleFileServer*" Programmatically {#h2-0-starting-simplefileserver-programmatically}
-------------------------------------------------------------------------------------------------

The class "*SimpleFileServer* " resides in the module "*com.sun.net.httpserver*".

For purposes of this article, we create a new class "*WebServerSimpleMain*" and make it executable by defining a "main" method.

Our journey starts with basic programmatic server initiation.

### Simple server serves a directory {#h3-1-simple-server-serves-a-directory}

The example shows how to create a server with an absolute folder path (Example 8.). This path is used as the source for the served files and its subdirectories (Example 9.).

It means that the started server has access to the all available content inside the initiated server context. The folder does not contain the "*index.html*" file which implies that the content is printed out (Image 2.)

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">├── get
│ &nbsp; └── get_request.json
├── get_simple.json
└── post
 &nbsp;&nbsp;&nbsp;└── post_request.json</pre>

***Example 9.**: "http-static" folder structure*

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">var servedPath = Paths.get("http-static").toAbsolutePath();
var server = SimpleFileServer.createFileServer(
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;new InetSocketAddress(SIMPLE_SERVER_PORT), servedPath, OutputLevel.VERBOSE);
server.start();</pre>

***Example 10.**: Programmatic simple file server initiation and start*

![](/images/posts/2022/04/how-to-create-apis-with-the-java-18-jwebserver-ready-for-docker/Image2.png)

***Image 2.**: Default configuration print the folder structure*

### Adding request handlers and filters to HttpServer {#h3-2-adding-request-handlers-and-filters-to-httpserver}

The JEP-408 (Reference 2.) mentions that the class "*SimpleFileServer* ", added in Java 18, is based on the already existing "*HttpServer"* class that has been present since Java 1.6. In this section we explore its underlying components "*HttpServer* ", "*HttpHandler* " and "*OutputFilter*"

Let us consider an example where we want to create a web-server. Such a web-server allows the methods "*GET* ", "*POST* " d (Example 10.). In case of disallowed methods it returns a message "*Sorry, not allowed method*".

In this case, we need to extend the previously mentioned three components. For each request type we also define its own file that holds the response (Example 8.).

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">...
//GET, POST Handlers with the content and predicates
var getHandler = HttpHandlers.of(200, jsonHeaders, Files.readString(getResponsePath));
var postHandler = HttpHandlers.of(200, jsonHeaders, Files.readString(postResponsePath));
Predicate&lt;Request&gt; IS_GET = r -&gt; r.getRequestMethod().equals("GET");
Predicate&lt;Request&gt; IS_POST = r -&gt; r.getRequestMethod().equals("POST");
var notAllowedHandler = HttpHandlers.of(405, 
     Headers.of("Access-Control-Allow-Methods", "GET,POST"), 
    "Sorry, not allowed method");

var h1 = HttpHandlers.handleOrElse(IS_GET, getHandler, notAllowedHandler);
var h2 = HttpHandlers.handleOrElse(IS_POST, postHandler, h1);
var server = HttpServer.create(new InetSocketAddress(HANDLER_SERVER_PORT), 2,
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"/", h2, SimpleFileServer.createOutputFilter(System.out, OutputLevel.INFO));
server.start();
...</pre>

***Example 11.**: Starting HttpServer with custom Handlers and output Filters*

A closer look at the custom handlers (Example 10., *getHandler,h1, postHandler,h2,* notAllowedHandler) shows their concatenation.

Running in Docker {#h2-3-running-in-docker}
-------------------------------------------

The "*jwebserver* " can be pretty handy as it allows to execute it just as a command "*$ docker run*" (Example 10.) or create Docker containers with a mounted directory that can be used in a docker-compose file (Example 11.).

The mounted directory content can be continually updated based on your needs (adding, updating, removing static files).

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">$ docker run&nbsp; -p 8000:8000 -t -i -v &lt;PROJECT_PATH&gt;/http-static:/http-static -w /http-static openjdk:18-jdk-slim jwebserver -b 0.0.0.0</pre>

***Example 12.**: running command line "jwebserver" version from the docker image with mounted directory*

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">...
jserver_one:
 &nbsp;&nbsp;image: jdk18-slim-jwebserver:latest
 &nbsp;&nbsp;restart: always
 &nbsp;&nbsp;ports:
 &nbsp;&nbsp;&nbsp;&nbsp;- "8000:8000"
 jserver_two:
 &nbsp;&nbsp;image: openjdk:18-jdk-slim
 &nbsp;&nbsp;restart: always
 &nbsp;&nbsp;volumes:
 &nbsp;&nbsp;&nbsp;&nbsp;- ./http-static:/http-static
 &nbsp;&nbsp;ports:
 &nbsp;&nbsp;&nbsp;&nbsp;- "8001:8001"
...</pre>

***Example 13.**: docker-compose file snipped, created image by Dockerfile(Example 12.) and mounting directory*

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">FROM openjdk:18-jdk-slim
RUN mkdir /http-static
RUN mkdir /http-static/images
COPY http-static/ /http-static/
WORKDIR /http-static
CMD ["sh", "-c", "jwebserver -b 0.0.0.0"]</pre>

***Example 14.**: Dockerfile snippet*

Conclusion {#h2-4-conclusion}
-----------------------------

The newly added "*jwebserver*" is a pretty neat feature.

The mentioned examples above show how easy it is to configure different approaches and even start them as containers.

The "*jwebserver*" comes with Java 18.

It is a hot candidate for cases where only simple content or responses are required and reduces the necessity to search for more complicated solutions like Jetty and Netty.

References {#h2-5-references}
-----------------------------

1. [GitHub Project, JVM-Lanuage-Examples](https://github.com/mirage22/jvm-language-examples) : https://github.com/mirage22/jvm-language-examples
2. [JEP 408: Simple Web Server](https://openjdk.java.net/jeps/408): https://openjdk.java.net/jeps/408

```
 
```
