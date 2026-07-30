---
title: "Diagnose and Mitigate Pinning in Java's Virtual Thread Execution"
slug: "how-to-diagnose-and-mitigate-pinning-in-javas-virtual-thread-execution"
date: "2023-10-10T09:08:10+00:00"
lastmod: "2023-10-10T09:08:51+00:00"
description: "In the context of virtual threads, pinning refers to the condition where a virtual thread is \"stuck\" to its carrier thread (the platform thread on which it runs)."
authors:
  - "bazlur-rahman"
image: "https://foojay.io/wp-content/uploads/2022/02/shutterstock_1111260050-350x233-1.jpg"
categories:
  - "Java"
tags:
related_posts:
  - "web-crawling-in-java-a-tale-of-classical-threads-and-virtual-threads"
  - "7-reasons-why-after-26-years-java-still-makes-sense"
  - "busting-myths-building-futures-a-conversation-with-cay-horstmann-on-java-and-machine-learning"
  - "virtual-thread-pinning-field-guide"
enlighterjs: true
frozen: false
---

**In [our last article](https://foojay.io/today/web-crawling-in-java-a-tale-of-classical-threads-and-virtual-threads/), we highlighted the impressive performance gains achieved through the use of virtual threads. However, upon diving deeper into the code, we discovered an issue caused by the jsoup library: a phenomenon known as pinning. But before we delve into solutions, let's take a moment to understand what pinning actually is.**

What is Pinning? {#h2-0-what-is-pinning}
----------------------------------------

In the context of virtual threads, pinning refers to the condition where a virtual thread is "stuck" to its carrier thread (the platform thread on which it runs).

When a virtual thread is pinned, it cannot be unmounted from its carrier, effectively monopolizing that carrier thread for the duration of the pinning.

### **When Does Pinning Occur?** {#h3-1-when-does-pinning-occur}

Pinning occurs in two main scenarios:

1. **Synchronized Blocks or Methods**: When a virtual thread enters a synchronized block or method, it becomes pinned to its carrier thread. This means that during the execution of that block or method, the carrier thread cannot be reused for other tasks.
2. **Native Methods or Foreign Functions**: When a virtual thread executes a native method or a foreign function, it also becomes pinned.

### **Why is Pinning a Limitation?** {#h3-2-why-is-pinning-a-limitation}

The essence of virtual threads is their ability to be unmounted from carrier threads when they perform blocking operations, thereby freeing up the carrier threads for other tasks. Pinning negates this advantage in the following ways:

1. **Reduced Throughput**: Because a pinned virtual thread occupies its carrier thread, other virtual threads have to wait for free carrier threads, reducing the system's overall throughput.
2. **Resource Inefficiency**: Carrier threads are a finite resource tied to system capabilities. Having them blocked due to pinned virtual threads is an inefficient use of these resources.
3. **Scalability Concerns**: If a significant portion of your virtual threads becomes pinned due to frequent use of synchronized blocks or native methods, you might run into scalability issues.

### **Mitigating Pinning** {#h3-3-mitigating-pinning}

To alleviate the effects of pinning, consider the following strategies:

1. **Use ReentrantLocks** : Instead of synchronized blocks or methods, use ReentrantLock from `java.util.concurrent.locks` as it allows the virtual thread to be unmounted when blocked.
2. **Code Review** : Regularly review your code to identify and minimize the use of `synchronized` methods or blocks and native methods in the context of virtual threads.

Monitoring Pinning {#h2-4-monitoring-pinning}
---------------------------------------------

So, you may be wondering, how do you diagnose this pinning issue in your own code? One way to get to the bottom of this problem is by utilizing specific JVM flags.

To identify pinning, you can use the JVM flag `-Djdk.tracePinnedThreads=full` when executing your program.  

Let's go ahead and run the previous program with this flag enabled.

Upon doing so, you'll observe certain outputs that shed light on the issue at hand.  

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group=""> Task :Crawler.main()
Thread[#421,ForkJoinPool-1-worker-51,5,CarrierThreads]
    java.base/java.lang.VirtualThread$VThreadContinuation.onPinned(VirtualThread.java:185)
    java.base/jdk.internal.vm.Continuation.onPinned0(Continuation.java:393)
    java.base/java.lang.VirtualThread.parkNanos(VirtualThread.java:631)
    java.base/java.lang.System$2.parkVirtualThread(System.java:2648)
    java.base/jdk.internal.misc.VirtualThreads.park(VirtualThreads.java:67)
    java.base/java.util.concurrent.locks.LockSupport.parkNanos(LockSupport.java:408)
    java.base/sun.nio.ch.Poller.pollIndirect(Poller.java:137)
    java.base/sun.nio.ch.Poller.poll(Poller.java:102)
    java.base/sun.nio.ch.Poller.poll(Poller.java:87)
    java.base/sun.nio.ch.NioSocketImpl.park(NioSocketImpl.java:175)
    java.base/sun.nio.ch.NioSocketImpl.timedRead(NioSocketImpl.java:280)
    java.base/sun.nio.ch.NioSocketImpl.implRead(NioSocketImpl.java:304)
    java.base/sun.nio.ch.NioSocketImpl.read(NioSocketImpl.java:346)
    java.base/sun.nio.ch.NioSocketImpl$1.read(NioSocketImpl.java:796)
    java.base/java.net.Socket$SocketInputStream.read(Socket.java:1099)
    java.base/java.net.Socket$SocketInputStream.read(Socket.java:1093)
    java.base/sun.security.ssl.SSLSocketInputRecord.deplete(SSLSocketInputRecord.java:509)
    java.base/sun.security.ssl.SSLSocketImpl$AppInputStream.readLockedDeplete(SSLSocketImpl.java:1216)
    java.base/sun.security.ssl.SSLSocketImpl$AppInputStream.deplete(SSLSocketImpl.java:1191)
    java.base/sun.security.ssl.SSLSocketImpl.bruteForceCloseInput(SSLSocketImpl.java:808)
    java.base/sun.security.ssl.SSLSocketImpl.duplexCloseOutput(SSLSocketImpl.java:664)
    java.base/sun.security.ssl.SSLSocketImpl.close(SSLSocketImpl.java:584)
    java.base/sun.net.www.http.HttpClient.closeServer(HttpClient.java:1139)
    java.base/sun.net.www.protocol.https.HttpsClient.closeServer(HttpsClient.java:442)
    java.base/sun.net.www.http.KeepAliveCache.put(KeepAliveCache.java:196)
    java.base/sun.net.www.protocol.https.HttpsClient.putInKeepAliveCache(HttpsClient.java:679)
    java.base/sun.net.www.http.HttpClient.finished(HttpClient.java:450)
    java.base/sun.net.www.http.KeepAliveStream.close(KeepAliveStream.java:100)
    java.base/sun.net.www.MeteredStream.justRead(MeteredStream.java:85)
    java.base/sun.net.www.MeteredStream.read(MeteredStream.java:132)
    java.base/java.io.FilterInputStream.read(FilterInputStream.java:119)
</pre>

If you scrutinize the stack trace, one line will likely grab your attention:

<br />

    java.base/java.lang.VirtualThread$VThreadContinuation.onPinned(VirtualThread.java:185)

<br />

This line indicates that our virtual thread is, in fact, getting pinned to carrier threads. This isn't ideal for our application's performance. Upon closer inspection, you'll find that this pinning issue arises due to the use of the jsoup library in our code. The culprit could be a \`synchronized\` block or perhaps some sort of native function call within the library.

To remedy this, consider refactoring your application to work without the jsoup library. By avoiding the use of \`synchronized\` blocks or native code, we're likely to see a significant performance improvement.

<pre class="EnlighterJSRAW" data-enlighter-language="java" data-enlighter-theme="" data-enlighter-highlight="" data-enlighter-linenumbers="" data-enlighter-lineoffset="" data-enlighter-title="" data-enlighter-group="">package ca.bazlur.virtualthreads;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler2 implements Runnable {
    private static final Pattern LINK_PATTERN = Pattern.compile("href\\s*=\\s*\"([^\"#]*?)\"", Pattern.CASE_INSENSITIVE);
    private static final ReentrantLock QUEUE_LOCK = new ReentrantLock();
    private static final Condition QUEUE_NOT_EMPTY = QUEUE_LOCK.newCondition();
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().build();
    private static final int MAX_PAGES_TO_SEARCH = 100;
    private final AtomicInteger pageCount;
    private final ConcurrentMap&lt;String, Boolean&gt; visitedPages;
    private final String url;
    private final Queue&lt;String&gt; pageQueue;

    public Crawler2(AtomicInteger pageCount, ConcurrentMap&lt;String, Boolean&gt; visitedPages, String url, Queue&lt;String&gt; pageQueue) {
        this.pageCount = pageCount;
        this.visitedPages = visitedPages;
        this.url = url;
        this.pageQueue = pageQueue;
    }

    @Override
    public void run() {
        if (!visitedPages.containsKey(url) &amp;&amp; pageCount.get() &lt; MAX_PAGES_TO_SEARCH) {
            try {
                List&lt;String&gt; links = extractLinks(url);
                List&lt;String&gt; newUrls = links.stream()
                        .filter(nextUrl -&gt; nextUrl.startsWith("http") &amp;&amp; !visitedPages.containsKey(nextUrl))
                        .toList();

                visitedPages.put(url, true);
                pageCount.incrementAndGet();

                if (!newUrls.isEmpty()) {
                    pageQueue.addAll(newUrls);
                    signalQueueNotEmpty();
                }

            } catch (IOException | InterruptedException e) {
                System.err.printf("Error occurred while accessing URL '%s', the error: %s\n", url, e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static List&lt;String&gt; extractLinks(String url) throws IOException, InterruptedException {
        String pageContent = getPageContent(url);
        var links = new HashSet&lt;String&gt;();
        Matcher matcher = LINK_PATTERN.matcher(pageContent);

        while (matcher.find()) {
            links.add(matcher.group(1));
        }

        return new ArrayList&lt;&gt;(links);
    }

    private static String getPageContent(String url) throws IOException, InterruptedException {
        System.out.println("Visiting: " + url);

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        var response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    private static void signalQueueNotEmpty() {
        QUEUE_LOCK.lock();
        try {
            QUEUE_NOT_EMPTY.signalAll();
        } finally {
            QUEUE_LOCK.unlock();
        }
    }

    //Classical Threads
    //Pages crawled: 731
    //Execution time: 9766ms
    //Throughput: 74.85152570141307 pages/sec

    //Virtual threads
    //Pages crawled: 710
    //Execution time: 3119ms
    //Throughput: 227.63706316126962 pages/sec

    public static void main(String[] args) {
        final ConcurrentMap&lt;String, Boolean&gt; visitedPages = new ConcurrentHashMap&lt;&gt;();
        final Queue&lt;String&gt; pageQueue = new LinkedBlockingDeque&lt;&gt;();
        pageQueue.add("https://en.wikipedia.org/wiki/Main_Page");

        long startTime = System.currentTimeMillis();
        AtomicInteger pageCount = new AtomicInteger();

        //try (var executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            while (pageCount.get() &lt;= MAX_PAGES_TO_SEARCH) {
                QUEUE_LOCK.lock();
                try {
                    while (pageQueue.isEmpty()) {
                        QUEUE_NOT_EMPTY.await();
                    }
                    String polledUrl = pageQueue.poll();
                    if (polledUrl != null &amp;&amp; !visitedPages.containsKey(polledUrl)) {
                        executor.submit(new Crawler2(pageCount, visitedPages, polledUrl, pageQueue));
                    }
                } finally {
                    QUEUE_LOCK.unlock();
                }
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Main thread interrupted!");
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println("\nPages crawled: " + visitedPages.size());
        System.out.println("Execution time: " + totalTime + "ms");

        double pagesPerSecond = (double) visitedPages.size() / (totalTime / 1000.0);
        System.out.println("Throughput: " + pagesPerSecond + " pages/sec");
    }
}
</pre>

To fix the pinning issue, we switched from using the jsoup library to using HttpClient. HttpClient is built into Java and is available from version 11 onwards. The best part? It doesn't have the pinning problem we saw with jsoup.

By making this change, we avoid the issues that were slowing down our program, likely making it run faster.  

But be careful; this new approach isn't perfect. You might run into an error that says:

`Caused by: java.io.IOException: too many concurrent streams`

I'll talk more about what this error means in my next article.
