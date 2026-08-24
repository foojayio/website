---
title: "Understanding Apache Maven (Part 4): Maven Lifecycle"
date: "2021-10-08T09:00:35+00:00"
lastmod: "2021-10-08T09:00:36+00:00"
description: "In Part 4 of the series, a walkthrough of the Apache Maven lifecycles and executions is covered in quite some detail!"
canonical: "https://cguntur.me/2020/05/29/understanding-apache-maven-part-4/"
authors:
  - "c-guntur"
image: "https://cgunturme.files.wordpress.com/2020/05/mavenlifecycles.png?w=1024"
categories:
  - "Maven"
related_posts:
  - "understanding-apache-maven-part-1-the-basics"
  - "understanding-apache-maven-part-2-pom-hierarchy"
  - "understanding-apache-maven-part-3-maven-coordinates-pom-inheritance"
frozen: false
---

In Part 4 of the series, a walkthrough of the Maven lifecycles and executions is covered.

Apache Maven executions are tied to **lifecycles**. A lifecycle groups a sequence of activities. Maven provides three basic lifecycles for its standard build management. More lifecycles can be created as needed, though that is a rare need. There are three standard lifecycles provided by Maven.

## Standard lifecycles

* **clean** – Intended for clean-up of any prior build-managed outputs and artifacts.
* **default (build)** – Intended for project build, test and deployment of artifacts.
* **site** – Intended for project site documentation.

### What's in a lifecycle?

A lifecycle is a collection of related activities pertaining to a specific type of build-management. Standard lifecycles include generation of build output artifacts, creation of a documentation site as well as cleaning any artifacts produced from a prior execution.

Lifecycles in Maven comprise of **phases**. Each standard lifecycle is made up of a few phases. Maven commands are typically executions of phases. More on this in a little bit.

**Phases are sequentially executed**.

Invoking a phase **implies all prior phases in that lifecycle are executed**.
![A tree structure of the standard lifecycles and phases in each.](https://cgunturme.files.wordpress.com/2020/05/mavenlifecycles.png?w=1024) Maven standard lifecycles and their respective phases

## Exploring phases

Phases are executable blocks. Phases follow an ordered sequence within a given lifecycle. Reiterating what was already mentioned, invoking a phase implies invoking all phases before it in the lifecycle.

**Goals** are bound to phases.

## Goals – units of work

Goals are units of work (tasks). Goals are attached to a phase and this is called a ***binding*** . A goal performs a task that is considered relevant for the given lifecycle and phase. Maven provides some built in goals. Goals are defined in **plugins**.

### Binding Goals

> Brief digression: Maven outputs are controlled by **packaging** . Packaging defines the type of output expected when executing a Maven POM. A POM includes the packaging as a top level element. Default packaging is a `jar`.

As mentioned earlier, goals are bound to phases. Every `packaging` comes with a predefined set of goals bound to phases of the default lifecycle.

However, for some goals, there is a clear phase that fully corresponds to their execution. Such goals do not have a need for being linked to a `packaging`.

Some goals may not be bound to anything and can be invoked directly without a phase-binding.

### Plugins – definers of goals

**Plugins** are Maven's way of defining **goals** and providing connectors for the goals to **phases** . Plugins are developed as MOJOs (Maven's plain Old Java Objects). The plugins define goals and supply logic to deliver the goals. Goals are usually bound to phases in ***either*** the Maven built-in configurations ***or*** the project POM file.

### Binding Goals to Phases

An example of binding for the Clean lifecycle (***very confusing nomenclature***, but worth reading)
> <br />
>
> Lifecycle **clean** : Phase **clean**   
> Plugin **maven-clean-plugin** (prefix **clean** ) : Goal **clean**   
>
> The **clean** :**clean** (plugin:goal) is bound to the **clean** phase. Thus, executing the **clean** phase will trigger the run of the **maven-clean-plugin** which will look for and execute the **clean** goal.

Another example of binding, for the Site Lifecycle (site-deploy phase)
> <br />
>
> Lifecycle **site** : Phase **site-deploy**   
> Plugin **maven-site-plugin** (prefix **site** ) : Goal **site-deploy**   
>
> The **site** :**site-deploy** (plugin:goal) is bound to the **site-deploy** phase. Thus, executing the **site-deploy** phase will trigger the run of the **maven-site-plugin** which will look for and execute the **site-deploy** goal.

Maven offers a set of bundled plugins and these plugins have a defined set of goals. Plugins can also be external to the Maven distribution and can be built by other users of Maven. The standard lifecycle phases of **clean** and **site** have default phases and bindings defined. This is done in the maven-core `META-INF/plexus/components.xml`, if you're curious to dig through the details. Link to GitHub source: <https://github.com/apache/maven/blob/maven-3.6.3/maven-core/src/main/resources/META-INF/plexus/components.xml>.

