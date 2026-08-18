---
title: "Brokk: AI for Large (Java) Codebases"
slug: "brokk-for-java-developers"
date: "2025-05-30T04:39:44+00:00"
lastmod: "2025-05-31T16:39:24+00:00"
description: "Brokk can help you tackle large Java codebases with ease. Big codebase? Bring it."
canonical: "https://brokk.ai/blog/brokk-for-java-developers"
authors:
  - "jbellis"
image: "hero.png"
categories:
  - "Developer Tools"
  - "Java"
  - "Tools"
tags:
related_posts:
  - "indexing-all-of-wikipedia-on-a-laptop"
  - "java-logging-what-to-log-what-not-to-log"
  - "data-modeling-in-cassandra-and-astra-db"
  - "foojay-podcast-27"
frozen: false
---

There are two reasons that AI makes mistakes writing code:

1. The LLM just isn't smart enough to tackle the problem effectively, and it simply gets the answer wrong.
2. The AI doesn't know enough about the relationships and dependencies of the code it's editing, so (best case) it hallucinates APIs that don't exist or (worse, because more subtle) it solves the problem in an awkward way that increases technical debt.

The first category is small, but it does still exist; e.g., concurrent data structures remain challenging for today's models to get right the first time. AI labs will continue to make progress on this.

