---
title: "Web Crawling in Java: Classical Threads and Virtual Threads"
slug: "web-crawling-in-java-a-tale-of-classical-threads-and-virtual-threads"
date: "2023-09-29T12:23:48+00:00"
lastmod: "2023-09-29T12:23:49+00:00"
description: "Explore the intriguing world of web crawling in Java through a comparative lens: classical threads vs. virtual threads."
authors:
  - "bazlur-rahman"
image: "83ea75d7-d979-46cc-a848-8cc082f8aba1.jpeg"
categories:
  - "Java Core"
  - "Performance"
tags:
related_posts:
  - "3-ways-to-refactor-your-code-in-intellij-idea"
  - "42-practical-java-design-patterns-builder-and-more"
  - "5-great-reasons-to-use-jooq"
  - "virtual-thread-pinning-field-guide"
enlighterjs: true
frozen: false
---

I**n today's fast-paced digital world, web crawling is a cornerstone technology behind search engines, data analysis tools, and various other applications.**

**Java, a language known for its robustness and scalability, offers intriguing ways to implement web crawling.**

**Yet, the thread model you choose can make a world of difference.**

**This article unfolds a compelling narrative around web crawling in Java, contrasting classical threads with their newer counterpart: virtual threads.**

## **Environment Setup**

Before diving into the code and its intricate comparisons, ensure you have JDK 21 installed on your system. If you're using a Unix-based system, SDKMAN! It makes this simple. Just open your terminal and run `curl -s "https://get.sdkman.io" | bash` to install SDKMAN!, followed by `source "$HOME/.sdkman/bin/sdkman-init.sh"` to initialize it. Finally, install JDK 21 with `sdk install java 21.0.0-<vendor>`, replacing \<vendor\> with your preferred vendor (e.g., zulu, adopt).

Now that your environment is ready let's move forward with our captivating tale of web crawling in Java.

By having JDK 21 at your disposal, you are now well-equipped to delve into the fascinating juxtaposition of classical and virtual threads in Java's web-crawling landscape.  

## **The Code Overview**

The Java code in focus here serves as a skeletal web crawler, a program that starts at an initial URL and recursively follows links to gather data from various web pages.

It leverages both classical threads, using a fixed thread pool, and virtual threads.

The objective is to examine how each threading model impacts the performance of the web crawler.

```java
package ca.bazlur;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler implements Runnable {
    public static final ReentrantLock QUEUE_LOCK = new ReentrantLock();
    public static final Condition QUEUE_NOT_EMPTY = QUEUE_LOCK.newCondition();
    private static final int MAX_PAGES_TO_SEARCH = 100;
    private final AtomicInteger count;
    private final ConcurrentMap<String, Boolean> visited;
    private final String url;
    private final Queue<String> pageQueue;

    public Crawler(AtomicInteger count, ConcurrentMap<String, Boolean> visited, String url,
                   Queue<String> pageQueue) {
        this.count = count;
        this.visited = visited;
        this.url = url;
        this.pageQueue = pageQueue;
    }

    @Override
    public void run() {
        while (!visited.containsKey(url) && count.get() < MAX_PAGES_TO_SEARCH) {
            try {
                var connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("HEAD");
                connection.connect();
                String contentType = connection.getContentType();

                if (contentType != null && (contentType.startsWith("text/") || contentType.contains("xml"))) {
                    Document document = Jsoup.connect(url).get();
                    Elements linksOnPage = document.select("a[href]");
                    visited.put(url, true);
                    count.incrementAndGet();

                    for (Element link : linksOnPage) {
                        String nextUrl = link.attr("abs:href");
                        if (nextUrl.startsWith("http") && !visited.containsKey(nextUrl)) {
                            pageQueue.add(nextUrl);
                        }
                    }
                    signalQueue();
                }
            } catch (IOException e) {
                System.err.printf("Error occurred while accessing URL '%s', the error: %s\n", url, e.getMessage());
            }
        }
    }

    private static void signalQueue() {
        QUEUE_LOCK.lock();
        try {
            QUEUE_NOT_EMPTY.signalAll();
        } finally {
            QUEUE_LOCK.unlock();
        }
    }

    //Classical Threads
    //Pages crawled: 101
    //Execution time: 20910ms
    //Throughput: 4.8302247728359635 pages/sec

    //Virtual threads
    //Pages crawled: 785
    //Execution time: 29323ms
    //Throughput: 26.77079425706783 pages/sec

    public static void main(String[] args) {
        final ConcurrentMap<String, Boolean> visited = new ConcurrentHashMap<>();
        final Queue<String> pageQueue = new LinkedBlockingDeque<>();
        pageQueue.add("https://foojay.io/");

        long startTime = System.currentTimeMillis();
        AtomicInteger pageCount = new AtomicInteger();

 //       try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
       try (var executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
            while (pageCount.get() < MAX_PAGES_TO_SEARCH) {
                QUEUE_LOCK.lock();
                while (pageQueue.isEmpty()) {
                    QUEUE_NOT_EMPTY.await();
                }
                String poll = pageQueue.poll();
                if (poll != null && !visited.containsKey(poll)) {
                    executor.submit(new Crawler(pageCount, visited, poll, pageQueue));
                }
                QUEUE_LOCK.unlock();
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Main thread interrupted!");
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("\nPages crawled: " + visited.size());
        System.out.println("Execution time: " + totalTime + "ms");

        double pagesPerSecond = (double) visited.size() / (totalTime / 1000.0);
        System.out.println("Throughput: " + pagesPerSecond + " pages/sec");
    }
}
```

