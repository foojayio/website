---
title: "OpenRewrite: Automatic Code Refactoring and Maintenance"
slug: "openrewrite-automatic-code-refactoring-and-maintenance"
date: "2023-09-05T17:05:06+00:00"
lastmod: "2023-09-18T12:24:50+00:00"
description: "OpenRewrite is a powerful tool for automatic code refactoring and maintenance, utilizing customizable recipes to streamline your coding process."
authors:
  - "mahendra1413"
image: "OpenRewrite.gif"
categories:
  - "Java"
  - "OpenRewrite"
tags:
related_posts:
  - "we-all-grow-older-but-do-our-projects-really-have-to-openrewrite"
  - "how-to-upgrade-to-jakarta-ee-10-and-glassfish-7-its-much-easier-than-you-think"
  - "foojay-io-at-fosdem-2023-trip-report"
enlighterjs: true
frozen: false
---

As software engineers and architects, we often face the challenge of upgrading dependencies, security patches, and framework upgrades to the latest versions. Enterprise organizations often have legacy code developed and maintained for a long time.

Updating dependency versions in the build file in many build files in the software projects, and implementing large framework upgrades can be a daunting task for developers, who must manually inspect and modify class files, properties, and the build files such as `pom.xml` or `build.gradle` or any other build files.

