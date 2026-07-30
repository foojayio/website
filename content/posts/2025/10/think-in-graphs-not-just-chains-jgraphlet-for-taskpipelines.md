---
title: "Think in Graphs, Not Just Chains: JGraphlet for TaskPipelines"
slug: "think-in-graphs-not-just-chains-jgraphlet-for-taskpipelines"
date: "2025-10-02T13:24:28+00:00"
lastmod: "2025-10-02T13:24:29+00:00"
description: "JGraphlet is a tiny, zero-dependency library for building task pipelines in Java. Its power comes not from a long list of features, but from a small set of core design principles that work together in harmony."
canonical: "https://shaaf.dev/post/2025-08-25-think-in-graphs-not-just-chains-jgraphlet-for-taskpipelines/"
authors:
  - "shaaf"
image: "https://foojay.io/wp-content/uploads/2025/08/jgraphlets.jpg"
categories:
  - "Java"
  - "Library"
tags:
related_posts:
enlighterjs: true
frozen: false
---

****JGraphlet is a tiny, zero-dependency Java library for building task pipelines. It uses a graph model where you define tasks as nodes and connect them to create simple or complex workflows (like fan-in/fan-out). It supports both asynchronous (default) and synchronous tasks, has a simple API, allows data sharing via a PipelineContext, and offers optional caching to avoid re-computing results.****
**Its power comes not from a long list of features, but from a small set of core design principles that work together in harmony.
At the heart of JGraphlet is simplicity, backed by a Graph. Add Tasks to a pipeline and connect them to create your graph. Each `Task` has an input and output. A `TaskPipeline` builds and executes a pipeline while managing the I/O for each Task.
For example, a `Map` for Fan-in, a `Record` for your own data model, etc. A Task pipeline also has a way `PipelineContext` to share data between Tasks, and Tasks can also be cached, so the computation doesn't need to take place again and again.
You can choose how your Task pipeline flow should be, and you can decide whether it should be synchronous `SyncTask` or asynchronous. By default, all Tasks are asynchronous.
Let's dive into the eight core principles that define JGraphlet. {#h2-0-let-s-dive-into-the-eight-core-principles-that-define-jgraphlet}
----------------------------------------------------------------------------------------------------------------------------------------
### 1. A Graph-First Execution Model {#h3-1-1-a-graph-first-execution-model}
JGraphlet treats your workflow as a Directed Acyclic Graph (DAG). You define tasks as nodes and explicitly draw the connections (edges) between them. This makes complex patterns like fan-out (one task feeding many) and fan-in (many tasks feeding one) natural.
**Example:**

<pre class="EnlighterJSRAW" data-enlighter-language="java">import dev.shaaf.jgraphlet.*;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

try (TaskPipeline pipeline = new TaskPipeline()) {
    Task&lt;String, String&gt; fetchInfo = (id, ctx) -&gt; CompletableFuture.supplyAsync(() -&gt; "Info for " + id);
    Task&lt;String, String&gt; fetchFeed = (id, ctx) -&gt; CompletableFuture.supplyAsync(() -&gt; "Feed for " + id);
    Task&lt;Map&lt;String, Object&gt;, String&gt; combine = (inputs, ctx) -&gt; CompletableFuture.supplyAsync(() -&gt;
        inputs.get("infoNode") + " | " + inputs.get("feedNode")
    );

    pipeline.addTask("infoNode", fetchInfo)
            .addTask("feedNode", fetchFeed)
            .addTask("summaryNode", combine);

    pipeline.connect("infoNode", "summaryNode")
            .connect("feedNode", "summaryNode");

    String result = (String) pipeline.run("user123").join();
    System.out.println(result); // "Info for user123 | Feed for user123"
}</pre>

```
```
### 2. Two Task Styles: Task\<I\> and SyncTask\<I\> {#h3-2-2-two-task-styles-task-i-and-synctask-i}
JGraphlet provides two distinct task types you can mix and match:
* **Task*(Async):*** Returns a `CompletableFuture`. Perfect for I/O operations or heavy computations.
* **SyncTask*(Sync):*** Returns a direct *O* - output. Ideal for fast, CPU-bound operations.
**Example:**

<pre class="EnlighterJSRAW" data-enlighter-language="java">try (TaskPipeline pipeline = new TaskPipeline()) {
    Task&lt;String, String&gt; fetchName = (userId, ctx) -&gt;
        CompletableFuture.supplyAsync(() -&gt; "John Doe");

    SyncTask&lt;String, String&gt; toUpper = (name, ctx) -&gt; name.toUpperCase();

    pipeline.add("fetch", fetchName)
            .then("transform", toUpper);

    String result = (String) pipeline.run("user-42").join();
    System.out.println(result); // "JOHN DOE"
}</pre>

*** ** * ** ***
### 3. A Simple, Explicit API {#h3-3-3-a-simple-explicit-api}
JGraphlet avoids complex builders or magic configurations. The API is lean and explicit:
1. Create a pipeline: `new TaskPipeline()`
2. Register nodes: `addTask("uniqueId", task)`
3. Wire them up: `connect("fromId", "toId")`
**Example:**

