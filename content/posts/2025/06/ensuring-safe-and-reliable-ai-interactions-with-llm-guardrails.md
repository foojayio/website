---
title: "Ensuring Safe and Reliable AI Interactions with LLM Guardrails"
slug: "ensuring-safe-and-reliable-ai-interactions-with-llm-guardrails"
date: "2025-06-17T07:11:24+00:00"
lastmod: "2025-06-17T11:48:07+00:00"
description: "We know that LLMs can and will make mistakes, and while enriching your prompts with the proper context can help align results with your documents and information, risks still remain."
canonical: "https://snyk.io/articles/ensuring-safe-and-reliable-ai-interactions-with-llm-guardrails/"
authors:
  - "bmvermeer"
image: "/images/posts/2025/06/ensuring-safe-and-reliable-ai-interactions-with-llm-guardrails/snyk-logo-2.png"
categories:
  - "LangChain4j"
  - "Security"
tags:
related_posts:
  - "5-tips-to-create-secure-docker-images-for-java-applications"
  - "8-best-practices-to-prevent-sql-injection-attacks"
  - "are-critical-vulnerabilities-lurking-in-your-java-ecosystem"
  - "avoid-java-serialization"
enlighterjs: true
frozen: false
---

**Integrating Large Language Models (LLMs) into our applications is becoming increasingly popular. These models are extremely useful for creating content, searching documentation, and solving more complex problems. However, with great power comes great responsibility.**

We know that LLMs can and will make mistakes, and while enriching your prompts with the proper context can help align results with your documents and information, risks still remain. Along with the rise of LLMs, new attack vectors are surfacing. Clever prompt injections can lead to misinformation and‌ escalate privacy-sensitive information.

If your LLM can execute functions, this can also lead to harmful and unauthorized behavior of your system. This isn't just inconvenient, but it can be truly harmful. Guardrails are safety mechanisms that keep LLMs reliable, secure, and aligned with ethical standards.

