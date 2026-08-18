---
title: "JC-AI Newsletter #9"
date: "2025-11-12T15:21:12+00:00"
lastmod: "2025-11-12T15:21:14+00:00"
description: "Fourteen days have passed, and it is time to present a fresh collection of readings that could influence developments in the field of artificial - by Miro Wengner"
authors:
  - "miro-wengner"
image: "ai-insider.png"
categories:
  - "AI"
  - "Design Patterns"
  - "GenAI"
  - "Java"
  - "JC-AI Newsletter"
  - "Machine Learning"
  - "Research"
  - "Tutorials"
  - "Videos"
tags:
related_posts:
  - "jc-ai-newsletter-10"
  - "build-secure-ai-chat-applications-with-boxlang-rag-ollama-and-amazon-bedrock-with-dan-card"
  - "tiberius-a-security-testing-framework-for-llm-applications-in-java"
  - "boxlang-ai-3-2-0-image-generation-web-search-fluent-audio-agent-registry-mcp-observability"
frozen: false
---

**F**ourteen days have passed, and it is time to present a fresh collection of readings that could influence developments in the field of artificial intelligence.

This newsletter focuses on examining how AI enhances productivity through enterprise studies, tutorial, agentic system architecture, GraphRAG, evaluating risk methodologies in agentic systems, and the security challenges arising from increased AI-LLM adoption. This edition of the AI newsletter includes a compelling discussion between six of the most influential leaders in artificial intelligence, along with additional content.

The world influenced by LLM is changing very quickly, let's start...

