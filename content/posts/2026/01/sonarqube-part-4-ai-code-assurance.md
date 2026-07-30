---
title: "SonarQube AI Code Assurance & MCP: Auto-Fix Java (Part 4)"
slug: "sonarqube-part-4-ai-code-assurance"
date: "2026-01-19T15:27:14+00:00"
lastmod: "2026-01-19T15:27:16+00:00"
description: "Secure Java projects with SonarQube AI Code Assurance. Use AI CodeFix and the MCP Server to auto-remediate technical debt instantly."
authors:
  - "jonathan-vila"
image: "https://foojay.io/wp-content/uploads/2026/01/Gemini_Generated_Image_d9mrmpd9mrmpd9mr.png"
categories:
  - "AI"
  - "Developer Tools"
  - "Security"
tags:
related_posts:
  - "tiberius-a-security-testing-framework-for-llm-applications-in-java"
  - "boxlang-ai-3-2-0-image-generation-web-search-fluent-audio-agent-registry-mcp-observability"
  - "the-5-knights-of-the-mcp-apocalypse"
  - "jc-ai-newsletter-9"
frozen: false
---

Hola Java developers! 👋

Welcome to the **Grand Finale** of our series.

* **[Part 1](https://foojay.io/today/developers-guide-to-sonarqube-part-1/)**: We turned your IDE into a fortress.
* **[Part 2](https://foojay.io/today/developers-guide-to-sonarqube-part-2/)**: We synced the team with Connected Mode.
* **[Part 3](https://foojay.io/today/avoid-the-trojan-horse-in-your-pom-xml-sonarqube-advanced-security-part-3/)**: We secured the Supply Chain (dependencies).

We have become excellent at *finding* bugs. But let's be honest: Finding them is only half the battle. **Who is going to fix them?**

We are drowning in a backlog of "Technical Debt," "Code Smells," and "Security Hotspots." You don't have enough hours in the day to refactor every complex method or research the perfect fix for a regex denial-of-service vulnerability.

This is **Part 4** . Today, we stop "finding" and start **"auto-remediating"** using the new AI superpowers in SonarQube.

*** ** * ** ***

**Problem #1: "I see the bug, but I'm too lazy (or busy) to fix it"** {#h2-0-problem-1-i-see-the-bug-but-i-m-too-lazy-or-busy-to-fix-it}
----------------------------------------------------------------------------------------------------------------------------------------

You are in IntelliJ. SonarQube highlights a block of code with high Cognitive Complexity.

You sigh. You know you should refactor it, but untangling those nested if/else statements will take you 20 minutes, and you have a deadline. So, you ignore it.

**The Solution:** [AI CodeFix](https://www.sonarsource.com/solutions/ai/ai-codefix/) (The "Magic Button").

SonarQube is no longer just a spellchecker; it is an autocorrect.

Whether you are in IntelliJ (via Connected Mode) or reviewing a Pull Request in the SonarQube dashboard, you will see a new button: "Generate AI Fix".

* **How it works:** It analyzes the specific issue and your code context using a deterministic static code analysis and it creates a fix using an LLM (like GPT-4o).
* **The Result:** It proposes a complete code change that fixes the bug *without* breaking the logic.
* **Your Job:** You just review the diff and click **"Apply"**.

What used to take 20 minutes of refactoring now takes 10 seconds of reviewing.
![](/images/posts/2026/01/sonarqube-part-4-ai-code-assurance/Screenshot-2026-01-19-at-15.23.11.png)

We can see here the issue and the execution flow, and the "Generate AI Fix" button
![](/images/posts/2026/01/sonarqube-part-4-ai-code-assurance/Screenshot-2026-01-19-at-15.24.02.png)

And the solution suggested by SonarQube AI CodeFix feature
![](/images/posts/2026/01/sonarqube-part-4-ai-code-assurance/Screenshot-2026-01-19-at-15.24.15.png)

And finally this is the way we see the change coming from SonarQube AI CodeFix in IntelliJ IDE

*** ** * ** ***

**Problem #2: "My AI Assistant writes buggy code because it doesn't know our rules"** {#h2-1-problem-2-my-ai-assistant-writes-buggy-code-because-it-doesn-t-know-our-rules}
---------------------------------------------------------------------------------------------------------------------------------------------------------------------------

You are using Cursor, Windsurf, or Claude Desktop to generate a new Spring Boot Service.

The AI writes valid Java code, but it violates some specific rules:

* *"Don't use Field Injection."*
* *"Always use the* *var* *keyword."*
* *"Copyright headers must be present."*

You copy-paste the AI code, and immediately, SonarQube yells at you. You waste time fixing what the AI messed up.

**The Solution:** [SonarQube MCP Server](https://www.sonarsource.com/products/sonarqube/mcp-server/) (Model Context Protocol).

This is a game-changer. **MCP** is an open standard that allows AI Assistants (like Claude or Cursor) to "talk" to your tools.

By enabling the SonarQube MCP Server, you are effectively giving your AI Assistant the confidence of deterministic tools.

Before generating code, your AI agent can query SonarQube to understand the active rules and project context.

* **The Scenario:** You ask Cursor: *"Refactor this class."*
* **The Background Magic:** Cursor asks SonarQube: *"What are the rules for this project?"*
* **The Outcome:** The AI generates code that is *already* compliant with your Quality Profile. No red squiggles. No rework.

Read these extended articles about [MCP](https://foojay.io/today/lets-talk-about-mcp/) , [SonarQube MCP Server](https://foojay.io/today/your-new-ai-powered-coding-buddy-a-guide-to-sonarqube-mcp-server-on-intellij-%f0%9f%a4%96/) and [MCP Security pitfalls](https://foojay.io/today/the-5-knights-of-the-mcp-apocalypse/).
![](/images/posts/2026/01/sonarqube-part-4-ai-code-assurance/Screenshot-2026-01-19-at-15.24.27-1024x164.png)

Asking the agent to produce code but connecting it to SonarQube MCP Server in order to be sure it doesn't contain issues.

*** ** * ** ***

**Problem #3: "How do I know if this Project with AI-generated code is actually good?"** {#h2-2-problem-3-how-do-i-know-if-this-project-with-ai-generated-code-is-actually-good}
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Your team is using GitHub Copilot heavily. Productivity is up 30%. But... are they generating high-quality code, or just generating more bugs faster?

The "Black Box" of AI code is a major anxiety for Tech Leads.

**The Solution:** [AI Code Assurance](https://www.sonarsource.com/solutions/ai/ai-code-assurance/).

SonarQube allows you to tag projects that use Generative AI or Autodetects AI-generated content (at the moment only considering Github Copilot projects). It then enforces a specific, stricter **"AI Code Assurance"** process.
![](/images/posts/2026/01/sonarqube-part-4-ai-code-assurance/Screenshot-2026-01-19-at-15.24.38.png)

It will assign the "Sonar way" quality profile to the project, that checks for:

1. **No New Issues:** Zero tolerance for bugs in AI code.
2. **Security:** All hotspots must be reviewed (AI loves to hallucinate insecure config).
3. **Tests:** High coverage requirements (because AI code is only as good as its tests).

When a project passes this strict gate, it earns the **"AI Code Assurance"** badge. It's a seal of approval that tells management: *"Yes, we used AI, and yes, we verified it's safe."*
![](/images/posts/2026/01/sonarqube-part-4-ai-code-assurance/Screenshot-2026-01-19-at-15.24.49-1024x197.png)

*** ** * ** ***

**🎯 Series Summary: The Complete Cycle** {#h2-3-series-summary-the-complete-cycle}
-----------------------------------------------------------------------------------

We have come a long way.

1. **Part 1 (The Developer):** We installed **SonarQube for IDE** to catch bugs locally.
2. **Part 2 (The Team):** We used **Connected Mode** to sync rules and enforce Quality Gates in CI/CD.
3. **Part 3 (The Supply Chain):** We used **Advanced Security** to catch vulnerable dependencies and deep injection attacks.
4. **Part 4 (The Future):** We used **AI CodeFix and MCP** to automate the cleanup and guide our AI assistants.

**The Conclusion?**

Quality is not about slowing down to fix bugs.

It's about building a pipeline where the IDE coaches you, the Server protects you, and the AI can help you.

Stop fixing bugs on Fridays. Let the tools do the work, so you can focus on building the next big feature.

**Happy Coding!** 🚀☕️
