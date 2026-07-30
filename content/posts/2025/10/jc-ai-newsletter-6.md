---
title: "JC-AI Newsletter #6"
slug: "jc-ai-newsletter-6"
date: "2025-10-01T17:18:16+00:00"
lastmod: "2025-10-14T05:43:05+00:00"
description: "Fourteen days have passed, and it is time to present a fresh collection of readings that could influence developments in the field of artificial intelligence."
authors:
  - "miro-wengner"
image: "https://foojay.io/wp-content/uploads/2025/07/ai-insider-1.png"
categories:
  - "AI"
  - "Java"
  - "JC-AI Newsletter"
  - "LangChain4j"
  - "LLM"
  - "Machine Learning"
  - "Tutorials"
tags:
related_posts:
frozen: false
---

**Fourteen days have passed, and it is time to present a fresh collection of readings that could influence developments in the field of artificial intelligence.**

Beyond opinion pieces and Java focused tutorials that can enhance your understanding of AI applications, this newsletter concentrates on Hallucination, Security, RAG and LLM benchmarking methodologies designed to ensure models accuracy and competency in handling complex contextual information.

The world influenced by LLM is changing very quickly, let's start...

article: [ADK for Java opening up the third-party Language Models via LangChain4j integration](https://developers.googleblog.com/en/adk-for-java-opening-up-to-third-party-language-models-via-langchain4j-integration/ "ADK for Java opening up the third-party Language Models via LangChain4j integration")  

authors: Guillaume Laforge  

date: 2025-09-16  

desc.: The ADK for Java framework for developing AI agents in Java added an integration with the LangChain4j LLM orchestration framework, giving developers to choose from all the LLMs supported by LangChain4j for developing their ADK agents.  

category: tutorial

article: [Creative Java AI agents with ADK and Nano Banana](https://glaforge.dev/posts/2025/09/22/creative-ai-agents-with-adk-and-nano-banana/ "Creative Java AI agents with ADK and Nano Banana")  

authors: Guillaume Laforge  

date: 2025-09-22  

desc.: Taking advantage of chat models that can generate both text and images, to create creative Java AI agents with the ADK framework.  

category: tutorial

article: [Position: AI Safety Must Embrace an Antifragile Perspective](https://arxiv.org/abs/2509.13339 "Position: AI Safety Must Embrace an Antifragile Perspective")  

authors: Ming Jin, Hyunin Lee  

date: 2025-09-11  

desc.: This paper challenges conventional static benchmarks and single-shot robustness tests, which may overlook the fact that the LLM landscape is constantly evolving and that models, when left unchallenged, can drift toward adaptive hallucination at scale.This could not only increase attack vectors but also evolve into a stochastic chain of unwilling events. The paper suggests a series of steps to mitigate such behavior through a list called 'Red Flags of Fragility'  

category: research

article: [All for law and law for all: Adaptive RAG Pipeline for Legal Research](https://arxiv.org/abs/2508.13107 "All for law and law for all: Adaptive RAG Pipeline for Legal Research")  

date: 2025-08-19  

desc.: Large Language Models (LLMs) frequently experience hallucinations that can lead to false or inaccurate conclusions, potentially causing various forms of harm or damage in the legal domain. This paper presents a new approach to end-to-end Retrieval-Augmented Generation (RAG) pipelines that aims to address inconsistencies through three key components: a context-aware query translator, open-source retrieval strategies employing SBERT and GTE embeddings, and a comprehensive evaluation framework that integrates RAGAS, BERTScore-F1, and ROUGE-Recall metrics. Beyond reporting achieved improvements, the paper provides a thorough discussion of methodological and experimental limitations.  

category: research

article: [A Scoping Review of Machine Learning Applications in Power System Protection and Disturbance Management](https://arxiv.org/abs/2509.09053 "A Scoping Review of Machine Learning Applications in Power System Protection and Disturbance Management")  

authors: Julian Oelhaf, Georg Kordowich, Mehran Pashaei, Christian Bergler and others.  

date: 2025-08-10  

desc.: While machine learning applications frequently achieve high accuracy in simulated environments, their validation in real-time scenarios remains inadequate. This paper addresses the critical issue of lacking or incompatible standardization approaches within Power System Protection and Disturbance Management, a deficiency that renders cross-study comparisons of reported achievements problematic. This paper provides a comprehensive evaluation of various methodologies for assessing Fault Detection, Classification, and Localization systems. Additionally, it proposes standardized processes and examines potential challenges while outlining future research opportunities.  

category: research

article: [SAGE: A Realistic Benchmark for Semantic Understanding](https://arxiv.org/abs/2509.21310 "SAGE: A Realistic Benchmark for Semantic Understanding")  

authors: Samarth Goel, Reagan J. Lee, Kannan Ramchandran  

date: 2025-09-25  

desc.: This paper introduces the novel SAGE Benchmark for evaluating semantic understanding through alignment and generalization assessment, while accounting for text noise, information sensitivity, clustering performance, and stress-test-based retrieval robustness. The paper demonstrates its performance compared to traditional approaches and outlines directions for future research.  

category: research

article: [Mixture of Thoughts: Learning to Aggregate What Experts Think, Not Just What They Say](https://arxiv.org/abs/2509.21164 "Mixture of Thoughts: Learning to Aggregate What Experts Think, Not Just What They Say")  

authors: Jacob Fein-Ashley, Dhruv Parikh, Rajgopal Kannan, Viktor Prasanna  

date: 2025-09-25  

desc.: The paper introduces the Mixture of Thoughts(MoT) approach that offers a simple latent-space mechanism (experts cross-attention and actors collaboration space) for combining LLMs, a practical step toward broader multi-LLM collaboration.  

category: research

article: [PerHalluEval: Persian Hallucination Evaluation Benchmark for Large Language Models](https://arxiv.org/abs/2509.21104 "PerHalluEval: Persian Hallucination Evaluation Benchmark for Large Language Models")  

authors: Mohammad Hosseini, Kimia Hosseini, Shayan Bali, Zahra Zanjani, Saeedeh Momtazi  

date: 2025-09-25  

desc.: Although this paper focuses on Persian texts, it may provide valuable insights into how LLMs perform in non-English contexts. The article demonstrates that providing external knowledge can partially mitigate hallucination phenomena while also revealing no significant performance difference between models trained on Persian texts and other models. The paper provides a critical analysis of the achieved results.  

category: research

article: [Towards Synthesizing Normative Data for Cognitive Assessments Using Generative Multimodal Large Language Models](https://arxiv.org/abs/2508.17675 "Towards Synthesizing Normative Data for Cognitive Assessments Using Generative Multimodal Large Language Models")  

authors: Victoria Yan, Honor Chotkowski, Fengran Wang, Xinhui Li and others.  

date: 2025-08-25  

desc.: This paper investigates the utilization of multimodal large language models to generate synthetic normative data from existing cognitive test images. The analysis employs BLEU, ROUGE, BERTScore metrics and LLM-as-a-judge evaluation strategies. Despite results, the utilization of LLMs may introduce new challenges, including bias, error propagation, and reproducibility issues. Hallucination remains a significant challenge in synthetic data generation.  

category: research

article: [Enhancing COBOL Code Explanations: A Multi-Agents Approach Using Large Language Models](https://arxiv.org/abs/2507.02182 "Enhancing COBOL Code Explanations: A Multi-Agents Approach Using Large Language Models")  

authors: Fangjian Lei, Jiawen Liu, Shayan Noei, Ying Zou, Derek Truong, William Alexander  

date: 2025-07-02  

desc.: Despite a COBOL programming language age it remains crucial for financial institutions, government agencies and large corporations to handle critical tasks due to its reliability. Although COBOL has a business-oriented, English-like syntax, the lack of documentation for implemented concepts may cause significant challenges in project migration, even when LLM models are utilized. This paper reports improvements in analyzing source code that exceeds LLMs' token size window while reading source files, using common benchmarks: METEOR, chrF, and SentenceBERT.  

category: research

article: [Library Hallucinations in LLMs: Risk Analysis Grounded in Developer Queries](https://arxiv.org/abs/2509.22202 "Library Hallucinations in LLMs: Risk Analysis Grounded in Developer Queries")  

authors: Lukas Twist, Jie M. Zhang, Mark Harman, Helen Yannakoudakis  

date: 2025-09-26  

desc.: Despite the increasing risks associated with using Large Language Models (LLMs) for system development, vibe coding has gained popularity in the application development process. However, this approach remains problematic due to hallucination issues that may lead to unintended results or overlooked bottlenecks. This paper provides a comprehensive study on the usage of libraries in LLM-generated code and highlights an urgent need for safeguards against library-related hallucinations.  

category: research

Previous:  
[Newsletter vol.1](https://foojay.io/today/ai-newsletter-1/ "Newsletter vol.1")  
[Newsletter vol.2](https://foojay.io/today/jc-ai-newsletter-2/ "Newsletter vol.2")  
[Newsletter vol.3](https://foojay.io/today/jc-ai-newsletter-3/ "Newsletter vol.3")  
[Newsletter vol.4](https://foojay.io/today/jc-ai-newsletter-4/ "Newsletter vol.4")  
[Newsletter vol.5](https://foojay.io/today/jc-ai-newsletter-5/ "Newsletter vol.5")
