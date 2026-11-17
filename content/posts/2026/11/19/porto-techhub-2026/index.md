---
title: "Porto TechHub 2026"
date: "2026-11-19"
description: "Notes from Porto TechHub 2026: Embabel and agentic AI planning, a comparison of Spring AI, LangChain4j, and Embabel, and the new defaults in Git 3."
canonical: "https://blog.frankel.ch/porto-techhub-2026/"
authors:
  - "nicolas-frankel"
image: "cover_large.jpg"
categories:
  - "Conference"
  - "AI"
related_posts:
  - "spring-ai-langchain4j-three-stacks-one-test"
  - "embabel-spring-boot-wikidata-definition-api"
  - "yow-2025"
  - "kcdc-2025"
---

My conference participation has dropped sharply since I left DevRel for a regular office engineering job. In 2023, I was proud to have had [70 talks](https://blog.frankel.ch/speaking/2023/); I'll finish this year with 4, including one online and one quickie. It has one beneficial side effect, though. Because I go to so few, I take the time to attend. On Tuesday, November 10<sup>th</sup>, I was lucky to attend and talk at the [Porto TechHub](https://portotechhub.com/) conference, for the 4th year.

![My Porto TechHub 2026 badge](badge.jpg)

Here are the talks I attended and their respective summary. If you attended a talk or are the presenter, feel free to correct my possible misunderstandings.

## Stop Prompting, Start Planning: Reliable Agentic AI with Embabel by Patrick Baumgartner

I'm cheating with this one: I couldn't attend it, as it was running in parallel with one of the other talks. However, Patrick presented it at the Geneva JUG [last month](https://genevajug.org/session/2026-10-27-stop-prompting-start-planning-embabel/).

Patrick introduced the subject by mentioning the core issue with LLMs: their non-deterministic nature. Two guardrails can help with that.

* The first is Goal-Oriented Action Planning. <abbr title="Goal-Oriented Action Planning">GOAP</abbr> is a planner that lets an agent decide its course of action by providing it with a context, a goal, and a set of actions.
* The second is standard programmatic constructs, _e.g._, conditionals (`if`).

Patrick then described Embabel, which aims to leverage both:

> Embabel is an Agentic AI framework for the JVM. Python has long been the go-to for machine learning experiments, thanks to its ease, ecosystem richness, and data scientist-friendly tools. However, it often struggles when you try to move from experiment to real-world, production-scale AI.
>
> —- [Embabel](https://hub.embabel.com/)

Embabel relies on Java code, types, annotations, and Spring so you can program steps in a workflow. The wiring from one step to the next is inferred via types. If the method `taskA()` returns `TypeX`, and the method `taskB()` accepts `TypeX` as a parameter, then A and B are steps, and B follows A.

Patrick showed several demos grounded in business use cases and the relevant code to highlight his points. At the end, he also did a demo of a visualization tool for the code.

{{< tip >}}
**My opinion**

I like Spring and strong typing, even though I could nitpick about having a proper DSL instead of annotations. In any case, I must admit that I fail to see the value proposition in Embabel.

We have plenty of workflow engines on the JVM. I started my Java career on one. Today, [Camunda](https://camunda.com/) offers a modern Open Source platform fully compatible with <abbr title="Business Process Model and Notation">BPMN</abbr>. BPMN has the benefit of being backed by a [specification](https://www.omg.org/spec/BPMN). Icing on the cake, many software components provide visualization of any BPMN-based workflow.

One could achieve the same benefits as Embabel with a workflow task calling an LLM.
{{< /tip >}}

Material: https://github.com/patbaumgartner/talk-stop-prompting-start-planning-with-embabel/

## The Agentic Loops Every Developer Should Know by Rui Quintino

I expected a talk in line with the title and the description. I missed the first 5 minutes, but the rest was mainly demos of what the speaker built with coding assistants. For example, he described how he wanted to edit a Word document in parallel with his assistant. However, Microsoft Word sets a lock so you can't do it. Thus, he used Claude Code to write his own Word clone to allow it.

{{< tip >}}
**My opinion**

The speaker was very enthusiastic about what he achieved, and I can't deny that it was great, but I didn't get anything actionable out of the talk.
{{< /tip >}}

## Comparing Agentic AI Frameworks for Java by Sandra Ahlgrimm and Timo Salm

The talk compares Spring AI, LangChain4j, and Embabel through the lens of an AI nutrition planner.

<table>
  <thead>
    <tr>
      <th>Capability</th>
      <th>Spring AI</th>
      <th>LangChain4j</th>
      <th>Embabel</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <th>Deterministic workflows</th>
      <td>Advisors &amp; plain Java code</td>
      <td><span style="color: green">Workflow agents</span></td>
      <td><span style="color: green">@State and planner</span></td>
    </tr>
    <tr>
      <th>Pure agents</th>
      <td><span style="color: green">Tool calling loop</span></td>
      <td><span style="color: green">Supervisor agent</span></td>
      <td><span style="color: green">Built in</span></td>
    </tr>
    <tr>
      <th>Memory and state</th>
      <td><span style="color: green">ChatMemory advisors</span></td>
      <td><span style="color: green">AgenticScope</span></td>
      <td><span style="color: green">Blackboard</span></td>
    </tr>
    <tr>
      <th>Agentic patterns</th>
      <td>Experimental</td>
      <td><span style="color: green">Built in, some experimental</span></td>
      <td><span style="color: green">Built in, some experimental</span></td>
    </tr>
    <tr>
      <th>Observability</th>
      <td>Model level only</td>
      <td>Agent listeners</td>
      <td>Agent level</td>
    </tr>
    <tr>
      <th>Testing</th>
      <td>Mocks, evaluators</td>
      <td>Mostly manual</td>
      <td><span style="color: green">Advanced testing support</span></td>
    </tr>
    <tr>
      <th><abbr title="Model Context Protocol">MCP</abbr></th>
      <td><span style="color: green">Client and server</span></td>
      <td><span style="color: red">No HTTP server support</span></td>
      <td><span style="color: green">Client and server</span></td>
    </tr>
    <tr>
      <th><abbr title="Agent to Agent">A2A</abbr></th>
      <td>Experimental</td>
      <td><span style="color: red">Client only</span></td>
      <td>Experimental</td>
    </tr>
    <tr>
      <th>Maturity</th>
      <td>
        <ul>
          <li>Agentic experimental</li>
          <li>Official Spring project</li>
          <li>Enterprise support</li>
        </ul>
      </td>
      <td>Agentic experimental</td>
      <td><span style="color: green">GA, plus some experimental features</span></td>
    </tr>
    <tr>
      <td colspan="4" align="right"><em>Legend</em>: <span style="color: green">Built-in</span> | Experimental or some effort | <span style="color: red">Limited</span></td>
    </tr>
  </tbody>
</table>

{{< tip >}}
**My opinion**

I found the talk to be to the point. The table above summarizes the comparison. I'm happy to have it if I need help choosing in the next 6 months.

I also liked that the two speakers work for competing companies, Broadcom and Microsoft. Timo, from Broadcom, didn't push Spring AI over the other two. Sandra, from Microsoft, is a committer on LangChain4j, which Microsoft doesn't own.
{{< /tip >}}

Material:

* https://github.com/SandraAhlgrimm/ai-nutrition-planner/
* https://spring.academy/courses/spring-ai-intro/

## What's New in Git 3 by Rob Richardson

The talk focuses on the 4 features that become the defaults in Git 3:

* The hash moves from SHA-1 to SHA-256
* Reference storage moves from loose files to _reftable_
* The first branch moves from `master` to `main`
* Bare repositories are now `safe` by default

The speaker explained each item, its current state, and its future state. He also reiterated several times during the talk: these are the new defaults of existing options. Using Git 3 on a repo created with Git 2 defaults will work flawlessly. He also demoed how to migrate such a repo to the new defaults, one item at a time.

According to Rob, the biggest obstacle to releasing Git 3 is the lack of support from Git hosting providers.

{{< tip >}}
**My opinion**

I wanted a non-AI talk, and I wasn't disappointed. I particularly liked the fact that it was targeted at Git users, not Git internals experts.

I'm now able to understand each item and how its default changes between Git 2 and 3.
{{< /tip >}}

Material: https://robrich.org/presentation/2026/11/10/git-3.aspx

## Conclusion

Porto TechHub was a very enjoyable experience. I've been a regular speaker, and if the chance presents itself, I'll be happy to come again. Many thanks to the team for the flawless speaker experience.

**To go further:**

* [Stop Prompting, Start Planning: Reliable Agentic AI with Embabel](https://github.com/patbaumgartner/talk-stop-prompting-start-planning-with-embabel/)
* [AI nutrition planner](https://github.com/SandraAhlgrimm/ai-nutrition-planner/)
* [Spring AI introduction course](https://spring.academy/courses/spring-ai-intro/)
* [What's New in Git 3](https://robrich.org/presentation/2026/11/10/git-3.aspx)
* [Spring AI](https://docs.spring.io/spring-ai/reference/index.html)
* [LangChain4j](https://docs.langchain4j.dev/)
* [Embabel](https://docs.embabel.com/)

<hr>

_Originally published at [A Java Geek](https://blog.frankel.ch/porto-techhub-2026/) on November 15<sup>th</sup>, 2026._
