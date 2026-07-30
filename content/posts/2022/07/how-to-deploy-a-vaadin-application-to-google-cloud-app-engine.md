---
title: "How to deploy a Vaadin application to Google Cloud App Engine"
slug: "how-to-deploy-a-vaadin-application-to-google-cloud-app-engine"
date: "2022-07-26T08:27:20+00:00"
lastmod: "2022-10-10T08:53:15+00:00"
description: "Deploying to Google App Engine is straight-forward using the Maven plugin, but you must analyze the log files and probably configure the Java version, the instance size, and session affinity."
authors:
  - "simon-martinelli"
image: "https://foojay.io/wp-content/uploads/2022/07/appenginedemo.png"
categories:
  - "Cloud"
  - "Vaadin"
tags:
related_posts:
enlighterjs: true
frozen: false
---

I tried to deploy a Vaadin application to Google Cloud App Engine.

It was not as straightforward as expected, so I want to share my findings.

The example project is available on GitHub: <https://github.com/simasch/vaadin-appengine-demo>

The Vaadin Application {#h2-0-the-vaadin-application}
-----------------------------------------------------

First, I've created a new Vaadin application: <https://start.vaadin.com>

Then I did a production build:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mvn package -Pproduction</pre>

That will generate an executable JAR file in the target directory.

First Deployment {#h2-1-first-deployment}
-----------------------------------------

Google AppEngine provides F2 instance type with enough memory for Spring Boot and Java 17 by default. There is no need to configure anything, and I can simply execute:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">gcloud app deploy</pre>

This work! But is there even a simpler way?

Maven Plugin {#h2-2-maven-plugin}
---------------------------------

I could use a Maven plugin to deploy the application:

<pre class="EnlighterJSRAW" data-enlighter-language="xml" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;plugin&gt;
    &lt;groupId&gt;com.google.cloud.tools&lt;/groupId&gt;
    &lt;artifactId&gt;appengine-maven-plugin&lt;/artifactId&gt;
    &lt;version&gt;2.4.2&lt;/version&gt;
    &lt;configuration&gt;
        &lt;projectId&gt;GCLOUD_CONFIG&lt;/projectId&gt;
        &lt;version&gt;GCLOUD_CONFIG&lt;/version&gt;
    &lt;/configuration&gt;
&lt;/plugin&gt;</pre>

And now I just have to call:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">mvn package appengine:deploy -Pproduction</pre>

Session State {#h2-3-session-state}
-----------------------------------

As you may know, Vaadin applications have server state and may also have push enabled that will use WebSockets.

We must enable session affinity to ensure our application runs correctly in multiple instances.

This can be done in the app.yaml as well

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">network:
    session_affinity: true</pre>

But what does session affinity mean? Let's check the documentation:

***session_affinity***

*Optional. Set to true to configure App Engine to route multiple sequential requests for a given user to the same App Engine instance such as when storing user data locally during a session.*

*Session affinity enables inspecting the value of a cookie to identify multiple requests by the same user and then directs all such requests to the same instance. If the instance is rebooted, unhealthy, overloaded or becomes unavailable when the number of instances has been scaled down, session affinity will be broken and further requests are then routed to a different instance.*

*Note that enabling session affinity can affect your load balancing setup. This parameter is disabled by default.*

Conclusion {#h2-4-conclusion}
-----------------------------

Deploying to Google App Engine is straight-forwarded using the Maven plugin, but you must analyze the log files and probably configure the Java version, the instance size, and session affinity.
