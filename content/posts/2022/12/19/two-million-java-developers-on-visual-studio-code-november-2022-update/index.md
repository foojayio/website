---
title: "Two Million Java Developers on Visual Studio Code!"
date: "2022-12-19T13:12:37+00:00"
lastmod: "2022-12-19T13:12:39+00:00"
description: "New code editing features, the debugger gets a nice update, and visual enhancements to the Spring components."
authors:
  - "nick-zhu"
image: "postfix.gif"
categories:
  - "Tools"
  - "VS Code"
related_posts:
  - "java-on-azure-tooling-update-october-2022"
  - "java-on-azure-tooling-update-september-2022"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
frozen: false
---

**Hi everyone, we are excited to share that now there are over two million Java developers on Visual Studio Code, which wouldn't be possible without all the support from the community and our users, so thank you!**

For the November update, we are bringing you new code editing features, such as postfix completion and optimized organize imports. In addition, the debugger gets a nice update, as we support a new feature called "Step Into Target". Lastly, we've made some visual enhancements to the Spring components.

So, let's get into it...

#### **Postfix Completion**

Postfix completion lets you add code snippets around an expression you've just typed and boosts your productivity.

It's a popular feature in JetBrain products and the community have [requested this feature](https://github.com/redhat-developer/vscode-java/issues/1455) for our extensions as well. Through some investigation and changes to the upstream, postfix completion is now available in our latest release.

The postfix shortcuts we currently support are the following:

| shortcut key |      template content       |                                  description                                  |
|--------------|-----------------------------|-------------------------------------------------------------------------------|
| cast         | ((SomeType) expr)           | Casts the expression to a new type                                            |
| else         | if (!expr)                  | Creates a negated if statement                                                |
| for          | for (T item : expr)         | Creates a for statement                                                       |
| fori         | for (int i = 0; i = 0; i--) | Creates a for statement which iterates over an array in reverse order         |
| if           | if (expr)                   | Creates a if statement                                                        |
| nnull        | if (expr != null)           | Creates an if statement and checks if the expression does not resolve to null |
| null         | if (expr == null)           | Creates an if statement which checks if expression resolves to null           |
| sysout       | System.out.println(expr)    | Sends the affected string to a System.out.println(..) call                    |
| throw        | throw expr                  | Throws the given Exception                                                    |
| var          | T name = expr               | Creates a new variable                                                        |
| while        | while (expr) {}             | Creates a while loop                                                          |

Here's a demo for this feature:

[![Postfix Completion](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/11/postfix.gif)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/11/postfix.gif)

We plan to continue adding more postfix shortcuts so stay tuned.

#### **Optimized Organize Import**

Organize import is one of the most common actions for Java developers during code editing. We have made several optimizations regarding this scenario.

* Remove all unused imports from QuickFix

We have added this option to the QuickFix (the lightbulb icon) so you can remove all the unused imports all at the same time.

* Add all missing imports from QuickFix

If there is an unresolved class, you can also use QuickFix to add all missing imports and pick the classes from the dropdown

* Add all missing imports from source action menu

Another way to add all missing imports to right click to pull out the context menu, then select "Source Action", you will see "Add all missing imports" there as well.

Here's a demo for all features mentioned above

[![Organize import](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/11/organizeimport.gif)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/11/organizeimport.gif)

We are continuing to add more improvements around "organize import" scenarios and you can find all our future plans in [this GitHub issue](https://github.com/redhat-developer/vscode-java/issues/2748). Feel free to leave your feedback or comments there.

#### **Debugging - Step Into Target**

Debugging into a statement with many nested function calls can be painful because you might have to step through functions you are not really interested into.

To improve this scenario, the Debug Adapter Protocol has supported the "Step Into Target" feature that displays UI for directly stepping into the function you are interested in.

In our latest Extension Pack for Java, we have started to support this feature.

When debugging has stopped on a statement, you can select "Step Into Target" from the context menu, which allows you to directly step into the function or target you are interested in. Here's a demo for this feature:

[![Step into target](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/11/stepintotarget.gif)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/11/stepintotarget.gif)

#### **Gutter Icons for Spring Components**

Spring has always been our focus throughout the year. This time, we have added some visual enhancements to the editing areas so developer can better identify the Spring component and interact with Spring Boot dashboard.

With the latest release of Spring Boot Extension Pack, you will find gutter icons right next to Spring beans and endpoint mappings in the editor area.

This gives you visual clues that this is a Spring entity. When hovering on these components, a tooltip will pop up and you will see details about this component as well as a link to open it in Spring Boot dashboard. Here is a quick demo for this feature.

[![Spring gutter icon](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/11/springguttericon.gif)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/11/springguttericon.gif)

#### **Install Extension Pack for Java**

To use all features mentioned above, please download and install [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack) on Visual Studio Code.

[![Extension pack for Java](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/09/javapack.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/09/javapack.png)

If you are a Spring developer working on a Spring Boot application, you can also download the [Spring Boot Extension Pack](https://marketplace.visualstudio.com/items?itemName=Pivotal.vscode-boot-dev-pack) for specialized Spring experience.

[![Spring boot extension pack](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/10/spring.png)](https://devblogs.microsoft.com/java/wp-content/uploads/sites/51/2022/10/spring.png)

**Feedback and suggestions**

As always, your feedback and suggestions are very important to us and will help shape our product in future.

There are several ways to give us feedback:

* Leave your comment on this blog post
* [Open an issue](https://github.com/microsoft/vscode-java-pack/issues/new/choose) on our GitHub Issues page
* Send an email to: [\[email protected\]](/cdn-cgi/l/email-protection)

## **Resources**

Here is a list of links that are helpful to learn Java on Visual Studio Code.

* Learn more about [Java on Visual Studio Code](https://code.visualstudio.com/docs/languages/java).