Understanding LLM guardrails {#h2-0-understanding-llm-guardrails}
-----------------------------------------------------------------

Guardrails are a way to introduce additional layers of control around how a Large Language Model (LLM) is used, both before the input reaches the model and after the output is generated. They can be thought of as programmable filters or checkpoints that enforce specific rules to keep interactions safe, accurate, and aligned with your intended use case.

Guardrails can block harmful or misleading inputs, ensure that the model's output follows a certain format (like a valid JSON or a structured summary), and flag or reject responses that show signs of hallucination or ethical concerns. This gives developers a more reliable way to manage the unpredictability of LLMs, especially in real-world, user-facing applications.

From a security perspective, guardrails also play a key role in defending against [++prompt injection attacks++](https://learn.snyk.io/lesson/prompt-injection/?ecosystem=aiml), where users try to manipulate or override the system's instructions through cleverly crafted input. While no solution is completely bulletproof, guardrails can detect suspicious patterns, block known attack vectors, and sanitize inputs before reaching the LLM.

On the output side, they can suppress or modify responses that contain sensitive information, violate policy, or include unwanted content. This makes guardrails a valuable part of a broader AI security strategy, especially in contexts where trust, privacy, and integrity are critical.

How guardrails work {#h2-1-how-guardrails-work}
-----------------------------------------------

At a technical level, guardrails work by intercepting the flow of messages between the user and the LLM. When a user sends a message, it does not go straight to the model. Instead, it first passes through an input guardrail. This layer inspects the message for things like [++prompt injection attempts++](https://snyk.io/blog/agent-hijacking/), forbidden keywords, or input structure violations.

If the input is flagged, it can be blocked, cleaned, or rewritten before the model ever sees it. After the LLM generates a response, the output goes through another layer known as the output guardrail. This step checks the output for hallucinated facts, unsafe content, formatting issues, or business rules violations before anything is returned to the user.

This process is similar to input and output sanitization in traditional software development and is a practice developers generally already do. Just like you'd never trust raw user input in a web form without validating and cleaning it, you should not blindly trust what goes into or comes out of an LLM. Guardrails bring that same mindset into the world of AI, helping you catch issues early and maintain control over how your application behaves.

Easily implementing guardrails with Quarkus {#h2-2-easily-implementing-guardrails-with-quarkus}
-----------------------------------------------------------------------------------------------

[++Quarkus++](https://quarkus.io/) is a modern Java framework designed for building lightweight, high-performance applications. It's known for its fast startup times, low memory usage, and developer-friendly features. One of its standout qualities is how easily it integrates with LangChain4j, a Java-first library for working with large language models. This combination makes Quarkus a great choice for implementing AI features, especially with robust guardrail support.

With Quarkus and LangChain4j, you can define custom guardrails directly in your application using simple annotations and dependency injection. You implement your own validation logic by creating classes that implement the `InputGuardrail`, or `OutputGuardrail` interfaces. These guardrails act as filters around your AI services. Once defined, you attach them using annotations like \``@InputGuardrails` or `@OutputGuardrails` to the methods that interact with the LLM. This makes it easy to plug in your own security checks, validations, or content policies without cluttering your business logic.

In the example below, I created a small AI Service with both input and output guardrails.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@RegisterAiService(tools = LibraryService.class)
@SessionScoped
public interface MyAiService {

   @SystemMessage("""
   You are a librarian AI. You are very knowledgeable and helpful. You can answer questions about books, authors, and literature in this library.
   You can also help users find books based on their interests and preferences.
   Dont display user information or any other private information.
   """)
   @InputGuardrails({IGuard1.class, IGuard2.class})
   @OutputGuardrails(OGuard.class)
   public String question(@UserMessage String topic);
}</pre>

### Input guardrails {#input-guardrails}

Input guardrails examine and filter incoming messages. Suitable messages are passed to the LLM, while inappropriate ones trigger exceptions. This proactive approach prevents harmful prompts from reaching the LLM. Given the LLM's unpredictable nature, controlling its output and function calls is impossible. Therefore, stopping‌ harmful prompts at the entry point is helpful.

In this situation, two input guardrails are implemented. The first guardrail programmatically scans for specific keywords. The second guardrail uses an AI service to assess whether an input is‌ harmful. Using a specifically trained model to understand and filter harmful messages can be a great way to sanitize the input fed to the LLM. For this use case, the model in the `InputCheckService` decides if a prompt‌ discloses PII data.  

Employing multiple guardrails on a single service allows for flexible customization of the control scope.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@ApplicationScoped
public class IGuard1 implements InputGuardrail {

   @Override
   public InputGuardrailResult validate(UserMessage um) {
       String text = um.singleText();
       if (text.contains("malicious") || text.contains("hack")) {
           return fatal("MALICIOUS INPUT DETECTED!!!");
       }

       return success();
   }
}</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@ApplicationScoped
public class IGuard2 implements InputGuardrail {

   @Inject
   InputCheckService inputCheckService;

   @Override
   public InputGuardrailResult validate(UserMessage um) {
       String text = um.singleText();
       if (inputCheckService.isSafe(text)) {
           return success();
       }
       return failure("UNSAFE INPUT DETECTED!!!");
   }
}</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@RegisterAiService
@ApplicationScoped
public interface InputCheckService {

   @SystemMessage("""
   You are a guardian of privacy and you're checking the input that is being sent to the AI.
   Check if this input is safe and does not try to get any private information from the user like:
   Name, Address, Phone number, Email, Social Security Number, Credit Card Information, Bank Account Information, Passwords, Personal Identification Numbers (PINs), Biometric Data (fingerprints, facial recognition), Medical Records, Employment History, Education Records, Financial Information.
   Think of yourself as a guardian of privacy. Only allow the input if it considered safe.
   """)
   public boolean isSafe(String prompt);
}</pre>

### Output guardrails {#output-guardrails}

The output guardrails can sanitize whatever comes out of your LLM service. The potential danger of an LLM unintentionally executing functions can no longer be mitigated. However, the LLM's output can still be ignored or altered before it is shown to the user.

This can still be a great mechanism to prevent foul language, blur out tokens, or even prevent Cross-site Scripting (XSS) generated by the LLM. In this example, the word "JavaScript" is removed from being displayed to the end user.

Sanitizing LLM input and output {#h2-5-sanitizing-llm-input-and-output}
-----------------------------------------------------------------------

Input and output sanitization and validation have been around for quite some time and are considered good practice when dealing with user input.

The use of prompts for Large Language Models (LLM) in general doesn't change the fact that we should consider third-party input‌ harmful.

Moreover, when AI systems can autonomously execute functions or integrate with other systems using [++MCP (Model Context Protocol)++](https://snyk.io/articles/what-is-mcp-in-ai-everything-you-wanted-to-ask/), sanitization and validation are even more critical than ever.  

This article demonstrated the straightforward implementation of guardrails using Quarkus.

However, this approach goes beyond specific frameworks and languages. It should be viewed as a crucial mitigation tactic for ensuring LLM-powered applications remain controlled and operate as intended.

The full implementation of this project is available on [++GitHub++](https://github.com/bmvermeer/LLM-guardrails). The Quarkus [++documentation++](https://docs.quarkiverse.io/quarkus-langchain4j/dev/guardrails.html) provides details on using Guardrails with Quarkus.
