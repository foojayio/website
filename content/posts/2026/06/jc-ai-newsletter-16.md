---
title: "JC-AI Newsletter #16"
slug: "jc-ai-newsletter-16"
date: "2026-06-09T18:28:33+00:00"
lastmod: "2026-06-11T09:06:23+00:00"
description: "Over the past two weeks, the field of artificial intelligence has continued its remarkable pace of advancement. As AI becomes increasingly woven into the - by Miro Wengner"
authors:
  - "miro-wengner"
image: "https://foojay.io/wp-content/uploads/2025/07/ai-insider-1.png"
categories:
  - "Data Engineering"
  - "Interviews"
  - "Java"
  - "JC-AI Newsletter"
  - "Machine Learning"
  - "Observability"
  - "Opinion"
  - "Research"
  - "Videos"
tags:
related_posts:
frozen: false
---

Over the past two weeks, the field of artificial intelligence has continued its remarkable pace of advancement. As AI becomes increasingly woven into the fabric of daily life, shaping how we work, communicate, and make decisions, it is both timely and valuable to step back and understand the broader trajectory of this technology. Whether the developments around us feel promising or challenging, one truth remains clear: AI is not simply going away. It is here to stay, and understanding its evolution is essential from many perspectives. Have you ever wondered what harness engineering is, how evals attempt to move traditional unit tests onto a probabilistic plane, or how AI is reshaping entire industries across various branches? Let's start.