The [OpenRewrite](https://docs.openrewrite.org/) library will alleviate this problem among a number of tools available to us.

In this article, we will understand how the OpenRewrite library addresses these developer challenges.

![OpenRewrite](OpenRewrite.gif)   

Source: OpenReWrite

So,

The [OpenRewrite documentation](https://docs.openrewrite.org/ "OpenRewrite") defines itself as follows:
> OpenRewrite enables large-scale distributed source code refactoring for framework migrations, vulnerability patches, and API migrations.

While the original focus was on Java, OpenRewrite continuously expands language and framework coverage.

Its key feature is that it will automatically refactor source code by applying `recipes` to your project. These recipes are written in Java code and include in the build process using the OpenRewrite Maven or Gradle plugin. It cannot only refactor Java code but also Maven's `pom.xml`, Gradle's `build.gradle`, and property files( `application.properties` or `application.yml`). Alternatively, you can also run recipes on your codebase without including them in your build process in command line interface.

This process will involve the refactoring of both Java code and build files such as `pom.xml` or `build.gradle`, and application.properties or application.yml. Refactoring will takes place main branch itself, without creating a feature branch a separate CI pipeline or build profile.

OpenRewrite offers a plethora of modules that are readily available for code maintenance and framework upgrades, for example

* Upgrading Spring Boot
* Rectifying the code smells identified by Static Analysis Tools
* Migrating to JDK11 or JDK 17
* Upgrading from JUnit4 to JUnit5
* Automated resolution to checkstyle violations
* Migrate to SL4J from Log4j
* Automating Maven Dependency Management
* Migrate to Quarkus 2 from Quarkus 1
* Migrate to Micronaut 3 from Micronaut 2
* and many more

A comprehensive inventory of all recipes is available in the [recipe catalogue](https://docs.openrewrite.org/recipes "recipe catalog"). In the [Popular Recipe Guides](https://docs.openrewrite.org/running-recipes/popular-recipe-guides) section, you'll find guides for the most popular recipes.

How does OpenRewrite work Internally? {#h2-0-how-does-openrewrite-work-internally}
----------------------------------------------------------------------------------

When implementing a recipe on the code base, OpenRewrite will generate a tree representation of the code that is an extended version of the [Abstract Syntax Tree(AST)](https://en.wikipedia.org/wiki/Abstract_syntax_tree "Abstract Syntax Tree(AST)"). This tree representation furnishes fundamental information to the compiler during the compilation process and possesses various structural properties.

A "Lossless Semantic Tree" or LST is a tree structure that possesses the below properties.

* The preservation of the original formatting of the code when executing the recipes through the utilization of the whitespace before and after tree elements
* The source file does not expressly define encompassing types for all elements.
* Markers will be associated with tree elements. Markers essentially provide metadata information.
* For instance, [Build markers](https://docs.openrewrite.org/reference/framework-provided-markers#build-markers), record the type and version of the build too that generated the LST
* For instance, [Java markers](https://docs.openrewrite.org/reference/framework-provided-markers#java-markers), such as NamedStyles comprise a named collection of styles that represent code/style formatting and associated configuration options.
* The tree possesses complete serialization, notwithstanding the presence of cyclic elements. This feature enables the tree to be pre-generate in JSON format and preserved for subsequent processing.

Let's understand above mentioned description with a pragmatic approach.

Consider the following simple example

```java
package com.bsmlabs;

public class HelloOpenRewrite {
   public String welcome() {
      return "Welcome to OpenRewrite!";
   }
}
```


When generating the LST for this class, we will get the following output:

```
#root
\---J.ClassDeclaration
    |---J.Modifier | "public"
    |---J.Identifier | "HelloOpenRewrite"
    \---J.Block
        \---#JRightPadded
            \---J.MethodDeclaration
                |---J.Modifier | "public"
                |---J.Identifier | "String"
                |---J.Identifier | "welcome"
                |---#JContainer
                |   \---#JRightPadded
                |       \---J.Empty
                \---J.Block
                    \---#JRightPadded
                        \---J.Return | "return "Welcome to OpenRewrite!""
                            \---J.Literal
```


In the following example, the JavaVisitor defines the relevant visitor methods in a general class.

```java
class JavaVisitor<P> extends TreeVisitor<J, P> {

    ...
    public J visitClassDeclaration(J.ClassDeclaration classDecl, P p) {...}
    public J visitIdentifier(J.Identifier ident, P p) {...}
    public J visitBlock(J.Block block, P p) {...}
    public J visitMethodDeclaration(J.MethodDeclaration method, P p) {...}
    public J visitReturn(J.Return retrn, P p) {...}
    ...

}
```


Each of the methods available allows accessing and, crucially, modifying metadata information pertaining to the respective LST element to effect transformations in the source code. However, note that not all LST elements possess a corresponding visitor method. For instance, the `J.Modifier` element can solely be accessed via its parent element, namely `J.ClassDeclaration or J.MethodDeclaration`.

We can implement the preceding class and can develop custom refactoring recipe. In the subsequent section, we shall comprehend the methodology for executing this refactoring code in action.

OpenRewrite in Action {#h2-1-openrewrite-in-action}
---------------------------------------------------

You can accomplish the integration of OpenRewrite into the build process with ease by utilizing either the OpenRewrite Maven or Gradle plugin. In the configuration section of the plugin, you can specify which recipes to activate for the current project.

For **Maven** , add the below *OpenRewrite Maven Plugin* in `pom.xml`

```xml
<plugin>
    <groupId>org.openrewrite.maven</groupId>
    <artifactId>rewrite-maven-plugin</artifactId>
    <version>5.4.2</version>
    <configuration>
        <activeRecipes>
            <!-- Define the recipes available in the catalogure or to the name custom defined in rewrite.yml -->
            <recipe>...</recipe> 
            ...
        </activeRecipes>
    </configuration>
    <dependencies>
        <!-- It is necessary to declare dependencies for recipes that are not included in the OpenRewrite bundle. -->
        ...
    </dependencies>
</plugin>
```


It is recommended to take a precautionary measure by executing the command `mvn rewrite:dryrun`. This command will generate a set of differences solely for the modifications made within the `/target/rewrite` directory, which will be saved in the `rewrite.patch`.

During the `dryRun` phase, if one is sufficiently confident in the modifications made, the command `mvn rewrite:run` may be executed for the code base. Upon completion, the system will generate a collection of changed source files, which the user can review and commit if they deem them satisfactory.

For **Gradle** , add the below *OpenRewrite Gradle Plugin* in `build.gradle`

<br />

```yaml
plugins {
    id 'java'
    id 'maven-publish'
    id 'org.openrewrite.rewrite' version '6.2.4'
}

rewrite {
    activeRecipe(
        'org.openrewrite.java.OrderImports',
    )
}
// for running external recipe modules
dependencies {
    rewrite platform('org.openrewrite.recipe:rewrite-recipe-bom:2.2.1')
    rewrite('org.openrewrite.recipe:rewrite-spring')

    // Other project dependencies
}
```


And also if you want to add custom related changes you can add it `rewrite.yml` and place it under the root of the project (or in `/META-INF/rewrite`). In this file, you can add as many number of recipes as you desired, For instance, if you wanted to run the [ChangePackage recipe](https://docs.openrewrite.org/reference/recipes/java/changepackage) to change `org.old.package.name` to `org.new.package.name`, you can add a `rewrite.yml` file that looks like this:

```yaml
---
type: specs.openrewrite.org/v1beta/recipe
name: com.bsmlabs.ChangePackageExample
displayName: Rename package name example
recipeList:
  - org.openrewrite.java.ChangePackage:
      oldPackageName: com.bsmlabs.openaccount
      newPackageName: com.bsmlabs.account
      recursive: null
```


Having defined `com.bsmlabs.ChangePackageExample`, it is now imperative to activate it in your build file.

For **Maven**,

```xml
<project>
  <build>
    <plugins>
      <plugin>
        <groupId>org.openrewrite.maven</groupId>
        <artifactId>rewrite-maven-plugin</artifactId>
        <version>5.4.2</version>
        <configuration>
          <activeRecipes>
            <recipe>com.bsmlabs.ChangePackageExample</recipe>
          </activeRecipes>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>
```


For **Gradle**,

```yaml
plugins {
    id("org.openrewrite.rewrite") version("6.2.4")
}

rewrite {
    activeRecipe("com.bsmlabs.ChangePackageExample")
}

repositories {
    mavenCentral()
}
```


Conclusion {#h2-2-conclusion}
-----------------------------

OpenRewrite is a potent instrument that facilitates the systematic maintenance of numerous software projects within an organization. It offers a plethora of refactoring recipes that automate various maintenance tasks, including dependency updates and resolution of issues identified by static code analysis tools. Nonetheless, given that OpenRewrite is still in the developmental phase, the stability of the recipes may not be guaranteed. It is recommended to utilize the latest versions of both OpenRewrite and the recipes whenever feasible.

In the next blog post, I will show you how to migrate Java and Spring Boot projects using OpenRewrite recipes.

Reference {#h2-3-reference}
---------------------------

This article has been a significant source of inspiration for me to create the aforementioned blog post. Kindly refer to the link provided below.  
<https://www.innoq.com/en/blog/2023/03/code-maintenance-openrewrite/>
