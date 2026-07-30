---
title: "Spec-Driven Development with AI: A New Approach and a Journey into the Past"
slug: "spec-driven-development-with-ai-a-new-approach-and-a-journey-into-the-past"
date: "2025-09-08T07:35:46+00:00"
lastmod: "2025-09-09T08:53:57+00:00"
description: "We’re at an inflection point. AI can generate high-quality code, but only if we give it high-quality specifications."
authors:
  - "simon-martinelli"
image: "https://foojay.io/wp-content/uploads/2025/07/116749_image_676x380.webp"
categories:
  - "AI"
  - "Opinion"
tags:
related_posts:
enlighterjs: true
frozen: false
---

The software development world is buzzing about AI-assisted coding. Tools like Claude Code, Windsurf, and JetBrains Junie promise to make us more productive. But most approaches focus on generating code faster -- they're still **code-centric**.

What if we took a different approach? What if we made **requirements** the source of truth and let AI handle everything downstream?

After years of building business applications, I began developing a methodology that combines ideas from the [Rational Unified Process](https://en.wikipedia.org/wiki/Rational_unified_process) (RUP) with modern AI tooling: The [AI Unified Process](https://aiup.dev). The results are remarkable: better business alignment, maintainable code, and complete traceability from business needs to implementation.

The Problem with Code-Centric Development {#h2-0-the-problem-with-code-centric-development}
-------------------------------------------------------------------------------------------

Traditional development follows this pattern:

1. Write requirements documents
2. Code the application
3. Add tests (maybe)
4. Update documentation (rarely)

The problem? Code becomes the source of truth. Requirements documents get outdated. When bugs appear or features need changes, we dig through the code to understand what the system was supposed to do. It gets even worse if we need to modernize the application a few years later.

AI coding tools make this worse by generating code faster. We're accelerating toward the same maintenance problems we've always had.

Requirements as the Single Source of Truth {#h2-1-requirements-as-the-single-source-of-truth}
---------------------------------------------------------------------------------------------

My approach flips this around. Requirements stay at the center, and everything else gets generated from them:

### The Complete Workflow {#h3-2-the-complete-workflow}

1. **Business Requirements Catalog** (manual, with business stakeholders)
2. **Business Use Case Diagrams** (AI-generated, then reviewed with business stakeholders)
3. **Entity Models** (AI-derived from requirements catalog, reviewed by the business)
4. **System Use Case Diagrams** (AI-generated and revised by the developer and/or business)
5. **System Use Case Specifications** (AI-generated, detailed markdown, revised by the developer and/or business)
6. **Application Code** (AI-generated, reviewed by developer)

The key: **Every step gets reviewed and revised by the business team.** They validate not just the business artifacts, but also the entity models and system use cases. This catches domain modeling errors before they become expensive code problems.

### Everything is Code, Everything is Versioned {#h3-3-everything-is-code-everything-is-versioned}

All specifications are written in Markdown and stored in Git alongside the application code. Use case diagrams are generated as PlantUML source code. This gives us:

* **Visual diffs**: See exactly what changed in diagrams
* **Complete audit trails**: Track every change from requirements to code
* **Version control**: Branch specifications alongside code
* **Collaborative editing**: Business stakeholders can request specific changes

### AI as the Consistency Engine {#h3-4-ai-as-the-consistency-engine}

When requirements change, AI tools like Claude Code update all downstream artifacts automatically:

* Regenerate the affected use case diagrams
* Update entity models
* Modify system specifications
* Generate new application code
* Maintain traceability links

No manual synchronization. No outdated documentation. No guessing what the system should do.

The Structure: Independent Epics {#h2-5-the-structure-independent-epics}
------------------------------------------------------------------------

Business applications are complex, but that doesn't mean they have to be complicated. I organize everything into independent epics:

* **Event Management**: UC-EVENT-001, UC-EVENT-002, etc.
* **User Management**: UC-USER-001, UC-USER-002, etc.
* **Organization Management**: UC-ORG-001, UC-ORG-002, etc.

No cross-epic dependencies. Each epic is a bounded context that can be developed, tested, and deployed independently. This simplifies both the AI generation process and the overall system architecture.

A Real Example: System Use Case Specification {#h2-6-a-real-example-system-use-case-specification}
--------------------------------------------------------------------------------------------------

Here's how a system use case looks in practice:

<pre class="EnlighterJSRAW" data-enlighter-language="raw" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""># Use Case: Create Events
**Use Case ID:** UC-EVENT-001  
**Primary Actor:** Manager  
**Goal:** Allow managers to create new events for their organization

## Main Success Scenario
1. Manager navigates to "Events" section
2. Manager clicks "Create New Event" button
3. System displays event creation form
...

## Business Rules
### BR-EVENT-001: Event Date Validation
- End date must be equal to or after start date
- Events cannot be created with start dates in the past

## Technical Notes
### Validation Rules
- Title: Required, 3-200 characters
- Start Date: Required, must be future date
- End Date: Required, must be &gt;= start date</pre>

This level of detail gives AI tools everything they need to generate correct, complete implementations. Business stakeholders can understand the main flow, while developers get precise technical requirements.

The Results {#h2-7-the-results}
-------------------------------

This approach has transformed how I build business applications:

**Better Business Alignment**: Business stakeholders review every artifact, ensuring the system matches their actual needs.

**Maintainable Code**: When requirements change, everything downstream updates consistently. No drift between documentation and implementation.

**Faster Development**: AI handles the tedious work of generating diagrams, specifications, and code. Business stakeholders concentrate on providing the requirements and reviewing the specification. Developers focus on business logic and architecture.

**Complete Traceability**: From a business requirement to a line of code, every connection is maintained and visible.

**Quality Assurance**: Generated code includes comprehensive tests based on the use case specifications.

Why This Works for Business Applications {#h2-8-why-this-works-for-business-applications}
-----------------------------------------------------------------------------------------

Business applications have predictable technical patterns but more or less complex domain logic. Frameworks like Vaadin, Spring Boot, and jOOQ provide stable foundations (see [the Simon Martinelli Stack](https://martinelli.ch/the-simon-martinelli-stack-a-pragmatic-approach-to-full-stack-java-development/)). The real complexity lies in understanding and modeling the business domain correctly.

This methodology plays to both strengths:

* Human expertise handles business domain complexity
* AI handles consistent technical implementation

Getting Started {#h2-9-getting-started}
---------------------------------------

If you want to try this approach:

1. **Start with requirements**: Don't jump to code. Invest time in understanding and documenting business needs.
2. **Make everything code**: Use markdown for specifications, PlantUML for diagrams. Version everything in Git.
3. **Review with business**: Get stakeholders to validate not just business artifacts, but also entity models and system use cases.
4. **Use AI as your consistency engine**: Let tools like Claude Code handle generation and updates.
5. **Keep epics independent**: Avoid cross-dependencies to simplify both development and AI generation.

The Future of Business Application Development {#h2-10-the-future-of-business-application-development}
------------------------------------------------------------------------------------------------------

We're at an inflection point. AI can generate high-quality code, but only if we give it high-quality specifications. The organizations that invest in better requirements processes will build better software faster.

This isn't about replacing developers with AI. It's about using AI to eliminate the tedious, error-prone work so we can focus on what matters: understanding business needs and designing systems that serve them well.

The code is the easy part. Getting the requirements right is where the real value lies. Read more: [https://aiup.dev](https://aiup.dev/)

*This article was first published on <https://martinelli.ch/spec-driven-development-with-ai-a-new-approach-and-a-journey-into-the-past/>*

<br />