**article** : [Parallel distributed processing: explorations in the microstructure of cognition. Volume 1. Foundations](https://www.researchgate.net/publication/200033859_Parallel_distributed_processing_explorations_in_the_microstructure_of_cognition_Volume_1_Foundations "Parallel distributed processing: explorations in the microstructure of cognition. Volume 1. Foundations")  
**authors** : James L Mcclelland (Stanford University)  
**date** : 1986-01-01  
**desc.** : The fundamental principles, basic mechanisms, and formal analyses involved in the development of parallel distributed processing (PDP) systems are presented in individual chapters contributed by leading experts. Topics examined include distributed representations, PDP models and general issues in cognitive science, feature discovery by competitive learning, the foundations of harmony theory, learning and relearning in Boltzmann machines, and learning internal representations by error propagation. Consideration is given to linear algebra in PDP, the logic of additive functions, resource requirements of standard and programmable nets, and the P3 parallel-network simulating system.  
**category**: research

**article** : [Generalization in LLM Problem Solving: The Case of the Shortest Path](https://arxiv.org/abs/2604.15306 "Generalization in LLM Problem Solving: The Case of the Shortest Path")  
**authors** : Yao Tong, Jiayuan Ye, Anastasia Borovykh, Reza Shokri (Google Research)  
**date** : 2026-04-16  
**desc.** : The article studies systematic generalization in composable sequential optimization problems within a controlled synthetic pathfinding environment. The article reveals a clear asymmetry: models transfer structurally across unseen maps but fail under length scaling due to recursive instability. By disentangling training data, training paradigms, and inference-time strategies, The article systematically analyzes the contribution of each factor to generalization performance. The findings offer a unified view of how different stages of the learning pipeline shape generalization in language models.  
**category**: research

**article** : [Atropos: Improving Cost-Benefit Trade-off of LLM-based Agents under Self-Consistency with Early Termination and Model Hotswap](https://arxiv.org/abs/2604.15075 "Atropos: Improving Cost-Benefit Trade-off of LLM-based Agents under Self-Consistency with Early Termination and Model Hotswap")  
**authors** : Naryeong Kim, Shin Yoo  
**date** : 2026-04-16  
**desc.** : Open-weight Small Language Models (SLMs) can provide faster local inference at lower financial cost, but may not achieve the same performance level as commercial Large Language Models (LLMs) that are orders of magnitude larger. This article introduces the ATROPOS framework, which uses a Graph Convolutional Network (GCN) to predict whether an ongoing inference will eventually succeed or not. An empirical evaluation of ATROPOS using three recent LLM-based agents shows that ATROPOS can predict early termination of eventually failing inferences with an accuracy of 0.85 at the midpoint of the inference. Hotswapping LLMs for such inferences can convert up to 27.57% of them to successful outcomes. Consequently, ATROPOS achieves 74.35% of the performance of closed LLMs at as low as 23.9% of the cost. The article proposes future research based on mathematical evaluations.  
**category**: research

**article** : [From Text to Discovery: How Are LLMs Reshaping Scientific and Humanistic Research?](https://arxiv.org/abs/2606.08723 "From Text to Discovery: How Are LLMs Reshaping Scientific and Humanistic Research?")  
**authors** : Saleh Afroogh, Yasser Pouresmaeil, Yiming Xu, Kevin Chen, Abhejay Murali and Junfeng Jiao  
**date** : 2026-06-07  
**desc.** : Large Language Models (LLMs) are rapidly reshaping academic research across the natural sciences, social sciences, and humanities, yet the scientific community lacks a comprehensive, cross-disciplinary account of how these tools are being integrated, what they deliver, and where they fall short. This article addresses that gap by mapping their current state across various domains (Materials Science \& Chemistry, Healthcare \& Medical Research, Pharmaceutical \& Drug Discovery, Biological Research, Physics, Economics, etc.) and outlining an agenda for their responsible integration into scientific research. Beyond technical limitations, the article identifies ten underexplored challenges, including the erosion of researcher autonomy, AI-driven confirmation bias, authorship ambiguity, and unequal access to these technologies --- systemic risks that demand interdisciplinary governance frameworks, robust validation standards, and expanded explainability research. The article attempts to address opportunities while acknowledging challenges related to hallucinations and bias. It opens discussion on LLM usability across various fields and future research possibilities, as critical gaps remain to be described.  
**category**: research

**article** : [Harness engineering: leveraging Codex in an agent-first world](https://openai.com/index/harness-engineering/ "Harness engineering: leveraging Codex in an agent-first world")  
**authors** : AI Engineer  
**date** : 2026-04-09  
**desc.** : What is harness engineering, and why should we care about it? In simple terms, harness engineering wraps agent behaviour and attempts to ground it in order to enforce deterministic outcomes. Nevertheless, it comes with multiple challenges that are beyond the scope of this article.  
**category**: research

**article** : [Software Fundamentals Matter More Than Ever --- Matt Pocock](https://www.youtube.com/watch?v=v4F1gFy-hqg)  
**authors** : AI Engineer, Matt Pocock  
**date** : 2026-04-23  
**desc.** : AI coding tools are overhyped and powerful at the same time. Used well, they're extraordinary. Used badly, they'll bury you in spaghetti code faster than any human team could. The difference isn't the tool. It's the process. After 18 months of teaching developers to build with AI agents, Matt Pocock has watched the same patterns emerge: the devs who succeed aren't the ones who delegate everything or nothing. They're the ones who fall back on engineering fundamentals. In this talk, he shares the iterative process his students use to ship high-quality applications with AI agent swarms, and why the principles that make it work (ubiquitous language, vertical slices, TDD, deep modules) are decades-old ideas that didn't break. They got more important.  
**category**: youtube

**article** : [When Prompt Under-Specification Improves Code Correctness: An Exploratory Study of Prompt Wording and Structure Effects on LLM-Based Code Generation](https://arxiv.org/abs/2604.24712 "When Prompt Under-Specification Improves Code Correctness: An Exploratory Study of Prompt Wording and Structure Effects on LLM-Based Code Generation")  
**authors** : Amal AKLI, Mike PAPADAKIS, Maxime CORDY, Yves Le TRAON  
**date** : 2026-04-27  
**desc.** : Multiple studies have already reported that small changes in natural language prompts, particularly under-specification, can substantially reduce code correctness. These findings are largely based on minimal-specification benchmarks. This article reveals that robustness is not a fixed property of LLMs but is highly dependent on prompt structure. The article's results show not only that rich task descriptions may lead to negative effects, but also how to improve prompts to obtain desired results. The article provides directions for future studies regarding the reliability of the output.  
**category**: research

**article** : [Leveraging LLMs for Grammar Adaptation: A Study onMetamodel-Grammar Co-Evolution](https://arxiv.org/abs/2605.21465 "Leveraging LLMs for Grammar Adaptation: A Study onMetamodel-Grammar Co-Evolution")  
**authors** : Weixing Zhang, Bowen Jiang, Rahul Sharma, Regina Hebig, and Daniel Strüber  
**date** : 2026-04-20  
**desc.** : In model-driven engineering, metamodel evolution leads to the need to adapt corresponding grammars to maintain consistency, which typically requires tedious manual work. Existing rule-based methods can achieve partial automation but have limitations when handling complex grammar scenarios. This paper proposes a Large Language Model-based approach that automatically applies adaptations to new grammars after evolution by learning grammar adaptations from previous versions. The paper uses four DSLs (Domain-Specific Languages) as a training set to develop prompting strategies and two DSLs as a test set for validation, while conducting a longitudinal case study. The evaluation is performed against three Large Language Models (Claude Sonnet 4.5, ChatGPT 5.1, and Gemini 3). However, on large-scale grammars (EAST-ADL, 297 rules), the consistency of LLM adaptations fell well below 90%. Evaluation on six real-world DSLs demonstrates that LLMs outperform the rule-based approach on complex grammar scenarios and successfully reuse adaptations across consecutive evolution steps without manual grammar editing; however, systematic omission of adaptation operations on large-scale grammars reveals the current limitations of LLMs at scale.  
**category**: research

**article** : [Evals Are Broken, Use Them Anyway --- Ara Khan, Cline](https://www.youtube.com/watch?v=QuuIywMG4s8 "Evals Are Broken, Use Them Anyway — Ara Khan, Cline")  
**authors** : AI Engineer, Ara Khan  
**date** : 2026-06-06  
**desc.** : Presentation goes with the motto: Evals are broken but use them anyway. Probabilistically eval are just measurements of the confidence level which the user willing to accept due to the model nature (Agent= Model + Harness). Cline started at 43% on Terminal Bench. The improvements came from container CPU and memory settings, raised timeouts, and prompt engineering techniques specific to Anthropic model families that do not transfer to Codex or Gemini. Not from switching to a better model. Ara Khan's argument is that benchmark numbers are not gospel and vibes are not a system, and that the truth is inconveniently in between.  
**category**: youtube

**article** : [Act As a Real Researcher: A Suite of Benchmarks Evaluating Frontier LLMs and Agentic Harnesses in Research Lifecycle](https://arxiv.org/abs/2606.07462 "Act As a Real Researcher: A Suite of Benchmarks Evaluating Frontier LLMs and Agentic Harnesses in Research Lifecycle")  
**authors** : Jiayu Wang, Weijiang Lv, Bowen Fu, Jing Fu, Jiayi Song, Lingyu Zhang, Lanxuan Xue and others  
**date** : 2026-06-05  
**desc.** : Despite agents' evolution from research assistants into autonomous research agents, frontier agents remain unable to fully replace human researchers. However, existing benchmarks still suffer from two main limitations: 1) Lack of Researcher-Quality-Oriented Tasks, 2) Limited Human-Agent Difference Awareness. In the article, researchers conceptualize the AARR (Act As a Real Researcher) benchmark series (Real Research Intern, Assistant, Scientist) and report achievements with discussed limitations. Alongside advances in model capabilities, harness and scaffolding design has become increasingly important for reliable agent execution. From the overall evaluation results presented, the authors observe that the highest-performing configuration is the combination of Mini-SWEAgent and Claude-Opus-4.7, achieving an overall success rate of 68.3%. This outperforms more complex, feature-rich harnesses, such as Hermes Agent (64.6%) and Claude Code (62.2%). The article provides future research directions and approaches to overcome these limitations.  
**category**: research

**article** : [Harnesses in AI: A Deep Dive --- Tejas Kumar](https://www.youtube.com/watch?v=C_GG5g38vLU "Harnesses in AI: A Deep Dive — Tejas Kumar")  
**authors** : AI Engineer, Tejas Kumar (IBM)  
**date** : 2026-05-17  
**desc.** : The agent hit a login page, panicked, reported success anyway, and the upvote never happened. Tejas Kumar's diagnosis: not a prompt problem. A harness problem.  

The demo builds a browser agent on GPT-3.5 Turbo (consciously choosing a VERY old model to show how good harness eng can improve it a lot) against Hacker News and layers in a harness without touching the prompt once. Guardrails cap iterations and compact context. A verify step reads the tool call history to catch the agent lying about what it did. A login handler watches the browser URL each loop and injects credentials programmatically when it hits the login page. By the end the cheap old model reliably logs in and upvotes the post.  

, [github link](https://github.com/TejasQ?tab=repositories "github link")  
**category**: youtube

**article** : [Harness Engineering: How to Build Software When Humans Steer, Agents Execute --- Ryan Lopopolo](https://www.youtube.com/watch?v=am_oeAoUhew "Harness Engineering: How to Build Software When Humans Steer, Agents Execute — Ryan Lopopolo")  
**authors** : AI Engineer, Rayn Lopopolo (OpenAI)  
**date** : 2026-04-17  
**desc.** : Although code may seem free to produce, this assumption comes with questions and challenges that may harm the final result. Ryan suggests that engineers think 6 months ahead on a project, touching on several crucial points such as maintainability, stability, reproducibility, and determinism. The presentation offers a harness engineering approach to overcome these struggles while grounding agent behaviour, alongside promoting fast iterations through continual refactoring. Such an approach may open space for various questions, as it may appear that engineers are simply shifting misleading solutions onto the model. Nevertheless, Ryan is a lucky 'token billionaire', which allows him to maintain large context windows to prodoce good enough results. What is Harness Engineering ?  
**category**: youtube
