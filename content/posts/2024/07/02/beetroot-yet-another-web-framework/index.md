---
title: "beetRoot: Yet Another Web Framework?"
date: "2024-07-02T07:26:01+00:00"
lastmod: "2025-12-08T22:31:32+00:00"
description: "beetRoot is a slim and rapid Java web development as well as a full & secure client-server framework ready to run."
authors:
  - "michael-gasche"
image: "https://raw.githubusercontent.com/autumoswitzerland/autumo-beetroot/master/web/img/autumo-beetroot-login.webp"
categories:
  - "Developer Tools"
  - "Release Notes"
tags:
related_posts:
  - "building-javafx-with-gradle"
  - "getting-started-with-rife2-java-web-framework-v1-0-0"
  - "creating-executables-for-javafx-applications"
frozen: false
---

*News: beetRoot 3.2.0 was released on December 8, 2025*.

Over the years, I have struggled with various Java web development frameworks and they all have their weaknesses and strengths.

A few years ago, I evaluated around 10 of these frameworks for a project for a Swedish company. Nothing really impressed me at the time.

It was only later that [SpringBoot](https://spring.io/projects/spring-boot) came along, which seemed to fulfill all the requirements and there was actually very little to criticize about it. Then came microservices such as [Quarkus](https://quarkus.io/) or [Micronaut](https://micronaut.io/), which are often compiled into native code for small web service applications, but which can then rarely be embedded in a more mature architecture.
![beetRoot starting on console](https://raw.githubusercontent.com/autumoswitzerland/autumo-beetroot/master/web/img/autumo-beetroot-login.webp)

## The "Buts"

Something that has always bothered me is that with full-blown frameworks, the configuration layer of the components is too opaque and complicated and the web microservices don't really offer much out of the box.

So what I want is a fully-fledged, high-performance Java web development framework like those found in the PHP world (e.g. [CakePHP](https://cakephp.org/), [Laravel](https://laravel.com/), etc.) and which already comes with ready-to-use components such as [CSRF](https://owasp.org/www-community/attacks/csrf) prevention, login with [2FA](https://dictionary.cambridge.org/dictionary/english/2fa) authentication, [CRUD](https://medium.com/geekculture/crud-operations-explained-2a44096e9c88) generator (based on the database model), compliant with other web containers such as [Tomcat](https://tomcat.apache.org/), [Jetty](https://jetty.org/index.html) and [Oracle WebLogic](https://www.oracle.com/java/weblogic/) as well as executable as a standalone web server, a simple web template engine, standard CRUD views and functions with automatic mapping of referential database integrities on the web masks, theme support, fallback mechanisms when loading web resources as well as full language management and some more features.

## What is beetRoot?

beetRoot is a slim and rapid Java web development as well as a full \& secure client-server framework ready to run.

It starts faster than a second and you get a set of working dependencies with the initial setup for the current release, a transparent and clear way to configure the framework and its components and the freedom to choose any web container or simply use the optimized and embedded web container from the start.

However, none of this prevents you from customizing the dependencies in the Maven **pom.xml** file itself.

The quick start setup is also effortless and very fast!

If you know CakePHP for web development, you'll like beetRoot. It is based on the same principles and comes with a full CRUD generator generating all views, the model specification and controllers (handlers in beetRoot's terminology) based on the database model! The client-server framework supports encrypted communication (SSL) as well as HTTP/HTTPS-tunneling, provides a file download and upload interface and it can be extended with own (distributed) modules.
![beetRoot website after login](https://raw.githubusercontent.com/autumoswitzerland/autumo-beetroot/master/web/img/autumo-beetroot-screen.webp)

autumo beetRoot is open source ([Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)) and is now available in version 3.1.5. It also largely forms the basis for the product [autumo ifaceX](https://products.autumo.ch/ifacex/overview).

## How to QuickStart?

### Linux, macOS

Enter the following statements into your UNIX terminal:

```bash
VERSION=3.2.0
PACKAGE=autumo-beetRoot-$VERSION

curl -LO https://github.com/autumoswitzerland/autumo-beetroot/releases/download/v$VERSION/$PACKAGE.zip

unzip $PACKAGE.zip
rm $PACKAGE.zip

$PACKAGE/bin/beetroot.sh start
```

**Windows**

Enter the following statements into your Windows terminal or PowerShell:

```batch
SET VERSION=3.2.0
SET PACKAGE=autumo-beetRoot-%VERSION%

curl -LO https://github.com/autumoswitzerland/autumo-beetroot/releases/download/v%VERSION%/%PACKAGE%.zip

tar -xf %PACKAGE%.zip
del %PACKAGE%.zip

%PACKAGE%\bin\beetroot.bat start
```

autumo beetRoot starting up:
![beetRoot starting on console](https://raw.githubusercontent.com/autumoswitzerland/autumo-beetroot/master/web/img/autumo-beetroot-console.webp)

Now go to <http://localhost:8778> and log in:

* **Default user**: admin
* **Default password** : beetroot   

## QuickStart Video

Take a look at the QuickStart Video if you want to play around with the framework.
[![Quickstart Video](https://i3.ytimg.com/vi/X2_FVYiMnIE/hqdefault.jpg)](https://youtu.be/X2_FVYiMnIE)

## Full Feature List

The Web framework is shipped with the following features ready to use:

* Language management
* File up- and download
* Full MIME types control
* 2-Factor-Authentication
* Extendable user settings
* Password reset mechanism
* SMS and phone call interfaces
* Dark theme and theme support
* Mailing inclusive mail templates
* URL routing with language support
* Argon2/PBKPD2 password encryption
* Fulfills the Jakarta EE 11 specification
* Easy to understand HTML template engine
* File caching (resources and templates)
* Export tables in CSV, JSON, or XLSX format
* HTTPS protocol and TLS for mail if configured
* Bean support with transient and unique fields
* User sessions are stored when servers are stopped
* Entities can be served through the JSON REST API
* Logging implementations other than log4j2 supported
* Add, edit, view, list and delete functionality for entities
* Full CRUD-Generator **PLANT** for views, models and handlers
* Optimized console logging with colored sections (if required)
* One-to-many database relationships are fully applied in MVC layers
* User roles \& access control on controller level and within templates
* Tested on Apache Tomcat 10 \& 11, Eclipse Jetty 12.1 and Oracle Weblogic 15.1
* Standard CSRF mechanism as well as obfuscated CRUD IDs within HTTP requests
* Database connection pooling (HikariCP, with internal and external JNDI data sources)
* Runs stand-alone as well as in common servlet containers such as Apache Tomcat and Jetty on URL root path as well behind a servlet-path without modifications of HTML templates, etc.
* Secure client-server communication, if beetRoot is installed in a servlet container apart from beetRoot server and if there's need for such communication to steer backend processes
* Hierarchical resource loader; e.g. German language requested, if not found, use configured default language, then use no language at all; "lookup till you find something usable" is the  
  algorithm for everything. As well, load resources from file system (first), then as a resource within packages (jar, war) if not found beforehand.
* And some more stuff...

## Links

* [Project website](https://github.com/autumoswitzerland/autumo-beetroot)
* [API Docs](https://products.autumo.ch/javadoc/autumo-beetroot/index.html)

**Enjoy!**
