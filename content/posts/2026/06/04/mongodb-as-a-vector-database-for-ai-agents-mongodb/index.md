---
title: "MongoDB as a Vector Database for AI Agents-MongoDB"
slug: "mongodb-as-a-vector-database-for-ai-agents-mongodb"
date: "2026-06-04T10:00:00+00:00"
description: "Modern artificial intelligence systems are continually evolving. Large Language Models, or LLMs, have become the backbone of modern applications and help build conversational interfaces, like GPS, to more integrated content. However, LLMs lack memory and the capacity to retain content across interactions because they are stateless. And these limitations led to the building of AI agents. These AI agents build beyond simple prompt-response interactions into more autonomous, task-oriented workflows."
authors:
  - "aasawari-sahasrabuddhe"
image: "Brand-Shape-Yes-Shape-Color-Lavender-7-e1780489356637.png"
categories:
  - "Mongo"
  - "Uncategorized"
tags:
related_posts:
  - "agent-memory-with-spring-ai-redis"
  - "agents-meet-databases-the-future-of-agentic-architectures"
  - "ai-powered-chat-application-using-ibm-watsonx-ai-and-spring-ai"
  - "ai-powered-code-review-assistant-automated-code-analysis-with-spring-ai-and-mongodb"
enlighterjs: true
frozen: false
---

Modern artificial intelligence systems are continually evolving. Large Language Models, or LLMs, have become the backbone of modern applications and help build conversational interfaces, like GPS, to more integrated content. However, LLMs lack memory and the capacity to retain content across interactions because they are stateless. And these limitations led to the building of AI agents. These AI agents build beyond simple prompt-response interactions into more autonomous, task-oriented workflows.

These agents are not just model invocations; rather, they are an orchestration layer that combines reasoning with capabilities like retrieval, memory, and tool execution. While developing these agents, a database with the ability to store and retrieve semantically meaningful data is needed, which is where vector databases come into the picture.

