---
title: "Translating a Website into 8 Languages with AI Agents in One Night"
slug: "translating-a-website-into-8-languages-with-ai-agents-in-one-night"
date: "2026-02-26T14:15:15+00:00"
lastmod: "2026-02-26T14:16:01+00:00"
description: "How I used Claude Sonnet 4.6 and fleets of GitHub Copilot Coding Agents to internationalize java.evolved — from spec to deployment java.evolved is a - by Bruno Borges"
authors:
  - "bruno-borges"
image: "Screenshot-2026-02-25-at-22.43.09.png"
categories:
  - "AI"
tags:
related_posts:
frozen: false
---

How I used Claude Sonnet 4.6 and fleets of GitHub Copilot Coding Agents to internationalize java.evolved --- from spec to deployment {#h2-0-how-i-used-claude-sonnet-4-6-and-fleets-of-github-copilot-coding-agents-to-internationalize-java-evolved-from-spec-to-deployment}
-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

*** ** * ** ***

[java.evolved](https://javaevolved.github.io) is a static site I built to showcase modern Java patterns side-by-side with their legacy equivalents. 112 patterns across 11 categories --- language, collections, streams, concurrency, and more --- each with code comparisons, explanations, and curated documentation links. All generated from YAML content files by a JBang-powered Java build script.

By the end of February 25, the entire site was English-only. By the morning of February 26, it was available in **9 languages** --- English, German, Spanish, Portuguese (Brazil), Simplified Chinese, Arabic, French, Japanese, and Korean --- with full RTL support for Arabic. The total human effort was a few hours of prompting, reviewing PRs, and filing one bug.

This is the story of that experiment.

*** ** * ** ***

The Architecture Decision: Let the AI Draft the Spec {#h2-1-the-architecture-decision-let-the-ai-draft-the-spec}
----------------------------------------------------------------------------------------------------------------

The first step wasn't writing code. It was writing a specification.

I opened [issue #74](https://github.com/javaevolved/javaevolved.github.io/issues/74) --- "Plan architectural change for i18n" --- and assigned it to a Copilot Coding Agent. The prompt was simple: propose an architectural plan for internationalizing the website, considering the existing static-site structure.

The agent ([PR #75](https://github.com/javaevolved/javaevolved.github.io/pull/75)) came back with a comprehensive i18n specification that addressed:

* **Two-layer translation model:** UI strings (labels, nav, footer) separated from content translations (pattern titles, explanations, summaries)
* **Partial translation files:** Translation files contain *only* translatable fields. Structural data (code snippets, navigation links, metadata) always comes from the English source of truth
* **Graceful fallback:** Missing translations fall back to English with a build-time warning --- no page is ever blank
* **Locale registry:** A simple `locales.properties` file drives the entire build pipeline and language selector
* **AI-friendly design:** The architecture was explicitly designed so that an AI receives the full English content and returns a partial translation file --- no field-filtering logic needed in the prompt

This last point proved prescient. The clean separation between "what changes per locale" and "what stays constant" meant each translation task was self-contained and parallelizable.

After iterating through review comments over 5 days (the original PR had 12 comments of back-and-forth), the spec was merged on February 25.

*** ** * ** ***

Phase 1: Building the Infrastructure {#h2-2-phase-1-building-the-infrastructure}
--------------------------------------------------------------------------------

With the spec in hand, I coordinated an agent locally with Coilot CLI to implement the core i18n infrastructure in [PR #83](https://github.com/javaevolved/javaevolved.github.io/pull/83) --- the generator changes that made everything locale-aware:

* The `generate.java` build script learned to iterate all locales from `locales.properties`
* Template tokens like `{{sections.codeComparison}}` replaced hard-coded English strings
* `hreflang` meta tags, a locale picker dropdown, and client-side locale detection were added
* The `resolveSnippet` function loads translated content overlaid onto the English base, falling back gracefully when a translation doesn't exist

This was the only PR that required significant intervention. Everything after it was delegation.

*** ** * ** ***

Phase 2: The First Translations (Spanish + Portuguese) {#h2-3-phase-2-the-first-translations-spanish-portuguese}
----------------------------------------------------------------------------------------------------------------

The first real translation --- Spanish --- came in [PR #84](https://github.com/javaevolved/javaevolved.github.io/pull/84). This PR also migrated the English content files from JSON to YAML for improved readability --- a format change that the AI handled naturally since the generator already supported multiple formats.

Brazilian Portuguese followed immediately in [PR #85](https://github.com/javaevolved/javaevolved.github.io/pull/85), completing all 112 pattern files (going from 3/112 translated to 112/112 --- 100% coverage). These first two translations validated the architecture and surfaced a CI issue: the deploy workflow's path triggers didn't match YAML files or the `translations/` directory. A quick fix in [PR #86](https://github.com/javaevolved/javaevolved.github.io/pull/86) resolved that.

*** ** * ** ***

Phase 3: The Fleet --- 6 Languages in Parallel {#h2-4-phase-3-the-fleet-6-languages-in-parallel}
------------------------------------------------------------------------------------------------

This is where things got interesting. With the architecture proven and two complete translations validated, I launched a **fleet of Copilot Coding Agents** --- multiple agents working in parallel, each assigned a single language. I used the Copilot CLI `/delegate` command to dispatch them asynchonously, all with the same prompt:

|                                 PR                                  |     Language      |  Agent  | Time to PR |
|---------------------------------------------------------------------|-------------------|---------|------------|
| [#87](https://github.com/javaevolved/javaevolved.github.io/pull/87) | Japanese (日本語)    | Copilot | \~36 min   |
| [#88](https://github.com/javaevolved/javaevolved.github.io/pull/88) | German (Deutsch)  | Copilot | \~35 min   |
| [#89](https://github.com/javaevolved/javaevolved.github.io/pull/89) | French (Français) | Copilot | \~32 min   |
| [#90](https://github.com/javaevolved/javaevolved.github.io/pull/90) | Chinese 中文 (简体)   | Copilot | \~58 min   |
| [#91](https://github.com/javaevolved/javaevolved.github.io/pull/91) | Arabic (العربية)  | Copilot | \~53 min   |
| [#94](https://github.com/javaevolved/javaevolved.github.io/pull/94) | Korean (한국어)      | Copilot | \~24 min   |

Each agent independently:

1. Read the i18n spec and understood the file structure
2. Created a UI strings YAML file (`translations/strings/{locale}.yaml`) with 60+ translated keys
3. Generated 112 content translation YAML files --- one per pattern, across all 11 categories
4. Registered the locale in `locales.properties`
5. Opened a PR with a detailed description of what was translated

All six PRs were opened within a span of about 12 minutes (2:33 AM to 2:45 AM) and were all merged within the next hour. **No translation conflicts**, no merge issues, no structural errors. The partial-file architecture did exactly what it was designed to do --- each agent's output was isolated to its own locale directory.

### The Numbers {#h3-5-the-numbers}

By the time the fleet finished:

* **8 locales** fully supported (besides English)
* **112 patterns × 8 translated locales = 896 content translation files**
* **8 UI string files** with 60+ keys each
* **\~1,008 generated HTML pages** (112 pages × 8 locales)
* **8 localized search indexes** (`snippets.json` per locale)

*** ** * ** ***

The Arabic Surprise: RTL Support {#h2-6-the-arabic-surprise-rtl-support}
------------------------------------------------------------------------

The most impressive moment was the Arabic translation ([PR #91](https://github.com/javaevolved/javaevolved.github.io/pull/91)). The agent not only translated, it also noted that Arabic is a right-to-left language and **proactively added RTL infrastructure**:

* Added `dir="{{htmlDir}}"` to the \`\` tag in both templates
* Modified the generator to resolve `htmlDir` to `"rtl"` for Arabic and `"ltr"` for all other locales
* Updated the Python benchmark generator to pass the new token

This was a genuinely thoughtful architectural change that I hadn't explicitly requested. The agent understood that Arabic support implies RTL layout and acted on it.

![Arabic localization on the Java Evolved website](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/w3itxwxcyyoksvae22m3.png)

### The One Bug {#h3-7-the-one-bug}

Of course, it wasn't perfect. When `dir="rtl"` is applied globally, the browser flips *everything* --- including Java source code blocks and the navigation bar, which IMO should always be LTR regardless of locale. I spotted this when reviewing the deployed site and [filed issue #96](https://github.com/javaevolved/javaevolved.github.io/issues/96).

A Copilot Coding Agent [fixed it in PR #97](https://github.com/javaevolved/javaevolved.github.io/pull/97) within minutes, adding targeted CSS overrides:

```css
[dir="rtl"] nav,
[dir="rtl"] .nav-inner {
  direction: ltr;
}

[dir="rtl"] pre,
[dir="rtl"] code,
[dir="rtl"] .code-text,
[dir="rtl"] .card-code,
[dir="rtl"] .compare-code {
  direction: ltr;
  text-align: left;
  unicode-bidi: embed;
}
```

Arabic prose continues to render RTL. Java code stays LTR. One bug, one issue, one PR, fixed in minutes. That's the entire human intervention required for 8-language support.

*** ** * ** ***

Why It Worked: Architecture as Force Multiplier {#h2-8-why-it-worked-architecture-as-force-multiplier}
------------------------------------------------------------------------------------------------------

This experiment succeeded because of deliberate architectural decisions, not just because of a magic AI prompt. A few design choices made parallelized AI translation almost trivially easy:

### 1. Source of Truth Separation {#h3-9-1-source-of-truth-separation}

Content files contain both translatable text and structural data (code, navigation, metadata). But translation files contain *only* the translatable fields. The generator overlays translations onto the English base at build time. This means:

* AI agents can't accidentally modify code snippets or break navigation
* Translation files are small and self-contained
* Structural changes to the English source propagate immediately without needing to update translations

### 2. Partial File Fallback {#h3-10-2-partial-file-fallback}

If a pattern has no translation file for a locale, the site shows the English content with an "untranslated" banner. This means:

* Partial translations are always valid
* New patterns added in English are immediately visible in all locales
* There's no "all or nothing" pressure on translation completeness

### 3. Convention Over Configuration {#h3-11-3-convention-over-configuration}

Each locale follows the same directory structure. Adding a new language is a checklist:

1. Add a line to `locales.properties`
2. Create `translations/strings/{locale}.yaml`
3. Create files under `translations/content/{locale}/`

No code changes needed in the generator. No template modifications. The AI spec anticipated this workflow, and the agents followed it exactly.

*** ** * ** ***

Lessons Learned {#h2-12-lessons-learned}
----------------------------------------

### What Went Well {#h3-13-what-went-well}

* **Spec-first approach:** Having the AI draft the specification before any implementation meant the architecture was explicitly designed for AI-driven translation from day one
* **Fleet parallelism:** Six language translations running simultaneously with zero conflicts demonstrates that well-isolated task boundaries enable effortless parallelization
* **Proactive problem-solving:** The Arabic agent identifying and implementing RTL support without being explicitly asked shows that LLMs can reason about the downstream implications of their translations
* **Technical term preservation:** Across all 9 languages, Java API names, annotations, JDK versions, and code examples were correctly left in English --- the agents understood what should and shouldn't be translated in a technical context

### What Required Human Intervention {#h3-14-what-required-human-intervention}

* **One bug:** The RTL global flip affecting code blocks. This is a classic CSS gotcha that even experienced developers might miss on first pass. Filed as an issue, fixed by an agent in minutes
* **CI pipeline fixes:** The deploy workflow needed path trigger updates after the JSON→YAML migration. A routine ops fix
* **Review and merge:** Each PR needed a human to review and merge. The content quality was consistently good, but spot-checking translations in languages you speak is still important

*** ** * ** ***

The Timeline {#h2-15-the-timeline}
----------------------------------

|      Time (UTC)      |                                          Event                                          |
|----------------------|-----------------------------------------------------------------------------------------|
| Feb 20, 20:33        | Issue #74 opened --- "Plan architectural change for i18n"                               |
| Feb 20, 20:33        | PR #75 opened --- i18n specification (by Copilot Agent)                                 |
| Feb 25, 20:56        | PR #75 merged (after 5 days of procrastination, and other projects at hand... finally!) |
| Feb 25, 23:49        | PR #83 merged --- core i18n infrastructure                                              |
| Feb 26, 01:28        | PR #84 merged --- Spanish translation + JSON→YAML migration                             |
| Feb 26, 02:27        | PR #85 merged --- Complete Brazilian Portuguese (112/112)                               |
| Feb 26, 02:29        | PR #86 merged --- CI path trigger fix                                                   |
| Feb 26, 02:33--02:45 | PRs #87--#91 opened --- fleet of 6 language agents launched                             |
| Feb 26, 03:09--03:39 | All fleet PRs merged (Japanese, German, French, Chinese, Arabic)                        |
| Feb 26, 03:51        | PR #94 opened --- Korean (final language)                                               |
| Feb 26, 03:59        | Issue #96 filed --- Arabic RTL bug                                                      |
| Feb 26, 04:12        | PR #97 merged --- RTL fix                                                               |
| Feb 26, 04:15        | PR #94 merged --- Korean complete                                                       |

**From spec merge to 8-language site: \~7 hours.** From fleet launch to all 6 languages merged: **\~1 hour.**

*** ** * ** ***

Conclusion {#h2-16-conclusion}
------------------------------

The experiment proved a simple thesis: **if you design your architecture for AI-driven workflows, AI agents can do remarkable things.**

I didn't build a custom translation pipeline or prompt-engineering framework. I wrote a specification. An AI agent drafted it, I refined it through code review, and then a fleet of agents executed against it. The architecture --- partial files, fallback behavior, source-of-truth separation --- did all the heavy lifting.

The result is a Java patterns reference site available in 8 languages, with 1,008 generated pages, serving developers who speak English, German, Spanish, Portuguese, Chinese, Arabic, French, and Korean. It handles right-to-left layouts. It has localized search. Every page has proper `hreflang` tags for SEO.

And the only bug was a CSS rule that took 13 minutes from report to fix.

The future isn't AI replacing developers. It's developers designing systems that let AI do what it's good at --- repetitive, structured, parallelizable work --- while humans focus on architecture, review, and the occasional RTL bug.

*** ** * ** ***

*java.evolved is open source at [github.com/javaevolved/javaevolved.github.io](https://github.com/javaevolved/javaevolved.github.io). The i18n specification lives at [specs/i18n/i18n-spec.md](https://github.com/javaevolved/javaevolved.github.io/blob/main/specs/i18n/i18n-spec.md).*
