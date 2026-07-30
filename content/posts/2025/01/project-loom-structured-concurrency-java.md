---
title: "Project Loom: Structured Concurrency - Java"
slug: "project-loom-structured-concurrency-java"
date: "2025-01-29T12:12:38+00:00"
lastmod: "2025-01-29T12:12:40+00:00"
description: "Learn about structured concurrency, a paradigm that simplifies concurrent programming by bringing order and predictability to task management."
authors:
  - "mahendra1413"
image: "https://foojay.io/wp-content/uploads/2025/01/sc.png"
categories:
  - "Java"
  - "Java Core"
  - "JDK 23"
  - "JDK21"
  - "Tutorials"
tags:
related_posts:
  - "foojay-podcast-64"
  - "loom-is-just-hyperthreading-in-java"
  - "what-the-heck-is-project-loom-for-java"
enlighterjs: true
frozen: false
---

In today's era of cloud computing, where high-performance infrastructure is readily available, developers face a complex challenge in achieving efficient concurrency.

While modern technological advancements have brought us closer to solving these challenges, we must adopt a thoughtful, incremental approach to concurrency.

This principle holds especially true as we strive to design scalable, reliable, and easy-to-maintain systems.

One such advancement in this domain is *structured concurrency*, a paradigm that simplifies concurrent programming by bringing order and predictability to task management.

