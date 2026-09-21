---
title: "Writing an agent skill"
date: "2026-03-17T15:36:30+00:00"
lastmod: "2026-09-21T05:56:51+00:00"
description: "Most developers now use coding assistants. I do too—Copilot at work, Claude Code at home. As a developer, I prefer not to repeat myself. This post…"
canonical: "https://blog.frankel.ch/writing-agent-skill/"
authors:
  - "nicolas-frankel"
image: "cover_large-1.jpg"
categories:
  - "AI"
related_posts:
frozen: false
---

Most developers now use coding assistants. I do too—Copilot at work, Claude Code at home. As a developer, I prefer not to repeat myself. This post explains why and how to avoid repetition as a skill.

## Don't Repeat Yourself

The principle has been present in the software development field for ages. The idea is that if you copy and paste code in multiple places and a bug appears, you'll need to fix the bug in all these places. The more the number of duplications, the more chances you'll miss one of them when fixing.

In the context of coding assistants, DRY means something entirely different. From a personal point of view, it means you don't need to ask the assistant to analyze your project at every session. Or to reference your language's coding conventions again. Or that your project favors immutability.

When a new developer joins a good project, they are told what the project's features, architecture, and conventions are. In great projects, conventions are formalized in written form and sometimes even kept up-to-date. Your coding assistant is a team member like any other: it **requires** written documentation.

## Less is more

Both coding assistants I use allow the use of such instructions. I assume all do. For example, [GitHub Copilot](https://docs.github.com/en/copilot/how-tos/configure-custom-instructions/add-repository-instructions) automatically reads the `.github/instructions.md` file.

The problem with such instructions is that the assistant may read them automatically. If they are too broad and don't apply to the tasks, you'll pollute the context with irrelevant data. Conclusion: you need to keep the file relatively *small*. Thus, focus file instructions on general information.

Sizing advices differ depending on the exact source. For example, [docs.factory.ai](https://docs.factory.ai/cli/configuration/agents-md#7-%C2%B7-best-practices) mentions less than 150 lines. [A Q\&A on Reddit](https://www.reddit.com/r/GithubCopilot/comments/1lvvqrd/is_copilot_agent_really_reading_my_700_line/) clarifies that 700 lines is too long. My advice would be to start small, increment when needed, analyze results, and refactor when it grows too large.

## Token economics

Today, critical resources aren't CPU, RAM, or storage, but tokens. Tokens are a finite and expensive resource. My opinion is that soon, developers will be measured on their token usage: the better one will be the one using the fewest tokens to achieve similar results.

Most agents will load the default instructions file, `AGENTS.md`. Claude will load `CLAUDE.md`. The smaller the file, the fewer tokens used in the context.

A good context contains all the necessary tokens, but not more. That's where skills come into play.

## Skills

> Agent Skills are folders of instructions, scripts, and resources that agents can discover and use to do things more accurately and efficiently.
>
> — [Agent skills](https://agentskills.io/home)

Skills are different from `AGENTS.md` in that they aren't loaded automatically. Hence, they don't bloat the context. Now comes the fun part: depending on the coding assistant, it may discover the skill and load it automatically, or not.

I will describe how Claude works. I think its approach is superior, so I guess other assistants will implement it soon.
> By default, both you and Claude can invoke any skill. You can type `/skill-name` to invoke it directly, and **Claude can load it automatically when relevant to your conversation**.
>
> — [Control who invokes a skill](https://code.claude.com/docs/en/skills#control-who-invokes-a-skill)

Claude knows when to invoke a skill by transforming all `SKILL.md` files' front matter into tools. Here's the front matter of the Kotlin skill I created:

```yaml
---
name: kotlin
description: |
  Use this skill when working with Kotlin code in any capacity:
  - Reading, writing, editing, or reviewing Kotlin files (*.kt, *.kts)
  - Running Gradle tasks for Kotlin modules
  - Writing or debugging Kotlin/JS code that targets Node.js
  - Working with external JavaScript libraries from Kotlin
  - Writing tests for Kotlin code (@Test, @BeforeTest, @AfterTest)
  - Setting up dependency injection or mocking in Kotlin
  - Dealing with multiplatform Kotlin projects (common, jsMain, jsTest, jvmMain)
  - Troubleshooting Kotlin compilation or runtime errors
  - Any task involving Kotlin/JS modules targeting Node.js

  This skill provides Kotlin/JS technical knowledge (especially JS interop gotchas)
  and coding style preferences beyond the official conventions.
---
```

The above gives Claude information on when to invoke the tool. Obviously, the skill is about Kotlin and Kotlin/JS.

For the content, follow the same rules as regular `AGENTS.md` files:

* You can reference other documents, textual and others, local in the same skill folder or accessible online. I have copied and pasted the whole [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html) in a dedicated file. `SKILL.md` summarizes the main items and links to the conventions.

```markdown
The full conventions are cached locally in this skill directory at kotlin-coding-conventions.md.

Key reminders:
- Prefer val over var - immutability first
- Use immutable collection interfaces (List, Set, Map, not MutableList, etc.)
- Use it for short lambdas, named parameters for nested/complex ones
- Prefer expression form of if, when, try over statement form
- Prefer functional style (filter, map) over imperative loops
```

* You should structure the document using headings, subheadings, and lists.
* You should use *good* and *bad* examples.

````markdown
### 1. External Interfaces vs Kotlin Classes

**Problem**: Kotlin classes have methods on their prototype. External interfaces expect methods directly on the object. You cannot use unsafeCast to convert between them.

```kotlin
// ❌ WRONG: This will fail at runtime class
MockSql {
fun unsafe(query: String) = ... 
}
val sql: Sql = mockSql.unsafeCast() // Will throw: sql.unsafe is not a function

// ✅ CORRECT: Use extension function to build plain JS object
fun MockSql.toExternal(): Sql {
val obj = Any().asDynamic()
val mock = this

obj.unsafe = { query: String -> mock.unsafe(query) }
obj.end = { mock.end() }

return obj.unsafeCast()
}
// Then use it:
val sql: Sql = mockSql.toExternal()
```
````

* etc.

## Conclusion

Skills are great for avoiding repeating the same instructions over and over. The main difference with instructions is that they don't bloat the context for nothing. Depending on your coding assistants, they may trigger when required, or you may need to explicitly activate them.

The complete source code for this post can be found on [Codeberg](https://codeberg.org/nfrankel/skills).

**To go further:**

* [Agent Skills](https://agentskills.io/home)
* [Extend Claude with skills](https://code.claude.com/docs/en/skills)
* [Methodical Development Skill](https://codeberg.org/ai-skills/methodical-dev/src/branch/main/en/methodical-dev/SKILL.md)
* [Awesome Skills](https://github.com/sickn33/antigravity-awesome-skills)
* [AGENTS.md](https://agents.md/)

*Originally published at [A Java Geek](https://blog.frankel.ch/writing-agent-skill/) on March 15^th^, 2026.*

*[DRY]: Don't Repeat Yourself