A vector database stores data as dense numerical representations of text, images, or unstructured data. These embeddings capture semantic meaning, enabling similarity search instead of exact matching. With [MongoDB Atlas](https://www.mongodb.com/products/platform/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=vector-mongodb-foojay&utm_term=hugh.murray), developers can generate embeddings, store them alongside application data, and perform vector search within MongoDB Atlas, thus allowing AI agents to seamlessly combine operational data with semantic retrieval, simplifying architecture while improving performance.

In this blog post, we'll build an AI agent in Java using MongoDB as our database, by storing user queries, documents, agent memory, and embeddings in a single place. We will understand how MongoDB simplifies the implementation of retrieval-augmented generation and persistent memory systems.

Why should you use MongoDB for building AI agents? {#h2-0-why-should-you-use-mongodb-for-building-ai-agents}
------------------------------------------------------------------------------------------------------------

1. **Vector store and voyage AI support** -- MongoDB Atlas infrastructure offers you a developer-friendly ecosystem. Giving you the ability to store vector embeddings, create vector embeddings, and finally perform the vector search directly from the platform. This reduces the need to have different systems to build an enterprise application.
2. **Hybrid Search --** With MongoDB Atlas infrastructure, you can add filters with a vector search query and add additional conditions to the query results. Unlike specialized vector stores, MongoDB can do both semantic ([vector](https://www.mongodb.com/products/platform/atlas-vector-search/?utm_campaign=devrel&utm_source=third-party-content&utm_medium=cta&utm_content=vector-mongodb-foojay&utm_term=hugh.murray)) and classically structured (keyword) queries together.
3. **Developer Ecosystem --** MongoDB has been a developer-first database ever since, and as it continues to do so, it lets your application integrate efficiently.
4. **Operational Efficiency** - If you already use MongoDB, adding vector search avoids the need to introduce new infrastructure. It simplifies schema, transactions, and ops.

Understanding AI agents {#h2-1-understanding-ai-agents}
-------------------------------------------------------

While we are building AI agents, it is important to understand the core principles of embeddings, retrieval-augmented generation (RAG), and agentic architectures.

Vector embeddings, or simply embeddings, are dense vector representations of numerical data derived from texts, audio, videos, or any form of unstructured data. These vectors reside in a high-dimensional space where semantic similarity is preserved, which means semantically similar inputs are located closer together based on distance metrics such as cosine similarity or dot product.

This vector representation helps retrieve the top-K most similar vectors, effectively performing semantic retrieval rather than exact matching using vector search. This is critical for handling paraphrasing, ambiguity, and contextual queries.

With retrieval-augmented generation, or RAG, it builds the retrieval step into a pipeline. The model uses the semantic search ability to generate responses. One of the most common challenges with standard LLMs is hallucination, or the generation of incorrect or fabricated information when relying solely on parametric knowledge stored in model weights. RAG addresses this by grounding responses in retrieved documents rather than depending only on internal weights. As a result, it improves factual consistency, traceability, and the freshness of responses.

With these changes, the concepts of agents came into the picture. In these agentic architectures, vector search becomes a core abstraction for implementing memory systems:

1. **Short-term memory**: recent interaction history embedded and retrieved for conversational continuity
2. **Long-term memory**: persisted embeddings of past interactions, documents, and tool outputs
3. **Semantic recall**: retrieving context dynamically based on similarity rather than rigid keys

In these architectures, vector databases serve as both the retrieval and the storage layer for these systems. Therefore, vector search no longer remains just for semantic searches but rather a foundational building block for agentic systems. It underpins how agents retrieve knowledge, maintain memory, and produce contextually relevant, low-hallucination outputs in real-world applications.

Building a multi-agent application with MongoDB {#h2-2-building-a-multi-agent-application-with-mongodb}
-------------------------------------------------------------------------------------------------------

Before we get into the actual code for building the agents, let's first understand a few basic prerequisites for building the application.

1. A free-tier MongoDB Atlas cluster.
2. Create your free Voyage AI API key to generate embeddings in the database.
3. A Spring Boot setup to work with MongoDB using Spring Initializr.
4. Latest Java and Gradle/Maven versions installed.

To build the multi-agent system, we are using a travel replanning system as an example.

Here is a scenario to better understand this system: You are traveling from Toronto to San Francisco with a layover at New York. And then the reality happens. The flight between New York and SF is delayed by 9 hours, and now you need a better plan, since you have that one client meeting to showcase your product.

At this point, we do not need just a system that tells me another way, but rather helps me replan the entire trip. And this is where this multi-agent replanning system would come in. This system basically does the following:

* A Monitoring Agent that detects disruptions
* A Planner Agent orchestrates decisions
* A Booking Agent finds alternative routes
* A Budget Agent filters based on cost
* A Preference Agent aligns with user choices
* A Memory Agent recalls similar past situations

Each agent is simple on its own. But together, they behave like a coordinated system.

What makes this system powerful is the use of MongoDB as the database. MongoDB stores real-time data in a database; every event is recorded in the system, and Voyage AI and MongoDB's vector search capabilities store embeddings of past travel incidents and retrieve similar cases during replanning.

To build this system, we will be using four different collections: *trip_state, event, agent_decision, and incident_memory* . The *trip_state* stores the current state of the trip; all disruptions are copied into *events* . Every agent logs its reasoning in *agent_decision,* and *incident_memory* stores the past incidents.

Let's do this step by step.

### Step 1: Creating a vector search index {#h3-3-step-1-creating-a-vector-search-index}

Before we build the system, we need a vector search index. The embeddings in this project are produced by Voyage AI's*voyage-3-large model*.

Go to MongoDB Atlas, create a collection named *incident_memory*, and create a vector search index with the JSON below.

<pre class="EnlighterJSRAW" data-enlighter-language="json" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
  "fields": [
    {
      "numDimensions": 1024,
      "path": "embedding",
      "similarity": "cosine",
      "type": "vector"
    }
  ]
}
</pre>

### Step 2: Creating the Trip {#h3-4-step-2-creating-the-trip}

The trip is created with the following API call. This request lands in the controller. Because the request body is optional, we use a default CreateTripRequest when none is supplied and pass that normalized request into the service. So, normalized is just the incoming request or a default placeholder when the client omits the body.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@PostMapping("/create")
public TripState createTrip(@RequestBody(required = false) CreateTripRequest request) {
    CreateTripRequest normalized = request == null
            ? new CreateTripRequest("demo-user", null, null)
            : request;
    return tripService.createTrip(normalized);
}</pre>

And with the Service layer, it creates the trip. Example:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">curl -X POST "http://localhost:8080/trip/create" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "traveler-001",
    "preferences": {
      "airlinePreference": "SkyJet",
      "avoidRedEye": true,
      "maxAdditionalBudget": 250
    }
  }'</pre>

Would result in:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
  "id": "69dd6111674d2228e4db4b25",
  "userId": "traveler-001",
  "itinerary": [
    {
      "segmentId": "SEG-1",
      "type": "FLIGHT",
      "provider": "SkyJet",
      "fromLocation": "JFK",
      "toLocation": "SFO",
      "cost": 420.0
    }
  ],
  "status": "ON_TRACK"
}
</pre>

This trip gets stored in *trip_state*. At this point, everything looks fine.

### Step 3: Induce a disruption {#h3-5-step-3-induce-a-disruption}

At this step, we would add a delay status in the database. This is done using another post method:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">curl -X POST "http://localhost:8080/event/simulate-delay" \
  -H "Content-Type: application/json" \
  -d '{
    "tripId": "69dd6111674d2228e4db4b25",
    "delayMinutes": 180,
    "severity": "HIGH"
  }'
</pre>

This is done using another code block in the controller.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@PostMapping("/simulate-delay")
public TravelEvent simulateDelay(@RequestBody SimulateDelayRequest request)</pre>

And at the same time, something critical happens:

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">tripState.setStatus(TripStatus.DISRUPTED);
tripService.saveTrip(tripState);</pre>

This is your first agent that detects a problem, updates the state, and logs the decision.

The following delay is stimulated:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
  "id": "69dd6160674d2228e4db4b26",
  "tripId": "69dd6111674d2228e4db4b25",
  "type": "FLIGHT_DELAY",
  "severity": "HIGH",
  "metadata": {
    "from": "JFK",
    "to": "SFO",
    "delayMinutes": 180
  }
}</pre>

### Step 4: Replanning {#h3-6-step-4-replanning}

To trigger replanning, the PlannerAgent orchestrates the other agents. It asks MemoryAgent for similar incidents using MongoDB Vector Search and asks BookingAgent for alternative routes; then BudgetAgent and PreferenceAgent refine those options before PlannerAgent commits the final itinerary.

This enters the

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">@PostMapping("/plan/replan")
public TripState replan(@RequestBody ReplanRequest request)</pre>

And the planner agent takes over. Example:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">curl -X POST http://localhost:8080/plan/replan \
  -H "Content-Type: application/json" \
  -d '{
    "tripId": "69dd6111674d2228e4db4b25"
  }'</pre>

