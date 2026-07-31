---
title: "Payara Server Maven Plugin + AI Agent"
slug: "boost-developer-productivity-with-payara-server-maven-plugin-ai-agent"
date: "2025-09-30T08:43:20+00:00"
lastmod: "2025-09-30T09:29:28+00:00"
description: "Imagine managing your Jakarta EE applications not just with Maven goals, but by asking natural questions."
canonical: "https://blog.payara.fish/payara-server-maven-plugin-ai-agent"
authors:
  - "dominika-tasarz"
image: "see-it-in-action-screenshot-AI-Maven-Blog.jpg"
categories:
  - "AI"
  - "Jakarta EE"
  - "Java"
  - "Java Beginner"
  - "Maven"
  - "Tutorials"
tags:
related_posts:
  - "whats-new-in-the-july-2026-azul-payara-release"
  - "shaping-jakarta-agentic-ai-together-watch-the-open-conversation"
  - "bring-ai-into-your-jakarta-ee-apps-with-langchain4j-cdi"
  - "how-to-kickstart-your-jakarta-ee-11-projects-with-payara-starter"
enlighterjs: true
frozen: false
---

Managing Payara Server Just Got Smarter {#h2-0-managing-payara-server-just-got-smarter}
---------------------------------------------------------------------------------------

Imagine managing your Jakarta EE applications not just with Maven goals, but by asking natural questions.

With the [experimental AI Agent](http:/https://docs.payara.fish/community/docs/6.2025.5/Technical%20Documentation/Ecosystem/Project%20Management%20Tools/Maven%20Plugin/Payara%20Server%20Maven%20Plugin.html#ai-agent/ "experimental AI Agent") built into the Payara Server Community's Maven Plugin, you can now query server internals and perform admin tasks using plain English --- directly from the same terminal where you build and deploy your applications.

Thanks to this integration, developers can inspect, query and even execute server commands using natural language. It's a powerful and intuitive way to interact with Payara Server --- no need to remember long command syntax or dig through documentation.

In this post, I'll walk you through how it works, with real examples that show just how seamless the AI-powered CLI experience can be.

What Is the Payara Server Maven Plugin? {#h2-1-what-is-the-payara-server-maven-plugin}
--------------------------------------------------------------------------------------

The Payara Server Maven Plugin simplifies server lifecycle management and app deployment via Maven goals like:

* start: Launch the server with configurable options.
* dev: A development-friendly mode enabling auto build, auto deploy, live reload browser, and session persistence.

Now, with the AI Agent, it goes a step further, bringing intelligence control directly to your terminal.

Meet the AI Agent (Experimental) {#h2-2-meet-the-ai-agent-experimental}
-----------------------------------------------------------------------

The AI Agent listens to natural language queries typed in your terminal. Instead of memorizing asadmin commands, you can now just ask:

**"What is the current heap usage?**   
**Show thread pool stats.**   
**Create a JDBC connection pool**   
**Ping a JDBC pool. "**

It interprets your questions, queries internal APIs (JMX, domain.xml, logs, etc.), and responds intelligently. Perfect for speeding up diagnostics and reducing context switching.

Demo #1 -- Memory \& Threads, in Plain English {#h2-3-demo-1-memory-threads-in-plain-english}
---------------------------------------------------------------------------------------------

In the first clip below, I ran the mvn payara-server:dev goal and started typing natural queries into the CLI:

### What's happening: {#h3-4-what-s-happening}

* Asked about current non-heap memory usage (both absolute and percentage)
* Queried heap memory
* Requested details on thread pools
* Asked how many threads are allocated

See it in action:  

No need to dig through JMX or logs manually. The AI Agent fetches and summarizes it instantly.

<img fetchpriority="high" decoding="async" class="alignnone size-medium" src="https://blog.payara.fish/hs-fs/hubfs/AIAgent2.gif?width=2067&amp;height=1119&amp;name=AIAgent2.gif" width="1378" height="746">

<br />

Demo #2 -- JDBC, JMX, and JMS Made Easy {#h2-5-demo-2-jdbc-jmx-and-jms-made-easy}
---------------------------------------------------------------------------------

In the second demo, I explored resource management features:

### What's happening: {#h3-6-what-s-happening}

* Asked the AI to create a JDBC connection for a PostgreSQL DB named alphadb using user alphauser
* Listed all existing JDBC connection pools
* Pinged a known pool (h2pool)
* Asked how to create a JMX resource
* Checked for existing JMS resources

See it in action:  

Instead of navigating the admin console or writing XML config, AI does the heavy lifting.

<img decoding="async" class="alignnone size-medium" src="https://blog.payara.fish/hs-fs/hubfs/AIAgent1.gif?width=2067&amp;height=1119&amp;name=AIAgent1.gif" width="1378" height="746">

<br />

### Configuring the AI Agent {#h3-7-configuring-the-ai-agent}

To unlock the full capabilities of the Payara AI Agent, you can fine-tune its behavior using system properties or environment variables. Here's what you need to know:

#### Essential Configuration Variables

|  Environment Variable  |  System Property   |                                           Description                                           |
|------------------------|--------------------|-------------------------------------------------------------------------------------------------|
| **PAYARA_AI_AGENT**    | payara.ai.agent    | Enables the AI Agent (true or false). Default: true in dev mode.                                |
| **PAYARA_AI_API_KEY**  | payara.ai.api.key  | Your API key for accessing the AI service provider (e.g., OpenAI). Required for most providers. |
| **PAYARA_AI_PROVIDER** | payara.ai.provider | Defines the AI provider to use. See list below.                                                 |
| **PAYARA_AI_MODEL**    | payara.ai.model    | Specifies the AI model (e.g., gpt-4o-mini, claude-3-opus, etc.).                                |

You can pass these as environment variables:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">export PAYARA_AI_AGENT=true
export PAYARA_AI_API_KEY=sk-xxxxxx
export PAYARA_AI_PROVIDER=OPEN_AI
export PAYARA_AI_MODEL=gpt-4o-mini</pre>

Or via Maven:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">mvn payara-server:dev \
-Dpayara.ai.agent=true \
-Dpayara.ai.api.key=sk-xxxxxx \
-Dpayara.ai.provider=OPEN_AI \
-Dpayara.ai.model=gpt-4o-mini</pre>

#### How to Enable the AI Agent

By default, the AI Agent is enabled in [dev mode](https://blog.payara.fish/stratospheric-developer-productivity-unveiling-payara-dev-mode "dev mode"). Otherwise, enable it explicitly using:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">-Dpayara.ai.agent=true</pre>

Or set the environment variable:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">export PAYARA_AI_AGENT=true</pre>

The AI Agent currently supports a variety of providers, so you can integrate with the one that best fits your setup:

**OPEN_AI, CUSTOM_OPEN_AI, GOOGLE, DEEPINFRA,**   
**DEEPSEEK, GROQ, MISTRAL, OLLAMA, ANTHROPIC,**   
**LM_STUDIO, GPT4ALL**

This gives you flexibility to run models locally (e.g., Ollama, LM Studio), via API (e.g., OpenAI, Google, Mistral), or even from custom endpoints.

Final Thoughts {#h2-8-final-thoughts}
-------------------------------------

The Payara Server Maven Plugin already simplified the Jakarta EE development lifecycle but with the AI Agent, it becomes an intelligent assistant. Whether you're inspecting memory, managing JDBC pools, or just experimenting, it drastically reduces friction and boosts productivity.

⚠️ As powerful as the AI Agent is, it's still experimental and not recommended for production environments.

⚠️ Also, since it accesses sensitive runtime internals like JMX metrics, logs, configuration files and connection pool details, keep it restricted to local development, testing or isolated sandboxes.

### Want to see this in action? {#h3-9-want-to-see-this-in-action}

We're hosting a free, 45-minute webinar where we'll demo the AI Agent in the Payara Server Community Maven Plugin. You'll discover how to query server metrics, manage JDBC and JMS resources, and run diagnostics in plain English --- all from your terminal. This session is designed for the community: experimental, fun, and a glimpse into how AI can make Jakarta EE development more intuitive. [Register here to join the webinar (or watch on demand after the date)](https://www.crowdcast.io/c/ai-powered-payara-server-with-maven "Register here to join the webinar (or watch on demand after the date)")!

![webinar promo image: Smarter Jakarta EE Management: AI-Powered Payara Server with Maven](Gaurav-Webinar-AI-September-2-700x394.jpg)

[Original article by Gaurav Gupta published on Payara Blog.](https://blog.payara.fish/payara-server-maven-plugin-ai-agent "Original article by Gaurav Gupta published on Payara Blog.")
