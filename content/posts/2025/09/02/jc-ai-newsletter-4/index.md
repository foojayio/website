---
title: "JC-AI Newsletter #4"
slug: "jc-ai-newsletter-4"
date: "2025-09-02T20:27:52+00:00"
lastmod: "2025-10-14T05:41:38+00:00"
description: "14 days have passed and it's time for a new batch of readings that could shape developments in the field of artificial intelligence."
authors:
  - "miro-wengner"
image: "ai-insider-1.png"
categories:
  - "AI"
  - "GenAI"
  - "Java"
  - "JC-AI Newsletter"
  - "LangChain4j"
  - "LLM"
  - "Machine Learning"
  - "Research"
tags:
related_posts:
  - "ai-newsletter-1"
  - "jc-ai-newsletter-2"
  - "jc-ai-newsletter-3"
  - "do-we-understand-the-value-of-ai-knowledge"
frozen: false
---

14 days have passed and it's time for a new batch of readings that could shape developments in the field of artificial intelligence.

The current newsletter vol. 4 offers us a closer look at several different areas of artificial intelligence. We start with the topic of energy consumption and the environmental impact of systems serving artificial intelligence, and continue with automation, how we obtain data for RAG, robustness of GenAI systems, vibe-coding and more.

The world influenced by LLM is changing very quickly, let's start...

