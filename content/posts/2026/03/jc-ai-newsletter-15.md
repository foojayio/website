---
title: "JC-AI Newsletter #15"
slug: "jc-ai-newsletter-15"
date: "2026-03-20T07:56:01+00:00"
lastmod: "2026-03-20T07:56:03+00:00"
description: "Over the past two weeks, the field of artificial intelligence has continued its remarkable pace of advancement. As AI becomes increasingly woven into the - by Miro Wengner"
authors:
  - "miro-wengner"
image: "https://foojay.io/wp-content/uploads/2025/07/ai-insider.png"
categories:
  - "AI"
  - "DataEngineering"
  - "Developer Tools"
  - "JC-AI Newsletter"
  - "LangChain4j"
  - "Library"
  - "LLM"
  - "Machine Learning"
  - "Opinion"
  - "Research"
  - "Tutorials"
tags:
related_posts:
frozen: false
---

Over the past two weeks, the field of artificial intelligence has continued its remarkable pace of advancement. As AI becomes increasingly woven into the fabric of daily life, shaping how we work, communicate, and make decisions, it is both timely and valuable to step back and understand the broader trajectory of this technology. Whether the developments around us feel promising or challenging, one truth remains clear: AI is not simply leaving. It is here to stay, and understanding its evolution is essential from many perspectives.

