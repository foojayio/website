---
title: "Java on Visual Studio Code Update: February 2022"
date: "2022-02-22T10:52:50+00:00"
lastmod: "2022-02-22T10:55:03+00:00"
description: "Hi everyone, welcome to the February update of Visual Studio Code Java and this time we have a special edition for education!"
authors:
  - "nick-zhu"
image: "https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/02/enabletest.png"
categories:
  - "Release Notes"
  - "VS Code"
related_posts:
  - "java-development-with-vs-code-on-the-raspberry-pi"
  - "say-goodbye-to-project-files-in-visual-studio-code"
  - "java-on-visual-studio-code-update-january-2022"
frozen: false
---

Hi everyone, welcome to the February update of Visual Studio Code Java and this time we have a special edition for education!

Many schools are back in session after the winter holidays, and in order to provide student and educators a better experience using Java on Visual Studio Code this semester, we have been making a series of improvements regarding unit testing, GUI application development and Gradle project creation.

Let us take a look at these new features!

#### Easily enabling unit testing in basic Java projects

We have received a lot of feedback from students that when working on a simple Java project, they always have to manually add the testing framework JARs (such as JUnit) to the project, and our Java extensions does not offer any functionality to easily help them with this common task.

To address this, we have added a new feature in our extensions so that if your project (assuming it is a basic project without build tools) does not contain any testing related libraries, you can easily add the JAR and enable unit testing in your project.

To use this feature, simply go to the "Testing" view and select "Enable Java Tests" (shown in the picture below).

[![Enable test button](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/02/enabletest.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/02/enabletest.png)

This button will do two things for you:

* Download the testing related JARs based on your choice (JUnit, JUnit Jupiter, TestNG)
* Add the downloaded JAR to the libraries folder

Here is a demo of the feature:

![](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/02/enable-tests.gif)

After this step is done, simply create a Java file and start writing unit tests! You can get this feature in the latest update of [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack).

### Better support for GUI application development (JavaFX, Swing and AWT)

GUI-based Java applications are quite common in school projects. The most commonly used GUI frameworks are JavaFX, Swing and AWT. In this section, we are going to highlight some optimizations we have made to support these frameworks better.

#### JavaFX

We have added built-in support to create a JavaFX based project with Maven archetype in our Java extensions. To use this new feature, simply bring up the Command Palette (Ctrl+Shift+P) and run command "Java: Create Java Project". From the drop-down list, select "JavaFX". After selecting this item, a JavaFX project based on Maven will be created for you.

[![create javafx](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/02/create-javafx.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/02/create-javafx.png)

To run the JavaFX application, you can open the Maven view, expand `hellofx` \> `Plugins` \> `javafx` and run the Maven goal: `javafx:run`.

![](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/02/run-javafx.gif)

#### More Visual Studio Code sample projects for JavaFX

We also realize students might be working on JavaFX projects using other build tools (Gradle) or no build tools at all, therefore we have provided complete JavaFX samples for all those different cases. Please find the latest JavaFX samples for Visual Studio Code [in this sample repository](https://github.com/openjfx/samples/tree/master/IDE/VSCode).

#### Abstract Window Toolkit (AWT)

AWT is another framework that is popular among Java GUI application development. However, the types from the Abstract Window Toolkit (AWT) are hidden by default due to some constraints, therefore you may notice that code completion does not prompt AWT classes when you are working on an AWT application.

To enable the code completion for AWT, you can open the Command Palette (Ctrl+Shift+P) and then select the command Java: Help Center. Go to the Student section and select Enable AWT Development. Please note that this action will update a setting at the workspace level, so please make sure a workspace is opened in Visual Studio Code. Here is a demo of this feature:

![](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/02/enableawt-1.gif)

After the setting is enabled, code completion will work on AWT applications! You can use [this basic sample code](https://code.visualstudio.com/docs/java/java-gui#_develop-awt-applications) to test your AWT on Visual Studio Code.

#### Swing

Swing application development is supported in the Extension Pack for Java by default. You can directly develop any Swing application without additional setup. To find more Swing examples, you can visit the [official Oracle documentation](https://docs.oracle.com/javase/tutorial/uiswing/examples/components/index.html).

#### Documentation for Java GUI application development on Visual Studio Code

Apart from these feature updates, we have also added [a dedicated article](https://code.visualstudio.com/docs/java/java-gui#_develop-awt-applications) for Java GUI application development on official Visual Studio Code documentation. We hope this article can help students and educators to easily get started on GUI development and set up the project.

#### New Gradle project creation workflow

In addition to unit testing and GUI applications, we are also seeing that Gradle is becoming more popular among students as well as professional developers. Therefore, we have added support for Gradle project creation in our Java project creation workflow.

This feature will help you to bootstrap a simple Gradle project with just a few steps. To use this feature, simply bring up the command palette (Ctrl+Shift+P), then run the "Java: Create Java Project", and select "Gradle" from the list. Currently this workflow supports both Groovy and Kotlin as your Domain Specific Language (DSL).

[![Create gradle project](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/02/gradle.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/02/gradle.png)

You can also click the "Create Java Project" button to use this feature.

[![Create Java Project](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/02/createjavaproject.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/02/createjavaproject.png)

Note that you will need to install the [Gradle for Java extension](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-gradle) to use this feature. To use all the new features mentioned above, please download the latest version of [Extension Pack for Java.](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)

### Feedback and suggestions

There will be lots of exciting updates for Java on Visual Studio Code in 2022, and as always, your feedback and suggestions are very important to us and will help shape our product in future. There are several ways to give us feedback

* Leave your comment on this blog post
* [Open an issue](https://github.com/microsoft/vscode-java-pack/issues/new/choose) on our GitHub Issues page
* Send an email to: [\[email protected\]](/cdn-cgi/l/email-protection)

### Resources

Here is a list of links that are helpful to learn Java on Visual Studio Code.

* Learn more about [Java on Visual Studio Code](https://code.visualstudio.com/docs/languages/java).
