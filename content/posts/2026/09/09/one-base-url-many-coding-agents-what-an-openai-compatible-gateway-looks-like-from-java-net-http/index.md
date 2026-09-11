---
title: "One Base URL, Many Coding Agents: What an OpenAI-Compatible Gateway Looks Like From java.net.http"
date: "2026-09-09T16:09:53+00:00"
lastmod: "2026-09-10T15:07:25+00:00"
description: "Somewhere in every coding agent there is an HTTP client. Claude Code, Codex, Cursor, Cline, OpenCode: strip away the terminal UI or the editor pane and…"
authors:
  - "viktoria-evdokimova"
image: "generated-image-2026-09-09T181859.166-e1788970136304.jpg"
categories:
  - "AI"
  - "Debugging"
  - "IntelliJ IDEA"
  - "Java"
  - "Java Beginner"
  - "Java Core"
  - "JavaFX"
  - "JavaPro"
  - "Kotlin"
  - "LLM"
  - "Machine Learning"
  - "Release Notes"
related_posts:
frozen: false
---

Somewhere in every coding agent there is an HTTP client. Claude Code, Codex, Cursor, Cline, OpenCode: strip away the terminal UI or the editor pane and each of them serializes a conversation into JSON, POSTs it to a base URL with an API key in a header, and streams the answer back. The client owns the prompt assembly, the tool loop and the rendering; the model sits on the far side of that socket.

That framing is worth holding on to, because "OpenAI-compatible" is a phrase that gets thrown around loosely. For a Java developer it has a precise meaning: the server accepts the request shape the OpenAI SDKs emit, under paths the SDKs expect, with `Authorization: Bearer `. If a vendor lets you swap the base URL, you can point the client at any server that speaks that shape.

In its latest release the product I work on, an AI agent for JetBrains IDEs, started exposing such a server to its subscribers. I am less interested here in the announcement than in what it teaches about the architecture of these tools, and in two JVM-flavoured lessons buried in the same release.

## What a gateway is, mechanically

Seen from the client, a gateway of this kind is one URL and one key in front of several model providers. The URL is shared by every user; the key identifies your subscription. Behind the URL sit OpenAI and Anthropic models plus an `auto` alias that picks one for you.

How the clients are configured differs, and the differences are instructive.

Claude Code reads environment variables from `~/.claude/settings.json`:

```json
{
  "env": {
    "ANTHROPIC_BASE_URL": "https://api.explyt.ai/openai/api",
    "ANTHROPIC_API_KEY": "<YOUR_API_KEY>",
    "CLAUDE_CODE_ENABLE_GATEWAY_MODEL_DISCOVERY": 1
  }
}
```

Codex CLI declares a provider in `~/.codex/config.toml` and tells it which wire protocol to speak (an excerpt; the docs page adds three header lines, a model catalog file and `auth.json`):

```toml
[model_providers.explyt]
name = "explyt"
base_url = "https://api.explyt.ai/openai/api"
wire_api = "responses"
requires_openai_auth = true
```

Cursor has no file at all: a toggle called **Override OpenAI Base URL** in its settings and a field for the key.

Notice that Claude Code's variable is named `ANTHROPIC_BASE_URL` while Codex asks for the `responses` wire API, and both point at the same URL. The same base URL must therefore accept at least two request formats under different paths. That is the part of "OpenAI-compatible" the phrase undersells: compatibility is a per-client contract, and the only ways to know a given client is honoured are the vendor's per-tool page or a probe. I checked only `/models` myself.

## Probing it from Java

You do not need any of those tools to find out whether a gateway is alive and what it serves. The models endpoint is a plain GET. Here is the whole probe in `java.net.http`, no dependencies:

```java
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public final class GatewayProbe {

    public static void main(String[] args) throws Exception {
        String baseUrl = args.length > 0 ? args[0] : "https://api.explyt.ai/openai/api";
        String apiKey = System.getenv().getOrDefault("GATEWAY_API_KEY", "");

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create(baseUrl + "/models"))
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "application/json")
                .GET();
        if (!apiKey.isBlank()) {
            request.header("Authorization", "Bearer " + apiKey);
        }

        HttpResponse<String> response = client.send(request.build(), HttpResponse.BodyHandlers.ofString());

        System.out.println("HTTP " + response.statusCode());
        response.headers().firstValue("content-type").ifPresent(ct -> System.out.println("content-type: " + ct));
        String body = response.body();
        System.out.println(body.length() > 600 ? body.substring(0, 600) + "..." : body);
    }
}
```

Compile with `javac --release 17 GatewayProbe.java`. Run it without a key first:

```bash
$ java GatewayProbe
HTTP 401
content-type: application/json
{"error":{"message":"API key is required (request 072ce715-...)","type":"authentication_error","code":"authentication_error"}}
```

