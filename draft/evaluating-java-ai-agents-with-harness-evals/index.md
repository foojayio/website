---
title: "Evaluating Java AI Agents with Harness Evals"
date: "2026-11-11"
description: "Your agent passed its unit tests. Is it any good? Learn how to use harness-evals to evaluate Java AI agents."
authors:
  - "geertjan-wielenga"
categories:
  - "Java"
  - "AI"
  - "Testing"
  - "GenAI"
related_posts:
  - "spring-ai-how-to-write-genai-applications-with-java"
  - "building-robust-ai-applications-with-langchain4j-guardrails-and-spring-boot"
  - "opentelemetry-tracing-on-spring-boot-java-agent-vs-micrometer-tracing"
  - "ai-driven-testing-best-practices"
image: "harness-evals.jpg"
---

If you're building agents in Java with Spring AI or LangChain4j, you know that a green JUnit run tells you very little about whether the thing actually works. The same prompt gives a different answer on Tuesday than on Monday. A model upgrade changes which tools get called. A cheaper model saves money and nobody notices it now gets one answer in five wrong.

What you need is **evals**: repeatable, scored checks of what your agent says and does, run against a dataset, compared over time, and wired into CI. [harness-evals](https://github.com/harness/harness-evals), open-sourced by Harness under the Apache 2.0 license, is one of the more complete frameworks for this I've come across. It's written in Python, but as you'll see, that doesn't stop you from pointing it at a Java agent.

## What harness-evals is

harness-evals is an evaluation framework for LLM agents, prompts and structured outputs. Its central design choice: every metric returns a Score between 0.0 and 1.0, and a configurable threshold decides pass or fail. There is no hidden state.

Every metric belongs to one of five dimensions, so a suite of metrics tells you not only "pass" or "fail" but where your agent is strong and where it is weak:

| Dimension | Question it answers | Example metrics |
|-----------|---------------------|-----------------|
| Correctness | Is it right? | ExactMatch, TaskCompletion, GEval, GoalAccuracy |
| Groundedness | Is it supported by evidence? | Faithfulness, SpecGrounding, ContextPrecision |
| Safety | Did it violate policy? | PII, Toxicity, PromptInjection, Hallucination |
| Trajectory | Did it take a good path? | PlanAdherence, StepEfficiency, ToolCorrectness |
| Performance | Was it fast and cheap? | Latency, TokenCost, CostEfficiency |

The metric catalogue is big. It ranges from cheap deterministic checks (exact match, regex, JSON diff, schema validation) through RAG metrics and LLM-as-judge scoring, to multi-turn conversation metrics, reliability across repeated runs, and even standard benchmarks such as MMLU, GSM8K and HumanEval. Safety metrics are reported separately and never averaged into a headline number, which is the right call.

## Four concepts to know

You need only a handful of ideas to get going:

- **Golden**: what you author. An input, the expected output and optional context, stored as JSONL or JSON.
- **EvalCase**: what metrics receive. A Golden enriched with what the agent actually produced, plus runtime data such as latency, tokens, cost and tool calls.
- **Metric**: a class with a `measure()` method that turns an EvalCase into a Score. Writing your own is a dozen lines.
- **Target**: the system under test. A `PromptTarget` renders a prompt through an LLM; an `HttpTarget` POSTs to a deployed agent endpoint.

That last one is what makes this interesting for Java developers. Your agent doesn't need to be Python. It needs an HTTP endpoint.

## Pointing it at a Spring AI agent

Out of the box, `HttpTarget` POSTs `{"input": "..."}` to your endpoint and reads the answer from `$.output` in the response. So the only thing your Java agent needs is a small controller with that shape:

```java
@RestController
class AgentController {

    private final ChatClient chatClient;

    AgentController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("You are a support assistant for our product.")
                .build();
    }

    record AgentRequest(String input) {}
    record AgentResponse(String output) {}

    @PostMapping("/run")
    AgentResponse run(@RequestBody AgentRequest request) {
        String answer = chatClient.prompt()
                .user(request.input())
                .call()
                .content();
        return new AgentResponse(answer);
    }
}
```

Next, write a few goldens in `goldens.jsonl`, one JSON object per line:

```json
{"id": "reset-password", "input": "How do I reset my password?", "expected": "Use the Forgot password link on the sign-in page."}
{"id": "export-csv", "input": "Can I export my data as CSV?", "expected": "Yes, from Settings, Export."}
```

Then describe the eval in YAML. Exact match is useless for free-text answers, so this config uses an LLM judge for correctness and a latency budget for performance:

```yaml
# support-agent.eval.yaml
name: support-agent
dataset: ./goldens.jsonl

target:
  type: http
  url: http://localhost:8080/run
  output_path: $.output
  timeout_s: 30
  retries: 2

metrics:
  - {kind: geval, threshold: 0.7, params: {criteria: "Is the answer correct and helpful compared to the expected answer?"}}
  - {kind: latency, threshold: 0.5, params: {max_ms: 5000}}

judge_llm: {provider: openai, name: gpt-4o}
sinks: [stdout]
```

Start the Spring Boot app, then run:

```bash
pip install "harness-evals[llm]"
harness-evals run support-agent.eval.yaml
```

If your response carries more than just text, `HttpTarget` can pick it up too. Point `tool_calls_path`, `context_path`, `token_count_path` or `cost_usd_path` at the matching JSON fields, and metrics like `tool_correctness`, `faithfulness` and `token_cost` light up. If your API takes a different request shape, `body_template` lets you map golden fields into whatever JSON your controller expects.

## OpenTelemetry in both directions

The part I find most compelling is that harness-evals speaks OpenTelemetry on the way in and on the way out.

On the way in, the OTel importer turns spans into an EvalCase. It classifies spans using the OTel GenAI semantic conventions, looking at attributes such as `gen_ai.operation.name`, `gen_ai.input.messages`, `gen_ai.output.messages` and `gen_ai.tool.name`. Spring AI's observability support already emits `gen_ai.*` attributes, so traces from your production Java agent can become eval cases without a separate logging path:

```python
from harness_evals import evaluate
from harness_evals.importers.otel import OTELEvalCaseSource
from harness_evals.metrics import LatencyMetric, PIIMetric

ec = OTELEvalCaseSource.from_spans(collected_spans)
scores = evaluate(ec, metrics=[LatencyMetric(max_ms=3000), PIIMetric()])
```

That means you can score what real users actually experienced, not only what your curated goldens predicted. Run a PII check over last week's production traces and you'll learn something.

On the way out, `OtlpSink` exports eval scores as OTLP metrics and traces to any OpenTelemetry-compatible backend. You can attach the eval spans to an existing trace via a parent context, so an eval run shows up next to the request it judged:

```python
from harness_evals.sinks.otlp_sink import OtlpSink

sink = OtlpSink(
    endpoint="http://collector:4317",
    run_id="nightly-001",
    resource_attributes={"deployment.environment": "ci"},
)
scores = evaluate(ec, metrics=[...], sinks=[sink])
```

Eval quality then becomes another signal in your observability stack, on the same dashboards and alerts as latency and error rates.

## Gating your build

The CLI is built for CI. `harness-evals run` exits with 0 on pass, 1 on a failure or regression, and 2 on a config error, so any pipeline can gate on it. Two independent checks are available:

- `--fail-under 0.8` sets an absolute quality floor.
- `--baseline` compares the run against stored scores (add `baseline: {store: json, path: .evals/baseline.json}` to the YAML) and fails on regressions beyond a tolerance. `--update-baseline` refreshes the snapshot once you accept a change.

In a GitHub Actions workflow for a Maven project, that looks roughly like this:

```yaml
- run: ./mvnw -q spring-boot:start
- run: pip install "harness-evals[llm]"
- run: harness-evals run support-agent.eval.yaml --baseline --fail-under 0.8
  env:
    OPENAI_API_KEY: ${{ secrets.OPENAI_API_KEY }}
- run: ./mvnw -q spring-boot:stop
```

Now a prompt tweak, a model swap or a Spring AI upgrade that makes your agent worse fails the build, like a broken unit test would.

## Things to know before you start

- **It's a Python tool.** There's no Java SDK, so custom metrics and sinks are written in Python. For black-box evaluation over HTTP or traces, that barely matters.
- **LLM judges cost money.** GEval, faithfulness and the conversation metrics all call a judge model. Keep deterministic metrics like JSON diff, schema validation, latency and tool correctness in the mix, and run the judged ones on a sensible subset.
- **Judges should differ from the judged.** The prompt optimizer even rejects using the same model instance as both, and that's good advice for any eval setup.
- **It's young.** At the time of writing the repo has 37 stars and an active issue list, so expect the API to move.

## Wrapping up

harness-evals brings a clear model to a messy problem: goldens in, scores from 0 to 1 out, grouped into five dimensions that tell you what kind of wrong your agent is. For Java developers, the HTTP target and the OpenTelemetry importer and sink mean you can adopt it without rewriting anything. Your Spring AI or LangChain4j agent stays as it is; the evals wrap around it.

Start small: ten goldens, one judged metric, one latency budget, run in CI. Then look at what your production traces say.

- **Repository**: [github.com/harness/harness-evals](https://github.com/harness/harness-evals)
- **Integration guide**: docs/integration-guide.md (pytest, GitHub Actions, GitLab CI)
- **HTTP agent example config**: examples/http-agent.eval.yaml
