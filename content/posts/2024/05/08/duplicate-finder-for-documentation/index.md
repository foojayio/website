---
title: "Duplicate Finder for Documentation"
slug: "duplicate-finder-for-documentation"
date: "2024-05-08T22:08:04+00:00"
lastmod: "2024-12-08T21:40:59+00:00"
description: "Let's make a duplicate finder for documentation together – a tool to quickly detect non-exact, or fuzzy, matches in large text repositories."
canonical: "https://flounder.dev/posts/duplicate-finder-intro/"
authors:
  - "igor-kulakov"
image: "duplicate-finder-banner.png"
categories:
  - "Java"
  - "Kotlin"
tags:
related_posts:
  - "debug-without-breakpoints"
  - "how-object-reuse-can-reduce-latency-and-improve-performance"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "spring-boot-api-documentation-redocusaurus"
enlighterjs: true
frozen: false
---

Other languages: [Español](https://flounder.dev/es/posts/duplicate-finder-intro/) [한국어](https://flounder.dev/ko/posts/duplicate-finder-intro/) [Português](https://flounder.dev/pt/posts/duplicate-finder-intro/) [中文](https://flounder.dev/zh/posts/duplicate-finder-intro/)  
This post is about the development of the duplicate finder tool. For downloads and instructions on how to use it, see the 'Download' page
[Download](https://flounder.dev/duplicate-finder/)  

Anyone who worked on technical documentation in a big team is certainly aware of the content duplication issue. Even with the best tools and practices at hand, duplication is fundamentally difficult to overcome.
![Duplicate Finder for Documentation - Post banner](https://flounder.dev/img/duplicate-finder-banner.png "Duplicate Finder for Documentation - Post banner")

As the project grows in size, duplicated content will start to occur. This is especially true for big projects including many similar products or features.

*** ** * ** ***

**Good:**{#duplication-good-bad}

define once:

<pre class="EnlighterJSRAW" data-enlighter-language="html" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;p&gt; 
    If you encounter any issues, refer to the troubleshooting guide
    or contact support. 
&lt;/p&gt;</pre>

reuse elsewhere:

<pre class="EnlighterJSRAW" data-enlighter-language="html" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;TroubleshootingNote/&gt;</pre>

*** ** * ** ***

**Bad**:

<pre class="EnlighterJSRAW" data-enlighter-language="html" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">&lt;p&gt;
    If you encounter any issues, refer to the troubleshooting
    guide or contact support.
&lt;/p&gt;

&lt;!-- same meaning, slightly different wording--&gt;
&lt;p&gt;
    In case of problems, consult the troubleshooting guide
    or contact support
&lt;/p&gt;</pre>

*** ** * ** ***

The idea that advocates against duplication is commonly known as [DRY Principle](https://en.wikipedia.org/wiki/Don%27t_repeat_yourself). Though it is primarily associated with programming, the same property is highly favoured in documentation.

Project intro {#h2-0-project-intro}
-----------------------------------

Modern authoring tools typically have features for content reuse, making technical constraints less of a concern. The real problem, on the other hand, lies in spotting duplicates. Before you extract something to a reusable chunk, you need to know what to extract.

If you are a programmer, your IDE might highlight duplicate code for you:
![IntelliJ IDEA hightlights duplicated code](https://flounder.dev/img/duplicates-idea-dark.png "IntelliJ IDEA hightlights duplicated code")

Unfortunately, the same feature is not suitable for documentation, as it relies on comparing abstract syntax trees ([AST](https://en.wikipedia.org/wiki/Abstract_syntax_tree)). This approach doesn't work well with text.

One of my ongoing projects is to implement a duplicate finder for documentation. The tool will be capable of quickly finding non-exact, or *fuzzy* , matches, such as the [bad](#duplication-good-bad) example above.

Current status {#h2-1-current-status}
-------------------------------------

As of this writing, the project is WIP, but there is already a working prototype:
![The UI of the duplicate finder tool prototype showing several detected duplicates in a dummy project](https://flounder.dev/img/duplicates-finder-prototype.png)

The algorithm takes under 30 seconds to analyze a project with \~6k source files on my MBP M1, and I'm planning on improving it to instantly highlight duplicates right as you type in the editor.

The prototype has already helped me and my colleagues find a lot of duplicates in real projects, so I'm quite enthusiastic about the results and future improvements.

What's next {#h2-2-what-s-next}
-------------------------------

In the following posts, I will lay out the algorithm step-by-step and perform benchmarks to evaluate its performance. If you are into programming, you are welcome to code along.

Alternatively, you can keep an eye on the progress and use the final deliverable when the project is complete. Once finished, this feature will be available in [Writerside](https://www.jetbrains.com/writerside/), a great authoring tool made by my colleagues.

I hope that the project description resonates with you, and that you'll find the walkthrough useful. You won't miss the future chapters of this series if you regularly check out [Foojay](https://foojay.io), but it's still a good idea to subscribe to my [blog](https://flounder.dev/posts/duplicate-finder-intro/) and [Twitter](https://twitter.com/flounder4130) account.

See you in the next posts!