That is the answer you want from an unauthenticated call: a fast 401 with a JSON error body in the OpenAI error shape and a request id in the message. A hanging socket or a 404 would each mean a different problem. The 401 tells you the path exists and the server is filtering on the bearer token. Export `GATEWAY_API_KEY` and run again, and the same call returns the model catalog the gateway serves. Individual clients may show a subset: Codex reads a static catalog file, and the ChatGPT app is limited to OpenAI models.

Why bother when the tool has a settings screen? Because when the tool later says "model not found" or "unauthorized", the settings screen is the last place you will learn anything. A twenty-line probe with the same base URL and the same header is the fastest way to separate "my key is wrong" from "my client sends the wrong shape".

## The boundary a Java developer should care about

Here is the design decision in this release that I think generalizes: the key carries inference only.

Put an agent in front of a Java project and ask what it needs beyond a model. It needs the project structure the IDE has already indexed. It needs to run a Gradle or Maven configuration and read the result. It needs a debugger that can stop on a line and show you a variable, because a stack trace in a log is a hypothesis and a paused frame is a fact. None of that travels over the model connection, and the release is explicit that pasting the key into Cursor or Claude Code gives them the model only.

Those capabilities take a different route: an MCP server that runs inside the IDE, exposes selected tool groups to whichever external agent connects, and does no inference of its own. The agent brings its own model, over whatever gateway it likes, and calls IDE tools over MCP. Two planes, two protocols, two failure modes you can debug separately.

I find that separation healthier than the alternative, where a vendor's key silently unlocks a vendor's tools. If you have ever debugged a Spring application where the security filter and the business logic shared a config bean, you know why.

## Lesson one: code does not tokenize like prose

The release also fixes a context-fill indicator that had been reporting about a third less than the truth. The cause is worth a paragraph because it applies to anyone building on top of an LLM API.

Local token estimates are usually calibrated on natural language. An agent's conversation is not natural language; it is mostly source code, stack traces, diffs and tool output. Those tokenize denser: more tokens per character, because identifiers, punctuation and indentation split into many small pieces. Estimate a Java file with a prose heuristic and you will undercount.

The consequence in an agent is not cosmetic. Auto-compaction of history is triggered by the estimate. If the estimate is low, compaction never fires, the real context overflows, and the request fails at the provider with an error the user did not see coming.

The fix is the one you would write for any drifting estimator: reconcile against ground truth whenever ground truth arrives. Every provider response carries the input token count it reports for that request. Compare it with the local estimate for the same request, derive a correction factor, apply it going forward, and persist it with the conversation so a restart does not reset the calibration.

The shipped code is not what follows; this is an illustration of the shape:

```java
import java.util.Objects;

/** Keeps the latest correction between a local token heuristic and provider-reported input tokens. */
public final class ContextEstimate {

    private final double charsPerTokenGuess;
    private double correction;

    public ContextEstimate(double charsPerTokenGuess, double persistedCorrection) {
        if (charsPerTokenGuess <= 0) {
            throw new IllegalArgumentException("charsPerTokenGuess must be positive");
        }
        this.charsPerTokenGuess = charsPerTokenGuess;
        this.correction = persistedCorrection > 0 ? persistedCorrection : 1.0;
    }

    /** What the UI shows before the provider has answered. */
    public long estimateTokens(CharSequence history) {
        Objects.requireNonNull(history, "history");
        double raw = history.length() / charsPerTokenGuess;
        return Math.round(raw * correction);
    }

    /**
     * Call once per provider response with the input tokens it reported for this exact history.
     * The last ratio wins; a production version would smooth it (for example an exponential moving average).
     */
    public void reconcile(CharSequence historySent, long reportedInputTokens) {
        Objects.requireNonNull(historySent, "historySent");
        if (reportedInputTokens <= 0 || historySent.length() == 0) {
            return;
        }
        double raw = historySent.length() / charsPerTokenGuess;
        correction = reportedInputTokens / raw;
    }

    /** Persist this next to the chat so a restart keeps the calibration. */
    public double correction() {
        return correction;
    }
}
```

The provider's `usage` block is the only authoritative token count you get, and an estimator that never reads it has no way to notice its own drift.

## Lesson two: refresh the token before it dies, one per server

The second fix is about MCP servers that authenticate through a browser and hand back a short-lived access token. Roughly an hour, in the cases the release describes. An IDE session runs longer than that. The old behaviour was the classic one: first call after expiry fails, the user is sent to reconnect.

Every Java developer who has wired an OAuth client has met this. The fix is equally classic: schedule the refresh ahead of expiry with a safety margin, keep credentials per server so a refresh for one connection cannot be handed to another, and make sure one failed refresh does not end the chain.