**article** : [Measuring the environmental impact of delivering AI at Google Scale](https://arxiv.org/abs/2508.15734)  
**authors** : Cooper Elsworth, Keguo Huang, David Patterson, Ian Schneider, Robert Sedivy, Savannah Goodman, Ben Townsend, Parthasarathy Ranganathan, Jeff Dean, Amin Vahdat, Ben Gomes, James Manyika **date** : 2025-08-21  
**desc.** : The paper addresses environmental impact gaps by designing and implementing a comprehensive methodology for measuring energy consumption, carbon emissions, and water consumption of artificial intelligence inference tasks in a large-scale Google AI production environment. The paper discussed the fact that the impact of AI serving can be significantly underestimated by existing, narrower measurement approaches  
**category**: research

**article** :[Breaking Barriers in Software Testing: The Power of AI-Driven Automation](https://arxiv.org/abs/2508.16025)  
**authors** : Saba Naqvi, Mohammad Baqar  
**date** : 2025-08-22  
**desc.** : This paper proposes a framework for automated test generation driven by artificial intelligence through learning, validation of results through real-time analytics, and bias mitigation. The framework uses natural language processing (NLP), reinforcement learning (RL), and predictive models to translate requirements into tests. The paper addresses efficiency, quality, maintainability, potential risks, and bottlenecks in simulated scenarios.  
**category**: research

**article** : [Data Auctions for Retrieval Augmented Generation](https://arxiv.org/abs/2508.16007)  
**authors** : Minbiao Han, Seyed A. Esmaeili, Michael Albert, Haifeng Xu  
**date** : 2025-08-21  
**desc.** : This paper proposes an optimization for solving the data sales challenge for Retrieval Augmented Generation (RAG) tasks used in generative artificial intelligence (GenAI) applications to maximize revenue. The paper demonstrates the practical effectiveness of the proposed algorithm compared to traditional methods used by buyers on datasets considering synthetic and real-world images and texts.  
**category**: research

**article** : [Foundational Design Principles and Patterns for Building Robust and Adaptive GenAI-Native Systems](https://arxiv.org/abs/2508.15411)  
**authors** : Frederik Vandeputte  
**date** : 2025-08-21  
**desc.** : The paper proposes a paradigm shift in the development of GenAI systems by integrating cognitive capabilities with traditional software engineering principles to create robust, adaptive, and efficient systems. The paper addresses and describes challenges over design patterns utilization during GenAI system development.  
**category**: research

**article** : [Quantifying Uncertainty in Error Consistency: Towards Reliable Behavioral Comparison of Classifiers](https://arxiv.org/abs/2507.06645)  
**authors** : Thomas Klein, Sascha Meyen, Wieland Brendel, Felix A. Wichmann, Kristof Meding  
**date** : 2025-07-09  
**desc.** : The paper presents a methodology that allows designing statistically reliable experiments capable of detecting behavioral differences between models and humans. This allows ranking different deep neural networks (DNNs) according to their behavioral consistency with humans, the so-called Error Consistency (EC). The paper shows how deep neural networks (DNNs) can be used to formulate new experiments and design at least 1000 trails per classification. The paper raises the question of how reliable benchmarks should be designed, as they are crucial for the progress of machine learning (ML).  
**category**: research

**article** : [Small Language Models are the Future of Agentic AI](https://arxiv.org/abs/2506.02153)  
**authors** : Peter Belcak, Greg Heinrich, Shizhe Diao, Yonggan Fu, Xin Dong, Saurav Muralidharan, Yingyan Celine Lin, Pavlo Molchanov  
**date** : 2025-06-02  
**desc** .: Although LLMs are often praised for their human-like responses in many domains, they require training models on large amounts of data to cover all possible domains, even those above the scope. This article aims to find arguments for the purpose of dividing such large LLMs into small, focused batches, small-language models (SMLs). This may seem like a very similar approach to dividing monolithic IT systems. The articles discuss the problems associated with this approach.  
**category**: research

**article** :[Academic Vibe Coding: Opportunities for Accelerating Research in an Era of Resource](https://arxiv.org/abs/2508.00952)  
**authors** : Matthew G Crowson, Leo Celi A. Celi  
**date** : 2025-08-01  
**desc.** : The article discusses the potential of overcoming financial pressure in academic fields by using LLM, and respectively the effectiveness of the Vibe coding approach for achieving competitive results.  
**category**: opinion, research

**article** : [Vibe Modeling: Challenges and Opportunities](https://arxiv.org/abs/2507.23120)  
**authors** : Jordi Cabot  
**date** : 2025-07-30  
**desc.** : The paper addresses the challenge of increasing application development complexity and the issues associated with Vibe coding at the expense of code vulnerabilities, scalability, and maintainability issues. The paper presents the Vibe Modeling approach using Large Language Models (LLM) compared to established model-driven engineering patterns. The paper discusses the usefulness and risks of Vibe Modeling for developing new applications.  
**category**: research

**artcile** : [Vibe-coding a Chrome extension with Gemini CLI to summarize articles](https://glaforge.dev/posts/2025/08/06/vibe-coding-a-chrome-extension-with-gemini-cli-to-summarize-articles/)  
**authors** : Guillaume Laforge  
**date** : 2025-08-06  
**desc.** : You know that moment when you\\'re staring at a web page full of text and having just on wish, summary? This article introduces the Gemini summarizer Chrome extension, built with Vibe-coding, and provides you with a walkthrough on how to do just that using JavaScript.  
**category**: tutorial

**article** : [Stochastic AI Agility: addressing cycle of debts](https://www.linkedin.com/pulse/stochastic-ai-agility-addressing-cycle-debts-miroslav-miro-wengner-aaerf/?trackingId=%2BYDEhTIMSJa56cOYYsn%2Fmw%3D%3D)  
**author** : Miro Wengner  
**date** : 2025-08-30  
**desc.** : Have you ever wondered what the impact of using Large Language Models (LLMs) is on different levels of the product development process? How does LLM shape not only agile methodologies (Agile, Scrum, Kanban, Waterfall)? This article aims to address these challenges by introducting term Stochastic AI Agility.  
**category**: opinion

**article** : [Mastering agentic workflows with ADK: the recap](https://glaforge.dev/posts/2025/07/29/mastering-agentic-workflows-with-adk-the-recap/)  
**authors** : Guillaume Laforge  
**date** : 2025-07-29  
**desc.** : The article provides a comprehensive deep introduction into agentic workflow orchestration capabilities by using Agent Development Kit (ADK). It dives into the challenge when, why and how to choose proper AI agentic system workflow with examples and additional tutorials.  
**category**: tutorial

**article** : [Investigating Advanced Reasoning of Large Language Models via Black-Box Interaction](https://arxiv.org/abs/2508.19035)  
**authors** : Congchi Yin, Tianyi Wu, Yankai Shu, Alex Gu, Yunhan Wang, Jun Shao, Xun Jiang, Piji Li  
**date** : 2025-08-26  
**desc.** : The paper elaborates on the fact that Large Language Models (LLMs) may fall short in unknown environments which may lead to the isolated assessment of neglecting the integrated reasoning process that is indispensable for human discovery of the real world. The paper introduces a new evaluation paradigm, black-box interaction, to overcome this challenge.  
**category**: research

**article** : [User-Centered Design with AI in the Loop: A Case Study of Rapid User Interface Prototyping with](https://arxiv.org/abs/2507.21012)  
**authors** : Tianyi Li, Tanay Maheshwari, Alex Voelker  
**date** : 2025-07-28  
**desc.** : The paper presents a case study of using the Vibe Coding approach using large language models (LLMs) to generate code using natural language instructions, to support rapid prototyping in user-centered design. The paper discusses errors occurrences which require extensive debugging conversation as such prompting deviates from design ideas. The paper touches on the security risks of generated code.  
**category**: research

**article** : [Tales from the jar side: GPT-5 from Java, gpt-oss, More theme song experiments, and the usual social media gags](https://kenkousen.substack.com/p/tales-from-the-jar-side-gpt-5-from?r=2dwq5&amp;utm_campaign=post&amp;utm_medium=web&amp;triedRedirect=true)  
**authors** : Ken Kousen  
**date** : 2025-08-10  
**desc.** : The release of ChatGPT-5 can be considered big news in the world of artificial intelligence. This article provides first-hand experience with the new model through examples and required changes in Java frameworks (Spring AI, LangChain4j).  
**category**: tutorial

**article** : [Through the Looking Glass: How Engineers Should Approach AI, Its Tools, and Adapt](https://www.linkedin.com/pulse/through-looking-glass-how-engineers-should-approach-ai-freddy-guime-ypwoc/)  
**authors** : Freddy Guime  
**date** : 2025-08-10  
**desc.** : This article discusses several reasons why today\\'s engineers should consider using large language models (LLMs) in their daily work and application development process. The article discusses the role of LLMs in various engineering positions from junior to senior levels.  
**category**: opinion

Enjoy reading and look forward to the next one!

### Previous: {#h3-0-previous}

[Newsletter vol. 1](https://foojay.io/today/ai-newsletter-1)  
[Newsletter vol. 2](https://foojay.io/today/jc-ai-newsletter-2)  
[Newsletter vol. 3](https://foojay.io/today/jc-ai-newsletter-3)
