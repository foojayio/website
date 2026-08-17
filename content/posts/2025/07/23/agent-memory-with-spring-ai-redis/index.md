---
title: "Agent Memory with Spring AI & Redis"
slug: "agent-memory-with-spring-ai-redis"
date: "2025-07-23T16:34:04+00:00"
lastmod: "2025-09-05T10:01:06+00:00"
description: "Learn how to build a memory-enabled AI agent using Spring Boot, Spring AI, and Redis. Store and recall user preferences and conversation history for personalized, context-aware responses."
authors:
  - "raphael-delio"
image: "Copia-de-Blue---WIP.png"
categories:
  - "Redis"
  - "Spring"
  - "Tutorials"
  - "Use Cases"
tags:
related_posts:
  - "semantic-caching-with-springboot-redis"
  - "checking-out-junie-a-coding-agent-by-jetbrains"
  - "exposed-kotlin-orm-complete-guide"
  - "foojay-podcast-99"
enlighterjs: true
frozen: false
---

### **You're building an AI agent with memory using Spring AI and Redis.** **Unlike traditional chatbots that forget previous interactions, memory-enabled agents can recall past conversations and facts.** **It works by storing two types of memory in Redis: short-term (conversation history) and long-term (facts and experiences as vectors), allowing agents to provide personalized, context-aware responses.** {#h3-0-you-re-building-an-ai-agent-with-memory-using-spring-ai-and-redis-unlike-traditional-chatbots-that-forget-previous-interactions-memory-enabled-agents-can-recall-past-conversations-and-facts-it-works-by-storing-two-types-of-memory-in-redis-short-term-conversation-history-and-long-term-facts-and-experiences-as-vectors-allowing-agents-to-provide-personalized-context-aware-responses}

LLMs respond to each message in isolation, treating every interaction as if it's the first time they've spoken with a user. They lack the ability to remember previous conversations, preferences, or important facts.

Memory-enabled AI agents, on the other hand, can maintain context across multiple interactions. They remember who you are, what you've told them before, and can use that information to provide more personalized, relevant responses.

In a travel assistant scenario, for example, if a user mentions "I'm allergic to shellfish" in one conversation, and later asks for restaurant recommendations in Boston, a memory-enabled agent would recall the allergy information and filter out inappropriate suggestions, creating a much more helpful and personalized experience.

{{< youtube 0U1S0WSsPuE >}}

<br />

