---
title: "Intro to the BoxLang Formatter"
slug: "intro-to-the-boxlang-formatter"
date: "2026-05-28T17:45:09+00:00"
lastmod: "2026-05-28T17:49:19+00:00"
description: "You know the drill. Someone opens a PR and half the review comments are about tabs vs spaces, where braces go, or why that one function has its arguments - by Cristobal Escobar"
authors:
  - "cristobal-escobar"
image: "https://foojay.io/wp-content/uploads/2026/05/formatter.png"
categories:
  - "BoxLang"
  - "Developer Tools"
  - "Java"
  - "Tools"
tags:
related_posts:
enlighterjs: true
frozen: false
---

![](/images/posts/2026/05/intro-to-the-boxlang-formatter/boxlang-formatter-700x394.jpg)

You know the drill. Someone opens a PR and half the review comments are about tabs vs spaces, where braces go, or why that one function has its arguments formatted differently from everything else. It's noise. And it's over.

**The BoxLang Formatter is here, and it handles all of that for you.**

You can find the docs here: <https://boxlang.ortusbooks.com/getting-started/ide-tooling/boxlang-formatter>

The BoxLang Formatter is a built-in code formatting tool that ships with BoxLang. It enforces consistent style across `.bx`, `.bxs`, `.bxm`, `.cfm`, `.cfc`, and `.cfs` files --- automatically.

It's not a linter. It doesn't just complain. It *fixes* your code, or tells CI to fail when style drift sneaks in.

If you have BoxLang installed, you already have the formatter. No extra install needed.

Format everything in your current directory:

<pre class="EnlighterJSRAW" data-enlighter-language="java">boxlang format
</pre>

That's it. It recurses through your project and rewrites supported files in place.

Want to target a specific path or file?

<pre class="EnlighterJSRAW" data-enlighter-language="java"># A directory
boxlang format --source ./src

# A single file
boxlang format --source ./models/User.bx
</pre>

Multiple paths at once (v1.14+):

<pre class="EnlighterJSRAW" data-enlighter-language="java">boxlang format --source commands,models,services
</pre>

The formatter works great out of the box with sensible defaults, but you can customize it with a `.bxformat.json` file in your project root.

Bootstrap one instantly:

<pre class="EnlighterJSRAW" data-enlighter-language="java">boxlang format --initConfig
</pre>

This drops a starter config in your current directory. From there, tweak what you care about. Here's a minimal example:

<pre class="EnlighterJSRAW" data-enlighter-language="java">{
  "maxLineLength": 120,
  "tabIndent": true,
  "singleQuote": false,
  "braces": {
    "style": "same-line",
    "require_for_single_statement": true
  },
  "operators": {
    "comparison_style": "symbols"
  }
}
</pre>

You've got control over indentation, line length, brace style, struct/array formatting, operator style, SQL keyword casing, import sorting, and a lot more. Only override what you need --- everything else uses sensible defaults.

This is where it gets really useful. Run the formatter in check mode as a quality gate:

<pre class="EnlighterJSRAW" data-enlighter-language="java">boxlang format --check --source ./
</pre>

* Exits `0` if everything is already formatted correctly
* Exits non-zero if any file has drift

Drop that into your CI pipeline and pull requests with messy formatting simply won't merge. One command, no separate linter needed.

### Recommended Team Workflow {#h3-0-recommended-team-workflow}

* Developers run `boxlang format` before pushing
* CI runs boxlang `format --check` on every PR
* PRs that fail must reformat before merge

No more style debates in code review. The formatter wins.

If you want formatting to happen automatically as you work, the BoxLang LSP supports experimental format-on-save.

**Step 1** - Enable it in `.bxlint.json`:

<pre class="EnlighterJSRAW" data-enlighter-language="java">{
  "formatting": {
    "experimental": {
      "enabled": true
    }
  }
}
</pre>

**Step 2** - Add this to your VS Code `settings.json`:

<pre class="EnlighterJSRAW" data-enlighter-language="java">{
  "[boxlang]": {
    "editor.formatOnSave": true
  },
  "[boxlang-template]": {
    "editor.formatOnSave": true
  }
}
</pre>

**Step 3** - Open the Command Palette and run:

* `BoxLang: Select BoxLang Version` (pick latest)
* `BoxLang: Select LSP Version` (pick latest)
* `Developer: Reload Window`

Save a `.bx` file and it just formats. Local fast feedback, CI enforcement as the source of truth.

Already using cfformat in your project? Migration is a two-step process, and your existing style intent is preserved.

**Step 1 - Convert your config:**

<pre class="EnlighterJSRAW" data-enlighter-language="java">boxlang format --convertConfig --source ./
</pre>

This transforms your `.cfformat.json` into a `.bxformat.json`, keeping your rules intact.

**Step 2 - Validate with check mode:**

<pre class="EnlighterJSRAW" data-enlighter-language="java">boxlang format --check --source ./
</pre>

See what (if anything) drifted. Run the formatter once in a cleanup commit, then turn on `--check` in CI and you're done.

**Preview without rewriting files** --- pipe output to stdout instead:

<pre class="EnlighterJSRAW" data-enlighter-language="java">boxlang format --overwrite false --source ./handlers/MainHandler.cfc
</pre>

**Exclude directories** (v1.14+):

<pre class="EnlighterJSRAW" data-enlighter-language="java">boxlang format --source . --excludes generated,vendor
</pre>

**Use a custom config path:**

<pre class="EnlighterJSRAW" data-enlighter-language="java">boxlang format --config ./config/.bxformat.json --source ./
</pre>

Stop spending review cycles on style. The formatter handles it --- in your editor, in your pre-commit hook, in CI. One command, consistent output, zero arguments about semicolons ever again.

Go format something:

<pre class="EnlighterJSRAW" data-enlighter-language="java">boxlang format
</pre>

Questions? Hit us up on [Community \& Support](https://community.ortussolutions.com/ "Community &amp; Support") or open a discussion on the [BoxLang repo](https://github.com/ortus-boxlang/BoxLang "BoxLang repo"). We'd love to hear how you're using it.