It is usually rare to bind goals, by default, to phases prefixed with **pre** and **post**, but those are valid phases and goals can be bound to such.
> Lifecycle **clean**  
>
> Default Phase: **`clean`**
> Associated plugin (as of Maven 3.6.3): `org.apache.maven.plugins:maven-clean-plugin:2.5 : clean`  
>
> The **default phase** implies that if the lifecycle is invoked without a phase, the default phase (and any phases prior to it) will be executed. In this case, invoking the lifecycle will invoke both the **pre-clean** and **clean** phases.   
>
> The plugin coordinates follow the standard maven GAV coordinate system that was covered in [Part 3](https://cguntur.me/2020/05/26/understanding-apache-maven-part-3/) of this series. The content after the colon separator after the plugin coordinates is to point to the goal in the plugin.  
>
> Command line: `mvn clean`

The site lifecycle has two *default* phases, **site** and **site-deploy** . A dissection of the **site-deploy** default phase is shared below.
> Lifecycle **site**  
>
> Default Phase(s) **`site`** and **site-deploy**
> Associated plugin (for **site-deploy** and as of Maven 3.6.3): **org.apache.maven.plugins:maven-site-plugin:3.3** : **deploy**  
>
> Invoking the default phase of site-deploy will cause all phases (**pre-site** , **site** , **post-site** and **site-deploy** ) to be executed.   
>
> The plugin coordinates follow the standard maven GAV coordinate system that was covered in [Part 3](https://cguntur.me/2020/05/26/understanding-apache-maven-part-3/) of this series. The content after the colon separator after the plugin coordinates is to point to the goal in the plugin.  
>
> Command line: `mvn site-deploy`

The **default** lifecycle is unique. The overarching focus of the default lifecycle is to verify, compile, generate sources/resources, test, install and deploy the project code. It is thus not practical to setup defaults in this lifecycle, to a phase. The bindings are set at a different level called **packaging**.

A good listing for **site** and **clean** phases' bindings can be found at: <https://maven.apache.org/ref/3.6.3/maven-core/lifecycles.html>.

### Binding Goals Using Packaging

#### Packages – descriptors of outputs

**Packages** are a core element in the Maven POM and define the type of output produced by building the POM. It is possible to tie goals to the **package** element. Valid package names include: `jar`, `war`, `ear`, `pom` etc. Each package has some unique goals and thus goals are bound to these package names.

#### Binding Goals to Packages

Maven allows binding goals to packages. The default lifecycle is a great example and the bindings can be located at the maven-core `META-INF/plexus/default-bindings.xml`. Link to GitHub source: <https://github.com/apache/maven/blob/maven-3.6.3/maven-core/src/main/resources/META-INF/plexus/default-bindings.xml>

A few examples of bindings:

Package bindings for **ejb** , **ejb3** , **jar** , **war** , **par** and **rar** packages:
> `jar`, `war`, `rar`, `ejb`, `ejb3`,`par` packages
> Phaseplugin:goalprocess-resourcesresources:resourcescompilecompiler:compileprocess-test-resourcesresources:testResourcestest-compilecompiler:testCompiletestsurefire:testpackageejb:ejb or ejb3:ejb3 or jar:jar or par:par or rar:rar or war:warinstallinstall:installdeploydeploy:deploy

Package bindings for **pom** package:
> `pom` package
> Phaseplugin:goalpackage installinstall:installdeploydeploy:deploy

A good listing for the **default** lifecycle bindings can be found at: <https://maven.apache.org/ref/3.6.3/maven-core/default-bindings.html>.

## Summary

This blog was lengthy !

A lot of material was covered. In summary:

1. Maven provides standard **lifecycles**.
2. Lifecycles are a collection of **phases**.
3. Logic to execute specific actions is developed in **plugins**.
4. Plugins are **MOJO**s (Maven plain Old Java Objects)
5. Plugins define and carve out tasks called **goals**.
6. A **plugin** can define several **goals**.
7. Plugin **goals** are bound to parts of lifecycle and executed.
8. The **goals** can be bound at ***either*** to a **phase** ***or*** based on **packaging** type of the POM.
9. Execution of a Maven lifecycle implies execution of goals bound to parts of the lifecycle.
10. Maven commands **invoke** a **goal** .

A deeper dive into plugin-prefixes can be found at: <https://maven.apache.org/guides/introduction/introduction-to-plugin-prefix-mapping.html>.

Convention standards for plugin prefixes:

* `maven-${prefix}-plugin` – for official plugins maintained by the Apache Maven team itself (you **must not** use this naming pattern for your plugin, more on this in a future blog on plugin development)
* `${prefix}-maven-plugin` – for plugins from other sources

## Something Something – Personal Learning

Here is a **very crude and unscientific pictorial** of the my understanding of lifecycles, phases, goals and plugins. This is not meant to be accurate in terms of either human lifecycles or in explaining maven's lifecycles. This picture is absolutely a personal means of illustrating how I went about learning these concepts.
![Possibly inaccurate analogy of a young human lifecycle with phases such as terrible twos and adoloscence, with goals associated with each and soe external influences as plugins.](https://cgunturme.files.wordpress.com/2020/05/mavenhumananalogy.png?w=1024) Highly unscientific, possibly inaccurate lifecycle of a young human from Age 0 to Age 18. Time ranges also not distributed proportionally.

That's a wrap on this article. Have fun with Maven!
