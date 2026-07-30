---
title: "Latest Java Releases and Eclipse Java IDE"
slug: "keeping-pace-with-java-using-eclipse-ide"
date: "2021-03-30T07:51:12+00:00"
lastmod: "2021-07-12T05:11:54+00:00"
description: "Major interesting features of latest Java versions like records and switch expressions with their tooling support in Eclipse Java IDE."
authors:
  - "noopur-gupta"
image: "/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/eclipse-logo.png"
categories:
  - "Eclipse"
  - "Sealed Classes"
tags:
related_posts:
  - "foojay-podcast-12"
  - "effective-cloud-native-development-eclipse-ide-open-liberty"
  - "write-once-run-embedded-in-any-ide"
  - "the-visitor-pattern-revisited-using-data-oriented-programming-techniques"
frozen: false
---

The Java language has been evolving at a fast pace with a six month release cadence and preview features.

With faster Java releases, it's an exciting time to be a Java developer. Every new release of Java promises interesting features and updates.

To give them a spin, you have the tooling support in Eclipse Java IDE ready at your disposal.

Under the Hood {#h2-0-under-the-hood}
-------------------------------------

The Java tooling in Eclipse IDE has its own compiler implementation which not only generates class files, but also produces a Java document model which forms the basis for implementing a large number of tooling features.

To support the latest Java versions, the Eclipse Compiler for Java implements all the new language enhancements, existing tooling support is updated to blend with the new language features, and new functionality is added to help you while working with the new language constructs.

Here is a sneak peek into the major interesting features of recent Java versions with their support in the Eclipse IDE.

Highlights {#h2-1-highlights}
-----------------------------

To get started, you can add a JRE in Preferences \> Java \> Installed JREs:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Installed-JREs.png)

Then, set the JDK compliance in Preferences \> Java \> Compiler:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/JDK-compliance.png)

To quickly enable the preview features on an existing Java project, you can right-click on it in the Package or Project Explorer and select Configure \> Enable preview features:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Enable-preview-features.png)

When a preview feature is used in the code, a default warning is provided by the compiler that the preview feature may not be supported in a future release. You can choose to ignore this problem or set it to Info by changing its severity level on the Java Compiler preference page:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Preview-features-severity.png)

### Sealed Classes {#h3-2-sealed-classes}

Sealed classes have received a second round of preview in Java 16 and they can be used to restrict the type hierarchy of a class. You can create a permitted class or interface declaring the sealed type as its super type with the provided Quick Fix (Ctrl/Cmd + 1):

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Create-permitted-class.png)

The type hierarchy needs to be controlled at the permitted sub type by declaring it as `final`, `sealed`, or `non-sealed`, which can be done using the provided Quick Fixes:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Restrict-permitted-class.png)

### Records {#h3-3-records}

You can create a record in Eclipse IDE by using the New \> Record wizard which provides more options like selecting the visibility modifier and adding the interfaces to be implemented by the record:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Record-wizard.png)

You can run a Java program which is using the record to see that the record instance is provided with auto-generated constructor, component accessor, `toString`, `equals`, and `hashcode` methods:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Record-usage.png)

Eclipse IDE allows you to perform the rename refactoring on record components and it updates the accessor method names along with the component references:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Record-component-rename.png)

A lot of new settings have been added to the formatter profile to control the formatting of records. You can use the filter to quickly view these configurable settings:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Record-formatter.png)

### Pattern Matching for instanceof {#h3-4-pattern-matching-for-instanceof}

Pattern matching for instanceof provides a pattern variable with the `instanceof` operator. Eclipse IDE provides you a new clean up option on the Java Feature tab of your clean up profile to use the pattern matching for instanceof and simplify your code by reducing explicit casts created after an `instanceof` check:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Pattern-matching-for-instanceof-clean-up.png)

Eclipse IDE understands the pattern variable's type and scope, and allows you to perform various actions like invoking the content assist (Ctrl + Space) and renaming the pattern variable:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Instanceof-pattern-variable-actions.png)