> Video: [What is an embedding model?](https://youtu.be/0U1S0WSsPuE)

Behind the scenes, this works thanks to vector similarity search. It turns text into vectors (embeddings) --- lists of numbers --- stores them in a vector database, and then finds the ones closest to your query when relevant information needs to be recalled.

{{< youtube o3XN4dImESE >}}

<br />

> Video: [What is semantic search?](https://youtu.be/o3XN4dImESE)

Today, we're gonna build a memory-enabled AI agent that helps users plan travel. It will remember user preferences, past trips, and important details across multiple conversations --- even if the user leaves and comes back later.

To do that, we'll build a Spring Boot app from scratch and use Redis as our memory store. It'll handle both short-term memory (conversation history) and long-term memory (facts and preferences as vector embeddings), enabling our agent to provide truly personalized assistance.

Redis as a Memory Store for AI Agents {#h2-1-redis-as-a-memory-store-for-ai-agents}
-----------------------------------------------------------------------------------

{{< youtube Yhv19le0sBw >}}

<br />

> Video: [What is a vector database?](https://youtu.be/Yhv19le0sBw)

In the last 15 years, Redis became the foundational infrastructure for realtime applications. Today, with Redis Open Source 8, it's committed to becoming the foundational infrastructure for AI applications as well.

Redis Open Source 8 not only turns the community version of Redis into a Vector Database, but also makes it the fastest and most scalable database in the market today. Redis 8 allows you to scale to one billion vectors without penalizing latency.

Learn more: <https://redis.io/blog/searching-1-billion-vectors-with-redis-8/>

For AI agents, Redis serves as both:

1. A short-term memory store using Redis Lists to maintain conversation history
2. A long-term memory store using Redis JSON and the Redis Query Engine that enables vector search to store and retrieve facts and experiences

Spring AI and Redis {#h2-2-spring-ai-and-redis}
-----------------------------------------------

Spring AI provides a unified API for working with various AI models and vector stores. Combined with Redis, it allows our users to easily build memory-enabled AI agents that can:

1. Store and retrieve vector embeddings for semantic search
2. Maintain conversation context across sessions
3. Extract and deduplicate memories from conversations
4. Summarize long conversations to prevent context window overflow

Building the Application {#h2-3-building-the-application}
---------------------------------------------------------

Our application will be built using Spring Boot with Spring AI and Redis. It will implement a travel assistant that remembers user preferences and past trips, providing personalized recommendations based on this memory.

### 0. GitHub Repository {#h3-4-0-github-repository}

The full application can be found on GitHub: <https://github.com/redis-developer/redis-springboot-resources/tree/main/artificial-intelligence/agent-long-term-memory-with-spring-ai>

### 1. Add the required dependencies {#h3-5-1-add-the-required-dependencies}

From a Spring Boot application, add the following dependencies to your Maven or Gradle file:

```
implementation("org.springframework.ai:spring-ai-transformers:1.0.0")
implementation("org.springframework.ai:spring-ai-starter-vector-store-redis")
implementation("org.springframework.ai:spring-ai-starter-model-openai")

implementation("com.redis.om:redis-om-spring:1.0.0-RC3")
```


### 2. Define the Memory model {#h3-6-2-define-the-memory-model}

The core of our implementation is the Memory class that represents items stored in long-term memory:

```
data class Memory(
    val id: String? = null,
    val content: String,
    val memoryType: MemoryType,
    val userId: String,
    val metadata: String = "{}",
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class MemoryType {
    EPISODIC,  // Personal experiences and preferences
    SEMANTIC   // General knowledge and facts
}
```


### 3. Configure the Vector Store {#h3-7-3-configure-the-vector-store}

We'll use Spring AI's `RedisVectorStore` to store and search vector embeddings of memories:

```
@Configuration
class MemoryVectorStoreConfig {

    @Bean
    fun memoryVectorStore(
        embeddingModel: EmbeddingModel,
        jedisPooled: JedisPooled
    ): RedisVectorStore {
        return RedisVectorStore.builder(jedisPooled, embeddingModel)
            .indexName("longTermMemoryIdx")
            .contentFieldName("content")
            .embeddingFieldName("embedding")
            .metadataFields(
                RedisVectorStore.MetadataField("memoryType", Schema.FieldType.TAG),
                RedisVectorStore.MetadataField("metadata", Schema.FieldType.TEXT),
                RedisVectorStore.MetadataField("userId", Schema.FieldType.TAG),
                RedisVectorStore.MetadataField("createdAt", Schema.FieldType.TEXT)
            )
            .prefix("memory:")
            .initializeSchema(true)
            .vectorAlgorithm(RedisVectorStore.Algorithm.HSNW)
            .build()
    }
}
```


Let's break this down:

* **Index Name** : `longTermMemoryIdx` - Redis will create an index with this name for searching memories
* **Content Field** : `content` - The raw memory content that will be embedded
* **Embedding Field** : `embedding` - The field that will store the resulting vector embedding
* **Metadata Fields** :
  * `memoryType`: TAG field for filtering by memory type (EPISODIC or SEMANTIC)
  * `metadata`: TEXT field for storing additional context about the memory
  * `userId`: TAG field for filtering by user ID
  * `createdAt`: TEXT field for storing the creation timestamp

### 4. Implement the Memory Service {#h3-8-4-implement-the-memory-service}

The MemoryService handles storing and retrieving memories from Redis:

```
@Service
class MemoryService(
    private val memoryVectorStore: RedisVectorStore
) {
    private val systemUserId = "system"

    fun storeMemory(
        content: String,
        memoryType: MemoryType,
        userId: String? = null,
        metadata: String = "{}"
    ): StoredMemory {
        // Check if a similar memory already exists to avoid duplicates
        if (similarMemoryExists(content, memoryType, userId)) {
            return StoredMemory(
                Memory(
                    content = content,
                    memoryType = memoryType,
                    userId = userId ?: systemUserId,
                    metadata = metadata,
                    createdAt = LocalDateTime.now()
                )
            )
        }

        // Create a document for the vector store
        val document = Document(
            content,
            mapOf(
                "memoryType" to memoryType.name,
                "metadata" to metadata,
                "userId" to (userId ?: systemUserId),
                "createdAt" to LocalDateTime.now().toString()
            )
        )

        // Store the document in the vector store
        memoryVectorStore.add(listOf(document))

        return StoredMemory(
            Memory(
                content = content,
                memoryType = memoryType,
                userId = userId ?: systemUserId,
                metadata = metadata,
                createdAt = LocalDateTime.now()
            )
        )
    }

    fun retrieveMemories(
        query: String,
        memoryType: MemoryType? = null,
        userId: String? = null,
        limit: Int = 5,
        distanceThreshold: Float = 0.9f
    ): List<StoredMemory> {
        // Build filter expression
        val b = FilterExpressionBuilder()
        val filterList = mutableListOf<FilterExpressionBuilder.Op>()

        // Add user filter
        val effectiveUserId = userId ?: systemUserId
        filterList.add(b.or(b.eq("userId", effectiveUserId), b.eq("userId", systemUserId)))

        // Add memory type filter if specified
        if (memoryType != null) {
            filterList.add(b.eq("memoryType", memoryType.name))
        }

        // Combine filters
        val filterExpression = when (filterList.size) {
            0 -> null
            1 -> filterList[0]
            else -> filterList.reduce { acc, expr -> b.and(acc, expr) }
        }?.build()

        // Execute search
        val searchResults = memoryVectorStore.similaritySearch(
            SearchRequest.builder()
                .query(query)
                .topK(limit)
                .filterExpression(filterExpression)
                .build()
        )

        // Transform results to StoredMemory objects
        return searchResults.mapNotNull { result ->
            if (distanceThreshold < (result.score ?: 1.0)) {
                val metadata = result.metadata
                val memoryObj = Memory(
                    id = result.id,
                    content = result.text ?: "",
                    memoryType = MemoryType.valueOf(metadata["memoryType"] as String? ?: MemoryType.SEMANTIC.name),
                    metadata = metadata["metadata"] as String? ?: "{}",
                    userId = metadata["userId"] as String? ?: systemUserId,
                    createdAt = try {
                        LocalDateTime.parse(metadata["createdAt"] as String?)
                    } catch (_: Exception) {
                        LocalDateTime.now()
                    }
                )
                StoredMemory(memoryObj, result.score)
            } else {
                null
            }
        }
    }
}
```


Key features of the memory service:

* Stores memories as vector embeddings in Redis
* Retrieves memories using vector similarity search
* Filters memories by user ID and memory type
* Prevents duplicate memories through similarity checking

### 5. Implement Spring AI Advisors {#647e}

We're going to rely on the Spring AI Advisors API. Advisors are a way to intercept, modify, and enhance AI-driven interactions.{#6ffe}

We will implement two advisors: one for retrieval and another for recorder. These advisors will be plugged in our `ChatClient` and intercept every interaction with the LLM.{#32fd}

The retrieval advisor runs before your LLM call. It takes the user's current message, performs a vector similarity search over Redis, and injects the most relevant memories into the system portion of the prompt so the model can ground its answer.{#4cc5}

### 5.1 Advisor for Long-term memory retrieval {#bfc5}

The retrieval advisor runs before LLM calls. It takes the user's current message, performs a vector similarity search over Redis, and injects the most relevant memories into the system portion of the prompt so the model can ground its answer.{#ee9b}

```kotlin
@Component
class LongTermMemoryRetrievalAdvisor(
  private val memoryService: MemoryService,
) : CallAdvisor, Ordered {

  companion object {
    const val USER_ID = "ltm_user_id"   
    const val TOP_K = "ltm_top_k"      
  }

  override fun getOrder() = Ordered.HIGHEST_PRECEDENCE + 40
  override fun getName() = "LongTermMemoryRetrievalAdvisor"

  override fun adviseCall(req: ChatClientRequest, chain: CallAdvisorChain): ChatClientResponse {
    val userId = (req.context()[USER_ID] as? String) ?: "system"
    val k = (req.context()[TOP_K] as? Int) ?: 5

    val query = req.prompt().userMessage.text
    val memories = memoryService.retrieveRelevantMemories(query, userId = userId)
      .take(k)

    val memoryBlock = buildString {
      appendLine("Use the MEMORY below if relevant. Keep answers factual and concise.")
      appendLine("----- MEMORY -----")
      memories.forEachIndexed { i, m -> appendLine("${i+1}. ${m.memory.content}") }
      appendLine("------------------")
    }

    val enrichedPrompt = req.prompt().augmentSystemMessage { sys ->
      val existing = sys.text
      sys.mutate()
        .text(
          buildString {
            appendLine(memoryBlock)
            if (existing.isNotBlank()) {
              appendLine()
              append(existing)
            }
          }
        ).build()
    }

    val enrichedReq = req.mutate()
      .prompt(enrichedPrompt)
      .build()

    return chain.nextCall(enrichedReq)
  }
}
```


### 5.2 Advisor for Long-term memory recording {#0151}

The recorder advisor runs after the assistant responds. It looks at the last user message and the assistant's reply, asks the model to extract atomic, useful facts (episodic or semantic), deduplicates them, and stores them in Redis.{#8206}

```kotlin
@Component
class LongTermMemoryRecorderAdvisor(
  private val memoryService: MemoryService,
  private val chatModel: ChatModel
) : CallAdvisor, Ordered {

  data class MemoryCandidate(val content: String, val type: MemoryType, val userId: String?)
  data class ExtractionResult(val memories: List<MemoryCandidate> = emptyList())

  private val extractorConverter = BeanOutputConverter(ExtractionResult::class.java)

  override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE + 60
  override fun getName(): String = "LongTermMemoryRecorderAdvisor"

  override fun adviseCall(req: ChatClientRequest, chain: CallAdvisorChain): ChatClientResponse {
    // 1) Proceed with the normal call (other advisors may have enriched the prompt)
    val res = chain.nextCall(req)

    // 2) Build extraction prompt (user + assistant text of *this* turn)
    val userText = req.prompt().userMessage.text
    val assistantText = res.chatResponse()?.result?.output?.text

    // 3) Ask the model to extract long-term memories as structured JSON
    val schemaHint = extractorConverter.jsonSchema // JSON schema string for the POJO
    val extractSystem = """
            You extract LONG-TERM MEMORIES from a dialogue turn.

            A memory is either:

            1. EPISODIC MEMORIES: Personal experiences and user-specific preferences
               Examples: "User prefers Delta airlines", "User visited Paris last year"

            2. SEMANTIC MEMORIES: General domain knowledge and facts
               Examples: "Singapore requires passport", "Tokyo has excellent public transit"

            Only extract clear, factual information. Do not make assumptions or infer information that isn't explicitly stated.
            If no memories can be extracted, return an empty array.

            The instance must conform to this JSON Schema (for validation, do not output it):
              $schemaHint

            Do not include code fences, schema, or properties. Output a single-line JSON object.
        """.trimIndent()

    val extractUser = """
            USER SAID:
            $userText

            ASSISTANT REPLIED:
            $assistantText

            Extract up to 5 memories with correct type; set userId if present/known.
        """.trimIndent()

    val options: ChatOptions = OpenAiChatOptions.builder()
      .responseFormat(ResponseFormat.builder().type(ResponseFormat.Type.JSON_OBJECT).build())
      .build()

    val extraction = chatModel.call(
      Prompt(
        listOf(
          UserMessage(extractUser),
          SystemMessage(extractSystem)
        ),
        options
      ),
    )

    val parsed = extractorConverter.convert(extraction.result.output.text ?: "")
      ?: ExtractionResult()

    // 4) Persist memories (MemoryService handles dedupe/thresholding)
    val userId = (req.context["ltm_user_id"] as? String) // optional per-call param
    parsed.memories.forEach { m ->
      val owner = m.userId ?: userId
      memoryService.storeMemory(
        content = m.content,
        memoryType = m.type,
        userId = owner
      )
    }

    return res
  }
}
```


### 6. Plugging the advisors in our ChatClient {#41a4}

In our `ChatConfig` class, we will configure our `ChatClient` as:{#0f6b}

```kotlin
@Bean
fun chatClient(
    chatModel: ChatModel,
    // chatMemory: ChatMemory, (Necessary for short-term memory)
    longTermRecorder: LongTermMemoryRecorderAdvisor,
    longTermMemoryRetrieval: LongTermMemoryRetrievalAdvisor
): ChatClient {
    return ChatClient.builder(chatModel)
        .defaultAdvisors(
            // MessageChatMemoryAdvisor.builder(chatMemory).build(),
            longTermRecorder,
            longTermMemoryRetrieval
        ).build()
}
```


### 7. Implement the Chat Service {#h3-13-7-implement-the-chat-service}

Since the advisors have been plugged in the `ChatClient` itself, we don't need to worry about managing memory ourselves when interacting with the LLM. The only thing we need to make sure is that with every interaction we send the expected parameters, namely the session or user ID, so that the advisors know which history to look at.

```kotlin
@Service
class ChatService(
    private val chatClient: ChatClient,
    private val shortTermMemoryRepository: ShortTermMemoryRepository,
    private val travelAgentSystemPrompt: Message,
    private val chatMemoryRepository: ChatMemoryRepository
) {
    private val log = LoggerFactory.getLogger(ChatService::class.java)

    fun sendMessage(
        message: String,
        userId: String,
    ): ChatResult {
        // Use userId as the key for conversation history and long-term memory
        log.info("Processing message from user $userId: $message")
        val response = chatClient
            .prompt(
                Prompt(
                    travelAgentSystemPrompt,
                    UserMessage(message)
                )
            )
            .advisors { it
                .param(ChatMemory.CONVERSATION_ID, userId)
                .param("ltm_user_id", userId)
            }
            .call()

        return ChatResult(
            response = response.chatResponse()!!
        )
    }

    fun getConversationHistory(userId: String): List<Message?> {
        return chatMemoryRepository.findByConversationId(userId)
    }

    fun clearConversationHistory(userId: String) {
        shortTermMemoryRepository.deleteById(userId)
        log.info("Cleared conversation history for user $userId from Redis")
    }
}
```


### 8. Configure the Agent System Prompt {#h3-14-8-configure-the-agent-system-prompt}

The agent is configured with a system prompt that explains its capabilities and access to different types of memory:

```
@Bean
fun travelAgentSystemPrompt(): Message {
    val promptText = """
        You are a travel assistant helping users plan their trips. You remember user preferences
        and provide personalized recommendations based on past interactions.

        You have access to the following types of memory:
        1. Short-term memory: The current conversation thread
        2. Long-term memory:
           - Episodic: User preferences and past trip experiences (e.g., "User prefers window seats")
           - Semantic: General knowledge about travel destinations and requirements

        Always be helpful, personal, and context-aware in your responses.

        Always answer in text format. No markdown or special formatting.
    """.trimIndent()

    return SystemMessage(promptText)
}
```


### 9. Create the REST Controller {#h3-15-9-create-the-rest-controller}

The REST controller exposes endpoints for chat and memory management:

```
@RestController
@RequestMapping("/api")
class ChatController(private val chatService: ChatService) {

    @PostMapping("/chat")
    fun chat(@RequestBody request: ChatRequest): ChatResponse {
        val result = chatService.sendMessage(request.message, request.userId)
        return ChatResponse(
            message = result.response.result.output.text ?: "",
            metrics = result.metrics
        )
    }

    @GetMapping("/history/{userId}")
    fun getHistory(@PathVariable userId: String): List<MessageDto> {
        return chatService.getConversationHistory(userId).map { message ->
            MessageDto(
                role = when (message) {
                    is SystemMessage -> "system"
                    is UserMessage -> "user"
                    is AssistantMessage -> "assistant"
                    else -> "unknown"
                },
                content = when (message) {
                    is SystemMessage -> message.content
                    is UserMessage -> message.content
                    is AssistantMessage -> message.content
                    else -> ""
                }
            )
        }
    }

    @DeleteMapping("/history/{userId}")
    fun clearHistory(@PathVariable userId: String) {
        chatService.clearConversationHistory(userId)
    }
}
```


Running the Demo {#h2-16-running-the-demo}
------------------------------------------

The easiest way to run the demo is with Docker Compose, which sets up all required services in one command.

### Step 1: Clone the repository {#h3-17-step-1-clone-the-repository}

```
git clone https://github.com/redis/redis-springboot-recipes.git
cd redis-springboot-recipes/artificial-intelligence/agent-long-term-memory-with-spring-ai
```


### Step 2: Configure your environment {#h3-18-step-2-configure-your-environment}

Create a `.env` file with your OpenAI API key:

```
OPENAI_API_KEY=sk-your-api-key
```


### Step 3: Start the services {#h3-19-step-3-start-the-services}

```
docker compose up --build
```


This will start:

* redis: for storing both vector embeddings and chat history
* redis-insight: a UI to explore the Redis data
* agent-memory-app: the Spring Boot app that implements the memory-aware AI agent

### Step 4: Use the application {#h3-20-step-4-use-the-application}

When all services are running, go to `localhost:8080` to access the demo. You'll see a travel assistant interface with a chat panel and a memory management sidebar:

![Screenshot of the Redis Agent Memory demo web interface. The interface is titled “Travel Agent with Redis Memory” and features two main panels: a “Memory Management” section on the left with tabs for Episodic and Semantic memories (currently showing “No episodic memories yet”), and a “Travel Assistant” chat on the right displaying a welcome message. At the top right, there’s a field to enter a user ID and buttons to start or clear the chat. The interface is clean and styled with Redis branding.](https://github.com/redis-developer/redis-springboot-recipes/blob/main/artificial-intelligence/agent-memory-with-spring-ai/readme-assets/1_demo_home.png?raw=true)

1. Enter a user ID and click "Start Chat":

![Close-up screenshot of the user ID input and chat controls. The label “User ID:” appears on the left with a text input field containing the value “raphael”. To the right are two red buttons labeled “Start Chat” and “Clear Chat”.](https://github.com/redis-developer/redis-springboot-recipes/raw/main/artificial-intelligence/agent-memory-with-spring-ai/readme-assets/2_userid_box.png)

2. Send a message like: "Hi, my name's Raphael. I went to Paris back in 2009 with my wife for our honeymoon and we had a lovely time. For our 10-year anniversary we're planning to go back. Help us plan the trip!"

![Animated screen recording of a user sending a message in the Redis Agent Memory demo. The user, identified as “raphael”, types a detailed message into the chat input box: “Hi, my name’s Raphael. I went to Paris back in 2009 with my wife for our honeymoon and we had a lovely time. For our 10-year anniversary we’re planning to go back. Help us plan the trip!” The cursor then clicks the red “Send” button, initiating the interaction with the AI travel assistant.](https://github.com/redis-developer/redis-springboot-recipes/raw/main/artificial-intelligence/agent-memory-with-spring-ai/readme-assets/3_sending_a_msg.gif)

The system will reply with the response to your message and, in case it identifies potential memories to be stored, they will be stored either as semantic or episodic memories. You can see the stored memories on the "Memory Management" sidebar.

On top of that, with each message, the system will also return performance metrics.

If you refresh the page, you will see that all memories and the chat history are gone.

If you reenter the same user ID, the long-term memories will be reloaded on the sidebar and the short-term memory (the chat history) will be reloaded as well:

![Animated screen recording of the Redis Agent Memory demo after sending a message. The sidebar under “Episodic Memories” now shows two stored entries: one noting that the user went to Paris in 2009 for their honeymoon, and another about planning a return for their 10-year anniversary. The chat assistant responds with a personalized message suggesting activities and asking follow-up questions. The browser page is then refreshed, clearing both the chat history and memory display. After re-entering the same user ID, the agent reloads the long-term memories in the sidebar and restores the conversation history, demonstrating persistent memory retrieval.](https://github.com/redis-developer/redis-springboot-recipes/raw/main/artificial-intelligence/agent-memory-with-spring-ai/readme-assets/4_refreshing_page.gif)

5. If you refresh the page and enter the same user ID, your memories and conversation history will be reloaded

![Animated screen recording of a cleared chat session in the Redis Agent Memory demo. The “Episodic Memories” panel still shows two past memories about a trip to Paris. In the chat panel, the message “Conversation cleared. How can I assist you today?” appears, indicating that the short-term memory has been reset. The user is about to start a new conversation. This demonstrates that although the short-term context is gone, the agent retains access to long-term memories, allowing it to respond with relevant information from past interactions.](https://github.com/redis-developer/redis-springboot-recipes/raw/main/artificial-intelligence/agent-memory-with-spring-ai/readme-assets/5_starting_new_chat.gif)

Exploring the Data in Redis Insight {#h2-21-exploring-the-data-in-redis-insight}
--------------------------------------------------------------------------------

RedisInsight provides a visual interface for exploring the data stored in Redis. Access it at `localhost:5540` to see:

1. Short-term memory (conversation history) stored in Redis Lists

![Screenshot of RedisInsight displaying the contents of the conversation:raphael key. The selected key is a Redis list representing a conversation history. On the right panel, the list shows four indexed elements: system prompts defining the assistant’s role and memory access, a user message asking “Where did I go back in 2009?”, and the assistant’s reply recalling a previous trip to Paris. Below this, several memory entries stored as JSON keys are also visible. This illustrates how short-term chat history is preserved in Redis and replayed per user session.](https://github.com/redis-developer/redis-springboot-recipes/raw/main/artificial-intelligence/agent-memory-with-spring-ai/readme-assets/7_redis_insight_short_term_memory.png)

2. Long-term memory (facts and experiences) stored as JSON documents with vector embeddings

![Screenshot of RedisInsight showing a semantic memory stored in Redis. The selected key is a JSON object with the name memory:04d04.... The right panel displays the memory’s fields: createdAt timestamp, empty metadata, memoryType set to “SEMANTIC”, an embedding vector (collapsed), userId set to “system”, and the memory content: “Paris is a beautiful city known for celebrating love”. This illustrates how general knowledge is stored as semantic memory in the AI agent.](https://github.com/redis-developer/redis-springboot-recipes/raw/main/artificial-intelligence/agent-memory-with-spring-ai/readme-assets/8_redis_insight_long_term_memory.png)

3. The vector index schema used for similarity search

If you run the `FT.INFO memoryIdx` command in the RedisInsight workbench, you'll see the details of the vector index schema that enables efficient memory retrieval.

![Screenshot of RedisInsight Workbench showing the schema details of the memoryIdx vector index. The result of the FT.INFO memoryIdx command displays an index on JSON documents prefixed with memory:. The schema includes: • $.content as a TEXT field named content • $.embedding as a VECTOR field using HNSW with 384-dimension FLOAT32 vectors and COSINE distance • $.memoryType and $.userId as TAG fields • $.metadata and $.createdAt as TEXT fields This shows how memory data is structured and searchable in Redis using RediSearch vector similarity.](https://github.com/redis-developer/redis-springboot-recipes/raw/main/artificial-intelligence/agent-memory-with-spring-ai/readme-assets/redis_insight_index_details.png)

Wrapping up {#h2-22-wrapping-up}
--------------------------------

And that's it --- you now have a working AI agent with memory using Spring Boot and Redis.

Instead of forgetting everything between conversations, your agent can now remember user preferences, past experiences, and important facts. Redis handles both short-term memory (conversation history) and long-term memory (vector embeddings) --- all with the performance and scalability Redis is known for.

With Spring AI and Redis, you get an easy way to integrate this into your Java applications. The combination of vector similarity search for semantic retrieval and traditional data structures for conversation history gives you a powerful foundation for building truly intelligent agents.

Whether you're building customer service bots, personal assistants, or domain-specific experts, this memory architecture gives you the tools to create more helpful, personalized, and context-aware AI experiences.

Try it out, experiment with different memory types, explore other embedding models, and see how far you can push the boundaries of AI agent capabilities!

Stay Curious!
