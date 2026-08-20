---
title: "JC-AI Newsletter #7"
date: "2025-10-14T05:35:01+00:00"
lastmod: "2025-10-14T07:49:59+00:00"
description: "Fourteen days have passed, and it is time to present a fresh collection of readings that could influence developments in the field of artificial intelligence."
authors:
  - "miro-wengner"
image: "ai-insider-1.jpg"
categories:
  - "AI"
  - "Design Patterns"
  - "DevOps"
  - "Java"
  - "Java Core"
  - "JC-AI Newsletter"
  - "LLM"
  - "Machine Learning"
  - "Opinion"
  - "Research"
  - "Testing"
  - "Tutorials"
related_posts:
  - "ai-newsletter-1"
  - "jc-ai-newsletter-2"
  - "jc-ai-newsletter-3"
  - "jc-ai-newsletter-4"
frozen: false
---

**Fourteen days have passed, and it is time to present a fresh collection of readings that could influence developments in the field of artificial intelligence.**

Beyond focused tutorials that can enhance your understanding of AI applications, this newsletter concentrates on Hallucination, Java Code Generation, Testing, Agentic System Architecture and LLM benchmarking methodologies designed to ensure models accuracy and competency in handling complex contextual information.

The world influenced by LLM is changing very quickly, let's start...

