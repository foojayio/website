---
title: "JC-AI Newsletter #11"
date: "2025-12-09T16:12:01+00:00"
lastmod: "2025-12-11T10:00:32+00:00"
description: "Fourteen days have passed, and it is time to present a fresh collection of readings that could influence developments in the field of artificial - by Miro Wengner"
authors:
  - "miro-wengner"
image: "ai-insider-1.png"
categories:
  - "AI"
  - "DataEngineering"
  - "Design Patterns"
  - "Foojay"
  - "GenAI"
  - "JC-AI Newsletter"
  - "Research"
  - "Videos"
related_posts:
  - "jc-ai-newsletter-14"
  - "jc-ai-newsletter-8"
  - "jc-ai-newsletter-2"
  - "build-secure-ai-chat-applications-with-boxlang-rag-ollama-and-amazon-bedrock-with-dan-card"
frozen: false
---

**F**ourteen days have passed, and it is time to present a fresh collection of readings that could influence developments in the field of artificial intelligence.

**T**his newsletter explores the evolution of agentic AI systems, provides valuable insights into the Chain-of-Thought (CoT) approach, Vibe coding, and discusses the pattern-matching capabilities of LLMs. The newsletter features an insightful interview with Stuart J. Russell, known for his significant contributions to the AI field. Even more exciting is the published paper by Apple researchers titled 'The Illusion of Thinking...' and several immediate reactions to the authors' conclusions, which allow newsletter readers to observe current research challenges and scientific community responses. This provides readers with a vital picture of the state-of-the-art in AI research.

