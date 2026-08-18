---
title: "Managing Dependencies in IntelliJ IDEA"
slug: "managing-dependencies-in-intellij-idea"
date: "2023-01-10T15:13:39+00:00"
lastmod: "2023-08-03T08:27:15+00:00"
description: "Learn the different ways in which we can view our project's dependencies in IntelliJ IDEA, and the different focus for each view."
canonical: "https://maritvandijk.com/managing-dependencies-in-intellij-idea/"
authors:
  - "marit-van-dijk"
image: "mvnrepository-maven.png"
categories:
  - "Gradle"
  - "IntelliJ IDEA"
  - "Maven"
  - "Tutorials"
tags:
related_posts:
  - "viewing-dependencies-in-intellij-idea"
  - "migrating-from-java-ee-to-jakarta-ee-with-intellij-idea"
  - "3-ways-to-refactor-your-code-in-intellij-idea"
frozen: false
---

In this tutorial, following on from [viewing dependencies](https://foojay.io/today/viewing-dependencies-in-intellij-idea/), we're going to take a look at managing dependencies in IntelliJ IDEA.

We'll look at different ways to add dependencies to your project, and how to add, upgrade and remove dependencies using Package Search.

There are several ways to add new dependencies to your project.

## From the build file using copy-paste

You have probably copied a dependency from [Maven Repository](https://mvnrepository.com/) (or another website) and pasted into your build file.

For example, we can copy the Gradle format for this dependency and paste it into our build.gradle file.

[![MvnRepository Gradle format](mvnrepository-gradle.png "MvnRepository Gradle format")](mvnrepository-gradle.png "MvnRepository Gradle format")

[![Copy dependency into build.gradle](copy-into-build-gradle.png "Copy dependency into build.gradle")](copy-into-build-gradle.png "Copy dependency into build.gradle")

Or, if we are using Maven, we can copy the Maven xml format into our pom.xml.

[![MvnRepository Maven format](mvnrepository-maven.png "MvnRepository Maven format")](mvnrepository-maven.png "MvnRepository Maven format")

[![Copy dependency into pom.xml](copy-into-pom-xml.png "Copy dependency into pom.xml")](copy-into-pom-xml.png "Copy dependency into pom.xml")

Did you know that if you copy-paste a Maven XML dependency into your build.gradle file, IntelliJ IDEA automatically turns it into the correct format for Gradle?

## From the build file using code completion

We can also add dependencies to our build file using code completion. For example, let's add a new dependency to our pom.xml.

[![Code completion in pom.xml](pom-xml-code-completion-1.png "Code completion in pom.xml")](pom-xml-code-completion-1.png "Code completion in pom.xml")

[![Code completion in pom.xml](pom-xml-code-completion-2.png "Code completion in pom.xml")](pom-xml-code-completion-2.png "Code completion in pom.xml")

[![Code completion in pom.xml](pom-xml-code-completion-3.png "Code completion in pom.xml")](pom-xml-code-completion-3.png "Code completion in pom.xml")

[![Code completion in pom.xml](pom-xml-code-completion-4.png "Code completion in pom.xml")](pom-xml-code-completion-4.png "Code completion in pom.xml")

We see that IntelliJ IDEA autocompletes the dependency xml, and we can search for the dependency we want, in this example AssertJ.

If needed, the version number will also be added. Since this is a test dependency, we need to add the test scope, still using code completion.

[![Code completion in pom.xml](pom-xml-code-completion-5.png "Code completion in pom.xml")](pom-xml-code-completion-5.png "Code completion in pom.xml")

[![Code completion in pom.xml](pom-xml-code-completion-6.png "Code completion in pom.xml")](pom-xml-code-completion-6.png "Code completion in pom.xml")

Code completion works in Gradle too, as you can see below.

[![Code completion in build.gradle](build-gradle-code-completion-1.png "Code completion in build.gradle")](build-gradle-code-completion-1.png "Code completion in build.gradle")

[![Code completion in build.gradle](build-gradle-code-completion-2.png "Code completion in build.gradle")](build-gradle-code-completion-2.png "Code completion in build.gradle")

## From the build file using code generation

We can also use code generation from the build file to add dependencies.

In the build file, the pom.xml in a Maven project, invoke Package Search using **⌘N** (on macOS) or **Alt+Insert** (on Windows \& Linux) and in the menu that opens, select **Add dependency**.

This will open the Dependencies tool window.

[![Invoke Package Search in pom.xml](add-dependency-pom-xml.png "Invoke Package Search in pom.xml")](add-dependency-pom-xml.png "Invoke Package Search in pom.xml")

Note that if we are using Gradle, we can do the same in our build.gradle file.

[![Invoke Package Search in build.gradle](add-dependency-build-gradle.png "Invoke Package Search in build.gradle")](add-dependency-build-gradle.png "Invoke Package Search in build.gradle")

## From the Dependencies tool window

Alternatively, we can open the Dependencies tool window directly.

There is no shortcut to open the Dependencies tool window, so we can either use Recent Files, **⌘E** (on Mac) or **Ctrl+E** (on Windows/Linux), and type in "dependencies" to open the Dependencies tool window.

[![Recent Files Dependencies](recent-files-dependencies.png "Recent Files Dependencies")](recent-files-dependencies.png "Recent Files Dependencies")

Alternatively, we can open it by clicking **Quick Launch** in the bottom-left and selecting **Dependencies**.

[![Quick Launch Dependencies](quick-launch-dependencies.png "Quick Launch Dependencies")](quick-launch-dependencies.png "Quick Launch Dependencies")

In the Dependencies tool window, we can search for a dependency. For example, let's search for AssertJ.

[![Search AssertJ](search-assertj-gradle.png "Search AssertJ")](search-assertj-gradle.png "Search AssertJ")

Note that we can select a scope for this dependency.

The names of the scopes are based on the build tool with which you are working.

Since this is a test dependency, and we are using Gradle in this project, we can set the scope to testImplementation.

[![Set Scope](set-scope.png "Set Scope")](set-scope.png "Set Scope")

We can also select the version we want to use.

[![Set Version](set-version.png "Set Version")](set-version.png "Set Version")

We can do the same in Maven.

[![Search AssertJ](search-assertj-mvn.png "Search AssertJ")](search-assertj-mvn.png "Search AssertJ")

Note that the names of scopes for Maven are different from Gradle.

In Maven, we can set the scope for a test dependency to test.

[![Scope Maven](scope-maven.png "Scope Maven")](scope-maven.png "Scope Maven")

When we click **Add**, we see that the dependency is added to the build file.

[![Add AssertJ](add-assertj.png "Add AssertJ")](add-assertj.png "Add AssertJ")

If the version number is shown in red, that means IntelliJ IDEA hasn't downloaded this library before.

Click **Load Maven Changes** so IntelliJ IDEA will update its dependencies based on the changes to the pom.xml or build.gradle file.

Go back to the Dependencies tool window and clear the search box by clicking the **x** on the right-hand side. You'll see the project's dependencies are updated with your new dependency.

Next, let's look for jackson-databind. We see that there are several versions available.

Since we have selected **Only stable**, only stable versions are shown in the list.

[![Jackson-Databind Versions](jackson-versions.png "Jackson-Databind Versions")](jackson-versions.png "Jackson-Databind Versions")

If we uncheck this option, we see that the list of versions also includes the release candidates.

[![Jackson-Databind Only Stable Versions](jackson-versions-stable.png "Jackson-Databind Only Stable Versions")](jackson-versions-stable.png "Jackson-Databind Only Stable Versions")

For production code, we probably want to use stable versions, so let's select the **Only stable** checkbox again.

With this option enabled, IntelliJ IDEA will exclude any dependencies that have no stable versions, and hide them from the list.

Now we can select the latest stable version and add this to our project. Let's also **Load Maven Changes** again.

Finally, let's also add a new dependency to the Kotlin module.

Let's switch to the Kotlin module and open the pom.xml for this module.

Open the Dependencies Tool Window and search for Ktor.

[![Search Ktor](search-ktor.png "Search Ktor")](search-ktor.png "Search Ktor")

Notice that some dependencies are marked as Multiplatform.

[![Show Kotlin Multiplatform](show-kotlin-multiplatform.png "Show Kotlin Multiplatform")](show-kotlin-multiplatform.png "Show Kotlin Multiplatform")

If we want to see only Kotlin multiplatform dependencies, we can select the **Kotlin multiplatform** checkbox, as shown below.

[![Select Kotlin Multiplatform](select-kotlin-multiplatform.png "Select Kotlin Multiplatform")](select-kotlin-multiplatform.png "Select Kotlin Multiplatform")

When we click **Add** to the right of the Ktor dependency, we see that Ktor is added to the list of dependencies and to the pom.xml for the Kotlin module.

[![Add Ktor](add-ktor.png "Add Ktor")](add-ktor.png "Add Ktor")

We will also need to keep our dependencies up to date.

To show you how IntelliJ IDEA can help, we are using this extremely outdated project as an example.

In the pom.xml below, we see that several dependencies are marked with squiggly lines underneath them.

[![Outdated Dependencies in pom.xml](pom-xml-outdated-dependencies.png "Outdated Dependencies in pom.xml")](pom-xml-outdated-dependencies.png "Outdated Dependencies in pom.xml")

IntelliJ IDEA will show the suggestion to upgrade when we hover over the dependency, and we can click the suggestion to upgrade the dependencies.

[![Hover over outdated dependency](hover.png "Hover over outdated dependency")](hover.png "Hover over outdated dependency")

Alternatively, we can use Context Actions **⌥⏎** (on macOS) or **Alt+Enter** (on Windows \& Linux) to upgrade these dependencies.

[![Context Actions](context-action.png "Context Actions")](context-action.png "Context Actions")

We can also upgrade our dependencies using the Dependencies tool window.

The Dependencies tool window will tell us if there's a newer version of a dependency, as we can see here.

[![Dependencies with newer versions](dependencies-with-upgrades.png "Dependencies with newer versions")](dependencies-with-upgrades.png "Dependencies with newer versions")

We can choose the version to upgrade to by clicking on the version number in the list.

Note that we don't have to use the latest version.

[![Select version](select-version.png "Select version")](select-version.png "Select version")

We can also automatically upgrade a dependency to the latest version by clicking **Upgrade** for that particular dependency.

[![Upgrade individual dependency](upgrade-individual.png "Upgrade individual dependency")](upgrade-individual.png "Upgrade individual dependency")

Or, we can even upgrade all our dependencies at once, by clicking the **Upgrade all** link.

[![Upgrade all dependencies](upgrade-all.png "Upgrade all dependencies")](upgrade-all.png "Upgrade all dependencies")

Finally, we can remove dependencies we no longer need. In the Dependencies tool window, let's remove jackson-databind from the Java module.

We select the dependency we want to remove (jackson-databind) and in the Dependency details pane on the right, click the **More** button (three dots) and select **Remove**.

[![Remove Dependency](remove-dependency.png "Remove Dependency")](remove-dependency.png "Remove Dependency")

We will see that the dependency is removed from the pom.xml and the dependency list. To remove a dependency from the whole project, select **All Modules** on the left.

Now we know the different ways in which we can view our project's dependencies in IntelliJ IDEA, and the different focus for each view.

### IntelliJ IDEA Shortcuts Used

Here are the IntelliJ IDEA shortcuts that we used.

|                                               Name                                               | macOS Shortcut | Windows / Linux Shortcut |
|--------------------------------------------------------------------------------------------------|----------------|--------------------------|
| Open / Close [Project Tool Window](https://www.jetbrains.com/help/idea/project-tool-window.html) | **⌘1**         | **Alt+1**                |
| Recent Files                                                                                     | **⌘E**         | **Control+E**            |
| Invoke Package Search                                                                            | **⌘N**         | **Alt+Insert**           |
| Context Actions                                                                                  | **⌥⏎**         | **Alt+Enter**            |

### Related Links

* [(video) JetBrains - IntelliJ IDEA: Managing Dependencies](https://www.youtube.com/watch?v=nqb9yAecM9Y)
* [(video) JetBrains - IntelliJ IDEA: Viewing Dependencies](https://www.youtube.com/watch?v=nqb9yAecM9Y)
* [(blog) Viewing Dependencies in IntelliJ IDEA](https://maritvandijk.com/viewing-dependencies/)
* [(docs) JetBrains - Package Search](https://www.jetbrains.com/help/idea/package-search.html)
* [(docs) JetBrains - Package Search Build System Support Limitations](https://www.jetbrains.com/help/idea/package-search-build-system-support-limitations.html)
* [(code) JetBrains - intellij-samples](https://github.com/JetBrains/intellij-samples)
* [(code) Spring PetClinic](https://github.com/spring-projects/spring-petclinic)
* [(book) Getting to Know IntelliJ IDEA - Trisha Gee \& Helen Scott](https://leanpub.com/gettingtoknowIntelliJIDEA)