The second category is the overwhelmingly dominant one for almost every large codebase today. I experienced this myself when I moved from writing the [JVector](https://foojay.io/today/indexing-all-of-wikipedia-on-a-laptop/) library, where AI was [very useful](https://thenewstack.io/how-ai-helped-us-add-vector-search-to-cassandra-in-6-weeks/), to integrating it with Cassandra, where AI tools struggled because of the codebase size. Brokk is designed to solve this problem.

Brokk is a standalone application, not a VSCode or IntelliJ plugin, because it's designed around letting you supervise the AI effectively instead of writing code by hand. AI is faster and more accurate at the tactical level of writing and even reading code line-by-line; you need new tools to supervise effectively at a higher level.

Part of effective supervision is being able to understand why the AI (inevitably) makes mistakes, so you can prevent and fix them. Since category 2 errors are caused by the AI not having enough information to make optimal decisions, to fix them you need to know what information the AI sees. Here's how Brokk makes this possible.

Sidebar: Under the Hood
-----------------------

Brokk is built with Java Swing for the UI, [Joern](https://docs.joern.io/) for advanced code analysis, [Jlama](https://github.com/tjake/Jlama) for local inference, and [langchain4j](https://docs.langchain4j.dev/) to access LLM APIs. Read more about what makes Brokk tick [here](https://brokk.ai/blog/brokk-under-the-hood), or check out the source [on GitHub](https://github.com/BrokkAi/brokk).

When you start Brokk, you'll see five main areas:

![Brokk Interface Overview](https://lh7-rt.googleusercontent.com/docsz/AD_4nXcSbbNxObiIqzkcDI2CSRLj5Wnca7Kt-YHnokuKoMD-BWtBqE9U09viBQI_2Weds_XsDu8USUPO26fgxyincMI5apjNHG8z9Dij_TQkq9l73XqLelI9v96ZseE5wK-NFWcHSLpZ2A?key=uP1lqs1lPqiVB7uD01Ypkg)  
*Brokk main interface showing the five key areas: Instructions panel on the left, Output panel in the center, Activity panel on the right, Workspace panel on the far right, and Git panel at the bottom*

From left to right, starting at the top, these are:

1. **Instructions**: Code, Ask, Search, and Run in Shell specify how your input is interpreted. Stop cancels the in-progress action. Deep Scan recommends relevant source files to add to the Workspace to accomplish the given task.
2. **Output**: Displays the LLM (or shell command) output.
3. **Activity**: A chronological list of your actions. Can undo changes to the Workspace as well as to your code.
4. **Workspace**: Lists active files and code fragments. Manipulated through the right-click menu or the top-level Workspace menu.
5. **Git**: The Log tab allows viewing diffs or adding them to context; Commit tab allows committing or stashing your changes

The Workspace is the core of Brokk's approach to managing context. Here's an example of (most of) the different types of context that Brokk can manipulate:

![Workspace Context Types](https://lh7-rt.googleusercontent.com/docsz/AD_4nXevPQ03ftq_gYGoR7LsFT9zrACMkckkFqnKdEkIAgFSDvdOkRsTnK-gvNu-3_KladCH0Es9YBAZjS8l-oROYIAexWljFVrvGzULcrROE0fVgtw1nP2lPdFRrD91ie9wtR2VpndwVg?key=uP1lqs1lPqiVB7uD01Ypkg)  
*Workspace panel showing various types of context including image files, source files, library summaries, stacktraces, Git diffs, documentation, and method usages*

From top to bottom, these are:

1. An image file added from local disk
2. An ordinary source file marked read-only
3. Summaries of all the classes in the langchain4j-core library (more on how Brokk deals with dependencies below)
4. A pasted image (the description is automatically generated)
5. A pasted stacktrace
6. A diff of a Git commit
7. A page of langchain4j documentation, acquired by pasting [the URL](https://docs.langchain4j.dev/tutorials/tools/)
8. Usages of Brokk's GitRepo::getTrackedFiles method
9. An ordinary, editable source file

You can right-click on any of the referenced files on the right to add them individually or as a group to the Workspace. This is particularly useful to instantly pull all the files touched by a Git commit into the Workspace.

Brokk also enriches context with what it knows about your code. Here, I've double-clicked on the stacktrace (on the left) and the getTrackedFiles usages (on the right) to show their contents:

![Context Enrichment](https://lh7-rt.googleusercontent.com/docsz/AD_4nXcR84k2iNjH2EWBYN1LO_l8rpl5EB21_blpQNXorhRI-4otDCYsyjV_X1aYVqbV8dEXbVz-KnfG0P8SmtdH9FAwtaS2MI8Gsq1fxSKDfJqjZ6hOVYuyp57JShpLVULorl6r515nNQ?key=uP1lqs1lPqiVB7uD01Ypkg)  
*Split view showing expanded stacktrace details on the left with full source code of methods in the stack, and expanded method usages on the right showing calling method source code*

For the stacktrace, Brokk includes the full source of any method in your project. For the usages, it similarly shows not just the call site but the source of each calling method. This lets Brokk perform refactoring across your codebase without having to load entire files into the Workspace. Smaller contexts mean faster and cheaper calls to the LLM.

The other main tool that Brokk uses to slim down the Workspace is summarization. Here is what a Brokk summary of a source file looks like:

![File Summarization](https://lh7-rt.googleusercontent.com/docsz/AD_4nXeaqRSFqPlMVJdJXwBrdva4a5YHiCzV9XDMkopZIS8uq3VRTRVkfogN-V5EgEqO5JlDO6RarVMQYdV1DmnR9ZW9s4nIk4Oynp_Gk8xRdKx7k_pRTv_Ly955P9jvR-b5ZdBuIHp6dA?key=uP1lqs1lPqiVB7uD01Ypkg)  
*Example of Brokk's file summarization showing class signatures, method declarations, and private fields without full implementation details*

Brokk extracts the signatures and declarations to show the LLM how to use this class without hallucinating. Brokk also includes private fields and methods to give a hint of how the implementation works under the hood. This means that you almost never need to include full source files in the Workspace unless they are going to be actively edited.

Recommendations
---------------

Brokk's goal is to make context management *explicit* (you always know what's in the Workspace) but not *manual* (you don't have to add each file by hand or even know what the right files are).

We already covered how Brokk links referenced files in the Workspace so you can easily add related code. Brokk also offers similar recommendations as you type your instructions in realtime.

![Quick Context Recommendations](https://lh7-rt.googleusercontent.com/docsz/AD_4nXe6lCZqHaNq1k3HWfjb1Jf6xLFFQyX7-JZAJfiVdQf_-J0IyvAoeJAoEV06J2ytgG1Ls8O3YEnS_Kugavngyd3kFqne4-u_n9rZ0jDG1RjaO8fzIQ88y-Age8Xt1CyZhzFkTyAoXA?key=uP1lqs1lPqiVB7uD01Ypkg)  
*Quick Context recommendations panel showing suggested files to add to the workspace based on the current instruction being typed*

This is called Quick Context, and it's optimized for speed. If you want to trade that speed away for a more in-depth analysis, click on Deep Scan:

![Deep Scan Results](https://lh7-rt.googleusercontent.com/docsz/AD_4nXdwRWOIsd5Ojljil2DoUI9s50TqbpG8EcDtY-gkl1b-ueedX9M-sA4zBClkmmvkp3TOqz6Fm_PJNK4CsZheVIB1V3OnWQ-ruDDbxoq8cWsrI5gEa8TrD-inP_UnK4_9VxLJw1z1fA?key=uP1lqs1lPqiVB7uD01Ypkg)  
*Deep Scan results dialog showing recommended files for a serialization refactor task, with options to Edit, Summarize, or Add files to the workspace*

This is a solid set of recommendations for the serialization refactor, although not perfect; I would change Json.java and ContextFragment.java from Summarize to Edit.

### Working with Git

The Git Panel, located at the bottom of the Brokk interface, is your primary interface to bring Git-based historical context into the Workspace. It's divided into two main tabs: Log and Commit. The Commit tab is fairly self-explanatory, so we'll focus on the operations available in the Log.

![Git Panel](https://lh7-rt.googleusercontent.com/docsz/AD_4nXdfpsNuQXEs_FfFCzSkl8ge3vTCdMC2XyI7Ch-wqQFyGj2p7F1_V2kzwD_Oq5KUdFAJDItzjB7t4RF-pMI-B8P1bpgjHboyUhxN3e9-6SFrqsNpgzp2Myn70zyPMu5YuWgQnXn6ow?key=uP1lqs1lPqiVB7uD01Ypkg)  
*Git Log panel showing commit history with right-click context menu options for capturing diffs and viewing file revisions*

* **Capture Diff:** This is the most commonly used integration point, useful for asking questions, troubleshooting regressions, and performing merges.
  * **How it works:** Right-click any set of commits or file changes, in the log and select Capture Diff. This adds a unified diff of the selected changes to your Workspace.
  * **Best for:** Providing the LLM with focused context about a past bug fix, feature implementation, or refactor. You can also right-click on the diff to add all (or some) of the affected files to the Workspace in their entirety or as summaries.

![Git Diff Context](https://lh7-rt.googleusercontent.com/docsz/AD_4nXfFi_tpV133_o_NcWdaXwsAvWrrElDw_sbRBP5QI8mzl9S2RRzBCMrvVRDMX4s_sDfpOggoRLLLjqmVAdigEnUrfstYgzyOYvv5aymxlj-vjVy8-nR6nmlPO_6URp3SnJSbcjgR?key=uP1lqs1lPqiVB7uD01Ypkg)  
*Git diff context menu showing options to add affected files to workspace as summaries or full files*

* **Capturing Older Revisions:** Primarily used to give the LLM an anchor point for a particularly complex diff.

  * **How it works:** Right-click a file from a commit's changes list or from the file's history and select View File at Revision. From the preview that opens, select Capture Revision.
  * **Best for:** Understanding the state of code at a specific point in time, or for tasks like backporting a fix.
* **Compare with Local:** Opens a human-readable diff window from which you can revert changes by chunk.

With these tools, Brokk allows you to easily pull Git's knowledge of your code's history into the Workspace to improve the LLM's ability to solve problems.

The Instructions panel is where you provide your textual input to Brokk. The buttons below it (and related features like Deep Scan) determine how Brokk interprets and acts upon those instructions.

1. **Code:** Executes your instructions to directly modify the code files currently in the Workspace.

   * **How it works:** You provide a specific instruction (e.g., "update the `add_to_repo` method in `GitRepo` to return a boolean..."), and Brokk, using the files you've added to the Workspace (either manually or via suggestions), will attempt to write and apply the necessary code changes.
   * **Best for:** Simpler, well-defined changes where you have a clear idea of what needs to be done in the provided context. It's designed for human-proposed changes.
2. **Ask:** Allows you to ask questions about the code in your Workspace or general programming concepts.

   * **How it works:** You type a question (e.g., "How does GUI theming work in this application?"), and Brokk invokes the LLM to provide an explanatory answer based (only) on the Workspace context.
   * **Best for:** Understanding existing code or planning a more complex set of changes. Like with ChatGPT, you can continue the conversation by using Ask multiple times. Ask will not make code changes, though the LLM might suggest code snippets in its answer.
3. **Search:** Performs an "agentic" search across your *entire project* (even files not currently in the Workspace) to find code relevant to your instructions.

   * **How it works:** The LLM is given tools to explore the codebase (e.g., search for symbols, fetch class summaries) to answer your query or find relevant starting points for a task. For example, if you instruct "Add an issues tab to the Git panel," and `GitPanel.java` is not in your Workspace, Search can find it.
   * **Best for:** When you're unsure where to start, need to locate specific functionalities, or want to find all usages of a particular method across the project.
4. **Run in Shell:** Executes shell commands.

   * **How it works:** You provide a shell command (e.g., `./gradlew shadowJar` or a specific command to run a test like `xclip -o | java -jar build/libs/jbang.jar --source-type=java -verbose -`), and Brokk executes it, typically within the project's root directory. The output of the command is displayed.
   * **Best for:** Running build commands, executing tests, or interacting with your project using command-line tools, when you may want to capture the output as part of your instructions or as an entry in the Workspace.
5. **Architect:** Engages an agentic system capable of performing multi-step, complex tasks.

   * **How it works:** The Architect agent can plan and execute a sequence of actions, potentially calling other tools (like Code, Ask, Search) and manipulating the Workspace (adding/removing/summarizing files) to achieve a larger goal. For example, "Refactor the OrderProcessingService to use the new AsyncEventBus for notifications. Break this into steps that keep the build green; when you are done, update relevant sequence diagrams in the documentation."
   * **Best for:** Larger refactorings, implementing new features that touch multiple parts of the codebase, or tasks that require a combination of understanding, searching, and coding.

Sidebar: LLM Models
-------------------

Brokk allows configuring both default models for each of the main actions, and a selection of commonly-used overrides. This allows the best of both worlds with a default you use most frequently, without having to reconfigure things (or worse, restart) when you want to use a different option.

![Model Configuration 1](https://lh7-rt.googleusercontent.com/docsz/AD_4nXdc1t53Ju-31AtsUVbkEHGYjukOOcrcWxFLLw1wk_3vIIgMe5rwnNluJEMVHoFrMV3I80hkMtZrBUZV1mcMPL64ogawAu8hYDxtbM4qYMRkN4C0ca0jkIGHQIMvwLD5lsq6WmgJsQ?key=uP1lqs1lPqiVB7uD01Ypkg)  
*Model configuration dialog showing default model settings for different actions like Code, Ask, Search, and Architect*

![Model Configuration 2](https://lh7-rt.googleusercontent.com/docsz/AD_4nXdadu97agADtWToJ1Fb06p0v8GtfTqBv3606NLDo-sD7M-0XSmEAbZ3IYCjZCt71fpqYz05cwnYWF-JUnpiU_WWTPjVIiZmgncnOauw4YnBEjX_-E6c6q2FReygI0ztPqxSatI67A?key=uP1lqs1lPqiVB7uD01Ypkg)  
*Model override selection interface showing commonly-used model options for quick switching without reconfiguration*

See [this article](https://brokk.ai/blog/the-best-llm-for-code) for more on how to choose which models to use.

The Edit Loop
-------------

After each set of changes to your code (whether via manual invocation of Code, or via the Architect), Brokk attempts to compile your code and run tests; it then automatically takes build and test failures back to the LLM for revision.

To do this effectively, Brokk asks the LLM to infer details about your build when you first open a project. This works particularly well with Gradle and Maven projects, but it can also handle more obscure systems like ant and sbt.

By default, Brokk runs only the tests in the Workspace after each set of changes to your code; if your test suite runs quickly enough, you can change that to run all tests instead, in `File -> Settings -> Build`:

![Build Settings](https://lh7-rt.googleusercontent.com/docsz/AD_4nXdFz3jXmgLdtmqrghO4R5PU2e9utpTP82p2SvCmwO4Ee84cGIKkAJozfBAWLeHA_OCF4MUkE2hkoocLbzbA9dzsC16pq4kIAg7aP8k4s4Y-zzGkMH19HfZoq6CEixt0oNNVmJrDnw?key=uP1lqs1lPqiVB7uD01Ypkg)  
*Build settings dialog showing options for test execution, including running only workspace tests vs all tests, and custom shell command configuration*

You can also (ab)use the Run All Tests option to specify an arbitrary shell command; any non-zero exit code will be treated as a failure and sent to the coding LLM for revision. For example, when I was debugging tree-sitter parsing I changed it to `tree-sitter query ... && sbt "testOnly ..."`

The Activity panel is designed to allow you to solve side quests, like a quick refactor or a bug fix, and come back to where you were without losing your flow or confusing the AI with irrelevant context. There are three options when you right-click on an earlier state:

![Activity History Options](https://lh7-rt.googleusercontent.com/docsz/AD_4nXcOj9vfq5QasaTLeQUSk7c34967Gb6gx-5ei4nEflx4LT5RDGFOXpy3WuYmfZBL3Z7v9yoLtI1MGh9vKbnfKrHn2kfmnMEItb1QlJRJ9K2dGFyWJFRW3Wh3jSBPibboEllT-wzc?key=uP1lqs1lPqiVB7uD01Ypkg)  
*Activity history context menu showing three options: Undo to Here, Copy Workspace, and Copy Workspace with History*

1. **Undo to Here**: This action reverts both your file changes on disk and the Workspace to the state they were in at that selected point in history. Any file modifications made after that point will be undone. (But Git history is not touched.)
2. **Copy Workspace**: This option reverts only the Workspace to the selected point. Critically, this action does not affect any changes made to your files on disk. This is ideal for scenarios where you've completed a side quest, committed those file changes, and now want the AI to refocus on your main task as it was before the diversion, while keeping the code from your side quest intact.
3. **Copy Workspace with History**: Copies the Workspace contents and also the Task History from the previous state; useful for when you need to continue building on your conversation thread from the main development line.

Often, you'll work with dependencies that are poorly documented or for which your LLM lacks sufficient version-specific knowledge, leading to API hallucinations. Brokk addresses this by allowing you to pull these dependencies into your project, where they can be manipulated like any other source file.

To do this:

1. Use `File -> Import Dependency...` and select the appropriate JAR file for your dependency.
2. Brokk will decompile the JAR. You'll see a message like "Decompilation completed. Reopen project to incorporate the new source files."
3. Reopen your project as prompted.

Once reopened, the decompiled source code from the dependency is available to Brokk. You can interact with these decompiled files much like your own project source:

* They can be read, searched, and their symbols can be used for operations like "Symbol Usage" or "Call graph."
* They can be summarized. To get an overview of an entire imported library, you can use the Summarize action (from the Workspace menu or Ctrl+M), and in the Summarize Sources dialog, enter the path to the decompiled dependency JAR followed by the recursive glob pattern `**` (e.g., `.brokk/dependencies/langchain4j-core-1.0.3.jar/**.java`) to summarize all classes within it. You don't need to memorize this path; just give the name of a class in the library and hit ctrl-space to autocomplete it. This is how I summarized the langchain4j-core library in the first Workspace screenshot.
* These decompiled files are treated as read-only; you cannot edit them directly.

The key benefit is that the LLM (and Brokk's code intelligence features) gain access to the precise API and structure of the *exact version* of the library you are using. The decompiled source is highly effective for LLM comprehension---often 99% as useful as the original source would be---and this drastically reduces the chances of the AI hallucinating non-existent or incorrectly used APIs from the dependency. That said, automating the download of sources jars to avoid decompilation is on the roadmap as well.

Lutz Leonhardt has recorded a video showing his workflow using Brokk to solve a "live fire" issue in jbang. Check out how he uses Deep Scan to get a foothold in the code, then Ask and Search to refine his plan of attack before executing it with Code:

<https://www.youtube.com/watch?v=t_7MqowT638>

Today's models are already smart enough to work in your codebase and make you 2x to 5x more productive; the reason that it doesn't feel that way is because they doesn't know your specific codebase. Brokk fixes this. It's a standalone Java application, not an IDE plugin, designed to let you find and feed the LLM *only* the relevant context (files, summaries, diffs) via its Workspace. This makes the AI faster, cheaper, and less prone to hallucinating or writing awkward code. Tools like Deep Scan, agentic Search, Git integration, and dependency decompilation help you build this focused context efficiently.

We created Brokk to let you supervise an AI assistant at a higher level than autocompletion.

If you're wrestling with AI in a large Java project, see how Brokk can improve its accuracy and your productivity. Foojay readers will get a $10 credit towards LLM usage with the following link: <https://brokk.ai/signup?code=01781b691d>. Try it out and let us know what you think!
