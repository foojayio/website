---
title: "Understanding Apache Maven (Part 2): POM Hierarchy and Effective POM"
date: "2021-09-03T08:57:41+00:00"
lastmod: "2021-09-03T09:04:19+00:00"
description: "Following on from the first article covering the Maven basics, the Project Object Model (POM) is explored, here in part 2!"
canonical: "https://cguntur.me/2020/05/24/understanding-apache-maven-part-2/"
authors:
  - "c-guntur"
image: "https://cgunturme.files.wordpress.com/2020/05/maveneffectivepom.png?w=1024"
categories:
  - "Maven"
tags:
related_posts:
  - "understanding-apache-maven-part-1-the-basics"
  - "a-simple-service-with-spring-boot"
  - "getting-started-with-jakarta-ee-9-hello-world"
frozen: false
---

Following on from [the first article on Maven basics](https://foojay.io/today/understanding-apache-maven-part-1-the-basics/), the Project Object Model (POM) is explored, below.

## What is the Project Object Model?

First, a maven POM is not a [popular pomegranate juice](https://www.pomwonderful.com/) nor is it related to the colorful [pom-poms](https://www.bing.com/images/search?q=pom+poms). A Maven POM is definitely is wonderful and brings as much joy to a developer as does a pom-pom to a kid.

A POM describes build management needs of a project:

* **project coordinates** -- uniquely identifiable set of properties by which the project artifacts can be consumed elsewhere.
* **dependencies** -- libraries and code needed to execute the project build management
* **plugins** -- helper tools that execute build and build management aspects
* **properties** -- common and extracted values used in the project
* **inheritance details** -- the ability to create a hierarchy of re-usable POM components
* **profiles** -- alternate execution pathways that can be activated on a per-execution basis
* . . .

## How does Maven interact with a POM?

Maven utilizes content in the POM for its build management. However, maven also has convention-based defaults. Maven thus has the onus of amalgamating defaults and applying overrides and additions discovered in the project's pom file (typically a `pom.xml`).

This amalgamation of defaults and applying overrides and additions results in an **effective POM**.

### What is an effective POM?

An effective POM is:

* an assembly of execution steps, properties and profiles
* the content that maven can execute for the project
* an exhaustive set of dependencies and plugins needed for such an execution
* determination of any transitive dependencies (dependencies of dependencies, full depth imaginable)
* any conflict resolution in terms of dependency versions

### How does Maven assemble the effective POM?

![A set of boxes representing the maven internal defaults -> maven super pom -> maven global settings -> maven user settings -> Parent/bill-of-material poms -> project pom that finally results in an effective POM.](https://cgunturme.files.wordpress.com/2020/05/maveneffectivepom.png?w=1024)

Maven *assembles* its effective POM by traversing the layers that act as building blocks. Each layer used has the ability to override or enrich the content of what will become an **effective POM**. Maven internal defaults and the super POM are built-in to the maven installation, so ideally not subject to customization. The layers below, the global settings and user settings are, as their name suggests, inclined towards hosting and overriding any settings for maven. The parent, bill-of-material and project POM files are where maven instructions can be customized. Default values from above layers are utilized if no customization is made.

## The layers explained

Going through the layers:

### Maven Internal Defaults

This layer is within the maven's own code. Unless the intent is to modify maven's source code, it is safe to assume that these defaults are not modifiable.  
**Location:** Internal to the maven installation.

### Maven Super POM

This Super POM exists in the maven's own code. Once again, unless the intent is to modify maven's own source code, it is same to assume that the Super POM is not modifiable.  
**Location**: Internal to the maven installation

### Maven Global Settings

Once Maven is installed/unarchived on a computing device, it creates a directory structure. One of the base directories, (right below the maven directory) is `conf`. This directory contains a `settings.xml` referred to as a **global settings** file. Since this file exists on the local computing device, it is editable. Typically, any settings that apply to **all** projects being managed on the computing device are managed in this global `settings.xml`. Examples may include proxy settings, corporate server URLs, mirrors etc. It should not often be modified, in any case.  
**Location** :  
**Unix/MacOS** : \<maven installation\>/conf/settings.xml  
**Windows**: \<maven installation\\conf\\settings.xml

### Maven User Settings

Similar to the global settings, but at a different location, it is possible to create a **user settings** file. This file is also named `settings.xml` but its location is under the user home in an `.m2` directory. The purpose of the user `settings.xml` is to setup any settings that apply to projects managed by the specific user (there could be multiple users on a given computing device). Examples include usernames and passwords to connect to the network, repository ordering etc.  
**Location:**   
**Unix/MacOS:** \<user.home\>/.m2/settings.xml  
**Windows:** \<user.home\>\\.m2\\settings.xml

### Parent / Bill-of-Material POMs

Maven has an inheritance and version-trait model. Maven can inherit content from a parent pom and version-traits from a bill-of-materials pom. Typically parent POMs contain re-usable dependencies, plugins and properties used by a project POM. The Bill-of-Material (BOM) POM is a specialized POM that allows to group together dependency versions of dependencies that are known to be valid and tested to work together. Using a BOM POM reduces the developer grief from having to test compatibility of different dependencies. Modification of either is possible if there is a need and the entitlement to change. Since changes to either can impact several other projects, appropriate version increments and reviews are recommended for changes.  
**Location:** Various locations, either on device or elsewhere in some repository.

### Project POM

This is the Project Object Model for the project. Project specific maven instructions are detailed in this location. Typical contents include: a unique set of coordinates used to identify the project, name and description of the project, a set of developers associated with the project, any source control management details specific to the project, all project-specific dependencies and plugins, any profiles that allow for alternate executions of maven on this project and so on. All these will be covered in a blog in this series. The file containing this POM is, by convention, named pom.xml, but other names can be used. If an alternate name is used, then the maven executable will need to be pointed to the the filename for execution.  
**Location:**   
**UNIX/MacOS:** $PROJECT/pom.xml  
**Windows:** $PROJECT\\pom.xml

The next article will cover POM hierarchies. Have fun!
