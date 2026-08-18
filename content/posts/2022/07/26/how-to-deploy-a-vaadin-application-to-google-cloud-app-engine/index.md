---
title: "How to deploy a Vaadin application to Google Cloud App Engine"
slug: "how-to-deploy-a-vaadin-application-to-google-cloud-app-engine"
date: "2022-07-26T08:27:20+00:00"
lastmod: "2022-10-10T08:53:15+00:00"
description: "Deploying to Google App Engine is straight-forward using the Maven plugin, but you must analyze the log files and probably configure the Java version, the instance size, and session affinity."
authors:
  - "simon-martinelli"
image: "appenginedemo.png"
categories:
  - "Cloud"
  - "Vaadin"
tags:
related_posts:
  - "blink-a-led-on-raspberry-pi-with-vaadin"
  - "build-web-apps-in-pure-java-with-vaadin-flow"
  - "hilla-1-0-a-new-frontend-framework-for-springboot"
frozen: false
---

I tried to deploy a Vaadin application to Google Cloud App Engine.

It was not as straightforward as expected, so I want to share my findings.

The example project is available on GitHub: <https://github.com/simasch/vaadin-appengine-demo>

## The Vaadin Application

First, I've created a new Vaadin application: <https://start.vaadin.com>

Then I did a production build:

```
mvn package -Pproduction
```

That will generate an executable JAR file in the target directory.

## First Deployment

Google AppEngine provides F2 instance type with enough memory for Spring Boot and Java 17 by default. There is no need to configure anything, and I can simply execute:

```
gcloud app deploy
```

This work! But is there even a simpler way?

## Maven Plugin

I could use a Maven plugin to deploy the application:

```xml
<plugin>
    <groupId>com.google.cloud.tools</groupId>
    <artifactId>appengine-maven-plugin</artifactId>
    <version>2.4.2</version>
    <configuration>
        <projectId>GCLOUD_CONFIG</projectId>
        <version>GCLOUD_CONFIG</version>
    </configuration>
</plugin>
```

And now I just have to call:

```
mvn package appengine:deploy -Pproduction
```

## Session State

As you may know, Vaadin applications have server state and may also have push enabled that will use WebSockets.

We must enable session affinity to ensure our application runs correctly in multiple instances.

This can be done in the app.yaml as well

```
network:
    session_affinity: true
```

But what does session affinity mean? Let's check the documentation:

***session_affinity***

*Optional. Set to true to configure App Engine to route multiple sequential requests for a given user to the same App Engine instance such as when storing user data locally during a session.*

*Session affinity enables inspecting the value of a cookie to identify multiple requests by the same user and then directs all such requests to the same instance. If the instance is rebooted, unhealthy, overloaded or becomes unavailable when the number of instances has been scaled down, session affinity will be broken and further requests are then routed to a different instance.*

*Note that enabling session affinity can affect your load balancing setup. This parameter is disabled by default.*

## Conclusion

Deploying to Google App Engine is straight-forwarded using the Maven plugin, but you must analyze the log files and probably configure the Java version, the instance size, and session affinity.