<pre class="EnlighterJSRAW" data-enlighter-language="java">try (TaskPipeline pipeline = new TaskPipeline()) {
    SyncTask&lt;String, Integer&gt; lengthTask = (s, c) -&gt; s.length();
    SyncTask&lt;Integer, String&gt; formatTask = (i, c) -&gt; "Length is " + i;

    pipeline.addTask("calculateLength", lengthTask);
    pipeline.addTask("formatOutput", formatTask);

    pipeline.connect("calculateLength", "formatOutput");

    String result = (String) pipeline.run("Hello").join();
    System.out.println(result); // "Length is 5"
}</pre>

*** ** * ** ***
### 4. A Clear Fan-In Input Shape {#h3-4-4-a-clear-fan-in-input-shape}
A fan-in task receives a `Map`, where keys are parent task IDs and values are their results.
**Example:**

<pre class="EnlighterJSRAW" data-enlighter-language="java">try (TaskPipeline pipeline = new TaskPipeline()) {
    SyncTask&lt;String, String&gt; fetchUser = (id, ctx) -&gt; "User: " + id;
    SyncTask&lt;String, String&gt; fetchPerms = (id, ctx) -&gt; "Role: admin";

    Task&lt;Map&lt;String, Object&gt;, String&gt; combine = (inputs, ctx) -&gt; CompletableFuture.supplyAsync(() -&gt; {
        String userData = (String) inputs.get("userNode");
        String permsData = (String) inputs.get("permsNode");
        return userData + " (" + permsData + ")";
    });

    pipeline.addTask("userNode", fetchUser)
            .addTask("permsNode", fetchPerms)
            .addTask("combiner", combine);

    pipeline.connect("userNode", "combiner").connect("permsNode", "combiner");

    String result = (String) pipeline.run("user-1").join();
    System.out.println(result); // "User: user-1 (Role: admin)"
}</pre>

*** ** * ** ***
### 5. A Clear Run Contract {#h3-5-5-a-clear-run-contract}
Executing a pipeline is straightforward: `pipeline.run(input)` returns a `CompletableFuture` for the final result. You can block with `.join()` or use async chaining.
**Example:**

<pre class="EnlighterJSRAW" data-enlighter-language="java">String input = "my-data";

// Blocking approach
try {
    String result = (String) pipeline.run(input).join();
    System.out.println("Result (blocking): " + result);
} catch (Exception e) {
    System.err.println("Pipeline failed: " + e.getMessage());
}

// Non-blocking approach
pipeline.run(input)
        .thenAccept(result -&gt; System.out.println("Result (non-blocking): " + result))
        .exceptionally(ex -&gt; {
            System.err.println("Async pipeline failed: " + ex.getMessage());
            return null;
        });</pre>

*** ** * ** ***
### 6. A Built-in Resource Lifecycle {#h3-6-6-a-built-in-resource-lifecycle}
JGraphlet implements `AutoCloseable`. Use try-with-resources to guarantee safe shutdown of internal resources.
**Example:**

<pre class="EnlighterJSRAW" data-enlighter-language="java">try (TaskPipeline pipeline = new TaskPipeline()) {
    pipeline.add("taskA", new SyncTask&lt;String, String&gt;() {
        @Override
        public String executeSync(String input, PipelineContext context) {
            if (input == null) {
                throw new IllegalArgumentException("Input cannot be null");
            }
            return "Processed: " + input;
        }
    });

    pipeline.run("data").join();

} // pipeline.shutdown() is called automatically
System.out.println("Pipeline resources have been released.");</pre>

*** ** * ** ***
### 7. Context {#h3-7-7-context}
`PipelineContext` is a thread-safe, per-run workspace for metadata.
**Example:**

<pre class="EnlighterJSRAW" data-enlighter-language="java">SyncTask&lt;String, String&gt; taskA = (input, ctx) -&gt; {
    ctx.put("requestID", "xyz-123");
    return input;
};
SyncTask&lt;String, String&gt; taskB = (input, ctx) -&gt; {
    String reqId = ctx.get("requestID", String.class).orElse("unknown");
    return "Processed input " + input + " for request: " + reqId;
};</pre>

*** ** * ** ***
### 8. Optional Caching {#h3-8-8-optional-caching}
Tasks can opt into caching to prevent re-computation.
**Example:**

<pre class="EnlighterJSRAW" data-enlighter-language="java">Task&lt;String, String&gt; expensiveApiCall = new Task&lt;&gt;() {
    @Override
    public CompletableFuture&lt;String&gt; execute(String input, PipelineContext context) {
        System.out.println("Performing expensive call for: " + input);
        return CompletableFuture.completedFuture("Data for " + input);
    }
    @Override
    public boolean isCacheable() { return true; }
};

try (TaskPipeline pipeline = new TaskPipeline()) {
    pipeline.add("expensive", expensiveApiCall);

    System.out.println("First call...");
    pipeline.run("same-key").join();

    System.out.println("Second call...");
    pipeline.run("same-key").join(); // Result is from cache
}</pre>

*** ** * ** ***
The result is a clean, testable way to orchestrate synchronous or asynchronous tasks for composing complex flows, such as parallel retrieval, merging, judging, and guardrails---without requiring a heavyweight workflow engine.
To learn more or try it out:
* [Maven central](https://mvnrepository.com/artifact/dev.shaaf.jgraphlet/jgraphlet)
* [Github repo](https://github.com/sshaaf/jgraphlet)**
