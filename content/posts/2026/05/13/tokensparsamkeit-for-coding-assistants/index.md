---
title: "Tokensparsamkeit for coding assistants"
date: "2026-05-13T08:24:31+00:00"
lastmod: "2026-09-21T05:56:10+00:00"
description: "You make decisions with data. Most businesses assumed that the most data, the better the decision. Then, several factors put a halt to the hoarding of…"
canonical: "https://blog.frankel.ch/tokensparsamkeit-coding-assistants/"
authors:
  - "nicolas-frankel"
image: "cover_large-1.jpg"
categories:
  - "AI"
related_posts:
  - "ai-assisted-genealogy"
  - "ai-gateways-why-and-how"
frozen: false
---

You make decisions with data. Most businesses assumed that the most data, the better the decision. Then, several factors put a halt to the hoarding of always more data. and its localized counterparts, and the cost of storage. However, before it happened, the Datensparsamkeit approach already existed.
> Datensparsamkeit is a German word that's difficult to translate properly into English. It's an attitude to how we capture and store data, saying that we should only handle data that we really need.
>
> — [Datensparsamkeit](https://martinfowler.com/bliki/Datensparsamkeit.html)

I don't agree with Martin Fowler's claim that it's difficult to translate. The translation of Sparsamkeit is *frugality*. In the context of coding assistants, token frugality is a good thing.
> Today, critical resources aren't CPU, RAM, or storage, but tokens. Tokens are a finite and expensive resource. My opinion is that soon, developers will be measured on their token usage: the better one will be the one using the fewest tokens to achieve similar results.
>
> — [Writing an agent skill](https://blog.frankel.ch/writing-agent-skill/)

Imagine two engineers finishing the same job with the same quality in the same timeframe. If the organization needs to let go of one, it will be the one that costs more. In the era of AI, it means the one who consumes more tokens.

In this post, I want to show a couple of methods to keep the usage of tokens small.

## Compression

One of the first steps toward Tokensparsamkeit is to compress tokens sent to the underlying LLM **while keeping the same data** . But what are tokens? It's a gross oversimplification, but you for the sake of explanation, let's consider a word is a token. Read this [deep dive](https://letsdatascience.com/blog/tokenization-deep-dive-why-it-matters-more-than-you-think) if you want more details.

If we consider tokens are words, we could remove articles and similar words from the payload to decrease the tokens number. "Find the distance between the Earth and the moon" becomes "Find distance between Earth and moon". For all intents and purpose, the data received is the same, with less words.

The trick is to set a proxy between the client and the LLM backend. I'm using `rtk` myself:
> CLI proxy that reduces LLM token consumption by 60-90% on common dev commands. Single Rust binary, zero dependencies
>
> — [rtk project on GitHub](https://github.com/rtk-ai/rtk)

The tool works across file commands, `git`, `gh`, test runners, build/lint commands, `aws`, `docker`, `kubectl`, etc. Note that it's not a magical recipe, as `rtk` itself mentions:
> This only applies to Bash tool calls. Claude Code built-in tools such as Read, Grep, and Glob bypass the hook, so use shell commands or explicit rtk commands when you want RTK filtering there.

## Context optimization

The second step toward Tokensparsamkeit is to avoid stuffing the context with irrelevant data.

Most people who start using coding assistants assume the context only consists of the system prompt and user prompts. There actually is a lot more. Anthropic's [Effective context engineering for AI agents](https://www.anthropic.com/engineering/effective-context-engineering-for-ai-agents) article mentions:

* System prompt
* User prompt
* Message history
* Tool definitions
* Tool results
* MCP servers
* RAG
* Agent memory if applicable

Claude Code introduced the option to compact (or clear?) the context before each interaction. It explicitly asked with each interaction whether to do it. I liked it, but they removed it a week or so later. Perhaps too many people didn't understand what it entailed? In any case, make good use of the `/compact` command that most assistants provide: it will reduce the conversation history to reduce its token usage, while trying to keep the relevant bits.

Also notice that tools and MCP servers use tokens; the more you configure, the more tokens used. Some MCP servers are so easy to set up, it's tempting to stuff your assistant with them. Don't. Or enable them either on a case-by-case basis or at the project level. Why enable the Vaadin MCP on a Rust project?

The same goes for tools, although I don't think many do use them a lot in comparison to MCP servers.

## Local models

Tokens usage is only important for cloud-based billing. We don't care about it if we use a local model. There are several ways to do it, including AI gateways. In the scope of this article, I'll keep it simple.

I want to keep Claude Code as the client, because it's really good. At the same time, I want to use my own hardware with a local model: the cost is upfront, but then I have zero recurring cost but the power.

If you want to just do it, [How to Run Local LLMs with Claude Code](https://unsloth.ai/docs/basics/claude-code) is where I found the solution. Continue reading the section if you want to read about the issues I faced.

I tried initially to run Qwen3 32B via Ollama in Docker. Docker containers cannot access Apple's Metal GPU framework, so the model ran entirely on CPU. It loaded successfully but crashed during inference with a 500 error; CPU-only inference on a 32B model is simply too slow to be usable.

I have been using Ollama a bit as the default, because others did. Then I stumbled upon [Friends Don't Let Friends Use Ollama](https://sleepingrobots.com/dreams/stop-using-ollama). I switched from Ollama to llama-cpp, which enabled low-level configuration.

The biggest hurdle was the context window size. Claude Code sends **lots** of tokens to the backend. On the [OpenTelemetry tracing demo](https://github.com/nfrankel/opentelemetry-tracing/), it's around 35k on each request.

I started with Qwen3 models. The default token size wasn't big enough. When a model received more tokens than its maximum, llama server immediately rejects the request. I tried to increase the limit with the `--ctx-size` option, to no avail. Qwen3 modelsare trained with 32,768 tokens. It's a hard limit baked into the GGUF file metadata. Llama server abides by it.

Llama server is meant to serve multiple requests simultaneously. It turns out that the count of available tokens is shared equally across all possible requests. If the number of max tokens is `T` and the server can handle `x` requests in parallel, each request only has `T/x` tokens available. For this reason, I set the parallelism with `--parallel 1`.

Despite all of the above, it still didn't work.

## Mixture of Experts vs. dense models

I was using a *dense model*, which is what we use regularly. Dense models load all in memory at once. The alternative is to use a Mixture of Experts model.
> In the context of transformer models, a MoE consists of two main elements:
>
> * **Sparse MoE layers** are used instead of dense feed-forward network (FFN) layers. MoE layers have a certain number of "experts" (e.g. 8), where each expert is a neural network. In practice, the experts are FFNs, but they can also be more complex networks or even a MoE itself, leading to hierarchical MoEs!
> * A **gate network or router**, that determines which tokens are sent to which expert. For example, in the image below, the token "More" is sent to the second expert, and the token "Parameters" is sent to the first network. As we'll explore later, we can send a token to more than one expert. How to route a token to an expert is one of the big decisions when working with MoEs - the router is composed of learned parameters and is pretrained at the same time as the rest of the network.
>
> — [What is a Mixture of Experts](https://huggingface.co/blog/moe)

In layman's terms, a segments its weights/parameters into separate specialized submodels called experts. A routing layer activates only the necessary experts depending on the request. Compared to regular dense models, instead of computing across the entire model of size `T`, only a small subset of experts is activated per request. The combined size of activated experts `t` is much smaller than `T`, even though the sum of all experts together is larger than `T`.

The Qwen3.5-35B-A3B model is a MoE that works perfectly on my machine.

## Putting it all together

We still miss a couple of elements to reach the goal.

To better interact with Claude Code, the model should return structured content. That's what the `--jinja` flag is for. For better performance, you should also use [Flash Attention](https://bentoml.com/llm/kernel-optimization/flashattention). It's an optimized algorithm for computing the attention mechanism in Transformer models. It's faster, more memory-efficient, and more scalable than standard attention. Activate it via `--flash-attn on`. The last configuration parameter is to offload as many layers as possible to the GPU with `--n-gpu-layers 99`.

The final server command line is:

```bash
llama-server \
  --model ~/models/Qwen3.5-35B-A3B-Q4_K_M.gguf \
  --n-gpu-layers 99 \
  --ctx-size 65536 \
  --parallel 1 \
  --flash-attn on \
  --jinja \
  --port 8080
```

On the Claude Code side, we need to set several environment variables:

|            Environment variable            |              Meaning               |         Example         |
|--------------------------------------------|------------------------------------|-------------------------|
| `ANTHROPIC_BASE_URL`                       | URL to the `llama-server` instance | `http://127.0.0.1:8080` |
| `ANTHROPIC_API_KEY`                        | Anything                           | `dummy`                 |
| `ANTHROPIC_AUTH_TOKEN`                     | Anything                           | `dummy`                 |
| `CLAUDE_CODE_DISABLE_NONESSENTIAL_TRAFFIC` | Self-explicit                      | `1`                     |

```bash
export ANTHROPIC_BASE_URL=http://127.0.0.1:8080
export ANTHROPIC_API_KEY=dummy
export ANTHROPIC_AUTH_TOKEN=dummy
export CLAUDE_CODE_DISABLE_NONESSENTIAL_TRAFFIC=1
claude
```

At this point, you can use Claude Code, which will query your local model. Here's a sample server output for a query, for information.

```bash
srv params_from_: Chat format: peg-native
slot get_availabl: id 0 | task -1 | selected slot by LCP similarity, sim_best = 0.788 (> 0.100 thold), f_keep = 0.789
slot launch_slot_: id 0 | task -1 | sampler chain: logits -> ?penalties -> ?dry -> ?top-n-sigma -> top-k -> ?typical -> top-p -> min-p -> ?xtc -> temp-ext -> dist
slot launch_slot_: id 0 | task 1464 | processing task, is_child = 0
slot update_slots: id 0 | task 1464 | new prompt, n_ctx_slot = 65536, n_keep = 0, task.n_tokens = 56401
slot update_slots: id 0 | task 1464 | n_past = 44456, slot.prompt.tokens.size() = 56378, seq_id = 0, pos_min = 56377, n_swa = 0
slot update_slots: id 0 | task 1464 | Checking checkpoint with [56141, 56141] against 44456...
slot update_slots: id 0 | task 1464 | Checking checkpoint with [55629, 55629] against 44456...
slot update_slots: id 0 | task 1464 | Checking checkpoint with [49151, 49151] against 44456...
slot update_slots: id 0 | task 1464 | Checking checkpoint with [40959, 40959] against 44456...
slot update_slots: id 0 | task 1464 | restored context checkpoint (pos_min = 40959, pos_max = 40959, n_tokens = 40960, n_past = 40960, size = 62.813 MiB)
slot update_slots: id 0 | task 1464 | erased invalidated context checkpoint (pos_min = 49151, pos_max = 49151, n_tokens = 49152, n_swa = 0, pos_next = 40960, size = 62.813 MiB)
slot update_slots: id 0 | task 1464 | erased invalidated context checkpoint (pos_min = 55629, pos_max = 55629, n_tokens = 55630, n_swa = 0, pos_next = 40960, size = 62.813 MiB)
slot update_slots: id 0 | task 1464 | erased invalidated context checkpoint (pos_min = 56141, pos_max = 56141, n_tokens = 56142, n_swa = 0, pos_next = 40960, size = 62.813 MiB)
slot update_slots: id 0 | task 1464 | n_tokens = 40960, memory_seq_rm [40960, end)
slot update_slots: id 0 | task 1464 | prompt processing progress, n_tokens = 43008, batch.n_tokens = 2048, progress = 0.762540
slot update_slots: id 0 | task 1464 | n_tokens = 43008, memory_seq_rm [43008, end)
slot update_slots: id 0 | task 1464 | prompt processing progress, n_tokens = 45056, batch.n_tokens = 2048, progress = 0.798851
slot update_slots: id 0 | task 1464 | n_tokens = 45056, memory_seq_rm [45056, end)
slot update_slots: id 0 | task 1464 | prompt processing progress, n_tokens = 47104, batch.n_tokens = 2048, progress = 0.835163
slot update_slots: id 0 | task 1464 | n_tokens = 47104, memory_seq_rm [47104, end)
slot update_slots: id 0 | task 1464 | prompt processing progress, n_tokens = 49152, batch.n_tokens = 2048, progress = 0.871474
slot update_slots: id 0 | task 1464 | n_tokens = 49152, memory_seq_rm [49152, end)
slot update_slots: id 0 | task 1464 | 8192 tokens since last checkpoint at 40960, creating new checkpoint during processing at position 51200
slot update_slots: id 0 | task 1464 | prompt processing progress, n_tokens = 51200, batch.n_tokens = 2048, progress = 0.907785
slot update_slots: id 0 | task 1464 | created context checkpoint 6 of 32 (pos_min = 49151, pos_max = 49151, n_tokens = 49152, size = 62.813 MiB)
slot update_slots: id 0 | task 1464 | n_tokens = 51200, memory_seq_rm [51200, end)
slot update_slots: id 0 | task 1464 | prompt processing progress, n_tokens = 53248, batch.n_tokens = 2048, progress = 0.944097
slot update_slots: id 0 | task 1464 | n_tokens = 53248, memory_seq_rm [53248, end)
slot update_slots: id 0 | task 1464 | prompt processing progress, n_tokens = 55296, batch.n_tokens = 2048, progress = 0.980408
slot update_slots: id 0 | task 1464 | n_tokens = 55296, memory_seq_rm [55296, end)
slot update_slots: id 0 | task 1464 | prompt processing progress, n_tokens = 55885, batch.n_tokens = 589, progress = 0.990851
slot update_slots: id 0 | task 1464 | n_tokens = 55885, memory_seq_rm [55885, end)
slot update_slots: id 0 | task 1464 | prompt processing progress, n_tokens = 56397, batch.n_tokens = 512, progress = 0.999929
slot update_slots: id 0 | task 1464 | created context checkpoint 7 of 32 (pos_min = 55884, pos_max = 55884, n_tokens = 55885, size = 62.813 MiB)
slot update_slots: id 0 | task 1464 | n_tokens = 56397, memory_seq_rm [56397, end)
reasoning-budget: activated, budget=2147483647 tokens
slot init_sampler: id 0 | task 1464 | init sampler, took 4.37 ms, tokens: text = 56401, total = 56401
slot update_slots: id 0 | task 1464 | prompt processing done, n_tokens = 56401, batch.n_tokens = 4
slot update_slots: id 0 | task 1464 | created context checkpoint 8 of 32 (pos_min = 56396, pos_max = 56396, n_tokens = 56397, size = 62.813 MiB)
srv log_server_r: done request: POST /v1/messages 127.0.0.1 200
reasoning-budget: deactivated (natural end)
slot print_timing: id 0 | task 1464 |
prompt eval time = 65949.79 ms / 15441 tokens ( 4.27 ms per token, 234.13 tokens per second)
eval time = 3639.91 ms / 87 tokens ( 41.84 ms per token, 23.90 tokens per second)
total time = 69589.71 ms / 15528 tokens
slot release: id 0 | task 1464 | stop processing: n_tokens = 56487, truncated = 0
```

## Discussion

While the underlying model is important, most people undervalue the client. I used both Claude Code and Copilot CLI with the same underlying model, Claude Sonnet 4.6. I found Claude Code superior by far across several sessions.

The move of most vendors toward subscriptions to benefit from recurring revenues make sense for them. For the customer, however, it's another question: once you stop paying, you lose access to the service.

In the context of coding assistants, vendors justify it by cloud usage. Unfortunately, the per-token metering is quite opaque. If the vendor doesn't size their service properly, users get charged more. I don't think that's fair.

Keeping Claude Code while hosting the model local is a great cost-savvy alternative. You only need to pay for the hardware once. Granted, it's slower, but it's a business model I prefer. If you have well-designed working autonomous agents, you can run them during the night anyway.

**To go further:**

* [Datensparsamkeit](https://martinfowler.com/bliki/Datensparsamkeit.html)
* [LLM Token Optimization Strategies: The Complete Guide for 2026](https://www.tokenoptimize.dev/guides/llm-token-optimization-strategies)
* [Tokenization Deep Dive: Why It Matters More Than You Think](https://letsdatascience.com/blog/tokenization-deep-dive-why-it-matters-more-than-you-think)
* [Effective context engineering for AI agents](https://www.anthropic.com/engineering/effective-context-engineering-for-ai-agents)
* [How to Run Local LLMs with Claude Code](https://unsloth.ai/docs/basics/claude-code)
* [Mixture of Experts Explained](https://huggingface.co/blog/moe)
* [Qwen3 Coder Next](https://openrouter.ai/qwen/qwen3-coder-next)

*Originally published at [A Java Geek](https://blog.frankel.ch/tokensparsamkeit-coding-assistants/) on May 10^th^, 2026.*

*[MoE]: Mixture of Experts
*[GDPR]: General Data Protection Regulation
