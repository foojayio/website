---
title: "Explore project structure with IntelliJ IDEA's Dependency Matrix"
slug: "explore-project-structure-with-intellij-ideas-dependency-matrix"
date: "2023-11-20T10:28:01+00:00"
lastmod: "2023-11-28T07:47:47+00:00"
description: "Use IntelliJ IDEA's Dependency Matrix to explore the structure of your project and the dependencies between components."
canonical: "https://maritvandijk.com/explore-project-structure-with-dependency-matrix/"
authors:
  - "marit-van-dijk"
image: "https://foojay.io/wp-content/uploads/2023/11/dependency-matrix-menu.png"
categories:
  - "IntelliJ IDEA"
  - "Tutorials"
tags:
related_posts:
frozen: false
---

In this tutorial, we'll look at exploring project structure with [IntelliJ IDEA](https://www.jetbrains.com/idea/)'s Dependency Matrix.

When working with large, complex software projects, we need to understand the dependencies between components in your projects. IntelliJ IDEA has a feature called **Dependency Structure Matrix** (**DSM** ), or **Dependency Matrix**, that can help us with this.

When you are new to a project, the Dependency Matrix can help you get an overview of the dependencies in the project. The Dependency Matrix can also help us get an idea of how hard it will be to split a project, based on the dependencies between components. Finally, it can help us find and untangle cyclic dependencies.

It offers a matrix of the components in our project to help you and highlights the usage flow between them. Let's take a look!

