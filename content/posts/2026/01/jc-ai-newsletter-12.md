---
title: "JC-AI Newsletter #12"
slug: "jc-ai-newsletter-12"
date: "2026-01-14T07:15:44+00:00"
lastmod: "2026-02-05T20:40:57+00:00"
description: "First of all, Happy New Year 2026! This year is designated in the Chinese Calendar as the Year of the Fire Horse (starting on February 17.). The year 2026 - by Miro Wengner"
authors:
  - "miro-wengner"
image: "/images/posts/2026/01/jc-ai-newsletter-12/ai-insider-1.png"
categories:
  - "AI"
  - "Data Engineering"
  - "Design Patterns"
  - "GenAI"
  - "JC-AI Newsletter"
  - "Machine Learning"
  - "Opinion"
  - "Performance"
  - "Research"
tags:
related_posts:
  - "boxlang-ai-deep-dive-part-6-of-7-memory-systems-rag-building-ai-that-remembers"
  - "boxlang-ai-deep-dive-part-2-of-7-building-a-production-grade-ai-tool-ecosystem"
  - "jc-ai-newsletter-11"
  - "jc-ai-newsletter-9"
frozen: false
---

**F** irst of all, **Happy New Year 2026!** This year is designated in the Chinese Calendar as the Year of the Fire Horse (starting on February 17.). The year 2026 brings not only tremendous energy to AI development but also, in my humble opinion, many breakthroughs in the field.

Although there have been many small steps toward the field's evolution, it often feels that development is stagnating, applying known or slightly tweaked strategies to non-deterministic problems while expecting deterministic results. This includes the often misleading benchmarking strategies (deterministic) performed on synthetic datasets.

The first New Year edition of the JC-AI Newsletter aims to shed light on new approaches and movements in the field, including the directions of its evolution.

Let's jump in and **happy reading**!