**article** : [AI Expert: (Warning) 2030 Might Be The Point Of No Return! We've Been Lied To About AI!](http://https://www.youtube.com/watch?v=P7Y-fynYsgE "AI Expert: (Warning) 2030 Might Be The Point Of No Return! We've Been Lied To About AI!")  
**authors** : The Diary Of A CEO  
**date** : 2025-12-04  
**desc.** : AI Expert [Stuart J. Russel](https://en.wikipedia.org/wiki/Stuart_J._Russell), exposes the trillion-dollar AI race, why governments won't regulate, how AGI could replace humans by 2030, and why only a nuclear-level AI catastrophe will wake us up. NOTE: During the interview, a crucial question arises: If you had a 'red button' that could erase all AI-LLM current development, would you press it?... hear the answer with reasons  
**category**: youtube, interivew

**article** : [Is Chain-of-Thought Reasoning of LLMs a Mirage? A Data Distribution Lens](https://arxiv.org/abs/2508.01191 "Is Chain-of-Thought Reasoning of LLMs a Mirage? A Data Distribution Lens")  
**authors** : Chengshuai Zhao, Zhen Tan, Pingchuan Ma, Dawei Li, Bohan Jiang and others  
**date** : 2025-08-02, revisited 2025-08-13  
**desc.** : The aim of the Chain-of-Thought (CoT) approach is to produce human-like reasoning steps, but this may be more superficial than it appears. This paper studies CoT using data distribution analysis to enable observation of reasoning paths. For this purpose, the DataAlchemy environment has been designed. Systematic validation reveals that CoT exhibits sharp performance degradation when detecting unknown patterns.  
**category**: research

**article** : [The BS-meter: A ChatGPT-Trained Instrument to Detect Sloppy Language-Games](https://arxiv.org/abs/2411.15129 "The BS-meter: A ChatGPT-Trained Instrument to Detect Sloppy Language-Games")  
**authors** : Alessandro Trevisan, Harry Giddens, Sarah Dillon, Alan F. Blackwell (Cambridge)  
**date** : 2024-11-22, revisited 2025-06-10  
**desc.** : Using hypothesis-testing methods, this paper demonstrates that a statistical model of sloppy language can reliably generate the artificial output of ChatGPT to the social and workplace referred to bullshit as observed in natural human language. The paper presents an empirical investigation of LLM behavior that offers insights into language use while clarifying the social and epistemological status of LLMs themselves. The results indicate with high significance that ChatGPT's outputs resemble bullshit jobs rather than precise, factual scientific writing. While this is often evident from observing its outputs, the mechanisms by which such imprecise language is produced have not been previously established.  
**category**: research

**article** : [Comparison of Text-Based and Image-Based Retrieval in Multimodal Retrieval Augmented Generation Large Language Model Systems](https://arxiv.org/abs/2511.16654 "Comparison of Text-Based and Image-Based Retrieval in Multimodal Retrieval Augmented Generation Large Language Model Systems")  
**authors** : Elias Lumer, Alex Cardenas, Matt Melich, Myles Mason, Sara Dieter and others (PricewaterhouseCoopers)  
**date** : 2025-11-25  
**desc.** : This paper discusses the capabilities of Retrieval-Augmented Generation (RAG) systems to access multimodal knowledge bases containing both text and visual information, such as charts, for information extraction. The paper reveals limitations, such as contextual loss, and presents a novel RAG analysis approach for comparing embedding creation methods. The paper analyzes the most suitable approaches for storing embeddings that incorporate both text and visual information.  
**category**: research

**article** : [The Illusion of Thinking: Understanding the Strengths and Limitations of Reasoning Models via the Lens of Problem Complexity](https://arxiv.org/abs/2506.06941 "The Illusion of Thinking: Understanding the Strengths and Limitations of Reasoning Models via the Lens of Problem Complexity")  
**authors** : Parshin Shojaee, Iman Mirzadeh, Keivan Alizadeh, Maxwell Horton and others (Apple)  
**date** : 2025-07-07  
**desc.** : This paper discusses the progress of language models in generating detailed reasoning processes (Chain-of-Thought) prior to producing answers and improved benchmarks performance. However, the paper argues, supported by empirical evidence, that their fundamental capabilities, scaling properties, and limitations remain poorly understood. The paper systematically reveals the limitations related to task complexity and provides directions for future research.  
**category**: research

**article** : [Comment on The Illusion of Thinking: Understanding the Strengths and Limitations of Reasoning Models via the Lens of Problem Complexity](https://arxiv.org/abs/2506.09250 "Comment on The Illusion of Thinking: Understanding the Strengths and Limitations of Reasoning Models via the Lens of Problem Complexity")  
**authors** : A. Lawsen  
**date** : 2025-07-10  
**desc.** : This paper responds to "The Illusion of Thinking: Understanding the Strengths and Limitations of Reasoning Models via the Lens of Problem Complexity." It presents an alternative perspective, aiming to recontextualize the original findings while identifying three potential critical issues in the original paper: (1) Tower of Hanoi experiments risk exceeding model output token limits, (2) limitations of the automated evaluation framework employed, and (3) benchmark constraints. Nevertheless, the paper acknowledges that the original findings underscore the importance of rigorous experimental design when evaluating AI reasoning capabilities.  
**category**: research

**article** : [A Comment On The Illusion of Thinking: Reframing the Reasoning Cliff as an Agentic Gap](https://arxiv.org/abs/2506.18957 "A Comment On The Illusion of Thinking: Reframing the Reasoning Cliff as an Agentic Gap")  
**authors** : Sheraz Khan, Subha Madhavan, Kannan Natarajan (Pfizer, Cambridge)  
**date** : 2025-07-25  
**desc.** : While the paper acknowledges the results provided by "The Illusion of Thinking: Understanding the Strengths and Limitations of Reasoning Models via the Lens of Problem Complexity," it aims to present an alternative perspective. The paper argues that the observed failures in Chain-of-Thought reasoning do not constitute evidence of a fundamental cognitive boundary, but rather represent predictable outcomes of various system-level constraints. The paper concludes that "The Illusion of Thinking" provides a valuable contribution by developing a rigorous benchmark and demonstrating that explicit Chain-of-Thought in models such as DeepSeek-R1 and Claude 3.7 Sonnet-Thinking does not guarantee reliable execution of long plans. However, it contends that the conclusion regarding an intrinsic reasoning frontier is premature.  
**category**: research

**article** : [LLMs' 'simulated reasoning' abilities are a 'brittle mirage', researchers find](https://arstechnica.com/ai/2025/08/researchers-find-llms-are-bad-at-logical-inference-good-at-fluent-nonsense/ "LLMs’ 'simulated reasoning' abilities are a 'brittle mirage', researchers find")  
**authors** : Kyle Orland (arstechnica)  
**date** : 2025-08-11  
**desc.** : Over recent months, LLMs have demonstrated capabilities in pattern matching across both structured and unstructured data. This article examines whether the responses generated by agentic systems can be considered equivalent to the logical reasoning observed in human thought processes. The presented data and cited sources raise questions about such capabilities, including concerns regarding the systems' understanding of their own generated responses. The article includes the following sections: "No One Trained Me for This," "A False Aura of Dependability," and discussions of warned findings related to "chain-of-thought" approaches with supporting references.  
**category**: research

**article** : [Detecting Perspective Shifts in Multi-agent Systems](https://arxiv.org/abs/2512.05013 "Detecting Perspective Shifts in Multi-agent Systems")  
**authors** : Eric Bridgeford, Hayden Helm  
**date** : 2025-12-04  
**desc.** : Let us model a situation where data-scrapers access the internet, databases, or other LLMs and, based on collected data, generate or serve decision proposals. This paper introduces the Temporal Data Kernel Perspective Space (TDKPS) approach, which aims to detect agent behavioral changes in black-box settings. The paper discusses limitations and future research proposals.  
**category**: research

**article** : [Strategic Self-Improvement for Competitive Agents in AI Labour Markets](https://arxiv.org/abs/2512.04988 "Strategic Self-Improvement for Competitive Agents in AI Labour Markets")  
**authors** : Christopher Chiu, Simpson Zhang, Mihaela van der Schaar (University of Cambridge)  
**date** : 2025-12-04  
**desc.** : The paper introduces a novel framework that captures the real-world simulation of economic forces that may shape agentic labor markets in comparison with traditional human labor markets. Although agentic labor markets will differ significantly from their human counterparts, this paper identifies critical economic forces and capabilities required by agentic systems: metacognition, competitive awareness, and long-horizon strategic planning. Despite reported limitations, self-improving agents have demonstrated superior performance compared to other agent types (e.g., CoT, ReAct).  
**category**: research

**article** : [Is Vibe Coding Safe? Benchmarking Vulnerability of Agent-Generated Code in Real-World Tasks](https://arxiv.org/abs/2512.03262 "Is Vibe Coding Safe? Benchmarking Vulnerability of Agent-Generated Code in Real-World Tasks")  
**authors** : Songwen Zhao, Danqing Wang and others  
**date** : 2025-12-02  
**desc.** : In recent months, the developer community has witnessed a rapid increase in the adoption of the "Vibe Coding" programming paradigm. "Vibe coding" practices are widely used, predominantly by beginner developers, despite unresolved concerns regarding associated risks and vulnerabilities. The paper reports that although coding agents may achieve cca. 60% solution success rates, only cca. 10% of these solutions are free from known security issues, with the possibility of introducing undocumented attack vectors remaining a significant concern.  
**category**: research

**article** : [Mathematical Framing for Different Agent Strategies](https://arxiv.org/abs/2512.04469 "Mathematical Framing for Different Agent Strategies")  
**authors** : Philip Stephens, Emmanuel Salawu (Google Cloud AI)  
**date** : 2025-12-05  
**desc.** : The paper introduces a probabilistic framework for comparing diverse AI agent strategies, allowing for a more detailed view of outcomes. The paper discusses the trade-offs of various architectures while highlighting the necessity of mathematical evaluation. The paper establishes that the behavior of any agentic system may be understood as a probabilistic process by framing individual agent behavior as a chain of probabilities. The paper does not question the non-deterministic nature of LLMs themselves, but rather aims to establish a "Degrees of Freedom" agentic concept and considering probability.  
**category**: research