**article** : [The Minds of Modern AI: Jensen Huang, Geoffrey Hinton, Yann LeCun \& the AI Vision of the Future](https://www.youtube.com/watch?v=0zXSrsKlm5A "The Minds of Modern AI: Jensen Huang, Geoffrey Hinton, Yann LeCun &amp; the AI Vision of the Future")  
**authors** : Financial Times Live  
**date** : 2025-11-06  
**desc.** : Six of the most influential figures in AI (**Jensen Huang, Yoshua Bengio, Geoffrey Hinton, Fei-Fei Li, Yann LeCun, and Bill Dally** ) share their vision for the future of the field. Defining a clear future horizon for AI remains a challenging goal. The interviewees appear to grapple with questions regarding concrete AI contributions and the trajectory of progress, avoiding discussion of current challenges while expressing hope that future research will adequately address these issues.  
**category**: youtube

**article** : [GraphRAG: The Marriage of Knowledge Graphs and RAG: Emil Eifrem](https://www.youtube.com/watch?v=knDDGYHnnSI "GraphRAG: The Marriage of Knowledge Graphs and RAG: Emil Eifrem")  
**authors** : Emil Eifrem, AI Engineers  
**date** : 2024-08-28  
**desc.** : Although GraphRAG has made dramatic progress, the fundamentals are sometimes overlooked in favor of introducing additional features. As the saying goes, 'Natural language is most powerful when it can draw from a rich context.' This principle applies equally to both poetry and large language models. Knowledge graphs excel at capturing context, which raises an important question: how can combining knowledge graphs with RAG enhance this capability?  
**category**: youtube

**article** : [GraphRAG: Unlocking LLM discovery on narrative private data](https://www.microsoft.com/en-us/research/blog/graphrag-unlocking-llm-discovery-on-narrative-private-data/ "GraphRAG: Unlocking LLM discovery on narrative private data")  
**authors** : Jonathan Larson, Steven Truitt (Microsoft)  
**date** : 2024-02-13  
**desc.** : A remaining challenge for LLMs is extending their powerful capabilities to solve problems beyond their training data and to achieve comparable results with data the LLM has never encountered. Although the Microsoft Research work on GraphRAG is already somewhat dated given the current pace of LLM development, it remains valuable to understand the fundamentals, rationale, and purpose of GraphRAG. GraphRAG may play an important role in the development of agentic AI systems.  
**category**: research

**article** : [Agentic GraphRAG: Simplifying Retrieval Across Structured \& Unstructured Data --- Zach Blumenfeld](https://www.youtube.com/watch?v=CzM3cW6FdBs "Agentic GraphRAG: Simplifying Retrieval Across Structured &amp; Unstructured Data — Zach Blumenfeld")  
**authors** : Zach Blumenfeld, AI Engineers  
**date** : 2025-06-27  
**desc.** : Agentic workflows often become complex, brittle, and difficult to maintain when they need to retrieve and reason across both structured and unstructured data. This talk explores how mapping key information into a knowledge graph can simplify these workflows and improve retrieval quality. The presented example of identifying individuals with similar skills and abilities extracted from CVs provides insight into the practical application of agentic AI systems with GraphRAG.  
**category**: youtube

**article** : [TAMAS: Benchmarking Adversarial Risks in Multi-Agent LLM Systems](https://arxiv.org/abs/2511.05269 "TAMAS: Benchmarking Adversarial Risks in Multi-Agent LLM Systems")  
**authors** : Ishan Kavathekar, Hemang Jain, Ameya Rathod, Ponnurangam Kumaraguru, Tanuja Ganu  
**date** : 2025-11-07  
**desc.** : The agentic AI systems are increasingly used to collaboratively solve problems. However, the safety and security of these systems remain largely under-explored. Existing benchmarks and datasets predominantly focus on single-agent settings, providing biased results and failing to capture the unique vulnerabilities of multi-agent dynamics and coordination. The paper aims to address a gap related to the safety, security, and various vulnerabilities of multi-agent LLM systems by introducing the Threats and Attacks in Multi-Agent Systems (TAMAS) benchmark. Reported findings show that multi-agent systems are highly vulnerable to adversarial attacks.  
**category**: research

**article** : [ORCHID: Orchestrated Retrieval-Augmented Classification with Human-in-the-Loop Intelligent Decision-Making for High-Risk Property](https://arxiv.org/abs/2511.04956 "ORCHID: Orchestrated Retrieval-Augmented Classification with Human-in-the-Loop Intelligent Decision-Making for High-Risk Property")  
**authors** : Maria Mahbub, Vanessa Lama, Sanjay Das, Brian Starks and others  
**date** : 2025-11-07  
**desc.** : High-Risk Property (HRP) classification is critical at U.S. Department of Energy (DOE) sites, where inventories include sensitive and often dual-use equipment. Compliance efforts must track evolving regulations designated by various export control policies to ensure transparent and auditable decisions. Traditional expert-only workflows are time-consuming, prone to backlogs, and struggle to keep pace with shifting regulatory boundaries. The paper introduces ORCHID, a modular agentic system for HRP classification that pairs retrieval-augmented generation (RAG) with human oversight to produce policy-based outputs that can be audited. "Although ORCHID enhances classification reliability, transparency, and reproducibility through evidence-based, policy-aware decision-making, it comes with several limiting factors: the precision and validity of source documents, ambiguity in decision-making processes, the requirement for qualified reviewers, and other constraints.  
**category**: research

**article** : [Multi-Agent Craftax: Benchmarking Open-Ended Multi-Agent Reinforcement Learning at the Hyperscale](https://arxiv.org/abs/2511.04904 "Multi-Agent Craftax: Benchmarking Open-Ended Multi-Agent Reinforcement Learning at the Hyperscale")  
**authors** : Bassel Al Omari, Michael Matthews, Alexander Rutherford, Jakob Nicolaus Foerster  
**date** : 2025-11-07  
**desc.** : Through analytical examination, the paper demonstrates that existing algorithms struggle with key challenges in this benchmark, including long-horizon credit assignment, exploration, and cooperation, and argues for its potential to drive long-term research in multi-agent reinforcement learning (MARL). MARL extends the reinforcement learning paradigm to the co-learning of multiple agents simultaneously. The paper introduces Craftax-MA and its extension Craftax-Coop, a multi-agent extension of the hardware-accelerated Craftax benchmark. The obtained results were limited by small agent populations, and future research directions are proposed.  
**category**: research

**article** : [StepChain GraphRAG: Reasoning Over Knowledge Graphs for Multi-Hop Question Answering](https://arxiv.org/abs/2510.02827 "StepChain GraphRAG: Reasoning Over Knowledge Graphs for Multi-Hop Question Answering")  
**authors** : Tengjun Ni, Xin Yuan, Shenghong Li, Kai Wu, Ren Ping Liu, Wei Ni, Wenjie Zhang  
**date** : 2025-10-03  
**desc.** : The paper addresses challenges of commonly used approaches that rely on static or ad hoc expansions of knowledge graphs. The paper introduces the StepChain GraphRAG framework, which combines question decomposition and BFS-RF (breadth-first search reasoning flow) with dynamic graph maintenance. This pipeline dynamically inserts new evidence at each sub-question, refining the knowledge graph in real time. The result is a more transparent, debuggable process for multi-hop question answering that fully exploits both text-based retrieval and graph-structured insights.  
**category**: research

**article** : [RAG Meets Temporal Graphs: Time-Sensitive Modeling and Retrieval for Evolving Knowledge](https://arxiv.org/abs/2510.13590 "RAG Meets Temporal Graphs: Time-Sensitive Modeling and Retrieval for Evolving Knowledge")  
**authors** : Jiale Han, Austin Cheung, Yubai Wei and others  
**date** : 2025-10-15  
**desc.** : While Retrieval-Augmented Generation (RAG) systems enrich LLMs with external knowledge, they largely ignore temporal dynamics, which raises two challenges for RAG systems. First, current RAG methods lack effective time-aware representations. The same facts at different time points are difficult to distinguish using vector embeddings or conventional knowledge graphs. Second, most RAG evaluations assume a static corpus, leaving a blind spot regarding update costs and retrieval stability as knowledge evolves. This paper introduces Temporal GraphRAG (TG-RAG), which incorporates time-aware retrieval strategies. Although TG-RAG outperforms current baselines, it comes with several challenges.  
**category**: research

**article** : [TeaRAG: A Token-Efficient Agentic Retrieval-Augmented Generation Framework](https://arxiv.org/abs/2511.05385 "TeaRAG: A Token-Efficient Agentic Retrieval-Augmented Generation Framework")  
**authors** : Chao Zhang, Yuhao Wang, Derong Xu, Haoxin Zhang and others  
**date** : 2025-11-07  
**desc.** : Retrieval-Augmented Generation (RAG) enhances Large Language Models' reliability through external knowledge integration. While agentic RAG systems use autonomous, multi-round retrieval for improved accuracy, they generate substantial token overhead. TeaRAG addresses this efficiency challenge by compressing both retrieval content and reasoning steps, delivering a token-efficient agentic RAG framework that balances accuracy with computational economy.  
**category**: research

**article** : [The Learning Loop and LLMs](https://martinfowler.com/articles/llm-learning-loop.html "The Learning Loop and LLMs")  
**authors** : Unmesh Joshi, Thoughtworks  
**date** : 2025-11-04  
**desc.** : Software development has consistently resisted the notion that it can be reduced to an assembly-line process. Even as our tools become smarter, faster, and more capable, the essential nature of the work remains unchanged: we learn by doing. We must acknowledge the fundamental role of experiential learning in this field, "***there are no shortcuts to learning*** ".  
**category**: opinion

**article** : [Driving a web browser with Gemini's Computer Use model in Java](https://glaforge.dev/posts/2025/11/03/driving-a-web-browser-with-gemini-computer-use-model-in-java/ "Driving a web browser with Gemini's Computer Use model in Java")  
**authors** : Guillaume Laforge  
**date** : 2025-11-02  
**desc.** : This tutorial will guide you through the process of programmatically interacting with a web browser using the new Computer Use model in Gemini 2.5 Pro. The tutorial presents an example project written in Java that leverages Microsoft's powerful Playwright Java SDK to handle browser automation. Multi-agentic systems may complement classical end-to-end tests, but several challenges remain, including hallucination.  
**category**: tutorial
