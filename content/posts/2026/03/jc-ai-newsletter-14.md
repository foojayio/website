---
title: "JC-AI Newsletter #14"
slug: "jc-ai-newsletter-14"
date: "2026-03-03T15:11:53+00:00"
lastmod: "2026-03-03T15:11:55+00:00"
description: "Two weeks have passed and a lot have been happening on the field of artificial-intelligence. Two weeks have passed and a lot has been silently yet visibly - by Miro Wengner"
authors:
  - "miro-wengner"
image: "https://foojay.io/wp-content/uploads/2025/07/ai-insider.png"
categories:
  - "AI"
  - "Cloud"
  - "Data Engineering"
  - "DevOps"
  - "Interviews"
  - "Java"
  - "JC-AI Newsletter"
  - "LLM"
  - "Machine Learning"
  - "Observability"
  - "Opinion"
  - "Performance"
  - "Research"
  - "Tutorials"
tags:
related_posts:
frozen: false
---

**Two** weeks have passed and a lot have been happening on the field of artificial-intelligence.  

Two weeks have passed and a lot has been silently yet visibly happening in the field of artificial intelligence. This newsletter brings interesting developments, including Dario Amodei's (Anthropic) view on the progress achieved in the LLM field and his response to the utilization of these models for specific kinds of military purposes, as well as OpenAI's response to it. Aside from the fact that development may follow more sigmoids instead of exponential progress, it is important to have awareness of utilization across branches. Does prompting and clarifying the goal influence agent responses, and if so, how? How far are we from reliable robotics applications? How much bias is introduced when clinical data is being analyzed?  

Let's jump in and happy reading!