The structured concurrency feature was first introduced as an incubator in [JEP-428](https://openjdk.org/jeps/428 "JEP - 428") with the release of **Java 19,** later evolved into a preview feature in [JEP-453](https://openjdk.org/jeps/453 "JEP-453"), part of the **Java 21** release, and as an another preview in [JEP-480](https://openjdk.org/jeps/480) part of the Java 23 release.

<img fetchpriority="high" decoding="async" class="size-medium wp-image-115435" src="/images/posts/2025/01/project-loom-structured-concurrency-java/SC-700x394.jpg" alt="Structured Concurrency" width="700" height="394">

<br />

To effectively explore structured concurrency, one must grasp several fundamental concepts, including:

* **Concurrency:** This refers to a system that can handle multiple tasks at the same time, where tasks may not operate in parallel but can progress independently.

* **Threads:** These are the smallest units of execution within a process. Threads operate within the same memory space and can execute concurrently.

* **Processes:** Threads are the smallest units of execution within a process. They operate within the same memory space and can execute concurrently.

* **Processes vs Threads:** **Processes** differ from one another and each has its own separate memory space, which makes them more demanding in terms of resources. Conversely, **threads** reside within processes and share the same memory space, which facilitates more efficient communication, though they require careful oversight to prevent issues such as race conditions.

* **Synchronization:** The method of managing access to shared resources within a multi-threaded environment refers to maintaining data integrity and avoiding race conditions. This process ensures that only a single thread interacts with a critical section of code or a shared resource at any given moment.

* **Deadlock:** In concurrent programming, a deadlock happens when two or more threads cannot continue execution because each thread waits for resources held by another. For instance, if Thread A secures Resource 1 and then waits for Resource 2, while Thread B secures Resource 2 and waits for Resource 1, both threads become inoperative and cannot make any progress.

* **Semaphores:** Synchronization tools called semaphores manage access to shared resources among multiple threads. Two primary types exist:

  1. **Binary Semaphore:** Functions like a lock, permitting only one thread to access a resource at any given time.
  2. **Counting Semaphore:** Allows a predetermined number of threads to access a resource simultaneously.

With those essential concepts as our foundation, let us explore *Structured Concurrency* in greater detail. So,

### **What is Structured Concurrency?** {#h3-0-what-is-structured-concurrency}

Succinctly, a method for concurrent programming that

* Streamlines scenarios where a primary task divides into multiple subtasks
* Maintains the inherent relationships between tasks and their subtasks
* Minimizes errors that developers associate with cancellation and shutdown processes
* JDK 21 introduces the concept of [Virtual Threads](https://docs.oracle.com/en/java/javase/21/core/virtual-threads.html "Virtual Threads"), which is well-suited for.

In a nutshell, *Structured Concurrency occurs when a primary task divides into multiple simultaneous subtasks, and the primary task cannot proceed until those subtasks are finished.*

### How Structured Concurrency Works? {#h3-1-how-structured-concurrency-works}

The structured concurrency API includes the primary class *StructuredTaskScope* , which resides in the `java.util.concurrent` package.

<img decoding="async" class="size-medium wp-image-115436" src="/images/posts/2025/01/project-loom-structured-concurrency-java/SC1-700x394.jpg" alt="StructuredTaskScope" width="700" height="394">

<br />

**Overview of the StructuredTaskScope Class Utilization**

* Begin by establishing a `StructuredTaskScope` utilizing a `try-with-resources` statement.
* Create your subtasks as instances of the `StructuredTaskScope.Subtask` class.
* Within the try block, initiate each subtask in a separate thread using `StructuredTaskScope::fork`.
* Subsequently, invoke `StructuredTaskScope::join`.
* Manage the final results of all subtasks.
* Confirm that the StructuredTaskScope is properly `shutdown`.

The `try-with-resources` statement establishes the `StructuredScope`, which ensures that it automatically releases resources, allowing the task scope to pause until all threads executing any pending subtasks complete.

The StructuredScope class includes a shutdown method that terminates a task scope while keeping it open. This method effectively cancels any ongoing subtasks by interrupting their respective threads. Additionally, two subclasses of StructuredTaskScope facilitate a policy that allows for the non-completion of all subtasks:

* **StructuredTaskScope.ShutdownOnSuccess:** If any single subtask is completed successfully, it cancels all other subtasks.
* **StructuredTaskScope.ShutdownOnFailure:** If any single subtask fails, it will result in the cancellation of all subtasks.

Let's put all the above threotical concept into one example

<pre class="EnlighterJSRAW" data-enlighter-language="generic">package com.bsmlabs.java21examples;

import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public class StructuredConcurrencyDemo {

    public static void main(String[] args) {
        try {
            System.out.println("Executing processShutdownOnSuccessTasks...");
            new StructuredConcurrencyDemo().processShutdownOnSuccessTasks();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }

        try {
            System.out.println("Executing processShutdownOnFailureTasks...");
            new StructuredConcurrencyDemo().processShutdownOnFailureTasks();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }
    }

    public void processShutdownOnSuccessTasks() throws InterruptedException, ExecutionException {
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess&lt;Integer&gt;()) {

            // Fork multiple subtasks
            StructuredTaskScope.Subtask&lt;Integer&gt; taskOne = scope.fork(() -&gt; performTask("Task One", 2));
            StructuredTaskScope.Subtask&lt;Integer&gt; taskTwo = scope.fork(() -&gt; performTask("Task Two", 3));
            StructuredTaskScope.Subtask&lt;Integer&gt; taskThree = scope.fork(() -&gt; performTask("Task Three", 1));

            // Wait for the first successful result
            int result = scope.join().result();

            // Print the result
            System.out.println("First successful result: " + result);
        }
    }

    public void processShutdownOnFailureTasks() throws InterruptedException, ExecutionException {
          try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            // Submit tasks within the scope
            StructuredTaskScope.Subtask&lt;Integer&gt; taskOne = scope.fork(() -&gt; performTask("Task One", 2));
            StructuredTaskScope.Subtask&lt;Integer&gt; taskTwo = scope.fork(() -&gt; performTask("Task Two", 3));
            StructuredTaskScope.Subtask&lt;Integer&gt; taskThree = scope.fork(() -&gt; performTask("Task Three", 4));

            // Wait for all tasks to complete
            scope.join();  // Wait for both tasks
            scope.throwIfFailed(); // Propagate exceptions if any task failed

            // Retrieve results
            int result1 = taskOne.get();
            int result2 = taskTwo.get();
            int result3 = taskThree.get();

            System.out.println("Results: Task 1 = " + result1 + ", Task 2 = " + result2+ ", Task 3 = " + result3);
        }
        // The scope ensures that tasks are cleaned up and resources are released
        System.out.println("All tasks completed successfully.");
    }

    private Integer performTask(String taskName, int durationInSeconds) throws InterruptedException {
        System.out.println(StringTemplate.STR."\{taskName} started.");
        Thread.sleep(Duration.ofSeconds(durationInSeconds).toMillis());
        System.out.println(StringTemplate.STR."\{taskName} completed.");
        return durationInSeconds * 10; // Return some result
    }
}

