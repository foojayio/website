---
title: "Review of Junie, a coding agent by JetBrains – Revie"
slug: "checking-out-junie-a-coding-agent-by-jetbrains"
date: "2025-02-26T01:38:47+00:00"
lastmod: "2025-03-26T09:16:14+00:00"
description: "Checking out Junie, a newly announced coding agent by JetBrains by asking it to add a new feature to my pet project."
canonical: "https://flounder.dev/posts/trying-out-junie/"
authors:
  - "igor-kulakov"
image: "https://foojay.io/wp-content/uploads/2025/02/banner-1.png"
categories:
  - "IntelliJ IDEA"
  - "Kotlin"
  - "Uncategorized"
  - "Use Cases"
tags:
related_posts:
frozen: false
---

Other languages: [Español](https://flounder.dev/es/posts/trying-out-junie/) [한국어](https://flounder.dev/ko/posts/trying-out-junie/) [Português](https://flounder.dev/pt/posts/trying-out-junie/) [中文](https://flounder.dev/zh/posts/trying-out-junie/)

*** ** * ** ***

Recently, I talked about [Duplicate Finder](https://flounder.dev/duplicate-finder/) on the [Foojay Podcast](https://foojay.io/today/category/podcast/) hosted by [Frank Delporte](https://foojay.social/@frankdelporte). We briefly touched upon implementing support for other formats, and Frank asked if I'm planning on adding AsciiDoc, as it could be useful for his technical writing at Azul.

We agreed to think about adding the support soon. At the same time I got access to [Junie](https://www.jetbrains.com/junie/), a newly announced coding agent by JetBrains, which is currently in early access. After a while, a thought came to me that this is a great opportunity to try it in action.

Coding agents are known to solve typical tasks well. But what about a project that is not based on a well-known framework and is outside a very common domain? As somewhat of a coding-agent skeptic, my expectations were mixed.
![Post banner](https://flounder.dev/img/trying-out-junie/banner-1.png "Post banner")

<br />

Here is how it went.

Installation and overview {#h2-0-installation-and-overview}
-----------------------------------------------------------

Junie is an IntelliJ IDEA plugin. Its UI adopts a familiar vertical tool window, similar to that of JetBrains AI Assistant or GitHub Copilot. Here's what it looks like:
![Junie UI consising of a text field, the 'add context' button, and the 'Brave Mode' checkbox](https://flounder.dev/img/trying-out-junie/overview.png "Junie UI consising of a text field, the 'add context' button, and the 'Brave Mode' checkbox")

<br />

The minimalistic design only features a prompt field, a button for adding context, and a checkbox titled **Brave Mode**. This option controls whether Junie can run commands without double-checking with you. I'm not that brave yet, so I'll try that next time.

Setting requirements {#h2-1-setting-requirements}
-------------------------------------------------

Before giving Junie the coding task, I downloaded a topic from AsciiDoctor guide  

and placed it under `src/test/resources/` for Junie to use as test data and a reference.
![Initial prompt (Implement AsciiDoc support)](https://flounder.dev/img/trying-out-junie/initial-prompt.png "Initial prompt (Implement AsciiDoc support)")

<br />

For debugging purposes, I asked Junie to add the parsed blocks to a separate collection. This is because the actual index is structured in multiple levels, which makes it inconvenient to debug. For simplicity, I'd rather view the parsed elements as a flat structure if I need to check the results at runtime.

'Coding' {#h2-2-coding}
-----------------------

After you enter the prompt, Junie breaks the task down into smaller items and starts to implement them. For adding the AsciiDoc support, it came up with the following plan:
![Initial plan consisting of several sub-items](https://flounder.dev/img/trying-out-junie/initial-plan.png "Initial plan consisting of several sub-items")

<br />

As Junie executes each item, it gives you the summary of the changes. You can review them right away, without having to wait for the entire workflow to complete:
![Changes appearing in the chat dialog](https://flounder.dev/img/trying-out-junie/first-changes.png "Changes appearing in the chat dialog")

<br />

By clicking the filenames, you can track the changes in the IntelliJ IDEA's diff view in a similar way to viewing Git changes or Local History.
![Diff view](https://flounder.dev/img/trying-out-junie/diff.png "Diff view")

<br />

After all items are completed, Junie proceeds with writing the tests and then prompts you to run them:
![Junie reasons about how to test the changes, creates the tests, and prompts to run them](https://flounder.dev/img/trying-out-junie/tests.png "Junie reasons about how to test the changes, creates the tests, and prompts to run them")

<br />

In this task, I gave Junie test data and explicitly requested tests. However, it appears that Junie generates them along with the test data by default. I experimented by running tasks without mentioning tests, and Junie created them anyway.

After running the tests, which were successful in this case, Junie provides the summary of what has been done:
![Junie gives the summary of the changes together with the list of changed files](https://flounder.dev/img/trying-out-junie/summary.png "Junie gives the summary of the changes together with the list of changed files")

<br />

Code quality {#h2-3-code-quality}
---------------------------------

Upon reviewing the code and tests, I found them well-structured and neat. What I really liked is that Junie changed not just the code required for the project to compile, but also took the extra step to introduce other meaningful changes in the context of the task.
![Diff view](https://flounder.dev/img/trying-out-junie/diff-2.png "Diff view")

**Tip:** **Evaluate Expression** has a lot of interesting use-cases beyond exploring collections. For example, you can use it to [prototype and apply fixes without restarting the program](https://flounder.dev/posts/efficient-debugging-exceptions/) or [arbitrarily modify its state](https://flounder.dev/posts/debugger-god-mode/)

For instance, it slipped my mind to mention that the new indexer must be exposed in the command-line args. This oversight wouldn't cause a compilation error, still it doesn't make sense for the end user if they cannot access the feature.

Junie recognized that and added the corresponding command-line option together with a description. It also correctly updated the factory method, so that the client code could get an instance of the new indexer. At the same time, there were no unnecessary changes, which is also great!

Everything's good so far, but it appears that more work still needs to be done.

Correcting the implementation {#h2-4-correcting-the-implementation}
-------------------------------------------------------------------

One area where coding agents are not yet fully autonomous is identifying potential problems at runtime. Technically, the implementation is correct, and it passes all the tests. The results of the parsing are consistent, as seen in the **Evaluate** dialog.
![The debugger's evaluate dialog showing the list of parsed blocks](https://flounder.dev/img/trying-out-junie/evaluate.png "The debugger's evaluate dialog showing the list of parsed blocks")

**Tip** : **Evaluate Expression** has a lot of interesting use-cases beyond exploring collections. For example, [here's how](https://flounder.dev/posts/efficient-debugging-exceptions/) you can use it to prototype fixes and apply them to a running application.

*** ** * ** ***

Everything looks fine, except processing a single file is taking a surprisingly long time. Looking into it, I also found out that parsing a batch of \~35 files always fails with an `OutOfMemoryError` . Upon analyzing the implementation, I didn't find any obvious flaws such as inefficient loops or leaking resources. Running the app with `-XX:+HeapDumpOnOutOfMemoryError` gave me a heap dump, which revealed numerous JRuby types with huge retained sizes. This hinted at the library as a possible source of the problem.

Of course, this guess might not be accurate, giving us a fascinating opportunity for [profiling](https://flounder.dev/posts/get-started-with-profiling/) (or reading documentation). Anyway, changing a JRuby dependency for a simple Kotlin implementation would very likely speed things up. So, I decided to ask Junie to rewrite the code using a custom parser.

Rather than starting a new task, I used the **Follow up** prompt for that:
![A follow-up prompt that says 'Could you replace the asciidoctorj library with a homemade no-frills implementation? It should pass the same test'](https://flounder.dev/img/trying-out-junie/follow-up.png "A follow-up prompt that says 'Could you replace the asciidoctorj library with a homemade no-frills implementation? It should pass the same test'")

<br />

Results {#h2-5-results}
-----------------------

Junie revised the implementation as requested. Although I'm not very familiar with the AsciiDoc format, the parsing seems to be largely correct at the first glance. There is some room for improvement in parsing of the preamble, and likely something else, but it does its job.

Running the updated Duplicate Finder on AsciiDoctor's own help detected some duplicates! The analysis took 350 milliseconds on my laptop:
![Duplicate Finder UI showing duplicates in AsciiDoctor help](https://flounder.dev/img/trying-out-junie/results.png "Duplicate Finder UI showing duplicates in AsciiDoctor help")

<br />

The project with the [committed changes](https://github.com/flounder4130/duplicate-finder/compare/0530670ed98a5815394944f28446ca47e983dfba...f10dd6ac8e3cddb3ec5fb0cac45f56cd4a9bcbc9) is on my [GitHub](https://github.com/flounder4130/duplicate-finder). To try the new version of the app, you can find the instructions and the download link on the [Duplicate Finder page](https://flounder.dev/duplicate-finder/). Overall, the implementation might not be perfect, and it definitely requires more thorough checking, but still I'm very impressed by what you can get done in 5 minutes nowadays.

If you'd like to try out Junie yourself, you can sign up for EAP [here](https://www.jetbrains.com/junie/).
