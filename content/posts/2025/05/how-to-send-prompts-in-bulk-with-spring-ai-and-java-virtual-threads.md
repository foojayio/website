---
title: "How to send prompts in bulk with Spring AI and Virtual Threads"
slug: "how-to-send-prompts-in-bulk-with-spring-ai-and-java-virtual-threads"
date: "2025-05-21T09:18:45+00:00"
lastmod: "2025-05-21T09:23:24+00:00"
description: "Learn how to use Spring AI and Java Virtual Threads to send hundreds of prompts to OpenAI in parallel."
authors:
  - "raphael-delio"
image: "/images/posts/2025/05/how-to-send-prompts-in-bulk-with-spring-ai-and-java-virtual-threads/Screenshot-2025-05-13-at-11.13.22.png"
categories:
  - "Java"
  - "JDK21"
  - "Spring"
  - "Tutorials"
tags:
related_posts:
  - "back-to-basics-accessing-kubernetes-pods"
  - "building-autopo-an-ai-powered-open-source-application-to-manage-po-files"
  - "different-approaches-to-building-stateful-microservices-in-the-cloud-native-world"
  - "even-more-opentelemetry"
enlighterjs: true
frozen: false
---

> TL;DR: You're building an AI-powered app that needs to send lots of prompts to OpenAI.
>
> Instead of sending them one by one, you want to do it in bulk --- efficiently and safely.
>
> This is how you can use Spring AI with Java Virtual Threads to process hundreds of prompts in parallel.

When calling LLM APIs like OpenAI, you're dealing with a high-latency, network-bound task. Normally, doing that in a loop slows you down and blocks threads. But with Spring AI and Java 21 Virtual Threads, you can fire off hundreds of requests in parallel without killing your app.

This is particularly useful when you want the LLM to perform actions such as summarizing or extracting information from lots of documents.

Here's the flow: {#h2-0-here-s-the-flow}
----------------------------------------

1. Get your list of text inputs.
2. Filter the ones that need processing.
3. Split them into batches.
4. For each batch:  
   --- Use Virtual Threads to make OpenAI calls in parallel  
   --- Wait for all calls to finish (using CompletableFuture)  
   --- Save the results

Virtual Threads for Massive Parallelism {#h2-1-virtual-threads-for-massive-parallelism}
---------------------------------------------------------------------------------------

Java Virtual Threads are perfect for this. They're lightweight, run on the JVM, and don't block OS threads. Ideal for I/O-heavy operations like talking to APIs.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()</pre>

Each OpenAI request runs in its own thread, but without the overhead of real threads.

Spring AI Prompt Call {#h2-2-spring-ai-prompt-call}
---------------------------------------------------

You create a Prompt, then send it to the model:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">ChatResponse response = chatModel.call(
  new Prompt(List.of(
    new SystemMessage(“You are a helpful assistant…”),
    new UserMessage(userInput)
  ))
);</pre>

You get back a structured response. From there, you just extract the output:

<pre class="EnlighterJSRAW" data-enlighter-language="generic">String summary = response.getResult().getOutput().getText();</pre>

Processing in Batches {#h2-3-processing-in-batches}
---------------------------------------------------

Sending all prompts at once isn't a good idea (rate limits, reliability, memory). Instead, chunk them into smaller batches (e.g., 300 items):

<pre class="EnlighterJSRAW" data-enlighter-language="generic">int batchSize = 300;
int totalBatches = (inputs.size() + batchSize — 1) / batchSize;</pre>

For each batch:

* Launch a CompletableFuture for every input
* Wait for all with CompletableFuture.allOf(...).join()
* Collect the results

Handling Errors Gracefully {#h2-4-handling-errors-gracefully}
-------------------------------------------------------------

Each task is wrapped in a try/catch block. So if one OpenAI call fails, it doesn't crash the batch. You just skip that result.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">.map(input -&gt; CompletableFuture.supplyAsync(() -&gt; {
  try {
    ChatResponse r = chatModel.call(…);
    return r.getResult().getOutput().getText();
  } catch (Exception e) {
    return null;
  }
}))</pre>

Process Results in Bulk {#h2-5-process-results-in-bulk}
-------------------------------------------------------

After processing each batch:

* Filter out the failed ones
* Process the valid results

<pre class="EnlighterJSRAW" data-enlighter-language="generic">List processed = futures.stream()
.map(CompletableFuture::join)
.filter(Objects::nonNull)
.toList();

</pre>

Full Implementation {#h2-6-full-implementation}
-----------------------------------------------

In this example, we get a list of text, and send them to OpenAI in batches to get a summary. We do that in parallel, which makes the process much faster. After getting the summaries, we saves the results. Everything runs in a way that handles errors and avoids overloading the system.

<pre class="EnlighterJSRAW" data-enlighter-language="generic">@Service
public class BulkSummarizationService {

    private static final Logger logger = LoggerFactory.getLogger(BulkSummarizationService.class);
    private final ChatClient chatClient;
    private final TextRepository textRepository;

    public BulkSummarizationService(ChatClient chatClient, TextRepository textRepository) {
        this.chatClient = chatClient;
        this.textRepository = textRepository;
    }

    public void summarizeTexts(boolean overwrite) {
        logger.info("Starting bulk summarization");
        List textsToSummarize = textRepository.findAll();
        logger.info("Found {} texts to summarize", textsToSummarize.size());

        if (textsToSummarize.isEmpty()) return;

        int batchSize = 300;
        int totalBatches = (textsToSummarize.size() + batchSize - 1) / batchSize;

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i &lt; totalBatches; i++) {
                int start = i * batchSize;
                int end = Math.min(start + batchSize, textsToSummarize.size());
                List batch = textsToSummarize.subList(start, end);

                logger.info("Processing batch {} of {} ({} items)", i + 1, totalBatches, batch.size());

                List&lt;CompletableFuture&gt; futures = batch.stream()
                        .map(text -&gt; CompletableFuture.supplyAsync(() -&gt; {
                            try {
                                ChatResponse response = chatClient.call(
                                        new Prompt(List.of(
                                                new SystemMessage("""
                                                    You are a helpful assistant that summarizes long pieces of text.
                                                    Focus on keeping the summary dense and informative.
                                                    Limit to 512 words.
                                                """),
                                                new UserMessage(text.getContent())
                                        ))
                                );
                                text.setSummary(response.getResult().getOutput().getText());
                                return text;
                            } catch (Exception e) {
                                logger.error("Failed to summarize text with ID: {}", text.getId(), e);
                                return null;
                            }
                        }, executor))
                        .toList();

                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

                List summarized = futures.stream()
                        .map(CompletableFuture::join)
                        .filter(Objects::nonNull)
                        .toList();

                if (!summarized.isEmpty()) {
                    textRepository.saveAll(summarized);
                    logger.info("Saved {} summaries", summarized.size());
                }
            }
        }

        logger.info("Bulk summarization complete");
    }
}</pre>

And that's it! You now have a fully async, high-throughput pipeline that can send hundreds of prompts to OpenAI --- safely and efficiently --- using nothing but Spring AI, Java Virtual Threads, and good batching.

Stay curious! {#h2-7-stay-curious}
----------------------------------
