---
title: "OpenRewrite: we all grow older, but do our projects really have to?"
slug: "we-all-grow-older-but-do-our-projects-really-have-to-openrewrite"
date: "2023-08-08T09:24:44+00:00"
lastmod: "2023-08-08T10:32:46+00:00"
description: "An introduction to how OpenRewrite can help you modernize your application, refactor it, perform migrations, and more!"
authors:
  - "simon-verhoeven"
image: "/images/posts/2023/08/we-all-grow-older-but-do-our-projects-really-have-to-openrewrite/openrewrite.png"
categories:
  - "Developer Tools"
  - "OpenRewrite"
  - "Tools"
tags:
related_posts:
  - "foojay-podcast-19"
  - "3-ways-to-refactor-your-code-in-intellij-idea"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "boxlang-1-14-0-sets-ranges-inner-classes-and-a-runtime-that-talks-back"
enlighterjs: true
frozen: false
---

**We have all likely worked on a project, that has grown quite "mature" over time, who knows we might even have forgotten to keep our dependencies up to date? Mayhap we finally got the green light to move from JDK 8 to 17?**

This will quite likely entail a lot of library updates, and maybe some interesting changes (cough javax =\> jakarta).

This is where a rewrite tool like OpenRewrite can come in handy, and I would like to walk through some of the options of this tool in this article.