### Text Blocks {#h3-5-text-blocks}

You can create a text block by enclosing any text in triple quotes. Eclipse IDE makes it easier for you to add this new delimiter with the new keyboard shortcut Ctrl/Cmd + Shift + ' (apostrophe). You can also select any existing text and use this key binding to quickly enclose it in text block delimiters:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Text-block-shortcut.png)

The indentation of a text block can be configured in the formatter profile:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Text-block-formatter.png)

### Switch Expressions {#h3-6-switch-expressions}

Eclipse IDE provides you a Quick Assist (Ctrl/Cmd + 1) and clean up option to convert eligible switch statements into switch expressions:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Convert-to-switch-expression.png)

Many quick fixes, quick assists, and templates are also provided to help you in writing code with switch improvements. For example, there are quick fixes to add the `default` case or the missing case statements in a switch expression:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Add-cases-to-switch-expression.png)

The proposals are inserted in linked mode so you can quickly replace them with the desired values:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Linked-mode-proposals.png)

A quick assist is provided to split multiple labels in a single case statement so that you can provide separate code for these cases if needed:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Split-case-labels.png)

You can press Ctrl/Cmd and click on the `default` and `case` keywords to quickly navigate to the beginning of the switch expression.

The formatter profile has new settings that allow you to control spaces at various locations in switch expressions:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Switch-formatter.png)

### Local-Variable Type Inference {#h3-7-local-variable-type-inference}

While extracting an expression to a local variable, you can choose to declare the type of the local variable as `var`:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Local-variable-type-as-var-.png)

Quick Assists are provided to change the type of a variable from `var` to the inferred type and vice-versa:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Convert-var-to-type.png)

You can view the inferred type for `var` by just hovering over it and you can also use Ctrl/Cmd + click on `var` to navigate to that type.

### Java Platform Module System {#h3-8-java-platform-module-system}

To convert an existing Java project to a modular project by creating a module-info.java file in it, you can right-click on the project in the Package or Project Explorer and select Configure \> Create module-info.java:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Create-module-info.png)

The module-info.java file is created with directives like exports, requires, etc. based on the existing contents of the project.

You can go to the project's properties and add libraries to its modulepath on the Java Build Path \> Libraries tab:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Add-libraries-to-modulepath.png)

You can also configure the properties of its module graph on the Java Build Path \> Module Dependencies tab:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Configure-module-dependencies.png)

Quick Fixes are provided to identify the used types and add the required modules to module-info.java file:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Add-required-module-qf.png)

You can create and export a new non-empty package directly from the module-info.java file using the provided Quick Fixes:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Create-and-export-package.png)

You can also provide a service implementation from module-info.java with the help of the provided Quick Fixes:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Import-type-and-add-required-module.png)

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Create-service-implementation.png)

### Functional Interface Instances {#h3-9-functional-interface-instances}

You can hover over the `->` of a lambda expression or the `::` of a method reference to view the functional interface method being implemented and you can use Ctrl/Cmd + click on these operators to navigate to that method:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/View-functional-method.png)

Eclipse IDE provides you many quick assist, clean up, formatting, and refactoring options to work with anonymous classes, lambda expressions, and method references:

![](/images/posts/2021/03/keeping-pace-with-java-using-eclipse-ide/Functional-instances-quick-assists.png)

Summary {#h2-10-summary}
------------------------

This post provides you a glimpse of the tooling features provided by Eclipse IDE as part of its support for the recent Java versions.

The [2021-03 (4.19)](https://www.eclipse.org/eclipseide/2021-03/ "2021-03 (4.19)") release of the Eclipse IDE provides integrated support up to Java 15 and it supports [Java 16](https://foojay.io/today/highlights-of-new-jeps-in-java-16/ "Java 16") with the [Eclipse Marketplace entry](https://marketplace.eclipse.org/content/java-16-support-eclipse-2021-03-419 "Eclipse Marketplace entry").

So, download it now and start taking advantage of these enhancements!