Which responds as

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
  "id": "69dd6111674d2228e4db4b25",
  "status": "REPLANNED",
  "itinerary": [
    {
      "segmentId": "OPT-CHI-1",
      "fromLocation": "JFK",
      "toLocation": "ORD",
      "cost": 320.0
    },
    {
      "segmentId": "OPT-CHI-2",
      "fromLocation": "ORD",
      "toLocation": "SFO",
      "cost": 320.0
    }
  ]
}</pre>

This is where it starts to suggest taking another flight from Chicago.

### Step 5: The Memory agents make use of vector search. {#h3-7-step-5-the-memory-agents-make-use-of-vector-search}

At first, the planner agents check, "Have we seen something like this?" If so, they retrieve it from the *incident_memory* and suggest what could be done.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">List&lt;IncidentMemory&gt; results = vectorSearchService.findSimilar(query);</pre>

### Step 6: Booking agent generates options {#h3-8-step-6-booking-agent-generates-options}

At this point, when no response is found, it starts to generate its own options. To do so,

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">List&lt;AlternativeRoute&gt; options =
    bookingAgent.generateOptions(tripState, latestEvent, memories);</pre>

The budget agent also starts to filter options with

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">List&lt;AlternativeRoute&gt; budgeted =
    budgetAgent.filterOptions(tripState, options);</pre>

### Step 7: The system finally makes the decision {#h3-9-step-7-the-system-finally-makes-the-decision}

Finally, the trip is updated, and the system records the reason for the same. At this point, when you call:

<pre class="EnlighterJSRAW" data-enlighter-language="bash" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">curl http://localhost:8080/trip/69dd6111674d2228e4db4b25</pre>

It would give you the response as:

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">{
  "status": "REPLANNED",
  "itinerary": [
    {
      "fromLocation": "JFK",
      "toLocation": "ORD"
    },
    {
      "fromLocation": "ORD",
      "toLocation": "SFO"
    }
  ]
}</pre>

Finally, the system didn't just detect a delay, but it used memory, coordinated multiple agents, and produced a better plan with a fully traceable decision history stored in MongoDB.

The complete code for this multi-agent system is available on the [GitHub repository](https://github.com/aasawariS/travel_multi-agent_with_mongodb).

Conclusion {#h2-10-conclusion}
------------------------------

In this blog, we tried to build a multi-agent system that is adaptive, stateful, and intelligent, all using MongoDB.

Starting from a simple travel itinerary, we saw how a disruption triggered a chain of coordinated actions across multiple agents. The Monitoring Agent detected the issue, the Memory Agent recalled similar past incidents using vector search, and the Planner Agent orchestrated Booking, Budget, and Preference Agents to arrive at a better alternative. Most importantly, every step of this process was persisted, making the system not just intelligent, but also explainable.

What makes this architecture powerful is the role of MongoDB as a unified data platform. Instead of separating operational data and AI memory into separate systems, MongoDB brings them together: This allows agents to move beyond stateless execution and operate with context and experience.

The vector search capability of MongoDB enables the system to retrieve similar past situations and apply that knowledge to new problems, reducing guesswork and improving decision quality.
