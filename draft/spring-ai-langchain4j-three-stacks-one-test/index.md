---
title: "Spring AI or LangChain4j? One App, Three Stacks, One Test"
date: "2026-01-01"
description: "The same small AI service in Spring AI, LangChain4j on Spring Boot and on Quarkus, held to one shared test. Where they differ, and where they do not."
authors:
  - "nicolas-duminil"
image: "three-stacks.jpg"
categories:
  - "AI"
  - "LangChain4j"
  - "Spring"
  - "Java"
related_posts: []
---

Java has quietly become a credible place to build AI applications. That raises a
practical question for anyone starting out: which framework do you reach for,
[Spring AI](https://docs.spring.io/spring-ai/reference/) or
[LangChain4j](https://docs.langchain4j.dev/), and does it matter whether you run
it on Spring Boot or on Quarkus?

The consistent way to answer is not a feature table. It is to build the same thing on
each stack and read the difference. This article does exactly that for the
smallest useful AI application: one HTTP endpoint that forwards a question to a
model and returns its answer. We build it three times, in Spring AI, in
LangChain4j on Spring Boot, and in LangChain4j on Quarkus, and then hold all three
to a single shared test.

## One interface, three implementations

Everything starts from one framework-neutral contract, a plain Java interface
that knows nothing about any AI library:

```java
public interface QuestionAnsweringService {
  Answer askQuestion(Question question);
}
```

`Question` and `Answer` are simple records. Each stack provides only its own
implementation of this interface, and nothing framework-specific leaks into the
shared code. So the only thing that varies between the three is how each one
talks to the model.

## Spring AI: a fluent ChatClient

```java
@Service
public class SpringAiQuestionAnsweringService implements QuestionAnsweringService {

  private final ChatClient chatClient;

  public SpringAiQuestionAnsweringService(ChatClient.Builder chatClientBuilder) {
    this.chatClient = chatClientBuilder.build();
  }

  @Override
  public Answer askQuestion(Question question) {
    var answerText = chatClient.prompt()
      .user(question.question())
      .call()
      .content();
    return new Answer(answerText);
  }
}
```

Spring AI hands you a `ChatClient.Builder`, auto-configured from your properties,
and a fluent API: `prompt()`, `user()`, `call()`, `content()`. The model, its
base URL and its options are all configuration, not code.

## LangChain4j: a plain ChatModel, and the same code on two runtimes

On Spring Boot:

```java
@Service
public class LangChain4jQuestionAnsweringService implements QuestionAnsweringService {

  private final ChatModel chatModel;

  public LangChain4jQuestionAnsweringService(ChatModel chatModel) {
    this.chatModel = chatModel;
  }

  @Override
  public Answer askQuestion(Question question) {
    return new Answer(chatModel.chat(question.question()));
  }
}
```

LangChain4j hands you a `ChatModel` and a single method, `chat()`. There is no
builder to call: the model is injected ready to use.

Now the Quarkus version:

```java
@ApplicationScoped
public class LangChain4jQuestionAnsweringService implements QuestionAnsweringService {

  private final ChatModel chatModel;

  public LangChain4jQuestionAnsweringService(ChatModel chatModel) {
    this.chatModel = chatModel;
  }

  @Override
  public Answer askQuestion(Question question) {
    return new Answer(chatModel.chat(question.question()));
  }
}
```

Look closely. The body is identical to the Spring Boot version. The only
difference is the annotation: `@ApplicationScoped` (CDI) instead of `@Service`
(Spring). Same library, same `ChatModel`, same `chat()` call. The runtime
changes, the code does not. That is precisely why it is worth running LangChain4j
on both: it isolates what the framework costs from what the runtime costs.

## One test to compare them all

Three implementations are only comparable if they are held to the same standard.
Each stack's integration test extends one abstract test that talks to the running
application over HTTP:

```java
public abstract class AbstractQuestionAnsweringIT {

  protected abstract URI baseUri();

  @Test
  @DisplayName("answers a factual question")
  protected void answersAFactualQuestion() {
    ask()
      .body(new Question("What is the capital of France? Answer with the city name only."))
      .when()
      .post("/ask")
      .then()
      .statusCode(200)
      .body("answer", not(blankOrNullString()))
      .body("answer", containsStringIgnoringCase("paris"));
  }

  // ... plus: rejects a blank question (400), rejects a malformed payload (400)

  private RequestSpecification ask() {
    return given().baseUri(baseUri().toString()).contentType(ContentType.JSON);
  }
}
```

Two things matter here. First, the suite talks HTTP with REST Assured, never an
in-process API, so the same test class can hold a Spring Boot application and a
Quarkus application to the same standard: a subclass supplies only its base URI.
Second, the assertion is semantic, not exact-string.
It checks that the answer contains "paris", case-insensitively, because LLM output
is non-deterministic even at temperature zero. Asserting on a literal completion
would make the test lie.

The tests run against a local model in an Ollama container, started with
[Testcontainers](https://testcontainers.com/), so there is no API key, no
per-run cost, and the whole comparison is reproducible in CI.

## What the comparison reveals

The three stacks are not the same size and do not start
at the same speed. Counting only non-blank, non-comment lines of production Java:

| | Spring AI | LangChain4j / Boot | LangChain4j / Quarkus |
|---|:---:|:---:|:---:|
| Production Java (LOC) | 48 | 47 | 39 |
| Files in `src/main/java` | 3 | 3 | 2 |
| Resolved runtime jars | 106 | 49 | 142 |
| Startup, JVM mode | 1.74 s | 2.69 s | 0.66 s |

The startup figures are medians of a handful of runs on a
single machine, with the Ollama container already up so that only application boot
is inside the window: they are indicative, not benchmarks. And the jar counts do
not mean Quarkus is three times heavier than LangChain4j on Boot; it simply
splits its runtime into many small artifacts. A fair comparison shows that
Spring AI brings 106 jars while LangChain4j brings only 49 on the *same* runtime.
That's because Spring AI
brings a JSON-schema generator, a tokenizer and Reactor along with it, none of
which this use case touches.

Even hedged this way, the shape is clear: Quarkus produces the smallest module and
starts fastest, Spring AI is compact in code but the heaviest in dependencies, and
LangChain4j on Boot sits close to Spring AI, the same three files with a few extra
jars for its starter.

Qualitatively, the differences are small but relevant:

- Spring AI leads with a fluent, builder-based `ChatClient`, and configuration
  drives the model.
- LangChain4j leads with a single `ChatModel.chat()` call, and the injected model
  is the whole surface.
- Between Spring Boot and Quarkus, the LangChain4j code is identical except for
  the dependency-injection annotation, which tells you the runtime is the
  variable, not the library.

None of this is a verdict. The interesting output of a comparison like this is
not a winner, it is the diff, and the diff is what tells you which framework fits
the way your team already thinks.

## Where this goes next

This is the smallest use case. The same method, build once per stack and grade
with one shared test, carries all the way up: retrieval-augmented generation,
conversational memory, tool calling, the Model Context Protocol, observability,
security, and agents. Each one adds a capability and shows where the three stacks
converge and where they diverge.

If you want the full treatment, it is the subject of my book, *Java AI
Frameworks*, available on [Leanpub](https://leanpub.com/javaaiframeworks) with a
free sample of the first two chapters.