Opening the Dependency Matrix {#h2-0-opening-the-dependency-matrix}
-------------------------------------------------------------------

You can open the Dependency Matrix from the main menu by going to **Code \| Analyze Code \| Dependency Matrix**.

![Open Dependency Matrix from the menu](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/dependency-matrix-menu.png)

Alternatively, use **Find Action** (**⌘⇧A** on macOS, or **Ctrl+Shift+A** on Windows/Linux) and search for "matrix".

![Open Dependency Matrix using Find Action](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/dependency-matrix-find-action.png)

To use this feature, the **Dependency Matrix plugin** needs to be enabled.

![Dependency Matrix plugin](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/dependency-matrix-plugin.png)

When opening the Dependency Matrix, specify the scope you want to analyze. You can select the **Whole project** or specify a **Custom scope**, and whether to include test sources or not.

![Dependency Matrix scope](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/dependency-matrix-scope.png)

After defining the scope, click **Analyze**.

If the project's class files are out of date, the analysis may result in incomplete or incorrect data. To avoid this, IntelliJ IDEA will prompt you to compile the project before continuing the DSM analysis. Click **Yes** to build the project and make sure everything is up-to-date.

![Dependency Matrix - Project is out of date](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/dependency-matrix-out-of-date.png)

The **DSM tool window** will open in a popup, showing a matrix of your project's components.

Interpreting the Dependency Matrix {#h2-1-interpreting-the-dependency-matrix}
-----------------------------------------------------------------------------

Let's take a look at how to interpret the Dependency Matrix.

![Dependency Matrix](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/dependency-matrix.png)

The row headers represent the program structure. In this example, the matrix contains the same modules from the **Project tool window** as rows.

Notice that the modules are not sorted alphabetically, as they are in the **Project tool window** . The matrix moves the components that are used the most to the bottom. This means that the components located at the top of the matrix *depend on* the components below.

Since this is a matrix, the column headers are the same as the row headers. They are omitted to save space. The dashes on the diagonal correspond to self-dependencies, which are not shown.

As you can see in the legend at the top right of the **DSM tool window**:

* dependencies are shown in blue
* mutual or cyclic dependencies, meaning that two components depend on each other, are shown in red
* dependencies flow from green to yellow.

### Dependencies {#h3-2-dependencies}

Dependencies are shown in blue. The numbers in the cells show the number of dependencies of the selected row on the selected column. An ellipsis (the three dots) in a cell means that there are more than 99 dependencies. Hover over the cell to get more information. In the example below, we see the tooltip "cucumber-java -\> cucumber-core (209)". This means that in this project the component cucumber-java (represented in the column) depends on the component cucumber-core 209 times.

![Dependency Matrix - Information on hover](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/dependency-matrix-hover.png)

You can click rows or cells to examine the relationship between the components in more detail.

When you select a row to see the relationship between the selected component and others, the selected row and corresponding column are highlighted to visualize row dependencies.

![Dependency Matrix - Selected row](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/dependency-matrix-select-row.png)

The column shows the dependencies ***of*** the selected row.

The row shows the dependencies ***on*** the selected row.

Remember from the legend that dependencies flow from green to yellow.

In this example, the core module is selected. You can see that this module ***uses*** several other modules, marked in yellow. In turn, you see that this module ***is used*** by several modules marked in green.

You can select different rows to see which components they use or are used by. You'll see that the components at the top mostly use the components at the bottom, while the components at the bottom are mostly used by components at the top and no longer using other components themselves.

You can drill down further into specific cells. When you click a cell, one component will be marked green and the other will be marked yellow. The green component ***uses*** the yellow component. The corresponding cell (marked purple) will show dependencies in the other direction, in this case 0.

![Dependency Matrix - Selected cell](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/dependency-matrix-select-cell.png)

We can drill down even further.

### Expand components {#h3-3-expand-components}

Right now everything is collapsed and only the modules are shown. You can click a module to expand its packages. The module name is shown to the left, the packages are shown as rows and the dependencies between the packages are shown in the matrix inside the box marked with a black line.

![Dependency Matrix - Expand](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/dependency-matrix-expand.png)

You can expand the packages further to show the classes in that package, and see the dependencies between classes inside the packages. You can expand modules and packages by clicking the arrows in the rows on the left, and collapse them again.

You can also expand the modules and packages by double-clicking a cell. We can collapse everything again by using the **Flatten Packages** button on the top left.

### Limit scope {#h3-4-limit-scope}

You can limit the scope of your Dependency Matrix. Right-click the row you want to look at in more detail, and from the context menu, select **Limit Scope To Selection**.

![Dependency Matrix - Limit Scope to Selection](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/dependency-matrix-limit-scope.png)

The limited scope will be opened in a new tab in the **DSM tool window**.

![Dependency Matrix - Limit Scope to Selection](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/dependency-matrix-limit-scope-result.png)

You can limit the view to see only selected dependencies. In the **DSM tool window** , right-click the cell representing the dependency you're interested in and select **Explore Dependencies Between**.

![Dependency Matrix - Explore Dependencies Between](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/dependency-matrix-explore.png)

The classes that produce these dependencies will be opened in a new tab in the **DSM tool window** . In contrast to the **Limit Scope** option, only classes which produce selected dependencies are left.

![Dependency Matrix - Explore Dependencies Between](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/dependency-matrix-explore-result.png)

Another way to open the Dependency Matrix, with the option to select a specific scope, is from the **Project tool window** (**⌘1** on macOS, or **Alt+1** on Windows/Linux). Right-click an item in the **Project tool window** and select **Analyze \| Analyze Dependency Matrix**.

![Analyze Dependency Matrix](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/analyze-dependency-matrix.png)

Notice how you can now select the module or directory as scope for the Dependency Matrix, in addition to the whole project or a custom scope.

![Analyze Dependency Matrix scope](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/analyze-dependency-matrix-scope.png)

### Navigate to relevant code {#h3-5-navigate-to-relevant-code}

We can also navigate to the relevant code from the Dependency Matrix. To select a specific dependency for further source-code analysis, right-click the dependency you are interested in in the **DSM tool window** , and select **Find Usages for Dependencies**.

![Dependency Matrix - Find Usages](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/dependency-matrix-find-usages.png)

The **Find tool window** will open, showing the usages of the selected dependency. Close the **DSM tool window** to look at the results and explore the code you're interested in. We can open the relevant code by double-clicking it in the **Find tool window** (**⌘3** on macOS, or **Alt+3** on Windows/Linux).

![Find tool window](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/find-tool-window.png)

You can reopen the window again from the main menu by going to **View \| Tool Windows \| DSM**.

![Reopen Dependency Matrix](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/dsm.png)

And remember that all windows can be opened from **Recent Files** (**⌘E** on macOS, or **Ctrl+E** on Windows/Linux) as well.

![Reopen Dependency Matrix](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/recent-files-dsm.png)

### Cyclic dependencies {#h3-6-cyclic-dependencies}

Remember from the legend that mutual or cyclic dependencies are shown in red. This means that two components depend on each other.

In a large application with multiple cyclic dependencies, you don't need to expand all the nodes one by one to find all the cyclic dependencies. You can press **F2** or select **Go to Next Cycle** from the context menu to quickly jump to the next cycle.

![Dependency Matrix - Go to Next Cycle](/images/posts/2023/11/explore-project-structure-with-intellij-ideas-dependency-matrix/go-to-next-cycle.png)

Summary {#h2-7-summary}
-----------------------

In this tutorial you've seen how the Dependency Matrix can help visualize and explore dependencies between components in  

your project.

### IntelliJ IDEA Shortcuts Used {#h3-8-intellij-idea-shortcuts-used}

Here are the IntelliJ IDEA shortcuts that we used.

|        Name         | macOS Shortcut | Windows / Linux Shortcut |
|---------------------|----------------|--------------------------|
| Find Action         | **⌘⇧A**        | **Ctrl+Shift+A**         |
| Project Tool Window | **⌘1**         | **Alt+1**                |
| Find Tool Window    | **⌘3**         | **Alt+3**                |
| Recent Files        | **⌘E**         | **Ctrl+E**               |
| Go to Next Cycle    | **F2**         | **F2**                   |

### Related Links {#h3-9-related-links}

* [(video) JetBrains - IntelliJ IDEA: Explore project structure with the Dependency Matrix](https://youtu.be/moi49_V_4g0)
* [(docs) JetBrains - Dependency Structure Matrix](https://www.jetbrains.com/help/idea/dsm-analysis.html)
* [(blog) DSM: Prepare Your Application for Modularity](https://blog.jetbrains.com/idea/2020/01/dsm-prepare-your-application-for-modularity/)
* [(blog) IntelliJ IDEA: Dependency Analysis with DSM](https://blog.jetbrains.com/idea/2008/01/intellij-idea-dependency-analysis-with-dsm/)

{{< youtube moi49_V_4g0 >}}
