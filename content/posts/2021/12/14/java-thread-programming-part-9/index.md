---
title: "Java Thread Programming (Part 9)"
slug: "java-thread-programming-part-9"
date: "2021-12-14T11:14:33+00:00"
lastmod: "2021-12-14T11:23:04+00:00"
description: "Let's continue the discussion and share a few more thread-safe classes that we can use in our day-to-day coding!"
authors:
  - "bazlur-rahman"
image: "Favicon-3-2.png"
categories:
  - "Tutorials"
tags:
related_posts:
  - "java-thread-programming-part-8"
  - "java-thread-programming-part-7"
  - "java-thread-programming-part-6"
  - "relearning-java-thread-primitives"
enlighterjs: true
frozen: false
---

**In [our last article](https://foojay.io/today/java-thread-programming-part-8/), we discussed thread-safety in naive terms and shared a couple of ways to ensure thread safety.** **We also introduced a package containing thread-safe classes, which we can confidently use without worrying much, e.g., AtomicInteger. This is because they are designed in a thread-safe way.**

Today in this article, we will continue the discussion and share a few more thread-safe classes that we can use in our day-to-day coding.

### **Synchronized Collections** {#h3-0-synchronized-collections}

One of the most important data structures that Java provides are collections. We depend on them heavily in our day-to-day coding. Not all collection implementations available in java.util package are thread-safe, but a few of them are:

<pre class="EnlighterJSRAW" data-enlighter-language="java">java.util.Vector
java.util.Stack
java.util.HashTable</pre>

Every method in these classes has synchronized keywords associated with them. Although we can use them in a multi-threaded environment, these classes are no longer recommended to use, as we have better alternatives. However, we will discuss them shortly.

Apart from these classes, we can transform any collections available in java.util package, using the following factory methods available in java.util.Collections:

<pre class="EnlighterJSRAW" data-enlighter-language="java">static &lt;T&gt; Collection&lt;T&gt; synchronizedCollection(Collection&lt;T&gt; c); 
static &lt;T&gt; Set&lt;T&gt; synchronizedSet(Set&lt;T&gt; s);
static &lt;T&gt; List&lt;T&gt; synchronizedList(List&lt;T&gt; list); 
static &lt;K,V&gt; Map&lt;K,V&gt; synchronizedMap(Map&lt;K,V&gt; m); 
static &lt;T&gt; SortedSet&lt;T&gt; synchronizedSortedSet(SortedSet&lt;T&gt; s);
static &lt;K,V&gt; SortedMap&lt;K, V&gt; synchronizedSortedMap(SortedMap&lt;K,V&gt; m);</pre>

These methods return synchronized collections. Example:

<pre class="EnlighterJSRAW" data-enlighter-language="java">var ints = new ArrayList&lt;Integer&gt;();
var synchronizedList = Collections.synchronizedList(ints);</pre>

### **Client-Side Locking** {#h3-1-client-side-locking}

The synchronizedList instance is thread-safe. However, there is a caveat. Even though these classes are thread-safe, a compound operation on these thread-safe collections is not thread-safe. This could seem a bit puzzling. Not to worry, bear with me.

An example of compound operation could be - while iterating over a collection and then removing elements, perhaps with a condition.

Look at the following code:

<pre class="EnlighterJSRAW" data-enlighter-language="java">package ca.bazlur.playground;

import java.util.Vector;

public class ListHelper {
  public static &lt;E&gt; E getLast(Vector&lt;E&gt; list) {
    int lastIndex = list.size() - 1;
    return list.get(lastIndex);
  }

  public static &lt;E&gt; void removeLast(Vector&lt;E&gt; list) {
    int lastIndex = list.size() - 1;
    list.remove(lastIndex);
  }
}</pre>

In the above class, we have two methods, one gets the last item, and the other removes the last one. So now the question is, what if we call these two methods from two different threads?

In the first method (getLast()) has two statements in it. The first statement finds the size of the vector and then subtracts one from it to find the last index of the last elements. The second statement uses this index to find the last element. What if while executing the first segment in one thread, before returning the element, another thread removes the element? We will certainly get an ArrayIndexOutOfBoundException. The reason is, while the second statement of the first method is trying to access the element, it's not there anymore; it's already removed.

Now, if we synchronize these two methods, would that help?

<pre class="EnlighterJSRAW" data-enlighter-language="java">import java.util.Vector;

public class ListHelper {
  public synchronized static &lt;E&gt; E getLast(Vector&lt;E&gt; list) {
    int lastIndex = list.size() - 1;
    return list.get(lastIndex);
  }

  public synchronized static &lt;E&gt; void removeLast(Vector&lt;E&gt; list) {
    int lastIndex = list.size() - 1;
    list.remove(lastIndex);
  }
}</pre>

Even though it sounds like the above code will solve the issue, but it doesn't. The reason is, when we use synchronized keywords on a static method, it uses the class (ListHelper.class) object as its lock. On the other hand, the vector class is a synchronized class; it has its own lock. That means we are dealing with two different locks here. If these methods are called from two different threads ( A and B), one of them will acquire the lock of the ListHelper class at a point in time.

However, since the Vector class itself has its own lock, other threads ( aside from A and B) can acquire that lock and execute any compound operations. The reason is, the lock of ListHelper isn't preventing doing so. We can only fix this problem if we can use one lock, and when a thread acquires that lock, no other operation can be done from any other threads on this Vector class.

<pre class="EnlighterJSRAW" data-enlighter-language="java">import java.util.Vector;

public class ListHelper {
  public static &lt;E&gt; E getLast(Vector&lt;E&gt; list) {
    synchronized (list) {
      int lastIndex = list.size() - 1;
      return list.get(lastIndex);
    }
  }

  public static &lt;E&gt; void removeLast(Vector&lt;E&gt; list) {
    synchronized (list) {
      int lastIndex = list.size() - 1;
      list.remove(lastIndex);
    }
  }
}</pre>

The above class exactly does that. It synchronizes over the list object itself. This sort of synchronization is called client-side locking or external locking.

Although I have used the Vector class in the above example, we no longer use Vector in our day-to-day coding. It is considered a legacy collection. In that case, we may be tempted to use our regular collection classes and the factory method to synchronize them, which we introduced earlier. For example:

<pre class="EnlighterJSRAW" data-enlighter-language="java">package ca.bazlur.playground;

import java.util.ArrayList;
import java.util.Collections;

public class SynchronizedCollectionDemo {
  public static void main(String[] args) {
    var numbers = new ArrayList&lt;Integer&gt;();
    numbers.add(1);
    numbers.add(2);
    numbers.add(3);
    numbers.add(4);
    var synchronizedNumbers = Collections.synchronizedList(numbers);

    synchronized (synchronizedNumbers){
      for (int i = 0; i &lt; synchronizedNumbers.size(); i++) {
        Integer number = synchronizedNumbers.get(i);
        processIt(number);
      }
    }

  }

  private static void processIt(Integer number) {
    //TODO we process the number here
  }
}</pre>

There is another standard way to iterate over a collection.

<pre class="EnlighterJSRAW" data-enlighter-language="java">for (Integer number : synchronizedNumbers) {
  processIt(number);
}</pre>

However, this iteration doesn't avert the need for client-side locking if other threads can modify the collection. This is because the iteration returned by synchronized collections are not designed to deal with concurrent modification; rather, a fail-first approach was taken. If they detect that collection was changed after the iteration began, it throws the unchecked ConcurrentModificationException.

### **Concurrent Collections** {#h3-2-concurrent-collections}

Although client-side locking solves our issue discussed above, it has some downside as well. If a collection is extensive, it may take a while to iterate. While it's being iterated, no other operation can be performed, which would certainly hurt the overall performance of the applications. To deal with this issue, in java 5.0, a few classes are added to the concurrent packages. These are:

<pre class="EnlighterJSRAW" data-enlighter-language="java"><code class="language-java">java.util.concurrent.ConcurrentLinkedQueue
java.util.concurrent.ConcurrentLinkedDeque
java.util.concurrent.ConcurrentSkipListSet
java.util.concurrent.ConcurrentHashMap
java.util.concurrent.ConcurrentSkipListMap
java.util.concurrent.ConcurrentNavigableMap
java.util.concurrent.CopyOnWriteArraySet
java.util.concurrent.CopyOnWriteArrayList
java.util.concurrent.ArrayBlockingQueue</code></pre>

We don't need to use client-side locking in the above classes. These are thread-safe, optimized, and highly performant classes. Ideally, in our modern code, we will use these classes in our day-to-day coding rather than the technique discussed in this article.

That's all for today!