</pre>

<pre class="EnlighterJSRAW" data-enlighter-language="generic">Executing processShutdownOnSuccessTasks...
Task Three started.
Task One started.
Task Two started.
Task Three completed.
First successful result: 10
Executing processShutdownOnFailureTasks...
Task One started.
Task Two started.
Task Three started.
Task One completed.
Task Two completed.
Task Three completed.
Results: Task 1 = 20, Task 2 = 30, Task 3 = 40
All tasks completed successfully.</pre>

* `ShutdownOnSuccess` **focuses on the first successful result**.
* `ShutdownOnFailure` ensures that **all tasks are complete unless one fails**.

We can also create custom StructuredTaskScope policies that handle `ShutdownOnSuccess` `and` `ShutdownOnFailure` differently. We can do this by extending `StructuredTaskScope` class.

We can debug StructuredTaskScope and its forked tasks using the `jcmd` command

**jcmd**

* A tool in the JDK that allows you to work with Java processes through the command line.
* Can create a thread dump in JSON format, simplifying the analysis of program behavior.

<pre class="EnlighterJSRAW" data-enlighter-language="generic" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">jcmd &lt;PID&gt; Thread.print -format=json
</pre>

Steps to follow

* Run `SturcuturedTaskScope` Java application
* Use this command. `jcmd <PID> Thread.print -format=json` Replace `<PID>` with the **process ID** of the Java application.
* Analyze the Output: Look for threads related to `StructuredTaskScope` and their stack traces.

### Benefits of Strutured Concurrency {#h3-2-benefits-of-strutured-concurrency}

* **Simplicity:** Users can easily understand limits and manage tasks running at the same time.
* **Error Handling:**The system handles and reports failures in a uniform way.
* **Resource Safety:** The system automatically cleans up tasks and their resources when the process finishes.
* **Predictability:** The connection between parent and child tasks ensures that no task continues running in the background.

Conclusion {#h2-3-conclusion}
-----------------------------

* **Simplified Task Management:** Structured concurrency organizes tasks in a clear hierarchy, ensuring that all smaller tasks finish or stop when the main task is done.

<!-- -->

* **Improved Resource Handling:** The try-with-resources feature helps clean up threads properly, avoiding resource leaks. Enhanced Reliability: By managing task execution and errors, it reduces issues like deadlocks and unhandled exceptions.

<!-- -->

* **Better Debugging:** Tools such as `jcmd` allow easy monitoring and debugging of tasks with clear stack traces.

<!-- -->

* **Future-Ready**: Structured concurrency is a preview feature in Java 21 and Java 23, setting the stage for creating modern, maintainable, and efficient concurrent applications.

*Structured concurrency* encourages clean, predictable, and safer patterns in concurrency, marking an important step forward for Java developers.

References {#h2-4-references}
-----------------------------

* <https://openjdk.org/jeps/428>
* <https://openjdk.org/jeps/453>
* <https://openjdk.org/jeps/480>
* <https://docs.oracle.com/en/java/javase/21/core/structured-concurrency.html>
* <https://docs.oracle.com/en/java/javase/23/core/structured-concurrency.html>
* <https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/StructuredTaskScope.html>

Happy Reading and Learning!

<br />