**article** : [Driving is a Game: Combining Planning and Prediction with Bayesian Iterative Best Response](https://arxiv.org/abs/2512.03936 "Driving is a Game: Combining Planning and Prediction with Bayesian Iterative Best Response")  
**authors** : Aron Distelzweig, Yiwei Wang, Faris Janjoš and others  
**date** : 2025-12-03  
**desc.** : Autonomous driving, specifically decision-making, remains a significant challenge. While routine scenarios yield nearly perfect plans using multi-agent collaboration, dense urban traffic presents considerable difficulties, particularly for vehicle lane changes. This paper presents the Bayesian Iterative Best Response (BIReR) framework, which aims to unify motion prediction and planning based on game theory. The framework demonstrates an 11% improvement in lane change performance compared to classical approaches.  
**category**: research

**article** : [PBFuzz: Agentic Directed Fuzzing for PoV Generation](https://arxiv.org/abs/2512.04611 "PBFuzz: Agentic Directed Fuzzing for PoV Generation")  
**authors** : Haochen Zeng, Andrew Bao, Jiajun Cheng, Chengyu Song  
**date** : 2025-12-04  
**desc.** : Proof-of-Vulnerability (PoV) input generation is a critical task in software security. Generating a PoV input requires solving two sets of constraints: (1) reachability constraints for reaching the vulnerable code location(s), and (2) triggering constraints for activating the target vulnerability. Despite dramatic advancements in the LLM field, fuzzing models struggle to solve these constraints effectively. This paper proposes the PBFuzz framework, composed of four layers and enabling property-based directed fuzzing. Although PBFuzz underperformed in several scenarios, it outperforms conventional fuzzers overall.  
**category**: research

**article** : [DSPy: The End of Prompt Engineering - Kevin Madura, AlixPartners Enhancement](https://www.youtube.com/watch?v=-cKUW6n8hBU "DSPy: The End of Prompt Engineering - Kevin Madura, AlixPartners Enhancement")  
**authors** : AI Engineer, Kevin Madura  
**date** : 2026-01-08  
**desc.** : Applications developed for enterprise environments need to be rigorous, testable, and robust. The same is true for AI-powered applications, but LLMs can make this challenging. In other words, users need to be able to program with LLMs, not just tweak prompts. This talk covers why DSPy may be all users need when building applications with LLMs. Although the talk dives into some real-world examples, the audience is encouraged to explore the DSPy tool themselves to determine whether it fits their particular needs.  
**category**: youtube

**article** : [From Vibe Coding To Vibe Engineering -- Kitze, Sizzy](https://www.youtube.com/watch?v=JV-wY5pxXLo "From Vibe Coding To Vibe Engineering – Kitze, Sizzy")  
**authors** : AI Engineer, Ryan Florence  
**date** : 2025-12-14  
**desc.** : Web development has always moved in cycles of hype, from frameworks to tooling. With the rise of large language models, we're entering a new era of "vibe coding," where developers shape software through collaboration with Al rather than syntax. This talk explores what that means for the future of coding, especially in frontend development, and how it echoes the past while redefining what comes next.  
**category**: youtube

**article** : [The AI Bubble Should Have Never Existed In The First Place](https://www.planetearthandbeyond.co/p/the-ai-bubble-should-have-never-existed?r=6hpwx&amp;utm_medium=ios&amp;triedRedirect=true "The AI Bubble Should Have Never Existed In The First Place")  
**authors** : Will Lockett  
**date** : 2025-12-07  
**desc.** : The article elaborates on the existence of an AI bubble, arguing that so much money has been poured into AI that we have effectively bet the entire economy on its success. Regardless of whether an AI bubble exists or in what form, the article formulates valid points that should be taken into account when considering future developments.  
**category**: opinion

**article** : [We Let AI Run Our Office Vending Machine. It Lost Hundreds of Dollars](https://www.wsj.com/tech/ai/anthropic-claude-ai-vending-machine-agent-b7e84e34 "We Let AI Run Our Office Vending Machine. It Lost Hundreds of Dollars")  
**authors** : The Wall Street Journal (Antropic)  
**date** : 2025-12-18  
**desc.** : In a research case study supported by Anthropic, the Claudius Agent was developed to manage vending machine operations. Testing revealed multiple exploitable vulnerabilities that allowed users to obtain goods without payment. Real-world trials consistently resulted in operational failures, with the system dispensing free products while automatically reordering inventory, a combination that would lead to bankruptcy in commercial-like deployment.  
**category**: youtube

**article** : [When Small Models Are Right for Wrong Reasons: Process Verification for Trustworthy Agents](https://arxiv.org/abs/2601.00121 "When Small Models Are Right for Wrong Reasons: Process Verification for Trustworthy Agents")  
**authors** : Yaqi Duan, Yichun Hu, Jiashuo Jiang  
**date** : 2025-12-31  
**desc.** : Inventory control (encompassing cash management, storage, order quantities, etc.) presents a stochastic control challenge where minor structural errors result in recurring costs. Direct interaction with LLM models may produce plausible yet systematically suboptimal or even inconsistent results. This paper proposes using LLMs not as problem solvers but as language interfaces to enhance optimization through a hybrid agentic approach.  
**category**: research

**article** : [Memory in LLMs: Weights and Activations - Jack Morris, Cornell](https://www.youtube.com/watch?v=Jty4s9-Jb78 "Memory in LLMs: Weights and Activations - Jack Morris, Cornell")  
**authors** : AI Engineer, Jack Morris  
**date** : 2025-12-29  
**desc.** : This work examines memory mechanisms in large language models through the lens of weights and activations. Jack Morris addresses the limitations of current Large Language Models (LLMs) in handling niche, long-tail knowledge that falls outside their training data or beyond knowledge cutoffs. He critiques the reliance on massive context windows and Retrieval Augmented Generation (RAG), citing their high computational cost and latency due to the quadratic complexity of self-attention. The core thesis advocates for a third paradigm: training knowledge into weights, efficiently injecting specific knowledge directly into model parameters. This approach treats weights as a memory storage mechanism, conceptually distinct from the working memory represented by activations.  
**category**: youtube

**article** : [There are no new ideas in AI --- only new datasets](https://www.freethink.com/artificial-intelligence/ai-datasets "There are no new ideas in AI — only new datasets")  
**authors** : Jack Morris  
**date** : 2025-07-06  
**desc.** : This article provides a comprehensive overview of progress in the AI field over recent years. All four major breakthroughs in LLMs occurred because researchers unlocked new sources of data. The question remains: what will be the next breakthrough?  
**category**: opinion

**article** : [VL-JEPA: Joint Embedding Predictive Architecture for Vision-language](https://arxiv.org/abs/2512.10942 "VL-JEPA: Joint Embedding Predictive Architecture for Vision-language")  
**authors** : Delong Chen, Mustafa Shukor, Theo Moutakanni, Willy Chung, Jade Yu, Tejaswi Kasarla, Allen Bolourchi, Yann LeCun, Pascale Fung  
**date** : 2025-12-11  
**desc.** : This paper introduces the Joint Embedding Predictive Architecture for Vision-Language models (VL-JEPA). Current Vision-Language Models (VLMs) are straightforward but inadequate for two main reasons. First, VLMs are expensive to develop. Second, real-time tasks involving live streaming video (e.g., live action tracking) require sparse and selective decoding. The paper empirically validates the advantages of this newly introduced approach against token-generative VLMs. VL-JEPA delivers consistently higher performance on zero-shot captioning and classification while improving inference-time efficiency during the training phase. Although improvements remain in the experimental stage, the work demonstrates clear benefits from scaling both parameters and dataset size.  
**category**: research

**article** : [Rephrasing the Web: A Recipe for Compute and Data-Efficient Language Modeling](https://arxiv.org/abs/2401.16380 "Rephrasing the Web: A Recipe for Compute and Data-Efficient Language Modeling")  
**authors** : Pratyush Maini, Skyler Seto, He Bai, David Grangier, Yizhe Zhang, Navdeep Jaitly (Carnegie Mellon Univeristy, Apple)  
**date** : 2024-01-29  
**desc.** : Although this paper is older, it may shed light on the approaches chosen for training LLM models and provide better understanding of their evolution. The paper proposes Web Rephrase Augmented Pre-training (WRAP), which uses an off-the-shelf instruction-tuned model to rephrase noisy input data. It offers insights into how the structure of training data impacts LLM performance.  
**category**: research

**article** : [When Small Models Are Right for Wrong Reasons: Process Verification for Trustworthy Agents](https://arxiv.org/abs/2601.00513 "When Small Models Are Right for Wrong Reasons: Process Verification for Trustworthy Agents")  
**authors** : Laksh Advani  
**date** : 2026-01-01  
**desc.** : This paper investigates the reasoning performance of agentic systems based on small language models (Mistral-7B, Llama-3-8B, Qwen-2.5-7B). The findings reveal statistically significant evidence that RAG systems may improve reasoning performance while simultaneously increasing the likelihood of hallucination due to the Right-for-Wrong-Reason (RWR) phenomenon. The paper introduces the Reasoning Integrity Score (RIS) approach to identify hidden flaws in reasoning processes.  
**category**: research