**article** : [Exclusive: Why are Chinese AI models dominating open-source as Western labs step back?](https://www.artificialintelligence-news.com/news/chinese-ai-models-175k-unprotected-systems-western-retreat/ "Exclusive: Why are Chinese AI models dominating open-source as Western labs step back?")  
**authors** : Dashveenjit Kaur, AI News  
**date** : 2026-02-09  
**desc.** : A shift in what AI models are being used and where the models are being produced.  
**category**: opinion

**article** : [Machines of Loving Grace](https://darioamodei.com/essay/machines-of-loving-grace "Machines of Loving Grace")  
**authors** : Dario Amodei  
**date** : 2024-10-01  
**desc.** : Although the article is older, it remains relevant for any author aiming to sketch a future in which everything with AI goes right. In light of recent developments, which appear to follow a sigmoid curve rather than exponential growth (marked by stagnation, with current models reaching a point where another breakthrough is required), the trajectory looks more measured than initially anticipated. Although the author discusses multiple risks (grandiosity, market forces, propaganda, sci-fi-like expectations, etc.), he also highlights the bright sides and explores areas where current AI may prove genuinely helpful. The question remains whether the current state of affairs can truly guarantee progress, rather than causing damage through non-deterministic outcomes (education, industry, human creativity etc.).  
**category**: opinion

**article** : [The Urgency of Interpretability](https://darioamodei.com/post/the-urgency-of-interpretability "The Urgency of Interpretability")  
**authors** : Dario Amodai  
**date** : 2025-04-01  
**desc.** : The author describes lessons learned from current AI development and adds multiple valuable thoughts and facts to consider when interacting with AI models. The main point is that progress in the underlying technology is inexorable, driven by forces too powerful to stop, but what matters is the way in which it unfolds. Accepting that the current evolution of LLM-based AI cannot be halted, the author expresses hope that it may still be guided (this fact affect not only entire industry but also human kind thoughs and perception of reality), much like a bus controlled by a steering wheel, and warns of the dangers of ignorance, illustrating this through several concrete examples.  
**category**: opinion

**article** : [From Delegates to Trustees: How Optimizing for Long-Term Interests Shapes Bias and Alignment in LLM](https://arxiv.org/abs/2510.12689 "From Delegates to Trustees: How Optimizing for Long-Term Interests Shapes Bias and Alignment in LLM")  
**authors** : Suyash Fulay, Jocelyn Zhu, Michiel Bakker (MIT)  
**date** : 2025-10-14  
**desc.** : The article addresses the question of 'behavioral cloning', specifically, how accurately LLMs reproduce individuals' expressed preferences. Large language models have demonstrated promising accuracy in predicting survey responses and policy preferences, which has fueled growing interest in their potential to represent human interests across various domains. Drawing on theories of political representation, the article highlights an underexplored design trade-off: whether AI systems should act as delegates, mirroring expressed preferences, or as trustees, acting in users' broader interests. Models may align well with users' short-term preferences while failing to account for their long-term interests. Studies further indicate greater bias in topics where consensus is lacking.  
**category**: research

**article** : [DARE-bench: Evaluating Modeling and Instruction Fidelity of LLMs in Data Science](https://arxiv.org/abs/2602.24288 "DARE-bench: Evaluating Modeling and Instruction Fidelity of LLMs in Data Science")  
**authors** : Fan Shu, Yite Wang, Ruofan Wu, Boyi Liu, Zhewei Yao, Yuxiong He, Feng Yan  
**date** : 2026-02-27  
**desc.** : The article addresses the challenge posed by fast-growing demand for Large Language Models (LLMs) to tackle complex, multi-step data science tasks, which has created an urgent need for accurate benchmarking. Two major gaps are identified in existing benchmarks: (i) the lack of standardized, process-aware evaluation that captures instruction adherence and process fidelity, and (ii) the scarcity of accurately labeled training data. While highlighting that even capable models (Anthropic, OpenAI, etc.) may struggle in performance, the article introduces the DARE-bench benchmark alongside supervised fine-tuning as approaches that may improve outcomes in specific applications. Although the results appear promising, they retain considerable potential for further improvement, as accuracy is not yet guaranteed.  
**category**: research

**article** : [Do LLMs Benefit From Their Own Words?](https://arxiv.org/abs/2602.24287 "Do LLMs Benefit From Their Own Words?")  
**authors** : Jenny Y. Huang, Leshem Choshen, Ramon Astudillo, Tamara Broderick, Jacob Andreas (MIT, IBM Research)  
**date** : 2026-02-27  
**desc.** : The article aims to answer the question of whether preserving past assistant responses is more beneficial than harmful. The study uses in-the-wild, multi-turn conversations and compares standard (full-context) prompting with a user-turn-only prompting approach that omits all previous assistant responses, evaluated across three open reasoning models and one state-of-the-art model. Surprisingly, omitting past assistant responses does not negatively affect response quality in a large fraction of turns and may also reduce token length. The article concludes with a discussion of findings and directions for future research.  
**category**: research

**article** : [SafeGen-LLM: Enhancing Safety Generalization in Task Planning for Robotic Systems](https://arxiv.org/abs/2602.24235 "SafeGen-LLM: Enhancing Safety Generalization in Task Planning for Robotic Systems")  
**authors** : Jialiang Fan, Weizhe Xu, Mengyu Liu, Oleg Sokolsky, Insup Lee, Fangxin Kong  
**date** : 2026-02-27  
**desc.** : Safety-critical task planning in robotic systems remains a significant challenge: classical planners suffer from poor scalability, reinforcement learning (RL)-based methods generalize poorly, and base large language models (LLMs) cannot guarantee safety. To address this gap, the article proposes SafeGen-LLM, a safety-generalizable large language model framework. As part of this contribution, a multi-domain Planning Domain Definition Language 3 (PDDL3) benchmark with explicit safety constraints is introduced, along with Supervised Fine-Tuning (SFT) on those constraints. Although the results appear optimistic, with minimal safety violations observed across tested domains, the approach still requires further research in more complex robotic settings.  
**category**: research

**article** : [LemmaBench: A Live, Research-Level Benchmark to Evaluate LLM Capabilities in Mathematics](https://arxiv.org/abs/2602.24173 "LemmaBench: A Live, Research-Level Benchmark to Evaluate LLM Capabilities in Mathematics")  
**authors** : Antoine Peyronnet, Fabian Gloeckle, Amaury Hayat  
**date** : 2026-02-27  
**desc.** : Existing benchmarks largely rely on static, hand-curated sets of contest or textbook-style problems as proxies for mathematical research. The article introduces a novel approach leveraging state-of-the-art models (GPT-5, Gemini 2.5, Gemini 3, Claude Opus 4.5, and DeepSeek-R) by extracting lemmas from arXiv and updating them dynamically. This results in a benchmark that can be refreshed regularly with new problems drawn directly from current mathematical research, while previous instances can be used for training without compromising future evaluations. This approach achieves 10--15% accuracy in theorem proving and opens a new frontier for future research. Although the process may appear fully automated, a human in the loop, such as the article's author or reviewer, remains critically necessary to produce high-quality inputs and to effectively use LLM models.The results also indicate that it is considerably easier for a model to validate an existing proof than to produce one.  
**category**: research

**article** : [Task Complexity Matters: An Empirical Study of Reasoning in LLMs for Sentiment Analysis](https://arxiv.org/abs/2602.24060 "Task Complexity Matters: An Empirical Study of Reasoning in LLMs for Sentiment Analysis")  
**authors** : Donghao Huang, Zhaoxia Wang  
**date** : 2026-02-27  
**desc.** : It is a well-established narrative that reasoning in large language models (LLMs) universally improves performance across language tasks. This article aims to test that claim through a comprehensive evaluation of 504 configurations across seven models, considering different reasoning architectures such as adaptive, conditional, and reinforcement-based approaches. The findings reveal that the effectiveness of reasoning is strongly task-dependent and degrades for simpler tasks. The article provides quantitative findings alongside error analysis and outlines directions for future research.  
**category**: research

**article** : [Benchmarking LLM Summaries of Multimodal Clinical Time Series for Remote Monitoring](https://arxiv.org/abs/2603.01557 "Benchmarking LLM Summaries of Multimodal Clinical Time Series for Remote Monitoring")  
**authors** : Aditya Shukla, Yining Yuan, Ben Tamo, Yifei Wang, Micky Nnamdi and others  
**date** : 2026-03-02  
**desc.** : Large language models (LLMs) can generate fluent clinical summaries of remote therapeutic monitoring time series, however, the impact of information bias on clinically significant events, such as sustained abnormalities, remains poorly understood. The article presents the Technology-Integrated Health Management (TIHM) framework to address these questions, introducing a protocol that measures abnormality recall, duration recall, and measurement coverage, while utilizing GPT-4o-mini as a proxy evaluator. Traditional models frequently exhibit near-zero abnormality recall, whereas the vision-based approach achieves the strongest event alignment, with 45.7% abnormality recall and 100% duration recall. These results underscore the need for event-aware evaluation methods in future research to ensure reliable clinical time-series summarization.  
**category**: research

**article** : [Full interview: Anthropic CEO responds to Trump order, Pentagon clash](https://youtu.be/MPTNHrq_4LU?si=2MhGxkeJAmlZU_Lt "Full interview: Anthropic CEO responds to Trump order, Pentagon clash")  
**authors** : CBS News  
**date** : 2026-02-28  
**desc.** : Anthropic CEO Dario Amodei sat down with CBS News for an exclusive interview, hours after Defense Secretary Pete Hegseth declared the company a supply chain risk to national security, which restricts military contractors from doing business with the AI giant. Amodei called the move "retaliatory and punitive," and he said Anthropic sought to draw "red lines" in the government's use of its technology because "we believe that crossing those lines is contrary to American values, and we wanted to stand up for American values.". [Response of the OpenAI striking a deal with Pentagon](https://www.youtube.com/watch?v=cvv9eFuBMtI "Response of the OpenAI striking a deal with Pentagon") causes many questions.  
**category**: youtube

**article** : [Scary Agent Skills: Hidden Unicode Instructions in Skills ...And How To Catch Them](https://embracethered.com/blog/posts/2026/scary-agent-skills/ "Scary Agent Skills: Hidden Unicode Instructions in Skills ...And How To Catch Them")  
**authors** : Embrace The Red  
**date** : 2026-02-11  
**desc.** : Skills introduce common threats such as prompt injection, supply chain attacks, remote code execution (RCE), and data exfiltration, among others. This post discusses the fundamentals, highlights the most straightforward prompt injection vector, and demonstrates how a real Skill from OpenAI can be back-doored using invisible Unicode Tag code-points, a technique that certain models, including Gemini, Claude, and Grok, are known to interpret as instructions. From a security perspective, Skills present serious concerns, as they represent a typical supply chain risk with limited governance or security controls. The author identified that some Skills instruct the AI to embed API tokens directly in curl requests and similar constructs , a poor design practice. This means that credentials are passed through the LLM, making them susceptible to leakage and leaving them vulnerable to being overwritten by an attacker via indirect prompt injection.  
**category**: tutorial
