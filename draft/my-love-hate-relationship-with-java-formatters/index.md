---
title: "My love-hate relationship with Java formatters"
date: "2026-09-23"
description: "Learning to love google-java-format, why AI agents make formatters matter even more, and why palantir-java-format now has a fork: open-java-format."
authors:
  - "abashev"
image: "open-java-format-cover.jpg"
categories:
  - "Developer Tools"
  - "Java"
  - "Opinion"
related_posts:
  - "enterprise-java-quality-gates-ai"
  - "intro-to-the-boxlang-formatter"
  - "why-picnic-picked-java"
  - "embracing-java-17-heres-what-we-learned-at-picnic"
---

Disclaimer: I don't want to turn this into "Java formatters week", but I started my own project a while ago, and I think it's time for it to come out of stealth mode.

I've been writing Java for many, many years, and the Sun Code Conventions were my favorite bedside reading. I've seen plenty of variations over the years, and even after they became the Oracle Code Conventions, they were still the same few-page guide on how to write a hello-world app with the standard class library. For some reason I never took to the Google conventions: too C-flavored, with tiny indents, too few columns on the screen, and so on.

Later I had to work a lot with Terraform and Go, and you can't imagine how liberating it was to just write code without thinking about questions like "does the dot go at the start of the line or at the end?", "how many spaces for parameters?", "do we put the brace on the same line?" and many, many more. And all those review comments about wrong indentation... Then I joined a project that used `google-java-format` as a CI quality gate. I hated it for the first month, and after that I couldn't live without it. All that knowledge of rituals and voodoo dolls was wiped out, a lot of code review friction disappeared, and a good chunk of my brain was freed up because I no longer had to decipher every developer's handwriting (and yes, I believe every developer has their own). Then I discovered `palantir-java-format`, a fork of `google-java-format` with a much more compact layout, brilliant handling of reactive code, and a native binary that runs in milliseconds.

But nothing lasts forever. New Java versions keep coming out, yet open issues in these projects never change status, PRs sit forever with red checks, and the last few releases look like chores synced with internal infrastructure. That matters a lot to me as a formatter fan. Whenever I join a new project and want to introduce a Java formatter, it's always the same two-question plot. What's it called? Palantir? Hmm... And who's actually building the artifacts?

And with AI it's become even worse. Every model, and every session, has its own idea of code style, no matter how precisely you describe everything in your `.md` files. One agent wraps every parameter onto its own line, another crams an entire stream pipeline into a single 200-character line, and a third "helpfully" reformats files it was never asked to touch, so a two-line fix shows up as a 400-line diff of whitespace noise. Then CI rejects it, the agent "fixes" it back its own way, and the loop begins again.

That's why I forked Palantir's repository and gave it a serious facelift. It's not a prototype, it's a working release with an IntelliJ IDEA plugin, Gradle integration, and native binaries for all major platforms. I moved the whole CI infrastructure to GitHub Actions, added artifact signing, added support for Java 27, and fixed some of the most annoying bugs from the Google and Palantir issue trackers. Next up: replacing all custom dependencies with open-source alternatives, fixing Error Prone findings, and a bit of dogfooding, meaning formatting the entire codebase with the formatter itself. I also have a few ideas for making code with reactive libraries and maps more expressive, but those will wait for the next major release. All 2.98.0.x releases are strictly for bug fixes and internal refactoring.

Documentation: [openjavaformat.dev](https://openjavaformat.dev/)  
GitHub repository: [github.com/openjavaformat/open-java-format](https://github.com/openjavaformat/open-java-format)

I'd love to hear what you think, so any feedback is welcome!