```java
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/** One access token per server, refreshed before it expires. Illustrative. */
public final class TokenRegistry {

    public record Token(String value, Instant expiresAt) {}

    private static final long MIN_DELAY_MILLIS = 1_000;
    private static final long RETRY_DELAY_MILLIS = 30_000;

    private final ConcurrentMap<String, Token> tokens = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, ScheduledFuture<?>> pending = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler;
    private final Function<String, Token> refreshForServer;
    private final Duration skew;

    public TokenRegistry(ScheduledExecutorService scheduler,
                         Function<String, Token> refreshForServer,
                         Duration skew) {
        this.scheduler = scheduler;
        this.refreshForServer = refreshForServer;
        this.skew = skew;
    }

    /** Registering the same server again cancels its previous refresh chain. */
    public void register(String serverId, Token initial) {
        tokens.put(serverId, initial);
        scheduleRefresh(serverId, initial);
    }

    public String bearerFor(String serverId) {
        Token t = tokens.get(serverId);
        if (t == null) {
            throw new IllegalStateException("no token for " + serverId);
        }
        return "Bearer " + t.value();
    }

    private void scheduleRefresh(String serverId, Token current) {
        long untilRefresh = Duration.between(Instant.now(), current.expiresAt().minus(skew)).toMillis();
        schedule(serverId, Math.max(MIN_DELAY_MILLIS, untilRefresh), () -> {
            try {
                Token fresh = refreshForServer.apply(serverId);
                tokens.put(serverId, fresh);
                scheduleRefresh(serverId, fresh);
            } catch (RuntimeException e) {
                // keep the old token until it expires; try again, do not let the chain die
                schedule(serverId, RETRY_DELAY_MILLIS, () -> scheduleRefresh(serverId, current));
            }
        });
    }

    private void schedule(String serverId, long delayMillis, Runnable task) {
        ScheduledFuture<?> previous = pending.put(serverId,
                scheduler.schedule(task, delayMillis, TimeUnit.MILLISECONDS));
        if (previous != null) {
            previous.cancel(false);
        }
    }
}
```

The per-server map matters. With one shared token field, a refresh for server A overwrites the credential server B was using, and you get an authentication failure that looks random. The release notes state the outcome, each connected server now gets its own token; they do not say what the old code did, and this sketch is my guess at the shape.

## A checklist before you paste a key into any agent

None of this is specific to one vendor. Whichever gateway and whichever agent you use, these are the questions I would answer first.

1. Probe the base URL yourself with a plain HTTP client. Unauthenticated, you want a fast 401 with a JSON body. Authenticated, you want the model list.
2. Find out which wire dialect your client speaks and confirm the gateway accepts it. An Anthropic-style client and an OpenAI Responses client pointing at the same URL are two different contracts.
3. Do not sign in to the vendor's own account in the same client. In the release I am describing, that sign-in bypasses the gateway and the requests are rejected; other gateways will fail in their own way.
4. Ask what the key does not carry. If the answer is "IDE tools come with it", ask how, over which protocol, and what happens to those tools when the key is revoked.
5. Treat the key like a password. One key across many tools means one revocation invalidates all of them at once, which is the right property and also the one that hurts when you forget where you pasted it.
6. Check how the client counts context. If it never reads the provider's reported usage, expect it to overflow on code-heavy sessions.
7. For any tool that authenticates through a browser, find out how it refreshes and whether credentials are scoped per connection.

## Disclosure and where the details live

I am the product manager at Explyt, and the release I have been describing is [Explyt 5.19](https://explyt.ai/en/blog/release-explyt-5-19). The release notes carry the per-tool setup pages, the model restrictions per client and the exact wording of the key/MCP boundary. Everything I said about the gateway's behaviour comes from those pages or from running the probe above; I have not described anything about its internals, because I do not know them at that level and neither should an article like this pretend to.

*Yurii Kostyukov is the product manager of Explyt, an AI agent for JetBrains IDEs. Before that he worked on symbolic-execution test generation for .NET and LLVM at JetBrains Research and Huawei.*

## Related Posts

* [Did Your AI Agent Ever Run a Debugger? One JVM Bug, Two Agent Runs](https://foojay.io/today/did-your-ai-agent-run-the-debugger-one-jvm-bug-two-agent-runs/)
* [Debugging Is Invariant Discovery: What One Kafka Session Taught Us About AI Agents](https://foojay.io/today/debugging-is-invariant-discovery-what-one-kafka-session-taught-us-about-ai-agents/)
* [Code. Check. Commit. Never Leave the Terminal with Claude Code + SonarQube MCP](https://foojay.io/today/claude-code-sonarqube-mcp/)