**Note** : the commands here are executed against a sample project which you can find [in this GitHub repository](https://github.com/SimonVerhoeven/openrewrite-demo/tree/main).

OpenRewrite allows us to do major refactorings on our source code using (prewritten) recipes. It works by making changes to the [Lossless Semantic Trees](https://docs.openrewrite.org/concepts-explanations/lossless-semantic-trees) representing our source code and printing the modifications back to the source code/diffs which we can then compare and commit if we deem them ok.

*** ** * ** ***

Use cases {#h2-0-use-cases}
---------------------------

* fixes: autoformatting, unused imports, applying new conventions using a recipe, ...
* migrations: log4j ⇒ slf4j, java 8 ⇒ 11 ⇒ 17, JUnit 4 ⇒ 5, ...
* static analysis fixes: resolve common issues reported by SAST tools, code cleanup, ...
* utility: generate a CycloneDx bill of materials, update GitHub actions, ...

How does OpenRewrite work? {#h2-1-how-does-openrewrite-work}
------------------------------------------------------------

OpenRewrite makes changes to the **L** osless **S** emantic **T** ree representation of your code using *visitors*.

*Visitors* are basically event handlers, which deal with *what* to do, and *when* to do it that get triggered as OpenRewrite goes through the LST translation of our codebase.

These *visitors* can in turn be gathered into *recipes*.

Setup {#h2-2-setup}
-------------------

OpenRewrite can be run using the Maven/Gradle build plugin tools or directly from a java `main` method if a build tool plugin is not possible ([see for reference](https://docs.openrewrite.org/running-recipes/running-rewrite-without-build-tool-plugins))

Both for Maven and Gradle we can run the migrations either by modifying our build files or by running a shell command or init script respectively.

### Maven {#h3-3-maven}

If we add the plugin to our pom.xml file

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;plugin&gt;
  &lt;groupId&gt;org.openrewrite.maven&lt;/groupId&gt;
  &lt;artifactId&gt;rewrite-maven-plugin&lt;/artifactId&gt;
  &lt;version&gt;5.4.0&lt;/version&gt;
&lt;/plugin&gt;</pre>

### Gradle {#h3-4-gradle}

For Gradle, we need to be certain that `mavenCentral()` is present in our repositories section, then we need to add the following to our build file:

<pre class="EnlighterJSRAW" data-enlighter-language="groovy">plugins {
    id 'org.openrewrite.rewrite' version '6.1.16'
}

repositories {
  // needed to resolve recipe artifacts
  mavenCentral()
}

rewrite {
    // here we'll place the recipes we wish to use
}</pre>

**Note** : With Gradle, you can either add each dependency with the version number specified or add `rewrite-recipe-bom` as a bill of materials dependency:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">rewrite(platform("org.openrewrite.recipe:rewrite-recipe-bom:2.1.0"))</pre>

After which we can try `./mvnw rewrite:discover` or `./gradlew rewriteDiscover` to discover which recipes we can run from OpenRewrite using this setup. (we can add other sources/write our own).

Usage {#h2-5-usage}
-------------------

### Adding a recipe without configuration {#h3-6-adding-a-recipe-without-configuration}

Some OpenRewrite recipes require some configuration, but we will start easy with a standard OpenRewrite which doesn't need any setup.

For example, if you have a project with a lot of unused imports you can use the `org.openrewrite.java.RemoveUnusedImports` recipe which is part of the core library.

#### Maven

a) run `mvn -U org.openrewrite.maven:rewrite-maven-plugin:run -Drewrite.activeRecipes=org.openrewrite.java.RemoveUnusedImports`

b) add `org.openrewrite.java.RemoveUnusedImports` to the `<activerecipes>` in your pom file and perform `./mvnw rewrite:run`

#### Gradle

Add `activeRecipe("org.openrewrite.java.RemoveUnusedImports")` and perform `./gradlew rewriteRun`

If we were to run this one on the current project, and then execute a `git diff` we would see:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">diff --git a/src/main/java/dev/simonverhoeven/openrewritedemo/OpenrewritedemoApplication.java b/src/main/java/dev/simonverhoeven/openrewritedemo/OpenrewritedemoApplication.java
index d97b878..8e85aaf 100644
--- a/src/main/java/dev/simonverhoeven/openrewritedemo/OpenrewritedemoApplication.java
+++ b/src/main/java/dev/simonverhoeven/openrewritedemo/OpenrewritedemoApplication.java
@@ -3,8 +3,6 @@ package dev.simonverhoeven.openrewritedemo;
 import org.springframework.boot.SpringApplication;
 import org.springframework.boot.autoconfigure.SpringBootApplication;

-import java.math.BigDecimal;
-
 @SpringBootApplication
 public class OpenrewritedemoApplication {</pre>

### Adding a recipe with a configuration {#h3-7-adding-a-recipe-with-a-configuration}

Some recipes require configuration. Let us start with an easy one. For example, your organisation changes its name, and suddenly you need to rewrite your package names.

For this, we can use the `org.openrewrite.java.ChangePackage` recipe.

To set it up we will need to create a `rewrite.yml` in which we'll define a recipe name, optionally a display name, and the recipe list with the parameters. In this case, we will be renaming  
`dev.simonverhoeven.openrewritedemo.oldorgname` to  
`dev.simonverhoeven.openrewritedemo.neworgname`

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">---
type: specs.openrewrite.org/v1beta/recipe
name: dev.simonverhoeven.sampleRecipe
displayName: A simple recipe
recipeList:
  - org.openrewrite.java.ChangePackage:
      oldPackageName: dev.simonverhoeven.openrewritedemo.oldorgname
      newPackageName: dev.simonverhoeven.openrewritedemo.neworgname
      recursive: null                                             |</pre>

Then we will add `dev.simonverhoeven.sampleRecipe` to our active recipes.

When we then run the rewrite we will see that our `oldorgname` has been renamed to `neworgname` and that the package statement in our `Sample` file has also been adapted.

### Without build tool plugins {#h3-8-without-build-tool-plugins}

It is possible to use OpenRewrite without the build tool plugins, the hardest part is determining the applicable classpath for each set of files. A brief overview of the approach is documented at [running rewrite without build tool plugins](https://docs.openrewrite.org/running-recipes/running-rewrite-without-build-tool-plugins) on the OpenRewrite website.

The real power {#h2-9-the-real-power}
-------------------------------------

For now, we have used 2 quite basic recipes, which had relatively limited impact. Now let us take a leap forward to java 17 \& Spring Boot 3.1.

### Migration {#h3-10-migration}

#### Hamcrest ⇒ AssertJ

Now taking a look at our project, we stumble upon an issue. We are still using `Hamcrest`, which is no longer actively being supported, and we have encountered some challenges with using it. So a migration to a different framework such as `AssertJ` seems apt.

OpenRewrite has a lot of individual recipes for this, but we can also use `org.openrewrite.recipe:rewrite-testing-frameworks:2.0.7` ⇒ `org.openrewrite.java.testing.hamcrest.MigrateHamcrestToAssertJ` which has no required input.

So we can just add this one to our `pom.xml or build.gradle`, or execute it directly from the mvn command line.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">mvn -U org.openrewrite.maven:rewrite-maven-plugin:run  
-Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-testing-frameworks:RELEASE  
-Drewrite.activeRecipes=org.openrewrite.java.testing.hamcrest.MigrateHamcrestToAssertJ</pre>

After running this command you can see that this recipe has managed to fully replace all usages of Hamcrest. So if desired one can remove the library.

### Modernization {#h3-11-modernization}

And we would love to finally start using `spring-boot-starter-test`. Now we would like to take the sensible approach and make certain that all our tests run properly using this library. Now here is where we stumble upon a small hiccup. For some reason, our project is using JUnit 4, not 5 and since Spring boot 2.2 the backward compatibility with Spring JUnit 4 has been dropped.

#### JUnit 4 ⇒ JUnit 5

[As documented](https://junit.org/junit5/docs/current/user-guide/#migrating-from-junit4) the upgrade to JUnit 5 entails a couple of steps for which there are recipes

* `@Ignore` ⇒ `@Disabled`:  
  `org.openrewrite.java.testing.junit5.IgnoreToDisabled`
* `org.junit.Assert` ⇒ `org.junit.jupiter.api.Assertions`:  
  `org.openrewrite.java.test.junit5.AssertToAssertions`
* `org.junit.Test` ⇒ `org.junit.jupiter.api.Test`:  
  `org.openrewrite.java.test.junit5.UpdateTestAnnotation`
* @Junit 4's  
  ` @Rule ExpectedException => JUnit 5's``Assertions.assertThrows()``:`org.openrewrite.java.testing.junit5.ExpectedExceptionToAssertThrows\`
* ...

And that is the premise behind OpenRewrite, large migrations in small steps.

One of the recipes we can use for this is [org.openrewrite.java.testing.junit5.JUnit4to5Migration](https://docs.openrewrite.org/recipes/java/testing/junit5/junit4to5migration)  

for which we will need a dependency on `org.openrewrite.recipe:rewrite-testing-frameworks:2.0.7`.

When we execute this recipe we will get

<pre class="EnlighterJSRAW" data-enlighter-language="generic">[WARNING] Changes have been made to pom.xml by:
[WARNING]     org.openrewrite.java.testing.junit5.JUnit4to5Migration
[WARNING]         org.openrewrite.java.dependencies.RemoveDependency: {groupId=junit, artifactId=junit}
[WARNING]             org.openrewrite.maven.RemoveDependency: {groupId=junit, artifactId=junit}
[WARNING]         org.openrewrite.java.dependencies.AddDependency: {groupId=org.junit.jupiter, artifactId=junit-jupiter, version=5.x, onlyIfUsing=org.junit.jupiter.api.Test, acceptTransitive=true}
[WARNING]             org.openrewrite.maven.AddDependency: {groupId=org.junit.jupiter, artifactId=junit-jupiter, version=5.x, onlyIfUsing=org.junit.jupiter.api.Test, acceptTransitive=true}
[WARNING] Changes have been made to src\test\java\dev\simonverhoeven\openrewritedemo\JunitTest.java by:
[WARNING]     org.openrewrite.java.testing.junit5.JUnit4to5Migration
[WARNING]         org.openrewrite.java.testing.junit5.IgnoreToDisabled
[WARNING]             org.openrewrite.java.ChangeType: {oldFullyQualifiedTypeName=org.junit.Ignore, newFullyQualifiedTypeName=org.junit.jupiter.api.Disabled}
[WARNING]         org.openrewrite.java.testing.junit5.AssertToAssertions
[WARNING]         org.openrewrite.java.testing.junit5.CategoryToTag
[WARNING]         org.openrewrite.java.testing.junit5.TemporaryFolderToTempDir
[WARNING]         org.openrewrite.java.testing.junit5.UpdateBeforeAfterAnnotations
[WARNING]         org.openrewrite.java.testing.junit5.UpdateTestAnnotation
[WARNING]         org.openrewrite.java.testing.junit5.ExpectedExceptionToAssertThrows</pre>

If we then run a `git diff` to see the changes that were made we will notice that our `pom.xml` has been upgraded, our imports are now from the `jupiter` hierarchy, `@Ignore` ⇒ `@Disabled`, `Assert.*` ⇒ `Assertions.*`, ...

*note:* there are multiple recipes that can be used from this. For example, there is also `org.openrewrite.java.spring.boot2.SpringBoot2JUnit4to5Migration` which is a superset of the JUnit 4 to 5 \& Mockito 1 to 3 recipes.

Now we can run those tests, and everything looks fine and dandy.

#### Java 8 ⇒ 17 \& Spring boot 2.17 ⇒ 3.1

Let us take the next step, and try a migration to Java 17 and spring boot.

In our pom.xml:

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;plugin&gt;
    &lt;groupId&gt;org.openrewrite.maven&lt;/groupId&gt;
    &lt;artifactId&gt;rewrite-maven-plugin&lt;/artifactId&gt;
    &lt;version&gt;5.4.0&lt;/version&gt;
    &lt;configuration&gt;
        &lt;activeRecipes&gt;
            &lt;recipe&gt;org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_1&lt;/recipe&gt;
        &lt;/activeRecipes&gt;
    &lt;/configuration&gt;
    &lt;dependencies&gt;
        &lt;dependency&gt;
            &lt;groupId&gt;org.openrewrite.recipe&lt;/groupId&gt;
            &lt;artifactId&gt;rewrite-spring&lt;/artifactId&gt;
            &lt;version&gt;5.0.6&lt;/version&gt;
        &lt;/dependency&gt;
    &lt;/dependencies&gt;
&lt;/plugin&gt;</pre>

or build.gradle:

<pre class="EnlighterJSRAW" data-enlighter-language="groovy">plugins {
    id("org.openrewrite.rewrite") version("6.1.18")
}

rewrite {
    activeRecipe("org.openrewrite.java.spring.boot3.UpgradeSpringBoot_3_1")
}

repositories {
    mavenCentral()
}

dependencies {
    rewrite("org.openrewrite.recipe:rewrite-spring:5.0.5")
}</pre>

After running `./mvnw rewrite:run` or `./gradlew rewriteRun` we can use `git diff` to take a look at the results.

And we can see a lot of interesting changes:

* our outdated spring properties have been migrated
* our Java version has been upgraded from Java 8 to 17 (the new spring boot 3 baseline), including improvements such as:
  * using the BigDecimal RoundingMode enum rather than an int
  * `!emptyOptional.isPresent();` ⇒ `emptyOptional.isEmpty()`
  * concatenated text has been replaced with a text block
  * updated String formatting
* JUnit 4 ⇒ JUnit 5
* ...

We got all this thanks to the recipe list of [UpgradeSpringBoot_3_1](https://docs.openrewrite.org/recipes/java/spring/boot3/upgradespringboot_3_1)

It is quite amazing to see what we can achieve with just this simple action.

#### ¿Guava?

One will quite likely encounter Guava in a lot of older projects, it offered us a lot of functionality that was not part of the JDK. Over the years a lot of this functionality has become part of it though, and after all the effort we have done to upgrade our project, we would like to use the standard JDK as much as possible.

For example, in our [SampleService](https://github.com/SimonVerhoeven/openrewrite-demo/blob/main/src/main/java/dev/simonverhoeven/openrewritedemo/oldorgname/SampleService.java) we will see that a lot of things are being done using the Guava library.

OpenRewrite has a lot of individual recipes for this, but we can also use `org.openrewrite.recipe:rewrite-migrate-java:2.0.8` ⇒ `org.openrewrite.java.migrate.guava.NoGuava` which has no required input.

So we can just add this one to our `pom.xml or build.gradle`, or execute it directly from the mvn command line.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">mvn -U org.openrewrite.maven:rewrite-maven-plugin:run  
-Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE  
-Drewrite.activeRecipes=org.openrewrite.java.migrate.guava.NoGuava —-</pre>

After running this command you can see that this recipe has managed to fully replace all usages of Guava. So if desired one can remove the library.

### Static analysis fixes {#h3-12-static-analysis-fixes}

Now that we have done all this, we are finally starting to reach our target.  

The next thing we would like to tackle are the results we got from our upgraded Sonar instance. Whilst some of these will of course require human intervention, OpenRewrite offers a lot of (composite) recipes which will help us clean up the common issues which can be found at [static analysis](https://docs.openrewrite.org/recipes/staticanalysis).

We can run a lot of recipes manually, such as `org.openrewrite.staticanalysis.MissingOverrideAnnotation`, but our eye  

swiftly gets drawn to [org.openrewrite.staticanalysis.CommonStaticAnalysis](https://docs.openrewrite.org/recipes/staticanalysis/commonstaticanalysis)  

which is part of `org.openrewrite.recipe:rewrite-static-analysis:1.0.3` and has no required input.

So we can just do:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">mvn -U org.openrewrite.maven:rewrite-maven-plugin:run  
-Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-static-analysis:RELEASE  
-Drewrite.activeRecipes=org.openrewrite.staticanalysis.CommonStaticAnalysis</pre>

And we will notice that a lot of complaints such as:

* missing serialVersionUID
* inverted boolean checks
* catch should do more than just rethrow
* modifier order
* missing braces
* Strings not using .equals
* unnecessary String#toString()
* no double variable declaration
* ...

are resolved for us

In our console we will see:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">[WARNING]     org.openrewrite.staticanalysis.CommonStaticAnalysis
[WARNING]         org.openrewrite.staticanalysis.BigDecimalRoundingConstantsToEnums
[WARNING] Changes have been made to src\main\java\dev\simonverhoeven\openrewritedemo\oldorgname\SampleController.java by:
[WARNING]     org.openrewrite.staticanalysis.CommonStaticAnalysis
[WARNING]         org.openrewrite.staticanalysis.AddSerialVersionUidToSerializable
[WARNING]         org.openrewrite.staticanalysis.BooleanChecksNotInverted
[WARNING]         org.openrewrite.staticanalysis.CaseInsensitiveComparisonsDoNotChangeCase
[WARNING]         org.openrewrite.staticanalysis.DefaultComesLast
[WARNING]         org.openrewrite.staticanalysis.EmptyBlock
[WARNING]         org.openrewrite.staticanalysis.FinalizePrivateFields
[WARNING]         org.openrewrite.staticanalysis.FinalClass
[WARNING]         org.openrewrite.staticanalysis.ForLoopIncrementInUpdate
[WARNING]         org.openrewrite.staticanalysis.ModifierOrder
[WARNING]         org.openrewrite.staticanalysis.MultipleVariableDeclarations
[WARNING]         org.openrewrite.staticanalysis.NoToStringOnStringType
[WARNING]         org.openrewrite.staticanalysis.RemoveExtraSemicolons
[WARNING]         org.openrewrite.staticanalysis.RenamePrivateFieldsToCamelCase
[WARNING]         org.openrewrite.staticanalysis.UseDiamondOperator
[WARNING]         org.openrewrite.staticanalysis.InlineVariable</pre>

And looking at [SampleController](https://github.com/SimonVerhoeven/openrewrite-demo/blob/main/src/main/java/dev/simonverhoeven/openrewritedemo/oldorgname/SampleController.java) will reveal a lot of changes

### Utility {#h3-13-utility}

Now OpenRewrite goes beyond just rewriting one's codebase. There are a lot of other convenient features:

#### GitHub actions

There are quite a bit of [recipes](https://docs.openrewrite.org/recipes/github) to help you manage your GitHub workflows.

For example, there is [setup-java](https://docs.openrewrite.org/recipes/github/setupjavaupgradejavaversion)  

which updates your setup-java action if needed (and is part of the upgrade to Spring Boot 3.1 recipe for example)

<pre class="EnlighterJSRAW" data-enlighter-language="generic">mvn -U org.openrewrite.maven:rewrite-maven-plugin:run \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-github-actions:RELEASE \
  -Drewrite.activeRecipes=org.openrewrite.github.SetupJavaUpgradeJavaVersion</pre>

Or say if one wants to bulk update the used runners there is the [replacerunners](https://docs.openrewrite.org/recipes/github/replacerunners) recipe.

#### Cloud suitability analysis

OpenRewrite offers a lot of recipes for [cloud suitability](https://docs.openrewrite.org/recipes/cloudsuitability) to help you determine the cloud suitability of your project

One nice example is [findunsuitablecode](https://docs.openrewrite.org/recipes/cloudsuitability/findunsuitablecode)

Which will scan for items that may potentially cause issues such as:

* usage of ehcache
* usage of corba
* hardcoded IP addresses
* remote method invocation
* unhandled term signals
* ...

#### Secrets

Hopefully one will never need these, but there are [recipes](https://docs.openrewrite.org/recipes/java/security/secrets) to scan for different types of secrets within your codes.

For example one can spot that in our [SampleController](https://github.com/SimonVerhoeven/openrewrite-demo/blob/main/src/main/java/dev/simonverhoeven/openrewritedemo/oldorgname/SampleController.java) we have:

<pre class="EnlighterJSRAW" data-enlighter-language="java">private static final String ACCOUNT_KEY = “lJzRc1YdHaAA2KCNJJ1tkYwF/+mKK6Ygw0NGe170Xu592euJv2wYUtBlV8z+qnlcNQSnIYVTkLWntUO1F8j8rQ==”;</pre>

After running:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">mvn -U org.openrewrite.maven:rewrite-maven-plugin:run  
-Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-java-security:RELEASE  
-Drewrite.activeRecipes=org.openrewrite.java.security.secrets.FindAzureSecrets</pre>

We will see that it has been transformed to:

<pre class="EnlighterJSRAW" data-enlighter-language="java">private static final String ACCOUNT_KEY = /*\[line-through\]**(Azure access key)**\&gt;*/“lJzRc1YdHaAA2KCNJJ1tkYwF/+mKK6Ygw0NGe170Xu592euJv2wYUtBlV8z+qnlcNQSnIYVTkLWntUO1F8j8rQ==”;</pre>

Which makes it a lot easier for us to find these kind of issues.

#### Generating a Bill of Materials (BOM)

You might be asked to provide a list of your (transitive) project dependencies, this can easily be achieved using the `cyclonedx` goal.

#### etc...

A last one I wanted to point out which showcases a way in which OpenRewrite can help with the readability of your codebase is the [formatsql](https://docs.openrewrite.org/recipes/sql/formatsql) one

Which automatically transforms this:

<pre class="EnlighterJSRAW" data-enlighter-language="java">class Test {
    String query = """
            SELECT b.book_id, b.title, COUNT(r.review_id) AS num_reviews,AVG(r.rating) AS median_rating FROM books b
            JOIN reads rd ON b.book_id = rd.book_id JOIN readers
            re ON rd.reader_id = re.reader_id
            JOIN reviews r ON b.book_id = r.book_id
            GROUP BY b.book_id, b.title ORDER
            BY num_reviews DESC;\
            """;
}</pre>

to

<pre class="EnlighterJSRAW" data-enlighter-language="java">class Test {
    String query = """
            SELECT
              b.book_id,
              b.title,
              COUNT(r.review_id) AS num_reviews,
              AVG(r.rating) AS median_rating
            FROM
              books b
              JOIN reads rd ON b.book_id = rd.book_id
              JOIN readers re ON rd.reader_id = re.reader_id
              JOIN reviews r ON b.book_id = r.book_id
            GROUP BY
              b.book_id,
              b.title
            ORDER BY
              num_reviews DESC;\
            """;
}</pre>

### Summary {#h3-14-summary}

OpenRewrite has so many more interesting recipes, and I would invite you to take a gander at their recipe list.

But hopefully, this has wet your appetite, and given you a brief insight as to what can be achieved.

If you have any questions feel free to reach out, or join the OpenRewrite [Slack](https://join.slack.com/t/rewriteoss/shared_invite/zt-nj42n3ea-b~62rIHzb3Vo0E1APKCXEA).

References {#h2-15-references}
------------------------------

* [OpenRewrite documentation](https://docs.openrewrite.org/)
* [Creating your own recipe](https://docs.openrewrite.org/authoring-recipes)
* [OpenRewrite Recipe catalog](https://docs.openrewrite.org/recipes)
* [OpenRewrite recipe explanation](https://docs.openrewrite.org/concepts-explanations/recipes)
* [Moderne](https://www.moderne.io/) - a platform to automate migrating, securing, and maintaining source code. It uses OpenRewrite recipes and offers certain extra features like data tables to view the changes that were made. It is free for open-source projects.
* [Spring boot migrator](https://github.com/spring-projects-experimental/spring-boot-migrator) - a CLI tool that offers recipes to migrate/upgrade an application to Spring boot and is compatible with \& uses OpenRewrite

Notes {#h2-16-notes}
--------------------

If you have a multi-module maven project you might run into errors when using the maven plugin, a workaround \& more information is documented at [multi-module maven](https://docs.openrewrite.org/running-recipes/multi-module-maven).
