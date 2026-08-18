---
title: "Why Java Developers Over-Trust AI-Generated Code"
date: "2026-04-09T10:45:36+00:00"
lastmod: "2026-04-09T18:22:21+00:00"
description: "AI coding tools sound confident even when they're wrong. Here's the psychology behind why Java developers accept bad suggestions — and habits that help."
authors:
  - "steve-poole"
image: "Gemini_Generated_Image_zu1ebzu1ebzu1ebz-1024x559.png"
categories:
  - "Java"
  - "Security"
tags:
related_posts:
  - "build-secure-ai-chat-applications-with-boxlang-rag-ollama-and-amazon-bedrock-with-dan-card"
  - "spring-ai-agents-no-second-runtime"
  - "jc-ai-newsletter-16"
  - "tiberius-a-security-testing-framework-for-llm-applications-in-java"
frozen: false
---

*This article is adapted from [The Confidence Trap](https://noregressions.substack.com/p/the-confidence-trap-why-developers), part of the "2026 Supply Chain Reckoning" series on my No Regressions newsletter.*

Your boss calls you on a Friday afternoon. He's read all the available data, he tells you with absolute confidence, and he's decided that migrating from Spring Boot 3.5 to 4.0 will be straightforward. Wants it done over the weekend.

You'd push back. You'd ask which data. You'd point out the breaking changes.

Now replace your boss with Copilot. It suggests a code change: a refactored method, a new dependency, an implementation pattern. The recommendation arrives in that polished, authoritative tone we are all used to. No hedging. No "I'm not sure about this one." Just clean, confident code. Do you review it with the same rigour you'd apply to your boss's claim, or do you accept?

Sonatype's 2026 State of the Software Supply Chain report found that nearly 30% of LLM-generated dependency recommendations reference non-existent package versions. Somewhere between one in three and one in four. And that's just dependencies, the same confidence problem applies to every line of AI-generated code.

So why do we keep accepting it?

## Your Brain Is Working Against You

The short answer is psychology. Specifically, something called the *fluency heuristic*: when information is easy to process, your brain treats it as more likely to be true.

Organisational psychologist Tomas Chamorro-Premuzic demonstrated that there's virtually no relationship between how competent people *appear* and how competent they *actually are*. We promote confident leaders over capable ones. We trust the colleague who speaks first and sounds certain, even when the quiet one in the corner has the better answer.

Large language models exploit this wiring perfectly. They produce fluent, structured, confident-sounding output every time. A well-formatted code block *feels* right. The class names follow conventions. The exception handling looks sensible. Your brain pattern-matches against thousands of similar blocks you've seen before and says: fine, move on.

Psychologists at Carnegie Mellon found that LLMs hallucinated in 69% to 88% of legal queries while maintaining a tone that consistently misled even trained evaluators. The packaging was so good that experts couldn't reliably spot the fakes.

## Where Java Developers Are Most Exposed

This isn't an abstract concern. Java's ecosystem has characteristics that make several areas particularly vulnerable to confident-sounding AI output.

**Dependencies are easy to hallucinate convincingly.** Maven Central is vast and version-dense. An LLM can suggest `org.apache.commons:commons-csv` when it means `org.apache.commons:commons-text`, or generate a coordinate like `commons-utils` that follows the naming convention perfectly but doesn't exist. The patterns are regular enough to hallucinate convincingly. And a one-word difference is all an attacker needs to register a *slopsquatted* package. Lasso Security found that a single hallucinated package name received over 30,000 genuine downloads in three months.

**Transitive dependencies are invisible by default.** Your `pom.xml` might declare a few dozen dependencies. Maven resolves hundreds. An AI suggesting a top-level change has no visibility into what that cascades through your transitive tree. It can't know that upgrading `spring-cloud-openfeign` pulls in a vulnerable version of `commons-fileupload` through `feign-form`. Which is exactly what happened with CVE-2025-48976.

**Boilerplate code looks right even when it's wrong.** Java's verbosity means a lot of AI-generated code is structural. Configuration classes, Spring annotations, repository patterns, DTO mappings. These follow templates closely enough that an LLM can produce them fluently. But "it compiles and follows the pattern" doesn't mean it's correct. A `@Transactional` annotation on the wrong method. A `SecurityFilterChain` that looks complete but leaves an endpoint exposed. An `ObjectMapper` configuration that silently drops unknown fields. The code reads well. The bugs hide in the semantics, not the syntax.

**API usage patterns are plausible but outdated.** An LLM trained on older codebases will confidently suggest deprecated APIs, removed methods, or patterns that worked in Java 11 but behave differently in Java 21. It doesn't know your runtime version or your Spring Boot version. Code that compiled fine against last year's stack may not compile against this year's.

## Your Toolchain Catches Some of This

If an AI hallucinates a Maven coordinate that doesn't exist, your build fails. `mvn compile` resolves every dependency against Maven Central. IntelliJ underlines it red before you even run the build. Deprecated API calls get warnings. Type errors fail at compile time.

So the obvious mistakes are caught. Your toolchain handles those.

The harder problems are the ones that pass the build. The dependency that *does* exist but has a known CVE. The code that compiles but has a subtle security flaw. The pattern that works but creates a performance bottleneck under load. Green tick. No warnings.

For dependencies specifically, that verification gap needs:

* `mvn dependency:tree -Dverbose` to see what shifted in your transitive tree
* OWASP's `dependency-check-maven` or tools like Snyk and Sonatype Lifecycle scanning against known vulnerabilities in CI
* \`\` or Gradle platform constraints to pin transitive versions explicitly

For generated code more broadly: code review with the same rigour you'd apply to a pull request from a stranger. The AI is a contributor you've never worked with before. Its code compiles. Its suggestions are well-formatted. But you have no track record to trust.

## Make the Model Show Its Working

Toolchain checks catch problems after the fact. But you can also change how you interact with the model to reduce false confidence at the source. There are habits that can help shift the dynamic. Worth trying, not guaranteed to work. (aka - works for me)

**1: Ask it what it doesn't know.** Before accepting a suggestion, ask: "What assumptions are you making about my project?" or "What are you uncertain about here?" Hopefully, the model will list the caveats it'd otherwise skip. A model that admits "I don't know your Java version or your Spring Boot version" is being more useful than one that silently guesses.

**2: Give it your actual context.** The less a model has to infer, the less it fabricates. Don't just say "write a REST controller." Paste your existing code, your `pom.xml`, your Spring Boot version, your constraints. A model working with real context has far less room to hallucinate.

**3: Ask for alternatives and trade-offs.** If the model can only suggest one approach, that's a warning flag. Ask: "What other ways could I do this? What are the trade-offs?" Even if the model confidently recommends three mutually contradictory approaches, it's still showing you something useful about where it's guessing.

**4: Verify the reasoning, not just the output.** Ask "why this approach?" If the answer is vague ("it's best practice") or circular ("it's the recommended pattern"), the model is probably hallucinating. A good recommendation has a specific rationale: compatibility with your stack, a security consideration, a performance characteristic.

**5: Treat the first answer as a draft.** The human expert heuristic hits hardest on the first pass. The code looks good; it makes sense in your head, and your instinct is to paste it in. Train yourself to treat every AI suggestion as a starting point, not a finished answer.

## The Confidence Tax

None of this eliminates the confidence trap. But it shifts the dynamic from a model that delivers and a developer who accepts to a conversation where the model has to justify itself.

This bit is scary - take a moment to see if you're affected. Microsoft surveyed knowledge workers and found that the more they use AI tools, the less critical effort they report applying. The "irony of automation": the tool handles the routine thinking, and in doing so, erodes the very skill you need to catch it when it's wrong!

For developers, this is the real risk. Not that AI will suggest something catastrophically wrong on day one. But that the habit of accepting well-formatted, confident-sounding code without checking will become the default.

The tools are useful. The confidence they project is not earned. Build the habit of checking now, while it still feels unnecessary.

*For the full psychology behind the confidence trap, including the research on status-enhancement theory and automation bias, read [The Confidence Trap](https://noregressions.substack.com) on the No Regressions newsletter.*

*This article is part of the "2026 Supply Chain Reckoning" series. Read the full series on [No Regressions](https://noregressions.substack.com).*

## Sources

1. Sonatype, [2026 State of the Software Supply Chain](https://www.sonatype.com/state-of-the-software-supply-chain/introduction) --- 28% hallucination rate and 345 cases of worsened security from LLM dependency recommendations.

2. Chamorro-Premuzic, T., [Why Do So Many Incompetent Men Become Leaders?](https://ideas.ted.com/why-do-so-many-incompetent-men-become-leaders-and-what-can-we-do-about-it/) --- the confidence-competence gap in leadership selection.

3. Carnegie Mellon University, [AI Chatbots Remain Confident --- Even When They're Wrong](https://www.cmu.edu/news/stories/archives/2025/july/ai-chatbots-remain-confident-even-when-theyre-wrong) --- 69--88% hallucination rate in legal queries with misleading authoritative tone.

4. Microsoft Research, [The Impact of Generative AI on Critical Thinking](https://www.microsoft.com/en-us/research/wp-content/uploads/2025/01/lee_2025_ai_critical_thinking_survey.pdf) --- documented reductions in independent problem-solving among AI tool users.
