---
title: "Understanding Apache Maven (Part 6): POM Reference"
slug: "understanding-apache-maven-part-6-pom-reference"
date: "2021-10-29T08:20:00+00:00"
lastmod: "2021-10-29T08:22:31+00:00"
description: "Not meant as a full dissection of the Apache Maven pom.xml, this article will go through some of its common portions."
authors:
  - "c-guntur"
image: "https://cgunturme.files.wordpress.com/2020/06/mavenpomproject.png?w=1024"
categories:
  - "Maven"
tags:
related_posts:
  - "understanding-apache-maven-part-3-maven-coordinates-pom-inheritance"
  - "understanding-apache-maven-part-4-maven-lifecycle"
  - "understanding-apache-maven-part-5-dependencies-in-maven"
frozen: false
---

In Part 6 [of the series](https://foojay.io/today/author/c-guntur/), a walkthrough of POM content (XML) is covered!

This article is not meant for a full dissection of a `pom.xml`, since the Apache Maven doc does the job so well that anything else written will at best be a copy of that content. It is strongly recommended to peruse the linked doc below.
> Excellent documentation of a `pom.xml` on the Apache Maven site: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html>.

With the assumption that the above linked content has been read *and bookmarked for future use,* this blog will go through some of the **common portions of the `pom.xml`**.

**Reminder**: Apache Maven is polyglot. XML was the first and most commonly used format for describing a POM. This blog assumes XML format but other formats share the same logic.

The `project`
-------------

![POM Contents. The dark background elements have complex structures while the light background are simple elements. Build and Profiles have additional diagrams](https://cgunturme.files.wordpress.com/2020/06/mavenpomproject.png?w=1024) POM contents. The dark background elements have complex structures while the light background is for simple elements. Build and Profiles have additional diagrams

This is the root element of a POM (Project Object Model). All convention overrides of a maven project are listed under the `project` element in the XML. A parent is identified by its coordinates (**groupId** , **artifactId** and **version** ) and an optional **relativePath** . The **relativePath** by convention expects a parent to exist one directory above. This relativePath value can be overridden to point to relative alternate locations (such as same directory) or an empty value, to ignore searching locally and only search in configured repositories.

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_project>

### Project Model `modelVersion`

A project is required to conform to an XML Schema Definition (XSD) version. Apache Maven 3.6.3 depends on Model 4.0.0. Ongoing discussions propose a conformity in future releases (*Apache Maven 5 potentially being the next, possibly skipping 4, and the model version for such to be 5.0.0* ). A POM requires a `modelVersion` element that is set to 4.0.0 for use with Apache Maven 3.x.x.

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_project>

### Project Coordinates

A project will need to specify its GAV (Group-Artifact-Version) coordinates. A POM can also specify a **parent** from which a **groupId** and **version** can be inherited. While a project can share the **groupId** and **version** with its parent, it will require a unique **artifactId** under that group to distinguish itself from other projects under the same group. Also, the **groupId** and **version** of the current POM can be specified in the `pom.xml`, in which case they override whatever values the parent provides. *Maven works on convention and overrides*.

When inheriting from a parent POM, Apache Maven inherits the following:

* any coordinates (typically a **groupId** and a **version**)
* **properties** element
* **url** , **inceptionYear** , **organization** , **developers** , **contributors** , **mailingLists** , **scm** elements
* **issueManagement** , **ciManagement** elements
* **dependencies** and **dependencyManagement** elements
* **repositories** and **pluginRepositories** elements
* **plugins** element along with any plugin **executions** and plugin **configurations**
* **reporting** element
* **profiles** element

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_project>

### Output `packaging`

This element (defaults to a `jar`) defines the output artifact type when the POM is executed. Typical values may include (but are not limited to): `jar`, `war`, `ear`, `pom` etc.

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_project>

### Project `name`, `description` and `url`

The **name** can be used to provide a wordy yet small title for the project. If not included, maven uses the directory name of the the project. The name is displayed in the output when executing the POM.

The description can be used to include a more verbose description of the project's intent. It is optional to include a description, but generally considered a good practice to include one.

The **url** can be used to provide a link to a webpage or site relevant to the project. It is optional to include a URL.

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_project>

### Relationship `modules`

If the current project itself is a parent or an aggregator POM (see [Part 2](https://cguntur.me/2020/05/24/understanding-apache-maven-part-2/) for definitions), then the optional **modules** element can be used. Each listed `module` refers to a relative path to the child project's directory. *It is considered a best-practice to name the **artifactId** of the child the same as its base directory*.

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_project>

### Source Control Management `scm`

The **scm** element allows specifying the connection information to the source control system for the current project. This information is valuable to the the release process for tagging the source code. IDEs too can benefit from determining the source control location.

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_scm>

### Dependency \& Plugin Artifact Search `repositories` and `pluginRepositories`

When maven executes a POM and builds an artifact, it also builds in some metadata. This metadata includes a lot of content from the POM. Adding **repository** and **pluginRepository** sections in the POM mean that potential consumers of the current project artifacts will need to resolve from the same location as was used in the current POM. This may cause potential problems if the current repositories are private or under some limited access. Best to include these elements in a `settings.xml` instead. More on this later, but a link to a detailed reference to the settings.xml is included here: <https://maven.apache.org/ref/3.6.3/maven-settings/settings.html>. It describes what content is valid in a `settings.xml`.

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_repository>  

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_pluginRepository>

### Artifact Publishing `distributionManagement`

A **distributionManagement** section is used to provide locations for publishing the build outputs (artifacts as well as site content). It allows for specifying repository locations where either a SNAPSHOT version or a release version of the artifact can be pushed. Additionally, the location to deploy the site content can be included. In most commercial and large workplaces, a parent POM for the organization provides a generic set of distribution management which the current project can inherit from.

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_distributionManagement>

### Issue Tracking \& Continuous Integration `issueManagement` and `ciManagement`

It is a good discipline for a project to have issue and bug trackers. These are used to identify rationale for changes being made to the source code either for maintaining a history or changes or for auditing why a change was made. The **issueManagement** section is where the location of the tracking system. It is commonly used in site generation.

Similar to tracking issues, it is considered good discipline for a project to have a continuous integration build. Builds could be triggered either on a change in the project ***or*** manually ***or*** on a periodic basis. Similar to configuring issue management, maven POM has a **ciManagement** section which allows specifying the location as well as notification configuration for success and failures of builds.

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_issueManagement>  

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_ciManagement>

### POM Properties `properties`

If a project has values which are re-used in multiple locations and all require update when this value has to change, then it is ideal to take advantage of using **properties** . Common usages include version numbers of dependencies, re-used configuration values and replacements of variables (templates, filters etc.) during the maven execution. The properties are declared as `<name>value</name>` pairs in XML and can be used later in the POM as dollar-substitutions `${name}`.

### Dependency Management `dependencyManagement`

In [Part 5](https://cguntur.me/2020/06/03/understanding-apache-maven-part-5/), **dependencyManagement** was covered as a lookup reference to coerce maven to resolve to a desired version of a dependency. The dependencyManagement element contains a **dependencies** element which is a set of **dependency** elements that may (*or may not* ) be used while generating the ***effective POM*** *.* Dependencies declared under this element can include GAV coordinates as well as **scope** , **optional** and **exclusions** elements.

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_dependencyManagement>

### Project Dependencies `dependencies`

The actual set of dependencies used in a project are declared in a **dependencies** element. Each **dependency** element declared within is considered to be of the nearest depth when maven generates an effective POM for the current project. A **dependency** can be declared with its GAV coordinates as well as **scope** , **optional** and **exclusions** elements.
> If a dependency was already added as a lookup reference in the **dependencyManagement** section, then such a dependency here can skip inclusion of a **version** (so the version specified in the dependencyManagement can be used). All scope, optional and exclusions declared in the dependencyManagement section are incorporated when just a **groupId** and **artifactId** are specified in this section, for any looked up dependency.

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_dependency>  

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_exclusion>

### The build instructions

![Build element contents. The dark background elements have complex structures while the light background is for simple elements.](https://cgunturme.files.wordpress.com/2020/06/mavenpombuild.png?w=1024) Build element contents. The dark background elements have complex structures while the light background is for simple elements.

Most of the instructions to chain build configuration together are all defined under a **build** element. A few elements of note are listed below.

#### Configuring directories for source, script and test files

While it is heavily recommended to not change the convention of sources, scripts and test file locations, there is, at times, a need to customize or alter such. It may also be rarely required to alter the output location of a build. In such cases it is possible to point to the directories by setting their relative paths (to the pom.xml) via the following:

* `sourceDirectory`
* `scriptSourceDirectory`
* `testSourceDirectory`
* `outputDirectory`
* `testOutputDirectory`

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_build>

#### Maven `extensions`

The ability to extend maven functionality to perform other tasks. These extensions are declared with GAV coordinates. Many of such extensions are ***Wagon Providers***, for providing artifact customization (file providers, ftp provider, SSH providers, HTTP providers etc.). Newer extensions are for format benefits of a polyglot maven (Ruby, XML, YAML, JSON etc.).

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_extension>

#### Customizing `resources`

Resources are additional content useful in running the project or its tests. Content could include properties, configurations, images and other assets that do not necessarily need compilation. Some resources may also need values added (or replaced) during the maven execution of the project POM. The **resources** (and the **testResources** compliment) element allows defining a set **resource** (or **testResource** ) elements which can customize the location in the final artifact, replacement patterns the location of resources (overriding convention or `src/main/resources` or `src/test/resources`. It is possible to filter **includes** and **excludes**sections based on filenames and wildcard patterns.

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_resource>  

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_testResource>

#### Configuring `plugins` and `pluginManagement`

Plugins are maven's means of executing **goals** . **Plugin goals** can bind to maven **lifecycle** **phases** as was discussed in [Part 4](https://cguntur.me/2020/05/29/understanding-apache-maven-part-4/). Plugins implement behavior that execute in the lifecycle phase/goal sequence. A **plugins** element can contain several `plugin` definitions which can further be configured, if needed.

A **pluginManagement** section is to a **plugin** what a **dependencyManangement** is to a **dependency** . A lookup table for configured plugins to be re-used across many modules in the project. Plugins declared in a **pluginManagement** are not loaded but are used to specify a reusable version and configuration setup that can be re-used within the actual build plugins if listed. Similar to **dependencyManagement** , a plugin declared in an accessible **pluginManagement** section can skip re-configuration and skip the **version** in the **build** -\> **plugins** section.

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_plugin>  

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_pluginManagement>

#### Customizing the entire POM based on `profiles`

![Profiles/profile content. The dark background elements have complex structures while the light background is for simple elements.](https://cgunturme.files.wordpress.com/2020/06/mavenpomprofiles-1.png?w=1024) Profiles/profile content. The dark background elements have complex structures while the light background is for simple elements.

**Profiles** will require an entire blog post by themselves. Maven offers build **profiles** which can be activated ***either*** by default, ***or*** when certain conditions are met ***or*** by flagging them in a command line to maven execution. Build profiles contain many of the sections already present under the **project** element but are only executed when the profile is activated.

A **profiles** element contains a set of **profile** elements which can be activated:

* by default (`activeByDefault`)
* matched by JDK definition in a **toolchains.xml**
* based on operating system (name, family, architecture and/or version)
* based on a property existence or a specific value
* based on a file either existing or missing

A profile can contain several other elements including:   
**build** (**resources** , **testResources** , **pluginManagement** , **plugins** ),   
**modules** ,   
**distributionManagement** ,   
**properties** ,   
**dependencyManagement** ,   
**dependencies** ,   
**repositories** ,   
**pluginRepositories** ,   
**reporting**   

etc., all covered earlier in this series.

Link: <https://maven.apache.org/ref/3.6.3/maven-model/maven.html#class_profile>

Additional links
----------------

Link: <https://maven.apache.org/pom.html>  

Link: <https://maven.apache.org/guides/introduction/introduction-to-the-pom.html>

That's a wrap on this blog. Next up is Configuring Apache Maven. Have fun !
