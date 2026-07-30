---
title: "No Keys, No LLM: Building a Wikidata Definition API with Embabel"
slug: "embabel-spring-boot-wikidata-definition-api"
date: "2026-03-05T08:00:25+00:00"
lastmod: "2026-03-05T08:31:48+00:00"
description: "I built a Spring Boot 4 REST API that defines terms using Wikidata, orchestrated by an Embabel agent—no API keys, no local models, fully reproducible."
authors:
  - "vincent-vauban"
image: "/images/posts/2026/03/embabel-spring-boot-wikidata-definition-api/3embabel.png"
categories:
  - "AI"
  - "Java"
  - "Spring"
  - "Videos"
tags:
related_posts:
enlighterjs: true
frozen: false
---

TL;DR {#h2-0-tl-dr}
-------------------

* I built a **Spring Boot 4** API that defines terms via **Wikidata**.
* The app is fully reproducible: **no API keys** and **no model installation** needed.
* Embabel orchestrates the pipeline as a sequence of actions to achieve the goal `DefinitionResult`.
* The logs show planning, execution, and typed object binding---the most useful part for teaching agentic flows.

{{< youtube TiuYS6K3HaU >}}

<br />

I wanted a demo that is **simple** , **reproducible** , and still **shows agentic orchestration** in a way that's easy to explain on video.

So I built a small Spring Boot 4 app that exposes a single endpoint:

* `GET /api/wiki/define?term=...`

It returns a compact JSON "definition" fetched from **Wikidata** (no authentication, no API keys).  

The important part: I used **Embabel** to orchestrate the workflow, even though the workflow is deterministic and **does not need an LLM**.

*** ** * ** ***

Part I --- Concepts {#h2-1-part-i-concepts}
-------------------------------------------

### I.1 Embabel {#h3-2-i-1-embabel}

Embabel is an agent framework for the JVM. I like to think of it as a way to model a workflow as:

* **Actions**: steps the agent can execute
* **Goals**: what the workflow should produce
* **State / facts**: typed objects available at each moment
* **Planning**: decide which actions to run and in which order to achieve the goal

In practice, that means I don't call methods in a fixed chain. I provide an initial input (a domain object), tell Embabel what type I want as the result, and Embabel **plans** and runs the required actions.

*** ** * ** ***

### I.2 Spring AI (even in a "no LLM" demo) {#h3-3-i-2-spring-ai-even-in-a-no-llm-demo}

Spring AI provides an abstraction layer for interacting with chat models (and other AI components) using Spring-friendly APIs.

In this project, I implemented a tiny **NOOP** chat model. It's not used to generate anything. It exists because the Embabel starter expects a default model entry to be configured at startup.

This kept the demo:

* fully runnable without credentials,
* focused on orchestration,
* and easy to extend later with a real model.

*** ** * ** ***

### I.3 Role of Embabel in this application {#h3-4-i-3-role-of-embabel-in-this-application}

A reasonable question is: *"What's the point of using Embabel just to query a REST API?"*

The REST call is not the point. The point is to demonstrate a workflow that:

1. starts from a `DefinitionRequest(term)`
2. resolves a Wikidata **entity ID** (Q-id)
3. fetches **entity details**
4. builds a typed `DefinitionResult`

Embabel makes these steps explicit, typed, and observable, and it can re-plan as the state evolves. That's a much better foundation than packing everything into one big service method---especially when the demo grows.

*** ** * ** ***

### I.4 Wikidata: definition and why it's ideal for demos {#h3-5-i-4-wikidata-definition-and-why-it-s-ideal-for-demos}

Wikidata is a public, open knowledge base. It's perfect for demos because:

* it's online,
* it's free to read,
* and the APIs are easy to call from a small Java project.

I used two endpoints:

* `wbsearchentities` to search for a term and retrieve the most relevant Q-id
* `Special:EntityData/{QID}.json` to fetch structured entity data (labels, descriptions, and Wikipedia sitelinks)

This gives a nice "definition API" in a few lines of code, with zero setup for viewers.

*** ** * ** ***

Part II --- App building (code + explanations) {#h2-6-part-ii-app-building-code-explanations}
---------------------------------------------------------------------------------------------

### II.1 Maven setup (`pom.xml`) {#h3-7-ii-1-maven-setup-pom-xml}

I used Spring Boot **4.0.3** with Java **25** , and Embabel **0.3.4**.

Because this is Boot 4, I added `spring-boot-starter-restclient` so `RestClient.Builder` is auto-configured.

I also forced **Jackson 2** compatibility (`spring-boot-jackson2`) and excluded `spring-boot-starter-json`, because the Embabel starter wiring in this setup expects `Jackson2ObjectMapperBuilder`.

<pre class="EnlighterJSRAW" data-enlighter-language="xml">&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd"&gt;
    &lt;modelVersion&gt;4.0.0&lt;/modelVersion&gt;

    &lt;parent&gt;
        &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
        &lt;artifactId&gt;spring-boot-starter-parent&lt;/artifactId&gt;
        &lt;version&gt;4.0.3&lt;/version&gt;
        &lt;relativePath/&gt;
    &lt;/parent&gt;

    &lt;groupId&gt;com.example&lt;/groupId&gt;
    &lt;artifactId&gt;wikidemo&lt;/artifactId&gt;
    &lt;version&gt;0.0.1-SNAPSHOT&lt;/version&gt;
    &lt;name&gt;wikidemo&lt;/name&gt;

    &lt;properties&gt;
        &lt;java.version&gt;25&lt;/java.version&gt;
        &lt;embabel-agent.version&gt;0.3.4&lt;/embabel-agent.version&gt;
    &lt;/properties&gt;

    &lt;dependencies&gt;
        &lt;!-- REST endpoint --&gt;
        &lt;dependency&gt;
            &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
            &lt;artifactId&gt;spring-boot-starter-web&lt;/artifactId&gt;
            &lt;exclusions&gt;
                &lt;exclusion&gt;
                    &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
                    &lt;artifactId&gt;spring-boot-starter-json&lt;/artifactId&gt;
                &lt;/exclusion&gt;
            &lt;/exclusions&gt;
        &lt;/dependency&gt;

        &lt;dependency&gt;
            &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
            &lt;artifactId&gt;spring-boot-starter-restclient&lt;/artifactId&gt;
        &lt;/dependency&gt;

        &lt;dependency&gt;
            &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
            &lt;artifactId&gt;spring-boot-jackson2&lt;/artifactId&gt;
        &lt;/dependency&gt;

        &lt;!-- Embabel agent platform --&gt;
        &lt;dependency&gt;
            &lt;groupId&gt;com.embabel.agent&lt;/groupId&gt;
            &lt;artifactId&gt;embabel-agent-starter&lt;/artifactId&gt;
            &lt;version&gt;${embabel-agent.version}&lt;/version&gt;
        &lt;/dependency&gt;
    &lt;/dependencies&gt;

    &lt;build&gt;
        &lt;plugins&gt;
            &lt;plugin&gt;
                &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
                &lt;artifactId&gt;spring-boot-maven-plugin&lt;/artifactId&gt;
            &lt;/plugin&gt;
        &lt;/plugins&gt;
    &lt;/build&gt;
&lt;/project&gt;</pre>

*** ** * ** ***

### II.2 Configuration (`application.yml`) {#h3-8-ii-2-configuration-application-yml}

I set the server port and configured the default Embabel model name to `noop`.

<pre class="EnlighterJSRAW" data-enlighter-language="yaml">spring:
  application:
    name: wikidemo

server:
  port: 8080

embabel:
  models:
    default-llm: noop
</pre>

*** ** * ** ***

### II.3 App launcher + Embabel enablement + NOOP LLM registration {#h3-9-ii-3-app-launcher-embabel-enablement-noop-llm-registration}

The application entrypoint enables agent scanning using `@EnableAgents`, then registers a "noop" model so the platform boots without external dependencies.

<pre class="EnlighterJSRAW" data-enlighter-language="java">package com.vv.wikidemo;

import com.embabel.agent.config.annotation.EnableAgents;
import com.embabel.agent.spi.LlmService;
import com.embabel.agent.spi.support.springai.SpringAiLlmService;
import com.vv.wikidemo.service.NoopChatModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableAgents
public class WikiDemoApplication {
    public static void main( String[] args ) {
        SpringApplication.run( WikiDemoApplication.class, args );
    }

    @Bean
    public LlmService&lt;?&gt; noopLlm() {
        return new SpringAiLlmService(
                "noop",          // model name (must match embabel.models.default-llm)
                "noop-provider", // provider label (any string)
                new NoopChatModel()
        );
    }
}

</pre>

*** ** * ** ***

### II.4 The NOOP ChatModel (Spring AI) {#h3-10-ii-4-the-noop-chatmodel-spring-ai}

This is intentionally minimal. If Embabel ever calls it, it returns a predictable message.

<pre class="EnlighterJSRAW" data-enlighter-language="java">package com.vv.wikidemo.service;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.Prompt;

import java.util.List;

public class NoopChatModel implements ChatModel {

    @Override
    public ChatResponse call( Prompt prompt ) {
        var msg = new AssistantMessage(
                "NOOP LLM: no real LLM configured (this demo doesn't need one)."
        );
        return new ChatResponse( List.of( new Generation( msg ) ) );
    }
}
</pre>

*** ** * ** ***

### II.5 Domain model (Java records) {#h3-11-ii-5-domain-model-java-records}

I used records for the request, intermediate agent objects, and final result.

```java

```

<pre class="EnlighterJSRAW" data-enlighter-language="java">package com.vv.wikidemo.model;
public record DefinitionRequest(String term) {
}

public record DefinitionResult(
        String term,
        String entityId,
        String label,
        String description,
        String wikidataUrl,
        String wikipediaUrl
) {
}

public record WikidataEntityDetails(
        String label,
        String description,
        String wikipediaTitle
) {}

public record WikidataEntityId(String id) {
}
</pre>

```java

```

```java

```

The key idea is that Embabel "stores" and "reuses" these typed objects during execution. They become the agent's working memory.

*** ** * ** ***

### II.6 Repository: Wikidata calls with RestClient {#h3-12-ii-6-repository-wikidata-calls-with-restclient}

The repository is responsible for the data access logic only:

* search for the best match (Q-id)
* fetch details for that Q-id
* build stable URLs

I kept DTO mappings minimal and resilient with `@JsonIgnoreProperties(ignoreUnknown = true)`.

<pre class="EnlighterJSRAW" data-enlighter-language="java">package com.vv.wikidemo.repository;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vv.wikidemo.model.WikidataEntityDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class WikidataRepository {

    private final RestClient wikidata;

    public WikidataRepository( RestClient.Builder builder ) {
        this.wikidata = builder
                .baseUrl( "https://www.wikidata.org" )
                .defaultHeader( HttpHeaders.USER_AGENT, "wikidemo/0.0.1 (SpringBoot+Embabel demo)" )
                .build();
    }

    /**
     * Step 1: find the first matching Q-id for a term
     */
    public Optional&lt;SearchItem&gt; searchFirst( String term ) {
        SearchResponse response = wikidata.get()
                                          .uri( uriBuilder -&gt; uriBuilder
                                                  .path( "/w/api.php" )
                                                  .queryParam( "action", "wbsearchentities" )
                                                  .queryParam( "search", term )
                                                  .queryParam( "language", "en" )
                                                  .queryParam( "format", "json" )
                                                  .queryParam( "limit", "1" )
                                                  .build() )
                                          .retrieve()
                                          .body( SearchResponse.class );

        if ( response == null || response.search == null || response.search.isEmpty() ) {
            return Optional.empty();
        }
        return Optional.ofNullable( response.search.get( 0 ) );
    }

    /**
     * Step 2: fetch label/description (and Wikipedia title if present) from Special:EntityData
     */
    public WikidataEntityDetails fetchEntityDetails( String entityId ) {
        EntityDataResponse data = wikidata.get()
                                          .uri( "/wiki/Special:EntityData/{id}.json", entityId )
                                          .retrieve()
                                          .body( EntityDataResponse.class );

        if ( data == null || data.entities == null || !data.entities.containsKey( entityId ) ) {
            return new WikidataEntityDetails( null, null, null );
        }

        Entity entity = data.entities.get( entityId );
        String label = valueOf( entity.labels, "en" );
        String desc = valueOf( entity.descriptions, "en" );
        String wikiTitle = (entity.sitelinks != null &amp;&amp; entity.sitelinks.containsKey( "enwiki" ))
                ? entity.sitelinks.get( "enwiki" ).title
                : null;

        return new WikidataEntityDetails( label, desc, wikiTitle );
    }

    public static String wikidataUrl( String entityId ) {
        return "https://www.wikidata.org/wiki/" + entityId;
    }

    public static String wikipediaUrl( String title ) {
        if ( title == null || title.isBlank() ) {
            return null;
        }
        String normalized = title.replace( ' ', '_' );
        return "https://en.wikipedia.org/wiki/" + URLEncoder.encode( normalized, StandardCharsets.UTF_8 );
    }

    private static String valueOf( Map&lt;String, LangValue&gt; map, String lang ) {
        if ( map == null ) {
            return null;
        }
        LangValue lv = map.get( lang );
        return lv == null ? null : lv.value;
    }

    // --- DTOs for JSON mapping (minimal fields only) ---

    @JsonIgnoreProperties( ignoreUnknown = true )
    static class SearchResponse {
        @JsonProperty( "search" )
        public List&lt;SearchItem&gt; search;
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    public static class SearchItem {
        @JsonProperty( "id" )
        public String id;

        @JsonProperty( "label" )
        public String label;

        @JsonProperty( "description" )
        public String description;
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    static class EntityDataResponse {
        @JsonProperty( "entities" )
        public Map&lt;String, Entity&gt; entities;
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    static class Entity {
        @JsonProperty( "labels" )
        public Map&lt;String, LangValue&gt; labels;

        @JsonProperty( "descriptions" )
        public Map&lt;String, LangValue&gt; descriptions;

        @JsonProperty( "sitelinks" )
        public Map&lt;String, Sitelink&gt; sitelinks;
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    static class LangValue {
        @JsonProperty( "value" )
        public String value;
    }

    @JsonIgnoreProperties( ignoreUnknown = true )
    static class Sitelink {
        @JsonProperty( "title" )
        public String title;
    }
}</pre>

*** ** * ** ***

### II.7 The Embabel agent (actions + goal) {#h3-13-ii-7-the-embabel-agent-actions-goal}

The agent defines the workflow. Each method is a step (`@Action`). The final step is tagged as a goal (`@AchievesGoal`) because it produces the desired output type `DefinitionResult`.

```java

```

<pre class="EnlighterJSRAW" data-enlighter-language="java">package com.vv.wikidemo.service;

import com.embabel.agent.api.annotation.AchievesGoal;
import com.embabel.agent.api.annotation.Action;
import com.embabel.agent.api.annotation.Agent;
import com.vv.wikidemo.model.DefinitionRequest;
import com.vv.wikidemo.model.DefinitionResult;
import com.vv.wikidemo.model.WikidataEntityDetails;
import com.vv.wikidemo.model.WikidataEntityId;
import com.vv.wikidemo.repository.WikidataRepository;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Agent( description = "Define a word using Wikidata (no LLM, no auth)" )
public class WikidataDefinitionAgent {

    private final WikidataRepository repo;

    public WikidataDefinitionAgent( WikidataRepository repo ) {
        this.repo = repo;
    }

    @Action
    public WikidataEntityId findEntityId( DefinitionRequest request ) {
        var hit = repo.searchFirst( request.term() )
                      .orElseThrow( () -&gt; new ResponseStatusException(
                              NOT_FOUND, "No Wikidata entity found for term: " + request.term()
                      ) );
        return new WikidataEntityId( hit.id );
    }

    @Action
    public WikidataEntityDetails fetchDetails( WikidataEntityId id ) {
        return repo.fetchEntityDetails( id.id() );
    }

    @Action
    @AchievesGoal( description = "Return a Wikidata-based definition" )
    public DefinitionResult build( DefinitionRequest request,
                                   WikidataEntityId id,
                                   WikidataEntityDetails details ) {

        String wikidataUrl = WikidataRepository.wikidataUrl( id.id() );
        String wikipediaUrl = WikidataRepository.wikipediaUrl( details.wikipediaTitle() );

        // If Wikidata doesn't have an English label/description, you still get a stable entity link.
        return new DefinitionResult(
                request.term(),
                id.id(),
                details.label(),
                details.description(),
                wikidataUrl,
                wikipediaUrl
        );
    }
}
</pre>

```java

```

```java

```

I like this structure because it stays small and readable. More importantly, it becomes easy to extend later:

* add a disambiguation action,
* add a caching action,
* add alternative paths,
* add optional post-processing.

*** ** * ** ***

### II.8 Service: running the agent via `AgentInvocation` {#h3-14-ii-8-service-running-the-agent-via-agentinvocation}

The service is the bridge between the web layer and Embabel. It creates an `AgentInvocation` and calls it with a `DefinitionRequest`.

<pre class="EnlighterJSRAW" data-enlighter-language="java">package com.vv.wikidemo.service;

import com.embabel.agent.api.invocation.AgentInvocation;
import com.embabel.agent.core.AgentPlatform;
import com.vv.wikidemo.model.DefinitionRequest;
import com.vv.wikidemo.model.DefinitionResult;
import org.springframework.stereotype.Service;

@Service
public class WikiService {

    private final AgentPlatform                     agentPlatform;
    private final AgentInvocation&lt;DefinitionResult&gt; invocation;

    public WikiService( AgentPlatform agentPlatform ) {
        this.agentPlatform = agentPlatform;
        this.invocation = AgentInvocation
                .builder( agentPlatform )
                .build( DefinitionResult.class );
    }

    public DefinitionResult define( String term ) {
        return invocation.invoke( new DefinitionRequest( term ) );
    }
}</pre>

*** ** * ** ***

### II.9 Controller: a single endpoint {#h3-15-ii-9-controller-a-single-endpoint}

The controller stays boring on purpose. All the interesting logic is in the agent and repository.

```java

```

<pre class="EnlighterJSRAW" data-enlighter-language="java">package com.vv.wikidemo.controller;

import com.vv.wikidemo.model.DefinitionResult;
import com.vv.wikidemo.service.WikiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping( "/api/wiki" )
public class WikiController {

    private final WikiService wikiService;

    public WikiController( WikiService wikiService ) {
        this.wikiService = wikiService;
    }

    @GetMapping( "/define" )
    public DefinitionResult define( @RequestParam( "term" ) String term ) {
        return wikiService.define( term );
    }
}</pre>

```java

```

```java

```

*** ** * ** ***

Part III --- Demo {#h2-16-part-iii-demo}
----------------------------------------

### III.1 Curl request {#h3-17-iii-1-curl-request}

<pre class="EnlighterJSRAW" data-enlighter-language="shell">curl --request get --url 'http://localhost:8080/api/wiki/define?term=kafka'</pre>

### III.2 Response {#h3-18-iii-2-response}

<pre class="EnlighterJSRAW" data-enlighter-language="json">{
  "term": "kafka",
  "entityId": "Q16235208",
  "label": "Apache Kafka",
  "description": "open source data stream processing platform",
  "wikidataUrl": "https://www.wikidata.org/wiki/Q16235208",
  "wikipediaUrl": "https://en.wikipedia.org/wiki/Apache_Kafka"
}</pre>

This is intentionally "small JSON": label + description + canonical links.

### III.3 Logs: the agentic part {#h3-19-iii-3-logs-the-agentic-part}

These logs are the best part to show on screen, because they reveal Embabel's planning and execution.

<pre class="EnlighterJSRAW" data-enlighter-language="shell">21:35:05.039 [tomcat-handler-2] INFO  Embabel - [goofy_mcclintock] created
21:35:05.039 [tomcat-handler-2] INFO  Embabel - [goofy_mcclintock] object added: DefinitionRequest

21:35:05.046 [task-1] INFO  Embabel - [goofy_mcclintock] formulated plan:
  com.vv.wikidemo.service.WikidataDefinitionAgent.findEntityId -&gt;
  com.vv.wikidemo.service.WikidataDefinitionAgent.fetchDetails -&gt;
  com.vv.wikidemo.service.WikidataDefinitionAgent.build

21:35:05.047 [task-1] INFO  Embabel - [goofy_mcclintock] executing action ... findEntityId
21:35:05.745 [task-1] INFO  Embabel - [goofy_mcclintock] executed action ... findEntityId in PT0.686S
21:35:05.743 [task-1] INFO  Embabel - [goofy_mcclintock] object bound it:WikidataEntityId

21:35:05.749 [task-1] INFO  Embabel - [goofy_mcclintock] formulated plan:
  com.vv.wikidemo.service.WikidataDefinitionAgent.fetchDetails -&gt;
  com.vv.wikidemo.service.WikidataDefinitionAgent.build

21:35:05.749 [task-1] INFO  Embabel - [goofy_mcclintock] executing action ... fetchDetails
21:35:06.187 [task-1] INFO  Embabel - [goofy_mcclintock] executed action ... fetchDetails in PT0.437S
21:35:06.186 [task-1] INFO  Embabel - [goofy_mcclintock] object bound it:WikidataEntityDetails

21:35:06.189 [task-1] INFO  Embabel - [goofy_mcclintock] formulated plan:
  com.vv.wikidemo.service.WikidataDefinitionAgent.build

21:35:06.190 [task-1] INFO  Embabel - [goofy_mcclintock] executing action ... build
21:35:06.191 [task-1] INFO  Embabel - [goofy_mcclintock] object bound it:DefinitionResult
21:35:06.196 [task-1] INFO  Embabel - [goofy_mcclintock] goal ... achieved in PT1.164...</pre>

What stands out:

* Embabel starts with `DefinitionRequest`
* It **formulates a plan** (sequence of actions)
* It executes each action
* It binds the produced objects (`WikidataEntityId`, `WikidataEntityDetails`, then `DefinitionResult`)
* It declares the goal achieved

This is the "agentic" angle: Embabel is not just calling methods---it's planning against typed state.

*** ** * ** ***

Part IV --- Conclusion and extensions {#h2-20-part-iv-conclusion-and-extensions}
--------------------------------------------------------------------------------

This application intentionally starts simple. It's a demo designed to be reproduced in minutes.

However, the Embabel structure is already useful because it's an orchestrator. Extending the system becomes a matter of adding actions and (optionally) conditions, not rewriting a monolithic service method.

Here are extensions that make the demo evolve naturally:

### 1) Disambiguation {#h3-21-1-disambiguation}

Instead of `limit=1`, fetch the top N hits and add an action to pick the best match. For example:

* exact label match
* description keyword match
* "instance of" filtering (person vs concept vs product)

### 2) Multi-language {#h3-22-2-multi-language}

Add `lang` to `DefinitionRequest` and propagate it into:

* `wbsearchentities&language=...`
* selecting labels/descriptions by language

### 3) Confidence score {#h3-23-3-confidence-score}

Add a `ConfidenceScore` record and an action that computes a score based on:

* match quality
* label similarity
* number of aliases
* presence of sitelinks

Return it to consumers to make the API safer to use.

### 4) Caching and rate limiting {#h3-24-4-caching-and-rate-limiting}

Add an action that checks a cache before querying Wikidata. This is a classic production step and it fits nicely as an independent action.

### 5) Multi-source enrichment {#h3-25-5-multi-source-enrichment}

Add an alternative source for definitions:

* DBpedia
* Wikipedia summary API
* internal enterprise knowledge base

Embabel becomes more valuable as the number of sources increases, because orchestration becomes a first-class concept.

### 6) Optional LLM post-processing (when needed) {#h3-26-6-optional-llm-post-processing-when-needed}

A good, minimal LLM use case is last-mile text rewriting:

* convert the Wikidata description into a more "dictionary-like" sentence
* add examples
* translate to French
* generate a short TL;DR

This keeps the retrieval deterministic and makes the LLM optional, which is often a safer architecture.

*** ** * ** ***

Repo: [https://github.com/vinny59200/embabel](https://github.com/vinny59200/embabel%20)

Udemy Spring Certification Practice Course: <https://www.udemy.com/course/spring-professional-certification-6-full-tests-2v0-7222-a/?referralCode=04B6ED315B27753236AC>

Study Guide For Spring: <https://spring-book.mystrikingly.com>