### Key Components

* **ConcurrentMap** **visited** : Our trusty guide, marking the URLs we've visited.
* **Queue** **pageQueue** : The frontier, storing URLs awaiting exploration.
* **AtomicInteger** **count** : The diligent counter, keeping track of the number of pages visited.
* **Crawler Class** : The main protagonist embodies crawling logic.
* **run()** **Method** : The heart of the operation, orchestrating the fetching and parsing of web pages.

### **The Core Logic**

When the program starts, it initializes a shared visited map, a pageQueue, and an AtomicInteger count.

It then creates an executor service---either classical or virtual threads, based on the scenario---and enters a while loop.

Inside this loop, it picks URLs from the pageQueue and submits new Crawler tasks for execution.

Each Crawler task fetches the web page, parses the HTML to find links, and adds those new URLs to the pageQueue.  

## Running the Code: Execution Command with Dependencies

Before running your web crawler, it's important to include its dependency: the jsoup library, specifically version 1.16.1, which is used for HTML parsing.

### Command-Line Execution

1. **Compile the Java File** : Navigate to the directory containing your `Crawler.java` file. To compile the code, include the [jsoup library](https://jsoup.org/) in the classpath. If the jsoup JAR file is in the same directory, you can run:` javac -cp '.:jsoup-1.16.1.jar' Crawler.java`
2. **Run the Compiled Class** : When running the Java program, don't forget to include the JSoup library in the classpath again:` java -cp '.:jsoup-1.16.1.jar' Crawler `Make sure you've replaced `jsoup-1.16.1.jar` with the correct version if you downloaded a different one.

By following these steps, your Java web crawler will be up and running, fully equipped with the jsoup library for HTML parsing.  

If you prefer to manage dependencies using a build tool, both Maven and Gradle offer straightforward ways to include the JSoup library. Below are the steps for each:

### Using Maven

* **Add Dependency** : In your `pom.xml`, include the following dependency for JSoup version 1.16.1:` `

```xml
<dependencies>
     <dependency>
         <groupId>org.jsoup</groupId>
         <artifactId>jsoup</artifactId>
         <version>1.16.1</version>
     </dependency>
 </dependencies>
```

* **Compile and Run** : Navigate to your project directory and execute:` mvn compile exec:java -Dexec.mainClass="``Crawler``"` .

### Using Gradle

* **Add Dependency** : In your file`build.gradle`, add the following dependency:` dependencies { implementation 'org.jsoup:jsoup:1.16.1' }`

<!-- -->

* **Compile and Run** : Navigate to your project directory and run:` gradle run`

Either of these build tools will automatically download the JSoup library and include it in your project's classpath, making the compilation and execution process much smoother.

## **A Comparative Analysis**

### **Metrics Observed**

With **Virtual Threads** :

* Pages crawled: 785
* Execution time: 29,323 ms
* Throughput: 26.77 pages/sec

With **Classical Threads** :

* Pages crawled: 101
* Execution time: 20,910 ms
* Throughput: 4.83 pages/sec

### **The Revelation: Virtual Threads Shine**

1. **Throughput** : Virtual threads deliver a throughput of 26.77 pages/sec, significantly higher than the 4.83 pages/sec achieved by classical threads. It's akin to a sports car leaving a bicycle in the dust.
2. **Pages Crawled** : Virtual threads manage to crawl a whopping 785 pages, compared to the modest 101 by classical threads. The difference is too vast to ignore.
3. **Resource Efficiency** : Both virtual and classical threads in this example are backed by the same number of underlying physical threads, thanks to Java's ForkJoinPool. This makes the comparison even more compelling, showing how much more you can achieve with the same resources when using virtual threads.

## **Drawing Conclusions**

The story told by this code and its performance metrics is one of stark contrast between classical and virtual threads. Virtual threads not only outperform classical threads in terms of throughput but also do so without requiring more underlying resources. It's as if they're performing a high-wire act while juggling, making the most of what they have.

**In essence, the choice of threading model isn't merely a technical decision; it's a strategic one that could redefine the efficiency and scalability of your applications. As our web crawler has demonstrated, opting for virtual threads can be akin to unlocking a hidden level of performance, propelling your Java applications into a new realm of efficiency.**  

***NOTE: Keep in mind that the code I've shared here has an issue known as pinning. I'll go into more detail about this in my next article.***