**article** : [Anthropic Study: AI Coding Assistance Reduces Developer Skill Mastery by 17%](https://www.infoq.com/news/2026/02/ai-coding-skill-formation/ "Anthropic Study: AI Coding Assistance Reduces Developer Skill Mastery by 17%")  
**authors** : Steef-Jan Wiggers, InfoQ  
**date** : 2026-02-23  
**desc.** : This article provides additional commentary on the research paper recently published by Anthropic. The original article is included below to allow readers to obtain a complete picture of the challenge. Some previous issues of the JC-AI Newsletter contain multiple research studies related to published findings on various groups of individuals.  
**category**: opinion

**article** : [How AI assistance impacts the formation of coding skills](https://www.anthropic.com/research/AI-assistance-coding-skills "How AI assistance impacts the formation of coding skills")  
**authors** : Anthropic  
**date** : 2026-01-29  
**desc.** : Previous editions of this AI Newsletter have covered multiple clinical studies examining the impact of AI-assisted advisory tools. The findings appear consistent with earlier research on individuals who tend to defer to navigation systems rather than their own spatial judgment.  

Anthropic has conducted its own study on this phenomenon. In a randomized controlled trial, researchers investigated two questions: first, how quickly software developers acquired a new skill, specifically, proficiency with a Python library, with and without AI assistance; and second, whether AI use reduced their comprehension of the code they had just written.  

The results showed that AI assistance was associated with a statistically significant decline in knowledge retention. On a quiz covering concepts participants had applied only minutes earlier, those in the AI-assisted group scored 17 percentage points lower than their counterparts who had coded manually, a gap equivalent to nearly two letter grades. While AI assistance modestly accelerated task completion, this effect did not reach statistical significance. At this stage, drawing direct comparisons with clinical findings may prove difficult.  
**category**: research

**article** : [Censored LLMs as a Natural Testbed for Secret Knowledge Elicitation](https://arxiv.org/abs/2603.05494 "Censored LLMs as a Natural Testbed for Secret Knowledge Elicitation")  
**authors** : Helena Casademunt, Bartosz Cywiński, Khoi Tran, Arya Jakkli, Samuel Marks, Neel Nanda (Harvard University, Antropic ...)  
**date** : 2026-03-05  
**desc.** : Large language models (LLMs) sometimes produce false or misleading responses. Two primary approaches address this problem: honesty elicitation (modifying prompts or model weights so that the model responds truthfully) and lie detection, which involves classifying false responses.  

Prior work evaluates such methods on models specifically trained to lie or conceal information, however, these artificial constructions may not accurately reflect naturally occurring dishonesty. This article proposes an alternative approach such as studying open-weight LLMs developed by Chinese developers, which are trained to censor politically sensitive topics. The findings indicate that no single technique fully eliminates false responses.  
**category**: research

**article** : [Probing Materials Knowledge in LLMs: From Latent Embeddings to Reliable Predictions](https://arxiv.org/abs/2603.01834 "Probing Materials Knowledge in LLMs: From Latent Embeddings to Reliable Predictions")  
**authors** : Vineeth Venugopal, Soroush Mahjoubi, Elsa Olivetti (MIT)  
**date** : 2026-03-02  
**desc.** : Large language models are increasingly applied to materials science, yet fundamental questions remain about their reliability and knowledge encoding. This study evaluates 25 LLMs across four materials science tasks, encompassing over 200 base and fine-tuned configurations. The findings reveal that output modality fundamentally determines model behavior. For symbolic tasks, fine-tuning converges to consistent, verifiable answers with reduced response entropy, while for numerical tasks, fine-tuning improves prediction accuracy but models remain inconsistent across repeated inference runs, limiting their reliability as quantitative predictors. Models were tracked over 18 months, with observations revealing a 9--43% performance variation that poses reproducibility challenges for scientific and industrial applications.  
**category**: research

**article** : [Is AI Hiding Its Full Power? With Geoffrey Hinton](https://www.youtube.com/watch?v=l6ZcFa8pybE&amp;t=4616s "Is AI Hiding Its Full Power? With Geoffrey Hinton")  
**authors** : StarTalk, Geoffrey Hinton  
**date** : 2026-02-28  
**desc.** : In this interview, Hinton addresses pressing questions about employment in the age of AI, beginning with the fundamental shift from logic-based, rule-driven programming to a biologically inspired approach. As the field looks toward the future, the conversation turns to weightier concerns , the enormous energy demands of data centers, and whether AI itself might accelerate breakthroughs in solar technology to meet them.  

Hinton introduces the "Volkswagen Effect": the possibility that a model might strategically underperform in order to avoid being shut down. The discussion then ventures into the philosophy of consciousness, asking whether subjective experience is simply a byproduct of complex perception and whether today's chatbots might already possess some form of it. Both the promise and the peril are examined in full.  

As for the singularity? It may not be imminent but that word yet is doing a great deal of heavy lifting.  
**category**: youtube

**article** : [Lifelong Imitation Learning with Multimodal Latent Replay and Incremental Adjustment](https://arxiv.org/abs/2603.10929 "Lifelong Imitation Learning with Multimodal Latent Replay and Incremental Adjustment")  
**authors** : Fanqi Yu, Matteo Tiezzi, Tommaso Apicella, Cigdem Beyan, Vittorio Murino  
**date** : 2026-03-11  
**desc.** : This article introduces a lifelong imitation learning framework designed to enable continual policy refinement across sequential tasks under realistic memory and data constraints. The proposed Multimodal Latent Replay (MLR) method stores joint compact latent representations that jointly encapsulate visual, linguistic, and state-based modalities, including robot orientation and position, alongside their corresponding control commands.  

When evaluated on the LIBERO benchmark, the presented method achieves a 65% reduction in catastrophic forgetting compared to standard approaches across the tested scenarios. The authors note that further research is needed to validate the method's performance in complex, real-world environments.  
**category**: research

**article** : [Colluding LoRA: A Composite Attack on LLM Safety Alignment](https://arxiv.org/abs/2603.12681 "Colluding LoRA: A Composite Attack on LLM Safety Alignment")  
**authors** : Sihao Ding  
**date** : 2026-03-13  
**desc.** : The article presents Colluding LoRA (CoLoRA), an attack where multiple seemingly harmless adapters work in tandem to disable model safety guardrails through linear composition. Unlike traditional trigger-based attacks, CoLoRA's refusal suppression is inherent to the combination of the adapters themselves. Although this discovery poses dual-use risks for decentralized model sharing, the authors argue that disclosing this vulnerability is a necessary step toward securing the broader AI landscape.  
**category**: research

**article** : [When LLM Judge Scores Look Good but Best-of-N Decisions Fail](https://arxiv.org/abs/2603.12520 "When LLM Judge Scores Look Good but Best-of-N Decisions Fail")  
**authors** : Eddie Landesberg  
**date** : 2026-03-12  
**desc.** : Practitioners increasingly rely on reward models(GPT 5.2, Claude Sonnet 4, Gemini etc) as well as LLM-based judges for best-of-n selection, reranking, and model iteration. A common validation approach involves a single global metric, such as correlation, average error, or pairwise win-rate. When such a metric yields a seemingly acceptable result (e.g., r ≈ 0.5), teams often conclude that the judge is reliable enough to optimize against. That assumption can fail.  

This article investigates how aggregate validity metrics may substantially overstate an LLM judge's practical utility for within-prompt optimization. Specifically, a judge may appear adequate according to a single global metric while still producing poor best-of-n selection decisions. The article discusses these limitations in detail, addresses the associated challenges, and outlines directions for future research.  
**category**: research

**article** : [Continual Learning in Large Language Models: Methods, Challenges, and Opportunities](https://arxiv.org/abs/2603.12658 "Continual Learning in Large Language Models: Methods, Challenges, and Opportunities")  
**authors** : Hongyang Chen, Zhongwu Sun, Hongfei Ye, Kunchi Li, Xuemin Lin  
**date** : 2026-03-13  
**desc.** : Continual learning (CL) has emerged as a pivotal paradigm enabling large language models (LLMs) to dynamically adapt to evolving knowledge and sequential tasks while mitigating catastrophic forgetting. This article provides a comprehensive analysis covering key evaluation metrics, including forgetting rates and knowledge transfer efficiency, along with emerging benchmarks for assessing CL performance. Although results appear promising, LLMs' internal knowledge remains largely static, and continual learning continues to require further research. Complementing these findings, the article presents a practical framework for addressing challenges related to the forgetting phenomenon.  
**category**: research

**article** : [Can LLMs Model Incorrect Student Reasoning? A Case Study on Distractor Generation](https://arxiv.org/abs/2603.15547 "Can LLMs Model Incorrect Student Reasoning? A Case Study on Distractor Generation")  
**authors** : Yanick Zengaffinen, Andreas Opedal, Donya Rooein, Kv Aditya Srivatsa, Shashank Sonkar, Mrinmaya Sachan  
**date** : 2026-03-16  
**desc.** : Modeling plausible student misconceptions is critical for AI in education. This article reveals the failure modes in which errors arise primarily from shortcomings in recovering the correct solution and selecting among response candidates, rather than from simulating errors or structuring the process. Consistent with these findings, providing the correct solution in the prompt improves alignment with human-authored distractors by 8%, highlighting the critical role of anchoring to the correct solution when generating plausible incorrect student reasoning. Overall, this article provides a structured and interpretable lens into LLMs' ability to model incorrect student reasoning and produce high-quality distractors. The topic still requires future research.  
**category**: research

**article** :[Agent Commander: Promptware-Powered Command and Control](https://embracethered.com/blog/posts/2026/agent-commander-your-agent-works-for-me-now/ " Agent Commander: Promptware-Powered Command and Control")  
**authors** : wunderwuzzi, EmbraceTheRed  
**date** : 2026-03-16  
**desc.** : The article examines prompt-based command and control (C2), an increasingly relevant threat vector. While users may grow more comfortable trusting AI agents over time, LLM outputs are inherently probabilistic and therefore untrusted, meaning they can potentially instruct an agent to perform harmful or malicious actions. The article outlines several considerations for mitigating and responding to the prompt injection challenge, particularly as the associated attack surface continues to expand.  
**category**: tutorial

**article** : [TRACE: Evaluating Execution Efficiency of LLM-Based Code Translation](https://arxiv.org/abs/2603.16479 "TRACE: Evaluating Execution Efficiency of LLM-Based Code Translation")  
**authors** : Zhihao Gong, Zeyu Sun, Dong Huang, Qingyuan Liang, Jie M. Zhang, Dan Hao  
**date** : 2026-03-17  
**desc.** : This article presents TRACE, a benchmark that explicitly exposes efficiency gaps beyond correctness through progressive stress test generation and efficiency-critical task selection. From an evaluation of 28 models, findings reveal that correctness is a weak predictor of efficiency, inefficiencies are both prevalent and patterned, and inference-time prompt strategies deliver limited and model-dependent gains. The article highlights the open challenge of developing training paradigms that endow LLMs with intrinsic efficiency awareness for code translation.  
**category**: research