**article** : [The Missing Layer in AI Infrastructure: Aggregating Agentic Traffic](https://www.infoq.com/articles/ai-infrastructure-aggregating-agentic-traffic/?topicPageSponsorship=c7e33386-ab42-4a29-b4ab-f46324b2a524 "The Missing Layer in AI Infrastructure: Aggregating Agentic Traffic")  
**authors** : Eyal Solomon  
**date** : 2025-08-22  
**desc.** : Agentic AI systems introduce new challenges across the entire system architecture. Beyond what research articles address, several critical issues remain unresolved and may pose serious risks, particularly in system architecture where the adoption of LLMs has triggered a major paradigm shift in system design. A key concern involves outbound API calls made by autonomously acting AI agents (e.g., chaining tools, calling external services). Current infrastructure, including API gateways and service meshes, is primarily designed around inbound traffic or service-to-service communication, rather than managing agent-initiated outbound calls. This creates a significant blind spot in our architectural oversight.  
**category**: architecture

**article** : [Learning to Reason for Hallucination Span Detection](https://arxiv.org/abs/2510.02173 "Learning to Reason for Hallucination Span Detection")  
**authors** : Hsuan Su, Ting-Yao Hu, Hema Swetha Koppula, Kundan Krishna, Hadi Pouransari, Cheng-Yu Hsieh and others  
**date** : 2025-10-02  
**desc.** : This paper addresses the challenges of advancing from simple binary classification (Chain-of-Thought, COT)) to fine-grained span-level hallucination detection. In-domain reasoning is essential for robust hallucination detection. The normalization step in Group Relative Policy Optimization proves crucial, as simple reward rescaling policies cannot effectively mitigate reward hacking in the dataset employed. The paper proposes a reinforcement learning framework with span-level rewards to align large language model (LLM) reasoning with hallucination detection tasks on the RAGTruth benchmark. Paper research has been done during an internship at Apple.  
**category**: research

**article** : [Stream RAG: Instant and Accurate Spoken Dialogue Systems with Streaming Tool Usage](https://arxiv.org/abs/2510.02044 "Stream RAG: Instant and Accurate Spoken Dialogue Systems with Streaming Tool Usage")  
**authors** : Siddhant Arora, Haidar Khan, Kai Sun, Xin Luna Dong, Sajal Choudhary, Seungwhan Moon, Xinyuan Zhang and others  
**date** : 2025-10-02  
**desc.** : End-to-end speech recognition and fluent answering without noticeable pauses present significant challenges for utilizing LLMs in dialogue-based agentic systems. These systems are prone to hallucination effects caused by various factors. While improving input/output accuracy through Retrieval Augmented Generation (RAG) approaches can mitigate hallucination effects and significantly increase accuracy, this comes with various penalties such as increased resource requirements and latencies. The paper proposes a Model-Triggered Stream RAG approach as an alternative to fixed-interval RAG streaming or without RAG. Although the paper does not provide a complete solution to these challenges, it proposes a benchmarking strategy for future research and highlights key achievements. This research was conducted in cooperation with Meta.  
**category**: research

**article** : [Self-Anchor: Large Language Model Reasoning via Step-by-step Attention Alignment](https://arxiv.org/abs/2510.03223 "Self-Anchor: Large Language Model Reasoning via Step-by-step Attention Alignment")  
**authors** : Hongxiang Zhang, Yuan Tian, Tianyi Zhang  
**date** : 2025-10-03  
**desc.** : While prompting strategies show effectiveness in certain tasks, they lack robustness across different benchmarks and model architectures, performing better on larger LLMs and simpler reasoning problems. This paper proposes a Self-Anchor mechanism for structured reasoning with automatic anchoring. Self-Anchor delivers consistent improvements across tasks, model sizes, and architectures, demonstrating strong robustness and effectiveness. The approach leverages inherent structure in reasoning chains to improve attention alignment and enhance reasoning capabilities. However, Self-Anchor primarily addresses attention misalignment without fully resolving deeper issues related to logical validity, semantic understanding, or computational precision.  
**category**: research

**article** : [Abstain and Validate: A Dual-LLM Policy for Reducing Noise in Agentic Program Repair](https://arxiv.org/abs/2510.03217 "Abstain and Validate: A Dual-LLM Policy for Reducing Noise in Agentic Program Repair")  
**authors** : José Cambronero, Michele Tufano, Sherry Shi, Renyao Wei, Grant Uy, Runxiang Cheng and others  
**date** : 2025-10-03  
**desc.** : Agentic Automated Program Repair (APR) is increasingly addressing complex, repository-level bugs in industry settings. However, agent-generated patches still require human review before deployment to ensure they properly resolve the underlying issues. This paper introduces two complementary LLM-based policies for patch assessment. The paper addresses and discusses limitations in automated patch procedures, human supervision requirements, and company-specific bug-fixing approaches. This paper results from cooperation between Google and Meta.  
**category**: research

**article** : [Cache-to-Cache: Direct Semantic Communication Between Large Language Models](https://arxiv.org/abs/2510.03215 "Cache-to-Cache: Direct Semantic Communication Between Large Language Models")  
**authors** : Tianyu Fu, Zihan Min, Hanling Zhang, Jichao Yan, Guohao Dai, Wanli Ouyang, Yu Wang  
**date** : 2025-10-03  
**desc.** : Rather than relying on text-to-text communication between LLM-based systems, which incurs latency penalties, this paper proposes the Cache-To-Cache paradigm for direct inter-system communication. Experimental results demonstrate improved efficiency and performance without requiring additional cache capacity.  
**category**: research

**article** : [FreshBrew: A Benchmark for Evaluating AI Agents on Java Code Migration](https://arxiv.org/abs/2510.04852 "FreshBrew: A Benchmark for Evaluating AI Agents on Java Code Migration")  
**authors** : Victor May, Diganta Misra, Yanqi Luo, Anjali Sridhar, Justine Gehring, Silvio Soares Ribeiro Junior  
**date** : 2025-10-06  
**desc.** : This paper introduces the FreshBrew approach for Java code migration tasks utilizing agentic LLM systems. The migration experiments from JDK8 to JDK17 and JDK21 demonstrate the limitations of current LLM implementations, even when integrated with modern deterministic migration tools such as OpenRewrite. Although the overall migration success rate was approximately 50%, the paper provides a comprehensive discussion of the associated limitations and challenges. The article has been done in cooperation with Max-Planck Institute, Google and Saleforce companies.  
**category**: research

**article** :[Investigating The Smells of LLM Generated Code](https://arxiv.org/abs/2510.03029 " Investigating The Smells of LLM Generated Code")  
**authors** : Debalina Ghosh Paul, Hong Zhu, Ian Bayley  
**date** : 2025-10-03  
**desc.** : The paper proposes a scenario-based method for evaluating the quality of LLM-generated code, as such models are increasingly utilized for program code generation. The study experiments with Java programs using Gemini Pro, ChatGPT, Codex, and Falcon LLMs to obtain results. The paper highlights that for moderately advanced topics, particularly those involving object-oriented programming concepts, the generated code quality is noticeably poorer to human-written code.  
**category**: research

**article** : [Which Programming Language and Model Work Best With LLM-as-a-Judge For Code Retrieval?](https://arxiv.org/abs/2510.00324 "Which Programming Language and Model Work Best With LLM-as-a-Judge For Code Retrieval?")  
**authors** : Lucas Roberts, Denisa Roberts  
**date** : 2025-09-30  
**desc.** : This paper examines the comparative abilities of Large Language Models (LLMs) and human annotators in identifying and annotating specific elements within source code. The study investigates several widely-used programming languages, including C, Java, JavaScript, Go, and Python. The experimental results reveal various limitations and challenges associated with automated code annotation, while proposing possibilities for future research and emphasizing the critical role of human expertise in the annotation process.  
**category**:

**article** : [AI Coding Tools Blog Post - Model Context Protocol Mastery - Claude, Cursor](https://github.com/neomatrix369/awesome-ai-ml-dl/blob/master/blogs/ai-coding-tools/README.md "AI Coding Tools Blog Post - Model Context Protocol Mastery - Claude, Cursor")  
**authors** : Mani Sarkar  
**date** : 2025-10-07  
**desc.** : The post describes and provides guidance on configuring MCP for AI-assisted development using Claude and Cursor. Please be aware of the 'No Warranty' statement and use this as an example only.  
**category**: tutorial

**article** : [DiffTester: Accelerating Unit Test Generation for Diffusion LLMs via Repetitive Pattern](https://arxiv.org/abs/2509.24975 "DiffTester: Accelerating Unit Test Generation for Diffusion LLMs via Repetitive Pattern")  
**authors** : Lekang Yang, Yuetong Liu, Yitong Zhang, Jia Li  
**date** : 2025-09-29  
**desc.** : Writing high-quality unit tests is often a time-consuming effort that requires extensive knowledge of the business domain. This paper proposes DiffTester, an acceleration framework designed to overcome the limitations imposed by single-token generation constraints. The DiffTester framework identifies common patterns through syntax tree analysis. Experimental results demonstrate that the DiffTester framework can used to generate a larger number of tokens, thereby achieving better accuracy.  
**category**: research

**article** : [Deloitte caught out using AI in $440,000 report \| 7.30](https://www.youtube.com/watch?v=oN0nViY4gn4 "Deloitte caught out using AI in $440,000 report | 7.30")  
**authors** : ABC News In-depth  
**date** : 2025-10-09  
**desc.** : Hallucination remains a significant challenge in current large language models (LLMs). These inaccuracies can cause damage at various levels and require careful eye to identify.  
**category**: youtube

**article** : [SusBench: An Online Benchmark for Evaluating Dark Pattern Susceptibility of Computer-Use Agents](https://arxiv.org/abs/2510.11035)  
**authors** : Longjie Guo, Chenjie Yuan, Mingyuan Zhong, Robert Wolfe, Ruican Zhong, Yue Xu, Bingbing Wen and others  
**date** : 2025-10-13  
**desc.** : In the age of AI, deception through manipulation or hallucination-based choices poses a serious threat to human or system reliability. This paper proposes SysBench, a benchmark for evaluating the susceptibility of computer-user agents (CUAs) and humans to dark patterns that may mislead both users and agents into making harmful decisions. The paper demonstrates that neither humans nor agentic AI systems based on large language models (LLMs) exhibit adequate resistance against dark patterns.  
**category**: research

Previous:  
[Newsletter vol.1](https://foojay.io/today/ai-newsletter-1/ "Newsletter vol.1")  
[Newsletter vol.2](https://foojay.io/today/jc-ai-newsletter-2/ "Newsletter vol.2")  
[Newsletter vol.3](https://foojay.io/today/jc-ai-newsletter-3/ "Newsletter vol.3")  
[Newsletter vol.4](https://foojay.io/today/jc-ai-newsletter-4/ "Newsletter vol.4")  
[Newsletter vol.5](https://foojay.io/today/jc-ai-newsletter-5/ "Newsletter vol.5")  
[Newsletter vol.6](https://foojay.io/today/jc-ai-newsletter-6/ "Newsletter vol.6")
